package com.acer.android_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import com.acer.android_services.LocalServiceClient;
import com.acer.aop.R;
import com.acer.aop.accounts.PartnerAuthenticator;
import com.acer.aop.debug.L;
import com.acer.aop.exception.AcerCloudException;
import com.acer.aop.exception.AcerCloudIOException;
import com.acer.aop.exception.AcerCloudUpdateException;
import com.acer.aop.util.AopErrorCodes;
import com.acer.aop.util.CcdSdkDefines;
import com.acer.aop.util.GlobalPreferencesManager;
import com.acer.aop.util.NetworkUtility;
import com.acer.aop.util.ProductUtils;
import com.acer.aop.util.SoftwareUpdateDefine;
import com.acer.aop.util.igware.Constants;
import com.acer.aop.util.igware.Dataset;
import com.acer.aop.util.igware.Utils;
import com.acer.aop.util.internal.Action;
import com.acer.aop.util.internal.InternalDefines;
import com.acer.aop.util.internal.Version;
import com.igware.android_services.ICcdiAidlRpc;
import com.igware.android_services.ICcdiServiceCallback;
import com.igware.android_services.IHttpServiceCallback;
import com.igware.android_services.IInternalCallback;
import igware.gvm.pb.CcdiRpc;
import igware.gvm.pb.CcdiRpcClient;
import igware.protobuf.AbstractByteArrayProtoChannel;
import igware.vplex.pb.VsDirectoryServiceTypes;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CcdiService extends Service {
    private static final String ACT_DEV_AOP_DOMAIN = "dev.aop.acer.com";
    private static final int APP_VERSION_ACER_PHOTO_2013_Q3 = 2320000;
    private static final String BROADCAST_SYNC_CONNECTION_SETTING_CHANGED = "com.android.sync.SYNC_CONN_STATUS_CHANGED";
    private static final int CHUNK_SIZE = 512000;
    private static final int DEQUEUE_TIMEOUT = -1;
    private static final int EVENT_TYPE_APP_UPGRADE_STATUS_CHANGE = 9;
    private static final int EVENT_TYPE_DATASET_CHANGE = 8;
    private static final int EVENT_TYPE_DATASET_CONTENT_CHANGE = 1;
    private static final int EVENT_TYPE_DEVICE_CONNECTION_CHANGE = 6;
    private static final int EVENT_TYPE_DEVICE_INFO_CHANGE = 10;
    private static final int EVENT_TYPE_HAS_USER_LOGIN_CHANGE = 4;
    private static final int EVENT_TYPE_HAS_USER_LOGOUT_CHANGE = 5;
    private static final int EVENT_TYPE_NONE = 0;
    private static final int EVENT_TYPE_PICSTREAM_DB_CHANGE = 7;
    private static final int EVENT_TYPE_STORAGE_NODE_CHANGE = 3;
    private static final int EVENT_TYPE_SYNC_FEATURE_STATUS_CHANGE = 2;
    private static final int GET_PSN_RETRY_COUNT = 20;
    private static final int GROUP_OFFLINE = 1;
    private static final int GROUP_ONLINE = 0;
    private static final int HTTPS_REQUEST_TIMEOUT = 60000;
    private static final String KEY_ERROR_CODE = "errCode";
    private static final String KEY_ERROR_MSG = "errMsg";
    private static final String KEY_PARTNER_ID = "partnerId";
    private static final String KEY_TITLE_ID = "titleId";
    private static final String LOG_TAG = "CcdiService";
    private static final int MAX_QUEUE_EVENT = 10;
    private static final int MESSAGE_DELAY_UNIT = 3000;
    private static final int MSG_CHECK_MY_DEVICE_STATE = 9;
    private static final int MSG_DO_LOGOUT_BY_PW_CHANGED = 6;
    private static final int MSG_PSN_CREATED = 8;
    private static final int MSG_PSN_DELETED = 7;
    private static final int MSG_SEND_OFFLINE_BROADCAST = 4;
    private static final int MSG_START_GET_MEDIA_SERVER = 3;
    public static final int MSG_STOP_SERVICE = 13;
    private static final int MSG_TRIGGER_EVENT_QUEUE_CALLBACK = 12;
    private static final int MSG_UPDATE_AUTO_SYNC_SETTINGS = 11;
    private static final int MSG_UPDATE_BACKGROUND_DATA_SYNC_SETTINGS = 10;
    private static final int MY_DEVICEID_DELAY = 60000;
    private static final int OFFLINE_DELAY = 5000;
    private static final int OFFLINE_NOTIFY_ID = 160;
    private static final String PREFIX_HTTPS = "https://";
    private static final int PSN_DELAY = 2000;
    private static final String WS_CHECKER_METHOD = "/ws/v1/validity";
    private static final String WS_CHECKER_SERVER_HOST = "dev.abeing.acer.com";
    private static final int WS_CHECKER_SERVER_PORT = 25238;
    private static RemoteCallbackList<ICcdiServiceCallback> mCallbacks;
    private static RemoteCallbackList<IInternalCallback> mInternalCallbacks;
    private static int sUpdateResult;
    private AopBroadcastReceiver mAopReceiver;
    private InnerBackgroundTask mBackgroundTask;
    private CcdBroadcastReceiver mCcdReceiver;
    private LocalServiceClient mClient;
    private NetworkUtility mNetworkUtility;
    private ArrayList<Thread> mThreadPool;
    private WifiBroadcastReceiver mWifiReceiver;
    private int mGetPsnRetryCnt = 0;
    private int mConnectionState = 2;
    private long mCloudPCId = -1;
    private String mCloudPCName = null;
    private PendingIntent mBackgroundKeepAliveIntent = null;
    private PendingIntent mCheckLifecycleControlIntent = null;
    private MediaObserver mMediaObserver = null;
    private Object mCloudPCInfoLocker = new Object();
    private Object mEventCallbackLocker = new Object();
    private Object mSendCredentialLocker = new Object();
    private Object mUpdatePowerModeLocker = new Object();
    private final VsDirectoryServiceTypes.DatasetDetail SYNCBOX_INITIAL = VsDirectoryServiceTypes.DatasetDetail.getDefaultInstance();
    private VsDirectoryServiceTypes.DatasetDetail mSyncBoxDataset = this.SYNCBOX_INITIAL;
    private boolean mIsWaitingToSendOffline = false;
    private boolean mServiceIsGone = false;
    private boolean mSkipLifecycleControlCheckingAlarm = false;
    private String mAppId = null;
    private CcdiRpc.CcdApp_t mAppType = CcdiRpc.CcdApp_t.CCD_APP_DEFAULT;
    private ArrayList<String> mQueuedExternalDCIM = null;
    private final CcdiAidlRpcImpl mBinder = new CcdiAidlRpcImpl(this);
    private boolean mIsSetCcdVersion = false;
    private Handler mHandler = new Handler() { // from class: com.acer.android_services.CcdiService.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            long myDeviceId;
            switch (msg.what) {
                case 3:
                    L.i(CcdiService.LOG_TAG, "handleMessage received : MSG_START_GET_MEDIA_SERVER");
                    if (!CcdiService.this.isGetMediaServerThreadAlive()) {
                        GetMediaServerThread mediaserverThread = new GetMediaServerThread(CcdiService.this.mClient);
                        mediaserverThread.start();
                        CcdiService.this.addThread(CcdiService.this.mThreadPool, mediaserverThread);
                    } else {
                        L.i(CcdiService.LOG_TAG, "GetMediaServerThread is running.");
                    }
                    if (!CcdiService.this.isGetPowerModeThreadAlive()) {
                        GetPowerModeThread getPowerModeThread = new GetPowerModeThread();
                        getPowerModeThread.start();
                        CcdiService.this.addThread(CcdiService.this.mThreadPool, getPowerModeThread);
                        return;
                    }
                    return;
                case 4:
                    L.i(CcdiService.LOG_TAG, "handleMessage received : MSG_SEND_OFFLINE_BROADCAST");
                    CcdiService.this.broadcastCurrentConnectionState();
                    CcdiService.this.mIsWaitingToSendOffline = false;
                    return;
                case 5:
                default:
                    return;
                case 6:
                    CcdiService.this.sendBroadcast(new Intent(CcdSdkDefines.ACTION_PASSWORD_IS_CHANGED));
                    return;
                case 7:
                    Intent intent = new Intent(CcdSdkDefines.ACTION_PSN_DELETED);
                    long id = ((Long) msg.obj).longValue();
                    intent.putExtra(CcdSdkDefines.EXTRA_CLOUDPC_ID, id);
                    CcdiService.this.sendBroadcast(intent);
                    L.i(CcdiService.LOG_TAG, "send a broadcast MSG_PSN_DELETED to apps, CloudPC id = " + id);
                    return;
                case 8:
                    Intent intent2 = new Intent(CcdSdkDefines.ACTION_PSN_CREATED);
                    long id2 = ((Long) msg.obj).longValue();
                    intent2.putExtra(CcdSdkDefines.EXTRA_CLOUDPC_ID, id2);
                    CcdiService.this.sendBroadcast(intent2);
                    L.i(CcdiService.LOG_TAG, "send a broadcast MSG_PSN_CREATED to apps, CloudPC id = " + id2);
                    return;
                case 9:
                    long userId = CcdiService.this.optGetUserId();
                    if (userId < 0) {
                        L.e(CcdiService.LOG_TAG, "GetCloudPCConnectStateThread() Error, cannot get correct userId");
                        return;
                    }
                    if (!InnerServiceUtils.hasMyDeviceId(CcdiService.this)) {
                        L.w(CcdiService.LOG_TAG, "The devices id is not exist in SharedPreferences, do getDeviceId()");
                        myDeviceId = CcdiService.this.mClient.getDeviceId();
                        if (myDeviceId > 0) {
                            InnerServiceUtils.setMyDeviceId(CcdiService.this, myDeviceId);
                        }
                    } else {
                        myDeviceId = InnerServiceUtils.getMyDeviceId(CcdiService.this);
                    }
                    L.i(CcdiService.LOG_TAG, "my device id = " + myDeviceId);
                    if (myDeviceId > 0) {
                        int state = CcdiService.this.mClient.getLinkedDeviceConnectionState(userId, myDeviceId, false, false, false);
                        L.i(CcdiService.LOG_TAG, "my device state = " + state);
                        return;
                    } else {
                        L.e(CcdiService.LOG_TAG, "My device ID is invalid.");
                        return;
                    }
                case 10:
                    boolean bgDataSettings = InnerServiceUtils.getBackgroundDataSettings(CcdiService.this);
                    CcdiService.this.mClient.updateBackgroundDataSyncSettings(bgDataSettings);
                    return;
                case 11:
                    CcdiService.this.mClient.updateAutoSyncSettings(ContentResolver.getMasterSyncAutomatically());
                    return;
                case 12:
                    new CreateEventCallbackTask(msg.arg1, (CcdiRpc.CcdiEvent) msg.obj).start();
                    return;
                case 13:
                    CcdiService.this.stopService();
                    return;
            }
        }
    };
    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver = null;

    static /* synthetic */ int access$4604(CcdiService x0) {
        int i = x0.mGetPsnRetryCnt + 1;
        x0.mGetPsnRetryCnt = i;
        return i;
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [com.acer.android_services.CcdiService$1] */
    @Override // android.app.Service
    public void onCreate() {
        L.i(LOG_TAG, "SDK version: " + CcdSdkDefines.getSdkVersion());
        super.onCreate();
        mCallbacks = new RemoteCallbackList<>();
        mInternalCallbacks = new RemoteCallbackList<>();
        this.mClient = new LocalServiceClient(getApplicationContext(), this.mBinder);
        this.mBackgroundTask = new InnerBackgroundTask(getApplicationContext(), this.mClient, this.mHandler);
        this.mNetworkUtility = new NetworkUtility(this);
        this.mThreadPool = new ArrayList<>();
        this.mCloudPCId = GlobalPreferencesManager.getLong(this, "cloud_pc_device_id", -1L);
        this.mConnectionState = GlobalPreferencesManager.getInt(this, "cloud_pc_device_connection_state", 2);
        this.mCloudPCName = GlobalPreferencesManager.getString(this, InnerServiceUtils.PREFERENCE_CLOUD_PC_DEVICE_NAME, null);
        startDequeueThread();
        new Thread() { // from class: com.acer.android_services.CcdiService.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (CcdiService.this.mClient.isLoggedIn()) {
                    CcdiService.this.registerMediaContentObserver();
                    CcdiService.this.reviseExternalPicStreamPath();
                    CcdiService.this.retrieveMediaServerInfo();
                    CcdiService.this.ensureMmThumbnailDownloadPath();
                    new UpdateConnectionAndSyncSettingsThread(CcdiService.this.mNetworkUtility).start();
                }
            }
        }.start();
        registerBroadcastReceiver();
        if (ProductUtils.isDeviceRegisterRetry(getApplicationContext())) {
            registerDevice();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        String appId;
        L.i(LOG_TAG, "start id " + startId + ": " + intent);
        if (intent != null && (appId = intent.getStringExtra("com.acer.ccd.extra.EXTRA_STRING_APP_ID")) != null) {
            this.mAppId = appId;
            this.mAppType = getAppType(intent.getIntExtra("com.acer.ccd.extra.EXTRA_BUNDLE_APP_TYPE", 0));
        }
        checkLifecycleControl();
        if (this.mBackgroundTask == null || !this.mBackgroundTask.onStartCommand(intent)) {
            if (intent != null) {
                boolean reportForeground = intent.getBooleanExtra(InternalDefines.EXTRA_BOOLEAN_REPORT_FOREGROUND, false);
                if (reportForeground) {
                    if (this.mAppId != null) {
                        new UpdateCcdPowerModeThread(intent, true).start();
                    } else {
                        L.w(LOG_TAG, "Intent extra did not carry app id, skip report power mode.");
                    }
                } else {
                    L.i(LOG_TAG, "Client side wouldn't request to report foreground.");
                    if (ServiceSingleton.getInstance(getApplicationContext()).onceReportForeground() && getAlivePokePowerModeToForegroundThread() == null) {
                        L.i(LOG_TAG, "force set forground mode once after CCDIService was started.");
                        PokePowerModeToForegroundThread t = new PokePowerModeToForegroundThread();
                        t.start();
                        this.mThreadPool.add(t);
                    }
                }
                ServiceSingleton.getInstance(getApplicationContext()).clearOnceReportForeground();
            } else {
                L.w(LOG_TAG, "intent is null because service is being restarted after its process has gone away");
            }
        }
        return 1;
    }

    private void onDestroy(boolean forceKillProcess) {
        if (this.mServiceIsGone) {
            L.w(LOG_TAG, "Due to onCreate() was skipped, exit onDestroy().");
            return;
        }
        this.mGetPsnRetryCnt = 0;
        stopBackgroundKeepAliveAlarm(this);
        unregisterMediaContentObserver();
        removeAllHandlerMessage();
        if (this.mCcdReceiver != null) {
            unregisterReceiver(this.mCcdReceiver);
            this.mCcdReceiver = null;
        }
        if (this.mAopReceiver != null) {
            unregisterReceiver(this.mAopReceiver);
            this.mAopReceiver = null;
        }
        if (this.mWifiReceiver != null) {
            unregisterReceiver(this.mWifiReceiver);
            this.mWifiReceiver = null;
        }
        if (this.mThreadPool != null) {
            destroyThread(this.mThreadPool);
            this.mThreadPool = null;
        }
        mCallbacks.kill();
        mInternalCallbacks.kill();
        if (!isBgSyncMode()) {
            setCcdToBackground();
            ServiceSingleton.getInstance(getApplicationContext()).stopService();
            if (forceKillProcess) {
                Process.killProcess(Process.myPid());
            }
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        L.i(LOG_TAG);
        stopCheckLifecycleControlAlarm();
        onDestroy(true);
    }

    private boolean isBgSyncMode() {
        boolean bgSyncMode = true;
        boolean usePortal = Utils.isUsePortal(getApplicationContext());
        if (!usePortal) {
            try {
                bgSyncMode = this.mClient.getBackgroundSyncMode();
            } catch (AcerCloudException e) {
                e.printStackTrace();
            }
        }
        L.i(LOG_TAG, "bgSyncMode: " + bgSyncMode + ", usePortal: " + usePortal);
        return bgSyncMode;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        L.i(LOG_TAG);
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        L.i(LOG_TAG);
        if (this.mServiceIsGone) {
            L.w(LOG_TAG, "Due to onCreate() was skipped, exit onUnbind().");
        } else if (!this.mClient.isLoggedIn()) {
            stopSelf();
        } else if (!isBgSyncMode()) {
            stopSelf();
        } else {
            UpdateCcdPowerModeThread t = new UpdateCcdPowerModeThread(intent, false);
            t.start();
        }
        return true;
    }

    /* loaded from: classes.dex */
    public static class CcdiAidlRpcImpl extends ICcdiAidlRpc.Stub {
        private CcdiService mService;
        private List<MetadataClone> mMetadataList = new ArrayList();
        private HttpServiceApi mHttpServiceApi = new HttpServiceApi();

        public CcdiAidlRpcImpl(CcdiService service) {
            this.mService = service;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public byte[] protoRpc(int pid, byte[] serializedRequest) {
            ensureCcdiLoaded();
            long a = System.currentTimeMillis();
            byte[] result = ServiceSingleton.ccdiJniProtoRpc(serializedRequest, false);
            long b = System.currentTimeMillis();
            L.i(CcdiService.LOG_TAG, " [PROFILING_CCD_RPC] after remote ccdiJniProtoRpc() time = " + (b - a) + " ms");
            if (CcdiService.CHUNK_SIZE < result.length) {
                boolean newPid = true;
                long a2 = System.currentTimeMillis();
                Iterator i$ = this.mMetadataList.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    MetadataClone clone = i$.next();
                    if (pid == clone.pid) {
                        clone.metadata = null;
                        clone.length = result.length;
                        clone.metadata = result;
                        newPid = false;
                        break;
                    }
                }
                if (newPid) {
                    L.i(CcdiService.LOG_TAG, "New Clone");
                    MetadataClone tmpClone = new MetadataClone();
                    tmpClone.pid = pid;
                    tmpClone.length = result.length;
                    tmpClone.metadata = result;
                    this.mMetadataList.add(tmpClone);
                }
                long b2 = System.currentTimeMillis();
                L.i(CcdiService.LOG_TAG, "after Clone time = " + (b2 - a2) + " ms");
                return null;
            }
            return result;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int getLength(int pid) {
            int length = 0;
            for (MetadataClone clone : this.mMetadataList) {
                if (pid == clone.pid) {
                    length = clone.length;
                }
            }
            return length;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public boolean isReady() {
            return ServiceSingleton.getInstance(this.mService).isReady();
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public byte[] getNext(int pid, int offset, int size) {
            L.i(CcdiService.LOG_TAG, "pid = " + pid + ", offset = " + offset + ", size = " + size);
            int eoc = size + offset;
            ByteArrayOutputStream chunkOut = new ByteArrayOutputStream();
            for (MetadataClone clone : this.mMetadataList) {
                if (pid == clone.pid) {
                    chunkOut.write(clone.metadata, offset, size);
                    if (eoc == clone.length) {
                        clone.length = 0;
                        clone.metadata = null;
                    }
                }
            }
            L.i(CcdiService.LOG_TAG, "getNext mChunkOut.size = " + chunkOut.size());
            return chunkOut.toByteArray();
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int createEventQueue(ICcdiServiceCallback callback) {
            Log.i(CcdiService.LOG_TAG, "create event queue from client");
            if (callback != null) {
                CcdiService.mCallbacks.register(callback);
                return 0;
            }
            return AopErrorCodes.CCD_ERROR_PARAMETER;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int destroyEventQueue(ICcdiServiceCallback callback) throws RemoteException {
            Log.i(CcdiService.LOG_TAG, "destroy event queue from client");
            if (callback != null) {
                CcdiService.mCallbacks.unregister(callback);
                return 0;
            }
            return AopErrorCodes.CCD_ERROR_PARAMETER;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public boolean deletePicStream(String filePath) throws RemoteException {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                return true;
            }
            L.e(CcdiService.LOG_TAG, filePath + " cannot be deleted.");
            return false;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public boolean rotatePicStream(String filePath, String newDegree) throws RemoteException {
            try {
                ExifInterface exif = new ExifInterface(filePath);
                exif.setAttribute("Orientation", newDegree);
                exif.saveAttributes();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                L.e(CcdiService.LOG_TAG, filePath + " cannot be updated.");
                return false;
            }
        }

        /* loaded from: classes.dex */
        private class LocalCcdiProtoChannel extends AbstractByteArrayProtoChannel {
            private LocalCcdiProtoChannel() {
            }

            @Override // igware.protobuf.AbstractByteArrayProtoChannel
            protected byte[] perform(byte[] requestBuf) {
                L.i(CcdiService.LOG_TAG, "local protoRpc() waiting for ccdi");
                CcdiAidlRpcImpl.this.ensureCcdiLoaded();
                L.i(CcdiService.LOG_TAG, "before local ccdiJniProtoRpc()");
                byte[] result = ServiceSingleton.ccdiJniProtoRpc(requestBuf, false);
                L.i(CcdiService.LOG_TAG, "after local ccdiJniProtoRpc()");
                return result;
            }
        }

        public CcdiRpcClient.CCDIServiceClient getLocalServiceClient() {
            return new CcdiRpcClient.CCDIServiceClient(new LocalCcdiProtoChannel(), true);
        }

        public void ensureCcdiLoaded() {
            while (!ServiceSingleton.getInstance(this.mService).isReady()) {
                try {
                    Thread.sleep(25L);
                } catch (InterruptedException e) {
                }
            }
        }

        /* loaded from: classes.dex */
        private class MetadataClone {
            public int length;
            public byte[] metadata;
            public int pid;

            private MetadataClone() {
            }
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int getSdkVersionCode() throws RemoteException {
            try {
                return Integer.parseInt(Version.AOP_SDK_VERSION_CODE);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public String getSdkVersionName() throws RemoteException {
            return Version.AOP_SDK_VERSION_NAME;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public long httpServiceCreate() throws RemoteException {
            return this.mHttpServiceApi.create();
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceDestroy(long handle) throws RemoteException {
            return this.mHttpServiceApi.destroy(handle);
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceCancelRequest(long handle) throws RemoteException {
            return this.mHttpServiceApi.cancelRequest(handle);
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceGet(long handle, String uri, String[] headers, byte[] body, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            int resultCode = this.mHttpServiceApi.get(handle, uri, headers, body, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServiceGet, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceGetStream(long hanlde, String uri, String[] headers, byte[] body, IHttpServiceCallback cb) throws RemoteException {
            HttpServiceGetCB hCb = new HttpServiceGetCB(cb);
            int resultCode = this.mHttpServiceApi.getStream(hanlde, uri, headers, body, hCb, cb);
            L.i(CcdiService.LOG_TAG, "httpServiceGetStream, resultCode: " + resultCode);
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceGetWithHttpHeaderResponse(long handle, String uri, String[] headers, byte[] body, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            int resultCode = this.mHttpServiceApi.getWithHttpHeaderResponse(handle, uri, headers, body, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServiceGet, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceGetStreamWithHttpHeaderResponse(long hanlde, String uri, String[] headers, byte[] body, IHttpServiceCallback cb) throws RemoteException {
            HttpServiceGetCB hCb = new HttpServiceGetCB(cb);
            int resultCode = this.mHttpServiceApi.getStreamWithHttpHeaderResponse(hanlde, uri, headers, body, hCb, cb);
            L.i(CcdiService.LOG_TAG, "httpServiceGetStream, resultCode: " + resultCode);
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServicePut(long handle, String uri, String[] headers, byte[] body, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            int resultCode = this.mHttpServiceApi.put(handle, uri, headers, body, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServicePut, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServicePutStream(long handle, String uri, String[] headers, IHttpServiceCallback cb, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            HttpServicePutCB hCb = new HttpServicePutCB(cb);
            int resultCode = this.mHttpServiceApi.putStream(handle, uri, headers, hCb, cb, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServicePutStream, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServicePost(long handle, String uri, String[] headers, byte[] body, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            int resultCode = this.mHttpServiceApi.post(handle, uri, headers, body, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServicePost, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServicePostStream(long handle, String uri, String[] headers, IHttpServiceCallback cb, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            HttpServicePutCB hCb = new HttpServicePutCB(cb);
            int resultCode = this.mHttpServiceApi.postStream(handle, uri, headers, hCb, cb, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServicePostStream, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int httpServiceDelete(long handle, String uri, String[] headers, byte[] body, String[] resp) throws RemoteException {
            if (resp == null) {
                resp = new String[1];
            }
            byte[][] respBytes = new byte[resp.length];
            int resultCode = this.mHttpServiceApi.delete(handle, uri, headers, body, respBytes);
            L.i(CcdiService.LOG_TAG, "httpServiceDelete, resultCode: " + resultCode);
            if (respBytes != null) {
                for (int i = 0; i < resp.length; i++) {
                    try {
                        resp[i] = new String(respBytes[i]);
                        L.i(CcdiService.LOG_TAG, "response: " + resp[i]);
                    } catch (Exception e) {
                    }
                }
            }
            return resultCode;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public int checkLifeCycleControl() throws RemoteException {
            int result = this.mService.checkLifecycleControl();
            L.i(CcdiService.LOG_TAG, "client requests to receive update result: " + result);
            return result;
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public void registerInternallCallback(IInternalCallback callback) throws RemoteException {
            if (callback != null) {
                CcdiService.mInternalCallbacks.register(callback);
            } else {
                L.e(CcdiService.LOG_TAG, "the given callback is null.");
            }
        }

        @Override // com.igware.android_services.ICcdiAidlRpc
        public void unregisterInternallCallback(IInternalCallback callback) throws RemoteException {
            if (callback != null) {
                CcdiService.mInternalCallbacks.unregister(callback);
            } else {
                L.e(CcdiService.LOG_TAG, "the given callback is null.");
            }
        }

        /* loaded from: classes.dex */
        private class HttpServiceGetCB implements HttpServiceCbInterface {
            private final IHttpServiceCallback mHttpServiceCb;

            public HttpServiceGetCB(IHttpServiceCallback cb) {
                this.mHttpServiceCb = cb;
            }

            @Override // com.acer.android_services.HttpServiceCbInterface
            public int cbFunc(Object context, ByteBuffer buf) {
                int consumed = 0;
                if (buf != null) {
                    int capacity = buf.capacity();
                    byte[] tempBuf = new byte[capacity];
                    buf.get(tempBuf);
                    consumed = 0;
                    while (consumed < capacity) {
                        try {
                            byte[] b = new byte[capacity - consumed];
                            System.arraycopy(tempBuf, consumed, b, 0, b.length);
                            int count = this.mHttpServiceCb.cbFunc(b);
                            consumed += count;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return consumed;
            }
        }

        /* loaded from: classes.dex */
        private class HttpServicePutCB implements HttpServiceCbInterface {
            private final IHttpServiceCallback mHttpServiceCb;

            public HttpServicePutCB(IHttpServiceCallback cb) {
                this.mHttpServiceCb = cb;
            }

            @Override // com.acer.android_services.HttpServiceCbInterface
            public int cbFunc(Object context, ByteBuffer buf) {
                if (buf == null) {
                    return 0;
                }
                try {
                    byte[] b = new byte[4096];
                    int produced = this.mHttpServiceCb.cbFunc(b);
                    if (produced > 0 && produced <= 4096) {
                        buf.put(b, 0, produced);
                        return produced;
                    }
                    return 0;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GetPowerModeThread extends Thread {
        private GetPowerModeThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            L.i(CcdiService.LOG_TAG, "GetPowerModeThread");
            CcdiService.this.updatePowerMode();
        }
    }

    /* loaded from: classes.dex */
    private class ForceBackgroundTaskThread extends Thread {
        private NetworkUtility mNetwork;

        public ForceBackgroundTaskThread(NetworkUtility netwuork) {
            this.mNetwork = null;
            this.mNetwork = netwuork;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (this.mNetwork == null) {
                L.e(CcdiService.LOG_TAG, "ForceBackgroundTaskThread.run() error, mNetwork is null, drop this activity");
                return;
            }
            boolean hasNetwork = this.mNetwork.isNetworkConnected();
            L.i(CcdiService.LOG_TAG, "ForceBackgroundTaskThread.run() hasNetwork = " + hasNetwork);
            if (hasNetwork) {
                CcdiService.this.mClient.forceBackgroundTCPPing();
            }
        }
    }

    /* loaded from: classes.dex */
    private class UpdateConnectionAndSyncSettingsThread extends Thread {
        NetworkUtility mNetwork;

        public UpdateConnectionAndSyncSettingsThread(NetworkUtility network) {
            this.mNetwork = null;
            this.mNetwork = network;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            CcdiService.this.mClient.updateAutoSyncSettings(ContentResolver.getMasterSyncAutomatically());
            if (this.mNetwork.isNetworkConnected()) {
                CcdiService.this.mClient.updateMobileNetworkState(this.mNetwork.is3GConnected(), false);
            }
            if (Build.VERSION.SDK_INT >= 14) {
                CcdiService.this.mClient.updateBackgroundDataSyncSettings(this.mNetwork.isActiveNetworkConnected());
            } else {
                boolean bgDataSettings = InnerServiceUtils.getBackgroundDataSettings(CcdiService.this.getApplicationContext());
                CcdiService.this.mClient.updateBackgroundDataSyncSettings(bgDataSettings);
            }
        }
    }

    /* loaded from: classes.dex */
    private class UpdateCcdPowerModeThread extends Thread {
        private Intent mIntent;
        private boolean mIsForeground;

        public UpdateCcdPowerModeThread(Intent intent, boolean isForeground) {
            this.mIsForeground = false;
            this.mIntent = intent;
            this.mIsForeground = isForeground;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            synchronized (CcdiService.this.mUpdatePowerModeLocker) {
                if (CcdiService.this.mClient.isLoggedIn()) {
                    if (!this.mIsForeground) {
                        CcdiService.this.setCcdToBackground();
                        CcdiService.this.mClient.updateStreamPowerMode(false);
                    } else if (this.mIntent != null) {
                        PokePowerModeToForegroundThread t = CcdiService.this.getAlivePokePowerModeToForegroundThread();
                        if (t != null) {
                            L.i(CcdiService.LOG_TAG, "found PokePowerModeToForegroundThread is still alive before app reports foreground.");
                            t.interrupt();
                        }
                        CcdiService.this.setCcdToForeground();
                    } else if (CcdiService.this.mClient.getPowerMode() == 3) {
                        CcdiService.this.startBackgroundKeepAliveAlarm(CcdiService.this);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PokePowerModeToForegroundThread extends Thread {
        private boolean mInterrupted;

        private PokePowerModeToForegroundThread() {
        }

        @Override // java.lang.Thread
        public void interrupt() {
            this.mInterrupted = true;
            super.interrupt();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            synchronized (CcdiService.this.mUpdatePowerModeLocker) {
                if (CcdiService.this.mClient.isLoggedIn()) {
                    int powerMode = CcdiService.this.mClient.getPowerMode();
                    if (powerMode != 2) {
                        CcdiService.this.setCcdToForeground();
                        if (!this.mInterrupted) {
                            CcdiService.this.setCcdToBackground();
                            CcdiService.this.startBackgroundKeepAliveAlarm(CcdiService.this);
                        } else {
                            Log.w(CcdiService.LOG_TAG, "Application has already requested to report foreground, won't set background back.");
                        }
                    } else {
                        L.i(CcdiService.LOG_TAG, "PokePowerModeToForegroundThread: power mode is already foreground, skip to poke power mode.");
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class CreateEventCallbackTask extends Thread {
        private CcdiRpc.CcdiEvent mEvent;
        private int mType;

        public CreateEventCallbackTask(int type, CcdiRpc.CcdiEvent event) {
            this.mEvent = null;
            this.mType = 0;
            this.mType = type;
            this.mEvent = event;
        }

        /* JADX WARN: Removed duplicated region for block: B:146:0x0414  */
        /* JADX WARN: Removed duplicated region for block: B:167:0x0493 A[Catch: all -> 0x005f, RemoteException -> 0x048d, TRY_ENTER, TryCatch #0 {RemoteException -> 0x048d, blocks: (B:16:0x004d, B:18:0x0062, B:20:0x0072, B:21:0x0080, B:26:0x0094, B:31:0x00b1, B:34:0x00c7, B:37:0x00f5, B:39:0x010f, B:41:0x011f, B:42:0x012d, B:44:0x013d, B:48:0x0155, B:52:0x019f, B:54:0x01af, B:55:0x01bd, B:58:0x01d4, B:61:0x01ef, B:63:0x01ff, B:64:0x020d, B:66:0x021d, B:67:0x022f, B:70:0x0244, B:74:0x0262, B:76:0x0272, B:77:0x0280, B:79:0x0290, B:83:0x02ac, B:85:0x02ba, B:87:0x02cd, B:90:0x02e8, B:92:0x02fe, B:93:0x0302, B:95:0x0308, B:98:0x031a, B:101:0x0320, B:104:0x032c, B:107:0x0354, B:119:0x0370, B:121:0x038e, B:123:0x03a4, B:124:0x03ac, B:126:0x03b2, B:129:0x03c4, B:132:0x03ca, B:133:0x03ce, B:135:0x03d4, B:138:0x03e6, B:144:0x03fa, B:147:0x0416, B:148:0x041a, B:150:0x0420, B:156:0x0438, B:164:0x045e, B:166:0x047e, B:167:0x0493, B:173:0x04a7, B:175:0x04c7, B:176:0x04d6, B:182:0x04ea, B:184:0x0514, B:185:0x0525, B:188:0x0533, B:193:0x054f, B:195:0x0596, B:205:0x05a5, B:208:0x05b9, B:210:0x05d1, B:212:0x05e1, B:213:0x05ef, B:215:0x05ff, B:219:0x0617, B:221:0x0625, B:222:0x0646), top: B:15:0x004d, outer: #1 }] */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 1672
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.acer.android_services.CcdiService.CreateEventCallbackTask.run():void");
        }
    }

    /* loaded from: classes.dex */
    private class SendUpdateCredentialBroadcastThread extends Thread {
        private int mOption;

        public SendUpdateCredentialBroadcastThread(int option) {
            this.mOption = 127;
            this.mOption = option;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            synchronized (CcdiService.this.mSendCredentialLocker) {
                long userId = CcdiService.this.mClient.getUserId();
                if (userId == -32238) {
                    L.w(CcdiService.LOG_TAG, "Cannot get user id, skip action");
                    return;
                }
                String filePath = CcdiService.this.getApplicationContext().getFilesDir().getAbsolutePath();
                File databaseFile = CcdiService.this.getApplicationContext().getDatabasePath(GlobalPreferencesManager.DB_FILE_NAME);
                File mainFile = new File(filePath.concat("/cc/cache/main.bin"));
                File userdataFile = new File(String.format("%s/cc/cache/users/%016x/userdata.bin", filePath, Long.valueOf(userId)));
                File deviceClearFile = new File(filePath.concat("/cc/device/dev_cred_clear"));
                File deviceSecretFile = new File(filePath.concat("/cc/device/dev_cred_secret"));
                File deviceIdFile = new File(filePath.concat("/cc/device/deviceId"));
                File deviceTokenFile = new File(filePath.concat("/cc/device/renewal_token"));
                String permission = CcdiService.this.getString(R.string.aop_data_sync_permission);
                if (!TextUtils.isEmpty(permission)) {
                    Intent broadcastIntent = new Intent(Action.ACITON_UPDATE_CREDENTIAL);
                    broadcastIntent.putExtra(Action.EXTRA_RCS_PACKAGE_NAME, CcdiService.this.getApplicationInfo().packageName);
                    broadcastIntent.putExtra(Action.EXTRA_MIGRATE_OPTION, 127);
                    broadcastIntent.putExtra(Action.EXTRA_USER_ID, userId);
                    checkFileAndAttachToIntent(2, broadcastIntent, Action.EXTRA_CREDENTIAL_MAIN, mainFile);
                    checkFileAndAttachToIntent(1, broadcastIntent, Action.EXTRA_CREDENTIAL_USERDATA, userdataFile);
                    checkFileAndAttachToIntent(4, broadcastIntent, Action.EXTRA_CREDENTIAL_DEVICE_CLEAR, deviceClearFile);
                    checkFileAndAttachToIntent(8, broadcastIntent, Action.EXTRA_CREDENTIAL_DEVICE_SECRET, deviceSecretFile);
                    checkFileAndAttachToIntent(16, broadcastIntent, Action.EXTRA_CREDENTIAL_DEVICE_ID, deviceIdFile);
                    checkFileAndAttachToIntent(32, broadcastIntent, Action.EXTRA_CREDENTIAL_DEVICE_TOKEN, deviceTokenFile);
                    checkFileAndAttachToIntent(64, broadcastIntent, Action.EXTRA_PREFERENCE_DATABASE, databaseFile);
                    CcdiService.this.sendBroadcast(broadcastIntent, permission);
                    L.i(CcdiService.LOG_TAG, "update credential broadcast was sent.");
                }
            }
        }

        private void checkFileAndAttachToIntent(int fileOption, Intent intent, String extra, File file) {
            if ((this.mOption & fileOption) == fileOption) {
                if (file.exists()) {
                    intent.putExtra(extra, convertFileToByteArray(file));
                } else {
                    L.w(CcdiService.LOG_TAG, file.getAbsolutePath() + " doesn't exist.");
                }
            }
        }

        private byte[] convertFileToByteArray(File file) {
            L.e(CcdiService.LOG_TAG, "convert to byte: " + file.getAbsolutePath());
            byte[] result = new byte[(int) file.length()];
            L.e(CcdiService.LOG_TAG, "byte length: " + result.length);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    fileInputStream.read(result);
                    fileInputStream.close();
                } catch (Exception e) {
                    e = e;
                    e.printStackTrace();
                    return result;
                }
            } catch (Exception e2) {
                e = e2;
            }
            return result;
        }
    }

    /* loaded from: classes.dex */
    private class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
        public HttpGetWithEntity() {
        }

        public HttpGetWithEntity(URI uri) {
            setURI(uri);
        }

        public HttpGetWithEntity(String uri) {
            setURI(URI.create(uri));
        }

        @Override // org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
        public String getMethod() {
            return HttpGet.METHOD_NAME;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CustomSSLSocketFactory extends SSLSocketFactory {
        SSLContext mSSLContext;

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
        public Socket createSocket() throws IOException {
            return this.mSSLContext.getSocketFactory().createSocket();
        }

        @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return this.mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        public CustomSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            this.mSSLContext = SSLContext.getInstance("TLSv1.2");
            TrustManager mTrustManager = new X509TrustManager() { // from class: com.acer.android_services.CcdiService.CustomSSLSocketFactory.1
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
            this.mSSLContext.init(null, new TrustManager[]{mTrustManager}, null);
        }
    }

    /* loaded from: classes.dex */
    public class CcdBroadcastReceiver extends BroadcastReceiver {
        public CcdBroadcastReceiver() {
        }

        /* JADX WARN: Type inference failed for: r2v40, types: [com.acer.android_services.CcdiService$CcdBroadcastReceiver$2] */
        /* JADX WARN: Type inference failed for: r2v48, types: [com.acer.android_services.CcdiService$CcdBroadcastReceiver$1] */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                L.i(CcdiService.LOG_TAG, "onReceive: action = " + action);
                if (action != null) {
                    if (action.equals(CcdSdkDefines.ACTION_POST_PROCESS_ADD_ACCOUNT)) {
                        CcdiService.this.startDequeueThread();
                        CcdiService.this.retrieveMediaServerInfo();
                        CcdiService.this.ensureMmThumbnailDownloadPath();
                        return;
                    }
                    if (action.equals(CcdSdkDefines.ACTION_RETRIEVE_PSN_INFO)) {
                        CcdiService.this.retrieveMediaServerInfo();
                        return;
                    }
                    if (action.equals(CcdSdkDefines.BROADCAST_MESASGE_SEND_DEVICE_STATE_DELAYED)) {
                        CcdiService.this.mHandler.removeMessages(9);
                        new Thread() { // from class: com.acer.android_services.CcdiService.CcdBroadcastReceiver.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                if (CcdiService.this.mClient.getPowerMode() != 1 && CcdiService.this.mNetworkUtility.isWalledGardenConnection()) {
                                    L.e(CcdiService.LOG_TAG, "isWalledGardenConnection = false, send delayed message");
                                    CcdiService.this.mHandler.sendEmptyMessageDelayed(9, 60000L);
                                } else {
                                    L.e(CcdiService.LOG_TAG, "isWalledGardenConnection = true or power mode is no_sync, do not send delayed message");
                                }
                            }
                        }.start();
                        return;
                    }
                    if (action.equals("com.acer.ccd.BROADCAST_MESASGE_REMOVE_DEVICE_STATE_DELAYED")) {
                        CcdiService.this.mHandler.removeMessages(9);
                        return;
                    }
                    if (action.equals(InnerServiceUtils.BROADCAST_MESSAGE_BACKGROUND_KEEPALIVE)) {
                        new ForceBackgroundTaskThread(CcdiService.this.mNetworkUtility).start();
                        return;
                    }
                    if (action.equals(InnerServiceUtils.BROADCAST_MESSAGE_REGISTRATION_RETRY)) {
                        CcdiService.this.registerDevice();
                        return;
                    }
                    if (action.equals(InnerServiceUtils.BROADCAST_MESSAGE_CHECK_LIFECYCLE_CONTROL)) {
                        if (CcdiService.this.mSkipLifecycleControlCheckingAlarm) {
                            CcdiService.this.mSkipLifecycleControlCheckingAlarm = false;
                            return;
                        } else {
                            L.i(CcdiService.LOG_TAG, "repeated checking lifecycle control.");
                            new Thread() { // from class: com.acer.android_services.CcdiService.CcdBroadcastReceiver.2
                                @Override // java.lang.Thread, java.lang.Runnable
                                public void run() {
                                    int result = CcdiService.this.checkLifecycleControl();
                                    CcdiService.this.sendCheckResultToClient(result);
                                }
                            }.start();
                            return;
                        }
                    }
                    if (action.equals("android.net.conn.BACKGROUND_DATA_SETTING_CHANGED")) {
                        CcdiService.this.mHandler.removeMessages(10);
                        CcdiService.this.mHandler.sendEmptyMessage(10);
                        return;
                    }
                    if (action.equals(CcdiService.BROADCAST_SYNC_CONNECTION_SETTING_CHANGED)) {
                        CcdiService.this.mHandler.removeMessages(11);
                        CcdiService.this.mHandler.sendEmptyMessage(11);
                        return;
                    }
                    if (action.equals(CcdSdkDefines.ACTION_UPDATE_POWER_MODE)) {
                        GetPowerModeThread getPowerModeThread = new GetPowerModeThread();
                        getPowerModeThread.start();
                        CcdiService.this.addThread(CcdiService.this.mThreadPool, getPowerModeThread);
                    } else if (intent.getAction().equals(InternalDefines.ACTION_START_CONTENT_OBSERVER)) {
                        CcdiService.this.registerMediaContentObserver();
                    } else if (intent.getAction().equals(InternalDefines.ACTION_STOP_CONTENT_OBSERVER)) {
                        CcdiService.this.unregisterMediaContentObserver();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.acer.android_services.CcdiService$2] */
    public void registerDevice() {
        new Thread() { // from class: com.acer.android_services.CcdiService.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                CcdiService.this.mBinder.ensureCcdiLoaded();
                LocalServiceClient.UserProfile profile = null;
                try {
                    profile = CcdiService.this.mClient.getUserProfile();
                } catch (AcerCloudException e) {
                    e.printStackTrace();
                }
                if (profile != null) {
                    ProductUtils.registerDevice(CcdiService.this.getApplicationContext(), profile.userEmail, "", profile.firstName, profile.lastName, profile.countryCode, profile.language, true, false, false);
                }
            }
        }.start();
    }

    /* loaded from: classes.dex */
    public class AopBroadcastReceiver extends BroadcastReceiver {
        public AopBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                L.i(CcdiService.LOG_TAG, "onReceive: action = " + action);
                if (action != null) {
                    if (action.equals(Action.ACTION_KILL_RCS_PROCESS)) {
                        String packageName = intent.getStringExtra(Action.EXTRA_BEST_RCS_PACKAGE_NAME);
                        L.i(CcdiService.LOG_TAG, "rcs = " + packageName + ", local pack name = " + context.getPackageName());
                        if (packageName != null && packageName.length() > 0 && !packageName.equalsIgnoreCase(context.getPackageName())) {
                            L.i(CcdiService.LOG_TAG, "ready to kill myself");
                            Process.killProcess(Process.myPid());
                            return;
                        }
                        return;
                    }
                    if (action.equals(Action.ACTION_REQUEST_CREDENTIAL)) {
                        if (CcdiService.this.getApplicationInfo().packageName.equals(intent.getStringExtra(Action.EXTRA_RCS_PACKAGE_NAME))) {
                            CcdiService.this.broadcastCredentialChanged(intent.getIntExtra(Action.EXTRA_MIGRATE_OPTION, 127));
                        } else {
                            L.w(CcdiService.LOG_TAG, "Not target RCS, skip action");
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        private WifiBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.STATE_CHANGE") && CcdiService.this.mNetworkUtility.isWifiConnected()) {
                Intent serviceIntent = new Intent();
                serviceIntent.putExtra(Action.EXTRA_BACKGROND_SERV_ACTION_TYPE, Action.ACTION_TYPE_EVENT_REPORT);
                CcdiService.this.mBackgroundTask.onStartCommand(serviceIntent);
            }
        }
    }

    private CcdiRpc.CcdApp_t getAppType(int appTypeValue) {
        CcdiRpc.CcdApp_t type = CcdiRpc.CcdApp_t.CCD_APP_DEFAULT;
        switch (appTypeValue) {
            case 1:
                type = CcdiRpc.CcdApp_t.CCD_APP_PHOTO;
                break;
            case 2:
                type = CcdiRpc.CcdApp_t.CCD_APP_MUSIC;
                break;
            case 3:
                type = CcdiRpc.CcdApp_t.CCD_APP_VIDEO;
                break;
            case 5:
                type = CcdiRpc.CcdApp_t.CCD_APP_ALL_MEDIA;
                break;
        }
        L.i(LOG_TAG, "appTypeValue = " + appTypeValue + ", type = " + type);
        return type;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCcdToForeground() {
        if (this.mAppId == null) {
            L.e(LOG_TAG, "no appId carried with intent, CcdiServices should be restarted for some reasons. Ignore this request");
            return;
        }
        int result = this.mClient.updateAppState(this.mAppId, this.mAppType, true);
        if (result == 0) {
            L.v(LOG_TAG, "CCD set to foreground, Disable the ALARM which trigger updateSystemState(), appId = " + this.mAppId);
            stopBackgroundKeepAliveAlarm(this);
        }
        L.w(LOG_TAG, "appId: " + this.mAppId + ", appType: " + this.mAppType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCcdToBackground() {
        if (this.mAppId != null) {
            int result = this.mClient.updateAppState(this.mAppId, this.mAppType, false);
            if (result == 0) {
                L.v(LOG_TAG, "setCcdToBackground() CCD set to background, start a ALARM to trigger updateSystemState(), appId = " + this.mAppId);
                startBackgroundKeepAliveAlarm(this);
                return;
            }
            return;
        }
        L.w(LOG_TAG, "mAppId is null, skip to set background");
    }

    private void removeAllHandlerMessage() {
        if (this.mHandler != null) {
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(6);
            this.mHandler.removeMessages(8);
            this.mHandler.removeMessages(7);
            this.mHandler.removeMessages(4);
            this.mHandler.removeMessages(9);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBackgroundKeepAliveAlarm(Context context) {
        AlarmManager alarmManager;
        if (this.mBackgroundKeepAliveIntent == null && (alarmManager = (AlarmManager) context.getSystemService("alarm")) != null) {
            Intent intent = new Intent(InnerServiceUtils.BROADCAST_MESSAGE_BACKGROUND_KEEPALIVE);
            this.mBackgroundKeepAliveIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmManager.setInexactRepeating(3, SystemClock.elapsedRealtime(), 900000L, this.mBackgroundKeepAliveIntent);
        }
    }

    private void stopBackgroundKeepAliveAlarm(Context context) {
        AlarmManager alarmManager;
        if (this.mBackgroundKeepAliveIntent != null && (alarmManager = (AlarmManager) context.getSystemService("alarm")) != null) {
            alarmManager.cancel(this.mBackgroundKeepAliveIntent);
            this.mBackgroundKeepAliveIntent = null;
        }
    }

    private void startCheckLifecycleControlAlarm() {
        AlarmManager alarmManager;
        if (this.mCheckLifecycleControlIntent == null && (alarmManager = (AlarmManager) getSystemService("alarm")) != null) {
            this.mSkipLifecycleControlCheckingAlarm = true;
            Intent intent = new Intent(InnerServiceUtils.BROADCAST_MESSAGE_CHECK_LIFECYCLE_CONTROL);
            this.mCheckLifecycleControlIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            alarmManager.setInexactRepeating(3, SystemClock.elapsedRealtime(), 86400000L, this.mCheckLifecycleControlIntent);
        }
    }

    private void stopCheckLifecycleControlAlarm() {
        AlarmManager alarmManager;
        if (this.mCheckLifecycleControlIntent != null && (alarmManager = (AlarmManager) getSystemService("alarm")) != null) {
            alarmManager.cancel(this.mCheckLifecycleControlIntent);
            this.mCheckLifecycleControlIntent = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeSharedPreference() {
        String[] lastUserInformation = {InternalDefines.PREFERENCE_LOGIN_ACCOUNT_NAME, InternalDefines.PREFERENCE_LOGIN_ACCOUNT_TYPE, InternalDefines.PREFERENCE_LOGIN_ACCOUNT_EMAIL};
        GlobalPreferencesManager.removeAllPreferences(this, lastUserInformation);
        this.mCloudPCId = -1L;
    }

    private void removePSNSharedPreferences() {
        GlobalPreferencesManager.remove(this, "cloud_pc_device_id");
        GlobalPreferencesManager.remove(this, InnerServiceUtils.PREFERENCE_CLOUD_PC_DEVICE_NAME);
        this.mCloudPCId = -1L;
    }

    private long getPicStreamDatasetId() {
        long pcsDatasetId = GlobalPreferencesManager.getLong(this, InnerServiceUtils.PREFERENCE_PICSTREAM_DATASET_ID, -32236L);
        if (pcsDatasetId == -32236) {
            long userId = optGetUserId();
            Dataset[] syncFoldersArray = this.mClient.listOwnedDatasets(userId, true);
            if (syncFoldersArray != null) {
                int len$ = syncFoldersArray.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        break;
                    }
                    Dataset item = syncFoldersArray[i$];
                    if (!item.getName().equals(Constants.DATASET_PICSTREAM)) {
                        i$++;
                    } else {
                        pcsDatasetId = item.getDatasetId();
                        GlobalPreferencesManager.putLong(this, InnerServiceUtils.PREFERENCE_PICSTREAM_DATASET_ID, pcsDatasetId);
                        break;
                    }
                }
            }
        }
        L.i(LOG_TAG, "getCameraRollDatasetId() pcsDatasetId = " + pcsDatasetId);
        return pcsDatasetId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getCloudFilesDatasetId() {
        Dataset[] cloudfilesArray = this.mClient.listOwnedDatasets(optGetUserId(), true);
        if (cloudfilesArray == null) {
            return -32236L;
        }
        for (Dataset item : cloudfilesArray) {
            if (item.getName().equalsIgnoreCase(Constants.DATASET_CLOUD_FILES)) {
                long cloudFilesDatasetId = item.getDatasetId();
                return cloudFilesDatasetId;
            }
        }
        return -32236L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getCloudDocsDatasetId() {
        Dataset[] syncFoldersArray = this.mClient.listOwnedDatasets(optGetUserId(), true);
        if (syncFoldersArray == null) {
            return -32236L;
        }
        for (Dataset item : syncFoldersArray) {
            if (item.getName().equalsIgnoreCase(Constants.DATASET_CLOUD_DOCS) || item.getName().equalsIgnoreCase("Doc Save and Go")) {
                long docsDatasetId = item.getDatasetId();
                return docsDatasetId;
            }
        }
        return -32236L;
    }

    private long getMediaMetadataDatasetId() {
        Dataset[] syncFoldersArray = this.mClient.listOwnedDatasets(optGetUserId(), true);
        if (syncFoldersArray == null) {
            return -32236L;
        }
        for (Dataset item : syncFoldersArray) {
            if (item.getName().equals(Constants.DATASET_MEDIA_METADATA)) {
                long mediaDatasetId = item.getDatasetId();
                return mediaDatasetId;
            }
        }
        return -32236L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getMediaServerName(long serverId) {
        List<CcdiRpc.LinkedDeviceInfo> nodes = new ArrayList<>();
        int ret = this.mClient.getLinkedDevices(optGetUserId(), nodes, true);
        if (ret >= 0 && nodes != null && nodes.size() > 0) {
            for (CcdiRpc.LinkedDeviceInfo device : nodes) {
                if (device.getDeviceId() == serverId) {
                    return device.getDeviceName();
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean ensureCloudDataValid() {
        long userId = GlobalPreferencesManager.getLong(this, "cloud_user_id", -32238L);
        this.mCloudPCId = GlobalPreferencesManager.getLong(this, "cloud_pc_device_id", -1L);
        this.mCloudPCName = GlobalPreferencesManager.getString(this, InnerServiceUtils.PREFERENCE_CLOUD_PC_DEVICE_NAME, null);
        L.v(LOG_TAG, "ensureCloudDataValid() id = " + this.mCloudPCId + ", state = " + this.mConnectionState + ", userId = " + userId + ", name = " + this.mCloudPCName);
        return (this.mCloudPCId == -1 || userId == -32238 || this.mCloudPCName == null) ? false : true;
    }

    private boolean isMusicInstalled() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageinfo : packages) {
            if (packageinfo.packageName.equals("com.acer.c5music")) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int updatePowerMode() {
        int powerMode = this.mClient.getPowerMode();
        L.i(LOG_TAG, "updatePowerMode: save power mode to shared preference, powerMode = " + powerMode);
        GlobalPreferencesManager.putInt(this, "power_mode_state", powerMode);
        return powerMode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int mapStorageNodeChangeType(int type) {
        if (type == 1) {
            return 1;
        }
        if (type == 2) {
            return 2;
        }
        return type == 3 ? 3 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int mapDeviceChangeType(int type) {
        if (type == 1) {
            return 1;
        }
        if (type == 2) {
            return 2;
        }
        return type == 3 ? 3 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GetMediaServerThread extends Thread {
        private LocalServiceClient mServiceClient;

        public GetMediaServerThread(LocalServiceClient client) {
            this.mServiceClient = client;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            L.i(CcdiService.LOG_TAG, "GetMediaServerThread() Start");
            synchronized (CcdiService.this.mCloudPCInfoLocker) {
                if (!CcdiService.this.ensureCloudDataValid()) {
                    long userId = CcdiService.this.optGetUserId();
                    List<VsDirectoryServiceTypes.UserStorage> userStorageNodes = new ArrayList<>();
                    int ret = this.mServiceClient.getMediaServer(userId, userStorageNodes, true);
                    if (ret >= 0) {
                        if (userStorageNodes != null && userStorageNodes.size() > 0) {
                            boolean isConnected = true;
                            long cloudPCId = userStorageNodes.get(0).getStorageClusterId();
                            String cloudPCName = CcdiService.this.getMediaServerName(cloudPCId);
                            L.i(CcdiService.LOG_TAG, "GetMediaServerThread() Retrieve MediaServer info. cloudPCId = " + cloudPCId + ", mCloudPCId = " + CcdiService.this.mCloudPCId);
                            if (CcdiService.this.mCloudPCId != cloudPCId) {
                                CcdiService.this.mCloudPCId = cloudPCId;
                            }
                            if (CcdiService.this.mCloudPCName == null || !CcdiService.this.mCloudPCName.equalsIgnoreCase(cloudPCName)) {
                                CcdiService.this.mCloudPCName = cloudPCName;
                            }
                            GlobalPreferencesManager.putLong(CcdiService.this, "cloud_pc_device_id", CcdiService.this.mCloudPCId);
                            GlobalPreferencesManager.putString(CcdiService.this, InnerServiceUtils.PREFERENCE_CLOUD_PC_DEVICE_NAME, CcdiService.this.mCloudPCName);
                            if (CcdiService.this.mCloudPCId != -1 && (isConnected = CcdiService.this.mNetworkUtility.isNetworkConnected())) {
                                List<CcdiRpc.LinkedDeviceInfo> nodes = new ArrayList<>();
                                if (this.mServiceClient.getLinkedDevices(userId, nodes, true) >= 0 && nodes != null && nodes.size() > 0) {
                                    for (CcdiRpc.LinkedDeviceInfo node : nodes) {
                                        if (node.hasDeviceId() && node.getDeviceId() == CcdiService.this.mCloudPCId) {
                                            int state = nodes.get(0).getConnectionStatus().getState().getNumber();
                                            boolean isUpdating = nodes.get(0).getConnectionStatus().getUpdating();
                                            if (!isUpdating) {
                                                if (CcdiService.this.mConnectionState != state) {
                                                    CcdiService.this.mConnectionState = state;
                                                }
                                                GlobalPreferencesManager.putInt(CcdiService.this, "cloud_pc_device_connection_state", CcdiService.this.mConnectionState);
                                            } else {
                                                L.i(CcdiService.LOG_TAG, "GetMediaServerThread() cloud node is updating, skip getting connection state and waiting for event message to update cloud pc connection state.");
                                            }
                                        } else {
                                            L.w(CcdiService.LOG_TAG, "GetMediaServerThread() A cloud node is found, but it is not major cloud node. id: " + node.getDeviceId());
                                        }
                                    }
                                }
                            }
                            L.i(CcdiService.LOG_TAG, "GetMediaServerThread() Retrieve MediaServer info and state. id = " + CcdiService.this.mCloudPCId + ", state = " + CcdiService.this.mConnectionState + ", isConnected = " + isConnected);
                            return;
                        }
                        if (CcdiService.this.mCloudPCId != -1) {
                            CcdiService.this.removeMediaServer(CcdiService.this.mCloudPCId);
                        }
                    } else if (ret == -14118) {
                        L.e(CcdiService.LOG_TAG, "GetMediaServerThread() get storage node failed due to errCode -14118, may be cuased by losing credential, stop polling. retry count = " + CcdiService.this.mGetPsnRetryCnt);
                        return;
                    }
                    L.i(CcdiService.LOG_TAG, "GetMediaServerThread() get storage node failed! try again after 3 seconds. retry count = " + CcdiService.this.mGetPsnRetryCnt);
                    if (CcdiService.access$4604(CcdiService.this) <= 20) {
                        CcdiService.this.mHandler.sendEmptyMessageDelayed(3, 3000L);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DeQueueThread extends Thread {
        public long mHandle;
        private boolean mIsInterrupted;

        private DeQueueThread() {
            this.mIsInterrupted = false;
            this.mHandle = 0L;
        }

        @Override // java.lang.Thread
        public void interrupt() {
            this.mIsInterrupted = true;
            super.interrupt();
        }

        @Override // java.lang.Thread
        public boolean isInterrupted() {
            return this.mIsInterrupted;
        }

        /* JADX WARN: Code restructure failed: missing block: B:244:0x02ca, code lost:
        
            continue;
         */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 2370
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.acer.android_services.CcdiService.DeQueueThread.run():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAcerPhotoCompatible() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(SoftwareUpdateDefine.PACKAGENAME_CLEARFI_PHOTO, 1);
            int versionCode = pi.versionCode;
            if (versionCode >= APP_VERSION_ACER_PHOTO_2013_Q3) {
                L.i(LOG_TAG, "isAcerPhotoCompatible() applications are compatible! versionCode = " + versionCode);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            L.e(LOG_TAG, "isAcerPhotoCompatible() Acer Photo is not installed, skip the event");
            e.printStackTrace();
        }
        L.i(LOG_TAG, "isAcerPhotoCompatible() applications are incompatible! skip the SYNCING event");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeMediaServer(long cloudPCId) {
        L.i(LOG_TAG, "removeMediaServer() cloudPCId = " + cloudPCId);
        this.mHandler.removeMessages(7);
        Message msg = this.mHandler.obtainMessage(7, Long.valueOf(cloudPCId));
        this.mHandler.sendMessageDelayed(msg, 2000L);
        removePSNSharedPreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectionState() {
        long userId = optGetUserId();
        int state = this.mClient.getLinkedDeviceConnectionState(userId, this.mCloudPCId, true, true, true);
        int currentGroup = getStatusGroupNumber(this.mConnectionState);
        int newGroup = getStatusGroupNumber(state);
        if (state != 4) {
            this.mConnectionState = state;
            GlobalPreferencesManager.putInt(this, "cloud_pc_device_connection_state", this.mConnectionState);
            GlobalPreferencesManager.putLong(this, "cloud_user_id", userId);
            if (newGroup == 0 && currentGroup != newGroup) {
                broadcastCurrentConnectionState();
                return;
            }
            return;
        }
        L.w(LOG_TAG, "state is unknown, skip update");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startMusicSyncService() {
        Intent musicIntent = new Intent(InnerServiceUtils.MUSIC_MEDIA_SYNC_INTENT_FILTER);
        musicIntent.setPackage("com.acer.c5music");
        musicIntent.putExtra(CcdSdkDefines.EXTRA_SYNC_ACTION, 0);
        startService(musicIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startVideoSyncService() {
        Intent videoIntent = new Intent(InnerServiceUtils.VIDEO_MEDIA_SYNC_INTENT_FILTER);
        videoIntent.setPackage(SoftwareUpdateDefine.PACKAGENAME_CLEARFI_VIDEO);
        videoIntent.putExtra(CcdSdkDefines.EXTRA_SYNC_ACTION, 0);
        startService(videoIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPhotoSyncService() {
        Intent photoIntent = new Intent(InnerServiceUtils.PHOTO_MEDIA_SYNC_INTENT_FILTER);
        photoIntent.setPackage(SoftwareUpdateDefine.PACKAGENAME_CLEARFI_PHOTO);
        photoIntent.putExtra(CcdSdkDefines.EXTRA_SYNC_ACTION, 0);
        startService(photoIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlaylistSyncService() {
        if (isMusicInstalled()) {
            Intent serviceIntent = new Intent();
            serviceIntent.setPackage("com.acer.c5music");
            serviceIntent.putExtra(Action.EXTRA_BACKGROND_SERV_ACTION_TYPE, 2);
            this.mBackgroundTask.onStartCommand(serviceIntent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDequeueThread() {
        boolean hasDequeueThread = isDeQueueTheadAlive();
        L.i(LOG_TAG, "hasDequeueThread = " + hasDequeueThread);
        if (!hasDequeueThread) {
            L.i(LOG_TAG, "starting DeQueueThread!");
            DeQueueThread dequeueThread = new DeQueueThread();
            dequeueThread.start();
            addThread(this.mThreadPool, dequeueThread);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastCredentialChanged(int option) {
        L.i(LOG_TAG, "rcs is not enabled, skip this request");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastCurrentConnectionState() {
        Intent intent = new Intent(CcdSdkDefines.ACTION_CLOUDPC_CONNECTION_STATE_CHANGED);
        intent.putExtra(CcdSdkDefines.EXTRA_CLOUDPC_CONNECTION_STATE, this.mConnectionState);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastDeviceConnectionState(long deviceId, int state) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_DEVICE_CONNECTION_STATE_CHANGED);
        intent.putExtra(CcdSdkDefines.EXTRA_CLOUD_DEVICE_ID, deviceId);
        intent.putExtra(CcdSdkDefines.EXTRA_DEVICE_CONNECTION_STATE, state);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastCurrentPowerMode(int powermode) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_POWER_MODE_CHANGED);
        intent.putExtra(CcdSdkDefines.EXTRA_POWER_MODE, powermode);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastAsyncUploadCompletion(long reqId, int status) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_ASYNC_UPLOAD_COMPLETION);
        intent.putExtra(CcdSdkDefines.EXTRA_ASYNC_FILE_TRANS_OPERATION_REQ_ID, reqId);
        intent.putExtra(CcdSdkDefines.EXTRA_ASYNC_FILE_TRANS_OPERATION_STATUS, mapAsyncUploadStatus(status));
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastDatasetChange(List<Long> datasetList) {
        long[] list = null;
        if (datasetList != null && datasetList.size() > 0) {
            int size = datasetList.size();
            list = new long[size];
            for (int i = 0; i < size; i++) {
                list[i] = datasetList.get(i).longValue();
            }
        }
        Intent intent = new Intent(Action.AsyncEventAction.ACTION_DATASET_CHANGE);
        intent.putExtra(CcdSdkDefines.EXTRA_DATASET_IDS, list);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastDocsDatasetContentChange(long datasetId) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_DOCS_DATASET_CONTENT_CHANGE);
        intent.putExtra(CcdSdkDefines.EXTRA_DATASET_ID, datasetId);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastStorageNodeChange(long deviceId, int changeType) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_STORAGE_NODE_INFO_CHANGE);
        intent.putExtra(CcdSdkDefines.EXTRA_CLOUD_DEVICE_ID, deviceId);
        intent.putExtra(CcdSdkDefines.EXTRA_STORAGE_NODE_INFO_CHANGE_TYPE, changeType);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broascastDocsCompletion(CcdiRpc.DocSaveAndGoChangeType changetype, String pathName, long modifyTime, int result, String docName, long compId, long revision) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_DOCS_DATASET_COMPLETION);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_CHANGE_TYPE, changetype);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_FILE_PATH_AND_NAME, pathName);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_MODIFY_TIME, modifyTime);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_RESULT, result);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_DOCUMENT_NAME, docName);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_COMP_ID, compId);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_REVISION, revision);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broascastDocsEngineStateChange(boolean state) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_DOCS_ENGINE_STATE_CHANGE);
        intent.putExtra(CcdSdkDefines.EXTRA_DOCS_ENGINE_STATE, state);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastUploadFilesCountChanged(int uploadfiles) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_PICSTREAM_PENDING_FILES_COUNT_CHANGED);
        intent.putExtra(CcdSdkDefines.EXTRA_UPLOAD_FILES_COUNT, uploadfiles);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastPicStreamDownloadSyncCompletion(int syncfeature) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_CAMERAROLL_SYNC_COMPLETE);
        sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastPicStreamIndexSyncCompletion(long datasetId) {
        Intent intent = new Intent(CcdSdkDefines.ACTION_PICSTREAM_INDEX_SYNC_COMPLETE);
        intent.putExtra(CcdSdkDefines.EXTRA_DATASET_ID, datasetId);
        sendBroadcast(intent);
    }

    private int mapAsyncUploadStatus(int type) {
        int ret = 0;
        switch (type) {
            case 0:
                ret = 0;
                break;
            case 1:
                ret = 1;
                break;
            case 2:
                ret = 2;
                break;
            case 3:
                ret = 3;
                break;
            case 4:
                ret = 4;
                break;
            case 5:
                ret = 5;
                break;
            case 6:
                ret = 6;
                break;
        }
        L.i(LOG_TAG, "mapAsyncUploadStatus() type = " + type + ", ret = " + ret);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getStatusGroupNumber(int connectionState) {
        if (connectionState != 2 && connectionState != 3) {
            return 1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long optGetUserId() {
        long result = GlobalPreferencesManager.getLong(this, "cloud_user_id", -32238L);
        if (result == -32238 || result <= 0) {
            long result2 = this.mClient.getUserId();
            GlobalPreferencesManager.putLong(this, "cloud_user_id", result2);
            return result2;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reviseExternalPicStreamPath() {
        Intent serviceIntent = new Intent(CcdSdkDefines.ACTION_LAUNCH_CCD_BACKGROUND_SERVICE);
        serviceIntent.putExtra(Action.EXTRA_BACKGROND_SERV_ACTION_TYPE, Action.ACTION_TYPE_REVISE_EXTERNAL_PICSTREAM_PATH);
        this.mBackgroundTask.onStartCommand(serviceIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerMediaContentObserver() {
        String paths = GlobalPreferencesManager.getString(getApplicationContext(), InternalDefines.PREFERENCE_QUEUED_EXTERNAL_CAMERA_PATH, null);
        if (TextUtils.isEmpty(paths)) {
            unregisterMediaContentObserver();
            return;
        }
        String[] queuedExternalDCIM = paths.split(":");
        this.mQueuedExternalDCIM = new ArrayList<>(Arrays.asList(queuedExternalDCIM));
        if (this.mMediaObserver == null) {
            L.i(LOG_TAG, "start media observer to monitor external DCIM path to be created.");
            this.mMediaObserver = new MediaObserver(this.mHandler);
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this.mMediaObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterMediaContentObserver() {
        if (this.mMediaObserver != null) {
            L.i(LOG_TAG);
            getContentResolver().unregisterContentObserver(this.mMediaObserver);
            this.mMediaObserver.clear();
            this.mMediaObserver = null;
        }
    }

    private boolean isDeQueueTheadAlive() {
        if (this.mThreadPool != null) {
            synchronized (this.mThreadPool) {
                L.i(LOG_TAG, "deQueueTheadStillAlive() pool size = " + this.mThreadPool.size());
                Iterator i$ = this.mThreadPool.iterator();
                while (i$.hasNext()) {
                    Thread thread = i$.next();
                    if (thread != null && thread.isAlive()) {
                        L.i(LOG_TAG, "thread name = " + thread.getName());
                        if (thread instanceof DeQueueThread) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGetMediaServerThreadAlive() {
        if (this.mThreadPool != null) {
            synchronized (this.mThreadPool) {
                Iterator i$ = this.mThreadPool.iterator();
                while (i$.hasNext()) {
                    Thread thread = i$.next();
                    if (thread != null && thread.isAlive() && (thread instanceof GetMediaServerThread)) {
                        L.i(LOG_TAG, "Service is alive.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGetPowerModeThreadAlive() {
        if (this.mThreadPool != null) {
            synchronized (this.mThreadPool) {
                Iterator i$ = this.mThreadPool.iterator();
                while (i$.hasNext()) {
                    Thread thread = i$.next();
                    if (thread != null && thread.isAlive()) {
                        L.i(LOG_TAG, "thread name = " + thread.getName());
                        if (thread instanceof GetPowerModeThread) {
                            L.i(LOG_TAG, "Service is alive.");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PokePowerModeToForegroundThread getAlivePokePowerModeToForegroundThread() {
        if (this.mThreadPool != null) {
            synchronized (this.mThreadPool) {
                Iterator i$ = this.mThreadPool.iterator();
                while (i$.hasNext()) {
                    Thread thread = i$.next();
                    if (thread != null && thread.isAlive() && (thread instanceof PokePowerModeToForegroundThread)) {
                        return (PokePowerModeToForegroundThread) thread;
                    }
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retrieveMediaServerInfo() {
        this.mGetPsnRetryCnt = 0;
        this.mHandler.removeMessages(3);
        this.mHandler.sendEmptyMessage(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureMmThumbnailDownloadPath() {
        Intent serviceIntent = new Intent(CcdSdkDefines.ACTION_LAUNCH_CCD_BACKGROUND_SERVICE);
        serviceIntent.putExtra(Action.EXTRA_BACKGROND_SERV_ACTION_TYPE, Action.ACTION_TYPE_CHANGE_MM_THUMBNAIL_LOCATION);
        this.mBackgroundTask.onStartCommand(serviceIntent);
    }

    private void registerBroadcastReceiver() {
        if (this.mCcdReceiver == null) {
            this.mCcdReceiver = new CcdBroadcastReceiver();
            IntentFilter ccdFilter = new IntentFilter();
            ccdFilter.addAction(CcdSdkDefines.ACTION_POST_PROCESS_ADD_ACCOUNT);
            ccdFilter.addAction(CcdSdkDefines.ACTION_RETRIEVE_PSN_INFO);
            ccdFilter.addAction(CcdSdkDefines.BROADCAST_MESASGE_SEND_DEVICE_STATE_DELAYED);
            ccdFilter.addAction("com.acer.ccd.BROADCAST_MESASGE_REMOVE_DEVICE_STATE_DELAYED");
            ccdFilter.addAction(InnerServiceUtils.BROADCAST_MESSAGE_BACKGROUND_KEEPALIVE);
            ccdFilter.addAction(InnerServiceUtils.BROADCAST_MESSAGE_REGISTRATION_RETRY);
            ccdFilter.addAction(InnerServiceUtils.BROADCAST_MESSAGE_CHECK_LIFECYCLE_CONTROL);
            ccdFilter.addAction("android.net.conn.BACKGROUND_DATA_SETTING_CHANGED");
            ccdFilter.addAction(BROADCAST_SYNC_CONNECTION_SETTING_CHANGED);
            ccdFilter.addAction(InternalDefines.ACTION_START_CONTENT_OBSERVER);
            registerReceiver(this.mCcdReceiver, ccdFilter);
        }
        String permission = getString(R.string.aop_data_sync_permission);
        if (!TextUtils.isEmpty(permission) && this.mAopReceiver == null) {
            this.mAopReceiver = new AopBroadcastReceiver();
            IntentFilter aopFilter = new IntentFilter();
            aopFilter.addAction(Action.ACTION_KILL_RCS_PROCESS);
            aopFilter.addAction(Action.ACTION_REQUEST_CREDENTIAL);
            registerReceiver(this.mAopReceiver, aopFilter, permission, null);
        }
        if (this.mWifiReceiver == null) {
            this.mWifiReceiver = new WifiBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            registerReceiver(this.mWifiReceiver, intentFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addThread(ArrayList<Thread> pool, Thread t) {
        if (pool != null && t != null) {
            synchronized (pool) {
                pool.add(t);
            }
        }
    }

    private void destroyThread(ArrayList<Thread> pool) {
        if (pool != null) {
            synchronized (pool) {
                Iterator i$ = pool.iterator();
                while (i$.hasNext()) {
                    Thread curr = i$.next();
                    if (curr.isAlive()) {
                        curr.interrupt();
                        if ((curr instanceof DeQueueThread) && this.mBinder != null) {
                            L.i(LOG_TAG, "destroy event queue");
                            this.mClient.destroyEventQueue(((DeQueueThread) curr).mHandle);
                        }
                    }
                }
                pool.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postTriggerEventQueueCallback(int type, CcdiRpc.CcdiEvent event) {
        Message msg = this.mHandler.obtainMessage(12, type, 0, event);
        this.mHandler.sendMessage(msg);
    }

    public void stopService() {
        if (this.mBackgroundTask == null || !this.mBackgroundTask.hasThreadAlive()) {
            if (!isBgSyncMode() || !this.mClient.isLoggedIn()) {
                stopSelf();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateQueuedExternalDCIMPaths() {
        if (this.mQueuedExternalDCIM == null || this.mQueuedExternalDCIM.size() <= 0) {
            L.i(LOG_TAG, "No path in the queue, delete queue.");
            GlobalPreferencesManager.remove(getApplicationContext(), InternalDefines.PREFERENCE_QUEUED_EXTERNAL_CAMERA_PATH);
            return;
        }
        StringBuilder builder = new StringBuilder("");
        Iterator i$ = this.mQueuedExternalDCIM.iterator();
        while (i$.hasNext()) {
            String path = i$.next();
            builder.append(path).append(":");
        }
        builder.deleteCharAt(builder.length() - 1);
        GlobalPreferencesManager.putString(getApplicationContext(), InternalDefines.PREFERENCE_QUEUED_EXTERNAL_CAMERA_PATH, builder.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDrive(long deviceId) {
        List<VsDirectoryServiceTypes.UserStorage> storageInfoList = null;
        try {
            storageInfoList = this.mClient.listUserStorage(optGetUserId(), true);
            if (storageInfoList == null || storageInfoList.size() == 0) {
                storageInfoList = this.mClient.listUserStorage(optGetUserId(), false);
            }
        } catch (AcerCloudIOException e) {
            e.printStackTrace();
        }
        if (storageInfoList == null) {
            return false;
        }
        for (VsDirectoryServiceTypes.UserStorage storage : storageInfoList) {
            if (storage.getFeatureRemoteFileAccessEnabled() && storage.getStorageClusterId() == deviceId) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MediaObserver extends ContentObserver {
        private final String TAG;
        private MediaChangeThread mThread;
        public Queue<Uri> mUriQueue;

        public MediaObserver(Handler handler) {
            super(handler);
            this.TAG = MediaObserver.class.getSimpleName() + "@" + CcdiService.LOG_TAG;
            this.mUriQueue = new LinkedList();
        }

        public void clear() {
            this.mUriQueue.clear();
            if (this.mThread != null && this.mThread.isAlive()) {
                this.mThread.interrupt();
            }
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (!CcdiService.this.mClient.isLoggedIn()) {
                L.i(CcdiService.LOG_TAG, "Acer ID has been logged out.");
                CcdiService.this.unregisterMediaContentObserver();
                return;
            }
            if (uri != null) {
                synchronized (this.mUriQueue) {
                    this.mUriQueue.add(uri);
                }
            }
            if (this.mThread == null || !this.mThread.isAlive()) {
                this.mThread = new MediaChangeThread();
                this.mThread.start();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class MediaChangeThread extends Thread {
            private boolean mInterrupted;

            private MediaChangeThread() {
                this.mInterrupted = false;
            }

            @Override // java.lang.Thread
            public void interrupt() {
                this.mInterrupted = true;
                super.interrupt();
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Uri uri;
                while (!this.mInterrupted) {
                    synchronized (MediaObserver.this.mUriQueue) {
                        uri = MediaObserver.this.mUriQueue.poll();
                    }
                    if (uri == null) {
                        L.i(MediaObserver.this.TAG, "MediaChangeThread run(): Uri queue is empty.");
                        return;
                    }
                    String changedPath = null;
                    Cursor cursor = null;
                    try {
                        try {
                            cursor = CcdiService.this.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                            if (cursor != null && cursor.moveToLast()) {
                                changedPath = cursor.getString(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.e(MediaObserver.this.TAG, "cannot get changed data from the uri: " + uri);
                            if (cursor != null && !cursor.isClosed()) {
                                cursor.close();
                            }
                        }
                        if (changedPath != null) {
                            if (CcdiService.this.mQueuedExternalDCIM != null && CcdiService.this.mQueuedExternalDCIM.size() > 0) {
                                Iterator<String> it = CcdiService.this.mQueuedExternalDCIM.iterator();
                                while (it.hasNext()) {
                                    String queuedPath = it.next();
                                    if (changedPath.indexOf(queuedPath) >= 0) {
                                        L.i(MediaObserver.this.TAG, "external DCIM path is created, it will be monitored for PicStream. path: " + queuedPath);
                                        int result = CcdiService.this.mClient.addCameraRollUploadDir(queuedPath);
                                        if (result < 0 && result != -14142) {
                                            L.i(MediaObserver.this.TAG, "The external path can't be added to ccd, addCameraRollUploadDir(): " + result);
                                        } else {
                                            it.remove();
                                        }
                                    }
                                }
                            }
                            CcdiService.this.updateQueuedExternalDCIMPaths();
                            if (CcdiService.this.mQueuedExternalDCIM == null || CcdiService.this.mQueuedExternalDCIM.size() <= 0) {
                                Log.i(MediaObserver.this.TAG, "There is no queued path to be monitored, will unregister content observer.");
                                CcdiService.this.unregisterMediaContentObserver();
                                return;
                            }
                        }
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:103:0x0348 -> B:28:0x0015). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:119:0x0337 -> B:28:0x0015). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:45:0x0141 -> B:28:0x0015). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:52:0x01af -> B:28:0x0015). Please report as a decompilation issue!!! */
    public int swUpdateCheckTitleIdInvalid() {
        String sdkVersion;
        int updateBit;
        int updateBit2;
        int i;
        long updateMask;
        long updateMask2;
        int result = 0;
        if (!this.mNetworkUtility.isNetworkConnected()) {
            L.i(LOG_TAG, "no network connection, pending sdk update check until network is connected");
            return AopErrorCodes.Network.VPL_ERR_UNREACH;
        }
        boolean usePortal = Utils.isUsePortal(getApplicationContext());
        boolean isDevAopDomain = InnerServiceUtils.getInfraDomainFromConfig(getApplicationContext()).contains(ACT_DEV_AOP_DOMAIN);
        if (!usePortal && isDevAopDomain) {
            int swCheckResult = isQueryWebServiceValid();
            L.i(LOG_TAG, "perform wsCheck code " + swCheckResult);
            if (swCheckResult != 200) {
                if (swCheckResult == 100) {
                    result = AopErrorCodes.SoftwareUpdate.SWU_ERR_INVALID_PARTNER;
                } else {
                    result = AopErrorCodes.SoftwareUpdate.SWU_ERR_NOT_FOUND;
                }
                this.mClient.stopModule(true);
            }
            return result;
        }
        String partnerPassword = getString(R.string.aop_partner_password);
        if (TextUtils.isEmpty(partnerPassword)) {
            return AopErrorCodes.SoftwareUpdate.SWU_ERR_NO_PROPER_CRED;
        }
        if (usePortal) {
            sdkVersion = this.mClient.getSdkVersionName();
        } else {
            sdkVersion = Version.AOP_SDK_VERSION_NAME;
        }
        L.i(LOG_TAG, "sdkVersion: " + sdkVersion + "\nstart sdk update check");
        JSONObject output = new JSONObject();
        try {
            updateMask2 = this.mClient.swUpdateCheckApp(InternalDefines.ACCOUNT_PROVIDER_ACER, "acersubarashi", SoftwareUpdateDefine.GUID_AOP_APP_SDK, sdkVersion, false, output);
        } catch (AcerCloudUpdateException e) {
            L.i(LOG_TAG, "sdk update check error: " + e.getMessage());
            updateBit = e.getErrorCode();
        }
        if (needToStopModule(updateMask2)) {
            L.e(LOG_TAG, "Stopping CCD module was due to the update mask: " + updateMask2);
            this.mClient.stopModule(true);
            result = (int) updateMask2;
            i = result;
        } else if (updateMask2 == -9032) {
            L.i(LOG_TAG, "no network connection, pending sdk update check until network is connected");
            result = (int) updateMask2;
            i = result;
        } else {
            if (2 == (2 & updateMask2) || 8 == (8 & updateMask2)) {
                updateBit = 1;
            } else if (1 == (1 & updateMask2) || 4 == (4 & updateMask2)) {
                updateBit = 2;
            } else {
                updateBit = 0;
            }
            L.i(LOG_TAG, "end sdk update check: " + updateBit);
            if (updateBit == 1) {
                this.mClient.stopModule(true);
                L.i(LOG_TAG, "Stopping CCD module was due to sdk critical");
                result = updateBit;
                i = result;
            } else {
                L.i(LOG_TAG, "start app update check");
                String guid = null;
                String appVersion = null;
                if (usePortal && this.mAppId != null) {
                    int index = this.mAppId.indexOf(":");
                    if (index <= 0) {
                        i = result;
                    } else {
                        String titleId = this.mAppId.substring(0, index);
                        appVersion = this.mAppId.substring(index + 1);
                        if (titleId.equals(com.acer.ccd.util.InternalDefines.APP_TITLEID_ACER_PORTAL)) {
                            guid = SoftwareUpdateDefine.GUID_ACERCLOUD;
                        } else if (titleId.equals("0000000602000004")) {
                            guid = SoftwareUpdateDefine.GUID_CLEARFI_MUSIC;
                        } else if (titleId.equals("0000000602000002")) {
                            guid = SoftwareUpdateDefine.GUID_CLEARFI_PHOTO;
                        } else if (titleId.equals("0000000602000003")) {
                            guid = SoftwareUpdateDefine.GUID_CLEARFI_VIDEO;
                        } else if (titleId.equals("0000000602000005")) {
                            guid = SoftwareUpdateDefine.GUID_DOC_SAVENGO;
                        } else if (titleId.equals("0000000602000008")) {
                            guid = SoftwareUpdateDefine.GUID_REMOTE_FILES;
                        } else if (titleId.equals("0000000602000009")) {
                            guid = SoftwareUpdateDefine.GUID_ORBE_MANAGER;
                        } else if (titleId.equals("000000060200000B")) {
                            guid = SoftwareUpdateDefine.GUID_AOP_DEMOS;
                        } else if (titleId.equals("000000060200000F")) {
                            guid = SoftwareUpdateDefine.GUID_IP_STORAGE;
                        } else if (titleId.equals("0000000602000010")) {
                            guid = SoftwareUpdateDefine.GUID_SMART_TRACKER;
                        } else if (titleId.equals("0000000602000011")) {
                            guid = SoftwareUpdateDefine.GUID_TEMP_SENSOR;
                        }
                    }
                }
                if (TextUtils.isEmpty(guid)) {
                    guid = getString(R.string.aop_app_guid);
                }
                if (appVersion == null) {
                    appVersion = InnerServiceUtils.getPackageVersionName(getApplication(), getApplicationContext().getPackageName());
                }
                JSONObject output2 = new JSONObject();
                try {
                    updateMask = this.mClient.swUpdateCheckApp(PartnerAuthenticator.getPartnerId(getApplicationContext()), getString(R.string.aop_partner_password), guid, appVersion, false, output2);
                } catch (AcerCloudUpdateException e2) {
                    L.i(LOG_TAG, "app update check error: " + e2.getMessage());
                    updateBit2 = e2.getErrorCode();
                }
                if (needToStopModule(updateMask)) {
                    L.e(LOG_TAG, "Stopping CCD module was due to the update mask: " + updateMask);
                    if (usePortal) {
                        L.i(LOG_TAG, "won't stop module due to this is Acer Device");
                        result = (int) updateMask;
                        i = result;
                    } else {
                        this.mClient.stopModule(true);
                        result = (int) updateMask;
                        i = result;
                    }
                } else if (updateMask == -9032) {
                    L.i(LOG_TAG, "no network connection, pending sdk update check until network is connected");
                    result = (int) updateMask;
                    i = result;
                } else {
                    if (2 == (2 & updateMask) || 8 == (8 & updateMask)) {
                        updateBit2 = 1;
                    } else if (1 == (1 & updateMask) || 4 == (4 & updateMask)) {
                        updateBit2 = 2;
                    } else {
                        updateBit2 = 0;
                    }
                    L.i(LOG_TAG, "end app update check: " + updateBit2);
                    result = updateBit2;
                    if (result == 1) {
                        L.i(LOG_TAG, "Stopping CCD module was due to app critical");
                        if (usePortal) {
                            L.i(LOG_TAG, "won't stop module because of Acer Device");
                        } else {
                            this.mClient.stopModule(true);
                        }
                    }
                    i = result;
                }
            }
        }
        return i;
    }

    private boolean needToStopModule(long updateMask) {
        return updateMask == -14325 || updateMask == -30013 || updateMask == -31399;
    }

    public HttpClient createCustomHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sSLSocketFactory = new CustomSSLSocketFactory(trustStore);
            sSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            HttpConnectionParams.setConnectionTimeout(params, 60000);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sSLSocketFactory, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (IOException e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        } catch (KeyManagementException e2) {
            e2.printStackTrace();
            return new DefaultHttpClient();
        } catch (KeyStoreException e3) {
            e3.printStackTrace();
            return new DefaultHttpClient();
        } catch (NoSuchAlgorithmException e4) {
            e4.printStackTrace();
            return new DefaultHttpClient();
        } catch (UnrecoverableKeyException e5) {
            e5.printStackTrace();
            return new DefaultHttpClient();
        } catch (CertificateException e6) {
            e6.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    private int isQueryWebServiceValid() {
        final int[] result = {200};
        long start = System.currentTimeMillis();
        Thread thread = new Thread() { // from class: com.acer.android_services.CcdiService.4
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    String httpMessage = "";
                    JSONObject info = new JSONObject();
                    try {
                        info.put("partnerId", PartnerAuthenticator.getPartnerId(CcdiService.this.getApplicationContext()));
                        info.put(CcdiService.KEY_TITLE_ID, PartnerAuthenticator.getTitleId(CcdiService.this.getApplicationContext()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String body = info.toString();
                    HttpClient client = CcdiService.this.createCustomHttpClient();
                    HttpGetWithEntity request = new HttpGetWithEntity("https://dev.abeing.acer.com:25238/ws/v1/validity");
                    request.setEntity(new StringEntity(body, "UTF8"));
                    HttpResponse response = client.execute(request);
                    String respMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                    if (respMsg.contains(CcdiService.KEY_ERROR_CODE)) {
                        try {
                            JSONObject obj = new JSONObject(respMsg);
                            if (obj.has(CcdiService.KEY_ERROR_CODE)) {
                                result[0] = obj.getInt(CcdiService.KEY_ERROR_CODE);
                            }
                            if (obj.has(CcdiService.KEY_ERROR_MSG)) {
                                httpMessage = obj.getString(CcdiService.KEY_ERROR_MSG);
                            }
                            L.i(CcdiService.LOG_TAG, "wsCheck 0, " + result[0] + ", " + httpMessage);
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (UnsupportedEncodingException e3) {
                    e3.printStackTrace();
                    L.i(CcdiService.LOG_TAG, "wsCheck fail with " + e3.getMessage());
                } catch (ClientProtocolException e4) {
                    e4.printStackTrace();
                    L.i(CcdiService.LOG_TAG, "wsCheck fail with " + e4.getMessage());
                } catch (IOException e5) {
                    e5.printStackTrace();
                    L.i(CcdiService.LOG_TAG, "wsCheck fail with " + e5.getMessage());
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        L.i(LOG_TAG, "request profile " + (end - start));
        return result[0];
    }

    private void registerNetworkBroadcastReceiver() {
        if (this.mNetworkChangeBroadcastReceiver == null) {
            this.mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(this.mNetworkChangeBroadcastReceiver, filter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterNetworkBroadcastReceiver() {
        if (this.mNetworkChangeBroadcastReceiver != null) {
            unregisterReceiver(this.mNetworkChangeBroadcastReceiver);
            this.mNetworkChangeBroadcastReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
        private NetworkChangeBroadcastReceiver() {
        }

        /* JADX WARN: Type inference failed for: r0v5, types: [com.acer.android_services.CcdiService$NetworkChangeBroadcastReceiver$1] */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && CcdiService.this.mNetworkUtility.isNetworkConnected()) {
                new Thread() { // from class: com.acer.android_services.CcdiService.NetworkChangeBroadcastReceiver.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        int result = CcdiService.this.checkLifecycleControl();
                        CcdiService.this.sendCheckResultToClient(result);
                    }
                }.start();
                CcdiService.this.unregisterNetworkBroadcastReceiver();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized int checkLifecycleControl() {
        sUpdateResult = 0;
        stopCheckLifecycleControlAlarm();
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Thread thread = new Thread() { // from class: com.acer.android_services.CcdiService.5
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    super.run();
                    int unused = CcdiService.sUpdateResult = CcdiService.this.swUpdateCheckTitleIdInvalid();
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            sUpdateResult = swUpdateCheckTitleIdInvalid();
        }
        if (sUpdateResult == -9032) {
            registerNetworkBroadcastReceiver();
        }
        startCheckLifecycleControlAlarm();
        return sUpdateResult;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void sendCheckResultToClient(int result) {
        if (mInternalCallbacks != null) {
            int count = mInternalCallbacks.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    mInternalCallbacks.getBroadcastItem(i).onLifeCycleControlResult(result);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mInternalCallbacks.finishBroadcast();
        }
    }
}
