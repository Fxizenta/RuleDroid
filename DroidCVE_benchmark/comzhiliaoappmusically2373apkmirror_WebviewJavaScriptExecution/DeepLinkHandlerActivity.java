package com.ss.android.ugc.aweme.deeplink;

import X.AbstractC95433oQ;
import X.ActivityC34501Wb;
import X.C021805t;
import X.C07090Oq;
import X.C09230Ww;
import X.C0P5;
import X.C0Y9;
import X.C10U;
import X.C11770cm;
import X.C13530fc;
import X.C14100gX;
import X.C14610hM;
import X.C15900jR;
import X.C16570kW;
import X.C16620kb;
import X.C18240nD;
import X.C1DF;
import X.C21790sw;
import X.C22650uK;
import X.C22670uM;
import X.C24750xi;
import X.C2RR;
import X.C3R1;
import X.C49751ww;
import X.C85073Uo;
import X.C95163nz;
import X.C95363oJ;
import X.C95443oR;
import X.JFC;
import X.JYO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.bytedance.android.livesdk.livesetting.pullstream.LiveNetAdaptiveHurryTimeSetting;
import com.bytedance.apm.agent.v2.instrumentation.ActivityAgent;
import com.bytedance.covode.number.Covode;
import com.bytedance.ies.abmock.SettingsManager;
import com.ss.android.ugc.aweme.AccountService;
import com.ss.android.ugc.aweme.DetailFeedServiceImpl;
import com.ss.android.ugc.aweme.IAccountUserService;
import com.ss.android.ugc.aweme.deeplink.DeepLinkHandlerActivity;
import com.ss.android.ugc.aweme.login.LoginUtilsServiceImpl;
import com.ss.android.ugc.aweme.out.AVExternalServiceImpl;
import com.ss.android.ugc.aweme.services.MainServiceImpl;
import com.ss.android.ugc.aweme.utils.ActivityStack;
import com.ss.android.ugc.tiktok.deeplink.impl.DeeplinkPrefetchImpl;
import com.zhiliaoapp.musically.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DeepLinkHandlerActivity extends ActivityC34501Wb {
    public Uri LIZIZ;
    public C95163nz LIZ = new C95163nz();
    public boolean LIZJ = false;
    public boolean LIZLLL = false;
    public long LJII = 0;
    public String LJ = null;
    public boolean LJIIIIZZ = false;
    public boolean LJIIIZ = false;
    public boolean LJIIJ = false;
    public boolean LJIIJJI = false;
    public ArrayList<String> LJIIL = new ArrayList<>();
    public ArrayList<Intent> LJIILIIL = new ArrayList<>();
    public Intent LJIILJJIL = null;
    public Intent LJIILL = null;
    public boolean LJFF = false;
    public boolean LJIILLIIL = false;
    public boolean LJIIZILJ = false;
    public IAccountUserService LJIJ = AccountService.LIZ().LJFF();
    public IDeepLinkService LJIJI = DeepLinkServiceImpl.LJIIJ();
    public String LJIJJ = "";
    public boolean LJI = false;

    static {
        Covode.recordClassIndex(54631);
    }

    @Override // X.ActivityC34501Wb, X.C1K3, android.app.Activity
    public void onPause() {
        C09230Ww.LIZJ(this);
        super.onPause();
    }

    @Override // X.ActivityC34501Wb, X.C1K3, android.app.Activity
    public void onResume() {
        C09230Ww.LIZIZ(this);
        ActivityAgent.onTrace("com.ss.android.ugc.aweme.deeplink.DeepLinkHandlerActivity", "onResume", true);
        super.onResume();
        ActivityAgent.onTrace("com.ss.android.ugc.aweme.deeplink.DeepLinkHandlerActivity", "onResume", false);
    }

    @Override // X.ActivityC34501Wb, X.ActivityC32601Ot, X.C1K3, android.app.Activity
    public void onStop() {
        C09230Ww.LIZLLL(this);
        super.onStop();
    }

    @Override // X.ActivityC34501Wb, android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        ActivityAgent.onTrace("com.ss.android.ugc.aweme.deeplink.DeepLinkHandlerActivity", "onWindowFocusChanged", true);
        super.onWindowFocusChanged(z);
    }

    public final boolean LIZ() {
        return MainServiceImpl.createIMainServicebyMonsterPlugin(false).isAppHot();
    }

    public Intent LIZ(Uri uri, boolean z, boolean z2) {
        boolean z3;
        String scheme = uri.getScheme();
        Intent intent = null;
        if (TextUtils.isEmpty(scheme)) {
            return null;
        }
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        if (DeeplinkPrefetchImpl.LIZ().LIZ(new C22650uK(uri, Boolean.valueOf(this.LIZLLL))).LJIIIIZZ) {
            String path = uri.getPath() == null ? "" : uri.getPath();
            String LIZ = LIZ(getIntent(), "from_token");
            String str = TextUtils.isEmpty(LIZ) ? "" : LIZ;
            Uri.Builder buildUpon = uri.buildUpon();
            buildUpon.appendQueryParameter("from_token", str);
            if (TextUtils.isEmpty(uri.getQueryParameter("enter_from"))) {
                if (TextUtils.equals("token", LIZ(getIntent(), "enter_from"))) {
                    buildUpon.appendQueryParameter("enter_from", "token");
                } else {
                    buildUpon.appendQueryParameter("enter_from", this.LIZLLL ? "push" : "deeplink");
                }
            }
            Uri build = buildUpon.build();
            Iterator<C3R1> it = MainServiceImpl.createIMainServicebyMonsterPlugin(false).getDeeplinkCommands().iterator();
            while (true) {
                if (it.hasNext()) {
                    C3R1 next = it.next();
                    if (next.LIZ(build, scheme, host, path)) {
                        if ((host + path).contains("aweme/detail") || (host + path).contains("aweme/push_detail")) {
                            this.LJIIJJI = true;
                        } else {
                            this.LJIIJJI = false;
                        }
                        this.LIZ.LIZJ(next.LIZ(build));
                        intent = next.LIZ(this, build, host, path, str, this.LIZLLL, z);
                        if (intent != null) {
                            if (LIZ(intent, "share_url_user_id") != null) {
                                this.LIZ.LJFF(LIZ(intent, "share_url_user_id"));
                            } else if (LIZ(intent, "share_sec_url_user_id") != null) {
                                this.LIZ.LJFF(LIZ(intent, "share_sec_url_user_id"));
                            }
                            if (LIZ(intent, "share_url_link_id") != null) {
                                this.LIZ.LJI(LIZ(intent, "share_url_link_id"));
                            }
                        }
                        if (intent == null) {
                            next.LIZ(this, build, this.LIZLLL);
                            C16570kW.LIZ("independent_transfer", LiveNetAdaptiveHurryTimeSetting.DEFAULT, this.LIZIZ);
                        }
                        if (!TextUtils.isEmpty(next.LIZ())) {
                            C16620kb.LIZ(build, next.LIZ());
                        } else if (intent != null) {
                            C16620kb.LIZ(build, intent.getComponent().getClassName());
                        }
                        z3 = true;
                    }
                } else {
                    z3 = false;
                    break;
                }
            }
            if (intent != null && !LIZ() && !intent.hasExtra("com.ss.android.ugc.aweme.intent.extra.EXTRA_AWEME_PUSH_TAB")) {
                String LIZ2 = AbstractC95433oQ.LIZ(build.getQueryParameter("tab_index"));
                if ((!"aweme".equals(host) || !"click_push_newvideo".equals(build.getQueryParameter("gd_label")) || !TextUtils.equals(LIZ2, "DISCOVER")) && !TextUtils.isEmpty(LIZ2)) {
                    intent.putExtra("com.ss.android.ugc.aweme.intent.extra.EXTRA_AWEME_PUSH_TAB", LIZ2);
                }
                intent.putExtra("is_from_push", true);
            }
            if (intent != null) {
                String queryParameter = build.getQueryParameter("backurl");
                if (!TextUtils.isEmpty(queryParameter)) {
                    intent.putExtra("backurl", queryParameter);
                }
            }
            C85073Uo.LIZ().LIZ = false;
            C95443oR.LIZ(build, intent);
            if (intent != null && build.getQueryParameter("gd_label") != null && build.getQueryParameter("gd_label").startsWith("click_wap")) {
                intent.putExtra("ads_app_activity_by_wap_click", true);
            }
            if (!z3) {
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("open_url", build.toString());
                    C1DF.LIZ("service_monitor", "no_matched_deep_link", jSONObject);
                } catch (Exception unused) {
                }
            }
            if (!z3 && z2) {
                this.LJFF = true;
                this.LIZ.LIZJ("default_homepage");
            }
        }
        return intent;
    }

    @Override // X.ActivityC34501Wb, X.ActivityC32601Ot, X.C1K3, android.app.Activity
    public void onDestroy() {
        C09230Ww.LJ(this);
        super.onDestroy();
        this.LJFF = false;
    }

    @Override // X.ActivityC34501Wb, X.ActivityC32601Ot, X.C1K3, android.app.Activity
    public void onStart() {
        C09230Ww.LIZ(this);
        super.onStart();
        finish();
    }

    private boolean LIZIZ() {
        String[] strArr;
        String LIZ = C18240nD.LIZ();
        try {
            strArr = (String[]) SettingsManager.LIZ().LIZ("app_action_allowlist", String[].class);
        } catch (Throwable unused) {
            strArr = new String[]{"US"};
        }
        if (strArr == null) {
            strArr = new String[]{"US"};
        }
        for (String str : strArr) {
            if (TextUtils.equals(LIZ, str)) {
                return false;
            }
        }
        if (!LIZ()) {
            Intent mainActivityIntent = MainServiceImpl.createIMainServicebyMonsterPlugin(false).getMainActivityIntent(this);
            mainActivityIntent.putExtra("app_action_restricted", true);
            LIZ(this, mainActivityIntent);
        } else {
            new C11770cm(this).LJ(R.string.c5v).LIZIZ();
        }
        return true;
    }

    private void LIZJ() {
        Intent intent = getIntent();
        this.LJIIL = intent.getStringArrayListExtra("pre_o_urls");
        try {
            if (!LIZIZ(intent)) {
                return;
            }
            if (!this.LJIIZILJ) {
                LIZ(this.LJIILJJIL, this.LJIILL, this.LJIILIIL);
            }
            C16570kW.LIZ("Transfer", LiveNetAdaptiveHurryTimeSetting.DEFAULT, this.LIZIZ);
            C85073Uo.LIZ().LIZ = false;
            this.LJIJI.LIZ(LIZ(this.LIZIZ, this.LJIIL), true, "");
            JFC.LIZ.LIZ(this.LIZIZ, this.LIZLLL);
            if (this.LIZLLL && this.LJIIJJI) {
                int isPushVideoPreload = C13530fc.LIZ().isPushVideoPreload(this.LJII);
                if (LIZ()) {
                    C95363oJ.LIZIZ(System.currentTimeMillis(), this.LJIJJ, isPushVideoPreload);
                } else {
                    C95363oJ.LIZ(System.currentTimeMillis(), this.LJIJJ, isPushVideoPreload);
                }
            }
        } catch (Exception e) {
            this.LJIJI.LIZ(LIZ(this.LIZIZ, this.LJIIL), false, e.getMessage());
            Uri uri = this.LIZIZ;
            e.getMessage();
            C16570kW.LIZ("Transfer", 2014, uri);
            e.printStackTrace();
        }
    }

    @Override // X.ActivityC34501Wb, X.ActivityC32601Ot, android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        try {
            super.setTheme(i);
        } catch (Exception unused) {
        }
    }

    private boolean LIZ(Intent intent) {
        if (intent == null) {
            return false;
        }
        String action = intent.getAction();
        if (!C0P5.LIZ(action) && action.indexOf("com.ss.android.sdk.") == 0) {
            String LIZ = LIZ(intent, "open_url");
            if (!C0P5.LIZ(LIZ)) {
                try {
                    this.LIZIZ = Uri.parse(LIZ);
                } catch (Exception unused) {
                    return false;
                }
            }
        }
        if (this.LIZIZ == null) {
            this.LIZIZ = intent.getData();
        }
        Uri uri = this.LIZIZ;
        if (uri == null || uri.isOpaque()) {
            return false;
        }
        return true;
    }

    private boolean LIZIZ(Intent intent) {
        String str;
        Uri uri;
        String str2;
        boolean z;
        boolean z2;
        boolean z3;
        String str3 = "";
        if (intent == null) {
            str = "";
        } else {
            str = LIZ(intent, "rule_id");
        }
        if (intent != null) {
            uri = intent.getData();
        } else {
            uri = null;
        }
        List<String> allUidList = this.LJIJ.allUidList();
        String curUserId = this.LJIJ.getCurUserId();
        if (uri != null) {
            str2 = uri.getQueryParameter("multi_account_push_uid");
        } else {
            str2 = null;
        }
        boolean isEmpty = TextUtils.isEmpty(str2);
        boolean isLogin = C14100gX.LJI().isLogin();
        boolean equals = TextUtils.equals(str2, curUserId);
        boolean contains = allUidList.contains(str2);
        if (C18240nD.LIZJ() && !isEmpty && isLogin && !equals) {
            this.LJIIZILJ = true;
            if (AVExternalServiceImpl.LIZ().publishService().isPublishing()) {
                new Handler().post(new Runnable(this) { // from class: X.1gg
                    public final DeepLinkHandlerActivity LIZ;

                    static {
                        Covode.recordClassIndex(54715);
                    }

                    {
                        this.LIZ = this;
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        new C11770cm(this.LIZ).LJ(R.string.fl3).LIZIZ();
                    }
                });
                C16570kW.LIZ("Transfer", 2011, this.LIZIZ);
                return false;
            }
            LIZIZ(contains, intent);
        }
        if (intent != null && intent.getBooleanExtra("second_jump", false)) {
            z = true;
        } else {
            z = false;
        }
        if (!C07090Oq.LIZ((Collection) this.LJIIL)) {
            if (C14100gX.LJI().isLogin() && !TextUtils.isEmpty(str2) && !TextUtils.equals(str2, this.LJIJ.getCurUserId())) {
                if (LIZ()) {
                    this.LJIJI.LIZ(this.LJIIL.toString(), false, "abs isAppHot == true");
                    return false;
                }
                this.LJIILJJIL = MainServiceImpl.createIMainServicebyMonsterPlugin(false).getMainActivityIntent(this);
            } else if (!LIZ() || z) {
                Iterator<String> it = this.LJIIL.iterator();
                while (it.hasNext()) {
                    Uri parse = Uri.parse(it.next());
                    if (this.LJIILL != null) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    Intent LIZ = LIZ(parse, z3, false);
                    if (LIZ != null) {
                        LIZ.putExtra("is_have_intents", true);
                        LIZ.putExtra("rule_id", str);
                        this.LJIILIIL.add(0, LIZ);
                    }
                }
            }
        }
        if (this.LJIILJJIL == null) {
            Uri uri2 = this.LIZIZ;
            if (this.LJIILL != null) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.LJIILJJIL = LIZ(uri2, z2, true);
        }
        Intent intent2 = this.LJIILJJIL;
        if (intent2 == null) {
            if (!this.LJIILLIIL) {
                IDeepLinkService iDeepLinkService = this.LJIJI;
                Uri uri3 = this.LIZIZ;
                if (uri3 != null) {
                    str3 = uri3.toString();
                }
                iDeepLinkService.LIZ(str3, false, "abs intent == null");
            }
            if (!LIZ() && this.LJFF) {
                Intent mainActivityIntent = MainServiceImpl.createIMainServicebyMonsterPlugin(false).getMainActivityIntent(this);
                this.LJIILJJIL = mainActivityIntent;
                C10U.LIZ(this, mainActivityIntent, (Bundle) null);
            }
            return false;
        }
        intent2.putExtra("from_notification", this.LIZLLL);
        this.LJIILJJIL.putExtra("from_notification_uuid", this.LJ);
        this.LJIILJJIL.putExtra("rule_id", str);
        if (!this.LIZJ) {
            this.LJIILJJIL.addFlags(268435456);
        }
        if (this.LJIILIIL.size() > 0) {
            if (this.LJIILJJIL.getComponent() != null && (TextUtils.equals(MainServiceImpl.createIMainServicebyMonsterPlugin(false).getMainActivityClass().getName(), this.LJIILJJIL.getComponent().getClassName()) || TextUtils.equals(MainServiceImpl.createIMainServicebyMonsterPlugin(false).getSplashActivityClass().getName(), this.LJIILJJIL.getComponent().getClassName()))) {
                this.LJIILIIL.clear();
            } else {
                this.LJIILJJIL.putExtra("is_have_intents", true);
                this.LJIILIIL.add(this.LJIILJJIL);
            }
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x033d  */
    /* JADX WARN: Removed duplicated region for block: B:111:0x034e  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x035b  */
    @Override // X.ActivityC34501Wb, X.C1W2, X.ActivityC32601Ot, X.C1K3, X.C10W, android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onCreate(android.os.Bundle r17) {
        /*
            Method dump skipped, instructions count: 933
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ss.android.ugc.aweme.deeplink.DeepLinkHandlerActivity.onCreate(android.os.Bundle):void");
    }

    public static String LIZ(Intent intent, String str) {
        try {
            return intent.getStringExtra(str);
        } catch (Exception unused) {
            return null;
        }
    }

    public static void LIZ(DeepLinkHandlerActivity deepLinkHandlerActivity, Intent intent) {
        C22670uM.LIZ(intent, deepLinkHandlerActivity);
        deepLinkHandlerActivity.startActivity(intent);
    }

    private String LIZ(Uri uri, ArrayList<String> arrayList) {
        if (uri != null) {
            return uri.toString();
        }
        if (!C07090Oq.LIZ((Collection) arrayList)) {
            return arrayList.toString();
        }
        return "";
    }

    private void LIZ(String str, final Bundle bundle) {
        C21790sw findSignificanUserInfo = C14100gX.LJI().findSignificanUserInfo(str);
        if (findSignificanUserInfo == null) {
            return;
        }
        C14100gX.LIZIZ().switchAccount(findSignificanUserInfo, bundle, new JYO() { // from class: com.ss.android.ugc.aweme.deeplink.DeepLinkHandlerActivity.1
            static {
                Covode.recordClassIndex(54632);
            }

            @Override // X.JYO
            public final void LIZ() {
                C15900jR.LIZ("switch_account_result", new C14610hM().LIZ("status", 1).LIZ);
            }

            @Override // X.JYO
            public final void LIZ(Integer num, String str2) {
                if (!DeepLinkHandlerActivity.this.LIZ()) {
                    LoginUtilsServiceImpl.LIZ().LIZ(bundle);
                    return;
                }
                if (Boolean.valueOf(MainServiceImpl.createIMainServicebyMonsterPlugin(false).isAwemeAppDataEmpty()).booleanValue()) {
                    new C11770cm(C0Y9.LJIILLIIL.LJIIIZ()).LJ(R.string.c0e).LIZIZ();
                }
                C15900jR.LIZ("switch_account_result", new C14610hM().LIZ("status", 0).LIZ("fail_info", num).LIZ);
            }
        });
    }

    private void LIZIZ(final boolean z, final Intent intent) {
        Activity previousActivity = ActivityStack.getPreviousActivity();
        if (previousActivity != null) {
            String canonicalName = previousActivity.getClass().getCanonicalName();
            ArrayList arrayList = new ArrayList();
            arrayList.add("com.ss.android.ugc.aweme.shortvideo.ui.VideoRecordNewActivity");
            arrayList.add("com.ss.android.ugc.aweme.shortvideo.edit.VEVideoPublishEditActivity");
            arrayList.add("com.ss.android.ugc.aweme.shortvideo.ui.VideoPublishActivity");
            if (arrayList.contains(canonicalName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(previousActivity, R.style.uw);
                builder.setMessage(R.string.fm9);
                builder.setNegativeButton(R.string.ae2, C2RR.LIZ);
                builder.setPositiveButton(R.string.azk, new DialogInterface.OnClickListener(this, z, intent) { // from class: X.2RQ
                    public final DeepLinkHandlerActivity LIZ;
                    public final boolean LIZIZ;
                    public final Intent LIZJ;

                    static {
                        Covode.recordClassIndex(54717);
                    }

                    {
                        this.LIZ = this;
                        this.LIZIZ = z;
                        this.LIZJ = intent;
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        this.LIZ.LIZ(this.LIZIZ, this.LIZJ);
                    }
                });
                AlertDialog create = builder.create();
                create.setCanceledOnTouchOutside(false);
                create.setCancelable(false);
                try {
                    create.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                arrayList.clear();
                return;
            }
            arrayList.clear();
        }
        LIZ(z, intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: LIZJ, reason: merged with bridge method [inline-methods] */
    public void LIZ(boolean z, Intent intent) {
        Uri uri;
        String str;
        if (intent != null) {
            uri = intent.getData();
        } else {
            uri = null;
        }
        if (uri != null) {
            str = uri.getQueryParameter("multi_account_push_uid");
        } else {
            str = null;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("push_intent", intent);
        bundle.putBoolean("mFromNotification", this.LIZLLL);
        bundle.putString("from_notification_uuid", this.LJ);
        bundle.putBoolean("cancelRestoreOnMain", true);
        if (z) {
            bundle.putBoolean("switch_jump", true);
            LIZ(str, bundle);
            return;
        }
        Intent intent2 = new Intent(C49751ww.LIZ, AccountService.LIZ().LJII().getPushLoginActivityClass());
        intent2.putExtra("multi_account_push_uid", str);
        intent2.putExtra("multi_account", bundle);
        if (LIZ()) {
            LIZ(this, intent2);
        } else {
            C10U.LIZ(this, new Intent[]{MainServiceImpl.createIMainServicebyMonsterPlugin(false).getMainActivityIntent(this), intent2}, (Bundle) null);
        }
    }

    public final /* synthetic */ C24750xi LIZ(String str, String str2) {
        C15900jR.LIZ("2131829380", new C14610hM().LIZ("rule_id", this.LJ).LIZ("push_label", this.LJIJJ).LIZ("anchor_id", str).LIZ("room_id", str2).LIZ);
        C16570kW.LIZ("live_dialog", 2010, this.LIZIZ);
        return C24750xi.LIZ;
    }

    public static void LIZ(DeepLinkHandlerActivity deepLinkHandlerActivity, Intent intent, Bundle bundle) {
        C22670uM.LIZ(intent, deepLinkHandlerActivity);
        deepLinkHandlerActivity.startActivity(intent, bundle);
    }

    private void LIZ(final Intent intent, Intent intent2, ArrayList<Intent> arrayList) {
        String str;
        C021805t LIZ;
        if (getIntent() != null) {
            str = LIZ(getIntent(), "rule_id");
        } else {
            str = "";
        }
        boolean equals = TextUtils.equals(intent.getComponent().getClassName(), DetailFeedServiceImpl.LIZIZ().LIZ().getName());
        this.LJIIJ = equals;
        final Bundle bundle = null;
        if (equals && (LIZ = C021805t.LIZ(this)) != null) {
            bundle = LIZ.LIZ();
        }
        if (intent2 != null) {
            if (arrayList.size() > 1) {
                intent2.putExtra("next_steps", arrayList);
            } else {
                intent2.putExtra("next_step", intent);
            }
            intent2.putExtra("rule_id", str);
            if (LIZ()) {
                LIZ(this, intent2, bundle);
                return;
            }
            r1[0].putExtra("rule_id", str);
            Intent[] intentArr = {MainServiceImpl.createIMainServicebyMonsterPlugin(false).getMainActivityIntent(this), intent2};
            C10U.LIZ(this, intentArr, bundle);
            return;
        }
        if (arrayList.size() > 1) {
            C10U.LIZ(this, (Intent[]) arrayList.toArray(new Intent[arrayList.size()]), bundle);
        } else if (intent.getBooleanExtra("need_post", false)) {
            new Handler().post(new Runnable(this, intent, bundle) { // from class: X.21Y
                public final DeepLinkHandlerActivity LIZ;
                public final Intent LIZIZ;
                public final Bundle LIZJ;

                static {
                    Covode.recordClassIndex(54718);
                }

                {
                    this.LIZ = this;
                    this.LIZIZ = intent;
                    this.LIZJ = bundle;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    C10U.LIZ(this.LIZ, this.LIZIZ, this.LIZJ);
                }
            });
        } else {
            C10U.LIZ(this, intent, bundle);
        }
    }

    public final /* synthetic */ C24750xi LIZ(String str, String str2, Context context) {
        C15900jR.LIZ("2131829381", new C14610hM().LIZ("rule_id", this.LJ).LIZ("push_label", this.LJIJJ).LIZ("anchor_id", str).LIZ("room_id", str2).LIZ);
        LIZJ();
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
        return C24750xi.LIZ;
    }
}
