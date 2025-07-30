package net.rapidgator.application.module;

import android.content.Context;
import com.google.gson.Gson;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import net.rapidgator.R;
import net.rapidgator.application.RapidApplication;
import net.rapidgator.server.AppServerApi;
import net.rapidgator.server.ServerApi;
import net.rapidgator.server.utils.ReLoginAuthenticator;
import net.rapidgator.utils.GoogleSignInApi;
import net.rapidgator.utils.LocalStorage;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
/* loaded from: classes.dex */
public final class ApiModule {
    private static final int DISK_CACHE_SIZE = 10485760;
    private static final int TIMEOUT = 15;
    private ReLoginAuthenticator reLoginAuthenticator;

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public Picasso providesPicasso(Context context, ExecutorService executorService, OkHttpClient okHttpClient) {
        return new Picasso.Builder(context).downloader(new OkHttp3Downloader(okHttpClient)).executor(executorService).build();
    }

    @Provides
    @Singleton
    public ServerApi provideServerApi(OkHttpClient okHttpClient, ExecutorService executorService, Gson gson) {
        return (ServerApi) toServerApi(okHttpClient, executorService, gson).create(ServerApi.class);
    }

    @Provides
    @Singleton
    public AppServerApi provideAppServerApi(Context context, ServerApi serverApi, LocalStorage localStorage) {
        AppServerApi appServerApi = new AppServerApi(context, serverApi, localStorage);
        this.reLoginAuthenticator.setServerApi(appServerApi);
        return appServerApi;
    }

    @Provides
    @Singleton
    public GoogleSignInApi provideGoogleSignInApi(Context context) {
        return new GoogleSignInApi(context);
    }

    @Provides
    @Singleton
    public OkHttpClient provideHttpClient(Context context, LocalStorage localStorage) {
        HttpLoggingInterceptor.Logger logger;
        Cache cache = new Cache(new File(context.getCacheDir(), "cached"), 10485760L);
        this.reLoginAuthenticator = new ReLoginAuthenticator(localStorage);
        logger = ApiModule$$Lambda$1.instance;
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(logger);
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().connectTimeout(15L, TimeUnit.SECONDS).readTimeout(15L, TimeUnit.SECONDS).writeTimeout(15L, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor).authenticator(this.reLoginAuthenticator).cache(cache).build();
    }

    private Retrofit toServerApi(OkHttpClient okHttpClient, ExecutorService executorService, Gson gson) {
        return new Retrofit.Builder().baseUrl(RapidApplication.getResString(R.string.apiUrl)).client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.from(executorService))).callbackExecutor(executorService).build();
    }
}
