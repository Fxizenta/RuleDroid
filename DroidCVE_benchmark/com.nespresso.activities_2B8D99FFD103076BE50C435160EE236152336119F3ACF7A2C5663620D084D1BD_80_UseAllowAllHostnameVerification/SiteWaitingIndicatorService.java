package com.nespresso.mobile.nespresso.service.queuemanagement.esirius.sitewaitingindicator;

import android.content.Context;
import com.nespresso.mobile.nespresso.R;
import com.nespresso.mobile.nespresso.domain.queuemanagement.esirius.sitewaitingindicator.ArrayOfString;
import com.nespresso.mobile.nespresso.domain.queuemanagement.esirius.sitewaitingindicator.serviceWaitingIndicatorList;
import com.nespresso.mobile.nespresso.domain.queuemanagement.esirius.sitewaitingindicator.siteWaitingIndicatorList;
import com.nespresso.mobile.nespresso.domain.queuemanagement.esirius.sitewaitingindicator.string2ArrayOfStringMap;
import com.neurospeech.wsclient.SoapRequest;
import com.neurospeech.wsclient.SoapResponse;
import com.neurospeech.wsclient.SoapWebService;
import com.neurospeech.wsclient.WSHelper;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.w3c.dom.Element;

/* loaded from: classes.dex */
public class SiteWaitingIndicatorService extends SoapWebService {
    protected Context mContext;

    public SiteWaitingIndicatorService(Context context) {
        setUrl("/esirius/webservices/sitewaitingindicator/v1.0");
        this.mContext = context;
    }

    @Override // com.neurospeech.wsclient.SoapWebService
    protected String getNamespaces() {
        return " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \r\n xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \r\n xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" \r\n xmlns:ns4=\"http://www.esii.com/esirius/sitewaitingindicator/v1.0\" \r\n xmlns:ns5=\"http://util.java\" \r\n xmlns:ns6=\"http://bean.v1_0.sitewaitingindicator.webservices.esirius.es2i.com\" \r\n";
    }

    @Override // com.neurospeech.wsclient.SoapWebService
    protected void appendNamespaces(Element e) {
        e.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        e.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        e.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
        e.setAttribute("xmlns:ns4", "http://www.esii.com/esirius/sitewaitingindicator/v1.0");
        e.setAttribute("xmlns:ns5", "http://util.java");
        e.setAttribute("xmlns:ns6", "http://bean.v1_0.sitewaitingindicator.webservices.esirius.es2i.com");
    }

    public siteWaitingIndicatorList getSitesIndicators() throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getSitesIndicators ____method = new SiteWaitingIndicatorService_getSitesIndicators();
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getSitesIndicatorsResponse __response = SiteWaitingIndicatorService_getSitesIndicatorsResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getsiteWaitingIndicatorList();
    }

    public siteWaitingIndicatorList getAllIndicatorsBySites(ArrayOfString siteCodeArray) throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getAllIndicatorsBySites ____method = new SiteWaitingIndicatorService_getAllIndicatorsBySites();
        ____method.setsiteCodeArray(siteCodeArray);
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getAllIndicatorsBySitesResponse __response = SiteWaitingIndicatorService_getAllIndicatorsBySitesResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getsiteWaitingIndicatorList();
    }

    public serviceWaitingIndicatorList getServiceIndicatorsBySiteAndServices(String siteCode, ArrayOfString serviceIdArray) throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndServices ____method = new SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndServices();
        ____method.setsiteCode(siteCode);
        ____method.setserviceIdArray(serviceIdArray);
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndServicesResponse __response = SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndServicesResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getserviceWaitingIndicatorList();
    }

    public siteWaitingIndicatorList getAllIndicatorsFilteredByServiceType() throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getAllIndicatorsFilteredByServiceType ____method = new SiteWaitingIndicatorService_getAllIndicatorsFilteredByServiceType();
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getAllIndicatorsFilteredByServiceTypeResponse __response = SiteWaitingIndicatorService_getAllIndicatorsFilteredByServiceTypeResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getsiteWaitingIndicatorList();
    }

    public serviceWaitingIndicatorList getServiceIndicatorsBySiteAndFilters(String siteCode, string2ArrayOfStringMap filterMap) throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndFilters ____method = new SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndFilters();
        ____method.setsiteCode(siteCode);
        ____method.setfilterMap(filterMap);
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndFiltersResponse __response = SiteWaitingIndicatorService_getServiceIndicatorsBySiteAndFiltersResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getserviceWaitingIndicatorList();
    }

    public siteWaitingIndicatorList getSitesIndicatorsBySites(ArrayOfString siteCodeArray) throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getSitesIndicatorsBySites ____method = new SiteWaitingIndicatorService_getSitesIndicatorsBySites();
        ____method.setsiteCodeArray(siteCodeArray);
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getSitesIndicatorsBySitesResponse __response = SiteWaitingIndicatorService_getSitesIndicatorsBySitesResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getsiteWaitingIndicatorList();
    }

    public siteWaitingIndicatorList getAllIndicators() throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getAllIndicators ____method = new SiteWaitingIndicatorService_getAllIndicators();
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getAllIndicatorsResponse __response = SiteWaitingIndicatorService_getAllIndicatorsResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getsiteWaitingIndicatorList();
    }

    public serviceWaitingIndicatorList getServiceIndicatorsBySites(ArrayOfString siteCodeArray) throws Exception {
        SoapRequest ___req = buildSoapRequest("\"\"");
        WSHelper ws = new WSHelper(___req.document);
        SiteWaitingIndicatorService_getServiceIndicatorsBySites ____method = new SiteWaitingIndicatorService_getServiceIndicatorsBySites();
        ____method.setsiteCodeArray(siteCodeArray);
        ___req.method = ____method.toXMLElement(ws, ___req.root);
        SoapResponse sr = getSoapResponse(___req);
        SiteWaitingIndicatorService_getServiceIndicatorsBySitesResponse __response = SiteWaitingIndicatorService_getServiceIndicatorsBySitesResponse.loadFrom((Element) sr.body.getFirstChild());
        return __response.getserviceWaitingIndicatorList();
    }

    @Override // com.neurospeech.wsclient.HttpWebService
    protected DefaultHttpClient getHttpClient() throws Exception {
        X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        DefaultHttpClient client = new DefaultHttpClient();
        SchemeRegistry registry = new SchemeRegistry();
        KeyStore trusted = KeyStore.getInstance("BKS");
        trusted.load(null, "".toCharArray());
        SSLSocketFactory sslf = new SSLSocketFactory(trusted);
        sslf.setHostnameVerifier(hostnameVerifier);
        registry.register(new Scheme("https", new MySSLSocketFactory(trusted), 443));
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager mgr = new ThreadSafeClientConnManager(client.getParams(), registry);
        DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
        CredentialsProvider credentialsProvider = httpClient.getCredentialsProvider();
        if (credentialsProvider != null) {
            String wsLogin = this.mContext.getString(R.string.esirius_http_user);
            String wsPwd = this.mContext.getString(R.string.esirius_http_pw);
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(wsLogin, wsPwd));
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        return httpClient;
    }

    /* loaded from: classes.dex */
    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext;

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            this.sslContext = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() { // from class: com.nespresso.mobile.nespresso.service.queuemanagement.esirius.sitewaitingindicator.SiteWaitingIndicatorService.MySSLSocketFactory.1
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            this.sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
        public Socket createSocket() throws IOException {
            return this.sslContext.getSocketFactory().createSocket();
        }
    }
}
