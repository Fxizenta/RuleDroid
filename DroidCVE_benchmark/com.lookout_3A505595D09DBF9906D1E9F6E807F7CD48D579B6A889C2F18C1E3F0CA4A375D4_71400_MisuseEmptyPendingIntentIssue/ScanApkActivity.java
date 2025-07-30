package com.lookout.security;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.lookout.C0000R;

/* loaded from: classes.dex */
public class ScanApkActivity extends Activity {

    /* renamed from: a, reason: collision with root package name */
    private final com.lookout.h.a.c f962a = new com.lookout.security.filesystem.m();

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Uri data = getIntent().getData();
        String lastPathSegment = data.getLastPathSegment();
        new s(this, lastPathSegment, Toast.makeText(getApplicationContext(), getString(C0000R.string.status_scan_failed, new Object[]{lastPathSegment}), 1), data).execute(new Void[0]);
    }
}
