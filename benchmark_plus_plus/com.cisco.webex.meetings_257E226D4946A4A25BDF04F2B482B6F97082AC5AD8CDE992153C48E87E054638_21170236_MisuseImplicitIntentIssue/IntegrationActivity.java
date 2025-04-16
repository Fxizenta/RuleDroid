package com.cisco.webex.meetings.ui.integration;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.cisco.android.lib.wearcommon.message.PhoneMeetingInfo;
import com.cisco.webex.meetings.R;
import com.cisco.webex.meetings.app.MeetingApplication;
import com.cisco.webex.meetings.client.premeeting.OtherRoomInfoActivity;
import com.cisco.webex.meetings.client.premeeting.RecentPMR;
import com.cisco.webex.meetings.service.MeetingService;
import com.cisco.webex.meetings.ui.WbxActivity;
import com.cisco.webex.meetings.ui.inmeeting.MeetingClient;
import com.cisco.webex.meetings.ui.premeeting.meetinglist.MeetingListActivity;
import com.cisco.webex.meetings.ui.premeeting.welcome.TermsofUseActivity;
import com.cisco.webex.meetings.ui.premeeting.welcome.WelcomeActivity;
import com.cisco.webex.permission.PermissionManager;
import com.microsoft.identity.client.internal.MsalUtils;
import com.webex.meeting.model.dto.ElevenAccount;
import com.webex.meeting.model.dto.WebexAccount;
import com.webex.util.Logger;
import defpackage.aaf;
import defpackage.aak;
import defpackage.aam;
import defpackage.abc;
import defpackage.abe;
import defpackage.abp;
import defpackage.cwo;
import defpackage.doz;
import defpackage.dqi;
import defpackage.dqq;
import defpackage.dvf;
import defpackage.ehm;
import defpackage.ehp;
import defpackage.eiy;
import defpackage.mi;
import defpackage.mk;
import defpackage.mu;
import defpackage.ob;
import defpackage.xm;
import defpackage.xo;
import defpackage.xp;
import java.io.Serializable;
import java.util.List;

