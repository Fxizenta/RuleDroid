package com.sophos.sophtoken;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.sophos.sophtoken.AccountDb;
import com.sophos.sophtoken.Base32String;
import com.sophos.sophtoken.wizard.WizardPageActivity;
import java.io.Serializable;

/* loaded from: classes.dex */
public class EnterKeyActivity extends WizardPageActivity<Serializable> implements TextWatcher, AdapterView.OnItemSelectedListener {
    private static final int MAX_TIMESTEP = 300;
    private static final int MIN_COUNTER = 0;
    private static final int MIN_KEY_BYTES = 10;
    private static final int MIN_TIMESTEP = 5;
    private EditText mAccountName;
    private Spinner mAlgorithm;
    private EditText mKeyEntryField;
    private TextView mTimestepText;
    private EditText mTokenTimestep;
    private Spinner mType;

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.sophos.sophtoken.wizard.WizardPageActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setPageContentView(R.layout.enter_key);
        this.mKeyEntryField = (EditText) findViewById(R.id.key_value);
        this.mAccountName = (EditText) findViewById(R.id.account_name);
        this.mTokenTimestep = (EditText) findViewById(R.id.token_timestep);
        this.mType = (Spinner) findViewById(R.id.type_choice);
        this.mAlgorithm = (Spinner) findViewById(R.id.algorithm_choice);
        this.mTimestepText = (TextView) findViewById(R.id.manual_timestep_text);
        ArrayAdapter<CharSequence> createFromResource = ArrayAdapter.createFromResource(this, R.array.type, R.layout.sophtoken_spinner_item);
        createFromResource.setDropDownViewResource(R.layout.sophtoken_spinner_dropdown_item);
        this.mType.setAdapter((SpinnerAdapter) createFromResource);
        ArrayAdapter<CharSequence> createFromResource2 = ArrayAdapter.createFromResource(this, R.array.algorithm, R.layout.sophtoken_spinner_item);
        createFromResource2.setDropDownViewResource(R.layout.sophtoken_spinner_dropdown_item);
        this.mAlgorithm.setAdapter((SpinnerAdapter) createFromResource2);
        this.mKeyEntryField.addTextChangedListener(this);
        this.mType.setOnItemSelectedListener(this);
        this.mAlgorithm.setOnItemSelectedListener(this);
        this.mRightButton.setText(R.string.enter_key_page_add_button);
    }

    private String getEnteredKey() {
        return this.mKeyEntryField.getText().toString().replace('1', 'I').replace('0', 'O');
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        setDefaultTimestepOrCounter();
    }

    private void setDefaultTimestepOrCounter() {
        if ((this.mType.getSelectedItemPosition() == AccountDb.OtpType.TOTP.value.intValue() ? AccountDb.OtpType.TOTP : AccountDb.OtpType.HOTP) == AccountDb.OtpType.TOTP) {
            this.mTokenTimestep.setText(AccountDb.DEFAULT_TOTP_VALUE.toString());
            this.mTimestepText.setText(R.string.manual_add_timestep);
            this.mAlgorithm.setVisibility(0);
        } else {
            this.mTokenTimestep.setText(AccountDb.DEFAULT_HOTP_COUNTER.toString());
            this.mTimestepText.setText(R.string.manual_add_counter);
            this.mAlgorithm.setVisibility(8);
        }
    }

    private boolean validateKeyAndUpdateStatus(boolean z) {
        try {
            if (Base32String.decode(getEnteredKey()).length < 10) {
                this.mKeyEntryField.setError(z ? getString(R.string.enter_key_too_short) : null);
                return false;
            }
            this.mKeyEntryField.setError(null);
            return true;
        } catch (Base32String.DecodingException unused) {
            this.mKeyEntryField.setError(getString(R.string.enter_key_illegal_char));
            return false;
        }
    }

    private Integer retrieveTokenTimestepOrCounter(AccountDb.OtpType otpType) {
        String obj = this.mTokenTimestep.getText().toString();
        int i = R.string.enter_timestep_error;
        try {
            Integer valueOf = Integer.valueOf(Integer.parseInt(obj));
            if (otpType == AccountDb.OtpType.TOTP) {
                if (valueOf.intValue() < 5 || valueOf.intValue() > MAX_TIMESTEP) {
                    this.mTokenTimestep.setError(getString(R.string.enter_timestep_error));
                    return -1;
                }
            } else if (otpType == AccountDb.OtpType.HOTP && valueOf.intValue() < 0) {
                this.mTokenTimestep.setError(getString(R.string.enter_counter_error));
                return -1;
            }
            this.mTokenTimestep.setError(null);
            return valueOf;
        } catch (NumberFormatException unused) {
            EditText editText = this.mTokenTimestep;
            if (otpType != AccountDb.OtpType.TOTP) {
                i = R.string.enter_counter_error;
            }
            editText.setError(getString(i));
            return -1;
        }
    }

    @Override // com.sophos.sophtoken.wizard.WizardPageActivity
    protected void onRightButtonPressed() {
        AccountDb.OtpType otpType = this.mType.getSelectedItemPosition() == AccountDb.OtpType.TOTP.value.intValue() ? AccountDb.OtpType.TOTP : AccountDb.OtpType.HOTP;
        Integer retrieveTokenTimestepOrCounter = retrieveTokenTimestepOrCounter(otpType);
        Integer valueOf = Integer.valueOf(this.mAlgorithm.getSelectedItemPosition());
        if (!validateKeyAndUpdateStatus(true) || retrieveTokenTimestepOrCounter.intValue() <= -1) {
            return;
        }
        if (otpType == AccountDb.OtpType.TOTP) {
            SophTokenActivity.saveSecret(this, this.mAccountName.getText().toString(), getEnteredKey(), null, otpType, retrieveTokenTimestepOrCounter, AccountDb.DEFAULT_HOTP_COUNTER, OtpProvider.getDefaultDigits(), valueOf);
        } else {
            SophTokenActivity.saveSecret(this, this.mAccountName.getText().toString(), getEnteredKey(), null, otpType, AccountDb.DEFAULT_TOTP_VALUE, retrieveTokenTimestepOrCounter, OtpProvider.getDefaultDigits(), OtpProvider.getDefaultAlgorithm());
        }
        exitWizard();
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        validateKeyAndUpdateStatus(false);
    }
}
