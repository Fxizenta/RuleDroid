package com.netqin.android.nqhttp;

import android.content.Context;
import android.net.Proxy;
import android.text.TextUtils;
import java.io.File;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;

/* loaded from: classes.dex */
public class NqHttp {
    private Context a;
    private e b;
    private String c = "application/octet-stream";
    private String d = null;
    private String e = null;
    private int f = 15000;
    private int g = 45000;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ResponseCodeException extends Exception {
        private static final long serialVersionUID = -4070450929355933491L;
        private int mResponseCode;

        public ResponseCodeException(int i) {
            this.mResponseCode = 200;
            this.mResponseCode = i;
        }

        public int getResponseCode() {
            return this.mResponseCode;
        }
    }

    public NqHttp(Context context, e eVar) {
        this.a = context;
        this.b = eVar;
    }

    private void a(HttpResponse httpResponse) {
        com.netqin.antivirus.util.a.b("NQHTTP", "recvResponse()");
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        com.netqin.antivirus.util.a.b("NQHTTP", "responseCode is " + statusCode);
        if (this.b.a()) {
            throw new ResponseCodeException(3);
        }
        if (statusCode < 200 || statusCode > 206) {
            throw new ResponseCodeException(statusCode);
        }
        InputStream content = httpResponse.getEntity().getContent();
        if (this.b.a()) {
            throw new ResponseCodeException(3);
        }
        this.b.b(httpResponse.getEntity().getContentLength());
        byte[] bArr = new byte[2048];
        com.netqin.antivirus.util.a.b("NQHTTP", "recvResponse() ongoing...");
        while (true) {
            int read = content.read(bArr, 0, 2048);
            if (read == -1) {
                com.netqin.antivirus.util.a.b("NQHTTP", "recvResponse() ending...");
                content.close();
                return;
            } else {
                if (this.b.a()) {
                    content.close();
                    throw new ResponseCodeException(3);
                }
                try {
                    this.b.a(bArr, read);
                } catch (Exception e) {
                    content.close();
                    throw e;
                }
            }
        }
    }

    private boolean a(String str, int i, long j) {
        File file;
        return i >= 0 && j > 0 && (file = new File(str)) != null && file.exists() && ((long) i) + j <= ((long) ((int) file.length()));
    }

    public int a(String str, String str2, int i, long j) {
        try {
            if (!a(str2, i, j)) {
                throw new Exception("NqHttpWrongArgument");
            }
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            c cVar = new c(this, keyStore);
            cVar.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            BasicHttpParams basicHttpParams = new BasicHttpParams();
            HttpProtocolParams.setVersion(basicHttpParams, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(basicHttpParams, "UTF-8");
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", cVar, 443));
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry), basicHttpParams);
            String defaultHost = Proxy.getDefaultHost();
            com.netqin.antivirus.util.a.a("NqHttp", "proxy host=" + defaultHost);
            if (defaultHost != null) {
                defaultHttpClient.getParams().setParameter("http.route.default-proxy", new HttpHost(defaultHost, Proxy.getPort(this.a)));
            }
            HttpPost httpPost = new HttpPost(str);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Connection", "close");
            if (!TextUtils.isEmpty(this.d)) {
                httpPost.setHeader("Range", this.d);
                this.d = null;
            }
            if (!TextUtils.isEmpty(this.e)) {
                httpPost.setHeader("Referer", this.e);
                this.e = null;
            }
            httpPost.getParams().setParameter("http.connection.timeout", Integer.valueOf(this.f));
            httpPost.getParams().setParameter("http.socket.timeout", Integer.valueOf(this.g));
            httpPost.setEntity(new a(this, new File(str2), this.c));
            a(defaultHttpClient.execute(httpPost));
            return 0;
        } catch (Exception e) {
            if ((e instanceof SocketTimeoutException) || (e instanceof ConnectTimeoutException)) {
                return 2;
            }
            if (e instanceof ResponseCodeException) {
                return ((ResponseCodeException) e).getResponseCode();
            }
            return 1;
        }
    }

    public int a(String str, byte[] bArr) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            c cVar = new c(this, keyStore);
            cVar.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            BasicHttpParams basicHttpParams = new BasicHttpParams();
            HttpProtocolParams.setVersion(basicHttpParams, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(basicHttpParams, "UTF-8");
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", cVar, 443));
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry), basicHttpParams);
            String defaultHost = Proxy.getDefaultHost();
            com.netqin.antivirus.util.a.a("NqHttp", "proxy host=" + defaultHost);
            if (defaultHost != null) {
                defaultHttpClient.getParams().setParameter("http.route.default-proxy", new HttpHost(defaultHost, Proxy.getPort(this.a)));
            }
            HttpPost httpPost = new HttpPost(str);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Connection", "close");
            if (!TextUtils.isEmpty(this.d)) {
                httpPost.setHeader("Range", this.d);
                this.d = null;
            }
            if (!TextUtils.isEmpty(this.e)) {
                httpPost.setHeader("Referer", this.e);
                this.e = null;
            }
            httpPost.getParams().setParameter("http.connection.timeout", Integer.valueOf(this.f));
            httpPost.getParams().setParameter("http.socket.timeout", Integer.valueOf(this.g));
            httpPost.setEntity(new ByteArrayEntity(bArr));
            a(defaultHttpClient.execute(httpPost));
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            if ((e instanceof SocketTimeoutException) || (e instanceof ConnectTimeoutException)) {
                com.netqin.antivirus.util.a.a("ccc", "超时" + str);
                return 2;
            }
            if (e instanceof ResponseCodeException) {
                return ((ResponseCodeException) e).getResponseCode();
            }
            return 1;
        }
    }

    public void a(int i, int i2) {
        if (i > 0) {
            this.f = i;
        }
        if (i2 > 0) {
            this.g = i2;
        }
    }

    public void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.c = str;
    }

    public void b(String str) {
        this.d = str;
    }

    public void c(String str) {
        this.e = str;
    }
}
