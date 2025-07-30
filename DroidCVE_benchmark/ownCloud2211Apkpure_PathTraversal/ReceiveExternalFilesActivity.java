package com.owncloud.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.owncloud.android.MainApp;
import com.owncloud.android.R;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.db.PreferenceManager;
import com.owncloud.android.extensions.ActivityExtKt;
import com.owncloud.android.files.services.FileUploader;
import com.owncloud.android.interfaces.ISecurityEnforced;
import com.owncloud.android.interfaces.LockType;
import com.owncloud.android.lib.common.OwnCloudAccount;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.operations.CreateFolderOperation;
import com.owncloud.android.operations.RefreshFolderOperation;
import com.owncloud.android.presentation.ui.files.SortBottomSheetFragment;
import com.owncloud.android.presentation.ui.files.SortOptionsView;
import com.owncloud.android.presentation.ui.files.SortOrder;
import com.owncloud.android.presentation.ui.files.SortType;
import com.owncloud.android.presentation.ui.files.ViewType;
import com.owncloud.android.syncadapter.FileSyncAdapter;
import com.owncloud.android.ui.adapter.ReceiveExternalFilesAdapter;
import com.owncloud.android.ui.asynctasks.CopyAndUploadContentUrisTask;
import com.owncloud.android.ui.dialog.ConfirmationDialogFragment;
import com.owncloud.android.ui.dialog.CreateFolderDialogFragment;
import com.owncloud.android.ui.errorhandling.ErrorMessageAdapter;
import com.owncloud.android.ui.fragment.TaskRetainerFragment;
import com.owncloud.android.ui.helpers.UriUploader;
import com.owncloud.android.utils.DisplayUtils;
import com.owncloud.android.utils.Extras;
import com.owncloud.android.utils.FileStorageUtils;
import com.owncloud.android.utils.SortFilesUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import timber.log.Timber;

/* loaded from: classes2.dex */
public class ReceiveExternalFilesActivity extends FileActivity implements AdapterView.OnItemClickListener, View.OnClickListener, CopyAndUploadContentUrisTask.OnCopyTmpFilesTaskListener, SortOptionsView.SortOptionsListener, SortBottomSheetFragment.SortDialogListener, SortOptionsView.CreateFolderListener, SearchView.OnQueryTextListener, View.OnFocusChangeListener, ReceiveExternalFilesAdapter.OnSearchQueryUpdateListener, ISecurityEnforced {
    private static final int DIALOG_MULTIPLE_ACCOUNT = 1;
    private static final int DIALOG_NO_ACCOUNT = 0;
    private static final String DIALOG_WAIT_COPY_FILE = "DIALOG_WAIT_COPY_FILE";
    private static final String FTAG_ERROR_FRAGMENT = "ERROR_FRAGMENT";
    private static final String KEY_ACCOUNT_SELECTED = "ACCOUNT_SELECTED";
    private static final String KEY_ACCOUNT_SELECTION_SHOWING = "ACCOUNT_SELECTION_SHOWING";
    private static final String KEY_FILE = "FILE";
    private static final String KEY_PARENTS = "PARENTS";
    private static final int MAX_FILENAME_LENGTH = 223;
    private static final int REQUEST_CODE__SETUP_ACCOUNT = 1;
    private AccountManager mAccountManager;
    private boolean mAccountSelected;
    private boolean mAccountSelectionShowing;
    private TextView mEmptyListMessage;
    private OCFile mFile;
    private ListView mListView;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Stack<String> mParents;
    private SearchView mSearchView;
    private SortOptionsView mSortOptionsView;
    private SyncBroadcastReceiver mSyncBroadcastReceiver;
    private UploadBroadcastReceiver mUploadBroadcastReceiver;
    private String mUploadPath;
    private ArrayList<Uri> mStreamsToUpload = new ArrayList<>();
    private ReceiveExternalFilesAdapter mAdapter = null;
    private boolean mSyncInProgress = false;

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    @Override // com.owncloud.android.presentation.ui.files.SortOptionsView.SortOptionsListener
    public void onViewTypeListener(ViewType viewType) {
    }

