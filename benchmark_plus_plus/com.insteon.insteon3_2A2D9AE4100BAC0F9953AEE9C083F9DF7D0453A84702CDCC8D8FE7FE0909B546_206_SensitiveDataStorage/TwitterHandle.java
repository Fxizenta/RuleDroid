package com.androidquery.auth;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.androidquery.AQuery;
import com.androidquery.WebDialog;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import java.net.HttpURLConnection;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import org.apache.http.HttpRequest;

/* loaded from: classes2.dex */
public class TwitterHandle extends AccountHandle {
    private static final String CALLBACK_URI = "twitter://callback";
    private static final String CANCEL_URI = "twitter://cancel";
    private static final String OAUTH_ACCESS_TOKEN = "https://api.twitter.com/oauth/access_token";
    private static final String OAUTH_AUTHORIZE = "https://api.twitter.com/oauth/authorize";
    private static final String OAUTH_REQUEST_TOKEN = "https://api.twitter.com/oauth/request_token";
    private static final String TW_SECRET = "aq.tw.secret";
    private static final String TW_TOKEN = "aq.tw.token";
    private Activity act;
    private CommonsHttpOAuthConsumer consumer;
    private WebDialog dialog;
    private CommonsHttpOAuthProvider provider;
    private String token = fetchToken(TW_TOKEN);
    private String secret = fetchToken(TW_SECRET);

    public TwitterHandle(Activity act, String consumerKey, String consumerSecret) {
        this.act = act;
        this.consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        if (this.token != null && this.secret != null) {
            this.consumer.setTokenWithSecret(this.token, this.secret);
        }
        this.provider = new CommonsHttpOAuthProvider(OAUTH_REQUEST_TOKEN, OAUTH_ACCESS_TOKEN, OAUTH_AUTHORIZE);
    }

    public String getToken() {
        return this.token;
    }

