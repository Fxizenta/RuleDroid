package com.owncloud.android.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ComponentCallbacks;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import com.owncloud.android.MainApp;
import com.owncloud.android.R;
import com.owncloud.android.data.Executors;
import com.owncloud.android.data.OwncloudDatabase;
import com.owncloud.android.data.capabilities.datasources.implementation.OCLocalCapabilitiesDataSource;
import com.owncloud.android.data.capabilities.db.OCCapabilityEntity;
import com.owncloud.android.data.folderbackup.datasources.FolderBackupLocalDataSource;
import com.owncloud.android.data.migrations.CameraUploadsMigrationToRoom;
import com.owncloud.android.data.preferences.datasources.SharedPreferencesProvider;
import com.owncloud.android.data.sharing.shares.db.OCShareDao;
import com.owncloud.android.data.sharing.shares.db.OCShareEntity;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.datamodel.UploadsStorageManager;
import com.owncloud.android.db.ProviderMeta;
import com.owncloud.android.domain.camerauploads.model.FolderBackUpConfiguration;
import com.owncloud.android.domain.files.MimeTypeConstantsKt;
import com.owncloud.android.extensions.CursorExtKt;
import com.owncloud.android.lib.common.accounts.AccountUtils;
import com.owncloud.android.providers.FileContentProvider;
import com.owncloud.android.utils.FileStorageUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import org.koin.android.ext.android.AndroidKoinScopeExtKt;
import org.koin.core.qualifier.Qualifier;
import timber.log.Timber;

