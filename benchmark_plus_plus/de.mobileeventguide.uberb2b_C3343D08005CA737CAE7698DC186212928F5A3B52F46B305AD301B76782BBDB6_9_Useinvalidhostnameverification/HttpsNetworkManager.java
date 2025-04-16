package com.mobileeventguide.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

/* loaded from: classes.dex */
public class HttpsNetworkManager {
    public static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { // from class: com.mobileeventguide.service.HttpsNetworkManager.1
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static void trustAllHosts() {
        TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.mobileeventguide.service.HttpsNetworkManager.2
            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyUrlToFile(String url, File file) throws IOException {
        if (!file.isDirectory()) {
            trustAllHosts();
            URL ur = new URL(url.replaceAll(" ", "%20"));
            HttpsURLConnection https = (HttpsURLConnection) ur.openConnection();
            https.setRequestProperty("User-agent", "Mozilla/4.0");
            https.setHostnameVerifier(DO_NOT_VERIFY);
            https.connect();
            InputStream is = https.getInputStream();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            byte[] b = new byte[1024];
            while (true) {
                int read = is.read(b);
                if (read > 0) {
                    dos.write(b, 0, read);
                } else {
                    dos.flush();
                    dos.close();
                    is.close();
                    return;
                }
            }
        }
    }

    public static void copyInputStreamToFile(InputStream is, File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        byte[] b = new byte[1024];
        while (true) {
            int read = is.read(b);
            if (read > 0) {
                dos.write(b, 0, read);
            } else {
                dos.flush();
                dos.close();
                is.close();
                return;
            }
        }
    }

    public static HttpClient getDefaultHttpClient() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", new PlainSocketFactory(), 80));
        registry.register(new Scheme("https", new FakeSocketFactory(), 443));
        BasicHttpParams params = new BasicHttpParams();
        return new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params);
    }
}
