package com.seafile.seadroid2.crypto;

import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.seafile.seadroid2.SeafException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jce.provider.BouncyCastleProvider;

/* loaded from: classes.dex */
public class Crypto {
    private static final String CHAR_SET = "UTF-8";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    private static int ITERATION_COUNT = 1000;
    private static int KEY_LENGTH = 32;
    private static int KEY_LENGTH_SHORT = 16;
    private static final String TAG = "Crypto";
    private static byte[] salt = {-38, -112, 69, -61, 6, -57, -52, 38};

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private Crypto() {
    }

    private static byte[] generateMagic(String str, String str2, int i) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, SeafException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (i != 1 && i != 2) {
            throw SeafException.unsupportedEncVersion;
        }
        return deriveKey(str + str2, i);
    }

    public static void verifyRepoPassword(String str, String str2, int i, String str3) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, SeafException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        byte[] bytes = toHex(generateMagic(str, str2, i)).getBytes(CHAR_SET);
        byte[] bytes2 = str3.getBytes(CHAR_SET);
        int length = bytes.length ^ bytes2.length;
        for (int i2 = 0; i2 < bytes.length && i2 < bytes2.length; i2++) {
            length |= bytes[i2] ^ bytes2[i2];
        }
        if (length != 0) {
            throw SeafException.invalidPassword;
        }
    }

    public static Pair<String, String> generateKey(String str, String str2, int i) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] deriveKey = deriveKey(str, i);
        String deriveKey2 = deriveKey(seafileDecrypt(fromHex(str2), new SecretKeySpec(deriveKey, "AES"), deriveIv(deriveKey)), i);
        return new Pair<>(deriveKey2, toHex(deriveIv(fromHex(deriveKey2))));
    }

    private static byte[] deriveKey(String str, int i) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        pKCS5S2ParametersGenerator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(str.toCharArray()), salt, ITERATION_COUNT);
        return ((KeyParameter) pKCS5S2ParametersGenerator.generateDerivedMacParameters((i == 2 ? KEY_LENGTH : KEY_LENGTH_SHORT) * 8)).getKey();
    }

    private static String deriveKey(byte[] bArr, int i) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            pKCS5S2ParametersGenerator.init(bArr, salt, ITERATION_COUNT);
            return toHex(((KeyParameter) pKCS5S2ParametersGenerator.generateDerivedMacParameters((i == 2 ? KEY_LENGTH : KEY_LENGTH_SHORT) * 8)).getKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(" Attempt to get length of null array");
        }
    }

    private static byte[] deriveIv(byte[] bArr) throws UnsupportedEncodingException {
        PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        pKCS5S2ParametersGenerator.init(bArr, salt, 10);
        return ((KeyParameter) pKCS5S2ParametersGenerator.generateDerivedMacParameters(KEY_LENGTH_SHORT * 8)).getKey();
    }

    private static byte[] seafileDecrypt(byte[] bArr, SecretKey secretKey, byte[] bArr2) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(2, secretKey, new IvParameterSpec(bArr2));
            return cipher.doFinal(bArr);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(TAG, "InvalidAlgorithmParameterException " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            Log.e(TAG, "InvalidKeyException " + e2.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            Log.e(TAG, "NoSuchAlgorithmException " + e3.getMessage());
            return null;
        } catch (BadPaddingException e4) {
            e4.printStackTrace();
            Log.e(TAG, "seafileDecrypt BadPaddingException " + e4.getMessage());
            return null;
        } catch (IllegalBlockSizeException e5) {
            Log.e(TAG, "IllegalBlockSizeException " + e5.getMessage());
            e5.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e6) {
            e6.printStackTrace();
            Log.e(TAG, "NoSuchPaddingException " + e6.getMessage());
            return null;
        }
    }

    private static byte[] seafileEncrypt(byte[] bArr, int i, SecretKey secretKey, byte[] bArr2) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(1, secretKey, new IvParameterSpec(bArr2));
            return cipher.doFinal(bArr, 0, i);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            Log.e(TAG, "InvalidAlgorithmParameterException " + e.getMessage());
            return null;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            Log.e(TAG, "InvalidKeyException " + e2.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            Log.e(TAG, "NoSuchAlgorithmException " + e3.getMessage());
            return null;
        } catch (BadPaddingException e4) {
            e4.printStackTrace();
            Log.e(TAG, "seafileEncrypt BadPaddingException " + e4.getMessage());
            return null;
        } catch (IllegalBlockSizeException e5) {
            e5.printStackTrace();
            Log.e(TAG, "IllegalBlockSizeException " + e5.getMessage());
            return null;
        } catch (NoSuchPaddingException e6) {
            e6.printStackTrace();
            Log.e(TAG, "NoSuchPaddingException " + e6.getMessage());
            return null;
        }
    }

    public static byte[] encrypt(byte[] bArr, String str, String str2) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return encrypt(bArr, bArr.length, str, str2);
    }

    public static byte[] encrypt(byte[] bArr, int i, String str, String str2) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return seafileEncrypt(bArr, i, new SecretKeySpec(fromHex(str), "AES"), fromHex(str2));
    }

    public static byte[] decrypt(byte[] bArr, String str, String str2) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return seafileDecrypt(bArr, new SecretKeySpec(fromHex(str), "AES"), fromHex(str2));
    }

    private static String toHex(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            sb.append("0123456789abcdef".charAt((bArr[i] >> 4) & 15));
            sb.append("0123456789abcdef".charAt(bArr[i] & 15));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String str) throws NoSuchAlgorithmException {
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
        }
        return bArr;
    }

    public static String toBase64(byte[] bArr) {
        return Base64.encodeToString(bArr, 2);
    }

    public static byte[] fromBase64(String str) {
        return Base64.decode(str, 2);
    }

    public static String sha1(byte[] bArr) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(bArr, 0, bArr.length);
        return toHex(messageDigest.digest());
    }
}
