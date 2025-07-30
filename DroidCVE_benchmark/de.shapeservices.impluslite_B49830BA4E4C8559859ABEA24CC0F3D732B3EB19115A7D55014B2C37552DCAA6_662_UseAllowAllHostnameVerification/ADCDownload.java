package com.jirbo.adcolony;

import java.io.File;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ADCDownload extends ADCEvent implements Runnable {
    ADCController controller;
    String data;
    File file;
    Object info;
    Listener listener;
    String post_content_type;
    String post_data;
    int size;
    SSLContext ssl_context;
    boolean success;
    boolean third_party_tracking;
    String url;
    boolean use_ssl;

    /* loaded from: classes.dex */
    public interface Listener {
        void on_download_finished(ADCDownload aDCDownload);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ADCDownload(ADCController aDCController, String str, Listener listener) {
        this(aDCController, str, listener, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ADCDownload(ADCController aDCController, String str, Listener listener, String str2) {
        super(aDCController, false);
        this.url = str;
        this.listener = listener;
        if (str2 != null) {
            this.file = new File(str2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ADCDownload with_info(Object obj) {
        this.info = obj;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ADCDownload with_post_data(String str, String str2) {
        this.post_content_type = str;
        this.post_data = str2;
        return this;
    }

    public void start() {
        ADCThreadPool.run(this);
    }

    /* JADX WARN: Removed duplicated region for block: B:75:0x0387  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01d1 A[EDGE_INSN: B:83:0x01d1->B:84:0x01d1 BREAK  A[LOOP:0: B:2:0x0004->B:80:0x0391], SYNTHETIC] */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void run() {
        /*
            Method dump skipped, instructions count: 941
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jirbo.adcolony.ADCDownload.run():void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.jirbo.adcolony.ADCEvent
    public void dispatch() {
        this.listener.on_download_finished(this);
    }

    /* loaded from: classes.dex */
    class DefaultTrustManager implements X509TrustManager {
        private DefaultTrustManager() {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
