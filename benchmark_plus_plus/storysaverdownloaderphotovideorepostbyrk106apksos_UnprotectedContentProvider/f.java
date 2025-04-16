package com.bytedance.sdk.openadsdk.multipro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.bytedance.sdk.component.utils.j;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* compiled from: TTProviderManager.java */
/* loaded from: classes.dex */
public class f implements a {
    private static volatile f a;
    private static WeakReference<Context> b;
    private static List<a> c;

    private boolean c(Uri uri) {
        return true;
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public String a() {
        return "";
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public void b() {
    }

    static {
        List<a> synchronizedList = Collections.synchronizedList(new ArrayList());
        c = synchronizedList;
        synchronizedList.add(new com.bytedance.sdk.openadsdk.multipro.d.c());
        c.add(new com.bytedance.sdk.openadsdk.multipro.a.b());
        c.add(new com.bytedance.sdk.openadsdk.multipro.c.b());
        c.add(new com.bytedance.sdk.openadsdk.multipro.c.a());
        Iterator<a> it = c.iterator();
        while (it.hasNext()) {
            it.next().b();
        }
    }

    public static f b(Context context) {
        if (context != null) {
            b = new WeakReference<>(context.getApplicationContext());
        }
        if (a == null) {
            synchronized (f.class) {
                if (a == null) {
                    a = new f();
                }
            }
        }
        return a;
    }

    private f() {
    }

    private a b(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (!c(uri)) {
            j.b("TTProviderManager", "uri is error1");
            return null;
        }
        String[] split = uri.getPath().split("/");
        if (split.length < 2) {
            j.b("TTProviderManager", "uri is error2");
            return null;
        }
        String str = split[1];
        if (TextUtils.isEmpty(str)) {
            j.b("TTProviderManager", "uri is error3");
            return null;
        }
        for (a aVar : c) {
            if (str.equals(aVar.a())) {
                return aVar;
            }
        }
        j.b("TTProviderManager", "uri is error4");
        return null;
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public void a(Context context) {
        Iterator<a> it = c.iterator();
        while (it.hasNext()) {
            it.next().a(context);
        }
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public Cursor a(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        try {
            a b2 = b(uri);
            if (b2 != null) {
                return b2.a(uri, strArr, str, strArr2, str2);
            }
            return null;
        } catch (Throwable th) {
            j.a("TTProviderManager", "==provider query error==", th);
            return null;
        }
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public String a(Uri uri) {
        try {
            a b2 = b(uri);
            if (b2 != null) {
                return b2.a(uri);
            }
            return null;
        } catch (Throwable th) {
            j.a("TTProviderManager", "==provider getType error==", th);
            return null;
        }
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public Uri a(Uri uri, ContentValues contentValues) {
        try {
            a b2 = b(uri);
            if (b2 != null) {
                return b2.a(uri, contentValues);
            }
            return null;
        } catch (Throwable th) {
            j.a("TTProviderManager", "==provider insert error==", th);
            return null;
        }
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public int a(Uri uri, String str, String[] strArr) {
        try {
            a b2 = b(uri);
            if (b2 != null) {
                return b2.a(uri, str, strArr);
            }
            return 0;
        } catch (Throwable th) {
            j.a("TTProviderManager", "==provider delete error==", th);
            return 0;
        }
    }

    @Override // com.bytedance.sdk.openadsdk.multipro.a
    public int a(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        try {
            a b2 = b(uri);
            if (b2 != null) {
                return b2.a(uri, contentValues, str, strArr);
            }
            return 0;
        } catch (Throwable th) {
            j.a("TTProviderManager", "==provider update error==", th);
            return 0;
        }
    }
}
