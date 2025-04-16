package net.idt.pennytalk.android.soap.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Vector;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import net.idt.pennytalk.android.log.Logger;
import net.idt.pennytalk.android.soap.SoapConstants;
import net.idt.pennytalk.android.soap.SoapObject;
import net.idt.pennytalk.android.soap.XMLWriter;

/* loaded from: classes.dex */
public class HttpConnectionHandler extends Thread implements Runnable {
    public static final int MAX_TIMEOUT = 30000;
    private boolean killThread;
    private static HttpConnectionHandler instance = null;
    private static boolean canExit = false;
    HttpURLConnection conn = null;
    private Vector requests = new Vector();

    private HttpConnectionHandler() {
    }

    public void close() {
        this.killThread = true;
        Sleep(100L);
    }

    public static HttpConnectionHandler instance() {
        if (instance == null) {
            synchronized (HttpConnectionHandler.class) {
                if (instance == null) {
                    instance = new HttpConnectionHandler();
                    instance.start();
                }
            }
        }
        return instance;
    }

    public synchronized void pushRequest(Object obj, HttpConnectionListener listener) {
        if (obj != null) {
            Logger.log("HttpConnectionHandler Request will be  queued", 5);
            this.requests.add(new RequestObject(obj, listener));
            Logger.log("HttpConnectionHandler Request is queued", 5);
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!this.killThread && !canExit) {
            while (!this.killThread && !canExit) {
                if (this.requests == null || this.requests.size() < 1) {
                    Sleep(10L);
                } else {
                    OutputStream httpOutputStream = null;
                    InputStream httpInputStream = null;
                    Object request = this.requests.elementAt(0);
                    if (request instanceof RequestObject) {
                        RequestObject reqObject = (RequestObject) request;
                        SoapObject soapInstance = (SoapObject) reqObject.getRequest();
                        HttpConnectionListener listener = reqObject.getListener();
                        try {
                            try {
                                try {
                                    try {
                                        if (!soapInstance.isCancelable()) {
                                            trustServer();
                                            String url = soapInstance.getUrl();
                                            URL uri = new URL(url);
                                            this.conn = (HttpURLConnection) uri.openConnection();
                                            this.conn.setAllowUserInteraction(false);
                                            this.conn.setDoInput(true);
                                            this.conn.setDoOutput(true);
                                            this.conn.setUseCaches(false);
                                            this.conn.setRequestMethod("POST");
                                            this.conn.addRequestProperty("Host", SoapConstants.SYSTEM.host);
                                            this.conn.addRequestProperty("Content-Type", SoapConstants.SOAPTemplate.content_type_header_value);
                                            this.conn.addRequestProperty(SoapConstants.SOAPTemplate.soap_action_header_key, XMLWriter.getSoapActionValue(soapInstance.getSoapActionString()));
                                            this.conn.setConnectTimeout(MAX_TIMEOUT);
                                            this.conn.setReadTimeout(MAX_TIMEOUT);
                                            this.conn.connect();
                                            httpOutputStream = this.conn.getOutputStream();
                                            if (httpOutputStream == null) {
                                                throw new SocketException("Outputstream is null for the connection " + this.conn);
                                            }
                                            soapInstance.postRequestToService(httpOutputStream);
                                            Logger.log("HttpConnectionHandler Response Code: " + this.conn.getResponseCode(), 5);
                                            if (this.conn.getResponseCode() == 200) {
                                                httpInputStream = this.conn.getInputStream();
                                                if (httpInputStream == null) {
                                                    throw new SocketException("Inputstream is null for the connection " + this.conn);
                                                }
                                                listener.onDataReceived(soapInstance, httpInputStream, httpInputStream.available());
                                            } else {
                                                listener.onError(soapInstance, this.conn.getErrorStream(), this.conn.getResponseCode());
                                            }
                                            httpOutputStream.close();
                                            if (httpInputStream != null) {
                                                httpInputStream.close();
                                            }
                                            this.conn.disconnect();
                                            this.conn = null;
                                        }
                                        if (httpOutputStream != null) {
                                            try {
                                                httpOutputStream.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (httpInputStream != null) {
                                            try {
                                                httpInputStream.close();
                                            } catch (IOException e2) {
                                                Logger.log(" HttpConnectionHandler " + e2.getStackTrace(), 5);
                                                e2.printStackTrace();
                                            }
                                        }
                                        if (this.conn != null) {
                                            try {
                                                this.conn.disconnect();
                                            } catch (Exception e3) {
                                                Logger.log(" HttpConnectionHandler " + e3.getStackTrace(), 5);
                                                e3.printStackTrace();
                                            }
                                            this.conn = null;
                                        }
                                        this.requests.remove(0);
                                        Logger.log("HttpConnectionHandler: requests.size() " + this.requests.size(), 5);
                                    } catch (SocketTimeoutException ex) {
                                        ex.printStackTrace();
                                        Logger.log(" HttpConnectionHandler " + ex.getStackTrace(), 5);
                                        if (reqObject != null && reqObject.getRetryCount() <= 2) {
                                            reqObject.retry();
                                            this.requests.add(reqObject);
                                        } else if (listener != null && soapInstance != null) {
                                            listener.onError(soapInstance, ex, (String) null);
                                        }
                                        if (0 != 0) {
                                            try {
                                                httpOutputStream.close();
                                            } catch (IOException e4) {
                                                e4.printStackTrace();
                                            }
                                        }
                                        if (0 != 0) {
                                            try {
                                                httpInputStream.close();
                                            } catch (IOException e5) {
                                                Logger.log(" HttpConnectionHandler " + e5.getStackTrace(), 5);
                                                e5.printStackTrace();
                                            }
                                        }
                                        if (this.conn != null) {
                                            try {
                                                this.conn.disconnect();
                                            } catch (Exception e6) {
                                                Logger.log(" HttpConnectionHandler " + e6.getStackTrace(), 5);
                                                e6.printStackTrace();
                                            }
                                            this.conn = null;
                                        }
                                        this.requests.remove(0);
                                        Logger.log("HttpConnectionHandler: requests.size() " + this.requests.size(), 5);
                                    }
                                } catch (SocketException ex2) {
                                    ex2.printStackTrace();
                                    Logger.log(" HttpConnectionHandler " + ex2.getStackTrace(), 5);
                                    if (reqObject != null && reqObject.getRetryCount() <= 2) {
                                        reqObject.retry();
                                        this.requests.add(reqObject);
                                    } else if (listener != null && soapInstance != null) {
                                        listener.onError(soapInstance, ex2, (String) null);
                                    }
                                    if (0 != 0) {
                                        try {
                                            httpOutputStream.close();
                                        } catch (IOException e7) {
                                            e7.printStackTrace();
                                        }
                                    }
                                    if (0 != 0) {
                                        try {
                                            httpInputStream.close();
                                        } catch (IOException e8) {
                                            Logger.log(" HttpConnectionHandler " + e8.getStackTrace(), 5);
                                            e8.printStackTrace();
                                        }
                                    }
                                    if (this.conn != null) {
                                        try {
                                            this.conn.disconnect();
                                        } catch (Exception e9) {
                                            Logger.log(" HttpConnectionHandler " + e9.getStackTrace(), 5);
                                            e9.printStackTrace();
                                        }
                                        this.conn = null;
                                    }
                                    this.requests.remove(0);
                                    Logger.log("HttpConnectionHandler: requests.size() " + this.requests.size(), 5);
                                } catch (IOException ex3) {
                                    ex3.printStackTrace();
                                    Logger.log(" HttpConnectionHandler " + ex3.getStackTrace(), 5);
                                    if (reqObject != null && reqObject.getRetryCount() <= 2) {
                                        reqObject.retry();
                                        this.requests.add(reqObject);
                                    } else if (listener != null && soapInstance != null) {
                                        listener.onError(soapInstance, ex3, (String) null);
                                    }
                                    if (0 != 0) {
                                        try {
                                            httpOutputStream.close();
                                        } catch (IOException e10) {
                                            e10.printStackTrace();
                                        }
                                    }
                                    if (0 != 0) {
                                        try {
                                            httpInputStream.close();
                                        } catch (IOException e11) {
                                            Logger.log(" HttpConnectionHandler " + e11.getStackTrace(), 5);
                                            e11.printStackTrace();
                                        }
                                    }
                                    if (this.conn != null) {
                                        try {
                                            this.conn.disconnect();
                                        } catch (Exception e12) {
                                            Logger.log(" HttpConnectionHandler " + e12.getStackTrace(), 5);
                                            e12.printStackTrace();
                                        }
                                        this.conn = null;
                                    }
                                    this.requests.remove(0);
                                    Logger.log("HttpConnectionHandler: requests.size() " + this.requests.size(), 5);
                                }
                            } catch (Throwable th) {
                                if (0 != 0) {
                                    try {
                                        httpOutputStream.close();
                                    } catch (IOException e13) {
                                        e13.printStackTrace();
                                    }
                                }
                                if (0 != 0) {
                                    try {
                                        httpInputStream.close();
                                    } catch (IOException e14) {
                                        Logger.log(" HttpConnectionHandler " + e14.getStackTrace(), 5);
                                        e14.printStackTrace();
                                    }
                                }
                                if (this.conn != null) {
                                    try {
                                        this.conn.disconnect();
                                    } catch (Exception e15) {
                                        Logger.log(" HttpConnectionHandler " + e15.getStackTrace(), 5);
                                        e15.printStackTrace();
                                    }
                                    this.conn = null;
                                }
                                this.requests.remove(0);
                                Logger.log("HttpConnectionHandler: requests.size() " + this.requests.size(), 5);
                                throw th;
                            }
                        } catch (Exception ex4) {
                            ex4.printStackTrace();
                            Logger.log(" HttpConnectionHandler " + ex4.getStackTrace(), 5);
                            if (listener != null && soapInstance != null) {
                                listener.onError(soapInstance, ex4, ex4.getMessage());
                            }
                            if (0 != 0) {
                                try {
                                    httpOutputStream.close();
                                } catch (IOException e16) {
                                    e16.printStackTrace();
                                }
                            }
                            if (0 != 0) {
                                try {
                                    httpInputStream.close();
                                } catch (IOException e17) {
                                    Logger.log(" HttpConnectionHandler " + e17.getStackTrace(), 5);
                                    e17.printStackTrace();
                                }
                            }
                            if (this.conn != null) {
                                try {
                                    this.conn.disconnect();
                                } catch (Exception e18) {
                                    Logger.log(" HttpConnectionHandler " + e18.getStackTrace(), 5);
                                    e18.printStackTrace();
                                }
                                this.conn = null;
                            }
                            this.requests.remove(0);
                            Logger.log("HttpConnectionHandler: requests.size() " + this.requests.size(), 5);
                        }
                    }
                }
            }
            instance = null;
            return;
        }
        instance = null;
    }

    private void Sleep(long millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException ex) {
            Logger.log(" HttpConnectionHandler " + ex.getStackTrace(), 5);
        }
    }

    /* loaded from: classes.dex */
    private static class RequestObject {
        private HttpConnectionListener mListener;
        private Object request;
        private int retryCount = 0;

        public int getRetryCount() {
            Logger.log("HttpConnectionHandler: Retry Count: " + this.retryCount, 5);
            return this.retryCount;
        }

        public void retry() {
            this.retryCount++;
        }

        public RequestObject(Object request, HttpConnectionListener listener) {
            this.request = request;
            this.mListener = listener;
        }

        public Object getRequest() {
            return this.request;
        }

        public HttpConnectionListener getListener() {
            return this.mListener;
        }
    }

    private void trustServer() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: net.idt.pennytalk.android.soap.connection.HttpConnectionHandler.1
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() { // from class: net.idt.pennytalk.android.soap.connection.HttpConnectionHandler.2
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            Logger.log("HTTPConnectionHandler: trustServer() Exception -  " + e.getMessage(), 1);
        }
    }

    public static void sendExitSignal() {
        canExit = true;
    }
}
