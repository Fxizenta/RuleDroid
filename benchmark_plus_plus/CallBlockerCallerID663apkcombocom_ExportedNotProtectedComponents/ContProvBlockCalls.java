package com.cuiet.blockCalls.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.cuiet.blockCalls.service.ServiceHandleEvents;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.api.client.http.HttpStatusCodes;
import z2.b;

/* loaded from: classes.dex */
public class ContProvBlockCalls extends ContentProvider {

    /* renamed from: b, reason: collision with root package name */
    private static final UriMatcher f13145b;

    /* renamed from: a, reason: collision with root package name */
    private b f13146a;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        f13145b = uriMatcher;
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbBlackList", 200);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/lookup/is.blocked.lookup", 205);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbBlackList/#", 210);
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append("tbWhiteList");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb.toString(), HttpStatusCodes.STATUS_CODE_MULTIPLE_CHOICES);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbWhiteList/#", 310);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("/");
        sb2.append("tbOutgoingCallsBlackList");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb2.toString(), HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbOutgoingCallsBlackList/#", 410);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("/");
        sb3.append("tbOutgoingCallsWhiteList");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb3.toString(), 500);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbOutgoingCallsWhiteList/#", 510);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("/");
        sb4.append("tbChiamateBloccate");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb4.toString(), 900);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbChiamateBloccate/#", 910);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("/");
        sb5.append("tbGruppi");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb5.toString(), 1000);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbGruppi/#", AnalyticsListener.EVENT_AUDIO_POSITION_ADVANCING);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("/");
        sb6.append("tbIncomingCall");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb6.toString(), 2000);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tbIncomingCall/#", 2010);
        StringBuilder sb7 = new StringBuilder();
        sb7.append("/");
        sb7.append("top_spammers");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb7.toString(), 2058);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/top_spammers/#", 2068);
        StringBuilder sb8 = new StringBuilder();
        sb8.append("/");
        sb8.append("tb_contact_helper");
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", sb8.toString(), 8058);
        uriMatcher.addURI("com.cuiet.blockCalls.ContProvBlockCalls", "/tb_contact_helper/#", 8068);
    }

    private void a(String str) {
        if (str.equals("tbWhiteList")) {
            ServiceHandleEvents.m(getContext());
        } else if (str.equals("tbBlackList")) {
            ServiceHandleEvents.l(getContext());
        }
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        String str;
        int match = f13145b.match(uri);
        if (match != 200) {
            if (match != 300) {
                if (match != 400) {
                    if (match != 500) {
                        if (match != 900) {
                            if (match != 1000) {
                                if (match != 2000) {
                                    if (match != 2058) {
                                        if (match == 8058) {
                                            str = "tb_contact_helper";
                                        } else {
                                            throw new IllegalArgumentException("Invalid Uri!");
                                        }
                                    } else {
                                        str = "top_spammers";
                                    }
                                } else {
                                    str = "tbIncomingCall";
                                }
                            } else {
                                str = "tbGruppi";
                            }
                        } else {
                            str = "tbChiamateBloccate";
                        }
                    } else {
                        str = "tbOutgoingCallsWhiteList";
                    }
                } else {
                    str = "tbOutgoingCallsBlackList";
                }
            } else {
                str = "tbWhiteList";
            }
        } else {
            str = "tbBlackList";
        }
        SQLiteDatabase writableDatabase = this.f13146a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            for (ContentValues contentValues : contentValuesArr) {
                if (writableDatabase.insert(str, " ", contentValues) <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            writableDatabase.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            a(str);
            return contentValuesArr.length;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x001e. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:10:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x00bf A[Catch: SQLiteException -> 0x00cf, TRY_LEAVE, TryCatch #0 {SQLiteException -> 0x00cf, blocks: (B:8:0x0072, B:11:0x00a8, B:14:0x00b9, B:16:0x00bf, B:20:0x0090), top: B:7:0x0072 }] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0090 A[Catch: SQLiteException -> 0x00cf, TryCatch #0 {SQLiteException -> 0x00cf, blocks: (B:8:0x0072, B:11:0x00a8, B:14:0x00b9, B:16:0x00bf, B:20:0x0090), top: B:7:0x0072 }] */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int delete(android.net.Uri r17, java.lang.String r18, java.lang.String[] r19) {
        /*
            Method dump skipped, instructions count: 284
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cuiet.blockCalls.contentProvider.ContProvBlockCalls.delete(android.net.Uri, java.lang.String, java.lang.String[]):int");
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (f13145b.match(uri)) {
            case 200:
                return "vnd.android.cursor.dir/blacklist";
            case 210:
                return "vnd.android.cursor.item/blacklist_item";
            case HttpStatusCodes.STATUS_CODE_MULTIPLE_CHOICES /* 300 */:
                return "vnd.android.cursor.dir/whitelist";
            case 310:
                return "vnd.android.cursor.item/whitelist_item";
            case HttpStatusCodes.STATUS_CODE_BAD_REQUEST /* 400 */:
                return "vnd.android.cursor.dir/outgoing.blacklist";
            case 410:
                return "vnd.android.cursor.item/outgoing.blacklist_item";
            case 500:
                return "vnd.android.cursor.dir/outgoing.whitelist";
            case 510:
                return "vnd.android.cursor.item/outgoing.whitelist_item";
            case 900:
                return "vnd.android.cursor.dir/chiamate";
            case 910:
                return "vnd.android.cursor.item/chiamate_item";
            case 1000:
                return "vnd.android.cursor.dir/gruppi";
            case AnalyticsListener.EVENT_AUDIO_POSITION_ADVANCING /* 1010 */:
                return "vnd.android.cursor.item/gruppi_item";
            case 2000:
                return "vnd.android.cursor.dir/incoming_call";
            case 2010:
                return "vnd.android.cursor.item/incoming_call_item";
            case 2058:
                return "vnd.android.cursor.dir/top_spammers_blacklist";
            case 2068:
                return "vnd.android.cursor.item/top_spammers_blacklist_item";
            case 8058:
                return "vnd.android.cursor.dir/contact_helper";
            case 8068:
                return "vnd.android.cursor.item/contact_helper_item";
            default:
                throw new IllegalArgumentException("Invalid Uri!");
        }
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        String str;
        Uri uri2;
        int match = f13145b.match(uri);
        if (match != 200) {
            if (match != 300) {
                if (match != 400) {
                    if (match != 500) {
                        if (match != 900) {
                            if (match != 1000) {
                                if (match != 2000) {
                                    if (match != 2058) {
                                        if (match == 8058) {
                                            str = "tb_contact_helper";
                                        } else {
                                            throw new IllegalArgumentException("Invalid Uri!");
                                        }
                                    } else {
                                        str = "top_spammers";
                                    }
                                } else {
                                    str = "tbIncomingCall";
                                }
                            } else {
                                str = "tbGruppi";
                            }
                        } else {
                            str = "tbChiamateBloccate";
                        }
                    } else {
                        str = "tbOutgoingCallsWhiteList";
                    }
                } else {
                    str = "tbOutgoingCallsBlackList";
                }
            } else {
                str = "tbWhiteList";
            }
        } else {
            str = "tbBlackList";
        }
        long j10 = -1;
        try {
            long insertOrThrow = this.f13146a.getWritableDatabase().insertOrThrow(str, null, contentValues);
            uri2 = ContentUris.withAppendedId(uri, insertOrThrow);
            j10 = insertOrThrow;
        } catch (SQLiteConstraintException | Exception unused) {
            uri2 = null;
        }
        if (j10 <= 0) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri2, null);
        a(str);
        return uri2;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.f13146a = new b(getContext());
        return true;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0020. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:11:0x013e  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00c4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.database.Cursor query(android.net.Uri r25, java.lang.String[] r26, java.lang.String r27, java.lang.String[] r28, java.lang.String r29) {
        /*
            Method dump skipped, instructions count: 530
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cuiet.blockCalls.contentProvider.ContProvBlockCalls.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0020. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0096  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x00be  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x00cd A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x009b A[Catch: SQLiteException -> 0x00de, TryCatch #0 {SQLiteException -> 0x00de, blocks: (B:11:0x0083, B:14:0x00b3, B:17:0x00bf, B:20:0x00cf, B:23:0x009b), top: B:10:0x0083 }] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x007d  */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int update(android.net.Uri r18, android.content.ContentValues r19, java.lang.String r20, java.lang.String[] r21) {
        /*
            Method dump skipped, instructions count: 298
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.cuiet.blockCalls.contentProvider.ContProvBlockCalls.update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]):int");
    }
}
