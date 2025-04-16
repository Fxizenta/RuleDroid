package jp.co.fenrir.android.sleipnir.tab;

import android.R;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.co.fenrir.android.sleipnir.C0000R;
import jp.co.fenrir.android.sleipnir.ci;
import jp.co.fenrir.android.sleipnir.ck;
import jp.co.fenrir.android.sleipnir.cl;
import jp.co.fenrir.android.sleipnir.df;
import jp.co.fenrir.android.sleipnir.ek;

/* loaded from: classes.dex */
public final class Tab extends jp.co.fenrir.android.sleipnir.w {
    private static final Pattern b = Pattern.compile("https?://market.android.com/(.+)");
    private UUID c = UUID.randomUUID();
    private ai d;
    private jp.co.fenrir.android.sleipnir.g e;
    private boolean f;
    private boolean g;
    private String h;
    private String i;
    private Bundle j;
    private df k;
    private FrameLayout l;
    private ImageView m;
    private FrameLayout n;
    private Bitmap o;
    private jp.co.fenrir.android.sleipnir.a.p p;

    /* loaded from: classes.dex */
    public class JsSleipnirMobile {
        private JsSleipnirMobile() {
        }

        /* synthetic */ JsSleipnirMobile(Tab tab, JsSleipnirMobile jsSleipnirMobile) {
            this();
        }

        public void addBookmark(String str, String str2) {
            if (cl.b(Tab.this.b())) {
                Tab.f494a.f();
                jp.co.fenrir.android.sleipnir.e.b.a(str, str2).a((Runnable) null);
            }
        }

        public void tweet(String str) {
            if (cl.b(Tab.this.b())) {
                Tab.f494a.a(new l(this, str));
            }
        }
    }

    public Tab(ai aiVar, int i, a.a.a.ak akVar, Tab tab) {
        this.g = false;
        if (tab != null) {
            this.p = tab.p;
        }
        this.d = aiVar;
        f494a.e().a(this);
        this.d.a(this, i);
        if (akVar != null) {
            this.h = cl.c(akVar, ah.NAME.name());
            this.i = cl.c(akVar, ah.URL.name());
            a.a.a.am b2 = cl.b(akVar, ah.LOCKED.name());
            this.g = (b2 == null || !b2.a()) ? false : b2.g();
            int a2 = cl.a(akVar, ah.BUNDLE_INDEX.name(), -1);
            String c = cl.c(akVar, ah.BUNDLE_HISTORY.name());
            if (a2 >= 0 && c != null) {
                this.j = new Bundle();
                try {
                    this.j.putSerializable("history", (Serializable) ck.b(c));
                    this.j.putInt("index", a2);
                } catch (IOException e) {
                    f494a.a(e);
                } catch (ClassNotFoundException e2) {
                    f494a.a(e2);
                }
            }
            if (this.i == null || this.i.length() == 0) {
                this.h = null;
                this.i = null;
                this.j = null;
            }
        }
        this.f = this.i == null;
    }

    private Bitmap E() {
        if (this.o == null) {
            this.o = cl.a(cl.a((View) this.k, true), 0.5f);
        }
        return this.o;
    }

    public static /* synthetic */ void a(Tab tab, df dfVar, int i) {
        Picture capturePicture = dfVar.capturePicture();
        jp.co.fenrir.android.sleipnir.i.a a2 = (capturePicture == null || capturePicture.getWidth() <= 0 || capturePicture.getHeight() <= 0) ? null : f494a.i().a(tab.b(), capturePicture);
        Bitmap c = a2 == null ? null : a2.c();
        if (c != null) {
            ((ImageView) tab.l.findViewById(C0000R.id.tab_thumbnail_layer)).setImageBitmap(c);
        } else if (i > 0) {
            f494a.a(new c(tab, dfVar, i), 200L);
            return;
        }
        if (dfVar.getProgress() == 100) {
            tab.l.findViewById(C0000R.id.tab_loading_layer).setVisibility(8);
        }
    }

    public boolean a(jp.co.fenrir.android.sleipnir.a.p pVar) {
        if (pVar == null || pVar.a(this.p)) {
            return false;
        }
        if (this.k != null && !pVar.a(this.k.getSettings())) {
            return false;
        }
        this.p = pVar;
        return true;
    }

