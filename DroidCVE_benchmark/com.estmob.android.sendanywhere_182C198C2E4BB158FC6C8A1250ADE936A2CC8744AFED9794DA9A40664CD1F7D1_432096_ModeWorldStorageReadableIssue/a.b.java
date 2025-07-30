package com.estmob.paprika.transfer.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.facebook.internal.NativeProtocol;
import com.facebook.places.model.PlaceFields;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import java.lang.reflect.Field;
import java.util.Locale;
import org.json.JSONObject;

/* compiled from: DeviceInfo.java */
/* loaded from: classes.dex */
public final class b extends a {
    public static String b;
    public static String c;
    public static String d;
    public static String e;
    public static String f;
    public static String g;
    public static String h;
    public static String i;
    public static String j;
    public static String k;
    public static String l;
    public static String m;
    public static String n;

    public b() {
        b = Build.VERSION.RELEASE;
        d = Build.MODEL;
        e = Build.MANUFACTURER;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        for (Field field : getClass().getDeclaredFields()) {
            try {
                if (!field.get(this).equals(bVar.getClass().getField(field.getName()).get(bVar))) {
                    return false;
                }
            } catch (Exception unused) {
                return true;
            }
        }
        return true;
    }

    private static String b(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    private static String c(Context context) {
        AdvertisingIdClient.Info info;
        SharedPreferences sharedPreferences = context.getSharedPreferences("sendanywhere_device", 0);
        String string = sharedPreferences.getString("advertising_id", null);
        if (string != null) {
            return string;
        }
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (Exception e2) {
            e2.printStackTrace();
            info = null;
        }
        if (info == null) {
            return null;
        }
        String id = info.getId();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("advertising_id", id);
        edit.commit();
        return id;
    }

    public static b a(Context context) {
        int i2;
        WifiInfo connectionInfo;
        String macAddress;
        b bVar = new b();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(PlaceFields.PHONE);
        if (telephonyManager != null) {
            try {
                if (g == null) {
                    g = telephonyManager.getNetworkOperatorName();
                }
                if (h == null) {
                    h = telephonyManager.getNetworkCountryIso();
                }
            } catch (SecurityException unused) {
            }
        }
        if (f == null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            String str = null;
            if (wifiManager != null && (connectionInfo = wifiManager.getConnectionInfo()) != null && (macAddress = connectionInfo.getMacAddress()) != null && macAddress.length() >= 12) {
                str = macAddress.toUpperCase();
            }
            f = str;
        }
        if (c == null) {
            c = Settings.Secure.getString(context.getContentResolver(), "android_id");
        }
        if (j == null) {
            j = b(context);
        }
        if (l == null && (i2 = context.getApplicationInfo().labelRes) > 0) {
            l = context.getString(i2);
        }
        if (m == null) {
            m = context.getPackageName();
        }
        k = Locale.getDefault().getLanguage();
        if (n == null) {
            n = c(context);
        }
        return bVar;
    }

    @Override // com.estmob.paprika.transfer.a.a
    public final void a(JSONObject jSONObject) {
        super.a(jSONObject);
        jSONObject.put("os_version", b);
        jSONObject.put("device_uid", c);
        jSONObject.put("model_number", d);
        jSONObject.put("manufacturer", e);
        jSONObject.put("mac_address", f);
        jSONObject.put("carrier", g);
        jSONObject.put("network_country", h);
        jSONObject.put("phone_number_hash", i);
        jSONObject.put("app_version", j);
        jSONObject.put("device_language", k);
        jSONObject.put(NativeProtocol.BRIDGE_ARG_APP_NAME_STRING, l);
        jSONObject.put("package_name", m);
        jSONObject.put("advertising_id", n);
    }
}
