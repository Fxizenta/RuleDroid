package com.sovereign.santander.netinsight;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class TagNI extends AsyncTask<Bundle, Void, Void> {
    Activity a;

    public TagNI(Activity act) {
        this.a = act;
    }

    private String sendTagNI(Bundle bundle, Activity a) {
        String url = String.valueOf("https://m.sovereignbank.com/Estatico/ntpagetag.gif?js=1") + "&ts=" + getCurrentTimeStamp();
        String lc = "";
        if (bundle.getString(Constants.LC) != null) {
            lc = bundle.getString(Constants.LC);
        }
        bundle.remove(Constants.LC);
        try {
            lc = URLEncoder.encode(lc, "UTF-8");
        } catch (Exception e) {
        }
        String urlLc = Constants.URL_LC + lc;
        String url2 = String.valueOf(String.valueOf(String.valueOf(String.valueOf(String.valueOf(String.valueOf(url) + "&lc=" + urlLc) + "&rf=") + "&rs=" + getUserScreenResolution(a)) + "&inch=" + getUserScreenInch(a)) + "&ln=" + obtenerBrowserLanguaje()) + "&tz=" + obtenerTimeZone();
        String appName = "";
        if (bundle.getString("appName") != null) {
            appName = bundle.getString("appName");
            bundle.getString("appName");
        }
        bundle.remove("appName");
        Set<String> claves = bundle.keySet();
        for (String clave : claves) {
            String valor = bundle.getString(clave);
            try {
                valor = URLEncoder.encode(valor, "UTF-8");
            } catch (Exception e2) {
            }
            url2 = String.valueOf(url2) + "&" + clave + "=" + valor;
        }
        try {
            String userAgent = getUserAgent(appName);
            URL miUrl = new URL(url2);
            if (url2.startsWith("http://")) {
                HttpURLConnection miUrlCon = (HttpURLConnection) miUrl.openConnection();
                miUrlCon.setRequestProperty("User-Agent", userAgent);
                miUrlCon.disconnect();
            } else {
                TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.sovereign.santander.netinsight.TagNI.1
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
                    HttpsURLConnection miUrlCon2 = (HttpsURLConnection) miUrl.openConnection();
                    miUrlCon2.setHostnameVerifier(Constants.DO_NOT_VERIFY);
                    miUrlCon2.setRequestProperty("User-Agent", userAgent);
                    String contentType = miUrlCon2.getContentType();
                    url2 = String.valueOf(url2) + "&contentType=" + contentType + "&userAgent=" + userAgent;
                    miUrlCon2.disconnect();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
        }
        return url2;
    }

    public static String getUserAgent(String appName) {
        String model = Build.MODEL;
        try {
            String idioma = String.valueOf(Locale.getDefault().getLanguage()) + "-" + Locale.getDefault().getCountry();
            String userAgent = String.valueOf(model) + "; Android ; " + idioma;
            return "SantanderNativeApp/2.1 (" + userAgent + ") (" + Constants.NETINSIGHT_VERSION + "-Android)";
        } catch (Exception e) {
            return "";
        }
    }

    public String getColorDepth(Activity a) {
        String cd = String.valueOf(a.getWindowManager().getDefaultDisplay().getPixelFormat());
        return cd;
    }

    public static long getCurrentTimeStamp() {
        try {
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            long timestamp = ts.getTime();
            return timestamp;
        } catch (Exception e) {
            return 0L;
        }
    }

    public static String getUserScreenResolution(Activity a) {
        try {
            Display display = ((WindowManager) a.getSystemService("window")).getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            String rs = String.valueOf(width) + "x" + height;
            return rs;
        } catch (Exception ex) {
            ex.toString();
            return "";
        }
    }

    public static String getUserScreenInch(Activity a) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int pixWidth = metrics.widthPixels;
            int pixHeight = metrics.heightPixels;
            float xdpi = metrics.xdpi;
            float ydpi = metrics.ydpi;
            float xInch = pixWidth / xdpi;
            float yInch = pixHeight / ydpi;
            double sInch = Math.sqrt(Math.pow(xInch, 2.0d) + Math.pow(yInch, 2.0d));
            String inch = new StringBuilder().append(truncate(sInch)).toString();
            return inch;
        } catch (Exception ex) {
            ex.toString();
            return "";
        }
    }

    private static double truncate(double x) {
        long y = (long) (x * 100.0d);
        return y / 100.0d;
    }

    public static String obtenerBrowserLanguaje() {
        try {
            String ln = Locale.getDefault().getLanguage();
            return ln;
        } catch (Exception e) {
            return "";
        }
    }

    public static String obtenerTimeZone() {
        try {
            TimeZone timez = TimeZone.getDefault();
            String tz = TimeZone.getTimeZone(timez.getID()).getDisplayName(false, 0);
            return tz;
        } catch (Exception e) {
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Void doInBackground(Bundle... arg0) {
        sendTagNI(arg0[0], this.a);
        return null;
    }
}
