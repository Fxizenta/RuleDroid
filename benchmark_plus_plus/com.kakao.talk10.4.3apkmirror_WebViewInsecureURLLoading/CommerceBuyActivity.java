package com.kakao.talk.commerce.ui.buy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import bc1.a;
import com.alibaba.wireless.security.SecExceptionCode;
import com.kakao.kampmediaextension.common.streamer.WatchtowerSender;
import com.kakao.talk.activity.d;
import com.kakao.talk.music.R;
import com.kakao.talk.util.p2;
import com.kakao.talk.widget.CommonWebChromeClient;
import com.kakao.talk.widget.webview.WebViewHelper;
import com.kakao.vox.media.util.RTCStatsParser;
import dv2.g;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import kz.e;
import wb1.v;

/* loaded from: classes3.dex */
public class CommerceBuyActivity extends d implements View.OnClickListener {

    /* renamed from: y, reason: collision with root package name */
    public static final /* synthetic */ int f27128y = 0;

    /* renamed from: p, reason: collision with root package name */
    public ValueCallback<Uri> f27129p;

    /* renamed from: q, reason: collision with root package name */
    public ValueCallback<Uri[]> f27130q;
    public WebView r;

    /* renamed from: s, reason: collision with root package name */
    public ImageButton f27131s;

    /* renamed from: t, reason: collision with root package name */
    public ImageButton f27132t;
    public LinearLayout u;

    /* renamed from: v, reason: collision with root package name */
    public String f27133v;

    /* renamed from: w, reason: collision with root package name */
    public LinearLayout f27134w;

    /* renamed from: x, reason: collision with root package name */
    public boolean f27135x = false;

    /* loaded from: classes3.dex */
    public final class a extends WebChromeClient {
        public a() {
        }

