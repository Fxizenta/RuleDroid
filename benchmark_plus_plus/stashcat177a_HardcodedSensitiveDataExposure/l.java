package de.heinekingmedia.stashcat.utils;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes2.dex */
public class l {

    /* renamed from: a, reason: collision with root package name */
    private static String f6514a = KeyStore.class.getSimpleName();

    /* renamed from: b, reason: collision with root package name */
    private static String f6515b = "stashcat.ks";

    /* renamed from: c, reason: collision with root package name */
    private static String f6516c = "st.ks";

    public static String a(Context context, String str, String str2, String str3, boolean z) {
        try {
            String str4 = f6515b;
            if (z) {
                str4 = f6516c;
            }
            KeyStore.Entry entry = a(new File(context.getFilesDir(), str4), str3).getEntry(str, new KeyStore.PasswordProtection(str2.toCharArray()));
            return entry != null ? a(((KeyStore.SecretKeyEntry) entry).getSecretKey()) : a(a(context, str, str2, str3));
        } catch (Exception e2) {
            n.a(f6514a, Log.getStackTraceString(e2));
            return null;
        }
    }

    private static String a(SecretKey secretKey) {
        try {
            return new String(secretKey.getEncoded(), "UTF-8");
        } catch (Exception e2) {
            n.a("KeyStore", Log.getStackTraceString(e2));
            return new String(secretKey.getEncoded());
        }
    }

    private static KeyStore a(File file, String str) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        if (file.exists()) {
            try {
                keyStore.load(new FileInputStream(file), str.toCharArray());
            } catch (IOException e2) {
                file.delete();
                a(file, str);
            }
        } else {
            keyStore.load(null, null);
            keyStore.store(new FileOutputStream(file), str.toCharArray());
        }
        return keyStore;
    }

    private static SecretKey a() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    private static SecretKey a(Context context, String str, String str2, String str3) {
        try {
            File file = new File(context.getFilesDir(), f6515b);
            KeyStore a2 = a(file, str3);
            SecretKey a3 = a();
            a2.setEntry(str, new KeyStore.SecretKeyEntry(a3), new KeyStore.PasswordProtection(str2.toCharArray()));
            a2.store(new FileOutputStream(file), str3.toCharArray());
            return a3;
        } catch (Exception e2) {
            n.a(f6514a, Log.getStackTraceString(e2));
            return null;
        }
    }

    public static SecretKey a(Context context, String str, String str2, String str3, String str4) {
        try {
            File file = new File(context.getFilesDir(), f6515b);
            KeyStore a2 = a(file, str4);
            SecretKey a3 = a(str2);
            a2.setEntry(str, new KeyStore.SecretKeyEntry(a3), new KeyStore.PasswordProtection(str3.toCharArray()));
            a2.store(new FileOutputStream(file), str4.toCharArray());
            return a3;
        } catch (Exception e2) {
            n.a(f6514a, Log.getStackTraceString(e2));
            return null;
        }
    }

    private static SecretKey a(String str) {
        return new SecretKeySpec(str.getBytes(), "AES");
    }

    public static boolean a(Context context) {
        return new File(context.getFilesDir(), f6516c).exists();
    }

    public static boolean b(Context context) {
        File file = new File(context.getFilesDir(), f6516c);
        return file.exists() && file.delete();
    }
}
