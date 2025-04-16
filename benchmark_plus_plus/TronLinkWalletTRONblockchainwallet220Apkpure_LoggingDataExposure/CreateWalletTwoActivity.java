package com.tron.wallet.bussiness.creat.creatwallet;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.tron.tron_base.frame.base.BaseActivity;
import com.tron.tron_base.frame.base.EmptyModel;
import com.tron.tron_base.frame.base.EmptyPresenter;
import com.tron.wallet.utils.ToastUtil;
import com.tron.wallet.utils.UIUtils;
import com.tronlink.wallet.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tron.walletserver.StringTronUtil;

/* loaded from: classes21.dex */
public class CreateWalletTwoActivity extends BaseActivity<EmptyPresenter, EmptyModel> {

    @BindView(R.id.backview)
    RelativeLayout backview;

    @BindView(R.id.bottom_line)
    View bottomLine;

    @BindView(R.id.cb_four)
    CheckBox cbFour;

    @BindView(R.id.cb_one)
    CheckBox cbOne;

    @BindView(R.id.cb_three)
    CheckBox cbThree;

    @BindView(R.id.cb_two)
    CheckBox cbTwo;

    @BindView(R.id.creat)
    Button creat;

    @BindView(R.id.error_password)
    TextView errorPassword;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.iv_common_left)
    ImageView ivCommonLeft;

    @BindView(R.id.ll_common_left)
    LinearLayout llCommonLeft;
    private String name;
    private String password;

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.root_header_bar)
    RelativeLayout rootHeaderBar;

    @BindView(R.id.statusbar)
    LinearLayout statusbar;

    @BindView(R.id.text)
    TextView text;

    @BindView(R.id.tv_common_title)
    TextView tvCommonTitle;

    @BindView(R.id.tv_four)
    TextView tvFour;

    @BindView(R.id.tv_one)
    TextView tvOne;

    @BindView(R.id.tv_three)
    TextView tvThree;

    @BindView(R.id.tv_two)
    TextView tvTwo;

    @BindView(R.id.tv_v)
    TextView tvV;

    @BindView(R.id.v_round_one)
    View vRoundOne;

    @BindView(R.id.v_round_two)
    View vRoundTwo;

    @Override // com.tron.tron_base.frame.base.BaseActivity
    protected void setLayout() {
        setView(R.layout.activity_create_wallet_two, 1);
        setHeaderBar(getResources().getString(R.string.creat_wallet), getResources().getString(R.string.next_step));
    }

    @Override // com.tron.tron_base.frame.base.BaseActivity
    protected void processData() {
        this.name = getIntent().getStringExtra("name");
        this.etPassword.addTextChangedListener(new TextWatcher() { // from class: com.tron.wallet.bussiness.creat.creatwallet.CreateWalletTwoActivity.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("lll", "onTextChanged: " + charSequence.toString());
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                String s = editable.toString().trim();
                CreateWalletTwoActivity.this.checkPassword(s);
            }
        });
    }

    @OnClick({R.id.ll_common_left, R.id.creat, R.id.root})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.creat /* 2131296384 */:
                this.password = this.etPassword.getText().toString().trim();
                if (!StringTronUtil.isOkPasswordTwo(this.password)) {
                    this.errorPassword.setVisibility(0);
                    return;
                }
                this.errorPassword.setVisibility(8);
                Intent intent = new Intent(this, (Class<?>) CreateWalletThreeActivity.class);
                intent.putExtra("name", this.name);
                intent.putExtra("password", this.password);
                go(intent);
                return;
            case R.id.ll_common_left /* 2131296618 */:
                finish();
                return;
            case R.id.root /* 2131296836 */:
                UIUtils.hideSoftKeyBoard(this);
                return;
            default:
                return;
        }
    }

    public void checkPassword(String password) {
        Pattern patternOne = Pattern.compile(".*[A-Z]+.*");
        Pattern patternTwo = Pattern.compile(".*[a-z]+.*");
        Pattern patternThree = Pattern.compile(".*[0-9]+.*");
        Matcher matcherOne = patternOne.matcher(password);
        Matcher matcherTwo = patternTwo.matcher(password);
        Matcher matcherThree = patternThree.matcher(password);
        if (matcherOne.matches()) {
            changeView(this.cbOne, this.tvOne, true);
        } else {
            changeView(this.cbOne, this.tvOne, false);
        }
        if (matcherTwo.matches()) {
            changeView(this.cbTwo, this.tvTwo, true);
        } else {
            changeView(this.cbTwo, this.tvTwo, false);
        }
        if (matcherThree.matches()) {
            changeView(this.cbThree, this.tvThree, true);
        } else {
            changeView(this.cbThree, this.tvThree, false);
        }
        if (password.length() >= 8) {
            changeView(this.cbFour, this.tvFour, true);
        } else {
            changeView(this.cbFour, this.tvFour, false);
        }
    }

    public void changeView(CheckBox checkBox, TextView textView, boolean flag) {
        if (flag) {
            checkBox.setChecked(true);
            textView.setTextColor(getResources().getColor(R.color.green_76));
        } else {
            checkBox.setChecked(false);
            textView.setTextColor(getResources().getColor(R.color.orange_db));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.tron.tron_base.frame.base.BaseActivity
    public void onRightButtonClick() {
        super.onRightButtonClick();
        this.password = this.etPassword.getText().toString().trim();
        if (!StringTronUtil.isOkPasswordTwo(this.password)) {
            ToastUtil.getInstance().showToast((Activity) this, getString(R.string.password3));
            return;
        }
        Intent intent = new Intent(this, (Class<?>) CreateWalletThreeActivity.class);
        intent.putExtra("name", this.name);
        intent.putExtra("password", this.password);
        go(intent);
    }
}
