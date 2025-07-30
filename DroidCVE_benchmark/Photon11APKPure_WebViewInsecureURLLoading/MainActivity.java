package com.photoneconomy.photon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wiserbit.adjson.ad_manager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    static final int INPUT_FILE_REQUEST_CODE = 1;
    static final int PERMISSION_AUDIO = 106;
    static final int PERMISSION_LOC = 100;
    static final int PERMISSION_VIDEO_CAPTURE1 = 1001;
    static final int PERMISSION_VIDEO_CAPTURE2 = 1002;
    private static final String TAG = "MainActivity";
    static String currentUrl = "";
    static boolean homeLoaded = false;
    SwipeRefreshLayout NavigateProgressBar;
    ad_manager _ad_manager;
    private String mCameraPhotoPath;
    private ValueCallback<Uri[]> mFilePathCallback;
    private WebView mWebView;
    PermissionRequest permissionRequest;
    private WebView splash_mWebView;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences prefs = null;
    int width = 0;
    int height = 0;
    boolean display_error = false;
    boolean no_internet = false;
    GeolocationPermissions.Callback mGeoLocationCallback = null;
    String mGeoLocationRequestOrigin = null;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(-1);
        this._ad_manager = new ad_manager(this, "https://ads.webintoapp.com/ads/", "23762", 3);
        WebView webView = (WebView) findViewById(R.id.activity_splash_webview);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/htmlapp/helpers/loading.html");
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        this.prefs = getSharedPreferences(BuildConfig.APPLICATION_ID, 0);
        WebView webView2 = (WebView) findViewById(R.id.activity_main_webview);
        this.mWebView = webView2;
        webView2.setWebChromeClient(new WebChromeClient() { // from class: com.photoneconomy.photon.MainActivity.1
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public void MyWebClient() {
            }

            @Override // android.webkit.WebChromeClient
            public void onCloseWindow(WebView webView3) {
            }

            @Override // android.webkit.WebChromeClient
            public boolean onCreateWindow(WebView webView3, boolean z, boolean z2, Message message) {
                return true;
            }

            @Override // android.webkit.WebChromeClient
            public Bitmap getDefaultVideoPoster() {
                MainActivity mainActivity = MainActivity.this;
                if (mainActivity == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(mainActivity.getApplicationContext().getResources(), R.attr.buttonBarPositiveButtonStyle);
            }

            @Override // android.webkit.WebChromeClient
            public void onHideCustomView() {
                ((FrameLayout) MainActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                MainActivity.this.setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            @Override // android.webkit.WebChromeClient
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback customViewCallback) {
                if (this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = view;
                this.mOriginalSystemUiVisibility = MainActivity.this.getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = MainActivity.this.getRequestedOrientation();
                this.mCustomViewCallback = customViewCallback;
                ((FrameLayout) MainActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846);
            }

            @Override // android.webkit.WebChromeClient
            public void onGeolocationPermissionsShowPrompt(String str, GeolocationPermissions.Callback callback) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 100);
                    MainActivity.this.mGeoLocationRequestOrigin = str;
                    MainActivity.this.mGeoLocationCallback = callback;
                    return;
                }
                callback.invoke(str, true, true);
            }

            @Override // android.webkit.WebChromeClient
            public void onPermissionRequest(PermissionRequest permissionRequest) {
                MainActivity.this.permissionRequest = permissionRequest;
                if (Build.VERSION.SDK_INT >= 21) {
                    for (String str : permissionRequest.getResources()) {
                        if (str.equals("android.webkit.resource.VIDEO_CAPTURE")) {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.RECORD_AUDIO") == 0) {
                                MainActivity.this.getVideoCapturePermission();
                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.RECORD_AUDIO"}, 1002);
                                return;
                            }
                        }
                    }
                }
            }

            /* JADX WARN: Removed duplicated region for block: B:13:0x0087  */
            /* JADX WARN: Removed duplicated region for block: B:20:0x0049  */
            /* JADX WARN: Removed duplicated region for block: B:9:0x0082  */
            @Override // android.webkit.WebChromeClient
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean onShowFileChooser(android.webkit.WebView r4, android.webkit.ValueCallback<android.net.Uri[]> r5, android.webkit.WebChromeClient.FileChooserParams r6) {
                /*
                    r3 = this;
                    com.photoneconomy.photon.MainActivity r4 = com.photoneconomy.photon.MainActivity.this
                    android.webkit.ValueCallback r4 = com.photoneconomy.photon.MainActivity.access$100(r4)
                    r6 = 0
                    if (r4 == 0) goto L12
                    com.photoneconomy.photon.MainActivity r4 = com.photoneconomy.photon.MainActivity.this
                    android.webkit.ValueCallback r4 = com.photoneconomy.photon.MainActivity.access$100(r4)
                    r4.onReceiveValue(r6)
                L12:
                    com.photoneconomy.photon.MainActivity r4 = com.photoneconomy.photon.MainActivity.this
                    com.photoneconomy.photon.MainActivity.access$102(r4, r5)
                    android.content.Intent r4 = new android.content.Intent
                    java.lang.String r5 = "android.media.action.IMAGE_CAPTURE"
                    r4.<init>(r5)
                    com.photoneconomy.photon.MainActivity r5 = com.photoneconomy.photon.MainActivity.this
                    android.content.pm.PackageManager r5 = r5.getPackageManager()
                    android.content.ComponentName r5 = r4.resolveActivity(r5)
                    if (r5 == 0) goto L6c
                    com.photoneconomy.photon.MainActivity r5 = com.photoneconomy.photon.MainActivity.this     // Catch: java.io.IOException -> L3e
                    java.io.File r5 = com.photoneconomy.photon.MainActivity.access$200(r5)     // Catch: java.io.IOException -> L3e
                    java.lang.String r0 = "PhotoPath"
                    com.photoneconomy.photon.MainActivity r1 = com.photoneconomy.photon.MainActivity.this     // Catch: java.io.IOException -> L3c
                    java.lang.String r1 = com.photoneconomy.photon.MainActivity.access$300(r1)     // Catch: java.io.IOException -> L3c
                    r4.putExtra(r0, r1)     // Catch: java.io.IOException -> L3c
                    goto L47
                L3c:
                    r0 = move-exception
                    goto L40
                L3e:
                    r0 = move-exception
                    r5 = r6
                L40:
                    java.lang.String r1 = "MainActivity"
                    java.lang.String r2 = "Unable to create Image File"
                    android.util.Log.e(r1, r2, r0)
                L47:
                    if (r5 == 0) goto L6d
                    com.photoneconomy.photon.MainActivity r6 = com.photoneconomy.photon.MainActivity.this
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r1 = "file:"
                    r0.append(r1)
                    java.lang.String r1 = r5.getAbsolutePath()
                    r0.append(r1)
                    java.lang.String r0 = r0.toString()
                    com.photoneconomy.photon.MainActivity.access$302(r6, r0)
                    android.net.Uri r5 = android.net.Uri.fromFile(r5)
                    java.lang.String r6 = "output"
                    r4.putExtra(r6, r5)
                L6c:
                    r6 = r4
                L6d:
                    android.content.Intent r4 = new android.content.Intent
                    java.lang.String r5 = "android.intent.action.GET_CONTENT"
                    r4.<init>(r5)
                    java.lang.String r5 = "android.intent.category.OPENABLE"
                    r4.addCategory(r5)
                */
                //  java.lang.String r5 = "*/*"
                /*
                    r4.setType(r5)
                    r5 = 0
                    r0 = 1
                    if (r6 == 0) goto L87
                    android.content.Intent[] r1 = new android.content.Intent[r0]
                    r1[r5] = r6
                    goto L89
                L87:
                    android.content.Intent[] r1 = new android.content.Intent[r5]
                L89:
                    android.content.Intent r5 = new android.content.Intent
                    java.lang.String r6 = "android.intent.action.CHOOSER"
                    r5.<init>(r6)
                    java.lang.String r6 = "android.intent.extra.INTENT"
                    r5.putExtra(r6, r4)
                    java.lang.String r4 = "android.intent.extra.TITLE"
                    java.lang.String r6 = "Files Chooser"
                    r5.putExtra(r4, r6)
                    java.lang.String r4 = "android.intent.extra.INITIAL_INTENTS"
                    r5.putExtra(r4, r1)
                    com.photoneconomy.photon.MainActivity r4 = com.photoneconomy.photon.MainActivity.this
                    r4.startActivityForResult(r5, r0)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: com.photoneconomy.photon.MainActivity.AnonymousClass1.onShowFileChooser(android.webkit.WebView, android.webkit.ValueCallback, android.webkit.WebChromeClient$FileChooserParams):boolean");
            }
        });
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.NavigateProgressBar = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.setDownloadListener(new DownloadListener() { // from class: com.photoneconomy.photon.MainActivity.2
            @Override // android.webkit.DownloadListener
            public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (MainActivity.this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        Log.v(MainActivity.TAG, "Permission is granted");
                        MainActivity.this.downloadDialog(str, str2, str3, str4);
                        return;
                    } else {
                        Log.v(MainActivity.TAG, "Permission is revoked");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                        return;
                    }
                }
                Log.v(MainActivity.TAG, "Permission is granted");
                MainActivity.this.downloadDialog(str, str2, str3, str4);
            }
        });
        this.mWebView.setWebViewClient(new WebViewClient() { // from class: com.photoneconomy.photon.MainActivity.3
            void IntentFallvack(WebView webView3, Intent intent) {
                String stringExtra = intent.getStringExtra("browser_fallback_url");
                if (stringExtra != null) {
                    webView3.loadUrl(stringExtra);
                }
            }

            @Override // android.webkit.WebViewClient
            public void onPageStarted(WebView webView3, String str, Bitmap bitmap) {
                super.onPageStarted(webView3, str, bitmap);
                MainActivity.currentUrl = str;
                if (MainActivity.homeLoaded) {
                    MainActivity.this.showProgress();
                }
                if (MainActivity.checkInternetConnection(MainActivity.this)) {
                    return;
                }
                MainActivity.this.hideProgress();
            }

            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView3, String str) {
                if (str.startsWith("https") || str.startsWith("http")) {
                    return false;
                }
                if (str.startsWith("mailto:")) {
                    MainActivity.this.startActivity(new Intent("android.intent.action.SENDTO", Uri.parse(str)));
                } else if (str.startsWith("tel:")) {
                    MainActivity.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(str)));
                } else if (str.startsWith("intent:")) {
                    Uri parse = Uri.parse(str);
                    PackageManager packageManager = MainActivity.this.getPackageManager();
                    Intent data = new Intent("android.intent.action.VIEW").setData(parse);
                    if (data.resolveActivity(packageManager) != null) {
                        MainActivity.this.startActivity(data);
                        return true;
                    }
                    try {
                        Intent parseUri = Intent.parseUri(str, 1);
                        if (parseUri.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            MainActivity.this.startActivity(parseUri);
                            return true;
                        }
                        Intent data2 = new Intent("android.intent.action.VIEW").setData(Uri.parse("market://details?id=" + parseUri.getPackage()));
                        if (data2.resolveActivity(packageManager) != null) {
                            MainActivity.this.startActivity(data2);
                            return true;
                        }
                        IntentFallvack(webView3, parseUri);
                    } catch (URISyntaxException unused) {
                    }
                }
                return true;
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView3, String str) {
                super.onPageFinished(MainActivity.this.mWebView, str);
                MainActivity.this.hideProgress();
                MainActivity.this.swipeRefreshLayout.setRefreshing(false);
                MainActivity.this.findViewById(R.id.activity_splash_webview).setVisibility(8);
                MainActivity.this.findViewById(R.id.activity_main_webview).setVisibility(0);
                MainActivity.this.display_error = true;
                if (MainActivity.homeLoaded) {
                    return;
                }
                MainActivity.homeLoaded = true;
            }

            @Override // android.webkit.WebViewClient
            public void onReceivedError(WebView webView3, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                if (MainActivity.this.display_error) {
                    return;
                }
                MainActivity.this.mWebView.loadUrl("file:///android_asset/htmlapp/helpers/error.html");
                MainActivity.this.display_error = true;
            }

            @Override // android.webkit.WebViewClient
            public void onLoadResource(WebView webView3, String str) {
                if (MainActivity.checkInternetConnection(MainActivity.this)) {
                    return;
                }
                boolean z = MainActivity.this.no_internet;
                MainActivity.this.no_internet = true;
            }
        });
        SetWebView(this.mWebView);
        if (!checkInternetConnection(this)) {
            this.mWebView.loadUrl("file:///android_asset/htmlapp/helpers/error.html");
            this.no_internet = true;
            return;
        }
        Intent intent = getIntent();
        if (intent.getData() != null) {
            String dataString = intent.getDataString();
            if (dataString.startsWith("https://www.photon-economy.ga")) {
                this.mWebView.loadUrl(dataString);
            } else {
                this.mWebView.loadUrl("https://www.photon-economy.ga");
            }
        } else {
            this.mWebView.loadUrl("https://www.photon-economy.ga");
        }
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.photoneconomy.photon.MainActivity.4
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                MainActivity.this.mWebView.reload();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getVideoCapturePermission() {
        PermissionRequest permissionRequest = this.permissionRequest;
        permissionRequest.grant(permissionRequest.getResources());
    }

    public static boolean checkAudioPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, "android.permission.RECORD_AUDIO") == 0;
    }

    public static void getAudioPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{"android.permission.RECORD_AUDIO"}, 106);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length <= 0 || iArr[0] != 0) {
            return;
        }
        if (i == 100) {
            GeolocationPermissions.Callback callback = this.mGeoLocationCallback;
            if (callback != null) {
                callback.invoke(this.mGeoLocationRequestOrigin, true, true);
            }
        } else if (i == 1001) {
            if (!checkAudioPermission(this)) {
                getAudioPermission(this);
            } else {
                getVideoCapturePermission();
            }
        } else if (i == 1002) {
            getVideoCapturePermission();
        }
        this.mWebView.reload();
    }

    public void downloadDialog(final String str, final String str2, String str3, String str4) {
        if (str.startsWith("blob")) {
            this.mWebView.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(str, str4));
            return;
        }
        final String guessFileName = URLUtil.guessFileName(str, str3, str4);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download");
        builder.setMessage("Download File " + guessFileName);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.photoneconomy.photon.MainActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(str));
                request.addRequestHeader("User-Agent", str2);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(1);
                DownloadManager downloadManager = (DownloadManager) MainActivity.this.getSystemService("download");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, guessFileName);
                downloadManager.enqueue(request);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.photoneconomy.photon.MainActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public void showProgress() {
        this.NavigateProgressBar.setRefreshing(true);
    }

    public void hideProgress() {
        this.NavigateProgressBar.setRefreshing(false);
    }

    private void SetWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setSupportZoom(true);
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT > 17) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(1);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.prefs.getBoolean("firstrun", true)) {
            WindowManager windowManager = (WindowManager) getSystemService("window");
            Point point = new Point();
            windowManager.getDefaultDisplay().getRealSize(point);
            this.width = point.x;
            this.height = point.y;
            RequestQueue newRequestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(1, "https://install.webintoapp.com/install/", new Response.Listener<String>() { // from class: com.photoneconomy.photon.MainActivity.7
                @Override // com.android.volley.Response.Listener
                public void onResponse(String str) {
                    Log.d("Install", "Response is: " + str);
                    Log.d("Sent install to server", "Success");
                    MainActivity.this.prefs.edit().putBoolean("firstrun", false).apply();
                }
            }, new Response.ErrorListener() { // from class: com.photoneconomy.photon.MainActivity.8
                @Override // com.android.volley.Response.ErrorListener
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("Sent install to server", "Error:" + volleyError.toString());
                }
            }) { // from class: com.photoneconomy.photon.MainActivity.9
                @Override // com.android.volley.Request
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override // com.android.volley.Request
                protected Map<String, String> getParams() {
                    HashMap hashMap = new HashMap();
                    hashMap.put("key", "lwlzpTyPCOKsVwdtIAVAnRlExfovWubz");
                    hashMap.put("app_version", BuildConfig.VERSION_NAME);
                    hashMap.put("device", "Android");
                    hashMap.put("device_version", System.getProperty("os.version"));
                    hashMap.put("resolution", MainActivity.this.width + "x" + MainActivity.this.height);
                    return hashMap;
                }
            };
            this.prefs.edit().putBoolean("firstrun", false).apply();
            newRequestQueue.add(stringRequest);
        }
        this._ad_manager.PauseRunning();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        this._ad_manager.PauseRunning();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_home) {
            this.mWebView.loadUrl("https://www.photon-economy.ga");
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 4) {
            if (this.no_internet) {
                finish();
            }
            if (this.mWebView.canGoBack()) {
                this.mWebView.goBack();
            } else {
                this._ad_manager.popup(true);
            }
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    void IntentFallvack(WebView webView, Intent intent) {
        String stringExtra = intent.getStringExtra("browser_fallback_url");
        if (stringExtra != null) {
            webView.loadUrl(stringExtra);
        }
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public File createImageFile() throws IOException {
        return File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    public static void openUrlInChrome(Activity activity, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        intent.addFlags(268435456);
        intent.setPackage("com.android.chrome");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            intent.setPackage(null);
            activity.startActivity(intent);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri[] uriArr;
        if (i != 1 || this.mFilePathCallback == null) {
            super.onActivityResult(i, i2, intent);
            return;
        }
        if (i2 == -1) {
            if (intent == null) {
                String str = this.mCameraPhotoPath;
                if (str != null) {
                    uriArr = new Uri[]{Uri.parse(str)};
                }
            } else {
                String dataString = intent.getDataString();
                if (dataString != null) {
                    uriArr = new Uri[]{Uri.parse(dataString)};
                }
            }
            this.mFilePathCallback.onReceiveValue(uriArr);
            this.mFilePathCallback = null;
        }
        uriArr = null;
        this.mFilePathCallback.onReceiveValue(uriArr);
        this.mFilePathCallback = null;
    }
}
