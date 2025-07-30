package com.innogames.core.frontend.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.innogames.core.corelogger.CoreLogger;
import com.innogames.core.frontend.notifications.CoreNotificationBuilder;
import com.innogames.core.frontend.notifications.NotificationsUtils;
import com.innogames.core.frontend.notifications.data.NotificationParserLocal;

/* loaded from: classes.dex */
public class LocalNotificationBroadcastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        CoreLogger.Verbose(NotificationsUtils.LOGGER_ENUM_NAME, NotificationsUtils.LOGGER_TAG, "LocalNotificationBroadcastReceiver");
        CoreNotificationBuilder.cacheMainActivityClass(context, intent.getComponent().getPackageName());
        CoreNotificationBuilder.displayNotification(context, NotificationParserLocal.parse(intent.getBundleExtra(NotificationParserLocal.INTENT_EXTRA)));
    }
}
