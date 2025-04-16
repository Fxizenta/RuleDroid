package de.heinekingmedia.stashcat.f;

import android.content.Context;
import android.os.Build;
import de.heinekingmedia.stashcat.globals.App;
import java.util.concurrent.atomic.AtomicInteger;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

/* loaded from: classes2.dex */
public class d {

    /* renamed from: b, reason: collision with root package name */
    private static d f4999b;

    /* renamed from: c, reason: collision with root package name */
    private static SQLiteOpenHelper f5000c;

    /* renamed from: e, reason: collision with root package name */
    private static Context f5001e;

    /* renamed from: a, reason: collision with root package name */
    private AtomicInteger f5002a = new AtomicInteger();

    /* renamed from: d, reason: collision with root package name */
    private SQLiteDatabase f5003d;

    public static synchronized d a() {
        d dVar;
        synchronized (d.class) {
            if (f4999b == null) {
                throw new IllegalStateException(d.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
            }
            dVar = f4999b;
        }
        return dVar;
    }

    public static synchronized void a(Context context, SQLiteOpenHelper sQLiteOpenHelper) {
        synchronized (d.class) {
            if (f4999b == null) {
                SQLiteDatabase.loadLibs(context);
                f5001e = context;
                f4999b = new d();
                f5000c = sQLiteOpenHelper;
            }
        }
    }

    public synchronized SQLiteDatabase b() {
        if (this.f5002a.incrementAndGet() == 1) {
            String str = "";
            if (!"fat".equals("devel")) {
                String b2 = de.heinekingmedia.stashcat.utils.d.b(App.j().h().e(), Build.MANUFACTURER + Build.MODEL, 42L);
                String b3 = de.heinekingmedia.stashcat.utils.d.b(b2, App.j().E(), 1995L);
                if (de.heinekingmedia.stashcat.utils.l.a(f5001e)) {
                    str = de.heinekingmedia.stashcat.utils.l.a(f5001e, "stashcat", "db123456", "987654312", true);
                    de.heinekingmedia.stashcat.utils.l.a(f5001e, "stashcat", str, b3, b2);
                    de.heinekingmedia.stashcat.utils.l.b(f5001e);
                } else {
                    str = de.heinekingmedia.stashcat.utils.l.a(f5001e, "stashcat", b3, b2, false);
                }
            }
            try {
                this.f5003d = f5000c.getWritableDatabase(str);
            } catch (SQLiteException e2) {
                App.a().deleteDatabase("dbStashCat_v2.db");
                this.f5003d = f5000c.getWritableDatabase(str);
            }
        }
        return this.f5003d;
    }

    public synchronized void c() {
        if (this.f5002a.decrementAndGet() == 0 && this.f5003d != null) {
            this.f5003d.close();
        }
    }
}
