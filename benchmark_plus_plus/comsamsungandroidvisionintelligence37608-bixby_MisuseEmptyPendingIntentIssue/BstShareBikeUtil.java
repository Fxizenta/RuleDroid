package com.samsung.android.visionintelligence.util.link.bst.ShareBike;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import dalvik.annotation.MethodParameters;

/* loaded from: classes2.dex */
public class BstShareBikeUtil {
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "packageName"})
    public static boolean isInstalledApp(Context context, String str) {
        try {
            context.getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "intent"})
    public static void startActivity(Context context, Intent intent) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getApplicationContext().getSystemService("keyguard");
        if (!keyguardManager.isKeyguardLocked()) {
            context.startActivity(intent);
        } else {
            keyguardManager.semSetPendingIntentAfterUnlock(PendingIntent.getActivity(context, 0, intent, 134217728), new Intent());
        }
    }
}
