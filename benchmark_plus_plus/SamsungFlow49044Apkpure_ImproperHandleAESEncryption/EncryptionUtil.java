package com.samsung.android.galaxycontinuity.auth.util;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import com.samsung.android.galaxycontinuity.SamsungFlowApplication;
import com.samsung.android.galaxycontinuity.data.FlowDevice;
import com.samsung.android.galaxycontinuity.manager.FlowDeviceDBHelper;
import com.samsung.android.galaxycontinuity.util.FlowLog;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes2.dex */
public class EncryptionUtil implements IUpgrageProtocol {
    public static final String AUTHKEYPREFIX = "authkey_";
    public static final String DEVICEKEYPREFIX = "devicekey_";
    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    public static final String LEGACYAUTHKEY = "authkey";
    public static final String LEGACYDEVICEKEY = "devicekey";
    private static final String mAlias = "SamsungFlowEnrollKey";
    private static EncryptionUtil sInstance;
    private HashMap<String, byte[]> mDeviceKeyMap = new HashMap<>();
    private HashMap<String, byte[]> mAuthKeyMap = new HashMap<>();
    private byte[] mTempDeviceKey = null;
    private byte[] mTempAuthKey = null;
    private KeyStore keyStore = null;
    private boolean initialized = false;

    public static synchronized EncryptionUtil getInstance() {
        EncryptionUtil encryptionUtil;
        synchronized (EncryptionUtil.class) {
            if (sInstance == null) {
                sInstance = new EncryptionUtil();
            }
            try {
                sInstance.initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            encryptionUtil = sInstance;
        }
        return encryptionUtil;
    }

    @Override // com.samsung.android.galaxycontinuity.auth.util.IUpgrageProtocol
    public void onUpgrade(int oldVersion, int newVersion) {
        ArrayList<FlowDevice> allFlowDevices;
        if (newVersion == 5) {
            if ((oldVersion == 1 || oldVersion == 2 || oldVersion == 3 || oldVersion == 4) && (allFlowDevices = FlowDeviceDBHelper.getInstance().getAllFlowDevices()) != null && allFlowDevices.size() > 0) {
                FlowDevice flowDevice = allFlowDevices.get(0);
                renameKeyFile(LEGACYDEVICEKEY, DEVICEKEYPREFIX + flowDevice.MACAddress);
                renameKeyFile(LEGACYAUTHKEY, AUTHKEYPREFIX + flowDevice.MACAddress);
            }
        }
    }

    private void renameKeyFile(String originalName, String NewName) {
        initialize();
        byte[] loadFile = loadFile(originalName);
        if (loadFile != null) {
            try {
                saveTempKeyToFile(decryptData(loadFile), NewName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getDeviceKey(String MACAddress) {
        if (this.mDeviceKeyMap.containsKey(MACAddress)) {
            return this.mDeviceKeyMap.get(MACAddress);
        }
        initialize();
        byte[] bArr = new byte[32];
        byte[] loadFile = loadFile(DEVICEKEYPREFIX + MACAddress);
        if (loadFile == null) {
            return bArr;
        }
        try {
            bArr = decryptData(loadFile);
            this.mDeviceKeyMap.put(MACAddress, bArr);
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr;
        }
    }

    public byte[] getAuthKey(String keyName) {
        if (this.mAuthKeyMap.containsKey(keyName)) {
            return this.mAuthKeyMap.get(keyName);
        }
        initialize();
        byte[] bArr = new byte[32];
        byte[] loadFile = loadFile(AUTHKEYPREFIX + keyName);
        if (loadFile == null) {
            return bArr;
        }
        try {
            bArr = decryptData(loadFile);
            this.mAuthKeyMap.put(keyName, bArr);
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr;
        }
    }

    public byte[] getTempAuthKey() {
        return this.mTempAuthKey;
    }

    public void createKeys(byte[] secretKey) throws IOException {
        FlowLog.d("createKeys");
        try {
            createCert();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(secretKey);
            byte[] digest = messageDigest.digest();
            if (this.mTempDeviceKey == null) {
                this.mTempDeviceKey = new byte[32];
            }
            if (this.mTempAuthKey == null) {
                this.mTempAuthKey = new byte[32];
            }
            System.arraycopy(digest, 0, this.mTempDeviceKey, 0, 32);
            System.arraycopy(digest, 32, this.mTempAuthKey, 0, 32);
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
    }

    public void replaceKeyWithTempKey(String keyName) throws IOException {
        if (this.mTempDeviceKey == null || this.mTempAuthKey == null) {
            return;
        }
        String str = DEVICEKEYPREFIX + keyName;
        String str2 = AUTHKEYPREFIX + keyName;
        saveTempKeyToFile(this.mTempDeviceKey, str);
        if (this.mDeviceKeyMap.containsKey(keyName)) {
            this.mDeviceKeyMap.remove(keyName);
        }
        saveTempKeyToFile(this.mTempAuthKey, str2);
        if (this.mAuthKeyMap.containsKey(keyName)) {
            this.mAuthKeyMap.remove(keyName);
        }
        this.mTempDeviceKey = null;
        this.mTempAuthKey = null;
    }

    private void saveTempKeyToFile(byte[] mAuthKey, String authkey) throws IOException {
        try {
            if (saveFile(encryptData(mAuthKey), authkey)) {
                FlowLog.d("encryptedAuthKey save success");
            } else {
                FlowLog.d("encryptedAuthKey save failed");
            }
        } catch (Exception e) {
            FlowLog.e(e);
        }
    }

    public void replaceKeyName(String oldKeyName, String newKeyName) {
        String str = AUTHKEYPREFIX + oldKeyName;
        String str2 = DEVICEKEYPREFIX + oldKeyName;
        if (SamsungFlowApplication.get().getFileStreamPath(str).exists()) {
            byte[] authKey = getAuthKey(oldKeyName);
            byte[] deviceKey = getDeviceKey(oldKeyName);
            SamsungFlowApplication.get().deleteFile(str);
            SamsungFlowApplication.get().deleteFile(str2);
            try {
                saveTempKeyToFile(authKey, AUTHKEYPREFIX + newKeyName);
            } catch (IOException e) {
                FlowLog.e(e);
            }
            try {
                saveTempKeyToFile(deviceKey, DEVICEKEYPREFIX + newKeyName);
            } catch (IOException e2) {
                FlowLog.e(e2);
            }
        }
    }

    private synchronized void initialize() {
        if (!this.initialized) {
            try {
                KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                this.keyStore = keyStore;
                keyStore.load(null);
                this.initialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createCert() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        initialize();
        try {
            if (!this.keyStore.containsAlias(mAlias)) {
                FlowLog.d("key not exist");
                Calendar.getInstance().add(1, 1);
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(mAlias, 3).setDigests("SHA-256", "SHA-512").setEncryptionPaddings("OAEPPadding").build());
                keyPairGenerator.generateKeyPair();
            } else {
                FlowLog.d("key exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encryptData(byte[] inputBytes) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CertificateException {
        Cipher cipher;
        try {
            RSAPublicKey rSAPublicKey = (RSAPublicKey) ((KeyStore.PrivateKeyEntry) this.keyStore.getEntry(mAlias, null)).getCertificate().getPublicKey();
            if (Build.VERSION.SDK_INT > 24) {
                cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
                cipher.init(1, rSAPublicKey, new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT));
            } else if (Build.VERSION.SDK_INT >= 23) {
                cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                cipher.init(1, rSAPublicKey);
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(1, rSAPublicKey);
            }
            return cipher.doFinal(inputBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptData(byte[] encryptedBytes) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, InvalidKeyException, SignatureException {
        Cipher cipher;
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) this.keyStore.getEntry(mAlias, null);
            if (Build.VERSION.SDK_INT > 24) {
                cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
                cipher.init(2, privateKeyEntry.getPrivateKey(), new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT));
            } else if (Build.VERSION.SDK_INT >= 23) {
                cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                cipher.init(2, privateKeyEntry.getPrivateKey());
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(2, privateKeyEntry.getPrivateKey());
            }
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean saveFile(byte[] input, String fileName) {
        FileOutputStream fileOutputStream = null;
        try {
            try {
                SamsungFlowApplication.get().deleteFile(fileName);
                fileOutputStream = SamsungFlowApplication.get().openFileOutput(fileName, 0);
                if (fileOutputStream != null) {
                    fileOutputStream.write(input);
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        FlowLog.e(e);
                    }
                }
                return true;
            } catch (Throwable th) {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e2) {
                        FlowLog.e(e2);
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
            FlowLog.e(e3);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e4) {
                    FlowLog.e(e4);
                }
            }
            return false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:33:0x003c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v4, types: [java.io.FileInputStream] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private byte[] loadFile(java.lang.String r6) {
        /*
            r5 = this;
            r0 = 0
            com.samsung.android.galaxycontinuity.SamsungFlowApplication r1 = com.samsung.android.galaxycontinuity.SamsungFlowApplication.get()     // Catch: java.lang.Throwable -> L24 java.io.IOException -> L29
            java.io.FileInputStream r6 = r1.openFileInput(r6)     // Catch: java.lang.Throwable -> L24 java.io.IOException -> L29
            int r1 = r6.available()     // Catch: java.io.IOException -> L22 java.lang.Throwable -> L39
            byte[] r1 = new byte[r1]     // Catch: java.io.IOException -> L22 java.lang.Throwable -> L39
        Lf:
            int r2 = r6.read(r1)     // Catch: java.io.IOException -> L22 java.lang.Throwable -> L39
            r3 = -1
            if (r2 == r3) goto L17
            goto Lf
        L17:
            if (r6 == 0) goto L21
            r6.close()     // Catch: java.io.IOException -> L1d
            goto L21
        L1d:
            r6 = move-exception
            r6.printStackTrace()
        L21:
            return r1
        L22:
            r1 = move-exception
            goto L2b
        L24:
            r6 = move-exception
            r4 = r0
            r0 = r6
            r6 = r4
            goto L3a
        L29:
            r1 = move-exception
            r6 = r0
        L2b:
            r1.printStackTrace()     // Catch: java.lang.Throwable -> L39
            if (r6 == 0) goto L38
            r6.close()     // Catch: java.io.IOException -> L34
            goto L38
        L34:
            r6 = move-exception
            r6.printStackTrace()
        L38:
            return r0
        L39:
            r0 = move-exception
        L3a:
            if (r6 == 0) goto L44
            r6.close()     // Catch: java.io.IOException -> L40
            goto L44
        L40:
            r6 = move-exception
            r6.printStackTrace()
        L44:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.galaxycontinuity.auth.util.EncryptionUtil.loadFile(java.lang.String):byte[]");
    }

    public static byte[] generateNonce() {
        byte[] bArr = new byte[32];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    public static byte[] getSHA256Hash(String hashString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            return messageDigest.digest(hashString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] genHMACSHA256(byte[] keyArray, byte[] nonceArray) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyArray, "HmacSHA256"));
            return mac.doFinal(nonceArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getAESCBCEncryptedBytes(byte[] keyArray, byte[] original) {
        byte[] bArr = new byte[16];
        System.arraycopy(keyArray, 0, bArr, 0, 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        byte[] bArr2 = new byte[16];
        System.arraycopy(keyArray, 16, bArr2, 0, 16);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(original);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            return null;
        } catch (BadPaddingException e4) {
            e4.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e5) {
            e5.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e6) {
            e6.printStackTrace();
            return null;
        }
    }

    public static byte[] getAESCBCDecryptedBytes(byte[] keyArray, byte[] encryptedBytes) {
        byte[] bArr = new byte[16];
        System.arraycopy(keyArray, 0, bArr, 0, 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        byte[] bArr2 = new byte[16];
        System.arraycopy(keyArray, 16, bArr2, 0, 16);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(encryptedBytes);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            return null;
        } catch (BadPaddingException e4) {
            e4.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e5) {
            e5.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e6) {
            e6.printStackTrace();
            return null;
        }
    }
}
