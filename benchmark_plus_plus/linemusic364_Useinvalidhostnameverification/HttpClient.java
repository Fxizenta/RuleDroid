package com.linecorp.uniplayer.core.upstream;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpIOExceptionHandler;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes2.dex */
public class HttpClient {
    private static final String a = HttpClient.class.getSimpleName();
    private static final JsonFactory b = new GsonFactory();
    private static PublicKey c;

    public static PublicKey getPublicKey() {
        return c;
    }

    public static void setPubKey(PublicKey publicKey) {
        c = publicKey;
    }

    public static <T> HttpResponse send(String str, Uri uri, String str2, Map<String, String> map, HttpContent httpContent) throws IOException {
        HttpRequest buildRequest = a(true, b).buildRequest(str2, new GenericUrl(uri.toString()), httpContent);
        buildRequest.setConnectTimeout(30000);
        buildRequest.setReadTimeout(30000);
        buildRequest.setSuppressUserAgentSuffix(true);
        if (HttpMethods.POST.equals(str2) || HttpMethods.PUT.equals(str2)) {
            buildRequest.setNumberOfRetries(3);
            buildRequest.setIOExceptionHandler(new HttpIOExceptionHandler() { // from class: com.linecorp.uniplayer.core.upstream.HttpClient.1
                @Override // com.google.api.client.http.HttpIOExceptionHandler
                public boolean handleIOException(HttpRequest httpRequest, boolean z) throws IOException {
                    return true;
                }
            });
        }
        buildRequest.setThrowExceptionOnExecuteError(true);
        buildRequest.setHeaders(a(str, map, httpContent));
        return buildRequest.execute();
    }

    private static HttpHeaders a(String str, Map<String, String> map, HttpContent httpContent) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setUserAgent(str);
        if (map != null && !map.isEmpty()) {
            httpHeaders.putAll(map);
        }
        if (httpContent != null && !TextUtils.isEmpty(httpContent.getType())) {
            httpHeaders.setContentType(httpContent.getType());
        }
        return httpHeaders;
    }

    private static HttpRequestFactory a(boolean z, final JsonFactory jsonFactory) {
        return new NetHttpTransport.Builder().setSslSocketFactory(a(z)).build().createRequestFactory(new HttpRequestInitializer() { // from class: com.linecorp.uniplayer.core.upstream.HttpClient.2
            @Override // com.google.api.client.http.HttpRequestInitializer
            public void initialize(HttpRequest httpRequest) throws IOException {
                httpRequest.setParser(new JsonObjectParser(JsonFactory.this));
            }
        });
    }

    private static SSLSocketFactory a(boolean z) {
        try {
            TrustManager[] trustManagerArr = {new a(z)};
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            sSLContext.init(null, trustManagerArr, null);
            return sSLContext.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class a implements X509TrustManager {
        private static final String b = a.class.getSimpleName();
        final boolean a;

        a(boolean z) {
            this.a = z;
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
            if (x509CertificateArr != null && x509CertificateArr.length > 0 && this.a) {
                PublicKey unused = HttpClient.c = x509CertificateArr[0].getPublicKey();
            }
        }

        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public static <T> T getObjectFromJson(HttpResponse httpResponse, Class<T> cls) throws IOException {
        if (httpResponse == null || cls == null) {
            return null;
        }
        InputStream content = httpResponse.getContent();
        T t = (T) new Gson().fromJson((Reader) new InputStreamReader(content), (Class) cls);
        if (t == null) {
            Log.e(a, "getObjectFromJson()-response content is null!!!");
        }
        content.close();
        httpResponse.disconnect();
        return t;
    }

    public static int readRawData(HttpResponse httpResponse, byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        if (httpResponse != null && bArr != null) {
            InputStream content = httpResponse.getContent();
            while (true) {
                int read = content.read(bArr, i, i2);
                if (read <= 0) {
                    break;
                }
                i += read;
                i2 -= read;
                i3 += read;
            }
            content.close();
            httpResponse.disconnect();
        }
        return i3;
    }
}