        @Override // android.webkit.WebChromeClient
        public final boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            CommerceBuyActivity commerceBuyActivity = CommerceBuyActivity.this;
            ValueCallback<Uri[]> valueCallback2 = commerceBuyActivity.f27130q;
            if (valueCallback2 != null) {
                valueCallback2.onReceiveValue(null);
                commerceBuyActivity.f27130q = null;
            }
            commerceBuyActivity.f27130q = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType(WebViewHelper.IMAGE_MIME_TYPE);
            commerceBuyActivity.startActivityForResult(Intent.createChooser(intent, commerceBuyActivity.getString(R.string.title_for_file_chooser)), 2);
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            openFileChooser(valueCallback, "");
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String str) {
            CommerceBuyActivity commerceBuyActivity = CommerceBuyActivity.this;
            commerceBuyActivity.f27129p = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType(WebViewHelper.IMAGE_MIME_TYPE);
            commerceBuyActivity.startActivityForResult(Intent.createChooser(intent, commerceBuyActivity.getString(R.string.title_for_file_chooser)), 1);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String str, String str2) {
            openFileChooser(valueCallback, str);
        }
    }

    public final HashMap<String, String> V5() {
        HashMap<String, String> hashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : a.C0253a.f12224a.b().entrySet()) {
            if ("S".equalsIgnoreCase(entry.getKey())) {
                hashMap.put("Authorization", entry.getValue());
            } else {
                hashMap.put(entry.getKey(), entry.getValue());
            }
        }
        hashMap.put("Kakao-Buy-Version", "1.0");
        hashMap.put("os_name", "Android");
        hashMap.put("os_version", "10.4.3");
        return hashMap;
    }

    public final String W5(Uri uri) {
        String d9 = v.d();
        if (uri != null) {
            if (!ff.a.J(uri.toString(), new ArrayList(Arrays.asList(e.f96824b0, "buy")))) {
                return null;
            }
            if (g.i(uri.getQueryParameter("refresh"), RTCStatsParser.Key.TRUE)) {
                this.f27135x = true;
            }
            if ((WatchtowerSender.WATCHTOWER_API_PATH_KAKAOTALK.equals(uri.getScheme()) || "alphatalk".equals(uri.getScheme())) && "buy".equals(uri.getHost())) {
                if (!TextUtils.isEmpty(uri.getPath())) {
                    d9 = String.format("%s%s", d9, uri.getPath());
                }
                if (!TextUtils.isEmpty(uri.getQuery())) {
                    d9 = String.format("%s?%s", d9, uri.getQuery());
                }
                if (!TextUtils.isEmpty(uri.getFragment())) {
                    return String.format("%s#%s", d9, uri.getFragment());
                }
                return d9;
            }
            if (p2.f44593k.matcher(uri.toString()).matches()) {
                String uri2 = uri.toString();
                if (uri2.startsWith("http://")) {
                    return uri2.replace("http://", "https://");
                }
                return uri2;
            }
        }
        return d9;
    }

    @Override // com.kakao.talk.activity.d, android.app.Activity
    public final void finish() {
        if (this.f27135x) {
            setResult(SecExceptionCode.SEC_ERROR_STA_KEY_ENC_INVALID_PARAM);
        }
        super.finish();
    }

    @Override // com.kakao.talk.activity.d, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public final void onActivityResult(int i8, int i13, Intent intent) {
        Uri uri;
        boolean z13 = false;
        String.format(Locale.US, "onActivityResult() requestCode:%d, resultCode:%d ", Integer.valueOf(i8), Integer.valueOf(i13));
        if (this.f27129p == null && this.f27130q == null) {
            if (i8 != 1) {
                super.onActivityResult(i8, i13, intent);
                return;
            }
            if (i13 == -1) {
                if (intent != null && intent.hasExtra("isItemStoreSucceedSnapShot")) {
                    z13 = intent.getBooleanExtra("isItemStoreSucceedSnapShot", false);
                }
                if (z13) {
                    this.r.reload();
                    return;
                }
                return;
            }
            return;
        }
        if (i8 == 1) {
            if (intent != null && i13 == -1) {
                uri = intent.getData();
            } else {
                uri = null;
            }
            this.f27129p.onReceiveValue(uri);
            this.f27129p = null;
            return;
        }
        if (i8 == 2) {
            this.f27130q.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i13, intent));
            this.f27130q = null;
        }
    }

    @Override // com.kakao.talk.activity.d, androidx.activity.ComponentActivity, android.app.Activity
    public final void onBackPressed() {
        WebView webView = this.r;
        if (webView != null && webView.canGoBack()) {
            this.r.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id3 = view.getId();
        if (id3 == R.id.kakaobuy_navigation_close_button) {
            finish();
            return;
        }
        if (id3 == R.id.kakaobuy_navigation_prev_button) {
            if (this.r.canGoBack()) {
                this.r.goBack();
                return;
            } else {
                super.onBackPressed();
                return;
            }
        }
        if (id3 == R.id.kakaobuy_navigation_next_button && this.r.canGoForward()) {
            this.r.goForward();
        }
    }

    @Override // com.kakao.talk.activity.d, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        B5(R.layout.kakao_buy, false);
        this.f27134w = (LinearLayout) findViewById(R.id.buy_root_layout);
        this.f27131s = (ImageButton) findViewById(R.id.kakaobuy_navigation_prev_button);
        this.f27132t = (ImageButton) findViewById(R.id.kakaobuy_navigation_next_button);
        this.u = (LinearLayout) findViewById(R.id.buy_navigation_layout);
        this.f27131s.setOnClickListener(this);
        this.f27132t.setOnClickListener(this);
        findViewById(R.id.kakaobuy_navigation_close_button).setOnClickListener(this);
        this.f27133v = W5(getIntent().getData());
        this.r = (WebView) findViewById(R.id.webview_res_0x7f0a14fc);
        CookieManager cookieManagerInstance = WebViewHelper.getInstance().getCookieManagerInstance();
        cookieManagerInstance.setAcceptCookie(true);
        cookieManagerInstance.setCookie("Kakao-Buy-Version", "1.0");
        cookieManagerInstance.setAcceptThirdPartyCookies(this.r, true);
        WebViewHelper.getInstance().syncCookie();
        this.r.setScrollBarStyle(0);
        WebSettings settings = this.r.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        this.r.getSettings().setMixedContentMode(0);
        this.r.setWebChromeClient(new CommonWebChromeClient(this.f24230g, (ProgressBar) findViewById(R.id.progress)));
        this.r.setWebViewClient(new wy.a(this));
        this.r.setWebChromeClient(new a());
        this.r.loadUrl(this.f27133v, V5());
        com.kakao.talk.commerce.util.d.a(this.r);
    }

    @Override // com.kakao.talk.activity.d, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public final void onDestroy() {
        try {
            WebView webView = this.r;
            if (webView != null) {
                webView.stopLoading();
                this.r.clearCache(true);
                this.r.destroyDrawingCache();
                this.f27134w.removeView(this.r);
                this.r.setWebViewClient(null);
                this.r.setWebChromeClient(null);
                this.r.destroy();
                this.r = null;
            }
        } catch (Exception unused) {
        }
        super.onDestroy();
        WebViewHelper.getInstance().getCookieManagerInstance().setCookie("Kakao-Buy-Version", "");
        WebViewHelper.getInstance().syncCookie();
    }

    @Override // com.kakao.talk.activity.d, androidx.activity.ComponentActivity, android.app.Activity
    public final void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.r.loadUrl(W5(intent.getData()), V5());
    }
}
