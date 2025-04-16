package com.zayviusdigital.wallpaper_json.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes2.dex */
public class Helper {
    private static final String DATABASE_TABLE = "wall";
    private static Helper INSTANCE;
    private static final String[] allColum = {"_id", DatabaseHelper.COLUMN_NAME};
    private static DatabaseHelper dataBaseHelper;
    private static SQLiteDatabase database;

    public Helper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static Helper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Helper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    public Cursor queryAll() {
        return database.query("wall", null, null, null, null, null, "_id DESC");
    }

    public Cursor queryById(String str) {
        return database.query("wall", null, "_id = ?", new String[]{str}, null, null, null, null);
    }

    public long insert(ContentValues contentValues) {
        return database.insert("wall", null, contentValues);
    }

    public int update(String str, ContentValues contentValues) {
        return database.update("wall", contentValues, "_id = ?", new String[]{str});
    }

    public int deleteById(String str) {
        return database.delete("wall", "_id = ?", new String[]{str});
    }

    public boolean isFavorite(Integer num) {
        SQLiteDatabase sQLiteDatabase = database;
        String[] strArr = allColum;
        StringBuilder sb = new StringBuilder();
        sb.append("_id =");
        sb.append(num);
        return sQLiteDatabase.query("wall", strArr, sb.toString(), null, null, null, null, null).getCount() > 0;
    }

    public boolean inserta(String str, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", str);
        contentValues.put(DatabaseHelper.COLUMN_NAME, str2);
        return database.insert("wall", null, contentValues) != -1;
    }
}
