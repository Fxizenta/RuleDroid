package bytedance.framwork.core.sdkmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import bytedance.framwork.core.sdkmonitor.MonitorNetUtil;
import bytedance.framwork.core.sdkmonitor.a;
import com.bytedance.frameworks.baselib.log.LogLib;
import com.bytedance.frameworks.core.monitor.MonitorManager;
import com.bytedance.frameworks.core.monitor.config.IMonitorConfigure;
import com.bytedance.frameworks.core.monitor.net.ISendLog;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SDKMonitor implements MonitorManager.IHandleOnTimerEvent, IMonitorConfigure, ISendLog {
    private static ConcurrentHashMap<String, List<String>> x = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, List<String>> y = new ConcurrentHashMap<>();

    /* renamed from: a, reason: collision with root package name */
    private Context f2474a;

    /* renamed from: b, reason: collision with root package name */
    private JSONObject f2475b;
    private MonitorManager c;
    private volatile long d;
    private volatile long e;
    private volatile int f;
    private volatile int g;
    private volatile int h;
    private volatile int i;
    private volatile JSONObject j;
    private volatile long l;
    private HashMap<String, String> m;
    public IGetCommonParams mGetCommonParams;
    private volatile boolean n;
    private volatile boolean r;
    private volatile boolean s;
    private volatile long t;
    private List<String> u;
    private List<String> v;
    private String z;
    private volatile int k = 1;
    public List<String> mConfigUrls = new ArrayList(Arrays.asList("http://mon.snssdk.com/monitor/appmonitor/v2/settings", "http://monsetting.toutiao.com/monitor/appmonitor/v2/settings"));
    private volatile long o = 0;
    private volatile long p = 0;
    private volatile int q = 0;
    public volatile boolean mIsEncrypt = true;
    private List<String> w = new ArrayList(Arrays.asList("http://mon.snssdk.com/monitor/collect/", "http://mon.toutiao.com/monitor/collect/", "http://mon.toutiaocloud.com/monitor/collect/", "http://mon.toutiaocloud.net/monitor/collect/"));
    public volatile boolean mConfigExit = false;
    public a mCacheData = new a();

    /* loaded from: classes2.dex */
    public interface IGetCommonParams {
        String getSessionId();
    }

    private void b(JSONObject jSONObject) {
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public String getReportTypeByUploadType(String str) {
        return "monitor";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void a(@NonNull String str, @NonNull List<String> list) {
        x.put(str, list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void b(@NonNull String str, @NonNull List<String> list) {
        y.put(str, list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SDKMonitor(Context context, String str, JSONObject jSONObject, IGetCommonParams iGetCommonParams) {
        this.z = str;
        init(context, jSONObject, iGetCommonParams);
    }

    public boolean init(@NonNull Context context, @NonNull JSONObject jSONObject, @NonNull IGetCommonParams iGetCommonParams) {
        if (jSONObject == null || jSONObject.length() <= 0) {
            return false;
        }
        a(context, jSONObject, iGetCommonParams);
        if (!this.mConfigExit) {
            return true;
        }
        this.mCacheData.handleCacheData(this);
        return true;
    }

    private void a(Context context, JSONObject jSONObject, IGetCommonParams iGetCommonParams) {
        this.s = true;
        this.f2474a = context.getApplicationContext();
        this.f2475b = jSONObject;
        if (TextUtils.isEmpty(this.f2475b.optString("package_name"))) {
            try {
                this.f2475b.put("package_name", context.getPackageName());
            } catch (Exception unused) {
            }
        }
        try {
            this.f2475b.put("aid", this.z);
            this.f2475b.put("os", "Android");
            this.f2475b.put("device_platform", "android");
            this.f2475b.put("os_version", Build.VERSION.RELEASE);
            this.f2475b.put("os_api", Build.VERSION.SDK_INT);
            this.f2475b.put("device_model", Build.MODEL);
            this.f2475b.put("device_brand", Build.BRAND);
            this.f2475b.put("device_manufacturer", Build.MANUFACTURER);
            PackageInfo packageInfo = null;
            if (TextUtils.isEmpty(this.f2475b.optString("version_name"))) {
                packageInfo = this.f2474a.getPackageManager().getPackageInfo(this.f2474a.getPackageName(), 0);
                this.f2475b.put("version_name", packageInfo.versionName);
            }
            if (TextUtils.isEmpty(this.f2475b.optString("version_code"))) {
                if (packageInfo == null) {
                    packageInfo = this.f2474a.getPackageManager().getPackageInfo(this.f2474a.getPackageName(), 0);
                }
                this.f2475b.put("version_code", packageInfo.versionCode);
            }
            this.mGetCommonParams = iGetCommonParams;
            if (this.mGetCommonParams == null) {
                this.mGetCommonParams = new IGetCommonParams() { // from class: bytedance.framwork.core.sdkmonitor.SDKMonitor.1
                    @Override // bytedance.framwork.core.sdkmonitor.SDKMonitor.IGetCommonParams
                    public String getSessionId() {
                        return null;
                    }
                };
            }
            if (this.m == null) {
                this.m = new HashMap<>();
            }
            this.m.put("aid", this.z);
            this.m.put("device_id", this.f2475b.optString("device_id"));
            this.m.put("device_platform", "android");
            this.m.put("package_name", this.f2475b.optString("package_name"));
            this.m.put("channel", this.f2475b.optString("channel"));
            this.m.put("app_version", this.f2475b.optString("app_version"));
            com.bytedance.frameworks.core.monitor.config.b.setCommonConfig(this.z, this);
            com.bytedance.frameworks.core.monitor.net.b.setISendLog(this.z, this);
            e();
            this.c = new MonitorManager(context.getApplicationContext(), this, this.z);
            List<String> list = x.get(this.z);
            if (!d.isEmpty(list)) {
                this.mConfigUrls.clear();
                this.mConfigUrls.addAll(list);
            }
            List<String> list2 = y.get(this.z);
            if (!d.isEmpty(list2)) {
                this.w.clear();
                this.w.addAll(list2);
            }
            x.clear();
            y.clear();
            b();
            d();
        } catch (Exception unused2) {
        }
    }

    private void b() {
        SharedPreferences sharedPreferences = com.ss.android.ugc.aweme.n.c.getSharedPreferences(this.f2474a, "monitor_config" + this.z, 0);
        String string = sharedPreferences.getString("monitor_net_config", null);
        this.d = sharedPreferences.getLong("monitor_configure_refresh_time", 0L);
        if (string != null && !TextUtils.isEmpty(string)) {
            try {
                this.mConfigExit = true;
                a(new JSONObject(string));
            } catch (Exception unused) {
            }
        }
        a(false);
    }

    @VisibleForTesting
    void a(boolean z) {
        if (this.e < 600) {
            this.e = 600L;
        }
        if ((z || c()) && MonitorNetUtil.isNetworkAvailable(this.f2474a)) {
            synchronized (SDKMonitor.class) {
                this.d = System.currentTimeMillis();
            }
            try {
                com.bytedance.frameworks.core.thread.a.getTTExecutor().executeApiTask(new com.bytedance.frameworks.core.thread.b() { // from class: bytedance.framwork.core.sdkmonitor.SDKMonitor.2
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            if (SDKMonitor.this.mGetCommonParams != null) {
                                Iterator<String> it2 = SDKMonitor.this.mConfigUrls.iterator();
                                while (it2.hasNext()) {
                                    try {
                                        byte[] request = MonitorNetUtil.getRequest(SDKMonitor.this.addParamsToURL(it2.next()) + "&encrypt=close", null, SDKMonitor.this.mIsEncrypt);
                                        if (request != null) {
                                            SDKMonitor.this.updateToSP(new JSONObject(new String(request)));
                                            return;
                                        }
                                        return;
                                    } catch (Throwable unused) {
                                    }
                                }
                            }
                        } catch (Throwable unused2) {
                            if (SDKMonitor.this.mConfigExit) {
                                return;
                            }
                            SDKMonitor.this.mConfigExit = true;
                            SDKMonitor.this.mCacheData.handleCacheData(SDKMonitor.this);
                        }
                    }
                });
            } catch (Throwable unused) {
            }
        }
    }

    private boolean c() {
        return (System.currentTimeMillis() - this.d) / 1000 > this.e;
    }

    public void updateToSP(JSONObject jSONObject) {
        if (jSONObject == null || jSONObject.length() <= 0) {
            return;
        }
        try {
            a(jSONObject.getJSONObject("data"));
            SharedPreferences.Editor edit = com.ss.android.ugc.aweme.n.c.getSharedPreferences(this.f2474a, "monitor_config" + this.z, 0).edit();
            edit.putLong("monitor_configure_refresh_time", System.currentTimeMillis());
            edit.putString("monitor_net_config", jSONObject.getJSONObject("data").toString());
            edit.apply();
            if (this.mConfigExit) {
                return;
            }
            this.mConfigExit = true;
            this.mCacheData.handleCacheData(this);
        } catch (Throwable unused) {
        }
    }

    synchronized void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            if (jSONObject.length() > 0) {
                ArrayList arrayList = new ArrayList();
                try {
                    JSONArray optJSONArray = jSONObject.optJSONArray("report_host_new");
                    if (optJSONArray != null && optJSONArray.length() > 0) {
                        int length = optJSONArray.length();
                        for (int i = 0; i < length; i++) {
                            String string = optJSONArray.getString(i);
                            if (!TextUtils.isEmpty(string) && string.indexOf(46) > 0) {
                                arrayList.add(string);
                            }
                        }
                    }
                } catch (Exception unused) {
                }
                if (!arrayList.isEmpty()) {
                    this.w = arrayList;
                }
                this.e = jSONObject.optLong("fetch_setting_interval", 1200L);
                this.f = jSONObject.optInt("polling_interval", 120);
                this.g = jSONObject.optInt("once_max_count", 100);
                this.h = jSONObject.optInt("max_retry_count", 4);
                this.i = jSONObject.optInt("report_fail_base_time", 15);
                this.k = jSONObject.optInt("log_send_switch", 1);
                this.l = jSONObject.optLong("more_channel_stop_interval", 1800L);
                this.n = jSONObject.optBoolean("log_remvove_switch", false);
                this.j = jSONObject.optJSONObject("allow_service_name");
                this.mIsEncrypt = jSONObject.optBoolean("monitor_encrypt_switch", true);
                this.t = jSONObject.optLong("monitor_log_max_save_count", 40000L);
                if (this.c != null) {
                    this.c.updateConfig();
                }
                return;
            }
        }
        this.w = null;
    }

    private void d() {
        if (this.c == null) {
            return;
        }
        try {
            this.c.setCurrentVersionInfo(new com.bytedance.frameworks.core.monitor.a.e(this.f2475b.optString("version_code"), this.f2475b.optString("version_name"), this.f2475b.optString("manifest_version_code"), this.f2475b.optString("update_version_code")));
        } catch (Exception unused) {
        }
    }

    public MonitorManager getMonitorManager() {
        return this.c;
    }

    public boolean getServiceSwitch(String str) {
        return (this.j == null || TextUtils.isEmpty(str) || this.j.opt(str) == null) ? false : true;
    }

    boolean a() {
        return this.k == 1;
    }

    public int getNetWorkType() {
        return MonitorNetUtil.getNetworkType(this.f2474a.getApplicationContext()).getValue();
    }

    @Override // com.bytedance.frameworks.core.monitor.MonitorManager.IHandleOnTimerEvent
    public void handleEvent() {
        if (System.currentTimeMillis() - this.o > this.p) {
            this.r = false;
            if (this.c != null) {
                this.c.setReportLogSwitch(a());
            }
        }
        if (this.e <= 0) {
            return;
        }
        a(false);
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public boolean isLogSendSwitch() {
        return this.k == 1;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public int reportInterval() {
        if (this.f <= 0) {
            return 120;
        }
        return this.f;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public int reportCount() {
        if (this.g <= 0) {
            return 100;
        }
        return this.g;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public List<String> reportUrl(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, "monitor")) {
            return this.w;
        }
        return Collections.emptyList();
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public List<String> getReportTypeList() {
        if (this.u == null) {
            this.u = Arrays.asList("monitor");
        }
        return this.u;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public List<String> getUploadTypeByReportType(String str) {
        if (!TextUtils.isEmpty(str) && !TextUtils.equals(str, "monitor")) {
            return null;
        }
        if (this.v == null) {
            this.v = Arrays.asList("service_monitor");
        }
        return this.v;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public long getMonitorLogMaxSaveCount() {
        return this.t;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public JSONObject reportJsonHeaderInfo() {
        return this.f2475b;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public int reportFailRepeatCount() {
        if (this.h <= 0) {
            return 4;
        }
        return this.h;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public int reportFailRepeatBaseTime() {
        if (this.i <= 0) {
            return 15;
        }
        return this.i;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public long stopMoreChannelInterval() {
        if (this.l == 0) {
            return 1800000L;
        }
        return this.l * 1000;
    }

    @Override // com.bytedance.frameworks.core.monitor.config.IMonitorConfigure
    public boolean getRemoveSwitch() {
        return this.r ? this.r : this.n;
    }

    @Override // com.bytedance.frameworks.core.monitor.net.ISendLog
    public com.bytedance.frameworks.core.monitor.net.c sendLog(long j, String str, byte[] bArr, int i, String str2) {
        byte[] excutePost;
        com.bytedance.frameworks.core.monitor.net.c cVar = new com.bytedance.frameworks.core.monitor.net.c();
        try {
            String addParamsToURL = addParamsToURL(str);
            if (i == 1) {
                excutePost = MonitorNetUtil.excutePost(j, addParamsToURL, bArr, MonitorNetUtil.a.GZIP, str2, this.mIsEncrypt);
            } else {
                excutePost = MonitorNetUtil.excutePost(j, addParamsToURL, bArr, MonitorNetUtil.a.NONE, str2, this.mIsEncrypt);
            }
            this.q = 0;
            this.p = 0L;
            cVar.stateCode = 200;
            JSONObject jSONObject = new JSONObject(new String(excutePost));
            try {
                b(jSONObject);
            } catch (Throwable unused) {
            }
            cVar.responseMsg = jSONObject;
            return cVar;
        } catch (Throwable th) {
            if (th instanceof b) {
                cVar.stateCode = ((b) th).getStatusCode();
            } else {
                cVar.stateCode = -1;
            }
            if (cVar.stateCode == 503 || cVar.stateCode == 509) {
                this.r = true;
                this.o = System.currentTimeMillis();
                if (this.c != null) {
                    this.c.setReportLogSwitch(false);
                }
                if (this.q == 0) {
                    this.p = 300000L;
                } else if (this.q == 1) {
                    this.p = 900000L;
                } else {
                    this.p = 1800000L;
                }
                this.q++;
            }
            return cVar;
        }
    }

    public String addParamsToURL(String str) {
        if (TextUtils.isDigitsOnly(str) || this.m == null) {
            return str;
        }
        if (str.indexOf("?") < 0) {
            str = str + "?";
        }
        String str2 = str.endsWith("?") ? str + a("sdk_version", "UTF-8") + "=" + a(String.valueOf(400), "UTF-8") : str + "&" + a("sdk_version", "UTF-8") + "=" + a(String.valueOf(400), "UTF-8");
        HashMap<String, String> hashMap = this.m;
        if (hashMap != null && hashMap.size() > 0) {
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (hashMap.get(entry.getKey()) != null) {
                    str2 = str2.endsWith("?") ? str2 + a(entry.getKey().toString(), "UTF-8") + "=" + a(hashMap.get(entry.getKey()).toString(), "UTF-8") : str2 + "&" + a(entry.getKey().toString(), "UTF-8") + "=" + a(hashMap.get(entry.getKey()).toString(), "UTF-8");
                }
            }
        }
        return str2;
    }

    private static String a(String str, String str2) {
        if (str2 == null) {
            str2 = "UTF-8";
        }
        try {
            return URLEncoder.encode(str, str2);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void e() {
        LogLib.init(new LogLib.ILogDelegate() { // from class: bytedance.framwork.core.sdkmonitor.SDKMonitor.3
            @Override // com.bytedance.frameworks.baselib.log.LogLib.ILogDelegate
            public boolean isNetworkAvailable(Context context) {
                return MonitorNetUtil.isNetworkAvailable(context);
            }
        });
    }

    private String f() {
        if (this.mGetCommonParams != null) {
            return this.mGetCommonParams.getSessionId();
        }
        return null;
    }

    public void monitorStatusAndDuration(String str, int i, JSONObject jSONObject, JSONObject jSONObject2) {
        try {
            if (!this.mConfigExit) {
                this.mCacheData.insertServiceMonitorData(new a.C0056a(str, i, jSONObject, jSONObject2));
            } else {
                a(str, i, jSONObject, jSONObject2);
            }
        } catch (Throwable unused) {
        }
    }

    public void monitorStatusRate(String str, int i, JSONObject jSONObject) {
        monitorStatusAndDuration(str, i, null, jSONObject);
    }

    public void monitorDuration(String str, JSONObject jSONObject, JSONObject jSONObject2) {
        monitorStatusAndDuration(str, 0, jSONObject, jSONObject2);
    }

    public void monitorEvent(String str, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3) {
        if (jSONObject3 == null) {
            try {
                jSONObject3 = new JSONObject();
            } catch (Throwable unused) {
                return;
            }
        }
        jSONObject3.put("log_type", "service_monitor");
        jSONObject3.put("service", str);
        jSONObject3.put("network_type", getNetWorkType());
        if (jSONObject != null) {
            jSONObject3.put("category", jSONObject);
        }
        if (jSONObject2 != null) {
            jSONObject3.put("metric", jSONObject2);
        }
        jSONObject3.put("timestamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(f())) {
            jSONObject3.put("session_id", f());
        }
        if (getMonitorManager() == null || !getServiceSwitch(str)) {
            return;
        }
        getMonitorManager().logSend("service_monitor", jSONObject3.toString(), true);
    }

    private void a(String str, int i, JSONObject jSONObject, JSONObject jSONObject2) {
        if (jSONObject2 == null) {
            try {
                jSONObject2 = new JSONObject();
            } catch (Throwable unused) {
                return;
            }
        }
        jSONObject2.put("log_type", "service_monitor");
        jSONObject2.put("service", str);
        jSONObject2.put("status", i);
        jSONObject2.put("network_type", getNetWorkType());
        jSONObject2.put("value", jSONObject);
        jSONObject2.put("timestamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(f())) {
            jSONObject2.put("session_id", f());
        }
        if (getMonitorManager() == null || !getServiceSwitch(str)) {
            return;
        }
        getMonitorManager().logSend("service_monitor", jSONObject2.toString(), true);
    }
}
