package com.samsung.android.visionintelligence.service.spen.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import androidx.appcompat.widget.ActivityChooserModel;
import androidx.core.app.NotificationCompat;
import com.samsung.android.settings.search.provider.SearchIndexablesContract;
import com.samsung.android.visionintelligence.R;
import com.samsung.android.visionintelligence.bixby.BixbyActionHandler;
import com.samsung.android.visionintelligence.service.spen.util.reflection.SpenReflectionUtils;
import dalvik.annotation.MethodParameters;

/* loaded from: classes2.dex */
public class ForegroundServiceHelper {
    private static final String TAG = "ForegroundServiceHelper";
    private final IBinder mForegroundToken = new Binder();
    private Service mService;

    @MethodParameters(accessFlags = {0}, names = {NotificationCompat.CATEGORY_SERVICE})
    public ForegroundServiceHelper(Service service) {
        this.mService = service;
    }

    @MethodParameters(accessFlags = {0, 0, 0, 0}, names = {"channelId", "resIconId", "title", "contentText"})
    private Notification createAppNotification(String str, int i, String str2, String str3) {
        Service service = this.mService;
        Notification.Builder createAppNotificationBuilder = createAppNotificationBuilder(str);
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts(BixbyActionHandler.Keys.COMMING_PACKAGE, service.getPackageName(), null));
        createAppNotificationBuilder.setSmallIcon(i).setColor(service.getColor(R.color.foreground_notification_text_color)).setContentTitle(str2).setContentText(str3).setContentIntent(PendingIntent.getActivity(service, 0, intent, 67108864)).setStyle(new Notification.BigTextStyle().bigText(str3));
        return createAppNotificationBuilder.build();
    }

    @MethodParameters(accessFlags = {0}, names = {"channelId"})
    private Notification.Builder createAppNotificationBuilder(String str) {
        ensureOsVersionO();
        try {
            return new Notification.Builder(this.mService, str);
        } catch (Exception e) {
            Log.e(TAG, "createAppNotificationBuilder : e=" + e, e);
            return null;
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"channelId", "name"})
    private void createNotificationChannel(String str, String str2) {
        ensureOsVersionO();
        try {
            ((NotificationManager) this.mService.getSystemService("notification")).createNotificationChannel(new NotificationChannel(str, str2, 2));
        } catch (Exception e) {
            Log.e(TAG, "createNotificationChannel : e=" + e, e);
        }
    }

    private void ensureOsVersionO() {
        Assert.e(OsVersion.isSupportO(), "The OS version should be O(=26) or heigher");
    }

    @MethodParameters(accessFlags = {0}, names = {"isForeground"})
    private void setProcessForeground(boolean z) {
        Log.d(TAG, "setProcessForeground : " + z);
        Assert.e(!OsVersion.isSupportO());
        try {
            SpenReflectionUtils.invokeMethod((ActivityManager) this.mService.getSystemService(ActivityChooserModel.ATTRIBUTE_ACTIVITY), "semSetProcessForeground", this.mForegroundToken, Integer.valueOf(Process.myPid()), Boolean.valueOf(z));
        } catch (Exception e) {
            Log.v(TAG, "setProcessForground : e = " + e, e);
        }
    }

    @MethodParameters(accessFlags = {0, 0, 0, 0}, names = {"channelId", "channelName", SearchIndexablesContract.BaseColumns.COLUMN_ICON_RESID, "appName"})
    private void startForegroundWithNotification(String str, String str2, int i, String str3) {
        Log.d(TAG, "startForegroundWithNotification : " + str + " / " + str2);
        ensureOsVersionO();
        createNotificationChannel(str, str2);
        Service service = this.mService;
        Notification createAppNotification = createAppNotification(str, i, service.getString(R.string.running_in_background, str3), service.getString(R.string.foreground_notification_content_text));
        createAppNotification.extras.putCharSequence("android.substName", str3);
        this.mService.startForeground(hashCode(), createAppNotification);
    }

    private void stopForegroundWithRemovingNotification() {
        ensureOsVersionO();
        this.mService.stopForeground(true);
    }

    public void setServiceBackground() {
        if (OsVersion.isSupportO()) {
            stopForegroundWithRemovingNotification();
        } else {
            setProcessForeground(false);
        }
    }

    @MethodParameters(accessFlags = {0, 0, 0, 0}, names = {"channelId", "channelName", SearchIndexablesContract.BaseColumns.COLUMN_ICON_RESID, "appName"})
    public void setServiceForeground(String str, String str2, int i, String str3) {
        if (OsVersion.isSupportO()) {
            startForegroundWithNotification(str, str2, i, str3);
        } else {
            setProcessForeground(true);
        }
    }
}
