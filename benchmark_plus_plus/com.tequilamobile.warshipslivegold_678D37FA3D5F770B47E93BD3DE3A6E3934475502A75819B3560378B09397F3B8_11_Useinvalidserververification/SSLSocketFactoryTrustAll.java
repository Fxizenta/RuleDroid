package com.getjar.sdk.comm;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
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
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};
    private static final X509HostnameVerifier _HostnameVerifier = new X509HostnameVerifier() { // from class: com.getjar.sdk.comm.SSLSocketFactoryTrustAll.2
        @Override // org.apache.http.conn.ssl.X509HostnameVerifier, javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String host, SSLSocket ssl) throws IOException {
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String host, X509Certificate cert) throws SSLException {
        }

        @Override // org.apache.http.conn.ssl.X509HostnameVerifier
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        }
    };

    public SSLSocketFactoryTrustAll(KeyStore truststore) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        this._sslContext = SSLContext.getInstance("TLS");
        this._sslContext.init(null, _TrustAllCerts, null);
        setHostnameVerifier(_HostnameVerifier);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return this._sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
    public Socket createSocket() throws IOException {
        return this._sslContext.getSocketFactory().createSocket();
    }
}
