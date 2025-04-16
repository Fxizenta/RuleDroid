package com.vectorform.wattsonandroid.b;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class l extends SQLiteOpenHelper {

    /* renamed from: a, reason: collision with root package name */
    private static l f1329a = null;

    private l(Context context) {
        super(context, "favorites.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public static synchronized l a(Context context) {
        l lVar;
        synchronized (l.class) {
            if (f1329a == null) {
                f1329a = new l(context.getApplicationContext());
            }
            lVar = f1329a;
        }
        return lVar;
    }

    public final ArrayList<com.vectorform.wattsonandroid.tools.productcompare.t> a() {
        ArrayList<com.vectorform.wattsonandroid.tools.productcompare.t> arrayList;
        Cursor cursor = null;
        try {
            Cursor rawQuery = getReadableDatabase().rawQuery("select * from Favorites", null);
            if (rawQuery.getCount() > 0) {
                rawQuery.moveToFirst();
                arrayList = new ArrayList<>();
                while (!rawQuery.isAfterLast()) {
                    com.vectorform.wattsonandroid.tools.productcompare.t tVar = new com.vectorform.wattsonandroid.tools.productcompare.t();
                    tVar.f2258a = Integer.parseInt(rawQuery.getString(1));
                    tVar.f2259b = rawQuery.getString(2);
                    tVar.f2260c = rawQuery.getString(3);
                    tVar.f2261d = rawQuery.getString(4);
                    tVar.e = rawQuery.getString(5);
                    tVar.f = false;
                    arrayList.add(tVar);
                    rawQuery.moveToNext();
                }
                if (rawQuery != null) {
                    rawQuery.close();
                }
            } else {
                arrayList = new ArrayList<>();
                if (rawQuery != null) {
                    rawQuery.close();
                }
            }
            return arrayList;
        } catch (Throwable th) {
            if (0 != 0) {
                cursor.close();
            }
            throw th;
        }
    }

    public final boolean a(int i) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("select pd_id from Favorites where pd_id ='" + i + "'", null);
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table Favorites(_id integer primary key autoincrement, pd_id text not null,brandName text not null, modelName text, modelNumber text not null, category text not null);");
        sQLiteDatabase.execSQL("create table Attributes(_id integer primary key autoincrement, parent text not null,info text not null, name text,value text not null);");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS Favorites");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS Attributes");
        onCreate(sQLiteDatabase);
    }
}
