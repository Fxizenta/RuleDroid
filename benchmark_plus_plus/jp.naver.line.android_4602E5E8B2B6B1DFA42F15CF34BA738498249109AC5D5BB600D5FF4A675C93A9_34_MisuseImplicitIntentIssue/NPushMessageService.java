package jp.naver.android.npush.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;
import dalvik.system.DexClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.naver.android.npush.b.d;
import jp.naver.android.npush.b.h;

/* loaded from: classes.dex */
public class NPushMessageService extends Service {
    private Class d;
    private Object e;
    private d f;
    private DexClassLoader a = null;
    private boolean b = false;
    private final ArrayList c = new ArrayList();
    private int g = 24;
    private boolean h = false;
    private final BroadcastReceiver i = new a(this);

    @Override // android.app.Service
    public void onCreate() {
        String str = "NPushMessageService onCreate : " + getApplication().getPackageName();
        b();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void a(NPushMessageService nPushMessageService, String str, boolean z) {
        boolean z2;
        String str2 = "NPushMessageService onUpgradeComplete : dexPath=" + str + ", loadNewDex=" + z;
        if (nPushMessageService.a == null) {
            try {
                String str3 = "NPushMessageService loadClasses : dexPath=" + str;
                nPushMessageService.a = new DexClassLoader(str, nPushMessageService.getFilesDir().getAbsolutePath(), null, nPushMessageService.getClassLoader());
                nPushMessageService.d = nPushMessageService.a.loadClass("jp.naver.android.npush.network.NPushNetworkController");
                nPushMessageService.e = nPushMessageService.d.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                z2 = true;
            } catch (Exception e) {
                e.printStackTrace();
                Process.killProcess(Process.myPid());
                z2 = false;
            }
        } else if (z) {
            Process.killProcess(Process.myPid());
            z2 = false;
        } else {
            z2 = false;
        }
        Intent action = new Intent().setAction("jp.naver.android.npush.intent.action.CURRENTSTATE");
        action.putExtra("state", "Dex Library Load Completed");
        nPushMessageService.sendBroadcast(action);
        if (z2) {
            nPushMessageService.a("setServiceContext", Context.class, nPushMessageService);
            nPushMessageService.a("SERVICE_procOnCreate");
        }
        nPushMessageService.b = false;
        nPushMessageService.a();
        int i = nPushMessageService.g;
        String str4 = "NPushMessageService scheduleNextUpgrade : " + i + " hours later";
        ((AlarmManager) nPushMessageService.getSystemService("alarm")).set(0, (i * 3600000) + System.currentTimeMillis(), PendingIntent.getService(nPushMessageService, 0, new Intent("jp.naver.android.npush.intent.action.UPGRADE"), 134217728));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void a(NPushMessageService nPushMessageService, int i) {
        if (i > 0) {
            nPushMessageService.g = i;
        } else {
            nPushMessageService.g = 24;
        }
    }

    private void a() {
        if (this.c.size() != 0) {
            Iterator it = this.c.iterator();
            while (it.hasNext()) {
                a((Intent) it.next());
            }
        }
    }

    private void a(Intent intent) {
        if (intent != null) {
            String str = "NPushMessageService handleRequestIntent : intent.getAction()=" + intent.getAction();
            a("SERVICE_handleRequestIntent", Intent.class, intent);
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String str = "NPushMessageService onStartCommand : intent.getAction()=" + intent.getAction();
        }
        if (this.b || this.a == null) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals("jp.naver.android.npush.intent.action.SUBSCRIBE") || intent.getAction().equals("jp.naver.android.npush.intent.action.UNSUBSCRIBE")) {
                    b(intent);
                    return 1;
                }
                if (intent.getAction().equals("jp.naver.android.npush.intent.action.GETSTATE")) {
                    PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("app");
                    String stringExtra = intent.getStringExtra("serviceid");
                    Intent addCategory = new Intent().setAction("jp.naver.android.npush.intent.action.CURRENTSTATE").addCategory(stringExtra == null ? pendingIntent.getTargetPackage() : stringExtra);
                    addCategory.putExtra("state", "Dex Library Loading");
                    sendBroadcast(addCategory);
                    return 1;
                }
                if (intent.getAction().equals("jp.naver.android.npush.intent.action.GETVERSION")) {
                    c(intent);
                    return 1;
                }
                return 1;
            }
            return 1;
        }
        if (!h.a(this)) {
            stopSelf();
            startService(intent);
            return 1;
        }
        if (intent != null) {
            if (intent.getAction().equals("jp.naver.android.npush.intent.action.GETVERSION")) {
                c(intent);
                return 1;
            }
            if (intent.getAction().equals("jp.naver.android.npush.intent.action.UPGRADE")) {
                b();
                return 1;
            }
            a(intent);
            return 1;
        }
        return 1;
    }

    private void b(Intent intent) {
        String stringExtra = intent.getStringExtra("serviceid");
        if (!TextUtils.isEmpty(stringExtra)) {
            Iterator it = this.c.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Intent intent2 = (Intent) it.next();
                String stringExtra2 = intent2.getStringExtra("serviceid");
                if (!TextUtils.isEmpty(stringExtra2) && stringExtra2.equals(stringExtra)) {
                    this.c.remove(intent2);
                    break;
                }
            }
            this.c.add(intent);
        }
    }

    private void c(Intent intent) {
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("app");
        String stringExtra = intent.getStringExtra("serviceid");
        Intent addCategory = new Intent().setAction("jp.naver.android.npush.intent.action.CURRENTVERSION").addCategory(stringExtra == null ? pendingIntent.getTargetPackage() : stringExtra);
        addCategory.putExtra("version", String.format("service : %d, library : %s", Integer.valueOf(h.b(this)), this.f.d()));
        sendBroadcast(addCategory);
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.a != null && !this.b) {
            a("SERVICE_onDestroy");
        }
        Intent action = new Intent().setAction("jp.naver.android.npush.intent.action.CURRENTSTATE");
        action.putExtra("state", "Service onDestroy");
        sendBroadcast(action);
        Process.killProcess(Process.myPid());
    }

    private void b() {
        a(2);
        this.b = true;
        this.f = new d(this);
        this.f.a(new b(this));
        this.f.a();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i) {
        String str = "NPushMessageService informStatusToSubscribers : status=" + i;
        try {
            this.d.getMethod("SERVICE_informStatusToSubscribers", Integer.TYPE).invoke(this.e, Integer.valueOf(i));
        } catch (Exception e) {
            b(i);
        }
    }

    private void b(int i) {
        List c = jp.naver.android.npush.a.b.c(this);
        List<String> arrayList = c == null ? new ArrayList() : c;
        Iterator it = this.c.iterator();
        while (it.hasNext()) {
            Intent intent = (Intent) it.next();
            if (!TextUtils.isEmpty(intent.getAction())) {
                String action = intent.getAction();
                String stringExtra = intent.getStringExtra("serviceid");
                if (action.equals("jp.naver.android.npush.intent.action.SUBSCRIBE")) {
                    if (!arrayList.contains(stringExtra)) {
                        arrayList.add(stringExtra);
                    }
                } else if (action.equals("jp.naver.android.npush.intent.action.UNSUBSCRIBE") && arrayList.contains(stringExtra)) {
                    arrayList.remove(stringExtra);
                }
            }
        }
        if (arrayList.size() > 0) {
            for (String str : arrayList) {
                Intent addCategory = new Intent("jp.naver.android.npush.intent.action.INFORM_STATUS").addCategory(str);
                addCategory.putExtra("staus", i);
                String str2 = "NPushMessageService informStatusToSubscribers : broadcast status=" + i + " to " + str;
                sendBroadcast(addCategory);
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Object a(String str) {
        try {
            String str2 = "NPushMessageService invokeFunc : invoke " + str;
            return this.d.getMethod(str, new Class[0]).invoke(this.e, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            String str3 = "NPushMessageService invokeFunc : error on invoke " + str + "!! kill process!!";
            Process.killProcess(Process.myPid());
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object a(String str, Class cls, Object obj) {
        try {
            String str2 = "NPushMessageService invokeFunc : invoke " + str + " with param : " + obj.toString();
            return this.d.getMethod(str, cls).invoke(this.e, obj);
        } catch (Exception e) {
            e.printStackTrace();
            String str3 = "NPushMessageService invokeFunc : error on invoke " + str + "!! kill process!!";
            Process.killProcess(Process.myPid());
            return null;
        }
    }
}
