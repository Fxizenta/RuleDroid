package com.oki_access.android.ims.call.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class i {
    public static String A;
    public static String B;
    public static String C;
    public static String D;
    public static String E;
    public static String F;
    public static String G;
    public static String H;
    public static String I;
    public static String J;
    public static String K;
    public static String L;
    public static Uri M;
    public static Uri N;
    public static Uri O;
    public static Uri P;
    public static Uri Q;
    public static Uri R;
    public static Uri S;
    public static final String[] T;
    public static final String[] U;
    public static final String[] V;
    public static final String[] W;
    public static final String[] X;
    public static final String[] Y;
    public static final String[] Z;

    /* renamed from: a, reason: collision with root package name */
    public static Uri f1102a = ContactsContract.Contacts.CONTENT_URI;
    public static final String[] aa;
    private static final String[] ab;
    private static final String[] ac;
    private static final String[] ad;
    private static final String[] ae;
    private static Context af;
    private static final char[] ah;
    private static final String[] ai;
    private static final char[] aj;
    private static final char[] ak;
    public static boolean b;
    public static String c;
    public static String d;
    public static String e;
    public static String f;
    public static String g;
    public static String h;
    public static String i;
    public static String j;
    public static String k;
    public static String l;
    public static String m;
    public static String n;
    public static String o;
    public static String p;
    public static String q;
    public static String r;
    public static String s;
    public static String t;
    public static String u;
    public static String v;
    public static String w;
    public static String x;
    public static String y;
    public static String z;
    private StringBuffer ag;

    static {
        b = true;
        c = null;
        d = null;
        e = null;
        f = null;
        g = null;
        h = null;
        i = null;
        j = null;
        k = null;
        l = null;
        m = null;
        n = null;
        o = null;
        p = null;
        q = null;
        r = null;
        s = null;
        t = null;
        u = null;
        v = null;
        w = null;
        x = null;
        y = null;
        z = null;
        A = null;
        B = null;
        C = null;
        D = null;
        E = null;
        F = null;
        G = null;
        H = null;
        I = null;
        J = null;
        K = null;
        L = null;
        M = null;
        N = null;
        O = null;
        P = null;
        Q = null;
        R = null;
        S = null;
        if (b()) {
            b = true;
            c = "_id";
            d = "phone_id";
            e = "display_name";
            f = "photo_id";
            g = "contact_id";
            h = "data1";
            i = "data1";
            j = "data1";
            k = "data1";
            l = "data2";
            m = "data2";
            n = "data3";
            o = "data3";
            p = "data4";
            q = "data4";
            r = "data5";
            s = "data5";
            t = "data7";
            u = "data7";
            v = "data8";
            w = "data9";
            x = "data9";
            y = "data15";
            z = "_id";
            B = "starred";
            C = "vnd.android.cursor.item/contact";
            D = "phone";
            G = "android.provider.Contacts.SEARCH_SUGGESTION_CLICKED";
            H = "android.provider.Contacts.SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED";
            I = "android.provider.Contacts.SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED";
            J = "photo_id";
            K = "custom_ringtone";
            L = "times_contacted";
            if (c()) {
                E = "com.android.contacts.action.FILTER_CONTACTS";
                F = "phonetic_name";
                R = Uri.parse("content://com.android.contacts/contacts/lookup");
                A = "sort_key";
            } else {
                E = "android.intent.action.VIEW";
                F = "display_name";
                R = Uri.parse("content://com.android.contacts/contacts");
                A = "display_name COLLATE LOCALIZED ASC";
            }
            M = Uri.parse("content://com.android.contacts/contacts");
            N = Uri.parse("content://com.android.contacts/data");
            O = Uri.parse("content://com.android.contacts/data");
            P = Uri.parse("content://com.android.contacts/data/phones");
            Q = Uri.parse("content://com.android.contacts/phone_lookup");
            S = Uri.parse("content://com.android.contacts/contacts/strequent");
        } else {
            b = false;
            c = "_id";
            d = "phone_id";
            e = "display_name";
            f = "person";
            g = "person";
            h = "number";
            i = "company";
            j = "address";
            k = "formatted_address";
            l = "type";
            m = "given_name";
            n = "label";
            o = "family_name";
            p = "title";
            q = "street";
            r = "department";
            s = "pobox";
            t = "city";
            u = "phonetic_given_name";
            v = "region";
            w = "postcode";
            x = "phonetic_family_name";
            y = "data";
            z = "person";
            A = "display_name COLLATE LOCALIZED ASC";
            B = "starred";
            C = "vnd.android.cursor.item/person";
            D = "phone";
            E = "android.intent.action.VIEW";
            F = "phonetic_name";
            G = "android.provider.Contacts.SEARCH_SUGGESTION_CLICKED";
            H = "android.provider.Contacts.SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED";
            I = "android.provider.Contacts.SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED";
            J = "_id";
            K = "custom_ringtone";
            L = "times_contacted";
            M = Uri.parse("content://contacts/people");
            N = Uri.parse("content://contacts/photos");
            O = Uri.parse("content://contacts/phones");
            P = Uri.parse("content://contacts/phones");
            Q = Uri.parse("content://contacts/phones/filter");
            R = Uri.parse("content://contacts/people");
            S = Uri.parse("content://contacts/people/strequent");
        }
        ab = new String[]{g, h, l, n, f, e, B, F};
        T = new String[]{c, e, F, B, J};
        ac = new String[]{g, h, l, n};
        ad = new String[]{c, g, e, h, l, n, f};
        ae = new String[]{c, l, n, p};
        U = new String[]{"_id", "name", "number", "typestring", "photoid"};
        V = new String[]{"_id", "name", "typestring", "photoid", "starred"};
        W = new String[]{"_id", "number", "type", "label"};
        X = new String[]{"_id", "display_name", "label", "photoId", "isChat"};
        Y = new String[]{"data1", "display_name", "label", "photoId", "isFreecall", "isChat"};
        Z = new String[]{"_id", "display_name", "label", "photoId", "isFreecall", "isChat"};
        aa = new String[]{"type", "number", "date", "duration", "phone_id"};
        ah = new char[]{12449, 12450, 12451, 12452, 12453, 12454, 12455, 12456, 12457, 12458, 12459, 12460, 12461, 12462, 12463, 12464, 12465, 12466, 12467, 12468, 12469, 12470, 12471, 12472, 12473, 12474, 12475, 12476, 12477, 12478, 12479, 12480, 12481, 12482, 12483, 12484, 12485, 12486, 12487, 12488, 12489, 12490, 12491, 12492, 12493, 12494, 12495, 12496, 12497, 12498, 12499, 12500, 12501, 12502, 12503, 12504, 12505, 12506, 12507, 12508, 12509, 12510, 12511, 12512, 12513, 12514, 12515, 12516, 12517, 12518, 12519, 12520, 12521, 12522, 12523, 12524, 12525, 12526, 12527, 12528, 12529, 12530, 12531, 12532, 12533, 12534};
        ai = new String[]{"ｧ", "ｱ", "ｨ", "ｲ", "ｩ", "ｳ", "ｪ", "ｴ", "ｫ", "ｵ", "ｶ", "ｶﾞ", "ｷ", "ｷﾞ", "ｸ", "ｸﾞ", "ｹ", "ｹﾞ", "ｺ", "ｺﾞ", "ｻ", "ｻﾞ", "ｼ", "ｼﾞ", "ｽ", "ｽﾞ", "ｾ", "ｾﾞ", "ｿ", "ｿﾞ", "ﾀ", "ﾀﾞ", "ﾁ", "ﾁﾞ", "ｯ", "ﾂ", "ﾂﾞ", "ﾃ", "ﾃﾞ", "ﾄ", "ﾄﾞ", "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ", "ﾊ", "ﾊﾞ", "ﾊﾟ", "ﾋ", "ﾋﾞ", "ﾋﾟ", "ﾌ", "ﾌﾞ", "ﾌﾟ", "ﾍ", "ﾍﾞ", "ﾍﾟ", "ﾎ", "ﾎﾞ", "ﾎﾟ", "ﾏ", "ﾐ", "ﾑ", "ﾒ", "ﾓ", "ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", "ﾜ", "ﾜ", "ｲ", "ｴ", "ｦ", "ﾝ", "ｳﾞ", "ｶ", "ｹ"};
        aj = new char[]{12530, 12449, 12451, 12453, 12455, 12457, 12515, 12517, 12519, 12483, 12540, 12450, 12452, 12454, 12456, 12458, 12459, 12461, 12463, 12465, 12467, 12469, 12471, 12473, 12475, 12477, 12479, 12481, 12484, 12486, 12488, 12490, 12491, 12492, 12493, 12494, 12495, 12498, 12501, 12504, 12507, 12510, 12511, 12512, 12513, 12514, 12516, 12518, 12520, 12521, 12522, 12523, 12524, 12525, 12527, 12531, 12443, 12444};
        ak = new char[]{65382, 65383, 65384, 65385, 65386, 65387, 65388, 65389, 65390, 65391, 65392, 65393, 65394, 65395, 65396, 65397, 65398, 65399, 65400, 65401, 65402, 65403, 65404, 65405, 65406, 65407, 65408, 65409, 65410, 65411, 65412, 65413, 65414, 65415, 65416, 65417, 65418, 65419, 65420, 65421, 65422, 65423, 65424, 65425, 65426, 65427, 65428, 65429, 65430, 65431, 65432, 65433, 65434, 65435, 65436, 65437, 65438, 65439};
    }

    public i(Context context) {
        af = context;
    }

    public static Cursor a(long j2) {
        return af.getContentResolver().query(O, null, "mimetype='vnd.android.cursor.item/organization' AND " + g + "=?", new String[]{String.valueOf(j2)}, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x0137, code lost:
    
        if (r7.moveToFirst() != false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0139, code lost:
    
        r1 = new com.oki_access.android.ims.call.util.k();
        r1.f1104a = r7.getLong(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x014a, code lost:
    
        if (r7.isNull(2) == false) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x014c, code lost:
    
        r1.b = r7.getString(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0153, code lost:
    
        r2.add(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x015a, code lost:
    
        if (r7.moveToNext() != false) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x01be, code lost:
    
        r1.b = r7.getString(2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x015c, code lost:
    
        java.util.Collections.sort(r2, new com.oki_access.android.ims.call.util.j(r0));
        r0 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0166, code lost:
    
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x016a, code lost:
    
        if (r1 >= r2.size()) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x016c, code lost:
    
        r0 = (com.oki_access.android.ims.call.util.k) r2.get(r1);
        r7.moveToFirst();
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x017e, code lost:
    
        if (r7.getLong(0) != r0.f1104a) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x01ca, code lost:
    
        if (r7.moveToNext() != false) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x01ba, code lost:
    
        r0 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0180, code lost:
    
        r6.addRow(new java.lang.Object[]{java.lang.Long.valueOf(r7.getLong(0)), r7.getString(1), r7.getString(2), java.lang.Integer.valueOf(r7.getInt(3)), java.lang.Long.valueOf(r7.getLong(4))});
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01cd, code lost:
    
        r7.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:?, code lost:
    
        return r6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.database.Cursor a(android.database.Cursor r13) {
        /*
            Method dump skipped, instructions count: 467
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oki_access.android.ims.call.util.i.a(android.database.Cursor):android.database.Cursor");
    }

    private static Cursor a(Cursor cursor, String str) {
        MatrixCursor matrixCursor = new MatrixCursor(T);
        Cursor query = af.getContentResolver().query(O, null, "mimetype='vnd.android.cursor.item/name' AND (data1 LIKE '" + str + "%' OR data9 LIKE '" + str + "%')", null, g + " ASC");
        Iterator<n> it = new l(cursor, new String[]{c}, query, new String[]{g}).iterator();
        while (it.hasNext()) {
            try {
                if (it.next() == n.BOTH) {
                    matrixCursor.addRow(new Object[]{Long.valueOf(cursor.getLong(cursor.getColumnIndex(c))), cursor.getString(cursor.getColumnIndex(e)), cursor.getString(cursor.getColumnIndex(F)), Integer.valueOf(cursor.getInt(cursor.getColumnIndex(B))), Long.valueOf(cursor.getLong(cursor.getColumnIndex(J)))});
                }
            } catch (Exception e2) {
            }
        }
        query.deactivate();
        query.close();
        cursor.deactivate();
        cursor.close();
        return matrixCursor;
    }

    public static Cursor a(Uri uri) {
        return af.getContentResolver().query(uri, null, null, null, null);
    }

    public static Cursor a(String str) {
        String str2;
        Object[] objArr;
        String[] strArr;
        String str3;
        int i2 = 0;
        Uri uri = M;
        if (str == null) {
            objArr = null;
        } else {
            String str4 = "display_name LIKE ? OR display_name LIKE ? OR " + F + " LIKE ? OR " + F + " LIKE ?";
            ArrayList arrayList = new ArrayList();
            arrayList.add(str + "%");
            arrayList.add("% " + str + "%");
            arrayList.add(str + "%");
            arrayList.add("% " + str + "%");
            StringBuffer stringBuffer = new StringBuffer();
            if (str != null && str.length() != 0) {
                if (Pattern.matches("[ぁ-ん]", str.substring(0, 1))) {
                    stringBuffer.delete(0, stringBuffer.length());
                    stringBuffer.append(str);
                    for (int i3 = 0; i3 < str.length(); i3++) {
                        char charAt = stringBuffer.charAt(i3);
                        if (Pattern.matches("[ぁ-ん]", stringBuffer.subSequence(i3, i3 + 1))) {
                            stringBuffer.setCharAt(i3, (char) ((charAt - 12353) + 12449));
                        }
                    }
                    String str5 = str4 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                    String str6 = null;
                    for (int i4 = 0; i4 < stringBuffer.length(); i4++) {
                        char charAt2 = stringBuffer.charAt(i4);
                        str6 = (charAt2 < ah[0] || charAt2 > ah[ah.length + (-1)]) ? str6 != null ? str6 + charAt2 : String.valueOf(charAt2) : str6 != null ? str6 + ai[charAt2 - ah[0]] : ai[charAt2 - ah[0]];
                    }
                    String str7 = str5 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(str6 + "%");
                    arrayList.add("% " + str6 + "%");
                    arrayList.add(str6 + "%");
                    arrayList.add("% " + str6 + "%");
                    str2 = str7;
                } else if (Pattern.matches("[ァ-ン]", str.substring(0, 1)) || str.substring(0, 1).equals("ヵ") || str.substring(0, 1).equals("ヶ") || str.substring(0, 1).equals("ヴ")) {
                    stringBuffer.delete(0, stringBuffer.length());
                    stringBuffer.append(str);
                    String str8 = null;
                    for (int i5 = 0; i5 < stringBuffer.length(); i5++) {
                        char charAt3 = stringBuffer.charAt(i5);
                        str8 = (charAt3 < ah[0] || charAt3 > ah[ah.length + (-1)]) ? str8 != null ? str8 + charAt3 : String.valueOf(charAt3) : str8 != null ? str8 + ai[charAt3 - ah[0]] : ai[charAt3 - ah[0]];
                    }
                    String str9 = str4 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(str8 + "%");
                    arrayList.add("% " + str8 + "%");
                    arrayList.add(str8 + "%");
                    arrayList.add("% " + str8 + "%");
                    int i6 = 0;
                    while (i6 < stringBuffer.length()) {
                        char charAt4 = stringBuffer.charAt(i6);
                        if (Pattern.matches("[ァ-ン]", stringBuffer.substring(i6, i6 + 1))) {
                            stringBuffer.setCharAt(i6, (char) ((charAt4 - 12449) + 12353));
                        } else if (charAt4 == 12533) {
                            stringBuffer.setCharAt(i6, (char) 12363);
                        } else if (charAt4 == 12534) {
                            stringBuffer.setCharAt(i6, (char) 12369);
                        } else if (charAt4 == 12532) {
                            stringBuffer.setCharAt(i6, (char) 12358);
                            stringBuffer.insert(i6 + 1, (char) 12443);
                            i6++;
                        }
                        i6++;
                    }
                    str2 = str9 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                } else if (Pattern.matches("[ｦ-ﾝ]", str.substring(0, 1))) {
                    stringBuffer.delete(0, stringBuffer.length());
                    stringBuffer.append(str);
                    String str10 = null;
                    for (int i7 = 0; i7 < stringBuffer.length(); i7++) {
                        char charAt5 = stringBuffer.charAt(i7);
                        str10 = (charAt5 < ak[0] || charAt5 > ak[ak.length + (-1)]) ? str10 != null ? str10 + charAt5 : String.valueOf(charAt5) : str10 != null ? str10 + aj[charAt5 - ak[0]] : new StringBuilder().append(aj[charAt5 - ak[0]]).toString();
                    }
                    String str11 = str4 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(str10 + "%");
                    arrayList.add("% " + str10 + "%");
                    arrayList.add(str10 + "%");
                    arrayList.add("% " + str10 + "%");
                    stringBuffer.delete(0, stringBuffer.length());
                    stringBuffer.append(str10);
                    int i8 = 0;
                    while (i8 < stringBuffer.length()) {
                        char charAt6 = stringBuffer.charAt(i8);
                        if (Pattern.matches("[ァ-ン]", stringBuffer.substring(i8, i8 + 1))) {
                            stringBuffer.setCharAt(i8, (char) ((charAt6 - 12449) + 12353));
                        } else if (charAt6 == 12533) {
                            stringBuffer.setCharAt(i8, (char) 12363);
                        } else if (charAt6 == 12534) {
                            stringBuffer.setCharAt(i8, (char) 12369);
                        } else if (charAt6 == 12532) {
                            stringBuffer.setCharAt(i8, (char) 12358);
                            stringBuffer.insert(i8 + 1, (char) 12443);
                            i8++;
                        }
                        i8++;
                    }
                    str2 = str11 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                } else if (Pattern.matches("[A-Za-z]", str.substring(0, 1))) {
                    stringBuffer.delete(0, stringBuffer.length());
                    stringBuffer.append(str);
                    for (int i9 = 0; i9 < stringBuffer.length(); i9++) {
                        char charAt7 = stringBuffer.charAt(i9);
                        if (Pattern.matches("[a-z]", str.substring(i9, i9 + 1))) {
                            stringBuffer.setCharAt(i9, (char) ((charAt7 - 'a') + 65345));
                        } else if (Pattern.matches("[A-Z]", str.substring(i9, i9 + 1))) {
                            stringBuffer.setCharAt(i9, (char) ((charAt7 - 'A') + 65313));
                        }
                    }
                    str2 = str4 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                } else if (Pattern.matches("[Ａ-Ｚａ-ｚ]", str.substring(0, 1))) {
                    stringBuffer.delete(0, stringBuffer.length());
                    stringBuffer.append(str);
                    for (int i10 = 0; i10 < stringBuffer.length(); i10++) {
                        char charAt8 = stringBuffer.charAt(i10);
                        if (Pattern.matches("[ａ-ｚ]", str.substring(i10, i10 + 1))) {
                            stringBuffer.setCharAt(i10, (char) ((charAt8 - 65345) + 97));
                        } else if (Pattern.matches("[Ａ-Ｚ]", str.substring(i10, i10 + 1))) {
                            stringBuffer.setCharAt(i10, (char) ((charAt8 - 65313) + 65));
                        }
                    }
                    str2 = str4 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                    arrayList.add(stringBuffer.toString() + "%");
                    arrayList.add("% " + stringBuffer.toString() + "%");
                }
                Log.i("ContactDataStore", "selection = " + str2 + " args = " + arrayList.toString());
                objArr = new Object[]{str2, arrayList};
            }
            str2 = str4;
            Log.i("ContactDataStore", "selection = " + str2 + " args = " + arrayList.toString());
            objArr = new Object[]{str2, arrayList};
        }
        if (objArr != null) {
            String str12 = (String) objArr[0];
            List list = (List) objArr[1];
            String[] strArr2 = new String[list.size()];
            while (true) {
                int i11 = i2;
                if (i11 >= list.size()) {
                    break;
                }
                strArr2[i11] = (String) list.get(i11);
                i2 = i11 + 1;
            }
            strArr = strArr2;
            str3 = str12;
        } else {
            strArr = null;
            str3 = null;
        }
        if (b()) {
            if (!c()) {
                return str == null ? a(af.getContentResolver().query(uri, T, null, null, c + " ASC")) : a(a(af.getContentResolver().query(uri, T, null, null, c + " ASC"), str));
            }
        } else if (Locale.getDefault().equals(Locale.JAPAN)) {
            A = "GET_PHONETICALLY_SORTABLE_STRING(CASE WHEN (phonetic_name IS NOT NULL AND phonetic_name != '') THEN phonetic_name ELSE (CASE WHEN (name is NOT NULL AND name != '')THEN name ELSE (CASE WHEN primary_email IS NOT NULL THEN (SELECT data FROM contact_methods WHERE contact_methods._id = primary_email) ELSE (CASE WHEN primary_phone IS NOT NULL THEN (SELECT number FROM phones WHERE phones._id = primary_phone) ELSE null END) END) END) END) ASC";
        } else {
            A = "display_name COLLATE LOCALIZED ASC";
        }
        return af.getContentResolver().query(uri, T, str3, strArr, A);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x004f, code lost:
    
        if (r2.moveToFirst() != false) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0051, code lost:
    
        r3 = r2.getLong(r2.getColumnIndex(com.oki_access.android.ims.call.util.i.c));
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0060, code lost:
    
        if (r1 >= r11.size()) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x006e, code lost:
    
        if (r11.get(r1).longValue() != r3) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x00c4, code lost:
    
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0070, code lost:
    
        r7.addRow(new java.lang.Object[]{java.lang.Long.valueOf(r3), r2.getString(r2.getColumnIndex(com.oki_access.android.ims.call.util.i.e)), r2.getString(r2.getColumnIndex(com.oki_access.android.ims.call.util.i.F)), java.lang.Integer.valueOf(r2.getInt(r2.getColumnIndex(com.oki_access.android.ims.call.util.i.B))), java.lang.Long.valueOf(r2.getLong(r2.getColumnIndex(com.oki_access.android.ims.call.util.i.J)))});
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x00bc, code lost:
    
        if (r2.moveToNext() != false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00be, code lost:
    
        r2.close();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.database.Cursor a(java.util.ArrayList<java.lang.Long> r11) {
        /*
            r6 = 0
            r3 = 0
            android.database.MatrixCursor r7 = new android.database.MatrixCursor
            java.lang.String[] r0 = com.oki_access.android.ims.call.util.i.T
            r7.<init>(r0)
            boolean r0 = c()
            if (r0 == 0) goto L24
            android.content.Context r0 = com.oki_access.android.ims.call.util.i.af
            android.content.ContentResolver r0 = r0.getContentResolver()
            android.net.Uri r1 = com.oki_access.android.ims.call.util.i.M
            java.lang.String[] r2 = com.oki_access.android.ims.call.util.i.T
            java.lang.String r5 = com.oki_access.android.ims.call.util.i.A
            r4 = r3
            android.database.Cursor r2 = r0.query(r1, r2, r3, r4, r5)
        L20:
            if (r11 != 0) goto L49
            r0 = r2
        L23:
            return r0
        L24:
            android.content.Context r0 = com.oki_access.android.ims.call.util.i.af
            android.content.ContentResolver r0 = r0.getContentResolver()
            android.net.Uri r1 = com.oki_access.android.ims.call.util.i.M
            java.lang.String[] r2 = com.oki_access.android.ims.call.util.i.T
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = com.oki_access.android.ims.call.util.i.c
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = " ASC"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = r4.toString()
            r4 = r3
            android.database.Cursor r2 = r0.query(r1, r2, r3, r4, r5)
            goto L20
        L49:
            if (r2 == 0) goto Lc1
            boolean r0 = r2.moveToFirst()
            if (r0 == 0) goto Lbe
        L51:
            java.lang.String r0 = com.oki_access.android.ims.call.util.i.c
            int r0 = r2.getColumnIndex(r0)
            long r3 = r2.getLong(r0)
            r1 = r6
        L5c:
            int r0 = r11.size()
            if (r1 >= r0) goto Lb8
            java.lang.Object r0 = r11.get(r1)
            java.lang.Long r0 = (java.lang.Long) r0
            long r8 = r0.longValue()
            int r0 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r0 != 0) goto Lc4
            java.lang.String r0 = com.oki_access.android.ims.call.util.i.e
            int r0 = r2.getColumnIndex(r0)
            java.lang.String r0 = r2.getString(r0)
            java.lang.String r1 = com.oki_access.android.ims.call.util.i.F
            int r1 = r2.getColumnIndex(r1)
            java.lang.String r1 = r2.getString(r1)
            java.lang.String r5 = com.oki_access.android.ims.call.util.i.B
            int r5 = r2.getColumnIndex(r5)
            int r5 = r2.getInt(r5)
            java.lang.String r8 = com.oki_access.android.ims.call.util.i.J
            int r8 = r2.getColumnIndex(r8)
            long r8 = r2.getLong(r8)
            r10 = 5
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r10[r6] = r3
            r3 = 1
            r10[r3] = r0
            r0 = 2
            r10[r0] = r1
            r0 = 3
            java.lang.Integer r1 = java.lang.Integer.valueOf(r5)
            r10[r0] = r1
            r0 = 4
            java.lang.Long r1 = java.lang.Long.valueOf(r8)
            r10[r0] = r1
            r7.addRow(r10)
        Lb8:
            boolean r0 = r2.moveToNext()
            if (r0 != 0) goto L51
        Lbe:
            r2.close()
        Lc1:
            r0 = r7
            goto L23
        Lc4:
            int r0 = r1 + 1
            r1 = r0
            goto L5c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oki_access.android.ims.call.util.i.a(java.util.ArrayList):android.database.Cursor");
    }

    public static String a(int i2) {
        return af.getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(i2));
    }

    public static String a(Context context, long j2, String str) {
        com.oki_access.android.util.m.b("ContactDataStore", "convertContactNumber: phoneId=[" + j2 + "]");
        if (j2 < 0) {
            return d(context, str);
        }
        Cursor query = context.getContentResolver().query(ContentUris.withAppendedId(P, j2), new String[]{h}, null, null, c + " ASC");
        if (query == null) {
            return d(context, str);
        }
        try {
            if (query.moveToFirst()) {
                return query.getString(0);
            }
            query.close();
            return d(context, str);
        } finally {
            query.close();
        }
    }

    public static String a(Context context, String str) {
        Cursor query = context.getContentResolver().query(P, new String[]{e}, h + "=? OR REPLACE(REPLACE(REPLACE(REPLACE(" + h + ",'-',''),' ',''),'(',''),')','')=?", new String[]{PhoneNumberUtils.formatNumber(str), str}, "display_name COLLATE LOCALIZED ASC");
        String str2 = null;
        while (query.moveToNext()) {
            str2 = query.getString(query.getColumnIndex(e));
        }
        query.close();
        return str2;
    }

    public static Cursor b(long j2) {
        return af.getContentResolver().query(O, null, "mimetype='vnd.android.cursor.item/name' AND " + g + "=?", new String[]{String.valueOf(j2)}, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x00a5, code lost:
    
        r0 = r1.getString(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x00a9, code lost:
    
        if (r0 == null) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x00af, code lost:
    
        if (r0.length() != 0) goto L7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x00b1, code lost:
    
        r0 = a(7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x00a1, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x00a4, code lost:
    
        return r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:2:0x0054, code lost:
    
        if (r1.moveToFirst() != false) goto L4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0056, code lost:
    
        r3 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.h)).replace("-", "");
        r0 = r1.getColumnIndex(com.oki_access.android.ims.call.util.i.l);
        r4 = r1.getColumnIndex(com.oki_access.android.ims.call.util.i.n);
        r0 = r1.getInt(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0078, code lost:
    
        if (r0 == 0) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x007a, code lost:
    
        r0 = a(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x007e, code lost:
    
        r2.addRow(new java.lang.Object[]{java.lang.Long.valueOf(r1.getLong(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.c))), com.oki_access.android.ims.call.util.z.b(r3), r0});
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x009f, code lost:
    
        if (r1.moveToNext() != false) goto L17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.database.Cursor b(java.lang.String r11) {
        /*
            r10 = 3
            r9 = 2
            r8 = 1
            r7 = 0
            android.content.Context r0 = com.oki_access.android.ims.call.util.i.af
            android.content.ContentResolver r0 = r0.getContentResolver()
            android.net.Uri r1 = com.oki_access.android.ims.call.util.i.P
            r2 = 4
            java.lang.String[] r2 = new java.lang.String[r2]
            java.lang.String r3 = com.oki_access.android.ims.call.util.i.c
            r2[r7] = r3
            java.lang.String r3 = com.oki_access.android.ims.call.util.i.h
            r2[r8] = r3
            java.lang.String r3 = com.oki_access.android.ims.call.util.i.l
            r2[r9] = r3
            java.lang.String r3 = com.oki_access.android.ims.call.util.i.n
            r2[r10] = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = com.oki_access.android.ims.call.util.i.g
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = "=?"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String[] r4 = new java.lang.String[r8]
            r4[r7] = r11
            r5 = 0
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)
            android.database.MatrixCursor r2 = new android.database.MatrixCursor
            java.lang.String[] r0 = new java.lang.String[r10]
            java.lang.String r3 = "_id"
            r0[r7] = r3
            java.lang.String r3 = "number"
            r0[r8] = r3
            java.lang.String r3 = "typestring"
            r0[r9] = r3
            r2.<init>(r0)
            boolean r0 = r1.moveToFirst()
            if (r0 == 0) goto La1
        L56:
            java.lang.String r0 = com.oki_access.android.ims.call.util.i.h
            int r0 = r1.getColumnIndex(r0)
            java.lang.String r0 = r1.getString(r0)
            java.lang.String r3 = "-"
            java.lang.String r4 = ""
            java.lang.String r3 = r0.replace(r3, r4)
            java.lang.String r0 = com.oki_access.android.ims.call.util.i.l
            int r0 = r1.getColumnIndex(r0)
            java.lang.String r4 = com.oki_access.android.ims.call.util.i.n
            int r4 = r1.getColumnIndex(r4)
            int r0 = r1.getInt(r0)
            if (r0 == 0) goto La5
            java.lang.String r0 = a(r0)
        L7e:
            java.lang.String r4 = com.oki_access.android.ims.call.util.i.c
            int r4 = r1.getColumnIndex(r4)
            long r4 = r1.getLong(r4)
            java.lang.Object[] r6 = new java.lang.Object[r10]
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            r6[r7] = r4
            java.lang.String r3 = com.oki_access.android.ims.call.util.z.b(r3)
            r6[r8] = r3
            r6[r9] = r0
            r2.addRow(r6)
            boolean r0 = r1.moveToNext()
            if (r0 != 0) goto L56
        La1:
            r1.close()
            return r2
        La5:
            java.lang.String r0 = r1.getString(r4)
            if (r0 == 0) goto Lb1
            int r4 = r0.length()
            if (r4 != 0) goto L7e
        Lb1:
            r0 = 7
            java.lang.String r0 = a(r0)
            goto L7e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oki_access.android.ims.call.util.i.b(java.lang.String):android.database.Cursor");
    }

    public static List<Object> b(Context context, String str) {
        Cursor query = context.getContentResolver().query(P, new String[]{e, c}, h + "=? OR REPLACE(REPLACE(REPLACE(REPLACE(" + h + ",'-',''),' ',''),'(',''),')','')=?", new String[]{PhoneNumberUtils.formatNumber(str), str}, "display_name COLLATE LOCALIZED ASC");
        ArrayList arrayList = new ArrayList();
        while (query.moveToNext()) {
            int columnIndex = query.getColumnIndex(e);
            int columnIndex2 = query.getColumnIndex(c);
            arrayList.add(query.getString(columnIndex));
            arrayList.add(Long.valueOf(query.getLong(columnIndex2)));
        }
        query.close();
        return arrayList;
    }

    private static boolean b() {
        return Integer.valueOf(Build.VERSION.SDK).intValue() > 4;
    }

    public static Cursor c(long j2) {
        return af.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null, "contact_id=?", new String[]{String.valueOf(j2)}, null);
    }

    private static boolean c() {
        return Integer.valueOf(Build.VERSION.SDK).intValue() > 7;
    }

    public static Object[] c(Context context, String str) {
        String str2;
        String str3;
        int i2;
        int i3 = -1;
        Cursor query = context.getContentResolver().query(P, new String[]{e, l, n}, h + "=? OR REPLACE(REPLACE(REPLACE(REPLACE(" + h + ",'-',''),' ',''),'(',''),')','')=?", new String[]{PhoneNumberUtils.formatNumber(str), str}, "display_name COLLATE LOCALIZED ASC");
        if (query == null || !query.moveToFirst()) {
            str2 = null;
            str3 = null;
            i2 = -1;
        } else {
            int columnIndex = query.getColumnIndex(e);
            int columnIndex2 = query.getColumnIndex(l);
            int columnIndex3 = query.getColumnIndex(n);
            str2 = query.getString(columnIndex);
            i3 = query.getInt(columnIndex2);
            String string = query.getString(columnIndex3);
            if (string == null || string.length() == 0) {
                string = af.getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(7));
            }
            str3 = string;
            i2 = query.getCount();
        }
        query.close();
        return new Object[]{str2, Integer.valueOf(i3), str3, Integer.valueOf(i2)};
    }

    public static Object[] c(String str) {
        String str2;
        String str3 = "display_name LIKE ? OR display_name LIKE ? OR " + F + " LIKE ? OR " + F + " LIKE ? OR data1 LIKE ? OR REPLACE(REPLACE(REPLACE(REPLACE(" + h + ",'-',''),' ',''),'(',''),')','') LIKE ?";
        ArrayList arrayList = new ArrayList();
        arrayList.add(str + "%");
        arrayList.add("% " + str + "%");
        arrayList.add(str + "%");
        arrayList.add("% " + str + "%");
        arrayList.add(PhoneNumberUtils.formatNumber(str) + '%');
        arrayList.add(str + '%');
        StringBuffer stringBuffer = new StringBuffer();
        if (str != null && str.length() != 0) {
            if (Pattern.matches("[ぁ-ん]", str.substring(0, 1))) {
                stringBuffer.delete(0, stringBuffer.length());
                stringBuffer.append(str);
                for (int i2 = 0; i2 < str.length(); i2++) {
                    char charAt = stringBuffer.charAt(i2);
                    if (Pattern.matches("[ぁ-ん]", stringBuffer.subSequence(i2, i2 + 1))) {
                        stringBuffer.setCharAt(i2, (char) ((charAt - 12353) + 12449));
                    }
                }
                String str4 = str3 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
                String str5 = null;
                for (int i3 = 0; i3 < stringBuffer.length(); i3++) {
                    char charAt2 = stringBuffer.charAt(i3);
                    str5 = (charAt2 < ah[0] || charAt2 > ah[ah.length + (-1)]) ? str5 != null ? str5 + charAt2 : String.valueOf(charAt2) : str5 != null ? str5 + ai[charAt2 - ah[0]] : ai[charAt2 - ah[0]];
                }
                String str6 = str4 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(str5 + "%");
                arrayList.add("% " + str5 + "%");
                arrayList.add(str5 + "%");
                arrayList.add("% " + str5 + "%");
                str2 = str6;
            } else if (Pattern.matches("[ァ-ン]", str.substring(0, 1)) || str.substring(0, 1).equals("ヵ") || str.substring(0, 1).equals("ヶ") || str.substring(0, 1).equals("ヴ")) {
                stringBuffer.delete(0, stringBuffer.length());
                stringBuffer.append(str);
                String str7 = null;
                for (int i4 = 0; i4 < stringBuffer.length(); i4++) {
                    char charAt3 = stringBuffer.charAt(i4);
                    str7 = (charAt3 < ah[0] || charAt3 > ah[ah.length + (-1)]) ? str7 != null ? str7 + charAt3 : String.valueOf(charAt3) : str7 != null ? str7 + ai[charAt3 - ah[0]] : ai[charAt3 - ah[0]];
                }
                String str8 = str3 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(str7 + "%");
                arrayList.add("% " + str7 + "%");
                arrayList.add(str7 + "%");
                arrayList.add("% " + str7 + "%");
                int i5 = 0;
                while (i5 < stringBuffer.length()) {
                    char charAt4 = stringBuffer.charAt(i5);
                    if (Pattern.matches("[ァ-ン]", stringBuffer.substring(i5, i5 + 1))) {
                        stringBuffer.setCharAt(i5, (char) ((charAt4 - 12449) + 12353));
                    } else if (charAt4 == 12533) {
                        stringBuffer.setCharAt(i5, (char) 12363);
                    } else if (charAt4 == 12534) {
                        stringBuffer.setCharAt(i5, (char) 12369);
                    } else if (charAt4 == 12532) {
                        stringBuffer.setCharAt(i5, (char) 12358);
                        stringBuffer.insert(i5 + 1, (char) 12443);
                        i5++;
                    }
                    i5++;
                }
                str2 = str8 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
            } else if (Pattern.matches("[ｦ-ﾝ]", str.substring(0, 1))) {
                stringBuffer.delete(0, stringBuffer.length());
                stringBuffer.append(str);
                String str9 = null;
                for (int i6 = 0; i6 < stringBuffer.length(); i6++) {
                    char charAt5 = stringBuffer.charAt(i6);
                    str9 = (charAt5 < ak[0] || charAt5 > ak[ak.length + (-1)]) ? str9 != null ? str9 + charAt5 : String.valueOf(charAt5) : str9 != null ? str9 + aj[charAt5 - ak[0]] : new StringBuilder().append(aj[charAt5 - ak[0]]).toString();
                }
                String str10 = str3 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(str9 + "%");
                arrayList.add("% " + str9 + "%");
                arrayList.add(str9 + "%");
                arrayList.add("% " + str9 + "%");
                stringBuffer.delete(0, stringBuffer.length());
                stringBuffer.append(str9);
                int i7 = 0;
                while (i7 < stringBuffer.length()) {
                    char charAt6 = stringBuffer.charAt(i7);
                    if (Pattern.matches("[ァ-ン]", stringBuffer.substring(i7, i7 + 1))) {
                        stringBuffer.setCharAt(i7, (char) ((charAt6 - 12449) + 12353));
                    } else if (charAt6 == 12533) {
                        stringBuffer.setCharAt(i7, (char) 12363);
                    } else if (charAt6 == 12534) {
                        stringBuffer.setCharAt(i7, (char) 12369);
                    } else if (charAt6 == 12532) {
                        stringBuffer.setCharAt(i7, (char) 12358);
                        stringBuffer.insert(i7 + 1, (char) 12443);
                        i7++;
                    }
                    i7++;
                }
                str2 = str10 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
            } else if (Pattern.matches("[A-Za-z]", str.substring(0, 1))) {
                stringBuffer.delete(0, stringBuffer.length());
                stringBuffer.append(str);
                for (int i8 = 0; i8 < stringBuffer.length(); i8++) {
                    char charAt7 = stringBuffer.charAt(i8);
                    if (Pattern.matches("[a-z]", str.substring(i8, i8 + 1))) {
                        stringBuffer.setCharAt(i8, (char) ((charAt7 - 'a') + 65345));
                    } else if (Pattern.matches("[A-Z]", str.substring(i8, i8 + 1))) {
                        stringBuffer.setCharAt(i8, (char) ((charAt7 - 'A') + 65313));
                    }
                }
                str2 = str3 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
            } else if (Pattern.matches("[Ａ-Ｚａ-ｚ]", str.substring(0, 1))) {
                stringBuffer.delete(0, stringBuffer.length());
                stringBuffer.append(str);
                for (int i9 = 0; i9 < stringBuffer.length(); i9++) {
                    char charAt8 = stringBuffer.charAt(i9);
                    if (Pattern.matches("[ａ-ｚ]", str.substring(i9, i9 + 1))) {
                        stringBuffer.setCharAt(i9, (char) ((charAt8 - 65345) + 97));
                    } else if (Pattern.matches("[Ａ-Ｚ]", str.substring(i9, i9 + 1))) {
                        stringBuffer.setCharAt(i9, (char) ((charAt8 - 65313) + 65));
                    }
                }
                str2 = str3 + " OR " + F + " LIKE ? OR " + F + " LIKE ? OR display_name LIKE ? OR display_name LIKE ?";
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
                arrayList.add(stringBuffer.toString() + "%");
                arrayList.add("% " + stringBuffer.toString() + "%");
            }
            Log.i("ContactDataStore", "selection = " + str2 + " args = " + arrayList.toString());
            return new Object[]{str2, arrayList.toArray(new String[0])};
        }
        str2 = str3;
        Log.i("ContactDataStore", "selection = " + str2 + " args = " + arrayList.toString());
        return new Object[]{str2, arrayList.toArray(new String[0])};
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0177, code lost:
    
        if (r1.moveToFirst() != false) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0179, code lost:
    
        r2 = r1.getLong(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.c));
        r4 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.j));
        r0 = r1.getInt(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.l));
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0197, code lost:
    
        if (r0 == 0) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0199, code lost:
    
        r0 = com.oki_access.android.ims.call.util.i.af.getString(android.provider.ContactsContract.CommonDataKinds.Email.getTypeLabelResource(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x01a3, code lost:
    
        r9.addRow(new java.lang.Object[]{java.lang.Integer.valueOf(r6), 2, java.lang.Long.valueOf(r2), r4, r0, -1, null, null, null, null});
        r6 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x01e6, code lost:
    
        if (r1.moveToNext() != false) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x02e7, code lost:
    
        r0 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.n));
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x02f1, code lost:
    
        if (r0 == null) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x02f7, code lost:
    
        if (r0.length() != 0) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x02f9, code lost:
    
        r0 = com.oki_access.android.ims.call.util.i.af.getString(android.provider.ContactsContract.CommonDataKinds.Email.getTypeLabelResource(3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x01e8, code lost:
    
        r1.deactivate();
        r1.close();
        r1 = com.oki_access.android.ims.call.util.i.af.getContentResolver().query(com.oki_access.android.ims.call.util.i.O, null, com.oki_access.android.ims.call.util.i.g + "=? AND mimetype='vnd.android.cursor.item/postal-address_v2'", new java.lang.String[]{java.lang.String.valueOf(r15)}, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x021f, code lost:
    
        if (r1.moveToFirst() == false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0221, code lost:
    
        r2 = r1.getLong(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.c));
        r4 = new java.lang.StringBuilder();
        r0 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.w));
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x023a, code lost:
    
        if (r0 == null) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x023c, code lost:
    
        r4.append(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0244, code lost:
    
        if (r4.length() <= 3) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0246, code lost:
    
        r4.insert(3, '-');
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x024c, code lost:
    
        r5 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.v));
        r7 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.t));
        r8 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.q));
        r0 = r1.getInt(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.l));
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0274, code lost:
    
        if (r0 == 0) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0276, code lost:
    
        r0 = com.oki_access.android.ims.call.util.i.af.getString(android.provider.ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabelResource(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0280, code lost:
    
        r9.addRow(new java.lang.Object[]{java.lang.Integer.valueOf(r6), 3, java.lang.Long.valueOf(r2), null, r0, -1, r4.toString(), r5, r7, r8});
        r6 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x02c4, code lost:
    
        if (r1.moveToNext() != false) goto L56;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0306, code lost:
    
        r0 = r1.getString(r1.getColumnIndex(com.oki_access.android.ims.call.util.i.n));
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0310, code lost:
    
        if (r0 == null) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0316, code lost:
    
        if (r0.length() != 0) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0318, code lost:
    
        r0 = com.oki_access.android.ims.call.util.i.af.getString(android.provider.ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabelResource(3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x02c6, code lost:
    
        r1.deactivate();
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x02cc, code lost:
    
        return r9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.database.Cursor d(long r15) {
        /*
            Method dump skipped, instructions count: 814
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oki_access.android.ims.call.util.i.d(long):android.database.Cursor");
    }

    private static String d(Context context, String str) {
        com.oki_access.android.util.m.b("ContactDataStore", "convertContactNumber: number=[" + str + "]");
        Cursor query = context.getContentResolver().query(P, new String[]{h}, h + "=? OR REPLACE(REPLACE(REPLACE(REPLACE(" + h + ",'-',''),' ',''),'(',''),')','')=?", new String[]{PhoneNumberUtils.formatNumber(str), str}, c + " ASC");
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    str = query.getString(0);
                }
            } finally {
                query.close();
            }
        }
        return str;
    }

    public static String e(long j2) {
        String string;
        String str;
        String string2;
        String str2 = null;
        Cursor query = af.getContentResolver().query(P, ab, g + "=?", new String[]{String.valueOf(j2)}, null);
        if (query != null && query.moveToFirst()) {
            boolean z2 = false;
            while (true) {
                int columnIndex = query.getColumnIndex(l);
                int columnIndex2 = query.getColumnIndex(n);
                int i2 = query.getInt(columnIndex);
                if (!z2) {
                    if (i2 != 0) {
                        string2 = a(i2);
                    } else {
                        string2 = query.getString(columnIndex2);
                        if (string2 == null || string2.length() == 0) {
                            string2 = a(7);
                        }
                    }
                    str = string2;
                    z2 = true;
                } else {
                    if (i2 != 0) {
                        string = a(i2);
                    } else {
                        string = query.getString(columnIndex2);
                        if (string == null || string.length() == 0) {
                            string = a(7);
                        }
                    }
                    str = str2 + ", " + string;
                }
                if (!query.moveToNext()) {
                    break;
                }
                str2 = str;
            }
            str2 = str;
        }
        query.close();
        return str2;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x006d -> B:15:0x001f). Please report as a decompilation issue!!! */
    public static byte[] f(long j2) {
        byte[] bArr;
        try {
            Cursor query = af.getContentResolver().query(ContentUris.withAppendedId(N, j2), new String[]{y}, null, null, null);
            try {
                if (query == null) {
                    return null;
                }
                try {
                    if (query.moveToFirst()) {
                        bArr = query.getBlob(0);
                    } else {
                        com.oki_access.android.util.m.a("ContactDataStore", "Index 0 requested, with a size of 0");
                        query.deactivate();
                        query.close();
                        bArr = null;
                    }
                } catch (Exception e2) {
                    com.oki_access.android.util.m.d("ContactDataStore", "getPhotoData2:Exception = " + e2);
                    query.deactivate();
                    query.close();
                    bArr = null;
                }
                return bArr;
            } finally {
                query.deactivate();
                query.close();
            }
        } catch (Exception e3) {
            com.oki_access.android.util.m.d("ContactDataStore", "getPhotoData1:Exception = " + e3);
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0073, code lost:
    
        r11 = r3.getLong(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.c));
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x007f, code lost:
    
        r2 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0083, code lost:
    
        if (r2 >= r4.size()) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0091, code lost:
    
        if (((java.lang.Long) r4.get(r2)).longValue() != r11) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x01b4, code lost:
    
        r1 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0093, code lost:
    
        r13 = r3.getLong(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.g));
        r6 = r3.getString(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.e));
        r15 = r3.getString(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.h));
        r16 = r3.getInt(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.l));
        r1 = r3.getString(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.n));
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x00c5, code lost:
    
        if (r1 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x00cb, code lost:
    
        if (r1.length() != 0) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00d2, code lost:
    
        r17 = r3.getString(r3.getColumnIndex(com.oki_access.android.ims.call.util.i.f));
        r0 = new java.lang.Object[7];
        r0[0] = java.lang.Long.valueOf(r11);
        r0[1] = java.lang.Long.valueOf(r13);
        r0[2] = r6;
        r0[3] = r15;
        r0[4] = java.lang.Integer.valueOf(r16);
        r0[5] = r1;
        r0[6] = r17;
        r5[r2] = r0;
        r1 = r8.indexOf(java.lang.Long.valueOf(r11));
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x011d, code lost:
    
        if (r15.equals(r9.get(r1)) != false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0123, code lost:
    
        if (r11 == (-1)) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0125, code lost:
    
        r2 = new android.content.ContentValues();
        r2.put("phone_num", r15);
        com.oki_access.android.ims.call.util.i.af.getContentResolver().update(com.oki_access.android.ims.call.provider.e.f1080a, r2, "phone_id=?", new java.lang.String[]{java.lang.String.valueOf(r11)});
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0147, code lost:
    
        r8.remove(r1);
        r9.remove(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0151, code lost:
    
        if (r3.moveToNext() != false) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00cd, code lost:
    
        r1 = a(7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0153, code lost:
    
        r6 = r8.iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x015b, code lost:
    
        if (r6.hasNext() == false) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x015d, code lost:
    
        r11 = ((java.lang.Long) r6.next()).longValue();
        r2 = r8.indexOf(java.lang.Long.valueOf(r11));
        r13 = r4.indexOf(java.lang.Long.valueOf(r11));
        r1 = (java.lang.String) r9.get(r2);
        r2 = (java.lang.String) r9.get(r2);
        r15 = new java.lang.Object[7];
        r15[0] = java.lang.Long.valueOf(r11);
        r15[1] = -1L;
        r15[2] = r1;
        r15[3] = r2;
        r15[4] = 0;
        r15[5] = null;
        r15[6] = "0";
        r5[r13] = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x01b9, code lost:
    
        r2 = r5.length;
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x01bb, code lost:
    
        if (r1 >= r2) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x01bd, code lost:
    
        r4 = r5[r1];
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x01bf, code lost:
    
        if (r4 == null) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x01c1, code lost:
    
        r7.addRow(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x01c4, code lost:
    
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x01c7, code lost:
    
        r3.deactivate();
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x01cd, code lost:
    
        r10.deactivate();
        r10.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0027, code lost:
    
        if (r10.moveToFirst() != false) goto L5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0029, code lost:
    
        r8.add(java.lang.Long.valueOf(r10.getLong(r10.getColumnIndex("phone_id"))));
        r9.add(r10.getString(r10.getColumnIndex("phone_num")));
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x004b, code lost:
    
        if (r10.moveToNext() != false) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x004d, code lost:
    
        r3 = com.oki_access.android.ims.call.util.i.af.getContentResolver().query(com.oki_access.android.ims.call.util.i.P, com.oki_access.android.ims.call.util.i.ad, "mimetype='vnd.android.cursor.item/phone_v2'", null, null);
        r4 = new java.util.ArrayList();
        r4.addAll(r8);
        r5 = new java.lang.Object[r8.size()];
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0071, code lost:
    
        if (r3.moveToFirst() == false) goto L27;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final synchronized android.database.Cursor a() {
        /*
            Method dump skipped, instructions count: 469
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oki_access.android.ims.call.util.i.a():android.database.Cursor");
    }

    public final Cursor b(Cursor cursor) {
        String str;
        Object obj = null;
        this.ag = new StringBuffer();
        if (cursor != null && cursor.moveToFirst()) {
            MatrixCursor matrixCursor = new MatrixCursor(V);
            do {
                long j2 = cursor.getLong(0);
                String string = cursor.getString(1);
                long j3 = cursor.getLong(4);
                String string2 = cursor.getString(2);
                int i2 = cursor.getInt(3);
                if (string2 == null || string2.length() == 0 || string2.equals(string)) {
                    if (string == null || string.length() == 0) {
                        str = "Unknown";
                        if (!"Unknown".equals(obj)) {
                            matrixCursor.addRow(new Object[]{-1, "Unknown", null, -1, -1});
                        }
                    } else if (Pattern.matches("[A-Za-zぁ-んァ-ンｦ-ﾝ]", string.substring(0, 1))) {
                        str = string.substring(0, 1);
                        if (Pattern.matches("[a-z]", str)) {
                            str = str.toUpperCase();
                        } else if (Pattern.matches("[ぁ-んァ-ンｦ-ﾝ]", str)) {
                            this.ag.delete(0, this.ag.length());
                            if (Pattern.matches("[ぁ-おァ-オｧ-ｵ]", str)) {
                                this.ag.append("あ");
                            } else if (Pattern.matches("[か-ごカ-ゴｶ-ｺ]", str)) {
                                this.ag.append("か");
                            } else if (Pattern.matches("[さ-ぞサ-ゾｻ-ｿ]", str)) {
                                this.ag.append("さ");
                            } else if (Pattern.matches("[た-どタ-ドﾀ-ﾄ]", str)) {
                                this.ag.append("た");
                            } else if (Pattern.matches("[な-のナ-ノﾅ-ﾉ]", str)) {
                                this.ag.append("な");
                            } else if (Pattern.matches("[は-ぽハ-ポﾊ-ﾎ]", str)) {
                                this.ag.append("は");
                            } else if (Pattern.matches("[ま-もマ-モﾏ-ﾓ]", str)) {
                                this.ag.append("ま");
                            } else if (Pattern.matches("[ゃ-よャ-ヨｬ-ﾖ]", str)) {
                                this.ag.append("や");
                            } else if (Pattern.matches("[ら-ろラ-ロﾗ-ﾛ]", str)) {
                                this.ag.append("ら");
                            } else {
                                this.ag.append("わ");
                            }
                            str = this.ag.substring(0);
                        }
                        if (!str.equals(obj)) {
                            matrixCursor.addRow(new Object[]{-1, str, null, -1, -1});
                            obj = str;
                        }
                    } else if (Pattern.matches("[0-9０-９]", string.substring(0, 1))) {
                        str = "Number";
                        if (!"Number".equals(obj)) {
                            matrixCursor.addRow(new Object[]{-1, "Number", null, -1, -1});
                            obj = "Number";
                        }
                    } else {
                        str = "Other";
                        if (!"Other".equals(obj)) {
                            matrixCursor.addRow(new Object[]{-1, "Other", null, -1, -1});
                            obj = "Other";
                        }
                    }
                    obj = str;
                } else {
                    str = string2.substring(0, 1);
                    if (Pattern.matches("[a-z]", str)) {
                        str = str.toUpperCase();
                    } else if (Pattern.matches("[ぁ-んァ-ンｦ-ﾝ]", str)) {
                        this.ag.delete(0, this.ag.length());
                        if (Pattern.matches("[ぁ-おァ-オｧ-ｵ]", str)) {
                            this.ag.append("あ");
                        } else if (Pattern.matches("[か-ごカ-ゴｶ-ｺ]", str)) {
                            this.ag.append("か");
                        } else if (Pattern.matches("[さ-ぞサ-ゾｻ-ｿ]", str)) {
                            this.ag.append("さ");
                        } else if (Pattern.matches("[た-どタ-ドﾀ-ﾄ]", str)) {
                            this.ag.append("た");
                        } else if (Pattern.matches("[な-のナ-ノﾅ-ﾉ]", str)) {
                            this.ag.append("な");
                        } else if (Pattern.matches("[は-ぽハ-ポﾊ-ﾎ]", str)) {
                            this.ag.append("は");
                        } else if (Pattern.matches("[ま-もマ-モﾏ-ﾓ]", str)) {
                            this.ag.append("ま");
                        } else if (Pattern.matches("[ゃ-よャ-ヨｬ-ﾖ]", str)) {
                            this.ag.append("や");
                        } else if (Pattern.matches("[ら-ろラ-ロﾗ-ﾛ]", str)) {
                            this.ag.append("ら");
                        } else {
                            this.ag.append("わ");
                        }
                        str = this.ag.substring(0);
                    }
                    if (!str.equals(obj)) {
                        matrixCursor.addRow(new Object[]{-1, str, null, -1, -1});
                        obj = str;
                    }
                    obj = str;
                }
                matrixCursor.addRow(new Object[]{Long.valueOf(j2), string, null, Long.valueOf(j3), Integer.valueOf(i2)});
            } while (cursor.moveToNext());
            return matrixCursor;
        }
        return null;
    }
}
