package com.inmobi.commons.analytics.iat.impl.net;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.net.http.SslError;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.inmobi.commons.analytics.iat.impl.AdTrackerConstants;
import com.inmobi.commons.analytics.iat.impl.AdTrackerUtils;
import com.inmobi.commons.analytics.iat.impl.Goal;
import com.inmobi.commons.analytics.iat.impl.config.AdTrackerEventType;
import com.inmobi.commons.internal.FileOperations;
import com.inmobi.commons.internal.InternalSDKUtil;
import com.inmobi.commons.internal.Log;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class AdTrackerWebViewLoader {
    private static WebView a = null;
    private static AtomicBoolean b;
    private long c = 0;
    private long d = 0;
    private Goal e;

    public static boolean isWebviewLoading() {
        return b.get();
    }

    public static WebView getWebview() {
        return a;
    }

    /* loaded from: classes.dex */
    class d implements Runnable {
        d() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AtomicBoolean unused = AdTrackerWebViewLoader.b = new AtomicBoolean(false);
            WebView unused2 = AdTrackerWebViewLoader.a = new WebView(InternalSDKUtil.getContext());
            AdTrackerWebViewLoader.a.setWebViewClient(new MyWebViewClient());
            AdTrackerWebViewLoader.a.getSettings().setJavaScriptEnabled(true);
            AdTrackerWebViewLoader.a.getSettings().setCacheMode(2);
            AdTrackerWebViewLoader.a.addJavascriptInterface(new JSInterface(), AdTrackerConstants.IATNAMESPACE);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public AdTrackerWebViewLoader() {
        AdTrackerNetworkInterface.getUIHandler().post(new d());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AdTrackerWebViewLoader.a != null) {
                AdTrackerWebViewLoader.a.stopLoading();
                AdTrackerWebViewLoader.a.destroy();
                WebView unused = AdTrackerWebViewLoader.a = null;
                AdTrackerWebViewLoader.b.set(false);
            }
        }
    }

    public void deinit(int i) {
        AdTrackerNetworkInterface.getUIHandler().postDelayed(new a(), i);
    }

    /* loaded from: classes.dex */
    class b implements Runnable {
        b() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AdTrackerWebViewLoader.a != null) {
                AdTrackerWebViewLoader.b.set(false);
            }
            AdTrackerWebViewLoader.a.stopLoading();
        }
    }

    public void stopLoading() {
        AdTrackerNetworkInterface.getUIHandler().post(new b());
    }

    /* loaded from: classes.dex */
    protected class MyWebViewClient extends WebViewClient {
        protected MyWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            try {
                if (AdTrackerWebViewLoader.b.compareAndSet(true, false)) {
                    AdTrackerWebViewLoader.this.d = System.currentTimeMillis() - AdTrackerWebViewLoader.this.c;
                    if (str.contains("iat")) {
                        e a = AdTrackerWebViewLoader.this.a(str.substring(7));
                        if (a == null) {
                            AdTrackerNetworkInterface.setWebviewUploadStatus(false);
                        } else {
                            FileOperations.setPreferences(InternalSDKUtil.getContext(), AdTrackerConstants.IMPREF_FILE, AdTrackerConstants.ERRORCODE, Integer.toString(a.a));
                            if (5000 == a.a) {
                                if (AdTrackerWebViewLoader.this.b(a.b)) {
                                    AdTrackerNetworkInterface.setWebviewUploadStatus(true);
                                } else {
                                    AdTrackerNetworkInterface.setWebviewUploadStatus(false);
                                }
                            } else {
                                AdTrackerNetworkInterface.setWebviewUploadStatus(false);
                                AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_FAILURE, AdTrackerWebViewLoader.this.e, 0, 0L, a.a, null);
                            }
                        }
                    }
                    synchronized (AdTrackerNetworkInterface.getNetworkThread()) {
                        AdTrackerNetworkInterface.getNetworkThread().notify();
                    }
                }
                webView.loadUrl(str);
            } catch (Exception e) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Exception Should Override Error", e);
            }
            return true;
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView webView, int i, String str, String str2) {
            try {
                if (AdTrackerWebViewLoader.b.compareAndSet(true, false)) {
                    AdTrackerNetworkInterface.setWebviewUploadStatus(false);
                    synchronized (AdTrackerNetworkInterface.getNetworkThread()) {
                        AdTrackerNetworkInterface.getNetworkThread().notify();
                    }
                    AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_FAILURE, AdTrackerWebViewLoader.this.e, 0, 0L, i, null);
                    Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Webview Received Error");
                }
            } catch (Exception e) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Exception onReceived Error", e);
            }
            super.onReceivedError(webView, i, str, str2);
        }

        @Override // android.webkit.WebViewClient
        @TargetApi(8)
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "SSL Error. Proceeding" + sslError);
            try {
                if (AdTrackerWebViewLoader.b.compareAndSet(true, false)) {
                    AdTrackerNetworkInterface.setWebviewUploadStatus(false);
                    synchronized (AdTrackerNetworkInterface.getNetworkThread()) {
                        AdTrackerNetworkInterface.getNetworkThread().notify();
                    }
                    AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_FAILURE, AdTrackerWebViewLoader.this.e, 0, 0L, sslError.getPrimaryError(), null);
                    Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Webview Received SSL Error");
                }
            } catch (Exception e) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Exception onReceived SSL Error", e);
            }
            sslErrorHandler.proceed();
        }
    }

    /* loaded from: classes.dex */
    protected static class JSInterface {
        protected JSInterface() {
        }

        @JavascriptInterface
        public String getParams() {
            String preferences = FileOperations.getPreferences(InternalSDKUtil.getContext(), AdTrackerConstants.IMPREF_FILE, "referrer");
            String webViewRequestParam = AdTrackerRequestResponseBuilder.getWebViewRequestParam();
            if (preferences != null) {
                webViewRequestParam = webViewRequestParam + "&referrer=" + preferences;
            }
            Log.debug(AdTrackerConstants.IAT_LOGGING_TAG, "Request param for webview" + webViewRequestParam);
            return webViewRequestParam;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public e a(String str) {
        Exception exc;
        int i;
        e eVar = new e();
        try {
            String str2 = null;
            int i2 = 0;
            for (String str3 : str.split("&")) {
                try {
                    String[] split = str3.split("=");
                    for (int i3 = 0; i3 < split.length; i3++) {
                        if (AdTrackerConstants.ERROR.equals(split[i3])) {
                            i2 = Integer.parseInt(split[i3 + 1]);
                        } else if (AdTrackerConstants.RESPONSE.equals(split[i3])) {
                            str2 = split[i3 + 1];
                        }
                    }
                } catch (Exception e2) {
                    exc = e2;
                    i = i2;
                    AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_FAILURE, this.e, 0, 0L, i, null);
                    Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Check content Exception", exc);
                    return null;
                }
            }
            eVar.a = i2;
            eVar.b = str2;
            if (5003 == i2) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Webview Timeout " + str2);
            } else if (5001 == i2) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Invalid params passed " + str2);
            } else if (5002 == i2) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "XMLHTTP request not supported " + str2);
            } else if (5005 == i2) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Invalid JSON Response " + str2);
            } else if (5004 == i2) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Webview Server Error " + str2);
            } else if (5000 == i2) {
                Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Webview response " + URLDecoder.decode(str2, "utf-8"));
            }
            return eVar;
        } catch (Exception e3) {
            exc = e3;
            i = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean b(String str) {
        boolean z;
        try {
            JSONObject jSONObject = new JSONObject(URLDecoder.decode(str, "utf-8"));
            JSONObject jSONObject2 = jSONObject.getJSONObject(AdTrackerConstants.VALIDIDS);
            String string = jSONObject.getString(AdTrackerConstants.ERRORMSG);
            int i = jSONObject.getInt(AdTrackerConstants.TIMETOLIVE);
            int i2 = jSONObject.getInt(AdTrackerConstants.ERRORCODE);
            if (i2 != 6000) {
                AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_FAILURE, this.e, 0, 0L, i2, null);
                Log.debug(AdTrackerConstants.IAT_LOGGING_TAG, "Failed to upload goal in webview" + string);
                z = false;
            } else {
                AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_SUCCESS, this.e, 0, this.d, 0, null);
                String jSONObject3 = jSONObject2.toString();
                if (i2 == 6001) {
                    jSONObject3 = null;
                }
                FileOperations.setPreferences(InternalSDKUtil.getContext(), AdTrackerConstants.IMPREF_FILE, AdTrackerConstants.VALIDIDS, jSONObject3);
                FileOperations.setPreferences(InternalSDKUtil.getContext(), AdTrackerConstants.IMPREF_FILE, AdTrackerConstants.TIMETOLIVE, Integer.toString(i));
                z = true;
            }
            return z;
        } catch (Exception e2) {
            AdTrackerUtils.reportMetric(AdTrackerEventType.GOAL_FAILURE, this.e, 0, 0L, AdTrackerConstants.EXCEPTION, e2.getMessage());
            Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Failed to upload goal in webview", e2);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class e {
        private int a = 0;
        private String b = null;
    }

    public boolean loadWebview(Goal goal) {
        this.e = goal;
        try {
            AdTrackerNetworkInterface.getUIHandler().post(new c());
            return true;
        } catch (Exception e2) {
            Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, e2.toString());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class c implements Runnable {
        c() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AdTrackerWebViewLoader.this.c = System.currentTimeMillis();
            AdTrackerWebViewLoader.b.set(true);
            Log.internal(AdTrackerConstants.IAT_LOGGING_TAG, "Load Webview: " + AdTrackerNetworkInterface.b());
            AdTrackerWebViewLoader.a.loadUrl(AdTrackerNetworkInterface.b());
        }
    }
}
