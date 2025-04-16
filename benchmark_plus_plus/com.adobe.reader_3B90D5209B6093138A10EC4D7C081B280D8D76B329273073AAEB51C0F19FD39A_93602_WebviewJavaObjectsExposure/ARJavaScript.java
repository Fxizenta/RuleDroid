package com.adobe.reader.javascript;

import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.adobe.reader.filebrowser.ARFileBrowserUtils;
import com.adobe.reader.viewer.ARViewerActivity;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class ARJavaScript {
    private static final long JAVASCRIPT_TIMEOUT_IN_SECONDS = 10;
    private ARViewerActivity mContext;
    private AtomicBoolean mIsReady = new AtomicBoolean(false);
    private CountDownLatch mLatch = null;
    private WebView mWebView;

    public ARJavaScript(ARViewerActivity aRViewerActivity) {
        this.mContext = aRViewerActivity;
        this.mWebView = null;
        this.mWebView = new WebView(this.mContext);
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAppCacheEnabled(false);
        settings.setAppCacheMaxSize(0L);
        settings.setAppCachePath(null);
        settings.setBlockNetworkImage(true);
        settings.setBlockNetworkLoads(true);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(2);
        settings.setDatabaseEnabled(false);
        settings.setDatabasePath(null);
        settings.setDomStorageEnabled(false);
        settings.setGeolocationDatabasePath(null);
        settings.setGeolocationEnabled(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setLightTouchEnabled(false);
        settings.setLoadWithOverviewMode(false);
        settings.setLoadsImagesAutomatically(false);
        settings.setNeedInitialFocus(false);
        settings.setPluginState(WebSettings.PluginState.OFF);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setSupportMultipleWindows(false);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(false);
        if (Build.VERSION.SDK_INT >= 11) {
            settings.setAllowContentAccess(false);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }
        this.mWebView.addJavascriptInterface(new ARJavaScriptInterface(this), "_adobereader");
        this.mWebView.addJavascriptInterface(new ARJavaScriptApp(this.mContext), "_app");
        this.mWebView.addJavascriptInterface(new ARJavaScriptDoc(), "_doc");
        this.mWebView.addJavascriptInterface(new ARJavaScriptEScriptString(this.mContext), "_escriptString");
        this.mWebView.addJavascriptInterface(new ARJavaScriptEvent(), "_event");
        this.mWebView.addJavascriptInterface(new ARJavaScriptField(), "_field");
        this.mWebView.setWebViewClient(new WebViewClient() { // from class: com.adobe.reader.javascript.ARJavaScript.1
            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView, String str) {
                ARJavaScript.this.mIsReady.set(true);
                super.onPageFinished(webView, str);
            }
        });
        this.mWebView.loadUrl("file:///android_asset/javascript/index.html");
    }

    public static boolean shouldInitializeJavaScript() {
        return Build.VERSION.SDK_INT >= 17;
    }

    public void exec(String str) {
        if (this.mIsReady.get() && this.mLatch == null) {
            this.mLatch = new CountDownLatch(1);
            this.mWebView.loadUrl("javascript: try { " + str + " } catch (e) { window._adobereader.javaScript_error(e.toString()); } window._adobereader.javaScript_finished();");
            try {
                if (this.mLatch != null) {
                    this.mLatch.await(JAVASCRIPT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
            }
            this.mLatch = null;
        }
    }

    public boolean isReady() {
        return this.mIsReady.get();
    }

    public void javaScript_error(String str) {
        String str2 = "JavaScript Exception: " + str + ARFileBrowserUtils.EXTENSION_SEP;
        this.mContext.getDocLoaderManager().trackAnalyticsString("Forms: " + str + ARFileBrowserUtils.EXTENSION_SEP);
    }

    public void javaScript_finished() {
        if (this.mLatch != null) {
            this.mLatch.countDown();
        }
    }
}
