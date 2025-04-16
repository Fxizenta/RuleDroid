package com.netqin.antivirus.util;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes.dex */
public class DataUtils {
    public static final String ENCRYPT_KEY = "0x8F*NQ18#KeYSecuRItY";
    static int CRYPTKEY = 110;
    static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};

    public static String decryptForXml(String key, String value) {
        byte[] base64Value = NqBase64.decodeBase64(value.getBytes());
        String decryptValue = RunRC4(new String(base64Value), key);
        return decryptValue;
    }

    public static String encryptForXml(String key, String value) {
        String rc4 = RunRC4(value, key);
        byte[] base64Value = NqBase64.encodeBase64(rc4.getBytes());
        String encryptValue = new String(base64Value);
        return encryptValue;
    }

    public static String RunRC4(String aInput, String aKey) {
        int[] iS = new int[256];
        byte[] iK = new byte[256];
        for (int i = 0; i < 256; i++) {
            iS[i] = i;
        }
        for (short i2 = 0; i2 < 256; i2 = (short) (i2 + 1)) {
            iK[i2] = (byte) aKey.charAt(i2 % aKey.length());
        }
        int j = 0;
        for (int i3 = 0; i3 < 255; i3++) {
            j = ((iS[i3] + j) + iK[i3]) % 256;
            int temp = iS[i3];
            iS[i3] = iS[j];
            iS[j] = temp;
        }
        int i4 = 0;
        int j2 = 0;
        char[] iInputChar = aInput.toCharArray();
        char[] iOutputChar = new char[iInputChar.length];
        for (short x = 0; x < iInputChar.length; x = (short) (x + 1)) {
            i4 = (i4 + 1) % 256;
            j2 = (iS[i4] + j2) % 256;
            int temp2 = iS[i4];
            iS[i4] = iS[j2];
            iS[j2] = temp2;
            int t = (iS[i4] + (iS[j2] % 256)) % 256;
            int iY = iS[t];
            char iCY = (char) iY;
            iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
        }
        return new String(iOutputChar);
    }

    public static byte[] decrypt(byte[] bytes) {
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (bytes[i] ^ CRYPTKEY);
        }
        return bytes;
    }

    public static byte[] ecrypt(byte[] bytes) {
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (bytes[i] ^ CRYPTKEY);
        }
        return bytes;
    }

    public static byte[] ecryptCompress(byte[] bytes) {
        return ecrypt(zlib.compressData(bytes));
    }

    public static byte[] decryptDecompress(byte[] bytes) {
        return zlib.decompressData(decrypt(bytes));
    }

    public static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 240) >>> 4]);
            sb.append(hexChar[b[i] & 15]);
        }
        return sb.toString();
    }

    /* loaded from: classes.dex */
    public static class Zip {
        public static void doDecompression(String apkPathFile, String DestPath, String filter) {
            try {
                FileInputStream fins = new FileInputStream(apkPathFile);
                ZipInputStream zins = new ZipInputStream(fins);
                byte[] ch = new byte[256];
                while (true) {
                    ZipEntry ze = zins.getNextEntry();
                    if (ze != null) {
                        if (ze.getName().equals(filter)) {
                            File zfile = new File(String.valueOf(DestPath) + ze.getName());
                            File fpath = new File(zfile.getParentFile().getPath());
                            if (ze.isDirectory()) {
                                if (!zfile.exists()) {
                                    zfile.mkdirs();
                                }
                                zins.closeEntry();
                            } else {
                                if (!fpath.exists()) {
                                    fpath.mkdirs();
                                }
                                FileOutputStream fouts = new FileOutputStream(zfile);
                                while (true) {
                                    int i = zins.read(ch);
                                    if (i == -1) {
                                        break;
                                    } else {
                                        fouts.write(ch, 0, i);
                                    }
                                }
                                zins.closeEntry();
                                fouts.close();
                            }
                        }
                    } else {
                        fins.close();
                        zins.close();
                        return;
                    }
                }
            } catch (Exception e) {
            }
        }

        public static void doDecompressionMatchRegexp(String apkPathFile, String DestPath, String regularExpression) {
            try {
                FileInputStream fins = new FileInputStream(apkPathFile);
                ZipInputStream zins = new ZipInputStream(fins);
                byte[] ch = new byte[256];
                while (true) {
                    ZipEntry ze = zins.getNextEntry();
                    if (ze != null) {
                        if (ze.getName().matches(regularExpression)) {
                            File zfile = new File(String.valueOf(DestPath) + "/" + ze.getName());
                            File fpath = new File(zfile.getParentFile().getPath());
                            if (ze.isDirectory()) {
                                if (!zfile.exists()) {
                                    zfile.mkdirs();
                                }
                                zins.closeEntry();
                            } else {
                                if (!fpath.exists()) {
                                    fpath.mkdirs();
                                }
                                FileOutputStream fouts = new FileOutputStream(zfile);
                                while (true) {
                                    int i = zins.read(ch);
                                    if (i == -1) {
                                        break;
                                    } else {
                                        fouts.write(ch, 0, i);
                                    }
                                }
                                zins.closeEntry();
                                fouts.close();
                            }
                        }
                    } else {
                        fins.close();
                        zins.close();
                        return;
                    }
                }
            } catch (Exception e) {
            }
        }

        public static byte[] extractFromZipFile(String apkPathFile, String DestPath, String filter) {
            try {
                FileInputStream fins = new FileInputStream(apkPathFile);
                ZipInputStream zins = new ZipInputStream(fins);
                byte[] ch = new byte[256];
                while (true) {
                    ZipEntry ze = zins.getNextEntry();
                    if (ze != null) {
                        if (ze.getName().equals(filter)) {
                            if (ze.isDirectory()) {
                                zins.closeEntry();
                            } else {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                while (true) {
                                    int i = zins.read(ch);
                                    if (i != -1) {
                                        bos.write(ch, 0, i);
                                    } else {
                                        zins.closeEntry();
                                        fins.close();
                                        zins.close();
                                        return bos.toByteArray();
                                    }
                                }
                            }
                        }
                    } else {
                        fins.close();
                        zins.close();
                        return null;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }

        public static boolean doDecompressionMatchExtension(String apkPathFile, String DestPath, String filter) {
            boolean res = false;
            try {
                FileInputStream fins = new FileInputStream(apkPathFile);
                ZipInputStream zins = new ZipInputStream(fins);
                byte[] ch = new byte[256];
                while (true) {
                    ZipEntry ze = zins.getNextEntry();
                    if (ze == null) {
                        break;
                    }
                    if (ze.getName().endsWith(filter)) {
                        File zfile = new File(DestPath);
                        File fpath = new File(zfile.getParentFile().getPath());
                        if (ze.isDirectory()) {
                            if (!zfile.exists()) {
                                zfile.mkdirs();
                            }
                            zins.closeEntry();
                        } else {
                            if (!fpath.exists()) {
                                fpath.mkdirs();
                            }
                            FileOutputStream fouts = new FileOutputStream(zfile);
                            while (true) {
                                int i = zins.read(ch);
                                if (i == -1) {
                                    break;
                                }
                                fouts.write(ch, 0, i);
                            }
                            zins.closeEntry();
                            fouts.close();
                        }
                        res = true;
                    }
                }
                fins.close();
                zins.close();
            } catch (Exception e) {
            }
            return res;
        }

        public static byte[] extractFromZipFileMatchExtension(String apkPathFile, String DestPath, String filter) {
            try {
                FileInputStream fins = new FileInputStream(apkPathFile);
                ZipInputStream zins = new ZipInputStream(fins);
                byte[] ch = new byte[256];
                while (true) {
                    ZipEntry ze = zins.getNextEntry();
                    if (ze != null) {
                        if (ze.getName().endsWith(filter)) {
                            if (ze.isDirectory()) {
                                zins.closeEntry();
                            } else {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                while (true) {
                                    int i = zins.read(ch);
                                    if (i != -1) {
                                        bos.write(ch, 0, i);
                                    } else {
                                        zins.closeEntry();
                                        fins.close();
                                        zins.close();
                                        return bos.toByteArray();
                                    }
                                }
                            }
                        }
                    } else {
                        fins.close();
                        zins.close();
                        return null;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    /* loaded from: classes.dex */
    static class zlib {
        zlib() {
        }

        public static byte[] compressData(byte[] bytes) {
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            try {
                byte[] tempByte = new byte[100];
                int compressedDataLength = -1;
                Deflater compresser = new Deflater();
                compresser.setInput(bytes);
                compresser.finish();
                while (compressedDataLength != 0) {
                    compressedDataLength = compresser.deflate(tempByte);
                    bis.write(tempByte, 0, compressedDataLength);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return bis.toByteArray();
        }

        public static byte[] decompressData(byte[] bytes) {
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            int resultLength = -1;
            try {
                Inflater decompresser = new Inflater();
                decompresser.setInput(bytes, 0, bytes.length);
                byte[] result = new byte[1024];
                while (resultLength != 0) {
                    resultLength = decompresser.inflate(result);
                    bis.write(result, 0, resultLength);
                }
                decompresser.end();
            } catch (Exception e) {
            }
            return bis.toByteArray();
        }
    }

    /* loaded from: classes.dex */
    static class file {
        file() {
        }

        public static void craeteFile(String path, byte[] bytes) {
            FileOutputStream fout;
            File f = new File(path);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                fout = new FileOutputStream(f);
            } catch (FileNotFoundException e2) {
                e = e2;
            } catch (IOException e3) {
                e = e3;
            }
            try {
                fout.write(bytes);
                fout.close();
            } catch (FileNotFoundException e4) {
                e = e4;
                e.printStackTrace();
            } catch (IOException e5) {
                e = e5;
                e.printStackTrace();
            }
        }

        public static Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap = maixImage(Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565), 50.0f, 50.0f);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, 50, 50);
            drawable.draw(canvas);
            return bitmap;
        }

        public static Bitmap maixImage(Bitmap image, float x, float y) {
            float sx = x / image.getWidth();
            float sy = y / image.getHeight();
            Matrix mMatrix = new Matrix();
            mMatrix.reset();
            mMatrix.setScale(sx, sy);
            Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mMatrix, true);
            return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mMatrix, true);
        }
    }

    /* loaded from: classes.dex */
    static class cmd {
        cmd() {
        }

        public static boolean requirement4Root() {
            try {
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                try {
                    os.writeBytes("exit\n");
                    int exitValue = p.waitFor();
                    if (os != null) {
                        p.destroy();
                    }
                    return exitValue == 0;
                } catch (Exception e) {
                    e = e;
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e2) {
                e = e2;
            }
        }

        public static boolean uninstallPackageSilent(String pkgName, PackageManager pm) {
            if (!isPackageInstalled(pkgName, pm)) {
                return true;
            }
            runCmd("pm uninstall " + pkgName);
            boolean r = isPackageInstalled(pkgName, pm);
            return !r;
        }

        public static boolean isPackageInstalled(String pkgName, PackageManager pm) {
            try {
                pm.getPackageInfo(pkgName, 129);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        public static boolean runCmd(String cmd) {
            try {
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                os.writeBytes(String.valueOf(cmd) + "\n");
                os.writeBytes("exit\n");
                os.flush();
                int ret = p.waitFor();
                if (ret != 0) {
                    return false;
                }
                if (os != null) {
                    p.destroy();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