    public void e(boolean z) {
        if (this.k == null || this.e == null) {
            f494a.b("invalid call: updateWebViewVisibility");
            return;
        }
        if (!z) {
            this.k.setVisibility(4);
            this.k.g();
            this.e.b(this.k);
        } else {
            this.e.a(this.k);
            this.k.f();
            this.k.setVisibility(0);
            this.k.requestFocus();
        }
    }

    public static boolean e(String str) {
        if (str.startsWith("about:")) {
            return false;
        }
        if (str.startsWith("market:") || str.startsWith("mailto:") || str.startsWith("tel:") || str.startsWith("geo:0,0?q=") || str.startsWith("vnd.youtube:")) {
            f494a.n().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            return true;
        }
        Matcher matcher = b.matcher(str);
        if (!matcher.find()) {
            return false;
        }
        try {
            f494a.n().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://" + matcher.group(1))));
        } catch (ActivityNotFoundException e) {
            f494a.n().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search")));
        }
        return true;
    }

    private void f(boolean z) {
        if (this.l != null) {
            this.l.scrollTo(0, -(z ? f494a.b().d() : f494a.b().e()));
            this.l.findViewById(C0000R.id.tab_shadow_frame).setBackgroundResource(z ? C0000R.drawable.layer_tab_active : C0000R.drawable.layer_tab_deactive);
        }
    }

    public final void A() {
        this.l.findViewById(C0000R.id.tab_shadow_frame).startAnimation(f494a.b().f());
    }

    public final a.a.a.ak B() {
        a.a.a.ak akVar = new a.a.a.ak();
        akVar.a(ah.NAME.name(), this.h != null ? this.h : a());
        akVar.a(ah.URL.name(), this.i != null ? this.i : b());
        akVar.a(ah.LOCKED.name(), Boolean.valueOf(this.g));
        Bundle bundle = this.j;
        if (bundle == null) {
            bundle = new Bundle();
            this.k.saveState(bundle);
        }
        if (bundle.containsKey("index") && bundle.containsKey("history")) {
            akVar.a(ah.BUNDLE_INDEX.name(), Integer.valueOf(bundle.getInt("index")));
            try {
                akVar.a(ah.BUNDLE_HISTORY.name(), ck.a(bundle.getSerializable("history")));
            } catch (IOException e) {
                f494a.a(e);
            }
        }
        return akVar;
    }

    public final void C() {
        this.e.b(this.k);
        this.e = null;
    }

    public final String a() {
        if (this.h != null) {
            return this.h;
        }
        if (this.f) {
            return f494a.a().getResources().getString(C0000R.string.search_or_input_url);
        }
        String title = this.k.getTitle();
        return title == null ? "" : title;
    }

    public final void a(float f) {
        int d = (int) (this.k.d() * f);
        if (d <= 1) {
            d = 0;
        } else if (d >= this.k.d() - 1) {
            d = this.k.d();
        }
        this.k.scrollTo(this.k.getScrollX(), d);
    }

    public final void a(ImageView imageView, ci ciVar) {
        imageView.setImageDrawable(null);
        imageView.setImageBitmap(this.f ? ciVar.c() : E());
    }

    public final void a(LinearLayout linearLayout) {
        linearLayout.removeView(this.n);
        this.n = null;
    }

    public final void a(LinearLayout linearLayout, int i) {
        this.n = (FrameLayout) cl.b(C0000R.layout.tab_layout);
        this.n.setOnTouchListener(new j(this));
        if (this.i != null) {
            this.n.findViewById(C0000R.id.tab_reserved_layer).setVisibility(0);
        }
        this.n.findViewById(C0000R.id.tab_shadow_frame).setBackgroundResource(f494a.e().e() == this ? C0000R.drawable.layer_tab_active : C0000R.drawable.layer_tab_deactive);
        this.n.findViewById(C0000R.id.tab_lock_layer).setVisibility(this.g ? 0 : 8);
        jp.co.fenrir.android.sleipnir.i.a a2 = f494a.i().a(this.i != null ? this.i : b());
        Bitmap c = a2 == null ? null : a2.c();
        if (c != null) {
            ((ImageView) this.n.findViewById(C0000R.id.tab_thumbnail_layer)).setImageBitmap(c);
        }
        linearLayout.addView(this.n, i);
    }

