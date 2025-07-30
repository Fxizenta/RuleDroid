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
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ScringoJsonFetcher extends Thread {
    private static final int SCRINGO_CONNECT_TIMEOUT = 10000;
    private static final int SCRINGO_READ_TIMEOUT = 60000;
    private static boolean initialized;
    private static HostnameVerifier nullHostNameVerifier = new HostnameVerifier() { // from class: com.scringo.utils.ScringoJsonFetcher.1
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private static TrustManager[] wrappedTrustManagers = {new X509TrustManager() { // from class: com.scringo.utils.ScringoJsonFetcher.2
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
    protected ScringoJsonResponseHandler jsonResponseHandler;
    private String responseStr;
    public ScringoRetryType retryType = ScringoRetryType.SCRINGO_RETRY_TYPE_RETRY;
    private String url;

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
            initialized = true;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
    }

    public ScringoJsonFetcher(String url, ScringoJsonResponseHandler jsonResponseHandler) {
        this.url = url;
        this.jsonResponseHandler = jsonResponseHandler;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        if (initialized) {
            try {
                if (!ScringoConnectivityManager.instance.connectivityOn) {
                    if (this.retryType == ScringoRetryType.SCRINGO_RETRY_TYPE_RETRY) {
                        ScringoLogger.d("Network unavailable, pending");
                        ScringoConnectivityManager.instance.addJsonRequest(this.url, this.jsonResponseHandler);
                    } else if (this.retryType == ScringoRetryType.SCRINGO_RETRY_TYPE_RETRY_AND_WARN) {
                        ScringoLogger.d("Network unavailable, pending");
                        ScringoConnectivityManager.instance.addJsonRequest(this.url, this.jsonResponseHandler);
                        this.jsonResponseHandler.handleNetworkWarning(this);
                    } else if (this.retryType == ScringoRetryType.SCRINGO_RETRY_TYPE_ERROR) {
                        this.jsonResponseHandler.handleError(this, new Exception("Scringo: Connectivity"));
                    } else {
                        ScringoRetryType scringoRetryType = ScringoRetryType.SCRINGO_RETRY_TYPE_IGNORE;
                    }
                } else {
                    HttpURLConnection ucon = createConnection();
                    handleResponse(ucon);
                }
            } catch (IOException e) {
                ScringoLogger.d("JsonFetcher Error(io): " + this.responseStr);
                if (e.getMessage() != null && (e.getMessage().contains("Network is unreachable") || e.getMessage().contains("Connection timed out"))) {
                    ScringoLogger.d("Network unavailable (error), pending");
                    ScringoConnectivityManager.instance.addJsonRequest(this.url, this.jsonResponseHandler);
                } else {
                    this.jsonResponseHandler.handleError(this, e);
                }
            } catch (JSONException e2) {
                ScringoLogger.d("JsonFetcher Error(json): " + this.responseStr);
                this.jsonResponseHandler.handleError(this, e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpURLConnection createConnection() throws MalformedURLException, IOException {
        URL myURL = new URL(this.url);
        if (this.url.startsWith("https")) {
            HttpsURLConnection sucon = (HttpsURLConnection) myURL.openConnection();
            sucon.setHostnameVerifier(nullHostNameVerifier);
            return sucon;
        }
        HttpURLConnection ucon = (HttpURLConnection) myURL.openConnection();
        return ucon;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleResponse(HttpURLConnection ucon) throws IOException, JSONException {
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
                try {
                    JSONObject json = new JSONObject(this.responseStr);
                    this.jsonResponseHandler.handleResponse(this, json);
                    return;
                } catch (JSONException e) {
                    ScringoLogger.d("Error in JSON: " + this.responseStr);
                    throw e;
                }
            }
        }
    }
}
