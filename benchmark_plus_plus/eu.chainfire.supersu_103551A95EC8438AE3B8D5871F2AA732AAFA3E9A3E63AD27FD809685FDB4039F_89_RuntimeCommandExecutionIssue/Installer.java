package eu.chainfire.supersu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StatFs;
import android.preference.PreferenceManager;
import java.io.File;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class Installer {
    public int a = -3;
    public int b = -3;
    public int c = -3;
    public int d = -3;
    public boolean e = false;
    public boolean f = false;
    public boolean g = false;

    /* loaded from: classes.dex */
    public enum UninstallMode {
        UNINSTALL_NORMAL,
        UNINSTALL_FULL_UNROOT,
        UNINSTALL_SWITCH_SU_APP,
        UNINSTALL_MARKET_REINSTALL;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static UninstallMode[] valuesCustom() {
            UninstallMode[] valuesCustom = values();
            int length = valuesCustom.length;
            UninstallMode[] uninstallModeArr = new UninstallMode[length];
            System.arraycopy(valuesCustom, 0, uninstallModeArr, 0, length);
            return uninstallModeArr;
        }
    }

    public static boolean c() {
        try {
            StatFs statFs = new StatFs("/system/xbin");
            return statFs.getBlockSize() * statFs.getAvailableBlocks() >= 500000;
        } catch (Exception e) {
            return true;
        }
    }

    public final void a(Context context) {
        this.a = 89;
        this.b = SuperUser.a("su");
        this.c = SuperUser.a("/system/bin/.ext/.su");
        this.d = SuperUser.a("/system/.bash/.ssu");
        this.e = this.b < this.a;
        this.g = this.c < this.a;
        this.f = !PreferenceManager.getDefaultSharedPreferences(context).getBoolean("superuser", true);
        this.e = this.e && !this.f;
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [eu.chainfire.supersu.Installer$1] */
    public final void a(final Context context, boolean z, final Runnable runnable) {
        final Boolean valueOf = Boolean.valueOf(z);
        new AsyncTask() { // from class: eu.chainfire.supersu.Installer.1
            private ProgressDialog b = null;

            private Integer a() {
                try {
                    Thread.sleep(100L);
                } catch (Exception e) {
                }
                return Installer.this.b(context) ? 1 : 0;
            }

            @Override // android.os.AsyncTask
            protected /* synthetic */ Object doInBackground(Object... objArr) {
                return a();
            }

            @Override // android.os.AsyncTask
            protected /* synthetic */ void onPostExecute(Object obj) {
                Integer num = (Integer) obj;
                try {
                    this.b.dismiss();
                    if (valueOf.booleanValue() || num.intValue() == 0) {
                        (Build.VERSION.SDK_INT >= 11 ? new AlertDialog.Builder(context, 2131361859) : new AlertDialog.Builder(context)).setTitle("SuperSU").setMessage(num.intValue() == 1 ? R.string.install_binary_success : R.string.install_binary_failure).setNeutralButton(R.string.generic_ok, (DialogInterface.OnClickListener) null).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                try {
                    if (Build.VERSION.SDK_INT >= 11) {
                        this.b = new ProgressDialog(context, 2131361859);
                    } else {
                        this.b = new ProgressDialog(context);
                    }
                    this.b.setIndeterminate(true);
                    this.b.setCancelable(false);
                    this.b.setTitle("SuperSU");
                    this.b.setMessage(context.getResources().getString(R.string.install_binary_progress));
                    this.b.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(new Integer[0]);
    }

    public final boolean a() {
        return this.e || this.g;
    }

    public final boolean a(Context context, UninstallMode uninstallMode) {
        if (uninstallMode == UninstallMode.UNINSTALL_NORMAL) {
            SuperUser.a("/system/bin/.ext/.su", new String[]{"mount -o rw,remount /system /system", "rm /system/bin/su", "rm /system/xbin/su", "mount -o ro,remount /system /system"});
            return SuperUser.a("su") == -2 && SuperUser.a("/system/bin/.ext/.su") >= -1;
        }
        a(context);
        this.f = false;
        b(context);
        a(context);
        String a = Asset.a(context, "chattr.png");
        if (a == null || a.equals("") || !new File(a).exists()) {
            return false;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("mount -o rw,remount /system /system");
        arrayList.add(String.format("chmod 0755 %s", a));
        if (uninstallMode != UninstallMode.UNINSTALL_MARKET_REINSTALL) {
            arrayList.add(String.format("rm %s", "/system/.pin"));
        }
        if (uninstallMode == UninstallMode.UNINSTALL_FULL_UNROOT) {
            arrayList.add(String.format("%s -i %s", a, "/system/bin/su"));
            arrayList.add("rm /system/bin/su");
            arrayList.add(String.format("%s -i %s", a, "/system/xbin/su"));
            arrayList.add("rm /system/xbin/su");
        }
        arrayList.add(String.format("%s -i %s", a, "/system/.bash/.ssu"));
        arrayList.add("rm /system/.bash/.ssu");
        arrayList.add("rm /system/.bash/*");
        arrayList.add("rm -rf /system/.bash");
        arrayList.add("rmdir /system/.bash");
        arrayList.add(String.format("%s -i %s", a, "/system/su-backup"));
        arrayList.add("rm /system/su-backup");
        arrayList.add(String.format("%s -i %s", a, "/system/bin/.ext/.su"));
        arrayList.add("rm /system/bin/.ext/.su");
        arrayList.add("rm /system/bin/.ext/*");
        arrayList.add("rm -rf /system/bin/.ext");
        arrayList.add("rmdir /system/bin/.ext");
        arrayList.add("rm /system/app/superuser.apk");
        arrayList.add("rm /system/app/Superuser.apk");
        arrayList.add("rm /system/app/SuperUser.apk");
        if (uninstallMode != UninstallMode.UNINSTALL_SWITCH_SU_APP) {
            arrayList.add("rm /data/app/com.noshufou.android.su.apk");
            arrayList.add("rm /data/app/com.noshufou.android.su-*.apk");
            arrayList.add("pm uninstall com.noshufou.android.su");
        }
        arrayList.add("rm /data/app/eu.chainfire.supersu.apk");
        arrayList.add("rm /data/app/eu.chainfire.supersu-*.apk");
        arrayList.add("pm uninstall eu.chainfire.supersu");
        arrayList.add(String.format("rm %s", a));
        arrayList.add("mount -o ro,remount /system /system");
        SuperUser.a(this.b < -1 ? "/system/bin/.ext/.su" : "su", arrayList);
        return true;
    }

    public final boolean a(Context context, boolean z) {
        String a = Asset.a(context, "chattr.png");
        if (a != null && !a.equals("") && new File(a).exists()) {
            String str = this.b < -1 ? "/system/bin/.ext/.su" : "su";
            ArrayList arrayList = new ArrayList();
            arrayList.add("mount -o rw,remount /system /system");
            arrayList.add(String.format("chmod 0755 %s", a));
            Object[] objArr = new Object[3];
            objArr[0] = a;
            objArr[1] = z ? "+i" : "-i";
            objArr[2] = "/system/bin/.ext/.su";
            arrayList.add(String.format("%s %s %s", objArr));
            arrayList.add(String.format("rm %s", a));
            arrayList.add("mount -o ro,remount /system /system");
            SuperUser.a(str, arrayList);
            return true;
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [eu.chainfire.supersu.Installer$2] */
    public final void b(final Context context, final UninstallMode uninstallMode) {
        new AsyncTask() { // from class: eu.chainfire.supersu.Installer.2
            private ProgressDialog b = null;

            private Integer a() {
                try {
                    Thread.sleep(100L);
                } catch (Exception e) {
                }
                return Installer.this.a(context, uninstallMode) ? 1 : 0;
            }

            @Override // android.os.AsyncTask
            protected /* synthetic */ Object doInBackground(Object... objArr) {
                return a();
            }

            @Override // android.os.AsyncTask
            protected /* synthetic */ void onPostExecute(Object obj) {
                Integer num = (Integer) obj;
                try {
                    this.b.dismiss();
                    if (num.intValue() == 0) {
                        (Build.VERSION.SDK_INT >= 11 ? new AlertDialog.Builder(context, 2131361859) : new AlertDialog.Builder(context)).setTitle("SuperSU").setMessage(R.string.uninstall_binary_failure).setNeutralButton(R.string.generic_ok, (DialogInterface.OnClickListener) null).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                try {
                    if (Build.VERSION.SDK_INT >= 11) {
                        this.b = new ProgressDialog(context, 2131361859);
                    } else {
                        this.b = new ProgressDialog(context);
                    }
                    this.b.setIndeterminate(true);
                    this.b.setCancelable(false);
                    this.b.setTitle("SuperSU");
                    this.b.setMessage(context.getResources().getString(R.string.uninstall_binary_progress));
                    this.b.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(new Integer[0]);
    }

    public final boolean b() {
        return this.b >= -1 || this.c >= -1 || this.d >= -1;
    }

    public final boolean b(Context context) {
        String a;
        String a2;
        if (!a()) {
            return true;
        }
        if (b() && c() && (a = Asset.a(context, "supersu.png")) != null && !a.equals("") && new File(a).exists() && (a2 = Asset.a(context, "chattr.png")) != null && !a2.equals("") && new File(a2).exists()) {
            String str = null;
            if (this.b >= -1) {
                str = "su";
            } else if (this.c >= -1) {
                str = "/system/bin/.ext/.su";
            } else if (this.d >= -1) {
                str = "/system/.bash/.ssu";
            }
            SuperUser.a(str, new String[]{"mount -o rw,remount /system /system", "mkdir /system/xbin", "chmod 755 /system/xbin", "chown 0.2000 /system/xbin", "chown 0:2000 /system/xbin", "chown root.shell /system/xbin", "chown root:shell /system/xbin", String.format("mkdir %s", "/data/data/eu.chainfire.supersu/requests/"), String.format("chown 0.0 %s", "/data/data/eu.chainfire.supersu/requests/"), String.format("chown 0:0 %s", "/data/data/eu.chainfire.supersu/requests/"), String.format("chown root.root %s", "/data/data/eu.chainfire.supersu/requests/"), String.format("chown root:root %s", "/data/data/eu.chainfire.supersu/requests/"), String.format("chmod 600 %s", "/data/data/eu.chainfire.supersu/requests/"), String.format("mkdir %s", "/data/data/eu.chainfire.supersu/logs/"), String.format("chown 0.0 %s", "/data/data/eu.chainfire.supersu/logs/"), String.format("chown 0:0 %s", "/data/data/eu.chainfire.supersu/logs/"), String.format("chown root.root %s", "/data/data/eu.chainfire.supersu/logs/"), String.format("chown root:root %s", "/data/data/eu.chainfire.supersu/logs/"), String.format("chmod 755 %s", "/data/data/eu.chainfire.supersu/logs/"), String.format("rm %s", "/system/.tmpsu"), String.format("cat %s > %s", a, "/system/.tmpsu"), String.format("chown 0.0 %s", "/system/.tmpsu"), String.format("chown 0:0 %s", "/system/.tmpsu"), String.format("chown root.root %s", "/system/.tmpsu"), String.format("chown root:root %s", "/system/.tmpsu"), String.format("chmod 06755 %s", "/system/.tmpsu"), String.format("chmod 0755 %s", a2)});
            ArrayList arrayList = new ArrayList();
            arrayList.add(String.format("%s -i %s", a2, "/system/bin/su"));
            arrayList.add("rm /system/bin/su");
            arrayList.add(String.format("%s -i %s", a2, "/system/xbin/su"));
            arrayList.add("rm /system/xbin/su");
            arrayList.add(String.format("%s -i %s", a2, "/system/.bash/.ssu"));
            arrayList.add("rm /system/.bash/.ssu");
            arrayList.add("rm /system/.bash/*");
            arrayList.add("rm -rf /system/.bash");
            arrayList.add("rmdir /system/.bash");
            arrayList.add(String.format("%s -i %s", a2, "/system/su-backup"));
            arrayList.add("rm /system/su-backup");
            arrayList.add(String.format("%s -i %s", a2, "/system/bin/.ext/.su"));
            arrayList.add("rm /system/bin/.ext/.su");
            arrayList.add("rm /system/bin/.ext/*");
            arrayList.add("rm -rf /system/bin/.ext");
            arrayList.add("rmdir /system/bin/.ext");
            if (!this.f) {
                arrayList.add(String.format("cat %s > %s", a, "/system/xbin/su"));
                arrayList.add(String.format("chown 0.0 %s", "/system/xbin/su"));
                arrayList.add(String.format("chown 0:0 %s", "/system/xbin/su"));
                arrayList.add(String.format("chown root.root %s", "/system/xbin/su"));
                arrayList.add(String.format("chown root:root %s", "/system/xbin/su"));
                arrayList.add(String.format("chmod 06755 %s", "/system/xbin/su"));
            }
            arrayList.add(String.format("mkdir %s", "/system/bin/.ext"));
            arrayList.add(String.format("chown 0.0 %s", "/system/bin/.ext"));
            arrayList.add(String.format("chown 0:0 %s", "/system/bin/.ext"));
            arrayList.add(String.format("chown root.root %s", "/system/bin/.ext"));
            arrayList.add(String.format("chown root:root %s", "/system/bin/.ext"));
            arrayList.add(String.format("chmod 0777 %s", "/system/bin/.ext"));
            arrayList.add(String.format("cat %s > %s", a, "/system/bin/.ext/.su"));
            arrayList.add(String.format("chown 0.0 %s", "/system/bin/.ext/.su"));
            arrayList.add(String.format("chown 0:0 %s", "/system/bin/.ext/.su"));
            arrayList.add(String.format("chown root.root %s", "/system/bin/.ext/.su"));
            arrayList.add(String.format("chown root:root %s", "/system/bin/.ext/.su"));
            arrayList.add(String.format("chmod 06755 %s", "/system/bin/.ext/.su"));
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("survival", false)) {
                arrayList.add(String.format("%s +i %s", a2, "/system/bin/.ext/.su"));
            }
            arrayList.add(String.format("chmod 0711 %s", "/system/bin/.ext"));
            SuperUser.a("/system/.tmpsu", arrayList);
            SuperUser.a(!this.f ? "su" : "/system/bin/.ext/.su", new String[]{String.format("rm %s", "/system/.tmpsu"), String.format("rm %s", a2), "mount -o ro,remount /system /system"});
            return (SuperUser.a("su") >= 89 || this.f) && SuperUser.a("/system/bin/.ext/.su") >= 89;
        }
        return false;
    }
}
