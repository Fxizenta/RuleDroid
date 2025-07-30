package com.cybozu.kunailite.base.f.a;

import android.content.Context;
import android.content.SharedPreferences;
import com.cybozu.kunailite.common.exception.KunaiException;
import com.cybozu.kunailite.common.p.r;
import com.cybozu.kunailite.common.p.t;
import com.cybozu.kunailite.common.p.w;
import java.util.HashMap;
import java.util.Map;

/* compiled from: RemoteServiceImpl.java */
/* loaded from: classes.dex */
public final class c extends com.cybozu.kunailite.common.k.a.a {
    public c(Context context) {
        super(context);
    }

    public final String a(String str, String str2, Map map) {
        com.cybozu.kunailite.common.m.c.a aVar = new com.cybozu.kunailite.common.m.c.a(this.b, str);
        aVar.e(com.cybozu.kunailite.common.p.f.b(this.b));
        aVar.c(com.cybozu.kunailite.common.p.f.a(this.b));
        aVar.d(com.cybozu.kunailite.common.bean.b.a(this.b).b());
        aVar.c(com.cybozu.kunailite.common.e.e.KUNAI.ordinal());
        return aVar.a(str2, map, this.b, 3);
    }

    public final String a(String str) {
        String a = a(str, com.cybozu.kunailite.common.e.h.SECURITY.name(), (Map) null);
        if (com.cybozu.kunailite.common.p.k.c(this.b, "kunai_log.txt")) {
            a(str, com.cybozu.kunailite.common.e.h.LOG.name(), (Map) null);
            this.b.deleteFile("kunai_log.txt");
        }
        String str2 = "";
        if (!t.a(a) && a.length() > 1) {
            int indexOf = a.indexOf(":");
            str2 = a.substring(0, indexOf);
            String substring = a.substring(indexOf + 1, a.length());
            if (str2.equals("1")) {
                com.cybozu.kunailite.base.i.i.c(this.b);
            } else {
                if ((r.a("log-preferences-name", "02005", 0, this.b) == 1) && a()) {
                    com.cybozu.kunailite.base.i.i.c(this.b);
                } else {
                    com.cybozu.kunailite.base.i.i.b(this.b);
                    com.cybozu.kunailite.base.i.d.a(this.b, Integer.parseInt(substring), Integer.parseInt(substring));
                    SharedPreferences.Editor edit = this.b.getSharedPreferences("kunai_login_info_temp", 0).edit();
                    edit.putString("securityPeriod", substring);
                    edit.putString("securityFlag", str2);
                    edit.commit();
                }
            }
        }
        return str2;
    }

    public final String a(String str, String str2, String str3) {
        HashMap hashMap = new HashMap();
        hashMap.put("accountName", str2);
        hashMap.put("password", str3);
        w.a(this.b, a(str, com.cybozu.kunailite.common.e.h.AUTH.name(), hashMap), str);
        return a(str);
    }

    public final boolean a() {
        try {
            new com.cybozu.kunailite.base.a.a.c(this.b).a();
            return false;
        } catch (KunaiException e) {
            com.cybozu.kunailite.common.j.b.b(e);
            return "02005".equals(e.b());
        }
    }
}
