package com.owncloud.android.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.nextcloud.client.R;
import com.owncloud.android.MainApp;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.datamodel.UploadsStorageManager;
import com.owncloud.android.db.ProviderMeta;
import com.owncloud.android.lib.common.accounts.AccountUtils;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.lib.resources.shares.ShareType;
import com.owncloud.android.utils.FileStorageUtils;
import com.owncloud.android.utils.MimeType;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: classes.dex */
public class FileContentProvider extends ContentProvider {
    private static final String ADD_COLUMN = " ADD COLUMN ";
    private static final String ALTER_TABLE = "ALTER TABLE ";
    private static final int CAPABILITIES = 5;
    private static final int DIRECTORY = 2;
    private static final String ERROR = "ERROR ";
    private static final String INTEGER = " INTEGER, ";
    private static final int ROOT_DIRECTORY = 3;
    private static final int SHARES = 4;
    private static final int SINGLE_FILE = 1;
    private static final String SQL = "SQL";
    private static final int SYNCED_FOLDERS = 7;
    private static final String TAG = FileContentProvider.class.getSimpleName();
    private static final String TEXT = " TEXT, ";
    private static final String UPGRADE_VERSION_MSG = "OUT of the ADD in onUpgrade; oldVersion == %d, newVersion == %d";
    private static final int UPLOADS = 6;
    private DataBaseHelper mDbHelper;
    private UriMatcher mUriMatcher;

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int count = delete(db, uri, where, whereArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    private int delete(SQLiteDatabase db, Uri uri, String where, String[] whereArgs) {
        int delete;
        int count = 0;
        switch (this.mUriMatcher.match(uri)) {
            case 1:
                Cursor c = query(db, uri, (String[]) null, where, whereArgs, (String) null);
                String remoteId = "";
                if (c != null) {
                    try {
                        try {
                            if (c.moveToFirst()) {
                                remoteId = c.getString(c.getColumnIndex(ProviderMeta.ProviderTableMeta.FILE_REMOTE_ID));
                            }
                        } catch (Exception e) {
                            Log_OC.d(TAG, "DB-Error removing file!", e);
                            if (c == null) {
                                return 0;
                            }
                            c.close();
                            return 0;
                        }
                    } catch (Throwable th) {
                        if (c != null) {
                            c.close();
                        }
                        throw th;
                    }
                }
                Log_OC.d(TAG, "Removing FILE " + remoteId);
                int count2 = db.delete("filelist", "_id=" + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
                if (c != null) {
                    c.close();
                    return count2;
                }
                return count2;
            case 2:
                Cursor children = query(uri, null, null, null, null);
                if (children != null && children.moveToFirst()) {
                    while (!children.isAfterLast()) {
                        long childId = children.getLong(children.getColumnIndex("_id"));
                        boolean isDir = MimeType.DIRECTORY.equals(children.getString(children.getColumnIndex(ProviderMeta.ProviderTableMeta.FILE_CONTENT_TYPE)));
                        if (isDir) {
                            delete = delete(db, ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_DIR, childId), null, null);
                        } else {
                            delete = delete(db, ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_FILE, childId), null, null);
                        }
                        count += delete;
                        children.moveToNext();
                    }
                    children.close();
                }
                return count + db.delete("filelist", "_id=" + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
            case 3:
                return db.delete("filelist", where, whereArgs);
            case 4:
                return db.delete(ProviderMeta.ProviderTableMeta.OCSHARES_TABLE_NAME, where, whereArgs);
            case 5:
                return db.delete(ProviderMeta.ProviderTableMeta.CAPABILITIES_TABLE_NAME, where, whereArgs);
            case 6:
                return db.delete(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME, where, whereArgs);
            case 7:
                return db.delete(ProviderMeta.ProviderTableMeta.SYNCED_FOLDERS_TABLE_NAME, where, whereArgs);
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri.toString());
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (this.mUriMatcher.match(uri)) {
            case 1:
                return ProviderMeta.ProviderTableMeta.CONTENT_TYPE_ITEM;
            case 2:
            default:
                throw new IllegalArgumentException("Unknown Uri id." + uri.toString());
            case 3:
                return ProviderMeta.ProviderTableMeta.CONTENT_TYPE;
        }
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Uri newUri = insert(db, uri, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    private Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        switch (this.mUriMatcher.match(uri)) {
            case 1:
            case 3:
                String remotePath = values.getAsString("path");
                String accountName = values.getAsString(ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER);
                String[] projection = {"_id", "path", ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER};
                String[] whereArgs = {remotePath, accountName};
                Cursor doubleCheck = query(db, uri, projection, "path=? AND file_owner=?", whereArgs, (String) null);
                if (doubleCheck == null || !doubleCheck.moveToFirst()) {
                    if (doubleCheck != null) {
                        doubleCheck.close();
                    }
                    long rowId = db.insert("filelist", null, values);
                    if (rowId > 0) {
                        return ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_FILE, rowId);
                    }
                    throw new SQLException(ERROR + uri);
                }
                Uri withAppendedId = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_FILE, doubleCheck.getLong(doubleCheck.getColumnIndex("_id")));
                doubleCheck.close();
                return withAppendedId;
            case 2:
            default:
                throw new IllegalArgumentException("Unknown uri id: " + uri);
            case 4:
                long rowId2 = db.insert(ProviderMeta.ProviderTableMeta.OCSHARES_TABLE_NAME, null, values);
                if (rowId2 > 0) {
                    Uri insertedShareUri = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_SHARE, rowId2);
                    updateFilesTableAccordingToShareInsertion(db, values);
                    return insertedShareUri;
                }
                throw new SQLException(ERROR + uri);
            case 5:
                long id = db.insert(ProviderMeta.ProviderTableMeta.CAPABILITIES_TABLE_NAME, null, values);
                if (id > 0) {
                    Uri insertedCapUri = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_CAPABILITIES, id);
                    return insertedCapUri;
                }
                throw new SQLException(ERROR + uri);
            case 6:
                long uploadId = db.insert(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME, null, values);
                if (uploadId > 0) {
                    Uri insertedUploadUri = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_UPLOADS, uploadId);
                    trimSuccessfulUploads(db);
                    return insertedUploadUri;
                }
                throw new SQLException(ERROR + uri);
            case 7:
                long syncedFolderId = db.insert(ProviderMeta.ProviderTableMeta.SYNCED_FOLDERS_TABLE_NAME, null, values);
                if (syncedFolderId > 0) {
                    Uri insertedSyncedFolderUri = ContentUris.withAppendedId(ProviderMeta.ProviderTableMeta.CONTENT_URI_SYNCED_FOLDERS, syncedFolderId);
                    return insertedSyncedFolderUri;
                }
                throw new SQLException(ERROR + uri);
        }
    }

    private void updateFilesTableAccordingToShareInsertion(SQLiteDatabase db, ContentValues newShare) {
        ContentValues fileValues = new ContentValues();
        int newShareType = newShare.getAsInteger(ProviderMeta.ProviderTableMeta.OCSHARES_SHARE_TYPE).intValue();
        if (newShareType == ShareType.PUBLIC_LINK.getValue()) {
            fileValues.put(ProviderMeta.ProviderTableMeta.FILE_SHARED_VIA_LINK, (Integer) 1);
        } else if (newShareType == ShareType.USER.getValue() || newShareType == ShareType.GROUP.getValue() || newShareType == ShareType.FEDERATED.getValue()) {
            fileValues.put(ProviderMeta.ProviderTableMeta.FILE_SHARED_WITH_SHAREE, (Integer) 1);
        }
        String[] whereArgs = {newShare.getAsString("path"), newShare.getAsString(ProviderMeta.ProviderTableMeta.OCSHARES_ACCOUNT_OWNER)};
        db.update("filelist", fileValues, "path=? AND file_owner=?", whereArgs);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mDbHelper = new DataBaseHelper(getContext());
        String authority = getContext().getResources().getString(R.string.authority);
        this.mUriMatcher = new UriMatcher(-1);
        this.mUriMatcher.addURI(authority, null, 3);
        this.mUriMatcher.addURI(authority, "file/", 1);
        this.mUriMatcher.addURI(authority, "file/#", 1);
        this.mUriMatcher.addURI(authority, "dir/", 2);
        this.mUriMatcher.addURI(authority, "dir/#", 2);
        this.mUriMatcher.addURI(authority, "shares/", 4);
        this.mUriMatcher.addURI(authority, "shares/#", 4);
        this.mUriMatcher.addURI(authority, "capabilities/", 5);
        this.mUriMatcher.addURI(authority, "capabilities/#", 5);
        this.mUriMatcher.addURI(authority, "uploads/", 6);
        this.mUriMatcher.addURI(authority, "uploads/#", 6);
        this.mUriMatcher.addURI(authority, ProviderMeta.ProviderTableMeta.SYNCED_FOLDERS_TABLE_NAME, 7);
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            Cursor result = query(db, uri, projection, selection, selectionArgs, sortOrder);
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    private Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String order;
        SQLiteQueryBuilder sqlQuery = new SQLiteQueryBuilder();
        sqlQuery.setTables("filelist");
        switch (this.mUriMatcher.match(uri)) {
            case 1:
                if (uri.getPathSegments().size() > 1) {
                    sqlQuery.appendWhere("_id=" + uri.getPathSegments().get(1));
                    break;
                }
                break;
            case 2:
                String folderId = uri.getPathSegments().get(1);
                sqlQuery.appendWhere("parent=" + folderId);
                break;
            case 3:
                break;
            case 4:
                sqlQuery.setTables(ProviderMeta.ProviderTableMeta.OCSHARES_TABLE_NAME);
                if (uri.getPathSegments().size() > 1) {
                    sqlQuery.appendWhere("_id=" + uri.getPathSegments().get(1));
                    break;
                }
                break;
            case 5:
                sqlQuery.setTables(ProviderMeta.ProviderTableMeta.CAPABILITIES_TABLE_NAME);
                if (uri.getPathSegments().size() > 1) {
                    sqlQuery.appendWhere("_id=" + uri.getPathSegments().get(1));
                    break;
                }
                break;
            case 6:
                sqlQuery.setTables(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME);
                if (uri.getPathSegments().size() > 1) {
                    sqlQuery.appendWhere("_id=" + uri.getPathSegments().get(1));
                    break;
                }
                break;
            case 7:
                sqlQuery.setTables(ProviderMeta.ProviderTableMeta.SYNCED_FOLDERS_TABLE_NAME);
                if (uri.getPathSegments().size() > 1) {
                    sqlQuery.appendWhere("_id=" + uri.getPathSegments().get(1));
                    break;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri id: " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            switch (this.mUriMatcher.match(uri)) {
                case 4:
                    order = ProviderMeta.ProviderTableMeta.OCSHARES_DEFAULT_SORT_ORDER;
                    break;
                case 5:
                    order = ProviderMeta.ProviderTableMeta.CAPABILITIES_DEFAULT_SORT_ORDER;
                    break;
                case 6:
                    order = ProviderMeta.ProviderTableMeta.UPLOADS_DEFAULT_SORT_ORDER;
                    break;
                case 7:
                    order = "local_path";
                    break;
                default:
                    order = ProviderMeta.ProviderTableMeta.FILE_DEFAULT_SORT_ORDER;
                    break;
            }
        } else {
            order = sortOrder;
        }
        db.execSQL("PRAGMA case_sensitive_like = true");
        Cursor c = sqlQuery.query(db, projection, selection, selectionArgs, null, null, order);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int count = update(db, uri, values, selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    private int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (this.mUriMatcher.match(uri)) {
            case 2:
                return 0;
            case 3:
            default:
                return db.update("filelist", values, selection, selectionArgs);
            case 4:
                return db.update(ProviderMeta.ProviderTableMeta.OCSHARES_TABLE_NAME, values, selection, selectionArgs);
            case 5:
                return db.update(ProviderMeta.ProviderTableMeta.CAPABILITIES_TABLE_NAME, values, selection, selectionArgs);
            case 6:
                int update = db.update(ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME, values, selection, selectionArgs);
                trimSuccessfulUploads(db);
                return update;
            case 7:
                return db.update(ProviderMeta.ProviderTableMeta.SYNCED_FOLDERS_TABLE_NAME, values, selection, selectionArgs);
        }
    }

    @Override // android.content.ContentProvider
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        Log_OC.d("FileContentProvider", "applying batch in provider " + this + " (temporary: " + isTemporary() + ")");
        ContentProviderResult[] results = new ContentProviderResult[operations.size()];
        int i = 0;
        SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Iterator<ContentProviderOperation> it = operations.iterator();
            while (it.hasNext()) {
                ContentProviderOperation operation = it.next();
                results[i] = operation.apply(this, results, i);
                i++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Log_OC.d("FileContentProvider", "applied batch in provider " + this);
            return results;
        } catch (Throwable th) {
            db.endTransaction();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context) {
            super(context, "filelist", (SQLiteDatabase.CursorFactory) null, 16);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            Log_OC.i(FileContentProvider.SQL, "Entering in onCreate");
            FileContentProvider.this.createFilesTable(db);
            FileContentProvider.this.createOCSharesTable(db);
            FileContentProvider.this.createCapabilitiesTable(db);
            FileContentProvider.this.createUploadsTable(db);
            FileContentProvider.this.createSyncedFoldersTable(db);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log_OC.i(FileContentProvider.SQL, "Entering in onUpgrade");
            boolean upgraded = false;
            if (oldVersion == 1 && newVersion >= 2) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #1 ADD in onUpgrade");
                db.execSQL("ALTER TABLE filelist ADD COLUMN keep_in_sync INTEGER  DEFAULT 0");
                upgraded = true;
            }
            if (oldVersion < 3 && newVersion >= 3) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #2 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN last_sync_date_for_data INTEGER  DEFAULT 0");
                    db.execSQL("UPDATE filelist SET last_sync_date_for_data = " + System.currentTimeMillis() + " WHERE " + ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH + " IS NOT NULL");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (oldVersion < 4 && newVersion >= 4) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #3 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN modified_at_last_sync_for_data INTEGER  DEFAULT 0");
                    db.execSQL("UPDATE filelist SET modified_at_last_sync_for_data = modified WHERE media_path IS NOT NULL");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 5 && newVersion >= 5) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #4 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN etag TEXT  DEFAULT NULL");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 6 && newVersion >= 6) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #5 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN share_by_link INTEGER  DEFAULT 0");
                    db.execSQL("ALTER TABLE filelist ADD COLUMN public_link TEXT  DEFAULT NULL");
                    FileContentProvider.this.createOCSharesTable(db);
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 7 && newVersion >= 7) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #7 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN permissions TEXT  DEFAULT NULL");
                    db.execSQL("ALTER TABLE filelist ADD COLUMN remote_id TEXT  DEFAULT NULL");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 8 && newVersion >= 8) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #8 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN update_thumbnail INTEGER  DEFAULT 0");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 9 && newVersion >= 9) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #9 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN is_downloading INTEGER  DEFAULT 0");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 10 && newVersion >= 10) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #10 ADD in onUpgrade");
                FileContentProvider.this.updateAccountName(db);
                upgraded = true;
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 11 && newVersion >= 11) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #11 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN etag_in_conflict TEXT  DEFAULT NULL");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 12 && newVersion >= 12) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #12 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE filelist ADD COLUMN shared_via_users INTEGER  DEFAULT 0");
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
            if (oldVersion < 13 && newVersion >= 13) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #13 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    FileContentProvider.this.createCapabilitiesTable(db);
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (oldVersion < 14 && newVersion >= 14) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #14 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("DROP TABLE IF EXISTS instant_upload;");
                    FileContentProvider.this.createUploadsTable(db);
                    upgraded = true;
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } finally {
                }
            }
            if (oldVersion < 15 && newVersion >= 15) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #15 ADD in onUpgrade");
                db.beginTransaction();
                try {
                    db.execSQL("DROP TABLE IF EXISTS capabilities;");
                    FileContentProvider.this.createCapabilitiesTable(db);
                    upgraded = true;
                    db.setTransactionSuccessful();
                } finally {
                }
            }
            if (oldVersion < 16 && newVersion >= 16) {
                Log_OC.i(FileContentProvider.SQL, "Entering in the #16 ADD synced folders table");
                db.beginTransaction();
                try {
                    FileContentProvider.this.createSyncedFoldersTable(db);
                    upgraded = true;
                    db.setTransactionSuccessful();
                } finally {
                }
            }
            if (!upgraded) {
                Log_OC.i(FileContentProvider.SQL, String.format(Locale.ENGLISH, FileContentProvider.UPGRADE_VERSION_MSG, Integer.valueOf(oldVersion), Integer.valueOf(newVersion)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createFilesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE filelist(_id INTEGER PRIMARY KEY, filename TEXT, path TEXT, parent INTEGER, created INTEGER, modified INTEGER, content_type TEXT, content_length INTEGER, media_path TEXT, file_owner TEXT, last_sync_date INTEGER, keep_in_sync INTEGER, last_sync_date_for_data INTEGER, modified_at_last_sync_for_data INTEGER, etag TEXT, share_by_link INTEGER, public_link TEXT, permissions TEXT null,remote_id TEXT null,update_thumbnail INTEGER, is_downloading INTEGER, etag_in_conflict TEXT, shared_via_users INTEGER);");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createOCSharesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ocshares(_id INTEGER PRIMARY KEY, file_source INTEGER, item_source INTEGER, share_type INTEGER, shate_with TEXT, path TEXT, permissions INTEGER, shared_date INTEGER, expiration_date INTEGER, token TEXT, shared_with_display_name TEXT, is_directory INTEGER, user_id INTEGER, id_remote_shared INTEGER, owner_share TEXT );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createCapabilitiesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE capabilities(_id INTEGER PRIMARY KEY, account TEXT, version_mayor INTEGER, version_minor INTEGER, version_micro INTEGER, version_string TEXT, version_edition TEXT, core_pollinterval INTEGER, sharing_api_enabled INTEGER, sharing_public_enabled INTEGER, sharing_public_password_enforced INTEGER, sharing_public_expire_date_enabled INTEGER, sharing_public_expire_date_days INTEGER, sharing_public_expire_date_enforced INTEGER, sharing_public_send_mail INTEGER, sharing_public_upload INTEGER, sharing_user_send_mail INTEGER, sharing_resharing INTEGER, sharing_federation_outgoing INTEGER, sharing_federation_incoming INTEGER, files_bigfilechunking INTEGER, files_undelete INTEGER, files_versioning INTEGER, files_drop INTEGER );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createUploadsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE list_of_uploads(_id INTEGER PRIMARY KEY, local_path TEXT, remote_path TEXT, account_name TEXT, file_size LONG, status INTEGER, local_behaviour INTEGER, upload_time INTEGER, force_overwrite INTEGER, is_create_remote_folder INTEGER, upload_end_timestamp INTEGER, last_result INTEGER, created_by INTEGER );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createSyncedFoldersTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE synced_folders(_id INTEGER PRIMARY KEY, local_path TEXT, remote_path TEXT, wifi_only INTEGER, charging_only INTEGER, enabled INTEGER, subfolder_by_date INTEGER, account  TEXT, upload_option INTEGER );");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccountName(SQLiteDatabase db) {
        Log_OC.d(SQL, "THREAD:  " + Thread.currentThread().getName());
        AccountManager ama = AccountManager.get(getContext());
        try {
            Account[] accounts = AccountManager.get(getContext()).getAccountsByType(MainApp.getAccountType());
            for (Account account : accounts) {
                String serverUrl = ama.getUserData(account, AccountUtils.Constants.KEY_OC_BASE_URL);
                String username = AccountUtils.getUsernameForAccount(account);
                String oldAccountName = AccountUtils.buildAccountNameOld(Uri.parse(serverUrl), username);
                String newAccountName = AccountUtils.buildAccountName(Uri.parse(serverUrl), username);
                db.beginTransaction();
                try {
                    try {
                        ContentValues cv = new ContentValues();
                        cv.put(ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER, newAccountName);
                        int num = db.update("filelist", cv, "file_owner=?", new String[]{oldAccountName});
                        Log_OC.d(SQL, "Updated account in database: old name == " + oldAccountName + ", new name == " + newAccountName + " (" + num + " rows updated )");
                        updateDownloadedFiles(db, newAccountName, oldAccountName);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    } catch (Throwable th) {
                        db.endTransaction();
                        throw th;
                    }
                } catch (SQLException e) {
                    Log_OC.e(TAG, "SQL Exception upgrading account names or paths in database", e);
                    db.endTransaction();
                }
            }
        } catch (Exception e2) {
            Log_OC.e(TAG, "Exception upgrading account names or paths in database", e2);
        }
    }

    private void updateDownloadedFiles(SQLiteDatabase db, String newAccountName, String oldAccountName) {
        Cursor c = db.query("filelist", null, "file_owner=? AND media_path IS NOT NULL", new String[]{newAccountName}, null, null, null);
        try {
            if (c.moveToFirst()) {
                String oldAccountPath = FileStorageUtils.getSavePath(oldAccountName);
                String newAccountPath = FileStorageUtils.getSavePath(newAccountName);
                File oldAccountFolder = new File(oldAccountPath);
                File newAccountFolder = new File(newAccountPath);
                oldAccountFolder.renameTo(newAccountFolder);
                do {
                    String oldPath = c.getString(c.getColumnIndex(ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH));
                    OCFile file = new OCFile(c.getString(c.getColumnIndex("path")));
                    String newPath = FileStorageUtils.getDefaultSavePathFor(newAccountName, file);
                    ContentValues cv = new ContentValues();
                    cv.put(ProviderMeta.ProviderTableMeta.FILE_STORAGE_PATH, newPath);
                    db.update("filelist", cv, "media_path=?", new String[]{oldPath});
                    Log_OC.v(SQL, "Updated path of downloaded file: old file name == " + oldPath + ", new file name == " + newPath);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
    }

    private void trimSuccessfulUploads(SQLiteDatabase db) {
        Cursor c = null;
        try {
            try {
                c = db.rawQuery("delete from list_of_uploads where status == " + UploadsStorageManager.UploadStatus.UPLOAD_SUCCEEDED.getValue() + " and _id not in (select _id from " + ProviderMeta.ProviderTableMeta.UPLOADS_TABLE_NAME + " where status == " + UploadsStorageManager.UploadStatus.UPLOAD_SUCCEEDED.getValue() + " order by " + ProviderMeta.ProviderTableMeta.UPLOADS_UPLOAD_END_TIMESTAMP + " desc limit 30)", null);
                c.moveToFirst();
                if (c != null) {
                    c.close();
                }
            } catch (Exception e) {
                Log_OC.e(TAG, "Something wrong trimming successful uploads, database could grow more than expected", e);
                if (c != null) {
                    c.close();
                }
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }
}
