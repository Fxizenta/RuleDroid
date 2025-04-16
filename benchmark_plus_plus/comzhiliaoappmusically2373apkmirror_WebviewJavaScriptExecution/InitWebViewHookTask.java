package com.ss.android.ugc.aweme.legoImp.task;

import X.AbstractC19080oZ;
import X.AbstractC51091z6;
import X.C0WB;
import X.C0Y1;
import X.C15920jT;
import X.C17990mo;
import X.C523122o;
import X.EnumC19120od;
import X.EnumC19140of;
import X.EnumC19150og;
import X.InterfaceC30081Fb;
import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.bytedance.covode.number.Covode;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;

/* loaded from: classes.dex */
public final class InitWebViewHookTask implements InterfaceC30081Fb {
    static {
        Covode.recordClassIndex(77405);
    }

    @Override // X.InterfaceC19050oW
    public final boolean meetTrigger() {
        return true;
    }

    @Override // X.InterfaceC19050oW
    public final String prefix() {
        return "task_";
    }

    @Override // X.InterfaceC30081Fb
    public final boolean serialExecute() {
        return false;
    }

    @Override // X.InterfaceC19050oW
    public final int targetProcess() {
        return 1048575;
    }

    @Override // X.InterfaceC19050oW
    public final List triggerOtherLegoComponents() {
        return null;
    }

    @Override // X.InterfaceC19050oW
    public final EnumC19140of triggerType() {
        return AbstractC19080oZ.LIZ(this);
    }

    @Override // X.InterfaceC19050oW
    public final EnumC19120od scenesType() {
        return EnumC19120od.DEFAULT;
    }

    @Override // X.InterfaceC30081Fb
    public final EnumC19150og type() {
        return EnumC19150og.BACKGROUND;
    }

    @Override // X.InterfaceC19050oW
    public final String key() {
        return getClass().getSimpleName();
    }

