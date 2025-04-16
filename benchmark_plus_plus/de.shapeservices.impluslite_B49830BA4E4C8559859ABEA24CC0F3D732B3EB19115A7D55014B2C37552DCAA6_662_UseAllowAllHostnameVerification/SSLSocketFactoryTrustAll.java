package com.getjar.sdk.comm;

import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

/* loaded from: classes.dex */
public class SSLSocketFactoryTrustAll extends SSLSocketFactory {
    private SSLContext _sslContext;
    private static final TrustManager[] _TrustAllCerts = {new X509TrustManager() { // from class: com.getjar.sdk.comm.SSLSocketFactoryTrustAll.1
        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
        }
    }};
    private static final X509HostnameVerifier _HostnameVerifier = new X509HostnameVerifier() { // from class: com.getjar.sdk.comm.SSLSocketFactoryTrustAll.2
        @Override // org.apache.http.conn.ssl.X509HostnameVerifier, javax.net.ssl.HostnameVerifier
        public boolean verify(String str, SSLSession sSLSession) {
            return true;
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String str, SSLSocket sSLSocket) {
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String str, X509Certificate x509Certificate) {
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String str, String[] strArr, String[] strArr2) {
        }
    };

    public SSLSocketFactoryTrustAll(KeyStore keyStore) {
        super(keyStore);
        this._sslContext = SSLContext.getInstance("TLS");
        this._sslContext.init(null, _TrustAllCerts, null);
        setHostnameVerifier(_HostnameVerifier);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
    public Socket createSocket(Socket socket, String str, int i, boolean z) {
        return this._sslContext.getSocketFactory().createSocket(socket, str, i, z);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
    public Socket createSocket() {
        return this._sslContext.getSocketFactory().createSocket();
    }
}
