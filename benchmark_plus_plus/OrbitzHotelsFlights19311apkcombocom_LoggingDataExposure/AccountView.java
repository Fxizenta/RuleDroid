package com.expedia.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.c;
import androidx.appcompat.widget.Toolbar;
import com.expedia.account.data.AccountResponse;
import com.expedia.account.data.Db;
import com.expedia.account.data.PartialUser;
import com.expedia.account.presenter.BufferedPresenter;
import com.expedia.account.presenter.CompoundTransition;
import com.expedia.account.presenter.FadeTransition;
import com.expedia.account.presenter.LeftToRightTransition;
import com.expedia.account.presenter.OffScreenBottomTransition;
import com.expedia.account.presenter.Presenter;
import com.expedia.account.presenter.RightToLeftTransition;
import com.expedia.account.presenter.SlideInBottomTransition;
import com.expedia.account.presenter.SlideUpTransition;
import com.expedia.account.recaptcha.Recaptcha;
import com.expedia.account.recaptcha.RecaptchaHandler;
import com.expedia.account.singlepage.SinglePageSignUpLayout;
import com.expedia.account.util.AccessibilityUtil;
import com.expedia.account.util.AndroidNetworkConnectivity;
import com.expedia.account.util.ErrorReporting;
import com.expedia.account.util.Events;
import com.expedia.account.util.FacebookViewHelper;
import com.expedia.account.util.PresenterUtils;
import com.expedia.account.util.Utils;
import com.expedia.account.view.AnimatedIconToolbar;
import com.expedia.account.view.EmailNameLayout;
import com.expedia.account.view.FacebookAPIHostLayout;
import com.expedia.account.view.FacebookLayout;
import com.expedia.account.view.HeaderLayout;
import com.expedia.account.view.PasswordLayout;
import com.expedia.account.view.SignInLayout;
import com.expedia.account.view.TOSLayout;
import com.expedia.account.view.WelcomeLayout;
import com.mobiata.android.Log;
import com.squareup.a.h;
import io.reactivex.a.c;
import io.reactivex.b.g;
import io.reactivex.e.d;
import io.reactivex.t;
import org.joda.time.DateTimeConstants;

/* loaded from: classes.dex */
public class AccountView extends BufferedPresenter {
    public static final String STATE_EMAIL_NAME = "STATE_EMAIL_NAME";
    public static final String STATE_FACEBOOK = "STATE_FACEBOOK";
    public static final String STATE_FACEBOOK_API_HOST = "STATE_FACEBOOK_API_HOST";
    public static final String STATE_LOADING_ACCOUNT = "STATE_LOADING_ACCOUNT";
    public static final String STATE_LOADING_FACEBOOK = "STATE_LOADING_FACEBOOK";
    public static final String STATE_LOADING_SIGN_IN = "STATE_LOADING_SIGN_IN";
    public static final String STATE_LOADING_SINGLE_PAGE = "STATE_LOADING_SINGLE_PAGE";
    public static final String STATE_PASSWORD = "STATE_PASSWORD";
    public static final String STATE_SIGN_IN = "STATE_SIGN_IN";
    public static final String STATE_SINGLE_PAGE_SIGN_UP = "STATE_SINGLE_PAGE_SIGN_UP";
    public static final String STATE_TOS = "STATE_TOS";
    public static final String STATE_WELCOME = "STATE_WELCOME";
    private static final String TAG = "AccountView";
    private boolean handleKeyBoardVisibilityChanges;
    private String mBrand;
    private Config mConfig;
    private c mCurrentDownload;
    private FacebookViewHelper mFacebookHelper;
    private boolean mFocusEmailAddress;
    private boolean mFocusFirstName;
    private boolean mFocusLastName;
    private t<Boolean> mLinkButtonController;
    private View.OnClickListener mNavigationClickListener;
    private t<Boolean> mNextButtonController;
    private Toolbar.c mNextClickMenuListener;
    private EmailNameLayout vEmailNameLayout;
    private FacebookAPIHostLayout vFacebookAPIHostLayout;
    private FacebookLayout vFacebookLayout;
    private HeaderLayout vHeaderLayout;
    private PasswordLayout vPasswordLayout;
    private SignInLayout vSignInLayout;
    private SinglePageSignUpLayout vSinglePageSignUpLayout;
    private Toolbar vSinglePageToolbar;
    public View vSinglePageWhiteBackground;
    private TOSLayout vTOSLayout;
    private AnimatedIconToolbar vToolbar;
    private WelcomeLayout vWelcomeLayout;

    public void configure(Config config) {
        this.mConfig = config;
        this.vTOSLayout.configurePOS(this.mConfig.showSpamOptIn, this.mConfig.enableSpamByDefault, this.mConfig.hasUserRewardsEnrollmentCheck, this.mConfig.tosText, this.mConfig.marketingText, this.mConfig.rewardsText);
        this.vSignInLayout.configure(this.mConfig.enableFacebookSignIn);
        if (this.mConfig.signupString != null) {
            this.vSignInLayout.configureAccountCreationString(this.mConfig.signupString);
        }
        this.vHeaderLayout.configurePOS(this.mConfig.enableSignInMessaging, this.mConfig.signInMessagingText);
        this.vSinglePageSignUpLayout.configure(this.mConfig.showSpamOptIn, this.mConfig.enableSpamByDefault, this.mConfig.hasUserRewardsEnrollmentCheck, this.mConfig.tosText, this.mConfig.marketingText, this.mConfig.rewardsText);
        if (this.mConfig.enableFacebookSignIn) {
            this.mFacebookHelper = createFacebookViewHelper();
        }
        if (STATE_SIGN_IN.equals(this.mConfig.initialState)) {
            return;
        }
        show(this.mConfig.initialState, 32802);
    }

    public FacebookViewHelper createFacebookViewHelper() {
        return new FacebookViewHelper(this);
    }

    public AccountService getService() {
        Config config = this.mConfig;
        if (config == null) {
            return null;
        }
        return config.getService();
    }

    public void setAnalyticsListener(AnalyticsListener analyticsListener) {
        this.mConfig.setAnalyticsListener(analyticsListener);
    }

    public void setAccountSignInListener(AccountSignInListener accountSignInListener) {
        this.mConfig.setListener(accountSignInListener);
    }

