package com.samsung.android.visionintelligence.lens.qr;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SemSystemProperties;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.core.net.MailTo;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.AddressStruct;
import com.google.zxing.client.result.BirthdayStruct;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.EventStruct;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.NameStruct;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.WifiParsedResult;
import com.google.zxing.client.result.iot.IoTParsedResult;
import com.google.zxing.client.result.iot.IoTResultType;
import com.samsung.android.sdk.bixby2.action.ActionHandler;
import com.samsung.android.visionintelligence.QRTextActivity;
import com.samsung.android.visionintelligence.R;
import com.samsung.android.visionintelligence.define.PackageUtil;
import com.samsung.android.visionintelligence.display.LayoutHelper;
import com.samsung.android.visionintelligence.feature.VIFeature;
import com.samsung.android.visionintelligence.util.ObjectHelper;
import dalvik.annotation.MethodParameters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/* loaded from: classes2.dex */
public class LinkAction extends LinkActionLegacy {
    private static final String BAIDU_PRODUCT_URL_END_PREFIX = "&tn=samsung";
    private static final String BAIDU_PRODUCT_URL_PREFIX = "http://graph.baidu.com/s?barcode=";
    private static final String ESIM_QR_CODE_PREFIX = "LPA:";
    private static final String FACEBOOK_QR_PATTERN = "((http(s)?)://)?(www[.])?(facebook.com)/.*";
    private static final String GALAXY_APP_STORE = "samsungapps://ProductDetail/";
    private static final String GALAXY_WEARABLE_DEEP_LINK_PREFIX = "galaxywearablefromqr://launch?qrdata=";
    private static final String GALAXY_WEARABLE_MANAGER_PACKAGE_NAME = "com.samsung.android.app.watchmanager";
    private static final String GOOGLE_PRODUCT_URL_PREFIX = "http://google.com/search?q=";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String INSTAGRAM_QR_PATTERN = "((http(s)?)://)?(www[.])?(instagram.com)/.*";
    private static final String INTENT_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";
    private static final String MEMO_CLASS_NAME = "com.samsung.android.app.memo.Main";
    private static final String MEMO_PACKAGE_NAME = "com.samsung.android.app.memo";
    private static final String PLAY_STORE_URL_PREFIX = "https://play.google.com/store/apps/details?id=";
    private static final String SAMSUNGNOTE_CLASS_NAME = "com.samsung.android.app.notes.composer.ComposerBaseActivity";
    private static final String SAMSUNGNOTE_PACKAGE_NAME = "com.samsung.android.app.notes";
    private static final String SAMSUNG_CMC_PREFIX = "cmc://setting?";
    private static final String SAMSUNG_HEALTH_URL_PREFIX = "https://shealth.samsung.com/deepLink?sc_id=";
    private static final String SAMSUNG_INTERNET_SEARCH_DEEP_LINK = "samsunginternet://search?keyword=";
    private static final String SIM_CARD_MANAGER_DEEP_LINK_PREFIX = "esimmanagerfromqr://launch?qrdata=";
    private static final String SMART_THINGS_CHINA_URL = "";
    private static final String SMART_THINGS_DEEP_LINK_PREFIX = "scapp_qronboarding://";
    private static final String SMART_THINGS_GLOBAL_URL = "https://qr.samsungiots.com";
    private static final String SPAY_DANA_DEEP_LINK_PREFIX = "spay://qr.spay/dana?data=";
    private static final String SPAY_DANA_M_URL_PREFIX = "https://m.dana.id/";
    private static final String SPAY_DANA_URL_PREFIX = "https://qr.dana.id/";
    private static final String SPAY_INDONESIA_STANDARD_PREFIX = "000201";
    private static final String TAG = "LinkAction";
    private static final int TYPE_EAP = 3;
    private static final int TYPE_NOPASS = 0;
    private static final int TYPE_SAE = 4;
    private static final int TYPE_WAPI_PSK = 5;
    private static final int TYPE_WEP = 1;
    private static final int TYPE_WPA = 2;
    private static final Map<String, Integer> mNumberTypeMap = new LinkedHashMap<String, Integer>() { // from class: com.samsung.android.visionintelligence.lens.qr.LinkAction.1
        {
            put("HOME_FAX", 5);
            put("WORK_FAX", 4);
            put("HOME", 1);
            put("WORK", 3);
            put("CELL", 2);
            put("TEL", 2);
            put("VOICE", 7);
            put("OTHER", 7);
            put("PAGER", 6);
            put("CALLBACK", 8);
        }
    };
    private static final Map<QrCodeActionType, IntentGetter> mIntentGetterMap = new AnonymousClass2();

