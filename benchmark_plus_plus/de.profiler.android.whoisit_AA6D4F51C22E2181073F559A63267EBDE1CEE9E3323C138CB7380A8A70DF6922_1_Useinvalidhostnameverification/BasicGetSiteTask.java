package de.profiler.android.whoisit;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class BasicGetSiteTask extends AsyncTask<String, Integer, String> {
    public static final int ERROR = 1;
    public static final int HTTPNOTFOUND = 2;
    public static final int OK = 0;
    public HashMap<String, String> HTTPparams;
    public int error;
    public String urlreq;
    boolean post = false;
    String postParameters = "";
    public boolean disableHTTPSHostnameVerification = false;
    public boolean disableHTTPSCertVerification = false;
    public String result = "";
    public String encoding = "UTF-8";

    public void setupURL(String param) {
        this.urlreq = param;
    }

    public static String slurp(InputStream is, int bufferSize) {
        return slurp(is, bufferSize, "UTF-8");
    }

    public static String slurp(InputStream is, int bufferSize, String enc) {
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        try {
            Reader in = new InputStreamReader(is, enc);
            while (true) {
                try {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0) {
                        break;
                    }
                    out.append(buffer, 0, rsz);
                } finally {
                    in.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e2) {
        }
        return out.toString();
    }

    public void setupPostData(String postParameters) {
        this.post = true;
        this.postParameters = postParameters;
    }

    public void setupPostData(ArrayList<String> keys, ArrayList<String> values) {
        this.post = true;
        for (int i = 0; i < keys.size(); i++) {
            if (i != 0) {
                this.postParameters = String.valueOf(this.postParameters) + "&";
            }
            try {
                this.postParameters = String.valueOf(this.postParameters) + URLEncoder.encode(keys.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                this.postParameters = "";
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public String doInBackground(String... arg0) {
        setupURL(arg0[0]);
        this.error = 0;
        MyDebug.waitdebugger();
        HttpURLConnection urlConnection = null;
        publishProgress(0);
        try {
            try {
                URL url = new URL(this.urlreq);
                urlConnection = (HttpURLConnection) url.openConnection();
                if ((urlConnection instanceof HttpsURLConnection) && this.disableHTTPSHostnameVerification) {
                    ((HttpsURLConnection) urlConnection).setHostnameVerifier(new HostnameVerifier() { // from class: de.profiler.android.whoisit.BasicGetSiteTask.1
                        @Override // javax.net.ssl.HostnameVerifier
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });
                }
                if ((urlConnection instanceof HttpsURLConnection) && this.disableHTTPSCertVerification) {
                    TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: de.profiler.android.whoisit.BasicGetSiteTask.2
                        @Override // javax.net.ssl.X509TrustManager
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }
                    }};
                    try {
                        SSLContext sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustAllCerts, new SecureRandom());
                        ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                publishProgress(1);
                urlConnection.setRequestProperty("User-Agent", "");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("User-Agent", "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.16");
                if (this.HTTPparams != null) {
                    for (String name : this.HTTPparams.keySet()) {
                        urlConnection.setRequestProperty(name, this.HTTPparams.get(name));
                    }
                }
                if (this.post) {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Content-Length", Integer.toString(this.postParameters.getBytes().length));
                    urlConnection.setUseCaches(false);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    wr.writeBytes(this.postParameters);
                    wr.flush();
                    wr.close();
                }
                publishProgress(2);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                this.result = slurp(in, 1024, this.encoding);
                publishProgress(3);
                if (this.result.indexOf("java.net.UnknownHostException") > -1) {
                    this.error = 1;
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                publishProgress(4);
                onReceived();
                return this.result;
            } catch (Exception e2) {
                e2.printStackTrace();
                if (e2 instanceof FileNotFoundException) {
                    this.error = 2;
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    return "";
                }
                this.error = 1;
                String exc = e2.toString();
                if (urlConnection == null) {
                    return exc;
                }
                urlConnection.disconnect();
                return exc;
            }
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onProgressUpdate(Integer... progress) {
    }

    protected void onReceived() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(String result) {
    }
}
