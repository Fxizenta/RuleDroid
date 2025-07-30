package com.amdroidalarmclock.amdroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import b.a0.u;
import com.amdroidalarmclock.amdroid.alarm.AlarmSchedulerService;
import d.b.a.l1.c;
import d.b.a.l1.d;
import d.b.a.o;
import d.c.b.a.a;
import d.f.c.m.i;
import java.util.ArrayList;
import java.util.Calendar;

/* loaded from: classes.dex */
public class ApiCalls extends Activity {

    /* renamed from: a, reason: collision with root package name */
    public o f3304a;

    public final void a(Intent intent) {
        String str;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        String H;
        ArrayList<Integer> integerArrayListExtra;
        if (intent.hasExtra("android.intent.extra.alarm.HOUR")) {
            int intExtra = intent.getIntExtra("android.intent.extra.alarm.HOUR", -1);
            c.F("ApiCalls", "hour: " + intExtra);
            int intExtra2 = intent.hasExtra("android.intent.extra.alarm.MINUTES") ? intent.getIntExtra("android.intent.extra.alarm.MINUTES", -1) : 0;
            c.F("ApiCalls", "minutes: " + intExtra2);
            if (intExtra >= 0 && intExtra <= 23 && intExtra2 >= 0 && intExtra2 <= 59) {
                if (intent.hasExtra("android.intent.extra.alarm.MESSAGE")) {
                    str = intent.getStringExtra("android.intent.extra.alarm.MESSAGE");
                    c.F("ApiCalls", "message was added as well");
                    intent.getStringExtra("android.intent.extra.alarm.MESSAGE");
                } else {
                    c.F("ApiCalls", "no message was added");
                    str = "";
                }
                if (!intent.hasExtra("android.intent.extra.alarm.DAYS") || (integerArrayListExtra = intent.getIntegerArrayListExtra("android.intent.extra.alarm.DAYS")) == null) {
                    i2 = 3;
                    i3 = 0;
                    i4 = 0;
                    i5 = 0;
                    i6 = 0;
                    i7 = 0;
                    i8 = 0;
                    i9 = 0;
                } else {
                    i3 = 0;
                    i4 = 0;
                    i5 = 0;
                    i6 = 0;
                    i7 = 0;
                    i8 = 0;
                    i9 = 0;
                    for (int i11 = 0; i11 < integerArrayListExtra.size(); i11++) {
                        switch (integerArrayListExtra.get(i11).intValue()) {
                            case 1:
                                c.F("ApiCalls", "Sunday");
                                i9 = 1;
                                break;
                            case 2:
                                c.F("ApiCalls", "Monday");
                                i3 = 1;
                                break;
                            case 3:
                                c.F("ApiCalls", "Tuesday");
                                i4 = 1;
                                break;
                            case 4:
                                c.F("ApiCalls", "Wednesday");
                                i5 = 1;
                                break;
                            case 5:
                                c.F("ApiCalls", "Thursday");
                                i6 = 1;
                                break;
                            case 6:
                                c.F("ApiCalls", "Friday");
                                i7 = 1;
                                break;
                            case 7:
                                c.F("ApiCalls", "Saturday");
                                i8 = 1;
                                break;
                        }
                        StringBuilder Z = a.Z("days: ");
                        Z.append(integerArrayListExtra.get(i11));
                        c.F("ApiCalls", Z.toString());
                    }
                    i2 = 0;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Calendar calendar2 = Calendar.getInstance();
                int i12 = i9;
                calendar2.set(1, calendar.get(1));
                calendar2.set(2, calendar.get(2));
                calendar2.set(5, calendar.get(5));
                calendar2.set(11, intExtra);
                calendar2.set(12, intExtra2);
                calendar2.set(13, 0);
                if (i2 == 3) {
                    if (calendar2.getTimeInMillis() <= calendar.getTimeInMillis()) {
                        i10 = i8;
                        calendar2.setTimeInMillis(calendar2.getTimeInMillis() + 86400000);
                        c.F("ApiCalls", "alarm would have been in the past, added 1 day");
                    } else {
                        i10 = i8;
                    }
                    StringBuilder Z2 = a.Z("alarm date and time: ");
                    Z2.append(calendar2.getTime().toString());
                    c.F("ApiCalls", Z2.toString());
                } else {
                    i10 = i8;
                }
                int i13 = calendar2.get(1);
                int i14 = calendar2.get(2);
                int i15 = calendar2.get(5);
                o oVar = new o(this);
                this.f3304a = oVar;
                oVar.s0();
                ContentValues m2 = this.f3304a.m();
                m2.put("hour", Integer.valueOf(intExtra));
                m2.put("minute", Integer.valueOf(intExtra2));
                m2.put("note", str);
                m2.put("monday", Integer.valueOf(i3 ^ 1));
                m2.put("tuesday", Integer.valueOf(i4 ^ 1));
                m2.put("wednesday", Integer.valueOf(i5 ^ 1));
                m2.put("thursday", Integer.valueOf(i6 ^ 1));
                m2.put("friday", Integer.valueOf(i7 ^ 1));
                m2.put("saturday", Integer.valueOf(i10 ^ 1));
                m2.put("sunday", Integer.valueOf(i12 ^ 1));
                m2.put("recurrence", Integer.valueOf(i2));
                m2.put("year", Integer.valueOf(i13));
                m2.put("month", Integer.valueOf(i14));
                m2.put("day", Integer.valueOf(i15));
                if (i2 == 3) {
                    m2.put("autoDelete", (Integer) 1);
                }
                o oVar2 = this.f3304a;
                oVar2.s0();
                long insert = oVar2.f10358b.insert("scheduled_alarm", null, m2);
                try {
                    if (i2 == 3) {
                        H = this.f3304a.H(calendar2.getTimeInMillis());
                    } else {
                        H = this.f3304a.H(this.f3304a.J(insert, null).getTimeInMillis());
                    }
                    if (!TextUtils.isEmpty(H)) {
                        d.k(this, H, 1).show();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    try {
                        i.a().c(e2);
                    } catch (Exception unused) {
                    }
                }
                this.f3304a.f();
                d.o(this, new Intent(this, (Class<?>) AlarmSchedulerService.class));
                try {
                    try {
                        getSharedPreferences("alarm", 0).edit().putLong("automationAlarmAddEditId", insert).apply();
                        u.r0(this, 32003);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    return;
                } catch (Exception e4) {
                    e4.printStackTrace();
                    return;
                }
            }
            finish();
            return;
        }
        c.l0("ApiCalls", "hour is not set, nothing to do");
        finish();
    }

    @SuppressLint({"InlinedApi"})
    public final void b(Intent intent) {
        if (intent.getExtras() == null || !intent.getExtras().containsKey("android.intent.extra.alarm.LENGTH")) {
            return;
        }
        o oVar = new o(this);
        this.f3304a = oVar;
        oVar.s0();
        ContentValues m2 = this.f3304a.m();
        m2.put("recurrence", (Integer) 4);
        m2.put("interval", Integer.valueOf(intent.getExtras().getInt("android.intent.extra.alarm.LENGTH")));
        if (!TextUtils.isEmpty(intent.getExtras().getString("android.intent.extra.alarm.MESSAGE"))) {
            m2.put("note", intent.getExtras().getString("android.intent.extra.alarm.MESSAGE"));
        }
        o oVar2 = this.f3304a;
        oVar2.s0();
        long insert = oVar2.f10358b.insert("scheduled_alarm", null, m2);
        this.f3304a.J0(insert);
        try {
            o oVar3 = this.f3304a;
            String H = oVar3.H(oVar3.r(insert, 4));
            if (!TextUtils.isEmpty(H)) {
                d.k(this, H, 1).show();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            try {
                i.a().c(e2);
            } catch (Exception unused) {
            }
        }
        this.f3304a.f();
        try {
            try {
                getSharedPreferences("alarm", 0).edit().putLong("automationAlarmAddEditId", insert).apply();
                u.r0(this, 32003);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            c.F("ApiCalls", "onCreate");
            if (u.m(getApplicationContext())) {
                c.F("ApiCalls", "lock is active, ignoring this one");
                finish();
                return;
            }
            Intent intent = getIntent();
            if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                if ("android.intent.action.SET_ALARM".equals(intent.getAction())) {
                    a(intent);
                } else if ("android.intent.action.SHOW_ALARMS".equals(intent.getAction())) {
                    Intent intent2 = new Intent(this, (Class<?>) MainActivity.class);
                    intent2.setAction("android.intent.action.MAIN");
                    intent2.addCategory("android.intent.category.LAUNCHER");
                    startActivity(intent2);
                } else if ("android.intent.action.SET_TIMER".equals(intent.getAction())) {
                    b(intent);
                } else if ("amdroid.intent.alarm.QUICK_ADD".equals(intent.getAction())) {
                    u.e0(this, intent.getExtras());
                }
            }
        } finally {
            finish();
        }
    }
}
