package com.samsung.android.app.icalendar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import b.f.a.d.a.i;
import b.f.a.d.h.q.a.c;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public class ICalendarImportActivity extends Activity {

    /* renamed from: a, reason: collision with root package name */
    private static final String f19046a = b.f.a.b.w.a("ICalendarImportActivity");

    private void c() {
        ClipData clipData;
        Intent intent = getIntent();
        boolean z = intent != null;
        a(z, "No intent to process.");
        if (z) {
            b.f.a.b.w.a("ICalendar", " ICalendarImportActivity - processIntent");
            boolean a2 = b.f.a.d.h.q.d.a.a(this, b());
            a(a2, "Permission denied.", false);
            if (a2) {
                Uri data = intent.getData();
                if (data == null && (clipData = intent.getClipData()) != null && clipData.getItemCount() > 0) {
                    data = clipData.getItemAt(0).getUri();
                }
                boolean z2 = data != null;
                a(z2, "Intent does not contain any data to import.");
                if (z2) {
                    if (com.samsung.android.libcalendarfileprovider.i.a(this, data, (Charset) null) == 1) {
                        a(intent);
                    } else {
                        b(intent);
                    }
                }
            }
        }
    }

    public /* synthetic */ void a(String[] strArr) {
        c();
    }

    public /* synthetic */ void b(String[] strArr) {
        a();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String[] b2 = b();
        if (b.f.a.d.h.q.d.a.a(this, b2)) {
            c();
            return;
        }
        Intent intent = getIntent();
        if ((intent.getFlags() & 268435456) == 268435456) {
            b.f.a.b.w.h("ICalendar", f19046a + "Redirecting to ICalendarListActivity due to FLAG_ACTIVITY_NEW_TASK.");
            b(intent);
            return;
        }
        b.f.a.d.h.q.b a2 = b.f.a.d.h.q.b.a(this);
        a2.a(b2);
        a2.a(c.a.UNABLE_TO_OPEN_APP);
        a2.a(new b.f.a.d.h.q.d() { // from class: com.samsung.android.app.icalendar.f
            @Override // b.f.a.d.h.q.d
            public final void a(String[] strArr) {
                ICalendarImportActivity.this.a(strArr);
            }
        }, new b.f.a.d.h.q.c() { // from class: com.samsung.android.app.icalendar.g
            @Override // b.f.a.d.h.q.c
            public final void a(String[] strArr) {
                ICalendarImportActivity.this.b(strArr);
            }
        });
        a2.b();
    }

    private void a(final Intent intent) {
        b.f.a.b.w.a("ICalendar", " ICalendarImportActivity - launchImportDetail");
        com.samsung.android.app.icalendar.b.h.a(this).a(intent).a(new i.a() { // from class: com.samsung.android.app.icalendar.h
            @Override // b.f.a.d.a.i.a
            public final void accept(Object obj) {
                ICalendarImportActivity.this.a(intent, (com.samsung.android.app.icalendar.b.e.b) obj);
            }
        });
    }

    private void b(Intent intent) {
        Intent intent2 = new Intent(intent);
        intent2.setClass(this, ICalendarListActivity.class);
        try {
            startActivity(intent2);
        } catch (ActivityNotFoundException unused) {
            b.f.a.b.w.b("ICalendar", f19046a + "Failed to start import list activity.");
        }
        finish();
    }

    public /* synthetic */ void a(Intent intent, com.samsung.android.app.icalendar.b.e.b bVar) {
        boolean z = bVar != null;
        a(z, "Failed to parse the data.");
        if (z) {
            boolean c2 = bVar.c();
            a(c2, bVar.a(getResources()));
            if (c2) {
                b.f.a.e.b.a a2 = bVar.a();
                boolean z2 = a2.b() != 0;
                a(z2, "There are no valid items to import.");
                if (z2) {
                    if (a2.b() > 1) {
                        b.f.a.b.w.k("ICalendar", f19046a + "Unexpected number of components. Launching import list.");
                        b(intent);
                        return;
                    }
                    try {
                        v.a(this, a2.a(0));
                    } catch (ActivityNotFoundException unused) {
                        b.f.a.b.w.b("ICalendar", f19046a + "Failed to start import detail activity.");
                    }
                    finish();
                }
            }
        }
    }

    private String[] b() {
        ClipData clipData;
        String[] strArr = b.f.a.d.h.q.e.f.b.f6152c;
        Intent intent = getIntent();
        boolean z = intent != null;
        a(z, "No intent to process.");
        if (!z) {
            return strArr;
        }
        Uri data = intent.getData();
        if (data == null && (clipData = intent.getClipData()) != null && clipData.getItemCount() > 0) {
            data = clipData.getItemAt(0).getUri();
        }
        boolean z2 = data != null;
        a(z2, "Intent does not contain any data to import.");
        return (z2 && "content".equals(data.getScheme()) && !"media".equals(data.getAuthority())) ? b.f.a.d.h.q.e.f.a.f6151c : strArr;
    }

    private void a() {
        finish();
        overridePendingTransition(0, 0);
    }

    private boolean a(boolean z, String str) {
        a(z, str, true);
        return z;
    }

    private boolean a(boolean z, String str, boolean z2) {
        if (!z) {
            if (str != null) {
                b.f.a.b.w.d("ICalendar", f19046a + str);
            }
            if (z2) {
                b.f.a.b.G.a(this, getString(G.parse_error));
            }
            finish();
        }
        return z;
    }
}
