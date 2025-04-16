package com.sophos.sophtoken;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;
import android.util.Log;
import com.sophos.sophtoken.Base32String;
import com.sophos.sophtoken.FileUtilities;
import com.sophos.sophtoken.OtpProvider;
import com.sophos.sophtoken.PasscodeGenerator;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class AccountDb {
    private static final String ALGORITHM_COLUMN = "algorithm";
    private static final String COUNTER_COLUMN = "counter";
    public static final Integer DEFAULT_HOTP_COUNTER = 0;
    public static final Integer DEFAULT_TOTP_VALUE = 30;
    private static final String DIGITS_COLUMN = "digits";
    private static final String EMAIL_COLUMN = "email";
    public static final String GOOGLE_CORP_ACCOUNT_NAME = "Google Internal 2Factor";
    private static final String ID_COLUMN = "_id";
    private static final String LOCAL_TAG = "SophToken.AccountDb";
    static final String PATH = "databases";
    static final String PROVIDER_COLUMN = "provider";
    private static final int PROVIDER_GOOGLE = 1;
    private static final int PROVIDER_UNKNOWN = 0;
    private static final String SECRET_COLUMN = "secret";
    private static final String TABLE_INFO_COLUMN_NAME_COLUMN = "name";
    static final String TABLE_NAME = "accounts";
    private static final String TIMESTEP_COLUMN = "timestep";
    private static final String TYPE_COLUMN = "type";
    SQLiteDatabase mDatabase;

    /* loaded from: classes.dex */
    public enum OtpType {
        TOTP(0),
        HOTP(1);

        public final Integer value;

        OtpType(Integer num) {
            this.value = num;
        }

        public static OtpType getEnum(Integer num) {
            for (OtpType otpType : values()) {
                if (otpType.value.equals(num)) {
                    return otpType;
                }
            }
            return null;
        }
    }

    public AccountDb(Context context) {
        SQLiteDatabase openDatabase = openDatabase(context);
        this.mDatabase = openDatabase;
        openDatabase.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT NOT NULL,  %s INTEGER DEFAULT %s, %s INTEGER, %s INTEGER, %s INTEGER DEFAULT %s)", TABLE_NAME, ID_COLUMN, "email", SECRET_COLUMN, COUNTER_COLUMN, DEFAULT_HOTP_COUNTER, TYPE_COLUMN, TIMESTEP_COLUMN, DIGITS_COLUMN, ALGORITHM_COLUMN, PROVIDER_COLUMN, 0));
        Collection<String> listTableColumnNamesLowerCase = listTableColumnNamesLowerCase();
        if (!listTableColumnNamesLowerCase.contains(PROVIDER_COLUMN.toLowerCase(Locale.US))) {
            this.mDatabase.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT %s", TABLE_NAME, PROVIDER_COLUMN, 0));
        }
        if (!listTableColumnNamesLowerCase.contains(DIGITS_COLUMN.toLowerCase(Locale.US))) {
            this.mDatabase.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT %s", TABLE_NAME, DIGITS_COLUMN, OtpProvider.getDefaultDigits()));
        }
        if (listTableColumnNamesLowerCase.contains(ALGORITHM_COLUMN.toLowerCase(Locale.US))) {
            return;
        }
        this.mDatabase.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT %s", TABLE_NAME, ALGORITHM_COLUMN, OtpProvider.getDefaultAlgorithm()));
    }

    private SQLiteDatabase openDatabase(Context context) {
        int i = 0;
        while (true) {
            try {
                return context.openOrCreateDatabase(PATH, 0, null);
            } catch (SQLiteException e) {
                if (i >= 2) {
                    throw new AccountDbOpenException("Failed to open AccountDb database in three tries.\n" + getAccountDbOpenFailedErrorString(context), e);
                }
                i++;
            }
        }
    }

    private String getAccountDbOpenFailedErrorString(Context context) {
        String exc;
        String[] strArr = {context.getApplicationInfo().dataDir, context.getDatabasePath(PATH).getParent(), context.getDatabasePath(PATH).getAbsolutePath()};
        StringBuilder sb = new StringBuilder();
        int myUid = Process.myUid();
        for (int i = 0; i < 3; i++) {
            String str = strArr[i];
            try {
                FileUtilities.StatStruct stat = FileUtilities.getStat(str);
                try {
                    if (stat.uid == 0) {
                        exc = "root";
                    } else {
                        PackageManager packageManager = context.getPackageManager();
                        exc = packageManager != null ? packageManager.getNameForUid(stat.uid) : null;
                    }
                } catch (Exception e) {
                    exc = e.toString();
                }
                sb.append(str + " directory stat (my UID: " + myUid);
                if (exc == null) {
                    sb.append("): ");
                } else {
                    sb.append(", dir owner UID name: " + exc + "): ");
                }
                sb.append(stat.toString() + "\n");
            } catch (IOException e2) {
                sb.append(str + " directory stat threw an exception: " + e2 + "\n");
            }
        }
        return sb.toString();
    }

    public void close() {
        this.mDatabase.close();
    }

    static Collection<String> listTableColumnNamesLowerCase(SQLiteDatabase sQLiteDatabase, String str) {
        Cursor rawQuery = sQLiteDatabase.rawQuery(String.format("PRAGMA table_info(%s)", str), new String[0]);
        ArrayList arrayList = new ArrayList();
        if (rawQuery != null) {
            try {
                int columnIndexOrThrow = rawQuery.getColumnIndexOrThrow(TABLE_INFO_COLUMN_NAME_COLUMN);
                while (rawQuery.moveToNext()) {
                    arrayList.add(rawQuery.getString(columnIndexOrThrow).toLowerCase(Locale.US));
                }
            } finally {
                tryCloseCursor(rawQuery);
            }
        }
        return arrayList;
    }

    private Collection<String> listTableColumnNamesLowerCase() {
        return listTableColumnNamesLowerCase(this.mDatabase, TABLE_NAME);
    }

    public boolean deleteAllData() {
        this.mDatabase.delete(TABLE_NAME, null, null);
        return true;
    }

    public boolean nameExists(String str) {
        Cursor account = getAccount(str);
        try {
            return !cursorIsEmpty(account);
        } finally {
            tryCloseCursor(account);
        }
    }

    public String getSecret(String str) {
        Cursor account = getAccount(str);
        try {
            if (cursorIsEmpty(account)) {
                tryCloseCursor(account);
                return null;
            }
            account.moveToFirst();
            return account.getString(account.getColumnIndex(SECRET_COLUMN));
        } finally {
            tryCloseCursor(account);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PasscodeGenerator.Signer getSigningOracle(String str, Integer num) {
        final Mac mac;
        try {
            byte[] decodeKey = decodeKey(str);
            OtpProvider.ALGORITHM_TYPE fromInt = OtpProvider.ALGORITHM_TYPE.fromInt(num.intValue());
            if (fromInt == OtpProvider.ALGORITHM_TYPE.SHA512) {
                mac = Mac.getInstance("HMACSHA512");
            } else if (fromInt == OtpProvider.ALGORITHM_TYPE.SHA256) {
                mac = Mac.getInstance("HMACSHA256");
            } else {
                mac = Mac.getInstance("HMACSHA1");
            }
            mac.init(new SecretKeySpec(decodeKey, ""));
            return new PasscodeGenerator.Signer() { // from class: com.sophos.sophtoken.AccountDb.1
                @Override // com.sophos.sophtoken.PasscodeGenerator.Signer
                public byte[] sign(byte[] bArr) {
                    return mac.doFinal(bArr);
                }
            };
        } catch (Base32String.DecodingException e) {
            Log.e(LOCAL_TAG, e.getMessage());
            return null;
        } catch (InvalidKeyException e2) {
            Log.e(LOCAL_TAG, e2.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e3) {
            Log.e(LOCAL_TAG, e3.getMessage());
            return null;
        }
    }

    private static byte[] decodeKey(String str) throws Base32String.DecodingException {
        return Base32String.decode(str);
    }

    public Integer getCounter(String str) {
        Cursor account = getAccount(str);
        try {
            if (cursorIsEmpty(account)) {
                tryCloseCursor(account);
                return null;
            }
            account.moveToFirst();
            return Integer.valueOf(account.getInt(account.getColumnIndex(COUNTER_COLUMN)));
        } finally {
            tryCloseCursor(account);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void incrementCounter(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", str);
        contentValues.put(COUNTER_COLUMN, Integer.valueOf(getCounter(str).intValue() + 1));
        this.mDatabase.update(TABLE_NAME, contentValues, whereClause(str), null);
    }

    public OtpType getType(String str) {
        Cursor account = getAccount(str);
        try {
            if (cursorIsEmpty(account)) {
                tryCloseCursor(account);
                return null;
            }
            account.moveToFirst();
            return OtpType.getEnum(Integer.valueOf(account.getInt(account.getColumnIndex(TYPE_COLUMN))));
        } finally {
            tryCloseCursor(account);
        }
    }

    void setType(String str, OtpType otpType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", str);
        contentValues.put(TYPE_COLUMN, otpType.value);
        this.mDatabase.update(TABLE_NAME, contentValues, whereClause(str), null);
    }

    public Integer getTimestep(String str) {
        Cursor account = getAccount(str);
        try {
            if (cursorIsEmpty(account)) {
                tryCloseCursor(account);
                return null;
            }
            account.moveToFirst();
            return Integer.valueOf(account.getInt(account.getColumnIndex(TIMESTEP_COLUMN)));
        } finally {
            tryCloseCursor(account);
        }
    }

    public Integer getDigits(String str) {
        Integer defaultDigits;
        Cursor account = getAccount(str);
        try {
            if (cursorIsEmpty(account)) {
                tryCloseCursor(account);
                return null;
            }
            account.moveToFirst();
            if (account.getColumnIndex(DIGITS_COLUMN) != -1) {
                defaultDigits = Integer.valueOf(account.getInt(account.getColumnIndex(DIGITS_COLUMN)));
            } else {
                defaultDigits = OtpProvider.getDefaultDigits();
            }
            return defaultDigits;
        } finally {
            tryCloseCursor(account);
        }
    }

    public Integer getAlgorithm(String str) {
        Integer defaultAlgorithm;
        Cursor account = getAccount(str);
        try {
            if (!cursorIsEmpty(account)) {
                account.moveToFirst();
                if (account.getColumnIndex(ALGORITHM_COLUMN) != -1) {
                    defaultAlgorithm = Integer.valueOf(account.getInt(account.getColumnIndex(ALGORITHM_COLUMN)));
                } else {
                    defaultAlgorithm = OtpProvider.getDefaultAlgorithm();
                }
                return defaultAlgorithm;
            }
            tryCloseCursor(account);
            return OtpProvider.getDefaultAlgorithm();
        } finally {
            tryCloseCursor(account);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x003b, code lost:
    
        if (r6.equals(com.sophos.sophtoken.AccountDb.GOOGLE_CORP_ACCOUNT_NAME) != false) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isGoogleAccount(java.lang.String r6) {
        /*
            r5 = this;
            android.database.Cursor r0 = r5.getAccount(r6)
            boolean r1 = cursorIsEmpty(r0)     // Catch: java.lang.Throwable -> L46
            r2 = 0
            if (r1 != 0) goto L42
            r0.moveToFirst()     // Catch: java.lang.Throwable -> L46
            java.lang.String r1 = "provider"
            int r1 = r0.getColumnIndex(r1)     // Catch: java.lang.Throwable -> L46
            int r1 = r0.getInt(r1)     // Catch: java.lang.Throwable -> L46
            r3 = 1
            if (r1 != r3) goto L1f
            tryCloseCursor(r0)
            return r3
        L1f:
            java.util.Locale r1 = java.util.Locale.US     // Catch: java.lang.Throwable -> L46
            java.lang.String r1 = r6.toLowerCase(r1)     // Catch: java.lang.Throwable -> L46
            java.lang.String r4 = "@gmail.com"
            boolean r4 = r1.endsWith(r4)     // Catch: java.lang.Throwable -> L46
            if (r4 != 0) goto L3d
            java.lang.String r4 = "@google.com"
            boolean r1 = r1.endsWith(r4)     // Catch: java.lang.Throwable -> L46
            if (r1 != 0) goto L3d
            java.lang.String r1 = "Google Internal 2Factor"
            boolean r6 = r6.equals(r1)     // Catch: java.lang.Throwable -> L46
            if (r6 == 0) goto L3e
        L3d:
            r2 = 1
        L3e:
            tryCloseCursor(r0)
            return r2
        L42:
            tryCloseCursor(r0)
            return r2
        L46:
            r6 = move-exception
            tryCloseCursor(r0)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sophos.sophtoken.AccountDb.isGoogleAccount(java.lang.String):boolean");
    }

    public String findGoogleCorpAccount() {
        if (nameExists(GOOGLE_CORP_ACCOUNT_NAME)) {
            return GOOGLE_CORP_ACCOUNT_NAME;
        }
        return null;
    }

    private static String whereClause(String str) {
        return "email = " + DatabaseUtils.sqlEscapeString(str);
    }

    public void delete(String str) {
        this.mDatabase.delete(TABLE_NAME, whereClause(str), null);
    }

    public void update(String str, String str2, String str3, OtpType otpType, Integer num, Integer num2, Integer num3, Integer num4) {
        update(str, str2, str3, otpType, num, num2, num3, num4, null);
    }

    public void update(String str, String str2, String str3, OtpType otpType, Integer num, Integer num2, Integer num3, Integer num4, Boolean bool) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", str);
        contentValues.put(SECRET_COLUMN, str2);
        contentValues.put(TYPE_COLUMN, Integer.valueOf(otpType.ordinal()));
        contentValues.put(TIMESTEP_COLUMN, num);
        contentValues.put(COUNTER_COLUMN, num2);
        contentValues.put(DIGITS_COLUMN, num3);
        contentValues.put(ALGORITHM_COLUMN, num4);
        if (bool != null) {
            contentValues.put(PROVIDER_COLUMN, Integer.valueOf(bool.booleanValue() ? 1 : 0));
        }
        if (this.mDatabase.update(TABLE_NAME, contentValues, whereClause(str3), null) == 0) {
            this.mDatabase.insert(TABLE_NAME, null, contentValues);
        }
    }

    private Cursor getNames() {
        return this.mDatabase.query(TABLE_NAME, null, null, null, null, null, null, null);
    }

    private Cursor getAccount(String str) {
        return this.mDatabase.query(TABLE_NAME, null, "email= ?", new String[]{str}, null, null, null);
    }

    private static boolean cursorIsEmpty(Cursor cursor) {
        return cursor == null || cursor.getCount() == 0;
    }

    private static void tryCloseCursor(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }
        cursor.close();
    }

    public int getNames(Collection<String> collection) {
        Cursor names = getNames();
        try {
            if (cursorIsEmpty(names)) {
                return 0;
            }
            int count = names.getCount();
            int columnIndex = names.getColumnIndex("email");
            for (int i = 0; i < count; i++) {
                names.moveToPosition(i);
                collection.add(names.getString(columnIndex));
            }
            return count;
        } finally {
            tryCloseCursor(names);
        }
    }

    public int getTimesteps(Collection<Integer> collection) {
        Cursor names = getNames();
        try {
            if (cursorIsEmpty(names)) {
                return 0;
            }
            int count = names.getCount();
            int columnIndex = names.getColumnIndex(TIMESTEP_COLUMN);
            for (int i = 0; i < count; i++) {
                names.moveToPosition(i);
                collection.add(Integer.valueOf(names.getInt(columnIndex)));
            }
            return count;
        } finally {
            tryCloseCursor(names);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AccountDbOpenException extends RuntimeException {
        public AccountDbOpenException(String str, Exception exc) {
            super(str, exc);
        }
    }
}
