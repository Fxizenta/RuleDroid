package jp.r246.twicca.base.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.io.File;
import jp.r246.twicca.R;
import jp.r246.twicca.a.b.c;
import jp.r246.twicca.a.c.a;
import jp.r246.twicca.e.d;
import jp.r246.twicca.j.k;
import jp.r246.twicca.savedsearches.h;
import jp.r246.twicca.system.ActivityChooser;
import jp.r246.twicca.timelines.a.g;
import jp.r246.twicca.widgets.MenuWidget;

/* loaded from: classes.dex */
public abstract class TwiccaActivity extends Activity {
    protected static int j;
    protected SharedPreferences b;
    protected SharedPreferences.Editor c;
    private static String k = null;
    public static final int a = k.a;
    public static final long[] d = {0, 180000, 300000, 600000, 900000, 1800000, 3600000, 7200000, 10800000, 14400000, 18000000, 21600000};
    public static final int[] e = {-65536, -16711936, -16776961, -65281, -16711681};
    public static final int[] f = {3, 4, 6, 0, 1, 2, 5, 7, 8, 9};
    public static final int[] g = {3, 4, 5, 0, 1, 6, 2, 7, 8, 9};
    public static final long[] h = {3600000, 7200000, 10800000, 21600000, 43200000, 86400000, 172800000, 259200000};
    public static String i = "twicca/0.x.x(Twitter client for Android)";

    public static final File a(File file) {
        File file2 = new File(file, "queue");
        if (!file2.exists()) {
            file2.mkdir();
        }
        return file2;
    }

