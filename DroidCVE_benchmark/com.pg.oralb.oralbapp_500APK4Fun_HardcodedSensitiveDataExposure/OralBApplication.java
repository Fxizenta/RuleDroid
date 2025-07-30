package com.pg.oralb.oralbapp.application;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import com.iconmobile.android.core.receiver.InternetConnectivityReceiver;
import com.iconmobile.android.core.util.DebugLog;
import com.iconmobile.android.core.util.SecurityUtil;
import com.oralb.bluetooth.brush.BrushManager;
import com.pg.oralb.oralbapp.amazon.AmazonFeatures;
import com.pg.oralb.oralbapp.amazon.common.Callback;
import com.pg.oralb.oralbapp.api.ModulesManager;
import com.pg.oralb.oralbapp.application.preferences.AccessLevel;
import com.pg.oralb.oralbapp.application.preferences.Preferences;
import com.pg.oralb.oralbapp.application.preferences.PreferencesItem;
import com.pg.oralb.oralbapp.dao.queries.ApplicationDatabaseQueries;
import com.pg.oralb.oralbapp.domain.ChallengeScoresProvider;
import com.pg.oralb.oralbapp.domain.achievements.AchievementManager;
import com.pg.oralb.oralbapp.domain.content.ProfessionalProductManager;
import com.pg.oralb.oralbapp.features.AppFeaturesManager;
import com.pg.oralb.oralbapp.features.brush_head.BrushHeadFeatures;
import com.pg.oralb.oralbapp.features.ergo.ErgoFeatures;
import com.pg.oralb.oralbapp.features.kiip.KiipFeatures;
import com.pg.oralb.oralbapp.features.session.SessionFeatures;
import com.pg.oralb.oralbapp.features.session.notifications.SessionNotificationType;
import com.pg.oralb.oralbapp.io.content.ContentLoader;
import com.pg.oralb.oralbapp.journey.JourneyMessagesManager;
import com.pg.oralb.oralbapp.logic.bluetooth.BluetoothManager;
import com.pg.oralb.oralbapp.logic.service.CurrentLocationManager;
import com.pg.oralb.oralbapp.logic.service.DentistSearchManager;
import com.pg.oralb.oralbapp.logic.service.FeedbackManager;
import com.pg.oralb.oralbapp.logic.service.GoogleAnalyticsManager;
import com.pg.oralb.oralbapp.logic.service.NewUserInterface;
import com.pg.oralb.oralbapp.logic.service.OralBCalendarManager;
import com.pg.oralb.oralbapp.logic.service.SoundRecognition;
import com.pg.oralb.oralbapp.logic.service.UICallback;
import com.pg.oralb.oralbapp.logic.settings.BaseManager;
import com.pg.oralb.oralbapp.logic.settings.SettingsManager;
import com.pg.oralb.oralbapp.logic.sm.StateMachine;
import com.pg.oralb.oralbapp.logic.toothbrush.ToothbrushInfoProvider;
import com.pg.oralb.oralbapp.logic.toothbrush.api.ToothBrushNotificationsManager;
import com.pg.oralb.oralbapp.logic.toothbrush.calibration.DeviceAlignmentManager;
import com.pg.oralb.oralbapp.notifications.NotificationController;
import com.pg.oralb.oralbapp.ui.activity.NavigationActivity;
import com.pg.oralb.oralbapp.ui.dialog.DialogManager;
import com.pg.oralb.oralbapp.ui.fragment.base.BaseNavigator;
import com.pg.oralb.oralbapp.util.DeviceClass;
import com.pg.oralb.oralbapp.util.FeatureManager;
import com.pg.oralb.oralbapp.util.ImageCacheContainer;
import com.pg.oralb.oralbapp.util.ImageLoader;
import com.pg.oralb.positiondetectionlibrary.CameraManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jcodec.containers.mps.MPSUtils;

