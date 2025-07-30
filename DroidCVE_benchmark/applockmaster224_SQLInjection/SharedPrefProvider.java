package com.ludashi.superlock.util.pref;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.ludashi.superlock.application.SuperLockApplication;
import com.mbridge.msdk.foundation.download.database.DownloadModel;

/* loaded from: classes3.dex */
public class SharedPrefProvider extends ContentProvider {
    public static final Uri a;

    /* renamed from: b, reason: collision with root package name */
    private static final int f15199b;

    /* renamed from: c, reason: collision with root package name */
    private static String f15200c = null;

    /* renamed from: d, reason: collision with root package name */
    private static String f15201d = null;

    /* renamed from: e, reason: collision with root package name */
    private static String f15202e = null;

    /* renamed from: f, reason: collision with root package name */
    private static String f15203f = null;

    /* renamed from: g, reason: collision with root package name */
    private static final int f15204g = 1;

    /* renamed from: h, reason: collision with root package name */
    private static final int f15205h = 2;

    /* renamed from: i, reason: collision with root package name */
    private static final int f15206i = 3;

    /* renamed from: j, reason: collision with root package name */
    private static final int f15207j = 4;

    /* renamed from: k, reason: collision with root package name */
    private static final int f15208k = 5;

    static {
        Uri parse = Uri.parse("content://com.ludashi.superlock.main.SharedPrefProvider");
        a = parse;
        f15199b = parse.toString().length() + 1;
        f15200c = com.ludashi.framework.utils.d0.b.f12405d;
        f15201d = "key";
        f15202e = "value";
        f15203f = DownloadModel.FILE_NAME;
    }

    private static ContentResolver a() {
        return SuperLockApplication.context().getContentResolver();
    }

    public static void b(String str, String str2, String str3) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 4);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, str2);
        contentValues.put(f15203f, str3);
        try {
            a().update(a, contentValues, null, null);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public Uri insert(Uri uri, ContentValues contentValues) {
        int intValue = contentValues.getAsInteger(f15200c).intValue();
        String str = "";
        if (intValue == 1) {
            str = "" + c.a(contentValues.getAsString(f15201d), contentValues.getAsBoolean(f15202e).booleanValue(), contentValues.getAsString(f15203f));
        } else if (intValue == 4) {
            str = "" + c.a(contentValues.getAsString(f15201d), contentValues.getAsString(f15202e), contentValues.getAsString(f15203f));
        } else if (intValue == 2) {
            str = "" + c.a(contentValues.getAsString(f15201d), contentValues.getAsInteger(f15202e).intValue(), contentValues.getAsString(f15203f));
        } else if (intValue == 3) {
            str = "" + c.a(contentValues.getAsString(f15201d), contentValues.getAsLong(f15202e).longValue(), contentValues.getAsString(f15203f));
        } else if (intValue == 5) {
            str = "" + c.a(contentValues.getAsString(f15201d), contentValues.getAsFloat(f15202e).floatValue(), contentValues.getAsString(f15203f));
        }
        return Uri.parse(a.toString() + "/" + str);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int intValue = contentValues.getAsInteger(f15200c).intValue();
        if (intValue == 1) {
            c.b(contentValues.getAsString(f15201d), contentValues.getAsBoolean(f15202e).booleanValue(), contentValues.getAsString(f15203f));
        } else if (intValue == 4) {
            c.b(contentValues.getAsString(f15201d), contentValues.getAsString(f15202e), contentValues.getAsString(f15203f));
        } else if (intValue == 2) {
            c.b(contentValues.getAsString(f15201d), contentValues.getAsInteger(f15202e).intValue(), contentValues.getAsString(f15203f));
        } else if (intValue == 3) {
            c.b(contentValues.getAsString(f15201d), contentValues.getAsLong(f15202e).longValue(), contentValues.getAsString(f15203f));
        } else if (intValue == 5) {
            c.b(contentValues.getAsString(f15201d), contentValues.getAsFloat(f15202e).floatValue(), contentValues.getAsString(f15203f));
        }
        return 1;
    }

    public static String a(String str, String str2, String str3) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 4);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, str2);
        contentValues.put(f15203f, str3);
        try {
            if (a() == null) {
                return str2;
            }
            Uri insert = a().insert(a, contentValues);
            return insert == null ? str2 : String.valueOf(insert.toString().substring(f15199b));
        } catch (Exception unused) {
            return str2;
        }
    }

    public static void b(String str, boolean z, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 1);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Boolean.valueOf(z));
        contentValues.put(f15203f, str2);
        try {
            a().update(a, contentValues, null, null);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static boolean a(String str, boolean z, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 1);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Boolean.valueOf(z));
        contentValues.put(f15203f, str2);
        try {
            Uri insert = a().insert(a, contentValues);
            return insert == null ? z : Boolean.valueOf(insert.toString().substring(f15199b)).booleanValue();
        } catch (Throwable unused) {
            return z;
        }
    }

    public static void b(String str, int i2, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 2);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Integer.valueOf(i2));
        contentValues.put(f15203f, str2);
        try {
            a().update(a, contentValues, null, null);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static int a(String str, int i2, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 2);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Integer.valueOf(i2));
        contentValues.put(f15203f, str2);
        try {
            Uri insert = a().insert(a, contentValues);
            return insert == null ? i2 : Integer.valueOf(insert.toString().substring(f15199b)).intValue();
        } catch (Exception unused) {
            return i2;
        }
    }

    public static void b(String str, long j2, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 3);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Long.valueOf(j2));
        contentValues.put(f15203f, str2);
        try {
            a().update(a, contentValues, null, null);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static long a(String str, long j2, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 3);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Long.valueOf(j2));
        contentValues.put(f15203f, str2);
        try {
            Uri insert = a().insert(a, contentValues);
            if (insert == null) {
                return j2;
            }
            String uri = insert.toString();
            return (!TextUtils.isEmpty(uri) && uri.length() > f15199b) ? Long.valueOf(insert.toString().substring(f15199b)).longValue() : j2;
        } catch (Exception unused) {
            return j2;
        }
    }

    public static void b(String str, float f2, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 5);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Float.valueOf(f2));
        contentValues.put(f15203f, str2);
        try {
            a().update(a, contentValues, null, null);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static float a(String str, float f2, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(f15200c, (Integer) 5);
        contentValues.put(f15201d, str);
        contentValues.put(f15202e, Float.valueOf(f2));
        contentValues.put(f15203f, str2);
        try {
            Uri insert = a().insert(a, contentValues);
            if (insert == null) {
                return f2;
            }
            String uri = insert.toString();
            return (!TextUtils.isEmpty(uri) && uri.length() > f15199b) ? Float.valueOf(insert.toString().substring(f15199b)).floatValue() : f2;
        } catch (Exception unused) {
            return f2;
        }
    }
}
