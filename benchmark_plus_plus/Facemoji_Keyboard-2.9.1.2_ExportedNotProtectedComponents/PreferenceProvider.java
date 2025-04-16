package com.preff.kb.dpreference;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;
import androidx.b.a;
import com.appsflyer.share.Constants;
import com.preff.kb.BaseLib;
import java.util.List;
import java.util.Map;

/* compiled from: Proguard */
/* loaded from: classes.dex */
public class PreferenceProvider extends ContentProvider {
    public static final int CACHE_STRING = 302;
    public static final int CONTAIN_KEY = 100;
    public static final int GET_ALL = 200;
    private static String[] PREFERENCE_COLUMNS = null;
    public static final int PREF_BOOLEAN = 1;
    public static final int PREF_FLOAT = 5;
    public static final int PREF_INT = 3;
    public static final String PREF_KEY = "key";
    public static final int PREF_LONG = 4;
    public static final int PREF_STRING = 2;
    public static final String PREF_VALUE = "value";
    public static PreferenceProvider instance;
    private static Map<String, IPrefImpl> sPreferences;
    private static final UriMatcher sUriMatcher;
    private static final String AUTHORITY = BaseLib.getInstance().getApplicationContext().getPackageName() + ".dprefrenceprovider";
    public static final String CONTENT_PREF_BOOLEAN_URI = "content://" + AUTHORITY + "/boolean/";
    public static final String CONTENT_PREF_STRING_URI = "content://" + AUTHORITY + "/string/";
    public static final String CONTENT_PREF_INT_URI = "content://" + AUTHORITY + "/integer/";
    public static final String CONTENT_PREF_LONG_URI = "content://" + AUTHORITY + "/long/";
    public static final String CONTENT_PREF_FLOAT_URI = "content://" + AUTHORITY + "/float/";
    public static final String CONTENT_PREF_CONTAINKEY_URI = "content://" + AUTHORITY + "/containkey/";
    public static final String CONTENT_PREF_GETALL_URI = "content://" + AUTHORITY + "/getall/";
    public static final String CONTENT_CACHE_STRING_URI = "content://" + AUTHORITY + "/cachestring/";

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sUriMatcher = uriMatcher;
        uriMatcher.addURI(AUTHORITY, "boolean/*/*", 1);
        sUriMatcher.addURI(AUTHORITY, "string/*/*", 2);
        sUriMatcher.addURI(AUTHORITY, "integer/*/*", 3);
        sUriMatcher.addURI(AUTHORITY, "long/*/*", 4);
        sUriMatcher.addURI(AUTHORITY, "float/*/*", 5);
        sUriMatcher.addURI(AUTHORITY, "containkey/*/*", 100);
        sUriMatcher.addURI(AUTHORITY, "getall/*/*", 200);
        sUriMatcher.addURI(AUTHORITY, "cachestring/*/*", CACHE_STRING);
        PREFERENCE_COLUMNS = new String[]{"value"};
        sPreferences = new a();
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        instance = this;
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String cacheString;
        PrefModel prefModelByUri = getPrefModelByUri(uri);
        int match = sUriMatcher.match(uri);
        if (match == 1) {
            if (getDPreference(prefModelByUri.getName()).hasKey(prefModelByUri.getKey())) {
                return preferenceToCursor(Integer.valueOf(getDPreference(prefModelByUri.getName()).getPrefBoolean(prefModelByUri.getKey(), false) ? 1 : 0));
            }
            return null;
        }
        if (match == 2) {
            if (getDPreference(prefModelByUri.getName()).hasKey(prefModelByUri.getKey())) {
                return preferenceToCursor(getDPreference(prefModelByUri.getName()).getPrefString(prefModelByUri.getKey(), ""));
            }
            return null;
        }
        if (match == 3) {
            if (getDPreference(prefModelByUri.getName()).hasKey(prefModelByUri.getKey())) {
                return preferenceToCursor(Integer.valueOf(getDPreference(prefModelByUri.getName()).getPrefInt(prefModelByUri.getKey(), -1)));
            }
            return null;
        }
        if (match == 4) {
            if (getDPreference(prefModelByUri.getName()).hasKey(prefModelByUri.getKey())) {
                return preferenceToCursor(Long.valueOf(getDPreference(prefModelByUri.getName()).getPrefLong(prefModelByUri.getKey(), -1L)));
            }
            return null;
        }
        if (match == 5) {
            if (getDPreference(prefModelByUri.getName()).hasKey(prefModelByUri.getKey())) {
                return preferenceToCursor(Float.valueOf(getDPreference(prefModelByUri.getName()).getPrefFloat(prefModelByUri.getKey(), -1.0f)));
            }
            return null;
        }
        if (match == 100) {
            return preferenceToCursor(Integer.valueOf(getDPreference(prefModelByUri.getName()).hasKey(prefModelByUri.getKey()) ? 1 : 0));
        }
        if (match == 200) {
            return getAll(prefModelByUri.getName());
        }
        if (match == 302 && (cacheString = getDPreference(prefModelByUri.getName()).getCacheString(prefModelByUri.getKey(), null)) != null) {
            return preferenceToCursor(cacheString);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new IllegalStateException("insert unsupport!!!");
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int match = sUriMatcher.match(uri);
        if (match == 1 || match == 2 || match == 3 || match == 4 || match == 5 || match == 302) {
            PrefModel prefModelByUri = getPrefModelByUri(uri);
            if (prefModelByUri == null) {
                return 0;
            }
            getDPreference(prefModelByUri.getName()).removePreference(prefModelByUri.getKey());
            return 0;
        }
        throw new IllegalStateException(" unsupported uri : " + uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        PrefModel prefModelByUri = getPrefModelByUri(uri);
        if (prefModelByUri == null) {
            throw new IllegalArgumentException("update prefModel is null");
        }
        int match = sUriMatcher.match(uri);
        if (match == 1) {
            persistBoolean(prefModelByUri.getName(), contentValues);
            return 0;
        }
        if (match == 2) {
            persistString(prefModelByUri.getName(), contentValues);
            return 0;
        }
        if (match == 3) {
            persistInt(prefModelByUri.getName(), contentValues);
            return 0;
        }
        if (match == 4) {
            persistLong(prefModelByUri.getName(), contentValues);
            return 0;
        }
        if (match == 5) {
            persistFloat(prefModelByUri.getName(), contentValues);
            return 0;
        }
        if (match == 302) {
            persistCacheString(prefModelByUri.getName(), contentValues);
            return 0;
        }
        throw new IllegalStateException("update unsupported uri : " + uri);
    }

    private <T> MatrixCursor preferenceToCursor(T t) {
        MatrixCursor matrixCursor = new MatrixCursor(PREFERENCE_COLUMNS, 1);
        matrixCursor.newRow().add(t);
        return matrixCursor;
    }

    private MatrixCursor getAll(String str) {
        Map<String, Object> all = getDPreference(str).getAll();
        if (all.size() == 0) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"key", "value"}, all.size());
        for (Map.Entry<String, Object> entry : all.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
                newRow.add(key);
                newRow.add(value);
            }
        }
        matrixCursor.moveToFirst();
        String str2 = "";
        while (matrixCursor.moveToNext()) {
            str2 = str2 + matrixCursor.getString(matrixCursor.getColumnIndex("key")) + "||";
        }
        return matrixCursor;
    }

    private void persistInt(String str, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        getDPreference(str).setPrefInt(contentValues.getAsString("key"), contentValues.getAsInteger("value").intValue());
    }

    private void persistBoolean(String str, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        getDPreference(str).setPrefBoolean(contentValues.getAsString("key"), contentValues.getAsBoolean("value").booleanValue());
    }

    private void persistLong(String str, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        getDPreference(str).setPrefLong(contentValues.getAsString("key"), contentValues.getAsLong("value").longValue());
    }

    private void persistFloat(String str, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        getDPreference(str).setPrefFloat(contentValues.getAsString("key"), contentValues.getAsFloat("value").floatValue());
    }

    private void persistString(String str, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        getDPreference(str).setPrefString(contentValues.getAsString("key"), contentValues.getAsString("value"));
    }

    private void persistCacheString(String str, ContentValues contentValues) {
        if (contentValues == null) {
            throw new IllegalArgumentException(" values is null!!!");
        }
        getDPreference(str).setCacheString(contentValues.getAsString("key"), contentValues.getAsString("value"));
    }

    public IPrefImpl getDPreference(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("getDPreference name is null!!!");
        }
        if (sPreferences.get(str) == null) {
            synchronized (this) {
                if (sPreferences.get(str) == null) {
                    sPreferences.put(str, new PreferenceImpl(getContext(), str));
                }
            }
        }
        return sPreferences.get(str);
    }

    private PrefModel getPrefModelByUri(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("getPrefModelByUri uri is wrong : " + uri);
        }
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments == null || pathSegments.size() != 3) {
            throw new IllegalArgumentException("getPrefModelByUri segments size wrong : " + pathSegments);
        }
        return new PrefModel(pathSegments.get(1), pathSegments.get(2));
    }

    public static Uri buildUri(String str, String str2, int i) {
        return Uri.parse(getUriByType(i) + str + Constants.URL_PATH_DELIMITER + str2);
    }

    private static String getUriByType(int i) {
        if (i == 1) {
            return CONTENT_PREF_BOOLEAN_URI;
        }
        if (i == 2) {
            return CONTENT_PREF_STRING_URI;
        }
        if (i == 3) {
            return CONTENT_PREF_INT_URI;
        }
        if (i == 4) {
            return CONTENT_PREF_LONG_URI;
        }
        if (i == 5) {
            return CONTENT_PREF_FLOAT_URI;
        }
        if (i == 100) {
            return CONTENT_PREF_CONTAINKEY_URI;
        }
        if (i == 200) {
            return CONTENT_PREF_GETALL_URI;
        }
        if (i == 302) {
            return CONTENT_CACHE_STRING_URI;
        }
        throw new IllegalStateException("unsupport preftype : " + i);
    }

    /* compiled from: Proguard */
    private static class PrefModel {
        String key;
        String name;

        public PrefModel(String str, String str2) {
            this.name = str;
            this.key = str2;
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }
    }
}
