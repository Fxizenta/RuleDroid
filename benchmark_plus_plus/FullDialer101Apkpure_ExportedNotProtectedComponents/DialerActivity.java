package com.full.dialer.top.secure.encrypted.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import com.full.dialer.top.secure.encrypted.R;
import f9.l;
import g9.i;
import i3.s0;
import java.util.LinkedHashMap;
import o9.a0;
import p8.r;
import r3.b;
import u8.g;

/* loaded from: classes.dex */
public final class DialerActivity extends s0 {
    public Uri B;

    /* loaded from: classes.dex */
    public static final class a extends i implements l<PhoneAccountHandle, g> {
        public a() {
            super(1);
        }

        @Override // f9.l
        public final g p(PhoneAccountHandle phoneAccountHandle) {
            PhoneAccountHandle phoneAccountHandle2 = phoneAccountHandle;
            if (phoneAccountHandle2 != null) {
                Bundle bundle = new Bundle();
                DialerActivity dialerActivity = DialerActivity.this;
                bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle2);
                bundle.putBoolean("android.telecom.extra.START_CALL_WITH_VIDEO_STATE", false);
                bundle.putBoolean("android.telecom.extra.START_CALL_WITH_SPEAKERPHONE", false);
                r.q(dialerActivity).placeCall(dialerActivity.B, bundle);
            }
            DialerActivity.this.finish();
            return g.f7018a;
        }
    }

    public DialerActivity() {
        new LinkedHashMap();
    }

    @SuppressLint({"MissingPermission"})
    public final void P() {
        try {
            b.b(this, getIntent(), String.valueOf(this.B), new a());
        } catch (Exception e10) {
            r.A(this, e10);
            finish();
        }
    }

    @Override // m8.e, androidx.fragment.app.o, androidx.activity.ComponentActivity, android.app.Activity
    public final void onActivityResult(int i10, int i11, Intent intent) {
        super.onActivityResult(i10, i11, intent);
        if (i10 == 1007) {
            if (!r.u(this)) {
                try {
                    p8.l.d(this);
                    Intent intent2 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent2.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent2);
                    r.B(this, R.string.default_phone_app_prompt, 1);
                } catch (Exception unused) {
                }
                finish();
                return;
            }
            P();
        }
    }

    @Override // m8.e, androidx.fragment.app.o, androidx.activity.ComponentActivity, y.h, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (a0.e(getIntent().getAction(), "android.intent.action.CALL") && getIntent().getData() != null) {
            this.B = getIntent().getData();
            if (!r.u(this)) {
                H();
                return;
            } else {
                P();
                return;
            }
        }
        r.B(this, R.string.unknown_error_occurred, 0);
        finish();
    }
}
