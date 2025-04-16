package com.omniture;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/* loaded from: classes.dex */
public class RequestHandlerSe extends RequestHandlerSe13 {
    @Override // com.omniture.RequestHandlerSe13
    protected HttpURLConnection requestConnect(String str) {
        HttpURLConnection httpURLConnection;
        try {
            URL url = new URL(str);
            if (str.indexOf("https://") >= 0) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() { // from class: com.omniture.RequestHandlerSe.1
                    @Override // javax.net.ssl.HostnameVerifier
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                httpURLConnection = httpsURLConnection;
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }
            return httpURLConnection;
        } catch (Exception e) {
            return null;
        }
    }
}
