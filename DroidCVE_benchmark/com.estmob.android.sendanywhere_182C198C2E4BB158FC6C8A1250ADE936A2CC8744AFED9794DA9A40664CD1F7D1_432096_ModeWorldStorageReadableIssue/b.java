package com.estmob.paprika.transfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.estmob.paprika.transfer.c;
import com.facebook.AccessToken;

/* compiled from: AuthTokenDefaultValue.java */
/* loaded from: classes.dex */
public final class b extends c {
    private Context k;

    public b(Context context) {
        this.k = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("sendanywhere_device", 0);
        String string = sharedPreferences.getString("device_id", null);
        String string2 = sharedPreferences.getString("device_password", null);
        String string3 = sharedPreferences.getString(AccessToken.USER_ID_KEY, null);
        String string4 = sharedPreferences.getString("user_password", null);
        String string5 = sharedPreferences.getString("login_provider", null);
        String string6 = sharedPreferences.getString("user_token", null);
        super.a(string, string2);
        if (!TextUtils.isEmpty(string6)) {
            if ("google".equals(string5)) {
                super.a(string3, c.a.GOOGLE, string6);
                return;
            } else {
                if ("facebook".equals(string5)) {
                    super.a(string3, c.a.FACEBOOK, string6);
                    return;
                }
                return;
            }
        }
        super.b(string3, string4);
    }

    @Override // com.estmob.paprika.transfer.c
    public final void a(String str, String str2) {
        super.a(str, str2);
        SharedPreferences.Editor edit = this.k.getSharedPreferences("sendanywhere_device", 0).edit();
        edit.putString("device_id", str);
        edit.putString("device_password", str2);
        edit.commit();
    }

    @Override // com.estmob.paprika.transfer.c
    public final void b(String str, String str2) {
        super.b(str, str2);
        SharedPreferences.Editor edit = this.k.getSharedPreferences("sendanywhere_device", 0).edit();
        edit.putString(AccessToken.USER_ID_KEY, str);
        edit.putString("user_password", str2);
        edit.commit();
    }

    @Override // com.estmob.paprika.transfer.c
    public final void a(String str, c.a aVar, String str2) {
        super.a(str, aVar, str2);
        SharedPreferences.Editor edit = this.k.getSharedPreferences("sendanywhere_device", 0).edit();
        switch (aVar) {
            case GOOGLE:
                edit.putString("login_provider", "google");
                break;
            case FACEBOOK:
                edit.putString("login_provider", "facebook");
                break;
        }
        edit.putString(AccessToken.USER_ID_KEY, str);
        edit.putString("user_token", str2);
        edit.commit();
    }
}
