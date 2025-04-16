package com.quoord.tapatalkpro.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.quoord.tapatalkpro.bean.BaseBean;
import com.quoord.tapatalkpro.bean.ForumStatus;
import com.quoord.tapatalkpro.bean.Topic;
import com.quoord.tapatalkpro.sqlhelper.SubscribeForumSqlHelper;
import com.tapatalk.marksdailyapplecomforum.R;
import com.tapatalk.marksdailyapplecomforum.forum.CreateTopicActivity;
import com.tapatalk.marksdailyapplecomforum.forum.TapPreferenceActivity;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class Util {
    static final int MAXTHREAD = 4;
    public static final int timeout = 30000;
    public static String tag = "util";
    public static Object lock = new Object();
    static int maxsize = 10;
    static int size = 0;
    public static int counter = 0;
    static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { // from class: com.quoord.tapatalkpro.util.Util.2
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    public static Date today = new Date();
    public static String cacheBasePath = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/";
    public static String cacheSessionPath = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/session/";
    public static String cacheLongtermPath = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/longterm/";
    public static String cacheAdsPath = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/longterm/ads/";
    public static String favoriteForumLogo = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/favoriteForumLogo/";
    public static String remoteImageCache = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/session/remote-image-cache";
    public static String accountsLogo = "/data/data/com.tapatalk.marksdailyapplecomforum/cache/accountlogo/";

    public static byte[] parseByteArray(byte[] inArray) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int begin = 0;
        int i = 0;
        while (i < inArray.length - 1) {
            if (inArray[i] == 13 && inArray[i + 1] == 10) {
                out.write(inArray, begin, i - begin);
                begin = i + 1;
            }
            i++;
        }
        out.write(inArray, begin, (i - begin) + 1);
        return out.toByteArray();
    }

    public static void lockReset() {
        size = 0;
        lock = new Object();
        maxsize = 1;
    }

    public static void unMyLock(int _size) {
        synchronized (lock) {
            size -= _size;
            try {
                Thread.sleep(50L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lock.notify();
        }
    }

    public static String formatSize(float size2) {
        long mb = 1024 * 1024;
        long gb = mb * 1024;
        if (size2 < ((float) 1024)) {
            return String.format("%d bytes", Integer.valueOf((int) size2));
        }
        if (size2 < ((float) mb)) {
            return String.format("%.1f kB", Float.valueOf(size2 / ((float) 1024)));
        }
        if (size2 < ((float) gb)) {
            return String.format("%.1f MB", Float.valueOf(size2 / ((float) mb)));
        }
        return String.format("%.1f GB", Float.valueOf(size2 / ((float) gb)));
    }

    public static String returnBitMap(String url, String cookie, ForumStatus forumStatus) {
        String mLocal = remoteImageCache + "/" + url.hashCode() + ".jpg";
        return returnBitMap(url, cookie, mLocal, forumStatus);
    }

    public static void trustAllHosts() {
        TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.quoord.tapatalkpro.util.Util.1
            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String returnBitMap(String url, String cookie, String mLocal, ForumStatus forumStatus) {
        HttpURLConnection conn;
        if (!checkCacheData("session/remote-image-cache")) {
            createCacheDir();
        }
        if (url == null || url.length() == 0) {
            return null;
        }
        String url2 = url.replaceAll("&amp;", "&").replaceAll(" ", "%20");
        if (!url2.startsWith("http://") && !url2.startsWith("https://")) {
            url2 = "http://" + url2;
        }
        try {
            URL myFileUrl = new URL(url2);
            try {
                try {
                    if (url2.startsWith("https")) {
                        trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) myFileUrl.openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        conn = https;
                    } else {
                        conn = (HttpURLConnection) myFileUrl.openConnection();
                    }
                    try {
                        if (!myFileUrl.toString().startsWith("https")) {
                            if (conn.getResponseCode() == 302) {
                                URL myFileUrl2 = conn.getURL();
                                try {
                                    try {
                                        if (myFileUrl2.toString().startsWith("https")) {
                                            conn = (HttpURLConnection) myFileUrl2.openConnection();
                                        }
                                    } catch (OutOfMemoryError e) {
                                        eee = e;
                                        eee.printStackTrace();
                                        return mLocal;
                                    }
                                } catch (Exception e2) {
                                }
                            } else {
                                conn = (HttpURLConnection) myFileUrl.openConnection();
                            }
                        }
                    } catch (Exception e3) {
                    }
                    try {
                        conn.setDoInput(true);
                        if (cookie != null && cookie.length() > 0) {
                            conn.setRequestProperty("Cookie", forumStatus.getCookie());
                        }
                        if (forumStatus != null) {
                            conn.setRequestProperty("Referer", forumStatus.getUrl());
                        }
                        if (forumStatus == null || forumStatus.isAgent()) {
                            if (forumStatus == null) {
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1_3 like Mac OS X; fr-fr) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7E18 Safari/528.16");
                            } else {
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1_3 like Mac OS X; fr-fr) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7E18 Safari/528.16 BYO-4/" + forumStatus.getAppVersion());
                            }
                        } else {
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 Firefox/3.5.6 BYO-4/" + forumStatus.getAppVersion());
                        }
                        if (forumStatus != null && forumStatus.isContentType()) {
                            conn.setRequestProperty("Content-Type", "text/xml");
                        }
                        if (forumStatus != null && forumStatus.isRequestZip()) {
                            conn.setRequestProperty("Content-Encoding", "gzip");
                        }
                        conn.setRequestProperty("accept-Encoding", "none");
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = new FileOutputStream(mLocal);
                        try {
                            try {
                                byte[] buffer = new byte[4096];
                                while (true) {
                                    int l = is.read(buffer);
                                    if (l == -1) {
                                        break;
                                    }
                                    fos.write(buffer, 0, l);
                                    fos.flush();
                                }
                                is.close();
                                fos.flush();
                                fos.close();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                                is.close();
                                fos.flush();
                                fos.close();
                            }
                            unMyLock(1);
                            return mLocal;
                        } catch (Throwable th) {
                            is.close();
                            fos.flush();
                            fos.close();
                            throw th;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        e.printStackTrace();
                        return mLocal;
                    }
                } catch (OutOfMemoryError e6) {
                    eee = e6;
                }
            } catch (Exception e7) {
                e = e7;
            }
        } catch (MalformedURLException e8) {
            e8.printStackTrace();
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class BitMapLoader {
        ForumStatus forumStatus;
        int liveThread;
        Map<String, BitMapElement> mDatas = new HashMap();
        ArrayList<BaseBean> datas = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();
        boolean isStop = false;
        Vector threadVector = new Vector(0);

        public BitMapLoader(ForumStatus forumStatus) {
            this.forumStatus = forumStatus;
        }

        public BitMapLoader() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class BitMapElement {
            ArrayList<BaseBean> beans = new ArrayList<>();
            boolean isLoaded;
            boolean isStart;
            String mLocalUrl;
            String mUrl;

            public void addBeans(BaseBean b) {
                this.beans.add(b);
            }

            BitMapElement() {
            }

            public void setIcon(Bitmap icon) {
                for (int i = 0; i < this.beans.size(); i++) {
                    this.beans.get(i).setIcon(icon);
                }
            }

            public void setLocalUri(String uri) {
                for (int i = 0; i < this.beans.size(); i++) {
                    this.beans.get(i).setLocalIconUri(uri);
                }
            }

            public String getCookie() {
                return this.beans.get(0).getCookies();
            }
        }

        /* loaded from: classes.dex */
        class BitMapThd extends Thread {
            BitMapElement b;
            BitMapLoader loader;
            String localUri;
            Bitmap mBitmap = null;

            public BitMapThd(BitMapLoader _loader, BitMapElement bElement) {
                this.loader = _loader;
                this.b = bElement;
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                this.localUri = Util.returnBitMap(this.b.mUrl, this.b.getCookie(), this.b.mLocalUrl, BitMapLoader.this.forumStatus);
                this.b.setLocalUri(this.localUri);
                synchronized (this.loader) {
                    this.loader.finishDwAt(this.b.mUrl);
                    this.loader.notify();
                }
            }
        }

        public ArrayList<BaseBean> getDatas() {
            return this.datas;
        }

        public int getPosition(BaseBean bean) {
            return this.datas.indexOf(bean);
        }

        public BaseBean getBean(int position) {
            return this.datas.get(position);
        }

        public void clearDatas() {
            this.isStop = false;
            this.datas.clear();
            this.urls.clear();
            this.mDatas.clear();
            this.threadVector.clear();
            this.liveThread = 0;
        }

        public void stopDownload() {
            this.isStop = true;
        }

        public void addElement(BaseBean d, String u) {
            String mLocal = Util.remoteImageCache + "/" + u.hashCode() + ".jpg";
            addElement(d, u, mLocal);
        }

        public void addElement(BaseBean d, String u, String mLocal) {
            if (Util.checkLocalData(mLocal)) {
                d.setLocalIconUri(mLocal);
                return;
            }
            this.datas.add(d);
            if (this.mDatas.containsKey(u)) {
                this.mDatas.get(u).addBeans(d);
            } else {
                BitMapElement bitMapElement = new BitMapElement();
                bitMapElement.mUrl = u;
                bitMapElement.mLocalUrl = mLocal;
                bitMapElement.addBeans(d);
                this.mDatas.put(u, bitMapElement);
            }
            if (!this.urls.contains(u)) {
                this.urls.add(u);
            }
        }

        public void finishDwAt(String url) {
            if (url != null) {
                for (int i = 0; i < this.urls.size(); i++) {
                    if (!this.urls.isEmpty() && url.equals(this.urls.get(i))) {
                        try {
                            if (this.mDatas.containsKey(this.urls.get(i))) {
                                this.mDatas.get(this.urls.get(i)).isLoaded = true;
                                this.liveThread--;
                                return;
                            }
                            continue;
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        public int getCount() {
            return this.mDatas.size();
        }

        public void showIcon() {
        }

        public boolean isDownloadFinished() {
            boolean isFinished = true;
            if (this.isStop) {
                return true;
            }
            int i = 0;
            while (true) {
                if (i < this.threadVector.size()) {
                    if (this.mDatas.size() <= i || this.mDatas.get(this.urls.get(i)).isLoaded) {
                        i++;
                    } else {
                        isFinished = false;
                        break;
                    }
                } else {
                    break;
                }
            }
            return isFinished;
        }

        public void waitForDownload() {
            synchronized (this) {
                try {
                    if (this.liveThread < 4 && !this.isStop) {
                        for (int i = 0; i < this.threadVector.size(); i++) {
                            if (this.urls.size() > i && !this.mDatas.get(this.urls.get(i)).isStart && this.liveThread < 4) {
                                if (this.threadVector.size() <= i) {
                                    break;
                                }
                                Thread td = (Thread) this.threadVector.elementAt(i);
                                this.mDatas.get(this.urls.get(i)).isStart = true;
                                if (!td.isAlive()) {
                                    this.liveThread++;
                                    td.start();
                                }
                            }
                        }
                    }
                    wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void destory() {
            for (int i = 0; i < this.threadVector.size(); i++) {
                Thread td = (Thread) this.threadVector.elementAt(i);
                if (td.isAlive()) {
                    td.interrupt();
                }
            }
            Util.size = 0;
            this.datas.clear();
            this.mDatas.clear();
            this.urls.clear();
            this.threadVector.clear();
            this.liveThread = 0;
        }

        public void startDownload() {
            for (int i = 0; i < this.urls.size(); i++) {
                try {
                    BitMapElement bElement = this.mDatas.get(this.urls.get(i));
                    BitMapThd thd = new BitMapThd(this, bElement);
                    this.threadVector.add(thd);
                    if (this.liveThread <= 4) {
                        this.liveThread++;
                        if (this.mDatas.get(this.urls.get(i)) != null) {
                            this.mDatas.get(this.urls.get(i)).isStart = true;
                            System.out.println("start a thread " + this.urls.get(i));
                            thd.start();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public static String formatDate(Date date, Context context) {
        SimpleDateFormat dateFormat;
        DateFormat dateFormat2;
        if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate()) {
            if (TapPreferenceActivity.is24TimeFormat(context)) {
                dateFormat2 = new SimpleDateFormat("HH:mm");
            } else {
                dateFormat2 = new SimpleDateFormat("h:mmaa");
            }
            return dateFormat2.format(date);
        }
        if (TapPreferenceActivity.is24TimeFormat(context)) {
            dateFormat = new SimpleDateFormat("HH:mm");
        } else {
            dateFormat = new SimpleDateFormat("h:mmaa");
        }
        if (date.getYear() == today.getYear()) {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd");
            return dateFormat1.format(date) + " " + dateFormat.format(date);
        }
        DateFormat dateFormat22 = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
        return dateFormat22.format(date);
    }

    public static String formatDateInThread(Date date, Context context) {
        SimpleDateFormat dateformat;
        Date today2 = new Date();
        if (date.getYear() == today2.getYear() && date.getMonth() == today2.getMonth() && date.getDate() == today2.getDate()) {
            if (TapPreferenceActivity.is24TimeFormat(context)) {
                dateformat = new SimpleDateFormat("HH:mm");
            } else {
                dateformat = new SimpleDateFormat("h:mmaa");
            }
        } else if (date.getYear() == today2.getYear()) {
            dateformat = new SimpleDateFormat("MMM dd");
        } else {
            DateFormat dateFormat2 = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
            return dateFormat2.format(date);
        }
        dateformat.setTimeZone(TimeZone.getDefault());
        return dateformat.format(date);
    }

    public static Bitmap getLogoRemoteAvatar(String localUri) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            return decodeLocalImage(localUri, 300, 100);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static void cacheData(String fileName, Object object) {
        if (fileName != null && fileName.length() > 0) {
            try {
                FileOutputStream fo = new FileOutputStream(cacheBasePath + fileName);
                ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
                oo.writeObject(object);
                oo.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("error writing to file");
            }
        }
    }

    public static boolean cachePic(String fileTo, String fileFrom) {
        if (fileTo == null || fileTo.length() <= 0) {
            return false;
        }
        try {
            String fileTo2 = cacheBasePath + fileTo;
            FileInputStream in = new FileInputStream(fileFrom);
            FileOutputStream out = new FileOutputStream(fileTo2);
            byte[] bt = new byte[1024];
            while (true) {
                int count = in.read(bt);
                if (count > 0) {
                    out.write(bt, 0, count);
                } else {
                    in.close();
                    out.close();
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean cachePicFullPath(String fileTo, String fileFrom) {
        if (fileTo == null || fileTo.length() <= 0) {
            return false;
        }
        try {
            File f = new File(fileTo);
            if (f.exists()) {
                f.delete();
            }
            FileInputStream in = new FileInputStream(fileFrom);
            FileOutputStream out = new FileOutputStream(fileTo);
            byte[] bt = new byte[1024];
            while (true) {
                int count = in.read(bt);
                if (count > 0) {
                    out.write(bt, 0, count);
                } else {
                    in.close();
                    out.close();
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Bitmap getCachePic(String fileName) {
        try {
            return BitmapFactory.decodeFile(cacheBasePath + fileName);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static InputStream getRemotePicData(String localUri) {
        try {
            FileInputStream is = new FileInputStream(localUri);
            return is;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeLocalImage(String mLocal, int MAX_WIDTH, int MAX_HEITH) {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mLocal, options1);
        int scale = 1;
        int picWidth = options1.outWidth;
        int picHeight = options1.outHeight;
        if (picWidth > MAX_WIDTH || picHeight > MAX_HEITH) {
            scale = (int) Math.pow(2.0d, (int) Math.round(Math.log(MAX_WIDTH / Math.max(picWidth, picHeight)) / Math.log(0.5d)));
        }
        if (scale < 1) {
            scale = 1;
        }
        options1.inJustDecodeBounds = false;
        options1.inSampleSize = scale;
        options1.inPurgeable = true;
        Bitmap bitmap1 = BitmapFactory.decodeFile(mLocal, options1);
        return bitmap1;
    }

    public static Bitmap decodeLocalBlogImage(String mLocal, int MAX_WIDTH, int MAX_HEITH) {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mLocal, options1);
        int scale = 1;
        int picWidth = options1.outWidth;
        int i = options1.outHeight;
        if (picWidth > MAX_WIDTH) {
            scale = picWidth / MAX_WIDTH;
        }
        if (scale < 1) {
            scale = 1;
        }
        options1.inJustDecodeBounds = false;
        options1.inSampleSize = scale;
        options1.inPurgeable = true;
        Bitmap bitmap1 = BitmapFactory.decodeFile(mLocal, options1);
        return bitmap1;
    }

    public static Bitmap decodeLocalImageUri(Context mContext, Uri uri, int MAX_WIDTH, int MAX_HEITH) {
        try {
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = true;
            InputStream in = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(in, null, options1);
            int scale = 1;
            int picWidth = options1.outWidth;
            int picHeight = options1.outHeight;
            if (picWidth > MAX_WIDTH || picHeight > MAX_HEITH) {
                scale = (int) Math.pow(2.0d, (int) Math.round(Math.log(MAX_WIDTH / Math.max(picWidth, picHeight)) / Math.log(0.5d)));
            }
            if (scale < 1) {
                scale = 1;
            }
            in.close();
            options1.inJustDecodeBounds = false;
            options1.inSampleSize = scale;
            options1.inPurgeable = true;
            return BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, options1);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getRemotePicFullScreen(String localUri) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            return decodeLocalImage(localUri, 900, 1024);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getRemotePic(String localUri) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            return decodeLocalImage(localUri, 400, 600);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static Bitmap getRemoteAvatar(String localUri) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            return decodeLocalImage(localUri, 100, 100);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static Bitmap getRemoteBlog(Context context, String localUri) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            return decodeLocalBlogImage(localUri, context.getResources().getDimensionPixelOffset(R.dimen.blog_user_icon_width), context.getResources().getDimensionPixelOffset(R.dimen.blog_user_icon_height));
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static Bitmap getRemoteTapatalkPic(String localUri, int width, int height) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            Bitmap result = decodeLocalImage(localUri, 400, 600);
            Rect r = new Rect((result.getWidth() - width) / 2, (result.getHeight() - height) / 2, ((result.getWidth() - width) / 2) + width, ((result.getHeight() - height) / 2) + width);
            return cutBitmap(result, r, Bitmap.Config.RGB_565, width, height);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    private static Bitmap matrixImage(Bitmap map, int defaultWidth, int defaultHeight) {
        int newWidth;
        int newHeight;
        int width = map.getWidth();
        int height = map.getHeight();
        if (width < defaultWidth) {
            newWidth = defaultWidth;
        } else {
            newWidth = width;
        }
        if (height < defaultHeight) {
            newHeight = defaultHeight;
        } else {
            newHeight = height;
        }
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(map, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap cutBitmap(Bitmap mBitmap, Rect r, Bitmap.Config config, int defaultWidth, int defaultHeight) {
        int width = r.width();
        int height = r.height();
        int newWidth = mBitmap.getWidth();
        int newHeight = mBitmap.getHeight();
        if (newWidth < width && newHeight < height) {
            width = newWidth;
            height = newHeight;
        } else if (newWidth < width && newHeight > height) {
            width = newWidth;
        } else if (newWidth > width && newHeight < height) {
            height = newHeight;
        }
        Bitmap croppedImage = Bitmap.createBitmap(width, height, config);
        Canvas cvs = new Canvas(croppedImage);
        Rect dr = new Rect((croppedImage.getWidth() - defaultWidth) / 2, (croppedImage.getHeight() - defaultHeight) / 2, ((croppedImage.getWidth() - defaultWidth) / 2) + defaultWidth, ((croppedImage.getHeight() - defaultHeight) / 2) + defaultWidth);
        cvs.drawBitmap(mBitmap, r, dr, (Paint) null);
        if (newWidth < defaultWidth || newHeight < defaultHeight) {
            return Bitmap.createBitmap(matrixImage(croppedImage, defaultWidth, defaultHeight));
        }
        return croppedImage;
    }

    public static boolean checkBitmap(String localUri) {
        if (localUri == null || localUri.length() == 0) {
            return false;
        }
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(localUri, options1);
        int picWidth = options1.outHeight;
        int picHeight = options1.outHeight;
        return picWidth > 0 && picHeight > 0;
    }

    public static Bitmap getRemotePicSmall(String localUri) {
        if (localUri == null) {
            return null;
        }
        try {
            if (localUri.length() == 0) {
                return null;
            }
            File f = new File(localUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) ((f.length() / 20000) + 1);
            return BitmapFactory.decodeFile(localUri, options);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static boolean checkLocalData(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }
        File f = new File(fileName);
        return f.exists();
    }

    public static Bitmap getCachePic(String fileName, int size2) {
        try {
            String fileName2 = cacheBasePath + fileName;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = size2;
            return BitmapFactory.decodeFile(fileName2, options);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("error writing to pic");
            return null;
        }
    }

    public static Object getCacheData(String fileName) {
        Object readob = new Object();
        try {
            FileInputStream fi = new FileInputStream(cacheBasePath + fileName);
            ObjectInputStream oi = new ObjectInputStream(fi);
            readob = oi.readObject();
            oi.close();
            return readob;
        } catch (Exception e) {
            e.printStackTrace();
            return readob;
        }
    }

    public static Object getUserNameCacheData(String fileName) {
        Object readob = null;
        try {
            FileInputStream fi = new FileInputStream(cacheBasePath + fileName);
            ObjectInputStream oi = new ObjectInputStream(fi);
            readob = oi.readObject();
            oi.close();
            return readob;
        } catch (Exception e) {
            e.printStackTrace();
            return readob;
        }
    }

    public static boolean checkCacheData(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }
        File f = new File(cacheBasePath + fileName);
        return f.exists();
    }

    public static void createCacheDir() {
        File f = new File(cacheBasePath);
        if (!f.exists()) {
            f.mkdir();
        }
        File fSession = new File(cacheSessionPath);
        if (!fSession.exists()) {
            fSession.mkdir();
        }
        File fLongTerm = new File(cacheLongtermPath);
        if (!fLongTerm.exists()) {
            fLongTerm.mkdir();
        }
        File fAdsTerm = new File(cacheAdsPath);
        if (!fAdsTerm.exists()) {
            fAdsTerm.mkdir();
        }
        File favoriteFourmLogo = new File(favoriteForumLogo);
        if (!favoriteFourmLogo.exists()) {
            favoriteFourmLogo.mkdir();
        }
        File rImageCache = new File(remoteImageCache);
        if (!rImageCache.exists()) {
            rImageCache.mkdir();
        }
        File raccountLogoCache = new File(accountsLogo);
        if (!raccountLogoCache.exists()) {
            raccountLogoCache.mkdir();
        }
    }

    public static void createCacheDir(String dir) {
        File f = new File(cacheBasePath + dir);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static void createCacheSessionDir(String dir) {
        File f = new File(cacheSessionPath + dir);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static void createCacheLongtermDir(String dir) {
        File f = new File(cacheLongtermPath + dir);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static void cleanCache() {
        try {
            del(cacheSessionPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cleanCache(String dir) {
        try {
            del(cacheSessionPath + dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cleanForumCache(String dir) {
        try {
            del(cacheBasePath + dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delFile(String filepath) {
        try {
            File f = new File(filepath);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
        }
    }

    public static void del(String filepath) throws IOException {
        try {
            File f = new File(filepath);
            if (f.exists() && f.isDirectory()) {
                if (f.listFiles().length == 0) {
                    f.delete();
                    return;
                }
                File[] delFile = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());
                    }
                    delFile[j].delete();
                }
                return;
            }
            f.delete();
        } catch (Exception e) {
        }
    }

    public static int getDip(Context a, int px) {
        int dip = (int) ((px * a.getResources().getDisplayMetrics().density) + 0.5f);
        return dip;
    }

    public static String getMD5(String str) {
        String md5Str = null;
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes("UTF8"));
            byte[] s = digest.digest();
            String result = "";
            for (byte b : s) {
                result = result + Integer.toHexString((b & 255) | (-256)).substring(6);
            }
            md5Str = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    public static String getLogoNameFromUrl(String url, String path) {
        String[] temp_path = url.split("/");
        String file_name = temp_path[temp_path.length - 1];
        return path + "/" + file_name;
    }

    public static Bitmap getForumScaledIcon(String uri, Context mContext, String cacheFileIcon) {
        String uri2 = cacheBasePath + getLogoNameFromUrl(uri, cacheFileIcon);
        Matrix matrix = new Matrix();
        Bitmap a = getRemotePic(uri2);
        if (a == null) {
            return null;
        }
        int width = a.getWidth();
        int height = a.getHeight();
        float scaleWidth = getDip(mContext, 28) / width;
        float scaleHeight = getDip(mContext, 28) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(a, 0, 0, width, height, matrix, true);
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService("wifi");
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();
        return macAddr;
    }

    public static View prepareTabView(String text, Activity mActivity, boolean isLight) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.user_details_activity_tabs, (ViewGroup) null);
        TextView tv = (TextView) view.findViewById(R.id.userDetailsActivityTabTextView);
        tv.setText(text);
        tv.setTextColor(mActivity.getResources().getColorStateList(ThemeUtil.getSubTabTextSelector(mActivity)));
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        ThemeUtil.getTabIndicatorColor(mActivity, iv);
        return view;
    }

    public static String getHost(String url) {
        if (url == null) {
            return null;
        }
        String ret = url;
        String url2 = url.trim();
        if (!url2.startsWith("http://")) {
            url2 = "http://" + url2;
        }
        try {
            URL theUrl = new URL(url2);
            ret = theUrl.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (ret.startsWith("www.")) {
            return ret.replaceFirst("www.", "");
        }
        return ret;
    }

    public static String getRefer(String url) {
        if (url == null) {
            return null;
        }
        String ret = url;
        try {
            URL theUrl = new URL(url);
            ret = theUrl.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url.contains("https")) {
            return "https://" + ret;
        }
        return "http://" + ret;
    }

    public static String getDeviceName() {
        try {
            return "Android:" + URLEncoder.encode(Build.MODEL, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "Android:" + URLEncoder.encode(Build.MODEL);
        }
    }

    public static void startReplyActivity(Activity mContext, ForumStatus forumStatus, Topic mTopic, String quote_title, String quote_content, int firstQuotePosition, boolean canUpload) {
        Intent newIntent = new Intent(mContext, (Class<?>) CreateTopicActivity.class);
        if (quote_title != null && quote_title.length() > 0) {
            newIntent.putExtra("posttitle", quote_title);
        } else {
            newIntent.putExtra("posttitle", mTopic.getTitle());
        }
        newIntent.putExtra("modifytype", 38);
        newIntent.putExtra("topicid", mTopic.getId());
        newIntent.putExtra(SubscribeForumSqlHelper.FORUM_ID, mTopic.getForumId());
        newIntent.putExtra("forumStatus", forumStatus);
        if (firstQuotePosition != 0) {
            newIntent.putExtra("firstQuotePosition", firstQuotePosition);
        }
        if (quote_content != null) {
            newIntent.putExtra("quotecontent", quote_content);
        }
        newIntent.putExtra("canUpload", canUpload);
        mContext.startActivityForResult(newIntent, 38);
    }

    public static void displayFullScreenMessage(Activity mContext, String message, int icon) {
        LinearLayout messageLayout = (LinearLayout) mContext.findViewById(R.id.message_lay);
        if (messageLayout != null) {
            TextView messageText = (TextView) messageLayout.findViewById(R.id.message_text);
            ImageView messageIcon = (ImageView) messageLayout.findViewById(R.id.message_icon);
            messageText.setText(message);
            messageIcon.setImageResource(icon);
            messageLayout.setVisibility(0);
        }
    }

    public static void hideFullScreenMessage(Activity mContext) {
        LinearLayout messageLayout = (LinearLayout) mContext.findViewById(R.id.message_lay);
        if (messageLayout != null) {
            messageLayout.setVisibility(4);
        }
    }

    public static String getTimeString(Context mContext, int timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("gmt"));
        Long tsLong = Long.valueOf(c.getTimeInMillis() / 1000);
        int ts = Integer.parseInt(tsLong.toString());
        int time = ts - timeStamp;
        if (time < 10) {
            return mContext.getString(R.string.moment_ago);
        }
        if (time < 60) {
            return String.format(mContext.getString(R.string.seconds_ago_string), Integer.valueOf(time));
        }
        if (time < 3600) {
            return time / 60 == 1 ? mContext.getString(R.string.min_ago) : String.format(mContext.getString(R.string.mins_ago_string), Integer.valueOf(time / 60));
        }
        if (time < 86400) {
            return time / 3600 == 1 ? mContext.getString(R.string.hour_ago) : String.format(mContext.getString(R.string.hours_ago_string), Integer.valueOf(time / 3600));
        }
        Date date = new Date(timeStamp * 1000);
        DateFormat dateFormat2 = android.text.format.DateFormat.getDateFormat(mContext.getApplicationContext());
        return dateFormat2.format(date);
    }

    public static String getExternalSDCard() {
        String system_file_path;
        String s = "";
        try {
            try {
                Process process = new ProcessBuilder(new String[0]).command("mount").redirectErrorStream(true).start();
                process.waitFor();
                InputStream is = process.getInputStream();
                byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    s = s + new String(buffer);
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String default_path = Environment.getExternalStorageDirectory().toString();
            String[] path = default_path.split("\\/");
            if (default_path.startsWith("/")) {
                system_file_path = path[1];
            } else {
                system_file_path = path[0];
            }
            String[] lines = s.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (-1 != lines[i].indexOf("vfat")) {
                    String[] blocks = lines[i].split("\\s");
                    for (int j = 0; j < blocks.length; j++) {
                        if (-1 != blocks[j].indexOf(system_file_path) && -1 == blocks[j].indexOf(default_path) && -1 != blocks[j].toLowerCase().indexOf("sdcard")) {
                            return blocks[j];
                        }
                    }
                }
            }
            return null;
        } catch (Exception e2) {
            return null;
        }
    }
}