    public AccountView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFocusEmailAddress = false;
        this.mFocusFirstName = false;
        this.mFocusLastName = false;
        this.handleKeyBoardVisibilityChanges = true;
        this.mNextButtonController = new d<Boolean>() { // from class: com.expedia.account.AccountView.1
            @Override // io.reactivex.t
            public void onComplete() {
            }

            @Override // io.reactivex.t
            public void onError(Throwable th) {
            }

            @Override // io.reactivex.t
            public void onNext(Boolean bool) {
                AccountView.this.setNextEnabled(bool);
            }
        };
        this.mLinkButtonController = new d<Boolean>() { // from class: com.expedia.account.AccountView.2
            @Override // io.reactivex.t
            public void onComplete() {
            }

            @Override // io.reactivex.t
            public void onError(Throwable th) {
            }

            @Override // io.reactivex.t
            public void onNext(Boolean bool) {
                AccountView.this.setLinkEnabled(bool);
            }
        };
        this.mNavigationClickListener = new View.OnClickListener() { // from class: com.expedia.account.AccountView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AccountView.this.up();
            }
        };
        this.mNextClickMenuListener = new Toolbar.c() { // from class: com.expedia.account.AccountView.4
            @Override // androidx.appcompat.widget.Toolbar.c
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_next) {
                    AccountView.this.onNextClicked();
                    return true;
                }
                if (menuItem.getItemId() != R.id.action_link || AccountView.this.mFacebookHelper == null) {
                    return false;
                }
                AccountView.this.mFacebookHelper.onLinkClicked();
                return false;
            }
        };
        inflate(context, R.layout.acct__widget_parent, this);
        setClipChildren(false);
        setClipToPadding(false);
        this.vSignInLayout = (SignInLayout) findViewById(R.id.parent_sign_in_layout);
        this.vEmailNameLayout = (EmailNameLayout) findViewById(R.id.parent_create_account_email_name_layout);
        this.vSinglePageSignUpLayout = (SinglePageSignUpLayout) findViewById(R.id.parent_create_account_single_page_layout);
        this.vFacebookAPIHostLayout = (FacebookAPIHostLayout) findViewById(R.id.parent_facebook_api_host_layout);
        this.vFacebookLayout = (FacebookLayout) findViewById(R.id.parent_facebook_layout);
        this.vPasswordLayout = (PasswordLayout) findViewById(R.id.parent_create_account_password_layout);
        this.vTOSLayout = (TOSLayout) findViewById(R.id.parent_create_account_tos_layout);
        this.vHeaderLayout = (HeaderLayout) findViewById(R.id.parent_user_image_presenter);
        this.vToolbar = (AnimatedIconToolbar) findViewById(R.id.toolbar);
        this.vWelcomeLayout = (WelcomeLayout) findViewById(R.id.welcome_loading_container);
        this.vSinglePageToolbar = (Toolbar) findViewById(R.id.single_page_toolbar);
        this.vToolbar.setNavigationOnClickListener(this.mNavigationClickListener);
        this.vToolbar.setOnMenuItemClickListener(this.mNextClickMenuListener);
        this.vToolbar.showNavigationIconAsX();
        this.vToolbar.inflateMenu(R.menu.acct__menu_account_creation);
        this.vSinglePageToolbar.setNavigationOnClickListener(this.mNavigationClickListener);
        this.vSinglePageToolbar.setNavigationContentDescription(R.string.acct__Toolbar_nav_close_icon_cont_desc);
        styleize(context, attributeSet);
        brandIt();
    }

    @SuppressLint({"CustomViewStyleable"})
    private void styleize(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.acct__AccountView);
            verifyRequiredAttrs(obtainStyledAttributes);
            this.mBrand = obtainStyledAttributes.getString(R.styleable.acct__AccountView_acct__brand);
            this.vSignInLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vEmailNameLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vFacebookLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vPasswordLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vTOSLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vHeaderLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vToolbar.styleizeFromAccountView(obtainStyledAttributes);
            this.vWelcomeLayout.styleizeFromAccountView(obtainStyledAttributes);
            this.vSinglePageSignUpLayout.styleizeFromAccountView(obtainStyledAttributes);
            obtainStyledAttributes.recycle();
        }
    }

    private void brandIt() {
        this.vSignInLayout.brandIt(this.mBrand);
        this.vEmailNameLayout.brandIt(this.mBrand);
        this.vFacebookLayout.brandIt(this.mBrand);
        this.vPasswordLayout.brandIt(this.mBrand);
        this.vHeaderLayout.brandIt(this.mBrand);
        this.vWelcomeLayout.brandIt(this.mBrand);
        this.vSinglePageSignUpLayout.brandIt(this.mBrand);
    }

    public String getBrand() {
        return this.mBrand;
    }

    private void verifyRequiredAttrs(TypedArray typedArray) {
        int[] iArr = {R.styleable.acct__AccountView_acct__logo_small_drawable, R.styleable.acct__AccountView_acct__logo_large_drawable, R.styleable.acct__AccountView_acct__logo_text_drawable, R.styleable.acct__AccountView_acct__brand};
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            if (!typedArray.hasValue(i)) {
                throw new RuntimeException(getResources().getResourceName(iArr[i]) + " is not defined");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void anchorUserImage(String str) {
        if (str == null) {
            this.vHeaderLayout.resetCenter();
            return;
        }
        if (str.equals(STATE_SIGN_IN)) {
            this.vHeaderLayout.anchorTo(this.vSignInLayout.getContent(), getResources().getDimension(R.dimen.acct__user_image_view_min_height_default));
            return;
        }
        if (str.equals(STATE_EMAIL_NAME)) {
            this.vHeaderLayout.anchorTo(this.vEmailNameLayout.getContent(), getResources().getDimension(R.dimen.acct__user_image_view_min_height_default));
            return;
        }
        if (str.equals(STATE_PASSWORD)) {
            this.vHeaderLayout.anchorTo(this.vPasswordLayout.getContent(), getResources().getDimension(R.dimen.acct__user_image_view_min_height_hi_name));
        } else if (str.equals(STATE_FACEBOOK)) {
            this.vHeaderLayout.anchorTo(this.vFacebookLayout.getContent(), getResources().getDimension(R.dimen.acct__user_image_view_min_height_default));
        } else {
            this.vHeaderLayout.resetCenter();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.expedia.account.presenter.Presenter, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        createAndAddTransitions();
        show(STATE_SIGN_IN);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        FacebookViewHelper facebookViewHelper = this.mFacebookHelper;
        if (facebookViewHelper != null) {
            facebookViewHelper.onActivityResult(i, i2, intent);
        }
    }

    private void createAndAddTransitions() {
        addDefaultTransition(new Presenter.DefaultCompoundTransition(STATE_SIGN_IN, this.vHeaderLayout.getDefaultTransition(), new Presenter.Transition() { // from class: com.expedia.account.AccountView.5
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AccountView.this.vSignInLayout.enableButtons();
                AccountView.this.vSignInLayout.setVisibility(0);
                AccountView.this.vEmailNameLayout.setVisibility(4);
                AccountView.this.vSinglePageSignUpLayout.setVisibility(4);
                AccountView.this.vSinglePageToolbar.setVisibility(4);
                AccountView.this.vFacebookAPIHostLayout.setVisibility(4);
                AccountView.this.vFacebookLayout.setVisibility(4);
                AccountView.this.vPasswordLayout.setVisibility(4);
                AccountView.this.vTOSLayout.setVisibility(4);
                AccountView.this.vToolbar.showNavigationIconAsX();
                AccountView.this.anchorUserImage(AccountView.STATE_SIGN_IN);
                Events.post(new Events.OverallProgress(true, 0.0f));
                Events.post(new Events.ObscureBackgroundDesired(0.0f));
            }
        }));
        addTransition(new CompoundTransition(STATE_SIGN_IN, STATE_EMAIL_NAME, new ReportProgressTransition(0.0f, 0.33f), new LeftToRightTransition(this, SignInLayout.class.getName(), EmailNameLayout.class.getName()), new FadeTransition(this.vSignInLayout, this.vEmailNameLayout), new Presenter.Transition() { // from class: com.expedia.account.AccountView.6
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.vSignInLayout.suppressCurrentErrors();
                    AccountView.this.anchorUserImage(AccountView.STATE_EMAIL_NAME);
                    if (AccountView.STATE_EMAIL_NAME.equals(AccountView.this.mConfig.initialState)) {
                        return;
                    }
                    AccountView.this.vToolbar.showNavigationIconAsBack();
                    return;
                }
                AccountView.this.vHeaderLayout.showSpecialMessage(true);
                AccountView.this.anchorUserImage(AccountView.STATE_SIGN_IN);
                AccountView.this.vToolbar.showNavigationIconAsX();
                AccountView.this.hideMenu();
                Utils.hideKeyboard(AccountView.this);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AnalyticsListener analyticsListener;
                if (z) {
                    AccountView.this.anchorUserImage(AccountView.STATE_EMAIL_NAME);
                    if (AccountView.this.mConfig != null && (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) != null) {
                        analyticsListener.userViewedNameEntering();
                    }
                    AccountView.this.showMenuNext();
                    AccountView.this.vEmailNameLayout.setNextButtonController(AccountView.this.mNextButtonController);
                    AccountView.this.vSignInLayout.suppressCurrentErrors();
                    AccountView.this.postDelayed(new Runnable() { // from class: com.expedia.account.AccountView.6.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (AccessibilityUtil.isTalkbackEnabled(AccountView.this.getContext())) {
                                AccessibilityUtil.setFocusToToolBarUpIcon(AccountView.this.vToolbar);
                                AccountView.this.vEmailNameLayout.setAccessibilityTraversalAfter(R.id.parent_user_image_presenter);
                                AccountView.this.vEmailNameLayout.setAccessibilityLiveRegion(2);
                                AccountView.this.vEmailNameLayout.announceForAccessibility(AccountView.this.mConfig.signupString);
                                return;
                            }
                            AccountView.this.vEmailNameLayout.focusEmailAddress();
                        }
                    }, 50L);
                    return;
                }
                AccountView.this.vSignInLayout.enableButtons();
                AccountView.this.anchorUserImage(AccountView.STATE_SIGN_IN);
            }
        }));
        addTransition(new CompoundTransition(STATE_SIGN_IN, STATE_SINGLE_PAGE_SIGN_UP, getResources().getInteger(R.integer.acct__single_page_sliding_duration), new SlideInBottomTransition(this.vSinglePageSignUpLayout), new SlideInBottomTransition(this.vSinglePageToolbar), new SlideUpTransition(this.vHeaderLayout), new SlideUpTransition(this.vSignInLayout), new FadeTransition(this.vSignInLayout, null), new Presenter.Transition() { // from class: com.expedia.account.AccountView.7
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.vSignInLayout.suppressCurrentErrors();
                    AccountView.this.vToolbar.setVisibility(4);
                } else {
                    AccountView.this.vHeaderLayout.showSpecialMessage(true);
                    Utils.hideKeyboard(AccountView.this);
                    AccountView.this.vSinglePageWhiteBackground.setVisibility(8);
                    AccountView.this.vSinglePageSignUpLayout.removeKeyboardChangeListener();
                }
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AnalyticsListener analyticsListener;
                if (z) {
                    if (AccountView.this.mConfig != null && (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) != null) {
                        analyticsListener.userViewedSinglePage();
                    }
                    AccountView.this.vSignInLayout.suppressCurrentErrors();
                    AccountView.this.setAccessibilityFocus();
                    AccountView.this.vSinglePageWhiteBackground.setVisibility(0);
                    AccountView.this.vSinglePageSignUpLayout.addKeyboardChangeListener();
                    AccessibilityUtil.setFocusToToolBarUpIcon(AccountView.this.vSinglePageToolbar);
                    return;
                }
                AccountView.this.vToolbar.setVisibility(0);
                AccountView.this.vSignInLayout.enableButtons();
            }
        }));
        addTransition(new CompoundTransition(STATE_SINGLE_PAGE_SIGN_UP, STATE_LOADING_SINGLE_PAGE, getResources().getInteger(R.integer.acct__single_page_sliding_duration), this.vHeaderLayout.getLogoToLoadingSignInTransition(), new OffScreenBottomTransition(this.vSinglePageSignUpLayout), new OffScreenBottomTransition(this.vSinglePageToolbar), new Presenter.Transition() { // from class: com.expedia.account.AccountView.8
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.anchorUserImage(z ? AccountView.STATE_LOADING_ACCOUNT : AccountView.STATE_SINGLE_PAGE_SIGN_UP);
                if (z) {
                    Utils.hideKeyboard(AccountView.this);
                    AccountView.this.vSinglePageWhiteBackground.setVisibility(8);
                    AccountView.this.vSinglePageSignUpLayout.removeKeyboardChangeListener();
                }
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AnalyticsListener analyticsListener;
                if (z) {
                    return;
                }
                if (AccountView.this.mConfig != null && (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) != null) {
                    analyticsListener.userViewedSinglePage();
                }
                AccountView.this.vSinglePageWhiteBackground.setVisibility(0);
                AccountView.this.vSinglePageSignUpLayout.addKeyboardChangeListener();
            }
        }));
        addTransition(new CompoundTransition(STATE_SIGN_IN, STATE_FACEBOOK_API_HOST, new ReportProgressTransition(0.0f, 0.33f), new LeftToRightTransition(this, SignInLayout.class.getName(), FacebookAPIHostLayout.class.getName()), new Presenter.Transition() { // from class: com.expedia.account.AccountView.9
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.vFacebookAPIHostLayout.setTranslationY(0.0f);
                    AccountView.this.vFacebookAPIHostLayout.setMessage(R.string.acct__fb_attempting_sign_in);
                    AccountView.this.vSignInLayout.suppressCurrentErrors();
                    if (!AccountView.STATE_FACEBOOK_API_HOST.equals(AccountView.this.mConfig.initialState)) {
                        AccountView.this.vToolbar.showNavigationIconAsBack();
                    }
                } else {
                    AccountView.this.vToolbar.showNavigationIconAsX();
                    AccountView.this.hideMenu();
                }
                Utils.hideKeyboard(AccountView.this);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                if (z) {
                    if (AccountView.this.mFacebookHelper != null) {
                        AccountView.this.mFacebookHelper.doFacebookLogin();
                        return;
                    }
                    return;
                }
                AccountView.this.vSignInLayout.enableButtons();
            }
        }));
        addTransition(new CompoundTransition(STATE_FACEBOOK_API_HOST, STATE_FACEBOOK, new ReportProgressTransition(0.33f, 0.67f), new LeftToRightTransition(this, FacebookAPIHostLayout.class.getName(), FacebookLayout.class.getName()), new Presenter.Transition() { // from class: com.expedia.account.AccountView.10
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.anchorUserImage(AccountView.STATE_FACEBOOK);
                }
                Utils.hideKeyboard(AccountView.this);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                if (z) {
                    AccountView.this.showMenuLink();
                    AccountView.this.vFacebookLayout.setLinkButtonController(AccountView.this.mLinkButtonController);
                }
            }
        }));
        addTransition(new CompoundTransition(STATE_FACEBOOK_API_HOST, STATE_LOADING_FACEBOOK, new ReportProgressTransition(0.33f, 0.67f), this.vHeaderLayout.getLogoToLoadingSignInTransition(), new OffScreenBottomTransition(this.vFacebookAPIHostLayout), new Presenter.Transition() { // from class: com.expedia.account.AccountView.11
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.anchorUserImage(z ? AccountView.STATE_LOADING_FACEBOOK : AccountView.STATE_FACEBOOK_API_HOST);
            }
        }));
        addTransition(new CompoundTransition(STATE_FACEBOOK, STATE_LOADING_FACEBOOK, this.vHeaderLayout.getLogoToLoadingSignInTransition(), new OffScreenBottomTransition(this.vFacebookLayout), new Presenter.Transition() { // from class: com.expedia.account.AccountView.12
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.hideMenu();
                }
                AccountView.this.anchorUserImage(z ? AccountView.STATE_LOADING_FACEBOOK : AccountView.STATE_FACEBOOK);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                super.finalizeTransition(z);
                if (z) {
                    return;
                }
                AccountView.this.showMenuLink();
            }
        }));
        addTransition(new CompoundTransition(STATE_FACEBOOK, STATE_SIGN_IN, new ReportProgressTransition(0.67f, 0.0f), new RightToLeftTransition(this, FacebookLayout.class.getName(), SignInLayout.class.getName()), new Presenter.Transition() { // from class: com.expedia.account.AccountView.13
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.vToolbar.showNavigationIconAsX();
                AccountView.this.anchorUserImage(AccountView.STATE_SIGN_IN);
                AccountView.this.hideMenu();
                Utils.hideKeyboard(AccountView.this);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                if (z) {
                    AccountView.this.vSignInLayout.enableButtons();
                }
            }
        }));
        addTransition(new CompoundTransition(STATE_SIGN_IN, STATE_LOADING_FACEBOOK, new ReportProgressTransition(0.0f, 0.67f), this.vHeaderLayout.getLogoToLoadingSignInTransition(), new OffScreenBottomTransition(this.vSignInLayout), new Presenter.Transition() { // from class: com.expedia.account.AccountView.14
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.anchorUserImage(AccountView.STATE_SIGN_IN);
                AccountView.this.vToolbar.showNavigationIconAsX();
                if (z) {
                    return;
                }
                AccountView.this.vSignInLayout.enableButtons();
            }
        }));
        addTransition(new CompoundTransition(STATE_EMAIL_NAME, STATE_PASSWORD, new ReportProgressTransition(0.33f, 0.67f), new LeftToRightTransition(this, EmailNameLayout.class.getName(), PasswordLayout.class.getName()), new FadeTransition(this.vEmailNameLayout, this.vPasswordLayout), this.vHeaderLayout.getLogoToSmallTransition(), new Presenter.Transition() { // from class: com.expedia.account.AccountView.15
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.anchorUserImage(AccountView.STATE_PASSWORD);
                    AccountView.this.vPasswordLayout.setNextButtonController(AccountView.this.mNextButtonController);
                    AccountView.this.vPasswordLayout.requestFocus(z);
                    if (AccountView.STATE_EMAIL_NAME.equals(AccountView.this.mConfig.initialState)) {
                        AccountView.this.vToolbar.showNavigationIconAsBack();
                    }
                } else {
                    AccountView.this.anchorUserImage(AccountView.STATE_EMAIL_NAME);
                    AccountView.this.vEmailNameLayout.setNextButtonController(AccountView.this.mNextButtonController);
                    AccountView.this.vEmailNameLayout.requestFocus(z);
                    if (AccountView.STATE_EMAIL_NAME.equals(AccountView.this.mConfig.initialState)) {
                        AccountView.this.vToolbar.showNavigationIconAsX();
                    }
                }
                AccessibilityUtil.setFocusToToolBarUpIcon(AccountView.this.vToolbar);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AnalyticsListener analyticsListener;
                AnalyticsListener analyticsListener2;
                super.finalizeTransition(z);
                if (z) {
                    AccountView.this.anchorUserImage(AccountView.STATE_PASSWORD);
                    if (AccountView.this.mConfig == null || (analyticsListener2 = AccountView.this.mConfig.getAnalyticsListener()) == null) {
                        return;
                    }
                    analyticsListener2.userViewedPasswordEntering();
                    return;
                }
                AccountView.this.anchorUserImage(AccountView.STATE_EMAIL_NAME);
                if (AccountView.this.mConfig == null || (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) == null) {
                    return;
                }
                analyticsListener.userViewedNameEntering();
            }
        }));
        addTransition(new CompoundTransition(STATE_PASSWORD, STATE_TOS, new ReportProgressTransition(0.67f, 1.0f), new LeftToRightTransition(this, PasswordLayout.class.getName(), TOSLayout.class.getName()), new FadeTransition(this.vPasswordLayout, this.vTOSLayout), this.vHeaderLayout.getSmallToLargeTransition(), new Presenter.Transition() { // from class: com.expedia.account.AccountView.16
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                if (z) {
                    AccountView.this.anchorUserImage(AccountView.STATE_TOS);
                    AccountView.this.hideMenu();
                    Utils.hideKeyboard(AccountView.this);
                    AccessibilityUtil.setFocusToToolBarUpIcon(AccountView.this.vToolbar);
                    AccountView.this.vHeaderLayout.setImportantForAccessibility(4);
                    return;
                }
                AccountView.this.anchorUserImage(AccountView.STATE_PASSWORD);
                AccountView.this.vPasswordLayout.requestFocus(true);
                AccessibilityUtil.setFocusToToolBarUpIcon(AccountView.this.vToolbar);
                AccountView.this.vHeaderLayout.setImportantForAccessibility(1);
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AnalyticsListener analyticsListener;
                AnalyticsListener analyticsListener2;
                if (z) {
                    AccountView.this.anchorUserImage(AccountView.STATE_TOS);
                    if (AccountView.this.mConfig == null || (analyticsListener2 = AccountView.this.mConfig.getAnalyticsListener()) == null) {
                        return;
                    }
                    analyticsListener2.userViewedTosPage();
                    return;
                }
                AccountView.this.anchorUserImage(AccountView.STATE_PASSWORD);
                AccountView.this.showMenuNext();
                if (AccountView.this.mConfig == null || (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) == null) {
                    return;
                }
                analyticsListener.userViewedPasswordEntering();
            }
        }));
        addTransition(new CompoundTransition(STATE_TOS, STATE_SIGN_IN, new ReportProgressTransition(1.0f, 0.0f), new RightToLeftTransition(this, TOSLayout.class.getName(), SignInLayout.class.getName()), new FadeTransition(this.vTOSLayout, this.vSignInLayout), this.vHeaderLayout.getLargeToLogoTransition(), new Presenter.Transition() { // from class: com.expedia.account.AccountView.17
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.anchorUserImage(z ? AccountView.STATE_SIGN_IN : AccountView.STATE_TOS);
                PartialUser newUser = Db.getNewUser();
                AccountView.this.vSignInLayout.populate(newUser.email, newUser.password);
                AccountView.this.vSignInLayout.focusPassword();
                AccountView.this.vToolbar.showNavigationIconAsX();
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AccountView.this.vSignInLayout.enableButtons();
                AccountView.this.anchorUserImage(z ? AccountView.STATE_SIGN_IN : AccountView.STATE_TOS);
            }
        }));
        addTransition(new CompoundTransition(STATE_TOS, STATE_EMAIL_NAME, new ReportProgressTransition(1.0f, 0.33f), new RightToLeftTransition(this, TOSLayout.class.getName(), EmailNameLayout.class.getName()), new FadeTransition(this.vTOSLayout, this.vEmailNameLayout), this.vHeaderLayout.getLargeToLogoTransition(), new Presenter.Transition() { // from class: com.expedia.account.AccountView.18
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AccountView.this.anchorUserImage(z ? AccountView.STATE_EMAIL_NAME : AccountView.STATE_TOS);
                if (AccountView.this.mFocusEmailAddress) {
                    AccountView.this.vEmailNameLayout.focusEmailAddress();
                } else if (AccountView.this.mFocusFirstName) {
                    AccountView.this.vEmailNameLayout.focusFirstName();
                } else if (AccountView.this.mFocusLastName) {
                    AccountView.this.vEmailNameLayout.focusLastName();
                }
                AccountView.this.mFocusEmailAddress = false;
                AccountView.this.mFocusFirstName = false;
                AccountView.this.mFocusLastName = false;
            }
        }));
        addTransition(new CompoundTransition(STATE_SIGN_IN, STATE_LOADING_SIGN_IN, this.vHeaderLayout.getLogoToLoadingSignInTransition(), new OffScreenBottomTransition(this.vSignInLayout), new Presenter.Transition() { // from class: com.expedia.account.AccountView.19
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.anchorUserImage(z ? AccountView.STATE_LOADING_SIGN_IN : AccountView.STATE_SIGN_IN);
                if (!z) {
                    AccountView.this.vToolbar.showNavigationIconAsX();
                } else {
                    Utils.hideKeyboard(AccountView.this);
                    AccountView.this.vToolbar.showNavigationIconAsBack();
                }
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                if (z) {
                    return;
                }
                AccountView.this.vSignInLayout.enableButtons();
            }
        }));
        addTransition(new CompoundTransition(STATE_TOS, STATE_LOADING_ACCOUNT, this.vHeaderLayout.getLargeToLoadingTransition(), new OffScreenBottomTransition(this.vTOSLayout), new Presenter.Transition() { // from class: com.expedia.account.AccountView.20
            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                AccountView.this.anchorUserImage(z ? AccountView.STATE_LOADING_ACCOUNT : AccountView.STATE_TOS);
            }
        }));
        Presenter.Transition transition = new Presenter.Transition() { // from class: com.expedia.account.AccountView.21
            float welcomeEndAlpha;
            float welcomeStartAlpha;

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void startTransition(boolean z) {
                this.welcomeStartAlpha = z ? 0.0f : 1.0f;
                this.welcomeEndAlpha = z ? 1.0f : 0.0f;
                AccountView.this.vWelcomeLayout.setVisibility(0);
                AccountView.this.vWelcomeLayout.setAlpha(this.welcomeStartAlpha);
                AccountView.this.vWelcomeLayout.setTranslationY(AccountView.this.vHeaderLayout.getTrueBottom());
                AccountView.this.vToolbar.setVisibility(8);
                AccountView accountView = AccountView.this;
                accountView.anchorUserImage(accountView.getCurrentState());
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void updateTransition(float f, boolean z) {
                AccountView.this.vWelcomeLayout.setAlpha(PresenterUtils.calculateStep(this.welcomeStartAlpha, this.welcomeEndAlpha, f));
            }

            @Override // com.expedia.account.presenter.Presenter.Transition
            public void finalizeTransition(boolean z) {
                AccountView.this.vWelcomeLayout.setAlpha(this.welcomeEndAlpha);
                if (z) {
                    AccountView.this.vWelcomeLayout.setVisibility(0);
                    AccountView.this.doSignInSuccessful();
                } else {
                    AccountView.this.vWelcomeLayout.setVisibility(8);
                }
            }
        };
        addTransition(new CompoundTransition(STATE_LOADING_SIGN_IN, STATE_WELCOME, DateTimeConstants.MILLIS_PER_SECOND, this.vHeaderLayout.getLoadingSignInToDoneLoadingTransition(), transition));
        addTransition(new CompoundTransition(STATE_LOADING_ACCOUNT, STATE_WELCOME, DateTimeConstants.MILLIS_PER_SECOND, this.vHeaderLayout.getLoadingAccountToDoneLoadingTransition(), transition));
        addTransition(new CompoundTransition(STATE_LOADING_SINGLE_PAGE, STATE_WELCOME, DateTimeConstants.MILLIS_PER_SECOND, this.vHeaderLayout.getLoadingSinglePageToDoneLoadingTransition(), transition));
        addTransition(new CompoundTransition(STATE_LOADING_FACEBOOK, STATE_WELCOME, DateTimeConstants.MILLIS_PER_SECOND, this.vHeaderLayout.getLoadingFacebookToDoneLoadingTransition(), transition));
    }

    public void up() {
        back();
    }

    @Override // com.expedia.account.presenter.Presenter
    public boolean back() {
        Config config;
        AccountSignInListener accountSignInListener;
        String currentState = getCurrentState();
        if (currentState != null) {
            if (this.mCurrentDownload != null && (STATE_LOADING_ACCOUNT.equals(currentState) || STATE_LOADING_SIGN_IN.equals(currentState))) {
                this.mCurrentDownload.dispose();
                this.mCurrentDownload = null;
            } else {
                if (STATE_WELCOME.equals(currentState)) {
                    return true;
                }
                if (STATE_FACEBOOK.equals(currentState)) {
                    Config config2 = this.mConfig;
                    if (config2 != null) {
                        config2.getService().facebookLogOut();
                    }
                    Config config3 = this.mConfig;
                    if (config3 != null && STATE_FACEBOOK_API_HOST.equals(config3.initialState)) {
                        clearBackStack();
                    } else {
                        show(STATE_SIGN_IN, 32768);
                        return true;
                    }
                }
            }
        }
        boolean back = super.back();
        if (!back && (config = this.mConfig) != null && (accountSignInListener = config.getAccountSignInListener()) != null) {
            accountSignInListener.onSignInCancelled();
        }
        return back;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ReportProgressTransition extends Presenter.Transition {
        private float mFrom;
        private float mTo;

        public ReportProgressTransition(float f, float f2) {
            super(null, null);
            this.mFrom = f;
            this.mTo = f2;
        }

        @Override // com.expedia.account.presenter.Presenter.Transition
        public void startTransition(boolean z) {
            super.startTransition(z);
            postProgress(z, z ? this.mFrom : this.mTo);
        }

        @Override // com.expedia.account.presenter.Presenter.Transition
        public void updateTransition(float f, boolean z) {
            float f2;
            float f3;
            super.updateTransition(f, z);
            if (z) {
                f2 = this.mTo;
                f3 = this.mFrom;
            } else {
                f2 = this.mFrom;
                f3 = this.mTo;
            }
            postProgress(z, ((f2 - f3) * f) + f3);
        }

        @Override // com.expedia.account.presenter.Presenter.Transition
        public void endTransition(boolean z) {
            super.endTransition(z);
            postProgress(z, z ? this.mTo : this.mFrom);
        }

        @Override // com.expedia.account.presenter.Presenter.Transition
        public void finalizeTransition(boolean z) {
            super.finalizeTransition(z);
            postProgress(z, z ? this.mTo : this.mFrom);
        }

        private void postProgress(boolean z, float f) {
            Events.post(new Events.OverallProgress(z, f));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSignInError(String str) {
        AnalyticsListener analyticsListener;
        Config config = this.mConfig;
        if (config != null && (analyticsListener = config.getAnalyticsListener()) != null) {
            analyticsListener.userReceivedErrorOnSignInAttempt("Account: " + str);
        }
        show(STATE_SIGN_IN, 67108864);
        showSignInErrorGeneric();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSignInError(AccountResponse accountResponse) {
        AnalyticsListener analyticsListener;
        Config config = this.mConfig;
        if (config != null && (analyticsListener = config.getAnalyticsListener()) != null) {
            analyticsListener.userReceivedErrorOnSignInAttempt("Account: " + accountResponse.getMostRelevantErrorCause());
        }
        show(STATE_SIGN_IN, 67108864);
        showSignInErrorGeneric(accountResponse.SignInFailureError());
    }

    private void showSignInErrorGeneric() {
        showSignInErrorGeneric(new AccountResponse().SignInFailureError());
    }

    private void showSignInErrorGeneric(AccountResponse.SignInError signInError) {
        int i;
        int i2;
        if (signInError == AccountResponse.SignInError.ACCOUNT_LOCKED) {
            i = R.string.acct__Sign_in_locked;
            i2 = R.string.acct__Sign_in_locked_TITLE;
        } else if (signInError == AccountResponse.SignInError.INVALID_CREDENTIALS) {
            i = R.string.acct__Sign_in_failed;
            i2 = R.string.acct__Sign_in_failed_TITLE;
        } else if (signInError == AccountResponse.SignInError.RECAPTCHA_TOKEN_MISSING) {
            i = R.string.acct__Sign_in_failed_generic;
            i2 = R.string.acct__Sign_in_failed_TITLE;
            Config config = this.mConfig;
            if (config != null && config.getAccountSignInListener() != null) {
                new ErrorReporting(this.mConfig.getAccountSignInListener()).reportRecaptchaError();
            }
        } else {
            i = R.string.acct__Sign_in_failed_generic;
            i2 = R.string.acct__Sign_in_failed_TITLE;
        }
        AndroidNetworkConnectivity androidNetworkConnectivity = new AndroidNetworkConnectivity((ConnectivityManager) getContext().getSystemService("connectivity"));
        c.a a2 = new c.a(getContext()).a(i2);
        if (!androidNetworkConnectivity.hasInternetCapability()) {
            i = R.string.acct__no_network_connection;
        }
        a2.b(i).a(android.R.string.ok, (DialogInterface.OnClickListener) null).b().show();
    }

    public void onFacebookError() {
        Config config = this.mConfig;
        if (config != null) {
            config.initialState = STATE_SIGN_IN;
            show(STATE_SIGN_IN, 32768);
        }
    }

    public void onFacebookCancel() {
        Config config = this.mConfig;
        if (config != null && STATE_FACEBOOK_API_HOST.equals(config.initialState)) {
            back();
        } else {
            show(STATE_SIGN_IN, 32768);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCreateAccountError(String str) {
        Config config = this.mConfig;
        if (config != null) {
            AnalyticsListener analyticsListener = config.getAnalyticsListener();
            if (analyticsListener != null) {
                analyticsListener.userReceivedErrorOnAccountCreationAttempt("Account: " + str);
            }
            show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        }
        showCreateAccountErrorGeneric();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCreateAccountError(AccountResponse accountResponse) {
        Log.e("ohno " + accountResponse);
        Config config = this.mConfig;
        if (config != null) {
            AnalyticsListener analyticsListener = config.getAnalyticsListener();
            if (analyticsListener != null) {
                analyticsListener.userReceivedErrorOnAccountCreationAttempt("Account: " + accountResponse.getMostRelevantErrorCause());
            }
            show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        }
        if (accountResponse != null) {
            if (accountResponse.hasError(AccountResponse.ErrorCode.EMAIL_PASSWORD_IDENTICAL_ERROR)) {
                showErrorPassword(AccountResponse.ErrorCode.EMAIL_PASSWORD_IDENTICAL_ERROR);
                return;
            }
            if (accountResponse.hasError(AccountResponse.ErrorCode.COMMON_PASSWORD_ERROR)) {
                showErrorPassword(AccountResponse.ErrorCode.COMMON_PASSWORD_ERROR);
                return;
            }
            if (accountResponse.hasError(AccountResponse.ErrorCode.USER_SERVICE_DUPLICATE_EMAIL)) {
                showErrorAccountExists();
                return;
            }
            if (accountResponse.hasError(AccountResponse.ErrorCode.INVALID_INPUT)) {
                int i = AnonymousClass31.$SwitchMap$com$expedia$account$data$AccountResponse$ErrorField[accountResponse.findError(AccountResponse.ErrorCode.INVALID_INPUT).errorInfo.field.ordinal()];
                if (i == 1) {
                    showErrorEmail();
                    return;
                }
                if (i == 2) {
                    showErrorPassword(AccountResponse.ErrorCode.INVALID_INPUT);
                    return;
                } else if (i == 3) {
                    showErrorFirstName();
                    return;
                } else if (i == 4) {
                    showErrorLastName();
                    return;
                }
            }
        }
        showCreateAccountErrorGeneric();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.expedia.account.AccountView$31, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass31 {
        static final /* synthetic */ int[] $SwitchMap$com$expedia$account$data$AccountResponse$ErrorField = new int[AccountResponse.ErrorField.values().length];

        static {
            try {
                $SwitchMap$com$expedia$account$data$AccountResponse$ErrorField[AccountResponse.ErrorField.email.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$expedia$account$data$AccountResponse$ErrorField[AccountResponse.ErrorField.password.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$expedia$account$data$AccountResponse$ErrorField[AccountResponse.ErrorField.firstName.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$expedia$account$data$AccountResponse$ErrorField[AccountResponse.ErrorField.lastName.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void showCreateAccountErrorGeneric() {
        new c.a(getContext()).a(R.string.acct__Create_account_failed_TITLE).b(new AndroidNetworkConnectivity((ConnectivityManager) getContext().getSystemService("connectivity")).hasInternetCapability() ? R.string.acct__Create_account_failed : R.string.acct__no_network_connection).a(android.R.string.ok, (DialogInterface.OnClickListener) null).b().show();
    }

    private void showErrorAccountExists() {
        Resources resources = getResources();
        CharSequence[] charSequenceArr = {resources.getString(R.string.acct__Sign_in_to_my_existing_account), resources.getString(R.string.acct__Create_a_new_account_with_different_email)};
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.expedia.account.AccountView.22
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AnalyticsListener analyticsListener;
                PartialUser newUser = Db.getNewUser();
                boolean z = false;
                boolean z2 = true;
                if (i == 0) {
                    newUser.password = null;
                    newUser.lastName = null;
                    newUser.firstName = null;
                    if (AccountView.this.mConfig != null) {
                        AccountView.this.mConfig.initialState = AccountView.STATE_SIGN_IN;
                    }
                    AccountView.this.show(AccountView.STATE_SIGN_IN, 32768);
                    z2 = false;
                    z = true;
                } else if (i != 1) {
                    z2 = false;
                } else {
                    newUser.email = null;
                    AccountView.this.show(AccountView.STATE_SINGLE_PAGE_SIGN_UP, 67108864);
                }
                if (AccountView.this.mConfig != null && (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) != null) {
                    analyticsListener.accountCreationAttemptWithPreexistingEmail(z, z2);
                }
                Events.post(new Events.PartialUserDataChanged());
            }
        };
        CharSequence a2 = Utils.obtainBrandedPhrase(getContext(), R.string.acct__Brand_account_already_exists_TITLE, this.mBrand).a();
        show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        new c.a(getContext()).a(a2).a(charSequenceArr, onClickListener).b().show();
    }

    private void showErrorEmail() {
        show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        this.mFocusEmailAddress = true;
        new c.a(getContext()).a(R.string.acct__Create_account_failed_TITLE).b(R.string.acct__invalid_email_address).a(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.expedia.account.AccountView.23
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).b().show();
    }

    private void showErrorPassword(AccountResponse.ErrorCode errorCode) {
        int i;
        show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.expedia.account.AccountView.24
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
            }
        };
        if (errorCode.equals(AccountResponse.ErrorCode.EMAIL_PASSWORD_IDENTICAL_ERROR)) {
            i = R.string.acct__email_password_identical;
        } else if (errorCode.equals(AccountResponse.ErrorCode.COMMON_PASSWORD_ERROR)) {
            i = R.string.acct__common_password;
        } else {
            i = R.string.acct__invalid_password;
        }
        new c.a(getContext()).a(R.string.acct__Create_account_failed_TITLE).b(i).a(android.R.string.ok, onClickListener).b().show();
    }

    private void showErrorFirstName() {
        show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        this.mFocusFirstName = true;
        new c.a(getContext()).a(R.string.acct__Create_account_failed_TITLE).b(R.string.acct__invalid_first_name).a(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.expedia.account.AccountView.25
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountView.this.show(AccountView.STATE_EMAIL_NAME, 67108864);
            }
        }).b().show();
    }

    private void showErrorLastName() {
        show(STATE_SINGLE_PAGE_SIGN_UP, 67108864);
        this.mFocusLastName = true;
        new c.a(getContext()).a(R.string.acct__Create_account_failed_TITLE).b(R.string.acct__invalid_last_name).a(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.expedia.account.AccountView.26
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountView.this.show(AccountView.STATE_EMAIL_NAME, 67108864);
            }
        }).b().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSignIn(String str, String str2, String str3) {
        show(STATE_LOADING_SIGN_IN);
        getService().signIn(str, str2, str3).subscribe(new t<AccountResponse>() { // from class: com.expedia.account.AccountView.27
            @Override // io.reactivex.t
            public void onComplete() {
                AccountView.this.mCurrentDownload = null;
            }

            @Override // io.reactivex.t
            public void onError(Throwable th) {
                AccountView.this.mCurrentDownload = null;
                AccountView.this.show(AccountView.STATE_SIGN_IN, 32768);
                AccountView.this.showSignInError("local");
                if (AccountView.this.mConfig == null || AccountView.this.mConfig.getAccountSignInListener() == null) {
                    return;
                }
                new ErrorReporting(AccountView.this.mConfig.getAccountSignInListener()).reportGenericSignInError("local", th);
            }

            @Override // io.reactivex.t
            public void onSubscribe(io.reactivex.a.c cVar) {
                AccountView.this.mCurrentDownload = cVar;
            }

            @Override // io.reactivex.t
            public void onNext(AccountResponse accountResponse) {
                AnalyticsListener analyticsListener;
                if (!accountResponse.success) {
                    AccountView.this.showSignInError(accountResponse);
                    if (AccountView.this.mConfig == null || AccountView.this.mConfig.getAccountSignInListener() == null) {
                        return;
                    }
                    new ErrorReporting(AccountView.this.mConfig.getAccountSignInListener()).reportGenericSignInErrorIfPresent(accountResponse);
                    return;
                }
                if (AccountView.this.mConfig != null && (analyticsListener = AccountView.this.mConfig.getAnalyticsListener()) != null) {
                    analyticsListener.signInSucceeded();
                }
                AccountView.this.show(AccountView.STATE_WELCOME);
            }
        });
    }

    public void doSignInSuccessful() {
        AccountSignInListener accountSignInListener;
        Config config = this.mConfig;
        if (config == null || (accountSignInListener = config.getAccountSignInListener()) == null) {
            return;
        }
        accountSignInListener.onSignInSuccessful();
    }

    public void doFacebookSignInSuccessful() {
        AccountSignInListener accountSignInListener;
        Config config = this.mConfig;
        if (config == null || (accountSignInListener = config.getAccountSignInListener()) == null) {
            return;
        }
        accountSignInListener.onFacebookSignInSuccess();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doCreateAccount(String str) {
        PartialUser newUser = Db.getNewUser();
        newUser.recaptchaResponseToken = str;
        getService().createUser(newUser).map(new g<AccountResponse, AccountResponse>() { // from class: com.expedia.account.AccountView.29
            @Override // io.reactivex.b.g
            public AccountResponse apply(AccountResponse accountResponse) {
                if (!TextUtils.isEmpty(accountResponse.tuid)) {
                    accountResponse.success = true;
                }
                return accountResponse;
            }
        }).subscribe(new t<AccountResponse>() { // from class: com.expedia.account.AccountView.28
            @Override // io.reactivex.t
            public void onComplete() {
                AccountView.this.mCurrentDownload = null;
            }

            @Override // io.reactivex.t
            public void onError(Throwable th) {
                AccountView.this.mCurrentDownload = null;
                AccountView.this.showCreateAccountError("local");
            }

            @Override // io.reactivex.t
            public void onSubscribe(io.reactivex.a.c cVar) {
                AccountView.this.mCurrentDownload = cVar;
            }

            @Override // io.reactivex.t
            public void onNext(AccountResponse accountResponse) {
                if (!accountResponse.success) {
                    AccountView.this.showCreateAccountError(accountResponse);
                } else {
                    AccountView.this.doCreateAccountSuccessful();
                    AccountView.this.show(AccountView.STATE_WELCOME);
                }
            }
        });
    }

    public void doCreateAccountSuccessful() {
        AnalyticsListener analyticsListener;
        Config config = this.mConfig;
        if (config == null || (analyticsListener = config.getAnalyticsListener()) == null) {
            return;
        }
        analyticsListener.userSucceededInCreatingAccount(Db.getNewUser().enrollInLoyalty);
    }

    /* loaded from: classes.dex */
    private class SigninHandler implements RecaptchaHandler {
        private String email;
        private String password;

        private SigninHandler(String str, String str2) {
            this.email = str;
            this.password = str2;
        }

        @Override // com.expedia.account.recaptcha.RecaptchaHandler
        public void onRecaptchaSuccess(String str) {
            AccountView.this.doSignIn(this.email, this.password, str);
        }

        @Override // com.expedia.account.recaptcha.RecaptchaHandler
        public void onRecaptchaFailure() {
            AccountView.this.showSignInError("recaptcha failure");
        }
    }

    /* loaded from: classes.dex */
    private class CreateAccountHandler implements RecaptchaHandler {
        private CreateAccountHandler() {
        }

        @Override // com.expedia.account.recaptcha.RecaptchaHandler
        public void onRecaptchaSuccess(String str) {
            AccountView.this.doCreateAccount(str);
        }

        @Override // com.expedia.account.recaptcha.RecaptchaHandler
        public void onRecaptchaFailure() {
            AccountView.this.showCreateAccountError("recaptcha failure");
        }
    }

    @h
    public void otto(Events.SignInButtonClicked signInButtonClicked) {
        AnalyticsListener analyticsListener = this.mConfig.getAnalyticsListener();
        if (analyticsListener != null) {
            analyticsListener.signInButtonClicked();
        }
        this.handleKeyBoardVisibilityChanges = false;
        if (this.mConfig.enableRecaptcha) {
            Recaptcha.recaptchaCheck((Activity) getContext(), this.mConfig.recaptchaAPIKey, new SigninHandler(signInButtonClicked.email, signInButtonClicked.password));
            this.vSignInLayout.enableButtons();
        } else {
            doSignIn(signInButtonClicked.email, signInButtonClicked.password, null);
            Log.i("RECAPTCHA", "Not Enabled -> From Sign In Button Click");
        }
        this.vHeaderLayout.showSpecialMessage(false);
    }

    @h
    public void otto(Events.SignInWithFacebookButtonClicked signInWithFacebookButtonClicked) {
        Config config = this.mConfig;
        if (config == null || config.getAccountSignInListener() == null) {
            return;
        }
        AnalyticsListener analyticsListener = this.mConfig.getAnalyticsListener();
        if (analyticsListener != null) {
            analyticsListener.facebookSignInButtonClicked();
        }
        show(STATE_FACEBOOK_API_HOST);
        this.vHeaderLayout.showSpecialMessage(false);
    }

    @h
    public void otto(Events.ForgotPasswordButtonClicked forgotPasswordButtonClicked) {
        AccountSignInListener accountSignInListener;
        Config config = this.mConfig;
        if (config == null || (accountSignInListener = config.getAccountSignInListener()) == null) {
            return;
        }
        accountSignInListener.onForgotPassword();
        this.vHeaderLayout.showSpecialMessage(false);
    }

    @h
    public void otto(Events.CreateAccountButtonClicked createAccountButtonClicked) {
        AnalyticsListener analyticsListener;
        show(STATE_SINGLE_PAGE_SIGN_UP);
        this.vHeaderLayout.showSpecialMessage(false);
        Config config = this.mConfig;
        if (config == null || (analyticsListener = config.getAnalyticsListener()) == null) {
            return;
        }
        analyticsListener.newCreateAccountTabClicked();
    }

    @h
    public void otto(Events.NextFromPasswordFired nextFromPasswordFired) {
        onNextClicked();
    }

    @h
    public void otto(Events.NextFromLastNameFired nextFromLastNameFired) {
        onNextClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNextClicked() {
        if (STATE_PASSWORD.equals(getCurrentState()) && this.vPasswordLayout.passwordsAreValid()) {
            Db.getNewUser().password = this.vPasswordLayout.getPassword();
            show(STATE_TOS);
        }
        if (STATE_EMAIL_NAME.equals(getCurrentState()) && this.vEmailNameLayout.everythingChecksOut()) {
            this.vEmailNameLayout.storeDataInNewUser();
            show(STATE_PASSWORD);
        }
    }

    @h
    public void otto(Events.LinkFromFacebookFired linkFromFacebookFired) {
        FacebookViewHelper facebookViewHelper = this.mFacebookHelper;
        if (facebookViewHelper != null) {
            facebookViewHelper.onLinkClicked();
        }
    }

    @h
    public void otto(Events.KeyBoardVisibilityChanged keyBoardVisibilityChanged) {
        if (this.handleKeyBoardVisibilityChanges) {
            this.vHeaderLayout.showSpecialMessage(!keyBoardVisibilityChanged.isVisible);
        }
        this.handleKeyBoardVisibilityChanges = true;
    }

    @h
    public void otto(Events.TOSContinueButtonClicked tOSContinueButtonClicked) {
        if (getCurrentState() == STATE_SINGLE_PAGE_SIGN_UP) {
            this.vSinglePageSignUpLayout.storeDataInNewUser();
            this.vHeaderLayout.showSpecialMessage(false);
            show(STATE_LOADING_SINGLE_PAGE);
        } else {
            show(STATE_LOADING_ACCOUNT);
        }
        if (this.mConfig.enableRecaptcha) {
            Recaptcha.recaptchaCheck((Activity) getContext(), this.mConfig.recaptchaAPIKey, new CreateAccountHandler());
        } else {
            doCreateAccount(null);
            Log.i("RECAPTCHA", "Not Enabled -> From TOS Button Click");
        }
    }

    @h
    public void otto(Events.ObscureBackgroundDesired obscureBackgroundDesired) {
        Config config = this.mConfig;
        if (config == null || config.background == null || this.mConfig.background.get() == null) {
            return;
        }
        this.mConfig.background.get().setObscure(obscureBackgroundDesired.amount);
    }

    @h
    public void otto(Events.OverallProgress overallProgress) {
        Config config = this.mConfig;
        if (config == null || config.background == null || this.mConfig.background.get() == null) {
            return;
        }
        this.mConfig.background.get().setPan(overallProgress.progress);
    }

    @h
    public void otto(Events.UserChangedSpamOptin userChangedSpamOptin) {
        AnalyticsListener analyticsListener;
        Config config = this.mConfig;
        if (config == null || (analyticsListener = config.getAnalyticsListener()) == null) {
            return;
        }
        analyticsListener.userExplicitlyModifiedMarketingOptIn(userChangedSpamOptin.wantsSpam);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideMenu() {
        Menu menu = this.vToolbar.getMenu();
        menu.findItem(R.id.action_next).setVisible(false);
        menu.findItem(R.id.action_link).setVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMenuNext() {
        Menu menu = this.vToolbar.getMenu();
        menu.findItem(R.id.action_next).setVisible(true);
        menu.findItem(R.id.action_link).setVisible(false);
        if (AccessibilityUtil.isTalkbackEnabled(getContext())) {
            menu.findItem(R.id.action_next).setTitle(getResources().getString(R.string.acct__NEXT_a11y_button));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMenuLink() {
        Menu menu = this.vToolbar.getMenu();
        menu.findItem(R.id.action_next).setVisible(false);
        menu.findItem(R.id.action_link).setVisible(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNextEnabled(Boolean bool) {
        this.vToolbar.getMenu().findItem(R.id.action_next).setEnabled(bool.booleanValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLinkEnabled(Boolean bool) {
        this.vToolbar.getMenu().findItem(R.id.action_link).setEnabled(bool.booleanValue());
    }

    public void setWhiteBackgroundFromActivity(View view) {
        this.vSinglePageWhiteBackground = view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAccessibilityFocus() {
        postDelayed(new Runnable() { // from class: com.expedia.account.AccountView.30
            @Override // java.lang.Runnable
            public void run() {
                if (AccessibilityUtil.isTalkbackEnabled(AccountView.this.getContext())) {
                    AccessibilityUtil.setFocusToToolBarUpIcon(AccountView.this.vToolbar);
                }
            }
        }, 50L);
    }
}
