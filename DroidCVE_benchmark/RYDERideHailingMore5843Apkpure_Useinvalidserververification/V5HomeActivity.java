package com.sts.ryde.v5.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.appboy.enums.Channel;
import com.braze.IBrazeDeeplinkHandler;
import com.braze.ui.BrazeDeeplinkHandler;
import com.braze.ui.actions.NewsfeedAction;
import com.braze.ui.actions.UriAction;
import com.braze.ui.inappmessage.BrazeInAppMessageManager;
import com.facebook.gamingservices.cloudgaming.internal.SDKConstants;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.material.timepicker.TimeModel;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.framework.common.ContainerUtils;
import com.huawei.hms.push.HmsMessaging;
import com.rydesharing.ryde.R;
import com.rydesharing.ryde.RDApplication;
import com.sts.ryde.buildconfig.BuildConfiguration;
import com.sts.ryde.data.AppData;
import com.sts.ryde.data.UserManager;
import com.sts.ryde.data.model.AdditionalStopAddress;
import com.sts.ryde.data.model.CreateTripTaxiModel;
import com.sts.ryde.data.model.JobInfo;
import com.sts.ryde.data.model.JobInfoResponseModel;
import com.sts.ryde.data.model.JobRiderRequestModel;
import com.sts.ryde.data.model.LastTripResponseModel;
import com.sts.ryde.data.model.LoginResponseJSM;
import com.sts.ryde.data.model.MemberInfo;
import com.sts.ryde.data.model.MemberSettingsResponseModel;
import com.sts.ryde.data.model.MenuPromotionBannerModel;
import com.sts.ryde.data.model.OtherUserProfileResponseModel;
import com.sts.ryde.data.model.PaymentSourceModel;
import com.sts.ryde.data.model.PaymentSourcesResponseModel;
import com.sts.ryde.data.model.PromoBannerModel;
import com.sts.ryde.data.model.PromoBannerResponseModel;
import com.sts.ryde.data.model.PushMessageInfo;
import com.sts.ryde.data.model.RDLocation;
import com.sts.ryde.data.model.RouteModel;
import com.sts.ryde.data.model.RouteResponseModel;
import com.sts.ryde.data.model.RydeHireHoursModel;
import com.sts.ryde.data.model.RydeSettingInfo;
import com.sts.ryde.data.model.SchoolModel;
import com.sts.ryde.data.model.Taxi;
import com.sts.ryde.define.AppConstant;
import com.sts.ryde.mvvm.tools.datadog.RydeDatadog;
import com.sts.ryde.mvvm.ui.ProfileFragment;
import com.sts.ryde.mvvm.ui.PromotionFragment;
import com.sts.ryde.mvvm.ui.ScanQrCodeFragment;
import com.sts.ryde.mvvm.ui.businessprofile.BusinessProfileLandingFragment;
import com.sts.ryde.mvvm.ui.rydeplus.SubscriptionSettingsFragment;
import com.sts.ryde.mvvm.ui.rydesend.BookingRydeSendFragment;
import com.sts.ryde.mvvm.ui.rydesend.LandingFragment;
import com.sts.ryde.mvvm.ui.savedaddress.SavedAddressFragment;
import com.sts.ryde.mvvm.ui.upcomingpastlist.UpcomingPastTripsFragment;
import com.sts.ryde.proxy.RDBaseService;
import com.sts.ryde.proxy.RDServiceConfig;
import com.sts.ryde.proxy.RDServiceHelper;
import com.sts.ryde.pushnotification.FcmListenerService;
import com.sts.ryde.ui.dialog.DialogManager;
import com.sts.ryde.ui.dialog.MatchDialog;
import com.sts.ryde.ui.dialog.MatchDialogFoundFragment;
import com.sts.ryde.ui.dialog.RDAlertDialog;
import com.sts.ryde.ui.dialog.RDConfirmDialog;
import com.sts.ryde.utils.PreferenceUtils;
import com.sts.ryde.utils.Utils;
import com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer;
import com.sts.ryde.v5.basecomponent.Event;
import com.sts.ryde.v5.basecomponent.SideMenuFragment;
import com.sts.ryde.v5.branch.BranchEvents;
import com.sts.ryde.v5.cdg.FindingTaxiEvent;
import com.sts.ryde.v5.customui.MyCustomRecyclerView;
import com.sts.ryde.v5.dialog.HomeToWorkDialog;
import com.sts.ryde.v5.editprofile.PictureSelectorFragment;
import com.sts.ryde.v5.helper.PermissionHelper;
import com.sts.ryde.v5.home.RiderHomeAdapter;
import com.sts.ryde.v5.kotlin.NotificationSchedule;
import com.sts.ryde.v5.kotlin.UpdateMemberDetailEvent;
import com.sts.ryde.v5.kotlin.dialog.LostItemDialog;
import com.sts.ryde.v5.kotlin.dialog.NotificationCheckerPopupFragment;
import com.sts.ryde.v5.kotlin.dialog.payment.TopUpSuccessDialog;
import com.sts.ryde.v5.kotlin.dialog.payment.TopupSuccessFragment;
import com.sts.ryde.v5.kotlin.dialog.rydesend.RydeSendDriverCancelledDialog;
import com.sts.ryde.v5.kotlin.dialog.rydesend.RydeSendTripExpiredDialog;
import com.sts.ryde.v5.kotlin.model.bonus.BonusOverviewModel;
import com.sts.ryde.v5.kotlin.model.firebasetoken.FirebaseTokenModel;
import com.sts.ryde.v5.kotlin.model.firestoretrip.FirestoreTrip;
import com.sts.ryde.v5.kotlin.model.pendingupcomingtrip.PendingTrip;
import com.sts.ryde.v5.kotlin.model.pendingupcomingtrip.PendingTripModel;
import com.sts.ryde.v5.kotlin.model.pendingupcomingtrip.UpcomingTripModel;
import com.sts.ryde.v5.kotlin.model.rydehelp.LostItemsUnreadModel;
import com.sts.ryde.v5.kotlin.model.tracking.TrackingLocationInfo;
import com.sts.ryde.v5.kotlin.model.tracking.TrackingLocationModel;
import com.sts.ryde.v5.kotlin.model.tripdetails.DriverInfo;
import com.sts.ryde.v5.kotlin.model.tripdetails.TripDetailInfo;
import com.sts.ryde.v5.kotlin.model.tripdetails.TripDetailModel;
import com.sts.ryde.v5.kotlin.pages.BookingFragments.BookingFragment;
import com.sts.ryde.v5.kotlin.pages.BoostFragments.RydeBoostFragment;
import com.sts.ryde.v5.kotlin.pages.BusinessProfileFragments.BusinessProfileFragment;
import com.sts.ryde.v5.kotlin.pages.FWDFragments.RegisterFWDFragment;
import com.sts.ryde.v5.kotlin.pages.RydeChatFragment;
import com.sts.ryde.v5.kotlin.pages.RydeHelpFragments.LostFoundFragment;
import com.sts.ryde.v5.kotlin.pages.RydeHelpFragments.LostItemChatFragment;
import com.sts.ryde.v5.kotlin.pages.TopupFragments.TopupFragment;
import com.sts.ryde.v5.kotlin.pages.TopupFragments.TopupVoucherFragment;
import com.sts.ryde.v5.kotlin.pages.TripFragments.TripAcceptedFragment;
import com.sts.ryde.v5.kotlin.pages.TripFragments.TripPendingFragment;
import com.sts.ryde.v5.kotlin.pages.TripFragments.TripReviewFragment;
import com.sts.ryde.v5.location.GoogleLocationHelper;
import com.sts.ryde.v5.login.V5LoginActivity;
import com.sts.ryde.v5.model.NewPricingResponseModel;
import com.sts.ryde.v5.model.SuggestedJobsResponseModel;
import com.sts.ryde.v5.modelinstantiate.JobInstantiate;
import com.sts.ryde.v5.profile.ReviewProfileFragment;
import com.sts.ryde.v5.promotion.RefreshBonusOverviewEvent;
import com.sts.ryde.v5.rydehelp.RydeHelpFragment;
import com.sts.ryde.v5.rydepay.AddCreditCardFragment;
import com.sts.ryde.v5.rydepay.PaymentMethodFragment;
import com.sts.ryde.v5.rydepay.PaymentOptionsFragment;
import com.sts.ryde.v5.rydepay.RefreshPaymentsEvent;
import com.sts.ryde.v5.rydepay.RydeCreditsFragment;
import com.sts.ryde.v5.rydesend.ItemListFragment;
import com.sts.ryde.v5.schoolpool.SchoolPoolDialog;
import com.sts.ryde.v5.settings.EditEmailAddressFragment;
import com.sts.ryde.v5.settings.EditMobileNumberFragment;
import com.sts.ryde.v5.settings.EmailVerificationFragment;
import com.sts.ryde.v5.settings.MobileVerificationFragment;
import com.sts.ryde.v5.settings.SettingsFragment;
import com.sts.ryde.v5.tripdetail.SelectTripTypeHelper;
import com.sts.ryde.v5.utils.WebViewFragment;
import com.sts.ryde.v5.webservices.WebServiceCalls;
import com.urbanairship.UAirship;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.actions.DeepLinkListener;
import io.branch.referral.Branch;
import io.netty.util.internal.StringUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* loaded from: classes5.dex */
public class V5HomeActivity extends BaseActivityWithMenuDrawer implements MyCustomRecyclerView.OnRefresh, RDBaseService.IBaseService, View.OnClickListener, GoogleLocationHelper.OnLocationChangedListener, DialogInterface.OnDismissListener, RiderHomeAdapter.RiderHomeAdapterListener, DeepLinkListener {
    public static final String FINDING_TAXI_JOB_ID = "finding_taxi_job_id";
    public static final int HOME_TO_WORK = 1;
    public static final int SCHOOL = 3;
    public static final String TAXI_RETRY_COUNT = "taxi_retry_count";
    public JobInfo activeTaxiBooking;
    private RDAlertDialog alert;
    private String amountHeader;
    Branch branch;
    private CountDownTimer countDownTimer;
    private DeepLinkAction deepLinkAction;
    private Dialog dialog;
    private RDAlertDialog dialogAlert;
    public DocumentReference docRef;
    FragmentManager fragmentManager;
    private GoogleLocationHelper googleLocationHelper;
    private ImageView imvBonusLady;
    private ImageView imvBonusRydePlusLogo;
    private ImageView imvRydePlusLabel;
    private RydeSettingInfo.RydeSettingData.RydeSetting info;
    private ArrayList<RDBaseService> listRequest;
    public Location location;
    private LostItemDialog lostFoundChatDialog;
    private LinearLayout lyPromo;
    private RelativeLayout lyRiderPromo;
    private RelativeLayout lyRiderPromoLabel;
    private View lyRydePay;
    private MatchDialog matchDialog;
    private MatchDialogFoundFragment matchFoundDialog;
    private ArrayList<PromoBannerModel> promoList;
    private PromoBannerResponseModel promo_info;
    private List<PushMessageInfo> pushInfos;
    public JobInfo queueJob;
    private RDConfirmDialog rdConfirmDialog;
    private MyCustomRecyclerView recyclerView;
    private View reminderTrip;
    private int retry_time;
    private RiderHomeAdapter riderHomeAdapter;
    private Runnable runnable;
    private Runnable runnableRetry;
    private RydeSendDriverCancelledDialog rydeSendDriverCancelledDialog;
    private RydeSendTripExpiredDialog rydeSendTripExpiredDialog;
    private RDLocation startLocation;
    public TopupSuccessFragment successDialog;
    private int taxiRetryNumberLimit;
    private TopUpSuccessDialog topUpSuccessDialog;
    public ListenerRegistration tripListener;
    private TextView tvBalanceAmount;
    private TextView tvBalanceTitle;
    private TextView tvBonusOverview;
    private TextView txtReminderContent;
    private TextView txtReminderCountdown;
    private TripDetailModel upcomingTripDetailModel;
    private View viewOpacity;
    private JobInfo findingTaxiJobInfo = null;
    private int home_work_school = 0;
    private boolean updateUserLocation = true;
    PaymentSourceModel rydeCredits = null;
    Handler startAnimationHandler = new Handler();
    Handler stopAnimationHandler = new Handler();
    long STOP_ANIMATION_DURATION = 2000;
    long START_ANIMATION_DURATION = 1000;
    private MemberInfo currentUser = null;
    private RDLocation homeAddress = null;
    private RDLocation workAddress = null;
    private boolean isGoToTripDetails = false;
    private boolean isFromPromoDeepLink = false;
    private double match_stop_latitude = 0.0d;
    private double match_stop_longitude = 0.0d;
    private boolean isFirstLoad = true;
    private Boolean is_boosted = false;
    Runnable startAnimationRunnable = new Runnable() { // from class: com.sts.ryde.v5.home.V5HomeActivity.1
        @Override // java.lang.Runnable
        public void run() {
            V5HomeActivity.this.stopAnimationHandler.postDelayed(V5HomeActivity.this.stopAnimationRunnable, V5HomeActivity.this.STOP_ANIMATION_DURATION);
        }
    };
    Runnable stopAnimationRunnable = new Runnable() { // from class: com.sts.ryde.v5.home.V5HomeActivity.2
        @Override // java.lang.Runnable
        public void run() {
            V5HomeActivity.this.startBounceAnimation();
            V5HomeActivity.this.startAnimationHandler.postDelayed(V5HomeActivity.this.startAnimationRunnable, V5HomeActivity.this.START_ANIMATION_DURATION);
        }
    };
    private Handler handler = new Handler();

    /* loaded from: classes5.dex */
    private interface SELECT {
        public static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
        public static final int MATCH_DIALOG_FOUND = 100;
        public static final int NOTIFICATION_CHECKER = 101;
        public static final int NOTIFICATION_RYDESEND_DRIVER_CANCELED = 102;
        public static final int TOP_UP_SUCCESS = 997;
    }

    /* loaded from: classes5.dex */
    private interface SHORTCUTS {
        public static final String INSURANCE = "insurance";
        public static final String LUCKY = "luckyspin";
        public static final String PER_PAX = "rydepoolperpax";
        public static final String PET = "rydepet";
        public static final String PROMO = "promotion";
        public static final String RYDE_EXEC = "rydeexec";
        public static final String RYDE_FLASH = "rydepool";
        public static final String RYDE_HIRE = "rydehire";
        public static final String RYDE_SEND = "rydesend";
        public static final String RYDE_X = "rydex";
        public static final String RYDE_XL = "rydexl";
        public static final String TAXI = "rydetaxi";
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithHeader
    protected int getMainContentLayoutResourceId() {
        return R.layout.v5_activity_home;
    }

    protected long[] getTime() {
        return new long[0];
    }

