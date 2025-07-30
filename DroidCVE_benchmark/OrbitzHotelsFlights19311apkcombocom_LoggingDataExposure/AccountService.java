package com.expedia.account;

import android.content.Context;
import android.text.TextUtils;
import com.expedia.account.data.AccountResponse;
import com.expedia.account.data.FacebookLinkResponse;
import com.expedia.account.data.GoogleSignInResponse;
import com.expedia.account.data.JoinRewardsResponse;
import com.expedia.account.data.PartialUser;
import com.expedia.account.server.ExpediaAccountApi;
import com.facebook.AccessToken;
import com.facebook.g;
import com.facebook.login.f;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import io.fabric.sdk.android.services.b.a;
import io.reactivex.n;
import io.reactivex.u;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/* loaded from: classes.dex */
public class AccountService {
    private static final String PROVIDER_FACEBOOK = "Facebook";
    private ExpediaAccountApi api;
    private String clientId;
    private Integer langId;
    private u observeOnScheduler;
    private Integer siteId;
    private u subscribeOnScheduler;

    protected AccountService(ExpediaAccountApi expediaAccountApi, Integer num, Integer num2, String str, u uVar, u uVar2) {
        this.siteId = num;
        this.langId = num2;
        this.clientId = str;
        this.api = expediaAccountApi;
        this.subscribeOnScheduler = uVar;
        this.observeOnScheduler = uVar2;
    }

    public AccountService(OkHttpClient okHttpClient, String str, int i, int i2, String str2, final String str3, u uVar, u uVar2) {
        this.siteId = Integer.valueOf(i);
        this.langId = Integer.valueOf(i2);
        this.clientId = str2;
        this.subscribeOnScheduler = uVar;
        this.observeOnScheduler = uVar2;
        if (!str.startsWith("https")) {
            throw new IllegalArgumentException("Must use an HTTPS endpoint");
        }
        OkHttpClient.Builder newBuilder = okHttpClient.newBuilder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        newBuilder.addInterceptor(httpLoggingInterceptor);
        if (!TextUtils.isEmpty(str3)) {
            newBuilder.addInterceptor(new Interceptor() { // from class: com.expedia.account.AccountService.1
                @Override // okhttp3.Interceptor
                public Response intercept(Interceptor.Chain chain) {
                    Request.Builder newBuilder2 = chain.request().newBuilder();
                    newBuilder2.addHeader(a.HEADER_USER_AGENT, str3);
                    return chain.proceed(newBuilder2.build());
                }
            });
        }
        this.api = (ExpediaAccountApi) new Retrofit.Builder().baseUrl(str).client(newBuilder.build()).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(ExpediaAccountApi.class);
    }

    public n<AccountResponse> signIn(String str, String str2, String str3) {
        return this.api.signIn(str, str2, true, str3, getCommonParams()).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<AccountResponse> signInProfileOnly() {
        return this.api.signInProfileOnly(true, getCommonParams()).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<AccountResponse> createUser(PartialUser partialUser) {
        return this.api.createUser(partialUser.email, partialUser.password, partialUser.firstName, partialUser.lastName, partialUser.expediaEmailOptIn, true, partialUser.enrollInLoyalty, partialUser.recaptchaResponseToken, getCommonParams()).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<JoinRewardsResponse> joinRewards() {
        return this.api.joinRewards().subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    private Map<String, String> getCommonParams() {
        HashMap hashMap = new HashMap();
        hashMap.put("langid", Integer.toString(this.langId.intValue()));
        hashMap.put("siteid", Integer.toString(this.siteId.intValue()));
        hashMap.put("clientid", this.clientId);
        hashMap.put("sourceType", "mobileapp");
        return hashMap;
    }

    public n<FacebookLinkResponse> facebookAutoLogin(String str, String str2) {
        return this.api.facebookAutoLogin(PROVIDER_FACEBOOK, str, str2).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<FacebookLinkResponse> facebookLinkNewAccount(String str, String str2, String str3) {
        return this.api.facebookLinkNewAccount(PROVIDER_FACEBOOK, str, str2, str3).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<FacebookLinkResponse> facebookLinkExistingAccount(String str, String str2, String str3, String str4) {
        return this.api.facebookLinkExistingAccount(PROVIDER_FACEBOOK, str, str2, str3, str4).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<GoogleSignInResponse> googleSignIn(String str, String str2) {
        return this.api.googleSignIn(str, str2).subscribeOn(this.subscribeOnScheduler).observeOn(this.observeOnScheduler);
    }

    public n<FacebookLinkResponse> facebookReauth(Context context) {
        g.a(context);
        AccessToken a2 = AccessToken.a();
        if (a2 == null || a2.l() || a2.g().contains("email")) {
            return n.empty();
        }
        return facebookAutoLogin(a2.k(), a2.d());
    }

    public void facebookLogOut() {
        if (g.a()) {
            f.a().b();
        }
    }

    public void googleLogOut(Context context) {
        com.google.android.gms.auth.api.signin.a.a(context, new GoogleSignInOptions.a(GoogleSignInOptions.f).d()).b();
    }

    public boolean isFacebookSignedIn(Context context) {
        g.a(context);
        return AccessToken.a() != null;
    }

    public boolean isGoogleSignedIn(Context context) {
        return com.google.android.gms.auth.api.signin.a.a(context) != null;
    }
}
