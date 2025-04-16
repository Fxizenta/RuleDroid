package com.estrongs.android.pop.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.estrongs.android.pop.R;
import com.estrongs.android.pop.esclasses.ESWebView;
import com.estrongs.android.pop.esclasses.n;
import com.facebook.ads.AudienceNetworkActivity;

/* loaded from: classes.dex */
public class HelpActivity extends n {
    private ESWebView c;
    private final String a = AudienceNetworkActivity.WEBVIEW_MIME_TYPE;
    private final String b = AudienceNetworkActivity.WEBVIEW_ENCODING;
    private final String[] d = {"index.html"};
    private final int[] e = new int[0];
    private int f = 0;

    @Override // com.estrongs.android.pop.esclasses.n, com.estrongs.android.pop.esclasses.b, android.support.v7.app.AppCompatActivity, android.support.v4.app.j, android.support.v4.app.af, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.help_title);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.help_page);
        this.c = (ESWebView) findViewById(R.id.help);
        this.c.setFocusableInTouchMode(true);
        this.c.getSettings().setJavaScriptEnabled(true);
        this.c.removeJavascriptInterface("accessibility");
        this.c.removeJavascriptInterface("accessibilityTraversal");
        this.c.removeJavascriptInterface("searchBoxJavaBridge_");
        this.c.setWebViewClient(new WebViewClient() { // from class: com.estrongs.android.pop.app.HelpActivity.1
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                return super.shouldOverrideUrlLoading(webView, str);
            }
        });
        String str = "http://www.estrongs.com/eshelp/en/ES_File_Explorer_User_Manual3.0.htm";
        if (com.estrongs.android.pop.utils.h.b()) {
            str = "http://www.estrongs.com/eshelp/cn/ES_File_Explorer_User_Manual3.0.htm";
        }
        a(str);
    }

    @Override // com.estrongs.android.pop.esclasses.b, android.support.v7.app.AppCompatActivity, android.support.v4.app.j, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean a(String str) {
        this.c.clearHistory();
        this.c.clearCache(true);
        this.c.loadUrl(str);
        return true;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && keyEvent.getAction() == 0) {
            if (this.c.canGoBack()) {
                this.c.stopLoading();
                this.c.goBack();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }
}