    @Override // com.urbanairship.actions.DeepLinkListener
    public boolean onDeepLink(String s) {
        return false;
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithHeader
    protected int getSecondRightButtonBackgroundResourceAnimationId() {
        if (this.showClose) {
            this.txtUnreadCount.setVisibility(8);
            return 0;
        }
        this.txtUnreadCount.setVisibility(0);
        if (TextUtils.isEmpty(this.amountHeader)) {
            return R.drawable.wallet_animation;
        }
        this.txtUnreadCount.setText(this.amountHeader);
        return R.drawable.wallet_animation;
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.BaseActivityWithHeader, com.sts.ryde.v5.basecomponent.BaseActivityNavigation, com.sts.ryde.ui.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        boolean z;
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        this.currentUser = UserManager.getInstance().getMemberInfo();
        this.fragmentManager = getSupportFragmentManager();
        if (this.currentUser.isDriver()) {
            showLoading();
            WebServiceCalls.logOut(this, this);
        } else {
            if (!PermissionHelper.haveLocationPermission(this)) {
                PermissionHelper.requestLocationPermission(this);
            }
            this.branch = Branch.getInstance(getApplicationContext());
            RydeSettingInfo.RydeSettingData.RydeSetting configs = PreferenceUtils.getConfigs(this);
            this.info = configs;
            if (configs != null) {
                this.taxiRetryNumberLimit = configs.systemConfigs.rydetaxiNumberLimitTrying;
                z = this.info.systemConfigs.showRydeX;
            } else {
                z = true;
            }
            this.listRequest = new ArrayList<>();
            this.branch.setIdentity(String.format("R%s%s", this.currentUser.countryCode, this.currentUser.mobileNumber));
            this.branch.userCompletedAction(BranchEvents.Events.RIDER_HOME_SCREEN_LOAD);
            RiderHomeAdapter riderHomeAdapter = new RiderHomeAdapter(this, this);
            this.riderHomeAdapter = riderHomeAdapter;
            riderHomeAdapter.showRydeX(z);
            this.recyclerView.setAdapter(this.riderHomeAdapter);
            refreshRydeCredits(null);
            this.googleLocationHelper = new GoogleLocationHelper(this, this);
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.3
                @Override // androidx.fragment.app.FragmentManager.OnBackStackChangedListener
                public void onBackStackChanged() {
                    EventBus.getDefault().post(new PopBackStackCallback());
                    if (V5HomeActivity.this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        V5HomeActivity.this.resetLeftMenuItem();
                        if (AppConstant.BUILD_STATE == BuildConfiguration.BuildState.DEVELOP) {
                            V5HomeActivity.this.setTitle(R.string.app_name_dev);
                        } else {
                            V5HomeActivity.this.setTitle(R.string.app_name);
                        }
                    }
                }
            });
            RDApplication.initializeSecondaryFirebase(this);
            WebServiceCalls.getMemberSettings(this, this);
            PreferenceUtils.setNotDisplayHomeToWorkPopup(this, this.currentUser.id);
            handleIntent();
            handleBranchIntent();
            WebServiceCalls.getAvailablePaymentSource(this, this);
            WebServiceCalls.getPromoBanner(this, this);
            WebServiceCalls.getRiderBonusOverview(this, this);
            WebServiceCalls.getLostItemsUnread(this, this);
            RDApplication.updateBugsnagUser(this.currentUser.id, this.currentUser.firstName);
            if (!RDApplication.isGooglePlayServicesAvailable(getApplicationContext()) && Build.MANUFACTURER.equals("HUAWEI")) {
                getHuaweiToken();
            } else {
                RDApplication.saveDeviceTokenRydeChat(this, AppConstant.RYDECHAT_USER, this.currentUser.id, RDApplication.getDeviceToken(), this.currentUser.firstName, this.currentUser.mobileNumber, Integer.parseInt(this.currentUser.countryCode), false);
            }
            RDApplication.updateFirebaseProperties(this.currentUser.uuid, this.currentUser.firstName);
            RydeDatadog.INSTANCE.setDatadogUser(this.currentUser.uuid, this.currentUser.firstName);
            if (RDApplication.loadFirestoreTrip(this) != null) {
                retrieveFirestoreTrip(AppConstant.FIRESTORE_TRIPS, RDApplication.loadFirestoreTrip(this).getJob_id(), Boolean.valueOf(RDApplication.loadFirestoreTrip(this).getShowMatchCard()), Boolean.valueOf(RDApplication.loadFirestoreTrip(this).getCancelJob()));
            }
            RDApplication.getGrpcChannel(this);
        }
        UAirship.shared().getAnalytics().trackScreen("Home");
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            NotificationCheckerPopupFragment init = NotificationCheckerPopupFragment.INSTANCE.init();
            init.setTargetFragment(this.fragmentManager.getPrimaryNavigationFragment(), 101);
            init.show(this.fragmentManager, NotificationCheckerPopupFragment.class.getName());
        }
        this.deepLinkAction = new DeepLinkAction();
        authorizeFirebaseToken();
        if (UserManager.getInstance().getMemberInfo().businessEmail != null) {
            getNextBusinessReminder();
        }
        UAirship.shared().setDeepLinkListener(new DeepLinkListener() { // from class: com.sts.ryde.v5.home.-$$Lambda$V5HomeActivity$6SFtnATja3LRSqg-o3V4DJ1AHN4
            @Override // com.urbanairship.actions.DeepLinkListener
            public final boolean onDeepLink(String str) {
                return V5HomeActivity.this.lambda$onCreate$0$V5HomeActivity(str);
            }
        });
        BrazeDeeplinkHandler.setBrazeDeeplinkHandler(new IBrazeDeeplinkHandler() { // from class: com.sts.ryde.v5.home.V5HomeActivity.4
            @Override // com.braze.IBrazeDeeplinkHandler
            public int getIntentFlags(IBrazeDeeplinkHandler.IntentFlagPurpose intentFlagPurpose) {
                return 0;
            }

            @Override // com.braze.IBrazeDeeplinkHandler
            public void gotoNewsFeed(Context context, NewsfeedAction newsfeedAction) {
            }

            @Override // com.braze.IBrazeDeeplinkHandler
            public void gotoUri(Context context, UriAction uriAction) {
            }

            @Override // com.braze.IBrazeDeeplinkHandler
            public UriAction createUriActionFromUrlString(String s, Bundle bundle, boolean b, Channel channel) {
                String queryParameter;
                if (s == null || (queryParameter = Uri.parse(s).getQueryParameter("action")) == null) {
                    return null;
                }
                V5HomeActivity.this.handleAirshipDeeplink(queryParameter);
                return null;
            }

            @Override // com.braze.IBrazeDeeplinkHandler
            public UriAction createUriActionFromUri(Uri uri, Bundle bundle, boolean b, Channel channel) {
                String queryParameter;
                if (uri == null || (queryParameter = uri.getQueryParameter("action")) == null) {
                    return null;
                }
                V5HomeActivity.this.handleAirshipDeeplink(queryParameter);
                return null;
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$V5HomeActivity(String str) {
        String queryParameter = Uri.parse(str).getQueryParameter("action");
        if (queryParameter == null) {
            return false;
        }
        handleAirshipDeeplink(queryParameter);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAirshipDeeplink(String action) {
        handleAction(action, null, null, null, null, null, null, null, null, null, null);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.sts.ryde.v5.home.V5HomeActivity$5] */
    private void getHuaweiToken() {
        new Thread() { // from class: com.sts.ryde.v5.home.V5HomeActivity.5
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    String token = HmsInstanceId.getInstance(V5HomeActivity.this).getToken(AGConnectServicesConfig.fromContext(V5HomeActivity.this).getString("client/app_id"), HmsMessaging.DEFAULT_TOKEN_SCOPE);
                    if (TextUtils.isEmpty(token)) {
                        return;
                    }
                    RDApplication.saveDeviceToken(token);
                    RDApplication.saveDeviceTokenRydeChat(V5HomeActivity.this, AppConstant.RYDECHAT_USER, V5HomeActivity.this.currentUser.id, token, V5HomeActivity.this.currentUser.firstName, V5HomeActivity.this.currentUser.mobileNumber, Integer.parseInt(V5HomeActivity.this.currentUser.countryCode), true);
                } catch (Exception unused) {
                }
            }
        }.start();
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.BaseActivityWithHeader
    protected void initView() {
        super.initView();
        View findViewById = findViewById(R.id.reminder_trip);
        this.reminderTrip = findViewById;
        findViewById.setOnClickListener(this);
        this.txtReminderContent = (TextView) this.reminderTrip.findViewById(R.id.txt_reminder_content);
        this.txtReminderCountdown = (TextView) this.reminderTrip.findViewById(R.id.txt_reminder_count_down);
        MyCustomRecyclerView myCustomRecyclerView = (MyCustomRecyclerView) findViewById(R.id.recycler_view);
        this.recyclerView = myCustomRecyclerView;
        myCustomRecyclerView.setUpParams(false, true, new LinearLayoutManager(this), new ItemOffsetDecoration(10), null, this);
        View findViewById2 = findViewById(R.id.view_opacity);
        this.viewOpacity = findViewById2;
        findViewById2.setOnClickListener(this);
        View findViewById3 = findViewById(R.id.ly_ryde_pay);
        this.lyRydePay = findViewById3;
        findViewById3.findViewById(R.id.ly_scan).setOnClickListener(this);
        View findViewById4 = this.lyRydePay.findViewById(R.id.ly_ryde_credits);
        findViewById4.setOnClickListener(this);
        this.tvBalanceTitle = (TextView) findViewById4.findViewById(R.id.tv_balance_title);
        this.tvBalanceAmount = (TextView) findViewById4.findViewById(R.id.tv_balance_amount);
        this.lyRydePay.findViewById(R.id.ly_top_up).setOnClickListener(this);
        findViewById(getContainerFullScreen()).setVisibility(0);
        this.tvBonusOverview = (TextView) findViewById(R.id.tv_promo_available);
        this.imvBonusRydePlusLogo = (ImageView) findViewById(R.id.imv_bonus_rydeplus);
        this.imvBonusLady = (ImageView) findViewById(R.id.imv_promo_lady);
        this.imvRydePlusLabel = (ImageView) findViewById(R.id.imv_label_rydeplus);
        this.lyRiderPromo = (RelativeLayout) findViewById(R.id.ly_rider_promo);
        this.lyRiderPromoLabel = (RelativeLayout) findViewById(R.id.ly_rider_promo_label);
        this.lyPromo = (LinearLayout) findViewById(R.id.ly_promo);
        this.lyRiderPromoLabel.setOnClickListener(this);
        this.stopAnimationHandler.postDelayed(this.stopAnimationRunnable, this.STOP_ANIMATION_DURATION);
        if (UserManager.getInstance().getMemberInfo().isRydePlusOptIn()) {
            setRydePlusUi();
        } else {
            setNonRydePlusUi();
        }
    }

    private void setRydePlusUi() {
        this.imvBonusLady.setVisibility(0);
        this.imvBonusLady.setBackgroundResource(R.drawable.ic_promo_boy_shade);
        this.imvRydePlusLabel.setVisibility(8);
    }

    private void setNonRydePlusUi() {
        this.imvBonusLady.setVisibility(0);
        this.imvBonusLady.setBackgroundResource(R.drawable.ic_promo_boy_no_shade);
        this.imvRydePlusLabel.setVisibility(8);
        this.lyRiderPromoLabel.setBackgroundResource(R.drawable.background_rider_promo);
        this.tvBonusOverview.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBounceAnimation() {
        this.lyRiderPromo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_up));
    }

    private void showNewFeatureDialog() {
        if (PreferenceUtils.isDisplayHomeToWorkPopup(this, this.currentUser.id)) {
            showOverlay(true);
            HomeToWorkDialog homeToWorkDialog = new HomeToWorkDialog(this, this.location);
            homeToWorkDialog.setOnDismissListener(this);
            homeToWorkDialog.show();
            PreferenceUtils.setNotDisplayHomeToWorkPopup(this, this.currentUser.id);
        }
    }

    public void requestLastTrip(boolean isShowLoading) {
        WebServiceCalls.requestUpcomingTrip(this, this);
        if (isShowLoading) {
            showLoading();
        }
    }

    @Override // com.sts.ryde.v5.customui.MyCustomRecyclerView.OnRefresh
    public void onRefresh() {
        requestLastTrip(false);
        WebServiceCalls.getCommuteStatus(this, this);
        WebServiceCalls.getRiderBonusOverview(this, this);
        WebServiceCalls.getPromoBanner(this, this);
        WebServiceCalls.getLostItemsUnread(this, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sts.ryde.ui.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        GoogleLocationHelper googleLocationHelper = this.googleLocationHelper;
        if (googleLocationHelper != null) {
            googleLocationHelper.onStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sts.ryde.ui.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        GoogleLocationHelper googleLocationHelper = this.googleLocationHelper;
        if (googleLocationHelper != null) {
            googleLocationHelper.onStop();
        }
        stopCountDownReminder();
        super.onStop();
        stopRetryTimer();
        ListenerRegistration listenerRegistration = this.tripListener;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            this.tripListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sts.ryde.ui.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        BrazeInAppMessageManager.getInstance().registerInAppMessageManager(this);
        RDApplication.activityResumed();
        if (PermissionHelper.haveLocationPermission(this)) {
            if (!Utils.checkLocationEnabled(this)) {
                RDAlertDialog rDAlertDialog = this.alert;
                if (rDAlertDialog != null && rDAlertDialog.isShowing()) {
                    return;
                } else {
                    this.alert = DialogManager.showDialogAlert(this, getResources().getString(R.string.please_enable_location), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.6
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            V5HomeActivity.this.alert.dismiss();
                            Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                            intent.setFlags(268435456);
                            V5HomeActivity.this.startActivity(intent);
                        }
                    }, false);
                }
            } else {
                RDAlertDialog rDAlertDialog2 = this.alert;
                if (rDAlertDialog2 != null && rDAlertDialog2.isShowing()) {
                    this.alert.dismiss();
                    this.alert = null;
                }
            }
        }
        PushMessageInfo pushMessageInfo = (PushMessageInfo) PreferenceUtils.loadObject(this, PreferenceUtils.PUSH_INFO, PushMessageInfo.class);
        if (pushMessageInfo != null) {
            PreferenceUtils.clearValue(this, PreferenceUtils.PUSH_INFO);
            pushInfoHandler(pushMessageInfo);
        }
        String loadString = PreferenceUtils.loadString(this, "RYDE_SHOW_USER_PROFILE", null);
        if (loadString != null) {
            PreferenceUtils.clearValue(this, "RYDE_SHOW_USER_PROFILE");
            requestGetOtherUserProfile(loadString);
        }
        requestLastTrip(false);
        try {
            refreshRydeCredits(null);
        } catch (Exception unused) {
        }
        getWindow().clearFlags(8192);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        BrazeInAppMessageManager.getInstance().unregisterInAppMessageManager(this);
        RDApplication.activityPaused();
        ListenerRegistration listenerRegistration = this.tripListener;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            this.tripListener = null;
        }
        setScreenCache();
    }

    private void setScreenCache() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (backStackEntryCount != -1) {
            Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount).getName());
            if ((findFragmentByTag instanceof SettingsFragment) || (findFragmentByTag instanceof WebViewFragment) || (findFragmentByTag instanceof PaymentMethodFragment) || (findFragmentByTag instanceof RydeCreditsFragment) || (findFragmentByTag instanceof MobileVerificationFragment) || (findFragmentByTag instanceof EmailVerificationFragment) || (findFragmentByTag instanceof AddCreditCardFragment) || (findFragmentByTag instanceof PaymentOptionsFragment) || (findFragmentByTag instanceof RegisterFWDFragment) || (findFragmentByTag instanceof BusinessProfileFragment) || (findFragmentByTag instanceof EditEmailAddressFragment) || (findFragmentByTag instanceof EditMobileNumberFragment)) {
                getWindow().setFlags(8192, 8192);
            } else {
                getWindow().clearFlags(8192);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        ListenerRegistration listenerRegistration = this.tripListener;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            this.tripListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getSupportFragmentManager().popBackStack((String) null, 1);
        resetLeftMenuItem();
        setTitle(R.string.app_name);
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer
    public void closeButtonClicked() {
        super.closeButtonClicked();
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_up);
        this.lyRydePay.setVisibility(8);
        showOverlay(false);
        if (AppConstant.BUILD_STATE == BuildConfiguration.BuildState.DEVELOP) {
            setTitle(R.string.app_name_dev);
        } else {
            setTitle(R.string.app_name);
        }
        invalidateOptionsMenu();
    }

    private void processJobWaitingQueue() {
        if (this.activeTaxiBooking != null) {
            stopCountDownReminder();
            this.reminderTrip.setVisibility(0);
            this.txtReminderCountdown.setText("");
            if (this.activeTaxiBooking.isTripTaxiPending()) {
                this.txtReminderContent.setText(getString(R.string.finding_taxi_now));
                return;
            }
            Taxi taxi = this.activeTaxiBooking.taxi;
            if (taxi != null) {
                if (this.activeTaxiBooking.isTaxiAccepted()) {
                    this.txtReminderContent.setText(String.format(getString(R.string.taxi_on_way), taxi.taxiNumber));
                    return;
                } else if (this.activeTaxiBooking.isTaxiArrived()) {
                    this.txtReminderContent.setText(String.format(getString(R.string.taxi_arrived_banner), taxi.taxiNumber));
                    return;
                } else {
                    if (this.activeTaxiBooking.isTaxiTripStarted()) {
                        this.txtReminderContent.setText(String.format(getString(R.string.taxi_started_banner), taxi.taxiNumber));
                        return;
                    }
                    return;
                }
            }
            return;
        }
        startCountDownReminder();
    }

    private void stopCountDownReminder() {
        Runnable runnable = this.runnable;
        if (runnable != null) {
            this.handler.removeCallbacks(runnable);
        }
    }

