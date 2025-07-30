package cm.aptoide.pt.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAd;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomStoredMinimalAdPersistence;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.util.referrer.SimpleTimedFuture;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.RegisterAdRefererRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.util.ReferrerUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.schedulers.Schedulers;

/* loaded from: classes.dex */
public class ReferrerUtils extends cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils {
    private static final String TAG = "ReferrerUtils";

    public static void broadcastReferrer(String str, String str2, Context context) {
        Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
        intent.setPackage(str);
        intent.setFlags(32);
        intent.putExtra("referrer", str2);
        context.sendBroadcast(intent);
        Logger.getInstance().d(TAG, "Sent broadcast to " + str + " with referrer " + str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<Long> clearExcludedNetworks(String str) {
        return cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils.excludedNetworks.remove(str);
    }

    public static void extractReferrer(SearchAdResult searchAdResult, int i2, boolean z, AdsRepository adsRepository, OkHttpClient okHttpClient, Converter.Factory factory, QManager qManager, final Context context, SharedPreferences sharedPreferences, MinimalAdMapper minimalAdMapper, RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence) {
        String packageName = searchAdResult.getPackageName();
        long networkId = searchAdResult.getNetworkId();
        final String clickUrl = searchAdResult.getClickUrl();
        if (clickUrl == null) {
            Logger.getInstance().d("ExtractReferrer", "No click_url for packageName " + packageName);
            return;
        }
        if (!AptoideUtils.ThreadU.isUiThread()) {
            throw new RuntimeException("ExtractReferrer must be run on UI thread!");
        }
        try {
            Logger.getInstance().d("ExtractReferrer", "Called for: " + clickUrl + " with packageName " + packageName);
            final String[] strArr = {clickUrl};
            final SimpleTimedFuture simpleTimedFuture = new SimpleTimedFuture();
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2006, 16, -3);
            layoutParams.gravity = 51;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.width = 0;
            layoutParams.height = 0;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            AptoideUtils.ThreadU.runOnIoThread(new Runnable() { // from class: cm.aptoide.pt.util.g
                @Override // java.lang.Runnable
                public final void run() {
                    ReferrerUtils.lambda$extractReferrer$0(context, strArr, clickUrl, simpleTimedFuture);
                }
            });
            simpleTimedFuture.get();
            WebView webView = new WebView(context);
            webView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            linearLayout.addView(webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new AnonymousClass1(searchAdResult, i2, okHttpClient, factory, qManager, z, packageName, context, roomStoredMinimalAdPersistence, minimalAdMapper, sharedPreferences, networkId, adsRepository));
            webView.loadUrl(strArr[0]);
            windowManager.addView(linearLayout, layoutParams);
        } catch (Exception e2) {
            CrashReport.getInstance().log(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getReferrer(String str) {
        String queryParameter = Uri.parse(str).getQueryParameter("referrer");
        if (TextUtils.isEmpty(queryParameter)) {
            Logger.getInstance().v(TAG, "Didn't find any referrer: " + str);
        } else {
            Logger.getInstance().v(TAG, "Found referrer: " + queryParameter);
        }
        return queryParameter;
    }

    static /* synthetic */ void lambda$extractReferrer$0(Context context, String[] strArr, String str, SimpleTimedFuture simpleTimedFuture) {
        IdsRepository idsRepository = ((AptoideApplication) context.getApplicationContext()).getIdsRepository();
        strArr[0] = AdNetworkUtils.parseMacros(str, idsRepository.getAndroidId(), idsRepository.getUniqueIdentifier().y().b(), idsRepository.getAdvertisingId().y().b());
        simpleTimedFuture.set(strArr[0]);
        Logger.getInstance().d("ExtractReferrer", "Parsed clickUrl: " + strArr[0]);
    }

    /* renamed from: cm.aptoide.pt.util.ReferrerUtils$1, reason: invalid class name */
    class AnonymousClass1 extends WebViewClient {
        Future<Void> future;
        final /* synthetic */ MinimalAdMapper val$adMapper;
        final /* synthetic */ AdsRepository val$adsRepository;
        final /* synthetic */ boolean val$broadcastReferrer;
        final /* synthetic */ Context val$context;
        final /* synthetic */ Converter.Factory val$converterFactory;
        final /* synthetic */ OkHttpClient val$httpClient;
        final /* synthetic */ long val$networkId;
        final /* synthetic */ String val$packageName;
        final /* synthetic */ QManager val$qManager;
        final /* synthetic */ int val$retries;
        final /* synthetic */ RoomStoredMinimalAdPersistence val$roomStoredMinimalAdPersistence;
        final /* synthetic */ SearchAdResult val$searchAdResult;
        final /* synthetic */ SharedPreferences val$sharedPreferences;

        AnonymousClass1(SearchAdResult searchAdResult, int i2, OkHttpClient okHttpClient, Converter.Factory factory, QManager qManager, boolean z, String str, Context context, RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence, MinimalAdMapper minimalAdMapper, SharedPreferences sharedPreferences, long j2, AdsRepository adsRepository) {
            this.val$searchAdResult = searchAdResult;
            this.val$retries = i2;
            this.val$httpClient = okHttpClient;
            this.val$converterFactory = factory;
            this.val$qManager = qManager;
            this.val$broadcastReferrer = z;
            this.val$packageName = str;
            this.val$context = context;
            this.val$roomStoredMinimalAdPersistence = roomStoredMinimalAdPersistence;
            this.val$adMapper = minimalAdMapper;
            this.val$sharedPreferences = sharedPreferences;
            this.val$networkId = j2;
            this.val$adsRepository = adsRepository;
        }

        static /* synthetic */ MinimalAd lambda$postponeReferrerExtraction$1(Throwable th) {
            return null;
        }

        static /* synthetic */ Void lambda$postponeReferrerExtraction$5(boolean z, final SearchAdResult searchAdResult, final OkHttpClient okHttpClient, final Converter.Factory factory, final QManager qManager, final SharedPreferences sharedPreferences, final int i2, final String str, long j2, final AdsRepository adsRepository, final boolean z2, final Context context, final RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence) throws Exception {
            Logger.getInstance().d("ExtractReferrer", "Sending RegisterAdRefererRequest with value " + z);
            RegisterAdRefererRequest.of(searchAdResult.getAdId(), searchAdResult.getAppId(), searchAdResult.getClickUrl(), z, okHttpClient, factory, qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)), sharedPreferences).execute();
            Logger.getInstance().d("ExtractReferrer", "Retries left: " + i2);
            if (z) {
                ReferrerUtils.clearExcludedNetworks(str);
                return null;
            }
            cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils.excludedNetworks.add(str, j2);
            try {
                if (i2 > 0) {
                    adsRepository.getAdsFromSecondTry(str).j0(rx.l.c.a.b()).p0(new rx.m.e() { // from class: cm.aptoide.pt.util.f
                        @Override // rx.m.e
                        public final Object call(Object obj) {
                            ReferrerUtils.AnonymousClass1.lambda$postponeReferrerExtraction$1((Throwable) obj);
                            return null;
                        }
                    }).D(new rx.m.e() { // from class: cm.aptoide.pt.util.c
                        @Override // rx.m.e
                        public final Object call(Object obj) {
                            Boolean valueOf;
                            SearchAdResult searchAdResult2 = SearchAdResult.this;
                            valueOf = Boolean.valueOf(r0 != null);
                            return valueOf;
                        }
                    }).H0(new rx.m.b() { // from class: cm.aptoide.pt.util.e
                        @Override // rx.m.b
                        public final void call(Object obj) {
                            ReferrerUtils.extractReferrer(new SearchAdResult((MinimalAd) obj), i2 - 1, z2, adsRepository, okHttpClient, factory, qManager, context, sharedPreferences, new MinimalAdMapper(), roomStoredMinimalAdPersistence);
                        }
                    }, new rx.m.b() { // from class: cm.aptoide.pt.util.b
                        @Override // rx.m.b
                        public final void call(Object obj) {
                            ReferrerUtils.clearExcludedNetworks(str);
                        }
                    });
                } else {
                    ReferrerUtils.clearExcludedNetworks(str);
                }
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }

        private ScheduledFuture<Void> postponeReferrerExtraction(SearchAdResult searchAdResult, int i2, int i3, OkHttpClient okHttpClient, Converter.Factory factory, QManager qManager) {
            return postponeReferrerExtraction(searchAdResult, i2, false, i3, okHttpClient, factory, qManager.getFilters(ManagerPreferences.getHWSpecsFilter(this.val$sharedPreferences)), qManager);
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
            Logger.getInstance().d("ExtractReferrer", "Openened clickUrl: " + str);
            if (this.future == null) {
                this.future = postponeReferrerExtraction(this.val$searchAdResult, 5, this.val$retries, this.val$httpClient, this.val$converterFactory, this.val$qManager);
            }
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (this.future == null) {
                Logger.getInstance().d("ExtractReferrer", "onPageStarted potentially not called : " + str);
                this.future = postponeReferrerExtraction(this.val$searchAdResult, 5, this.val$retries, this.val$httpClient, this.val$converterFactory, this.val$qManager);
            }
            Logger.getInstance().d("ExtractReferrer", "ClickUrl redirect: " + str);
            if (str.startsWith("market://") || str.startsWith("https://play.google.com") || str.startsWith("http://play.google.com")) {
                Logger.getInstance().d("ExtractReferrer", "Clickurl landed on market");
                final String referrer = ReferrerUtils.getReferrer(str);
                if (!TextUtils.isEmpty(referrer)) {
                    Logger.getInstance().d("ExtractReferrer", "Referrer successfully extracted");
                    if (this.val$broadcastReferrer) {
                        ReferrerUtils.broadcastReferrer(this.val$packageName, referrer, this.val$context);
                    } else {
                        final RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence = this.val$roomStoredMinimalAdPersistence;
                        final MinimalAdMapper minimalAdMapper = this.val$adMapper;
                        final SearchAdResult searchAdResult = this.val$searchAdResult;
                        rx.b.t(new rx.m.a() { // from class: cm.aptoide.pt.util.d
                            @Override // rx.m.a
                            public final void call() {
                                RoomStoredMinimalAdPersistence.this.insert(minimalAdMapper.map(searchAdResult, referrer));
                            }
                        }).K(Schedulers.io()).H();
                    }
                    Future<Void> future = this.future;
                    if (future != null) {
                        future.cancel(false);
                    }
                    postponeReferrerExtraction(this.val$searchAdResult, 0, true, this.val$httpClient, this.val$converterFactory, this.val$qManager);
                }
            }
            return false;
        }

        private ScheduledFuture<Void> postponeReferrerExtraction(SearchAdResult searchAdResult, int i2, boolean z, OkHttpClient okHttpClient, Converter.Factory factory, QManager qManager) {
            return postponeReferrerExtraction(searchAdResult, i2, z, 0, okHttpClient, factory, qManager.getFilters(ManagerPreferences.getHWSpecsFilter(this.val$sharedPreferences)), qManager);
        }

        private ScheduledFuture<Void> postponeReferrerExtraction(final SearchAdResult searchAdResult, int i2, final boolean z, final int i3, final OkHttpClient okHttpClient, final Converter.Factory factory, String str, final QManager qManager) {
            Logger.getInstance().d("ExtractReferrer", "Referrer postponed " + i2 + " seconds.");
            final SharedPreferences sharedPreferences = this.val$sharedPreferences;
            final String str2 = this.val$packageName;
            final long j2 = this.val$networkId;
            final AdsRepository adsRepository = this.val$adsRepository;
            final boolean z2 = this.val$broadcastReferrer;
            final Context context = this.val$context;
            final RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence = this.val$roomStoredMinimalAdPersistence;
            return cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils.executorService.schedule(new Callable() { // from class: cm.aptoide.pt.util.a
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    ReferrerUtils.AnonymousClass1.lambda$postponeReferrerExtraction$5(z, searchAdResult, okHttpClient, factory, qManager, sharedPreferences, i3, str2, j2, adsRepository, z2, context, roomStoredMinimalAdPersistence);
                    return null;
                }
            }, i2, TimeUnit.SECONDS);
        }
    }
}
