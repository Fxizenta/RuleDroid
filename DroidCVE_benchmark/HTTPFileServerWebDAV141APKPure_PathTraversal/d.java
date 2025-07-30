package f.a;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import b.h.b.g;
import b.h.b.i;
import b.h.b.m;
import b.o.j;
import d.a.a.a;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.net.ssl.KeyManagerFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import slowscript.httpfileserver.ServerService;

/* compiled from: SimpleServer.java */
/* loaded from: classes.dex */
public class d extends d.a.a.a {
    public static String l = "/storage/emulated/0/";
    public static String m = "127.0.0.1";
    public static int n = 8080;
    public static boolean o = false;
    public static boolean p = false;
    public static boolean s;
    public boolean A;
    public String B;
    public boolean C;
    public boolean D;
    public boolean E;
    public boolean F;
    public final ServerService w;
    public SharedPreferences x;
    public ArrayList<String> y;
    public boolean z;
    public static ArrayList<String> q = new ArrayList<>();
    public static ArrayList<String> r = new ArrayList<>();
    public static ArrayList<Uri> t = new ArrayList<>();
    public static final String[] u = {"bz", "bz2", "gz", "rar", "tar", "zip", "7z", "deb", "rpm", "iso", "jar", "apk", "tgz", "pkg", "ar"};
    public static final String[] v = {"doc", "docx", "odt", "rtf", "pdf"};

    /* compiled from: SimpleServer.java */
    public class a implements Runnable {

        /* renamed from: b, reason: collision with root package name */
        public final byte[] f3340b = new byte[1024];

        /* renamed from: c, reason: collision with root package name */
        public final /* synthetic */ String[] f3341c;

        /* renamed from: d, reason: collision with root package name */
        public final /* synthetic */ String f3342d;

        /* renamed from: e, reason: collision with root package name */
        public final /* synthetic */ ZipOutputStream f3343e;

        public a(d dVar, String[] strArr, String str, ZipOutputStream zipOutputStream) {
            this.f3341c = strArr;
            this.f3342d = str;
            this.f3343e = zipOutputStream;
        }

        public void a(String str, File file) {
            File[] listFiles = file.listFiles();
            for (File file2 : listFiles) {
                StringBuilder d2 = c.a.a.a.a.d(str, "/");
                d2.append(file2.getName());
                String sb = d2.toString();
                if (file2.isDirectory()) {
                    a(sb, file2);
                } else {
                    b(sb, file2);
                }
            }
            if (listFiles.length == 0) {
                this.f3343e.putNextEntry(new ZipEntry(c.a.a.a.a.l(str, "/")));
            }
        }

        public void b(String str, File file) {
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(str);
            zipEntry.setTime(file.lastModified());
            this.f3343e.putNextEntry(zipEntry);
            while (true) {
                int read = fileInputStream.read(this.f3340b);
                if (read <= 0) {
                    this.f3343e.closeEntry();
                    fileInputStream.close();
                    return;
                }
                this.f3343e.write(this.f3340b, 0, read);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                for (String str : this.f3341c) {
                    File file = new File(d.l + this.f3342d, str);
                    if (file.isDirectory()) {
                        a(str, file);
                    } else {
                        b(str, file);
                    }
                }
                this.f3343e.close();
            } catch (IOException e2) {
                StringBuilder c2 = c.a.a.a.a.c("Failed to create zip. Ex: ");
                c2.append(e2.getMessage());
                Log.e("HTTP", c2.toString(), e2);
            }
        }
    }

