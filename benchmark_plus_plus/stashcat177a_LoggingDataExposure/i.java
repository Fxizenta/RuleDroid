package de.heinekingmedia.stashcat_api.model.channel;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* loaded from: classes2.dex */
public class i extends de.heinekingmedia.stashcat_api.model.d.b {

    /* renamed from: b, reason: collision with root package name */
    private final String f6917b;

    /* renamed from: c, reason: collision with root package name */
    private final long f6918c;

    public i(long j, String str) {
        this.f6918c = j;
        this.f6917b = str;
    }

    @Override // de.heinekingmedia.stashcat_api.model.d.b
    public String a() {
        String str = "";
        try {
            str = URLEncoder.encode(this.f6917b, "UTF-8");
        } catch (UnsupportedEncodingException e2) {
            Log.e("LoginData", Log.getStackTraceString(e2));
        }
        return super.a() + "&password=" + str + "&channel_id=" + this.f6918c;
    }
}