    public final void a(LinearLayout linearLayout, LinearLayout linearLayout2, int i) {
        this.l = (FrameLayout) cl.b(C0000R.layout.tab_layout);
        this.l.setOnTouchListener(new ad(this));
        f(f494a.e().e() == this);
        if (this.i != null) {
            this.l.findViewById(C0000R.id.tab_reserved_layer).setVisibility(0);
        }
        jp.co.fenrir.android.sleipnir.i.a a2 = f494a.i().a(this.i != null ? this.i : b());
        Bitmap c = a2 == null ? null : a2.c();
        if (c != null) {
            ((ImageView) this.l.findViewById(C0000R.id.tab_thumbnail_layer)).setImageBitmap(c);
        }
        this.m = new ImageView(f494a.a());
        this.m.setImageResource(C0000R.drawable.pageindicator_deactive);
        linearLayout.addView(this.l, i);
        linearLayout2.addView(this.m, i);
        a(this.g);
    }

    public final void a(LinearLayout linearLayout, LinearLayout linearLayout2, boolean z) {
        this.l.setOnTouchListener(null);
        if (z) {
            FrameLayout frameLayout = this.l;
            Animation loadAnimation = AnimationUtils.loadAnimation(f494a.a(), C0000R.anim.tab_close);
            loadAnimation.setAnimationListener(new d(this, linearLayout, frameLayout));
            this.l.startAnimation(loadAnimation);
        } else {
            linearLayout.removeView(this.l);
        }
        linearLayout2.removeView(this.m);
    }

    public final void a(String str) {
        if (e(str)) {
            return;
        }
        jp.co.fenrir.android.sleipnir.a.p a2 = f494a.m().a(str);
        if (a2 == null || !a2.a(str)) {
            a(a2);
            this.f = str.length() == 0;
            boolean z = f494a.e().e() == this;
            e(!this.f && z);
            if (z) {
                f494a.e().a(this.f);
            }
            if (this.f) {
                f494a.e().b(this, null);
            } else if (URLUtil.isJavaScriptUrl(str)) {
                this.k.loadUrl(Uri.decode(str));
            } else {
                this.k.loadUrl(str);
                f494a.e().a(this, str);
            }
        }
    }

    public final void a(jp.co.fenrir.android.sleipnir.g gVar) {
        this.e = gVar;
        if (this.k == null) {
            this.k = new df(f494a.o());
            this.k.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            this.k.setVerticalFadingEdgeEnabled(false);
            this.k.setScrollBarStyle(0);
            this.k.getSettings().setNeedInitialFocus(false);
            this.k.getSettings().setBuiltInZoomControls(true);
            this.k.getSettings().setLoadWithOverviewMode(true);
            this.k.getSettings().setUseWideViewPort(true);
            this.k.getSettings().setJavaScriptEnabled(true);
            this.k.getSettings().setSupportMultipleWindows(true);
            this.k.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            this.k.getSettings().setDomStorageEnabled(true);
            this.k.getSettings().setDatabasePath(f494a.b().i());
            this.k.getSettings().setDatabaseEnabled(true);
            this.k.getSettings().setAppCacheMaxSize(8388608L);
            this.k.getSettings().setAppCachePath(f494a.b().j());
            this.k.getSettings().setAppCacheEnabled(true);
            this.k.getSettings().setGeolocationDatabasePath(f494a.b().k());
            this.k.getSettings().setGeolocationEnabled(true);
            this.k.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            this.k.getSettings().setSavePassword(f494a.r().getBoolean(ek.SECURITY_SAVE_PASSWORD.name(), true));
            if (Build.VERSION.SDK_INT <= 7) {
                this.k.getSettings().setPluginsEnabled(true);
            } else {
                this.k.getSettings().setPluginState(f494a.r().getBoolean(ek.DETAILS_ALWAYS_ENABLE_PLUGINS.name(), false) ? WebSettings.PluginState.ON : WebSettings.PluginState.ON_DEMAND);
            }
            try {
                Field declaredField = this.k.getSettings().getClass().getDeclaredField("mBuiltInZoomControls");
                declaredField.setAccessible(true);
                declaredField.set(this.k.getSettings(), false);
            } catch (Exception e) {
                f494a.a(e);
                this.k.getSettings().setBuiltInZoomControls(false);
            }
            this.k.setDownloadListener(new g(this));
            this.k.setWebChromeClient(new f(this));
            this.k.setWebViewClient(new e(this));
            this.k.addJavascriptInterface(new JsSleipnirMobile(this, null), "SleipnirMobile");
            if (this.p != null) {
                jp.co.fenrir.android.sleipnir.a.p pVar = this.p;
                this.p = null;
                a(pVar);
            }
            this.e.c(this.k);
            e(false);
        }
    }

