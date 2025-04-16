package com.mayank.rucky.activity;

import a2.d;
import a2.e;
import a2.h;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.a;
import androidx.biometric.BiometricPrompt;
import com.google.android.material.snackbar.Snackbar;
import com.mayank.rucky.R;
import com.mayank.rucky.activity.EditorActivity;
import com.mayank.rucky.service.SocketHeartbeatService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import v.g;
import w1.z;
import x0.o;
import x0.p;
import x0.u;
import y0.k;
import y0.l;
import y0.p;
import y0.r;

/* loaded from: classes.dex */
public class EditorActivity extends d.b {
    public static NotificationManager A = null;
    public static NotificationManager B = null;

    @SuppressLint({"StaticFieldLeak"})
    public static g.c C = null;
    private static boolean D = false;
    public static ArrayList<y1.a> E;
    public static int F;
    public static String G;
    public static boolean H;
    static double I;
    static double J;
    static int K;
    static int L;
    public static int M;

    /* renamed from: v, reason: collision with root package name */
    public static d f3948v;

    /* renamed from: w, reason: collision with root package name */
    static SecretKey f3949w;

    /* renamed from: x, reason: collision with root package name */
    static AlgorithmParameterSpec f3950x;

    /* renamed from: y, reason: collision with root package name */
    private static DataOutputStream f3951y;

    /* renamed from: z, reason: collision with root package name */
    public static ArrayList<String> f3952z;

    /* renamed from: r, reason: collision with root package name */
    private Boolean f3953r = Boolean.FALSE;

    /* renamed from: s, reason: collision with root package name */
    public g.c f3954s;

    /* renamed from: t, reason: collision with root package name */
    Process f3955t;

    /* renamed from: u, reason: collision with root package name */
    o f3956u;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a extends BiometricPrompt.a {
        a() {
        }

        @Override // androidx.biometric.BiometricPrompt.a
        public void a(int i2, CharSequence charSequence) {
            super.a(i2, charSequence);
            EditorActivity.this.finishAffinity();
            System.exit(0);
        }

        @Override // androidx.biometric.BiometricPrompt.a
        public void b() {
            super.b();
            EditorActivity.this.finishAffinity();
            System.exit(0);
        }

        @Override // androidx.biometric.BiometricPrompt.a
        public void c(BiometricPrompt.b bVar) {
            super.c(bVar);
            EditorActivity.this.t0();
        }
    }

    private p A0() {
        String str;
        if (H) {
            str = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.sha512";
        } else {
            str = "https://github.com/mayankmetha/Rucky/releases/download/" + J + "/rucky.sha512";
        }
        return new p(0, str, new p.b() { // from class: w1.f0
            @Override // x0.p.b
            public final void a(Object obj) {
                EditorActivity.J0((String) obj);
            }
        }, new p.a() { // from class: w1.a0
            @Override // x0.p.a
            public final void a(x0.u uVar) {
                EditorActivity.G = "";
            }
        });
    }