    @Override // com.owncloud.android.ui.activity.FileActivity, com.owncloud.android.ui.activity.DrawerActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        prepareStreamsToUpload();
        ActivityExtKt.checkPasscodeEnforced(this, this);
        if (bundle == null) {
            this.mParents = new Stack<>();
            this.mAccountSelected = false;
            this.mAccountSelectionShowing = false;
        } else {
            this.mParents = (Stack) bundle.getSerializable(KEY_PARENTS);
            this.mFile = (OCFile) bundle.getParcelable("FILE");
            this.mAccountSelected = bundle.getBoolean(KEY_ACCOUNT_SELECTED);
            this.mAccountSelectionShowing = bundle.getBoolean(KEY_ACCOUNT_SELECTION_SHOWING);
        }
        super.onCreate(bundle);
        if (this.mAccountSelected) {
            setAccount((Account) bundle.getParcelable(FileActivity.EXTRA_ACCOUNT));
        }
        setContentView(R.layout.uploader_layout);
        SortOptionsView sortOptionsView = (SortOptionsView) findViewById(R.id.options_layout);
        this.mSortOptionsView = sortOptionsView;
        if (sortOptionsView != null) {
            sortOptionsView.setOnSortOptionsListener(this);
            this.mSortOptionsView.setOnCreateFolderListener(this);
            this.mSortOptionsView.selectAdditionalView(SortOptionsView.AdditionalView.CREATE_FOLDER);
        }
        this.mEmptyListMessage = (TextView) findViewById(R.id.empty_list_view);
        ListView listView = (ListView) findViewById(android.R.id.list);
        this.mListView = listView;
        listView.setOnItemClickListener(this);
        IntentFilter intentFilter = new IntentFilter(RefreshFolderOperation.EVENT_SINGLE_FOLDER_CONTENTS_SYNCED);
        intentFilter.addAction(RefreshFolderOperation.EVENT_SINGLE_FOLDER_SHARES_SYNCED);
        this.mSyncBroadcastReceiver = new SyncBroadcastReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        this.mLocalBroadcastManager = localBroadcastManager;
        localBroadcastManager.registerReceiver(this.mSyncBroadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter(FileUploader.getUploadFinishMessage());
        intentFilter2.addAction(FileUploader.getUploadStartMessage());
        UploadBroadcastReceiver uploadBroadcastReceiver = new UploadBroadcastReceiver();
        this.mUploadBroadcastReceiver = uploadBroadcastReceiver;
        this.mLocalBroadcastManager.registerReceiver(uploadBroadcastReceiver, intentFilter2);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (((TaskRetainerFragment) supportFragmentManager.findFragmentByTag(TaskRetainerFragment.FTAG_TASK_RETAINER_FRAGMENT)) == null) {
            supportFragmentManager.beginTransaction().add(new TaskRetainerFragment(), TaskRetainerFragment.FTAG_TASK_RETAINER_FRAGMENT).commit();
        }
    }

    @Override // com.owncloud.android.ui.activity.BaseActivity
    protected void setAccount(Account account, boolean z) {
        if (somethingToUpload()) {
            AccountManager accountManager = (AccountManager) getSystemService("account");
            this.mAccountManager = accountManager;
            Account[] accountsByType = accountManager.getAccountsByType(MainApp.INSTANCE.getAccountType());
            if (accountsByType.length == 0) {
                Timber.i("No ownCloud account is available", new Object[0]);
                showDialog(0);
            } else if (accountsByType.length > 1 && !this.mAccountSelected && !this.mAccountSelectionShowing) {
                Timber.i("More than one ownCloud is available", new Object[0]);
                showDialog(1);
                this.mAccountSelectionShowing = true;
            } else if (!z) {
                setAccount(accountsByType[0]);
            }
        } else {
            showErrorDialog(R.string.uploader_error_message_no_file_to_upload, R.string.uploader_error_title_no_file_to_upload);
        }
        super.setAccount(account, z);
    }

    @Override // com.owncloud.android.ui.activity.BaseActivity
    protected void onAccountSet(boolean z) {
        super.onAccountSet(this.mAccountWasRestored);
        initTargetFolder();
        updateDirectoryList();
    }

