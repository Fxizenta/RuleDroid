package o9;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;
import androidx.cardview.widget.CardView;
import com.airbnb.lottie.R;
import ea.h;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import k8.c;
import k8.i;
import n9.k;
import o.d;
import org.json.JSONArray;
import org.json.JSONObject;
import p4.b5;
import p4.e;
import p4.e4;
import p4.g;
import p4.o;
import p4.ra;
import u2.f;
import u4.v2;
import u4.w2;
import u4.x2;
import y9.m;

/* loaded from: classes.dex */
public class b implements f, i, o.b, v2 {

    /* renamed from: l, reason: collision with root package name */
    public static final /* synthetic */ b f6802l = new b();

    public /* synthetic */ b() {
    }

    public /* synthetic */ b(c cVar) {
    }

    public static String A(Context context, long j9) {
        int c = k.c(context);
        Locale locale = r2.c.e(context).c;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(q(locale) + " yyyy", locale);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date) + "\n" + I(j9, context.getResources().getStringArray(R.array.week_simple)) + ", " + x(context, j9, c, locale);
    }

    public static String B(Context context, long j9) {
        Calendar calendar = Calendar.getInstance();
        int i10 = calendar.get(1);
        calendar.setTimeInMillis(j9);
        int i11 = calendar.get(1);
        Locale locale = r2.c.e(context).c;
        StringBuilder sb = new StringBuilder();
        sb.append(q(locale));
        sb.append(i10 == i11 ? "" : " yyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sb.toString(), locale);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date);
    }

    public static String C(Context context, int i10, int i11) {
        SimpleDateFormat simpleDateFormat;
        Calendar E = E(context);
        E.set(11, i10);
        E.set(12, i11);
        int c = k.c(context);
        if ((c == 0 && DateFormat.is24HourFormat(context)) || c == 2) {
            simpleDateFormat = new SimpleDateFormat("HH:mm", r2.c.e(context).c);
        } else {
            simpleDateFormat = new SimpleDateFormat("hh:mm a", r2.c.e(context).c);
        }
        return simpleDateFormat.format(E.getTime());
    }

    public static String D(long j9) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date);
    }

    public static Calendar E(Context context) {
        Calendar calendar = Calendar.getInstance();
        int u10 = u(context);
        calendar.setFirstDayOfWeek(u10 != 1 ? u10 == 2 ? 1 : 7 : 2);
        calendar.setMinimalDaysInFirstWeek(7);
        return calendar;
    }

    public static long H() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static String I(long j9, String[] strArr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j9);
        switch (calendar.get(7)) {
            case 1:
                return strArr[0];
            case 2:
                return strArr[1];
            case 3:
                return strArr[2];
            case 4:
                return strArr[3];
            case 5:
                return strArr[4];
            case 6:
                return strArr[5];
            case 7:
                return strArr[6];
            default:
                return "";
        }
    }

    public static final boolean J(Context context) {
        NetworkInfo activeNetworkInfo;
        u4.c.r(context, "context");
        Object systemService = context.getApplicationContext().getSystemService("connectivity");
        if (!(systemService instanceof ConnectivityManager)) {
            systemService = null;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) systemService;
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return false;
        }
        return activeNetworkInfo.isAvailable();
    }

    public static final List K(Object... objArr) {
        if (objArr.length > 0) {
            List asList = Arrays.asList(objArr);
            u4.c.p(asList, "asList(this)");
            return asList;
        }
        return h.f4292l;
    }

    public static RuntimeException L(Throwable th) {
        Object obj = o6.h.f6799a;
        if (th instanceof RuntimeException) {
            throw ((RuntimeException) th);
        }
        if (!(th instanceof Error)) {
            throw new RuntimeException(th);
        }
        throw ((Error) th);
    }

    public static final void N() {
        throw new ArithmeticException("Index overflow has happened.");
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0090  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x008a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean O(android.content.Context r11, w9.b r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 264
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.O(android.content.Context, w9.b, boolean):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0050 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0052 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean Q(android.content.Context r5, android.content.ContentValues r6, int r7) {
        /*
            r0 = 0
            o9.a r1 = new o9.a     // Catch: java.lang.Throwable -> L34 java.lang.Exception -> L38
            r1.<init>(r5)     // Catch: java.lang.Throwable -> L34 java.lang.Exception -> L38
            android.database.sqlite.SQLiteDatabase r5 = r1.getWritableDatabase()     // Catch: java.lang.Throwable -> L2f java.lang.Exception -> L32
            java.lang.String r2 = "user"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2c
            r3.<init>()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2c
            java.lang.String r4 = "uid="
            r3.append(r4)     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2c
            r3.append(r7)     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2c
            java.lang.String r7 = r3.toString()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2c
            int r6 = r5.update(r2, r6, r7, r0)     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2c
            long r6 = (long) r6
            r5.close()
            r1.close()
            goto L4a
        L29:
            r6 = move-exception
            r0 = r5
            goto L54
        L2c:
            r6 = move-exception
            r0 = r5
            goto L3b
        L2f:
            r5 = move-exception
            r6 = r5
            goto L54
        L32:
            r6 = move-exception
            goto L3b
        L34:
            r5 = move-exception
            r6 = r5
            r1 = r0
            goto L54
        L38:
            r5 = move-exception
            r6 = r5
            r1 = r0
        L3b:
            r6.printStackTrace()     // Catch: java.lang.Throwable -> L2f
            if (r0 == 0) goto L43
            r0.close()
        L43:
            if (r1 == 0) goto L48
            r1.close()
        L48:
            r6 = -1
        L4a:
            r0 = 0
            int r5 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r5 <= 0) goto L52
            r5 = 1
            goto L53
        L52:
            r5 = 0
        L53:
            return r5
        L54:
            if (r0 == 0) goto L59
            r0.close()
        L59:
            if (r1 == 0) goto L5e
            r1.close()
        L5e:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.Q(android.content.Context, android.content.ContentValues, int):boolean");
    }

    public static e R(e eVar, e4 e4Var, p4.i iVar, Boolean bool, Boolean bool2) {
        e eVar2 = new e();
        Iterator r10 = eVar.r();
        while (r10.hasNext()) {
            int intValue = ((Integer) r10.next()).intValue();
            if (eVar.v(intValue)) {
                o a10 = iVar.a(e4Var, Arrays.asList(eVar.p(intValue), new p4.h(Double.valueOf(intValue)), eVar));
                if (a10.h().equals(bool)) {
                    return eVar2;
                }
                if (bool2 == null || a10.h().equals(bool2)) {
                    eVar2.u(intValue, a10);
                }
            }
        }
        return eVar2;
    }

    public static o S(e eVar, e4 e4Var, List list, boolean z10) {
        o oVar;
        b5.i("reduce", 1, list);
        b5.j("reduce", 2, list);
        o b10 = e4Var.b((o) list.get(0));
        if (!(b10 instanceof p4.i)) {
            throw new IllegalArgumentException("Callback should be a method");
        }
        if (list.size() == 2) {
            oVar = e4Var.b((o) list.get(1));
            if (oVar instanceof g) {
                throw new IllegalArgumentException("Failed to parse initial value");
            }
        } else {
            if (eVar.n() == 0) {
                throw new IllegalStateException("Empty array with no initial value error");
            }
            oVar = null;
        }
        p4.i iVar = (p4.i) b10;
        int n = eVar.n();
        int i10 = z10 ? 0 : n - 1;
        int i11 = z10 ? n - 1 : 0;
        int i12 = true == z10 ? 1 : -1;
        if (oVar == null) {
            oVar = eVar.p(i10);
            i10 += i12;
        }
        while ((i11 - i10) * i12 >= 0) {
            if (eVar.v(i10)) {
                oVar = iVar.a(e4Var, Arrays.asList(oVar, eVar.p(i10), new p4.h(Double.valueOf(i10)), eVar));
                if (oVar instanceof g) {
                    throw new IllegalStateException("Reduce operation failed");
                }
                i10 += i12;
            } else {
                i10 += i12;
            }
        }
        return oVar;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x008e  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00ab  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00b6  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00bb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean c(android.content.Context r11, w9.b r12, boolean r13, boolean r14) {
        /*
            java.lang.String r0 = "DBUtils"
            java.lang.String r1 = "addDiary"
            h4.a.k(r0, r1)
            r1 = -1
            r3 = 0
            o9.a r4 = new o9.a     // Catch: java.lang.Throwable -> L73 java.lang.Exception -> L76
            r4.<init>(r11)     // Catch: java.lang.Throwable -> L73 java.lang.Exception -> L76
            android.database.sqlite.SQLiteDatabase r5 = r4.getWritableDatabase()     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
            android.content.ContentValues r6 = new android.content.ContentValues     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r6.<init>()     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            if (r13 != 0) goto L22
            long r7 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r12.f9706x = r7     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r12.y = r7     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
        L22:
            java.lang.String r7 = "uid"
            long r8 = r12.f9697m     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.Long r8 = java.lang.Long.valueOf(r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r6.put(r7, r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.String r7 = "date"
            long r8 = r12.n     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.Long r8 = java.lang.Long.valueOf(r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r6.put(r7, r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.String r7 = "title"
            java.lang.String r8 = r12.h()     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r6.put(r7, r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.String r7 = "content"
            java.lang.String r8 = r12.b()     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r6.put(r7, r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.String r7 = "data"
            java.lang.String r8 = r12.c()     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            r6.put(r7, r8)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            java.lang.String r7 = "diary"
            long r6 = r5.insert(r7, r3, r6)     // Catch: java.lang.Exception -> L6a java.lang.Throwable -> Lb2
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 == 0) goto L63
            r12.f9696l = r6     // Catch: java.lang.Exception -> L60 java.lang.Throwable -> Lb2
            goto L63
        L60:
            r3 = move-exception
            r1 = r6
            goto L7a
        L63:
            r5.close()
            r4.close()
            goto L88
        L6a:
            r3 = move-exception
            goto L7a
        L6c:
            r11 = move-exception
            goto Lb4
        L6e:
            r5 = move-exception
            r10 = r5
            r5 = r3
            r3 = r10
            goto L7a
        L73:
            r11 = move-exception
            r4 = r3
            goto Lb4
        L76:
            r4 = move-exception
            r5 = r3
            r3 = r4
            r4 = r5
        L7a:
            r3.printStackTrace()     // Catch: java.lang.Throwable -> Lb2
            if (r5 == 0) goto L82
            r5.close()
        L82:
            if (r4 == 0) goto L87
            r4.close()
        L87:
            r6 = r1
        L88:
            r1 = 0
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 <= 0) goto Lab
            if (r13 != 0) goto L95
            long r1 = r12.f9706x
            q9.e.a(r11, r1)
        L95:
            e(r11, r12)
            if (r14 == 0) goto La4
            android.content.Intent r12 = new android.content.Intent
            java.lang.String r13 = "diary.journal.lock.mood.daily.db.ACTION_UPDATE_DIARY_LIST"
            r12.<init>(r13)
            r11.sendBroadcast(r12)
        La4:
            java.lang.String r11 = "addDiary result: true"
            h4.a.k(r0, r11)
            r11 = 1
            return r11
        Lab:
            java.lang.String r11 = "addDiary result: false"
            h4.a.k(r0, r11)
            r11 = 0
            return r11
        Lb2:
            r11 = move-exception
            r3 = r5
        Lb4:
            if (r3 == 0) goto Lb9
            r3.close()
        Lb9:
            if (r4 == 0) goto Lbe
            r4.close()
        Lbe:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.c(android.content.Context, w9.b, boolean, boolean):boolean");
    }

    public static void e(Context context, w9.b bVar) {
        int size = n9.i.b().a(context).size();
        int i10 = 0;
        for (int i11 = 0; i11 < size; i11++) {
            if (n9.i.b().a(context).get(i11).f9696l == bVar.f9696l) {
                return;
            }
        }
        if (k.f(context)) {
            if (size != 0 && bVar.n >= n9.i.b().a(context).get(size - 1).n) {
                if (bVar.n <= n9.i.b().a(context).get(0).n) {
                    while (i10 < size) {
                        if (bVar.n < n9.i.b().a(context).get(i10).n) {
                            i10++;
                        }
                    }
                    return;
                }
                n9.i.b().a(context).add(i10, bVar);
                return;
            }
            n9.i.b().a(context).add(bVar);
        }
        if (size != 0 && bVar.n <= n9.i.b().a(context).get(size - 1).n) {
            if (bVar.n >= n9.i.b().a(context).get(0).n) {
                while (i10 < size) {
                    if (bVar.n > n9.i.b().a(context).get(i10).n) {
                        i10++;
                    }
                }
                return;
            }
            n9.i.b().a(context).add(i10, bVar);
            return;
        }
        n9.i.b().a(context).add(bVar);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x006f A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0071 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean f(android.content.Context r7) {
        /*
            java.lang.String r0 = "diary"
            java.lang.String r1 = "DBUtils"
            java.lang.String r2 = "deleteAllDiary"
            h4.a.k(r1, r2)
            r2 = 0
            r3 = -1
            o9.a r4 = new o9.a     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5c
            r4.<init>(r7)     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5c
            android.database.sqlite.SQLiteDatabase r5 = r4.getWritableDatabase()     // Catch: java.lang.Throwable -> L55 java.lang.Exception -> L57
            r5.beginTransaction()     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L52
            java.lang.String r6 = ""
            int r2 = r5.delete(r0, r6, r2)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L52
            if (r2 < 0) goto L45
            r5.setTransactionSuccessful()     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            java.lang.String r3 = "sqlite_sequence"
            java.lang.String r6 = "name = ?"
            java.lang.String[] r0 = new java.lang.String[]{r0}     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            int r0 = r5.delete(r3, r6, r0)     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            r3.<init>()     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            java.lang.String r6 = "deleteAllDiary result: "
            r3.append(r6)     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            r3.append(r0)     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            java.lang.String r0 = r3.toString()     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            h4.a.k(r1, r0)     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            y9.m.b(r7)     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
        L45:
            r5.endTransaction()     // Catch: java.lang.Exception -> L4c java.lang.Throwable -> L4f
            r5.close()
            goto L69
        L4c:
            r7 = move-exception
            r3 = r2
            goto L53
        L4f:
            r7 = move-exception
            r2 = r5
            goto L73
        L52:
            r7 = move-exception
        L53:
            r2 = r5
            goto L5e
        L55:
            r7 = move-exception
            goto L73
        L57:
            r7 = move-exception
            goto L5e
        L59:
            r7 = move-exception
            r4 = r2
            goto L73
        L5c:
            r7 = move-exception
            r4 = r2
        L5e:
            r7.printStackTrace()     // Catch: java.lang.Throwable -> L55
            if (r2 == 0) goto L66
            r2.close()
        L66:
            if (r4 == 0) goto L6d
            r2 = r3
        L69:
            r4.close()
            r3 = r2
        L6d:
            if (r3 <= 0) goto L71
            r7 = 1
            goto L72
        L71:
            r7 = 0
        L72:
            return r7
        L73:
            if (r2 == 0) goto L78
            r2.close()
        L78:
            if (r4 == 0) goto L7d
            r4.close()
        L7d:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.f(android.content.Context):boolean");
    }

    public static void i(Context context, w9.b bVar) {
        try {
            ArrayList arrayList = new ArrayList();
            JSONArray jSONArray = new JSONArray(bVar.b());
            for (int i10 = 0; i10 < jSONArray.length(); i10++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i10);
                int optInt = jSONObject.optInt("type");
                String optString = jSONObject.optString("value");
                if (optInt == 1) {
                    arrayList.add(new JSONObject(optString).optString("localeName", ""));
                } else if (optInt == 2) {
                    JSONArray jSONArray2 = new JSONArray(optString);
                    for (int i11 = 0; i11 < jSONArray2.length(); i11++) {
                        arrayList.add(jSONArray2.getJSONObject(i11).optString("localeName", ""));
                    }
                }
            }
            if (l(context, bVar.f9696l)) {
                for (int i12 = 0; i12 < arrayList.size(); i12++) {
                    m.c(m.e(context, bVar, (String) arrayList.get(i12)).getAbsolutePath());
                }
            }
        } catch (Exception e9) {
            e9.printStackTrace();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0041  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean l(android.content.Context r9, long r10) {
        /*
            java.lang.String r0 = "DBUtils"
            java.lang.String r1 = "deleteDiary"
            h4.a.k(r0, r1)
            r1 = 0
            r2 = 1
            r3 = 0
            o9.a r4 = new o9.a     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            r4.<init>(r9)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            android.database.sqlite.SQLiteDatabase r3 = r4.getWritableDatabase()     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L50
            java.lang.String r5 = "diary"
            java.lang.String r6 = "_id = ?"
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L50
            java.lang.String r8 = java.lang.String.valueOf(r10)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L50
            r7[r1] = r8     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L50
            int r5 = r3.delete(r5, r6, r7)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L50
            r3.close()
            r4.close()
            goto L3f
        L2a:
            r5 = move-exception
            goto L31
        L2c:
            r9 = move-exception
            r4 = r3
            goto L51
        L2f:
            r5 = move-exception
            r4 = r3
        L31:
            r5.printStackTrace()     // Catch: java.lang.Throwable -> L50
            if (r3 == 0) goto L39
            r3.close()
        L39:
            if (r4 == 0) goto L3e
            r4.close()
        L3e:
            r5 = -1
        L3f:
            if (r5 <= 0) goto L4a
            n(r9, r10)
            java.lang.String r9 = "deleteDiary result: true"
            h4.a.k(r0, r9)
            return r2
        L4a:
            java.lang.String r9 = "deleteDiary result: false"
            h4.a.k(r0, r9)
            return r1
        L50:
            r9 = move-exception
        L51:
            if (r3 == 0) goto L56
            r3.close()
        L56:
            if (r4 == 0) goto L5b
            r4.close()
        L5b:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.l(android.content.Context, long):boolean");
    }

    public static void n(Context context, long j9) {
        int size = n9.i.b().a(context).size();
        int i10 = 0;
        for (int i11 = 0; i11 < size; i11++) {
            w9.b bVar = n9.i.b().a(context).get(i11);
            if (j9 == bVar.f9696l) {
                long j10 = bVar.f9706x;
                try {
                    JSONArray jSONArray = new JSONArray(q9.e.i(context));
                    while (true) {
                        if (i10 >= jSONArray.length()) {
                            i10 = -1;
                            break;
                        } else if (j10 == jSONArray.getLong(i10)) {
                            break;
                        } else {
                            i10++;
                        }
                    }
                    if (i10 != -1) {
                        jSONArray.remove(i10);
                        q9.e.l(context, jSONArray.toString());
                    }
                } catch (Exception e9) {
                    e9.printStackTrace();
                }
                n9.i.b().a(context).remove(i11);
                return;
            }
        }
    }

    public static String[] o(Context context) {
        String[] strArr = new String[2];
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a", r2.c.e(context).c);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(11, 6);
            strArr[0] = simpleDateFormat.format(calendar.getTime());
            calendar.set(11, 20);
            strArr[1] = simpleDateFormat.format(calendar.getTime());
        } catch (Exception unused) {
            strArr[0] = "AM";
            strArr[1] = "PM";
        }
        return strArr;
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b2, code lost:
    
        if (r4 != null) goto L21;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static synchronized java.util.ArrayList p(android.content.Context r23) {
        /*
            java.lang.Class<o9.b> r1 = o9.b.class
            monitor-enter(r1)
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch: java.lang.Throwable -> Lc8
            r2.<init>()     // Catch: java.lang.Throwable -> Lc8
            r3 = 0
            o9.a r4 = new o9.a     // Catch: java.lang.Throwable -> L9e java.lang.Exception -> La2
            r0 = r23
            r4.<init>(r0)     // Catch: java.lang.Throwable -> L9e java.lang.Exception -> La2
            android.database.sqlite.SQLiteDatabase r13 = r4.getReadableDatabase()     // Catch: java.lang.Throwable -> L98 java.lang.Exception -> L9b
            boolean r0 = n9.k.f(r23)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            if (r0 == 0) goto L1d
            java.lang.String r0 = "desc"
            goto L1f
        L1d:
            java.lang.String r0 = "asc"
        L1f:
            java.lang.String r6 = "diary"
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            r5.<init>()     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r12 = "date "
            r5.append(r12)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            r5.append(r0)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r12 = r5.toString()     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            r5 = r13
            android.database.Cursor r3 = r5.query(r6, r7, r8, r9, r10, r11, r12)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            if (r3 == 0) goto L8a
        L3e:
            boolean r0 = r3.moveToNext()     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            if (r0 == 0) goto L8a
            w9.b r0 = new w9.b     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r5 = "_id"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            long r15 = r3.getLong(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r5 = "uid"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            int r17 = r3.getInt(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r5 = "date"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            long r18 = r3.getLong(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r5 = "title"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r20 = r3.getString(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r5 = "content"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r21 = r3.getString(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r5 = "data"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            java.lang.String r22 = r3.getString(r5)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            r14 = r0
            r14.<init>(r15, r17, r18, r20, r21, r22)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            r2.add(r0)     // Catch: java.lang.Exception -> L96 java.lang.Throwable -> Lb7
            goto L3e
        L8a:
            if (r3 == 0) goto L8f
            r3.close()     // Catch: java.lang.Throwable -> Lc8
        L8f:
            r13.close()     // Catch: java.lang.Throwable -> Lc8
        L92:
            r4.close()     // Catch: java.lang.Throwable -> Lc8
            goto Lb5
        L96:
            r0 = move-exception
            goto La5
        L98:
            r0 = move-exception
            r13 = r3
            goto Lb8
        L9b:
            r0 = move-exception
            r13 = r3
            goto La5
        L9e:
            r0 = move-exception
            r4 = r3
            r13 = r4
            goto Lb8
        La2:
            r0 = move-exception
            r4 = r3
            r13 = r4
        La5:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> Lb7
            if (r3 == 0) goto Lad
            r3.close()     // Catch: java.lang.Throwable -> Lc8
        Lad:
            if (r13 == 0) goto Lb2
            r13.close()     // Catch: java.lang.Throwable -> Lc8
        Lb2:
            if (r4 == 0) goto Lb5
            goto L92
        Lb5:
            monitor-exit(r1)
            return r2
        Lb7:
            r0 = move-exception
        Lb8:
            if (r3 == 0) goto Lbd
            r3.close()     // Catch: java.lang.Throwable -> Lc8
        Lbd:
            if (r13 == 0) goto Lc2
            r13.close()     // Catch: java.lang.Throwable -> Lc8
        Lc2:
            if (r4 == 0) goto Lc7
            r4.close()     // Catch: java.lang.Throwable -> Lc8
        Lc7:
            throw r0     // Catch: java.lang.Throwable -> Lc8
        Lc8:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.p(android.content.Context):java.util.ArrayList");
    }

    public static String q(Locale locale) {
        return DateFormat.getBestDateTimePattern(locale, locale.getLanguage().toLowerCase().equals("zh") ? "MMMM" : "MMM");
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00eb  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00f0  */
    /* JADX WARN: Removed duplicated region for block: B:43:? A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static w9.j s(android.content.Context r13) {
        /*
            Method dump skipped, instructions count: 244
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: o9.b.s(android.content.Context):w9.j");
    }

    public static long t(long j9) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j9);
        return ((calendar.get(2) + 1) * 100) + (calendar.get(1) * 10000) + calendar.get(5);
    }

    public static int u(Context context) {
        int i10 = k.a(context).getInt("first_day_of_week", -1);
        if (i10 != -1) {
            return i10;
        }
        int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
        if (firstDayOfWeek == 2) {
            return 1;
        }
        return firstDayOfWeek == 7 ? 3 : 2;
    }

    public static String v(Context context, long j9) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", r2.c.e(context).c);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date);
    }

    public static String w(Context context, long j9) {
        Locale locale = r2.c.e(context).c;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(v(context, j9) + " " + q(locale) + " yyyy", locale);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date);
    }

    public static String x(Context context, long j9, int i10, Locale locale) {
        SimpleDateFormat simpleDateFormat = ((i10 == 0 && DateFormat.is24HourFormat(context)) || i10 == 2) ? new SimpleDateFormat("HH:mm", locale) : new SimpleDateFormat("hh:mm aa", locale);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date);
    }

    public static String y(Context context, long j9) {
        Locale locale = r2.c.e(context).c;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(q(locale) + " yyyy", locale);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date);
    }

    public static String z(Context context, long j9) {
        Locale locale = r2.c.e(context).c;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(q(locale) + " yyyy", locale);
        Date date = new Date();
        date.setTime(j9);
        return simpleDateFormat.format(date) + "\n" + I(j9, context.getResources().getStringArray(R.array.weeks_full));
    }

    public float F(o.a aVar) {
        return r(aVar).f6587e;
    }

    public float G(o.a aVar) {
        return r(aVar).f6584a;
    }

    public void M(o.a aVar, float f10) {
        o.c r10 = r(aVar);
        CardView.a aVar2 = (CardView.a) aVar;
        boolean useCompatPadding = aVar2.f940b.getUseCompatPadding();
        boolean a10 = aVar2.a();
        if (f10 != r10.f6587e || r10.f6588f != useCompatPadding || r10.f6589g != a10) {
            r10.f6587e = f10;
            r10.f6588f = useCompatPadding;
            r10.f6589g = a10;
            r10.c(null);
            r10.invalidateSelf();
        }
        P(aVar);
    }

    public void P(o.a aVar) {
        CardView.a aVar2 = (CardView.a) aVar;
        if (!aVar2.f940b.getUseCompatPadding()) {
            aVar2.b(0, 0, 0, 0);
            return;
        }
        float f10 = r(aVar).f6587e;
        float f11 = r(aVar).f6584a;
        int ceil = (int) Math.ceil(d.a(f10, f11, aVar2.a()));
        int ceil2 = (int) Math.ceil(d.b(f10, f11, aVar2.a()));
        aVar2.b(ceil, ceil2, ceil, ceil2);
    }

    @Override // u4.v2
    public Object a() {
        w2 w2Var = x2.f9178b;
        return Long.valueOf(ra.f7247m.a().d());
    }

    @Override // u2.f
    public void b(String str) {
        Log.e("IapManager", "error: " + str);
    }

    @Override // u2.a
    public void h(String str) {
        Log.e("IapManager", "initFailed: " + str);
    }

    @Override // u2.f
    public void j(List list) {
        Log.i("IapManager", "query in app sku details: " + list);
        if (list == null || !(!list.isEmpty())) {
            return;
        }
        k1.a.f5374f.g(list);
    }

    @Override // k8.i
    public Object k() {
        return new LinkedHashSet();
    }

    public o.c r(o.a aVar) {
        return (o.c) ((CardView.a) aVar).f939a;
    }
}
