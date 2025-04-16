package com.softphone.b;

import android.app.Activity;
import com.softphone.common.k;
import com.softphone.common.s;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class i implements Runnable {

    /* renamed from: a, reason: collision with root package name */
    final /* synthetic */ c f175a;
    private boolean b;

    public i(c cVar, boolean z) {
        this.f175a = cVar;
        this.b = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        Activity activity;
        Activity activity2;
        b bVar;
        try {
            activity = this.f175a.c;
            String a2 = s.a("config.properties", "updateinfo_serverurl", activity);
            k.a("VersionManager", "upgradeUrl:" + a2);
            this.f175a.b = a.a(com.softphone.common.f.a(a2).a());
            activity2 = this.f175a.c;
            int b = a.b(activity2);
            bVar = this.f175a.b;
            if (bVar.a() <= b) {
                k.a("VersionManager", "已经最新版本，无需升级");
                if (this.b) {
                    this.f175a.f169a.sendEmptyMessage(4);
                }
            } else {
                k.a("VersionManager", "有新版本 ,提示用户升级 ");
                this.f175a.f169a.sendEmptyMessage(0);
            }
        } catch (Exception e) {
            k.a("VersionManager", "Exception:" + e.getMessage());
            if (this.b) {
                this.f175a.f169a.sendEmptyMessage(1);
            }
            e.printStackTrace();
        } finally {
            c.h = false;
        }
    }
}