    public String getSecret() {
        return this.secret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss() {
        if (this.dialog != null) {
            new AQuery(this.act).dismiss(this.dialog);
            this.dialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show() {
        if (this.dialog != null) {
            new AQuery(this.act).show(this.dialog);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void failure() {
        dismiss();
        failure(this.act, 401, "cancel");
    }

    @Override // com.androidquery.auth.AccountHandle
    protected void auth() {
        Task task = new Task(this, null);
        task.execute(new String[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Task extends AsyncTask<String, String, String> implements DialogInterface.OnCancelListener, Runnable {
        private AbstractAjaxCallback<?, ?> cb;

        private Task() {
        }

        /* synthetic */ Task(TwitterHandle twitterHandle, Task task) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(String... params) {
            try {
                String url = TwitterHandle.this.provider.retrieveRequestToken(TwitterHandle.this.consumer, TwitterHandle.CALLBACK_URI);
                return url;
            } catch (Exception e) {
                AQUtility.report(e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String url) {
            if (url == null) {
                TwitterHandle.this.failure();
                return;
            }
            TwitterHandle.this.dialog = new WebDialog(TwitterHandle.this.act, url, new TwWebViewClient(TwitterHandle.this, null));
            TwitterHandle.this.dialog.setOnCancelListener(this);
            TwitterHandle.this.show();
            TwitterHandle.this.dialog.load();
        }

        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface arg0) {
            TwitterHandle.this.failure();
        }

        @Override // java.lang.Runnable
        public void run() {
            TwitterHandle.this.auth(this.cb);
        }
    }

    public void authenticate(boolean refreshToken) {
        if (!refreshToken && this.token != null && this.secret != null) {
            authenticated(this.secret, this.token);
        } else {
            auth();
        }
    }

    protected void authenticated(String secret, String token) {
    }

    private String fetchToken(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this.act).getString(key, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void storeToken(String key1, String token1, String key2, String token2) {
        PreferenceManager.getDefaultSharedPreferences(this.act).edit().putString(key1, token1).putString(key2, token2).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String extract(String url, String param) {
        Uri uri = Uri.parse(url);
        String value = uri.getQueryParameter(param);
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Task2 extends AsyncTask<String, String, String> {
        private Task2() {
        }

        /* synthetic */ Task2(TwitterHandle twitterHandle, Task2 task2) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(String... params) {
            try {
                TwitterHandle.this.provider.retrieveAccessToken(TwitterHandle.this.consumer, params[0]);
                return "";
            } catch (Exception e) {
                AQUtility.report(e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String url) {
            if (url == null) {
                TwitterHandle.this.failure();
                TwitterHandle.this.authenticated(null, null);
                return;
            }
            TwitterHandle.this.token = TwitterHandle.this.consumer.getToken();
            TwitterHandle.this.secret = TwitterHandle.this.consumer.getTokenSecret();
            AQUtility.debug("token", TwitterHandle.this.token);
            AQUtility.debug("secret", TwitterHandle.this.secret);
            TwitterHandle.this.storeToken(TwitterHandle.TW_TOKEN, TwitterHandle.this.token, TwitterHandle.TW_SECRET, TwitterHandle.this.secret);
            TwitterHandle.this.dismiss();
            TwitterHandle.this.success(TwitterHandle.this.act);
            TwitterHandle.this.authenticated(TwitterHandle.this.secret, TwitterHandle.this.token);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class TwWebViewClient extends WebViewClient {
        private TwWebViewClient() {
        }

        /* synthetic */ TwWebViewClient(TwitterHandle twitterHandle, TwWebViewClient twWebViewClient) {
            this();
        }

        private boolean checkDone(String url) {
            if (url.startsWith(TwitterHandle.CALLBACK_URI)) {
                String verf = TwitterHandle.this.extract(url, "oauth_verifier");
                TwitterHandle.this.dismiss();
                Task2 task = new Task2(TwitterHandle.this, null);
                task.execute(verf);
                return true;
            }
            if (!url.startsWith(TwitterHandle.CANCEL_URI)) {
                return false;
            }
            TwitterHandle.this.failure();
            return true;
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return checkDone(url);
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            AQUtility.debug("started", url);
            if (!checkDone(url)) {
                super.onPageStarted(view, url, favicon);
            }
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            AQUtility.debug("finished", url);
            super.onPageFinished(view, url);
            TwitterHandle.this.show();
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            TwitterHandle.this.failure();
        }
    }

    @Override // com.androidquery.auth.AccountHandle
    public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
        int code = status.getCode();
        return code == 400 || code == 401;
    }

    @Override // com.androidquery.auth.AccountHandle
    public boolean reauth(AbstractAjaxCallback<?, ?> cb) {
        this.token = null;
        this.secret = null;
        storeToken(TW_TOKEN, null, TW_SECRET, null);
        Task task = new Task(this, null);
        task.cb = cb;
        AQUtility.post(cb);
        return false;
    }

    @Override // com.androidquery.auth.AccountHandle
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request) {
        AQUtility.debug("apply token", cb.getUrl());
        try {
            this.consumer.sign(request);
        } catch (Exception e) {
            AQUtility.report(e);
        }
    }

    @Override // com.androidquery.auth.AccountHandle
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpURLConnection conn) {
        AQUtility.debug("apply token multipart", cb.getUrl());
        DefaultOAuthConsumer defaultOAuthConsumer = new DefaultOAuthConsumer(this.consumer.getConsumerKey(), this.consumer.getConsumerSecret());
        defaultOAuthConsumer.setTokenWithSecret(this.consumer.getToken(), this.consumer.getTokenSecret());
        try {
            defaultOAuthConsumer.sign(conn);
        } catch (Exception e) {
            AQUtility.report(e);
        }
    }

    @Override // com.androidquery.auth.AccountHandle
    public boolean authenticated() {
        return (this.token == null || this.secret == null) ? false : true;
    }

    @Override // com.androidquery.auth.AccountHandle
    public void unauth() {
        this.token = null;
        this.secret = null;
        CookieSyncManager.createInstance(this.act);
        CookieManager.getInstance().removeAllCookie();
        storeToken(TW_TOKEN, null, TW_SECRET, null);
    }
}
