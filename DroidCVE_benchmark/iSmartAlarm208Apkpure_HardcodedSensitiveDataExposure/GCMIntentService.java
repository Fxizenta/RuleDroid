package iSA.common;

import andon.common.C;
import andon.common.CommonUtilities;
import andon.common.Log;
import andon.isa.database.DataBaseClass;
import andon.isa.database.PreferenceKey;
import andon.isa.database.SharePreferenceOperator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMConstants;
import com.google.android.gcm.GCMRegistrar;

@SuppressLint({"WorldWriteableFiles"})
/* loaded from: classes.dex */
public class GCMIntentService extends GCMBaseIntentService {
    private static final String TAG = "GCMIntentService";
    private static final int VERSION_CODE_LOLLIPOP = 20;
    SharedPreferences sharePref;

    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
        Log.i(TAG, "创建");
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onRegistered(Context context, String registrationId) {
        Log.i("GCMIntentService:onRegistered", "Device registered: regId ===> " + registrationId);
        Log.i("GCMIntentService:context", "Device context ===> " + context.toString());
        SharePreferenceOperator.setStringValue(CommonUtilities.APPLICATION_CONTEXT, PreferenceKey.NOTIFICATION_REGISTER_ID, registrationId.equals(svCode.asyncSetHome) ? "debug" : registrationId);
        SaveRegisteID(context, registrationId);
        CommonUtilities.displayMessage(context, registrationId);
        CommonUtilities.registCount = 0;
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "反注册完成 id=" + registrationId);
        Log.i("GCMIntentService:onUnregistered", "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            GCMRegistrar.setRegisteredOnServer(context, true);
        } else {
            Log.i("GCMIntentService:onUnregistered", "Ignoring unregister callback");
        }
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "收到消息 msg=" + (intent == null ? "空" : intent.getStringExtra(CommonUtilities.EXTRA_MESSAGE)));
        try {
            String action = intent.getAction();
            if (action.equals(GCMConstants.INTENT_FROM_GCM_MESSAGE)) {
                String message = intent.getStringExtra(CommonUtilities.EXTRA_MESSAGE);
                if (message == null) {
                    Log.i(TAG, "null msg, return");
                    return;
                }
                Log.i("GCMIntentService:onMessage:", message);
                try {
                    if (this.sharePref == null) {
                        this.sharePref = context.getSharedPreferences("iSmartAlermData", 0);
                    }
                    String usreName = this.sharePref.getString("phoneNum", svCode.asyncSetHome);
                    String usrePWD = this.sharePref.getString(DataBaseClass.CAMERA_PASSWORD, svCode.asyncSetHome);
                    C.getCurrentUser(TAG).setTels(TAG, usreName);
                    C.getCurrentUser(TAG).setPassWord(TAG, usrePWD);
                } catch (Exception e) {
                    Log.d("GCMIntentService:onMessage 2-1 ", "error:" + e.getMessage());
                    generateNotification(context, message, null);
                }
                Log.d("GCMIntentService:onMessage 8", "即将生成通知栏图标");
                generateNotification(context, message, null);
            }
        } catch (Exception e2) {
            Log.d("GCMIntentService:onMessage 9", "exception：" + e2.getMessage());
            e2.printStackTrace();
        }
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "删除msg");
        Log.i("GCMIntentService:onDeletedMessages", "Received deleted messages notification");
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    public void onError(Context context, String errorId) {
        Log.i(TAG, "错误error");
        Log.i("GCMIntentService:onError", "Received error: " + errorId);
        if (CommonUtilities.registCount >= 5) {
            Log.e("GCMIntentServiceonError", "registCount>=5" + errorId);
            GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
            CommonUtilities.registCount++;
            SystemClock.sleep(60000L);
            return;
        }
        GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
        CommonUtilities.registCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gcm.GCMBaseIntentService
    public boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "可恢复型错误");
        Log.i("GCMIntentServiceonRecoverableError", errorId);
        if (CommonUtilities.registCount >= 5) {
            Log.i("GCMIntentServiceonRecoverableError", "registCount>=5  " + errorId);
            GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
            CommonUtilities.registCount++;
            SystemClock.sleep(60000L);
        } else if (errorId != null && !errorId.equalsIgnoreCase(GCMConstants.ERROR_SERVICE_NOT_AVAILABLE)) {
            GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
            CommonUtilities.registCount++;
        }
        return super.onRecoverableError(context, errorId);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x02c8  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x01b5  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0292 A[Catch: Exception -> 0x039e, TRY_LEAVE, TryCatch #0 {Exception -> 0x039e, blocks: (B:33:0x0281, B:35:0x0292, B:41:0x038f), top: B:32:0x0281 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x038f A[Catch: Exception -> 0x039e, TRY_ENTER, TRY_LEAVE, TryCatch #0 {Exception -> 0x039e, blocks: (B:33:0x0281, B:35:0x0292, B:41:0x038f), top: B:32:0x0281 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0323  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void generateNotification(android.content.Context r26, java.lang.String r27, android.graphics.Bitmap r28) {
        /*
            Method dump skipped, instructions count: 936
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: iSA.common.GCMIntentService.generateNotification(android.content.Context, java.lang.String, android.graphics.Bitmap):void");
    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = Build.VERSION.SDK_INT > 20;
        return useWhiteIcon ? R.drawable.notification_icon : R.drawable.appic;
    }

    public void SaveRegisteID(Context contex, String regId) {
        SharedPreferences sharedPrefrences = contex.getSharedPreferences("REG_KEY", 2);
        SharedPreferences.Editor editor = sharedPrefrences.edit();
        editor.putString("REG_KEY", regId);
        editor.commit();
    }

    @Override // android.app.IntentService, android.app.Service
    public void onStart(Intent intent, int startId) {
        Log.e(TAG, "-----GCMIntentService onstar-------");
        super.onStart(intent, startId);
    }

    @Override // android.app.IntentService, android.app.Service
    public void onDestroy() {
        Log.e(TAG, "-----GCMIntentService onDestroy-------");
        super.onDestroy();
    }
}
