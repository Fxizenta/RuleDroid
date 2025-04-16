package it.thespacecinema.android;

import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
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

/* loaded from: classes.dex */
public class CustomSSLSocketFactory extends SSLSocketFactory {
    private SSLContext sslContext;

    public CustomSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        this.sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
        TrustManager tm = new X509TrustManager() { // from class: it.thespacecinema.android.CustomSSLSocketFactory.1
            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        };
        this.sslContext.init(null, new TrustManager[]{tm}, null);
    }

    private void injectHostname(Socket socket, String host) {
        try {
            Utils.logDebug("Inject Hostname");
            Field field = InetAddress.class.getDeclaredField("hostName");
            field.setAccessible(true);
            field.set(socket.getInetAddress(), host);
        } catch (Exception ignored) {
            Utils.logDebug("Ignored exception:");
            ignored.printStackTrace();
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.LayeredSocketFactory
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        Utils.logDebug("createSocket(...)");
        injectHostname(socket, host);
        return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public Socket createSocket() throws IOException {
        Utils.logDebug("createSocket()");
        return this.sslContext.getSocketFactory().createSocket();
    }
}
