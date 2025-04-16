package ij;

import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import jp.co.canon.bsd.ad.sdk.a.b.d;
import jp.co.canon.bsd.ad.sdk.core.c.c;

/* loaded from: classes.dex */
public class IJPrinterCapabilityProvider extends ContentProvider {

    /* renamed from: a, reason: collision with root package name */
    a f1029a = null;

    /* renamed from: b, reason: collision with root package name */
    SQLiteDatabase f1030b = null;

    /* renamed from: c, reason: collision with root package name */
    SQLiteDatabase f1031c = null;

    /* loaded from: classes.dex */
    public static class a extends SQLiteOpenHelper {
        a(Context context) {
            super(context, "canon.IjCsPrinterCapabilityData", (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE capability (" + d.a._ID.r + " INTEGER PRIMARY KEY," + d.a.VERSION.r + " INTEGER," + d.a.TIMESTAMP.r + " INTEGER," + d.a.MACADDRESS.r + " TEXT," + d.a.DEVICEID.r + " TEXT," + d.a.ISSETTINGBYAPMODE.r + " INTEGER," + d.a.CONNECTIONTYPE.r + " INTEGER," + d.a.NICKNAME.r + " TEXT," + d.a.PROTOCOLSEARCHING.r + " INTEGER," + d.a.PROTOCOLGETTINGSTATUS.r + " INTEGER," + d.a.PROTOCOLPRINTING.r + " INTEGER," + d.a.PROTOCOLSCANNING.r + " INTEGER," + d.a.XMLCAPPRINT.r + " TEXT," + d.a.XMLCONFPRINT.r + " TEXT," + d.a.XMLCAPDEVICE.r + " TEXT," + d.a.XMLCONFDEVICE.r + " TEXT," + d.a.SCANNERTYPE.r + " INTEGER);");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS capability");
            onCreate(sQLiteDatabase);
        }
    }

    public static ContentValues a(c cVar) {
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put(d.a._ID.r, (Integer) 0);
        contentValues.put(d.a.VERSION.r, (Integer) 1);
        contentValues.put(d.a.TIMESTAMP.r, Long.valueOf(System.currentTimeMillis()));
        contentValues.put(d.a.MACADDRESS.r, cVar.a());
        contentValues.put(d.a.DEVICEID.r, cVar.e);
        contentValues.put(d.a.ISSETTINGBYAPMODE.r, Integer.valueOf(cVar.u()));
        contentValues.put(d.a.CONNECTIONTYPE.r, Integer.valueOf(cVar.n()));
        contentValues.put(d.a.NICKNAME.r, cVar.d);
        contentValues.put(d.a.PROTOCOLSEARCHING.r, Integer.valueOf(cVar.e()));
        contentValues.put(d.a.PROTOCOLGETTINGSTATUS.r, Integer.valueOf(cVar.f()));
        contentValues.put(d.a.PROTOCOLPRINTING.r, Integer.valueOf(cVar.g()));
        contentValues.put(d.a.PROTOCOLSCANNING.r, Integer.valueOf(cVar.h()));
        contentValues.put(d.a.XMLCAPPRINT.r, cVar.i());
        contentValues.put(d.a.XMLCONFPRINT.r, cVar.k());
        contentValues.put(d.a.XMLCAPDEVICE.r, cVar.j());
        contentValues.put(d.a.XMLCONFDEVICE.r, cVar.l());
        if (cVar instanceof jp.co.canon.bsd.ad.sdk.extension.d.c) {
            contentValues.put(d.a.SCANNERTYPE.r, Integer.valueOf(((jp.co.canon.bsd.ad.sdk.extension.d.c) cVar).W));
        } else {
            contentValues.put(d.a.SCANNERTYPE.r, (Integer) 0);
        }
        return contentValues;
    }

    @Override // android.content.ContentProvider
    public int delete(@NonNull Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        String str;
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        int callingPid = Binder.getCallingPid();
        if (getContext() != null && (runningAppProcesses = ((ActivityManager) getContext().getSystemService("activity")).getRunningAppProcesses()) != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo != null && runningAppProcessInfo.pid == callingPid) {
                    str = runningAppProcessInfo.processName;
                    break;
                }
            }
        }
        str = null;
        if (str == null || !str.equals(jp.co.canon.bsd.ad.pixmaprint.application.c.a())) {
            throw new SecurityException();
        }
        int update = this.f1031c.update("capability", contentValues, "_id=" + contentValues.getAsInteger("_id"), null);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        if (update > 0) {
            return ContentUris.withAppendedId(d.f3156a, update);
        }
        long insert = this.f1031c.insert("capability", null, contentValues);
        if (insert <= 0) {
            return null;
        }
        Uri withAppendedId = ContentUris.withAppendedId(d.f3156a, insert);
        if (getContext() == null) {
            return withAppendedId;
        }
        getContext().getContentResolver().notifyChange(withAppendedId, null);
        return withAppendedId;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        try {
            this.f1029a = new a(getContext());
            this.f1030b = this.f1029a.getReadableDatabase();
            this.f1031c = this.f1029a.getWritableDatabase();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // android.content.ContentProvider
    @Nullable
    public Cursor query(@NonNull Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables("capability");
        sQLiteQueryBuilder.setStrict(true);
        HashMap hashMap = new HashMap();
        hashMap.put(d.a._ID.r, d.a._ID.r);
        hashMap.put(d.a.VERSION.r, d.a.VERSION.r);
        hashMap.put(d.a.TIMESTAMP.r, d.a.TIMESTAMP.r);
        hashMap.put(d.a.MACADDRESS.r, d.a.MACADDRESS.r);
        hashMap.put(d.a.DEVICEID.r, d.a.DEVICEID.r);
        hashMap.put(d.a.ISSETTINGBYAPMODE.r, d.a.ISSETTINGBYAPMODE.r);
        hashMap.put(d.a.CONNECTIONTYPE.r, d.a.CONNECTIONTYPE.r);
        hashMap.put(d.a.NICKNAME.r, d.a.NICKNAME.r);
        hashMap.put(d.a.PROTOCOLSEARCHING.r, d.a.PROTOCOLSEARCHING.r);
        hashMap.put(d.a.PROTOCOLGETTINGSTATUS.r, d.a.PROTOCOLGETTINGSTATUS.r);
        hashMap.put(d.a.PROTOCOLPRINTING.r, d.a.PROTOCOLPRINTING.r);
        hashMap.put(d.a.PROTOCOLSCANNING.r, d.a.PROTOCOLSCANNING.r);
        hashMap.put(d.a.XMLCAPPRINT.r, d.a.XMLCAPPRINT.r);
        hashMap.put(d.a.XMLCONFPRINT.r, d.a.XMLCONFPRINT.r);
        hashMap.put(d.a.XMLCAPDEVICE.r, d.a.XMLCAPDEVICE.r);
        hashMap.put(d.a.XMLCONFDEVICE.r, d.a.XMLCONFDEVICE.r);
        hashMap.put(d.a.SCANNERTYPE.r, d.a.SCANNERTYPE.r);
        sQLiteQueryBuilder.setProjectionMap(hashMap);
        return sQLiteQueryBuilder.query(this.f1030b, strArr, str, strArr2, null, null, null);
    }

    @Override // android.content.ContentProvider
    public int update(@NonNull Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
