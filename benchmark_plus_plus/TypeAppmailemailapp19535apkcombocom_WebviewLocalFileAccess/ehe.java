package defpackage;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.android.emaileas.mail.store.imap.ImapConstants;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Marker;

/* loaded from: classes.dex */
public class ehe {
    public static ehe cVG;
    protected WebView cVH;
    protected List<ehd> cVI;
    private String cVJ = "";
    private Context context;
    private Handler handler;

    protected ehe() {
        cVG = this;
    }

    public static ehe arn() {
        if (cVG == null) {
            cVG = new ehe();
        }
        return cVG;
    }

    public WebView getWebView() {
        return this.cVH;
    }

    public void a(Context context, String str, boolean z, boolean z2, String str2, int i, List<ehd> list) {
        this.context = context;
        this.handler = new Handler();
        this.cVI = list;
        this.cVJ = "http://localhost:" + i + "/";
        this.cVH = new WebView(context);
        WebSettings settings = this.cVH.getSettings();
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        this.cVH.setWebViewClient(new ehf(this, z2, i, context, str2));
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(2);
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(z);
        }
        this.cVH.setLayerType(2, null);
        if (z2) {
            this.cVH.loadUrl(this.cVJ + "index.html");
        } else {
            this.cVH.loadUrl(str);
        }
        for (ehd ehdVar : list) {
            this.cVH.addJavascriptInterface(ehdVar, ehdVar.getTag());
        }
    }

    public Handler getHandler() {
        return this.handler;
    }

    public WebResourceResponse q(String str, String str2, String str3) {
        InputStream open = this.context.getAssets().open(str);
        if (Build.VERSION.SDK_INT < 21) {
            return new WebResourceResponse(str2, str3, open);
        }
        HashMap hashMap = new HashMap();
        hashMap.put("Access-Control-Allow-Origin", Marker.ANY_MARKER);
        return new WebResourceResponse(str2, str3, 200, ImapConstants.OK, hashMap, open);
    }
}
