package b1.mobile.http.client;

import b1.mobile.http.interfaces.IHTTPInputOutput;
import b1.mobile.util.FileUtil;
import b1.mobile.util.MLog;
import b1.mobile.util.ResUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/* loaded from: classes.dex */
public abstract class BaseHttpClient {
    static final String HTTP = "http";
    static final String HTTPS = "https";
    private static boolean isCertificateEnabled = false;

    public static URL buildURL(String protocol, String host, String port, String path) {
        int iPort = 80;
        try {
            iPort = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            MLog.e(ex, ex.getMessage(), new Object[0]);
        }
        try {
            URL url = new URL(protocol, host, iPort, path);
            return url;
        } catch (MalformedURLException ex2) {
            MLog.e(ex2.getMessage(), new Object[0]);
            return null;
        }
    }

    public static HttpURLConnection newConnection(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            if (url.getProtocol().equals(HTTPS)) {
                HttpsURLConnection sslConn = (HttpsURLConnection) connection;
                sslConn.setSSLSocketFactory(getSSLFactory());
                sslConn.setHostnameVerifier(new HostnameVerifier() { // from class: b1.mobile.http.client.BaseHttpClient.1
                    @Override // javax.net.ssl.HostnameVerifier
                    public boolean verify(String s, SSLSession sslSession) {
                        boolean retVerified = HttpsURLConnection.getDefaultHostnameVerifier().verify(s, sslSession);
                        if (!retVerified) {
                            try {
                                Certificate[] certs = sslSession.getPeerCertificates();
                                for (Certificate peerCert : certs) {
                                    X509Certificate cert509 = (X509Certificate) peerCert;
                                    Principal subjectDN = cert509.getSubjectDN();
                                    String subjectDNString = subjectDN.toString();
                                    int index = subjectDNString.indexOf("CN=" + s);
                                    retVerified = index >= 0;
                                    if (retVerified) {
                                        break;
                                    }
                                }
                            } catch (SSLPeerUnverifiedException e) {
                                e.printStackTrace();
                            }
                        }
                        return retVerified;
                    }
                });
            }
        } catch (Exception ex) {
            MLog.e(ex, ex.getMessage(), new Object[0]);
        }
        return connection;
    }

    private static TrustManager[] getTrustManagers() throws Exception {
        if (!isCertificateEnabled) {
            return new TrustManager[]{new TrustAnyTrustManager()};
        }
        KeyStore keyStore = KeyStore.getInstance("AndroidCAStore");
        keyStore.load(null, null);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        return tmf.getTrustManagers();
    }

    static SSLSocketFactory getSSLFactory() throws Exception {
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, getTrustManagers(), null);
        context.getSocketFactory();
        return context.getSocketFactory();
    }

    public static void doConnect(String requestMethod, HttpURLConnection connection, IHTTPInputOutput inputOutput) {
        InputStream inputStream;
        try {
            connection.setRequestMethod(requestMethod);
        } catch (ProtocolException ex) {
            MLog.e(ex, ex.getMessage(), new Object[0]);
        }
        connection.setRequestProperty("HOST", connection.getURL().getHost());
        try {
            if (!requestMethod.equals("GET")) {
                connection.setDoOutput(true);
                OutputStream outputStream = null;
                try {
                    outputStream = connection.getOutputStream();
                    inputOutput.write(outputStream);
                } finally {
                    FileUtil.safeFlushAndClose(outputStream);
                }
            }
            int statusCode = -1;
            try {
                statusCode = connection.getResponseCode();
            } catch (IOException ex2) {
                if (ex2.getMessage().contains("authentication challenge")) {
                    statusCode = 401;
                    inputOutput.read(ex2.getMessage());
                }
            }
            if (statusCode == 502) {
                inputOutput.read(ResUtil.getStringRes("CONN_REOPEN_AND_TRY_AGAIN"));
            } else if (statusCode != 401 && statusCode != 403) {
                if (statusCode == 500) {
                    if (connection.getErrorStream() != null) {
                        inputStream = connection.getErrorStream();
                        try {
                            inputOutput.read(inputStream);
                            FileUtil.safeClose(inputStream);
                        } finally {
                        }
                    }
                } else {
                    inputStream = null;
                    try {
                        inputStream = connection.getInputStream();
                        inputOutput.read(inputStream);
                        inputStream.close();
                        FileUtil.safeClose(inputStream);
                    } finally {
                    }
                }
            }
            inputOutput.done(statusCode);
        } catch (SSLHandshakeException ex3) {
            inputOutput.read(ex3.toString());
            inputOutput.done(-1);
        } catch (Exception ex4) {
            inputOutput.read(ex4.getMessage());
            inputOutput.done(-1);
        }
        connection.disconnect();
    }
}
