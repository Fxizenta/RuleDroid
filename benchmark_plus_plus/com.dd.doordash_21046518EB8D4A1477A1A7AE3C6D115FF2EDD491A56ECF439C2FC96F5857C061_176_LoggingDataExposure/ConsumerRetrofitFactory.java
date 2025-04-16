package com.dd.doordash.network;

import android.content.SharedPreferences;
import com.dd.doordash.base.models.DeliveryTime;
import com.dd.doordash.network.managers.AccountManager;
import com.dd.doordash.network.managers.PapertrailManager;
import com.dd.doordash.network.serializers.DateDeserializer;
import com.dd.doordash.network.serializers.DeliveryTimeDeserializer;
import com.dd.doordash.network.tracking.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.newrelic.agent.android.instrumentation.okhttp3.OkHttp3Instrumentation;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* loaded from: classes.dex */
public class ConsumerRetrofitFactory {
    private static final String BASE_URL_DRS_LOCAL = "http://4484e0f0.ngrok.io/";
    private static final String BASE_URL_DRS_PROD = "https://drs.doordash.com/";
    private static final String BASE_URL_DRS_STAGING = "https://drs.doorcrawl.com/";
    public static final String BASE_URL_SANDBOX4 = "https://api-sandbox4.doorcrawl.com/";
    private static final String DD_IDS_IDENTIFIER = "dd-ids";
    private static final String SP_API_BASE_URL_IDENTIFIER = "sp-api-base-url-identifier";
    private AccountManager accountManager;
    private Gson gson = createGson();
    private PapertrailManager papertrailManager;
    private SharedPreferences sharedPreferences;
    private Tracker tracker;
    public static final Object AUTH_HEADER_VALUE = "JWT";
    public static final String BASE_URL_PROD = "https://api.doordash.com/";
    public static final String BASE_URL_STAGING = "https://api.doorcrawl.com/";
    public static final String BASE_URL_SANDBOX0 = "https://api-sandbox0.doorcrawl.com/";
    public static final String BASE_URL_SANDBOX1 = "https://api-sandbox1.doorcrawl.com/";
    public static final String BASE_URL_SANDBOX2 = "https://api-sandbox2.doorcrawl.com/";
    private static final String BASE_URL_LOCAL = "http://api.doordash.dev/";
    public static final String[] BASE_URL_ENDPOINT = {BASE_URL_PROD, BASE_URL_STAGING, BASE_URL_SANDBOX0, BASE_URL_SANDBOX1, BASE_URL_SANDBOX2, BASE_URL_LOCAL};
    public static final List<String> BASE_URL_ENDPOINT_LIST = Arrays.asList(BASE_URL_ENDPOINT);

    @Inject
    public ConsumerRetrofitFactory(AccountManager accountManager, PapertrailManager papertrailManager, SharedPreferences sharedPreferences, Tracker tracker) {
        this.accountManager = accountManager;
        this.papertrailManager = papertrailManager;
        this.tracker = tracker;
        this.sharedPreferences = sharedPreferences;
    }

    private Gson createGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").registerTypeAdapter(Date.class, new DateDeserializer()).registerTypeAdapter(new TypeToken<ArrayList<DeliveryTime>>() { // from class: com.dd.doordash.network.ConsumerRetrofitFactory.1
        }.getType(), new DeliveryTimeDeserializer()).serializeNulls().create();
    }

    private Request appendTrackingHeaders(Request input) {
        Request.Builder header = input.newBuilder().header("User-Agent", "DoorDashConsumer/Android").header("Client-Version", NetworkUtils.getClientVersionInfo()).header(DD_IDS_IDENTIFIER, this.tracker.getDdIds());
        if (header instanceof Request.Builder) {
            Request result = OkHttp3Instrumentation.build(header);
            return result;
        }
        Request result2 = header.build();
        return result2;
    }

    private Request appendAuthHeader(Request input, boolean auth) {
        String sessionToken = this.accountManager.getAuthToken();
        if (!auth || sessionToken == null || sessionToken.isEmpty()) {
            return input;
        }
        String authHeader = String.format("%s %s", AUTH_HEADER_VALUE, sessionToken);
        Request.Builder header = input.newBuilder().header(HttpRequest.HEADER_AUTHORIZATION, authHeader);
        if (header instanceof Request.Builder) {
            Request result = OkHttp3Instrumentation.build(header);
            return result;
        }
        Request result2 = header.build();
        return result2;
    }

    public Retrofit createRetrofit(boolean auth) {
        if (this.gson == null) {
            return null;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(ConsumerRetrofitFactory$$Lambda$1.lambdaFactory$(this, auth));
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        builder.addInterceptor(ConsumerRetrofitFactory$$Lambda$2.lambdaFactory$(this, auth));
        String apiBaseUrl = getBaseUrl();
        return new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(this.gson)).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(apiBaseUrl).client(builder.build()).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Response lambda$createRetrofit$0(boolean auth, Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request processedRequest = appendTrackingHeaders(originalRequest);
        return chain.proceed(appendAuthHeader(processedRequest, auth));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Response lambda$createRetrofit$1(boolean auth, Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.isSuccessful()) {
            ResponseBody body = originalResponse.body();
            int responseCode = originalResponse.code();
            if (body != null) {
                this.papertrailManager.logError(String.format("REST Failure.  URL: %s.  Status Code: %d", chain.request().url().toString(), Integer.valueOf(responseCode)));
            }
            if (auth && responseCode == 401) {
                this.accountManager.refreshCache();
            }
        } else {
            String ddIds = originalResponse.header(Tracker.DD_ID_EXTRAS);
            this.tracker.saveDdIds(ddIds);
        }
        return originalResponse;
    }

    public String getBaseUrl() {
        return this.sharedPreferences.getString(SP_API_BASE_URL_IDENTIFIER, getDefaultBaseUrl());
    }

    public void setBaseUrl(String newValue) {
        this.sharedPreferences.edit().putString(SP_API_BASE_URL_IDENTIFIER, newValue).apply();
    }

    public void toggleBaseUrl() {
        String baseUrl = getBaseUrl();
        int pos = BASE_URL_ENDPOINT_LIST.indexOf(baseUrl);
        setBaseUrl(BASE_URL_ENDPOINT_LIST.get((pos + 1) % BASE_URL_ENDPOINT_LIST.size()));
    }

    public Retrofit createMapsApiRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(this.gson)).baseUrl("https://maps.googleapis.com").client(client).build();
    }

    public Retrofit createDrsApiRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(ConsumerRetrofitFactory$$Lambda$3.lambdaFactory$(this));
        OkHttpClient client = builder.addInterceptor(interceptor).build();
        return new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(this.gson)).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(BASE_URL_DRS_PROD).client(client).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Response lambda$createDrsApiRetrofit$2(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request processedRequest = appendTrackingHeaders(originalRequest);
        return chain.proceed(appendAuthHeader(processedRequest, true));
    }

    private String getDefaultBaseUrl() {
        return "prod".equals("local") ? BASE_URL_LOCAL : BASE_URL_PROD;
    }
}
