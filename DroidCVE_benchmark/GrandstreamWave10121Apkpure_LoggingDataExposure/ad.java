package com.softphone.common.view;

import android.util.Log;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
class ad extends ac {

    /* renamed from: a, reason: collision with root package name */
    private Method f250a;
    private Field b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ad() {
        try {
            this.f250a = View.class.getDeclaredMethod("getDisplayList", null);
        } catch (NoSuchMethodException e) {
            Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", e);
        }
        try {
            this.b = View.class.getDeclaredField("mRecreateDisplayList");
            this.b.setAccessible(true);
        } catch (NoSuchFieldException e2) {
            Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", e2);
        }
    }

    @Override // com.softphone.common.view.ac, com.softphone.common.view.ab
    public void a(SimpleSlidingPaneLayout simpleSlidingPaneLayout, View view) {
        if (this.f250a == null || this.b == null) {
            view.invalidate();
            return;
        }
        try {
            this.b.setBoolean(view, true);
            this.f250a.invoke(view, null);
        } catch (Exception e) {
            Log.e("SlidingPaneLayout", "Error refreshing display list state", e);
        }
        super.a(simpleSlidingPaneLayout, view);
    }
}
