package com.sds.samsung.global;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/* loaded from: classes.dex */
public class App extends MultiDexApplication {
    public static boolean IS_RUNNING = false;
    public static final String PREF_APP_VERSION = "app_version";
    public static final String URL_API_GET_IDENTITY = "http://displaysolutions.samsung.com/api/identity/get";
    public static final String URL_API_PUSH_LANDING = "http://displaysolutions.samsung.com/api/push/landing";
    public static final String URL_API_REG_IDENTITY = "http://displaysolutions.samsung.com/api/identity/register";
    public static final String URL_ROOT = "http://displaysolutions.samsung.com";
    public static final String URL_ROOT_REAL = "http://displaysolutions.samsung.com";
    public static final String URL_ROOT_TEST = "http://dsf-stg.devtree.co.kr";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.multidex.MultiDexApplication, android.content.ContextWrapper
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
