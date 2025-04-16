package com.yellowbook.android2.api.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class MySslSocketFactory extends SSLSocketFactory {
    private static MySslSocketFactory instance;
    private final SSLContext sslContext;

    public static synchronized MySslSocketFactory getSSLSocketFactory() throws IOException {
        MySslSocketFactory mySslSocketFactory;
        synchronized (MySslSocketFactory.class) {
            if (instance == null) {
                try {
                    try {
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);
                        instance = new MySslSocketFactory(trustStore);
                        instance.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    } catch (KeyManagementException e) {
                        throw new IOException("Couldnt set up SSL properly");
                    } catch (UnrecoverableKeyException e2) {
                        throw new IOException("Couldnt set up SSL properly");
                    }
                } catch (KeyStoreException e3) {
                    throw new IOException("Couldnt set up SSL properly");
                } catch (NoSuchAlgorithmException e4) {
                    throw new IOException("Couldnt set up SSL properly");
                } catch (CertificateException e5) {
                    throw new IOException("Couldnt set up SSL properly");
                }
            }
            mySslSocketFactory = instance;
        }
        return mySslSocketFactory;
    }

    private MySslSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        this.sslContext = SSLContext.getInstance("TLS");
        TrustManager tm = new X509TrustManager() { // from class: com.yellowbook.android2.api.net.MySslSocketFactory.1
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        this.sslContext.init(null, new TrustManager[]{tm}, null);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
    public Socket createSocket() throws IOException {
        return this.sslContext.getSocketFactory().createSocket();
    }
}
