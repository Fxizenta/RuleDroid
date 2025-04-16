package com.elink.lib.offlinelock;

import com.tencent.connect.common.Constants;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class a {
    public static byte[] a(SecretKeySpec secretKeySpec, byte[] bArr) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, secretKeySpec);
        return cipher.doFinal(bArr);
    }

    public static byte[] b(byte[] bArr) throws GeneralSecurityException {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        try {
            return a(c(), bArr);
        } catch (UnsupportedEncodingException e2) {
            b.n.a.f.h("BleAESCrypt").j(e2, "UnsupportedEncodingException ", new Object[0]);
            throw new GeneralSecurityException(e2);
        }
    }

    private static SecretKeySpec c() throws UnsupportedEncodingException {
        return new SecretKeySpec("7b7079bb69001dce".getBytes(Constants.ENC_UTF_8), "AES");
    }
}
