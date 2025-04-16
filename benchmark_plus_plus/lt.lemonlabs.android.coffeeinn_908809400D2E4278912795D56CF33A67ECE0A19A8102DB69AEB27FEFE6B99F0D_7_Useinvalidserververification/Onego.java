package nsoft.onego;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Onego {
    public Context contextForDialog = null;
    public static String URLAuthorizeOnego = "https://oauth.onego.com/rest/authorize";
    public static String URLLogoutOnego = "https://auth.onego.com/m/logout";
    public static String URLProfile = "https://api.onego.com/services/v1/me";
    public static String URLProfilePhoto = "https://api.onego.com/services/v1/me/photo";
    public static String URLMerchantInbox = "https://api.onego.com/services/v1/merchants/{merchant_id}/inbox";
    public static String URLSupported_countries = "https://api.onego.com/services/v1/location/supported_countries";
    public static String URLSupported_states = "https://api.onego.com/services/v1/location/supported_states";
    public static String URLSupported_cities = "https://api.onego.com/services/v1/location/cities?query=&country=";
    public static String URL_GET_CARDS = "https://api.onego.com/services/v1/bims";
    public static String URL_BlOCK_CARD = "https://api.onego.com/services/v1/bims/{card_code}/block";
    public static String URL_UNBlOCK_CARD = "https://api.onego.com/services/v1/bims/{card_code}/unblock";
    public static String URL_REMOVE_CARD = "https://api.onego.com/services/v1/bims/{card_code}/remove";
    public static String URL_ADD_CARD = "https://api.onego.com/services/v1/bims/card";
    public static String URL_ONEGO_REGISTRATION = "https://oauth.onego.com/rest/register";
    public static String URL_3RD_PARTY_AUTHORIZE = "https://oauth.onego.com/rest/{provider}/authorize/token";
    public static String URL_3RD_PARTY_REGISTRATION = "https://oauth.onego.com/rest/third-party/register/{sessionToken}";
    public static String URL_REDIRECT_SUCCESS = "https://oauth.onego.com/rest/register/confirm/";
    public static String URL_TERMS = "https://onego.com/terms";
    public static String stateSuffix = "&state=";
    public static String Client_ID = "d35ssdn7j0c03cid1cfwq3es04la9hpu8ko0";
    public static String Merchant_ID = "0u1zx9v5omct7lncn00qy0265u300yfg0b56";
    public static String EULA_VERSION = "1.0";

    /* loaded from: classes.dex */
    public static final class OnegoMethod {
        public static final int ADD_CARD = 7;
        public static final int BLOCK_CARD = 5;
        public static final int GET_CARDS = 4;
        public static final int GET_MERCHANT_AUTHORIZED_INBOX = 1;
        public static final int GET_MERCHANT_UNAUTHORIZED_INBOX = 0;
        public static final int GET_PROFILE = 3;
        public static final int POST_AUTHORIZE = 2;
        public static final int POST_AUTHORIZE_FB = 10;
        public static final int POST_AUTHORIZE_GOOGLE = 13;
        public static final int REFRESH_AUTHORIZED_INBOX = 16;
        public static final int REGISTER_FB = 14;
        public static final int REGISTER_GOOGLE = 15;
        public static final int REGISTER_ONEGO = 11;
        public static final int REMOVE_CARD = 8;
        public static final int SAVE_PROFILE = 9;
        public static final int SAVE_PROFILE_PHOTO = 12;
        public static final int UNBLOCK_CARD = 6;
    }

    /* loaded from: classes.dex */
    public static final class PairType {
        public String PairName;
        public String PairValue;

        public PairType(String PName, String PValue) {
            this.PairName = PName;
            this.PairValue = PValue;
        }
    }

    /* loaded from: classes.dex */
    public class SendResponse {
        public String errorMSG;
        public int httpStatus;
        public JSONObject json;
        public String result;
        public int statusCode;

        public SendResponse(HttpResponse resp) {
            this.httpStatus = 0;
            this.statusCode = 0;
            this.errorMSG = "";
            this.result = "";
            this.json = null;
            this.httpStatus = resp.getStatusLine().getStatusCode();
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                try {
                    InputStream isResp = entity.getContent();
                    this.result = Onego.this.convertStreamToString(isResp);
                    try {
                        if (this.result != "") {
                            this.json = new JSONObject(this.result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.errorMSG = e.getMessage();
                    }
                } catch (Exception e2) {
                    this.statusCode = 11;
                    e2.printStackTrace();
                    this.errorMSG = e2.getMessage();
                }
            } else {
                this.statusCode = 10;
            }
            this.statusCode = 1;
        }
    }

    public SendResponse SendPost(String url, ArrayList<PairType> headers, ArrayList<PairType> jsonData) {
        HttpClient client = getNewHttpClient();
        HttpPost post = new HttpPost(url);
        HttpResponse response = null;
        for (int hi = 0; hi < headers.size(); hi++) {
            PairType hid = headers.get(hi);
            post.setHeader(hid.PairName, hid.PairValue);
        }
        JSONObject json = new JSONObject();
        if (jsonData != null) {
            for (int ji = 0; ji < jsonData.size(); ji++) {
                try {
                    PairType jid = jsonData.get(ji);
                    json.put(jid.PairName, jid.PairValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            post.setEntity(new StringEntity(json.toString(), "UTF-8"));
        }
        response = client.execute(post);
        if (response == null) {
            return null;
        }
        SendResponse sendResp = new SendResponse(response);
        return sendResp;
    }

    public SendResponse SendPostPhoto(String url, ArrayList<PairType> headers, byte[] photoData) {
        HttpClient client = getNewHttpClient();
        HttpPost post = new HttpPost(url);
        HttpResponse response = null;
        for (int hi = 0; hi < headers.size(); hi++) {
            PairType hid = headers.get(hi);
            post.setHeader(hid.PairName, hid.PairValue);
        }
        try {
            post.setEntity(new ByteArrayEntity(photoData));
            response = client.execute(post);
        } catch (Exception e) {
            System.out.println("sending post photo: exception");
            e.printStackTrace();
        }
        if (response == null) {
            return null;
        }
        SendResponse sendResp = new SendResponse(response);
        return sendResp;
    }

    public SendResponse SendGet(String url, ArrayList<PairType> headers) {
        return SendGet(url, headers, null);
    }

    public SendResponse SendGet(String url, ArrayList<PairType> headers, ArrayList<PairType> data) {
        HttpResponse response;
        HttpClient client = getNewHttpClient();
        String formedURL = url;
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                if (i == 0) {
                    formedURL = String.valueOf(formedURL) + "?";
                }
                formedURL = String.valueOf(formedURL) + data.get(i).PairName + "=" + data.get(i).PairValue;
            }
        }
        HttpGet get = new HttpGet(formedURL);
        for (int hi = 0; hi < headers.size(); hi++) {
            PairType hid = headers.get(hi);
            get.setHeader(hid.PairName, hid.PairValue);
        }
        try {
            response = client.execute(get);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            response = null;
            client.getConnectionManager().shutdown();
        } catch (IOException e2) {
            e2.printStackTrace();
            response = null;
            client.getConnectionManager().shutdown();
        }
        if (response == null) {
            return null;
        }
        SendResponse GetResp = new SendResponse(response);
        return GetResp;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();
        while (true) {
            try {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        try {
                            break;
                        } catch (IOException e) {
                        }
                    } else {
                        sb.append(String.valueOf(line) + "\n");
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                    try {
                        is.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public HttpClient getNewHttpClient() {
        try {
            System.setProperty("http.keepAlive", "false");
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLTruster(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = getTimeoutHttpParams(20);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            System.out.println("HttpClient returned with Sertificate");
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            System.out.println("empty/new DefaultHttpClient returned");
            return new DefaultHttpClient(getTimeoutHttpParams(20));
        }
    }

    private HttpParams getTimeoutHttpParams(int TimeoutInSeconds) {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = TimeoutInSeconds * 1000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = timeoutConnection * 1000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        return httpParameters;
    }

    public void printResponseData(SendResponse resp) {
        if (resp != null) {
            System.out.println("################# PRINTING DATA #############");
            System.out.println("httpstatus: " + resp.httpStatus);
            System.out.println("statuscode: " + resp.statusCode);
            System.out.println("result: " + resp.result);
            System.out.println("error: " + resp.errorMSG);
            System.out.println("#############################################");
            return;
        }
        System.out.println("################# PRINTING DATA #############");
        System.out.println("################# Response null #############");
    }
}