    @Override // com.owncloud.android.ui.activity.FileActivity, com.owncloud.android.ui.activity.DrawerActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        Timber.v("onSaveInstanceState() start", new Object[0]);
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(KEY_PARENTS, this.mParents);
        bundle.putParcelable("FILE", this.mFile);
        bundle.putBoolean(KEY_ACCOUNT_SELECTED, this.mAccountSelected);
        bundle.putBoolean(KEY_ACCOUNT_SELECTION_SHOWING, this.mAccountSelectionShowing);
        bundle.putParcelable(FileActivity.EXTRA_ACCOUNT, getAccount());
        Timber.v("onSaveInstanceState() end", new Object[0]);
    }

    @Override // com.owncloud.android.ui.activity.FileActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        SyncBroadcastReceiver syncBroadcastReceiver = this.mSyncBroadcastReceiver;
        if (syncBroadcastReceiver != null) {
            this.mLocalBroadcastManager.unregisterReceiver(syncBroadcastReceiver);
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (i == 0) {
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(R.string.uploader_wrn_no_account_title);
            builder.setMessage(String.format(getString(R.string.uploader_wrn_no_account_text), getString(R.string.app_name)));
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.uploader_wrn_no_account_setup_btn_text, new DialogInterface.OnClickListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    Intent intent = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
                    intent.putExtra("authorities", new String[]{MainApp.INSTANCE.getAuthTokenType()});
                    ReceiveExternalFilesActivity.this.startActivityForResult(intent, 1);
                }
            });
            builder.setNegativeButton(R.string.uploader_wrn_no_account_quit_btn_text, new DialogInterface.OnClickListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    ReceiveExternalFilesActivity.this.finish();
                }
            });
            return builder.create();
        }
        if (i == 1) {
            Account[] accountsByType = this.mAccountManager.getAccountsByType(MainApp.INSTANCE.getAccountType());
            int length = accountsByType.length;
            CharSequence[] charSequenceArr = new CharSequence[length];
            for (int i2 = 0; i2 < length; i2++) {
                try {
                    charSequenceArr[i2] = new OwnCloudAccount(accountsByType[i2], this).getDisplayName() + " @ " + DisplayUtils.convertIdn(accountsByType[i2].name.substring(accountsByType[i2].name.lastIndexOf("@") + 1), false);
                } catch (Exception unused) {
                    Timber.w("Couldn't read display name of account; using account name instead", new Object[0]);
                    charSequenceArr[i2] = DisplayUtils.convertIdn(accountsByType[i2].name, false);
                }
            }
            builder.setTitle(R.string.common_choose_account);
            builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    ReceiveExternalFilesActivity.this.m360xa73e4455(dialogInterface, i3);
                }
            });
            builder.setCancelable(true);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ReceiveExternalFilesActivity.this.m361x9acdc896(dialogInterface);
                }
            });
            return builder.create();
        }
        throw new IllegalArgumentException("Unknown dialog id: " + i);
    }

    /* renamed from: lambda$onCreateDialog$0$com-owncloud-android-ui-activity-ReceiveExternalFilesActivity, reason: not valid java name */
    public /* synthetic */ void m360xa73e4455(DialogInterface dialogInterface, int i) {
        setAccount(this.mAccountManager.getAccountsByType(MainApp.INSTANCE.getAccountType())[i]);
        onAccountSet(this.mAccountWasRestored);
        dialogInterface.dismiss();
        this.mAccountSelected = true;
        this.mAccountSelectionShowing = false;
    }

    /* renamed from: lambda$onCreateDialog$1$com-owncloud-android-ui-activity-ReceiveExternalFilesActivity, reason: not valid java name */
    public /* synthetic */ void m361x9acdc896(DialogInterface dialogInterface) {
        this.mAccountSelectionShowing = false;
        dialogInterface.cancel();
        finish();
    }

    @Override // com.owncloud.android.ui.activity.DrawerActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.mParents.size() <= 1) {
            super.onBackPressed();
            return;
        }
        this.mParents.pop();
        startSyncFolderOperation(getStorageManager().getFileByPath(generatePath(this.mParents)));
        updateDirectoryList();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Timber.d("on item click", new Object[0]);
        Vector<OCFile> sortFileList = sortFileList(this.mAdapter.getFiles());
        if (sortFileList.size() <= 0) {
            return;
        }
        Vector vector = new Vector(sortFileList);
        if (vector.size() < i) {
            throw new IndexOutOfBoundsException("Incorrect item selected");
        }
        if (((OCFile) vector.get(i)).isFolder()) {
            OCFile oCFile = (OCFile) vector.get(i);
            startSyncFolderOperation(oCFile);
            this.mParents.push(oCFile.getFileName());
            updateDirectoryList();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploader_cancel /* 2131362730 */:
                finish();
                return;
            case R.id.uploader_choose_folder /* 2131362731 */:
                this.mUploadPath = "";
                Iterator<String> it = this.mParents.iterator();
                while (it.hasNext()) {
                    this.mUploadPath += it.next() + File.separator;
                }
                if (!isPlainTextUpload()) {
                    Timber.d("Uploading file to dir %s", this.mUploadPath);
                    uploadFiles();
                    return;
                } else {
                    showUploadTextDialog();
                    return;
                }
            default:
                throw new IllegalArgumentException("Wrong element clicked");
        }
    }

    @Override // com.owncloud.android.ui.activity.DrawerActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Timber.i("result received. req: " + i + " res: " + i2, new Object[0]);
        if (i == 1) {
            dismissDialog(0);
            if (i2 == 0) {
                finish();
            }
            Account[] accountsByType = this.mAccountManager.getAccountsByType(MainApp.INSTANCE.getAuthTokenType());
            if (accountsByType.length == 0) {
                showDialog(0);
            } else {
                setAccount(accountsByType[0]);
                updateDirectoryList();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDirectoryList() {
        initToolbar(this.mParents.peek());
        String generatePath = generatePath(this.mParents);
        Timber.d("Populating view with content of : %s", generatePath);
        OCFile fileByPath = getStorageManager().getFileByPath(generatePath);
        this.mFile = fileByPath;
        if (fileByPath != null) {
            if (this.mAdapter == null) {
                ReceiveExternalFilesAdapter receiveExternalFilesAdapter = new ReceiveExternalFilesAdapter(this, getStorageManager(), getAccount());
                this.mAdapter = receiveExternalFilesAdapter;
                this.mListView.setAdapter((ListAdapter) receiveExternalFilesAdapter);
            }
            this.mAdapter.setNewItemVector(sortFileList(getStorageManager().getFolderContent(this.mFile)));
            ((Button) findViewById(R.id.uploader_choose_folder)).setOnClickListener(this);
            ((Button) findViewById(R.id.uploader_cancel)).setOnClickListener(this);
        }
    }

    private void initToolbar(String str) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.root_toolbar);
        toolbar.setVisibility(0);
        constraintLayout.setVisibility(8);
        if (str.equals("")) {
            str = getString(R.string.uploader_top_message);
        }
        toolbar.setTitle(str);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        boolean z = this.mParents.size() > 1;
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(z);
            supportActionBar.setHomeButtonEnabled(z);
        }
    }

    @Override // com.owncloud.android.ui.activity.FileActivity, com.owncloud.android.ui.dialog.SslUntrustedCertDialog.OnSslUntrustedCertListener
    public void onSavedCertificate() {
        startSyncFolderOperation(getCurrentDir());
    }

    private void startSyncFolderOperation(OCFile oCFile) {
        this.mSyncInProgress = true;
        new RefreshFolderOperation(oCFile, false, getAccount(), getApplicationContext()).execute(getStorageManager(), this, (OnRemoteOperationListener) null, (Handler) null);
    }

    private Vector<OCFile> sortFileList(Vector<OCFile> vector) {
        FileStorageUtils.mSortOrderFileDisp = Integer.valueOf(PreferenceManager.getSortOrder(this, 3));
        FileStorageUtils.mSortAscendingFileDisp = Boolean.valueOf(PreferenceManager.getSortAscending(this, 3));
        return new SortFilesUtils().sortFiles(vector, FileStorageUtils.mSortOrderFileDisp.intValue(), FileStorageUtils.mSortAscendingFileDisp.booleanValue());
    }

    private String generatePath(Stack<String> stack) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append("/");
        }
        return sb.toString();
    }

    private void prepareStreamsToUpload() {
        ArrayList<Uri> parcelableArrayListExtra;
        if (getIntent().getAction() != null && getIntent().getAction().equals("android.intent.action.SEND")) {
            Uri uri = (Uri) getIntent().getParcelableExtra("android.intent.extra.STREAM");
            if (uri != null) {
                this.mStreamsToUpload.add(uri);
            }
        } else if (getIntent().getAction().equals("android.intent.action.SEND_MULTIPLE") && (parcelableArrayListExtra = getIntent().getParcelableArrayListExtra("android.intent.extra.STREAM")) != null) {
            this.mStreamsToUpload = parcelableArrayListExtra;
        }
        if (isPlainTextUpload()) {
            return;
        }
        Iterator<Uri> it = this.mStreamsToUpload.iterator();
        while (it.hasNext()) {
            String uri2 = it.next().toString();
            if (uri2.contains("/data") && uri2.contains(getPackageName()) && !uri2.contains(getCacheDir().getPath()) && !uri2.contains(Environment.getExternalStorageDirectory().toString())) {
                finish();
            }
        }
    }

    private boolean somethingToUpload() {
        return !this.mStreamsToUpload.isEmpty() || isPlainTextUpload();
    }

    private boolean isPlainTextUpload() {
        return this.mStreamsToUpload.isEmpty() && getIntent().getStringExtra("android.intent.extra.TEXT") != null;
    }

    public void uploadFiles() {
        UriUploader.UriUploaderResultCode uploadUris = new UriUploader(this, this.mStreamsToUpload, this.mUploadPath, getAccount(), 2, true, this).uploadUris();
        PreferenceManager.setLastUploadPath(this.mUploadPath, this);
        if (uploadUris == UriUploader.UriUploaderResultCode.OK) {
            finish();
            return;
        }
        if (uploadUris != UriUploader.UriUploaderResultCode.COPY_THEN_UPLOAD) {
            int i = R.string.uploader_error_title_file_cannot_be_uploaded;
            int i2 = R.string.common_error_unknown;
            if (uploadUris == UriUploader.UriUploaderResultCode.ERROR_NO_FILE_TO_UPLOAD) {
                i2 = R.string.uploader_error_message_no_file_to_upload;
                i = R.string.uploader_error_title_no_file_to_upload;
            } else if (uploadUris == UriUploader.UriUploaderResultCode.ERROR_READ_PERMISSION_NOT_GRANTED) {
                i2 = R.string.uploader_error_message_read_permission_not_granted;
            }
            showErrorDialog(i2, i);
        }
    }

    @Override // com.owncloud.android.ui.activity.FileActivity, com.owncloud.android.lib.common.operations.OnRemoteOperationListener
    public void onRemoteOperationFinish(RemoteOperation remoteOperation, RemoteOperationResult remoteOperationResult) {
        super.onRemoteOperationFinish(remoteOperation, remoteOperationResult);
        if (remoteOperation instanceof CreateFolderOperation) {
            onCreateFolderOperationFinish((CreateFolderOperation) remoteOperation, remoteOperationResult);
        }
    }

    private void onCreateFolderOperationFinish(CreateFolderOperation createFolderOperation, RemoteOperationResult remoteOperationResult) {
        if (remoteOperationResult.isSuccess()) {
            updateDirectoryList();
            return;
        }
        try {
            showSnackMessage(ErrorMessageAdapter.INSTANCE.getResultMessage(remoteOperationResult, createFolderOperation, getResources()));
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "Error while trying to show fail message ", new Object[0]);
        }
    }

    private void initTargetFolder() {
        if (getStorageManager() == null) {
            throw new IllegalStateException("Do not call this method before initializing mStorageManager");
        }
        String lastUploadPath = PreferenceManager.getLastUploadPath(this);
        if (lastUploadPath.equals("/")) {
            this.mParents.add("");
        } else {
            String[] split = lastUploadPath.split("/");
            this.mParents.clear();
            this.mParents.addAll(Arrays.asList(split));
        }
        while (!getStorageManager().fileExists(generatePath(this.mParents)) && this.mParents.size() > 1) {
            this.mParents.pop();
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        this.mSearchView = searchView;
        searchView.setMaxWidth(Integer.MAX_VALUE);
        this.mSearchView.setQueryHint(getResources().getString(R.string.actionbar_search));
        this.mSearchView.setOnQueryTextFocusChangeListener(this);
        this.mSearchView.setOnQueryTextListener(this);
        menu.removeItem(menu.findItem(R.id.action_share_current_folder).getItemId());
        return true;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (this.mParents.size() <= 1) {
                return true;
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.owncloud.android.ui.activity.FileActivity, com.owncloud.android.ui.activity.DrawerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
    }

    private void sortByName(boolean z) {
        this.mAdapter.setSortOrder(0, z);
    }

    private void sortBySize(boolean z) {
        this.mAdapter.setSortOrder(2, z);
    }

    private void sortByDate(boolean z) {
        this.mAdapter.setSortOrder(1, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OCFile getCurrentFolder() {
        OCFile oCFile = this.mFile;
        if (oCFile == null) {
            return null;
        }
        if (oCFile.isFolder()) {
            return oCFile;
        }
        if (getStorageManager() != null) {
            return getStorageManager().getFileByPath(oCFile.getParentRemotePath());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void browseToRoot() {
        OCFile fileByPath = getStorageManager().getFileByPath(OCFile.ROOT_PATH);
        this.mFile = fileByPath;
        startSyncFolderOperation(fileByPath);
    }

    @Override // com.owncloud.android.presentation.ui.files.SortOptionsView.SortOptionsListener
    public void onSortTypeListener(SortType sortType, SortOrder sortOrder) {
        SortBottomSheetFragment newInstance = SortBottomSheetFragment.INSTANCE.newInstance(sortType, sortOrder);
        newInstance.setSortDialogListener(this);
        newInstance.show(getSupportFragmentManager(), SortBottomSheetFragment.TAG);
    }

    @Override // com.owncloud.android.presentation.ui.files.SortBottomSheetFragment.SortDialogListener
    public void onSortSelected(SortType sortType) {
        this.mSortOptionsView.setSortTypeSelected(sortType);
        boolean equals = this.mSortOptionsView.getSortOrderSelected().equals(SortOrder.SORT_ORDER_ASCENDING);
        if (sortType == SortType.SORT_TYPE_BY_NAME) {
            sortByName(equals);
        } else if (sortType == SortType.SORT_TYPE_BY_DATE) {
            sortByDate(equals);
        } else if (sortType == SortType.SORT_TYPE_BY_SIZE) {
            sortBySize(equals);
        }
    }

    @Override // com.owncloud.android.presentation.ui.files.SortOptionsView.CreateFolderListener
    public void onCreateFolderListener() {
        CreateFolderDialogFragment.newInstance(this.mFile).show(getSupportFragmentManager(), CreateFolderDialogFragment.CREATE_FOLDER_FRAGMENT);
    }

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        this.mAdapter.filterBySearch(str);
        return true;
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View view, boolean z) {
        if (z && this.mAdapter.getFiles().isEmpty()) {
            updateEmptyListMessage(getString(R.string.local_file_list_search_with_no_matches));
        } else if (!z && this.mAdapter.getFiles().isEmpty()) {
            updateEmptyListMessage(getString(R.string.file_list_empty));
        } else {
            updateEmptyListMessage(getString(R.string.empty));
        }
    }

    @Override // com.owncloud.android.ui.adapter.ReceiveExternalFilesAdapter.OnSearchQueryUpdateListener
    public void updateEmptyListMessage(String str) {
        this.mEmptyListMessage.setText(str);
    }

    @Override // com.owncloud.android.interfaces.ISecurityEnforced
    public void optionLockSelected(LockType lockType) {
        ActivityExtKt.manageOptionLockSelected(this, lockType);
    }

    private class SyncBroadcastReceiver extends BroadcastReceiver {
        private SyncBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Timber.d("Received broadcast %s", action);
            String stringExtra = intent.getStringExtra(FileSyncAdapter.EXTRA_ACCOUNT_NAME);
            String stringExtra2 = intent.getStringExtra(FileSyncAdapter.EXTRA_FOLDER_PATH);
            RemoteOperationResult remoteOperationResult = (RemoteOperationResult) intent.getSerializableExtra(FileSyncAdapter.EXTRA_RESULT);
            if ((ReceiveExternalFilesActivity.this.getAccount() == null || !stringExtra.equals(ReceiveExternalFilesActivity.this.getAccount().name) || ReceiveExternalFilesActivity.this.getStorageManager() == null) ? false : true) {
                if (FileSyncAdapter.EVENT_FULL_SYNC_START.equals(action)) {
                    ReceiveExternalFilesActivity.this.mSyncInProgress = true;
                } else {
                    OCFile fileByPath = ReceiveExternalFilesActivity.this.mFile == null ? null : ReceiveExternalFilesActivity.this.getStorageManager().getFileByPath(ReceiveExternalFilesActivity.this.mFile.getRemotePath());
                    OCFile fileByPath2 = ReceiveExternalFilesActivity.this.getCurrentFolder() != null ? ReceiveExternalFilesActivity.this.getStorageManager().getFileByPath(ReceiveExternalFilesActivity.this.getCurrentFolder().getRemotePath()) : null;
                    if (fileByPath2 != null) {
                        if (fileByPath == null && !ReceiveExternalFilesActivity.this.mFile.isFolder()) {
                            fileByPath = fileByPath2;
                        }
                        if (fileByPath2.getRemotePath().equals(stringExtra2)) {
                            ReceiveExternalFilesActivity.this.updateDirectoryList();
                        }
                        ReceiveExternalFilesActivity.this.mFile = fileByPath;
                    } else {
                        ReceiveExternalFilesActivity receiveExternalFilesActivity = ReceiveExternalFilesActivity.this;
                        receiveExternalFilesActivity.showSnackMessage(String.format(receiveExternalFilesActivity.getString(R.string.sync_current_folder_was_removed), ReceiveExternalFilesActivity.this.getCurrentFolder().getFileName()));
                        ReceiveExternalFilesActivity.this.browseToRoot();
                    }
                    ReceiveExternalFilesActivity.this.mSyncInProgress = (FileSyncAdapter.EVENT_FULL_SYNC_END.equals(action) || RefreshFolderOperation.EVENT_SINGLE_FOLDER_SHARES_SYNCED.equals(action)) ? false : true;
                    if (RefreshFolderOperation.EVENT_SINGLE_FOLDER_CONTENTS_SYNCED.equals(action) && remoteOperationResult != null && !remoteOperationResult.isSuccess()) {
                        if (remoteOperationResult.getCode() == RemoteOperationResult.ResultCode.UNAUTHORIZED || (remoteOperationResult.isException() && (remoteOperationResult.getException() instanceof AuthenticatorException))) {
                            ReceiveExternalFilesActivity.this.requestCredentialsUpdate();
                        } else if (RemoteOperationResult.ResultCode.SSL_RECOVERABLE_PEER_UNVERIFIED.equals(remoteOperationResult.getCode())) {
                            ReceiveExternalFilesActivity.this.showUntrustedCertDialog(remoteOperationResult);
                        }
                    }
                }
                Timber.d("Setting progress visibility to %s", Boolean.valueOf(ReceiveExternalFilesActivity.this.mSyncInProgress));
            }
        }
    }

    @Override // com.owncloud.android.ui.asynctasks.CopyAndUploadContentUrisTask.OnCopyTmpFilesTaskListener
    public void onTmpFilesCopied(RemoteOperationResult.ResultCode resultCode) {
        try {
            dismissLoadingDialog();
        } catch (IllegalStateException e) {
            Timber.e(e);
        }
        finish();
    }

    private void showErrorDialog(int i, int i2) {
        ConfirmationDialogFragment newInstance = ConfirmationDialogFragment.newInstance(i, new String[]{getString(R.string.app_name)}, i2, R.string.common_back, -1, -1);
        newInstance.setCancelable(false);
        newInstance.setOnConfirmationListener(new ConfirmationDialogFragment.ConfirmationDialogFragmentListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity.3
            @Override // com.owncloud.android.ui.dialog.ConfirmationDialogFragment.ConfirmationDialogFragmentListener
            public void onCancel(String str) {
            }

            @Override // com.owncloud.android.ui.dialog.ConfirmationDialogFragment.ConfirmationDialogFragmentListener
            public void onNeutral(String str) {
            }

            @Override // com.owncloud.android.ui.dialog.ConfirmationDialogFragment.ConfirmationDialogFragmentListener
            public void onConfirmation(String str) {
                ReceiveExternalFilesActivity.this.finish();
            }
        });
        newInstance.show(getSupportFragmentManager(), FTAG_ERROR_FRAGMENT);
    }

    private void showUploadTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.dialog_upload_text, (ViewGroup) null);
        builder.setView(inflate);
        builder.setTitle(R.string.uploader_upload_text_dialog_title);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.uploader_btn_upload_text, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
        final TextInputEditText textInputEditText = (TextInputEditText) inflate.findViewById(R.id.inputFileName);
        final TextInputLayout textInputLayout = (TextInputLayout) inflate.findViewById(R.id.inputTextLayout);
        textInputEditText.addTextChangedListener(new TextWatcher() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }
        });
        final AlertDialog create = builder.create();
        setFileNameFromIntent(create, textInputEditText);
        create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                ReceiveExternalFilesActivity.this.m363xe159d148(create, textInputEditText, textInputLayout, dialogInterface);
            }
        });
        create.show();
    }

    /* renamed from: lambda$showUploadTextDialog$3$com-owncloud-android-ui-activity-ReceiveExternalFilesActivity, reason: not valid java name */
    public /* synthetic */ void m363xe159d148(AlertDialog alertDialog, final TextInputEditText textInputEditText, final TextInputLayout textInputLayout, DialogInterface dialogInterface) {
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() { // from class: com.owncloud.android.ui.activity.ReceiveExternalFilesActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ReceiveExternalFilesActivity.this.m362xedca4d07(textInputEditText, textInputLayout, view);
            }
        });
    }

    /* renamed from: lambda$showUploadTextDialog$2$com-owncloud-android-ui-activity-ReceiveExternalFilesActivity, reason: not valid java name */
    public /* synthetic */ void m362xedca4d07(TextInputEditText textInputEditText, TextInputLayout textInputLayout, View view) {
        String str;
        String obj = textInputEditText.getText().toString();
        if (obj.length() > MAX_FILENAME_LENGTH) {
            str = String.format(getString(R.string.uploader_upload_text_dialog_filename_error_length_max), Integer.valueOf(MAX_FILENAME_LENGTH));
        } else if (obj.length() == 0) {
            str = getString(R.string.uploader_upload_text_dialog_filename_error_empty);
        } else {
            Uri savePlainTextToFile = savePlainTextToFile(obj + ".txt");
            this.mStreamsToUpload.clear();
            this.mStreamsToUpload.add(savePlainTextToFile);
            uploadFiles();
            str = null;
        }
        textInputLayout.setErrorEnabled(str != null);
        textInputLayout.setError(str);
    }

    private Uri savePlainTextToFile(String str) {
        String stringExtra = getIntent().getStringExtra("android.intent.extra.TEXT");
        try {
            File file = new File(getCacheDir(), str);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(stringExtra.getBytes());
            fileOutputStream.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            Timber.w(e, "Failed to create temp file for uploading plain text: %s", e.getMessage());
            return null;
        }
    }

    private void setFileNameFromIntent(AlertDialog alertDialog, EditText editText) {
        Window window;
        String stringExtra = getIntent().getStringExtra("android.intent.extra.SUBJECT");
        String stringExtra2 = getIntent().getStringExtra("android.intent.extra.TITLE");
        if (stringExtra == null) {
            stringExtra = stringExtra2;
        }
        editText.setText(stringExtra);
        editText.selectAll();
        if (stringExtra != null || (window = alertDialog.getWindow()) == null) {
            return;
        }
        window.setSoftInputMode(4);
    }

    private class UploadBroadcastReceiver extends BroadcastReceiver {
        private UploadBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean booleanExtra = intent.getBooleanExtra("RESULT", false);
            String stringExtra = intent.getStringExtra(Extras.EXTRA_OLD_FILE_PATH);
            if (booleanExtra && stringExtra.contains(ReceiveExternalFilesActivity.this.getCacheDir().getPath())) {
                FileStorageUtils.deleteDir(ReceiveExternalFilesActivity.this.getCacheDir());
            }
        }
    }
}
