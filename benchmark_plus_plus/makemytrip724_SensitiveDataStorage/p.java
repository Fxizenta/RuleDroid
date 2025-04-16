package com.mmt.travel.app.rightstay.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.mmt.travel.app.common.util.LogUtils;
import com.mmt.travel.app.rightstay.data.model.review.ReviewDBEntry;
import com.mmt.travel.app.rightstay.data.model.review.ReviewJobEntry;
import com.mmt.travel.app.rightstay.data.model.shortlisting.ShortlistEntry;
import io.hansel.pebbletracesdk.HanselCrashReporter;
import io.hansel.pebbletracesdk.annotations.HanselInclude;
import io.hansel.pebbletracesdk.codepatch.PatchJoinPoint;
import io.hansel.pebbletracesdk.codepatch.patch.Patch;

@HanselInclude
/* loaded from: classes2.dex */
public abstract class p extends SQLiteOpenHelper {

    /* renamed from: a, reason: collision with root package name */
    public boolean f8129a;

    public p(Context context, String str) {
        super(context, str, (SQLiteDatabase.CursorFactory) null, 2);
        try {
            getReadableDatabase();
        } catch (SQLiteException e) {
            LogUtils.b(e);
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onOpen(SQLiteDatabase sQLiteDatabase) {
        Patch patch = HanselCrashReporter.getPatch(p.class, "onOpen", SQLiteDatabase.class);
        if (patch == null) {
            this.f8129a = true;
        } else {
            patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase}).toPatchJoinPoint());
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Patch patch = HanselCrashReporter.getPatch(p.class, "onCreate", SQLiteDatabase.class);
        if (patch == null) {
            sQLiteDatabase.execSQL(ShortlistEntry.CREATE_TABLE);
            sQLiteDatabase.execSQL(ReviewDBEntry.a());
            sQLiteDatabase.execSQL(ReviewJobEntry.a());
            return;
        }
        patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase}).toPatchJoinPoint());
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Patch patch = HanselCrashReporter.getPatch(p.class, "onUpgrade", SQLiteDatabase.class, Integer.TYPE, Integer.TYPE);
        if (patch == null) {
            if (i == 1 && i2 == 2) {
                sQLiteDatabase.execSQL(ReviewJobEntry.a());
                sQLiteDatabase.execSQL(ReviewDBEntry.a());
                return;
            }
            return;
        }
        patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase, new Integer(i), new Integer(i2)}).toPatchJoinPoint());
    }
}
