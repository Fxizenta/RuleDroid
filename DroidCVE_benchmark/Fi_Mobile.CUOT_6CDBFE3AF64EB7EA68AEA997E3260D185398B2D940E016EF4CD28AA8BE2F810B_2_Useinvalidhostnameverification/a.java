package a.a.a.c.d;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/* loaded from: classes.dex */
public abstract class a implements f {

    /* renamed from: a, reason: collision with root package name */
    private static final String[] f199a;

    static {
        String[] strArr = {"ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org"};
        f199a = strArr;
        Arrays.sort(strArr);
    }

    private static int a(String str) {
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (str.charAt(i2) == '.') {
                i++;
            }
        }
        return i;
    }

    private void a(String str, X509Certificate x509Certificate) {
        String[] strArr;
        LinkedList linkedList = new LinkedList();
        StringTokenizer stringTokenizer = new StringTokenizer(x509Certificate.getSubjectX500Principal().toString(), ",");
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            int indexOf = nextToken.indexOf("CN=");
            if (indexOf >= 0) {
                linkedList.add(nextToken.substring(indexOf + 3));
            }
        }
        if (linkedList.isEmpty()) {
            strArr = null;
        } else {
            strArr = new String[linkedList.size()];
            linkedList.toArray(strArr);
        }
        a(str, strArr, a(x509Certificate, str));
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00b6  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00f7 A[EDGE_INSN: B:59:0x00f7->B:60:0x00f7 BREAK  A[LOOP:1: B:24:0x005c->B:64:?], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:? A[LOOP:1: B:24:0x005c->B:64:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0135  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void a(java.lang.String r11, java.lang.String[] r12, java.lang.String[] r13, boolean r14) {
        /*
            Method dump skipped, instructions count: 315
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: a.a.a.c.d.a.a(java.lang.String, java.lang.String[], java.lang.String[], boolean):void");
    }

    private static String[] a(X509Certificate x509Certificate, String str) {
        Collection<List<?>> collection;
        int i = b(str) ? 7 : 2;
        LinkedList linkedList = new LinkedList();
        try {
            collection = x509Certificate.getSubjectAlternativeNames();
        } catch (CertificateParsingException e) {
            Logger.getLogger(a.class.getName()).log(Level.FINE, "Error parsing certificate.", (Throwable) e);
            collection = null;
        }
        if (collection != null) {
            for (List<?> list : collection) {
                if (((Integer) list.get(0)).intValue() == i) {
                    linkedList.add((String) list.get(1));
                }
            }
        }
        if (linkedList.isEmpty()) {
            return null;
        }
        String[] strArr = new String[linkedList.size()];
        linkedList.toArray(strArr);
        return strArr;
    }

    private static boolean b(String str) {
        return str != null && (a.a.a.c.e.a.a(str) || a.a.a.c.e.a.b(str));
    }

    @Override // a.a.a.c.d.f
    public final void a(String str, SSLSocket sSLSocket) {
        if (str == null) {
            throw new NullPointerException("host to verify is null");
        }
        SSLSession session = sSLSocket.getSession();
        if (session == null) {
            sSLSocket.getInputStream().available();
            session = sSLSocket.getSession();
            if (session == null) {
                sSLSocket.startHandshake();
                session = sSLSocket.getSession();
            }
        }
        a(str, (X509Certificate) session.getPeerCertificates()[0]);
    }

    @Override // javax.net.ssl.HostnameVerifier
    public final boolean verify(String str, SSLSession sSLSession) {
        try {
            a(str, (X509Certificate) sSLSession.getPeerCertificates()[0]);
            return true;
        } catch (SSLException e) {
            return false;
        }
    }
}
