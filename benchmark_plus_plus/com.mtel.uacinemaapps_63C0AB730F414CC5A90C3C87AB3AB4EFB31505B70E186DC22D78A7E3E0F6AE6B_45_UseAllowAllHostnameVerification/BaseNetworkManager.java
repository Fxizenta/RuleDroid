package com.amaze.ad;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

/* loaded from: classes.dex */
class BaseNetworkManager {
    protected static final String TAG = "DownLoadManager";
    private static int HTTP_ESTABLISH_TIMEOUT = 0;
    private static int HTTP_DATA_RECEIVING_TIMEOUT = 0;

    public static HttpEntity httpGet(String url) {
        HttpEntity result;
        HttpClient httpclient;
        HttpGet httpget;
        try {
            try {
                httpclient = getHttpClient();
                httpget = new HttpGet(url);
            } catch (Throwable th) {
                th = th;
            }
        } catch (ConnectTimeoutException e) {
            cte = e;
        } catch (Exception e2) {
            e = e2;
        }
        try {
            HttpResponse response = httpclient.execute(httpget);
            result = response.getEntity();
        } catch (ConnectTimeoutException e3) {
            cte = e3;
            Configure.Error("ConnectTimeoutException: " + cte.toString());
            result = null;
            return result;
        } catch (Exception e4) {
            e = e4;
            Configure.Error("Error in http connection: " + e.toString());
            result = null;
            return result;
        } catch (Throwable th2) {
            th = th2;
            throw th;
        }
        return result;
    }

    public static String getStringFromHttpGet(String url) {
        try {
            String result = EntityUtils.toString(httpGet(url), "UTF-8");
            return result;
        } catch (Exception e) {
            Configure.Error("Error in http connection: " + e.toString());
            return null;
        }
    }

    public static InputStream getInputStreamFromHttpGet(String url) {
        InputStream result;
        BufferedHttpEntity bufHttpEntity;
        try {
            try {
                bufHttpEntity = new BufferedHttpEntity(httpGet(url));
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            result = bufHttpEntity.getContent();
        } catch (Exception e2) {
            e = e2;
            Configure.Error("Error in http connection: " + e.toString());
            result = null;
            return result;
        } catch (Throwable th2) {
            th = th2;
            throw th;
        }
        return result;
    }

    public static String getStringFromHttpPost(String url, HttpEntity params) {
        String result;
        HttpClient httpclient;
        HttpPost httppost;
        try {
            try {
                httpclient = getHttpClient();
                httppost = new HttpPost(url);
            } catch (Throwable th) {
                th = th;
            }
        } catch (ConnectTimeoutException e) {
            cte = e;
        } catch (Exception e2) {
            e = e2;
        }
        try {
            httppost.setEntity(params);
            HttpResponse response = httpclient.execute(httppost);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (ConnectTimeoutException e3) {
            cte = e3;
            Configure.Error("ConnectTimeoutException: " + cte.toString());
            result = null;
            return result;
        } catch (Exception e4) {
            e = e4;
            Configure.Error("Error in http connection: " + e.toString());
            result = null;
            return result;
        } catch (Throwable th2) {
            th = th2;
            throw th;
        }
        return result;
    }

    public static String getStringFromHttpsPost(String url, HttpEntity params) {
        String result;
        HttpClient httpclient;
        HttpPost httppost;
        try {
            try {
                httpclient = getHttpsClient();
                httppost = new HttpPost(url);
            } catch (Throwable th) {
                th = th;
            }
        } catch (ConnectTimeoutException e) {
            cte = e;
        } catch (Exception e2) {
            e = e2;
        }
        try {
            httppost.setEntity(params);
            HttpResponse response = httpclient.execute(httppost);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (ConnectTimeoutException e3) {
            cte = e3;
            Configure.Error("ConnectTimeoutException: " + cte.toString());
            result = null;
            return result;
        } catch (Exception e4) {
            e = e4;
            Configure.Error("Error in http connection: " + e.toString());
            result = null;
            return result;
        } catch (Throwable th2) {
            th = th2;
            throw th;
        }
        return result;
    }

    private static HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        return new DefaultHttpClient(httpParams);
    }

    public static HttpClient getHttpsClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory mSSLSocketFactory = new MySSLSocketFactory(trustStore);
            mSSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            HttpConnectionParams.setConnectionTimeout(params, HTTP_ESTABLISH_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_DATA_RECEIVING_TIMEOUT);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", mSSLSocketFactory, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (IOException e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        } catch (KeyManagementException e2) {
            e2.printStackTrace();
            return new DefaultHttpClient();
        } catch (KeyStoreException e3) {
            e3.printStackTrace();
            return new DefaultHttpClient();
        } catch (NoSuchAlgorithmException e4) {
            e4.printStackTrace();
            return new DefaultHttpClient();
        } catch (UnrecoverableKeyException e5) {
            e5.printStackTrace();
            return new DefaultHttpClient();
        } catch (CertificateException e6) {
            e6.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext mSSLContext;

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
        public Socket createSocket() throws IOException {
            return this.mSSLContext.getSocketFactory().createSocket();
        }

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return this.mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            this.mSSLContext = SSLContext.getInstance("TLS");
            TrustManager mTrustManager = new X509TrustManager() { // from class: com.amaze.ad.BaseNetworkManager.MySSLSocketFactory.1
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
            this.mSSLContext.init(null, new TrustManager[]{mTrustManager}, null);
        }
    }
}
