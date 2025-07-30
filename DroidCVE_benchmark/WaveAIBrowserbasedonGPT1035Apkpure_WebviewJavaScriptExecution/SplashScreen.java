package wave.ai.browser.ui.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.lifecycle.ViewModelLazy;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.viewbinding.ViewBindings;
import com.google.firebase.crashlytics.internal.common.CommonUtils;
import com.safedk.android.utils.Logger;
import kotlin.Lazy;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.text.StringsKt__StringsJVMKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.internal.MainDispatcherLoader;
import wave.ai.browser.R;
import wave.ai.browser.base.BaseActivity;
import wave.ai.browser.di.DI;
import wave.ai.browser.di.features.splashscreen.SplashScreenModule;
import wave.ai.browser.ui.ai.ui.chat.ChatGptActivity;
import wave.ai.browser.ui.mainscreen.MainActivity;
import wave.ai.browser.ui.tab.externalurl.ExternalUrlProvider;

@SuppressLint({"CustomSplashScreen"})
/* loaded from: classes.dex */
public final class SplashScreen extends BaseActivity {
    public static final /* synthetic */ int $r8$clinit = 0;
    public boolean isNavigationActionInvoked;
    public final Lazy vm$delegate;

    public SplashScreen() {
        Function0 function0 = new Function0<ViewModelProvider.Factory>() { // from class: wave.ai.browser.ui.splash.SplashScreen$vm$2
            @Override // kotlin.jvm.functions.Function0
            public ViewModelProvider.Factory invoke() {
                return ((SplashScreenModule) DI.getDependencies().featuresModule.splashScreenModule$delegate.getValue()).splashScreenViewModelFactory;
            }
        };
        final Function0 function02 = null;
        this.vm$delegate = new ViewModelLazy(Reflection.getOrCreateKotlinClass(SplashScreenViewModel.class), new Function0<ViewModelStore>() { // from class: wave.ai.browser.ui.splash.SplashScreen$special$$inlined$viewModels$default$2
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public ViewModelStore invoke() {
                ViewModelStore viewModelStore = ComponentActivity.this.getViewModelStore();
                Intrinsics.checkNotNullExpressionValue(viewModelStore, "viewModelStore");
                return viewModelStore;
            }
        }, function0 == null ? new Function0<ViewModelProvider.Factory>() { // from class: wave.ai.browser.ui.splash.SplashScreen$special$$inlined$viewModels$default$1
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public ViewModelProvider.Factory invoke() {
                ViewModelProvider.Factory defaultViewModelProviderFactory = ComponentActivity.this.getDefaultViewModelProviderFactory();
                Intrinsics.checkNotNullExpressionValue(defaultViewModelProviderFactory, "defaultViewModelProviderFactory");
                return defaultViewModelProviderFactory;
            }
        } : function0, new Function0<CreationExtras>(function02, this) { // from class: wave.ai.browser.ui.splash.SplashScreen$special$$inlined$viewModels$default$3
            public final /* synthetic */ ComponentActivity $this_viewModels;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.$this_viewModels = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public CreationExtras invoke() {
                CreationExtras defaultViewModelCreationExtras = this.$this_viewModels.getDefaultViewModelCreationExtras();
                Intrinsics.checkNotNullExpressionValue(defaultViewModelCreationExtras, "this.defaultViewModelCreationExtras");
                return defaultViewModelCreationExtras;
            }
        });
    }

    public static void safedk_Activity_startActivity_9d898b58165fa4ba0e12c3900a2b8533(Activity p0, Intent p1) {
        Logger.d("SafeDK-Special|SafeDK: Call> Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V");
        if (p1 == null) {
            return;
        }
        p0.startActivity(p1);
    }

