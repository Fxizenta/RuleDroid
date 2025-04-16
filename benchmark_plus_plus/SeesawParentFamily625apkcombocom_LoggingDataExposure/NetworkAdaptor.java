package seesaw.shadowpuppet.co.seesaw.utils.network;

import android.text.TextUtils;
import android.util.Log;
import c.e.d.o;
import com.google.common.base.Charsets;
import com.google.common.base.InterfaceC0705h;
import com.google.common.base.Joiner;
import com.google.common.collect.C0726fa;
import com.google.common.collect.C0737m;
import com.google.common.io.i;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bytedeco.opencv.global.opencv_videoio;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;
import seesaw.shadowpuppet.co.seesaw.BuildConfig;
import seesaw.shadowpuppet.co.seesaw.R;
import seesaw.shadowpuppet.co.seesaw.SeesawApplication;
import seesaw.shadowpuppet.co.seesaw.model.API.AccountLoginResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.AddChildQRCodeResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.AddChildResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.AppThemeResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ArchivedClassesResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.BlogResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.BlogTagsFilterResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.BookmarkPromptResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CalendarCountResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CalendarItemsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CheckBlogSubdomainIsAvailableResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ChildItemsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ClassDashboardInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ClassItemsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ClassJoinCodeResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ClassShortCodeResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CommentsTranslationResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ConnectedBlogsCountResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CreateParentResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CreateStudentResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CreateTagResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.CreateTeacherResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.DuplicateItemResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.DuplicatedUserPromptResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.EditItemTagsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.EditPeopleTagsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.EmailResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.EmptyResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ExportItemToPdfResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.FamilyRepliesEnabledResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.GDPRProtectionsInfo;
import seesaw.shadowpuppet.co.seesaw.model.API.ItemAndCommentsTranslationResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ItemResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.JoinClassResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.JoinedClassResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.LabelStylesResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.LinkScreenshotResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ListConnectedBlogResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.MarkNotificationSeenResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.NotificationInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.NotificationSectionDataResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ParentDashboardInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ParentsInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PendingApprovalResultResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PendingParentApprovalsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PersonFilterResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PlusInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PredefinedIconsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PrivateNoteResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptCollectionResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptCollectionsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptDetailsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptLibraryInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptSubmissionResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PromptsFeedResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.PublicProfileResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.SectionDataResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.SeesawPlusSkuResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ShortcutQRCodeResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.ShouldRefreshFeedResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.SingleClassResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.SkillListsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.SkillsFilterResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.SkillsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.StudentDashboardInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.StudentInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.StudentListResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.TagsResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.TeacherDashboardInfoResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.UpdateIconPictureResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.UpdateProfilePictureResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.VersionCheckResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.announcements.InboxSummaryResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.announcements.StudentInboxSummaryResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.announcements.conversation.ConversationMessagesResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.announcements.conversation.CreateAnnouncementResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.announcements.conversation.SendConversationMessageResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.base.APIError;
import seesaw.shadowpuppet.co.seesaw.model.API.base.APIObjectList;
import seesaw.shadowpuppet.co.seesaw.model.API.base.APIResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.journals.ChildClassesResponse;
import seesaw.shadowpuppet.co.seesaw.model.API.journals.FamilyInfoChildrenResponse;
import seesaw.shadowpuppet.co.seesaw.model.AudioDraftObject;
import seesaw.shadowpuppet.co.seesaw.model.FeedObjectApprovalInfo;
import seesaw.shadowpuppet.co.seesaw.model.Item;
import seesaw.shadowpuppet.co.seesaw.model.MCClass;
import seesaw.shadowpuppet.co.seesaw.model.Person;
import seesaw.shadowpuppet.co.seesaw.model.PlusInfo;
import seesaw.shadowpuppet.co.seesaw.model.PromptSharePayload;
import seesaw.shadowpuppet.co.seesaw.model.Tag;
import seesaw.shadowpuppet.co.seesaw.translation.model.api.SingleCommentTranslationResponse;
import seesaw.shadowpuppet.co.seesaw.utils.AppConfig;
import seesaw.shadowpuppet.co.seesaw.utils.CollectionUtils;
import seesaw.shadowpuppet.co.seesaw.utils.FeatureFlagManager;
import seesaw.shadowpuppet.co.seesaw.utils.FeedObjectsController;
import seesaw.shadowpuppet.co.seesaw.utils.Session;
import seesaw.shadowpuppet.co.seesaw.utils.StringUtils;
import seesaw.shadowpuppet.co.seesaw.utils.Utils;
import seesaw.shadowpuppet.co.seesaw.utils.network.ProgressTypedFile;

/* loaded from: classes.dex */
public class NetworkAdaptor {
    private static NetworkAdaptor instance;
    private NetworkService mApiNetworkService = null;
    private NetworkService mUploadNetworkService = null;
    private String mApiEndPoint = null;
    private String mUploadEndPoint = null;

    /* renamed from: seesaw.shadowpuppet.co.seesaw.utils.network.NetworkAdaptor$5, reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$seesaw$shadowpuppet$co$seesaw$utils$FeedObjectsController$FeedApprovalStatus = new int[FeedObjectsController.FeedApprovalStatus.values().length];

        static {
            try {
                $SwitchMap$seesaw$shadowpuppet$co$seesaw$utils$FeedObjectsController$FeedApprovalStatus[FeedObjectsController.FeedApprovalStatus.ALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$seesaw$shadowpuppet$co$seesaw$utils$FeedObjectsController$FeedApprovalStatus[FeedObjectsController.FeedApprovalStatus.APPROVED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$seesaw$shadowpuppet$co$seesaw$utils$FeedObjectsController$FeedApprovalStatus[FeedObjectsController.FeedApprovalStatus.UNAPPROVED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class APICallback<T> {
        private boolean isCancelled = false;

        public void cancel() {
            this.isCancelled = true;
        }

        public abstract void failure(Error error);

        public boolean isCancelled() {
            return this.isCancelled;
        }

        public abstract void success(T t);
    }

    /* loaded from: classes2.dex */
    public static class ErrorSummary implements Serializable {
        private String mDescription;
        private String mTitle;

        public ErrorSummary(String str, String str2) {
            this.mTitle = str;
            this.mDescription = str2;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public String getTitle() {
            return this.mTitle;
        }
    }

