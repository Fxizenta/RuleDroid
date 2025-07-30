package com.teamwire.messenger;

import android.content.Intent;
import android.os.Bundle;
import com.teamwire.messenger.uicomponents.TextView;
import com.teamwire.messenger.uicomponents.ThemedProgressBar;
import d.f.d.q;

/* loaded from: classes.dex */
public class LoadingActivity extends com.teamwire.messenger.b {
    private boolean s2 = false;
    private ThemedProgressBar t2;
    private TextView u2;

    /* loaded from: classes.dex */
    class a implements Runnable {
        final /* synthetic */ int a;

        a(int i2) {
            this.a = i2;
        }

        @Override // java.lang.Runnable
        public void run() {
            LoadingActivity.this.t2.a(this.a);
        }
    }

    /* loaded from: classes.dex */
    class b implements Runnable {
        final /* synthetic */ String a;

        b(String str) {
            this.a = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            LoadingActivity.this.u2.setText(this.a);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void c(String str) {
        if (this.u2 != null) {
            q.E().c().a().execute(new b(str));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void l(int i2) {
        ThemedProgressBar themedProgressBar = this.t2;
        if (themedProgressBar != null) {
            if (themedProgressBar.getVisibility() != 0) {
                this.t2.setTheme(this);
                this.t2.setVisibility(0);
            }
            q.E().c().a().execute(new a(i2));
        }
    }

    @Override // com.teamwire.messenger.b
    protected void o0() {
        ThemedProgressBar themedProgressBar = this.t2;
        if (themedProgressBar != null) {
            themedProgressBar.setTheme(this);
            this.t2.setVisibility(0);
        }
    }

    @Override // com.teamwire.messenger.b
    protected void a(Bundle bundle) {
        if (this.s2) {
            finish();
        } else {
            p0();
        }
    }

    @Override // com.teamwire.messenger.b
    protected void b(Bundle bundle) {
        setContentView(R.layout.activity_loading);
        this.t2 = (ThemedProgressBar) findViewById(R.id.loading_indicator);
        this.u2 = (TextView) findViewById(R.id.loading_message);
        Intent intent = getIntent();
        if (intent != null) {
            this.s2 = intent.getBooleanExtra("ONLY_INITIALIZE", false);
        }
    }
}
