package com.talkatone.vedroid.ui.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.talkatone.android.R;
import com.talkatone.vedroid.TalkatoneApplication;
import com.talkatone.vedroid.service.XmppService;
import com.talkatone.vedroid.ui.call.LiveCall2;
import com.talkatone.vedroid.utils.a;
import defpackage.a4;
import defpackage.bp;
import defpackage.dt0;
import defpackage.i30;
import defpackage.it1;
import defpackage.lp0;
import defpackage.vr1;
import defpackage.z31;
import defpackage.z41;
import java.util.Timer;
import org.slf4j.LoggerFactory;

/* loaded from: classes3.dex */
public class OutgoingCallInterceptor extends SplashActivity {
    public static final lp0 j = LoggerFactory.b(OutgoingCallInterceptor.class);
    public int i = 0;

    @Override // com.talkatone.vedroid.ui.launcher.SplashActivity, com.talkatone.vedroid.base.activity.TalkatoneActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ((TalkatoneApplication) getApplication()).k(false);
    }

    @Override // com.talkatone.vedroid.ui.launcher.SplashActivity, com.talkatone.vedroid.base.activity.TalkatoneActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public final void onStart() {
        super.onStart();
        q();
    }

    public final void q() {
        int i = 0;
        if (((TalkatoneApplication) getApplication()).a != null && bp.b.a) {
            Intent intent = getIntent();
            if (intent.getBooleanExtra("proceed", false) || "android.intent.action.CALL".equals(intent.getAction())) {
                String numberFromIntent = PhoneNumberUtils.getNumberFromIntent(intent, this);
                String stringExtra = intent.getStringExtra("dtmf");
                if (!TextUtils.isEmpty(numberFromIntent)) {
                    r(numberFromIntent, stringExtra);
                } else {
                    Uri data = intent.getData();
                    intent.getExtras();
                    j.getClass();
                    if (data != null && "tel".equals(data.getScheme())) {
                        r(data.getSchemeSpecificPart(), stringExtra);
                    } else {
                        String stringExtra2 = intent.getStringExtra("com.talkatone.android.extra.PhoneNumber");
                        if (!TextUtils.isEmpty(stringExtra2)) {
                            int intExtra = intent.getIntExtra("com.talkatone.android.extra.NOTIFICATION_TYPE", -1);
                            if (intExtra >= 0) {
                                int B = dt0.B(dt0.G(3)[intExtra]);
                                if (B != 0) {
                                    if (B == 1) {
                                        i30.d.e("notification_action_launch", "inbound_message", "callback");
                                    }
                                } else {
                                    i30.d.e("notification_action_launch", NotificationCompat.CATEGORY_MISSED_CALL, "callback");
                                }
                            }
                            r(stringExtra2, null);
                        } else {
                            a.e(this, String.format(getString(R.string.call_unknown_uri), data), 0);
                        }
                    }
                }
                finish();
            }
            finish();
            return;
        }
        if (this.i < 10) {
            vr1 vr1Var = vr1.i;
            z31 z31Var = new z31(this, i);
            vr1Var.getClass();
            new Timer().schedule(new a4(z31Var, 7), 1000L);
            return;
        }
        a.d(this, R.string.call_start_failed_restart_later, 1);
        finish();
    }

    public final void r(String str, String str2) {
        String b;
        if (!TextUtils.isEmpty(str2)) {
            str = dt0.k(str, ",", str2);
        }
        if (!it1.a(str)) {
            if (str != null && !str.toLowerCase().contains("unknown") && !str.toLowerCase().contains("anonymous")) {
                str.toLowerCase().contains("restricted");
            }
            b = str;
        } else {
            b = z41.b(str);
        }
        if (TextUtils.isEmpty(b)) {
            a.e(this, String.format(getString(R.string.call_cant_call_to), str), 0);
            return;
        }
        XmppService xmppService = ((TalkatoneApplication) getApplicationContext()).a;
        if (xmppService == null) {
            a.d(this, R.string.server_not_connected, 0);
        } else if (xmppService.f().length != 0) {
            a.d(this, R.string.single_call_only, 1);
        } else {
            Intent intent = new Intent(this, (Class<?>) LiveCall2.class);
            intent.putExtra("com.talkatone.android.extra.PhoneNumber", b);
            intent.setFlags(335675392);
            startActivity(intent);
        }
        finish();
    }
}
