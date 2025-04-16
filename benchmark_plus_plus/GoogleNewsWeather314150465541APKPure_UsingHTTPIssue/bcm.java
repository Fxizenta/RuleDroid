package com.google.android.apps.genie.geniewidget;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class bcm {
    public static void a(bbm bbmVar, List list, Integer num, Integer num2) {
        if (num != null) {
            b(bbmVar);
        } else {
            a(bbmVar);
        }
        if (bbmVar.b(list)) {
            if (num != null) {
                a(bbmVar, num.intValue());
                return;
            } else {
                b(bbmVar, num2.intValue());
                return;
            }
        }
        String a = a(list, num, num2);
        a(bbmVar.a(), a);
        bbmVar.a(list);
        bbv.c("Instant Article Viewer: injectModelOrChooseIndex: [%s]: %s", bbmVar, a);
    }

    public static void a(bbm bbmVar, List list) {
        a(bbmVar);
        String a = a(list, null, 0);
        a(bbmVar.a(), a);
        bbmVar.a(list);
        bbv.c("Instant Article Viewer: injectModelAndPrerender: [%s]: %s", bbmVar, a);
    }

    public static void a(bbm bbmVar, int i) {
        b(bbmVar);
        String a = a(i);
        a(bbmVar.a(), a);
        bbv.c("Instant Article Viewer: injectSelectedIndexJS: [%s]: %s", bbmVar, a);
    }

    public static void b(bbm bbmVar, int i) {
        String b = b(i);
        a(bbmVar.a(), b);
        bbv.c("Instant Article Viewer: injectPrerenderJS: [%s]: %s", bbmVar, b);
    }

    private static void a(bbm bbmVar) {
        a(bbmVar.a(), "javascript:setFakeSize()");
        bbv.c("Instant Article Viewer: injectSetFakeSizeJs: [%s]: %s", bbmVar, "javascript:setFakeSize()");
    }

    private static void b(bbm bbmVar) {
        a(bbmVar.a(), "javascript:unsetFakeSize()");
        bbv.c("Instant Article Viewer: injectUnsetFakeSizeJs: [%s]: %s", bbmVar, "javascript:unsetFakeSize()");
    }

    private static String a(int i) {
        return new StringBuilder(40).append("javascript:setSelectedIndex(").append(i).append(")").toString();
    }

    private static String b(int i) {
        return new StringBuilder(41).append("javascript:setPrerenderIndex(").append(i).append(")").toString();
    }

    private static void a(WebView webView, String str) {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.evaluateJavascript(str, null);
        } else {
            webView.loadUrl(str);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r14v0, types: [java.lang.Integer] */
    /* JADX WARN: Type inference failed for: r14v1, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r14v2, types: [java.lang.String] */
    private static String a(List list, Integer num, Integer num2) {
        String valueOf = String.valueOf(b("n.apiv2.avm"));
        String valueOf2 = String.valueOf(",");
        StringBuilder sb = new StringBuilder(new StringBuilder(String.valueOf(valueOf).length() + 37 + String.valueOf(valueOf2).length()).append("javascript:setSerializedJspbModel('[").append(valueOf).append(valueOf2).append("[").toString());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            aww awwVar = (aww) it.next();
            String a = a(awwVar.b.h);
            String str = awwVar.b.i;
            String str2 = awwVar.b.h;
            if (a == null) {
                a = awwVar.b.f;
            }
            sb.append(a(str, str2, a, awwVar.b.b, bcc.b(awwVar.b)));
            sb.append(",");
        }
        if (list.size() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        if (num == 0) {
            num = "";
        }
        String valueOf3 = String.valueOf((Object) num);
        String valueOf4 = String.valueOf(",");
        Object obj = num2;
        if (num2 == null) {
            obj = "";
        }
        String valueOf5 = String.valueOf(obj);
        String valueOf6 = String.valueOf(",");
        String valueOf7 = String.valueOf(",");
        String valueOf8 = String.valueOf(",");
        String valueOf9 = String.valueOf(",");
        String valueOf10 = String.valueOf(",");
        String valueOf11 = String.valueOf(",");
        String valueOf12 = String.valueOf(",");
        sb.append(new StringBuilder(String.valueOf(valueOf3).length() + 10 + String.valueOf(valueOf4).length() + String.valueOf(valueOf5).length() + String.valueOf(valueOf6).length() + String.valueOf(valueOf7).length() + String.valueOf(valueOf8).length() + String.valueOf(valueOf9).length() + String.valueOf(valueOf10).length() + String.valueOf(valueOf11).length() + String.valueOf(valueOf12).length()).append("],").append(valueOf3).append(valueOf4).append(valueOf5).append(valueOf6).append(valueOf7).append(valueOf8).append(valueOf9).append(valueOf10).append(valueOf11).append(valueOf12).append("true").append("]');").toString());
        return sb.toString();
    }

    private static String a(String str, String str2, String str3, String str4, String str5) {
        String valueOf = String.valueOf(b("n.apiv2.avam"));
        String valueOf2 = String.valueOf(",");
        String valueOf3 = String.valueOf(b(str));
        String valueOf4 = String.valueOf(",");
        String valueOf5 = String.valueOf(b(str2));
        String valueOf6 = String.valueOf(",");
        String valueOf7 = String.valueOf(b(str3));
        String valueOf8 = String.valueOf(",");
        String valueOf9 = String.valueOf(",");
        String valueOf10 = String.valueOf(c(str4));
        String valueOf11 = String.valueOf(",");
        String valueOf12 = String.valueOf(c(str5));
        return new StringBuilder(String.valueOf(valueOf).length() + 2 + String.valueOf(valueOf2).length() + String.valueOf(valueOf3).length() + String.valueOf(valueOf4).length() + String.valueOf(valueOf5).length() + String.valueOf(valueOf6).length() + String.valueOf(valueOf7).length() + String.valueOf(valueOf8).length() + String.valueOf(valueOf9).length() + String.valueOf(valueOf10).length() + String.valueOf(valueOf11).length() + String.valueOf(valueOf12).length()).append("[").append(valueOf).append(valueOf2).append(valueOf3).append(valueOf4).append(valueOf5).append(valueOf6).append(valueOf7).append(valueOf8).append(valueOf9).append(valueOf10).append(valueOf11).append(valueOf12).append("]").toString();
    }

    private static String b(String str) {
        return new StringBuilder(String.valueOf(str).length() + 2).append("\"").append(str).append("\"").toString();
    }

    private static String c(String str) {
        return TextUtils.isEmpty(str) ? "" : b(str.replace("'", "\\'").replace("\"", "“"));
    }

    public static String a(String str) {
        String substring;
        String str2;
        if (str.matches("^https://cdn\\.ampproject\\.org/.*")) {
            substring = str.substring("https://cdn.ampproject.org/".length());
        } else {
            if (!str.matches("^http://cdn\\.ampproject\\.org/.*")) {
                return null;
            }
            substring = str.substring("http://cdn.ampproject.org/".length());
        }
        if (!substring.matches("^c/.*")) {
            return null;
        }
        String substring2 = substring.substring("c/".length());
        if (substring2.matches("^s/.*")) {
            substring2 = substring2.substring("s/".length());
            str2 = "https";
        } else {
            str2 = "http";
        }
        return new StringBuilder(String.valueOf(str2).length() + 3 + String.valueOf(substring2).length()).append(str2).append("://").append(substring2).toString();
    }
}
