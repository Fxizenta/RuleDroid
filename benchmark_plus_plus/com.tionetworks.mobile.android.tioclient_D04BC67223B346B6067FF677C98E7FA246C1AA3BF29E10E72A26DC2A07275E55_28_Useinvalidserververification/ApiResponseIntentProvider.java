package com.tionetworks.mobile.android.tioclient.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import com.tionetworks.mobile.android.api.enums.ApiActionTask;
import com.tionetworks.mobile.android.api.enums.ApiIntentExtraKey;
import com.tionetworks.mobile.android.api.service.handler.ApiServiceHandler;
import com.tionetworks.mobile.android.tioclient.TioClientApplication;
import com.tionetworks.mobile.android.tioclient.library.R;
import com.tionetworks.mobile.android.tioclient.utilities.AuthenticationManager;
import com.tionetworks.mobile.android.tioclient.utilities.AuthenticationManagerImpl;
import com.tionetworks.mobile.android.tioclient.utilities.DateTimeUtility;
import com.tionetworks.mobile.android.tioclient.utilities.DateTimeUtilityImpl;
import com.tionetworks.mobile.android.tioclient.utilities.DebugUtils;
import com.tionetworks.mobile.android.tioclient.utilities.MySSLSocketFactory;
import com.tionetworks.mobile.android.tioclient.utilities.NetworkUtility;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import roboguice.util.Ln;

/* loaded from: classes.dex */
public class ApiResponseIntentProvider {
    DateTimeUtility dateTimeUtility = new DateTimeUtilityImpl();
    private NetworkUtility networkUtility;

    @Inject
    public ApiResponseIntentProvider(NetworkUtility networkUtility) {
        this.networkUtility = networkUtility;
    }

    public Intent generateResponseIntent(Context context, ApiActionTask method, ApiServiceHandler serviceHandler) {
        return generateResponseIntent(context, serviceHandler.getServiceApiUrl(), method, serviceHandler);
    }

    public Intent generateResponseIntent(Context context, String serviceApiUrl, ApiActionTask method, ApiServiceHandler serviceHandler) {
        Intent responseIntent = null;
        if (this.networkUtility.isNetworkAvailable(context)) {
            try {
                if (serviceHandler != null) {
                    Ln.d("serviceApiUrl = %s", serviceApiUrl);
                    JSONObject apiRequest = serviceHandler.generateJsonRequest();
                    Ln.d("apiRequest = %s", apiRequest.toString(4));
                    SchemeRegistry schemeRegistry = new SchemeRegistry();
                    SSLContext sslCtx = SSLContext.getInstance("TLS");
                    X509TrustManager tm = new X509TrustManager() { // from class: com.tionetworks.mobile.android.tioclient.service.ApiResponseIntentProvider.1
                        @Override // javax.net.ssl.X509TrustManager
                        public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    };
                    sslCtx.init(null, new TrustManager[]{tm}, null);
                    schemeRegistry.register(new Scheme("https", new MySSLSocketFactory(sslCtx), 443));
                    HttpParams params = new BasicHttpParams();
                    SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);
                    DefaultHttpClient defaultHttpClient = new DefaultHttpClient(mgr, params);
                    defaultHttpClient.setRoutePlanner(new DefaultHttpRoutePlanner(schemeRegistry));
                    HttpPost post = new HttpPost(serviceApiUrl);
                    StringEntity entity = new StringEntity(apiRequest.toString(), "UTF-8");
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    HttpResponse response = defaultHttpClient.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    String responseString = EntityUtils.toString(response.getEntity());
                    DebugUtils.longDebug(responseString);
                    JSONObject jsonResponse = (JSONObject) new JSONTokener(responseString).nextValue();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                        updateAuthToken(jsonResponse);
                        responseIntent = serviceHandler.generateSuccessResponseIntent(jsonResponse);
                        responseIntent.putExtra(ApiIntentExtraKey.isSuccess.toValue(), true);
                    } else {
                        responseIntent = serviceHandler.generateFailedResponseIntent(jsonResponse, context.getString(R.string.error_http_response_unknown));
                    }
                } else {
                    Intent responseIntent2 = new Intent();
                    try {
                        responseIntent2.putExtra(ApiIntentExtraKey.isSuccess.toValue(), false);
                        responseIntent2.putExtra("message", context.getString(R.string.error_service_method_unknown));
                        responseIntent = responseIntent2;
                    } catch (Exception e) {
                        e = e;
                        responseIntent = responseIntent2;
                        e.printStackTrace();
                        Ln.e(e, "Encountered unrecoverable exception", new Object[0]);
                        if (responseIntent == null) {
                            responseIntent = new Intent();
                        }
                        responseIntent.putExtra(ApiIntentExtraKey.isSuccess.toValue(), false);
                        responseIntent.putExtra("message", e.getLocalizedMessage());
                        responseIntent.setAction(method.toString());
                        return responseIntent;
                    }
                }
            } catch (Exception e2) {
                e = e2;
            }
        } else {
            responseIntent = new Intent();
            responseIntent.putExtra(ApiIntentExtraKey.isSuccess.toValue(), false);
            responseIntent.putExtra("message", context.getString(R.string.error_network_unavailable));
        }
        responseIntent.setAction(method.toString());
        return responseIntent;
    }

    private void updateAuthToken(JSONObject jsonResponse) {
        try {
            AuthenticationManager authenticationManager = AuthenticationManagerImpl.getInstance(TioClientApplication.getInstance());
            JSONObject d = jsonResponse.getJSONObject("d");
            JSONObject header = d.getJSONObject("Header");
            try {
                JSONObject jsonTioAuthenticationToken = header.getJSONObject("AuthenticationToken");
                String authenticationToken = jsonTioAuthenticationToken.getString("Token");
                DateTime responseDate = this.dateTimeUtility.parseServerDateString(header.getString("ResponseDateTimeUtc"));
                DateTime tokenExpiration = this.dateTimeUtility.parseServerDateString(jsonTioAuthenticationToken.getString("ExpirationDateUTC"));
                Log.i("Session Retrieved", "Expiring on " + tokenExpiration.toString());
                authenticationManager.saveAuthenticationToken(authenticationToken, tokenExpiration.getMillis(), responseDate.getMillis());
            } catch (Exception e) {
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }
}
