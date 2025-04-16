package com.estrongs.android.pop.ftp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.estrongs.android.ftp.ESFtpService;
import com.estrongs.android.ftp.a;
import com.estrongs.android.ftp.c;
import com.estrongs.android.pop.R;
import com.estrongs.android.pop.esclasses.g;
import com.estrongs.android.pop.h;
import com.estrongs.android.statistics.b;
import com.integralads.avid.library.intowow.session.internal.InternalAvidAdSessionContext;

/* loaded from: classes.dex */
public class ESFtpShortcut extends g {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.estrongs.android.pop.esclasses.g, android.support.v7.app.AppCompatActivity, android.support.v4.app.j, android.support.v4.app.af, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isStart", false)) {
            b.a().c("act3", "remote_manager");
        }
        final int intExtra = intent.getIntExtra(InternalAvidAdSessionContext.CONTEXT_MODE, 1);
        final boolean booleanExtra = intent.getBooleanExtra("adbControl", false);
        try {
            final c a = c.a();
            a.a(this, new c.a() { // from class: com.estrongs.android.pop.ftp.ESFtpShortcut.1
                @Override // com.estrongs.android.ftp.c.a
                public void a() {
                    try {
                        if (intExtra == 1) {
                            h a2 = h.a();
                            String X = a2.X();
                            int Z = a2.Z();
                            String Y = a2.Y();
                            a.a(X, a2.W(), Z, Y);
                            int d = a.d();
                            if (d != 0) {
                                String str = null;
                                if (d == 1) {
                                    str = ESFtpShortcut.this.getString(R.string.ftp_server_start_error);
                                } else if (d == 2) {
                                    str = ESFtpShortcut.this.getString(R.string.wifi_network_error);
                                }
                                com.estrongs.android.ui.view.c.a(ESFtpShortcut.this, str, 0);
                                return;
                            }
                            if (booleanExtra && a.e() != null) {
                                a.e().a(true);
                            }
                            if (a.a.intValue() == 1) {
                                Intent intent2 = new Intent(ESFtpShortcut.this, (Class<?>) ESFtpService.class);
                                if (Build.VERSION.SDK_INT >= 26) {
                                    ESFtpShortcut.this.startForegroundService(intent2);
                                } else {
                                    ESFtpShortcut.this.startService(intent2);
                                }
                            }
                        } else if (intExtra == 2) {
                            a.c();
                            ESFtpShortcut.this.stopService(new Intent(ESFtpShortcut.this, (Class<?>) ESFtpService.class));
                        } else if (intExtra == 3) {
                            a.e();
                        } else if (intExtra == 4) {
                            a.f();
                        }
                        a.b(ESFtpShortcut.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