    public d(ServerService serverService) {
        super(m, n);
        this.y = new ArrayList<>();
        this.w = serverService;
        SharedPreferences a2 = j.a(serverService);
        this.x = a2;
        s = a2.getBoolean("https", false);
        q();
        if (s) {
            try {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                KeyStore g = f.g(m);
                keyManagerFactory.init(g, null);
                this.h = new a.p(d.a.a.a.c(g, keyManagerFactory), null);
            } catch (Exception e2) {
                ServerService serverService2 = this.w;
                StringBuilder c2 = c.a.a.a.a.c("Failed to initialize HTTPS: ");
                c2.append(e2.getLocalizedMessage());
                c2.append("\nRunning in unencrypted HTTP instead.");
                Toast.makeText(serverService2, c2.toString(), 1).show();
                Log.w("HTTP", "Failed to initialize HTTPS server", e2);
            }
        }
        this.x.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: f.a.b
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
                d dVar = d.this;
                dVar.getClass();
                if (str.equals("https")) {
                    Toast.makeText(dVar.w, "Enabling or disabling HTTPS requires the server to be restarted", 1).show();
                } else {
                    dVar.q();
                }
            }
        });
    }

    @Override // d.a.a.a
    public a.n h(a.m mVar) {
        a.n.d dVar;
        a.n.d dVar2;
        a.n.d dVar3;
        String str;
        a.n.d dVar4;
        boolean z;
        String str2;
        String str3;
        Uri uri;
        a.n.d dVar5 = a.n.d.CREATED;
        a.n.d dVar6 = a.n.d.CONFLICT;
        a.n.d dVar7 = a.n.d.UNAUTHORIZED;
        a.n.d dVar8 = a.n.d.REDIRECT;
        a.n.d dVar9 = a.n.d.OK;
        a.n.d dVar10 = a.n.d.INTERNAL_ERROR;
        a.n.d dVar11 = a.n.d.FORBIDDEN;
        a.n.d dVar12 = a.n.d.NOT_FOUND;
        a.l lVar = (a.l) mVar;
        Map<String, String> map = lVar.i;
        String trim = lVar.f2696f.trim();
        String str4 = lVar.l;
        boolean z2 = false;
        if (this.z && !q.contains(str4)) {
            if (r.contains(str4)) {
                return d.a.a.a.e(dVar11, "text/plain", "Connection denied by server");
            }
            ServerService serverService = this.w;
            serverService.getClass();
            if (!ServerService.f3356d) {
                Intent intent = new Intent(serverService, (Class<?>) ServerService.ConfirmConnectionReceiver.class);
                intent.setAction("action_confirm");
                intent.putExtra("ip", str4);
                PendingIntent broadcast = PendingIntent.getBroadcast(serverService, 0, intent, 134217728);
                Intent intent2 = new Intent(serverService, (Class<?>) ServerService.ConfirmConnectionReceiver.class);
                intent2.setAction("action_deny");
                intent2.putExtra("ip", str4);
                PendingIntent broadcast2 = PendingIntent.getBroadcast(serverService, 0, intent2, 134217728);
                b.h.b.j jVar = serverService.h;
                String format = String.format("Allow connection from %s?", str4);
                jVar.getClass();
                jVar.f1161f = b.h.b.j.b(format);
                jVar.f1157b.clear();
                jVar.f1157b.add(new i(0, "Confirm", broadcast));
                jVar.f1157b.add(new i(0, "Deny", broadcast2));
                m mVar2 = serverService.i;
                Notification a2 = serverService.h.a();
                mVar2.getClass();
                Bundle z3 = g.z(a2);
                if (z3 != null && z3.getBoolean("android.support.useSideChannel")) {
                    z2 = true;
                }
                if (z2) {
                    mVar2.b(new m.b(mVar2.f1174f.getPackageName(), 10, null, a2));
                    mVar2.g.cancel(null, 10);
                } else {
                    mVar2.g.notify(null, 10, a2);
                }
                ServerService.f3356d = true;
            }
            return d.a.a.a.e(dVar9, "text/plain", "Please wait for confirmation and try again");
        }
        if (this.A) {
            dVar = dVar5;
            dVar2 = dVar6;
            if (!map.containsKey("authorization")) {
                a.n e2 = d.a.a.a.e(dVar7, "text/plain", null);
                e2.f2701f.put("WWW-Authenticate", "Basic realm=\"File Server\"");
                return e2;
            }
            dVar3 = dVar11;
            if (!new String(Base64.decode(map.get("authorization").split(" ")[r12.length - 1], 0)).split(":")[1].equals(this.B)) {
                a.n e3 = d.a.a.a.e(dVar7, "text/plain", "Wrong credentials");
                e3.f2701f.put("WWW-Authenticate", "Basic realm=\"File Server\"");
                return e3;
            }
        } else {
            dVar = dVar5;
            dVar2 = dVar6;
            dVar3 = dVar11;
        }
        Log.v("HTTP", b.f.b.g.d(lVar.g) + " " + trim);
        String str5 = "";
        String str6 = "Failed to generate page";
        if (o) {
            if (trim.equals("/") || trim.startsWith("/files")) {
                a.n e4 = d.a.a.a.e(dVar8, "text/html", "Redirecting");
                e4.f2701f.put("Location", "/share/");
                return e4;
            }
            if (!trim.startsWith("/share")) {
                return trim.startsWith("/data") ? u(trim, map) : d.a.a.a.e(dVar12, "text/plain", "Not found");
            }
            if (trim.length() > 7) {
                String substring = trim.substring(7);
                Iterator<Uri> it = t.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        uri = null;
                        break;
                    }
                    uri = it.next();
                    if (uri != null && f.e(this.w, uri).equals(substring)) {
                        break;
                    }
                }
                if (uri == null) {
                    return d.a.a.a.e(dVar12, "text/plain", "File not found");
                }
                try {
                    return v(this.w.getContentResolver().openInputStream(uri), trim, 0L, this.w.getContentResolver().getType(uri), map);
                } catch (Exception e5) {
                    Log.e("HTTP", "Could not open URI.", e5);
                    return d.a.a.a.e(dVar10, "text/plain", "Could not open URI");
                }
            }
            StringBuilder sb = new StringBuilder();
            Iterator<Uri> it2 = t.iterator();
            while (it2.hasNext()) {
                Uri next = it2.next();
                if (next != null) {
                    String e6 = f.e(this.w, next);
                    sb.append("<div class=\"item\"><a href=\"./" + URLEncoder.encode(e6).replace("+", "%20") + "\"><img src=\"" + p(e6) + "\" /><span>" + TextUtils.htmlEncode(e6) + "</span></a></div>\n");
                    it2 = it2;
                }
            }
            try {
                Scanner useDelimiter = new Scanner(this.w.getAssets().open("data/share.html")).useDelimiter("\\A");
                str6 = (useDelimiter.hasNext() ? useDelimiter.next() : str5).replace("{0}", sb);
            } catch (Exception unused) {
            }
            return d.a.a.a.e(dVar9, "text/html", str6);
        }
        if (p) {
            String str7 = l + lVar.f2696f;
            File file = new File(str7);
            try {
                try {
                    str3 = "Not found";
                } catch (SecurityException e7) {
                    return d.a.a.a.e(dVar3, "text/plain", e7.getLocalizedMessage());
                }
            } catch (FileNotFoundException unused2) {
                str3 = "Not found";
            }
            try {
                return v(new FileInputStream(file), str7, file.lastModified(), URLConnection.guessContentTypeFromName(str7), map);
            } catch (FileNotFoundException unused3) {
                return d.a.a.a.e(dVar12, "text/plain", str3);
            }
        }
        a.n.d dVar13 = dVar3;
        if (trim.equals("/") || trim.startsWith("/share")) {
            a.n e8 = d.a.a.a.e(dVar8, "text/plain", "Redirecting");
            e8.f2701f.put("Location", "/files/");
            return e8;
        }
        int i = lVar.g;
        if (i == 1) {
            if (!trim.startsWith("/files")) {
                trim.startsWith("/preview");
                if (trim.startsWith("/data")) {
                    return u(trim, map);
                }
                if (trim.startsWith("/delete")) {
                    return t(mVar);
                }
                if (trim.startsWith("/zip")) {
                    return w(mVar);
                }
                return d.a.a.a.e(dVar12, "text/plain", "The requested resource does not exist \"" + trim + "\"");
            }
            String substring2 = trim.substring(6);
            String n2 = c.a.a.a.a.n(new StringBuilder(), l, substring2);
            Log.d("HTTP", "Requested " + n2);
            File file2 = new File(n2);
            if (!file2.isDirectory()) {
                if (file2.exists()) {
                    try {
                        return v(new FileInputStream(file2), file2.getAbsolutePath(), file2.lastModified(), URLConnection.guessContentTypeFromName(n2), map);
                    } catch (FileNotFoundException unused4) {
                    }
                }
                return d.a.a.a.e(dVar12, "text/plain", "The requested resource does not exist on this device");
            }
            if (!trim.endsWith("/")) {
                a.n e9 = d.a.a.a.e(dVar8, "text/html", "Redirecting");
                e9.f2701f.put("Location", c.a.a.a.a.l(trim, "/"));
                return e9;
            }
            File[] listFiles = file2.listFiles();
            if (listFiles == null) {
                str6 = "Cannot access specified directory";
            } else {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (File file3 : listFiles) {
                    String name = file3.getName();
                    if (file3.isDirectory()) {
                        arrayList2.add(name);
                    } else {
                        arrayList.add(name);
                    }
                }
                Collections.sort(arrayList);
                Collections.sort(arrayList2);
                StringBuilder sb2 = new StringBuilder();
                Iterator it3 = arrayList2.iterator();
                while (it3.hasNext()) {
                    String str8 = (String) it3.next();
                    sb2.append("<div class=\"item\"><input name=\"checked\" type=\"checkbox\"><a href=\"./" + URLEncoder.encode(str8).replace("+", "%20") + "/\"><img src=\"/data/folder.png\" /><span>" + TextUtils.htmlEncode(str8) + "</span></a></div>\n");
                }
                Iterator it4 = arrayList.iterator();
                while (it4.hasNext()) {
                    String str9 = (String) it4.next();
                    sb2.append("<div class=\"item\"><input name=\"checked\" type=\"checkbox\"><a href=\"./" + URLEncoder.encode(str9).replace("+", "%20") + "\"><img src=\"" + p(substring2 + str9) + "\" /><span>" + TextUtils.htmlEncode(str9) + "</span></a></div>\n");
                }
                String[] split = substring2.split("/");
                int length = split.length;
                String str10 = "<a href=\"/files/\">> Home</a>";
                String str11 = "/files";
                int i2 = 0;
                while (i2 < length) {
                    String str12 = split[i2];
                    String str13 = str5;
                    if (!str12.equals(str13)) {
                        str11 = c.a.a.a.a.m(str11, "/", str12);
                        str10 = str10 + "<a href=\"" + str11 + "/\">> " + TextUtils.htmlEncode(str12) + "</a>";
                    }
                    i2++;
                    str5 = str13;
                }
                String str14 = str5;
                String str15 = split.length > 0 ? split[split.length - 1] : "Home";
                try {
                    Scanner useDelimiter2 = new Scanner(this.w.getAssets().open("data/index.html")).useDelimiter("\\A");
                    str6 = (useDelimiter2.hasNext() ? useDelimiter2.next() : str14).replace("{1}", str10).replace("{3}", str14).replace("{2}", str15).replace("{0}", sb2);
                } catch (Exception unused5) {
                }
            }
            return d.a.a.a.f(str6);
        }
        String str16 = "text/plain";
        if (i == 2) {
            if (!this.D) {
                return d.a.a.a.e(dVar13, str16, "Uploading not allowed");
            }
            try {
                String str17 = l + URLDecoder.decode(((a.l) mVar).f2696f.substring(6));
                Map<String, String> map2 = ((a.l) mVar).i;
                Iterator<String> it5 = map2.keySet().iterator();
                while (it5.hasNext()) {
                    if (it5.next().equals("content-range")) {
                        return d.a.a.a.e(a.n.d.NOT_IMPLEMENTED, str16, "Content-Range headers are not implemented");
                    }
                }
                String str18 = map2.get("x-last-modified");
                if (str18 == null) {
                    str18 = map2.get("x-oc-mtime");
                }
                File file4 = new File(str17);
                if (!file4.getParentFile().isDirectory()) {
                    return d.a.a.a.e(dVar2, str16, "Parent directory does not exist");
                }
                Log.d("HTTP", "Saving file " + str17);
                if (file4.exists() && !this.E) {
                    Log.i("HTTP", "Did not store file. Overwriting not allowed.");
                    return d.a.a.a.e(dVar13, str16, "Overwriting not allowed.");
                }
                long f2 = ((a.l) mVar).f();
                long j = 0;
                byte[] bArr = new byte[4096];
                BufferedInputStream bufferedInputStream = ((a.l) mVar).f2693c;
                FileOutputStream fileOutputStream = new FileOutputStream(file4);
                while (j < f2) {
                    int read = bufferedInputStream.read(bArr, 0, (int) Math.min(f2, 4096L));
                    j += read;
                    fileOutputStream.write(bArr, 0, read);
                }
                fileOutputStream.close();
                if (str18 != null) {
                    file4.setLastModified(Long.parseLong(str18));
                }
                Log.d("HTTP", "Saved successfully");
                return d.a.a.a.e(dVar, str16, "File saved");
            } catch (Throwable th) {
                Log.e("HTTP", "Error storing file", th);
                return d.a.a.a.e(dVar10, str16, "Error storing file: " + th.getMessage());
            }
        }
        a.n.d dVar14 = dVar;
        a.n.d dVar15 = dVar2;
        a.n.d dVar16 = dVar13;
        if (i == 10) {
            String substring3 = lVar.f2696f.substring(6);
            Map<String, String> map3 = lVar.i;
            String n3 = c.a.a.a.a.n(new StringBuilder(), l, substring3);
            Log.d("HTTP", "Propfind on " + n3 + ", depth = " + map3.get("depth"));
            try {
                str2 = "HTTP";
                try {
                    ((a.l) mVar).f2693c.skip(((a.l) mVar).f());
                } catch (Exception unused6) {
                }
            } catch (Exception unused7) {
                str2 = "HTTP";
            }
            try {
                Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                int parseInt = map3.containsKey("depth") ? Integer.parseInt(map3.get("depth")) : 1;
                Element createElement = newDocument.createElement("D:multistatus");
                createElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:D", "DAV:");
                newDocument.appendChild(createElement);
                File file5 = new File(n3);
                if (file5.isDirectory()) {
                    File[] listFiles2 = file5.listFiles();
                    createElement.appendChild(s(newDocument, ((a.l) mVar).f2696f, file5));
                    if (parseInt > 0) {
                        for (File file6 : listFiles2) {
                            createElement.appendChild(s(newDocument, ((a.l) mVar).f2696f + "/" + file6.getName(), file6));
                        }
                    }
                } else {
                    if (!file5.exists()) {
                        return d.a.a.a.e(dVar12, str16, "Not found");
                    }
                    createElement.appendChild(s(newDocument, ((a.l) mVar).f2696f, file5));
                }
                return d.a.a.a.e(a.n.d.MULTI_STATUS, "application/xml", m(newDocument));
            } catch (Exception e10) {
                Log.e(str2, "Propfind error", e10);
                return d.a.a.a.e(dVar10, str16, "Internal error: " + e10.getMessage());
            }
        }
        if (i != 11) {
            if (i != 13) {
                if (i == 12) {
                    return r(mVar);
                }
                if (i == 4) {
                    return t(mVar);
                }
                if (i != 6) {
                    try {
                        ((a.l) mVar).f2693c.skip(((a.l) mVar).f());
                    } catch (Exception unused8) {
                    }
                    return d.a.a.a.e(a.n.d.METHOD_NOT_ALLOWED, str16, "Unsupported method");
                }
                a.n e11 = d.a.a.a.e(dVar9, null, null);
                e11.f2701f.put("DAV", "1");
                e11.f2701f.put("Allow", "GET, PUT, PROPFIND, PROPPATCH, MKCOL, OPTIONS, MOVE, DELETE");
                return e11;
            }
            Map<String, String> map4 = lVar.i;
            String str19 = l + lVar.f2696f.substring(6);
            String replaceAll = map4.get("destination").replaceAll("https?://", str5);
            String str20 = l + URLDecoder.decode(replaceAll.substring(replaceAll.indexOf(47)).substring(6));
            File file7 = new File(str19);
            File file8 = new File(str20);
            StringBuilder c2 = c.a.a.a.a.c("Move to ");
            c2.append(file8.getAbsolutePath());
            Log.d("HTTP", c2.toString());
            boolean z4 = this.E && "T".equals(map4.get("overwrite"));
            if (!file7.exists()) {
                return d.a.a.a.e(dVar12, str16, "Not found");
            }
            if (!file8.exists()) {
                z = false;
            } else {
                if (!z4) {
                    return d.a.a.a.e(a.n.d.PRECONDITION_FAILED, str16, "Destination exists but overwriting is not allowed");
                }
                file8.delete();
                z = true;
            }
            File parentFile = file8.getParentFile();
            return (parentFile == null || !parentFile.exists()) ? d.a.a.a.e(dVar15, str16, "Intermediate collection does not exist") : file7.renameTo(file8) ? z ? d.a.a.a.e(a.n.d.NO_CONTENT, str16, "OK") : d.a.a.a.e(dVar14, str16, "OK") : d.a.a.a.e(dVar10, str16, "Move failed");
        }
        try {
            File file9 = new File(l + URLDecoder.decode(((a.l) mVar).f2696f.substring(6)));
            BufferedInputStream bufferedInputStream2 = ((a.l) mVar).f2693c;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            long f3 = ((a.l) mVar).f();
            long j2 = 0;
            byte[] bArr2 = new byte[4096];
            while (j2 < f3) {
                str = str16;
                dVar4 = dVar16;
                try {
                    int read2 = bufferedInputStream2.read(bArr2, 0, (int) Math.min(f3, 4096L));
                    j2 += read2;
                    byteArrayOutputStream.write(bArr2, 0, read2);
                    f3 = f3;
                    dVar16 = dVar4;
                    str16 = str;
                } catch (Exception e12) {
                    e = e12;
                    Log.e("HTTP", "PROPPATCH failed", e);
                    return d.a.a.a.e(dVar4, str, e.getMessage());
                }
            }
            str = str16;
            dVar4 = dVar16;
            String byteArrayOutputStream2 = byteArrayOutputStream.toString();
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            NodeList elementsByTagName = newDocumentBuilder.parse(new InputSource(new StringReader(byteArrayOutputStream2))).getElementsByTagName("getlastmodified");
            Document newDocument2 = newDocumentBuilder.newDocument();
            Element createElement2 = newDocument2.createElement("D:multistatus");
            createElement2.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:D", "DAV:");
            newDocument2.appendChild(createElement2);
            Element createElement3 = newDocument2.createElement("D:response");
            createElement2.appendChild(createElement3);
            Element createElement4 = newDocument2.createElement("D:href");
            createElement4.setTextContent(f.b(((a.l) mVar).f2696f));
            createElement3.appendChild(createElement4);
            Element createElement5 = newDocument2.createElement("D:propstat");
            createElement3.appendChild(createElement5);
            if (elementsByTagName.getLength() > 0) {
                String textContent = elementsByTagName.item(0).getTextContent();
                Element createElement6 = newDocument2.createElement("D:prop");
                createElement5.appendChild(createElement6);
                createElement6.appendChild(newDocument2.createElement("D:getlastmodified"));
                Element createElement7 = newDocument2.createElement("D:status");
                if (file9.setLastModified(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz", Locale.US).parse(textContent).getTime())) {
                    createElement7.setTextContent("HTTP/1.1 200 OK");
                } else {
                    createElement7.setTextContent("HTTP/1.1 403 Forbidden");
                }
                createElement5.appendChild(createElement7);
            }
            return d.a.a.a.e(a.n.d.MULTI_STATUS, "application/xml", m(newDocument2));
        } catch (Exception e13) {
            e = e13;
            str = str16;
            dVar4 = dVar16;
        }
    }

    public void l(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    l(file2);
                } else {
                    file2.delete();
                }
            }
        }
        file.delete();
    }

    public final String m(Document document) {
        Transformer newTransformer = TransformerFactory.newInstance().newTransformer();
        newTransformer.setOutputProperty("indent", "yes");
        DOMSource dOMSource = new DOMSource(document);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newTransformer.transform(dOMSource, new StreamResult(byteArrayOutputStream));
        return byteArrayOutputStream.toString();
    }

    public boolean n(String str, String[] strArr) {
        for (String str2 : strArr) {
            if (str.equalsIgnoreCase(str2)) {
                return true;
            }
        }
        return false;
    }

    public final String o(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(Long.valueOf(j));
    }

    public final String p(String str) {
        String guessContentTypeFromName = URLConnection.guessContentTypeFromName(str);
        if (guessContentTypeFromName == null) {
            return "/data/icons/file.png";
        }
        if (guessContentTypeFromName.startsWith("audio")) {
            return "/data/icons/audio.png";
        }
        if (guessContentTypeFromName.startsWith("image")) {
            return "/data/icons/image.png";
        }
        if (guessContentTypeFromName.startsWith("text")) {
            return "/data/icons/text.png";
        }
        if (guessContentTypeFromName.startsWith("video")) {
            return "/data/icons/video.png";
        }
        String str2 = str.split("\\.")[r4.length - 1];
        return n(str2, u) ? "/data/icons/archive.png" : n(str2, v) ? "/data/icons/document.png" : "/data/icons/file.png";
    }

    public void q() {
        this.C = this.x.getBoolean("allowDelete", false);
        this.D = this.x.getBoolean("upload", true);
        this.E = this.x.getBoolean("overwrite", false);
        this.z = this.x.getBoolean("ipConfirmation", false);
        this.A = this.x.getBoolean("authentication", false);
        this.B = this.x.getString("password", "");
        p = this.x.getBoolean("staticMode", false);
        this.F = this.x.getBoolean("preview", true);
    }

    public final a.n r(a.m mVar) {
        String n2 = c.a.a.a.a.n(new StringBuilder(), l, ((a.l) mVar).f2696f.substring(6));
        Log.d("HTTP", "Creating directory " + n2);
        File file = new File(n2);
        try {
            if (((a.l) mVar).f2693c.available() > 0) {
                return d.a.a.a.e(a.n.d.UNSUPPORTED_MEDIA_TYPE, "text/plain", "Body not supported");
            }
        } catch (IOException unused) {
        }
        if (file.exists()) {
            return d.a.a.a.e(a.n.d.METHOD_NOT_ALLOWED, "text/plain", "A directory or a file of this name already exists.");
        }
        try {
            return file.mkdir() ? d.a.a.a.e(a.n.d.CREATED, "text/plain", "OK") : d.a.a.a.e(a.n.d.INTERNAL_ERROR, "text/plain", "Directory was not created");
        } catch (SecurityException e2) {
            a.n.d dVar = a.n.d.FORBIDDEN;
            StringBuilder c2 = c.a.a.a.a.c("Failed to create directory: ");
            c2.append(e2.getMessage());
            return d.a.a.a.e(dVar, "text/plain", c2.toString());
        }
    }

    public final Element s(Document document, String str, File file) {
        String b2 = f.b(str);
        Element createElement = document.createElement("D:response");
        Element createElement2 = document.createElement("D:href");
        createElement2.setTextContent(b2);
        createElement.appendChild(createElement2);
        Element createElement3 = document.createElement("D:propstat");
        createElement.appendChild(createElement3);
        Element createElement4 = document.createElement("D:status");
        createElement4.setTextContent("HTTP/1.1 200 OK");
        createElement3.appendChild(createElement4);
        Element createElement5 = document.createElement("D:prop");
        createElement3.appendChild(createElement5);
        Element createElement6 = document.createElement("D:displayname");
        createElement6.setTextContent(file.getName());
        createElement5.appendChild(createElement6);
        Element createElement7 = document.createElement("D:getcontentlength");
        createElement5.appendChild(createElement7);
        Element createElement8 = document.createElement("D:getlastmodified");
        createElement8.setTextContent(o(file.lastModified()));
        createElement5.appendChild(createElement8);
        Element createElement9 = document.createElement("D:resourcetype");
        createElement5.appendChild(createElement9);
        Element createElement10 = document.createElement("D:getcontenttype");
        createElement5.appendChild(createElement10);
        if (file.isDirectory()) {
            createElement9.appendChild(document.createElement("D:collection"));
        } else {
            createElement10.setTextContent(URLConnection.guessContentTypeFromName(file.getName()));
            createElement7.setTextContent(String.valueOf(file.length()));
            Element createElement11 = document.createElement("D:getetag");
            createElement11.setTextContent(Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode()));
            createElement5.appendChild(createElement11);
        }
        return createElement;
    }

    public final a.n t(a.m mVar) {
        String[] split;
        String str;
        a.n.d dVar = a.n.d.FORBIDDEN;
        if (!this.C) {
            return d.a.a.a.e(dVar, "text/plain", "Deleting is not allowed. You can change this in the settings.");
        }
        a.l lVar = (a.l) mVar;
        if (lVar.g == 4) {
            split = new String[]{lVar.f2696f.substring(6)};
            str = "";
        } else {
            String[] split2 = lVar.h.get("items").get(0).split("///");
            String str2 = split2[0];
            split = split2[1].split("/");
            str = str2;
        }
        for (String str3 : split) {
            File file = new File(c.a.a.a.a.n(new StringBuilder(), l, str), str3);
            if (!file.getAbsolutePath().startsWith(l)) {
                return d.a.a.a.e(dVar, "text/plain", "Outside root dir");
            }
            StringBuilder c2 = c.a.a.a.a.c("Deleting item: ");
            c2.append(file.getAbsolutePath());
            Log.d("HTTP", c2.toString());
            if (file.isDirectory()) {
                l(file);
            } else {
                if (!file.exists()) {
                    return d.a.a.a.e(a.n.d.NOT_FOUND, "text/plain", "Not found");
                }
                if (!file.delete()) {
                    return d.a.a.a.e(a.n.d.INTERNAL_ERROR, "text/plain", "File not deleted");
                }
            }
        }
        return d.a.a.a.f("Done");
    }

    public final a.n u(String str, Map<String, String> map) {
        try {
            if (map.get("if-none-match") != null) {
                if (this.y.contains(str)) {
                    return d.a.a.a.e(a.n.d.NOT_MODIFIED, "", "");
                }
                this.y.add(str);
            }
            InputStream open = this.w.getAssets().open(str.substring(1));
            int available = open.available();
            String guessContentTypeFromName = URLConnection.guessContentTypeFromName(str);
            if (guessContentTypeFromName == null) {
                guessContentTypeFromName = "application/octet-stream";
            }
            a.n d2 = d.a.a.a.d(a.n.d.OK, guessContentTypeFromName, open, available);
            d2.f2701f.put("ETag", Integer.toHexString(str.hashCode()));
            return d2;
        } catch (Exception e2) {
            StringBuilder c2 = c.a.a.a.a.c("Failed to send static files. Ex: ");
            c2.append(e2.getMessage());
            return d.a.a.a.f(c2.toString());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00a4 A[Catch: Exception -> 0x0143, TRY_LEAVE, TryCatch #2 {Exception -> 0x0143, blocks: (B:5:0x000f, B:7:0x0049, B:9:0x0051, B:46:0x005e, B:49:0x0067, B:19:0x009a, B:21:0x00a4, B:29:0x00c0, B:30:0x00c7, B:33:0x00dd, B:35:0x00e5, B:39:0x012d, B:56:0x0090), top: B:4:0x000f }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00b8 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final d.a.a.a.n v(java.io.InputStream r23, java.lang.String r24, long r25, java.lang.String r27, java.util.Map<java.lang.String, java.lang.String> r28) {
        /*
            Method dump skipped, instructions count: 355
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: f.a.d.v(java.io.InputStream, java.lang.String, long, java.lang.String, java.util.Map):d.a.a.a$n");
    }

    public final a.n w(a.m mVar) {
        String[] split = ((a.l) mVar).h.get("zip").get(0).split("///");
        String str = split[0];
        String[] split2 = split[1].split("/");
        PipedInputStream pipedInputStream = new PipedInputStream();
        try {
            new Thread(new a(this, split2, str, new ZipOutputStream(new PipedOutputStream(pipedInputStream)))).start();
            return new a.n(a.n.d.OK, "application/zip", pipedInputStream, -1L);
        } catch (IOException e2) {
            StringBuilder c2 = c.a.a.a.a.c("Failed to send zip. Ex: ");
            c2.append(e2.getMessage());
            return d.a.a.a.f(c2.toString());
        }
    }
}
