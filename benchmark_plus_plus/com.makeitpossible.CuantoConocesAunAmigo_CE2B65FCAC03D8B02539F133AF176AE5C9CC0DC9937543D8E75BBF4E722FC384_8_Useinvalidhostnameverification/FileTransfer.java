package com.phonegap;

import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FileTransfer extends Plugin {
    private static final String BOUNDRY = "*****";
    private static final String LINE_END = "\r\n";
    private static final String LINE_START = "--";
    private static final String LOG_TAG = "FileUploader";
    public static int FILE_NOT_FOUND_ERR = 1;
    public static int INVALID_URL_ERR = 2;
    public static int CONNECTION_ERR = 3;
    static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { // from class: com.phonegap.FileTransfer.1
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private SSLSocketFactory defaultSSLSocketFactory = null;
    private HostnameVerifier defaultHostnameVerifier = null;

    @Override // com.phonegap.api.Plugin, com.phonegap.api.IPlugin
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult pluginResult;
        try {
            String source = args.getString(0);
            String target = args.getString(1);
            try {
                if (action.equals("upload")) {
                    String fileKey = getArgument(args, 2, "file");
                    String fileName = getArgument(args, 3, "image.jpg");
                    String mimeType = getArgument(args, 4, "image/jpeg");
                    JSONObject params = args.optJSONObject(5);
                    boolean trustEveryone = args.optBoolean(6);
                    boolean chunkedMode = args.optBoolean(7);
                    FileUploadResult r = upload(source, target, fileKey, fileName, mimeType, params, trustEveryone, chunkedMode);
                    Log.d(LOG_TAG, "****** About to return a result from upload");
                    pluginResult = new PluginResult(PluginResult.Status.OK, r.toJSONObject());
                } else if (action.equals("download")) {
                    JSONObject r2 = download(source, target);
                    Log.d(LOG_TAG, "****** About to return a result from download");
                    pluginResult = new PluginResult(PluginResult.Status.OK, r2, "window.localFileSystem._castEntry");
                } else {
                    pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
                }
                return pluginResult;
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                JSONObject error = createFileTransferError(FILE_NOT_FOUND_ERR, source, target);
                return new PluginResult(PluginResult.Status.IO_EXCEPTION, error);
            } catch (IOException e2) {
                Log.e(LOG_TAG, e2.getMessage(), e2);
                JSONObject error2 = createFileTransferError(CONNECTION_ERR, source, target);
                return new PluginResult(PluginResult.Status.IO_EXCEPTION, error2);
            } catch (IllegalArgumentException e3) {
                Log.e(LOG_TAG, e3.getMessage(), e3);
                JSONObject error3 = createFileTransferError(INVALID_URL_ERR, source, target);
                return new PluginResult(PluginResult.Status.IO_EXCEPTION, error3);
            } catch (SSLException e4) {
                Log.e(LOG_TAG, e4.getMessage(), e4);
                Log.d(LOG_TAG, "Got my ssl exception!!!");
                JSONObject error4 = createFileTransferError(CONNECTION_ERR, source, target);
                return new PluginResult(PluginResult.Status.IO_EXCEPTION, error4);
            } catch (JSONException e5) {
                Log.e(LOG_TAG, e5.getMessage(), e5);
                return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
            }
        } catch (JSONException e6) {
            Log.d(LOG_TAG, "Missing source or target");
            return new PluginResult(PluginResult.Status.JSON_EXCEPTION, "Missing source or target");
        }
    }

    private void trustAllHosts() {
        TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.phonegap.FileTransfer.2
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
            this.defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private JSONObject createFileTransferError(int errorCode, String source, String target) {
        JSONObject error = null;
        try {
            JSONObject error2 = new JSONObject();
            try {
                error2.put("code", errorCode);
                error2.put("source", source);
                error2.put("target", target);
                return error2;
            } catch (JSONException e) {
                e = e;
                error = error2;
                Log.e(LOG_TAG, e.getMessage(), e);
                return error;
            }
        } catch (JSONException e2) {
            e = e2;
        }
    }

    private String getArgument(JSONArray args, int position, String defaultString) {
        if (args.length() < position) {
            return defaultString;
        }
        String arg = args.optString(position);
        if (arg == null || "null".equals(arg)) {
            return defaultString;
        }
        return arg;
    }

    public FileUploadResult upload(String file, String server, String fileKey, String fileName, String mimeType, JSONObject params, boolean trustEveryone, boolean chunkedMode) throws IOException, SSLException {
        HttpURLConnection conn;
        FileUploadResult result = new FileUploadResult();
        InputStream fileInputStream = getPathFromUri(file);
        URL url = new URL(server);
        if (url.getProtocol().toLowerCase().equals("https")) {
            if (!trustEveryone) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                this.defaultHostnameVerifier = https.getHostnameVerifier();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            }
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
        String cookie = CookieManager.getInstance().getCookie(server);
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }
        if (chunkedMode) {
            conn.setChunkedStreamingMode(8096);
        }
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        try {
            Iterator iter = params.keys();
            while (iter.hasNext()) {
                Object key = iter.next();
                dos.writeBytes("--*****\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"" + key.toString() + "\";");
                dos.writeBytes("\r\n\r\n");
                dos.write(params.getString(key.toString()).getBytes());
                dos.writeBytes(LINE_END);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        dos.writeBytes("--*****\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + fileName + "\"" + LINE_END);
        dos.writeBytes("Content-Type: " + mimeType + LINE_END);
        dos.writeBytes(LINE_END);
        int bytesAvailable = fileInputStream.available();
        int bufferSize = Math.min(bytesAvailable, 8096);
        byte[] buffer = new byte[bufferSize];
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        long totalBytes = 0;
        while (bytesRead > 0) {
            totalBytes += bytesRead;
            result.setBytesSent(totalBytes);
            dos.write(buffer, 0, bufferSize);
            int bytesAvailable2 = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable2, 8096);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        dos.writeBytes(LINE_END);
        dos.writeBytes("--*****--\r\n");
        fileInputStream.close();
        dos.flush();
        dos.close();
        StringBuffer responseString = new StringBuffer("");
        try {
            DataInputStream inStream = new DataInputStream(conn.getInputStream());
            while (true) {
                String line = inStream.readLine();
                if (line == null) {
                    break;
                }
                responseString.append(line);
            }
            Log.d(LOG_TAG, "got response from server");
            Log.d(LOG_TAG, responseString.toString());
            result.setResponseCode(conn.getResponseCode());
            result.setResponse(responseString.toString());
            inStream.close();
            conn.disconnect();
            if (trustEveryone && url.getProtocol().toLowerCase().equals("https")) {
                ((HttpsURLConnection) conn).setHostnameVerifier(this.defaultHostnameVerifier);
                HttpsURLConnection.setDefaultSSLSocketFactory(this.defaultSSLSocketFactory);
            }
            return result;
        } catch (FileNotFoundException e2) {
            throw new IOException("Received error from server");
        }
    }

    public JSONObject download(String source, String target) throws IOException {
        try {
            File file = new File(target);
            file.getParentFile().mkdirs();
            URL url = new URL(source);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            Log.d(LOG_TAG, "Download file:" + url);
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            FileOutputStream outputStream = new FileOutputStream(file);
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                } else {
                    outputStream.close();
                    Log.d(LOG_TAG, "Saved file: " + target);
                    FileUtils fileUtil = new FileUtils();
                    return fileUtil.getEntry(file);
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            throw new IOException("Error while downloading");
        }
    }

    private InputStream getPathFromUri(String path) throws FileNotFoundException {
        if (path.startsWith("content:")) {
            Uri uri = Uri.parse(path);
            return this.ctx.getContentResolver().openInputStream(uri);
        }
        if (path.startsWith("file://")) {
            return new FileInputStream(path.substring(7));
        }
        return new FileInputStream(path);
    }
}
