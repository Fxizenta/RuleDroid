package com.cutestudio.dialer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.view.Menu;
import android.view.View;
import com.cutestudio.colordialer.R;
import com.cutestudio.commons.activities.BaseSimpleActivity;
import java.util.LinkedHashMap;
import java.util.Map;

@kotlin.g0(bv = {}, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0015\u0010\u0016J\b\u0010\u0003\u001a\u00020\u0002H\u0003J\u0012\u0010\u0006\u001a\u00020\u00022\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004H\u0014J\u0010\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0007H\u0016J\"\u0010\u0010\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000b2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000eH\u0014R\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00118\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0012\u0010\u0013¨\u0006\u0017"}, d2 = {"Lcom/cutestudio/dialer/activities/DialerActivity;", "Lcom/cutestudio/dialer/activities/SimpleActivity;", "Lkotlin/g2;", "P1", "Landroid/os/Bundle;", "savedInstanceState", "onCreate", "Landroid/view/Menu;", "menu", "", "onCreateOptionsMenu", "", "requestCode", "resultCode", "Landroid/content/Intent;", "resultData", "onActivityResult", "Landroid/net/Uri;", "g0", "Landroid/net/Uri;", "callNumber", "<init>", "()V", "app_release"}, k = 1, mv = {1, 7, 1})
/* loaded from: classes.dex */
public final class DialerActivity extends SimpleActivity {

    /* renamed from: g0, reason: collision with root package name */
    @g4.f
    private Uri f16956g0;

    /* renamed from: h0, reason: collision with root package name */
    @g4.e
    public Map<Integer, View> f16957h0 = new LinkedHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    @kotlin.g0(bv = {}, d1 = {"\u0000\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0003\u001a\u00020\u00022\b\u0010\u0001\u001a\u0004\u0018\u00010\u0000H\n¢\u0006\u0004\b\u0003\u0010\u0004"}, d2 = {"Landroid/telecom/PhoneAccountHandle;", "handle", "Lkotlin/g2;", "c", "(Landroid/telecom/PhoneAccountHandle;)V"}, k = 3, mv = {1, 7, 1})
    /* loaded from: classes.dex */
    public static final class a extends kotlin.jvm.internal.n0 implements p2.l<PhoneAccountHandle, kotlin.g2> {
        a() {
            super(1);
        }

        public final void c(@g4.f PhoneAccountHandle phoneAccountHandle) {
            if (phoneAccountHandle != null) {
                Bundle bundle = new Bundle();
                DialerActivity dialerActivity = DialerActivity.this;
                bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
                bundle.putBoolean("android.telecom.extra.START_CALL_WITH_VIDEO_STATE", false);
                bundle.putBoolean("android.telecom.extra.START_CALL_WITH_SPEAKERPHONE", false);
                com.cutestudio.commons.extensions.a0.N0(dialerActivity).placeCall(dialerActivity.f16956g0, bundle);
            }
            DialerActivity.this.finish();
        }

        @Override // p2.l
        public /* bridge */ /* synthetic */ kotlin.g2 m(PhoneAccountHandle phoneAccountHandle) {
            c(phoneAccountHandle);
            return kotlin.g2.f35117a;
        }
    }

    @a.a({"MissingPermission"})
    private final void P1() {
        try {
            com.cutestudio.dialer.extensions.a.b(this, getIntent(), String.valueOf(this.f16956g0), new a());
        } catch (Exception e5) {
            com.cutestudio.commons.extensions.a0.F1(this, e5, 0, 2, null);
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cutestudio.commons.activities.BaseSimpleActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i4, int i5, @g4.f Intent intent) {
        super.onActivityResult(i4, i5, intent);
        if (i4 == 1005) {
            if (!com.cutestudio.commons.extensions.a0.g1(this)) {
                finish();
            } else {
                P1();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cutestudio.commons.activities.BaseSimpleActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(@g4.f Bundle bundle) {
        super.onCreate(bundle);
        if (kotlin.jvm.internal.l0.g(getIntent().getAction(), "android.intent.action.CALL") && getIntent().getData() != null) {
            this.f16956g0 = getIntent().getData();
            if (!com.cutestudio.commons.extensions.a0.g1(this)) {
                m1();
                return;
            } else {
                P1();
                return;
            }
        }
        com.cutestudio.commons.extensions.a0.K1(this, R.string.unknown_error_occurred, 0, 2, null);
        finish();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(@g4.e Menu menu) {
        kotlin.jvm.internal.l0.p(menu, "menu");
        BaseSimpleActivity.J1(this, menu, false, 0, 6, null);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // com.cutestudio.dialer.activities.SimpleActivity, com.cutestudio.commons.activities.BaseSimpleActivity
    public void s0() {
        this.f16957h0.clear();
    }

    @Override // com.cutestudio.dialer.activities.SimpleActivity, com.cutestudio.commons.activities.BaseSimpleActivity
    @g4.f
    public View t0(int i4) {
        Map<Integer, View> map = this.f16957h0;
        View view = map.get(Integer.valueOf(i4));
        if (view != null) {
            return view;
        }
        View findViewById = findViewById(i4);
        if (findViewById == null) {
            return null;
        }
        map.put(Integer.valueOf(i4), findViewById);
        return findViewById;
    }
}
