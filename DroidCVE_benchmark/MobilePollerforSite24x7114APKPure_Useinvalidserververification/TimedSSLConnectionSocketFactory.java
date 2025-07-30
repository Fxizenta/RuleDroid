package com.site24x7.android.agent.httpclient;

import android.annotation.TargetApi;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifierHC4;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifierHC4;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.conn.ssl.StrictHostnameVerifierHC4;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@ThreadSafe
/* loaded from: classes.dex */
public final class TimedSSLConnectionSocketFactory implements LayeredConnectionSocketFactory {
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    private static final String TAG = "HttpClient";
    public static final String TLS = "TLS";
    private final X509HostnameVerifier hostnameVerifier;
    private final SSLSocketFactory socketfactory;
    private final String[] supportedCipherSuites;
    private final String[] supportedProtocols;
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifierHC4();
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifierHC4();
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifierHC4();

    public static TimedSSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
        return new TimedSSLConnectionSocketFactory((SSLSocketFactory) SSLCertificateSocketFactory.getDefault(0), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    public static TimedSSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new TimedSSLConnectionSocketFactory((SSLSocketFactory) SSLCertificateSocketFactory.getDefault(0), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    public TimedSSLConnectionSocketFactory(SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
        this.socketfactory = (SSLSocketFactory) Args.notNull(socketfactory, "SSL socket factory");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.hostnameVerifier = hostnameVerifier == null ? BROWSER_COMPATIBLE_HOSTNAME_VERIFIER : hostnameVerifier;
    }

    public TimedSSLConnectionSocketFactory(SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier) {
        this(socketfactory, (String[]) null, (String[]) null, hostnameVerifier);
    }

    public TimedSSLConnectionSocketFactory(SSLContext sslContext) {
        this(sslContext, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    public TimedSSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
        this(((SSLContext) Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
    }

    public TimedSSLConnectionSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
        this(((SSLContext) Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[]) null, (String[]) null, hostnameVerifier);
    }

    @Override // org.apache.http.conn.socket.ConnectionSocketFactory
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        Args.notNull(host, "HTTP host");
        Args.notNull(remoteAddress, "Remote address");
        TimedHttpClientContext timedContext = (TimedHttpClientContext) context;
        List<ConnectionMetrics> connectionMetrics = timedContext.getConnectionMetrics();
        ConnectionMetrics currentMetrics = connectionMetrics.get(connectionMetrics.size() - 1);
        if (currentMetrics.socketStartTime != null || currentMetrics.socketEndTime != null) {
            throw new IllegalStateException("Last record already contains socketStartTime or socketEndTime.");
        }
        if (currentMetrics.sslStartTime != null || currentMetrics.sslEndTime != null) {
            throw new IllegalStateException("Last record already contains sslStartTime or sslEndTime.");
        }
        Socket sock = socket != null ? socket : createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            Long socketStartTime = Long.valueOf(System.currentTimeMillis());
            sock.connect(remoteAddress, connectTimeout);
            currentMetrics.socketEndTime = Long.valueOf(System.currentTimeMillis());
            currentMetrics.socketStartTime = socketStartTime;
            if (sock instanceof SSLSocket) {
                SSLSocket sslsock = (SSLSocket) sock;
                Long sslStartTime = Long.valueOf(System.currentTimeMillis());
                sslsock.startHandshake();
                verifyHostname(sslsock, host.getHostName());
                currentMetrics.sslEndTime = Long.valueOf(System.currentTimeMillis());
                currentMetrics.sslStartTime = sslStartTime;
                return sock;
            }
            Long sslStartTime2 = Long.valueOf(System.currentTimeMillis());
            Socket layeredSocket = createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
            currentMetrics.sslEndTime = Long.valueOf(System.currentTimeMillis());
            currentMetrics.sslStartTime = sslStartTime2;
            return layeredSocket;
        } catch (IOException ex) {
            try {
                sock.close();
            } catch (IOException e) {
            }
            throw ex;
        }
    }

    @Override // org.apache.http.conn.socket.LayeredConnectionSocketFactory
    @TargetApi(17)
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
        SSLSocket sslsock = (SSLSocket) this.socketfactory.createSocket(socket, target, port, true);
        if (this.supportedProtocols != null) {
            sslsock.setEnabledProtocols(this.supportedProtocols);
        }
        if (this.supportedCipherSuites != null) {
            sslsock.setEnabledCipherSuites(this.supportedCipherSuites);
        }
        prepareSocket(sslsock);
        if (Build.VERSION.SDK_INT >= 17 && (this.socketfactory instanceof SSLCertificateSocketFactory)) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Enabling SNI for " + target);
            }
            ((SSLCertificateSocketFactory) this.socketfactory).setHostname(sslsock, target);
        }
        sslsock.startHandshake();
        verifyHostname(sslsock, target);
        return sslsock;
    }

    @Override // org.apache.http.conn.socket.ConnectionSocketFactory
    public Socket createSocket(HttpContext context) throws IOException {
        return SocketFactory.getDefault().createSocket();
    }

    private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
        try {
            this.hostnameVerifier.verify(hostname, sslsock);
        } catch (IOException iox) {
            try {
                sslsock.close();
            } catch (Exception e) {
            }
            throw iox;
        }
    }

    protected void prepareSocket(SSLSocket socket) throws IOException {
    }

    X509HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
}
