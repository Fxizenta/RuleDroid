package com.alipay.sdk.m.l0;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import org.apache.tools.tar.TarConstants;

/* loaded from: classes.dex */
public class a {
    public static String a(String str) {
        byte[] bArrA;
        try {
            bArrA = a(a(), str.getBytes());
        } catch (Exception unused) {
            bArrA = null;
        }
        if (bArrA != null) {
            return a(bArrA);
        }
        return null;
    }

    public static String b(String str) {
        try {
            return new String(b(a(), m62a(str)));
        } catch (Exception unused) {
            return null;
        }
    }

    public static byte[] a() throws Exception {
        return e.a(new byte[]{33, TarConstants.LF_GNUTYPE_SPARSE, -50, -89, -84, -114, 80, 99, 10, 63, MissingArgPtg.sid, -65, -11, IntPtg.sid, 101, -118});
    }

    public static byte[] b(byte[] bArr, byte[] bArr2) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKeySpec, new IvParameterSpec(b()));
        return cipher.doFinal(bArr2);
    }

    public static byte[] a(byte[] bArr, byte[] bArr2) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, secretKeySpec, new IvParameterSpec(b()));
        return cipher.doFinal(bArr2);
    }

    public static byte[] b() {
        try {
            byte[] bArrA = b.a("IUQSvE6r1TfFPdPEjfklLw==".getBytes("UTF-8"), 2);
            if (bArrA != null) {
                return e.a(bArrA);
            }
        } catch (Exception unused) {
        }
        return new byte[16];
    }

    /* renamed from: a, reason: collision with other method in class */
    public static byte[] m62a(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i10 = 0; i10 < length; i10++) {
            int i11 = i10 * 2;
            bArr[i10] = Integer.valueOf(str.substring(i11, i11 + 2), 16).byteValue();
        }
        return bArr;
    }

    public static String a(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte b10 : bArr) {
            a(stringBuffer, b10);
        }
        return stringBuffer.toString();
    }

    public static void a(StringBuffer stringBuffer, byte b10) {
        stringBuffer.append("0123456789ABCDEF".charAt((b10 >> 4) & 15));
        stringBuffer.append("0123456789ABCDEF".charAt(b10 & IntersectionPtg.sid));
    }
}
