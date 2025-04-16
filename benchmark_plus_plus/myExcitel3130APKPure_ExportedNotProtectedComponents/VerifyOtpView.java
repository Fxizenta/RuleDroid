package com.scaleforce.mobile.myexcitel.ui.verifyotp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgsLazy;
import androidx.navigation.fragment.FragmentKt;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.scaleforce.mobile.myexcitel.R;
import com.scaleforce.mobile.myexcitel.data.model.NavDestination;
import com.scaleforce.mobile.myexcitel.data.model.VerificationType;
import com.scaleforce.mobile.myexcitel.databinding.FragmentVerifyOtpBinding;
import com.scaleforce.mobile.myexcitel.ui.customViews.InputOTPView;
import com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpUIEvent;
import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

/* compiled from: VerifyOtpView.kt */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\b\u0007\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00040\u0001B\u0005¢\u0006\u0002\u0010\u0005J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002H\u0016J\u0012\u0010\u0011\u001a\u00020\u000f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0004H\u0016R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00078VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u000b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\r¨\u0006\u0013²\u0006\n\u0010\u0014\u001a\u00020\u0015X\u008a\u0084\u0002²\u0006\n\u0010\u0014\u001a\u00020\u0015X\u008a\u0084\u0002"}, d2 = {"Lcom/scaleforce/mobile/myexcitel/ui/verifyotp/VerifyOtpView;", "Lcom/scaleforce/mobile/myexcitel/base/BaseView;", "Lcom/scaleforce/mobile/myexcitel/ui/verifyotp/VerifyOtpUIModel;", "Lcom/scaleforce/mobile/myexcitel/ui/verifyotp/VerifyOtpViewModel;", "Lcom/scaleforce/mobile/myexcitel/databinding/FragmentVerifyOtpBinding;", "()V", "viewModelClass", "Ljava/lang/Class;", "getViewModelClass", "()Ljava/lang/Class;", "viewResourceId", "", "getViewResourceId", "()I", "handleUIUpdate", "", "uiModel", "initUI", "binding", "MA3.13.0_release", "args", "Lcom/scaleforce/mobile/myexcitel/ui/verifyotp/VerifyOtpViewArgs;"}, k = 1, mv = {1, 8, 0}, xi = 48)
@AndroidEntryPoint
/* loaded from: classes3.dex */
public final class VerifyOtpView extends Hilt_VerifyOtpView<VerifyOtpUIModel, VerifyOtpViewModel, FragmentVerifyOtpBinding> {
    @Override // com.scaleforce.mobile.myexcitel.base.BaseView
    public Class<VerifyOtpViewModel> getViewModelClass() {
        return VerifyOtpViewModel.class;
    }

