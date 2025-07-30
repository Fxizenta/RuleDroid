package com.framer.viewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import com.framer.viewer.Downloader;
import com.framer.viewer.FramerViewSheetFragment;
import com.framer.viewer.MultiTapDetector;
import com.framer.viewer.ShakeDetector;
import java.util.ArrayList;
import java.util.HashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.anko.Sdk25ServicesKt;

/* compiled from: FramerView.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000¬\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\t\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u0004B\u0005¢\u0006\u0002\u0010\u0005J\u0010\u0010L\u001a\u00020G2\u0006\u0010M\u001a\u00020NH\u0016J\u0006\u0010O\u001a\u00020PJ\u0010\u0010Q\u001a\u00020G2\u0006\u0010R\u001a\u00020SH\u0002J\u0006\u0010T\u001a\u00020PJ\u0012\u0010U\u001a\u00020P2\b\u0010V\u001a\u0004\u0018\u00010WH\u0014J\u0010\u0010X\u001a\u00020P2\u0006\u0010Y\u001a\u00020ZH\u0016J\u0010\u0010[\u001a\u00020P2\u0006\u0010R\u001a\u00020SH\u0014J-\u0010\\\u001a\u00020P2\u0006\u0010]\u001a\u00020Z2\u000e\u0010^\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00110_2\u0006\u0010`\u001a\u00020aH\u0016¢\u0006\u0002\u0010bJ\b\u0010c\u001a\u00020PH\u0014J\b\u0010d\u001a\u00020PH\u0016J\b\u0010e\u001a\u00020PH\u0016J\u0010\u0010f\u001a\u00020P2\u0006\u0010g\u001a\u00020\u0011H\u0016J\b\u0010h\u001a\u00020PH\u0014J\b\u0010i\u001a\u00020PH\u0002R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001c\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u001a\u0010\u001c\u001a\u00020\u001dX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u001a\u0010\"\u001a\u00020#X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'R\u001a\u0010(\u001a\u00020)X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R\u001c\u0010.\u001a\u0004\u0018\u00010/X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u00101\"\u0004\b2\u00103R\u001c\u00104\u001a\u0004\u0018\u000105X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b6\u00107\"\u0004\b8\u00109R\u001a\u0010:\u001a\u00020;X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b<\u0010=\"\u0004\b>\u0010?R\u001a\u0010@\u001a\u00020AX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\bB\u0010C\"\u0004\bD\u0010ER\u001a\u0010F\u001a\u00020GX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bH\u0010I\"\u0004\bJ\u0010K¨\u0006j"}, d2 = {"Lcom/framer/viewer/FramerViewActivity;", "Landroid/support/v7/app/AppCompatActivity;", "Lcom/framer/viewer/ShakeDetector$Listener;", "Lcom/framer/viewer/MultiTapDetector$Listener;", "Lcom/framer/viewer/FramerViewSheetFragment$Listener;", "()V", "mDataStore", "Lcom/framer/viewer/DataStore;", "getMDataStore", "()Lcom/framer/viewer/DataStore;", "mFramerView", "Lcom/framer/viewer/FramerView;", "getMFramerView", "()Lcom/framer/viewer/FramerView;", "setMFramerView", "(Lcom/framer/viewer/FramerView;)V", "mGelocationOrigin", "", "getMGelocationOrigin", "()Ljava/lang/String;", "setMGelocationOrigin", "(Ljava/lang/String;)V", "mGeolocationCallback", "Landroid/webkit/GeolocationPermissions$Callback;", "getMGeolocationCallback", "()Landroid/webkit/GeolocationPermissions$Callback;", "setMGeolocationCallback", "(Landroid/webkit/GeolocationPermissions$Callback;)V", "mGestureDetector", "Landroid/support/v4/view/GestureDetectorCompat;", "getMGestureDetector", "()Landroid/support/v4/view/GestureDetectorCompat;", "setMGestureDetector", "(Landroid/support/v4/view/GestureDetectorCompat;)V", "mMainLayout", "Landroid/support/design/widget/CoordinatorLayout;", "getMMainLayout", "()Landroid/support/design/widget/CoordinatorLayout;", "setMMainLayout", "(Landroid/support/design/widget/CoordinatorLayout;)V", "mMultiTapDetector", "Lcom/framer/viewer/MultiTapDetector;", "getMMultiTapDetector", "()Lcom/framer/viewer/MultiTapDetector;", "setMMultiTapDetector", "(Lcom/framer/viewer/MultiTapDetector;)V", "mOpenedProject", "Lcom/framer/viewer/FramerProject;", "getMOpenedProject", "()Lcom/framer/viewer/FramerProject;", "setMOpenedProject", "(Lcom/framer/viewer/FramerProject;)V", "mPermissionRequest", "Landroid/webkit/PermissionRequest;", "getMPermissionRequest", "()Landroid/webkit/PermissionRequest;", "setMPermissionRequest", "(Landroid/webkit/PermissionRequest;)V", "mPreferences", "Landroid/content/SharedPreferences;", "getMPreferences", "()Landroid/content/SharedPreferences;", "setMPreferences", "(Landroid/content/SharedPreferences;)V", "mShakeDetector", "Lcom/framer/viewer/ShakeDetector;", "getMShakeDetector", "()Lcom/framer/viewer/ShakeDetector;", "setMShakeDetector", "(Lcom/framer/viewer/ShakeDetector;)V", "mSheetShowing", "", "getMSheetShowing", "()Z", "setMSheetShowing", "(Z)V", "dispatchTouchEvent", NotificationCompat.CATEGORY_EVENT, "Landroid/view/MotionEvent;", "goFullscreen", "", "handleIntent", "intent", "Landroid/content/Intent;", "maybeShowHelpSnackbar", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onMultiTap", "nTap", "", "onNewIntent", "onRequestPermissionsResult", "requestCode", "permissions", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "onShake", "onSheetDismiss", "onSheetItemClicked", "tag", "onStop", "showToolbar", "app_release"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes.dex */
public final class FramerViewActivity extends AppCompatActivity implements ShakeDetector.Listener, MultiTapDetector.Listener, FramerViewSheetFragment.Listener {
    private HashMap _$_findViewCache;
    private final DataStore mDataStore = DataStore.INSTANCE.getCurrent();
    public FramerView mFramerView;
    private String mGelocationOrigin;
    private GeolocationPermissions.Callback mGeolocationCallback;
    public GestureDetectorCompat mGestureDetector;
    public CoordinatorLayout mMainLayout;
    public MultiTapDetector mMultiTapDetector;
    private FramerProject mOpenedProject;
    private PermissionRequest mPermissionRequest;
    public SharedPreferences mPreferences;
    public ShakeDetector mShakeDetector;
    private boolean mSheetShowing;

