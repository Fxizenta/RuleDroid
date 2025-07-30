package com.mmt.travel.app.common.tracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.mmt.travel.app.common.util.LogUtils;
import io.hansel.pebbletracesdk.HanselCrashReporter;
import io.hansel.pebbletracesdk.annotations.HanselInclude;
import io.hansel.pebbletracesdk.codepatch.PatchJoinPoint;
import io.hansel.pebbletracesdk.codepatch.patch.Patch;
import java.io.File;

@HanselInclude
/* loaded from: classes.dex */
final class MMTEventDB {
    private static final String b = "CREATE TABLE " + Table.EVENTS.b + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, data STRING NOT NULL, event_type STRING NOT NULL, created_at INTEGER NOT NULL);";
    private static final String c = "CREATE INDEX IF NOT EXISTS time_idx ON " + Table.EVENTS.b + " (created_at);";

    /* renamed from: a, reason: collision with root package name */
    final a f4351a;

    static /* synthetic */ String a() {
        Patch patch = HanselCrashReporter.getPatch(MMTEventDB.class, "a", null);
        return patch != null ? (String) patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(MMTEventDB.class).setArguments(new Object[0]).toPatchJoinPoint()) : b;
    }

    static /* synthetic */ String b() {
        Patch patch = HanselCrashReporter.getPatch(MMTEventDB.class, "b", null);
        return patch != null ? (String) patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(MMTEventDB.class).setArguments(new Object[0]).toPatchJoinPoint()) : c;
    }

    @HanselInclude
    /* loaded from: classes.dex */
    public enum Table {
        EVENTS;

        final String b;

        public static Table valueOf(String str) {
            Patch patch = HanselCrashReporter.getPatch(Table.class, "valueOf", String.class);
            return patch != null ? (Table) patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(Table.class).setArguments(new Object[]{str}).toPatchJoinPoint()) : (Table) Enum.valueOf(Table.class, str);
        }

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Table[] valuesCustom() {
            Patch patch = HanselCrashReporter.getPatch(Table.class, "values", null);
            return patch != null ? (Table[]) patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(Table.class).setArguments(new Object[0]).toPatchJoinPoint()) : (Table[]) values().clone();
        }

        Table() {
            this.b = r3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @HanselInclude
    /* loaded from: classes.dex */
    public static class a extends SQLiteOpenHelper {

        /* renamed from: a, reason: collision with root package name */
        private final File f4353a;

        a(Context context, String str) {
            super(context, str, (SQLiteDatabase.CursorFactory) null, 5);
            this.f4353a = context.getDatabasePath(str);
        }

        public final void a() {
            Patch patch = HanselCrashReporter.getPatch(a.class, "a", null);
            if (patch == null) {
                close();
                this.f4353a.delete();
            } else {
                patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[0]).toPatchJoinPoint());
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onCreate(SQLiteDatabase sQLiteDatabase) {
            Patch patch = HanselCrashReporter.getPatch(a.class, "onCreate", SQLiteDatabase.class);
            if (patch == null) {
                sQLiteDatabase.execSQL(MMTEventDB.a());
                sQLiteDatabase.execSQL(MMTEventDB.b());
            } else {
                patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase}).toPatchJoinPoint());
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Patch patch = HanselCrashReporter.getPatch(a.class, "onUpgrade", SQLiteDatabase.class, Integer.TYPE, Integer.TYPE);
            if (patch == null) {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Table.EVENTS.b);
                sQLiteDatabase.execSQL(MMTEventDB.a());
                sQLiteDatabase.execSQL(MMTEventDB.b());
                return;
            }
            patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase, new Integer(i), new Integer(i2)}).toPatchJoinPoint());
        }
    }

    public MMTEventDB(Context context) {
        this(context, "mmt_event_db");
    }

    private MMTEventDB(Context context, String str) {
        this.f4351a = new a(context, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00f5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final int a(org.json.JSONObject r9, com.mmt.travel.app.common.tracker.MMTEventDB.Table r10, java.lang.String r11) {
        /*
            Method dump skipped, instructions count: 261
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mmt.travel.app.common.tracker.MMTEventDB.a(org.json.JSONObject, com.mmt.travel.app.common.tracker.MMTEventDB$Table, java.lang.String):int");
    }

    public final void a(String str, Table table, String str2) {
        Patch patch = HanselCrashReporter.getPatch(MMTEventDB.class, "a", String.class, Table.class, String.class);
        if (patch != null) {
            patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{str, table, str2}).toPatchJoinPoint());
            return;
        }
        String str3 = table.b;
        try {
            this.f4351a.getWritableDatabase().delete(str3, "_id <= " + str + " AND event_type='" + str2 + "'", null);
        } catch (SQLiteException e) {
            new StringBuilder("cleanupEvents ").append(str3).append(" by id FAILED. Deleting DB.");
            LogUtils.a(e);
            this.f4351a.a();
        } finally {
            this.f4351a.close();
        }
    }

    public final void a(Table table, String str) {
        Patch patch = HanselCrashReporter.getPatch(MMTEventDB.class, "a", Table.class, String.class);
        if (patch != null) {
            patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{table, str}).toPatchJoinPoint());
            return;
        }
        String str2 = table.b;
        try {
            this.f4351a.getWritableDatabase().delete(str2, "event_type='" + str + "'", null);
        } catch (SQLiteException e) {
            new StringBuilder("cleanupEvents ").append(str2).append(" for type:").append(str);
            LogUtils.a(e);
            this.f4351a.a();
        } finally {
            this.f4351a.close();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00d1 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0138 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r3v2, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v7, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r3v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.String[] b(com.mmt.travel.app.common.tracker.MMTEventDB.Table r10, java.lang.String r11) {
        /*
            Method dump skipped, instructions count: 350
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mmt.travel.app.common.tracker.MMTEventDB.b(com.mmt.travel.app.common.tracker.MMTEventDB$Table, java.lang.String):java.lang.String[]");
    }
}
