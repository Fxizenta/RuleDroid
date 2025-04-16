package com.mtel.Tools.Net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;

/* loaded from: classes.dex */
public class NetUtil {
    public static String defaultUseragent = null;
    public static String defaultEncoding = "UTF-8";
    public static int intDefaultTimeout = 86400000;
    public static int intAutoRetryTime = 3;
    public static String defaultPostEncoding = null;
    public static boolean bndefallowFollow = false;
    protected static boolean bnAllowInvalidSSLCert = false;
    protected static boolean bnShowForwardedFor = false;
    protected static boolean bnShowYahooRemoteIP = false;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class GetResult {
        int intStatus = -1;
        HttpGet method;
        HttpResponse response;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class PostResult {
        int intStatus = -1;
        HttpPost method;
        HttpResponse response;
    }

    public static int getAutoRetryTime() {
        return intAutoRetryTime;
    }

    public static void setAutoRetryTime(int intAutoRetryTimeA) {
        intAutoRetryTime = intAutoRetryTimeA;
    }

    public static boolean getDefAllowFollow() {
        return bndefallowFollow;
    }

    public static void setDefAllowFollow(boolean bninputallow302) {
        bndefallowFollow = bninputallow302;
    }

    public static boolean getDefShowHeaderForwardedFor() {
        return bnShowForwardedFor;
    }

    public static void setDefShowHeaderForwardedFor(boolean bnInputShowHeaderForwardedFor) {
        bnShowForwardedFor = bnInputShowHeaderForwardedFor;
    }

    public static boolean getDefShowHeaderYahooRemoteIP() {
        return bnShowYahooRemoteIP;
    }

    public static void setDefShowHeaderYahooRemoteIP(boolean bnInputShowHeaderYahooRemoteIP) {
        bnShowYahooRemoteIP = bnInputShowHeaderYahooRemoteIP;
    }

    public static boolean getAllowInvalidSSLCert() {
        return bnAllowInvalidSSLCert;
    }

    public static void setAllowInvalidSSLCert(boolean bnAllowInvalidSSLCert1) {
        bnAllowInvalidSSLCert = bnAllowInvalidSSLCert1;
    }

    public static String getUseragent() {
        return defaultUseragent;
    }

    public static void setUseragent(String strUseragent) {
        defaultUseragent = strUseragent;
    }

    public static String getDefEncoding() {
        return defaultEncoding;
    }

    public static void setDefEncoding(String strEncoding) {
        defaultEncoding = strEncoding;
    }

    public static String getDefPostEncoding() {
        return defaultPostEncoding;
    }

    public static void setDefPostEncoding(String strEncoding) {
        defaultPostEncoding = strEncoding;
    }

    public static String getInternetAddress() throws IOException {
        Enumeration<NetworkInterface> nicList = NetworkInterface.getNetworkInterfaces();
        while (nicList.hasMoreElements()) {
            NetworkInterface nic = nicList.nextElement();
            Enumeration<InetAddress> addrList = nic.getInetAddresses();
            while (addrList.hasMoreElements()) {
                InetAddress addr = addrList.nextElement();
                if (!addr.isLoopbackAddress()) {
                    String strIAddr = addr.getHostAddress();
                    if (!strIAddr.startsWith("127.")) {
                        return strIAddr;
                    }
                }
            }
        }
        return null;
    }

    protected static DefaultHttpClient getHttpClient() {
        if (bnAllowInvalidSSLCert) {
            DefaultHttpClient base = new DefaultHttpClient();
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory ssf = new MySSLSocketFactory(trustStore);
                ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                ClientConnectionManager ccm = base.getConnectionManager();
                SchemeRegistry sr = ccm.getSchemeRegistry();
                sr.register(new Scheme("https", ssf, 443));
                return new DefaultHttpClient(ccm, base.getParams());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return new DefaultHttpClient();
    }

    public static String getResult(String strUrl) throws IOException {
        return getResult(strUrl, defaultEncoding);
    }

    public static String getResult(String strUrl, String encoding) throws IOException {
        String strResult;
        InputStream in = new URL(strUrl).openStream();
        try {
            if (encoding != null) {
                strResult = inputStream2String(in, encoding);
            } else {
                strResult = inputStream2String(in, defaultEncoding);
            }
            return strResult;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    protected static GetResult createGetMethod(String strUrl, int timeout, int intRetryTime, String username, String password) throws IllegalStateException, ClientProtocolException, IOException {
        return createGetMethod(strUrl, null, null, timeout, intRetryTime, username, password, defaultUseragent);
    }

    protected static GetResult createGetMethod(String strUrl, int timeout, int intRetryTime, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        return createGetMethod(strUrl, null, null, timeout, intRetryTime, username, password, strUseragent);
    }

    protected static GetResult createGetMethod(String strUrl, String strETag, String strLastModified, int timeout, int intRetryTime, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        GetResult getResult = new GetResult();
        URL url = new URL(strUrl);
        DefaultHttpClient client = getHttpClient();
        HttpGet method = new HttpGet(strUrl);
        if (username != null && password != null) {
            client.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(username, password));
        }
        method.getParams().setParameter("http.socket.timeout", Integer.valueOf(timeout));
        method.getParams().setParameter("http.connection.timeout", new Integer(timeout));
        if (strUseragent != null) {
            method.setHeader("User-Agent", strUseragent);
        } else if (defaultUseragent != null) {
            method.setHeader("User-Agent", defaultUseragent);
        }
        if (intRetryTime > 0) {
            client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(intRetryTime, false));
        }
        if (strETag != null) {
            method.addHeader("If-None-Match", strETag);
        }
        if (strLastModified != null) {
            method.addHeader("If-Modified-Since", strLastModified);
        }
        if (bnShowForwardedFor || bnShowYahooRemoteIP) {
            String strSourceIPAddress = getInternetAddress();
            if (bnShowForwardedFor && strSourceIPAddress != null) {
                method.addHeader("X-Forwarded-For", strSourceIPAddress);
            }
            if (bnShowYahooRemoteIP && strSourceIPAddress != null) {
                method.addHeader("YahooRemoteIP", strSourceIPAddress);
            }
        }
        try {
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && (statusCode != 304 || (strETag == null && strLastModified == null))) {
                throw new IllegalStateException("Method failed: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
            getResult.intStatus = statusCode;
            getResult.method = method;
            getResult.response = response;
            return getResult;
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static String getResult(String strUrl, int timeout) throws IllegalStateException, ClientProtocolException, IOException {
        return getResult(strUrl, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static String getResult(String strUrl, int timeout, String encoding) throws IllegalStateException, ClientProtocolException, IOException {
        return getResult(strUrl, timeout, null, null, encoding, defaultUseragent);
    }

    public static String getResult(String strUrl, int timeout, String username, String password, String encoding) throws IllegalStateException, ClientProtocolException, IOException {
        return getResult(strUrl, timeout, username, password, encoding, defaultUseragent);
    }

    public static String getResult(String strUrl, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        InputStream in;
        String result;
        try {
            GetResult getResult = createGetMethod(strUrl, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpGet method = getResult.method;
            HttpResponse response = getResult.response;
            int intStatusCode = getResult.intStatus;
            HttpEntity entity = response.getEntity();
            if (encoding != null) {
                in = entity.getContent();
                try {
                    result = inputStream2String(in, encoding);
                    return result;
                } finally {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }
            in = entity.getContent();
            try {
                result = entity.getContentEncoding() != null ? inputStream2String(in, entity.getContentEncoding().getValue()) : inputStream2String(in, defaultEncoding);
                return result;
            } finally {
                try {
                    in.close();
                } catch (Exception e2) {
                }
            }
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static InputStream getResultInputStream(String strUrl, int timeout, String username, String password) throws IllegalStateException, ClientProtocolException, IOException {
        return getResultInputStream(strUrl, timeout, username, password, defaultUseragent);
    }

    public static InputStream getResultInputStream(String strUrl, int timeout, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        try {
            GetResult getResult = createGetMethod(strUrl, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpGet method = getResult.method;
            HttpResponse response = getResult.response;
            int intStatusCode = getResult.intStatus;
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            return in;
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static String downloadFile(String strUrl, File targetFile) throws IllegalStateException, ClientProtocolException, IOException {
        return downloadFile(strUrl, targetFile, intDefaultTimeout, defaultUseragent);
    }

    public static String downloadFile(String strUrl, File targetFile, int timeout) throws IllegalStateException, ClientProtocolException, IOException {
        return downloadFile(strUrl, targetFile, timeout, defaultUseragent);
    }

    public static String downloadFile(String strUrl, File targetFile, int timeout, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        return downloadFile(strUrl, targetFile, timeout, null, null, strUseragent);
    }

    public static String downloadFile(String strUrl, File targetFile, int timeout, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        try {
            GetResult getResult = createGetMethod(strUrl, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpGet method = getResult.method;
            HttpResponse response = getResult.response;
            int intStatusCode = getResult.intStatus;
            HttpEntity entity = response.getEntity();
            FileOutputStream out = new FileOutputStream(targetFile);
            InputStream in = entity.getContent();
            copyStreams(in, out, 40960);
            out.close();
            in.close();
            if (entity.getContentType() != null) {
                return entity.getContentType().getValue();
            }
            return null;
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static StringHTTPResult getResultExtend(String strUrl, String strETag, String strLastModified, int timeout) throws IllegalStateException, HttpException, IOException {
        return getResultExtend(strUrl, strETag, strLastModified, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static StringHTTPResult getResultExtend(String strUrl, String strETag, String strLastModified, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, HttpException, IOException {
        InputStream in;
        String result;
        try {
            GetResult getResult = createGetMethod(strUrl, strETag, strLastModified, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpGet httpGet = getResult.method;
            HttpResponse response = getResult.response;
            int i = getResult.intStatus;
            HttpEntity entity = response.getEntity();
            long intResponseLength = entity != null ? entity.getContentLength() : -1L;
            try {
                if (getResult.intStatus == 304) {
                    StringHTTPResult returnData = new StringHTTPResult(true, strETag, strLastModified, null);
                    return returnData;
                }
                String strLastModified2 = retrieveHeader(response, "Modified-Since");
                String strETag2 = retrieveHeader(response, "ETag");
                if (encoding != null) {
                    in = entity.getContent();
                    try {
                        result = inputStream2String(in, encoding);
                        try {
                            in.close();
                        } catch (Exception e) {
                        }
                    } finally {
                    }
                } else {
                    in = entity.getContent();
                    try {
                        result = entity.getContentEncoding() != null ? inputStream2String(in, entity.getContentEncoding().getValue()) : inputStream2String(in, defaultEncoding);
                        try {
                            in.close();
                        } catch (Exception e2) {
                        }
                    } finally {
                    }
                }
                StringHTTPResult returnData2 = new StringHTTPResult(result, entity.getContentType(), intResponseLength, strETag2, strLastModified2, null);
                return returnData2;
            } catch (IOException ioe) {
                throw ioe;
            } catch (HttpException httpe) {
                throw httpe;
            }
        } catch (IOException ioe2) {
            throw ioe2;
        } catch (HttpException httpe2) {
            throw httpe2;
        }
    }

    public static InputStreamHTTPResult getResultInputStreamExtend(String strUrl, String strETag, String strLastModified, int timeout) throws IllegalStateException, HttpException, IOException {
        return getResultInputStreamExtend(strUrl, strETag, strLastModified, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static InputStreamHTTPResult getResultInputStreamExtend(String strUrl, String strETag, String strLastModified, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, HttpException, IOException {
        try {
            GetResult getResult = createGetMethod(strUrl, strETag, strLastModified, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpGet httpGet = getResult.method;
            HttpResponse response = getResult.response;
            int intStatusCode = getResult.intStatus;
            HttpEntity entity = response.getEntity();
            long intResponseLength = entity != null ? entity.getContentLength() : -1L;
            try {
                if (getResult.intStatus == 304) {
                    InputStreamHTTPResult returnData = new InputStreamHTTPResult(true, strETag, strLastModified, null);
                    return returnData;
                }
                InputStreamHTTPResult returnData2 = new InputStreamHTTPResult(entity.getContent(), entity.getContentType(), intResponseLength, retrieveHeader(response, "ETag"), retrieveHeader(response, "Modified-Since"), null);
                return returnData2;
            } catch (IOException ioe) {
                throw ioe;
            } catch (HttpException httpe) {
                throw httpe;
            }
        } catch (IOException ioe2) {
            throw ioe2;
        } catch (HttpException httpe2) {
            throw httpe2;
        }
    }

    protected static PostResult createPostMethod(String strUrl, Map parameter, int timeout, int intRetryTime, String username, String password) throws IllegalStateException, ClientProtocolException, IOException {
        return createFormPostMethod(strUrl, null, parameter, timeout, intRetryTime, username, password);
    }

    protected static PostResult createFormPostMethod(String strUrl, Map queryString, Map parameter, int timeout, int intRetryTime, String username, String password) throws IllegalStateException, ClientProtocolException, IOException {
        return createFormPostMethod(strUrl, queryString, parameter, timeout, intRetryTime, username, password, defaultUseragent);
    }

    protected static PostResult createFormPostMethod(String strUrl, Map queryString, Map parameter, int timeout, int intRetryTime, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        return createFormPostMethod(strUrl, queryString, parameter, null, null, timeout, intRetryTime, username, password, strUseragent);
    }

    protected static PostResult createFormPostMethod(String strUrl, Map queryString, Map parameter, String strETag, String strLastModified, int timeout, int intRetryTime, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        PostResult postResult = new PostResult();
        String strUrl2 = new StringBuilder(String.valueOf(strUrl)).toString();
        if (queryString != null && queryString.keySet().size() > 0) {
            int j = 0;
            for (String strKey : queryString.keySet()) {
                String strValue = (String) queryString.get(strKey);
                if (strUrl2.indexOf("?") >= 0) {
                    strUrl2 = String.valueOf(strUrl2) + "&" + strKey + "=" + URLEncoder.encode(strValue);
                } else {
                    strUrl2 = String.valueOf(strUrl2) + "?" + strKey + "=" + URLEncoder.encode(strValue);
                }
                j++;
            }
        }
        URL url = new URL(strUrl2);
        DefaultHttpClient client = getHttpClient();
        HttpPost method = new HttpPost(strUrl2);
        if (username != null && password != null) {
            client.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(username, password));
        }
        method.getParams().setParameter("http.socket.timeout", Integer.valueOf(timeout));
        method.getParams().setParameter("http.connection.timeout", new Integer(timeout));
        if (strUseragent != null) {
            method.setHeader("User-Agent", strUseragent);
        } else if (defaultUseragent != null) {
            method.setHeader("User-Agent", defaultUseragent);
        }
        if (intRetryTime > 0) {
            client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(intRetryTime, false));
        }
        if (strETag != null) {
            method.addHeader("If-None-Match", strETag);
        }
        if (strLastModified != null) {
            method.addHeader("If-Modified-Since", strLastModified);
        }
        if (parameter != null && parameter.keySet().size() > 0) {
            List<NameValuePair> nvps = new ArrayList<>();
            int j2 = 0;
            for (String strKey2 : parameter.keySet()) {
                nvps.add(new BasicNameValuePair(strKey2, (String) parameter.get(strKey2)));
                j2++;
            }
            method.setEntity(new UrlEncodedFormEntity(nvps, defaultPostEncoding != null ? defaultPostEncoding : "UTF-8"));
        }
        try {
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IllegalStateException("Method failed: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
            postResult.intStatus = statusCode;
            postResult.method = method;
            postResult.response = response;
            return postResult;
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    protected static PostResult createStringPostMethod(String strUrl, Map queryString, String strContentType, String strContent, String charset, int timeout, int intRetryTime, String username, String password) throws IllegalStateException, ClientProtocolException, IOException {
        return createStringPostMethod(strUrl, queryString, null, strContentType, strContent, charset, null, null, timeout, intRetryTime, username, password, defaultUseragent);
    }

    protected static PostResult createStringPostMethod(String strUrl, Map queryString, Map requestHeader, String strContentType, String strContent, String charset, int timeout, int intRetryTime, String username, String password) throws IllegalStateException, ClientProtocolException, IOException {
        return createStringPostMethod(strUrl, queryString, requestHeader, strContentType, strContent, charset, null, null, timeout, intRetryTime, username, password, defaultUseragent);
    }

    protected static PostResult createStringPostMethod(String strUrl, Map queryString, Map requestHeader, String strContentType, String strContent, String charset, String strETag, String strLastModified, int timeout, int intRetryTime, String username, String password, String strUseragent) throws IllegalStateException, ClientProtocolException, IOException {
        PostResult postResult = new PostResult();
        String strUrl2 = new StringBuilder(String.valueOf(strUrl)).toString();
        if (queryString != null && queryString.keySet().size() > 0) {
            int j = 0;
            for (String strKey : queryString.keySet()) {
                String strValue = (String) queryString.get(strKey);
                if (strUrl2.indexOf("?") >= 0) {
                    strUrl2 = String.valueOf(strUrl2) + "&" + strKey + "=" + URLEncoder.encode(strValue);
                } else {
                    strUrl2 = String.valueOf(strUrl2) + "?" + strKey + "=" + URLEncoder.encode(strValue);
                }
                j++;
            }
        }
        URL url = new URL(strUrl2);
        DefaultHttpClient httpClient = getHttpClient();
        HttpPost method = new HttpPost(strUrl2);
        if (username != null && password != null) {
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(username, password));
        }
        method.getParams().setParameter("http.socket.timeout", Integer.valueOf(timeout));
        method.getParams().setParameter("http.connection.timeout", new Integer(timeout));
        if (intRetryTime > 0) {
            httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(intRetryTime, false));
        }
        if (strETag != null) {
            method.addHeader("If-None-Match", strETag);
        }
        if (strLastModified != null) {
            method.addHeader("If-Modified-Since", strLastModified);
        }
        if (requestHeader != null && requestHeader.keySet().size() > 0) {
            int j2 = 0;
            for (String strKey2 : requestHeader.keySet()) {
                method.addHeader(strKey2, (String) requestHeader.get(strKey2));
                j2++;
            }
        }
        StringEntity re = new StringEntity(strContent, charset);
        re.setContentType(strContentType);
        method.setEntity(re);
        try {
            HttpResponse response = httpClient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IllegalStateException("Method failed: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            }
            postResult.intStatus = statusCode;
            postResult.method = method;
            postResult.response = response;
            return postResult;
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static String getPostResult(String strUrl, Map parameter, int timeout) throws IllegalStateException, HttpException, IOException {
        return getPostResult(strUrl, parameter, timeout, defaultEncoding);
    }

    public static String getPostResult(String strUrl, Map queryString, Map parameter, int timeout) throws IllegalStateException, HttpException, IOException {
        return getPostResult(strUrl, queryString, parameter, timeout, null, null, defaultEncoding);
    }

    public static String getPostResult(String strUrl, Map parameter, int timeout, String encoding) throws IllegalStateException, HttpException, IOException {
        return getPostResult(strUrl, parameter, timeout, null, null, encoding);
    }

    public static String getPostResult(String strUrl, Map parameter, int timeout, String username, String password, String encoding) throws IllegalStateException, HttpException, IOException {
        return getPostResult(strUrl, null, parameter, timeout, username, password, encoding);
    }

    public static String getPostResult(String strUrl, Map queryString, Map parameter, int timeout, String username, String password, String encoding) throws IllegalStateException, HttpException, IOException {
        InputStream in;
        String result;
        try {
            PostResult postResult = createFormPostMethod(strUrl, queryString, parameter, timeout, intAutoRetryTime, username, password);
            HttpPost method = postResult.method;
            HttpResponse response = postResult.response;
            int intStatusCode = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            if (encoding != null) {
                in = entity.getContent();
                try {
                    result = inputStream2String(in, encoding);
                    return result;
                } finally {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }
            in = entity.getContent();
            try {
                result = entity.getContentEncoding() != null ? inputStream2String(in, entity.getContentEncoding().getValue()) : inputStream2String(in, defaultEncoding);
                return result;
            } finally {
                try {
                    in.close();
                } catch (Exception e2) {
                }
            }
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static String getPostResult(String strUrl, Map queryString, String strContent, String strContentType, String strCharset, int timeout, String username, String password, String encoding) throws IllegalStateException, HttpException, IOException {
        InputStream in;
        String result;
        try {
            PostResult postResult = createStringPostMethod(strUrl, queryString, strContentType, strContent, strCharset, timeout, intAutoRetryTime, username, password);
            HttpPost method = postResult.method;
            HttpResponse response = postResult.response;
            int intStatusCode = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            if (encoding != null) {
                in = entity.getContent();
                try {
                    result = inputStream2String(in, encoding);
                    return result;
                } finally {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }
            in = entity.getContent();
            try {
                result = entity.getContentEncoding() != null ? inputStream2String(in, entity.getContentEncoding().getValue()) : inputStream2String(in, defaultEncoding);
                return result;
            } finally {
                try {
                    in.close();
                } catch (Exception e2) {
                }
            }
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static InputStream getPostResultInputStream(String strUrl, Map queryString, String strContent, String strContentType, String strCharset, int timeout, String username, String password, String encoding) throws IllegalStateException, HttpException, IOException {
        try {
            PostResult postResult = createStringPostMethod(strUrl, queryString, strContentType, strContent, strCharset, timeout, intAutoRetryTime, username, password);
            HttpPost method = postResult.method;
            HttpResponse response = postResult.response;
            int intStatusCode = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            return in;
        } catch (ClientProtocolException httpe) {
            throw httpe;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public static StringHTTPResult getPostResultExtend(String strUrl, Map queryString, Map parameter, String strETag, String strLastModified, int timeout) throws IllegalStateException, HttpException, IOException {
        return getPostResultExtend(strUrl, queryString, parameter, strETag, strLastModified, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static StringHTTPResult getPostResultExtend(String strUrl, Map queryString, Map parameter, String strETag, String strLastModified, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, HttpException, IOException {
        InputStream in;
        String result;
        try {
            PostResult postResult = createFormPostMethod(strUrl, queryString, parameter, strETag, strLastModified, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpPost httpPost = postResult.method;
            HttpResponse response = postResult.response;
            int i = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            long intResponseLength = entity != null ? entity.getContentLength() : -1L;
            try {
                if (postResult.intStatus == 304) {
                    StringHTTPResult returnData = new StringHTTPResult(true, strETag, strLastModified, null);
                    return returnData;
                }
                String strLastModified2 = retrieveHeader(response, "Modified-Since");
                String strETag2 = retrieveHeader(response, "ETag");
                if (encoding != null) {
                    in = entity.getContent();
                    try {
                        result = inputStream2String(in, encoding);
                        try {
                            in.close();
                        } catch (Exception e) {
                        }
                    } finally {
                    }
                } else {
                    in = entity.getContent();
                    try {
                        result = entity.getContentEncoding() != null ? inputStream2String(in, entity.getContentEncoding().getValue()) : inputStream2String(in, defaultEncoding);
                        try {
                            in.close();
                        } catch (Exception e2) {
                        }
                    } finally {
                    }
                }
                StringHTTPResult returnData2 = new StringHTTPResult(result, entity.getContentType(), intResponseLength, strETag2, strLastModified2, null);
                return returnData2;
            } catch (IOException ioe) {
                throw ioe;
            } catch (HttpException httpe) {
                throw httpe;
            }
        } catch (IOException ioe2) {
            throw ioe2;
        } catch (HttpException httpe2) {
            throw httpe2;
        }
    }

    public static InputStreamHTTPResult getPostResultInputStreamExtend(String strUrl, Map queryString, Map parameter, String strETag, String strLastModified, int timeout) throws IllegalStateException, HttpException, IOException {
        return getPostResultInputStreamExtend(strUrl, queryString, parameter, strETag, strLastModified, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static InputStreamHTTPResult getPostResultInputStreamExtend(String strUrl, Map queryString, Map parameter, String strETag, String strLastModified, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, HttpException, IOException {
        try {
            PostResult postResult = createFormPostMethod(strUrl, queryString, parameter, strETag, strLastModified, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpPost httpPost = postResult.method;
            HttpResponse response = postResult.response;
            int i = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            long intResponseLength = entity != null ? entity.getContentLength() : -1L;
            try {
                if (postResult.intStatus == 304) {
                    InputStreamHTTPResult returnData = new InputStreamHTTPResult(true, strETag, strLastModified, null);
                    return returnData;
                }
                InputStreamHTTPResult returnData2 = new InputStreamHTTPResult(entity.getContent(), entity.getContentType(), intResponseLength, retrieveHeader(response, "ETag"), retrieveHeader(response, "Modified-Since"), null);
                return returnData2;
            } catch (IOException ioe) {
                throw ioe;
            } catch (HttpException httpe) {
                throw httpe;
            }
        } catch (IOException ioe2) {
            throw ioe2;
        } catch (HttpException httpe2) {
            throw httpe2;
        }
    }

    public static StringHTTPResult getPostResultExtend(String strUrl, Map queryString, String strContent, String strContentType, String strCharset, String strETag, String strLastModified, int timeout) throws IllegalStateException, HttpException, IOException {
        return getPostResultExtend(strUrl, queryString, strContent, strContentType, strCharset, strETag, strLastModified, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static StringHTTPResult getPostResultExtend(String strUrl, Map queryString, String strContent, String strContentType, String strCharset, String strETag, String strLastModified, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, HttpException, IOException {
        InputStream in;
        String result;
        try {
            PostResult postResult = createStringPostMethod(strUrl, queryString, null, strContentType, strContent, strCharset, strETag, strLastModified, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpPost httpPost = postResult.method;
            HttpResponse response = postResult.response;
            int i = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            long intResponseLength = entity != null ? entity.getContentLength() : -1L;
            try {
                if (postResult.intStatus == 304) {
                    StringHTTPResult returnData = new StringHTTPResult(true, strETag, strLastModified, null);
                    return returnData;
                }
                String strLastModified2 = retrieveHeader(response, "Modified-Since");
                String strETag2 = retrieveHeader(response, "ETag");
                if (encoding != null) {
                    in = entity.getContent();
                    try {
                        result = inputStream2String(in, encoding);
                        try {
                            in.close();
                        } catch (Exception e) {
                        }
                    } finally {
                    }
                } else {
                    in = entity.getContent();
                    try {
                        result = entity.getContentEncoding() != null ? inputStream2String(in, entity.getContentEncoding().getValue()) : inputStream2String(in, defaultEncoding);
                        try {
                            in.close();
                        } catch (Exception e2) {
                        }
                    } finally {
                    }
                }
                StringHTTPResult returnData2 = new StringHTTPResult(result, entity.getContentType(), intResponseLength, strETag2, strLastModified2, null);
                return returnData2;
            } catch (IOException ioe) {
                throw ioe;
            } catch (HttpException httpe) {
                throw httpe;
            }
        } catch (IOException ioe2) {
            throw ioe2;
        } catch (HttpException httpe2) {
            throw httpe2;
        }
    }

    public static InputStreamHTTPResult getPostResultInputStreamExtend(String strUrl, Map queryString, String strContent, String strContentType, String strCharset, String strETag, String strLastModified, int timeout) throws IllegalStateException, HttpException, IOException {
        return getPostResultInputStreamExtend(strUrl, queryString, strContent, strContentType, strCharset, strETag, strLastModified, timeout, null, null, defaultEncoding, defaultUseragent);
    }

    public static InputStreamHTTPResult getPostResultInputStreamExtend(String strUrl, Map queryString, String strContent, String strContentType, String strCharset, String strETag, String strLastModified, int timeout, String username, String password, String encoding, String strUseragent) throws IllegalStateException, HttpException, IOException {
        try {
            PostResult postResult = createStringPostMethod(strUrl, queryString, null, strContentType, strContent, strCharset, strETag, strLastModified, timeout, intAutoRetryTime, username, password, strUseragent);
            HttpPost httpPost = postResult.method;
            HttpResponse response = postResult.response;
            int i = postResult.intStatus;
            HttpEntity entity = response.getEntity();
            long intResponseLength = entity != null ? entity.getContentLength() : -1L;
            try {
                if (postResult.intStatus == 304) {
                    InputStreamHTTPResult returnData = new InputStreamHTTPResult(true, strETag, strLastModified, null);
                    return returnData;
                }
                InputStreamHTTPResult returnData2 = new InputStreamHTTPResult(entity.getContent(), entity.getContentType(), intResponseLength, retrieveHeader(response, "ETag"), retrieveHeader(response, "Modified-Since"), null);
                return returnData2;
            } catch (IOException ioe) {
                throw ioe;
            } catch (HttpException httpe) {
                throw httpe;
            }
        } catch (IOException ioe2) {
            throw ioe2;
        } catch (HttpException httpe2) {
            throw httpe2;
        }
    }

    public static boolean exists(String URLName) {
        return exists(URLName, true);
    }

    public static boolean exists(String URLName, boolean allowFollow) {
        try {
            if (!allowFollow) {
                HttpURLConnection.setFollowRedirects(false);
            } else {
                HttpURLConnection.setFollowRedirects(true);
            }
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            if (!allowFollow) {
                con.setInstanceFollowRedirects(false);
            }
            con.setRequestMethod("HEAD");
            return con.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int size(String URLName) {
        return size(URLName, true);
    }

    public static int size(String URLName, boolean allowFollow) {
        try {
            if (!allowFollow) {
                HttpURLConnection.setFollowRedirects(false);
            } else {
                HttpURLConnection.setFollowRedirects(true);
            }
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            if (!allowFollow) {
                con.setInstanceFollowRedirects(false);
            }
            con.setRequestMethod("HEAD");
            return con.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long lastModified(String URLName) {
        return lastModified(URLName, true);
    }

    public static long lastModified(String URLName, boolean allowFollow) {
        try {
            if (!allowFollow) {
                HttpURLConnection.setFollowRedirects(false);
            } else {
                HttpURLConnection.setFollowRedirects(true);
            }
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            if (!allowFollow) {
                con.setInstanceFollowRedirects(false);
            }
            con.setRequestMethod("HEAD");
            return con.getLastModified();
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public static String inputStream2String(InputStream in, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        InputStreamReader inr = new InputStreamReader(in, encoding);
        copyStreams(inr, sw);
        return sw.toString();
    }

    public static void copyStreams(InputStream in, OutputStream out) throws IOException {
        copyStreams(in, out, 4096);
    }

    public static void copyStreams(InputStream in, OutputStream out, int buffersize) throws IOException {
        byte[] bytes = new byte[buffersize];
        while (true) {
            int bytesRead = in.read(bytes);
            if (bytesRead > -1) {
                out.write(bytes, 0, bytesRead);
            } else {
                return;
            }
        }
    }

    public static void copyStreams(Reader in, Writer out) throws IOException {
        copyStreams(in, out, 4096);
    }

    public static void copyStreams(Reader in, Writer out, int buffersize) throws IOException {
        char[] buffer = new char[buffersize];
        int count = 0;
        while (true) {
            int n = in.read(buffer);
            if (-1 != n) {
                out.write(buffer, 0, n);
                count += n;
            } else {
                return;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class StringHTTPResult {
        boolean bnIsNotChanged;
        long intContentLength;
        String strContentType;
        String strETag;
        String strLastModified;
        String strResultData;

        private StringHTTPResult(String strResultData, Header hdrContentType, long intContentLength, String strETag, String strLastModified) {
            this.strContentType = null;
            this.strLastModified = null;
            this.strETag = null;
            this.strResultData = null;
            this.intContentLength = -1L;
            this.bnIsNotChanged = false;
            this.strResultData = strResultData;
            this.strContentType = hdrContentType != null ? hdrContentType.getValue() : null;
            this.intContentLength = intContentLength;
            this.strETag = strETag;
            this.strLastModified = strLastModified;
        }

        /* synthetic */ StringHTTPResult(String str, Header header, long j, String str2, String str3, StringHTTPResult stringHTTPResult) {
            this(str, header, j, str2, str3);
        }

        private StringHTTPResult(boolean bnIsNotChanged, String strETag, String strLastModified) {
            this.strContentType = null;
            this.strLastModified = null;
            this.strETag = null;
            this.strResultData = null;
            this.intContentLength = -1L;
            this.bnIsNotChanged = false;
            if (!bnIsNotChanged) {
                throw new IllegalStateException("Cannot invoke this constructor while HTTP response is not 304");
            }
            this.bnIsNotChanged = bnIsNotChanged;
            this.strETag = strETag;
            this.strLastModified = strLastModified;
        }

        /* synthetic */ StringHTTPResult(boolean z, String str, String str2, StringHTTPResult stringHTTPResult) {
            this(z, str, str2);
        }

        public boolean isNotChanged() {
            return this.bnIsNotChanged;
        }

        public String getData() throws IOException {
            if (this.bnIsNotChanged) {
                throw new IOException("Cannot invoke get data while HTTP response is 304");
            }
            return this.strResultData;
        }

        public String getContentType() {
            return this.strContentType;
        }

        public String getETag() {
            return this.strETag;
        }

        public String getLastModified() {
            return this.strLastModified;
        }

        public long getDataLength() {
            return this.intContentLength;
        }
    }

    /* loaded from: classes.dex */
    public static class InputStreamHTTPResult {
        boolean bnIsNotChanged;
        long intContentLength;
        InputStream isResultData;
        String strContentType;
        String strETag;
        String strLastModified;

        private InputStreamHTTPResult(InputStream isResultData, Header hdrContentType, long intContentLength, String strETag, String strLastModified) {
            this.strContentType = null;
            this.strLastModified = null;
            this.strETag = null;
            this.isResultData = null;
            this.intContentLength = -1L;
            this.bnIsNotChanged = false;
            this.isResultData = isResultData;
            this.strContentType = hdrContentType != null ? hdrContentType.getValue() : null;
            this.intContentLength = intContentLength;
            this.strETag = strETag;
            this.strLastModified = strLastModified;
        }

        /* synthetic */ InputStreamHTTPResult(InputStream inputStream, Header header, long j, String str, String str2, InputStreamHTTPResult inputStreamHTTPResult) {
            this(inputStream, header, j, str, str2);
        }

        private InputStreamHTTPResult(boolean bnIsNotChanged, String strETag, String strLastModified) {
            this.strContentType = null;
            this.strLastModified = null;
            this.strETag = null;
            this.isResultData = null;
            this.intContentLength = -1L;
            this.bnIsNotChanged = false;
            if (!bnIsNotChanged) {
                throw new IllegalStateException("Cannot invoke this constructor while HTTP response is not 304");
            }
            this.bnIsNotChanged = bnIsNotChanged;
            this.strETag = strETag;
            this.strLastModified = strLastModified;
        }

        /* synthetic */ InputStreamHTTPResult(boolean z, String str, String str2, InputStreamHTTPResult inputStreamHTTPResult) {
            this(z, str, str2);
        }

        public boolean isNotChanged() {
            return this.bnIsNotChanged;
        }

        public InputStream getData() throws IOException {
            if (this.bnIsNotChanged) {
                throw new IOException("Cannot invoke get data while HTTP response is 304");
            }
            return this.isResultData;
        }

        public String getContentType() {
            return this.strContentType;
        }

        public String getETag() {
            return this.strETag;
        }

        public String getLastModified() {
            return this.strLastModified;
        }

        public long getDataLength() {
            return this.intContentLength;
        }
    }

    private static String retrieveHeader(HttpResponse method, String name) throws HttpException {
        Header[] headers = method.getHeaders(name);
        if (headers == null || headers.length <= 0) {
            return null;
        }
        return headers[0].getValue();
    }

    /* loaded from: classes.dex */
    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext;

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            this.sslContext = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() { // from class: com.mtel.Tools.Net.NetUtil.MySSLSocketFactory.1
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

    public static void main(String[] arg) throws IOException {
        try {
            System.out.print(getResult("http://game1.ctm.mtel.ws/java/CTMJavaGame/javagame/CTL_DAFDC6FE9E634E8399FB78E24EE3590D/thq_f_Motorola_RAZRMaxx_efigs_ChopSushi_v1_0_46.jar", "UTF-8").length());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
