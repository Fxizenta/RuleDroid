package canvasm.myo2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import canvasm.myo2.app_globals.GATracker;
import canvasm.myo2.app_globals.storage.DataStorage;
import canvasm.myo2.app_globals.storage.DataStorageEntries;
import canvasm.myo2.app_utils.SysUtils;
import canvasm.myo2.deeplink.DeepLinkMain;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class SplashActivity extends Activity {
    private static final String SCREENNAME = "welcome";

    @Inject
    DeepLinkMain deepLinkMain;
    private Handler mHandler = new Handler();

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(telefonica.de.o2business.R.layout.o2theme_splash);
        ((TextView) findViewById(telefonica.de.o2business.R.id.textView_Version)).setText(SysUtils.GetLongAppVersion(this));
        handleIntentForAppStart(2000L);
    }

    private void handleIntentForAppStart(long j) {
        this.mHandler.postDelayed(new Runnable() { // from class: canvasm.myo2.-$$Lambda$SplashActivity$Nr9iaXcPfZyXYhWhmbFF__pUw-s
            @Override // java.lang.Runnable
            public final void run() {
                SplashActivity.this.lambda$handleIntentForAppStart$0$SplashActivity();
            }
        }, j);
    }

    public /* synthetic */ void lambda$handleIntentForAppStart$0$SplashActivity() {
        GATracker.getInstance(getApplicationContext()).trackScreenView(SCREENNAME);
        handleUpdatedFromSystem();
        this.deepLinkMain.onNewIntent(this, getIntent());
        finish();
    }

    private void handleUpdatedFromSystem() {
        DataStorage dataStorage;
        if (!((getApplicationInfo().flags & 128) != 0) || (dataStorage = DataStorage.getInstance(this)) == null || dataStorage.hasPersistentKey(DataStorageEntries.PREINSTALL_FCM_DONE)) {
            return;
        }
        GATracker.getInstance(getApplicationContext()).trackEvent(SCREENNAME, "sw_update_preinstall");
        dataStorage.putPersistentInteger(DataStorageEntries.PREINSTALL_FCM_DONE, 1);
    }
}
