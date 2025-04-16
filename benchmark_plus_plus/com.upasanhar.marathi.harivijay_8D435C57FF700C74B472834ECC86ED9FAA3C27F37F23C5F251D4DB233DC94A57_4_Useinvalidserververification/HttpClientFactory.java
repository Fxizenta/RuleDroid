package com.amazonaws.http;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.impl.client.HttpRequestNoRetryHandler;
import com.amazonaws.http.impl.client.SdkHttpClient;
import com.amazonaws.org.apache.http.HttpHost;
import com.amazonaws.org.apache.http.HttpRequestInterceptor;
import com.amazonaws.org.apache.http.ProtocolException;
import com.amazonaws.org.apache.http.auth.AuthScope;
import com.amazonaws.org.apache.http.auth.ChallengeState;
import com.amazonaws.org.apache.http.auth.NTCredentials;
import com.amazonaws.org.apache.http.client.AuthCache;
import com.amazonaws.org.apache.http.client.HttpClient;
import com.amazonaws.org.apache.http.client.protocol.ClientContext;
import com.amazonaws.org.apache.http.conn.ConnectTimeoutException;
import com.amazonaws.org.apache.http.conn.params.ConnRoutePNames;
import com.amazonaws.org.apache.http.conn.params.ConnRouteParams;
import com.amazonaws.org.apache.http.conn.scheme.PlainSocketFactory;
import com.amazonaws.org.apache.http.conn.scheme.Scheme;
import com.amazonaws.org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import com.amazonaws.org.apache.http.conn.scheme.SchemeRegistry;
import com.amazonaws.org.apache.http.conn.scheme.SchemeSocketFactory;
import com.amazonaws.org.apache.http.conn.ssl.SSLSocketFactory;
import com.amazonaws.org.apache.http.impl.auth.BasicScheme;
import com.amazonaws.org.apache.http.impl.client.BasicAuthCache;
import com.amazonaws.org.apache.http.impl.client.DefaultRedirectStrategy;
import com.amazonaws.org.apache.http.impl.conn.PoolingClientConnectionManager;
import com.amazonaws.org.apache.http.params.BasicHttpParams;
import com.amazonaws.org.apache.http.params.HttpConnectionParams;
import com.amazonaws.org.apache.http.params.HttpParams;
import com.amazonaws.org.apache.http.protocol.HttpContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class HttpClientFactory {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class LocationHeaderNotRequiredRedirectStrategy extends DefaultRedirectStrategy {
        private LocationHeaderNotRequiredRedirectStrategy() {
        }

        @Override // com.amazonaws.org.apache.http.impl.client.DefaultRedirectStrategy, com.amazonaws.org.apache.http.client.RedirectStrategy
        public boolean isRedirected(com.amazonaws.org.apache.http.HttpRequest httpRequest, com.amazonaws.org.apache.http.HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (httpResponse.getFirstHeader("location") == null && statusCode == 301) {
                return false;
            }
            return super.isRedirected(httpRequest, httpResponse, httpContext);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PreemptiveProxyAuth implements HttpRequestInterceptor {
        private final HttpHost proxyHost;

        public PreemptiveProxyAuth(HttpHost httpHost) {
            this.proxyHost = httpHost;
        }

        @Override // com.amazonaws.org.apache.http.HttpRequestInterceptor
        public void process(com.amazonaws.org.apache.http.HttpRequest httpRequest, HttpContext httpContext) {
            BasicScheme basicScheme = new BasicScheme(ChallengeState.PROXY);
            if (httpContext.getAttribute(ClientContext.AUTH_CACHE) != null) {
                ((AuthCache) httpContext.getAttribute(ClientContext.AUTH_CACHE)).put(this.proxyHost, basicScheme);
                return;
            }
            BasicAuthCache basicAuthCache = new BasicAuthCache();
            basicAuthCache.put(this.proxyHost, basicScheme);
            httpContext.setAttribute(ClientContext.AUTH_CACHE, basicAuthCache);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TrustingSocketFactory implements SchemeSocketFactory, SchemeLayeredSocketFactory {
        private SSLContext sslcontext;

        private TrustingSocketFactory() {
            this.sslcontext = null;
        }

        private static SSLContext createSSLContext() throws IOException {
            try {
                SSLContext sSLContext = SSLContext.getInstance(SSLSocketFactory.TLS);
                sSLContext.init(null, new TrustManager[]{new TrustingX509TrustManager()}, null);
                return sSLContext;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        private SSLContext getSSLContext() throws IOException {
            if (this.sslcontext == null) {
                this.sslcontext = createSSLContext();
            }
            return this.sslcontext;
        }

        @Override // com.amazonaws.org.apache.http.conn.scheme.SchemeSocketFactory
        public Socket connectSocket(Socket socket, InetSocketAddress inetSocketAddress, InetSocketAddress inetSocketAddress2, HttpParams httpParams) throws IOException, UnknownHostException, ConnectTimeoutException {
            int connectionTimeout = HttpConnectionParams.getConnectionTimeout(httpParams);
            int soTimeout = HttpConnectionParams.getSoTimeout(httpParams);
            SSLSocket sSLSocket = (SSLSocket) (socket != null ? socket : createSocket(httpParams));
            if (inetSocketAddress2 != null) {
                sSLSocket.bind(inetSocketAddress2);
            }
            sSLSocket.connect(inetSocketAddress, connectionTimeout);
            sSLSocket.setSoTimeout(soTimeout);
            return sSLSocket;
        }

        @Override // com.amazonaws.org.apache.http.conn.scheme.SchemeLayeredSocketFactory
        public Socket createLayeredSocket(Socket socket, String str, int i, HttpParams httpParams) throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(socket, str, i, true);
        }

        @Override // com.amazonaws.org.apache.http.conn.scheme.SchemeSocketFactory
        public Socket createSocket(HttpParams httpParams) throws IOException {
            return getSSLContext().getSocketFactory().createSocket();
        }

        @Override // com.amazonaws.org.apache.http.conn.scheme.SchemeSocketFactory
        public boolean isSecure(Socket socket) throws IllegalArgumentException {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TrustingX509TrustManager implements X509TrustManager {
        private static final X509Certificate[] X509_CERTIFICATES = new X509Certificate[0];

        private TrustingX509TrustManager() {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return X509_CERTIFICATES;
        }
    }

    public HttpClient createHttpClient(ClientConfiguration clientConfiguration) {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, clientConfiguration.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(basicHttpParams, clientConfiguration.getSocketTimeout());
        HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, true);
        HttpConnectionParams.setTcpNoDelay(basicHttpParams, true);
        int i = clientConfiguration.getSocketBufferSizeHints()[0];
        int i2 = clientConfiguration.getSocketBufferSizeHints()[1];
        if (i > 0 || i2 > 0) {
            HttpConnectionParams.setSocketBufferSize(basicHttpParams, Math.max(i, i2));
        }
        PoolingClientConnectionManager createPoolingClientConnManager = ConnectionManagerFactory.createPoolingClientConnManager(clientConfiguration, basicHttpParams);
        SdkHttpClient sdkHttpClient = new SdkHttpClient(createPoolingClientConnManager, basicHttpParams);
        sdkHttpClient.setHttpRequestRetryHandler(HttpRequestNoRetryHandler.Singleton);
        sdkHttpClient.setRedirectStrategy(new LocationHeaderNotRequiredRedirectStrategy());
        if (clientConfiguration.getLocalAddress() != null) {
            ConnRouteParams.setLocalAddress(basicHttpParams, clientConfiguration.getLocalAddress());
        }
        try {
            Scheme scheme = new Scheme(HttpHost.DEFAULT_SCHEME_NAME, 80, PlainSocketFactory.getSocketFactory());
            Scheme scheme2 = new Scheme("https", 443, new SSLSocketFactory(SSLContext.getDefault(), SSLSocketFactory.STRICT_HOSTNAME_VERIFIER));
            SchemeRegistry schemeRegistry = createPoolingClientConnManager.getSchemeRegistry();
            schemeRegistry.register(scheme);
            schemeRegistry.register(scheme2);
            sdkHttpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, new TrustingSocketFactory()));
            String proxyHost = clientConfiguration.getProxyHost();
            int proxyPort = clientConfiguration.getProxyPort();
            if (proxyHost != null && proxyPort > 0) {
                AmazonHttpClient.log.info("Configuring Proxy. Proxy Host: " + proxyHost + " Proxy Port: " + proxyPort);
                HttpHost httpHost = new HttpHost(proxyHost, proxyPort);
                sdkHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
                String proxyUsername = clientConfiguration.getProxyUsername();
                String proxyPassword = clientConfiguration.getProxyPassword();
                String proxyDomain = clientConfiguration.getProxyDomain();
                String proxyWorkstation = clientConfiguration.getProxyWorkstation();
                if (proxyUsername != null && proxyPassword != null) {
                    sdkHttpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort), new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
                }
                if (clientConfiguration.isPreemptiveBasicProxyAuth()) {
                    sdkHttpClient.addRequestInterceptor(new PreemptiveProxyAuth(httpHost), 0);
                }
            }
            return sdkHttpClient;
        } catch (NoSuchAlgorithmException e) {
            throw new AmazonClientException("Unable to access default SSL context", e);
        }
    }
}
