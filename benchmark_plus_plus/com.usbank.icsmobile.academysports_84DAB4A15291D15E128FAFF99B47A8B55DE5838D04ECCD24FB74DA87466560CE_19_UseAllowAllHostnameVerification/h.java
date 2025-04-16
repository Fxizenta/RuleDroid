package com.a.a.a;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/* compiled from: ADMS_RequestHandler.java */
/* loaded from: classes.dex */
public final class h {
    Method a;
    Method b;

    /* JADX INFO: Access modifiers changed from: protected */
    public h() {
        this.a = null;
        this.b = null;
        try {
            this.a = Class.forName("HttpURLConnection").getMethod("setConnectTimeout", Integer.TYPE);
        } catch (Exception e) {
            this.a = null;
        }
        try {
            this.b = Class.forName("HttpURLConnection").getMethod("setReadTimeout", Integer.TYPE);
        } catch (Exception e2) {
            this.b = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean a(String url, Hashtable<String, String> headers) {
        if (url == null) {
            return false;
        }
        boolean requestSent = false;
        try {
            HttpURLConnection connection = a(url);
            if (connection != null) {
                if (this.a != null) {
                    this.a.invoke(connection, new Integer(5000));
                }
                if (this.b != null) {
                    this.b.invoke(connection, new Integer(5000));
                }
                if (headers != null) {
                    Enumeration<String> keys = headers.keys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        String value = headers.get(key);
                        if (value != null && value.trim().length() != 0) {
                            connection.setRequestProperty(key, value);
                        }
                    }
                }
                f.x("Request Sent : " + url);
                connection.getResponseCode();
                requestSent = true;
            }
            if (connection != null) {
                connection.getInputStream().close();
                return requestSent;
            }
            return requestSent;
        } catch (IOException e) {
            if (a(e)) {
                return true;
            }
            f.x("IOException : " + e.getClass().getSimpleName() + ", " + e.getMessage());
            d.a(e);
            return requestSent;
        } catch (IllegalAccessException e2) {
            if (a(e2)) {
                return true;
            }
            f.x("IllegalAccessException : " + e2.getMessage());
            d.a(e2);
            return requestSent;
        } catch (InvocationTargetException e3) {
            if (a(e3)) {
                return true;
            }
            f.x("InvocationTargetException : " + e3.getMessage());
            d.a(e3);
            return requestSent;
        }
    }

    protected HttpURLConnection a(String str) {
        HttpURLConnection httpURLConnection;
        try {
            URL url = new URL(str);
            if (str.indexOf("https://") >= 0) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() { // from class: com.a.a.a.h.1
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
        } catch (IOException e) {
            f.x("IOException : " + e.getMessage());
            d.a(e);
            return null;
        }
    }

    private boolean a(Exception e) {
        return (e instanceof FileNotFoundException) || (e instanceof MalformedURLException);
    }
}
