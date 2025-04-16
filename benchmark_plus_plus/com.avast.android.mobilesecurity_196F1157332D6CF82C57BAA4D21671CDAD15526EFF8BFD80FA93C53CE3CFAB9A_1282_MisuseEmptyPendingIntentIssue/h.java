package com.avast.android.generic.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/* compiled from: C2DMUtils.java */
/* loaded from: classes.dex */
public class h {
    public static void a(Context context, boolean z) {
        com.avast.android.generic.c cVar = (com.avast.android.generic.c) com.avast.android.generic.s.a(context, com.avast.android.generic.q.class);
        com.avast.android.generic.c cVar2 = (com.avast.android.generic.c) com.avast.android.generic.s.a(context, com.avast.android.generic.i.class);
        if (cVar.H() && Integer.parseInt(Build.VERSION.SDK) >= 8) {
            String F = cVar2.F();
            boolean z2 = F != null;
            x.a("AvastComms", context, "C2DM request, current reg ID before C2DM request is " + F);
            if (!z2 || z) {
                x.a("AvastComms", context, "C2DM requestor is requesting new reg ID");
                Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
                intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
                intent.putExtra("sender", "office@itagents.at");
                if (context.startService(intent) == null) {
                    x.a("AvastComms", context, "C2DM requestor failed requesting new reg ID");
                } else {
                    x.a("AvastComms", context, "C2DM requestor requested new reg ID successfully");
                }
            }
        }
    }
}
