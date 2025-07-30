package info.varden.hauk;

import info.varden.hauk.http.security.CertificateValidationPolicy;
import info.varden.hauk.struct.Version;
import info.varden.hauk.system.preferences.Preference;
import info.varden.hauk.system.preferences.indexresolver.NightModeStyle;
import info.varden.hauk.system.preferences.indexresolver.ProxyTypeResolver;

/* loaded from: classes.dex */
public enum Constants {
    ;

    public static final String DATE_FORMAT_LOG = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_UI = "yyyy-MM-dd HH:mm:ss z";
    public static final String DEVICE_PREF_WARNED_BATTERY_SAVINGS = "hasPromptedBatterySavings";
    public static final int DURATION_UNIT_DAYS = 2;
    public static final int DURATION_UNIT_HOURS = 1;
    public static final int DURATION_UNIT_MINUTES = 0;
    public static final int E2E_AES_KEY_SIZE = 256;
    public static final String E2E_KD_FUNCTION = "PBKDF2WithHmacSHA1";
    public static final String E2E_KEY_SPEC = "AES";
    public static final int E2E_PBKDF2_ITERATIONS = 65536;
    public static final String E2E_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String EXTRA_BROADCAST_AUTHORIZATION_IDENTIFIER = "source";
    public static final String EXTRA_BROADCAST_RECEIVER_REGISTRY_INDEX = "dataRegistryIndex";
    public static final String EXTRA_GNSS_ACTIVE_TASK = "gnssActiveTask";
    public static final String EXTRA_HANDLER = "handler";
    public static final String EXTRA_SESSION_ALLOW_ADOPT = "adoptable";
    public static final String EXTRA_SESSION_MIN_DISTANCE = "minDistance";
    public static final String EXTRA_SHARE = "share";
    public static final String EXTRA_STOP_TASK = "stopTask";
    public static final String HTTP_HEADER_HAUK_VERSION = "X-Hauk-Version";
    public static final String INTENT_TYPE_COPY_LINK = "text/plain";
    public static final String PACKET_PARAM_ACCURACY = "acc";
    public static final String PACKET_PARAM_ADOPTABLE = "ado";
    public static final String PACKET_PARAM_DURATION = "dur";
    public static final String PACKET_PARAM_E2E_FLAG = "e2e";
    public static final String PACKET_PARAM_GROUP_PIN = "pin";
    public static final String PACKET_PARAM_ID_TO_ADOPT = "aid";
    public static final String PACKET_PARAM_INIT_VECTOR = "iv";
    public static final String PACKET_PARAM_INTERVAL = "int";
    public static final String PACKET_PARAM_LATITUDE = "lat";
    public static final String PACKET_PARAM_LONGITUDE = "lon";
    public static final String PACKET_PARAM_NICKNAME = "nic";
    public static final String PACKET_PARAM_PASSWORD = "pwd";
    public static final String PACKET_PARAM_PROVIDER_ACCURACY = "prv";
    public static final String PACKET_PARAM_SALT = "salt";
    public static final String PACKET_PARAM_SESSION_ID = "sid";
    public static final String PACKET_PARAM_SHARE_ID = "lid";
    public static final String PACKET_PARAM_SHARE_MODE = "mod";
    public static final String PACKET_PARAM_SPEED = "spd";
    public static final String PACKET_PARAM_TIMESTAMP = "time";
    public static final String PACKET_PARAM_USERNAME = "usr";
    public static final String PACKET_RESPONSE_OK = "OK";
    public static final int PORT_MAX = 65536;
    public static final int PORT_MIN = 0;
    public static final String REGEX_ADOPT_ID_FROM_LINK = "\\?([A-Za-z0-9-]+)";
    public static final String RESUME_AVAILABLE = "canResume";
    public static final String RESUME_CLIENT_VERSION = "clientVersion";
    public static final String RESUME_SESSION_PARAMS = "sessionParams";
    public static final String RESUME_SHARE_PARAMS = "shareParams";
    public static final String SHARED_PREFS_AUTHORIZATIONS = "broadcastAuthorizations";
    public static final String SHARED_PREFS_CONNECTION = "connectionPrefs";
    public static final String SHARED_PREFS_DEVICE_SPECS = "deviceSpecs";
    public static final String SHARED_PREFS_RESUMABLE = "sessionResumption";
    public static final String URL_PATH_ADOPT_SHARE = "api/adopt.php";
    public static final String URL_PATH_CREATE_NEW_LINK = "api/new-link.php";
    public static final String URL_PATH_CREATE_SHARE = "api/create.php";
    public static final String URL_PATH_POST_LOCATION = "api/post.php";
    public static final String URL_PATH_STOP_SHARING = "api/stop.php";
    public static final Preference<String> PREF_SERVER_ENCRYPTED = new Preference.EncryptedString("cryptServer", BuildConfig.FLAVOR);
    public static final Preference<ProxyTypeResolver> PREF_PROXY_TYPE = new Preference.Enum("proxyType", ProxyTypeResolver.SYSTEM_DEFAULT);
    public static final Preference<String> PREF_PROXY_HOST = new Preference.String("proxyHost", "localhost");
    public static final Preference<Integer> PREF_PROXY_PORT = new Preference.Integer("proxyPort", 9050);
    public static final Preference<Integer> PREF_CONNECTION_TIMEOUT = new Preference.Integer("connectTimeout", 10);
    public static final Preference<CertificateValidationPolicy> PREF_CERTIFICATE_VALIDATION = new Preference.Enum("tlsCertValidation", CertificateValidationPolicy.VALIDATE_ALL);
    public static final Preference<String> PREF_USERNAME_ENCRYPTED = new Preference.EncryptedString("cryptUsername", BuildConfig.FLAVOR);
    public static final Preference<String> PREF_PASSWORD_ENCRYPTED = new Preference.EncryptedString("cryptPassword", BuildConfig.FLAVOR);
    public static final String EXTRA_SESSION_DURATION = "duration";
    public static final Preference<Integer> PREF_DURATION = new Preference.Integer(EXTRA_SESSION_DURATION, 30);
    public static final String EXTRA_SESSION_INTERVAL = "interval";
    public static final Preference<Integer> PREF_INTERVAL = new Preference.Integer(EXTRA_SESSION_INTERVAL, 1);
    public static final Preference<Integer> PREF_NO_GNSS_FALLBACK = new Preference.Integer("noGnssFallback", 45);
    public static final Preference<Float> PREF_UPDATE_DISTANCE = new Preference.Float("minUpdateDistance", 0.0f);
    public static final String EXTRA_SESSION_CUSTOM_ID = "requestLink";
    public static final Preference<String> PREF_CUSTOM_ID = new Preference.String(EXTRA_SESSION_CUSTOM_ID, BuildConfig.FLAVOR);
    public static final Preference<Boolean> PREF_ENABLE_E2E = new Preference.Boolean("enableE2E", false);
    public static final String EXTRA_SESSION_E2E_PASSWORD = "e2ePassword";
    public static final Preference<String> PREF_E2E_PASSWORD = new Preference.EncryptedString(EXTRA_SESSION_E2E_PASSWORD, BuildConfig.FLAVOR);
    public static final Preference<String> PREF_NICKNAME = new Preference.String("nickname", BuildConfig.FLAVOR);
    public static final Preference<Integer> PREF_DURATION_UNIT = new Preference.Integer("durUnit", 0);
    public static final Preference<Boolean> PREF_ALLOW_ADOPTION = new Preference.Boolean("allowAdoption", true);
    public static final Preference<NightModeStyle> PREF_NIGHT_MODE = new Preference.Enum("nightMode", NightModeStyle.FOLLOW_SYSTEM);
    public static final Preference<Boolean> PREF_CONFIRM_STOP = new Preference.Boolean("confirmStop", true);
    public static final String EXTRA_SESSION_SERVER_URL = "server";

    @Deprecated
    public static final Preference<String> PREF_SERVER = new Preference.String(EXTRA_SESSION_SERVER_URL, BuildConfig.FLAVOR);
    public static final String EXTRA_SESSION_USERNAME = "username";

    @Deprecated
    public static final Preference<String> PREF_USERNAME = new Preference.String(EXTRA_SESSION_USERNAME, BuildConfig.FLAVOR);
    public static final String EXTRA_SESSION_PASSWORD = "password";

    @Deprecated
    public static final Preference<String> PREF_PASSWORD = new Preference.String(EXTRA_SESSION_PASSWORD, BuildConfig.FLAVOR);
    public static final Version VERSION_COMPAT_GROUP_SHARE = new Version("1.1");
    public static final Version VERSION_COMPAT_VIEW_ID = new Version("1.2");
    public static final Version VERSION_COMPAT_E2E_ENCRYPTION = new Version("1.5");
}
