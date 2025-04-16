package com.samsung.android.fmm.push;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import com.google.firebase.iid.FirebaseInstanceId;
import com.samsung.android.fmm.common.MessageVO;
import com.samsung.android.fmm.common.MgVO;
import com.samsung.android.fmm.common.account.AccountVO;
import com.samsung.android.fmm.common.util.n;
import com.samsung.android.fmm.operation.task.q;
import com.samsung.android.fmm.push.a;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class d {

    /* renamed from: a, reason: collision with root package name */
    private static com.samsung.android.fmm.common.b f5518a = new com.samsung.android.fmm.common.b("PushManager");

    /* renamed from: b, reason: collision with root package name */
    private static Map<String, com.samsung.android.fmm.push.a> f5519b;

    /* renamed from: c, reason: collision with root package name */
    private static d f5520c;

    /* renamed from: d, reason: collision with root package name */
    private com.samsung.android.fmm.common.account.e f5521d;
    private com.samsung.android.fmm.push.f.a e;
    private com.samsung.android.fmm.operation.d f;
    private int g;
    private MessageVO h;

    /* loaded from: classes.dex */
    class a extends com.samsung.android.fmm.operation.d {

        /* renamed from: a, reason: collision with root package name */
        final /* synthetic */ Context f5522a;

        a(Context context) {
            this.f5522a = context;
        }

        @Override // com.samsung.android.fmm.operation.d
        public synchronized void a(MessageVO messageVO) {
            if (messageVO != null) {
                d.f5518a.a("receiveSuccess and delete it from db: " + messageVO.getMsgId() + ", " + messageVO.getType());
                d.this.e.a(messageVO.getMsgId());
                d.this.w(this.f5522a, messageVO);
            }
        }
    }

    /* loaded from: classes.dex */
    class b implements a.c {
        b() {
        }

        @Override // com.samsung.android.fmm.push.a.c
        public void a(MgVO mgVO) {
            if (d.this.g > 0) {
                d.e(d.this, ~(mgVO.getType().equals("SPP") ? 1 : 2));
                d.f5518a.a("packageReplaced : " + d.this.g);
            }
        }

        @Override // com.samsung.android.fmm.push.a.c
        public void b(MgVO mgVO) {
        }
    }

    private d(Context context, com.samsung.android.fmm.common.account.e eVar) {
        f5518a.h("PushManager is created.");
        this.g = 0;
        f5519b = new HashMap();
        this.e = com.samsung.android.fmm.push.f.a.d(context);
        if (!com.samsung.android.fmm.common.util.c.H()) {
            f5519b.put("FCM", new c("FCM"));
        }
        f5519b.put("SPP", new e(context, "SPP"));
        this.f5521d = eVar;
        m(com.samsung.android.fmm.common.util.c.C(context));
        this.f = new a(context);
    }

    static /* synthetic */ int e(d dVar, int i) {
        int i2 = i & dVar.g;
        dVar.g = i2;
        return i2;
    }

    private synchronized boolean f(Context context, MessageVO messageVO) {
        boolean z;
        if (TextUtils.isEmpty(com.samsung.android.fmm.common.c.a.g(context).f())) {
            f5518a.a("There is no delivery url. Re-register push");
            if (TextUtils.isEmpty(messageVO.getType())) {
                f5518a.a("Push type is empty");
            } else {
                n.g(context, messageVO.getType().equals("FCM") ? "pref_mgvo_FCM" : "pref_mgvo_SPP", "");
                if (u(messageVO)) {
                    Intent intent = new Intent("SA_AUTH_STEP_FINISH");
                    intent.setClassName("com.samsung.android.fmm", "com.samsung.android.fmm.application.FmmService");
                    context.startService(intent);
                }
                z = true;
            }
        }
        z = false;
        return z;
    }

    public static synchronized d h(Context context, com.samsung.android.fmm.common.account.e eVar) {
        d dVar;
        synchronized (d.class) {
            if (f5520c == null) {
                f5520c = new d(context, eVar);
            }
            dVar = f5520c;
        }
        return dVar;
    }

    public static synchronized MgVO i(Context context, String str) {
        synchronized (d.class) {
            Map<String, com.samsung.android.fmm.push.a> map = f5519b;
            if (map == null) {
                f5518a.e("PushManager is not ready.");
                return null;
            }
            com.samsung.android.fmm.push.a aVar = map.get(str);
            if (aVar == null) {
                return null;
            }
            return aVar.h(context);
        }
    }

    private synchronized void l(Context context, MessageVO messageVO) {
        try {
            if (this.e.f(messageVO.getMsgId())) {
                if (!TextUtils.isEmpty(messageVO.getErrorCode())) {
                    f5518a.i("delete message id from db because account info become different. : " + messageVO.getType());
                    this.e.a(messageVO.getMsgId());
                }
            } else if (TextUtils.isEmpty(messageVO.getErrorCode())) {
                if (!TextUtils.isEmpty(messageVO.getClientType()) && "MSG".equals(messageVO.getClientType())) {
                    f5518a.a("this is message push");
                    new q(context).d(1015, new String(Base64.decode(messageVO.getMsg(), 2), "utf-8"), System.currentTimeMillis());
                    w(context, messageVO);
                    return;
                }
                f5518a.h("new message id save to db. : " + messageVO.getType());
                this.e.e(messageVO.getMsgId());
                x(context, messageVO);
            }
        } catch (Exception e) {
            f5518a.d(e);
        }
    }

    private boolean n(String str) {
        return TextUtils.isEmpty(str) ? !com.samsung.android.fmm.common.util.c.H() : !str.contains("chn.ospserver.net");
    }

    public static void p(MessageVO messageVO, String str) {
        if (str.contains("@")) {
            String[] split = str.split("@");
            if (split.length >= 2) {
                com.samsung.android.fmm.common.b bVar = f5518a;
                if (bVar != null) {
                    bVar.a("Push for wearable device");
                }
                messageVO.setSubDeviceId(split[1]);
                str = split[0];
                if (split.length >= 3) {
                    String[] split2 = split[2].split(":");
                    messageVO.setOpName(split2[0]);
                    messageVO.setOpExtra(split2.length > 1 ? split2[1] : "");
                }
            }
        }
        if (str.contains("|")) {
            String[] split3 = str.split("\\|");
            if (split3.length == 3 || split3.length == 4 || split3.length == 5) {
                for (String str2 : split3) {
                    if (TextUtils.isEmpty(str2)) {
                        com.samsung.android.fmm.common.b bVar2 = f5518a;
                        if (bVar2 != null) {
                            bVar2.e("parseMessage failed, empty element exist in the msg : " + str);
                        }
                        messageVO.setErrorCode("RCV-0200");
                        return;
                    }
                }
                messageVO.setMsgId(split3[0]);
                if (messageVO.getMsgId().length() > 10) {
                    messageVO.setUserId(messageVO.getMsgId().substring(messageVO.getMsgId().length() - 10));
                }
                messageVO.setClientType(split3[1]);
                messageVO.setMsg(split3[2]);
                if (split3.length <= 3 || !"Y".equals(split3[3])) {
                    return;
                }
                com.samsung.android.fmm.common.b bVar3 = f5518a;
                if (bVar3 != null) {
                    bVar3.a("Origin push message has location operation");
                }
                messageVO.setIncludeLocOprt(true);
                return;
            }
            com.samsung.android.fmm.common.b bVar4 = f5518a;
            if (bVar4 != null) {
                bVar4.e("parseMessage failed, invalid msg Protocol :" + str);
            }
        } else {
            com.samsung.android.fmm.common.b bVar5 = f5518a;
            if (bVar5 != null) {
                bVar5.e("parseMessage failed, msg does not have (|) delimiter.");
            }
        }
        messageVO.setErrorCode("RCV-0204");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized void t(Context context, MgVO mgVO) {
        synchronized (d.class) {
            Map<String, com.samsung.android.fmm.push.a> map = f5519b;
            if (map == null) {
                f5518a.e("PushManager is not ready.");
                return;
            }
            com.samsung.android.fmm.push.a aVar = map.get(mgVO.getType());
            if (aVar != null) {
                aVar.r(context, mgVO);
            } else {
                f5518a.e("saveMgInfo failed : not supported type : " + mgVO.getType());
            }
        }
    }

    private boolean u(MessageVO messageVO) {
        if (this.h != null) {
            return false;
        }
        this.h = messageVO;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void w(Context context, MessageVO messageVO) {
        if (messageVO == null) {
            f5518a.e("msg vo is null");
            return;
        }
        f5518a.a("Requested delivery report : " + messageVO.getMsgId() + ", " + messageVO.getType());
        Intent intent = new Intent("PUSH_DELIVERY_REPORT");
        intent.setClassName("com.samsung.android.fmm", "com.samsung.android.fmm.application.FmmService");
        intent.putExtra("push_vo", messageVO);
        context.startService(intent);
    }

    private void x(Context context, MessageVO messageVO) {
        if (messageVO == null) {
            f5518a.e("msg vo is null");
            return;
        }
        f5518a.a("startOperation START_VALID_OPRT : " + messageVO.toString());
        Intent intent = new Intent("START_VALID_OPRT");
        intent.setClassName("com.samsung.android.fmm", "com.samsung.android.fmm.application.FmmService");
        intent.putExtra("push_vo", messageVO);
        context.startService(intent);
    }

    public void g(Context context) {
        try {
            f5518a.a("checkRegId");
            MgVO i = i(context, "SPP");
            MgVO i2 = i(context, "FCM");
            if (com.samsung.android.fmm.common.util.c.C(context)) {
                if (i != null && !TextUtils.isEmpty(i.getInstanceId()) && !i.getInstanceId().contains("sppeos_")) {
                    f5518a.g("Get spp token for expire case");
                    Intent intent = new Intent("com.sec.spp.action.SPP_REQUEST");
                    intent.setPackage("com.sec.spp.push");
                    intent.putExtra("reqType", 1);
                    intent.putExtra("appId", "fb0bdc9021c264df");
                    intent.putExtra("userdata", context.getPackageName());
                    intent.addFlags(32);
                    context.sendBroadcast(intent);
                }
                if (i2 == null || TextUtils.isEmpty(i2.getInstanceId())) {
                    return;
                }
                String e = FirebaseInstanceId.c().e();
                if (TextUtils.isEmpty(e) || i2.getInstanceId().equalsIgnoreCase(e)) {
                    return;
                }
                f5518a.g("Fcm token has changed.");
                i2.setInstanceId(e);
                t(context, i2);
                Intent intent2 = new Intent("FCM_TOKEN_REFRESH");
                intent2.setClassName("com.samsung.android.fmm", "com.samsung.android.fmm.application.FmmService");
                context.startService(intent2);
            }
        } catch (Exception e2) {
            f5518a.d(e2);
        }
    }

    public com.samsung.android.fmm.operation.d j() {
        return this.f;
    }

    public synchronized void k(Context context, String str, String str2) {
        f5518a.a("HandleMsg : type= " + str2 + ", msg= " + str);
        MessageVO messageVO = new MessageVO();
        p(messageVO, str);
        AccountVO a2 = this.f5521d.a();
        String userId = a2 != null ? a2.getUserId() : "";
        String msgId = messageVO.getMsgId();
        String userId2 = messageVO.getUserId();
        messageVO.setType(str2);
        messageVO.setDeviceId(a2 != null ? a2.getDeviceId() : com.samsung.android.fmm.common.util.c.j(context));
        if (TextUtils.isEmpty(msgId) || TextUtils.isEmpty(userId) || userId.equals(userId2)) {
            if (f(context, messageVO)) {
                f5518a.a("Ignore push and wait mg register");
                return;
            } else {
                l(context, messageVO);
                return;
            }
        }
        f5518a.i("Account is different. local(" + userId + "), message(" + userId2 + ")");
        messageVO.setErrorCode("RCV-0205");
        w(context, messageVO);
        u(messageVO);
        com.samsung.android.fmm.common.account.b.p(context).E();
    }

    public void m(boolean z) {
        Iterator<Map.Entry<String, com.samsung.android.fmm.push.a>> it = f5519b.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().k(z);
        }
    }

    public boolean o() {
        Iterator<Map.Entry<String, com.samsung.android.fmm.push.a>> it = f5519b.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().j().booleanValue()) {
                return true;
            }
        }
        return false;
    }

    public synchronized void q(Context context, String str) {
        com.samsung.android.fmm.common.b bVar;
        String str2;
        String n;
        f5518a.a("registerToMgServer : " + str);
        com.samsung.android.fmm.push.a aVar = f5519b.get(str);
        if (aVar == null || !str.equalsIgnoreCase(aVar.i())) {
            bVar = f5518a;
            str2 = "MG registration failed. not exist pushType : " + str;
        } else {
            AccountVO a2 = this.f5521d.a();
            if (a2 != null) {
                MgVO h = aVar.h(context);
                if (h == null) {
                    f5518a.e("Register id not exist");
                    return;
                }
                h.setGlobalDvc(n(a2.getServerUrl()));
                if (TextUtils.isEmpty(a2.getDeviceId())) {
                    n = com.samsung.android.fmm.common.util.c.n(context);
                    f5518a.i(" use real IMEI from Account : " + str + ", " + com.samsung.android.fmm.common.util.c.v(n));
                } else {
                    n = a2.getDeviceId();
                    f5518a.a(" use real IMEI from Account : " + str + ", " + com.samsung.android.fmm.common.util.c.v(n));
                }
                h.setIMEI(n);
                boolean z = true;
                if (((h.getType().equals("SPP") ? 1 : 2) & this.g) <= 0) {
                    z = false;
                }
                h.setUpdate(z);
                aVar.n(context, h, new b());
            }
            bVar = f5518a;
            str2 = "accountVO is null.";
        }
        bVar.e(str2);
    }

    public synchronized void r(Context context, boolean z) {
        f5518a.a("MG registration info remove start.");
        Iterator<Map.Entry<String, com.samsung.android.fmm.push.a>> it = f5519b.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().o(context, z);
        }
        f5518a.a("MG registration info remove has been completed.");
    }

    public void s(Context context) {
        try {
            if (this.h != null) {
                f5518a.a("restartPendingMsg");
                l(context, this.h);
                this.h = null;
            }
        } catch (Exception e) {
            f5518a.d(e);
        }
    }

    public void v(Boolean bool) {
        this.g = bool.booleanValue() ? 3 : 0;
    }

    public synchronized void y(Context context, String str) {
        Iterator<Map.Entry<String, com.samsung.android.fmm.push.a>> it = f5519b.entrySet().iterator();
        while (it.hasNext()) {
            com.samsung.android.fmm.push.a value = it.next().getValue();
            if (value != null && (str == null || value.i().equals(str))) {
                value.w(context, this.f5521d.a());
            }
        }
    }
}
