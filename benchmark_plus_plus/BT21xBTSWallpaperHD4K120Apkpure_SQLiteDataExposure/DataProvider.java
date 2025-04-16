package com.zayviusdigital.wallpaper_json.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import com.zayviusdigital.wallpaper_json.db.DatabaseContract;
import com.zayviusdigital.wallpaper_json.db.DatabaseHelper;
import com.zayviusdigital.wallpaper_json.db.Helper;
import java.util.Objects;

/* loaded from: classes2.dex */
public class DataProvider extends ContentProvider {
    private static final int N = 1;
    private static final int N_ID = 2;
    private static final UriMatcher sUriMatcher;
    private Helper helper;

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sUriMatcher = uriMatcher;
        uriMatcher.addURI("com.bungaakp007.bt21wallpaperoffline130920", DatabaseHelper.TABLE_NAME, 1);
        uriMatcher.addURI("com.bungaakp007.bt21wallpaperoffline130920", "wall/#", 2);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Helper helper = Helper.getInstance(getContext());
        this.helper = helper;
        helper.open();
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        int match = sUriMatcher.match(uri);
        if (match == 1) {
            return this.helper.queryAll();
        }
        if (match != 2) {
            return null;
        }
        return this.helper.queryById(uri.getLastPathSegment());
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        long insert = sUriMatcher.match(uri) == 1 ? this.helper.insert(contentValues) : 0L;
        if (Build.VERSION.SDK_INT >= 19) {
            Context context = getContext();
            Objects.requireNonNull(context);
            context.getContentResolver().notifyChange(DatabaseContract.NoteColumns.CONTENT_URI, null);
        }
        return Uri.parse(DatabaseContract.NoteColumns.CONTENT_URI + "/" + insert);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int update = sUriMatcher.match(uri) == 2 ? this.helper.update(uri.getLastPathSegment(), contentValues) : 0;
        if (Build.VERSION.SDK_INT >= 19) {
            Context context = getContext();
            Objects.requireNonNull(context);
            context.getContentResolver().notifyChange(DatabaseContract.NoteColumns.CONTENT_URI, null);
        }
        return update;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int deleteById = sUriMatcher.match(uri) == 2 ? this.helper.deleteById(uri.getLastPathSegment()) : 0;
        if (Build.VERSION.SDK_INT >= 19) {
            Context context = getContext();
            Objects.requireNonNull(context);
            context.getContentResolver().notifyChange(DatabaseContract.NoteColumns.CONTENT_URI, null);
        }
        return deleteById;
    }
}
