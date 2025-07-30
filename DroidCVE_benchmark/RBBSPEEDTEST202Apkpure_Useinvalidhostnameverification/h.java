package it.partytrack.sdk.a;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/* loaded from: classes.dex */
final class h implements HostnameVerifier {
    /* JADX INFO: Access modifiers changed from: package-private */
    public h(f fVar) {
    }

    @Override // javax.net.ssl.HostnameVerifier
    public final boolean verify(String str, SSLSession sSLSession) {
        return true;
    }
}
