package com.dolphin.browser.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/* loaded from: classes.dex */
public class bs {

    /* renamed from: a, reason: collision with root package name */
    public static final a f5994a = new a() { // from class: com.dolphin.browser.util.bs.1
        @Override // com.dolphin.browser.util.bs.a
        public boolean a(ZipEntry zipEntry) {
            String name;
            return (zipEntry == null || (name = zipEntry.getName()) == null || name.contains("../")) ? false : true;
        }
    };

    /* loaded from: classes2.dex */
    public interface a {
        boolean a(ZipEntry zipEntry);
    }

    public static void a(String str, ArrayList<String> arrayList, String str2) {
        ZipOutputStream zipOutputStream;
        ZipOutputStream zipOutputStream2 = null;
        try {
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(str)));
        } catch (Exception e) {
            e = e;
        } catch (Throwable th) {
            th = th;
            zipOutputStream = null;
        }
        try {
            byte[] bArr = new byte[2048];
            for (int i = 0; i < arrayList.size(); i++) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(arrayList.get(i)), 2048);
                zipOutputStream.putNextEntry(new ZipEntry(arrayList.get(i).substring(arrayList.get(i).lastIndexOf("/") + 1)));
                while (true) {
                    int read = bufferedInputStream.read(bArr, 0, 2048);
                    if (read != -1) {
                        zipOutputStream.write(bArr, 0, read);
                    }
                }
                bufferedInputStream.close();
            }
            if (str2 != null) {
                zipOutputStream.putNextEntry(new ZipEntry("info.txt"));
                zipOutputStream.write(bc.k(str2));
            }
            IOUtilities.closeStream(zipOutputStream);
        } catch (Exception e2) {
            e = e2;
            zipOutputStream2 = zipOutputStream;
            try {
                e.printStackTrace();
                IOUtilities.closeStream(zipOutputStream2);
            } catch (Throwable th2) {
                th = th2;
                zipOutputStream = zipOutputStream2;
                IOUtilities.closeStream(zipOutputStream);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            IOUtilities.closeStream(zipOutputStream);
            throw th;
        }
    }

    public static void a(File file, File file2) throws IOException {
        ZipFile zipFile;
        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream = null;
        if (file2 == null) {
            return;
        }
        try {
            zipFile = new ZipFile(file);
            try {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                BufferedInputStream bufferedInputStream2 = null;
                while (entries.hasMoreElements()) {
                    try {
                        ZipEntry nextElement = entries.nextElement();
                        File file3 = new File(file2, nextElement.getName());
                        if (nextElement.isDirectory()) {
                            IOUtilities.ensureDir(file3);
                        } else {
                            BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(new FileOutputStream(file3));
                            try {
                                byte[] bArr = new byte[2048];
                                BufferedInputStream bufferedInputStream3 = new BufferedInputStream(zipFile.getInputStream(nextElement));
                                while (true) {
                                    try {
                                        int read = bufferedInputStream3.read(bArr);
                                        if (read <= 0) {
                                            break;
                                        } else {
                                            bufferedOutputStream2.write(bArr, 0, read);
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        bufferedInputStream = bufferedInputStream3;
                                        bufferedOutputStream = bufferedOutputStream2;
                                        if (zipFile != null) {
                                            zipFile.close();
                                        }
                                        IOUtilities.closeStream(bufferedInputStream);
                                        IOUtilities.closeStream(bufferedOutputStream);
                                        throw th;
                                    }
                                }
                                bufferedInputStream2 = bufferedInputStream3;
                                bufferedOutputStream = bufferedOutputStream2;
                            } catch (Throwable th2) {
                                th = th2;
                                bufferedOutputStream = bufferedOutputStream2;
                                bufferedInputStream = bufferedInputStream2;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        bufferedInputStream = bufferedInputStream2;
                    }
                }
                if (zipFile != null) {
                    zipFile.close();
                }
                IOUtilities.closeStream(bufferedInputStream2);
                IOUtilities.closeStream(bufferedOutputStream);
            } catch (Throwable th4) {
                th = th4;
                bufferedInputStream = null;
            }
        } catch (Throwable th5) {
            th = th5;
            zipFile = null;
            bufferedInputStream = null;
        }
    }

    public static boolean a(File file, b bVar) {
        ZipFile zipFile;
        ZipFile zipFile2;
        ZipFile zipFile3;
        boolean z;
        if (file == null) {
            return false;
        }
        boolean z2 = true;
        try {
            try {
                zipFile2 = new ZipFile(file);
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile2.entries();
                    while (true) {
                        if (!entries.hasMoreElements()) {
                            z = true;
                            break;
                        }
                        if (!bVar.a(entries.nextElement())) {
                            z = false;
                            break;
                        }
                    }
                    a(zipFile2);
                    z2 = z;
                } catch (ZipException e) {
                    e = e;
                    zipFile3 = zipFile2;
                    try {
                        e.printStackTrace();
                        a(zipFile3);
                        return z2;
                    } catch (Throwable th) {
                        th = th;
                        zipFile = zipFile3;
                        a(zipFile);
                        throw th;
                    }
                } catch (IOException e2) {
                    e = e2;
                    e.printStackTrace();
                    a(zipFile2);
                    return z2;
                }
            } catch (Throwable th2) {
                th = th2;
                a(zipFile);
                throw th;
            }
        } catch (ZipException e3) {
            e = e3;
            zipFile3 = null;
        } catch (IOException e4) {
            e = e4;
            zipFile2 = null;
        } catch (Throwable th3) {
            th = th3;
            zipFile = null;
            a(zipFile);
            throw th;
        }
        return z2;
    }

    private static void a(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class b {

        /* renamed from: a, reason: collision with root package name */
        Set<a> f5995a = new HashSet();

        boolean a(ZipEntry zipEntry) {
            Iterator<a> it = this.f5995a.iterator();
            while (it.hasNext()) {
                if (!it.next().a(zipEntry)) {
                    return false;
                }
            }
            return true;
        }

        public void a(a aVar) {
            this.f5995a.add(aVar);
        }
    }
}
