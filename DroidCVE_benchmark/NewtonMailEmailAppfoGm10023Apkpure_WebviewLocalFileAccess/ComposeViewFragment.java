package com.cloudmagic.android.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cloudmagic.android.BaseActivity;
import com.cloudmagic.android.ComposeActivity;
import com.cloudmagic.android.activity.SnoozeTimeChooserActivity;
import com.cloudmagic.android.adapters.ComposeViewFromAddressAdapter;
import com.cloudmagic.android.adapters.EmailListAdapter;
import com.cloudmagic.android.adapters.ImageListAdapter;
import com.cloudmagic.android.adapters.InteractionSummaryListAdapter;
import com.cloudmagic.android.chips.BaseNetworkRecipientAdapter;
import com.cloudmagic.android.chips.Contact;
import com.cloudmagic.android.chips.NetworkRecipientAdapter;
import com.cloudmagic.android.chips.RecipientChip;
import com.cloudmagic.android.chips.RecipientEditTextView;
import com.cloudmagic.android.chips.RecipientEntry;
import com.cloudmagic.android.data.CMDBWrapper;
import com.cloudmagic.android.data.entities.AccountColor;
import com.cloudmagic.android.data.entities.Alias;
import com.cloudmagic.android.data.entities.Attachment;
import com.cloudmagic.android.data.entities.ConversationChange;
import com.cloudmagic.android.data.entities.ExternalContact;
import com.cloudmagic.android.data.entities.Folder;
import com.cloudmagic.android.data.entities.GalleryImage;
import com.cloudmagic.android.data.entities.Message;
import com.cloudmagic.android.data.entities.UserAccount;
import com.cloudmagic.android.data.entities.ViewConversation;
import com.cloudmagic.android.data.tables.CardListTable;
import com.cloudmagic.android.data.tables.PeopleProfileTable;
import com.cloudmagic.android.dialogs.AttachmentSizeOptimiseDialog;
import com.cloudmagic.android.dialogs.ConfirmationDialog;
import com.cloudmagic.android.dialogs.ErrorDialogFragment;
import com.cloudmagic.android.dialogs.NewtonFeatureExpiredDialog;
import com.cloudmagic.android.dialogs.PopupDialog;
import com.cloudmagic.android.dialogs.ProgressDialogFragment;
import com.cloudmagic.android.dialogs.SimpleDialogFragment;
import com.cloudmagic.android.dialogs.SnoozeDialogFragment;
import com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener;
import com.cloudmagic.android.fragments.ComposeSendExtrasFragment;
import com.cloudmagic.android.global.CalendarConstants;
import com.cloudmagic.android.global.Constants;
import com.cloudmagic.android.helper.AccountSettingsPreferences;
import com.cloudmagic.android.helper.CMCalendarHelper;
import com.cloudmagic.android.helper.CMLogger;
import com.cloudmagic.android.helper.ComposeFragmentHelper;
import com.cloudmagic.android.helper.ComposeState;
import com.cloudmagic.android.helper.ObjectStorageSingleton;
import com.cloudmagic.android.helper.UserPreferences;
import com.cloudmagic.android.network.api.response.APIError;
import com.cloudmagic.android.observers.ConversationViewChangeObserver;
import com.cloudmagic.android.observers.MessageResourceIDUpdateObserver;
import com.cloudmagic.android.payment.Newton.Newton;
import com.cloudmagic.android.payment.ProductFactory;
import com.cloudmagic.android.providers.PreviewDataProvider;
import com.cloudmagic.android.services.ActionService;
import com.cloudmagic.android.utils.AsyncTask;
import com.cloudmagic.android.utils.AttachmentUtils;
import com.cloudmagic.android.utils.ComposePayloadUtils;
import com.cloudmagic.android.utils.DateTimeUtils;
import com.cloudmagic.android.utils.ImageOptimizationsUtils;
import com.cloudmagic.android.utils.MailTo;
import com.cloudmagic.android.utils.Pair;
import com.cloudmagic.android.utils.PreferenceSettingsUtilities;
import com.cloudmagic.android.utils.Utilities;
import com.cloudmagic.android.view.ActionDoneChipAddressTextView;
import com.cloudmagic.android.view.ActionEditText;
import com.cloudmagic.android.view.AttachmentView;
import com.cloudmagic.android.view.ChipsAddressTextView;
import com.cloudmagic.android.view.ComposeSpinner;
import com.cloudmagic.android.view.CustomLinearLayout;
import com.cloudmagic.android.view.CustomTextView;
import com.cloudmagic.android.view.compose.ComposeMail;
import com.cloudmagic.android.widget.HorizontalListView;
import com.cloudmagic.mail.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ComposeViewFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener, BaseActivity.PermissionCallback, InteractionSummaryListAdapter.ClickOfContactsInterface, BaseNetworkRecipientAdapter.UserAccountGetter, RecipientEditTextView.KeyboardEventListener, ErrorDialogFragment.OnErrorDialogActionListener, ConversationViewChangeObserver.ConversationViewChangeObserverInterface, MessageResourceIDUpdateObserver.MessageResourceIDUpdateObserverInterface, PreviewDataProvider.Callback, AttachmentView.AttachmentViewListener {
    public static final String ATTACHMENT_LIST = "attachment_list";
    public static final String BCC = "bcc";
    public static final String BCC_VISIBILITY = "bcc_visibility";
    public static final String CC = "cc";
    public static final String CC_VISIBILITY = "cc_visibility";
    public static final String CHECKBOX_DRAWABLE = "checkbox_drawable";
    public static final String CUSTOM_ADDED_BCC = "custom_added_bcc";
    public static final String DRAFT_EDIT_FROM_LIST = "draft_edit_from_list";
    public static final String DRAFT_EDIT_FROM_PREVIEW = "draft_edit_from_preview";
    private static final long DRAFT_SAVE_WINDOW = 300000;
    public static final String EXTRA_ACCOUNT_ID = "composeviewfragment.account_id";
    public static final String EXTRA_ATTACHMENT = "composeviewfragment.attachments";
    public static final String EXTRA_FROM = "composeviewfragment.from";
    public static final String EXTRA_HTML_BODY = "composeviewfragment.html_body";
    public static final String EXTRA_IN_REPLY_TO = "composeviewfragment.in_reply_to";
    public static final String EXTRA_LAST_MESSAGE_SENDER = "composeviewfragment.last_message_sender";
    public static final String EXTRA_MESSAGE_ACTION_TYPE = "composeviewfragment.action_type";
    public static final String EXTRA_MESSAGE_TS_RECEIVED = "composeviewfragment.msg_ts_received";
    public static final String EXTRA_MIME_ID = "composeviewfragment.mime_id";
    public static final String EXTRA_MIME_IN_REPLY_TO_ID = "composeviewfragment.mime_in_reply_to_id";
    public static final String EXTRA_QUOTED_HTML_TEXT = "composeviewfragment.quoted_html_text";
    public static final String EXTRA_QUOTED_PLAIN_TEXT = "composeviewfragment.quoted_plain_text";
    public static final String EXTRA_REFERENCES = "composeviewfragment.references";
    public static final String EXTRA_REFERENCE_RESOURCE_ID = "composeviewfragment.reference_resource_id";
    public static final int IMAGE_CAPTURE_ATTACHMENT = 6;
    public static final String INITIAL_STATE = "initial_state";
    public static final String IS_DEFAULT_CM_ACTION = "default_cm_action";
    private static final String IS_EDIT_DRAFT = "is_edit_draft";
    private static final String IS_EDIT_OUTBOX = "is_edit_outbox";
    public static final String IS_MESSAGE_DIRTY = "is_message_dirty";
    public static final String IS_SIGNATURE_DIRTY = "is_signature_dirty";
    public static final String IS_STARRED = "is_starred";
    public static final String IS_SYSTEM_ACTION = "system_action";
    public static final String JS_INTERFACE = "external";
    public static final String OUTBOX_EDIT_FROM_LIST = "outbox_edit_from_list";
    public static final String OUTBOX_EDIT_FROM_PREVIEW = "outbox_edit_from_preview";
    public static final int PICK_ATTACHMENT = 4;
    public static final int PICK_BCC_ADDRESS_CONTACT = 2;
    public static final int PICK_CC_ADDRESS_CONTACT = 3;
    public static final int PICK_TO_ADDRESS_CONTACT = 1;
    private static final String QUOTED_TEXT_FILE = "quotedtextfile";
    public static final String REMIND_IF_NOT_REPLIED_TIME = "remind-if-not-replied-time";
    public static final String SEND_LATER_AUTO_CANCELABLE = "send-later-auto-cancelabel";
    public static final String SEND_LATER_TIME = "send-later-time";
    public static final String SHOULD_NOTIFY_WHEN_READ = "should-notify-when-read";
    public static final String TAG = "compose_view_fragment";
    public static final String THREAD_CONTAINS_SENT = "thread_contains_sent";
    public static final String TO = "to";
    public static final String WEBVIEW_FOCUS_STATE = "webview_focus_state";
    public static String shouldAutoBccFromPrefs = "should_load_from_prefs";
    private HorizontalListView attachmentImagecontainer;
    private LinearLayout attachmentLayout;
    private LinearLayout attachmentPickerContainer;
    private LinearLayout attachmentTable;
    private AttachmentTypesClickListener attachmentTypesListener;
    private ArrayList<String> attachmentsFromBundle;
    private RelativeLayout bccAddressContactPickerContainer;
    private LinearLayout bccAddressContainer;
    private ActionDoneChipAddressTextView bccAddressTextView;
    private CustomTextView bccHeader;
    private NetworkRecipientAdapter bccRecipientAdapter;
    private LinearLayout bottomLayoutContainer;
    private RelativeLayout ccAddressContactPickerContainer;
    private LinearLayout ccAddressContainer;
    private ActionDoneChipAddressTextView ccAddressTextView;
    private CustomTextView ccHeader;
    private NetworkRecipientAdapter ccRecipientAdapter;
    private LinearLayout composeAttachmentLayout;
    private LinearLayout composeContainer;
    private LinearLayout composeExtrasContainer;
    private CustomTextView composeHeader;
    private ComposeFragmentHelper composeHelper;
    private ComposeMail composeMail;
    private LinearLayout compose_web_attachment_view;
    private DraftsTextWatcher draftsTextWatcher;
    private LinearLayout emailSuggestionContainer;
    private HorizontalListView emailSuggestionView;
    private View emptyImagesMsgContainer;
    private TextView emptyImagesMsgContainerButton;
    private View extContactStrip;
    private ImageButton followUpButton;
    private FromFieldSelection fromFieldSelection;
    private ComposeSpinner fromFieldSpinner;
    private GestureDetector gd;
    private List<GalleryImage> imageLst;
    private ComposeState initialAddressState;
    private boolean isFrequectContactsEmpty;
    private boolean isKeyboardVisible;
    private boolean isSnoozeTimePickerActivityActive;
    private View layoutRoot;
    private int mAccountID;
    private int mAccountType;
    private String mActionTrigger;
    private ArrayList<Attachment> mAttachment;
    private String[] mAutoBCC;
    private String[] mBCC;
    private int mBCCVisibility;
    private PopupDialog mBccRecipientAddressMenuPopup;
    private String mBody;
    private String[] mCC;
    private List<Pair> mCCReferences;
    private int mCCVisibility;
    private FloatingActionButton mCameraFab;
    private PopupDialog mCcRecipientAddressMenuPopup;
    private int mCheckBoxDrawable;
    Context mContext;
    private ConversationViewChangeObserver mConversationChangeObserver;
    private Folder mCurrentFolder;
    private String mExternalContact;
    private LinearLayout mFileMgrImageContainer;
    private int[] mFolderIdList;
    private int[] mFolderTypeList;
    private String[] mFrom;
    private String mInReplyTo;
    private boolean mIsAutoCancellable;
    private boolean mIsEditDraft;
    private boolean mIsEditOutbox;
    private boolean mIsSendAgain;
    private boolean mIsSentUsingCMSet;
    private Pair mLastMessageSender;
    private String mMailEditSource;
    private MessageResourceIDUpdateObserver mMessageResourceIDUpdateObserver;
    private String mMimeId;
    private String mMimeInReplyToId;
    private String mOldDiscardDraftPayload;
    private int mOldWebViewHeightForWebViewKeyUp;
    private boolean mOpenQuotedText;
    private LinearLayout mRecentPicturesContainer;
    private ViewConversation mReferenceConversation;
    private String mReferenceResourceId;
    private String mReferences;
    private long mSendLaterTime;
    private PreviewDataProvider mService;
    private boolean mShowAttachmentWhileScroll;
    private String mSignature;
    private String mSignatureAttachmentData;
    private long mSnoozeTime;
    private String mSubject;
    private long mTSMessageReceived;
    private String[] mTo;
    private PopupDialog mToRecipientAddressMenuPopup;
    private List<Pair> mToReferences;
    private RelativeLayout menuButtonContainerTo;
    private Message messageToSend;
    private ImageButton quotedTextButton;
    private ImageButton recentPicsButton;
    private Map<String, List<RecipientChip>> recipientChipMap;
    private ImageButton removeQuotedTextButton;
    private CustomLinearLayout rootContainer;
    private View rrEnabledSnackbar;
    private ScrollView scrollView;
    private LinearLayout sendButton;
    private View sendLaterPicker;
    private int shouldNotifyWhenRead;
    private WebView signatureWebview;
    private Animation slideInFromBottom;
    private Animation slideOutFromBottom;
    private ActionEditText subjectView;
    private LinearLayout toAddressContainer;
    private ActionDoneChipAddressTextView toAddressTextView;
    private CustomTextView toHeader;
    private NetworkRecipientAdapter toRecipientAdapter;
    private WebView webView;
    private RelativeLayout webViewContainer;
    private boolean mSaveAsDraft = false;
    private boolean mDraftSavedPreviously = false;
    private boolean mNewActivityStarted = false;
    private boolean mMailSendingOrSavingIsInProcess = false;
    private boolean mIsMailSent = false;
    private boolean mOnKeyboardDown = false;
    private String mime = "text/html";
    private String encoding = "utf-8";
    private String mQuotedText = null;
    private int mActualMessageAccountId = -1;
    private boolean mHasAttachments = false;
    private String mCMSignature = null;
    ArrayList<String> mUrisToHandleAfterPermission = new ArrayList<>();
    private boolean mIsBodyHTML = true;
    private boolean mIsQuotedTextHTML = true;
    private String mActionType = ActionService.ACTION_TYPE_COMPOSE;
    private long mTSReminder = -1;
    private boolean mIsStarred = false;
    private int mTouchPositionOnRootElement = -1;
    private Boolean mIsWebViewTouchDetected = false;
    private Boolean mIsWebViewFocused = false;
    private Boolean mShouldWebViewGetFocus = true;
    private String mPreviousDraftId = null;
    private Boolean isWebViewLoaded = false;
    private final int EXTRA_SCROLL_PADDING = 30;
    private Handler handler = new Handler();
    private Boolean mIsMessageDirty = false;
    private Boolean mIsSignatureDirty = false;
    private Boolean mIsBodyDirty = true;
    private boolean mIsQuotedTextDirty = false;
    private boolean mGoBackOnSaveDraft = false;
    boolean doubletap = false;
    boolean longPress = false;
    private boolean isTablet = false;
    private boolean isTablet10 = false;
    private boolean isLandscape = false;
    private int mInitialAccountIdSet = -1;
    private boolean shouldLoadFromPrefs = true;
    private ArrayList<String> customAddedBcc = new ArrayList<>();
    private Contact contact = null;
    Timer mTimer = null;
    private boolean mThreadContainsSent = false;
    private boolean mReplyArchiveAllowed = false;
    private boolean mIsSearchModeActive = false;
    private int keyBoardHeight = 0;
    private boolean mIsCustomReminder = false;

    /* loaded from: classes.dex */
    public enum FromFieldSelection {
        INIT,
        CHANGED,
        AUTO,
        NONE
    }

    public boolean isReminderViewVisible() {
        return false;
    }

    @Override // com.cloudmagic.android.view.AttachmentView.AttachmentViewListener
    public void onAttachmentViewRendered() {
    }

    @Override // com.cloudmagic.android.dialogs.ErrorDialogFragment.OnErrorDialogActionListener
    public void onCancel() {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onConversationChanged(int i, List<ConversationChange> list) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onDraftOrOutboxMessageDeleted(Message message, Message message2, boolean z) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onEmailSyncComplete() {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onFlushRequested() {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onForceRefreshDone(String str) {
    }

    @Override // com.cloudmagic.android.providers.PreviewDataProvider.Callback
    public void onLazyLoadedMessage(Message message) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onLocalDraftFolderDeleted(int i) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onMaintenance(String str) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onMessageTypeChanged(String str, int i) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onMessagesChanged(List<Message> list, boolean z) {
    }

    @Override // com.cloudmagic.android.providers.PreviewDataProvider.Callback
    public void onOlderMessageResponse(@NotNull List<? extends Message> list, @Nullable ViewConversation viewConversation, @Nullable UserAccount userAccount) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onOutboxMessageResendError(ViewConversation viewConversation) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onOutboxMessageResent(ViewConversation viewConversation) {
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onSingleDraftConversationChanged(ViewConversation viewConversation, ViewConversation viewConversation2) {
    }

    @Override // com.cloudmagic.android.dialogs.ErrorDialogFragment.OnErrorDialogActionListener
    public void onTryAgain() {
    }

    @Override // com.cloudmagic.android.BaseActivity.PermissionCallback
    public void permissionDenied(int i, Bundle bundle) {
    }

    @Override // com.cloudmagic.android.view.AttachmentView.AttachmentViewListener
    public void updateShareAttachmentDownloadCount(Attachment attachment) {
    }

    @Override // com.cloudmagic.android.fragments.BaseFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity().getApplicationContext();
        this.composeHelper = new ComposeFragmentHelper(getActivity());
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            if (getArguments().getInt("notification_id", -999) == -999) {
                initializeFromBundle(arguments, getActivity().getIntent().getAction());
            }
        } else {
            this.mAccountID = -1;
            if (getActivity().getIntent().getData() != null) {
                initializeFromDataUri(getActivity().getIntent().getData());
            }
        }
        if (bundle != null) {
            this.mIsStarred = bundle.getBoolean(IS_STARRED);
            this.mIsEditDraft = bundle.getBoolean(IS_EDIT_DRAFT, false);
            this.mIsEditOutbox = bundle.getBoolean(IS_EDIT_OUTBOX, false);
            this.mThreadContainsSent = bundle.getBoolean(THREAD_CONTAINS_SENT, false);
            this.mCheckBoxDrawable = bundle.getInt(CHECKBOX_DRAWABLE);
            this.mAttachment = bundle.getParcelableArrayList(ATTACHMENT_LIST);
            if (this.mAttachment != null && this.mAttachment.size() > 0) {
                this.mHasAttachments = true;
            }
            this.attachmentsFromBundle = bundle.getStringArrayList("attachments_from_bundle");
            this.mCCVisibility = bundle.getInt(CC_VISIBILITY);
            this.mBCCVisibility = bundle.getInt(BCC_VISIBILITY);
            this.mShouldWebViewGetFocus = Boolean.valueOf(bundle.getBoolean(WEBVIEW_FOCUS_STATE));
            this.mIsMessageDirty = Boolean.valueOf(bundle.getBoolean(IS_MESSAGE_DIRTY));
            this.mIsSignatureDirty = Boolean.valueOf(bundle.getBoolean(IS_SIGNATURE_DIRTY));
            this.mTo = bundle.getStringArray("to");
            this.mCC = bundle.getStringArray("cc");
            this.mBCC = bundle.getStringArray("bcc");
            if (Build.VERSION.SDK_INT >= 24) {
                getStoredQuotedTextFromFile();
            } else {
                String string = bundle.getString(EXTRA_QUOTED_PLAIN_TEXT, null);
                if (string == null) {
                    this.mQuotedText = bundle.getString(EXTRA_QUOTED_HTML_TEXT, null);
                } else {
                    this.mQuotedText = string;
                    this.mIsQuotedTextHTML = false;
                }
            }
            this.mSnoozeTime = bundle.getLong(REMIND_IF_NOT_REPLIED_TIME);
            this.mSendLaterTime = bundle.getLong("send-later-time");
            this.mIsAutoCancellable = bundle.getBoolean(SEND_LATER_AUTO_CANCELABLE);
            this.shouldNotifyWhenRead = bundle.getInt(SHOULD_NOTIFY_WHEN_READ);
            this.initialAddressState = (ComposeState) bundle.getParcelable(INITIAL_STATE);
            this.customAddedBcc = bundle.getStringArrayList(CUSTOM_ADDED_BCC);
            this.mIsSearchModeActive = bundle.getBoolean("is_search_active");
            String currentDraftResourceId = UserPreferences.getInstance(getActivity()).getCurrentDraftResourceId();
            if (currentDraftResourceId != null && currentDraftResourceId.length() > 0) {
                new FetchDraftDataAsyncTask().execute(currentDraftResourceId);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 24) {
                File file = new File(this.mContext.getFilesDir(), QUOTED_TEXT_FILE);
                if (file.exists()) {
                    file.delete();
                }
            }
            this.mCheckBoxDrawable = R.drawable.star;
            this.mCCVisibility = 8;
            this.mBCCVisibility = 8;
            this.mShouldWebViewGetFocus = true;
            if (((Newton) ProductFactory.getProduct(0, getActivity())).canAccessFeature(3)) {
                UserPreferences userPreferences = UserPreferences.getInstance(getContext());
                if (userPreferences.isRREnabled() && userPreferences.isRREnabledForAllEmails()) {
                    if (userPreferences.isRRNotifyEnableForAllEmails()) {
                        this.shouldNotifyWhenRead = 1;
                    } else {
                        this.shouldNotifyWhenRead = 0;
                    }
                }
            }
            UserPreferences.getInstance(getActivity()).setCurrentDraftResourceId(null);
        }
        this.fromFieldSelection = FromFieldSelection.NONE;
    }

    @Override // com.cloudmagic.android.providers.PreviewDataProvider.Callback
    public void onMessageResponse(@NotNull List<? extends Message> list, @Nullable ViewConversation viewConversation, @Nullable UserAccount userAccount) {
        if (getActivity() == null) {
            return;
        }
        hideDialog();
        if (viewConversation == null || viewConversation.doesNotExist || list == null || list.size() == 0) {
            if (getActivity() != null) {
                showError(3, this.mContext.getResources().getString(R.string.conversation_does_not_exist));
            }
        } else {
            for (Message message : list) {
                if (message.messageResourceId.equals(getArguments().getString("message_server_id"))) {
                    new ProcessMessageResponseTask(message, viewConversation).execute(new Void[0]);
                    return;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class FetchDraftDataAsyncTask extends AsyncTask<String, Void, Void> {
        private FetchDraftDataAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public Void doInBackground(String... strArr) {
            if (ComposeViewFragment.this.getActivity() == null || strArr[0] == null) {
                return null;
            }
            ComposeViewFragment.this.mPreviousDraftId = strArr[0];
            ComposeViewFragment.this.mIsEditDraft = true;
            ComposeViewFragment.this.mMailEditSource = null;
            CMDBWrapper cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
            Message messageWithoutBody = cMDBWrapper.getMessageWithoutBody(ComposeViewFragment.this.mPreviousDraftId);
            if (messageWithoutBody != null) {
                if (messageWithoutBody.referenceResourceId != null && messageWithoutBody.referenceResourceId.length() > 0) {
                    ComposeViewFragment.this.mReferenceResourceId = messageWithoutBody.referenceResourceId;
                } else {
                    ComposeViewFragment.this.mReferenceResourceId = ComposeViewFragment.this.mPreviousDraftId;
                }
                ComposeViewFragment.this.mReferenceConversation = cMDBWrapper.getCompleteConversationWithFolderMapping(messageWithoutBody.conversationId, ComposeViewFragment.this.mPreviousDraftId);
            }
            cMDBWrapper.close();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] getBCCFromPreferences() {
        Exception e;
        String[] strArr;
        AccountSettingsPreferences accountSettingsPreferences = AccountSettingsPreferences.getInstance(getActivity());
        String typeAutoBccEmails = accountSettingsPreferences.getTypeAutoBccEmails(accountSettingsPreferences.getPreferenceKey(this.mAccountID, "list_of_auto_bcc_emails"));
        if (typeAutoBccEmails == null) {
            return null;
        }
        try {
            JSONArray jSONArray = new JSONArray(typeAutoBccEmails);
            strArr = new String[jSONArray.length()];
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                    strArr[i] = Utilities.getFormattedAddress(jSONArray2.getString(0), jSONArray2.getString(1));
                } catch (Exception e2) {
                    e = e2;
                    e.printStackTrace();
                    return strArr;
                }
            }
            return strArr;
        } catch (Exception e3) {
            e = e3;
            strArr = null;
        }
    }

    private void initializeFromDataUri(Uri uri) {
        if (MailTo.isMailTo(uri.toString())) {
            try {
                MailTo parse = MailTo.parse(MailTo.MAILTO_SCHEME + Utilities.mailToEncoding(uri.toString().replace(MailTo.MAILTO_SCHEME, "")));
                this.mTo = Utilities.getStringArrayFromCommaSeparatedStrinbg(parse.getTo());
                this.mCC = Utilities.getStringArrayFromCommaSeparatedStrinbg(parse.getCc());
                if (parse.getSubject() != null) {
                    this.mSubject = parse.getSubject();
                }
                if (parse.getBody() != null) {
                    this.mBody = parse.getBody();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeFromBundle(Bundle bundle, String str) {
        this.mReferenceConversation = (ViewConversation) bundle.getParcelable("conversation");
        this.mCurrentFolder = (Folder) bundle.getParcelable("current_folder");
        this.mOpenQuotedText = bundle.getBoolean("open_quoted_text");
        this.mAccountID = bundle.getInt(EXTRA_ACCOUNT_ID, -1);
        this.mActualMessageAccountId = this.mAccountID;
        this.mReferenceResourceId = bundle.getString(EXTRA_REFERENCE_RESOURCE_ID);
        this.mReferences = bundle.getString(EXTRA_REFERENCES);
        this.mMimeId = bundle.getString(EXTRA_MIME_ID);
        this.mMimeInReplyToId = bundle.getString(EXTRA_MIME_IN_REPLY_TO_ID);
        this.mInReplyTo = bundle.getString(EXTRA_IN_REPLY_TO);
        this.mFrom = bundle.getStringArray(EXTRA_FROM);
        this.mTo = bundle.getStringArray("android.intent.extra.EMAIL");
        this.mCC = bundle.getStringArray("android.intent.extra.CC");
        this.mActionTrigger = bundle.getString("action_trigger");
        this.shouldLoadFromPrefs = bundle.getBoolean(shouldAutoBccFromPrefs, true);
        this.mSubject = bundle.getString("android.intent.extra.SUBJECT");
        this.mTSMessageReceived = bundle.getLong(EXTRA_MESSAGE_TS_RECEIVED);
        this.mLastMessageSender = (Pair) bundle.getParcelable(EXTRA_LAST_MESSAGE_SENDER);
        if (bundle.getString(EXTRA_MESSAGE_ACTION_TYPE) != null) {
            this.mActionType = bundle.getString(EXTRA_MESSAGE_ACTION_TYPE);
        }
        if (this.mTo == null && getActivity().getIntent().getData() != null) {
            initializeFromDataUri(getActivity().getIntent().getData());
        }
        if (bundle.getCharSequence("android.intent.extra.TEXT") != null) {
            this.mBody = bundle.getCharSequence("android.intent.extra.TEXT").toString();
            if (getActivity().getIntent().getType() != null && getActivity().getIntent().getType().equals("text/plain")) {
                this.mIsBodyHTML = true;
            }
        } else if (bundle.getString(EXTRA_HTML_BODY) != null) {
            this.mIsBodyHTML = true;
            this.mBody = bundle.getString(EXTRA_HTML_BODY);
        }
        ObjectStorageSingleton objectStorageSingleton = ObjectStorageSingleton.getInstance(this.mContext);
        if (objectStorageSingleton.getObject(EXTRA_QUOTED_PLAIN_TEXT) != null) {
            this.mQuotedText = (String) objectStorageSingleton.getObject(EXTRA_QUOTED_PLAIN_TEXT);
            this.mIsQuotedTextHTML = false;
        } else if (objectStorageSingleton.getObject(EXTRA_QUOTED_HTML_TEXT) != null) {
            this.mQuotedText = (String) objectStorageSingleton.getObject(EXTRA_QUOTED_HTML_TEXT);
        }
        objectStorageSingleton.removeObject(EXTRA_QUOTED_PLAIN_TEXT);
        objectStorageSingleton.removeObject(EXTRA_QUOTED_HTML_TEXT);
        if (bundle.getParcelableArrayList(EXTRA_ATTACHMENT) != null) {
            this.mHasAttachments = true;
            this.mAttachment = new ArrayList<>();
            this.mAttachment = bundle.getParcelableArrayList(EXTRA_ATTACHMENT);
            if (this.mAttachment.size() > 0) {
                this.attachmentsFromBundle = new ArrayList<>();
                Iterator<Attachment> it = this.mAttachment.iterator();
                while (it.hasNext()) {
                    this.attachmentsFromBundle.add(it.next().name);
                }
            }
        }
        if (str != null && (((str.equals("android.intent.action.SEND_MULTIPLE") && bundle.getParcelableArrayList("android.intent.extra.STREAM") != null) || ((str.equals("android.intent.action.SEND") || str.equals("android.intent.action.SENDTO")) && bundle.getParcelable("android.intent.extra.STREAM") != null)) && getActivity().getIntent().getType() != null && getActivity().getIntent().getType().equals("text/plain"))) {
            this.mIsBodyHTML = true;
        }
        handleAttachmentsFromBundle(str, bundle);
        if (TextUtils.isEmpty(this.mActionTrigger) && ((this.mSubject != null && !this.mSubject.isEmpty()) || ((this.mBody != null && !this.mBody.isEmpty()) || ((this.mQuotedText != null && !this.mQuotedText.isEmpty()) || ((this.mTo != null && this.mTo.length > 0) || (this.mCC != null && this.mCC.length > 0)))))) {
            this.mIsMessageDirty = true;
        }
        this.mToReferences = bundle.getParcelableArrayList("to_addresses_reference");
        this.mCCReferences = bundle.getParcelableArrayList("cc_addresses_reference");
        this.mFolderIdList = bundle.getIntArray("folder_id_list");
        this.mFolderTypeList = bundle.getIntArray("folder_type_list");
        this.mIsEditDraft = bundle.getBoolean(IS_EDIT_DRAFT, false);
        this.mIsSendAgain = bundle.getBoolean("is_send_again", false);
        this.mPreviousDraftId = bundle.getString("previous_draft_id");
        this.mSendLaterTime = bundle.getLong("ts_send");
        this.mSnoozeTime = bundle.getLong(SnoozeTimeChooserBaseFragment.TIME_SET);
        this.mIsAutoCancellable = bundle.getBoolean("auto_send_cancellable");
        this.shouldNotifyWhenRead = bundle.getInt("notify_when_read");
        this.mOldDiscardDraftPayload = bundle.getString("old_discard_draft_payload", null);
        this.mMailEditSource = bundle.getString("mail_edit_source", null);
        this.mIsEditOutbox = bundle.getBoolean(IS_EDIT_OUTBOX, false);
        this.mBCC = bundle.getStringArray("android.intent.extra.BCC");
        if (this.mBCC != null && this.mBCC.length > 0) {
            for (String str2 : this.mBCC) {
                this.customAddedBcc.add(str2);
            }
        }
        this.mThreadContainsSent = bundle.getBoolean(THREAD_CONTAINS_SENT);
        if (this.shouldLoadFromPrefs) {
            this.mAutoBCC = getBCCFromPreferences();
        }
        this.mIsSearchModeActive = bundle.getBoolean("is_search_active", false);
    }

    private void handleAttachmentsFromBundle(String str, Bundle bundle) {
        if (str != null && str.equals("android.intent.action.SEND_MULTIPLE") && bundle.getParcelableArrayList("android.intent.extra.STREAM") != null) {
            ArrayList parcelableArrayList = bundle.getParcelableArrayList("android.intent.extra.STREAM");
            if (TextUtils.isEmpty(this.mActionTrigger)) {
                this.mIsMessageDirty = true;
            }
            for (int i = 0; i < parcelableArrayList.size(); i++) {
                AttachmentFileInfo fileInfo = getFileInfo((Uri) parcelableArrayList.get(i));
                if (fileInfo != null) {
                    if (fileInfo.requireReadPermission) {
                        this.mUrisToHandleAfterPermission.add(((Uri) parcelableArrayList.get(i)).toString());
                    } else {
                        addAttachmentFromBundle(fileInfo);
                    }
                }
            }
            return;
        }
        if (str != null) {
            if ((str.equals("android.intent.action.SEND") || str.equals("android.intent.action.SENDTO")) && bundle.getParcelable("android.intent.extra.STREAM") != null) {
                if (TextUtils.isEmpty(this.mActionTrigger)) {
                    this.mIsMessageDirty = true;
                }
                Uri uri = (Uri) bundle.getParcelable("android.intent.extra.STREAM");
                AttachmentFileInfo fileInfo2 = getFileInfo(uri);
                if (fileInfo2 != null) {
                    if (fileInfo2.requireReadPermission) {
                        this.mUrisToHandleAfterPermission.add(uri.toString());
                    } else {
                        addAttachmentFromBundle(fileInfo2);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addAttachmentFromBundle(final AttachmentFileInfo attachmentFileInfo) {
        if (!attachmentFileInfo.isExternalFile && attachmentFileInfo.filePath != null) {
            addAttachmentView(new File(attachmentFileInfo.filePath), this.mReferenceResourceId, false);
        } else {
            new Thread(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ComposeViewFragment.this.getActivity() != null) {
                        final File file = Utilities.getFile(ComposeViewFragment.this.mContext, attachmentFileInfo.filePath, attachmentFileInfo.uri);
                        if (ComposeViewFragment.this.getActivity() != null) {
                            ComposeViewFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    ComposeViewFragment.this.addAttachmentView(file, ComposeViewFragment.this.mReferenceResourceId, false);
                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }

    public void updateActionBarColor(int i) {
        AccountColor accountColor;
        if (getActivity() == null) {
            return;
        }
        if (i == -1) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color)));
            if (Utilities.isHolo()) {
                return;
            }
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
            return;
        }
        if (Constants.accountIdColorMap == null || (accountColor = Constants.accountIdColorMap.get(Integer.valueOf(i))) == null) {
            return;
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(accountColor.colorLight));
        if (Utilities.isHolo()) {
            return;
        }
        getActivity().getWindow().setStatusBarColor(accountColor.colorDark);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEmailSuggestionContainer(ChipsAddressTextView chipsAddressTextView, boolean z) {
        if (this.isFrequectContactsEmpty || this.emailSuggestionContainer.getVisibility() == 0) {
            if (this.isFrequectContactsEmpty) {
                hideEmailSuggestionContainer();
            }
        } else {
            if (z) {
                refreshSuggestionList();
            }
            if (this.emailSuggestionContainer.isShown() || this.rrEnabledSnackbar.getVisibility() != 8) {
                return;
            }
            this.scrollView.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.2
                @Override // java.lang.Runnable
                public void run() {
                    if (ComposeViewFragment.this.getActivity() == null) {
                        return;
                    }
                    ComposeViewFragment.this.emailSuggestionContainer.setVisibility(0);
                }
            }, 300L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideExternalContactStripe() {
        this.extContactStrip.setVisibility(8);
    }

    public void hideEmailSuggestionContainer() {
        if (this.emailSuggestionContainer.isShown()) {
            this.emailSuggestionContainer.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSuggestionList() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.toAddressTextView.getChips());
        arrayList.addAll(this.ccAddressTextView.getChips());
        arrayList.addAll(this.bccAddressTextView.getChips());
        ((EmailListAdapter) this.emailSuggestionView.getAdapter()).updateRemovedContactList(convertToContactWrapperLst(arrayList));
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        customizeActionBar();
        if (bundle == null && getArguments() != null && getArguments().getInt("notification_id", -999) != -999) {
            showProgressDialog(null);
            this.mService = new PreviewDataProvider();
            this.mService.getMessagesForNotification(this.mContext, getArguments().getString("conversation_server_id"), getArguments().getString("message_resource_id"), getArguments().getInt("account_id"), getArguments().getString("notification_tag"), null);
            this.mService.setMessageListReceiver(this);
        }
        setHasOptionsMenu(true);
        this.isTablet = getResources().getBoolean(R.bool.isTablet);
        this.isTablet10 = getResources().getBoolean(R.bool.isTablet10);
        this.isLandscape = getResources().getConfiguration().orientation == 2;
        final View inflate = layoutInflater.inflate(R.layout.compose_fragment, viewGroup, false);
        this.layoutRoot = inflate;
        this.scrollView = (ScrollView) inflate.findViewById(R.id.compose_scroll_view);
        this.rootContainer = (CustomLinearLayout) inflate.findViewById(R.id.rootContainer);
        this.rrEnabledSnackbar = inflate.findViewById(R.id.rr_enabled_snackbar_in_compose);
        this.bottomLayoutContainer = (LinearLayout) inflate.findViewById(R.id.bottom_layout_container);
        this.composeExtrasContainer = (LinearLayout) inflate.findViewById(R.id.compose_extras_container);
        inflate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.3
            private static final int DIFF_THESHOLD = 180;
            private static final int DIFF_THESHOLD_10_TABLET = 140;
            private View composeContainer;

            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                int height;
                Rect rect = new Rect();
                inflate.getWindowVisibleDisplayFrame(rect);
                if (ComposeViewFragment.this.isTablet10) {
                    if (this.composeContainer == null) {
                        this.composeContainer = ComposeViewFragment.this.getActivity().findViewById(R.id.compose_container);
                    }
                    height = inflate.getRootView().getHeight() - this.composeContainer.getHeight();
                } else {
                    height = inflate.getRootView().getHeight() - (rect.bottom - rect.top);
                }
                int statusBarHeight = height - ComposeViewFragment.this.getStatusBarHeight();
                if (statusBarHeight > getDiffThreshold() && !ComposeViewFragment.this.isAttachmentComponentVisible()) {
                    ComposeViewFragment.this.setUpAttachmentContainerHeight(statusBarHeight);
                    ChipsAddressTextView focusedChipAddressTextView = ComposeViewFragment.this.getFocusedChipAddressTextView();
                    if (focusedChipAddressTextView != null && !ComposeViewFragment.this.mOnKeyboardDown) {
                        ComposeViewFragment.this.showEmailSuggestionContainer(focusedChipAddressTextView, true);
                        ComposeViewFragment.this.isKeyboardVisible = true;
                        return;
                    } else {
                        ComposeViewFragment.this.hideEmailSuggestionContainer();
                        ComposeViewFragment.this.mOnKeyboardDown = false;
                        return;
                    }
                }
                ComposeViewFragment.this.hideEmailSuggestionContainer();
                ComposeViewFragment.this.mOnKeyboardDown = false;
                ComposeViewFragment.this.isKeyboardVisible = false;
            }

            private int getDiffThreshold() {
                return ComposeViewFragment.this.isTablet10 ? DIFF_THESHOLD_10_TABLET : DIFF_THESHOLD;
            }
        });
        if ((getArguments() != null && getArguments().getInt("notification_id", -999) != -999) || this.mActionType.equals(ActionService.ACTION_TYPE_REPLY) || this.mActionType.equals("reply_all")) {
            this.rootContainer.setVisibility(4);
        }
        this.toAddressTextView = (ActionDoneChipAddressTextView) inflate.findViewById(R.id.multiAutoCompleteTextView);
        this.toAddressTextView.getViewTreeObserver();
        this.toAddressContainer = (LinearLayout) inflate.findViewById(R.id.toAddressContainer);
        this.ccAddressTextView = (ActionDoneChipAddressTextView) inflate.findViewById(R.id.multiAutoCompleteTextViewCc);
        this.ccAddressContainer = (LinearLayout) inflate.findViewById(R.id.ccAddressContainer);
        this.bccAddressTextView = (ActionDoneChipAddressTextView) inflate.findViewById(R.id.multiAutoCompleteTextViewBcc);
        this.bccAddressContainer = (LinearLayout) inflate.findViewById(R.id.bccAddressContainer);
        this.menuButtonContainerTo = (RelativeLayout) inflate.findViewById(R.id.menuButtonContainer);
        this.bccAddressContactPickerContainer = (RelativeLayout) inflate.findViewById(R.id.bccAddressContactPickerContainer);
        this.ccAddressContactPickerContainer = (RelativeLayout) inflate.findViewById(R.id.ccAddressContactPickerContainer);
        this.attachmentLayout = (LinearLayout) inflate.findViewById(R.id.attachmentLayout);
        this.attachmentTable = (LinearLayout) inflate.findViewById(R.id.attachmentTable);
        this.attachmentPickerContainer = (LinearLayout) inflate.findViewById(R.id.attachment_picker_container);
        this.attachmentImagecontainer = (HorizontalListView) inflate.findViewById(R.id.compose_attachment_image_container);
        this.emptyImagesMsgContainer = inflate.findViewById(R.id.empty_image_msg_container);
        this.emptyImagesMsgContainerButton = (CustomTextView) this.emptyImagesMsgContainer.findViewById(R.id.permission_button);
        this.emptyImagesMsgContainerButton.setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean(BaseActivity.REQUIRE_REQUEST_MESSAGE_SNACKBAR, false);
                ComposeViewFragment.this.grantReadStoragePermission(ComposeViewFragment.this.getResources().getString(R.string.read_permission_recent_files_snackbar), bundle2);
            }
        });
        this.emailSuggestionContainer = (LinearLayout) inflate.findViewById(R.id.email_suggestion_container);
        this.emailSuggestionView = (HorizontalListView) inflate.findViewById(R.id.email_keyboard_suggestion_view);
        this.emailSuggestionView.setDividerWidth((int) Utilities.getPixels(getActivity(), 0.5f));
        this.emailSuggestionView.setAdapter((ListAdapter) new EmailListAdapter(getActivity(), R.id.from_address_snippet, new ArrayList()));
        this.emailSuggestionView.setOnItemClickListener(new EmailSuggestionClickListener());
        this.imageLst = new ArrayList();
        this.attachmentImagecontainer.setAdapter((ListAdapter) new ImageListAdapter(getActivity(), R.id.from_address_snippet, this.imageLst));
        executeFetchImagesAsyncTask();
        this.attachmentImagecontainer.setOnItemClickListener(new AttachmentListViewClickListener());
        this.composeAttachmentLayout = (LinearLayout) inflate.findViewById(R.id.compose_attachment_view);
        this.attachmentTypesListener = new AttachmentTypesClickListener();
        this.mRecentPicturesContainer = (LinearLayout) inflate.findViewById(R.id.recent_pictures_image_containter);
        this.mRecentPicturesContainer.setOnClickListener(this.attachmentTypesListener);
        this.recentPicsButton = (ImageButton) this.mRecentPicturesContainer.findViewById(R.id.recent_pictures_image_button);
        this.mCameraFab = (FloatingActionButton) inflate.findViewById(R.id.camera_fab);
        this.mCameraFab.setOnClickListener(this.attachmentTypesListener);
        this.mFileMgrImageContainer = (LinearLayout) inflate.findViewById(R.id.file_manager_img_container);
        this.mFileMgrImageContainer.setOnClickListener(this.attachmentTypesListener);
        this.sendButton = (LinearLayout) inflate.findViewById(R.id.send_mail);
        this.sendButton.setOnClickListener(new SendClickListener());
        ((CustomTextView) inflate.findViewById(R.id.send_mail_text)).setText(Utilities.getSpannableStringMedium(this.mContext, getString(R.string.compose_ab_send)));
        this.subjectView = (ActionEditText) inflate.findViewById(R.id.subject);
        this.sendLaterPicker = inflate.findViewById(R.id.reminder_picker);
        this.followUpButton = (ImageButton) inflate.findViewById(R.id.follow_up_btn);
        this.sendLaterPicker.setOnClickListener(this);
        updateMailSendButton();
        this.composeContainer = (LinearLayout) inflate.findViewById(R.id.composeContainer);
        this.composeMail = (ComposeMail) inflate.findViewById(R.id.composeMail);
        this.toHeader = (CustomTextView) inflate.findViewById(R.id.toHeader);
        this.ccHeader = (CustomTextView) inflate.findViewById(R.id.ccHeader);
        this.bccHeader = (CustomTextView) inflate.findViewById(R.id.bccHeader);
        this.composeHeader = (CustomTextView) inflate.findViewById(R.id.composeHeader);
        this.webViewContainer = (RelativeLayout) inflate.findViewById(R.id.webviewContainer);
        this.quotedTextButton = (ImageButton) inflate.findViewById(R.id.quotedTextButton);
        this.removeQuotedTextButton = (ImageButton) inflate.findViewById(R.id.removeQuotedTextButton);
        this.webView = (WebView) inflate.findViewById(R.id.simpleWebView);
        this.signatureWebview = (WebView) inflate.findViewById(R.id.signatureWebView);
        this.signatureWebview.setVerticalScrollBarEnabled(false);
        this.signatureWebview.setHorizontalScrollBarEnabled(false);
        this.signatureWebview.setScrollContainer(false);
        this.signatureWebview.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.5
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (!ComposeViewFragment.this.mShowAttachmentWhileScroll && z) {
                    ComposeViewFragment.this.hideExtraComponent();
                }
            }
        });
        if ((this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && !this.mIsSendAgain) || ((this.mIsEditDraft || this.mIsSendAgain) && this.mQuotedText == null)) {
            this.webViewContainer.setVisibility(8);
        }
        HeaderClickListener headerClickListener = new HeaderClickListener();
        this.toHeader.setOnClickListener(headerClickListener);
        this.bccHeader.setOnClickListener(headerClickListener);
        this.ccHeader.setOnClickListener(headerClickListener);
        this.composeHeader.setOnClickListener(headerClickListener);
        this.attachmentLayout.setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ComposeViewFragment.this.attachmentLayout.isShown()) {
                    ComposeViewFragment.this.setFocusToComposeBody();
                }
            }
        });
        this.extContactStrip = inflate.findViewById(R.id.ext_contact_strip);
        inflate.findViewById(R.id.close_ext_contact_panel).setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ComposeViewFragment.this.extContactStrip.setVisibility(8);
            }
        });
        this.rootContainer.registerOnInterceptTouchListener(new RootInterceptTouchListener());
        this.subjectView.setHint(Html.fromHtml("<b>" + ((Object) this.subjectView.getHint()) + "</b>"));
        this.gd = new GestureDetector(getActivity(), new GestureListener());
        setSoftInputStateVisiblilty(true);
        attachTextChangedListeners();
        setUpWebView();
        updateViews();
        inflateMoreRecipientsPopup();
        if ((this.mIsEditDraft || this.mIsSendAgain) && this.mOpenQuotedText) {
            this.webView.setVisibility(0);
            setQuotedTextInWebView();
            this.removeQuotedTextButton.setVisibility(0);
        }
        this.quotedTextButton.setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ComposeViewFragment.this.webView.getVisibility() == 8) {
                    ComposeViewFragment.this.webView.setVisibility(0);
                    ComposeViewFragment.this.setQuotedTextInWebView();
                    ComposeViewFragment.this.removeQuotedTextButton.setVisibility(0);
                } else {
                    ComposeViewFragment.this.webView.setVisibility(8);
                    ComposeViewFragment.this.removeQuotedTextButton.setVisibility(8);
                }
            }
        });
        this.removeQuotedTextButton.setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ComposeViewFragment.this.webViewContainer.setVisibility(8);
                ComposeViewFragment.this.mIsMessageDirty = true;
            }
        });
        ContactPickerListener contactPickerListener = new ContactPickerListener();
        AttachmentClickListener attachmentClickListener = new AttachmentClickListener();
        this.menuButtonContainerTo.setOnClickListener(contactPickerListener);
        this.bccAddressContactPickerContainer.setOnClickListener(contactPickerListener);
        this.ccAddressContactPickerContainer.setOnClickListener(contactPickerListener);
        this.attachmentPickerContainer.setOnClickListener(attachmentClickListener);
        attachGlobalLayoutListenerOnScrollView();
        this.toAddressTextView.registerKeyboardEventListener(this);
        this.bccAddressTextView.registerKeyboardEventListener(this);
        this.ccAddressTextView.registerKeyboardEventListener(this);
        initAnimations();
        if (!isContactPickerSupported(getActivity())) {
            this.bccAddressContactPickerContainer.setVisibility(8);
            this.ccAddressContactPickerContainer.setVisibility(8);
        }
        this.mConversationChangeObserver = new ConversationViewChangeObserver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENT_ACTION_BROADCAST_CONVERSATION_CREATED);
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.mConversationChangeObserver, intentFilter);
        this.mMessageResourceIDUpdateObserver = new MessageResourceIDUpdateObserver(this);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Constants.INTENT_ACTION_BROADCAST_MESSAGE_RESOURCE_ID_UPDATE);
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.mMessageResourceIDUpdateObserver, intentFilter2);
        this.compose_web_attachment_view = (LinearLayout) inflate.findViewById(R.id.compose_web_attachment_container);
        this.compose_web_attachment_view.setOnClickListener(new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ComposeViewFragment.this.hideExtraComponent();
                ComposeViewFragment.this.setFocusToComposeBody();
            }
        });
        this.toAddressTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.11
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                ComposeViewFragment.this.subjectView.requestFocus();
                return true;
            }
        });
        this.ccAddressTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.12
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                ComposeViewFragment.this.subjectView.requestFocus();
                return true;
            }
        });
        this.bccAddressTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.13
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                ComposeViewFragment.this.subjectView.requestFocus();
                return true;
            }
        });
        if (UserPreferences.getInstance(this.mContext).hasUserSeenRRGDPRMessage() <= 0) {
            this.rrEnabledSnackbar.setVisibility(0);
        }
        updateMailSendButton();
        return inflate;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.reminder_picker) {
            return;
        }
        if (this.composeExtrasContainer.getVisibility() == 0) {
            this.composeExtrasContainer.animate().alpha(0.0f).setDuration(300L);
            this.rootContainer.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.14
                @Override // java.lang.Runnable
                public void run() {
                    if (ComposeViewFragment.this.getActivity() == null) {
                        return;
                    }
                    Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.getFocusedView());
                    ComposeViewFragment.this.hideSendLaterFragment();
                }
            }, 300L);
            return;
        }
        Utilities.hideSoftKeyboard(getActivity());
        if (this.composeAttachmentLayout.getVisibility() == 0) {
            hideComposeAttachmentLayout();
            showSendLaterFragment();
        } else {
            this.rootContainer.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.15
                @Override // java.lang.Runnable
                public void run() {
                    ComposeViewFragment.this.showSendLaterFragment();
                }
            }, 300L);
        }
    }

    public void hideSendLaterFragment() {
        this.composeExtrasContainer.setVisibility(8);
        updateFollowUpButton();
    }

    public void showSendLaterFragment() {
        this.followUpButton.setSelected(true);
        this.recentPicsButton.setSelected(false);
        hideEmailSuggestionContainer();
        this.composeExtrasContainer.setVisibility(0);
        if (this.keyBoardHeight > 180) {
            this.composeExtrasContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, this.keyBoardHeight));
        }
        ComposeSendExtrasFragment composeSendExtrasFragment = new ComposeSendExtrasFragment();
        composeSendExtrasFragment.setOnOptionSelectionListener(new SendLaterItemClickListener((AppCompatActivity) getActivity()));
        Bundle bundle = new Bundle();
        if (this.mAccountType == 1010 || this.mAccountType == 1011 || this.mAccountType == 1014) {
            bundle.putBoolean(ComposeSendExtrasFragment.HIDE_SNOOZE, true);
        }
        if (this.mSnoozeTime != 0) {
            bundle.putLong(ComposeSendExtrasFragment.REMIND_LATER_TIME, this.mSnoozeTime);
        }
        if (this.mSendLaterTime != 0) {
            bundle.putLong("send-later-time", this.mSendLaterTime);
        }
        bundle.putInt(ComposeSendExtrasFragment.NOTIFY_WHEN_READ, this.shouldNotifyWhenRead);
        composeSendExtrasFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.compose_extras_container, composeSendExtrasFragment, ComposeSendExtrasFragment.TAG);
        beginTransaction.commitAllowingStateLoss();
        this.composeExtrasContainer.setAlpha(0.0f);
        this.composeExtrasContainer.animate().alpha(1.0f).setDuration(300L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OnBccEntryRemovedListener implements RecipientEditTextView.OnChipDeletedEventListener {
        private OnBccEntryRemovedListener() {
        }

        @Override // com.cloudmagic.android.chips.RecipientEditTextView.OnChipDeletedEventListener
        public void onChipDeletion(RecipientChip recipientChip) {
            if (ComposeViewFragment.this.customAddedBcc == null || ComposeViewFragment.this.customAddedBcc.size() <= 0) {
                return;
            }
            ComposeViewFragment.this.customAddedBcc.remove(recipientChip.getEntry().getAddress());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SendLaterItemClickListener implements ComposeSendExtrasFragment.OnOptionSelectionListener {
        private AppCompatActivity mActivity;

        public SendLaterItemClickListener(AppCompatActivity appCompatActivity) {
            this.mActivity = appCompatActivity;
        }

        @Override // com.cloudmagic.android.fragments.ComposeSendExtrasFragment.OnOptionSelectionListener
        public boolean onOptionSelected(int i, int i2) {
            Newton newton = (Newton) ProductFactory.getProduct(0, ComposeViewFragment.this.getActivity());
            if (1 == i) {
                if (newton.canAccessFeature(3)) {
                    ComposeViewFragment.this.shouldNotifyWhenRead = i2;
                    ComposeViewFragment.this.updateMailSendButton();
                } else {
                    NewtonFeatureExpiredDialog.newInstance(9).show(ComposeViewFragment.this.getFragmentManager(), NewtonFeatureExpiredDialog.TAG);
                    return false;
                }
            }
            return true;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // com.cloudmagic.android.fragments.ComposeSendExtrasFragment.OnOptionSelectionListener
        public boolean onOptionSelected(int i) {
            Newton newton = (Newton) ProductFactory.getProduct(0, ComposeViewFragment.this.getActivity());
            switch (i) {
                case 2:
                    if (!newton.canAccessFeature(1)) {
                        NewtonFeatureExpiredDialog.newInstance(9).show(ComposeViewFragment.this.getFragmentManager(), NewtonFeatureExpiredDialog.TAG);
                        return false;
                    }
                    SnoozeDialogFragment snoozeDialogFragment = new SnoozeDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(SnoozeDialogFragment.TRIGGER_LOC, 3);
                    if (ComposeViewFragment.this.mSnoozeTime != 0) {
                        bundle.putLong(SnoozeDialogFragment.REMIND_LATER_TIME, ComposeViewFragment.this.mSnoozeTime);
                    }
                    snoozeDialogFragment.setArguments(bundle);
                    setSnoozeDialogItemClickListener(snoozeDialogFragment);
                    snoozeDialogFragment.show(this.mActivity.getSupportFragmentManager(), SnoozeDialogFragment.TAG);
                    return true;
                case 3:
                    if (!newton.canAccessFeature(5)) {
                        NewtonFeatureExpiredDialog.newInstance(9).show(ComposeViewFragment.this.getFragmentManager(), NewtonFeatureExpiredDialog.TAG);
                        return false;
                    }
                    if (ComposeViewFragment.this.mSendLaterTime != 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(ComposeViewFragment.this.mSendLaterTime);
                        ConfirmationDialog newInstance = ConfirmationDialog.newInstance(ComposeViewFragment.this.getString(R.string.remind_send_later), String.format(ComposeViewFragment.this.getString(CMCalendarHelper.daysBetween(Calendar.getInstance(), calendar) < 2 ? R.string.send_later_message_today_tomorrow : R.string.send_later_message), DateTimeUtils.getFormattedString(ComposeViewFragment.this.getContext(), ComposeViewFragment.this.mSendLaterTime)), ComposeViewFragment.this.getString(R.string.send_later_negative), ComposeViewFragment.this.getString(R.string.send_later_positive));
                        newInstance.registerCallback(new ConfirmationDialog.ActionListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.SendLaterItemClickListener.1
                            @Override // com.cloudmagic.android.dialogs.ConfirmationDialog.ActionListener
                            public void onPositiveConfirmation() {
                                SendLaterItemClickListener.this.openTimeChooseActivity();
                            }

                            @Override // com.cloudmagic.android.dialogs.ConfirmationDialog.ActionListener
                            public void onNegativeConfirmation() {
                                ComposeViewFragment.this.mSendLaterTime = 0L;
                                ComposeViewFragment.this.mIsAutoCancellable = false;
                                ComposeViewFragment.this.updateSendLaterText();
                                ComposeViewFragment.this.updateMailSendButton();
                            }
                        });
                        newInstance.setCanceledOnTouchOutside(true);
                        newInstance.show(ComposeViewFragment.this.getFragmentManager(), ConfirmationDialog.TAG);
                    } else {
                        openTimeChooseActivity();
                    }
                    return true;
                default:
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void openTimeChooseActivity() {
            int i;
            int i2;
            ComposeViewFragment.this.isSnoozeTimePickerActivityActive = true;
            Intent intent = new Intent(ComposeViewFragment.this.getContext(), (Class<?>) SnoozeTimeChooserActivity.class);
            AccountColor accountColor = Constants.accountIdColorMap.get(Integer.valueOf(ComposeViewFragment.this.mAccountID));
            if (accountColor == null) {
                i2 = R.color.primary_color;
                i = R.color.primary_color_dark;
            } else {
                int i3 = accountColor.colorLight;
                i = accountColor.colorDark;
                i2 = i3;
            }
            intent.putExtra(SnoozeTimeChooserBaseFragment.TOOLBAR_BACKGROUND, i2);
            intent.putExtra(SnoozeTimeChooserBaseFragment.STATUS_BAR_COLOR, i);
            intent.putExtra(SnoozeTimeChooserBaseFragment.TYPE, SnoozeTimeChooserBaseFragment.TYPE_SEND_LATER);
            intent.putExtra(SnoozeTimeChooserBaseFragment.PREFILL_TEXT, ComposeViewFragment.this.mSendLaterTime);
            intent.putExtra(SnoozeTimeChooserBaseFragment.IS_AUTO_CANCELABLE, ComposeViewFragment.this.mIsAutoCancellable);
            ComposeViewFragment.this.getActivity().startActivityForResult(intent, SnoozeTimeChooserBaseFragment.TYPE_SEND_LATER);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class ComposeSnoozeItemClickListener extends SnoozeItemClickListener {
            public ComposeSnoozeItemClickListener(BaseFragment baseFragment, FragmentActivity fragmentActivity, SnoozeItemClickListener.SnoozeCallback snoozeCallback) {
                super(baseFragment, fragmentActivity, snoozeCallback);
            }

            @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener, com.cloudmagic.android.dialogs.SnoozeDialogFragment.OnSnoozeItemClickListener
            public void onClick(int i) {
                if (i == 4) {
                    ComposeViewFragment.this.isSnoozeTimePickerActivityActive = true;
                }
                super.onClick(i);
            }
        }

        private void setSnoozeDialogItemClickListener(@NonNull SnoozeDialogFragment snoozeDialogFragment) {
            snoozeDialogFragment.setOnSnoozeItemClickListener(new ComposeSnoozeItemClickListener(ComposeViewFragment.this, ComposeViewFragment.this.getActivity(), new SnoozeItemClickListener.SnoozeCallback() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.SendLaterItemClickListener.2
                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public boolean createDateWithIdInTag() {
                    return false;
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public ViewConversation getConversation() {
                    return null;
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public ArrayList<ViewConversation> getConversations() {
                    return null;
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public void onDateTimePickerDismiss(boolean z) {
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public void onEmailSnoozeTime() {
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public AccountColor getAccountColor() {
                    return Constants.accountIdColorMap.get(Integer.valueOf(ComposeViewFragment.this.mAccountID));
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public void onSnoozeTimePast(Calendar calendar) {
                    SimpleDialogFragment.newInstance(ComposeViewFragment.this.getString(R.string.snooze_time_past_title), ComposeViewFragment.this.getString(R.string.snooze_time_past_content)).show(ComposeViewFragment.this.getFragmentManager(), SimpleDialogFragment.TAG);
                }

                @Override // com.cloudmagic.android.dialogs.listener.SnoozeItemClickListener.SnoozeCallback
                public void onSnooze(Calendar calendar) {
                    ComposeViewFragment.this.setRemindLaterTime(calendar);
                }
            }));
            snoozeDialogFragment.setOnCancelReminderListener(new SnoozeDialogFragment.OnCancelReminderListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.SendLaterItemClickListener.3
                @Override // com.cloudmagic.android.dialogs.SnoozeDialogFragment.OnCancelReminderListener
                public void onCancel() {
                    ComposeViewFragment.this.mSnoozeTime = 0L;
                    ComposeViewFragment.this.updateRemindLaterText();
                    ComposeViewFragment.this.updateMailSendButton();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMailSendButton() {
        updateFollowUpButton();
        if (this.mSendLaterTime > 0) {
            ((CustomTextView) this.sendButton.findViewById(R.id.send_mail_text)).setText(Utilities.getSpannableStringMedium(this.mContext, getString(R.string.remind_send_later)));
        } else {
            ((CustomTextView) this.sendButton.findViewById(R.id.send_mail_text)).setText(Utilities.getSpannableStringMedium(this.mContext, getString(R.string.compose_ab_send)));
        }
    }

    private void setSendLaterTime(long j, boolean z) {
        this.mSendLaterTime = j;
        this.mIsAutoCancellable = z;
        updateSendLaterText();
        updateMailSendButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRemindLaterTime(Calendar calendar) {
        long timeInMillis = calendar.getTimeInMillis();
        if (this.mSendLaterTime > 0 && timeInMillis <= this.mSendLaterTime) {
            SimpleDialogFragment.newInstance(getString(R.string.heads_up), getString(R.string.send_time_more_than_snooze)).show(getFragmentManager(), SimpleDialogFragment.TAG);
        } else {
            this.mSnoozeTime = calendar.getTimeInMillis();
            updateRemindLaterText();
        }
        updateMailSendButton();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.toRecipientAdapter != null) {
            this.toRecipientAdapter.releaseResources();
        }
        if (this.ccRecipientAdapter != null) {
            this.ccRecipientAdapter.releaseResources();
        }
        if (this.bccRecipientAdapter != null) {
            this.bccRecipientAdapter.releaseResources();
        }
        LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.mConversationChangeObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getStatusBarHeight() {
        if (getActivity() == null) {
            return 0;
        }
        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeFetchSuggestionLstTask(int i) {
        new GetContactSuggestionsAsyncTask().execute(Integer.valueOf(i));
    }

    private void initAnimations() {
        this.slideInFromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_bottom);
        this.slideInFromBottom.setAnimationListener(new Animation.AnimationListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.16
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ComposeViewFragment.this.scrollToBottom();
            }
        });
        this.slideOutFromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.out_to_bottom);
        this.slideOutFromBottom.setAnimationListener(new Animation.AnimationListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.17
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ComposeViewFragment.this.mShowAttachmentWhileScroll = false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSoftInputStateVisiblilty(Boolean bool) {
        if (bool.booleanValue()) {
            getActivity().getWindow().setSoftInputMode(5);
        } else {
            getActivity().getWindow().setSoftInputMode(2);
        }
    }

    private void attachTextChangedListeners() {
        this.recipientChipMap = new HashMap();
        RecipientChipFocusChangeListener recipientChipFocusChangeListener = new RecipientChipFocusChangeListener();
        this.toAddressTextView.setOnFocusChangeListener(recipientChipFocusChangeListener);
        this.ccAddressTextView.setOnFocusChangeListener(recipientChipFocusChangeListener);
        this.bccAddressTextView.setOnFocusChangeListener(recipientChipFocusChangeListener);
        this.toAddressTextView.setOnChipDeletedEventListener(new RecipientDeleteListener());
        this.ccAddressTextView.setOnChipDeletedEventListener(new RecipientDeleteListener());
        this.bccAddressTextView.setOnChipDeletedEventListener(new RecipientDeleteListener());
        this.toAddressTextView.setOnRecipientEntryDropDownClick(new RecipientAddedThroughDropDownListener());
        this.ccAddressTextView.setOnRecipientEntryDropDownClick(new RecipientAddedThroughDropDownListener());
        this.bccAddressTextView.setOnRecipientEntryDropDownClick(new RecipientAddedThroughDropDownListener());
        this.toAddressTextView.setOnFirstEntryAddedListener(new RecipientTextVFirstEntryAddedListener());
        this.ccAddressTextView.setOnFirstEntryAddedListener(new RecipientTextVFirstEntryAddedListener());
        this.bccAddressTextView.setOnEntryAddedListener(new BccViewEntryAddedListener());
        this.bccAddressTextView.setOnChipDeletedEventListener(new OnBccEntryRemovedListener());
        this.subjectView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.18
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (ComposeViewFragment.this.mShowAttachmentWhileScroll) {
                    return;
                }
                if (z) {
                    ComposeViewFragment.this.hideExtraComponent();
                }
                if (z && ComposeViewFragment.this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && ComposeViewFragment.this.subjectView.getText().length() == 0) {
                    if (ComposeViewFragment.this.bccAddressContainer.getVisibility() != 0) {
                        if (ComposeViewFragment.this.ccAddressContainer.getVisibility() == 0) {
                            ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.ccAddressContainer.getTop());
                            return;
                        } else {
                            ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.toAddressContainer.getTop());
                            return;
                        }
                    }
                    ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.bccAddressContainer.getTop());
                }
            }
        });
        this.composeMail.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.19
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    ComposeViewFragment.this.hideExtraComponent();
                }
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.20
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ComposeViewFragment.this.hideExtraComponent();
            }
        };
        this.composeMail.setOnClickListener(onClickListener);
        this.toAddressTextView.setOnClickListener(onClickListener);
        this.ccAddressTextView.setOnClickListener(onClickListener);
        this.bccAddressTextView.setOnClickListener(onClickListener);
        this.subjectView.setOnClickListener(onClickListener);
    }

    @Override // android.support.v4.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mToRecipientAddressMenuPopup != null) {
            this.mToRecipientAddressMenuPopup.dismiss();
        }
        if (this.mCcRecipientAddressMenuPopup != null) {
            this.mCcRecipientAddressMenuPopup.dismiss();
        }
        if (this.mBccRecipientAddressMenuPopup != null) {
            this.mBccRecipientAddressMenuPopup.dismiss();
        }
        this.isLandscape = getResources().getConfiguration().orientation == 2;
        this.attachmentLayout.post(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.21
            @Override // java.lang.Runnable
            public void run() {
                ArrayList attachmentViews = ComposeViewFragment.this.getAttachmentViews(null);
                ComposeViewFragment.this.attachmentTable.removeAllViews();
                ComposeViewFragment.this.updateAllAttachments(attachmentViews);
            }
        });
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (((BaseActivity) getActivity()).checkBreakingChanges(this) || this.mUrisToHandleAfterPermission == null || this.mUrisToHandleAfterPermission.size() <= 0) {
            return;
        }
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("handle_attachments_from_bundle", true);
        bundle2.putStringArrayList("attachments_from_bundle", this.mUrisToHandleAfterPermission);
        grantReadStoragePermission(getResources().getString(R.string.read_permission_common), bundle2);
    }

    @Override // com.cloudmagic.android.observers.MessageResourceIDUpdateObserver.MessageResourceIDUpdateObserverInterface
    public void updateMessageResourceId(String str, String str2) {
        if (str != null && str.equals(this.mPreviousDraftId)) {
            this.mPreviousDraftId = str2;
        }
        if (this.mReferenceResourceId == null || !this.mReferenceResourceId.equals(str)) {
            return;
        }
        this.mReferenceResourceId = str2;
    }

    @Override // com.cloudmagic.android.BaseActivity.PermissionCallback
    public void permissionGrantedContinueTask(int i, Bundle bundle) {
        if (i == 1) {
            if (bundle.getBoolean("handle_attachments_from_bundle")) {
                final ArrayList<String> stringArrayList = bundle.getStringArrayList("attachments_from_bundle");
                if (stringArrayList != null) {
                    this.rootContainer.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.22
                        @Override // java.lang.Runnable
                        public void run() {
                            Iterator it = stringArrayList.iterator();
                            while (it.hasNext()) {
                                AttachmentFileInfo fileInfo = ComposeViewFragment.this.getFileInfo(Uri.parse((String) it.next()));
                                if (fileInfo != null) {
                                    ComposeViewFragment.this.addAttachmentFromBundle(fileInfo);
                                }
                            }
                        }
                    }, 100L);
                    return;
                }
                return;
            }
            if (bundle.getString("image_uri") != null) {
                doOnAttachmentSelected(Uri.parse(bundle.getString("image_uri")));
                return;
            }
            return;
        }
        if (i == 3 && this.attachmentTypesListener != null) {
            this.attachmentTypesListener.onCameraImageContanierClick();
        }
    }

    /* loaded from: classes.dex */
    private class HeaderClickListener implements View.OnClickListener {
        private HeaderClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ComposeViewFragment.this.hideExtraComponent();
            if (view.getId() == R.id.toHeader) {
                ComposeViewFragment.this.toAddressTextView.requestFocus();
                ComposeViewFragment.this.setSoftInputStateVisiblilty(true);
                ComposeViewFragment.this.mShouldWebViewGetFocus = false;
                Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.toAddressTextView);
                return;
            }
            if (view.getId() == R.id.ccHeader) {
                ComposeViewFragment.this.ccAddressTextView.requestFocus();
                ComposeViewFragment.this.setSoftInputStateVisiblilty(true);
                ComposeViewFragment.this.mShouldWebViewGetFocus = false;
                Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.ccAddressTextView);
                return;
            }
            if (view.getId() == R.id.bccHeader) {
                ComposeViewFragment.this.bccAddressTextView.requestFocus();
                ComposeViewFragment.this.setSoftInputStateVisiblilty(true);
                ComposeViewFragment.this.mShouldWebViewGetFocus = false;
                Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.bccAddressTextView);
                return;
            }
            if (view.getId() == R.id.subjectHeader) {
                ComposeViewFragment.this.subjectView.requestFocus();
                ComposeViewFragment.this.setSoftInputStateVisiblilty(true);
                ComposeViewFragment.this.mShouldWebViewGetFocus = false;
                Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.subjectView);
                return;
            }
            if (view.getId() == R.id.composeHeader) {
                ComposeViewFragment.this.composeMail.requestFocus();
                ComposeViewFragment.this.setSoftInputStateVisiblilty(true);
                Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.composeMail);
            }
        }
    }

    public void changeSidePadding(int i) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.scrollView.getLayoutParams();
        if (i == 2) {
            int dimension = (int) this.mContext.getResources().getDimension(R.dimen.compose_screen_side_padding);
            layoutParams.setMargins(dimension, 0, dimension, 0);
        } else if (i == 1) {
            layoutParams.setMargins(0, 0, 0, 0);
        }
    }

    private void attachGlobalLayoutListenerOnScrollView() {
        this.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.23
            private int oldHeightDiff;

            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (ComposeViewFragment.this.getActivity() == null) {
                    return;
                }
                final int height = ComposeViewFragment.this.scrollView.getRootView().getHeight() - ComposeViewFragment.this.scrollView.getHeight();
                Rect rect = new Rect();
                Window window = ComposeViewFragment.this.getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                if (height > window.findViewById(android.R.id.content).getTop()) {
                    ComposeViewFragment.this.scrollView.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.23.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (!ComposeViewFragment.this.isQuotedTextRemoved() && AnonymousClass23.this.oldHeightDiff != height && ComposeViewFragment.this.mTouchPositionOnRootElement != -1 && ComposeViewFragment.this.mIsWebViewTouchDetected.booleanValue()) {
                                if (ComposeViewFragment.this.mIsWebViewFocused.booleanValue()) {
                                    ComposeViewFragment.this.mIsWebViewFocused = false;
                                    AnonymousClass23.this.oldHeightDiff = height;
                                    return;
                                }
                                ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.mTouchPositionOnRootElement - 30);
                                ComposeViewFragment.this.mIsWebViewTouchDetected = false;
                            }
                            AnonymousClass23.this.oldHeightDiff = height;
                        }
                    }, 300L);
                    return;
                }
                if (this.oldHeightDiff != height) {
                    ComposeViewFragment.this.mIsWebViewTouchDetected = false;
                }
                this.oldHeightDiff = height;
            }
        });
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(IS_STARRED, this.mIsStarred);
        bundle.putInt(CHECKBOX_DRAWABLE, this.mCheckBoxDrawable);
        bundle.putParcelableArrayList(ATTACHMENT_LIST, this.mAttachment);
        bundle.putInt(CC_VISIBILITY, this.ccAddressContainer.getVisibility());
        bundle.putInt(BCC_VISIBILITY, this.bccAddressContainer.getVisibility());
        bundle.putBoolean(WEBVIEW_FOCUS_STATE, this.mShouldWebViewGetFocus.booleanValue());
        bundle.putBoolean(IS_MESSAGE_DIRTY, this.mIsMessageDirty.booleanValue());
        bundle.putBoolean(IS_SIGNATURE_DIRTY, this.mIsSignatureDirty.booleanValue());
        bundle.putStringArray("to", createAddressArrayFromChip(this.recipientChipMap.get("TO-CHIPS")));
        bundle.putStringArray("cc", createAddressArrayFromChip(this.recipientChipMap.get("CC-CHIPS")));
        bundle.putStringArray("bcc", createAddressArrayFromChip(this.recipientChipMap.get("BCC-CHIPS")));
        bundle.putParcelable(INITIAL_STATE, this.initialAddressState);
        bundle.putStringArrayList(CUSTOM_ADDED_BCC, this.customAddedBcc);
        bundle.putBoolean(IS_EDIT_DRAFT, this.mIsEditDraft);
        bundle.putBoolean(IS_EDIT_OUTBOX, this.mIsEditOutbox);
        bundle.putBoolean(THREAD_CONTAINS_SENT, this.mThreadContainsSent);
        if (Build.VERSION.SDK_INT >= 24) {
            storeQuotedTextInFile();
        } else if (!this.mIsQuotedTextHTML) {
            bundle.putString(EXTRA_QUOTED_PLAIN_TEXT, this.mQuotedText);
        } else {
            bundle.putString(EXTRA_QUOTED_HTML_TEXT, this.mQuotedText);
        }
        bundle.putLong(REMIND_IF_NOT_REPLIED_TIME, this.mSnoozeTime);
        bundle.putLong("send-later-time", this.mSendLaterTime);
        bundle.putBoolean(SEND_LATER_AUTO_CANCELABLE, this.mIsAutoCancellable);
        bundle.putInt(SHOULD_NOTIFY_WHEN_READ, this.shouldNotifyWhenRead);
        bundle.putStringArrayList("attachments_from_bundle", this.attachmentsFromBundle);
        bundle.putBoolean("is_search_active", this.mIsSearchModeActive);
    }

    /* loaded from: classes.dex */
    private class RootInterceptTouchListener implements CustomLinearLayout.InterceptTouchListener {
        private RootInterceptTouchListener() {
        }

        @Override // com.cloudmagic.android.view.CustomLinearLayout.InterceptTouchListener
        public boolean onInterceptTouch(MotionEvent motionEvent) {
            boolean hideReminderOnTouchPositions = ComposeViewFragment.this.hideReminderOnTouchPositions(motionEvent);
            ComposeViewFragment.this.mTouchPositionOnRootElement = (int) motionEvent.getY();
            ComposeViewFragment.this.composeMail.dismissEditorActionMode();
            return hideReminderOnTouchPositions;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hideReminderOnTouchPositions(MotionEvent motionEvent) {
        motionEvent.getX();
        motionEvent.getY();
        return false;
    }

    public void handleReminderDismiss() {
        this.mTSReminder = -1L;
        this.mIsStarred = true;
    }

    private void inflateMoreRecipientsPopup() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PopupDialog.SimplePopupDialogItem("cc_address", getResources().getString(R.string.recipient_cc_hint)));
        arrayList.add(new PopupDialog.SimplePopupDialogItem("bcc_address", getResources().getString(R.string.recipient_bcc_hint)));
        if (isContactPickerSupported(this.mContext)) {
            arrayList.add(new PopupDialog.SimplePopupDialogItem("contact_picker", getResources().getString(R.string.pick_from_contacts)));
        }
        this.mToRecipientAddressMenuPopup = new PopupDialog(getActivity(), arrayList);
        this.mToRecipientAddressMenuPopup.setSingleChoiceMode(false);
        this.mToRecipientAddressMenuPopup.setOnPopupItemClickListener(new RecipientMenuItemClickListener(this.toAddressTextView));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new PopupDialog.SimplePopupDialogItem("contact_picker", getResources().getString(R.string.pick_from_contacts)));
        this.mCcRecipientAddressMenuPopup = new PopupDialog(getActivity(), arrayList2);
        this.mCcRecipientAddressMenuPopup.setSingleChoiceMode(false);
        this.mCcRecipientAddressMenuPopup.setOnPopupItemClickListener(new RecipientMenuItemClickListener(this.ccAddressTextView));
        this.mBccRecipientAddressMenuPopup = new PopupDialog(getActivity(), arrayList2);
        this.mBccRecipientAddressMenuPopup.setSingleChoiceMode(false);
        this.mBccRecipientAddressMenuPopup.setOnPopupItemClickListener(new RecipientMenuItemClickListener(this.bccAddressTextView));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecipientMenuItemClickListener implements PopupDialog.OnPopupItemClickListener {
        private View view;

        public RecipientMenuItemClickListener(View view) {
            this.view = view;
        }

        @Override // com.cloudmagic.android.dialogs.PopupDialog.OnPopupItemClickListener
        public void onItemSelected(PopupDialog.PopupDialogItemInterface popupDialogItemInterface) {
            int i;
            if (popupDialogItemInterface.getId().equals("cc_address")) {
                if (ComposeViewFragment.this.ccAddressContainer.getVisibility() == 8) {
                    ComposeViewFragment.this.ccAddressTextView.requestFocus();
                    ComposeViewFragment.this.ccAddressContainer.setVisibility(0);
                } else {
                    ComposeViewFragment.this.ccAddressContainer.setVisibility(8);
                }
            } else if (popupDialogItemInterface.getId().equals("bcc_address")) {
                if (ComposeViewFragment.this.bccAddressContainer.getVisibility() == 8) {
                    ComposeViewFragment.this.bccAddressTextView.requestFocus();
                    ComposeViewFragment.this.bccAddressContainer.setVisibility(0);
                } else {
                    ComposeViewFragment.this.bccAddressContainer.setVisibility(8);
                }
            } else if (popupDialogItemInterface.getId().equals("contact_picker")) {
                Intent intent = new Intent("android.intent.action.PICK", ContactsContract.CommonDataKinds.Email.CONTENT_URI);
                switch (this.view.getId()) {
                    case R.id.multiAutoCompleteTextView /* 2131297001 */:
                        i = 1;
                        break;
                    case R.id.multiAutoCompleteTextViewBcc /* 2131297002 */:
                        i = 2;
                        break;
                    case R.id.multiAutoCompleteTextViewCc /* 2131297003 */:
                        i = 3;
                        break;
                    default:
                        i = -1;
                        break;
                }
                if (i != -1) {
                    ComposeViewFragment.this.startActivityForResult(intent, i);
                    ComposeViewFragment.this.mNewActivityStarted = true;
                }
            }
            ComposeViewFragment.this.setSoftInputStateVisiblilty(true);
        }
    }

    private void setUpWebView() {
        WebSettings settings = this.webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        this.webView.setOnTouchListener(this);
        this.webView.setWebChromeClient(new WebChromeClient() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.24
            @Override // android.webkit.WebChromeClient
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String str = consoleMessage.message() + " -- From line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId();
                CMLogger cMLogger = new CMLogger(ComposeViewFragment.this.mContext);
                cMLogger.putMessage("Compose webview console log:" + str);
                cMLogger.commit();
                return true;
            }
        });
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new CMJSInterface(getActivity()), "external");
        this.webView.setWebViewClient(new WebViewClient() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.25
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (MailTo.isMailTo(str)) {
                    try {
                        Utilities.startComposeFromUrl(ComposeViewFragment.this.getActivity().getApplicationContext(), str);
                        return true;
                    } catch (Exception unused) {
                        return true;
                    }
                }
                if (str.startsWith("tel:")) {
                    ComposeViewFragment.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(str)));
                    return true;
                }
                Utilities.openURLInBrowser(ComposeViewFragment.this.getActivity(), str);
                return true;
            }

            @Override // android.webkit.WebViewClient
            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                super.onPageStarted(webView, str, bitmap);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                if (Build.VERSION.SDK_INT >= 19) {
                    ComposeViewFragment.this.webView.evaluateJavascript("javascript:CMonLoadFinished();", null);
                } else {
                    ComposeViewFragment.this.webView.loadUrl("javascript:CMonLoadFinished();");
                }
                ComposeViewFragment.this.webView.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.25.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ComposeViewFragment.this.mOldWebViewHeightForWebViewKeyUp = ComposeViewFragment.this.webView.getContentHeight();
                    }
                }, 300L);
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        onTouchEvent(motionEvent);
        this.mShouldWebViewGetFocus = true;
        if (motionEvent.getAction() != 1) {
            return false;
        }
        if (this.doubletap) {
            return true;
        }
        this.mIsWebViewTouchDetected = true;
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.gd.onTouchEvent(motionEvent);
    }

    /* loaded from: classes.dex */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            ComposeViewFragment.this.doubletap = false;
            ComposeViewFragment.this.longPress = false;
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            ComposeViewFragment.this.doubletap = false;
            ComposeViewFragment.this.longPress = false;
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent motionEvent) {
            ComposeViewFragment.this.doubletap = true;
            ComposeViewFragment.this.longPress = false;
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            ComposeViewFragment.this.doubletap = true;
            ComposeViewFragment.this.longPress = false;
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            ComposeViewFragment.this.longPress = true;
        }
    }

    private void customizeActionBar() {
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.compose_spinner, (ViewGroup) null);
        this.fromFieldSpinner = (ComposeSpinner) inflate.findViewById(R.id.fromFieldSpinner);
        this.fromFieldSpinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.26
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                int[] iArr = new int[2];
                ComposeViewFragment.this.fromFieldSpinner.getLocationOnScreen(iArr);
                ComposeViewFromAddressAdapter composeViewFromAddressAdapter = (ComposeViewFromAddressAdapter) ComposeViewFragment.this.fromFieldSpinner.getAdapter();
                if (composeViewFromAddressAdapter != null) {
                    composeViewFromAddressAdapter.setFromSpinnerLoc(iArr);
                }
                ComposeViewFragment.this.setVerticalOffsetForSpinner(ComposeViewFragment.this.fromFieldSpinner);
                ComposeViewFragment.this.setHorizontalOffsetForSpinner(ComposeViewFragment.this.fromFieldSpinner);
            }
        });
        inflate.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        this.fromFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.27
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                ComposeViewFragment.this.bccAddressTextView.removeDuplicates(ComposeViewFragment.this.bccAddressTextView.getChips());
                ComposeViewFragment.this.bccAddressTextView.removeChipByEmail(ComposeViewFragment.this.mAutoBCC, ComposeViewFragment.this.bccAddressTextView.getChips(), ComposeViewFragment.this.customAddedBcc);
                switch (AnonymousClass46.$SwitchMap$com$cloudmagic$android$fragments$ComposeViewFragment$FromFieldSelection[ComposeViewFragment.this.fromFieldSelection.ordinal()]) {
                    case 1:
                        ComposeViewFragment.this.fromFieldSelection = FromFieldSelection.INIT;
                        break;
                    case 2:
                        ComposeViewFragment.this.fromFieldSelection = FromFieldSelection.CHANGED;
                        break;
                    case 3:
                        ComposeViewFragment.this.fromFieldSelection = FromFieldSelection.INIT;
                        break;
                }
                if (ComposeViewFragment.this.fromFieldSpinner.getSelectedItem() != null) {
                    ComposeViewFragment.this.mAccountID = ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).accountId;
                    ComposeViewFragment.this.executeFetchSuggestionLstTask(ComposeViewFragment.this.mAccountID);
                    ComposeViewFragment.this.mAccountType = ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).accountType;
                    ComposeViewFragment.this.mSignature = ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).getSignature(ComposeViewFragment.this.getActivity().getApplicationContext());
                    ComposeViewFragment.this.mSignatureAttachmentData = ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).signatureAttachmentData;
                    ComposeViewFragment.this.mIsSentUsingCMSet = ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).isSentUsingCMSet;
                    if (ComposeViewFragment.this.mInitialAccountIdSet != ComposeViewFragment.this.mAccountID) {
                        ComposeViewFragment.this.updateActionBarColor(ComposeViewFragment.this.mAccountID);
                    }
                    ComposeViewFragment.this.mInitialAccountIdSet = ComposeViewFragment.this.mAccountID;
                    if (ComposeViewFragment.this.draftsTextWatcher == null) {
                        ComposeViewFragment.this.addDraftsTextWatcher();
                    }
                    ComposeViewFragment.this.bccAddressTextView.setOnEntryAddedListener(null);
                    if (ComposeViewFragment.this.shouldLoadFromPrefs) {
                        ComposeViewFragment.this.mAutoBCC = ComposeViewFragment.this.getBCCFromPreferences();
                        if (ComposeViewFragment.this.mAutoBCC != null && ComposeViewFragment.this.mAutoBCC.length != 0) {
                            for (int i2 = 0; i2 < ComposeViewFragment.this.mAutoBCC.length; i2++) {
                                ComposeViewFragment.this.bccAddressTextView.setChipFromText(ComposeViewFragment.this.mAutoBCC[i2]);
                            }
                        }
                    }
                    if (!(ComposeViewFragment.this.toAddressTextView.hasFocus() || ComposeViewFragment.this.ccAddressTextView.hasFocus() || ComposeViewFragment.this.bccAddressTextView.hasFocus())) {
                        ComposeViewFragment.this.toAddressTextView.removeAllChips();
                        ComposeViewFragment.this.recipientChipMap.put("CC-CHIPS", ComposeViewFragment.this.ccAddressTextView.getChips());
                        ComposeViewFragment.this.recipientChipMap.put("BCC-CHIPS", ComposeViewFragment.this.bccAddressTextView.getChips());
                        ComposeViewFragment.this.toAddressTextView.addChips((List) ComposeViewFragment.this.recipientChipMap.get("TO-CHIPS"));
                        ComposeViewFragment.this.toAddressTextView.addChips((List) ComposeViewFragment.this.recipientChipMap.get("CC-CHIPS"));
                        ComposeViewFragment.this.toAddressTextView.addChips((List) ComposeViewFragment.this.recipientChipMap.get("BCC-CHIPS"));
                        ComposeViewFragment.this.toAddressTextView.createChips();
                    }
                    ComposeViewFragment.this.bccAddressTextView.setOnEntryAddedListener(new BccViewEntryAddedListener());
                    ComposeViewFragment.this.bccAddressTextView.removeDuplicates(ComposeViewFragment.this.bccAddressTextView.getChips());
                    ComposeViewFragment.this.setSignatureAndOtherDetailsInComposeEditText();
                    if (ComposeViewFragment.this.initialAddressState == null) {
                        ComposeViewFragment.this.initialAddressState = new ComposeState(ComposeViewFragment.this.mTo, ComposeViewFragment.this.mCC, ComposeViewFragment.this.mBCC, ComposeViewFragment.this.mSubject, ComposeViewFragment.this.composeMail.getText().toString());
                    }
                    ComposeViewFragment.this.setQuotedTextInWebView();
                }
            }
        });
        supportActionBar.setCustomView(inflate);
    }

    /* renamed from: com.cloudmagic.android.fragments.ComposeViewFragment$46, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass46 {
        static final /* synthetic */ int[] $SwitchMap$com$cloudmagic$android$fragments$ComposeViewFragment$FromFieldSelection = new int[FromFieldSelection.values().length];

        static {
            try {
                $SwitchMap$com$cloudmagic$android$fragments$ComposeViewFragment$FromFieldSelection[FromFieldSelection.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$cloudmagic$android$fragments$ComposeViewFragment$FromFieldSelection[FromFieldSelection.INIT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$cloudmagic$android$fragments$ComposeViewFragment$FromFieldSelection[FromFieldSelection.AUTO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public class CMJSInterface {
        Context mContext;

        CMJSInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public boolean checkFileExist(String str) {
            return new File(str).exists();
        }

        @JavascriptInterface
        public String getAbsoluteContentPath(String str) {
            return Constants.HTTPS + Utilities.getServerMobilePageUrl(this.mContext) + str;
        }

        @JavascriptInterface
        public String getSignatureAttachmentData() {
            return ComposeViewFragment.this.mSignatureAttachmentData;
        }

        @JavascriptInterface
        public void notify(final String str, final String str2) {
            ComposeViewFragment.this.handler.post(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.CMJSInterface.1
                @Override // java.lang.Runnable
                public void run() {
                    boolean z;
                    if (str.equals("editor_key_press")) {
                        ComposeViewFragment.this.mIsMessageDirty = true;
                        ComposeViewFragment.this.mIsQuotedTextDirty = true;
                        return;
                    }
                    if (str.equals("editorHeight_keyUp")) {
                        ComposeViewFragment.this.mIsMessageDirty = true;
                        ComposeViewFragment.this.mIsQuotedTextDirty = true;
                        final int parseInt = Integer.parseInt(str2);
                        ComposeViewFragment.this.handler.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.CMJSInterface.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                int i = parseInt - ComposeViewFragment.this.mOldWebViewHeightForWebViewKeyUp;
                                float f = CMJSInterface.this.mContext.getResources().getDisplayMetrics().density;
                                if (i < parseInt) {
                                    ComposeViewFragment.this.scrollView.scrollBy(0, i * ((int) f));
                                    ComposeViewFragment.this.mOldWebViewHeightForWebViewKeyUp = parseInt;
                                    ComposeViewFragment.this.mIsWebViewFocused = false;
                                    return;
                                }
                                ComposeViewFragment.this.mOldWebViewHeightForWebViewKeyUp = parseInt;
                            }
                        }, 100L);
                        return;
                    }
                    if (str.equals("editor_Focus")) {
                        if (ComposeViewFragment.this.mShouldWebViewGetFocus.booleanValue()) {
                            ComposeViewFragment.this.handler.post(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.CMJSInterface.1.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.scrollView.getScrollY() - 60);
                                    ComposeViewFragment.this.mIsWebViewFocused = false;
                                }
                            });
                        }
                        ComposeViewFragment.this.mIsWebViewFocused = true;
                        return;
                    }
                    if (str.equals("editor_LostFocus")) {
                        ComposeViewFragment.this.mShouldWebViewGetFocus = false;
                        return;
                    }
                    if (str.equals("get_quoted_text")) {
                        try {
                            JSONObject jSONObject = new JSONObject(str2);
                            ArrayList arrayList = new ArrayList();
                            JSONArray optJSONArray = jSONObject.optJSONArray("inlineAttachmentCID");
                            ArrayList arrayList2 = new ArrayList();
                            if (optJSONArray != null) {
                                for (int i = 0; i < optJSONArray.length(); i++) {
                                    arrayList.add(optJSONArray.getString(i));
                                }
                            }
                            if (ComposeViewFragment.this.mAttachment != null) {
                                for (int i2 = 0; i2 < ComposeViewFragment.this.mAttachment.size(); i2++) {
                                    if (((Attachment) ComposeViewFragment.this.mAttachment.get(i2)).isInlineAttachment() && !((Attachment) ComposeViewFragment.this.mAttachment.get(i2)).isSignatureAttachment && !arrayList.contains(((Attachment) ComposeViewFragment.this.mAttachment.get(i2)).cid)) {
                                        arrayList2.add(ComposeViewFragment.this.mAttachment.get(i2));
                                    }
                                }
                            }
                            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                                ComposeViewFragment.this.mAttachment.remove(arrayList2.get(i3));
                            }
                            ComposeViewFragment.this.mQuotedText = jSONObject.getString("innerHTML");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ComposeViewFragment.this.getContactFromDBAndSendMessage();
                        return;
                    }
                    if (str.equals("get_signature_text")) {
                        if (TextUtils.isEmpty(str2)) {
                            return;
                        }
                        try {
                            JSONObject jSONObject2 = new JSONObject(str2);
                            ComposeViewFragment.this.mCMSignature = jSONObject2.getString("signature");
                            JSONArray optJSONArray2 = jSONObject2.optJSONArray("cids");
                            if (!TextUtils.isEmpty(ComposeViewFragment.this.mSignatureAttachmentData)) {
                                try {
                                    JSONArray jSONArray = new JSONArray(ComposeViewFragment.this.mSignatureAttachmentData);
                                    ArrayList<String> arrayList3 = new ArrayList();
                                    for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                                        String string = ((JSONObject) jSONArray.get(i4)).getString(Constants.CID);
                                        int i5 = 0;
                                        while (true) {
                                            if (i5 >= optJSONArray2.length()) {
                                                z = true;
                                                break;
                                            } else {
                                                if (optJSONArray2.get(i5) != null && optJSONArray2.get(i5).equals(string)) {
                                                    z = false;
                                                    break;
                                                }
                                                i5++;
                                            }
                                        }
                                        if (z) {
                                            arrayList3.add(string);
                                        }
                                    }
                                    if (!arrayList3.isEmpty()) {
                                        for (String str3 : arrayList3) {
                                            if (ComposeViewFragment.this.mAttachment != null) {
                                                Iterator it = ComposeViewFragment.this.mAttachment.iterator();
                                                while (true) {
                                                    if (!it.hasNext()) {
                                                        break;
                                                    }
                                                    Attachment attachment = (Attachment) it.next();
                                                    if (attachment != null && attachment.cid != null && attachment.cid.equals(str3)) {
                                                        it.remove();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (JSONException e2) {
                                    Log.e(ComposeViewFragment.TAG, "Error occurred while parsing signature attachment data", e2);
                                }
                            }
                            if (!ComposeViewFragment.this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && !ComposeViewFragment.this.isQuotedTextRemoved()) {
                                if (Build.VERSION.SDK_INT >= 19) {
                                    ComposeViewFragment.this.webView.evaluateJavascript("javascript:CMgetQuotedText();", null);
                                    return;
                                } else {
                                    ComposeViewFragment.this.webView.loadUrl("javascript:CMgetQuotedText();");
                                    return;
                                }
                            }
                            ComposeViewFragment.this.getContactFromDBAndSendMessage();
                            return;
                        } catch (JSONException unused) {
                            return;
                        }
                    }
                    if (str.equals("signature_editor_key_press")) {
                        ComposeViewFragment.this.mIsSignatureDirty = true;
                    }
                }
            });
        }
    }

    private void clearFocusAndCommitChips() {
        ChipsAddressTextView focusedChipAddressTextView = getFocusedChipAddressTextView();
        if (focusedChipAddressTextView != null) {
            focusedChipAddressTextView.chipify();
            setValuesToRecipientMap();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendReplyArchiveMessage(boolean z) {
        final UserPreferences userPreferences = UserPreferences.getInstance(this.mContext);
        this.mReplyArchiveAllowed = z;
        if (z && !userPreferences.isReplyArchiveDialogInComposeShown()) {
            SimpleDialogFragment newInstance = SimpleDialogFragment.newInstance(getResources().getString(R.string.conversation_will_be_archived_message), true);
            newInstance.setCallback(new SimpleDialogFragment.Callback() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.28
                @Override // com.cloudmagic.android.dialogs.SimpleDialogFragment.Callback
                public void okClicked() {
                    ComposeViewFragment.this.sendMailFromSendOption();
                    userPreferences.replyArchiveDialogInComposeShown();
                }
            });
            FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            beginTransaction.add(newInstance, SimpleDialogFragment.TAG);
            beginTransaction.commitAllowingStateLoss();
            return;
        }
        sendMailFromSendOption();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMailFromSendOption() {
        hideEmailSuggestionContainer();
        generateMessage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void isReplyAndArchiveAllowedForSendDraft() {
        if (this.mReferenceConversation != null && this.mReferenceConversation.conversationId != 0) {
            new Thread(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.29
                @Override // java.lang.Runnable
                public void run() {
                    CMDBWrapper cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
                    try {
                        Folder draftFolder = Utilities.getDraftFolder(ComposeViewFragment.this.mContext, ComposeViewFragment.this.mAccountID, true);
                        if (draftFolder != null) {
                            final boolean conversationContainsNonDraftMails = cMDBWrapper.conversationContainsNonDraftMails(ComposeViewFragment.this.mReferenceConversation.conversationId, draftFolder.id);
                            if (ComposeViewFragment.this.getActivity() != null) {
                                ComposeViewFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.29.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        ComposeViewFragment.this.sendReplyArchiveMessage(conversationContainsNonDraftMails);
                                    }
                                });
                            }
                        }
                    } finally {
                        if (cMDBWrapper != null) {
                            cMDBWrapper.close();
                        }
                    }
                }
            }).start();
        } else {
            sendReplyArchiveMessage(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void generateMessage() {
        this.mMailSendingOrSavingIsInProcess = true;
        this.bccAddressTextView.getChips();
        clearFocusAndCommitChips();
        if (this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) || isQuotedTextRemoved()) {
            if (isQuotedTextRemoved() && !this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE)) {
                ArrayList arrayList = new ArrayList();
                if (this.mAttachment != null) {
                    for (int i = 0; i < this.mAttachment.size(); i++) {
                        if (this.mAttachment.get(i).isInlineAttachment() && !this.mAttachment.get(i).isSignatureAttachment) {
                            arrayList.add(this.mAttachment.get(i));
                        }
                    }
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        this.mAttachment.remove(arrayList.get(i2));
                    }
                }
            }
            if (this.signatureWebview.getVisibility() == 0) {
                if (Build.VERSION.SDK_INT >= 19) {
                    this.signatureWebview.evaluateJavascript("javascript:CMgetSignature();", null);
                } else {
                    this.signatureWebview.loadUrl("javascript:CMgetSignature();");
                }
            } else {
                getContactFromDBAndSendMessage();
            }
        } else if (this.signatureWebview.getVisibility() == 0) {
            if (Build.VERSION.SDK_INT >= 19) {
                this.signatureWebview.evaluateJavascript("javascript:CMgetSignature();", null);
            } else {
                this.signatureWebview.loadUrl("javascript:CMgetSignature();");
            }
        } else if (Build.VERSION.SDK_INT >= 19) {
            this.webView.evaluateJavascript("javascript:CMgetQuotedText();", null);
        } else {
            this.webView.loadUrl("javascript:CMgetQuotedText();");
        }
        Utilities.hideSoftKeyboard(getActivity());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isQuotedTextRemoved() {
        return this.webViewContainer.getVisibility() == 8;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GetContactFromDBAsyncTask extends AsyncTask<String, Void, List<Contact>> {
        private GetContactFromDBAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public List<Contact> doInBackground(String... strArr) {
            CMDBWrapper cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
            List<Contact> contacts = cMDBWrapper.getContacts(strArr[0], 1);
            cMDBWrapper.close();
            return contacts;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(List<Contact> list) {
            if (list != null && list.size() > 0) {
                ComposeViewFragment.this.contact = list.get(0);
            }
            ComposeViewFragment.this.sendMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SendMailAsyncTask extends AsyncTask<String, Void, Boolean> {
        private SendMailAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public Boolean doInBackground(String... strArr) {
            CMDBWrapper cMDBWrapper;
            Throwable th;
            try {
                cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
            } catch (Throwable th2) {
                cMDBWrapper = null;
                th = th2;
            }
            try {
                if (cMDBWrapper.getFolderID(1, ComposeViewFragment.this.messageToSend.accountId) != -1) {
                    if (cMDBWrapper != null) {
                        cMDBWrapper.close();
                    }
                    return true;
                }
                String sentMailDestinationFolderLabel = UserAccount.getSentMailDestinationFolderLabel(ComposeViewFragment.this.mContext, ComposeViewFragment.this.mAccountID);
                String sentMailDestinationFolderMailBoxPath = UserAccount.getSentMailDestinationFolderMailBoxPath(ComposeViewFragment.this.mContext, ComposeViewFragment.this.mAccountID);
                if (sentMailDestinationFolderMailBoxPath == null || sentMailDestinationFolderMailBoxPath.length() == 0) {
                    if (ComposeViewFragment.this.mAccountType == 2) {
                        if (cMDBWrapper != null) {
                            cMDBWrapper.close();
                        }
                        return true;
                    }
                    ComposeViewFragment.this.mNewActivityStarted = true;
                    ActionService.configureFolderForAction(ComposeViewFragment.this, ComposeViewFragment.this.mAccountID, -1, ActionService.ACTION_TYPE_SENT, null, true);
                } else {
                    if (cMDBWrapper.getFolderFromLabelAndMailboxPath(ComposeViewFragment.this.mAccountID, sentMailDestinationFolderLabel, sentMailDestinationFolderMailBoxPath) != null || ComposeViewFragment.this.mAccountType == 2) {
                        if (cMDBWrapper != null) {
                            cMDBWrapper.close();
                        }
                        return true;
                    }
                    ComposeViewFragment.this.mNewActivityStarted = true;
                    ActionService.configureFolderForAction(ComposeViewFragment.this, ComposeViewFragment.this.mAccountID, -1, ActionService.ACTION_TYPE_SENT, null, true);
                }
                if (cMDBWrapper != null) {
                    cMDBWrapper.close();
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                if (cMDBWrapper != null) {
                    cMDBWrapper.close();
                }
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(Boolean bool) {
            if (bool.booleanValue()) {
                ComposeViewFragment.this.send(ComposeViewFragment.this.messageToSend);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage() {
        this.messageToSend = new Message();
        UserAccount userAccount = (UserAccount) this.fromFieldSpinner.getSelectedItem();
        String str = userAccount.accountName;
        this.messageToSend.accountName = str;
        this.messageToSend.accountType = userAccount.accountType;
        this.messageToSend.containsFromAsAlias = userAccount.isTreatedAsAlias;
        JSONArray jSONArray = new JSONArray();
        if (this.contact != null) {
            jSONArray.put(this.contact.name);
        } else {
            jSONArray.put("");
        }
        jSONArray.put(str);
        this.messageToSend.fromAddress = jSONArray.toString();
        if ((!areAllAddressesValid(this.toAddressTextView).booleanValue() || !areAllAddressesValid(this.ccAddressTextView).booleanValue() || !areAllAddressesValid(this.bccAddressTextView).booleanValue()) && !this.mSaveAsDraft) {
            showDialog(getResources().getString(R.string.invalid_email_addresses));
            return;
        }
        this.messageToSend.toAddresses = getAddressStringFromView(this.toAddressTextView);
        this.messageToSend.ccAddresses = getAddressStringFromView(this.ccAddressTextView);
        this.messageToSend.bccAddresses = getAddressStringFromView(this.bccAddressTextView);
        try {
            if (new JSONArray(this.messageToSend.toAddresses).length() == 0 && new JSONArray(this.messageToSend.ccAddresses).length() == 0 && new JSONArray(this.messageToSend.bccAddresses).length() == 0 && !this.mSaveAsDraft) {
                showDialog(getResources().getString(R.string.no_recipients));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.messageToSend.subject = this.subjectView.getOriginalText();
        if (this.mSignature != null && !this.mSignature.isEmpty() && userAccount.hasHtmlSignature) {
            this.mIsBodyHTML = true;
        }
        if (this.mIsBodyHTML || this.mQuotedText != null) {
            this.messageToSend.isHtmlMessage = 1;
        } else {
            this.messageToSend.isHtmlMessage = 0;
        }
        this.messageToSend.accountId = this.mAccountID;
        this.composeMail.clearComposingText();
        if (this.mIsBodyHTML) {
            Spanned text = this.composeMail.getText();
            String obj = text.toString();
            if (Build.VERSION.SDK_INT >= 24) {
                if (!obj.endsWith("\n")) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
                    spannableStringBuilder.append((CharSequence) "\n");
                    text = SpannableString.valueOf(spannableStringBuilder);
                }
                this.mBody = Html.toHtml(text, 63);
            } else {
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(text);
                if (obj.endsWith("\n\n")) {
                    spannableStringBuilder2.append((CharSequence) "\n\n");
                } else if (obj.endsWith("\n")) {
                    spannableStringBuilder2.append((CharSequence) "\n\n\n");
                } else {
                    spannableStringBuilder2.append((CharSequence) "\n\n\n\n");
                }
                this.mBody = Html.toHtml(SpannableString.valueOf(spannableStringBuilder2));
                if (this.mBody.startsWith("<p dir=\"ltr\">")) {
                    this.mBody = this.mBody.replaceFirst("<p dir=\"ltr\">", "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">");
                } else if (this.mBody.startsWith("<p dir=\"rtl\">")) {
                    this.mBody = this.mBody.replaceFirst("<p dir=\"rtl\">", "<p dir=\"rtl\" style=\"margin-top:0; margin-bottom:0;\">");
                }
            }
        } else {
            this.mBody = this.composeMail.getText().toString();
            if (this.mBody.length() <= 0 || this.mBody.charAt(this.mBody.length() - 1) != '\n') {
                this.mBody += '\n';
            }
            this.mBody = this.mBody.replace("\n", "<br/>");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mBody);
        sb.append(this.mCMSignature == null ? "" : this.mCMSignature);
        this.mBody = sb.toString();
        if (!isQuotedTextRemoved()) {
            if (!this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.mBody);
                sb2.append(this.mQuotedText == null ? "" : this.mQuotedText);
                this.mBody = sb2.toString();
                if (this.mIsQuotedTextDirty) {
                    this.messageToSend.smartBody = this.mBody;
                } else {
                    this.messageToSend.smartBody = null;
                }
            }
        } else {
            this.messageToSend.smartBody = null;
        }
        Message message = this.messageToSend;
        Message message2 = this.messageToSend;
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        message2.tsMessageLanding = currentTimeMillis;
        message.tsMessageSending = currentTimeMillis;
        ObjectStorageSingleton objectStorageSingleton = ObjectStorageSingleton.getInstance(this.mContext);
        String str2 = "message_body_plain_" + this.messageToSend.tsMessageLanding;
        String str3 = "message_body_html_" + this.messageToSend.tsMessageLanding;
        if (this.mIsBodyHTML) {
            objectStorageSingleton.storeObject(str2, null);
            objectStorageSingleton.storeObject(str3, this.mBody);
        } else {
            String obj2 = Html.fromHtml(this.mBody).toString();
            objectStorageSingleton.storeObject(str2, obj2);
            objectStorageSingleton.storeObject(str3, obj2);
        }
        if (this.mIsStarred) {
            this.messageToSend.setFolderTypeList(new int[]{-1});
        }
        if (this.mAttachment != null) {
            checkAttachmentsStatus();
        } else {
            prepareToSend();
        }
    }

    private void checkAttachmentsStatus() {
        if (checkNumberOfAttachments(((UserAccount) this.fromFieldSpinner.getSelectedItem()).maxAttachmentCount)) {
            return;
        }
        checkAttachmentImageSizeAndSend();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkFilesAttachmentSize() {
        long maxAttachmentSizePerEmail = AccountSettingsPreferences.getInstance(this.mContext).getMaxAttachmentSizePerEmail((UserAccount) this.fromFieldSpinner.getSelectedItem());
        long j = ((UserAccount) this.fromFieldSpinner.getSelectedItem()).maxAttachmentSizePerFile;
        Iterator<Attachment> it = this.mAttachment.iterator();
        int i = 0;
        while (it.hasNext()) {
            Attachment next = it.next();
            if (next.size > j && !next.isInlineAttachment()) {
                String string = getString(R.string.attachment_large_file);
                String format = String.format(getString(R.string.compose_mail_attachment_too_large), Long.valueOf(j / 1048576));
                this.mSaveAsDraft = false;
                this.mGoBackOnSaveDraft = false;
                showDialog(string, format);
                return;
            }
            if (!next.isInlineAttachment()) {
                i = (int) (i + next.size);
            }
        }
        if (i > maxAttachmentSizePerEmail) {
            long j2 = maxAttachmentSizePerEmail / 1048576;
            String format2 = String.format(getString(R.string.max_attachment_size_exceeded), Long.valueOf(j2));
            String format3 = String.format(getString(R.string.compose_mail_attachments_exceed_limit), Long.valueOf(j2));
            this.mSaveAsDraft = false;
            this.mGoBackOnSaveDraft = false;
            showDialog(format2, format3);
            return;
        }
        prepareToSend();
    }

    private boolean checkNumberOfAttachments(int i) {
        int size = this.mAttachment.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (!this.mAttachment.get(i3).isInlineAttachment()) {
                i2++;
            }
        }
        if (i2 <= i) {
            return false;
        }
        showDialog(getString(R.string.too_many_attachments), String.format(getString(R.string.compose_mail_attachments_too_many), Integer.valueOf(i)));
        return true;
    }

    private void prepareToSend() {
        this.messageToSend.attachmentList = this.mAttachment;
        boolean z = true;
        if (this.messageToSend.attachmentList != null && this.messageToSend.attachmentList.size() != 0) {
            this.messageToSend.hasAttachments = 1;
        }
        this.messageToSend.references = this.mReferences;
        this.messageToSend.mimeId = this.mMimeId;
        this.messageToSend.mimeInReplyToId = this.mMimeInReplyToId;
        if (this.mInReplyTo != null && this.mInReplyTo.length() > 0) {
            this.messageToSend.replyToAddresses = this.mInReplyTo;
        }
        if (this.mIsEditDraft) {
            this.messageToSend.messageResourceId = this.mPreviousDraftId;
            this.messageToSend.folderIdList = this.mFolderIdList;
            this.messageToSend.folderTypeList = this.mFolderTypeList;
        }
        if (this.mIsEditOutbox) {
            this.messageToSend.messageResourceId = this.mReferenceResourceId;
        }
        if (!this.mSaveAsDraft && this.mSnoozeTime != 0) {
            this.messageToSend.tsSnoozeRelativeUtc = (this.mSnoozeTime / 1000) + UserPreferences.getInstance(this.mContext).getSnoozeTimezoneOffset();
            this.messageToSend.tsSnoozeCreated = System.currentTimeMillis() / 1000;
        }
        this.messageToSend.shouldNotifyWhenRead = this.shouldNotifyWhenRead;
        boolean z2 = UserPreferences.getInstance(this.mContext).isRREnabledForAllEmails() && UserPreferences.getInstance(this.mContext).isRREnabled();
        if (((Newton) ProductFactory.getProduct(0, this.mContext)).canAccessFeature(3)) {
            Message message = this.messageToSend;
            if (!z2 && this.messageToSend.shouldNotifyWhenRead == 0) {
                z = false;
            }
            message.isTracked = z;
        } else {
            this.messageToSend.isTracked = false;
        }
        if (!this.mSaveAsDraft && this.mSendLaterTime != 0) {
            this.messageToSend.tsSend = this.mSendLaterTime / 1000;
            this.messageToSend.isSendAutoCancelable = this.mIsAutoCancellable;
        }
        if (this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && this.subjectView.getOriginalText() != null && this.subjectView.getOriginalText().toString().trim().equals("") && this.composeMail.getText().toString().trim().equals("") && !this.mSaveAsDraft && (this.mAttachment == null || this.mAttachment.size() == 0)) {
            showSendConfigurationDialog(this.messageToSend);
        } else if (this.mSaveAsDraft) {
            send(this.messageToSend);
        } else {
            checkAndSend(this.messageToSend);
        }
    }

    public void showSendConfigurationDialog(final Message message) {
        if (this.mAttachment != null && this.mAttachment.size() > 0) {
            refreshVisibleListViewItems();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SpannableString spannableString = Utilities.getSpannableString(this.mContext, "");
        SpannableString spannableString2 = Utilities.getSpannableString(this.mContext, getResources().getString(R.string.compose_no_body));
        SpannableString spannableStringBold = Utilities.getSpannableStringBold(this.mContext, getString(R.string.cancel));
        SpannableString spannableStringBold2 = Utilities.getSpannableStringBold(this.mContext, getResources().getString(R.string.compose_send));
        builder.setTitle(spannableString);
        builder.setNegativeButton(spannableStringBold, new DialogInterface.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.30
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton(spannableStringBold2, new DialogInterface.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.31
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ComposeViewFragment.this.checkAndSend(message);
            }
        });
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.dialog_message, (ViewGroup) null);
        ((CustomTextView) inflate.findViewById(android.R.id.message)).setText(spannableString2);
        builder.setView(inflate);
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndSend(Message message) {
        String shouldShowDoesnotContainAttachment = AttachmentUtils.shouldShowDoesnotContainAttachment(this.composeMail.getText().toString(), this.mAttachment, getContext());
        if (shouldShowDoesnotContainAttachment == null && !this.mActionType.equals(ActionService.ACTION_TYPE_REPLY) && !this.mActionType.equals("reply_all")) {
            shouldShowDoesnotContainAttachment = AttachmentUtils.shouldShowDoesnotContainAttachment(message.subject, this.mAttachment, getContext());
        }
        if (shouldShowDoesnotContainAttachment != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(Utilities.getSpannableStringBold(this.mContext, this.mContext.getString(R.string.attachment_reminder_title_in_compose))).setMessage(Utilities.getSpannableString(this.mContext, String.format(this.mContext.getResources().getString(R.string.attachment_reminder_message_in_compose), shouldShowDoesnotContainAttachment))).setPositiveButton(Utilities.getSpannableStringBold(this.mContext, getString(R.string.attachment_reminder_just_send_in_compose)), new DialogInterface.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.33
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    new SendMailAsyncTask().execute(new String[0]);
                }
            }).setNegativeButton(Utilities.getSpannableStringBold(this.mContext, getString(R.string.calendar_sorry_goback)), new DialogInterface.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.32
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.create().show();
            return;
        }
        new SendMailAsyncTask().execute(new String[0]);
    }

    private void checkAttachmentImageSizeAndSend() {
        if (this.mSaveAsDraft && !this.mGoBackOnSaveDraft) {
            checkFilesAttachmentSize();
            return;
        }
        boolean z = false;
        if (this.mAttachment != null && this.mAttachment.size() > 0) {
            Iterator<Attachment> it = this.mAttachment.iterator();
            while (it.hasNext()) {
                Attachment next = it.next();
                if (next.size > 716800 && next.isImageScallingAllowed() && (this.attachmentsFromBundle == null || !this.attachmentsFromBundle.contains(next.name))) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(next.localStoragePath, options);
                    if (options.outHeight > 1280 || options.outWidth > 1280) {
                        z = true;
                        break;
                    }
                }
            }
        }
        if (!z) {
            checkFilesAttachmentSize();
            return;
        }
        AttachmentSizeOptimiseDialog attachmentSizeOptimiseDialog = new AttachmentSizeOptimiseDialog();
        attachmentSizeOptimiseDialog.setCallBack(new AttachmentSizeOptimiseDialog.AttachmentSizeOptimiseDialogCallBack() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.34
            @Override // com.cloudmagic.android.dialogs.AttachmentSizeOptimiseDialog.AttachmentSizeOptimiseDialogCallBack
            public void justSend() {
                ComposeViewFragment.this.checkFilesAttachmentSize();
            }

            @Override // com.cloudmagic.android.dialogs.AttachmentSizeOptimiseDialog.AttachmentSizeOptimiseDialogCallBack
            public void optimiseAndSend() {
                new ImageOptimizerTask().execute(new Void[0]);
            }

            @Override // com.cloudmagic.android.dialogs.AttachmentSizeOptimiseDialog.AttachmentSizeOptimiseDialogCallBack
            public void cancel() {
                ComposeViewFragment.this.mSaveAsDraft = false;
                ComposeViewFragment.this.mGoBackOnSaveDraft = false;
                ComposeViewFragment.this.mMailSendingOrSavingIsInProcess = false;
            }
        });
        attachmentSizeOptimiseDialog.setNumberOfAttachments(this.mAttachment.size());
        attachmentSizeOptimiseDialog.setIsSaveDraft(this.mSaveAsDraft);
        attachmentSizeOptimiseDialog.show(getActivity().getFragmentManager(), AttachmentSizeOptimiseDialog.TAG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void optimizeAttachmentImages() {
        Iterator<Attachment> it = this.mAttachment.iterator();
        while (it.hasNext()) {
            Attachment next = it.next();
            if (next.size > 716800 && next.isImageScallingAllowed() && (this.attachmentsFromBundle == null || !this.attachmentsFromBundle.contains(next.name))) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(next.localStoragePath, options);
                    if (options.outWidth > 1280 || options.outHeight > 1280) {
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = Utilities.calculateInSampleSize(options, ImageOptimizationsUtils.SCALE_SIZE, ImageOptimizationsUtils.SCALE_SIZE);
                        File storeScaledBitmap = ImageOptimizationsUtils.storeScaledBitmap(this.mContext, ImageOptimizationsUtils.getScaledBitmap(BitmapFactory.decodeFile(next.localStoragePath, options)), next.name, next.mimeType, this.messageToSend.tsMessageSending);
                        if (storeScaledBitmap != null && storeScaledBitmap.exists()) {
                            ((ImageListAdapter) this.attachmentImagecontainer.getAdapter()).removeSelectedItem(next.localStoragePath);
                            next.localStoragePath = storeScaledBitmap.getPath();
                            next.size = storeScaledBitmap.length();
                            next.isScaledDownImage = true;
                        }
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void send(Message message) {
        Intent intent = new Intent(this.mContext, (Class<?>) ActionService.class);
        intent.putExtra("message", message);
        intent.putExtra("account_id", this.mAccountID);
        intent.putExtra("is_draft", this.mSaveAsDraft);
        if (this.mIsEditOutbox) {
            intent.putExtra(IS_EDIT_OUTBOX, true);
        }
        intent.setAction(this.mActionType);
        if (!TextUtils.isEmpty(this.mMailEditSource) && ((this.mMailEditSource.equals(OUTBOX_EDIT_FROM_LIST) || this.mMailEditSource.equals(OUTBOX_EDIT_FROM_PREVIEW)) && !this.mActionType.equals(ActionService.ACTION_TYPE_SEND_DRAFT))) {
            intent.putExtra("is_send_draft", false);
        } else {
            intent.putExtra("is_send_draft", this.mIsEditDraft);
        }
        if (getArguments() != null && getArguments().getString("loc") != null) {
            intent.putExtra("loc", getArguments().getString("loc"));
        } else if (!this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE)) {
            intent.putExtra("loc", ActionService.ACTION_LOCATION_PREVIEW);
        }
        if (message.belongsToFolder(-1) && this.mTSReminder > 0) {
            intent.putExtra("ts_reminder", this.mTSReminder);
            intent.putExtra("is_custom_time", this.mIsCustomReminder);
        }
        if (this.mReferenceResourceId != null && (!this.mIsSendAgain || this.subjectView.getOriginalText().equals(this.mSubject))) {
            intent.putExtra("reference_conversation", this.mReferenceConversation);
            intent.putExtra(CalendarConstants.KEY_MAIL_DATA_REFERENCE_RES_ID, this.mReferenceResourceId);
        }
        if ((this.mActionType.equals(ActionService.ACTION_TYPE_REPLY) || this.mActionType.equals("reply_all") || this.mActionType.equals(ActionService.ACTION_TYPE_FORWARD) || this.mActionType.equals(ActionService.ACTION_TYPE_SEND_DRAFT) || (this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && this.mIsEditDraft)) && !this.subjectView.getOriginalText().equals(this.mSubject)) {
            intent.putExtra("create_new_conversation", true);
            this.mSubject = this.subjectView.getOriginalText();
        }
        if (((this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && this.mIsEditDraft) || !this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE)) && this.mActualMessageAccountId != -1 && this.mActualMessageAccountId != this.mAccountID) {
            intent.putExtra("create_new_conversation", true);
        }
        if (this.mIsEditDraft) {
            intent.putExtra("discard_draft_payload", this.mOldDiscardDraftPayload);
        }
        if (this.mCurrentFolder != null) {
            intent.putExtra("current_folder", this.mCurrentFolder);
        }
        if (this.mSaveAsDraft) {
            this.mIsMessageDirty = false;
            this.mSaveAsDraft = false;
            if (this.mGoBackOnSaveDraft) {
                if (this.mActionTrigger != null && this.mActionTrigger.equals(IS_DEFAULT_CM_ACTION) && (((this.mMailEditSource != null && this.mMailEditSource.equals(OUTBOX_EDIT_FROM_LIST)) || (this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && (this.mMailEditSource == null || this.mMailEditSource.equals(DRAFT_EDIT_FROM_LIST)))) && !this.mIsSendAgain)) {
                    if (getActivity() != null) {
                        getActivity().setResult(-1, intent);
                    }
                } else if (getArguments() != null && getArguments().getString("on_back", null) != null) {
                    if (getActivity() != null) {
                        ((ComposeActivity) getActivity()).setDraftData(intent.getExtras());
                    }
                } else {
                    ActionService.enqueueWork(this.mContext, intent);
                    Utilities.showCustomToast(this.mContext, this.mContext.getResources().getString(R.string.save_draft), 0, true);
                }
                if (getActivity() != null) {
                    ((ComposeActivity) getActivity()).goBack();
                    return;
                }
                return;
            }
            ActionService.enqueueWork(this.mContext, intent);
            Utilities.showCustomToast(this.mContext, this.mContext.getResources().getString(R.string.save_draft), 0, true);
            this.mDraftSavedPreviously = true;
            this.mIsEditDraft = true;
            this.mMailEditSource = null;
            this.mMailSendingOrSavingIsInProcess = false;
            return;
        }
        if (this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && TextUtils.isEmpty(this.mMailEditSource) && (this.mActionTrigger == null || this.mActionTrigger.equals(IS_SYSTEM_ACTION))) {
            intent.putExtra("toast_action_required", false);
            if (this.messageToSend.tsSend == 0) {
                Utilities.showCustomToast(this.mContext, this.mContext.getResources().getString(R.string.compose_sending_mail), 0, true);
            } else if (DateUtils.isToday(this.messageToSend.tsSend * 1000)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(this.messageToSend.tsSend * 1000);
                Utilities.showCustomToast(this.mContext, String.format(this.mContext.getResources().getString(R.string.compose_send_later_message), DateTimeUtils.getDisplayTime(this.mContext, calendar)), 0, true);
            } else {
                Utilities.showCustomToast(this.mContext, String.format(this.mContext.getResources().getString(R.string.compose_send_later_message), DateTimeUtils.getFormattedString(this.mContext, this.messageToSend.tsSend * 1000)), 0, true);
            }
        }
        intent.putExtra("is_reply_archive", this.mReplyArchiveAllowed);
        ActionService.enqueueWork(this.mContext, intent);
        this.mSaveAsDraft = false;
        this.mIsMessageDirty = false;
        this.mIsMailSent = true;
        if (getActivity() != null) {
            getActivity().setResult(-1);
            ((ComposeActivity) getActivity()).goBack();
        }
    }

    private void setCurrentStateAsInitialState() {
        this.initialAddressState = new ComposeState(createAddressArrayFromChip(getChipsForTextView(this.toAddressTextView)), createAddressArrayFromChip(getChipsForTextView(this.ccAddressTextView)), createAddressArrayFromChip(getChipsForTextView(this.bccAddressTextView)), "".equals(this.subjectView.getOriginalText()) ? null : this.subjectView.getOriginalText(), this.composeMail.getText().toString());
    }

    private String[] createAddressArrayFromChip(List<RecipientChip> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        String[] strArr = new String[list.size()];
        int i = 0;
        for (RecipientChip recipientChip : list) {
            strArr[i] = Utilities.getFormattedAddress(recipientChip.getEntry().getDisplayName(), recipientChip.getEntry().getAddress());
            i++;
        }
        return strArr;
    }

    private ArrayList<String> createAddressListFromChip(List<RecipientChip> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<RecipientChip> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getEntry().getAddress());
        }
        return arrayList;
    }

    private boolean isSameAsInitialAddressState() {
        String[] createAddressArrayFromChip = createAddressArrayFromChip(getChipsForTextView(this.toAddressTextView));
        String[] createAddressArrayFromChip2 = createAddressArrayFromChip(getChipsForTextView(this.ccAddressTextView));
        ArrayList<String> createAddressListFromChip = createAddressListFromChip(getChipsForTextView(this.bccAddressTextView));
        if (this.mAutoBCC != null) {
            for (int i = 0; i < this.mAutoBCC.length; i++) {
                if (createAddressListFromChip.contains(getAddressOnly(this.mAutoBCC[i])) && !this.customAddedBcc.contains(this.mAutoBCC[i])) {
                    createAddressListFromChip.remove(createAddressListFromChip.indexOf(getAddressOnly(this.mAutoBCC[i])));
                }
            }
        }
        return new ComposeState(createAddressArrayFromChip, createAddressArrayFromChip2, (String[]) createAddressListFromChip.toArray(new String[createAddressListFromChip.size()]), "".equals(this.subjectView.getOriginalText()) ? null : this.subjectView.getOriginalText(), this.composeMail.getText().toString()).equals(this.initialAddressState);
    }

    private String getAddressOnly(String str) {
        return str.substring(str.indexOf(60) + 1, str.indexOf(62)).trim();
    }

    public boolean saveDraft(final boolean z) {
        clearFocusAndCommitChips();
        if ((!this.mIsMessageDirty.booleanValue() || this.mIsSendAgain) && ((TextUtils.isEmpty(this.mMailEditSource) || !(this.mMailEditSource.equals(OUTBOX_EDIT_FROM_LIST) || this.mMailEditSource.equals(OUTBOX_EDIT_FROM_PREVIEW))) && isSameAsInitialAddressState() && !((z && this.mDraftSavedPreviously) || this.mIsSignatureDirty.booleanValue()))) {
            return false;
        }
        if (z || getActivity().isFinishing()) {
            this.mGoBackOnSaveDraft = z;
            this.mSaveAsDraft = true;
            generateMessage();
        } else {
            TimerTask timerTask = new TimerTask() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.35
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ComposeViewFragment.this.handler.post(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.35.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ComposeViewFragment.this.mGoBackOnSaveDraft = z;
                            ComposeViewFragment.this.mSaveAsDraft = true;
                            ComposeViewFragment.this.generateMessage();
                        }
                    });
                }
            };
            this.mTimer = new Timer();
            this.mTimer.schedule(timerTask, DRAFT_SAVE_WINDOW);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getContactFromDBAndSendMessage() {
        if (this.fromFieldSpinner == null || this.fromFieldSpinner.getAdapter() == null || this.fromFieldSpinner.getAdapter().getCount() == 0 || this.fromFieldSpinner.getSelectedItem() == null) {
            if (this.mSaveAsDraft && this.mGoBackOnSaveDraft) {
                ((ComposeActivity) getActivity()).goBack();
                return;
            }
            return;
        }
        if (this.mSaveAsDraft || ActionService.checkActionEnabledForAccount(this, ((UserAccount) this.fromFieldSpinner.getSelectedItem()).accountId)) {
            new GetContactFromDBAsyncTask().execute(((UserAccount) this.fromFieldSpinner.getSelectedItem()).accountName);
        }
    }

    private String getAddressStringFromView(RecipientEditTextView recipientEditTextView) {
        List<RecipientChip> chipsForTextView = getChipsForTextView(recipientEditTextView);
        if (chipsForTextView == null) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        for (RecipientChip recipientChip : chipsForTextView) {
            JSONArray jSONArray2 = new JSONArray();
            String displayName = recipientChip.getEntry().getDisplayName();
            if (displayName != null && displayName.length() != 0 && -2 == recipientChip.getEntry().getContactId()) {
                jSONArray2.put(Rfc822Token.quoteNameIfNecessary(displayName));
            } else {
                jSONArray2.put("");
            }
            jSONArray2.put(recipientChip.getEntry().getAddress());
            jSONArray.put(jSONArray2);
        }
        return jSONArray.toString();
    }

    private Boolean areAllAddressesValid(RecipientEditTextView recipientEditTextView) {
        List<RecipientChip> chipsForTextView = getChipsForTextView(recipientEditTextView);
        if (chipsForTextView != null) {
            Iterator<RecipientChip> it = chipsForTextView.iterator();
            while (it.hasNext()) {
                if (!Utilities.isEmailValid(it.next().getEntry().getAddress())) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<RecipientChip> getChipsForTextView(RecipientEditTextView recipientEditTextView) {
        List<RecipientChip> list;
        switch (recipientEditTextView.getId()) {
            case R.id.multiAutoCompleteTextView /* 2131297001 */:
                list = this.recipientChipMap.get("TO-CHIPS");
                break;
            case R.id.multiAutoCompleteTextViewBcc /* 2131297002 */:
                list = this.recipientChipMap.get("BCC-CHIPS");
                break;
            case R.id.multiAutoCompleteTextViewCc /* 2131297003 */:
                list = this.recipientChipMap.get("CC-CHIPS");
                break;
            default:
                list = null;
                break;
        }
        return list == null ? new ArrayList() : list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViews() {
        this.toRecipientAdapter = new NetworkRecipientAdapter(this.mContext, this.toAddressTextView, this);
        this.toAddressTextView.setAdapter(this.toRecipientAdapter);
        this.toAddressTextView.setTokenizer(new Rfc822Tokenizer());
        this.bccRecipientAdapter = new NetworkRecipientAdapter(this.mContext, this.bccAddressTextView, this);
        this.bccAddressTextView.setAdapter(this.bccRecipientAdapter);
        this.bccAddressTextView.setTokenizer(new Rfc822Tokenizer());
        this.ccRecipientAdapter = new NetworkRecipientAdapter(this.mContext, this.ccAddressTextView, this);
        this.ccAddressTextView.setAdapter(this.ccRecipientAdapter);
        this.ccAddressTextView.setTokenizer(new Rfc822Tokenizer());
        this.ccAddressContainer.setVisibility(this.mCCVisibility);
        this.bccAddressContainer.setVisibility(this.mBCCVisibility);
        this.toAddressTextView.setPopUpParentView((View) this.toAddressTextView.getParent().getParent().getParent());
        this.bccAddressTextView.setPopUpParentView((View) this.bccAddressTextView.getParent().getParent().getParent());
        this.ccAddressTextView.setPopUpParentView((View) this.ccAddressTextView.getParent().getParent().getParent());
        new GetEmailAccountsFromDBAsyncTask().execute(new Void[0]);
        if (this.mTo != null && this.mTo.length != 0) {
            for (int i = 0; i < this.mTo.length; i++) {
                this.toAddressTextView.setChipFromText(this.mTo[i]);
            }
        }
        if (this.mCC != null && this.mCC.length != 0) {
            for (int i2 = 0; i2 < this.mCC.length; i2++) {
                this.ccAddressTextView.setChipFromText(this.mCC[i2]);
            }
        }
        if (this.mBCC != null && this.mBCC.length != 0) {
            for (int i3 = 0; i3 < this.mBCC.length; i3++) {
                this.bccAddressTextView.setChipFromText(this.mBCC[i3]);
            }
        }
        if (this.shouldLoadFromPrefs && this.mAutoBCC != null && this.mAutoBCC.length != 0) {
            for (int i4 = 0; i4 < this.mAutoBCC.length; i4++) {
                this.bccAddressTextView.setChipFromText(this.mAutoBCC[i4]);
            }
        }
        this.bccAddressTextView.removeDuplicates(this.bccAddressTextView.getChips());
        this.subjectView.setOriginalText(this.mSubject);
        if ((this.mTo != null && this.mTo.length > 0) || ((this.mCC != null && this.mCC.length > 0) || (this.mBCC != null && this.mBCC.length > 0))) {
            foldTextViewLines(true);
            setAllValuesToToRecipientTextView();
            this.toAddressTextView.createChips();
            if (!this.composeMail.isFocused()) {
                this.composeMail.requestFocus();
            }
            this.menuButtonContainerTo.setVisibility(8);
            modifyToHeader();
        }
        if ((this.mIsEditDraft || this.mIsSendAgain || this.mIsEditOutbox) && !this.composeMail.isFocused()) {
            this.composeMail.requestFocus();
        }
        this.webView.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.36
            @Override // java.lang.Runnable
            public void run() {
                ComposeViewFragment.this.rootContainer.setVisibility(0);
                if (ComposeViewFragment.this.mTo == null || ComposeViewFragment.this.mTo.length <= 0) {
                    return;
                }
                ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.composeContainer.getTop());
            }
        }, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setQuotedTextInWebView() {
        if (this.mQuotedText != null) {
            if (!this.isWebViewLoaded.booleanValue() && this.mFrom != null) {
                loadWebView(this.mFrom[0]);
                this.isWebViewLoaded = true;
            } else {
                this.webView.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.37
                    @Override // java.lang.Runnable
                    public void run() {
                        ComposeViewFragment.this.mOldWebViewHeightForWebViewKeyUp = ComposeViewFragment.this.webView.getContentHeight();
                        ComposeViewFragment.this.scrollView.smoothScrollTo(0, ComposeViewFragment.this.webViewContainer.getTop() - 20);
                    }
                }, 300L);
            }
        }
    }

    private void removeSignatureAttachments() {
        if (this.mAttachment == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<Attachment> it = this.mAttachment.iterator();
        while (it.hasNext()) {
            Attachment next = it.next();
            if (next.isSignatureAttachment) {
                arrayList.add(next);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        this.mAttachment.removeAll(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSignatureAndOtherDetailsInComposeEditText() {
        String str;
        JSONArray jSONArray;
        boolean z;
        String composeFooterHyperlink;
        String str2 = "";
        UserPreferences userPreferences = UserPreferences.getInstance(this.mContext);
        String html = Html.toHtml(this.composeMail.getText());
        if (this.mBody != null && html.trim().length() == 0) {
            str2 = (!this.mIsEditDraft && !this.mIsSendAgain && !this.mIsEditOutbox) ? "" + Utilities.escapeHtmlString(this.mBody) : "" + this.mBody;
        }
        str = "";
        AccountSettingsPreferences accountSettingsPreferences = AccountSettingsPreferences.getInstance(getActivity());
        if (!this.mThreadContainsSent || accountSettingsPreferences.getSignatureStateOnReply(accountSettingsPreferences.getPreferenceKey(this.mAccountID, "show_signature_on_reply")) != 1 || (!this.mActionType.equals("reply_all") && !this.mActionType.equals(ActionService.ACTION_TYPE_REPLY) && !this.mIsSendAgain)) {
            if (this.mThreadContainsSent && accountSettingsPreferences.getSignatureStateOnReply(accountSettingsPreferences.getPreferenceKey(this.mAccountID, "show_signature_on_reply")) == 2 && ((this.mActionType.equals("reply_all") || this.mActionType.equals(ActionService.ACTION_TYPE_REPLY) || this.mIsSendAgain) && !this.mIsEditDraft && this.mSignature != null && this.mSignature.length() > 0)) {
                str = Utilities.getFirstNameFromFullNameCaps(UserPreferences.getInstance(getActivity()).getUserNickName());
            } else {
                if (!TextUtils.isEmpty(this.mSignatureAttachmentData)) {
                    try {
                        jSONArray = new JSONArray(this.mSignatureAttachmentData);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error occurred while parsing signature attachment data", e);
                    }
                    if (!this.mIsEditDraft && html.trim().length() == 0) {
                        if (this.mSignature != null && !this.mSignature.equals("")) {
                            removeAllSignatureAttachment(jSONArray);
                        }
                        Matcher matcher = Pattern.compile("<div\\s+?id\\s*?=\\s*?[\",']cm_signature[\",']\\s*?>(.*)<\\s*\\/\\s*div>").matcher(str2);
                        str = matcher.find() ? matcher.group(1) : "";
                        str2 = str2.replaceFirst("<div\\s+?id\\s*?=\\s*?[\",']cm_signature[\",']\\s*?>(.*)<\\s*\\/\\s*div>", "");
                    } else if (this.mSignature != null && !this.mSignature.equals("")) {
                        str = "" + this.mSignature.replace("\n", "<br/>");
                    }
                    if (jSONArray == null && jSONArray.length() > 0) {
                        if (this.mAttachment == null) {
                            this.mAttachment = new ArrayList<>();
                        } else {
                            removeSignatureAttachments();
                        }
                        ArrayList arrayList = new ArrayList();
                        for (int i = 0; i < jSONArray.length(); i++) {
                            JSONObject jSONObject = (JSONObject) jSONArray.opt(i);
                            if (jSONObject != null) {
                                String optString = jSONObject.optString(Constants.CID);
                                Iterator<Attachment> it = this.mAttachment.iterator();
                                while (true) {
                                    if (it.hasNext()) {
                                        if (optString.equals(it.next().cid)) {
                                            z = true;
                                            break;
                                        }
                                    } else {
                                        z = false;
                                        break;
                                    }
                                }
                                if (!z) {
                                    Attachment attachment = new Attachment("");
                                    attachment.cid = jSONObject.optString(Constants.CID);
                                    attachment.downloadUrl = jSONObject.optString(Constants.DOWNLOAD_PATH);
                                    attachment.contentPath = jSONObject.optString(Constants.CONTENT_PATH);
                                    attachment.mimeType = jSONObject.optString("ct");
                                    attachment.name = "signature_" + i;
                                    attachment.isSignatureAttachment = true;
                                    arrayList.add(attachment);
                                }
                            }
                        }
                        if (!arrayList.isEmpty()) {
                            this.mAttachment.addAll(arrayList);
                        }
                    } else {
                        removeSignatureAttachments();
                    }
                }
                jSONArray = null;
                if (!this.mIsEditDraft) {
                }
                if (this.mSignature != null) {
                    str = "" + this.mSignature.replace("\n", "<br/>");
                }
                if (jSONArray == null) {
                }
                removeSignatureAttachments();
            }
        }
        if (this.mIsSentUsingCMSet && ((!this.mIsEditDraft && !this.mIsSendAgain && !this.mIsEditOutbox) || html.trim().length() != 0)) {
            if (userPreferences.getMailFooter() != null && userPreferences.getMailFooter().length() > 0) {
                composeFooterHyperlink = userPreferences.getMailFooter();
            } else {
                composeFooterHyperlink = Utilities.getComposeFooterHyperlink(this.mContext);
            }
            if (this.mSignature != null && !this.mSignature.equals("")) {
                str = this.mSignature.matches(".*?<[^>]*>") ? str + "<br/>" + composeFooterHyperlink : str + "<br/><br/>" + composeFooterHyperlink;
            } else if ((this.mSignature == null || this.mSignature.equals("")) && !str.equals("")) {
                str = str + "<br/><br/>" + composeFooterHyperlink;
            } else {
                str = str + composeFooterHyperlink;
            }
        }
        if (!str2.equals("") && this.mBody != null && html.trim().length() == 0) {
            html = html.replace(this.mBody, "");
            this.mBody = null;
        }
        String replace = html.replace("\n", "<br/>");
        if (replace.trim().isEmpty()) {
            this.mIsBodyDirty = false;
        } else {
            this.mIsBodyDirty = true;
        }
        if (str2.length() == 0) {
            if (Build.VERSION.SDK_INT >= 24) {
                this.composeMail.setText(Html.fromHtml(replace, 63));
            } else {
                this.composeMail.setText(Html.fromHtml(replace));
            }
        } else if (Build.VERSION.SDK_INT >= 24) {
            this.composeMail.setText(Html.fromHtml(replace + str2, 63));
        } else {
            this.composeMail.setText(Html.fromHtml(replace + str2));
        }
        this.signatureWebview.setVisibility(8);
        if (str.replace("<br/>", "").length() != 0) {
            String cMSignature = Utilities.getCMSignature(str);
            this.signatureWebview.setOnTouchListener(this);
            this.signatureWebview.getSettings().setJavaScriptEnabled(true);
            this.signatureWebview.addJavascriptInterface(new CMJSInterface(getActivity()), "external");
            this.signatureWebview.setVisibility(0);
            this.signatureWebview.setWebChromeClient(new WebChromeClient() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.38
                @Override // android.webkit.WebChromeClient
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    String str3 = consoleMessage.message() + " -- From line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId();
                    CMLogger cMLogger = new CMLogger(ComposeViewFragment.this.mContext);
                    cMLogger.putMessage("Signature webview console log:" + str3);
                    cMLogger.commit();
                    return true;
                }
            });
            if (Build.VERSION.SDK_INT >= 21) {
                this.signatureWebview.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.signatureWebview, true);
            }
            if (Build.VERSION.SDK_INT >= 16) {
                this.signatureWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
            }
            Utilities.setUpCMCookies(this.mContext, Constants.HTTPS + Utilities.getServerMobilePageUrl(getActivity()));
            this.signatureWebview.loadDataWithBaseURL("file:///android_asset/", cMSignature, "text/html", "utf-8", null);
            this.signatureWebview.setWebViewClient(new WebViewClient() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.39
                @Override // android.webkit.WebViewClient
                public void onPageFinished(WebView webView, String str3) {
                    super.onPageFinished(webView, str3);
                    if (Build.VERSION.SDK_INT >= 19) {
                        ComposeViewFragment.this.signatureWebview.evaluateJavascript("javascript:CMonLoadFinished();", null);
                    } else {
                        ComposeViewFragment.this.signatureWebview.loadUrl("javascript:CMonLoadFinished();");
                    }
                }
            });
        }
    }

    private void removeAllSignatureAttachment(JSONArray jSONArray) {
        if (jSONArray == null || jSONArray.length() <= 0 || this.mAttachment == null || this.mAttachment.isEmpty()) {
            return;
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = (JSONObject) jSONArray.opt(i);
            if (jSONObject != null) {
                String optString = jSONObject.optString(Constants.CID);
                Iterator<Attachment> it = this.mAttachment.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (optString.equals(it.next().cid)) {
                            it.remove();
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addDraftsTextWatcher() {
        this.draftsTextWatcher = new DraftsTextWatcher();
        this.composeMail.addTextChangedListener(this.draftsTextWatcher);
    }

    private void loadWebView(String str) {
        Date date = new Date(this.mTSMessageReceived * 1000);
        String format = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH).format(date);
        String lowerCase = new SimpleDateFormat("h:mma", Locale.ENGLISH).format(date).toLowerCase();
        String str2 = "";
        if (this.mLastMessageSender != null) {
            if (this.mActionType.equals(ActionService.ACTION_TYPE_FORWARD)) {
                String str3 = "";
                if (this.mToReferences != null && this.mToReferences.size() > 0) {
                    String str4 = this.mToReferences.get(0).first + " &lt;<a href='mailto:" + this.mToReferences.get(0).second + "'>" + this.mToReferences.get(0).second + "</a>&gt;";
                    for (int i = 1; i < this.mToReferences.size(); i++) {
                        str4 = str4 + "<br/>" + this.mToReferences.get(i).first + " &lt;<a href='mailto:" + this.mToReferences.get(i).second + "'>" + this.mToReferences.get(i).second + "</a>&gt;";
                    }
                    str3 = str4;
                }
                String str5 = "";
                if (this.mCCReferences != null && this.mCCReferences.size() > 0) {
                    str5 = this.mCCReferences.get(0).first + " &lt;<a href='mailto:" + this.mCCReferences.get(0).second + "'>" + this.mCCReferences.get(0).second + "</a>&gt;";
                    for (int i2 = 1; i2 < this.mCCReferences.size(); i2++) {
                        str5 = str5 + "<br/>" + this.mCCReferences.get(i2).first + " &lt;<a href='mailto:" + this.mCCReferences.get(i2).second + "'>" + this.mCCReferences.get(i2).second + "</a>&gt;";
                    }
                }
                String str6 = "--------Forwarded message--------<br/>From: " + this.mLastMessageSender.first + " &lt;<a href='mailto:" + this.mLastMessageSender.second + "'>" + this.mLastMessageSender.second + "</a>&gt;<br/>Date: " + format + " at " + lowerCase + "<br/>Subject: " + this.subjectView.getOriginalText() + "<br/>To: " + str3;
                if (!str5.equals("")) {
                    str6 = str6 + "<br/>Cc: " + str5;
                }
                str2 = str6;
            } else if (!this.mIsEditDraft && !this.mIsSendAgain && !this.mIsEditOutbox) {
                str2 = "On " + format + " at " + lowerCase + ", " + this.mLastMessageSender.first + " &lt;<a href='mailto:" + this.mLastMessageSender.second + "'>" + this.mLastMessageSender.second + "</a>&gt; wrote:";
            }
        }
        String str7 = this.mQuotedText;
        if (!this.mIsQuotedTextHTML) {
            str7 = Utilities.escapeHtmlString(str7);
        }
        ArrayList arrayList = new ArrayList();
        if (this.mHasAttachments) {
            for (int i3 = 0; i3 < this.mAttachment.size(); i3++) {
                if (this.mAttachment.get(i3).isInlineAttachment()) {
                    arrayList.add(this.mAttachment.get(i3));
                }
            }
        }
        this.webView.loadDataWithBaseURL("file:///android_asset/", Utilities.getEditableHtmlBody(this.mContext, "" + str7, str2, this.mActionType, arrayList), this.mime, this.encoding, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GetEmailAccountsFromDBAsyncTask extends AsyncTask<Void, Void, List<UserAccount>> {
        private GetEmailAccountsFromDBAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public List<UserAccount> doInBackground(Void... voidArr) {
            if (ComposeViewFragment.this.getActivity() == null) {
                return null;
            }
            CMDBWrapper cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
            ArrayList arrayList = new ArrayList();
            List<UserAccount> accountList = cMDBWrapper.getAccountList("message");
            if (accountList != null) {
                for (UserAccount userAccount : accountList) {
                    arrayList.add(userAccount);
                    List<Alias> validatedAccountAliases = cMDBWrapper.getValidatedAccountAliases(userAccount.accountId);
                    if (validatedAccountAliases != null) {
                        for (Alias alias : validatedAccountAliases) {
                            if (alias.errorCode == 0 && alias.statusCode == 0) {
                                arrayList.add(new UserAccount(ComposeViewFragment.this.mContext, alias, userAccount));
                            }
                        }
                    }
                }
            }
            cMDBWrapper.close();
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(List<UserAccount> list) {
            if (list == null || ComposeViewFragment.this.getActivity() == null) {
                return;
            }
            ComposeViewFragment.this.updateFromAddressSpinner(list);
        }
    }

    /* loaded from: classes.dex */
    private class GetAccountNameFromDBAsyncTask extends AsyncTask<String, Void, String> {
        private GetAccountNameFromDBAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public String doInBackground(String... strArr) {
            if (ComposeViewFragment.this.getActivity() == null || ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).isTreatedAsAlias) {
                return null;
            }
            String str = ((UserAccount) ComposeViewFragment.this.fromFieldSpinner.getSelectedItem()).accountName;
            CMDBWrapper cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
            String contactAccount = cMDBWrapper.getContactAccount(strArr[0], str);
            cMDBWrapper.close();
            return contactAccount;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(String str) {
            List<UserAccount> userAccounts;
            if (TextUtils.isEmpty(str) || ComposeViewFragment.this.getActivity() == null || (userAccounts = ((ComposeViewFromAddressAdapter) ComposeViewFragment.this.fromFieldSpinner.getAdapter()).getUserAccounts()) == null || userAccounts.isEmpty()) {
                return;
            }
            for (int i = 0; i < userAccounts.size(); i++) {
                UserAccount userAccount = userAccounts.get(i);
                if (str.equals(userAccount.accountName) && !userAccount.isTreatedAsAlias) {
                    ComposeViewFragment.this.fromFieldSelection = FromFieldSelection.AUTO;
                    ComposeViewFragment.this.fromFieldSpinner.setSelection(i);
                    return;
                }
            }
        }
    }

    public void showNoAccountConfigurationDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SpannableString spannableStringBold = Utilities.getSpannableStringBold(this.mContext, "");
        SpannableString spannableString = Utilities.getSpannableString(this.mContext, str);
        SpannableString spannableStringBold2 = Utilities.getSpannableStringBold(this.mContext, this.mContext.getResources().getString(R.string.cancel));
        builder.setTitle(spannableStringBold);
        builder.setPositiveButton(spannableStringBold2, new DialogInterface.OnClickListener() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.40
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ComposeViewFragment.this.getActivity().finish();
            }
        });
        builder.setCancelable(false);
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R.layout.dialog_message, (ViewGroup) null);
        ((CustomTextView) inflate.findViewById(android.R.id.message)).setText(spannableString);
        builder.setView(inflate);
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00b8, code lost:
    
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00e2, code lost:
    
        if (r0 != null) goto L43;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateFromAddressSpinner(java.util.List<com.cloudmagic.android.data.entities.UserAccount> r7) {
        /*
            Method dump skipped, instructions count: 472
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cloudmagic.android.fragments.ComposeViewFragment.updateFromAddressSpinner(java.util.List):void");
    }

    public static UserAccount getAliasDefaultFromAddressFromList(String str, int i, List<UserAccount> list) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        for (UserAccount userAccount : list) {
            if (str.equals(userAccount.accountName) && userAccount.accountId == i) {
                return userAccount;
            }
        }
        return null;
    }

    private void setExternalContactFromRenderedRecipients() {
        if (this.mExternalContact == null) {
            this.mExternalContact = getExternalContactFromRenderedRecipients(this.toAddressTextView);
        }
        if (this.mExternalContact == null) {
            this.mExternalContact = getExternalContactFromRenderedRecipients(this.bccAddressTextView);
        }
        if (this.mExternalContact == null) {
            this.mExternalContact = getExternalContactFromRenderedRecipients(this.ccAddressTextView);
        }
    }

    private String getExternalContactFromRenderedRecipients(RecipientEditTextView recipientEditTextView) {
        List<RecipientChip> chipsForTextView = getChipsForTextView(recipientEditTextView);
        if (chipsForTextView == null) {
            return null;
        }
        Iterator<RecipientChip> it = chipsForTextView.iterator();
        while (it.hasNext()) {
            String address = it.next().getEntry().getAddress();
            UserAccount userAccount = (UserAccount) this.fromFieldSpinner.getSelectedItem();
            if (userAccount != null && !Utilities.isContactIsInDomain(address, userAccount.accountName)) {
                return address;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAllAttachments(ArrayList<AttachmentView> arrayList) {
        LinearLayout linearLayout;
        if (getActivity() == null) {
            return;
        }
        int integer = getResources().getInteger(R.integer.message_attachment_max_columns);
        if (this.isTablet && !this.isTablet10 && this.isLandscape) {
            integer++;
        }
        int integer2 = getResources().getInteger(R.integer.message_attachments_spacing);
        int i = integer - 1;
        int dimension = ((((ComposeActivity) getActivity()).getWindowSize().x - (((((int) getResources().getDimension(R.dimen.message_attachment_left_margin)) + ((int) getResources().getDimension(R.dimen.message_attachment_right_margin))) + ((int) getResources().getDimension(R.dimen.compose_screen_side_padding))) + ((int) getResources().getDimension(R.dimen.compose_screen_side_padding)))) - (integer2 * i)) / integer;
        Iterator<AttachmentView> it = arrayList.iterator();
        loop0: while (true) {
            linearLayout = null;
            while (it.hasNext()) {
                AttachmentView next = it.next();
                ((LinearLayout) next.getParent()).removeView(next);
                if (linearLayout == null) {
                    linearLayout = new LinearLayout(this.mContext);
                }
                if (linearLayout.getChildCount() < integer) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                    if (linearLayout.getChildCount() < i) {
                        layoutParams.rightMargin = integer2;
                    }
                    next.resizeView(dimension);
                    next.setLayoutParams(layoutParams);
                    linearLayout.addView(next);
                }
                if (linearLayout.getChildCount() == integer) {
                    break;
                }
            }
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
            layoutParams2.bottomMargin = integer2;
            linearLayout.setLayoutParams(layoutParams2);
            this.attachmentTable.addView(linearLayout);
        }
        if (linearLayout != null) {
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
            layoutParams3.bottomMargin = integer2;
            linearLayout.setLayoutParams(layoutParams3);
            this.attachmentTable.addView(linearLayout);
        }
    }

    private void addAllAttachments() {
        LinearLayout linearLayout;
        int i;
        boolean z;
        Iterator<Attachment> it;
        if (getActivity() == null || !this.mHasAttachments || this.mAttachment == null) {
            return;
        }
        try {
            this.attachmentLayout.setVisibility(0);
            int integer = getResources().getInteger(R.integer.message_attachment_max_columns);
            if (this.isTablet && !this.isTablet10 && this.isLandscape) {
                integer++;
            }
            int integer2 = getResources().getInteger(R.integer.message_attachments_spacing);
            int i2 = integer - 1;
            int dimension = ((((ComposeActivity) getActivity()).getWindowSize().x - (((((int) getResources().getDimension(R.dimen.message_attachment_left_margin)) + ((int) getResources().getDimension(R.dimen.message_attachment_right_margin))) + ((int) getResources().getDimension(R.dimen.compose_screen_side_padding))) + ((int) getResources().getDimension(R.dimen.compose_screen_side_padding)))) - (integer2 * i2)) / integer;
            Iterator<Attachment> it2 = this.mAttachment.iterator();
            boolean z2 = true;
            boolean z3 = true;
            LinearLayout linearLayout2 = null;
            while (it2.hasNext()) {
                Attachment next = it2.next();
                if (!next.isInlineAttachment()) {
                    if (linearLayout2 == null) {
                        linearLayout2 = new LinearLayout(this.mContext);
                    }
                    LinearLayout linearLayout3 = linearLayout2;
                    if (linearLayout3.getChildCount() < integer) {
                        z = z2;
                        it = it2;
                        AttachmentView attachmentView = new AttachmentView(getActivity(), next, null, null, Boolean.valueOf(z2), false, false, dimension);
                        attachmentView.registerAttachmentViewListener(this);
                        i = -2;
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                        linearLayout = linearLayout3;
                        if (linearLayout.getChildCount() < i2) {
                            layoutParams.rightMargin = integer2;
                        }
                        attachmentView.setLayoutParams(layoutParams);
                        linearLayout.addView(attachmentView);
                    } else {
                        linearLayout = linearLayout3;
                        i = -2;
                        z = z2;
                        it = it2;
                    }
                    if (linearLayout.getChildCount() == integer) {
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, i);
                        layoutParams2.bottomMargin = integer2;
                        linearLayout.setLayoutParams(layoutParams2);
                        this.attachmentTable.addView(linearLayout);
                        linearLayout2 = null;
                    } else {
                        linearLayout2 = linearLayout;
                    }
                    z2 = z;
                    it2 = it;
                    z3 = false;
                }
            }
            if (linearLayout2 != null) {
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams3.bottomMargin = integer2;
                linearLayout2.setLayoutParams(layoutParams3);
                this.attachmentTable.addView(linearLayout2);
            }
            if (z3) {
                this.attachmentLayout.setVisibility(8);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GetContactSuggestionsAsyncTask extends AsyncTask<Integer, Void, List<Contact>> {
        private GetContactSuggestionsAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public List<Contact> doInBackground(Integer... numArr) {
            return ComposeViewFragment.this.composeHelper.getMostContactedContacts(numArr[0].intValue());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(List<Contact> list) {
            if (list == null || list.isEmpty()) {
                ComposeViewFragment.this.isFrequectContactsEmpty = true;
            } else {
                ComposeViewFragment.this.isFrequectContactsEmpty = false;
                ChipsAddressTextView focusedChipAddressTextView = ComposeViewFragment.this.getFocusedChipAddressTextView();
                if (focusedChipAddressTextView != null && ComposeViewFragment.this.isKeyboardVisible) {
                    ComposeViewFragment.this.showEmailSuggestionContainer(focusedChipAddressTextView, false);
                }
            }
            ((EmailListAdapter) ComposeViewFragment.this.emailSuggestionView.getAdapter()).updateList(list);
            ComposeViewFragment.this.refreshSuggestionList();
            ComposeViewFragment.this.emailSuggestionView.scrollTo(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GetImagesAsyncTask extends AsyncTask<Object, Void, List<GalleryImage>> {
        private GetImagesAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public List<GalleryImage> doInBackground(Object... objArr) {
            List<String> imageFilePaths;
            if (ComposeViewFragment.this.getActivity() == null || (imageFilePaths = ComposeViewFragment.this.getImageFilePaths()) == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            if (imageFilePaths != null) {
                Iterator<String> it = imageFilePaths.iterator();
                while (it.hasNext()) {
                    arrayList.add(new GalleryImage(it.next()));
                }
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(List<GalleryImage> list) {
            if (ComposeViewFragment.this.getActivity() == null) {
                return;
            }
            if (list != null || ActivityCompat.checkSelfPermission(ComposeViewFragment.this.getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != -1) {
                ComposeViewFragment.this.imageLst.clear();
                ArrayAdapter arrayAdapter = (ArrayAdapter) ComposeViewFragment.this.attachmentImagecontainer.getAdapter();
                if (list == null || list.isEmpty()) {
                    ComposeViewFragment.this.emptyImagesMsgContainer.setVisibility(0);
                    ((CustomTextView) ComposeViewFragment.this.emptyImagesMsgContainer.findViewById(R.id.empty_msg_text)).setText(ComposeViewFragment.this.getResources().getString(R.string.empty_image_list_message));
                    ComposeViewFragment.this.emptyImagesMsgContainer.findViewById(R.id.permission_button).setVisibility(8);
                    ComposeViewFragment.this.attachmentImagecontainer.setVisibility(8);
                } else {
                    ComposeViewFragment.this.imageLst.addAll(list);
                    ComposeViewFragment.this.emptyImagesMsgContainer.setVisibility(8);
                    ComposeViewFragment.this.attachmentImagecontainer.setVisibility(0);
                }
                arrayAdapter.notifyDataSetChanged();
                return;
            }
            ComposeViewFragment.this.emptyImagesMsgContainer.setVisibility(0);
            ComposeViewFragment.this.attachmentImagecontainer.setVisibility(8);
            ((CustomTextView) ComposeViewFragment.this.emptyImagesMsgContainer.findViewById(R.id.empty_msg_text)).setText(ComposeViewFragment.this.getResources().getString(R.string.read_permission_recent_files));
            ComposeViewFragment.this.emptyImagesMsgContainerButton.setVisibility(0);
        }
    }

    public List<String> getImageFilePaths() {
        try {
            Cursor query = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{CardListTable.DATA, "datetaken"}, null, null, "datetaken DESC");
            ArrayList arrayList = new ArrayList(query.getCount());
            if (query.moveToFirst()) {
                int columnIndexOrThrow = query.getColumnIndexOrThrow(CardListTable.DATA);
                do {
                    arrayList.add(query.getString(columnIndexOrThrow));
                } while (query.moveToNext());
            }
            query.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRecipientFromContactData(Uri uri) {
        Cursor query;
        String str = "";
        try {
            query = getActivity().getContentResolver().query(uri, null, null, null, null);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
        if (query == null) {
            return "";
        }
        while (true) {
            if (!query.moveToNext()) {
                break;
            }
            String string = query.getString(query.getColumnIndex("display_name"));
            String string2 = query.getString(query.getColumnIndex("data1"));
            if (!string2.equals("") && !string2.isEmpty()) {
                str = Utilities.getFormattedAddress(string, string2);
                break;
            }
        }
        query.close();
        return str;
    }

    public static boolean isContactPickerSupported(Context context) {
        return isIntentAvailable(ContactsContract.CommonDataKinds.Email.CONTENT_URI, context);
    }

    public static boolean isIntentAvailable(Uri uri, Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setData(uri);
        return packageManager.queryIntentActivities(intent, 0).size() > 0;
    }

    /* loaded from: classes.dex */
    private class ContactPickerListener implements View.OnClickListener {
        private ContactPickerListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view.getId() == R.id.menuButtonContainer) {
                if (Build.VERSION.SDK_INT < 21) {
                    ComposeViewFragment.this.mToRecipientAddressMenuPopup.showOnTopOf(view, -40);
                    return;
                } else {
                    ComposeViewFragment.this.mToRecipientAddressMenuPopup.showOnTopOf(view, view.getHeight() / 2);
                    return;
                }
            }
            if (view.getId() == R.id.ccAddressContactPickerContainer) {
                if (Build.VERSION.SDK_INT < 21) {
                    ComposeViewFragment.this.mCcRecipientAddressMenuPopup.showOnTopOf(view, -40);
                    return;
                } else {
                    ComposeViewFragment.this.mCcRecipientAddressMenuPopup.showOnTopOf(view, view.getHeight() / 2);
                    return;
                }
            }
            if (view.getId() == R.id.bccAddressContactPickerContainer) {
                if (Build.VERSION.SDK_INT < 21) {
                    ComposeViewFragment.this.mBccRecipientAddressMenuPopup.showOnTopOf(view, -40);
                } else {
                    ComposeViewFragment.this.mBccRecipientAddressMenuPopup.showOnTopOf(view, view.getHeight() / 2);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class AttachmentClickListener implements View.OnClickListener {
        private AttachmentClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ComposeViewFragment.this.toggleAttachmentComponent();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void grantReadStoragePermission(String str, Bundle bundle) {
        ((BaseActivity) getActivity()).setPermissionGrantedCallback(this);
        ((BaseActivity) getActivity()).grantPermission(1, getView(), "android.permission.READ_EXTERNAL_STORAGE", str, getResources().getString(R.string.read_permission_common), bundle);
    }

    public boolean isAttachmentComponentVisible() {
        return this.composeAttachmentLayout.getVisibility() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleAttachmentComponent() {
        this.mShowAttachmentWhileScroll = false;
        if (isAttachmentComponentVisible()) {
            this.mCameraFab.animate().alpha(0.0f).setDuration(300L);
            this.mRecentPicturesContainer.animate().alpha(0.0f).setDuration(300L);
            this.composeAttachmentLayout.animate().alpha(0.0f).setDuration(300L);
            this.mFileMgrImageContainer.animate().alpha(0.0f).setDuration(300L);
            this.layoutRoot.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.41
                @Override // java.lang.Runnable
                public void run() {
                    if (ComposeViewFragment.this.getActivity() == null) {
                        return;
                    }
                    ComposeViewFragment.this.attachmentPickerContainer.setVisibility(0);
                    ComposeViewFragment.this.mRecentPicturesContainer.setVisibility(8);
                    ComposeViewFragment.this.mFileMgrImageContainer.setVisibility(8);
                    ComposeViewFragment.this.mCameraFab.setVisibility(8);
                    ComposeViewFragment.this.composeAttachmentLayout.setVisibility(8);
                    Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.getFocusedView());
                }
            }, 300L);
            return;
        }
        showAttachmentComponent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideComposeAttachmentLayout() {
        this.attachmentPickerContainer.setVisibility(0);
        this.mRecentPicturesContainer.setVisibility(8);
        this.mFileMgrImageContainer.setVisibility(8);
        this.mCameraFab.setVisibility(8);
        this.composeAttachmentLayout.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideExtraComponent() {
        if (this.mShowAttachmentWhileScroll) {
            this.mShowAttachmentWhileScroll = false;
            return;
        }
        if (this.composeAttachmentLayout.getVisibility() == 0) {
            this.mShowAttachmentWhileScroll = false;
            hideComposeAttachmentLayout();
        }
        if (this.composeExtrasContainer.getVisibility() == 0) {
            hideSendLaterFragment();
        }
    }

    private void showAttachmentComponent() {
        boolean z;
        Utilities.hideSoftKeyboard(getActivity());
        if (this.composeExtrasContainer.getVisibility() == 0) {
            hideSendLaterFragment();
            z = false;
        } else {
            z = true;
        }
        if (this.keyBoardHeight > 180) {
            this.composeAttachmentLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, this.keyBoardHeight));
            ((ImageListAdapter) this.attachmentImagecontainer.getAdapter()).setWidth(this.keyBoardHeight);
            ((ImageListAdapter) this.attachmentImagecontainer.getAdapter()).notifyDataSetChanged();
        }
        if (z) {
            this.composeAttachmentLayout.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.42
                @Override // java.lang.Runnable
                public void run() {
                    ComposeViewFragment.this.hideEmailSuggestionContainer();
                    ComposeViewFragment.this.showAttachmentComponentAnimated();
                }
            }, 300L);
        } else {
            hideEmailSuggestionContainer();
            showAttachmentComponentAnimated();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollToBottom() {
        this.scrollView.post(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.43
            @Override // java.lang.Runnable
            public void run() {
                ComposeViewFragment.this.mShowAttachmentWhileScroll = true;
                ComposeViewFragment.this.scrollView.fullScroll(130);
                ComposeViewFragment.this.scrollView.post(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.43.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ComposeViewFragment.this.mShowAttachmentWhileScroll = false;
                        ComposeViewFragment.this.composeAttachmentLayout.requestFocus();
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAttachmentComponentAnimated() {
        this.attachmentPickerContainer.setVisibility(8);
        this.recentPicsButton.setSelected(true);
        this.composeAttachmentLayout.setVisibility(0);
        this.composeAttachmentLayout.setAlpha(0.0f);
        this.composeAttachmentLayout.animate().alpha(1.0f).setDuration(300L);
        this.mFileMgrImageContainer.setVisibility(0);
        this.mFileMgrImageContainer.setAlpha(0.0f);
        this.mFileMgrImageContainer.animate().alpha(1.0f).setDuration(300L);
        this.mRecentPicturesContainer.setVisibility(0);
        this.mRecentPicturesContainer.setAlpha(0.0f);
        this.mRecentPicturesContainer.animate().alpha(1.0f).setDuration(300L);
        this.mCameraFab.setVisibility(0);
        this.mCameraFab.setAlpha(0.0f);
        this.mCameraFab.animate().alpha(1.0f).setDuration(300L);
        this.composeAttachmentLayout.requestFocus();
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        Folder folder;
        super.onActivityResult(i, i2, intent);
        if (i == 1111) {
            if (intent != null) {
                Calendar calendar = (Calendar) intent.getExtras().getSerializable(SnoozeTimeChooserBaseFragment.TIME_SET);
                if (i2 == 2222) {
                    SimpleDialogFragment.newInstance(getString(R.string.snooze_time_past_title), getString(R.string.snooze_time_past_content)).show(getFragmentManager(), SimpleDialogFragment.TAG);
                } else {
                    setRemindLaterTime(calendar);
                }
            } else if (i2 == -23314) {
                this.isSnoozeTimePickerActivityActive = false;
            }
            this.isSnoozeTimePickerActivityActive = false;
            return;
        }
        if (i == 4444) {
            if (intent == null) {
                if (i2 == -23314) {
                    this.isSnoozeTimePickerActivityActive = false;
                    return;
                }
                return;
            }
            Calendar calendar2 = (Calendar) intent.getExtras().getSerializable(SnoozeTimeChooserBaseFragment.TIME_SET);
            boolean z = intent.getExtras().getBoolean(SnoozeTimeChooserBaseFragment.IS_AUTO_CANCELABLE);
            if (i2 == 2222) {
                SimpleDialogFragment.newInstance(getString(R.string.snooze_time_past_title), getString(R.string.snooze_time_past_content)).show(getFragmentManager(), SimpleDialogFragment.TAG);
                return;
            }
            if (calendar2 != null) {
                long timeInMillis = calendar2.getTimeInMillis();
                if (this.mSnoozeTime > 0 && timeInMillis >= this.mSnoozeTime) {
                    this.mSnoozeTime = 0L;
                    SimpleDialogFragment.newInstance(getString(R.string.heads_up), getString(R.string.send_time_more_than_snooze)).show(getFragmentManager(), SimpleDialogFragment.TAG);
                }
                setSendLaterTime(timeInMillis, z);
                return;
            }
            return;
        }
        switch (i) {
            case 1:
                if (i2 == -1) {
                    String recipientFromContactData = getRecipientFromContactData(intent.getData());
                    if (recipientFromContactData.length() == 0) {
                        return;
                    }
                    this.toAddressTextView.setChipFromText(recipientFromContactData);
                    ((EmailListAdapter) this.emailSuggestionView.getAdapter()).removeFromList(convertToContactWrapperLst(this.toAddressTextView.getChips()));
                    return;
                }
                return;
            case 2:
                if (i2 == -1) {
                    String recipientFromContactData2 = getRecipientFromContactData(intent.getData());
                    if (recipientFromContactData2.length() == 0) {
                        return;
                    }
                    this.bccAddressTextView.setChipFromText(recipientFromContactData2);
                    ((EmailListAdapter) this.emailSuggestionView.getAdapter()).removeFromList(convertToContactWrapperLst(this.bccAddressTextView.getChips()));
                    return;
                }
                return;
            case 3:
                if (i2 == -1) {
                    String recipientFromContactData3 = getRecipientFromContactData(intent.getData());
                    if (recipientFromContactData3.length() == 0) {
                        return;
                    }
                    this.ccAddressTextView.setChipFromText(recipientFromContactData3);
                    ((EmailListAdapter) this.emailSuggestionView.getAdapter()).removeFromList(convertToContactWrapperLst(this.ccAddressTextView.getChips()));
                    return;
                }
                return;
            case 4:
                if (i2 == -1) {
                    doOnAttachmentSelected(intent.getData());
                    return;
                }
                return;
            case 5:
                if (i2 == 1 && (folder = (Folder) intent.getExtras().getParcelable("target_folder")) != null) {
                    UserAccount.setSentMailDestinationFolder(this.mContext, folder.accountId, folder);
                    new UpdateFolderSyncTask(this.mContext, folder).execute(new Void[0]);
                }
                send(this.messageToSend);
                return;
            case 6:
                if (i2 == -1) {
                    doOnAttachmentSelected(this.attachmentTypesListener.getImageFileUri());
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* loaded from: classes.dex */
    private class UpdateFolderSyncTask extends AsyncTask<Void, Void, Void> {
        Context mContext;
        Folder mFolder;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(Void r1) {
        }

        public UpdateFolderSyncTask(Context context, Folder folder) {
            this.mContext = context;
            this.mFolder = folder;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public Void doInBackground(Void... voidArr) {
            if (this.mContext == null || this.mFolder == null || this.mFolder.isSyncable) {
                return null;
            }
            this.mFolder.isSyncable = true;
            FolderSyncSettingsPreferenceFragment.updateFolderSyncSettings(this.mContext, this.mFolder);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AttachmentView addAttachmentView(File file, String str, boolean z) {
        boolean z2 = file == null;
        if (this.attachmentLayout == null || this.fromFieldSpinner.getChildCount() == 0) {
            Attachment attachment = new Attachment(file, str);
            if (this.mAttachment == null) {
                this.mAttachment = new ArrayList<>();
                if (file != null) {
                    this.mAttachment.add(attachment);
                }
            } else if (file != null) {
                this.mAttachment.add(attachment);
            }
            this.mHasAttachments = true;
            return null;
        }
        if (this.attachmentLayout.getVisibility() == 8) {
            this.attachmentLayout.setVisibility(0);
        }
        Attachment attachment2 = new Attachment(file, str);
        if (this.mAttachment == null) {
            this.mAttachment = new ArrayList<>();
            if (file != null) {
                this.mAttachment.add(attachment2);
            }
        } else if (file != null) {
            this.mAttachment.add(attachment2);
        }
        this.mHasAttachments = true;
        if (getActivity() == null) {
            return null;
        }
        int integer = getResources().getInteger(R.integer.message_attachment_max_columns);
        if (this.isTablet && !this.isTablet10 && this.isLandscape) {
            integer++;
        }
        int integer2 = getResources().getInteger(R.integer.message_attachments_spacing);
        int i = integer - 1;
        int dimension = ((((ComposeActivity) getActivity()).getWindowSize().x - (((((int) getResources().getDimension(R.dimen.message_attachment_left_margin)) + ((int) getResources().getDimension(R.dimen.message_attachment_right_margin))) + ((int) getResources().getDimension(R.dimen.compose_screen_side_padding))) + ((int) getResources().getDimension(R.dimen.compose_screen_side_padding)))) - (integer2 * i)) / integer;
        if (this.attachmentTable.getChildCount() == 0 || ((LinearLayout) this.attachmentTable.getChildAt(this.attachmentTable.getChildCount() - 1)).getChildCount() == integer) {
            LinearLayout linearLayout = new LinearLayout(this.mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
            layoutParams.bottomMargin = integer2;
            linearLayout.setLayoutParams(layoutParams);
            this.attachmentTable.addView(linearLayout);
        }
        LinearLayout linearLayout2 = (LinearLayout) this.attachmentTable.getChildAt(this.attachmentTable.getChildCount() - 1);
        final AttachmentView attachmentView = new AttachmentView(getActivity(), attachment2, null, null, true, z2, true, dimension);
        attachmentView.registerAttachmentViewListener(this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        if (linearLayout2.getChildCount() < i) {
            layoutParams2.rightMargin = integer2;
        }
        attachmentView.setLayoutParams(layoutParams2);
        linearLayout2.addView(attachmentView);
        Rect rect = new Rect();
        rect.set(this.scrollView.getLeft(), this.scrollView.getTop(), this.scrollView.getRight(), this.scrollView.getBottom());
        if (linearLayout2.getLocalVisibleRect(rect)) {
            final Animation loadAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
            loadAnimation.setAnimationListener(new GrowAnimationListener(attachmentView));
            attachmentView.setVisibility(4);
            attachmentView.postDelayed(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.44
                @Override // java.lang.Runnable
                public void run() {
                    attachmentView.startAnimation(loadAnimation);
                }
            }, 200L);
        }
        if (z && this.scrollView != null) {
            scrollToBottom();
        }
        return attachmentView;
    }

    @Override // com.cloudmagic.android.view.AttachmentView.AttachmentViewListener
    public void onAttachmentViewRemoved(Attachment attachment) {
        this.mAttachment.remove(attachment);
        this.mIsMessageDirty = true;
        ImageListAdapter imageListAdapter = (ImageListAdapter) this.attachmentImagecontainer.getAdapter();
        if (attachment.localStoragePath != null) {
            Uri parse = Uri.parse(attachment.localStoragePath);
            if (parse.getPath() != null) {
                imageListAdapter.removeSelectedItem(parse.getPath());
                refreshVisibleListViewItems();
            }
        }
        ArrayList arrayList = new ArrayList();
        Iterator<Attachment> it = this.mAttachment.iterator();
        while (it.hasNext()) {
            Attachment next = it.next();
            if (!next.isInlineAttachment()) {
                arrayList.add(next);
            }
        }
        if (arrayList.size() == 0) {
            this.attachmentLayout.setVisibility(8);
        }
        ArrayList<AttachmentView> attachmentViews = getAttachmentViews(attachment);
        this.attachmentTable.removeAllViews();
        updateAllAttachments(attachmentViews);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<AttachmentView> getAttachmentViews(Attachment attachment) {
        ArrayList<AttachmentView> arrayList = new ArrayList<>();
        for (int i = 0; i < this.attachmentTable.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) this.attachmentTable.getChildAt(i);
            if (linearLayout != null) {
                for (int i2 = 0; i2 < linearLayout.getChildCount(); i2++) {
                    AttachmentView attachmentView = (AttachmentView) linearLayout.getChildAt(i2);
                    if (attachmentView.getAttachment() != attachment) {
                        arrayList.add(attachmentView);
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AttachmentView getAttachmentView(Attachment attachment) {
        for (int i = 0; i < this.attachmentTable.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) this.attachmentTable.getChildAt(i);
            if (linearLayout != null) {
                for (int i2 = 0; i2 < linearLayout.getChildCount(); i2++) {
                    AttachmentView attachmentView = (AttachmentView) linearLayout.getChildAt(i2);
                    if (attachmentView.getAttachment().localStoragePath.equalsIgnoreCase(attachment.localStoragePath)) {
                        return attachmentView;
                    }
                }
            }
        }
        return null;
    }

    public AttachmentFileInfo getFileInfo(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (FirebaseAnalytics.Param.CONTENT.equalsIgnoreCase(uri.getScheme())) {
            if (uri.toString().startsWith("content://com.android.contacts") || uri.toString().startsWith("content://com.microsoft.skydrive.content") || uri.toString().startsWith("content://com.microsoft.office.officehub.emailprovider")) {
                try {
                    Cursor query = this.mContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
                    if (query != null) {
                        query.moveToFirst();
                        int columnIndex = query.getColumnIndex("_display_name");
                        if (columnIndex != -1) {
                            return new AttachmentFileInfo(query.getString(columnIndex), uri, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if ((e instanceof SecurityException) && Build.VERSION.SDK_INT > 22) {
                        return new AttachmentFileInfo(true);
                    }
                }
            } else {
                try {
                    Cursor query2 = this.mContext.getContentResolver().query(uri, new String[]{CardListTable.DATA, "_display_name"}, null, null, null);
                    if (uri.toString().startsWith("content://com.android.gallery3d.provider")) {
                        uri = Uri.parse(uri.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"));
                    }
                    if (query2 != null) {
                        query2.moveToFirst();
                        int columnIndex2 = query2.getColumnIndex(CardListTable.DATA);
                        if (columnIndex2 == -1) {
                            return new AttachmentFileInfo(query2.getString(query2.getColumnIndex("_display_name")), uri, true);
                        }
                        String string = query2.getString(columnIndex2);
                        if (string != null && !string.equals("")) {
                            if (!string.startsWith(Constants.HTTP) && !string.startsWith(Constants.HTTPS)) {
                                if (string.startsWith("file://")) {
                                    String replace = string.replace("file://", "");
                                    AttachmentFileInfo fileInfoIfHasReadPermission = getFileInfoIfHasReadPermission(replace);
                                    return fileInfoIfHasReadPermission == null ? new AttachmentFileInfo(replace, uri, false) : fileInfoIfHasReadPermission;
                                }
                                int columnIndex3 = query2.getColumnIndex("_display_name");
                                if (columnIndex3 != -1 && query2.getString(columnIndex3) != null) {
                                    return new AttachmentFileInfo(query2.getString(columnIndex3), uri, true);
                                }
                                AttachmentFileInfo fileInfoIfHasReadPermission2 = getFileInfoIfHasReadPermission(string);
                                return fileInfoIfHasReadPermission2 == null ? new AttachmentFileInfo(string, uri, false) : fileInfoIfHasReadPermission2;
                            }
                            int columnIndex4 = query2.getColumnIndex("_display_name");
                            if (columnIndex4 != -1 && query2.getString(columnIndex4) != null) {
                                return new AttachmentFileInfo(query2.getString(columnIndex4), uri, true);
                            }
                            AttachmentFileInfo fileInfoIfHasReadPermission3 = getFileInfoIfHasReadPermission(string);
                            return fileInfoIfHasReadPermission3 == null ? new AttachmentFileInfo(string, uri, false) : fileInfoIfHasReadPermission3;
                        }
                        int columnIndex5 = query2.getColumnIndex("_display_name");
                        if (columnIndex5 != -1 && query2.getString(columnIndex5) != null) {
                            return new AttachmentFileInfo(query2.getString(columnIndex5), uri, true);
                        }
                        AttachmentFileInfo fileInfoIfHasReadPermission4 = getFileInfoIfHasReadPermission(uri.getPath());
                        return fileInfoIfHasReadPermission4 == null ? new AttachmentFileInfo(uri.getPath(), uri, false) : fileInfoIfHasReadPermission4;
                    }
                    if (uri != null && uri.toString().length() > 0) {
                        return new AttachmentFileInfo(uri.getLastPathSegment(), uri, true);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    if ((e2 instanceof SecurityException) && Build.VERSION.SDK_INT > 22) {
                        return new AttachmentFileInfo(true);
                    }
                    try {
                        Cursor query3 = this.mContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
                        if (query3 != null) {
                            query3.moveToFirst();
                            int columnIndex6 = query3.getColumnIndex("_display_name");
                            if (columnIndex6 != -1) {
                                return new AttachmentFileInfo(query3.getString(columnIndex6), uri, true);
                            }
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        if ((e3 instanceof SecurityException) && Build.VERSION.SDK_INT > 22) {
                            return new AttachmentFileInfo(true);
                        }
                    }
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            AttachmentFileInfo fileInfoIfHasReadPermission5 = getFileInfoIfHasReadPermission(uri.getPath());
            return fileInfoIfHasReadPermission5 == null ? new AttachmentFileInfo(uri.getPath(), uri, false) : fileInfoIfHasReadPermission5;
        }
        return null;
    }

    private AttachmentFileInfo getFileInfoIfHasReadPermission(String str) {
        try {
            if (Build.VERSION.SDK_INT <= 22 || new File(str).canRead()) {
                return null;
            }
            return new AttachmentFileInfo(true);
        } catch (Exception unused) {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public class AttachmentFileInfo {
        public String filePath;
        public boolean isExternalFile;
        public boolean requireReadPermission;
        public Uri uri;

        public AttachmentFileInfo(boolean z) {
            this.requireReadPermission = false;
            this.requireReadPermission = z;
        }

        public AttachmentFileInfo(String str, Uri uri, boolean z) {
            this.requireReadPermission = false;
            this.filePath = str;
            this.uri = uri;
            this.isExternalFile = z;
        }
    }

    public void cancelRunningTasks() {
        if (this.mService != null) {
            this.mService.cancelRunningTasks();
        }
    }

    public ViewConversation getReferenceConversation() {
        return this.mReferenceConversation;
    }

    public String getConversationServerId() {
        if (getArguments() != null) {
            return getArguments().getString("conversation_server_id");
        }
        return null;
    }

    public String getMessageResourceID() {
        if (getArguments() != null) {
            return getArguments().getString("message_resource_id");
        }
        return null;
    }

    public String getSubject() {
        if (getArguments() != null) {
            return getArguments().getString("subject");
        }
        return null;
    }

    private void showError(int i, String str) {
        ((BaseActivity) getActivity()).showErrorDialog(i, str, this);
    }

    /* loaded from: classes.dex */
    private class ProcessMessageResponseTask extends AsyncTask<Void, Void, List<Alias>> {
        private ViewConversation mConversation;
        private Message mMessage;

        public ProcessMessageResponseTask(Message message, ViewConversation viewConversation) {
            this.mMessage = message;
            this.mConversation = viewConversation;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public List<Alias> doInBackground(Void... voidArr) {
            if (ComposeViewFragment.this.getActivity() == null) {
                return null;
            }
            CMDBWrapper cMDBWrapper = new CMDBWrapper(ComposeViewFragment.this.mContext);
            List<Alias> validatedAccountAliases = cMDBWrapper.getValidatedAccountAliases(this.mMessage.accountId);
            cMDBWrapper.close();
            return validatedAccountAliases;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(List<Alias> list) {
            if (ComposeViewFragment.this.getActivity() == null) {
                return;
            }
            ComposeViewFragment.this.initializeFromBundle(ComposePayloadUtils.getComposeBundle(ComposeViewFragment.this.mContext, this.mMessage, list, 5, false), ComposeViewFragment.this.getActivity().getIntent().getAction());
            ComposeViewFragment.this.mReferenceConversation = this.mConversation;
            ComposeViewFragment.this.webViewContainer.setVisibility(0);
            ComposeViewFragment.this.updateViews();
            ComposeViewFragment.this.rootContainer.setVisibility(0);
            ComposeViewFragment.this.setFocusToComposeBody();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFocusToComposeBody() {
        if (this.removeQuotedTextButton.getVisibility() == 8) {
            this.composeMail.requestFocus();
            Utilities.showKeyboard(getActivity(), this.composeMail);
        }
    }

    @Override // com.cloudmagic.android.ErrorInterface
    public void onError(APIError aPIError, boolean z) {
        hideDialog();
    }

    @Override // com.cloudmagic.android.dialogs.ErrorDialogFragment.OnErrorDialogActionListener
    public void goBack() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override // com.cloudmagic.android.dialogs.ErrorDialogFragment.OnErrorDialogActionListener
    public void onUpgradeFromErrorDialogFragment() {
        getActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doOnAttachmentSelected(Uri uri) {
        if (getActivity() == null) {
            return;
        }
        Utilities.hideSoftKeyboard(getActivity());
        setSoftInputStateVisiblilty(false);
        this.mIsMessageDirty = true;
        final AttachmentFileInfo fileInfo = getFileInfo(uri);
        if (fileInfo == null) {
            Utilities.showDialog(getActivity(), "", getResources().getString(R.string.compose_attachment_failed));
            return;
        }
        if (fileInfo.requireReadPermission && uri != null) {
            Bundle bundle = new Bundle();
            bundle.putString("image_uri", uri.toString());
            grantReadStoragePermission(getResources().getString(R.string.read_permission_common), bundle);
        } else if (!fileInfo.isExternalFile && fileInfo.filePath != null) {
            addAttachmentView(new File(fileInfo.filePath), this.mReferenceResourceId, true);
        } else {
            final AttachmentView addAttachmentView = addAttachmentView(null, this.mReferenceResourceId, true);
            new DownloadThread(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.45
                @Override // java.lang.Runnable
                public void run() {
                    if (ComposeViewFragment.this.getActivity() != null) {
                        final File file = Utilities.getFile(ComposeViewFragment.this.mContext, fileInfo.filePath, fileInfo.uri);
                        if (ComposeViewFragment.this.getActivity() == null) {
                            return;
                        }
                        ComposeViewFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.cloudmagic.android.fragments.ComposeViewFragment.45.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (file == null) {
                                    if (ComposeViewFragment.this.getActivity() != null) {
                                        Utilities.showDialog(ComposeViewFragment.this.getActivity(), "", ComposeViewFragment.this.getResources().getString(R.string.compose_attachment_failed));
                                    }
                                    if (addAttachmentView != null) {
                                        addAttachmentView.remove();
                                        return;
                                    }
                                    return;
                                }
                                if (addAttachmentView != null) {
                                    Attachment attachment = new Attachment(file, ComposeViewFragment.this.mReferenceResourceId);
                                    ComposeViewFragment.this.mAttachment.add(attachment);
                                    addAttachmentView.updateDetailsPreAttachment(attachment);
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DownloadThread extends Thread {
        private Attachment attachment;

        public DownloadThread(Runnable runnable) {
            super(runnable);
        }

        void setAttachment(Attachment attachment) {
            this.attachment = attachment;
        }

        Attachment getAttachment() {
            return this.attachment;
        }
    }

    /* loaded from: classes.dex */
    private class EmailSuggestionClickListener implements AdapterView.OnItemClickListener {
        private EmailSuggestionClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            EmailListAdapter emailListAdapter = (EmailListAdapter) ComposeViewFragment.this.emailSuggestionView.getAdapter();
            EmailListAdapter.ContactWrapper item = emailListAdapter.getItem(i);
            if (item != null) {
                EmailListAdapter.ContactWrapper contactWrapper = item;
                String formattedAddress = Utilities.getFormattedAddress(contactWrapper.getName(), contactWrapper.getEmailId());
                ChipsAddressTextView focusedChipAddressTextView = ComposeViewFragment.this.getFocusedChipAddressTextView();
                if (focusedChipAddressTextView != null) {
                    focusedChipAddressTextView.setChipFromText(formattedAddress);
                }
                emailListAdapter.removeFromList(contactWrapper);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ChipsAddressTextView getFocusedChipAddressTextView() {
        if (this.ccAddressTextView.isFocused()) {
            return this.ccAddressTextView;
        }
        if (this.bccAddressTextView.isFocused()) {
            return this.bccAddressTextView;
        }
        if (this.toAddressTextView.isFocused()) {
            return this.toAddressTextView;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getFocusedView() {
        if (this.ccAddressTextView.isFocused()) {
            return this.ccAddressTextView;
        }
        if (this.bccAddressTextView.isFocused()) {
            return this.bccAddressTextView;
        }
        if (this.toAddressTextView.isFocused()) {
            return this.toAddressTextView;
        }
        if (this.composeMail.isFocused()) {
            return this.composeMail;
        }
        if (this.subjectView.isFocused()) {
            return this.subjectView;
        }
        return null;
    }

    /* loaded from: classes.dex */
    private class AttachmentListViewClickListener implements AdapterView.OnItemClickListener {
        private AttachmentListViewClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            ImageListAdapter imageListAdapter = (ImageListAdapter) ComposeViewFragment.this.attachmentImagecontainer.getAdapter();
            GalleryImage item = imageListAdapter.getItem(i);
            if (item != null) {
                String filePath = item.getFilePath();
                if (imageListAdapter.isAlreadySelected(filePath)) {
                    AttachmentView attachmentView = ComposeViewFragment.this.getAttachmentView(new Attachment(new File(filePath), (String) null));
                    if (attachmentView == null) {
                        return;
                    }
                    Rect rect = new Rect();
                    rect.set(ComposeViewFragment.this.scrollView.getLeft(), ComposeViewFragment.this.scrollView.getTop(), ComposeViewFragment.this.scrollView.getRight(), ComposeViewFragment.this.scrollView.getBottom());
                    if (attachmentView.getLocalVisibleRect(rect)) {
                        attachmentView.removeAnimated();
                        return;
                    } else {
                        attachmentView.remove();
                        return;
                    }
                }
                imageListAdapter.addSelectedItem(filePath, view);
                ComposeViewFragment.this.doOnAttachmentSelected(Uri.fromFile(new File(filePath)));
                ComposeViewFragment.this.refreshVisibleListViewItems();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshVisibleListViewItems() {
        int firstVisiblePosition = this.attachmentImagecontainer.getFirstVisiblePosition();
        if (firstVisiblePosition >= 0) {
            int lastVisiblePosition = this.attachmentImagecontainer.getLastVisiblePosition();
            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                this.attachmentImagecontainer.getAdapter().getView(i, this.attachmentImagecontainer.getChildAt(i - firstVisiblePosition), this.attachmentImagecontainer);
            }
        }
    }

    /* loaded from: classes.dex */
    private class AttachmentTypesClickListener implements View.OnClickListener {
        private Uri unqFileUri;

        private AttachmentTypesClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.camera_fab) {
                ((BaseActivity) ComposeViewFragment.this.getActivity()).setPermissionGrantedCallback(ComposeViewFragment.this);
                ((BaseActivity) ComposeViewFragment.this.getActivity()).grantPermission(3, ComposeViewFragment.this.getView(), "android.permission.WRITE_EXTERNAL_STORAGE", ComposeViewFragment.this.getResources().getString(R.string.write_permission_common), ComposeViewFragment.this.getResources().getString(R.string.write_permission_common), null);
            } else if (id == R.id.file_manager_img_container) {
                onFileManagerContainerClick();
            } else {
                if (id != R.id.recent_pictures_image_containter) {
                    return;
                }
                ComposeViewFragment.this.composeMail.requestFocus();
                Utilities.showKeyboard(ComposeViewFragment.this.getActivity(), ComposeViewFragment.this.composeMail);
                ComposeViewFragment.this.hideComposeAttachmentLayout();
            }
        }

        private void onFileManagerContainerClick() {
            try {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                ComposeViewFragment.this.startActivityForResult(Intent.createChooser(intent, ComposeViewFragment.this.getResources().getString(R.string.compose_choose_attachment)), 4);
                ComposeViewFragment.this.mNewActivityStarted = true;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onCameraImageContanierClick() {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            try {
                this.unqFileUri = ComposeViewFragment.this.createUniqueFile();
                intent.putExtra("output", this.unqFileUri);
                intent.addFlags(2);
                if (Build.VERSION.SDK_INT <= 21) {
                    intent.setClipData(ClipData.newRawUri("", this.unqFileUri));
                }
                ComposeViewFragment.this.startActivityForResult(intent, 6);
                ComposeViewFragment.this.mNewActivityStarted = true;
            } catch (IOException e) {
                Log.e(ComposeViewFragment.TAG, "Error occurred while creating image file", e);
                Utilities.showCustomToast(ComposeViewFragment.this.mContext, ComposeViewFragment.this.mContext.getText(R.string.camera_init_error).toString(), 0, true);
            }
        }

        public Uri getImageFileUri() {
            return this.unqFileUri;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri createUniqueFile() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!externalStoragePublicDirectory.exists()) {
            Log.e(TAG, "Image folder does not exist");
            return null;
        }
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return FileProvider.getUriForFile(this.mContext, "com.cloudmagic.android.fileprovider", new File(externalStoragePublicDirectory.getPath(), "cm-img-" + format + ".jpg"));
    }

    private void executeFetchImagesAsyncTask() {
        new GetImagesAsyncTask().execute(new Object[0]);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        this.mNewActivityStarted = false;
        super.onResume();
        executeFetchImagesAsyncTask();
        if (isAttachmentComponentVisible() || ((ComposeActivity) getActivity()).isComposeExtrasFragmentVisible() || this.composeExtrasContainer.getVisibility() == 0) {
            setSoftInputStateVisiblilty(false);
        } else {
            setSoftInputStateVisiblilty(true);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mIsMailSent || this.isSnoozeTimePickerActivityActive || this.mSaveAsDraft || this.mMailSendingOrSavingIsInProcess || this.mNewActivityStarted) {
            return;
        }
        saveDraft(false);
    }

    @Override // com.cloudmagic.android.view.AttachmentView.AttachmentViewListener
    public void onAttachmentOpened() {
        this.mNewActivityStarted = true;
    }

    private Attachment getAttachmentByFileName(String str) {
        if (this.mAttachment != null) {
            Iterator<Attachment> it = this.mAttachment.iterator();
            while (it.hasNext()) {
                Attachment next = it.next();
                if (next.localStoragePath != null) {
                    Uri parse = Uri.parse(next.localStoragePath);
                    if (parse.getPath() != null && parse.getPath().equals(str)) {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVerticalOffsetForSpinner(ComposeSpinner composeSpinner) {
        if (getActivity() == null) {
            return;
        }
        if (!this.isTablet10) {
            int[] iArr = new int[2];
            getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getLocationOnScreen(iArr);
            int[] iArr2 = new int[2];
            composeSpinner.getLocationOnScreen(iArr2);
            if (Build.VERSION.SDK_INT >= 21) {
                composeSpinner.setDropDownVerticalOffset(iArr[1] - iArr2[1]);
                return;
            } else {
                if (Build.VERSION.SDK_INT >= 16) {
                    composeSpinner.setDropDownVerticalOffset((iArr[1] - iArr2[1]) - composeSpinner.getHeight());
                    return;
                }
                return;
            }
        }
        if (Build.VERSION.SDK_INT < 16 || Build.VERSION.SDK_INT >= 21) {
            return;
        }
        composeSpinner.setDropDownVerticalOffset((int) getResources().getDimension(R.dimen.compose_spinner_extra_vertical_offset));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHorizontalOffsetForSpinner(ComposeSpinner composeSpinner) {
        if (!this.isTablet10 || Build.VERSION.SDK_INT < 16) {
            return;
        }
        int width = this.scrollView.getWidth();
        int[] iArr = new int[2];
        this.scrollView.getLocationOnScreen(iArr);
        int[] iArr2 = new int[2];
        composeSpinner.getLocationOnScreen(iArr2);
        composeSpinner.setDropDownWidth(width - ((iArr2[0] - iArr[0]) * 2));
        composeSpinner.setDropDownHorizontalOffset(-(iArr2[0] - iArr[0]));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void foldTextViewLines(boolean z) {
        this.toAddressTextView.setFold(z);
        this.ccAddressTextView.setFold(z);
        this.bccAddressTextView.setFold(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeRecipientsHeaderColor(boolean z) {
        if (z) {
            this.toHeader.setHintTextColor(this.mContext.getResources().getColor(R.color.compose_hint_color));
            this.ccHeader.setHintTextColor(this.mContext.getResources().getColor(R.color.compose_hint_color));
            this.bccHeader.setHintTextColor(this.mContext.getResources().getColor(R.color.compose_hint_color));
        } else {
            this.toHeader.setHintTextColor(this.mContext.getResources().getColor(R.color.compose_value));
            this.ccHeader.setHintTextColor(this.mContext.getResources().getColor(R.color.compose_value));
            this.bccHeader.setHintTextColor(this.mContext.getResources().getColor(R.color.compose_value));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecipientChipFocusChangeListener implements View.OnFocusChangeListener {
        private static final String BCC_CHIPS = "BCC-CHIPS";
        private static final String CC_CHIPS = "CC-CHIPS";
        private static final String TO_CHIPS = "TO-CHIPS";

        public RecipientChipFocusChangeListener() {
        }

        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View view, boolean z) {
            ChipsAddressTextView focusedChipAddressTextView = ComposeViewFragment.this.getFocusedChipAddressTextView();
            if (z) {
                ComposeViewFragment.this.hideExtraComponent();
                showRecipientTxtViews();
                if (view instanceof RecipientEditTextView) {
                    ComposeViewFragment.this.refreshSuggestionList();
                }
                if (focusedChipAddressTextView != null) {
                    ComposeViewFragment.this.changeRecipientsHeaderColor(true);
                }
            } else {
                hideRecipientTxtViews(view);
                if (focusedChipAddressTextView == null) {
                    ComposeViewFragment.this.changeRecipientsHeaderColor(false);
                }
            }
            ComposeViewFragment.this.mOnKeyboardDown = false;
        }

        private void showRecipientTxtViews() {
            ChipsAddressTextView focusedChipAddressTextView = ComposeViewFragment.this.getFocusedChipAddressTextView();
            ComposeViewFragment.this.foldTextViewLines(false);
            if (focusedChipAddressTextView == ComposeViewFragment.this.toAddressTextView) {
                ComposeViewFragment.this.toHeader.setHint(ComposeViewFragment.this.mContext.getResources().getString(R.string.recipient_to_hint));
                List list = (List) ComposeViewFragment.this.recipientChipMap.get(CC_CHIPS);
                List list2 = (List) ComposeViewFragment.this.recipientChipMap.get(BCC_CHIPS);
                ComposeViewFragment.this.toAddressTextView.removeAllChips();
                ComposeViewFragment.this.toAddressTextView.addChips((List) ComposeViewFragment.this.recipientChipMap.get(TO_CHIPS));
                if (list != null && !list.isEmpty()) {
                    ComposeViewFragment.this.ccAddressContainer.setVisibility(0);
                    ComposeViewFragment.this.ccAddressTextView.removeAllChips();
                    ComposeViewFragment.this.ccAddressTextView.addChips((List) ComposeViewFragment.this.recipientChipMap.get(CC_CHIPS));
                }
                if (list2 != null && !list2.isEmpty()) {
                    ComposeViewFragment.this.bccAddressContainer.setVisibility(0);
                    ComposeViewFragment.this.bccAddressTextView.removeAllChips();
                    ComposeViewFragment.this.bccAddressTextView.addChips((List) ComposeViewFragment.this.recipientChipMap.get(BCC_CHIPS));
                }
                ComposeViewFragment.this.menuButtonContainerTo.setVisibility(0);
                return;
            }
            ComposeViewFragment.this.setValuesToRecipientMap();
        }

        private void hideRecipientTxtViews(View view) {
            if (ComposeViewFragment.this.getFocusedChipAddressTextView() == null) {
                ComposeViewFragment.this.toAddressTextView.setFold(true);
                ComposeViewFragment.this.ccAddressContainer.setVisibility(8);
                ComposeViewFragment.this.bccAddressContainer.setVisibility(8);
                ComposeViewFragment.this.setAllValuesToToRecipientTextView();
                if (view != ComposeViewFragment.this.toAddressTextView) {
                    ComposeViewFragment.this.toAddressTextView.createChips();
                }
                ComposeViewFragment.this.modifyToHeader();
                ComposeViewFragment.this.menuButtonContainerTo.setVisibility(8);
                return;
            }
            if (view instanceof ChipsAddressTextView) {
                ComposeViewFragment.this.setValuesToRecipientMap();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void modifyToHeader() {
        if ((this.recipientChipMap.get("CC-CHIPS") != null && !this.recipientChipMap.get("CC-CHIPS").isEmpty()) || (this.recipientChipMap.get("TO-CHIPS") != null && !this.recipientChipMap.get("TO-CHIPS").isEmpty())) {
            this.toHeader.setHint(this.mContext.getResources().getString(R.string.recipient_to_hint));
            return;
        }
        if (this.recipientChipMap.get("BCC-CHIPS") == null || this.recipientChipMap.get("BCC-CHIPS").isEmpty()) {
            return;
        }
        this.toHeader.setHint(this.mContext.getResources().getString(R.string.recipient_bcc_hint) + ":");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValuesToRecipientMap() {
        this.recipientChipMap.put("TO-CHIPS", this.toAddressTextView.getChips());
        this.recipientChipMap.put("CC-CHIPS", this.ccAddressTextView.getChips());
        this.recipientChipMap.put("BCC-CHIPS", this.bccAddressTextView.getChips());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAllValuesToToRecipientTextView() {
        setValuesToRecipientMap();
        this.toAddressTextView.removeAllChips();
        this.toAddressTextView.addChips(this.recipientChipMap.get("TO-CHIPS"));
        this.toAddressTextView.addChips(this.recipientChipMap.get("CC-CHIPS"));
        this.toAddressTextView.addChips(this.recipientChipMap.get("BCC-CHIPS"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GrowAnimationListener implements Animation.AnimationListener {
        private AttachmentView view;

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        public GrowAnimationListener(AttachmentView attachmentView) {
            this.view = attachmentView;
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
            this.view.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DraftsTextWatcher implements TextWatcher {
        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private DraftsTextWatcher() {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.toString().isEmpty() && (ComposeViewFragment.this.mAttachment == null || ComposeViewFragment.this.mAttachment.size() == 0)) {
                ComposeViewFragment.this.mIsBodyDirty = false;
                ComposeViewFragment.this.mIsMessageDirty = false;
            }
            if (ComposeViewFragment.this.mIsBodyDirty.booleanValue() || ComposeViewFragment.this.mIsMessageDirty.booleanValue()) {
                ComposeViewFragment.this.mIsMessageDirty = true;
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            ComposeViewFragment.this.mIsBodyDirty = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecipientAddedThroughDropDownListener implements RecipientEditTextView.OnRecipientEntryDropDownClick {
        private RecipientAddedThroughDropDownListener() {
        }

        @Override // com.cloudmagic.android.chips.RecipientEditTextView.OnRecipientEntryDropDownClick
        public void onRecipientDropDownEntryClick(RecipientEntry recipientEntry) {
            EmailListAdapter.ContactWrapper contactWrapper = new EmailListAdapter.ContactWrapper();
            contactWrapper.setName(recipientEntry.getDisplayName());
            contactWrapper.setEmailId(recipientEntry.getAddress());
            ((EmailListAdapter) ComposeViewFragment.this.emailSuggestionView.getAdapter()).removeFromList(contactWrapper);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecipientDeleteListener implements RecipientEditTextView.OnChipDeletedEventListener {
        private RecipientDeleteListener() {
        }

        @Override // com.cloudmagic.android.chips.RecipientEditTextView.OnChipDeletedEventListener
        public void onChipDeletion(RecipientChip recipientChip) {
            if (ComposeViewFragment.this.mExternalContact != null && ComposeViewFragment.this.mExternalContact.equals(recipientChip.getEntry().getAddress())) {
                ComposeViewFragment.this.hideExternalContactStripe();
                ComposeViewFragment.this.mExternalContact = null;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(new EmailListAdapter.ContactWrapper(recipientChip.getEntry().getDisplayName(), recipientChip.getEntry().getAddress()));
            ((EmailListAdapter) ComposeViewFragment.this.emailSuggestionView.getAdapter()).addContactsBack(arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BccViewEntryAddedListener implements RecipientEditTextView.OnEntryAddedListener {
        private BccViewEntryAddedListener() {
        }

        @Override // com.cloudmagic.android.chips.RecipientEditTextView.OnEntryAddedListener
        public void onEntryAdded(RecipientChip recipientChip, RecipientEditTextView recipientEditTextView) {
            if (ComposeViewFragment.this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) && getChipsCount() == 1 && ComposeViewFragment.this.fromFieldSelection != FromFieldSelection.CHANGED && ComposeViewFragment.this.fromFieldSpinner != null && ComposeViewFragment.this.fromFieldSpinner.getAdapter() != null && ((ComposeViewFromAddressAdapter) ComposeViewFragment.this.fromFieldSpinner.getAdapter()).getUserAccounts().size() > 1) {
                new GetAccountNameFromDBAsyncTask().execute(recipientChip.getEntry().getAddress());
            }
            if (ComposeViewFragment.this.customAddedBcc.contains(recipientChip.getEntry().getAddress())) {
                return;
            }
            ComposeViewFragment.this.customAddedBcc.add(recipientChip.getEntry().getAddress());
        }

        private int getChipsCount() {
            int size = ComposeViewFragment.this.toAddressTextView.getChips().size();
            int size2 = ComposeViewFragment.this.ccAddressTextView.getChips().size();
            List<RecipientChip> chips = ComposeViewFragment.this.bccAddressTextView.getChips();
            int size3 = chips.size();
            int i = size + size2;
            if (i > 0) {
                return i + size3;
            }
            if (ComposeViewFragment.this.mAutoBCC == null || ComposeViewFragment.this.mAutoBCC.length <= 0 || size3 <= 0) {
                return size3;
            }
            ArrayList arrayList = new ArrayList(chips.size());
            int i2 = 0;
            for (String str : ComposeViewFragment.this.mAutoBCC) {
                arrayList.add(Rfc822Tokenizer.tokenize(str)[0].getAddress());
            }
            Iterator<RecipientChip> it = chips.iterator();
            while (it.hasNext()) {
                if (!arrayList.contains(it.next().getEntry().getAddress())) {
                    i2++;
                }
            }
            return i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecipientTextVFirstEntryAddedListener implements RecipientEditTextView.OnFirstEntryAddedListener {
        private RecipientTextVFirstEntryAddedListener() {
        }

        @Override // com.cloudmagic.android.chips.RecipientEditTextView.OnFirstEntryAddedListener
        public void onFirstEntryAdded(RecipientChip recipientChip, RecipientEditTextView recipientEditTextView) {
            if (!ComposeViewFragment.this.mActionType.equals(ActionService.ACTION_TYPE_COMPOSE) || getChipsCount() != 1 || ComposeViewFragment.this.fromFieldSelection == FromFieldSelection.CHANGED || ComposeViewFragment.this.fromFieldSpinner == null || ComposeViewFragment.this.fromFieldSpinner.getAdapter() == null || ((ComposeViewFromAddressAdapter) ComposeViewFragment.this.fromFieldSpinner.getAdapter()).getUserAccounts().size() <= 1) {
                return;
            }
            new GetAccountNameFromDBAsyncTask().execute(recipientChip.getEntry().getAddress());
        }

        private int getChipsCount() {
            int size = ComposeViewFragment.this.toAddressTextView.getChips().size();
            int size2 = ComposeViewFragment.this.ccAddressTextView.getChips().size();
            List<RecipientChip> chips = ComposeViewFragment.this.bccAddressTextView.getChips();
            int size3 = chips.size();
            int i = size + size2;
            if (i > 1) {
                return i + size3;
            }
            if (ComposeViewFragment.this.mAutoBCC == null || ComposeViewFragment.this.mAutoBCC.length <= 0 || size3 <= 0) {
                return i + size3;
            }
            ArrayList arrayList = new ArrayList(chips.size());
            int i2 = 0;
            for (String str : ComposeViewFragment.this.mAutoBCC) {
                arrayList.add(Rfc822Tokenizer.tokenize(str)[0].getAddress());
            }
            Iterator<RecipientChip> it = chips.iterator();
            while (it.hasNext()) {
                if (!arrayList.contains(it.next().getEntry().getAddress())) {
                    i2++;
                }
            }
            return i + i2;
        }
    }

    private List<EmailListAdapter.ContactWrapper> convertToContactWrapperLst(List<RecipientChip> list) {
        ArrayList arrayList = new ArrayList();
        if (list != null && !list.isEmpty()) {
            for (RecipientChip recipientChip : list) {
                arrayList.add(new EmailListAdapter.ContactWrapper(recipientChip.getEntry().getDisplayName(), recipientChip.getEntry().getAddress()));
            }
        }
        return arrayList;
    }

    @Override // com.cloudmagic.android.observers.ConversationViewChangeObserver.ConversationViewChangeObserverInterface
    public void onConversationCreated(ViewConversation viewConversation, ViewConversation viewConversation2, String str, String str2) {
        if (viewConversation2 == null || this.messageToSend == null || this.mGoBackOnSaveDraft || viewConversation2.tsReceived != this.messageToSend.tsMessageLanding) {
            return;
        }
        this.mReferenceConversation = viewConversation2;
        this.mPreviousDraftId = str;
        this.mReferenceResourceId = str2;
    }

    @Override // com.cloudmagic.android.chips.RecipientEditTextView.KeyboardEventListener
    public void onKeyboardDown() {
        hideEmailSuggestionContainer();
        this.mOnKeyboardDown = true;
    }

    @Override // com.cloudmagic.android.adapters.InteractionSummaryListAdapter.ClickOfContactsInterface
    public void onClickOfContacts(Object obj, int i) {
        ExternalContact externalContact = (ExternalContact) obj;
        Bundle bundle = new Bundle();
        bundle.putString("preview_type", PeopleProfileTable.TABLE_NAME);
        bundle.putParcelable("address_pair", new Pair(externalContact.getName(), externalContact.getEmail()));
        bundle.putInt("account_id", i);
        if (!isTablet()) {
            PeopleProfileFragment peopleProfileFragment = new PeopleProfileFragment();
            peopleProfileFragment.setArguments(bundle);
            FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            beginTransaction.add(R.id.profile_details_container, peopleProfileFragment, "people_profile_fragment");
            beginTransaction.commitAllowingStateLoss();
            return;
        }
        PeopleProfileFragment peopleProfileFragment2 = new PeopleProfileFragment();
        peopleProfileFragment2.setArguments(bundle);
        peopleProfileFragment2.show(getFragmentManager(), "people_profile_fragment");
    }

    @Override // com.cloudmagic.android.chips.BaseNetworkRecipientAdapter.UserAccountGetter
    public UserAccount getUserAccount() {
        return (UserAccount) this.fromFieldSpinner.getSelectedItem();
    }

    /* loaded from: classes.dex */
    private class ImageOptimizerTask extends AsyncTask<Void, Void, Void> {
        ProgressDialogFragment dialogFragment;

        private ImageOptimizerTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPreExecute() {
            super.onPreExecute();
            this.dialogFragment = ProgressDialogFragment.newInstance(ComposeViewFragment.this.getString(R.string.please_wait_msg), false);
            this.dialogFragment.show(ComposeViewFragment.this.getActivity().getSupportFragmentManager(), ProgressDialogFragment.TAG);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public Void doInBackground(Void... voidArr) {
            if (ComposeViewFragment.this.getActivity() == null) {
                return null;
            }
            ComposeViewFragment.this.optimizeAttachmentImages();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.cloudmagic.android.utils.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((ImageOptimizerTask) r1);
            if (ComposeViewFragment.this.getActivity() == null) {
                return;
            }
            try {
                this.dialogFragment.dismiss();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            ComposeViewFragment.this.checkFilesAttachmentSize();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0083 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void getStoredQuotedTextFromFile() {
        /*
            r5 = this;
            java.io.File r0 = new java.io.File
            android.content.Context r1 = r5.mContext
            java.io.File r1 = r1.getFilesDir()
            java.lang.String r2 = "quotedtextfile"
            r0.<init>(r1, r2)
            boolean r1 = r0.exists()
            if (r1 == 0) goto L91
            r1 = 0
            java.util.Scanner r2 = new java.util.Scanner     // Catch: java.lang.Throwable -> L64 java.io.FileNotFoundException -> L69
            r2.<init>(r0)     // Catch: java.lang.Throwable -> L64 java.io.FileNotFoundException -> L69
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            r1.<init>()     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
        L1e:
            boolean r3 = r2.hasNextLine()     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            if (r3 == 0) goto L2c
            java.lang.String r3 = r2.nextLine()     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            r1.append(r3)     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            goto L1e
        L2c:
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            java.lang.String r1 = r1.toString()     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            r3.<init>(r1)     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            java.lang.String r1 = "quoted_html_text"
            boolean r1 = r3.getBoolean(r1)     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            if (r1 == 0) goto L46
            java.lang.String r1 = "data"
            java.lang.String r1 = r3.optString(r1)     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            r5.mQuotedText = r1     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            goto L56
        L46:
            java.lang.String r1 = "data"
            java.lang.String r1 = r3.optString(r1)     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            r5.mQuotedText = r1     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            r1 = 0
            r5.mIsQuotedTextHTML = r1     // Catch: org.json.JSONException -> L52 java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            goto L56
        L52:
            r1 = move-exception
            r1.printStackTrace()     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
        L56:
            r2.close()     // Catch: java.io.FileNotFoundException -> L62 java.lang.Throwable -> L80
            if (r2 == 0) goto L5e
            r2.close()     // Catch: java.lang.Exception -> L76
        L5e:
            r0.delete()     // Catch: java.lang.Exception -> L76
            goto L91
        L62:
            r1 = move-exception
            goto L6d
        L64:
            r2 = move-exception
            r4 = r2
            r2 = r1
            r1 = r4
            goto L81
        L69:
            r2 = move-exception
            r4 = r2
            r2 = r1
            r1 = r4
        L6d:
            r1.printStackTrace()     // Catch: java.lang.Throwable -> L80
            if (r2 == 0) goto L78
            r2.close()     // Catch: java.lang.Exception -> L76
            goto L78
        L76:
            r0 = move-exception
            goto L7c
        L78:
            r0.delete()     // Catch: java.lang.Exception -> L76
            goto L91
        L7c:
            r0.printStackTrace()
            goto L91
        L80:
            r1 = move-exception
        L81:
            if (r2 == 0) goto L89
            r2.close()     // Catch: java.lang.Exception -> L87
            goto L89
        L87:
            r0 = move-exception
            goto L8d
        L89:
            r0.delete()     // Catch: java.lang.Exception -> L87
            goto L90
        L8d:
            r0.printStackTrace()
        L90:
            throw r1
        L91:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cloudmagic.android.fragments.ComposeViewFragment.getStoredQuotedTextFromFile():void");
    }

    private void storeQuotedTextInFile() {
        FileOutputStream fileOutputStream;
        File file = new File(this.mContext.getFilesDir(), QUOTED_TEXT_FILE);
        JSONObject jSONObject = new JSONObject();
        try {
            if (this.mIsQuotedTextHTML) {
                jSONObject.put("quoted_html_text", true);
            } else {
                jSONObject.put("quoted_html_text", false);
            }
            jSONObject.put("data", this.mQuotedText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (FileNotFoundException e3) {
                e = e3;
            } catch (IOException e4) {
                e = e4;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            fileOutputStream.write(jSONObject.toString().getBytes());
        } catch (FileNotFoundException e5) {
            e = e5;
            fileOutputStream2 = fileOutputStream;
            e.printStackTrace();
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
        } catch (IOException e6) {
            e = e6;
            fileOutputStream2 = fileOutputStream;
            e.printStackTrace();
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (Exception e7) {
                    e7.printStackTrace();
                }
            }
            throw th;
        }
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mTimer != null) {
            this.mTimer.cancel();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
        }
        super.onDestroy();
    }

    /* loaded from: classes.dex */
    private class SendClickListener implements View.OnClickListener {
        private SendClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (UserAccount.getArchiveDestinationFolderId(ComposeViewFragment.this.mContext, ComposeViewFragment.this.mAccountID) == -999 || !UserPreferences.getInstance(ComposeViewFragment.this.mContext).getIsConversationView() || !UserPreferences.getInstance(ComposeViewFragment.this.mContext).replyArchiveEnabled() || ComposeViewFragment.this.mSnoozeTime != 0 || ComposeViewFragment.this.mReferenceConversation == null || (!ComposeViewFragment.this.mReferenceConversation.belongsToFolder(0) && (ComposeViewFragment.this.mIsSearchModeActive || !ComposeViewFragment.this.mReferenceConversation.belongsToFolder(1) || ComposeViewFragment.this.mCurrentFolder == null || ComposeViewFragment.this.mCurrentFolder.folderType != 0))) {
                ComposeViewFragment.this.sendMailFromSendOption();
                return;
            }
            if ((ComposeViewFragment.this.mActionType.equals(ActionService.ACTION_TYPE_REPLY) || ComposeViewFragment.this.mActionType.equals("reply_all")) && !ComposeViewFragment.this.mIsEditDraft) {
                ComposeViewFragment.this.sendReplyArchiveMessage(true);
            } else if (ComposeViewFragment.this.mActionType.equals(ActionService.ACTION_TYPE_SEND_DRAFT) || ComposeViewFragment.this.mIsEditDraft) {
                ComposeViewFragment.this.isReplyAndArchiveAllowedForSendDraft();
            } else {
                ComposeViewFragment.this.sendMailFromSendOption();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUpAttachmentContainerHeight(int i) {
        if (getResources().getConfiguration().orientation == 1) {
            if (getFocusedView() instanceof ActionDoneChipAddressTextView) {
                this.keyBoardHeight = (i - ((int) getResources().getDimension(R.dimen.stock_bottom_navigation_height))) + this.emailSuggestionContainer.getHeight();
                return;
            } else {
                this.keyBoardHeight = i - ((int) getResources().getDimension(R.dimen.stock_bottom_navigation_height));
                return;
            }
        }
        if (getFocusedView() instanceof ActionDoneChipAddressTextView) {
            this.keyBoardHeight = i + this.emailSuggestionContainer.getHeight();
        } else {
            this.keyBoardHeight = i;
        }
    }

    private void updateFollowUpButton() {
        if (this.shouldNotifyWhenRead != 0 || this.mSendLaterTime > 0 || this.mSnoozeTime > 0) {
            this.followUpButton.setSelected(true);
        } else if (this.composeExtrasContainer.getVisibility() == 8) {
            this.followUpButton.setSelected(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRemindLaterText() {
        ComposeSendExtrasFragment composeSendExtrasFragment = (ComposeSendExtrasFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ComposeSendExtrasFragment.TAG);
        if (composeSendExtrasFragment == null || this.composeExtrasContainer.getVisibility() != 0) {
            return;
        }
        composeSendExtrasFragment.updateRemindLater(this.mSnoozeTime);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSendLaterText() {
        ComposeSendExtrasFragment composeSendExtrasFragment = (ComposeSendExtrasFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ComposeSendExtrasFragment.TAG);
        if (composeSendExtrasFragment == null || this.composeExtrasContainer.getVisibility() != 0) {
            return;
        }
        composeSendExtrasFragment.updateSendLater(this.mSendLaterTime);
        composeSendExtrasFragment.updateRemindLater(this.mSnoozeTime);
    }

    public void onGotItClick() {
        this.rrEnabledSnackbar.setVisibility(8);
        UserPreferences.getInstance(this.mContext).setHasUserSeenRRGDPRMessage(System.currentTimeMillis() / 1000);
        PreferenceSettingsUtilities.passPreferenceSettingsToServer(this.mContext, -1, PreferenceSettingsUtilities.ACTION_TYPE_MERGE, UserPreferences.HAS_USER_SEEN_RR_GDPR_MESSAGE);
    }
}