    private void startCountDownReminder() {
        String str;
        String str2;
        stopCountDownReminder();
        if (this.queueJob == null) {
            this.reminderTrip.setVisibility(8);
            return;
        }
        this.reminderTrip.setVisibility(0);
        final long max = Math.max(Long.parseLong(this.queueJob.driverStartTime), Long.parseLong(this.queueJob.rideStartTime));
        this.runnable = new Runnable() { // from class: com.sts.ryde.v5.home.V5HomeActivity.7
            @Override // java.lang.Runnable
            public void run() {
                long currentTimeMillis = max - (System.currentTimeMillis() / 1000);
                long hours = TimeUnit.SECONDS.toHours(currentTimeMillis);
                if (hours > 0) {
                    V5HomeActivity.this.txtReminderCountdown.setText(String.format("%02dh:%02d", Long.valueOf(hours), Long.valueOf(TimeUnit.SECONDS.toMinutes(currentTimeMillis) - (hours * 60))));
                    V5HomeActivity.this.handler.postDelayed(this, 1000L);
                    return;
                }
                long minutes = TimeUnit.SECONDS.toMinutes(currentTimeMillis);
                if (minutes <= 0) {
                    V5HomeActivity.this.txtReminderCountdown.setText("");
                } else {
                    V5HomeActivity.this.txtReminderCountdown.setText(String.format("%02dm:%02d", Long.valueOf(minutes), Long.valueOf(currentTimeMillis - (minutes * 60))));
                    V5HomeActivity.this.handler.postDelayed(this, 1000L);
                }
            }
        };
        MemberInfo memberInfo = this.queueJob.driverInfo;
        if (memberInfo != null) {
            String firstName = memberInfo.getFirstName();
            String str3 = memberInfo.carNumberPlate;
            boolean equals = this.queueJob.tripType.equals(JobInfo.TRIP_TYPE.RYDE_SEND_REQUEST);
            if (this.queueJob.isTripAccepted()) {
                this.txtReminderContent.setText(getString(R.string.home_reminder_content, new Object[]{this.queueJob.getTripType(this), firstName}));
                this.handler.post(this.runnable);
                return;
            }
            stopCountDownReminder();
            this.txtReminderCountdown.setText("");
            if (this.queueJob.isDriverOnTheWay()) {
                if (!TextUtils.isEmpty(str3)) {
                    firstName = firstName + "(" + str3 + ")";
                }
                this.txtReminderContent.setText(firstName + " is on the way");
                return;
            }
            if (this.queueJob.isDriverArrived()) {
                String str4 = firstName + " ";
                if (!TextUtils.isEmpty(str3)) {
                    str4 = str4 + "(" + str3 + ") ";
                }
                if (equals) {
                    str2 = str4 + " arrived at pickup";
                } else {
                    str2 = str4 + " arrived";
                }
                this.txtReminderContent.setText(str2);
                return;
            }
            if (this.queueJob.isTripStarted()) {
                if (!TextUtils.isEmpty(str3)) {
                    firstName = firstName + "(" + str3 + ") ";
                }
                this.txtReminderContent.setText(equals ? "Item is in transit" : firstName + " trip started");
            } else {
                if (this.queueJob.isTripDriverArrivedAtDropoff()) {
                    this.txtReminderContent.setText(firstName + " arrived at dropoff");
                    return;
                }
                if (equals) {
                    str = "Review " + firstName;
                } else {
                    str = "Review trip with " + firstName;
                }
                this.txtReminderContent.setText(str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openUpcomingReminder() {
        String str;
        String str2;
        TripDetailModel tripDetailModel = this.upcomingTripDetailModel;
        if (tripDetailModel == null) {
            this.reminderTrip.setVisibility(8);
            return;
        }
        TripDetailInfo result = tripDetailModel.getResult();
        this.reminderTrip.setVisibility(0);
        final long max = Math.max(Long.parseLong(result.getStartTime()), Long.parseLong(result.getStartTime()));
        this.runnable = new Runnable() { // from class: com.sts.ryde.v5.home.V5HomeActivity.8
            @Override // java.lang.Runnable
            public void run() {
                long currentTimeMillis = max - (System.currentTimeMillis() / 1000);
                long hours = TimeUnit.SECONDS.toHours(currentTimeMillis);
                if (hours > 0) {
                    V5HomeActivity.this.txtReminderCountdown.setText(String.format("%02dh:%02d", Long.valueOf(hours), Long.valueOf(TimeUnit.SECONDS.toMinutes(currentTimeMillis) - (hours * 60))));
                    V5HomeActivity.this.handler.postDelayed(this, 1000L);
                    return;
                }
                long minutes = TimeUnit.SECONDS.toMinutes(currentTimeMillis);
                if (minutes <= 0) {
                    V5HomeActivity.this.txtReminderCountdown.setText("");
                } else {
                    V5HomeActivity.this.txtReminderCountdown.setText(String.format("%02dm:%02d", Long.valueOf(minutes), Long.valueOf(currentTimeMillis - (minutes * 60))));
                    V5HomeActivity.this.handler.postDelayed(this, 1000L);
                }
            }
        };
        DriverInfo driverInfo = result.getDriverInfo();
        if (driverInfo != null) {
            String firstName = driverInfo.getFirstName();
            String carPlateNumber = driverInfo.getCarPlateNumber();
            boolean z = result.getPriceType() == 9 || result.getPriceType() == 10;
            if (result.getStatus() == 1) {
                this.txtReminderContent.setText(getString(R.string.home_reminder_content, new Object[]{JobInfo.getTripType(this, result.getPriceType()), driverInfo.getFirstName()}));
                this.handler.post(this.runnable);
                return;
            }
            stopCountDownReminder();
            this.txtReminderCountdown.setText("");
            if (result.getStatus() == 21) {
                if (!TextUtils.isEmpty(carPlateNumber)) {
                    firstName = firstName + "(" + carPlateNumber + ")";
                }
                this.txtReminderContent.setText(firstName + " is on the way");
                return;
            }
            if (result.getStatus() == 18) {
                String str3 = firstName + " ";
                if (!TextUtils.isEmpty(carPlateNumber)) {
                    str3 = str3 + "(" + carPlateNumber + ") ";
                }
                if (z) {
                    str2 = str3 + " arrived at pickup";
                } else {
                    str2 = str3 + " arrived";
                }
                this.txtReminderContent.setText(str2);
                return;
            }
            if (result.getStatus() == 13) {
                if (!TextUtils.isEmpty(carPlateNumber)) {
                    firstName = firstName + "(" + carPlateNumber + ") ";
                }
                this.txtReminderContent.setText(z ? "Item is in transit" : firstName + " trip started");
            } else {
                if (result.getStatus() == 22) {
                    this.txtReminderContent.setText(firstName + " arrived at dropoff");
                    return;
                }
                if (z) {
                    str = "Review " + firstName;
                } else {
                    str = "Review trip with " + firstName;
                }
                this.txtReminderContent.setText(str);
            }
        }
    }

    @Override // com.sts.ryde.ui.BaseActivity
    public void pushInfoHandler(PushMessageInfo pushInfo) {
        super.pushInfoHandler(pushInfo);
        prepareForPushNotification();
    }

    private void prepareForPushNotification() {
        if (AppData.getInstance().haveNotification) {
            AppData.getInstance().haveNotification = false;
            boolean z = false;
            for (int i = 0; i < this.pushInfos.size(); i++) {
                if (!TextUtils.isEmpty(this.pushInfos.get(i).pushData.jobID) && this.pushInfos.get(i).pushData.jobID.equalsIgnoreCase(AppData.getInstance().pushInfo.pushData.jobID) && this.pushInfos.get(i).pushData.type == AppData.getInstance().pushInfo.pushData.type) {
                    z = true;
                }
            }
            this.pushInfos.add(AppData.getInstance().pushInfo);
            if (z) {
                if (AppData.getInstance().pushInfo.pushData.message == null) {
                    FcmListenerService.sendNotificationHomeScreen(this, PaymentSourceModel.PAYMENT_BRAND.RYDE, AppData.getInstance().pushInfo.pushData.title);
                    return;
                } else {
                    FcmListenerService.sendNotificationHomeScreen(this, PaymentSourceModel.PAYMENT_BRAND.RYDE, AppData.getInstance().pushInfo.pushData.message);
                    return;
                }
            }
            showPushAlertMessage(AppData.getInstance().pushInfo, true);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        this.pushInfos = new ArrayList();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00be, code lost:
    
        if (r1.equals("rideInsurance") == false) goto L51;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showPushAlertMessage(final com.sts.ryde.data.model.PushMessageInfo r17, boolean r18) {
        /*
            Method dump skipped, instructions count: 834
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sts.ryde.v5.home.V5HomeActivity.showPushAlertMessage(com.sts.ryde.data.model.PushMessageInfo, boolean):void");
    }

    public /* synthetic */ void lambda$showPushAlertMessage$1$V5HomeActivity() {
        RDApplication.firebaseLogEvents("check_promotions_overview");
        onPromoClickListener();
    }

    public /* synthetic */ void lambda$showPushAlertMessage$2$V5HomeActivity(PushMessageInfo.PushData pushData) {
        EventBus.getDefault().post(new Event.RefreshLostFound());
        EventBus.getDefault().post(new Event.LostItemEvent(0, true));
        addFragmentFullScreen(LostItemChatFragment.INSTANCE.newInstance(pushData.firstName, Integer.parseInt(pushData.senderId), pushData.countryCode, pushData.phoneNumber, null, Integer.parseInt(pushData.tripId)), true);
    }

    private void showDialogAlert(String alertTitle, String alertMessage) {
        if (TextUtils.isEmpty(alertTitle) && TextUtils.isEmpty(alertMessage)) {
            return;
        }
        FcmListenerService.sendNotificationHomeScreen(this, alertTitle, alertMessage);
    }

    private void showDialogAlert(String alertTitle, String alertMessage, View.OnClickListener listener) {
        if (TextUtils.isEmpty(alertTitle) && TextUtils.isEmpty(alertMessage)) {
            return;
        }
        RDAlertDialog rDAlertDialog = this.dialogAlert;
        if (rDAlertDialog != null && rDAlertDialog.isShowing()) {
            this.dialogAlert.dismiss();
            this.dialogAlert = null;
        }
        if (TextUtils.isEmpty(alertTitle)) {
            this.dialogAlert = DialogManager.showDialogAlert(this, alertMessage, listener, false);
        } else {
            this.dialogAlert = DialogManager.showDialogAlertWithTitle(this, alertTitle, alertMessage, listener, false);
        }
    }

    public void callLastTripForJobAccept(final String jobId) {
        try {
            RDBaseService.GetTripDetailService getTripDetailService = (RDBaseService.GetTripDetailService) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_NEW_TRIP_DETAIL, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.16
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
                    V5HomeActivity.this.hideLoading();
                    TripDetailModel tripDetailModel = (TripDetailModel) result;
                    if (tripDetailModel != null) {
                        V5HomeActivity.this.upcomingTripDetailModel = tripDetailModel;
                        TripDetailInfo result2 = tripDetailModel.getResult();
                        V5HomeActivity.this.match_stop_latitude = result2.getStartLocation().getLatitude();
                        V5HomeActivity.this.match_stop_longitude = result2.getStartLocation().getLongitude();
                        V5HomeActivity.this.showMatchDialog(result2);
                        V5HomeActivity.this.realTimeOnDemandTripUpdate(AppConstant.FIRESTORE_TRIPS, String.valueOf(result2.getId()));
                        if (!result2.getTripType().equals(JobInfo.TRIP_TYPE.ON_DEMAND_REQUEST) && !result2.getTripType().equals(JobInfo.TRIP_TYPE.RYDE_SEND_REQUEST)) {
                            V5HomeActivity.this.matchFoundDialog.setJobTime();
                        } else if (result2.getDriverInfo() != null) {
                            V5HomeActivity.this.requestTrackingLocation(result2.getDriverInfo().getId());
                        }
                    }
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                    V5HomeActivity.this.hideLoading();
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                    V5HomeActivity.this.hideLoading();
                }
            });
            getTripDetailService.setUrl(AppConstant.BASE_URL_MS + RDServiceConfig.ServiceConfigName.GET_NEW_TRIP_DETAIL + "/" + String.valueOf(jobId));
            getTripDetailService.setInfo();
            getTripDetailService.connect(this, TripDetailModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callLastTripForJobCanceled(final String jobId) {
        try {
            RDBaseService.GetTripDetailService getTripDetailService = (RDBaseService.GetTripDetailService) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_NEW_TRIP_DETAIL, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.17
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
                    V5HomeActivity.this.hideLoading();
                    TripDetailModel tripDetailModel = (TripDetailModel) result;
                    if (tripDetailModel != null) {
                        TripDetailInfo result2 = tripDetailModel.getResult();
                        Utils.playCancellationSound(V5HomeActivity.this);
                        V5HomeActivity.this.openHome();
                        RDApplication.saveFirestoreTripInfo(V5HomeActivity.this, null);
                        if (result2.getTripType().equals(JobInfo.TRIP_TYPE.RYDE_SEND_REQUEST)) {
                            V5HomeActivity.this.showRydeSendDriverCancelled(String.valueOf(result2.getId()));
                        } else {
                            final RDConfirmDialog rDConfirmDialog = new RDConfirmDialog(V5HomeActivity.this);
                            rDConfirmDialog.showDialog(result2.getDriverInfo().getFirstName() + " has cancelled the trip.", "If you still need of a ride, please book another trip. We apologize for the inconvenience!", V5HomeActivity.this.getString(R.string.okay), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.17.1
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view) {
                                    rDConfirmDialog.dismiss();
                                }
                            });
                        }
                    }
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                    V5HomeActivity.this.hideLoading();
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                    V5HomeActivity.this.hideLoading();
                }
            });
            getTripDetailService.setUrl(AppConstant.BASE_URL_MS + RDServiceConfig.ServiceConfigName.GET_NEW_TRIP_DETAIL + "/" + String.valueOf(jobId));
            getTripDetailService.setInfo();
            getTripDetailService.connect(this, TripDetailModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMatchFound(LastTripResponseModel.LastTripsModel data, String jobId) {
        if (data != null) {
            this.queueJob = null;
            ArrayList<JobInfo> arrayList = data.acceptedAdvance;
            if (arrayList.size() > 0) {
                int i = 0;
                boolean z = false;
                while (true) {
                    if (i >= arrayList.size()) {
                        break;
                    }
                    JobInfo jobInfo = arrayList.get(i);
                    if (!z) {
                        z = handleReminderJob(jobInfo);
                    }
                    if (jobInfo.id.equalsIgnoreCase(jobId)) {
                        showMatchDialog(jobInfo);
                        break;
                    }
                    i++;
                }
                processJobWaitingQueue();
            } else {
                this.reminderTrip.setVisibility(8);
            }
            EventBus.getDefault().post(new SideMenuFragment.TripBadge(arrayList.size()));
        }
    }

    private void handlePendingTrip(PendingTripModel responseModel) {
        if (responseModel != null) {
            ArrayList<JobInfo> arrayList = new ArrayList<>();
            if (responseModel.getResult() != null) {
                ArrayList<PendingTrip> result = responseModel.getResult();
                if (result.size() > 0 && result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(0).getTripType().equals(JobInfo.TRIP_TYPE.ON_DEMAND_REQUEST) || result.get(0).getTripType().equals(JobInfo.TRIP_TYPE.RYDE_SEND_REQUEST)) {
                            if (result.get(0).getStatus() == 0) {
                                RDApplication.setOnDemandJobFireStore(this, AppConstant.FIRESTORE_TRIPS, String.valueOf(result.get(0).getId()), result.get(0).getStatus());
                            }
                            realTimeOnDemandTripUpdate(AppConstant.FIRESTORE_TRIPS, String.valueOf(result.get(0).getId()));
                        }
                        JobInfo jobInfo = new JobInfo();
                        RydeHireHoursModel rydeHireHoursModel = new RydeHireHoursModel();
                        jobInfo.id = String.valueOf(result.get(i).getId());
                        jobInfo.rideStartTime = String.valueOf(result.get(i).getTime());
                        jobInfo.startPlace = result.get(i).getStartPlace();
                        jobInfo.endPlace = result.get(i).getEndPlace();
                        jobInfo.priceType = result.get(i).getPriceType();
                        jobInfo.price = String.valueOf(result.get(i).getPrice());
                        jobInfo.additionalStops = result.get(i).getAdditionalStops();
                        jobInfo.status = String.valueOf(result.get(i).getStatus());
                        jobInfo.tripType = result.get(i).getTripType();
                        jobInfo.priceOriginal = String.valueOf(result.get(i).getRydeTaxiLowFare());
                        jobInfo.boosted = String.valueOf(result.get(i).getBoosted());
                        if (result.get(i).getTripType().equals(JobInfo.TRIP_TYPE.RYDE_HIRE)) {
                            rydeHireHoursModel.hours = result.get(i).getRydeHireDuration();
                        }
                        if (result.get(i).isAdvanceRequest()) {
                            jobInfo.isAdvanceRequest = 1;
                        } else {
                            jobInfo.isAdvanceRequest = 0;
                        }
                        if (result.get(i).getTripType().equals(JobInfo.TRIP_TYPE.PRIVATE_REQUEST) && result.get(i).getDriverInfo() != null) {
                            MemberInfo memberInfo = new MemberInfo();
                            memberInfo.firstName = result.get(i).getDriverInfo().getFirstName();
                            jobInfo.driverInfo = memberInfo;
                        }
                        jobInfo.rydeHire = rydeHireHoursModel;
                        arrayList.add(jobInfo);
                    }
                }
            } else {
                arrayList.clear();
            }
            RiderHomeAdapter riderHomeAdapter = this.riderHomeAdapter;
            if (riderHomeAdapter != null) {
                riderHomeAdapter.addDashboardItems(arrayList);
            }
        }
    }

    private boolean handleReminderJob(JobInfo job) {
        if (job.isTripDriverCompleted() || job.isDriverOnTheWay() || job.isDriverArrived() || job.isTripStarted() || job.isTripDriverArrivedAtDropoff()) {
            this.queueJob = job;
            return true;
        }
        if (!job.isTripAccepted()) {
            return false;
        }
        if (this.queueJob == null) {
            this.queueJob = job;
            return false;
        }
        if (Double.parseDouble(job.getTripStartTime()) >= Double.parseDouble(this.queueJob.getTripStartTime())) {
            return false;
        }
        this.queueJob = job;
        return false;
    }

    private boolean handleUpcomingReminder(TripDetailModel tripDetailModel) {
        TripDetailInfo result = tripDetailModel.getResult();
        if (result.getStatus() != 7 && result.getStatus() != 21 && result.getStatus() != 18 && result.getStatus() != 13 && result.getStatus() != 22 && result.getStatus() != 1) {
            return false;
        }
        this.upcomingTripDetailModel = tripDetailModel;
        return true;
    }

    private void showMatchDialog(final JobInfo job) {
        if (job == null) {
            return;
        }
        MatchDialog matchDialog = this.matchDialog;
        if (matchDialog == null || !matchDialog.isShowing()) {
            Utils.playTripMatchSound(this);
            this.matchDialog = DialogManager.showMatchDialog(this, job, new MatchDialog.MatchDialogDelegate() { // from class: com.sts.ryde.v5.home.V5HomeActivity.18
                @Override // com.sts.ryde.ui.dialog.MatchDialog.MatchDialogDelegate
                public void onOKClick() {
                    V5HomeActivity.this.matchDialog.dismiss();
                    WebServiceCalls.requestTripDetail(V5HomeActivity.this, Integer.parseInt(job.id), V5HomeActivity.this);
                }
            });
        }
    }

    public void showMatchDialog(final TripDetailInfo job) {
        if (job == null) {
            return;
        }
        MatchDialogFoundFragment matchDialogFoundFragment = this.matchFoundDialog;
        if (matchDialogFoundFragment == null || !matchDialogFoundFragment.isShowing()) {
            Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag(RydeBoostFragment.class.getName());
            if (findFragmentByTag != null && (findFragmentByTag instanceof RydeBoostFragment)) {
                onBackPressed();
            }
            requestLastTrip(false);
            UserManager.getInstance().clearBoostId(this);
            RDApplication.removeRydeBoostPopup(this);
            Utils.playTripMatchSound(this);
            this.matchFoundDialog = DialogManager.showMatchFoundDialog(this, job, new MatchDialogFoundFragment.MatchDialogFoundDelegate() { // from class: com.sts.ryde.v5.home.-$$Lambda$V5HomeActivity$g9gaWHHK_UmD2O-Bxaf8lfZnIJU
                @Override // com.sts.ryde.ui.dialog.MatchDialogFoundFragment.MatchDialogFoundDelegate
                public final void onOKClick() {
                    V5HomeActivity.this.lambda$showMatchDialog$3$V5HomeActivity(job);
                }
            });
            RDApplication.saveFirestoreTripInfo(this, new FirestoreTrip(String.valueOf(job.getId()), job.getStatus(), false, false, false));
        }
    }

    public /* synthetic */ void lambda$showMatchDialog$3$V5HomeActivity(TripDetailInfo tripDetailInfo) {
        Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag(BookingFragment.class.getName());
        if (findFragmentByTag instanceof BookingFragment) {
            ((BookingFragment) findFragmentByTag).closeDialogs();
        }
        this.matchFoundDialog.dismiss();
        WebServiceCalls.requestTripDetail(this, tripDetailInfo.getId(), this);
    }

    /* renamed from: com.sts.ryde.v5.home.V5HomeActivity$42, reason: invalid class name */
    /* loaded from: classes5.dex */
    static /* synthetic */ class AnonymousClass42 {
        static final /* synthetic */ int[] $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType;

        static {
            int[] iArr = new int[RDServiceConfig.ServiceConfigType.values().length];
            $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType = iArr;
            try {
                iArr[RDServiceConfig.ServiceConfigType.GET_TRIP_INFO.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.JOB_TAXI_CREATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_MEMBER_SETTINGS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_OTHER_USER_PROFILE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.LOGOUT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_AVAILABLE_PAYMENT_SOURCE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_BONUS_OVERVIEW.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_PROMO_BANNER.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_NEW_PENDING_TRIP.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_NEW_UPCOMING_TRIP.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_NEW_TRIP_DETAIL.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_NEW_TRACKING_LOCATION.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_ROUTE.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.RIDER_BONUS_OVERVIEW.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.GET_LOST_ITEMS_UNREAD.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.AUTH_FIREBASE_TOKEN.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[RDServiceConfig.ServiceConfigType.USER_DETAIL.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
        }
    }

    @Override // com.sts.ryde.ui.BaseActivity, com.sts.ryde.proxy.RDBaseService.IBaseService
    public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
        int i;
        RouteModel routeModel;
        super.onWebServiceSuccess(type, result);
        switch (AnonymousClass42.$SwitchMap$com$sts$ryde$proxy$RDServiceConfig$ServiceConfigType[type.ordinal()]) {
            case 1:
                try {
                    i = Integer.parseInt(((JobInfoResponseModel) result).data.status);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    i = 1;
                }
                if (i == 0) {
                    return;
                }
                this.dialogAlert = DialogManager.showDialogAlert(this, getString(R.string.trip_not_available), null, true);
                return;
            case 2:
                JobInfoResponseModel jobInfoResponseModel = (JobInfoResponseModel) result;
                if (!jobInfoResponseModel.status) {
                    this.dialog = DialogManager.showDialogAlertWithTitle(this, jobInfoResponseModel.error.title, jobInfoResponseModel.error.message, new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.19
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            V5HomeActivity.this.dialog.dismiss();
                        }
                    }, false);
                    return;
                } else {
                    EventBus.getDefault().post(new FindingTaxiEvent(true, jobInfoResponseModel.data, true));
                    return;
                }
            case 3:
                MemberSettingsResponseModel.MemberSettings memberSettings = ((MemberSettingsResponseModel) result).memberSettings;
                if (memberSettings != null) {
                    getHomeWorkAddress(memberSettings);
                    MenuPromotionBannerModel menuPromotionBannerModel = memberSettings.menuPromotionBanner;
                    if (menuPromotionBannerModel != null) {
                        EventBus.getDefault().post(new SideMenuFragment.PromotionBanner(menuPromotionBannerModel));
                    }
                    int i2 = this.home_work_school;
                    if (i2 == 1) {
                        if (this.homeAddress != null && this.workAddress != null) {
                            v5openRiderHomeToWork(JobInstantiate.createRiderJobInfo(this.homeAddress, this.workAddress, this.location), this.location, this.homeAddress, this.workAddress, memberSettings.verificationInfo != null ? memberSettings.verificationInfo.businessEmail : null);
                            return;
                        } else {
                            new HomeToWorkDialog(this, this.location).show();
                            this.home_work_school = 0;
                            return;
                        }
                    }
                    if (i2 != 3) {
                        return;
                    }
                    ArrayList<SchoolModel> arrayList = memberSettings.schoolList;
                    if (this.homeAddress != null && arrayList != null && arrayList.size() > 0) {
                        v5openSchoolPoolListFragment(this.location, this.homeAddress, arrayList);
                        return;
                    } else {
                        new SchoolPoolDialog(this, this.location).show();
                        return;
                    }
                }
                return;
            case 4:
                hideLoading();
                OtherUserProfileResponseModel otherUserProfileResponseModel = (OtherUserProfileResponseModel) result;
                if (otherUserProfileResponseModel.data != null) {
                    ReviewProfileFragment newInstance = ReviewProfileFragment.newInstance(otherUserProfileResponseModel.data);
                    newInstance.setEnableContact(false);
                    newInstance.setAllowChat(true);
                    newInstance.setHideChat(false);
                    newInstance.setLastLocation(this.location);
                    newInstance.setStartLocation(this.startLocation);
                    addFragmentUnderActionbar(newInstance, true, true);
                    return;
                }
                return;
            case 5:
                hideLoading();
                Branch.getInstance().logout();
                UserManager.getInstance().cleanLoginData(this);
                LoginManager.getInstance().logOut();
                onSwitchAccountClickListener();
                return;
            case 6:
                PaymentSourcesResponseModel paymentSourcesResponseModel = (PaymentSourcesResponseModel) result;
                if (paymentSourcesResponseModel != null) {
                    paymentSourcesResponseModel.updateAvailablePayments(this);
                    return;
                }
                return;
            case 7:
                return;
            case 8:
                if (result instanceof PromoBannerResponseModel) {
                    this.promoList = new ArrayList<>();
                    ArrayList<PromoBannerModel> arrayList2 = ((PromoBannerResponseModel) result).result;
                    this.promoList = arrayList2;
                    this.riderHomeAdapter.updatePromoBanner(this, arrayList2);
                    return;
                }
                return;
            case 9:
                if (this.recyclerView.isRefreshing()) {
                    this.recyclerView.stopRefresh();
                }
                PendingTripModel pendingTripModel = (PendingTripModel) result;
                if (pendingTripModel != null) {
                    handlePendingTrip(pendingTripModel);
                    if (pendingTripModel.getResult() == null || pendingTripModel.getResult().size() <= 0 || this.info.systemConfigs.pricingBoostWaitTime <= 0) {
                        return;
                    }
                    PendingTrip pendingTrip = pendingTripModel.getResult().get(0);
                    Date currentDateTime = Utils.getCurrentDateTime();
                    Date addSecondtoDateTime = Utils.addSecondtoDateTime(String.valueOf(pendingTrip.getCreatedTime()), this.info.systemConfigs.pricingBoostWaitTime);
                    if (pendingTrip.getTripType().equals(JobInfo.TRIP_TYPE.ON_DEMAND_REQUEST) && currentDateTime.after(addSecondtoDateTime)) {
                        findTripForRydeBoost(String.valueOf(pendingTrip.getId()));
                        return;
                    }
                    return;
                }
                return;
            case 10:
                if (this.recyclerView.isRefreshing()) {
                    this.recyclerView.stopRefresh();
                }
                UpcomingTripModel upcomingTripModel = (UpcomingTripModel) result;
                if (upcomingTripModel.getResult() != null) {
                    getUpcomingTripDetail(upcomingTripModel.getResult().getId());
                    if ((upcomingTripModel.getResult().getTripType().equals(JobInfo.TRIP_TYPE.ON_DEMAND_REQUEST) || upcomingTripModel.getResult().getTripType().equals(JobInfo.TRIP_TYPE.RYDE_SEND_REQUEST)) && (upcomingTripModel.getResult().getStatus() == 1 || upcomingTripModel.getResult().getStatus() == 18 || upcomingTripModel.getResult().getStatus() == 21 || upcomingTripModel.getResult().getStatus() == 13 || upcomingTripModel.getResult().getStatus() == 31 || upcomingTripModel.getResult().getStatus() == 32 || upcomingTripModel.getResult().getStatus() == 33 || upcomingTripModel.getResult().getStatus() == 34 || upcomingTripModel.getResult().getStatus() == 35)) {
                        realTimeOnDemandTripUpdate(AppConstant.FIRESTORE_TRIPS, String.valueOf(upcomingTripModel.getResult().getId()));
                    } else if (upcomingTripModel.getResult().getStatus() == 13 || upcomingTripModel.getResult().getStatus() == 31 || upcomingTripModel.getResult().getStatus() == 32 || upcomingTripModel.getResult().getStatus() == 33 || upcomingTripModel.getResult().getStatus() == 34 || upcomingTripModel.getResult().getStatus() == 35) {
                        realTimeOnDemandTripUpdate(AppConstant.FIRESTORE_TRIPS, String.valueOf(upcomingTripModel.getResult().getId()));
                    } else if (upcomingTripModel.getResult().getStatus() == 7 && this.isFirstLoad) {
                        addFragmentFullScreen(TripReviewFragment.INSTANCE.newInstance(upcomingTripModel.getResult().getId(), String.valueOf(upcomingTripModel.getResult().getPriceType()), upcomingTripModel.getResult().getDriverId(), upcomingTripModel.getResult().getDriverName(), upcomingTripModel.getResult().getDriverAvatar(), true, true), true);
                    }
                } else {
                    this.reminderTrip.setVisibility(8);
                }
                this.isFirstLoad = false;
                WebServiceCalls.requestPendingTrips(this, 1, 10, this);
                return;
            case 11:
                TripDetailModel tripDetailModel = (TripDetailModel) result;
                if (tripDetailModel.getResult().getStatus() != 0) {
                    if (tripDetailModel.getResult().getStatus() == 7) {
                        addFragmentFullScreen(TripAcceptedFragment.INSTANCE.newInstance(tripDetailModel.getResult()), true);
                        return;
                    } else {
                        addFragmentFullScreen(TripAcceptedFragment.INSTANCE.newInstance(tripDetailModel.getResult()), true);
                        return;
                    }
                }
                addFragmentFullScreen(TripPendingFragment.INSTANCE.newInstance(tripDetailModel.getResult()), true);
                return;
            case 12:
                TrackingLocationInfo result2 = ((TrackingLocationModel) result).getResult();
                if (result2 != null) {
                    WebServiceCalls.getRoute(this, 0L, result2.getLatitude(), result2.getLongitude(), this.match_stop_latitude, this.match_stop_longitude, this);
                    return;
                }
                return;
            case 13:
                RouteResponseModel routeResponseModel = (RouteResponseModel) result;
                if (routeResponseModel == null || (routeModel = routeResponseModel.data) == null || routeModel == null || TextUtils.isEmpty(routeModel.points)) {
                    return;
                }
                long j = (int) routeModel.duration;
                String format = String.format(TimeModel.NUMBER_FORMAT, Long.valueOf(TimeUnit.SECONDS.toMinutes(j) - (TimeUnit.SECONDS.toHours(j) * 60)));
                if (this.matchFoundDialog != null) {
                    if (!format.equals("0")) {
                        this.matchFoundDialog.setDriverOnDemandArriving(format + " mins");
                        return;
                    } else {
                        this.matchFoundDialog.setDriverOnDemandArriving("1 min");
                        return;
                    }
                }
                return;
            case 14:
                BonusOverviewModel bonusOverviewModel = (BonusOverviewModel) result;
                if (bonusOverviewModel.getTitle() != null) {
                    this.lyPromo.setVisibility(0);
                    this.tvBonusOverview.setText(bonusOverviewModel.getTitle());
                    this.imvBonusRydePlusLogo.setVisibility(8);
                    if (UserManager.getInstance().getMemberInfo().isRydePlusOptIn()) {
                        setRydePlusUi();
                        return;
                    } else {
                        setNonRydePlusUi();
                        return;
                    }
                }
                this.lyPromo.setVisibility(0);
                return;
            case 15:
                LostItemsUnreadModel lostItemsUnreadModel = (LostItemsUnreadModel) result;
                if (lostItemsUnreadModel.getResult() != null) {
                    if (lostItemsUnreadModel.getResult().getChatActivated()) {
                        if (lostItemsUnreadModel.getResult().getCount() > 0) {
                            setIndicatorAlert(true);
                        } else {
                            setIndicatorAlert(false);
                        }
                    } else {
                        setIndicatorAlert(false);
                    }
                    EventBus.getDefault().post(new Event.LostItemEvent(lostItemsUnreadModel.getResult().getCount(), Boolean.valueOf(lostItemsUnreadModel.getResult().getChatActivated())));
                    return;
                }
                return;
            case 16:
                FirebaseTokenModel firebaseTokenModel = (FirebaseTokenModel) result;
                if (firebaseTokenModel != null) {
                    if (!firebaseTokenModel.status) {
                        Branch.getInstance().logout();
                        UserManager.getInstance().cleanLoginData(this);
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(AppData.getInstance().currentActivity, (Class<?>) V5LoginActivity.class);
                        intent.setFlags(268468224);
                        AppData.getInstance().currentActivity.startActivityWithAnimation(intent);
                        AppData.getInstance().currentActivity.finish();
                        return;
                    }
                    signInFirebaseWithCustomToken(firebaseTokenModel.getData().getToken());
                    return;
                }
                return;
            case 17:
                MemberInfo memberInfo = ((LoginResponseJSM) result).data.memberInfo;
                memberInfo.token = UserManager.getInstance().loadMemberToken();
                UserManager.getInstance().saveMember(memberInfo);
                EventBus.getDefault().post(new UpdateMemberDetailEvent());
                return;
            default:
                hideLoading();
                return;
        }
    }

    @Override // com.sts.ryde.ui.BaseActivity, com.sts.ryde.proxy.RDBaseService.IBaseService
    public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
        hideLoading();
        if (type == RDServiceConfig.ServiceConfigType.JOB_TAXI_CREATE) {
            stopRetryTimer();
            super.onWebServiceError(type, error, isException);
            return;
        }
        if (type == RDServiceConfig.ServiceConfigType.GET_LAST_TRIPS_REQUEST) {
            if (this.recyclerView.isRefreshing()) {
                this.recyclerView.stopRefresh();
            }
        } else {
            if (type == RDServiceConfig.ServiceConfigType.AUTH_FIREBASE_TOKEN) {
                Branch.getInstance().logout();
                UserManager.getInstance().cleanLoginData(this);
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(AppData.getInstance().currentActivity, (Class<?>) V5LoginActivity.class);
                intent.setFlags(268468224);
                AppData.getInstance().currentActivity.startActivityWithAnimation(intent);
                AppData.getInstance().currentActivity.finish();
                return;
            }
            super.onWebServiceError(type, error, isException);
            if (this.recyclerView.isRefreshing()) {
                this.recyclerView.stopRefresh();
            }
        }
    }

    @Override // com.sts.ryde.ui.BaseActivity, com.sts.ryde.proxy.RDBaseService.IBaseService
    public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
        hideLoading();
        if (this.recyclerView.isRefreshing()) {
            this.recyclerView.stopRefresh();
        }
        if (type == RDServiceConfig.ServiceConfigType.UPDATE_LOCATION || type == RDServiceConfig.ServiceConfigType.GET_LAST_TRIPS_REQUEST) {
            return;
        }
        super.onWebServiceOfflineMode(type, error);
    }

    @Override // com.sts.ryde.v5.location.GoogleLocationHelper.OnLocationChangedListener
    public void onLocationChanged(Location location) {
        this.location = location;
        EventBus.getDefault().post(location);
        if (this.updateUserLocation) {
            this.updateUserLocation = false;
        }
        if (this.listRequest.size() > 0) {
            Iterator<RDBaseService> it2 = this.listRequest.iterator();
            while (it2.hasNext()) {
                RDBaseService next = it2.next();
                if ((next instanceof RDBaseService.GetSuggestedJob) && UserManager.getInstance().getMemberInfo() != null) {
                    ((RDBaseService.GetSuggestedJob) next).setInfo(location.getLongitude(), location.getLatitude());
                    next.connect(this, SuggestedJobsResponseModel.class);
                    it2.remove();
                }
            }
        }
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.BaseActivityWithHeader, androidx.activity.ComponentActivity, android.app.Activity
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (!super.onCreateOptionsMenu(menu)) {
            return true;
        }
        this.secondView.setOnClickListener(new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.20
            @Override // android.view.View.OnClickListener
            public void onClick(final View v) {
                V5HomeActivity.this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.v5_action_bar_close);
                V5HomeActivity.this.setTitle(R.string.ryde_pay);
                V5HomeActivity.this.showClose = true;
                V5HomeActivity.this.branch.userCompletedAction(BranchEvents.Events.RIDER_HOME_CREDITS);
                Animation loadAnimation = AnimationUtils.loadAnimation(V5HomeActivity.this.getApplicationContext(), R.anim.slide_in_down);
                V5HomeActivity.this.lyRydePay.setVisibility(0);
                V5HomeActivity.this.showOverlay(true);
                V5HomeActivity.this.lyRydePay.startAnimation(loadAnimation);
                V5HomeActivity.this.invalidateOptionsMenu();
            }
        });
        return true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_rider_promo_label /* 2131363052 */:
                RDApplication.firebaseLogEvents("check_promotions_overview");
                onPromoClickListener();
                return;
            case R.id.ly_ryde_credits /* 2131363054 */:
                RDApplication.firebaseLogEvents("check_balance");
                closeButtonClicked();
                openRydeCredits();
                return;
            case R.id.ly_scan /* 2131363068 */:
                if (this.location == null || !Utils.checkLocationEnabled(this)) {
                    DialogManager.showDialogAlertWithTitle(this, getString(R.string.unable_to_scan), getString(R.string.enable_location_try_again), null, true);
                    return;
                } else {
                    closeButtonClicked();
                    addFragmentUnderActionbar(ScanQrCodeFragment.INSTANCE.newInstance(this.location), true);
                    return;
                }
            case R.id.ly_top_up /* 2131363120 */:
                RDApplication.firebaseLogEvents("topup_header");
                closeButtonClicked();
                openRydeCredits();
                return;
            case R.id.reminder_trip /* 2131363386 */:
                WebServiceCalls.requestTripDetail(this, this.upcomingTripDetailModel.getResult().getId(), this);
                return;
            case R.id.view_opacity /* 2131364240 */:
                closeButtonClicked();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sts.ryde.ui.BaseActivity
    public void showAdvanceTimeAlert() {
        super.showAdvanceTimeAlert();
        String string = getResources().getString(R.string.your_ryde_will_start);
        RDAlertDialog rDAlertDialog = this.dialogAlert;
        if (rDAlertDialog != null && rDAlertDialog.isShowing()) {
            this.dialogAlert.dismiss();
            this.dialogAlert = null;
        }
        this.dialogAlert = DialogManager.showDialogAlert(this, string, null, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestSuggestedTrip(Event.RequestSuggestedTripEvent event) {
        requestLastTrip(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateRydePlusEvent(Event.UpdateRydePlusEvent event) {
        if (event.getRydePlus().booleanValue()) {
            setRydePlusUi();
        } else {
            setNonRydePlusUi();
        }
    }

    @Override // com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onAvatarClickListener() {
        closeLeftMenu();
        addFragmentFullScreen(ProfileFragment.INSTANCE.newInstance(), true);
    }

    @Override // com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onSwitchAccountClickListener() {
        closeLeftMenu();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("rydedriver://open"));
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
            return;
        }
        Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("https://rydedriver.app.link/driver"));
        if (intent2.resolveActivity(packageManager) != null) {
            startActivity(intent2);
        }
    }

    @Override // com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onPromotionBannerClicked(String action) {
        closeLeftMenu();
        handleAction(action, null, null, null, null, null, null, null, null, null, null);
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onSettingsClickListener() {
        super.onSettingsClickListener();
        closeLeftMenu();
        v5openSettings(this.location);
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onFWDInsuranceClickListener() {
        super.onFWDInsuranceClickListener();
        closeLeftMenu();
        openFwdInsuranceFragment();
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onRydePlusClickListener() {
        super.onRydePlusClickListener();
        closeLeftMenu();
        onRydePlusClickFragment();
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onInsuranceClickListener() {
        closeLeftMenu();
        v5openInsuranceFragment();
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onPromotionClickListener() {
        this.branch.userCompletedAction(BranchEvents.Events.RIDER_HOME_PROMOTIONS);
        addFragmentFullScreen(PromotionFragment.INSTANCE.newInstance(false), true, true);
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onPromoClickListener() {
        addFragmentFullScreen(PromotionFragment.INSTANCE.newInstance(), true, true);
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onBusinessEmailClickListener() {
        if (UserManager.getInstance().getMemberInfo().businessEmail != null) {
            addFragmentUnderActionbar(BusinessProfileFragment.INSTANCE.newInstance(), true);
        } else {
            addFragmentUnderActionbar(new BusinessProfileLandingFragment(), true);
        }
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onLostFoundClickListener() {
        closeLeftMenu();
        addFragmentFullScreen(LostFoundFragment.INSTANCE.newInstance(), true, true);
    }

    public void onHomeToWorkClickListener() {
        this.home_work_school = 1;
        showLoading();
        WebServiceCalls.getMemberSettings(this, this);
    }

    public void onRydeSchoolClickListener() {
        this.home_work_school = 3;
        showLoading();
        WebServiceCalls.getMemberSettings(this, this);
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onLuckyClicked(String url) {
        this.branch.userCompletedAction(BranchEvents.Events.RIDER_HOME_LUCKY_SPIN);
        v5openWebViewBack(getString(R.string.play), url);
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onRydeSendClicked() {
        if (UserManager.getInstance().loadRydeSendLanding()) {
            addFragmentFullScreen(LandingFragment.INSTANCE.newInstance(), true);
        } else {
            addFragmentFullScreen(BookingRydeSendFragment.INSTANCE.newInstance(), true);
        }
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onRydeSendClicked(JobRiderRequestModel jobRiderRequest) {
        if (UserManager.getInstance().loadRydeSendLanding()) {
            addFragmentFullScreen(LandingFragment.INSTANCE.newInstance(), true);
        } else {
            addFragmentFullScreen(BookingRydeSendFragment.INSTANCE.newInstance(), true);
        }
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onPromotionClicked(PromoBannerModel promoBanner) {
        String str = promoBanner.action;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -505296440:
                if (str.equals("merchant")) {
                    c = 0;
                    break;
                }
                break;
            case -209928196:
                if (str.equals("rydehire")) {
                    c = 1;
                    break;
                }
                break;
            case 116079:
                if (str.equals("url")) {
                    c = 2;
                    break;
                }
                break;
            case 629233382:
                if (str.equals(SDKConstants.PARAM_TOURNAMENTS_DEEPLINK)) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                addFragmentFullScreen(ItemListFragment.newInstance(this.location, promoBanner.merchantId), true);
                return;
            case 1:
                onCreateTripRydeHire();
                return;
            case 2:
                v5openWebsite(promoBanner.website_url);
                return;
            case 3:
                onPromotionBannerClicked(promoBanner.deeplink_url);
                return;
            default:
                return;
        }
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onCreateTripClickListener(int type) {
        if (PermissionHelper.haveLocationPermission(this)) {
            if (!Utils.checkLocationEnabled(this)) {
                RDAlertDialog rDAlertDialog = this.alert;
                if (rDAlertDialog == null || !rDAlertDialog.isShowing()) {
                    this.alert = DialogManager.showDialogAlert(this, getResources().getString(R.string.please_enable_location), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.21
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            V5HomeActivity.this.alert.dismiss();
                            Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                            intent.setFlags(268435456);
                            V5HomeActivity.this.startActivity(intent);
                        }
                    }, false);
                    return;
                }
                return;
            }
            RDAlertDialog rDAlertDialog2 = this.alert;
            if (rDAlertDialog2 != null && rDAlertDialog2.isShowing()) {
                this.alert.dismiss();
                this.alert = null;
            }
            v5openCreateTrip(this.location, type);
        }
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onCreateTripRydeHire() {
        if (this.location == null) {
            RDAlertDialog rDAlertDialog = this.alert;
            if (rDAlertDialog == null || !rDAlertDialog.isShowing()) {
                this.alert = DialogManager.showDialogAlert(this, getResources().getString(R.string.check_location), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.22
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        V5HomeActivity.this.alert.dismiss();
                    }
                }, false);
                return;
            }
            return;
        }
        RDAlertDialog rDAlertDialog2 = this.alert;
        if (rDAlertDialog2 != null && rDAlertDialog2.isShowing()) {
            this.alert.dismiss();
            this.alert = null;
        }
        v5openCreateTripRydeHire(this.location);
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onCreateTripRydeHire(JobRiderRequestModel jobRiderRequest) {
        v5openCreateTripRydeHire(this.location, jobRiderRequest);
    }

    @Override // com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onInviteFriendsClickListener() {
        closeLeftMenu();
        v5openShareScreen();
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onMyTripClickListener() {
        addFragmentUnderActionbar(UpcomingPastTripsFragment.INSTANCE.newInstance(), true);
    }

    @Override // com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onRydePayClickListener() {
        closeLeftMenu();
        openPaymentOptions();
    }

    @Override // com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onRydeChatClickListener() {
        closeLeftMenu();
        v5openRydeChatScreen();
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onFavoriteClickListener() {
        super.onFavoriteClickListener();
        closeLeftMenu();
        v5openFavorite(null, 0, this.location, this.startLocation);
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onRiderPromoClickListener() {
        super.onRiderPromoClickListener();
        closeLeftMenu();
        onPromoClickListener();
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onBusinessProfileClickListener() {
        super.onBusinessProfileClickListener();
        closeLeftMenu();
        onBusinessEmailClickListener();
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onRiderLostFoundClickListener() {
        super.onRiderLostFoundClickListener();
        EventBus.getDefault().post(new Event.LostItemEvent(0, true));
        closeLeftMenu();
        onLostFoundClickListener();
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onTripClickListener() {
        super.onTripClickListener();
        closeLeftMenu();
        addFragmentUnderActionbar(UpcomingPastTripsFragment.INSTANCE.newInstance(), true);
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onSavedAddressClickListener() {
        super.onSavedAddressClickListener();
        closeLeftMenu();
        addFragmentFullScreen(SavedAddressFragment.INSTANCE.newInstance(), true);
    }

    @Override // com.sts.ryde.v5.basecomponent.BaseActivityWithMenuDrawer, com.sts.ryde.v5.basecomponent.OnSideMenuClickListener
    public void onRydeHelpClickListener() {
        super.onRydeHelpClickListener();
        closeLeftMenu();
        addFragmentUnderActionbar(RydeHelpFragment.newInstance(), true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showOverlay(boolean show) {
        if (show) {
            this.viewOpacity.setVisibility(0);
        } else {
            this.viewOpacity.setVisibility(8);
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        showOverlay(false);
    }

    private void requestGetOtherUserProfile(String userId) {
        showLoading();
        RDBaseService.GetOtherUserProfile getOtherUserProfile = (RDBaseService.GetOtherUserProfile) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_OTHER_USER_PROFILE, this);
        getOtherUserProfile.setInfo(userId);
        getOtherUserProfile.connect(this, OtherUserProfileResponseModel.class);
    }

    @Override // com.sts.ryde.v5.home.RiderHomeAdapter.RiderHomeAdapterListener
    public void onTripClicked(JobInfo item) {
        WebServiceCalls.requestTripDetail(this, Integer.parseInt(item.id), this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestJobInfoForPush(PushMessageInfo pushMessageInfo, int notification_type, boolean showPopUpTaxiFound, boolean isTaxi) {
        PushMessageInfo.PushData pushData = pushMessageInfo.pushData;
        if (pushData != null) {
            requestTripDetailForPush(pushData.jobID, notification_type, true);
        }
    }

    private void requestJobInfoForPush(PushMessageInfo pushMessageInfo, int notification_type, boolean showPopUpTaxiFound, boolean isTaxi, boolean showPopUp) {
        PushMessageInfo.PushData pushData = pushMessageInfo.pushData;
        if (pushData != null) {
            requestTripDetailForPush(pushData.jobID, notification_type, showPopUp);
        }
    }

    private void requestTripDetailForPush(final String jobId, final int notification_type, final boolean showPopUp) {
        try {
            RDBaseService.GetTripDetailService getTripDetailService = (RDBaseService.GetTripDetailService) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_NEW_TRIP_DETAIL, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.23
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
                    TripDetailInfo result2 = ((TripDetailModel) result).getResult();
                    if (PushMessageInfo.PushNotificationCarpoolTripDriverArrived(notification_type) || PushMessageInfo.PushNotificationCarpoolTripDriverOnTheWay(notification_type) || PushMessageInfo.PushNotificationCarpoolTripDriverPickUp(notification_type) || PushMessageInfo.PushNotificationCarpoolTripDriverCompleted(notification_type) || PushMessageInfo.PushNotificationDriveStop1(notification_type) || PushMessageInfo.PushNotificationDriveStop2(notification_type)) {
                        EventBus.getDefault().post(result2);
                        if (showPopUp) {
                            return;
                        }
                        if (result2.getStatus() == 18 || result2.getStatus() == 21 || result2.getStatus() == 13 || result2.getStatus() == 31 || result2.getStatus() == 32 || result2.getStatus() == 7) {
                            V5HomeActivity.this.addFragmentFullScreen(TripAcceptedFragment.INSTANCE.newInstance(result2, false), true);
                        }
                    }
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                    V5HomeActivity.this.hideLoading();
                }
            });
            getTripDetailService.setUrl(AppConstant.BASE_URL_MS + RDServiceConfig.ServiceConfigName.GET_NEW_TRIP_DETAIL + "/" + String.valueOf(jobId));
            getTripDetailService.setInfo();
            getTripDetailService.connect(this, TripDetailModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CreateTripTaxiModel CreateTaxiModelFromJobInfo(JobInfo jobInfo) {
        CreateTripTaxiModel createTripTaxiModel = new CreateTripTaxiModel();
        createTripTaxiModel.userLocationLatitude = jobInfo.riderLocationLatitude;
        createTripTaxiModel.userLocationLatitude = jobInfo.riderLocationLongitude;
        createTripTaxiModel.startPlace = jobInfo.startPlace;
        createTripTaxiModel.endPlace = jobInfo.endPlace;
        createTripTaxiModel.rideStartLocationLatitude = String.valueOf(jobInfo.getTripStartLocationLatitude());
        createTripTaxiModel.rideStartLocationLongitude = String.valueOf(jobInfo.getTripStartLocationLongitude());
        createTripTaxiModel.rideStopLocationLatitude = String.valueOf(jobInfo.getTripStopLocationLatitude());
        createTripTaxiModel.rideStopLocationLongitude = String.valueOf(jobInfo.getTripStopLocationLongitude());
        createTripTaxiModel.distance = Float.parseFloat(jobInfo.distance);
        createTripTaxiModel.priceType = jobInfo.priceType;
        createTripTaxiModel.googleEta = jobInfo.googleEta;
        createTripTaxiModel.note = jobInfo.note;
        createTripTaxiModel.mapRoute = jobInfo.mapRoute;
        Taxi taxi = jobInfo.taxi;
        if (taxi != null) {
            createTripTaxiModel.flatFare = taxi.flatfare;
            createTripTaxiModel.lowFare = taxi.lowfare;
            createTripTaxiModel.highFare = taxi.highfare;
            createTripTaxiModel.fromAddressReference = taxi.fromAddressReference;
            createTripTaxiModel.toAddressReference = taxi.toAddressReference;
        }
        createTripTaxiModel.referral_job_id = jobInfo.getId();
        return createTripTaxiModel;
    }

    private void BookTaxi(CreateTripTaxiModel taxiModel) {
        RDBaseService.CreateJobTaxiService createJobTaxiService = (RDBaseService.CreateJobTaxiService) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.JOB_TAXI_CREATE, this);
        createJobTaxiService.setInfo(taxiModel, 4 - this.taxiRetryNumberLimit);
        createJobTaxiService.connect(this, JobInfoResponseModel.class);
    }

    private void showRetryingDialog(final PushMessageInfo pushMessageInfo) {
        stopRetryTimer();
        int loadInt = PreferenceUtils.loadInt(this, TAXI_RETRY_COUNT, this.info.systemConfigs.rydetaxiNumberLimitTrying);
        this.taxiRetryNumberLimit = loadInt;
        if (loadInt > 0) {
            RydeSettingInfo.RydeSettingData.RydeSetting rydeSetting = this.info;
            if (rydeSetting != null) {
                this.retry_time = rydeSetting.systemConfigs.rydetaxiAutoTryingAfterXSecond;
            }
            RDConfirmDialog rDConfirmDialog = new RDConfirmDialog(this);
            this.rdConfirmDialog = rDConfirmDialog;
            rDConfirmDialog.showDialog(getString(R.string.no_taxi_available_now), String.format(getString(R.string.res_0x7f120514_retrying_in_), Integer.valueOf(this.info.systemConfigs.rydetaxiAutoTryingAfterXSecond)), getString(R.string.RETRY), getString(R.string.CANCEL), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.24
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    V5HomeActivity.this.stopRetryTimer();
                    V5HomeActivity v5HomeActivity = V5HomeActivity.this;
                    PreferenceUtils.saveInt(v5HomeActivity, V5HomeActivity.TAXI_RETRY_COUNT, v5HomeActivity.taxiRetryNumberLimit);
                    V5HomeActivity.this.rdConfirmDialog.dismiss();
                    V5HomeActivity.this.requestJobInfoForPush(pushMessageInfo, 11, false, true);
                }
            }, new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.25
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    V5HomeActivity.this.stopRetryTimer();
                    V5HomeActivity.this.rdConfirmDialog.dismiss();
                    V5HomeActivity v5HomeActivity = V5HomeActivity.this;
                    PreferenceUtils.saveInt(v5HomeActivity, V5HomeActivity.TAXI_RETRY_COUNT, v5HomeActivity.info.systemConfigs.rydetaxiNumberLimitTrying);
                }
            });
            this.rdConfirmDialog.setCancelable(false);
            Runnable runnable = new Runnable() { // from class: com.sts.ryde.v5.home.V5HomeActivity.26
                @Override // java.lang.Runnable
                public void run() {
                    if (V5HomeActivity.this.retry_time == 0) {
                        V5HomeActivity.this.rdConfirmDialog.dismiss();
                        V5HomeActivity.this.requestJobInfoForPush(pushMessageInfo, 11, false, true);
                        V5HomeActivity.this.taxiRetryNumberLimit--;
                        V5HomeActivity v5HomeActivity = V5HomeActivity.this;
                        PreferenceUtils.saveInt(v5HomeActivity, V5HomeActivity.TAXI_RETRY_COUNT, v5HomeActivity.taxiRetryNumberLimit);
                        return;
                    }
                    V5HomeActivity.this.retry_time--;
                    V5HomeActivity.this.rdConfirmDialog.updateMessade(String.format(V5HomeActivity.this.getString(R.string.res_0x7f120514_retrying_in_), Integer.valueOf(V5HomeActivity.this.retry_time)));
                    V5HomeActivity.this.handler.postDelayed(this, 1000L);
                }
            };
            this.runnableRetry = runnable;
            this.handler.postDelayed(runnable, 1000L);
            return;
        }
        this.dialog = DialogManager.showDialogAlertWithTitle(this, getString(R.string.taxi_unavailable), getString(R.string.unable_to_find_taxi), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.27
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EventBus.getDefault().post(new FindingTaxiEvent(false, null, false));
                V5HomeActivity.this.stopRetryTimer();
                V5HomeActivity.this.dialog.dismiss();
                V5HomeActivity v5HomeActivity = V5HomeActivity.this;
                PreferenceUtils.saveInt(v5HomeActivity, V5HomeActivity.TAXI_RETRY_COUNT, v5HomeActivity.info.systemConfigs.rydetaxiNumberLimitTrying);
            }
        }, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopRetryTimer() {
        Runnable runnable;
        Handler handler = this.handler;
        if (handler == null || (runnable = this.runnableRetry) == null) {
            return;
        }
        handler.removeCallbacks(runnable);
        RDConfirmDialog rDConfirmDialog = this.rdConfirmDialog;
        if (rDConfirmDialog != null) {
            rDConfirmDialog.dismiss();
        }
    }

    private void handleDriverOnTheWayArrived(final PushMessageInfo pushMessageInfo, boolean showNotification) {
        PushMessageInfo.PushData pushData = pushMessageInfo.pushData;
        if (pushData != null) {
            if (showNotification) {
                if (pushData.title != null && !TextUtils.isEmpty(pushData.title)) {
                    FcmListenerService.sendNotificationHomeScreen(this, pushData.title, pushData.message);
                } else {
                    FcmListenerService.sendNotificationHomeScreen(this, PaymentSourceModel.PAYMENT_BRAND.RYDE, pushData.message);
                }
            }
            requestJobInfoForPush(pushMessageInfo, pushData.type, false, false, showNotification);
        }
    }

    private void handleRatingNotification(final PushMessageInfo pushMessageInfo, boolean showNotification) {
        PushMessageInfo.PushData pushData = pushMessageInfo.pushData;
        if (pushData == null || !showNotification) {
            return;
        }
        if (pushData.message == null) {
            FcmListenerService.sendNotificationHomeScreen(this, PaymentSourceModel.PAYMENT_BRAND.RYDE, pushData.title);
        } else {
            FcmListenerService.sendNotificationHomeScreen(this, PaymentSourceModel.PAYMENT_BRAND.RYDE, pushData.message);
        }
    }

    /* loaded from: classes5.dex */
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int mItemOffset;

        ItemOffsetDecoration(int itemOffset) {
            this.mItemOffset = itemOffset;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int itemViewType = V5HomeActivity.this.riderHomeAdapter.getItemViewType(parent.getChildAdapterPosition(view));
            if (itemViewType == 3 || itemViewType == 4) {
                outRect.bottom = this.mItemOffset;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRydeCredits(RefreshPaymentsEvent event) {
        Iterator<PaymentSourceModel> it2 = PaymentSourcesResponseModel.getAvailablePayments(this).iterator();
        while (it2.hasNext()) {
            PaymentSourceModel next = it2.next();
            if (next.brand.equals(PaymentSourceModel.PAYMENT_BRAND.RYDE)) {
                this.rydeCredits = next;
            }
        }
        PaymentSourceModel paymentSourceModel = this.rydeCredits;
        if (paymentSourceModel != null) {
            this.amountHeader = String.format("%s %.2f", paymentSourceModel.currency, Double.valueOf(this.rydeCredits.balance));
            if (this.txtUnreadCount != null) {
                this.txtUnreadCount.setText(this.amountHeader);
            }
            this.tvBalanceAmount.setText(String.format("%.2f", Double.valueOf(this.rydeCredits.balance)));
            this.tvBalanceTitle.setText(getString(R.string.balance_title_home, new Object[]{this.rydeCredits.currency}));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleWebViewDeeplink(final Event.DeeplinkActionEvent actionEvent) {
        new Handler().postDelayed(new Runnable() { // from class: com.sts.ryde.v5.home.V5HomeActivity.28
            @Override // java.lang.Runnable
            public void run() {
                V5HomeActivity.this.handleAction(actionEvent.deepLinkAction(), null, null, null, null, null, null, null, null, null, null);
            }
        }, 500L);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBonusOverview(RefreshBonusOverviewEvent event) {
        WebServiceCalls.getRiderBonusOverview(this, this);
    }

    private void getHomeWorkAddress(MemberSettingsResponseModel.MemberSettings settings) {
        if (settings.homeAddress != null) {
            RDLocation rDLocation = settings.homeAddress;
            this.homeAddress = rDLocation;
            String str = rDLocation.name;
            if (TextUtils.isEmpty(str) || str.equals("Home")) {
                RDLocation rDLocation2 = this.homeAddress;
                rDLocation2.name = rDLocation2.address;
            } else {
                this.homeAddress.name += ", " + this.homeAddress.address;
            }
        }
        if (settings.workAddress != null) {
            RDLocation rDLocation3 = settings.workAddress;
            this.workAddress = rDLocation3;
            String str2 = rDLocation3.name;
            if (TextUtils.isEmpty(str2) || str2.equals("Work")) {
                RDLocation rDLocation4 = this.workAddress;
                rDLocation4.name = rDLocation4.address;
            } else {
                this.workAddress.name += ", " + this.workAddress.address;
            }
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        Bundle extras = intent.getExtras();
        if (data != null) {
            handleAction(data);
        } else if (extras != null) {
            HashMap hashMap = new HashMap();
            for (String str : extras.keySet()) {
                Object obj = extras.get(str);
                if (!TextUtils.isEmpty(str) && obj != null) {
                    hashMap.put(str, obj.toString());
                }
            }
            showPushAlertMessage(new PushMessageInfo(hashMap), false);
            intent.replaceExtras(new Bundle());
        }
        intent.setData(null);
    }

    private void handleBranchIntent() {
        if (UserManager.getInstance().loadReferralLink() == null || UserManager.getInstance().loadReferralLink().equals("")) {
            return;
        }
        grantReferralReward(UserManager.getInstance().loadReferralLink());
        UserManager.getInstance().clearReferralLink(this);
    }

    private void grantReferralReward(String referralLink) {
        WebServiceCalls.grantReferralReward(this, referralLink, this);
    }

    @Override // com.sts.ryde.ui.BaseActivity
    public void hideLoading() {
        if (this.isGoToTripDetails) {
            return;
        }
        super.hideLoading();
    }

    private void openInsuranceDeeLink() {
        if (!this.currentUser.countryCode.equals(AppConstant.CountryCode.NUMBER_SINGAPORE)) {
            this.dialogAlert = DialogManager.showDialogAlert(this, getString(R.string.registered_sg_mobile), null, true);
        } else {
            v5openInsuranceFragment();
        }
    }

    private void openCreditsDeepLink() {
        openRydeCredits();
    }

    private void openFwdDeepLink() {
        openFwdInsuranceFragment();
    }

    private void openRydePayDeepLink() {
        openPaymentOptions();
    }

    private void openSettingsDeepLink(String memberType) {
        if (!TextUtils.isEmpty(memberType)) {
            memberType.hashCode();
            if (memberType.equals("2")) {
                this.dialogAlert = DialogManager.showDialogAlert(this, getString(R.string.log_in_as_driver), null, true);
                return;
            }
        }
        v5openSettings(this.location);
    }

    private void openTripListDeeplink(boolean openPastTrips, boolean openBusinessTrips) {
        openTripList(Boolean.valueOf(openPastTrips), openBusinessTrips);
    }

    public void handleAction(Uri uri) {
        String queryParameter = uri.getQueryParameter("action");
        String queryParameter2 = uri.getQueryParameter("start_address");
        String queryParameter3 = uri.getQueryParameter("start_latitude");
        String queryParameter4 = uri.getQueryParameter("start_longitude");
        String queryParameter5 = uri.getQueryParameter("end_address");
        String queryParameter6 = uri.getQueryParameter("end_latitude");
        String queryParameter7 = uri.getQueryParameter("end_longitude");
        String queryParameter8 = uri.getQueryParameter("source");
        String queryParameter9 = uri.getQueryParameter("referral_param");
        String queryParameter10 = uri.getQueryParameter("price_type");
        String queryParameter11 = uri.getQueryParameter("memberType");
        if (queryParameter8 != null) {
            handleAction(queryParameter, queryParameter2, queryParameter3, queryParameter4, queryParameter5, queryParameter6, queryParameter7, queryParameter8, queryParameter9, queryParameter10, queryParameter11);
        } else if (queryParameter2 == null && queryParameter5 == null) {
            handleAction(uri.getQueryParameter("action"), uri.getQueryParameter("startLocationAddress"), uri.getQueryParameter("startLocationLat"), uri.getQueryParameter("startLocationLong"), uri.getQueryParameter("stopLocationAddress"), uri.getQueryParameter("stopLocationLat"), uri.getQueryParameter("stopLocationLong"), null, uri.getQueryParameter("referral_param"), null, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAction(String action, String start_address, String start_latitude, String start_longitude, String end_address, String end_latitude, String end_longitude, String sourceId, String referralParam, String type, String memberType) {
        RDLocation rDLocation;
        RDLocation rDLocation2;
        if (TextUtils.isEmpty(action)) {
            return;
        }
        int i = 1;
        if (action.contains("rydesharing.com")) {
            v5openWebViewBack(action.split("title=")[1], Uri.decode(action) + ContainerUtils.FIELD_DELIMITER + ("secret=" + UserManager.getInstance().loadMemberToken()));
            return;
        }
        action.hashCode();
        char c = 65535;
        switch (action.hashCode()) {
            case -1689060648:
                if (action.equals("cheaprrides")) {
                    c = 0;
                    break;
                }
                break;
            case -931760132:
                if (action.equals("pastTrips")) {
                    c = 1;
                    break;
                }
                break;
            case -916418052:
                if (action.equals("rydexl")) {
                    c = 2;
                    break;
                }
                break;
            case -907977868:
                if (action.equals("school")) {
                    c = 3;
                    break;
                }
                break;
            case -799212381:
                if (action.equals("promotion")) {
                    c = 4;
                    break;
                }
                break;
            case -210003559:
                if (action.equals("rydeexec")) {
                    c = 5;
                    break;
                }
                break;
            case -209928196:
                if (action.equals("rydehire")) {
                    c = 6;
                    break;
                }
                break;
            case -209684188:
                if (action.equals("rydepool")) {
                    c = 7;
                    break;
                }
                break;
            case -209604464:
                if (action.equals("rydesend")) {
                    c = '\b';
                    break;
                }
                break;
            case -209578202:
                if (action.equals("rydetaxi")) {
                    c = '\t';
                    break;
                }
                break;
            case 3198785:
                if (action.equals("help")) {
                    c = '\n';
                    break;
                }
                break;
            case 3552798:
                if (action.equals("taxi")) {
                    c = 11;
                    break;
                }
                break;
            case 73049818:
                if (action.equals("insurance")) {
                    c = '\f';
                    break;
                }
                break;
            case 108985456:
                if (action.equals("rydex")) {
                    c = StringUtil.CARRIAGE_RETURN;
                    break;
                }
                break;
            case 109400031:
                if (action.equals("share")) {
                    c = 14;
                    break;
                }
                break;
            case 110546608:
                if (action.equals("topup")) {
                    c = 15;
                    break;
                }
                break;
            case 199747890:
                if (action.equals("upcomingTrips")) {
                    c = 16;
                    break;
                }
                break;
            case 345241144:
                if (action.equals("topupWithVoucher")) {
                    c = 17;
                    break;
                }
                break;
            case 554307056:
                if (action.equals("carpool")) {
                    c = 18;
                    break;
                }
                break;
            case 619854094:
                if (action.equals("businessTrips")) {
                    c = 19;
                    break;
                }
                break;
            case 950441179:
                if (action.equals("savedAddresses")) {
                    c = 20;
                    break;
                }
                break;
            case 1028633754:
                if (action.equals("credits")) {
                    c = 21;
                    break;
                }
                break;
            case 1309346912:
                if (action.equals("subscriptionSettings")) {
                    c = 22;
                    break;
                }
                break;
            case 1434631203:
                if (action.equals("settings")) {
                    c = 23;
                    break;
                }
                break;
            case 1655803552:
                if (action.equals("rydepay")) {
                    c = 24;
                    break;
                }
                break;
            case 1655803671:
                if (action.equals("rydepet")) {
                    c = 25;
                    break;
                }
                break;
            case 1683092674:
                if (action.equals("rideInsurance")) {
                    c = 26;
                    break;
                }
                break;
            case 1836954027:
                if (action.equals("hometowork")) {
                    c = 27;
                    break;
                }
                break;
            case 1946926476:
                if (action.equals("setupBusinessProfile")) {
                    c = 28;
                    break;
                }
                break;
            case 2080387048:
                if (action.equals("rydeflash")) {
                    c = 29;
                    break;
                }
                break;
            case 2119390158:
                if (action.equals("subscriptionDetail")) {
                    c = 30;
                    break;
                }
                break;
        }
        RDLocation rDLocation3 = null;
        switch (c) {
            case 0:
            case 2:
            case 5:
            case 7:
            case '\t':
            case 11:
            case '\r':
            case 18:
            case 25:
            case 29:
                if (TextUtils.isEmpty(start_address) || TextUtils.isEmpty(start_latitude) || TextUtils.isEmpty(start_longitude)) {
                    rDLocation = null;
                } else {
                    rDLocation = new RDLocation();
                    rDLocation.name = start_address;
                    rDLocation.latitude = Double.valueOf(Double.parseDouble(start_latitude));
                    rDLocation.longitude = Double.valueOf(Double.parseDouble(start_longitude));
                }
                if (!TextUtils.isEmpty(end_address) && !TextUtils.isEmpty(end_latitude) && !TextUtils.isEmpty(end_longitude)) {
                    rDLocation3 = new RDLocation();
                    rDLocation3.name = end_address;
                    rDLocation3.latitude = Double.valueOf(Double.parseDouble(end_latitude));
                    rDLocation3.longitude = Double.valueOf(Double.parseDouble(end_longitude));
                }
                if (!TextUtils.isEmpty(type)) {
                    try {
                        i = Integer.parseInt(type);
                    } catch (Exception unused) {
                    }
                }
                if (referralParam != null) {
                    JobRiderRequestModel createRiderJobInfo = JobInstantiate.createRiderJobInfo(action, rDLocation, rDLocation3, i, sourceId, referralParam);
                    if (rDLocation != null && rDLocation3 != null) {
                        v5openRiderTripCreatePage(this.location, createRiderJobInfo);
                        return;
                    } else {
                        onCreateTripClickListener(getActionType(action));
                        return;
                    }
                }
                JobRiderRequestModel createRiderJobInfo2 = JobInstantiate.createRiderJobInfo(action, rDLocation, rDLocation3);
                if (rDLocation != null && rDLocation3 != null) {
                    v5openRiderTripCreatePage(this.location, createRiderJobInfo2);
                    return;
                } else {
                    onCreateTripClickListener(getActionType(action));
                    return;
                }
            case 1:
                openTripListDeeplink(true, false);
                return;
            case 3:
                onRydeSchoolClickListener();
                return;
            case 4:
                onPromotionClickListener();
                return;
            case 6:
                if (TextUtils.isEmpty(start_address) || TextUtils.isEmpty(start_latitude) || TextUtils.isEmpty(start_longitude)) {
                    rDLocation2 = null;
                } else {
                    rDLocation2 = new RDLocation();
                    rDLocation2.name = start_address;
                    rDLocation2.latitude = Double.valueOf(Double.parseDouble(start_latitude));
                    rDLocation2.longitude = Double.valueOf(Double.parseDouble(start_longitude));
                }
                if (rDLocation2 != null) {
                    RDApplication.firebaseLogEvents("start_booking_rydehire");
                    onCreateTripRydeHire(JobInstantiate.createRiderJobInfo(action, rDLocation2, (RDLocation) null));
                    return;
                } else {
                    onCreateTripRydeHire();
                    return;
                }
            case '\b':
                onRydeSendClicked();
                return;
            case '\n':
                onRydeHelpClickListener();
                return;
            case '\f':
                openInsuranceDeeLink();
                return;
            case 14:
                v5openShareScreen();
                return;
            case 15:
                addFragmentUnderActionbar(TopupFragment.INSTANCE.newInstance(false, false), true);
                return;
            case 16:
                openTripListDeeplink(false, false);
                return;
            case 17:
                addFragmentUnderActionbar(TopupVoucherFragment.INSTANCE.newInstance(false), true);
                return;
            case 19:
                openTripListDeeplink(false, true);
                return;
            case 20:
                onSavedAddressClickListener();
                return;
            case 21:
                openCreditsDeepLink();
                return;
            case 22:
                if (UserManager.getInstance().getMemberInfo().isRydePlusOptIn()) {
                    addFragmentFullScreen(SubscriptionSettingsFragment.INSTANCE.newInstance(), true, true);
                    return;
                }
                return;
            case 23:
                openSettingsDeepLink(memberType);
                return;
            case 24:
                openRydePayDeepLink();
                return;
            case 26:
                openFwdDeepLink();
                return;
            case 27:
                onHomeToWorkClickListener();
                return;
            case 28:
                if (UserManager.getInstance().getMemberInfo().businessEmail != null) {
                    addFragmentUnderActionbar(BusinessProfileFragment.INSTANCE.newInstance(), true);
                    return;
                } else {
                    addFragmentUnderActionbar(new BusinessProfileLandingFragment(), true);
                    return;
                }
            case 30:
                onRydePlusClickFragment();
                return;
            default:
                return;
        }
    }

    private int getActionType(String action) {
        action.hashCode();
        char c = 65535;
        switch (action.hashCode()) {
            case -1689060648:
                if (action.equals("cheaprrides")) {
                    c = 0;
                    break;
                }
                break;
            case -916418052:
                if (action.equals("rydexl")) {
                    c = 1;
                    break;
                }
                break;
            case -210003559:
                if (action.equals("rydeexec")) {
                    c = 2;
                    break;
                }
                break;
            case -209684188:
                if (action.equals("rydepool")) {
                    c = 3;
                    break;
                }
                break;
            case -209578202:
                if (action.equals("rydetaxi")) {
                    c = 4;
                    break;
                }
                break;
            case 3552798:
                if (action.equals("taxi")) {
                    c = 5;
                    break;
                }
                break;
            case 108985456:
                if (action.equals("rydex")) {
                    c = 6;
                    break;
                }
                break;
            case 554307056:
                if (action.equals("carpool")) {
                    c = 7;
                    break;
                }
                break;
            case 1655803671:
                if (action.equals("rydepet")) {
                    c = '\b';
                    break;
                }
                break;
            case 2080387048:
                if (action.equals("rydeflash")) {
                    c = '\t';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 7:
            case '\t':
            default:
                return 1;
            case 1:
                return 8;
            case 2:
                return 7;
            case 3:
                return 2;
            case 4:
            case 5:
                return 13;
            case 6:
                return 6;
            case '\b':
                return 5;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 2) {
            if (requestCode == 3) {
                GoogleLocationHelper googleLocationHelper = this.googleLocationHelper;
                if (googleLocationHelper != null) {
                    googleLocationHelper.onStart();
                }
                if (PermissionHelper.haveLocationPermissionNeverAsk(this)) {
                    PermissionHelper.requestLocationPermission(this);
                    return;
                } else {
                    this.alert = DialogManager.showDialogAlertWithTitle(this, getString(R.string.location_permission_denied_title), getString(R.string.location_permission_denied_msg), new View.OnClickListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.29
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            V5HomeActivity.this.alert.dismiss();
                            PermissionHelper.openAppPermissionScreen(V5HomeActivity.this);
                        }
                    }, false);
                    return;
                }
            }
            if (requestCode != 4) {
                if (requestCode == 5 && PermissionHelper.haveLocationPermissionNeverAsk(this)) {
                    PermissionHelper.requestLocationPermission(this);
                    return;
                }
                return;
            }
        }
        Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag(PictureSelectorFragment.class.getName());
        if (findFragmentByTag != null && (findFragmentByTag instanceof PictureSelectorFragment)) {
            ((PictureSelectorFragment) findFragmentByTag).onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        Fragment findFragmentByTag2 = getSupportFragmentManager().findFragmentByTag(com.sts.ryde.v5.utils.ScanQrCodeFragment.class.getName());
        if (findFragmentByTag2 != null && (findFragmentByTag2 instanceof com.sts.ryde.v5.utils.ScanQrCodeFragment)) {
            ((com.sts.ryde.v5.utils.ScanQrCodeFragment) findFragmentByTag2).onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        Fragment findFragmentByTag3 = getSupportFragmentManager().findFragmentByTag(ScanQrCodeFragment.class.getName());
        if (findFragmentByTag3 == null || !(findFragmentByTag3 instanceof ScanQrCodeFragment)) {
            return;
        }
        ((ScanQrCodeFragment) findFragmentByTag3).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = 1.0f;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        ((WindowManager) getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        displayMetrics.scaledDensity = configuration.fontScale * displayMetrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, displayMetrics);
    }

    private void rydeChatFindJobInfo(final String jobId) {
        try {
            RDBaseService.GetTripInfo getTripInfo = (RDBaseService.GetTripInfo) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_TRIP_INFO, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.30
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
                    JobInfo jobInfo;
                    if (result != null) {
                        JobInfoResponseModel jobInfoResponseModel = (JobInfoResponseModel) result;
                        if (!jobInfoResponseModel.status || (jobInfo = jobInfoResponseModel.data) == null) {
                            return;
                        }
                        V5HomeActivity v5HomeActivity = V5HomeActivity.this;
                        SelectTripTypeHelper.tripType(v5HomeActivity, jobInfo, v5HomeActivity.location, true);
                    }
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                    V5HomeActivity.this.hideLoading();
                }
            });
            getTripInfo.setInfo(jobId);
            getTripInfo.connect(this, JobInfoResponseModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUpcomingTripDetail(final int jobId) {
        try {
            RDBaseService.GetTripDetailService getTripDetailService = (RDBaseService.GetTripDetailService) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_NEW_TRIP_DETAIL, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.31
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
                    V5HomeActivity.this.hideLoading();
                    TripDetailModel tripDetailModel = (TripDetailModel) result;
                    tripDetailModel.getResult();
                    V5HomeActivity.this.upcomingTripDetailModel = tripDetailModel;
                    V5HomeActivity.this.openUpcomingReminder();
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                    V5HomeActivity.this.hideLoading();
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                    V5HomeActivity.this.hideLoading();
                }
            });
            getTripDetailService.setUrl(AppConstant.BASE_URL_MS + RDServiceConfig.ServiceConfigName.GET_NEW_TRIP_DETAIL + "/" + String.valueOf(jobId));
            getTripDetailService.setInfo();
            getTripDetailService.connect(this, TripDetailModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1 || data == null) {
            return;
        }
        if (requestCode == 100) {
            WebServiceCalls.requestTripDetail(this, Integer.parseInt(data.getStringExtra("result")), this);
            return;
        }
        if (requestCode == 102) {
            if (data == null || !data.getStringExtra("result").equals("book_again")) {
                return;
            }
            findTripForRydeSendRebook(data.getStringExtra(AppConstant.JOB_ID));
            return;
        }
        if (requestCode != 991) {
            if (requestCode != 997) {
                return;
            }
            TopupSuccessFragment topupSuccessFragment = this.successDialog;
            if (topupSuccessFragment != null) {
                topupSuccessFragment.dismiss();
            }
            closeButtonClicked();
            openRydeCredits();
            return;
        }
        if (resultCode == -1) {
            EventBus.getDefault().post(new Event.PaymentDataEvent(PaymentData.getFromIntent(data)));
        } else {
            if (resultCode != 0) {
                return;
            }
            EventBus.getDefault().post(new Event.PaymentDataEvent(null));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestTripDetailForChat(final String jobId, final boolean redirectRydeChat) {
        try {
            RDBaseService.GetTripDetailService getTripDetailService = (RDBaseService.GetTripDetailService) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_NEW_TRIP_DETAIL, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.32
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object result) {
                    TripDetailModel tripDetailModel = (TripDetailModel) result;
                    TripDetailInfo result2 = tripDetailModel.getResult();
                    if (tripDetailModel.getResult().getStatus() == 7 && tripDetailModel.getResult().getStatus() == 6 && tripDetailModel.getResult().getStatus() == 5) {
                        return;
                    }
                    if (redirectRydeChat) {
                        V5HomeActivity.this.addFragmentFullScreen(TripAcceptedFragment.INSTANCE.newInstance(result2, redirectRydeChat), true);
                    } else {
                        V5HomeActivity.this.addFragmentFullScreen(RydeChatFragment.INSTANCE.newInstance(result2.getDriverInfo().getFirstName(), result2), true);
                    }
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                    V5HomeActivity.this.hideLoading();
                }
            });
            getTripDetailService.setUrl(AppConstant.BASE_URL_MS + RDServiceConfig.ServiceConfigName.GET_NEW_TRIP_DETAIL + "/" + String.valueOf(jobId));
            getTripDetailService.setInfo();
            getTripDetailService.connect(this, TripDetailModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestTrackingLocation(int driver_id) {
        WebServiceCalls.requestTrackingLocation(this, driver_id, this);
    }

    public void showMatchFoundDialog(String jobId) {
        callLastTripForJobAccept(jobId);
    }

    public void realTimeOnDemandTripUpdate(String collection_name, final String document_name) {
        try {
            if (this.tripListener == null) {
                DocumentReference document = RDApplication.fireStoreDb.collection(collection_name).document(document_name);
                this.docRef = document;
                this.tripListener = document.addSnapshotListener(new EventListener() { // from class: com.sts.ryde.v5.home.-$$Lambda$V5HomeActivity$jhADGlKyeKajke9-kFQsUWrVEtQ
                    @Override // com.google.firebase.firestore.EventListener
                    public final void onEvent(Object obj, FirebaseFirestoreException firebaseFirestoreException) {
                        V5HomeActivity.this.lambda$realTimeOnDemandTripUpdate$4$V5HomeActivity(document_name, (DocumentSnapshot) obj, firebaseFirestoreException);
                    }
                });
            }
        } catch (Exception unused) {
        }
    }

    public /* synthetic */ void lambda$realTimeOnDemandTripUpdate$4$V5HomeActivity(String str, DocumentSnapshot documentSnapshot, FirebaseFirestoreException firebaseFirestoreException) {
        if (firebaseFirestoreException == null && documentSnapshot != null && documentSnapshot.exists()) {
            long longValue = ((Long) documentSnapshot.getData().get("status")).longValue();
            EventBus.getDefault().post(new Event.FirestoreODTripEvent(String.valueOf(longValue)));
            if (longValue == 1 || longValue == 21) {
                if (getSupportFragmentManager().findFragmentByTag(TripPendingFragment.class.getName()) instanceof TripPendingFragment) {
                    onBackPressed();
                }
                FirestoreTrip loadFirestoreTrip = RDApplication.loadFirestoreTrip(this);
                if (loadFirestoreTrip != null && loadFirestoreTrip.getJob_id().equals(str) && loadFirestoreTrip.getShowMatchCard()) {
                    callLastTripForJobAccept(str);
                }
            } else if (longValue == 14) {
                callLastTripForJobCanceled(str);
            } else if (longValue == 3) {
                if (getSupportFragmentManager().findFragmentByTag(RydeBoostFragment.class.getName()) instanceof RydeBoostFragment) {
                    onBackPressed();
                }
            } else if (longValue == 7) {
                this.tripListener.remove();
            }
            requestLastTrip(false);
        }
    }

    public void retrieveFirestoreTrip(String collection_name, final String document_name, final Boolean showMatchCard, final Boolean canceledJob) {
        DocumentReference document = RDApplication.fireStoreDb.collection(collection_name).document(document_name);
        this.docRef = document;
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { // from class: com.sts.ryde.v5.home.V5HomeActivity.33
            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    if (result.exists()) {
                        long longValue = ((Long) result.getData().get("status")).longValue();
                        if (RDApplication.loadFirestoreTrip(V5HomeActivity.this).getStatus() != 0) {
                            if (RDApplication.loadFirestoreTrip(V5HomeActivity.this).getStatus() == 1 || RDApplication.loadFirestoreTrip(V5HomeActivity.this).getStatus() == 21) {
                                if (longValue == 14) {
                                    if (canceledJob.booleanValue()) {
                                        return;
                                    }
                                    V5HomeActivity.this.callLastTripForJobCanceled(document_name);
                                    return;
                                } else {
                                    if (RDApplication.loadFirestoreTrip(V5HomeActivity.this).getStatus() == 13 && longValue == 7) {
                                        RDApplication.saveFirestoreTripInfo(V5HomeActivity.this, null);
                                        return;
                                    }
                                    return;
                                }
                            }
                            return;
                        }
                        if (longValue == 1 || longValue == 21) {
                            if (showMatchCard.booleanValue()) {
                                V5HomeActivity.this.callLastTripForJobAccept(document_name);
                            }
                        } else if (longValue == 14) {
                            if (canceledJob.booleanValue()) {
                                return;
                            }
                            V5HomeActivity.this.callLastTripForJobCanceled(document_name);
                        } else if (longValue == 7) {
                            RDApplication.saveFirestoreTripInfo(V5HomeActivity.this, null);
                        }
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFirestoreTripEvent(Event.FirestoreTripEvent event) {
        ListenerRegistration listenerRegistration = this.tripListener;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            this.tripListener = null;
        }
        realTimeOnDemandTripUpdate(AppConstant.FIRESTORE_TRIPS, event.getJobId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusinessReminderEvent(Event.BusinessReminderEvent event) {
        if (event.isHasBusinessEmail()) {
            getNextBusinessReminder();
        } else {
            WorkManager.getInstance(this).cancelAllWork();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshLoadFound(Event.RefreshLostFound event) {
        WebServiceCalls.getLostItemsUnread(this, this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartBoostedTimer(Event.StartBoostedTimer event) {
        UserManager.getInstance().saveBoostId(event.getJobId());
        addRydeBoostReminder(event.getJobId(), Long.valueOf(event.getTimestamp()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshPendingTrips(Event.RefreshPendingTrip event) {
        requestLastTrip(false);
    }

    private void findTripForRydeBoost(String tripId) {
        if (UserManager.getInstance().loadBoostId().equals(tripId)) {
            UserManager.getInstance().clearBoostId(this);
            RDBaseService.GetTripInfo getTripInfo = (RDBaseService.GetTripInfo) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_TRIP_INFO, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.34
                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
                }

                @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
                public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object objectResult) {
                    RDLocation rDLocation;
                    if (objectResult != null) {
                        JobInfoResponseModel jobInfoResponseModel = (JobInfoResponseModel) objectResult;
                        if (jobInfoResponseModel.status) {
                            JobInfo jobInfo = jobInfoResponseModel.data;
                            RDLocation rDLocation2 = new RDLocation();
                            RDLocation rDLocation3 = new RDLocation();
                            ArrayList arrayList = new ArrayList();
                            rDLocation2.latitude = Double.valueOf(Double.parseDouble(jobInfo.rideStartLocationLatitude));
                            rDLocation2.longitude = Double.valueOf(Double.parseDouble(jobInfo.rideStartLocationLongitude));
                            ArrayList<AdditionalStopAddress> arrayList2 = jobInfo.additionalStops;
                            RDLocation rDLocation4 = null;
                            if (arrayList2 != null && arrayList2.size() > 0) {
                                if (arrayList2.size() >= 2) {
                                    rDLocation4 = new RDLocation();
                                    rDLocation = new RDLocation();
                                    rDLocation3.latitude = Double.valueOf(arrayList2.get(0).latitude);
                                    rDLocation3.longitude = Double.valueOf(arrayList2.get(0).longitude);
                                    rDLocation4.latitude = Double.valueOf(arrayList2.get(1).latitude);
                                    rDLocation4.longitude = Double.valueOf(arrayList2.get(1).longitude);
                                    rDLocation.latitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLatitude));
                                    rDLocation.longitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLongitude));
                                } else {
                                    RDLocation rDLocation5 = new RDLocation();
                                    rDLocation3.latitude = Double.valueOf(arrayList2.get(0).latitude);
                                    rDLocation3.longitude = Double.valueOf(arrayList2.get(0).longitude);
                                    rDLocation5.latitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLatitude));
                                    rDLocation5.longitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLongitude));
                                    rDLocation = null;
                                    rDLocation4 = rDLocation5;
                                }
                                for (int i = 0; i < jobInfo.additionalStops.size(); i++) {
                                    RDLocation rDLocation6 = new RDLocation();
                                    rDLocation6.latitude = Double.valueOf(jobInfo.additionalStops.get(i).latitude);
                                    rDLocation6.longitude = Double.valueOf(jobInfo.additionalStops.get(i).longitude);
                                    arrayList.add(rDLocation6);
                                }
                            } else {
                                rDLocation3.latitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLatitude));
                                rDLocation3.longitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLongitude));
                                rDLocation = null;
                            }
                            RDLocation rDLocation7 = new RDLocation();
                            rDLocation7.latitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLatitude));
                            rDLocation7.longitude = Double.valueOf(Double.parseDouble(jobInfo.rideStopLocationLongitude));
                            int i2 = jobInfo.priceType;
                            if (jobInfo.status.equals(String.valueOf(0))) {
                                if (jobInfo.priceType == 9 || jobInfo.priceType == 10) {
                                    V5HomeActivity.this.getBoostedPriceRydeSend(jobInfo, new long[]{System.currentTimeMillis() / 1000}, rDLocation2, rDLocation7, arrayList, new int[]{9}, Integer.parseInt(jobInfo.id), null);
                                } else {
                                    V5HomeActivity v5HomeActivity = V5HomeActivity.this;
                                    v5HomeActivity.getBoostedPrice(jobInfo, v5HomeActivity.getTime(), rDLocation2, rDLocation3, rDLocation4, rDLocation, new int[0], Integer.parseInt(jobInfo.id), null);
                                }
                            }
                        }
                    }
                }
            });
            getTripInfo.setInfo(tripId);
            getTripInfo.connect(this, JobInfoResponseModel.class);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getBoostedPrice(final JobInfo job_info, long[] time, RDLocation start, RDLocation end, RDLocation end2, RDLocation end3, int[] type, int tripId, String priceId) {
        RDBaseService.GetNewDynamicPrice getNewDynamicPrice = (RDBaseService.GetNewDynamicPrice) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_NEW_DYNAMIC_PRICE, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.35
            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceError(RDServiceConfig.ServiceConfigType type2, Object error, boolean isException) {
            }

            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type2, Object error) {
            }

            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type2, Object objectResult) {
                if (objectResult != null) {
                    NewPricingResponseModel newPricingResponseModel = (NewPricingResponseModel) objectResult;
                    if (newPricingResponseModel.status) {
                        V5HomeActivity.this.addFragmentFullScreen(RydeBoostFragment.INSTANCE.newInstance(job_info, newPricingResponseModel.result), true, true);
                    }
                }
            }
        });
        getNewDynamicPrice.setInfo(time, start, end, end2, end3, type, tripId, priceId);
        getNewDynamicPrice.connect(this, NewPricingResponseModel.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getBoostedPriceRydeSend(final JobInfo job_info, long[] time, RDLocation start, RDLocation stop, ArrayList<RDLocation> additional_stops, int[] type, int tripId, String priceId) {
        RDBaseService.GetNewDynamicPrice getNewDynamicPrice = (RDBaseService.GetNewDynamicPrice) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.PRICING_RYDE_SEND, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.36
            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceError(RDServiceConfig.ServiceConfigType type2, Object error, boolean isException) {
            }

            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type2, Object error) {
            }

            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type2, Object objectResult) {
                if (objectResult != null) {
                    NewPricingResponseModel newPricingResponseModel = (NewPricingResponseModel) objectResult;
                    if (newPricingResponseModel.status) {
                        V5HomeActivity.this.addFragmentFullScreen(RydeBoostFragment.INSTANCE.newInstance(job_info, newPricingResponseModel.result), true, true);
                    }
                }
            }
        });
        getNewDynamicPrice.setInfo(time, start, stop, additional_stops, type, tripId, priceId);
        getNewDynamicPrice.connect(this, NewPricingResponseModel.class);
    }

    private void authorizeFirebaseToken() {
        WebServiceCalls.authFirebaseToken(this, this);
    }

    private void signInFirebaseWithCustomToken(String token) {
        FirebaseAuth.getInstance(RDApplication.secondaryFirebaseApp).signInWithCustomToken(token).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() { // from class: com.sts.ryde.v5.home.V5HomeActivity.38
            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    return;
                }
                Branch.getInstance().logout();
                UserManager.getInstance().cleanLoginData(V5HomeActivity.this);
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(AppData.getInstance().currentActivity, (Class<?>) V5LoginActivity.class);
                intent.setFlags(268468224);
                AppData.getInstance().currentActivity.startActivityWithAnimation(intent);
                AppData.getInstance().currentActivity.finish();
            }
        }).addOnFailureListener(this, new OnFailureListener() { // from class: com.sts.ryde.v5.home.V5HomeActivity.37
            @Override // com.google.android.gms.tasks.OnFailureListener
            public void onFailure(Exception e) {
                Branch.getInstance().logout();
                UserManager.getInstance().cleanLoginData(V5HomeActivity.this);
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(AppData.getInstance().currentActivity, (Class<?>) V5LoginActivity.class);
                intent.setFlags(268468224);
                AppData.getInstance().currentActivity.startActivityWithAnimation(intent);
                AppData.getInstance().currentActivity.finish();
            }
        });
    }

    public void showRydeSendDriverCancelled(String jobId) {
        RydeSendDriverCancelledDialog rydeSendDriverCancelledDialog = this.rydeSendDriverCancelledDialog;
        if (rydeSendDriverCancelledDialog != null) {
            rydeSendDriverCancelledDialog.dismiss();
        }
        this.rydeSendDriverCancelledDialog = DialogManager.showRydeSendDriverCancelledDialog(this, new RydeSendDriverCancelledDialog.IRydeDriverCancelInterface() { // from class: com.sts.ryde.v5.home.V5HomeActivity.39
            @Override // com.sts.ryde.v5.kotlin.dialog.rydesend.RydeSendDriverCancelledDialog.IRydeDriverCancelInterface
            public void onBookAgain(String jobId2) {
                V5HomeActivity.this.showLoading();
                V5HomeActivity.this.findTripForRydeSendRebook(jobId2);
            }
        }, jobId);
    }

    public void showRydeSendTripExpired(String jobId) {
        RydeSendTripExpiredDialog rydeSendTripExpiredDialog = this.rydeSendTripExpiredDialog;
        if (rydeSendTripExpiredDialog != null) {
            rydeSendTripExpiredDialog.dismiss();
        }
        this.rydeSendTripExpiredDialog = DialogManager.showRydeSendExpiredDialog(this, new RydeSendTripExpiredDialog.IRydeSendExpiredInterface() { // from class: com.sts.ryde.v5.home.V5HomeActivity.40
            @Override // com.sts.ryde.v5.kotlin.dialog.rydesend.RydeSendTripExpiredDialog.IRydeSendExpiredInterface
            public void onBookAgain(String jobId2) {
                V5HomeActivity.this.showLoading();
                V5HomeActivity.this.findTripForRydeSendRebook(jobId2);
            }
        }, jobId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRebookRydeSendEvent(Event.RebookRydeSendEvent event) {
        findTripForRydeSendRebook(String.valueOf(event.getJobId()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void findTripForRydeSendRebook(String tripId) {
        RDBaseService.GetTripInfo getTripInfo = (RDBaseService.GetTripInfo) RDServiceHelper.getServiceByType(RDServiceConfig.ServiceConfigType.GET_TRIP_INFO, new RDBaseService.IBaseService() { // from class: com.sts.ryde.v5.home.V5HomeActivity.41
            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceOfflineMode(RDServiceConfig.ServiceConfigType type, Object error) {
            }

            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceSuccess(RDServiceConfig.ServiceConfigType type, Object objectResult) {
                V5HomeActivity.this.hideLoading();
                if (objectResult != null) {
                    JobInfoResponseModel jobInfoResponseModel = (JobInfoResponseModel) objectResult;
                    if (jobInfoResponseModel.status) {
                        JobInfo jobInfo = jobInfoResponseModel.data;
                        if (jobInfo.priceType == 10 || jobInfo.priceType == 9) {
                            V5HomeActivity.this.openRiderEditRydeSendPage(jobInfo);
                        }
                    }
                }
            }

            @Override // com.sts.ryde.proxy.RDBaseService.IBaseService
            public void onWebServiceError(RDServiceConfig.ServiceConfigType type, Object error, boolean isException) {
                V5HomeActivity.this.hideLoading();
            }
        });
        getTripInfo.setInfo(tripId);
        getTripInfo.connect(this, JobInfoResponseModel.class);
    }

    private void getNextBusinessReminder() {
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        new SimpleDateFormat("dd");
        try {
            Date parse = simpleDateFormat.parse(format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(2, 1);
            calendar.set(5, 1);
            calendar.set(11, 10);
            calendar.set(12, 0);
            Date time = calendar.getTime();
            if (parse.before(time)) {
                scheduleNotification(Long.valueOf(time.getTime() - parse.getTime()), "BUSINESS_REMINDER", "Remember to tag your business trips by today for them to be included in your upcoming monthly statement. Happy claiming!");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotification(Long timeDelay, String tag, String body) {
        Data.Builder putString = new Data.Builder().putString("body", body);
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);
        WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(NotificationSchedule.class).setInitialDelay(timeDelay.longValue(), TimeUnit.MILLISECONDS).setConstraints(new Constraints.Builder().setTriggerContentMaxDelay(timeDelay.longValue(), TimeUnit.MILLISECONDS).build()).setInputData(putString.build()).addTag(tag).build());
    }
}
