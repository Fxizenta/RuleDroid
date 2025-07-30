package com.scringo.utils;

import com.scringo.service.ScringoConnectivityManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;

/* loaded from: classes.dex */
public class ScringoHttpFetcher extends Thread {
    private static final int SCRINGO_CONNECT_TIMEOUT = 10000;
    private static final int SCRINGO_READ_TIMEOUT = 60000;
    protected static boolean initialized;
    private static HostnameVerifier nullHostNameVerifier = new HostnameVerifier() { // from class: com.scringo.utils.ScringoHttpFetcher.1
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private static TrustManager[] wrappedTrustManagers = {new X509TrustManager() { // from class: com.scringo.utils.ScringoHttpFetcher.2
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
    protected ScringoHttpResponseHandler responseHandler;
    protected String responseStr;
    public ScringoRetryType retryType = ScringoRetryType.SCRINGO_RETRY_TYPE_RETRY;
    protected String url;

    /* loaded from: classes.dex */
    public enum ScringoRetryType {
        SCRINGO_RETRY_TYPE_RETRY,
        SCRINGO_RETRY_TYPE_ERROR,
        SCRINGO_RETRY_TYPE_RETRY_AND_WARN,
        SCRINGO_RETRY_TYPE_IGNORE;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ScringoRetryType[] valuesCustom() {
            ScringoRetryType[] valuesCustom = values();
            int length = valuesCustom.length;
            ScringoRetryType[] scringoRetryTypeArr = new ScringoRetryType[length];
            System.arraycopy(valuesCustom, 0, scringoRetryTypeArr, 0, length);
            return scringoRetryTypeArr;
        }
    }

    static {
        initialized = false;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, wrappedTrustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(nullHostNameVerifier);
            initialized = true;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
    }

    public ScringoHttpFetcher(String url, ScringoHttpResponseHandler responseHandler) {
        this.url = url;
        this.responseHandler = responseHandler;
    }

    public ScringoHttpFetcher(String url) {
        this.url = url;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        if (initialized) {
            try {
                if (!ScringoConnectivityManager.instance.connectivityOn) {
                    if (this.retryType == ScringoRetryType.SCRINGO_RETRY_TYPE_RETRY) {
                        ScringoLogger.d("Network unavailable, pending - not implemented!");
                    } else if (this.retryType == ScringoRetryType.SCRINGO_RETRY_TYPE_RETRY_AND_WARN) {
                        ScringoLogger.d("Network unavailable, pending - not implemented");
                        this.responseHandler.handleNetworkWarning(this);
                    } else if (this.retryType == ScringoRetryType.SCRINGO_RETRY_TYPE_ERROR) {
                        this.responseHandler.handleError(this, new Exception("Scringo: Connectivity"));
                    } else {
                        ScringoRetryType scringoRetryType = ScringoRetryType.SCRINGO_RETRY_TYPE_IGNORE;
                    }
                } else {
                    HttpURLConnection ucon = createConnection();
                    handleResponse(ucon);
                }
            } catch (IOException e) {
                ScringoLogger.d("ScringoHttpFetcher Error(io): " + this.responseStr);
                if (e.getMessage() != null && (e.getMessage().contains("Network is unreachable") || e.getMessage().contains("Connection timed out"))) {
                    ScringoLogger.d("Network unavailable (error), pending - not implemented!");
                } else {
                    this.responseHandler.handleError(this, e);
                }
            } catch (JSONException e2) {
                ScringoLogger.d("ScringoHttpFetcher Error(json): " + this.responseStr);
                this.responseHandler.handleError(this, e2);
            } catch (Throwable e3) {
                ScringoLogger.d("ScringoHttpFetcher Error(general): " + this.responseStr);
                this.responseHandler.handleError(this, e3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpURLConnection createConnection() throws MalformedURLException, IOException {
        URL myURL = new URL(this.url);
        HttpURLConnection ucon = (HttpURLConnection) myURL.openConnection();
        return ucon;
    }

    protected void handleResponse(HttpURLConnection ucon) throws IOException, JSONException {
        getResponseStr(ucon);
        this.responseHandler.handleResponse(this, this.responseStr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void getResponseStr(HttpURLConnection ucon) throws IOException {
        ucon.setConnectTimeout(SCRINGO_CONNECT_TIMEOUT);
        ucon.setReadTimeout(SCRINGO_READ_TIMEOUT);
        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is, 8192);
        ByteArrayBuffer baf = new ByteArrayBuffer(1000);
        while (true) {
            int current = bis.read();
            if (current != -1) {
                baf.append((byte) current);
            } else {
                this.responseStr = new String(baf.toByteArray());
                return;
            }
        }
    }
}
