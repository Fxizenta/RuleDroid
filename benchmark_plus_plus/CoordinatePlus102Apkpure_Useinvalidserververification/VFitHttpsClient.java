package jp.co.toshiba.vft.comm;

import android.util.Log;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import jcifs.https.Handler;
import jp.co.toshiba.vft.data.stub.Request;
import jp.co.toshiba.vft.data.stub.Response;
import jp.co.toshiba.vft.model.ExternalStore;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/* loaded from: classes.dex */
public class VFitHttpsClient {
    public static final int MAX_CONNECTIONS_PER_ROUTE = 64;
    public static final int MAX_TOTAL_CONNECTION = 64;
    private static final String TAG = "VFitHttpsClient";
    public static final int TIMEOUT_CONNECT = 10000;
    public static final int TIMEOUT_READ = 60000;
    private static JsonFactory mJsonFactory;
    private static ObjectMapper mObjectMapper;
    private static VFitHttpsClient vFitHttpsClient;
    private static VFitHttpsClient vFitHttpsClientAnonymous;
    private HttpClient mDefaultHttpClient;

    static {
        String uid = ExternalStore.getApplicatinInfo().getAccountId();
        String pwd = ExternalStore.getApplicatinInfo().getPassword();
        vFitHttpsClient = createVFitHttpsClient(uid, pwd);
        vFitHttpsClientAnonymous = createVFitHttpsClient(null, null);
        mJsonFactory = null;
        mObjectMapper = null;
        mJsonFactory = new JsonFactory();
        mJsonFactory.setCharacterEscapes(new JacksonCharacterEscapes());
        mObjectMapper = new ObjectMapper(mJsonFactory).enable(SerializationFeature.INDENT_OUTPUT).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setSerializationInclusion(JsonInclude.Include.NON_NULL).setDateFormat(new TicksSinceFormat());
    }

    public static VFitHttpsClient getInstance() {
        return vFitHttpsClient;
    }

    public static VFitHttpsClient getAnonymousInstance() {
        return vFitHttpsClientAnonymous;
    }

