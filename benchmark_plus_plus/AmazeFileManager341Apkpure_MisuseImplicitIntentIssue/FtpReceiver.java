package com.amaze.filemanager.asynchronous.services.ftp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* loaded from: classes.dex */
public class FtpReceiver extends BroadcastReceiver {
    static final String TAG = FtpReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received: " + intent.getAction());
        try {
            Intent intent2 = new Intent(context, (Class<?>) FtpService.class);
            intent2.putExtras(intent);
            if (intent.getAction().equals("com.amaze.filemanager.services.ftpservice.FTPReceiver.ACTION_START_FTPSERVER") && !FtpService.isRunning()) {
                context.startService(intent2);
            } else if (intent.getAction().equals("com.amaze.filemanager.services.ftpservice.FTPReceiver.ACTION_STOP_FTPSERVER")) {
                context.stopService(intent2);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
