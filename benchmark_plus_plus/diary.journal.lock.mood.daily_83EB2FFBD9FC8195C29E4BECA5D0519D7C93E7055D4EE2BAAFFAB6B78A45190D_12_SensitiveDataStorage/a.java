package o9;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes.dex */
public class a extends SQLiteOpenHelper {
    public a(Context context) {
        super(context, "App", (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE user (_id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,uid INTEGER DEFAULT 0,username TEXT,avatar TEXT,password TEXT,pwd_type INTEGER DEFAULT 0,email TEXT,setting TEXT,temp1 TEXT,temp2 TEXT,temp3 TEXT)");
        sQLiteDatabase.execSQL("CREATE TABLE diary (_id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,uid INTEGER DEFAULT 0,date INTEGER DEFAULT 0,title TEXT,content TEXT,data TEXT,status TEXT,temp1 TEXT,temp2 TEXT,temp3 TEXT)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i10, int i11) {
    }
}
