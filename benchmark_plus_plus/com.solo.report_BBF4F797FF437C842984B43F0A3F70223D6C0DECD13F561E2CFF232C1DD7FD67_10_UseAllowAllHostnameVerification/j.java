package com.appyet.g;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class j implements HostnameVerifier {

    /* renamed from: a, reason: collision with root package name */
    final /* synthetic */ i f404a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public j(i iVar) {
        this.f404a = iVar;
    }

    @Override // javax.net.ssl.HostnameVerifier
    public final boolean verify(String str, SSLSession sSLSession) {
        return true;
    }
}
