package com.smartonline.mobileapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.smartonline.mobileapp.components.SmartActionBar;
import com.smartonline.mobileapp.config_json.AndroidAppConfigJsonData;
import com.smartonline.mobileapp.config_json.ConfigJsonModuleData;
import com.smartonline.mobileapp.database.DatabaseManager;
import com.smartonline.mobileapp.global.AppConstants;
import com.smartonline.mobileapp.modules.ModuleConstants;
import com.smartonline.mobileapp.services.sync_svr.DCMSyncRemoteSvrService;
import com.smartonline.mobileapp.utilities.AppUtility;
import com.smartonline.mobileapp.utilities.HttpUtils;
import com.smartonline.mobileapp.utilities.SmartLog;
import com.smartonline.mobileapp.utilities.debug.DebugLog;
import com.smartonline.mobileapp.utilities.imagemanager.ImageManager;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class SmartApplication extends Application {
    private static int sAppIconResId;
    public static String sAppName;
    private static int sBackgroundColor;
    private static int sBackgroundResId;
    public static String sGimbalContentSenderId;
    public static boolean sGimbalFeatureEnabled = false;
    private static String sPackageId;
    private static int sSplashScreenResId;
    private boolean mEnableCalculatorModulePref = false;
    private boolean mEnableRSSModulePref = false;
    private boolean mEnableRegistrationModulePref = false;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        DebugLog.setInDebugMode(false);
        DebugLog.setStrictMode(false);
        trustAllHttpsCerts();
        disableConnectionReuseIfNecessary();
        if (!isDebuggable()) {
            SmartLog.setLoggingLevel(SmartLog.LoggingLevel.Error);
        } else {
            SmartLog.setLoggingLevel(SmartLog.LoggingLevel.Verbose);
        }
        generateAndSaveMDG();
        HttpUtils.setSmartApplication(this);
        ImageManager.init(this, false);
        DatabaseManager.initialize(this);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        DCMSyncRemoteSvrService.startSyncServerService(this, DCMSyncRemoteSvrService.DCM_START_ALARM_SERVICE);
    }

    @Override // android.app.Application
    public void onTerminate() {
        DCMSyncRemoteSvrService.stopSyncServerService();
        super.onTerminate();
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:30:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9:0x003f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void initSmartApplication(java.lang.String r12, int r13, int r14, int r15) {
        /*
            r11 = this;
            com.smartonline.mobileapp.SmartApplication.sAppName = r12
            com.smartonline.mobileapp.SmartApplication.sAppIconResId = r14
            com.smartonline.mobileapp.SmartApplication.sSplashScreenResId = r15
            r0 = 0
            android.content.Context r8 = r11.getApplicationContext()     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            java.lang.String r5 = com.smartonline.mobileapp.utilities.FileIOUtils.readRawTextFile(r8, r13)     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            com.smartonline.mobileapp.preferences.AppConfigDataPrefs.saveAppConfigJsonToPrefs(r11, r5)     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            r2.<init>(r5)     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            com.smartonline.mobileapp.config_json.AndroidAppConfigJsonData r1 = new com.smartonline.mobileapp.config_json.AndroidAppConfigJsonData     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            r8 = 0
            r1.<init>(r2, r8)     // Catch: java.io.IOException -> L98 org.json.JSONException -> L9d
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            r9 = 0
            java.lang.String r10 = "SmartApplication config json"
            r8[r9] = r10     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            r9 = 1
            r8[r9] = r1     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            com.smartonline.mobileapp.utilities.debug.DebugLog.d(r8)     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            r11.configureSettingsPreferences(r1)     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationData r8 = r1.applicationConfigData     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationThemeData r8 = r8.appTheme     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            java.lang.String r8 = r8.appColorText     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            r9 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            int r8 = com.smartonline.mobileapp.utilities.ColorUtils.parseMCDTextColor(r8, r9)     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            com.smartonline.mobileapp.ui.components.ComponentConstants.DEFAULT_TEXT_COLOR = r8     // Catch: org.json.JSONException -> Lbb java.io.IOException -> Lbe
            r0 = r1
        L3d:
            if (r0 == 0) goto L7b
            r11.initializeBackOfficeUrl(r0)
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationData r8 = r0.applicationConfigData
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationConfigData r8 = r8.configData
            java.lang.String r3 = r8.backgroundImage
            int r8 = com.smartonline.mobileapp.utilities.AppUtility.getDrawableFromProject(r11, r3)
            com.smartonline.mobileapp.SmartApplication.sBackgroundResId = r8
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationData r8 = r0.applicationConfigData
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationThemeData r8 = r8.appTheme
            java.lang.String r8 = r8.appColorBg
            int r8 = com.smartonline.mobileapp.utilities.ColorUtils.parseMCDColor(r8)
            com.smartonline.mobileapp.SmartApplication.sBackgroundColor = r8
            com.google.android.gcm.GCMRegistrar.checkDevice(r11)     // Catch: java.lang.UnsupportedOperationException -> Lae
            com.google.android.gcm.GCMRegistrar.checkManifest(r11)     // Catch: java.lang.UnsupportedOperationException -> Lae
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationData r8 = r0.applicationConfigData     // Catch: java.lang.UnsupportedOperationException -> Lae
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationConfigData r8 = r8.configData     // Catch: java.lang.UnsupportedOperationException -> Lae
            java.lang.String r6 = r8.gcmSenderId     // Catch: java.lang.UnsupportedOperationException -> Lae
            java.lang.String r7 = com.google.android.gcm.GCMRegistrar.getRegistrationId(r11)     // Catch: java.lang.UnsupportedOperationException -> Lae
            java.lang.String r8 = ""
            boolean r8 = r8.equals(r7)     // Catch: java.lang.UnsupportedOperationException -> Lae
            if (r8 == 0) goto La2
            r8 = 1
            java.lang.String[] r8 = new java.lang.String[r8]     // Catch: java.lang.UnsupportedOperationException -> Lae
            r9 = 0
            r8[r9] = r6     // Catch: java.lang.UnsupportedOperationException -> Lae
            com.google.android.gcm.GCMRegistrar.register(r11, r8)     // Catch: java.lang.UnsupportedOperationException -> Lae
        L7b:
            if (r0 == 0) goto L97
            com.smartonline.mobileapp.config_json.ConfigJsonAppFeaturesData r8 = r0.appFeaturesConfigData
            if (r8 == 0) goto L97
            com.smartonline.mobileapp.config_json.ConfigJsonAppFeaturesData r8 = r0.appFeaturesConfigData
            com.smartonline.mobileapp.config_json.ConfigJsonGimbalData r8 = r8.mGimbalConfigData
            if (r8 == 0) goto L97
            com.smartonline.mobileapp.config_json.ConfigJsonAppFeaturesData r8 = r0.appFeaturesConfigData
            com.smartonline.mobileapp.config_json.ConfigJsonGimbalData r8 = r8.mGimbalConfigData
            boolean r8 = r8.mEnabled
            com.smartonline.mobileapp.SmartApplication.sGimbalFeatureEnabled = r8
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationData r8 = r0.applicationConfigData
            com.smartonline.mobileapp.config_json.ConfigJsonApplicationConfigData r8 = r8.configData
            java.lang.String r8 = r8.gcmSenderId
            com.smartonline.mobileapp.SmartApplication.sGimbalContentSenderId = r8
        L97:
            return
        L98:
            r4 = move-exception
        L99:
            r4.printStackTrace()
            goto L3d
        L9d:
            r4 = move-exception
        L9e:
            r4.printStackTrace()
            goto L3d
        La2:
            r8 = 1
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch: java.lang.UnsupportedOperationException -> Lae
            r9 = 0
            java.lang.String r10 = "Already registered"
            r8[r9] = r10     // Catch: java.lang.UnsupportedOperationException -> Lae
            com.smartonline.mobileapp.utilities.debug.DebugLog.v(r8)     // Catch: java.lang.UnsupportedOperationException -> Lae
            goto L7b
        Lae:
            r4 = move-exception
            r8 = 1
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            java.lang.String r10 = "This device does not support GCM."
            r8[r9] = r10
            com.smartonline.mobileapp.utilities.debug.DebugLog.ex(r4, r8)
            goto L7b
        Lbb:
            r4 = move-exception
            r0 = r1
            goto L9e
        Lbe:
            r4 = move-exception
            r0 = r1
            goto L99
        */
        throw new UnsupportedOperationException("Method not decompiled: com.smartonline.mobileapp.SmartApplication.initSmartApplication(java.lang.String, int, int, int):void");
    }

    private void initializeBackOfficeUrl(AndroidAppConfigJsonData appData) {
        String backOfficeIpAddress = appData.applicationConfigData.configData.backOfficeURL;
        sPackageId = appData.applicationConfigData.configData.backOfficePackageId;
        DebugLog.d(backOfficeIpAddress);
        DebugLog.d(sPackageId);
    }

    public static String getPackageId() {
        return sPackageId;
    }

    private void configureSettingsPreferences(AndroidAppConfigJsonData appData) {
        ConfigJsonModuleData[] moduleDataArr = appData.modulesConfigData;
        for (ConfigJsonModuleData moduleData : moduleDataArr) {
            if (ModuleConstants.MCDModuleTypes.REGISTRATION_MODULE.equals(moduleData.dataType)) {
                setEnableRegistrationModulePref(true);
            }
            if (ModuleConstants.MCDModuleTypes.CALC_BORROWING_MODULE.equals(moduleData.dataType) || ModuleConstants.MCDModuleTypes.CALC_REPAYMENT_MODULE.equals(moduleData.dataType) || ModuleConstants.MCDModuleTypes.CALC_STAMP_DUTY_MODULE.equals(moduleData.dataType)) {
                setEnableCalculatorModulePref(true);
            }
            if (ModuleConstants.MCDModuleTypes.RSS_MODULE.equals(moduleData.dataType)) {
                setEnableRSSModulePref(true);
            }
        }
    }

    public static String getAppName() {
        return sAppName;
    }

    public static int getAppIconResId() {
        return sAppIconResId;
    }

    public static int getSplashScreenResId() {
        return sSplashScreenResId;
    }

    public static int getBakcgroundResId() {
        return sBackgroundResId;
    }

    public static int getBakcgroundColor() {
        return sBackgroundColor;
    }

    public boolean isEnableCalculatorModulePref() {
        return this.mEnableCalculatorModulePref;
    }

    public void setEnableCalculatorModulePref(boolean state) {
        this.mEnableCalculatorModulePref = state;
    }

    public boolean isEnableRSSModulePref() {
        return this.mEnableRSSModulePref;
    }

    public void setEnableRSSModulePref(boolean enableRSSModulePref) {
        this.mEnableRSSModulePref = enableRSSModulePref;
    }

    public boolean isEnableRegistrationModulePref() {
        return this.mEnableRegistrationModulePref;
    }

    public void setEnableRegistrationModulePref(boolean mEnableRegsitrationModulePref) {
        this.mEnableRegistrationModulePref = mEnableRegsitrationModulePref;
    }

    public boolean isDebuggable() {
        if (DebugLog.isInDebugMode()) {
            DebugLog.v(DebugLog.METHOD_START, "isDebuggable()");
        }
        boolean debug = false;
        ApplicationInfo applicationInfo = getApplicationInfo();
        if (applicationInfo != null) {
            debug = (applicationInfo.flags & 2) != 0;
        }
        if (DebugLog.isInDebugMode()) {
            DebugLog.v(DebugLog.METHOD_END, Boolean.valueOf(debug));
        }
        return debug;
    }

    public boolean isOnline() {
        NetworkInfo netInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void disableConnectionReuseIfNecessary() {
        HttpURLConnection.setFollowRedirects(true);
        if (Build.VERSION.SDK_INT < 8) {
            System.setProperty("http.keepAlive", SmartActionBar.FALSE);
        }
    }

    private void trustAllHttpsCerts() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.smartonline.mobileapp.SmartApplication.1
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() { // from class: com.smartonline.mobileapp.SmartApplication.2
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            DebugLog.ex(e, new Object[0]);
        }
    }

    private void generateAndSaveMDG() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.SharedPrefs.REGISTRATION_PREF, 0);
        SharedPreferences.Editor prefEditor = prefs.edit();
        String mdgString = AppUtility.generateHash("mobilesmith-android" + generateUniqueId(), null);
        if (DebugLog.isInDebugMode()) {
            DebugLog.d(AppConstants.SharedPrefs.MDG, mdgString);
        }
        prefEditor.putString(AppConstants.SharedPrefs.MDG, mdgString);
        prefEditor.commit();
    }

    private String generateUniqueId() {
        String uniqueId = Build.SERIAL;
        if (DebugLog.isInDebugMode()) {
            DebugLog.d("UniqueId (SERIAL)", uniqueId);
        }
        if (TextUtils.isEmpty(uniqueId)) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
                uniqueId = telephonyManager.getDeviceId();
                if (DebugLog.isInDebugMode()) {
                    DebugLog.d("UniqueId (MEID/IMEI)", uniqueId);
                }
            } catch (Exception ex) {
                DebugLog.ex(ex, new Object[0]);
            }
            if (TextUtils.isEmpty(uniqueId)) {
                try {
                    WifiManager manager = (WifiManager) getSystemService("wifi");
                    WifiInfo wifiInfo = manager.getConnectionInfo();
                    uniqueId = wifiInfo.getMacAddress();
                    if (DebugLog.isInDebugMode()) {
                        DebugLog.d("UniqueId (WiFi MAC Address)", uniqueId);
                    }
                } catch (Exception ex2) {
                    DebugLog.ex(ex2, new Object[0]);
                }
                if (TextUtils.isEmpty(uniqueId)) {
                    try {
                        uniqueId = Settings.Secure.getString(getContentResolver(), "android_id");
                        if (DebugLog.isInDebugMode()) {
                            DebugLog.d("UniqueId (Android ID)", uniqueId);
                        }
                    } catch (Exception ex3) {
                        DebugLog.ex(ex3, new Object[0]);
                    }
                }
            }
        }
        return uniqueId;
    }
}
