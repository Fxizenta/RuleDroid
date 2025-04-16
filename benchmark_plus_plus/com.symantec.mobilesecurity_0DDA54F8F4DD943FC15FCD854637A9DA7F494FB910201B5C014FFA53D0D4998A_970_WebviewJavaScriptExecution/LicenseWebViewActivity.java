package com.symantec.drm.malt.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.symantec.util.m;

/* loaded from: classes.dex */
public class LicenseWebViewActivity extends Activity {
    private String b;
    private LinearLayout c;
    private ProgressBar d;
    private WebView e;
    private String a = null;
    private int f = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int a(LicenseWebViewActivity licenseWebViewActivity, int i) {
        licenseWebViewActivity.f = 13;
        return 13;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        a aVar = null;
        super.onCreate(bundle);
        m.d("LicenseWebViewActivity", "onCreate");
        requestWindowFeature(1);
        setContentView(com.symantec.drm.malt.c.a);
        this.c = (LinearLayout) findViewById(com.symantec.drm.malt.b.b);
        this.d = (ProgressBar) findViewById(com.symantec.drm.malt.b.c);
        this.e = (WebView) findViewById(com.symantec.drm.malt.b.a);
        this.e.setFocusable(true);
        this.e.setLongClickable(true);
        this.e.setScrollBarStyle(33554432);
        this.e.getSettings().setJavaScriptEnabled(true);
        this.e.getSettings().setPluginsEnabled(true);
        this.e.getSettings().setSupportMultipleWindows(false);
        this.e.getSettings().setSupportZoom(true);
        this.e.getSettings().setBuiltInZoomControls(true);
        this.e.getSettings().setUseWideViewPort(true);
        this.e.getSettings().setLoadWithOverviewMode(true);
        this.e.getSettings().setSaveFormData(false);
        this.e.getSettings().setSavePassword(false);
        this.e.getSettings().setDomStorageEnabled(true);
        this.e.setWebViewClient(new c(this, aVar));
        this.e.setWebChromeClient(new b(this, aVar));
        this.e.clearCache(true);
        m.d("LicenseWebViewActivity", "WebView=" + this.e.toString() + " user-agent=" + this.e.getSettings().getUserAgentString());
        this.a = getIntent().getStringExtra("URL");
        this.b = getIntent().getStringExtra("ACTION");
        m.d("LicenseWebViewActivity", "initialUrl=" + this.a + " finishedActionIntent=" + this.b);
        if (true == TextUtils.isEmpty(this.a) || true == TextUtils.isEmpty(this.b)) {
            m.a("LicenseWebViewActivity", "invalid intent input");
            finish();
        } else {
            this.e.loadUrl(this.a);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        m.d("LicenseWebViewActivity", "onDestroy");
        this.e.setWebViewClient(null);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        m.d("LicenseWebViewActivity", "onPause");
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        m.d("LicenseWebViewActivity", "onResume");
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        m.d("LicenseWebViewActivity", "onStart");
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        m.d("LicenseWebViewActivity", "onStop");
        this.e.stopLoading();
        Intent intent = new Intent(this.b);
        intent.setPackage(getPackageName());
        intent.putExtra("RETURN_CODE", this.f);
        sendBroadcast(intent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        m.d("LicenseWebViewActivity", "onBackPressed: url=" + this.e.getUrl() + " canGoBack=" + this.e.canGoBack());
        if (!this.e.canGoBack()) {
            super.onBackPressed();
            return;
        }
        String url = this.e.getUrl();
        if (!TextUtils.isEmpty(url) && (true == url.contains("/estore/mobile/mobileThankYou") || true == url.contains("/estore/mobile/mobileGenericError"))) {
            super.onBackPressed();
        } else {
            this.e.goBack();
        }
    }
}