/* compiled from: FileContentProvider.kt */
@Metadata(d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\u0018\u0000 B2\u00020\u0001:\u0002BCB\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J#\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0016¢\u0006\u0002\u0010\u0011J%\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00130\f2\u000e\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\fH\u0002¢\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001e\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001f\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J7\u0010 \u001a\u00020!2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u00132\u000e\u0010%\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\fH\u0002¢\u0006\u0002\u0010&J/\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u00132\u000e\u0010%\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\fH\u0016¢\u0006\u0002\u0010'J\u0012\u0010(\u001a\u0004\u0018\u00010\u00132\u0006\u0010\"\u001a\u00020#H\u0016J\"\u0010)\u001a\u00020#2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020#2\b\u0010*\u001a\u0004\u0018\u00010+H\u0002J\u001c\u0010)\u001a\u0004\u0018\u00010#2\u0006\u0010\"\u001a\u00020#2\b\u0010*\u001a\u0004\u0018\u00010+H\u0016J\b\u0010,\u001a\u00020-H\u0016J\u001a\u0010.\u001a\u0004\u0018\u00010/2\u0006\u0010\"\u001a\u00020#2\u0006\u00100\u001a\u00020\u0013H\u0016J$\u0010.\u001a\u0004\u0018\u00010/2\u0006\u0010\"\u001a\u00020#2\u0006\u00100\u001a\u00020\u00132\b\u00101\u001a\u0004\u0018\u000102H\u0016JI\u00103\u001a\u0002042\u0006\u0010\"\u001a\u00020#2\u000e\u00105\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\f2\b\u00106\u001a\u0004\u0018\u00010\u00132\u000e\u00107\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\f2\b\u00108\u001a\u0004\u0018\u00010\u0013H\u0016¢\u0006\u0002\u00109J\u0010\u0010:\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002JA\u0010;\u001a\u00020!2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020#2\b\u0010*\u001a\u0004\u0018\u00010+2\b\u00106\u001a\u0004\u0018\u00010\u00132\u000e\u00107\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\fH\u0002¢\u0006\u0002\u0010<J9\u0010;\u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\b\u0010*\u001a\u0004\u0018\u00010+2\b\u00106\u001a\u0004\u0018\u00010\u00132\u000e\u00107\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\fH\u0016¢\u0006\u0002\u0010=J\u0010\u0010>\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J \u0010?\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010@\u001a\u00020\u00132\u0006\u0010A\u001a\u00020\u0013H\u0002R\u0012\u0010\u0005\u001a\u00060\u0006R\u00020\u0000X\u0082.¢\u0006\u0002\n\u0000R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082.¢\u0006\u0002\n\u0000¨\u0006D"}, d2 = {"Lcom/owncloud/android/providers/FileContentProvider;", "Landroid/content/ContentProvider;", "executors", "Lcom/owncloud/android/data/Executors;", "(Lcom/owncloud/android/data/Executors;)V", "dbHelper", "Lcom/owncloud/android/providers/FileContentProvider$DataBaseHelper;", "getExecutors", "()Lcom/owncloud/android/data/Executors;", "uriMatcher", "Landroid/content/UriMatcher;", "applyBatch", "", "Landroid/content/ContentProviderResult;", "operations", "Ljava/util/ArrayList;", "Landroid/content/ContentProviderOperation;", "(Ljava/util/ArrayList;)[Landroid/content/ContentProviderResult;", "computeProjection", "", "projectionIn", "([Ljava/lang/String;)[Ljava/lang/String;", "createCameraUploadsSyncTable", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "createCapabilitiesTable", "createFilesTable", "createOCSharesTable", "createUploadsTable", "createUserAvatarsTable", "createUserQuotaTable", "delete", "", "uri", "Landroid/net/Uri;", "where", "whereArgs", "(Landroid/database/sqlite/SQLiteDatabase;Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "getType", "insert", "values", "Landroid/content/ContentValues;", "onCreate", "", "openFile", "Landroid/os/ParcelFileDescriptor;", "mode", "signal", "Landroid/os/CancellationSignal;", "query", "Landroid/database/Cursor;", "projection", "selection", "selectionArgs", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "trimSuccessfulUploads", "update", "(Landroid/database/sqlite/SQLiteDatabase;Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "updateAccountName", "updateDownloadedFiles", "newAccountName", "oldAccountName", "Companion", "DataBaseHelper", "owncloudApp_originalRelease"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class FileContentProvider extends ContentProvider {
    private static final int CAMERA_UPLOADS_SYNC = 7;
    private static final int CAPABILITIES = 5;
    private static final int DIRECTORY = 2;
    private static final String MAX_SUCCESSFUL_UPLOADS = "30";
    private static final int QUOTAS = 8;
    private static final int ROOT_DIRECTORY = 3;
    private static final int SHARES = 4;
    private static final int SINGLE_FILE = 1;
    private static final int UPLOADS = 6;
    private static final HashMap<String, String> cameraUploadSyncProjectionMap;
    private static final HashMap<String, String> capabilityProjectionMap;
    private static final HashMap<String, String> fileProjectionMap;
    private static final HashMap<String, String> quotaProjectionMap;
    private static final HashMap<String, String> shareProjectionMap;
    private static final HashMap<String, String> uploadProjectionMap;
    private DataBaseHelper dbHelper;
    private final Executors executors;
    private UriMatcher uriMatcher;

    /* JADX WARN: Multi-variable type inference failed */
    public FileContentProvider() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public FileContentProvider(Executors executors) {
        Intrinsics.checkNotNullParameter(executors, "executors");
        this.executors = executors;
    }

    public /* synthetic */ FileContentProvider(Executors executors, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? new Executors() : executors);
    }

    public final Executors getExecutors() {
        return this.executors;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String where, String[] whereArgs) {
        ContentResolver contentResolver;
        Intrinsics.checkNotNullParameter(uri, "uri");
        DataBaseHelper dataBaseHelper = this.dbHelper;
        if (dataBaseHelper == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dbHelper");
            dataBaseHelper = null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Intrinsics.checkNotNullExpressionValue(db, "db");
            int delete = delete(db, uri, where, whereArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            Context context = getContext();
            if (context != null && (contentResolver = context.getContentResolver()) != null) {
                contentResolver.notifyChange(uri, null);
            }
            return delete;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    private final int delete(SQLiteDatabase db, Uri uri, String where, String[] whereArgs) {
        String str;
        int delete;
        if (where != null && whereArgs == null) {
            throw new IllegalArgumentException("Selection not allowed, use parameterized queries");
        }
        UriMatcher uriMatcher = this.uriMatcher;
        if (uriMatcher == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher = null;
        }
        String str2 = "";
        switch (uriMatcher.match(uri)) {
            case 1:
                Cursor query = query(uri, null, where, whereArgs, null);
                if (query.moveToFirst()) {
                    str = CursorExtKt.getStringFromColumnOrThrow(query, ProviderMeta.ProviderTableMeta.FILE_REMOTE_ID);
                    query.close();
                } else {
                    str = "";
                }
                Timber.d("Removing FILE " + str, new Object[0]);
                StringBuilder sb = new StringBuilder();
                sb.append("_id=");
                sb.append(uri.getPathSegments().get(1));
                if (!TextUtils.isEmpty(where)) {
                    str2 = " AND (" + where + ')';
                }
                sb.append(str2);
                return db.delete("filelist", sb.toString(), whereArgs);
            case 2:
                Cursor query2 = query(uri, null, null, null, null);
                int i = 0;
                if (query2.moveToFirst()) {
                    while (!query2.isAfterLast()) {
                        long longFromColumnOrThrow = CursorExtKt.getLongFromColumnOrThrow(query2, "_id");
                        if (CollectionsKt.contains(MimeTypeConstantsKt.getLIST_MIME_DIR(), CursorExtKt.getStringFromColumnOrThrow(query2, ProviderMeta.ProviderTableMeta.FILE_CONTENT_TYPE))) {
                            Uri withAppendedId = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_DIR, longFromColumnOrThrow);
                            Intrinsics.checkNotNullExpressionValue(withAppendedId, "withAppendedId(ProviderT…CONTENT_URI_DIR, childId)");
                            delete = delete(db, withAppendedId, null, null);
                        } else {
                            Uri withAppendedId2 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_FILE, longFromColumnOrThrow);
                            Intrinsics.checkNotNullExpressionValue(withAppendedId2, "withAppendedId(ProviderT…ONTENT_URI_FILE, childId)");
                            delete = delete(db, withAppendedId2, null, null);
                        }
                        i += delete;
                        query2.moveToNext();
                    }
                    query2.close();
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("_id=");
                sb2.append(uri.getPathSegments().get(1));
                if (!TextUtils.isEmpty(where)) {
                    str2 = " AND (" + where + ')';
                }
                sb2.append(str2);
                return i + db.delete("filelist", sb2.toString(), whereArgs);
            case 3:
                return db.delete("filelist", where, whereArgs);
            case 4:
                OCShareDao shareDao = OwncloudDatabase.INSTANCE.getDatabase(MainApp.INSTANCE.getAppContext()).shareDao();
                String str3 = uri.getPathSegments().get(1);
                Intrinsics.checkNotNullExpressionValue(str3, "uri.pathSegments[1]");
                return shareDao.deleteShare(str3);
            case 5:
                return db.delete("capabilities", where, whereArgs);
            case 6:
                return db.delete(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME, where, whereArgs);
            case 7:
                return db.delete(ProviderMeta.ProviderTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME, where, whereArgs);
            case 8:
                return db.delete("user_quotas", where, whereArgs);
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        UriMatcher uriMatcher = this.uriMatcher;
        if (uriMatcher == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher = null;
        }
        int match = uriMatcher.match(uri);
        if (match == 1) {
            return ProviderMeta.ProviderTableMeta.CONTENT_TYPE_ITEM;
        }
        if (match == 3) {
            return ProviderMeta.ProviderTableMeta.CONTENT_TYPE;
        }
        throw new IllegalArgumentException("Unknown Uri id." + uri);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        ContentResolver contentResolver;
        Intrinsics.checkNotNullParameter(uri, "uri");
        DataBaseHelper dataBaseHelper = this.dbHelper;
        if (dataBaseHelper == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dbHelper");
            dataBaseHelper = null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Intrinsics.checkNotNullExpressionValue(db, "db");
            Uri insert = insert(db, uri, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            Context context = getContext();
            if (context != null && (contentResolver = context.getContentResolver()) != null) {
                Intrinsics.checkNotNull(insert);
                contentResolver.notifyChange(insert, null);
            }
            return insert;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    private final Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        UriMatcher uriMatcher = this.uriMatcher;
        if (uriMatcher == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher = null;
        }
        switch (uriMatcher.match(uri)) {
            case 1:
            case 3:
                String asString = values != null ? values.getAsString("path") : null;
                String asString2 = values != null ? values.getAsString(ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER) : null;
                String[] strArr = {"_id", "path", ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER};
                String[] strArr2 = new String[0];
                ArrayList arrayList = new ArrayList();
                if (asString != null) {
                    arrayList.add(asString);
                }
                if (asString2 != null) {
                    arrayList.add(asString2);
                }
                arrayList.toArray(strArr2);
                Cursor query = query(uri, strArr, "path=? AND file_owner=?", strArr2, null);
                if (!query.moveToFirst()) {
                    query.close();
                    long insert = db.insert("filelist", null, values);
                    if (insert <= 0) {
                        throw new SQLException("ERROR " + uri);
                    }
                    Uri withAppendedId = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_FILE, insert);
                    Intrinsics.checkNotNullExpressionValue(withAppendedId, "{\n                    do…fileId)\n                }");
                    return withAppendedId;
                }
                Uri withAppendedId2 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_FILE, CursorExtKt.getLongFromColumnOrThrow(query, "_id"));
                Intrinsics.checkNotNullExpressionValue(withAppendedId2, "withAppendedId(\n        …ID)\n                    )");
                query.close();
                return withAppendedId2;
            case 2:
            default:
                throw new IllegalArgumentException("Unknown uri id: " + uri);
            case 4:
                long insert2 = values != null ? OwncloudDatabase.INSTANCE.getDatabase(MainApp.INSTANCE.getAppContext()).shareDao().insert(OCShareEntity.INSTANCE.fromContentValues(values)) : 0L;
                if (insert2 <= 0) {
                    throw new SQLException("ERROR " + uri);
                }
                Uri withAppendedId3 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_SHARE, insert2);
                Intrinsics.checkNotNullExpressionValue(withAppendedId3, "withAppendedId(ProviderT…NTENT_URI_SHARE, shareId)");
                return withAppendedId3;
            case 5:
                long insert3 = db.insert("capabilities", null, values);
                if (insert3 <= 0) {
                    throw new SQLException("ERROR " + uri);
                }
                Uri withAppendedId4 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_CAPABILITIES, insert3);
                Intrinsics.checkNotNullExpressionValue(withAppendedId4, "withAppendedId(ProviderT…PABILITIES, capabilityId)");
                return withAppendedId4;
            case 6:
                long insert4 = db.insert(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME, null, values);
                if (insert4 <= 0) {
                    throw new SQLException("ERROR " + uri);
                }
                trimSuccessfulUploads(db);
                Uri withAppendedId5 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_UPLOADS, insert4);
                Intrinsics.checkNotNullExpressionValue(withAppendedId5, "withAppendedId(ProviderT…NT_URI_UPLOADS, uploadId)");
                return withAppendedId5;
            case 7:
                long insert5 = db.insert(ProviderMeta.ProviderTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME, null, values);
                if (insert5 <= 0) {
                    throw new SQLException("ERROR " + uri);
                }
                Uri withAppendedId6 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_CAMERA_UPLOADS_SYNC, insert5);
                Intrinsics.checkNotNullExpressionValue(withAppendedId6, "withAppendedId(ProviderT…ADS_SYNC, cameraUploadId)");
                return withAppendedId6;
            case 8:
                long insert6 = db.insert("user_quotas", null, values);
                if (insert6 <= 0) {
                    throw new SQLException("ERROR " + uri);
                }
                Uri withAppendedId7 = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_QUOTAS, insert6);
                Intrinsics.checkNotNullExpressionValue(withAppendedId7, "withAppendedId(ProviderT…TENT_URI_QUOTAS, quotaId)");
                return withAppendedId7;
        }
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Resources resources;
        this.dbHelper = new DataBaseHelper(getContext());
        Context context = getContext();
        UriMatcher uriMatcher = null;
        String string = (context == null || (resources = context.getResources()) == null) ? null : resources.getString(R.string.authority);
        UriMatcher uriMatcher2 = new UriMatcher(-1);
        this.uriMatcher = uriMatcher2;
        uriMatcher2.addURI(string, null, 3);
        UriMatcher uriMatcher3 = this.uriMatcher;
        if (uriMatcher3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher3 = null;
        }
        uriMatcher3.addURI(string, "file/", 1);
        UriMatcher uriMatcher4 = this.uriMatcher;
        if (uriMatcher4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher4 = null;
        }
        uriMatcher4.addURI(string, "file/#", 1);
        UriMatcher uriMatcher5 = this.uriMatcher;
        if (uriMatcher5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher5 = null;
        }
        uriMatcher5.addURI(string, "dir/", 2);
        UriMatcher uriMatcher6 = this.uriMatcher;
        if (uriMatcher6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher6 = null;
        }
        uriMatcher6.addURI(string, "dir/#", 2);
        UriMatcher uriMatcher7 = this.uriMatcher;
        if (uriMatcher7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher7 = null;
        }
        uriMatcher7.addURI(string, "shares/", 4);
        UriMatcher uriMatcher8 = this.uriMatcher;
        if (uriMatcher8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher8 = null;
        }
        uriMatcher8.addURI(string, "shares/#", 4);
        UriMatcher uriMatcher9 = this.uriMatcher;
        if (uriMatcher9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher9 = null;
        }
        uriMatcher9.addURI(string, "capabilities/", 5);
        UriMatcher uriMatcher10 = this.uriMatcher;
        if (uriMatcher10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher10 = null;
        }
        uriMatcher10.addURI(string, "capabilities/#", 5);
        UriMatcher uriMatcher11 = this.uriMatcher;
        if (uriMatcher11 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher11 = null;
        }
        uriMatcher11.addURI(string, "uploads/", 6);
        UriMatcher uriMatcher12 = this.uriMatcher;
        if (uriMatcher12 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher12 = null;
        }
        uriMatcher12.addURI(string, "uploads/#", 6);
        UriMatcher uriMatcher13 = this.uriMatcher;
        if (uriMatcher13 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher13 = null;
        }
        uriMatcher13.addURI(string, "cameraUploadsSync/", 7);
        UriMatcher uriMatcher14 = this.uriMatcher;
        if (uriMatcher14 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher14 = null;
        }
        uriMatcher14.addURI(string, "cameraUploadsSync/#", 7);
        UriMatcher uriMatcher15 = this.uriMatcher;
        if (uriMatcher15 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher15 = null;
        }
        uriMatcher15.addURI(string, "quotas/", 8);
        UriMatcher uriMatcher16 = this.uriMatcher;
        if (uriMatcher16 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
        } else {
            uriMatcher = uriMatcher16;
        }
        uriMatcher.addURI(string, "quotas/#", 8);
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0219  */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.database.Cursor query(android.net.Uri r11, java.lang.String[] r12, java.lang.String r13, java.lang.String[] r14, java.lang.String r15) {
        /*
            Method dump skipped, instructions count: 584
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.owncloud.android.providers.FileContentProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    private final String[] computeProjection(String[] projectionIn) {
        int i = 0;
        boolean z = true;
        if (projectionIn != null) {
            if (!(projectionIn.length == 0)) {
                z = false;
            }
        }
        if (!z) {
            String[] strArr = new String[projectionIn.length];
            int length = projectionIn.length;
            while (i < length) {
                String str = shareProjectionMap.get(projectionIn[i]);
                if (str != null) {
                    strArr[i] = str;
                    i++;
                } else {
                    throw new IllegalArgumentException("Invalid column " + projectionIn[i]);
                }
            }
            return strArr;
        }
        Set<Map.Entry<String, String>> entrySet = shareProjectionMap.entrySet();
        Intrinsics.checkNotNullExpressionValue(entrySet, "shareProjectionMap.entries");
        String[] strArr2 = new String[entrySet.size()];
        for (Map.Entry<String, String> entry : entrySet) {
            Intrinsics.checkNotNullExpressionValue(entry, "entryIter.next()");
            Map.Entry<String, String> entry2 = entry;
            if (!Intrinsics.areEqual(entry2.getKey(), "_count")) {
                strArr2[i] = entry2.getValue();
                i++;
            }
        }
        return strArr2;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        ContentResolver contentResolver;
        Intrinsics.checkNotNullParameter(uri, "uri");
        DataBaseHelper dataBaseHelper = this.dbHelper;
        if (dataBaseHelper == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dbHelper");
            dataBaseHelper = null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Intrinsics.checkNotNullExpressionValue(db, "db");
            int update = update(db, uri, values, selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            Context context = getContext();
            if (context != null && (contentResolver = context.getContentResolver()) != null) {
                contentResolver.notifyChange(uri, null);
            }
            return update;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    private final int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (selection != null && selectionArgs == null) {
            throw new IllegalArgumentException("Selection not allowed, use parameterized queries");
        }
        UriMatcher uriMatcher = this.uriMatcher;
        if (uriMatcher == null) {
            Intrinsics.throwUninitializedPropertyAccessException("uriMatcher");
            uriMatcher = null;
        }
        switch (uriMatcher.match(uri)) {
            case 2:
                return 0;
            case 3:
            default:
                return db.update("filelist", values, selection, selectionArgs);
            case 4:
                if (values == null) {
                    return 0;
                }
                OwncloudDatabase.Companion companion = OwncloudDatabase.INSTANCE;
                Context context = getContext();
                Intrinsics.checkNotNull(context);
                return (int) companion.getDatabase(context).shareDao().update(OCShareEntity.INSTANCE.fromContentValues(values));
            case 5:
                return db.update("capabilities", values, selection, selectionArgs);
            case 6:
                int update = db.update(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME, values, selection, selectionArgs);
                trimSuccessfulUploads(db);
                return update;
            case 7:
                return db.update(ProviderMeta.ProviderTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME, values, selection, selectionArgs);
            case 8:
                return db.update("user_quotas", values, selection, selectionArgs);
        }
    }

    @Override // android.content.ContentProvider
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        Intrinsics.checkNotNullParameter(operations, "operations");
        Timber.d("applying batch in provider " + this + " (temporary: " + isTemporary() + ')', new Object[0]);
        ContentProviderResult[] contentProviderResultArr = new ContentProviderResult[operations.size()];
        DataBaseHelper dataBaseHelper = this.dbHelper;
        if (dataBaseHelper == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dbHelper");
            dataBaseHelper = null;
        }
        SQLiteDatabase writableDatabase = dataBaseHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Iterator<ContentProviderOperation> it = operations.iterator();
            int i = 0;
            while (it.hasNext()) {
                contentProviderResultArr[i] = it.next().apply(this, contentProviderResultArr, i);
                i++;
            }
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
            Timber.d("applied batch in provider " + this, new Object[0]);
            return contentProviderResultArr;
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: FileContentProvider.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J \u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0016¨\u0006\r²\u0006\n\u0010\u000e\u001a\u00020\u000fX\u008a\u0084\u0002²\u0006\n\u0010\u0010\u001a\u00020\u0011X\u008a\u0084\u0002²\u0006\n\u0010\u0012\u001a\u00020\u0013X\u008a\u0084\u0002²\u0006\n\u0010\u0014\u001a\u00020\u0015X\u008a\u0084\u0002"}, d2 = {"Lcom/owncloud/android/providers/FileContentProvider$DataBaseHelper;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "(Lcom/owncloud/android/providers/FileContentProvider;Landroid/content/Context;)V", "onCreate", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "", "newVersion", "owncloudApp_originalRelease", "ocLocalCapabilitiesDataSource", "Lcom/owncloud/android/data/capabilities/datasources/implementation/OCLocalCapabilitiesDataSource;", "sharedPreferencesProvider", "Lcom/owncloud/android/data/preferences/datasources/SharedPreferencesProvider;", "backupLocalDataSource", "Lcom/owncloud/android/data/folderbackup/datasources/FolderBackupLocalDataSource;", "workManagerProvider", "Lcom/owncloud/android/providers/WorkManagerProvider;"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes2.dex */
    public final class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context) {
            super(context, "filelist", (SQLiteDatabase.CursorFactory) null, 36);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            Intrinsics.checkNotNullParameter(db, "db");
            Timber.i("SQL : Entering in onCreate", new Object[0]);
            FileContentProvider.this.createFilesTable(db);
            FileContentProvider.this.createCapabilitiesTable(db);
            FileContentProvider.this.createUploadsTable(db);
            FileContentProvider.this.createUserAvatarsTable(db);
            FileContentProvider.this.createUserQuotaTable(db);
            FileContentProvider.this.createCameraUploadsSyncTable(db);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            boolean z;
            boolean z2;
            Intrinsics.checkNotNullParameter(db, "db");
            Timber.i("SQL : Entering in onUpgrade", new Object[0]);
            boolean z3 = true;
            if (oldVersion != 1 || newVersion < 2) {
                z = false;
            } else {
                Timber.i("SQL : Entering in the #1 ADD in onUpgrade", new Object[0]);
                db.execSQL("ALTER TABLE filelist ADD COLUMN keep_in_sync INTEGER  DEFAULT 0");
                z = true;
            }
            if (oldVersion < 3 && newVersion >= 3) {
                Timber.i("SQL : Entering in the #2 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN last_sync_date_for_data INTEGER  DEFAULT 0");
                    db.execSQL("UPDATE filelist SET last_sync_date_for_data = " + System.currentTimeMillis() + " WHERE media_path IS NOT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 4 && newVersion >= 4) {
                Timber.i("SQL : Entering in the #3 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN modified_at_last_sync_for_data INTEGER  DEFAULT 0");
                    db.execSQL("UPDATE filelist SET modified_at_last_sync_for_data = modified WHERE media_path IS NOT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 5 && newVersion >= 5) {
                Timber.i("SQL : Entering in the #4 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN etag TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 6 && newVersion >= 6) {
                Timber.i("SQL : Entering in the #5 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN share_by_link INTEGER  DEFAULT 0");
                    db.execSQL("ALTER TABLE filelist ADD COLUMN public_link TEXT  DEFAULT NULL");
                    FileContentProvider.this.createOCSharesTable(db);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 7 && newVersion >= 7) {
                Timber.i("SQL : Entering in the #7 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN permissions TEXT  DEFAULT NULL");
                    db.execSQL("ALTER TABLE filelist ADD COLUMN remote_id TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 8 && newVersion >= 8) {
                Timber.i("SQL : Entering in the #8 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN update_thumbnail INTEGER  DEFAULT 0");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 9 && newVersion >= 9) {
                Timber.i("SQL : Entering in the #9 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN is_downloading INTEGER  DEFAULT 0");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 10 && newVersion >= 10) {
                Timber.i("SQL : Entering in the #10 ADD in onUpgrade", new Object[0]);
                FileContentProvider.this.updateAccountName(db);
                z = true;
            }
            if (oldVersion < 11 && newVersion >= 11) {
                Timber.i("SQL : Entering in the #11 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN etag_in_conflict TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 12 && newVersion >= 12) {
                Timber.i("SQL : Entering in the #12 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN shared_via_users INTEGER  DEFAULT 0");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 13 && newVersion >= 13) {
                Timber.i("SQL : Entering in the #13 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    FileContentProvider.this.createCapabilitiesTable(db);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 14 && newVersion >= 14) {
                Timber.i("SQL : Entering in the #14 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("DROP TABLE IF EXISTS instant_upload;");
                    FileContentProvider.this.createUploadsTable(db);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 15 && newVersion >= 15) {
                Timber.i("SQL : Entering in the #15 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    FileContentProvider.this.createUserAvatarsTable(db);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 16 && newVersion >= 16) {
                Timber.i("SQL : Entering in the #16 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN tree_etag TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 17 && newVersion >= 17) {
                Timber.i("SQL : Entering in the #17 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE ocshares ADD COLUMN name TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 18 && newVersion >= 18) {
                Timber.i("SQL : Entering in the #18 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE ocshares ADD COLUMN url TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 19 && newVersion >= 19) {
                Timber.i("SQL : Entering in the #19 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE capabilities ADD COLUMN sharing_public_multiple INTEGER  DEFAULT -1");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 20 && newVersion >= 20) {
                Timber.i("SQL : Entering in the #20 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE capabilities ADD COLUMN supports_upload_only INTEGER  DEFAULT -1");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 21 && newVersion >= 21) {
                Timber.i("SQL : Entering in the #21 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN private_link TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 22 && newVersion >= 22) {
                Timber.i("SQL : Entering in the #22 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    FileContentProvider.this.createCameraUploadsSyncTable(db);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 23 && newVersion >= 23) {
                Timber.i("SQL : Entering in the #23 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    FileContentProvider.this.createUserQuotaTable(db);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion < 24 && newVersion >= 24) {
                Timber.i("SQL : Entering in the #24 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE list_of_uploads ADD COLUMN transfer_id TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z = true;
                } finally {
                }
            }
            if (oldVersion >= 25 || newVersion < 25) {
                z2 = z;
            } else {
                Timber.i("SQL : Entering in the #25 ADD in onUpgrade", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE capabilities ADD COLUMN sharing_public_password_enforced_read_only INTEGER DEFAULT NULL");
                    db.execSQL("ALTER TABLE capabilities ADD COLUMN sharing_public_password_enforced_read_write INTEGER DEFAULT NULL");
                    db.execSQL("ALTER TABLE capabilities ADD COLUMN sharing_public_password_enforced_public_only INTEGER DEFAULT NULL");
                    db.execSQL("ALTER TABLE ocshares ADD COLUMN share_with_additional_info TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z2 = true;
                } finally {
                }
            }
            if (oldVersion < 26 && newVersion >= 26) {
                Timber.i("SQL : Entering in #26 to migrate shares from SQLite to Room", new Object[0]);
                db.execSQL("DROP TABLE IF EXISTS ocshares;");
            }
            final Qualifier qualifier = null;
            Object[] objArr = 0;
            Object[] objArr2 = 0;
            Object[] objArr3 = 0;
            Object[] objArr4 = 0;
            Object[] objArr5 = 0;
            if (oldVersion < 27 && newVersion >= 27) {
                Timber.i("SQL : Entering in #27 to migrate capabilities from SQLite to Room", new Object[0]);
                final Cursor query = db.query("capabilities", null, null, null, null, null, null);
                if (query.moveToFirst()) {
                    final FileContentProvider fileContentProvider = FileContentProvider.this;
                    LazyThreadSafetyMode lazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED;
                    final Object[] objArr6 = objArr5 == true ? 1 : 0;
                    final Lazy lazy = LazyKt.lazy(lazyThreadSafetyMode, (Function0) new Function0<OCLocalCapabilitiesDataSource>() { // from class: com.owncloud.android.providers.FileContentProvider$DataBaseHelper$onUpgrade$$inlined$inject$default$1
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        {
                            super(0);
                        }

                        /* JADX WARN: Type inference failed for: r0v2, types: [com.owncloud.android.data.capabilities.datasources.implementation.OCLocalCapabilitiesDataSource, java.lang.Object] */
                        @Override // kotlin.jvm.functions.Function0
                        public final OCLocalCapabilitiesDataSource invoke() {
                            ComponentCallbacks componentCallbacks = fileContentProvider;
                            return AndroidKoinScopeExtKt.getKoinScope(componentCallbacks).get(Reflection.getOrCreateKotlinClass(OCLocalCapabilitiesDataSource.class), qualifier, objArr6);
                        }
                    });
                    FileContentProvider.this.getExecutors().getDiskIO().execute(new Runnable() { // from class: com.owncloud.android.providers.FileContentProvider$DataBaseHelper$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            FileContentProvider.DataBaseHelper.m338onUpgrade$lambda2(query, lazy);
                        }
                    });
                }
            }
            if (oldVersion < 30 && newVersion >= 30) {
                Timber.i("SQL : Entering in the #30 ADD chunking capability", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE capabilities ADD COLUMN dav_chunking_version TEXT  DEFAULT NULL");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z2 = true;
                } finally {
                }
            }
            if (oldVersion < 32 && newVersion >= 32) {
                Timber.i("SQL : Entering in the #32 DROP quotas and avatars table and use room database", new Object[0]);
                db.beginTransaction();
                try {
                    db.execSQL("DROP TABLE IF EXISTS user_quotas;");
                    db.execSQL("DROP TABLE IF EXISTS user_avatars;");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    z2 = true;
                } finally {
                }
            }
            if (oldVersion >= 34 || newVersion < 34) {
                z3 = z2;
            } else {
                Timber.i("SQL : Entering in the #34 Migrate old camera uploads configuration to room database", new Object[0]);
                db.beginTransaction();
                long j = 0;
                long j2 = 0;
                try {
                    Cursor cursor = db.query(ProviderMeta.ProviderTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME, null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        Intrinsics.checkNotNullExpressionValue(cursor, "cursor");
                        j = CursorExtKt.getLongFromColumnOrThrow(cursor, ProviderMeta.ProviderTableMeta.PICTURES_LAST_SYNC_TIMESTAMP);
                        j2 = CursorExtKt.getLongFromColumnOrThrow(cursor, ProviderMeta.ProviderTableMeta.VIDEOS_LAST_SYNC_TIMESTAMP);
                    }
                    final FileContentProvider fileContentProvider2 = FileContentProvider.this;
                    LazyThreadSafetyMode lazyThreadSafetyMode2 = LazyThreadSafetyMode.SYNCHRONIZED;
                    final Object[] objArr7 = objArr4 == true ? 1 : 0;
                    final Object[] objArr8 = objArr3 == true ? 1 : 0;
                    CameraUploadsMigrationToRoom cameraUploadsMigrationToRoom = new CameraUploadsMigrationToRoom(m339onUpgrade$lambda3(LazyKt.lazy(lazyThreadSafetyMode2, (Function0) new Function0<SharedPreferencesProvider>() { // from class: com.owncloud.android.providers.FileContentProvider$DataBaseHelper$onUpgrade$$inlined$inject$default$2
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        {
                            super(0);
                        }

                        /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, com.owncloud.android.data.preferences.datasources.SharedPreferencesProvider] */
                        @Override // kotlin.jvm.functions.Function0
                        public final SharedPreferencesProvider invoke() {
                            ComponentCallbacks componentCallbacks = fileContentProvider2;
                            return AndroidKoinScopeExtKt.getKoinScope(componentCallbacks).get(Reflection.getOrCreateKotlinClass(SharedPreferencesProvider.class), objArr7, objArr8);
                        }
                    })));
                    final FolderBackUpConfiguration pictureUploadsConfigurationPreferences = cameraUploadsMigrationToRoom.getPictureUploadsConfigurationPreferences(j);
                    final FolderBackUpConfiguration videoUploadsConfigurationPreferences = cameraUploadsMigrationToRoom.getVideoUploadsConfigurationPreferences(j2);
                    final FileContentProvider fileContentProvider3 = FileContentProvider.this;
                    LazyThreadSafetyMode lazyThreadSafetyMode3 = LazyThreadSafetyMode.SYNCHRONIZED;
                    final Object[] objArr9 = objArr2 == true ? 1 : 0;
                    final Object[] objArr10 = objArr == true ? 1 : 0;
                    final Lazy lazy2 = LazyKt.lazy(lazyThreadSafetyMode3, (Function0) new Function0<FolderBackupLocalDataSource>() { // from class: com.owncloud.android.providers.FileContentProvider$DataBaseHelper$onUpgrade$$inlined$inject$default$3
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        {
                            super(0);
                        }

                        /* JADX WARN: Type inference failed for: r0v2, types: [com.owncloud.android.data.folderbackup.datasources.FolderBackupLocalDataSource, java.lang.Object] */
                        @Override // kotlin.jvm.functions.Function0
                        public final FolderBackupLocalDataSource invoke() {
                            ComponentCallbacks componentCallbacks = fileContentProvider3;
                            return AndroidKoinScopeExtKt.getKoinScope(componentCallbacks).get(Reflection.getOrCreateKotlinClass(FolderBackupLocalDataSource.class), objArr9, objArr10);
                        }
                    });
                    Executor diskIO = FileContentProvider.this.getExecutors().getDiskIO();
                    final FileContentProvider fileContentProvider4 = FileContentProvider.this;
                    diskIO.execute(new Runnable() { // from class: com.owncloud.android.providers.FileContentProvider$DataBaseHelper$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            FileContentProvider.DataBaseHelper.m341onUpgrade$lambda8(FolderBackUpConfiguration.this, videoUploadsConfigurationPreferences, fileContentProvider4, lazy2);
                        }
                    });
                    cursor.close();
                    db.execSQL("DROP TABLE IF EXISTS camera_uploads_sync;");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    Unit unit = Unit.INSTANCE;
                } finally {
                }
            }
            if (z3) {
                return;
            }
            Timber.i("SQL : OUT of the ADD in onUpgrade; oldVersion == " + oldVersion + ", newVersion == " + newVersion, new Object[0]);
        }

        /* renamed from: onUpgrade$lambda-0, reason: not valid java name */
        private static final OCLocalCapabilitiesDataSource m337onUpgrade$lambda0(Lazy<OCLocalCapabilitiesDataSource> lazy) {
            return lazy.getValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: onUpgrade$lambda-2, reason: not valid java name */
        public static final void m338onUpgrade$lambda2(Cursor cursor, Lazy ocLocalCapabilitiesDataSource$delegate) {
            Intrinsics.checkNotNullParameter(ocLocalCapabilitiesDataSource$delegate, "$ocLocalCapabilitiesDataSource$delegate");
            OCLocalCapabilitiesDataSource m337onUpgrade$lambda0 = m337onUpgrade$lambda0(ocLocalCapabilitiesDataSource$delegate);
            OCCapabilityEntity.Companion companion = OCCapabilityEntity.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(cursor, "cursor");
            List listOf = CollectionsKt.listOf(companion.fromCursor(cursor));
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(listOf, 10));
            Iterator it = listOf.iterator();
            while (it.hasNext()) {
                arrayList.add(OCLocalCapabilitiesDataSource.INSTANCE.toModel((OCCapabilityEntity) it.next()));
            }
            m337onUpgrade$lambda0.insert(arrayList);
        }

        /* renamed from: onUpgrade$lambda-3, reason: not valid java name */
        private static final SharedPreferencesProvider m339onUpgrade$lambda3(Lazy<? extends SharedPreferencesProvider> lazy) {
            return lazy.getValue();
        }

        /* renamed from: onUpgrade$lambda-4, reason: not valid java name */
        private static final FolderBackupLocalDataSource m340onUpgrade$lambda4(Lazy<? extends FolderBackupLocalDataSource> lazy) {
            return lazy.getValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        /* renamed from: onUpgrade$lambda-8, reason: not valid java name */
        public static final void m341onUpgrade$lambda8(FolderBackUpConfiguration folderBackUpConfiguration, FolderBackUpConfiguration folderBackUpConfiguration2, FileContentProvider this$0, Lazy backupLocalDataSource$delegate) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(backupLocalDataSource$delegate, "$backupLocalDataSource$delegate");
            if (folderBackUpConfiguration != null) {
                m340onUpgrade$lambda4(backupLocalDataSource$delegate).saveFolderBackupConfiguration(folderBackUpConfiguration);
            }
            if (folderBackUpConfiguration2 != null) {
                m340onUpgrade$lambda4(backupLocalDataSource$delegate).saveFolderBackupConfiguration(folderBackUpConfiguration2);
            }
            if (folderBackUpConfiguration == null && folderBackUpConfiguration2 == null) {
                return;
            }
            final FileContentProvider fileContentProvider = this$0;
            LazyThreadSafetyMode lazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED;
            final Qualifier qualifier = null;
            final Object[] objArr = 0 == true ? 1 : 0;
            m342onUpgrade$lambda8$lambda7(LazyKt.lazy(lazyThreadSafetyMode, (Function0) new Function0<WorkManagerProvider>() { // from class: com.owncloud.android.providers.FileContentProvider$DataBaseHelper$onUpgrade$lambda-8$$inlined$inject$default$1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(0);
                }

                /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, com.owncloud.android.providers.WorkManagerProvider] */
                @Override // kotlin.jvm.functions.Function0
                public final WorkManagerProvider invoke() {
                    ComponentCallbacks componentCallbacks = fileContentProvider;
                    return AndroidKoinScopeExtKt.getKoinScope(componentCallbacks).get(Reflection.getOrCreateKotlinClass(WorkManagerProvider.class), qualifier, objArr);
                }
            })).enqueueCameraUploadsWorker();
        }

        /* renamed from: onUpgrade$lambda-8$lambda-7, reason: not valid java name */
        private static final WorkManagerProvider m342onUpgrade$lambda8$lambda7(Lazy<WorkManagerProvider> lazy) {
            return lazy.getValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createFilesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE filelist(_id INTEGER PRIMARY KEY, filename TEXT, path TEXT, parent INTEGER, created INTEGER, modified INTEGER, content_type TEXT, content_length INTEGER, media_path TEXT, file_owner TEXT, last_sync_date INTEGER, keep_in_sync INTEGER, last_sync_date_for_data INTEGER, modified_at_last_sync_for_data INTEGER, etag TEXT, tree_etag TEXT, share_by_link INTEGER, public_link TEXT, permissions TEXT null,remote_id TEXT null,update_thumbnail INTEGER,is_downloading INTEGER,etag_in_conflict TEXT,shared_via_users INTEGER,private_link TEXT );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createOCSharesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ocshares(_id INTEGER PRIMARY KEY, file_source INTEGER, item_source INTEGER, share_type INTEGER, share_with TEXT, path TEXT, permissions INTEGER, shared_date INTEGER, expiration_date INTEGER, token TEXT, shared_with_display_name TEXT, is_directory INTEGER, user_id INTEGER, id_remote_shared INTEGER,owner_share TEXT, url TEXT, name TEXT );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createCapabilitiesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE capabilities(_id INTEGER PRIMARY KEY, account TEXT, version_mayor INTEGER, version_minor INTEGER, version_micro INTEGER, version_string TEXT, version_edition TEXT, core_pollinterval INTEGER, dav_chunking_version TEXT, sharing_api_enabled INTEGER, sharing_public_enabled INTEGER, sharing_public_password_enforced INTEGER, sharing_public_password_enforced_read_only INTEGER, sharing_public_password_enforced_read_write INTEGER, sharing_public_password_enforced_public_only INTEGER, sharing_public_expire_date_enabled INTEGER, sharing_public_expire_date_days INTEGER, sharing_public_expire_date_enforced INTEGER, sharing_public_upload INTEGER, sharing_public_multiple INTEGER, supports_upload_only INTEGER, sharing_resharing INTEGER, sharing_federation_outgoing INTEGER, sharing_federation_incoming INTEGER, files_bigfilechunking INTEGER, files_undelete INTEGER, files_versioning INTEGER );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createUploadsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE list_of_uploads(_id INTEGER PRIMARY KEY, local_path TEXT, remote_path TEXT, account_name TEXT, file_size LONG, status INTEGER, local_behaviour INTEGER, upload_time INTEGER, force_overwrite INTEGER, is_create_remote_folder INTEGER, upload_end_timestamp INTEGER, last_result INTEGER, created_by INTEGER, transfer_id TEXT );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createUserAvatarsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user_avatars(_id INTEGER PRIMARY KEY, account_name TEXT, cache_key TEXT, mime_type TEXT, etag TEXT );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createUserQuotaTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user_quotas(_id INTEGER PRIMARY KEY, account_name TEXT, free LONG, relative LONG, total LONG, used LONG );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void createCameraUploadsSyncTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE camera_uploads_sync(_id INTEGER PRIMARY KEY, pictures_last_sync_date INTEGER,videos_last_sync_date INTEGER);");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateAccountName(SQLiteDatabase db) {
        Timber.d("SQL : THREAD:  " + Thread.currentThread().getName(), new Object[0]);
        AccountManager accountManager = AccountManager.get(getContext());
        try {
            Account[] accountsByType = AccountManager.get(getContext()).getAccountsByType(MainApp.INSTANCE.getAccountType());
            Intrinsics.checkNotNullExpressionValue(accountsByType, "get(context).getAccounts…accountType\n            )");
            for (Account account : accountsByType) {
                String userData = accountManager.getUserData(account, AccountUtils.Constants.KEY_OC_BASE_URL);
                Intrinsics.checkNotNullExpressionValue(userData, "ama.getUserData(account,…onstants.KEY_OC_BASE_URL)");
                String usernameForAccount = AccountUtils.getUsernameForAccount(account);
                String buildAccountNameOld = AccountUtils.buildAccountNameOld(Uri.parse(userData), usernameForAccount);
                Intrinsics.checkNotNullExpressionValue(buildAccountNameOld, "buildAccountNameOld(Uri.…rse(serverUrl), username)");
                String buildAccountName = AccountUtils.buildAccountName(Uri.parse(userData), usernameForAccount);
                Intrinsics.checkNotNullExpressionValue(buildAccountName, "buildAccountName(Uri.parse(serverUrl), username)");
                db.beginTransaction();
                try {
                    try {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER, buildAccountName);
                        Timber.d("SQL : Updated account in database: old name == " + buildAccountNameOld + ", new name == " + buildAccountName + " (" + db.update("filelist", contentValues, "file_owner=?", new String[]{buildAccountNameOld}) + " rows updated )", new Object[0]);
                        updateDownloadedFiles(db, buildAccountName, buildAccountNameOld);
                        db.setTransactionSuccessful();
                    } catch (SQLException e) {
                        Timber.e(e, "SQL Exception upgrading account names or paths in database", new Object[0]);
                    }
                    db.endTransaction();
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
            }
        } catch (Exception e2) {
            Timber.e(e2, "Exception upgrading account names or paths in database", new Object[0]);
        }
    }

    private final void updateDownloadedFiles(SQLiteDatabase db, String newAccountName, String oldAccountName) {
        Cursor query = db.query("filelist", null, "file_owner=? AND media_path IS NOT NULL", new String[]{newAccountName}, null, null, null);
        try {
            Cursor it = query;
            if (it.moveToFirst()) {
                new File(FileStorageUtils.getSavePath(oldAccountName)).renameTo(new File(FileStorageUtils.getSavePath(newAccountName)));
                do {
                    Intrinsics.checkNotNullExpressionValue(it, "it");
                    String stringFromColumnOrThrow = CursorExtKt.getStringFromColumnOrThrow(it, ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH);
                    String defaultSavePathFor = FileStorageUtils.getDefaultSavePathFor(newAccountName, new OCFile(CursorExtKt.getStringFromColumnOrThrow(it, "path")));
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH, defaultSavePathFor);
                    db.update("filelist", contentValues, "media_path=?", new String[]{stringFromColumnOrThrow});
                    Timber.v("SQL : Updated path of downloaded file: old file name == " + stringFromColumnOrThrow + ", new file name == " + defaultSavePathFor, new Object[0]);
                } while (it.moveToNext());
            }
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(query, null);
        } finally {
        }
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(mode, "mode");
        return super.openFile(uri, mode, signal);
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(mode, "mode");
        return super.openFile(uri, mode);
    }

    private final void trimSuccessfulUploads(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            try {
                cursor = db.rawQuery("delete from list_of_uploads where status == " + UploadsStorageManager.UploadStatus.UPLOAD_SUCCEEDED.getValue() + " and _id not in (select _id from list_of_uploads where status == " + UploadsStorageManager.UploadStatus.UPLOAD_SUCCEEDED.getValue() + " order by upload_end_timestamp desc limit 30)", null);
                Intrinsics.checkNotNull(cursor);
                cursor.moveToFirst();
            } catch (Exception e) {
                Timber.e(e, "Something wrong trimming successful uploads, database could grow more than expected", new Object[0]);
                if (cursor == null) {
                    return;
                }
            }
            cursor.close();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    static {
        HashMap<String, String> hashMap = new HashMap<>();
        fileProjectionMap = hashMap;
        hashMap.put("_id", "_id");
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_PARENT, ProviderMeta.ProviderTableMeta.FILE_PARENT);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_NAME, ProviderMeta.ProviderTableMeta.FILE_NAME);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_CREATION, ProviderMeta.ProviderTableMeta.FILE_CREATION);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_MODIFIED, ProviderMeta.ProviderTableMeta.FILE_MODIFIED);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_MODIFIED_AT_LAST_SYNC_FOR_DATA, ProviderMeta.ProviderTableMeta.FILE_MODIFIED_AT_LAST_SYNC_FOR_DATA);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_CONTENT_LENGTH, ProviderMeta.ProviderTableMeta.FILE_CONTENT_LENGTH);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_CONTENT_TYPE, ProviderMeta.ProviderTableMeta.FILE_CONTENT_TYPE);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH, ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH);
        hashMap.put("path", "path");
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER, ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_LAST_SYNC_DATE, ProviderMeta.ProviderTableMeta.FILE_LAST_SYNC_DATE);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_LAST_SYNC_DATE_FOR_DATA, ProviderMeta.ProviderTableMeta.FILE_LAST_SYNC_DATE_FOR_DATA);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_KEEP_IN_SYNC, ProviderMeta.ProviderTableMeta.FILE_KEEP_IN_SYNC);
        hashMap.put("etag", "etag");
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_TREE_ETAG, ProviderMeta.ProviderTableMeta.FILE_TREE_ETAG);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_SHARED_VIA_LINK, ProviderMeta.ProviderTableMeta.FILE_SHARED_VIA_LINK);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_SHARED_WITH_SHAREE, ProviderMeta.ProviderTableMeta.FILE_SHARED_WITH_SHAREE);
        hashMap.put("permissions", "permissions");
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_REMOTE_ID, ProviderMeta.ProviderTableMeta.FILE_REMOTE_ID);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_UPDATE_THUMBNAIL, ProviderMeta.ProviderTableMeta.FILE_UPDATE_THUMBNAIL);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_IS_DOWNLOADING, ProviderMeta.ProviderTableMeta.FILE_IS_DOWNLOADING);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_ETAG_IN_CONFLICT, ProviderMeta.ProviderTableMeta.FILE_ETAG_IN_CONFLICT);
        hashMap.put(ProviderMeta.ProviderTableMeta.FILE_PRIVATE_LINK, ProviderMeta.ProviderTableMeta.FILE_PRIVATE_LINK);
        HashMap<String, String> hashMap2 = new HashMap<>();
        shareProjectionMap = hashMap2;
        hashMap2.put("id", "id");
        hashMap2.put("share_type", "share_type");
        hashMap2.put("share_with", "share_with");
        hashMap2.put("path", "path");
        hashMap2.put("permissions", "permissions");
        hashMap2.put("shared_date", "shared_date");
        hashMap2.put("expiration_date", "expiration_date");
        hashMap2.put("token", "token");
        hashMap2.put("shared_with_display_name", "shared_with_display_name");
        hashMap2.put("share_with_additional_info", "share_with_additional_info");
        hashMap2.put("is_directory", "is_directory");
        hashMap2.put("id_remote_shared", "id_remote_shared");
        hashMap2.put("owner_share", "owner_share");
        hashMap2.put("name", "name");
        hashMap2.put("url", "url");
        HashMap<String, String> hashMap3 = new HashMap<>();
        capabilityProjectionMap = hashMap3;
        hashMap3.put("_id", "_id");
        hashMap3.put("account", "account");
        hashMap3.put("version_mayor", "version_mayor");
        hashMap3.put("version_minor", "version_minor");
        hashMap3.put("version_micro", "version_micro");
        hashMap3.put("version_string", "version_string");
        hashMap3.put("version_edition", "version_edition");
        hashMap3.put("core_pollinterval", "core_pollinterval");
        hashMap3.put("dav_chunking_version", "dav_chunking_version");
        hashMap3.put("sharing_api_enabled", "sharing_api_enabled");
        hashMap3.put("sharing_public_enabled", "sharing_public_enabled");
        hashMap3.put("sharing_public_password_enforced", "sharing_public_password_enforced");
        hashMap3.put("sharing_public_password_enforced_read_only", "sharing_public_password_enforced_read_only");
        hashMap3.put("sharing_public_password_enforced_read_write", "sharing_public_password_enforced_read_write");
        hashMap3.put("sharing_public_password_enforced_public_only", "sharing_public_password_enforced_public_only");
        hashMap3.put("sharing_public_expire_date_enabled", "sharing_public_expire_date_enabled");
        hashMap3.put("sharing_public_expire_date_days", "sharing_public_expire_date_days");
        hashMap3.put("sharing_public_expire_date_enforced", "sharing_public_expire_date_enforced");
        hashMap3.put("sharing_public_upload", "sharing_public_upload");
        hashMap3.put("sharing_public_multiple", "sharing_public_multiple");
        hashMap3.put("supports_upload_only", "supports_upload_only");
        hashMap3.put("sharing_resharing", "sharing_resharing");
        hashMap3.put("sharing_federation_outgoing", "sharing_federation_outgoing");
        hashMap3.put("sharing_federation_incoming", "sharing_federation_incoming");
        hashMap3.put("files_bigfilechunking", "files_bigfilechunking");
        hashMap3.put("files_undelete", "files_undelete");
        hashMap3.put("files_versioning", "files_versioning");
        HashMap<String, String> hashMap4 = new HashMap<>();
        uploadProjectionMap = hashMap4;
        hashMap4.put("_id", "_id");
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_LOCAL_PATH, ProviderMeta.ProviderTableMeta.UPLOADS_LOCAL_PATH);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_REMOTE_PATH, ProviderMeta.ProviderTableMeta.UPLOADS_REMOTE_PATH);
        hashMap4.put("account_name", "account_name");
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_FILE_SIZE, ProviderMeta.ProviderTableMeta.UPLOADS_FILE_SIZE);
        hashMap4.put("status", "status");
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_LOCAL_BEHAVIOUR, ProviderMeta.ProviderTableMeta.UPLOADS_LOCAL_BEHAVIOUR);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_UPLOAD_TIME, ProviderMeta.ProviderTableMeta.UPLOADS_UPLOAD_TIME);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_FORCE_OVERWRITE, ProviderMeta.ProviderTableMeta.UPLOADS_FORCE_OVERWRITE);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_IS_CREATE_REMOTE_FOLDER, ProviderMeta.ProviderTableMeta.UPLOADS_IS_CREATE_REMOTE_FOLDER);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_UPLOAD_END_TIMESTAMP, ProviderMeta.ProviderTableMeta.UPLOADS_UPLOAD_END_TIMESTAMP);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_LAST_RESULT, ProviderMeta.ProviderTableMeta.UPLOADS_LAST_RESULT);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_CREATED_BY, ProviderMeta.ProviderTableMeta.UPLOADS_CREATED_BY);
        hashMap4.put(ProviderMeta.ProviderTableMeta.UPLOADS_TRANSFER_ID, ProviderMeta.ProviderTableMeta.UPLOADS_TRANSFER_ID);
        HashMap<String, String> hashMap5 = new HashMap<>();
        cameraUploadSyncProjectionMap = hashMap5;
        hashMap5.put("_id", "_id");
        hashMap5.put(ProviderMeta.ProviderTableMeta.PICTURES_LAST_SYNC_TIMESTAMP, ProviderMeta.ProviderTableMeta.PICTURES_LAST_SYNC_TIMESTAMP);
        hashMap5.put(ProviderMeta.ProviderTableMeta.VIDEOS_LAST_SYNC_TIMESTAMP, ProviderMeta.ProviderTableMeta.VIDEOS_LAST_SYNC_TIMESTAMP);
        HashMap<String, String> hashMap6 = new HashMap<>();
        quotaProjectionMap = hashMap6;
        hashMap6.put("_id", "_id");
        hashMap6.put("account_name", "account_name");
        hashMap6.put(ProviderMeta.ProviderTableMeta.USER_QUOTAS__FREE, ProviderMeta.ProviderTableMeta.USER_QUOTAS__FREE);
        hashMap6.put(ProviderMeta.ProviderTableMeta.USER_QUOTAS__RELATIVE, ProviderMeta.ProviderTableMeta.USER_QUOTAS__RELATIVE);
        hashMap6.put(ProviderMeta.ProviderTableMeta.USER_QUOTAS__TOTAL, ProviderMeta.ProviderTableMeta.USER_QUOTAS__TOTAL);
        hashMap6.put(ProviderMeta.ProviderTableMeta.USER_QUOTAS__USED, ProviderMeta.ProviderTableMeta.USER_QUOTAS__USED);
    }
}
