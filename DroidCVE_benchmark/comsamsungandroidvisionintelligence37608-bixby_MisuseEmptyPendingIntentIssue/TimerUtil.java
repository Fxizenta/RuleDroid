package com.samsung.context.sdk.samsunganalytics.internal.sender.DMA;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import dalvik.annotation.MethodParameters;

/* loaded from: classes2.dex */
public class TimerUtil {
    @MethodParameters(accessFlags = {0, 0}, names = {"ctx", "i"})
    public static void cancelTimer(Context context, Intent intent) {
        Debug.LogENG("Cancel timer " + System.currentTimeMillis());
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(PendingIntent.getBroadcast(context, 0, intent, 201326592));
    }

    @MethodParameters(accessFlags = {0, 0, 0}, names = {"ctx", "i", "interval"})
    public static void setTimer(Context context, Intent intent, long j) {
        Debug.LogENG("Set timer " + System.currentTimeMillis());
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 201326592);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (Build.VERSION.SDK_INT >= 31) {
            alarmManager.set(0, System.currentTimeMillis() + j, broadcast);
        } else if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(0, System.currentTimeMillis() + j, broadcast);
        } else {
            alarmManager.setExact(0, System.currentTimeMillis() + j, broadcast);
        }
    }
}
