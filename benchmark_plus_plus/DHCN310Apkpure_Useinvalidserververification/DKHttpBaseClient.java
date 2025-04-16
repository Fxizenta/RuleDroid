package jp.co.dhc.android.dhc.dkhttpclient;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import jp.co.dhc.android.dhc.utils.AppException;
import jp.co.dhc.android.dhc.utils.LogUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

/* loaded from: classes.dex */
public abstract class DKHttpBaseClient {
    public static final String REQUEST_ENCODING = "Shift_JIS";
    public boolean isNeedCookieSync;
    protected DefaultHttpClient mClient;
    protected Context mContext;
    public boolean useBasicAuth;
    public boolean useCookieManager;
    public boolean useMySsl;
    public boolean useRedirectHandler;

    protected abstract void setBasicAuth();

    protected abstract void setRedirectHandler();

    public DKHttpBaseClient(Context context) {
        this.useCookieManager = true;
        this.useMySsl = true;
        this.useBasicAuth = false;
        this.useRedirectHandler = true;
        this.isNeedCookieSync = true;
        this.mContext = context;
        this.mClient = new DefaultHttpClient();
    }

    public DKHttpBaseClient(Context context, boolean isNeedCookieSync) {
        this.useCookieManager = true;
        this.useMySsl = true;
        this.useBasicAuth = false;
        this.useRedirectHandler = true;
        this.isNeedCookieSync = true;
        this.isNeedCookieSync = isNeedCookieSync;
        this.mContext = context;
        this.mClient = new DefaultHttpClient();
    }

    public static String requestUriForGet(String schema, String authority, String path, HashMap<String, String> params) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(schema);
        builder.encodedAuthority(authority);
        builder.path(path);
        for (String key : params.keySet()) {
            String value = params.get(key);
            builder.appendQueryParameter(key, value);
        }
        return builder.toString();
    }

    public byte[] get(String uri) throws AppException {
        HttpGet request = new HttpGet(uri);
        HttpConnectionParams.setConnectionTimeout(request.getParams(), 20000);
        HttpConnectionParams.setSoTimeout(request.getParams(), 20000);
        HttpConnectionParams.setSocketBufferSize(request.getParams(), 8192);
        setExtraSettings(uri);
        try {
            try {
                HttpResponse response = this.mClient.execute(request);
                byte[] result = afterGetResponse(response);
                return result;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                throw new AppException(3, " ClientProtocolException ");
            } catch (IOException e2) {
                e2.printStackTrace();
                throw new AppException(3, " IOException");
            }
        } finally {
            closeConnection();
        }
    }

    public byte[] post(String uri, HashMap<String, String> params) throws AppException {
        LogUtil.d("", ">>>>>>> url : " + uri);
        HttpPost request = new HttpPost(uri);
        HttpConnectionParams.setConnectionTimeout(request.getParams(), 20000);
        HttpConnectionParams.setSoTimeout(request.getParams(), 20000);
        HttpConnectionParams.setSocketBufferSize(request.getParams(), 8192);
        setExtraSettings(uri);
        ArrayList<NameValuePair> p = null;
        if (params != null) {
            p = hashMapParamsToNameValuePair(params);
        }
        try {
            try {
                LogUtil.d("", ">>>>>>> params : " + (params == null));
                if (params != null) {
                    request.setEntity(new UrlEncodedFormEntity(p, REQUEST_ENCODING));
                }
                HttpResponse response = this.mClient.execute(request);
                byte[] result = afterGetResponse(response);
                return result;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new AppException(3, " UnsupportedEncodingException");
            } catch (ClientProtocolException e2) {
                e2.printStackTrace();
                throw new AppException(3, " ClientProtocolException");
            } catch (IOException e3) {
                e3.printStackTrace();
                throw new AppException(3, " IOException");
            }
        } finally {
            closeConnection();
        }
    }

    private ArrayList<NameValuePair> hashMapParamsToNameValuePair(HashMap<String, String> params) {
        ArrayList<NameValuePair> p = new ArrayList<>();
        for (String key : params.keySet()) {
            String value = params.get(key);
            LogUtil.d("", ">>>>>> key : " + key);
            LogUtil.d("", ">>>>>> value : " + value);
            p.add(new BasicNameValuePair(key, value));
        }
        return p;
    }

    private byte[] afterGetResponse(HttpResponse response) throws AppException {
        int responseCode = response.getStatusLine().getStatusCode();
        if (isRedirectResponseCode(responseCode)) {
            throw new AppException(5, " http status is redirect");
        }
        if (!isValideResponseCode(responseCode)) {
            throw new AppException(4, " http status is not 200. " + responseCode);
        }
        try {
            byte[] result = EntityUtils.toByteArray(response.getEntity());
            DKHttpClientCookieManager.sharedCookieStore = this.mClient.getCookieStore();
            if (this.useCookieManager && this.isNeedCookieSync) {
                DKHttpClientCookieManager.setCookieToManager(this.mContext, this.mClient.getCookieStore());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(4, " IOException ");
        }
    }

    public static boolean isValideResponseCode(int statusCode) {
        switch (statusCode) {
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
                return true;
            default:
                return false;
        }
    }

    private boolean isRedirectResponseCode(int statusCode) {
        switch (statusCode) {
            case 301:
            case 302:
            case 303:
            case 307:
                return true;
            case 304:
            case 305:
            case 306:
            default:
                return false;
        }
    }

    public void closeConnection() {
        if (this.mClient != null) {
            if (this.mClient.getConnectionManager() != null) {
                this.mClient.getConnectionManager().shutdown();
            }
            this.mClient = null;
        }
    }

    private void setExtraSettings(String uri) {
        setUserAgent();
        if (this.useCookieManager) {
            this.mClient.setCookieStore(DKHttpClientCookieManager.getSharedCookieStore());
        }
        if (this.useBasicAuth) {
            setBasicAuth();
        }
        if (this.useMySsl) {
            setMySslConfig();
        }
        if (this.useRedirectHandler) {
            setRedirectHandler();
        }
    }

    protected void setUserAgent() {
    }

    private void setMySslConfig() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new DKMySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme https = new Scheme("https", sf, 443);
            this.mClient.getConnectionManager().getSchemeRegistry().register(https);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e2) {
            e2.printStackTrace();
        } catch (KeyStoreException e3) {
            e3.printStackTrace();
        } catch (NoSuchAlgorithmException e4) {
            e4.printStackTrace();
        } catch (UnrecoverableKeyException e5) {
            e5.printStackTrace();
        } catch (CertificateException e6) {
            e6.printStackTrace();
        }
    }
}