    private static VFitHttpsClient createVFitHttpsClient(String accountId, String password) {
        DefaultHttpClient hc;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new VFitSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, VFitConst.VFIT_CHAR_CODE);
            ConnManagerParams.setMaxTotalConnections(params, 64);
            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(64));
            ConnManagerParams.setTimeout(params, 10000L);
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_CONNECT);
            HttpConnectionParams.setSoTimeout(params, TIMEOUT_READ);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, Handler.DEFAULT_HTTPS_PORT));
            ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(params, registry);
            DefaultHttpClient hc2 = new DefaultHttpClient(ccm, params);
            try {
                if (!StringUtils.isEmpty(accountId) && !StringUtils.isEmpty(password)) {
                    hc2.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
                    NTCredentials cred = new NTCredentials(accountId, password, "", "");
                    AuthScope scope = new AuthScope(AuthScope.ANY_HOST, -1);
                    hc2.getCredentialsProvider().setCredentials(scope, cred);
                }
                hc = hc2;
            } catch (Exception e) {
                e = e;
                e.printStackTrace();
                hc = new DefaultHttpClient();
                VFitHttpsClient client = new VFitHttpsClient(hc);
                return client;
            }
        } catch (Exception e2) {
            e = e2;
        }
        VFitHttpsClient client2 = new VFitHttpsClient(hc);
        return client2;
    }

    private VFitHttpsClient(DefaultHttpClient hc) {
        this.mDefaultHttpClient = null;
        this.mDefaultHttpClient = hc;
    }

    public <TRequest extends Request, TResponse extends Response> TResponse postData(String str, TRequest trequest, Class<?> cls) throws VFitCommonException {
        HttpEntity genPostEntity;
        if (StringUtils.isEmpty(str) || trequest == null || cls == null || (genPostEntity = genPostEntity(trequest)) == null) {
            return null;
        }
        HttpPost httpPost = new HttpPost(str);
        httpPost.addHeader("Content-type", "application/json");
        httpPost.setEntity(genPostEntity);
        try {
            HttpResponse execute = this.mDefaultHttpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() != 200) {
                Log.d(TAG, String.format("Action=%s StatusCode=%d", str, Integer.valueOf(execute.getStatusLine().getStatusCode())));
                throw new VFitCommonException("THC9999", new Object[0]);
            }
            return (TResponse) genReponseData(execute, cls);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            throw new VFitCommonException(e, "THC0001", new Object[0]);
        } catch (IOException e2) {
            e2.printStackTrace();
            throw new VFitCommonException(e2, "THC0001", new Object[0]);
        } catch (Exception e3) {
            e3.printStackTrace();
            throw new VFitCommonException(e3, "THC0001", new Object[0]);
        }
    }

    public <T> void getData(String actionURL, T data) {
        throw new UnsupportedOperationException();
    }

    public boolean download(String url, String saveTo) throws Exception {
        Log.d(TAG, String.format("[download] url=%s", url));
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("parameter \"url\" can not null or empty.");
        }
        if (StringUtils.isEmpty(saveTo)) {
            throw new IllegalArgumentException("parameter \"saveTo\" can not null or empty.");
        }
        File saveToFile = new File(saveTo);
        if (saveToFile.getParentFile() == null || !saveToFile.getParentFile().exists()) {
            throw new IOException("file \"" + saveTo + "\" parent folder is not exists.");
        }
        Stopwatch sw = new Stopwatch();
        sw.start();
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = this.mDefaultHttpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                Log.d(TAG, String.format("[download] error(%d) url=%s", Integer.valueOf(response.getStatusLine().getStatusCode()), url));
                if (response.getStatusLine().getStatusCode() != 404) {
                    throw new Exception(String.format("download error(%d) url=%s", Integer.valueOf(response.getStatusLine().getStatusCode()), url));
                }
                sw.stop();
                Log.d(TAG, String.format("[download] [性能測定] 時間(ミリ秒)=%f download(%s)", Float.valueOf(sw.getMilliseconds()), url));
                return false;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(response.getEntity().getContent());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(saveToFile));
            byte[] buffer = new byte[8196];
            while (true) {
                int inByte = bufferedInputStream.read(buffer);
                if (inByte == -1) {
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                    sw.stop();
                    Log.d(TAG, String.format("[download] [性能測定] 時間(ミリ秒)=%f download(%s)", Float.valueOf(sw.getMilliseconds()), url));
                    return true;
                }
                bufferedOutputStream.write(buffer, 0, inByte);
            }
        } catch (Throwable th) {
            sw.stop();
            Log.d(TAG, String.format("[download] [性能測定] 時間(ミリ秒)=%f download(%s)", Float.valueOf(sw.getMilliseconds()), url));
            throw th;
        }
    }

    public Header[] downloadWithHeaders(String url, String saveTo) throws Exception {
        Log.d(TAG, String.format("[downloadWithHeaders] url=%s", url));
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("parameter \"url\" can not null or empty.");
        }
        if (StringUtils.isEmpty(saveTo)) {
            throw new IllegalArgumentException("parameter \"saveTo\" can not null or empty.");
        }
        File saveToFile = new File(saveTo);
        if (saveToFile.getParentFile() == null || !saveToFile.getParentFile().exists()) {
            throw new IOException("file \"" + saveTo + "\" parent folder is not exists.");
        }
        Stopwatch sw = new Stopwatch();
        sw.start();
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = this.mDefaultHttpClient.execute(request);
            Header[] headers = response.getAllHeaders();
            if (response.getStatusLine().getStatusCode() != 200) {
                Log.d(TAG, String.format("[downloadWithHeaders] error(%d) url=%s", Integer.valueOf(response.getStatusLine().getStatusCode()), url));
                if (response.getStatusLine().getStatusCode() != 404) {
                    throw new Exception(String.format("download error(%d) url=%s", Integer.valueOf(response.getStatusLine().getStatusCode()), url));
                }
                sw.stop();
                Log.d(TAG, String.format("[downloadWithHeaders] [性能測定] 時間(ミリ秒)=%f download(%s)", Float.valueOf(sw.getMilliseconds()), url));
                return null;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(response.getEntity().getContent());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(saveToFile));
            byte[] buffer = new byte[8196];
            while (true) {
                int inByte = bufferedInputStream.read(buffer);
                if (inByte == -1) {
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                    sw.stop();
                    Log.d(TAG, String.format("[downloadWithHeaders] [性能測定] 時間(ミリ秒)=%f download(%s)", Float.valueOf(sw.getMilliseconds()), url));
                    return headers;
                }
                bufferedOutputStream.write(buffer, 0, inByte);
            }
        } catch (Throwable th) {
            sw.stop();
            Log.d(TAG, String.format("[downloadWithHeaders] [性能測定] 時間(ミリ秒)=%f download(%s)", Float.valueOf(sw.getMilliseconds()), url));
            throw th;
        }
    }

    public void recycle() {
        if (this.mDefaultHttpClient != null) {
            try {
                this.mDefaultHttpClient.getConnectionManager().shutdown();
            } catch (Exception e) {
            }
        }
    }

    private <T> HttpEntity genPostEntity(T data) {
        if (data == null) {
            return null;
        }
        try {
            byte[] buff = mObjectMapper.writeValueAsBytes(data);
            return new ByteArrayEntity(buff);
        } catch (Exception e) {
            try {
                e.printStackTrace();
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    private <TResponse extends Response> TResponse genReponseData(HttpResponse response, Class<?> responseType) {
        Stopwatch sw = new Stopwatch();
        sw.start();
        HttpEntity resEntity = response.getEntity();
        Object res = null;
        try {
            res = (Response) mObjectMapper.readValue(resEntity.getContent(), responseType);
        } catch (JsonParseException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex2) {
            ex2.printStackTrace();
        } catch (IOException ex3) {
            ex3.printStackTrace();
        } catch (IllegalStateException ex4) {
            ex4.printStackTrace();
        } catch (Exception ex5) {
            ex5.printStackTrace();
        }
        sw.stop();
        Log.d(TAG, "[性能測定]\u3000genReponseData() Stream→Object\u3000時間(ミリ秒)=" + sw.getMilliseconds());
        return (TResponse) res;
    }
}
