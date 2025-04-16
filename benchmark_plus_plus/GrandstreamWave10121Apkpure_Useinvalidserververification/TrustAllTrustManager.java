package com.unboundid.util.ssl;

import com.unboundid.util.NotMutable;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;
import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.X509TrustManager;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
@NotMutable
/* loaded from: classes.dex */
public final class TrustAllTrustManager implements Serializable, X509TrustManager {
    private static final long serialVersionUID = -1295254056169520318L;
    private final boolean examineValidityDates;

    public TrustAllTrustManager() {
        this.examineValidityDates = false;
    }

    public TrustAllTrustManager(boolean z) {
        this.examineValidityDates = z;
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
        if (this.examineValidityDates) {
            Date date = new Date();
            for (X509Certificate x509Certificate : x509CertificateArr) {
                x509Certificate.checkValidity(date);
            }
        }
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
        if (this.examineValidityDates) {
            Date date = new Date();
            for (X509Certificate x509Certificate : x509CertificateArr) {
                x509Certificate.checkValidity(date);
            }
        }
    }

    public boolean examineValidityDates() {
        return this.examineValidityDates;
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
