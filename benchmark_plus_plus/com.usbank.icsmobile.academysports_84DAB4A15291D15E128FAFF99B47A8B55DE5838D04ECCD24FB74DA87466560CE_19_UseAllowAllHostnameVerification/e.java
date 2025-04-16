package com.usbank.icsmobile.c.b;

import android.util.Log;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/* compiled from: HttpUtils.java */
/* loaded from: classes.dex */
public class e {
    public static HttpClient a() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            SSLSocketFactory sf = new a(keyStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static void b() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() { // from class: com.usbank.icsmobile.c.b.e.1
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, new SecureRandom());
            try {
                SSLContext.setDefault(ctx);
            } catch (Throwable thr) {
                Log.w("trustAllSSL()", "Caught " + thr.getClass().getName() + " while modifying SSL settings", thr);
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.usbank.icsmobile.c.b.e.2
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ex) {
            Log.w("trustAllSSL()", "Caught " + ex.getClass().getName() + " while modifying SSL settings", ex);
        }
    }

    public static void a(final String publicKey) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() { // from class: com.usbank.icsmobile.c.b.e.3
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] xcs, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] xcs, String authType) throws CertificateException {
                    if (xcs == null) {
                        throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
                    }
                    if (xcs.length <= 0) {
                        throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                    }
                    if (authType == null || !authType.equalsIgnoreCase("RSA")) {
                        throw new CertificateException("checkServerTrusted: AuthType is not RSA");
                    }
                    try {
                        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                        tmf.init((KeyStore) null);
                        TrustManager[] arr$ = tmf.getTrustManagers();
                        for (TrustManager trustManager : arr$) {
                            ((X509TrustManager) trustManager).checkServerTrusted(xcs, authType);
                        }
                        RSAPublicKey pubkey = (RSAPublicKey) xcs[0].getPublicKey();
                        String encoded = new BigInteger(1, pubkey.getEncoded()).toString(16);
                        boolean expected = publicKey.equalsIgnoreCase(encoded);
                        if (!expected) {
                            throw new CertificateException("checkServerTrusted: Expected public key: " + publicKey + ", got public key:" + encoded);
                        }
                    } catch (Exception e) {
                        throw new CertificateException(e);
                    }
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            try {
                SSLContext.setDefault(ctx);
            } catch (Throwable thr) {
                Log.w("trustAllSSL()", "Caught " + thr.getClass().getName() + " while modifying SSL settings", thr);
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(null);
        } catch (Exception ex) {
            Log.w("certificatePinSSL()", "Caught " + ex.getClass().getName() + " while modifying SSL settings", ex);
        }
    }

    public static void c() {
    }
}
