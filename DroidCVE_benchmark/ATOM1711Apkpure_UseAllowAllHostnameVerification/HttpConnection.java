package org.jsoup.helper;

import com.google.android.exoplayer2.C;
import com.hl.platformkit.utils.common.WpkCommonUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Connection;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.TokenQueue;

/* loaded from: classes2.dex */
public class HttpConnection implements Connection {
    private Connection.Request a = new Request();
    private Connection.Response b = new Response();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class Base<T extends Connection.Base> implements Connection.Base<T> {
        URL a;
        Connection.Method b;
        Map<String, String> c;
        Map<String, String> d;

        private static String f(String str) {
            try {
                byte[] bytes = str.getBytes("ISO-8859-1");
                return !a(bytes) ? str : new String(bytes, C.UTF8_NAME);
            } catch (UnsupportedEncodingException unused) {
                return str;
            }
        }

        private String g(String str) {
            Map.Entry<String, String> h;
            Validate.a((Object) str, "Header name must not be null");
            String str2 = this.c.get(str);
            if (str2 == null) {
                str2 = this.c.get(Normalizer.a(str));
            }
            return (str2 != null || (h = h(str)) == null) ? str2 : h.getValue();
        }

        private Map.Entry<String, String> h(String str) {
            String a = Normalizer.a(str);
            for (Map.Entry<String, String> entry : this.c.entrySet()) {
                if (Normalizer.a(entry.getKey()).equals(a)) {
                    return entry;
                }
            }
            return null;
        }

        @Override // org.jsoup.Connection.Base
        public URL a() {
            return this.a;
        }

        @Override // org.jsoup.Connection.Base
        public T b(String str) {
            Validate.a(str, "Header name must not be empty");
            Map.Entry<String, String> h = h(str);
            if (h != null) {
                this.c.remove(h.getKey());
            }
            return this;
        }

        public boolean c(String str, String str2) {
            return a(str) && e(str).equalsIgnoreCase(str2);
        }

        public boolean d(String str) {
            Validate.a(str, "Cookie name must not be empty");
            return this.d.containsKey(str);
        }

        public String e(String str) {
            Validate.a((Object) str, "Header name must not be null");
            String g = g(str);
            return g != null ? f(g) : g;
        }

        @Override // org.jsoup.Connection.Base
        public Connection.Method method() {
            return this.b;
        }

        private Base() {
            this.c = new LinkedHashMap();
            this.d = new LinkedHashMap();
        }

        @Override // org.jsoup.Connection.Base
        public T a(URL url) {
            Validate.a(url, "URL must not be null");
            this.a = url;
            return this;
        }

        @Override // org.jsoup.Connection.Base
        public Map<String, String> c() {
            return this.c;
        }

        @Override // org.jsoup.Connection.Base
        public T a(Connection.Method method) {
            Validate.a(method, "Method must not be null");
            this.b = method;
            return this;
        }

        @Override // org.jsoup.Connection.Base
        public T b(String str, String str2) {
            Validate.a(str, "Cookie name must not be empty");
            Validate.a((Object) str2, "Cookie value must not be null");
            this.d.put(str, str2);
            return this;
        }

        /* JADX WARN: Code restructure failed: missing block: B:12:0x0026, code lost:
        
            if ((((r8[1] & 255) == 187) & ((r8[2] & 255) == 191)) != false) goto L18;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static boolean a(byte[] r8) {
            /*
                int r0 = r8.length
                r1 = 3
                r2 = 0
                r3 = 1
                if (r0 < r1) goto L29
                r0 = r8[r2]
                r0 = r0 & 255(0xff, float:3.57E-43)
                r4 = 239(0xef, float:3.35E-43)
                if (r0 != r4) goto L29
                r0 = r8[r3]
                r0 = r0 & 255(0xff, float:3.57E-43)
                r4 = 187(0xbb, float:2.62E-43)
                if (r0 != r4) goto L18
                r0 = r3
                goto L19
            L18:
                r0 = r2
            L19:
                r4 = 2
                r4 = r8[r4]
                r4 = r4 & 255(0xff, float:3.57E-43)
                r5 = 191(0xbf, float:2.68E-43)
                if (r4 != r5) goto L24
                r4 = r3
                goto L25
            L24:
                r4 = r2
            L25:
                r0 = r0 & r4
                if (r0 == 0) goto L29
                goto L2a
            L29:
                r1 = r2
            L2a:
                int r0 = r8.length
            L2b:
                if (r1 >= r0) goto L5d
                r4 = r8[r1]
                r5 = r4 & 128(0x80, float:1.794E-43)
                if (r5 != 0) goto L34
                goto L5a
            L34:
                r5 = r4 & 224(0xe0, float:3.14E-43)
                r6 = 192(0xc0, float:2.69E-43)
                if (r5 != r6) goto L3d
                int r4 = r1 + 1
                goto L4e
            L3d:
                r5 = r4 & 240(0xf0, float:3.36E-43)
                r7 = 224(0xe0, float:3.14E-43)
                if (r5 != r7) goto L46
                int r4 = r1 + 2
                goto L4e
            L46:
                r4 = r4 & 248(0xf8, float:3.48E-43)
                r5 = 240(0xf0, float:3.36E-43)
                if (r4 != r5) goto L5c
                int r4 = r1 + 3
            L4e:
                if (r1 >= r4) goto L5a
                int r1 = r1 + 1
                r5 = r8[r1]
                r5 = r5 & r6
                r7 = 128(0x80, float:1.794E-43)
                if (r5 == r7) goto L4e
                return r2
            L5a:
                int r1 = r1 + r3
                goto L2b
            L5c:
                return r2
            L5d:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.HttpConnection.Base.a(byte[]):boolean");
        }

        @Override // org.jsoup.Connection.Base
        public Map<String, String> b() {
            return this.d;
        }

        @Override // org.jsoup.Connection.Base
        public T a(String str, String str2) {
            Validate.a(str, "Header name must not be empty");
            Validate.a((Object) str2, "Header value must not be null");
            b(str);
            this.c.put(str, str2);
            return this;
        }

        @Override // org.jsoup.Connection.Base
        public boolean a(String str) {
            Validate.a(str, "Header name must not be empty");
            return g(str) != null;
        }
    }

    /* loaded from: classes2.dex */
    public static class Request extends Base<Connection.Request> implements Connection.Request {
        private Proxy e;
        private int f;
        private int g;
        private boolean h;
        private Collection<Connection.KeyVal> i;
        private String j;
        private boolean k;
        private boolean l;
        private Parser m;
        private boolean n;
        private boolean o;
        private String p;

        @Override // org.jsoup.Connection.Request
        public boolean d() {
            return this.k;
        }

        @Override // org.jsoup.Connection.Request
        public boolean f() {
            return this.o;
        }

        @Override // org.jsoup.Connection.Request
        public boolean g() {
            return this.l;
        }

        @Override // org.jsoup.Connection.Request
        public Proxy h() {
            return this.e;
        }

        @Override // org.jsoup.Connection.Request
        public Collection<Connection.KeyVal> i() {
            return this.i;
        }

        @Override // org.jsoup.Connection.Request
        public boolean j() {
            return this.h;
        }

        @Override // org.jsoup.Connection.Request
        public String l() {
            return this.j;
        }

        @Override // org.jsoup.Connection.Request
        public int m() {
            return this.g;
        }

        @Override // org.jsoup.Connection.Request
        public Parser n() {
            return this.m;
        }

        @Override // org.jsoup.Connection.Request
        public int timeout() {
            return this.f;
        }

        private Request() {
            super();
            this.j = null;
            this.k = false;
            this.l = false;
            this.n = false;
            this.o = true;
            this.p = C.UTF8_NAME;
            this.f = WpkCommonUtil.FILE_SAVE_FINISHED;
            this.g = 1048576;
            this.h = true;
            this.i = new ArrayList();
            this.b = Connection.Method.GET;
            this.c.put("Accept-Encoding", "gzip");
            this.c.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
            this.m = Parser.b();
        }

        @Override // org.jsoup.Connection.Request
        public Connection.Request c(String str) {
            this.j = str;
            return this;
        }

        @Override // org.jsoup.Connection.Request
        public String e() {
            return this.p;
        }

        @Override // org.jsoup.Connection.Request
        public /* bridge */ /* synthetic */ Connection.Request a(Parser parser) {
            a(parser);
            return this;
        }

        @Override // org.jsoup.Connection.Request
        public /* bridge */ /* synthetic */ Connection.Request a(int i) {
            a(i);
            return this;
        }

        @Override // org.jsoup.Connection.Request
        public Request a(int i) {
            Validate.b(i >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            this.f = i;
            return this;
        }

        @Override // org.jsoup.Connection.Request
        public Request a(Parser parser) {
            this.m = parser;
            this.n = true;
            return this;
        }
    }

    /* loaded from: classes2.dex */
    public static class Response extends Base<Connection.Response> implements Connection.Response {
        private static SSLSocketFactory k;
        private static final Pattern l = Pattern.compile("(application|text)/\\w*\\+?xml.*");
        private ByteBuffer e;
        private String f;
        private String g;
        private boolean h;
        private int i;
        private Connection.Request j;

        Response() {
            super();
            this.h = false;
            this.i = 0;
        }

        private static HostnameVerifier p() {
            return new HostnameVerifier() { // from class: org.jsoup.helper.HttpConnection.Response.1
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String str, SSLSession sSLSession) {
                    return true;
                }
            };
        }

        private static synchronized void q() throws IOException {
            synchronized (Response.class) {
                if (k == null) {
                    TrustManager[] trustManagerArr = {new X509TrustManager() { // from class: org.jsoup.helper.HttpConnection.Response.2
                        @Override // javax.net.ssl.X509TrustManager
                        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }};
                    try {
                        SSLContext sSLContext = SSLContext.getInstance("SSL");
                        sSLContext.init(null, trustManagerArr, new SecureRandom());
                        k = sSLContext.getSocketFactory();
                    } catch (KeyManagementException unused) {
                        throw new IOException("Can't create unsecure trust manager");
                    } catch (NoSuchAlgorithmException unused2) {
                        throw new IOException("Can't create unsecure trust manager");
                    }
                }
            }
        }

        @Override // org.jsoup.Connection.Response
        public Document k() throws IOException {
            Validate.b(this.h, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
            Document a = DataUtil.a(this.e, this.f, this.a.toExternalForm(), this.j.n());
            this.e.rewind();
            this.f = a.H().a().name();
            return a;
        }

        public String o() {
            return this.g;
        }

        static Response b(Connection.Request request) throws IOException {
            return a(request, (Response) null);
        }

        private static String c(Connection.Request request) {
            StringBuilder sb = new StringBuilder();
            boolean z = true;
            for (Map.Entry<String, String> entry : request.b().entrySet()) {
                if (z) {
                    z = false;
                } else {
                    sb.append("; ");
                }
                sb.append(entry.getKey());
                sb.append('=');
                sb.append(entry.getValue());
            }
            return sb.toString();
        }

        private static void d(Connection.Request request) throws IOException {
            boolean z;
            URL a = request.a();
            StringBuilder sb = new StringBuilder();
            sb.append(a.getProtocol());
            sb.append("://");
            sb.append(a.getAuthority());
            sb.append(a.getPath());
            sb.append("?");
            if (a.getQuery() != null) {
                sb.append(a.getQuery());
                z = false;
            } else {
                z = true;
            }
            for (Connection.KeyVal keyVal : request.i()) {
                Validate.a(keyVal.c(), "InputStream data not supported in URL query string.");
                if (z) {
                    z = false;
                } else {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(keyVal.a(), C.UTF8_NAME));
                sb.append('=');
                sb.append(URLEncoder.encode(keyVal.value(), C.UTF8_NAME));
            }
            request.a(new URL(sb.toString()));
            request.i().clear();
        }

        private static String e(Connection.Request request) {
            if (!request.a("Content-Type")) {
                if (HttpConnection.b(request)) {
                    String b = DataUtil.b();
                    request.a("Content-Type", "multipart/form-data; boundary=" + b);
                    return b;
                }
                request.a("Content-Type", "application/x-www-form-urlencoded; charset=" + request.e());
            }
            return null;
        }

        /* JADX WARN: Code restructure failed: missing block: B:63:0x0158, code lost:
        
            if (org.jsoup.helper.HttpConnection.Response.l.matcher(r7).matches() == false) goto L73;
         */
        /* JADX WARN: Code restructure failed: missing block: B:65:0x015c, code lost:
        
            if ((r6 instanceof org.jsoup.helper.HttpConnection.Request) == false) goto L73;
         */
        /* JADX WARN: Code restructure failed: missing block: B:67:0x0165, code lost:
        
            if (((org.jsoup.helper.HttpConnection.Request) r6).n != false) goto L73;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x0167, code lost:
        
            r6.a(org.jsoup.parser.Parser.c());
         */
        /* JADX WARN: Finally extract failed */
        /* JADX WARN: Removed duplicated region for block: B:23:0x007c A[Catch: all -> 0x01d7, TryCatch #1 {all -> 0x01d7, blocks: (B:21:0x0073, B:23:0x007c, B:24:0x0083, B:26:0x0097, B:30:0x00a1, B:31:0x00b5, B:33:0x00bb, B:35:0x00c3, B:37:0x00cc, B:38:0x00d0, B:39:0x00e9, B:41:0x00ef, B:43:0x0105, B:50:0x011b, B:52:0x0121, B:54:0x0127, B:56:0x012f, B:59:0x013c, B:60:0x014b, B:62:0x014e, B:64:0x015a, B:66:0x015e, B:68:0x0167, B:69:0x016e, B:71:0x017c, B:82:0x01b0, B:89:0x01b7, B:90:0x01ba, B:91:0x01bb, B:92:0x0115, B:94:0x01c7, B:95:0x01d6, B:74:0x0184, B:76:0x018a, B:77:0x0193, B:79:0x019e, B:80:0x01a4, B:86:0x018f), top: B:20:0x0073, inners: #0 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static org.jsoup.helper.HttpConnection.Response a(org.jsoup.Connection.Request r6, org.jsoup.helper.HttpConnection.Response r7) throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 476
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.HttpConnection.Response.a(org.jsoup.Connection$Request, org.jsoup.helper.HttpConnection$Response):org.jsoup.helper.HttpConnection$Response");
        }

        private Response(Response response) throws IOException {
            super();
            this.h = false;
            this.i = 0;
            if (response != null) {
                this.i = response.i + 1;
                if (this.i >= 20) {
                    throw new IOException(String.format("Too many redirects occurred trying to load URL %s", response.a()));
                }
            }
        }

        private static HttpURLConnection a(Connection.Request request) throws IOException {
            URLConnection openConnection;
            if (request.h() == null) {
                openConnection = request.a().openConnection();
            } else {
                openConnection = request.a().openConnection(request.h());
            }
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setRequestMethod(request.method().name());
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setConnectTimeout(request.timeout());
            httpURLConnection.setReadTimeout(request.timeout());
            if ((httpURLConnection instanceof HttpsURLConnection) && !request.f()) {
                q();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
                httpsURLConnection.setSSLSocketFactory(k);
                httpsURLConnection.setHostnameVerifier(p());
            }
            if (request.method().d()) {
                httpURLConnection.setDoOutput(true);
            }
            if (request.b().size() > 0) {
                httpURLConnection.addRequestProperty("Cookie", c(request));
            }
            for (Map.Entry<String, String> entry : request.c().entrySet()) {
                httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
            }
            return httpURLConnection;
        }

        private void a(HttpURLConnection httpURLConnection, Connection.Response response) throws IOException {
            this.b = Connection.Method.valueOf(httpURLConnection.getRequestMethod());
            this.a = httpURLConnection.getURL();
            httpURLConnection.getResponseCode();
            httpURLConnection.getResponseMessage();
            this.g = httpURLConnection.getContentType();
            a(a(httpURLConnection));
            if (response != null) {
                for (Map.Entry<String, String> entry : response.b().entrySet()) {
                    if (!d(entry.getKey())) {
                        b(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        private static LinkedHashMap<String, List<String>> a(HttpURLConnection httpURLConnection) {
            LinkedHashMap<String, List<String>> linkedHashMap = new LinkedHashMap<>();
            int i = 0;
            while (true) {
                String headerFieldKey = httpURLConnection.getHeaderFieldKey(i);
                String headerField = httpURLConnection.getHeaderField(i);
                if (headerFieldKey == null && headerField == null) {
                    return linkedHashMap;
                }
                i++;
                if (headerFieldKey != null && headerField != null) {
                    if (linkedHashMap.containsKey(headerFieldKey)) {
                        linkedHashMap.get(headerFieldKey).add(headerField);
                    } else {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(headerField);
                        linkedHashMap.put(headerFieldKey, arrayList);
                    }
                }
            }
        }

        void a(Map<String, List<String>> map) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key != null) {
                    List<String> value = entry.getValue();
                    if (key.equalsIgnoreCase("Set-Cookie")) {
                        for (String str : value) {
                            if (str != null) {
                                TokenQueue tokenQueue = new TokenQueue(str);
                                String trim = tokenQueue.a("=").trim();
                                String trim2 = tokenQueue.c(";").trim();
                                if (trim.length() > 0) {
                                    b(trim, trim2);
                                }
                            }
                        }
                    } else {
                        if (value.size() == 1) {
                            a(key, value.get(0));
                        } else if (value.size() > 1) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < value.size(); i++) {
                                String str2 = value.get(i);
                                if (i != 0) {
                                    sb.append(", ");
                                }
                                sb.append(str2);
                            }
                            a(key, sb.toString());
                        }
                    }
                }
            }
        }

        private static void a(Connection.Request request, OutputStream outputStream, String str) throws IOException {
            Collection<Connection.KeyVal> i = request.i();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, request.e()));
            if (str != null) {
                for (Connection.KeyVal keyVal : i) {
                    bufferedWriter.write("--");
                    bufferedWriter.write(str);
                    bufferedWriter.write("\r\n");
                    bufferedWriter.write("Content-Disposition: form-data; name=\"");
                    bufferedWriter.write(HttpConnection.d(keyVal.a()));
                    bufferedWriter.write("\"");
                    if (keyVal.c()) {
                        bufferedWriter.write("; filename=\"");
                        bufferedWriter.write(HttpConnection.d(keyVal.value()));
                        bufferedWriter.write("\"\r\nContent-Type: application/octet-stream\r\n\r\n");
                        bufferedWriter.flush();
                        DataUtil.a(keyVal.b(), outputStream);
                        outputStream.flush();
                    } else {
                        bufferedWriter.write("\r\n\r\n");
                        bufferedWriter.write(keyVal.value());
                    }
                    bufferedWriter.write("\r\n");
                }
                bufferedWriter.write("--");
                bufferedWriter.write(str);
                bufferedWriter.write("--");
            } else if (request.l() != null) {
                bufferedWriter.write(request.l());
            } else {
                boolean z = true;
                for (Connection.KeyVal keyVal2 : i) {
                    if (z) {
                        z = false;
                    } else {
                        bufferedWriter.append('&');
                    }
                    bufferedWriter.write(URLEncoder.encode(keyVal2.a(), request.e()));
                    bufferedWriter.write(61);
                    bufferedWriter.write(URLEncoder.encode(keyVal2.value(), request.e()));
                }
            }
            bufferedWriter.close();
        }
    }

    private HttpConnection() {
    }

    public static Connection c(String str) {
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.a(str);
        return httpConnection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String d(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\"", "%22");
    }

    private static String e(String str) {
        try {
            return b(new URL(str)).toExternalForm();
        } catch (Exception unused) {
            return str;
        }
    }

    @Override // org.jsoup.Connection
    public Document get() throws IOException {
        this.a.a(Connection.Method.GET);
        a();
        return this.b.k();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static URL b(URL url) {
        try {
            return new URL(new URI(url.toExternalForm()).toASCIIString());
        } catch (Exception unused) {
            return url;
        }
    }

    @Override // org.jsoup.Connection
    public Connection a(String str) {
        Validate.a(str, "Must supply a valid URL");
        try {
            this.a.a(new URL(e(str)));
            return this;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + str, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean b(Connection.Request request) {
        Iterator<Connection.KeyVal> it = request.i().iterator();
        while (it.hasNext()) {
            if (it.next().c()) {
                return true;
            }
        }
        return false;
    }

    @Override // org.jsoup.Connection
    public Connection a(int i) {
        this.a.a(i);
        return this;
    }

    public Connection.Response a() throws IOException {
        this.b = Response.b(this.a);
        return this.b;
    }
}
