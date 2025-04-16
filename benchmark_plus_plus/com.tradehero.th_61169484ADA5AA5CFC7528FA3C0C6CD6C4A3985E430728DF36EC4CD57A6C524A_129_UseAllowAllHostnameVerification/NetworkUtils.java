package com.tradehero.th.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import timber.log.Timber;

/* loaded from: classes.dex */
public final class NetworkUtils {
    public static boolean isConnected(Context con) {
        boolean z;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) con.getSystemService("connectivity");
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                if (networkInfo.isConnected()) {
                    z = true;
                    boolean connected = z;
                    return connected;
                }
            }
            z = false;
            boolean connected2 = z;
            return connected2;
        } catch (Exception e2) {
            Timber.d(e2.getMessage(), new Object[0]);
            return false;
        }
    }

    public static boolean isWiFiConnected(Context context) {
        boolean haveConnectedWifi = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI") && ni.isConnected()) {
                haveConnectedWifi = true;
            }
        }
        return haveConnectedWifi;
    }

    public static boolean isMobileNetworkConnected(Context context) {
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE") && ni.isConnected()) {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedMobile;
    }

    public static SSLSocketFactory createBadSslSocketFactory() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager() { // from class: com.tradehero.th.utils.NetworkUtils.1
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
            context.init(null, new TrustManager[]{permissive}, new SecureRandom());
            return context.getSocketFactory();
        } catch (Exception e2) {
            throw new AssertionError(e2);
        }
    }
}
