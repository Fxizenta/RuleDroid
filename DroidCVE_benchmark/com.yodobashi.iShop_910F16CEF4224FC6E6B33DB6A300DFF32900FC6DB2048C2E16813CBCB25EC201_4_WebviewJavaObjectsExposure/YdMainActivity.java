package com.yodobashi.iShop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomButtonsController;
import com.felicanetworks.mfc.AppInfo;
import com.felicanetworks.mfc.Felica;
import com.felicanetworks.mfc.FelicaEventListener;
import com.yodobashi.iShop.YdHorizontalScrollView;
import com.yodobashi.iShop.YdResizeLayout;
import com.yodobashi.iShop.YdWebView;
import com.yodobashi.iShop.YdWebViewClient;
import com.yodobashi.iShop.json.JsonCtrlHandler;
import com.yodobashi.iShop.json.JsonInterface;
import com.yodobashi.iShop.util.LLog;
import com.yodobashi.iShop.zxing.CaptureActivity;
import com.yodobashi.iShop.zxing.camera.open.OpenCameraInterface;
import com.yodobashi.iShop.zxing.camera.open.OpenCameraManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class YdMainActivity extends Activity {
    View app;
    ImageView btnAudio;
    ImageView btnAudiocode;
    ImageView btnBarcode;
    ImageView btnSearch;
    ImageView btnSlide;
    int btnWidth;
    ImageView cartClick;
    ImageView cartCountbg;
    TextView cartId;
    private DisplayMetrics dm;
    YdEditText edit;
    AutoCompleteTextView editBefore;
    private String failingUrl;
    View imageMenu;
    View listView;
    ImageView loginClick;
    TextView loginId;
    LinearLayout menu_item;
    View oldView;
    ImageView pointClick;
    TextView pointId;
    YdHorizontalScrollView scrollView;
    ViewGroup searchBar;
    ViewGroup searchBarEx;
    ViewGroup searchbarafter;
    ViewGroup suggestAudioItem;
    ViewGroup suggestBarcodeItem;
    ViewGroup suggestBarcodeView;
    ListView suggestList;
    View top;
    YdWebView view;
    private ProgressDialog waitDialog;
    Handler handler = new Handler();
    private RelativeLayout offLineLayout = null;
    private boolean isOffLine = false;
    boolean menuOut = false;
    int oldIndex = -1;
    boolean isInputMode = false;
    private String[] AutoCompleteWords = null;
    DataInfo dataInfo = DataInfo.getInstance();
    FileManager fileManager = new FileManager(this);
    JsonInterface jsonIf = new JsonInterface();
    private boolean isChecking = false;
    private boolean isOncreate = false;
    private boolean isSuggestMode = false;
    private String searchKey = "";
    YdGpsInfoScan location = null;
    private boolean isBarcodeRunning = false;
    private boolean isFrontCamera = false;
    private boolean isNotLocationExec = false;
    private FelicaConnection mFelicaConnection = null;
    private InputHandler mHandler = new InputHandler();
    private final YdResizeLayout.OnResizeListener mOnResizeListener = new YdResizeLayout.OnResizeListener() { // from class: com.yodobashi.iShop.YdMainActivity.4
        @Override // com.yodobashi.iShop.YdResizeLayout.OnResizeListener
        public void OnResize(int w, int h, int oldw, int oldh) {
            int change = 1;
            if (h < oldh) {
                change = 2;
            }
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = change;
            YdMainActivity.this.mHandler.sendMessage(msg);
        }
    };
    private final YdHorizontalScrollView.IOnKeyboardStateChangedListener mIOnKeyboardStateChangedListener = new YdHorizontalScrollView.IOnKeyboardStateChangedListener() { // from class: com.yodobashi.iShop.YdMainActivity.5
        @Override // com.yodobashi.iShop.YdHorizontalScrollView.IOnKeyboardStateChangedListener
        public void onKeyboardStateChanged(int state) {
            switch (state) {
                case -4:
                    Message msg = new Message();
                    msg.what = 3;
                    YdMainActivity.this.mHandler.sendMessageDelayed(msg, 200L);
                    return;
                case -3:
                    YdMainActivity.this.isInputMode = true;
                    return;
                case PagerAdapter.POSITION_NONE /* -2 */:
                    LLog.i("isSuggestMode:" + YdMainActivity.this.isSuggestMode);
                    YdMainActivity.this.isInputMode = false;
                    return;
                default:
                    return;
            }
        }
    };
    private final View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.6
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            int tag = ((Integer) v.getTag()).intValue();
            if (tag >= 52 && tag <= (Constants.allMenulistIds.length + 52) - 1) {
                if (YdMainActivity.this.menuOut) {
                    YdMainActivity.this.SlideMenuOnOff();
                    if (YdMainActivity.this.oldIndex != -1 && YdMainActivity.this.oldIndex != tag) {
                        if (YdMainActivity.this.oldIndex == (Constants.allMenulistIds.length + 52) - 1) {
                            Drawable drawable = YdMainActivity.this.getResources().getDrawable(R.drawable.menu_list_bg_last);
                            YdMainActivity.this.oldView.setBackgroundDrawable(drawable);
                        } else {
                            Drawable drawable2 = YdMainActivity.this.getResources().getDrawable(R.drawable.menu_list_bg);
                            YdMainActivity.this.oldView.setBackgroundDrawable(drawable2);
                        }
                    }
                    v.setBackgroundResource(R.drawable.slide_background_in);
                    YdMainActivity.this.oldIndex = tag;
                    YdMainActivity.this.oldView = v;
                    Message msg = new Message();
                    msg.what = 2;
                    msg.arg1 = tag;
                    YdMainActivity.this.mHandler.sendMessageDelayed(msg, 200L);
                } else {
                    return;
                }
            }
            switch (tag) {
                case 1:
                    String editStr = YdMainActivity.this.edit.getText().toString().replaceAll("^[\u3000*| *]*", "").replaceAll("[\u3000*| *]*$", "");
                    if (!"".equals(editStr)) {
                        YdMainActivity.this.hideSoftInput();
                        Message msg2 = new Message();
                        msg2.what = 5;
                        msg2.obj = YdMainActivity.this.edit.getText().toString();
                        YdMainActivity.this.mHandler.sendMessageDelayed(msg2, 100L);
                        return;
                    }
                    return;
                case 2:
                    if (!YdMainActivity.this.getBarcodeRunFlg()) {
                        if (!YdMainActivity.this.dataInfo.getCameraCanUseFlg()) {
                            YdMainActivity.this.cameraUnsupportDialog();
                            return;
                        }
                        YdMainActivity.this.setBarcodeRunFlg(true);
                        YdMainActivity.this.hideSoftInput();
                        Message msg3 = new Message();
                        msg3.what = 4;
                        YdMainActivity.this.mHandler.sendMessageDelayed(msg3, 100L);
                        return;
                    }
                    return;
                case 3:
                    if (!YdMainActivity.this.menuOut) {
                        YdMainActivity.this.hideSoftInput();
                        try {
                            Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                            intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
                            intent.putExtra("android.speech.extra.PROMPT", "お話しください");
                            YdMainActivity.this.startActivityForResult(intent, 2);
                            return;
                        } catch (ActivityNotFoundException e) {
                            LLog.d(e.getLocalizedMessage() + " " + e.getCause());
                            YdMainActivity.this.showDialog(4);
                            return;
                        }
                    }
                    return;
                case 4:
                    YdMainActivity.this.hideSoftInput();
                    YdMainActivity.this.barcodeActivityStart();
                    return;
                case 5:
                    if (YdMainActivity.this.isInputMode) {
                        YdMainActivity.this.hideSoftInput();
                        return;
                    } else {
                        YdMainActivity.this.SlideMenuOnOff();
                        return;
                    }
                case 6:
                    if (!YdMainActivity.this.menuOut) {
                        YdMainActivity.this.MenuListClickListener(53);
                        return;
                    }
                    return;
                case 7:
                    if (!YdMainActivity.this.menuOut) {
                        YdMainActivity.this.MenuListClickListener(55);
                        return;
                    }
                    return;
                case 8:
                    if (!YdMainActivity.this.menuOut) {
                        YdMainActivity.this.MenuListClickListener(54);
                        return;
                    }
                    return;
                case 9:
                    YdMainActivity.this.hideSoftInput();
                    Message msgA = new Message();
                    msgA.what = 9;
                    YdMainActivity.this.mHandler.sendMessageDelayed(msgA, 100L);
                    return;
                default:
                    return;
            }
        }
    };
    private final YdWebView.OnTouchEventListener mOnTouchEventListener = new YdWebView.OnTouchEventListener() { // from class: com.yodobashi.iShop.YdMainActivity.9
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Failed to find 'out' block for switch in B:6:0x0051. Please report as an issue. */
        /* JADX WARN: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
        @Override // com.yodobashi.iShop.YdWebView.OnTouchEventListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r5) {
            /*
                r4 = this;
                r1 = 0
                r0 = 1
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "onTouchEvent menuOut:"
                java.lang.StringBuilder r2 = r2.append(r3)
                com.yodobashi.iShop.YdMainActivity r3 = com.yodobashi.iShop.YdMainActivity.this
                boolean r3 = r3.menuOut
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.String r3 = ",isInputMode:"
                java.lang.StringBuilder r2 = r2.append(r3)
                com.yodobashi.iShop.YdMainActivity r3 = com.yodobashi.iShop.YdMainActivity.this
                boolean r3 = r3.isInputMode
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.String r2 = r2.toString()
                com.yodobashi.iShop.util.LLog.i(r2)
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "ev.getAction():"
                java.lang.StringBuilder r2 = r2.append(r3)
                int r3 = r5.getAction()
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.String r2 = r2.toString()
                com.yodobashi.iShop.util.LLog.i(r2)
                com.yodobashi.iShop.YdMainActivity r2 = com.yodobashi.iShop.YdMainActivity.this
                boolean r2 = com.yodobashi.iShop.YdMainActivity.access$1100(r2)
                if (r2 == 0) goto L4d
            L4c:
                return r0
            L4d:
                int r2 = r5.getAction()
                switch(r2) {
                    case 0: goto L6f;
                    case 1: goto L5c;
                    default: goto L54;
                }
            L54:
                com.yodobashi.iShop.YdMainActivity r2 = com.yodobashi.iShop.YdMainActivity.this
                boolean r2 = r2.menuOut
                if (r2 != 0) goto L4c
            L5a:
                r0 = r1
                goto L4c
            L5c:
                com.yodobashi.iShop.YdMainActivity r2 = com.yodobashi.iShop.YdMainActivity.this
                boolean r2 = r2.menuOut
                if (r2 == 0) goto L68
                com.yodobashi.iShop.YdMainActivity r1 = com.yodobashi.iShop.YdMainActivity.this
                com.yodobashi.iShop.YdMainActivity.access$900(r1)
                goto L4c
            L68:
                com.yodobashi.iShop.YdMainActivity r0 = com.yodobashi.iShop.YdMainActivity.this
                boolean r0 = r0.isInputMode
                if (r0 == 0) goto L5a
                goto L5a
            L6f:
                com.yodobashi.iShop.YdMainActivity r2 = com.yodobashi.iShop.YdMainActivity.this
                com.yodobashi.iShop.DataInfo r2 = r2.dataInfo
                r2.setIntentBackFlg(r1)
                goto L54
            */
            throw new UnsupportedOperationException("Method not decompiled: com.yodobashi.iShop.YdMainActivity.AnonymousClass9.onTouchEvent(android.view.MotionEvent):boolean");
        }
    };
    private final TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() { // from class: com.yodobashi.iShop.YdMainActivity.10
        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId != 3) {
                return false;
            }
            String editStr = YdMainActivity.this.edit.getText().toString().replaceAll("^[\u3000*| *]*", "").replaceAll("[\u3000*| *]*$", "");
            if (!"".equals(editStr)) {
                YdMainActivity.this.hideSoftInput();
                Message msg = new Message();
                msg.what = 6;
                msg.obj = YdMainActivity.this.edit.getText().toString();
                YdMainActivity.this.mHandler.sendMessageDelayed(msg, 100L);
            }
            return true;
        }
    };
    private final TextWatcher mTextWatcher = new TextWatcher() { // from class: com.yodobashi.iShop.YdMainActivity.11
        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LLog.i("onTextChanged:");
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            LLog.i("beforeTextChanged:");
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            LLog.i("afterTextChanged:");
            if (YdMainActivity.this.searchbarafter.getVisibility() != 8) {
                String editStr = YdMainActivity.this.edit.getText().toString().replaceAll("^[\u3000*| *]*", "");
                if (!"".equals(editStr)) {
                    try {
                        YdMainActivity.this.jsonIf.serverCommExec(YdMainActivity.this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[11]).replace(Constants.SEARCH_SUGGEST_REP, URLEncoder.encode(editStr, "UTF-8").replaceAll("\\+", "%20")), 4, new HandlerForCommResultEx());
                        return;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if (YdMainActivity.this.searchbarafter.getVisibility() == 0 && YdMainActivity.this.suggestBarcodeView.getVisibility() == 8) {
                    YdMainActivity.this.suggestBarcodeView.setVisibility(0);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(YdMainActivity.this, R.layout.suggest_item_list, (List<String>) Arrays.asList(new String[0]));
                    YdMainActivity.this.suggestList.setAdapter((ListAdapter) adapter);
                }
            }
        }
    };
    private final AdapterView.OnItemClickListener mSuggestOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.12
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapter, View selectedView, int pos, long arg3) {
            TextView textView = (TextView) selectedView;
            String selectStr = textView.getText().toString();
            LLog.i("listview onItemClick:");
            LLog.i("searchCode:" + selectStr);
            YdMainActivity.this.edit.setText(selectStr);
            YdMainActivity.this.hideSoftInput();
            Message msg = new Message();
            msg.what = 7;
            msg.obj = selectStr;
            YdMainActivity.this.mHandler.sendMessageDelayed(msg, 0L);
        }
    };
    private final FelicaEventListener mFelicaEventListener = new FelicaEventListener() { // from class: com.yodobashi.iShop.YdMainActivity.21
        @Override // com.felicanetworks.mfc.FelicaEventListener
        public void finished() {
            LLog.i("finished()");
        }

        @Override // com.felicanetworks.mfc.FelicaEventListener
        public void errorOccurred(int id, String msg, AppInfo otherAppInfo) {
            LLog.i("errorOccurred()");
        }
    };

    /* loaded from: classes.dex */
    class InputHandler extends Handler {
        InputHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (msg.arg1 == 1) {
                    }
                    break;
                case 2:
                    YdMainActivity.this.MenuListClickListener(msg.arg1);
                    break;
                case 3:
                    LLog.i("MSG_INTENTBACK dataInfo.getIntentBackFlg():" + YdMainActivity.this.dataInfo.getIntentBackFlg());
                    LLog.i("isInputMode:" + YdMainActivity.this.isInputMode);
                    YdMainActivity.this.dataInfo.setIntentBackFlg(false);
                    LLog.i("MSG_INTENTBACK dataInfo.getIntentBackFlg():" + YdMainActivity.this.dataInfo.getIntentBackFlg());
                    break;
                case 4:
                    if (YdMainActivity.this.searchbarafter.getVisibility() == 0) {
                        YdMainActivity.this.isSuggestMode = false;
                        YdMainActivity.this.suggestModeView(false);
                    }
                    try {
                        Intent scanIntent = new Intent(YdMainActivity.this, (Class<?>) CaptureActivity.class);
                        scanIntent.putExtra(CaptureActivity.START_EXTRA_ISFRONTCAMERA, YdMainActivity.this.isFrontCamera);
                        YdMainActivity.this.startActivityForResult(scanIntent, 1);
                        break;
                    } catch (Exception e) {
                        LLog.d(e.getLocalizedMessage() + " " + e.getCause());
                        YdMainActivity.this.setBarcodeRunFlg(false);
                        break;
                    }
                case 5:
                case 6:
                case 7:
                    LLog.i("msg.obj.toString():" + msg.obj.toString());
                    YdMainActivity.this.searchKey = msg.obj.toString();
                    if (YdMainActivity.this.searchbarafter.getVisibility() == 0) {
                        YdMainActivity.this.isSuggestMode = false;
                        YdMainActivity.this.suggestModeView(false);
                    }
                    try {
                        YdMainActivity.this.view.loadUrl(YdMainActivity.this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[10]).replace(Constants.SEARCH_KEYWORD_REP, URLEncoder.encode(msg.obj.toString(), "UTF-8").replaceAll("\\+", "%20")));
                        break;
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                        break;
                    }
                case 8:
                    LLog.i("Constants.MSG_LOCATION_INFO:");
                    if (YdMainActivity.this.location != null) {
                        YdMainActivity.this.location.gpsRemove();
                        YdMainActivity.this.location.setLatitudeAndLongitude();
                        break;
                    }
                    break;
                case 9:
                    if (YdMainActivity.this.searchbarafter.getVisibility() == 0) {
                        YdMainActivity.this.isSuggestMode = false;
                        YdMainActivity.this.suggestModeView(false);
                    }
                    try {
                        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
                        intent.putExtra("android.speech.extra.PROMPT", "お話しください");
                        YdMainActivity.this.startActivityForResult(intent, 2);
                        break;
                    } catch (ActivityNotFoundException e2) {
                        LLog.d(e2.getLocalizedMessage() + " " + e2.getCause());
                        YdMainActivity.this.showDialog(4);
                        break;
                    }
            }
            super.handleMessage(msg);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 10) {
            getWindow().setFlags(16777216, 16777216);
        }
        requestWindowFeature(1);
        LayoutInflater inflater = LayoutInflater.from(this);
        this.imageMenu = inflater.inflate(R.layout.horz_scroll_menu, (ViewGroup) null);
        setContentView(this.imageMenu);
        this.scrollView = (YdHorizontalScrollView) this.imageMenu.findViewById(R.id.myScrollView);
        this.top = this.scrollView.findViewById(R.id.top);
        this.scrollView.setOnKeyboardStateChangedListener(this.mIOnKeyboardStateChangedListener);
        this.app = inflater.inflate(R.layout.horz_scroll_app, (ViewGroup) null);
        this.searchbarafter = (ViewGroup) this.app.findViewById(R.id.searchbarafter);
        this.searchbarafter.setVisibility(8);
        this.view = (YdWebView) this.app.findViewById(R.id.webView1);
        this.view.setOnTouchListener(new FlickTouchListener());
        this.view.setOnTouchEventListener(this.mOnTouchEventListener);
        this.view.setWebViewClient(new YdWebViewClient(this));
        this.view.setWebChromeClient(new YdWebChromeClient(this));
        WebView.PictureListener picture = new WebView.PictureListener() { // from class: com.yodobashi.iShop.YdMainActivity.1
            @Override // android.webkit.WebView.PictureListener
            public void onNewPicture(WebView view, Picture picture2) {
                if (!YdMainActivity.this.isHardwareAccelerated(view)) {
                    YdMainActivity.this.ConnectPageFinish("");
                    YdMainActivity.this.DialogClose();
                }
            }
        };
        this.view.setPictureListener(picture);
        YdWebView ydWebView = this.view;
        YdWebViewClient ydWebViewClient = new YdWebViewClient(this);
        ydWebViewClient.getClass();
        ydWebView.addJavascriptInterface(new YdWebViewClient.InJavaScriptLocalObj(), "local_obj");
        this.view.setScrollBarStyle(0);
        this.offLineLayout = YdCommErrView.addViewWhenConnectError(this.view, this, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
        this.listView = this.imageMenu.findViewById(R.id.list);
        this.dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.dm);
        LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(-1, -1);
        ll_params.width = ((this.dm.widthPixels * 3) / 4) + 2;
        int index = 52;
        for (int i = 0; i < Constants.allMenulistIds.length; i++) {
            this.menu_item = (LinearLayout) this.listView.findViewById(Constants.allMenulistIds[i]);
            this.menu_item.setLayoutParams(ll_params);
            this.menu_item.setOnClickListener(this.mClickListener);
            this.menu_item.setTag(Integer.valueOf(index));
            index++;
        }
        this.suggestBarcodeView = (ViewGroup) this.app.findViewById(R.id.suggestBarcodeView);
        this.suggestBarcodeView.setVisibility(8);
        this.suggestBarcodeItem = (ViewGroup) this.suggestBarcodeView.findViewById(R.id.suggestBarcodeItem);
        this.suggestBarcodeItem.setOnClickListener(this.mClickListener);
        this.suggestBarcodeItem.setTag(2);
        this.suggestAudioItem = (ViewGroup) this.suggestBarcodeView.findViewById(R.id.suggestAudioItem);
        this.suggestAudioItem.setOnClickListener(this.mClickListener);
        this.suggestAudioItem.setTag(9);
        this.suggestList = (ListView) this.app.findViewById(R.id.suggestlist);
        this.suggestList.setVisibility(8);
        this.suggestList.setOnItemClickListener(this.mSuggestOnItemClickListener);
        this.btnSearch = (ImageView) this.searchbarafter.findViewById(R.id.btnSearch);
        this.btnSearch.setOnClickListener(this.mClickListener);
        this.btnSearch.setTag(1);
        this.edit = (YdEditText) this.searchbarafter.findViewById(R.id.editsearchbarafter);
        this.edit.setOnEditorActionListener(this.mEditorActionListener);
        this.edit.addTextChangedListener(this.mTextWatcher);
        this.searchBarEx = (ViewGroup) this.app.findViewById(R.id.info);
        this.btnSlide = (ImageView) this.searchBarEx.findViewById(R.id.BtnSlide);
        this.btnSlide.setOnClickListener(this.mClickListener);
        this.btnSlide.setTag(5);
        this.loginClick = (ImageView) this.searchBarEx.findViewById(R.id.loginImage);
        this.loginClick.setOnClickListener(this.mClickListener);
        this.loginClick.setTag(6);
        this.loginId = (TextView) this.searchBarEx.findViewById(R.id.loginId);
        this.pointClick = (ImageView) this.searchBarEx.findViewById(R.id.pointImage);
        this.pointClick.setOnClickListener(this.mClickListener);
        this.pointClick.setTag(7);
        this.pointId = (TextView) this.searchBarEx.findViewById(R.id.pointId);
        this.cartClick = (ImageView) this.searchBarEx.findViewById(R.id.cartImage);
        this.cartClick.setOnClickListener(this.mClickListener);
        this.cartClick.setTag(8);
        this.cartId = (TextView) this.searchBarEx.findViewById(R.id.cartId);
        this.cartCountbg = (ImageView) this.searchBarEx.findViewById(R.id.cartCountbg);
        this.searchBar = (ViewGroup) this.app.findViewById(R.id.searchBar);
        this.editBefore = (AutoCompleteTextView) this.searchBar.findViewById(R.id.editsearchbar);
        this.editBefore.setOnTouchListener(new EditOnTouchListener());
        this.btnAudio = (ImageView) this.searchBar.findViewById(R.id.BtnAudio);
        this.btnAudio.setOnClickListener(this.mClickListener);
        this.btnAudio.setTag(3);
        this.btnBarcode = (ImageView) this.searchBar.findViewById(R.id.btnBarcode);
        this.btnBarcode.setOnClickListener(this.mClickListener);
        this.btnBarcode.setTag(4);
        YdResizeLayout layout = (YdResizeLayout) this.top;
        layout.setOnResizeListener(this.mOnResizeListener, 1);
        YdResizeLayout layoutApp = (YdResizeLayout) this.app;
        layoutApp.setOnResizeListener(this.mOnResizeListener, 2);
        View transparent = new TextView(this);
        View[] children = {transparent, this.app};
        this.scrollView.initViews(children, 1, new SizeCallbackForMenu(this.btnSlide, getWindowManager().getDefaultDisplay().getWidth()));
        this.waitDialog = null;
        init();
        webViewSetting();
        this.location = new YdGpsInfoScan(this);
        this.isOncreate = true;
    }

    private void webViewSetting() {
        LLog.i("webViewSetting()");
        LLog.i("userAgent:" + this.dataInfo.getUserAgent());
        this.view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        this.view.getSettings().setCacheMode(2);
        this.view.getSettings().setJavaScriptEnabled(true);
        this.view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        WebViewDatabase.getInstance(this).clearHttpAuthUsernamePassword();
        this.view.getSettings().setUserAgentString(this.dataInfo.getUserAgent());
        this.view.getSettings().setSupportZoom(true);
        this.view.getSettings().setBuiltInZoomControls(true);
        ZoomControlInvisible(this.view);
        this.view.getSettings().setUseWideViewPort(true);
        this.view.getSettings().setLoadWithOverviewMode(true);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.isChecking) {
            this.isChecking = false;
            if (!masterDateUpdateCheck()) {
                MenuListClickListener(52);
            }
        } else if (this.isOncreate) {
            this.isOncreate = false;
            startCheck();
        }
        Runnable intentFelica = new Runnable() { // from class: com.yodobashi.iShop.YdMainActivity.2
            @Override // java.lang.Runnable
            public void run() {
                Context ctx = YdMainActivity.this.getApplicationContext();
                Intent intent = new Intent(ctx, (Class<?>) Felica.class);
                YdMainActivity.this.mFelicaConnection = new FelicaConnection();
                if (!ctx.bindService(intent, YdMainActivity.this.mFelicaConnection, 1)) {
                    LLog.i("FelicaConnection cannot bind");
                }
            }
        };
        new Thread(intentFelica).start();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.view != null) {
            this.view.clearCache(true);
            this.view.setWebChromeClient(null);
            this.view.setWebViewClient(null);
            ((YdResizeLayout) this.app).removeView(this.view);
            this.view.removeAllViews();
            this.view.destroy();
            this.view = null;
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        getApplicationContext().unbindService(this.mFelicaConnection);
        CacheClear();
        if (!getBarcodeRunFlg()) {
            this.dataInfo.setScanLogInfo("", "", "", "");
        }
    }

    public void CacheClear() {
        Runnable clearCache = new Runnable() { // from class: com.yodobashi.iShop.YdMainActivity.3
            @Override // java.lang.Runnable
            public void run() {
                File file23 = new File(YdMainActivity.this.getCacheDir().toString() + "/webviewCache/");
                File file30_1 = new File(YdMainActivity.this.getCacheDir().toString() + "/webviewCacheChromium/");
                File file30_2 = new File(YdMainActivity.this.getCacheDir().toString() + "/webviewCacheChromiumStaging/");
                deleteFile(file23);
                deleteFile(file30_1);
                deleteFile(file30_2);
            }

            private void deleteFile(File file) {
                String[] files;
                if (file != null && file.exists() && (files = file.list()) != null) {
                    for (String str : files) {
                        File f = new File(file, str);
                        if (!f.delete()) {
                            LLog.e(f.getPath() + " delete failed.");
                        }
                    }
                }
            }
        };
        new Thread(clearCache).start();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LLog.i("onConfigurationChanged()");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x0008. Please report as an issue. */
    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            switch (keyCode) {
                case 4:
                    if (this.menuOut) {
                        SlideMenuOnOff();
                    }
                    if (this.searchbarafter.getVisibility() == 0) {
                        this.isSuggestMode = false;
                        suggestModeView(false);
                        return true;
                    }
                    if (this.view.canGoBack()) {
                        this.view.goBack();
                        return true;
                    }
                    break;
                case 84:
                    if (this.isSuggestMode) {
                        return true;
                    }
                    this.isSuggestMode = true;
                    suggestModeView(true);
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void barcodeActivityStart() {
        if (!getBarcodeRunFlg()) {
            if (!this.dataInfo.getCameraCanUseFlg()) {
                cameraUnsupportDialog();
                return;
            }
            setBarcodeRunFlg(true);
            try {
                Intent scanIntent = new Intent(this, (Class<?>) CaptureActivity.class);
                scanIntent.putExtra(CaptureActivity.START_EXTRA_ISFRONTCAMERA, this.isFrontCamera);
                startActivityForResult(scanIntent, 1);
            } catch (Exception e) {
                LLog.d(e.getLocalizedMessage() + " " + e.getCause());
                setBarcodeRunFlg(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftInput() {
        this.isInputMode = false;
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        imm.hideSoftInputFromWindow(this.edit.getWindowToken(), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void MenuListClickListener(int index) {
        this.view.requestFocus();
        switch (index) {
            case 1:
            case Constants.MENU_LIST_1 /* 52 */:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[0]));
                return;
            case 2:
            case 53:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[1]));
                return;
            case 3:
            case 54:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[2]));
                return;
            case 4:
            case 55:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[3]));
                return;
            case 5:
            case 56:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[4]));
                return;
            case 6:
            case Constants.MENU_LIST_6 /* 57 */:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[5]));
                return;
            case 7:
            case 58:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[6]));
                return;
            case 8:
            case 59:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[7]));
                return;
            case 9:
            case 60:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[8]));
                return;
            case 10:
            case 61:
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[9]));
                return;
            case 11:
            case 62:
                if (isFelicaSupport()) {
                    if (isPackageExists(Constants.INTENT_PKGNAME_BROWSER)) {
                        Uri content_url = Uri.parse(Constants.INTENT_GPC_URL);
                        Intent intentBrowser = new Intent("android.intent.action.VIEW", content_url);
                        intentBrowser.setClassName(Constants.INTENT_PKGNAME_BROWSER, "com.android.browser.BrowserActivity");
                        startActivity(intentBrowser);
                        return;
                    }
                    showDialog(3);
                    return;
                }
                showDialog(2);
                return;
            case 12:
            case Constants.MENU_LIST_12 /* 63 */:
                AboutShow();
                return;
            case 51:
            default:
                return;
        }
    }

    private void AboutShow() {
        String message = "";
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String appVersion = info.versionName;
            message = getString(R.string.about_version_name) + appVersion + "\n\n" + getString(R.string.about_copyInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.about_title)).setMessage(message).setPositiveButton(getString(R.string.about_ok), new DialogInterface.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public boolean isCameraCanUse() {
        boolean canUse = true;
        try {
            openCamera();
        } catch (IOException e) {
            canUse = false;
        } catch (RuntimeException e2) {
            canUse = false;
        }
        LLog.i("isCameraCanUse canUse:" + canUse);
        return canUse;
    }

    public synchronized void openCamera() throws IOException {
        OpenCameraInterface cameraInterface = new OpenCameraManager().build();
        Camera mCamera = cameraInterface.open();
        if (mCamera == null) {
            throw new IOException();
        }
        if (mCamera != null) {
            this.isFrontCamera = cameraInterface.isFrontCamera();
            LLog.i("isFrontCamera:" + this.isFrontCamera);
            mCamera.release();
        }
    }

    public void setBarcodeRunFlg(boolean isRun) {
        this.isBarcodeRunning = isRun;
    }

    public boolean getBarcodeRunFlg() {
        return this.isBarcodeRunning;
    }

    public void cameraUnsupportDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.camera_none_dialog_message).setPositiveButton(getString(R.string.camera_none_dialog_ok), new DialogInterface.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /* loaded from: classes.dex */
    private class FlickTouchListener implements View.OnTouchListener {
        private FlickTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    private class EditOnTouchListener implements View.OnTouchListener {
        private EditOnTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            LLog.i("edit onTouch event.getAction():" + event.getAction());
            switch (event.getAction()) {
                case 1:
                    if (!YdMainActivity.this.menuOut) {
                        YdMainActivity.this.isSuggestMode = true;
                        YdMainActivity.this.suggestModeView(true);
                    }
                case 0:
                default:
                    return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void suggestModeView(boolean isSugMode) {
        LLog.i("suggestModeView s");
        LLog.i("suggestModeView isSugMode:" + isSugMode);
        if (isSugMode) {
            this.searchbarafter.setVisibility(0);
            this.suggestBarcodeView.setVisibility(0);
            this.suggestList.setVisibility(0);
            this.searchBar.setVisibility(8);
            this.searchBarEx.setVisibility(8);
            this.view.setVisibility(8);
            this.edit.setFocusable(true);
            this.edit.setFocusableInTouchMode(true);
            this.edit.requestFocus();
            this.edit.selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
            imm.showSoftInput(this.edit, 1);
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.suggest_item_list, (List<String>) Arrays.asList(new String[0]));
        this.suggestList.setAdapter((ListAdapter) adapter);
        this.suggestList.setVisibility(8);
        this.searchbarafter.setVisibility(8);
        this.suggestBarcodeView.setVisibility(8);
        this.edit.setText(this.searchKey);
        this.editBefore.setText(this.searchKey);
        this.searchBar.setVisibility(0);
        this.searchBarEx.setVisibility(0);
        this.view.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SlideMenuOnOff() {
        int menuWidth = this.listView.getMeasuredWidth();
        this.listView.setVisibility(0);
        if (!this.menuOut) {
            if (this.edit != null) {
                this.edit.setFocusableInTouchMode(false);
                this.edit.clearFocus();
            }
            this.scrollView.smoothScrollTo(0, 0);
        } else {
            if (this.edit != null) {
                this.edit.setFocusableInTouchMode(true);
            }
            this.scrollView.smoothScrollTo(menuWidth, 0);
        }
        this.menuOut = this.menuOut ? false : true;
    }

    private void init() {
        this.dataInfo.setAppId(Constants.ANDROID_APPID);
        try {
            this.dataInfo.setAppVersion(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e1) {
            LLog.e("NameNotFoundException:" + e1);
            e1.printStackTrace();
        }
        this.dataInfo.setUserAgent("YodobashiAndroid " + this.dataInfo.getAppVersion() + " (" + DataInfo.deviceVersion + "; Android " + DataInfo.osVersion + "; ja_JP@calendar=japanese)");
        dataInfoMSDUpdate();
        this.dataInfo.setCameraCanUseFlg(isCameraCanUse());
    }

    private void startCheck() {
        LLog.i("startCheck s");
        if (!this.fileManager.isMVCFileIsExists(getFilesDir() + "/" + Constants.MARSTER_VER_CONFIG_FILENAME)) {
            this.fileManager.UpdateMVCFile("0", 2);
        }
        this.dataInfo.setMvcData(this.fileManager.getMVCData());
        showDialog(1);
        this.jsonIf.serverCommExec(JsonInterface.JSON_SERVER_URL_NOTIFICATION, 1, new HandlerForCommResultEx());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean startCheckEx(String getServerNotification) {
        LLog.i("startCheckEx s");
        boolean isPop = false;
        LLog.i("getServerNotification:" + getServerNotification);
        if (getServerNotification != null && !"".equals(getServerNotification)) {
            boolean ret = this.jsonIf.parseJsonForServerNotification(getServerNotification, 1);
            if (ret) {
                if ("2".equals(this.dataInfo.getAppVStatus())) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(getString(R.string.version_check_dialog_title)).setMessage(this.dataInfo.getAppVSDes()).setPositiveButton(getString(R.string.version_check_dialog_yes), new DialogInterface.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.15
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            YdMainActivity.this.isChecking = true;
                            if (YdMainActivity.this.dataInfo.getAppVSUrl() != null && !"".equals(YdMainActivity.this.dataInfo.getAppVSUrl())) {
                                if (YdMainActivity.this.isInGoogleAppStore()) {
                                    YdMainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(YdMainActivity.this.dataInfo.getAppVSUrl())));
                                } else {
                                    YdMainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(YdMainActivity.this.dataInfo.getAppVSUrl().replace(Constants.MARKET_STR, Constants.MARKET_AMAZON_STR))));
                                }
                            }
                        }
                    }).setNegativeButton(getString(R.string.version_check_dialog_cancel), new DialogInterface.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.14
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!YdMainActivity.this.masterDateUpdateCheck()) {
                                YdMainActivity.this.MenuListClickListener(52);
                            }
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.yodobashi.iShop.YdMainActivity.13
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == 4) {
                                dialog.dismiss();
                                YdMainActivity.this.finish();
                                return false;
                            }
                            return false;
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    isPop = true;
                } else if ("1".equals(this.dataInfo.getAppVStatus())) {
                    AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                    alert2.setTitle(getString(R.string.version_check_dialog_title)).setMessage(this.dataInfo.getAppVSDes()).setPositiveButton(getString(R.string.version_check_dialog_confim), new DialogInterface.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.17
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog2, int which) {
                            YdMainActivity.this.isChecking = true;
                            if (YdMainActivity.this.dataInfo.getAppVSUrl() != null && !"".equals(YdMainActivity.this.dataInfo.getAppVSUrl())) {
                                if (YdMainActivity.this.isInGoogleAppStore()) {
                                    YdMainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(YdMainActivity.this.dataInfo.getAppVSUrl())));
                                } else {
                                    YdMainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(YdMainActivity.this.dataInfo.getAppVSUrl().replace(Constants.MARKET_STR, Constants.MARKET_AMAZON_STR))));
                                }
                            }
                        }
                    }).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.yodobashi.iShop.YdMainActivity.16
                        @Override // android.content.DialogInterface.OnKeyListener
                        public boolean onKey(DialogInterface dialog2, int keyCode, KeyEvent event) {
                            if (keyCode == 4) {
                                dialog2.dismiss();
                                YdMainActivity.this.finish();
                                return false;
                            }
                            return false;
                        }
                    });
                    AlertDialog dialog2 = alert2.create();
                    dialog2.setCanceledOnTouchOutside(false);
                    dialog2.show();
                    isPop = true;
                }
            }
        }
        if (!isPop && !masterDateUpdateCheck()) {
            MenuListClickListener(52);
            return true;
        }
        return isPop;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean masterDateUpdateCheck() {
        boolean ret = false;
        if (!"2".equals(this.dataInfo.getMSDStatus()) && "1".equals(this.dataInfo.getMSDStatus())) {
            this.fileManager.UpdateMVCFile(this.dataInfo.getMSDVer(), 2);
            this.dataInfo.setMvcData(this.fileManager.getMVCData());
            showDialog(1);
            this.jsonIf.serverCommExec(JsonInterface.JSON_SERVER_URL_MASTERDATE, 2, new HandlerForCommResultEx());
            ret = true;
        }
        LLog.i("masterDateUpdateCheck ret:" + ret);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dataInfoMSDUpdate() {
        if (!this.fileManager.isMVCFileIsExists(getFilesDir() + "/" + Constants.ANDROID_WEB_URL_FILENAME)) {
            try {
                InputStream is = getResources().getAssets().open(Constants.ANDROID_WEB_URL_FILENAME);
                HashMap textfile = this.fileManager.convertStreamToMap(is);
                this.dataInfo.setMSDMap(textfile);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            FileInputStream fin = openFileInput(Constants.ANDROID_WEB_URL_FILENAME);
            HashMap textfile2 = this.fileManager.convertStreamToMap(fin);
            this.dataInfo.setMSDMap(textfile2);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            this.dataInfo.setIntentBackFlg(true);
            setBarcodeRunFlg(false);
        }
        if (requestCode == 1 && resultCode == -1) {
            String barcodeString = data.getExtras().getString(CaptureActivity.RESULT_EXTRA);
            if (barcodeString != null) {
                this.dataInfo.setFoundFlg("0");
                searchKeywordClear();
                this.dataInfo.setBarcodeFlg(true);
                this.dataInfo.setIsComplete(false);
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[12]).replace(Constants.SEARCH_BARCODE_REP, barcodeString));
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService("wifi");
                WifiInfo info = wifi.getConnectionInfo();
                String macAddress = info.getMacAddress();
                if ("".equals(this.dataInfo.getJanCode())) {
                    this.dataInfo.setScanLogInfo(barcodeString, "", "", macAddress);
                    Message msg = new Message();
                    msg.what = 8;
                    this.mHandler.sendMessage(msg);
                    this.isNotLocationExec = false;
                    return;
                }
                this.dataInfo.setScanLogInfo(barcodeString, this.dataInfo.getGpsLat(), this.dataInfo.getGpsLon(), macAddress);
                this.isNotLocationExec = true;
                return;
            }
            return;
        }
        if (requestCode == 2 && resultCode == -1) {
            ArrayList<String> matches = data.getStringArrayListExtra("android.speech.extra.RESULTS");
            String resultsString = matches.get(0);
            if (!"".equals(resultsString)) {
                this.searchKey = resultsString;
                this.edit.setText(this.searchKey);
                this.editBefore.setText(this.searchKey);
                this.view.loadUrl(this.dataInfo.getMSDUrl(Constants.ARR_WEBURL_KEY[10]).replace(Constants.SEARCH_KEYWORD_REP, resultsString));
            }
        }
    }

    public void BarcodeScanComplete(int type) {
        LLog.i("BarcodeScanComplete");
        LLog.i("location.getIsChange()：" + this.location.getIsChange());
        switch (type) {
            case 1:
                if (this.location.getIsChange() || this.isNotLocationExec) {
                    this.location.gpsRemove();
                    WriteCommodityScanLog();
                    return;
                }
                return;
            case 2:
                if (this.dataInfo.getIsComplete()) {
                    this.location.gpsRemove();
                    WriteCommodityScanLog();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void WriteCommodityScanLog() {
        LLog.i("WriteCommodityScanLog s");
        if ("".equals(this.dataInfo.getGpsLat())) {
            this.dataInfo.setScanLogInfo(this.dataInfo.getJanCode(), this.location.getLatitude(), this.location.getLongitude(), this.dataInfo.getDeviceId());
        }
        this.jsonIf.serverCommExec(JsonInterface.JSON_SERVER_URL_WRSCANLOG, 3, new HandlerForCommResultEx());
    }

    private void searchKeywordClear() {
        if (!"".equals(this.searchKey)) {
            this.searchKey = "";
            this.edit.setText(this.searchKey);
            this.editBefore.setText(this.searchKey);
        }
    }

    /* loaded from: classes.dex */
    static class SizeCallbackForMenu implements YdHorizontalScrollView.SizeCallback {
        View btnSlide;
        int btnWidth;
        int screenWidth;

        public SizeCallbackForMenu(View btnSlide, int screenWidth) {
            this.btnSlide = btnSlide;
            this.screenWidth = screenWidth;
        }

        @Override // com.yodobashi.iShop.YdHorizontalScrollView.SizeCallback
        public void onGlobalLayout() {
            this.btnWidth = this.btnSlide.getMeasuredWidth();
            if (this.btnWidth <= this.screenWidth / 4) {
                this.btnWidth = this.screenWidth / 4;
            }
            LLog.i("btnWidth=" + this.btnWidth);
        }

        @Override // com.yodobashi.iShop.YdHorizontalScrollView.SizeCallback
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            if (idx == 0) {
                dims[0] = w - this.btnWidth;
            }
        }
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (this.menuOut || this.isInputMode) {
            return false;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu more = menu.addSubMenu(0, Constants.allOptionsMenuIds[5], Constants.allOptionsMenuOrders[5], Constants.allOptionsMenuTexts[5]).setIcon(Constants.allOptionsMenuIcons[5]);
        for (int i = 0; i < Constants.allOptionsMenuTexts.length; i++) {
            if (i <= 4) {
                menu.add(0, Constants.allOptionsMenuIds[i], Constants.allOptionsMenuOrders[i], getString(Constants.allOptionsMenuTexts[i])).setIcon(Constants.allOptionsMenuIcons[i]);
            } else if (i >= 6) {
                more.add(0, Constants.allOptionsMenuIds[i], Constants.allOptionsMenuOrders[i], getString(Constants.allOptionsMenuTexts[i]));
            }
        }
        setMenuBackground();
        return super.onCreateOptionsMenu(menu);
    }

    protected void setMenuBackground() {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() { // from class: com.yodobashi.iShop.YdMainActivity.18
            @Override // android.view.LayoutInflater.Factory
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
                    try {
                        LayoutInflater f = YdMainActivity.this.getLayoutInflater();
                        final View view = f.createView(name, null, attrs);
                        new Handler().post(new Runnable() { // from class: com.yodobashi.iShop.YdMainActivity.18.1
                            @Override // java.lang.Runnable
                            public void run() {
                                view.setBackgroundColor(YdMainActivity.this.getResources().getColor(R.color.contents_text));
                            }
                        });
                        return view;
                    } catch (InflateException e) {
                    } catch (ClassNotFoundException e2) {
                    }
                }
                return null;
            }
        });
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuListClickListener(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        int resId = -1;
        switch (id) {
            case 0:
                this.waitDialog = new ProgressDialog(this);
                this.waitDialog.setMessage(getString(R.string.webview_reading));
                this.waitDialog.setCancelable(true);
                this.waitDialog.setCanceledOnTouchOutside(false);
                this.waitDialog.setProgressStyle(0);
                this.waitDialog.show();
                return this.waitDialog;
            case 1:
                ProgressDialog waitDialog = new ProgressDialog(this);
                waitDialog.setMessage(getString(R.string.getservernotification_exec));
                waitDialog.setCancelable(true);
                waitDialog.setCanceledOnTouchOutside(false);
                waitDialog.setProgressStyle(0);
                waitDialog.show();
                return waitDialog;
            case 2:
                resId = R.string.felica_unsupport_message;
                break;
            case 3:
                resId = R.string.pkg_not_exists;
                break;
            case 4:
                resId = R.string.kindle_audio_unsupport_message;
                break;
        }
        if (resId != -1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(resId).setPositiveButton(getString(R.string.camera_none_dialog_ok), new DialogInterface.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.19
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = alert.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return dialog;
        }
        return null;
    }

    public void DialogShow(WebView view) {
        LLog.i("DialogShow:");
        if (this.waitDialog == null) {
            if (!isFinishing()) {
                showDialog(0);
            }
            this.failingUrl = null;
        }
    }

    public void DialogClose() {
        LLog.i("DialogClose:");
        if (this.waitDialog != null) {
            removeDialog(0);
            this.waitDialog = null;
        }
    }

    public void ConnectError(String failingUrl) {
        LLog.i("ConnectError offLineLayout:" + this.offLineLayout);
        if (this.offLineLayout != null) {
            this.isOffLine = true;
            this.offLineLayout.setVisibility(0);
        }
        this.failingUrl = failingUrl;
    }

    public void ConnectPageFinish(String url) {
        LLog.i("ConnectPageFinish failingUrl:" + this.failingUrl);
        LLog.i("offLineLayout:" + this.offLineLayout);
        if (this.isOffLine && this.failingUrl == null && this.offLineLayout != null) {
            this.isOffLine = false;
            this.offLineLayout.setVisibility(4);
        }
    }

    public void setOnClickListenerForButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() { // from class: com.yodobashi.iShop.YdMainActivity.20
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                System.out.println("click button");
                if (YdMainActivity.this.failingUrl != null && !YdMainActivity.this.failingUrl.equals("")) {
                    YdMainActivity.this.failingUrl = null;
                    YdMainActivity.this.view.reload();
                }
            }
        });
    }

    public void ZoomControlInvisible(View view) {
        try {
            Field field = WebView.class.getDeclaredField("mZoomButtonsController");
            field.setAccessible(true);
            ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
            mZoomButtonsController.getZoomControls().setVisibility(8);
            try {
                field.set(view, mZoomButtonsController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            }
        } catch (NoSuchFieldException e3) {
            e3.printStackTrace();
        } catch (SecurityException e4) {
            e4.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class HandlerForCommResultEx implements JsonCtrlHandler {
        public HandlerForCommResultEx() {
        }

        @Override // com.yodobashi.iShop.json.JsonCtrlHandler
        public void onCommEnd(String strResult, int jsonType) {
            String[] userData;
            LLog.d("onCommEnd() strResult:" + strResult);
            LLog.d("onCommEnd() jsonType:" + jsonType);
            switch (jsonType) {
                case 1:
                    if (YdMainActivity.this.startCheckEx(strResult)) {
                        YdMainActivity.this.removeDialog(1);
                        return;
                    }
                    return;
                case 2:
                    if (strResult != null && !"".equals(strResult)) {
                        YdMainActivity.this.fileManager.UpdateMVCFile(strResult, 1);
                        YdMainActivity.this.dataInfoMSDUpdate();
                    }
                    YdMainActivity.this.removeDialog(1);
                    YdMainActivity.this.MenuListClickListener(52);
                    return;
                case 3:
                    boolean ret = YdMainActivity.this.jsonIf.parseJsonForServerNotification(strResult, 3);
                    if (!ret) {
                        LLog.i("WriteCommodityScanLog error");
                        return;
                    }
                    return;
                case 4:
                    String editStr = YdMainActivity.this.edit.getText().toString().replaceAll("^[\u3000*| *]*", "");
                    if (!"".equals(strResult) && YdMainActivity.this.searchbarafter.getVisibility() == 0 && !"".equals(editStr)) {
                        YdMainActivity.this.AutoCompleteWords = YdMainActivity.this.jsonIf.parseJsonForsuggest(strResult, 4);
                        if (YdMainActivity.this.AutoCompleteWords != null && YdMainActivity.this.AutoCompleteWords.length > 0) {
                            YdMainActivity.this.suggestBarcodeView.setVisibility(8);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(YdMainActivity.this, R.layout.suggest_item_list, (List<String>) Arrays.asList(YdMainActivity.this.AutoCompleteWords));
                            YdMainActivity.this.suggestList.setAdapter((ListAdapter) adapter);
                            return;
                        }
                    }
                    if (YdMainActivity.this.searchbarafter.getVisibility() == 0 && YdMainActivity.this.suggestBarcodeView.getVisibility() == 8) {
                        YdMainActivity.this.suggestBarcodeView.setVisibility(0);
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(YdMainActivity.this, R.layout.suggest_item_list, (List<String>) Arrays.asList(new String[0]));
                        YdMainActivity.this.suggestList.setAdapter((ListAdapter) adapter2);
                        return;
                    }
                    return;
                case 5:
                default:
                    return;
                case 6:
                    if (!"".equals(strResult) && (userData = YdMainActivity.this.jsonIf.parseJsonForUserDataServlet(strResult, 6)) != null) {
                        for (int i = 0; i < userData.length; i++) {
                            LLog.i("userData[" + i + "]" + userData[i]);
                        }
                        if ("true".equals(userData[4])) {
                            YdMainActivity.this.loginId.setText(YdMainActivity.this.getApplicationContext().getString(R.string.user_data_guest_name));
                            YdMainActivity.this.pointId.setText(YdMainActivity.this.getApplicationContext().getString(R.string.user_data_point_ext));
                        } else {
                            DecimalFormat formatter = new DecimalFormat("#,###");
                            String pointChange = "0";
                            try {
                                pointChange = formatter.format(Long.parseLong(userData[1]));
                            } catch (Exception e) {
                                LLog.i("parseLong err");
                            }
                            YdMainActivity.this.loginId.setText(userData[0] + YdMainActivity.this.getApplicationContext().getString(R.string.user_data_name_ext));
                            YdMainActivity.this.pointId.setText(pointChange + YdMainActivity.this.getApplicationContext().getString(R.string.user_data_point_ext));
                        }
                        YdMainActivity.this.cartId.setText(userData[2]);
                        if (userData[2].length() > 1) {
                            YdMainActivity.this.cartCountbg.setBackgroundResource(R.drawable.red_back03);
                            return;
                        } else {
                            YdMainActivity.this.cartCountbg.setBackgroundResource(R.drawable.red_back);
                            return;
                        }
                    }
                    return;
            }
        }

        @Override // com.yodobashi.iShop.json.JsonCtrlHandler
        public void onCommStart() {
            LLog.d("onCommStart()");
        }
    }

    public void userDataServletCheck(String url) {
        boolean isExec = false;
        if (url.equals(Constants.USERDATA_TOP) || url.equals(Constants.USERDATA_CART_AFTER) || url.equals(Constants.USERDATA_PRODUCT_DELETE) || url.equals(Constants.USERDATA_CART_WIN_MOVE) || url.equals(Constants.USERDATA_LOGIN) || url.equals(Constants.USERDATA_CARD_FROM_LOGIN) || url.equals(Constants.USERDATA_LOGOUT) || url.equals(Constants.USERDATA_ORDER_END)) {
            isExec = true;
        }
        if (isExec) {
            String cookie = CookieManager.getInstance().getCookie(url);
            this.dataInfo.setCookieValue(cookie);
            this.jsonIf.serverCommExec(JsonInterface.JSON_SERVER_URL_USERDATASERVLET, 6, new HandlerForCommResultEx());
        }
    }

    /* loaded from: classes.dex */
    public class FelicaConnection implements ServiceConnection {
        private Felica mFelica;

        public FelicaConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mFelica = ((Felica.LocalBinder) service).getInstance();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            try {
                this.mFelica.inactivateFelica();
            } catch (Exception e) {
            }
        }

        public boolean isSupported() {
            boolean z = false;
            try {
                if (this.mFelica != null) {
                    this.mFelica.activateFelica(null, YdMainActivity.this.mFelicaEventListener);
                    z = true;
                    try {
                        if (this.mFelica != null) {
                            this.mFelica.inactivateFelica();
                        }
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        if (this.mFelica != null) {
                            this.mFelica.inactivateFelica();
                        }
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                try {
                    if (this.mFelica != null) {
                        this.mFelica.inactivateFelica();
                    }
                } catch (Exception e4) {
                }
            } catch (Throwable th) {
                try {
                    if (this.mFelica != null) {
                        this.mFelica.inactivateFelica();
                    }
                } catch (Exception e5) {
                }
                throw th;
            }
            return z;
        }
    }

    private boolean isFelicaSupport() {
        if (this.mFelicaConnection != null) {
            return this.mFelicaConnection.isSupported();
        }
        return false;
    }

    public boolean isPackageExists(String pkgName) {
        try {
            PackageManager pm = getPackageManager();
            pm.getPackageInfo(pkgName, 1);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isHardwareAccelerated(WebView view) {
        boolean ret = false;
        try {
            Method isHardwareAcc = view.getClass().getMethod("isHardwareAccelerated", new Class[0]);
            if (isHardwareAcc != null) {
                ret = ((Boolean) isHardwareAcc.invoke(view, (Object[]) null)).booleanValue();
            }
        } catch (Exception e) {
            LLog.i("isHardwareAccelerated is null");
        }
        LLog.i("isHardwareAccelerated ret:" + ret);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInGoogleAppStore() {
        return getResources().getBoolean(R.bool.isGoogleMarket);
    }
}