    protected NetworkAdaptor() {
        updateEndPoints();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void a(RequestInterceptor.RequestFacade requestFacade) {
        String sessionToken = Session.getInstance().getSessionToken();
        if (sessionToken != null) {
            requestFacade.addHeader("Authorization", "Bearer " + sessionToken);
        }
        SeesawApplication application = Session.getInstance().getApplication();
        requestFacade.addQueryParam("_bundle", application.getBundleId());
        requestFacade.addQueryParam("_build", String.valueOf(application.getBuildNumber()));
        requestFacade.addQueryParam("_model", Utils.getDeviceName());
        requestFacade.addQueryParam("_install_id", AppConfig.getInstance().getInstallId());
        requestFacade.addQueryParam("_tz_offset", String.valueOf(Calendar.getInstance().getTimeZone().getRawOffset() / opencv_videoio.CAP_ANDROID));
        Person person = Session.getInstance().getPerson();
        if (person != null) {
            requestFacade.addQueryParam("user_id", person.personId);
        }
        FeatureFlagManager.getInstance().addVarIdToRequestHeaderWithEncoding(requestFacade);
    }

    public static void addBlogComment(String str, String str2, String str3, String str4, APICallback<Item> aPICallback) {
        getInstance().mApiNetworkService.addBlogComment(str, str2, str3, str4, createCallback(aPICallback));
    }

    public static void addChild(String str, String str2, String str3, String str4, APICallback<AddChildResponse> aPICallback) {
        getInstance().mApiNetworkService.addChild(str, str2, str3, str4, createCallback(aPICallback));
    }

    public static void addComment(String str, String str2, String str3, APICallback<Item> aPICallback) {
        getInstance().mApiNetworkService.addComment(str, str2, str3, createCallback(aPICallback));
    }

    public static void addConversationMessageAttachment(String str, String str2, double d2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.addConversationMessageAttachment(str, str2, d2, createCallback(aPICallback));
    }

    public static void addItemToBlog(String str, APICallback<ItemResponse> aPICallback) {
        getInstance().mApiNetworkService.addItemToBlog(str, createCallback(aPICallback));
    }

    public static void addPrivateAudioNote(String str, File file, double d2, APICallback<PrivateNoteResponse> aPICallback) {
        Callback<APIResponse<PrivateNoteResponse>> createCallback = createCallback(aPICallback);
        getInstance().mApiNetworkService.addPrivateAudioNote(str, new TypedFile("audio/mp4a-latm", file), d2, createCallback);
    }

    public static void addPrivateTextNote(String str, String str2, APICallback<PrivateNoteResponse> aPICallback) {
        getInstance().mApiNetworkService.addPrivateTextNote(str, str2, createCallback(aPICallback));
    }

    public static void addPromptToCollection(String str, String str2, APICallback<PromptCollectionResponse> aPICallback) {
        getInstance().mApiNetworkService.addPromptToCollection(str, str2, createCallback(aPICallback));
    }

    public static void addStudent(String str, String str2, APICallback<Person> aPICallback) {
        getInstance().mApiNetworkService.addStudent(str, str2, createCallback(aPICallback));
    }

    public static void addStudents(String str, List<String> list, APICallback<SingleClassResponse> aPICallback) {
        getInstance().mApiNetworkService.addStudentsToClass(str, TextUtils.join("\t\t", list), createCallback(aPICallback));
    }

    public static void addTag(String str, String str2, String str3, APICallback<CreateTagResponse> aPICallback) {
        getInstance().mApiNetworkService.addTag(str, str2, str3, createCallback(aPICallback));
    }

    public static void archiveClass(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.archiveClass(str, createCallback(aPICallback));
    }

    public static void archivePrompt(String str, APICallback<PromptResponse> aPICallback) {
        getInstance().mApiNetworkService.archivePrompt(str, createCallback(aPICallback));
    }

    public static void archiveSkill(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.archiveSkill(str, createCallback(aPICallback));
    }

    public static void bookmarkPrompt(String str, String str2, String str3, APICallback<BookmarkPromptResponse> aPICallback) {
        getInstance().mApiNetworkService.bookmarkPrompt(str, str2, str3, createCallback(aPICallback));
    }

    public static void bookmarkPromptWithShareToken(String str, String str2, APICallback<BookmarkPromptResponse> aPICallback) {
        getInstance().mApiNetworkService.bookmarkPromptWithShareToken(str, str2, createCallback(aPICallback));
    }

    private RestAdapter buildAdapter(String str) {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setConverter(new GsonConverter(new o()));
        builder.setEndpoint(str);
        builder.setRequestInterceptor(new RequestInterceptor() { // from class: seesaw.shadowpuppet.co.seesaw.utils.network.a
            @Override // retrofit.RequestInterceptor
            public final void intercept(RequestInterceptor.RequestFacade requestFacade) {
                NetworkAdaptor.a(requestFacade);
            }
        });
        builder.setLogLevel(RestAdapter.LogLevel.BASIC);
        return builder.build();
    }

    public static void bulkApproveFeedObjects(String str, List<FeedObjectApprovalInfo> list, APICallback<EmptyResponse> aPICallback) {
        Callback<APIResponse<EmptyResponse>> createCallback = createCallback(aPICallback);
        HashMap c2 = C0726fa.c();
        c2.put("item_approval_info", list);
        getInstance().mApiNetworkService.bulkApproveFeedObjects(str, new o().a(c2), createCallback);
    }

    public static void checkBlogSubdomainIsAvailable(String str, APICallback<CheckBlogSubdomainIsAvailableResponse> aPICallback) {
        getInstance().mApiNetworkService.checkBlogSubdomainIsAvailable(str, createCallback(aPICallback));
    }

    public static void connectBlog(String str, String str2, String str3, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.connectBlog(str, str2, str3, createCallback(aPICallback));
    }

    public static void copyUserPromptById(String str, APICallback<DuplicatedUserPromptResponse> aPICallback) {
        getInstance().mApiNetworkService.copyUserPrompt(str, createCallback(aPICallback));
    }

    public static void createAnnouncement(String str, String str2, String str3, String str4, APICallback<CreateAnnouncementResponse> aPICallback) {
        getInstance().mApiNetworkService.createAnnouncement(str, str2, str3, str4, createCallback(aPICallback));
    }

    public static void createBlog(String str, String str2, String str3, boolean z, boolean z2, String str4, APICallback<BlogResponse> aPICallback) {
        getInstance().mApiNetworkService.createBlog(str, str2, str3, getBooleanValue(z), getBooleanValue(z2), str4, createCallback(aPICallback));
    }

    private static <T> Callback<APIResponse<T>> createCallback(final APICallback<T> aPICallback) {
        return new Callback<APIResponse<T>>() { // from class: seesaw.shadowpuppet.co.seesaw.utils.network.NetworkAdaptor.3
            private boolean handleAuthError(APIError aPIError, Error error) {
                if (!aPIError.isAuthError()) {
                    return false;
                }
                if (!Session.getInstance().hasValidSession()) {
                    return true;
                }
                Session.getInstance().logout();
                AppConfig.getInstance().getEventBus().a(new Session.SessionDidExpireEvent(error));
                return true;
            }

            @Override // retrofit.Callback
            public void failure(RetrofitError retrofitError) {
                APICallback aPICallback2 = APICallback.this;
                if (aPICallback2 == null || aPICallback2.isCancelled) {
                    return;
                }
                Error error = new Error(retrofitError);
                Log.e("SEESAW-PARENTS", "NETWORK REQUEST FAILED." + error.getErrorMessage());
                Response response = retrofitError.getResponse();
                if (response != null) {
                    Log.e("SEESAW-PARENTS", "STATUS CODE" + response.getStatus());
                    if (response.getBody() != null) {
                        try {
                            InputStream in2 = response.getBody().in();
                            if (in2 != null) {
                                Log.e("SEESAW-PARENTS", "JSON: " + i.a(new InputStreamReader(in2, Charsets.UTF_8)));
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                APICallback.this.failure(error);
            }

            @Override // retrofit.Callback
            public void success(APIResponse<T> aPIResponse, Response response) {
                APICallback aPICallback2 = APICallback.this;
                if (aPICallback2 == null || aPICallback2.isCancelled) {
                    return;
                }
                if (aPIResponse.status.equals("OK")) {
                    APICallback.this.success(aPIResponse.response);
                    return;
                }
                Error error = new Error(aPIResponse.error);
                if (handleAuthError(aPIResponse.error, error)) {
                    return;
                }
                APICallback.this.failure(error);
            }
        };
    }

    private static <T> Callback<APIResponse<T>> createCallbackAndOverrideFeatureFlagVarId(final APICallback<T> aPICallback) {
        return new Callback<APIResponse<T>>() { // from class: seesaw.shadowpuppet.co.seesaw.utils.network.NetworkAdaptor.4
            private boolean handleAuthError(APIError aPIError, Error error) {
                if (!aPIError.isAuthError()) {
                    return false;
                }
                if (!Session.getInstance().hasValidSession()) {
                    return true;
                }
                Session.getInstance().logout();
                AppConfig.getInstance().getEventBus().a(new Session.SessionDidExpireEvent(error));
                return true;
            }

            @Override // retrofit.Callback
            public void failure(RetrofitError retrofitError) {
                APICallback aPICallback2 = APICallback.this;
                if (aPICallback2 == null || aPICallback2.isCancelled) {
                    return;
                }
                Error error = new Error(retrofitError);
                Log.e("SEESAW-PARENTS", "NETWORK REQUEST FAILED." + error.getErrorMessage());
                Response response = retrofitError.getResponse();
                if (response != null) {
                    Log.e("SEESAW-PARENTS", "STATUS CODE" + response.getStatus());
                    if (response.getBody() != null) {
                        try {
                            InputStream in2 = response.getBody().in();
                            if (in2 != null) {
                                Log.e("SEESAW-PARENTS", "JSON: " + i.a(new InputStreamReader(in2, Charsets.UTF_8)));
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                APICallback.this.failure(error);
            }

            @Override // retrofit.Callback
            public void success(APIResponse<T> aPIResponse, Response response) {
                FeatureFlagManager.getInstance().setVarIdFromHeadersWithDecoding(response.getHeaders());
                APICallback aPICallback2 = APICallback.this;
                if (aPICallback2 == null || aPICallback2.isCancelled) {
                    return;
                }
                if (aPIResponse.status.equals("OK")) {
                    APICallback.this.success(aPIResponse.response);
                    return;
                }
                Error error = new Error(aPIResponse.error);
                if (handleAuthError(aPIResponse.error, error)) {
                    return;
                }
                APICallback.this.failure(error);
            }
        };
    }

    public static void createClassCodeClass(String str, List<String> list, String str2, String str3, APICallback<SingleClassResponse> aPICallback) {
        getInstance().mApiNetworkService.createClass(str, list != null ? Joiner.a("\t\t").join(list) : null, null, str2, str3, createCallback(aPICallback));
    }

    public static void createGoogleClass(String str, String str2, String str3, APICallback<SingleClassResponse> aPICallback) {
        getInstance().mApiNetworkService.createClass(str, null, "google", str2, str3, createCallback(aPICallback));
    }

    public static void createNewCollection(String str, APICallback<PromptCollectionResponse> aPICallback) {
        getInstance().mApiNetworkService.createNewCollection(str, null, null, null, null, createCallback(aPICallback));
    }

    public static void createOrEditPrompt(boolean z, String str, String str2, String str3, Double d2, String str4, String str5, String str6, String str7, List<String> list, boolean z2, List<String> list2, List<String> list3, boolean z3, List<String> list4, List<String> list5, List<String> list6, AudioDraftObject audioDraftObject, boolean z4, APICallback<PromptResponse> aPICallback) {
        Callback<APIResponse<PromptResponse>> createCallback = createCallback(aPICallback);
        String listAsString = StringUtils.getListAsString(list);
        String listAsString2 = StringUtils.getListAsString(list2);
        String listAsString3 = StringUtils.getListAsString(list3);
        String listAsString4 = StringUtils.getListAsString(list4);
        String listAsString5 = StringUtils.getListAsString(list5);
        String listAsString6 = StringUtils.getListAsString(list6);
        NetworkService networkService = getInstance().mApiNetworkService;
        if (z) {
            if (audioDraftObject == null) {
                networkService.createNewPromptNoAudio(str, str3, d2, str4, str5, str6, str7, listAsString, z2, listAsString2, listAsString3, z3, listAsString4, listAsString5, listAsString6, z4, createCallback);
                return;
            } else {
                networkService.createNewPromptWithAudio(str, str3, d2, str4, str5, str6, str7, listAsString, z2, listAsString2, listAsString3, z3, listAsString4, listAsString5, listAsString6, new TypedFile("audio/mp4a-latm", audioDraftObject.getFile()), audioDraftObject.getFormat(), audioDraftObject.getDuration(), z4, createCallback);
                return;
            }
        }
        if (audioDraftObject == null) {
            networkService.editPromptNoAudio(str, str2, str3, d2, str4, str5, str6, str7, listAsString, z2, listAsString2, listAsString3, z3, listAsString4, listAsString5, listAsString6, z4, createCallback);
        } else {
            networkService.editPromptWithAudio(str, str2, str3, d2, str4, str5, str6, str7, listAsString, z2, listAsString2, listAsString3, z3, listAsString4, listAsString5, listAsString6, new TypedFile("audio/mp4a-latm", audioDraftObject.getFile()), audioDraftObject.getFormat(), audioDraftObject.getDuration(), z4, createCallback);
        }
    }

    public static void createParent(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, APICallback<CreateParentResponse> aPICallback) {
        getInstance().mApiNetworkService.createParentAccount(str, str2, str3, str4, str5, str6, str7, str8, createCallback(aPICallback));
    }

    public static void createSkill(String str, String str2, String str3, String str4, String str5, String str6, APICallback<SkillsResponse.Skill> aPICallback) {
        getInstance().mApiNetworkService.createSkill(str, str2, str3, str4, str5, str6, createCallback(aPICallback));
    }

    public static void createStudent(String str, String str2, String str3, String str4, String str5, APICallback<CreateStudentResponse> aPICallback) {
        getInstance().mApiNetworkService.createStudentAccount(str, str2, str3, str4, str5, createCallback(aPICallback));
    }

    public static void createTeacher(String str, String str2, String str3, String str4, String str5, APICallback<CreateTeacherResponse> aPICallback) {
        getInstance().mApiNetworkService.createTeacherAccount(str, str2, str3, str4, str5, createCallback(aPICallback));
    }

    public static void deleteClass(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteClass(str, createCallback(aPICallback));
    }

    public static void deleteCollection(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteCollection(str, createCallback(aPICallback));
    }

    public static void deleteComment(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteComment(str, str2, createCallback(aPICallback));
    }

    public static void deleteConversationMessage(String str, double d2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteConversationMessage(str, d2, createCallback(aPICallback));
    }

    public static void deleteConversationMessageAttachment(String str, double d2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteConversationMessageAttachment(str, d2, createCallback(aPICallback));
    }

    public static void deleteItem(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteItem(str, createCallback(aPICallback));
    }

    public static void deleteParent(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteParent(str, str2, createCallback(aPICallback));
    }

    public static void deletePrivateNote(String str, APICallback<PrivateNoteResponse> aPICallback) {
        getInstance().mApiNetworkService.deletePrivateNote(str, createCallback(aPICallback));
    }

    public static void deletePrompt(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deletePrompt(str, createCallback(aPICallback));
    }

    public static void deleteStudent(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.deleteStudent(str, str2, createCallback(aPICallback));
    }

    public static void duplicateItem(String str, String str2, APICallback<DuplicateItemResponse> aPICallback) {
        getInstance().mApiNetworkService.duplicateItem(str, str2, createCallback(aPICallback));
    }

    public static void duplicatePromptForClasses(String str, String str2, String str3, String str4, Long l, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.duplicatePromptForClasses(str, str2, str3, str4, l, createCallback(aPICallback));
    }

    public static void duplicatePromptFromPublicLibrary(String str, String str2, boolean z, APICallback<DuplicatedUserPromptResponse> aPICallback) {
        getInstance().mApiNetworkService.duplicatePromptFromPublicLibrary(str, str2, z, createCallback(aPICallback));
    }

    public static void editConversationMessage(String str, String str2, double d2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.editConversationMessage(str, str2, d2, createCallback(aPICallback));
    }

    public static void editItemFolderTags(String str, Set<Tag> set, Set<Tag> set2, APICallback<EditItemTagsResponse> aPICallback) {
        InterfaceC0705h<Tag, String> interfaceC0705h = new InterfaceC0705h<Tag, String>() { // from class: seesaw.shadowpuppet.co.seesaw.utils.network.NetworkAdaptor.2
            @Override // com.google.common.base.InterfaceC0705h
            public String apply(Tag tag) {
                return tag.tagId;
            }
        };
        Collection a2 = C0737m.a((Collection) set, (InterfaceC0705h) interfaceC0705h);
        Collection a3 = C0737m.a((Collection) set2, (InterfaceC0705h) interfaceC0705h);
        getInstance().mApiNetworkService.editItemFolders(str, Joiner.a(",").join(a2), Joiner.a(",").join(a3), createCallback(aPICallback));
    }

    public static void editItemPeopleTags(String str, Set<Person> set, Set<Person> set2, APICallback<EditPeopleTagsResponse> aPICallback) {
        InterfaceC0705h<Person, String> interfaceC0705h = new InterfaceC0705h<Person, String>() { // from class: seesaw.shadowpuppet.co.seesaw.utils.network.NetworkAdaptor.1
            @Override // com.google.common.base.InterfaceC0705h
            public String apply(Person person) {
                return person.personId;
            }
        };
        Collection a2 = C0737m.a((Collection) set, (InterfaceC0705h) interfaceC0705h);
        Collection a3 = C0737m.a((Collection) set2, (InterfaceC0705h) interfaceC0705h);
        getInstance().mApiNetworkService.editItemPersonTags(str, Joiner.a(",").join(a2), Joiner.a(",").join(a3), createCallback(aPICallback));
    }

    public static void editPrivateNote(String str, String str2, APICallback<PrivateNoteResponse> aPICallback) {
        getInstance().mApiNetworkService.editPrivateNote(str, str2, createCallback(aPICallback));
    }

    public static void editPromptCollection(String str, APIObjectList<String> aPIObjectList, String str2, String str3, String str4, APICallback<PromptCollectionResponse> aPICallback) {
        getInstance().mApiNetworkService.editPromptCollection(str, aPIObjectList, str2, str3, str4, createCallback(aPICallback));
    }

    public static void exportItemToPdf(String str, APICallback<ExportItemToPdfResponse> aPICallback) {
        getInstance().mApiNetworkService.exportToPdf(str, createCallback(aPICallback));
    }

    public static void familyShouldRefresFeedItems(String str, APICallback<ShouldRefreshFeedResponse> aPICallback) {
        getInstance().mApiNetworkService.shouldRefreshChildItems(str, createCallback(aPICallback));
    }

    public static void flagItem(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.addFlag(str, createCallback(aPICallback));
    }

    public static void getAndroidSku(APICallback<SeesawPlusSkuResponse> aPICallback) {
        getInstance().mApiNetworkService.getAndroidSku(createCallback(aPICallback));
    }

    public static void getArchivedClasses(APICallback<ArchivedClassesResponse> aPICallback) {
        getInstance().mApiNetworkService.getArchivedClassesList(createCallback(aPICallback));
    }

    public static void getBlogInfo(String str, APICallback<BlogResponse> aPICallback) {
        getInstance().mApiNetworkService.getBlogInfo(str, createCallback(aPICallback));
    }

    public static void getBlogStudentsFilter(String str, String str2, APICallback<PersonFilterResponse> aPICallback) {
        getInstance().mApiNetworkService.getBlogStudentsFilter(str, str2, createCallback(aPICallback));
    }

    public static void getBlogTagsFilter(String str, String str2, String str3, APICallback<BlogTagsFilterResponse> aPICallback) {
        getInstance().mApiNetworkService.getBlogTagsFilter(str, str2, str3, createCallback(aPICallback));
    }

    private static String getBooleanValue(boolean z) {
        return z ? "1" : "0";
    }

    public static void getChildItems(String str, String str2, String str3, int i, APICallback<ChildItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getChildItems(str, str2, str3, i, createCallback(aPICallback));
    }

    public static void getClassCalendarCounts(String str, String str2, String str3, APICallback<CalendarCountResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassCalendarCounts(str, str2, str3, createCallback(aPICallback));
    }

    public static void getClassCalendarItems(String str, long j, String str2, String str3, APICallback<CalendarItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassCalendarItems(str, j, str2, str3, createCallback(aPICallback));
    }

    public static void getClassConversationMessages(String str, double d2, int i, APICallback<ConversationMessagesResponse> aPICallback) {
        Callback<APIResponse<ConversationMessagesResponse>> createCallback = createCallback(aPICallback);
        if (d2 < 1.0d) {
            getInstance().mApiNetworkService.getConversationMessageClass(str, i, createCallback);
        } else {
            getInstance().mApiNetworkService.getConversationMessageClass(str, d2, i, createCallback);
        }
    }

    public static void getClassDashboardInfo(String str, APICallback<ClassDashboardInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassDashboardInfo(str, createCallback(aPICallback));
    }

    public static void getClassIcons(String str, APICallback<PredefinedIconsResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassIcons(str, createCallback(aPICallback));
    }

    public static void getClassInfo(String str, APICallback<MCClass> aPICallback) {
        getInstance().mApiNetworkService.getClassInfo(str, createCallback(aPICallback));
    }

    public static void getClassJoinCode(String str, APICallback<ClassJoinCodeResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassJoinCode(str, createCallback(aPICallback));
    }

    public static void getClassShortCode(String str, APICallback<ClassShortCodeResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassShortCode(str, createCallback(aPICallback));
    }

    public static void getClassStudentsFilter(String str, APICallback<PersonFilterResponse> aPICallback) {
        getInstance().mApiNetworkService.getStudentItemFilterCounts(str, createCallback(aPICallback));
    }

    public static void getClassesForChild(String str, APICallback<ChildClassesResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyConnectedChildClasses(str, createCallback(aPICallback));
    }

    public static void getClassroomItems(String str, String str2, String str3, FeedObjectsController.FeedApprovalStatus feedApprovalStatus, String str4, int i, APICallback<ClassItemsResponse> aPICallback) {
        Callback<APIResponse<ClassItemsResponse>> createCallback = createCallback(aPICallback);
        int i2 = AnonymousClass5.$SwitchMap$seesaw$shadowpuppet$co$seesaw$utils$FeedObjectsController$FeedApprovalStatus[feedApprovalStatus.ordinal()];
        if (i2 == 1) {
            getInstance().mApiNetworkService.getClassroomItems(str, str2, str3, "all", str4, i, createCallback);
        } else if (i2 == 2) {
            getInstance().mApiNetworkService.getClassroomItems(str, str2, str3, "approved", str4, i, createCallback);
        } else {
            if (i2 != 3) {
                return;
            }
            getInstance().mApiNetworkService.getApprovalQueue(str, str2, str3, str4, i, createCallback);
        }
    }

    public static void getClassroomPrintableInvitation(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.getClassroomPrintableInvitation(str, createCallback(aPICallback));
    }

    public static void getConnectedBlogs(String str, APICallback<ListConnectedBlogResponse> aPICallback) {
        getInstance().mApiNetworkService.getConnectedBlogs(str, createCallback(aPICallback));
    }

    public static void getConnectedBlogsCount(String str, APICallback<ConnectedBlogsCountResponse> aPICallback) {
        getInstance().mApiNetworkService.getConnectedBlogsCount(str, createCallback(aPICallback));
    }

    public static void getFamilyChildClassFolders(String str, String str2, APICallback<TagsResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyChildClassFolders(str, str2, createCallback(aPICallback));
    }

    public static void getFamilyConnectedChildren(APICallback<FamilyInfoChildrenResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyConnectedChildren(createCallback(aPICallback));
    }

    public static void getFamilyConversationMessages(String str, double d2, int i, APICallback<ConversationMessagesResponse> aPICallback) {
        Callback<APIResponse<ConversationMessagesResponse>> createCallback = createCallback(aPICallback);
        if (d2 < 1.0d) {
            getInstance().mApiNetworkService.getConversationMessageFamily(str, i, createCallback);
        } else {
            getInstance().mApiNetworkService.getConversationMessageFamily(str, d2, i, createCallback);
        }
    }

    public static void getFamilyDashboardInfo(APICallback<ParentDashboardInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyDashboardInfo(createCallbackAndOverrideFeatureFlagVarId(aPICallback));
    }

    public static void getFamilyMainFeed(String str, int i, APICallback<ChildItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyMainFeed(str, i, createCallback(aPICallback));
    }

    public static void getFeatureFlags(APICallback<Map<String, Object>> aPICallback) {
        getInstance().mApiNetworkService.getFeatureFlags(createCallback(aPICallback));
    }

    public static void getFeedForChildsClass(String str, String str2, String str3, int i, APICallback<ChildItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyGetChildJournalForClass(str, str2, str3, i, createCallback(aPICallback));
    }

    public static void getFeedForChildsClassFolder(String str, String str2, String str3, String str4, int i, APICallback<ChildItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getFamilyGetChildJournalForClassFolder(str, str2, str3, str4, i, createCallback(aPICallback));
    }

    public static void getGDPRDataProtectionFlags(APICallback<GDPRProtectionsInfo> aPICallback) {
        getInstance().mApiNetworkService.getGDPRProtections(AppConfig.getInstance().isFamilyApp() ? BuildConfig.FLAVOR : "teacher", createCallback(aPICallback));
    }

    public static void getInboxSummaryFamily(APICallback<InboxSummaryResponse> aPICallback) {
        getInstance().mApiNetworkService.getInboxSummaryFamily(createCallback(aPICallback));
    }

    public static void getInboxSummaryStudent(String str, APICallback<StudentInboxSummaryResponse> aPICallback) {
        getInstance().mApiNetworkService.getInboxSummaryStudent(str, createCallback(aPICallback));
    }

    public static void getInboxSummaryTeacher(String str, APICallback<InboxSummaryResponse> aPICallback) {
        getInstance().mApiNetworkService.getInboxSummaryTeacher(str, createCallback(aPICallback));
    }

    public static NetworkAdaptor getInstance() {
        if (instance == null) {
            instance = new NetworkAdaptor();
        }
        return instance;
    }

    public static void getInviteParentEmail(String str, APICallback<EmailResponse> aPICallback) {
        getInstance().mApiNetworkService.getInviteParentEmail(str, createCallback(aPICallback));
    }

    public static void getItem(String str, APICallback<ItemResponse> aPICallback) {
        getInstance().mApiNetworkService.getItem(str, null, 0.0d, createCallback(aPICallback));
    }

    public static void getItemFromAttachment(String str, String str2, double d2, APICallback<ItemResponse> aPICallback) {
        getInstance().mApiNetworkService.getItem(str, str2, d2, createCallback(aPICallback));
    }

    public static void getLibraryPromptById(String str, String str2, APICallback<PromptResponse> aPICallback) {
        getInstance().mApiNetworkService.getLibraryPrompt(str, str2, createCallback(aPICallback));
    }

    public static void getLinkScreenshot(String str, APICallback<LinkScreenshotResponse> aPICallback) {
        getInstance().mApiNetworkService.getLinkScreenshot(str, createCallback(aPICallback));
    }

    public static void getMergeStudentOptions(String str, String str2, APICallback<SectionDataResponse<Person>> aPICallback) {
        getInstance().mApiNetworkService.getMergeStudentOptions(str, str2, createCallback(aPICallback));
    }

    public static void getNotificationInfo(String str, APICallback<NotificationInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getNotificationInfo(str, createCallback(aPICallback));
    }

    public static void getParentCalendarCounts(String str, String str2, String str3, APICallback<CalendarCountResponse> aPICallback) {
        getInstance().mApiNetworkService.getParentCalendarCounts(str, str2, str3, createCallback(aPICallback));
    }

    public static void getParentCalendarItems(String str, long j, String str2, String str3, APICallback<CalendarItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getParentCalendarItems(str, j, str2, str3, createCallback(aPICallback));
    }

    public static void getParentDashboardInfo(String str, APICallback<ParentDashboardInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getParentDashboardInfo(str, createCallback(aPICallback));
    }

    public static void getParentNotifications(String str, boolean z, APICallback<NotificationSectionDataResponse.ParentNotificationSectionDataResponse> aPICallback) {
        getInstance().mApiNetworkService.getParentNotifications(str, z, createCallback(aPICallback));
    }

    public static void getParentQuestionEmail(String str, APICallback<EmailResponse> aPICallback) {
        getInstance().mApiNetworkService.getParentQuestionEmail(str, createCallback(aPICallback));
    }

    public static void getParents(String str, APICallback<ParentsInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getParents(str, createCallback(aPICallback));
    }

    public static void getParentsPrintableInvitation(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.getParentsPrintableInvitation(str, createCallback(aPICallback));
    }

    public static void getPendingParentApproval(String str, APICallback<PendingParentApprovalsResponse> aPICallback) {
        getInstance().mApiNetworkService.getPendingParentApproval(str, createCallback(aPICallback));
    }

    public static void getPredefinedIcons(String str, String str2, APICallback<PredefinedIconsResponse> aPICallback) {
        getInstance().mApiNetworkService.getPredefinedIcons(str, str2, createCallback(aPICallback));
    }

    public static void getPrivateNotes(String str, APICallback<PrivateNoteResponse> aPICallback) {
        getInstance().mApiNetworkService.getPrivateNotes(str, createCallback(aPICallback));
    }

    public static void getPromptCollectionById(String str, APICallback<PromptCollectionResponse> aPICallback) {
        getInstance().mApiNetworkService.getPromptCollectionById(str, createCallback(aPICallback));
    }

    public static void getPromptCollectionsForUser(APICallback<PromptCollectionsResponse> aPICallback) {
        getInstance().mApiNetworkService.getPromptCollectionsForUser(createCallback(aPICallback));
    }

    public static void getPromptDetailsWithResponses(String str, String str2, APICallback<PromptDetailsResponse> aPICallback) {
        getInstance().mApiNetworkService.getPromptDetailsWithResponses(str, str2, createCallback(aPICallback));
    }

    public static void getPromptFeed(String str, String str2, List<String> list, boolean z, String str3, APICallback<PromptsFeedResponse> aPICallback) {
        Callback<APIResponse<PromptsFeedResponse>> createCallback = createCallback(aPICallback);
        getInstance().mApiNetworkService.getPromptFeed(str, str2, StringUtils.getConcatenatedStringFromStrings(list, ","), z, str3, createCallback);
    }

    public static void getPromptLibraryInfo(String str, APICallback<PromptLibraryInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getPromptLibraryInfo(str, createCallback(aPICallback));
    }

    public static void getPromptSharePayload(String str, boolean z, APICallback<PromptSharePayload> aPICallback) {
        getInstance().mApiNetworkService.getPromptSharePayload(str, z, createCallback(aPICallback));
    }

    public static void getPublicBlogItem(String str, String str2, APICallback<ItemResponse> aPICallback) {
        getInstance().mApiNetworkService.getPublicBlogItem(str, str2, createCallback(aPICallback));
    }

    public static void getPublicBlogItems(String str, String str2, String str3, String str4, String str5, int i, APICallback<ClassItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getPublicBlogFeed(str, str2, str3, str4, str5, i, str5 == null, createCallback(aPICallback));
    }

    public static void getSkillFeedItems(String str, String str2, Boolean bool, String str3, String str4, Integer num, APICallback<ClassItemsResponse> aPICallback) {
        getInstance().mApiNetworkService.getSkillFeedItems(str, str2, bool == null ? false : bool.booleanValue(), str3, str4, num, createCallback(aPICallback));
    }

    public static void getSkillLists(APICallback<SkillListsResponse> aPICallback) {
        getInstance().mApiNetworkService.getSkillLists(createCallback(aPICallback));
    }

    public static void getSkillsGridFilters(String str, String str2, APICallback<SkillsFilterResponse> aPICallback) {
        getInstance().mApiNetworkService.getSkillsGridFilters(str, str2, createCallback(aPICallback));
    }

    public static void getSkillsStatsGrid(String str, String str2, String str3, APICallback<SkillsResponse> aPICallback) {
        getInstance().mApiNetworkService.getSkillsStatsGrid(str, str2, str3, createCallback(aPICallback));
    }

    public static void getSkillsTagListsForItem(String str, String str2, boolean z, APICallback<SkillListsResponse> aPICallback) {
        getInstance().mApiNetworkService.getSkillsTagLists(str, str2, null, z, createCallback(aPICallback));
    }

    public static void getSkillsTagsListForPrompt(String str, String str2, boolean z, APICallback<SkillListsResponse> aPICallback) {
        getInstance().mApiNetworkService.getSkillsTagLists(str, null, str2, z, createCallback(aPICallback));
    }

    public static void getStudentDashboardInfo(APICallback<StudentDashboardInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getStudentDashboardInfo(createCallback(aPICallback));
    }

    public static void getStudentInfo(String str, String str2, String str3, APICallback<StudentInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getStudentInfo(str, str2, str3, createCallback(aPICallback));
    }

    public static void getStudentJoinInstructions(String str, APICallback<EmailResponse> aPICallback) {
        getInstance().mApiNetworkService.getStudentJoinInstructionsEmail(str, createCallback(aPICallback));
    }

    public static void getStudentNotifications(String str, String str2, boolean z, APICallback<NotificationSectionDataResponse.StudentNotificationSectionDataResponse> aPICallback) {
        getInstance().mApiNetworkService.getStudentNotifications(str, str2, z, createCallback(aPICallback));
    }

    public static void getStudents(String str, APICallback<StudentListResponse> aPICallback) {
        getInstance().mApiNetworkService.getStudents(str, createCallback(aPICallback));
    }

    public static void getStyles(APICallback<LabelStylesResponse> aPICallback) {
        getInstance().mApiNetworkService.getStyles(createCallback(aPICallback));
    }

    public static void getTags(String str, String str2, TagsResponse.RequestMode requestMode, APICallback<TagsResponse> aPICallback) {
        getInstance().mApiNetworkService.getTags(str, str2, requestMode.toString(), createCallback(aPICallback));
    }

    public static void getTeacherDashboardInfo(APICallback<TeacherDashboardInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.getTeacherDashboardInfo(createCallbackAndOverrideFeatureFlagVarId(aPICallback));
    }

    public static void getTeacherNotifications(String str, String str2, boolean z, APICallback<NotificationSectionDataResponse.TeacherNotificationSectionDataResponse> aPICallback) {
        getInstance().mApiNetworkService.getTeacherNotifications(str, str2, z, createCallback(aPICallback));
    }

    public static void getThemes(APICallback<AppThemeResponse> aPICallback) {
        getInstance().mApiNetworkService.getThemes(createCallback(aPICallback));
    }

    public static void getUserPromptById(String str, APICallback<PromptResponse> aPICallback) {
        getInstance().mApiNetworkService.getUserPrompt(str, createCallback(aPICallback));
    }

    public static void inviteCoTeacher(String str, String str2, String str3, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.inviteCoTeacher(str, str2, str3, createCallback(aPICallback));
    }

    public static void joinClassByJoinCode(String str, String str2, APICallback<JoinedClassResponse> aPICallback) {
        getInstance().mApiNetworkService.joinClassByJoinCode(str, str2, createCallback(aPICallback));
    }

    public static void joinClassByQRCode(String str, APICallback<JoinClassResponse> aPICallback) {
        getInstance().mApiNetworkService.joinClassByQRCode(str, createCallback(aPICallback));
    }

    public static void joinClassByTempCode(String str, APICallback<JoinClassResponse> aPICallback) {
        getInstance().mApiNetworkService.joinClassByTempCode(str, createCallback(aPICallback));
    }

    public static void likeItem(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.likeItem(str, str2, createCallback(aPICallback));
    }

    public static void logDebugInfo(String str, String str2) {
        getInstance().mApiNetworkService.logDebugInfo(str2, str, createCallback(null));
    }

    public static void logIn(String str, String str2, String str3, APICallback<AccountLoginResponse> aPICallback) {
        getInstance().mApiNetworkService.logIn(str, str2, str3, createCallback(aPICallback));
    }

    public static void logInWithGoogle(String str, String str2, String str3, APICallback<AccountLoginResponse> aPICallback) {
        getInstance().mApiNetworkService.logInWithGoogle(str2, str, str3, createCallback(aPICallback));
    }

    public static void logInWithGoogleV3(String str, String str2, String str3, APICallback<AccountLoginResponse> aPICallback) {
        getInstance().mApiNetworkService.logInWithGoogleV3(str, str2, str3, createCallback(aPICallback));
    }

    public static void logPromotionImpression(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.logPromotionImpression(str, createCallback(aPICallback));
    }

    public static void loginWithClever(String str, String str2, APICallback<AccountLoginResponse> aPICallback) {
        getInstance().mApiNetworkService.loginWithClever(str2, str, createCallback(aPICallback));
    }

    public static void logout(APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.logout(createCallback(aPICallback));
    }

    public static void lookupBlog(String str, APICallback<BlogResponse> aPICallback) {
        getInstance().mApiNetworkService.lookupBlog(str, null, createCallback(aPICallback));
    }

    public static void markNotificationSeen(String str, APICallback<MarkNotificationSeenResponse> aPICallback) {
        getInstance().mApiNetworkService.markNotificationSeen(str, createCallback(aPICallback));
    }

    public static void mergeStudents(String str, String str2, String str3, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.mergeStudents(str, str2, str3, createCallback(aPICallback));
    }

    public static void pingBlog(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.pingBlog(str, str2, createCallback(aPICallback));
    }

    public static void recordNuxView(PlusInfo.NuxType nuxType, APICallback<PlusInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.recordNuxView(nuxType, createCallback(aPICallback));
    }

    public static void registerDevice(APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.registerDevice(createCallback(aPICallback));
    }

    public static void registerEmailClicks(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.registerEmailClick(str, createCallback(aPICallback));
    }

    public static void registerFCMOrGCMToken(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.registerGCMToken(str, str2, true, createCallback(aPICallback));
    }

    public static void removeChildFromParent(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.removeChildFromParent(str, createCallback(aPICallback));
    }

    public static void removeConnectedBlog(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.removeConnectedBlog(str, str2, createCallback(aPICallback));
    }

    public static void removeItemFromBlog(String str, APICallback<ItemResponse> aPICallback) {
        getInstance().mApiNetworkService.removeItemToBlog(str, createCallback(aPICallback));
    }

    public static void removePromptFromCollection(String str, String str2, APICallback<PromptCollectionResponse> aPICallback) {
        getInstance().mApiNetworkService.removePromptFromCollection(str, str2, createCallback(aPICallback));
    }

    public static void removePromptFromOrgLibrary(String str, APICallback<PromptResponse> aPICallback) {
        getInstance().mApiNetworkService.removePromptFromOrgLibrary(str, createCallback(aPICallback));
    }

    public static void removePromptFromSubmissionQueueOrPublicLibrary(String str, APICallback<PromptResponse> aPICallback) {
        getInstance().mApiNetworkService.removePromptFromSubmissionQueueOrLibrary(str, createCallback(aPICallback));
    }

    public static void removeTag(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.removeTag(str, str2, createCallback(aPICallback));
    }

    public static void removeTeacher(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.removeTeacher(str, str2, createCallback(aPICallback));
    }

    public static void resetPassword(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.resetPassword(str, createCallback(aPICallback));
    }

    public static void savePublicProfile(String str, String str2, String str3, String str4, String str5, APICallback<PublicProfileResponse> aPICallback) {
        Callback<APIResponse<PublicProfileResponse>> createCallback = createCallback(aPICallback);
        if (str5 == null) {
            getInstance().mApiNetworkService.savePublicProfileWithoutIcon(str, str2, str3, str4, createCallback);
        } else {
            getInstance().mApiNetworkService.savePublicProfileWithIcon(str, str2, str3, str4, new TypedFile("multipart/form-data", new File(str5)), createCallback);
        }
    }

    public static void scanAddChildQRCode(String str, APICallback<AddChildQRCodeResponse> aPICallback) {
        getInstance().mApiNetworkService.scanAddChildQRCode(str, createCallback(aPICallback));
    }

    public static void scanShortcutQRCode(String str, APICallback<ShortcutQRCodeResponse> aPICallback) {
        getInstance().mApiNetworkService.scanShortcutQRCode(str, createCallback(aPICallback));
    }

    public static void sendConversationMessage(String str, String str2, String str3, APICallback<SendConversationMessageResponse> aPICallback) {
        getInstance().mApiNetworkService.sendConversationMessage(str, str2, str3, createCallback(aPICallback));
    }

    public static void sendParentApprovalResult(String str, String str2, String str3, int i, APICallback<PendingApprovalResultResponse> aPICallback) {
        getInstance().mApiNetworkService.sendParentApprovalResult(str, str2, str3, i, createCallback(aPICallback));
    }

    public static void sendPromotionDismissEvent(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.sendPromotionDismissEvent(str, createCallback(aPICallback));
    }

    public static void sendPromotionTapEvent(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.sendPromotionTapEvent(str, createCallback(aPICallback));
    }

    public static void setProfilePicture(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.setProfilePicture(str, str2, createCallback(aPICallback));
    }

    public static void shouldRefreshBlogFeed(String str, String str2, String str3, String str4, String str5, APICallback<ShouldRefreshFeedResponse> aPICallback) {
        getInstance().mApiNetworkService.shouldRefreshBlogFeed(str, str2, str3, str4, str5, createCallback(aPICallback));
    }

    public static void shouldRefreshChildItems(String str, String str2, String str3, APICallback<ShouldRefreshFeedResponse> aPICallback) {
        getInstance().mApiNetworkService.shouldRefreshChildItems(str, str2, str3, createCallback(aPICallback));
    }

    public static void shouldRefreshClassItems(String str, String str2, String str3, FeedObjectsController.FeedApprovalStatus feedApprovalStatus, String str4, APICallback<ShouldRefreshFeedResponse> aPICallback) {
        Callback<APIResponse<ShouldRefreshFeedResponse>> createCallback = createCallback(aPICallback);
        int i = AnonymousClass5.$SwitchMap$seesaw$shadowpuppet$co$seesaw$utils$FeedObjectsController$FeedApprovalStatus[feedApprovalStatus.ordinal()];
        if (i == 1) {
            getInstance().mApiNetworkService.shouldRefreshClassItems(str, str2, str3, "all", str4, createCallback);
        } else if (i == 2) {
            getInstance().mApiNetworkService.shouldRefreshClassItems(str, str2, str3, "approved", str4, createCallback);
        } else {
            if (i != 3) {
                return;
            }
            getInstance().mApiNetworkService.shouldRefreshApprovalQueueItems(str, str2, str3, str4, createCallback);
        }
    }

    public static void startPlusTrial(APICallback<PlusInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.startPlusTrial(createCallback(aPICallback));
    }

    public static void submitPromptToOrgLibrary(String str, String str2, String str3, String str4, String str5, APICallback<PromptSubmissionResponse> aPICallback) {
        getInstance().mApiNetworkService.submitPromptToOrgLibrary(str, str2, str3, str4, str5, createCallback(aPICallback));
    }

    public static void submitPromptToPublicLibrary(String str, String str2, String str3, APICallback<PromptSubmissionResponse> aPICallback) {
        getInstance().mApiNetworkService.submitPromptToPublicLibrary(str, str2, str3, createCallback(aPICallback));
    }

    public static void translateComments(String str, String str2, APICallback<CommentsTranslationResponse> aPICallback) {
        getInstance().mApiNetworkService.translateComments(str, str2, createCallback(aPICallback));
    }

    public static void translateFamilyChatMessage(String str, double d2, String str2, APICallback<SingleCommentTranslationResponse> aPICallback) {
        getInstance().mApiNetworkService.translateFamilyChatMessage(str, d2, str2, createCallback(aPICallback));
    }

    public static void translateItemAndComments(String str, String str2, List<String> list, APICallback<ItemAndCommentsTranslationResponse> aPICallback) {
        getInstance().mApiNetworkService.translateItemAndComments(str, str2, CollectionUtils.isNotNullAndNotEmpty(list) ? Joiner.a(",").join(list) : null, null, 0.0d, createCallback(aPICallback));
    }

    public static void translateItemAndCommentsForAttachment(String str, String str2, String str3, double d2, APICallback<ItemAndCommentsTranslationResponse> aPICallback) {
        getInstance().mApiNetworkService.translateItemAndComments(str, str2, null, str3, d2, createCallback(aPICallback));
    }

    public static void translateSingleComment(String str, String str2, APICallback<SingleCommentTranslationResponse> aPICallback) {
        getInstance().mApiNetworkService.translateSingleComment(str, str2, createCallback(aPICallback));
    }

    public static void unarchiveClass(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.unarchiveClass(str, createCallback(aPICallback));
    }

    public static void unarchivePrompt(String str, APICallback<PromptResponse> aPICallback) {
        getInstance().mApiNetworkService.unarchivePrompt(str, createCallback(aPICallback));
    }

    public static void unarchiveSkill(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.unarchiveSkill(str, createCallback(aPICallback));
    }

    public static void unbookmarkPrompt(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.unbookmarkPrompt(str, createCallback(aPICallback));
    }

    public static void unflagItem(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.removeFlag(str, createCallback(aPICallback));
    }

    public static void unlikeItem(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.unlikeItem(str, str2, createCallback(aPICallback));
    }

    public static void unregisterFCMOrGCMToken(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.unregisterGCMToken(str, str2, createCallback(aPICallback));
    }

    public static void updateAccountSettings(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        Callback<APIResponse<EmptyResponse>> createCallback = createCallback(aPICallback);
        HashMap hashMap = new HashMap();
        if (str2 == null) {
            str2 = "";
        }
        hashMap.put(str, str2);
        getInstance().mApiNetworkService.updateAccountSettings(hashMap, createCallback);
    }

    public static void updateBlogHeaderPhoto(String str, String str2, APICallback<BlogResponse> aPICallback) {
        getInstance().mApiNetworkService.updateBlogHeaderPhoto(str, str2, createCallback(aPICallback));
    }

    public static void updateBlogSettings(String str, String str2, String str3, APICallback<BlogResponse> aPICallback) {
        Callback<APIResponse<BlogResponse>> createCallback = createCallback(aPICallback);
        HashMap hashMap = new HashMap();
        if (str3 == null) {
            str3 = "";
        }
        hashMap.put(str2, str3);
        getInstance().mApiNetworkService.updateBlogSettings(str, hashMap, createCallback);
    }

    public static void updateClassSettings(String str, String str2, String str3, APICallback<SingleClassResponse> aPICallback) {
        Callback<APIResponse<SingleClassResponse>> createCallback = createCallback(aPICallback);
        HashMap hashMap = new HashMap();
        if (str3 == null) {
            str3 = "";
        }
        hashMap.put(str2, str3);
        getInstance().mApiNetworkService.updateClassSettings(str, hashMap, createCallback);
    }

    public static void updateEmail(String str, String str2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.updateEmail(str, str2, createCallback(aPICallback));
    }

    public static void updateFamilyReplyEnabled(String str, boolean z, APICallback<FamilyRepliesEnabledResponse> aPICallback) {
        getInstance().mApiNetworkService.updateFamilyReplyEnabled(str, z, createCallback(aPICallback));
    }

    public static void updateItemAssessments(String str, String str2, String str3, APICallback<ItemResponse> aPICallback) {
        getInstance().mApiNetworkService.updateItemAssessments(str, str2, str3, createCallback(aPICallback));
    }

    public static void updateItemDate(String str, double d2, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.updateItemDate(str, d2, createCallback(aPICallback));
    }

    public static void updateMultipleClassSettings(String str, Map<String, String> map, APICallback<SingleClassResponse> aPICallback) {
        getInstance().mApiNetworkService.updateClassSettings(str, map, createCallback(aPICallback));
    }

    public static void updatePassword(String str, String str2, APICallback<AccountLoginResponse> aPICallback) {
        getInstance().mApiNetworkService.updatePassword(str, str2, createCallback(aPICallback));
    }

    public static void updatePushNotificationSetting(boolean z, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.updatePushNotificationSettings(z, createCallback(aPICallback));
    }

    public static void updateSeemStates(List<String> list, APICallback<EmptyResponse> aPICallback) {
        Callback<APIResponse<EmptyResponse>> createCallback = createCallback(aPICallback);
        getInstance().mApiNetworkService.updateSeemState(Joiner.a(",").join(list), createCallback);
    }

    public static void updateSkill(String str, String str2, String str3, String str4, String str5, String str6, String str7, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.updateSkill(str, str2, str3, str4, str5, str6, str7, createCallback(aPICallback));
    }

    public static void updateStudentSettings(String str, String str2, String str3, APICallback<EmptyResponse> aPICallback) {
        Callback<APIResponse<EmptyResponse>> createCallback = createCallback(aPICallback);
        HashMap hashMap = new HashMap();
        if (str3 == null) {
            str3 = "";
        }
        hashMap.put(str2, str3);
        getInstance().mApiNetworkService.updateStudentSettings(str, hashMap, createCallback);
    }

    public static void updateTag(String str, String str2, String str3, String str4, APICallback<EmptyResponse> aPICallback) {
        Callback<APIResponse<EmptyResponse>> createCallback = createCallback(aPICallback);
        HashMap hashMap = new HashMap();
        if (str4 == null) {
            str4 = "";
        }
        hashMap.put(str3, str4);
        getInstance().mApiNetworkService.updateTag(str, str2, hashMap, createCallback);
    }

    public static void upgradeAccount(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.upgradeAccount(str, createCallback(aPICallback));
    }

    public static void uploadClassIconPicture(String str, String str2, APICallback<UpdateIconPictureResponse> aPICallback) {
        getInstance().mApiNetworkService.uploadClassIconPicture(str, str2, createCallback(aPICallback));
    }

    public static void uploadItemZipFile(String str, String str2, File file, ProgressTypedFile.ProgressListener progressListener, APICallback<Item> aPICallback) {
        Callback<APIResponse<Item>> createCallback = createCallback(aPICallback);
        getInstance().mUploadNetworkService.uploadItemZipFile(str, str2, new ProgressTypedFile("application/zip", file, progressListener), createCallback);
    }

    public static void uploadProfilePicture(String str, String str2, APICallback<UpdateProfilePictureResponse> aPICallback) {
        getInstance().mApiNetworkService.uploadProfilePicture(str, str2, createCallback(aPICallback));
    }

    public static void validateJoinCode(String str, APICallback<EmptyResponse> aPICallback) {
        getInstance().mApiNetworkService.validateJoinCode(str, createCallback(aPICallback));
    }

    public static void validatePurchase(String str, String str2, APICallback<PlusInfoResponse> aPICallback) {
        getInstance().mApiNetworkService.validatePurchase(str, str2, createCallback(aPICallback));
    }

    public static void versionCheck(APICallback<VersionCheckResponse> aPICallback) {
        getInstance().mApiNetworkService.versionCheck(createCallback(aPICallback));
    }

    public String getApiEndPoint() {
        return this.mApiEndPoint;
    }

    public String getUploadEndPoint() {
        return this.mUploadEndPoint;
    }

    public void setApiEndpoint(String str) {
        this.mApiEndPoint = str;
        updateEndPoints();
    }

    public void setUploadEndpoint(String str) {
        this.mUploadEndPoint = str;
        updateEndPoints();
    }

    public void updateEndPoints() {
        SeesawApplication application = Session.getInstance().getApplication();
        String str = this.mApiEndPoint;
        if (str == null || str.length() == 0) {
            this.mApiEndPoint = application.getString(R.string.api_endpoint);
        }
        String str2 = this.mUploadEndPoint;
        if (str2 == null || str2.length() == 0) {
            this.mUploadEndPoint = application.getString(R.string.upload_endpoint);
        }
        this.mApiNetworkService = (NetworkService) buildAdapter(this.mApiEndPoint).create(NetworkService.class);
        this.mUploadNetworkService = (NetworkService) buildAdapter(this.mUploadEndPoint).create(NetworkService.class);
    }

    /* loaded from: classes2.dex */
    public static class Error {
        public final APIError apiError;
        public final RetrofitError retrofitError;

        public Error(RetrofitError retrofitError) {
            this.retrofitError = retrofitError;
            this.apiError = null;
        }

        public String getErrorMessage() {
            RetrofitError retrofitError = this.retrofitError;
            if (retrofitError != null) {
                if (retrofitError.getKind() == RetrofitError.Kind.NETWORK) {
                    return "No Internet Connection. Please make sure you are connected to the Internet and try again. " + this.retrofitError.getMessage();
                }
                return this.retrofitError.getMessage();
            }
            APIError aPIError = this.apiError;
            return aPIError != null ? aPIError.errorMessage : "Unknown Error";
        }

        public String getErrorTitle() {
            APIError aPIError = this.apiError;
            return aPIError != null ? aPIError.errorTitle : "Error";
        }

        public boolean isAPIError() {
            return this.apiError != null;
        }

        public boolean isNetworkError() {
            return this.retrofitError != null;
        }

        public ErrorSummary toSummary() {
            return new ErrorSummary(getErrorTitle(), getErrorMessage());
        }

        public Error(APIError aPIError) {
            this.apiError = aPIError;
            this.retrofitError = null;
        }
    }

    public static void addBlogComment(String str, String str2, File file, double d2, String str3, APICallback<Item> aPICallback) {
        Callback<APIResponse<Item>> createCallback = createCallback(aPICallback);
        getInstance().mApiNetworkService.addBlogComment(str, str2, new TypedFile("audio/mp4a-latm", file), Double.valueOf(d2), str3, createCallback);
    }

    public static void addComment(String str, File file, double d2, String str2, APICallback<Item> aPICallback) {
        Callback<APIResponse<Item>> createCallback = createCallback(aPICallback);
        getInstance().mApiNetworkService.addComment(str, new TypedFile("audio/mp4a-latm", file), Double.valueOf(d2), str2, createCallback);
    }

    public static void lookupBlog(String str, String str2, APICallback<BlogResponse> aPICallback) {
        getInstance().mApiNetworkService.lookupBlog(str, str2, createCallback(aPICallback));
    }

    public static void updateBlogSettings(String str, String str2, String str3, String str4, String str5, APICallback<BlogResponse> aPICallback) {
        Callback<APIResponse<BlogResponse>> createCallback = createCallback(aPICallback);
        HashMap hashMap = new HashMap();
        if (str2 == null) {
            str2 = "";
        }
        hashMap.put(str2, str3);
        if (str4 == null) {
            str4 = "";
        }
        hashMap.put(str4, str5);
        getInstance().mApiNetworkService.updateBlogSettings(str, hashMap, createCallback);
    }
}
