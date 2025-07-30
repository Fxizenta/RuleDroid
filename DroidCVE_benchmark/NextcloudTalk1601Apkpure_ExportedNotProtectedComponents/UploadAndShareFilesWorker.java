package com.nextcloud.talk.jobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.PermissionChecker;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.bluelinelabs.conductor.Controller;
import com.fasterxml.jackson.core.JsonPointer;
import com.google.android.exoplayer2.C;
import com.nextcloud.talk.activities.MainActivity;
import com.nextcloud.talk.api.NcApi;
import com.nextcloud.talk.application.NextcloudTalkApplication;
import com.nextcloud.talk.data.user.model.User;
import com.nextcloud.talk.upload.chunked.ChunkedFileUploader;
import com.nextcloud.talk.upload.chunked.OnDataTransferProgressListener;
import com.nextcloud.talk.upload.normal.FileUploader;
import com.nextcloud.talk.users.UserManager;
import com.nextcloud.talk.utils.FileUtils;
import com.nextcloud.talk.utils.RemoteFileUtils;
import com.nextcloud.talk.utils.bundle.BundleKeys;
import com.nextcloud.talk.utils.database.user.CapabilitiesUtilNew;
import com.nextcloud.talk.utils.preferences.AppPreferences;
import com.nextcloud.talk.webrtc.Globals;
import com.nextcloud.talk2.R;
import java.io.File;
import java.util.Arrays;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/* compiled from: UploadAndShareFilesWorker.kt */
@Metadata(d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u0000 I2\u00020\u00012\u00020\u0002:\u0001IB\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\b\u0010<\u001a\u00020=H\u0016J\n\u0010>\u001a\u0004\u0018\u00010?H\u0002J\u0010\u0010@\u001a\u00020\u00112\u0006\u0010A\u001a\u00020,H\u0002J\u0010\u0010B\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\b\u0010C\u001a\u00020\u0011H\u0002J\b\u0010D\u001a\u00020EH\u0002J\b\u0010F\u001a\u00020EH\u0002J\u0010\u0010G\u001a\u00020E2\u0006\u0010A\u001a\u00020,H\u0016J\b\u0010H\u001a\u00020EH\u0002R\u001e\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0010\u001a\u00020\u0011X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0016\u001a\u00020\u0017X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u001a\u0010\u001c\u001a\u00020\u0011X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0013\"\u0004\b\u001e\u0010\u0015R\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010#\u001a\u00020$8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R\u000e\u0010)\u001a\u00020*X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010-\u001a\u00020.8\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102R\u001a\u00103\u001a\u00020\u0011X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b4\u0010\u0013\"\u0004\b5\u0010\u0015R\u001e\u00106\u001a\u0002078\u0006@\u0006X\u0087.¢\u0006\u000e\n\u0000\u001a\u0004\b8\u00109\"\u0004\b:\u0010;¨\u0006J"}, d2 = {"Lcom/nextcloud/talk/jobs/UploadAndShareFilesWorker;", "Landroidx/work/Worker;", "Lcom/nextcloud/talk/upload/chunked/OnDataTransferProgressListener;", "context", "Landroid/content/Context;", "workerParameters", "Landroidx/work/WorkerParameters;", "(Landroid/content/Context;Landroidx/work/WorkerParameters;)V", "appPreferences", "Lcom/nextcloud/talk/utils/preferences/AppPreferences;", "getAppPreferences", "()Lcom/nextcloud/talk/utils/preferences/AppPreferences;", "setAppPreferences", "(Lcom/nextcloud/talk/utils/preferences/AppPreferences;)V", "getContext", "()Landroid/content/Context;", "conversationName", "", "getConversationName", "()Ljava/lang/String;", "setConversationName", "(Ljava/lang/String;)V", "currentUser", "Lcom/nextcloud/talk/data/user/model/User;", "getCurrentUser", "()Lcom/nextcloud/talk/data/user/model/User;", "setCurrentUser", "(Lcom/nextcloud/talk/data/user/model/User;)V", "fileName", "getFileName", "setFileName", "mBuilder", "Landroidx/core/app/NotificationCompat$Builder;", "mNotifyManager", "Landroid/app/NotificationManager;", "ncApi", "Lcom/nextcloud/talk/api/NcApi;", "getNcApi", "()Lcom/nextcloud/talk/api/NcApi;", "setNcApi", "(Lcom/nextcloud/talk/api/NcApi;)V", "notification", "Landroid/app/Notification;", "notificationId", "", "okHttpClient", "Lokhttp3/OkHttpClient;", "getOkHttpClient", "()Lokhttp3/OkHttpClient;", "setOkHttpClient", "(Lokhttp3/OkHttpClient;)V", Globals.ROOM_TOKEN, "getRoomToken", "setRoomToken", "userManager", "Lcom/nextcloud/talk/users/UserManager;", "getUserManager", "()Lcom/nextcloud/talk/users/UserManager;", "setUserManager", "(Lcom/nextcloud/talk/users/UserManager;)V", "doWork", "Landroidx/work/ListenableWorker$Result;", "getIntentToOpenConversation", "Landroid/app/PendingIntent;", "getNotificationContentText", "percentage", "getRemotePath", "getShortenedFileName", "initNotificationSetup", "", "initNotificationWithPercentage", "onTransferProgress", "showFailedToUploadNotification", "Companion", "app_gplayRelease"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class UploadAndShareFilesWorker extends Worker implements OnDataTransferProgressListener {
    private static final long CHUNK_UPLOAD_THRESHOLD_SIZE = 1024000;
    private static final String CONVERSATION_NAME = "CONVERSATION_NAME";
    private static final String DEVICE_SOURCE_FILE = "DEVICE_SOURCE_FILE";
    private static final int HUNDRED_PERCENT = 100;
    private static final String META_DATA = "META_DATA";
    private static final int NOTIFICATION_FILE_NAME_MAX_LENGTH = 20;
    public static final int REQUEST_PERMISSION = 3123;
    private static final String ROOM_TOKEN = "ROOM_TOKEN";
    private static final String THREE_DOTS = "…";
    private static final int ZERO_PERCENT = 0;

    @Inject
    public AppPreferences appPreferences;
    private final Context context;
    public String conversationName;
    public User currentUser;
    public String fileName;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;

    @Inject
    public NcApi ncApi;
    private Notification notification;
    private int notificationId;

    @Inject
    public OkHttpClient okHttpClient;
    public String roomToken;

    @Inject
    public UserManager userManager;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final String TAG = Reflection.getOrCreateKotlinClass(UploadAndShareFilesWorker.class).getSimpleName();

    public final Context getContext() {
        return this.context;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public UploadAndShareFilesWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(workerParameters, "workerParameters");
        this.context = context;
    }

    public final NcApi getNcApi() {
        NcApi ncApi = this.ncApi;
        if (ncApi != null) {
            return ncApi;
        }
        Intrinsics.throwUninitializedPropertyAccessException("ncApi");
        return null;
    }

    public final void setNcApi(NcApi ncApi) {
        Intrinsics.checkNotNullParameter(ncApi, "<set-?>");
        this.ncApi = ncApi;
    }

    public final UserManager getUserManager() {
        UserManager userManager = this.userManager;
        if (userManager != null) {
            return userManager;
        }
        Intrinsics.throwUninitializedPropertyAccessException("userManager");
        return null;
    }

    public final void setUserManager(UserManager userManager) {
        Intrinsics.checkNotNullParameter(userManager, "<set-?>");
        this.userManager = userManager;
    }

    public final AppPreferences getAppPreferences() {
        AppPreferences appPreferences = this.appPreferences;
        if (appPreferences != null) {
            return appPreferences;
        }
        Intrinsics.throwUninitializedPropertyAccessException("appPreferences");
        return null;
    }

    public final void setAppPreferences(AppPreferences appPreferences) {
        Intrinsics.checkNotNullParameter(appPreferences, "<set-?>");
        this.appPreferences = appPreferences;
    }

    public final OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = this.okHttpClient;
        if (okHttpClient != null) {
            return okHttpClient;
        }
        Intrinsics.throwUninitializedPropertyAccessException("okHttpClient");
        return null;
    }

    public final void setOkHttpClient(OkHttpClient okHttpClient) {
        Intrinsics.checkNotNullParameter(okHttpClient, "<set-?>");
        this.okHttpClient = okHttpClient;
    }

    public final String getFileName() {
        String str = this.fileName;
        if (str != null) {
            return str;
        }
        Intrinsics.throwUninitializedPropertyAccessException("fileName");
        return null;
    }

    public final void setFileName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.fileName = str;
    }

    public final String getRoomToken() {
        String str = this.roomToken;
        if (str != null) {
            return str;
        }
        Intrinsics.throwUninitializedPropertyAccessException(Globals.ROOM_TOKEN);
        return null;
    }

    public final void setRoomToken(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.roomToken = str;
    }

    public final String getConversationName() {
        String str = this.conversationName;
        if (str != null) {
            return str;
        }
        Intrinsics.throwUninitializedPropertyAccessException("conversationName");
        return null;
    }

    public final void setConversationName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.conversationName = str;
    }

    public final User getCurrentUser() {
        User user = this.currentUser;
        if (user != null) {
            return user;
        }
        Intrinsics.throwUninitializedPropertyAccessException("currentUser");
        return null;
    }

    public final void setCurrentUser(User user) {
        Intrinsics.checkNotNullParameter(user, "<set-?>");
        this.currentUser = user;
    }

    @Override // androidx.work.Worker
    public ListenableWorker.Result doWork() {
        boolean booleanValue;
        NextcloudTalkApplication sharedApplication = NextcloudTalkApplication.INSTANCE.getSharedApplication();
        Intrinsics.checkNotNull(sharedApplication);
        sharedApplication.getComponentApplication().inject(this);
        if (!INSTANCE.isStoragePermissionGranted(this.context)) {
            Log.w(TAG, "Storage permission is not granted. As a developer please make sure you check forpermissions via UploadAndShareFilesWorker.isStoragePermissionGranted() and UploadAndShareFilesWorker.requestStoragePermission() beforehand. If you already did but end up with this warning, the user most likely revoked the permission");
        }
        try {
            User blockingGet = getUserManager().getCurrentUser().blockingGet();
            Intrinsics.checkNotNullExpressionValue(blockingGet, "userManager.currentUser.blockingGet()");
            setCurrentUser(blockingGet);
            String string = getInputData().getString(DEVICE_SOURCE_FILE);
            String string2 = getInputData().getString(ROOM_TOKEN);
            Intrinsics.checkNotNull(string2);
            setRoomToken(string2);
            String string3 = getInputData().getString(CONVERSATION_NAME);
            Intrinsics.checkNotNull(string3);
            setConversationName(string3);
            String string4 = getInputData().getString(META_DATA);
            if (getCurrentUser() == null) {
                throw new IllegalStateException("Required value was null.".toString());
            }
            if (string == null) {
                throw new IllegalStateException("Required value was null.".toString());
            }
            if (!(string.length() > 0)) {
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            if (getRoomToken() == null) {
                throw new IllegalStateException("Required value was null.".toString());
            }
            Uri sourceFileUri = Uri.parse(string);
            FileUtils fileUtils = FileUtils.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(sourceFileUri, "sourceFileUri");
            setFileName(fileUtils.getFileName(sourceFileUri, this.context));
            File fileFromUri = FileUtils.INSTANCE.getFileFromUri(this.context, sourceFileUri);
            String remotePath = getRemotePath(getCurrentUser());
            initNotificationSetup();
            if (fileFromUri != null && fileFromUri.length() > CHUNK_UPLOAD_THRESHOLD_SIZE) {
                Log.d(TAG, "starting chunked upload because size is " + fileFromUri.length());
                initNotificationWithPercentage();
                String type = this.context.getContentResolver().getType(sourceFileUri);
                booleanValue = new ChunkedFileUploader(getOkHttpClient(), getCurrentUser(), getRoomToken(), string4, this).upload(fileFromUri, type != null ? MediaType.INSTANCE.parse(type) : null, remotePath);
            } else {
                Log.d(TAG, "starting normal upload (not chunked)");
                Boolean blockingFirst = new FileUploader(this.context, getCurrentUser(), getRoomToken(), getNcApi()).upload(sourceFileUri, getFileName(), remotePath, string4).blockingFirst();
                Intrinsics.checkNotNullExpressionValue(blockingFirst, "FileUploader(\n          …        ).blockingFirst()");
                booleanValue = blockingFirst.booleanValue();
            }
            if (booleanValue) {
                NotificationManager notificationManager = this.mNotifyManager;
                if (notificationManager != null) {
                    notificationManager.cancel(this.notificationId);
                }
                ListenableWorker.Result success = ListenableWorker.Result.success();
                Intrinsics.checkNotNullExpressionValue(success, "success()");
                return success;
            }
            Log.e(TAG, "Something went wrong when trying to upload file");
            showFailedToUploadNotification();
            ListenableWorker.Result failure = ListenableWorker.Result.failure();
            Intrinsics.checkNotNullExpressionValue(failure, "failure()");
            return failure;
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when trying to upload file", e);
            showFailedToUploadNotification();
            ListenableWorker.Result failure2 = ListenableWorker.Result.failure();
            Intrinsics.checkNotNullExpressionValue(failure2, "failure()");
            return failure2;
        }
    }

    private final String getRemotePath(User currentUser) {
        StringBuilder sb = new StringBuilder();
        String attachmentFolder = CapabilitiesUtilNew.getAttachmentFolder(currentUser);
        Intrinsics.checkNotNull(attachmentFolder);
        sb.append(attachmentFolder);
        sb.append(JsonPointer.SEPARATOR);
        sb.append(getFileName());
        return RemoteFileUtils.INSTANCE.getNewPathIfFileExists(getNcApi(), currentUser, sb.toString());
    }

    @Override // com.nextcloud.talk.upload.chunked.OnDataTransferProgressListener
    public void onTransferProgress(int percentage) {
        NotificationCompat.Builder builder = this.mBuilder;
        Intrinsics.checkNotNull(builder);
        Notification build = builder.setProgress(100, percentage, false).setContentText(getNotificationContentText(percentage)).build();
        Intrinsics.checkNotNullExpressionValue(build, "mBuilder!!\n            .…ge))\n            .build()");
        this.notification = build;
        NotificationManager notificationManager = this.mNotifyManager;
        Intrinsics.checkNotNull(notificationManager);
        int i = this.notificationId;
        Notification notification = this.notification;
        if (notification == null) {
            Intrinsics.throwUninitializedPropertyAccessException("notification");
            notification = null;
        }
        notificationManager.notify(i, notification);
    }

    private final void initNotificationSetup() {
        Object systemService = this.context.getSystemService("notification");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
        this.mNotifyManager = (NotificationManager) systemService;
        this.mBuilder = new NotificationCompat.Builder(this.context, "NOTIFICATION_CHANNEL_UPLOADS");
    }

    private final void initNotificationWithPercentage() {
        NotificationCompat.Builder builder = this.mBuilder;
        Intrinsics.checkNotNull(builder);
        Notification build = builder.setContentTitle(this.context.getResources().getString(R.string.nc_upload_in_progess)).setContentText(getNotificationContentText(0)).setSmallIcon(R.drawable.upload_white).setOngoing(true).setProgress(100, 0, false).setPriority(-1).setContentIntent(getIntentToOpenConversation()).build();
        Intrinsics.checkNotNullExpressionValue(build, "mBuilder!!\n            .…n())\n            .build()");
        this.notification = build;
        this.notificationId = (int) SystemClock.uptimeMillis();
        NotificationManager notificationManager = this.mNotifyManager;
        Intrinsics.checkNotNull(notificationManager);
        int i = this.notificationId;
        Notification notification = this.notification;
        if (notification == null) {
            Intrinsics.throwUninitializedPropertyAccessException("notification");
            notification = null;
        }
        notificationManager.notify(i, notification);
    }

    private final String getNotificationContentText(int percentage) {
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String string = this.context.getResources().getString(R.string.nc_upload_notification_text);
        Intrinsics.checkNotNullExpressionValue(string, "context.resources.getStr…upload_notification_text)");
        String format = String.format(string, Arrays.copyOf(new Object[]{getShortenedFileName(), getConversationName(), Integer.valueOf(percentage)}, 3));
        Intrinsics.checkNotNullExpressionValue(format, "format(format, *args)");
        return format;
    }

    private final String getShortenedFileName() {
        if (getFileName().length() > 20) {
            return "…" + StringsKt.takeLast(getFileName(), 20);
        }
        return getFileName();
    }

    private final PendingIntent getIntentToOpenConversation() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this.context, (Class<?>) MainActivity.class);
        intent.setFlags(C.ENCODING_PCM_32BIT);
        bundle.putString(BundleKeys.KEY_ROOM_TOKEN, getRoomToken());
        bundle.putParcelable(BundleKeys.KEY_USER_ENTITY, getCurrentUser());
        bundle.putBoolean(BundleKeys.KEY_FROM_NOTIFICATION_START_CALL, false);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(this.context, (int) System.currentTimeMillis(), intent, Build.VERSION.SDK_INT >= 31 ? 33554432 : 0);
    }

    private final void showFailedToUploadNotification() {
        String string = this.context.getResources().getString(R.string.nc_upload_failed_notification_title);
        Intrinsics.checkNotNullExpressionValue(string, "context.resources.getStr…ailed_notification_title)");
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String string2 = this.context.getResources().getString(R.string.nc_upload_failed_notification_text);
        Intrinsics.checkNotNullExpressionValue(string2, "context.resources.getStr…failed_notification_text)");
        String format = String.format(string2, Arrays.copyOf(new Object[]{getFileName()}, 1));
        Intrinsics.checkNotNullExpressionValue(format, "format(format, *args)");
        NotificationCompat.Builder builder = this.mBuilder;
        Intrinsics.checkNotNull(builder);
        Notification build = builder.setContentTitle(string).setContentText(format).setSmallIcon(R.drawable.baseline_error_24).setOngoing(false).build();
        Intrinsics.checkNotNullExpressionValue(build, "mBuilder!!\n            .…lse)\n            .build()");
        this.notification = build;
        NotificationManager notificationManager = this.mNotifyManager;
        if (notificationManager != null) {
            notificationManager.cancel(this.notificationId);
        }
        NotificationManager notificationManager2 = this.mNotifyManager;
        Intrinsics.checkNotNull(notificationManager2);
        int uptimeMillis = (int) SystemClock.uptimeMillis();
        Notification notification = this.notification;
        if (notification == null) {
            Intrinsics.throwUninitializedPropertyAccessException("notification");
            notification = null;
        }
        notificationManager2.notify(uptimeMillis, notification);
    }

    /* compiled from: UploadAndShareFilesWorker.kt */
    @Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018J(\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u001c\u001a\u00020\u00062\b\u0010\u001d\u001a\u0004\u0018\u00010\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/nextcloud/talk/jobs/UploadAndShareFilesWorker$Companion;", "", "()V", "CHUNK_UPLOAD_THRESHOLD_SIZE", "", UploadAndShareFilesWorker.CONVERSATION_NAME, "", UploadAndShareFilesWorker.DEVICE_SOURCE_FILE, "HUNDRED_PERCENT", "", UploadAndShareFilesWorker.META_DATA, "NOTIFICATION_FILE_NAME_MAX_LENGTH", "REQUEST_PERMISSION", UploadAndShareFilesWorker.ROOM_TOKEN, "TAG", "THREE_DOTS", "ZERO_PERCENT", "isStoragePermissionGranted", "", "context", "Landroid/content/Context;", "requestStoragePermission", "", "controller", "Lcom/bluelinelabs/conductor/Controller;", "upload", "fileUri", Globals.ROOM_TOKEN, "conversationName", "metaData", "app_gplayRelease"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean isStoragePermissionGranted(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            if (Build.VERSION.SDK_INT > 29) {
                if (PermissionChecker.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
                    Log.d(UploadAndShareFilesWorker.TAG, "Permission is granted (SDK 30 or greater)");
                    return true;
                }
                Log.d(UploadAndShareFilesWorker.TAG, "Permission is revoked (SDK 30 or greater)");
            } else {
                if (PermissionChecker.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                    Log.d(UploadAndShareFilesWorker.TAG, "Permission is granted");
                    return true;
                }
                Log.d(UploadAndShareFilesWorker.TAG, "Permission is revoked");
            }
            return false;
        }

        public final void requestStoragePermission(Controller controller) {
            Intrinsics.checkNotNullParameter(controller, "controller");
            if (Build.VERSION.SDK_INT > 29) {
                controller.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, UploadAndShareFilesWorker.REQUEST_PERMISSION);
            } else {
                controller.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, UploadAndShareFilesWorker.REQUEST_PERMISSION);
            }
        }

        public final void upload(String fileUri, String roomToken, String conversationName, String metaData) {
            Intrinsics.checkNotNullParameter(fileUri, "fileUri");
            Intrinsics.checkNotNullParameter(roomToken, "roomToken");
            Intrinsics.checkNotNullParameter(conversationName, "conversationName");
            Data build = new Data.Builder().putString(UploadAndShareFilesWorker.DEVICE_SOURCE_FILE, fileUri).putString(UploadAndShareFilesWorker.ROOM_TOKEN, roomToken).putString(UploadAndShareFilesWorker.CONVERSATION_NAME, conversationName).putString(UploadAndShareFilesWorker.META_DATA, metaData).build();
            Intrinsics.checkNotNullExpressionValue(build, "Builder()\n              …\n                .build()");
            OneTimeWorkRequest build2 = new OneTimeWorkRequest.Builder(UploadAndShareFilesWorker.class).setInputData(build).build();
            Intrinsics.checkNotNullExpressionValue(build2, "Builder(UploadAndShareFi…\n                .build()");
            WorkManager.getInstance().enqueueUniqueWork(fileUri, ExistingWorkPolicy.KEEP, build2);
        }
    }
}
