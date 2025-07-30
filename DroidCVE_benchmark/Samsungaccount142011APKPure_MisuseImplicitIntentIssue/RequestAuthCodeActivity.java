package com.samsung.android.samsungaccount.authentication.ui.authcode;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.samsung.android.samsungaccount.authentication.data.DbManagerV2;
import com.samsung.android.samsungaccount.authentication.data.DeviceRegistrationManager;
import com.samsung.android.samsungaccount.authentication.data.OpenDBManager;
import com.samsung.android.samsungaccount.authentication.server.common.url.UrlManager;
import com.samsung.android.samsungaccount.authentication.server.vo.ChecklistStepUtil;
import com.samsung.android.samsungaccount.authentication.server.vo.ResultTncMandatoryUtilVO;
import com.samsung.android.samsungaccount.authentication.ui.authcode.AuthCodeResult;
import com.samsung.android.samsungaccount.authentication.ui.authcode.RequestAuthCodeActivity;
import com.samsung.android.samsungaccount.configuration.Config;
import com.samsung.android.samsungaccount.configuration.ErrorCode;
import com.samsung.android.samsungaccount.utils.LocalBusinessException;
import com.samsung.android.samsungaccount.utils.NetworkStateUtil;
import com.samsung.android.samsungaccount.utils.StateCheckUtil;
import com.samsung.android.samsungaccount.utils.base.AccountManagerUtil;
import com.samsung.android.samsungaccount.utils.base.StringUtils;
import com.samsung.android.samsungaccount.utils.log.SALog;
import com.samsung.android.samsungaccount.utils.ui.BaseAppCompatActivity;
import com.samsung.android.samsungaccount.utils.ui.IntentCreator;
import com.samsung.android.samsungaccount.utils.ui.RoundedCornerUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/* loaded from: classes2.dex */
public class RequestAuthCodeActivity extends BaseAppCompatActivity {
    private static final int MAX_AUTH_CODE_REQUEST_COUNT = 3;
    private static final String TAG = "RequestAuthCodeActivity";
    private int mAuthCodeRequestedCount;
    private String mCallingPackageName;
    private ChecklistStepUtil mChecklistStepUtil;
    private String mClientId;
    private Disposable mGetAuthCodeDisposable;
    private Intent mIntent;
    private boolean mIsCheckBasicProfile = true;
    private boolean mIsGdpr;
    private boolean mIsNeedToServerCheck;
    private boolean mNeedToShowDisclaimer;
    private String mPrompt;
    private String mScope;
    private String mState;
    private Disposable mTncMandatoryDisposable;

    /* renamed from: com.samsung.android.samsungaccount.authentication.ui.authcode.RequestAuthCodeActivity$1, reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        public static final /* synthetic */ int[] $SwitchMap$com$samsung$android$samsungaccount$authentication$data$DbManagerV2$DataState;