    public final void a(ai aiVar, int i) {
        boolean z = f494a.e().e() == this;
        if (z) {
            this.d.e();
        }
        this.d.a(this, false);
        this.d = aiVar;
        this.d.a(this, i);
        if (z) {
            this.d.a(this, this.l);
            f494a.e().c(this);
            t();
        }
        f(z);
    }

    public final void a(boolean z) {
        this.g = z;
        this.l.findViewById(C0000R.id.tab_lock_layer).setVisibility(this.g ? 0 : 8);
        if (this.n != null) {
            this.n.findViewById(C0000R.id.tab_lock_layer).setVisibility(this.g ? 0 : 8);
        }
    }

    public final boolean a(jp.co.fenrir.android.sleipnir.ai aiVar) {
        Rect rect = new Rect();
        this.n.getGlobalVisibleRect(rect);
        return aiVar.x < rect.exactCenterX();
    }

    public final boolean a(jp.co.fenrir.android.sleipnir.ai aiVar, jp.co.fenrir.android.sleipnir.ai aiVar2) {
        float f = aiVar2.y;
        int min = Math.min(Math.max(0, this.k.getScrollX()), this.k.c());
        int min2 = Math.min(Math.max(0, this.k.getScrollY()), this.k.d());
        aiVar2.a(min, min2).a(((PointF) aiVar).x, ((PointF) aiVar).y);
        jp.co.fenrir.android.sleipnir.ai aiVar3 = new jp.co.fenrir.android.sleipnir.ai(Math.min(Math.max(0.0f, aiVar2.x), this.k.c()), Math.min(Math.max(0.0f, aiVar2.y), this.k.d()));
        aiVar2.a(aiVar3);
        if (f == 0.0f && aiVar2.y == 0.0f) {
            return false;
        }
        if (!aiVar3.equals(min, min2)) {
            this.k.scrollTo((int) aiVar3.x, (int) aiVar3.y);
        }
        return true;
    }

    public final String b() {
        if (this.i != null) {
            return this.i;
        }
        if (this.f || this.k == null) {
            return "";
        }
        String url = this.k.getUrl();
        if (url != null && url.length() > 0) {
            return url;
        }
        String originalUrl = this.k.getOriginalUrl();
        return originalUrl == null ? "" : originalUrl;
    }

    public final Tab b(String str) {
        Tab tab = new Tab(this.d, this.d.a(this) + 1, null, this);
        if (str != null) {
            tab.a(str);
        }
        return tab;
    }

    public final void b(boolean z) {
        this.k.setVerticalScrollBarEnabled(z);
    }

    public final int c(String str) {
        int findAll = this.f ? 0 : this.k.findAll(str);
        if (findAll > 0) {
            this.k.h();
        }
        return findAll;
    }

    public final String c() {
        String a2 = a();
        return a2.length() > 0 ? a2 : b();
    }

    public final void c(boolean z) {
        if (!this.f) {
            f494a.g().a(a(), b());
        }
        this.d.a(this, z);
        this.d = null;
        f494a.e().b(this);
        if (this.k != null) {
            this.k.destroy();
            this.k = null;
        }
    }

    public final Rect d() {
        Rect rect = new Rect();
        this.l.getGlobalVisibleRect(rect);
        return rect;
    }

    public final void d(boolean z) {
        if (this.f) {
            return;
        }
        this.k.findNext(z);
    }

    public final ai e() {
        return this.d;
    }

