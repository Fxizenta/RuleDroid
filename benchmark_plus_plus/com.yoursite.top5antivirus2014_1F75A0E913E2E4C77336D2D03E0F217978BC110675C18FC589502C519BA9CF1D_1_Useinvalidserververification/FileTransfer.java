package org.apache.cordova.filetransfer;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import com.qbiki.modules.sharepoint.SPServer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import nl.siegmann.epublib.epub.PackageDocumentBase;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;
import org.apache.cordova.file.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FileTransfer extends CordovaPlugin {
    private static final String BOUNDARY = "+++++";
    private static final String LINE_END = "\r\n";
    private static final String LINE_START = "--";
    private static final String LOG_TAG = "FileTransfer";
    private static final int MAX_BUFFER_SIZE = 16384;
    public static int FILE_NOT_FOUND_ERR = 1;
    public static int INVALID_URL_ERR = 2;
    public static int CONNECTION_ERR = 3;
    public static int ABORTED_ERR = 4;
    private static HashMap<String, RequestContext> activeRequests = new HashMap<>();
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { // from class: org.apache.cordova.filetransfer.FileTransfer.2
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private static final TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: org.apache.cordova.filetransfer.FileTransfer.3
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class RequestContext {
        boolean aborted;
        CallbackContext callbackContext;
        InputStream currentInputStream;
        OutputStream currentOutputStream;
        String source;
        String target;
        File targetFile;

        RequestContext(String source, String target, CallbackContext callbackContext) {
            this.source = source;
            this.target = target;
            this.callbackContext = callbackContext;
        }

        void sendPluginResult(PluginResult pluginResult) {
            synchronized (this) {
                if (!this.aborted) {
                    this.callbackContext.sendPluginResult(pluginResult);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private static abstract class TrackingInputStream extends FilterInputStream {
        public abstract long getTotalRawBytesRead();

        public TrackingInputStream(InputStream in) {
            super(in);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExposedGZIPInputStream extends GZIPInputStream {
        public ExposedGZIPInputStream(InputStream in) throws IOException {
            super(in);
        }

        public Inflater getInflater() {
            return this.inf;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TrackingGZIPInputStream extends TrackingInputStream {
        private ExposedGZIPInputStream gzin;

        public TrackingGZIPInputStream(ExposedGZIPInputStream gzin) throws IOException {
            super(gzin);
            this.gzin = gzin;
        }

        @Override // org.apache.cordova.filetransfer.FileTransfer.TrackingInputStream
        public long getTotalRawBytesRead() {
            return this.gzin.getInflater().getBytesRead();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SimpleTrackingInputStream extends TrackingInputStream {
        private long bytesRead;

        public SimpleTrackingInputStream(InputStream stream) {
            super(stream);
            this.bytesRead = 0L;
        }

        private int updateBytesRead(int newBytesRead) {
            if (newBytesRead != -1) {
                this.bytesRead += newBytesRead;
            }
            return newBytesRead;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            return updateBytesRead(super.read());
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] buffer) throws IOException {
            return updateBytesRead(super.read(buffer));
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bytes, int offset, int count) throws IOException {
            return updateBytesRead(super.read(bytes, offset, count));
        }

        @Override // org.apache.cordova.filetransfer.FileTransfer.TrackingInputStream
        public long getTotalRawBytesRead() {
            return this.bytesRead;
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("upload") || action.equals("download")) {
            String source = args.getString(0);
            String target = args.getString(1);
            if (action.equals("upload")) {
                try {
                    upload(URLDecoder.decode(source, "UTF-8"), target, args, callbackContext);
                    return true;
                } catch (UnsupportedEncodingException e) {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.MALFORMED_URL_EXCEPTION, "UTF-8 error."));
                    return true;
                }
            }
            download(source, target, args, callbackContext);
            return true;
        }
        if (!action.equals("abort")) {
            return false;
        }
        String objectId = args.getString(0);
        abort(objectId);
        callbackContext.success();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addHeadersToRequest(URLConnection connection, JSONObject headers) {
        try {
            Iterator<?> iter = headers.keys();
            while (iter.hasNext()) {
                String headerKey = iter.next().toString();
                JSONArray headerValues = headers.optJSONArray(headerKey);
                if (headerValues == null) {
                    headerValues = new JSONArray();
                    headerValues.put(headers.getString(headerKey));
                }
                connection.setRequestProperty(headerKey, headerValues.getString(0));
                for (int i = 1; i < headerValues.length(); i++) {
                    connection.addRequestProperty(headerKey, headerValues.getString(i));
                }
            }
        } catch (JSONException e) {
        }
    }

    private void upload(final String source, final String target, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, "upload " + source + " to " + target);
        final String fileKey = getArgument(args, 2, SPServer.DT_FILE);
        final String fileName = getArgument(args, 3, "image.jpg");
        final String mimeType = getArgument(args, 4, "image/jpeg");
        final JSONObject params = args.optJSONObject(5) == null ? new JSONObject() : args.optJSONObject(5);
        final boolean trustEveryone = args.optBoolean(6);
        final boolean chunkedMode = args.optBoolean(7) || args.isNull(7);
        final JSONObject headers = args.optJSONObject(8) == null ? params.optJSONObject("headers") : args.optJSONObject(8);
        final String objectId = args.getString(9);
        final String httpMethod = getArgument(args, 10, "POST");
        final CordovaResourceApi resourceApi = this.webView.getResourceApi();
        Log.d(LOG_TAG, "fileKey: " + fileKey);
        Log.d(LOG_TAG, "fileName: " + fileName);
        Log.d(LOG_TAG, "mimeType: " + mimeType);
        Log.d(LOG_TAG, "params: " + params);
        Log.d(LOG_TAG, "trustEveryone: " + trustEveryone);
        Log.d(LOG_TAG, "chunkedMode: " + chunkedMode);
        Log.d(LOG_TAG, "headers: " + headers);
        Log.d(LOG_TAG, "objectId: " + objectId);
        Log.d(LOG_TAG, "httpMethod: " + httpMethod);
        final Uri targetUri = resourceApi.remapUri(Uri.parse(target));
        Uri tmpSrc = Uri.parse(source);
        if (tmpSrc.getScheme() == null) {
            tmpSrc = Uri.fromFile(new File(source));
        }
        final Uri sourceUri = resourceApi.remapUri(tmpSrc);
        int uriType = CordovaResourceApi.getUriType(targetUri);
        final boolean useHttps = uriType == 6;
        if (uriType != 5 && !useHttps) {
            JSONObject error = createFileTransferError(INVALID_URL_ERR, source, target, null, 0);
            Log.e(LOG_TAG, "Unsupported URI: " + targetUri);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error));
        } else {
            final RequestContext context = new RequestContext(source, target, callbackContext);
            synchronized (activeRequests) {
                activeRequests.put(objectId, context);
            }
            this.cordova.getThreadPool().execute(new Runnable() { // from class: org.apache.cordova.filetransfer.FileTransfer.1
                @Override // java.lang.Runnable
                public void run() {
                    if (context.aborted) {
                        return;
                    }
                    HostnameVerifier oldHostnameVerifier = null;
                    SSLSocketFactory oldSocketFactory = null;
                    int fixedLength = -1;
                    try {
                        try {
                            try {
                                try {
                                    FileUploadResult result = new FileUploadResult();
                                    FileProgressResult progress = new FileProgressResult();
                                    HttpURLConnection conn = resourceApi.createHttpConnection(targetUri);
                                    if (useHttps && trustEveryone) {
                                        HttpsURLConnection https = (HttpsURLConnection) conn;
                                        oldSocketFactory = FileTransfer.trustAllHosts(https);
                                        oldHostnameVerifier = https.getHostnameVerifier();
                                        https.setHostnameVerifier(FileTransfer.DO_NOT_VERIFY);
                                    }
                                    conn.setDoInput(true);
                                    conn.setDoOutput(true);
                                    conn.setUseCaches(false);
                                    conn.setRequestMethod(httpMethod);
                                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=+++++");
                                    String cookie = CookieManager.getInstance().getCookie(target);
                                    if (cookie != null) {
                                        conn.setRequestProperty("Cookie", cookie);
                                    }
                                    if (headers != null) {
                                        FileTransfer.addHeadersToRequest(conn, headers);
                                    }
                                    StringBuilder beforeData = new StringBuilder();
                                    try {
                                        Iterator<?> iter = params.keys();
                                        while (iter.hasNext()) {
                                            Object key = iter.next();
                                            if (!String.valueOf(key).equals("headers")) {
                                                beforeData.append(FileTransfer.LINE_START).append(FileTransfer.BOUNDARY).append("\r\n");
                                                beforeData.append("Content-Disposition: form-data; name=\"").append(key.toString()).append('\"');
                                                beforeData.append("\r\n").append("\r\n");
                                                beforeData.append(params.getString(key.toString()));
                                                beforeData.append("\r\n");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.e(FileTransfer.LOG_TAG, e.getMessage(), e);
                                    }
                                    beforeData.append(FileTransfer.LINE_START).append(FileTransfer.BOUNDARY).append("\r\n");
                                    beforeData.append("Content-Disposition: form-data; name=\"").append(fileKey).append("\";");
                                    beforeData.append(" filename=\"").append(fileName).append('\"').append("\r\n");
                                    beforeData.append("Content-Type: ").append(mimeType).append("\r\n").append("\r\n");
                                    byte[] beforeDataBytes = beforeData.toString().getBytes("UTF-8");
                                    byte[] tailParamsBytes = "\r\n--+++++--\r\n".getBytes("UTF-8");
                                    CordovaResourceApi.OpenForReadResult readResult = resourceApi.openForRead(sourceUri);
                                    int stringLength = beforeDataBytes.length + tailParamsBytes.length;
                                    if (readResult.length >= 0) {
                                        fixedLength = ((int) readResult.length) + stringLength;
                                        progress.setLengthComputable(true);
                                        progress.setTotal(fixedLength);
                                    }
                                    Log.d(FileTransfer.LOG_TAG, "Content Length: " + fixedLength);
                                    boolean useChunkedMode = chunkedMode && (Build.VERSION.SDK_INT < 8 || useHttps);
                                    boolean useChunkedMode2 = useChunkedMode || fixedLength == -1;
                                    if (useChunkedMode2) {
                                        conn.setChunkedStreamingMode(16384);
                                        conn.setRequestProperty("Transfer-Encoding", "chunked");
                                    } else {
                                        conn.setFixedLengthStreamingMode(fixedLength);
                                    }
                                    conn.connect();
                                    OutputStream sendStream = null;
                                    try {
                                        sendStream = conn.getOutputStream();
                                        synchronized (context) {
                                            if (context.aborted) {
                                                synchronized (FileTransfer.activeRequests) {
                                                    FileTransfer.activeRequests.remove(objectId);
                                                }
                                                if (conn != null && trustEveryone && useHttps) {
                                                    HttpsURLConnection https2 = (HttpsURLConnection) conn;
                                                    https2.setHostnameVerifier(oldHostnameVerifier);
                                                    https2.setSSLSocketFactory(oldSocketFactory);
                                                }
                                            } else {
                                                context.currentOutputStream = sendStream;
                                                sendStream.write(beforeDataBytes);
                                                int totalBytes = 0 + beforeDataBytes.length;
                                                int bytesAvailable = readResult.inputStream.available();
                                                int bufferSize = Math.min(bytesAvailable, 16384);
                                                byte[] buffer = new byte[bufferSize];
                                                int bytesRead = readResult.inputStream.read(buffer, 0, bufferSize);
                                                long prevBytesRead = 0;
                                                while (bytesRead > 0) {
                                                    result.setBytesSent(totalBytes);
                                                    sendStream.write(buffer, 0, bytesRead);
                                                    totalBytes += bytesRead;
                                                    if (totalBytes > 102400 + prevBytesRead) {
                                                        prevBytesRead = totalBytes;
                                                        Log.d(FileTransfer.LOG_TAG, "Uploaded " + totalBytes + " of " + fixedLength + " bytes");
                                                    }
                                                    int bytesAvailable2 = readResult.inputStream.available();
                                                    bytesRead = readResult.inputStream.read(buffer, 0, Math.min(bytesAvailable2, 16384));
                                                    progress.setLoaded(totalBytes);
                                                    PluginResult progressResult = new PluginResult(PluginResult.Status.OK, progress.toJSONObject());
                                                    progressResult.setKeepCallback(true);
                                                    context.sendPluginResult(progressResult);
                                                }
                                                sendStream.write(tailParamsBytes);
                                                int totalBytes2 = totalBytes + tailParamsBytes.length;
                                                sendStream.flush();
                                                FileTransfer.safeClose(readResult.inputStream);
                                                FileTransfer.safeClose(sendStream);
                                                context.currentOutputStream = null;
                                                Log.d(FileTransfer.LOG_TAG, "Sent " + totalBytes2 + " of " + fixedLength);
                                                int responseCode = conn.getResponseCode();
                                                Log.d(FileTransfer.LOG_TAG, "response code: " + responseCode);
                                                Log.d(FileTransfer.LOG_TAG, "response headers: " + conn.getHeaderFields());
                                                TrackingInputStream inStream = null;
                                                try {
                                                    inStream = FileTransfer.getInputStream(conn);
                                                    synchronized (context) {
                                                        if (context.aborted) {
                                                            synchronized (FileTransfer.activeRequests) {
                                                                FileTransfer.activeRequests.remove(objectId);
                                                            }
                                                            if (conn != null && trustEveryone && useHttps) {
                                                                HttpsURLConnection https3 = (HttpsURLConnection) conn;
                                                                https3.setHostnameVerifier(oldHostnameVerifier);
                                                                https3.setSSLSocketFactory(oldSocketFactory);
                                                            }
                                                        } else {
                                                            context.currentInputStream = inStream;
                                                            ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(1024, conn.getContentLength()));
                                                            byte[] buffer2 = new byte[1024];
                                                            while (true) {
                                                                int bytesRead2 = inStream.read(buffer2);
                                                                if (bytesRead2 <= 0) {
                                                                    break;
                                                                } else {
                                                                    out.write(buffer2, 0, bytesRead2);
                                                                }
                                                            }
                                                            String responseString = out.toString("UTF-8");
                                                            context.currentInputStream = null;
                                                            FileTransfer.safeClose(inStream);
                                                            Log.d(FileTransfer.LOG_TAG, "got response from server");
                                                            Log.d(FileTransfer.LOG_TAG, responseString.substring(0, Math.min(256, responseString.length())));
                                                            result.setResponseCode(responseCode);
                                                            result.setResponse(responseString);
                                                            context.sendPluginResult(new PluginResult(PluginResult.Status.OK, result.toJSONObject()));
                                                            synchronized (FileTransfer.activeRequests) {
                                                                FileTransfer.activeRequests.remove(objectId);
                                                            }
                                                            if (conn != null && trustEveryone && useHttps) {
                                                                HttpsURLConnection https4 = (HttpsURLConnection) conn;
                                                                https4.setHostnameVerifier(oldHostnameVerifier);
                                                                https4.setSSLSocketFactory(oldSocketFactory);
                                                            }
                                                        }
                                                    }
                                                } finally {
                                                    context.currentInputStream = null;
                                                    FileTransfer.safeClose(inStream);
                                                }
                                            }
                                        }
                                    } finally {
                                        FileTransfer.safeClose(readResult.inputStream);
                                        FileTransfer.safeClose(sendStream);
                                    }
                                } catch (JSONException e2) {
                                    Log.e(FileTransfer.LOG_TAG, e2.getMessage(), e2);
                                    context.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
                                    synchronized (FileTransfer.activeRequests) {
                                        FileTransfer.activeRequests.remove(objectId);
                                        if (0 != 0 && trustEveryone && useHttps) {
                                            HttpsURLConnection https5 = (HttpsURLConnection) null;
                                            https5.setHostnameVerifier(null);
                                            https5.setSSLSocketFactory(null);
                                        }
                                    }
                                }
                            } catch (FileNotFoundException e3) {
                                JSONObject error2 = FileTransfer.createFileTransferError(FileTransfer.FILE_NOT_FOUND_ERR, source, target, null);
                                Log.e(FileTransfer.LOG_TAG, error2.toString(), e3);
                                context.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error2));
                                synchronized (FileTransfer.activeRequests) {
                                    FileTransfer.activeRequests.remove(objectId);
                                    if (0 != 0 && trustEveryone && useHttps) {
                                        HttpsURLConnection https6 = (HttpsURLConnection) null;
                                        https6.setHostnameVerifier(null);
                                        https6.setSSLSocketFactory(null);
                                    }
                                }
                            }
                        } catch (IOException e4) {
                            JSONObject error3 = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, null);
                            Log.e(FileTransfer.LOG_TAG, error3.toString(), e4);
                            Log.e(FileTransfer.LOG_TAG, "Failed after uploading 0 of -1 bytes.");
                            context.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error3));
                            synchronized (FileTransfer.activeRequests) {
                                FileTransfer.activeRequests.remove(objectId);
                                if (0 != 0 && trustEveryone && useHttps) {
                                    HttpsURLConnection https7 = (HttpsURLConnection) null;
                                    https7.setHostnameVerifier(null);
                                    https7.setSSLSocketFactory(null);
                                }
                            }
                        } catch (Throwable t) {
                            JSONObject error4 = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, null);
                            Log.e(FileTransfer.LOG_TAG, error4.toString(), t);
                            context.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error4));
                            synchronized (FileTransfer.activeRequests) {
                                FileTransfer.activeRequests.remove(objectId);
                                if (0 != 0 && trustEveryone && useHttps) {
                                    HttpsURLConnection https8 = (HttpsURLConnection) null;
                                    https8.setHostnameVerifier(null);
                                    https8.setSSLSocketFactory(null);
                                }
                            }
                        }
                    } catch (Throwable th) {
                        synchronized (FileTransfer.activeRequests) {
                            FileTransfer.activeRequests.remove(objectId);
                            if (0 != 0 && trustEveryone && useHttps) {
                                HttpsURLConnection https9 = (HttpsURLConnection) null;
                                https9.setHostnameVerifier(null);
                                https9.setSSLSocketFactory(null);
                            }
                            throw th;
                        }
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void safeClose(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TrackingInputStream getInputStream(URLConnection conn) throws IOException {
        String encoding = conn.getContentEncoding();
        return (encoding == null || !encoding.equalsIgnoreCase("gzip")) ? new SimpleTrackingInputStream(conn.getInputStream()) : new TrackingGZIPInputStream(new ExposedGZIPInputStream(conn.getInputStream()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return oldFactory;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static JSONObject createFileTransferError(int errorCode, String source, String target, URLConnection connection) {
        int httpStatus = 0;
        StringBuilder bodyBuilder = new StringBuilder();
        String body = null;
        if (connection != null) {
            try {
                if (connection instanceof HttpURLConnection) {
                    httpStatus = ((HttpURLConnection) connection).getResponseCode();
                    InputStream err = ((HttpURLConnection) connection).getErrorStream();
                    if (err != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(err, "UTF-8"));
                        String line = reader.readLine();
                        while (line != null) {
                            bodyBuilder.append(line);
                            line = reader.readLine();
                            if (line != null) {
                                bodyBuilder.append('\n');
                            }
                        }
                        body = bodyBuilder.toString();
                    }
                }
            } catch (Throwable e) {
                Log.w(LOG_TAG, "Error getting HTTP status code from connection.", e);
            }
        }
        return createFileTransferError(errorCode, source, target, body, Integer.valueOf(httpStatus));
    }

    private static JSONObject createFileTransferError(int errorCode, String source, String target, String body, Integer httpStatus) {
        JSONObject error;
        JSONObject error2 = null;
        try {
            error = new JSONObject();
        } catch (JSONException e) {
            e = e;
        }
        try {
            error.put("code", errorCode);
            error.put(PackageDocumentBase.DCTags.source, source);
            error.put("target", target);
            if (body != null) {
                error.put("body", body);
            }
            if (httpStatus != null) {
                error.put("http_status", httpStatus);
            }
            return error;
        } catch (JSONException e2) {
            e = e2;
            error2 = error;
            Log.e(LOG_TAG, e.getMessage(), e);
            return error2;
        }
    }

    private static String getArgument(JSONArray args, int position, String defaultString) {
        if (args.length() <= position) {
            return defaultString;
        }
        String arg = args.optString(position);
        if (arg == null || "null".equals(arg)) {
            return defaultString;
        }
        return arg;
    }

    private void download(final String source, final String target, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, "download " + source + " to " + target);
        final CordovaResourceApi resourceApi = this.webView.getResourceApi();
        final boolean trustEveryone = args.optBoolean(2);
        final String objectId = args.getString(3);
        final JSONObject headers = args.optJSONObject(4);
        final Uri sourceUri = resourceApi.remapUri(Uri.parse(source));
        Uri tmpTarget = Uri.parse(target);
        if (tmpTarget.getScheme() == null) {
            tmpTarget = Uri.fromFile(new File(target));
        }
        final Uri targetUri = resourceApi.remapUri(tmpTarget);
        int uriType = CordovaResourceApi.getUriType(sourceUri);
        final boolean useHttps = uriType == 6;
        final boolean isLocalTransfer = (useHttps || uriType == 5) ? false : true;
        if (uriType == -1) {
            JSONObject error = createFileTransferError(INVALID_URL_ERR, source, target, null, 0);
            Log.e(LOG_TAG, "Unsupported URI: " + targetUri);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error));
        } else if (!isLocalTransfer && !Config.isUrlWhiteListed(source)) {
            Log.w(LOG_TAG, "Source URL is not in white list: '" + source + "'");
            JSONObject error2 = createFileTransferError(CONNECTION_ERR, source, target, null, 401);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error2));
        } else {
            final RequestContext context = new RequestContext(source, target, callbackContext);
            synchronized (activeRequests) {
                activeRequests.put(objectId, context);
            }
            this.cordova.getThreadPool().execute(new Runnable() { // from class: org.apache.cordova.filetransfer.FileTransfer.4
                @Override // java.lang.Runnable
                public void run() {
                    TrackingInputStream inputStream;
                    if (context.aborted) {
                        return;
                    }
                    HttpURLConnection connection = null;
                    HostnameVerifier oldHostnameVerifier = null;
                    SSLSocketFactory oldSocketFactory = null;
                    File file = null;
                    OutputStream outputStream = null;
                    try {
                        try {
                            try {
                                try {
                                    try {
                                        outputStream = resourceApi.openOutputStream(targetUri);
                                        file = resourceApi.mapUriToFile(targetUri);
                                        context.targetFile = file;
                                        Log.d(FileTransfer.LOG_TAG, "Download file:" + sourceUri);
                                        FileProgressResult progress = new FileProgressResult();
                                        if (isLocalTransfer) {
                                            CordovaResourceApi.OpenForReadResult readResult = resourceApi.openForRead(sourceUri);
                                            if (readResult.length != -1) {
                                                progress.setLengthComputable(true);
                                                progress.setTotal(readResult.length);
                                            }
                                            TrackingInputStream inputStream2 = new SimpleTrackingInputStream(readResult.inputStream);
                                            inputStream = inputStream2;
                                        } else {
                                            connection = resourceApi.createHttpConnection(sourceUri);
                                            if (useHttps && trustEveryone) {
                                                HttpsURLConnection https = (HttpsURLConnection) connection;
                                                oldSocketFactory = FileTransfer.trustAllHosts(https);
                                                oldHostnameVerifier = https.getHostnameVerifier();
                                                https.setHostnameVerifier(FileTransfer.DO_NOT_VERIFY);
                                            }
                                            connection.setRequestMethod("GET");
                                            String cookie = CookieManager.getInstance().getCookie(sourceUri.toString());
                                            if (cookie != null) {
                                                connection.setRequestProperty("cookie", cookie);
                                            }
                                            connection.setRequestProperty("Accept-Encoding", "gzip");
                                            if (headers != null) {
                                                FileTransfer.addHeadersToRequest(connection, headers);
                                            }
                                            connection.connect();
                                            if (connection.getContentEncoding() == null || connection.getContentEncoding().equalsIgnoreCase("gzip")) {
                                                progress.setLengthComputable(true);
                                                progress.setTotal(connection.getContentLength());
                                            }
                                            inputStream = FileTransfer.getInputStream(connection);
                                        }
                                        try {
                                            synchronized (context) {
                                                if (context.aborted) {
                                                    context.currentInputStream = null;
                                                    FileTransfer.safeClose(inputStream);
                                                    FileTransfer.safeClose(outputStream);
                                                    FileTransfer.safeClose(outputStream);
                                                    synchronized (FileTransfer.activeRequests) {
                                                        FileTransfer.activeRequests.remove(objectId);
                                                    }
                                                    if (connection != null && trustEveryone && useHttps) {
                                                        HttpsURLConnection https2 = (HttpsURLConnection) connection;
                                                        https2.setHostnameVerifier(oldHostnameVerifier);
                                                        https2.setSSLSocketFactory(oldSocketFactory);
                                                    }
                                                    result = 0 == 0 ? new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection)) : null;
                                                    if (result.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                                        file.delete();
                                                    }
                                                    context.sendPluginResult(result);
                                                } else {
                                                    context.currentInputStream = inputStream;
                                                    byte[] buffer = new byte[16384];
                                                    while (true) {
                                                        int bytesRead = inputStream.read(buffer);
                                                        if (bytesRead <= 0) {
                                                            break;
                                                        }
                                                        outputStream.write(buffer, 0, bytesRead);
                                                        progress.setLoaded(inputStream.getTotalRawBytesRead());
                                                        PluginResult progressResult = new PluginResult(PluginResult.Status.OK, progress.toJSONObject());
                                                        progressResult.setKeepCallback(true);
                                                        context.sendPluginResult(progressResult);
                                                    }
                                                    context.currentInputStream = null;
                                                    FileTransfer.safeClose(inputStream);
                                                    FileTransfer.safeClose(outputStream);
                                                    Log.d(FileTransfer.LOG_TAG, "Saved file: " + target);
                                                    JSONObject fileEntry = FileUtils.getEntry(file);
                                                    PluginResult result = new PluginResult(PluginResult.Status.OK, fileEntry);
                                                    FileTransfer.safeClose(outputStream);
                                                    synchronized (FileTransfer.activeRequests) {
                                                        FileTransfer.activeRequests.remove(objectId);
                                                    }
                                                    if (connection != null && trustEveryone && useHttps) {
                                                        HttpsURLConnection https3 = (HttpsURLConnection) connection;
                                                        https3.setHostnameVerifier(oldHostnameVerifier);
                                                        https3.setSSLSocketFactory(oldSocketFactory);
                                                    }
                                                    result = result == null ? new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection)) : result;
                                                    if (result.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                                        file.delete();
                                                    }
                                                    context.sendPluginResult(result);
                                                }
                                            }
                                        } catch (Throwable th) {
                                            context.currentInputStream = null;
                                            FileTransfer.safeClose(inputStream);
                                            FileTransfer.safeClose(outputStream);
                                            throw th;
                                        }
                                    } catch (JSONException e) {
                                        Log.e(FileTransfer.LOG_TAG, e.getMessage(), e);
                                        PluginResult result2 = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
                                        FileTransfer.safeClose(outputStream);
                                        synchronized (FileTransfer.activeRequests) {
                                            FileTransfer.activeRequests.remove(objectId);
                                            if (connection != null && trustEveryone && useHttps) {
                                                HttpsURLConnection https4 = (HttpsURLConnection) connection;
                                                https4.setHostnameVerifier(oldHostnameVerifier);
                                                https4.setSSLSocketFactory(oldSocketFactory);
                                            }
                                            PluginResult result3 = result2 == null ? new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection)) : result2;
                                            if (result3.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                                file.delete();
                                            }
                                            context.sendPluginResult(result3);
                                        }
                                    }
                                } catch (IOException e2) {
                                    JSONObject error3 = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection);
                                    Log.e(FileTransfer.LOG_TAG, error3.toString(), e2);
                                    PluginResult result4 = new PluginResult(PluginResult.Status.IO_EXCEPTION, error3);
                                    FileTransfer.safeClose(outputStream);
                                    synchronized (FileTransfer.activeRequests) {
                                        FileTransfer.activeRequests.remove(objectId);
                                        if (connection != null && trustEveryone && useHttps) {
                                            HttpsURLConnection https5 = (HttpsURLConnection) connection;
                                            https5.setHostnameVerifier(oldHostnameVerifier);
                                            https5.setSSLSocketFactory(oldSocketFactory);
                                        }
                                        PluginResult result5 = result4 == null ? new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection)) : result4;
                                        if (result5.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                            file.delete();
                                        }
                                        context.sendPluginResult(result5);
                                    }
                                }
                            } catch (FileNotFoundException e3) {
                                JSONObject error4 = FileTransfer.createFileTransferError(FileTransfer.FILE_NOT_FOUND_ERR, source, target, connection);
                                Log.e(FileTransfer.LOG_TAG, error4.toString(), e3);
                                PluginResult result6 = new PluginResult(PluginResult.Status.IO_EXCEPTION, error4);
                                FileTransfer.safeClose(outputStream);
                                synchronized (FileTransfer.activeRequests) {
                                    FileTransfer.activeRequests.remove(objectId);
                                    if (connection != null && trustEveryone && useHttps) {
                                        HttpsURLConnection https6 = (HttpsURLConnection) connection;
                                        https6.setHostnameVerifier(oldHostnameVerifier);
                                        https6.setSSLSocketFactory(oldSocketFactory);
                                    }
                                    PluginResult result7 = result6 == null ? new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection)) : result6;
                                    if (result7.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                        file.delete();
                                    }
                                    context.sendPluginResult(result7);
                                }
                            }
                        } catch (Throwable e4) {
                            JSONObject error5 = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection);
                            Log.e(FileTransfer.LOG_TAG, error5.toString(), e4);
                            PluginResult result8 = new PluginResult(PluginResult.Status.IO_EXCEPTION, error5);
                            FileTransfer.safeClose(outputStream);
                            synchronized (FileTransfer.activeRequests) {
                                FileTransfer.activeRequests.remove(objectId);
                                if (connection != null && trustEveryone && useHttps) {
                                    HttpsURLConnection https7 = (HttpsURLConnection) connection;
                                    https7.setHostnameVerifier(oldHostnameVerifier);
                                    https7.setSSLSocketFactory(oldSocketFactory);
                                }
                                PluginResult result9 = result8 == null ? new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection)) : result8;
                                if (result9.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                    file.delete();
                                }
                                context.sendPluginResult(result9);
                            }
                        }
                    } catch (Throwable th2) {
                        FileTransfer.safeClose(outputStream);
                        synchronized (FileTransfer.activeRequests) {
                            FileTransfer.activeRequests.remove(objectId);
                            if (connection != null && trustEveryone && useHttps) {
                                HttpsURLConnection https8 = (HttpsURLConnection) connection;
                                https8.setHostnameVerifier(oldHostnameVerifier);
                                https8.setSSLSocketFactory(oldSocketFactory);
                            }
                            if (result == null) {
                                result = new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, source, target, connection));
                            }
                            if (result.getStatus() != PluginResult.Status.OK.ordinal() && file != null) {
                                file.delete();
                            }
                            context.sendPluginResult(result);
                            throw th2;
                        }
                    }
                }
            });
        }
    }

    private void abort(String objectId) {
        final RequestContext context;
        synchronized (activeRequests) {
            context = activeRequests.remove(objectId);
        }
        if (context != null) {
            File file = context.targetFile;
            if (file != null) {
                file.delete();
            }
            JSONObject error = createFileTransferError(ABORTED_ERR, context.source, context.target, null, -1);
            synchronized (context) {
                context.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, error));
                context.aborted = true;
            }
            this.cordova.getThreadPool().execute(new Runnable() { // from class: org.apache.cordova.filetransfer.FileTransfer.5
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (context) {
                        FileTransfer.safeClose(context.currentInputStream);
                        FileTransfer.safeClose(context.currentOutputStream);
                    }
                }
            });
        }
    }
}
