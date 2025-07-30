package net.rapidgator.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import net.rapidgator.R;
import net.rapidgator.database.models.Property;
import net.rapidgator.server.models.CheckLinkObject;
import net.rapidgator.server.models.FileDownloadObject;
import net.rapidgator.server.models.FileObject;
import net.rapidgator.server.models.FileResultObject;
import net.rapidgator.server.models.FilesObject;
import net.rapidgator.server.models.FolderObject;
import net.rapidgator.server.models.JobsObject;
import net.rapidgator.server.models.PremiumTariffsObject;
import net.rapidgator.server.models.PurchaseObject;
import net.rapidgator.server.models.ResultObject;
import net.rapidgator.server.models.SettingsListObject;
import net.rapidgator.server.models.SettingsUpdateObject;
import net.rapidgator.server.models.UploadObject;
import net.rapidgator.server.models.UserObject;
import net.rapidgator.server.models.WhiteIpListObject;
import net.rapidgator.utils.LocalStorage;
import net.rapidgator.utils.ServerApiThrowable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/* loaded from: classes.dex */
public class AppServerApi {
    private static final int FIRST_SERVER_ERROR_CODE = 400;
    private final Context context;
    private final String internetConnectionError;
    private final String moveFolderError;
    private final ServerApi serverApi;
    private final LocalStorage storage;

    public static boolean isValidatedStatus(int i) {
        return i < FIRST_SERVER_ERROR_CODE;
    }

    public AppServerApi(Context context, ServerApi serverApi, LocalStorage localStorage) {
        this.context = context;
        this.serverApi = serverApi;
        this.storage = localStorage;
        this.internetConnectionError = context.getString(R.string.app_internet_connection_error);
        this.moveFolderError = context.getString(R.string.files_move_folder_error);
    }

    public Call<UserObject> userLoginSync(String str, String str2) {
        return this.serverApi.userLoginSync(str, str2);
    }

    public Observable<UserObject> userLogin(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.userLogin(str, str2).compose(applySchedulers()).map(AppServerApi$$Lambda$1.lambdaFactory$(this));
    }

    public /* synthetic */ UserObject lambda$userLogin$0(UserObject userObject) {
        saveUserInfo(this.storage, userObject);
        return userObject;
    }

    public Call<UserObject> userGoogleLoginSync(String str) {
        return this.serverApi.userGoogleLoginSync(str);
    }

    public Observable<UserObject> userGoogleLogin(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.userGoogleLogin(str).compose(applySchedulers()).map(AppServerApi$$Lambda$2.lambdaFactory$(this));
    }

    public /* synthetic */ UserObject lambda$userGoogleLogin$1(UserObject userObject) {
        saveUserInfo(this.storage, userObject);
        return userObject;
    }

    public Observable<UserObject> userInfo() {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.userInfo(this.storage.getToken()).compose(applySchedulers()).map(AppServerApi$$Lambda$3.lambdaFactory$(this));
    }

    public /* synthetic */ UserObject lambda$userInfo$2(UserObject userObject) {
        saveUserInfo(this.storage, userObject);
        return userObject;
    }

