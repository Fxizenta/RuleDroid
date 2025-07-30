package com.viatom.baselib.mvvm.web;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.blankj.utilcode.util.LogUtils;
import com.itextpdf.text.xml.xmp.DublinCoreProperties;
import com.viatom.baselib.R;
import com.viatom.baselib.databinding.ActivityWebviewBinding;
import com.viatom.baselib.databinding.BaseLayoutSecondToolbarBinding;
import com.viatom.baselib.mvvm.BaseBindActivity;
import com.vihealth.db.LogUtil;
import im.delight.android.webview.AdvancedWebView;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: WebViewActivity.kt */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003B\u0005¢\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0014J\u0006\u0010\n\u001a\u00020\u000bJ\u0012\u0010\f\u001a\u00020\u000b2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0014JB\u0010\u000f\u001a\u00020\u000b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00062\b\u0010\u0011\u001a\u0004\u0018\u00010\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00062\b\u0010\u0016\u001a\u0004\u0018\u00010\u0006H\u0016J\u0012\u0010\u0017\u001a\u00020\u000b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0006H\u0016J$\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0019\u001a\u00020\t2\b\u0010\u001a\u001a\u0004\u0018\u00010\u00062\b\u0010\u001b\u001a\u0004\u0018\u00010\u0006H\u0016J\u0012\u0010\u001c\u001a\u00020\u000b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0006H\u0016J\u001c\u0010\u001d\u001a\u00020\u000b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00062\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010 \u001a\u00020!H\u0014J\b\u0010\"\u001a\u00020\tH\u0014J\u0012\u0010#\u001a\u00020\u000b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0006H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006$"}, d2 = {"Lcom/viatom/baselib/mvvm/web/WebViewActivity;", "Lcom/viatom/baselib/mvvm/BaseBindActivity;", "Lcom/viatom/baselib/databinding/ActivityWebviewBinding;", "Lim/delight/android/webview/AdvancedWebView$Listener;", "()V", "webTitle", "", "webUrl", "getLayoutId", "", "initView", "", "initialize", "savedInstanceState", "Landroid/os/Bundle;", "onDownloadRequested", "url", "suggestedFilename", "mimeType", "contentLength", "", "contentDisposition", "userAgent", "onExternalPageRequest", "onPageError", "errorCode", DublinCoreProperties.DESCRIPTION, "failingUrl", "onPageFinished", "onPageStarted", "favicon", "Landroid/graphics/Bitmap;", "setIsHomeActivity", "", "setStatusBarColor", "shareUrl", "baselib_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes4.dex */
public final class WebViewActivity extends BaseBindActivity<ActivityWebviewBinding> implements AdvancedWebView.Listener {
    private String webTitle = "";
    private String webUrl;

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onExternalPageRequest(String url) {
    }

    @Override // com.viatom.baselib.mvvm.BaseActivity
    protected boolean setIsHomeActivity() {
        return false;
    }

    @Override // com.viatom.baselib.mvvm.BaseActivity
    protected int setStatusBarColor() {
        return 0;
    }

    @Override // com.viatom.baselib.mvvm.BaseActivity
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override // com.viatom.baselib.mvvm.BaseActivity
    protected void initialize(Bundle savedInstanceState) {
        this.webUrl = getIntent().getStringExtra(WebViewActivityKt.WEB_URL);
        Bundle extras = getIntent().getExtras();
        String string = extras != null ? extras.getString(WebViewActivityKt.WEB_TITLE, "") : null;
        this.webTitle = string != null ? string : "";
        String str = this.webUrl;
        if (str == null || str.length() == 0) {
            LogUtils.e("web url is null or empty!!!");
            finish();
        }
        initView();
        LogUtil.e$default(LogUtil.INSTANCE, "web url = " + this.webUrl, (String) null, 2, (Object) null);
    }

    public final void initView() {
        BaseLayoutSecondToolbarBinding baseLayoutSecondToolbarBinding = getBinding().toolbar;
        Intrinsics.checkNotNullExpressionValue(baseLayoutSecondToolbarBinding, "binding.toolbar");
        BaseBindActivity.initBindingToolbar$default((BaseBindActivity) this, baseLayoutSecondToolbarBinding, this.webTitle, false, (Function0) null, true, R.drawable.vector_drawable_share_black, (Function0) new Function0<Unit>() { // from class: com.viatom.baselib.mvvm.web.WebViewActivity$initView$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                ActivityWebviewBinding binding;
                WebViewActivity webViewActivity = WebViewActivity.this;
                binding = webViewActivity.getBinding();
                webViewActivity.shareUrl(binding.webView.getUrl());
            }
        }, false, false, 0, (Function0) null, false, 3980, (Object) null);
        String str = this.webUrl;
        if (str != null) {
            getBinding().webView.loadUrl(str);
        }
        getBinding().webView.setDesktopMode(false);
        getBinding().webView.setListener(this, this);
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onPageStarted(String url, Bitmap favicon) {
        getBinding().progressBar.setVisibility(0);
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onPageFinished(String url) {
        getBinding().progressBar.setVisibility(8);
    }

    @Override // im.delight.android.webview.AdvancedWebView.Listener
    public void onPageError(int errorCode, String description, String failingUrl) {
        getBinding().progressBar.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void shareUrl(String url) {
        String str = url;
        if (str == null || str.length() == 0) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.addFlags(3);
        intent.putExtra("android.intent.extra.TEXT", url);
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
