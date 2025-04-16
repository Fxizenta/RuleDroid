package com.medhaapps.wififtpserver.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.d.a;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.medhaapps.wififtpserver.R;

/* loaded from: classes.dex */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /* renamed from: a, reason: collision with root package name */
    private Toolbar f2068a;

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);
        this.f2068a.setTitle(getString(R.string.action_settings));
    }

    @Override // android.app.Activity
    public void setContentView(int i) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_settings, (ViewGroup) new LinearLayout(this), false);
        this.f2068a = (Toolbar) viewGroup.findViewById(R.id.action_bar);
        this.f2068a.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.medhaapps.wififtpserver.activity.SettingsActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingsActivity.this.finish();
            }
        });
        LayoutInflater.from(this).inflate(i, (ViewGroup) viewGroup.findViewById(R.id.content_wrapper), true);
        getWindow().setContentView(viewGroup);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (str.equals("pref_mount")) {
            if (sharedPreferences.getString(str, "0").equals("4")) {
                Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, 42);
                    return;
                } else {
                    Toast.makeText(this, getString(R.string.lbl_missing_prod), 0).show();
                    return;
                }
            }
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString("pref_custom_uri", "");
            edit.apply();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 42 && i2 == -1) {
            Uri data = intent.getData();
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString("pref_custom_uri", data.toString());
            edit.apply();
            if (Build.VERSION.SDK_INT >= 19) {
                a(data);
            }
        }
    }

    @TargetApi(19)
    private void a(Uri uri) {
        a.b(this, uri);
        getContentResolver().takePersistableUriPermission(uri, 3);
    }
}