        static {
            int[] iArr = new int[DbManagerV2.DataState.values().length];
            $SwitchMap$com$samsung$android$samsungaccount$authentication$data$DbManagerV2$DataState = iArr;
            try {
                iArr[DbManagerV2.DataState.INVALID.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$samsung$android$samsungaccount$authentication$data$DbManagerV2$DataState[DbManagerV2.DataState.BUSY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public enum RequestCode {
        EMAIL_VALIDATION_VIEW,
        NAME_VALIDATION_VIEW,
        TNC,
        ACCOUNT_INFO,
        AGREE_TO_DISCLAIMER,
        RESIGN_IN,
        SIGN_IN
    }

    private boolean checkTaskPrerequisites() {
        SALog.i(TAG, "checkTaskPrerequisites");
        if (!AccountManagerUtil.getInstance().isSamsungAccountSignedIn(this)) {
            SALog.i(TAG, "Samsung Account doesn't exist");
            startSignIn();
            return false;
        }
        int i = AnonymousClass1.$SwitchMap$com$samsung$android$samsungaccount$authentication$data$DbManagerV2$DataState[DbManagerV2.getDataState(this).ordinal()];
        if (i == 1) {
            SALog.i(TAG, "DataState is invalid");
            startReSignIn();
            return false;
        }
        if (i != 2) {
            if (NetworkStateUtil.checkState(this)) {
                return true;
            }
            SALog.i(TAG, "Network is unavailable");
            setFailedResult(ErrorCode.SAC.NETWORK_ERROR, "Network is not available");
            return false;
        }
        SALog.i(TAG, "DataState is busy");
        Toast.makeText(this, StringUtils.getFailedMsgId(), 1).show();
        finish();
        return false;
    }

    private void disposeObservableIfRunning() {
        Disposable disposable = this.mGetAuthCodeDisposable;
        if (disposable != null && !disposable.isDisposed()) {
            this.mGetAuthCodeDisposable.dispose();
            this.mGetAuthCodeDisposable = null;
        }
        Disposable disposable2 = this.mTncMandatoryDisposable;
        if (disposable2 == null || disposable2.isDisposed()) {
            return;
        }
        this.mTncMandatoryDisposable.dispose();
        this.mTncMandatoryDisposable = null;
    }

    private void getParamsFromIntent() {
        SALog.i(TAG, "getParamsFromIntent");
        this.mClientId = this.mIntent.getStringExtra("client_id");
        this.mScope = this.mIntent.getStringExtra("scope");
        this.mState = this.mIntent.getStringExtra("state");
        this.mPrompt = this.mIntent.getStringExtra(Config.InterfaceKey.KEY_EXTERNAL_PROMPT);
        this.mNeedToShowDisclaimer = !"none".equals(r1);
        boolean z = StateCheckUtil.isGoogleLinked(this) || StateCheckUtil.isWeChatLinked(this);
        if (z) {
            this.mIsCheckBasicProfile = this.mIntent.getBooleanExtra(Config.InterfaceKey.KEY_EXTERNAL_CHECK_BASIC_PROFILE, false);
        }
        this.mIsNeedToServerCheck = z && this.mIsCheckBasicProfile && TextUtils.isEmpty(DbManagerV2.getBirthDate(this));
        this.mCallingPackageName = getCallingPackageName(this.mIntent);
        SALog.d(TAG, this.mIntent.getExtras() != null ? this.mIntent.getExtras().toString() : "Extra is null");
        SALog.d(TAG, "mNeedToShowDisclaimer : " + this.mNeedToShowDisclaimer);
        SALog.d(TAG, "mIsNeedToServerCheck : " + this.mIsNeedToServerCheck);
        SALog.d(TAG, "mIsCheckBasicProfile : " + this.mIsCheckBasicProfile);
    }

    private void handleActivityResultOk(RequestCode requestCode) {
        SALog.i(TAG, "handleActivityResultOk");
        if (RequestCode.AGREE_TO_DISCLAIMER.equals(requestCode)) {
            runGetAuthCodeRequest();
        } else {
            startProperActivityByChecklist();
        }
    }

    private void handleAuthCodeRequestFailed(String str, String str2) {
        if (ErrorCode.SAC.DISCLAIMER_AGREEMENT_IS_NOT_COMPLETED.equals(str)) {
            if ("none".equals(this.mPrompt)) {
                setFailedResult(str, str2);
                return;
            }
            if (this.mNeedToShowDisclaimer) {
                SALog.i(TAG, "Checklist is valid but server responded DISCLAIMER_AGREEMENT error, show disclaimer one last time");
                startAgreeToDisclaimerActivity();
                return;
            } else if (this.mAuthCodeRequestedCount < 3) {
                SALog.i(TAG, "retry AuthCodeTask. AuthCodeRequestedCount : " + this.mAuthCodeRequestedCount);
                runGetAuthCodeRequest();
                return;
            }
        }
        setFailedResult(str, str2);
    }

    private void handleAuthCodeRequestSuccess(AuthCodeResult authCodeResult) {
        SALog.d(TAG, "authCodeResult: " + authCodeResult.toString());
        String serverUrl = UrlManager.getServerUrl(this, "API_SERVER");
        String serverUrl2 = UrlManager.getServerUrl(this, "AUTH_SERVER");
        SALog.i(TAG, "apiServer : " + serverUrl + ", authServer : " + serverUrl2);
        Intent intent = new Intent();
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_AUTHCODE, authCodeResult.getAuthCode());
        intent.putExtra("code_expires_in", authCodeResult.getAuthCodeExpiration());
        if (!TextUtils.isEmpty(authCodeResult.getIdToken())) {
            intent.putExtra("id_token", authCodeResult.getIdToken());
        }
        intent.putExtra("api_server_url", serverUrl);
        intent.putExtra("auth_server_url", serverUrl2);
        if (!TextUtils.isEmpty(this.mState) && !TextUtils.isEmpty(authCodeResult.getState())) {
            intent.putExtra("state", authCodeResult.getState());
        }
        setResultWithLog(-1, intent);
        finish();
    }

    private void handleTncMandatoryRequestFailed(ResultTncMandatoryUtilVO resultTncMandatoryUtilVO) {
        this.mChecklistStepUtil = resultTncMandatoryUtilVO.getCheckListUtil();
        if (resultTncMandatoryUtilVO.getPreProcessResult() != null) {
            if (resultTncMandatoryUtilVO.getPreProcessResult().getCheckListResult() != null) {
                this.mIsGdpr = resultTncMandatoryUtilVO.getPreProcessResult().getCheckListResult().isGDPRCountry();
            }
            startProperActivityByChecklist();
            return;
        }
        setFailedResult(resultTncMandatoryUtilVO.getErrorCode(), resultTncMandatoryUtilVO.getErrorMessage());
    }

    private void handleTncMandatoryRequestSuccess() {
        if (isChecklistValid()) {
            runGetAuthCodeRequest();
        } else {
            startProperActivityByChecklist();
        }
    }

    private void initAuthCodeRequestCount() {
        this.mAuthCodeRequestedCount = 0;
    }

    private boolean isActivityCalledProperly() {
        SALog.i(TAG, "isActivityCalledProperly");
        if (this.mIntent == null) {
            setFailedResult(ErrorCode.SAC.INVALID_PARAM, Config.RESPONSE_ERROR_MESSAGE.INVALID_PARAM);
            return false;
        }
        if (getCallingActivity() == null) {
            setFailedResult(ErrorCode.SAC.NOT_CALLED_FROM_ACTIVITY, Config.RESPONSE_ERROR_MESSAGE.NOT_CALLED_FROM_ACTIVITY);
            return false;
        }
        if (DeviceRegistrationManager.isDeviceIdNull(this)) {
            SALog.i(TAG, "DeviceId is null");
            setFailedResult(ErrorCode.SAC.DEVICE_ID_IS_NULL_ERROR, Config.RESPONSE_ERROR_MESSAGE.DEVICEID_IS_NULL_ERROR);
            return false;
        }
        if (TextUtils.isEmpty(this.mIntent.getStringExtra("client_id"))) {
            SALog.i(TAG, "Empty client id");
            setFailedResult(ErrorCode.SAC.INVALID_PARAM, String.format(Config.RESPONSE_ERROR_MESSAGE.INVALID_PARAM, "client_id"));
            return false;
        }
        if (this.mIntent.hasExtra(Config.InterfaceKey.KEY_EXTERNAL_PROMPT)) {
            String stringExtra = this.mIntent.getStringExtra(Config.InterfaceKey.KEY_EXTERNAL_PROMPT);
            if (!"consent".equals(stringExtra) && !"none".equals(stringExtra)) {
                SALog.i(TAG, "Invalid prompt type : " + stringExtra + ", it should be \"consent\" or \"none\" or null");
                setFailedResult(ErrorCode.SAC.INVALID_PROMPT, Config.RESPONSE_ERROR_MESSAGE.INVALID_PROMPT);
                return false;
            }
        }
        return true;
    }

    private boolean isChecklistValid() {
        boolean z = OpenDBManager.getCheckListFromOpenDB(this, this.mClientId) == 0;
        SALog.i(TAG, "isChecklistValid : " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0() {
        setFailedResult(ErrorCode.SAC.SIGNATURE_FAIL_ERROR, "The signature of this application is not registered with the server.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runGetAuthCodeRequest$3() {
        SALog.i(TAG, "runGetAuthCodeRequest - doOnDispose");
        dismissProgressDialog();
        setResultWithLog(0);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runGetAuthCodeRequest$4(AuthCodeResult authCodeResult) {
        SALog.i(TAG, "runGetAuthCodeRequest - onSuccess");
        dismissProgressDialog();
        handleAuthCodeRequestSuccess(authCodeResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runGetAuthCodeRequest$5(Throwable th) {
        dismissProgressDialog();
        String message = th.getMessage();
        String message2 = th.getCause() == null ? "" : th.getCause().getMessage();
        SALog.i(TAG, "runGetAuthCodeRequest - onError : " + message);
        handleAuthCodeRequestFailed(message, message2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runTncMandatoryRequest$1() {
        SALog.i(TAG, "runTncMandatoryRequest - doOnDispose");
        dismissProgressDialog();
        setResultWithLog(0);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runTncMandatoryRequest$2(ResultTncMandatoryUtilVO resultTncMandatoryUtilVO) {
        SALog.i(TAG, "runTncMandatoryRequest - isSuccess : " + resultTncMandatoryUtilVO.isSuccess());
        dismissProgressDialog();
        if (resultTncMandatoryUtilVO.isSuccess()) {
            handleTncMandatoryRequestSuccess();
        } else {
            handleTncMandatoryRequestFailed(resultTncMandatoryUtilVO);
        }
    }

    private void runGetAuthCodeRequest() {
        SALog.i(TAG, "runGetAuthCodeRequest");
        disposeObservableIfRunning();
        this.mGetAuthCodeDisposable = new GetAuthCodeRequest(this, this.mClientId, this.mCallingPackageName, this.mScope, this.mState).getAuthCodeObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnDispose(new Action() { // from class: dn0
            @Override // io.reactivex.functions.Action
            public final void run() {
                RequestAuthCodeActivity.this.lambda$runGetAuthCodeRequest$3();
            }
        }).subscribe(new Consumer() { // from class: gn0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                RequestAuthCodeActivity.this.lambda$runGetAuthCodeRequest$4((AuthCodeResult) obj);
            }
        }, new Consumer() { // from class: hn0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                RequestAuthCodeActivity.this.lambda$runGetAuthCodeRequest$5((Throwable) obj);
            }
        });
        this.mAuthCodeRequestedCount++;
        SALog.i(TAG, "mAuthCodeRequestedCount : " + this.mAuthCodeRequestedCount);
        showProgressDialog(true, this.mGetAuthCodeDisposable);
        addDisposable(this.mGetAuthCodeDisposable);
    }

    private void runTncMandatoryRequest() {
        SALog.i(TAG, "runTncMandatoryRequest");
        Disposable subscribe = new TncMandatoryRequest(this, this.mClientId, this.mCallingPackageName, this.mIsCheckBasicProfile).tncMandatoryObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnDispose(new Action() { // from class: en0
            @Override // io.reactivex.functions.Action
            public final void run() {
                RequestAuthCodeActivity.this.lambda$runTncMandatoryRequest$1();
            }
        }).subscribe(new Consumer() { // from class: fn0
            @Override // io.reactivex.functions.Consumer
            public final void accept(Object obj) {
                RequestAuthCodeActivity.this.lambda$runTncMandatoryRequest$2((ResultTncMandatoryUtilVO) obj);
            }
        });
        this.mTncMandatoryDisposable = subscribe;
        showProgressDialog(true, subscribe);
        addDisposable(this.mTncMandatoryDisposable);
    }

    private void setFailedResult(String str, String str2) {
        SALog.i(TAG, "setFailedResult() errorCode : " + str + " errorMessage : " + str2);
        Intent intent = new Intent();
        intent.putExtra("error_code", str);
        intent.putExtra("error_message", str2);
        setResultWithLog(1, intent);
        finish();
    }

    private void setTransparentActivityLayout() {
        RoundedCornerUtil.setTranslucentCorners(getWindow());
    }

    private void startAgreeToDisclaimerActivity() {
        SALog.i(TAG, "startAgreeToDisclaimerActivity");
        this.mNeedToShowDisclaimer = false;
        Intent intent = new Intent();
        intent.setAction(Config.ACTION_NEW_THIRD_PARTY_INTEGRATION_WITH_SAMSUNG_ACCOUNT);
        intent.putExtra("client_id", this.mClientId);
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_CALLING_PACKAGE, this.mCallingPackageName);
        intent.putExtra(Config.InterfaceKey.KEY_EXTERNAL_PROMPT, this.mPrompt);
        startActivityForResult(intent, RequestCode.AGREE_TO_DISCLAIMER.ordinal());
    }

    private void startEmailValidationActivity() {
        SALog.i(TAG, "startEmailValidationActivity");
        Intent intent = new Intent();
        intent.setAction(Config.ACTION_SAMSUNGACCOUNT_EMAIL_VALIDATE);
        intent.putExtra("client_id", this.mClientId);
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_CALLING_PACKAGE, this.mCallingPackageName);
        intent.putExtra(Config.InterfaceKey.KEY_INTERNAL_IS_RESEND, true);
        intent.putExtra(Config.InterfaceKey.KEY_INTERNAL_IS_VERIFY_POPUP, true);
        startActivityForResult(intent, RequestCode.EMAIL_VALIDATION_VIEW.ordinal());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startMainProcess() {
        getParamsFromIntent();
        if (checkTaskPrerequisites()) {
            if (!this.mIsNeedToServerCheck && isChecklistValid()) {
                if ("consent".equals(this.mPrompt)) {
                    startAgreeToDisclaimerActivity();
                    return;
                } else {
                    runGetAuthCodeRequest();
                    return;
                }
            }
            runTncMandatoryRequest();
        }
    }

    private void startMandatoryCheckActivity() {
        SALog.i(TAG, "startMandatoryCheckActivity");
        Intent intent = new Intent();
        intent.setAction(Config.ACTION_SAMSUNGACCOUNT_SAVE_MANDATORY_INFO);
        intent.putExtra("client_id", this.mClientId);
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_CALLING_PACKAGE, this.mCallingPackageName);
        startActivityForResult(intent, RequestCode.ACCOUNT_INFO.ordinal());
    }

    private void startNameValidationActivity() {
        SALog.i(TAG, "startNameValidationActivity");
        Intent intent = new Intent();
        if (LocalBusinessException.isMccKorea(StateCheckUtil.getRegionMcc(this))) {
            intent.setAction(Config.ACTION_SAMSUNGACCOUNT_NAME_VALIDATE_POPUP);
        } else {
            intent.setAction(Config.ACTION_SAMSUNGACCOUNT_NAME_VALIDATE);
        }
        intent.putExtra(Config.InterfaceKey.KEY_INTERNAL_USER_ID, DbManagerV2.getUserID(this));
        intent.putExtra("client_id", this.mClientId);
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_CALLING_PACKAGE, this.mCallingPackageName);
        startActivityForResult(intent, RequestCode.NAME_VALIDATION_VIEW.ordinal());
    }

    private void startProperActivityByChecklist() {
        SALog.i(TAG, "startProperActivityByChecklist");
        ChecklistStepUtil checklistStepUtil = this.mChecklistStepUtil;
        int nextCheckItem = checklistStepUtil != null ? checklistStepUtil.getNextCheckItem(this, false) : 0;
        if (nextCheckItem == 1) {
            startTncActivity();
            return;
        }
        if (nextCheckItem == 2) {
            startNameValidationActivity();
            return;
        }
        if (nextCheckItem == 3) {
            startEmailValidationActivity();
            return;
        }
        if (nextCheckItem == 4) {
            startMandatoryCheckActivity();
            return;
        }
        if (nextCheckItem != 5) {
            if (this.mNeedToShowDisclaimer && "consent".equals(this.mPrompt)) {
                startAgreeToDisclaimerActivity();
                return;
            } else {
                runGetAuthCodeRequest();
                return;
            }
        }
        if (this.mNeedToShowDisclaimer) {
            startAgreeToDisclaimerActivity();
        } else {
            setFailedResult(ErrorCode.SAC.DISCLAIMER_AGREEMENT_IS_NOT_COMPLETED, Config.RESPONSE_ERROR_MESSAGE.DISCLAIMER_AGREEMENT_IS_NOT_COMPLETED);
        }
    }

    private void startReSignIn() {
        SALog.i(TAG, "startReSignIn");
        if (AccountManagerUtil.getInstance().isSamsungAccountSignedIn(this)) {
            startActivityForResult(IntentCreator.getIntentForReSignIn(this, this.mClientId, this.mCallingPackageName), RequestCode.RESIGN_IN.ordinal());
        }
    }

    private void startSignIn() {
        SALog.i(TAG, "startSignIn");
        startActivityForResult(new Intent(Config.ACTION_ADD_SAMSUNG_ACCOUNT), RequestCode.SIGN_IN.ordinal());
    }

    private void startTncActivity() {
        SALog.i(TAG, "startTncActivity");
        Intent intent = new Intent();
        intent.setAction(Config.ACTION_SAMSUNGACCOUNT_UPDATE_NEW_TERMS);
        intent.putExtra("client_id", this.mClientId);
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_CALLING_PACKAGE, this.mCallingPackageName);
        intent.putExtra(Config.InterfaceKey.KEY_INTERNAL_TNC_UPDATE_MODE, true);
        intent.putExtra(Config.InterfaceKey.KEY_COMMON_IS_GDPR_COUNTRY, this.mIsGdpr);
        startActivityForResult(intent, RequestCode.TNC.ordinal());
    }

    @Override // com.samsung.android.samsungaccount.utils.ui.BaseAppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        RequestCode requestCode = RequestCode.values()[i];
        StringBuilder sb = new StringBuilder();
        sb.append("onActivityResult requestCode : ");
        sb.append(requestCode);
        sb.append(" + resultCode : ");
        sb.append(i2);
        sb.append(" + data : ");
        sb.append(intent != null ? "nonNull" : "null");
        SALog.i(TAG, sb.toString());
        disposeObservableIfRunning();
        if (i2 != -1) {
            setResultWithLog(i2, intent);
            finish();
        } else {
            handleActivityResultOk(requestCode);
        }
    }

    @Override // com.samsung.android.samsungaccount.utils.ui.BaseAppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        SALog.i(TAG, "onCreate");
        super.onCreate(bundle);
        initAuthCodeRequestCount();
        setTransparentActivityLayout();
        this.mIntent = getIntent();
        if (isActivityCalledProperly()) {
            requestSignatureCheckAsync(new Runnable() { // from class: in0
                @Override // java.lang.Runnable
                public final void run() {
                    RequestAuthCodeActivity.this.startMainProcess();
                }
            }, new Runnable() { // from class: jn0
                @Override // java.lang.Runnable
                public final void run() {
                    RequestAuthCodeActivity.this.lambda$onCreate$0();
                }
            });
        }
    }
}
