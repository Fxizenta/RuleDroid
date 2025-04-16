package com.accells.access.home;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Pair;
import com.accells.PingIdApplication;
import com.accells.access.home.v0;
import com.google.gson.reflect.TypeToken;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.crypto.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* compiled from: ExplicitSessionAsyncNew.java */
/* loaded from: classes.dex */
public class v0 {

    /* renamed from: a, reason: collision with root package name */
    private static Logger f520a;
    private com.accells.communication.f.l e;
    private volatile boolean f;
    private z0 g;
    private long h;
    private String i = null;
    private final Runnable j = new a();

    /* renamed from: b, reason: collision with root package name */
    private final Executor f521b = Executors.newSingleThreadExecutor();

    /* renamed from: c, reason: collision with root package name */
    private final Handler f522c = new Handler(Looper.getMainLooper());

    /* renamed from: d, reason: collision with root package name */
    private final com.accells.communication.c f523d = new com.accells.communication.c();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: ExplicitSessionAsyncNew.java */
    /* loaded from: classes.dex */
    public class a implements Runnable {

        /* compiled from: ExplicitSessionAsyncNew.java */
        /* renamed from: com.accells.access.home.v0$a$a, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        class C0016a extends TypeToken<com.accells.communication.f.e<com.accells.communication.f.m>> {
            C0016a() {
            }
        }

        a() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public /* synthetic */ void b() {
            if (v0.this.i != null) {
                v0.this.g.a(v0.this.i);
            } else {
                v0.this.l().info("[flow=GET_EXPLICIT_SESSION] SessionId is NULL");
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            v0.this.l().info("[flow=GET_EXPLICIT_SESSION] Start");
            try {
                KeyPair m = v0.this.m();
                v0.this.e.setPublicKey(org.accells.utils.b.x(m.getPublic()));
                v0.this.e.setRequestKey(v0.this.h);
                int i = 0;
                while (!v0.this.f && !PingIdApplication.k().D()) {
                    v0.this.l().info(String.format(Locale.US, "[flow=GET_EXPLICIT_SESSION] Try #%d", Integer.valueOf(i)));
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        v0.this.l().error("Pause FAILED", (Throwable) e);
                    }
                    Pair D = v0.this.f523d.D(v0.this.e, 0, new C0016a());
                    if (((Integer) D.first).intValue() == 200) {
                        com.accells.communication.f.m mVar = (com.accells.communication.f.m) D.second;
                        if (mVar.getResponseStatus() == 0) {
                            String encryptedSessionId = mVar.getEncryptedSessionId();
                            PrivateKey privateKey = m.getPrivate();
                            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
                            cipher.init(2, privateKey);
                            v0.this.i = new String(cipher.doFinal(Base64.decode(encryptedSessionId, 2)));
                            v0.this.l().info(String.format("[flow=GET_EXPLICIT_SESSION] [result=success] [auth_session_id=%s]", v0.this.i));
                        } else {
                            v0.this.l().error(com.accells.i.m.a(mVar.getErrorId(), String.format(Locale.US, "[flow=GET_EXPLICIT_SESSION] [result=failed] Error from server. [responseStatus=%d]", Integer.valueOf(mVar.getResponseStatus()))));
                        }
                    } else {
                        v0.this.l().error("[flow=GET_EXPLICIT_SESSION] [result=failed] Sending request failed");
                    }
                    if (v0.this.i != null || (i = i + 1) >= 10) {
                        break;
                    }
                }
                v0.this.f522c.post(new Runnable() { // from class: com.accells.access.home.a
                    @Override // java.lang.Runnable
                    public final void run() {
                        v0.a.this.b();
                    }
                });
            } catch (Throwable th) {
                v0.this.l().error(String.format("[flow=GET_EXPLICIT_SESSION] [result=failed] [eMsg=%s] Sending request failed", th.getMessage()), th);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public KeyPair m() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        l().info("[flow=GET_EXPLICIT_SESSION] before key generation");
        keyPairGenerator.initialize(1024);
        l().info("[flow=GET_EXPLICIT_SESSION] after key generation");
        return keyPairGenerator.generateKeyPair();
    }

    public void j() {
        this.f521b.execute(this.j);
    }

    public z0 k() {
        return this.g;
    }

    Logger l() {
        if (f520a == null) {
            f520a = LoggerFactory.getLogger((Class<?>) v0.class);
        }
        return f520a;
    }

    public long n() {
        return this.h;
    }

    public Runnable o() {
        return this.j;
    }

    public boolean p() {
        return this.f;
    }

    public void q(z0 callback) {
        this.g = callback;
    }

    public void r(boolean cancelled) {
        this.f = cancelled;
    }

    public void s(com.accells.communication.f.l explicitSessionRequest) {
        this.e = explicitSessionRequest;
    }

    public void t(long secureRandom) {
        this.h = secureRandom;
    }
}
