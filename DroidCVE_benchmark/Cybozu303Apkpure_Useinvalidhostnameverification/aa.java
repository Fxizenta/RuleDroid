package com.cybozu.kunailite.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ReportService.java */
/* loaded from: classes.dex */
public final class aa extends AsyncTask implements HostnameVerifier, X509TrustManager {
    final /* synthetic */ ReportService a;

    private aa(ReportService reportService) {
        this.a = reportService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public /* synthetic */ aa(ReportService reportService, byte b) {
        this(reportService);
    }

    @Override // android.os.AsyncTask
    protected final /* synthetic */ Object doInBackground(Object[] objArr) {
        return a();
    }

    @Override // android.os.AsyncTask
    protected final /* synthetic */ void onPostExecute(Object obj) {
        Boolean bool = (Boolean) obj;
        super.onPostExecute(bool);
        ReportService.d(this.a).b = bool.booleanValue();
        ReportService.d(this.a).c = System.currentTimeMillis();
        ReportService.d(this.a).a(!bool.booleanValue());
        ReportService.d(this.a).b(this.a);
        AlarmManager alarmManager = (AlarmManager) this.a.getSystemService("alarm");
        PendingIntent service = PendingIntent.getService(this.a, 0, new Intent(this.a, (Class<?>) ReportService.class), 134217728);
        if (bool.booleanValue()) {
            alarmManager.cancel(service);
        } else {
            alarmManager.set(2, 3600000L, service);
        }
        ReportService.c(this.a);
        this.a.stopSelf();
    }

    private Boolean a() {
        URL b;
        HttpURLConnection httpURLConnection = null;
        ReportService.a(this.a).acquire();
        try {
            try {
                b = ReportService.b(this.a);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        if (b == null) {
            try {
                ReportService.a(this.a).release();
            } catch (Throwable th2) {
            }
            return false;
        }
        HttpURLConnection httpURLConnection2 = (HttpURLConnection) b.openConnection();
        try {
            if (httpURLConnection2 instanceof HttpsURLConnection) {
                ((HttpsURLConnection) httpURLConnection2).setHostnameVerifier(this);
                SSLContext sSLContext = SSLContext.getInstance("TLS");
                sSLContext.init(null, new TrustManager[]{this}, new SecureRandom());
                ((HttpsURLConnection) httpURLConnection2).setSSLSocketFactory(sSLContext.getSocketFactory());
            }
            httpURLConnection2.setRequestProperty("Connection", Build.VERSION.SDK_INT >= 14 ? "Keep-Alive" : "close");
            httpURLConnection2.connect();
            Boolean valueOf = Boolean.valueOf(httpURLConnection2.getResponseCode() == 200);
            if (httpURLConnection2 != null) {
                try {
                    httpURLConnection2.disconnect();
                } catch (Throwable th3) {
                }
            }
            ReportService.a(this.a).release();
            return valueOf;
        } catch (Exception e2) {
            httpURLConnection = httpURLConnection2;
            e = e2;
            e.printStackTrace();
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Throwable th4) {
                }
            }
            ReportService.a(this.a).release();
            return false;
        } catch (Throwable th5) {
            httpURLConnection = httpURLConnection2;
            th = th5;
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Throwable th6) {
                    throw th;
                }
            }
            ReportService.a(this.a).release();
            throw th;
        }
    }

    @Override // android.os.AsyncTask
    protected final void onCancelled() {
        super.onCancelled();
        ReportService.c(this.a);
    }

    @Override // javax.net.ssl.X509TrustManager
    public final void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
    }

    @Override // javax.net.ssl.X509TrustManager
    public final void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
    }

    @Override // javax.net.ssl.X509TrustManager
    public final X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override // javax.net.ssl.HostnameVerifier
    public final boolean verify(String str, SSLSession sSLSession) {
        return true;
    }
}
