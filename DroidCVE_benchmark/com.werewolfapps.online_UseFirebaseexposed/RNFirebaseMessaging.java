package io.invertase.firebase.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import io.invertase.firebase.Utils;

/* loaded from: classes.dex */
public class RNFirebaseMessaging extends ReactContextBaseJavaModule {
    private static final String TAG = "RNFirebaseMessaging";

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return TAG;
    }

    public RNFirebaseMessaging(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(reactApplicationContext);
        localBroadcastManager.registerReceiver(new MessageReceiver(), new IntentFilter(RNFirebaseMessagingService.MESSAGE_EVENT));
        localBroadcastManager.registerReceiver(new RefreshTokenReceiver(), new IntentFilter(RNFirebaseInstanceIdService.TOKEN_REFRESH_EVENT));
    }

    @ReactMethod
    public void getToken(Promise promise) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase token: " + token);
        promise.resolve(token);
    }

    @ReactMethod
    public void requestPermission(Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void hasPermission(Promise promise) {
        promise.resolve(true);
    }

    @ReactMethod
    public void sendMessage(ReadableMap readableMap, Promise promise) {
        if (!readableMap.hasKey("to")) {
            promise.reject("messaging/invalid-message", "The supplied message is missing a 'to' field");
            return;
        }
        RemoteMessage.Builder builder = new RemoteMessage.Builder(readableMap.getString("to"));
        if (readableMap.hasKey("collapseKey")) {
            builder = builder.setCollapseKey(readableMap.getString("collapseKey"));
        }
        if (readableMap.hasKey("messageId")) {
            builder = builder.setMessageId(readableMap.getString("messageId"));
        }
        if (readableMap.hasKey("messageType")) {
            builder = builder.setMessageType(readableMap.getString("messageType"));
        }
        if (readableMap.hasKey("ttl")) {
            builder = builder.setTtl(readableMap.getInt("ttl"));
        }
        if (readableMap.hasKey("data")) {
            ReadableMap map = readableMap.getMap("data");
            ReadableMapKeySetIterator keySetIterator = map.keySetIterator();
            while (keySetIterator.hasNextKey()) {
                String nextKey = keySetIterator.nextKey();
                builder = builder.addData(nextKey, map.getString(nextKey));
            }
        }
        FirebaseMessaging.getInstance().send(builder.build());
        promise.resolve(null);
    }

    @ReactMethod
    public void subscribeToTopic(String str, Promise promise) {
        FirebaseMessaging.getInstance().subscribeToTopic(str);
        promise.resolve(null);
    }

    @ReactMethod
    public void unsubscribeFromTopic(String str, Promise promise) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(str);
        promise.resolve(null);
    }

    /* loaded from: classes.dex */
    private class MessageReceiver extends BroadcastReceiver {
        private MessageReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (RNFirebaseMessaging.this.getReactApplicationContext().hasActiveCatalystInstance()) {
                Log.d(RNFirebaseMessaging.TAG, "Received new message");
                Utils.sendEvent(RNFirebaseMessaging.this.getReactApplicationContext(), "messaging_message_received", MessagingSerializer.parseRemoteMessage((RemoteMessage) intent.getParcelableExtra("message")));
            }
        }
    }

    /* loaded from: classes.dex */
    private class RefreshTokenReceiver extends BroadcastReceiver {
        private RefreshTokenReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (RNFirebaseMessaging.this.getReactApplicationContext().hasActiveCatalystInstance()) {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(RNFirebaseMessaging.TAG, "Received new FCM token: " + token);
                Utils.sendEvent(RNFirebaseMessaging.this.getReactApplicationContext(), "messaging_token_refreshed", token);
            }
        }
    }
}
