package com.owncloud.android.files.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Pair;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nextcloud.client.R;
import com.nextcloud.client.account.User;
import com.nextcloud.client.account.UserAccountManager;
import com.nextcloud.client.device.BatteryStatus;
import com.nextcloud.client.device.PowerManagementService;
import com.nextcloud.client.jobs.BackgroundJobManagerImpl;
import com.nextcloud.client.network.Connectivity;
import com.nextcloud.client.network.ConnectivityService;
import com.nextcloud.java.util.Optional;
import com.owncloud.android.MainApp;
import com.owncloud.android.authentication.AuthenticatorActivity;
import com.owncloud.android.datamodel.FileDataStorageManager;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.datamodel.ThumbnailsCacheManager;
import com.owncloud.android.datamodel.UploadsStorageManager;
import com.owncloud.android.db.OCUpload;
import com.owncloud.android.db.UploadResult;
import com.owncloud.android.lib.common.OwnCloudAccount;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientManagerFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.operations.UploadFileOperation;
import com.owncloud.android.ui.activity.ConflictsResolveActivity;
import com.owncloud.android.ui.activity.UploadListActivity;
import com.owncloud.android.ui.notifications.NotificationUtils;
import com.owncloud.android.utils.ErrorMessageAdapter;
import com.owncloud.android.utils.theme.ThemeColorUtils;
import dagger.android.AndroidInjection;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;

/* loaded from: classes.dex */
public class FileUploader extends Service implements OnDatatransferProgressListener, OnAccountsUpdateListener, UploadFileOperation.OnRenameListener {
    public static final String ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String EXTRA_LINKED_TO_PATH = "LINKED_TO";
    public static final String EXTRA_OLD_FILE_PATH = "OLD_FILE_PATH";
    public static final String EXTRA_OLD_REMOTE_PATH = "OLD_REMOTE_PATH";
    public static final String EXTRA_REMOTE_PATH = "REMOTE_PATH";
    public static final String EXTRA_UPLOAD_RESULT = "RESULT";
    private static final int FOREGROUND_SERVICE_ID = 411;
    public static final String KEY_ACCOUNT = "ACCOUNT";
    public static final String KEY_CREATED_BY = "CREATED_BY";
    public static final String KEY_CREATE_REMOTE_FOLDER = "CREATE_REMOTE_FOLDER";
    public static final String KEY_DISABLE_RETRIES = "DISABLE_RETRIES";
    public static final String KEY_FILE = "FILE";
    public static final String KEY_LOCAL_BEHAVIOUR = "BEHAVIOUR";
    public static final String KEY_LOCAL_FILE = "LOCAL_FILE";
    public static final String KEY_MIME_TYPE = "MIME_TYPE";
    public static final String KEY_NAME_COLLISION_POLICY = "KEY_NAME_COLLISION_POLICY";
    public static final String KEY_REMOTE_FILE = "REMOTE_FILE";
    private static final String KEY_RETRY = "KEY_RETRY";
    private static final String KEY_RETRY_UPLOAD = "KEY_RETRY_UPLOAD";
    public static final String KEY_WHILE_CHARGING_ONLY = "KEY_WHILE_CHARGING_ONLY";
    public static final String KEY_WHILE_ON_WIFI_ONLY = "KEY_ON_WIFI_ONLY";
    public static final int LOCAL_BEHAVIOUR_COPY = 0;
    public static final int LOCAL_BEHAVIOUR_DELETE = 3;
    public static final int LOCAL_BEHAVIOUR_FORGET = 2;
    public static final int LOCAL_BEHAVIOUR_MOVE = 1;
    private static final String TAG = "FileUploader";
    private static final String UPLOADS_ADDED_MESSAGE = "UPLOADS_ADDED";
    private static final String UPLOAD_FINISH_MESSAGE = "UPLOAD_FINISH";
    private static final String UPLOAD_START_MESSAGE = "UPLOAD_START";

    @Inject
    UserAccountManager accountManager;

    @Inject
    ConnectivityService connectivityService;

    @Inject
    LocalBroadcastManager localBroadcastManager;
    private IBinder mBinder;
    private Account mCurrentAccount;
    private UploadFileOperation mCurrentUpload;
    private int mLastPercent;
    private Notification mNotification;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private IndexedForest<UploadFileOperation> mPendingUploads = new IndexedForest<>();
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private FileDataStorageManager mStorageManager;
    private OwnCloudClient mUploadClient;

    @Inject
    UploadsStorageManager mUploadsStorageManager;

    @Inject
    PowerManagementService powerManagementService;

