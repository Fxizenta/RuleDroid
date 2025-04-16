package com.islonline.isllight.android;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.WindowManager;
import com.crittercism.app.Crittercism;
import com.crittercism.app.CrittercismConfig;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.islonline.android.common.jni.JNICrashHandlerService;
import com.islonline.android.common.jni.JNIInitializer;
import com.islonline.isllight.android.api.INativeApi;
import com.islonline.isllight.android.ioc.ProductionModule;
import com.islonline.isllight.android.translation.Translations;
import com.islonline.isllight.android.util.DeviceModelInfo;
import com.islonline.isllight.android.util.IslLog;
import com.islonline.isllight.mobile.android.R;
import dagger.ObjectGraph;
import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class IslLightApplication extends Application implements JNIInitializer {
    private static final String TAG = "IslLightApplication";
    private static IslLightApplication self;
    private Display _display;
    private boolean _initialized;
    private SharedPreferences _preferences;
    private ObjectGraph objectGraph;
    private Map<String, WeakReference<Object>> _asyncTasks = Collections.synchronizedMap(new HashMap());
    private Map<Translations.LanguageChangedListener, Object> _languageChangedListeners = Collections.synchronizedMap(new WeakHashMap());
    private boolean _computerListRefreshRequired = false;
    private Boolean _isDebuggable = null;

    public static IslLightApplication getApplication() {
        return self;
    }

    @Override // com.islonline.android.common.jni.JNIInitializer
    public void initialize(Context context) {
        int crittercismStringId;
        if (this._initialized) {
            IslLog.d(TAG, "App already initialized.");
            return;
        }
        this._initialized = true;
        String versionName = "n/a";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        IslLog.d(TAG, "Initializing Crittercism");
        if (isDebuggable()) {
            crittercismStringId = R.string.crittercism_dev;
        } else {
            crittercismStringId = versionName.contains("beta") ? R.string.crittercism_beta : R.string.crittercism_prod;
        }
        CrittercismConfig crittercismConfig = new CrittercismConfig();
        crittercismConfig.setCustomVersionName(versionName);
        crittercismConfig.setNdkCrashReportingEnabled(true);
        crittercismConfig.setLogcatReportingEnabled(true);
        crittercismConfig.setVersionCodeToBeIncludedInVersionString(false);
        Crittercism.initialize(getApplicationContext(), getString(crittercismStringId), crittercismConfig);
        if (context instanceof JNICrashHandlerService) {
            IslLog.i(TAG, "This is apparently crash handler service, so stopping initialization here.");
            return;
        }
        Bridge.configureCrashHandler(this);
        DisplaySize sz = getDisplaySize();
        int width = sz.width;
        int height = sz.height;
        String picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Bridge.setDeviceInfo(Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase(Locale.US), Build.VERSION.SDK_INT, DeviceModelInfo.isRooted(), width, height, picturesDirectory);
        IslLog.i(TAG, "Starting crash handler service...");
        Intent intent = new Intent(this, (Class<?>) JNICrashHandlerService.class);
        startService(intent);
        IslLog.setLogLevel(isDebuggable() ? 0 : 3);
        this._preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.objectGraph = ObjectGraph.create(new ProductionModule(this));
        IslLog.i(TAG, "Exporting embedded resources (cert, translations...) and setting things up...");
        Bridge.exportEmbeddedResourcesToFileSystemAndConfigurePaths(this);
        IslLog.i(TAG, "Setting up file logging");
        boolean logToFile = this._preferences.getBoolean("logtofile", true);
        IslLog.setFileLogging(logToFile);
        setLanguage();
        boolean isGaAllowed = this._preferences.getBoolean(Constants.ENABLE_GA_KEY, true);
        if (!isGaAllowed) {
            GoogleAnalytics.getInstance(this).setAppOptOut(!isGaAllowed);
        }
        this._preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.islonline.isllight.android.IslLightApplication.1
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(Constants.ENABLE_GA_KEY)) {
                    IslLog.d(IslLightApplication.TAG, "Changing usage collection preference!");
                    GoogleAnalytics.getInstance(IslLightApplication.this.getApplicationContext()).setAppOptOut(sharedPreferences.getBoolean(key, true) ? false : true);
                }
            }
        });
        trustEveryone();
        Bridge.setCaptureFormat(this._preferences.getBoolean(Constants.CAPTURE_IN_PNG, false) ? 2 : 1);
    }

    private void enableStrictMode() {
        if (!isDebuggable()) {
            IslLog.i(TAG, "Strict mode will not be enabled!");
        }
        IslLog.w(TAG, "################################ STRICT MODE IS ENABLED ###################################");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        IslLog.i(TAG, "onCreate...");
        self = this;
    }

    public ObjectGraph objectGraph() {
        if (!this._initialized) {
            IslLog.w(TAG, "Requesting objectGraph, but IslApplication has not been initialized!");
            initialize(this);
        }
        return this.objectGraph;
    }

    public void setCurrentTask(Object task, String key) {
        this._asyncTasks.put(key, new WeakReference<>(task));
        IslLog.d(TAG, "Storing task: " + task + " under key " + key);
    }

    public Object getCurrentTask(String key) {
        if (!this._asyncTasks.containsKey(key)) {
            return null;
        }
        WeakReference<Object> wr = this._asyncTasks.get(key);
        Object task = wr.get();
        if (task == null) {
            this._asyncTasks.remove(key);
            return task;
        }
        return task;
    }

    public String getLanguage() {
        IslLog.i(TAG, "Retrieving current language");
        String locale = Locale.getDefault().getLanguage();
        IslLog.d(TAG, "Current locale: " + locale);
        if ("pt_BR".equals(Locale.getDefault().toString())) {
            locale = "ptbr";
        }
        String chosenLanguage = this._preferences.getString(Constants.LANGUAGE_KEY, Constants.LANGUAGE_DEFAULT);
        IslLog.d(TAG, "Language setting in preferences: " + chosenLanguage);
        return Constants.LANGUAGE_DEFAULT.equals(chosenLanguage) ? locale : chosenLanguage;
    }

    public void addLanguageChangedListener(Translations.LanguageChangedListener listener) {
        this._languageChangedListeners.put(listener, null);
    }

    public void notifyLanguageChangedListeners() {
        for (Translations.LanguageChangedListener l : this._languageChangedListeners.keySet()) {
            l.onLanguageChanged();
        }
    }

    public static void broadcastLogoutEvent(boolean goToLogin) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplication());
        Intent intent = new Intent();
        intent.setAction("com.islonline.isllight.android.ACTION_LOGOUT");
        if (goToLogin) {
            intent.putExtra("authexception", true);
        }
        broadcastManager.sendBroadcast(intent);
    }

    private void setLanguage() {
        String code = getLanguage();
        IslLog.i(TAG, "Setting the language to " + code);
        INativeApi nativeApi = (INativeApi) this.objectGraph.get(INativeApi.class);
        nativeApi.setLanguage(code);
    }

    private boolean isDebuggable() {
        if (this._isDebuggable == null) {
            PackageManager pm = getPackageManager();
            try {
                ApplicationInfo appinfo = pm.getApplicationInfo(getPackageName(), 0);
                int i = appinfo.flags & 2;
                appinfo.flags = i;
                this._isDebuggable = Boolean.valueOf(i != 0);
            } catch (PackageManager.NameNotFoundException e) {
                this._isDebuggable = false;
            }
        }
        return this._isDebuggable.booleanValue();
    }

    private void trustEveryone() {
        try {
            IslLog.w(TAG, "Trusting all SSL certificates....");
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.islonline.isllight.android.IslLightApplication.2
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() { // from class: com.islonline.isllight.android.IslLightApplication.3
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
            e.printStackTrace();
        }
    }

    public void setComputerListRefreshRequired(boolean value) {
        this._computerListRefreshRequired = value;
    }

    public boolean getComputerListRefreshRequired() {
        return this._computerListRefreshRequired;
    }

    @SuppressLint({"NewApi"})
    DisplaySize getDisplaySize() {
        DisplaySize returnValue = new DisplaySize();
        try {
            WindowManager wm = (WindowManager) getSystemService("window");
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            if (Build.VERSION.SDK_INT >= 13) {
                display.getSize(size);
                returnValue.width = size.x;
                returnValue.height = size.y;
            } else {
                returnValue.width = display.getWidth();
                returnValue.height = display.getHeight();
            }
        } catch (Exception x) {
            IslLog.e(TAG, "Could not obtain device screen dimensions!", x);
        }
        return returnValue;
    }

    /* loaded from: classes.dex */
    public class DisplaySize {
        public int width = 0;
        public int height = 0;

        public DisplaySize() {
        }
    }
}
