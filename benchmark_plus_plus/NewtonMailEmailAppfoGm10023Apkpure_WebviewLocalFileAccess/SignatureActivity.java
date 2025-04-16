package com.cloudmagic.android;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cloudmagic.lib.cmsmart.CMCommon;
import com.cloudmagic.android.data.entities.Alias;
import com.cloudmagic.android.data.entities.StaleAttachment;
import com.cloudmagic.android.data.tables.AliasTable;
import com.cloudmagic.android.dialogs.ConfirmationDialog;
import com.cloudmagic.android.global.Constants;
import com.cloudmagic.android.helper.AccountSettingsPreferences;
import com.cloudmagic.android.helper.CMLogger;
import com.cloudmagic.android.helper.ObjectStorageSingleton;
import com.cloudmagic.android.helper.SignatureAttachmentUploader;
import com.cloudmagic.android.network.api.response.APIError;
import com.cloudmagic.android.observers.LogoutBroadcastReceiver;
import com.cloudmagic.android.utils.Utilities;
import com.cloudmagic.mail.R;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SignatureActivity extends BaseActivity implements Handler.Callback {
    public static final String IMAGE_NODE_KEY = "image_node_key";
    private static final String JS_INTERFACE = "signature";
    private static final int URL_CLICK = 2;
    private static final int WEB_VIEW_CLICK = 1;
    private final Handler handler = new Handler(this);
    private int imageCount = -1;
    private JSONArray imageDataDetailObj;
    private JSONArray initialImageDataDetailObj;
    private boolean isBackPressed;
    private boolean isSaveClicked;
    private int mAccountId;
    private Alias mAlias;
    private LogoutBroadcastReceiver mBroadcastReceiver;
    private String mHtmlSignature;
    private boolean preventFurtherUpload;
    private ConfirmationDialog retryConfirmationDialog;
    private WebView signatureWebview;

    @Override // com.cloudmagic.android.BaseActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.signature_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.signatureWebview = (WebView) findViewById(R.id.signature_webview);
        this.signatureWebview.setWebChromeClient(new WebChromeClient() { // from class: com.cloudmagic.android.SignatureActivity.1
            @Override // android.webkit.WebChromeClient
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                CMLogger cMLogger = new CMLogger(SignatureActivity.this.getApplicationContext());
                cMLogger.putMessage("CMJsSignatureLog error ::" + consoleMessage.message());
                cMLogger.putMessage("CMJsSignatureLog line no ::" + consoleMessage.lineNumber());
                cMLogger.commit();
                return true;
            }
        });
        WebViewClient webViewClient = new WebViewClient() { // from class: com.cloudmagic.android.SignatureActivity.2
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                SignatureActivity.this.handler.sendEmptyMessage(2);
                return false;
            }
        };
        this.signatureWebview.setOnTouchListener(new View.OnTouchListener() { // from class: com.cloudmagic.android.SignatureActivity.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0) {
                    return false;
                }
                SignatureActivity.this.handler.sendEmptyMessageDelayed(1, 500L);
                return false;
            }
        });
        this.signatureWebview.setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.SignatureActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        this.signatureWebview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.signatureWebview.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.signatureWebview, true);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            this.signatureWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        this.signatureWebview.addJavascriptInterface(new CMSignatureJavaScriptInterface(this), "signature");
        this.signatureWebview.setWebViewClient(webViewClient);
        Utilities.setUpCMCookies(this, Constants.HTTPS + Utilities.getServerMobilePageUrl(this));
        if (Build.VERSION.SDK_INT <= 18) {
            findViewById(R.id.signature_guide).setVisibility(8);
        }
        this.mAccountId = getIntent().getIntExtra("account_id", -1);
        this.mAlias = (Alias) getIntent().getParcelableExtra(AliasTable.TABLE_NAME);
        String str = (String) ObjectStorageSingleton.getInstance(this).getObject(ObjectStorageSingleton.SIGNATURE_ACTIVITY_SIGNATURE);
        ObjectStorageSingleton.getInstance(this).removeObject(ObjectStorageSingleton.SIGNATURE_ACTIVITY_SIGNATURE);
        AccountSettingsPreferences accountSettingsPreferences = AccountSettingsPreferences.getInstance(getApplicationContext());
        if (this.mAlias != null) {
            if (accountSettingsPreferences.hasKey(accountSettingsPreferences.getAliasPreferenceKey(this.mAccountId, this.mAlias.email, "html_signature_attachment"))) {
                String signatureAttachment = accountSettingsPreferences.getSignatureAttachment(accountSettingsPreferences.getAliasPreferenceKey(this.mAccountId, this.mAlias.email, "html_signature_attachment"));
                if (!TextUtils.isEmpty(signatureAttachment)) {
                    try {
                        this.initialImageDataDetailObj = new JSONArray(signatureAttachment);
                    } catch (JSONException e) {
                        Log.e("SignatureActivity", "error occurred while parsing json", e);
                    }
                }
            }
        } else if (accountSettingsPreferences.hasKey(accountSettingsPreferences.getPreferenceKey(this.mAccountId, "html_signature_attachment"))) {
            String signatureAttachment2 = accountSettingsPreferences.getSignatureAttachment(accountSettingsPreferences.getPreferenceKey(this.mAccountId, "html_signature_attachment"));
            if (!TextUtils.isEmpty(signatureAttachment2)) {
                try {
                    this.initialImageDataDetailObj = new JSONArray(signatureAttachment2);
                } catch (JSONException e2) {
                    Log.e("SignatureActivity", "error occurred while parsing json", e2);
                }
            }
        }
        String replaceAll = str.replaceAll("(\r\n|\n\r|\n)", "<br/>");
        String string = getResources().getString(R.string.signature_title);
        Object format = String.format("#%06X", Integer.valueOf(16777215 & getResources().getColor(R.color.signature_placeholder_color)));
        if (TextUtils.isEmpty(replaceAll)) {
            replaceAll = "";
        }
        this.signatureWebview.loadDataWithBaseURL("file:///android_asset/", getEditableHtmlSigantureHtml(new Object[]{string, replaceAll, format}), "text/html", "utf-8", null);
        customizeActionBar();
        registerLogoutBroadcastReciever();
        this.signatureWebview.requestFocus(130);
    }

    private String getEditableHtmlSigantureHtml(Object[] objArr) {
        return MessageFormat.format("<html><head><meta name=\"viewport\" content=\"width=device-width\" /> <script src=\"file:///android_asset/CMSignature.js\" type=\"text/javascript\"></script><style>.CMContentEditableDiv'{'min-height:30px;'}'.CMContentEditableDiv:empty:before'{'    content:attr(data-text); color: {2};'}'.CMContentEditableDiv:focus'{'-webkit-tap-highlight-color:rgba(0,0,0,0);outline: none;'}'</style><body><div id=\"CMContentEditableDiv\" class=\"CMContentEditableDiv\" onpaste=\"pasteClipboardContent.call(this);\" contenteditable=\"true\" data-text=\"{0}\">{1}</div></body></html>", objArr);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.isSaveClicked || this.isBackPressed) {
            return;
        }
        this.isBackPressed = true;
        this.signatureWebview.loadUrl("javascript:getListOfImagesToUpload();");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uploadHTMLAttachments() {
        if (this.isSaveClicked || this.isBackPressed) {
            return;
        }
        this.isSaveClicked = true;
        this.preventFurtherUpload = false;
        showProgressDialog(getResources().getString(R.string.signature_uploading), false);
        this.signatureWebview.loadUrl("javascript:getAllImages();");
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
            return true;
        }
        if (itemId == R.id.menu_save_signature) {
            uploadHTMLAttachments();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signature_activity, menu);
        return true;
    }

    private void registerLogoutBroadcastReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LogoutBroadcastReceiver.INTENT_ACTION_LOGOUT);
        this.mBroadcastReceiver = new LogoutBroadcastReceiver(this);
        registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    public void customizeActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getApplicationContext().getString(R.string.signature_title));
    }

    @Override // com.cloudmagic.android.BaseActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mBroadcastReceiver);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        PasscodeActivity.checkPasscodeSecurity(this);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what == 2) {
            this.handler.removeMessages(1);
            return true;
        }
        if (message.what != 1) {
            return false;
        }
        this.signatureWebview.loadUrl("javascript:document.getElementsByTagName('div')[0].focus();");
        Utilities.showKeyboard(this, this.signatureWebview);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<StaleAttachment> getAttachmentsToPurge() {
        boolean z;
        if (this.initialImageDataDetailObj == null) {
            return null;
        }
        ArrayList<StaleAttachment> arrayList = new ArrayList<>();
        if (this.imageDataDetailObj == null || "".equals(this.imageDataDetailObj.toString())) {
            for (int i = 0; i < this.initialImageDataDetailObj.length(); i++) {
                JSONObject jSONObject = (JSONObject) this.initialImageDataDetailObj.opt(i);
                if (jSONObject != null) {
                    String optString = jSONObject.optString(Constants.S3_FILE_TOKEN);
                    StaleAttachment staleAttachment = new StaleAttachment();
                    staleAttachment.setS3Token(optString);
                    String optString2 = jSONObject.optString(Constants.LOCAL_FILE_PATH);
                    if (!TextUtils.isEmpty(optString2)) {
                        staleAttachment.setLocalFilePath(optString2);
                    }
                    arrayList.add(staleAttachment);
                }
            }
            return arrayList;
        }
        for (int i2 = 0; i2 < this.initialImageDataDetailObj.length(); i2++) {
            JSONObject jSONObject2 = (JSONObject) this.initialImageDataDetailObj.opt(i2);
            if (jSONObject2 != null) {
                String optString3 = jSONObject2.optString(Constants.CONTENT_PATH);
                int i3 = 0;
                while (true) {
                    if (i3 >= this.imageDataDetailObj.length()) {
                        z = true;
                        break;
                    }
                    JSONObject jSONObject3 = (JSONObject) this.imageDataDetailObj.opt(i3);
                    if (jSONObject3 != null && optString3.equals(jSONObject3.optString(Constants.CONTENT_PATH))) {
                        z = false;
                        break;
                    }
                    i3++;
                }
                if (z) {
                    String optString4 = jSONObject2.optString(Constants.S3_FILE_TOKEN_SIGNATURE);
                    if (TextUtils.isEmpty(optString4)) {
                        optString4 = jSONObject2.optString(Constants.S3_FILE_TOKEN);
                    }
                    StaleAttachment staleAttachment2 = new StaleAttachment();
                    staleAttachment2.setS3Token(optString4);
                    String optString5 = jSONObject2.optString(Constants.LOCAL_FILE_PATH);
                    if (!TextUtils.isEmpty(optString5)) {
                        staleAttachment2.setLocalFilePath(optString5);
                    }
                    arrayList.add(staleAttachment2);
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getContentPath(String str, Context context) {
        return Constants.HTTPS + Utilities.getServerMobilePageUrl(context) + str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUploadConfirmationMessage() {
        final ConfirmationDialog newInstance = ConfirmationDialog.newInstance(null, getString(R.string.save_signature_message), getString(R.string.discard), getString(R.string.signature_save));
        newInstance.registerCallback(new ConfirmationDialog.ActionListener() { // from class: com.cloudmagic.android.SignatureActivity.5
            @Override // com.cloudmagic.android.dialogs.ConfirmationDialog.ActionListener
            public void onPositiveConfirmation() {
                SignatureActivity.this.isBackPressed = false;
                SignatureActivity.this.uploadHTMLAttachments();
                FragmentTransaction beginTransaction = SignatureActivity.this.getSupportFragmentManager().beginTransaction();
                beginTransaction.remove(newInstance);
                beginTransaction.commitAllowingStateLoss();
            }

            @Override // com.cloudmagic.android.dialogs.ConfirmationDialog.ActionListener
            public void onNegativeConfirmation() {
                Intent intent = new Intent();
                intent.putExtra("dont_save", true);
                SignatureActivity.this.setResult(-1, intent);
                SignatureActivity.this.finish();
            }
        });
        newInstance.setOnDismissListener(new ConfirmationDialog.OnDismissListener() { // from class: com.cloudmagic.android.SignatureActivity.6
            @Override // com.cloudmagic.android.dialogs.ConfirmationDialog.OnDismissListener
            public void onDismiss() {
                SignatureActivity.this.isSaveClicked = false;
                SignatureActivity.this.isBackPressed = false;
            }
        });
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.add(newInstance, ConfirmationDialog.TAG);
        beginTransaction.commitAllowingStateLoss();
    }

    /* loaded from: classes.dex */
    class CMSignatureJavaScriptInterface {
        private Context mContext;

        CMSignatureJavaScriptInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void onCheckUploadFiles(String str) {
            JSONArray jSONArray = new JSONArray(str);
            if (jSONArray.length() == 0) {
                SignatureActivity.this.signatureWebview.post(new Runnable() { // from class: com.cloudmagic.android.SignatureActivity.CMSignatureJavaScriptInterface.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SignatureActivity.this.signatureWebview.loadUrl("javascript:window.signature.saveHTML(document.getElementById(\"CMContentEditableDiv\").innerHTML);");
                    }
                });
                return;
            }
            if (SignatureActivity.this.initialImageDataDetailObj == null || SignatureActivity.this.initialImageDataDetailObj.length() == 0) {
                SignatureActivity.this.showUploadConfirmationMessage();
                return;
            }
            if (SignatureActivity.this.initialImageDataDetailObj.length() > 0) {
                boolean z = false;
                int i = 0;
                while (true) {
                    if (i < jSONArray.length()) {
                        String optString = jSONArray.optString(i);
                        if (optString != null && SignatureActivity.this.searchInUploadedAttachments(optString) == null) {
                            z = true;
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                if (z) {
                    SignatureActivity.this.showUploadConfirmationMessage();
                } else {
                    SignatureActivity.this.signatureWebview.post(new Runnable() { // from class: com.cloudmagic.android.SignatureActivity.CMSignatureJavaScriptInterface.2
                        @Override // java.lang.Runnable
                        public void run() {
                            SignatureActivity.this.imageDataDetailObj = SignatureActivity.this.initialImageDataDetailObj;
                            SignatureActivity.this.signatureWebview.loadUrl("javascript:replaceImagesWithCid();");
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public String getAbsoluteContentPath(String str) {
            return SignatureActivity.this.getContentPath(str, this.mContext);
        }

        @JavascriptInterface
        public void saveHTML(String str) {
            SignatureActivity.this.hideDialog();
            SignatureActivity.this.mHtmlSignature = str.replaceAll("^(<br/?>)+$", "");
            if (CMCommon.html2Text(SignatureActivity.this.mHtmlSignature) == null || TextUtils.isEmpty(CMCommon.html2Text(SignatureActivity.this.mHtmlSignature))) {
                SignatureActivity.this.mHtmlSignature = "";
            }
            ArrayList<? extends Parcelable> attachmentsToPurge = SignatureActivity.this.getAttachmentsToPurge();
            Intent intent = new Intent();
            ObjectStorageSingleton.getInstance(SignatureActivity.this.getApplicationContext()).storeObject(ObjectStorageSingleton.SIGNATURE_ACTIVITY_SIGNATURE, SignatureActivity.this.mHtmlSignature);
            intent.putExtra(AliasTable.TABLE_NAME, SignatureActivity.this.mAlias);
            intent.putExtra("signature_attachment_obj", SignatureActivity.this.imageDataDetailObj == null ? "" : SignatureActivity.this.imageDataDetailObj.toString());
            intent.putParcelableArrayListExtra("attachments_purge", attachmentsToPurge);
            SignatureActivity.this.setResult(-1, intent);
            SignatureActivity.this.finish();
        }

        @JavascriptInterface
        public void onReceiveImageFromJS(String str, String str2, int i) {
            if (SignatureActivity.this.imageCount == -1) {
                SignatureActivity.this.imageCount = i;
                SignatureActivity.this.imageDataDetailObj = new JSONArray();
            }
            final JSONObject searchInUploadedAttachments = SignatureActivity.this.searchInUploadedAttachments(str2);
            if (searchInUploadedAttachments != null) {
                SignatureActivity.this.signatureWebview.post(new Runnable() { // from class: com.cloudmagic.android.SignatureActivity.CMSignatureJavaScriptInterface.3
                    @Override // java.lang.Runnable
                    public void run() {
                        SignatureActivity.this.addImageAttachmentObj(searchInUploadedAttachments);
                    }
                });
            } else if (TextUtils.isEmpty(str)) {
                SignatureActivity.this.signatureWebview.post(new Runnable() { // from class: com.cloudmagic.android.SignatureActivity.CMSignatureJavaScriptInterface.4
                    @Override // java.lang.Runnable
                    public void run() {
                        SignatureActivity.this.addImageAttachmentObj(null);
                    }
                });
            } else {
                new SignatureUploadAsyncTask(new SignatureAttachmentUploader(SignatureActivity.this, SignatureActivity.this.createAttachmentObj(str2), SignatureActivity.this.mAccountId, str), str2).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
            }
        }

        @JavascriptInterface
        public String getInitialImageDataDetailObject() {
            if (SignatureActivity.this.initialImageDataDetailObj != null) {
                return SignatureActivity.this.initialImageDataDetailObj.toString();
            }
            return null;
        }

        @JavascriptInterface
        public String getImageDataDetailObject() {
            return SignatureActivity.this.imageDataDetailObj.toString();
        }

        @JavascriptInterface
        public boolean checkFileExist(String str) {
            return new File(str).exists();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x005b, code lost:
    
        r0.mimeType = java.net.URLDecoder.decode(r4[1], "UTF-8");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.cloudmagic.android.data.entities.Attachment createAttachmentObj(java.lang.String r8) {
        /*
            r7 = this;
            com.cloudmagic.android.data.entities.Attachment r0 = new com.cloudmagic.android.data.entities.Attachment
            java.lang.String r1 = ""
            r0.<init>(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "cm_"
            r1.append(r2)
            int r2 = r8.hashCode()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.name = r1
            java.lang.String r1 = "file://"
            boolean r1 = r8.startsWith(r1)
            if (r1 == 0) goto L2d
            java.lang.String r8 = com.cloudmagic.android.data.entities.Attachment.getMimeTypeFromPath(r8)
            r0.mimeType = r8
            goto L7d
        L2d:
            java.lang.String r1 = "^(http|https|ftp)://.*$"
            boolean r1 = r8.matches(r1)
            if (r1 == 0) goto L7d
            java.net.URL r1 = new java.net.URL     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            r1.<init>(r8)     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            java.lang.String r8 = r1.getQuery()     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            java.lang.String r1 = "&"
            java.lang.String[] r8 = r8.split(r1)     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            int r1 = r8.length     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            r2 = 0
            r3 = r2
        L47:
            if (r3 >= r1) goto L7d
            r4 = r8[r3]     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            java.lang.String r5 = "="
            java.lang.String[] r4 = r4.split(r5)     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            java.lang.String r5 = "cty"
            r6 = r4[r2]     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            boolean r5 = r5.equals(r6)     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            if (r5 == 0) goto L67
            r8 = 1
            r8 = r4[r8]     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            java.lang.String r1 = "UTF-8"
            java.lang.String r8 = java.net.URLDecoder.decode(r8, r1)     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            r0.mimeType = r8     // Catch: java.io.UnsupportedEncodingException -> L6a java.net.MalformedURLException -> L73
            goto L7d
        L67:
            int r3 = r3 + 1
            goto L47
        L6a:
            r8 = move-exception
            java.lang.String r1 = "SignatureActivity"
            java.lang.String r2 = "Error occured while determining mime type"
            android.util.Log.e(r1, r2, r8)
            goto L7d
        L73:
            r8 = move-exception
            java.lang.String r0 = "SignatureActivity"
            java.lang.String r1 = "Url is not proper"
            android.util.Log.e(r0, r1, r8)
            r8 = 0
            return r8
        L7d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cloudmagic.android.SignatureActivity.createAttachmentObj(java.lang.String):com.cloudmagic.android.data.entities.Attachment");
    }

    private void onAllUploadCompleted() {
        if (this.imageDataDetailObj.length() > 0) {
            this.signatureWebview.loadUrl("javascript:replaceImagesWithCid();");
        } else {
            this.signatureWebview.loadUrl("javascript:window.signature.saveHTML(document.getElementById(\"CMContentEditableDiv\").innerHTML);");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JSONObject searchInUploadedAttachments(String str) {
        if (this.initialImageDataDetailObj == null) {
            return null;
        }
        for (int i = 0; i < this.initialImageDataDetailObj.length(); i++) {
            JSONObject jSONObject = (JSONObject) this.initialImageDataDetailObj.opt(i);
            if (jSONObject != null) {
                if (("file://" + jSONObject.optString(Constants.LOCAL_FILE_PATH)).equals(str) || getContentPath(jSONObject.optString(Constants.CONTENT_PATH), this).equals(str)) {
                    return jSONObject;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SignatureResponse {
        private APIError error;
        private JSONObject json;

        public SignatureResponse(JSONObject jSONObject, APIError aPIError) {
            this.json = jSONObject;
            this.error = aPIError;
        }
    }

    /* loaded from: classes.dex */
    private class SignatureUploadAsyncTask extends AsyncTask<Void, Void, SignatureResponse> {
        private String imageSrc;
        private SignatureAttachmentUploader mUploader;

        public SignatureUploadAsyncTask(SignatureAttachmentUploader signatureAttachmentUploader, String str) {
            this.mUploader = signatureAttachmentUploader;
            this.imageSrc = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:42:0x0115 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r3v1 */
        /* JADX WARN: Type inference failed for: r3v3 */
        /* JADX WARN: Type inference failed for: r3v4, types: [java.io.OutputStream] */
        /* JADX WARN: Type inference failed for: r3v40 */
        /* JADX WARN: Type inference failed for: r3v41 */
        /* JADX WARN: Type inference failed for: r3v42 */
        /* JADX WARN: Type inference failed for: r3v43 */
        /* JADX WARN: Type inference failed for: r3v44 */
        /* JADX WARN: Type inference failed for: r3v45 */
        /* JADX WARN: Type inference failed for: r3v46 */
        /* JADX WARN: Type inference failed for: r3v47 */
        /* JADX WARN: Type inference failed for: r3v48 */
        /* JADX WARN: Type inference failed for: r3v49 */
        /* JADX WARN: Type inference failed for: r3v5 */
        /* JADX WARN: Type inference failed for: r3v6 */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:74:0x016d -> B:45:0x0185). Please report as a decompilation issue!!! */
        @Override // android.os.AsyncTask
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public com.cloudmagic.android.SignatureActivity.SignatureResponse doInBackground(java.lang.Void... r7) {
            /*
                Method dump skipped, instructions count: 397
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.cloudmagic.android.SignatureActivity.SignatureUploadAsyncTask.doInBackground(java.lang.Void[]):com.cloudmagic.android.SignatureActivity$SignatureResponse");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(SignatureResponse signatureResponse) {
            super.onPostExecute((SignatureUploadAsyncTask) signatureResponse);
            if (signatureResponse == null) {
                return;
            }
            if (signatureResponse.error == null) {
                if (signatureResponse.json != null) {
                    SignatureActivity.this.addImageAttachmentObj(signatureResponse.json);
                }
            } else {
                SignatureActivity.this.hideDialog();
                SignatureActivity.this.isSaveClicked = false;
                SignatureActivity.this.imageCount = -1;
                SignatureActivity.this.showErrorDialog(3, SignatureActivity.this.getString(R.string.signature_error_message), null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void addImageAttachmentObj(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                this.imageDataDetailObj.put(jSONObject);
            } catch (Throwable th) {
                throw th;
            }
        }
        this.imageCount--;
        if (this.imageCount == 0) {
            this.imageCount = -1;
            onAllUploadCompleted();
        }
    }
}
