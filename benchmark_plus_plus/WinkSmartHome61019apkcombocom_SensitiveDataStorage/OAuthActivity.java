package com.twitter.sdk.android.core.identity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.twitter.sdk.android.core.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.i;
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class OAuthActivity extends Activity {

    /* renamed from: a, reason: collision with root package name */
    TwitterAuthToken f6337a;

    /* renamed from: b, reason: collision with root package name */
    com.twitter.sdk.android.core.internal.oauth.d f6338b;
    private ProgressBar c;
    private WebView d;
    private TwitterAuthConfig e;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        boolean z;
        super.onCreate(bundle);
        setContentView(R.layout.tw__activity_oauth);
        this.c = (ProgressBar) findViewById(R.id.tw__spinner);
        this.d = (WebView) findViewById(R.id.tw__web_view);
        if (bundle != null) {
            z = bundle.getBoolean("progress", false);
        } else {
            z = true;
        }
        this.c.setVisibility(z ? 0 : 8);
        this.e = (TwitterAuthConfig) getIntent().getParcelableExtra("auth_config");
        i e = i.e();
        this.f6338b = new com.twitter.sdk.android.core.internal.oauth.d(e, e.f(), new com.twitter.sdk.android.core.internal.a());
        io.fabric.sdk.android.c.a();
        com.twitter.sdk.android.core.internal.oauth.d dVar = this.f6338b;
        com.twitter.sdk.android.core.c<OAuthResponse> cVar = new com.twitter.sdk.android.core.c<OAuthResponse>() { // from class: com.twitter.sdk.android.core.identity.OAuthActivity.1
            @Override // com.twitter.sdk.android.core.c
            public final void a(com.twitter.sdk.android.core.f<OAuthResponse> fVar) {
                OAuthActivity.this.f6337a = fVar.f6331a.f6372a;
                com.twitter.sdk.android.core.internal.oauth.d dVar2 = OAuthActivity.this.f6338b;
                TwitterAuthToken twitterAuthToken = OAuthActivity.this.f6337a;
                String[] strArr = {"oauth", "authorize"};
                Uri.Builder buildUpon = Uri.parse(dVar2.c.f6366a).buildUpon();
                for (int i = 0; i < 2; i++) {
                    buildUpon.appendPath(strArr[i]);
                }
                String uri = buildUpon.appendQueryParameter("oauth_token", twitterAuthToken.f6323a).build().toString();
                io.fabric.sdk.android.c.a();
                OAuthActivity.a(OAuthActivity.this.d, new a(com.twitter.sdk.android.core.internal.oauth.d.a(OAuthActivity.this.e)), uri);
            }

            @Override // com.twitter.sdk.android.core.c
            public final void a(TwitterException twitterException) {
                OAuthActivity.this.a(twitterException);
            }
        };
        TwitterAuthConfig twitterAuthConfig = dVar.f6381b.c;
        com.twitter.sdk.android.core.internal.oauth.d.a(twitterAuthConfig, null, com.twitter.sdk.android.core.internal.oauth.d.a(twitterAuthConfig), "POST", dVar.c.f6366a + "/oauth/request_token", null);
        dVar.a(cVar);
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        if (this.c.getVisibility() == 0) {
            bundle.putBoolean("progress", true);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        a(new TwitterAuthException("Authorization failed, request was canceled."));
    }

    final void a(TwitterException twitterException) {
        new Intent().putExtra("auth_error", twitterException);
        setResult(1);
        finish();
    }

    static void a(WebView webView, WebViewClient webViewClient, String str) {
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(str);
        webView.setVisibility(4);
    }

    final void a(WebViewException webViewException) {
        io.fabric.sdk.android.c.a().a("Twitter", "OAuth web view completed with an error", webViewException);
        a(new TwitterAuthException("OAuth web view completed with an error", webViewException));
    }

    final void a() {
        this.d.stopLoading();
        b();
    }

    final void b() {
        this.c.setVisibility(8);
    }

    /* loaded from: classes2.dex */
    private class a extends WebViewClient {

        /* renamed from: b, reason: collision with root package name */
        private final String f6342b;

        public a(String str) {
            this.f6342b = str;
        }

        @Override // android.webkit.WebViewClient
        public final void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            OAuthActivity.this.b();
            OAuthActivity.this.d.setVisibility(0);
        }

        @Override // android.webkit.WebViewClient
        public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (this.f6342b != null && str.startsWith(this.f6342b)) {
                TreeMap<String, String> a2 = io.fabric.sdk.android.services.network.h.a(URI.create(str), false);
                Bundle bundle = new Bundle(a2.size());
                for (Map.Entry<String, String> entry : a2.entrySet()) {
                    bundle.putString(entry.getKey(), entry.getValue());
                }
                final OAuthActivity oAuthActivity = OAuthActivity.this;
                io.fabric.sdk.android.c.a();
                if (bundle.getString("oauth_verifier") != null) {
                    io.fabric.sdk.android.c.a();
                    com.twitter.sdk.android.core.internal.oauth.d dVar = oAuthActivity.f6338b;
                    com.twitter.sdk.android.core.c<OAuthResponse> cVar = new com.twitter.sdk.android.core.c<OAuthResponse>() { // from class: com.twitter.sdk.android.core.identity.OAuthActivity.2
                        @Override // com.twitter.sdk.android.core.c
                        public final void a(com.twitter.sdk.android.core.f<OAuthResponse> fVar) {
                            Intent intent = new Intent();
                            OAuthResponse oAuthResponse = fVar.f6331a;
                            intent.putExtra("screen_name", oAuthResponse.f6373b);
                            intent.putExtra("user_id", oAuthResponse.c);
                            intent.putExtra("tk", oAuthResponse.f6372a.f6323a);
                            intent.putExtra("ts", oAuthResponse.f6372a.f6324b);
                            OAuthActivity.this.setResult(-1, intent);
                            OAuthActivity.this.finish();
                        }

                        @Override // com.twitter.sdk.android.core.c
                        public final void a(TwitterException twitterException) {
                            OAuthActivity.this.a(twitterException);
                        }
                    };
                    com.twitter.sdk.android.core.internal.oauth.d.a(dVar.f6381b.c, oAuthActivity.f6337a, null, "POST", dVar.c.f6366a + "/oauth/access_token", null);
                    dVar.a(cVar);
                } else {
                    io.fabric.sdk.android.c.a().a("Twitter", "Failed to get authorization, bundle incomplete " + bundle, (Throwable) null);
                    oAuthActivity.a(new TwitterAuthException("Failed to get authorization, bundle incomplete"));
                }
                OAuthActivity.this.a();
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, str);
        }

        @Override // android.webkit.WebViewClient
        public final void onReceivedError(WebView webView, int i, String str, String str2) {
            super.onReceivedError(webView, i, str, str2);
            OAuthActivity.this.a(new WebViewException(i, str, str2));
            OAuthActivity.this.a();
        }

        @Override // android.webkit.WebViewClient
        public final void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            super.onReceivedSslError(webView, sslErrorHandler, sslError);
            OAuthActivity.this.a(new WebViewException(sslError.getPrimaryError(), null, null));
            OAuthActivity.this.a();
        }
    }
}
