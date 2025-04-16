package com.readdle.spark.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import c.c.a.e.common.o;
import c.c.a.e.common.p;
import c.c.a.e.common.q;
import c.c.a.e.common.r;
import c.c.a.e.common.s;
import c.c.a.e.common.t;
import c.c.a.utils.S;
import c.c.a.utils.d.d;
import c.c.a.utils.d.f;
import com.facebook.stetho.common.Utf8Charset;
import com.readdle.spark.app.SparkApp;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt__CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__IndentKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 =2\u00020\u0001:\u0003=>?B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007B\u001f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\b\u0010/\u001a\u000200H\u0002J\b\u00101\u001a\u000200H\u0007JL\u00102\u001a\u0002002\u0006\u00103\u001a\u00020\u001b2\b\b\u0002\u00104\u001a\u00020\t2\b\b\u0002\u00105\u001a\u00020\u001f2\b\b\u0002\u0010\u0019\u001a\u00020\u00152\n\b\u0002\u00106\u001a\u0004\u0018\u00010'2\u0010\b\u0002\u00107\u001a\n\u0012\u0004\u0012\u00020\u001b\u0018\u000108H\u0007J\u0012\u00109\u001a\u00020\u00152\b\u0010:\u001a\u0004\u0018\u00010;H\u0016J\u0006\u0010<\u001a\u000200R\u001e\u0010\u000b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u0010\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001e\u0010\u0011\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u0010\u001a\u0004\b\u0012\u0010\r\"\u0004\b\u0013\u0010\u000fR\u001e\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u0015@BX\u0082\u000e¢\u0006\b\n\u0000\"\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0018\u00010\u001dR\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010 \u001a\u0004\u0018\u00010!X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u001c\u0010&\u001a\u0004\u0018\u00010'X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+R\u001c\u0010,\u001a\u0004\u0018\u00010'X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010)\"\u0004\b.\u0010+¨\u0006@"}, d2 = {"Lcom/readdle/spark/ui/common/HtmlPreviewWebView;", "Landroid/webkit/WebView;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "computedHeight", "getComputedHeight", "()Ljava/lang/Integer;", "setComputedHeight", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "computedWidth", "getComputedWidth", "setComputedWidth", "value", "", "contentScrollable", "setContentScrollable", "(Z)V", "htmlEditable", "innerHtml", "", "javaScriptInterface", "Lcom/readdle/spark/ui/common/HtmlPreviewWebView$JavaScriptInterface;", "loadedInitialScale", "", "onTextChanged", "Lcom/readdle/spark/ui/common/HtmlPreviewWebView$OnTextChanged;", "getOnTextChanged", "()Lcom/readdle/spark/ui/common/HtmlPreviewWebView$OnTextChanged;", "setOnTextChanged", "(Lcom/readdle/spark/ui/common/HtmlPreviewWebView$OnTextChanged;)V", "pageFinishedLoadingRunnable", "Ljava/lang/Runnable;", "getPageFinishedLoadingRunnable", "()Ljava/lang/Runnable;", "setPageFinishedLoadingRunnable", "(Ljava/lang/Runnable;)V", "requestLayoutRunnable", "getRequestLayoutRunnable", "setRequestLayoutRunnable", "executePendingGetCallback", "", "initJavascriptInterface", "load", "htmlContent", "viewWidth", "initialScale", "pageFinishedLoading", "customStyles", "", "onInterceptTouchEvent", "ev", "Landroid/view/MotionEvent;", "requestFocusByJS", "Companion", "JavaScriptInterface", "OnTextChanged", "app_release"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class HtmlPreviewWebView extends WebView {

    /* renamed from: a, reason: collision with root package name */
    public static final d f3044a;

    /* renamed from: b, reason: collision with root package name */
    public static final HtmlPreviewWebView f3045b = null;

    /* renamed from: c, reason: collision with root package name */
    public boolean f3046c;

    /* renamed from: d, reason: collision with root package name */
    public a f3047d;

    /* renamed from: e, reason: collision with root package name */
    public String f3048e;

    /* renamed from: f, reason: collision with root package name */
    public b f3049f;
    public Integer g;
    public Integer h;
    public Runnable i;
    public Runnable j;
    public float k;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class a {

        /* renamed from: a, reason: collision with root package name */
        public ValueCallback<String> f3050a;

        /* renamed from: b, reason: collision with root package name */
        public final ValueCallback<String> f3051b;

        public a() {
        }

        public final ValueCallback<String> a() {
            return this.f3050a;
        }

        @JavascriptInterface
        public final void contentSize(String str, String str2) {
            if (str == null) {
                Intrinsics.throwParameterIsNullException("height");
                throw null;
            }
            if (str2 != null) {
                try {
                    HtmlPreviewWebView htmlPreviewWebView = HtmlPreviewWebView.this;
                    float parseInt = Integer.parseInt(str);
                    Resources resources = HtmlPreviewWebView.this.getResources();
                    Intrinsics.checkExpressionValueIsNotNull(resources, "resources");
                    htmlPreviewWebView.setComputedHeight(Integer.valueOf((int) (parseInt * resources.getDisplayMetrics().density * HtmlPreviewWebView.this.k)));
                } catch (Exception unused) {
                }
                try {
                    HtmlPreviewWebView htmlPreviewWebView2 = HtmlPreviewWebView.this;
                    float parseInt2 = Integer.parseInt(str2);
                    Resources resources2 = HtmlPreviewWebView.this.getResources();
                    Intrinsics.checkExpressionValueIsNotNull(resources2, "resources");
                    htmlPreviewWebView2.setComputedWidth(Integer.valueOf((int) (parseInt2 * resources2.getDisplayMetrics().density * HtmlPreviewWebView.this.k)));
                } catch (Exception unused2) {
                }
                HtmlPreviewWebView htmlPreviewWebView3 = HtmlPreviewWebView.f3045b;
                d a2 = HtmlPreviewWebView.a();
                StringBuilder b2 = c.a.a.a.a.b("computed = {");
                b2.append(HtmlPreviewWebView.this.getG());
                b2.append(" x ");
                b2.append(HtmlPreviewWebView.this.getH());
                b2.append("}, actual = {");
                b2.append(str);
                b2.append(" x ");
                b2.append(str2);
                b2.append("} ");
                String str3 = HtmlPreviewWebView.this.f3048e;
                int min = Math.min(HtmlPreviewWebView.this.f3048e.length(), 20);
                if (str3 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                }
                String substring = str3.substring(0, min);
                Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                b2.append(substring);
                a2.b(b2.toString());
                return;
            }
            Intrinsics.throwParameterIsNullException("width");
            throw null;
        }

        @JavascriptInterface
        public final void getContent(String str) {
            if (str != null) {
                ValueCallback<String> valueCallback = this.f3050a;
                if (valueCallback == null) {
                    return;
                }
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(str);
                }
                this.f3050a = null;
                return;
            }
            Intrinsics.throwParameterIsNullException("html");
            throw null;
        }

        @JavascriptInterface
        public final void onKeyUp(String str) {
            if (str != null) {
                if (HtmlPreviewWebView.this.f3046c) {
                    HtmlPreviewWebView.this.f3048e = str;
                    b f3049f = HtmlPreviewWebView.this.getF3049f();
                    if (f3049f != null) {
                        f3049f.a(str);
                    }
                    HtmlPreviewWebView.this.post(new r(this));
                    ValueCallback<String> valueCallback = this.f3051b;
                    if (valueCallback == null) {
                        return;
                    }
                    valueCallback.onReceiveValue(str);
                    return;
                }
                return;
            }
            Intrinsics.throwParameterIsNullException("html");
            throw null;
        }

        @JavascriptInterface
        public final void scaleUpdated(String str) {
            if (str != null) {
                try {
                    float parseFloat = Float.parseFloat(str);
                    if (parseFloat > 0.01f) {
                        HtmlPreviewWebView.this.k = parseFloat;
                    }
                    HtmlPreviewWebView.this.post(new s(this));
                    return;
                } catch (Exception e2) {
                    HtmlPreviewWebView htmlPreviewWebView = HtmlPreviewWebView.f3045b;
                    HtmlPreviewWebView.a().a(e2.getLocalizedMessage());
                    return;
                }
            }
            Intrinsics.throwParameterIsNullException("scaleString");
            throw null;
        }
    }

    /* loaded from: classes.dex */
    public interface b {
        void a(String str);
    }

    static {
        d a2 = f.a(HtmlPreviewWebView.class);
        Intrinsics.checkExpressionValueIsNotNull(a2, "LoggerFactory.getLogger(…eviewWebView::class.java)");
        f3044a = a2;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public HtmlPreviewWebView(Context context) {
        super(context);
        if (context != null) {
            this.f3048e = "";
            setWebViewClient(new o(this));
            setOnTouchListener(new p(this));
            setContentScrollable(false);
            WebSettings settings = getSettings();
            settings.setUseWideViewPort(true);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            settings.setAppCacheEnabled(true);
            settings.setAppCachePath(SparkApp.e(getContext()));
            settings.setSupportZoom(false);
            setFocusable(true);
            b();
            this.i = new q(this);
            this.k = 1.0f;
            return;
        }
        Intrinsics.throwParameterIsNullException("context");
        throw null;
    }

    private final void setContentScrollable(boolean z) {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(z);
    }

    public final void a(String str) {
        a(this, str, 0, 0.0f, false, null, null, 62);
    }

    public final void a(String str, int i, float f2, boolean z, Runnable runnable) {
        a(this, str, i, f2, z, runnable, null, 32);
    }

    /* renamed from: getComputedHeight, reason: from getter */
    public final Integer getG() {
        return this.g;
    }

    /* renamed from: getComputedWidth, reason: from getter */
    public final Integer getH() {
        return this.h;
    }

    /* renamed from: getOnTextChanged, reason: from getter */
    public final b getF3049f() {
        return this.f3049f;
    }

    /* renamed from: getPageFinishedLoadingRunnable, reason: from getter */
    public final Runnable getJ() {
        return this.j;
    }

    /* renamed from: getRequestLayoutRunnable, reason: from getter */
    public final Runnable getI() {
        return this.i;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public final void setComputedHeight(Integer num) {
        this.g = num;
    }

    public final void setComputedWidth(Integer num) {
        this.h = num;
    }

    public final void setOnTextChanged(b bVar) {
        this.f3049f = bVar;
    }

    public final void setPageFinishedLoadingRunnable(Runnable runnable) {
        this.j = runnable;
    }

    public final void setRequestLayoutRunnable(Runnable runnable) {
        this.i = runnable;
    }

    @SuppressLint({"JavascriptInterface"})
    public final void b() {
        this.f3047d = new a();
        addJavascriptInterface(this.f3047d, "JavaInterface");
    }

    public final void c() {
        evaluateJavascript("document.body.focus()", new t(this));
    }

    public static final d a() {
        return f3044a;
    }

    public static /* synthetic */ void a(HtmlPreviewWebView htmlPreviewWebView, String str, int i, float f2, boolean z, Runnable runnable, List list, int i2) {
        htmlPreviewWebView.a(str, (i2 & 2) != 0 ? 0 : i, (i2 & 4) != 0 ? 1.0f : f2, (i2 & 8) != 0 ? false : z, (i2 & 16) != 0 ? null : runnable, (i2 & 32) != 0 ? CollectionsKt__CollectionsKt.arrayListOf(S.a()) : list);
    }

    public final void a(String str, int i, float f2, boolean z, Runnable runnable, List<String> list) {
        String a2;
        if (str != null) {
            this.f3046c = z;
            this.f3048e = str;
            this.k = f2;
            this.j = runnable;
            setContentScrollable(false);
            StringBuilder sb = new StringBuilder();
            HtmlPreviewWebView htmlPreviewWebView = f3045b;
            Context context = getContext();
            Intrinsics.checkExpressionValueIsNotNull(context, "context");
            sb.append(a(f2, context, list));
            sb.append(str);
            if (i < 1) {
                a2 = "";
            } else {
                HtmlPreviewWebView htmlPreviewWebView2 = f3045b;
                float f3 = i;
                Resources resources = getResources();
                Intrinsics.checkExpressionValueIsNotNull(resources, "resources");
                a2 = a(f3 / resources.getDisplayMetrics().density);
            }
            sb.append(a2);
            loadDataWithBaseURL(null, sb.toString(), "text/html", Utf8Charset.NAME, null);
            return;
        }
        Intrinsics.throwParameterIsNullException("htmlContent");
        throw null;
    }

    public static final /* synthetic */ void a(HtmlPreviewWebView htmlPreviewWebView) {
        a aVar = htmlPreviewWebView.f3047d;
        if ((aVar != null ? aVar.a() : null) == null) {
            return;
        }
        htmlPreviewWebView.evaluateJavascript("JavaGetContent()", null);
    }

    public static final String a(float f2, Context context, List<String> list) {
        if (context != null) {
            StringBuilder b2 = c.a.a.a.a.b("\n            <head>\n                <meta name='viewport' content='");
            b2.append("initial-scale=" + f2 + ",maximum-scale=4.0,minimum-scale=0.1,width=device-width");
            b2.append("'>\n                <style>\n                    ");
            b2.append(S.a(context));
            b2.append("\n                    ");
            return c.a.a.a.a.a(b2, list != null ? CollectionsKt__CollectionsKt.a(list, "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62) : null, "\n                </style>\n            </head>\n        ");
        }
        Intrinsics.throwParameterIsNullException("context");
        throw null;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public HtmlPreviewWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (context == null) {
            Intrinsics.throwParameterIsNullException("context");
            throw null;
        }
        if (attributeSet != null) {
            this.f3048e = "";
            setWebViewClient(new o(this));
            setOnTouchListener(new p(this));
            setContentScrollable(false);
            WebSettings settings = getSettings();
            settings.setUseWideViewPort(true);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            settings.setAppCacheEnabled(true);
            settings.setAppCachePath(SparkApp.e(getContext()));
            settings.setSupportZoom(false);
            setFocusable(true);
            b();
            this.i = new q(this);
            this.k = 1.0f;
            return;
        }
        Intrinsics.throwParameterIsNullException("attrs");
        throw null;
    }

    public static final String a(float f2) {
        return StringsKt__IndentKt.trimIndent("\n            <script>\n                document.onreadystatechange = function () {\n                    if (document.readyState != 'loading') {\n                        if (document.readyState == 'interactive') {\n                            initialScale = " + f2 + " / window.innerWidth\n                            v = \"initial-scale=\"+ initialScale +\",maximum-scale=\"+ initialScale  +\",minimum-scale=\"+ initialScale  +\",width=device-width\"\n\n                            console.log(v)\n\n                            scale = " + f2 + " / window.innerWidth\n                            if (scale > 1) {\n                                return\n                            }\n                            scale = Math.min(0.5, scale)\n\n                            document.querySelector(\"meta[name=viewport]\").setAttribute('content', v)\n                            JavaInterface.scaleUpdated(scale)\n                        }\n                    }\n                }\n            </script>\n        ");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public HtmlPreviewWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (context == null) {
            Intrinsics.throwParameterIsNullException("context");
            throw null;
        }
        if (attributeSet != null) {
            this.f3048e = "";
            setWebViewClient(new o(this));
            setOnTouchListener(new p(this));
            setContentScrollable(false);
            WebSettings settings = getSettings();
            settings.setUseWideViewPort(true);
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            settings.setAppCacheEnabled(true);
            settings.setAppCachePath(SparkApp.e(getContext()));
            settings.setSupportZoom(false);
            setFocusable(true);
            b();
            this.i = new q(this);
            this.k = 1.0f;
            return;
        }
        Intrinsics.throwParameterIsNullException("attrs");
        throw null;
    }
}