    @Override // com.scaleforce.mobile.myexcitel.base.BaseView
    public int getViewResourceId() {
        return R.layout.fragment_verify_otp;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final VerifyOtpViewArgs initUI$lambda$0(NavArgsLazy<VerifyOtpViewArgs> navArgsLazy) {
        return (VerifyOtpViewArgs) navArgsLazy.getValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.scaleforce.mobile.myexcitel.base.BaseView
    public void initUI(final FragmentVerifyOtpBinding binding) {
        AppCompatButton appCompatButton;
        AppCompatButton appCompatButton2;
        Toolbar toolbar;
        final VerifyOtpView verifyOtpView = this;
        NavArgsLazy navArgsLazy = new NavArgsLazy(Reflection.getOrCreateKotlinClass(VerifyOtpViewArgs.class), new Function0<Bundle>() { // from class: com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpView$initUI$$inlined$navArgs$1
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Bundle invoke() {
                Bundle arguments = Fragment.this.getArguments();
                if (arguments != null) {
                    return arguments;
                }
                throw new IllegalStateException("Fragment " + Fragment.this + " has null arguments");
            }
        });
        VerifyOtpViewModel verifyOtpViewModel = (VerifyOtpViewModel) getViewModel();
        if (verifyOtpViewModel != null) {
            verifyOtpViewModel.onUIEvent(new VerifyOtpUIEvent.ScreenEnter(initUI$lambda$0(navArgsLazy).getVerificationType()));
        }
        if (binding != null && (toolbar = binding.toolbar) != null) {
            setupToolbar(toolbar, "");
        }
        if (binding != null && (appCompatButton2 = binding.continueButton) != null) {
            appCompatButton2.setOnClickListener(new View.OnClickListener() { // from class: com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VerifyOtpView.initUI$lambda$2(FragmentVerifyOtpBinding.this, this, view);
                }
            });
        }
        if (binding == null || (appCompatButton = binding.resendOtpButton) == null) {
            return;
        }
        appCompatButton.setOnClickListener(new View.OnClickListener() { // from class: com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VerifyOtpView.initUI$lambda$3(VerifyOtpView.this, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final void initUI$lambda$2(FragmentVerifyOtpBinding fragmentVerifyOtpBinding, VerifyOtpView this$0, View view) {
        VerifyOtpViewModel verifyOtpViewModel;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (String.valueOf(fragmentVerifyOtpBinding.otpEditText.getText()).length() <= 0 || (verifyOtpViewModel = (VerifyOtpViewModel) this$0.getViewModel()) == null) {
            return;
        }
        verifyOtpViewModel.onUIEvent(new VerifyOtpUIEvent.VerifyOtpCode(String.valueOf(fragmentVerifyOtpBinding.otpEditText.getText())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final void initUI$lambda$3(VerifyOtpView this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        SmsRetriever.getClient((Activity) this$0.requireActivity()).startSmsUserConsent(null);
        VerifyOtpViewModel verifyOtpViewModel = (VerifyOtpViewModel) this$0.getViewModel();
        if (verifyOtpViewModel != null) {
            verifyOtpViewModel.onUIEvent(VerifyOtpUIEvent.ResendOtpCode.INSTANCE);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.scaleforce.mobile.myexcitel.base.BaseView
    public void handleUIUpdate(VerifyOtpUIModel uiModel) {
        FragmentVerifyOtpBinding fragmentVerifyOtpBinding;
        InputOTPView inputOTPView;
        Intrinsics.checkNotNullParameter(uiModel, "uiModel");
        super.handleUIUpdate((VerifyOtpView) uiModel);
        if (uiModel.getShowResentMessage()) {
            String string = getString(R.string.otp_code_resent);
            Intrinsics.checkNotNullExpressionValue(string, "getString(R.string.otp_code_resent)");
            showMessage(string);
        }
        if (uiModel.getOtpCodeSent()) {
            String string2 = getString(R.string.otp_code_sent);
            Intrinsics.checkNotNullExpressionValue(string2, "getString(R.string.otp_code_sent)");
            showMessage(string2);
        }
        if (uiModel.getReceivedOtpCode() != null && (fragmentVerifyOtpBinding = (FragmentVerifyOtpBinding) getBinding()) != null && (inputOTPView = fragmentVerifyOtpBinding.otpEditText) != null) {
            inputOTPView.setText(uiModel.getReceivedOtpCode().toString());
        }
        if (uiModel.getNavigateTo() == NavDestination.DASHBOARD) {
            new AlertDialog.Builder(requireContext()).setTitle(R.string.title_verification).setMessage(R.string.message_verification_success).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpView$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpView$$ExternalSyntheticLambda3
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    VerifyOtpView.handleUIUpdate$lambda$6(VerifyOtpView.this, dialogInterface);
                }
            }).create().show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void handleUIUpdate$lambda$6(VerifyOtpView this$0, DialogInterface dialogInterface) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        final VerifyOtpView verifyOtpView = this$0;
        if (handleUIUpdate$lambda$6$lambda$5(new NavArgsLazy(Reflection.getOrCreateKotlinClass(VerifyOtpViewArgs.class), new Function0<Bundle>() { // from class: com.scaleforce.mobile.myexcitel.ui.verifyotp.VerifyOtpView$handleUIUpdate$lambda$6$$inlined$navArgs$1
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Bundle invoke() {
                Bundle arguments = Fragment.this.getArguments();
                if (arguments != null) {
                    return arguments;
                }
                throw new IllegalStateException("Fragment " + Fragment.this + " has null arguments");
            }
        })).getVerificationType() == VerificationType.EDIT_ACCOUNT) {
            FragmentKt.findNavController(verifyOtpView).navigateUp();
        } else {
            FragmentKt.findNavController(verifyOtpView).navigate(VerifyOtpViewDirections.INSTANCE.actionVerifyOtpFragmentToHomeFragment());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final VerifyOtpViewArgs handleUIUpdate$lambda$6$lambda$5(NavArgsLazy<VerifyOtpViewArgs> navArgsLazy) {
        return (VerifyOtpViewArgs) navArgsLazy.getValue();
    }
}
