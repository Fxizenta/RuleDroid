package com.seawolftech.globaltalk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

/* loaded from: classes.dex */
public class NetworkManager {
    static final String TAG = "NetworkManager";
    Context mContext;
    private int connectTimeout = 30000;
    private int readTimeout = 30000;
    Proxy mProxy = null;

    public NetworkManager(Context context) {
        this.mContext = context;
        setDefaultHostnameVerifier();
    }

    private void detectProxy() {
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.getType() == 0) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null) {
                InetSocketAddress sa = new InetSocketAddress(proxyHost, port);
                this.mProxy = new Proxy(Proxy.Type.HTTP, sa);
            }
        }
    }

    private void setDefaultHostnameVerifier() {
        HostnameVerifier hv = new HostnameVerifier() { // from class: com.seawolftech.globaltalk.NetworkManager.1
            @Override // javax.net.ssl.HostnameVerifier
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public String SendAndWaitResponse(String strReqData, String strUrl) {
        detectProxy();
        String strResponse = null;
        ArrayList<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("requestData", strReqData));
        HttpURLConnection httpConnect = null;
        try {
            UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
            URL url = new URL(strUrl);
            if (this.mProxy != null) {
                httpConnect = (HttpURLConnection) url.openConnection(this.mProxy);
            } else {
                httpConnect = (HttpURLConnection) url.openConnection();
            }
            httpConnect.setConnectTimeout(this.connectTimeout);
            httpConnect.setReadTimeout(this.readTimeout);
            httpConnect.setDoOutput(true);
            httpConnect.addRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            httpConnect.connect();
            OutputStream os = httpConnect.getOutputStream();
            p_entity.writeTo(os);
            os.flush();
            InputStream content = httpConnect.getInputStream();
            strResponse = BaseHelper.convertStreamToString(content);
            BaseHelper.log(TAG, "response " + strResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpConnect.disconnect();
        }
        return strResponse;
    }

    public boolean urlDownloadToFile(Context context, String strurl, String path) {
        HttpURLConnection conn;
        boolean bRet = false;
        detectProxy();
        try {
            URL url = new URL(strurl);
            if (this.mProxy != null) {
                conn = (HttpURLConnection) url.openConnection(this.mProxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            while (true) {
                int i = is.read(temp);
                if (i > 0) {
                    fos.write(temp, 0, i);
                } else {
                    fos.close();
                    is.close();
                    bRet = true;
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bRet;
        }
    }
}
