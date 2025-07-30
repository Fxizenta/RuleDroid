package com.ants360.yicamera.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.ants360.yicamera.config.f;
import com.ants360.yicamera.d;
import com.ants360.yicamera.view.LoadingWebView;
import com.kamivision.yismart.R;
import com.xiaoyi.base.ui.SimpleDialogFragment;
import com.xiaoyi.log.AntsLog;

/* loaded from: classes2.dex */
public class WebViewActivity extends SimpleBarRootActivity implements View.OnClickListener {
    private static final String TAG = "WebViewActivity";
    private d keyBoardListener;
    private LoadingWebView mLoadingWebViewFAQ = null;

    public static void launch(Activity activity, String str, String str2) {
        Intent intent = new Intent(activity, (Class<?>) WebViewActivity.class);
        intent.putExtra("INTENT_KEY_WEBLOAD_TITLE", str);
        intent.putExtra("path", str2);
        activity.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ants360.yicamera.activity.SimpleBarRootActivity, com.xiaoyi.base.ui.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_web_view);
        d dVar = new d(this);
        this.keyBoardListener = dVar;
        dVar.a();
        init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaoyi.base.ui.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.mLoadingWebViewFAQ.onPause();
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ants360.yicamera.activity.BaseActivity, com.xiaoyi.base.ui.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        this.mLoadingWebViewFAQ.onResume();
        super.onResume();
    }

    @Override // com.ants360.yicamera.activity.SimpleBarRootActivity
    public void onNavigationIconClick(View view) {
        onBackPressed();
    }

    @Override // com.xiaoyi.base.ui.BaseActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        LoadingWebView loadingWebView = this.mLoadingWebViewFAQ;
        if (loadingWebView != null && loadingWebView.canGoBack()) {
            this.mLoadingWebViewFAQ.goBack();
        } else {
            super.onBackPressed();
        }
    }

    protected void init() {
        this.mLoadingWebViewFAQ = (LoadingWebView) findViewById(R.id.webviewFaq);
        String dataString = getIntent().getDataString();
        String str = "https://api.yitechnology.com/homecamera/cloud_agreement_ios.html";
        if (!TextUtils.isEmpty(dataString)) {
            String substring = dataString.substring(6);
            if (substring.contains("teamOfUse")) {
                str = f.i() ? com.ants360.yicamera.constants.f.aS : f.j() ? com.ants360.yicamera.constants.f.aR : com.ants360.yicamera.constants.f.aQ;
            } else if (substring.contains("privacyPolicy")) {
                str = f.i() ? com.ants360.yicamera.constants.f.aP : f.j() ? com.ants360.yicamera.constants.f.aO : com.ants360.yicamera.constants.f.aN;
            } else if (!substring.contains("cloud_agreement")) {
                str = substring;
            } else if (f.i()) {
                str = com.ants360.yicamera.constants.f.U;
            } else {
                f.r();
            }
        } else {
            str = getIntent().getStringExtra("path");
            if (str != null && !str.toLowerCase().startsWith("http")) {
                str = str.contains("443") ? "https://" + str : "http://" + str;
            }
        }
        if (str == null) {
            return;
        }
        AntsLog.d(TAG, "show web:" + str);
        this.mLoadingWebViewFAQ.setWebChromeClient(new WebChromeClient() { // from class: com.ants360.yicamera.activity.WebViewActivity.1
            @Override // android.webkit.WebChromeClient
            public void onReceivedTitle(WebView webView, String str2) {
                super.onReceivedTitle(webView, str2);
                WebViewActivity.this.setTitle(str2);
            }

            @Override // android.webkit.WebChromeClient
            public boolean onJsConfirm(WebView webView, String str2, String str3, final JsResult jsResult) {
                if (!WebViewActivity.this.isForegroundRunning) {
                    jsResult.cancel();
                    return true;
                }
                WebViewActivity.this.getHelper().a(str3, WebViewActivity.this.getString(R.string.cancel), WebViewActivity.this.getString(R.string.ok), new com.xiaoyi.base.ui.d() { // from class: com.ants360.yicamera.activity.WebViewActivity.1.1
                    @Override // com.xiaoyi.base.ui.d
                    public void a(SimpleDialogFragment simpleDialogFragment) {
                        jsResult.cancel();
                    }

                    @Override // com.xiaoyi.base.ui.d
                    public void b(SimpleDialogFragment simpleDialogFragment) {
                        jsResult.confirm();
                    }
                });
                return true;
            }
        });
        this.mLoadingWebViewFAQ.setWebViewClient(new WebViewClient() { // from class: com.ants360.yicamera.activity.WebViewActivity.2
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str2) {
                if (TextUtils.isEmpty(str2)) {
                    return true;
                }
                if (str2.startsWith("taobao://")) {
                    try {
                        WebViewActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str2)));
                        return true;
                    } catch (Exception e) {
                        AntsLog.d(WebViewActivity.TAG, e.toString());
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(webView, str2);
            }
        });
        this.mLoadingWebViewFAQ.loadUrl(str);
    }

    public void addJavascriptInterface(Object obj, String str) {
        this.mLoadingWebViewFAQ.addJavascriptInterface(obj, str);
    }
}
