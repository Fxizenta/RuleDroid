package com.duolingo.tinycards.webviewtinycards;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import me.leolin.shortcutbadger.ShortcutBadger;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 2;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    FrameLayout mContainer;
    private ValueCallback<Uri[]> mFilePathCallback;
    Snackbar mGoogleFailSnackbar;
    WebView mPopupWebView;
    WebChromeClient mWebChromeClient;
    WebView mWebView;
    WebViewClient mWebViewClient;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(com.duolingo.tinycards.R.layout.activity_main);
        this.mContainer = (FrameLayout) findViewById(com.duolingo.tinycards.R.id.webview_frame);
        this.mGoogleFailSnackbar = Snackbar.make(this.mContainer, getString(com.duolingo.tinycards.R.string.google_signin_failed), 0);
        this.mWebView = (WebView) findViewById(com.duolingo.tinycards.R.id.webview);
        this.mWebViewClient = new TinycardsWebViewClient(this);
        this.mWebChromeClient = new TinycardsWebChromeClient(this, this.mWebViewClient);
        Button button = (Button) findViewById(com.duolingo.tinycards.R.id.reload_button);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.duolingo.tinycards.webviewtinycards.MainActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((TinycardsWebViewClient) MainActivity.this.mWebViewClient).resetError();
            }
        });
        AssetManager am = getAssets();
        Typeface medium = Typeface.createFromAsset(am, "fonts/DINNextRoundedLTPro-Medium.otf");
        Typeface regular = Typeface.createFromAsset(am, "fonts/DINNextRoundedLTPro-Regular.otf");
        ((TextView) findViewById(com.duolingo.tinycards.R.id.connect_to_internet_title)).setTypeface(medium);
        ((TextView) findViewById(com.duolingo.tinycards.R.id.connect_to_internet)).setTypeface(regular);
        setupMainWebView(this.mWebView, this.mWebViewClient, this.mWebChromeClient);
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
            Uri data = intent.getData();
            this.mWebView.loadUrl(data.toString());
        } else {
            this.mWebView.loadUrl(BuildConfig.WEBVIEW_URL);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        clearNotifications();
        ReminderJobService.schedule(this);
        SharedPreferences settings = getSharedPreferences(ReminderJobService.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ReminderJobService.PREF_DAYS_AVOIDED, 0);
        editor.apply();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getPopupWebView() != null) {
            this.mPopupWebView.setVisibility(8);
            this.mContainer.removeView(this.mPopupWebView);
            setPopupWebView(null);
            return true;
        }
        if (keyCode == 4 && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String dataString;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.getSignInAccount() != null) {
                GoogleSignInAccount account = result.getSignInAccount();
                new AuthenticateGoogleTask().execute(account.getAccount());
                return;
            } else {
                this.mGoogleFailSnackbar.show();
                return;
            }
        }
        if (requestCode == 1 && this.mFilePathCallback != null) {
            Uri[] results = null;
            if (resultCode == -1 && (dataString = data.getDataString()) != null) {
                results = new Uri[]{Uri.parse(dataString)};
            }
            this.mFilePathCallback.onReceiveValue(results);
            this.mFilePathCallback = null;
        }
    }

    public void setPopupWebView(WebView webView) {
        this.mPopupWebView = webView;
    }

    public WebView getPopupWebView() {
        return this.mPopupWebView;
    }

    private void clearNotifications() {
        ShortcutBadger.removeCount(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        notificationManager.cancel(1);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void setupMainWebView(WebView webview, WebViewClient webClient, WebChromeClient chromeClient) {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(2);
        }
        String userAgent = String.format("%s %d", getString(com.duolingo.tinycards.R.string.user_agent_name), 9);
        webSettings.setUserAgentString(userAgent);
        webview.addJavascriptInterface(new JSInterface(this), "Android");
        webview.setWebViewClient(webClient);
        this.mWebView.setWebChromeClient(chromeClient);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.mGoogleFailSnackbar.show();
    }

    /* loaded from: classes.dex */
    private class AuthenticateGoogleTask extends AsyncTask<Account, Void, String> {
        private AuthenticateGoogleTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // io.fabric.sdk.android.services.concurrency.AsyncTask
        public String doInBackground(Account... accounts) {
            Account account = accounts[0];
            try {
                String token = GoogleAuthUtil.getToken(MainActivity.this, account, "oauth2:profile email");
                return token;
            } catch (GoogleAuthException | IOException e) {
                MainActivity.this.mGoogleFailSnackbar.show();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // io.fabric.sdk.android.services.concurrency.AsyncTask
        public void onPostExecute(String token) {
            super.onPostExecute((AuthenticateGoogleTask) token);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            try {
                URI currentUrl = new URI(MainActivity.this.mWebView.getUrl());
                String returnUrl = URLEncoder.encode(currentUrl.toString(), HttpRequest.CHARSET_UTF8);
                String authUrl = String.format("%stinydroid/login?referrer=%s", BuildConfig.WEBVIEW_URL, returnUrl);
                String googleTokenCookie = String.format("googleToken=%s", token);
                cookieManager.setCookie(BuildConfig.WEBVIEW_URL, googleTokenCookie);
                MainActivity.this.mWebView.loadUrl(authUrl);
            } catch (UnsupportedEncodingException | URISyntaxException e) {
                MainActivity.this.mGoogleFailSnackbar.show();
            }
        }
    }
}
