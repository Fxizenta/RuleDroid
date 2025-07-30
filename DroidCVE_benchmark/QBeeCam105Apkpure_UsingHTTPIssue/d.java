package com.vestiacom.qbeecamera.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import b.ab;
import b.ac;
import b.t;
import b.w;
import b.z;
import com.c.b.p;
import com.c.b.v;
import com.vestiacom.commonutilityandroid.app.AppHolder;
import com.vestiacom.commonutilityandroid.log.Log;
import com.vestiacom.qbeecamera.R;
import com.vestiacom.qbeecamera.http.responses.retrofit.BaseResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.CameraConfigResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.CameraEventResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.CameraResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.CameraSettingsResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.CameraTechnicalDetailsResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.EventListsPageResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.QBeeAuthorizationResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.RecordingStatusResponse;
import com.vestiacom.qbeecamera.http.responses.retrofit.UserInfoResponse;
import com.vestiacom.qbeecamera.model.retrofit.Camera;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* compiled from: RetrofitAPIHelper.java */
/* loaded from: classes.dex */
public class d {

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public interface p {
        String getErrorCode();

        String getStatus();
    }

    static /* synthetic */ String b() {
        return c();
    }

    public static Retrofit a(String str, boolean z) {
        w.a aVar = new w.a();
        aVar.a(z ? new b() : new k());
        return new Retrofit.Builder().baseUrl(str).addConverterFactory(GsonConverterFactory.create()).client(aVar.a()).build();
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static abstract class n implements t {
        protected z.a a(t.a aVar) {
            z a2 = aVar.a();
            z.a e = a2.e();
            e.a(a2.b(), a2.d());
            return e;
        }

        @Override // b.t
        public ab b(t.a aVar) throws IOException {
            return aVar.a(a(aVar).a());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class k extends n {
        @Override // com.vestiacom.qbeecamera.http.d.n
        protected z.a a(t.a aVar) {
            return super.a(aVar).b("Content-Type", "application/json;charset=UTF-8");
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class b extends k {
        @Override // com.vestiacom.qbeecamera.http.d.k, com.vestiacom.qbeecamera.http.d.n
        protected z.a a(t.a aVar) {
            return super.a(aVar).b("Cookie", "JSESSIONID=" + d.b());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class l implements com.c.b.p {
        @Override // com.c.b.p
        public v a(p.a aVar) throws IOException {
            return aVar.a(aVar.a().g().a("Cookie", "JSESSIONID=" + d.b()).a().b());
        }
    }

    public static ServerRetrofitInterface a(boolean z) {
        return (ServerRetrofitInterface) a(com.vestiacom.qbeecamera.http.a.a() + "/", z).create(ServerRetrofitInterface.class);
    }

    public static ServerRetrofitInterface a() {
        return a(true);
    }

    public static ServerRetrofitInterface a(int i2) {
        return (ServerRetrofitInterface) a(String.format(AppHolder.getString(R.string.qbee_server_webdis_uri), Integer.valueOf(i2)) + "/", true).create(ServerRetrofitInterface.class);
    }

    public static void a(Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a(false).authenthicate(map).enqueue(new a(eVar, map.get("username"), map.get("password")));
    }

    public static void b(Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a(false).register(map).enqueue(new o(eVar));
    }

    public static void c(Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a(false).resetPassword(map).enqueue(new o(eVar));
    }

    public static void d(Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a(false).register(map).enqueue(new o(eVar));
    }

    public static void e(Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().changeUserPassword(map).enqueue(new o(eVar));
    }

    public static void a(com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().removeAccount().enqueue(new o(eVar));
    }

    public static void b(com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().getUserInfo().enqueue(new q(eVar));
    }

    public static void c(com.vestiacom.qbeecamera.http.e<List<Camera>> eVar) {
        a().getCameras().enqueue(new e(eVar));
    }

    public static void f(Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().addCamera(map).enqueue(new o(eVar));
    }

    public static void a(int i2, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().removeCamera(Integer.toString(i2)).enqueue(new o(eVar));
    }

    public static void a(int i2, int i3, String str, String str2, String str3, List<String> list, com.vestiacom.qbeecamera.http.e<EventListsPageResponse> eVar) {
        Log.w("RetrofitAPIHelper", "EVENTS PAGES", "getEventsPage request");
        a().getEventPage(i2, i3, TextUtils.isEmpty(str) ? null : str, str2, str3, list).enqueue(new j(eVar));
    }

    public static String a(int i2, boolean z) {
        return com.vestiacom.qbeecamera.http.a.a() + String.format(z ? "/event/image?eventId=%d" : "/event/video?eventId=%d", Integer.valueOf(i2));
    }

    public static String b(int i2) {
        return com.vestiacom.qbeecamera.http.a.a() + String.format("/event/video?eventId=%d&JSESSIONID=%s", Integer.valueOf(i2), c());
    }

    public static void a(int i2, String str, com.vestiacom.qbeecamera.http.e<com.vestiacom.qbeecamera.model.f> eVar) {
        a().getCameraSettings(Integer.toString(i2), str).enqueue(new h(eVar));
    }

    public static void a(int i2, com.vestiacom.qbeecamera.model.f fVar, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        Call<BaseResponse> updateCameraSettings = a().updateCameraSettings(Integer.toString(i2), fVar.b());
        Log.d("RetrofitAPIHelper", "jsonObject cam settings: " + fVar.b());
        updateCameraSettings.enqueue(new o(eVar));
    }

    public static void a(int i2, String str, String str2, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().updateCameraSetting(Integer.toString(i2), str, str2).enqueue(new o(eVar));
    }

    public static void b(int i2, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().cameraSnapshot(Integer.toString(i2)).enqueue(new o(eVar));
    }

    public static void d(com.vestiacom.qbeecamera.http.e<Map<Integer, Boolean>> eVar) {
        a().getRecordingStatus().enqueue(new m(eVar));
    }

    public static void a(int i2, boolean z, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().updateCameraRecording(Integer.toString(i2), z ? "true" : "false").enqueue(new o(eVar));
    }

    public static void c(int i2, com.vestiacom.qbeecamera.http.e<Bitmap> eVar) {
        a().getCameraLastImage(Integer.toString(i2)).enqueue(new C0099d(eVar));
    }

    public static <V> void a(int i2, Map<String, V> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a(i2).updateCameraLocalConfig(map).enqueue(new o(eVar));
    }

    public static void d(int i2, com.vestiacom.qbeecamera.http.e<com.vestiacom.qbeecamera.model.a> eVar) {
        a(i2).getCameraLocalConfig().enqueue(new f(eVar));
    }

    public static void e(int i2, com.vestiacom.qbeecamera.http.e<CameraTechnicalDetailsResponse> eVar) {
        a(i2).getCameraTechnicalDetails().enqueue(new i(eVar));
    }

    public static void f(int i2, com.vestiacom.qbeecamera.http.e<com.vestiacom.qbeecamera.model.b> eVar) {
        a(i2).getCameraLocalEvent().enqueue(new g(eVar));
    }

    public static void a(boolean z, int i2, Map<String, String> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        Call<BaseResponse> removePushForCamera;
        ServerRetrofitInterface a2 = a();
        String valueOf = String.valueOf(i2);
        if (z) {
            removePushForCamera = a2.setPushForCamera(valueOf, map);
        } else {
            removePushForCamera = a2.removePushForCamera(valueOf, map);
        }
        removePushForCamera.enqueue(new o(eVar));
    }

    public static void g(Map<String, int[]> map, com.vestiacom.qbeecamera.http.e<Void> eVar) {
        a().removeEvents(map).enqueue(new o(eVar));
    }

    private static String c() {
        com.vestiacom.qbeecamera.model.i E = com.vestiacom.qbeecamera.e.c.b().E();
        return E != null ? E.b() : "";
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static abstract class c<T extends p> implements Callback<T> {

        /* renamed from: a, reason: collision with root package name */
        protected com.vestiacom.qbeecamera.http.e f4571a;

        public c(com.vestiacom.qbeecamera.http.e eVar) {
            this.f4571a = eVar;
        }

        @Override // retrofit2.Callback
        public void onResponse(Call<T> call, Response<T> response) {
            Log.d(this, "request: " + call.request().toString() + " code " + response.code());
            if (response.isSuccessful()) {
                T body = response.body();
                if (body != null) {
                    String status = body.getStatus();
                    if (status != null) {
                        char c2 = 65535;
                        switch (status.hashCode()) {
                            case -1975441958:
                                if (status.equals("CHECKING")) {
                                    c2 = 2;
                                    break;
                                }
                                break;
                            case -1149187101:
                                if (status.equals("SUCCESS")) {
                                    c2 = 1;
                                    break;
                                }
                                break;
                            case 2524:
                                if (status.equals("OK")) {
                                    c2 = 0;
                                    break;
                                }
                                break;
                            case 78673511:
                                if (status.equals("SAVED")) {
                                    c2 = 3;
                                    break;
                                }
                                break;
                        }
                        switch (c2) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                a(response);
                                return;
                            default:
                                String errorCode = body.getErrorCode();
                                Log.d(this, "Request failed with status " + status + " errorCode: " + errorCode);
                                this.f4571a.a(status, errorCode);
                                return;
                        }
                    }
                    a(response);
                    return;
                }
                a();
                return;
            }
            Log.d(this, "Request error with code " + response.code() + " msg " + response.message());
            this.f4571a.a();
        }

        @Override // retrofit2.Callback
        public void onFailure(Call<T> call, Throwable th) {
            Log.d(this, "onFailure " + th.getMessage());
            this.f4571a.a();
        }

        protected void a(Response<T> response) {
            this.f4571a.a(null);
        }

        protected void a() {
            this.f4571a.a(null);
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class o extends c<BaseResponse> {
        public o(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class a extends c<QBeeAuthorizationResponse> {

        /* renamed from: b, reason: collision with root package name */
        private String f4569b;

        /* renamed from: c, reason: collision with root package name */
        private String f4570c;

        public a(com.vestiacom.qbeecamera.http.e eVar, String str, String str2) {
            super(eVar);
            this.f4569b = str;
            this.f4570c = str2;
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<QBeeAuthorizationResponse> response) {
            QBeeAuthorizationResponse body = response.body();
            if (body.isLoggedIn()) {
                com.vestiacom.qbeecamera.e.c.b().a(new com.vestiacom.qbeecamera.model.i(body.getSessionId(), this.f4569b), this.f4569b, this.f4570c);
                Log.d(this, "Success in QBee login");
            }
            super.a(response);
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class e extends c<CameraResponse> {
        public e(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<CameraResponse> response) {
            List<Camera> devices = response.body().getDevices();
            this.f4571a.a(devices);
            Log.d(this, "Cameras num: " + devices.size());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class h extends c<CameraSettingsResponse> {
        public h(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<CameraSettingsResponse> response) {
            this.f4571a.a(new com.vestiacom.qbeecamera.model.f(response.body().getSettings()));
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class f extends c<CameraConfigResponse> {
        public f(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<CameraConfigResponse> response) {
            this.f4571a.a(response.body().createCameraConfig());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class g extends c<CameraEventResponse> {
        public g(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<CameraEventResponse> response) {
            this.f4571a.a(response.body().createCameraEvent());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class i extends c<CameraTechnicalDetailsResponse> {
        public i(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<CameraTechnicalDetailsResponse> response) {
            this.f4571a.a(response.body());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class j extends c<EventListsPageResponse> {
        public j(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<EventListsPageResponse> response) {
            Log.w(this, "EVENTS PAGE", "onSuccess");
            this.f4571a.a(response.body());
            Log.w(this, "EVENTS PAGE", "onSuccess completed");
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class m extends c<RecordingStatusResponse> {
        public m(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<RecordingStatusResponse> response) {
            this.f4571a.a(response.body().getRecordingStatus());
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* renamed from: com.vestiacom.qbeecamera.http.d$d, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public static class C0099d implements Callback<ac> {

        /* renamed from: a, reason: collision with root package name */
        protected com.vestiacom.qbeecamera.http.e f4572a;

        public C0099d(com.vestiacom.qbeecamera.http.e eVar) {
            this.f4572a = eVar;
        }

        @Override // retrofit2.Callback
        public void onResponse(Call<ac> call, Response<ac> response) {
            try {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        InputStream byteStream = response.body().byteStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        this.f4572a.a(BitmapFactory.decodeStream(byteStream, null, options));
                        Log.d(this, "success get snapshot");
                    }
                } else {
                    this.f4572a.a();
                }
            } catch (Exception e) {
                Log.d(this, "exception: " + e.getMessage());
                this.f4572a.a();
            }
        }

        @Override // retrofit2.Callback
        public void onFailure(Call<ac> call, Throwable th) {
            Log.d(this, "onFailure", th);
            this.f4572a.a();
        }
    }

    /* compiled from: RetrofitAPIHelper.java */
    /* loaded from: classes.dex */
    public static class q extends c<UserInfoResponse> {
        public q(com.vestiacom.qbeecamera.http.e eVar) {
            super(eVar);
        }

        @Override // com.vestiacom.qbeecamera.http.d.c
        protected void a(Response<UserInfoResponse> response) {
            this.f4571a.a(response.body());
        }
    }
}