    @Override // X.InterfaceC19050oW
    public final void run(Context context) {
        final Object invoke;
        Context LIZ = C0Y1.LJJI.LIZ();
        if (Build.VERSION.SDK_INT >= 21 && C523122o.LJI.contains(LIZ.getPackageName())) {
            try {
                if (!C523122o.LJ.getAndSet(true)) {
                    Context applicationContext = LIZ.getApplicationContext();
                    if (C17990mo.LIZJ && applicationContext == null) {
                        applicationContext = C17990mo.LIZ;
                    }
                    C523122o.LIZLLL = applicationContext;
                    C523122o.LIZ();
                    Method method = C523122o.LIZIZ;
                    Object[] objArr = new Object[0];
                    Pair<Boolean, Object> LIZ2 = C0WB.LIZ(method, new Object[]{null, objArr}, 110000, "java.lang.Object", true, "com_bytedance_lynx_hybrid_webkit_WebViewHooker_java_lang_reflect_Method_invoke(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
                    if (((Boolean) LIZ2.first).booleanValue()) {
                        invoke = LIZ2.second;
                    } else {
                        invoke = method.invoke(null, objArr);
                        C0WB.LIZ(invoke, method, new Object[]{null, objArr}, "com_bytedance_lynx_hybrid_webkit_WebViewHooker_java_lang_reflect_Method_invoke(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
                    }
                    C523122o.LIZ.set(null, new AbstractC51091z6(invoke) { // from class: X.1z7
                        static {
                            Covode.recordClassIndex(28942);
                        }

                        public static Object LIZ(Method method2, Object obj, Object[] objArr2) {
                            Pair<Boolean, Object> LIZ3 = C0WB.LIZ(method2, new Object[]{obj, objArr2}, 110000, "java.lang.Object", true, "com_bytedance_lynx_hybrid_webkit_WebViewHooker$WebViewFactoryProviderInvocationHandler_java_lang_reflect_Method_invoke(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
                            if (((Boolean) LIZ3.first).booleanValue()) {
                                return LIZ3.second;
                            }
                            Object invoke2 = method2.invoke(obj, objArr2);
                            C0WB.LIZ(invoke2, method2, new Object[]{obj, objArr2}, "com_bytedance_lynx_hybrid_webkit_WebViewHooker$WebViewFactoryProviderInvocationHandler_java_lang_reflect_Method_invoke(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
                            return invoke2;
                        }

                        @Override // java.lang.reflect.InvocationHandler
                        public final Object invoke(Object obj, Method method2, Object[] objArr2) {
                            final WebView webView;
                            if ("createWebView".equals(method2.getName())) {
                                final Object LIZ3 = LIZ(method2, this.LIZ, objArr2);
                                if (objArr2[0] instanceof WebView) {
                                    webView = (WebView) objArr2[0];
                                } else {
                                    webView = null;
                                }
                                return new AbstractC51091z6(LIZ3, webView) { // from class: X.22p
                                    public C90523gV LIZIZ;
                                    public C523322q LIZJ;
                                    public WebView LIZLLL;

                                    static {
                                        Covode.recordClassIndex(28943);
                                    }

                                    public static Object LIZ(Method method3, Object obj2, Object[] objArr3) {
                                        Pair<Boolean, Object> LIZ4 = C0WB.LIZ(method3, new Object[]{obj2, objArr3}, 110000, "java.lang.Object", true, "com_bytedance_lynx_hybrid_webkit_WebViewHooker$WebViewProviderInvocationHandler_java_lang_reflect_Method_invoke(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
                                        if (((Boolean) LIZ4.first).booleanValue()) {
                                            return LIZ4.second;
                                        }
                                        Object invoke2 = method3.invoke(obj2, objArr3);
                                        C0WB.LIZ(invoke2, method3, new Object[]{obj2, objArr3}, "com_bytedance_lynx_hybrid_webkit_WebViewHooker$WebViewProviderInvocationHandler_java_lang_reflect_Method_invoke(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
                                        return invoke2;
                                    }

                                    {
                                        this.LIZLLL = webView;
                                    }

                                    @Override // java.lang.reflect.InvocationHandler
                                    public final Object invoke(Object obj2, Method method3, Object[] objArr3) {
                                        String name = method3.getName();
                                        if ("setWebViewClient".equals(name)) {
                                            this.LIZIZ.LIZ((WebViewClient) objArr3[0]);
                                            return null;
                                        }
                                        if ("getWebViewClient".equals(name)) {
                                            C90523gV c90523gV = this.LIZIZ;
                                            if (c90523gV.LIZLLL == C90523gV.LIZJ) {
                                                return null;
                                            }
                                            return c90523gV.LIZLLL;
                                        }
                                        if ("init".equals(name)) {
                                            Object LIZ4 = LIZ(method3, this.LIZ, objArr3);
                                            this.LIZIZ = new C90523gV((byte) 0);
                                            LIZ(C523122o.LIZJ, this.LIZ, new Object[]{this.LIZIZ});
                                            C523322q c523322q = new C523322q((byte) 0);
                                            this.LIZJ = c523322q;
                                            c523322q.LIZJ = this.LIZ;
                                            this.LIZJ.LIZLLL = this.LIZLLL;
                                            C523122o.LJFF.put(this.LIZLLL, new WeakReference<>(this.LIZIZ));
                                            return LIZ4;
                                        }
                                        if ("loadUrl".equals(name)) {
                                            Class<?>[] parameterTypes = method3.getParameterTypes();
                                            if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {
                                                EnumC523422r.LOAD_URL_1.LIZ(method3);
                                                this.LIZJ.LIZ((String) objArr3[0]);
                                                return null;
                                            }
                                            if (parameterTypes.length == 2 && parameterTypes[0] == String.class && parameterTypes[1] == java.util.Map.class) {
                                                EnumC523422r.LOAD_URL_2.LIZ(method3);
                                                this.LIZJ.LIZ((String) objArr3[0], (java.util.Map<String, String>) objArr3[1]);
                                                return null;
                                            }
                                        } else if ("addJavascriptInterface".equals(name)) {
                                            EnumC523422r.ADD_JAVASCRIPT_INTERFACE.LIZ(method3);
                                            this.LIZJ.LIZ(objArr3[0], (String) objArr3[1]);
                                            return null;
                                        }
                                        return LIZ(method3, this.LIZ, objArr3);
                                    }
                                }.LIZ();
                            }
                            return LIZ(method2, this.LIZ, objArr2);
                        }
                    }.LIZ());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        C15920jT.LIZLLL().LJIIJ();
    }
}
