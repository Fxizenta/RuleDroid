package com.vectorform.wattsonandroid.b;

import android.net.Uri;
import android.util.Log;
import com.vectorform.wattsonandroid.GlobalAppContext;
import com.vectorform.wattsonandroid.c.x;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class q extends c {

    /* renamed from: a, reason: collision with root package name */
    public static boolean f1332a = false;

    /* renamed from: b, reason: collision with root package name */
    private static volatile Integer f1333b;

    public static Integer b(int i) {
        try {
            return Integer.valueOf(new JSONObject(a(a("/api/Notification" + Uri.encode("?$filter=DTEID eq " + i + " and NotificationType/IsAggregable eq false and NotificationType/IsActivityFeed eq true&IsCountRequest=true", "&=?")))).getInt("Count"));
        } catch (Exception e) {
            Log.e("API Calls", "Unable to retrieve activity count", e);
            return null;
        }
    }

    public static List<com.vectorform.wattsonandroid.domain.g.a> c(int i) {
        return d(Uri.encode("?$filter=DTEID eq " + i + " and NotificationType/IsAggregable eq false and NotificationType/IsActivityFeed eq true&$orderby=Created desc&$skip=0&$top=100", "&=?"));
    }

    private static List<com.vectorform.wattsonandroid.domain.g.a> d(String str) {
        try {
            String a2 = a(a("/api/Notification" + str));
            com.google.gson.h hVar = new com.google.gson.h();
            hVar.f1210a = "yyyy-MM-dd'T'HH:mm:ss";
            return (List) hVar.a(org.a.a.b.class, new com.vectorform.wattsonandroid.f.k()).a(com.vectorform.wattsonandroid.domain.g.c.class, new com.vectorform.wattsonandroid.notifications.i()).b().a(a2, new com.google.gson.c.a<List<com.vectorform.wattsonandroid.domain.g.a>>() { // from class: com.vectorform.wattsonandroid.b.q.1
            }.type);
        } catch (Exception e) {
            Log.e("API Calls", "parsing JSON failed in getNotifications", e);
            return null;
        }
    }

    public static boolean d(int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("isRead", true);
            jSONObject.put("NotificationID", i);
            return b(b("/api/Notification", jSONObject.toString()));
        } catch (Exception e) {
            Log.e("API Calls", "Unable to mark notification as read. id: " + i, e);
            return false;
        }
    }

    public static int h() {
        if (f1333b == null) {
            return 0;
        }
        return f1333b.intValue();
    }

    public static void i() {
        f1333b = null;
    }

    public static List<com.vectorform.wattsonandroid.domain.g.a> j() {
        return d(Uri.encode("?$filter=NotificationType/IsAggregable eq false and NotificationType/IsActivityFeed eq true&$orderby=Created desc&$skip=0&$top=100&FriendsOfDTEID=" + GlobalAppContext.d(), "&=?"));
    }

    public static Integer k() {
        try {
            int i = new JSONObject(a(a("/api/Notification" + Uri.encode("?$filter=DTEID eq " + GlobalAppContext.d() + " and IsRead eq false and NotificationType/IsNotification eq true&IsCountRequest=true", "&=?")))).getInt("Count");
            if (i != h()) {
                f1332a = true;
                f1333b = Integer.valueOf(i);
                a.a.a.c.a().c(new x());
            }
            return Integer.valueOf(h());
        } catch (Exception e) {
            Log.e("API Calls", "parsing JSON failed in getUnreadNotificationCountFromServer", e);
            return null;
        }
    }

    public static List<com.vectorform.wattsonandroid.domain.g.a> l() {
        return d(Uri.encode("?$filter=DTEID eq " + GlobalAppContext.d() + " and IsRead eq false and NotificationType/IsNotification eq true&$orderby=Created desc&$skip=0&$top=100", "&=?"));
    }

    public static List<com.vectorform.wattsonandroid.domain.g.a> m() {
        return d(Uri.encode("?$filter=DTEID eq " + GlobalAppContext.d() + " and IsRead eq false and NotificationType/IsNotification eq true and NotificationType/NotificationTypeID eq 16&$orderby=Created desc&$skip=0&$top=100", "&=?"));
    }

    public static boolean n() {
        try {
            return b(b("/api/Customer/" + GlobalAppContext.d() + "/ClearNotifications"));
        } catch (Exception e) {
            Log.e("API Calls", "Unable to clear notifications");
            return false;
        }
    }
}
