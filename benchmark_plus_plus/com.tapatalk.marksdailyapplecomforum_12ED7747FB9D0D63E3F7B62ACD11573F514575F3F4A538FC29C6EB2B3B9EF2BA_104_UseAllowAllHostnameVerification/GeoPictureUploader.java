package com.quoord.tapatalkpro.util;

import android.app.Activity;
import com.quoord.tapatalkpro.adapter.forum.SearchHistoryAdapter;
import com.quoord.tapatalkpro.adapter.forum.UploadAdapter;
import com.quoord.tapatalkpro.bean.ForumStatus;
import com.quoord.xmlrpc.Base64;
import com.quoord.xmlrpc.XmlRpcParser;
import com.tapatalk.marksdailyapplecomforum.R;
import com.tapatalk.marksdailyapplecomforum.forum.ForumActivityStatus;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.kxml2.io.KXmlParser;

/* loaded from: classes.dex */
public class GeoPictureUploader {
    private DataOutputStream dataStream;
    private String did;
    private String fid;
    private ForumStatus forumStatus;
    public String isLogin;
    public UploadAdapter mAdapter;
    private ForumActivityStatus mContext;
    private XmlRpcParser parser;
    private Object result;
    private KXmlParser xp;
    static String serviceDomain = "http://img.tapatalk.com";
    static String postUrl = serviceDomain + "/app/upload.php";
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    static String boundary = "*****mgd*****";
    static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { // from class: com.quoord.tapatalkpro.util.GeoPictureUploader.2
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /* loaded from: classes.dex */
    enum ReturnCode {
        noPicture,
        unknown,
        http201,
        http400,
        http401,
        http403,
        http404,
        http500
    }

    public GeoPictureUploader(String fid, String did, ForumActivityStatus context) {
        this.fid = null;
        this.did = null;
        this.dataStream = null;
        this.fid = fid;
        this.did = did;
        this.mContext = context;
    }

    public GeoPictureUploader(String fid, ForumStatus forumStatus, ForumActivityStatus context) {
        this.fid = null;
        this.did = null;
        this.dataStream = null;
        this.fid = fid;
        this.forumStatus = forumStatus;
        this.mContext = context;
    }

    public static void setServiceDomain(String domainName) {
        serviceDomain = domainName;
    }

    public static String getServiceDomain() {
        return serviceDomain;
    }

    public Object uploadAvatar(ArrayList para) {
        HttpURLConnection conn;
        UploadAdapter ada = (UploadAdapter) para.get(0);
        try {
            this.mAdapter = ada;
            if (ada.getUri() != null) {
                try {
                    try {
                        String mUrl = this.forumStatus.getFullUploadUrl();
                        URL connectURL = new URL(mUrl);
                        if (this.forumStatus.getUrl().startsWith("https")) {
                            trustAllHosts();
                            HttpsURLConnection https = (HttpsURLConnection) connectURL.openConnection();
                            https.setHostnameVerifier(DO_NOT_VERIFY);
                            conn = https;
                        } else {
                            conn = (HttpURLConnection) connectURL.openConnection();
                        }
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Cookie", this.forumStatus.getCookie());
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0 Firefox/3.5.6 BYO-4/" + this.forumStatus.getAppVersion());
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.connect();
                        this.dataStream = new DataOutputStream(conn.getOutputStream());
                        if (this.forumStatus.getVersion().endsWith("vb40_2.1.5") || this.forumStatus.getVersion().endsWith("vb40_2.1.4")) {
                            writeFormField("method_name", "set_avatar");
                        } else {
                            writeFormField("method_name", "upload_avatar");
                        }
                        String fileName = "uploadfromtaptalk" + System.currentTimeMillis() + ".jpg";
                        String key = "upload";
                        if (this.forumStatus.isVB() || this.forumStatus.isXF()) {
                            key = "upload";
                        } else if (this.forumStatus.isPB()) {
                            key = "uploadfile";
                        }
                        ada.imageName = fileName;
                        ByteArrayOutputStream a = ada.getAvatarByteStream(this.forumStatus);
                        writeFileField(key, fileName, "image/jpeg", a);
                        a.close();
                        ada.closeByteStream();
                        this.dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
                        this.dataStream.flush();
                        this.dataStream.close();
                        this.dataStream = null;
                        try {
                            DataInputStream dis = new DataInputStream(conn.getInputStream());
                            byte[] data = new byte[1024];
                            int len = dis.read(data, 0, 1024);
                            dis.close();
                            String login = conn.getHeaderField("Mobiquo_is_login");
                            String newlogin = conn.getHeaderField("mobiquologin");
                            if (login != null) {
                                this.isLogin = login;
                            }
                            if (newlogin != null) {
                                this.isLogin = newlogin;
                            }
                            if (len > 0) {
                                String response = new String(data, 0, len);
                                this.xp = new KXmlParser();
                                this.xp.setInput(new CharArrayReader(response.toCharArray()));
                                this.parser = new XmlRpcParser(this.xp);
                                this.result = this.parser.parseResponse();
                                return this.result;
                            }
                            return "";
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
                            return "";
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        System.out.println("GeoPictureUploader.uploadPicture: unknown: " + e2.getMessage());
                        return null;
                    }
                } catch (MalformedURLException mue) {
                    System.out.println("GeoPictureUploader.uploadPicture: Malformed URL: " + mue.getMessage());
                    mue.printStackTrace();
                    return null;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    System.out.println("GeoPictureUploader.uploadPicture: IOE: " + ioe.getMessage());
                    return null;
                }
            }
            return null;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    private static void trustAllHosts() {
        TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.quoord.tapatalkpro.util.GeoPictureUploader.1
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

    public Object uploadPictureToForum(ArrayList para) {
        HttpURLConnection conn;
        InputStream is;
        String response;
        UploadAdapter ada = (UploadAdapter) para.get(1);
        String group_id = null;
        if (para.size() == 3) {
            group_id = (String) para.get(2);
        }
        try {
            if (ada.getUri() != null) {
                try {
                    try {
                        String mUrl = this.forumStatus.getFullUploadUrl();
                        URI mUri = new URI(mUrl);
                        if (this.forumStatus.getUrl().startsWith("https")) {
                            String mUrl2 = "https://" + mUri.getHost().toLowerCase() + mUri.getPath();
                            URL connectURL = new URL(mUrl2);
                            trustAllHosts();
                            HttpsURLConnection https = (HttpsURLConnection) connectURL.openConnection();
                            https.setHostnameVerifier(DO_NOT_VERIFY);
                            conn = https;
                        } else {
                            String mUrl3 = "http://" + mUri.getHost().toLowerCase() + mUri.getPath();
                            URL connectURL2 = new URL(mUrl3);
                            conn = (HttpURLConnection) connectURL2.openConnection();
                        }
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Cookie", this.forumStatus.getCookie());
                        if (this.forumStatus == null || this.forumStatus.isAgent()) {
                            if (this.forumStatus == null) {
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1_3 like Mac OS X; fr-fr) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7E18 Safari/528.16");
                            } else {
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1_3 like Mac OS X; fr-fr) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7E18 Safari/528.16 BYO-4/" + this.forumStatus.getAppVersion());
                            }
                        } else {
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 Firefox/3.5.6 BYO-4/" + this.forumStatus.getAppVersion());
                        }
                        if (this.forumStatus != null && this.forumStatus.isRequestZip()) {
                            conn.setRequestProperty("Content-Encoding", "gzip");
                        }
                        if (this.forumStatus == null || !this.forumStatus.getUseZip()) {
                            conn.setRequestProperty("accept-Encoding", "none");
                        } else {
                            conn.setRequestProperty("accept-Encoding", "gzip");
                        }
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.connect();
                        this.dataStream = new DataOutputStream(conn.getOutputStream());
                        writeFormField("method_name", "upload_attach");
                        writeFormField(SearchHistoryAdapter.FORUMID, this.fid);
                        if (group_id != null) {
                            writeFormField("group_id", group_id);
                        }
                        this.mAdapter = ada;
                        String fileName = "uploadfromtaptalk" + System.currentTimeMillis() + ".jpg";
                        if (((Activity) this.mContext).getResources().getBoolean(R.bool.is_rebranding)) {
                            fileName = System.currentTimeMillis() + ".jpg";
                        }
                        ada.imageName = fileName;
                        String key = "attachment[]";
                        if (this.forumStatus.isVB()) {
                            key = "attachment[]";
                        } else if (this.forumStatus.isPB()) {
                            key = "fileupload";
                        }
                        writeFileField(key, fileName, "image/jpg", ada.getByteStream());
                        this.dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
                        this.dataStream.flush();
                        this.dataStream.close();
                        this.dataStream = null;
                        try {
                            String contentEncodeheaders = conn.getHeaderField("Content-Encoding");
                            boolean isGzip = false;
                            if (contentEncodeheaders != null && contentEncodeheaders.contains("gzip")) {
                                isGzip = true;
                            }
                            if (isGzip) {
                                is = new GZIPInputStream(conn.getInputStream());
                            } else {
                                is = conn.getInputStream();
                            }
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            try {
                                byte[] buffer = new byte[4096];
                                while (true) {
                                    int l = is.read(buffer);
                                    if (l == -1) {
                                        break;
                                    }
                                    bos.write(buffer, 0, l);
                                    bos.flush();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                is.close();
                                bos.flush();
                                bos.close();
                            }
                            response = new String(bos.toByteArray());
                        } catch (Exception e2) {
                            e = e2;
                        }
                        try {
                            String login = conn.getHeaderField("Mobiquo_is_login");
                            String newlogin = conn.getHeaderField("mobiquologin");
                            if (login != null) {
                                this.isLogin = login;
                            }
                            if (newlogin != null) {
                                this.isLogin = newlogin;
                            }
                            if (this.forumStatus.isPB()) {
                                this.isLogin = "true";
                            }
                            if (response.length() == 0) {
                                return "";
                            }
                            this.xp = new KXmlParser();
                            this.xp.setInput(new CharArrayReader(response.toCharArray()));
                            this.parser = new XmlRpcParser(this.xp);
                            this.result = this.parser.parseResponse();
                            return this.result;
                        } catch (Exception e3) {
                            e = e3;
                            e.printStackTrace();
                            System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
                            return "";
                        }
                    } catch (Exception e4) {
                        e4.printStackTrace();
                        System.out.println("GeoPictureUploader.uploadPicture: unknown: " + e4.getMessage());
                        return null;
                    }
                } catch (MalformedURLException mue) {
                    System.out.println("GeoPictureUploader.uploadPicture: Malformed URL: " + mue.getMessage());
                    mue.printStackTrace();
                    return null;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    System.out.println("GeoPictureUploader.uploadPicture: IOE: " + ioe.getMessage());
                    return null;
                }
            }
            return null;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public String getLoginStatus() {
        return this.isLogin;
    }

    public String uploadPicture(UploadAdapter ada, String userName, String uid) {
        String str = null;
        try {
            if (ada.getUri() == null) {
                return null;
            }
            try {
                try {
                    URL connectURL = new URL(postUrl);
                    HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.connect();
                    this.dataStream = new DataOutputStream(conn.getOutputStream());
                    if (ada.getAUID() != 0) {
                        writeFormField("auid", ada.getAUID() + "");
                    }
                    writeFormField("fid", this.fid);
                    writeFormField("device_id", this.did);
                    writeFormField("username", Base64.encode(userName.getBytes()));
                    if (uid != null && uid.length() > 0) {
                        writeFormField("uid", uid);
                    }
                    ByteArrayOutputStream a = ada.getTkByteStream();
                    writeFileField("file", "uploadfromtaptalk.jpg", "image/jpeg", a);
                    a.close();
                    ada.closeByteStream();
                    this.dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
                    this.dataStream.flush();
                    this.dataStream.close();
                    this.dataStream = null;
                    str = getResponse(conn);
                    return str;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    System.out.println("GeoPictureUploader.uploadPicture: IOE: " + ioe.getMessage());
                    return null;
                }
            } catch (MalformedURLException mue) {
                System.out.println("GeoPictureUploader.uploadPicture: Malformed URL: " + mue.getMessage());
                mue.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("GeoPictureUploader.uploadPicture: unknown: " + e.getMessage());
                return null;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return str;
        }
    }

    private String getResponse(HttpURLConnection conn) {
        try {
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            byte[] data = new byte[1024];
            int len = dis.read(data, 0, 1024);
            dis.close();
            if (len > 0) {
                return new String(data, 0, len);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
            return "";
        }
    }

    private String getResponseOrig(HttpURLConnection conn) {
        InputStream is = null;
        try {
            try {
                is = conn.getInputStream();
                StringBuffer sb = new StringBuffer();
                while (true) {
                    int ch = is.read();
                    if (ch == -1) {
                        break;
                    }
                    sb.append((char) ch);
                }
                String stringBuffer = sb.toString();
                if (is == null) {
                    return stringBuffer;
                }
                try {
                    is.close();
                    return stringBuffer;
                } catch (Exception e) {
                    return stringBuffer;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e3) {
                    }
                }
                return "";
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e4) {
                }
            }
            throw th;
        }
    }

    private void writeFormField(String fieldName, String fieldValue) {
        try {
            this.dataStream.writeBytes(twoHyphens + boundary + CRLF);
            this.dataStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + CRLF);
            this.dataStream.writeBytes(CRLF);
            this.dataStream.writeBytes(fieldValue);
            this.dataStream.writeBytes(CRLF);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
        }
    }

    private void writeFileField(String fieldName, String fieldValue, String type, InputStream fis) {
        try {
            this.dataStream.writeBytes(twoHyphens + boundary + CRLF);
            this.dataStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fieldValue + "\"" + CRLF);
            this.dataStream.writeBytes("Content-Type: " + type + CRLF);
            this.dataStream.writeBytes(CRLF);
            int bytesAvailable = fis.available();
            int bufferSize = Math.min(bytesAvailable, 1024);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fis.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                this.dataStream.write(buffer, 0, bufferSize);
                int bytesAvailable2 = fis.available();
                bufferSize = Math.min(bytesAvailable2, 1024);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            this.dataStream.writeBytes(CRLF);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
        }
    }

    private void writeFileField(String fieldName, String fieldValue, String type, ByteArrayOutputStream ois) {
        try {
            this.dataStream.writeBytes(twoHyphens + boundary + CRLF);
            this.dataStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fieldValue + "\"" + CRLF);
            this.dataStream.writeBytes("Content-Type: " + type + CRLF);
            this.dataStream.writeBytes(CRLF);
            byte[] buffer = ois.toByteArray();
            if (this.mAdapter != null && (this.mAdapter instanceof UploadAdapter)) {
                this.mAdapter.newSize = buffer.length;
            }
            int offset = 0;
            int i = 0;
            while (offset + 4096 < buffer.length) {
                this.dataStream.write(buffer, offset, 4096);
                offset += 4096;
                this.mContext.updateUI(37, Integer.valueOf(offset));
                if (this.mAdapter != null) {
                    this.mAdapter.updateProgress(offset);
                }
                i++;
            }
            this.dataStream.write(buffer, offset, buffer.length - offset);
            this.mContext.updateUI(37, Integer.valueOf(buffer.length));
            if (this.mAdapter != null) {
                this.mAdapter.updateProgress(buffer.length);
            }
            this.dataStream.writeBytes(CRLF);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
        }
    }
}
