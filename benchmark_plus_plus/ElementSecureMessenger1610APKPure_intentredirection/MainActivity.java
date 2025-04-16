package im.vector.app.features;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import androidx.appcompat.widget.ActivityChooserModel;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStateManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwnerKt;
import com.airbnb.mvrx.ActivityViewModelContext;
import com.airbnb.mvrx.Mavericks;
import com.airbnb.mvrx.MavericksExtensionsKt$activityViewModel$1$$ExternalSyntheticOutline0;
import com.airbnb.mvrx.MavericksView;
import com.airbnb.mvrx.MavericksViewModelProvider;
import com.airbnb.mvrx.lifecycleAwareLazy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import dagger.hilt.android.AndroidEntryPoint;
import im.vector.app.R;
import im.vector.app.core.extensions.SessionKt;
import im.vector.app.databinding.ActivityMainBinding;
import im.vector.app.features.analytics.VectorAnalytics;
import im.vector.app.features.home.ShortcutsHandler;
import im.vector.app.features.notifications.NotificationDrawerManager;
import im.vector.app.features.pin.UnlockedActivity;
import im.vector.app.features.pin.lockscreen.crypto.LockScreenKeyRepository;
import im.vector.app.features.pin.lockscreen.pincode.PinCodeHelper;
import im.vector.app.features.popup.PopupAlertManager;
import im.vector.app.features.session.VectorSessionStore;
import im.vector.app.features.start.StartAppAction;
import im.vector.app.features.start.StartAppAndroidService;
import im.vector.app.features.start.StartAppViewEvent;
import im.vector.app.features.start.StartAppViewModel;
import im.vector.app.features.start.StartAppViewState;
import im.vector.app.features.themes.ActivityOtherThemes;
import im.vector.app.features.ui.UiStateRepository;
import io.sentry.cache.EnvelopeCache;
import javax.inject.Inject;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.matrix.android.sdk.api.failure.GlobalError;
import org.matrix.android.sdk.api.session.Session;
import timber.log.Timber;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000¨\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 ^2\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003:\u0001^B\u0005¢\u0006\u0002\u0010\u0004J\b\u00107\u001a\u000208H\u0002J\u0018\u00109\u001a\u0002082\u0006\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020=H\u0002J\b\u0010>\u001a\u000208H\u0002J!\u0010?\u001a\u0002082\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020=H\u0082@ø\u0001\u0000¢\u0006\u0002\u0010CJ\b\u0010D\u001a\u00020\u0002H\u0016J\b\u0010E\u001a\u00020FH\u0016J\b\u0010G\u001a\u000208H\u0002J\u0010\u0010H\u001a\u0002082\u0006\u0010I\u001a\u00020JH\u0014J\b\u0010K\u001a\u000208H\u0002J\u0010\u0010L\u001a\u0002082\u0006\u0010M\u001a\u00020NH\u0002J\u0012\u0010O\u001a\u0002082\b\u0010P\u001a\u0004\u0018\u00010QH\u0014J\b\u0010R\u001a\u00020\u0006H\u0002J\u0010\u0010S\u001a\u0002082\u0006\u0010T\u001a\u00020UH\u0002J \u0010V\u001a\u0002082\u0006\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020=2\u0006\u0010W\u001a\u00020AH\u0002J\u0012\u0010X\u001a\u0002082\b\u0010Y\u001a\u0004\u0018\u00010ZH\u0002J\u0012\u0010[\u001a\u0002082\b\b\u0002\u0010\\\u001a\u00020AH\u0002J\b\u0010]\u001a\u000208H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.¢\u0006\u0002\n\u0000R\u001e\u0010\u0007\u001a\u00020\b8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001e\u0010\r\u001a\u00020\u000e8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001e\u0010\u0013\u001a\u00020\u00148\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001e\u0010\u0019\u001a\u00020\u001a8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001e\u0010\u001f\u001a\u00020 8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001b\u0010%\u001a\u00020&8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b)\u0010*\u001a\u0004\b'\u0010(R\u001e\u0010+\u001a\u00020,8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u001e\u00101\u001a\u0002028\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006_"}, d2 = {"Lim/vector/app/features/MainActivity;", "Lim/vector/app/core/platform/VectorBaseActivity;", "Lim/vector/app/databinding/ActivityMainBinding;", "Lim/vector/app/features/pin/UnlockedActivity;", "()V", "args", "Lim/vector/app/features/MainActivityArgs;", "lockScreenKeyRepository", "Lim/vector/app/features/pin/lockscreen/crypto/LockScreenKeyRepository;", "getLockScreenKeyRepository", "()Lim/vector/app/features/pin/lockscreen/crypto/LockScreenKeyRepository;", "setLockScreenKeyRepository", "(Lim/vector/app/features/pin/lockscreen/crypto/LockScreenKeyRepository;)V", "notificationDrawerManager", "Lim/vector/app/features/notifications/NotificationDrawerManager;", "getNotificationDrawerManager", "()Lim/vector/app/features/notifications/NotificationDrawerManager;", "setNotificationDrawerManager", "(Lim/vector/app/features/notifications/NotificationDrawerManager;)V", "pinCodeHelper", "Lim/vector/app/features/pin/lockscreen/pincode/PinCodeHelper;", "getPinCodeHelper", "()Lim/vector/app/features/pin/lockscreen/pincode/PinCodeHelper;", "setPinCodeHelper", "(Lim/vector/app/features/pin/lockscreen/pincode/PinCodeHelper;)V", "popupAlertManager", "Lim/vector/app/features/popup/PopupAlertManager;", "getPopupAlertManager", "()Lim/vector/app/features/popup/PopupAlertManager;", "setPopupAlertManager", "(Lim/vector/app/features/popup/PopupAlertManager;)V", "shortcutsHandler", "Lim/vector/app/features/home/ShortcutsHandler;", "getShortcutsHandler", "()Lim/vector/app/features/home/ShortcutsHandler;", "setShortcutsHandler", "(Lim/vector/app/features/home/ShortcutsHandler;)V", "startAppViewModel", "Lim/vector/app/features/start/StartAppViewModel;", "getStartAppViewModel", "()Lim/vector/app/features/start/StartAppViewModel;", "startAppViewModel$delegate", "Lkotlin/Lazy;", "uiStateRepository", "Lim/vector/app/features/ui/UiStateRepository;", "getUiStateRepository", "()Lim/vector/app/features/ui/UiStateRepository;", "setUiStateRepository", "(Lim/vector/app/features/ui/UiStateRepository;)V", "vectorAnalytics", "Lim/vector/app/features/analytics/VectorAnalytics;", "getVectorAnalytics", "()Lim/vector/app/features/analytics/VectorAnalytics;", "setVectorAnalytics", "(Lim/vector/app/features/analytics/VectorAnalytics;)V", "clearNotifications", "", "displaySignOutFailedDialog", EnvelopeCache.PREFIX_CURRENT_SESSION_FILE, "Lorg/matrix/android/sdk/api/session/Session;", "onboardingStore", "Lim/vector/app/features/session/VectorSessionStore;", "doCleanUp", "doLocalCleanup", "clearPreferences", "", "vectorSessionStore", "(ZLim/vector/app/features/session/VectorSessionStore;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBinding", "getOtherThemes", "Lim/vector/app/features/themes/ActivityOtherThemes$Launcher;", "handleAppStarted", "handleInvalidToken", "globalError", "Lorg/matrix/android/sdk/api/failure/GlobalError$InvalidToken;", "handleStartForegroundService", "handleViewEvents", "event", "Lim/vector/app/features/start/StartAppViewEvent;", "onCreate", FragmentStateManager.SAVED_INSTANCE_STATE_KEY, "Landroid/os/Bundle;", "parseArgs", "renderState", "state", "Lim/vector/app/features/start/StartAppViewState;", "signout", "ignoreServerError", "startIntentAndFinish", "intent", "Landroid/content/Intent;", "startNextActivityAndFinish", "ignoreClearCredentials", "startSyncing", "Companion", "vector_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
@AndroidEntryPoint
@SourceDebugExtension({"SMAP\nMainActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainActivity.kt\nim/vector/app/features/MainActivity\n+ 2 MavericksExtensions.kt\ncom/airbnb/mvrx/MavericksExtensionsKt\n+ 3 View.kt\nandroidx/core/view/ViewKt\n+ 4 Compat.kt\nim/vector/lib/core/utils/compat/CompatKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,384:1\n212#2,11:385\n262#3,2:396\n27#4,4:398\n27#4,4:402\n1#5:406\n*S KotlinDebug\n*F\n+ 1 MainActivity.kt\nim/vector/app/features/MainActivity\n*L\n120#1:385,11\n155#1:396,2\n188#1:398,4\n232#1:402,4\n*E\n"})
/* loaded from: classes5.dex */
public final class MainActivity extends Hilt_MainActivity<ActivityMainBinding> implements UnlockedActivity {

