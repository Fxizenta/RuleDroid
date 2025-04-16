package com.yellowbook.android2.api.request.mid_tier.bv;

import com.yellowbook.android2.api.net.HttpResponse;
import com.yellowbook.android2.api.request.mid_tier.POSTWithMidTier;
import com.yellowbook.android2.api.response.mid_tier.bv.SubmitPhotoResponse;
import com.yellowbook.android2.api.utils.ApiUtils;
import com.yellowbook.android2.api.utils.Logger;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import oauth.signpost.OAuth;
import org.apache.http.entity.mime.MIME;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SubmitPhotoRequest extends POSTWithMidTier<SubmitPhotoResponse> {
    private static final String LINEND = "\r\n";
    private static final String MULTIPART_FROM_DATA = "multipart/form-data";
    private static final String PREFIX = "--";
    private String uri;
    private static final String TAG = SubmitPhotoRequest.class.getName();
    private static final String BOUNDARY = UUID.randomUUID().toString();

    public SubmitPhotoRequest(String uri) {
        this.uri = uri;
    }

    @Override // com.yellowbook.android2.api.CallAPI
    protected String serviceComponent() {
        return "SubmitPhoto";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.yellowbook.android2.api.CallAPI, android.os.AsyncTask
    public HttpResponse doInBackground(Void... params) {
        HttpsURLConnection conn;
        DataOutputStream outStream;
        String response = null;
        int responseCode = 0;
        try {
            URL uri = new URL(getUrl());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() { // from class: com.yellowbook.android2.api.request.mid_tier.bv.SubmitPhotoRequest.1
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
            sslContext.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            conn = (HttpsURLConnection) uri.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setReadTimeout(45000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", OAuth.ENCODING);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            conn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty(MIME.CONTENT_DISPOSITION, "attachment; filename=\"photo\"");
            outStream = new DataOutputStream(conn.getOutputStream());
            File image = new File(this.uri);
            StringBuilder sb1 = new StringBuilder();
            sb1.append("--");
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            sb1.append("Content-Disposition: form-data; name=\"uuid\"\r\n");
            sb1.append(LINEND);
            sb1.append(ApiUtils.getMd5(image));
            sb1.append(LINEND);
            sb1.append("--");
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            sb1.append("Content-Disposition: form-data; name=\"photo\"; filename=\"photo\"\r\n");
            sb1.append(LINEND);
            outStream.write(sb1.toString().getBytes());
            RandomAccessFile is = new RandomAccessFile(image, "r");
            is.seek(0L);
            byte[] buffer = new byte[1024];
            while (true) {
                int len = is.read(buffer);
                if (len == -1) {
                    break;
                }
                outStream.write(buffer, 0, len);
            }
            is.close();
            outStream.write(LINEND.getBytes());
            byte[] end_data = ("--" + BOUNDARY + "--" + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e2) {
            e2.printStackTrace();
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
        }
        if (isCancelled()) {
            outStream.close();
            conn.disconnect();
            return null;
        }
        try {
            try {
                responseCode = conn.getResponseCode();
                InputStream in = conn.getInputStream();
                if (responseCode == 200) {
                    StringBuilder sb2 = new StringBuilder();
                    while (true) {
                        int ch = in.read();
                        if (ch == -1) {
                            break;
                        }
                        sb2.append((char) ch);
                    }
                    response = sb2.toString();
                }
                Logger.logI(TAG, response);
                outStream.close();
                conn.disconnect();
                if (isCancelled()) {
                    return null;
                }
            } catch (Exception e4) {
                e4.printStackTrace();
                outStream.close();
                conn.disconnect();
                if (isCancelled()) {
                    return null;
                }
            }
            return new HttpResponse(response, responseCode);
        } catch (Throwable th) {
            outStream.close();
            conn.disconnect();
            if (isCancelled()) {
                return null;
            }
            throw th;
        }
    }

    @Override // com.yellowbook.android2.api.request.mid_tier.CallAPIWithCacheableMidTier
    protected void onMidTierResponseReceived(JSONObject respJSONObject) throws JSONException {
        JSONObject bvResponse = respJSONObject.getJSONObject("DataApiResponse");
        boolean hasError = bvResponse.getBoolean("HasErrors");
        if (hasError) {
            String message = bvResponse.getJSONObject("FormErrors").getJSONObject("FieldErrors").getJSONArray("FieldError").getJSONObject(0).getString("message");
            error(4, 0, message);
        } else {
            SubmitPhotoResponse response = new SubmitPhotoResponse(bvResponse);
            this.responseHandler.handleResponse(response);
        }
    }
}
