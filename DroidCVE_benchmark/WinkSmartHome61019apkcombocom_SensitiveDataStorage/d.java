package com.twitter.sdk.android.core.internal.oauth;

import android.net.Uri;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.i;
import io.fabric.sdk.android.services.network.h;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.net.ssl.SSLSocketFactory;
import retrofit.c.f;

/* compiled from: OAuth1aService.java */
/* loaded from: classes2.dex */
public final class d extends e {

    /* renamed from: a, reason: collision with root package name */
    a f6377a;

    /* compiled from: OAuth1aService.java */
    /* loaded from: classes2.dex */
    interface a {
    }

    public d(i iVar, SSLSocketFactory sSLSocketFactory, com.twitter.sdk.android.core.internal.a aVar) {
        super(iVar, sSLSocketFactory, aVar);
        this.f6377a = (a) this.e.a(a.class);
    }

    public static String a(TwitterAuthConfig twitterAuthConfig) {
        return Uri.parse("twittersdk://callback").buildUpon().appendQueryParameter("version", "1.0.0.15").appendQueryParameter("app", twitterAuthConfig.f6321a).build().toString();
    }

    public static String a(TwitterAuthConfig twitterAuthConfig, TwitterAuthToken twitterAuthToken, String str, String str2, String str3, Map<String, String> map) {
        c cVar = new c(twitterAuthConfig, twitterAuthToken, str, str2, str3, map);
        String str4 = String.valueOf(System.nanoTime()) + String.valueOf(Math.abs(c.f6375a.nextLong()));
        String l = Long.toString(System.currentTimeMillis() / 1000);
        URI create = URI.create(cVar.f);
        TreeMap<String, String> a2 = h.a(create, true);
        if (cVar.g != null) {
            a2.putAll(cVar.g);
        }
        if (cVar.d != null) {
            a2.put("oauth_callback", cVar.d);
        }
        a2.put("oauth_consumer_key", cVar.f6376b.f6321a);
        a2.put("oauth_nonce", str4);
        a2.put("oauth_signature_method", "HMAC-SHA1");
        a2.put("oauth_timestamp", l);
        if (cVar.c != null && cVar.c.f6323a != null) {
            a2.put("oauth_token", cVar.c.f6323a);
        }
        a2.put("oauth_version", "1.0");
        String a3 = cVar.a(cVar.e.toUpperCase(Locale.ENGLISH) + '&' + h.b(create.getScheme() + "://" + create.getHost() + create.getPath()) + '&' + c.a(a2));
        StringBuilder sb = new StringBuilder("OAuth");
        c.a(sb, "oauth_callback", cVar.d);
        c.a(sb, "oauth_consumer_key", cVar.f6376b.f6321a);
        c.a(sb, "oauth_nonce", str4);
        c.a(sb, "oauth_signature", a3);
        c.a(sb, "oauth_signature_method", "HMAC-SHA1");
        c.a(sb, "oauth_timestamp", l);
        c.a(sb, "oauth_token", cVar.c != null ? cVar.c.f6323a : null);
        c.a(sb, "oauth_version", "1.0");
        return sb.substring(0, sb.length() - 1);
    }

    public final com.twitter.sdk.android.core.c<f> a(final com.twitter.sdk.android.core.c<OAuthResponse> cVar) {
        return new com.twitter.sdk.android.core.c<f>() { // from class: com.twitter.sdk.android.core.internal.oauth.d.1
            @Override // com.twitter.sdk.android.core.c
            public final void a(com.twitter.sdk.android.core.f<f> fVar) {
                BufferedReader bufferedReader;
                long j;
                OAuthResponse oAuthResponse = null;
                StringBuilder sb = new StringBuilder();
                try {
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(fVar.f6331a.e.P_()));
                        while (true) {
                            try {
                                String readLine = bufferedReader.readLine();
                                if (readLine == null) {
                                    break;
                                } else {
                                    sb.append(readLine);
                                }
                            } catch (Throwable th) {
                                th = th;
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                                throw th;
                            }
                        }
                        bufferedReader.close();
                        String sb2 = sb.toString();
                        TreeMap<String, String> a2 = h.a(sb2, false);
                        String str = a2.get("oauth_token");
                        String str2 = a2.get("oauth_token_secret");
                        String str3 = a2.get("screen_name");
                        if (a2.containsKey("user_id")) {
                            j = Long.parseLong(a2.get("user_id"));
                        } else {
                            j = 0;
                        }
                        if (str != null && str2 != null) {
                            oAuthResponse = new OAuthResponse(new TwitterAuthToken(str, str2), str3, j);
                        }
                        if (oAuthResponse == null) {
                            cVar.a(new TwitterAuthException("Failed to parse auth response: " + sb2));
                        } else {
                            cVar.a(new com.twitter.sdk.android.core.f(oAuthResponse, null));
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedReader = null;
                    }
                } catch (IOException e) {
                    cVar.a(new TwitterAuthException(e.getMessage(), e));
                }
            }

            @Override // com.twitter.sdk.android.core.c
            public final void a(TwitterException twitterException) {
                cVar.a(twitterException);
            }
        };
    }
}
