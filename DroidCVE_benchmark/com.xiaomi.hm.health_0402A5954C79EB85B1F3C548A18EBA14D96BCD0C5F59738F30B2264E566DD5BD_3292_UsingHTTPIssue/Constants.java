package com.xiaomi.market.sdk;

import android.text.TextUtils;

/* loaded from: classes2.dex */
public class Constants {
    public static final String APK_HASH = "apkHash";
    public static final String APK_SIZE = "apkSize";
    public static final String APK_URL = "apk";
    public static final String DIFF_HASH = "diffFileHash";
    public static final String DIFF_SIZE = "diffFileSize";
    public static final String DIFF_URL = "diffFile";
    public static final String FITNESS = "fitness";
    public static final String HOST = "host";
    public static final String JSON_CLIENT_ID = "clientId";
    public static final String JSON_COUNTRY = "co";
    public static final String JSON_DEBUG = "debug";
    public static final String JSON_DENSITY = "density";
    public static final String JSON_DEVICE_ID = "deviceId";
    public static final String JSON_FEATURE = "feature";
    public static final String JSON_FILTER_INFO = "info";
    public static final String JSON_GLES_VERSION = "glEsVersion";
    public static final String JSON_GL_EXTENSION = "glExtension";
    public static final String JSON_LANGUAGE = "la";
    public static final String JSON_LIBRARY = "library";
    public static final String JSON_OLD_APK_HASH = "apkHash";
    public static final String JSON_PACKAGE_NAME = "packageName";
    public static final String JSON_RESOLUTION = "resolution";
    public static final String JSON_SCREEN_SIZE = "screenSize";
    public static final String JSON_SDK = "sdk";
    public static final String JSON_SDK_VERSION = "sdk";
    public static final String JSON_SIGNATURE = "signature";
    public static final String JSON_SYSTEM_RELEASE = "release";
    public static final String JSON_SYSTEM_VERSION = "version";
    public static final String JSON_TOUCH_SCREEN = "touchScreen";
    public static final String JSON_VERSION = "os";
    public static final String JSON_VERSION_CODE = "versionCode";
    public static final String JSON_XIAOMI_SDK_VERSION = "xiaomiSDKVersion";
    public static volatile String PRODUCT_URL_BASE = null;
    public static final String PRODUCT_URL_BASE_GLOBAL = "http://global.developer.xiaomi.com/autoupdate/";
    public static final String PRODUCT_URL_DEFAULT = "http://api.developer.xiaomi.com/autoupdate/";
    public static final String SOURCE = "source";
    public static final String STAGING_URL_BASE = "http://dev.staging.api.developer.n.xiaomi.com/autoupdate/";
    public static final String UPDATE_LOG = "updateLog";
    public static String UPDATE_URL = null;
    public static final String VERSION_CODE = "versionCode";
    public static final String VERSION_NAME = "versionName";

    /* loaded from: classes2.dex */
    public interface BaseColumns {
        public static final String _ID = "_id";
    }

    /* loaded from: classes2.dex */
    public static final class Update implements BaseColumns {
        public static final String APK_HASH = "apk_hash";
        public static final String APK_PATH = "apk_path";
        public static final String APK_URL = "apk_url";
        public static final String CREATE_TABLE = "CREATE TABLE update_download (_id INTEGER PRIMARY KEY AUTOINCREMENT,package_name TEXT,download_id INTEGER, version_code INTEGER, apk_url TEXT, apk_hash TEXT, diff_url TEXT, diff_hash TEXT, apk_path TEXT, UNIQUE(package_name));";
        public static final String DIFF_HASH = "diff_hash";
        public static final String DIFF_URL = "diff_url";
        public static final String DOWNLOAD_ID = "download_id";
        public static final String PACKAGE_NAME = "package_name";
        public static final String TABLE = "update_download";
        public static final String[] UPDATE_PROJECTION = {"update_download.package_name", "update_download.download_id", "update_download.version_code", "update_download.apk_url", "update_download.apk_hash", "update_download.diff_url", "update_download.diff_hash", "update_download.apk_path"};
        public static final String VERSION_CODE = "version_code";
    }

    /* loaded from: classes2.dex */
    public enum UpdateMethod {
        MARKET,
        DOWNLOAD_MANAGER
    }

    public static void setUseInternalProductUrl(boolean z) {
        PRODUCT_URL_BASE = z ? PRODUCT_URL_BASE_GLOBAL : PRODUCT_URL_DEFAULT;
    }

    public static void configURL() {
        if (Utils.STAGING) {
            UPDATE_URL = "http://dev.staging.api.developer.n.xiaomi.com/autoupdate/updateself";
            return;
        }
        if (TextUtils.isEmpty(PRODUCT_URL_BASE)) {
            if (Client.isMiui() && SystemProperties.getString("ro.product.mod_device", "").contains("_global")) {
                PRODUCT_URL_BASE = PRODUCT_URL_BASE_GLOBAL;
            } else {
                PRODUCT_URL_BASE = PRODUCT_URL_DEFAULT;
            }
        }
        UPDATE_URL = PRODUCT_URL_BASE + "updateself";
    }
}
