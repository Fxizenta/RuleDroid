package com.adobe.mobile;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import android.os.Process;
import com.adobe.mobile.AbstractDatabaseBacking;
import com.adobe.mobile.AbstractHitDatabase;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AnalyticsWorker extends AbstractHitDatabase {
    protected static final String ANALYTICS_DB_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS HITS (ID INTEGER PRIMARY KEY AUTOINCREMENT, URL TEXT, TIMESTAMP INTEGER)";
    protected static final String ANALYTICS_FILENAME = "ADBMobileDataCache.sqlite";
    private static final int CONNECTION_TIMEOUT_MSEC = 5000;
    private static final int TIMESTAMP_DISABLED_WAIT_THRESHOLD = 60;
    private static String baseURL;
    protected SQLiteStatement _preparedInsertStatement = null;
    private static final SecureRandom randomGen = new SecureRandom();
    private static AnalyticsWorker _instance = null;
    private static final Object _instanceMutex = new Object();
    private static volatile boolean analyticsGetBaseURL_pred = true;

    protected AnalyticsWorker() {
        this.fileName = ANALYTICS_FILENAME;
        this.logPrefix = "Analytics";
        this.dbCreateStatement = ANALYTICS_DB_CREATE_STATEMENT;
        this.lastHitTimestamp = 0L;
        initDatabaseBacking(new File(StaticMethods.getCacheDirectory(), this.fileName));
        this.numberOfUnsentHits = getTrackingQueueSize();
    }

    static /* synthetic */ String access$000() {
        return getBaseURL();
    }

    private static String getBaseURL() {
        if (analyticsGetBaseURL_pred) {
            analyticsGetBaseURL_pred = false;
            StringBuilder sb = new StringBuilder();
            sb.append(MobileConfig.getInstance().getSSL() ? "https://" : "http://");
            sb.append(MobileConfig.getInstance().getTrackingServer());
            sb.append("/b/ss/");
            sb.append(StaticMethods.URLEncode(MobileConfig.getInstance().getReportSuiteIds()));
            sb.append("/");
            sb.append(MobileConfig.getInstance().getAnalyticsResponseType());
            sb.append("/JAVA-");
            sb.append("4.17.7-AN");
            sb.append("/s");
            baseURL = sb.toString();
            StaticMethods.logDebugFormat("Analytics - Setting base request URL(%s)", baseURL);
        }
        return baseURL;
    }

    public static AnalyticsWorker sharedInstance() {
        AnalyticsWorker analyticsWorker;
        synchronized (_instanceMutex) {
            if (_instance == null) {
                _instance = new AnalyticsWorker();
            }
            analyticsWorker = _instance;
        }
        return analyticsWorker;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void kickWithReferrerData(Map<String, Object> map) {
        String str;
        if (map == null || map.size() <= 0) {
            ReferrerHandler.setReferrerProcessed(true);
            kick(false);
            return;
        }
        AbstractHitDatabase.Hit selectOldestHit = selectOldestHit();
        if (selectOldestHit != null && (str = selectOldestHit.urlFragment) != null) {
            selectOldestHit.urlFragment = StaticMethods.appendContextData(map, str);
            updateHitInDatabase(selectOldestHit);
            ReferrerHandler.setReferrerProcessed(true);
        }
        kick(false);
    }

    @Override // com.adobe.mobile.AbstractDatabaseBacking
    protected void preMigrate() {
        File file = new File(StaticMethods.getCacheDirectory() + this.fileName);
        File file2 = new File(StaticMethods.getCacheDirectory(), this.fileName);
        if (!file.exists() || file2.exists()) {
            return;
        }
        try {
            if (file.renameTo(file2)) {
                return;
            }
            StaticMethods.logWarningFormat("Analytics - Unable to migrate old hits db, creating new hits db (move file returned false)", new Object[0]);
        } catch (Exception e) {
            StaticMethods.logWarningFormat("Analytics - Unable to migrate old hits db, creating new hits db (%s)", e.getLocalizedMessage());
        }
    }

    @Override // com.adobe.mobile.AbstractDatabaseBacking
    protected void prepareStatements() {
        try {
            this._preparedInsertStatement = this.database.compileStatement("INSERT INTO HITS (URL, TIMESTAMP) VALUES (?, ?)");
        } catch (SQLException e) {
            StaticMethods.logErrorFormat("Analytics - Unable to create database due to a sql error (%s)", e.getLocalizedMessage());
        } catch (NullPointerException e2) {
            StaticMethods.logErrorFormat("Analytics - Unable to create database due to an invalid path (%s)", e2.getLocalizedMessage());
        } catch (Exception e3) {
            StaticMethods.logErrorFormat("Analytics - Unable to create database due to an unexpected error (%s)", e3.getLocalizedMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void queue(String str, long j) {
        MobileConfig mobileConfig = MobileConfig.getInstance();
        if (mobileConfig == null) {
            StaticMethods.logErrorFormat("Analytics - Cannot send hit, MobileConfig is null (this really shouldn't happen)", new Object[0]);
            return;
        }
        if (MobileConfig.getInstance().mobileUsingAnalytics()) {
            if (mobileConfig.getPrivacyStatus() == MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_OUT) {
                StaticMethods.logDebugFormat("Analytics - Ignoring hit due to privacy status being opted out", new Object[0]);
                return;
            }
            if (this.databaseStatus == AbstractDatabaseBacking.DatabaseStatus.FATALERROR) {
                StaticMethods.logErrorFormat("Analytics - Ignoring hit due to database error", new Object[0]);
                return;
            }
            synchronized (this.dbMutex) {
                try {
                    try {
                        this._preparedInsertStatement.bindString(1, str);
                        this._preparedInsertStatement.bindLong(2, j);
                        this._preparedInsertStatement.execute();
                        StaticMethods.updateLastKnownTimestamp(Long.valueOf(j));
                        this.numberOfUnsentHits++;
                        this._preparedInsertStatement.clearBindings();
                    } catch (SQLException e) {
                        e = e;
                        StaticMethods.logErrorFormat("Analytics - Unable to insert url (%s)", str);
                        resetDatabase(e);
                        kick(false);
                    }
                } catch (Exception e2) {
                    e = e2;
                    StaticMethods.logErrorFormat("Analytics - Unknown error while inserting url (%s)", str);
                    resetDatabase(e);
                    kick(false);
                }
            }
            kick(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0087, code lost:
    
        if (r1 == null) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0074, code lost:
    
        r1.close();
        r1 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0072, code lost:
    
        if (r1 == null) goto L37;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v24 */
    /* JADX WARN: Type inference failed for: r1v25 */
    /* JADX WARN: Type inference failed for: r1v26 */
    /* JADX WARN: Type inference failed for: r1v27 */
    /* JADX WARN: Type inference failed for: r1v3, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r1v6 */
    @Override // com.adobe.mobile.AbstractHitDatabase
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.adobe.mobile.AbstractHitDatabase.Hit selectOldestHit() {
        /*
            r14 = this;
            java.lang.Object r0 = r14.dbMutex
            monitor-enter(r0)
            r1 = 0
            r2 = 0
            r3 = 1
            android.database.sqlite.SQLiteDatabase r4 = r14.database     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L63 android.database.SQLException -> L78
            java.lang.String r5 = "HITS"
            java.lang.String r6 = "ID"
            java.lang.String r7 = "URL"
            java.lang.String r8 = "TIMESTAMP"
            java.lang.String[] r6 = new java.lang.String[]{r6, r7, r8}     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L63 android.database.SQLException -> L78
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            java.lang.String r11 = "ID ASC"
            java.lang.String r12 = "1"
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9, r10, r11, r12)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L63 android.database.SQLException -> L78
            boolean r5 = r4.moveToFirst()     // Catch: java.lang.Throwable -> L51 java.lang.Exception -> L55 android.database.SQLException -> L5b
            if (r5 == 0) goto L4a
            com.adobe.mobile.AbstractHitDatabase$Hit r5 = new com.adobe.mobile.AbstractHitDatabase$Hit     // Catch: java.lang.Throwable -> L51 java.lang.Exception -> L55 android.database.SQLException -> L5b
            r5.<init>()     // Catch: java.lang.Throwable -> L51 java.lang.Exception -> L55 android.database.SQLException -> L5b
            java.lang.String r1 = r4.getString(r2)     // Catch: java.lang.Exception -> L40 android.database.SQLException -> L45 java.lang.Throwable -> L51
            r5.identifier = r1     // Catch: java.lang.Exception -> L40 android.database.SQLException -> L45 java.lang.Throwable -> L51
            java.lang.String r1 = r4.getString(r3)     // Catch: java.lang.Exception -> L40 android.database.SQLException -> L45 java.lang.Throwable -> L51
            r5.urlFragment = r1     // Catch: java.lang.Exception -> L40 android.database.SQLException -> L45 java.lang.Throwable -> L51
            r1 = 2
            long r6 = r4.getLong(r1)     // Catch: java.lang.Exception -> L40 android.database.SQLException -> L45 java.lang.Throwable -> L51
            r5.timestamp = r6     // Catch: java.lang.Exception -> L40 android.database.SQLException -> L45 java.lang.Throwable -> L51
            r1 = r5
            goto L4a
        L40:
            r1 = move-exception
            r13 = r4
            r4 = r1
            r1 = r13
            goto L65
        L45:
            r1 = move-exception
            r13 = r4
            r4 = r1
            r1 = r13
            goto L7a
        L4a:
            if (r4 == 0) goto L4f
            r4.close()     // Catch: java.lang.Throwable -> L92
        L4f:
            r5 = r1
            goto L8a
        L51:
            r1 = move-exception
            r2 = r1
            r1 = r4
            goto L8c
        L55:
            r5 = move-exception
            r13 = r5
            r5 = r1
            r1 = r4
            r4 = r13
            goto L65
        L5b:
            r5 = move-exception
            r13 = r5
            r5 = r1
            r1 = r4
            r4 = r13
            goto L7a
        L61:
            r2 = move-exception
            goto L8c
        L63:
            r4 = move-exception
            r5 = r1
        L65:
            java.lang.String r6 = "Analytics - Unknown error reading from database (%s)"
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L61
            java.lang.String r4 = r4.getMessage()     // Catch: java.lang.Throwable -> L61
            r3[r2] = r4     // Catch: java.lang.Throwable -> L61
            com.adobe.mobile.StaticMethods.logErrorFormat(r6, r3)     // Catch: java.lang.Throwable -> L61
            if (r1 == 0) goto L8a
        L74:
            r1.close()     // Catch: java.lang.Throwable -> L92
            goto L8a
        L78:
            r4 = move-exception
            r5 = r1
        L7a:
            java.lang.String r6 = "Analytics - Unable to read from database (%s)"
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L61
            java.lang.String r4 = r4.getMessage()     // Catch: java.lang.Throwable -> L61
            r3[r2] = r4     // Catch: java.lang.Throwable -> L61
            com.adobe.mobile.StaticMethods.logErrorFormat(r6, r3)     // Catch: java.lang.Throwable -> L61
            if (r1 == 0) goto L8a
            goto L74
        L8a:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L92
            return r5
        L8c:
            if (r1 == 0) goto L91
            r1.close()     // Catch: java.lang.Throwable -> L92
        L91:
            throw r2     // Catch: java.lang.Throwable -> L92
        L92:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L92
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.mobile.AnalyticsWorker.selectOldestHit():com.adobe.mobile.AbstractHitDatabase$Hit");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateHitInDatabase(AbstractHitDatabase.Hit hit) {
        synchronized (this.dbMutex) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("URL", hit.urlFragment);
                this.database.update("HITS", contentValues, "id=" + hit.identifier, null);
            } catch (SQLException e) {
                StaticMethods.logErrorFormat("Analytics - Unable to update url in database (%s)", e.getMessage());
            } catch (Exception e2) {
                StaticMethods.logErrorFormat("Analytics - Unknown error updating url in database (%s)", e2.getMessage());
            }
        }
    }

    @Override // com.adobe.mobile.AbstractHitDatabase
    protected final Runnable workerThread() {
        return new Runnable() { // from class: com.adobe.mobile.AnalyticsWorker.1
            @Override // java.lang.Runnable
            public void run() {
                AbstractHitDatabase.Hit selectOldestHit;
                String substring;
                AnalyticsWorker sharedInstance = AnalyticsWorker.sharedInstance();
                Process.setThreadPriority(10);
                HashMap hashMap = new HashMap();
                hashMap.put("Accept-Language", StaticMethods.getDefaultAcceptLanguage());
                hashMap.put("User-Agent", StaticMethods.getDefaultUserAgent());
                while (MobileConfig.getInstance().getPrivacyStatus() == MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN && ((!MobileConfig.getInstance().reachabilityChecksEnabled() || MobileConfig.getInstance().networkConnectivity()) && sharedInstance.databaseStatus == AbstractDatabaseBacking.DatabaseStatus.OK && (selectOldestHit = sharedInstance.selectOldestHit()) != null)) {
                    boolean offlineTrackingEnabled = MobileConfig.getInstance().getOfflineTrackingEnabled();
                    long j = selectOldestHit.timestamp;
                    if (offlineTrackingEnabled) {
                        long j2 = sharedInstance.lastHitTimestamp;
                        if (j - j2 < 0) {
                            long j3 = j2 + 1;
                            selectOldestHit.urlFragment = selectOldestHit.urlFragment.replaceFirst("&ts=" + Long.toString(selectOldestHit.timestamp), "&ts=" + Long.toString(j3));
                            StaticMethods.logDebugFormat("Analytics - Adjusting out of order hit timestamp(%d->%d)", Long.valueOf(selectOldestHit.timestamp), Long.valueOf(j3));
                            selectOldestHit.timestamp = j3;
                        }
                    } else if (j < StaticMethods.getTimeSince1970() - 60) {
                        sharedInstance.deleteHit(selectOldestHit.identifier);
                    }
                    if (selectOldestHit.urlFragment.startsWith("ndh")) {
                        substring = selectOldestHit.urlFragment;
                    } else {
                        String str = selectOldestHit.urlFragment;
                        substring = str.substring(str.indexOf(63) + 1);
                    }
                    byte[] retrieveAnalyticsRequestData = RequestHandler.retrieveAnalyticsRequestData(AnalyticsWorker.access$000() + AnalyticsWorker.randomGen.nextInt(100000000), substring, hashMap, 5000, AnalyticsWorker.this.logPrefix);
                    if (retrieveAnalyticsRequestData == null) {
                        for (int i = 0; i < 30; i++) {
                            try {
                                if (!MobileConfig.getInstance().reachabilityChecksEnabled() || MobileConfig.getInstance().networkConnectivity()) {
                                    Thread.sleep(1000L);
                                }
                            } catch (Exception e) {
                                StaticMethods.logWarningFormat("Analytics - Background Thread Interrupted(%s)", e.getMessage());
                            }
                        }
                    } else if (retrieveAnalyticsRequestData.length > 1) {
                        try {
                            try {
                                sharedInstance.deleteHit(selectOldestHit.identifier);
                                sharedInstance.lastHitTimestamp = selectOldestHit.timestamp;
                                final JSONObject jSONObject = new JSONObject(new String(retrieveAnalyticsRequestData, "UTF-8"));
                                StaticMethods.getAudienceExecutor().execute(new Runnable() { // from class: com.adobe.mobile.AnalyticsWorker.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        AudienceManagerWorker.processJsonResponse(jSONObject);
                                    }
                                });
                            } catch (AbstractDatabaseBacking.CorruptedDatabaseException e2) {
                                AnalyticsWorker.sharedInstance().resetDatabase(e2);
                            }
                        } catch (UnsupportedEncodingException e3) {
                            StaticMethods.logWarningFormat("Audience Manager - Unable to decode server response (%s)", e3.getLocalizedMessage());
                        } catch (JSONException e4) {
                            StaticMethods.logWarningFormat("Audience Manager - Unable to parse JSON data (%s)", e4.getLocalizedMessage());
                        }
                    } else {
                        sharedInstance.deleteHit(selectOldestHit.identifier);
                        sharedInstance.lastHitTimestamp = selectOldestHit.timestamp;
                    }
                }
                sharedInstance.bgThreadActive = false;
            }
        };
    }
}