    public final boolean f() {
        return this.g;
    }

    public final boolean g() {
        return this.f;
    }

    public final boolean h() {
        return this.k.e();
    }

    public final void i() {
        this.k.stopLoading();
    }

    public final void j() {
        boolean z = false;
        if (this.i == null) {
            this.k.reload();
            return;
        }
        if (this.j == null) {
            a(this.i);
        } else {
            this.k.restoreState(this.j);
            this.f = false;
            if (!this.f && f494a.e().e() == this) {
                z = true;
            }
            e(z);
            f494a.e().a(this.f);
        }
        this.h = null;
        this.i = null;
        this.j = null;
        this.l.findViewById(C0000R.id.tab_reserved_layer).setVisibility(8);
    }

    public final boolean k() {
        return this.k.canGoBack();
    }

    public final boolean l() {
        return this.k.canGoForward();
    }

    public final void m() {
        this.k.goBack();
    }

    public final void n() {
        this.k.goForward();
    }

    public final boolean o() {
        if (this.f) {
            return false;
        }
        return this.k.zoomOut();
    }

    public final boolean p() {
        if (this.f) {
            return false;
        }
        return this.k.zoomIn();
    }

    public final void q() {
        if (Build.VERSION.SDK_INT == 8) {
            this.k.emulateShiftHeld();
        } else {
            new KeyEvent(0L, 0L, 0, 59, 0, 0).dispatch(this.k);
        }
    }

    public final void r() {
        new KeyEvent(0L, 0L, 1, 59, 0, 0).dispatch(this.k);
    }

    public final void s() {
        if (this.g) {
            new AlertDialog.Builder(f494a.n()).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.dialog_alert_title).setMessage(C0000R.string.do_you_close_locked_tab).setPositiveButton(R.string.yes, new b(this)).setNegativeButton(R.string.no, new a(this)).setOnCancelListener(new i(this)).show();
        } else {
            c(true);
            f494a.e().j();
        }
    }

    public final Tab t() {
        Tab e = f494a.e().e();
        if (e != this) {
            if (this.i != null) {
                j();
            }
            if (e != null) {
                e.E();
                e.e(false);
                e.f(false);
                e.l.findViewById(C0000R.id.tab_shadow_frame).startAnimation(f494a.b().g());
                e.m.setImageResource(C0000R.drawable.pageindicator_deactive);
                ai aiVar = e.d;
                f494a.e();
                aiVar.e();
            }
            this.m.setImageResource(C0000R.drawable.pageindicator_active);
            this.d.a(this, this.l);
            f494a.e().c(this);
            e(this.f ? false : true);
            f494a.e().a(this.f);
            if (this.k.getProgress() < 100) {
                f494a.e().a(this, this.k.getUrl());
                f494a.e().a(this, this.k.getProgress());
            } else {
                f494a.e().b(this, this.k.getUrl());
            }
            u e2 = f494a.e();
            this.k.a();
            e2.a(this, this.k.b());
            f(true);
            if (this.o != null) {
                this.o = null;
            }
        }
        return this;
    }

    public final void u() {
        if (this.f) {
            return;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", b());
        intent.putExtra("android.intent.extra.SUBJECT", a());
        intent.setFlags(268435456);
        f494a.n().startActivity(intent);
    }

    public final void v() {
        if (this.f) {
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(b()));
        intent.setFlags(268435456);
        f494a.n().startActivity(Intent.createChooser(intent, f494a.a().getString(C0000R.string.complete_action_using)));
    }

    public final int w() {
        if (this.f) {
            return 0;
        }
        return this.k.i();
    }

    public final void x() {
        this.k.clearMatches();
    }

    public final void y() {
        jp.co.fenrir.android.sleipnir.a.p d;
        if (this.p != null) {
            d = this.p.g();
        } else {
            f494a.m();
            d = jp.co.fenrir.android.sleipnir.a.t.d();
        }
        String b2 = b();
        d.a(b2, new h(this, d, b2));
    }

    public final void z() {
        this.l.findViewById(C0000R.id.tab_shadow_frame).startAnimation(AnimationUtils.loadAnimation(f494a.a(), C0000R.anim.tab_join));
    }
}
