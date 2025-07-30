package jp.co.newphoria.html5app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.p016a.p017a.p018a.C0233r;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jp.co.newphoria.html5app.p048a.C0472a;
import jp.co.newphoria.html5app.p048a.C0473b;
import jp.co.newphoria.html5app.p048a.C0475d;
import jp.co.newphoria.reversi.C0650c;
import jp.co.newphoria.reversi.NotificationReceiver;
import jp.co.newphoria.reversi.R;
import jp.p046a.p047a.C0454a;
import jp.p046a.p047a.C0463j;
import jp.p046a.p047a.C0464k;
import jp.p046a.p047a.C0465l;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

@SuppressLint({"SetJavaScriptEnabled"})
/* loaded from: classes.dex */
public class WebActivity extends Activity implements SensorEventListener, LocationListener, SurfaceHolder.Callback {

    /* renamed from: A */
    C0464k f1425A;

    /* renamed from: B */
    C0464k f1426B;

    /* renamed from: I */
    protected int f1433I;

    /* renamed from: M */
    C0233r f1437M;

    /* renamed from: O */
    ConnectivityManager f1439O;

    /* renamed from: P */
    Timer f1440P;

    /* renamed from: S */
    VideoView f1443S;

    /* renamed from: T */
    RelativeLayout f1444T;

    /* renamed from: a */
    private SensorManager f1451a;

    /* renamed from: aA */
    float f1452aA;

    /* renamed from: aB */
    long f1453aB;

    /* renamed from: aC */
    long f1454aC;

    /* renamed from: aD */
    int f1455aD;

    /* renamed from: aE */
    Timer f1456aE;

    /* renamed from: aF */
    float f1457aF;

    /* renamed from: aG */
    float f1458aG;

    /* renamed from: aH */
    float f1459aH;

    /* renamed from: aI */
    long f1460aI;

    /* renamed from: aJ */
    long f1461aJ;

    /* renamed from: aK */
    boolean f1462aK;

    /* renamed from: aL */
    LocationManager f1463aL;

    /* renamed from: aM */
    int f1464aM;

    /* renamed from: aN */
    Timer f1465aN;

    /* renamed from: aO */
    long f1466aO;

    /* renamed from: aP */
    boolean f1467aP;

    /* renamed from: aQ */
    Timer f1468aQ;

    /* renamed from: aR */
    long f1469aR;

    /* renamed from: aS */
    boolean f1470aS;

    /* renamed from: aT */
    long f1471aT;

    /* renamed from: aX */
    int f1475aX;

    /* renamed from: aY */
    MediaRecorder f1476aY;

    /* renamed from: ad */
    VideoView f1481ad;

    /* renamed from: ae */
    RelativeLayout f1482ae;

    /* renamed from: af */
    MediaController f1483af;

    /* renamed from: ag */
    boolean f1484ag;

    /* renamed from: ah */
    Cursor f1485ah;

    /* renamed from: ai */
    boolean f1486ai;

    /* renamed from: ak */
    ArrayList f1488ak;

    /* renamed from: al */
    RelativeLayout f1489al;

    /* renamed from: am */
    RelativeLayout f1490am;

    /* renamed from: an */
    ImageView f1491an;

    /* renamed from: aw */
    int f1500aw;

    /* renamed from: ax */
    Timer f1501ax;

    /* renamed from: ay */
    float f1502ay;

    /* renamed from: az */
    float f1503az;

    /* renamed from: b */
    private SensorManager f1504b;

    /* renamed from: ba */
    long f1505ba;

    /* renamed from: bb */
    int f1506bb;

    /* renamed from: be */
    URL f1509be;

    /* renamed from: bf */
    C0526bu f1510bf;

    /* renamed from: bj */
    OutputStream f1514bj;

    /* renamed from: bq */
    private Uri f1521bq;

    /* renamed from: br */
    private SharedPreferences f1522br;

    /* renamed from: c */
    private SurfaceHolder f1525c;

    /* renamed from: d */
    private SurfaceView f1526d;

    /* renamed from: f */
    private JSONObject f1528f;

    /* renamed from: g */
    private boolean f1529g;

    /* renamed from: h */
    private long f1530h;

    /* renamed from: l */
    protected C0473b f1534l;

    /* renamed from: m */
    protected WebActivity f1535m;

    /* renamed from: o */
    protected DetectableKeyboardEventLayout f1537o;

    /* renamed from: p */
    protected RelativeLayout f1538p;

    /* renamed from: r */
    protected WebView f1540r;

    /* renamed from: s */
    protected ProgressBar f1541s;

    /* renamed from: t */
    protected LinearLayout f1542t;

    /* renamed from: u */
    protected RelativeLayout f1543u;

    /* renamed from: v */
    protected TextView f1544v;

    /* renamed from: w */
    protected LinearLayout f1545w;

    /* renamed from: x */
    C0464k f1546x;

    /* renamed from: y */
    C0464k f1547y;

    /* renamed from: z */
    C0464k f1548z;

    /* renamed from: n */
    protected Handler f1536n = new Handler();

    /* renamed from: q */
    protected boolean f1539q = false;

    /* renamed from: C */
    boolean f1427C = false;

    /* renamed from: D */
    protected float f1428D = 42.0f;

    /* renamed from: E */
    protected float f1429E = 0.0f;

    /* renamed from: F */
    protected float f1430F = 0.0f;

    /* renamed from: G */
    int f1431G = 37;

    /* renamed from: H */
    protected String f1432H = null;

    /* renamed from: J */
    boolean f1434J = false;

    /* renamed from: K */
    boolean f1435K = false;

    /* renamed from: L */
    float f1436L = 9.80665f;

    /* renamed from: N */
    boolean f1438N = false;

    /* renamed from: Q */
    boolean f1441Q = false;

    /* renamed from: R */
    int f1442R = 0;

    /* renamed from: e */
    private MediaPlayer f1527e = null;

    /* renamed from: U */
    String f1445U = "";

    /* renamed from: V */
    HashMap f1446V = new HashMap();

    /* renamed from: W */
    HashMap f1447W = new HashMap();

    /* renamed from: X */
    int f1448X = 0;

    /* renamed from: Y */
    int f1449Y = 0;

    /* renamed from: Z */
    public boolean f1450Z = false;

    /* renamed from: aa */
    boolean f1478aa = false;

    /* renamed from: ab */
    boolean f1479ab = true;

    /* renamed from: ac */
    String f1480ac = "";

    /* renamed from: aj */
    boolean f1487aj = false;

    /* renamed from: ao */
    boolean f1492ao = false;

    /* renamed from: ap */
    boolean f1493ap = false;

    /* renamed from: aq */
    boolean f1494aq = false;

    /* renamed from: ar */
    float[] f1495ar = null;

    /* renamed from: as */
    float[] f1496as = null;

    /* renamed from: at */
    int f1497at = 16;

    /* renamed from: au */
    int f1498au = 3;

    /* renamed from: av */
    boolean f1499av = false;

    /* renamed from: i */
    private float[] f1531i = {0.0f, 0.0f, 0.0f};

    /* renamed from: j */
    private float[] f1532j = {0.0f, 0.0f, 0.0f};

    /* renamed from: k */
    private final SensorEventListener f1533k = new C0549cq(this);

    /* renamed from: aU */
    String f1472aU = null;

    /* renamed from: aV */
    int f1473aV = 0;

    /* renamed from: aW */
    Camera f1474aW = null;

    /* renamed from: bo */
    private String f1519bo = "";

    /* renamed from: bp */
    private String f1520bp = "";

    /* renamed from: bs */
    private BroadcastReceiver f1523bs = new C0560da(this);

    /* renamed from: aZ */
    boolean f1477aZ = false;

    /* renamed from: bc */
    boolean f1507bc = false;

    /* renamed from: bd */
    boolean f1508bd = false;

    /* renamed from: bg */
    String f1511bg = "";

    /* renamed from: bh */
    BluetoothServerSocket f1512bh = null;

    /* renamed from: bi */
    BluetoothSocket f1513bi = null;

    /* renamed from: bk */
    boolean f1515bk = false;

    /* renamed from: bl */
    boolean f1516bl = false;

    /* renamed from: bm */
    JSONArray f1517bm = new JSONArray();

    /* renamed from: bt */
    private final BroadcastReceiver f1524bt = new C0567dh(this);

    /* renamed from: bn */
    boolean f1518bn = true;

