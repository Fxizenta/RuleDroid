package com.dragonflow;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Xml;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.aircard.soap.AirCard_SoapValue;
import com.dragonflow.busi.process.TestTask;
import com.dragonflow.cloud.RemoteApi;
import com.dragonflow.cloud.RequestResult;
import com.filebrowse.FileService;
import com.genie.statistics.db.Genie_Data_DBHelper;
import com.soap.api.SoapApi;
import com.soap.api.SoapParams;
import com.soap.api.SoapParser;
import com.soap.api.SoapUtil;
import com.tools.Tools;
import com.wififilemanage.http.WFM_NanoHTTPD;
import com.wififilemanage.obj.WFM_Result;
import com.wififilemanage.util.LogUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class GenieRequest {
    public static final String API_KEY = "3443313B70772D2DD73B45D39F376199";
    private static final String SmartNetworkUrl = "https://genie.netgear.com";
    private static final String TAG = "GenieRequest";
    public static final String m_SessionID = "A7D88AE69687E58D9A00";
    private boolean m_Cancleflag;
    private String m_GateWayIp;
    private boolean m_IsShowProgress;
    private ArrayList<GenieRequestInfo> m_RequestInfo;
    private String m_SmartUrl;
    private Context m_context;
    private static int testint = 0;
    public static boolean m_SmartNetWork = false;
    public static int m_RequestPort = 80;
    public static boolean m_First = false;
    public static boolean wifiIsClose = false;
    public static GenieSmartNetWorkInfo m_SmartInfo = new GenieSmartNetWorkInfo();
    public static String password = "";
    static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { // from class: com.dragonflow.GenieRequest.1
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private ProgressDialog m_ProgressDialog = null;
    private boolean m_ShowProgress = true;
    private boolean m_ProgressCancelFlag = true;
    private String m_Progresstitle = null;
    private String m_Progressmessage = null;
    private RequestTask m_RequestTask = null;
    private boolean m_CGDGflag = false;
    private RequestFinish m_finishaction = null;
    private OnProgressCancelListener m_ProgressCancel = null;

    /* loaded from: classes.dex */
    public interface OnProgressCancelListener {
        void OnProgressCancel();
    }

    /* loaded from: classes.dex */
    public interface RequestFinish {
        void OnFinish(GenieRequest genieRequest);
    }

    public void SetProgressCancelListener(OnProgressCancelListener action) {
        this.m_ProgressCancel = action;
    }

    public GenieRequest(Context context, ArrayList<GenieRequestInfo> requestinfo) {
        this.m_context = null;
        this.m_GateWayIp = null;
        this.m_IsShowProgress = false;
        this.m_SmartUrl = null;
        this.m_Cancleflag = false;
        this.m_context = context;
        this.m_GateWayIp = getGateWay();
        this.m_IsShowProgress = false;
        this.m_Cancleflag = false;
        this.m_RequestInfo = requestinfo;
        this.m_SmartUrl = GetSmartNetworkUrl(context);
    }

    public void SetFinishAction(RequestFinish action) {
        this.m_finishaction = action;
    }

    public void SetProgressInfo(boolean show, boolean cancel) {
        this.m_ShowProgress = show;
        this.m_ProgressCancelFlag = cancel;
    }

    public void SetProgressText(String title, String message) {
        this.m_Progresstitle = title;
        this.m_Progressmessage = message;
    }

    public void Start() {
        RequestTask requestTask = null;
        if (this.m_context != null && this.m_RequestInfo != null) {
            if (this.m_context != null && this.m_ShowProgress) {
                if (this.m_ProgressCancelFlag) {
                    if (this.m_Progressmessage == null) {
                        this.m_Progressmessage = String.valueOf(this.m_context.getResources().getString(R.string.pleasewait)) + "...";
                    }
                    this.m_ProgressDialog = ProgressDialog.show(this.m_context, this.m_Progresstitle, this.m_Progressmessage, true, true, new DialogInterface.OnCancelListener() { // from class: com.dragonflow.GenieRequest.2
                        @Override // android.content.DialogInterface.OnCancelListener
                        public void onCancel(DialogInterface dialog) {
                            GenieRequest.this.Stop();
                            if (GenieRequest.this.m_ProgressCancel != null) {
                                GenieRequest.this.m_ProgressCancel.OnProgressCancel();
                                GenieRequest.this.m_ProgressCancel = null;
                            }
                        }
                    });
                } else {
                    this.m_ProgressDialog = ProgressDialog.show(this.m_context, null, String.valueOf(this.m_context.getResources().getString(R.string.pleasewait)) + "...", true, true);
                }
                this.m_IsShowProgress = true;
            }
            this.m_RequestTask = new RequestTask(this, requestTask);
            if (getSDKVersionNumber() >= 11) {
                this.m_RequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.m_RequestInfo);
            } else {
                this.m_RequestTask.execute(this.m_RequestInfo);
            }
            GenieDebug.error("debug", "GenieRequest.Start end");
        }
    }

    public void Stop() {
        this.m_Cancleflag = true;
        if (this.m_IsShowProgress && this.m_ProgressDialog != null) {
            if (this.m_ProgressDialog.isShowing()) {
                this.m_ProgressDialog.cancel();
            }
            this.m_ProgressDialog = null;
            this.m_IsShowProgress = false;
        }
        if (this.m_RequestTask != null) {
            GenieDebug.error("debug", "GenieRequest Stop 0");
            this.m_RequestTask.cancelhttpUrlConnection();
            this.m_RequestTask.CancelRequest();
            this.m_RequestTask.CancleHttpGet();
            GenieDebug.error("debug", "GenieRequest Stop 1");
            if (!this.m_RequestTask.isCancelled()) {
                GenieDebug.error("debug", "GenieRequest Stop 2");
                this.m_RequestTask.cancel(true);
            }
            this.m_RequestTask = null;
        }
    }

    public void sendbroad(GenieRequestInfo value) {
        GenieDebug.error("debug", "sendbroad 0");
        if (this.m_context != null && !this.m_Cancleflag) {
            Intent intent = new Intent("REQUEST_ACTION_RET_BROADCAST");
            intent.putExtra("REQUEST_ACTION_RET_LABLE", value.aRequestLable);
            intent.putExtra("REQUEST_ACTION_RET_ACTION_LABLE", value.aActionLable);
            intent.putExtra("REQUEST_ACTION_RET_TYPE", value.aRequestType);
            intent.putExtra("REQUEST_ACTION_RET_SERVER", value.aServer);
            intent.putExtra("REQUEST_ACTION_RET_METHOD", value.aMethod);
            intent.putExtra("REQUEST_ACTION_RET_RESULTTYPE", value.aResultType);
            intent.putExtra("REQUEST_ACTION_RET_RESPONSECODE", value.aResponseCode);
            intent.putExtra("REQUEST_ACTION_RET_HTTPRESPONSECODE", value.aHttpResponseCode);
            intent.putExtra("REQUEST_ACTION_RET_RESPONSE", value.aResponse);
            intent.putExtra("REQUEST_ACTION_RET_HTTP_TYPE", value.aHttpType);
            intent.putExtra("REQUEST_ACTION_RET_SOAP_TYPE", value.aSoapType);
            intent.putExtra("REQUEST_ACTION_RET_SMART_TYPE", value.aSmartType);
            intent.putExtra("REQUEST_ACTION_RET_OPENDNS_TYPE", value.aOpenDNSType);
            intent.putExtra("REQUEST_ACTION_RET_ERROR_CODE", value.errorcode);
            this.m_context.sendBroadcast(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RequestTask extends AsyncTask<ArrayList<GenieRequestInfo>, Void, Void> {
        private static final int request_delay = 1000;
        int elementIndex;
        private boolean m_Cancelfalg;
        private GenieRequestInfo m_RequestInfo;
        private HttpURLConnection m_httpConn;
        private URL m_url;
        int recordResults;
        private HttpGet request;

        private RequestTask() {
            this.m_RequestInfo = null;
            this.m_httpConn = null;
            this.m_Cancelfalg = false;
            this.m_url = null;
            this.recordResults = 0;
            this.request = null;
        }

        /* synthetic */ RequestTask(GenieRequest genieRequest, RequestTask requestTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(ArrayList<GenieRequestInfo>... arrayListArr) {
            GenieDebug.error("debug", "GenieRequest.RequestTask params.length=" + arrayListArr.length);
            this.m_Cancelfalg = false;
            loop0: for (ArrayList<GenieRequestInfo> RequestInfo : arrayListArr) {
                Iterator<GenieRequestInfo> it = RequestInfo.iterator();
                while (it.hasNext()) {
                    GenieRequestInfo temp = it.next();
                    GenieDebug.error("debug", "GenieRequest.RequestTask m_Cancelfalg=" + this.m_Cancelfalg);
                    if (this.m_Cancelfalg) {
                        break loop0;
                    }
                    if (this.m_RequestInfo != null) {
                        this.m_RequestInfo = null;
                    }
                    this.m_RequestInfo = null;
                    this.m_RequestInfo = temp;
                    GenieDebug.error("debug", "GenieRequest.RequestTask aServer=" + temp.aServer);
                    GenieDebug.error("debug", "GenieRequest.RequestTask aMethod=" + temp.aMethod);
                    GenieDebug.error("debug", "GenieRequest.RequestTask aRequestLable=" + temp.aRequestLable);
                    sendRequest(temp);
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute((RequestTask) result);
            GenieDebug.error("debug", "onPostExecute 0");
            if (GenieRequest.this.m_finishaction != null) {
                GenieDebug.error("debug", "onPostExecute 1");
                GenieRequest.this.m_finishaction.OnFinish(GenieRequest.this);
                GenieRequest.this.m_finishaction = null;
            }
            GenieRequest.this.m_ProgressCancel = null;
            if (GenieRequest.this.m_IsShowProgress && GenieRequest.this.m_ProgressDialog != null) {
                GenieDebug.error("debug", "onPostExecute 2");
                if (GenieRequest.this.m_ProgressDialog.isShowing()) {
                    GenieDebug.error("debug", "onPostExecute 3");
                    GenieRequest.this.m_ProgressDialog.cancel();
                }
                GenieRequest.this.m_ProgressDialog = null;
                GenieRequest.this.m_IsShowProgress = false;
            }
        }

        public void sendRequest(GenieRequestInfo aRequestInfo) {
            if (aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.Soap) {
                if (GenieRequest.m_SmartNetWork) {
                    sendSmartRequest(aRequestInfo);
                    return;
                }
                String md = GenieSoap.dictionary.get("ModelName");
                if (md == null || (!md.startsWith("CG") && !md.startsWith("DG"))) {
                    GenieRequest.this.m_CGDGflag = false;
                } else {
                    GenieRequest.this.m_CGDGflag = true;
                    aRequestInfo.aNeedwrap = true;
                }
                GenieRequest.this.m_CGDGflag = true;
                if (aRequestInfo.aNeedwrap) {
                    GenieRequestInfo start = new GenieRequestInfo();
                    ArrayList<String> startelement = new ArrayList<>();
                    startelement.add("NewSessionID");
                    startelement.add("A7D88AE69687E58D9A00");
                    start.aNeedParser = false;
                    start.aServer = "DeviceConfig";
                    start.aMethod = "ConfigurationStarted";
                    start.aSoapType = 100;
                    start.aTimeout = 15000;
                    this.m_RequestInfo = start;
                    sendRequest2Router(start.aServer, start.aMethod, startelement, start.aNeedParser, start.aTimeout, start.aSoapType);
                    if (GenieRequest.this.m_CGDGflag) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.m_RequestInfo = aRequestInfo;
                    if (this.m_RequestInfo.aSoapType == 4) {
                        String wep = GenieSoap.dictionary.get("NewBasicEncryptionModes");
                        if (wep != null && wep.equals("WEP")) {
                            GenieRequestInfo WEPWLan = new GenieRequestInfo();
                            ArrayList<String> WEPWLanelement = new ArrayList<>();
                            WEPWLanelement.add("NewWEPKey");
                            WEPWLanelement.add("null");
                            WEPWLanelement.add("NewWPAPassphrase");
                            WEPWLanelement.add("null");
                            WEPWLan.aNeedParser = false;
                            WEPWLan.aServer = "WLANConfiguration";
                            WEPWLan.aMethod = "GetWEPSecurityKeys";
                            WEPWLan.aSoapType = 17;
                            WEPWLan.aTimeout = 20000;
                            this.m_RequestInfo = WEPWLan;
                            sendRequest2Router(WEPWLan.aServer, WEPWLan.aMethod, WEPWLanelement, WEPWLan.aNeedParser, WEPWLan.aTimeout, WEPWLan.aSoapType);
                        } else {
                            sendRequest2Router(aRequestInfo.aServer, aRequestInfo.aMethod, aRequestInfo.aElement, aRequestInfo.aNeedParser, aRequestInfo.aTimeout, aRequestInfo.aSoapType);
                        }
                    } else {
                        sendRequest2Router(aRequestInfo.aServer, aRequestInfo.aMethod, aRequestInfo.aElement, aRequestInfo.aNeedParser, aRequestInfo.aTimeout, aRequestInfo.aSoapType);
                    }
                    if (GenieRequest.this.m_CGDGflag) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    GenieRequestInfo finish = new GenieRequestInfo();
                    ArrayList<String> finishelement = new ArrayList<>();
                    finishelement.add("NewStatus");
                    finishelement.add("ChangesApplied");
                    finish.aNeedParser = false;
                    finish.aServer = "DeviceConfig";
                    finish.aMethod = "ConfigurationFinished";
                    finish.aSoapType = 109;
                    finish.aTimeout = 15000;
                    this.m_RequestInfo = finish;
                    sendRequest2Router(finish.aServer, finish.aMethod, finishelement, finish.aNeedParser, finish.aTimeout, finish.aSoapType);
                    if (GenieRequest.this.m_CGDGflag) {
                        try {
                            Thread.sleep(1000L);
                            return;
                        } catch (InterruptedException e3) {
                            e3.printStackTrace();
                            return;
                        }
                    }
                    return;
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e4) {
                    e4.printStackTrace();
                }
                sendRequest2Router(aRequestInfo.aServer, aRequestInfo.aMethod, aRequestInfo.aElement, aRequestInfo.aNeedParser, aRequestInfo.aTimeout, aRequestInfo.aSoapType);
                return;
            }
            if (aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.Http) {
                switch (aRequestInfo.aHttpType) {
                    case 23:
                        if (!GenieApplication.isCloud) {
                            Tools.writelog("Request---CurrentSetting----one \n", true, GenieRequest.TAG);
                            if (!Tools.CurrentSetting(this.m_RequestInfo, aRequestInfo.aTimeout) && this.m_RequestInfo.aResultType != GenieGlobalDefines.RequestResultType.Succes && this.m_RequestInfo.aResultType != GenieGlobalDefines.RequestResultType.currtting) {
                                Tools.writelog("Request---CurrentSetting----two \n", true, GenieRequest.TAG);
                                Tools.CurrentSetting(this.m_RequestInfo, aRequestInfo.aTimeout);
                            }
                            GenieRequest.this.sendbroad(this.m_RequestInfo);
                            return;
                        }
                        return;
                    case 24:
                        ChekcLpcHost(aRequestInfo.aTimeout);
                        return;
                    default:
                        return;
                }
            }
            if (aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.OpenDNS) {
                this.m_RequestInfo = aRequestInfo;
                sendRequest2OpenDNS(aRequestInfo.aMethod, aRequestInfo.aElement, aRequestInfo.aTimeout, aRequestInfo.aOpenDNSType, aRequestInfo.aNeedParser);
            } else if (aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.SmartNetWork) {
                GenieDebug.error("debug", "aSmartType = " + aRequestInfo.aSmartType);
                GenieDebug.error("debug", "aServer = " + aRequestInfo.aServer);
                sendRequestToSmartNetWork(aRequestInfo.aRequestLable, aRequestInfo.aSmartType, aRequestInfo.aServer, aRequestInfo.aElement);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public void parsejson(String json, int OpenDNSType) {
            JSONObject jsonobj_response;
            JSONObject jsonobj_response2;
            String response = null;
            try {
                JSONObject jsonobj = new JSONObject(json);
                String status = jsonobj.getString("status");
                GenieDebug.error("parsejson", "status = " + status);
                if (status.equals(WFM_Result.SUCCESS)) {
                    try {
                        switch (OpenDNSType) {
                            case 29:
                                GenieSoap.dictionary.put("status", status);
                                String response2 = jsonobj.getString("response");
                                GenieDebug.error("parsejson", "response = " + response2);
                                GenieSoap.dictionary.put("ChildDeviceIDUserName", response2);
                                this.m_RequestInfo.aResponse = json;
                                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                return;
                            case 30:
                                GenieSoap.dictionary.put("status", status);
                                String response3 = jsonobj.getString("response");
                                GenieDebug.error("parsejson", "response = " + response3);
                                GenieSoap.dictionary.put("DeviceIDUserName", response3);
                                this.m_RequestInfo.aResponse = json;
                                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                return;
                            case 31:
                                GenieSoap.dictionary.put("status", status);
                                String response4 = jsonobj.getString("response");
                                GenieDebug.error("parsejson", "response = " + response4);
                                GenieSoap.dictionary.put("LoginBypassAccount", response4);
                                this.m_RequestInfo.aResponse = json;
                                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                return;
                            case 200:
                                GenieSoap.dictionary.put("status", status);
                                response = jsonobj.getString("response");
                                jsonobj_response = new JSONObject(response);
                                String token = jsonobj_response.getString("token");
                                GenieDebug.error("parsejson", "token = " + token);
                                GenieSoap.dictionary.put("token", token);
                                break;
                            case 201:
                                GenieSoap.dictionary.put("status", status);
                                response = jsonobj.getString("response");
                                jsonobj_response = new JSONObject(response);
                                String available = jsonobj_response.getString("available");
                                GenieDebug.error("parsejson", "available = " + available);
                                GenieSoap.dictionary.put("available", available);
                                break;
                            case 202:
                            case 208:
                            default:
                                this.m_RequestInfo.aResponse = json;
                                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                return;
                            case 204:
                                GenieSoap.dictionary.put("status", status);
                                response = jsonobj.getString("response");
                                jsonobj_response = new JSONObject(response);
                                String deviceid = jsonobj_response.getString("device_id");
                                GenieDebug.error("parsejson", "deviceid = " + deviceid);
                                GenieSoap.dictionary.put("NewDeviceID", deviceid);
                                break;
                            case 205:
                                GenieSoap.dictionary.put("status", status);
                                response = jsonobj.getString("response");
                                jsonobj_response = new JSONObject(response);
                                String deviceid2 = jsonobj_response.getString("device_id");
                                GenieDebug.error("parsejson", "deviceid = " + deviceid2);
                                GenieSoap.dictionary.put("NewDeviceID", deviceid2);
                                break;
                            case 206:
                                GenieSoap.dictionary.put("status", status);
                                GenieDebug.error("parsejson", "333333 response get ");
                                response = jsonobj.getString("response");
                                GenieDebug.error("parsejson", "333333 response = " + response);
                                jsonobj_response = new JSONObject(response);
                                GenieDebug.error("parsejson", "333333 bundle get ");
                                String bundle = jsonobj_response.getString("bundle");
                                GenieDebug.error("parsejson", "333333 bundle = " + bundle);
                                GenieSoap.dictionary.put("bundle", bundle);
                                break;
                            case 209:
                                GenieSoap.dictionary.put("status", status);
                                response = jsonobj.getString("response");
                                jsonobj_response = new JSONObject(response);
                                String bundle2 = jsonobj_response.getString("relay_token");
                                GenieDebug.error("parsejson", "bundle = " + bundle2);
                                GenieSoap.dictionary.put("relay_token", bundle2);
                                break;
                        }
                        this.m_RequestInfo.aResponse = json;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        return;
                    } catch (JSONException e) {
                        e = e;
                        if (206 == OpenDNSType) {
                            GenieDebug.error("JSONException", "response = " + response);
                            try {
                                jsonobj_response2 = new JSONObject(response);
                            } catch (JSONException e2) {
                                e1 = e2;
                            }
                            try {
                                String categories = jsonobj_response2.getString("categories");
                                if (categories != null) {
                                    GenieSoap.dictionary.put("bundle", "Custom");
                                    GenieSoap.dictionary.put("Custom", categories);
                                    this.m_RequestInfo.aResponse = json;
                                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                    return;
                                }
                            } catch (JSONException e3) {
                                e1 = e3;
                                e1.printStackTrace();
                                GenieSoap.dictionary.put("status", "JSONException");
                                this.m_RequestInfo.aResponse = e1.getMessage();
                                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                                return;
                            }
                        }
                        GenieSoap.dictionary.put("status", "JSONException");
                        e.printStackTrace();
                        this.m_RequestInfo.aResponse = e.getMessage();
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                        return;
                    }
                }
                GenieSoap.dictionary.put("error", jsonobj.getString("error"));
                String error_message = jsonobj.getString("error_message");
                if (error_message != null) {
                    GenieSoap.dictionary.put("error_message", error_message);
                }
                switch (OpenDNSType) {
                    case 29:
                        String error = jsonobj.getString("error");
                        GenieDebug.error("parsejson", "response = " + error);
                        GenieSoap.dictionary.put("error", error);
                        String error_message2 = jsonobj.getString("error_message");
                        GenieDebug.error("parsejson", "response = " + error_message2);
                        if (error_message2 != null) {
                            GenieSoap.dictionary.put("error_message", error_message2);
                        }
                        this.m_RequestInfo.aResponse = error_message2;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 30:
                        String error2 = jsonobj.getString("error");
                        GenieDebug.error("parsejson", "response = " + error2);
                        GenieSoap.dictionary.put("error", error2);
                        String error_message3 = jsonobj.getString("error_message");
                        GenieDebug.error("parsejson", "response = " + error_message3);
                        if (error_message3 != null) {
                            GenieSoap.dictionary.put("error_message", error_message3);
                        }
                        this.m_RequestInfo.aResponse = error_message3;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 31:
                        String error3 = jsonobj.getString("error");
                        GenieDebug.error("parsejson", "response = " + error3);
                        GenieSoap.dictionary.put("error", error3);
                        String error_message4 = jsonobj.getString("error_message");
                        GenieDebug.error("parsejson", "response = " + error_message4);
                        if (error_message4 != null) {
                            GenieSoap.dictionary.put("error_message", error_message4);
                        }
                        this.m_RequestInfo.aResponse = error_message4;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 200:
                        GenieSoap.dictionary.put("error", jsonobj.getString("error"));
                        String error_message5 = jsonobj.getString("error_message");
                        if (error_message5 != null) {
                            GenieSoap.dictionary.put("error_message", error_message5);
                        }
                        this.m_RequestInfo.aResponse = error_message5;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 201:
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 202:
                        GenieSoap.dictionary.put("error", jsonobj.getString("error"));
                        String error_message6 = jsonobj.getString("error_message");
                        if (error_message6 != null) {
                            GenieSoap.dictionary.put("error_message", error_message6);
                        }
                        this.m_RequestInfo.aResponse = error_message6;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 204:
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 206:
                        GenieSoap.dictionary.put("error", jsonobj.getString("error"));
                        String error_message7 = jsonobj.getString("error_message");
                        if (error_message7 != null) {
                            GenieSoap.dictionary.put("error_message", error_message7);
                        }
                        this.m_RequestInfo.aResponse = error_message7;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 208:
                        GenieSoap.dictionary.put("error", jsonobj.getString("error"));
                        String error_message8 = jsonobj.getString("error_message");
                        if (error_message8 != null) {
                            GenieSoap.dictionary.put("error_message", error_message8);
                        }
                        this.m_RequestInfo.aResponse = error_message8;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                    case 209:
                        GenieSoap.dictionary.put("error", jsonobj.getString("error"));
                        String error_message9 = jsonobj.getString("error_message");
                        if (error_message9 != null) {
                            GenieSoap.dictionary.put("error_message", error_message9);
                        }
                        this.m_RequestInfo.aResponse = error_message9;
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        break;
                }
                this.m_RequestInfo.aResponse = json;
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            } catch (JSONException e4) {
                e = e4;
                jsonobj_response = null;
            }
        }

        private void httpUrlConnection_OpenDNS(String requestString, int timeout, int OpenDNSType, boolean needParser) {
            int responseCode;
            cancelhttpUrlConnection();
            try {
                GenieDebug.error("httpUrlConnection_OpenDNS", "--strart--");
                GenieDebug.system_string("lh_plc", "httpUrlConnection_OpenDNS", requestString);
                this.m_url = new URL("https://api.opendns.com/v1/");
                this.m_httpConn = (HttpURLConnection) this.m_url.openConnection();
                this.m_httpConn.setDoOutput(true);
                this.m_httpConn.setDoInput(true);
                this.m_httpConn.setUseCaches(false);
                this.m_httpConn.setRequestMethod("POST");
                byte[] requestStringBytes = requestString.getBytes(Manifest.JAR_ENCODING);
                this.m_httpConn.setRequestProperty("Content-length", new StringBuilder().append(requestStringBytes.length).toString());
                this.m_httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                this.m_httpConn.setRequestProperty("Connection", "Keep-Alive");
                this.m_httpConn.setConnectTimeout(timeout);
                this.m_httpConn.setReadTimeout(timeout);
                GenieDebug.error("httpUrlConnection_OpenDNS", "requestString = " + requestString);
                OutputStream outputStream = this.m_httpConn.getOutputStream();
                outputStream.write(requestStringBytes);
                outputStream.close();
                try {
                    responseCode = this.m_httpConn.getResponseCode();
                    this.m_RequestInfo.aHttpResponseCode = responseCode;
                    GenieDebug.error("httpUrlConnection_OpenDNS", "responseCode = " + responseCode);
                } catch (Exception e) {
                    GenieDebug.error("httpUrlConnection", "requestString 33 Exception");
                    this.m_RequestInfo.aHttpResponseCode = -2;
                    this.m_RequestInfo.aResponseCode = e.getMessage();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                    GenieRequest.this.sendbroad(this.m_RequestInfo);
                    e.printStackTrace();
                    cancelhttpUrlConnection();
                    return;
                }
            } catch (Exception ex) {
                this.m_RequestInfo.aResponse = ex.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex.printStackTrace();
            } finally {
                cancelhttpUrlConnection();
            }
            if (200 != responseCode) {
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                cancelhttpUrlConnection();
                GenieRequest.this.sendbroad(this.m_RequestInfo);
                return;
            }
            StringBuffer recString = new StringBuffer();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(this.m_httpConn.getInputStream(), Manifest.JAR_ENCODING));
            while (true) {
                String readLine = responseReader.readLine();
                if (readLine == null) {
                    break;
                } else {
                    recString.append(readLine).append("\n");
                }
            }
            responseReader.close();
            GenieDebug.error("httpUrlConnection_OpenDNS", "responseReader+" + recString.toString());
            cancelhttpUrlConnection();
            this.m_RequestInfo.aResponse = recString.toString();
            this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            if (!needParser) {
                GenieRequest.this.sendbroad(this.m_RequestInfo);
            } else {
                parsejson(recString.toString(), OpenDNSType);
                GenieRequest.this.sendbroad(this.m_RequestInfo);
            }
        }

        public void sendRequest2OpenDNS(String aMothed, ArrayList<String> aElement, int timeout, int OpenDNSType, boolean needParser) {
            GenieDebug.error("sendRequest2OpenDNS", "--strart--");
            StringBuffer soapMessage = new StringBuffer();
            GenieDebug.error("sendRequest2OpenDNS", "aElement.size() = " + aElement.size());
            for (int i = 0; i < aElement.size(); i += 2) {
                GenieDebug.error("sendRequest2OpenDNS", "aElement.get(i) = " + aElement.get(i));
                GenieDebug.error("sendRequest2OpenDNS", "aElement.get(i+1) = " + aElement.get(i + 1));
                soapMessage.append(String.format("%s=%s&", aElement.get(i), aElement.get(i + 1)));
            }
            if (soapMessage.length() > 0 && soapMessage.substring(0).endsWith("&")) {
                soapMessage = soapMessage.deleteCharAt(soapMessage.length() - 1);
            }
            httpUrlConnection_OpenDNS(soapMessage.substring(0), timeout, OpenDNSType, needParser);
        }

        public void sendRequest2Router(String aServer, String aMothed, ArrayList<String> aElement, boolean needParser, int timeout, int soaptype) {
            String tmpl;
            String bodyXml;
            GenieDebug.error("debug", "sendRequest2Router needParser  = " + needParser);
            GenieDebug.error("debug", "sendRequest2Router aServer  = " + aServer);
            GenieDebug.error("debug", "sendRequest2Router aMothed  = " + aMothed);
            boolean actionNS = true;
            if (aServer.equals("ParentalControl")) {
                actionNS = false;
            }
            if (actionNS) {
                tmpl = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\r\n<SOAP-ENV:Envelope xmlns:SOAPSDK1=\"http://www.w3.org/2001/XMLSchema\" xmlns:SOAPSDK2=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAPSDK3=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n<SOAP-ENV:Header>\r\n<SessionID>%s</SessionID>\r\n</SOAP-ENV:Header>\r\n<SOAP-ENV:Body>\r\n%s\r\n</SOAP-ENV:Body>\r\n</SOAP-ENV:Envelope>";
            } else {
                tmpl = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n<SOAP-ENV:Header>\r\n<SessionID xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">%s</SessionID>\r\n</SOAP-ENV:Header>\r\n<SOAP-ENV:Body>\r\n%s\r\n</SOAP-ENV:Body>\r\n</SOAP-ENV:Envelope>";
            }
            GenieDebug.error("debug", "sendRequest2Router aElement.size()  = " + aElement.size());
            StringBuffer paramXml = new StringBuffer();
            for (int i = 0; i < aElement.size(); i += 2) {
                String name = aElement.get(i);
                String value = aElement.get(i + 1);
                GenieDebug.error("debug", "sendRequest2Router aElement name  = " + name);
                GenieDebug.error("debug", "sendRequest2Router aElement value  = " + value);
                if (!value.equals("null")) {
                    paramXml.append(String.format("  <%s>%s</%s>\r\n", name, value, name));
                }
            }
            String ns = String.format("urn:NETGEAR-ROUTER:service:%s:1", aServer);
            if (actionNS) {
                bodyXml = paramXml != null ? String.format("<M1:%s xmlns:M1=\"%s\">\r\n%s</M1:%s>", aMothed, ns, paramXml.substring(0), aMothed) : String.format("<M1:%s xmlns:M1=\"%s\">\r\n%s</M1:%s>", aMothed, ns, "", aMothed);
            } else {
                bodyXml = paramXml != null ? String.format("<%s>\r\n%s</%s>", aMothed, paramXml.substring(0), aMothed) : String.format("<%s>\r\n%s</%s>", aMothed, "", aMothed);
            }
            String soapMessage = String.format(tmpl, "A7D88AE69687E58D9A00", bodyXml);
            String soapaction = String.format("\"urn:NETGEAR-ROUTER:service:%s:1#%s\"", aServer, aMothed);
            httpUrlConnection(soapMessage, soapaction, needParser, timeout, soaptype);
        }

        public void CancelRequest() {
            this.m_Cancelfalg = true;
        }

        public void cancelhttpUrlConnection() {
            if (this.m_httpConn != null) {
                this.m_httpConn.disconnect();
            }
            this.m_httpConn = null;
            this.m_url = null;
        }

        private void sendSoapByRemote(String soapAction, String requestString, boolean needParser, int timeout, int soaptype) {
            RequestResult rResult;
            if (this.m_RequestInfo != null) {
                rResult = RemoteApi.syn_remote_sendNotificationMessage(GenieRequest.this.m_context, GenieApplication.token, GenieApplication.remoteDeviceId, requestString, soapAction, timeout, this.m_RequestInfo.aSoapType);
            } else {
                rResult = RemoteApi.syn_remote_sendNotificationMessage(GenieRequest.this.m_context, GenieApplication.token, GenieApplication.remoteDeviceId, requestString, soapAction, timeout, 0);
            }
            if (rResult != null && rResult.getCode() != null && rResult.getCode().equals("200")) {
                String recString = rResult.getResult().substring(rResult.getResult().indexOf("<?xml "), rResult.getResult().length());
                LogUtil.info(GenieRequest.TAG, "sendSoapByRemote", recString);
                String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
                int soapresponsecode = -1;
                if (Code != null) {
                    try {
                        soapresponsecode = Integer.valueOf(Code).intValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                GenieDebug.error("httpUrlConnection", "soapresponsecode+" + soapresponsecode);
                if (soapresponsecode == 401 && soapAction.indexOf("Authenticate") == -1 && soapAction.indexOf("ConfigurationStarted") == -1 && soapAction.indexOf("ConfigurationFinished") == -1) {
                    String ret = SoapApi.loginsoap("admin", GenieRequest.password);
                    RequestResult rResult2 = RemoteApi.syn_remote_sendNotificationMessage(GenieRequest.this.m_context, GenieApplication.token, GenieApplication.remoteDeviceId, ret, SoapParams.Get_Authenticate, timeout, 0);
                    if (rResult2 != null && rResult2.getCode() != null && rResult2.getCode().equals("200")) {
                        String recString1 = rResult2.getResult().substring(rResult2.getResult().indexOf("<?xml "), rResult2.getResult().length());
                        LogUtil.info(GenieRequest.TAG, "sendSoapByRemote", recString1);
                        String Code1 = getXMLText(recString1.toString(), "<ResponseCode>", "</ResponseCode>");
                        int soapresponsecode1 = -1;
                        if (Code1 != null) {
                            try {
                                soapresponsecode1 = Integer.valueOf(Code1).intValue();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        if (soapresponsecode1 == 0) {
                            Authenticate_sendSoapByRemote(soapAction, requestString, needParser, timeout, soaptype);
                            return;
                        }
                    }
                }
                if (soapresponsecode == 0) {
                    this.m_RequestInfo.aResponseCode = Code;
                    if (needParser) {
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        try {
                            parseXml(new ByteArrayInputStream(recString.toString().getBytes()), soaptype);
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                        if (soapAction.indexOf("GetInfo") != -1) {
                            try {
                                getXMLText(recString.toString(), "<Firmwareversion>", "</Firmwareversion>");
                                FileService.SerialNumber = getXMLText(recString.toString(), "<SerialNumber>", "</SerialNumber>");
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                        }
                        LogUtil.debug(GenieRequest.TAG, "onResult", "1");
                        GenieRequest.this.sendbroad(this.m_RequestInfo);
                        cancelhttpUrlConnection();
                        return;
                    }
                    LogUtil.debug(GenieRequest.TAG, "onResult", "2");
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                } else {
                    LogUtil.debug(GenieRequest.TAG, "onResult", "3");
                    if (this.m_RequestInfo.aSoapType == 19) {
                        LogUtil.debug(GenieRequest.TAG, "onResult", "4");
                        GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                    }
                    if (Code != null) {
                        LogUtil.debug(GenieRequest.TAG, "onResult", "5");
                        this.m_RequestInfo.aResponseCode = Code;
                    }
                    LogUtil.debug(GenieRequest.TAG, "onResult", "6");
                    if (this.m_RequestInfo != null && soapAction != null && soapAction.indexOf("ConfigurationStarted") == -1 && soapAction.indexOf("ConfigurationFinished") == -1) {
                        this.m_RequestInfo.aResponse = recString.toString();
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                    }
                }
                cancelhttpUrlConnection();
                LogUtil.debug(GenieRequest.TAG, "onResult", "7");
                GenieRequest.this.sendbroad(this.m_RequestInfo);
                return;
            }
            if (this.m_RequestInfo != null && soapAction != null && soapAction.indexOf("ConfigurationStarted") == -1 && soapAction.indexOf("ConfigurationFinished") == -1) {
                this.m_RequestInfo.aHttpResponseCode = -2;
                this.m_RequestInfo.aResponseCode = "remote soap response status is " + rResult.getCode();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                GenieRequest.this.sendbroad(this.m_RequestInfo);
            }
            cancelhttpUrlConnection();
        }

        private void Authenticate_sendSoapByRemote(String soapAction, String requestString, boolean needParser, int timeout, int soaptype) {
            RequestResult rResult;
            if (this.m_RequestInfo != null) {
                rResult = RemoteApi.syn_remote_sendNotificationMessage(GenieRequest.this.m_context, GenieApplication.token, GenieApplication.remoteDeviceId, requestString, soapAction, timeout, this.m_RequestInfo.aSoapType);
            } else {
                rResult = RemoteApi.syn_remote_sendNotificationMessage(GenieRequest.this.m_context, GenieApplication.token, GenieApplication.remoteDeviceId, requestString, soapAction, timeout, 0);
            }
            if (rResult != null && rResult.getCode() != null && rResult.getCode().equals("200")) {
                String recString = rResult.getResult().substring(rResult.getResult().indexOf("<?xml "), rResult.getResult().length());
                LogUtil.info(GenieRequest.TAG, "sendSoapByRemote", recString);
                String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
                int soapresponsecode = -1;
                if (Code != null) {
                    try {
                        soapresponsecode = Integer.valueOf(Code).intValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                GenieDebug.error("httpUrlConnection", "soapresponsecode+" + soapresponsecode);
                if (soapresponsecode == 0) {
                    this.m_RequestInfo.aResponseCode = Code;
                    if (needParser) {
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        try {
                            parseXml(new ByteArrayInputStream(recString.toString().getBytes()), soaptype);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        if (soapAction.indexOf("GetInfo") != -1) {
                            try {
                                String firmwareVersion = getXMLText(recString.toString(), "<Firmwareversion>", "</Firmwareversion>");
                                GenieSoap.dictionary.put("Firmwareversion", firmwareVersion);
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                        }
                        LogUtil.debug(GenieRequest.TAG, "onResult", "1");
                        GenieRequest.this.sendbroad(this.m_RequestInfo);
                        cancelhttpUrlConnection();
                        return;
                    }
                    LogUtil.debug(GenieRequest.TAG, "onResult", "2");
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                } else {
                    LogUtil.debug(GenieRequest.TAG, "onResult", "3");
                    if (this.m_RequestInfo.aSoapType == 19) {
                        LogUtil.debug(GenieRequest.TAG, "onResult", "4");
                        GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                    }
                    if (Code != null) {
                        LogUtil.debug(GenieRequest.TAG, "onResult", "5");
                        this.m_RequestInfo.aResponseCode = Code;
                    }
                    LogUtil.debug(GenieRequest.TAG, "onResult", "6");
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                }
                cancelhttpUrlConnection();
                LogUtil.debug(GenieRequest.TAG, "onResult", "7");
                GenieRequest.this.sendbroad(this.m_RequestInfo);
                return;
            }
            LogUtil.debug(GenieRequest.TAG, "onResult", "8");
            GenieDebug.error("httpUrlConnection", "requestString 33 Exception");
            this.m_RequestInfo.aHttpResponseCode = -2;
            this.m_RequestInfo.aResponseCode = "remote soap response status is " + rResult.getCode();
            this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
            GenieRequest.this.sendbroad(this.m_RequestInfo);
            cancelhttpUrlConnection();
        }

        private void httpUrlConnection(String requestString, String soapAction, boolean needParser, int timeout, int soaptype) {
            String pathUrl;
            int responseCode;
            if (GenieApplication.isCloud) {
                sendSoapByRemote(soapAction, requestString, needParser, timeout, soaptype);
                return;
            }
            if (AirCard_SoapValue.BatteryOperated) {
                Http_Digest(requestString, soapAction, needParser, timeout, soaptype);
                return;
            }
            try {
                try {
                    cancelhttpUrlConnection();
                    System.setProperty("networkaddress.cache.ttl", "0");
                    System.setProperty("networkaddress.cache.negative.ttl", "0");
                    System.setProperty("http.keepAlive", "false");
                    GenieRequest.this.m_GateWayIp = GenieRequest.this.getGateWay();
                    GenieDebug.error("httpUrlConnection", "--strart-- m_GateWayIp = " + GenieRequest.this.m_GateWayIp);
                    GenieDebug.error("httpUrlConnection", "-httpUrlConnection-m_RequestPort = " + GenieRequest.m_RequestPort);
                    String address = GenieSoap.dictionary.get("");
                    if (address != null && !"".equals(address)) {
                        pathUrl = "http://" + address + ":" + GenieRequest.m_RequestPort + "/soap/server_sa/";
                    } else {
                        pathUrl = "http://routerlogin.net:" + GenieRequest.m_RequestPort + "/soap/server_sa/";
                    }
                    Tools.writelog("GenieRequest---httpUrlConnection--pathurl=>" + pathUrl + "\n", true, GenieRequest.TAG);
                    Tools.writelog("GenieRequest---httpUrlConnection--请求方法=>" + requestString + "\ntimeout " + timeout + " \n", true, GenieRequest.TAG);
                    Tools.writelog("GenieRequest---httpUrlConnection--请求内容=>" + soapAction + "\n", true, GenieRequest.TAG);
                    TestTask.saveLoginInfo("******* Send soap(allen add)*************:" + pathUrl + "\t SoapAction:" + soapAction);
                    GenieDebug.error("httpUrlConnection", "-httpUrlConnection-pathUrl = " + pathUrl);
                    this.m_url = new URL(pathUrl);
                    GenieDebug.error("httpUrlConnection", "-httpUrlConnection-m_url.getHost(); = " + this.m_url.getHost());
                    this.m_httpConn = (HttpURLConnection) this.m_url.openConnection();
                    this.m_httpConn.setDoOutput(true);
                    this.m_httpConn.setDoInput(true);
                    this.m_httpConn.setUseCaches(false);
                    this.m_httpConn.setRequestMethod("POST");
                    GenieDebug.error("httpUrlConnection", "-httpUrlConnection-soapAction = " + soapAction);
                    byte[] requestStringBytes = requestString.getBytes(Manifest.JAR_ENCODING);
                    this.m_httpConn.setRequestProperty("Accept", WFM_NanoHTTPD.MIME_XML);
                    this.m_httpConn.setRequestProperty("SOAPAction", soapAction);
                    this.m_httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                    this.m_httpConn.setRequestProperty("Cache-Control", "no-cache");
                    this.m_httpConn.setRequestProperty("Pragma", "no-cache");
                    this.m_httpConn.setRequestProperty("Accept-Encoding", "gzip");
                    this.m_httpConn.setRequestProperty("Accept-Language", "zh-CN,en,*");
                    this.m_httpConn.setRequestProperty("Content-type", "multipart/form-data");
                    this.m_httpConn.setRequestProperty("Content-Length", Integer.toString(requestStringBytes.length));
                    this.m_httpConn.setConnectTimeout(timeout);
                    this.m_httpConn.setReadTimeout(timeout);
                    this.m_httpConn.connect();
                    GenieDebug.error("httpUrlConnection", "soapAction = " + soapAction);
                    GenieDebug.error("httpUrlConnection", "requestString = " + requestString);
                    OutputStream outputStream = this.m_httpConn.getOutputStream();
                    GenieDebug.error("httpUrlConnection", "requestString 11 ");
                    outputStream.write(requestStringBytes);
                    GenieDebug.error("httpUrlConnection", "requestString 22 ");
                    outputStream.close();
                    GenieDebug.error("httpUrlConnection", "requestString 33 ");
                    try {
                        responseCode = this.m_httpConn.getResponseCode();
                        Tools.writelog("GenieRequest---httpUrlConnection--code=>" + responseCode + "\n", true, GenieRequest.TAG);
                        GenieDebug.error("httpUrlConnection", "requestString 44 ");
                        GenieDebug.error("httpUrlConnection", "responseCode = " + responseCode);
                        this.m_RequestInfo.aHttpResponseCode = responseCode;
                    } catch (Exception e) {
                        if (!httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                            doModifyPortal(soaptype, "time out");
                            if (!httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                                this.m_RequestInfo.aHttpResponseCode = -2;
                                this.m_RequestInfo.aResponseCode = e.getMessage();
                                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                                GenieRequest.this.sendbroad(this.m_RequestInfo);
                                e.printStackTrace();
                                cancelhttpUrlConnection();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                } catch (Exception ex) {
                    Tools.writelog("GenieRequest---httpUrlConnection- exception -请求内容=>" + ex.getMessage() + "\n", true, GenieRequest.TAG);
                    doModifyPortal(soaptype, ex.getMessage());
                    httpUrlConnection_agin(requestString, soapAction, "routerlogin.net", needParser, timeout, soaptype);
                }
            } catch (ConnectException ex2) {
                Tools.writelog("GenieRequest---httpUrlConnection- exception -请求内容=>" + ex2.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex2.getMessage());
                httpUrlConnection_agin(requestString, soapAction, "routerlogin.net", needParser, timeout, soaptype);
            } catch (SocketException ex3) {
                Tools.writelog("GenieRequest---httpUrlConnection- exception -请求内容=>" + ex3.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex3.getMessage());
                httpUrlConnection_agin(requestString, soapAction, "routerlogin.net", needParser, timeout, soaptype);
            } catch (UnknownHostException ex4) {
                Tools.writelog("GenieRequest---httpUrlConnection- exception -请求内容=>" + ex4.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex4.getMessage());
                httpUrlConnection_agin(requestString, soapAction, "", needParser, timeout, soaptype);
            }
            if (200 == responseCode) {
                StringBuffer recString = new StringBuffer();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(this.m_httpConn.getInputStream(), Manifest.JAR_ENCODING));
                while (true) {
                    String readLine = responseReader.readLine();
                    if (readLine == null) {
                        break;
                    } else {
                        recString.append(readLine).append("\n");
                    }
                }
                responseReader.close();
                cancelhttpUrlConnection();
                GenieDebug.error("httpUrlConnection", "responseReader+" + recString.toString());
                GenieDebug.error("httpUrlConnection", "needParser+" + needParser);
                Tools.writelog("GenieRequest---httpUrlConnection---方法--" + soapAction + "----请求结果=>" + recString.toString() + "\n", true, GenieRequest.TAG);
                String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
                int soapresponsecode = -1;
                if (Code != null) {
                    try {
                        soapresponsecode = Integer.valueOf(Code).intValue();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                GenieDebug.error("httpUrlConnection", "soapresponsecode+" + soapresponsecode);
                if (401 == soapresponsecode && soapAction.indexOf("Authenticate") == -1 && !"".equals(GenieRequest.password) && soapAction.indexOf("ConfigurationStarted") != -1 && soapAction.indexOf("ConfigurationFinished") != -1) {
                    String ret = Authenticate_httpUrlConnection(requestString, soapAction, pathUrl, timeout);
                    recString = new StringBuffer();
                    recString.append(ret);
                }
                if (soapresponsecode == 0) {
                    this.m_RequestInfo.aResponseCode = Code;
                    if (needParser) {
                        this.m_RequestInfo.aResponse = recString.toString();
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        String xmlStr = recString.toString();
                        parseXml(new ByteArrayInputStream(xmlStr.replace("<unknown>", EnvironmentCompat.MEDIA_UNKNOWN).getBytes()), soaptype);
                        if (soapAction.indexOf("GetInfo") != -1) {
                            try {
                                String firmwareVersion = getXMLText(recString.toString(), "<Firmwareversion>", "</Firmwareversion>");
                                GenieSoap.dictionary.put("Firmwareversion", firmwareVersion);
                            } catch (Exception e3) {
                            }
                        }
                        GenieRequest.this.sendbroad(this.m_RequestInfo);
                        cancelhttpUrlConnection();
                        return;
                    }
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                } else if (!httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                    if (this.m_RequestInfo.aSoapType == 19) {
                        GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                    }
                    if (Code != null) {
                        this.m_RequestInfo.aResponseCode = Code;
                    }
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                } else {
                    return;
                }
                cancelhttpUrlConnection();
                GenieRequest.this.sendbroad(this.m_RequestInfo);
                return;
            }
            if (!httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                doModifyPortal(soaptype, "time out");
                if (!httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                }
            }
            cancelhttpUrlConnection();
            GenieRequest.this.sendbroad(this.m_RequestInfo);
        }

        private void httpUrlConnection_agin(String requestString, String soapAction, String address, boolean needParser, int timeout, int soaptype) {
            String pathUrl;
            if (GenieApplication.isCloud) {
                sendSoapByRemote(soapAction, requestString, needParser, timeout, soaptype);
                return;
            }
            if (AirCard_SoapValue.BatteryOperated) {
                Http_Digest(requestString, soapAction, needParser, timeout, soaptype);
                return;
            }
            try {
                cancelhttpUrlConnection();
                System.setProperty("networkaddress.cache.ttl", "0");
                System.setProperty("networkaddress.cache.negative.ttl", "0");
                System.setProperty("http.keepAlive", "false");
                if ("".equals(address)) {
                    address = GenieRequest.this.getGateWay();
                }
                pathUrl = "http://" + address + ":" + GenieRequest.m_RequestPort + "/soap/server_sa/";
                Tools.writelog("GenieRequest---httpUrlConnection- agin -pathurl=>" + pathUrl + "\n", true, GenieRequest.TAG);
                Tools.writelog("GenieRequest---httpUrlConnection- agin -请求方法=>" + requestString + "\ntimeout " + timeout + " \n", true, GenieRequest.TAG);
                Tools.writelog("GenieRequest---httpUrlConnection- agin -请求内容=>" + soapAction + "\n", true, GenieRequest.TAG);
                this.m_url = new URL(pathUrl);
                this.m_httpConn = (HttpURLConnection) this.m_url.openConnection();
                this.m_httpConn.setDoOutput(true);
                this.m_httpConn.setDoInput(true);
                this.m_httpConn.setUseCaches(false);
                this.m_httpConn.setRequestMethod("POST");
                byte[] requestStringBytes = requestString.getBytes(Manifest.JAR_ENCODING);
                this.m_httpConn.setRequestProperty("Accept", WFM_NanoHTTPD.MIME_XML);
                this.m_httpConn.setRequestProperty("SOAPAction", soapAction);
                this.m_httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                this.m_httpConn.setRequestProperty("Cache-Control", "no-cache");
                this.m_httpConn.setRequestProperty("Pragma", "no-cache");
                this.m_httpConn.setRequestProperty("Accept-Encoding", "gzip");
                this.m_httpConn.setRequestProperty("Accept-Language", "zh-CN,en,*");
                this.m_httpConn.setRequestProperty("Content-type", "multipart/form-data");
                this.m_httpConn.setRequestProperty("Content-Length", Integer.toString(requestStringBytes.length));
                this.m_httpConn.setConnectTimeout(timeout);
                this.m_httpConn.setReadTimeout(timeout);
                this.m_httpConn.connect();
                OutputStream outputStream = this.m_httpConn.getOutputStream();
                outputStream.write(requestStringBytes);
                outputStream.close();
            } catch (UnknownHostException ex) {
                Tools.writelog("GenieRequest---httpUrlConnection-- UnknownHostException  " + ex.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex.getMessage());
                if (httpclient_agin(requestString, soapAction, "", needParser, timeout, soaptype)) {
                    return;
                }
                this.m_RequestInfo.aResponse = ex.getMessage();
                doModifyPortal(soaptype, ex.getMessage());
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex.printStackTrace();
                cancelhttpUrlConnection();
                cancelhttpUrlConnection();
            } catch (ConnectException ex2) {
                Tools.writelog("GenieRequest---httpUrlConnection-- ConnectException  " + ex2.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex2.getMessage());
                if (httpclient_agin(requestString, soapAction, "routerlogin.net", needParser, timeout, soaptype)) {
                    return;
                }
                this.m_RequestInfo.aResponse = ex2.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex2.printStackTrace();
                cancelhttpUrlConnection();
                cancelhttpUrlConnection();
            } catch (Exception ex3) {
                Tools.writelog("GenieRequest---httpUrlConnection-- Exception  " + ex3.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex3.getMessage());
                if (httpclient_agin(requestString, soapAction, "routerlogin.net", needParser, timeout, soaptype)) {
                    return;
                }
                GenieDebug.error("debug", "http Exception ex");
                this.m_RequestInfo.aResponse = ex3.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex3.printStackTrace();
                cancelhttpUrlConnection();
                cancelhttpUrlConnection();
            } catch (SocketException ex4) {
                Tools.writelog("GenieRequest---httpUrlConnection-- SocketException  " + ex4.getMessage() + "\n", true, GenieRequest.TAG);
                doModifyPortal(soaptype, ex4.getMessage());
                if (httpclient_agin(requestString, soapAction, "routerlogin.net", needParser, timeout, soaptype)) {
                    return;
                }
                this.m_RequestInfo.aResponse = ex4.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex4.printStackTrace();
                cancelhttpUrlConnection();
                cancelhttpUrlConnection();
            } finally {
                cancelhttpUrlConnection();
            }
            try {
                int responseCode = this.m_httpConn.getResponseCode();
                Tools.writelog("GenieRequest---httpUrlConnection- agin -code=>" + responseCode + "\n", true, GenieRequest.TAG);
                this.m_RequestInfo.aHttpResponseCode = responseCode;
                if (200 != responseCode) {
                    doModifyPortal(soaptype, "time out");
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                    cancelhttpUrlConnection();
                    GenieRequest.this.sendbroad(this.m_RequestInfo);
                    return;
                }
                StringBuffer recString = new StringBuffer();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(this.m_httpConn.getInputStream(), Manifest.JAR_ENCODING));
                while (true) {
                    String readLine = responseReader.readLine();
                    if (readLine == null) {
                        break;
                    } else {
                        recString.append(readLine).append("\n");
                    }
                }
                responseReader.close();
                cancelhttpUrlConnection();
                Tools.writelog("GenieRequest---httpUrlConnection- agin --方法--" + soapAction + "---请求结果=>" + recString.toString() + "\n", true, GenieRequest.TAG);
                String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
                int soapresponsecode = -1;
                if (Code != null) {
                    try {
                        soapresponsecode = Integer.valueOf(Code).intValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (401 == soapresponsecode && soapAction.indexOf("Authenticate") == -1 && !"".equals(GenieRequest.password)) {
                    String ret = Authenticate_httpUrlConnection(requestString, soapAction, pathUrl, timeout);
                    recString = new StringBuffer();
                    recString.append(ret);
                }
                if (soapresponsecode == 0) {
                    this.m_RequestInfo.aResponseCode = Code;
                    if (needParser) {
                        this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        String xmlStr = recString.toString();
                        parseXml(new ByteArrayInputStream(xmlStr.replace("<unknown>", EnvironmentCompat.MEDIA_UNKNOWN).getBytes()), soaptype);
                        if (soapAction.indexOf("GetInfo") != -1) {
                            try {
                                String firmwareVersion = getXMLText(recString.toString(), "<Firmwareversion>", "</Firmwareversion>");
                                GenieSoap.dictionary.put("Firmwareversion", firmwareVersion);
                            } catch (Exception e2) {
                            }
                        }
                        GenieRequest.this.sendbroad(this.m_RequestInfo);
                        cancelhttpUrlConnection();
                        return;
                    }
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                } else {
                    if (httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                        return;
                    }
                    if (this.m_RequestInfo.aSoapType == 19) {
                        GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                    }
                    if (Code != null) {
                        this.m_RequestInfo.aResponseCode = Code;
                    }
                    this.m_RequestInfo.aResponse = recString.toString();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                }
                cancelhttpUrlConnection();
                GenieRequest.this.sendbroad(this.m_RequestInfo);
            } catch (Exception e3) {
                if (!httpclient(requestString, soapAction, needParser, timeout, soaptype)) {
                    doModifyPortal(soaptype, "time out");
                    this.m_RequestInfo.aHttpResponseCode = -2;
                    this.m_RequestInfo.aResponseCode = e3.getMessage();
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                    GenieRequest.this.sendbroad(this.m_RequestInfo);
                    e3.printStackTrace();
                    cancelhttpUrlConnection();
                }
            }
        }

        private boolean Authenticate(String url, int timeout) {
            String ret = SoapUtil.createInstance().Authenticate_postSoap(SoapApi.loginsoap("admin", GenieRequest.password), SoapParams.Get_Authenticate, url, timeout);
            int return_code = SoapParser.getResponseCode(ret);
            return return_code == 0;
        }

        private String Authenticate_httpUrlConnection(String requestString, String soapAction, String url, int timeout) {
            HttpURLConnection m_httpConn1 = null;
            try {
                Tools.writelog("restart Authenticate \n", true, GenieRequest.TAG);
                if (Authenticate(url, timeout)) {
                    GenieDebug.system_string("lh_plc_soap", "httpUrlConnection--方法", soapAction);
                    GenieDebug.system_string("lh_plc_soap", "httpUrlConnection--内容", requestString);
                    cancelhttpUrlConnection();
                    System.setProperty("networkaddress.cache.ttl", "0");
                    System.setProperty("networkaddress.cache.negative.ttl", "0");
                    System.setProperty("http.keepAlive", "false");
                    GenieSoap.dictionary.get("");
                    URL m_url1 = new URL(url);
                    try {
                        try {
                            m_httpConn1 = (HttpURLConnection) m_url1.openConnection();
                            m_httpConn1.setDoOutput(true);
                            m_httpConn1.setDoInput(true);
                            m_httpConn1.setUseCaches(false);
                            m_httpConn1.setRequestMethod("POST");
                            byte[] requestStringBytes = requestString.getBytes(Manifest.JAR_ENCODING);
                            m_httpConn1.setRequestProperty("Accept", WFM_NanoHTTPD.MIME_XML);
                            m_httpConn1.setRequestProperty("SOAPAction", soapAction);
                            m_httpConn1.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                            m_httpConn1.setRequestProperty("Cache-Control", "no-cache");
                            m_httpConn1.setRequestProperty("Pragma", "no-cache");
                            m_httpConn1.setRequestProperty("Accept-Encoding", "gzip");
                            m_httpConn1.setRequestProperty("Accept-Language", "zh-CN,en,*");
                            m_httpConn1.setRequestProperty("Content-type", "multipart/form-data");
                            m_httpConn1.setRequestProperty("Content-Length", Integer.toString(requestStringBytes.length));
                            m_httpConn1.setConnectTimeout(timeout);
                            m_httpConn1.setReadTimeout(timeout);
                            m_httpConn1.connect();
                            OutputStream outputStream = m_httpConn1.getOutputStream();
                            outputStream.write(requestStringBytes);
                            outputStream.close();
                            try {
                                int responseCode = m_httpConn1.getResponseCode();
                                if (200 == responseCode) {
                                    StringBuffer recString = new StringBuffer();
                                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(m_httpConn1.getInputStream(), Manifest.JAR_ENCODING));
                                    while (true) {
                                        String readLine = responseReader.readLine();
                                        if (readLine == null) {
                                            break;
                                        }
                                        recString.append(readLine).append("\n");
                                    }
                                    responseReader.close();
                                    cancelhttpUrlConnection();
                                    Tools.writelog("restart Authenticate success context   " + recString.toString() + " \n", true, GenieRequest.TAG);
                                    String stringBuffer = recString.toString();
                                    if (m_httpConn1 != null) {
                                        try {
                                            m_httpConn1.disconnect();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return stringBuffer;
                                }
                                Tools.writelog("restart Authenticate fail responseCode" + responseCode, true, GenieRequest.TAG);
                            } catch (Exception e2) {
                                Tools.writelog("restart Authenticate fail responseCode-2", true, GenieRequest.TAG);
                                if (m_httpConn1 != null) {
                                    try {
                                        m_httpConn1.disconnect();
                                    } catch (Exception e3) {
                                        e3.printStackTrace();
                                    }
                                }
                                return "";
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (m_httpConn1 != null) {
                                try {
                                    m_httpConn1.disconnect();
                                } catch (Exception e4) {
                                    e4.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e5) {
                        if (m_httpConn1 != null) {
                            try {
                                m_httpConn1.disconnect();
                            } catch (Exception e6) {
                                e6.printStackTrace();
                            }
                        }
                        return "";
                    }
                }
                Tools.writelog("restart Authenticate fail \n", true, GenieRequest.TAG);
                if (m_httpConn1 != null) {
                    try {
                        m_httpConn1.disconnect();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                return "";
            } catch (Exception e8) {
            } catch (Throwable th2) {
                th = th2;
            }
        }

        public boolean httpclient_agin(String requestString, String soapAction, String address, boolean needParser, int timeout, int soaptype) {
            HttpResponse response;
            int code;
            if ("".equals(address)) {
                address = GenieRequest.this.getGateWay();
            }
            String pathUrl = "http://" + address + ":" + GenieRequest.m_RequestPort + "/soap/server_sa/";
            Tools.writelog("GenieRequest---agin----httpclient--请求开始=>\n", true, GenieRequest.TAG);
            RequestResult rResult = new RequestResult();
            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout);
            HttpConnectionParams.setSoTimeout(params, timeout);
            HttpPost request = new HttpPost(pathUrl);
            request.addHeader("Accept", WFM_NanoHTTPD.MIME_XML);
            request.addHeader("SOAPAction", soapAction);
            request.addHeader("Cache-Control", "no-cache");
            request.addHeader("Pragma", "no-cache");
            request.addHeader("Accept-Encoding", "gzip");
            request.addHeader("Accept-Language", "zh-CN,en,*");
            request.addHeader("Content-type", "multipart/form-data");
            try {
                StringEntity entity = new StringEntity(requestString.toString());
                request.setEntity(entity);
                response = httpclient.execute(request);
                code = response.getStatusLine().getStatusCode();
                Tools.writelog("GenieRequest---agin----httpclient--code=>" + code + "\n", true, GenieRequest.TAG);
            } catch (Exception e) {
                e.printStackTrace();
                rResult.setException(e.getMessage());
            } finally {
                Tools.writelog("GenieRequest---agin----httpclient--结果=>\n", true, GenieRequest.TAG);
                httpclient.getConnectionManager().shutdown();
            }
            if (code != 200) {
                String xErrorMessage = "";
                Header[] headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].getName().equals("X-Error-Code")) {
                        headers[i].getValue();
                    }
                    if (headers[i].getName().equals("X-Error-Message")) {
                        xErrorMessage = headers[i].getValue();
                    }
                }
                Tools.writelog("GenieRequest---agin----httpclient--code=>" + code + "   xErrorMessage  " + xErrorMessage + "\n", true, GenieRequest.TAG);
                return false;
            }
            HttpEntity entityResult = response.getEntity();
            String result = EntityUtils.toString(entityResult);
            Tools.writelog("GenieRequest---httpclient--again---" + soapAction + "------结果=>" + code + "\n", true, GenieRequest.TAG);
            rResult.setData(String.valueOf(code), result);
            if (rResult == null || rResult.getCode() == null || !rResult.getCode().equals("200")) {
                Tools.writelog("GenieRequest---agin----httpclient--结果=>\n", true, GenieRequest.TAG);
                httpclient.getConnectionManager().shutdown();
                return false;
            }
            String recString = rResult.getResult().substring(rResult.getResult().indexOf("<?xml "), rResult.getResult().length());
            String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
            int soapresponsecode = -1;
            if (Code != null) {
                try {
                    soapresponsecode = Integer.valueOf(Code).intValue();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (soapresponsecode == 0) {
                this.m_RequestInfo.aResponseCode = Code;
                if (needParser) {
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    String xmlStr = recString.toString();
                    parseXml(new ByteArrayInputStream(xmlStr.replace("<unknown>", EnvironmentCompat.MEDIA_UNKNOWN).getBytes()), soaptype);
                    if (soapAction.indexOf("GetInfo") != -1) {
                        try {
                            String firmwareVersion = getXMLText(recString.toString(), "<Firmwareversion>", "</Firmwareversion>");
                            GenieSoap.dictionary.put("Firmwareversion", firmwareVersion);
                        } catch (Exception e3) {
                        }
                    }
                    GenieRequest.this.sendbroad(this.m_RequestInfo);
                    Tools.writelog("GenieRequest---agin----httpclient--结果=>" + recString + "\n", true, GenieRequest.TAG);
                    httpclient.getConnectionManager().shutdown();
                    return true;
                }
                this.m_RequestInfo.aResponse = recString.toString();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            } else {
                if (this.m_RequestInfo.aSoapType == 19) {
                    GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                }
                if (Code != null) {
                    this.m_RequestInfo.aResponseCode = Code;
                }
                this.m_RequestInfo.aResponse = recString.toString();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            }
            cancelhttpUrlConnection();
            GenieRequest.this.sendbroad(this.m_RequestInfo);
            Tools.writelog("GenieRequest---agin----httpclient--结果=>" + recString + "\n", true, GenieRequest.TAG);
            httpclient.getConnectionManager().shutdown();
            return true;
        }

        public boolean httpclient(String requestString, String soapAction, boolean needParser, int timeout, int soaptype) {
            HttpResponse response;
            int code;
            String address = GenieSoap.dictionary.get("");
            String pathUrl = (address == null || "".equals(address)) ? "http://routerlogin.net:" + GenieRequest.m_RequestPort + "/soap/server_sa/" : "http://" + address + ":" + GenieRequest.m_RequestPort + "/soap/server_sa/";
            Tools.writelog("GenieRequest---httpclient--请求开始=>\n", true, GenieRequest.TAG);
            RequestResult rResult = new RequestResult();
            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout);
            HttpConnectionParams.setSoTimeout(params, timeout);
            HttpPost request = new HttpPost(pathUrl);
            request.addHeader("Accept", WFM_NanoHTTPD.MIME_XML);
            request.addHeader("SOAPAction", soapAction);
            request.addHeader("Cache-Control", "no-cache");
            request.addHeader("Pragma", "no-cache");
            request.addHeader("Accept-Encoding", "gzip");
            request.addHeader("Accept-Language", "zh-CN,en,*");
            request.addHeader("Content-type", "multipart/form-data");
            try {
                StringEntity entity = new StringEntity(requestString.toString());
                request.setEntity(entity);
                response = httpclient.execute(request);
                code = response.getStatusLine().getStatusCode();
                Tools.writelog("GenieRequest---httpclient--code=>" + code + "\n", true, GenieRequest.TAG);
            } catch (Exception e) {
                e.printStackTrace();
                rResult.setException(e.getMessage());
            } finally {
                Tools.writelog("GenieRequest---httpclient--结果=>\n", true, GenieRequest.TAG);
                httpclient.getConnectionManager().shutdown();
            }
            if (code != 200) {
                String xErrorMessage = "";
                Header[] headers = response.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].getName().equals("X-Error-Code")) {
                        headers[i].getValue();
                    }
                    if (headers[i].getName().equals("X-Error-Message")) {
                        xErrorMessage = headers[i].getValue();
                    }
                }
                Tools.writelog("GenieRequest---agin----httpclient--code=>" + code + "   xErrorMessage  " + xErrorMessage + "\n", true, GenieRequest.TAG);
                return false;
            }
            HttpEntity entityResult = response.getEntity();
            String result = EntityUtils.toString(entityResult);
            Tools.writelog("GenieRequest---httpclient-----" + soapAction + "------结果=>" + code + "\n", true, GenieRequest.TAG);
            rResult.setData(String.valueOf(code), result);
            if (rResult == null || rResult.getCode() == null || !rResult.getCode().equals("200")) {
                Tools.writelog("GenieRequest---httpclient--结果=>\n", true, GenieRequest.TAG);
                httpclient.getConnectionManager().shutdown();
                return false;
            }
            String recString = rResult.getResult().substring(rResult.getResult().indexOf("<?xml "), rResult.getResult().length());
            String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
            int soapresponsecode = -1;
            if (Code != null) {
                try {
                    soapresponsecode = Integer.valueOf(Code).intValue();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            GenieDebug.error("httpUrlConnection", "soapresponsecode+" + soapresponsecode);
            if (soapresponsecode == 0) {
                this.m_RequestInfo.aResponseCode = Code;
                if (needParser) {
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    String xmlStr = recString.toString();
                    parseXml(new ByteArrayInputStream(xmlStr.replace("<unknown>", EnvironmentCompat.MEDIA_UNKNOWN).getBytes()), soaptype);
                    if (soapAction.indexOf("GetInfo") != -1) {
                        try {
                            String firmwareVersion = getXMLText(recString.toString(), "<Firmwareversion>", "</Firmwareversion>");
                            GenieSoap.dictionary.put("Firmwareversion", firmwareVersion);
                        } catch (Exception e3) {
                        }
                    }
                    GenieRequest.this.sendbroad(this.m_RequestInfo);
                    Tools.writelog("GenieRequest---httpclient--结果=>" + recString + "\n", true, GenieRequest.TAG);
                    httpclient.getConnectionManager().shutdown();
                    return true;
                }
                this.m_RequestInfo.aResponse = recString.toString();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            } else {
                if (this.m_RequestInfo.aSoapType == 19) {
                    GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                }
                if (Code != null) {
                    this.m_RequestInfo.aResponseCode = Code;
                }
                this.m_RequestInfo.aResponse = recString.toString();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            }
            cancelhttpUrlConnection();
            GenieRequest.this.sendbroad(this.m_RequestInfo);
            Tools.writelog("GenieRequest---httpclient--结果=>" + recString + "\n", true, GenieRequest.TAG);
            httpclient.getConnectionManager().shutdown();
            return true;
        }

        private void Http_Digest(String requestString, String soapAction, boolean needParser, int timeout, int soaptype) {
            HttpResponse response;
            int code;
            try {
                String address = GenieSoap.dictionary.get("");
                String urlStr = (address == null || "".equals(address)) ? "http://routerlogin.net:" + GenieRequest.m_RequestPort + "/soap/server_sa/" : "http://" + address + ":" + GenieRequest.m_RequestPort + "/soap/server_sa/";
                URL url = new URL(urlStr);
                UsernamePasswordCredentials upc = new UsernamePasswordCredentials("admin", GenieRequest.password);
                String strHost = url.getHost();
                int iPort = url.getPort();
                AuthScope as = new AuthScope(strHost, iPort, AuthScope.ANY_REALM);
                BasicCredentialsProvider bcp = new BasicCredentialsProvider();
                bcp.setCredentials(as, upc);
                DefaultHttpClient client = new DefaultHttpClient();
                client.setCredentialsProvider(bcp);
                HttpPost hg = new HttpPost(urlStr);
                hg.addHeader("soapaction", soapAction);
                hg.addHeader("Content-Type", "application/soap+xml; charset=utf-8");
                StringEntity entity = new StringEntity(requestString.toString());
                hg.setEntity(entity);
                response = client.execute(hg);
                code = response.getStatusLine().getStatusCode();
            } catch (SocketException ex) {
                doModifyPortal(soaptype, ex.getMessage());
                this.m_RequestInfo.aResponse = ex.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex.printStackTrace();
                cancelhttpUrlConnection();
            } catch (Exception ex2) {
                doModifyPortal(soaptype, ex2.getMessage());
                GenieDebug.error("debug", "http Exception ex");
                this.m_RequestInfo.aResponse = ex2.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex2.printStackTrace();
                cancelhttpUrlConnection();
            } catch (ConnectException ex3) {
                doModifyPortal(soaptype, ex3.getMessage());
                this.m_RequestInfo.aResponse = ex3.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex3.printStackTrace();
            } catch (UnknownHostException ex4) {
                doModifyPortal(soaptype, ex4.getMessage());
                this.m_RequestInfo.aResponse = ex4.getMessage();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                ex4.printStackTrace();
                cancelhttpUrlConnection();
            } finally {
                cancelhttpUrlConnection();
            }
            if (200 != code) {
                doModifyPortal(soaptype, "time out");
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                cancelhttpUrlConnection();
                cancelhttpUrlConnection();
                GenieRequest.this.sendbroad(this.m_RequestInfo);
                return;
            }
            StringBuffer recString = new StringBuffer();
            HttpEntity entityResult = response.getEntity();
            String result = EntityUtils.toString(entityResult);
            recString.append(result);
            GenieDebug.error("httpUrlConnection", "responseReader+" + recString.toString());
            GenieDebug.error("httpUrlConnection", "needParser+" + needParser);
            String Code = getXMLText(recString.toString(), "<ResponseCode>", "</ResponseCode>");
            int soapresponsecode = -1;
            if (Code != null) {
                try {
                    soapresponsecode = Integer.valueOf(Code).intValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            GenieDebug.error("httpUrlConnection", "soapresponsecode+" + soapresponsecode);
            if (soapresponsecode == 0) {
                this.m_RequestInfo.aResponseCode = Code;
                if (needParser) {
                    this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    String xmlStr = recString.toString();
                    parseXml(new ByteArrayInputStream(xmlStr.replace("<unknown>", EnvironmentCompat.MEDIA_UNKNOWN).getBytes()), soaptype);
                    if (soapAction.indexOf("GetInfo") != -1) {
                        try {
                            String firmwareVersion = recString.toString().substring(recString.toString().indexOf("<Firmwareversion>") + 11, recString.toString().indexOf("</Firmwareversion>"));
                            GenieSoap.dictionary.put("Firmwareversion", firmwareVersion);
                        } catch (Exception e2) {
                        }
                    }
                    GenieRequest.this.sendbroad(this.m_RequestInfo);
                    cancelhttpUrlConnection();
                    return;
                }
                this.m_RequestInfo.aResponse = recString.toString();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            } else {
                if (this.m_RequestInfo.aSoapType == 19) {
                    GenieSoap.dictionary.put("NewBlockDeviceEnable", "N/A");
                }
                if (Code != null) {
                    this.m_RequestInfo.aResponseCode = Code;
                }
                this.m_RequestInfo.aResponse = recString.toString();
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            }
            cancelhttpUrlConnection();
            GenieRequest.this.sendbroad(this.m_RequestInfo);
        }

        private void doModifyPortal(int soapType, String message) {
            System.out.println("lh---返回----" + message);
            if (soapType == 1 && message != null) {
                if (message.indexOf("time out") != -1 || message.indexOf("timed out") != -1 || message.indexOf("failed to connect") != -1 || message.indexOf("Connection refused") != -1 || message.indexOf("ECONNREFUSED") != -1) {
                    GenieRequest.m_RequestPort = GenieRequest.m_RequestPort == 80 ? 5000 : 80;
                    System.out.println("lh----切换---" + GenieRequest.m_RequestPort);
                    Tools.writelog("GenieRequest---httpUrlConnection--切换端口=>" + GenieRequest.m_RequestPort + "\n", true, GenieRequest.TAG);
                }
            }
        }

        public String getXMLText(String source, String tagS, String tagE) {
            return SoapParser.getXMLText(source, tagS, tagE);
        }

        public void parseXml(InputStream paseText, int soaptype) throws Exception {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(paseText, "utf-8");
                int type = parser.getEventType();
                GenieDebug.error("parseXml", "parseXml type = " + type);
                while (type != 1) {
                    GenieDebug.error("parseXml", "while type = " + type);
                    GenieDebug.error("parseXml", "while parser.getName() = " + parser.getName());
                    switch (type) {
                        case 2:
                            this.recordResults = 0;
                            isParseTextTag(parser.getName(), soaptype);
                            break;
                        case 4:
                            String value = parser.getText();
                            if (value != null) {
                                value = GenieRequest.this.escapeString(value);
                            }
                            tackleXmlText(value, soaptype);
                            break;
                    }
                    type = parser.next();
                }
            } catch (Error e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                paseText.close();
                endParseXml(soaptype);
            }
        }

        public void endParseXml(int soaptype) {
            GenieDebug.error("endParseXml", "endParseXml soapType = " + this.m_RequestInfo.aSoapType);
            switch (soaptype) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                default:
                    return;
            }
        }

        public void isParseTextTag(String elementName, int soaptype) {
            GenieDebug.error("isParseTextTag", "elementName = " + elementName);
            GenieDebug.error("isParseTextTag", "soapType = " + this.m_RequestInfo.aSoapType);
            switch (soaptype) {
                case 0:
                    return;
                case 1:
                    if (elementName.equals("ResponseCode")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 2:
                    if (elementName.equals("ModelName")) {
                        this.elementIndex = 100;
                        this.recordResults = 1;
                        return;
                    } else {
                        if (elementName.equals("Firmwareversion")) {
                            this.elementIndex = 101;
                            this.recordResults = 1;
                            return;
                        }
                        return;
                    }
                case 3:
                    if (elementName.equals("NewEnable")) {
                        this.elementIndex = 110;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewSSID")) {
                        this.elementIndex = 111;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewChannel")) {
                        this.elementIndex = 112;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewRegion")) {
                        this.elementIndex = 113;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewWirelessMode")) {
                        this.elementIndex = 114;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewWPAEncryptionModes")) {
                        this.elementIndex = 115;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewWLANMACAddress")) {
                        this.elementIndex = 116;
                        this.recordResults = 1;
                        return;
                    } else if (elementName.equals("NewStatus")) {
                        this.elementIndex = 117;
                        this.recordResults = 1;
                        return;
                    } else {
                        if (elementName.equals("NewBasicEncryptionModes")) {
                            this.elementIndex = 118;
                            this.recordResults = 1;
                            return;
                        }
                        return;
                    }
                case 4:
                    if (elementName.equals("NewWPAPassphrase")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 5:
                    if (elementName.equals("NewGuestAccessEnabled")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 6:
                    GenieDebug.error("GUEST_INFO_SSID", " 99999 parse ESoapRequestGuestInfo elementName = " + elementName);
                    if (elementName.equals("NewSSID")) {
                        this.elementIndex = 120;
                        this.recordResults = 1;
                    } else if (elementName.equals("NewSecurityMode")) {
                        this.elementIndex = 121;
                        this.recordResults = 1;
                    } else if (elementName.equals("NewKey")) {
                        this.elementIndex = 122;
                        this.recordResults = 1;
                    }
                    GenieDebug.error("GUEST_INFO_SSID", " 99999 parse ESoapRequestGuestInfo elementIndex = " + this.elementIndex);
                    GenieDebug.error("GUEST_INFO_SSID", " 99999 parse ESoapRequestGuestInfo recordResults = " + this.recordResults);
                    return;
                case 7:
                    GenieDebug.error("isParseTextTag", "elementName = " + elementName);
                    if (elementName.equals("NewAttachDevice") || elementName.equals("AttachDevice")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 8:
                    if (elementName.equals("NewTrafficMeterEnable")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 9:
                    if (elementName.indexOf("NewToday") >= 0 || elementName.indexOf("NewYesterday") >= 0 || elementName.indexOf("NewWeek") >= 0 || elementName.indexOf("NewMonth") >= 0 || elementName.indexOf("NewLastMonth") >= 0) {
                        GenieDebug.error("debug", "99899 ESoapRequestTrafficMeter elementName=" + elementName);
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 10:
                    if (elementName.equals("NewControlOption")) {
                        this.elementIndex = 130;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("NewMonthlyLimit")) {
                        this.elementIndex = 131;
                        this.recordResults = 1;
                        return;
                    }
                    if (elementName.equals("RestartHour")) {
                        this.elementIndex = 132;
                        this.recordResults = 1;
                        return;
                    } else if (elementName.equals("RestartMinute")) {
                        this.elementIndex = 133;
                        this.recordResults = 1;
                        return;
                    } else {
                        if (elementName.equals("RestartDay")) {
                            this.elementIndex = 134;
                            this.recordResults = 1;
                            return;
                        }
                        return;
                    }
                case 11:
                    if (elementName.equals("NewDeviceID")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 12:
                    if (elementName.equals("ParentalControl")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 17:
                    if (elementName.equals("NewWEPKey")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 19:
                    GenieDebug.error("isParseTextTag", "BlockDeviceEnableStatus elementName = " + elementName);
                    if (elementName.equals("NewBlockDeviceEnable")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 25:
                    if (elementName.equals("NewDeviceID")) {
                        this.elementIndex = 26;
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 27:
                    if (elementName.equals("NewDeviceID")) {
                        this.elementIndex = 28;
                        this.recordResults = 1;
                        return;
                    }
                    return;
                case 99:
                    if (elementName.equals("NewMACAddress")) {
                        this.recordResults = 1;
                        return;
                    }
                    return;
                default:
                    if (elementName.equals("ResponseCode")) {
                        this.recordResults = 2;
                        return;
                    }
                    return;
            }
        }

        public void tackleXmlText(String string, int soaptype) {
            if (this.recordResults == 0 || string.equals("\n")) {
                this.recordResults = 0;
                return;
            }
            GenieDebug.error("tackleXmlText", "soapType = " + this.m_RequestInfo.aSoapType);
            GenieDebug.error("tackleXmlText", "elementIndex = " + this.elementIndex);
            GenieDebug.error("tackleXmlText", "string = " + string);
            switch (soaptype) {
                case 2:
                    switch (this.elementIndex) {
                        case 100:
                            GenieDebug.error("tackleXmlText", "123456789 string = " + string);
                            break;
                        case 101:
                            GenieDebug.error("tackleXmlText", "123456789 string = " + string);
                            break;
                    }
                case 3:
                    GenieDebug.error("ESoapRequestWLanInfo", "ESoapRequestWLanInfo elementIndex = " + this.elementIndex);
                    GenieDebug.error("ESoapRequestWLanInfo", "ESoapRequestWLanInfo elementIndex = " + string);
                    switch (this.elementIndex) {
                        case 110:
                            GenieSoap.dictionary.put("NewEnable", string);
                            break;
                        case 111:
                            GenieSoap.dictionary.put("NewSSID", GenieRequest.this.escapeString(string));
                            break;
                        case 112:
                            GenieSoap.dictionary.put("NewChannel", string);
                            break;
                        case 113:
                            if (string.equals("USA")) {
                                GenieSoap.dictionary.put("NewRegion", "US");
                                break;
                            } else {
                                GenieSoap.dictionary.put("NewRegion", string);
                                break;
                            }
                        case 114:
                            GenieDebug.error("", "WLAN_INFO_WIRELESS_MODES = " + string);
                            GenieSoap.dictionary.put("NewWirelessMode", string);
                            break;
                        case 115:
                            GenieDebug.error("", "WLAN_INFO_WPA_MODES = " + string);
                            GenieSoap.dictionary.put("NewWPAEncryptionModes", string);
                            break;
                        case 116:
                            StringBuffer mac = new StringBuffer(string);
                            if (mac != null && mac.length() == 12) {
                                for (int i = 10; i > 0; i -= 2) {
                                    mac.insert(i, ":");
                                }
                            }
                            GenieDebug.error("WLAN_INFO_MAC_ADDRESS", "mac = " + mac.toString());
                            GenieSoap.dictionary.put("NewWLANMACAddress", mac.toString());
                            break;
                        case 117:
                            GenieSoap.dictionary.put("NewStatus", string);
                            break;
                        case 118:
                            GenieDebug.error("", "wep777 WLAN_INFO_BASIC_MODES = " + string);
                            GenieSoap.dictionary.put("NewBasicEncryptionModes", string);
                            break;
                    }
                case 4:
                    if (GenieSoap.dictionary.get("NewWPAEncryptionModes").equals("None")) {
                        GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, None)");
                        GenieSoap.dictionary.put("NewWPAPassphrase", "None");
                        break;
                    } else {
                        GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string) string = " + string);
                        GenieSoap.dictionary.put("NewWPAPassphrase", GenieRequest.this.escapeString(string));
                        break;
                    }
                case 5:
                    if (this.recordResults == 1) {
                        GenieDebug.error("DICTIONARY_KEY_GUEST_ABLE", "DICTIONARY_KEY_GUEST_ABLE = " + string);
                        GenieSoap.dictionary.put("NewGuestAccessEnabled", string);
                        break;
                    }
                    break;
                case 6:
                    GenieDebug.error("GUEST_INFO_SSID", " 99999 ESoapRequestGuestInfo elementIndex = " + this.elementIndex);
                    switch (this.elementIndex) {
                        case 120:
                            GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_SSID = " + string);
                            GenieSoap.dictionary.put("NewSSID-Guest", GenieRequest.this.escapeString(string));
                            break;
                        case 121:
                            GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_SECURITY_MODE = " + string);
                            GenieSoap.dictionary.put("NewSecurityMode", string);
                            break;
                        case 122:
                            GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY = [" + string + "]" + string.length());
                            if (GenieSoap.dictionary.get("NewKey").equals("None")) {
                                GenieSoap.dictionary.put("NewKey", "None");
                                break;
                            } else if (parserStringIsEmpty(string)) {
                                GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY is empty");
                                GenieSoap.dictionary.put("NewKey", "");
                                break;
                            } else {
                                GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY not empty");
                                GenieSoap.dictionary.put("NewKey", GenieRequest.this.escapeString(string));
                                break;
                            }
                    }
                case 7:
                    GenieRequest.this.parserStringForNetworkMap(string);
                    break;
                case 8:
                    GenieDebug.error("tackleXmlText", "ESoapReqiestTrafficEnable =" + string);
                    GenieSoap.dictionary.put("NewTrafficMeterEnable", string);
                    break;
                case 9:
                    GenieDebug.error("tackleXmlText", string);
                    StringBuffer temp = new StringBuffer();
                    temp.append(GenieSoap.dictionary.get("NewTrafficMeter"));
                    temp.append(string);
                    temp.append("\n");
                    GenieSoap.dictionary.put("NewTrafficMeter", temp.toString());
                    break;
                case 10:
                    GenieDebug.error("tackleXmlText", "ESoapRequestTrafficOptions string =" + string);
                    GenieDebug.error("tackleXmlText", "ESoapRequestTrafficOptions elementIndex =" + this.elementIndex);
                    switch (this.elementIndex) {
                        case 130:
                            GenieSoap.dictionary.put("NewControlOption", string);
                        case 131:
                            GenieSoap.dictionary.put("NewMonthlyLimit", string);
                        case 132:
                            GenieSoap.dictionary.put("RestartHour", string);
                        case 133:
                            GenieSoap.dictionary.put("RestartMinute", string);
                        case 134:
                            GenieSoap.dictionary.put("RestartDay", string);
                    }
                case 11:
                    GenieSoap.dictionary.put("NewDeviceID", string);
                    GenieDebug.error("tackleXmlText", "DICTIONARY_KEY_DEVICEID = " + string + "DICTIONARY_KEY_DEVICEID");
                    break;
                case 12:
                    GenieDebug.error("tackleXmlText", "DICTIONARY_KEY_PC_STATUS = " + string);
                    GenieSoap.dictionary.put("ParentalControl", string);
                    break;
                case 17:
                    if (GenieSoap.dictionary.get("NewBasicEncryptionModes").equals("None")) {
                        GenieDebug.error("debug", "wep777 dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, None)");
                        GenieSoap.dictionary.put("NewWEPKey", "None");
                        break;
                    } else {
                        GenieDebug.error("debug", "wep777 dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string) string = " + string);
                        GenieSoap.dictionary.put("NewWEPKey", GenieRequest.this.escapeString(string));
                        break;
                    }
                case 19:
                    GenieDebug.error("tackleXmlText", "ESoapRequestBlockDeviceEnableStatus =" + string);
                    GenieSoap.dictionary.put("NewBlockDeviceEnable", string);
                    break;
                case 25:
                    switch (this.elementIndex) {
                        case 26:
                            GenieDebug.error("tackleXmlText", "DICTIONARY_KEY_LPC_NewDeviceID = " + string);
                            GenieSoap.dictionary.put("NewDeviceID", string);
                            break;
                    }
                case 27:
                    switch (this.elementIndex) {
                        case 28:
                            GenieDebug.error("tackleXmlText", "DICTIONARY_KEY_LPC_MyNewDeviceID = " + string);
                            GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_MyNewDeviceID, string);
                            break;
                    }
                case 99:
                    if (this.recordResults == 1) {
                        GenieDebug.error("debug", "DICTIONARY_KEY_MAC_ADDRESS = " + string);
                        GenieSoap.dictionary.put("NewMACAddress", string);
                        break;
                    }
                    break;
            }
            this.recordResults = 0;
        }

        public boolean parserStringIsEmpty(String str) {
            GenieDebug.error("debug", "parserStringIsEmpty 0");
            if (str == null || str.length() <= 0) {
                return true;
            }
            StringBuffer string = new StringBuffer(str);
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) != ' ' && string.charAt(i) != '\n' && string.charAt(i) != '\t') {
                    return false;
                }
            }
            return true;
        }

        public void CancleHttpGet() {
            if (this.request != null) {
                if (!this.request.isAborted()) {
                    this.request.abort();
                }
                this.request = null;
            }
        }

        public Boolean LookupHost(String Url) {
            try {
                InetAddress address = InetAddress.getByName(Url);
                if (address != null) {
                    GenieDebug.error("LookupHost", address.getHostName());
                    GenieDebug.error("LookupHost", "LookupHost rerurn true");
                    return true;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            GenieDebug.error("LookupHost", "LookupHost rerurn false");
            return false;
        }

        private void ChekcLpcHost(int timeout) {
            boolean ret;
            System.setProperty("networkaddress.cache.ttl", "0");
            System.setProperty("networkaddress.cache.negative.ttl", "0");
            if (GenieApplication.isCloud) {
                ret = LookupHost("using.netgear.opendns.com").booleanValue();
            } else if (GenieRequest.m_SmartNetWork) {
                ret = LookupHost("using.netgear.opendns.com").booleanValue();
            } else {
                ret = LookupHost("using.netgear.opendns.com").booleanValue() && LookupHost("routerlogin.net").booleanValue();
            }
            if (ret) {
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            } else {
                this.m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            }
            GenieRequest.this.sendbroad(this.m_RequestInfo);
        }

        public void sendRequestToSmartNetWork(String lab, int smarttype, String aServer, ArrayList<String> aElement) {
            GenieRequestInfo RequestInfo = new GenieRequestInfo();
            RequestInfo.aRequestLable = lab;
            RequestInfo.aRequestType = GenieGlobalDefines.RequestActionType.SmartNetWork;
            RequestInfo.aSmartType = smarttype;
            if (smarttype == 50) {
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);
                ArrayList<XmlParameter> parameterlist = new ArrayList<>();
                XmlParameter para1 = new XmlParameter();
                para1.Tag = "authenticate";
                para1.attribute = new ArrayList<>();
                XmlAttribute attr1_1 = new XmlAttribute(TypeSelector.TYPE_KEY, "basic");
                para1.attribute.add(attr1_1);
                XmlAttribute attr1_2 = new XmlAttribute("username", aElement.get(0));
                para1.attribute.add(attr1_2);
                XmlAttribute attr1_3 = new XmlAttribute("password", aElement.get(1));
                para1.attribute.add(attr1_3);
                parameterlist.add(para1);
                String data = GenieRequest.this.BuildXml(parameterlist);
                GenieRequest.m_SmartInfo.username = aElement.get(0);
                GenieRequest.m_SmartInfo.password = aElement.get(1);
                if (SmartNetWorkOpen("/fcp/authenticate", data, smarttype, true, RequestInfo)) {
                    ArrayList<XmlParameter> parameterlist2 = new ArrayList<>();
                    XmlParameter para2 = new XmlParameter();
                    para2.Tag = "init";
                    para2.attribute = new ArrayList<>();
                    XmlAttribute attr2_1 = new XmlAttribute(TypeSelector.TYPE_KEY, "ui");
                    para2.attribute.add(attr2_1);
                    XmlAttribute attr2_2 = new XmlAttribute("fcmb", "true");
                    para2.attribute.add(attr2_2);
                    parameterlist2.add(para2);
                    String data2 = GenieRequest.this.BuildXml(parameterlist2);
                    if (SmartNetWorkOpen("/fcp/init", data2, 51, true, RequestInfo)) {
                        ArrayList<XmlParameter> parameterlist3 = new ArrayList<>();
                        XmlParameter para3 = new XmlParameter();
                        para3.Tag = "fcml";
                        para3.attribute = new ArrayList<>();
                        XmlAttribute attr3_1 = new XmlAttribute("to", String.format("router@%s", GenieRequest.m_SmartInfo.domain));
                        para3.attribute.add(attr3_1);
                        XmlAttribute attr3_2 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
                        para3.attribute.add(attr3_2);
                        GenieSmartNetWorkInfo genieSmartNetWorkInfo = GenieRequest.m_SmartInfo;
                        int i = genieSmartNetWorkInfo.trace + 1;
                        genieSmartNetWorkInfo.trace = i;
                        XmlAttribute attr3_3 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i)));
                        para3.attribute.add(attr3_3);
                        para3.child = new ArrayList<>();
                        XmlParameter para23 = new XmlParameter();
                        para23.Tag = "get";
                        para3.child.add(para23);
                        parameterlist3.add(para3);
                        String content = GenieRequest.this.BuildXml(parameterlist3);
                        if (SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content, 52, RequestInfo)) {
                            RequestInfo.aSmartType = 52;
                            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        }
                    }
                }
            } else if (smarttype == 53) {
                ArrayList<XmlParameter> parameterlist32 = new ArrayList<>();
                XmlParameter para32 = new XmlParameter();
                para32.Tag = "fcml";
                para32.attribute = new ArrayList<>();
                XmlAttribute attr3_12 = new XmlAttribute("to", String.format("netrouter@%s", GenieRequest.m_SmartInfo.workcpid));
                para32.attribute.add(attr3_12);
                XmlAttribute attr3_22 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
                para32.attribute.add(attr3_22);
                GenieSmartNetWorkInfo genieSmartNetWorkInfo2 = GenieRequest.m_SmartInfo;
                int i2 = genieSmartNetWorkInfo2.trace + 1;
                genieSmartNetWorkInfo2.trace = i2;
                XmlAttribute attr3_32 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i2)));
                para32.attribute.add(attr3_32);
                para32.child = new ArrayList<>();
                XmlParameter para232 = new XmlParameter();
                para232.Tag = "SessionManagement.startSession";
                para232.attribute = new ArrayList<>();
                XmlAttribute attr23_1 = new XmlAttribute("username", aElement.get(0));
                para232.attribute.add(attr23_1);
                XmlAttribute attr23_2 = new XmlAttribute("password", aElement.get(1));
                para232.attribute.add(attr23_2);
                para32.child.add(para232);
                parameterlist32.add(para32);
                String content2 = GenieRequest.this.BuildXml(parameterlist32);
                GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).username = aElement.get(0);
                GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).password = aElement.get(1);
                if (SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content2, smarttype, RequestInfo)) {
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                }
            } else if (smarttype == 55) {
                ArrayList<XmlParameter> parameterlist33 = new ArrayList<>();
                XmlParameter para33 = new XmlParameter();
                para33.Tag = "fcml";
                para33.attribute = new ArrayList<>();
                XmlAttribute attr3_13 = new XmlAttribute("to", String.format("netrouter@%s", GenieRequest.m_SmartInfo.workcpid));
                para33.attribute.add(attr3_13);
                XmlAttribute attr3_23 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
                para33.attribute.add(attr3_23);
                GenieSmartNetWorkInfo genieSmartNetWorkInfo3 = GenieRequest.m_SmartInfo;
                int i3 = genieSmartNetWorkInfo3.trace + 1;
                genieSmartNetWorkInfo3.trace = i3;
                XmlAttribute attr3_33 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i3)));
                para33.attribute.add(attr3_33);
                para33.child = new ArrayList<>();
                XmlParameter para233 = new XmlParameter();
                para233.Tag = "SessionManagement.endSession";
                para233.attribute = new ArrayList<>();
                XmlAttribute attr23_12 = new XmlAttribute("sessionId", GenieRequest.m_SmartInfo.sessionid);
                para233.attribute.add(attr23_12);
                para33.child.add(para233);
                parameterlist33.add(para33);
                String content3 = GenieRequest.this.BuildXml(parameterlist33);
                if (SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content3, smarttype, RequestInfo)) {
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                }
            } else if (smarttype == 52) {
                ArrayList<XmlParameter> parameterlist34 = new ArrayList<>();
                XmlParameter para34 = new XmlParameter();
                para34.Tag = "fcml";
                para34.attribute = new ArrayList<>();
                XmlAttribute attr3_14 = new XmlAttribute("to", String.format("router@%s", GenieRequest.m_SmartInfo.domain));
                para34.attribute.add(attr3_14);
                XmlAttribute attr3_24 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
                para34.attribute.add(attr3_24);
                GenieSmartNetWorkInfo genieSmartNetWorkInfo4 = GenieRequest.m_SmartInfo;
                int i4 = genieSmartNetWorkInfo4.trace + 1;
                genieSmartNetWorkInfo4.trace = i4;
                XmlAttribute attr3_34 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i4)));
                para34.attribute.add(attr3_34);
                para34.child = new ArrayList<>();
                XmlParameter para234 = new XmlParameter();
                para234.Tag = "get";
                para34.child.add(para234);
                parameterlist34.add(para34);
                String content4 = GenieRequest.this.BuildXml(parameterlist34);
                if (SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content4, 52, RequestInfo)) {
                    RequestInfo.aSmartType = 52;
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                }
            }
            GenieRequest.this.sendbroad(RequestInfo);
        }

        private boolean SmartNetWorkOpen(String path, String data, int smarttype, boolean receive, GenieRequestInfo RequestInfo) {
            try {
                try {
                    String site = String.format("%s%s", GenieRequest.this.m_SmartUrl, path);
                    GenieDebug.error("debug", "SmartNetWorkOpen site=" + site);
                    GenieDebug.error("debug", "SmartNetWorkOpen data=" + data);
                    GenieDebug.error("debug", "SmartNetWorkOpen m_SmartInfo.trace = " + GenieRequest.m_SmartInfo.trace);
                    this.m_url = new URL(site);
                    if (this.m_url.getProtocol().toLowerCase().equals("https")) {
                        GenieRequest.trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) this.m_url.openConnection();
                        https.setHostnameVerifier(GenieRequest.DO_NOT_VERIFY);
                        this.m_httpConn = https;
                    } else {
                        this.m_httpConn = (HttpURLConnection) this.m_url.openConnection();
                    }
                    this.m_httpConn.setRequestMethod("POST");
                    this.m_httpConn.setRequestProperty("Content-Type", "text/html");
                    this.m_httpConn.setDoOutput(true);
                    this.m_httpConn.setConnectTimeout(120000);
                    this.m_httpConn.setReadTimeout(120000);
                    PrintWriter outputStream = new PrintWriter(this.m_httpConn.getOutputStream());
                    outputStream.write(data);
                    outputStream.flush();
                    outputStream.close();
                    int responsecode = this.m_httpConn.getResponseCode();
                    GenieDebug.error("debug", "SmartNetWorkOpen responsecode=" + responsecode);
                    InputStreamReader reader = new InputStreamReader((InputStream) this.m_httpConn.getContent());
                    StringBuffer sb = new StringBuffer();
                    BufferedReader buff = new BufferedReader(reader);
                    for (String line = buff.readLine(); line != null; line = buff.readLine()) {
                        sb.append(line);
                    }
                    reader.close();
                    GenieDebug.error("debug", "SmartNetWorkOpen sb.length()=" + sb.length());
                    GenieDebug.error("debug", "SmartNetWorkOpen sb=" + sb.toString());
                    if (sb.length() > 0) {
                        if (GenieRequest.this.parsefcml(sb.toString(), smarttype, receive, RequestInfo)) {
                            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                            cancelhttpUrlConnection();
                            return true;
                        }
                        cancelhttpUrlConnection();
                        return false;
                    }
                    if (receive) {
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        cancelhttpUrlConnection();
                        return false;
                    }
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    cancelhttpUrlConnection();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                    cancelhttpUrlConnection();
                    return false;
                }
            } catch (Throwable th) {
                cancelhttpUrlConnection();
                throw th;
            }
        }

        private boolean SmartNetWorkStartRouterSession() {
            GenieRequestInfo RequestInfo = new GenieRequestInfo();
            RequestInfo.aRequestType = GenieGlobalDefines.RequestActionType.SmartNetWork;
            RequestInfo.aSmartType = 53;
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            String username = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).username;
            String password = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).password;
            ArrayList<XmlParameter> parameterlist3 = new ArrayList<>();
            XmlParameter para3 = new XmlParameter();
            para3.Tag = "fcml";
            para3.attribute = new ArrayList<>();
            XmlAttribute attr3_1 = new XmlAttribute("to", String.format("netrouter@%s", GenieRequest.m_SmartInfo.workcpid));
            para3.attribute.add(attr3_1);
            XmlAttribute attr3_2 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
            para3.attribute.add(attr3_2);
            GenieSmartNetWorkInfo genieSmartNetWorkInfo = GenieRequest.m_SmartInfo;
            int i = genieSmartNetWorkInfo.trace + 1;
            genieSmartNetWorkInfo.trace = i;
            XmlAttribute attr3_3 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i)));
            para3.attribute.add(attr3_3);
            para3.child = new ArrayList<>();
            XmlParameter para23 = new XmlParameter();
            para23.Tag = "SessionManagement.startSession";
            para23.attribute = new ArrayList<>();
            XmlAttribute attr23_1 = new XmlAttribute("username", username);
            para23.attribute.add(attr23_1);
            XmlAttribute attr23_2 = new XmlAttribute("password", password);
            para23.attribute.add(attr23_2);
            para3.child.add(para23);
            parameterlist3.add(para3);
            String content = GenieRequest.this.BuildXml(parameterlist3);
            if (!SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content, 53, RequestInfo)) {
                return false;
            }
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            return true;
        }

        private boolean SmartNetWorkBackgroundAuthorize() {
            GenieRequestInfo RequestInfo = new GenieRequestInfo();
            RequestInfo.aRequestType = GenieGlobalDefines.RequestActionType.SmartNetWork;
            RequestInfo.aSmartType = 50;
            ArrayList<XmlParameter> parameterlist = new ArrayList<>();
            XmlParameter para1 = new XmlParameter();
            para1.Tag = "authenticate";
            para1.attribute = new ArrayList<>();
            XmlAttribute attr1_1 = new XmlAttribute(TypeSelector.TYPE_KEY, "basic");
            para1.attribute.add(attr1_1);
            XmlAttribute attr1_2 = new XmlAttribute("username", GenieRequest.m_SmartInfo.username);
            para1.attribute.add(attr1_2);
            XmlAttribute attr1_3 = new XmlAttribute("password", GenieRequest.m_SmartInfo.password);
            para1.attribute.add(attr1_3);
            parameterlist.add(para1);
            String data = GenieRequest.this.BuildXml(parameterlist);
            if (SmartNetWorkOpen("/fcp/authenticate", data, RequestInfo.aSmartType, true, RequestInfo)) {
                ArrayList<XmlParameter> parameterlist2 = new ArrayList<>();
                XmlParameter para2 = new XmlParameter();
                para2.Tag = "init";
                para2.attribute = new ArrayList<>();
                XmlAttribute attr2_1 = new XmlAttribute(TypeSelector.TYPE_KEY, "ui");
                para2.attribute.add(attr2_1);
                XmlAttribute attr2_2 = new XmlAttribute("fcmb", "true");
                para2.attribute.add(attr2_2);
                parameterlist2.add(para2);
                String data2 = GenieRequest.this.BuildXml(parameterlist2);
                RequestInfo.aSmartType = 51;
                if (SmartNetWorkOpen("/fcp/init", data2, 51, true, RequestInfo)) {
                    String username = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).username;
                    String password = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).password;
                    ArrayList<XmlParameter> parameterlist3 = new ArrayList<>();
                    XmlParameter para3 = new XmlParameter();
                    para3.Tag = "fcml";
                    para3.attribute = new ArrayList<>();
                    XmlAttribute attr3_1 = new XmlAttribute("to", String.format("netrouter@%s", GenieRequest.m_SmartInfo.workcpid));
                    para3.attribute.add(attr3_1);
                    XmlAttribute attr3_2 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
                    para3.attribute.add(attr3_2);
                    GenieSmartNetWorkInfo genieSmartNetWorkInfo = GenieRequest.m_SmartInfo;
                    int i = genieSmartNetWorkInfo.trace + 1;
                    genieSmartNetWorkInfo.trace = i;
                    XmlAttribute attr3_3 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i)));
                    para3.attribute.add(attr3_3);
                    para3.child = new ArrayList<>();
                    XmlParameter para23 = new XmlParameter();
                    para23.Tag = "SessionManagement.startSession";
                    para23.attribute = new ArrayList<>();
                    XmlAttribute attr23_1 = new XmlAttribute("username", username);
                    para23.attribute.add(attr23_1);
                    XmlAttribute attr23_2 = new XmlAttribute("password", password);
                    para23.attribute.add(attr23_2);
                    para3.child.add(para23);
                    parameterlist3.add(para3);
                    String content = GenieRequest.this.BuildXml(parameterlist3);
                    RequestInfo.aSmartType = 53;
                    if (SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content, 53, RequestInfo)) {
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }

        private boolean SmartNetWorkBackgroundAuthorize2() {
            GenieRequestInfo RequestInfo = new GenieRequestInfo();
            RequestInfo.aRequestType = GenieGlobalDefines.RequestActionType.SmartNetWork;
            RequestInfo.aSmartType = 50;
            ArrayList<XmlParameter> parameterlist = new ArrayList<>();
            XmlParameter para1 = new XmlParameter();
            para1.Tag = "authenticate";
            para1.attribute = new ArrayList<>();
            XmlAttribute attr1_1 = new XmlAttribute(TypeSelector.TYPE_KEY, "basic");
            para1.attribute.add(attr1_1);
            XmlAttribute attr1_2 = new XmlAttribute("username", GenieRequest.m_SmartInfo.username);
            para1.attribute.add(attr1_2);
            XmlAttribute attr1_3 = new XmlAttribute("password", GenieRequest.m_SmartInfo.password);
            para1.attribute.add(attr1_3);
            parameterlist.add(para1);
            String data = GenieRequest.this.BuildXml(parameterlist);
            if (SmartNetWorkOpen("/fcp/authenticate", data, RequestInfo.aSmartType, true, RequestInfo)) {
                ArrayList<XmlParameter> parameterlist2 = new ArrayList<>();
                XmlParameter para2 = new XmlParameter();
                para2.Tag = "init";
                para2.attribute = new ArrayList<>();
                XmlAttribute attr2_1 = new XmlAttribute(TypeSelector.TYPE_KEY, "ui");
                para2.attribute.add(attr2_1);
                XmlAttribute attr2_2 = new XmlAttribute("fcmb", "true");
                para2.attribute.add(attr2_2);
                parameterlist2.add(para2);
                String data2 = GenieRequest.this.BuildXml(parameterlist2);
                RequestInfo.aSmartType = 51;
                if (SmartNetWorkOpen("/fcp/init", data2, 51, true, RequestInfo)) {
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    return true;
                }
                return false;
            }
            return false;
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x0048, code lost:
        
            sendSmartRouterRequest(r23);
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:
        
            return true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:42:0x0093, code lost:
        
            sendSmartRouterRequest(r23);
         */
        /* JADX WARN: Code restructure failed: missing block: B:43:?, code lost:
        
            return true;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean SmartNetWorkOpen_fcml(java.lang.String r20, java.lang.String r21, int r22, com.dragonflow.GenieRequestInfo r23) {
            /*
                Method dump skipped, instructions count: 540
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.dragonflow.GenieRequest.RequestTask.SmartNetWorkOpen_fcml(java.lang.String, java.lang.String, int, com.dragonflow.GenieRequestInfo):boolean");
        }

        public void sendSmartRouterRequest(GenieRequestInfo aRequestInfo) {
            GenieDebug.error("debug", "sendSmartRouterRequest 0");
            ArrayList<XmlParameter> parameterlist = new ArrayList<>();
            XmlParameter para1 = new XmlParameter();
            para1.Tag = "fcml";
            para1.attribute = new ArrayList<>();
            XmlAttribute attr1_1 = new XmlAttribute("to", String.format("netrouter@%s", GenieRequest.m_SmartInfo.workcpid));
            para1.attribute.add(attr1_1);
            XmlAttribute attr1_2 = new XmlAttribute("from", String.format("%s@%s", GenieRequest.m_SmartInfo.uiid, GenieRequest.m_SmartInfo.domain));
            para1.attribute.add(attr1_2);
            GenieSmartNetWorkInfo genieSmartNetWorkInfo = GenieRequest.m_SmartInfo;
            int i = genieSmartNetWorkInfo.trace + 1;
            genieSmartNetWorkInfo.trace = i;
            XmlAttribute attr1_3 = new XmlAttribute("_tracer", String.format("%s", Integer.valueOf(i)));
            para1.attribute.add(attr1_3);
            para1.child = new ArrayList<>();
            XmlParameter para2 = new XmlParameter();
            para2.Tag = String.format("%s.%s", aRequestInfo.aServer, aRequestInfo.aMethod);
            para2.attribute = new ArrayList<>();
            XmlAttribute attr2 = new XmlAttribute("_sessionId", GenieRequest.m_SmartInfo.sessionid);
            para2.attribute.add(attr2);
            if (aRequestInfo.aElement != null) {
                for (int i2 = 0; i2 < aRequestInfo.aElement.size(); i2 += 2) {
                    String name = aRequestInfo.aElement.get(i2);
                    String value = aRequestInfo.aElement.get(i2 + 1);
                    GenieDebug.error("debug", "sendSmartRouterRequest aElement name  = " + name);
                    GenieDebug.error("debug", "sendSmartRouterRequest aElement value  = " + value);
                    if (!value.equals("null")) {
                        XmlAttribute attr12 = new XmlAttribute(name, value);
                        para2.attribute.add(attr12);
                    }
                }
            }
            para1.child.add(para2);
            parameterlist.add(para1);
            String content = GenieRequest.this.BuildXml(parameterlist);
            if (content != null) {
                GenieDebug.error("debug", "sendSmartRouterRequest content = " + content);
                SmartNetWorkOpen_fcml(GenieRequest.m_SmartInfo.uiid, content, 54, aRequestInfo);
                GenieRequest.this.sendbroad(aRequestInfo);
            }
        }

        private boolean SmartNetWorkReceive(String path, int smarttype, StringBuffer trace, GenieRequestInfo RequestInfo) {
            try {
                try {
                    String site = String.format("%s%s", GenieRequest.this.m_SmartUrl, path);
                    GenieDebug.error("debug", "SmartNetWorkReceive site=" + site);
                    this.m_url = new URL(site);
                    if (this.m_url.getProtocol().toLowerCase().equals("https")) {
                        GenieRequest.trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) this.m_url.openConnection();
                        https.setHostnameVerifier(GenieRequest.DO_NOT_VERIFY);
                        this.m_httpConn = https;
                    } else {
                        this.m_httpConn = (HttpURLConnection) this.m_url.openConnection();
                    }
                    this.m_httpConn.setRequestMethod("GET");
                    this.m_httpConn.setDoOutput(true);
                    this.m_httpConn.setConnectTimeout(120000);
                    this.m_httpConn.setReadTimeout(120000);
                    PrintWriter outputStream = new PrintWriter(this.m_httpConn.getOutputStream());
                    outputStream.flush();
                    outputStream.close();
                    int responsecode = this.m_httpConn.getResponseCode();
                    GenieDebug.error("debug", "SmartNetWorkOpen responsecode=" + responsecode);
                    InputStreamReader reader = new InputStreamReader((InputStream) this.m_httpConn.getContent());
                    StringBuffer sb = new StringBuffer();
                    BufferedReader buff = new BufferedReader(reader);
                    for (String line = buff.readLine(); line != null; line = buff.readLine()) {
                        sb.append(line);
                    }
                    reader.close();
                    GenieDebug.error("debug", "SmartNetWorkReceive sb.length()=" + sb.length());
                    GenieDebug.error("debug", "SmartNetWorkReceive sb=" + sb.toString());
                    if (sb.length() > 0) {
                        return GenieRequest.this.ParseReceiveFcml(sb.toString(), smarttype, trace, RequestInfo);
                    }
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                    cancelhttpUrlConnection();
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                    cancelhttpUrlConnection();
                    return false;
                }
            } finally {
                cancelhttpUrlConnection();
            }
        }

        public void sendSmartRequest(GenieRequestInfo aRequestInfo) {
            String md;
            GenieSmartRouterInfo workrouter = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid);
            if (workrouter != null && (md = workrouter.model) != null && (md.startsWith("CG") || md.startsWith("DG"))) {
                aRequestInfo.aNeedwrap = true;
            }
            if (aRequestInfo.aNeedwrap) {
                GenieRequestInfo start = new GenieRequestInfo();
                start.aElement = new ArrayList<>();
                start.aElement.add("NewSessionID");
                start.aElement.add(GenieRequest.m_SmartInfo.sessionid);
                start.aNeedParser = false;
                start.aServer = "DeviceConfig";
                start.aMethod = "ConfigurationStarted";
                start.aSoapType = 100;
                start.aTimeout = 15000;
                sendSmartRouterRequest(start);
                if (aRequestInfo.aSoapType == 4) {
                    String wep = GenieSoap.dictionary.get("NewBasicEncryptionModes");
                    if (wep != null && wep.equals("WEP")) {
                        GenieRequestInfo WEPWLan = new GenieRequestInfo();
                        WEPWLan.aElement = new ArrayList<>();
                        WEPWLan.aElement.add("NewWEPKey");
                        WEPWLan.aElement.add("null");
                        WEPWLan.aElement.add("NewWPAPassphrase");
                        WEPWLan.aElement.add("null");
                        WEPWLan.aNeedParser = false;
                        WEPWLan.aServer = "WLANConfiguration";
                        WEPWLan.aMethod = "GetWEPSecurityKeys";
                        WEPWLan.aSoapType = 17;
                        WEPWLan.aTimeout = 20000;
                        sendSmartRouterRequest(WEPWLan);
                    } else {
                        sendSmartRouterRequest(aRequestInfo);
                    }
                } else {
                    sendSmartRouterRequest(aRequestInfo);
                }
                GenieRequestInfo finish = new GenieRequestInfo();
                finish.aElement = new ArrayList<>();
                finish.aElement.add("NewStatus");
                finish.aElement.add("ChangesApplied");
                finish.aNeedParser = false;
                finish.aServer = "DeviceConfig";
                finish.aMethod = "ConfigurationFinished";
                finish.aSoapType = 109;
                finish.aTimeout = 15000;
                sendSmartRouterRequest(finish);
                return;
            }
            sendSmartRouterRequest(aRequestInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean ParseReceiveFcml(String fcml, int smarttype, StringBuffer trace, GenieRequestInfo RequestInfo) {
        switch (smarttype) {
            case 52:
                boolean ret = ParseReceive_GetRouterList(fcml, smarttype, trace, RequestInfo);
                return ret;
            case 53:
                boolean ret2 = ParseReceive_StartSession(fcml, smarttype, trace, RequestInfo);
                return ret2;
            case 54:
                boolean ret3 = ParseReceive_SoapRequest(fcml, smarttype, trace, RequestInfo);
                return ret3;
            case 55:
                boolean ret4 = ParseReceive_EndSession(fcml, smarttype, trace, RequestInfo);
                return ret4;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean parsefcml(String data, int smarttype, boolean receive, GenieRequestInfo RequestInfo) {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(data));
            int eventType = parser.getEventType();
            GenieDebug.error("debug", "eventType " + eventType);
            while (eventType != 1) {
                if (eventType == 0) {
                    GenieDebug.error("debug", "Start document " + parser.getName());
                } else if (eventType == 1) {
                    GenieDebug.error("debug", "End document " + parser.getName());
                } else if (eventType == 2) {
                    String tag = parser.getName();
                    GenieDebug.error("debug", "Start tag " + tag);
                    if (tag != null && tag.equals("authenticate")) {
                        if (smarttype == 54) {
                            String result = parser.getAttributeValue(null, "result");
                            try {
                                int code = Integer.valueOf(result).intValue();
                                if (code == 401) {
                                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                                    return false;
                                }
                                continue;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            String auth = parser.getAttributeValue(null, "authenticated");
                            GenieDebug.error("xiaotech", "auth:" + auth);
                            if (auth == null) {
                                GenieSoap.dictionary.put("authenticated", "N/A");
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                                return false;
                            }
                            GenieSoap.dictionary.put("authenticated", auth);
                            if (smarttype == 50 && auth.equals("true")) {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                return true;
                            }
                            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                            return false;
                        }
                    } else if (tag != null && tag.equals("init")) {
                        String domain = parser.getAttributeValue(null, "domain");
                        String name = parser.getAttributeValue(null, "name");
                        GenieDebug.error("xiaotech", "domain:" + domain);
                        GenieDebug.error("xiaotech", "name:" + name);
                        if (domain != null && name != null && smarttype == 51) {
                            m_SmartInfo.domain = domain;
                            m_SmartInfo.uiid = name;
                            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                            return true;
                        }
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                        return false;
                    }
                } else if (eventType == 3) {
                    GenieDebug.error("debug", "End tag " + parser.getName());
                } else if (eventType == 4) {
                    GenieDebug.error("debug", "Text " + parser.getText());
                }
                eventType = parser.next();
                GenieDebug.error("debug", "eventType " + eventType);
            }
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            return false;
        }
    }

    private boolean ParseReceive_GetRouterList(String fcml, int smarttype, StringBuffer trace, GenieRequestInfo RequestInfo) {
        int itrace = 0;
        if (m_SmartInfo.routerlist == null) {
            return false;
        }
        m_SmartInfo.routerlist.clear();
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(fcml));
            int eventType = parser.getEventType();
            GenieDebug.error("debug", "eventType " + eventType);
            while (eventType != 1) {
                if (eventType == 0) {
                    GenieDebug.error("debug", "Start document " + parser.getName());
                } else if (eventType == 1) {
                    GenieDebug.error("debug", "End document " + parser.getName());
                } else if (eventType == 2) {
                    String tag = parser.getName();
                    GenieDebug.error("debug", "Start tag " + tag);
                    if (tag != null && tag.equals("authenticate")) {
                        String result = parser.getAttributeValue(null, "result");
                        try {
                            int code = Integer.valueOf(result).intValue();
                            if (code == 401) {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (tag != null && tag.equals("fcml")) {
                        String _trace = parser.getAttributeValue(null, "_tracer");
                        trace.delete(0, trace.length());
                        trace.append(_trace);
                        if (_trace != null) {
                            try {
                                itrace = Integer.valueOf(_trace).intValue();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                    }
                    if (itrace == m_SmartInfo.trace && tag != null && tag.startsWith("portal.router.") && tag.endsWith(".is_all")) {
                        GenieSmartRouterInfo router = new GenieSmartRouterInfo();
                        router.cpid = tag.substring("portal.router.".length(), tag.length() - ".is_all".length());
                        router.active = parser.getAttributeValue(null, "active");
                        router.friendly_name = parser.getAttributeValue(null, "friendly_name");
                        router.model = parser.getAttributeValue(null, Genie_Data_DBHelper.FIELD_MODEL);
                        router.owner = parser.getAttributeValue(null, "owner");
                        router.serial = parser.getAttributeValue(null, "serial");
                        router.type = parser.getAttributeValue(null, TypeSelector.TYPE_KEY);
                        if (!router.model.toLowerCase().startsWith("rnd")) {
                            m_SmartInfo.routerlist.add(router);
                        }
                        GenieDebug.error("debug", "Start router.cpid: " + router.cpid);
                        GenieDebug.error("debug", "Start router.active: " + router.active);
                        GenieDebug.error("debug", "Start router.friendly_name: " + router.friendly_name);
                        GenieDebug.error("debug", "Start router.model: " + router.model);
                        GenieDebug.error("debug", "Start router.owner: " + router.owner);
                        GenieDebug.error("debug", "Start router.serial: " + router.serial);
                        GenieDebug.error("debug", "Start router.type: " + router.type);
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    }
                } else if (eventType == 3) {
                    GenieDebug.error("debug", "End tag " + parser.getName());
                } else if (eventType == 4) {
                    GenieDebug.error("debug", "Text " + parser.getText());
                }
                eventType = parser.next();
                GenieDebug.error("debug", "eventType " + eventType);
            }
            if (m_SmartInfo.routerlist.size() > 1) {
                SortRouterList(m_SmartInfo.routerlist);
            }
            if (itrace == m_SmartInfo.trace) {
                return true;
            }
            return false;
        } catch (Exception e3) {
            e3.printStackTrace();
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
            return false;
        }
    }

    public void SortRouterList(ArrayList<GenieSmartRouterInfo> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<GenieSmartRouterInfo>() { // from class: com.dragonflow.GenieRequest.3
                @Override // java.util.Comparator
                public int compare(GenieSmartRouterInfo object1, GenieSmartRouterInfo object2) {
                    if (object1.active.equals("true") && object2.active.equals("true")) {
                        return 0;
                    }
                    if (object1.active.equals("false") && object2.active.equals("true")) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
    }

    private boolean ParseReceive_SoapRequest(String fcml, int smarttype, StringBuffer trace, GenieRequestInfo RequestInfo) {
        XmlPullParser parser;
        int eventType;
        String responseCode;
        boolean ret = false;
        int itrace = 0;
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(fcml));
            eventType = parser.getEventType();
            GenieDebug.error("debug", "eventType " + eventType);
        } catch (Exception e) {
            e.printStackTrace();
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
            RequestInfo.aResponse = fcml;
            return false;
        }
        while (eventType != 1) {
            if (eventType == 0) {
                GenieDebug.error("debug", "Start document " + parser.getName());
            } else if (eventType == 1) {
                GenieDebug.error("debug", "End document " + parser.getName());
            } else {
                if (eventType == 2) {
                    String tag = parser.getName();
                    GenieDebug.error("debug", "Start tag " + tag);
                    if (tag != null && tag.equals("authenticate")) {
                        String result = parser.getAttributeValue(null, "result");
                        try {
                            int code = Integer.valueOf(result).intValue();
                            if (code == 401) {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                                return false;
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (tag != null && tag.equals("fcml")) {
                        String _trace = parser.getAttributeValue(null, "_tracer");
                        trace.delete(0, trace.length());
                        trace.append(_trace);
                        if (_trace != null) {
                            try {
                                itrace = Integer.valueOf(_trace).intValue();
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                    }
                    if (tag != null && tag.equals("error")) {
                        String errorcode = parser.getAttributeValue(null, "code");
                        if (errorcode != null) {
                            try {
                                int code2 = Integer.valueOf(errorcode).intValue();
                                RequestInfo.errorcode = code2;
                            } catch (NumberFormatException e4) {
                                e4.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.error;
                        return false;
                    }
                    GenieDebug.error("debug", String.format("ParseReceive_SoapRequest itrace = %s,m_SmartInfo.trace=%s ", Integer.valueOf(itrace), Integer.valueOf(m_SmartInfo.trace)));
                    GenieDebug.error("debug", String.format("ParseReceive_SoapRequest tag = %s,==%s ", tag, String.format("%s.netrouter.%s.%s", m_SmartInfo.workcpid, RequestInfo.aServer, RequestInfo.aMethod)));
                    if (itrace == m_SmartInfo.trace && tag != null && tag.equals(String.format("%s.netrouter.%s.%s", m_SmartInfo.workcpid, RequestInfo.aServer, RequestInfo.aMethod)) && (responseCode = parser.getAttributeValue(null, "_responseCode")) != null) {
                        ret = true;
                        GenieDebug.error("debug", "ParseReceive_SoapRequest responseCode=" + responseCode);
                        try {
                            int code3 = Integer.valueOf(responseCode).intValue();
                            if (code3 == 0) {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                                RequestInfo.aResponseCode = responseCode;
                                RequestInfo.aResponse = fcml;
                                if (RequestInfo.aNeedParser) {
                                    ParseReceive_SoapRequest_GetAttributeValue(parser, RequestInfo);
                                }
                            } else {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
                                RequestInfo.aResponse = fcml;
                                RequestInfo.aResponseCode = responseCode;
                            }
                        } catch (Exception e5) {
                            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                            e5.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
                    RequestInfo.aResponse = fcml;
                    return false;
                }
                if (eventType == 3) {
                    GenieDebug.error("debug", "End tag " + parser.getName());
                } else if (eventType == 4) {
                    GenieDebug.error("debug", "Text " + parser.getText());
                }
            }
            eventType = parser.next();
            GenieDebug.error("debug", "eventType " + eventType);
        }
        if (itrace == m_SmartInfo.trace) {
            return ret;
        }
        return false;
    }

    private boolean ParseReceive_SoapRequest_GetAttributeValue(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetAttributeValue  RequestInfo.aSoapType =" + RequestInfo.aSoapType);
        switch (RequestInfo.aSoapType) {
            case 2:
                boolean ret = ParseReceive_SoapRequest_GetRouterInfo(parser, RequestInfo);
                return ret;
            case 3:
                boolean ret2 = ParseReceive_SoapRequest_GetInfo(parser, RequestInfo);
                return ret2;
            case 4:
                boolean ret3 = ParseReceive_SoapRequest_GetWLanWEPKey(parser, RequestInfo);
                return ret3;
            case 5:
                boolean ret4 = ParseReceive_SoapRequest_GetGuestEnable(parser, RequestInfo);
                return ret4;
            case 6:
                boolean ret5 = ParseReceive_SoapRequest_GetGuestInfo(parser, RequestInfo);
                return ret5;
            case 7:
                boolean ret6 = ParseReceive_SoapRequest_RouterMap(parser, RequestInfo);
                return ret6;
            case 8:
                boolean ret7 = ParseReceive_SoapRequest_GetTrafficEnable(parser, RequestInfo);
                return ret7;
            case 9:
                boolean ret8 = ParseReceive_SoapRequest_GetTrafficData(parser, RequestInfo);
                return ret8;
            case 10:
                boolean ret9 = ParseReceive_SoapRequest_GetTrafficOptions(parser, RequestInfo);
                return ret9;
            case 11:
                boolean ret10 = ParseReceive_SoapRequest_GetLpcDeviceID(parser, RequestInfo);
                return ret10;
            case 12:
                boolean ret11 = ParseReceive_SoapRequest_GetLPCStatus(parser, RequestInfo);
                return ret11;
            case 19:
                boolean ret12 = ParseReceive_SoapRequest_BlockDeviceEnableStatus(parser, RequestInfo);
                return ret12;
            case 99:
                boolean ret13 = ParseReceive_SoapRequest_GetConfigWan(parser, RequestInfo);
                return ret13;
            default:
                return false;
        }
    }

    public void parserStringForNetworkMap(String string) {
        if (string.length() != 0 && string.charAt(0) != 0) {
            char num = string.charAt(0);
            GenieDebug.error("parserStringForNetworkMap", "num = " + num);
            GenieDebug.error("parserStringForNetworkMap", "string = " + string);
            GenieSoap.routerMap.clear();
            StringBuffer cmpKey = new StringBuffer();
            int i = 1;
            cmpKey.replace(0, cmpKey.length(), "@1;");
            while (i <= num) {
                int posS = string.indexOf(cmpKey.toString());
                if (posS != -1) {
                    i++;
                    String currentCmpKey = cmpKey.toString();
                    cmpKey.replace(0, cmpKey.length(), "@" + i + ";");
                    int posE = string.indexOf(cmpKey.toString());
                    if (posE == -1) {
                        posE = string.length();
                    }
                    String devicesinfo = string.substring(currentCmpKey.length() + posS, posE).replace("&lt;", "<").replace("&gt;", ">");
                    if (devicesinfo != null) {
                        GenieDebug.error("debug", "test  = " + devicesinfo);
                        int n = 0;
                        HashMap<String, String> detail = new HashMap<>();
                        for (String word : devicesinfo.split(";")) {
                            GenieDebug.error("debug", "test word = " + word);
                            if (word.endsWith("@")) {
                                GenieDebug.error("debug", "word.endsWith(@)");
                                word = word.substring(0, word.length() - 1);
                            }
                            switch (n) {
                                case 0:
                                    detail.put("DEVICE_IP", word);
                                    break;
                                case 1:
                                    detail.put("DEVICE_NAME", word);
                                    break;
                                case 2:
                                    detail.put("DEVICE_MAC", word);
                                    break;
                                case 3:
                                    detail.put("CONNECT_TYPE", word);
                                    break;
                                case 4:
                                    detail.put("CONNECT_SPEED", word);
                                    break;
                                case 5:
                                    detail.put("CONNECT_INTENSITY", word);
                                    break;
                                case 6:
                                    detail.put("DEVICE_BLOCK", word);
                                    break;
                            }
                            n++;
                        }
                        GenieSoap.routerMap.add(detail);
                    }
                } else {
                    return;
                }
            }
        }
    }

    private boolean ParseReceive_SoapRequest_RouterMap(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_RouterMap 0");
        String map = parser.getAttributeValue(null, "NewAttachDevice");
        if (map != null) {
            GenieDebug.error("debug", "ParseReceive_SoapRequest_RouterMap map=" + map);
            parserStringForNetworkMap(map);
            return true;
        }
        String map2 = parser.getAttributeValue(null, "AttachDevice");
        if (map2 == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_RouterMap map=" + map2);
        parserStringForNetworkMap(map2);
        return true;
    }

    private boolean ParseReceive_SoapRequest_BlockDeviceEnableStatus(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_BlockDeviceEnableStatus 0");
        String Status = parser.getAttributeValue(null, "NewBlockDeviceEnable");
        if (Status == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_BlockDeviceEnableStatus Status=" + Status);
        GenieSoap.dictionary.put("NewBlockDeviceEnable", Status);
        return true;
    }

    private boolean ParseReceive_SoapRequest_GetLPCStatus(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetLPCStatus 0");
        String LPCStatus = parser.getAttributeValue(null, "ParentalControl");
        if (LPCStatus == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetLPCStatus LPCStatus=" + LPCStatus);
        GenieSoap.dictionary.put("ParentalControl", LPCStatus);
        return true;
    }

    private boolean ParseReceive_SoapRequest_GetConfigWan(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetConfigWan 0");
        String mac = parser.getAttributeValue(null, "NewMACAddress");
        if (mac == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetConfigWan mac=" + mac);
        GenieSoap.dictionary.put("NewMACAddress", mac);
        return true;
    }

    private boolean ParseReceive_SoapRequest_GetRouterInfo(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        boolean ret = false;
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetRouterInfo 0");
        String modename = parser.getAttributeValue(null, "ModelName");
        if (modename != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetRouterInfo modename=" + modename);
        }
        String firmware = parser.getAttributeValue(null, "Firmwareversion");
        if (firmware != null) {
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetRouterInfo firmware=" + firmware);
            GenieSoap.dictionary.put("Firmwareversion", firmware);
            return true;
        }
        return ret;
    }

    private boolean ParseReceive_SoapRequest_GetLpcDeviceID(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetLpcDeviceID 0");
        String DeviceID = parser.getAttributeValue(null, "NewDeviceID");
        if (DeviceID == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetLpcDeviceID DeviceID=" + DeviceID);
        GenieSoap.dictionary.put("NewDeviceID", DeviceID);
        return true;
    }

    private boolean ParseReceive_SoapRequest_GetTrafficData(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        boolean ret = false;
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData 0");
        StringBuffer temp = new StringBuffer();
        String NewTodayConnectionTime = parser.getAttributeValue(null, "NewTodayConnectionTime");
        if (NewTodayConnectionTime != null) {
            ret = true;
            temp.append(NewTodayConnectionTime);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewTodayConnectionTime=" + NewTodayConnectionTime);
        }
        String NewTodayUpload = parser.getAttributeValue(null, "NewTodayUpload");
        if (NewTodayUpload != null) {
            ret = true;
            temp.append(NewTodayUpload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewTodayUpload=" + NewTodayUpload);
        }
        String NewTodayDownload = parser.getAttributeValue(null, "NewTodayDownload");
        if (NewTodayDownload != null) {
            ret = true;
            temp.append(NewTodayDownload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewTodayDownload=" + NewTodayDownload);
        }
        String NewYesterdayConnectionTime = parser.getAttributeValue(null, "NewYesterdayConnectionTime");
        if (NewYesterdayConnectionTime != null) {
            ret = true;
            temp.append(NewYesterdayConnectionTime);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewYesterdayConnectionTime=" + NewYesterdayConnectionTime);
        }
        String NewYesterdayUpload = parser.getAttributeValue(null, "NewYesterdayUpload");
        if (NewYesterdayUpload != null) {
            ret = true;
            temp.append(NewYesterdayUpload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewYesterdayUpload=" + NewYesterdayUpload);
        }
        String NewYesterdayDownload = parser.getAttributeValue(null, "NewYesterdayDownload");
        if (NewYesterdayDownload != null) {
            ret = true;
            temp.append(NewYesterdayDownload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewYesterdayDownload=" + NewYesterdayDownload);
        }
        String NewWeekConnectionTime = parser.getAttributeValue(null, "NewWeekConnectionTime");
        if (NewWeekConnectionTime != null) {
            ret = true;
            temp.append(NewWeekConnectionTime);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewWeekConnectionTime=" + NewWeekConnectionTime);
        }
        String NewWeekUpload = parser.getAttributeValue(null, "NewWeekUpload");
        if (NewWeekUpload != null) {
            ret = true;
            temp.append(NewWeekUpload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewWeekUpload=" + NewWeekUpload);
        }
        String NewWeekDownload = parser.getAttributeValue(null, "NewWeekDownload");
        if (NewWeekDownload != null) {
            ret = true;
            temp.append(NewWeekDownload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewWeekDownload=" + NewWeekDownload);
        }
        String NewMonthConnectionTime = parser.getAttributeValue(null, "NewMonthConnectionTime");
        if (NewMonthConnectionTime != null) {
            ret = true;
            temp.append(NewMonthConnectionTime);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewMonthConnectionTime=" + NewMonthConnectionTime);
        }
        String NewMonthUpload = parser.getAttributeValue(null, "NewMonthUpload");
        if (NewMonthUpload != null) {
            ret = true;
            temp.append(NewMonthUpload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewMonthUpload=" + NewMonthUpload);
        }
        String NewMonthDownload = parser.getAttributeValue(null, "NewMonthDownload");
        if (NewMonthDownload != null) {
            ret = true;
            temp.append(NewMonthDownload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewMonthDownload=" + NewMonthDownload);
        }
        String NewLastMonthConnectionTime = parser.getAttributeValue(null, "NewLastMonthConnectionTime");
        if (NewLastMonthConnectionTime != null) {
            ret = true;
            temp.append(NewLastMonthConnectionTime);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewLastMonthConnectionTime=" + NewLastMonthConnectionTime);
        }
        String NewLastMonthUpload = parser.getAttributeValue(null, "NewLastMonthUpload");
        if (NewLastMonthUpload != null) {
            ret = true;
            temp.append(NewLastMonthUpload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewLastMonthUpload=" + NewLastMonthUpload);
        }
        String NewLastMonthDownload = parser.getAttributeValue(null, "NewLastMonthDownload");
        if (NewLastMonthDownload != null) {
            ret = true;
            temp.append(NewLastMonthDownload);
            temp.append("\n");
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficData NewLastMonthDownload=" + NewLastMonthDownload);
        }
        GenieSoap.dictionary.put("NewTrafficMeter", temp.toString());
        return ret;
    }

    private boolean ParseReceive_SoapRequest_GetTrafficOptions(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        boolean ret = false;
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficOptions 0");
        String control = parser.getAttributeValue(null, "NewControlOption");
        if (control != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficOptions control=" + control);
            GenieSoap.dictionary.put("NewControlOption", control);
        }
        String limit = parser.getAttributeValue(null, "NewMonthlyLimit");
        if (limit != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficOptions limit=" + limit);
            GenieSoap.dictionary.put("NewMonthlyLimit", limit);
        }
        String hour = parser.getAttributeValue(null, "RestartHour");
        if (hour != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficOptions hour=" + hour);
            GenieSoap.dictionary.put("RestartHour", hour);
        }
        String minute = parser.getAttributeValue(null, "RestartMinute");
        if (minute != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficOptions minute=" + minute);
            GenieSoap.dictionary.put("RestartMinute", minute);
        }
        String day = parser.getAttributeValue(null, "RestartDay");
        if (day != null) {
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficOptions day=" + day);
            GenieSoap.dictionary.put("RestartDay", day);
            return true;
        }
        return ret;
    }

    private boolean ParseReceive_SoapRequest_GetTrafficEnable(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficEnable 0");
        String TrafficEnable = parser.getAttributeValue(null, "NewTrafficMeterEnable");
        if (TrafficEnable == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetTrafficEnable TrafficEnable=" + TrafficEnable);
        GenieSoap.dictionary.put("NewTrafficMeterEnable", TrafficEnable);
        return true;
    }

    private boolean ParseReceive_SoapRequest_GetGuestInfo(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        boolean ret = false;
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetGuestInfo 0");
        String ssid = parser.getAttributeValue(null, "NewSSID");
        if (ssid != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetGuestInfo ssid=" + ssid);
            GenieSoap.dictionary.put("NewSSID-Guest", ssid);
        }
        String guest_mode = parser.getAttributeValue(null, "NewSecurityMode");
        if (guest_mode != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetGuestInfo guest_mode=" + guest_mode);
            GenieSoap.dictionary.put("NewSecurityMode", guest_mode);
        }
        String guest_key = parser.getAttributeValue(null, "NewKey");
        if (guest_key != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetGuestInfo guest_key=" + guest_key);
            if (GenieSoap.dictionary.get("NewKey").equals("None")) {
                GenieSoap.dictionary.put("NewKey", "None");
            } else {
                GenieSoap.dictionary.put("NewKey", guest_key);
            }
        }
        return ret;
    }

    private boolean ParseReceive_SoapRequest_GetGuestEnable(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetGuestEnable 0");
        String guest_able = parser.getAttributeValue(null, "NewGuestAccessEnabled");
        if (guest_able == null) {
            return false;
        }
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetGuestEnable guest_able=" + guest_able);
        GenieSoap.dictionary.put("NewGuestAccessEnabled", guest_able);
        return true;
    }

    private boolean ParseReceive_SoapRequest_GetWLanWEPKey(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        boolean ret = false;
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetWLanWEPKey 0");
        String wpa_key = parser.getAttributeValue(null, "NewWPAPassphrase");
        if (wpa_key != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetWLanWEPKey wpa_key=" + wpa_key);
            if (GenieSoap.dictionary.get("NewWPAEncryptionModes").equals("None")) {
                GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, None)");
                GenieSoap.dictionary.put("NewWPAPassphrase", "None");
            } else {
                GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string) string = " + wpa_key);
                GenieSoap.dictionary.put("NewWPAPassphrase", wpa_key);
            }
        }
        return ret;
    }

    private boolean ParseReceive_SoapRequest_GetInfo(XmlPullParser parser, GenieRequestInfo RequestInfo) {
        boolean ret = false;
        GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo 0");
        String enable = parser.getAttributeValue(null, "NewEnable");
        if (enable != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo enable=" + enable);
            GenieSoap.dictionary.put("NewEnable", enable);
        }
        String ssid = parser.getAttributeValue(null, "NewSSID");
        if (ssid != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo ssid=" + ssid);
            GenieSoap.dictionary.put("NewSSID", ssid);
        }
        String channel = parser.getAttributeValue(null, "NewChannel");
        if (channel != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo channel=" + channel);
            GenieSoap.dictionary.put("NewChannel", channel);
        }
        String region = parser.getAttributeValue(null, "NewRegion");
        if (region != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo region=" + region);
            if (region.equals("USA")) {
                GenieSoap.dictionary.put("NewRegion", "US");
            } else {
                GenieSoap.dictionary.put("NewRegion", region);
            }
        }
        String wire_mode = parser.getAttributeValue(null, "NewWirelessMode");
        if (wire_mode != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo wire_mode=" + wire_mode);
            GenieSoap.dictionary.put("NewWirelessMode", wire_mode);
        }
        String wpa_key = parser.getAttributeValue(null, "NewWPAEncryptionModes");
        if (wpa_key != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo wpa_key=" + wpa_key);
            GenieSoap.dictionary.put("NewWPAEncryptionModes", wpa_key);
        }
        String wlan_mac = parser.getAttributeValue(null, "NewWLANMACAddress");
        if (wlan_mac != null) {
            ret = true;
            StringBuffer mac = new StringBuffer(wlan_mac);
            if (mac != null && mac.length() == 12) {
                for (int i = 10; i > 0; i -= 2) {
                    mac.insert(i, ":");
                }
            }
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo mac.toString()=" + mac.toString());
            GenieSoap.dictionary.put("NewWLANMACAddress", mac.toString());
        }
        String wlan_status = parser.getAttributeValue(null, "NewStatus");
        if (wlan_status != null) {
            ret = true;
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo wlan_status=" + wlan_status);
            GenieSoap.dictionary.put("NewStatus", wlan_status);
        }
        String basic_mode = parser.getAttributeValue(null, "NewBasicEncryptionModes");
        if (basic_mode != null) {
            GenieDebug.error("debug", "ParseReceive_SoapRequest_GetInfo basic_mode=" + basic_mode);
            GenieSoap.dictionary.put("NewBasicEncryptionModes", basic_mode);
            return true;
        }
        return ret;
    }

    private boolean ParseReceive_EndSession(String fcml, int smarttype, StringBuffer trace, GenieRequestInfo RequestInfo) {
        int itrace = 0;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(fcml));
            int eventType = parser.getEventType();
            GenieDebug.error("debug", "eventType " + eventType);
            while (eventType != 1) {
                if (eventType == 0) {
                    GenieDebug.error("debug", "Start document " + parser.getName());
                } else if (eventType == 1) {
                    GenieDebug.error("debug", "End document " + parser.getName());
                } else if (eventType == 2) {
                    String tag = parser.getName();
                    GenieDebug.error("debug", "Start tag " + tag);
                    if (tag != null && tag.equals("authenticate")) {
                        String result = parser.getAttributeValue(null, "result");
                        try {
                            int code = Integer.valueOf(result).intValue();
                            if (code == 401) {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (tag != null && tag.equals("fcml")) {
                        String _trace = parser.getAttributeValue(null, "_tracer");
                        trace.delete(0, trace.length());
                        trace.append(_trace);
                        if (_trace != null) {
                            try {
                                itrace = Integer.valueOf(_trace).intValue();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                    }
                    if (tag != null && tag.equals("error")) {
                        String errorcode = parser.getAttributeValue(null, "code");
                        if (errorcode != null) {
                            try {
                                int code2 = Integer.valueOf(errorcode).intValue();
                                RequestInfo.errorcode = code2;
                            } catch (NumberFormatException e3) {
                                e3.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.error;
                        return false;
                    }
                } else if (eventType == 3) {
                    GenieDebug.error("debug", "End tag " + parser.getName());
                } else if (eventType == 4) {
                    GenieDebug.error("debug", "Text " + parser.getText());
                }
                eventType = parser.next();
                GenieDebug.error("debug", "eventType " + eventType);
            }
            if (itrace == m_SmartInfo.trace) {
                return true;
            }
            return false;
        } catch (Exception e4) {
            e4.printStackTrace();
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
            return false;
        }
    }

    private boolean ParseReceive_StartSession(String fcml, int smarttype, StringBuffer trace, GenieRequestInfo RequestInfo) {
        int itrace = 0;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(fcml));
            int eventType = parser.getEventType();
            GenieDebug.error("debug", "eventType " + eventType);
            while (eventType != 1) {
                if (eventType == 0) {
                    GenieDebug.error("debug", "Start document " + parser.getName());
                } else if (eventType == 1) {
                    GenieDebug.error("debug", "End document " + parser.getName());
                } else if (eventType == 2) {
                    String tag = parser.getName();
                    GenieDebug.error("debug", "Start tag " + tag);
                    if (tag != null && tag.equals("authenticate")) {
                        String result = parser.getAttributeValue(null, "result");
                        try {
                            int code = Integer.valueOf(result).intValue();
                            if (code == 401) {
                                RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Unauthorized;
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (tag != null && tag.equals("fcml")) {
                        String _trace = parser.getAttributeValue(null, "_tracer");
                        trace.delete(0, trace.length());
                        trace.append(_trace);
                        if (_trace != null) {
                            try {
                                itrace = Integer.valueOf(_trace).intValue();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                    }
                    if (tag != null && tag.equals("error")) {
                        String errorcode = parser.getAttributeValue(null, "code");
                        if (errorcode != null) {
                            try {
                                int code2 = Integer.valueOf(errorcode).intValue();
                                RequestInfo.errorcode = code2;
                            } catch (NumberFormatException e3) {
                                e3.printStackTrace();
                            }
                            GenieDebug.error("debug", "Start itrace: " + itrace);
                        }
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.error;
                        return false;
                    }
                    if (itrace == m_SmartInfo.trace && tag != null && tag.equals(String.format("%s.netrouter.SessionManagement.startSession", m_SmartInfo.workcpid))) {
                        String session = parser.getAttributeValue(null, "sessionId");
                        if (session != null) {
                            m_SmartInfo.sessionid = session;
                            GenieDebug.error("debug", "Start session: " + session);
                        }
                        RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
                    }
                } else if (eventType == 3) {
                    GenieDebug.error("debug", "End tag " + parser.getName());
                } else if (eventType == 4) {
                    GenieDebug.error("debug", "Text " + parser.getText());
                }
                eventType = parser.next();
                GenieDebug.error("debug", "eventType " + eventType);
            }
            if (itrace == m_SmartInfo.trace) {
                return true;
            }
            return false;
        } catch (Exception e4) {
            e4.printStackTrace();
            RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String BuildXml(ArrayList<XmlParameter> parameterlist) {
        String str = null;
        if (parameterlist == null) {
            return null;
        }
        StringWriter paramXml = new StringWriter();
        try {
            Xml.newSerializer();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer serializer = factory.newSerializer();
            serializer.setOutput(paramXml);
            Iterator<XmlParameter> it = parameterlist.iterator();
            while (it.hasNext()) {
                XmlParameter parameter = it.next();
                if (parameter.Tag != null) {
                    serializer.startTag(null, parameter.Tag);
                    if (parameter.attribute != null) {
                        Iterator<XmlAttribute> it2 = parameter.attribute.iterator();
                        while (it2.hasNext()) {
                            XmlAttribute attr = it2.next();
                            if (attr != null) {
                                serializer.attribute(null, attr.name, attr.value);
                            }
                        }
                    }
                    if (parameter.text != null) {
                        serializer.text(parameter.text);
                    }
                    if (parameter.child != null) {
                        serializer.flush();
                        paramXml.append((CharSequence) BuildXml(parameter.child));
                    }
                    serializer.endTag(null, parameter.Tag);
                }
            }
            serializer.flush();
            GenieDebug.error("debug", "BuildXml paramXml.toString() = " + paramXml.toString());
            str = paramXml.toString();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    private static String ipIntToString(int ip) {
        try {
            byte[] bytes = {(byte) (ip & MotionEventCompat.ACTION_MASK), (byte) ((65280 & ip) >> 8), (byte) ((16711680 & ip) >> 16), (byte) (((-16777216) & ip) >> 24)};
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public String getGateWay() {
        WifiManager test = (WifiManager) this.m_context.getSystemService("wifi");
        DhcpInfo dhcpInfo = test.getDhcpInfo();
        GenieDebug.error("debug", "gateway = " + ipIntToString(dhcpInfo.gateway));
        return ipIntToString(dhcpInfo.gateway);
    }

    public static String GetSmartNetworkUrl(Context context) {
        GenieDebug.error("debug", "GetSmartNetworkUrl ");
        SharedPreferences settings = context.getSharedPreferences("GENIESMART", 0);
        if (settings == null) {
            return SmartNetworkUrl;
        }
        String url = settings.getString("SMARTNETWORKURL", SmartNetworkUrl);
        GenieDebug.error("debug", "GetSmartNetworkUrl url = " + url);
        return url;
    }

    public static void SaveSmartNetworkUrl(Context context, String url) {
        SharedPreferences settings = context.getSharedPreferences("GENIESMART", 0);
        if (settings != null) {
            settings.edit().putString("SMARTNETWORKURL", url).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void trustAllHosts() {
        TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.dragonflow.GenieRequest.4
            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getSDKVersionNumber() {
        try {
            int sdkVersion = Build.VERSION.SDK_INT;
            return sdkVersion;
        } catch (Error e) {
            return 10;
        } catch (Exception e2) {
            return 10;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String escapeString(String value) {
        if (value != null) {
            return value.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&#38;", "&").replace("&#62;", ">").replace("&#60;", "<").replace("&#34;", "\"").replace("&#39;", "'").replace("&#40;", "(").replace("&#41;", ")").replace("&#35;", "#").replace("&#92;", "\\");
        }
        return value;
    }
}
