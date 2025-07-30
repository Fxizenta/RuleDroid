package com.darkhorse.digital.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import com.darkhorse.digital.auth.AuthManager;
import com.darkhorse.digital.settings.SettingsUtils;
import com.darkhorse.digital.util.CompatibilityManager;
import com.darkhorse.digital.util.DHLog;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DungeonHTTPClient {
    public static final String API_BASE_URL_SECURE = "https://digital.darkhorse.com/api/v5";
    public static final String API_VERSION = "v5";
    public static final String AUTH_URL = "https://digital.darkhorse.com/api/v5/test_authentication/";
    public static final String BOOK_DOWNLOAD_URL = "https://digital.darkhorse.com/api/v5/book/";
    public static final String BOOK_MANIFEST_URL = "https://digital.darkhorse.com/api/v5/bookmanifest/";
    public static final String CATALOG_COLLECTION_URL = "https://digital.darkhorse.com/api/v5/collection/brands/?depth=3";
    public static final String CATALOG_SLICE_URL = "https://digital.darkhorse.com/api/v5/catalogslice/";
    public static final String CATALOG_URL = "https://digital.darkhorse.com/api/v5/brands/?depth=3";
    public static final String CLIENT_SETTINGS_URL = "https://digital.darkhorse.com/frankie/client_settings.json";
    public static final String DEFAULT_FEATURED_URL_BASE = "https://digital.darkhorse.com/frankie/store.html";
    public static final String DUNGEON_SERVER_DOMAIN = "digital.darkhorse.com";
    public static final int HTTP_CONNECTION_TIMEOUT = 10000;
    public static final int HTTP_SOCKET_TIMEOUT = 20000;
    public static final String INSTALL_DETAILS_URL = "https://digital.darkhorse.com/api/v5/install_details";
    public static final String LOADING_STRINGS_URL = "https://digital.darkhorse.com/api/v5/client_strings/loading/";
    public static final String PARAM_CLIENT_SETTING_CATALOG_URL = "CATALOG_URL";
    public static final String PARAM_CLIENT_SETTING_IS_NEWRELIC_AGENT_ENABLED = "IS_ANDROID_NEWRELIC_AGENT_ENABLED";
    public static final String REGISTER_ACCOUNT_URL = "https://digital.darkhorse.com/api/v5/register/";
    public static final String REGISTER_FIELD_CONFIRM_PASSWORD = "confirm_password";
    public static final String REGISTER_FIELD_EMAIL = "email";
    public static final String REGISTER_FIELD_NAME = "name";
    public static final String REGISTER_FIELD_NEWSLETTER_OPT_IN = "newsletter_opt_in";
    public static final String REGISTER_FIELD_PASSWORD = "password";
    public static final String REGISTER_FIELD_SOURCE = "source";
    public static final String REGISTER_FIELD_TOS = "tos";
    public static final String RESET_PASSWORD_URL = "https://digital.darkhorse.com/api/v5/reset_password/";
    public static final String SEARCH_URL = "https://digital.darkhorse.com/api/v5/search/";
    public static final String SIGNED_REQUEST_DOMAIN = "digital.darkhorse.com".split(":")[0];
    public static String TAG = "DarkHorse.DungeonHTTPClient";
    public static final String USER_BOOK_DETAILS_FIELD_MODIFIED_AT = "modified_at";
    public static final String USER_BOOK_DETAILS_FIELD_PAGE = "current_page";
    public static final String USER_BOOK_DETAILS_FIELD_VIEWPORT = "current_viewport";
    public static final String USER_BOOK_DETAILS_URL = "https://digital.darkhorse.com/api/v5/books/details/";
    private final AuthManager mAuthManager;
    private DungeonHTTPClientBase mClient;
    private Context mContext;
    private String mUserAgent;

    public DungeonHTTPClient(String str, AuthManager authManager, Context context) {
        this.mClient = new DungeonHTTPClientBase(context, "digital.darkhorse.com".equals("digital-staging.darkhorse.com"));
        this.mUserAgent = str;
        this.mAuthManager = authManager;
        this.mContext = context;
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, HTTP_SOCKET_TIMEOUT);
        this.mClient.setParams(basicHttpParams);
        this.mClient.getParams().setParameter("http.useragent", this.mUserAgent);
        this.mClient.setRedirectHandler(new DefaultRedirectHandler() { // from class: com.darkhorse.digital.net.DungeonHTTPClient.1
            @Override // org.apache.http.impl.client.DefaultRedirectHandler, org.apache.http.client.RedirectHandler
            public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                return false;
            }
        });
    }

    public void destroy() {
        if (this.mClient != null) {
            this.mClient.getConnectionManager().shutdown();
        }
    }

    private HttpRequestBase signRequest(HttpRequestBase httpRequestBase) {
        String host = httpRequestBase.getURI().getHost();
        if (host != null && host.equals(SIGNED_REQUEST_DOMAIN) && httpRequestBase.getURI().getScheme().equals("https")) {
            if (this.mAuthManager.isSignedIn()) {
                String authToken = this.mAuthManager.getAuthToken();
                httpRequestBase.setHeader(HttpRequest.HEADER_AUTHORIZATION, "Basic " + authToken);
                DHLog.v(TAG, String.format("Signing HTTP request with token: %s", authToken));
                DHLog.d(TAG, "Performing authenticated request.");
            } else {
                DHLog.d(TAG, "Performing unauthenticated request!");
            }
        }
        return httpRequestBase;
    }

    private HttpRequestBase signRequest(HttpRequestBase httpRequestBase, String str) {
        if (httpRequestBase.getURI().getHost().equals(SIGNED_REQUEST_DOMAIN) && httpRequestBase.getURI().getScheme().equals("https")) {
            httpRequestBase.setHeader(HttpRequest.HEADER_AUTHORIZATION, "Basic " + str);
            DHLog.v(TAG, String.format("Manually signing HTTP request with token: %s", str));
            DHLog.v(TAG, "Performing authenticated request.");
        }
        return httpRequestBase;
    }

    private URI getURI(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            DHLog.e(TAG, "URISyntaxException", e, this.mContext);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HttpResponse restApiGet(String str, boolean z) {
        return restApiGet(str, 0L, z, null);
    }

    private HttpGet buildRestApiRequest(String str, long j, boolean z, Header[] headerArr) {
        DHLog.d(TAG, String.format("GET %s", str));
        HttpGet httpGet = new HttpGet();
        URI uri = getURI(str);
        if (uri != null) {
            httpGet.setURI(uri);
            if (z) {
                httpGet = (HttpGet) signRequest(httpGet);
            }
            if (j > 0) {
                httpGet.setHeader("Range", "bytes=" + j + "-");
            }
            if (headerArr != null) {
                for (Header header : headerArr) {
                    httpGet.setHeader(header);
                }
            }
        }
        return httpGet;
    }

    private HttpResponse restApiGet(String str, long j, boolean z, Header[] headerArr) {
        try {
            return this.mClient.execute(buildRestApiRequest(str, j, z, headerArr));
        } catch (SocketTimeoutException e) {
            DHLog.e(TAG, "SocketTimeoutException", e, this.mContext);
            return null;
        } catch (ClientProtocolException e2) {
            DHLog.e(TAG, "ClientProtocolException", e2, this.mContext);
            return null;
        } catch (IOException e3) {
            DHLog.e(TAG, "IOException", e3, this.mContext);
            return null;
        }
    }

    private HttpResponse restApiGet(HttpGet httpGet) {
        try {
            return this.mClient.execute(httpGet);
        } catch (SocketTimeoutException e) {
            DHLog.e(TAG, "SocketTimeoutException", e, this.mContext);
            return null;
        } catch (ClientProtocolException e2) {
            DHLog.e(TAG, "ClientProtocolException", e2, this.mContext);
            return null;
        } catch (IOException e3) {
            DHLog.e(TAG, "IOException", e3, this.mContext);
            return null;
        }
    }

    public HttpResponse restApiPost(String str, List<NameValuePair> list, boolean z) {
        return restApiPost(str, list, z, null);
    }

    private HttpResponse restApiPost(String str, List<NameValuePair> list, boolean z, Header[] headerArr) {
        URI uri = getURI(str);
        if (uri != null) {
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);
            if (z) {
                httpPost = (HttpPost) signRequest(httpPost);
            }
            if (headerArr != null) {
                for (Header header : headerArr) {
                    httpPost.setHeader(header);
                }
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                return this.mClient.execute(httpPost);
            } catch (SocketTimeoutException e) {
                DHLog.e(TAG, "SocketTimeoutException", e, this.mContext);
            } catch (ClientProtocolException e2) {
                DHLog.e(TAG, "ClientProtocolException", e2, this.mContext);
            } catch (IOException e3) {
                DHLog.e(TAG, "IOException", e3, this.mContext);
            }
        }
        return null;
    }

    public HttpResponse getLoadingStrings() {
        return restApiGet(LOADING_STRINGS_URL, false);
    }

    public void getClientSettings() {
        new GetClientSettingsTask().execute(new Void[0]);
    }

    /* loaded from: classes.dex */
    private class GetClientSettingsTask extends AsyncTask<Void, Void, JSONObject> {
        private GetClientSettingsTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public JSONObject doInBackground(Void... voidArr) {
            HttpResponse restApiGet = DungeonHTTPClient.this.restApiGet(DungeonHTTPClient.CLIENT_SETTINGS_URL, false);
            if (restApiGet != null && restApiGet.getStatusLine().getStatusCode() == 200 && restApiGet.getEntity() != null) {
                try {
                    return new JSONObject(DungeonHTTPClient.this.getResponseAsString(restApiGet));
                } catch (JSONException unused) {
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(JSONObject jSONObject) {
            if (jSONObject != null) {
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(DungeonHTTPClient.this.mContext).edit();
                try {
                    edit.putString(DungeonHTTPClient.PARAM_CLIENT_SETTING_CATALOG_URL, jSONObject.getString(DungeonHTTPClient.PARAM_CLIENT_SETTING_CATALOG_URL));
                    edit.putString(SettingsUtils.OPT_FEATURED_URL_BASE, jSONObject.getString(SettingsUtils.OPT_FEATURED_URL_BASE));
                    edit.putBoolean(DungeonHTTPClient.PARAM_CLIENT_SETTING_IS_NEWRELIC_AGENT_ENABLED, jSONObject.getBoolean(DungeonHTTPClient.PARAM_CLIENT_SETTING_IS_NEWRELIC_AGENT_ENABLED));
                } catch (JSONException unused) {
                } catch (Throwable th) {
                    edit.commit();
                    throw th;
                }
                edit.commit();
            }
        }
    }

    private String getCatalogUrl() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(PARAM_CLIENT_SETTING_CATALOG_URL, CATALOG_URL);
    }

    public HttpResponse getBookCatalog(String str) {
        HttpResponse restApiGet = restApiGet(getCatalogUrl(), 0L, false, new BasicHeader[]{new BasicHeader(HttpRequest.HEADER_IF_NONE_MATCH, str)});
        if (restApiGet == null || restApiGet.getStatusLine().getStatusCode() < 400) {
            return restApiGet;
        }
        HttpResponse restApiGet2 = restApiGet(CATALOG_URL, 0L, false, new BasicHeader[]{new BasicHeader(HttpRequest.HEADER_IF_NONE_MATCH, str)});
        DHLog.w(TAG, String.format("catalog request failed for url %s, using fallback url", getCatalogUrl()));
        return restApiGet2;
    }

    public HttpResponse getCatalogSlice(String str) {
        return restApiGet(CATALOG_SLICE_URL + str, false);
    }

    public HttpResponse getPartialCatalog(String str, String str2) {
        return restApiGet("https://digital.darkhorse.com/api/v5/" + str + "/" + str2, false);
    }

    public HttpResponse getPartialCatalog(String str, String str2, String str3) {
        return restApiGet("https://digital.darkhorse.com/api/v5/" + str + "/" + str2, 0L, false, new BasicHeader[]{new BasicHeader(HttpRequest.HEADER_IF_NONE_MATCH, str3)});
    }

    public HttpResponse getSingleBookDetails(String str) {
        return restApiGet(BOOK_MANIFEST_URL + str, false);
    }

    public HttpResponse getSingleBookDetails(String str, String str2) {
        return restApiGet(BOOK_MANIFEST_URL + str, 0L, false, new BasicHeader[]{new BasicHeader(HttpRequest.HEADER_IF_NONE_MATCH, str2)});
    }

    public HttpResponse getRemoteSearchResults(String str, String str2) throws UnsupportedEncodingException {
        return getRemoteSearchResults(str, null, str2);
    }

    public HttpResponse getRemoteSearchResults(String str, String str2, String str3) throws UnsupportedEncodingException {
        String format;
        String encode = URLEncoder.encode(str3, "utf-8");
        if (str2 == null) {
            format = String.format("%s%s/%s/", SEARCH_URL, str, encode);
        } else {
            format = String.format("%s%s/%s/%s/", SEARCH_URL, str, str2, encode);
        }
        return restApiGet(format, false);
    }

    public HttpResponse getBookCollection() {
        return restApiGet(CATALOG_COLLECTION_URL, true);
    }

    public HttpResponse getUserBookDetails(String str, String str2) {
        return restApiGet(String.format("%s%s", USER_BOOK_DETAILS_URL, str), true);
    }

    public HttpResponse getAllUserBookDetails() {
        return restApiGet(USER_BOOK_DETAILS_URL, true);
    }

    public HttpResponse postUserBookDetails(String str, int i, int i2) {
        String format = String.format("%s%s", USER_BOOK_DETAILS_URL, str);
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(new BasicNameValuePair(USER_BOOK_DETAILS_FIELD_PAGE, Integer.toString(i + 1)));
        arrayList.add(new BasicNameValuePair(USER_BOOK_DETAILS_FIELD_VIEWPORT, Integer.toString(i2 + 1)));
        return restApiPost(format, arrayList, true);
    }

    public HttpGet startBookArchiveDownload(String str, long j) {
        HttpResponse restApiGet = restApiGet(BOOK_DOWNLOAD_URL + str, true);
        if (restApiGet == null) {
            return null;
        }
        StatusLine statusLine = restApiGet.getStatusLine();
        Header[] headers = restApiGet.getHeaders(HttpRequest.HEADER_LOCATION);
        HttpEntity entity = restApiGet.getEntity();
        if (entity != null) {
            try {
                entity.consumeContent();
            } catch (IOException unused) {
            }
        }
        if (statusLine.getStatusCode() == 302 || statusLine.getStatusCode() == 301) {
            if (headers.length > 0) {
                String value = headers[0].getValue();
                return buildRestApiRequest(value, j, getURI(value).getHost().equals(SIGNED_REQUEST_DOMAIN), null);
            }
            DHLog.w(TAG, "Book archive request redirected without location header");
            return null;
        }
        if (statusLine.getStatusCode() != 403) {
            return null;
        }
        DHLog.w(TAG, "Book archive request forbidden");
        return null;
    }

    public HttpResponse continueBookArchiveDownload(HttpGet httpGet) {
        return restApiGet(httpGet);
    }

    public HttpResponse getCoverImage(String str, int i) {
        if (str != null) {
            return restApiGet(String.format("%s/_w_%s", str.replaceFirst("https", "http"), Integer.valueOf(i)), false);
        }
        return null;
    }

    public HttpResponse postRegisterNewUser(String str, String str2, String str3, boolean z) {
        ArrayList arrayList = new ArrayList(6);
        arrayList.add(new BasicNameValuePair("name", str));
        arrayList.add(new BasicNameValuePair("email", str2));
        arrayList.add(new BasicNameValuePair(REGISTER_FIELD_PASSWORD, str3));
        arrayList.add(new BasicNameValuePair(REGISTER_FIELD_CONFIRM_PASSWORD, str3));
        arrayList.add(new BasicNameValuePair(REGISTER_FIELD_TOS, "checked"));
        arrayList.add(new BasicNameValuePair(REGISTER_FIELD_SOURCE, AbstractSpiCall.ANDROID_CLIENT_TYPE));
        if (z) {
            arrayList.add(new BasicNameValuePair(REGISTER_FIELD_NEWSLETTER_OPT_IN, "checked"));
        }
        return restApiPost(REGISTER_ACCOUNT_URL, arrayList, false);
    }

    public HttpResponse postResetPassword(String str) {
        URI uri = getURI(RESET_PASSWORD_URL);
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(uri);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(new BasicNameValuePair("email", str));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
            return this.mClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            DHLog.e(TAG, "ClientProtocolException in postResetPassword", e, this.mContext);
            return null;
        } catch (IOException e2) {
            DHLog.e(TAG, "IOException in postResetPassword", e2, this.mContext);
            return null;
        }
    }

    public int getBasicAuthVerification(String str, Context context) {
        int i;
        URI uri = getURI(AUTH_URL);
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(uri);
        try {
            HttpResponse execute = this.mClient.execute((HttpGet) signRequest(httpGet, str));
            StatusLine statusLine = execute.getStatusLine();
            i = statusLine.getStatusCode();
            try {
                if (i == 200) {
                    Scanner useDelimiter = new Scanner(execute.getEntity().getContent()).useDelimiter("\\A");
                    String next = useDelimiter.hasNext() ? useDelimiter.next() : null;
                    if (!StringUtils.isEmpty(next)) {
                        AuthManager.getInstance(context).setTrueDjangoUsername(next);
                    }
                } else {
                    DHLog.e(TAG, String.format("Auth verification failed %s!", statusLine.toString()), this.mContext);
                }
            } catch (SocketTimeoutException e) {
                e = e;
                DHLog.e(TAG, "SocketTimeoutException", e, context);
                return i;
            } catch (ClientProtocolException e2) {
                e = e2;
                DHLog.e(TAG, "ClientProtocolException", e, this.mContext);
                return i;
            } catch (IOException e3) {
                e = e3;
                DHLog.e(TAG, "IOException", e, this.mContext);
                return i;
            }
        } catch (SocketTimeoutException e4) {
            e = e4;
            i = -1;
        } catch (ClientProtocolException e5) {
            e = e5;
            i = -1;
        } catch (IOException e6) {
            e = e6;
            i = -1;
        }
        return i;
    }

    public boolean sendInstallDetails(String str, String str2) {
        URI uri = getURI(INSTALL_DETAILS_URL);
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(uri);
        HttpGet httpGet2 = (HttpGet) signRequest(httpGet, str);
        httpGet2.addHeader("Cookie", "installation_details=" + str2);
        try {
            StatusLine statusLine = this.mClient.execute(httpGet2).getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                return true;
            }
            DHLog.e(TAG, String.format("Sending Install Details failed! %s", statusLine.toString()), this.mContext);
            return false;
        } catch (ClientProtocolException e) {
            DHLog.e(TAG, "ClientProtocolException", e, this.mContext);
            return false;
        } catch (IOException e2) {
            DHLog.e(TAG, "IOException", e2, this.mContext);
            return false;
        }
    }

    public static long getInstanceLength(HttpResponse httpResponse) {
        if (httpResponse != null) {
            Header[] headers = httpResponse.getHeaders("Content-Range");
            if (headers.length > 0) {
                String value = headers[0].getValue();
                try {
                    return Long.parseLong(value.split("/")[1]);
                } catch (NumberFormatException unused) {
                    DHLog.d(TAG, String.format("The HttpResponse contains an invalid instance-length: %s", value));
                }
            }
        }
        return -1L;
    }

    public static boolean isConnected(Context context) {
        return isConnected(context, false);
    }

    public static boolean isConnected(Context context, boolean z) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return false;
        }
        boolean isConnected = activeNetworkInfo.isConnected();
        return (isConnected && z) ? activeNetworkInfo.getType() == 1 : isConnected;
    }

    public static String getNetworkTypeName(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        return (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) ? "UNKNOWN" : activeNetworkInfo.getTypeName();
    }

    public static boolean isWiFiModeOnAndConnected(Context context) {
        return !SettingsUtils.getWiFiOnlyDownloads(context) || getNetworkTypeName(context).equals("WIFI");
    }

    /* loaded from: classes.dex */
    public static class ClientNotAuthorizedException extends Exception {
        private static final long serialVersionUID = 5476185625582592959L;

        public ClientNotAuthorizedException(String str) {
            super(str);
        }
    }

    public static String getFeaturedTabUrl(Context context) {
        String str;
        String featuredUrlBase = SettingsUtils.getFeaturedUrlBase(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (CompatibilityManager.isLargeScreenDevice(context)) {
            str = featuredUrlBase + "?device=tablet";
        } else {
            str = featuredUrlBase + "?device=phone";
        }
        return (str + "&width=" + displayMetrics.widthPixels) + "&height=" + displayMetrics.heightPixels;
    }

    public String getResponseAsString(HttpResponse httpResponse) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                } else {
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            DHLog.e(TAG, "IOException when trying to get HttpResponse as string", e, this.mContext);
            return null;
        } catch (NullPointerException unused) {
            DHLog.e(TAG, "NullPointerException when trying to get HttpResponse as string", this.mContext);
            return null;
        }
    }

    public static String getEtagValue(HttpResponse httpResponse) {
        String str = "";
        for (Header header : httpResponse.getHeaders("etag")) {
            String value = header.getValue();
            if (value != null) {
                str = value;
            }
        }
        return str;
    }

    public static int getEstimatedNetworkSpeed(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            int type = activeNetworkInfo.getType();
            int subtype = activeNetworkInfo.getSubtype();
            if (type == 1) {
                System.out.println("CONNECTED VIA WIFI");
                return Integer.MAX_VALUE;
            }
            if (type == 0) {
                switch (subtype) {
                    case 1:
                        return 100;
                    case 2:
                        return 75;
                    case 3:
                        return 3700;
                    case 4:
                        return 39;
                    case 5:
                        return 700;
                    case 6:
                        return 1000;
                    case 7:
                        return 75;
                    case 8:
                        return 8192;
                    case 9:
                        return 12288;
                    case 10:
                        return 1200;
                    case 11:
                        return 25;
                    case 12:
                        return 5120;
                    case 13:
                        return TarConstants.DEFAULT_BLKSIZE;
                    case 14:
                        return 1536;
                    case 15:
                        return 15360;
                    default:
                        return 0;
                }
            }
        }
        return 0;
    }
}