    public Observable<PremiumTariffsObject> getPremiumTariffs(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.getPremiumTariffs(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FolderObject> folderCreate(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.folderCreate(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<FolderObject> folderInfo(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.folderInfo(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FolderObject> folderContent(String str, int i, int i2, String str2, String str3) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.folderContent(this.storage.getToken(), str, i, i2, str2, str3).compose(applySchedulers());
    }

    public Observable<FolderObject> folderRename(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.folderRename(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<ResultObject> folderCopy(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.folderCopy(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<ResultObject> folderMove(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        if (str.equals(str2)) {
            return Observable.error(new ServerApiThrowable(this.moveFolderError));
        }
        return this.serverApi.folderMove(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<ResultObject> folderDelete(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.folderDelete(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FileResultObject> fileUpload(String str, String str2, long j, String str3) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileUpload(this.storage.getToken(), str, str2, j, str3);
    }

    public Observable<UploadObject> fileUploadBin(String str, MultipartBody.Part part) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileUploadBin(str, part);
    }

    public Observable<FileResultObject> fileUploadInfo(long j) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileUploadInfo(this.storage.getToken(), j).compose(applySchedulers());
    }

    public Observable<FileDownloadObject> fileDownload(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileDownload(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FileDownloadObject> fileUrlDownload(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileUrlDownload(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FileObject> fileInfo(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileInfo(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FileObject> fileRename(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileRename(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<ResultObject> fileCopy(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileCopy(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<FileObject> fileXCopy(String str, String str2, String str3) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileXCopy(this.storage.getToken(), str, str2, str3).compose(applySchedulers());
    }

    public Observable<FileObject> fileHashCopy(String str, String str2, String str3) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileHashCopy(this.storage.getToken(), str, str2, str3).compose(applySchedulers());
    }

    public Observable<ResultObject> fileMove(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileMove(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<ResultObject> fileDelete(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileDelete(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FileObject> fileChangeMode(String str, int i) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileChangeMode(this.storage.getToken(), str, i).compose(applySchedulers());
    }

    public Observable<CheckLinkObject> fileCheckLink(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileCheckLink(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<FilesObject> fileSearch(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.fileSearch(this.storage.getToken(), str, str2).compose(applySchedulers());
    }

    public Observable<JobsObject> remoteCreate(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.remoteCreate(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<JobsObject> remoteInfo(long j) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.remoteInfo(this.storage.getToken(), j).compose(applySchedulers());
    }

    public Observable<ResultObject> remoteDelete(long j) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.remoteDelete(this.storage.getToken(), j).compose(applySchedulers());
    }

    public Observable<SettingsListObject> settingsList() {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.settingsList(this.storage.getToken()).map(AppServerApi$$Lambda$4.lambdaFactory$(this)).compose(applySchedulers());
    }

    public /* synthetic */ SettingsListObject lambda$settingsList$3(SettingsListObject settingsListObject) {
        try {
            saveSettings(settingsListObject);
        } catch (Exception unused) {
        }
        return settingsListObject;
    }

    public Observable<SettingsUpdateObject> settingsUpdate(String str, String str2) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.settingsUpdate(this.storage.getToken(), str, str2).map(AppServerApi$$Lambda$5.lambdaFactory$(this)).compose(applySchedulers());
    }

    public /* synthetic */ SettingsUpdateObject lambda$settingsUpdate$4(SettingsUpdateObject settingsUpdateObject) {
        saveProperty(settingsUpdateObject.getProperty());
        return settingsUpdateObject;
    }

    public Observable<WhiteIpListObject> whiteIpList() {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.whiteIpList(this.storage.getToken()).compose(applySchedulers());
    }

    public Observable<WhiteIpListObject> whiteIpCreate(String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.whiteIpCreate(this.storage.getToken(), str).compose(applySchedulers());
    }

    public Observable<WhiteIpListObject> whiteIpUpdate(long j, String str) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.whiteIpUpdate(this.storage.getToken(), j, str).compose(applySchedulers());
    }

    public Observable<ResultObject> whiteIpDelete(long j) {
        if (!isOnline()) {
            return Observable.error(new ServerApiThrowable(this.internetConnectionError));
        }
        return this.serverApi.whiteIpDelete(this.storage.getToken(), j).compose(applySchedulers());
    }

    public Call<ResponseBody> downloadFile(String str) {
        return this.serverApi.downloadFile(str);
    }

    public Observable<PurchaseObject> googlePurchase(String str, String str2, String str3, String str4) {
        return this.serverApi.googlePurchase(this.storage.getToken(), str, str2, str3, str4).compose(applySchedulers());
    }

    public static void saveUserInfo(LocalStorage localStorage, UserObject userObject) {
        if (!isValidatedStatus(userObject.getStatus())) {
            Observable.error(new ServerApiThrowable(userObject.getDetails()));
            return;
        }
        if (userObject.getToken() != null) {
            localStorage.setToken(userObject.getToken());
        }
        localStorage.setEmail(userObject.getEmail());
        localStorage.setPremium(userObject.isPremium());
        localStorage.setPremiumEndTime(userObject.getPremiumEndTime());
        localStorage.setState(userObject.getState());
        localStorage.setStateLabel(userObject.getStateLabel());
        localStorage.setStorageLeft(userObject.getStorageLeft());
        localStorage.setStorageTotal(userObject.getStorageTotal());
        localStorage.setTrafficLeft(userObject.getTrafficLeft());
        localStorage.setTrafficTotal(userObject.getTrafficTotal());
    }

    private void saveProperty(Property property) {
        switch (property.getProperty()) {
            case 1:
                this.storage.setNoticeTerminationOnFileStorage(property.getBoolValue());
                return;
            case 2:
                this.storage.setNoticeFilesDeletion(property.getBoolValue());
                return;
            case 3:
                this.storage.setEnableCopyToMyFiles(property.getBoolValue());
                return;
            case 4:
                this.storage.setSubscriptionEnabled(property.getBoolValue());
                return;
            case 5:
                this.storage.setAutorenewOfPremiumSwitch(property.getBoolValue());
                return;
            case 6:
                this.storage.setEmail(property.getValue());
                return;
            case 7:
                this.storage.setPassword(property.getValue());
                return;
            default:
                return;
        }
    }

    private void saveSettings(SettingsListObject settingsListObject) {
        this.storage.setNoticeTerminationOnFileStorage(settingsListObject.getProperty(1).getBoolValue());
        this.storage.setNoticeFilesDeletion(settingsListObject.getProperty(2).getBoolValue());
        this.storage.setEnableCopyToMyFiles(settingsListObject.getProperty(3).getBoolValue());
        this.storage.setAutorenewOfPremiumSwitch(settingsListObject.getProperty(5).getBoolValue());
        this.storage.setSubscriptionEnabled(settingsListObject.getProperty(4).getBoolValue());
    }

    public boolean isOnline() {
        return isOnline(this.context);
    }

    public static boolean isOnline(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private static <T extends Response<R>, R> Observable.Transformer<T, R> applyExepionCathcer() {
        Observable.Transformer<T, R> transformer;
        transformer = AppServerApi$$Lambda$6.instance;
        return transformer;
    }

    public static /* synthetic */ Observable lambda$applyExepionCathcer$6(Observable observable) {
        Func1 func1;
        func1 = AppServerApi$$Lambda$8.instance;
        return observable.flatMap(func1);
    }

    public static /* synthetic */ Observable lambda$null$5(Response response) {
        if (!response.isSuccessful()) {
            return Observable.error(new Throwable());
        }
        return Observable.just(response.body());
    }

    private static <T> Observable.Transformer<T, T> applySchedulers() {
        Observable.Transformer<T, T> transformer;
        transformer = AppServerApi$$Lambda$7.instance;
        return transformer;
    }

    public static /* synthetic */ Observable lambda$applySchedulers$7(Observable observable) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
