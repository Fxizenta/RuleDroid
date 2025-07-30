package com.owncloud.android.ui.helpers;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import com.nextcloud.client.R;
import com.nextcloud.client.account.User;
import com.owncloud.android.files.services.FileUploader;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.providers.UsersAndGroupsSearchProvider;
import com.owncloud.android.ui.activity.FileActivity;
import com.owncloud.android.ui.asynctasks.CopyAndUploadContentUrisTask;
import com.owncloud.android.ui.fragment.TaskRetainerFragment;
import com.owncloud.android.utils.UriUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class UriUploader {
    private final FileActivity mActivity;
    private final int mBehaviour;
    private final CopyAndUploadContentUrisTask.OnCopyTmpFilesTaskListener mCopyTmpTaskListener;
    private final boolean mShowWaitingDialog;
    private final String mUploadPath;
    private final List<Parcelable> mUrisToUpload;
    private User user;
    private final String TAG = UriUploader.class.getSimpleName();
    private UriUploaderResultCode mCode = UriUploaderResultCode.OK;

    /* loaded from: classes2.dex */
    public enum UriUploaderResultCode {
        OK,
        ERROR_UNKNOWN,
        ERROR_NO_FILE_TO_UPLOAD,
        ERROR_READ_PERMISSION_NOT_GRANTED
    }

    public UriUploader(FileActivity fileActivity, List<Parcelable> list, String str, User user, int i, boolean z, CopyAndUploadContentUrisTask.OnCopyTmpFilesTaskListener onCopyTmpFilesTaskListener) {
        this.mActivity = fileActivity;
        this.mUrisToUpload = list;
        this.mUploadPath = str;
        this.user = user;
        this.mBehaviour = i;
        this.mShowWaitingDialog = z;
        this.mCopyTmpTaskListener = onCopyTmpFilesTaskListener;
    }

    public UriUploaderResultCode uploadUris() {
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterator<Parcelable> it = this.mUrisToUpload.iterator();
            int i = 0;
            while (it.hasNext()) {
                Uri uri = (Uri) it.next();
                if (uri != null) {
                    String displayNameForUri = UriUtils.getDisplayNameForUri(uri, this.mActivity);
                    if (displayNameForUri == null) {
                        throw new IllegalStateException("DisplayName may not be null!");
                    }
                    String str = this.mUploadPath + displayNameForUri;
                    if (UsersAndGroupsSearchProvider.CONTENT.equals(uri.getScheme())) {
                        arrayList.add(uri);
                        arrayList2.add(str);
                    } else if ("file".equals(uri.getScheme())) {
                        requestUpload(uri.getPath(), str);
                        i++;
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                copyThenUpload((Uri[]) arrayList.toArray(new Uri[0]), (String[]) arrayList2.toArray(new String[0]));
            } else if (i == 0) {
                this.mCode = UriUploaderResultCode.ERROR_NO_FILE_TO_UPLOAD;
            }
        } catch (SecurityException e) {
            this.mCode = UriUploaderResultCode.ERROR_READ_PERMISSION_NOT_GRANTED;
            Log_OC.e(this.TAG, "Permissions fail", (Throwable) e);
        } catch (Exception e2) {
            this.mCode = UriUploaderResultCode.ERROR_UNKNOWN;
            Log_OC.e(this.TAG, "Unexpected error", (Throwable) e2);
        }
        return this.mCode;
    }

    private void requestUpload(String str, String str2) {
        FileUploader.uploadNewFile((Context) this.mActivity, this.user.toPlatformAccount(), str, str2, this.mBehaviour, (String) null, false, 0, false, false, FileUploader.NameCollisionPolicy.ASK_USER);
    }

    private void copyThenUpload(Uri[] uriArr, String... strArr) {
        if (this.mShowWaitingDialog) {
            FileActivity fileActivity = this.mActivity;
            fileActivity.showLoadingDialog(fileActivity.getResources().getString(R.string.wait_for_tmp_copy_from_private_storage));
        }
        CopyAndUploadContentUrisTask copyAndUploadContentUrisTask = new CopyAndUploadContentUrisTask(this.mCopyTmpTaskListener, this.mActivity);
        TaskRetainerFragment taskRetainerFragment = (TaskRetainerFragment) this.mActivity.getSupportFragmentManager().findFragmentByTag(TaskRetainerFragment.FTAG_TASK_RETAINER_FRAGMENT);
        if (taskRetainerFragment != null) {
            taskRetainerFragment.setTask(copyAndUploadContentUrisTask);
        }
        copyAndUploadContentUrisTask.execute(CopyAndUploadContentUrisTask.makeParamsToExecute(this.user, uriArr, strArr, this.mBehaviour, this.mActivity.getContentResolver()));
    }
}