    public static final String a(String str) {
        if (k == null) {
            int length = str.length();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i2 = 0; i2 < length; i2++) {
                stringBuffer.insert(0, (char) (((str.charAt(i2) + 256) - 246) % 256));
            }
            k = stringBuffer.toString();
        }
        return k;
    }

    public static void a(Context context) {
        RemoteViews b = MenuWidget.b(context);
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, (Class<?>) MenuWidget.class), b);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void a(int i2) {
        if (i2 == 500) {
            Toast makeText = Toast.makeText(this, R.string.SOMETHING_IS_TECHNICALLY_WRONG, 0);
            makeText.setView(((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.toast_something_is_technically_wrong, (ViewGroup) null));
            makeText.setGravity(17, 0, 0);
            makeText.show();
            return;
        }
        if (i2 == 502 || i2 == 503) {
            Toast makeText2 = Toast.makeText(this, R.string.TWITTER_IS_OVER_CAPACITY, 0);
            makeText2.setView(((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.toast_twitter_is_over_capacity, (ViewGroup) null));
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
            return;
        }
        String string = i2 == 400 ? getString(R.string.OOPS_SOMETHING_WENT_WRONG_TRY_AGAIN_IN_A_MINUTE) : i2 == 500 ? getString(R.string.SOMETHING_IS_TECHNICALLY_WRONG) : i2 == 502 ? getString(R.string.TWITTER_IS_OVER_CAPACITY) : i2 == 503 ? getString(R.string.TWITTER_IS_OVER_CAPACITY) : i2 == 800 ? getString(R.string.OOPS_SOMETHING_WENT_WRONG_TRY_AGAIN_IN_A_MINUTE) : i2 == 998 ? getString(R.string.OOPS_SOMETHING_WENT_WRONG_TRY_AGAIN_IN_A_MINUTE) : i2 == 999 ? getString(R.string.OOPS_SOMETHING_WENT_WRONG_TRY_AGAIN_IN_A_MINUTE) : null;
        if (string != null) {
            Toast.makeText(this, string, 0).show();
        }
    }

    public final void a(Intent intent) {
        Intent intent2 = new Intent(this, (Class<?>) ActivityChooser.class);
        intent2.putExtra("android.intent.extra.INTENT", intent);
        startActivity(intent2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void a(c cVar) {
        this.c.putString("account.oauth.token", cVar.c);
        this.c.putString("account.oauth.token_secret", cVar.d);
        this.c.putString("account.id", cVar.b);
        this.c.putLong("account.user.id", cVar.a);
        this.c.putString("account.user.screen_name", cVar.b);
        this.c.commit();
        a.a(this).c();
        h.a().a(false);
        jp.r246.twicca.a.a.c.a().c();
        new g(this, 0).b();
        new g(this, 1).b();
        new g(this, 2).b();
        new g(this, 3).b();
        new g(this, 4).b();
        new g(this, 5).b();
        new g(this, 6).b();
        new g(this, 7).b();
        new g(this, 8).b();
        new g(this, 9).b();
        new g(this, 10).b();
        new g(this, 11).b();
        new g(this, 12).b();
        new g(this, 13).b();
        new g(this, 14).b();
        new g(this, 15).b();
        new g(this, 16).b();
        new g(this, 17).b();
        new g(this, 99).b();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void a(jp.r246.twicca.timelines.c.a aVar) {
        if (aVar == null) {
            Toast.makeText(this, R.string.OOPS_SOMETHING_WENT_WRONG_TRY_AGAIN_IN_A_MINUTE, 0).show();
            Log.d("twicca", "(unknown status):(no messages)");
            return;
        }
        a(aVar.a);
        if (aVar.b != null) {
            Log.d("twicca", String.valueOf(String.valueOf(aVar.a)) + ":" + aVar.b);
        } else {
            Log.d("twicca", String.valueOf(String.valueOf(aVar.a)) + ":( no messages )");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean b() {
        return e() && d();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String c() {
        return this.b.getString("account.user.screen_name", "");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean d() {
        return (this.b.getString("account.oauth.token", "").equals("") || this.b.getString("account.oauth.token_secret", "").equals("")) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean e() {
        return this.b.getInt("eula.version", 0) > 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean f() {
        return (this.b.getString("account.oauth.temp.token", null) == null || this.b.getString("account.oauth.temp.token_secret", null) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final d g() {
        String string = this.b.getString("account.oauth.token", "");
        String string2 = this.b.getString("account.oauth.token_secret", "");
        String a2 = a(this.b.getString("account.oauth.consumer_secret", ""));
        if (string.equals("") || string2.equals("") || a2.equals("")) {
            return null;
        }
        return new d("7S2l5rQTuFCj4YJpF7xuTQ", a2, string, string2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File h() {
        File file = new File(getCacheDir(), "stored_cache");
        return (file.exists() || file.mkdirs()) ? file : getCacheDir();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void i() {
        if (this.b == null) {
            this.b = getSharedPreferences("twicca", 0);
            this.c = this.b.edit();
        }
        switch (this.b.getInt("theme.type", 0)) {
            case 1:
                setTheme(R.style.TwiccaTheme_Light);
                return;
            default:
                if (Build.VERSION.SDK_INT == 4 || "X10i".equals(Build.MODEL) || "SO-01B".equals(Build.MODEL) || "HTC Dream".equals(Build.MODEL) || "T-Mobile G1".equals(Build.MODEL) || "Era G1".equals(Build.MODEL) || "HTC Magic".equals(Build.MODEL) || "HTC Sapphire".equals(Build.MODEL) || "T-Mobile myTouch 3G".equals(Build.MODEL) || "Docomo HT-03A".equals(Build.MODEL)) {
                    setTheme(R.style.TwiccaTheme_LowSpec);
                    return;
                } else {
                    setTheme(R.style.TwiccaTheme);
                    return;
                }
        }
    }

    public final File j() {
        int i2 = this.b.getInt("cache.location.image", a);
        if (i2 == k.b) {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                return new File(Environment.getExternalStorageDirectory(), ".twicca");
            }
            return null;
        }
        if (i2 != k.c) {
            return getCacheDir();
        }
        if (!"mounted".equals(Environment.getExternalStorageState())) {
            return null;
        }
        File file = new File(Environment.getExternalStorageDirectory(), "external_sd");
        if (file.exists()) {
            return new File(file, ".twicca");
        }
        return null;
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        j = configuration.hardKeyboardHidden;
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.b == null) {
            this.b = getSharedPreferences("twicca", 0);
            this.c = this.b.edit();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onSearchRequested() {
        return false;
    }
}
