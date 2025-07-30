package com.zoodles.kidmode.gateway;

import com.zoodles.kidmode.ZoodlesConstants;
import com.zoodles.kidmode.gateway.exception.GatewayException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/* loaded from: classes.dex */
public class HTTPClientFactory {
    private static final HTTPClientFactory sInstance = new HTTPClientFactory();
    private final SchemeRegistry sRegistry = new SchemeRegistry();

    public static DefaultHttpClient newClient() throws GatewayException {
        return sInstance.newHttpClient();
    }

    private HTTPClientFactory() {
        this.sRegistry.register(new Scheme(ZoodlesConstants.HTTP, PlainSocketFactory.getSocketFactory(), 80));
        try {
            this.sRegistry.register(new Scheme(ZoodlesConstants.HTTPS, ZoodlesSSLSocketFactory.getDefault(), ZoodlesConstants.HTTPS_PORT));
        } catch (Exception e) {
        }
    }

    private DefaultHttpClient newHttpClient() throws GatewayException {
        HttpParams httpParams = new BasicHttpParams();
        SingleClientConnManager mgr = new SingleClientConnManager(httpParams, this.sRegistry);
        DefaultHttpClient client = new DefaultHttpClient(mgr, httpParams);
        return client;
    }

    /* loaded from: classes.dex */
    public static class ZoodlesSSLSocketFactory extends SSLSocketFactory {
        private static ZoodlesSSLSocketFactory sSocketFactory;
        private javax.net.ssl.SSLSocketFactory factory;

        public static synchronized SocketFactory getDefault() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            ZoodlesSSLSocketFactory zoodlesSSLSocketFactory;
            synchronized (ZoodlesSSLSocketFactory.class) {
                if (sSocketFactory == null) {
                    sSocketFactory = new ZoodlesSSLSocketFactory();
                }
                zoodlesSSLSocketFactory = sSocketFactory;
            }
            return zoodlesSSLSocketFactory;
        }

        private ZoodlesSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            super(null);
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{new ZoodlesTrustManager()}, null);
            this.factory = sslcontext.getSocketFactory();
        }

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
        public Socket createSocket() throws IOException {
            return this.factory.createSocket();
        }

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
        public Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
            return this.factory.createSocket(socket, s, i, flag);
        }

        public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1, int j) throws IOException {
            return this.factory.createSocket(inaddr, i, inaddr1, j);
        }

        public Socket createSocket(InetAddress inaddr, int i) throws IOException {
            return this.factory.createSocket(inaddr, i);
        }

        public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
            return this.factory.createSocket(s, i, inaddr, j);
        }

        public Socket createSocket(String s, int i) throws IOException {
            return this.factory.createSocket(s, i);
        }

        public String[] getDefaultCipherSuites() {
            return this.factory.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return this.factory.getSupportedCipherSuites();
        }
    }

    /* loaded from: classes.dex */
    public static class ZoodlesTrustManager implements X509TrustManager {
        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }
}
