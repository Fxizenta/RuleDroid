package de.bnjmnrhl.fundament.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import de.bnjmnrhl.fundament.App;
import de.bnjmnrhl.fundament.Globals;
import java.io.IOException;
import java.io.OutputStream;
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
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class RequestService extends PriorityIntentService {
    public static final String DATA_URL = "url";
    private static final int DEFAULT_CONNECTTIMEOUT = 15000;
    private static final int DEFAULT_READTIMEOUT = 10000;
    public static final String EXTRA_ACCEPT = "accept";
    public static final String EXTRA_CONNECTTIMEOUT = "connectTimeout";
    public static final String EXTRA_MESSENGER = "messenger";
    public static final String EXTRA_POSTPARAMSSTRING = "postParamsString";
    public static final String EXTRA_READTIMEOUT = "readTimeout";
    public static final String EXTRA_RESPONSE = "response";
    public static final String EXTRA_SELFSIGNEDCERTIFICATE = "sslSelfsigned";
    public static final String EXTRA_URL = "url";
    public static final int WHAT_EXCEPTION = 222;
    public static final int WHAT_SUCCESS = 111;

    public RequestService() {
        super("RequestService");
    }

    @Override // de.bnjmnrhl.fundament.service.PriorityIntentService
    protected void onHandleIntent(Intent intent) {
        HttpURLConnection connection;
        int responseCode;
        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger) extras.getParcelable("messenger");
        Message msg = Message.obtain();
        msg.setData(extras);
        String url = extras.getString("url");
        if (intent.hasExtra("sslSelfsigned")) {
            trustAllHosts();
            trustAllCertificates();
        }
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(intent.getIntExtra("readTimeout", 10000));
            connection.setConnectTimeout(intent.getIntExtra("connectTimeout", DEFAULT_CONNECTTIMEOUT));
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            if (extras.containsKey(EXTRA_POSTPARAMSSTRING)) {
                connection.setRequestMethod(HttpPost.METHOD_NAME);
                connection.setDoOutput(true);
            } else {
                connection.setRequestMethod(HttpGet.METHOD_NAME);
            }
            if (extras.containsKey(EXTRA_ACCEPT)) {
                connection.addRequestProperty(EXTRA_ACCEPT, extras.getString(EXTRA_ACCEPT));
            }
            connection.setDoInput(true);
            connection.connect();
            if (App.DEBUG) {
                Globals.error("-------------------------\n" + url);
            }
            if (extras.containsKey(EXTRA_POSTPARAMSSTRING)) {
                if (App.DEBUG) {
                    Globals.error(extras.getString(EXTRA_POSTPARAMSSTRING));
                }
                OutputStream out = connection.getOutputStream();
                out.write(extras.getString(EXTRA_POSTPARAMSSTRING).getBytes());
                out.flush();
            }
            responseCode = connection.getResponseCode();
        } catch (MalformedURLException e) {
            msg.what = 222;
            Globals.log("malformed url: " + url);
            Globals.error((Exception) e);
        } catch (IOException e2) {
            msg.what = 222;
            Globals.error((Exception) e2);
        } catch (NullPointerException e3) {
            msg.what = 222;
            Globals.error((Exception) e3);
        } catch (Exception e4) {
            msg.what = 222;
            Globals.error(e4);
        }
        if (responseCode == 200) {
            byte[] responseBytes = IOUtils.toByteArray(connection.getInputStream());
            if (App.DEBUG) {
                Globals.log("request finished, code: " + connection.getResponseCode());
            }
            msg.what = 111;
            msg.arg1 = responseCode;
            msg.obj = responseBytes;
            try {
                messenger.send(msg);
                return;
            } catch (RemoteException e5) {
                Globals.error((Exception) e5);
                return;
            }
        }
        byte[] responseBytes2 = IOUtils.toByteArray(connection.getErrorStream());
        if (App.DEBUG) {
            Globals.error("responsecode: " + connection.getResponseCode() + ", message: " + new String(responseBytes2));
        }
        msg.arg1 = responseCode;
        throw new NullPointerException();
    }

    private static void trustAllHosts() {
        HostnameVerifier myHostnameVerifier = new HostnameVerifier() { // from class: de.bnjmnrhl.fundament.service.RequestService.1
            @Override // javax.net.ssl.HostnameVerifier
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
    }

    private static void trustAllCertificates() {
        TrustManager[] myTrustManagers = {new X509TrustManager() { // from class: de.bnjmnrhl.fundament.service.RequestService.2
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
