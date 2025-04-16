package de.bnjmnrhl.fundament.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import de.bnjmnrhl.fundament.App;
import de.bnjmnrhl.fundament.Globals;
import java.io.IOException;
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
import org.apache.http.conn.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class ImageRequestService extends PriorityIntentService {
    public static final String DATA_URL = "url";
    private static final int DEFAULT_CONNECTTIMEOUT = 15000;
    private static final int DEFAULT_READTIMEOUT = 10000;
    public static final String EXTRA_CONNECTTIMEOUT = "connectTimeout";
    public static final String EXTRA_MESSENGER = "messenger";
    public static final String EXTRA_READTIMEOUT = "readTimeout";
    public static final String EXTRA_SELFSIGNEDCERTIFICATE = "sslSelfsigned";
    public static final String EXTRA_URL = "url";
    public static final int WHAT_EXCEPTION = 222;
    public static final int WHAT_SUCCESS = 111;

    public ImageRequestService() {
        super("RequestService");
    }

    @Override // de.bnjmnrhl.fundament.service.PriorityIntentService
    protected void onHandleIntent(Intent intent) {
        Bitmap bitmap;
        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger) extras.getParcelable("messenger");
        String url = extras.getString("url");
        if (intent.hasExtra("sslSelfsigned")) {
            trustAllHosts();
            trustAllCertificates();
        }
        Message msg = Message.obtain();
        try {
            Globals.error("-------------------------\n" + url);
            bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
        } catch (NullPointerException e) {
            msg.what = 222;
            Globals.error((Exception) e);
        } catch (MalformedURLException e2) {
            msg.what = 222;
            Globals.log("malformed url: " + url);
            Globals.error((Exception) e2);
        } catch (IOException e3) {
            msg.what = 222;
            Globals.error((Exception) e3);
        } catch (Exception e4) {
            msg.what = 222;
            Globals.error(e4);
        }
        if (bitmap == null) {
            throw new NullPointerException();
        }
        Bundle b = new Bundle(1);
        b.putString("url", url);
        msg.what = 111;
        msg.setData(b);
        msg.obj = bitmap;
        try {
            messenger.send(msg);
        } catch (RemoteException e5) {
            Globals.error((Exception) e5);
        }
    }

    private static void trustAllHosts() {
        HostnameVerifier myHostnameVerifier = new HostnameVerifier() { // from class: de.bnjmnrhl.fundament.service.ImageRequestService.1
            @Override // javax.net.ssl.HostnameVerifier
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
    }

    private static void trustAllCertificates() {
        TrustManager[] myTrustManagers = {new X509TrustManager() { // from class: de.bnjmnrhl.fundament.service.ImageRequestService.2
            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext context = SSLContext.getInstance(SSLSocketFactory.TLS);
            context.init(null, myTrustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (KeyManagementException e) {
            if (App.DEBUG) {
                Globals.error((Exception) e);
            }
        } catch (NoSuchAlgorithmException e2) {
            if (App.DEBUG) {
                Globals.error((Exception) e2);
            }
        }
    }
}
