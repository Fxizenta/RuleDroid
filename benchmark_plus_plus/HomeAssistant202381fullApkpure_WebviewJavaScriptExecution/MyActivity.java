package io.homeassistant.companion.android.launch.my;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.homeassistant.companion.android.BaseActivity;
import io.homeassistant.companion.android.databinding.ActivityMyBinding;
import io.homeassistant.companion.android.webview.WebViewActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: MyActivity.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0015¨\u0006\b"}, d2 = {"Lio/homeassistant/companion/android/launch/my/MyActivity;", "Lio/homeassistant/companion/android/BaseActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_fullRelease"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class MyActivity extends BaseActivity {
    public static final int $stable = 0;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final String EXTRA_URI = "EXTRA_URI";

    /* compiled from: MyActivity.kt */
    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lio/homeassistant/companion/android/launch/my/MyActivity$Companion;", "", "()V", MyActivity.EXTRA_URI, "", "newInstance", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "app_fullRelease"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Intent newInstance(Context context, Uri uri) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(uri, "uri");
            Intent intent = new Intent(context, (Class<?>) MyActivity.class);
            intent.putExtra(MyActivity.EXTRA_URI, uri.toString());
            return intent;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        String queryParameter;
        super.onCreate(savedInstanceState);
        ActivityMyBinding inflate = ActivityMyBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(layoutInflater)");
        setContentView(inflate.getRoot());
        Intent intent = getIntent();
        if (!Intrinsics.areEqual("android.intent.action.VIEW", intent != null ? intent.getAction() : null) || getIntent().getData() == null) {
            return;
        }
        Uri data = getIntent().getData();
        boolean z = false;
        if (data != null && (queryParameter = data.getQueryParameter("mobile")) != null && queryParameter.equals("1")) {
            z = true;
        }
        if (z) {
            finish();
            return;
        }
        Uri data2 = getIntent().getData();
        Intrinsics.checkNotNull(data2);
        Uri build = data2.buildUpon().appendQueryParameter("mobile", "1").build();
        final WebView webView = inflate.webview;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() { // from class: io.homeassistant.companion.android.launch.my.MyActivity$onCreate$1$1
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String valueOf = String.valueOf(request != null ? request.getUrl() : null);
                if (!StringsKt.startsWith$default(valueOf, "homeassistant://navigate/", false, 2, (Object) null)) {
                    return false;
                }
                MyActivity myActivity = MyActivity.this;
                WebViewActivity.Companion companion = WebViewActivity.INSTANCE;
                Context context = webView.getContext();
                Intrinsics.checkNotNullExpressionValue(context, "context");
                myActivity.startActivity(WebViewActivity.Companion.newInstance$default(companion, context, StringsKt.removePrefix(valueOf, (CharSequence) "homeassistant://navigate/"), null, 4, null));
                MyActivity.this.finish();
                return true;
            }
        });
        inflate.webview.loadUrl(build.toString());
    }
}
