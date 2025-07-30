package com.lookout.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/* compiled from: C2DMUtils.java */
/* loaded from: classes.dex */
public class h {
    public static void a(Context context) {
        if (bo.b() >= 8) {
            Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
            intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
            intent.putExtra("sender", "c2dm@mylookout.com");
            context.startService(intent);
        }
    }
}