/* loaded from: classes.dex */
public class IntegrationActivity extends WbxActivity {
    private static final String a = "IntegrationActivity";
    private String b = null;
    private String c = null;
    private boolean d = true;
    private boolean e = false;
    private boolean f = false;
    private boolean g = false;
    private Handler h = new Handler();
    private mu i = new mu();

    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Logger.i(a, "onCreate");
    }

    public static void a(int i, Activity activity) {
        Intent h = xo.h(activity);
        Logger.i(a, "doUriAction, action=" + i);
        if (h != null) {
            Logger.d(a, "doUriAction intent " + h + " extra " + h.getExtras());
            if (PermissionManager.a() > 0) {
                Logger.w(a, "ignore starting action " + i + " during permission requesting, count: " + PermissionManager.a());
                MeetingService.a(activity.getApplication());
                return;
            }
            switch (i) {
                case 1:
                case 2:
                    a(i, h, activity);
                    return;
                case 3:
                    c(i, h, activity);
                    return;
                case 4:
                    f(h, activity);
                    return;
                case 5:
                    g(h, activity);
                    return;
                case 6:
                    h(h, activity);
                    return;
                case 7:
                    i(h, activity);
                    return;
                case 8:
                    j(h, activity);
                    return;
                case 9:
                    a(h, activity);
                    return;
                case 10:
                    e(h, activity);
                    return;
                case 11:
                    c(h, activity);
                    return;
                case 12:
                default:
                    return;
                case 13:
                    d(h, activity);
                    return;
            }
        }
        Logger.i(a, "doUriAction intent is null");
    }

    public static void a(Activity activity, int i, Intent intent) {
        switch (i) {
            case 1:
            case 2:
            case 3:
                break;
            default:
                switch (i) {
                    case 9:
                    case 10:
                    case 11:
                        break;
                    default:
                        a(i, activity);
                        return;
                }
        }
        xo.a(activity, intent);
    }

    public static void a(int i, Intent intent, Activity activity) {
        Logger.i(a, "tryToStartMeetingClientActivity, starter=" + activity);
        if (xo.b() && !xo.c()) {
            Logger.i(a, "is connecting meeting");
            MeetingService.a(activity.getApplication());
        } else {
            if (b(i, intent, activity)) {
                return;
            }
            d(intent, activity);
        }
    }

    private static boolean b(int i, Intent intent, Activity activity) {
        Logger.i(a, "startMeetingClientActivity, starter=" + activity);
        switch (i) {
            case 1:
                return a(intent, (Context) activity);
            case 2:
                return b(intent, activity);
            default:
                return false;
        }
    }

    public static boolean a(Intent intent, Context context) {
        Logger.i(a, "startMeetingClientActivityForConnectMeeting, starter=" + context);
        if (intent == null) {
            return false;
        }
        doz.d dVar = new doz.d();
        if (!a(intent, context, dVar)) {
            return true;
        }
        aak.a(context, dVar);
        Intent intent2 = new Intent(context, (Class<?>) MeetingClient.class);
        intent2.setAction("com.webex.meeting.JoinMeeting");
        intent2.addFlags(131072);
        intent2.putExtra("ConnectParams", dVar);
        int intExtra = intent.getIntExtra("INTENT_EXTRA_INT_IMFORCESWITCH", 0);
        Logger.d(a, "bForceSwitch " + intExtra);
        if (intExtra == 0) {
            intent2.putExtra("ForceSwitch", false);
            intent2.putExtra("EndCurrentMeeting", false);
        } else if (intExtra == 1) {
            intent2.putExtra("ForceSwitch", false);
            intent2.putExtra("EndCurrentMeeting", true);
        } else if (intExtra == 2) {
            intent2.putExtra("ForceSwitch", true);
            intent2.putExtra("EndCurrentMeeting", false);
        } else if (intExtra == 3) {
            intent2.putExtra("ForceSwitch", true);
            intent2.putExtra("EndCurrentMeeting", true);
        }
        context.startActivity(intent2);
        return true;
    }

    private static boolean a(Intent intent, Activity activity, dqi.d dVar) {
        String a2 = xo.a(intent, "AT");
        String a3 = xo.a(intent, "SiteURL");
        String a4 = xo.a(intent, "UN");
        String a5 = xo.a(intent, "STY");
        String a6 = xo.a(intent, "SSO");
        if (ehm.B(a2) || ehm.B(a3) || ehm.B(a4) || ehm.B(a5) || !a3.matches("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$") || !ehm.b("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", a4)) {
            return false;
        }
        dVar.c = a2;
        dVar.b = a3;
        dVar.a = a4;
        dVar.d = a5;
        dVar.e = a6;
        return true;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(33:29|(2:31|(24:35|36|(2:38|(1:40)(1:99))(2:100|(1:102)(1:103))|41|42|(1:44)(1:98)|45|(1:47)(1:97)|48|(1:50)|51|(1:53)(1:96)|54|55|56|57|58|(2:60|(1:62))(1:92)|91|64|(1:66)|67|(2:73|(2:78|(2:85|(1:90)(1:89))(1:84))(1:77))(1:71)|72))(1:105)|104|36|(0)(0)|41|42|(0)(0)|45|(0)(0)|48|(0)|51|(0)(0)|54|55|56|57|58|(0)(0)|91|64|(0)|67|(1:69)|73|(1:75)|78|(1:80)|85|(1:87)|90|72) */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0276, code lost:
    
        if (defpackage.ehm.B(r2) == false) goto L88;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0248, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0249, code lost:
    
        com.webex.util.Logger.d(com.cisco.webex.meetings.ui.integration.IntegrationActivity.a, "parse event number for EC failed.", r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01e6  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0109  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0134  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x01a7 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01a9  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x01d8  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x01fe  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0229  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0233  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0264  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0282  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0279  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x023b  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0217  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0201  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean a(android.content.Intent r35, android.content.Context r36, doz.d r37) {
        /*
            Method dump skipped, instructions count: 775
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cisco.webex.meetings.ui.integration.IntegrationActivity.a(android.content.Intent, android.content.Context, doz$d):boolean");
    }

    private static void a(String str, String str2, String str3, String str4, String str5, doz.d dVar) {
        if (dvf.a(str2)) {
            if (!ehm.B(str4) && !ehm.B(str5)) {
                dVar.n = str4;
                dVar.m = str5;
            }
            dVar.F = 5;
        } else {
            dVar.F = 1;
        }
        if (!ehm.B(str)) {
            dVar.E = str;
        } else if (xo.a()) {
            WebexAccount e = xo.e();
            if (e.isEleven()) {
                dVar.E = ((ElevenAccount) e).getConferenceURL();
            }
        }
        dVar.B = str2;
        dVar.I = "MeetingCenter";
        if (ehm.B(str3)) {
            return;
        }
        dVar.A = str3;
    }

    private static void a(doz.d dVar) {
        WebexAccount e;
        dVar.F = 0;
        if (xo.a() && (e = xo.e()) != null) {
            dVar.B = e.sessionTicket.b();
        }
        if (xo.a()) {
            WebexAccount e2 = xo.e();
            if (e2.isTrain()) {
                dVar.t = e2.userID;
            }
            dVar.u = e2.userPwd;
            dVar.m = e2.email;
            return;
        }
        dVar.m = dVar.K;
    }

    private static void a(String str, String str2, doz.d dVar) {
        WebexAccount e;
        dVar.F = 2;
        if (!ehm.B(str2)) {
            dVar.B = str2;
        } else if (xo.a() && (e = xo.e()) != null) {
            dVar.B = e.sessionTicket.b();
        }
        if (!ehm.B(str)) {
            dVar.t = str;
            return;
        }
        if (xo.a()) {
            WebexAccount e2 = xo.e();
            if (e2 != null && e2.isTrain()) {
                dVar.t = e2.userID;
                dVar.n = e2.displayName;
            }
            dVar.u = e2.userPwd;
            dVar.m = e2.email;
        }
    }

    private static void a(String str, doz.d dVar, String str2, String str3, String str4) {
        dVar.F = 3;
        dVar.G = str;
        dVar.H = str2;
        dVar.B = str3;
        dVar.t = str4;
        if (ehm.B(dVar.K)) {
            return;
        }
        dVar.m = dVar.K;
    }

    private static void a(Context context, doz.d dVar) {
        WebexAccount e;
        dVar.F = 0;
        if (xo.a() && (e = xo.e()) != null) {
            dVar.B = e.sessionTicket.b();
        }
        if (xo.a()) {
            WebexAccount e2 = xo.e();
            if (e2.isTrain()) {
                dVar.t = e2.userID;
            }
            dVar.u = e2.userPwd;
            dVar.m = e2.email;
            if (ehm.B(dVar.n)) {
                String a2 = aak.a(context, e2);
                String h = mi.h(context);
                if (!ehm.B(h) && !ehm.h(a2, h)) {
                    Logger.d(a, " use customize DN " + h);
                    dVar.n = h;
                    return;
                }
                dVar.n = a2;
            }
        }
    }

    private static boolean b(Intent intent, Activity activity) {
        String str;
        String str2;
        String str3;
        Logger.i(a, "startMeetingClientActivityForInstantMeeting, starter=" + activity);
        if (intent == null || !xo.a()) {
            return false;
        }
        String str4 = null;
        if (xo.d() || xo.b()) {
            intent.setData(null);
            MeetingService.a(activity);
            return true;
        }
        String a2 = xo.a(intent, "attendees");
        String a3 = xo.a(intent, "nativecall");
        String a4 = xo.a(intent, "meetingpwd");
        Logger.d(a, "attendees=" + a2 + ", nativecall=" + a3);
        WebexAccount e = xo.e();
        if (e.isTrain()) {
            str = e.userID;
            str2 = null;
        } else if (e.isEleven()) {
            str2 = ((ElevenAccount) e).getConferenceURL();
            str = null;
        } else {
            str = null;
            str2 = null;
        }
        if (e != null) {
            str3 = e.userPwd;
            str4 = e.email;
        } else {
            str3 = null;
        }
        Intent intent2 = new Intent(activity, (Class<?>) MeetingClient.class);
        intent2.setAction("com.webex.meeting.InstantMeeting");
        intent2.addFlags(131072);
        doz.d dVar = new doz.d();
        dVar.t = str;
        dVar.m = str4;
        dVar.u = str3;
        dVar.v = a2;
        dVar.w = "TRUE".equalsIgnoreCase(a3);
        dVar.x = aak.b(activity, e);
        dVar.n = aak.a(activity, e);
        dVar.E = str2;
        if (e != null) {
            Logger.d(a, " sessionTicket " + e.sessionTicket);
            dVar.B = e.sessionTicket.b();
        }
        if (a4 != null && a4.length() > 0) {
            dVar.d = a4;
        }
        aak.a(activity, dVar);
        intent2.putExtra("ConnectParams", dVar);
        activity.startActivity(intent2);
        if (dVar.w) {
            abc.a().a("JoinMeeting", "ByOther", "FromSamsungPhone", true);
        }
        Logger.d(a, "[startMeetingClientActivityForInstantMeeting][CONNECTING] ? " + dVar.x);
        return true;
    }

    private static void c(Intent intent, Activity activity) {
        Logger.i(a, "onTSPHAVoIPResult");
        if (!xo.d()) {
            Logger.i(a, "onTSPHAVoIPResult, is not in meeting, just ignore this result.");
            return;
        }
        Intent intent2 = new Intent(activity, (Class<?>) MeetingClient.class);
        intent2.setData(intent.getData());
        intent2.addFlags(131072);
        intent2.setAction("WbxActivity.ACTION_TSPHA_VOIP_RESULT");
        activity.startActivity(intent2);
    }

    private static void c(int i, Intent intent, Activity activity) {
        if (aaf.e()) {
            try {
                Intent data = new Intent("android.intent.action.INSERT").setData(Uri.parse("content://com.android.calendar/events"));
                data.putExtra("beginTime", 0);
                data.putExtra("endTime", 0);
                data.addFlags(131072);
                activity.startActivity(data);
            } catch (ActivityNotFoundException unused) {
                Logger.i(a, "Activity not found {com.android.calendar/com.android.calendar.EditEvent}");
            }
            activity.finish();
            return;
        }
        d(i, intent, activity);
    }

    private static void d(int i, Intent intent, Activity activity) {
        Logger.i(a, "tryToStartMeetingListActivity, starter=" + activity);
        if (a(i, activity, intent)) {
            return;
        }
        k(intent, activity);
    }

    private static boolean a(int i, Activity activity, Intent intent) {
        Logger.i(a, "startMeetingListActivity, starter=" + activity);
        if (!xo.a()) {
            Logger.i(a, "have not sign in");
            return false;
        }
        Intent intent2 = new Intent(activity, (Class<?>) MeetingListActivity.class);
        intent2.addFlags(131072);
        if (i != 0) {
            intent2.setData(intent.getData());
            if (intent.getExtras() != null) {
                intent2.putExtras(intent.getExtras());
            }
        }
        activity.startActivity(intent2);
        return true;
    }

    private static void d(Intent intent, Activity activity) {
        Logger.i(a, "startWelcomeActivity, starter=" + activity);
        Intent intent2 = new Intent(activity, (Class<?>) WelcomeActivity.class);
        intent2.addFlags(131072);
        if (xo.g(intent) != 0) {
            intent2.setData(intent.getData());
            if (intent.getExtras() != null) {
                intent2.putExtras(intent.getExtras());
            }
        }
        activity.startActivity(intent2);
    }

    private static void e(Intent intent, Activity activity) {
        if (intent == null) {
            Logger.i(a, "activateUserAccount, intent is null.");
            return;
        }
        dqi.d dVar = new dqi.d();
        if (a(intent, activity, dVar)) {
            if (xo.d()) {
                a(MeetingClient.class, activity);
                return;
            }
            if (xo.a()) {
                eiy accountInfo = dqq.a().getSiginModel().b().getAccountInfo();
                if (ehm.h(dVar.a, accountInfo.n) && ehm.h(dVar.b, accountInfo.b)) {
                    a(MeetingListActivity.class, activity, intent.getData(), (String) null, (Serializable) null);
                    return;
                } else {
                    a(MeetingListActivity.class, activity, intent.getData(), "OrionParams", dVar);
                    return;
                }
            }
            a(WelcomeActivity.class, activity, intent.getData(), "OrionParams", dVar);
        }
    }

    private static void a(Class cls, Activity activity) {
        a(cls, activity, (Uri) null, (String) null, (Serializable) null);
    }

    private static void a(Class cls, Activity activity, Uri uri, String str, Serializable serializable) {
        Intent intent = new Intent(activity, (Class<?>) cls);
        intent.addFlags(131072);
        if (uri != null) {
            intent.setData(uri);
        }
        if (!ehm.B(str) && serializable != null) {
            intent.putExtra(str, serializable);
        }
        activity.startActivity(intent);
        activity.finish();
    }

    private static void f(Intent intent, Activity activity) {
        if (xo.a()) {
            Logger.d(a, "already signin!");
            activity.setResult(-1);
            activity.finish();
            return;
        }
        a(activity, true);
    }

    private static void g(Intent intent, Activity activity) {
        xo.d(activity);
        if (xo.a()) {
            Intent intent2 = new Intent(activity, (Class<?>) IntegrationWrapScheduleActivity.class);
            if (intent != null) {
                if (intent.getIntExtra("CALLER_ID", 0) == 7) {
                    intent2.putExtra("CALLER_ID", 7);
                } else {
                    intent2.putExtra("CALLER_ID", 2);
                }
                intent2.putExtra("noUI", xo.a(intent, "noUI"));
                intent2.putExtra("password", xo.a(intent, "password"));
                intent2.putExtra("attendees", xo.a(intent, "attendees"));
            }
            intent2.addFlags(33685504);
            activity.startActivity(intent2);
            activity.finish();
            return;
        }
        a(activity, true);
    }

    private static void h(Intent intent, Activity activity) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        try {
            str2 = xo.a(intent, "MK");
            str3 = xo.a(intent, "PWD");
            str4 = xo.a(intent, "r2sec");
            str = xo.a(intent, "GROUPID");
        } catch (Exception e) {
            Logger.e(a, "startCiusEscalate Error", e);
            str = null;
            str2 = "";
            str3 = "";
            str4 = "";
        }
        activity.setResult(0);
        if (a(str2, activity)) {
            return;
        }
        xo.d(activity);
        xp a2 = xo.a(intent.getData());
        String a3 = a2.a();
        String b = a2.b();
        Logger.i(a, "meeting3 ServerName: " + a3 + " SiteName : " + b);
        if (xo.a()) {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            if (!ehm.B(a3) && !ehm.B(b)) {
                str5 = "wbx://meeting/" + a3 + "/" + b + "?MK=" + str2 + "&MPW=" + str3 + "&r2sec=" + str4;
            } else {
                str5 = "wbx://meeting?MK=" + str2 + "&MPW=" + str3 + "&r2sec=" + str4;
            }
            Logger.d(a, "meeting url " + str5);
            intent2.setData(Uri.parse(str5));
            intent2.putExtra("MK", str2);
            intent2.putExtra("MPW", str3);
            String c = xo.c(intent, "forceswitch");
            intent2.putExtra("INTENT_EXTRA_INT_IMFORCESWITCH", (c.trim().equals(PhoneMeetingInfo.PERSONAL_ROOT_CONF_ID) || c.trim().equals("1") || c.trim().equals("2") || c.trim().equals("3")) ? Integer.parseInt(c) : 0);
            if (!ehm.B(str)) {
                intent2.putExtra("GROUPID", str);
            }
            a(activity, xo.g(intent2), intent2);
            activity.setResult(-1);
            activity.finish();
            return;
        }
        a(activity, false);
    }

    private static void i(Intent intent, Activity activity) {
        String a2 = xo.a(intent, "MK");
        String a3 = xo.a(intent, "MPW");
        if (a(a2, activity)) {
            Logger.w(a, "Must have meeting key!");
            activity.setResult(0);
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("wbx://meeting3?");
        stringBuffer.append("MK");
        stringBuffer.append("=");
        stringBuffer.append(a2);
        stringBuffer.append(MsalUtils.QUERY_STRING_DELIMITER);
        stringBuffer.append("PWD");
        stringBuffer.append("=");
        stringBuffer.append(ehp.a(a3));
        stringBuffer.append("&action=start");
        Intent intent2 = new Intent();
        intent2.putExtra("wbxHostURL", stringBuffer.toString());
        intent.putExtra("MK", a2);
        intent.putExtra("PWD", a3);
        activity.setResult(-1, intent2);
        activity.finish();
    }

    private static String a(String str) {
        return "1".equals(str) ? "MeetingCenter" : "7".equals(str) ? "TrainingCenter" : "6".equals(str) ? "EventCenter" : "";
    }

    private static void j(Intent intent, Activity activity) {
        Intent h = xo.h(activity);
        if (h == null || h.getData() == null) {
            activity.finish();
            return;
        }
        Intent intent2 = new Intent("android.intent.action.VIEW");
        intent2.setData(Uri.parse("wbx://meeting?" + h.getData().getQuery()));
        String a2 = xo.a(h, "MK");
        if (a(a2, activity)) {
            return;
        }
        String a3 = xo.a(h, "MPW");
        String a4 = xo.a(h, "RP");
        if (ehm.h(xo.a(h, "CALLERID"), "1")) {
            intent2.putExtra("CALLERID", "1");
        }
        String a5 = xo.a(h, "DN");
        if (!ehm.B(a5) && a5.length() <= 64) {
            intent2.putExtra("DN", a5);
        } else {
            Logger.i(a, "invalid display name");
        }
        String a6 = xo.a(h, "EM");
        if (!ehm.B(a6) && a6.length() <= 64 && ehm.o(a6)) {
            intent2.putExtra("email", a6);
        } else {
            Logger.i(a, "invalid email");
        }
        if (a2 != null) {
            intent2.putExtra("MK", a2);
        }
        if (a3 != null) {
            intent2.putExtra("MPW", a3);
        }
        if (a4 != null) {
            intent2.putExtra("RP", a4);
        }
        a(activity, xo.g(intent2), intent2);
        activity.setResult(-1);
        activity.finish();
    }

    public static void a(Intent intent, Activity activity) {
        if (intent == null || xo.a()) {
            return;
        }
        Intent intent2 = new Intent(activity, (Class<?>) WelcomeActivity.class);
        intent2.addFlags(131072);
        if (xo.g(intent) != 0) {
            intent2.setData(intent.getData());
            Bundle extras = intent.getExtras();
            if (extras != null) {
                intent2.putExtras(extras);
            }
        }
        activity.startActivity(intent2);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }

    private static void a(Activity activity, boolean z) {
        Intent h = xo.h(activity);
        Intent intent = new Intent(activity, (Class<?>) IntegrationWrapSigninActivity.class);
        intent.putExtra("INTENT_EXTRA_INTEGRATION_INTENT", h);
        intent.putExtra("CALLER_ID", 2);
        boolean c = dqq.a().getSiginModel().c();
        Logger.d(a, "tryToStartSignInWizardActivity isAutoSignin: " + c);
        intent.putExtra("AUTO_SIGNIN", c);
        intent.putExtra("SIGNIN_ACCOUNT", xo.e());
        if (z) {
            intent.addFlags(33554432);
        }
        activity.startActivity(intent);
        activity.finish();
    }

    private static void k(Intent intent, Activity activity) {
        Intent intent2 = new Intent(activity, (Class<?>) WelcomeActivity.class);
        intent2.setData(Uri.parse("wbx://signin"));
        intent2.addFlags(131072);
        intent2.putExtra("INTENT_EXTRA_INTEGRATION_INTENT", intent);
        intent2.putExtra("CALLER_ID", 2);
        activity.startActivity(intent2);
    }

    private void a(int i, Intent intent) {
        if (intent == null) {
            return;
        }
        Logger.i(a, "startTermsActivity");
        Intent intent2 = new Intent(this, (Class<?>) TermsofUseActivity.class);
        intent2.addFlags(33685504);
        intent2.putExtra("CALLER_ID", 2);
        intent2.putExtra("INTENT_EXTRA_INTEGRATION_INTENT", getIntent());
        if (i != 0) {
            intent2.setData(intent.getData());
            if (intent.getExtras() != null) {
                intent2.putExtras(intent.getExtras());
            }
        }
        startActivity(intent2);
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.permission.RuntimePermissionRequestActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Logger.i(a, "onActivityResult " + intent);
        setResult(i2, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        Logger.d(a, "onStart");
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Logger.d(a, "onDestroy, isFinishing=" + isFinishing());
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        Logger.i(a, "onResume");
        super.onResume();
        i();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        Logger.i(a, "onPause");
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        Logger.i(a, "onNewIntent, intent is :" + intent);
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override // android.app.Activity
    public void finish() {
        Logger.i(a, "finish");
        super.finish();
    }

    private boolean f() {
        return (getIntent().getFlags() & 1048576) == 1048576;
    }

    private void g() {
        Intent intent = new Intent(this, (Class<?>) WelcomeActivity.class);
        intent.addFlags(67174400);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private boolean b(int i, Intent intent) {
        if (i == 2) {
            Logger.i(a, "instant meeting");
            dqi siginModel = dqq.a().getSiginModel();
            if (!siginModel.d()) {
                return false;
            }
            if (xo.d() || xo.b()) {
                intent.setData(null);
                MeetingService.a(this);
                finish();
                return true;
            }
            WebexAccount b = siginModel.b();
            if (abe.a(b)) {
                String b2 = aak.b(this, b);
                String a2 = xo.a(intent, "meetingpwd");
                if (abe.a((Context) this, b, a2, b2, false)) {
                    Logger.d(a, "Instant meeting, use the stored or URI-specified password");
                    a(this, intent, a2);
                    finish();
                } else {
                    Logger.i(a, "Instant meeting, need set password");
                    showDialog(401);
                }
                return true;
            }
        }
        Logger.i(a, "Instant meeting without password or not an instant meeting");
        return false;
    }

    private Dialog h() {
        xm xmVar = new xm(this, dqq.a().getSiginModel().b(), xo.h(this));
        xmVar.setCanceledOnTouchOutside(false);
        return xmVar;
    }

    public static void a(Activity activity, Intent intent, String str) {
        if (activity == null || intent == null) {
            return;
        }
        Uri data = intent.getData();
        if (data != null && str != null && str.length() > 0) {
            if (xo.b(intent, "meetingpwd") == null) {
                StringBuffer stringBuffer = new StringBuffer(data.toString());
                int length = stringBuffer.length() - 1;
                String str2 = "meetingpwd=" + ehp.a(str);
                if (data.getQuery() == null) {
                    stringBuffer.append(MsalUtils.QUERY_STRING_SYMBOL);
                    stringBuffer.append(str2);
                } else {
                    stringBuffer.append((length < 0 || !(stringBuffer.charAt(length) == '&' || stringBuffer.charAt(length) == '?')) ? MsalUtils.QUERY_STRING_DELIMITER : "");
                    stringBuffer.append(str2);
                }
                data = Uri.parse(stringBuffer.toString());
            } else {
                String uri = data.toString();
                int indexOf = uri.indexOf("&meetingpwd=", 0);
                if (indexOf < 0) {
                    indexOf = uri.indexOf("?meetingpwd=", 0);
                }
                int length2 = indexOf + "&meetingpwd=".length();
                int indexOf2 = uri.indexOf(38, length2);
                String str3 = uri.substring(0, length2) + ehp.a(str);
                if (indexOf2 > 0) {
                    str3 = str3 + uri.substring(indexOf2);
                }
                data = Uri.parse(str3);
            }
        }
        intent.setData(data);
        intent.putExtra("meetingpwd", str);
        a(activity, xo.g(intent), intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.app.Activity
    public Dialog onCreateDialog(int i) {
        if (i == 401) {
            return h();
        }
        return super.onCreateDialog(i);
    }

    private static boolean a(String str, Context context) {
        if (ehm.B(str)) {
            Logger.e(a, "Invalid meeting key.");
            a(context);
            return true;
        }
        if (ehm.a((CharSequence) str, true) > 0) {
            return false;
        }
        Logger.e(a, "Invalid meeting key.");
        a(context);
        return true;
    }

    private static void a(Context context) {
        Intent intent = new Intent(context, (Class<?>) IntegrationFakeActivity.class);
        intent.putExtra("show invalid meeting key", true);
        context.startActivity(intent);
    }

    private void a(int i, Intent intent, String str, String str2) {
        String str3;
        if (intent == null) {
            return;
        }
        Logger.i(a, "startIntegrationAuthorizationActivity");
        final Intent intent2 = new Intent(this, (Class<?>) IntegrationAuthorizationActivity.class);
        intent2.addFlags(33685504);
        intent2.putExtra("CALLER_ID", 2);
        intent2.putExtra("INTENT_EXTRA_INTEGRATION_INTENT", getIntent());
        intent2.putExtra("calling_package", str);
        intent2.putExtra("CALLER_ID", 2);
        intent2.putExtra("target_server_url", str2);
        if (i != 0) {
            intent2.setData(intent.getData());
            if (intent.getExtras() != null) {
                intent2.putExtras(intent.getExtras());
            }
        }
        if (i == 40) {
            String uri = intent.getData().toString();
            String[] split = uri.split("\\?");
            String str4 = split[1];
            String str5 = str4.split("=")[1];
            if (ehm.G(str5)) {
                Logger.d("Index meeting key", str5);
                str3 = str5;
            } else {
                String str6 = split[2];
                Logger.d("IndexRecordingList recordingPostSuffix", str6);
                str3 = str5 + MsalUtils.QUERY_STRING_SYMBOL + str6;
                Logger.d("IndexRecordingList URl", str3);
            }
            Logger.d(a, "dataString " + uri + "pmrNumSec " + str4 + "pmrNum " + str3);
            for (RecentPMR recentPMR : this.i.a(false)) {
                Logger.d("Recent", "pmrNum " + str3 + "pmr.getPMRMeetingnumber()" + recentPMR.getPMRMeetingnumber() + "pmrNum.equals(String.valueOf(pmr.getPMRMeetingnumber()))" + str3.equals(String.valueOf(recentPMR.getPMRMeetingnumber())));
                if (str3.equals(String.valueOf(recentPMR.getPMRMeetingnumber()))) {
                    this.e = true;
                    Intent intent3 = new Intent(this, (Class<?>) OtherRoomInfoActivity.class);
                    intent3.addFlags(131072);
                    Bundle bundle = new Bundle();
                    bundle.putString("PMR", new cwo().b(recentPMR));
                    ob.a(this).b(recentPMR.url);
                    intent3.putExtras(bundle);
                    startActivity(intent3);
                }
            }
            if (!this.e && !ehm.G(str5)) {
                Logger.d(a, "IndexRecordingList + parse recording URL is  " + str3);
                this.g = true;
                Intent intent4 = new Intent("android.intent.action.VIEW");
                intent4.setData(Uri.parse(str3));
                startActivity(intent4);
            }
            if (this.e || this.g) {
                return;
            }
            Logger.d(a, "IndexMeetinglist + parse meeting key is  " + str3);
            this.f = true;
            startActivity(abp.a().a(Long.parseLong(str3)));
            return;
        }
        switch (i) {
            case 1:
            case 2:
            case 3:
                break;
            default:
                switch (i) {
                    case 9:
                    case 10:
                    case 11:
                        break;
                    default:
                        startActivity(intent2);
                        return;
                }
        }
        Runnable runnable = new Runnable() { // from class: com.cisco.webex.meetings.ui.integration.IntegrationActivity.1
            @Override // java.lang.Runnable
            public void run() {
                IntegrationActivity.this.startActivity(intent2);
            }
        };
        Runnable runnable2 = new Runnable() { // from class: com.cisco.webex.meetings.ui.integration.IntegrationActivity.2
            @Override // java.lang.Runnable
            public void run() {
                IntegrationActivity.this.finish();
            }
        };
        this.h.postDelayed(runnable, 500L);
        this.h.postDelayed(runnable2, 1000L);
        this.d = false;
    }

    public String e() {
        ActivityManager activityManager;
        List<ActivityManager.RunningTaskInfo> list;
        ActivityManager.RunningTaskInfo runningTaskInfo;
        ComponentName callingActivity = getCallingActivity();
        if (callingActivity != null) {
            String str = callingActivity.getPackageName().toString();
            Logger.d(a, " invokeApp " + str);
            if (!ehm.B(str)) {
                return str;
            }
        }
        if (Build.VERSION.SDK_INT >= 21 || (activityManager = (ActivityManager) getSystemService("activity")) == null) {
            return null;
        }
        try {
            list = activityManager.getRunningTasks(20);
        } catch (NullPointerException e) {
            Logger.e(a, "getCallerPackageName catch null point exception " + e);
            list = null;
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.size() > 1 && (runningTaskInfo = list.get(i)) != null) {
                    ComponentName componentName = runningTaskInfo.topActivity;
                    ComponentName componentName2 = runningTaskInfo.baseActivity;
                    Logger.d(a, " topActivity.getPackageName().toString() " + componentName.getPackageName().toString() + " baseActivity.getPackageName().toString() " + componentName2.getPackageName().toString());
                    if (componentName2 != null && !ehm.h(componentName2.getPackageName(), getPackageName())) {
                        return componentName2.getPackageName().toString();
                    }
                }
            }
        }
        Logger.i(a, "caller packagename return null");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        Logger.d(a, "onRestoreInstanceState() " + bundle);
        if (bundle != null) {
            this.b = bundle.getString("mPackageName");
            this.c = bundle.getString("target_server_url");
            this.d = bundle.getBoolean("mFinishDirectly");
        }
        super.onRestoreInstanceState(bundle);
    }

    @Override // com.cisco.webex.meetings.ui.WbxActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        Logger.i(a, "onSaveInstanceState()");
        if (bundle != null) {
            bundle.putString("mPackageName", this.b);
            bundle.putString("target_server_url", this.c);
            bundle.putBoolean("mFinishDirectly", this.d);
        }
        super.onSaveInstanceState(bundle);
    }

    private boolean a(String str, String str2) {
        if (ehm.B(str)) {
            return true;
        }
        if (ehm.h(str, "com.cisco.im")) {
            String c = aam.c(this, str);
            Logger.d(a, " packageHash " + c);
            if (ehm.h("C4EA62A3A16CEF0F0F1BE91707DCF0DAE94CBD62FA23C1D6AC0677E3902FB80E", c)) {
                Logger.d(a, " don't need pop up notification");
                return false;
            }
        }
        return (ehm.B(str2) && mi.f(this, str)) ? false : true;
    }

    private void i() {
        Logger.i(a, "integrationEntrance");
        Logger.d(a, "integrationEntrance, intent : " + getIntent() + "  integration : " + getIntent().getParcelableExtra("INTENT_EXTRA_INTEGRATION_INTENT"));
        if (getIntent() != null) {
            Logger.d(a, " extra " + getIntent().getExtras());
            if (getIntent().getData() != null) {
                Logger.d(a, "getIntent().getData() " + getIntent().getData().toString());
            }
        }
        if (aaf.P()) {
            xo.c((Context) this);
            finish();
            return;
        }
        if (mk.a().c()) {
            mk.a().b();
            finish();
            final Intent intent = getIntent();
            this.h.postDelayed(new Runnable(intent) { // from class: xn
                private final Intent a;

                {
                    this.a = intent;
                }

                @Override // java.lang.Runnable
                public void run() {
                    MeetingApplication.v().startActivity(this.a);
                }
            }, 800L);
            Logger.i(a, "integrationEntrance quit pip mode and restart.");
            return;
        }
        String b = xo.b((Context) this);
        if (!ehm.B(b)) {
            finish();
            xo.b(this, b);
            return;
        }
        this.b = e();
        String flattenToString = getCallingActivity() == null ? "null" : getCallingActivity().flattenToString();
        Logger.d(a, "callingActivity is : " + flattenToString);
        mk.a().b();
        Intent intent2 = getIntent();
        if (f()) {
            Logger.i(a, "onCreate, launch from history.");
            g();
            finish();
            return;
        }
        int g = xo.g(intent2);
        xo.a(this, g, intent2, e());
        this.c = xo.i(intent2);
        if (xo.b(this.c)) {
            this.c = null;
        }
        Bundle extras = intent2.getExtras();
        boolean z = false;
        boolean z2 = extras != null ? extras.getBoolean("com.cisco.android.setupwizard.FROM_WIZARD") : false;
        if (g == 4 && aaf.e() && z2) {
            z = true;
        }
        if (a(this.b, this.c)) {
            a(g, intent2, this.b, this.c);
        } else if (!mi.k(this) && !z && g != 9) {
            a(g, intent2);
        } else if (b(g, intent2)) {
            return;
        } else {
            a(this, g, intent2);
        }
        if (this.d) {
            finish();
        }
    }
}