    private void B0() {
        final int[] intArray = getResources().getIntArray(R.array.colors);
        Button button = (Button) findViewById(R.id.delBtn);
        Button button2 = (Button) findViewById(R.id.svBtb);
        Button button3 = (Button) findViewById(R.id.ldBtn);
        Button button4 = (Button) findViewById(R.id.exBtn);
        Button button5 = (Button) findViewById(R.id.cfgBtn);
        button.setOnClickListener(new View.OnClickListener() { // from class: w1.u
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.N0(intArray, view);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: w1.t
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.Q0(intArray, view);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() { // from class: w1.o
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.T0(view);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() { // from class: w1.s
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.U0(view);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() { // from class: w1.q
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.V0(view);
            }
        });
    }

    static String C0(File file) {
        StringWriter stringWriter = new StringWriter();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            b2.b.b(bufferedInputStream, stringWriter, "UTF-8");
            bufferedInputStream.close();
            fileInputStream.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static void D0(Context context) {
        E.clear();
        w0(context);
        u0(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void E0(Context context, JSONObject jSONObject) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(context.getExternalFilesDir("keymap"), E.get(0).a()));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(jSONObject.toString().getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.close();
            fileOutputStream.close();
            E.get(0).h(1);
            E.get(0).g(jSONObject.getInt("version"));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void F0(final Context context, o oVar, JSONArray jSONArray) {
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            try {
                y1.a aVar = new y1.a(jSONArray.getJSONObject(i2).getString("name"), Integer.parseInt(jSONArray.getJSONObject(i2).getString("revision")), jSONArray.getJSONObject(i2).getString("filename"), jSONArray.getJSONObject(i2).getString("url"), 3);
                boolean z2 = false;
                for (int i3 = 0; i3 < E.size(); i3++) {
                    if (E.get(i3).f(aVar)) {
                        y1.a aVar2 = E.get(i3);
                        if (aVar2.c() < aVar.c()) {
                            aVar2.h(2);
                        }
                        aVar2.i(aVar.e());
                        E.set(i3, aVar2);
                        z2 = true;
                    }
                }
                if (!z2) {
                    E.add(aVar);
                }
                if (D) {
                    D = false;
                    oVar.a(new l(0, E.get(0).e(), null, new p.b() { // from class: w1.c0
                        @Override // x0.p.b
                        public final void a(Object obj) {
                            EditorActivity.E0(context, (JSONObject) obj);
                        }
                    }, z.f5668a));
                    f3948v.x(E.get(0).a());
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void G0() {
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL("https://github.com/mayankmetha/Rucky/releases/latest").openConnection();
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.getInputStream();
            String headerField = httpsURLConnection.getHeaderField("Location");
            if (headerField.isEmpty()) {
                J = 0.0d;
            } else {
                J = Double.parseDouble(headerField.substring(headerField.lastIndexOf(47) + 1));
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (H || J > I) {
            this.f3956u.a(z0());
            runOnUiThread(new Runnable() { // from class: w1.w
                @Override // java.lang.Runnable
                public final void run() {
                    EditorActivity.this.q1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void H0(String str) {
        String[] split = str.split("\\r?\\n");
        try {
            M = split[0] != null ? Integer.parseInt(split[0]) : 0;
            L = split[1] != null ? Integer.parseInt(split[1]) : K;
        } catch (Exception unused) {
            M = 0;
            L = K;
        }
        int i2 = Build.VERSION.SDK_INT;
        if (i2 == 0 || i2 < M) {
            return;
        }
        this.f3956u.a(A0());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void I0(u uVar) {
        M = 0;
        L = K;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void J0(String str) {
        G = str.trim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void L0(ArrayList arrayList, View view, CharSequence[] charSequenceArr, int[] iArr, DialogInterface dialogInterface, int i2) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), ((File) arrayList.get(i2)).getName());
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (file.exists()) {
                getApplicationContext().deleteFile(file.getName());
            }
        }
        Snackbar W = Snackbar.W(view, ((Object) charSequenceArr[i2]) + " " + getResources().getString(R.string.file_deleted), -1);
        W.Y(iArr[f3948v.a()]);
        W.M();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void N0(final int[] iArr, final View view) {
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Objects.requireNonNull(externalFilesDir);
        File[] listFiles = externalFilesDir.listFiles();
        final ArrayList arrayList = new ArrayList();
        if (f3948v.p()) {
            arrayList.addAll(Arrays.asList(listFiles));
        } else {
            for (File file : listFiles) {
                if (file.getPath().endsWith(".txt")) {
                    arrayList.add(file);
                }
            }
        }
        final CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            charSequenceArr[i2] = ((File) arrayList.get(i2)).getName();
        }
        a.C0004a c0004a = new a.C0004a(this);
        c0004a.t(getResources().getString(R.string.title_delete_file));
        c0004a.d(false);
        c0004a.g(charSequenceArr, new DialogInterface.OnClickListener() { // from class: w1.i0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                EditorActivity.this.L0(arrayList, view, charSequenceArr, iArr, dialogInterface, i3);
            }
        });
        c0004a.k(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() { // from class: w1.k0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                dialogInterface.cancel();
            }
        });
        androidx.appcompat.app.a a3 = c0004a.a();
        Window window = a3.getWindow();
        Objects.requireNonNull(window);
        window.setFlags(8192, 8192);
        a3.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void O0(EditText editText, View view, int[] iArr, DialogInterface dialogInterface, int i2) {
        File file;
        FileOutputStream fileOutputStream;
        BufferedOutputStream bufferedOutputStream;
        EditText editText2 = (EditText) findViewById(R.id.code);
        String replaceAll = editText.getText().toString().replaceAll("[\\\\/.]+", "");
        if (replaceAll.isEmpty()) {
            replaceAll = String.valueOf(new Date().getTime());
        }
        if (f3948v.p()) {
            file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), replaceAll + ".enc");
        } else {
            file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), replaceAll + ".txt");
        }
        String obj = editText2.getText().toString();
        try {
            if (f3948v.p()) {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(1, f3949w, f3950x);
                fileOutputStream = new FileOutputStream(file);
                bufferedOutputStream = new BufferedOutputStream(new CipherOutputStream(fileOutputStream, cipher));
            } else {
                fileOutputStream = new FileOutputStream(file);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            }
            bufferedOutputStream.write(obj.getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        Snackbar W = Snackbar.W(view, file.getName() + " " + getResources().getString(R.string.file_saved), -1);
        W.Y(iArr[f3948v.a()]);
        W.M();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void Q0(final int[] iArr, final View view) {
        a.C0004a c0004a = new a.C0004a(this);
        c0004a.t(getResources().getString(R.string.title_save_file));
        View inflate = LayoutInflater.from(this).inflate(R.layout.editor_save, (ViewGroup) null);
        c0004a.u(inflate);
        final EditText editText = (EditText) inflate.findViewById(R.id.save_filename);
        c0004a.d(false);
        c0004a.p(getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() { // from class: w1.g0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                EditorActivity.this.O0(editText, view, iArr, dialogInterface, i2);
            }
        });
        c0004a.k(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() { // from class: w1.m0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.cancel();
            }
        });
        androidx.appcompat.app.a a3 = c0004a.a();
        Window window = a3.getWindow();
        Objects.requireNonNull(window);
        window.setFlags(8192, 8192);
        a3.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void R0(ArrayList arrayList, DialogInterface dialogInterface, int i2) {
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        EditText editText = (EditText) findViewById(R.id.code);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), ((File) arrayList.get(i2)).getName());
        try {
            if (f3948v.p() && file.getPath().endsWith(".enc")) {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(2, f3949w, f3950x);
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(new CipherInputStream(fileInputStream, cipher));
            } else {
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
            }
            StringWriter stringWriter = new StringWriter();
            b2.b.b(bufferedInputStream, stringWriter, "UTF-8");
            editText.setText(stringWriter.toString());
            bufferedInputStream.close();
            fileInputStream.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void T0(View view) {
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Objects.requireNonNull(externalFilesDir);
        File[] listFiles = externalFilesDir.listFiles();
        final ArrayList arrayList = new ArrayList();
        if (f3948v.p()) {
            arrayList.addAll(Arrays.asList(listFiles));
        } else {
            for (File file : listFiles) {
                if (file.getPath().endsWith(".txt")) {
                    arrayList.add(file);
                }
            }
        }
        CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            charSequenceArr[i2] = ((File) arrayList.get(i2)).getName();
        }
        a.C0004a c0004a = new a.C0004a(this);
        c0004a.t(getResources().getString(R.string.title_open_file));
        c0004a.d(false);
        c0004a.g(charSequenceArr, new DialogInterface.OnClickListener() { // from class: w1.h0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                EditorActivity.this.R0(arrayList, dialogInterface, i3);
            }
        });
        c0004a.k(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() { // from class: w1.n
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                dialogInterface.cancel();
            }
        });
        androidx.appcompat.app.a a3 = c0004a.a();
        Window window = a3.getWindow();
        Objects.requireNonNull(window);
        window.setFlags(8192, 8192);
        a3.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void U0(View view) {
        e1(f3948v.h(), f3948v.g(), ((EditText) findViewById(R.id.code)).getText().toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void V0(View view) {
        startActivity(new Intent(this, (Class<?>) ConfigActivity.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void X0() {
        try {
            Socket socket = new Socket(f3948v.n().substring(0, f3948v.n().indexOf(":")), Integer.parseInt(f3948v.n().substring(f3948v.n().indexOf(":") + 1)));
            PrintWriter printWriter = new PrintWriter((Writer) new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            for (int i2 = 0; i2 < f3952z.size(); i2++) {
                if (socket.isConnected()) {
                    printWriter.print(f3952z.get(i2));
                }
            }
            printWriter.close();
            socket.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        f3952z.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void Y0(DialogInterface dialogInterface, int i2) {
        if (f3948v.h() == 1) {
            k1(this);
        }
        finishAndRemoveTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a1(DialogInterface dialogInterface, int i2) {
        super.onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void b1(View view) {
        startActivity(new Intent(this, (Class<?>) SettingsActivity.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void d1(View view) {
        startActivity(new Intent(this, (Class<?>) UpdateActivity.class));
    }

    private void f1() {
        ((Button) findViewById(R.id.setting_button)).setOnClickListener(new View.OnClickListener() { // from class: w1.p
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.b1(view);
            }
        });
    }

    private void g1() {
        r0();
        v0();
    }

    private void h1() {
        ArrayList arrayList = new ArrayList();
        if (w.a.a(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (w.a.a(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (arrayList.isEmpty()) {
            return;
        }
        v.a.m(this, (String[]) arrayList.toArray(new String[arrayList.size()]), 0);
    }

    private void i1() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("com.mayank.rucky.service", "Foreground Service", 3);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.canBypassDnd();
            notificationChannel.setSound(null, null);
            notificationChannel.setLockscreenVisibility(1);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            A = notificationManager;
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            A = (NotificationManager) getSystemService("notification");
        }
        r1(this);
    }

    public static void j1(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, (Class<?>) SocketHeartbeatService.class));
        } else {
            context.startService(new Intent(context, (Class<?>) SocketHeartbeatService.class));
        }
        p1(context);
    }

    public static void k1(Context context) {
        context.stopService(new Intent(context, (Class<?>) SocketHeartbeatService.class));
    }

    private void l1() {
        File file = new File("/dev", "hidg0");
        File file2 = new File("/dev", "hidg1");
        if (file.exists() || file2.exists()) {
            try {
                f3951y.writeBytes("chmod 666 /dev/hidg0\n");
                f3951y.writeBytes("chmod 666 /dev/hidg1\n");
                f3951y.flush();
                return;
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
        }
        a.C0004a c0004a = new a.C0004a(this);
        c0004a.t(getResources().getString(R.string.kernel_err));
        c0004a.d(false);
        c0004a.p(getResources().getString(R.string.btn_continue), new DialogInterface.OnClickListener() { // from class: w1.n0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.cancel();
            }
        });
        androidx.appcompat.app.a a3 = c0004a.a();
        Window window = a3.getWindow();
        Objects.requireNonNull(window);
        window.setFlags(8192, 8192);
        a3.show();
    }

    public static void m1(d dVar, Context context) {
        ImageView imageView;
        int i2;
        if (o0()) {
            if (dVar.o()) {
                ConfigActivity.f3942u.setText(R.string.config_status_net_on);
                imageView = ConfigActivity.f3943v;
                i2 = R.drawable.ic_net;
            } else {
                ConfigActivity.f3942u.setText(R.string.config_status_net_off);
                imageView = ConfigActivity.f3943v;
                i2 = R.drawable.ic_net_off;
            }
            imageView.setImageDrawable(w.a.e(context, i2));
        }
    }

    public static void n0(Context context, String str) {
        Intent intent = new Intent(context, (Class<?>) SplashActivity.class);
        intent.setFlags(268468224);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 67108864);
        g.c cVar = new g.c(context, "com.mayank.rucky.service");
        cVar.f(str);
        cVar.i(R.drawable.ic_notification);
        cVar.h(true);
        cVar.e(activity);
        cVar.d(false);
        C = cVar;
        A.notify(1, cVar.a());
    }

    public static void n1(d dVar, Context context) {
        ImageView imageView;
        int i2;
        if (o0()) {
            if (dVar.r()) {
                ConfigActivity.f3942u.setText(R.string.config_status_usb_on);
                imageView = ConfigActivity.f3943v;
                i2 = R.drawable.ic_usb;
            } else {
                ConfigActivity.f3942u.setText(R.string.config_status_usb_off);
                imageView = ConfigActivity.f3943v;
                i2 = R.drawable.ic_usb_off;
            }
            imageView.setImageDrawable(w.a.e(context, i2));
        }
    }

    public static boolean o0() {
        return (ConfigActivity.f3942u == null || ConfigActivity.f3943v == null) ? false : true;
    }

    private void o1() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("com.mayank.rucky.update", "Update", 3);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.canBypassDnd();
            notificationChannel.setSound(null, null);
            notificationChannel.setLockscreenVisibility(1);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            B = notificationManager;
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            B = (NotificationManager) getSystemService("notification");
        }
        this.f3956u = r.a(this);
        if (f3948v.s()) {
            g1();
        }
    }

    public static void p1(Context context) {
        d dVar = new d(context);
        if (dVar.h() == 1) {
            n0(context, context.getString(dVar.o() ? R.string.config_status_net_on : R.string.config_status_net_off));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void q1() {
        PendingIntent activity;
        g.c cVar;
        StringBuilder sb;
        Button button = (Button) findViewById(R.id.update_button);
        if (f3948v.s()) {
            if (H && L > K) {
                Intent intent = new Intent(this, (Class<?>) UpdateActivity.class);
                intent.setFlags(268468224);
                activity = PendingIntent.getActivity(this, 0, intent, 134217728);
                cVar = new g.c(this, "com.mayank.rucky.update");
                sb = new StringBuilder();
                sb.append(getResources().getString(R.string.update_new));
                sb.append(" Nightly Version: ");
                sb.append(J);
                sb.append(" (");
                sb.append(L);
                sb.append(")");
            } else if (J > I) {
                Intent intent2 = new Intent(this, (Class<?>) UpdateActivity.class);
                intent2.setFlags(268468224);
                activity = PendingIntent.getActivity(this, 0, intent2, 134217728);
                cVar = new g.c(this, "com.mayank.rucky.update");
                sb = new StringBuilder();
                sb.append(getResources().getString(R.string.update_new));
                sb.append(" Version: ");
                sb.append(J);
            }
            cVar.f(sb.toString());
            cVar.i(R.drawable.ic_notification);
            cVar.e(activity);
            cVar.d(false);
            this.f3954s = cVar;
            B.notify(0, cVar.a());
            button.setVisibility(0);
            button.setOnClickListener(new View.OnClickListener() { // from class: w1.r
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EditorActivity.this.d1(view);
                }
            });
        }
        button.setVisibility(8);
        button.setOnClickListener(new View.OnClickListener() { // from class: w1.r
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditorActivity.this.d1(view);
            }
        });
    }

    private void r0() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            I = Double.parseDouble(packageInfo.versionName);
            K = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    public static void r1(Context context) {
        d dVar = new d(context);
        if (dVar.h() == 0) {
            k1(context);
            n1(dVar, context);
        } else if (dVar.h() == 1) {
            j1(context);
            m1(dVar, context);
            p1(context);
        }
    }

    static int s0(String str) {
        if (!str.isEmpty()) {
            try {
                return new JSONObject(str).getInt("version");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return 0;
    }

    static void u0(final Context context) {
        final o a3 = r.a(context);
        a3.a(new k(0, "https://raw.githubusercontent.com/mayankmetha/Rucky-KeyMap/main/keymap.json", null, new p.b() { // from class: w1.d0
            @Override // x0.p.b
            public final void a(Object obj) {
                EditorActivity.F0(context, a3, (JSONArray) obj);
            }
        }, z.f5668a));
    }

    private void v0() {
        if (new a2.k().a(this)) {
            new Thread(new Runnable() { // from class: w1.v
                @Override // java.lang.Runnable
                public final void run() {
                    EditorActivity.this.G0();
                }
            }).start();
        }
    }

    static void w0(Context context) {
        File externalFilesDir = context.getExternalFilesDir("keymap");
        Objects.requireNonNull(externalFilesDir);
        File[] listFiles = externalFilesDir.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            D = true;
            return;
        }
        D = false;
        for (File file : listFiles) {
            if (file.getPath().endsWith(".json")) {
                try {
                    E.add(new y1.a(file.getName().replace(".json", "").replace("_", " ").toUpperCase(), s0(C0(file)), file.getName(), "", 1));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private void x0() {
        int i2;
        int i3;
        ArrayList arrayList = new ArrayList();
        H = false;
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 134217728);
                if (packageInfo.signingInfo.hasMultipleSigners()) {
                    for (Signature signature : packageInfo.signingInfo.getApkContentsSigners()) {
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
                        messageDigest.update(signature.toByteArray());
                        arrayList.add(new String(Base64.encode(messageDigest.digest(), 0)));
                    }
                } else {
                    for (Signature signature2 : packageInfo.signingInfo.getSigningCertificateHistory()) {
                        MessageDigest messageDigest2 = MessageDigest.getInstance("SHA256");
                        messageDigest2.update(signature2.toByteArray());
                        arrayList.add(new String(Base64.encode(messageDigest2.digest(), 0)));
                    }
                }
            } else {
                for (Signature signature3 : getPackageManager().getPackageInfo(getPackageName(), 64).signatures) {
                    MessageDigest messageDigest3 = MessageDigest.getInstance("SHA256");
                    messageDigest3.update(signature3.toByteArray());
                    arrayList.add(new String(Base64.encode(messageDigest3.digest(), 0)));
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            if (((String) arrayList.get(i4)).trim().equals("x2j+O+TND/jjH0ryjO/2ROPpjCvHoHK/XnjrgdAHJfM=")) {
                i2 = R.string.releaseNetHunter;
            } else {
                if (((String) arrayList.get(i4)).trim().equals("0Xv/I6xP6Q1wKbIqCgXi4CafhKZtOZLOR575TiqN93s=")) {
                    F = R.string.releaseGitHub;
                } else {
                    if (((String) arrayList.get(i4)).trim().equals("eEk+yGxeE5dXukQ4HiGYS4eEyTAcoC6Mfm1OX/1l12c=")) {
                        i3 = R.string.releaseGitHubNightly;
                    } else if (((String) arrayList.get(i4)).trim().equals("im5KgLli2rx4iEvMVXotXGpfiR1/eqXEwBO2YQ6uP70=")) {
                        i3 = R.string.releaseTest;
                    } else {
                        i2 = R.string.releaseOthers;
                    }
                    F = i3;
                    H = true;
                }
                f3948v.M(true);
            }
            F = i2;
            f3948v.M(false);
        }
    }

    private void y0() {
        try {
            this.f3955t = Runtime.getRuntime().exec("su");
            f3951y = new DataOutputStream(this.f3955t.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.f3955t.getInputStream()));
            DataOutputStream dataOutputStream = f3951y;
            if (dataOutputStream != null) {
                dataOutputStream.writeBytes("id\n");
                f3951y.flush();
                if (bufferedReader.readLine().contains("uid=0")) {
                    this.f3953r = Boolean.TRUE;
                }
            }
        } catch (Exception unused) {
        }
    }

    private y0.p z0() {
        String str;
        if (H) {
            str = "https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.cfg";
        } else {
            str = "https://github.com/mayankmetha/Rucky/releases/download/" + J + "/rucky.cfg";
        }
        return new y0.p(0, str, new p.b() { // from class: w1.e0
            @Override // x0.p.b
            public final void a(Object obj) {
                EditorActivity.this.H0((String) obj);
            }
        }, new p.a() { // from class: w1.b0
            @Override // x0.p.a
            public final void a(x0.u uVar) {
                EditorActivity.I0(uVar);
            }
        });
    }

    void e1(int i2, int i3, String str) {
        if (i2 == 0) {
            y0();
            if (this.f3953r.booleanValue()) {
                l1();
                if (!f3948v.q() || f3948v.r()) {
                    try {
                        if (f3948v.c()) {
                            q0(C0(new File(getExternalFilesDir("keymap"), f3948v.e())), str);
                        } else {
                            p0(i3, str);
                        }
                        for (int i4 = 0; i4 < f3952z.size(); i4++) {
                            f3951y.writeBytes(f3952z.get(i4));
                            f3951y.flush();
                        }
                        f3952z.clear();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else if (f3948v.c()) {
                    q0(C0(new File(getExternalFilesDir("keymap"), f3948v.e())), str);
                } else {
                    p0(i3, str);
                }
            } else {
                a.C0004a c0004a = new a.C0004a(this);
                c0004a.t(getResources().getString(R.string.root_err));
                c0004a.d(false);
                c0004a.p(getResources().getString(R.string.btn_continue), new DialogInterface.OnClickListener() { // from class: w1.l0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        dialogInterface.cancel();
                    }
                });
                androidx.appcompat.app.a a3 = c0004a.a();
                Window window = a3.getWindow();
                Objects.requireNonNull(window);
                window.setFlags(8192, 8192);
                a3.show();
            }
        }
        if (i2 == 1) {
            if (!f3948v.m() || f3948v.o()) {
                if (f3948v.c()) {
                    q0(C0(new File(getExternalFilesDir("keymap"), f3948v.e())), str);
                } else {
                    p0(i3, str);
                }
                new Thread(new Runnable() { // from class: w1.y
                    @Override // java.lang.Runnable
                    public final void run() {
                        EditorActivity.X0();
                    }
                }).start();
                return;
            }
            if (f3948v.c()) {
                q0(C0(new File(getExternalFilesDir("keymap"), f3948v.e())), str);
            } else {
                p0(i3, str);
            }
        }
    }

    public void m0() {
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, w.a.h(this), new a());
        BiometricPrompt.d.a aVar = new BiometricPrompt.d.a();
        aVar.e(getResources().getString(R.string.unlock));
        aVar.d(getResources().getString(R.string.auth));
        aVar.c(getResources().getString(R.string.btn_cancel));
        aVar.b(false);
        biometricPrompt.a(aVar.a());
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        a.C0004a c0004a = new a.C0004a(this);
        c0004a.t(getResources().getString(R.string.exit_dialog));
        c0004a.d(false);
        c0004a.p(getResources().getString(R.string.btn_exit), new DialogInterface.OnClickListener() { // from class: w1.m
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                EditorActivity.this.Y0(dialogInterface, i2);
            }
        });
        c0004a.m(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() { // from class: w1.j0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.cancel();
            }
        });
        c0004a.k(getResources().getString(R.string.btn_back), new DialogInterface.OnClickListener() { // from class: w1.x
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                EditorActivity.this.a1(dialogInterface, i2);
            }
        });
        androidx.appcompat.app.a a3 = c0004a.a();
        Window window = a3.getWindow();
        Objects.requireNonNull(window);
        window.setFlags(8192, 8192);
        a3.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.e, androidx.activity.ComponentActivity, v.e, android.app.Activity
    public void onCreate(Bundle bundle) {
        f3952z = new ArrayList<>();
        super.onCreate(bundle);
        getWindow().setFlags(8192, 8192);
        requestWindowFeature(1);
        d dVar = new d(this);
        f3948v = dVar;
        d.d.F(dVar.b() ? 2 : 1);
        setTheme(e.f43a[f3948v.a()]);
        setContentView(R.layout.activity_editor);
        if (f3948v.i() && f3948v.p()) {
            m0();
        }
        if (bundle == null) {
            h1();
            y0();
        }
        x0();
        i1();
        if (f3948v.s()) {
            o1();
        }
        f1();
        E = new ArrayList<>();
        D0(this);
        B0();
    }

    @Override // androidx.fragment.app.e, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i2, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i2, strArr, iArr);
        if (i2 == 0 && iArr.length > 0 && strArr.length == iArr.length) {
            for (int i3 = 0; i3 < strArr.length; i3++) {
                if (iArr[i3] != 0) {
                    h1();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.e, android.app.Activity
    public void onResume() {
        super.onResume();
        d.d.F(f3948v.b() ? 2 : 1);
        setTheme(e.f43a[f3948v.a()]);
        q1();
    }

    void p0(int i2, String str) {
        h hVar = new h(i2);
        hVar.z(str);
        f3952z.clear();
        f3952z.addAll(hVar.u());
    }

    void q0(String str, String str2) {
        a2.g gVar = new a2.g(str);
        gVar.f(str2);
        f3952z.clear();
        f3952z.addAll(gVar.b());
    }

    void t0() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) keyStore.getEntry("AndroidKeyStore", null)).getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(2, privateKey);
            f3949w = new SecretKeySpec(cipher.doFinal(Base64.decode(f3948v.k(), 0)), "AES");
            f3950x = new IvParameterSpec(cipher.doFinal(Base64.decode(f3948v.l(), 0)));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
