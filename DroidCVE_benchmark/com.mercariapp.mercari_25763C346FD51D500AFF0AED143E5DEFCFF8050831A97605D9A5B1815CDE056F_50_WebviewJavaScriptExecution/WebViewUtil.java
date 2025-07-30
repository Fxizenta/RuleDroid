package com.mercariapp.mercari.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.AdX.tag.AdXConnect;
import com.mercariapp.mercari.R;
import com.mercariapp.mercari.ThisApplication;
import com.mercariapp.mercari.WebPageChangeListener;
import com.mercariapp.mercari.activity.ContactTopActivity;
import com.mercariapp.mercari.activity.DestinationRegisterActivity;
import com.mercariapp.mercari.activity.ItemDetailActivity;
import com.mercariapp.mercari.activity.ProfileActivity;
import com.mercariapp.mercari.dialog.BaseDialogFragment;
import com.mercariapp.mercari.dialog.ReviewDialog;
import com.mercariapp.mercari.fragment.BaseFragment;
import com.mercariapp.mercari.helper.StringFormatHelper;
import com.mercariapp.mercari.lang.Logger;
import com.mercariapp.mercari.lang.constant.Config;
import com.mercariapp.mercari.lang.constant.Const;
import com.mercariapp.mercari.lang.constant.S;
import com.mercariapp.mercari.lang.constant.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import jp.adstore.tracking.android.AdStoreConnector;
import jp.adstore.tracking.android.entity.AdStoreOrderDetail;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public abstract class WebViewUtil {
    static final int APP_CACHE_MAX_SIZE = 10485760;
    static final String ENCODING = "utf-8";
    static final String MIMETYPE_HTML = "text/html";
    static final String TAG = "Common";

    private static void setWebViewCookie(Context context) {
        CookieManager cm = CookieManager.getInstance();
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().setAcceptCookie(true);
        cm.setCookie(Config.BASE_URL, "access_token=" + ThisApplication.getContext().getAccessToken() + "; domainmercariapp.com");
        CookieSyncManager.getInstance().sync();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void setupWebView(Activity activity, WebView webView) {
        setupWebView(activity, webView, null);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void setupWebView(Activity activity, WebView webView, WebPageChangeListener listener) {
        setupWebView(activity, null, webView, listener);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private static void setupWebView(Activity activity, Fragment fragment, WebView webView, WebPageChangeListener listener) {
        setWebViewCookie(activity);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(2);
        settings.setUserAgentString(ThisApplication.getContext().getUserAgent());
        webView.setScrollBarStyle(0);
        DefaultWebViewClient client = new DefaultWebViewClient(activity, listener);
        webView.setWebViewClient(client);
        webView.setWebChromeClient(new DefaultWebChromeClient(activity));
        webView.addJavascriptInterface(new DefaultJavaScriptInterface(activity, client, webView), "app");
    }

    public static void loadUrl(final WebView webView, final String url) {
        if (!StringUtil.isBlankOrNull(url)) {
            if (url.startsWith("file:")) {
                webView.loadUrl(url);
                return;
            }
            Logger.d("Common", "loadUrl url:" + url);
            final HashMap<String, String> header = new HashMap<>();
            if (isMercariDomain(url)) {
                String accessToken = ThisApplication.getContext().getAccessToken();
                header.put(Const.HEADER_X_ACCESS_TOKEN, accessToken);
            }
            if (isAnchor(url)) {
                webView.post(new Runnable() { // from class: com.mercariapp.mercari.util.WebViewUtil.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (webView.getSettings() != null) {
                            webView.loadUrl(url, header);
                        }
                    }
                });
            } else {
                webView.loadUrl(url, header);
            }
        }
    }

    private static boolean isAnchor(String url) {
        return url.matches(".*#.*");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isMercariDomain(String url) {
        try {
            String host = URI.create(url).getHost();
            String[] parts = host.split("\\.");
            int length = parts.length;
            String domain = parts[length - 2] + "." + parts[length - 1];
            if (!Const.MERCARI_JP.equals(domain)) {
                if (!"mercariapp.com".equals(domain)) {
                    return false;
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IndexOutOfBoundsException e2) {
            return false;
        } catch (NullPointerException e3) {
            return false;
        }
    }

    public static void setupWebView(BaseDialogFragment fragment, WebView webView) {
        Activity activity = fragment.getActivity();
        setupWebView(activity, fragment, webView, null);
    }

    public static void setupWebView(BaseFragment fragment, WebView webView) {
        Activity activity = fragment.getActivity();
        setupWebView(activity, fragment, webView, null);
    }

    public static void sendMessage(WebView webview, String funcName, JSONObject parameter) {
        String strParameter;
        if (parameter != null) {
            strParameter = parameter.toString();
        } else {
            strParameter = "";
        }
        Logger.d(Tag.DEBUG, "----  javascript:" + funcName + "('" + strParameter + "')");
        webview.loadUrl("javascript:" + funcName + "('" + strParameter + "')");
    }

    public static void cleanWebView(WebView webView) {
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.setOnCreateContextMenuListener(null);
        ViewGroup parentView = (ViewGroup) webView.getParent();
        if (parentView != null) {
            parentView.removeView(webView);
        }
        webView.destroy();
    }

    /* loaded from: classes.dex */
    public static class DefaultJavaScriptInterface {
        Activity mActivity;
        DefaultWebViewClient mClient;
        ProgressDialog mProgress;
        WebView mWebView;

        public DefaultJavaScriptInterface(Activity activity, DefaultWebViewClient client, WebView webView) {
            this.mActivity = activity;
            this.mWebView = webView;
            this.mClient = client;
            this.mProgress = new ProgressDialog(activity);
            this.mProgress.setCanceledOnTouchOutside(false);
        }

        @JavascriptInterface
        public void gotoFixedProfile() {
            Logger.d(Tag.DEBUG, " ==== gotoFixedProfile");
            gotoProfile(Config.FIXED_USER_ID, null);
        }

        @JavascriptInterface
        public void gotoProfile(String id, String name) {
            Intent intent = new Intent(this.mActivity, (Class<?>) ProfileActivity.class);
            intent.putExtra("user_name", name);
            intent.putExtra("user_id", id);
            this.mActivity.startActivity(intent);
        }

        @JavascriptInterface
        public void gotoItemDetail(String id, String name) {
            Intent intent = new Intent(this.mActivity, (Class<?>) ItemDetailActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("id", id);
            this.mActivity.startActivity(intent);
        }

        @JavascriptInterface
        public void setTitle(String title) {
            this.mActivity.setTitle(title);
        }

        @JavascriptInterface
        public void getAccessToken(String funcName) {
            if (this.mWebView != null && WebViewUtil.isMercariDomain(this.mWebView.getUrl())) {
                String accessToken = ThisApplication.getContext().getAccessToken();
                this.mWebView.loadUrl("javascript:" + funcName + "('" + accessToken + "')");
            }
        }

        @JavascriptInterface
        public void showMessage(String msg) {
            ThisApplication.getContext().toast(msg);
        }

        @JavascriptInterface
        public void close(String transitType) {
            this.mActivity.finish();
            if (S.close.equals(transitType)) {
                this.mActivity.overridePendingTransition(R.anim.no, R.anim.open_exit);
            }
        }

        @JavascriptInterface
        public void shareMediaList(String type, String message) {
            if (!StringUtil.isBlankOrNull(type)) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.TEXT", message);
                intent.setType(type);
                this.mActivity.startActivity(intent);
            }
        }

        @JavascriptInterface
        public void shareWithTwitter(String message) {
            shareWithApp(Const.PACKAGE_NAME_TWITTER, "text/plain", message);
        }

        @JavascriptInterface
        public void shareWithFacebook(String message) {
            shareWithApp(Const.PACKAGE_NAME_FACEBOOK, "text/plain", message);
        }

        public void shareWithApp(String pkg, String type, String message) {
            if (!StringUtil.isBlankOrNull(type)) {
                try {
                    Intent intent = new Intent();
                    intent.setPackage(pkg);
                    intent.setAction("android.intent.action.SEND");
                    intent.putExtra("android.intent.extra.TEXT", message);
                    intent.setType(type);
                    this.mActivity.startActivity(intent);
                } catch (Exception e) {
                    Uri uri = Uri.parse("market://details?id=" + pkg);
                    this.mActivity.startActivity(new Intent("android.intent.action.VIEW", uri));
                }
            }
        }

        @JavascriptInterface
        public void shareWithMail(String message) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SENDTO");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra("android.intent.extra.TEXT", message);
            this.mActivity.startActivity(intent);
        }

        @JavascriptInterface
        public void openURL(String url) {
            if (!StringUtil.isBlankOrNull(url)) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                this.mActivity.startActivity(intent);
            }
        }

        @JavascriptInterface
        public void clipboard(String ivCode) {
            if (Build.VERSION.SDK_INT >= 11) {
                copyToClipboard(ivCode);
            } else {
                copyToClipboardUnder11(ivCode);
            }
            ThisApplication.getContext().toast(R.string.toast_copy_invitation_coad);
        }

        @JavascriptInterface
        public void gotoContact() {
            Intent intent = new Intent(this.mActivity, (Class<?>) ContactTopActivity.class);
            intent.putExtra(S.button_name, this.mActivity.getString(R.string.close_contact));
            this.mActivity.startActivity(intent);
        }

        @JavascriptInterface
        public void gotoAddressEdit() {
            Intent intent = new Intent(this.mActivity, (Class<?>) DestinationRegisterActivity.class);
            this.mActivity.startActivityForResult(intent, 18);
        }

        @JavascriptInterface
        public void showReviewDialog(String message) {
            if (!LocalStorageUtil.getBoolean(S.pref_is_reviewed)) {
                ReviewDialog reviewDialog = ReviewDialog.newInstance(message);
                FragmentActivity fa = (FragmentActivity) ThisApplication.getContext().getCurrentActivity();
                if (fa != null) {
                    FragmentTransaction ft = fa.getSupportFragmentManager().beginTransaction();
                    ft.add(reviewDialog, (String) null);
                    ft.commitAllowingStateLoss();
                }
            }
        }

        @SuppressLint({"ServiceCast"})
        @TargetApi(11)
        private void copyToClipboard(String ivCode) {
            ClipboardManager clipboardManager = (ClipboardManager) this.mActivity.getSystemService("clipboard");
            ClipData.Item item = new ClipData.Item(ivCode);
            String[] mimeTypes = {"text/plain"};
            ClipData clip = new ClipData("data", mimeTypes, item);
            clipboardManager.setPrimaryClip(clip);
        }

        private void copyToClipboardUnder11(String ivCode) {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) this.mActivity.getSystemService("clipboard");
            clipboardManager.setText(ivCode);
        }

        public void openInDialog(String url) {
            ThisApplication.getContext().openInDialog(url);
        }

        public void openInBrowser(String url) {
            ThisApplication.getContext().openInBrowser(url);
        }

        public void openInWebView(String url) {
            ThisApplication.getContext().openInWebView(url);
        }

        @JavascriptInterface
        public void showProgress(final String message) {
            if (this.mActivity != null && !this.mActivity.isFinishing()) {
                this.mActivity.runOnUiThread(new Runnable() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultJavaScriptInterface.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DefaultJavaScriptInterface.this.mProgress.setMessage(message);
                        DefaultJavaScriptInterface.this.mProgress.show();
                    }
                });
            }
        }

        @JavascriptInterface
        public void dismissProgress() {
            if (this.mActivity != null && !this.mActivity.isFinishing() && this.mProgress != null && this.mProgress.isShowing()) {
                this.mActivity.runOnUiThread(new Runnable() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultJavaScriptInterface.2
                    @Override // java.lang.Runnable
                    public void run() {
                        DefaultJavaScriptInterface.this.mProgress.dismiss();
                    }
                });
            }
        }

        @JavascriptInterface
        public void showDialog(String title, String message, String okLabel, String okFunctionName, String cancelLabel) {
            showDialog(title, message, okLabel, okFunctionName, cancelLabel, null);
        }

        @JavascriptInterface
        public void showDialog(String title, String message, String okLabel, final String okFunctionName, String cancelLabel, final String cancelFunctionName) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this.mActivity);
            if (!TextUtils.isEmpty(title)) {
                dialog.setTitle(title);
            }
            if (!TextUtils.isEmpty(message)) {
                dialog.setMessage(message);
            }
            if (TextUtils.isEmpty(okLabel)) {
                okLabel = this.mActivity.getString(R.string.ok);
            }
            dialog.setPositiveButton(okLabel, new DialogInterface.OnClickListener() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultJavaScriptInterface.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog2, int which) {
                    if (!TextUtils.isEmpty(okFunctionName)) {
                        DefaultJavaScriptInterface.this.mActivity.runOnUiThread(new Runnable() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultJavaScriptInterface.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                DefaultJavaScriptInterface.this.mWebView.loadUrl("javascript:" + okFunctionName + "()");
                            }
                        });
                    }
                }
            });
            if (TextUtils.isEmpty(cancelLabel)) {
                cancelLabel = this.mActivity.getString(R.string.cancel);
            }
            dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultJavaScriptInterface.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog2, int which) {
                    if (!TextUtils.isEmpty(cancelFunctionName)) {
                        DefaultJavaScriptInterface.this.mActivity.runOnUiThread(new Runnable() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultJavaScriptInterface.4.1
                            @Override // java.lang.Runnable
                            public void run() {
                                DefaultJavaScriptInterface.this.mWebView.loadUrl("javascript:" + cancelFunctionName + "()");
                            }
                        });
                    }
                }
            }).setCancelable(false).show();
        }

        @JavascriptInterface
        public void openActivity(String params) {
            JSONObject parametersObject = null;
            if (!StringUtil.isBlankOrNull(params)) {
                try {
                    parametersObject = new JSONObject(params);
                } catch (JSONException e) {
                    Logger.e(Tag.JS, "can't parse params to json", e);
                }
            }
            ThisApplication.getContext().gotoActivity(parametersObject);
        }

        @JavascriptInterface
        public void sendAdEvent(String id, String name, int price, String eventType) {
            AdStoreOrderDetail detailAdstore = new AdStoreOrderDetail();
            detailAdstore.add(id, AdStoreUtil.getItemName(name), price, 1, price);
            String orderId = AdStoreUtil.getOrderId(eventType, id);
            AdStoreConnector.order(ThisApplication.getContext().getApplicationContext(), orderId, price, ThisApplication.getContext().getMe(), detailAdstore);
            if (ConfigUtil.isUS()) {
                StringBuilder customData = new StringBuilder(id);
                customData.append(":");
                customData.append(ThisApplication.getContext().getMe());
                customData.append(":");
                try {
                    customData.append(URLEncoder.encode(name, Const.DEFAULT_ENCODING));
                } catch (UnsupportedEncodingException e) {
                }
                AdXConnect.getAdXConnectEventInstance(ThisApplication.getContext().getApplicationContext(), Const.ADX_ORDER_DONE, StringFormatHelper.getInstance().formatCurrency(price), "USD", customData.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isValidUrl(String host) {
        return true;
    }

    /* loaded from: classes.dex */
    public static class DefaultWebChromeClient extends WebChromeClient {
        Activity mActivity;

        public DefaultWebChromeClient(Activity activity) {
            this.mActivity = activity;
        }

        @Override // android.webkit.WebChromeClient
        public void onCloseWindow(WebView window) {
            Logger.d("Common", "onCloseWindow");
            Uri uri = Uri.parse(window.getUrl());
            if (WebViewUtil.isValidUrl(uri.getHost())) {
                this.mActivity.finish();
            }
        }

        @Override // android.webkit.WebChromeClient
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            if (Logger.isDebugEnabled("Common")) {
                Logger.d("Common", "onJsAlert url=" + url + "; message=" + message);
            }
            new AlertDialog.Builder(this.mActivity).setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultWebChromeClient.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).setCancelable(false).show();
            return true;
        }

        @Override // android.webkit.WebChromeClient
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            if (Logger.isDebugEnabled("Common")) {
                Logger.d("Common", "onJsConfirm url=" + url + "; message=" + message);
            }
            new AlertDialog.Builder(this.mActivity).setMessage(message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultWebChromeClient.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.mercariapp.mercari.util.WebViewUtil.DefaultWebChromeClient.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).setCancelable(false).show();
            return true;
        }

        @Override // android.webkit.WebChromeClient
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            if (Logger.isDebugEnabled("Common")) {
                Logger.d("Common", "onJsPrompt url=" + url + "; message=" + message + "; defaultValue" + defaultValue);
            }
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override // android.webkit.WebChromeClient
        public void onReceivedTitle(WebView view, String title) {
            Logger.d("Common", "onReceivedTitle " + title);
            super.onReceivedTitle(view, title);
            this.mActivity.setTitle(title);
        }

        @Override // android.webkit.WebChromeClient
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            Logger.d("Common", "onShowCustomView");
            super.onShowCustomView(view, callback);
        }

        @Override // android.webkit.WebChromeClient
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            Logger.d("Common", "onCreateWindow");
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override // android.webkit.WebChromeClient
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (Logger.isDebugEnabled("Common")) {
                Logger.v("Common", consoleMessage.message() + ", " + consoleMessage.lineNumber() + ":" + consoleMessage.sourceId());
                return true;
            }
            return true;
        }

        public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(2 * spaceNeeded);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DefaultWebViewClient extends WebViewClient {
        private boolean mDuringShowErrorPage = false;
        private WebPageChangeListener mListener;

        DefaultWebViewClient(Activity activity, WebPageChangeListener listener) {
            this.mListener = listener;
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d("Common", "shouldOverrideUrlLoading url:" + url);
            Uri uri = Uri.parse(url);
            if (!WebViewUtil.isValidUrl(uri.getHost())) {
                view.stopLoading();
                return false;
            }
            return false;
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            this.mDuringShowErrorPage = true;
            webView.setVisibility(8);
            super.onReceivedError(webView, errorCode, description, failingUrl);
            if (Logger.isDebugEnabled(Tag.JS)) {
                Logger.e(Tag.JS, "code:" + errorCode + ";description:" + description + ";url" + failingUrl);
            }
            if (errorCode == -11) {
                webView.clearSslPreferences();
                webView.clearCache(true);
            } else {
                webView.clearCache(false);
            }
            WebViewUtil.loadUrl(webView, "file:///android_asset/html/read_error_us.html");
        }

        @Override // android.webkit.WebViewClient
        public void onLoadResource(WebView view, String url) {
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            if (!this.mDuringShowErrorPage) {
                if (Logger.isDebugEnabled("Common")) {
                    Logger.d("Common", "onPageFinished url:" + url + ";size" + view.getWidth() + "*" + view.getHeight() + "; contentHeight=" + view.getContentHeight());
                }
                super.onPageFinished(view, url);
                view.setVisibility(0);
                view.requestFocus(TransportMediator.KEYCODE_MEDIA_RECORD);
                if (this.mListener != null) {
                    this.mListener.onPageFinished(view, url);
                }
            }
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ("file:///android_asset/html/read_error_us.html".equals(url)) {
                this.mDuringShowErrorPage = false;
            }
            if (!this.mDuringShowErrorPage) {
                Logger.d("Common", "onPageStarted url:" + url);
                super.onPageStarted(view, url, favicon);
                view.setVisibility(8);
                if (this.mListener != null) {
                    this.mListener.onPageStarted(view, url, favicon);
                }
            }
        }
    }
}
