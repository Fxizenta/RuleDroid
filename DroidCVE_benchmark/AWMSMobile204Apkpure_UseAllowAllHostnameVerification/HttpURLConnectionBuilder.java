package fujixerox.apeosware.mobileapps.android;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class HttpURLConnectionBuilder implements X509TrustManager, HostnameVerifier {
    private SSLContext sslContext;

    private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        if (this.sslContext == null) {
            this.sslContext = SSLContext.getInstance("SSL");
            this.sslContext.init(null, new TrustManager[]{this}, new SecureRandom());
        }
        return this.sslContext;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002d, code lost:
    
        r1 = null;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v4, types: [java.net.HttpURLConnection] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.net.HttpURLConnection openConnection(java.lang.String r6) {
        /*
            r5 = this;
            boolean r4 = fujixerox.apeosware.mobileapps.android.helpers.TextUtil.isValid(r6)
            if (r4 != 0) goto L6
        L6:
            java.net.URL r3 = new java.net.URL     // Catch: java.lang.Exception -> L36
            r3.<init>(r6)     // Catch: java.lang.Exception -> L36
            java.lang.String r4 = r3.getProtocol()     // Catch: java.lang.Exception -> L36
            fujixerox.apeosware.mobileapps.android.WebProtocol r2 = fujixerox.apeosware.mobileapps.android.WebProtocol.findByName(r4)     // Catch: java.lang.Exception -> L36
            if (r2 == 0) goto L3a
            fujixerox.apeosware.mobileapps.android.WebProtocol r4 = fujixerox.apeosware.mobileapps.android.WebProtocol.https     // Catch: java.lang.Exception -> L36
            if (r2 != r4) goto L2e
            java.net.URLConnection r1 = r3.openConnection()     // Catch: java.lang.Exception -> L36
            javax.net.ssl.HttpsURLConnection r1 = (javax.net.ssl.HttpsURLConnection) r1     // Catch: java.lang.Exception -> L36
            javax.net.ssl.SSLContext r4 = r5.getSSLContext()     // Catch: java.lang.Exception -> L36
            javax.net.ssl.SSLSocketFactory r4 = r4.getSocketFactory()     // Catch: java.lang.Exception -> L36
            r1.setSSLSocketFactory(r4)     // Catch: java.lang.Exception -> L36
            r1.setHostnameVerifier(r5)     // Catch: java.lang.Exception -> L36
        L2d:
            return r1
        L2e:
            java.net.URLConnection r4 = r3.openConnection()     // Catch: java.lang.Exception -> L36
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch: java.lang.Exception -> L36
            r1 = r4
            goto L2d
        L36:
            r0 = move-exception
            r0.printStackTrace()
        L3a:
            r1 = 0
            goto L2d
        */
        throw new UnsupportedOperationException("Method not decompiled: fujixerox.apeosware.mobileapps.android.HttpURLConnectionBuilder.openConnection(java.lang.String):java.net.HttpURLConnection");
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] aCertificatesArray, String anAuthType) {
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] aCertificatesArray, String anAuthType) {
    }

    @Override // javax.net.ssl.HostnameVerifier
    public boolean verify(String aHostName, SSLSession anSSLSession) {
        return true;
    }
}