    @NotNull
    private static final String ACTION_ROOM_DETAILS_FROM_SHORTCUT = "ROOM_DETAILS_FROM_SHORTCUT";

    /* renamed from: Companion, reason: from kotlin metadata */
    @NotNull
    public static final Companion INSTANCE = new Companion(null);

    @NotNull
    private static final String EXTRA_ARGS = "EXTRA_ARGS";

    @NotNull
    private static final String EXTRA_INIT_SESSION = "EXTRA_INIT_SESSION";

    @NotNull
    private static final String EXTRA_NEXT_INTENT = "EXTRA_NEXT_INTENT";

    @NotNull
    private static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    private MainActivityArgs args;

    @Inject
    public LockScreenKeyRepository lockScreenKeyRepository;

    @Inject
    public NotificationDrawerManager notificationDrawerManager;

    @Inject
    public PinCodeHelper pinCodeHelper;

    @Inject
    public PopupAlertManager popupAlertManager;

    @Inject
    public ShortcutsHandler shortcutsHandler;

    /* renamed from: startAppViewModel$delegate, reason: from kotlin metadata */
    @NotNull
    private final Lazy startAppViewModel;

    @Inject
    public UiStateRepository uiStateRepository;

    @Inject
    public VectorAnalytics vectorAnalytics;

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\nJ\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u0014J\u0016\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0017"}, d2 = {"Lim/vector/app/features/MainActivity$Companion;", "", "()V", "ACTION_ROOM_DETAILS_FROM_SHORTCUT", "", MainActivity.EXTRA_ARGS, MainActivity.EXTRA_INIT_SESSION, MainActivity.EXTRA_NEXT_INTENT, MainActivity.EXTRA_ROOM_ID, "getIntentToInitSession", "Landroid/content/Intent;", ActivityChooserModel.ATTRIBUTE_ACTIVITY, "Landroid/app/Activity;", "getIntentWithNextIntent", "context", "Landroid/content/Context;", "nextIntent", "restartApp", "", "args", "Lim/vector/app/features/MainActivityArgs;", "shortcutIntent", "roomId", "vector_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @NotNull
        public final Intent getIntentToInitSession(@NotNull Activity activity) {
            Intrinsics.checkNotNullParameter(activity, "activity");
            Intent intent = new Intent(activity, (Class<?>) MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_INIT_SESSION, true);
            return intent;
        }