    public final Object nextActivity(Intent intent, Continuation<? super Unit> continuation) {
        Dispatchers dispatchers = Dispatchers.INSTANCE;
        Object withContext = BuildersKt.withContext(MainDispatcherLoader.dispatcher, new SplashScreen$nextActivity$2(this, intent, null), continuation);
        return withContext == CoroutineSingletons.COROUTINE_SUSPENDED ? withContext : Unit.INSTANCE;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Uri data;
        androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(2);
        getWindow().setFlags(512, 512);
        if (!CommonUtils.isEmulator() && !CommonUtils.isRooted()) {
            Bundle extras = getIntent().getExtras();
            String action = extras != null ? extras.getString("appActionType") : null;
            Bundle extras2 = getIntent().getExtras();
            String query = extras2 != null ? extras2.getString("query") : null;
            Bundle extras3 = getIntent().getExtras();
            String string = extras3 != null ? extras3.getString("isNeedToUseTodayDate") : null;
            if (!(action == null || StringsKt__StringsJVMKt.isBlank(action))) {
                if (!(query == null || StringsKt__StringsJVMKt.isBlank(query))) {
                    boolean parseBoolean = Boolean.parseBoolean(string);
                    Intrinsics.checkNotNullParameter(action, "action");
                    Intrinsics.checkNotNullParameter(query, "query");
                    Intent intent = new Intent(this, (Class<?>) MainActivity.class);
                    intent.putExtra("appActionType", action);
                    intent.putExtra("query", query);
                    intent.putExtra("isNeedToUseTodayDate", parseBoolean);
                    safedk_Activity_startActivity_9d898b58165fa4ba0e12c3900a2b8533(this, intent);
                    return;
                }
            }
            Intent intent2 = getIntent();
            if (Intrinsics.areEqual(intent2 != null ? intent2.getAction() : null, "android.intent.action.PROCESS_TEXT")) {
                String stringExtra = getIntent().getStringExtra("android.intent.extra.PROCESS_TEXT");
                Intent intent3 = new Intent(this, (Class<?>) ChatGptActivity.class);
                intent3.putExtra("screen_type_key", R.id.ai_chat_search_fragment);
                intent3.putExtra("extra_text_to_process", stringExtra);
                safedk_Activity_startActivity_9d898b58165fa4ba0e12c3900a2b8533(this, intent3);
                finish();
                return;
            }
            Intent intent4 = getIntent();
            if (intent4 != null && (data = intent4.getData()) != null) {
                ExternalUrlProvider externalUrlProvider = DI.getDependencies().domainModule.getExternalUrlProvider();
                String uri = data.toString();
                Intrinsics.checkNotNullExpressionValue(uri, "it.toString()");
                ExternalUrlProvider.emitExternalUrl$default(externalUrlProvider, uri, true, true, false, false, 24);
            }
            Intent intent5 = getIntent();
            if (intent5 != null) {
                intent5.setData(null);
            }
            Lifecycle.State state = Lifecycle.State.RESUMED;
            BuildersKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, 0, new SplashScreen$init$$inlined$launchAndRepeatWithViewLifecycle$1(this, state, null, this), 3, null);
            BuildersKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, 0, new SplashScreen$init$$inlined$launchAndRepeatWithViewLifecycle$2(this, state, null, this), 3, null);
            return;
        }
        showDialog();
    }

    public final void showDialog() {
        int i = CommonUtils.isEmulator() ? R.string.dialog_app_blocked_title_emulated : R.string.dialog_app_blocked_title_rooted;
        View inflate = getLayoutInflater().inflate(R.layout.dialog_app_blocked, (ViewGroup) null, false);
        int i2 = R.id.btnOk;
        AppCompatButton appCompatButton = (AppCompatButton) ViewBindings.findChildViewById(inflate, R.id.btnOk);
        if (appCompatButton != null) {
            i2 = R.id.tvDescription;
            if (((AppCompatTextView) ViewBindings.findChildViewById(inflate, R.id.tvDescription)) != null) {
                i2 = R.id.tvTitle;
                AppCompatTextView appCompatTextView = (AppCompatTextView) ViewBindings.findChildViewById(inflate, R.id.tvTitle);
                if (appCompatTextView != null) {
                    AlertDialog.Builder cancelable = new AlertDialog.Builder(this, R.style.DefaultDialog).setCustomTitle(null).setView((ConstraintLayout) inflate).setCancelable(false);
                    appCompatTextView.setText(i);
                    appCompatButton.setOnClickListener(new View.OnClickListener() { // from class: wave.ai.browser.ui.splash.SplashScreen$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            SplashScreen this$0 = SplashScreen.this;
                            int i3 = SplashScreen.$r8$clinit;
                            Intrinsics.checkNotNullParameter(this$0, "this$0");
                            this$0.finish();
                        }
                    });
                    cancelable.show();
                    return;
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(inflate.getResources().getResourceName(i2)));
    }
}
