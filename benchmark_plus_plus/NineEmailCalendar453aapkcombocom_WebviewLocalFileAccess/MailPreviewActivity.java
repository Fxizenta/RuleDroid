package com.ninefolders.hd3.mail.browse;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import com.ninefolders.hd3.C0179R;
import com.ninefolders.hd3.activity.ActionBarLockActivity;
import com.ninefolders.hd3.emailcommon.utility.f;
import com.ninefolders.hd3.mail.providers.Message;
import com.ninefolders.hd3.mail.ui.ez;
import com.ninefolders.hd3.mail.utils.ThemeUtils;
import com.ninefolders.nfm.widget.ProtectedWebView;
import microsoft.exchange.webservices.data.core.EwsUtilities;

/* compiled from: ProGuard */
/* loaded from: classes2.dex */
public class MailPreviewActivity extends ActionBarLockActivity implements View.OnClickListener {
    private MailWebView a;
    private f.b b = new f.b();
    private Handler c = new Handler();
    private ax d;
    private Message e;
    private ez f;
    private View g;
    private boolean h;

    @TargetApi(19)
    public void g() {
        getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    public static void a(Context context, Uri uri, boolean z) {
        Intent intent = new Intent(context, (Class<?>) MailPreviewActivity.class);
        intent.putExtra("extra-mail-uri", uri);
        intent.putExtra("extra_safe_for_image", z);
        context.startActivity(intent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z || Build.VERSION.SDK_INT < 19) {
            return;
        }
        g();
    }

    @Override // com.ninefolders.hd3.activity.ActionBarLockActivity, com.ninefolders.mam.support.v7.app.NFMAppCompatActivity, com.microsoft.intune.mam.client.app.HookedActivity
    public void onMAMCreate(Bundle bundle) {
        ThemeUtils.b(this, 13);
        super.onMAMCreate(bundle);
        setContentView(C0179R.layout.mail_preview_activity);
        this.a = (MailWebView) findViewById(C0179R.id.nx_web_view);
        this.d = new ax(this, this.c);
        this.d.a();
        this.f = new ez(this);
        this.g = findViewById(C0179R.id.close_button);
        this.g.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 19) {
            g();
        }
        Intent intent = getIntent();
        this.h = intent.getBooleanExtra("extra_safe_for_image", false);
        i();
        if (bundle != null) {
            this.e = (Message) bundle.getParcelable("save-preview-mail");
        }
        this.d.b();
        if (this.e == null) {
            if (intent == null || !intent.hasExtra("extra-mail-uri")) {
                return;
            }
            new a((Uri) intent.getParcelableExtra("extra-mail-uri")).d(new Void[0]);
            return;
        }
        if (k()) {
            return;
        }
        this.d.c();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.ninefolders.mam.support.v7.app.NFMAppCompatActivity, com.microsoft.intune.mam.client.app.HookedActivity
    public void onMAMDestroy() {
        MailWebView mailWebView;
        super.onMAMDestroy();
        if (!com.ninefolders.hd3.mail.utils.bs.h() && (mailWebView = this.a) != null) {
            ViewGroup viewGroup = (ViewGroup) mailWebView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.a);
            }
            this.a.removeAllViews();
            this.a.destroy();
        }
        this.b.a();
    }

    public String h() {
        if (com.ninefolders.hd3.mail.j.m.a(this).aC()) {
            return null;
        }
        return ThemeUtils.a((Context) this);
    }

    private void i() {
        WebSettings settings = this.a.getSettings();
        settings.setBlockNetworkImage(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        ThemeUtils.DarkMode b = !com.ninefolders.hd3.mail.j.m.a(this).aC() ? ThemeUtils.b(this) : null;
        if (b != null) {
            this.a.setBackgroundColor(b.a());
        }
        if (Build.VERSION.SDK_INT >= 19 && (getApplicationInfo().flags & 2) != 0) {
            ProtectedWebView.setWebContentsDebuggingEnabled(true);
        }
        this.a.setWebViewClient(new as(this));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ProGuard */
    /* loaded from: classes2.dex */
    public class a extends com.ninefolders.hd3.emailcommon.utility.f<Void, Void, Message> {
        private final Uri b;

        public a(Uri uri) {
            super(MailPreviewActivity.this.b);
            this.b = uri;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.ninefolders.hd3.emailcommon.utility.f
        public Message a(Void... voidArr) {
            Uri uri = this.b;
            if (uri == null) {
                return null;
            }
            Cursor query = MailPreviewActivity.this.getContentResolver().query(TextUtils.isEmpty(uri.getQueryParameter("QUERY_ALL_MESSAGE_VIEW")) ? uri.buildUpon().appendQueryParameter("QUERY_ALL_MESSAGE_VIEW", EwsUtilities.XSTrue).build() : uri, com.ninefolders.hd3.mail.providers.bp.l, null, null, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        Bundle extras = query.getExtras();
                        Message message = new Message(query);
                        if (extras != null && message.M()) {
                            String string = extras.getString("decrypted_message");
                            if (!TextUtils.isEmpty(string)) {
                                message.a(string, (String) null, 1);
                            }
                        }
                        return message;
                    }
                } finally {
                    query.close();
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.ninefolders.hd3.emailcommon.utility.f
        public void a(Message message) {
            MailPreviewActivity.this.e = message;
            MailPreviewActivity.this.c.post(new au(this));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.ninefolders.hd3.emailcommon.utility.f
        public void b(Message message) {
            MailPreviewActivity.this.e = null;
            MailPreviewActivity.this.c.post(new aw(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean j() {
        Message message = this.e;
        return message == null || !message.k() || this.e.T();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean k() {
        if (this.e == null) {
            return false;
        }
        String h = h();
        this.f.a();
        this.f.a(this.e, h, false);
        this.a.loadDataWithBaseURL("x-thread://message-preview/" + this.e.b, this.f.b(), "text/html", "utf-8", null);
        return true;
    }
}
