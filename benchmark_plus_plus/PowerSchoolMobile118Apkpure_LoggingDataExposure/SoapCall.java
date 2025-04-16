package com.pearson.powerschool.android.webserviceclient.service.network;

import android.os.Build;
import android.util.Base64;
import android.util.Log;
import com.pearson.powerschool.android.data.mo.NotificationSettings;
import com.pearson.powerschool.android.data.mo.UserSession;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntityHC4;
import org.apache.http.impl.client.BasicCredentialsProviderHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/* loaded from: classes.dex */
public class SoapCall {
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS = 180000;
    private static final String JSON_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String LOGINSERVICE_NAMESPACE_ATTRIBUTE = "\"http://login.rest.powerschool.pearson.com/xsd\"";
    private static final String LOGIN_SERVICE_XML = "LoginService";
    public static final int ONBOARDING_CONNECTION_TIMEOUT_MILLISECONDS = 60000;
    private static final String PEARSON_REST_SERVICE_PATH = "/pearson-rest/services/";
    private static final String PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE = "\"http://publicportal.rest.powerschool.pearson.com/xsd\"";
    private static final String PUBLIC_PORTAL_SERVICE_JSON = "PublicPortalServiceJSON";
    private static final String SOAP_BODY_END_TAG = "</soap:Body>";
    private static final String SOAP_BODY_START_TAG = "<soap:Body>";
    private static final String SOAP_ENVELOPE_END_TAG = "</soap:Envelope>";
    private static final String SOAP_ENVELOPE_START_TAG_FRAGMENT = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"  xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"";
    private static final String SOAP_HEADER_END_TAG = "</soap:Header>";
    private static final String SOAP_HEADER_START_TAG = "<soap:Header>";
    private static final String SOAP_SECURITY_HEADER_CREATED_END_TAG = "</wsu:Created>";
    private static final String SOAP_SECURITY_HEADER_CREATED_START_TAG = "<wsu:Created>";
    private static final String SOAP_SECURITY_HEADER_END_TAG = "</wsse:Security>";
    private static final String SOAP_SECURITY_HEADER_NONCE_END_TAG = "</wsse:Nonce>";
    private static final String SOAP_SECURITY_HEADER_NONCE_START_TAG = "<wsse:Nonce>";
    private static final String SOAP_SECURITY_HEADER_PASSWORD_END_TAG = "</wsse:Password>";
    private static final String SOAP_SECURITY_HEADER_PASSWORD_START_TAG = "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">";
    private static final String SOAP_SECURITY_HEADER_START_TAG = "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" soap:mustUnderstand=\"1\">";
    private static final String SOAP_SECURITY_HEADER_USERNAME_END_TAG = "</wsse:Username>";
    private static final String SOAP_SECURITY_HEADER_USERNAME_START_TAG = "<wsse:Username>";
    private static final String SOAP_SECURITY_HEADER_USERNAME_TOKEN_END_TAG = "</wsse:UsernameToken>";
    private static final String SOAP_SECURITY_HEADER_USERNAME_TOKEN_START_TAG = "<wsse:UsernameToken>";
    private static final String TAG = "SoapCall";
    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    private static CloseableHttpClient getHttpClient(boolean z, int i) {
        HttpClientBuilder create = HttpClientBuilder.create();
        create.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(i).build());
        create.setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).build());
        if (z) {
            BasicCredentialsProviderHC4 basicCredentialsProviderHC4 = new BasicCredentialsProviderHC4();
            basicCredentialsProviderHC4.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("pearson", "m0bApP5"));
            create.setDefaultCredentialsProvider(basicCredentialsProviderHC4);
        }
        if (Build.VERSION.SDK_INT < 21) {
            try {
                create.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom().useTLS().build(), new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, (String[]) null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER));
            } catch (Exception e) {
                Log.e(TAG, "Error enabling TLS protocol for pre-Android API 21.", e);
            }
        }
        return create.build();
    }

    private static CloseableHttpClient getHttpClient() {
        return getHttpClient(true, DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS);
    }

    private static HttpPostHC4 getHttpPost(String str, String str2, String str3, boolean z) {
        String str4 = str + PEARSON_REST_SERVICE_PATH;
        Log.d(TAG, "Service path: " + str4);
        StringBuilder sb = new StringBuilder();
        sb.append(str4);
        sb.append(str2);
        if (z) {
            sb.append("?response=");
            sb.append("application/json");
        }
        HttpPostHC4 httpPostHC4 = new HttpPostHC4(sb.toString());
        httpPostHC4.addHeader("Content-type", CONTENT_TYPE_XML);
        httpPostHC4.addHeader("SOAPAction", str4 + str2 + "#" + str3);
        httpPostHC4.setProtocolVersion(HttpVersion.HTTP_1_1);
        return httpPostHC4;
    }

    private static HttpPostHC4 getHttpPost(String str, String str2, String str3) {
        return getHttpPost(str, str2, str3, true);
    }

    private static StringBuilder getSoapEnvelopeEndFragment(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("</");
        sb.append(str);
        sb.append(">");
        sb.append(SOAP_BODY_END_TAG);
        sb.append(SOAP_ENVELOPE_END_TAG);
        return sb;
    }

    private static StringBuilder getSoapEnvelopeStartFragment(String str, String str2, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(XML_DECLARATION);
        sb.append(SOAP_ENVELOPE_START_TAG_FRAGMENT);
        sb.append(" xmlns=");
        sb.append(str2);
        sb.append(">");
        if (z) {
            sb.append(SOAP_HEADER_START_TAG);
            serializeSOAPSecurityHeader("pearson", "m0bApP5", sb);
            sb.append(SOAP_HEADER_END_TAG);
        }
        sb.append(SOAP_BODY_START_TAG);
        sb.append("<");
        sb.append(str);
        sb.append(" xmlns=");
        sb.append(str2);
        sb.append(">");
        return sb;
    }

    private static StringBuilder getSoapEnvelopeStartFragment(String str, String str2) {
        return getSoapEnvelopeStartFragment(str, str2, false);
    }

    public static String getStudentData(String str, UserSession userSession, List<Long> list, List<Integer> list2) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "getStudentData");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("getStudentData", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            serializeStudentIDs(list, soapEnvelopeStartFragment);
            serializeQueryIncludeList(list2, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("getStudentData"));
            Log.d(TAG, "getStudentData request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("getStudentData", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    private static void closeHttpClient(CloseableHttpClient closeableHttpClient) {
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close httpClient", e);
        }
    }

    public static String getStudentPhoto(String str, UserSession userSession, long j) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "getStudentPhoto");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("getStudentPhoto", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            serializeStudentID(j, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("getStudentPhoto"));
            Log.d(TAG, "getStudentPhoto request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("getStudentPhoto", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String getSchoolMapBySchoolNumber(String str, UserSession userSession, long j) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "getSchoolMapBySchoolNumber");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("getSchoolMapBySchoolNumber", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            serializeSchoolNumber(j, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("getSchoolMapBySchoolNumber"));
            Log.d(TAG, "getSchoolMapBySchoolNumber request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("getSchoolMapBySchoolNumber", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String linkDeviceTokenToUser(String str, UserSession userSession, String str2) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "linkDeviceTokenToUser");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("linkDeviceTokenToUser", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append("<deviceToken>");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("</deviceToken>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("linkDeviceTokenToUser"));
            Log.d(TAG, "linkDeviceTokenToUser request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("linkDeviceTokenToUser", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String storeNotificationSettings(String str, UserSession userSession, NotificationSettings notificationSettings) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "storeNotificationSettings");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("storeNotificationSettings", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            serializeNotificationSettings(notificationSettings, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("storeNotificationSettings"));
            Log.d(TAG, "storeNotificationSettings request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("storeNotificationSettings", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String login(String str, String str2, String str3, int i) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "login");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("login", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            soapEnvelopeStartFragment.append("<username><![CDATA[");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("]]></username>");
            soapEnvelopeStartFragment.append("<password><![CDATA[");
            soapEnvelopeStartFragment.append(str3);
            soapEnvelopeStartFragment.append("]]></password>");
            soapEnvelopeStartFragment.append("<userType>");
            soapEnvelopeStartFragment.append(i);
            soapEnvelopeStartFragment.append("</userType>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("login"));
            Log.d(TAG, "login request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("login", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String loginToPublicPortal(String str, String str2, String str3) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "loginToPublicPortal");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("loginToPublicPortal", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            soapEnvelopeStartFragment.append("<username><![CDATA[");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("]]></username>");
            soapEnvelopeStartFragment.append("<password><![CDATA[");
            soapEnvelopeStartFragment.append(str3);
            soapEnvelopeStartFragment.append("]]></password>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("loginToPublicPortal"));
            Log.d(TAG, "loginToPublicPortal request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("loginToPublicPortal", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String recoverUserName(String str, String str2, int i) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "recoverUsername");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("recoverUsername", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            soapEnvelopeStartFragment.append("<emailAddress><![CDATA[");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("]]></emailAddress>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("recoverUsername"));
            Log.d(TAG, "recoverUsername request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("recoverUsername", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String sendPasswordRecoveryEmail(String str, String str2, String str3, int i) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "sendPasswordRecoveryEmail");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("sendPasswordRecoveryEmail", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            soapEnvelopeStartFragment.append("<userType>");
            soapEnvelopeStartFragment.append(i);
            soapEnvelopeStartFragment.append("</userType>");
            soapEnvelopeStartFragment.append("<userName><![CDATA[");
            soapEnvelopeStartFragment.append(str3);
            soapEnvelopeStartFragment.append("]]></userName>");
            soapEnvelopeStartFragment.append("<emailAddress><![CDATA[");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("]]></emailAddress>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("sendPasswordRecoveryEmail"));
            Log.d(TAG, "sendPasswordRecoveryEmail request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("sendPasswordRecoveryEmail", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String recoverPassword(String str, int i, String str2, String str3, String str4) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "recoverPassword");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("recoverPassword", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            soapEnvelopeStartFragment.append("<userType>");
            soapEnvelopeStartFragment.append(i);
            soapEnvelopeStartFragment.append("</userType>");
            soapEnvelopeStartFragment.append("<userName><![CDATA[");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("]]></userName>");
            soapEnvelopeStartFragment.append("<recoveryToken><![CDATA[");
            soapEnvelopeStartFragment.append(str3);
            soapEnvelopeStartFragment.append("]]></recoveryToken>");
            soapEnvelopeStartFragment.append("<newPassword><![CDATA[");
            soapEnvelopeStartFragment.append(str4);
            soapEnvelopeStartFragment.append("]]></newPassword>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("recoverPassword"));
            Log.d(TAG, "recoverPassword request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("recoverPassword", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String getCredentialComplexityRules(String str, int i) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "getCredentialComplexityRules");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("getCredentialComplexityRules", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            soapEnvelopeStartFragment.append("<userType>");
            soapEnvelopeStartFragment.append(i);
            soapEnvelopeStartFragment.append("</userType>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("getCredentialComplexityRules"));
            Log.d(TAG, "getCredentialComplexityRules request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("getCredentialComplexityRules", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String logout(String str, UserSession userSession) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "logout");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("logout", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("logout"));
            Log.d(TAG, "logout request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("logout", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String logoutAndDelinkDeviceToken(String str, UserSession userSession, String str2) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPostHC4 httpPost = getHttpPost(str, PUBLIC_PORTAL_SERVICE_JSON, "logoutAndDelinkDeviceToken");
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("logoutAndDelinkDeviceToken", PUBLICPORTALSERVICE_NAMESPACE_ATTRIBUTE);
            serializeUserSession(userSession, soapEnvelopeStartFragment);
            soapEnvelopeStartFragment.append("<deviceToken>");
            soapEnvelopeStartFragment.append(str2);
            soapEnvelopeStartFragment.append("</deviceToken>");
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("logoutAndDelinkDeviceToken"));
            Log.d(TAG, "logoutAndDelinkDeviceToken request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("logoutAndDelinkDeviceToken", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String getServerInfo(String str) throws ServerException {
        CloseableHttpClient httpClient = getHttpClient(false, ONBOARDING_CONNECTION_TIMEOUT_MILLISECONDS);
        try {
            HttpPostHC4 httpPost = getHttpPost(str, LOGIN_SERVICE_XML, "getServerInfo", false);
            StringBuilder soapEnvelopeStartFragment = getSoapEnvelopeStartFragment("getServerInfo", LOGINSERVICE_NAMESPACE_ATTRIBUTE, true);
            soapEnvelopeStartFragment.append((CharSequence) getSoapEnvelopeEndFragment("getServerInfo"));
            Log.d(TAG, "getServerInfo request xml " + ((Object) soapEnvelopeStartFragment));
            return executeSoapRequest("getServerInfo", httpClient, httpPost, soapEnvelopeStartFragment);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    public static String executeGetRequest(String str, int i, String str2) throws ServerException {
        CloseableHttpResponse execute;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            try {
                CloseableHttpClient httpClient = getHttpClient(false, i);
                HttpGetHC4 httpGetHC4 = new HttpGetHC4(str2);
                httpGetHC4.setProtocolVersion(HttpVersion.HTTP_1_1);
                execute = httpClient.execute((HttpUriRequest) httpGetHC4);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        } catch (Exception e2) {
            e = e2;
        }
        try {
            int statusCode = execute.getStatusLine().getStatusCode();
            String entityUtils = EntityUtils.toString(execute.getEntity());
            if (statusCode >= 300) {
                Log.e(TAG, "Non 2xx response code for Get request: " + str + ": " + statusCode);
                Log.e(TAG, "Response with non 2xx response code for Get request " + str + ": " + entityUtils);
                throw new ServerException(statusCode);
            }
            closeHttpResponse(execute);
            return entityUtils;
        } catch (IOException e3) {
            e = e3;
            Log.e(TAG, "Network call to " + str + " failed", e);
            throw new ServerException(102, e);
        } catch (Exception e4) {
            e = e4;
            Log.e(TAG, "Network call to " + str + " failed", e);
            if (e instanceof ServerException) {
                throw ((ServerException) e);
            }
            throw new ServerException(0, e);
        } catch (Throwable th2) {
            th = th2;
            closeableHttpResponse = execute;
            closeHttpResponse(closeableHttpResponse);
            throw th;
        }
    }

    private static void closeHttpResponse(CloseableHttpResponse closeableHttpResponse) {
        if (closeableHttpResponse != null) {
            try {
                closeableHttpResponse.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close http response", e);
            }
        }
    }

    private static String executeSoapRequest(String str, CloseableHttpClient closeableHttpClient, HttpPostHC4 httpPostHC4, StringBuilder sb) throws ServerException {
        CloseableHttpResponse closeableHttpResponse;
        long currentTimeMillis;
        try {
            try {
                currentTimeMillis = System.currentTimeMillis();
                StringEntityHC4 stringEntityHC4 = new StringEntityHC4(sb.toString(), HttpRequest.CHARSET_UTF8);
                stringEntityHC4.setContentType(CONTENT_TYPE_XML);
                httpPostHC4.setEntity(stringEntityHC4);
                closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest) httpPostHC4);
            } catch (Throwable th) {
                th = th;
                closeableHttpResponse = null;
            }
        } catch (IOException e) {
            e = e;
        } catch (Exception e2) {
            e = e2;
        }
        try {
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            long currentTimeMillis3 = System.currentTimeMillis();
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            String entityUtils = EntityUtils.toString(closeableHttpResponse.getEntity());
            long currentTimeMillis4 = System.currentTimeMillis() - currentTimeMillis3;
            System.currentTimeMillis();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Network Performance stats for " + str + ":\n");
            sb2.append("Network call duration: ");
            sb2.append(currentTimeMillis2);
            sb2.append("\n");
            sb2.append("Response i/o duration: ");
            sb2.append(currentTimeMillis4);
            sb2.append("\n");
            sb2.append("Total Service Call duration: ");
            sb2.append(currentTimeMillis2 + currentTimeMillis4);
            Log.d(TAG, sb2.toString());
            if (statusCode >= 300) {
                Log.e(TAG, "Non 2xx response code for " + str + ": " + statusCode);
                Log.e(TAG, "SOAP response with non 2xx response code for  " + str + ": " + entityUtils);
                throw new ServerException(statusCode, entityUtils);
            }
            closeHttpResponse(closeableHttpResponse);
            return entityUtils;
        } catch (IOException e3) {
            e = e3;
            Log.e(TAG, "Network call to " + str + " failed", e);
            throw new ServerException(102, e);
        } catch (Exception e4) {
            e = e4;
            Log.e(TAG, "Network call to " + str + " failed", e);
            if (e instanceof ServerException) {
                throw ((ServerException) e);
            }
            throw new ServerException(0, e);
        } catch (Throwable th2) {
            th = th2;
            closeHttpResponse(closeableHttpResponse);
            throw th;
        }
    }

    private static void serializeQueryIncludeList(List<Integer> list, StringBuilder sb) {
        sb.append("<qil>");
        for (Integer num : list) {
            sb.append("<includes>");
            sb.append(num);
            sb.append("</includes>");
        }
        sb.append("</qil>");
    }

    private static void serializeStudentIDs(List<Long> list, StringBuilder sb) {
        if (list == null || list.isEmpty()) {
            sb.append("<studentIDs/>");
            return;
        }
        for (Long l : list) {
            sb.append("<studentIDs>");
            sb.append(l);
            sb.append("</studentIDs>");
        }
    }

    private static void serializeStudentID(long j, StringBuilder sb) {
        sb.append("<studentID>");
        sb.append(j);
        sb.append("</studentID>");
    }

    private static void serializeSchoolNumber(long j, StringBuilder sb) {
        sb.append("<schoolNumber>");
        sb.append(j);
        sb.append("</schoolNumber>");
    }

    private static void serializeUserSession(UserSession userSession, StringBuilder sb) {
        sb.append("<userSessionVO><userId>");
        sb.append(userSession.getUserId());
        sb.append("</userId><serviceTicket>");
        sb.append(userSession.getServiceTicket());
        sb.append("</serviceTicket>");
        if (userSession.getStudentIDs() != null) {
            serializeStudentIDs(userSession.getStudentIDs(), sb);
        }
        if (userSession.getServerInfo() != null) {
            sb.append("<serverInfo><apiVersion><![CDATA[");
            sb.append(userSession.getServerInfo().getApiVersion());
            sb.append("]]></apiVersion></serverInfo>");
        }
        if (userSession.getServerCurrentTime() != null) {
            String format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userSession.getServerCurrentTime());
            sb.append("<serverCurrentTime>");
            sb.append(format);
            sb.append("</serverCurrentTime>");
        }
        sb.append("<userType>");
        sb.append(userSession.getUserType());
        sb.append("</userType>");
        sb.append("</userSessionVO>");
    }

    private static void serializeNotificationSettings(NotificationSettings notificationSettings, StringBuilder sb) {
        sb.append("<ns>");
        List<String> emailAddresses = notificationSettings.getEmailAddresses();
        if (emailAddresses != null) {
            for (String str : emailAddresses) {
                sb.append("<emailAddresses><![CDATA[");
                sb.append(str);
                sb.append("]]></emailAddresses>");
            }
        }
        sb.append("<frequency>");
        sb.append(notificationSettings.getFrequency());
        sb.append("</frequency>");
        sb.append("<mainEmail><![CDATA[");
        sb.append(notificationSettings.getMainEmail());
        sb.append("]]></mainEmail>");
        sb.append("<schoolAnnouncements>");
        sb.append(notificationSettings.isSchoolAnnouncements() ? 1 : 0);
        sb.append("</schoolAnnouncements>");
        sb.append("<guardianStudentId>");
        sb.append(notificationSettings.getGuardianStudentId());
        sb.append("</guardianStudentId>");
        sb.append("<applyToAllStudents>");
        sb.append(notificationSettings.isApplyToAllStudents() ? 1 : 0);
        sb.append("</applyToAllStudents>");
        sb.append("<balanceAlerts>");
        sb.append(notificationSettings.isBalanceAlerts() ? 1 : 0);
        sb.append("</balanceAlerts>");
        sb.append("<detailedAssignments>");
        sb.append(notificationSettings.isDetailedAssignments() ? 1 : 0);
        sb.append("</detailedAssignments>");
        sb.append("<detailedAttendance>");
        sb.append(notificationSettings.isDetailedAttendance() ? 1 : 0);
        sb.append("</detailedAttendance>");
        sb.append("<gradeAndAttSummary>");
        sb.append(notificationSettings.isGradeAndAttSummary() ? 1 : 0);
        sb.append("</gradeAndAttSummary>");
        sb.append("</ns>");
    }

    private static void serializeSOAPSecurityHeader(String str, String str2, StringBuilder sb) {
        try {
            String nonce = getNonce();
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
            String format = simpleDateFormat.format(date);
            String passwordDigestForSoapSecurityHeader = getPasswordDigestForSoapSecurityHeader(nonce, format, str2);
            sb.append(SOAP_SECURITY_HEADER_START_TAG);
            sb.append(SOAP_SECURITY_HEADER_USERNAME_TOKEN_START_TAG);
            sb.append(SOAP_SECURITY_HEADER_USERNAME_START_TAG);
            sb.append(str);
            sb.append(SOAP_SECURITY_HEADER_USERNAME_END_TAG);
            sb.append(SOAP_SECURITY_HEADER_PASSWORD_START_TAG);
            sb.append(passwordDigestForSoapSecurityHeader);
            sb.append(SOAP_SECURITY_HEADER_PASSWORD_END_TAG);
            sb.append(SOAP_SECURITY_HEADER_NONCE_START_TAG);
            sb.append(Base64.encodeToString(getUTF8EncodedBytes(hexEncode(nonce)), 2));
            sb.append(SOAP_SECURITY_HEADER_NONCE_END_TAG);
            sb.append(SOAP_SECURITY_HEADER_CREATED_START_TAG);
            sb.append(format);
            sb.append(SOAP_SECURITY_HEADER_CREATED_END_TAG);
            sb.append(SOAP_SECURITY_HEADER_USERNAME_TOKEN_END_TAG);
            sb.append(SOAP_SECURITY_HEADER_END_TAG);
        } catch (Exception e) {
            Log.e(TAG, "Could not add security header to the soap request. This will lead to soap request failiure", e);
        }
    }

    private static String getPasswordDigestForSoapSecurityHeader(String str, String str2, String str3) {
        try {
            return Base64.encodeToString(getMessageDigest(getUTF8EncodedBytes(hexEncode(str) + str2 + str3), "SHA1"), 2);
        } catch (Exception e) {
            Log.e(TAG, "Error computing password digest for Soap Security Header. Will be returning null", e);
            return null;
        }
    }

    private static String hexEncode(String str) {
        StringBuilder sb = new StringBuilder("");
        int i = 0;
        while (i < (str.length() - 2) + 1) {
            int i2 = i + 2;
            sb.append((char) Integer.parseInt(str.substring(i, i2), 16));
            i = i2;
        }
        return sb.toString();
    }

    private static byte[] getUTF8EncodedBytes(String str) throws UnsupportedEncodingException {
        return str.getBytes(HttpRequest.CHARSET_UTF8);
    }

    private static byte[] getMessageDigest(byte[] bArr, String str) throws Exception {
        return MessageDigest.getInstance(str).digest(bArr);
    }

    public static String getNonce() {
        return String.valueOf(new Random().nextInt(999999999));
    }
}
