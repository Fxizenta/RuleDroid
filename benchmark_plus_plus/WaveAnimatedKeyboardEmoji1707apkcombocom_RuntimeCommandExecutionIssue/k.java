package com.wave.keyboard.inputmethod.dictionarypack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.ServerProtocol;
import com.facebook.share.internal.ShareConstants;
import com.wave.keyboard.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import net.pubnative.library.request.PubnativeRequest;

/* compiled from: MetadataDbHelper.java */
/* loaded from: classes3.dex */
public class k extends SQLiteOpenHelper {

    /* renamed from: c, reason: collision with root package name */
    private static final String f16189c = k.class.getSimpleName();

    /* renamed from: d, reason: collision with root package name */
    static final String[] f16190d = {"pendingid", "type", AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS, "id", PubnativeRequest.Parameters.LOCALE, "description", "filename", "url", "date", "checksum", "filesize", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, "formatversion", "flags"};

    /* renamed from: e, reason: collision with root package name */
    static final String[] f16191e = {"clientid", ShareConstants.MEDIA_URI, "pendingid", "flags"};

    /* renamed from: f, reason: collision with root package name */
    static final String[] f16192f = {AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS, "id", PubnativeRequest.Parameters.LOCALE, "description", "date", "filesize", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION};

    /* renamed from: g, reason: collision with root package name */
    private static TreeMap<String, k> f16193g = null;
    private final Context a;
    private final String b;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private k(android.content.Context r4, java.lang.String r5) {
        /*
            r3 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "pendingUpdates"
            r0.append(r1)
            boolean r1 = android.text.TextUtils.isEmpty(r5)
            if (r1 == 0) goto L13
            java.lang.String r1 = ""
            goto L24
        L13:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "."
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
        L24:
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 0
            r2 = 6
            r3.<init>(r4, r0, r1, r2)
            r3.a = r4
            r3.b = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.wave.keyboard.inputmethod.dictionarypack.k.<init>(android.content.Context, java.lang.String):void");
    }

    public static ArrayList<g> C(Context context, long j) {
        SQLiteDatabase y = y(context, "");
        ArrayList<g> arrayList = new ArrayList<>();
        Cursor query = y.query("clients", f16191e, null, null, null, null, null);
        try {
            if (!query.moveToFirst()) {
                return arrayList;
            }
            int columnIndex = query.getColumnIndex("clientid");
            int columnIndex2 = query.getColumnIndex("pendingid");
            do {
                long j2 = query.getInt(columnIndex2);
                String string = query.getString(columnIndex);
                if (j2 == j) {
                    arrayList.add(new g(string, null));
                }
                ContentValues q = q(y(context, string), j);
                if (q != null) {
                    arrayList.add(new g(string, q));
                }
            } while (query.moveToNext());
            return arrayList;
        } finally {
            query.close();
        }
    }

    private static ContentValues D(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return null;
        }
        ContentValues contentValues = new ContentValues(13);
        k0(contentValues, cursor, "pendingid");
        k0(contentValues, cursor, "type");
        k0(contentValues, cursor, AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS);
        l0(contentValues, cursor, "id");
        l0(contentValues, cursor, PubnativeRequest.Parameters.LOCALE);
        l0(contentValues, cursor, "description");
        l0(contentValues, cursor, "filename");
        l0(contentValues, cursor, "url");
        k0(contentValues, cursor, "date");
        l0(contentValues, cursor, "checksum");
        k0(contentValues, cursor, "filesize");
        k0(contentValues, cursor, ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION);
        k0(contentValues, cursor, "formatversion");
        k0(contentValues, cursor, "flags");
        cursor.moveToNext();
        return contentValues;
    }

    public static ContentValues F(SQLiteDatabase sQLiteDatabase, String str) {
        Cursor query = sQLiteDatabase.query("pendingUpdates", f16190d, "id=? AND (status=? OR status=?)", new String[]{str, Integer.toString(3), Integer.toString(5)}, null, null, null);
        ContentValues D = D(query);
        query.close();
        return D;
    }

    public static synchronized k J(Context context, String str) {
        k kVar;
        synchronized (k.class) {
            if (str == null) {
                str = "";
            }
            if (f16193g == null) {
                f16193g = new TreeMap<>();
            }
            kVar = f16193g.get(str);
            if (kVar == null) {
                kVar = new k(context, str);
                f16193g.put(str, kVar);
            }
        }
        return kVar;
    }

    public static long M(Context context, String str) {
        SQLiteDatabase y = y(context, null);
        String[] strArr = {"lastupdate"};
        String[] strArr2 = new String[1];
        if (str == null) {
            str = "";
        }
        strArr2[0] = str;
        Cursor query = y.query("clients", strArr, "clientid = ?", strArr2, null, null, null, null);
        try {
            if (!query.moveToFirst()) {
                return 0L;
            }
            return query.getLong(0);
        } finally {
            query.close();
        }
    }

    public static long T(Context context, String str) {
        Cursor query = y(context, null).query("clients", new String[]{"pendingid"}, "uri = ?", new String[]{str}, null, null, null, null);
        try {
            return !query.moveToFirst() ? -1L : query.getInt(0);
        } finally {
            query.close();
        }
    }

    public static String X(Context context, String str) {
        Cursor query = y(context, null).query("clients", new String[]{ShareConstants.MEDIA_URI, "additionalid"}, "clientid = ?", new String[]{str}, null, null, null, null);
        try {
            if (!query.moveToFirst()) {
                return null;
            }
            String string = query.getString(0);
            n.a(context, string, query.getString(1));
            return string;
        } finally {
            query.close();
        }
    }

    public static long a0(Context context) {
        Cursor query = y(context, null).query("clients", new String[]{"lastupdate"}, null, null, null, null, null);
        try {
            if (!query.moveToFirst()) {
                return 0L;
            }
            long j = Long.MAX_VALUE;
            do {
                j = Math.min(query.getLong(0), j);
            } while (query.moveToNext());
            return j;
        } finally {
            query.close();
        }
    }

    public static boolean b0(Context context, String str) {
        return X(context, str) != null;
    }

    public static ContentValues c(ContentValues contentValues) throws BadFormatException {
        if (contentValues.get("id") != null && contentValues.get(PubnativeRequest.Parameters.LOCALE) != null) {
            if (contentValues.get("pendingid") == null) {
                contentValues.put("pendingid", (Integer) 0);
            }
            if (contentValues.get("type") == null) {
                contentValues.put("type", (Integer) 2);
            }
            if (contentValues.get(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS) == null) {
                contentValues.put(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS, (Integer) 3);
            }
            if (contentValues.get("description") == null) {
                contentValues.put("description", "");
            }
            if (contentValues.get("filename") == null) {
                contentValues.put("filename", "_");
            }
            if (contentValues.get("url") == null) {
                contentValues.put("url", "");
            }
            if (contentValues.get("date") == null) {
                contentValues.put("date", (Integer) 0);
            }
            if (contentValues.get("checksum") == null) {
                contentValues.put("checksum", "");
            }
            if (contentValues.get("filesize") == null) {
                contentValues.put("filesize", (Integer) 0);
            }
            if (contentValues.get(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION) == null) {
                contentValues.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, (Integer) 1);
            }
            if (contentValues.get("formatversion") == null) {
                contentValues.put("formatversion", (Integer) 2);
            }
            if (contentValues.get("flags") == null) {
                contentValues.put("flags", (Integer) 0);
            }
            return contentValues;
        }
        throw new BadFormatException();
    }

    public static ContentValues c0(int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, long j, String str6, long j2, int i5, int i6) {
        ContentValues contentValues = new ContentValues(13);
        contentValues.put("pendingid", Integer.valueOf(i2));
        contentValues.put("type", Integer.valueOf(i3));
        contentValues.put("id", str);
        contentValues.put(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS, Integer.valueOf(i4));
        contentValues.put(PubnativeRequest.Parameters.LOCALE, str2);
        contentValues.put("description", str3);
        contentValues.put("filename", str4);
        contentValues.put("url", str5);
        contentValues.put("date", Long.valueOf(j));
        contentValues.put("checksum", str6);
        contentValues.put("filesize", Long.valueOf(j2));
        contentValues.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, Integer.valueOf(i5));
        contentValues.put("formatversion", Integer.valueOf(i6));
        contentValues.put("flags", (Integer) 0);
        return contentValues;
    }

    private void d(SQLiteDatabase sQLiteDatabase) {
        if (TextUtils.isEmpty(this.b)) {
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS clients (clientid TEXT, uri TEXT, additionalid TEXT, lastupdate INTEGER NOT NULL DEFAULT 0, pendingid INTEGER, flags INTEGER, PRIMARY KEY (clientid));");
            String string = this.a.getString(R.string.default_metadata_uri);
            String string2 = this.a.getString(R.string.dictionary_pack_metadata_uri);
            if (!TextUtils.isEmpty(string)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("clientid", "");
                contentValues.put(ShareConstants.MEDIA_URI, string);
                contentValues.put("pendingid", (Integer) (-1));
                sQLiteDatabase.insert("clients", null, contentValues);
            }
            if (TextUtils.isEmpty(string2)) {
                return;
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("clientid", this.a.getString(R.string.dictionary_pack_client_id));
            contentValues2.put(ShareConstants.MEDIA_URI, string2);
            contentValues2.put("pendingid", (Integer) (-1));
            sQLiteDatabase.insert("clients", null, contentValues2);
        }
    }

    private static void d0(SQLiteDatabase sQLiteDatabase, String str, int i2, int i3, long j) {
        ContentValues t = t(sQLiteDatabase, str, i2);
        t.put(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS, Integer.valueOf(i3));
        if (-1 != j) {
            t.put("pendingid", Long.valueOf(j));
        }
        sQLiteDatabase.update("pendingUpdates", t, "id = ? AND version = ?", new String[]{str, Integer.toString(i2)});
    }

    public static void e0(SQLiteDatabase sQLiteDatabase, String str, int i2) {
        d0(sQLiteDatabase, str, i2, 1, -1L);
    }

    public static boolean f(Context context, String str) {
        SQLiteDatabase y = y(context, str);
        y.execSQL("DROP TABLE IF EXISTS pendingUpdates");
        y.execSQL("CREATE TABLE pendingUpdates (pendingid INTEGER, type INTEGER, status INTEGER, id TEXT, locale TEXT, description TEXT, filename TEXT, url TEXT, date INTEGER, checksum TEXT, filesize INTEGER, version INTEGER,formatversion INTEGER,flags INTEGER,PRIMARY KEY (id,version));");
        return y(context, "").delete("clients", "clientid = ?", new String[]{str}) != 0;
    }

    public static void f0(SQLiteDatabase sQLiteDatabase, String str, int i2) {
        d0(sQLiteDatabase, str, i2, 1, -1L);
    }

    public static void g0(SQLiteDatabase sQLiteDatabase, String str, int i2) {
        d0(sQLiteDatabase, str, i2, 4, -1L);
    }

    public static void h0(SQLiteDatabase sQLiteDatabase, String str, int i2, long j) {
        d0(sQLiteDatabase, str, i2, 2, j);
    }

    public static void i(SQLiteDatabase sQLiteDatabase, long j) {
        sQLiteDatabase.delete("pendingUpdates", "pendingid = ? AND status = ?", new String[]{Long.toString(j), Integer.toString(2)});
    }

    public static void i0(SQLiteDatabase sQLiteDatabase, String str, int i2) {
        d0(sQLiteDatabase, str, i2, 3, -1L);
    }

    public static void j0(SQLiteDatabase sQLiteDatabase, ContentValues contentValues) {
        if (contentValues.getAsInteger("type").intValue() != 2) {
            return;
        }
        com.wave.keyboard.inputmethod.latin.utils.j.c("Ended processing a wordlist");
        LinkedList linkedList = new LinkedList();
        Cursor query = sQLiteDatabase.query("pendingUpdates", new String[]{"filename"}, "locale = ? AND id = ? AND status = ?", new String[]{contentValues.getAsString(PubnativeRequest.Parameters.LOCALE), contentValues.getAsString("id"), Integer.toString(3)}, null, null, null);
        try {
            if (query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("filename");
                do {
                    com.wave.keyboard.inputmethod.latin.utils.j.c("Setting for removal", query.getString(columnIndex));
                    linkedList.add(query.getString(columnIndex));
                } while (query.moveToNext());
            }
            query.close();
            contentValues.put(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS, (Integer) 3);
            sQLiteDatabase.beginTransactionNonExclusive();
            sQLiteDatabase.delete("pendingUpdates", "id = ?", new String[]{contentValues.getAsString("id")});
            sQLiteDatabase.insert("pendingUpdates", null, contentValues);
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            Iterator it = linkedList.iterator();
            while (it.hasNext()) {
                try {
                    new File((String) it.next()).delete();
                } catch (SecurityException unused) {
                }
            }
        } catch (Throwable th) {
            query.close();
            throw th;
        }
    }

    private static void k0(ContentValues contentValues, Cursor cursor, String str) {
        contentValues.put(str, Integer.valueOf(cursor.getInt(cursor.getColumnIndex(str))));
    }

    private static void l0(ContentValues contentValues, Cursor cursor, String str) {
        contentValues.put(str, cursor.getString(cursor.getColumnIndex(str)));
    }

    public static Cursor m0(Context context) {
        return y(context, null).query("clients", new String[]{"clientid"}, null, null, null, null, null);
    }

    public static Cursor n0(Context context, String str) {
        return y(context, str).query("pendingUpdates", f16190d, null, null, null, null, PubnativeRequest.Parameters.LOCALE);
    }

    public static void o(SQLiteDatabase sQLiteDatabase, String str, int i2) {
        sQLiteDatabase.delete("pendingUpdates", "id = ? AND version = ?", new String[]{str, Integer.toString(i2)});
    }

    public static Cursor o0(Context context, String str) {
        return y(context, str).query("pendingUpdates", f16192f, "locale != ?", new String[]{""}, null, null, PubnativeRequest.Parameters.LOCALE);
    }

    public static Cursor p0(Context context, String str) {
        return y(context, str).query("pendingUpdates", f16190d, "status = ? OR status = ? OR status = ?", new String[]{Integer.toString(3), Integer.toString(5), Integer.toString(1)}, null, null, PubnativeRequest.Parameters.LOCALE);
    }

    public static ContentValues q(SQLiteDatabase sQLiteDatabase, long j) {
        Cursor query = sQLiteDatabase.query("pendingUpdates", f16190d, "pendingid= ?", new String[]{Long.toString(j)}, null, null, null);
        ContentValues D = D(query);
        query.close();
        return D;
    }

    public static void q0(Context context, String str, long j) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pendingid", Long.valueOf(j));
        SQLiteDatabase y = y(context, "");
        Cursor m0 = m0(context);
        if (m0 == null) {
            return;
        }
        try {
            if (!m0.moveToFirst()) {
                return;
            }
            do {
                String string = m0.getString(0);
                if (X(context, string).equals(str)) {
                    y.update("clients", contentValues, "clientid = ?", new String[]{string});
                }
            } while (m0.moveToNext());
        } finally {
            m0.close();
        }
    }

    public static void r0(Context context, String str) {
        o.a("Save last update time of URI : " + str + " " + System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put("lastupdate", Long.valueOf(System.currentTimeMillis()));
        SQLiteDatabase y = y(context, null);
        Cursor m0 = m0(context);
        if (m0 == null) {
            return;
        }
        try {
            if (!m0.moveToFirst()) {
                return;
            }
            do {
                String string = m0.getString(0);
                if (X(context, string).contains(str.substring(7))) {
                    y.update("clients", contentValues, "clientid = ?", new String[]{string});
                }
            } while (m0.moveToNext());
        } finally {
            m0.close();
        }
    }

    public static void s0(Context context, String str, ContentValues contentValues) {
        String asString = contentValues.getAsString("clientid");
        String asString2 = contentValues.getAsString(ShareConstants.MEDIA_URI);
        String asString3 = contentValues.getAsString("additionalid");
        if (!TextUtils.isEmpty(asString) && asString2 != null && asString3 != null) {
            if (!str.equals(asString)) {
                com.wave.keyboard.inputmethod.latin.utils.j.c("Received an updateClientInfo request for ", str, " but the values contain a different ID : ", asString);
                return;
            }
            contentValues.put("pendingid", (Integer) (-1));
            SQLiteDatabase y = y(context, "");
            if (-1 == y.insert("clients", null, contentValues)) {
                y.update("clients", contentValues, "clientid = ?", new String[]{str});
                return;
            }
            return;
        }
        com.wave.keyboard.inputmethod.latin.utils.j.c("Missing parameter for updateClientInfo");
    }

    public static ContentValues t(SQLiteDatabase sQLiteDatabase, String str, int i2) {
        Cursor query = sQLiteDatabase.query("pendingUpdates", f16190d, "id= ? AND version= ?", new String[]{str, Integer.toString(i2)}, null, null, null);
        ContentValues D = D(query);
        query.close();
        return D;
    }

    public static ContentValues v(SQLiteDatabase sQLiteDatabase, String str) {
        Cursor query = sQLiteDatabase.query("pendingUpdates", f16190d, "id= ?", new String[]{str}, null, null, "version DESC", "1");
        ContentValues D = D(query);
        query.close();
        return D;
    }

    public static SQLiteDatabase y(Context context, String str) {
        return J(context, str).getWritableDatabase();
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE pendingUpdates (pendingid INTEGER, type INTEGER, status INTEGER, id TEXT, locale TEXT, description TEXT, filename TEXT, url TEXT, date INTEGER, checksum TEXT, filesize INTEGER, version INTEGER,formatversion INTEGER,flags INTEGER,PRIMARY KEY (id,version));");
        d(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        if (i2 <= i3) {
            String str = "onDowngrade database but new version is higher? " + i2 + " <= " + i3;
        }
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS pendingUpdates");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS clients");
        onCreate(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        if (3 == i2 && 6 == i3) {
            if (TextUtils.isEmpty(this.b)) {
                d(sQLiteDatabase);
            }
        } else {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS pendingUpdates");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS clients");
            onCreate(sQLiteDatabase);
        }
    }
}
