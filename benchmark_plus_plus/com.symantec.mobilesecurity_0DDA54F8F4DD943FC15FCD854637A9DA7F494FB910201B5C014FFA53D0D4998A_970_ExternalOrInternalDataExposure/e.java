package com.symantec.b.a;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.symgson.Gson;
import com.google.symgson.GsonBuilder;
import com.symantec.util.n;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public final class e {
    private static final byte[] a = {54, 41, 63, 112, -13, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64, 89};
    private static final e b = new e();
    private Context c;
    private final l d = new l();
    private final ArrayList<k> e = new ArrayList<>();
    private final i f = new i(this);
    private a g;
    private j h;

    public static /* synthetic */ void d(e eVar) {
        synchronized (eVar.e) {
            Log.d("FingerprintManager", "calling observers:" + eVar.e.size());
            Iterator<k> it = eVar.e.iterator();
            while (it.hasNext()) {
                it.next().a();
            }
        }
    }

    public static e a() {
        return b;
    }

    public final synchronized l a(boolean z) {
        if (!d()) {
            throw new RuntimeException("not initialized");
        }
        return new l(this.d);
    }

    public final synchronized void a(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        Log.d("FingerprintManager", "context=" + context.toString());
        Log.d("FingerprintManager", "appcontext=" + context.getApplicationContext().toString());
        if (true != d()) {
            this.c = context.getApplicationContext();
            Log.d("FingerprintManager", "applicationContext=" + this.c.toString());
            b(true);
        }
    }

    public final String a(File file) {
        if (!d()) {
            throw new RuntimeException("not initialized");
        }
        if (file == null) {
            throw new IllegalArgumentException();
        }
        Log.d("FingerprintManager", "readData: " + file.getAbsolutePath());
        synchronized (this) {
            if (this.g == null) {
                String str = Build.MANUFACTURER + "_" + Build.MODEL;
                Log.d("FingerprintManager", "creating obfuscator: " + str);
                this.g = new a(a, "com.symantec.drm.malt", str);
            }
        }
        Gson create = new GsonBuilder().serializeNulls().create();
        FileInputStream fileInputStream = new FileInputStream(file);
        Log.d("FingerprintManager", "file length=" + file.length());
        byte[] bArr = new byte[(int) file.length()];
        fileInputStream.read(bArr);
        fileInputStream.close();
        h hVar = (h) create.fromJson(this.g.a(bArr), h.class);
        Log.d("FingerprintManager", "match hash");
        if (!hVar.c.equals(new String(b.a(n.a(hVar.a, hVar.b))))) {
            Log.e("FingerprintManager", "hash mismatch " + file.getAbsolutePath());
            throw new RuntimeException("hash does not match");
        }
        Log.d("FingerprintManager", "match fingerprints");
        l a2 = l.a(new String(b.b(hVar.a.getBytes())));
        if (a2 == null || !a(a2)) {
            Log.e("FingerprintManager", "fingerprint mismatch " + file.getAbsolutePath());
            throw new RuntimeException("fingerprint does not match");
        }
        if (true == b(a2)) {
            new g(this).start();
        }
        Log.d("FingerprintManager", file.getAbsolutePath() + " read successfully");
        return new String(b.b(hVar.b.getBytes()));
    }

    public final void a(File file, String str) {
        if (!d()) {
            throw new RuntimeException("not initialized");
        }
        if (file == null || str == null) {
            throw new IllegalArgumentException();
        }
        Log.d("FingerprintManager", "writeData: " + file.getAbsolutePath());
        h hVar = new h();
        synchronized (this) {
            hVar.a = new String(b.a(l.a(this.d).getBytes()));
            if (this.g == null) {
                this.g = new a(a, "com.symantec.drm.malt", Build.MANUFACTURER + "_" + Build.MODEL);
            }
        }
        hVar.b = new String(b.a(str.getBytes()));
        hVar.c = new String(b.a(n.a(hVar.a, hVar.b)));
        Gson create = new GsonBuilder().serializeNulls().create();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(this.g.a(create.toJson(hVar)));
        fileOutputStream.close();
        Log.d("FingerprintManager", file.length() + " bytes written to " + file.getAbsolutePath());
    }

    public final synchronized UUID b() {
        UUID nameUUIDFromBytes;
        if (!d()) {
            throw new RuntimeException("not initialized");
        }
        c cVar = this.d.a.get(d.ANDROID_ID);
        if (!cVar.b()) {
            nameUUIDFromBytes = null;
        } else {
            nameUUIDFromBytes = UUID.nameUUIDFromBytes(cVar.a().getBytes());
            Log.d("FingerprintManager", "mid (ANDROID_ID): " + nameUUIDFromBytes);
        }
        return nameUUIDFromBytes;
    }

    public void c() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".symantec_persisted");
        file.mkdirs();
        File file2 = new File(file, "mid.dat");
        try {
            a(file2, "");
            Log.d("FingerprintManager", "successfully wrote mid to " + file2.getAbsolutePath());
        } catch (Exception e) {
            Log.d("FingerprintManager", "writeFingerprint: " + e.toString());
        }
    }

    private boolean a(l lVar) {
        if (lVar == null) {
            throw new IllegalArgumentException();
        }
        for (d dVar : this.d.a.keySet()) {
            c cVar = this.d.a.get(dVar);
            if (cVar.b()) {
                c cVar2 = lVar.a.get(dVar);
                Log.d("FingerprintManager", "oldFingerprint=" + cVar2);
                if (cVar2 != null && cVar2.b() && true == cVar2.a().equalsIgnoreCase(cVar.a())) {
                    Log.d("FingerprintManager", "device is same as " + cVar2.toString() + " matches");
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized boolean b(l lVar) {
        boolean z;
        c cVar;
        if (lVar == null) {
            throw new IllegalArgumentException();
        }
        z = false;
        for (d dVar : this.d.a.keySet()) {
            c cVar2 = this.d.a.get(dVar);
            if (true != cVar2.b() && (cVar = lVar.a.get(dVar)) != null && cVar.b()) {
                Log.d("FingerprintManager", "copying saved fingerprint: " + cVar.toString() + " -> " + cVar2.toString());
                cVar2.a(cVar.a());
                z = true;
            }
        }
        return z;
    }

    private e() {
        this.d.a.put(d.ANDROID_ID, new c(d.ANDROID_ID));
        this.d.a.put(d.SERIAL_NO, new c(d.SERIAL_NO));
        this.d.a.put(d.PHONE_ID, new c(d.PHONE_ID));
        this.d.a.put(d.WIFI_MAC, new c(d.WIFI_MAC));
        this.d.a.put(d.BLUETOOTH_MAC, new c(d.BLUETOOTH_MAC));
    }

    private boolean d() {
        return this.c != null;
    }

    private void e() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        try {
            this.c.registerReceiver(this.f, intentFilter);
        } catch (IllegalArgumentException e) {
        }
    }

    private void f() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        try {
            this.c.registerReceiver(this.f, intentFilter);
        } catch (IllegalArgumentException e) {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00aa A[Catch: all -> 0x00d5, TRY_ENTER, TRY_LEAVE, TryCatch #1 {, blocks: (B:4:0x0003, B:7:0x000a, B:10:0x0013, B:13:0x001b, B:16:0x0023, B:19:0x002b, B:21:0x0054, B:22:0x006f, B:24:0x0081, B:26:0x0089, B:28:0x0090, B:31:0x00aa, B:38:0x00d9, B:42:0x00bc), top: B:3:0x0003, inners: #0, #2 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private synchronized void b(boolean r7) {
        /*
            Method dump skipped, instructions count: 243
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.symantec.b.a.e.b(boolean):void");
    }

    private synchronized boolean g() {
        boolean b2;
        c cVar = this.d.a.get(d.ANDROID_ID);
        if (cVar.b()) {
            b2 = false;
        } else {
            String string = Settings.Secure.getString(this.c.getContentResolver(), "android_id");
            if (true == TextUtils.isEmpty(string)) {
                Log.e("FingerprintManager", "empty ANDROID_ID");
                b2 = false;
            } else if (true == string.equalsIgnoreCase("9774d56d682e549c")) {
                Log.e("FingerprintManager", "bogus ANDROID_ID");
                b2 = false;
            } else {
                cVar.a(string);
                Log.d("FingerprintManager", cVar.toString());
                b2 = cVar.b();
            }
        }
        return b2;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0061 A[Catch: all -> 0x00b2, TryCatch #0 {, blocks: (B:4:0x0004, B:10:0x0019, B:26:0x001f, B:28:0x0032, B:21:0x0039, B:16:0x005b, B:18:0x0061, B:19:0x00a0, B:24:0x0086, B:32:0x006b), top: B:3:0x0004, inners: #1, #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x00a0 A[Catch: all -> 0x00b2, TRY_LEAVE, TryCatch #0 {, blocks: (B:4:0x0004, B:10:0x0019, B:26:0x001f, B:28:0x0032, B:21:0x0039, B:16:0x005b, B:18:0x0061, B:19:0x00a0, B:24:0x0086, B:32:0x006b), top: B:3:0x0004, inners: #1, #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0039 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private synchronized boolean h() {
        /*
            r9 = this;
            r1 = 0
            r8 = 1
            r2 = 0
            monitor-enter(r9)
            com.symantec.b.a.l r0 = r9.d     // Catch: java.lang.Throwable -> Lb2
            java.util.HashMap<com.symantec.b.a.d, com.symantec.b.a.c> r0 = r0.a     // Catch: java.lang.Throwable -> Lb2
            com.symantec.b.a.d r3 = com.symantec.b.a.d.SERIAL_NO     // Catch: java.lang.Throwable -> Lb2
            java.lang.Object r0 = r0.get(r3)     // Catch: java.lang.Throwable -> Lb2
            com.symantec.b.a.c r0 = (com.symantec.b.a.c) r0     // Catch: java.lang.Throwable -> Lb2
            boolean r3 = r0.b()     // Catch: java.lang.Throwable -> Lb2
            if (r3 == 0) goto L19
            r0 = r2
        L17:
            monitor-exit(r9)
            return r0
        L19:
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Throwable -> Lb2
            r4 = 9
            if (r3 < r4) goto L83
            java.lang.String r3 = "android.os.Build"
            java.lang.Class r3 = java.lang.Class.forName(r3)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.String r4 = "SERIAL"
            java.lang.reflect.Field r3 = r3.getDeclaredField(r4)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            if (r3 == 0) goto L36
            java.lang.String r1 = r3.toString()     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
        L36:
            r3 = r1
        L37:
            if (r3 != 0) goto L9e
            java.lang.String r1 = "android.os.SystemProperties"
            java.lang.Class r1 = java.lang.Class.forName(r1)     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            java.lang.String r4 = "get"
            r5 = 1
            java.lang.Class[] r5 = new java.lang.Class[r5]     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            r6 = 0
            java.lang.Class<java.lang.String> r7 = java.lang.String.class
            r5[r6] = r7     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            java.lang.reflect.Method r4 = r1.getMethod(r4, r5)     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            r6 = 0
            java.lang.String r7 = "ro.serialno"
            r5[r6] = r7     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            java.lang.Object r1 = r4.invoke(r1, r5)     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
            java.lang.String r1 = (java.lang.String) r1     // Catch: java.lang.Exception -> L85 java.lang.Throwable -> Lb2
        L5b:
            boolean r3 = android.text.TextUtils.isEmpty(r1)     // Catch: java.lang.Throwable -> Lb2
            if (r8 != r3) goto La0
            java.lang.String r0 = "FingerprintManager"
            java.lang.String r1 = "empty SERIAL_NO"
            android.util.Log.e(r0, r1)     // Catch: java.lang.Throwable -> Lb2
            r0 = r2
            goto L17
        L6a:
            r3 = move-exception
            java.lang.String r4 = "FingerprintManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r6 = "Unable to get SERIAL via reflection on android.os.Build: "
            r5.<init>(r6)     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r3 = r3.getMessage()     // Catch: java.lang.Throwable -> Lb2
            java.lang.StringBuilder r3 = r5.append(r3)     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> Lb2
            android.util.Log.d(r4, r3)     // Catch: java.lang.Throwable -> Lb2
        L83:
            r3 = r1
            goto L37
        L85:
            r1 = move-exception
            java.lang.String r4 = "FingerprintManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r6 = "Unable to get ro.serialno via SystemProperties reflection: "
            r5.<init>(r6)     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r1 = r1.getMessage()     // Catch: java.lang.Throwable -> Lb2
            java.lang.StringBuilder r1 = r5.append(r1)     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> Lb2
            android.util.Log.e(r4, r1)     // Catch: java.lang.Throwable -> Lb2
        L9e:
            r1 = r3
            goto L5b
        La0:
            r0.a(r1)     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r1 = "FingerprintManager"
            java.lang.String r2 = r0.toString()     // Catch: java.lang.Throwable -> Lb2
            android.util.Log.d(r1, r2)     // Catch: java.lang.Throwable -> Lb2
            boolean r0 = r0.b()     // Catch: java.lang.Throwable -> Lb2
            goto L17
        Lb2:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.symantec.b.a.e.h():boolean");
    }

    public synchronized boolean i() {
        boolean b2;
        c cVar = this.d.a.get(d.PHONE_ID);
        if (cVar.b()) {
            b2 = false;
        } else if (!this.c.getPackageManager().hasSystemFeature("android.hardware.telephony")) {
            cVar.a(4);
            Log.d("FingerprintManager", "FEATURE_TELEPHONY not found");
            b2 = false;
        } else if (-1 == this.c.getPackageManager().checkPermission("android.permission.READ_PHONE_STATE", this.c.getPackageName())) {
            cVar.a(6);
            Log.d("FingerprintManager", "READ_PHONE_STATE permission not held");
            b2 = false;
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) this.c.getSystemService("phone");
            if (telephonyManager == null) {
                cVar.a(7);
                Log.e("FingerprintManager", "telephony manager is null");
                b2 = false;
            } else {
                String deviceId = telephonyManager.getDeviceId();
                if (true == TextUtils.isEmpty(deviceId)) {
                    Log.e("FingerprintManager", "empty PHONE_ID");
                    cVar.a(2);
                    if (this.h == null) {
                        this.h = new j(this);
                        this.h.start();
                    }
                } else {
                    cVar.a(deviceId);
                }
                Log.d("FingerprintManager", cVar.toString());
                b2 = cVar.b();
            }
        }
        return b2;
    }

    public synchronized boolean j() {
        boolean b2;
        synchronized (this) {
            c cVar = this.d.a.get(d.WIFI_MAC);
            if (cVar.b()) {
                b2 = false;
            } else if (Integer.parseInt(Build.VERSION.SDK) >= 8 && !this.c.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
                cVar.a(4);
                Log.d("FingerprintManager", "FEATURE_WIFI not found");
                b2 = false;
            } else if (-1 == this.c.getPackageManager().checkPermission("android.permission.ACCESS_WIFI_STATE", this.c.getPackageName())) {
                cVar.a(6);
                Log.d("FingerprintManager", "ACCESS_WIFI_STATE permission not held");
                b2 = false;
            } else {
                WifiManager wifiManager = (WifiManager) this.c.getSystemService("wifi");
                if (wifiManager == null) {
                    cVar.a(7);
                    Log.e("FingerprintManager", "wifi manager is null");
                    b2 = false;
                } else {
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo == null) {
                        cVar.a(wifiManager.isWifiEnabled() ? 2 : 5);
                        Log.d("FingerprintManager", "wifi info is null");
                        e();
                        b2 = false;
                    } else {
                        String macAddress = connectionInfo.getMacAddress();
                        if (true == TextUtils.isEmpty(macAddress)) {
                            Log.e("FingerprintManager", "empty WIFI_MAC");
                            cVar.a(wifiManager.isWifiEnabled() ? 2 : 5);
                            e();
                        } else {
                            cVar.a(macAddress);
                        }
                        Log.d("FingerprintManager", cVar.toString());
                        b2 = cVar.b();
                    }
                }
            }
        }
        return b2;
    }

    public synchronized boolean k() {
        boolean b2;
        c cVar = this.d.a.get(d.BLUETOOTH_MAC);
        if (cVar.b()) {
            b2 = false;
        } else if (Integer.parseInt(Build.VERSION.SDK) >= 8 && !this.c.getPackageManager().hasSystemFeature("android.hardware.bluetooth")) {
            cVar.a(4);
            Log.d("FingerprintManager", "FEATURE_BLUETOOTH not found");
            b2 = false;
        } else if (-1 == this.c.getPackageManager().checkPermission("android.permission.BLUETOOTH", this.c.getPackageName())) {
            cVar.a(6);
            Log.d("FingerprintManager", "BLUETOOTH permission not held");
            b2 = false;
        } else {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter == null) {
                cVar.a(4);
                Log.e("FingerprintManager", "bluetoothAdapter is null");
                f();
                b2 = false;
            } else {
                String address = defaultAdapter.getAddress();
                if (true == TextUtils.isEmpty(address)) {
                    Log.e("FingerprintManager", "empty BLUETOOTH_MAC");
                    cVar.a(defaultAdapter.isEnabled() ? 2 : 5);
                    f();
                } else {
                    cVar.a(address);
                }
                Log.d("FingerprintManager", cVar.toString());
                b2 = cVar.b();
            }
        }
        return b2;
    }
}
