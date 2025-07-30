package com.rediff.mail.and;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.google.android.gms.drive.DriveFile;
import java.io.File;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class RediffmailPlugin extends CordovaPlugin {
    private static Bundle c = null;
    private static Bundle d = null;
    private static Bundle e = null;
    private static CallbackContext f = null;
    private static CordovaWebView g;

    /* renamed from: a, reason: collision with root package name */
    ConnectivityManager f215a;
    boolean b = false;
    private Context h;

    @Override // org.apache.cordova.CordovaPlugin
    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        super.initialize(cordovaInterface, cordovaWebView);
        this.h = this.cordova.getActivity().getApplicationContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        cordovaInterface.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = (int) displayMetrics.density;
        View rootView = cordovaInterface.getActivity().getWindow().getDecorView().findViewById(R.id.content).getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new p(this, i, rootView, cordovaWebView));
        Log.v("RediffmailPlugin", "Init RediffmailPlugin");
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) {
        PluginResult pluginResult;
        PluginResult pluginResult2;
        PluginResult pluginResult3;
        String str2;
        Cursor query;
        View view;
        View view2;
        Log.v("RediffmailPlugin", "RediffmailPlugin action received:" + str);
        Log.v("RediffmailPlugin", "args.toString:" + jSONArray.toString());
        if (str.equals("showMessage")) {
            String str3 = "";
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    str3 = jSONObject.getString("message");
                    jSONObject.getString("duration");
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    return false;
                } catch (Exception e3) {
                    e3.printStackTrace();
                    return false;
                }
            }
            h.a(this.cordova.getActivity(), str3, "long");
            return true;
        }
        if (str.equals("getTokenConfig")) {
            try {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, new JSONObject("{\"rns_config\": {\"subscriber_url\" : \"http://api.rediff.com/core/3.0/rns/subscriber\",\"consumerKey\" : \"ef531e9be8964d13992d38f5a5c953037ed66c08\",\"consumerSecret\" : \"1345106446502cb20e4f64328291\",\"app_id\" : \"6\",\"notification_service_id\" : \"1\",\"clienttype\" : \"mailclient\"}, \"gcm_config\": {\"sender_id\" : \"694978106838\"}}")));
            } catch (JSONException e4) {
                e4.printStackTrace();
            } catch (Exception e5) {
                e5.printStackTrace();
            }
            return true;
        }
        if (str.equals("setAppUserAgent")) {
            String str4 = "";
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                try {
                    str4 = jSONArray.getJSONObject(i2).getString("appUserAgent");
                } catch (JSONException e6) {
                    e6.printStackTrace();
                    return false;
                } catch (Exception e7) {
                    e7.printStackTrace();
                    return false;
                }
            }
            if (!str4.equals("")) {
                h.b(this.h, str4);
            }
            return true;
        }
        if (str.equals("showKeyboard")) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.cordova.getActivity().getSystemService("input_method");
            try {
                view2 = (View) this.webView.getClass().getMethod("getView", new Class[0]).invoke(this.webView, new Object[0]);
            } catch (Exception e8) {
                view2 = (View) this.webView;
            }
            inputMethodManager.showSoftInput(view2, 0);
            callbackContext.success();
            return true;
        }
        if (str.equals("hideKeyboard")) {
            InputMethodManager inputMethodManager2 = (InputMethodManager) this.cordova.getActivity().getSystemService("input_method");
            try {
                view = (View) this.webView.getClass().getMethod("getView", new Class[0]).invoke(this.webView, new Object[0]);
            } catch (Exception e9) {
                view = (View) this.webView;
            }
            inputMethodManager2.hideSoftInputFromWindow(view.getWindowToken(), 0);
            callbackContext.success();
            return true;
        }
        if (str.equals("getSyncConfig")) {
            this.cordova.getActivity().runOnUiThread(new q(this, callbackContext));
            return true;
        }
        if (str.equals("openFileMan")) {
            return true;
        }
        if (str.equals("getFileSize")) {
            String str5 = "";
            int i3 = 0;
            while (i3 < jSONArray.length()) {
                try {
                    String string = jSONArray.getJSONObject(i3).getString("uri");
                    i3++;
                    str5 = string;
                } catch (JSONException e10) {
                    e10.printStackTrace();
                    return false;
                } catch (Exception e11) {
                    e11.printStackTrace();
                    return false;
                }
            }
            Log.d("RediffmailPlugin", "uriString : " + str5);
            Log.d("RediffmailPlugin", "uri : " + Uri.parse(str5).toString());
            String a2 = str5.contains("file://") ? com.ipaulpro.afilechooser.a.a.a(this.h, Uri.parse(str5)) : str5;
            File file = new File(a2);
            Log.d("RediffmailPlugin", "file.toString" + file.toString());
            if (file.exists()) {
                String l = Long.toString(file.length());
                str2 = (l.length() == 0 || !l.equals("")) ? l : l;
            } else {
                Log.d("RediffmailPlugin", "file does not exists");
                str2 = "";
            }
            Log.d("RediffmailPlugin", "totalSize" + str2 + "totalSize" + str2.length());
            if ((str2.equals("") || str2.length() == 0) && (query = this.h.getContentResolver().query(Uri.parse(a2), null, null, null, null)) != null) {
                int columnIndex = query.getColumnIndex("_size");
                query.moveToFirst();
                str2 = Long.toString(query.getLong(columnIndex));
                query.close();
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, new JSONObject("{\"uri\":\"" + a2 + "\", \"totalSize\":\"" + str2 + "\", \"maxSize\":\"25000000\", \"maxSizeReadable\":\"25 MB\"}")));
            return true;
        }
        if (str.equals("shareCompose")) {
            g = this.webView;
            if (f != null) {
                callbackContext.error("listener already running.");
                return true;
            }
            if (c != null) {
                pluginResult3 = new PluginResult(PluginResult.Status.OK, a(c));
                c = null;
            } else {
                pluginResult3 = new PluginResult(PluginResult.Status.ERROR, "Nothing to share");
            }
            pluginResult3.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult3);
            return true;
        }
        if (str.equals("outboxCompose")) {
            g = this.webView;
            if (f != null) {
                callbackContext.error("listener already running.");
                return true;
            }
            if (d != null) {
                pluginResult2 = new PluginResult(PluginResult.Status.OK, a(d));
                d = null;
            } else {
                pluginResult2 = new PluginResult(PluginResult.Status.ERROR, "Nothing to compose");
            }
            pluginResult2.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult2);
            return true;
        }
        if (str.equals("versionCheck")) {
            g = this.webView;
            if (f != null) {
                callbackContext.error("listener already running.");
                return true;
            }
            if (e != null) {
                pluginResult = new PluginResult(PluginResult.Status.OK, a(e));
                e = null;
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Nothing to update");
            }
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        if (str.equals("setVersionDeprecate")) {
            h.a(this.h, "1");
            return true;
        }
        if (str.equals("clearVersionDeprecate")) {
            h.a(this.h, "0");
            return true;
        }
        if (str.equals("getVersionDeprecate")) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "{\"versionDeprFlag\":\"" + h.d(this.h) + "\"}"));
            return true;
        }
        if (str.equals("storeSession")) {
            e();
            return true;
        }
        if (str.equals("clearSession")) {
            d();
            return true;
        }
        if (str.equals("getSession")) {
            String str6 = "";
            for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                try {
                    str6 = jSONArray.getJSONObject(i4).getString("domain");
                } catch (JSONException e12) {
                    e12.printStackTrace();
                    return false;
                } catch (Exception e13) {
                    e13.printStackTrace();
                    return false;
                }
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "{\"cookies\":\"" + CookieManager.getInstance().getCookie(str6) + "\"}"));
            return true;
        }
        if (str.equals("getEULASetting")) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "{\"eula_accept\":\"" + c() + "\"}"));
            return true;
        }
        if (str.equals("setEULASetting")) {
            a("1");
            if (!h.r(this.h)) {
            }
            return true;
        }
        if (str.equals("clearEULASetting")) {
            a("0");
            return true;
        }
        if (str.equals("openPlayStore")) {
            String str7 = "com.rediff.mail.and";
            try {
                str7 = this.cordova.getActivity().getPackageName();
            } catch (Exception e14) {
            }
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str7));
            intent.addFlags(DriveFile.MODE_READ_ONLY);
            this.h.startActivity(intent);
            b();
            return false;
        }
        if (str.equals("getAppName")) {
            try {
                PackageManager packageManager = this.cordova.getActivity().getPackageManager();
                callbackContext.success((String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.cordova.getActivity().getPackageName(), 0)));
                return true;
            } catch (Exception e15) {
                callbackContext.error("N/A");
                return false;
            }
        }
        if (str.equals("getPackageName")) {
            try {
                callbackContext.success(this.cordova.getActivity().getPackageName());
                return true;
            } catch (Exception e16) {
                callbackContext.error("N/A");
                return false;
            }
        }
        if (str.equals("getVersionNumber")) {
            try {
                callbackContext.success(this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
                return true;
            } catch (PackageManager.NameNotFoundException e17) {
                callbackContext.error("N/A");
                return false;
            }
        }
        if (str.equals("getVersionCode")) {
            try {
                callbackContext.success(this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionCode);
                return true;
            } catch (Exception e18) {
                callbackContext.error("N/A");
                return false;
            }
        }
        if (str.equals("getDldList")) {
            String str8 = "";
            for (int i5 = 0; i5 < jSONArray.length(); i5++) {
                try {
                    str8 = jSONArray.getJSONObject(i5).getString("dldPath");
                } catch (JSONException e19) {
                    e19.printStackTrace();
                    return false;
                } catch (Exception e20) {
                    e20.printStackTrace();
                    return false;
                }
            }
            if (!str8.equals("")) {
                this.cordova.getActivity().runOnUiThread(new r(this, str8, callbackContext));
                return true;
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "{}"));
            return false;
        }
        if (str.equals("execMediaScan")) {
            String str9 = "";
            for (int i6 = 0; i6 < jSONArray.length(); i6++) {
                try {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i6);
                    jSONObject2.getString("fullPath");
                    str9 = jSONObject2.getString("nativeUrl");
                } catch (JSONException e21) {
                    e21.printStackTrace();
                    return false;
                } catch (Exception e22) {
                    e22.printStackTrace();
                    return false;
                }
            }
            if (str9.equals("")) {
                return false;
            }
            String lowerCase = j.a(str9.toLowerCase()).toLowerCase();
            Log.d("RediffmailPlugin", "sMimeType: " + lowerCase);
            if (!lowerCase.startsWith("image") && !lowerCase.startsWith("video")) {
                return false;
            }
            this.h.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(str9)));
            return false;
        }
        if (str.equals("cleanAttachedFiles")) {
            JSONArray jSONArray2 = new JSONArray();
            for (int i7 = 0; i7 < jSONArray.length(); i7++) {
                try {
                    jSONArray2 = jSONArray.getJSONObject(i7).getJSONArray("shareDelFiles");
                } catch (JSONException e23) {
                    e23.printStackTrace();
                    return false;
                } catch (Exception e24) {
                    e24.printStackTrace();
                    return false;
                }
            }
            if (jSONArray2.length() > 0) {
                String str10 = d.t;
                for (int i8 = 0; i8 < jSONArray2.length(); i8++) {
                    File file2 = new File(str10 + "/" + jSONArray2.getString(i8));
                    if (file2.exists()) {
                        file2.delete();
                    }
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "{\"deleted\": \"1\"}"));
                return true;
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "{}"));
            return false;
        }
        if (str.contains("xhrRequest")) {
            if (RMailApplication.f214a == null) {
            }
            this.cordova.getThreadPool().execute(new s(this, str.replace("xhrRequest_", ""), callbackContext, jSONArray));
            return true;
        }
        if (!str.contains("sqlQuery")) {
            return false;
        }
        this.cordova.getThreadPool().execute(new t(this, str, jSONArray, callbackContext));
        return true;
    }

    public Boolean a(Context context) {
        try {
            this.f215a = (ConnectivityManager) context.getSystemService("connectivity");
            NetworkInfo activeNetworkInfo = this.f215a.getActiveNetworkInfo();
            this.b = activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
            return Boolean.valueOf(this.b);
        } catch (Exception e2) {
            return Boolean.valueOf(this.b);
        }
    }

    private String c() {
        SharedPreferences sharedPreferences = this.cordova.getActivity().getSharedPreferences(LocalService.RediffmailAppPrefs, 0);
        if (!sharedPreferences.contains("eula_accept")) {
            return "0";
        }
        return sharedPreferences.getString("eula_accept", "0");
    }

    private void a(String str) {
        SharedPreferences.Editor edit = this.cordova.getActivity().getSharedPreferences(LocalService.RediffmailAppPrefs, 0).edit();
        edit.putString("eula_accept", str);
        edit.commit();
    }

    private void d() {
        if (Build.VERSION.SDK_INT >= 21) {
            System.out.println("Using ClearCookies code for API >=" + Build.VERSION.SDK_INT);
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            return;
        }
        System.out.println("Using ClearCookies code for API <" + Build.VERSION.SDK_INT);
        CookieSyncManager createInstance = CookieSyncManager.createInstance(this.h);
        createInstance.startSync();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.removeSessionCookie();
        createInstance.stopSync();
        createInstance.sync();
    }

    private void e() {
        if (Build.VERSION.SDK_INT >= 21) {
            System.out.println("Using cookies code for API >=" + Build.VERSION.SDK_INT);
            CookieManager.getInstance().flush();
            return;
        }
        System.out.println("Using ClearCookies code for API <" + Build.VERSION.SDK_INT);
        CookieSyncManager createInstance = CookieSyncManager.createInstance(this.cordova.getActivity().getApplicationContext());
        createInstance.startSync();
        createInstance.stopSync();
        createInstance.sync();
    }

    public static void a(Bundle bundle, String str) {
        if (bundle != null) {
            Log.v("RediffmailPlugin", "sendExtras: caching extras to send at a later time.");
            if (f != null) {
                Log.v("RediffmailPlugin", "Callback context is present");
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, a(bundle));
                pluginResult.setKeepCallback(true);
                f.sendPluginResult(pluginResult);
                return;
            }
            if (str.equals("outbox")) {
                Log.v("RediffmailPlugin", "Callback context is not available");
                d = bundle;
            } else if (str.equals("versionUpdate")) {
                Log.v("RediffmailPlugin", "Callback context is not available");
                e = bundle;
            } else {
                Log.v("RediffmailPlugin", "Callback context is not available");
                c = bundle;
            }
        }
    }

    public static JSONObject a(Bundle bundle) {
        JSONObject put;
        JSONObject jSONObject;
        try {
            put = new JSONObject().put("event", "message");
            jSONObject = new JSONObject();
        } catch (JSONException e2) {
            Log.e("RediffmailPlugin", "extrasToJSON: JSON exception");
            return null;
        }
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj instanceof String) {
                String str2 = (String) obj;
                if (str2.startsWith("{")) {
                    try {
                        jSONObject.put(str, new JSONObject(str2));
                    } catch (Exception e3) {
                        jSONObject.put(str, obj);
                    }
                } else if (str2.startsWith("[")) {
                    try {
                        jSONObject.put(str, new JSONArray(str2));
                    } catch (Exception e4) {
                        jSONObject.put(str, obj);
                    }
                } else {
                    jSONObject.put(str, obj);
                }
                Log.e("RediffmailPlugin", "extrasToJSON: JSON exception");
                return null;
            }
        }
        put.put("payload", jSONObject);
        Log.v("RediffmailPlugin", "extrasToJSON: " + put.toString());
        return put;
    }

    public static boolean a() {
        return g != null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void onDestroy() {
        super.onDestroy();
        g = null;
    }

    public void b() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.cordova.getActivity().finishAndRemoveTask();
        } else {
            this.cordova.getActivity().finish();
        }
    }
}
