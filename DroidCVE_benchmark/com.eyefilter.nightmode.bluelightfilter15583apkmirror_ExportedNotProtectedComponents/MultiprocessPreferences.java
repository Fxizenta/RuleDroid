package com.eyefilter.nightmode.bluelightfilter.utils;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class MultiprocessPreferences extends ContentProvider {

    /* renamed from: a, reason: collision with root package name */
    public static Uri f3381a;

    /* renamed from: b, reason: collision with root package name */
    public static UriMatcher f3382b;

    public static class b implements SharedPreferences.Editor {

        /* renamed from: a, reason: collision with root package name */
        public Context f3383a;

        /* renamed from: b, reason: collision with root package name */
        public ContentValues f3384b = new ContentValues();

        public b(Context context, a aVar) {
            this.f3383a = context;
        }

        @Override // android.content.SharedPreferences.Editor
        public void apply() {
            try {
                this.f3383a.getContentResolver().insert(MultiprocessPreferences.a(this.f3383a, "key", "type"), this.f3384b);
            } catch (Exception e7) {
                e7.printStackTrace();
            }
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor clear() {
            this.f3383a.getContentResolver().delete(MultiprocessPreferences.a(this.f3383a, "key", "type"), null, null);
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public boolean commit() {
            apply();
            return true;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putBoolean(String str, boolean z10) {
            this.f3384b.put(str, Boolean.valueOf(z10));
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putFloat(String str, float f6) {
            this.f3384b.put(str, Float.valueOf(f6));
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putInt(String str, int i10) {
            this.f3384b.put(str, Integer.valueOf(i10));
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putLong(String str, long j10) {
            this.f3384b.put(str, Long.valueOf(j10));
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putString(String str, String str2) {
            this.f3384b.put(str, str2);
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putStringSet(String str, Set set) {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor remove(String str) {
            this.f3384b.putNull(str);
            return this;
        }
    }

    public static class c implements SharedPreferences {

        /* renamed from: a, reason: collision with root package name */
        public Context f3385a;

        public c(Context context, a aVar) {
            this.f3385a = context;
        }

        @Override // android.content.SharedPreferences
        public boolean contains(String str) {
            return true;
        }

        @Override // android.content.SharedPreferences
        public SharedPreferences.Editor edit() {
            return new b(this.f3385a, null);
        }

        @Override // android.content.SharedPreferences
        public Map getAll() {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override // android.content.SharedPreferences
        public boolean getBoolean(String str, boolean z10) {
            try {
                Cursor query = this.f3385a.getContentResolver().query(MultiprocessPreferences.a(this.f3385a, str, "boolean"), null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            z10 = query.getInt(0) > 0;
                        }
                    } catch (Exception e7) {
                        e = e7;
                        z10 = false;
                    }
                    try {
                        query.close();
                    } catch (Exception e10) {
                        e = e10;
                        e.printStackTrace();
                        return Boolean.valueOf(z10).booleanValue();
                    }
                }
                return Boolean.valueOf(z10).booleanValue();
            } catch (Exception e11) {
                e11.printStackTrace();
                return false;
            }
        }

        @Override // android.content.SharedPreferences
        public float getFloat(String str, float f6) {
            try {
                Cursor query = this.f3385a.getContentResolver().query(MultiprocessPreferences.a(this.f3385a, str, "float"), null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            f6 = query.getFloat(0);
                        }
                        query.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                return f6;
            } catch (Exception e10) {
                e10.printStackTrace();
                return 0.0f;
            }
        }

        @Override // android.content.SharedPreferences
        public int getInt(String str, int i10) {
            try {
                Cursor query = this.f3385a.getContentResolver().query(MultiprocessPreferences.a(this.f3385a, str, "integer"), null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            i10 = query.getInt(0);
                        }
                        query.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                return i10;
            } catch (Exception e10) {
                e10.printStackTrace();
                return 0;
            }
        }

        @Override // android.content.SharedPreferences
        public long getLong(String str, long j10) {
            try {
                Cursor query = this.f3385a.getContentResolver().query(MultiprocessPreferences.a(this.f3385a, str, "long"), null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            j10 = query.getLong(0);
                        }
                        query.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                return j10;
            } catch (Exception e10) {
                e10.printStackTrace();
                return 0L;
            }
        }

        @Override // android.content.SharedPreferences
        public String getString(String str, String str2) {
            try {
                Cursor query = this.f3385a.getContentResolver().query(MultiprocessPreferences.a(this.f3385a, str, "string"), null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            str2 = query.getString(0);
                        }
                        query.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                return str2;
            } catch (Exception e10) {
                e10.printStackTrace();
                return "";
            }
        }

        @Override // android.content.SharedPreferences
        public Set getStringSet(String str, Set set) {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override // android.content.SharedPreferences
        public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override // android.content.SharedPreferences
        public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
            throw new IllegalArgumentException("Not implemented");
        }
    }

    public static Uri a(Context context, String str, String str2) {
        if (f3381a == null) {
            b();
        }
        return f3381a.buildUpon().appendPath(str).appendPath(str2).build();
    }

    public static void b() {
        UriMatcher uriMatcher = new UriMatcher(-1);
        f3382b = uriMatcher;
        uriMatcher.addURI("com.eyefilter.nightmode.bluelightfilter.PREFFERENCE_AUTHORITY", "*/*", 65536);
        f3381a = Uri.parse("content://com.eyefilter.nightmode.bluelightfilter.PREFFERENCE_AUTHORITY");
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        if (f3382b.match(uri) == 65536) {
            PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit().clear().commit();
            return 0;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd.com.eyefilter.nightmode.bluelightfilter.PREFFERENCE_AUTHORITY.item";
    }

    @Override // android.content.ContentProvider
    @SuppressLint({"NewApi"})
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (f3382b.match(uri) != 65536) {
            throw new IllegalArgumentException("Unsupported uri " + uri);
        }
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
        for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value == null) {
                edit.remove(key);
            } else if (value instanceof String) {
                edit.putString(key, (String) value);
            } else if (value instanceof Boolean) {
                edit.putBoolean(key, ((Boolean) value).booleanValue());
            } else if (value instanceof Long) {
                edit.putLong(key, ((Long) value).longValue());
            } else if (value instanceof Integer) {
                edit.putInt(key, ((Integer) value).intValue());
            } else {
                if (!(value instanceof Float)) {
                    throw new IllegalArgumentException("Unsupported type " + uri);
                }
                edit.putFloat(key, ((Float) value).floatValue());
            }
        }
        edit.apply();
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (f3382b != null) {
            return true;
        }
        getContext();
        b();
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SharedPreferences defaultSharedPreferences;
        Object valueOf;
        if (f3382b.match(uri) != 65536) {
            throw new IllegalArgumentException("Unsupported uri " + uri);
        }
        String str3 = uri.getPathSegments().get(0);
        String str4 = uri.getPathSegments().get(1);
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{str3});
        try {
            defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        } catch (Exception e7) {
            e7.printStackTrace();
        }
        if (!defaultSharedPreferences.contains(str3)) {
            return matrixCursor;
        }
        MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
        if ("string".equals(str4)) {
            valueOf = defaultSharedPreferences.getString(str3, null);
        } else if ("boolean".equals(str4)) {
            valueOf = Integer.valueOf(defaultSharedPreferences.getBoolean(str3, false) ? 1 : 0);
        } else if ("long".equals(str4)) {
            valueOf = Long.valueOf(defaultSharedPreferences.getLong(str3, 0L));
        } else if ("integer".equals(str4)) {
            valueOf = Integer.valueOf(defaultSharedPreferences.getInt(str3, 0));
        } else {
            if (!"float".equals(str4)) {
                throw new IllegalArgumentException("Unsupported type " + uri);
            }
            valueOf = Float.valueOf(defaultSharedPreferences.getFloat(str3, 0.0f));
        }
        newRow.add(valueOf);
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }
}