        @NotNull
        public final Intent getIntentWithNextIntent(@NotNull Context context, @NotNull Intent nextIntent) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(nextIntent, "nextIntent");
            Intent intent = new Intent(context, (Class<?>) MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_NEXT_INTENT, nextIntent);
            return intent;
        }

        public final void restartApp(@NotNull Activity activity, @NotNull MainActivityArgs args) {
            Intrinsics.checkNotNullParameter(activity, "activity");
            Intrinsics.checkNotNullParameter(args, "args");
            Intent intent = new Intent(activity, (Class<?>) MainActivity.class);
            intent.addFlags(268468224);
            intent.putExtra(MainActivity.EXTRA_ARGS, args);
            activity.startActivity(intent);
        }

        @NotNull
        public final Intent shortcutIntent(@NotNull Context context, @NotNull String roomId) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(roomId, "roomId");
            Intent intent = new Intent(context, (Class<?>) MainActivity.class);
            intent.setAction(MainActivity.ACTION_ROOM_DETAILS_FROM_SHORTCUT);
            intent.putExtra(MainActivity.EXTRA_ROOM_ID, roomId);
            return intent;
        }

        private Companion() {
        }
    }

    public MainActivity() {
        final KClass orCreateKotlinClass = Reflection.getOrCreateKotlinClass(StartAppViewModel.class);
        this.startAppViewModel = new lifecycleAwareLazy(this, null, new Function0<StartAppViewModel>() { // from class: im.vector.app.features.MainActivity$special$$inlined$viewModel$default$1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            /* JADX WARN: Type inference failed for: r0v1, types: [com.airbnb.mvrx.MavericksViewModel, im.vector.app.features.start.StartAppViewModel] */
            @Override // kotlin.jvm.functions.Function0
            @NotNull
            public final StartAppViewModel invoke() {
                MavericksViewModelProvider mavericksViewModelProvider = MavericksViewModelProvider.INSTANCE;
                Class javaClass = JvmClassMappingKt.getJavaClass(KClass.this);
                ComponentActivity componentActivity = this;
                Bundle extras = componentActivity.getIntent().getExtras();
                return MavericksViewModelProvider.get$default(mavericksViewModelProvider, javaClass, StartAppViewState.class, new ActivityViewModelContext(componentActivity, extras != null ? extras.get(Mavericks.KEY_ARG) : null, null, null, 12, null), MavericksExtensionsKt$activityViewModel$1$$ExternalSyntheticOutline0.m(orCreateKotlinClass, "viewModelClass.java.name"), false, null, 48, null);
            }
        }, 2, null);
    }

    private final void clearNotifications() {
        getNotificationDrawerManager().clearAllEvents();
        getShortcutsHandler().clearShortcuts();
        getPopupAlertManager().cancelAll();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void displaySignOutFailedDialog(final Session session, final VectorSessionStore onboardingStore) {
        if (getLifecycle().getState().isAtLeast(Lifecycle.State.RESUMED)) {
            new MaterialAlertDialogBuilder(this, 0).setTitle(R.string.dialog_title_error).setMessage(R.string.sign_out_failed_dialog_message).setPositiveButton(R.string.sign_out_anyway, new DialogInterface.OnClickListener() { // from class: im.vector.app.features.MainActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.displaySignOutFailedDialog$lambda$0(MainActivity.this, session, onboardingStore, dialogInterface, i);
                }
            }).setNeutralButton(R.string.global_retry, new DialogInterface.OnClickListener() { // from class: im.vector.app.features.MainActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.displaySignOutFailedDialog$lambda$1(MainActivity.this, session, onboardingStore, dialogInterface, i);
                }
            }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() { // from class: im.vector.app.features.MainActivity$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.displaySignOutFailedDialog$lambda$2(MainActivity.this, dialogInterface, i);
                }
            }).setCancelable(false).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void displaySignOutFailedDialog$lambda$0(MainActivity this$0, Session session, VectorSessionStore onboardingStore, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(session, "$session");
        Intrinsics.checkNotNullParameter(onboardingStore, "$onboardingStore");
        this$0.signout(session, onboardingStore, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void displaySignOutFailedDialog$lambda$1(MainActivity this$0, Session session, VectorSessionStore onboardingStore, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(session, "$session");
        Intrinsics.checkNotNullParameter(onboardingStore, "$onboardingStore");
        this$0.signout(session, onboardingStore, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void displaySignOutFailedDialog$lambda$2(MainActivity this$0, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.startNextActivityAndFinish(true);
    }

    private final void doCleanUp() {
        Session safeActiveSession = getActiveSessionHolder().getSafeActiveSession();
        if (safeActiveSession == null) {
            startNextActivityAndFinish$default(this, false, 1, null);
            return;
        }
        VectorSessionStore vectorStore = SessionKt.vectorStore(safeActiveSession, this);
        MainActivityArgs mainActivityArgs = this.args;
        if (mainActivityArgs == null) {
            Intrinsics.throwUninitializedPropertyAccessException("args");
            mainActivityArgs = null;
        }
        if (mainActivityArgs.isAccountDeactivated()) {
            BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new MainActivity$doCleanUp$1(this, vectorStore, null), 3, null);
            return;
        }
        MainActivityArgs mainActivityArgs2 = this.args;
        if (mainActivityArgs2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("args");
            mainActivityArgs2 = null;
        }
        if (mainActivityArgs2.getClearCredentials()) {
            signout(safeActiveSession, vectorStore, false);
            return;
        }
        MainActivityArgs mainActivityArgs3 = this.args;
        if (mainActivityArgs3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("args");
            mainActivityArgs3 = null;
        }
        if (mainActivityArgs3.getClearCache()) {
            BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new MainActivity$doCleanUp$2(safeActiveSession, this, vectorStore, null), 3, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:22:0x00cd A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00af A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00b0  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x009f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0025  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object doLocalCleanup(boolean r10, im.vector.app.features.session.VectorSessionStore r11, kotlin.coroutines.Continuation<? super kotlin.Unit> r12) {
        /*
            r9 = this;
            boolean r0 = r12 instanceof im.vector.app.features.MainActivity$doLocalCleanup$1
            if (r0 == 0) goto L13
            r0 = r12
            im.vector.app.features.MainActivity$doLocalCleanup$1 r0 = (im.vector.app.features.MainActivity$doLocalCleanup$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L13
            int r1 = r1 - r2
            r0.label = r1
            goto L18
        L13:
            im.vector.app.features.MainActivity$doLocalCleanup$1 r0 = new im.vector.app.features.MainActivity$doLocalCleanup$1
            r0.<init>(r9, r12)
        L18:
            java.lang.Object r12 = r0.result
            kotlin.coroutines.intrinsics.CoroutineSingletons r1 = kotlin.coroutines.intrinsics.CoroutineSingletons.COROUTINE_SUSPENDED
            int r2 = r0.label
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            r7 = 0
            if (r2 == 0) goto L5c
            if (r2 == r6) goto L4f
            if (r2 == r5) goto L43
            if (r2 == r4) goto L3a
            if (r2 != r3) goto L32
            kotlin.ResultKt.throwOnFailure(r12)
            goto Lce
        L32:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r11 = "call to 'resume' before 'invoke' with coroutine"
            r10.<init>(r11)
            throw r10
        L3a:
            java.lang.Object r10 = r0.L$0
            im.vector.app.features.MainActivity r10 = (im.vector.app.features.MainActivity) r10
            kotlin.ResultKt.throwOnFailure(r12)
            goto Lb1
        L43:
            java.lang.Object r10 = r0.L$1
            im.vector.app.features.session.VectorSessionStore r10 = (im.vector.app.features.session.VectorSessionStore) r10
            java.lang.Object r11 = r0.L$0
            im.vector.app.features.MainActivity r11 = (im.vector.app.features.MainActivity) r11
            kotlin.ResultKt.throwOnFailure(r12)
            goto La3
        L4f:
            java.lang.Object r10 = r0.L$1
            r11 = r10
            im.vector.app.features.session.VectorSessionStore r11 = (im.vector.app.features.session.VectorSessionStore) r11
            java.lang.Object r10 = r0.L$0
            im.vector.app.features.MainActivity r10 = (im.vector.app.features.MainActivity) r10
            kotlin.ResultKt.throwOnFailure(r12)
            goto L8f
        L5c:
            kotlin.ResultKt.throwOnFailure(r12)
            com.bumptech.glide.Glide r12 = com.bumptech.glide.Glide.get(r9)
            r12.clearMemory()
            if (r10 == 0) goto Lb9
            im.vector.app.features.settings.VectorPreferences r10 = r9.getVectorPreferences()
            r10.clearPreferences()
            im.vector.app.features.ui.UiStateRepository r10 = r9.getUiStateRepository()
            r10.reset()
            im.vector.app.features.pin.PinLocker r10 = r9.getPinLocker()
            r10.unlock()
            im.vector.app.features.pin.lockscreen.pincode.PinCodeHelper r10 = r9.getPinCodeHelper()
            r0.L$0 = r9
            r0.L$1 = r11
            r0.label = r6
            java.lang.Object r10 = r10.deletePinCode(r0)
            if (r10 != r1) goto L8e
            return r1
        L8e:
            r10 = r9
        L8f:
            im.vector.app.features.analytics.VectorAnalytics r12 = r10.getVectorAnalytics()
            r0.L$0 = r10
            r0.L$1 = r11
            r0.label = r5
            java.lang.Object r12 = r12.onSignOut(r0)
            if (r12 != r1) goto La0
            return r1
        La0:
            r8 = r11
            r11 = r10
            r10 = r8
        La3:
            r0.L$0 = r11
            r0.L$1 = r7
            r0.label = r4
            java.lang.Object r10 = r10.clear(r0)
            if (r10 != r1) goto Lb0
            return r1
        Lb0:
            r10 = r11
        Lb1:
            im.vector.app.features.pin.lockscreen.crypto.LockScreenKeyRepository r11 = r10.getLockScreenKeyRepository()
            r11.deleteSystemKey()
            goto Lba
        Lb9:
            r10 = r9
        Lba:
            kotlinx.coroutines.CoroutineDispatcher r11 = kotlinx.coroutines.Dispatchers.getIO()
            im.vector.app.features.MainActivity$doLocalCleanup$2 r12 = new im.vector.app.features.MainActivity$doLocalCleanup$2
            r12.<init>(r10, r7)
            r0.L$0 = r7
            r0.label = r3
            java.lang.Object r10 = kotlinx.coroutines.BuildersKt__Builders_commonKt.withContext(r11, r12, r0)
            if (r10 != r1) goto Lce
            return r1
        Lce:
            kotlin.Unit r10 = kotlin.Unit.INSTANCE
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: im.vector.app.features.MainActivity.doLocalCleanup(boolean, im.vector.app.features.session.VectorSessionStore, kotlin.coroutines.Continuation):java.lang.Object");
    }

    private final StartAppViewModel getStartAppViewModel() {
        return (StartAppViewModel) this.startAppViewModel.getValue();
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x00f9, code lost:
    
        if (r0.getClearCache() != false) goto L50;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void handleAppStarted() {
        /*
            Method dump skipped, instructions count: 294
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: im.vector.app.features.MainActivity.handleAppStarted():void");
    }

    private final void handleStartForegroundService() {
        if (getStartAppViewModel().shouldStartApp()) {
            ContextCompat.startForegroundService(this, new Intent(this, (Class<?>) StartAppAndroidService.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void handleViewEvents(StartAppViewEvent event) {
        if (Intrinsics.areEqual(event, StartAppViewEvent.StartForegroundService.INSTANCE)) {
            handleStartForegroundService();
        } else if (Intrinsics.areEqual(event, StartAppViewEvent.AppStarted.INSTANCE)) {
            handleAppStarted();
        }
    }

    private final MainActivityArgs parseArgs() {
        Object obj;
        Intent intent = getIntent();
        Intrinsics.checkNotNullExpressionValue(intent, "intent");
        if (Build.VERSION.SDK_INT >= 33) {
            obj = intent.getParcelableExtra(EXTRA_ARGS, MainActivityArgs.class);
        } else {
            Object parcelableExtra = intent.getParcelableExtra(EXTRA_ARGS);
            if (!(parcelableExtra instanceof MainActivityArgs)) {
                parcelableExtra = null;
            }
            obj = (MainActivityArgs) parcelableExtra;
        }
        MainActivityArgs mainActivityArgs = (MainActivityArgs) obj;
        Timber.INSTANCE.w("Starting MainActivity with " + mainActivityArgs, new Object[0]);
        return new MainActivityArgs(mainActivityArgs != null ? mainActivityArgs.getClearCache() : false, mainActivityArgs != null ? mainActivityArgs.getClearCredentials() : false, mainActivityArgs != null ? mainActivityArgs.isUserLoggedOut() : false, mainActivityArgs != null ? mainActivityArgs.isAccountDeactivated() : false, mainActivityArgs != null ? mainActivityArgs.isSoftLogout() : false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public final void renderState(StartAppViewState state) {
        if (state.getMayBeLongToProcess()) {
            ((ActivityMainBinding) getViews()).status.setText(R.string.updating_your_data);
        }
        TextView textView = ((ActivityMainBinding) getViews()).status;
        Intrinsics.checkNotNullExpressionValue(textView, "views.status");
        textView.setVisibility(state.getMayBeLongToProcess() ? 0 : 8);
    }

    private final void signout(Session session, VectorSessionStore onboardingStore, boolean ignoreServerError) {
        BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new MainActivity$signout$1(session, this, ignoreServerError, onboardingStore, null), 3, null);
    }

    private final void startIntentAndFinish(Intent intent) {
        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x002d, code lost:
    
        if (r13.isAccountDeactivated() != false) goto L18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void startNextActivityAndFinish(boolean r13) {
        /*
            r12 = this;
            im.vector.app.features.MainActivityArgs r0 = r12.args
            java.lang.String r1 = "args"
            r2 = 0
            if (r0 != 0) goto Lb
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            r0 = r2
        Lb:
            boolean r0 = r0.getClearCredentials()
            if (r0 == 0) goto L3d
            if (r13 != 0) goto L3d
            im.vector.app.features.MainActivityArgs r13 = r12.args
            if (r13 != 0) goto L1b
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            r13 = r2
        L1b:
            boolean r13 = r13.isUserLoggedOut()
            if (r13 == 0) goto L2f
            im.vector.app.features.MainActivityArgs r13 = r12.args
            if (r13 != 0) goto L29
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            r13 = r2
        L29:
            boolean r13 = r13.isAccountDeactivated()
            if (r13 == 0) goto L3d
        L2f:
            im.vector.app.features.navigation.Navigator r3 = r12.getNavigator()
            r5 = 0
            r6 = 0
            r7 = 4
            r8 = 0
            r4 = r12
            im.vector.app.features.navigation.Navigator.DefaultImpls.openLogin$default(r3, r4, r5, r6, r7, r8)
            goto La4
        L3d:
            im.vector.app.features.MainActivityArgs r13 = r12.args
            if (r13 != 0) goto L45
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            r13 = r2
        L45:
            boolean r13 = r13.isSoftLogout()
            if (r13 == 0) goto L53
            im.vector.app.features.navigation.Navigator r13 = r12.getNavigator()
            r13.softLogout(r12)
            goto La4
        L53:
            im.vector.app.features.MainActivityArgs r13 = r12.args
            if (r13 != 0) goto L5b
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            r13 = r2
        L5b:
            boolean r13 = r13.isUserLoggedOut()
            if (r13 == 0) goto L68
            im.vector.app.features.signout.hard.SignedOutActivity$Companion r13 = im.vector.app.features.signout.hard.SignedOutActivity.INSTANCE
            android.content.Intent r2 = r13.newIntent(r12)
            goto La4
        L68:
            im.vector.app.core.di.ActiveSessionHolder r13 = r12.getActiveSessionHolder()
            boolean r13 = r13.hasActiveSession()
            if (r13 == 0) goto L98
            im.vector.app.core.di.ActiveSessionHolder r13 = r12.getActiveSessionHolder()
            org.matrix.android.sdk.api.session.Session r13 = r13.getActiveSession()
            boolean r13 = r13.isOpenable()
            if (r13 == 0) goto L90
            im.vector.app.features.home.HomeActivity$Companion r3 = im.vector.app.features.home.HomeActivity.INSTANCE
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 1
            r9 = 0
            r10 = 44
            r11 = 0
            r4 = r12
            android.content.Intent r2 = im.vector.app.features.home.HomeActivity.Companion.newIntent$default(r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto La4
        L90:
            im.vector.app.features.navigation.Navigator r13 = r12.getNavigator()
            r13.softLogout(r12)
            goto La4
        L98:
            im.vector.app.features.navigation.Navigator r3 = r12.getNavigator()
            r5 = 0
            r6 = 0
            r7 = 4
            r8 = 0
            r4 = r12
            im.vector.app.features.navigation.Navigator.DefaultImpls.openLogin$default(r3, r4, r5, r6, r7, r8)
        La4:
            r12.startIntentAndFinish(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.vector.app.features.MainActivity.startNextActivityAndFinish(boolean):void");
    }

    public static /* synthetic */ void startNextActivityAndFinish$default(MainActivity mainActivity, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        mainActivity.startNextActivityAndFinish(z);
    }

    private final void startSyncing() {
        Session safeActiveSession = getActiveSessionHolder().getSafeActiveSession();
        if (safeActiveSession != null) {
            SessionKt.startSyncing(safeActiveSession, this);
        }
    }

    @NotNull
    public final LockScreenKeyRepository getLockScreenKeyRepository() {
        LockScreenKeyRepository lockScreenKeyRepository = this.lockScreenKeyRepository;
        if (lockScreenKeyRepository != null) {
            return lockScreenKeyRepository;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lockScreenKeyRepository");
        return null;
    }

    @NotNull
    public final NotificationDrawerManager getNotificationDrawerManager() {
        NotificationDrawerManager notificationDrawerManager = this.notificationDrawerManager;
        if (notificationDrawerManager != null) {
            return notificationDrawerManager;
        }
        Intrinsics.throwUninitializedPropertyAccessException("notificationDrawerManager");
        return null;
    }

    @NotNull
    public final PinCodeHelper getPinCodeHelper() {
        PinCodeHelper pinCodeHelper = this.pinCodeHelper;
        if (pinCodeHelper != null) {
            return pinCodeHelper;
        }
        Intrinsics.throwUninitializedPropertyAccessException("pinCodeHelper");
        return null;
    }

    @NotNull
    public final PopupAlertManager getPopupAlertManager() {
        PopupAlertManager popupAlertManager = this.popupAlertManager;
        if (popupAlertManager != null) {
            return popupAlertManager;
        }
        Intrinsics.throwUninitializedPropertyAccessException("popupAlertManager");
        return null;
    }

    @NotNull
    public final ShortcutsHandler getShortcutsHandler() {
        ShortcutsHandler shortcutsHandler = this.shortcutsHandler;
        if (shortcutsHandler != null) {
            return shortcutsHandler;
        }
        Intrinsics.throwUninitializedPropertyAccessException("shortcutsHandler");
        return null;
    }

    @NotNull
    public final UiStateRepository getUiStateRepository() {
        UiStateRepository uiStateRepository = this.uiStateRepository;
        if (uiStateRepository != null) {
            return uiStateRepository;
        }
        Intrinsics.throwUninitializedPropertyAccessException("uiStateRepository");
        return null;
    }

    @NotNull
    public final VectorAnalytics getVectorAnalytics() {
        VectorAnalytics vectorAnalytics = this.vectorAnalytics;
        if (vectorAnalytics != null) {
            return vectorAnalytics;
        }
        Intrinsics.throwUninitializedPropertyAccessException("vectorAnalytics");
        return null;
    }

    @Override // im.vector.app.core.platform.VectorBaseActivity
    public void handleInvalidToken(@NotNull GlobalError.InvalidToken globalError) {
        Intrinsics.checkNotNullParameter(globalError, "globalError");
        Timber.INSTANCE.w("Ignoring invalid token global error", new Object[0]);
    }

    @Override // im.vector.app.core.platform.VectorBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getShortcutsHandler().updateShortcutsWithPreviousIntent();
        MavericksView.DefaultImpls.onEach$default(this, getStartAppViewModel(), null, new MainActivity$onCreate$1(this, null), 1, null);
        observeViewEvents(getStartAppViewModel(), new Function1<StartAppViewEvent, Unit>() { // from class: im.vector.app.features.MainActivity$onCreate$2
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(StartAppViewEvent startAppViewEvent) {
                invoke2(startAppViewEvent);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(@NotNull StartAppViewEvent it) {
                Intrinsics.checkNotNullParameter(it, "it");
                MainActivity.this.handleViewEvents(it);
            }
        });
        getStartAppViewModel().handle((StartAppAction) StartAppAction.StartApp.INSTANCE);
    }

    public final void setLockScreenKeyRepository(@NotNull LockScreenKeyRepository lockScreenKeyRepository) {
        Intrinsics.checkNotNullParameter(lockScreenKeyRepository, "<set-?>");
        this.lockScreenKeyRepository = lockScreenKeyRepository;
    }

    public final void setNotificationDrawerManager(@NotNull NotificationDrawerManager notificationDrawerManager) {
        Intrinsics.checkNotNullParameter(notificationDrawerManager, "<set-?>");
        this.notificationDrawerManager = notificationDrawerManager;
    }

    public final void setPinCodeHelper(@NotNull PinCodeHelper pinCodeHelper) {
        Intrinsics.checkNotNullParameter(pinCodeHelper, "<set-?>");
        this.pinCodeHelper = pinCodeHelper;
    }

    public final void setPopupAlertManager(@NotNull PopupAlertManager popupAlertManager) {
        Intrinsics.checkNotNullParameter(popupAlertManager, "<set-?>");
        this.popupAlertManager = popupAlertManager;
    }

    public final void setShortcutsHandler(@NotNull ShortcutsHandler shortcutsHandler) {
        Intrinsics.checkNotNullParameter(shortcutsHandler, "<set-?>");
        this.shortcutsHandler = shortcutsHandler;
    }

    public final void setUiStateRepository(@NotNull UiStateRepository uiStateRepository) {
        Intrinsics.checkNotNullParameter(uiStateRepository, "<set-?>");
        this.uiStateRepository = uiStateRepository;
    }

    public final void setVectorAnalytics(@NotNull VectorAnalytics vectorAnalytics) {
        Intrinsics.checkNotNullParameter(vectorAnalytics, "<set-?>");
        this.vectorAnalytics = vectorAnalytics;
    }

    @Override // im.vector.app.core.platform.VectorBaseActivity
    @NotNull
    public ActivityMainBinding getBinding() {
        ActivityMainBinding inflate = ActivityMainBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(layoutInflater)");
        return inflate;
    }

    @Override // im.vector.app.core.platform.VectorBaseActivity
    @NotNull
    public ActivityOtherThemes.Launcher getOtherThemes() {
        return ActivityOtherThemes.Launcher.INSTANCE;
    }
}