    /* renamed from: A */
    private void m1648A() {
        C0475d.m1866a().m1867a(this, this.f1535m.getFilesDir());
        C0472a.f1620x = false;
        C0472a.f1621y = true;
        C0472a.f1568C = false;
        C0472a.f1569D = false;
        C0472a.f1570E = true;
        XmlPullParser newPullParser = Xml.newPullParser();
        File file = new File(this.f1535m.getFilesDir().toString() + "/contents/web/applican-config.xml");
        if (file.exists()) {
            try {
                newPullParser.setInput(new FileReader(file));
                String str = "";
                String str2 = "";
                for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                    switch (eventType) {
                        case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                            str = newPullParser.getName().trim().toLowerCase();
                            str2 = "";
                            if (newPullParser.getAttributeCount() > 0) {
                                str2 = newPullParser.getAttributeValue(0).trim().toUpperCase();
                                break;
                            } else {
                                break;
                            }
                        case C0650c.PullToRefresh_ptrMode /* 4 */:
                            String lowerCase = newPullParser.getText().trim().toLowerCase();
                            if (lowerCase.length() != 0 && str.equals("property")) {
                                if (str2.equals("NAVIGATION_BAR")) {
                                    if (lowerCase.equals("true")) {
                                        C0472a.f1620x = true;
                                        break;
                                    } else {
                                        C0472a.f1620x = false;
                                        break;
                                    }
                                } else if (str2.equals("PULL_TO_REFRESH")) {
                                    if (lowerCase.equals("true")) {
                                        C0472a.f1621y = true;
                                        break;
                                    } else {
                                        C0472a.f1621y = false;
                                        break;
                                    }
                                } else if (str2.equals("RESUME_RELOAD")) {
                                    if (lowerCase.equals("true")) {
                                        C0472a.f1568C = true;
                                        break;
                                    } else {
                                        C0472a.f1568C = false;
                                        break;
                                    }
                                } else if (str2.equals("KEEP_SCREEN_ON")) {
                                    if (lowerCase.equals("true")) {
                                        C0472a.f1569D = true;
                                        break;
                                    } else {
                                        C0472a.f1569D = false;
                                        break;
                                    }
                                } else if (str2.equals("WEBVIEW_BACK")) {
                                    if (lowerCase.equals("true")) {
                                        C0472a.f1570E = true;
                                        break;
                                    } else {
                                        C0472a.f1570E = false;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (C0472a.f1574I) {
            C0472a.f1566A = true;
            C0472a.f1567B = false;
        }
        if (C0472a.f1566A && !C0472a.f1567B) {
            setRequestedOrientation(1);
        } else if (C0472a.f1566A || !C0472a.f1567B) {
            setRequestedOrientation(-1);
        } else {
            setRequestedOrientation(0);
        }
        if (C0472a.f1569D) {
            this.f1534l.f1656s.getWindow().addFlags(128);
        } else {
            this.f1534l.f1656s.getWindow().clearFlags(128);
        }
    }

    /* renamed from: B */
    private String m1649B() {
        File file = new File(getExternalCacheDir(), "temp");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /* renamed from: C */
    private Uri m1650C() {
        long currentTimeMillis = System.currentTimeMillis();
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(currentTimeMillis));
        String str = "getpictrue_" + format + ".jpg";
        String str2 = m1649B() + "/" + str;
        File file = new File(str2);
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", format);
        contentValues.put("_display_name", str);
        contentValues.put("mime_type", "image/jpeg");
        contentValues.put("_data", str2);
        contentValues.put("datetaken", Long.valueOf(currentTimeMillis));
        if (file.exists()) {
            contentValues.put("_size", Long.valueOf(file.length()));
        }
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    /* renamed from: D */
    private void m1651D() {
        int i;
        try {
            i = this.f1528f.getInt("sourceType");
        } catch (Exception e) {
            i = 1;
        }
        if (i != 0 && i != 2) {
            this.f1521bq = m1650C();
            Intent intent = new Intent();
            intent.setAction("android.media.action.IMAGE_CAPTURE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.putExtra("output", this.f1521bq);
            startActivityForResult(intent, 6);
            return;
        }
        int i2 = 0;
        try {
            i2 = this.f1528f.getInt("mediaType");
        } catch (Exception e2) {
        }
        Intent intent2 = new Intent();
        intent2.setAction("android.intent.action.PICK");
        if (i2 == 1) {
            intent2.setType("video/*");
        } else if (i2 == 2) {
            intent2.setType("image/* video/*");
        } else {
            intent2.setType("image/*");
        }
        startActivityForResult(intent2, 6);
    }

    /* renamed from: E */
    private void m1652E() {
        MediaPlayer mediaPlayer;
        this.f1477aZ = false;
        if (this.f1476aY != null) {
            try {
                this.f1476aY.stop();
            } catch (Exception e) {
            }
            try {
                this.f1476aY.reset();
            } catch (Exception e2) {
            }
            try {
                this.f1476aY.release();
            } catch (Exception e3) {
            }
            this.f1476aY = null;
        }
        if (this.f1506bb < 1) {
            return;
        }
        C0606et c0606et = (C0606et) this.f1447W.get(Integer.valueOf(this.f1506bb));
        c0606et.f1953a = false;
        try {
            int i = this.f1506bb;
            try {
                mediaPlayer = MediaPlayer.create(this.f1535m, Uri.parse(c0606et.f1960h));
            } catch (Exception e4) {
                this.f1534l.m1853a("1>>" + e4.getMessage());
                mediaPlayer = null;
            }
            if (mediaPlayer != null) {
                c0606et.f1956d = mediaPlayer;
                c0606et.f1955c = true;
                long duration = mediaPlayer.getDuration();
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("status", "success");
                    jSONObject.put("duration", duration);
                } catch (Exception e5) {
                }
                m1822c("javascript:applican._successCallback(" + c0606et.f1957e + ", " + jSONObject.toString() + ", true);");
            } else {
                c0606et.f1955c = false;
            }
            this.f1447W.put(Integer.valueOf(i), c0606et);
            try {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("status", "status_change");
                jSONObject2.put("code", 4);
                m1822c("javascript:applican._successCallback(" + c0606et.f1957e + ", " + jSONObject2.toString() + ", true);");
            } catch (Exception e6) {
            }
        } catch (Exception e7) {
            this.f1534l.m1853a("2>>" + e7.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: F */
    public void m1653F() {
        if (this.f1481ad != null) {
            try {
                this.f1481ad.pause();
                this.f1481ad.setMediaController(null);
                this.f1482ae.removeView(this.f1481ad);
                this.f1481ad = null;
                this.f1538p.removeView(this.f1482ae);
                this.f1482ae = null;
            } catch (Exception e) {
                this.f1534l.m1853a("stopVideo:" + e.getMessage());
            }
            try {
                this.f1483af.hide();
            } catch (Exception e2) {
            }
        }
    }

    /* renamed from: G */
    private boolean m1654G() {
        try {
            return !BluetoothAdapter.getDefaultAdapter().equals(null);
        } catch (Exception e) {
            return false;
        }
    }

    /* renamed from: H */
    private boolean m1655H() {
        try {
            return BluetoothAdapter.getDefaultAdapter().isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: I */
    public void m1656I() {
        this.f1515bk = true;
        try {
            this.f1514bj.close();
        } catch (Exception e) {
        }
        this.f1514bj = null;
        try {
            this.f1513bi.close();
        } catch (Exception e2) {
        }
        this.f1513bi = null;
        try {
            this.f1512bh.close();
        } catch (Exception e3) {
        }
        this.f1512bh = null;
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.isDiscovering()) {
                defaultAdapter.cancelDiscovery();
            }
        } catch (Exception e4) {
        }
    }

    /* renamed from: a */
    public static float m1657a(int i, int i2, int i3, int i4) {
        if (i < i2) {
            if (i3 >= i4) {
                return i / i3;
            }
            float f = i2 / i4;
            return ((float) i3) * f > ((float) i) ? i / i3 : f;
        }
        if (i3 < i4) {
            return i2 / i4;
        }
        float f2 = i / i3;
        return ((float) i4) * f2 > ((float) i2) ? i2 / i4 : f2;
    }

    /* renamed from: a */
    private long m1659a(SQLiteDatabase sQLiteDatabase) {
        try {
            Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT last_insert_rowid()", null);
            r0 = rawQuery.moveToFirst() ? rawQuery.getLong(0) : -1L;
            rawQuery.close();
        } catch (Exception e) {
        }
        return r0;
    }

    /* renamed from: a */
    private PendingIntent m1661a(Context context, int i, String str, String str2) {
        Intent intent = new Intent(context, (Class<?>) NotificationReceiver.class);
        intent.putExtra("alertId", i);
        intent.putExtra("alertBody", str);
        intent.putExtra("uri", str2);
        return PendingIntent.getBroadcast(context, i, intent, 0);
    }

    /* renamed from: a */
    public static String m1665a(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        query.moveToFirst();
        return query.getString(0);
    }

    /* renamed from: a */
    private void m1666a(int i) {
        String str = DateFormat.format("yyyy-MM-dd_kk.mm.ss", System.currentTimeMillis()).toString() + ".jpg";
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues(5);
        contentValues.put("mime_type", "image/jpeg");
        contentValues.put("date_modified", Long.valueOf(System.currentTimeMillis() / 1000));
        contentValues.put("title", str);
        contentValues.put("_display_name", str);
        contentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        Uri insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (this.f1522br == null) {
            this.f1522br = getPreferences(0);
        }
        SharedPreferences.Editor edit = this.f1522br.edit();
        edit.putString("pictureUri", insert.toString());
        edit.commit();
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", insert);
        startActivityForResult(intent, i);
    }

    /* renamed from: a */
    private void m1667a(int i, String str) {
        switch (i) {
            case C0650c.PullToRefresh_ptrRefreshableViewBackground /* 0 */:
                m1822c("javascript:applican.capture._captureWithOverlayCancelled('');");
                return;
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                m1822c("javascript:applican.capture._captureWithOverlaySuccess('');");
                return;
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                File file = new File(str);
                String str2 = "";
                try {
                    JSONArray jSONArray = new JSONArray();
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("fullPath", "file://" + str);
                    jSONObject.put("name", file.getName());
                    jSONObject.put("lastModifiedDate", (int) (((float) file.lastModified()) / 1000.0f));
                    jSONObject.put("type", m1708d(str));
                    jSONObject.put("size", file.length());
                    jSONArray.put(jSONObject);
                    str2 = jSONArray.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                m1822c("javascript:applican.capture._captureWithOverlaySuccess('" + str2 + "');");
                return;
            case C0650c.PullToRefresh_ptrHeaderSubTextColor /* 3 */:
                m1822c("javascript:applican.capture._captureWithOverlaySuccess('" + str + "');");
                return;
            default:
                return;
        }
    }

    /* renamed from: a */
    private void m1668a(Context context, int i) {
        String m1574b = C0454a.m1574b(context, "localNotification");
        JSONArray jSONArray = null;
        JSONArray jSONArray2 = new JSONArray();
        if (m1574b != null && m1574b.length() > 0) {
            try {
                jSONArray = new JSONArray(m1574b);
            } catch (Exception e) {
            }
        }
        if (jSONArray != null) {
            try {
                int length = jSONArray.length();
                for (int i2 = 0; i2 < length; i2++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i2);
                    if (i != jSONObject.getInt("alertId")) {
                        jSONArray2.put(jSONObject);
                    }
                }
            } catch (Exception e2) {
            }
            if (jSONArray2.length() > 0) {
                C0454a.m1575b(context, "localNotification", jSONArray2.toString());
            } else {
                C0454a.m1575b(context, "localNotification", "");
            }
        }
    }

    /* renamed from: a */
    private void m1669a(String str) {
        this.f1536n.post(new RunnableC0539cg(this, str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1670a(String str, JSONObject jSONObject) {
        if (str.equals("getCurrentHeading")) {
            if (this.f1500aw > 0) {
                return;
            }
            this.f1500aw = 1000;
            m1777o();
            return;
        }
        if (!str.equals("watchHeading")) {
            if (str.equals("clearWatch")) {
                try {
                    this.f1501ax.cancel();
                } catch (Exception e) {
                }
                m1782p();
                this.f1500aw = 0;
                return;
            }
            return;
        }
        if (this.f1500aw <= 0) {
            this.f1500aw = 1001;
            long j = 1000;
            if (jSONObject != null) {
                try {
                    long j2 = jSONObject.getLong("frequency");
                    if (j2 <= 0) {
                        j2 = 1000;
                    }
                    j = j2;
                } catch (Exception e2) {
                }
            }
            this.f1454aC = j;
            m1785q();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    /* renamed from: a */
    public void m1671a(String str, JSONObject jSONObject, int i) {
        if (str.equals("getPushToken")) {
            String m1574b = C0454a.m1574b(this.f1535m, "gcm_id");
            m1822c((m1574b == null || m1574b.length() <= 0) ? "javascript:applican.device._getPushTokenError();" : "javascript:applican.device._getPushTokenSuccess('" + m1574b + "');");
            return;
        }
        if (str.equals("light")) {
            try {
                if (!jSONObject.getBoolean("enable")) {
                    if (this.f1474aW != null) {
                        Camera.Parameters parameters = this.f1474aW.getParameters();
                        parameters.setFlashMode("off");
                        this.f1474aW.setParameters(parameters);
                        this.f1474aW.stopPreview();
                        this.f1474aW.release();
                        this.f1474aW = null;
                        return;
                    }
                    return;
                }
                if (this.f1474aW == null) {
                    this.f1474aW = Camera.open();
                    if (Build.VERSION.SDK_INT >= 11) {
                        this.f1474aW.setPreviewTexture(new SurfaceTexture(0));
                    }
                    this.f1474aW.startPreview();
                    Camera.Parameters parameters2 = this.f1474aW.getParameters();
                    parameters2.setFlashMode("torch");
                    this.f1474aW.setParameters(parameters2);
                    return;
                }
                return;
            } catch (Exception e) {
                this.f1534l.m1853a(e.getMessage());
                return;
            }
        }
        if (str.equals("keepScreenOn")) {
            try {
                boolean z = jSONObject.getBoolean("enable");
                this.f1534l.m1853a("keepScreenOn>" + z);
                if (z) {
                    this.f1534l.f1656s.getWindow().addFlags(128);
                } else {
                    this.f1534l.f1656s.getWindow().clearFlags(128);
                }
                return;
            } catch (Exception e2) {
                this.f1534l.m1853a(e2.getMessage());
                return;
            }
        }
        if (!str.equals("getDisplayInfo")) {
            if ("getAdvertisingId".equals(str)) {
                C0488aj.m1873a().m1874a(this, i);
                return;
            }
            return;
        }
        try {
            JSONObject jSONObject2 = new JSONObject();
            try {
                Configuration configuration = getResources().getConfiguration();
                String str2 = "";
                if (configuration.orientation == 2) {
                    str2 = "LANDSCAPE";
                } else if (configuration.orientation == 1) {
                    str2 = "PORTRAIT";
                }
                jSONObject2.put("orientation", str2);
                jSONObject2.put("width", this.f1540r.getWidth());
                jSONObject2.put("height", this.f1540r.getHeight());
                jSONObject2.put("scale", this.f1534l.f1642e);
            } catch (Exception e3) {
            }
            m1822c("javascript:applican._successCallback(" + i + ", " + jSONObject2.toString() + ");");
        } catch (Exception e4) {
            this.f1534l.m1853a(e4.getMessage());
            m1822c("javascript:applican._failCallback(" + i + ", null);");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1672a(String str, JSONObject jSONObject, String str2) {
        if (this.f1441Q) {
            return;
        }
        this.f1441Q = true;
        if (str.equals("getPicture")) {
            this.f1528f = jSONObject;
            m1651D();
            return;
        }
        if (str.equals("cleanup")) {
            try {
                C0454a.m1572a(new File(m1649B()));
                m1822c("javascript:applican.camera._cleanupPictureSuccess();");
            } catch (Exception e) {
                m1822c("javascript:applican.camera._cleanupPictureError('" + m1717e(e.getMessage()) + "');");
            }
            this.f1441Q = false;
            return;
        }
        if (str.equals("takePicture")) {
            String str3 = "";
            String str4 = "camera";
            String str5 = "";
            String str6 = "";
            if (jSONObject != null) {
                try {
                    str3 = jSONObject.getString("mode");
                    if (str3.equals("send")) {
                        str4 = jSONObject.getString("source");
                        str5 = jSONObject.getString("url");
                        str6 = jSONObject.getString("name");
                    }
                } catch (Exception e2) {
                }
            }
            if (str3.equals("save")) {
                m1666a(4);
                return;
            }
            if (!str3.equals("send") || str4 == null || str4.length() <= 0 || str5 == null || str5.length() <= 0 || str6 == null || str6.length() <= 0) {
                m1822c("javascript:applican.camera._takePictureError();");
                return;
            }
            this.f1519bo = str5;
            this.f1520bp = str6;
            if (str4.equals("library")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.PICK");
                startActivityForResult(intent, 3);
                return;
            }
            boolean z = false;
            try {
                z = jSONObject.getBoolean("local_save");
            } catch (Exception e3) {
            }
            if (z) {
                m1666a(5);
                return;
            } else {
                m1666a(2);
                return;
            }
        }
        if (str.equals("saveToPhotoAlbum")) {
            try {
                if (str2.startsWith("file://")) {
                    String substring = str2.substring(7);
                    File file = new File(substring);
                    byte[] bArr = new byte[(int) file.length()];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    fileInputStream.read(bArr);
                    fileInputStream.close();
                    boolean z2 = substring.endsWith(".png");
                    long currentTimeMillis = System.currentTimeMillis();
                    String str7 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
                    File file2 = new File(str7);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String format = new SimpleDateFormat("yyyyMMdd_HHmmss_S").format(new Date(currentTimeMillis));
                    String str8 = format + (z2 ? ".png" : ".jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(str7 + "/" + str8));
                    fileOutputStream.write(bArr);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("title", format);
                    contentValues.put("_display_name", str8);
                    contentValues.put("mime_type", "image/" + (z2 ? "png" : "jpeg"));
                    contentValues.put("_data", str7 + "/" + str8);
                    contentValues.put("datetaken", Long.valueOf(currentTimeMillis));
                    contentValues.put("_size", Integer.valueOf(bArr.length));
                    if (!z2) {
                        contentValues.put("orientation", Integer.valueOf(m1739h(substring)));
                    }
                    this.f1534l.m1853a(getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).getPath());
                } else {
                    this.f1534l.m1853a("" + str2.length());
                    byte[] decode = Base64.decode(str2, 0);
                    this.f1534l.m1853a("" + str2.length() + ", " + decode.length);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(decode, 0, decode.length, options);
                    int i = options.outWidth;
                    int i2 = options.outHeight;
                    String str9 = options.outMimeType;
                    this.f1534l.m1853a("" + str9);
                    long currentTimeMillis2 = System.currentTimeMillis();
                    String str10 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
                    File file3 = new File(str10);
                    if (!file3.exists()) {
                        file3.mkdirs();
                    }
                    String format2 = new SimpleDateFormat("yyyyMMdd_HHmmss_S").format(new Date(currentTimeMillis2));
                    String str11 = format2 + (str9.equals("image/png") ? ".png" : ".jpg");
                    FileOutputStream fileOutputStream2 = new FileOutputStream(new File(str10 + "/" + str11));
                    fileOutputStream2.write(decode);
                    fileOutputStream2.flush();
                    fileOutputStream2.close();
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("title", format2);
                    contentValues2.put("_display_name", str11);
                    contentValues2.put("mime_type", str9);
                    contentValues2.put("_data", str10 + "/" + str11);
                    contentValues2.put("datetaken", Long.valueOf(currentTimeMillis2));
                    contentValues2.put("_size", Integer.valueOf(decode.length));
                    this.f1534l.m1853a(getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues2).getPath());
                }
                m1822c("javascript:applican.camera._saveToPhotoAlbumSuccess();");
            } catch (Exception e4) {
                m1822c("javascript:applican.camera._saveToPhotoAlbumError('" + m1717e(e4.getMessage()) + "');");
            }
            this.f1441Q = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1673a(String str, boolean z) {
        if (!this.f1435K || this.f1540r == null) {
            return;
        }
        if (z && this.f1534l.f1630G) {
            return;
        }
        if (!z || this.f1518bn) {
            try {
                this.f1534l.m1853a(">>>>>3:" + str);
                this.f1536n.post(new RunnableC0541ci(this, str));
            } catch (Exception e) {
                this.f1534l.m1853a("sendResponse Exception:" + e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m1674a(C0464k c0464k, float f) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f);
        alphaAnimation.setFillAfter(true);
        c0464k.startAnimation(alphaAnimation);
    }

    /* renamed from: a */
    private void m1682a(boolean z) {
        String message;
        boolean z2;
        String str;
        long j;
        String str2 = "";
        if (this.f1463aL != null) {
            try {
                this.f1463aL.removeUpdates(this);
                this.f1463aL = null;
            } catch (Exception e) {
                str2 = e.getMessage();
            }
        }
        try {
            this.f1463aL = null;
            this.f1463aL = (LocationManager) this.f1535m.getSystemService("location");
            message = str2;
        } catch (Exception e2) {
            message = e2.getMessage();
        }
        if (this.f1463aL == null) {
            if (this.f1464aM == 3000) {
                m1822c("javascript:applican.geolocation._getCurrentPositionError('PERMISSION_DENIED', '" + m1717e(message) + "');");
            } else if (this.f1464aM == 3001) {
                m1822c("javascript:applican.geolocation._watchPositionError('PERMISSION_DENIED', '" + m1717e(message) + "');");
            }
            this.f1464aM = 0;
            return;
        }
        String bestProvider = this.f1463aL.getBestProvider(new Criteria(), true);
        this.f1534l.m1853a("bestProvider 0 >>> " + bestProvider);
        try {
            z2 = this.f1463aL.isProviderEnabled("gps");
        } catch (Exception e3) {
            if (e3.getLocalizedMessage() != null) {
                Log.e("error", "error getting GPS status: " + e3.getLocalizedMessage());
            }
            z2 = false;
        }
        if (bestProvider == null || !z2) {
            if (this.f1464aM == 3000) {
                m1822c("javascript:applican.geolocation._getCurrentPositionError('POSITION_UNAVAILABLE', '');");
            } else if (this.f1464aM == 3001) {
                m1822c("javascript:applican.geolocation._watchPositionError('POSITION_UNAVAILABLE', '');");
            }
            this.f1464aM = 0;
            return;
        }
        boolean z3 = false;
        boolean z4 = false;
        String string = Settings.Secure.getString(getContentResolver(), "location_providers_allowed");
        this.f1534l.m1853a("gpsStatus>>> " + string);
        if (string.indexOf("gps") >= 0) {
            z3 = true;
            this.f1534l.m1853a("isGps>>> true");
        }
        boolean z5 = z3;
        if (string.indexOf("network") >= 0) {
            this.f1534l.m1853a("isNetwork>>> true");
            z4 = true;
        }
        Location location = null;
        long j2 = Long.MAX_VALUE;
        for (String str3 : this.f1463aL.getAllProviders()) {
            this.f1534l.m1853a("provider>>> " + str3);
            Location lastKnownLocation = this.f1463aL.getLastKnownLocation(str3);
            long currentTimeMillis = System.currentTimeMillis();
            if (lastKnownLocation != null) {
                j = currentTimeMillis - lastKnownLocation.getTime();
                if (j < this.f1471aT && j < j2) {
                    j2 = j;
                    location = lastKnownLocation;
                }
            }
            j = j2;
            lastKnownLocation = location;
            j2 = j;
            location = lastKnownLocation;
        }
        if (this.f1470aS) {
            if (z5) {
                str = "gps";
            } else {
                if (z4) {
                    str = "network";
                }
                str = bestProvider;
            }
        } else if (z4) {
            str = "network";
        } else {
            if (z5) {
                str = "gps";
            }
            str = bestProvider;
        }
        this.f1534l.m1853a(">>bestProvider 1:" + str);
        if (location != null) {
            this.f1534l.m1853a(">>>location_tmp :" + location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getTime() + ", " + j2);
            if (this.f1464aM == 3000) {
                str = null;
            }
            onLocationChanged(location);
        }
        String str4 = str;
        if (str4 != null) {
            this.f1468aQ = new Timer(true);
            this.f1468aQ.schedule(new C0551cs(this), this.f1469aR);
            try {
                this.f1534l.m1853a("LocationUpdates " + str4 + ", " + this.f1466aO);
                this.f1463aL.requestLocationUpdates(str4, this.f1466aO, 0.0f, this);
            } catch (Exception e4) {
                this.f1534l.m1853a(e4.getMessage());
            }
        }
    }

    /* renamed from: a */
    private void m1683a(float[] fArr, float[] fArr2) {
        SensorManager.getOrientation(fArr, fArr2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public boolean m1684a(String str, WebView webView) {
        String str2;
        if (str.indexOf("launch_browser=yes") >= 0 || str.startsWith("geo:") || str.startsWith("market:") || str.startsWith("mailto:") || str.startsWith("tel:")) {
            if (webView != null) {
                webView.stopLoading();
            }
            if (str.indexOf("launch_browser=yes") >= 0) {
                str2 = str.indexOf("launch_browser=yes&") >= 0 ? str.replace("launch_browser=yes&", "") : str.replace("launch_browser=yes", "");
                if (str2.endsWith("?")) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
            } else {
                str2 = str;
            }
            try {
                this.f1441Q = true;
                C0454a.m1568a(this.f1535m, str2);
            } catch (Exception e) {
                this.f1441Q = false;
                C0454a.m1569a(this.f1535m, "Error", getResources().getString(R.string.LabelCannotLaunchtheapplication) + "\n" + str2);
            }
            return true;
        }
        if ((str.indexOf("launch_webview=yes") >= 0 || str.indexOf("launch_webview=yes_without_toolbar") >= 0) && !this.f1478aa) {
            if (webView != null) {
                webView.stopLoading();
            }
            this.f1534l.f1661x = this.f1535m;
            Intent intent = new Intent(this.f1535m, (Class<?>) WebActivity.class);
            intent.putExtra("launchWebViewUrl", str);
            if (str.indexOf("launch_webview=yes_without_toolbar") >= 0) {
                intent.putExtra("toolbar", "no");
            } else {
                intent.putExtra("toolbar", "yes");
            }
            startActivity(intent);
            return true;
        }
        if (str.indexOf("close_webview=yes") >= 0) {
            if (webView != null) {
                webView.stopLoading();
            }
            if (this.f1478aa) {
                m1804w();
                finish();
                if (this.f1534l.f1661x != null) {
                    try {
                        this.f1534l.f1661x.m1823d();
                        this.f1534l.f1661x = null;
                    } catch (Exception e2) {
                    }
                }
            }
            return true;
        }
        if (str.indexOf("launch_tab=") < 0) {
            return false;
        }
        if (webView != null) {
            webView.stopLoading();
        }
        try {
            String substring = str.substring(str.indexOf("launch_tab=") + 11);
            if (substring.indexOf("&") >= 0) {
                substring = substring.substring(0, substring.indexOf("&"));
            }
            int parseInt = Integer.parseInt(substring);
            boolean z = str.indexOf("/?launch_tab=") >= 0;
            if (parseInt > 0 && parseInt < 99) {
                String str3 = "launch_tab=" + parseInt;
                String replace = str.indexOf(new StringBuilder().append(str3).append("&").toString()) >= 0 ? str.replace(str3 + "&", "") : str.replace(str3, "");
                if (replace.endsWith("?")) {
                    replace = replace.substring(0, replace.length() - 1);
                }
                this.f1534l.f1656s.m1908a(parseInt, replace, z);
                if (this.f1478aa) {
                    finish();
                }
            }
        } catch (Exception e3) {
        }
        return true;
    }

    /* renamed from: b */
    private long m1687b(SQLiteDatabase sQLiteDatabase) {
        try {
            Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT changes()", null);
            r0 = rawQuery.moveToFirst() ? rawQuery.getLong(0) : -1L;
            rawQuery.close();
        } catch (Exception e) {
        }
        return r0;
    }

    /* renamed from: b */
    private void m1690b(int i, String str) {
        if (this.f1477aZ) {
            return;
        }
        this.f1477aZ = true;
        this.f1506bb = i;
        this.f1505ba = System.currentTimeMillis();
        this.f1476aY = new MediaRecorder();
        this.f1476aY.setAudioSource(1);
        this.f1476aY.setOutputFormat(2);
        this.f1476aY.setAudioEncoder(3);
        this.f1476aY.setOutputFile(str);
        try {
            this.f1476aY.prepare();
        } catch (Exception e) {
        }
        this.f1476aY.start();
        try {
            C0606et c0606et = (C0606et) this.f1447W.get(Integer.valueOf(i));
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("status", "status_change");
            jSONObject.put("code", 2);
            m1822c("javascript:applican._successCallback(" + c0606et.f1957e + ", " + jSONObject.toString() + ", true);");
        } catch (Exception e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m1691b(String str, JSONObject jSONObject) {
        if (str.equals("getCurrentAcceleration")) {
            if (this.f1455aD > 0) {
                return;
            }
            this.f1455aD = 2000;
            m1789r();
            return;
        }
        if (str.equals("watchAcceleration")) {
            if (this.f1455aD <= 0) {
                this.f1455aD = 2001;
                long j = 1000;
                if (jSONObject != null) {
                    try {
                        long j2 = jSONObject.getLong("frequency");
                        if (j2 <= 0) {
                            j2 = 1000;
                        }
                        j = j2;
                    } catch (Exception e) {
                    }
                }
                this.f1461aJ = j;
                m1795t();
                return;
            }
            return;
        }
        if (str.equals("clearWatch")) {
            try {
                this.f1456aE.cancel();
            } catch (Exception e2) {
            }
            m1792s();
            this.f1455aD = 0;
        } else {
            if (str.equals("watchShake")) {
                if (this.f1462aK) {
                    return;
                }
                this.f1462aK = true;
                this.f1504b = (SensorManager) getSystemService("sensor");
                this.f1504b.registerListener(this.f1533k, this.f1504b.getDefaultSensor(1), 1);
                return;
            }
            if (str.equals("clearWatchShake")) {
                this.f1462aK = false;
                try {
                    this.f1504b.unregisterListener(this.f1533k);
                } catch (Exception e3) {
                }
                this.f1504b = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: b */
    public void m1692b(String str, JSONObject jSONObject, int i) {
        String str2;
        File file;
        long j;
        String str3;
        long j2;
        long j3;
        long j4;
        String str4;
        String str5;
        boolean z;
        boolean z2;
        String str6;
        String str7;
        boolean z3;
        boolean z4;
        long j5 = 0;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        boolean z8 = false;
        Object[] objArr = null;
        boolean z9 = false;
        z5 = false;
        z5 = false;
        if (str.equals("requestFileSystem")) {
            String str8 = "";
            try {
                str8 = jSONObject.getString("type");
            } catch (Exception e) {
            }
            String str9 = "";
            String str10 = "";
            if (str8.equals("persistent")) {
                str9 = "file://" + Environment.getExternalStorageDirectory().getPath();
                str10 = Environment.getExternalStorageDirectory().getName();
            } else if (str8.equals("temporary")) {
                File file2 = new File(getFilesDir() + "/cache");
                if (!file2.exists()) {
                    file2.mkdir();
                }
                str9 = "file://" + file2.getPath();
                str10 = file2.getName();
            }
            m1822c("javascript:applican._requestFileSystemSuccess('" + str8 + "', '" + str10 + "', '" + str9 + "');");
            return;
        }
        if (str.equals("getFile")) {
            String str11 = "";
            try {
                str11 = jSONObject.getString("fullPath");
                if (str11.startsWith("file:///")) {
                    str11 = str11.substring(7);
                }
                str6 = jSONObject.getString("filePath");
                str7 = str11;
            } catch (Exception e2) {
                str6 = "";
                str7 = str11;
            }
            try {
                z3 = jSONObject.getBoolean("create");
            } catch (Exception e3) {
                z3 = false;
            }
            try {
                z4 = jSONObject.getBoolean("exclusive");
            } catch (Exception e4) {
                z4 = false;
            }
            File file3 = new File(new File(str7), str6);
            if (!file3.exists() && !z3) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:1});");
                return;
            }
            if (!file3.exists() && z3) {
                try {
                    z8 = file3.createNewFile();
                } catch (Exception e5) {
                }
                if (!z8) {
                    m1822c("javascript:applican._failCallback(" + i + ", {code:2});");
                    return;
                }
            } else if (file3.exists() && z3 && z4) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:12});");
                return;
            }
            m1822c("javascript:applican._successCallback(" + i + ", {name:'" + file3.getName() + "', fullPath:'" + ("file://" + file3.getPath()) + "'});");
            return;
        }
        if (str.equals("getDirectory")) {
            String str12 = "";
            try {
                str12 = jSONObject.getString("fullPath");
                if (str12.startsWith("file:///")) {
                    str12 = str12.substring(7);
                }
                str4 = str12;
                str5 = jSONObject.getString("path");
            } catch (Exception e6) {
                str4 = str12;
                str5 = "";
            }
            try {
                z = jSONObject.getBoolean("create");
            } catch (Exception e7) {
                z = false;
            }
            try {
                z2 = jSONObject.getBoolean("exclusive");
            } catch (Exception e8) {
                z2 = false;
            }
            File file4 = new File(new File(str4), str5);
            if (!file4.exists() && !z) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:1});");
                return;
            }
            if (!file4.exists() && z) {
                try {
                    z7 = file4.mkdir();
                } catch (Exception e9) {
                }
                if (!z7) {
                    m1822c("javascript:applican._failCallback(" + i + ", {code:2});");
                    return;
                }
            } else if (file4.exists() && z && z2) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:12});");
                return;
            }
            m1822c("javascript:applican._successCallback(" + i + ", {name:'" + file4.getName() + "', fullPath:'file://" + file4.getPath() + "'});");
            return;
        }
        if (str.equals("createWriter")) {
            String str13 = "";
            String str14 = "";
            try {
                str13 = jSONObject.getString("fullPath");
                if (str13.startsWith("file:///")) {
                    str13 = str13.substring(7);
                }
                str14 = jSONObject.getString("filePath");
            } catch (Exception e10) {
            }
            File file5 = new File(new File(str13), str14);
            String str15 = "file://" + file5.getPath();
            String name = file5.getName();
            if (file5.exists()) {
                j5 = file5.lastModified();
                j4 = file5.length();
            } else {
                j4 = 0;
            }
            if (!file5.canRead() && file5.canWrite()) {
            }
            m1822c("javascript:applican._successCallback(" + i + ", {name:'" + name + "', fullPath:'" + str15 + "', type:'type', lastModifiedDate:" + j5 + ", size:" + j4 + "});");
            return;
        }
        if (str.equals("write")) {
            String str16 = "";
            try {
                str16 = jSONObject.getString("text");
            } catch (Exception e11) {
            }
            try {
                String string = jSONObject.getString("fileName");
                if (string.startsWith("file:///")) {
                    string = string.substring(7);
                }
                str3 = string;
            } catch (Exception e12) {
                str3 = "";
            }
            try {
                j2 = jSONObject.getLong("position");
            } catch (Exception e13) {
                j2 = 0;
            }
            try {
                j3 = jSONObject.getLong("length");
            } catch (Exception e14) {
                j3 = 0;
            }
            new C0588eb(this, i, str16, str3, j2, j3).start();
            return;
        }
        if (str.equals("file")) {
            String str17 = "";
            String str18 = "";
            try {
                str17 = jSONObject.getString("fullPath");
                if (str17.startsWith("file:///")) {
                    str17 = str17.substring(7);
                }
                str18 = jSONObject.getString("filePath");
            } catch (Exception e15) {
            }
            File file6 = new File(new File(str17), str18);
            String str19 = "file://" + file6.getPath();
            String name2 = file6.getName();
            if (file6.exists()) {
                j = file6.lastModified();
                j5 = file6.length();
            } else {
                j = 0;
            }
            if (!file6.canRead() && file6.canWrite()) {
            }
            m1822c("javascript:applican._successCallback(" + i + ", {name:'" + name2 + "', fullPath:'" + str19 + "', type:'type', lastModifiedDate:" + j + ", size:" + j5 + "});");
            return;
        }
        if (str.equals("readAsText") || str.equals("readAsDataURL")) {
            String str20 = "";
            String str21 = "";
            try {
                str20 = jSONObject.getString("fullPath");
                if (str20.startsWith("file:///")) {
                    str20 = str20.substring(7);
                }
                str21 = jSONObject.getString("filePath");
            } catch (Exception e16) {
            }
            new C0583dx(this, i, str, str20, str21).start();
            return;
        }
        if (str.equals("readEntries")) {
            String str22 = "";
            try {
                str22 = jSONObject.getString("path");
                if (str22.startsWith("file:///")) {
                    str22 = str22.substring(7);
                }
            } catch (Exception e17) {
            }
            File[] listFiles = new File(str22).listFiles();
            StringBuffer stringBuffer = new StringBuffer();
            if (listFiles == null) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:1});");
                return;
            }
            for (int i2 = 0; i2 < listFiles.length; i2++) {
                if (i2 == 0) {
                    stringBuffer.append("[");
                } else {
                    stringBuffer.append(",");
                }
                File file7 = listFiles[i2];
                stringBuffer.append("{isDirectory:" + file7.isDirectory() + ", isFile:" + file7.isFile() + ", name:'" + file7.getName() + "', fullPath:'file://" + file7.getPath() + "'}");
            }
            stringBuffer.append("]");
            m1822c("javascript:applican._successCallback(" + i + ", " + stringBuffer.toString() + ");");
            return;
        }
        if (str.equals("remove") || str.equals("removeRecursively")) {
            try {
                String string2 = jSONObject.getString("fullPath");
                if (string2.startsWith("file:///")) {
                    string2 = string2.substring(7);
                }
                File file8 = new File(string2);
                if (str.equals("remove")) {
                    z5 = file8.delete();
                } else if (str.equals("removeRecursively")) {
                    z5 = C0454a.m1572a(file8);
                }
            } catch (Exception e18) {
                if (e18.getLocalizedMessage() != null) {
                    this.f1534l.m1853a("Exception>>" + e18.getLocalizedMessage());
                }
            }
            if (z5) {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            } else {
                m1822c("javascript:applican._failCallback(" + i + ", {code:2});");
                return;
            }
        }
        if (str.equals("moveTo") || str.equals("copyTo")) {
            try {
                String string3 = jSONObject.getString("fullPath");
                String substring = string3.startsWith("file:///") ? string3.substring(7) : string3;
                String string4 = jSONObject.getString("parentPath");
                if (string4.startsWith("file:///")) {
                    string4 = string4.substring(7);
                }
                String string5 = jSONObject.getString("newName");
                File file9 = new File(substring);
                File file10 = new File(string4);
                if (str.equals("moveTo")) {
                    z9 = file9.renameTo(new File(file10, string5));
                } else {
                    C0454a.m1570a(file9, new File(file10, string5));
                    z9 = true;
                }
                str2 = file9.isFile() ? "{name:'" + string5 + "', fullPath:'file://" + string4 + "/" + string5 + "', type:'type', lastModifiedDate:" + file9.lastModified() + ", size:" + file9.length() + "}" : "{name:'" + string5 + "', fullPath:'file://" + string4 + "/" + string5 + "', lastModifiedDate:" + file9.lastModified() + "}";
            } catch (Exception e19) {
                if (e19.getLocalizedMessage() != null) {
                    this.f1534l.m1853a("Exception>>" + e19.getLocalizedMessage());
                }
                str2 = "";
            }
            if (z9) {
                m1822c("javascript:applican._successCallback(" + i + ", " + str2 + ");");
                return;
            } else {
                m1822c("javascript:applican._failCallback(" + i + ", {code:2});");
                return;
            }
        }
        if (str.equals("getParent")) {
            try {
                String string6 = jSONObject.getString("fullPath");
                if (string6.startsWith("file:///")) {
                    string6 = string6.substring(7);
                }
                file = new File(string6).getParentFile();
                objArr = 1;
            } catch (Exception e20) {
                if (e20.getLocalizedMessage() != null) {
                    this.f1534l.m1853a("Exception>>" + e20.getLocalizedMessage());
                }
                file = null;
            }
            if (objArr == null || file == null) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:2});");
                return;
            } else {
                m1822c("javascript:applican._successCallback(" + i + ", {name:'" + file.getName() + "', fullPath:'file://" + file.getPath() + "'});");
                return;
            }
        }
        if (str.equals("upload")) {
            try {
                m1696b(jSONObject.getInt("id"));
            } catch (Exception e21) {
            }
            AsyncTaskC0595ei asyncTaskC0595ei = new AsyncTaskC0595ei(this, i, jSONObject);
            asyncTaskC0595ei.execute(0);
            try {
                this.f1446V.put(Integer.valueOf(jSONObject.getInt("id")), asyncTaskC0595ei);
                return;
            } catch (Exception e22) {
                if (e22.getLocalizedMessage() != null) {
                    this.f1534l.m1853a(e22.getLocalizedMessage());
                    return;
                }
                return;
            }
        }
        if (str.equals("download")) {
            try {
                m1696b(jSONObject.getInt("id"));
            } catch (Exception e23) {
            }
            AsyncTaskC0577dr asyncTaskC0577dr = new AsyncTaskC0577dr(this, i, jSONObject);
            asyncTaskC0577dr.execute(0);
            try {
                this.f1446V.put(Integer.valueOf(jSONObject.getInt("id")), asyncTaskC0577dr);
                return;
            } catch (Exception e24) {
                if (e24.getLocalizedMessage() != null) {
                    this.f1534l.m1853a(e24.getLocalizedMessage());
                    return;
                }
                return;
            }
        }
        if (str.equals("ft_abort")) {
            try {
                z6 = m1696b(jSONObject.getInt("id"));
            } catch (Exception e25) {
            }
            if (z6) {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            } else {
                m1822c("javascript:applican._failCallback(" + i + ", null);");
                return;
            }
        }
        if (str.equals("getApplicationExternalCacheRoot")) {
            try {
                m1822c("javascript:applican._getApplicationExternalCacheRootSuccess('" + this.f1535m.getExternalCacheDir().getPath() + "');");
                return;
            } catch (Exception e26) {
                if (e26.getLocalizedMessage() != null) {
                    Log.e("Error", e26.getLocalizedMessage());
                }
                m1822c("javascript:applican._getApplicationExternalCacheRootError();");
                return;
            }
        }
        if (str.equals("getApplicationCacheRoot")) {
            try {
                m1822c("javascript:applican._getApplicationCacheRootSuccess('" + this.f1535m.getCacheDir().getPath() + "');");
                return;
            } catch (Exception e27) {
                if (e27.getLocalizedMessage() != null) {
                    Log.e("Error", e27.getLocalizedMessage());
                }
                m1822c("javascript:applican._getApplicationCacheRootError();");
                return;
            }
        }
        if (str.equals("getApplicationExternalFilesRoot")) {
            try {
                m1822c("javascript:applican._getApplicationExternalFilesRootSuccess('" + this.f1535m.getExternalFilesDir(null).getPath() + "');");
                return;
            } catch (Exception e28) {
                if (e28.getLocalizedMessage() != null) {
                    Log.e("Error", e28.getLocalizedMessage());
                }
                m1822c("javascript:applican._getApplicationExternalFilesRootError();");
                return;
            }
        }
        if (str.equals("getApplicationFilesRoot")) {
            try {
                m1822c("javascript:applican._getApplicationFilesRootSuccess('" + this.f1535m.getFilesDir().getPath() + "');");
            } catch (Exception e29) {
                if (e29.getLocalizedMessage() != null) {
                    Log.e("Error", e29.getLocalizedMessage());
                }
                m1822c("javascript:applican._getApplicationFilesRootError();");
            }
        }
    }

    /* renamed from: b */
    private boolean m1696b(int i) {
        try {
            ((AsyncTask) this.f1446V.get(Integer.valueOf(i))).cancel(false);
            this.f1446V.remove(Integer.valueOf(i));
            return true;
        } catch (Exception e) {
            if (e.getLocalizedMessage() != null) {
                Log.e("Error", e.getLocalizedMessage());
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public String m1699c(int i) {
        switch (i) {
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                return "home";
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                return "mobile";
            case C0650c.PullToRefresh_ptrHeaderSubTextColor /* 3 */:
                return "work";
            case C0650c.PullToRefresh_ptrMode /* 4 */:
                return "workFax";
            case C0650c.PullToRefresh_ptrShowIndicator /* 5 */:
                return "homeFax";
            case C0650c.PullToRefresh_ptrDrawable /* 6 */:
                return "pager";
            case C0650c.PullToRefresh_ptrDrawableStart /* 7 */:
                return "other";
            default:
                return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m1701c(String str, JSONObject jSONObject) {
        if (str.equals("getCurrentPosition")) {
            if (this.f1464aM > 0) {
                return;
            }
            this.f1464aM = 3000;
            this.f1467aP = false;
            this.f1469aR = 60000L;
            this.f1470aS = false;
            this.f1471aT = 0L;
            this.f1466aO = 0L;
            if (jSONObject != null) {
                try {
                    long j = jSONObject.getLong("timeout");
                    if (j > 0) {
                        this.f1469aR = j;
                    }
                } catch (Exception e) {
                }
                try {
                    long j2 = jSONObject.getLong("maximumAge");
                    if (j2 > 0) {
                        this.f1471aT = j2;
                    }
                } catch (Exception e2) {
                }
                try {
                    this.f1470aS = jSONObject.getBoolean("enableHighAccuracy");
                } catch (Exception e3) {
                }
            }
            m1682a(false);
            return;
        }
        if (!str.equals("watchPosition")) {
            if (str.equals("clearWatch")) {
                try {
                    this.f1465aN.cancel();
                } catch (Exception e4) {
                }
                m1798u();
                this.f1464aM = 0;
                return;
            }
            return;
        }
        if (this.f1464aM <= 0) {
            this.f1464aM = 3001;
            this.f1467aP = false;
            this.f1469aR = 60000L;
            this.f1470aS = false;
            long j3 = 10000;
            if (jSONObject != null) {
                try {
                    long j4 = jSONObject.getLong("frequency");
                    this.f1534l.m1853a("frequency" + j4);
                    if (j4 <= 0) {
                        j4 = 10000;
                    }
                    j3 = j4;
                } catch (Exception e5) {
                }
                try {
                    long j5 = jSONObject.getLong("timeout");
                    if (j5 > 0) {
                        this.f1469aR = j5;
                    }
                } catch (Exception e6) {
                }
                try {
                    long j6 = jSONObject.getLong("maximumAge");
                    if (j6 > 0) {
                        this.f1471aT = j6;
                    }
                } catch (Exception e7) {
                }
                try {
                    this.f1470aS = jSONObject.getBoolean("enableHighAccuracy");
                } catch (Exception e8) {
                }
            }
            this.f1466aO = j3;
            m1801v();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: c */
    public void m1702c(String str, JSONObject jSONObject, int i) {
        Exception exc;
        boolean z;
        boolean z2 = false;
        this.f1534l.m1853a(">>>>>>>>>> mediaExec !!!");
        if (str.equals("create")) {
            try {
                int i2 = jSONObject.getInt("id");
                String string = jSONObject.getString("src");
                try {
                    z2 = jSONObject.getBoolean("asynchronous");
                } catch (Exception e) {
                }
                if (z2) {
                    this.f1534l.m1853a("asynchronous:" + z2);
                    new C0601eo(this, i, jSONObject, new URL(this.f1540r.getUrl())).start();
                    return;
                }
                if (!string.startsWith("http") && !string.startsWith("file")) {
                    string = new URL(new URL(this.f1540r.getUrl()), string).toString();
                }
                try {
                    MediaPlayer create = MediaPlayer.create(this.f1535m, Uri.parse(string));
                    C0606et c0606et = new C0606et(this);
                    c0606et.f1958f = jSONObject;
                    c0606et.f1957e = i;
                    c0606et.f1959g = string;
                    if (create != null) {
                        c0606et.f1956d = create;
                        c0606et.f1955c = true;
                        long duration = create.getDuration();
                        JSONObject jSONObject2 = new JSONObject();
                        try {
                            jSONObject2.put("status", "success");
                            jSONObject2.put("duration", duration);
                        } catch (Exception e2) {
                        }
                        m1822c("javascript:applican._successCallback(" + i + ", " + jSONObject2.toString() + ", true);");
                        JSONObject jSONObject3 = new JSONObject();
                        try {
                            jSONObject3.put("status", "status_change");
                            jSONObject3.put("code", 1);
                        } catch (Exception e3) {
                        }
                        m1822c("javascript:applican._successCallback(" + c0606et.f1957e + ", " + jSONObject3.toString() + ", true);");
                    } else {
                        c0606et.f1955c = false;
                        JSONObject jSONObject4 = new JSONObject();
                        try {
                            jSONObject4.put("code", 0);
                            jSONObject4.put("message", "no data.");
                        } catch (Exception e4) {
                        }
                        m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject4.toString() + ");");
                    }
                    this.f1447W.put(Integer.valueOf(i2), c0606et);
                    this.f1534l.m1853a(">>>>>>>>>>>>soundMap " + this.f1447W.size());
                    return;
                } catch (Exception e5) {
                    JSONObject jSONObject5 = new JSONObject();
                    try {
                        jSONObject5.put("code", 0);
                        jSONObject5.put("message", e5.getMessage());
                    } catch (Exception e6) {
                    }
                    m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject5.toString() + ");");
                    return;
                }
            } catch (Exception e7) {
                this.f1534l.m1853a("2>>" + e7.getMessage());
                JSONObject jSONObject6 = new JSONObject();
                try {
                    jSONObject6.put("code", 0);
                    jSONObject6.put("message", e7.getMessage());
                } catch (Exception e8) {
                }
                m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject6.toString() + ");");
                return;
            }
        }
        if (str.equals("play") || str.equals("playBackground")) {
            if (str.equals("playBackground")) {
                this.f1529g = true;
            } else {
                this.f1529g = false;
            }
            try {
                C0606et c0606et2 = (C0606et) this.f1447W.get(Integer.valueOf(jSONObject.getInt("id")));
                if (c0606et2.f1955c) {
                    c0606et2.f1956d.start();
                    try {
                        JSONObject jSONObject7 = new JSONObject();
                        try {
                            jSONObject7.put("status", "status_change");
                            jSONObject7.put("code", 2);
                        } catch (Exception e9) {
                        }
                        m1822c("javascript:applican._successCallback(" + c0606et2.f1957e + ", " + jSONObject7.toString() + ", true);");
                        z = true;
                    } catch (Exception e10) {
                        z = true;
                        exc = e10;
                        this.f1534l.m1853a(exc.getMessage());
                        JSONObject jSONObject8 = new JSONObject();
                        try {
                            jSONObject8.put("code", 0);
                            jSONObject8.put("message", exc.getMessage());
                        } catch (Exception e11) {
                        }
                        m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject8.toString() + ");");
                        if (!z) {
                        }
                        return;
                    }
                } else {
                    z = false;
                }
            } catch (Exception e12) {
                exc = e12;
                z = false;
            }
            if (!z) {
            }
            return;
        }
        if (str.equals("pause")) {
            try {
                this.f1529g = false;
                C0606et c0606et3 = (C0606et) this.f1447W.get(Integer.valueOf(jSONObject.getInt("id")));
                c0606et3.f1956d.pause();
                JSONObject jSONObject9 = new JSONObject();
                try {
                    jSONObject9.put("status", "status_change");
                    jSONObject9.put("code", 3);
                } catch (Exception e13) {
                }
                m1822c("javascript:applican._successCallback(" + c0606et3.f1957e + ", " + jSONObject9.toString() + ", true);");
                return;
            } catch (Exception e14) {
                this.f1534l.m1853a(e14.getMessage());
                JSONObject jSONObject10 = new JSONObject();
                try {
                    jSONObject10.put("code", 0);
                    jSONObject10.put("message", e14.getMessage());
                } catch (Exception e15) {
                }
                m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject10.toString() + ");");
                return;
            }
        }
        if (str.equals("stop")) {
            this.f1529g = false;
            try {
                C0606et c0606et4 = (C0606et) this.f1447W.get(Integer.valueOf(jSONObject.getInt("id")));
                MediaPlayer mediaPlayer = c0606et4.f1956d;
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                JSONObject jSONObject11 = new JSONObject();
                try {
                    jSONObject11.put("status", "status_change");
                    jSONObject11.put("code", 4);
                } catch (Exception e16) {
                }
                m1822c("javascript:applican._successCallback(" + c0606et4.f1957e + ", " + jSONObject11.toString() + ", true);");
                return;
            } catch (Exception e17) {
                this.f1534l.m1853a(e17.getMessage());
                JSONObject jSONObject12 = new JSONObject();
                try {
                    jSONObject12.put("code", 0);
                    jSONObject12.put("message", e17.getMessage());
                } catch (Exception e18) {
                }
                m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject12.toString() + ");");
                return;
            }
        }
        if (str.equals("release")) {
            this.f1529g = false;
            try {
                int i3 = jSONObject.getInt("id");
                MediaPlayer mediaPlayer2 = ((C0606et) this.f1447W.get(Integer.valueOf(i3))).f1956d;
                this.f1447W.remove(Integer.valueOf(i3));
                mediaPlayer2.setOnCompletionListener(null);
                mediaPlayer2.setOnPreparedListener(null);
                mediaPlayer2.stop();
                mediaPlayer2.reset();
                mediaPlayer2.release();
                return;
            } catch (Exception e19) {
                this.f1534l.m1853a(e19.getMessage());
                return;
            }
        }
        if (str.equals("getCurrentPosition")) {
            try {
                m1822c("javascript:applican._successCallback(" + i + ", " + (((C0606et) this.f1447W.get(Integer.valueOf(jSONObject.getInt("id")))).f1956d.getCurrentPosition() / 1000.0f) + ");");
                return;
            } catch (Exception e20) {
                this.f1534l.m1853a(e20.getMessage());
                JSONObject jSONObject13 = new JSONObject();
                try {
                    jSONObject13.put("code", 0);
                    jSONObject13.put("message", e20.getMessage());
                } catch (Exception e21) {
                }
                m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject13.toString() + ");");
                return;
            }
        }
        if (str.equals("seekTo")) {
            try {
                ((C0606et) this.f1447W.get(Integer.valueOf(jSONObject.getInt("id")))).f1956d.seekTo(jSONObject.getInt("milliseconds"));
                return;
            } catch (Exception e22) {
                this.f1534l.m1853a(e22.getMessage());
                JSONObject jSONObject14 = new JSONObject();
                try {
                    jSONObject14.put("code", 0);
                    jSONObject14.put("message", e22.getMessage());
                } catch (Exception e23) {
                }
                m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject14.toString() + ");");
                return;
            }
        }
        if (str.equals("volume")) {
            try {
                float f = jSONObject.getInt("volume") / 100.0f;
                ((C0606et) this.f1447W.get(Integer.valueOf(jSONObject.getInt("id")))).f1956d.setVolume(f, f);
                return;
            } catch (Exception e24) {
                this.f1534l.m1853a(e24.getMessage());
                JSONObject jSONObject15 = new JSONObject();
                try {
                    jSONObject15.put("code", 0);
                    jSONObject15.put("message", e24.getMessage());
                } catch (Exception e25) {
                }
                m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject15.toString() + ");");
                return;
            }
        }
        if (!str.equals("startRecord")) {
            if (str.equals("stopRecord")) {
                m1652E();
                return;
            }
            return;
        }
        if (this.f1477aZ) {
            return;
        }
        try {
            int i4 = jSONObject.getInt("id");
            C0606et c0606et5 = (C0606et) this.f1447W.get(Integer.valueOf(i4));
            c0606et5.f1954b = true;
            String string2 = c0606et5.f1958f.getString("src");
            if (string2.startsWith("file:///")) {
                string2 = string2.substring(7);
            }
            c0606et5.f1960h = string2;
            c0606et5.f1953a = true;
            m1690b(i4, c0606et5.f1960h);
        } catch (Exception e26) {
            this.f1534l.m1853a(">>startRec Exception");
            this.f1534l.m1853a(e26.getMessage());
            JSONObject jSONObject16 = new JSONObject();
            try {
                jSONObject16.put("code", 0);
                jSONObject16.put("message", e26.getMessage());
            } catch (Exception e27) {
            }
            m1822c("javascript:applican._failCallback(" + i + ", " + jSONObject16.toString() + ");");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public String m1707d(int i) {
        switch (i) {
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                return "home";
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                return "work";
            case C0650c.PullToRefresh_ptrHeaderSubTextColor /* 3 */:
                return "other";
            default:
                return "";
        }
    }

    /* renamed from: d */
    public static String m1708d(String str) {
        int lastIndexOf = str.lastIndexOf(".");
        String substring = lastIndexOf > 0 ? str.substring(lastIndexOf + 1) : null;
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        if (substring != null) {
            return singleton.getMimeTypeFromExtension(substring);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m1710d(String str, JSONObject jSONObject) {
        this.f1441Q = true;
        startActivityForResult(new Intent(this.f1535m, (Class<?>) ZXingActivity.class), 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: d */
    public void m1711d(String str, JSONObject jSONObject, int i) {
        if (str.equals("find")) {
            new C0592ef(this, jSONObject, i).start();
            return;
        }
        if (str.equals("save")) {
            new C0607eu(this, jSONObject, i).start();
            return;
        }
        if (str.equals("remove")) {
            int i2 = -1;
            try {
                i2 = jSONObject.getInt("id");
            } catch (Exception e) {
            }
            if (i2 <= 0) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:1});");
                return;
            }
            try {
                ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
                arrayList.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection("_id=?", new String[]{String.valueOf(i2)}).build());
                getContentResolver().applyBatch("com.android.contacts", arrayList);
                m1822c("javascript:applican._successCallback(" + i + ", null);");
            } catch (Exception e2) {
                m1822c("javascript:applican._failCallback(" + i + ", {code:1});");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public String m1716e(int i) {
        switch (i) {
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                return "home";
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                return "work";
            case C0650c.PullToRefresh_ptrHeaderSubTextColor /* 3 */:
                return "other";
            case C0650c.PullToRefresh_ptrMode /* 4 */:
                return "mobile";
            default:
                return "";
        }
    }

    /* renamed from: e */
    public static String m1717e(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("\\", "\\\\").replaceAll("'", "\\\\'").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\n");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: e */
    public void m1719e(String str, JSONObject jSONObject) {
        if (str.equals("init")) {
            if (this.f1475aX > 0) {
                return;
            }
            this.f1475aX = 90000;
            new C0575dp(this).start();
            return;
        }
        if (str.equals("finish")) {
            try {
                finish();
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:103:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x024c  */
    @android.annotation.SuppressLint({"NewApi"})
    /* renamed from: e */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m1720e(java.lang.String r21, org.json.JSONObject r22, int r23) {
        /*
            Method dump skipped, instructions count: 1145
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: jp.co.newphoria.html5app.WebActivity.m1720e(java.lang.String, org.json.JSONObject, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: f */
    public String m1724f(int i) {
        switch (i) {
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                return "homepage";
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                return "blog";
            case C0650c.PullToRefresh_ptrHeaderSubTextColor /* 3 */:
                return "profile";
            case C0650c.PullToRefresh_ptrMode /* 4 */:
                return "home";
            case C0650c.PullToRefresh_ptrShowIndicator /* 5 */:
                return "work";
            case C0650c.PullToRefresh_ptrDrawable /* 6 */:
                return "ftp";
            case C0650c.PullToRefresh_ptrDrawableStart /* 7 */:
                return "other";
            default:
                return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: f */
    public void m1726f(String str, JSONObject jSONObject) {
        int i;
        int i2;
        int i3 = 1000;
        int i4 = 1;
        if (str.equals("alert")) {
            String str2 = "Alert";
            String str3 = "\u3000";
            String str4 = "OK";
            if (jSONObject != null) {
                try {
                    str2 = jSONObject.getString("title");
                } catch (Exception e) {
                }
                try {
                    str3 = jSONObject.getString("message");
                } catch (Exception e2) {
                }
                try {
                    str4 = jSONObject.getString("buttonName");
                } catch (Exception e3) {
                }
            }
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.f1535m);
                builder.setCancelable(false);
                builder.setTitle(str2);
                builder.setMessage(str3);
                builder.setPositiveButton(str4, new DialogInterfaceOnClickListenerC0555cw(this));
                builder.show();
                return;
            } catch (Exception e4) {
                return;
            }
        }
        if (!str.equals("confirm")) {
            if (str.equals("beep")) {
                if (jSONObject != null) {
                    try {
                        i2 = jSONObject.getInt("times");
                    } catch (Exception e5) {
                        i2 = 1;
                    }
                    if (i2 >= 1) {
                        i4 = i2;
                    }
                }
                new C0581dv(this, i4).start();
                return;
            }
            if (str.equals("vibrate")) {
                if (jSONObject != null) {
                    try {
                        i = jSONObject.getInt("milliseconds");
                    } catch (Exception e6) {
                        i = 1000;
                    }
                    if (i > 0) {
                        i3 = i;
                    }
                }
                try {
                    ((Vibrator) getSystemService("vibrator")).vibrate(i3);
                    return;
                } catch (Exception e7) {
                    return;
                }
            }
            return;
        }
        String str5 = "Confirm";
        String str6 = "\u3000";
        String[] strArr = {"OK", "Cancel"};
        if (jSONObject != null) {
            try {
                str5 = jSONObject.getString("title");
            } catch (Exception e8) {
            }
            try {
                str6 = jSONObject.getString("message");
            } catch (Exception e9) {
            }
            try {
                strArr = jSONObject.getString("buttonName").split(",");
            } catch (Exception e10) {
            }
            if (strArr == null || strArr.length < 2) {
                strArr = new String[]{"OK", "Cancel"};
            }
        }
        try {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this.f1535m);
            builder2.setCancelable(false);
            builder2.setTitle(str5);
            builder2.setMessage(str6);
            int length = strArr.length;
            if (length > 0) {
                builder2.setPositiveButton(strArr[0], new DialogInterfaceOnClickListenerC0556cx(this));
            }
            if (length > 1) {
                builder2.setNegativeButton(strArr[length - 1], new DialogInterfaceOnClickListenerC0557cy(this, length));
            }
            if (length > 2) {
                builder2.setNeutralButton(strArr[1], new DialogInterfaceOnClickListenerC0558cz(this));
            }
            builder2.show();
        } catch (Exception e11) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: f */
    public void m1727f(String str, JSONObject jSONObject, int i) {
        String str2;
        String str3 = null;
        if (str.equals("set")) {
            try {
                String string = jSONObject.getString("key");
                String string2 = jSONObject.getString("value");
                SharedPreferences.Editor edit = getSharedPreferences("SimpleStorage", 0).edit();
                edit.putString(string, string2);
                edit.commit();
            } catch (Exception e) {
                str3 = e.getMessage();
            }
            if (str3 == null) {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            } else {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            }
        }
        if (str.equals("get")) {
            try {
                str2 = getSharedPreferences("SimpleStorage", 0).getString(jSONObject.getString("key"), null);
            } catch (Exception e2) {
                str3 = e2.getMessage();
                str2 = null;
            }
            this.f1534l.m1853a(">>>>" + str2);
            if (str3 == null) {
                m1822c(str2 == null ? "javascript:applican._successCallback(" + i + ", null);" : "javascript:applican._successCallback(" + i + ", '" + m1717e(str2) + "');");
                return;
            } else {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            }
        }
        if (str.equals("remove")) {
            try {
                String string3 = jSONObject.getString("key");
                SharedPreferences.Editor edit2 = getSharedPreferences("SimpleStorage", 0).edit();
                edit2.remove(string3);
                edit2.commit();
            } catch (Exception e3) {
                str3 = e3.getMessage();
            }
            if (str3 == null) {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            } else {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
                return;
            }
        }
        if (str.equals("clear")) {
            try {
                SharedPreferences.Editor edit3 = getSharedPreferences("SimpleStorage", 0).edit();
                edit3.clear();
                edit3.commit();
            } catch (Exception e4) {
                str3 = e4.getMessage();
            }
            if (str3 == null) {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
            } else {
                m1822c("javascript:applican._successCallback(" + i + ", null);");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: g */
    public String m1732g(int i) {
        switch (i) {
            case C0650c.PullToRefresh_ptrRefreshableViewBackground /* 0 */:
                return "aim";
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                return "msn";
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                return "yahoo";
            case C0650c.PullToRefresh_ptrHeaderSubTextColor /* 3 */:
                return "skype";
            case C0650c.PullToRefresh_ptrMode /* 4 */:
                return "qq";
            case C0650c.PullToRefresh_ptrShowIndicator /* 5 */:
                return "googleTalk";
            case C0650c.PullToRefresh_ptrDrawable /* 6 */:
                return "icq";
            case C0650c.PullToRefresh_ptrDrawableStart /* 7 */:
                return "jabber";
            case C0650c.PullToRefresh_ptrDrawableEnd /* 8 */:
                return "netmeeting";
            default:
                return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:12:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00bc  */
    /* renamed from: g */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m1734g(java.lang.String r14, org.json.JSONObject r15) {
        /*
            Method dump skipped, instructions count: 393
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: jp.co.newphoria.html5app.WebActivity.m1734g(java.lang.String, org.json.JSONObject):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: g */
    public void m1735g(String str, JSONObject jSONObject, int i) {
        String str2;
        String str3;
        long j;
        if (!str.equals("show")) {
            if (str.equals("hide")) {
                try {
                    this.f1534l.f1656s.m1916b();
                    return;
                } catch (Exception e) {
                    return;
                }
            }
            return;
        }
        try {
            str2 = jSONObject.getString("portrait");
        } catch (Exception e2) {
            str2 = null;
        }
        try {
            str3 = jSONObject.getString("landscape");
        } catch (Exception e3) {
            str3 = null;
        }
        try {
            j = jSONObject.getLong("timeout");
        } catch (Exception e4) {
            j = -1;
        }
        try {
            this.f1534l.f1656s.m1909a(BitmapFactory.decodeStream(new FileInputStream(getResources().getConfiguration().orientation == 2 ? new File(this.f1535m.getFilesDir().toString() + "/contents/web/" + str3) : new File(this.f1535m.getFilesDir().toString() + "/contents/web/" + str2))));
        } catch (Exception e5) {
        }
        if (j >= 0) {
            new Timer().schedule(new C0562dc(this), j);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: h */
    public int m1739h(String str) {
        int i = 0;
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", 0);
            if (attributeInt == 6) {
                i = 90;
            } else if (attributeInt == 3) {
                i = 180;
            } else if (attributeInt == 8) {
                i = 270;
            }
            if (i != 0) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: h */
    public String m1742h(int i) {
        switch (i) {
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                return "work";
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                return "other";
            default:
                return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: h */
    public void m1743h(String str, JSONObject jSONObject) {
        int i;
        int i2;
        String str2;
        Integer num;
        String str3;
        String str4;
        if (!str.equals("download")) {
            if (str.equals("unzip")) {
                try {
                    String string = jSONObject.getString("source");
                    String string2 = jSONObject.getString("target");
                    File file = new File(string);
                    File file2 = new File(string2);
                    try {
                        C0527bv.m1925a(file, file2);
                        m1822c("javascript:applican.utilities._" + str + "Success('" + file2.getAbsolutePath() + "');");
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        StringBuilder append = new StringBuilder().append("javascript:applican.utilities._").append(str).append("Error({code:");
                        i2 = EnumC0574do.ERROR_UNZIP_UNZIP_ERROR.f1863d;
                        m1822c(append.append(i2).append("});").toString());
                        return;
                    }
                } catch (Exception e2) {
                    Log.e("Error", e2.getLocalizedMessage());
                    StringBuilder append2 = new StringBuilder().append("javascript:applican.utilities._").append(str).append("Error({code:");
                    i = EnumC0574do.ERROR_UNZIP_INVALID_PATHS.f1863d;
                    m1822c(append2.append(i).append("});").toString());
                    return;
                }
            }
            return;
        }
        try {
            str2 = jSONObject.getString("url");
        } catch (Exception e3) {
            Log.e("Error", e3.getLocalizedMessage());
            str2 = "";
        }
        try {
            num = Integer.valueOf(jSONObject.getInt("timeout"));
        } catch (Exception e4) {
            num = 60;
        }
        try {
            str3 = jSONObject.getString("destination");
        } catch (Exception e5) {
            str3 = "documents";
        }
        String substring = str2.substring(str2.lastIndexOf("/"));
        try {
            str4 = (String) new AsyncTaskC0617fd(this).execute(new String[]{str2, (str3.equals("cache") || str3.equals("caches")) ? this.f1535m.getCacheDir() + substring : this.f1535m.getFilesDir() + substring}).get(num.intValue(), TimeUnit.SECONDS);
        } catch (InterruptedException e6) {
            e6.printStackTrace();
            str4 = "";
        } catch (ExecutionException e7) {
            e7.printStackTrace();
            str4 = "";
        } catch (TimeoutException e8) {
            e8.printStackTrace();
            str4 = "";
        }
        if (str4.isEmpty()) {
            m1822c("javascript:applican.utilities._" + str + "Error('');");
        } else {
            m1822c("javascript:applican.utilities._" + str + "Success('" + str4 + "');");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: h */
    public void m1744h(String str, JSONObject jSONObject, int i) {
        String str2;
        String str3;
        String str4;
        Long l = null;
        String str5 = null;
        l = null;
        if (str.equals("trackView")) {
            try {
                str5 = jSONObject.getString("screen");
            } catch (Exception e) {
            }
            if (str5 != null) {
                this.f1534l.m1859b(str5);
                return;
            }
            return;
        }
        if (str.equals("trackEvent")) {
            try {
                str2 = jSONObject.getString("category");
            } catch (Exception e2) {
                str2 = null;
            }
            try {
                str3 = jSONObject.getString("action");
            } catch (Exception e3) {
                str3 = null;
            }
            try {
                str4 = jSONObject.getString("label");
            } catch (Exception e4) {
                str4 = null;
            }
            try {
                long parseLong = Long.parseLong(jSONObject.getString("value"));
                if (parseLong >= 0) {
                    l = Long.valueOf(parseLong);
                }
            } catch (Exception e5) {
            }
            if (str2 == null || str3 == null || str4 == null) {
                return;
            }
            this.f1534l.m1856a(str2, str3, str4, l);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: i */
    public int m1747i(String str) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        if (str.equals("home")) {
            return 1;
        }
        if (str.equals("mobile")) {
            return 2;
        }
        if (str.equals("work")) {
            return 3;
        }
        if (str.equals("workFax")) {
            return 4;
        }
        if (str.equals("homeFax")) {
            return 5;
        }
        if (str.equals("pager")) {
            return 6;
        }
        return str.equals("other") ? 7 : -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: i */
    public void m1748i(int i) {
        this.f1534l.m1853a(">>>clickSlideMenu : " + i);
        try {
            int length = C0472a.f1586U.length();
            int i2 = 0;
            while (i2 < length) {
                JSONObject jSONObject = C0472a.f1586U.getJSONObject(i2);
                String str = jSONObject.getString("image") + ".png";
                String str2 = jSONObject.getString("image") + "_selected.png";
                ((C0465l) this.f1488ak.get(i2)).m1590a(this, this.f1535m.getFilesDir().toString() + "/contents/web/slide_menu/" + (i2 == i ? str2 : str), this.f1535m.getFilesDir().toString() + "/contents/web/slide_menu/" + str2);
                i2++;
            }
        } catch (Exception e) {
        }
        try {
            String string = C0472a.f1586U.getJSONObject(i).getString("url");
            if (!string.startsWith("http")) {
                string = "file://" + this.f1535m.getFilesDir().toString() + "/contents/web/" + string;
            }
            m1820b(string);
        } catch (Exception e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: i */
    public void m1749i(String str, JSONObject jSONObject, int i) {
        int i2;
        JSONArray jSONArray;
        String str2;
        int i3;
        int i4;
        if (str.equals("show")) {
            try {
                i2 = jSONObject.getInt("type");
            } catch (Exception e) {
                i2 = 1;
            }
            try {
                jSONArray = jSONObject.getJSONArray("list_data");
            } catch (Exception e2) {
                jSONArray = null;
            }
            try {
                str2 = jSONObject.getString("title");
            } catch (Exception e3) {
                str2 = "";
            }
            try {
                i3 = jSONObject.getInt("width");
            } catch (Exception e4) {
                i3 = 50;
            }
            try {
                i4 = jSONObject.getInt("height");
            } catch (Exception e5) {
                i4 = 50;
            }
            if (i2 > 0) {
                this.f1534l.f1624A = i2;
            } else {
                this.f1534l.f1624A = 1;
            }
            if (jSONArray != null) {
                this.f1534l.f1625B = jSONArray;
            } else {
                this.f1534l.f1625B = null;
            }
            if (str2 != null) {
                this.f1534l.f1626C = str2;
            } else {
                this.f1534l.f1626C = "";
            }
            if (i3 <= 0) {
                this.f1534l.f1627D = 50;
            } else {
                this.f1534l.f1627D = i3;
            }
            if (i4 <= 0) {
                this.f1534l.f1628E = 50;
            } else {
                this.f1534l.f1628E = i4;
            }
            startActivityForResult(new Intent(this.f1535m, (Class<?>) MyListActivity.class), 7);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: j */
    public int m1752j(String str) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        if (str.equals("home")) {
            return 1;
        }
        if (str.equals("work")) {
            return 2;
        }
        return str.equals("other") ? 3 : -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: j */
    public void m1753j(String str, JSONObject jSONObject, int i) {
        int i2;
        int i3;
        int i4;
        if (!str.equals("play")) {
            if (!str.equals("stop") || this.f1481ad == null) {
                return;
            }
            m1653F();
            m1822c("javascript:applican.video._playError('CANCELED');");
            return;
        }
        int i5 = (int) this.f1534l.f1638a;
        int i6 = (int) this.f1534l.f1639b;
        String str2 = null;
        this.f1484ag = false;
        try {
            i2 = jSONObject.getInt("top");
        } catch (Exception e) {
            i2 = 0;
        }
        try {
            i3 = jSONObject.getInt("left");
        } catch (Exception e2) {
            i3 = 0;
        }
        try {
            i4 = jSONObject.getInt("width");
        } catch (Exception e3) {
            i4 = i5;
        }
        try {
            i6 = jSONObject.getInt("height");
        } catch (Exception e4) {
        }
        try {
            i5 = jSONObject.getInt("clientWidth");
        } catch (Exception e5) {
        }
        try {
            jSONObject.getInt("clientHeight");
        } catch (Exception e6) {
        }
        try {
            str2 = jSONObject.getString("src");
        } catch (Exception e7) {
        }
        try {
            this.f1484ag = jSONObject.getBoolean("control");
        } catch (Exception e8) {
        }
        this.f1534l.m1853a(str2);
        float f = this.f1534l.f1638a / i5;
        this.f1534l.m1853a(">>>> scale:" + f);
        int i7 = (int) (i2 * f);
        int i8 = (int) (i3 * f);
        int i9 = (int) (i4 * f);
        int i10 = (int) (f * i6);
        try {
            if (!str2.startsWith("http") && !str2.startsWith("file")) {
                str2 = new URL(new URL(this.f1540r.getUrl()), str2).toString();
            }
            this.f1534l.m1853a(str2);
            if (this.f1483af == null) {
                this.f1483af = new MediaController(this.f1535m);
            }
            this.f1482ae = new RelativeLayout(this.f1535m);
            this.f1482ae.setBackgroundColor(-16777216);
            this.f1538p.addView(this.f1482ae, this.f1534l.m1851a(i8, i7, i9, i10));
            LinearLayout linearLayout = new LinearLayout(this.f1535m);
            linearLayout.setGravity(17);
            this.f1482ae.addView(linearLayout, this.f1534l.m1851a(0, 0, i9, i10));
            this.f1481ad = new VideoView(this.f1535m);
            this.f1481ad.setVideoPath(str2);
            VideoView videoView = this.f1481ad;
            this.f1534l.getClass();
            this.f1534l.getClass();
            linearLayout.addView(videoView, new LinearLayout.LayoutParams(-2, -2));
            if (this.f1484ag) {
                this.f1481ad.setMediaController(this.f1483af);
            }
            this.f1481ad.setOnCompletionListener(new C0565df(this));
            this.f1481ad.setOnPreparedListener(new C0566dg(this));
        } catch (Exception e9) {
            m1653F();
            m1822c("javascript:applican.video._playError('NOT_FOUND_ERR');");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: k */
    public int m1756k(String str) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        if (str.equals("home")) {
            return 1;
        }
        if (str.equals("mobile")) {
            return 4;
        }
        if (str.equals("work")) {
            return 2;
        }
        return str.equals("other") ? 3 : -1;
    }

    /* renamed from: k */
    private void m1758k() {
        if (C0472a.f1622z) {
            this.f1534l.f1652o = 0.0f;
        }
        this.f1428D = 42.0f;
        this.f1431G = 37;
        if (this.f1534l.f1648k < this.f1534l.f1647j) {
            this.f1428D = 21.0f;
            this.f1431G = 18;
        }
        this.f1429E = (this.f1534l.f1639b - this.f1428D) - this.f1449Y;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: k */
    public void m1759k(String str, JSONObject jSONObject, int i) {
        String str2;
        String str3;
        String str4;
        if (str.equals("getStatus")) {
            try {
                m1822c("javascript:applican.wifi._getStatusSuccess('" + (((WifiManager) getSystemService("wifi")).isWifiEnabled() ? "WIFI_ON" : "WIFI_OFF") + "');");
                return;
            } catch (SecurityException e) {
                m1822c("javascript:applican.wifi._getStatusError('PERMISSION_DENIED');");
                return;
            } catch (Exception e2) {
                this.f1534l.m1853a(e2.toString());
                m1822c("javascript:applican.wifi._getStatusError('UNKNOWN_ERROR');");
                return;
            }
        }
        if (str.equals("on") || str.equals("off")) {
            try {
                ((WifiManager) getSystemService("wifi")).setWifiEnabled(str.equals("on"));
                m1822c("javascript:applican.wifi._wifiSuccess();");
                return;
            } catch (SecurityException e3) {
                m1822c("javascript:applican.wifi._wifiError('PERMISSION_DENIED');");
                return;
            } catch (Exception e4) {
                this.f1534l.m1853a(e4.toString());
                m1822c("javascript:applican.wifi._wifiError('UNKNOWN_ERROR');");
                return;
            }
        }
        if (str.equals("getSSIDList")) {
            try {
                WifiManager wifiManager = (WifiManager) getSystemService("wifi");
                String[] strArr = null;
                if (wifiManager.getWifiState() != 3) {
                    m1822c("javascript:applican.wifi._getSSIDListError('DISCONNECT');");
                    return;
                }
                if (wifiManager.getWifiState() == 3) {
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    strArr = new String[scanResults.size()];
                    for (int i2 = 0; i2 < scanResults.size(); i2++) {
                        String str5 = scanResults.get(i2).SSID;
                        if (str5 != null && str5.startsWith("\"") && str5.endsWith("\"")) {
                            str5 = str5.substring(1, str5.length() - 1);
                        }
                        strArr[i2] = str5;
                    }
                }
                if (strArr != null) {
                    String str6 = "[";
                    for (int i3 = 0; i3 < strArr.length; i3++) {
                        if (i3 > 0) {
                            str6 = str6 + ",";
                        }
                        str6 = str6 + "'" + m1717e(strArr[i3]) + "'";
                    }
                    str2 = str6 + "]";
                } else {
                    str2 = "[]";
                }
                m1822c("javascript:applican.wifi._getSSIDListSuccess(" + str2 + ");");
                return;
            } catch (SecurityException e5) {
                m1822c("javascript:applican.wifi._getSSIDListError('PERMISSION_DENIED');");
                return;
            } catch (Exception e6) {
                this.f1534l.m1853a(e6.toString());
                m1822c("javascript:applican.wifi._getSSIDListError('UNKNOWN_ERROR');");
                return;
            }
        }
        if (str.equals("getAccessPointList")) {
            try {
                WifiManager wifiManager2 = (WifiManager) getSystemService("wifi");
                JSONArray jSONArray = new JSONArray();
                if (wifiManager2.getWifiState() != 3) {
                    m1822c("javascript:applican.wifi._getAccessPointListError('DISCONNECT');");
                    return;
                }
                if (wifiManager2.getWifiState() == 3) {
                    List<ScanResult> scanResults2 = wifiManager2.getScanResults();
                    for (int i4 = 0; i4 < scanResults2.size(); i4++) {
                        ScanResult scanResult = scanResults2.get(i4);
                        JSONObject jSONObject2 = new JSONObject();
                        String str7 = scanResult.SSID;
                        if (str7 != null && str7.startsWith("\"") && str7.endsWith("\"")) {
                            str7 = str7.substring(1, str7.length() - 1);
                        }
                        jSONObject2.put("ssid", str7);
                        jSONObject2.put("bssid", scanResult.BSSID);
                        jSONObject2.put("frequency", scanResult.frequency);
                        jSONObject2.put("level", scanResult.level);
                        jSONObject2.put("capabilities", scanResult.capabilities);
                        jSONArray.put(jSONObject2);
                    }
                }
                m1822c("javascript:applican.wifi._getAccessPointListSuccess(" + jSONArray.toString() + ");");
                return;
            } catch (SecurityException e7) {
                m1822c("javascript:applican.wifi._getAccessPointListError('PERMISSION_DENIED');");
                return;
            } catch (Exception e8) {
                this.f1534l.m1853a(e8.toString());
                m1822c("javascript:applican.wifi._getAccessPointListError('UNKNOWN_ERROR');");
                return;
            }
        }
        if (!str.equals("getConfiguredNetworks") && !str.equals("hadConnected")) {
            if (!str.equals("getCurrentSSID")) {
                if (str.equals("connect")) {
                    new C0619ff(this, i, jSONObject).start();
                    return;
                }
                if (str.equals("getCurrentIPv4Address")) {
                    WifiManager wifiManager3 = (WifiManager) getSystemService("wifi");
                    if (wifiManager3.getWifiState() != 3) {
                        m1822c("javascript:applican.wifi._getCurrentIPv4AddressError('DISCONNECT');");
                        return;
                    }
                    int ipAddress = wifiManager3.getConnectionInfo().getIpAddress();
                    this.f1534l.m1853a("ip : " + ipAddress);
                    if (ipAddress != 0) {
                        m1822c("javascript:applican.wifi._getCurrentIPv4AddressSuccess('" + String.format("%d.%d.%d.%d", Integer.valueOf((ipAddress >> 0) & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255)) + "');");
                        return;
                    } else {
                        m1822c("javascript:applican.wifi._getCurrentIPv4AddressError('UNKNOWN_ERROR');");
                        return;
                    }
                }
                return;
            }
            try {
                WifiManager wifiManager4 = (WifiManager) getSystemService("wifi");
                if (wifiManager4.getWifiState() != 3) {
                    m1822c("javascript:applican.wifi._getCurrentSSIDError('DISCONNECT');");
                    return;
                }
                String ssid = wifiManager4.getConnectionInfo().getSSID();
                if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                m1822c("javascript:applican.wifi._getCurrentSSIDSuccess('" + ssid + "');");
                return;
            } catch (SecurityException e9) {
                m1822c("javascript:applican.wifi._getCurrentSSIDError('PERMISSION_DENIED');");
                return;
            } catch (Exception e10) {
                this.f1534l.m1853a(e10.toString());
                m1822c("javascript:applican.wifi._getCurrentSSIDError('UNKNOWN_ERROR');");
                return;
            }
        }
        boolean z = false;
        if (str.equals("getConfiguredNetworks")) {
            str3 = null;
            str4 = "_getConfiguredNetworksError";
        } else if (str.equals("hadConnected")) {
            try {
                str3 = jSONObject.getString("ssid").trim();
                str4 = "_hadConnectedError";
            } catch (Exception e11) {
                str3 = null;
                str4 = "_hadConnectedError";
            }
        } else {
            str3 = null;
            str4 = "";
        }
        try {
            WifiManager wifiManager5 = (WifiManager) getSystemService("wifi");
            if (wifiManager5.getWifiState() != 3) {
                m1822c("javascript:applican.wifi." + str4 + "('DISCONNECT');");
                return;
            }
            JSONArray jSONArray2 = new JSONArray();
            List<WifiConfiguration> configuredNetworks = wifiManager5.getConfiguredNetworks();
            if (configuredNetworks != null) {
                int size = configuredNetworks.size();
                int i5 = 0;
                while (i5 < size) {
                    WifiConfiguration wifiConfiguration = configuredNetworks.get(i5);
                    JSONObject jSONObject3 = new JSONObject();
                    String str8 = wifiConfiguration.SSID;
                    if (str8 != null && str8.startsWith("\"") && str8.endsWith("\"")) {
                        str8 = str8.substring(1, str8.length() - 1);
                    }
                    jSONObject3.put("ssid", str8);
                    boolean z2 = (str3 == null || !str3.equals(str8)) ? z : true;
                    String str9 = "";
                    if (wifiConfiguration.status == 0) {
                        str9 = "CURRENT";
                    } else if (wifiConfiguration.status == 1) {
                        str9 = "DISABLED";
                    } else if (wifiConfiguration.status == 2) {
                        str9 = "ENABLED";
                    }
                    jSONObject3.put("status", str9);
                    jSONObject3.put("networkId", wifiConfiguration.networkId);
                    jSONArray2.put(jSONObject3);
                    i5++;
                    z = z2;
                }
            }
            boolean z3 = z;
            if (str.equals("getConfiguredNetworks")) {
                m1822c("javascript:applican.wifi._getConfiguredNetworksSuccess(" + jSONArray2.toString() + ");");
            } else if (str.equals("hadConnected")) {
                m1822c("javascript:applican.wifi._hadConnectedSuccess(" + z3 + ");");
            }
        } catch (SecurityException e12) {
            m1822c("javascript:applican.wifi." + str4 + "('PERMISSION_DENIED');");
        } catch (Exception e13) {
            this.f1534l.m1853a(e13.toString());
            m1822c("javascript:applican.wifi." + str4 + "('UNKNOWN_ERROR');");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public int m1761l(String str) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        if (str.equals("blog")) {
            return 2;
        }
        if (str.equals("ftp")) {
            return 6;
        }
        if (str.equals("home")) {
            return 4;
        }
        if (str.equals("homepage")) {
            return 1;
        }
        if (str.equals("other")) {
            return 7;
        }
        if (str.equals("profile")) {
            return 3;
        }
        return str.equals("work") ? 5 : -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public void m1763l() {
        try {
            if (this.f1540r.canGoBack()) {
                this.f1546x.setEnabled(true);
                m1674a(this.f1546x, 1.0f);
            } else {
                this.f1546x.setEnabled(false);
                m1674a(this.f1546x, 0.5f);
            }
            if (this.f1540r.canGoForward()) {
                this.f1547y.setEnabled(true);
                m1674a(this.f1547y, 1.0f);
            } else {
                this.f1547y.setEnabled(false);
                m1674a(this.f1547y, 0.5f);
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public void m1764l(String str, JSONObject jSONObject, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: m */
    public int m1766m(String str) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        if (str.equals("aim")) {
            return 0;
        }
        if (str.equals("googleTalk")) {
            return 5;
        }
        if (str.equals("icq")) {
            return 6;
        }
        if (str.equals("jabber")) {
            return 7;
        }
        if (str.equals("msn")) {
            return 1;
        }
        if (str.equals("msn")) {
            return 8;
        }
        if (str.equals("qq")) {
            return 4;
        }
        if (str.equals("skype")) {
            return 3;
        }
        return str.equals("yahoo") ? 2 : -1;
    }

    /* renamed from: m */
    private void m1768m() {
        String stringExtra;
        if (!this.f1478aa && (C0472a.f1604h.equals("demo") || (C0472a.f1604h.equals("tab") && this.f1448X == 0))) {
            try {
                Intent intent = this.f1534l.f1656s.getIntent();
                String stringExtra2 = intent.getStringExtra("start_param");
                if (stringExtra2 != null && stringExtra2.equals("FromNotification") && (stringExtra = intent.getStringExtra("url")) != null && stringExtra.length() > 5 && !stringExtra.startsWith("update://")) {
                    m1820b(stringExtra);
                    return;
                }
            } catch (Exception e) {
            }
        }
        if (this.f1478aa) {
            m1824e();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: m */
    public void m1769m(String str, JSONObject jSONObject, int i) {
        if (str.equals("watchKeyDown")) {
            this.f1507bc = true;
            return;
        }
        if (str.equals("clearWatchKeyDown")) {
            this.f1507bc = false;
        } else if (str.equals("watchKeyUp")) {
            this.f1508bd = true;
        } else if (str.equals("clearWatchKeyUp")) {
            this.f1508bd = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: n */
    public int m1771n(String str) {
        if (str == null || str.length() < 1) {
            return -1;
        }
        if (str.equals("work")) {
            return 1;
        }
        return str.equals("other") ? 2 : -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: n */
    public void m1772n() {
        this.f1536n.post(new RunnableC0540ch(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: n */
    public void m1773n(String str, JSONObject jSONObject, int i) {
        String str2;
        if (str.equals("captureAudio")) {
            try {
                startActivityForResult(new Intent("android.provider.MediaStore.RECORD_SOUND"), 9);
                return;
            } catch (Exception e) {
                this.f1534l.m1853a(e.toString() + "," + e.getMessage());
                m1822c("javascript:applican.capture._captureAudioError(" + ("{code:20, message:'" + m1717e(e.getMessage()) + "'}") + ");");
                return;
            }
        }
        if (str.equals("captureVideo")) {
            try {
                startActivityForResult(new Intent("android.media.action.VIDEO_CAPTURE"), 11);
                return;
            } catch (Exception e2) {
                this.f1534l.m1853a(e2.toString() + "," + e2.getMessage());
                m1822c("javascript:applican.capture._captureVideoError(" + ("{code:20, message:'" + m1717e(e2.getMessage()) + "'}") + ");");
                return;
            }
        }
        if (str.equals("captureImage")) {
            try {
                Intent intent = new Intent();
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 10);
                return;
            } catch (Exception e3) {
                this.f1534l.m1853a(e3.toString() + "," + e3.getMessage());
                m1822c("javascript:applican.capture._captureImageError(" + ("{code:20, message:'" + m1717e(e3.getMessage()) + "'}") + ");");
                return;
            }
        }
        if (str.equals("captureWithOverlay")) {
            try {
                String str3 = "";
                String str4 = "library";
                String str5 = "false";
                new JSONObject(jSONObject.toString());
                try {
                    str3 = jSONObject.getString("overlayImage");
                    str4 = jSONObject.getString("mode");
                    str5 = jSONObject.getString("fill");
                    str2 = jSONObject.getString("keepAspectRatio");
                } catch (Exception e4) {
                    this.f1534l.m1853a(e4.toString() + "," + e4.getMessage());
                    str2 = "true";
                }
                String str6 = (this.f1535m.getFilesDir().toString() + "/contents/web/") + str3;
                try {
                    Intent intent2 = new Intent(this.f1535m, (Class<?>) CaptureOverlay.class);
                    intent2.putExtra("OverlayImage", str6);
                    intent2.putExtra("CaptureMode", str4);
                    intent2.putExtra("FillOverlay", str5);
                    intent2.putExtra("KeepAspectRatio", str2);
                    startActivityForResult(intent2, 13);
                } catch (Exception e5) {
                    this.f1534l.m1853a(e5.toString() + "," + e5.getMessage());
                    m1822c("javascript:applican.capture._captureWithOverlayError(" + ("{code:2, message:'" + m1717e(e5.getMessage()) + "'}") + ");");
                }
            } catch (Exception e6) {
                this.f1534l.m1853a(e6.toString() + "," + e6.getMessage());
                m1822c("javascript:applican.capture._captureWithOverlayError(" + ("{code:20, message:'" + m1717e(e6.getMessage()) + "'}") + ");");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: o */
    public String m1776o(String str) {
        if (str == null || str.equals("null") || str.length() < 1) {
            return null;
        }
        return str;
    }

    /* renamed from: o */
    private void m1777o() {
        if (this.f1455aD != 2001 || this.f1451a == null) {
            try {
                this.f1451a = (SensorManager) getSystemService("sensor");
                this.f1451a.registerListener(this, this.f1451a.getDefaultSensor(2), 2);
                this.f1451a.registerListener(this, this.f1451a.getDefaultSensor(1), 2);
            } catch (Exception e) {
                this.f1534l.m1853a(e.getMessage());
                if (this.f1500aw == 1000) {
                    m1822c("javascript:applican.compass._getCurrentHeadingError('COMPASS_NOT_SUPPORTED');");
                } else if (this.f1500aw == 1001) {
                    m1822c("javascript:applican.compass._watchHeadingError('COMPASS_NOT_SUPPORTED');");
                    try {
                        this.f1501ax.cancel();
                    } catch (Exception e2) {
                    }
                }
                m1782p();
                this.f1500aw = 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: o */
    public void m1778o(String str, JSONObject jSONObject, int i) {
        String str2 = null;
        if (str.equals("getPreferredLanguage")) {
            try {
                str2 = "{value:'" + Locale.getDefault().getDisplayLanguage(Locale.US) + "'}";
            } catch (Exception e) {
            }
            if (str2 == null || str2.length() <= 0) {
                m1822c("javascript:applican.globalization._getPreferredLanguageError({code:0, message:''});");
                return;
            } else {
                m1822c("javascript:applican.globalization._getPreferredLanguageSuccess(" + str2 + ");");
                return;
            }
        }
        if (str.equals("getLocaleName")) {
            try {
                str2 = "{value:'" + Locale.getDefault().toString() + "'}";
            } catch (Exception e2) {
            }
            if (str2 == null || str2.length() <= 0) {
                m1822c("javascript:applican.globalization._getLocaleNameError({code:0, message:''});");
                return;
            } else {
                m1822c("javascript:applican.globalization._getLocaleNameSuccess(" + str2 + ");");
                return;
            }
        }
        if (str.equals("getCountry")) {
            try {
                str2 = "{value:'" + Locale.getDefault().getCountry() + "'}";
            } catch (Exception e3) {
            }
            if (str2 == null || str2.length() <= 0) {
                m1822c("javascript:applican.globalization._getCountryError({code:0, message:''});");
                return;
            } else {
                m1822c("javascript:applican.globalization._getCountrySuccess(" + str2 + ");");
                return;
            }
        }
        if (str.equals("dateToString")) {
            try {
                str2 = "{value:'" + java.text.DateFormat.getDateInstance().format(new Date()) + "'}";
                this.f1534l.m1853a(">>>>>>>>>>>>" + str2);
            } catch (Exception e4) {
                this.f1534l.m1853a(e4.toString());
            }
            if (str2 == null || str2.length() <= 0) {
                m1822c("javascript:applican.globalization._dateToStringError({code:0, message:''});");
            } else {
                m1822c("javascript:applican.globalization._dateToStringSuccess(" + str2 + ");");
            }
        }
    }

    /* renamed from: p */
    private void m1782p() {
        if (this.f1455aD != 2001) {
            try {
                this.f1451a.unregisterListener(this);
                this.f1451a = null;
            } catch (Exception e) {
                this.f1534l.m1853a(e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: p */
    public void m1783p(String str, JSONObject jSONObject, int i) {
        int i2;
        int i3 = 0;
        if (str.equals("setBadge")) {
            try {
                i2 = jSONObject.getInt("tab");
            } catch (Exception e) {
                i2 = 0;
            }
            try {
                i3 = jSONObject.getInt("num");
            } catch (Exception e2) {
            }
            this.f1534l.f1656s.m1907a(i2, i3);
            return;
        }
        if (str.equals("changeTabImage")) {
            String str2 = "tab";
            try {
                str2 = jSONObject.getString("folder");
            } catch (Exception e3) {
            }
            this.f1534l.f1656s.m1910a(str2);
        }
    }

    /* renamed from: q */
    private void m1785q() {
        this.f1502ay = -1.0f;
        this.f1503az = -1.0f;
        this.f1452aA = -1.0f;
        this.f1453aB = -1L;
        m1777o();
        this.f1501ax = new Timer();
        this.f1501ax.schedule(new C0544cl(this), this.f1454aC, this.f1454aC);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: q */
    public void m1786q(String str, JSONObject jSONObject, int i) {
        if (str.equals("loadBGM")) {
            try {
                this.f1509be = new URL(this.f1540r.getUrl());
            } catch (Exception e) {
            }
            new C0599em(this, jSONObject).start();
        } else if (!str.equals("loadSE")) {
            new C0593eg(this, str, jSONObject).start();
        } else {
            try {
                this.f1509be = new URL(this.f1540r.getUrl());
            } catch (Exception e2) {
            }
            new C0600en(this, jSONObject).start();
        }
    }

    /* renamed from: r */
    private void m1789r() {
        if (this.f1500aw != 1001 || this.f1451a == null) {
            try {
                this.f1451a = (SensorManager) getSystemService("sensor");
                this.f1451a.registerListener(this, this.f1451a.getDefaultSensor(2), 2);
                this.f1451a.registerListener(this, this.f1451a.getDefaultSensor(1), 2);
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: r */
    public void m1790r(String str, JSONObject jSONObject, int i) {
        if (str.equals("get") || str.equals("post")) {
            new C0594eh(this, str, jSONObject, i).start();
        }
    }

    /* renamed from: s */
    private void m1792s() {
        if (this.f1500aw != 1001) {
            try {
                this.f1451a.unregisterListener(this);
                this.f1451a = null;
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: s */
    public void m1793s(String str, JSONObject jSONObject, int i) {
    }

    /* renamed from: t */
    private void m1795t() {
        this.f1457aF = 0.0f;
        this.f1458aG = 0.0f;
        this.f1459aH = 0.0f;
        this.f1460aI = -1L;
        m1789r();
        this.f1456aE = new Timer();
        this.f1456aE.schedule(new C0546cn(this), this.f1461aJ, this.f1461aJ);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: t */
    public void m1796t(String str, JSONObject jSONObject, int i) {
        double d;
        double d2 = 0.0d;
        if (str.equals("showAR")) {
            if (!C0472a.f1618v) {
                m1822c("javascript:applican.arpl._showARError({code:0, message:'AR is not available.'});");
                return;
            }
            String str2 = "";
            String str3 = "";
            try {
                str2 = jSONObject.getString("contents_id");
            } catch (Exception e) {
            }
            try {
                str3 = jSONObject.getString("uid");
            } catch (Exception e2) {
            }
            try {
                d = jSONObject.getDouble("lat");
            } catch (Exception e3) {
                d = 0.0d;
            }
            try {
                d2 = jSONObject.getDouble("lon");
            } catch (Exception e4) {
            }
            this.f1534l.f1656s.m1912a(str2, str3, d, d2, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: u */
    public void m1798u() {
        if (this.f1463aL != null) {
            try {
                this.f1463aL.removeUpdates(this);
            } catch (Exception e) {
            }
            this.f1463aL = null;
            try {
                this.f1468aQ.cancel();
            } catch (Exception e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: u */
    public void m1799u(String str, JSONObject jSONObject, int i) {
        this.f1534l.f1656s.m1914a(str, jSONObject, i, this);
    }

    /* renamed from: v */
    private void m1801v() {
        m1682a(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: v */
    public void m1802v(String str, JSONObject jSONObject, int i) {
        if (str.equals("open") || str.equals("send")) {
            this.f1534l.m1853a(">> WebSocketFuncThread");
            new C0618fe(this, str, jSONObject, i).start();
        } else if (str.equals("close")) {
            try {
                this.f1510bf.m51b();
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: w */
    public void m1804w() {
        this.f1435K = false;
        try {
            m1670a("clearWatch", (JSONObject) null);
        } catch (Exception e) {
        }
        try {
            m1691b("clearWatch", (JSONObject) null);
        } catch (Exception e2) {
        }
        try {
            m1701c("clearWatch", (JSONObject) null);
        } catch (Exception e3) {
        }
        try {
            m1691b("clearWatchShake", (JSONObject) null);
        } catch (Exception e4) {
        }
        m1807x();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: w */
    public void m1805w(String str, JSONObject jSONObject, int i) {
        if (str.equals("getCurrentMenu")) {
            String str2 = "";
            try {
                str2 = C0472a.f1586U.toString();
            } catch (Exception e) {
            }
            m1822c("javascript:applican.slideMenu._getCurrentMenuSuccess(" + str2 + ");");
            return;
        }
        if (!str.equals("setMenu")) {
            if (str.equals("resetMenu")) {
                try {
                    C0472a.f1586U = new JSONArray(C0472a.f1587V.toString());
                    m1821c();
                    m1824e();
                    C0454a.m1575b(this, "slideMenuInfo", C0472a.f1586U.toString());
                    return;
                } catch (Exception e2) {
                    this.f1534l.m1853a(e2.toString());
                    return;
                }
            }
            return;
        }
        try {
            String string = jSONObject.getString("menu");
            this.f1534l.m1853a(">> menuStr " + string);
            JSONArray jSONArray = new JSONArray(string);
            if (jSONArray == null || jSONArray.length() <= 0) {
                return;
            }
            C0472a.f1586U = jSONArray;
            m1821c();
            m1824e();
            C0454a.m1575b(this, "slideMenuInfo", C0472a.f1586U.toString());
        } catch (Exception e3) {
            this.f1534l.m1853a(e3.toString());
        }
    }

    /* renamed from: x */
    private void m1807x() {
        try {
            if (this.f1474aW != null) {
                Camera.Parameters parameters = this.f1474aW.getParameters();
                parameters.setFlashMode("off");
                this.f1474aW.setParameters(parameters);
                this.f1474aW.stopPreview();
                this.f1474aW.release();
                this.f1474aW = null;
            }
        } catch (Exception e) {
        }
        try {
            this.f1534l.m1853a("stop playBackground");
            if (!this.f1529g) {
                this.f1534l.m1853a("stop playBackground exec");
                Iterator it = this.f1447W.keySet().iterator();
                while (it.hasNext()) {
                    this.f1534l.m1853a("stop playBackground exec 1");
                    MediaPlayer mediaPlayer = ((C0606et) this.f1447W.get((Integer) it.next())).f1956d;
                    try {
                        this.f1534l.m1853a("stop playBackground exec 2");
                        mediaPlayer.setOnCompletionListener(null);
                        mediaPlayer.setOnPreparedListener(null);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        this.f1534l.m1853a("stop playBackground exec 3");
                    } catch (Exception e2) {
                    }
                }
                this.f1447W.clear();
                this.f1534l.m1853a("stop playBackground exec 4");
            }
        } catch (Exception e3) {
        }
        try {
            this.f1534l.f1656s.m1913a("stopAllBGM", (JSONObject) null);
        } catch (Exception e4) {
        }
        try {
            this.f1534l.f1656s.m1913a("stopAllSE", (JSONObject) null);
        } catch (Exception e5) {
        }
        if (this.f1477aZ) {
            this.f1506bb = -1;
            m1652E();
        }
        try {
            Iterator it2 = this.f1446V.keySet().iterator();
            while (it2.hasNext()) {
                try {
                    ((AsyncTask) this.f1446V.get((Integer) it2.next())).cancel(false);
                } catch (Exception e6) {
                }
            }
            this.f1446V.clear();
            this.f1447W.clear();
        } catch (Exception e7) {
        }
        if (this.f1481ad != null) {
            m1653F();
            try {
                m1822c("javascript:applican.video._playError('CANCELED');");
            } catch (Exception e8) {
            }
        }
        this.f1507bc = false;
        this.f1508bd = false;
        try {
            this.f1510bf.m51b();
        } catch (Exception e9) {
        }
        this.f1510bf = null;
        m1656I();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:140:0x0295, code lost:
    
        if (r0.length() > 0) goto L127;
     */
    /* renamed from: x */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m1808x(java.lang.String r5, org.json.JSONObject r6, int r7) {
        /*
            Method dump skipped, instructions count: 697
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: jp.co.newphoria.html5app.WebActivity.m1808x(java.lang.String, org.json.JSONObject, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: y */
    public void m1810y() {
        try {
            this.f1472aU = null;
            this.f1473aV = 0;
            this.f1440P = new Timer();
            this.f1440P.schedule(new C0553cu(this), 0L, 3000L);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: y */
    public void m1811y(String str, JSONObject jSONObject, int i) {
        if (str.equals("goBack")) {
            try {
                this.f1540r.goBack();
                return;
            } catch (Exception e) {
                return;
            }
        }
        if (str.equals("goForward")) {
            try {
                this.f1540r.goForward();
            } catch (Exception e2) {
            }
        } else if (str.equals("reload")) {
            try {
                this.f1540r.reload();
            } catch (Exception e3) {
            }
        } else if (str.equals("clearHistory")) {
            try {
                this.f1540r.clearHistory();
            } catch (Exception e4) {
            }
        }
    }

    /* renamed from: z */
    private void m1813z() {
        try {
            if (this.f1440P != null) {
                this.f1440P.cancel();
            }
        } catch (Exception e) {
        }
        this.f1440P = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: z */
    public void m1814z(String str, JSONObject jSONObject, int i) {
        boolean z;
        if (str.equals("urlScheme")) {
            try {
                this.f1441Q = true;
                C0454a.m1568a(this.f1535m, jSONObject.getString("url"));
                return;
            } catch (Exception e) {
                this.f1441Q = false;
                m1822c("javascript:applican.launcher._urlSchemeError({code:'NOT_FOUND', message:''});");
                return;
            }
        }
        if (str.equals("webview")) {
            try {
                this.f1534l.f1661x = this.f1535m;
                String str2 = "";
                try {
                    str2 = jSONObject.getString("url");
                    if (!str2.startsWith("http")) {
                        str2 = new URL(new URL(this.f1540r.getUrl()), str2).toString();
                    }
                } catch (Exception e2) {
                }
                Intent intent = new Intent(this.f1535m, (Class<?>) WebActivity.class);
                intent.putExtra("launchWebViewUrl", str2);
                try {
                    z = jSONObject.getBoolean("withoutToolbar");
                } catch (Exception e3) {
                    z = false;
                }
                if (z) {
                    intent.putExtra("toolbar", "no");
                } else {
                    intent.putExtra("toolbar", "yes");
                }
                startActivity(intent);
            } catch (Exception e4) {
            }
        }
    }

    /* renamed from: a */
    public Bitmap m1816a(File file, int i, int i2, int i3) {
        Bitmap decodeFile;
        if (file == null) {
            decodeFile = null;
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int sqrt = options.outWidth * options.outHeight > 640000 ? (int) (Math.sqrt((options.outWidth * options.outHeight) / 640000.0d) + 1.0d) : 1;
            options.inJustDecodeBounds = false;
            options.inSampleSize = sqrt;
            decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            if (decodeFile == null) {
                decodeFile = null;
            } else {
                int width = decodeFile.getWidth();
                int height = decodeFile.getHeight();
                float m1657a = m1657a(i, i2, width, height);
                Matrix matrix = new Matrix();
                matrix.postScale(m1657a, m1657a);
                if (i3 != 0) {
                    matrix.postRotate(i3);
                }
                if (m1657a != 1.0f || i3 != 0) {
                    Bitmap createBitmap = Bitmap.createBitmap(decodeFile, 0, 0, width, height, matrix, true);
                    decodeFile.recycle();
                    decodeFile = createBitmap;
                }
            }
        }
        System.gc();
        return decodeFile;
    }

    /* renamed from: a */
    public void mo1617a() {
        m1804w();
        this.f1494aq = false;
    }

    /* renamed from: a */
    public void m1817a(int i, String str, boolean z) {
        m1822c("javascript:applican.webSocket._wsOnClose({code:" + i + ", reason:'" + m1717e(str) + "', wasClean:true});");
    }

    /* renamed from: a */
    public void m1818a(Exception exc) {
        m1822c("javascript:applican.webSocket._wsOnError({data:'" + m1717e(exc.getMessage()) + "'});");
    }

    /* renamed from: b */
    public void m1819b() {
        this.f1534l.m1853a(">>> web initView " + this.f1448X);
        m1648A();
        if (!C0472a.f1574I || this.f1478aa) {
            this.f1449Y = 0;
        } else {
            this.f1449Y = C0472a.f1580O;
        }
        m1758k();
        this.f1538p.removeAllViews();
        m1821c();
    }

    /* renamed from: b */
    public void m1820b(String str) {
        if (C0475d.m1866a().m1869a(str)) {
            this.f1529g = false;
            m1804w();
            if (str.startsWith("local://")) {
                this.f1540r.loadUrl("file://" + this.f1535m.getFilesDir().toString() + "/contents/web/" + str.substring(8));
                this.f1434J = true;
                return;
            }
            if (str.startsWith("update://")) {
                return;
            }
            this.f1540r.loadUrl(str);
            this.f1434J = true;
        }
    }

    @SuppressLint({"NewApi"})
    /* renamed from: c */
    protected void m1821c() {
        boolean z;
        this.f1538p.removeAllViews();
        this.f1545w = new LinearLayout(this);
        this.f1545w.setOrientation(1);
        RelativeLayout relativeLayout = this.f1538p;
        LinearLayout linearLayout = this.f1545w;
        this.f1534l.getClass();
        this.f1534l.getClass();
        relativeLayout.addView(linearLayout, new LinearLayout.LayoutParams(-1, -1));
        if (C0472a.f1582Q && !this.f1478aa) {
            this.f1488ak = new ArrayList();
            this.f1489al = new RelativeLayout(this);
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
            RelativeLayout relativeLayout2 = this.f1489al;
            this.f1534l.getClass();
            this.f1534l.getClass();
            relativeLayout2.addView(horizontalScrollView, new LinearLayout.LayoutParams(-1, -2));
            horizontalScrollView.setOverScrollMode(2);
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            horizontalScrollView.setVerticalScrollBarEnabled(false);
            LinearLayout linearLayout2 = new LinearLayout(this);
            linearLayout2.setOrientation(0);
            horizontalScrollView.addView(linearLayout2);
            try {
                int length = C0472a.f1586U.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject = C0472a.f1586U.getJSONObject(i);
                    String str = jSONObject.getString("image") + ".png";
                    String str2 = jSONObject.getString("image") + "_selected.png";
                    if (i == 0) {
                        str = str2;
                    }
                    C0465l c0465l = new C0465l(this, this.f1535m.getFilesDir().toString() + "/contents/web/slide_menu/" + str, this.f1535m.getFilesDir().toString() + "/contents/web/slide_menu/" + str2);
                    c0465l.setTag(Integer.valueOf(i));
                    linearLayout2.addView(c0465l, this.f1534l.m1849a(C0472a.f1584S, C0472a.f1585T));
                    c0465l.setOnClickListener(new ViewOnClickListenerC0548cp(this));
                    this.f1488ak.add(c0465l);
                }
            } catch (Exception e) {
            }
            if (C0472a.f1583R.equals("top")) {
                LinearLayout linearLayout3 = this.f1545w;
                RelativeLayout relativeLayout3 = this.f1489al;
                this.f1534l.getClass();
                this.f1534l.getClass();
                linearLayout3.addView(relativeLayout3, new LinearLayout.LayoutParams(-1, -2));
            }
        }
        this.f1490am = new RelativeLayout(this);
        this.f1490am.setBackgroundColor(-16777216);
        this.f1491an = new ImageView(this);
        this.f1491an.setImageResource(R.drawable.navibar_bg);
        this.f1491an.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.f1490am.addView(this.f1491an, this.f1534l.m1850a(0.0f, 0.0f, this.f1534l.f1638a, this.f1428D));
        C0464k c0464k = new C0464k(this);
        c0464k.setImageResource(R.drawable.rewind_to_start_01_set);
        c0464k.setOnClickListener(new ViewOnClickListenerC0564de(this));
        if ((!C0472a.f1604h.equals("demo") && !C0472a.f1604h.equals("tab")) || this.f1478aa) {
            this.f1426B = new C0464k(this);
            this.f1426B.setImageResource(R.drawable.navibar_btn_close_set);
            this.f1490am.addView(this.f1426B, this.f1534l.m1850a(this.f1534l.f1638a - 49.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
            this.f1426B.setOnClickListener(new ViewOnClickListenerC0568di(this));
        }
        this.f1546x = new C0464k(this);
        this.f1546x.setBackgroundResource(R.drawable.navibar_btn_back_set);
        this.f1490am.addView(this.f1546x, this.f1534l.m1850a(30.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
        this.f1546x.setOnClickListener(new ViewOnClickListenerC0569dj(this));
        this.f1546x.setEnabled(false);
        this.f1547y = new C0464k(this);
        this.f1547y.setBackgroundResource(R.drawable.navibar_btn_forward_set);
        this.f1490am.addView(this.f1547y, this.f1534l.m1850a(90.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
        this.f1547y.setOnClickListener(new ViewOnClickListenerC0570dk(this));
        this.f1547y.setEnabled(false);
        this.f1425A = new C0464k(this);
        this.f1425A.setBackgroundResource(R.drawable.pause_set);
        this.f1425A.setOnClickListener(new ViewOnClickListenerC0571dl(this));
        this.f1548z = new C0464k(this);
        this.f1548z.setBackgroundResource(R.drawable.navibar_btn_reload_set);
        this.f1490am.addView(this.f1548z, this.f1534l.m1850a(150.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
        this.f1548z.setOnClickListener(new ViewOnClickListenerC0572dm(this));
        if (this.f1478aa) {
            if (this.f1479ab) {
                z = true;
            }
            z = false;
        } else {
            if (C0472a.f1620x) {
                z = true;
            }
            z = false;
        }
        if (!z) {
            this.f1428D = 0.0f;
            this.f1490am.setVisibility(8);
        }
        if (C0472a.f1621y) {
            this.f1437M = new C0233r(this);
            this.f1437M.setBackgroundColor(Color.rgb(34, 34, 34));
            this.f1540r = (WebView) this.f1437M.getRefreshableView();
            this.f1534l.getClass();
            this.f1534l.getClass();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
            layoutParams.weight = 1.0f;
            this.f1545w.addView(this.f1437M, layoutParams);
        } else {
            this.f1540r = new WebView(this);
            this.f1534l.getClass();
            this.f1534l.getClass();
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -1);
            layoutParams2.weight = 1.0f;
            this.f1545w.addView(this.f1540r, layoutParams2);
        }
        if (this.f1534l.f1654q <= this.f1534l.f1629F || C0472a.f1615s) {
        }
        if (C0472a.f1582Q && !this.f1478aa && C0472a.f1583R.equals("bottom")) {
            LinearLayout linearLayout4 = this.f1545w;
            RelativeLayout relativeLayout4 = this.f1489al;
            this.f1534l.getClass();
            this.f1534l.getClass();
            linearLayout4.addView(relativeLayout4, new LinearLayout.LayoutParams(-1, -2));
        }
        LinearLayout linearLayout5 = this.f1545w;
        RelativeLayout relativeLayout5 = this.f1490am;
        this.f1534l.getClass();
        this.f1534l.getClass();
        linearLayout5.addView(relativeLayout5, new LinearLayout.LayoutParams(-1, -2));
        this.f1540r.setScrollBarStyle(0);
        if (this.f1437M != null) {
            this.f1437M.setOnRefreshListener(new C0573dn(this));
        }
        WebSettings settings = this.f1540r.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);
        settings.setSavePassword(true);
        settings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= 16) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        try {
            Field declaredField = settings.getClass().getDeclaredField("mBuiltInZoomControls");
            declaredField.setAccessible(true);
            declaredField.set(settings, false);
        } catch (Exception e2) {
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    this.f1540r.getSettings().setDisplayZoomControls(false);
                }
            } catch (Exception e3) {
                if (e3.getLocalizedMessage() != null) {
                    Log.e("Error", e3.getLocalizedMessage());
                }
            }
        }
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(getApplicationContext().getDir("database", 0).getPath());
        if (Build.VERSION.SDK_INT >= 17) {
            settings.setMediaPlaybackRequiresUserGesture(C0472a.f1595ac);
        }
        if (C0472a.f1590Y == null || C0472a.f1590Y.length() <= 0) {
            settings.setUserAgentString(C0472a.f1591Z + C0472a.f1589X);
        } else {
            settings.setUserAgentString(C0472a.f1590Y + C0472a.f1589X);
        }
        this.f1540r.setWebChromeClient(new C0530by(this));
        this.f1540r.setWebViewClient(new C0531bz(this));
        this.f1543u = new RelativeLayout(this);
        RelativeLayout relativeLayout6 = this.f1538p;
        RelativeLayout relativeLayout7 = this.f1543u;
        this.f1534l.getClass();
        this.f1534l.getClass();
        relativeLayout6.addView(relativeLayout7, new LinearLayout.LayoutParams(-1, -1));
        C0463j c0463j = new C0463j(this);
        c0463j.setBackgroundColor(Color.argb(128, 0, 0, 0));
        RelativeLayout relativeLayout8 = this.f1543u;
        this.f1534l.getClass();
        this.f1534l.getClass();
        relativeLayout8.addView(c0463j, new LinearLayout.LayoutParams(-1, -1));
        this.f1544v = C0454a.m1565a(this, getResources().getString(R.string.LabelDownloading), 20, -1, 17);
        RelativeLayout relativeLayout9 = this.f1543u;
        TextView textView = this.f1544v;
        this.f1534l.getClass();
        this.f1534l.getClass();
        relativeLayout9.addView(textView, new LinearLayout.LayoutParams(-1, -1));
        this.f1543u.setVisibility(8);
        this.f1542t = new LinearLayout(this);
        RelativeLayout relativeLayout10 = this.f1538p;
        LinearLayout linearLayout6 = this.f1542t;
        this.f1534l.getClass();
        this.f1534l.getClass();
        relativeLayout10.addView(linearLayout6, new LinearLayout.LayoutParams(-1, -1));
        this.f1542t.setOrientation(1);
        this.f1542t.setGravity(17);
        int i2 = this.f1534l.f1648k < this.f1534l.f1647j ? 20 : 40;
        this.f1541s = new ProgressBar(this, null, android.R.attr.progressBarStyleLargeInverse);
        this.f1542t.addView(this.f1541s, new LinearLayout.LayoutParams(this.f1534l.m1848a(i2), this.f1534l.m1848a(i2)));
        this.f1541s.setVisibility(8);
        this.f1539q = true;
        m1768m();
    }

    /* renamed from: c */
    public void m1822c(String str) {
        m1673a(str, false);
    }

    /* renamed from: d */
    public void m1823d() {
        try {
            m1822c("javascript:applican._webViewClose();");
        } catch (Exception e) {
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:30:0x0172. Please report as an issue. */
    @Override // android.app.Activity, android.view.Window.Callback
    @SuppressLint({"NewApi"})
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && this.f1508bd) {
            try {
                String str = "{keyCode:" + keyEvent.getKeyCode() + ",shiftKey:" + keyEvent.isShiftPressed() + ",altKey:" + keyEvent.isAltPressed() + ",functionKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isFunctionPressed() : false) + ",ctrlKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isCtrlPressed() : false) + ",symKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isSymPressed() : false) + ",metaKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isMetaPressed() : false) + "}";
                this.f1534l.m1853a(str);
                m1673a("javascript:applican.keyboard._watchKeyUpCallback(" + str + ");", true);
            } catch (Exception e) {
            }
        }
        if (keyEvent.getAction() == 0) {
            if (this.f1507bc) {
                try {
                    String str2 = "{keyCode:" + keyEvent.getKeyCode() + ",shiftKey:" + keyEvent.isShiftPressed() + ",altKey:" + keyEvent.isAltPressed() + ",functionKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isFunctionPressed() : false) + ",ctrlKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isCtrlPressed() : false) + ",symKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isSymPressed() : false) + ",metaKey:" + (this.f1534l.f1654q >= 11 ? keyEvent.isMetaPressed() : false) + "}";
                    this.f1534l.m1853a(str2);
                    m1673a("javascript:applican.keyboard._watchKeyDownCallback(" + str2 + ");", true);
                } catch (Exception e2) {
                }
            }
            switch (keyEvent.getKeyCode()) {
                case C0650c.PullToRefresh_ptrMode /* 4 */:
                    try {
                        m1822c("javascript:applican.event._callback('backbutton');");
                    } catch (Exception e3) {
                    }
                    if (C0472a.f1615s) {
                    }
                    if (!C0472a.f1593aa) {
                        return true;
                    }
                    if (C0472a.f1570E && this.f1540r != null && this.f1540r.canGoBack()) {
                        try {
                            this.f1529g = false;
                            m1804w();
                            this.f1540r.goBack();
                            return true;
                        } catch (Exception e4) {
                            return true;
                        }
                    }
                    if (!this.f1478aa) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.f1535m);
                        builder.setTitle(getResources().getString(R.string.LabelConfirmation));
                        builder.setMessage(getResources().getString(R.string.LabelTerminatethisapplication));
                        builder.setPositiveButton(getResources().getString(R.string.LabelTerminate), new DialogInterfaceOnClickListenerC0543ck(this)).setNegativeButton(getResources().getString(R.string.LabelButtonCancel), new DialogInterfaceOnClickListenerC0542cj(this));
                        builder.show();
                        return true;
                    }
                    this.f1529g = false;
                    m1804w();
                    finish();
                    if (this.f1534l.f1661x == null) {
                        return true;
                    }
                    try {
                        this.f1534l.f1661x.m1823d();
                        this.f1534l.f1661x = null;
                        return true;
                    } catch (Exception e5) {
                        return true;
                    }
                case 24:
                    try {
                        m1822c("javascript:applican.event._callback('volumeupbutton');");
                        break;
                    } catch (Exception e6) {
                        break;
                    }
                case 25:
                    try {
                        m1822c("javascript:applican.event._callback('volumedownbutton');");
                        break;
                    } catch (Exception e7) {
                        break;
                    }
                case 82:
                    try {
                        m1822c("javascript:applican.event._callback('menubutton');");
                    } catch (Exception e8) {
                    }
                    return false;
                case 84:
                    try {
                        m1822c("javascript:applican.event._callback('searchbutton');");
                        return true;
                    } catch (Exception e9) {
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    /* renamed from: e */
    public void m1824e() {
        String str;
        this.f1529g = false;
        m1804w();
        this.f1534l.m1853a(">>>>> loadTop");
        if (C0472a.f1604h.equals("tab")) {
            str = C0472a.f1576K[this.f1448X].startsWith("http") ? C0472a.f1576K[this.f1448X] : "file://" + this.f1535m.getFilesDir().toString() + "/contents/web/" + C0472a.f1576K[this.f1448X];
            if (!this.f1434J && this.f1448X == 0 && C0472a.f1577L != null && C0472a.f1577L.length() > 0) {
                str = C0472a.f1577L.startsWith("http") ? C0472a.f1577L : "file://" + this.f1535m.getFilesDir().toString() + "/contents/web/" + C0472a.f1577L;
            }
        } else {
            str = null;
        }
        if (C0472a.f1582Q && !this.f1478aa) {
            try {
                String string = C0472a.f1586U.getJSONObject(0).getString("url");
                str = string.startsWith("http") ? string : "file://" + this.f1535m.getFilesDir().toString() + "/contents/web/" + string;
                this.f1534l.m1853a("loadTop isSlideMenu " + str);
            } catch (Exception e) {
            }
        }
        if (this.f1478aa) {
            str = this.f1480ac;
        } else if (this.f1540r != null) {
            if (this.f1534l.f1663z != null) {
                this.f1534l.m1853a("_sys.scheme >>> " + this.f1534l.f1663z);
                try {
                    if (this.f1534l.f1663z.indexOf("url=") > 0) {
                        String decode = URLDecoder.decode(this.f1534l.f1663z.substring(this.f1534l.f1663z.indexOf("url=") + 4), "UTF8");
                        this.f1534l.m1853a("scheme:" + decode);
                        if (decode != null) {
                            if (decode.startsWith("local://")) {
                                str = "file://" + this.f1535m.getFilesDir().toString() + "/contents/web/" + decode.substring(8);
                            } else if (decode.startsWith("http") && C0475d.m1866a().m1869a(decode)) {
                                str = decode;
                            }
                        }
                    }
                } catch (Exception e2) {
                }
            }
            this.f1534l.f1663z = null;
        }
        if (this.f1540r != null) {
            if (str == null) {
                String str2 = this.f1535m.getFilesDir().toString() + "/contents/web/index.html";
                if (!new File(str2).exists()) {
                    return;
                } else {
                    str = "file://" + str2;
                }
            }
            this.f1540r.loadUrl(str);
            this.f1434J = true;
        }
    }

    /* renamed from: f */
    public void m1825f() {
        if (this.f1540r != null) {
            try {
                this.f1534l.m1853a("" + this.f1448X + " javascript:applican.event._callback('resume');");
                m1822c("javascript:applican.event._callback('resume');");
            } catch (Exception e) {
            }
        }
        this.f1486ai = false;
    }

    /* renamed from: f */
    public void m1826f(String str) {
        m1822c("javascript:applican.arpl._showARSuccess('" + m1717e(str) + "');");
    }

    @Override // android.app.Activity
    public void finish() {
        if (this.f1451a != null) {
            try {
                this.f1451a.unregisterListener(this);
            } catch (Exception e) {
            }
            this.f1451a = null;
        }
        if (this.f1463aL != null) {
            try {
                this.f1463aL.removeUpdates(this);
            } catch (Exception e2) {
            }
            this.f1463aL = null;
        }
        this.f1500aw = 0;
        try {
            this.f1501ax.cancel();
        } catch (Exception e3) {
        }
        this.f1455aD = 0;
        try {
            this.f1456aE.cancel();
        } catch (Exception e4) {
        }
        this.f1464aM = 0;
        try {
            this.f1465aN.cancel();
        } catch (Exception e5) {
        }
        this.f1529g = false;
        m1807x();
        try {
            unregisterReceiver(this.f1524bt);
        } catch (Exception e6) {
        }
        try {
            Thread.sleep(200L);
        } catch (Exception e7) {
        }
        super.finish();
    }

    /* renamed from: g */
    public void m1827g() {
        if (this.f1540r != null) {
            this.f1486ai = true;
            try {
                this.f1534l.m1853a("" + this.f1448X + " javascript:applican.event._callback('pause');");
                m1822c("javascript:applican.event._callback('pause');");
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: g */
    public void m1828g(String str) {
        m1822c("javascript:applican.webSocket._wsOnMessage({data:'" + m1717e(str) + "'});");
    }

    /* renamed from: h */
    public void m1829h() {
        if (this.f1540r != null) {
            this.f1540r.clearCache(true);
            this.f1540r.clearHistory();
        }
    }

    /* renamed from: i */
    public boolean m1830i() {
        return this.f1434J;
    }

    /* renamed from: j */
    public void m1831j() {
        m1822c("javascript:applican.webSocket._wsOnOpen();");
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        String jSONArray;
        String str;
        String str2;
        String string;
        int i3;
        String str3;
        byte[] bArr;
        String string2;
        if (intent == null) {
        }
        super.onActivityResult(i, i2, intent);
        switch (i) {
            case C0650c.PullToRefresh_ptrScrollingWhileRefreshingEnabled /* 13 */:
                if (i2 != -1) {
                    m1822c("javascript:applican.capture._captureWithOverlayError('');");
                    break;
                } else {
                    Bundle extras = intent.getExtras();
                    m1667a(extras.getInt("CaptureMethod"), extras.getString("CaptureJSON"));
                    break;
                }
        }
        if (i == 1) {
            boolean z = false;
            if (i2 == -1) {
                try {
                    Bundle extras2 = intent.getExtras();
                    if (extras2 != null && (string2 = extras2.getString("barcode_result")) != null && string2.length() > 0) {
                        this.f1534l.m1853a(">>>>>>>>>>>" + m1717e(string2));
                        m1822c("javascript:applican.barcode._captureBarcodeSuccess(0, '" + m1717e(string2) + "');");
                        z = true;
                    }
                } catch (Exception e) {
                }
            }
            if (z) {
                return;
            }
            m1822c("javascript:applican.barcode._captureBarcodeError();");
            return;
        }
        if (i == 4) {
            if (i2 == -1) {
                m1822c("javascript:applican.camera._takePictureSuccess('LOCAL_SAVE_OK');");
                return;
            } else {
                m1822c("javascript:applican.camera._takePictureError();");
                return;
            }
        }
        if (i == 2 || i == 5) {
            if (i2 != -1) {
                if (this.f1522br == null) {
                    this.f1522br = getPreferences(0);
                }
                Uri parse = Uri.parse(this.f1522br.getString("pictureUri", ""));
                if (parse != null) {
                    try {
                        getContentResolver().delete(parse, null, null);
                    } catch (Exception e2) {
                    }
                    this.f1522br.edit().remove("pictureUri");
                }
                m1822c("javascript:applican.camera._takePictureError();");
                return;
            }
            if (this.f1522br == null) {
                this.f1522br = getPreferences(0);
            }
            this.f1521bq = (intent == null || intent.getData() == null) ? Uri.parse(this.f1522br.getString("pictureUri", "")) : intent.getData();
            m1669a("送信中...");
            if (i == 2) {
                new C0610ex(this, true).start();
                return;
            } else {
                new C0610ex(this, false).start();
                return;
            }
        }
        if (i == 3) {
            if (i2 != -1) {
                m1822c("javascript:applican.camera._takePictureError();");
                return;
            }
            this.f1521bq = intent.getData();
            m1669a("送信中...");
            new C0610ex(this, false).start();
            return;
        }
        if (i == 6) {
            if (i2 != -1) {
                this.f1534l.m1853a("no data.");
                m1822c("javascript:applican.camera._getPictureError('no data.');");
                return;
            }
            if (intent == null) {
            }
            if (intent != null) {
                try {
                    try {
                        if (intent.getData() != null) {
                            this.f1521bq = intent.getData();
                        }
                    } catch (IOException e3) {
                        this.f1534l.m1853a("IOException");
                        return;
                    }
                } catch (Exception e4) {
                    this.f1534l.m1853a(e4.getMessage());
                    m1822c("javascript:applican.camera._getPictureError('" + m1717e(e4.getMessage()) + "'));");
                    return;
                }
            }
            int i4 = 50;
            try {
                i4 = this.f1528f.getInt("quality");
            } catch (Exception e5) {
            }
            int i5 = 0;
            try {
                i5 = this.f1528f.getInt("destinationType");
            } catch (Exception e6) {
            }
            int i6 = 0;
            try {
                i6 = this.f1528f.getInt("encodingType");
            } catch (Exception e7) {
            }
            int i7 = 1024;
            try {
                i7 = this.f1528f.getInt("targetWidth");
            } catch (Exception e8) {
            }
            int i8 = 1024;
            try {
                i8 = this.f1528f.getInt("targetHeight");
            } catch (Exception e9) {
            }
            boolean z2 = false;
            try {
                z2 = this.f1528f.getBoolean("saveToPhotoAlbum");
            } catch (Exception e10) {
            }
            boolean z3 = true;
            try {
                z3 = this.f1528f.getBoolean("correctOrientation");
            } catch (Exception e11) {
            }
            try {
                i3 = this.f1528f.getInt("sourceType");
            } catch (Exception e12) {
                i3 = 1;
            }
            File file = new File(m1665a(this.f1535m, this.f1521bq));
            String name = file.getName();
            boolean z4 = name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".gif");
            if (z4) {
                Bitmap m1816a = m1816a(file, i7, i8, !z3 ? 0 : m1739h(file.getPath()));
                if (i3 == 1) {
                    try {
                        getContentResolver().delete(this.f1521bq, null, null);
                    } catch (Exception e13) {
                    }
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                m1816a.compress(i6 == 1 ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, i4, byteArrayOutputStream);
                bArr = byteArrayOutputStream.toByteArray();
                m1816a.recycle();
                System.gc();
                if (z2) {
                    long currentTimeMillis = System.currentTimeMillis();
                    String str4 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
                    File file2 = new File(str4);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String format = new SimpleDateFormat("yyyyMMdd_HHmmss_S").format(new Date(currentTimeMillis));
                    String str5 = format + (i6 == 1 ? ".png" : ".jpg");
                    File file3 = new File(str4 + "/" + str5);
                    FileOutputStream fileOutputStream = new FileOutputStream(file3);
                    fileOutputStream.write(bArr);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("title", format);
                    contentValues.put("_display_name", str5);
                    contentValues.put("mime_type", "image/" + (i6 == 1 ? "png" : "jpeg"));
                    contentValues.put("_data", str4 + "/" + str5);
                    contentValues.put("datetaken", Long.valueOf(currentTimeMillis));
                    contentValues.put("_size", Integer.valueOf(bArr.length));
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    str3 = "file://" + file3.getAbsolutePath();
                } else {
                    str3 = null;
                }
            } else {
                str3 = "file://" + file.getAbsolutePath();
                bArr = null;
            }
            if (i5 == 0) {
                m1822c("javascript:applican.camera._getPictureSuccess('" + Base64.encodeToString(!z4 ? C0454a.m1577b(file.getAbsolutePath()) : bArr, 2) + "');");
                return;
            }
            if (str3 == null) {
                File file4 = new File(m1649B() + "/" + (new SimpleDateFormat("yyyyMMdd_HHmmss_S").format(new Date(System.currentTimeMillis())) + (i6 == 1 ? ".png" : ".jpg")));
                FileOutputStream fileOutputStream2 = new FileOutputStream(file4);
                fileOutputStream2.write(bArr);
                fileOutputStream2.flush();
                fileOutputStream2.close();
                str3 = "file://" + file4.getAbsolutePath();
            }
            m1822c("javascript:applican.camera._getPictureSuccess('" + str3 + "');");
            return;
        }
        if (i == 7) {
            if (i2 != -1) {
                m1822c("javascript:applican.list._showError('CANCELED');");
                return;
            }
            try {
                Bundle extras3 = intent.getExtras();
                if (extras3 != null && (string = extras3.getString("value")) != null && string.length() > 0) {
                    boolean z5 = false;
                    try {
                        Double.parseDouble(string);
                        z5 = true;
                    } catch (Exception e14) {
                    }
                    m1822c(z5 ? "javascript:applican.list._showSuccess(" + string + ");" : "javascript:applican.list._showSuccess('" + m1717e(string) + "');");
                    return;
                }
            } catch (Exception e15) {
            }
            m1822c("javascript:applican.list._showSuccess('');");
            return;
        }
        if (i == 8) {
            m1822c("javascript:applican.popinfo._showError('CANCELED');");
            return;
        }
        if (i == 9) {
            if (intent != null) {
                try {
                    Uri data = intent.getData();
                    data.getPath();
                    Cursor query = this.f1535m.getContentResolver().query(data, null, null, null, null);
                    query.moveToFirst();
                    String string3 = query.getString(query.getColumnIndex("_data"));
                    File file5 = new File(string3);
                    JSONArray jSONArray2 = new JSONArray();
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("fullPath", "file://" + string3);
                    jSONObject.put("name", file5.getName());
                    jSONObject.put("lastModifiedDate", (int) (((float) file5.lastModified()) / 1000.0f));
                    jSONObject.put("type", m1708d(string3));
                    jSONObject.put("size", file5.length());
                    jSONArray2.put(jSONObject);
                    str2 = jSONArray2.toString();
                } catch (Exception e16) {
                    str2 = null;
                }
            } else {
                str2 = null;
            }
            if (str2 == null || str2.length() <= 0) {
                m1822c("javascript:applican.capture._captureAudioError({code:3, message:''});");
                return;
            } else {
                m1822c("javascript:applican.capture._captureAudioSuccess(" + str2 + ");");
                return;
            }
        }
        if (i == 11) {
            if (intent != null) {
                try {
                    Uri data2 = intent.getData();
                    this.f1534l.m1853a(data2.getPath());
                    Cursor query2 = this.f1535m.getContentResolver().query(data2, null, null, null, null);
                    query2.moveToFirst();
                    String string4 = query2.getString(query2.getColumnIndex("_data"));
                    this.f1534l.m1853a(string4);
                    File file6 = new File(string4);
                    JSONArray jSONArray3 = new JSONArray();
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("fullPath", "file://" + string4);
                    jSONObject2.put("name", file6.getName());
                    jSONObject2.put("lastModifiedDate", (int) (((float) file6.lastModified()) / 1000.0f));
                    jSONObject2.put("type", m1708d(string4));
                    jSONObject2.put("size", file6.length());
                    jSONArray3.put(jSONObject2);
                    str = jSONArray3.toString();
                } catch (Exception e17) {
                    str = null;
                }
            } else {
                str = null;
            }
            if (str == null || str.length() <= 0) {
                m1822c("javascript:applican.capture._captureVideoError({code:3, message:''});");
                return;
            }
            String str6 = "javascript:applican.capture._captureVideoSuccess(" + str + ");";
            this.f1534l.m1853a(str6);
            m1822c(str6);
            return;
        }
        if (i != 10) {
            if (i == 12) {
                if (i2 > 0) {
                    m1822c("javascript:applican.bluetooth._discoverableOnSuccess(true);");
                    return;
                } else {
                    m1822c("javascript:applican.bluetooth._discoverableOnSuccess(false);");
                    return;
                }
            }
            return;
        }
        Uri uri = null;
        if (i2 == -1) {
            if (intent == null || intent.getData() == null) {
                this.f1534l.m1853a(">> intent null");
            } else {
                uri = intent.getData();
            }
        }
        if (uri != null) {
            try {
                this.f1534l.m1853a(uri.getPath());
                Cursor query3 = this.f1535m.getContentResolver().query(uri, null, null, null, null);
                query3.moveToFirst();
                String string5 = query3.getString(query3.getColumnIndex("_data"));
                this.f1534l.m1853a(string5);
                File file7 = new File(string5);
                JSONArray jSONArray4 = new JSONArray();
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put("fullPath", "file://" + string5);
                jSONObject3.put("name", file7.getName());
                jSONObject3.put("lastModifiedDate", (int) (((float) file7.lastModified()) / 1000.0f));
                jSONObject3.put("type", m1708d(string5));
                jSONObject3.put("size", file7.length());
                jSONArray4.put(jSONObject3);
                jSONArray = jSONArray4.toString();
            } catch (Exception e18) {
                this.f1534l.m1853a(e18.toString());
            }
            if (jSONArray != null || jSONArray.length() <= 0) {
                m1822c("javascript:applican.capture._captureImageError({code:3, message:''});");
            }
            String str7 = "javascript:applican.capture._captureImageSuccess(" + jSONArray + ");";
            this.f1534l.m1853a(str7);
            m1822c(str7);
            return;
        }
        jSONArray = null;
        if (jSONArray != null) {
        }
        m1822c("javascript:applican.capture._captureImageError({code:3, message:''});");
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.f1534l.m1853a(">>web onConfigurationChanged " + this.f1448X);
        this.f1534l.m1853a(">>1");
        if (this.f1539q) {
            if (this.f1478aa) {
                this.f1534l.f1651n = this.f1534l.m1847a(getWindow());
                this.f1534l.f1652o = this.f1534l.f1651n;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                this.f1534l.m1852a(displayMetrics);
            }
            m1758k();
            this.f1428D = 42.0f;
            this.f1431G = 37;
            this.f1534l.m1853a("" + this.f1534l.f1647j + ", " + this.f1534l.f1648k);
            this.f1534l.m1853a("" + this.f1534l.f1638a + ", " + this.f1534l.f1639b);
            if (this.f1534l.f1648k < this.f1534l.f1647j) {
                this.f1428D = 21.0f;
                this.f1431G = 18;
            }
            this.f1429E = (this.f1534l.f1639b - this.f1428D) - this.f1449Y;
            if (!C0472a.f1620x) {
                this.f1428D = 0.0f;
                this.f1490am.setVisibility(8);
                this.f1534l.m1853a("IS_NAVI_BAR GONE");
            }
            this.f1490am.updateViewLayout(this.f1491an, this.f1534l.m1850a(0.0f, 0.0f, this.f1534l.f1638a, this.f1428D));
            this.f1490am.updateViewLayout(this.f1546x, this.f1534l.m1850a(30.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
            this.f1490am.updateViewLayout(this.f1547y, this.f1534l.m1850a(90.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
            this.f1490am.updateViewLayout(this.f1548z, this.f1534l.m1850a(150.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
            if (this.f1478aa) {
                this.f1490am.updateViewLayout(this.f1426B, this.f1534l.m1850a(this.f1534l.f1638a - 49.0f, ((this.f1428D - this.f1431G) / 2.0f) + 1.0f, this.f1431G, this.f1431G));
            }
            if (C0472a.f1621y) {
            }
            Configuration configuration2 = getResources().getConfiguration();
            if (this.f1442R != configuration2.orientation) {
                try {
                    if (configuration2.orientation == 2) {
                        m1822c("javascript:applican.event._orientation('LANDSCAPE');");
                    } else if (configuration2.orientation == 1) {
                        m1822c("javascript:applican.event._orientation('PORTRAIT');");
                    }
                } catch (Exception e) {
                }
                this.f1487aj = true;
            }
            this.f1442R = configuration2.orientation;
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        this.f1534l = C0473b.m1845a();
        this.f1535m = this;
        this.f1486ai = false;
        this.f1439O = (ConnectivityManager) getSystemService("connectivity");
        if (this.f1534l.f1654q <= this.f1534l.f1629F || !C0472a.f1615s || !C0472a.f1617u.equals("debug")) {
        }
        if (!C0472a.f1604h.equals("demo")) {
            this.f1429E = (this.f1534l.f1639b - this.f1428D) - this.f1449Y;
        }
        try {
            this.f1433I = getIntent().getIntExtra("index", 0);
        } catch (Exception e) {
        }
        try {
            this.f1448X = getIntent().getIntExtra("tab_index", 0);
            this.f1534l.m1853a(">>> webview create " + this.f1448X);
        } catch (Exception e2) {
        }
        try {
            Intent intent = getIntent();
            this.f1480ac = intent.getStringExtra("launchWebViewUrl");
            if (this.f1480ac != null && this.f1480ac.length() > 0) {
                this.f1478aa = true;
                String stringExtra = intent.getStringExtra("toolbar");
                this.f1534l.m1853a(">>>toolbar " + stringExtra);
                if (stringExtra.equals("no")) {
                    this.f1479ab = false;
                } else {
                    this.f1479ab = true;
                }
            }
        } catch (Exception e3) {
        }
        this.f1537o = new DetectableKeyboardEventLayout(this);
        this.f1537o.setOrientation(1);
        this.f1537o.setBackgroundColor(-16777216);
        this.f1537o.setGravity(17);
        DetectableKeyboardEventLayout detectableKeyboardEventLayout = this.f1537o;
        this.f1534l.getClass();
        this.f1534l.getClass();
        setContentView(detectableKeyboardEventLayout, new LinearLayout.LayoutParams(-1, -1));
        this.f1537o.setKeyboardListener(new C0529bx(this));
        this.f1538p = new RelativeLayout(this);
        this.f1538p.setBackgroundColor(-16777216);
        DetectableKeyboardEventLayout detectableKeyboardEventLayout2 = this.f1537o;
        RelativeLayout relativeLayout = this.f1538p;
        this.f1534l.getClass();
        this.f1534l.getClass();
        detectableKeyboardEventLayout2.addView(relativeLayout, new LinearLayout.LayoutParams(-1, -1));
        m1819b();
        this.f1450Z = true;
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (C0472a.f1604h.equals("demo")) {
        }
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(Location location) {
        this.f1534l.m1853a("onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
        if (this.f1464aM == 3000) {
            this.f1467aP = true;
            m1822c("javascript:applican.geolocation._getCurrentPositionSuccess(" + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getAccuracy() + ", " + location.getAccuracy() + ", " + location.getBearing() + ", " + location.getSpeed() + ", " + location.getTime() + ");");
            m1798u();
            this.f1464aM = 0;
            return;
        }
        if (this.f1464aM == 3001) {
            this.f1467aP = true;
            m1673a("javascript:applican.geolocation._watchPositionSuccess(" + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getAccuracy() + ", " + location.getAccuracy() + ", " + location.getBearing() + ", " + location.getSpeed() + ", " + location.getTime() + ");", true);
        }
    }

    @Override // android.app.Activity
    public void onPause() {
        if (this.f1540r != null && this.f1481ad != null) {
            m1653F();
            try {
                m1822c("javascript:applican.video._playError('CANCELED');");
            } catch (Exception e) {
            }
        }
        m1813z();
        if (this.f1451a != null) {
            try {
                this.f1451a.unregisterListener(this);
            } catch (Exception e2) {
            }
            this.f1451a = null;
        }
        if (this.f1462aK) {
            try {
                this.f1504b.unregisterListener(this.f1533k);
            } catch (Exception e3) {
            }
            this.f1504b = null;
        }
        if (this.f1463aL != null) {
            try {
                this.f1463aL.removeUpdates(this);
            } catch (Exception e4) {
            }
            this.f1463aL = null;
        }
        unregisterReceiver(this.f1523bs);
        try {
            if (this.f1540r != null) {
                Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(this.f1540r, (Object[]) null);
                this.f1540r.pauseTimers();
            }
        } catch (Exception e5) {
        }
        super.onPause();
    }

    @Override // android.location.LocationListener
    public void onProviderDisabled(String str) {
    }

    @Override // android.location.LocationListener
    public void onProviderEnabled(String str) {
    }

    @Override // android.app.Activity
    public void onResume() {
        if (this.f1534l != null) {
            this.f1534l.m1853a("webView onResume " + this.f1448X);
        }
        super.onResume();
        if (this.f1435K && this.f1440P == null) {
            m1810y();
        }
        try {
            if (this.f1540r != null) {
                Class.forName("android.webkit.WebView").getMethod("onResume", (Class[]) null).invoke(this.f1540r, (Object[]) null);
                this.f1540r.resumeTimers();
                if (C0472a.f1568C && !this.f1441Q) {
                    m1824e();
                } else if (!this.f1494aq && !this.f1478aa && C0472a.f1574I && this.f1448X > 0) {
                    m1824e();
                    this.f1494aq = true;
                }
            }
        } catch (Exception e) {
        }
        this.f1441Q = false;
        if (this.f1540r != null && !this.f1486ai) {
            try {
                m1822c("javascript:applican.event._callback('appear');");
                this.f1534l.m1853a("" + this.f1448X + " javascript:applican.event._callback('appear');");
            } catch (Exception e2) {
            }
        }
        if (this.f1500aw == 1001) {
            m1785q();
        }
        if (this.f1455aD == 2001) {
            m1795t();
        }
        if (this.f1464aM == 3001) {
            m1801v();
        }
        if (this.f1462aK) {
            try {
                this.f1504b = (SensorManager) getSystemService("sensor");
                this.f1504b.registerListener(this.f1533k, this.f1504b.getDefaultSensor(1), 1);
            } catch (Exception e3) {
            }
        }
        if (!C0472a.f1604h.equals("demo") || this.f1540r != null) {
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.f1523bs, intentFilter);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case C0650c.PullToRefresh_ptrHeaderBackground /* 1 */:
                this.f1495ar = (float[]) sensorEvent.values.clone();
                this.f1499av = true;
                this.f1457aF = this.f1495ar[0] / (-this.f1436L);
                this.f1458aG = this.f1495ar[1] / (-this.f1436L);
                this.f1459aH = this.f1495ar[2] / (-this.f1436L);
                this.f1460aI = System.currentTimeMillis();
                if (this.f1455aD == 2000) {
                    m1822c("javascript:applican.accelerometer._getCurrentAccelerationSuccess(" + this.f1457aF + ", " + this.f1458aG + ", " + this.f1459aH + ", " + this.f1460aI + ");");
                    m1792s();
                    this.f1455aD = 0;
                    return;
                }
                break;
            case C0650c.PullToRefresh_ptrHeaderTextColor /* 2 */:
                this.f1496as = (float[]) sensorEvent.values.clone();
                break;
        }
        if (this.f1496as == null || this.f1495ar == null || !this.f1499av) {
            return;
        }
        this.f1499av = false;
        float[] fArr = new float[16];
        SensorManager.getRotationMatrix(fArr, new float[16], this.f1495ar, this.f1496as);
        m1683a(fArr, new float[3]);
        float degrees = (float) Math.toDegrees(r1[0]);
        if (degrees < 0.0f) {
            degrees += 360.0f;
        }
        this.f1502ay = degrees;
        this.f1503az = degrees;
        this.f1452aA = 0.0f;
        this.f1453aB = this.f1460aI;
        if (this.f1500aw == 1000) {
            m1822c("javascript:applican.compass._getCurrentHeadingSuccess(" + this.f1502ay + ", " + this.f1503az + ", " + this.f1452aA + ", " + this.f1453aB + ");");
            m1782p();
            this.f1500aw = 0;
        }
    }

    @Override // android.location.LocationListener
    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        this.f1534l.m1853a("onWindowFocusChanged:" + z);
        this.f1518bn = z;
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            this.f1527e = new MediaPlayer();
            FileInputStream fileInputStream = new FileInputStream(this.f1445U);
            if (fileInputStream != null) {
                this.f1527e.setDataSource(fileInputStream.getFD());
            }
            this.f1527e.setDisplay(this.f1525c);
            this.f1527e.setOnPreparedListener(new C0561db(this));
            this.f1527e.prepare();
        } catch (Exception e) {
            this.f1534l.m1853a(e.getMessage());
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (this.f1527e != null) {
            this.f1527e.release();
            this.f1527e = null;
        }
    }
}