    /* renamed from: com.samsung.android.visionintelligence.lens.qr.LinkAction$2, reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass2 extends HashMap<QrCodeActionType, IntentGetter> {
        public AnonymousClass2() {
            put(QrCodeActionType.CALENDAR, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$sXWK9HT9xNxsTRqfY4uwbCBvvuA
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent addEventIntent;
                    addEventIntent = LinkAction.getAddEventIntent(context, parsedResult);
                    return addEventIntent;
                }
            });
            put(QrCodeActionType.CONTACTS_ADD, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$svTVUuI3tKKsFRaP-XxLUHdQe_g
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent addToContactsNameCardIntent;
                    addToContactsNameCardIntent = LinkAction.getAddToContactsNameCardIntent(context, parsedResult);
                    return addToContactsNameCardIntent;
                }
            });
            put(QrCodeActionType.CONTACTS_CALL, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$_2z7vs6nPLrQHyME3j764IaVwa4
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent callIntent;
                    callIntent = LinkAction.getCallIntent(context, parsedResult);
                    return callIntent;
                }
            });
            put(QrCodeActionType.CONTACTS_EMAIL, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$5moEF5pFC2-e_EbcUT1bK4HqeQ0
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent sendEmailIntent;
                    sendEmailIntent = LinkAction.getSendEmailIntent(context, parsedResult);
                    return sendEmailIntent;
                }
            });
            put(QrCodeActionType.CONTACTS_MESSAGE, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$9GtwwuIelln8rP9kCnqMc7OqEQE
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent sendMessageIntent;
                    sendMessageIntent = LinkAction.getSendMessageIntent(context, parsedResult);
                    return sendMessageIntent;
                }
            });
            put(QrCodeActionType.EMAIL, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$x2jSaD24wcxexdMYkbfFh779wi0
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent sendEmailIntent;
                    sendEmailIntent = LinkAction.getSendEmailIntent(context, parsedResult);
                    return sendEmailIntent;
                }
            });
            put(QrCodeActionType.ESIM_GALAXY_WEARABLE, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$Y_jVNWmO2lwilt5fZVN-2fsgghk
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.ESIM_SIM_CARD_MANAGER, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$m_BaNmms4V5Yb3AYL1u0mUzJri4
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.FACEBOOK, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$gnyi2ckZ0bFCTF-zcRDvHhTZ5d8
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.INSTAGRAM, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$WU_7CyeRN2a3MEOnIkaRrZ8i2zM
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.IOT, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$mBkzaAS0UDChRfdgMqKluV-7J1s
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent connectSmartThingsIntent;
                    connectSmartThingsIntent = LinkAction.getConnectSmartThingsIntent(context, parsedResult);
                    return connectSmartThingsIntent;
                }
            });
            put(QrCodeActionType.ISBN, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$ittebjxSVxFiOIhFkgc6e8p4cdM
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent searchProductIntent;
                    searchProductIntent = LinkAction.getSearchProductIntent(context, parsedResult);
                    return searchProductIntent;
                }
            });
            put(QrCodeActionType.MAP, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$4x1JxQ3ZVu3uFs9fsZ-QM506Yl8
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent openMapIntent;
                    openMapIntent = LinkAction.getOpenMapIntent(context, parsedResult);
                    return openMapIntent;
                }
            });
            put(QrCodeActionType.PHONE_NUMBER, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$tVB6q5WJpmSffZMIfrWVWIQr4W4
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent callIntent;
                    callIntent = LinkAction.getCallIntent(context, parsedResult);
                    return callIntent;
                }
            });
            put(QrCodeActionType.PLAY_STORE, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$f9MbdgAQ5pQGwzR9xANjw_-025k
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.PRODUCT, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$LwbOkgqCoBERx-T-v0Xo76nM8rc
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent searchProductIntent;
                    searchProductIntent = LinkAction.getSearchProductIntent(context, parsedResult);
                    return searchProductIntent;
                }
            });
            put(QrCodeActionType.SAMSUNG_CMC, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$ah1BqfeB70WL0v_bVikg3ldKt9E
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.SAMSUNG_HEALTH, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$gla8DxS_y3DBFE5MVTjBcxxmxzg
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.SAMSUNG_PAY, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$HbqOBKWLYwvUGX-HBa9RPsR6LUs
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.SMS, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$PpKV5iJWbj6mXyizyM4hMAXmwiI
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent sendMessageIntent;
                    sendMessageIntent = LinkAction.getSendMessageIntent(context, parsedResult);
                    return sendMessageIntent;
                }
            });
            put(QrCodeActionType.TEXT_COPY, null);
            put(QrCodeActionType.TEXT_VIEW, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$wFSp2Hi9SQVCr22NtIR-X4t2ltE
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkTextIntent;
                    linkTextIntent = LinkAction.getLinkTextIntent(context, parsedResult);
                    return linkTextIntent;
                }
            });
            put(QrCodeActionType.TEXT_SEARCH_WEB, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$Dsvsuo_yeRUsFC6jAwXN1NVpscQ
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent searchWebBySamsungInternetIntent;
                    searchWebBySamsungInternetIntent = LinkAction.getSearchWebBySamsungInternetIntent(context, parsedResult);
                    return searchWebBySamsungInternetIntent;
                }
            });
            put(QrCodeActionType.URL_COPY, null);
            put(QrCodeActionType.URL_OPEN, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$Z9acHsT0Lo3F48Dyv3BajVWoJ9k
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent linkUriIntent;
                    linkUriIntent = LinkAction.getLinkUriIntent(context, parsedResult);
                    return linkUriIntent;
                }
            });
            put(QrCodeActionType.WIFI, new IntentGetter() { // from class: com.samsung.android.visionintelligence.lens.qr.-$$Lambda$LinkAction$2$w4DK46mwkdl2GQwyvuvUrQXjlKQ
                @Override // com.samsung.android.visionintelligence.lens.qr.LinkAction.IntentGetter
                public final Intent get(Context context, ParsedResult parsedResult) {
                    Intent connectToWifiIntent;
                    connectToWifiIntent = LinkAction.getConnectToWifiIntent(context, parsedResult);
                    return connectToWifiIntent;
                }
            });
        }
    }

    /* renamed from: com.samsung.android.visionintelligence.lens.qr.LinkAction$3, reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass3 {
        public static final /* synthetic */ int[] $SwitchMap$com$google$zxing$client$result$EventStruct$Type;

