package com.cmcm.adsdk.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import com.cleanmaster.antitheft.commonlib.AntiTheftConfigManager;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.Const;
import com.cmcm.utils.e;
import com.cmcm.utils.g;
import com.mobvista.msdk.base.utils.CommonMD5;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class d {

    /* renamed from: a, reason: collision with root package name */
    private static SharedPreferences f1019a;
    private static d g = null;
    private static int h = -1;
    private static int i = -1;
    private SharedPreferences.Editor f;
    private a j;

    /* renamed from: b, reason: collision with root package name */
    private final String f1020b = "cmcmadsdk_config";
    private Context e = CMAdManager.getContext();

    /* renamed from: d, reason: collision with root package name */
    private String f1022d = com.cmcm.utils.a.a.c().a();

    /* renamed from: c, reason: collision with root package name */
    private String f1021c = CMAdManager.getMid();

    public static d a() {
        if (g == null) {
            g = new d();
        }
        return g;
    }

    private d() {
        f1019a = this.e.getSharedPreferences("cmcmadsdk_config", 0);
        this.f = f1019a.edit();
    }

    public void b() {
        g.b(new Runnable() { // from class: com.cmcm.adsdk.config.d.1
            @Override // java.lang.Runnable
            public void run() {
                d.this.c();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        if (!CMAdManager.isRequestUfs()) {
            com.cmcm.utils.d.b("RequestUFS", "request error, please turn on switch");
            return;
        }
        if (!com.cmcm.utils.b.d(this.e)) {
            com.cmcm.utils.d.b("RequestUFS", "network is unavailable");
            e();
            return;
        }
        if (TextUtils.isEmpty(this.f1022d)) {
            com.cmcm.utils.d.b("RequestUFS", "gaid is null, get gaid again");
            this.f1022d = com.cmcm.utils.a.a.c().a();
            if (TextUtils.isEmpty(this.f1022d)) {
                com.cmcm.utils.d.b("RequestUFS", "gaid is null, cannot request ufs");
                return;
            }
        }
        Long valueOf = Long.valueOf(f1019a.getLong("ufs_request_time", 0L));
        com.cmcm.utils.d.b("RequestUFS", "requestufs lasttime = " + valueOf);
        if (System.currentTimeMillis() - valueOf.longValue() > 86400000) {
            this.f.putLong("ufs_request_time", System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= 9) {
                this.f.apply();
            } else {
                this.f.commit();
            }
            e.a(Const.CONFIG_URL_UFS, a(this.f1021c, com.cmcm.utils.a.a(), this.f1022d), new e.b() { // from class: com.cmcm.adsdk.config.d.2
                @Override // com.cmcm.utils.e.b
                public void a(int i2, HashMap<String, String> hashMap, InputStream inputStream, String str, int i3) {
                    byte[] bArr = new byte[0];
                    try {
                        byte[] a2 = d.this.a(inputStream);
                        if (a2 != null && a2.length >= 0) {
                            d.this.b(d.this.a(a2));
                            d.this.d();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override // com.cmcm.utils.e.b
                public void a(int i2, com.cmcm.adsdk.b bVar) {
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] a(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String a(byte[] bArr) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec("2ba42a014f0c8e92".getBytes(), "AES");
            byte[] bArr2 = new byte[16];
            int length = bArr2.length;
            for (int i2 = 0; i2 < length; i2++) {
                bArr2[i2] = 0;
            }
            cipher.init(2, secretKeySpec, new IvParameterSpec(bArr2));
            String str = new String(cipher.doFinal(bArr));
            com.cmcm.utils.d.a("RequestUFS", "resultJson=" + str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String a(String str, String str2, String str3) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("c=sdk");
        stringBuffer.append("&gaid=" + str3);
        stringBuffer.append("&mid=" + str);
        stringBuffer.append("&androidid=" + str2);
        stringBuffer.append("&sig=" + a(stringBuffer.toString() + AntiTheftConfigManager.locationSeparator + "26f65c14a3df9c62"));
        return stringBuffer.toString();
    }

    private String a(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(CommonMD5.TAG);
            messageDigest.update(str.getBytes(), 0, str.length());
            return new BigInteger(1, messageDigest.digest()).toString(16).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(String str) {
        com.cmcm.utils.d.a("RequestUFS", "saveUFSInfo=" + str);
        try {
            JSONObject jSONObject = new JSONObject(str);
            h = jSONObject.optInt("age");
            i = jSONObject.optInt("gender");
            JSONArray jSONArray = jSONObject.getJSONArray("interests");
            this.f.putInt("age", h);
            this.f.putInt("gender", i);
            this.f.putString("interests", jSONArray.toString());
            if (Build.VERSION.SDK_INT >= 9) {
                this.f.apply();
            } else {
                this.f.commit();
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class a extends BroadcastReceiver {
        private a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (com.cmcm.utils.b.d(d.this.e)) {
                com.cmcm.utils.d.b("RequestUFS", "network changed : request again");
                d.this.b();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d() {
        try {
            if (this.e != null && this.j != null) {
                this.e.getApplicationContext().unregisterReceiver(this.j);
            }
        } catch (Exception e) {
        }
    }

    private void e() {
        try {
            if (this.e != null) {
                IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                this.j = new a();
                this.e.getApplicationContext().registerReceiver(this.j, intentFilter);
            }
        } catch (Exception e) {
        }
    }
}
