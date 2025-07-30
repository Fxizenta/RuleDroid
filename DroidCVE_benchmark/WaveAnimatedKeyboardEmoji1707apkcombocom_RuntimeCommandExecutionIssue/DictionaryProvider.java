package com.wave.keyboard.inputmethod.dictionarypack;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.ServerProtocol;
import com.singular.sdk.internal.Constants;
import com.wave.keyboard.R;
import com.wave.keyboard.inputmethod.dictionarypack.b;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import net.pubnative.library.request.PubnativeRequest;

/* loaded from: classes3.dex */
public final class DictionaryProvider extends ContentProvider {
    private static final String a = DictionaryProvider.class.getSimpleName();
    public static final Uri b = Uri.parse("content://com.wave.keyboard.inputmethod.dictionarypack.aosp");

    /* renamed from: c, reason: collision with root package name */
    private static final UriMatcher f16154c = new UriMatcher(0);

    /* renamed from: d, reason: collision with root package name */
    private static final UriMatcher f16155d = new UriMatcher(0);

    /* loaded from: classes3.dex */
    private static final class a extends AbstractCursor {
        private static final String[] b = {"id", PubnativeRequest.Parameters.LOCALE};
        final b[] a;

        public a(Collection<b> collection) {
            this.a = (b[]) collection.toArray(new b[0]);
            ((AbstractCursor) this).mPos = 0;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public String[] getColumnNames() {
            return b;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public int getCount() {
            return this.a.length;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public double getDouble(int i2) {
            return 0.0d;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public float getFloat(int i2) {
            return 0.0f;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public int getInt(int i2) {
            return 0;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public long getLong(int i2) {
            return 0L;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public short getShort(int i2) {
            return (short) 0;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public String getString(int i2) {
            if (i2 == 0) {
                return this.a[((AbstractCursor) this).mPos].a;
            }
            if (i2 != 1) {
                return null;
            }
            return this.a[((AbstractCursor) this).mPos].b;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public boolean isNull(int i2) {
            return ((AbstractCursor) this).mPos >= this.a.length || i2 != 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class b {
        public final String a;
        public final String b;

        /* renamed from: c, reason: collision with root package name */
        public final int f16156c;

        public b(String str, String str2, int i2) {
            this.a = str;
            this.b = str2;
            this.f16156c = i2;
        }
    }

    static {
        f16154c.addURI("com.wave.keyboard.inputmethod.dictionarypack.aosp", "list", 1);
        f16154c.addURI("com.wave.keyboard.inputmethod.dictionarypack.aosp", "*", 2);
        f16155d.addURI("com.wave.keyboard.inputmethod.dictionarypack.aosp", "*/metadata", 3);
        f16155d.addURI("com.wave.keyboard.inputmethod.dictionarypack.aosp", "*/list", 4);
        f16155d.addURI("com.wave.keyboard.inputmethod.dictionarypack.aosp", "*/dict/*", 5);
        f16155d.addURI("com.wave.keyboard.inputmethod.dictionarypack.aosp", "*/datafile/*", 6);
    }

    private int a(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        String b2 = b(uri);
        ContentValues d2 = d(b2, lastPathSegment);
        if (d2 == null) {
            return 0;
        }
        int intValue = d2.getAsInteger(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS).intValue();
        int intValue2 = d2.getAsInteger(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION).intValue();
        if (5 == intValue) {
            q.r(getContext(), b2, lastPathSegment, intValue2, intValue);
            return 1;
        }
        if (3 == intValue) {
            if ("failure".equals(uri.getQueryParameter("result"))) {
                q.q(getContext(), b2, lastPathSegment, intValue2);
            }
            return getContext().getFileStreamPath(d2.getAsString("filename")).delete() ? 1 : 0;
        }
        String str = "Attempt to delete a file whose status is " + intValue;
        return 0;
    }

    private static String b(Uri uri) {
        if (("2".equals(uri.getQueryParameter("protocol")) ? (char) 2 : (char) 1) != 2) {
            return null;
        }
        return uri.getPathSegments().get(0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x00ac, code lost:
    
        if (r2.moveToNext() != false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0043, code lost:
    
        r9 = android.text.TextUtils.split(r8, ":");
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x004b, code lost:
    
        if (2 != r9.length) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x004d, code lost:
    
        r9 = r9[0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0053, code lost:
    
        r10 = r2.getString(r5);
        r11 = r2.getString(r6);
        r12 = r2.getInt(r7);
        r14 = com.wave.keyboard.inputmethod.dictionarypack.h.b(r10, r18);
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0069, code lost:
    
        if (com.wave.keyboard.inputmethod.dictionarypack.h.d(r14) != false) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x006d, code lost:
    
        if (3 != r12) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x006f, code lost:
    
        r11 = getContext().getFileStreamPath(r11);
        com.wave.keyboard.inputmethod.dictionarypack.c.e(com.wave.keyboard.inputmethod.dictionarypack.c.i(getContext()), r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0086, code lost:
    
        if (r11.isFile() != false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0092, code lost:
    
        r12 = (com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider.b) r3.get(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x009a, code lost:
    
        if (r12 == null) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x009e, code lost:
    
        if (r12.f16156c >= r14) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00a0, code lost:
    
        r3.put(r9, new com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider.b(r8, r10, r14));
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x008a, code lost:
    
        if (1 != r12) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x008c, code lost:
    
        com.wave.keyboard.inputmethod.dictionarypack.q.o(r1, r17, r8, r19);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0051, code lost:
    
        r9 = "main";
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00ae, code lost:
    
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b9, code lost:
    
        return java.util.Collections.unmodifiableCollection(r3.values());
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0032, code lost:
    
        if (r2.moveToFirst() != false) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0034, code lost:
    
        r8 = r2.getString(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x003c, code lost:
    
        if (android.text.TextUtils.isEmpty(r8) == false) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.Collection<com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider.b> c(java.lang.String r17, java.lang.String r18, boolean r19) {
        /*
            r16 = this;
            r0 = r17
            android.content.Context r1 = r16.getContext()
            android.database.Cursor r2 = com.wave.keyboard.inputmethod.dictionarypack.k.p0(r1, r0)
            if (r2 != 0) goto L11
            java.util.List r0 = java.util.Collections.emptyList()
            return r0
        L11:
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            java.lang.String r4 = "id"
            int r4 = r2.getColumnIndex(r4)
            java.lang.String r5 = "locale"
            int r5 = r2.getColumnIndex(r5)
            java.lang.String r6 = "filename"
            int r6 = r2.getColumnIndex(r6)
            java.lang.String r7 = "status"
            int r7 = r2.getColumnIndex(r7)
            boolean r8 = r2.moveToFirst()
            if (r8 == 0) goto Lae
        L34:
            java.lang.String r8 = r2.getString(r4)
            boolean r9 = android.text.TextUtils.isEmpty(r8)
            if (r9 == 0) goto L43
            r13 = r18
        L40:
            r11 = r19
            goto La8
        L43:
            java.lang.String r9 = ":"
            java.lang.String[] r9 = android.text.TextUtils.split(r8, r9)
            r10 = 2
            int r11 = r9.length
            if (r10 != r11) goto L51
            r10 = 0
            r9 = r9[r10]
            goto L53
        L51:
            java.lang.String r9 = "main"
        L53:
            java.lang.String r10 = r2.getString(r5)
            java.lang.String r11 = r2.getString(r6)
            int r12 = r2.getInt(r7)
            r13 = r18
            int r14 = com.wave.keyboard.inputmethod.dictionarypack.h.b(r10, r13)
            boolean r15 = com.wave.keyboard.inputmethod.dictionarypack.h.d(r14)
            if (r15 != 0) goto L6c
        L6b:
            goto L40
        L6c:
            r15 = 3
            if (r15 != r12) goto L89
            android.content.Context r12 = r16.getContext()
            java.io.File r11 = r12.getFileStreamPath(r11)
            android.content.Context r12 = r16.getContext()
            android.content.SharedPreferences r12 = com.wave.keyboard.inputmethod.dictionarypack.c.i(r12)
            com.wave.keyboard.inputmethod.dictionarypack.c.e(r12, r10)
            boolean r11 = r11.isFile()
            if (r11 != 0) goto L92
            goto L6b
        L89:
            r11 = 1
            if (r11 != r12) goto L92
            r11 = r19
            com.wave.keyboard.inputmethod.dictionarypack.q.o(r1, r0, r8, r11)
            goto La8
        L92:
            r11 = r19
            java.lang.Object r12 = r3.get(r9)
            com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider$b r12 = (com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider.b) r12
            if (r12 == 0) goto La0
            int r12 = r12.f16156c
            if (r12 >= r14) goto La8
        La0:
            com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider$b r12 = new com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider$b
            r12.<init>(r8, r10, r14)
            r3.put(r9, r12)
        La8:
            boolean r8 = r2.moveToNext()
            if (r8 != 0) goto L34
        Lae:
            r2.close()
            java.util.Collection r0 = r3.values()
            java.util.Collection r0 = java.util.Collections.unmodifiableCollection(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.wave.keyboard.inputmethod.dictionarypack.DictionaryProvider.c(java.lang.String, java.lang.String, boolean):java.util.Collection");
    }

    private ContentValues d(String str, String str2) {
        Context context = getContext();
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        return k.F(k.y(context, str), str2);
    }

    private static int e(Uri uri) {
        char c2 = "2".equals(uri.getQueryParameter("protocol")) ? (char) 2 : (char) 1;
        if (c2 == 1) {
            return f16154c.match(uri);
        }
        if (c2 != 2) {
            return 0;
        }
        return f16155d.match(uri);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) throws UnsupportedOperationException {
        int e2 = e(uri);
        if (2 == e2 || 6 == e2) {
            return a(uri);
        }
        return (3 == e2 && k.f(getContext(), b(uri))) ? 1 : 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        o.a("Asked for type of : " + uri);
        int e2 = e(uri);
        if (e2 == 1 || e2 == 2 || e2 == 4 || e2 == 5) {
            return "vnd.android.cursor.item/vnd.google.dictionarylist";
        }
        if (e2 != 6) {
            return null;
        }
        return "vnd.android.cursor.item/vnd.google.dictionary";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) throws UnsupportedOperationException {
        if (uri == null || contentValues == null) {
            return null;
        }
        o.a("Insert, uri = " + uri.toString());
        String b2 = b(uri);
        int e2 = e(uri);
        if (e2 == 1 || e2 == 2) {
            o.a("Attempt to insert : " + uri);
            throw new UnsupportedOperationException("Insertion in the dictionary is not supported in this version");
        }
        if (e2 == 3) {
            k.s0(getContext(), b2, contentValues);
        } else if (e2 == 5) {
            try {
                k.c(contentValues);
                new b.h(b2, r.a(contentValues)).a(getContext());
            } catch (BadFormatException unused) {
                String str = "Not enough information to insert this dictionary " + contentValues;
            }
            q.w(getContext(), true);
        }
        return uri;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openAssetFile(Uri uri, String str) {
        if (str != null && Constants.REVENUE_AMOUNT_KEY.equals(str)) {
            int e2 = e(uri);
            if (2 != e2 && 6 != e2) {
                String str2 = "Unsupported URI for openAssetFile : " + uri;
                return null;
            }
            ContentValues d2 = d(b(uri), uri.getLastPathSegment());
            if (d2 == null) {
                return null;
            }
            try {
                if (5 == d2.getAsInteger(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS).intValue()) {
                    return getContext().getResources().openRawResourceFd(R.raw.empty);
                }
                ParcelFileDescriptor open = ParcelFileDescriptor.open(getContext().getFileStreamPath(d2.getAsString("filename")), 268435456);
                return new AssetFileDescriptor(open, 0L, open.getStatSize());
            } catch (FileNotFoundException unused) {
            }
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        com.wave.keyboard.inputmethod.latin.utils.j.c("Uri =", uri);
        o.a("Query : " + uri);
        String b2 = b(uri);
        int e2 = e(uri);
        if (e2 != 1) {
            if (e2 != 2) {
                if (e2 != 4) {
                    if (e2 != 5 || !k.b0(getContext(), b2)) {
                        return null;
                    }
                }
            }
            Collection<b> c2 = c(b2, uri.getLastPathSegment(), ServerProtocol.DIALOG_RETURN_SCOPES_TRUE.equals(uri.getQueryParameter("mayPrompt")));
            DictionaryService.g(getContext());
            if (c2 != null && c2.size() > 0) {
                o.a("Returned " + c2.size() + " files");
                return new a(c2);
            }
            o.a("No dictionary files for this URL");
            return new a(Collections.emptyList());
        }
        Cursor o0 = k.o0(getContext(), b2);
        com.wave.keyboard.inputmethod.latin.utils.j.c("List of dictionaries with count", Integer.valueOf(o0.getCount()));
        o.a("Returned a list of " + o0.getCount() + " items");
        return o0;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) throws UnsupportedOperationException {
        o.a("Attempt to update : " + uri);
        throw new UnsupportedOperationException("Updating dictionary words is not supported");
    }
}
