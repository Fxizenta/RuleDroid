package com.getjar.sdk.comm;

import com.getjar.sdk.exceptions.CommunicationException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/* loaded from: classes.dex */
public class GetJarHttpClient extends DefaultHttpClient {
    public static GetJarHttpClient newInstance(String userAgent, int connectionTimeout, int socketTimeout) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, socketTimeout);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, false);
        HttpProtocolParams.setUserAgent(params, userAgent);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        SSLSocketFactory socketFactory = newSSLSocketFactoryInstance();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", socketFactory, 8443));
        schemeRegistry.register(new Scheme("https", socketFactory, 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        return new GetJarHttpClient(manager, params);
    }

    private GetJarHttpClient(ClientConnectionManager connManager, HttpParams httpParams) {
        super(connManager, httpParams);
    }

    private static SSLSocketFactoryTrustAll newSSLSocketFactoryInstance() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            trusted.load(null, null);
            SSLSocketFactoryTrustAll sslFactory = new SSLSocketFactoryTrustAll(trusted);
            sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sslFactory;
        } catch (IOException e) {
            throw new CommunicationException(e);
        } catch (KeyManagementException e2) {
            throw new CommunicationException(e2);
        } catch (KeyStoreException e3) {
            throw new CommunicationException(e3);
        } catch (NoSuchAlgorithmException e4) {
            throw new CommunicationException(e4);
        } catch (UnrecoverableKeyException e5) {
            throw new CommunicationException(e5);
        } catch (CertificateException e6) {
            throw new CommunicationException(e6);
        }
    }
}
