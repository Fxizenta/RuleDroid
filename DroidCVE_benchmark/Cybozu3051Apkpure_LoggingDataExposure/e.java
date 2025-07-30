package com.cybozu.kunailite.e;

import android.util.Log;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandomSpi;

/* compiled from: PRNGFixes.java */
/* loaded from: classes.dex */
public class e extends SecureRandomSpi {
    private static final File a = new File("/dev/urandom");
    private static final Object b = new Object();
    private static DataInputStream c;
    private static OutputStream d;
    private boolean e;

    @Override // java.security.SecureRandomSpi
    protected void engineSetSeed(byte[] bArr) {
        OutputStream b2;
        try {
            synchronized (b) {
                b2 = b();
            }
            b2.write(bArr);
            b2.flush();
        } catch (IOException e) {
            Log.w(d.class.getSimpleName(), "Failed to mix seed into " + a);
        } finally {
            this.e = true;
        }
    }

    @Override // java.security.SecureRandomSpi
    protected void engineNextBytes(byte[] bArr) {
        DataInputStream a2;
        byte[] c2;
        if (!this.e) {
            c2 = d.c();
            engineSetSeed(c2);
        }
        try {
            synchronized (b) {
                a2 = a();
            }
            synchronized (a2) {
                a2.readFully(bArr);
            }
        } catch (IOException e) {
            throw new SecurityException("Failed to read from " + a, e);
        }
    }

    @Override // java.security.SecureRandomSpi
    protected byte[] engineGenerateSeed(int i) {
        byte[] bArr = new byte[i];
        engineNextBytes(bArr);
        return bArr;
    }

    private static DataInputStream a() {
        DataInputStream dataInputStream;
        synchronized (b) {
            if (c == null) {
                try {
                    c = new DataInputStream(new FileInputStream(a));
                } catch (IOException e) {
                    throw new SecurityException("Failed to open " + a + " for reading", e);
                }
            }
            dataInputStream = c;
        }
        return dataInputStream;
    }

    private static OutputStream b() {
        OutputStream outputStream;
        synchronized (b) {
            if (d == null) {
                d = new FileOutputStream(a);
            }
            outputStream = d;
        }
        return outputStream;
    }
}
