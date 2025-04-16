package com.sophos.sophtoken;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.client.android.Intents;
import com.sophos.sophtoken.AccountDb;
import com.sophos.sophtoken.OtpProvider;
import com.sophos.sophtoken.TotpCountdownTask;
import com.sophos.sophtoken.dataimport.ImportController;
import com.sophos.sophtoken.howitworks.IntroEnterPasswordActivity;
import com.sophos.sophtoken.testability.DependencyInjector;
import com.sophos.sophtoken.testability.TestableActivity;
import eu.livotov.zxscan.ZXScanHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/* loaded from: classes.dex */
public class SophTokenActivity extends TestableActivity {
    static final String ACTION_SCAN_BARCODE = SophTokenActivity.class.getName() + ".ScanBarcode";
    private static final String ALGORITHM_PARAM = "algorithm";
    private static final int CAMERA_PERMISSION_REQUEST = 0;
    public static final int CHECK_KEY_VALUE_ID = 0;
    static final int COPY_TO_CLIPBOARD_ID = 3;
    private static final String COUNTER_PARAM = "counter";
    static final int DIALOG_ID_SAVE_KEY = 13;
    static final int DIALOG_ID_UNINSTALL_OLD_APP = 12;
    private static final String DIGITS_PARAM = "digits";
    private static final String HOTP = "hotp";
    private static final long HOTP_DISPLAY_TIMEOUT = 120000;
    private static final long HOTP_MIN_TIME_INTERVAL_BETWEEN_CODES = 5000;
    private static final String KEY_OLD_APP_UNINSTALL_INTENT = "oldAppUninstallIntent";
    private static final String KEY_SAVE_KEY_DIALOG_PARAMS = "saveKeyDialogParams";
    private static final String LOCAL_TAG = "SophTokenActivity";
    private static final String OTP_SCHEME = "otpauth";
    private static final String PERIOD_PARAM = "period";
    private static final float PIN_TEXT_SCALEX_NORMAL = 1.0f;
    private static final float PIN_TEXT_SCALEX_UNDERSCORE = 0.87f;
    private static final int RC_BARCODE_CAPTURE = 9001;
    public static final int REMOVE_ID = 2;
    public static final int RENAME_ID = 1;
    static final int SCAN_REQUEST = 31337;
    private static final String SECRET_PARAM = "secret";
    private static final String TOTP = "totp";
    private static final long TOTP_COUNTDOWN_REFRESH_PERIOD = 100;
    private static final long VIBRATE_DURATION = 200;
    private AccountDb mAccountDb;
    private View mContentAccountsPresent;
    private View mContentNoAccounts;
    private boolean mDataImportInProgress;
    private TextView mEnterPinPrompt;
    private Intent mOldAppUninstallIntent;
    private OtpSource mOtpProvider;
    private SaveKeyDialogParams mSaveKeyDialogParams;
    private boolean mSaveKeyIntentConfirmationInProgress;
    private TotpClock mTotpClock;
    private TotpCountdownTask mTotpCountdownTask;
    private TotpCounter mTotpCounter;
    private PinListAdapter mUserAdapter;
    private ListView mUserList;
    private PinInfo[] mUsers = new PinInfo[0];
    private boolean mUseVisionApi = false;
    private boolean mImprovedScanning = true;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAccountDb = DependencyInjector.getAccountDb();
        this.mOtpProvider = DependencyInjector.getOtpProvider();
        setTitle(R.string.app_name);
        this.mTotpCounter = this.mOtpProvider.getTotpCounter();
        this.mTotpClock = this.mOtpProvider.getTotpClock();
        getWindow().setFlags(8192, 8192);
        setContentView(R.layout.main);
        Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
        if (lastNonConfigurationInstance != null) {
            PinInfo[] pinInfoArr = (PinInfo[]) lastNonConfigurationInstance;
            this.mUsers = pinInfoArr;
            for (PinInfo pinInfo : pinInfoArr) {
                if (pinInfo.isHotp) {
                    pinInfo.hotpCodeGenerationAllowed = true;
                }
            }
        }
        if (bundle != null) {
            this.mOldAppUninstallIntent = (Intent) bundle.getParcelable(KEY_OLD_APP_UNINSTALL_INTENT);
            this.mSaveKeyDialogParams = (SaveKeyDialogParams) bundle.getSerializable(KEY_SAVE_KEY_DIALOG_PARAMS);
        }
        this.mUserList = (ListView) findViewById(R.id.user_list);
        this.mContentNoAccounts = findViewById(R.id.content_no_accounts);
        this.mContentAccountsPresent = findViewById(R.id.content_accounts_present);
        this.mContentNoAccounts.setVisibility(this.mUsers.length > 0 ? 8 : 0);
        this.mContentAccountsPresent.setVisibility(this.mUsers.length > 0 ? 0 : 8);
        ((TextView) findViewById(R.id.details)).setText(Html.fromHtml(getString(R.string.welcome_page_details)));
        findViewById(R.id.scan_qr_button).setOnClickListener(new View.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SophTokenActivity.this.addAccount(false);
            }
        });
        findViewById(R.id.manual_add_button).setOnClickListener(new View.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SophTokenActivity.this.addAccount(true);
            }
        });
        findViewById(R.id.how_it_works_button).setOnClickListener(new View.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SophTokenActivity.this.displayHowItWorksInstructions();
            }
        });
        this.mEnterPinPrompt = (TextView) findViewById(R.id.enter_pin_prompt);
        this.mUserAdapter = new PinListAdapter(this, R.layout.user_row, this.mUsers);
        this.mUserList.setVisibility(8);
        this.mUserList.setAdapter((ListAdapter) this.mUserAdapter);
        this.mUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                NextOtpButtonListener nextOtpButtonListener = (NextOtpButtonListener) view.getTag();
                View findViewById = view.findViewById(R.id.next_otp);
                if (nextOtpButtonListener != null && findViewById.isEnabled()) {
                    nextOtpButtonListener.onClick(view);
                }
                SophTokenActivity.this.mUserList.sendAccessibilityEvent(4);
            }
        });
        if (bundle == null) {
            DependencyInjector.getOptionalFeatures().onSophTokenActivityCreated(this);
            importDataFromOldAppIfNecessary();
            handleIntent(getIntent());
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            Log.i(getString(R.string.app_name), "Permission CAMERA is not yet granted");
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 0);
        } else {
            Log.i(getString(R.string.app_name), "Permission CAMERA granted");
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 0) {
            return;
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            Log.i(getString(R.string.app_name), "User granted the CAMERA permission");
        } else {
            Log.i(getString(R.string.app_name), "User did not grant the CAMERA permission");
        }
    }

    private void handleIntent(Intent intent) {
        String action;
        if (intent == null || (action = intent.getAction()) == null) {
            return;
        }
        if (ACTION_SCAN_BARCODE.equals(action)) {
            scanBarcode();
        } else if (intent.getData() != null) {
            interpretScanResult(intent.getData(), true);
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_OLD_APP_UNINSTALL_INTENT, this.mOldAppUninstallIntent);
        bundle.putSerializable(KEY_SAVE_KEY_DIALOG_PARAMS, this.mSaveKeyDialogParams);
    }

    @Override // android.app.Activity
    public Object onRetainNonConfigurationInstance() {
        return this.mUsers;
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        Log.i(getString(R.string.app_name), "SophTokenActivity: onNewIntent");
        handleIntent(intent);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        getDefaultPrefs();
        updateCodesAndStartTotpCountdownTask();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.i(getString(R.string.app_name), "SophTokenActivity: onResume");
        importDataFromOldAppIfNecessary();
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == 0) {
            this.mUseVisionApi = true;
            Log.v(getString(R.string.app_name), "SophTokenActivity: Vision API present");
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        stopTotpCountdownTask();
        super.onStop();
    }

    private void getDefaultPrefs() {
        this.mImprovedScanning = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("improved_scanning", true);
    }

    private void updateCodesAndStartTotpCountdownTask() {
        stopTotpCountdownTask();
        HashSet<Integer> currentTimesteps = getCurrentTimesteps();
        Log.v(getString(R.string.app_name), "SophTokenActivity: getCurrentTimesteps() returned " + currentTimesteps.size() + " elements");
        TotpCountdownTask totpCountdownTask = new TotpCountdownTask(this.mTotpCounter, this.mTotpClock, TOTP_COUNTDOWN_REFRESH_PERIOD, currentTimesteps);
        this.mTotpCountdownTask = totpCountdownTask;
        totpCountdownTask.setListener(new TotpCountdownTask.Listener() { // from class: com.sophos.sophtoken.SophTokenActivity.5
            @Override // com.sophos.sophtoken.TotpCountdownTask.Listener
            public void onTotpCountdown() {
                if (SophTokenActivity.this.isFinishing()) {
                    return;
                }
                SophTokenActivity.this.setTotpCountdownPhaseFromTimeTillNextValue();
            }

            @Override // com.sophos.sophtoken.TotpCountdownTask.Listener
            public void onTotpCounterValueChanged(Integer num) {
                if (SophTokenActivity.this.isFinishing()) {
                    return;
                }
                SophTokenActivity.this.refreshVerificationCodes(num);
            }
        });
        this.mTotpCountdownTask.startAndNotifyListener();
    }

    private void stopTotpCountdownTask() {
        TotpCountdownTask totpCountdownTask = this.mTotpCountdownTask;
        if (totpCountdownTask != null) {
            totpCountdownTask.stop();
            this.mTotpCountdownTask = null;
        }
    }

    private HashSet<Integer> getCurrentTimesteps() {
        HashSet<Integer> hashSet = new HashSet<>();
        this.mAccountDb.getTimesteps(hashSet);
        return hashSet;
    }

    protected void refreshUserList() {
        refreshUserList(false);
    }

    private void setTotpCountdownPhase(Integer num) {
        for (int i = 0; i < this.mUsers.length; i++) {
            if (num.intValue() <= 0) {
                long timeTillNextCounterValue = this.mTotpCountdownTask.getTimeTillNextCounterValue(this.mTotpClock.currentTimeMillis(), this.mUsers[i].timestep);
                PinInfo pinInfo = this.mUsers[i];
                double d = timeTillNextCounterValue;
                double secondsToMillis = Utilities.secondsToMillis(r3[i].timestep.intValue());
                Double.isNaN(d);
                Double.isNaN(secondsToMillis);
                pinInfo.TotpCountdownPhase = d / secondsToMillis;
            } else if (this.mUsers[i].timestep == num) {
                this.mUsers[i].TotpCountdownPhase = 1.0d;
            }
        }
        updateCountdownIndicators();
    }

    private void setTotpCountdownPhase() {
        setTotpCountdownPhase(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTotpCountdownPhaseFromTimeTillNextValue() {
        setTotpCountdownPhase();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshVerificationCodes(Integer num) {
        refreshUserList();
        setTotpCountdownPhase(num);
    }

    private void updateCountdownIndicators() {
        int childCount = this.mUserList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CountdownIndicator countdownIndicator = (CountdownIndicator) this.mUserList.getChildAt(i).findViewById(R.id.countdown_icon);
            if (countdownIndicator != null) {
                PinInfo[] pinInfoArr = this.mUsers;
                if (pinInfoArr.length > i) {
                    countdownIndicator.setPhase(pinInfoArr[i].TotpCountdownPhase);
                }
            }
        }
    }

    public void refreshUserList(boolean z) {
        ArrayList arrayList = new ArrayList();
        this.mAccountDb.getNames(arrayList);
        int size = arrayList.size();
        if (size > 0) {
            boolean z2 = z || this.mUsers.length != size;
            if (z2) {
                this.mUsers = new PinInfo[size];
                HashSet<Integer> currentTimesteps = getCurrentTimesteps();
                Log.v(getString(R.string.app_name), "SophTokenActivity: in refreshUserList getCurrentTimesteps() returned " + currentTimesteps.size() + " elements");
                TotpCountdownTask.initTimesteps(currentTimesteps);
            }
            for (int i = 0; i < size; i++) {
                try {
                    computeAndDisplayPin((String) arrayList.get(i), i, false);
                } catch (OtpSourceException unused) {
                }
            }
            if (z2) {
                PinListAdapter pinListAdapter = new PinListAdapter(this, R.layout.user_row, this.mUsers);
                this.mUserAdapter = pinListAdapter;
                this.mUserList.setAdapter((ListAdapter) pinListAdapter);
            }
            this.mUserAdapter.notifyDataSetChanged();
            if (this.mUserList.getVisibility() != 0) {
                this.mUserList.setVisibility(0);
                registerForContextMenu(this.mUserList);
            }
        } else {
            this.mUsers = new PinInfo[0];
            this.mUserList.setVisibility(8);
        }
        this.mContentNoAccounts.setVisibility(this.mUsers.length > 0 ? 8 : 0);
        this.mContentAccountsPresent.setVisibility(this.mUsers.length > 0 ? 0 : 8);
    }

    public void computeAndDisplayPin(String str, int i, boolean z) throws OtpSourceException {
        PinInfo pinInfo;
        PinInfo[] pinInfoArr = this.mUsers;
        if (pinInfoArr[i] != null) {
            pinInfo = pinInfoArr[i];
        } else {
            pinInfo = new PinInfo();
            pinInfo.pin = getString(R.string.empty_pin);
            pinInfo.hotpCodeGenerationAllowed = true;
        }
        pinInfo.isHotp = this.mAccountDb.getType(str) == AccountDb.OtpType.HOTP;
        pinInfo.user = str;
        pinInfo.timestep = this.mAccountDb.getTimestep(str);
        if (!pinInfo.isHotp || z) {
            pinInfo.pin = this.mOtpProvider.getNextCode(str);
            pinInfo.hotpCodeGenerationAllowed = true;
        }
        this.mUsers[i] = pinInfo;
    }

    private void parseSecretWithTimestep(Uri uri, boolean z) {
        AccountDb.OtpType otpType;
        Integer valueOf;
        Integer defaultDigits;
        OtpProvider.ALGORITHM_TYPE fromString;
        String lowerCase = uri.getScheme().toLowerCase();
        String path = uri.getPath();
        String authority = uri.getAuthority();
        int i = 30;
        Integer defaultDigits2 = OtpProvider.getDefaultDigits();
        Integer defaultAlgorithm = OtpProvider.getDefaultAlgorithm();
        if (!OTP_SCHEME.equals(lowerCase)) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid or missing scheme in uri");
            showDialog(3);
            return;
        }
        if (TOTP.equals(authority)) {
            otpType = AccountDb.OtpType.TOTP;
            valueOf = AccountDb.DEFAULT_HOTP_COUNTER;
            String queryParameter = uri.getQueryParameter(PERIOD_PARAM);
            if (queryParameter != null) {
                try {
                    i = Integer.valueOf(Integer.parseInt(queryParameter));
                } catch (NumberFormatException unused) {
                    Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid timestep in uri");
                }
                Log.v(getString(R.string.app_name), "SophTokenActivity: Found a timestep of " + i + " seconds");
            }
        } else if (HOTP.equals(authority)) {
            otpType = AccountDb.OtpType.HOTP;
            String queryParameter2 = uri.getQueryParameter(COUNTER_PARAM);
            if (queryParameter2 != null) {
                try {
                    valueOf = Integer.valueOf(Integer.parseInt(queryParameter2));
                } catch (NumberFormatException unused2) {
                    Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid counter in uri");
                    showDialog(3);
                    return;
                }
            } else {
                valueOf = AccountDb.DEFAULT_HOTP_COUNTER;
            }
        } else {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid or missing authority in uri");
            showDialog(3);
            return;
        }
        AccountDb.OtpType otpType2 = otpType;
        Integer num = i;
        Integer num2 = valueOf;
        String queryParameter3 = uri.getQueryParameter(DIGITS_PARAM);
        if (queryParameter3 != null) {
            try {
                defaultDigits = Integer.valueOf(Integer.parseInt(queryParameter3));
            } catch (NumberFormatException unused3) {
                defaultDigits = OtpProvider.getDefaultDigits();
                Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid digits in uri, fallback to " + defaultDigits);
            }
            if (defaultDigits.intValue() <= 0 || defaultDigits.intValue() >= 9) {
                defaultDigits = OtpProvider.getDefaultDigits();
                Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid digits in uri, fallback to " + defaultDigits);
            }
            Log.v(getString(R.string.app_name), "SophTokenActivity: Digits specified as " + defaultDigits);
            defaultDigits2 = defaultDigits;
        }
        String queryParameter4 = uri.getQueryParameter(ALGORITHM_PARAM);
        if (queryParameter4 != null && (fromString = OtpProvider.ALGORITHM_TYPE.fromString(queryParameter4)) != null) {
            Log.v(getString(R.string.app_name), "SophTokenActivity: Algorithm specified as " + fromString.toString());
            defaultAlgorithm = Integer.valueOf(fromString.ordinal());
        }
        String validateAndGetUserInPath = validateAndGetUserInPath(path);
        if (validateAndGetUserInPath == null) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Missing user id in uri");
            showDialog(3);
            return;
        }
        String queryParameter5 = uri.getQueryParameter(SECRET_PARAM);
        if (queryParameter5 == null || queryParameter5.length() == 0) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Secret key not found in URI");
            showDialog(7);
            return;
        }
        if (AccountDb.getSigningOracle(queryParameter5, defaultAlgorithm) == null) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid secret key");
            showDialog(7);
            return;
        }
        if (queryParameter5.equals(this.mAccountDb.getSecret(validateAndGetUserInPath)) && num2.equals(this.mAccountDb.getCounter(validateAndGetUserInPath)) && num.equals(this.mAccountDb.getTimestep(validateAndGetUserInPath)) && otpType2 == this.mAccountDb.getType(validateAndGetUserInPath) && defaultDigits2.equals(this.mAccountDb.getDigits(validateAndGetUserInPath)) && defaultAlgorithm.equals(this.mAccountDb.getAlgorithm(validateAndGetUserInPath))) {
            return;
        }
        if (z) {
            this.mSaveKeyDialogParams = new SaveKeyDialogParams(validateAndGetUserInPath, queryParameter5, otpType2, num, num2, defaultDigits2, defaultAlgorithm);
            showDialog(13);
        } else {
            saveSecretAndRefreshUserList(validateAndGetUserInPath, queryParameter5, null, otpType2, num, num2, defaultDigits2, defaultAlgorithm);
        }
    }

    private void parseSecret(Uri uri, boolean z) {
        AccountDb.OtpType otpType;
        Integer valueOf;
        String lowerCase = uri.getScheme().toLowerCase();
        String path = uri.getPath();
        String authority = uri.getAuthority();
        Integer defaultDigits = OtpProvider.getDefaultDigits();
        Integer defaultAlgorithm = OtpProvider.getDefaultAlgorithm();
        if (!OTP_SCHEME.equals(lowerCase)) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid or missing scheme in uri");
            showDialog(3);
            return;
        }
        if (TOTP.equals(authority)) {
            otpType = AccountDb.OtpType.TOTP;
            valueOf = AccountDb.DEFAULT_HOTP_COUNTER;
        } else if (HOTP.equals(authority)) {
            otpType = AccountDb.OtpType.HOTP;
            String queryParameter = uri.getQueryParameter(COUNTER_PARAM);
            if (queryParameter != null) {
                try {
                    valueOf = Integer.valueOf(Integer.parseInt(queryParameter));
                } catch (NumberFormatException unused) {
                    Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid counter in uri");
                    showDialog(3);
                    return;
                }
            } else {
                valueOf = AccountDb.DEFAULT_HOTP_COUNTER;
            }
        } else {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid or missing authority in uri");
            showDialog(3);
            return;
        }
        AccountDb.OtpType otpType2 = otpType;
        Integer num = valueOf;
        String validateAndGetUserInPath = validateAndGetUserInPath(path);
        if (validateAndGetUserInPath == null) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Missing user id in uri");
            showDialog(3);
            return;
        }
        String queryParameter2 = uri.getQueryParameter(SECRET_PARAM);
        if (queryParameter2 == null || queryParameter2.length() == 0) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Secret key not found in URI");
            showDialog(7);
            return;
        }
        if (AccountDb.getSigningOracle(queryParameter2, defaultAlgorithm) == null) {
            Log.e(getString(R.string.app_name), "SophTokenActivity: Invalid secret key");
            showDialog(7);
            return;
        }
        if (queryParameter2.equals(this.mAccountDb.getSecret(validateAndGetUserInPath)) && num.equals(this.mAccountDb.getCounter(validateAndGetUserInPath)) && otpType2 == this.mAccountDb.getType(validateAndGetUserInPath) && defaultDigits.equals(this.mAccountDb.getDigits(validateAndGetUserInPath)) && defaultAlgorithm.equals(this.mAccountDb.getAlgorithm(validateAndGetUserInPath))) {
            return;
        }
        if (z) {
            this.mSaveKeyDialogParams = new SaveKeyDialogParams(validateAndGetUserInPath, queryParameter2, otpType2, 0, num, defaultDigits, defaultAlgorithm);
            showDialog(13);
        } else {
            saveSecretAndRefreshUserList(validateAndGetUserInPath, queryParameter2, null, otpType2, 0, num, defaultDigits, defaultAlgorithm);
        }
    }

    private static String validateAndGetUserInPath(String str) {
        if (str == null || !str.startsWith("/")) {
            return null;
        }
        String trim = str.substring(1).trim();
        if (trim.length() == 0) {
            return null;
        }
        return trim;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSecretAndRefreshUserList(String str, String str2, String str3, AccountDb.OtpType otpType, Integer num, Integer num2, Integer num3, Integer num4) {
        if (saveSecret(this, str, str2, str3, otpType, num, num2, num3, num4)) {
            refreshUserList(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean saveSecret(Context context, String str, String str2, String str3, AccountDb.OtpType otpType, Integer num, Integer num2, Integer num3, Integer num4) {
        String str4 = str3 == null ? str : str3;
        if (str2 != null) {
            DependencyInjector.getAccountDb().update(str, str2, str4, otpType, num, num2, num3, num4);
            DependencyInjector.getOptionalFeatures().onSophTokenActivityAccountSaved(context, str);
            Toast.makeText(context, R.string.secret_saved, 1).show();
            return true;
        }
        Log.e(LOCAL_TAG, "Trying to save an empty secret key");
        Toast.makeText(context, R.string.error_empty_secret, 1).show();
        return false;
    }

    private String idToEmail(long j) {
        return this.mUsers[(int) j].user;
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        String idToEmail = idToEmail(((AdapterView.AdapterContextMenuInfo) contextMenuInfo).id);
        AccountDb.OtpType type = this.mAccountDb.getType(idToEmail);
        contextMenu.setHeaderTitle(idToEmail);
        contextMenu.add(0, 3, 0, R.string.copy_to_clipboard);
        if (type == AccountDb.OtpType.HOTP) {
            contextMenu.add(0, 0, 0, R.string.check_code_menu_item);
        }
        contextMenu.add(0, 1, 0, R.string.rename);
        contextMenu.add(0, 2, 0, R.string.context_menu_remove_account);
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final String idToEmail = idToEmail(adapterContextMenuInfo.id);
        int itemId = menuItem.getItemId();
        if (itemId == 0) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setClass(this, CheckCodeActivity.class);
            intent.putExtra("user", idToEmail);
            startActivity(intent);
            return true;
        }
        if (itemId == 1) {
            View inflate = getLayoutInflater().inflate(R.layout.rename, (ViewGroup) findViewById(R.id.rename_root));
            EditText editText = (EditText) inflate.findViewById(R.id.rename_edittext);
            editText.setText(idToEmail);
            new AlertDialog.Builder(this).setTitle(String.format(getString(R.string.rename_message), idToEmail)).setView(inflate).setPositiveButton(R.string.submit, getRenameClickListener(this, idToEmail, editText)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
            return true;
        }
        if (itemId != 2) {
            if (itemId != 3) {
                return super.onContextItemSelected(menuItem);
            }
            ((ClipboardManager) getSystemService("clipboard")).setText(this.mUsers[(int) adapterContextMenuInfo.id].pin);
            return true;
        }
        View inflate2 = getLayoutInflater().inflate(R.layout.remove_account_prompt, (ViewGroup) null, false);
        WebView webView = (WebView) inflate2.findViewById(R.id.web_view);
        webView.setBackgroundColor(0);
        double applyDimension = TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics());
        Double.isNaN(applyDimension);
        WebSettings settings = webView.getSettings();
        double textSize = this.mEnterPinPrompt.getTextSize();
        Double.isNaN(textSize);
        settings.setDefaultFontSize((int) (textSize / (applyDimension / 10.0d)));
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style=\"background-color: transparent;\" text=\"white\">");
        sb.append(getString(this.mAccountDb.isGoogleAccount(idToEmail) ? R.string.remove_google_account_dialog_message : R.string.remove_account_dialog_message));
        sb.append("</body></html>");
        Utilities.setWebViewHtml(webView, sb.toString());
        new AlertDialog.Builder(this).setTitle(getString(R.string.remove_account_dialog_title, new Object[]{idToEmail})).setView(inflate2).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(R.string.remove_account_dialog_button_remove, new DialogInterface.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SophTokenActivity.this.mAccountDb.delete(idToEmail);
                SophTokenActivity.this.refreshUserList(true);
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
        return true;
    }

    private DialogInterface.OnClickListener getRenameClickListener(final Context context, final String str, final EditText editText) {
        return new DialogInterface.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                String obj = editText.getText().toString();
                if (obj != str) {
                    if (SophTokenActivity.this.mAccountDb.nameExists(obj)) {
                        Toast.makeText(context, R.string.error_exists, 1).show();
                    } else {
                        SophTokenActivity sophTokenActivity = SophTokenActivity.this;
                        sophTokenActivity.saveSecretAndRefreshUserList(obj, sophTokenActivity.mAccountDb.getSecret(str), str, SophTokenActivity.this.mAccountDb.getType(str), SophTokenActivity.this.mAccountDb.getTimestep(str), SophTokenActivity.this.mAccountDb.getCounter(str), SophTokenActivity.this.mAccountDb.getDigits(str), SophTokenActivity.this.mAccountDb.getAlgorithm(str));
                    }
                }
            }
        };
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.how_it_works /* 2131099694 */:
                displayHowItWorksInstructions();
                return true;
            case R.id.manual_add /* 2131099707 */:
                addAccount(true);
                return true;
            case R.id.scan_qr /* 2131099728 */:
                addAccount(false);
                return true;
            case R.id.scan_settings /* 2131099730 */:
                showScanSettings();
                return true;
            case R.id.settings /* 2131099731 */:
                showSettings();
                return true;
            default:
                return super.onMenuItemSelected(i, menuItem);
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri parse;
        Log.i(getString(R.string.app_name), "SophTokenActivity: onActivityResult");
        if (i == SCAN_REQUEST && i2 == -1) {
            String stringExtra = intent != null ? intent.getStringExtra(Intents.Scan.RESULT) : null;
            parse = stringExtra != null ? Uri.parse(stringExtra) : null;
            Log.v(getString(R.string.app_name), "SophTokenActivity: scanned data =  " + parse);
            interpretScanResult(parse, false);
            return;
        }
        if (i == RC_BARCODE_CAPTURE && i2 == 0 && intent != null) {
            Barcode barcode = (Barcode) intent.getParcelableExtra(VisionApiScanActivity.BarcodeObject);
            parse = barcode.displayValue != null ? Uri.parse(barcode.displayValue) : null;
            Log.v(getString(R.string.app_name), "SophTokenActivity: scanned data =  " + parse);
            interpretScanResult(parse, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayHowItWorksInstructions() {
        startActivity(new Intent(this, (Class<?>) IntroEnterPasswordActivity.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addAccount(boolean z) {
        if (!z) {
            startActivity(getLaunchIntentActionScanBarcode(this));
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClass(this, EnterKeyActivity.class);
        startActivity(intent);
    }

    private void scanBarcode() {
        if (this.mImprovedScanning && this.mUseVisionApi) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setClass(this, VisionApiScanActivity.class);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        } else {
            Log.v(getString(R.string.app_name), "SophTokenActivity: Vision API not present, defaulting to scan lib");
            ZXScanHelper.scan(this, SCAN_REQUEST);
        }
    }

    public static Intent getLaunchIntentActionScanBarcode(Context context) {
        return new Intent(ACTION_SCAN_BARCODE).setComponent(new ComponentName(context, (Class<?>) SophTokenActivity.class));
    }

    private void showScanSettings() {
        Intent intent = new Intent();
        intent.setClass(this, SettingsScanActivity.class);
        startActivity(intent);
    }

    private void showSettings() {
        Intent intent = new Intent();
        intent.setClass(this, SettingsAboutActivity.class);
        startActivity(intent);
    }

    private void interpretScanResult(Uri uri, boolean z) {
        if (DependencyInjector.getOptionalFeatures().interpretScanResult(this, uri)) {
            return;
        }
        if (z) {
            if (this.mSaveKeyIntentConfirmationInProgress) {
                Log.w(LOCAL_TAG, "Ignoring save key Intent: previous Intent not yet confirmed by user");
                return;
            }
            this.mSaveKeyIntentConfirmationInProgress = true;
        }
        if (uri == null) {
            showDialog(3);
        } else if (OTP_SCHEME.equals(uri.getScheme()) && uri.getAuthority() != null) {
            parseSecretWithTimestep(uri, z);
        } else {
            showDialog(3);
        }
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(final int i) {
        if (i == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.install_dialog_title);
            builder.setMessage(R.string.install_dialog_message);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(R.string.install_button, new DialogInterface.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    try {
                        SophTokenActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Utilities.ZXING_MARKET)));
                    } catch (ActivityNotFoundException unused) {
                        SophTokenActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Utilities.ZXING_DIRECT)));
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
        if (i == 3) {
            Dialog createOkAlertDialog = createOkAlertDialog(R.string.error_title, R.string.error_qr, android.R.drawable.ic_dialog_alert);
            markDialogAsResultOfSaveKeyIntent(createOkAlertDialog);
            return createOkAlertDialog;
        }
        if (i == 7) {
            Dialog createOkAlertDialog2 = createOkAlertDialog(R.string.error_title, R.string.error_uri, android.R.drawable.ic_dialog_alert);
            markDialogAsResultOfSaveKeyIntent(createOkAlertDialog2);
            return createOkAlertDialog2;
        }
        if (i == 12) {
            return new AlertDialog.Builder(this).setTitle(R.string.dataimport_import_succeeded_uninstall_dialog_title).setMessage(DependencyInjector.getOptionalFeatures().appendDataImportLearnMoreLink(this, getString(R.string.dataimport_import_succeeded_uninstall_dialog_prompt))).setCancelable(true).setPositiveButton(R.string.button_uninstall_old_app, new DialogInterface.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.11
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    SophTokenActivity sophTokenActivity = SophTokenActivity.this;
                    sophTokenActivity.startActivity(sophTokenActivity.mOldAppUninstallIntent);
                }
            }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
        }
        if (i == 13) {
            final SaveKeyDialogParams saveKeyDialogParams = this.mSaveKeyDialogParams;
            AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.save_key_message).setMessage(saveKeyDialogParams.user).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.sophos.sophtoken.SophTokenActivity.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    SophTokenActivity.this.saveSecretAndRefreshUserList(saveKeyDialogParams.user, saveKeyDialogParams.secret, null, saveKeyDialogParams.type, saveKeyDialogParams.timestep, saveKeyDialogParams.counter, saveKeyDialogParams.digits, saveKeyDialogParams.algorithm);
                }
            }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
            create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sophos.sophtoken.SophTokenActivity.10
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    SophTokenActivity.this.removeDialog(i);
                    SophTokenActivity.this.onSaveKeyIntentConfirmationPromptDismissed();
                }
            });
            return create;
        }
        Dialog onSophTokenActivityCreateDialog = DependencyInjector.getOptionalFeatures().onSophTokenActivityCreateDialog(this, i);
        return onSophTokenActivityCreateDialog == null ? super.onCreateDialog(i) : onSophTokenActivityCreateDialog;
    }

    private void markDialogAsResultOfSaveKeyIntent(Dialog dialog) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sophos.sophtoken.SophTokenActivity.12
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                SophTokenActivity.this.onSaveKeyIntentConfirmationPromptDismissed();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSaveKeyIntentConfirmationPromptDismissed() {
        this.mSaveKeyIntentConfirmationInProgress = false;
    }

    private Dialog createOkAlertDialog(int i, int i2, int i3) {
        return new AlertDialog.Builder(this).setTitle(i).setMessage(i2).setIcon(i3).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PinInfo {
        private double TotpCountdownPhase;
        private boolean hotpCodeGenerationAllowed;
        private boolean isHotp;
        private String pin;
        private Integer timestep;
        private String user;

        private PinInfo() {
            this.isHotp = false;
        }
    }

    /* loaded from: classes.dex */
    private class NextOtpButtonListener implements View.OnClickListener {
        private final PinInfo mAccount;
        private final Handler mHandler;

        private NextOtpButtonListener(PinInfo pinInfo) {
            this.mHandler = new Handler();
            this.mAccount = pinInfo;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int findAccountPositionInList = findAccountPositionInList();
            if (findAccountPositionInList != -1) {
                try {
                    SophTokenActivity.this.computeAndDisplayPin(this.mAccount.user, findAccountPositionInList, true);
                    final String str = this.mAccount.pin;
                    this.mAccount.hotpCodeGenerationAllowed = false;
                    SophTokenActivity.this.mUserAdapter.notifyDataSetChanged();
                    this.mHandler.postDelayed(new Runnable() { // from class: com.sophos.sophtoken.SophTokenActivity.NextOtpButtonListener.1
                        @Override // java.lang.Runnable
                        public void run() {
                            NextOtpButtonListener.this.mAccount.hotpCodeGenerationAllowed = true;
                            SophTokenActivity.this.mUserAdapter.notifyDataSetChanged();
                        }
                    }, SophTokenActivity.HOTP_MIN_TIME_INTERVAL_BETWEEN_CODES);
                    this.mHandler.postDelayed(new Runnable() { // from class: com.sophos.sophtoken.SophTokenActivity.NextOtpButtonListener.2
                        @Override // java.lang.Runnable
                        public void run() {
                            if (str.equals(NextOtpButtonListener.this.mAccount.pin)) {
                                NextOtpButtonListener.this.mAccount.pin = SophTokenActivity.this.getString(R.string.empty_pin);
                                SophTokenActivity.this.mUserAdapter.notifyDataSetChanged();
                            }
                        }
                    }, SophTokenActivity.HOTP_DISPLAY_TIMEOUT);
                    return;
                } catch (OtpSourceException e) {
                    DependencyInjector.getOptionalFeatures().onSophTokenActivityGetNextOtpFailed(SophTokenActivity.this, this.mAccount.user, e);
                    return;
                }
            }
            throw new RuntimeException("Account not in list: " + this.mAccount);
        }

        private int findAccountPositionInList() {
            int length = SophTokenActivity.this.mUsers.length;
            for (int i = 0; i < length; i++) {
                if (SophTokenActivity.this.mUsers[i] == this.mAccount) {
                    return i;
                }
            }
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PinListAdapter extends ArrayAdapter<PinInfo> {
        public PinListAdapter(Context context, int i, PinInfo[] pinInfoArr) {
            super(context, i, pinInfoArr);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r9v1, types: [android.view.View] */
        /* JADX WARN: Type inference failed for: r9v4 */
        /* JADX WARN: Type inference failed for: r9v5 */
        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = SophTokenActivity.this.getLayoutInflater();
            PinInfo item = getItem(i);
            ViewGroup viewGroup2 = view;
            if (view == null) {
                viewGroup2 = layoutInflater.inflate(R.layout.user_row, (ViewGroup) null);
            }
            TextView textView = (TextView) viewGroup2.findViewById(R.id.pin_value);
            TextView textView2 = (TextView) viewGroup2.findViewById(R.id.current_user);
            View findViewById = viewGroup2.findViewById(R.id.next_otp);
            CountdownIndicator countdownIndicator = (CountdownIndicator) viewGroup2.findViewById(R.id.countdown_icon);
            if (item.isHotp) {
                findViewById.setVisibility(0);
                findViewById.setEnabled(item.hotpCodeGenerationAllowed);
                viewGroup2.setDescendantFocusability(393216);
                NextOtpButtonListener nextOtpButtonListener = new NextOtpButtonListener(item);
                findViewById.setOnClickListener(nextOtpButtonListener);
                viewGroup2.setTag(nextOtpButtonListener);
                countdownIndicator.setVisibility(8);
            } else {
                findViewById.setVisibility(8);
                findViewById.setOnClickListener(null);
                viewGroup2.setTag(null);
                countdownIndicator.setVisibility(0);
                countdownIndicator.setPhase(item.TotpCountdownPhase);
            }
            if (SophTokenActivity.this.getString(R.string.empty_pin).equals(item.pin)) {
                textView.setTextScaleX(SophTokenActivity.PIN_TEXT_SCALEX_UNDERSCORE);
            } else {
                textView.setTextScaleX(SophTokenActivity.PIN_TEXT_SCALEX_NORMAL);
            }
            textView.setText(item.pin);
            textView2.setText(item.user);
            return viewGroup2;
        }
    }

    private void importDataFromOldAppIfNecessary() {
        if (this.mDataImportInProgress) {
            return;
        }
        this.mDataImportInProgress = true;
        DependencyInjector.getDataImportController().start(this, new ImportController.Listener() { // from class: com.sophos.sophtoken.SophTokenActivity.13
            @Override // com.sophos.sophtoken.dataimport.ImportController.Listener
            public void onOldAppUninstallSuggested(Intent intent) {
                if (SophTokenActivity.this.isFinishing()) {
                    return;
                }
                SophTokenActivity.this.mOldAppUninstallIntent = intent;
                SophTokenActivity.this.showDialog(12);
            }

            @Override // com.sophos.sophtoken.dataimport.ImportController.Listener
            public void onDataImported() {
                if (SophTokenActivity.this.isFinishing()) {
                    return;
                }
                SophTokenActivity.this.refreshUserList(true);
                DependencyInjector.getOptionalFeatures().onDataImportedFromOldApp(SophTokenActivity.this);
            }

            @Override // com.sophos.sophtoken.dataimport.ImportController.Listener
            public void onFinished() {
                if (SophTokenActivity.this.isFinishing()) {
                    return;
                }
                SophTokenActivity.this.mDataImportInProgress = false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SaveKeyDialogParams implements Serializable {
        private final Integer algorithm;
        private final Integer counter;
        private final Integer digits;
        private final String secret;
        private final Integer timestep;
        private final AccountDb.OtpType type;
        private final String user;

        private SaveKeyDialogParams(String str, String str2, AccountDb.OtpType otpType, Integer num, Integer num2, Integer num3, Integer num4) {
            this.user = str;
            this.secret = str2;
            this.type = otpType;
            this.timestep = num;
            this.counter = num2;
            this.digits = num3;
            this.algorithm = num4;
        }
    }
}