/* loaded from: classes.dex */
public class OralBApplication extends Application implements ModulesManager, Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    public static final long DELAY_BEFORE_RETURN_TO_TIMER = 3600000;
    private static final int ENC_ITERATIONS = 6412;
    private static final String ENC_PW = "testpassword91241473";
    private static final String IV = "36fbcj38dusi2dv5";
    private static final String SALT = "uihfDAIHDI7T63/TZ=)(!NASJDOFU(!=IHBJNJA!";
    private static Context STATIC_CONTEXT = null;
    private static final String TWITTER_KEY = "2UGKNRVIDdgC0r6IPwcC0UcwC";
    private static final String TWITTER_SECRET = "ycXHbtEKNAK2vPGf6snmqayMX3BeAzroLJJwsJYf1P2Smx9ZB5";
    public static Map<String, Typeface> TYPEFACES = new HashMap();
    private static int currentAppExperience = 0;
    private static boolean isInForeground = false;
    private Handler handler;
    private GoogleAnalyticsManager mAnalytics;
    private BaseNavigator mBaseNavigator;
    private BluetoothManager mBluetoothManager;
    private ChallengeScoresProvider mChallengeScoresProvider;
    private ContentLoader mContentLoader;
    private ApplicationDatabaseQueries mDatabaseManager;
    private DeviceAlignmentManager mDeviceAlignmentManager;
    private DialogManager mDialogManager;
    private Preferences mPreferences;
    private SettingsManager mSettingsManager;
    private SoundRecognition mSoundRecognition;
    private StateMachine mStateMachine;
    private ToothbrushInfoProvider mToothbrushProvider;
    private final Runnable pauseTimer = new Runnable() { // from class: com.pg.oralb.oralbapp.application.OralBApplication.1
        @Override // java.lang.Runnable
        public void run() {
            NewUserInterface.getInstance().onApplicationPaused();
            Preferences.getInstance().setLong(PreferencesItem.LAST_TIME_APP_USED, System.currentTimeMillis());
            boolean drsEnabled = AmazonFeatures.getStatus().isDrsEnabled();
            boolean notificationBrushingReminderIsEnabled = SettingsManager.getInstance().getNotificationBrushingReminderIsEnabled();
            SessionFeatures.getUpdateBrushingReminderNotifications().scheduleNotifications(drsEnabled, notificationBrushingReminderIsEnabled);
        }
    };
    public boolean APPLICATION_JUST_STARTED = true;
    public boolean HAS_NATIVE_SHOP = false;
    public boolean REPLACE_BRUSH_HEAD_DIALOG_WAS_SHOWN = false;
    private List<BaseManager> mManagers = new ArrayList();

    public static Typeface getTypeface(String key) {
        return TYPEFACES.get(key);
    }

    public static ModulesManager get(Context context) {
        return (ModulesManager) context.getApplicationContext();
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        isInForeground = true;
        boolean shouldReplace = AmazonFeatures.getCheckStockUseCase().shouldReplaceSilently();
        if (shouldReplace) {
            AmazonFeatures.getBrushHeadReplacedUseCase().brushHeadReplaced();
            AmazonFeatures.getStatus().setLastReplenishDate(0L);
            BrushHeadFeatures.getBrushHeadChangedUseCase().setNewBrushHeadAge(0);
            SettingsManager.getInstance().setCheckRefillsOverlayWasShown(false);
        }
        this.handler.removeCallbacks(this.pauseTimer);
        if (activity instanceof NavigationActivity) {
            SessionFeatures.getUpdateBrushingReminderNotifications().cancelNotifications();
            dismissSystemNotifications();
        }
        NewUserInterface.getInstance().onApplicationResumed();
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        isInForeground = false;
        this.handler.postDelayed(this.pauseTimer, 500L);
        Preferences.getInstance().setLong(PreferencesItem.LAST_TIME_USAGE, System.currentTimeMillis());
        if (!(activity instanceof NavigationActivity) || !SettingsManager.getInstance().getNotificationBrushingReminderIsEnabled() || !SettingsManager.getInstance().isAmazonDrsEnabled()) {
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof NavigationActivity) {
            DebugLog.e("SHOP REMOVE and RESET");
        }
    }

    private void dismissSystemNotifications() {
        NotificationController.dismissSystemNotification(SessionNotificationType.SESSION_PAUSED.getNotificationProvider());
        NotificationController.dismissSystemNotification(SessionNotificationType.SESSION_ENDED.getNotificationProvider());
        NotificationController.dismissSystemNotification(SessionNotificationType.BRUSHING_REMINDER_IN_TWO_DAYS.getNotificationProvider());
        NotificationController.dismissSystemNotification(SessionNotificationType.BRUSHING_REMINDER_IN_TEN_DAYS.getNotificationProvider());
        NotificationController.dismissSystemNotification(SessionNotificationType.BRUSHING_REMINDER_BEFORE_HUNDRED_DAYS.getNotificationProvider());
        NotificationController.dismissSystemNotification(SessionNotificationType.BRUSHING_REMINDER_AFTER_HUNDRED_DAYS.getNotificationProvider());
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        STATIC_CONTEXT = getApplicationContext();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        try {
            SecurityUtil.createInstance(SALT, ENC_PW, ENC_ITERATIONS, IV);
        } catch (Exception e) {
            DebugLog.e("SECURITY UTILS ERROR");
        }
        DebugLog.provideApplicationContext(this);
        DebugLog.enableLog(false);
        DebugLog.d("APP-DB oncreate");
        createManagers();
        initManagers();
        AppFeaturesManager.init(this);
        initDeviceClass();
        DentistSearchManager.getInstance(this);
        ProfessionalProductManager.loadAllProducts(getApplicationContext());
        this.mContentLoader.updateCachedItems();
        ImageLoader.init(this);
        registerActivityLifecycleCallbacks(this);
        this.handler = new Handler();
        JourneyMessagesManager.createInstance(this);
        InternetConnectivityReceiver.createInstance(this);
        JourneyMessagesManager.getInstance().setLastTimeAppUsed(Preferences.getInstance().getLong(PreferencesItem.LAST_TIME_APP_USED));
        initTypefaces();
        ImageCacheContainer.createInstance(getResources());
        BroadcastReceiver localeChangedReceiver = new BroadcastReceiver() { // from class: com.pg.oralb.oralbapp.application.OralBApplication.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.action.LOCALE_CHANGED")) {
                    DebugLog.i("LOCALE CHANGED BROADCAST RECEIVED!");
                    FeatureManager.onLocaleChanged();
                }
            }
        };
        IntentFilter intentFiler = new IntentFilter();
        intentFiler.addAction("android.intent.action.LOCALE_CHANGED");
        registerReceiver(localeChangedReceiver, intentFiler);
        long lastNotificationDate = AmazonFeatures.getStatus().getLastNotificationDate();
        AmazonFeatures.getPullNotificationsUseCase().pullNotifications(lastNotificationDate, (Callback<Void, Throwable>) null);
        AmazonFeatures.getReplenishUseCase().replenishIfNecessary(null);
        KiipFeatures.initKiip(this);
        if (FeatureManager.isErgoDirektSupported(this)) {
            ErgoFeatures.getErgoCampaignStatusUseCase().updateCampaignStatus(FeatureManager.getMarketLocale(this));
        }
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void createManagers() {
        this.mPreferences = Preferences.create();
        addManager(this.mPreferences);
        this.mPreferences.bind(this);
        DaoSessionProvider.setApplicationContextOnce(this);
        BrushManager.createInstance(this);
        this.mDatabaseManager = ApplicationDatabaseQueries.create();
        addManager(this.mDatabaseManager);
        this.mSettingsManager = SettingsManager.create();
        addManager(this.mSettingsManager);
        this.mBaseNavigator = BaseNavigator.create();
        addManager(this.mBaseNavigator);
        this.mAnalytics = GoogleAnalyticsManager.create();
        addManager(this.mAnalytics);
        addManager(OralBCalendarManager.create());
        this.mDialogManager = DialogManager.create();
        addManager(this.mDialogManager);
        this.mSoundRecognition = SoundRecognition.create();
        addManager(this.mSoundRecognition);
        this.mBluetoothManager = BluetoothManager.create();
        addManager(this.mBluetoothManager);
        this.mStateMachine = StateMachine.create();
        addManager(this.mStateMachine);
        this.mDeviceAlignmentManager = DeviceAlignmentManager.create();
        addManager(this.mDeviceAlignmentManager);
        this.mContentLoader = new ContentLoader(this);
        addManager(FeedbackManager.create());
        this.mChallengeScoresProvider = ChallengeScoresProvider.getInstance();
        addManager(this.mChallengeScoresProvider);
        CameraManager.createInstance(this, MPSUtils.VIDEO_MIN, 640);
        this.mToothbrushProvider = ToothbrushInfoProvider.create();
        addManager(this.mToothbrushProvider);
        addManager(CurrentLocationManager.create());
        addManager(ProfessionalProductManager.create());
        addManager(AchievementManager.create());
    }

    private void initManagers() {
        for (BaseManager manager : this.mManagers) {
            manager.bind(this);
        }
        for (BaseManager manager2 : this.mManagers) {
            manager2.init();
        }
    }

    private void initDeviceClass() {
        getSettingsManager().setDeviceClass(DeviceClass.getDeviceClass());
    }

    private void initTypefaces() {
        TYPEFACES.put("Gotham-Black.otf", Typeface.createFromAsset(getAssets(), "fonts/Gotham-Black.otf"));
        TYPEFACES.put("Gotham-Bold.otf", Typeface.createFromAsset(getAssets(), "fonts/Gotham-Bold.otf"));
        TYPEFACES.put("Gotham-Book.otf", Typeface.createFromAsset(getAssets(), "fonts/Gotham-Book.otf"));
        TYPEFACES.put("Gotham-BookItalic.otf", Typeface.createFromAsset(getAssets(), "fonts/Gotham-BookItalic.otf"));
        TYPEFACES.put("Gotham-Light.otf", Typeface.createFromAsset(getAssets(), "fonts/Gotham-Light.otf"));
        TYPEFACES.put("Gotham-Medium.otf", Typeface.createFromAsset(getAssets(), "fonts/Gotham-Medium.otf"));
        TYPEFACES.put("Neutra2Display-Medium.otf", Typeface.createFromAsset(getAssets(), "fonts/Neutra2Display-Medium.otf"));
        TYPEFACES.put("NeutraCond-Bold.otf", Typeface.createFromAsset(getAssets(), "fonts/NeutraCond-Bold.otf"));
        TYPEFACES.put("NeutraCond-LightAlt.otf", Typeface.createFromAsset(getAssets(), "fonts/NeutraCond-LightAlt.otf"));
        TYPEFACES.put("NeutraCond-MediumAlt.otf", Typeface.createFromAsset(getAssets(), "fonts/NeutraCond-MediumAlt.otf"));
        TYPEFACES.put("NeutraText-Bold.otf", Typeface.createFromAsset(getAssets(), "fonts/NeutraText-Bold.otf"));
        TYPEFACES.put("GlinyHand-Dense.otf", Typeface.createFromAsset(getAssets(), "fonts/GlinyHand-Dense.otf"));
    }

    public void addManager(BaseManager baseManager) {
        this.mManagers.add(baseManager);
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public SettingsManager getSettingsManager() {
        return this.mSettingsManager;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public ApplicationDatabaseQueries getDatabaseQueries() {
        return this.mDatabaseManager;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public GoogleAnalyticsManager getGoogleAnalytics() {
        return this.mAnalytics;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public BluetoothManager getBluetoothManager() {
        return this.mBluetoothManager;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public ContentLoader getContentLoader() {
        return this.mContentLoader;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public ChallengeScoresProvider getChallengeScoresProvider() {
        this.mChallengeScoresProvider.updateSessions();
        return this.mChallengeScoresProvider;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public UICallback getUserInterface() {
        return this.mStateMachine;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public ToothBrushNotificationsManager getToothbrushNotificationManager() {
        return this.mToothbrushProvider;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public DeviceAlignmentManager getDeviceAlignmentManager() {
        return this.mDeviceAlignmentManager;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public BaseNavigator getNavigator() {
        return this.mBaseNavigator;
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public Locale getLocale() {
        return getSettingsManager().getLocale();
    }

    @Override // com.pg.oralb.oralbapp.api.ModulesManager
    public AccessLevel getAccessLevel() {
        return getSettingsManager().getAccessLevel();
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return STATIC_CONTEXT;
    }

    public SoundRecognition getSoundRecognition() {
        return this.mSoundRecognition;
    }

    public ApplicationDatabaseQueries getDatabaseManager() {
        return this.mDatabaseManager;
    }

    public StateMachine getStateMachine() {
        return this.mStateMachine;
    }

    public static boolean isInBackground() {
        return !isInForeground;
    }

    public static int getCurrentAppExperience() {
        return currentAppExperience;
    }

    public static void setCurrentAppExperience(int currentAppExperience2) {
        currentAppExperience = currentAppExperience2;
    }
}
