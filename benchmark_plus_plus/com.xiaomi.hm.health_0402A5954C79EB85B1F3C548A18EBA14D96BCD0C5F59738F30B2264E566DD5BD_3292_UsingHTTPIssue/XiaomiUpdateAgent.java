package com.xiaomi.market.sdk;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.google.a.a.a.a.a.a;
import com.xiaomi.market.sdk.Connection;
import com.xiaomi.market.sdk.Constants;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class XiaomiUpdateAgent {
    private static final String TAG = "MarketUpdateAgent";
    private static LocalAppInfo mAppInfo;
    private static UpdateInfo mUpdateInfo;
    private static XiaomiUpdateListener mUpdateListener;
    private static Constants.UpdateMethod mUpdateMethod;
    private static boolean sDebug;
    private static boolean mIsInited = false;
    private static boolean mIsLoading = false;
    private static WeakReference<Context> mContext = new WeakReference<>(null);
    private static boolean mAutoPopup = true;
    private static boolean mCheckUpdateOnlyWifi = false;
    private static boolean mIsPathcerLibraryLoaded = false;

    static {
        mUpdateMethod = Utils.isMiuiPad() ? Constants.UpdateMethod.DOWNLOAD_MANAGER : Constants.UpdateMethod.MARKET;
    }

    public static synchronized void update(Context context) {
        boolean z;
        synchronized (XiaomiUpdateAgent.class) {
            try {
                Class<?> cls = Class.forName(context.getPackageName() + ".BuildConfig");
                z = cls.getDeclaredField("DEBUG").getBoolean(cls);
            } catch (Exception e2) {
                a.a(e2);
                z = false;
            }
            update(context, z);
        }
    }

    public static synchronized void update(Context context, boolean z) {
        synchronized (XiaomiUpdateAgent.class) {
            if (context != null) {
                if (!mIsLoading) {
                    sDebug = z;
                    mIsLoading = true;
                    Client.init(context);
                    mContext = new WeakReference<>(context);
                    if (!mIsInited) {
                        mAppInfo = null;
                        mUpdateInfo = null;
                        Constants.configURL();
                        mIsInited = true;
                    }
                    new CheckUpdateTask().execute(new Void[0]);
                }
            }
        }
    }

    public static void setUseInternationalHost(boolean z) {
        Constants.setUseInternalProductUrl(z);
        Constants.configURL();
    }

    public static void arrange() {
        Context context = mContext.get();
        if (context != null) {
            Client.init(context);
            openMarketOrArrange();
        }
    }

    public static void setUpdateAutoPopup(boolean z) {
        mAutoPopup = z;
    }

    public static void setCheckUpdateOnlyWifi(boolean z) {
        mCheckUpdateOnlyWifi = z;
    }

    public static void setUpdateListener(XiaomiUpdateListener xiaomiUpdateListener) {
        mUpdateListener = xiaomiUpdateListener;
    }

    public static int getSDKVersion() {
        return 2;
    }

    public static void setUpdateMethod(Constants.UpdateMethod updateMethod) {
        mUpdateMethod = updateMethod;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Context getContext() {
        return mContext.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static LocalAppInfo getAppInfo(Context context) {
        PackageInfo packageInfo;
        LocalAppInfo localAppInfo = LocalAppInfo.get(context.getPackageName());
        PackageManager packageManager = context.getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(localAppInfo.packageName, 64);
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "get package info failed");
            packageInfo = null;
        }
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return null;
        }
        localAppInfo.displayName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        localAppInfo.versionCode = packageInfo.versionCode;
        localAppInfo.versionName = packageInfo.versionName;
        localAppInfo.signature = Coder.encodeMD5(String.valueOf(packageInfo.signatures[0].toChars()));
        localAppInfo.sourceDir = packageInfo.applicationInfo.sourceDir;
        localAppInfo.sourceMD5 = Coder.encodeMD5(new File(localAppInfo.sourceDir));
        return localAppInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class UpdateInfo {
        String apkHash;
        long apkSize;
        String apkUrl;
        long diffSize;
        int fitness;
        String host;
        int source;
        String updateLog;
        int versionCode;
        String versionName;
        String diffUrl = "";
        String diffHash = "";

        public String toString() {
            return "UpdateInfo:\nhost = " + this.host + "\nfitness = " + this.fitness + "\nupdateLog = " + this.updateLog + "\nversionCode = " + this.versionCode + "\nversionName = " + this.versionName + "\napkUrl = " + this.apkUrl + "\napkHash = " + this.apkHash + "\napkSize = " + this.apkSize + "\ndiffUrl = " + this.diffUrl + "\ndiffHash = " + this.diffHash + "\ndiffSize = " + this.diffSize;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class CheckUpdateTask extends AsyncTask<Void, Void, Integer> {
        private CheckUpdateTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Log.d(XiaomiUpdateAgent.TAG, "start to check update");
            if (!XiaomiUpdateAgent.mIsPathcerLibraryLoaded) {
                boolean unused = XiaomiUpdateAgent.mIsPathcerLibraryLoaded = Patcher.tryLoadLibrary();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(Void... voidArr) {
            Context context = (Context) XiaomiUpdateAgent.mContext.get();
            if (context == null) {
                return 4;
            }
            if (!Utils.isConnected(context)) {
                return 3;
            }
            if (Utils.isWifiConnected(context) || !XiaomiUpdateAgent.mCheckUpdateOnlyWifi) {
                LocalAppInfo unused = XiaomiUpdateAgent.mAppInfo = XiaomiUpdateAgent.getAppInfo(context);
                if (XiaomiUpdateAgent.mAppInfo == null) {
                    return 5;
                }
                Connection connection = new Connection(Constants.UPDATE_URL);
                connection.getClass();
                Connection.Parameter parameter = new Connection.Parameter(connection);
                parameter.add(Constants.JSON_FILTER_INFO, getFilterParams());
                parameter.add(Constants.JSON_PACKAGE_NAME, XiaomiUpdateAgent.mAppInfo.packageName);
                parameter.add("versionCode", XiaomiUpdateAgent.mAppInfo.versionCode + "");
                parameter.add("apkHash", XiaomiUpdateAgent.mAppInfo.sourceMD5);
                parameter.add(Constants.JSON_SIGNATURE, XiaomiUpdateAgent.mAppInfo.signature);
                parameter.add(Constants.JSON_CLIENT_ID, Client.UUID);
                parameter.add("sdk", String.valueOf(Client.SDK_VERSION));
                parameter.add(Constants.JSON_VERSION, Client.SYSTEM_VERSION);
                parameter.add(Constants.JSON_LANGUAGE, Client.LANGUAGE);
                parameter.add(Constants.JSON_COUNTRY, Client.COUNTRY);
                parameter.add(Constants.JSON_XIAOMI_SDK_VERSION, "2");
                parameter.add(Constants.JSON_DEBUG, XiaomiUpdateAgent.sDebug ? "1" : "0");
                if (Connection.NetworkError.OK == connection.requestJSON()) {
                    UpdateInfo unused2 = XiaomiUpdateAgent.mUpdateInfo = parseUpdateInfo(connection.getResponse());
                    if (XiaomiUpdateAgent.mUpdateInfo != null) {
                        Log.i(XiaomiUpdateAgent.TAG, XiaomiUpdateAgent.mUpdateInfo.toString());
                        return Integer.valueOf(XiaomiUpdateAgent.mUpdateInfo.fitness == 0 ? 0 : 1);
                    }
                }
                return 4;
            }
            return 2;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer num) {
            boolean unused = XiaomiUpdateAgent.mIsLoading = false;
            if (!XiaomiUpdateAgent.mAutoPopup) {
                UpdateResponse updateResponse = new UpdateResponse();
                if (num.intValue() == 0) {
                    updateResponse.updateLog = XiaomiUpdateAgent.mUpdateInfo.updateLog;
                    updateResponse.versionCode = XiaomiUpdateAgent.mUpdateInfo.versionCode;
                    updateResponse.versionName = XiaomiUpdateAgent.mUpdateInfo.versionName;
                    updateResponse.apkSize = XiaomiUpdateAgent.mUpdateInfo.apkSize;
                    updateResponse.apkHash = XiaomiUpdateAgent.mUpdateInfo.apkHash;
                    updateResponse.diffSize = XiaomiUpdateAgent.mUpdateInfo.diffSize;
                    updateResponse.path = Connection.connect(XiaomiUpdateAgent.mUpdateInfo.host, XiaomiUpdateAgent.mUpdateInfo.apkUrl);
                }
                if (XiaomiUpdateAgent.mUpdateListener != null) {
                    XiaomiUpdateAgent.mUpdateListener.onUpdateReturned(num.intValue(), updateResponse);
                    return;
                }
                return;
            }
            switch (num.intValue()) {
                case 0:
                    new CheckDownloadTask().execute(new Void[0]);
                    return;
                default:
                    return;
            }
        }

        private String getFilterParams() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(Constants.JSON_SCREEN_SIZE, Client.DISPLAY_WIDTH + "*" + Client.DISPLAY_HEIGHT);
                jSONObject.put(Constants.JSON_RESOLUTION, Client.DISPLAY_RESOLUTION);
                jSONObject.put(Constants.JSON_DENSITY, Client.DISPLAY_DENSITY);
                jSONObject.put(Constants.JSON_TOUCH_SCREEN, Client.TOUCH_SCREEN);
                jSONObject.put(Constants.JSON_GLES_VERSION, Client.GLES_VERSION);
                jSONObject.put(Constants.JSON_FEATURE, Client.FEATURE);
                jSONObject.put(Constants.JSON_LIBRARY, Client.LIBRARY);
                jSONObject.put(Constants.JSON_GL_EXTENSION, Client.GL_EXTENSION);
                jSONObject.put("sdk", Client.SDK_VERSION);
                jSONObject.put("version", Client.SYSTEM_VERSION);
                jSONObject.put("release", Client.RELEASE);
                jSONObject.put(Constants.JSON_DEVICE_ID, Coder.encodeMD5(Client.IMEI));
                return jSONObject.toString();
            } catch (JSONException e2) {
                return "";
            }
        }

        private UpdateInfo parseUpdateInfo(JSONObject jSONObject) {
            if (jSONObject == null) {
                Log.e(XiaomiUpdateAgent.TAG, "update info json obj null");
                return null;
            }
            if (Utils.DEBUG) {
                Log.d(XiaomiUpdateAgent.TAG, "updateInfo : " + jSONObject.toString());
            }
            UpdateInfo updateInfo = new UpdateInfo();
            try {
                updateInfo.host = jSONObject.getString(Constants.HOST);
                updateInfo.fitness = jSONObject.getInt(Constants.FITNESS);
                updateInfo.source = jSONObject.getInt("source");
                updateInfo.updateLog = jSONObject.getString(Constants.UPDATE_LOG);
                updateInfo.versionCode = jSONObject.getInt("versionCode");
                updateInfo.versionName = jSONObject.getString(Constants.VERSION_NAME);
                updateInfo.apkUrl = jSONObject.getString(Constants.APK_URL);
                updateInfo.apkHash = jSONObject.getString("apkHash");
                updateInfo.apkSize = jSONObject.getLong(Constants.APK_SIZE);
                if (XiaomiUpdateAgent.mIsPathcerLibraryLoaded) {
                    updateInfo.diffUrl = jSONObject.getString(Constants.DIFF_URL);
                    updateInfo.diffHash = jSONObject.getString(Constants.DIFF_HASH);
                    updateInfo.diffSize = jSONObject.getLong(Constants.DIFF_SIZE);
                }
                return updateInfo;
            } catch (JSONException e2) {
                Log.e(XiaomiUpdateAgent.TAG, "get update info failed : " + e2.toString());
                Log.e(XiaomiUpdateAgent.TAG, "original content : " + jSONObject.toString());
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class CheckDownloadTask extends AsyncTask<Void, Void, Boolean> {
            private CheckDownloadTask() {
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                Context context = (Context) XiaomiUpdateAgent.mContext.get();
                if (context != null) {
                    return Boolean.valueOf(DownloadInstallManager.getManager(context).isDownloading(XiaomiUpdateAgent.mAppInfo));
                }
                return false;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                if (!bool.booleanValue()) {
                    CheckUpdateTask.showUpdateDialog();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void showUpdateDialog() {
            String string;
            Context context = (Context) XiaomiUpdateAgent.mContext.get();
            if (context != null) {
                if ((context instanceof Activity) && ((Activity) context).isFinishing()) {
                    Log.e(XiaomiUpdateAgent.TAG, "activity not running!");
                    return;
                }
                AlertDialog.Builder title = new AlertDialog.Builder(context).setTitle(context.getString(Utils.getResourseIdByName(context.getPackageName(), "string", "xiaomi_update_dialog_title"), XiaomiUpdateAgent.mAppInfo.displayName));
                if (TextUtils.isEmpty(XiaomiUpdateAgent.mUpdateInfo.diffUrl)) {
                    string = context.getString(Utils.getResourseIdByName(context.getPackageName(), "string", "xiaomi_update_dialog_message"), XiaomiUpdateAgent.mUpdateInfo.versionName, Utils.getByteString(XiaomiUpdateAgent.mUpdateInfo.apkSize, context));
                } else {
                    string = context.getString(Utils.getResourseIdByName(context.getPackageName(), "string", "xiaomi_update_dialog_message_diff"), XiaomiUpdateAgent.mUpdateInfo.versionName, Utils.getByteString(XiaomiUpdateAgent.mUpdateInfo.diffSize, context));
                }
                title.setMessage(string).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.xiaomi.market.sdk.XiaomiUpdateAgent.CheckUpdateTask.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        XiaomiUpdateAgent.openMarketOrArrange();
                    }
                }).show();
            }
        }
    }

    static void openMarketOrArrange() {
        Context context = mContext.get();
        if (context != null && mUpdateInfo != null && mAppInfo != null) {
            if (mUpdateMethod.equals(Constants.UpdateMethod.MARKET) && mUpdateInfo.source != 1 && Utils.isMiuiMarketExisted(context)) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?back=true&id=" + mAppInfo.packageName));
                intent.setClassName("com.xiaomi.market", "com.xiaomi.market.ui.AppDetailActivity");
                List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
                if (queryIntentActivities != null && queryIntentActivities.size() == 1) {
                    ResolveInfo resolveInfo = queryIntentActivities.get(0);
                    if (resolveInfo.activityInfo != null && resolveInfo.activityInfo.exported && resolveInfo.activityInfo.enabled) {
                        context.startActivity(intent);
                        return;
                    }
                }
            }
            DownloadInstallManager.getManager(context).arrange(mAppInfo, mUpdateInfo);
        }
    }
}
