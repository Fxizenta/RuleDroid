package com.bytedance.sdk.openadsdk.multipro;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/* loaded from: classes.dex */
public class TTMultiProvider extends ContentProvider {
    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (!com.bytedance.sdk.openadsdk.core.h.d.a()) {
            return false;
        }
        f.b(getContext()).a(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (com.bytedance.sdk.openadsdk.core.h.d.a()) {
            return f.b(getContext()).a(uri, strArr, str, strArr2, str2);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (com.bytedance.sdk.openadsdk.core.h.d.a()) {
            return f.b(getContext()).a(uri);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (com.bytedance.sdk.openadsdk.core.h.d.a()) {
            return f.b(getContext()).a(uri, contentValues);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        if (com.bytedance.sdk.openadsdk.core.h.d.a()) {
            return f.b(getContext()).a(uri, str, strArr);
        }
        return 0;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (com.bytedance.sdk.openadsdk.core.h.d.a()) {
            return f.b(getContext()).a(uri, contentValues, str, strArr);
        }
        return 0;
    }
}
