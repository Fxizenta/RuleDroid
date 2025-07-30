package es.lacabradev.escuchaeldiario.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import es.lacabradev.escuchaeldiario.EscuchaElDiario;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/* loaded from: classes.dex */
public class JSoupService {
    private static final String LOG_NAME = JSoupService.class.getName();
    private static final int TIMEOUT = 20000;
    private static final String USER_AGENT = "Mozilla/6.0 (Windows NT 6.2; WOW64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1";
    private Map<String, String> cookies;
    private ServiceManager manager;

    public JSoupService(ServiceManager manager) {
        this.manager = manager;
    }

    public static void trustAllHosts() {
        X509TrustManager easyTrustManager = new X509TrustManager() { // from class: es.lacabradev.escuchaeldiario.services.JSoupService.1
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        TrustManager[] trustAllCerts = {easyTrustManager};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    public void setUser() {
        this.manager.getExecutorService().execute(new Runnable() { // from class: es.lacabradev.escuchaeldiario.services.JSoupService.2
            @Override // java.lang.Runnable
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(JSoupService.this.manager.getAppContext());
                String emailUser = sp.getString("emailUser", "");
                String passwordUser = sp.getString("passwordUser", "");
                if (!emailUser.equals("")) {
                    JSoupService.trustAllHosts();
                    Connection connection = Jsoup.connect("https://seguro.eldiario.es/login.json");
                    connection.userAgent(JSoupService.USER_AGENT);
                    connection.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    connection.header("X-Requested-With", "XMLHttpRequest");
                    connection.data("email", emailUser);
                    connection.data("password", passwordUser);
                    connection.header("Accept:", "*/*");
                    connection.header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3");
                    connection.header("Accept-Encoding", "gzip, deflate");
                    connection.header("Referer", "https://seguro.eldiario.es/login.html");
                    connection.header("Content-Length", Integer.toString(emailUser.length() + 18 + passwordUser.length()));
                    connection.header("Connection", "keep-alive");
                    connection.header("Pragma", "no-cache");
                    connection.header("Cache-Control", "no-cache");
                    try {
                        connection.post();
                        Connection.Response response = connection.response();
                        JSoupService.this.cookies = response.cookies();
                    } catch (Exception e) {
                        Log.e(JSoupService.LOG_NAME, "Error al hacer login", e);
                    }
                }
            }
        });
    }

    public Connection getConnection(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent(USER_AGENT);
        connection.timeout(TIMEOUT);
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.header("Accept-Encoding", "gzip, deflate");
        connection.header("Referer", EscuchaElDiario.HTTP_WWW_ELDIARIO_ES);
        connection.header("Connection", "keep-alive");
        if (this.cookies != null) {
            connection.cookies(this.cookies);
        }
        return connection;
    }

    public boolean isLogged() {
        return this.cookies != null;
    }
}
