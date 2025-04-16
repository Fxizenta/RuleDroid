package com.yum.pizzahut.quickorder;

import android.util.Log;
import com.yum.pizzahut.PizzaHutApp;
import com.yum.pizzahut.messages.Messages;
import com.yum.pizzahut.social.TwitterManager;
import com.yum.pizzahut.user.Address;
import com.yum.pizzahut.user.Card;
import com.yum.pizzahut.user.PizzaHutUser;
import com.yum.pizzahut.user.StoreInfo;
import com.yum.pizzahut.user.Token;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import oauth.signpost.OAuth;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class QuickOrderAPI {
    public static final String ACCOUNTID = "phimc2api";
    public static final String ACCOUNTPW = "fs112358";
    public static final String API_CREATECUSTOMER = "CreateCustomer";
    public static final String API_EMAILPASSWORD = "EmailPassword";
    public static final String API_FINDELIVERYSTORE = "FindDeliveryStore";
    public static final String API_FINDNEARBYADDRESS = "FindNearByAddress";
    public static final String API_FINDNEARBYLATLONG = "FindNearByLatLong";
    public static final String API_GENERATETEMPCUSTOMER = "GenerateTempAccount";
    public static final String API_GETCUSTOMERHISTORY = "GetCustomerHistory";
    public static final String API_GETDEALBYCODE = "GetDealByCode";
    public static final String API_GETMENUDEALS = "GetDeals";
    public static final String API_GETMENUITEMS = "GetMenuItems";
    public static final String API_GETMENUPIZZA = "GetMenuPizza";
    public static final String API_GETNATIONALDEALSBYSTORE = "GetNationalDealsByStore";
    public static final String API_GETORDERSTATUS = "GetOrderStatus";
    public static final String API_GETSTATICCONTENT = "GetStaticContent";
    public static final String API_GETSTOREINFO = "GetStoreInfo";
    public static final String API_GIFTCARDBALANCE = "GiftCardBalanceCheck";
    public static final String API_HTMLORDER = "HTMLOrder";
    public static final String API_LOGINCUSTOMER = "LoginCustomer";
    public static final String API_MODIFYCUSTOMER = "ModifyCustomer";
    public static final String API_PRICEORDER = "PriceOrder";
    public static final String API_SUBMITORDER = "SubmitOrder";
    private static final String APP_SOURCE = "Android";
    private static final String APP_VERSION = "2.1.1";
    public static final String CITY = "City";
    public static final int DEFAULT_MAXRESULTS = 10;
    public static final String DINE_IN = "DineIn";
    public static final String DISTANCE = "Distance";
    public static final String DISTANCE_UNITS = "DistanceUnits";
    public static final int IDP_EMAIL = 0;
    public static final int IDP_GETSECURITYQUESTION = 1;
    public static final int IDP_RESET = 2;
    public static final int ID_ADDRESS = 1;
    public static final int ID_BIRTHDATE = 3;
    public static final int ID_CELLPHONE = 2;
    public static final int ID_DELETE_ADDRESS = 9;
    public static final int ID_DELETE_PAYMENT = 10;
    public static final int ID_EMAIL = 6;
    public static final int ID_EMAILOPTIN = 5;
    public static final int ID_NAME = 0;
    public static final int ID_PASSWORD = 7;
    public static final int ID_PAYMENT = 8;
    public static final int ID_SECURITYCHALLENGE = 4;
    public static final String JSON_ADDRESS = "address";
    public static final String JSON_ADDRESSLIST = "address_list";
    public static final String JSON_APARTMENT = "apartment";
    public static final String JSON_BILLINGADDRESS = "billing_address";
    public static final String JSON_BILLINGNAME = "billing_name";
    public static final String JSON_BILLINGZIP = "billing_zip";
    public static final String JSON_BIRTHMONTH = "birth_month";
    public static final String JSON_BIRTHYEAR = "birth_year";
    public static final String JSON_CARDEXPIRATION = "card_expiration";
    public static final String JSON_CARDNUMBER = "card_number";
    public static final String JSON_CARDTYPE = "card_type";
    public static final String JSON_CARD_EDITED = "edited";
    public static final String JSON_CELLPHONE = "cellphone";
    public static final String JSON_CELLPHONECRMOPTIN = "cellphone_crm_optin";
    public static final String JSON_CELLPHONEOPTIN = "cellphone_optin";
    public static final String JSON_CELLPHONEOPTOUT = "cellphone_optout";
    public static final String JSON_CITY = "city";
    public static final String JSON_COUPON = "coupon";
    public static final String JSON_COUPON_CODE = "couponCode";
    public static final String JSON_CUSTOMERCITY = "customer_city";
    public static final String JSON_CUSTOMERDATA = "customer_data";
    public static final String JSON_CUSTOMERSTATE = "customer_state";
    public static final String JSON_CUSTOMERSTREET = "customer_street";
    public static final String JSON_CUSTOMERZIP = "customer_zip";
    public static final String JSON_DIGEST = "digest";
    public static final String JSON_DWELLCODE = "dwell_code";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_EMAILCRMOPTIN = "email_crm_optin";
    public static final String JSON_FIRSTNAME = "first_name";
    public static final String JSON_FOP = "fop";
    public static final String JSON_FORMAT = "format";
    public static final String JSON_GATEWAY = "https://quikorder.pizzahut.com/phorders3/service.php";
    public static final String JSON_INDEX = "index";
    public static final String JSON_LASTNAME = "last_name";
    public static final String JSON_LATITUDE = "latitude";
    public static final String JSON_LOCATIONINDEX = "location_index";
    public static final String JSON_LOCATIONNAME = "location_name";
    public static final String JSON_LONGITUDE = "longitude";
    public static final String JSON_MAXRESULTS = "maxresults";
    public static final String JSON_MENUCATEGORY = "menu_category";
    public static final String JSON_NEWPASSWORD = "new_password";
    public static final String JSON_NOEXCEPTIONS = "no_exceptions";
    public static final String JSON_OCCASSION = "occasion";
    public static final String JSON_OPTION = "option";
    public static final String JSON_ORDERITEMS = "orderItems";
    public static final String JSON_ORDERNUMBER = "orderNumber";
    public static final String JSON_PASSWORD = "password";
    public static final String JSON_PAYMENT = "payment";
    public static final String JSON_PAYMENTHISTORY = "payment_history";
    public static final String JSON_PHONE = "phone";
    public static final String JSON_PHONEEXT = "phone_ext";
    public static final String JSON_PRIMARYINDEX = "primary_index";
    public static final String JSON_SAVEDCARDS = "saved_cards";
    public static final String JSON_SECTION = "section";
    public static final String JSON_SECURITYCHALLENGE = "security_challenge";
    public static final String JSON_SECURITYRESPONSE = "security_response";
    public static final String JSON_SESSIONTOKEN = "sessionToken";
    public static final String JSON_SPECIALINSTR = "special_instr";
    public static final String JSON_SPECINST = "spec_inst";
    public static final String JSON_SSOTOKEN = "SSOtoken";
    public static final String JSON_STATE = "state";
    public static final String JSON_STORENUMBER = "store_number";
    public static final String JSON_STREETADDRESS = "street_address";
    public static final String JSON_SUBSECTION = "subsection";
    public static final String JSON_TOTALORDERS = "total_orders";
    public static final String JSON_UNITID = "unitID";
    public static final String JSON_USERID = "userID";
    public static final String JSON_USERPW = "userPW";
    public static final String JSON_ZIP = "zip";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String OPTION_EMAIL = "email";
    public static final String OPTION_GETSECURITYQUESTION = "getSecurityQuestion";
    public static final String OPTION_HTML = "html";
    public static final String OPTION_RESET = "reset";
    public static final String OPTION_TEXT = "text";
    public static final String POSTAL_CODE = "PostalCode";
    public static final String POST_ACCOUNTID = "accountID";
    public static final String POST_ACCOUNTPW = "accountPW";
    public static final String POST_APPSOURCE = "appsource";
    public static final String POST_APPVERSION = "appversion";
    public static final String POST_DATA = "data";
    public static final String POST_REQUEST = "request";
    public static final String POST_VERSION = "version";
    public static final String SECTION_ADDRESS = "address";
    public static final String SECTION_ALL = "all";
    public static final String SECTION_BIRTHDATE = "birthdate";
    public static final String SECTION_CELLPHONE = "cellphone";
    public static final String SECTION_DELETEADDRESS = "deleteaddress";
    public static final String SECTION_EMAIL = "email";
    public static final String SECTION_EMAILOPTIN = "emailoptin";
    public static final String SECTION_FAQ = "faq";
    public static final String SECTION_NAME = "name";
    public static final String SECTION_PASSWORD = "password";
    public static final String SECTION_PAYMENT = "payment";
    public static final String SECTION_PRIVACY = "privacy";
    public static final String SECTION_SECURITYCHALLENGE = "securitychallenge";
    public static final String SECTION_TERMS = "terms";
    public static final String STATE = "State";
    public static final String STORE_NUMBER = "StoreNumber";
    public static final String STREET_ADDRESS = "StreetAddress";
    public static final String TEST_ACCOUNT = "phdemo027278";
    public static final String TEST_PASSWORD = "demo027278";
    private static final int TIMEOUT = 10000;
    private static final String VERSION = "2.0";
    private BasicNameValuePair accountID;
    private BasicNameValuePair accountPW;
    private BasicNameValuePair appSource;
    private BasicNameValuePair appVersion;
    HttpClient httpclient;
    HttpPost httppost;
    List<NameValuePair> postPairs;
    private BasicNameValuePair version;
    private boolean useProxy = false;
    private String proxyHost = "";
    private int proxyPort = 8000;
    private int TIMEOUT_MS = 20000;

    public static QuickOrderAPI getInstance() {
        QuickOrderAPI instance = new QuickOrderAPI();
        instance.clearPostPairs();
        return instance;
    }

    private QuickOrderAPI() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SMSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            if (this.useProxy) {
                HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort);
                params.setParameter("http.route.default-proxy", proxy);
            }
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, OAuth.ENCODING);
            HttpConnectionParams.setStaleCheckingEnabled(params, false);
            HttpConnectionParams.setConnectionTimeout(params, this.TIMEOUT_MS);
            HttpConnectionParams.setSoTimeout(params, this.TIMEOUT_MS);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme(TwitterManager.OAUTH_CALLBACK_SCHEME, PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            this.httpclient = new DefaultHttpClient(ccm, params);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e2) {
            e2.printStackTrace();
        } catch (KeyStoreException e3) {
            e3.printStackTrace();
        } catch (NoSuchAlgorithmException e4) {
            e4.printStackTrace();
        } catch (UnrecoverableKeyException e5) {
            e5.printStackTrace();
        } catch (CertificateException e6) {
            e6.printStackTrace();
        }
        HttpConnectionParams.setConnectionTimeout(this.httpclient.getParams(), TIMEOUT);
        this.httppost = new HttpPost("https://quikorder.pizzahut.com/phorders3/service.php");
        this.postPairs = new ArrayList();
        this.accountID = new BasicNameValuePair(POST_ACCOUNTID, ACCOUNTID);
        this.accountPW = new BasicNameValuePair(POST_ACCOUNTPW, ACCOUNTPW);
        this.version = new BasicNameValuePair(POST_VERSION, VERSION);
        this.appSource = new BasicNameValuePair(POST_APPSOURCE, APP_SOURCE);
        this.appVersion = new BasicNameValuePair(POST_APPVERSION, APP_VERSION);
    }

    private void clearPostPairs() {
        this.postPairs.clear();
        this.postPairs.add(this.accountID);
        this.postPairs.add(this.accountPW);
        this.postPairs.add(this.version);
        this.postPairs.add(this.appSource);
        this.postPairs.add(this.appVersion);
    }

    /* loaded from: classes.dex */
    public class SMSSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext;

        public SMSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            this.sslContext = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() { // from class: com.yum.pizzahut.quickorder.QuickOrderAPI.SMSSLSocketFactory.1
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

    private String getData(List<NameValuePair> p, boolean usePOST) throws ClientProtocolException, IOException {
        String responseBody;
        BasicResponseHandler basicResponseHandler = new BasicResponseHandler();
        if (usePOST) {
            this.httppost.setEntity(new UrlEncodedFormEntity(p));
            responseBody = (String) this.httpclient.execute(this.httppost, basicResponseHandler);
        } else {
            String getUrl = "https://quikorder.pizzahut.com/phorders3/service.php?" + URLEncodedUtils.format(p, "utf-8");
            HttpGet httpget = new HttpGet(getUrl);
            responseBody = (String) this.httpclient.execute(httpget, basicResponseHandler);
        }
        shutdown();
        return responseBody;
    }

    public void shutdown() {
        if (this.httpclient != null && this.httpclient.getConnectionManager() != null) {
            this.httpclient.getConnectionManager().shutdown();
        }
        this.httpclient = null;
        this.httppost = null;
        this.postPairs = null;
    }

    public static boolean getHasStoreProperty(JSONObject item, String name) throws JSONException {
        return item.getString("Name").equals(name);
    }

    public Object loginCustomer(String username, String password) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_USERID, username);
        custJson.put(JSON_USERPW, password);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_LOGINCUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String jsonStr = getData(this.postPairs, false);
        JSONObject resultJson = new JSONObject(jsonStr);
        if (resultJson == null || resultJson.length() <= 0) {
            return null;
        }
        if (resultJson.has("error")) {
            return resultJson.getString("error");
        }
        PizzaHutUser user = new PizzaHutUser();
        user.userLogin(resultJson, false);
        if (user.getToken() == null) {
            return null;
        }
        return user;
    }

    public PizzaHutUser loginSSO(String username, String password, String ssoToken) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_USERID, username);
        custJson.put(JSON_USERPW, password);
        custJson.put(JSON_SSOTOKEN, ssoToken);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_LOGINCUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String jsonStr = getData(this.postPairs, false);
        JSONObject resultJson = new JSONObject(jsonStr);
        if (resultJson == null || resultJson.length() <= 0) {
            return null;
        }
        PizzaHutUser user = new PizzaHutUser();
        user.userLogin(resultJson, false);
        return user;
    }

    public PizzaHutUser loginWithToken(String ssoToken) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_SSOTOKEN, ssoToken);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_LOGINCUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String jsonStr = getData(this.postPairs, false);
        JSONObject resultJson = new JSONObject(jsonStr);
        if (resultJson == null || resultJson.length() <= 0) {
            return null;
        }
        PizzaHutUser user = new PizzaHutUser();
        user.userLogin(resultJson, false);
        return user;
    }

    public boolean clearSSOToken(String ssoToken) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_SSOTOKEN, ssoToken);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_LOGINCUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String jsonStr = getData(this.postPairs, false);
        JSONObject resultJson = new JSONObject(jsonStr);
        if (resultJson == null || !resultJson.has("status")) {
            return false;
        }
        String status = resultJson.optString("status");
        return status.equals(Response.SUCCESS);
    }

    public Object createCustomer(PizzaHutUser user) throws JSONException, ClientProtocolException, IOException {
        JSONObject customer_data = new JSONObject();
        JSONObject payment = new JSONObject();
        JSONArray saved_cards = new JSONArray();
        JSONObject address = new JSONObject();
        JSONArray address_list = new JSONArray();
        payment.put(JSON_PRIMARYINDEX, "");
        payment.put(JSON_PAYMENTHISTORY, false);
        payment.put(JSON_SAVEDCARDS, saved_cards);
        customer_data.put("payment", payment);
        Address addr = user.getAddressList().get(0);
        address.put(JSON_STREETADDRESS, addr.getAddress());
        address.put(JSON_APARTMENT, addr.getApartment());
        address.put(JSON_CITY, addr.getCity());
        address.put(JSON_STATE, addr.getState());
        address.put(JSON_ZIP, addr.getZip());
        address.put(JSON_LOCATIONINDEX, JSONObject.NULL);
        address.put(JSON_LOCATIONNAME, addr.getName());
        address.put(JSON_STORENUMBER, JSONObject.NULL);
        address.put(JSON_PHONE, addr.getPhone());
        address.put(JSON_PHONEEXT, "");
        address.put(JSON_DWELLCODE, "P");
        address.put(JSON_SPECINST, addr.getSpecInst());
        address_list.put(address);
        customer_data.put("address", address_list);
        customer_data.put(JSON_FIRSTNAME, user.getFirstName());
        customer_data.put(JSON_LASTNAME, user.getLastName());
        customer_data.put(JSON_BIRTHMONTH, user.getBirthMonth());
        customer_data.put(JSON_BIRTHYEAR, user.getBirthYear());
        customer_data.put("cellphone", user.getCellPhone());
        customer_data.put(JSON_CELLPHONECRMOPTIN, user.getPhoneCrmOptIn().toString());
        customer_data.put(JSON_EMAILCRMOPTIN, user.getEmailOptIn());
        customer_data.put(JSON_SECURITYCHALLENGE, user.getSecurityChallenge());
        customer_data.put(JSON_SECURITYRESPONSE, user.getSecurityResponse());
        customer_data.put("email", user.getEmail());
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_CUSTOMERDATA, customer_data);
        custJson.put(JSON_USERPW, user.getPW());
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_CREATECUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        if (resultJson.has("error")) {
            return resultJson.getString("error");
        }
        return new Token(resultJson);
    }

    public Response modifyCustomer(PizzaHutUser user, int section) throws JSONException, ClientProtocolException, IOException {
        String sessionToken = user.getToken().getSessionToken();
        boolean usePost = false;
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        switch (section) {
            case 0:
                jSONObject2.put(JSON_SECTION, SECTION_NAME);
                jSONObject.put(JSON_FIRSTNAME, user.getFirstName());
                jSONObject.put(JSON_LASTNAME, user.getLastName());
                break;
            case 1:
                jSONObject2.put(JSON_SECTION, "address");
                JSONArray address_list = new JSONArray();
                ArrayList<Address> addresses = user.getAddressList();
                int i = 0;
                while (true) {
                    if (i < addresses.size()) {
                        Address addr = addresses.get(i);
                        JSONObject address = new JSONObject();
                        if (addr.getIndex() < 0) {
                            address.put(JSON_LOCATIONINDEX, JSONObject.NULL);
                            address.put(JSON_STREETADDRESS, addr.getAddress());
                            address.put(JSON_APARTMENT, addr.getApartment());
                            address.put(JSON_CITY, addr.getCity());
                            address.put(JSON_STATE, addr.getState());
                            address.put(JSON_ZIP, addr.getZip());
                            address.put(JSON_LOCATIONNAME, addr.getName());
                            address.put(JSON_PHONE, addr.getPhone());
                            address.put(JSON_DWELLCODE, "P");
                            address.put(JSON_SPECINST, addr.getSpecInst());
                            address_list.put(address);
                        } else if (!addr.isEdited().booleanValue()) {
                            i++;
                        } else {
                            address.put(JSON_LOCATIONINDEX, Integer.toString(addr.getIndex()));
                            address.put(JSON_STREETADDRESS, addr.getAddress());
                            address.put(JSON_APARTMENT, addr.getApartment());
                            address.put(JSON_CITY, addr.getCity());
                            address.put(JSON_STATE, addr.getState());
                            address.put(JSON_ZIP, addr.getZip());
                            address.put(JSON_LOCATIONNAME, addr.getName());
                            address.put(JSON_PHONE, addr.getPhone());
                            address.put(JSON_DWELLCODE, "P");
                            address.put(JSON_SPECINST, addr.getSpecInst());
                            address_list.put(address);
                        }
                    }
                }
                jSONObject.put("address", address_list);
                break;
            case 2:
                jSONObject2.put(JSON_SECTION, "cellphone");
                jSONObject.put("cellphone", user.getCellPhone());
                jSONObject.put(JSON_CELLPHONECRMOPTIN, user.getPhoneCrmOptIn().toString());
                break;
            case 3:
                jSONObject2.put(JSON_SECTION, SECTION_BIRTHDATE);
                jSONObject.put(JSON_BIRTHMONTH, user.getBirthMonth());
                jSONObject.put(JSON_BIRTHYEAR, user.getBirthYear());
                break;
            case 4:
                jSONObject2.put(JSON_SECTION, SECTION_SECURITYCHALLENGE);
                jSONObject.put(JSON_SECURITYCHALLENGE, user.getSecurityChallenge());
                jSONObject.put(JSON_SECURITYRESPONSE, user.getSecurityResponse());
                break;
            case 5:
                jSONObject2.put(JSON_SECTION, SECTION_EMAILOPTIN);
                jSONObject.put(JSON_EMAILCRMOPTIN, user.getEmailOptIn());
                break;
            case 6:
                jSONObject2.put(JSON_SECTION, "email");
                jSONObject.put("email", user.getEmail());
                break;
            case 7:
                jSONObject2.put(JSON_SECTION, "password");
                jSONObject.put("password", user.getPW());
                break;
            case 8:
                usePost = true;
                jSONObject2.put(JSON_SECTION, "payment");
                JSONObject jSONObject3 = new JSONObject();
                JSONArray saved_cards = new JSONArray();
                ArrayList<Card> cards = user.getPayCards();
                for (int i2 = 0; i2 < cards.size(); i2++) {
                    Card c = cards.get(i2);
                    JSONObject card = new JSONObject();
                    card.put(JSON_INDEX, c.getIndex());
                    card.put(JSON_CARDTYPE, c.getCardType());
                    card.put(JSON_CARDEXPIRATION, c.getCardExpire());
                    card.put(JSON_BILLINGZIP, c.getCardBillingZip());
                    card.put(JSON_BILLINGADDRESS, "");
                    card.put(JSON_BILLINGNAME, c.getCardBillingName());
                    card.put(JSON_CARDNUMBER, (c.getCardLastFour() == null || c.getCardLastFour().length() < 1) ? c.getCardNumber() : c.getCardLastFour());
                    card.put(JSON_CARD_EDITED, c.getCardEdited().equals("Y") ? "Y" : "N");
                    saved_cards.put(card);
                }
                jSONObject3.put(JSON_PRIMARYINDEX, 1);
                jSONObject3.put(JSON_PAYMENTHISTORY, true);
                jSONObject3.put(JSON_SAVEDCARDS, saved_cards);
                jSONObject.put("payment", jSONObject3);
                break;
            case 9:
                jSONObject2.put(JSON_SECTION, SECTION_DELETEADDRESS);
                JSONArray address_array = new JSONArray();
                ArrayList<Address> addressList = user.getAddressList();
                int i3 = 0;
                while (true) {
                    if (i3 < addressList.size()) {
                        Address addr2 = addressList.get(i3);
                        JSONObject address2 = new JSONObject();
                        if (!addr2.isEdited().booleanValue()) {
                            i3++;
                        } else {
                            address2.put(JSON_LOCATIONINDEX, Integer.toString(addr2.getIndex()));
                            address_array.put(address2);
                        }
                    }
                }
                jSONObject.put("address", address_array);
                break;
        }
        jSONObject2.put(JSON_CUSTOMERDATA, jSONObject);
        jSONObject2.put(JSON_SESSIONTOKEN, sessionToken.toString());
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_MODIFYCUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, jSONObject2.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, usePost));
        Response r = new Response(resultJson);
        return r;
    }

    public PizzaHutUser generateTempAccount(Address tempAddress) throws JSONException, ClientProtocolException, IOException {
        JSONObject apiAddress = new JSONObject();
        JSONObject custJson = new JSONObject();
        new JSONObject();
        if (tempAddress != null) {
            apiAddress.put(JSON_STREETADDRESS, tempAddress.getAddress());
            apiAddress.put(JSON_APARTMENT, tempAddress.getApartment());
            apiAddress.put(JSON_CITY, tempAddress.getCity());
            apiAddress.put(JSON_STATE, tempAddress.getState());
            apiAddress.put(JSON_ZIP, tempAddress.getZip());
            custJson.put("address", apiAddress);
        }
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GENERATETEMPCUSTOMER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        PizzaHutUser user = new PizzaHutUser();
        user.userLogin(resultJson, true);
        return user;
    }

    public Response emailPassword(String email) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_OPTION, "email");
        custJson.put("email", email);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_EMAILPASSWORD));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        Response resp = new Response(resultJson);
        return resp;
    }

    public StoreInfo getStoreInfo(String storeNumber) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        if (storeNumber != null && storeNumber.length() > 0) {
            custJson.put(JSON_UNITID, storeNumber.toString());
            custJson.put(JSON_MENUCATEGORY, true);
            custJson.put(JSON_NOEXCEPTIONS, true);
        }
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GETSTOREINFO));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        StoreInfo store = new StoreInfo(resultJson);
        return store;
    }

    public JSONObject findNearbyStores(String latitude, String longitude) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_LATITUDE, latitude);
        custJson.put(JSON_LONGITUDE, longitude);
        custJson.put(JSON_MAXRESULTS, 10);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_FINDNEARBYLATLONG));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        return resultJson;
    }

    public JSONObject findNearbyAddress(String zipCode, String city, String state) throws JSONException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_CUSTOMERZIP, zipCode);
        if (city != null && city.length() > 0) {
            custJson.put(JSON_CUSTOMERCITY, city);
        }
        if (state != null && state.length() > 0) {
            custJson.put(JSON_CUSTOMERSTATE, state);
        }
        custJson.put(JSON_MAXRESULTS, 10);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_FINDNEARBYADDRESS));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String data = getData(this.postPairs, false);
        JSONObject resultJson = new JSONObject(data);
        return resultJson;
    }

    public JSONObject findDeliveryStore(String address, String zip, String city, String state) throws JSONException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_CUSTOMERSTREET, address);
        if (zip != null) {
            custJson.put(JSON_CUSTOMERZIP, zip);
        }
        if (city != null && state != null) {
            custJson.put(JSON_CUSTOMERCITY, city);
            custJson.put(JSON_CUSTOMERSTATE, state);
        }
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_FINDELIVERYSTORE));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String data = getData(this.postPairs, false);
        JSONObject resultJson = new JSONObject(data);
        return resultJson;
    }

    public String getStaticContent(String format, String section) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        if (format != null && format != "") {
            custJson.put(JSON_FORMAT, format);
        } else {
            custJson.put(JSON_FORMAT, OPTION_TEXT);
        }
        if (section != null && section != "") {
            custJson.put(JSON_SECTION, section);
        } else {
            custJson.put(JSON_SECTION, SECTION_ALL);
        }
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GETSTATICCONTENT));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        if (!resultJson.has(section)) {
            return null;
        }
        if (section.equals("sec_question")) {
            String data = resultJson.getJSONObject(section).toString();
            return data;
        }
        String data2 = resultJson.getString(section);
        return data2;
    }

    public String[] getGiftCardBalance(String giftCardNumber, String giftCardPin) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put("GiftCardNumber", giftCardNumber);
        custJson.put("GiftCardPin", giftCardPin);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GIFTCARDBALANCE));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        String result = null;
        try {
            result = resultJson.getString("error");
        } catch (JSONException e) {
        }
        if (result == null) {
            result = resultJson.getString("balance");
        }
        String[] results = {resultJson.getString("status"), result};
        return results;
    }

    public String getOrderStatus(String sessionToken, String orderNumber, String storeNumber) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_UNITID, storeNumber);
        custJson.put(JSON_ORDERNUMBER, orderNumber);
        custJson.put(JSON_SESSIONTOKEN, sessionToken);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GETORDERSTATUS));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        try {
            String orderTime = resultJson.optString("order_time");
            return orderTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray getCarouselItems() throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_UNITID, "997");
        custJson.put(JSON_OCCASSION, "D");
        custJson.put(JSON_SECTION, "category");
        custJson.put(JSON_SUBSECTION, "APICAROUSEL");
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GETMENUITEMS));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String response = getData(this.postPairs, false);
        JSONObject resp = new JSONObject(response);
        JSONObject cate = resp.getJSONObject("category");
        JSONArray caroArray = cate.getJSONArray("APICAROUSEL");
        return caroArray;
    }

    public String getHmtlOrder(String storeNumber, String occasion, int locationIndex, String action, String reorderNumber) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_SESSIONTOKEN, PizzaHutApp.getInstance().getUser().getToken().getSessionToken());
        custJson.put(JSON_UNITID, storeNumber);
        custJson.put(JSON_OCCASSION, occasion);
        if (locationIndex == -1) {
            locationIndex = 1;
        }
        custJson.put(JSON_LOCATIONINDEX, locationIndex);
        custJson.put("action", action);
        if (reorderNumber != null) {
            custJson.put("reorder_number", reorderNumber);
        }
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_HTMLORDER));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        String url = resultJson.optString("url");
        Log.i("HTML5ORDER", url);
        return url;
    }

    public String getCustomerHistory(PizzaHutUser user) throws JSONException, ClientProtocolException, IOException, ParseException, NullPointerException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_SESSIONTOKEN, user.getToken().getSessionToken());
        custJson.put("max_previous", 10);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, API_GETCUSTOMERHISTORY));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        JSONObject resultJson = new JSONObject(getData(this.postPairs, false));
        if (resultJson.has("error")) {
            return "expired";
        }
        user.setPreviousOrders(resultJson);
        return resultJson.toString();
    }

    public ArrayList<Messages> getMessages() throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put("maxcount", 10);
        this.postPairs.add(new BasicNameValuePair(POST_REQUEST, "GetMessages"));
        this.postPairs.add(new BasicNameValuePair(POST_DATA, custJson.toString()));
        String result = getData(this.postPairs, false);
        JSONArray resultArray = new JSONArray(result);
        ArrayList<Messages> list = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {
            Messages message = new Messages(resultArray.getJSONObject(i));
            list.add(message);
        }
        return list;
    }

    public ArrayList<Object> getFacebookFeed(String accountID, String accountPW, int maxPosts, int maxDays) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_USERID, accountID);
        custJson.put(JSON_USERPW, accountPW);
        custJson.put("max_posts", maxPosts);
        custJson.put("max_days", maxDays);
        String result = getData(this.postPairs, false);
        JSONArray resultArray = new JSONArray(result);
        ArrayList<Object> facebookEntries = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {
            facebookEntries.add(resultArray.get(i));
        }
        return facebookEntries;
    }

    public ArrayList<Object> getTwitterFeed(String accountID, String accountPW, int maxPosts, int maxDays) throws JSONException, ClientProtocolException, IOException {
        JSONObject custJson = new JSONObject();
        custJson.put(JSON_USERID, accountID);
        custJson.put(JSON_USERPW, accountPW);
        custJson.put("max_posts", maxPosts);
        custJson.put("max_days", maxDays);
        String result = getData(this.postPairs, false);
        JSONArray resultArray = new JSONArray(result);
        ArrayList<Object> twitterEntries = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {
            twitterEntries.add(resultArray.get(i));
        }
        return twitterEntries;
    }
}
