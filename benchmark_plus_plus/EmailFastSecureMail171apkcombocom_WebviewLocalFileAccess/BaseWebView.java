package com.easilydo.mail.ui.webview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.easilydo.mail.helper.EdoAppHelper;
import com.easilydo.mail.logging.EdoLog;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringEscapeUtils;

/* loaded from: classes2.dex */
public class BaseWebView extends WebView {
    private static String a = "EDO";
    private EmailJsAPI b;
    private NativeToJsMessageQueue c;

    @NonNull
    private List<JsCallInfo> d;
    private WeakHashMap<String, ValueCallback<String>> e;
    private boolean f;
    private boolean g;
    public boolean hasBeenFocused;
    public boolean shouldDisableAutoScroll;
    protected WebChromeClient webChromeClient;
    protected EmailWebViewClient webViewClient;

    public boolean isReply() {
        return this.g;
    }

    public void setReply(boolean z) {
        this.g = z;
    }

    public BaseWebView(Context context) {
        super(context);
        this.d = new LinkedList();
        this.g = false;
        this.hasBeenFocused = false;
        this.shouldDisableAutoScroll = true;
    }

    public BaseWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.d = new LinkedList();
        this.g = false;
        this.hasBeenFocused = false;
        this.shouldDisableAutoScroll = true;
    }

    public BaseWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.d = new LinkedList();
        this.g = false;
        this.hasBeenFocused = false;
        this.shouldDisableAutoScroll = true;
    }

    public BaseWebView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.d = new LinkedList();
        this.g = false;
        this.hasBeenFocused = false;
        this.shouldDisableAutoScroll = true;
    }

    public BaseWebView(Context context, AttributeSet attributeSet, int i, boolean z) {
        super(context, attributeSet, i, z);
        this.d = new LinkedList();
        this.g = false;
        this.hasBeenFocused = false;
        this.shouldDisableAutoScroll = true;
    }

    public void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAppCacheEnabled(true);
        File attachmentsFolder = EdoAppHelper.getAttachmentsFolder();
        if (attachmentsFolder != null) {
            settings.setAppCachePath(attachmentsFolder.getAbsolutePath());
        }
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        setScrollContainer(false);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        this.webViewClient = new EmailWebViewClient();
        this.webChromeClient = new EmailWebChromeClient();
        setWebViewClient(this.webViewClient);
        setWebChromeClient(this.webChromeClient);
        this.c = new NativeToJsMessageQueue(this);
        this.b = new EmailJsAPI(this);
        addJavascriptInterface(this.b, a);
        this.e = new WeakHashMap<>();
    }

    public void loadDataWithBaseURL(String str, String str2, String str3) {
        loadDataWithBaseURL(str, str2, "text/html", "UTF-8", str3);
    }

    public void loadDataWithLocalResource(String str) {
        loadDataWithBaseURL("file:///android_asset/", str, null);
    }

    public void sendJavascript(String str, final ValueCallback<String> valueCallback) {
        JsCallInfo jsCallInfo;
        if (TextUtils.isEmpty(str)) {
            if (valueCallback != null) {
                valueCallback.onReceiveValue(null);
                return;
            }
            return;
        }
        ValueCallback<String> valueCallback2 = valueCallback != null ? new ValueCallback<String>() { // from class: com.easilydo.mail.ui.webview.BaseWebView.1
            @Override // android.webkit.ValueCallback
            /* renamed from: a, reason: merged with bridge method [inline-methods] */
            public void onReceiveValue(String str2) {
                valueCallback.onReceiveValue(StringEscapeUtils.unescapeJava(str2));
            }
        } : null;
        if (Build.VERSION.SDK_INT >= 19) {
            jsCallInfo = new JsCallInfo(str, valueCallback2);
        } else {
            String format = String.format("%d:%d", Long.valueOf(System.currentTimeMillis()), Integer.valueOf(str.hashCode()));
            JsCallInfo jsCallInfo2 = new JsCallInfo(str, format);
            this.e.put(format, valueCallback2);
            jsCallInfo = jsCallInfo2;
        }
        if (this.f) {
            this.c.addJavaScript(jsCallInfo);
            return;
        }
        this.d.add(jsCallInfo);
        EdoLog.w("EmailWebView", "execute js before page loaded:" + str);
    }

    public void onJsCallback(String str, String str2) {
        ValueCallback<String> valueCallback = this.e.get(str);
        if (valueCallback != null) {
            valueCallback.onReceiveValue(str2);
            this.e.remove(str);
        } else {
            EdoLog.d("EmailWebView", "Can't find callback:" + str);
        }
    }

    public boolean getState() {
        return this.f;
    }

    public void setState(boolean z) {
        this.f = z;
        if (!z || this.d.size() <= 0) {
            return;
        }
        Iterator<JsCallInfo> it2 = this.d.iterator();
        while (it2.hasNext()) {
            this.c.addJavaScript(it2.next());
        }
        this.d.clear();
    }

    @Override // android.webkit.WebView, android.view.View
    public void setOverScrollMode(int i) {
        try {
            super.setOverScrollMode(i);
        } catch (Exception | UnsatisfiedLinkError unused) {
        }
    }

    public void configureForEmailDisplay() {
        setScrollBarStyle(0);
        setLongClickable(true);
        setInitialScale(100);
        WebSettings settings = getSettings();
        settings.setCacheMode(2);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setOverScrollMode(2);
    }

    public void focus() {
        this.hasBeenFocused = true;
        requestFocus();
    }

    public void getElementInnerHtmlById(String str, ValueCallback<String> valueCallback) {
        sendJavascript(String.format("document.getElementById('%s').innerHTML;", str), valueCallback);
    }

    public void setElementInnerHtmlById(String str, String str2) {
        sendJavascript(String.format("document.getElementById('%s').innerHTML = '%s';", str, str2), null);
    }

    public void openHTTPInExtraBrowser(boolean z) {
        this.webViewClient.setOpenHTTPInExtraBrowser(z);
    }
}
