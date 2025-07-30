package com.sap.byd.cod.pushnotificationplugin;

import android.R;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.internal.view.SupportMenu;
import android.text.Html;
import android.text.Spanned;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sap.cloud4custex.logger.ExLOG;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.internal.cache.DiskLruCache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FCMService extends FirebaseMessagingService implements PushConstants {
    private static HashMap<Integer, ArrayList<String>> messageMap = new HashMap<>();

    public void setNotification(int i, String str) {
        ArrayList<String> arrayList = messageMap.get(Integer.valueOf(i));
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            messageMap.put(Integer.valueOf(i), arrayList);
        }
        if (str.isEmpty()) {
            arrayList.clear();
        } else {
            arrayList.add(str);
        }
    }

    private void clearAllNotifications() {
        ((NotificationManager) getSystemService("notification")).cancelAll();
    }

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        ExLOG.d("FCMService:onMessageReceived", "onMessage - from: " + from);
        Bundle bundle = new Bundle();
        if (remoteMessage.getNotification() != null) {
            bundle.putString("title", remoteMessage.getNotification().getTitle());
            bundle.putString(PushConstants.MESSAGE, remoteMessage.getNotification().getBody());
            bundle.putString(PushConstants.SOUND, remoteMessage.getNotification().getSound());
            bundle.putString(PushConstants.ICON, remoteMessage.getNotification().getIcon());
            bundle.putString(PushConstants.COLOR, remoteMessage.getNotification().getColor());
        }
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        if (isAvailableSender(from)) {
            Context applicationContext = getApplicationContext();
            SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
            boolean z = sharedPreferences.getBoolean(PushConstants.FORCE_SHOW, false);
            boolean z2 = sharedPreferences.getBoolean(PushConstants.CLEAR_BADGE, false);
            Bundle normalizeExtras = normalizeExtras(applicationContext, bundle, sharedPreferences.getString(PushConstants.MESSAGE_KEY, PushConstants.MESSAGE), sharedPreferences.getString(PushConstants.TITLE_KEY, "title"));
            if (z2) {
                clearAllNotifications();
            }
            if (!z && PushPlugin.isInForeground()) {
                ExLOG.d("FCMService:onMessageReceived", PushConstants.FOREGROUND);
                normalizeExtras.putBoolean(PushConstants.FOREGROUND, true);
                normalizeExtras.putBoolean(PushConstants.COLDSTART, false);
                PushPlugin.sendExtras(normalizeExtras);
                return;
            }
            if (z && PushPlugin.isInForeground()) {
                ExLOG.d("FCMService:onMessageReceived", "foreground force");
                normalizeExtras.putBoolean(PushConstants.FOREGROUND, true);
                normalizeExtras.putBoolean(PushConstants.COLDSTART, false);
                showNotificationIfPossible(applicationContext, normalizeExtras);
                return;
            }
            ExLOG.d("FCMService:onMessageReceived", "background");
            normalizeExtras.putBoolean(PushConstants.FOREGROUND, false);
            normalizeExtras.putBoolean(PushConstants.COLDSTART, PushPlugin.isActive());
            showNotificationIfPossible(applicationContext, normalizeExtras);
        }
    }

    private void replaceKey(Context context, String str, String str2, Bundle bundle, Bundle bundle2) {
        Object obj = bundle.get(str);
        if (obj != null) {
            if (obj instanceof String) {
                bundle2.putString(str2, localizeKey(context, str2, (String) obj));
                return;
            }
            if (obj instanceof Boolean) {
                bundle2.putBoolean(str2, ((Boolean) obj).booleanValue());
            } else if (obj instanceof Number) {
                bundle2.putDouble(str2, ((Number) obj).doubleValue());
            } else {
                bundle2.putString(str2, String.valueOf(obj));
            }
        }
    }

    private String localizeKey(Context context, String str, String str2) {
        if (!str.equals("title") && !str.equals(PushConstants.MESSAGE) && !str.equals(PushConstants.SUMMARY_TEXT)) {
            return str2;
        }
        try {
            JSONObject jSONObject = new JSONObject(str2);
            String string = jSONObject.getString(PushConstants.LOC_KEY);
            ArrayList arrayList = new ArrayList();
            if (!jSONObject.isNull(PushConstants.LOC_DATA)) {
                JSONArray jSONArray = new JSONArray(jSONObject.getString(PushConstants.LOC_DATA));
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.getString(i));
                }
            }
            String packageName = context.getPackageName();
            Resources resources = context.getResources();
            int identifier = resources.getIdentifier(string, "string", packageName);
            if (identifier != 0) {
                return resources.getString(identifier, arrayList.toArray());
            }
            ExLOG.d("FCMService:localizeKey", "can't find resource for locale key = " + string);
            return str2;
        } catch (JSONException e) {
            ExLOG.d("FCMService:localizeKey", "no locale found for key = " + str + ", error " + e.getMessage());
            return str2;
        }
    }

    private String normalizeKey(String str, String str2, String str3) {
        if (str.equals(PushConstants.BODY) || str.equals(PushConstants.ALERT) || str.equals(PushConstants.MP_MESSAGE) || str.equals(PushConstants.GCM_NOTIFICATION_BODY) || str.equals(PushConstants.TWILIO_BODY) || str.equals(str2)) {
            return PushConstants.MESSAGE;
        }
        if (str.equals(PushConstants.TWILIO_TITLE) || str.equals(PushConstants.SUBJECT) || str.equals(str3)) {
            return "title";
        }
        if (str.equals(PushConstants.MSGCNT) || str.equals(PushConstants.BADGE)) {
            return "count";
        }
        if (str.equals(PushConstants.SOUNDNAME) || str.equals(PushConstants.TWILIO_SOUND)) {
            return PushConstants.SOUND;
        }
        if (str.startsWith(PushConstants.GCM_NOTIFICATION)) {
            return str.substring(17, str.length());
        }
        if (str.startsWith(PushConstants.GCM_N)) {
            return str.substring(7, str.length());
        }
        return str.startsWith(PushConstants.UA_PREFIX) ? str.substring(22, str.length()).toLowerCase() : str;
    }

    private Bundle normalizeExtras(Context context, Bundle bundle, String str, String str2) {
        ExLOG.d("FCMService:normalizeExtras", "normalize extras");
        Bundle bundle2 = new Bundle();
        for (String str3 : bundle.keySet()) {
            ExLOG.d("FCMService:normalizeExtras", "key = " + str3);
            if (str3.equals("data") || str3.equals(PushConstants.MESSAGE) || str3.equals(str)) {
                Object obj = bundle.get(str3);
                if ((obj instanceof String) && ((String) obj).startsWith("{")) {
                    ExLOG.d("FCMService:normalizeExtras", "extracting nested message data from key = " + str3);
                    try {
                        JSONObject jSONObject = new JSONObject((String) obj);
                        if (!jSONObject.has(PushConstants.ALERT) && !jSONObject.has(PushConstants.MESSAGE) && !jSONObject.has(PushConstants.BODY) && !jSONObject.has("title") && !jSONObject.has(str) && !jSONObject.has(str2)) {
                            if (jSONObject.has(PushConstants.LOC_KEY) || jSONObject.has(PushConstants.LOC_DATA)) {
                                String normalizeKey = normalizeKey(str3, str, str2);
                                ExLOG.d("FCMService:normalizeExtras", "replace key " + str3 + " with " + normalizeKey);
                                replaceKey(context, str3, normalizeKey, bundle, bundle2);
                            }
                        }
                        Iterator<String> keys = jSONObject.keys();
                        while (keys.hasNext()) {
                            String next = keys.next();
                            ExLOG.d("FCMService:normalizeExtras", "key = data/" + next);
                            String string = jSONObject.getString(next);
                            String normalizeKey2 = normalizeKey(next, str, str2);
                            bundle2.putString(normalizeKey2, localizeKey(context, normalizeKey2, string));
                        }
                    } catch (JSONException unused) {
                        ExLOG.e("FCMService:normalizeExtras", "normalizeExtras: JSON exception");
                    }
                } else {
                    String normalizeKey3 = normalizeKey(str3, str, str2);
                    ExLOG.d("FCMService:normalizeExtras", "replace key " + str3 + " with " + normalizeKey3);
                    replaceKey(context, str3, normalizeKey3, bundle, bundle2);
                }
            } else if (str3.equals("notification")) {
                Bundle bundle3 = bundle.getBundle(str3);
                for (String str4 : bundle3.keySet()) {
                    ExLOG.d("FCMService:normalizeExtras", "notifkey = " + str4);
                    String normalizeKey4 = normalizeKey(str4, str, str2);
                    ExLOG.d("FCMService:normalizeExtras", "replace key " + str4 + " with " + normalizeKey4);
                    bundle2.putString(normalizeKey4, localizeKey(context, normalizeKey4, bundle3.getString(str4)));
                }
            } else {
                String normalizeKey5 = normalizeKey(str3, str, str2);
                ExLOG.d("FCMService:normalizeExtras", "replace key " + str3 + " with " + normalizeKey5);
                replaceKey(context, str3, normalizeKey5, bundle, bundle2);
            }
        }
        return bundle2;
    }

    private int extractBadgeCount(Bundle bundle) {
        String string = bundle.getString("count");
        if (string == null) {
            return -1;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            ExLOG.e("FCMService:extractBadgeCount", e.getLocalizedMessage());
            return -1;
        }
    }

    private void showNotificationIfPossible(Context context, Bundle bundle) {
        String string = bundle.getString(PushConstants.MESSAGE);
        String string2 = bundle.getString("title");
        String string3 = bundle.getString(PushConstants.CONTENT_AVAILABLE);
        String string4 = bundle.getString(PushConstants.FORCE_START);
        int extractBadgeCount = extractBadgeCount(bundle);
        if (Build.VERSION.SDK_INT < 26) {
            ExLOG.d("FCMService:showNotificationIfPossible", "count =[" + extractBadgeCount + "]");
            if (extractBadgeCount >= 0) {
                ShortcutBadger.applyCount(context, extractBadgeCount);
            } else {
                ShortcutBadger.removeCount(context);
            }
        }
        ExLOG.d("FCMService:showNotificationIfPossible", "message =[" + string + "]");
        ExLOG.d("FCMService:showNotificationIfPossible", "title =[" + string2 + "]");
        ExLOG.d("FCMService:showNotificationIfPossible", "contentAvailable =[" + string3 + "]");
        ExLOG.d("FCMService:showNotificationIfPossible", "forceStart =[" + string4 + "]");
        if ((string != null && string.length() != 0) || (string2 != null && string2.length() != 0)) {
            ExLOG.d("FCMService:showNotificationIfPossible", "create notification");
            if (string2 == null || string2.isEmpty()) {
                bundle.putString("title", getAppName(this));
            }
            createNotification(context, bundle);
        }
        if (!PushPlugin.isActive() && DiskLruCache.VERSION_1.equals(string4)) {
            ExLOG.d("FCMService:showNotificationIfPossible", "app is not running but we should start it and put in background");
            Intent intent = new Intent(this, (Class<?>) PushHandlerActivity.class);
            intent.addFlags(268435456);
            intent.putExtra(PushConstants.PUSH_BUNDLE, bundle);
            intent.putExtra(PushConstants.START_IN_BACKGROUND, true);
            intent.putExtra(PushConstants.FOREGROUND, false);
            startActivity(intent);
            return;
        }
        if (DiskLruCache.VERSION_1.equals(string3)) {
            ExLOG.d("FCMService:showNotificationIfPossible", "app is not running and content available true");
            ExLOG.d("FCMService:showNotificationIfPossible", "send notification event");
            PushPlugin.sendExtras(bundle);
        }
    }

    public void createNotification(Context context, Bundle bundle) {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("c4c_app_push_notification_channel", "push notifications", 4);
            notificationChannel.setDescription("This channel is for c4c extended app push notifications.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(SupportMenu.CATEGORY_MASK);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int parseInt = parseInt(PushConstants.NOT_ID, bundle);
        Intent intent = new Intent(this, (Class<?>) PushHandlerActivity.class);
        intent.addFlags(603979776);
        intent.putExtra(PushConstants.PUSH_BUNDLE, bundle);
        intent.putExtra(PushConstants.NOT_ID, parseInt);
        SecureRandom secureRandom = new SecureRandom();
        PendingIntent activity = PendingIntent.getActivity(this, secureRandom.nextInt(), intent, 134217728);
        Intent intent2 = new Intent(this, (Class<?>) PushDismissedHandler.class);
        intent2.putExtra(PushConstants.PUSH_BUNDLE, bundle);
        intent2.putExtra(PushConstants.NOT_ID, parseInt);
        intent2.putExtra(PushConstants.DISMISSED, true);
        intent2.setAction(PushConstants.PUSH_DISMISSED);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, secureRandom.nextInt(), intent2, 268435456);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "c4c_app_push_notification_channel");
        builder.setWhen(System.currentTimeMillis()).setContentTitle(fromHtml(bundle.getString("title"))).setTicker(fromHtml(bundle.getString("title"))).setContentIntent(activity).setDeleteIntent(broadcast).setAutoCancel(true);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
        String string = sharedPreferences.getString(PushConstants.ICON, null);
        String string2 = sharedPreferences.getString(PushConstants.ICON_COLOR, null);
        boolean z = sharedPreferences.getBoolean(PushConstants.SOUND, true);
        boolean z2 = sharedPreferences.getBoolean(PushConstants.VIBRATE, true);
        ExLOG.d("FCMService:createNotification", "stored icon=" + string);
        ExLOG.d("FCMService:createNotification", "stored iconColor=" + string2);
        ExLOG.d("FCMService:createNotification", "stored sound=" + z);
        ExLOG.d("FCMService:createNotification", "stored vibrate=" + z2);
        setNotificationVibration(bundle, Boolean.valueOf(z2), builder);
        setNotificationIconColor(bundle.getString(PushConstants.COLOR), builder, string2);
        setNotificationSmallIcon(context, bundle, packageName, resources, builder, string);
        setNotificationLargeIcon(bundle, packageName, resources, builder);
        if (z) {
            setNotificationSound(context, bundle, builder);
        }
        setNotificationLedColor(bundle, builder);
        setNotificationPriority(bundle, builder);
        setNotificationMessage(parseInt, bundle, builder);
        setNotificationCount(context, bundle, builder);
        setNotificationOngoing(bundle, builder);
        setVisibility(context, bundle, builder);
        createActions(bundle, builder, resources, packageName, parseInt);
        createCustomActions(bundle, builder, resources, packageName, parseInt);
        notificationManager.notify(parseInt, builder.build());
    }

    private void createCustomActions(Bundle bundle, NotificationCompat.Builder builder, Resources resources, String str, int i) {
        ExLOG.d("FCMService:createCustomActions", "create createCustomActions");
        if (bundle.containsKey(PushConstants.CATEGORY_APPROVAL)) {
            createApprovalActions(bundle, builder, i);
        }
    }

    private void createApprovalActions(Bundle bundle, NotificationCompat.Builder builder, int i) {
        builder.setAutoCancel(true);
        try {
            JSONObject jSONObject = new JSONObject(getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getString("quickactions_categories", null));
            JSONObject jSONObject2 = jSONObject.getJSONObject(jSONObject.keys().next());
            String string = jSONObject2.getString("servicePath");
            JSONObject jSONObject3 = jSONObject2.getJSONObject(PushConstants.ACTIONS);
            Iterator<String> keys = jSONObject3.keys();
            SecureRandom secureRandom = new SecureRandom();
            while (keys.hasNext()) {
                String next = keys.next();
                JSONObject jSONObject4 = jSONObject3.getJSONObject(next);
                String string2 = jSONObject4.getString("title");
                String string3 = jSONObject4.getString("serviceUrlEndpoint");
                String string4 = jSONObject4.getString("serviceUrlParameterName");
                try {
                    NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_menu_add, string2, PendingIntent.getService(this, secureRandom.nextInt(1000), getActionIntent(PushPlugin.convertBundleToJson(bundle).toString(), "com.sap.cloud4custex.quickactions." + next, string, string3, string4, i), 0));
                    builder.setPriority(4);
                    builder.addAction(action);
                } catch (Exception e) {
                    e = e;
                    ExLOG.e("FCMService:createApprovalActions", "Unable to parse categories " + e);
                    return;
                }
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    private Intent getActionIntent(String str, String str2, String str3, String str4, String str5, int i) {
        Intent intent = new Intent(this, (Class<?>) QuickActionsIntentService.class);
        intent.putExtra("payload", str);
        intent.putExtra("servicePath", str3);
        intent.putExtra("serviceUrlEndpoint", str4);
        intent.putExtra("serviceUrlParameterName", str5);
        intent.putExtra(PushConstants.NOT_ID, i);
        intent.setAction(str2);
        return intent;
    }

    private void updateIntent(Intent intent, String str, Bundle bundle, boolean z, int i) {
        intent.putExtra(PushConstants.CALLBACK, str);
        intent.putExtra(PushConstants.PUSH_BUNDLE, bundle);
        intent.putExtra(PushConstants.FOREGROUND, z);
        intent.putExtra(PushConstants.NOT_ID, i);
    }

    private void createActions(Bundle bundle, NotificationCompat.Builder builder, Resources resources, String str, int i) {
        ArrayList arrayList;
        JSONArray jSONArray;
        String str2;
        int i2;
        JSONObject jSONObject;
        PendingIntent broadcast;
        NotificationCompat.Builder builder2;
        Intent intent;
        NotificationCompat.Builder builder3 = builder;
        int i3 = i;
        String str3 = "title";
        ExLOG.d("FCMService:createActions", "create actions: with in-line");
        String string = bundle.getString(PushConstants.ACTIONS);
        if (string != null) {
            try {
                JSONArray jSONArray2 = new JSONArray(string);
                ArrayList arrayList2 = new ArrayList();
                int i4 = 0;
                while (i4 < jSONArray2.length()) {
                    int nextInt = new SecureRandom().nextInt(2000000000) + 1;
                    ExLOG.d("FCMService:createActions", "adding action");
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i4);
                    StringBuilder sb = new StringBuilder();
                    int i5 = i4;
                    sb.append("adding callback = ");
                    sb.append(jSONObject2.getString(PushConstants.CALLBACK));
                    ExLOG.d("FCMService:createActions", sb.toString());
                    boolean optBoolean = jSONObject2.optBoolean(PushConstants.FOREGROUND, true);
                    boolean optBoolean2 = jSONObject2.optBoolean("inline", false);
                    if (optBoolean2) {
                        ExLOG.d("FCMService:createActions", "Version: " + Build.VERSION.SDK_INT + " = 23");
                        if (Build.VERSION.SDK_INT <= 23) {
                            ExLOG.d("FCMService:createActions", "push activity");
                            intent = new Intent(this, (Class<?>) PushHandlerActivity.class);
                        } else {
                            ExLOG.d("FCMService:createActions", "push receiver");
                            intent = new Intent(this, (Class<?>) BackgroundActionButtonHandler.class);
                        }
                        Intent intent2 = intent;
                        str2 = str3;
                        i2 = i5;
                        arrayList = arrayList2;
                        jSONArray = jSONArray2;
                        updateIntent(intent2, jSONObject2.getString(PushConstants.CALLBACK), bundle, optBoolean, i);
                        if (Build.VERSION.SDK_INT <= 23) {
                            ExLOG.d("FCMService:createActions", "push activity for notId " + i3);
                            broadcast = PendingIntent.getActivity(this, nextInt, intent2, 1073741824);
                        } else {
                            ExLOG.d("FCMService:createActions", "push receiver for notId " + i3);
                            broadcast = PendingIntent.getBroadcast(this, nextInt, intent2, 1073741824);
                        }
                        jSONObject = jSONObject2;
                    } else {
                        arrayList = arrayList2;
                        jSONArray = jSONArray2;
                        str2 = str3;
                        i2 = i5;
                        if (optBoolean) {
                            Intent intent3 = new Intent(this, (Class<?>) PushHandlerActivity.class);
                            updateIntent(intent3, jSONObject2.getString(PushConstants.CALLBACK), bundle, optBoolean, i);
                            broadcast = PendingIntent.getActivity(this, nextInt, intent3, 134217728);
                            jSONObject = jSONObject2;
                        } else {
                            Intent intent4 = new Intent(this, (Class<?>) BackgroundActionButtonHandler.class);
                            jSONObject = jSONObject2;
                            updateIntent(intent4, jSONObject2.getString(PushConstants.CALLBACK), bundle, optBoolean, i);
                            broadcast = PendingIntent.getBroadcast(this, nextInt, intent4, 134217728);
                        }
                    }
                    String str4 = str2;
                    NotificationCompat.Action.Builder builder4 = new NotificationCompat.Action.Builder(getImageId(resources, jSONObject.optString(PushConstants.ICON, ""), str), jSONObject.getString(str4), broadcast);
                    if (optBoolean2) {
                        ExLOG.d("FCMService:createActions", "create remote input");
                        builder4.addRemoteInput(new RemoteInput.Builder(PushConstants.INLINE_REPLY).setLabel(jSONObject.optString(PushConstants.INLINE_REPLY_LABEL, "Enter your reply here")).build());
                    }
                    NotificationCompat.Action build = builder4.build();
                    ArrayList arrayList3 = arrayList;
                    arrayList3.add(builder4.build());
                    if (optBoolean2) {
                        builder2 = builder;
                        builder2.addAction(build);
                    } else {
                        builder2 = builder;
                        builder2.addAction(getImageId(resources, jSONObject.optString(PushConstants.ICON, ""), str), jSONObject.getString(str4), broadcast);
                    }
                    i3 = i;
                    builder3 = builder2;
                    str3 = str4;
                    arrayList2 = arrayList3;
                    jSONArray2 = jSONArray;
                    i4 = i2 + 1;
                }
                ArrayList arrayList4 = arrayList2;
                builder3.extend(new NotificationCompat.WearableExtender().addActions(arrayList4));
                arrayList4.clear();
            } catch (JSONException unused) {
            }
        }
    }

    private void setNotificationCount(Context context, Bundle bundle, NotificationCompat.Builder builder) {
        int extractBadgeCount = extractBadgeCount(bundle);
        if (extractBadgeCount >= 0) {
            ExLOG.d("FCMService:setNotificationCount", "count =[" + extractBadgeCount + "]");
            builder.setNumber(extractBadgeCount);
        }
    }

    private void setVisibility(Context context, Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.VISIBILITY);
        if (string != null) {
            try {
                Integer valueOf = Integer.valueOf(Integer.parseInt(string));
                if (valueOf.intValue() >= -1 && valueOf.intValue() <= 1) {
                    builder.setVisibility(valueOf.intValue());
                } else {
                    ExLOG.e("FCMService:setVisibility", "Visibility parameter must be between -1 and 1");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNotificationVibration(Bundle bundle, Boolean bool, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.VIBRATION_PATTERN);
        if (string != null) {
            String[] split = string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            long[] jArr = new long[split.length];
            for (int i = 0; i < split.length; i++) {
                try {
                    jArr[i] = Long.parseLong(split[i].trim());
                } catch (NumberFormatException unused) {
                }
            }
            builder.setVibrate(jArr);
            return;
        }
        if (bool.booleanValue()) {
            builder.setDefaults(2);
        }
    }

    private void setNotificationOngoing(Bundle bundle, NotificationCompat.Builder builder) {
        builder.setOngoing(Boolean.parseBoolean(bundle.getString(PushConstants.ONGOING, "false")));
    }

    private void setNotificationMessage(int i, Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.MESSAGE);
        String string2 = bundle.getString(PushConstants.STYLE, "text");
        if (PushConstants.STYLE_INBOX.equals(string2)) {
            setNotification(i, string);
            builder.setContentText(fromHtml(string));
            ArrayList<String> arrayList = messageMap.get(Integer.valueOf(i));
            Integer valueOf = Integer.valueOf(arrayList.size());
            if (valueOf.intValue() > 1) {
                String num = valueOf.toString();
                String str = valueOf + " more";
                if (bundle.getString(PushConstants.SUMMARY_TEXT) != null) {
                    str = bundle.getString(PushConstants.SUMMARY_TEXT).replace("%n%", num);
                }
                NotificationCompat.InboxStyle summaryText = new NotificationCompat.InboxStyle().setBigContentTitle(fromHtml(bundle.getString("title"))).setSummaryText(fromHtml(str));
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    summaryText.addLine(fromHtml(arrayList.get(size)));
                }
                builder.setStyle(summaryText);
                return;
            }
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            if (string != null) {
                bigTextStyle.bigText(fromHtml(string));
                bigTextStyle.setBigContentTitle(fromHtml(bundle.getString("title")));
                builder.setStyle(bigTextStyle);
                return;
            }
            return;
        }
        if ("picture".equals(string2)) {
            setNotification(i, "");
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(getBitmapFromURL(bundle.getString("picture")));
            bigPictureStyle.setBigContentTitle(fromHtml(bundle.getString("title")));
            bigPictureStyle.setSummaryText(fromHtml(bundle.getString(PushConstants.SUMMARY_TEXT)));
            builder.setContentTitle(fromHtml(bundle.getString("title")));
            builder.setContentText(fromHtml(string));
            builder.setStyle(bigPictureStyle);
            return;
        }
        setNotification(i, "");
        NotificationCompat.BigTextStyle bigTextStyle2 = new NotificationCompat.BigTextStyle();
        if (string != null) {
            builder.setContentText(fromHtml(string));
            bigTextStyle2.bigText(fromHtml(string));
            bigTextStyle2.setBigContentTitle(fromHtml(bundle.getString("title")));
            String string3 = bundle.getString(PushConstants.SUMMARY_TEXT);
            if (string3 != null) {
                bigTextStyle2.setSummaryText(fromHtml(string3));
            }
            builder.setStyle(bigTextStyle2);
        }
    }

    private void setNotificationSound(Context context, Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.SOUNDNAME);
        if (string == null) {
            string = bundle.getString(PushConstants.SOUND);
        }
        if (PushConstants.SOUND_RINGTONE.equals(string)) {
            builder.setSound(Settings.System.DEFAULT_RINGTONE_URI);
            return;
        }
        if (string != null && !string.contentEquals(PushConstants.SOUND_DEFAULT)) {
            Uri parse = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + string);
            ExLOG.d("FCMService:setNotificationSound", parse.toString());
            builder.setSound(parse);
            return;
        }
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
    }

    private void setNotificationLedColor(Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.LED_COLOR);
        if (string != null) {
            String[] split = string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            int[] iArr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                try {
                    iArr[i] = Integer.parseInt(split[i].trim());
                } catch (NumberFormatException unused) {
                }
            }
            if (iArr.length == 4) {
                builder.setLights(Color.argb(iArr[0], iArr[1], iArr[2], iArr[3]), 500, 500);
            } else {
                ExLOG.e("FCMService:setNotificationLedColor", "ledColor parameter must be an array of length == 4 (ARGB)");
            }
        }
    }

    private void setNotificationPriority(Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.PRIORITY);
        if (string != null) {
            try {
                Integer valueOf = Integer.valueOf(Integer.parseInt(string));
                if (valueOf.intValue() >= -2 && valueOf.intValue() <= 2) {
                    builder.setPriority(valueOf.intValue());
                } else {
                    ExLOG.e("FCMService:setNotificationPriority", "Priority parameter must be between -2 and 2");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(SupportMenu.CATEGORY_MASK);
        float width = bitmap.getWidth() / 2;
        float height = bitmap.getHeight() / 2;
        canvas.drawCircle(width, height, width < height ? width : height, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return createBitmap;
    }

    private void setNotificationLargeIcon(Bundle bundle, String str, Resources resources, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.IMAGE);
        String string2 = bundle.getString(PushConstants.IMAGE_TYPE, PushConstants.IMAGE_TYPE_SQUARE);
        if (string == null || "".equals(string)) {
            return;
        }
        if (string.startsWith("http://") || string.startsWith("https://")) {
            Bitmap bitmapFromURL = getBitmapFromURL(string);
            if (PushConstants.IMAGE_TYPE_SQUARE.equalsIgnoreCase(string2)) {
                builder.setLargeIcon(bitmapFromURL);
            } else {
                builder.setLargeIcon(getCircleBitmap(bitmapFromURL));
            }
            ExLOG.d("FCMService:setNotificationLargeIcon", "using remote large-icon from gcm");
            return;
        }
        try {
            Bitmap decodeStream = BitmapFactory.decodeStream(getAssets().open(string));
            if (PushConstants.IMAGE_TYPE_SQUARE.equalsIgnoreCase(string2)) {
                builder.setLargeIcon(decodeStream);
            } else {
                builder.setLargeIcon(getCircleBitmap(decodeStream));
            }
            ExLOG.d("FCMService:setNotificationLargeIcon", "using assets large-icon from gcm");
        } catch (IOException unused) {
            int imageId = getImageId(resources, string, str);
            if (imageId != 0) {
                builder.setLargeIcon(BitmapFactory.decodeResource(resources, imageId));
                ExLOG.d("FCMService:setNotificationLargeIcon", "using resources large-icon from gcm");
            } else {
                ExLOG.d("FCMService:setNotificationLargeIcon", "Not setting large icon");
            }
        }
    }

    private int getImageId(Resources resources, String str, String str2) {
        int identifier = resources.getIdentifier(str, PushConstants.DRAWABLE, str2);
        return identifier == 0 ? resources.getIdentifier(str, "mipmap", str2) : identifier;
    }

    private void setNotificationSmallIcon(Context context, Bundle bundle, String str, Resources resources, NotificationCompat.Builder builder, String str2) {
        int i;
        String string = bundle.getString(PushConstants.ICON);
        if (string != null && !"".equals(string)) {
            i = getImageId(resources, string, str);
            ExLOG.d("FCMService:setNotificationSmallIcon", "using icon from plugin options");
        } else if (str2 == null || "".equals(str2)) {
            i = 0;
        } else {
            i = getImageId(resources, str2, str);
            ExLOG.d("FCMService:setNotificationSmallIcon", "using icon from plugin options");
        }
        if (i == 0) {
            ExLOG.d("FCMService:setNotificationSmallIcon", "no icon resource found - using application icon");
            i = com.sap.cloud4custex.R.drawable.icon_transparent;
        }
        builder.setSmallIcon(i);
    }

    private void setNotificationIconColor(String str, NotificationCompat.Builder builder, String str2) {
        int parseColor;
        if (str != null && !"".equals(str)) {
            try {
                parseColor = Color.parseColor(str);
            } catch (IllegalArgumentException unused) {
                ExLOG.e("FCMService:setNotificationIconColor", "couldn't parse color from android options");
            }
        } else {
            if (str2 != null && !"".equals(str2)) {
                try {
                    parseColor = Color.parseColor(str2);
                } catch (IllegalArgumentException unused2) {
                    ExLOG.e("FCMService:setNotificationIconColor", "couldn't parse color from android options");
                }
            }
            parseColor = 0;
        }
        if (parseColor != 0) {
            builder.setColor(parseColor);
        }
    }

    public Bitmap getBitmapFromURL(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppName(Context context) {
        return (String) context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
    }

    private int parseInt(String str, Bundle bundle) {
        try {
            return Integer.parseInt(bundle.getString(str));
        } catch (NumberFormatException e) {
            ExLOG.e("FCMService:parseInt", "Number format exception - Error parsing " + str + ": " + e.getMessage());
            return 0;
        } catch (Exception e2) {
            ExLOG.e("FCMService:parseInt", "Number format exception - Error parsing " + str + ": " + e2.getMessage());
            return 0;
        }
    }

    private Spanned fromHtml(String str) {
        if (str != null) {
            return Html.fromHtml(str);
        }
        return null;
    }

    private boolean isAvailableSender(String str) {
        String string = getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getString(PushConstants.SENDER_ID, "");
        ExLOG.d("FCMService:isAvailableSender", "sender id = " + string);
        return str.equals(string) || str.startsWith("/topics/");
    }
}
