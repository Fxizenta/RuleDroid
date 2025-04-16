package com.milestonesys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class s implements Serializable {
    private long a;
    private String b;
    private String c;
    private String d;
    private String e;
    private int f;
    private String g;
    private int h;
    private String i;
    private a j;
    private URL k;
    private String l;
    private String m;
    private int n;
    private String o;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class a {
        private Object[] b = new Object[2];

        public a() {
            for (int i = 0; i < 2; i++) {
                this.b[i] = new ArrayList();
            }
        }

        public String a() {
            JSONArray jSONArray = new JSONArray();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= 2) {
                    break;
                }
                try {
                    JSONArray jSONArray2 = new JSONArray();
                    for (i iVar : a(i2)) {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("ConnectionHost", iVar.a());
                        int b = iVar.b();
                        if (b < 0 || b > 65535) {
                            b = -1;
                        }
                        jSONObject.put("ConnectionPort", b);
                        int c = iVar.c();
                        if (c < 0 || c > 65535) {
                            c = -1;
                        }
                        jSONObject.put("ConnectionSecurePort", c);
                        jSONArray2.put(jSONObject);
                    }
                    jSONArray.put(jSONArray2);
                    i = i2 + 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jSONArray.toString();
        }

        public Collection<String> a(boolean z) {
            HashSet hashSet = new HashSet();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= 2) {
                    return hashSet;
                }
                Iterator<i> it = a(i2).iterator();
                while (it.hasNext()) {
                    String a = it.next().a(Boolean.valueOf(z));
                    if (a != null) {
                        hashSet.add(a);
                    }
                }
                i = i2 + 1;
            }
        }

        public List<i> a(int i) {
            return (List) this.b[i];
        }

        public void a(i iVar, int i) {
            a(i).add(iVar);
        }

        public void a(a aVar, int i) {
            a(i).clear();
            a(i).addAll(aVar.a(i));
        }

        public void a(String str) {
            int i;
            int i2;
            m.d("ServerInfo", "ConnectionsFromJSON: " + str);
            try {
                JSONArray jSONArray = new JSONArray(str);
                for (int i3 = 0; i3 < 2; i3++) {
                    List<i> a = a(i3);
                    a.clear();
                    JSONArray jSONArray2 = jSONArray.getJSONArray(i3);
                    for (int i4 = 0; i4 < jSONArray2.length(); i4++) {
                        JSONObject jSONObject = jSONArray2.getJSONObject(i4);
                        String string = jSONObject.getString("ConnectionHost");
                        try {
                            i = jSONObject.getInt("ConnectionPort");
                        } catch (JSONException e) {
                            i = -1;
                        }
                        try {
                            i2 = jSONObject.getInt("ConnectionSecurePort");
                        } catch (JSONException e2) {
                            i2 = -1;
                        }
                        a.add(new i(string, i, i2));
                    }
                }
            } catch (JSONException e3) {
                e3.printStackTrace();
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof a) {
                return a().equals(((a) obj).a());
            }
            return false;
        }
    }

    public s(long j, String str, String str2, String str3, String str4, int i, String str5, int i2, String str6, String str7, String str8, String str9, int i3, String str10) {
        this.j = new a();
        this.a = j;
        this.b = str;
        this.c = str2;
        this.d = str3;
        this.e = str4;
        this.f = i;
        this.g = str5.toLowerCase();
        this.h = i2;
        this.i = str6;
        this.o = str10;
        if (str7 != null) {
            this.j.a(str7);
        }
        this.l = str8;
        this.m = str9;
        this.n = i3;
    }

    public s(String str, String str2, String str3, List<String> list) {
        this.j = new a();
        this.b = str2;
        this.c = str3;
        this.f = 68;
        this.a = 0L;
        this.i = str;
        this.d = "";
        this.e = "";
        this.g = "";
        c(a(list));
        this.l = "";
        this.m = "";
        this.o = "";
    }

    public static ArrayList<i> a(String str) {
        SSLContext sSLContext;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(str + "/Configuration/DeviceConfiguration/");
            URLConnection openConnection = url.openConnection();
            if (url.getProtocol().equalsIgnoreCase("https")) {
                ((HttpsURLConnection) openConnection).setHostnameVerifier(new AllowAllHostnameVerifier());
                try {
                    SSLContext sSLContext2 = SSLContext.getInstance("TLS");
                    sSLContext2.init(null, new TrustManager[]{new X509TrustManager() { // from class: com.milestonesys.mobile.s.1
                        @Override // javax.net.ssl.X509TrustManager
                        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str2) {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str2) {
                        }

                        @Override // javax.net.ssl.X509TrustManager
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }}, null);
                    sSLContext = sSLContext2;
                } catch (Exception e) {
                    sSLContext = null;
                }
                if (sSLContext != null) {
                    ((HttpsURLConnection) openConnection).setSSLSocketFactory(sSLContext.getSocketFactory());
                }
            }
            openConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
            }
            bufferedReader.close();
            try {
                JSONObject jSONObject = new JSONObject(sb.toString());
                m.c("ServerInfo", jSONObject.toString());
                m.c("ServerInfo", "Server ID: " + jSONObject.getString("ServerId"));
                JSONArray jSONArray = jSONObject.getJSONArray("ConnectionMethods");
                int length = jSONArray.length();
                ArrayList arrayList = new ArrayList(length);
                for (int i = 0; i < length; i++) {
                    arrayList.add(jSONArray.getJSONObject(i).getString("ConnectionString"));
                }
                m.c("ServerInfo", "Available connections: " + arrayList.toString());
                return a(arrayList);
            } catch (JSONException e2) {
                e2.printStackTrace();
                return null;
            }
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
            return null;
        } catch (IOException e4) {
            e4.printStackTrace();
            return null;
        }
    }

    public static ArrayList<i> a(List<String> list) {
        boolean z;
        ArrayList<i> arrayList = new ArrayList<>();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            try {
                URL url = new URL(it.next());
                String host = url.getHost();
                int port = url.getPort();
                boolean z2 = url.getProtocol().compareToIgnoreCase("https") == 0;
                Iterator<i> it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    i next = it2.next();
                    if (next.a().compareToIgnoreCase(host) == 0) {
                        if (z2) {
                            if (next.c() < 0 || next.c() > 65535) {
                                next.b(port);
                                z = true;
                                break;
                            }
                        } else if (next.b() < 0 || next.b() > 65535) {
                            next.a(port);
                            z = true;
                            break;
                        }
                    }
                }
                z = false;
                if (!z) {
                    arrayList.add(new i(host, z2 ? -1 : port, z2 ? port : -1));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public String A() {
        return c() + ".logo.png";
    }

    public String B() {
        return this.i;
    }

    public String C() {
        return this.o;
    }

    public String D() {
        return this.j.a();
    }

    public Collection<String> E() {
        return this.j.a(b(16));
    }

    public List<i> F() {
        return new ArrayList(this.j.a(1));
    }

    public List<i> G() {
        return new ArrayList(this.j.a(0));
    }

    public e H() {
        return new e(this.l);
    }

    public String I() {
        return this.l;
    }

    public com.milestonesys.mobile.a J() {
        return new com.milestonesys.mobile.a(this.m);
    }

    public String K() {
        return this.m;
    }

    public String a() {
        return this.b;
    }

    public void a(int i, i iVar) {
        List<i> a2 = this.j.a(1);
        a2.remove(i);
        a2.add(i, iVar);
    }

    public void a(int i, boolean z) {
        if (z) {
            this.n |= i;
        } else {
            this.n &= i ^ (-1);
        }
    }

    public void a(com.milestonesys.mobile.a aVar) {
        String b = aVar.b();
        m.d("ServerInfo", "Storing AC filters: " + b);
        this.m = b;
    }

    public void a(e eVar) {
        String a2 = eVar.a();
        m.d("ServerInfo", "Storing alarm filters: " + a2);
        this.l = a2;
    }

    public void a(i iVar) {
        this.j.a(iVar, 1);
    }

    public void a(String str, String str2, boolean z) {
        this.d = str;
        this.e = str2;
        if (z) {
            this.f |= 1;
            this.f &= -32769;
        } else {
            this.f &= -2;
            this.f |= 32768;
        }
    }

    public boolean a(int i) {
        return (this.n & i) != 0;
    }

    public boolean a(s sVar) {
        return this.j.equals(sVar.j) && this.d.equals(sVar.d) && this.e.equals(sVar.e) && this.f == sVar.f && this.g.equalsIgnoreCase(sVar.g) && this.i.equals(sVar.i);
    }

    public String b() {
        return this.c;
    }

    public void b(int i, boolean z) {
        if (z) {
            this.f |= i;
        } else {
            this.f &= i ^ (-1);
        }
    }

    public void b(s sVar) {
        this.j.a(sVar.j, 0);
    }

    public void b(String str) {
        try {
            this.k = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void b(List<String> list) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(',');
        }
        this.g = sb.toString().toLowerCase();
    }

    public boolean b(int i) {
        return (this.f & i) != 0;
    }

    public long c() {
        return this.a;
    }

    public void c(int i) {
        this.h = i;
    }

    public void c(List<i> list) {
        List<i> a2 = this.j.a(0);
        a2.clear();
        a2.addAll(list);
    }

    public boolean c(String str) {
        return !this.g.contains(str.toLowerCase());
    }

    public String d() {
        if (this.k != null) {
            return this.k.toExternalForm();
        }
        Collection<String> E = E();
        return E.size() > 0 ? E.iterator().next() : "";
    }

    public void d(int i) {
        this.j.a(1).remove(i);
    }

    public void d(String str) {
        this.c = str;
    }

    public String e() {
        return this.k != null ? this.k.getHost() : "";
    }

    public void e(String str) {
        this.i = str;
    }

    public boolean equals(Object obj) {
        if (obj instanceof s) {
            s sVar = (s) obj;
            if (this.a == sVar.a && this.b.equals(sVar.b) && this.c.equals(sVar.c) && this.j.equals(((s) obj).j) && this.d.equals(sVar.d) && this.e.equals(sVar.e) && this.f == sVar.f && this.g.equalsIgnoreCase(sVar.g) && this.h == sVar.h && this.i.equals(sVar.i) && this.o.equals(sVar.C())) {
                return true;
            }
        }
        return false;
    }

    public int f() {
        if (this.k != null) {
            return this.k.getPort();
        }
        return -1;
    }

    public void f(String str) {
        this.o = str;
    }

    public String g() {
        return this.d;
    }

    public String h() {
        return this.e;
    }

    public int i() {
        return this.f;
    }

    public String j() {
        return this.g;
    }

    public boolean k() {
        if (this.b.length() == 0) {
            return false;
        }
        if (b(1) && (this.d.length() == 0 || this.e.length() == 0)) {
            return false;
        }
        return (F().size() == 0 && G().size() == 0) ? false : true;
    }

    public int l() {
        return this.n;
    }

    public boolean m() {
        return !MainApplication.a && b(64);
    }

    public boolean n() {
        return !a(1);
    }

    public boolean o() {
        return b(16);
    }

    public int p() {
        return this.h;
    }

    public boolean q() {
        return b(32);
    }

    public void r() {
        this.f |= 32;
    }

    public boolean s() {
        return x();
    }

    public boolean t() {
        return (!b(1) || this.d == null || this.e == null) ? false : true;
    }

    public String toString() {
        return "Server Name: " + this.b + ", Connections: " + this.j.toString() + ", Description: " + this.c + ", UserName: " + this.d + ", Database row: " + this.a + ", flags: " + this.f + ", filters: " + this.g + ", tHashcode: " + this.h + ", hashCode: " + hashCode() + ", UDN: " + this.i + ", Recent assignee: " + this.o;
    }

    public boolean u() {
        return b(32768);
    }

    public boolean v() {
        return b(8);
    }

    public void w() {
        if (u()) {
            this.f &= -32769;
            if (t()) {
                return;
            }
            this.d = null;
            this.e = null;
        }
    }

    public boolean x() {
        return (this.f & 2) == 2;
    }

    public void y() {
        this.f &= -3;
    }

    public String z() {
        return c() + ".icon.png";
    }
}