    public void _$_clearFindViewByIdCache() {
        HashMap hashMap = this._$_findViewCache;
        if (hashMap != null) {
            hashMap.clear();
        }
    }

    public View _$_findCachedViewById(int i) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }
        View view = (View) this._$_findViewCache.get(Integer.valueOf(i));
        if (view != null) {
            return view;
        }
        View findViewById = findViewById(i);
        this._$_findViewCache.put(Integer.valueOf(i), findViewById);
        return findViewById;
    }

    public final DataStore getMDataStore() {
        return this.mDataStore;
    }

    public final FramerProject getMOpenedProject() {
        return this.mOpenedProject;
    }

    public final void setMOpenedProject(FramerProject framerProject) {
        this.mOpenedProject = framerProject;
    }

    public final boolean getMSheetShowing() {
        return this.mSheetShowing;
    }

    public final void setMSheetShowing(boolean z) {
        this.mSheetShowing = z;
    }

    public final SharedPreferences getMPreferences() {
        SharedPreferences sharedPreferences = this.mPreferences;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        return sharedPreferences;
    }

    public final void setMPreferences(SharedPreferences sharedPreferences) {
        Intrinsics.checkParameterIsNotNull(sharedPreferences, "<set-?>");
        this.mPreferences = sharedPreferences;
    }

    public final FramerView getMFramerView() {
        FramerView framerView = this.mFramerView;
        if (framerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
        }
        return framerView;
    }

    public final void setMFramerView(FramerView framerView) {
        Intrinsics.checkParameterIsNotNull(framerView, "<set-?>");
        this.mFramerView = framerView;
    }

    public final ShakeDetector getMShakeDetector() {
        ShakeDetector shakeDetector = this.mShakeDetector;
        if (shakeDetector == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mShakeDetector");
        }
        return shakeDetector;
    }

    public final void setMShakeDetector(ShakeDetector shakeDetector) {
        Intrinsics.checkParameterIsNotNull(shakeDetector, "<set-?>");
        this.mShakeDetector = shakeDetector;
    }

    public final MultiTapDetector getMMultiTapDetector() {
        MultiTapDetector multiTapDetector = this.mMultiTapDetector;
        if (multiTapDetector == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMultiTapDetector");
        }
        return multiTapDetector;
    }

    public final void setMMultiTapDetector(MultiTapDetector multiTapDetector) {
        Intrinsics.checkParameterIsNotNull(multiTapDetector, "<set-?>");
        this.mMultiTapDetector = multiTapDetector;
    }

    public final GestureDetectorCompat getMGestureDetector() {
        GestureDetectorCompat gestureDetectorCompat = this.mGestureDetector;
        if (gestureDetectorCompat == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mGestureDetector");
        }
        return gestureDetectorCompat;
    }

    public final void setMGestureDetector(GestureDetectorCompat gestureDetectorCompat) {
        Intrinsics.checkParameterIsNotNull(gestureDetectorCompat, "<set-?>");
        this.mGestureDetector = gestureDetectorCompat;
    }

    public final CoordinatorLayout getMMainLayout() {
        CoordinatorLayout coordinatorLayout = this.mMainLayout;
        if (coordinatorLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMainLayout");
        }
        return coordinatorLayout;
    }

    public final void setMMainLayout(CoordinatorLayout coordinatorLayout) {
        Intrinsics.checkParameterIsNotNull(coordinatorLayout, "<set-?>");
        this.mMainLayout = coordinatorLayout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        ShakeDetector shakeDetector = this.mShakeDetector;
        if (shakeDetector == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mShakeDetector");
        }
        shakeDetector.stop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        FramerProject currentLaunch;
        super.onCreate(savedInstanceState);
        this.mFramerView = new FramerView(this);
        FramerViewActivity framerViewActivity = this;
        this.mMainLayout = new CoordinatorLayout(framerViewActivity);
        FramerView framerView = this.mFramerView;
        if (framerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
        }
        framerView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        CoordinatorLayout coordinatorLayout = this.mMainLayout;
        if (coordinatorLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMainLayout");
        }
        FramerView framerView2 = this.mFramerView;
        if (framerView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
        }
        coordinatorLayout.addView(framerView2);
        CoordinatorLayout coordinatorLayout2 = this.mMainLayout;
        if (coordinatorLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMainLayout");
        }
        coordinatorLayout2.setKeepScreenOn(true);
        CoordinatorLayout coordinatorLayout3 = this.mMainLayout;
        if (coordinatorLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMainLayout");
        }
        setContentView(coordinatorLayout3);
        PreferenceManager.setDefaultValues(framerViewActivity, com.framerjs.android.R.xml.preferences, false);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(framerViewActivity);
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…ltSharedPreferences(this)");
        this.mPreferences = defaultSharedPreferences;
        Intent intent = getIntent();
        Intrinsics.checkExpressionValueIsNotNull(intent, "intent");
        if (!handleIntent(intent) && (currentLaunch = this.mDataStore.getCurrentLaunch()) != null) {
            this.mOpenedProject = currentLaunch;
            DataStore.INSTANCE.getCurrent().justOpenedProject(framerViewActivity, currentLaunch);
            FramerView framerView3 = this.mFramerView;
            if (framerView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
            }
            framerView3.load(currentLaunch.getUrl(), true);
        }
        maybeShowHelpSnackbar();
        this.mShakeDetector = new ShakeDetector(this);
        SharedPreferences sharedPreferences = this.mPreferences;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        if (sharedPreferences.getBoolean("gesture_ctrl_shake", true)) {
            ShakeDetector shakeDetector = this.mShakeDetector;
            if (shakeDetector == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mShakeDetector");
            }
            shakeDetector.start(Sdk25ServicesKt.getSensorManager(this));
        }
        this.mMultiTapDetector = new MultiTapDetector(3);
        MultiTapDetector multiTapDetector = this.mMultiTapDetector;
        if (multiTapDetector == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMultiTapDetector");
        }
        multiTapDetector.setListener(this);
        this.mGestureDetector = new GestureDetectorCompat(framerViewActivity, new GestureDetector.SimpleOnGestureListener() { // from class: com.framer.viewer.FramerViewActivity$onCreate$2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent event) {
                Intrinsics.checkParameterIsNotNull(event, "event");
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Intrinsics.checkParameterIsNotNull(e1, "e1");
                Intrinsics.checkParameterIsNotNull(e2, "e2");
                DisplayMetrics displayMetrics = new DisplayMetrics();
                WindowManager windowManager = FramerViewActivity.this.getWindowManager();
                Intrinsics.checkExpressionValueIsNotNull(windowManager, "windowManager");
                windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
                float f = displayMetrics.density;
                float x = (e2.getX() - e1.getX()) / f;
                float y = (e2.getY() - e1.getY()) / f;
                float abs = Math.abs(x);
                float f2 = 100;
                boolean z = abs >= f2;
                boolean z2 = Math.abs(y) >= f2;
                if (e2.getEventTime() - e1.getEventTime() > 500) {
                    z = false;
                    z2 = false;
                }
                if (z2 && !z && y < 0) {
                    if ((e1.getY() / ((float) displayMetrics.heightPixels) > 0.95f) && FramerViewActivity.this.getMPreferences().getBoolean("gesture_ctrl_swipe", true)) {
                        FramerViewActivity.this.showToolbar();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        goFullscreen();
    }

    public final void maybeShowHelpSnackbar() {
        SharedPreferences sharedPreferences = this.mPreferences;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        if (sharedPreferences.getBoolean("gesture_help_suppress", false)) {
            return;
        }
        SharedPreferences sharedPreferences2 = this.mPreferences;
        if (sharedPreferences2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        boolean z = !sharedPreferences2.getBoolean("gesture_help_seen", false);
        SharedPreferences sharedPreferences3 = this.mPreferences;
        if (sharedPreferences3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        boolean z2 = sharedPreferences3.getBoolean("gesture_ctrl_swipe", true);
        SharedPreferences sharedPreferences4 = this.mPreferences;
        if (sharedPreferences4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        boolean z3 = sharedPreferences4.getBoolean("gesture_ctrl_shake", true);
        String str = (z2 && z3) ? "Shake device or swipe from the bottom for options." : z2 ? "Swipe from the bottom for options." : z3 ? "Shake device for options." : "Change options to control preview in settings menu.";
        CoordinatorLayout coordinatorLayout = this.mMainLayout;
        if (coordinatorLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMainLayout");
        }
        final Snackbar make = Snackbar.make(coordinatorLayout, str, z ? -2 : 0);
        Intrinsics.checkExpressionValueIsNotNull(make, "Snackbar.make(mMainLayou…lse Snackbar.LENGTH_LONG)");
        if (z) {
            make.setAction("hide", new View.OnClickListener() { // from class: com.framer.viewer.FramerViewActivity$maybeShowHelpSnackbar$1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    Snackbar.this.dismiss();
                }
            });
        } else {
            make.setAction("don’t show again", new View.OnClickListener() { // from class: com.framer.viewer.FramerViewActivity$maybeShowHelpSnackbar$2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FramerViewActivity.this.getMPreferences().edit().putBoolean("gesture_help_suppress", true).apply();
                }
            });
        }
        make.show();
        if (z) {
            SharedPreferences sharedPreferences5 = this.mPreferences;
            if (sharedPreferences5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
            }
            sharedPreferences5.edit().putBoolean("gesture_help_seen", true).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private final boolean handleIntent(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (!Intrinsics.areEqual("android.intent.action.VIEW", action) || data == null) {
            return false;
        }
        String uri = data.toString();
        Intrinsics.checkExpressionValueIsNotNull(uri, "appLinkData.toString()");
        this.mOpenedProject = new FramerProject(uri, DataStoreKt.nameForUrl(uri), null, 4, null);
        FramerProject framerProject = this.mOpenedProject;
        if (framerProject != null) {
            DataStore.INSTANCE.getCurrent().justOpenedProject(this, framerProject);
        }
        FramerView framerView = this.mFramerView;
        if (framerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
        }
        framerView.load(uri, true);
        return true;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, "event");
        GestureDetectorCompat gestureDetectorCompat = this.mGestureDetector;
        if (gestureDetectorCompat == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mGestureDetector");
        }
        gestureDetectorCompat.onTouchEvent(event);
        MultiTapDetector multiTapDetector = this.mMultiTapDetector;
        if (multiTapDetector == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mMultiTapDetector");
        }
        multiTapDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override // com.framer.viewer.ShakeDetector.Listener
    public void onShake() {
        if (this.mSheetShowing) {
            return;
        }
        SharedPreferences sharedPreferences = this.mPreferences;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        if (sharedPreferences.getBoolean("gesture_ctrl_shake", true)) {
            showToolbar();
        }
    }

    @Override // com.framer.viewer.MultiTapDetector.Listener
    public void onMultiTap(int nTap) {
        SharedPreferences sharedPreferences = this.mPreferences;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mPreferences");
        }
        if (sharedPreferences.getBoolean("gesture_refresh_tap3", true)) {
            FramerView framerView = this.mFramerView;
            if (framerView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
            }
            framerView.reload();
        }
    }

    @Override // com.framer.viewer.FramerViewSheetFragment.Listener
    public void onSheetItemClicked(String tag) {
        FramerProject framerProject;
        FramerProject framerProject2;
        Intrinsics.checkParameterIsNotNull(tag, "tag");
        switch (tag.hashCode()) {
            case -1335458389:
                if (!tag.equals(FramerViewSheetFragment.DELETE_ITEM_TAG) || (framerProject = this.mOpenedProject) == null) {
                    return;
                }
                Downloader.Companion companion = Downloader.INSTANCE;
                FramerView framerView = this.mFramerView;
                if (framerView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
                }
                Context context = framerView.getContext();
                Intrinsics.checkExpressionValueIsNotNull(context, "mFramerView.context");
                companion.deleteDownloadedProject(context, framerProject);
                return;
            case 94756344:
                if (tag.equals(FramerViewSheetFragment.CLOSE_ITEM_TAG)) {
                    onBackPressed();
                    return;
                }
                return;
            case 1085444827:
                if (tag.equals(FramerViewSheetFragment.REFRESH_ITEM_TAG)) {
                    FramerView framerView2 = this.mFramerView;
                    if (framerView2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
                    }
                    framerView2.reload();
                    return;
                }
                return;
            case 1427818632:
                if (!tag.equals(FramerViewSheetFragment.DOWNLOAD_ITEM_TAG) || (framerProject2 = this.mOpenedProject) == null) {
                    return;
                }
                Downloader downloader = new Downloader(framerProject2.getUrl(), framerProject2.getName());
                FramerView framerView3 = this.mFramerView;
                if (framerView3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
                }
                Context context2 = framerView3.getContext();
                Intrinsics.checkExpressionValueIsNotNull(context2, "mFramerView.context");
                downloader.start(context2, CookieManager.getInstance().getCookie(framerProject2.getUrl()));
                return;
            default:
                return;
        }
    }

    @Override // com.framer.viewer.FramerViewSheetFragment.Listener
    public void onSheetDismiss() {
        this.mSheetShowing = false;
        goFullscreen();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void showToolbar() {
        FramerProject currentLaunch = this.mDataStore.getCurrentLaunch();
        if (currentLaunch == null) {
            return;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        if (!currentLaunch.isWebProject()) {
            if (currentLaunch.getSaved()) {
                arrayList.add(FramerViewSheetFragment.DOWNLOAD_ITEM_TAG);
            } else {
                arrayList.add(FramerViewSheetFragment.DELETE_ITEM_TAG);
            }
        }
        this.mSheetShowing = true;
        FramerViewSheetFragment.INSTANCE.newInstance(currentLaunch.getName(), arrayList).show(getSupportFragmentManager(), "dialog");
    }

    public final void goFullscreen() {
        FramerView framerView = this.mFramerView;
        if (framerView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mFramerView");
        }
        framerView.setSystemUiVisibility(5894);
    }

    public final String getMGelocationOrigin() {
        return this.mGelocationOrigin;
    }

    public final void setMGelocationOrigin(String str) {
        this.mGelocationOrigin = str;
    }

    public final GeolocationPermissions.Callback getMGeolocationCallback() {
        return this.mGeolocationCallback;
    }

    public final void setMGeolocationCallback(GeolocationPermissions.Callback callback) {
        this.mGeolocationCallback = callback;
    }

    public final PermissionRequest getMPermissionRequest() {
        return this.mPermissionRequest;
    }

    public final void setMPermissionRequest(PermissionRequest permissionRequest) {
        this.mPermissionRequest = permissionRequest;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int i;
        int i2;
        String str;
        String str2;
        Intrinsics.checkParameterIsNotNull(permissions, "permissions");
        Intrinsics.checkParameterIsNotNull(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        i = FramerViewKt.GEO_REQUEST;
        if (requestCode != i) {
            i2 = FramerViewKt.CAMERA_REQUEST;
            if (requestCode == i2) {
                ArrayList arrayList = new ArrayList();
                int length = grantResults.length;
                for (int i3 = 0; i3 < length; i3++) {
                    int i4 = grantResults[i3];
                    if (i4 == -1) {
                        arrayList.add(Integer.valueOf(i4));
                    }
                }
                ArrayList arrayList2 = arrayList;
                str = FramerViewKt.TAG;
                Log.i(str, "camera permissions granted: " + arrayList2);
                PermissionRequest permissionRequest = this.mPermissionRequest;
                if (permissionRequest != null) {
                    if (arrayList2.size() != 5) {
                        permissionRequest.grant(permissionRequest.getResources());
                    } else {
                        permissionRequest.deny();
                    }
                }
                this.mPermissionRequest = (PermissionRequest) null;
                return;
            }
            return;
        }
        ArrayList arrayList3 = new ArrayList();
        int length2 = grantResults.length;
        for (int i5 = 0; i5 < length2; i5++) {
            int i6 = grantResults[i5];
            if (i6 == -1) {
                arrayList3.add(Integer.valueOf(i6));
            }
        }
        ArrayList arrayList4 = arrayList3;
        str2 = FramerViewKt.TAG;
        Log.i(str2, "geo permissions granted: " + arrayList4);
        GeolocationPermissions.Callback callback = this.mGeolocationCallback;
        if (callback != null) {
            callback.invoke(this.mGelocationOrigin, arrayList4.size() != 2, arrayList4.size() != 2);
        }
        this.mGeolocationCallback = (GeolocationPermissions.Callback) null;
    }
}
