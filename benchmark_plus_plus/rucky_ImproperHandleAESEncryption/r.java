package x1;

import a2.b;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import androidx.appcompat.app.a;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mayank.rucky.R;
import com.mayank.rucky.activity.BrowserActivity;
import com.mayank.rucky.activity.EditorActivity;
import com.mayank.rucky.activity.HidActivity;
import com.mayank.rucky.activity.SplashActivity;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: classes.dex */
public class r extends androidx.preference.d {

    /* renamed from: j0, reason: collision with root package name */
    private a2.d f5806j0;

    private void A2() {
        g("git").s0(new Preference.e() { // from class: x1.q
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean K2;
                K2 = r.this.K2(preference);
                return K2;
            }
        });
    }

    private void B2() {
        SwitchPreference switchPreference = (SwitchPreference) g("hidSelect");
        final Preference g2 = g("hid");
        g2.k0(this.f5806j0.c());
        g2.u0(this.f5806j0.c());
        g2.v0(this.f5806j0.c());
        g2.B0(this.f5806j0.c());
        switchPreference.r0(new Preference.d() { // from class: x1.p
            @Override // androidx.preference.Preference.d
            public final boolean a(Preference preference, Object obj) {
                boolean L2;
                L2 = r.this.L2(g2, preference, obj);
                return L2;
            }
        });
        g2.s0(new Preference.e() { // from class: x1.b
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean M2;
                M2 = r.this.M2(preference);
                return M2;
            }
        });
    }

    private void C2() {
        ((SwitchPreference) g("icon")).r0(new Preference.d() { // from class: x1.o
            @Override // androidx.preference.Preference.d
            public final boolean a(Preference preference, Object obj) {
                boolean N2;
                N2 = r.this.N2(preference, obj);
                return N2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void D2(a2.b bVar, int[] iArr, int i2, View view) {
        this.f5806j0.t(i2);
        bVar.G(i2);
        ImageView imageView = (ImageView) view.findViewById(R.id.color_button);
        imageView.setImageDrawable(w.a.e(q1(), R.drawable.color_button_selected));
        imageView.setColorFilter(iArr[i2]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void E2(DialogInterface dialogInterface, int i2) {
        W2();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean G2(Preference preference) {
        final int[] intArray = P().getIntArray(R.array.colors);
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(q1()).inflate(R.layout.color_grid, (ViewGroup) null);
        final a2.b bVar = new a2.b(intArray, q1());
        bVar.F(new b.a() { // from class: x1.a
            @Override // a2.b.a
            public final void a(int i2, View view) {
                r.this.D2(bVar, intArray, i2, view);
            }
        });
        recyclerView.setAdapter(bVar);
        recyclerView.setLayoutManager(new GridLayoutManager(q1(), 5));
        recyclerView.setForegroundGravity(17);
        a.C0004a c0004a = new a.C0004a(q1());
        c0004a.d(false);
        c0004a.u(recyclerView);
        c0004a.s(R.string.accent_theme_title);
        c0004a.p(P().getString(R.string.btn_select), new DialogInterface.OnClickListener() { // from class: x1.i
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                r.this.E2(dialogInterface, i2);
            }
        });
        c0004a.k(P().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() { // from class: x1.j
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
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean H2(Preference preference) {
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.setFlags(268435456);
        intent.setData(Uri.parse("package:com.mayank.rucky"));
        intent.addFlags(1);
        I1(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean I2(Preference preference, Object obj) {
        this.f5806j0.u(!((SwitchPreference) preference).H0());
        W2();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean J2(Preference preference) {
        Intent intent = new Intent(o(), (Class<?>) BrowserActivity.class);
        intent.putExtra("ACTIVITY_TITLE", P().getString(R.string.setting_developer_title));
        intent.putExtra("WEBVIEW_URL", "https://mayankmetha.github.io");
        I1(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean K2(Preference preference) {
        Intent intent = new Intent(o(), (Class<?>) BrowserActivity.class);
        intent.putExtra("ACTIVITY_TITLE", P().getString(R.string.settings_git_title));
        intent.putExtra("WEBVIEW_URL", "https://github.com/mayankmetha/Rucky/issues");
        I1(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean L2(Preference preference, Preference preference2, Object obj) {
        this.f5806j0.v(!((SwitchPreference) preference2).H0());
        preference.k0(this.f5806j0.c());
        preference.u0(this.f5806j0.c());
        preference.v0(this.f5806j0.c());
        preference.B0(this.f5806j0.c());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean M2(Preference preference) {
        I1(new Intent(o(), (Class<?>) HidActivity.class));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean N2(Preference preference, Object obj) {
        boolean z2 = !((SwitchPreference) preference).H0();
        this.f5806j0.B(z2);
        if (z2) {
            q1().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky", "com.mayank.rucky.Main"), 2, 1);
        } else {
            q1().getPackageManager().setComponentEnabledSetting(new ComponentName("com.mayank.rucky", "com.mayank.rucky.Main"), 1, 1);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean O2(Preference preference) {
        Intent intent = new Intent(o(), (Class<?>) BrowserActivity.class);
        intent.putExtra("ACTIVITY_TITLE", P().getString(R.string.settings_lic_title));
        intent.putExtra("WEBVIEW_URL", "https://raw.githubusercontent.com/mayankmetha/Rucky/master/LICENSE");
        I1(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean P2(Preference preference, Object obj) {
        this.f5806j0.G(!((SwitchPreference) preference).H0());
        W2();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean Q2(Preference preference, Object obj) {
        boolean z2 = !((SwitchPreference) preference).H0();
        if (!this.f5806j0.j()) {
            try {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(new SecureRandom().generateSeed(16));
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256);
                SecretKey generateKey = keyGenerator.generateKey();
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                keyPairGenerator.initialize(new KeyGenParameterSpec.Builder("AndroidKeyStore", 3).setEncryptionPaddings("PKCS1Padding").setBlockModes("ECB").build());
                KeyPair generateKeyPair = keyPairGenerator.generateKeyPair();
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(1, generateKeyPair.getPublic());
                this.f5806j0.F(Base64.encodeToString(cipher.doFinal(ivParameterSpec.getIV()), 0));
                this.f5806j0.E(Base64.encodeToString(cipher.doFinal(generateKey.getEncoded()), 0));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.f5806j0.D(true);
        }
        this.f5806j0.J(z2);
        W2();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean R2(String str, String str2, Preference preference) {
        Intent intent = new Intent(o(), (Class<?>) BrowserActivity.class);
        intent.putExtra("ACTIVITY_TITLE", str);
        intent.putExtra("WEBVIEW_URL", str2);
        I1(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean S2(Preference preference) {
        Intent intent = new Intent(o(), (Class<?>) BrowserActivity.class);
        intent.putExtra("ACTIVITY_TITLE", P().getString(R.string.settings_locale_title));
        intent.putExtra("WEBVIEW_URL", "https://crwd.in/rucky");
        I1(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean T2(Preference preference, Object obj) {
        this.f5806j0.K(!((SwitchPreference) preference).H0());
        W2();
        return true;
    }

    private void U2() {
        g("lic").s0(new Preference.e() { // from class: x1.c
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean O2;
                O2 = r.this.O2(preference);
                return O2;
            }
        });
    }

    private void V2() {
        ((SwitchPreference) g("net")).r0(new Preference.d() { // from class: x1.m
            @Override // androidx.preference.Preference.d
            public final boolean a(Preference preference, Object obj) {
                boolean P2;
                P2 = r.this.P2(preference, obj);
                return P2;
            }
        });
    }

    private void W2() {
        Intent intent = new Intent(o(), (Class<?>) SplashActivity.class);
        intent.addFlags(67108864);
        intent.addFlags(32768);
        q1().finish();
        I1(intent);
    }

    private void X2() {
        ((SwitchPreference) g("sec")).r0(new Preference.d() { // from class: x1.k
            @Override // androidx.preference.Preference.d
            public final boolean a(Preference preference, Object obj) {
                boolean Q2;
                Q2 = r.this.Q2(preference, obj);
                return Q2;
            }
        });
    }

    @SuppressLint({"NonConstantResourceId"})
    private void Y2() {
        Resources P;
        int i2;
        Preference g2 = g("source");
        g2.w0(EditorActivity.F);
        boolean z2 = true;
        final String str = "";
        final String str2 = "https://mayankmetha.github.io/Rucky/";
        switch (EditorActivity.F) {
            case R.string.releaseGitHub /* 2131689649 */:
                P = P();
                i2 = R.string.releaseGitHub;
                str = P.getString(i2);
                break;
            case R.string.releaseGitHubNightly /* 2131689650 */:
                P = P();
                i2 = R.string.releaseGitHubNightly;
                str = P.getString(i2);
                break;
            case R.string.releaseNetHunter /* 2131689651 */:
                str = P().getString(R.string.releaseNetHunter);
                str2 = "https://store.nethunter.com/en/packages/com.mayank.rucky/";
                break;
            case R.string.releaseOthers /* 2131689652 */:
            default:
                z2 = false;
                str2 = "";
                break;
            case R.string.releaseTest /* 2131689653 */:
                P = P();
                i2 = R.string.releaseTest;
                str = P.getString(i2);
                break;
        }
        if (z2) {
            g2.s0(new Preference.e() { // from class: x1.h
                @Override // androidx.preference.Preference.e
                public final boolean a(Preference preference) {
                    boolean R2;
                    R2 = r.this.R2(str, str2, preference);
                    return R2;
                }
            });
        }
    }

    private void Z2() {
        g("locale").s0(new Preference.e() { // from class: x1.f
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean S2;
                S2 = r.this.S2(preference);
                return S2;
            }
        });
    }

    private void a3() {
        ((SwitchPreference) g("usb")).r0(new Preference.d() { // from class: x1.l
            @Override // androidx.preference.Preference.d
            public final boolean a(Preference preference, Object obj) {
                boolean T2;
                T2 = r.this.T2(preference, obj);
                return T2;
            }
        });
    }

    private void b3() {
        Preference g2 = g("version");
        double d2 = 0.0d;
        int i2 = 0;
        try {
            PackageInfo packageInfo = q1().getPackageManager().getPackageInfo(q1().getPackageName(), 0);
            i2 = packageInfo.versionCode;
            d2 = Double.parseDouble(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
        g2.x0(d2 + " (" + i2 + ")");
    }

    private void v2() {
        g("accent").s0(new Preference.e() { // from class: x1.g
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean G2;
                G2 = r.this.G2(preference);
                return G2;
            }
        });
    }

    private void w2() {
        g("arch").x0(Build.SUPPORTED_ABIS[0]);
    }

    private void x2() {
        g("uninstall").s0(new Preference.e() { // from class: x1.e
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean H2;
                H2 = r.this.H2(preference);
                return H2;
            }
        });
    }

    private void y2() {
        ((SwitchPreference) g("theme")).r0(new Preference.d() { // from class: x1.n
            @Override // androidx.preference.Preference.d
            public final boolean a(Preference preference, Object obj) {
                boolean I2;
                I2 = r.this.I2(preference, obj);
                return I2;
            }
        });
    }

    private void z2() {
        g("developer").s0(new Preference.e() { // from class: x1.d
            @Override // androidx.preference.Preference.e
            public final boolean a(Preference preference) {
                boolean J2;
                J2 = r.this.J2(preference);
                return J2;
            }
        });
    }

    @Override // androidx.preference.d
    public void U1(Bundle bundle, String str) {
        c2(R.xml.settings, str);
        a2.d dVar = new a2.d(r1());
        this.f5806j0 = dVar;
        d.d.F(dVar.b() ? 2 : 1);
        q1().setTheme(a2.e.f43a[this.f5806j0.a()]);
        y2();
        v2();
        C2();
        X2();
        x2();
        B2();
        a3();
        V2();
        z2();
        b3();
        w2();
        Y2();
        U2();
        A2();
        Z2();
    }
}
