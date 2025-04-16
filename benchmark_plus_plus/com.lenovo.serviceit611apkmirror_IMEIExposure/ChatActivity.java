package com.lenovo.serviceit.selfhelp.chat.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.lenovo.serviceit.BaseActivity;
import com.lenovo.serviceit.Constants;
import com.lenovo.serviceit.R;
import com.lenovo.serviceit.productselector.SelectedProduct;
import com.lenovo.serviceit.selfhelp.chat.utils.JavascriptInterface;
import com.lenovo.serviceit.selfhelp.utils.CountryUtils;
import com.lenovo.serviceit.service.moto.fragment.HelpTopicsFragment;
import com.lenovo.serviceit.utils.GAUtils;
import com.lenovo.serviceit.utils.Logger;
import com.lenovo.serviceit.utils.NetworkUtils;
import com.lenovo.serviceit.utils.SPUtils;
import com.lenovo.serviceit.utils.SiteCatalystUtils;
import com.lenovo.serviceit.widget.dialog.CustomDialog;
import java.util.Locale;

/* loaded from: classes.dex */
public class ChatActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_FIRSTNAME = "firstName";
    public static final String EXTRA_IMEI = "imei";
    public static final String EXTRA_INQUIRY = "inquiry";
    public static final String EXTRA_ISSUE = "issue";
    public static final String EXTRA_LASTNAME = "lastName";
    public static final String EXTRA_PHONENUMBER = "phoneNumber";
    public static final String EXTRA_POSTURLFORMOBILE = "PostURLForMobile";
    public static final String EXTRA_TRANSCRIPT = "transcript";
    public static final int FILE_CHOOSER_RESULT_CODE = 1;
    public static final int FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 2;
    private static final String TAG = ChatActivity.class.getSimpleName();
    private String brandUrl;
    private Button mLeaveButton;
    private Button mLeaveCancel;
    private LinearLayout mLeaveChat;
    private LinearLayout mNetWorkErrorView;
    private boolean mPressBackKey = true;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private WebView mWebView;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lenovo.serviceit.BaseActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.k, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_chat);
        this.mWebView = (WebView) findViewById(R.id.web_view);
        this.mLeaveChat = (LinearLayout) findViewById(R.id.leave_chat);
        this.mLeaveButton = (Button) findViewById(R.id.leave_button);
        this.mLeaveCancel = (Button) findViewById(R.id.leave_button_cancle);
        initSettings();
        initProgressBar();
        SelectedProduct selectedProduct = (SelectedProduct) SPUtils.getObject(SPUtils.SELECTED_PRODUCT_BROWSE);
        String str = selectedProduct != null ? selectedProduct.Brand : SelectedProduct.BRAND_TABLET;
        if (SelectedProduct.BRAND_TPG.equalsIgnoreCase(str)) {
            this.brandUrl = "Incident.CustomFields.c.brand=11";
        } else if (SelectedProduct.BRAND_PHONE.equalsIgnoreCase(str)) {
            this.brandUrl = "Incident.CustomFields.c.os=2";
        } else if (SelectedProduct.BRAND_IPG.equalsIgnoreCase(str) || SelectedProduct.BRAND_TABLET.equalsIgnoreCase(str)) {
            this.brandUrl = "Incident.CustomFields.c.brand=10";
        }
        checkNetWorkAndLoad();
        GAUtils.sendScreenEvent(this, GAUtils.GA_SCREEN_NAME_CA_CHAT);
        SiteCatalystUtils.trackPage(GAUtils.GA_EVENT_ACTION_CHAT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi", "JavascriptInterface"})
    public void loadUrl2WebView() {
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(EXTRA_FIRSTNAME);
        String stringExtra2 = intent.getStringExtra(EXTRA_LASTNAME);
        String stringExtra3 = intent.getStringExtra(EXTRA_PHONENUMBER);
        String stringExtra4 = intent.getStringExtra(EXTRA_EMAIL);
        String stringExtra5 = intent.getStringExtra(EXTRA_IMEI);
        String stringExtra6 = intent.getStringExtra(EXTRA_ISSUE);
        String stringExtra7 = intent.getStringExtra(EXTRA_INQUIRY);
        String stringExtra8 = intent.getStringExtra(EXTRA_TRANSCRIPT);
        String stringExtra9 = intent.getStringExtra(EXTRA_POSTURLFORMOBILE);
        String displayLanguage = Locale.getDefault().getDisplayLanguage();
        String short2Language = CountryUtils.getShort2Language(SPUtils.getString(SPUtils.SELECTED_PRODUCT_LOCATION), Locale.ENGLISH);
        SelectedProduct selectedProduct = (SelectedProduct) SPUtils.getObject(SPUtils.SELECTED_PRODUCT_BROWSE);
        String str = "Contact.Name.First=" + stringExtra + "&Contact.Name.Last=" + stringExtra2 + "&Incident.CustomFields.c.country=" + short2Language + "&Incident.CustomFields.c.mobilephone=" + stringExtra3 + "&Contact.Emails.PRIMARY.Address=" + stringExtra4 + "&Incident.CustomFields.c.language=" + displayLanguage + "&Incident.CustomFields.c.serialno=" + stringExtra5 + "&Incident.CustomFields.c.issuedescription=" + stringExtra6 + "&Incident.CustomFields.c.inquiryoptions=" + stringExtra7 + "&Incident.CustomFields.c.productmodel=" + (selectedProduct != null ? selectedProduct.Name : "") + "&Incident.CustomFields.c.type=" + (selectedProduct != null ? selectedProduct.ProductId.split("/")[0] : "") + "&Incident.CustomFields.c.routerule=&Incident.CustomFields.c.mobile=1&Incident.CustomFields.c.transcript=" + stringExtra8 + "&" + this.brandUrl;
        this.mWebView.postUrl(TextUtils.isEmpty(stringExtra9) ? Constants.get_submitchat_url : stringExtra9, str.getBytes());
        Logger.d(TAG, "url: " + this.mWebView.getUrl());
        Logger.d(TAG, "url Params: " + str);
        this.mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        this.mWebView.setWebChromeClient(new WebChromeClient() { // from class: com.lenovo.serviceit.selfhelp.chat.activity.ChatActivity.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String str2) {
                ChatActivity.this.openFileChooserImpl(valueCallback);
            }

            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                ChatActivity.this.openFileChooserImpl(valueCallback);
            }

            public void openFileChooser(ValueCallback<Uri> valueCallback, String str2, String str3) {
                ChatActivity.this.openFileChooserImpl(valueCallback);
            }

            @Override // android.webkit.WebChromeClient
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                ChatActivity.this.openFileChooserImplForAndroid5(valueCallback);
                return true;
            }
        });
    }

    private void checkNetWorkAndLoad() {
        this.mNetWorkErrorView = (LinearLayout) findViewById(R.id.rl_net_work_error);
        if (!NetworkUtils.isConnected(this)) {
            this.mNetWorkErrorView.setVisibility(0);
            findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() { // from class: com.lenovo.serviceit.selfhelp.chat.activity.ChatActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (NetworkUtils.isConnected(ChatActivity.this)) {
                        ChatActivity.this.mNetWorkErrorView.setVisibility(8);
                        ChatActivity.this.loadUrl2WebView();
                    }
                }
            });
        } else {
            loadUrl2WebView();
        }
    }

    private void initSettings() {
        WebSettings settings = this.mWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(2);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    private void initProgressBar() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
        this.mWebView.setWebViewClient(new WebViewClient() { // from class: com.lenovo.serviceit.selfhelp.chat.activity.ChatActivity.3
            @Override // android.webkit.WebViewClient
            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                super.onPageStarted(webView, str, bitmap);
                progressBar.setVisibility(0);
                ChatActivity.this.mWebView.setVisibility(0);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                progressBar.setVisibility(8);
                ChatActivity.this.mWebView.setVisibility(0);
                ChatActivity.this.addImageClickListner();
            }

            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                Logger.d(ChatActivity.this, HelpTopicsFragment.EXTRA_URL);
                return true;
            }

            @Override // android.webkit.WebViewClient
            public void onReceivedError(WebView webView, int i, String str, String str2) {
                super.onReceivedError(webView, i, str, str2);
            }

            @Override // android.webkit.WebViewClient
            public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
                CustomDialog customDialog = new CustomDialog(ChatActivity.this.mContext);
                customDialog.setMessage(R.string.ssl_error);
                customDialog.setNegativeButton(ChatActivity.this.mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { // from class: com.lenovo.serviceit.selfhelp.chat.activity.ChatActivity.3.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sslErrorHandler.cancel();
                        ChatActivity.this.finish();
                    }
                });
                customDialog.setPositiveButton(ChatActivity.this.mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() { // from class: com.lenovo.serviceit.selfhelp.chat.activity.ChatActivity.3.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sslErrorHandler.proceed();
                    }
                });
                customDialog.show();
            }
        });
        this.mWebView.setWebChromeClient(new WebChromeClient() { // from class: com.lenovo.serviceit.selfhelp.chat.activity.ChatActivity.4
            @Override // android.webkit.WebChromeClient
            public void onProgressChanged(WebView webView, int i) {
                progressBar.setProgress(i);
                if (i == 100) {
                    progressBar.setVisibility(8);
                }
                super.onProgressChanged(webView, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addImageClickListner() {
        this.mWebView.loadUrl("javascript:(function(){var objs = document.getElementsByTagName(\"img\"); for(var i=0;i<objs.length;i++)  {    objs[i].onclick=function()      {          window.imagelistner.openImage(this.src);      }  }})()");
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.mPressBackKey) {
            this.mLeaveChat.setVisibility(0);
            this.mLeaveButton.setOnClickListener(this);
            this.mLeaveCancel.setOnClickListener(this);
            GAUtils.sendGAEvent(this, GAUtils.GA_EVENT_CATEGORY_CHAT_LEAVE, GAUtils.GA_EVENT_ACTION_CHAT, GAUtils.GA_EVENT_LABEL_CHAT_LEAVE, null);
        } else {
            finish();
        }
        return false;
    }

    @Override // com.lenovo.serviceit.BaseActivity, android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leave_button /* 2131755304 */:
                this.mWebView.loadUrl("javascript:discon()");
                this.mLeaveChat.setVisibility(8);
                this.mPressBackKey = false;
                Logger.d(this, this.mWebView.getUrl() + "exit==submit");
                return;
            case R.id.leave_button_cancle /* 2131755305 */:
                this.mLeaveChat.setVisibility(8);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openFileChooserImpl(ValueCallback<Uri> valueCallback) {
        this.mUploadMessage = valueCallback;
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "File Chooser"), 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openFileChooserImplForAndroid5(ValueCallback<Uri[]> valueCallback) {
        this.mUploadMessageForAndroid5 = valueCallback;
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        Intent intent2 = new Intent("android.intent.action.CHOOSER");
        intent2.putExtra("android.intent.extra.INTENT", intent);
        intent2.putExtra("android.intent.extra.TITLE", "File Chooser");
        startActivityForResult(intent2, 2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            if (this.mUploadMessage != null) {
                this.mUploadMessage.onReceiveValue((intent == null || i2 != -1) ? null : intent.getData());
                this.mUploadMessage = null;
                return;
            }
            return;
        }
        if (i == 2 && this.mUploadMessageForAndroid5 != null) {
            Uri data = (intent == null || i2 != -1) ? null : intent.getData();
            if (data != null) {
                this.mUploadMessageForAndroid5.onReceiveValue(new Uri[]{data});
            } else {
                this.mUploadMessageForAndroid5.onReceiveValue(new Uri[0]);
            }
            this.mUploadMessageForAndroid5 = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        SiteCatalystUtils.onPageResume(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        SiteCatalystUtils.onPagePause();
    }
}
