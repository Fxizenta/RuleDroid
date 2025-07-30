package com.samsung.android.fmm.common;

import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.Serializable;

/* loaded from: classes.dex */
public class b implements Serializable {

    /* renamed from: a, reason: collision with root package name */
    private static LocalLog f5049a = new LocalLog(300);

    /* renamed from: b, reason: collision with root package name */
    String f5050b;

    /* renamed from: c, reason: collision with root package name */
    private boolean f5051c = false;

    public b(String str) {
        String str2;
        if (str.length() > 20) {
            str2 = "DBG_FMM :[" + str + "]";
        } else {
            str2 = "DBG_FMM :[" + String.format("%-20s", str) + "]";
        }
        this.f5050b = str2;
    }

    public static void b(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        f5049a.dump(fileDescriptor, printWriter, strArr);
    }

    public void a(String str) {
        Log.i(this.f5050b, str);
    }

    public void c(String str) {
        g(str);
        f5049a.log(this.f5050b + "." + str);
    }

    public void d(Exception exc) {
        if (exc != null) {
            Log.e(this.f5050b, TextUtils.isEmpty(exc.getMessage()) ? "Exception message is null" : exc.getMessage(), exc);
        }
    }

    public void e(String str) {
        String str2 = this.f5050b;
        if (TextUtils.isEmpty(str)) {
            str = "Exception message is null";
        }
        Log.e(str2, str);
    }

    public void f(String str, Exception exc) {
        if (str != null) {
            Log.e(this.f5050b, str + " : ", exc);
        }
    }

    public void g(String str) {
        Log.i(this.f5050b, str);
    }

    public void h(String str) {
        Log.v(this.f5050b, str);
    }

    public void i(String str) {
        Log.w(this.f5050b, str);
    }

    public void j(String str, Exception exc) {
        Log.w(this.f5050b, str, exc);
    }
}