        static {
            int[] iArr = new int[EventStruct.Type.values().length];
            $SwitchMap$com$google$zxing$client$result$EventStruct$Type = iArr;
            try {
                iArr[EventStruct.Type.ANNIVERSARY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$EventStruct$Type[EventStruct.Type.CUSTOM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$zxing$client$result$EventStruct$Type[EventStruct.Type.OTHER.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface IntentGetter {
        @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
        Intent get(Context context, ParsedResult parsedResult);
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    private static void copyText(Context context, ParsedResult parsedResult) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("QR Code Text", parsedResult.getDisplayResult()));
        Toast.makeText(context, context.getString(R.string.qrdialog_action_copy_toast), 1).show();
    }

    @MethodParameters(accessFlags = {0, 0, 0}, names = {"context", ActionHandler.ACTION_TYPE, "parsedResult"})
    public static void doLinkAction(Context context, QrCodeActionType qrCodeActionType, ParsedResult parsedResult) throws ActivityNotFoundException, IllegalArgumentException {
        if (parsedResult == null) {
            throw new IllegalArgumentException("parsedResult is null!");
        }
        if (qrCodeActionType == QrCodeActionType.URL_COPY || qrCodeActionType == QrCodeActionType.TEXT_COPY) {
            copyText(context, parsedResult);
        } else {
            startActivity(context, ((IntentGetter) Objects.requireNonNull(mIntentGetterMap.get(qrCodeActionType))).get(context, parsedResult));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getAddEventIntent(Context context, ParsedResult parsedResult) {
        CalendarParsedResult calendarParsedResult = (CalendarParsedResult) parsedResult;
        Intent intent = new Intent("android.intent.action.INSERT");
        intent.setData(CalendarContract.Events.CONTENT_URI);
        String description = calendarParsedResult.getDescription();
        Date end = calendarParsedResult.getEnd();
        String location = calendarParsedResult.getLocation();
        Date start = calendarParsedResult.getStart();
        String summary = calendarParsedResult.getSummary();
        boolean isStartAllDay = calendarParsedResult.isStartAllDay();
        intent.putExtra("title", summary);
        intent.putExtra("beginTime", start.getTime());
        if (end != null) {
            intent.putExtra("endTime", end.getTime());
        }
        intent.putExtra("description", description);
        intent.putExtra("eventLocation", location);
        intent.putExtra("allDay", isStartAllDay);
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getAddToContactsNameCardIntent(Context context, ParsedResult parsedResult) {
        AddressBookParsedResult addressBookParsedResult = (AddressBookParsedResult) parsedResult;
        Intent intent = new Intent("android.intent.action.INSERT");
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        ArrayList arrayList = new ArrayList();
        updateContactName(addressBookParsedResult, intent, arrayList);
        updateContactNumber(addressBookParsedResult, arrayList);
        updateContactOrganization(addressBookParsedResult, arrayList);
        updateContactEmail(addressBookParsedResult, arrayList);
        updateContactAddress(addressBookParsedResult, intent, arrayList);
        updateContactUrl(addressBookParsedResult, arrayList);
        updateContactNote(addressBookParsedResult, arrayList);
        updateContactNickName(addressBookParsedResult, arrayList);
        updateContactBirthday(addressBookParsedResult, arrayList);
        updateContactEvent(addressBookParsedResult, arrayList);
        if (arrayList.size() != 0) {
            intent.putExtra("data", arrayList);
        }
        intent.addFlags(335544320);
        return intent;
    }

    @MethodParameters(accessFlags = {0}, names = {"type"})
    private static int getAddressType(String str) {
        if (str == null) {
            return 3;
        }
        str.hashCode();
        if (str.equals("HOME")) {
            return 1;
        }
        return !str.equals("WORK") ? 0 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getCallIntent(Context context, ParsedResult parsedResult) {
        Intent intent = new Intent("android.intent.action.DIAL", parsedResult.getType() == ParsedResultType.TEL ? Uri.parse(((TelParsedResult) parsedResult).getTelURI()) : parsedResult.getType() == ParsedResultType.ADDRESSBOOK ? Uri.fromParts("tel", ((AddressBookParsedResult) parsedResult).getPhoneNumbers()[0], null) : Uri.fromParts("tel", parsedResult.getDisplayResult(), null));
        intent.addFlags(268435456);
        return intent;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "uriString"})
    private static Intent getConnectSamsungCmcIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getConnectSmartThingsIntent(Context context, ParsedResult parsedResult) {
        IoTParsedResult ioTParsedResult = (IoTParsedResult) parsedResult;
        String qRString = ioTParsedResult.getQRString();
        if (ioTParsedResult.getIoTType() == IoTResultType.SMART_TAG_QR) {
            return getLaunchBrowserIntent(context, qRString);
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(SMART_THINGS_DEEP_LINK_PREFIX + qRString));
        intent.addFlags(268435456);
        if (isLaunchAvailable(context, intent)) {
            return intent;
        }
        Log.w(TAG, "getConnectSmartThingsIntent : Activity cannot found");
        if (ioTParsedResult.getIoTType() == IoTResultType.ON_BOARDING_STANDARD_QR) {
            return getLaunchBrowserIntent(context, qRString);
        }
        return getLaunchBrowserIntent(context, isSmartThingsInChina(context) ? "" : SMART_THINGS_GLOBAL_URL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {16, 16}, names = {"context", "parsedResult"})
    public static Intent getConnectToWifiIntent(Context context, ParsedResult parsedResult) {
        WifiParsedResult wifiParsedResult = (WifiParsedResult) parsedResult;
        String ssid = wifiParsedResult.getSsid();
        String password = wifiParsedResult.getPassword();
        String networkEncryption = wifiParsedResult.getNetworkEncryption();
        boolean isHidden = wifiParsedResult.isHidden();
        Intent intent = new Intent(INTENT_WIFI_SETTINGS);
        intent.putExtra("AUTH_TYPE", getWifiAuthType(networkEncryption.toUpperCase(Locale.getDefault())));
        intent.putExtra("SSID", ssid);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("HIDDEN", isHidden);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268468224);
        return intent;
    }

    @MethodParameters(accessFlags = {0}, names = {"type"})
    private static int getDefaultNumberType(String str) {
        for (String str2 : mNumberTypeMap.keySet()) {
            if (str.contains(str2)) {
                return mNumberTypeMap.get(str2).intValue();
            }
        }
        return 0;
    }

    @MethodParameters(accessFlags = {0}, names = {"type"})
    private static int getEmailType(String str) {
        if (str == null) {
            return 3;
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2015525726:
                if (str.equals("MOBILE")) {
                    c = 0;
                    break;
                }
                break;
            case 2223327:
                if (str.equals("HOME")) {
                    c = 1;
                    break;
                }
                break;
            case 2670353:
                if (str.equals("WORK")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 4;
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                if (str.contains("WORK")) {
                    return 2;
                }
                if (str.contains("HOME")) {
                    return 1;
                }
                return str.contains("MOBILE") ? 4 : 0;
        }
    }

    @MethodParameters(accessFlags = {0}, names = {"type"})
    private static int getEventType(EventStruct.Type type) {
        int i = AnonymousClass3.$SwitchMap$com$google$zxing$client$result$EventStruct$Type[type.ordinal()];
        if (i != 1) {
            return i != 2 ? 2 : 0;
        }
        return 1;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "uriString"})
    private static Intent getLaunchBrowserIntent(Context context, String str) {
        if (str == null) {
            Log.e(TAG, "getLaunchBrowserIntent : uriString is null.");
            return null;
        }
        String lowerCaseProtocolUri = getLowerCaseProtocolUri(str);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(268435456);
        intent.setData(Uri.parse(lowerCaseProtocolUri));
        return intent;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "packageName"})
    private static Intent getLaunchGalaxyStoreIntent(Context context, String str) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(GALAXY_APP_STORE + str));
        intent.putExtra("type", "cover");
        intent.addFlags(335544352);
        return intent;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "uriString"})
    private static Intent getLaunchGalaxyWearableIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(GALAXY_WEARABLE_DEEP_LINK_PREFIX + str));
        intent.addFlags(268435456);
        return !isLaunchAvailable(context, intent) ? getLaunchGalaxyStoreIntent(context, GALAXY_WEARABLE_MANAGER_PACKAGE_NAME) : intent;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "resultText"})
    private static Intent getLaunchPostViewIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        if (isInstalledApp(context, MEMO_PACKAGE_NAME)) {
            intent.setComponent(new ComponentName(MEMO_PACKAGE_NAME, MEMO_CLASS_NAME));
        } else {
            if (!isInstalledApp(context, SAMSUNGNOTE_PACKAGE_NAME)) {
                Intent createIntent = ObjectHelper.createIntent(context, (Class<?>) QRTextActivity.class);
                createIntent.putExtra(LinkActionLegacy.QRCODE_TEXT, str);
                Log.e(TAG, "getLaunchPostViewIntent : Memo and Note package is not installed.");
                return createIntent;
            }
            intent.setType("text/plain");
            intent.setComponent(new ComponentName(SAMSUNGNOTE_PACKAGE_NAME, SAMSUNGNOTE_CLASS_NAME));
        }
        intent.putExtra("android.intent.extra.TEXT", str);
        intent.addFlags(268435456);
        return intent;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "uriString"})
    private static Intent getLaunchSimCardManagerIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(SIM_CARD_MANAGER_DEEP_LINK_PREFIX + str));
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getLinkTextIntent(Context context, ParsedResult parsedResult) {
        return (isSpayDanaUrl(parsedResult.getDisplayResult()) && PackageUtil.isPkgExist(context, LinkActionLegacy.PACKAGE_NAME_SPAY_INDONESIA)) ? getSendSpayDeepLinkIntent(context, parsedResult.getDisplayResult()) : getLaunchPostViewIntent(context, parsedResult.getDisplayResult());
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getLinkUriIntent(Context context, ParsedResult parsedResult) {
        if (!(parsedResult instanceof URIParsedResult)) {
            Log.e(TAG, "getLinkUriIntent : ParsedResult is NOT instanceof URIParsedResult.");
            return null;
        }
        String uri = ((URIParsedResult) parsedResult).getURI();
        if (uri != null) {
            return (isSpayDanaUrl(uri) && PackageUtil.isPkgExist(context, LinkActionLegacy.PACKAGE_NAME_SPAY_INDONESIA)) ? getSendSpayDeepLinkIntent(context, uri) : isSamsungCmcUrl(uri) ? getConnectSamsungCmcIntent(context, uri) : isEsimQrPrefix(uri) ? VIFeature.SUPPORT_ESIM_MANAGER ? getLaunchSimCardManagerIntent(context, uri) : getLaunchGalaxyWearableIntent(context, uri) : getLaunchBrowserIntent(context, uri);
        }
        Log.e(TAG, "getLinkUriIntent : uriString is null.");
        return null;
    }

    @MethodParameters(accessFlags = {0}, names = {"uri"})
    private static String getLowerCaseProtocolUri(String str) {
        String lowerCase = str.toLowerCase(Locale.getDefault());
        String str2 = HTTPS;
        if (!lowerCase.startsWith(HTTPS)) {
            String lowerCase2 = str.toLowerCase(Locale.getDefault());
            str2 = HTTP;
            if (!lowerCase2.startsWith(HTTP)) {
                return str;
            }
            if (str.split("://").length > 1) {
                return HTTP + str.split("://")[1];
            }
        } else if (str.split("://").length > 1) {
            return HTTPS + str.split("://")[1];
        }
        return str2;
    }

    @MethodParameters(accessFlags = {0}, names = {"type"})
    private static int getNumberType(String str) {
        if (str == null) {
            return 7;
        }
        Map<String, Integer> map = mNumberTypeMap;
        return map.get(str) == null ? getDefaultNumberType(str) : map.get(str).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getOpenMapIntent(Context context, ParsedResult parsedResult) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(((GeoParsedResult) parsedResult).getGeoURI()));
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getSearchProductIntent(Context context, ParsedResult parsedResult) {
        return SemSystemProperties.getCountryCode().equals("CN") ? getLaunchBrowserIntent(context, BAIDU_PRODUCT_URL_PREFIX + parsedResult.getDisplayResult() + BAIDU_PRODUCT_URL_END_PREFIX) : getLaunchBrowserIntent(context, GOOGLE_PRODUCT_URL_PREFIX + parsedResult.getDisplayResult());
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    private static Intent getSearchWebByGoogleSearchIntent(Context context, ParsedResult parsedResult) {
        Intent intent = new Intent("android.intent.action.WEB_SEARCH");
        intent.putExtra("query", parsedResult.getDisplayResult());
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getSearchWebBySamsungInternetIntent(Context context, ParsedResult parsedResult) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(268435456);
        intent.setData(Uri.parse(SAMSUNG_INTERNET_SEARCH_DEEP_LINK + parsedResult.getDisplayResult()));
        if (isLaunchAvailable(context, intent)) {
            return intent;
        }
        Log.e(TAG, "getSearchWebBySamsungInternetIntent : Activity cannot found.");
        return getSearchWebByGoogleSearchIntent(context, parsedResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getSendEmailIntent(Context context, ParsedResult parsedResult) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(MailTo.MAILTO_SCHEME));
        intent.addFlags(268435456);
        if (parsedResult.getType() == ParsedResultType.EMAIL_ADDRESS) {
            EmailAddressParsedResult emailAddressParsedResult = (EmailAddressParsedResult) parsedResult;
            String[] tos = emailAddressParsedResult.getTos();
            String subject = emailAddressParsedResult.getSubject();
            String body = emailAddressParsedResult.getBody();
            String[] cCs = emailAddressParsedResult.getCCs();
            String[] bCCs = emailAddressParsedResult.getBCCs();
            if (tos != null && tos.length > 0) {
                intent.putExtra("android.intent.extra.EMAIL", tos);
            }
            if (cCs != null && cCs.length > 0) {
                intent.putExtra("android.intent.extra.CC", cCs);
            }
            if (bCCs != null && bCCs.length > 0) {
                intent.putExtra("android.intent.extra.BCC", bCCs);
            }
            intent.putExtra("android.intent.extra.SUBJECT", subject);
            intent.putExtra("android.intent.extra.TEXT", body);
        } else if (parsedResult.getType() == ParsedResultType.ADDRESSBOOK) {
            intent.putExtra("android.intent.extra.EMAIL", ((AddressBookParsedResult) parsedResult).getEmails());
        }
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @MethodParameters(accessFlags = {0, 0}, names = {"context", "parsedResult"})
    public static Intent getSendMessageIntent(Context context, ParsedResult parsedResult) {
        Intent intent = new Intent("android.intent.action.SENDTO", parsedResult.getType() == ParsedResultType.SMS ? Uri.parse(((SMSParsedResult) parsedResult).getSMSURI()) : parsedResult.getType() == ParsedResultType.ADDRESSBOOK ? Uri.fromParts("smsto", ((AddressBookParsedResult) parsedResult).getPhoneNumbers()[0], null) : Uri.fromParts("smsto", parsedResult.getDisplayResult(), null));
        intent.addFlags(268435456);
        return intent;
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "resultText"})
    private static Intent getSendSpayDeepLinkIntent(Context context, String str) {
        String str2 = SPAY_DANA_DEEP_LINK_PREFIX + str;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(str2));
        intent.addFlags(268435456);
        return intent;
    }

    @MethodParameters(accessFlags = {0}, names = {"type"})
    private static int getWifiAuthType(String str) {
        if (str == null) {
            return 0;
        }
        String upperCase = str.toUpperCase(Locale.getDefault());
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 68404:
                if (upperCase.equals("EAP")) {
                    c = 0;
                    break;
                }
                break;
            case 81847:
                if (upperCase.equals("SAE")) {
                    c = 1;
                    break;
                }
                break;
            case 85826:
                if (upperCase.equals("WEP")) {
                    c = 2;
                    break;
                }
                break;
            case 86152:
                if (upperCase.equals("WPA")) {
                    c = 3;
                    break;
                }
                break;
            case 2670762:
                if (upperCase.equals("WPA2")) {
                    c = 4;
                    break;
                }
                break;
            case 1219499692:
                if (upperCase.equals("WAPI_PSK")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 3;
            case 1:
                return 4;
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
                return 5;
            default:
                return 0;
        }
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isEsimQrPrefix(String str) {
        return str.startsWith(ESIM_QR_CODE_PREFIX);
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isFacebookUrl(String str) {
        return str.matches(FACEBOOK_QR_PATTERN);
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isInstagramUrl(String str) {
        return str.matches(INSTAGRAM_QR_PATTERN);
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "packageName"})
    public static boolean isInstalledApp(Context context, String str) {
        try {
            context.getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "intent"})
    private static boolean isLaunchAvailable(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    @MethodParameters(accessFlags = {0, 0, 0}, names = {"context", ActionHandler.ACTION_TYPE, "parsedResult"})
    public static boolean isLaunchAvailable(Context context, QrCodeActionType qrCodeActionType, ParsedResult parsedResult) {
        return isLaunchAvailable(context, ((IntentGetter) Objects.requireNonNull(mIntentGetterMap.get(qrCodeActionType))).get(context, parsedResult));
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isPlayStoreUrl(String str) {
        return str.startsWith(PLAY_STORE_URL_PREFIX);
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isSamsungCmcUrl(String str) {
        return str.startsWith(SAMSUNG_CMC_PREFIX);
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isSamsungHealthUrl(String str) {
        return str.startsWith(SAMSUNG_HEALTH_URL_PREFIX);
    }

    @MethodParameters(accessFlags = {0}, names = {"context"})
    public static boolean isSmartThingsInChina(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(LayoutHelper.DEVICE_TYPE_PHONE);
        String str = "";
        if (telephonyManager != null) {
            String simCountryIso = telephonyManager.getSimCountryIso();
            if (simCountryIso == null || "".equals(simCountryIso)) {
                String networkCountryIso = telephonyManager.getNetworkCountryIso();
                str = (networkCountryIso == null || "".equals(networkCountryIso)) ? Resources.getSystem().getConfiguration().getLocales().get(0).getCountry() : networkCountryIso;
            } else {
                str = simCountryIso;
            }
        }
        Log.d(TAG, "isSmartThingsInChina : countryIso=" + str);
        if (str != null) {
            return "CN".equals(str.toUpperCase(Locale.getDefault()));
        }
        return false;
    }

    @MethodParameters(accessFlags = {0}, names = {"qrData"})
    public static boolean isSpayDanaUrl(String str) {
        return str.startsWith(SPAY_DANA_URL_PREFIX) || str.startsWith(SPAY_DANA_M_URL_PREFIX) || str.startsWith(SPAY_INDONESIA_STANDARD_PREFIX);
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"context", "intent"})
    private static void startActivity(Context context, Intent intent) throws ActivityNotFoundException {
        KeyguardManager keyguardManager = (KeyguardManager) context.getApplicationContext().getSystemService("keyguard");
        if (!keyguardManager.isKeyguardLocked()) {
            context.startActivity(intent);
        } else {
            keyguardManager.semSetPendingIntentAfterUnlock(PendingIntent.getActivity(context, 0, intent, 134217728), new Intent());
        }
    }

    @MethodParameters(accessFlags = {0, 0, 0}, names = {"result", "intent", "values"})
    private static void updateContactAddress(AddressBookParsedResult addressBookParsedResult, Intent intent, ArrayList<ContentValues> arrayList) {
        String[] addresses = addressBookParsedResult.getAddresses();
        String[] addressTypes = addressBookParsedResult.getAddressTypes();
        AddressStruct[] addressStructs = addressBookParsedResult.getAddressStructs();
        if (addressStructs == null) {
            if (addresses != null && addresses.length > 0) {
                intent.putExtra("postal", addresses[0]);
            }
            if (addressTypes == null || addressTypes.length <= 0) {
                return;
            }
            intent.putExtra("postal_type", getAddressType(addressTypes[0]));
            return;
        }
        for (AddressStruct addressStruct : addressStructs) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("mimetype", "vnd.android.cursor.item/postal-address_v2");
            contentValues.put("data4", addressStruct.street);
            contentValues.put("data5", addressStruct.postOfficeBox);
            contentValues.put("data8", addressStruct.region);
            contentValues.put("data7", addressStruct.locality);
            contentValues.put("data9", addressStruct.postalCode);
            contentValues.put("data10", addressStruct.country);
            int addressType = getAddressType(addressStruct.type);
            contentValues.put("data2", Integer.valueOf(addressType));
            if (addressType == 0) {
                contentValues.put("data3", addressStruct.type);
            }
            arrayList.add(contentValues);
        }
        if (addresses != null) {
            for (int i = 0; i < addresses.length; i++) {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("mimetype", "vnd.android.cursor.item/postal-address_v2");
                contentValues2.put("data1", addresses[i]);
                contentValues2.put("data2", Integer.valueOf(getAddressType(addressTypes[i])));
                arrayList.add(contentValues2);
            }
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactBirthday(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        BirthdayStruct birthdayStruct = addressBookParsedResult.getBirthdayStruct();
        if (birthdayStruct != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("mimetype", "vnd.android.cursor.item/contact_event");
            contentValues.put("data1", birthdayStruct.birthday);
            contentValues.put("data2", (Integer) 3);
            contentValues.put("data15", birthdayStruct.solarType);
            contentValues.put("data14", birthdayStruct.solarDate);
            arrayList.add(contentValues);
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactEmail(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        String[] emails = addressBookParsedResult.getEmails();
        String[] emailTypes = addressBookParsedResult.getEmailTypes();
        if (emails != null) {
            for (int i = 0; i < emails.length; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("mimetype", "vnd.android.cursor.item/email_v2");
                contentValues.put("data1", emails[i]);
                if (emailTypes != null) {
                    try {
                        int emailType = getEmailType(emailTypes[i]);
                        contentValues.put("data2", Integer.valueOf(emailType));
                        if (emailType == 0) {
                            contentValues.put("data3", emailTypes[i]);
                        }
                    } catch (ArrayIndexOutOfBoundsException unused) {
                        Log.e(TAG, "ArrayIndexOutOfBoundsException occurred! Input default e-mail type.");
                        contentValues.put("data2", (Integer) 3);
                    }
                }
                arrayList.add(contentValues);
            }
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactEvent(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        EventStruct[] eventStructs = addressBookParsedResult.getEventStructs();
        if (eventStructs != null) {
            for (EventStruct eventStruct : eventStructs) {
                if (eventStruct != null && eventStruct.type == EventStruct.Type.ANNIVERSARY) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("mimetype", "vnd.android.cursor.item/contact_event");
                    contentValues.put("data1", eventStruct.date);
                    int eventType = getEventType(eventStruct.type);
                    Log.d(TAG, "eventStruct type:" + eventStruct.type + ", contactType:" + eventType);
                    contentValues.put("data2", Integer.valueOf(eventType));
                    if (!TextUtils.isEmpty(eventStruct.title)) {
                        contentValues.put("data3", eventStruct.title);
                    }
                    contentValues.put("data15", eventStruct.solarType);
                    contentValues.put("data14", eventStruct.solarDate);
                    arrayList.add(contentValues);
                }
            }
        }
    }

    @MethodParameters(accessFlags = {0, 0, 0}, names = {"result", "intent", "values"})
    private static void updateContactName(AddressBookParsedResult addressBookParsedResult, Intent intent, ArrayList<ContentValues> arrayList) {
        String[] names = addressBookParsedResult.getNames();
        String[] selfFormatNames = addressBookParsedResult.getSelfFormatNames();
        NameStruct[] nameStructs = addressBookParsedResult.getNameStructs();
        if (nameStructs != null) {
            for (NameStruct nameStruct : nameStructs) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("mimetype", "vnd.android.cursor.item/name");
                contentValues.put("data3", nameStruct.familyName);
                contentValues.put("data2", nameStruct.givenName);
                contentValues.put("data5", nameStruct.additionalName);
                contentValues.put("data4", nameStruct.honorificPrefix);
                contentValues.put("data6", nameStruct.honorificSuffix);
                contentValues.put("data9", nameStruct.phoneticFamilyName);
                contentValues.put("data7", nameStruct.phoneticGivenName);
                contentValues.put("data8", nameStruct.phoneticAdditionalName);
                arrayList.add(contentValues);
            }
        }
        if (names != null && names.length > 0) {
            intent.putExtra("name", names[0]);
        } else {
            if (selfFormatNames == null || selfFormatNames.length <= 0) {
                return;
            }
            intent.putExtra("name", selfFormatNames[0]);
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactNickName(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        String[] nicknames = addressBookParsedResult.getNicknames();
        if (nicknames != null) {
            for (String str : nicknames) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("mimetype", "vnd.android.cursor.item/nickname");
                contentValues.put("data1", str);
                arrayList.add(contentValues);
            }
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactNote(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        String note = addressBookParsedResult.getNote();
        if (note != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("mimetype", "vnd.android.cursor.item/note");
            contentValues.put("data1", note);
            arrayList.add(contentValues);
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactNumber(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        String[] phoneNumbers = addressBookParsedResult.getPhoneNumbers();
        String[] phoneTypes = addressBookParsedResult.getPhoneTypes();
        if (phoneNumbers != null) {
            for (int i = 0; i < phoneNumbers.length; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
                contentValues.put("data1", phoneNumbers[i]);
                if (phoneTypes != null) {
                    try {
                        int numberType = getNumberType(phoneTypes[i]);
                        contentValues.put("data2", Integer.valueOf(numberType));
                        if (numberType == 0) {
                            contentValues.put("data3", phoneTypes[i]);
                        }
                    } catch (ArrayIndexOutOfBoundsException unused) {
                        Log.e(TAG, "ArrayIndexOutOfBoundsException occurred! Input default phone type.");
                        contentValues.put("data2", (Integer) 7);
                    }
                }
                arrayList.add(contentValues);
            }
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactOrganization(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        String title = addressBookParsedResult.getTitle();
        String org2 = addressBookParsedResult.getOrg();
        if (org2 != null) {
            ContentValues contentValues = new ContentValues();
            int indexOf = org2.indexOf(StringUtils.LF);
            String substring = indexOf != -1 ? org2.substring(indexOf + 1) : null;
            String str = org2.split(StringUtils.LF)[0];
            contentValues.put("mimetype", "vnd.android.cursor.item/organization");
            contentValues.put("data1", str);
            contentValues.put("data5", substring);
            contentValues.put("data4", title);
            arrayList.add(contentValues);
        }
    }

    @MethodParameters(accessFlags = {0, 0}, names = {"result", "values"})
    private static void updateContactUrl(AddressBookParsedResult addressBookParsedResult, ArrayList<ContentValues> arrayList) {
        String[] uRLs = addressBookParsedResult.getURLs();
        if (uRLs != null) {
            for (String str : uRLs) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("mimetype", "vnd.android.cursor.item/website");
                contentValues.put("data1", str);
                arrayList.add(contentValues);
            }
        }
    }
}