    @Override // com.owncloud.android.operations.UploadFileOperation.OnRenameListener
    public void onRenameUpload() {
        this.mUploadsStorageManager.updateDatabaseUploadStart(this.mCurrentUpload);
        sendBroadcastUploadStarted(this.mCurrentUpload);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
        Log_OC.d(TAG, "Creating service");
        this.mNotificationManager = (NotificationManager) getSystemService(BackgroundJobManagerImpl.JOB_NOTIFICATION);
        HandlerThread handlerThread = new HandlerThread("FileUploaderThread", 10);
        handlerThread.start();
        this.mServiceLooper = handlerThread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper, this);
        this.mBinder = new FileUploaderBinder();
        NotificationCompat.Builder color = new NotificationCompat.Builder(this).setContentTitle(getApplicationContext().getResources().getString(R.string.app_name)).setContentText(getApplicationContext().getResources().getString(R.string.foreground_service_upload)).setSmallIcon(R.drawable.notification_icon).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon)).setColor(ThemeColorUtils.primaryColor(getApplicationContext(), true));
        if (Build.VERSION.SDK_INT >= 26) {
            color.setChannelId(NotificationUtils.NOTIFICATION_CHANNEL_UPLOAD);
        }
        this.mNotification = color.build();
        if (this.mUploadsStorageManager.failInProgressUploads(UploadResult.SERVICE_INTERRUPTED) > 0) {
            resurrection();
        }
        AccountManager.get(getApplicationContext()).addOnAccountsUpdatedListener(this, null, false);
    }

    private void resurrection() {
        this.mNotificationManager.cancel(411);
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log_OC.v(TAG, "Destroying service");
        this.mBinder = null;
        this.mServiceHandler = null;
        this.mServiceLooper.quit();
        this.mServiceLooper = null;
        NotificationManager notificationManager = this.mNotificationManager;
        if (notificationManager != null) {
            notificationManager.cancel(411);
        }
        this.mNotificationManager = null;
        AccountManager.get(getApplicationContext()).removeOnAccountsUpdatedListener(this);
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        String str = TAG;
        Log_OC.d(str, "Starting command with id " + i2);
        startForeground(411, this.mNotification);
        if (intent == null) {
            Log_OC.e(str, "Intent is null");
            return 2;
        }
        if (!intent.hasExtra("ACCOUNT")) {
            Log_OC.e(str, "Not enough information provided in intent");
            return 2;
        }
        Account account = (Account) intent.getParcelableExtra("ACCOUNT");
        if (account == null) {
            return 2;
        }
        Optional<User> user = this.accountManager.getUser(account.name);
        if (!user.isPresent()) {
            return 2;
        }
        User user2 = user.get();
        boolean booleanExtra = intent.getBooleanExtra(KEY_RETRY, false);
        ArrayList arrayList = new ArrayList();
        boolean booleanExtra2 = intent.getBooleanExtra(KEY_WHILE_ON_WIFI_ONLY, false);
        boolean booleanExtra3 = intent.getBooleanExtra(KEY_WHILE_CHARGING_ONLY, false);
        if (!booleanExtra) {
            if (!intent.hasExtra(KEY_LOCAL_FILE) && !intent.hasExtra("FILE")) {
                Log_OC.e(str, "Not enough information provided in intent");
                return 2;
            }
            Integer gatherAndStartNewUploads = gatherAndStartNewUploads(intent, user2, arrayList, booleanExtra2, booleanExtra3);
            if (gatherAndStartNewUploads != null) {
                return gatherAndStartNewUploads.intValue();
            }
        } else {
            if (!intent.hasExtra("ACCOUNT") || !intent.hasExtra(KEY_RETRY_UPLOAD)) {
                Log_OC.e(str, "Not enough information provided in intent: no KEY_RETRY_UPLOAD_KEY");
                return 2;
            }
            retryUploads(intent, user2, arrayList);
        }
        if (arrayList.size() > 0) {
            Message obtainMessage = this.mServiceHandler.obtainMessage();
            obtainMessage.arg1 = i2;
            obtainMessage.obj = arrayList;
            this.mServiceHandler.sendMessage(obtainMessage);
            sendBroadcastUploadsAdded();
        }
        return 2;
    }

    @Nullable
    private Integer gatherAndStartNewUploads(Intent intent, User user, List<String> list, boolean z, boolean z2) {
        String[] stringArrayExtra;
        String[] stringArrayExtra2;
        String[] strArr;
        OCFile[] oCFileArr;
        if (intent.hasExtra("FILE")) {
            Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("FILE");
            oCFileArr = new OCFile[parcelableArrayExtra.length];
            System.arraycopy(parcelableArrayExtra, 0, oCFileArr, 0, parcelableArrayExtra.length);
            stringArrayExtra = null;
            strArr = null;
            stringArrayExtra2 = null;
        } else {
            stringArrayExtra = intent.getStringArrayExtra(KEY_LOCAL_FILE);
            String[] stringArrayExtra3 = intent.getStringArrayExtra(KEY_REMOTE_FILE);
            stringArrayExtra2 = intent.getStringArrayExtra(KEY_MIME_TYPE);
            strArr = stringArrayExtra3;
            oCFileArr = null;
        }
        if (intent.hasExtra("FILE") && oCFileArr == null) {
            Log_OC.e(TAG, "Incorrect array for OCFiles provided in upload intent");
            return 2;
        }
        if (!intent.hasExtra("FILE")) {
            if (stringArrayExtra == null) {
                Log_OC.e(TAG, "Incorrect array for local paths provided in upload intent");
                return 2;
            }
            if (strArr == null) {
                Log_OC.e(TAG, "Incorrect array for remote paths provided in upload intent");
                return 2;
            }
            if (stringArrayExtra.length != strArr.length) {
                Log_OC.e(TAG, "Different number of remote paths and local paths!");
                return 2;
            }
            oCFileArr = new OCFile[stringArrayExtra.length];
            for (int i = 0; i < stringArrayExtra.length; i++) {
                oCFileArr[i] = UploadFileOperation.obtainNewOCFileToUpload(strArr[i], stringArrayExtra[i], stringArrayExtra2 != null ? stringArrayExtra2[i] : null);
                if (oCFileArr[i] == null) {
                    Log_OC.e(TAG, "obtainNewOCFileToUpload() returned null for remotePaths[i]:" + strArr[i] + " and localPaths[i]:" + stringArrayExtra[i]);
                    return 2;
                }
            }
        }
        NameCollisionPolicy nameCollisionPolicy = (NameCollisionPolicy) intent.getSerializableExtra(KEY_NAME_COLLISION_POLICY);
        if (nameCollisionPolicy == null) {
            nameCollisionPolicy = NameCollisionPolicy.DEFAULT;
        }
        int intExtra = intent.getIntExtra(KEY_LOCAL_BEHAVIOUR, 2);
        boolean booleanExtra = intent.getBooleanExtra(KEY_CREATE_REMOTE_FOLDER, false);
        int intExtra2 = intent.getIntExtra(KEY_CREATED_BY, 0);
        boolean booleanExtra2 = intent.getBooleanExtra(KEY_DISABLE_RETRIES, true);
        try {
            for (OCFile oCFile : oCFileArr) {
                startNewUpload(user, list, z, z2, nameCollisionPolicy, intExtra, booleanExtra, intExtra2, oCFile, booleanExtra2);
            }
            return null;
        } catch (IllegalArgumentException e) {
            Log_OC.e(TAG, "Not enough information provided in intent: " + e.getMessage());
            return 2;
        } catch (IllegalStateException e2) {
            Log_OC.e(TAG, "Bad information provided in intent: " + e2.getMessage());
            return 2;
        } catch (Exception e3) {
            Log_OC.e(TAG, "Unexpected exception while processing upload intent", (Throwable) e3);
            return 2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void startNewUpload(User user, List<String> list, boolean z, boolean z2, NameCollisionPolicy nameCollisionPolicy, int i, boolean z3, int i2, OCFile oCFile, boolean z4) {
        OCUpload oCUpload = new OCUpload(oCFile, user.toPlatformAccount());
        oCUpload.setFileSize(oCFile.getFileLength());
        oCUpload.setNameCollisionPolicy(nameCollisionPolicy);
        oCUpload.setCreateRemoteFolder(z3);
        oCUpload.setCreatedBy(i2);
        oCUpload.setLocalAction(i);
        oCUpload.setUseWifiOnly(z);
        oCUpload.setWhileChargingOnly(z2);
        oCUpload.setUploadStatus(UploadsStorageManager.UploadStatus.UPLOAD_IN_PROGRESS);
        UploadFileOperation uploadFileOperation = new UploadFileOperation(this.mUploadsStorageManager, this.connectivityService, this.powerManagementService, user, oCFile, oCUpload, nameCollisionPolicy, i, this, z, z2, z4);
        uploadFileOperation.setCreatedBy(i2);
        if (z3) {
            uploadFileOperation.setRemoteFolderToBeCreated();
        }
        uploadFileOperation.addDataTransferProgressListener(this);
        uploadFileOperation.addDataTransferProgressListener((FileUploaderBinder) this.mBinder);
        uploadFileOperation.addRenameUploadListener(this);
        Pair<String, String> putIfAbsent = this.mPendingUploads.putIfAbsent(user.getAccountName(), oCFile.getRemotePath(), uploadFileOperation);
        if (putIfAbsent != null) {
            list.add(putIfAbsent.first);
            uploadFileOperation.setOCUploadId(this.mUploadsStorageManager.storeUpload(oCUpload));
        }
    }

    private void retryUploads(Intent intent, User user, List<String> list) {
        OCUpload oCUpload = (OCUpload) intent.getParcelableExtra(KEY_RETRY_UPLOAD);
        UploadFileOperation uploadFileOperation = new UploadFileOperation(this.mUploadsStorageManager, this.connectivityService, this.powerManagementService, user, null, oCUpload, oCUpload.getNameCollisionPolicy(), oCUpload.getLocalAction(), this, oCUpload.isUseWifiOnly(), oCUpload.isWhileChargingOnly(), true);
        uploadFileOperation.addDataTransferProgressListener(this);
        uploadFileOperation.addDataTransferProgressListener((FileUploaderBinder) this.mBinder);
        uploadFileOperation.addRenameUploadListener(this);
        Pair<String, String> putIfAbsent = this.mPendingUploads.putIfAbsent(user.getAccountName(), oCUpload.getRemotePath(), uploadFileOperation);
        if (putIfAbsent != null) {
            list.add((String) putIfAbsent.first);
            oCUpload.setUploadStatus(UploadsStorageManager.UploadStatus.UPLOAD_IN_PROGRESS);
            this.mUploadsStorageManager.updateUpload(oCUpload);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        ((FileUploaderBinder) this.mBinder).clearListeners();
        return false;
    }

    @Override // android.accounts.OnAccountsUpdateListener
    public void onAccountsUpdated(Account[] accountArr) {
        UploadFileOperation uploadFileOperation = this.mCurrentUpload;
        if (uploadFileOperation == null || this.accountManager.exists(uploadFileOperation.getAccount())) {
            return;
        }
        this.mCurrentUpload.cancel(RemoteOperationResult.ResultCode.ACCOUNT_NOT_FOUND);
    }

    public void uploadFile(String str) {
        UploadFileOperation uploadFileOperation = this.mPendingUploads.get(str);
        this.mCurrentUpload = uploadFileOperation;
        if (uploadFileOperation != null) {
            if (!this.accountManager.exists(uploadFileOperation.getAccount())) {
                Log_OC.w(TAG, "Account " + this.mCurrentUpload.getAccount().name + " does not exist anymore -> cancelling all its uploads");
                cancelUploadsForAccount(this.mCurrentUpload.getAccount());
                return;
            }
            this.mUploadsStorageManager.updateDatabaseUploadStart(this.mCurrentUpload);
            notifyUploadStart(this.mCurrentUpload);
            sendBroadcastUploadStarted(this.mCurrentUpload);
            try {
                try {
                    Account account = this.mCurrentAccount;
                    if (account == null || !account.equals(this.mCurrentUpload.getAccount())) {
                        Account account2 = this.mCurrentUpload.getAccount();
                        this.mCurrentAccount = account2;
                        this.mStorageManager = new FileDataStorageManager(account2, getContentResolver());
                    }
                    OwnCloudClient clientFor = OwnCloudClientManagerFactory.getDefaultSingleton().getClientFor(new OwnCloudAccount(this.mCurrentAccount, this), this);
                    this.mUploadClient = clientFor;
                    RemoteOperationResult execute = this.mCurrentUpload.execute(clientFor, this.mStorageManager);
                    Pair<UploadFileOperation, String> removePayload = this.mCurrentUpload.wasRenamed() ? this.mPendingUploads.removePayload(this.mCurrentAccount.name, this.mCurrentUpload.getOldFile().getRemotePath()) : this.mPendingUploads.removePayload(this.mCurrentAccount.name, this.mCurrentUpload.getDecryptedRemotePath());
                    this.mUploadsStorageManager.updateDatabaseUploadResult(execute, this.mCurrentUpload);
                    notifyUploadResult(this.mCurrentUpload, execute);
                    sendBroadcastUploadFinished(this.mCurrentUpload, execute, (String) removePayload.second);
                } catch (Exception e) {
                    Log_OC.e(TAG, "Error uploading", (Throwable) e);
                    RemoteOperationResult remoteOperationResult = new RemoteOperationResult(e);
                    Pair<UploadFileOperation, String> removePayload2 = this.mCurrentUpload.wasRenamed() ? this.mPendingUploads.removePayload(this.mCurrentAccount.name, this.mCurrentUpload.getOldFile().getRemotePath()) : this.mPendingUploads.removePayload(this.mCurrentAccount.name, this.mCurrentUpload.getDecryptedRemotePath());
                    this.mUploadsStorageManager.updateDatabaseUploadResult(remoteOperationResult, this.mCurrentUpload);
                    notifyUploadResult(this.mCurrentUpload, remoteOperationResult);
                    sendBroadcastUploadFinished(this.mCurrentUpload, remoteOperationResult, (String) removePayload2.second);
                }
                new ThumbnailsCacheManager.ThumbnailGenerationTask(this.mStorageManager, this.mCurrentAccount).execute(new ThumbnailsCacheManager.ThumbnailGenerationTaskObject(new File(this.mCurrentUpload.getOriginalStoragePath()), this.mCurrentUpload.getFile().getRemoteId()));
            } catch (Throwable th) {
                Pair<UploadFileOperation, String> removePayload3 = this.mCurrentUpload.wasRenamed() ? this.mPendingUploads.removePayload(this.mCurrentAccount.name, this.mCurrentUpload.getOldFile().getRemotePath()) : this.mPendingUploads.removePayload(this.mCurrentAccount.name, this.mCurrentUpload.getDecryptedRemotePath());
                this.mUploadsStorageManager.updateDatabaseUploadResult(null, this.mCurrentUpload);
                notifyUploadResult(this.mCurrentUpload, null);
                sendBroadcastUploadFinished(this.mCurrentUpload, null, (String) removePayload3.second);
                throw th;
            }
        }
    }

    private void notifyUploadStart(UploadFileOperation uploadFileOperation) {
        this.mLastPercent = 0;
        NotificationCompat.Builder newNotificationBuilder = NotificationUtils.newNotificationBuilder(this);
        this.mNotificationBuilder = newNotificationBuilder;
        newNotificationBuilder.setOngoing(true).setSmallIcon(R.drawable.notification_icon).setTicker(getString(R.string.uploader_upload_in_progress_ticker)).setContentTitle(getString(R.string.uploader_upload_in_progress_ticker)).setProgress(100, 0, false).setContentText(String.format(getString(R.string.uploader_upload_in_progress_content), 0, uploadFileOperation.getFileName()));
        if (Build.VERSION.SDK_INT >= 26) {
            this.mNotificationBuilder.setChannelId(NotificationUtils.NOTIFICATION_CHANNEL_UPLOAD);
        }
        this.mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis(), UploadListActivity.createIntent(uploadFileOperation.getFile(), uploadFileOperation.getAccount(), 67108864, this), 0));
        if (uploadFileOperation.isInstantPicture() || uploadFileOperation.isInstantVideo()) {
            return;
        }
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) getSystemService(BackgroundJobManagerImpl.JOB_NOTIFICATION);
        }
        this.mNotificationManager.notify(411, this.mNotificationBuilder.build());
    }

    @Override // com.owncloud.android.lib.common.network.OnDatatransferProgressListener
    public void onTransferProgress(long j, long j2, long j3, String str) {
        int i = (int) ((j2 * 100.0d) / j3);
        if (i != this.mLastPercent) {
            this.mNotificationBuilder.setProgress(100, i, false);
            this.mNotificationBuilder.setContentText(String.format(getString(R.string.uploader_upload_in_progress_content), Integer.valueOf(i), str.substring(str.lastIndexOf("/") + 1)));
            if (this.mNotificationManager == null) {
                this.mNotificationManager = (NotificationManager) getSystemService(BackgroundJobManagerImpl.JOB_NOTIFICATION);
            }
            this.mNotificationManager.notify(411, this.mNotificationBuilder.build());
        }
        this.mLastPercent = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUploadResult(UploadFileOperation uploadFileOperation, RemoteOperationResult remoteOperationResult) {
        Intent createIntent;
        Log_OC.d(TAG, "NotifyUploadResult with resultCode: " + remoteOperationResult.getCode());
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) getSystemService(BackgroundJobManagerImpl.JOB_NOTIFICATION);
        }
        if (remoteOperationResult.isCancelled() || remoteOperationResult.isSuccess() || RemoteOperationResult.ResultCode.LOCAL_FILE_NOT_FOUND.equals(remoteOperationResult.getCode()) || remoteOperationResult.getCode().equals(RemoteOperationResult.ResultCode.DELAYED_FOR_WIFI) || remoteOperationResult.getCode().equals(RemoteOperationResult.ResultCode.DELAYED_FOR_CHARGING) || remoteOperationResult.getCode().equals(RemoteOperationResult.ResultCode.DELAYED_IN_POWER_SAVE_MODE) || remoteOperationResult.getCode().equals(RemoteOperationResult.ResultCode.LOCK_FAILED)) {
            return;
        }
        int i = R.string.uploader_upload_failed_ticker;
        boolean z = remoteOperationResult.getCode() == RemoteOperationResult.ResultCode.UNAUTHORIZED;
        if (z) {
            i = R.string.uploader_upload_failed_credentials_error;
        } else if (remoteOperationResult.getCode() == RemoteOperationResult.ResultCode.SYNC_CONFLICT) {
            i = R.string.uploader_upload_failed_sync_conflict_error;
        }
        this.mNotificationBuilder.setTicker(getString(i)).setContentTitle(getString(i)).setAutoCancel(true).setOngoing(false).setProgress(0, 0, false);
        String errorCauseMessage = ErrorMessageAdapter.getErrorCauseMessage(remoteOperationResult, uploadFileOperation, getResources());
        if (z) {
            Intent intent = new Intent(this, (Class<?>) AuthenticatorActivity.class);
            intent.putExtra("ACCOUNT", uploadFileOperation.getAccount());
            intent.putExtra(AuthenticatorActivity.EXTRA_ACTION, (byte) 2);
            intent.addFlags(268435456);
            intent.addFlags(8388608);
            intent.addFlags(4);
            this.mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 1073741824));
        } else {
            if (remoteOperationResult.getCode().equals(RemoteOperationResult.ResultCode.SYNC_CONFLICT)) {
                createIntent = ConflictsResolveActivity.createIntent(uploadFileOperation.getFile(), uploadFileOperation.getAccount(), uploadFileOperation.getOCUploadId(), 67108864, this);
            } else {
                createIntent = UploadListActivity.createIntent(uploadFileOperation.getFile(), uploadFileOperation.getAccount(), 67108864, this);
            }
            this.mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis(), createIntent, 0));
        }
        this.mNotificationBuilder.setContentText(errorCauseMessage);
        if (remoteOperationResult.isSuccess()) {
            return;
        }
        this.mNotificationManager.notify(new SecureRandom().nextInt(), this.mNotificationBuilder.build());
    }

    private void sendBroadcastUploadsAdded() {
        Intent intent = new Intent(getUploadsAddedMessage());
        intent.setPackage(getPackageName());
        this.localBroadcastManager.sendBroadcast(intent);
    }

    private void sendBroadcastUploadStarted(UploadFileOperation uploadFileOperation) {
        Intent intent = new Intent(getUploadStartMessage());
        intent.putExtra("REMOTE_PATH", uploadFileOperation.getRemotePath());
        intent.putExtra(EXTRA_OLD_FILE_PATH, uploadFileOperation.getOriginalStoragePath());
        intent.putExtra("ACCOUNT_NAME", uploadFileOperation.getAccount().name);
        intent.setPackage(getPackageName());
        this.localBroadcastManager.sendBroadcast(intent);
    }

    private void sendBroadcastUploadFinished(UploadFileOperation uploadFileOperation, RemoteOperationResult remoteOperationResult, String str) {
        Intent intent = new Intent(getUploadFinishMessage());
        intent.putExtra("REMOTE_PATH", uploadFileOperation.getRemotePath());
        if (uploadFileOperation.wasRenamed()) {
            intent.putExtra(EXTRA_OLD_REMOTE_PATH, uploadFileOperation.getOldFile().getRemotePath());
        }
        intent.putExtra(EXTRA_OLD_FILE_PATH, uploadFileOperation.getOriginalStoragePath());
        intent.putExtra("ACCOUNT_NAME", uploadFileOperation.getAccount().name);
        intent.putExtra("RESULT", remoteOperationResult.isSuccess());
        if (str != null) {
            intent.putExtra("LINKED_TO", str);
        }
        intent.setPackage(getPackageName());
        this.localBroadcastManager.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelUploadsForAccount(Account account) {
        this.mPendingUploads.remove(account.name);
        this.mUploadsStorageManager.removeUploads(account.name);
    }

    public static void uploadNewFile(Context context, Account account, String str, String str2, int i, String str3, boolean z, int i2, boolean z2, boolean z3, NameCollisionPolicy nameCollisionPolicy) {
        uploadNewFile(context, account, new String[]{str}, new String[]{str2}, new String[]{str3}, Integer.valueOf(i), Boolean.valueOf(z), i2, z2, z3, nameCollisionPolicy);
    }

    public static void uploadNewFile(Context context, Account account, String[] strArr, String[] strArr2, String[] strArr3, Integer num, Boolean bool, int i, boolean z, boolean z2, NameCollisionPolicy nameCollisionPolicy) {
        Intent intent = new Intent(context, (Class<?>) FileUploader.class);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra(KEY_LOCAL_FILE, strArr);
        intent.putExtra(KEY_REMOTE_FILE, strArr2);
        intent.putExtra(KEY_MIME_TYPE, strArr3);
        intent.putExtra(KEY_LOCAL_BEHAVIOUR, num);
        intent.putExtra(KEY_CREATE_REMOTE_FOLDER, bool);
        intent.putExtra(KEY_CREATED_BY, i);
        intent.putExtra(KEY_WHILE_ON_WIFI_ONLY, z);
        intent.putExtra(KEY_WHILE_CHARGING_ONLY, z2);
        intent.putExtra(KEY_NAME_COLLISION_POLICY, nameCollisionPolicy);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void uploadUpdateFile(Context context, Account account, OCFile oCFile, Integer num, NameCollisionPolicy nameCollisionPolicy) {
        uploadUpdateFile(context, account, new OCFile[]{oCFile}, num, nameCollisionPolicy, true);
    }

    public static void uploadUpdateFile(Context context, Account account, OCFile oCFile, Integer num, NameCollisionPolicy nameCollisionPolicy, boolean z) {
        uploadUpdateFile(context, account, new OCFile[]{oCFile}, num, nameCollisionPolicy, z);
    }

    public static void uploadUpdateFile(Context context, Account account, OCFile[] oCFileArr, Integer num, NameCollisionPolicy nameCollisionPolicy, boolean z) {
        Intent intent = new Intent(context, (Class<?>) FileUploader.class);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra("FILE", oCFileArr);
        intent.putExtra(KEY_LOCAL_BEHAVIOUR, num);
        intent.putExtra(KEY_NAME_COLLISION_POLICY, nameCollisionPolicy);
        intent.putExtra(KEY_DISABLE_RETRIES, z);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void retryUpload(Context context, Account account, OCUpload oCUpload) {
        Intent intent = new Intent(context, (Class<?>) FileUploader.class);
        intent.putExtra(KEY_RETRY, true);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra(KEY_RETRY_UPLOAD, oCUpload);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void retryFailedUploads(Context context, @Nullable Account account, UploadsStorageManager uploadsStorageManager, ConnectivityService connectivityService, UserAccountManager userAccountManager, PowerManagementService powerManagementService, @Nullable UploadResult uploadResult) {
        OCUpload[] failedUploads = uploadsStorageManager.getFailedUploads();
        if (failedUploads.length == 0) {
            return;
        }
        Account account2 = null;
        Connectivity connectivity = connectivityService.getConnectivity();
        boolean z = connectivity.isConnected() && !connectivityService.isInternetWalled();
        boolean isWifi = connectivity.isWifi();
        BatteryStatus battery = powerManagementService.getBattery();
        boolean z2 = battery.isCharging() || battery.isFull();
        boolean isPowerSavingEnabled = powerManagementService.isPowerSavingEnabled();
        for (OCUpload oCUpload : failedUploads) {
            boolean z3 = account == null || account.name.equals(oCUpload.getAccountName());
            boolean z4 = uploadResult == null || uploadResult == oCUpload.getLastResult();
            if (z3 && z4) {
                if (account2 == null || !account2.name.equals(oCUpload.getAccountName())) {
                    account2 = oCUpload.getAccount(userAccountManager);
                }
                if (!new File(oCUpload.getLocalPath()).exists()) {
                    if (oCUpload.getLastResult() != UploadResult.FILE_NOT_FOUND) {
                        oCUpload.setLastResult(UploadResult.FILE_NOT_FOUND);
                        uploadsStorageManager.updateUpload(oCUpload);
                    }
                } else if (!isPowerSavingEnabled && z && canUploadBeRetried(oCUpload, isWifi, z2)) {
                    retryUpload(context, account2, oCUpload);
                }
            }
        }
    }

    private static boolean canUploadBeRetried(OCUpload oCUpload, boolean z, boolean z2) {
        return new File(oCUpload.getLocalPath()).exists() && (!oCUpload.isUseWifiOnly() || z) && (!oCUpload.isWhileChargingOnly() || z2);
    }

    public static String getUploadsAddedMessage() {
        return FileUploader.class.getName() + UPLOADS_ADDED_MESSAGE;
    }

    public static String getUploadStartMessage() {
        return FileUploader.class.getName() + UPLOAD_START_MESSAGE;
    }

    public static String getUploadFinishMessage() {
        return FileUploader.class.getName() + UPLOAD_FINISH_MESSAGE;
    }

    /* loaded from: classes.dex */
    public enum NameCollisionPolicy {
        RENAME,
        OVERWRITE,
        CANCEL,
        ASK_USER;

        public static final NameCollisionPolicy DEFAULT = RENAME;

        public static NameCollisionPolicy deserialize(int i) {
            NameCollisionPolicy[] values = values();
            return (i < 0 || i >= values.length) ? DEFAULT : values[i];
        }

        public int serialize() {
            return ordinal();
        }
    }

    /* loaded from: classes.dex */
    public class FileUploaderBinder extends Binder implements OnDatatransferProgressListener {
        private Map<String, OnDatatransferProgressListener> mBoundListeners = new HashMap();

        public FileUploaderBinder() {
        }

        public void cancel(Account account, OCFile oCFile) {
            cancel(account.name, oCFile.getRemotePath(), null);
        }

        public void cancel(OCUpload oCUpload) {
            cancel(oCUpload.getAccountName(), oCUpload.getRemotePath(), null);
        }

        private void cancel(String str, String str2, @Nullable RemoteOperationResult.ResultCode resultCode) {
            UploadFileOperation uploadFileOperation = (UploadFileOperation) FileUploader.this.mPendingUploads.remove(str, str2).first;
            if (uploadFileOperation == null && FileUploader.this.mCurrentUpload != null && FileUploader.this.mCurrentAccount != null && FileUploader.this.mCurrentUpload.getRemotePath().startsWith(str2) && str.equals(FileUploader.this.mCurrentAccount.name)) {
                uploadFileOperation = FileUploader.this.mCurrentUpload;
            }
            if (uploadFileOperation != null) {
                uploadFileOperation.cancel(resultCode);
                if (resultCode != null) {
                    FileUploader.this.mUploadsStorageManager.updateDatabaseUploadResult(new RemoteOperationResult(resultCode), uploadFileOperation);
                    FileUploader.this.notifyUploadResult(uploadFileOperation, new RemoteOperationResult(resultCode));
                } else {
                    FileUploader.this.mUploadsStorageManager.removeUpload(str, str2);
                }
            }
        }

        public void cancel(Account account) {
            Log_OC.d(FileUploader.TAG, "Account= " + account.name);
            if (FileUploader.this.mCurrentUpload != null) {
                Log_OC.d(FileUploader.TAG, "Current Upload Account= " + FileUploader.this.mCurrentUpload.getAccount().name);
                if (FileUploader.this.mCurrentUpload.getAccount().name.equals(account.name)) {
                    FileUploader.this.mCurrentUpload.cancel(RemoteOperationResult.ResultCode.CANCELLED);
                }
            }
            FileUploader.this.cancelUploadsForAccount(account);
        }

        public void clearListeners() {
            this.mBoundListeners.clear();
        }

        public boolean isUploading(User user, OCFile oCFile) {
            if (user == null || oCFile == null) {
                return false;
            }
            return FileUploader.this.mPendingUploads.contains(user.getAccountName(), oCFile.getRemotePath());
        }

        public boolean isUploadingNow(OCUpload oCUpload) {
            return (oCUpload == null || FileUploader.this.mCurrentAccount == null || FileUploader.this.mCurrentUpload == null || !oCUpload.getAccountName().equals(FileUploader.this.mCurrentAccount.name) || !oCUpload.getRemotePath().equals(FileUploader.this.mCurrentUpload.getRemotePath())) ? false : true;
        }

        public void addDatatransferProgressListener(OnDatatransferProgressListener onDatatransferProgressListener, User user, OCFile oCFile) {
            if (user == null || oCFile == null || onDatatransferProgressListener == null) {
                return;
            }
            this.mBoundListeners.put(buildRemoteName(user.getAccountName(), oCFile.getRemotePath()), onDatatransferProgressListener);
        }

        public void addDatatransferProgressListener(OnDatatransferProgressListener onDatatransferProgressListener, OCUpload oCUpload) {
            if (oCUpload == null || onDatatransferProgressListener == null) {
                return;
            }
            this.mBoundListeners.put(buildRemoteName(oCUpload.getAccountName(), oCUpload.getRemotePath()), onDatatransferProgressListener);
        }

        public void removeDatatransferProgressListener(OnDatatransferProgressListener onDatatransferProgressListener, User user, OCFile oCFile) {
            if (user == null || oCFile == null || onDatatransferProgressListener == null) {
                return;
            }
            String buildRemoteName = buildRemoteName(user.getAccountName(), oCFile.getRemotePath());
            if (this.mBoundListeners.get(buildRemoteName) == onDatatransferProgressListener) {
                this.mBoundListeners.remove(buildRemoteName);
            }
        }

        public void removeDatatransferProgressListener(OnDatatransferProgressListener onDatatransferProgressListener, OCUpload oCUpload) {
            if (oCUpload == null || onDatatransferProgressListener == null) {
                return;
            }
            String buildRemoteName = buildRemoteName(oCUpload.getAccountName(), oCUpload.getRemotePath());
            if (this.mBoundListeners.get(buildRemoteName) == onDatatransferProgressListener) {
                this.mBoundListeners.remove(buildRemoteName);
            }
        }

        @Override // com.owncloud.android.lib.common.network.OnDatatransferProgressListener
        public void onTransferProgress(long j, long j2, long j3, String str) {
            OnDatatransferProgressListener onDatatransferProgressListener = this.mBoundListeners.get(buildRemoteName(FileUploader.this.mCurrentUpload.getAccount().name, FileUploader.this.mCurrentUpload.getFile().getRemotePath()));
            if (onDatatransferProgressListener != null) {
                onDatatransferProgressListener.onTransferProgress(j, j2, j3, str);
            }
            if (MainApp.getAppContext() != null) {
                RemoteOperationResult.ResultCode resultCode = null;
                Connectivity connectivity = FileUploader.this.connectivityService.getConnectivity();
                if (!FileUploader.this.mCurrentUpload.isWifiRequired() || connectivity.isWifi()) {
                    if (!FileUploader.this.mCurrentUpload.isChargingRequired() || FileUploader.this.powerManagementService.getBattery().isCharging()) {
                        if (!FileUploader.this.mCurrentUpload.isIgnoringPowerSaveMode() && FileUploader.this.powerManagementService.isPowerSavingEnabled()) {
                            resultCode = RemoteOperationResult.ResultCode.DELAYED_IN_POWER_SAVE_MODE;
                        }
                    } else {
                        resultCode = RemoteOperationResult.ResultCode.DELAYED_FOR_CHARGING;
                    }
                } else {
                    resultCode = RemoteOperationResult.ResultCode.DELAYED_FOR_WIFI;
                }
                if (resultCode != null) {
                    cancel(FileUploader.this.mCurrentUpload.getAccount().name, FileUploader.this.mCurrentUpload.getFile().getRemotePath(), resultCode);
                }
            }
        }

        private String buildRemoteName(String str, String str2) {
            return str + str2;
        }
    }

    /* loaded from: classes.dex */
    private static class ServiceHandler extends Handler {
        private FileUploader mService;

        public ServiceHandler(Looper looper, FileUploader fileUploader) {
            super(looper);
            if (fileUploader == null) {
                throw new IllegalArgumentException("Received invalid NULL in parameter 'service'");
            }
            this.mService = fileUploader;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            List list = (List) message.obj;
            if (message.obj != null) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    this.mService.uploadFile((String) it.next());
                }
            }
            Log_OC.d(FileUploader.TAG, "Stopping command after id " + message.arg1);
            this.mService.mNotificationManager.cancel(411);
            this.mService.stopForeground(true);
            this.mService.stopSelf(message.arg1);
        }
    }
}
