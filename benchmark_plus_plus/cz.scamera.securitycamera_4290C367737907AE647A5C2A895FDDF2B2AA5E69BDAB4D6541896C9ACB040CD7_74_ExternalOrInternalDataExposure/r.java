package cz.scamera.securitycamera.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.TypedValue;
import com.google.firebase.firestore.r;
import cz.scamera.securitycamera.camera.S;
import cz.scamera.securitycamera.utils.v;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.zhanghai.android.materialprogressbar.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: Utils.java */
/* loaded from: classes.dex */
public class r {
    public static final String upperStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lowerStr = upperStr.toLowerCase(Locale.ROOT);
    public static final String digitsStr = "0123456789";
    public static final String alphanumStr = upperStr + lowerStr + digitsStr;

    public static void addImageToGallery(Context context, File file, String str) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(file));
        context.getApplicationContext().sendBroadcast(intent);
        g.a.b.a("Added image to video gallery", new Object[0]);
    }

    public static void addVideoToGallery(Context context, File file) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(file));
        context.getApplicationContext().sendBroadcast(intent);
        g.a.b.a("Added video to video gallery", new Object[0]);
    }

    public static int alarmLevelToSensitivity(int i) {
        return 28 - ((int) Math.round(Math.log10(i) / 0.15051500499248505d));
    }

    public static int alarmSensitivityToLevel(int i) {
        if (i < 1) {
            i = 1;
        }
        if (i > 16) {
            i = 16;
        }
        return (int) Math.pow(1.4142d, 28 - i);
    }

    public static int calendarDayNoToArrayNo(int i) {
        return ((i - 2) + 7) % 7;
    }

    public static int celsiusToFarenheit(int i) {
        return Math.round((i * 9.0f) / 5.0f) + 32;
    }

    public static boolean checkTimestamps(String... strArr) {
        Pattern compile = Pattern.compile(m.TIMESTAMP_REGEX);
        for (String str : strArr) {
            if (!compile.matcher(str).matches()) {
                return false;
            }
        }
        return true;
    }

    public static void cleanDir(File file) {
        if (file == null || !file.isDirectory()) {
            return;
        }
        for (File file2 : file.listFiles()) {
            deleteRecursive(file2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0030 A[Catch: all -> 0x0034, Throwable -> 0x0036, TRY_ENTER, TryCatch #6 {, blocks: (B:3:0x0006, B:11:0x001a, B:19:0x0030, B:20:0x0033), top: B:2:0x0006, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x002c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void copyFile(java.io.File r5, java.io.File r6) throws java.io.IOException {
        /*
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r5)
            r5 = 0
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L34 java.lang.Throwable -> L36
            r1.<init>(r6)     // Catch: java.lang.Throwable -> L34 java.lang.Throwable -> L36
            r6 = 1024(0x400, float:1.435E-42)
            byte[] r6 = new byte[r6]     // Catch: java.lang.Throwable -> L21 java.lang.Throwable -> L24
        Lf:
            int r2 = r0.read(r6)     // Catch: java.lang.Throwable -> L21 java.lang.Throwable -> L24
            if (r2 <= 0) goto L1a
            r3 = 0
            r1.write(r6, r3, r2)     // Catch: java.lang.Throwable -> L21 java.lang.Throwable -> L24
            goto Lf
        L1a:
            r1.close()     // Catch: java.lang.Throwable -> L34 java.lang.Throwable -> L36
            r0.close()
            return
        L21:
            r6 = move-exception
            r2 = r5
            goto L2a
        L24:
            r6 = move-exception
            throw r6     // Catch: java.lang.Throwable -> L26
        L26:
            r2 = move-exception
            r4 = r2
            r2 = r6
            r6 = r4
        L2a:
            if (r2 == 0) goto L30
            r1.close()     // Catch: java.lang.Throwable -> L33 java.lang.Throwable -> L34
            goto L33
        L30:
            r1.close()     // Catch: java.lang.Throwable -> L34 java.lang.Throwable -> L36
        L33:
            throw r6     // Catch: java.lang.Throwable -> L34 java.lang.Throwable -> L36
        L34:
            r6 = move-exception
            goto L38
        L36:
            r5 = move-exception
            throw r5     // Catch: java.lang.Throwable -> L34
        L38:
            if (r5 == 0) goto L3e
            r0.close()     // Catch: java.lang.Throwable -> L41
            goto L41
        L3e:
            r0.close()
        L41:
            throw r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: cz.scamera.securitycamera.common.r.copyFile(java.io.File, java.io.File):void");
    }

    private static boolean createPath(String str) {
        File file = new File(str);
        if (file.exists() || file.mkdirs()) {
            return true;
        }
        g.a.b.a("Failed to create directory %s", file.getAbsolutePath());
        return false;
    }

    public static void deleteDir(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        deleteRecursive(file);
    }

    public static void deleteDirIfEmpty(File file) {
        File[] listFiles;
        if (file != null && file.isDirectory() && (listFiles = file.listFiles()) != null && listFiles.length == 0 && file.delete()) {
            g.a.b.a("Dir deleted %s", file.getPath());
        }
    }

    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                deleteRecursive(file2);
            }
        }
        if (file.delete()) {
            g.a.b.a("File deleted %s", file.getName());
        } else {
            g.a.b.b("File not deleted %s", file.getName());
        }
    }

    public static boolean equalBundles(Bundle bundle, Bundle bundle2) {
        if (bundle == null || bundle2 == null || bundle.size() != bundle2.size()) {
            return false;
        }
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            Object obj2 = bundle2.get(str);
            if ((obj instanceof Bundle) && (obj2 instanceof Bundle) && !equalBundles((Bundle) obj, (Bundle) obj2)) {
                return false;
            }
            if (obj == null) {
                if (obj2 != null || !bundle2.containsKey(str)) {
                    return false;
                }
            } else if (!obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    public static String formatDateLocale(Date date) {
        return ((SimpleDateFormat) DateFormat.getDateInstance(2)).format(date);
    }

    public static String formatTimeHms(Date date) {
        return ((SimpleDateFormat) DateFormat.getTimeInstance(2)).format(date);
    }

    public static String formatTimeMinSec(Context context, int i) {
        int i2 = i / 60;
        int i3 = i % 60;
        return i2 > 0 ? i3 > 0 ? context.getString(R.string.pref_cam_siren_mins_secs, Integer.valueOf(i2), String.format(Locale.US, "%02d", Integer.valueOf(i3))) : context.getString(R.string.pref_cam_siren_minutes, Integer.valueOf(i2)) : context.getString(R.string.pref_cam_siren_seconds, Integer.valueOf(i3));
    }

    public static String formatTimeShortHm(int i) {
        return formatTimeShortHm(i / 100, i % 100);
    }

    public static String getAlarmFileNameL(String str) {
        return m.IMAGE_PREFIX_ALARM + str + m.IMAGE_SUFFIX_L + m.IMAGE_JPG_EXTENSION;
    }

    public static String getAlarmFileNameS(String str) {
        return m.IMAGE_PREFIX_ALARM + str + m.IMAGE_SUFFIX_S + m.IMAGE_JPG_EXTENSION;
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException unused) {
            g.a.b.b("Cannot get package info to get version code", new Object[0]);
            return 0;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            g.a.b.b("Cannot get package info to get version name", new Object[0]);
            return "?";
        }
    }

    public static String getAspectRatioStr(int i, int i2) {
        double d2 = i;
        double d3 = i2;
        Double.isNaN(d2);
        Double.isNaN(d3);
        double d4 = d2 / d3;
        for (int i3 = 1; i3 < 13; i3++) {
            double d5 = i3;
            Double.isNaN(d5);
            double d6 = d5 * d4;
            double round = Math.round(d6);
            Double.isNaN(round);
            double abs = Math.abs(d6 - round);
            double round2 = Math.round(d6);
            Double.isNaN(round2);
            if (abs < round2 * 0.01d) {
                return ((int) Math.round(d6)) + ":" + i3;
            }
        }
        return "?:?";
    }

    public static String getBeforeSentence(Context context, Date date, int i) {
        long currentTimeMillis = (System.currentTimeMillis() - ((date.getTime() + getUtcOffset()) - i)) / 1000;
        if (currentTimeMillis < 60) {
            return context.getString(R.string.mon_date_nejmin);
        }
        if (currentTimeMillis < 110) {
            return context.getString(R.string.mon_date_minutou);
        }
        if (currentTimeMillis < 3000) {
            return context.getString(R.string.mon_date_minutami, Integer.valueOf((int) ((currentTimeMillis + 10) / 60)));
        }
        if (currentTimeMillis < 5400) {
            return context.getString(R.string.mon_date_hodinou);
        }
        long round = Math.round(((float) currentTimeMillis) / 3600.0f);
        if (round < 24) {
            return context.getString(R.string.mon_date_hodinami, Long.valueOf(round));
        }
        if (round < 36) {
            return context.getString(R.string.mon_date_dnem);
        }
        long round2 = Math.round(((float) round) / 24.0f);
        if (round2 < 7) {
            return context.getString(R.string.mon_date_dny, Long.valueOf(round2));
        }
        if (round2 < 11) {
            return context.getString(R.string.mon_date_tydnem);
        }
        if (round2 < 30) {
            return context.getString(R.string.mon_date_tydny, Integer.valueOf(Math.round(((float) round2) / 7.0f)));
        }
        if (round2 < 46) {
            return context.getString(R.string.mon_date_mesicem);
        }
        return round2 < 351 ? context.getString(R.string.mon_date_mesici, Integer.valueOf(Math.round(((float) round2) / 30.5f))) : round2 < 548 ? context.getString(R.string.mon_date_rokem) : context.getString(R.string.mon_date_roky, Integer.valueOf(Math.round(((float) round2) / 365.0f)));
    }

    public static String getBlockCountsList(int i) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < S.BLOCK_COUNTS[0].length; i2++) {
            if (i2 > 0) {
                sb.append("|");
            }
            sb.append(S.BLOCK_COUNTS[0][i2]);
        }
        sb.append("x");
        for (int i3 = 0; i3 < S.BLOCK_COUNTS[i].length; i3++) {
            if (i3 > 0) {
                sb.append("|");
            }
            sb.append(S.BLOCK_COUNTS[i][i3]);
        }
        return sb.toString();
    }

    public static String getBytesFormated(long j) {
        String format;
        int i = 0;
        String[] strArr = {"B", "kB", "MB", "GB", "TB"};
        double d2 = j;
        while (d2 >= 1000.0d) {
            d2 /= 1024.0d;
            i++;
        }
        if (d2 < 100.0d) {
            format = new DecimalFormat("#0.#").format(d2);
        } else {
            format = new DecimalFormat("#").format(d2);
        }
        return format + " " + strArr[i];
    }

    public static JSONObject getDifferenceBetweenMaps(Map<String, Object> map, Map<String, Object> map2) {
        JSONObject jSONObject = new JSONObject();
        for (String str : map.keySet()) {
            if (!map2.containsKey(str)) {
                g.a.b.b("Comparing two maps and newMap do not contain key %s, skipping", str);
            } else {
                Object obj = map.get(str);
                Object obj2 = map2.get(str);
                if (obj != null || obj2 != null) {
                    if (obj == null) {
                        jsonPut(jSONObject, str, obj2);
                    } else if (!obj.equals(obj2)) {
                        jsonPut(jSONObject, str, obj2);
                    }
                }
            }
        }
        return jSONObject;
    }

    public static com.google.firebase.firestore.p getFirestoreInstance() {
        com.google.firebase.firestore.p f2 = com.google.firebase.firestore.p.f();
        if (!f2.e().a()) {
            r.a aVar = new r.a();
            aVar.a(true);
            f2.a(aVar.a());
        }
        return f2;
    }

    public static String getFullPictureSize(String str) {
        String[] split = str.split("x");
        if (split.length != 2) {
            return null;
        }
        return getFullPictureSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public static int getGeoDistance(double d2, double d3, double d4, double d5) {
        double cos = (d5 - d3) * Math.cos((d2 + d4) / 2.0d);
        double d6 = d4 - d2;
        double sqrt = Math.sqrt((cos * cos) + (d6 * d6));
        double d7 = 6371;
        Double.isNaN(d7);
        return (int) Math.round(sqrt * d7);
    }

    public static int getImageRatio(String str) throws SCException {
        try {
            if (str != null) {
                String[] split = str.split("x");
                if (split.length == 2) {
                    return getImageRatio(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                }
                throw new SCException("wrong picture size format " + str);
            }
            throw new SCException("picture size is null");
        } catch (SCException e2) {
            e = e2;
            throw new SCException("Error getting ratio: " + e.getMessage());
        } catch (NumberFormatException e3) {
            e = e3;
            throw new SCException("Error getting ratio: " + e.getMessage());
        }
    }

    public static int getImmersiveFlags() {
        return Build.VERSION.SDK_INT >= 19 ? 3846 : 1799;
    }

    public static String getLocalTempString(Context context, int i) {
        boolean isFarenheit = isFarenheit();
        int i2 = isFarenheit ? R.string.temperature_farenheit : R.string.temperature_celsius;
        if (isFarenheit) {
            i = celsiusToFarenheit(i);
        }
        return context.getString(i2, Integer.valueOf(i));
    }

    public static String getLocalizedEDM(Date date) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(1);
        try {
            simpleDateFormat.applyPattern(removeYearFromPattern(simpleDateFormat.toPattern()));
        } catch (SCException | IllegalArgumentException e2) {
            g.a.b.a(e2, "Error removing year for %s", Locale.getDefault());
        }
        return simpleDateFormat.format(Long.valueOf(date.getTime()));
    }

    public static File getMonitorHotImageS(Context context, String str) {
        File externalCacheDir = context.getExternalCacheDir();
        String str2 = (externalCacheDir != null ? externalCacheDir.getPath() : context.getCacheDir().getPath()) + File.separator + str;
        createPath(str2);
        return new File(str2 + File.separator + m.IMAGE_HOT_S_NAME);
    }

    public static File getMonitorImageFileL(Context context, String str) {
        StringBuilder sb = new StringBuilder(getMonitorImageLFolder(context));
        createPath(sb.toString());
        sb.append(File.separator);
        sb.append(getAlarmFileNameL(str));
        return new File(sb.toString());
    }

    private static String getMonitorImageLFolder(Context context) {
        return getPublicPicturesAppFolder(context);
    }

    public static File getMonitorTempAlarmFile(Context context, String str) {
        return new File(context.getCacheDir().getPath() + File.separatorChar + str);
    }

    public static String getNewVideoFile(Context context, String str) {
        String str2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath() + File.separator + context.getResources().getString(R.string.app_name) + File.separator + "video_" + str + ".mp4";
        new File(str2).getParentFile().mkdirs();
        return str2;
    }

    public static long getNextSchedulerChangeTimeMs(int[][] iArr, int i) {
        return getNextSchedulerChangeTimeMs(iArr, Integer.MAX_VALUE, i);
    }

    public static long getOneDayMilis() {
        return 86400000L;
    }

    public static long getOneYearMilis() {
        return 31622400000L;
    }

    public static String[] getPictureSizesText(String[] strArr) {
        String[] strArr2 = new String[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            strArr2[i] = getFullPictureSize(strArr[i]);
        }
        return strArr2;
    }

    private static String getPublicPicturesAppFolder(Context context) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator + context.getResources().getString(R.string.app_name);
    }

    public static String getRandomString(int i) {
        char[] charArray = alphanumStr.toCharArray();
        SecureRandom secureRandom = new SecureRandom();
        char[] cArr = new char[i];
        for (int i2 = 0; i2 < cArr.length; i2++) {
            cArr[i2] = charArray[secureRandom.nextInt(charArray.length)];
        }
        return new String(cArr);
    }

    private static String getResolutionStr(int i, int i2) {
        double d2 = i * i2;
        Double.isNaN(d2);
        return new DecimalFormat("#0.#").format(d2 / 1000000.0d) + " MPx";
    }

    public static String getSchedulerCurrentStateSumm(Context context, String str, int i) {
        int[][] schedulerDataFromStrToArr = schedulerDataFromStrToArr(str);
        boolean isCurrentlyScheduled = isCurrentlyScheduled(schedulerDataFromStrToArr, i);
        long nextSchedulerChangeTimeMs = getNextSchedulerChangeTimeMs(schedulerDataFromStrToArr, i, 0);
        String string = context.getString(R.string.pref_cam_sched_set_data_summ_e);
        boolean z = i == Integer.MAX_VALUE || i == getUtcOffset();
        if (nextSchedulerChangeTimeMs > -1) {
            string = ((SimpleDateFormat) DateFormat.getTimeInstance(3)).format(Long.valueOf(nextSchedulerChangeTimeMs));
            if (!DateUtils.isToday(nextSchedulerChangeTimeMs)) {
                DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(nextSchedulerChangeTimeMs);
                int i2 = calendar.get(7);
                if (z) {
                    string = dateFormatSymbols.getWeekdays()[i2] + " " + string;
                } else {
                    string = dateFormatSymbols.getShortWeekdays()[i2] + " " + string + " " + context.getString(R.string.pref_cam_sched_set_local_time);
                }
            }
        }
        return context.getString(isCurrentlyScheduled ? R.string.pref_cam_sched_set_data_summ_1 : R.string.pref_cam_sched_set_data_summ_0, string);
    }

    public static String getTimestamp(File file) {
        if (file == null) {
            return null;
        }
        Matcher matcher = Pattern.compile(m.TIMESTAMP_REGEX).matcher(file.getName());
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static int getTransparentFlags() {
        return 1792;
    }

    public static String getUniqueTimeStamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    }

    public static int getUtcOffset() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis());
    }

    public static String getZoomText(String str, int i) {
        String[] split = str.split("\\|");
        int[] iArr = new int[split.length];
        for (int i2 = 0; i2 < split.length; i2++) {
            iArr[i2] = Integer.parseInt(split[i2]);
        }
        float f2 = iArr[i] / 100.0f;
        return new DecimalFormat("#0.00").format(f2) + "x";
    }

    public static int interpolateBlocksCount(int i, int i2, int i3) {
        int[][] iArr = S.BLOCK_COUNTS;
        int[] iArr2 = iArr[i];
        int[] iArr3 = iArr[i2];
        int binarySearch = Arrays.binarySearch(iArr2, i3);
        if (binarySearch < 0) {
            binarySearch = (-binarySearch) - 1;
            if (binarySearch == iArr2.length) {
                binarySearch--;
            }
            g.a.b.a("Interpolation for new block count did not find " + i3 + ", using instead " + iArr2[binarySearch], new Object[0]);
        }
        return iArr3[Math.round(((binarySearch + 1) * iArr3.length) / iArr2.length) - 1];
    }

    public static String interpolateMask(int i, int i2, int i3, int i4, String str) {
        int i5;
        int i6 = i;
        int i7 = i3;
        int i8 = i4;
        String str2 = str;
        if (str.length() != i6 * i2) {
            g.a.b.b("Mask length " + str.length() + " is different from blocks count " + i6 + "x" + i2, new Object[0]);
            char[] cArr = new char[i7 * i8];
            Arrays.fill(cArr, '0');
            return new String(cArr);
        }
        if (i6 == i7 && i2 == i8) {
            g.a.b.a("Interpolating not needed, same block counts", new Object[0]);
            return str2;
        }
        StringBuilder sb = new StringBuilder();
        float f2 = 100.0f / i7;
        float f3 = 100.0f / i8;
        float f4 = 100.0f / i6;
        float f5 = 100.0f / i2;
        int i9 = 0;
        while (i9 < i8) {
            int i10 = 0;
            while (i10 < i7) {
                float f6 = i10 * f2;
                float f7 = (f6 + f2) - 1.0f;
                float f8 = i9 * f3;
                float f9 = (f8 + f3) - 1.0f;
                float f10 = f2;
                Rect rect = new Rect(Math.round(f6), Math.round(f8), Math.round(f7), Math.round(f9));
                int width = rect.width() * rect.height();
                int i11 = (int) (f6 / f4);
                int i12 = (int) (f7 / f4);
                int i13 = (int) (f8 / f5);
                int i14 = (int) (f9 / f5);
                int i15 = i9;
                float f11 = f3;
                int i16 = 0;
                while (true) {
                    char c2 = '1';
                    if (i13 > i14) {
                        break;
                    }
                    int i17 = i16;
                    int i18 = i11;
                    while (i18 <= i12) {
                        if (str2.charAt((i13 * i6) + i18) != c2) {
                            i5 = i14;
                        } else {
                            float f12 = i18 * f4;
                            i5 = i14;
                            float f13 = i13 * f5;
                            if (rect.intersect(Math.round(f12), Math.round(f13), Math.round((f12 + f4) - 1.0f), Math.round((f13 + f5) - 1.0f))) {
                                i17 += rect.width() * rect.height();
                                rect.set(Math.round(f6), Math.round(f8), Math.round(f7), Math.round(f9));
                            }
                        }
                        i18++;
                        i6 = i;
                        i14 = i5;
                        str2 = str;
                        c2 = '1';
                    }
                    i13++;
                    i6 = i;
                    str2 = str;
                    i16 = i17;
                }
                sb.append(((float) i16) > ((float) width) * 0.45f ? '1' : '0');
                i10++;
                i6 = i;
                i7 = i3;
                str2 = str;
                f2 = f10;
                f3 = f11;
                i9 = i15;
            }
            i9++;
            i6 = i;
            i7 = i3;
            i8 = i4;
            str2 = str;
        }
        g.a.b.a("Interpolated mask: %s", sb.toString());
        return sb.toString();
    }

    public static boolean isBetween(int i, int i2, int i3) {
        return i >= i2 && i < i3;
    }

    public static boolean isCurrentlyScheduled(int[][] iArr) {
        return isCurrentlyScheduled(iArr, Integer.MAX_VALUE);
    }

    public static boolean isFarenheit() {
        String locale = Locale.getDefault().toString();
        return locale.equalsIgnoreCase("en_US") || locale.equalsIgnoreCase("en_BS") || locale.equalsIgnoreCase("en_BZ") || locale.equalsIgnoreCase("en_KY") || locale.equalsIgnoreCase("en_PW") || locale.equalsIgnoreCase("en_PR") || locale.equalsIgnoreCase("en_GU") || locale.equalsIgnoreCase("en_VI");
    }

    public static void jsonPut(JSONObject jSONObject, String str, Object obj) {
        try {
            jSONObject.put(str, obj);
        } catch (JSONException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String listIntToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static int makeDivideBy4(int i) {
        int i2 = i % 4;
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i : i + 1 : i + 2 : i - 1;
    }

    public static String mapToString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!z) {
                sb.append(", ");
            }
            z = false;
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(String.valueOf(entry.getValue()));
        }
        return sb.toString();
    }

    public static String motionSensitivityToSummary(Context context, int i) {
        return motionSensitivyToText(context, i) + " (" + i + ")";
    }

    public static String motionSensitivyToText(Context context, int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
                return context.getString(R.string.pref_cam_sensitivity_text, context.getString(R.string.pref_cam_sensitivity_1));
            case 4:
            case 5:
            case 6:
                return context.getString(R.string.pref_cam_sensitivity_text, context.getString(R.string.pref_cam_sensitivity_2));
            case 7:
            case 8:
            case 9:
            case 10:
                return context.getString(R.string.pref_cam_sensitivity_text, context.getString(R.string.pref_cam_sensitivity_3));
            case 11:
            case 12:
            case 13:
                return context.getString(R.string.pref_cam_sensitivity_text, context.getString(R.string.pref_cam_sensitivity_4));
            case 14:
            case 15:
            case 16:
                return context.getString(R.string.pref_cam_sensitivity_text, context.getString(R.string.pref_cam_sensitivity_5));
            default:
                return null;
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private static String removeYearFromPattern(String str) throws SCException {
        int i;
        while (i < str.length() && str.charAt(i) != 'y' && str.charAt(i) != 'Y') {
            i = str.charAt(i) != '\'' ? i + 1 : 0;
            do {
                i++;
                if (i < str.length()) {
                }
            } while (str.charAt(i) != '\'');
        }
        if (i < str.length()) {
            String str2 = "EMd";
            int i2 = i;
            while (i2 < str.length() && "EMd".indexOf(str.charAt(i2)) == -1) {
                i2++;
                if (i2 < str.length() && str.charAt(i2) == '\'') {
                    do {
                        i2++;
                        if (i2 < str.length()) {
                        }
                    } while (str.charAt(i2) != '\'');
                }
            }
            if (i2 != str.length()) {
                str2 = "EMd,";
            }
            while (i >= 0 && str2.indexOf(str.charAt(i)) == -1) {
                i--;
                if (i >= 0 && str.charAt(i) == '\'') {
                    do {
                        i--;
                        if (i >= 0) {
                        }
                    } while (str.charAt(i) != '\'');
                }
            }
            return str.replace(str.substring(i + 1, i2), " ").trim();
        }
        throw new SCException("Did not find year in pattern");
    }

    public static String schedulerDataFromArrToStr(int[][] iArr) {
        try {
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < iArr.length; i++) {
                JSONArray jSONArray2 = new JSONArray();
                for (int i2 = 0; i2 < iArr[i].length; i2++) {
                    jSONArray2.put(iArr[i][i2]);
                }
                jSONArray.put(jSONArray2);
            }
            return jSONArray.toString();
        } catch (Throwable th) {
            g.a.b.a(th, "Error while encoding scheduler data to JSONArray", new Object[0]);
            return null;
        }
    }

    public static int[][] schedulerDataFromStrToArr(String str) {
        JSONArray jSONArray;
        int[][] iArr = new int[7];
        if (str == null) {
            return iArr;
        }
        try {
            jSONArray = new JSONArray(str);
        } catch (SCException | JSONException e2) {
            g.a.b.a(e2, "Error in translating scheduler data", new Object[0]);
        }
        if (jSONArray.length() == 7) {
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                int[] iArr2 = new int[jSONArray2.length()];
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    iArr2[i2] = jSONArray2.getInt(i2);
                }
                iArr[i] = iArr2;
            }
            return iArr;
        }
        throw new SCException("Scheduler data array length " + jSONArray.length() + " must be 7");
    }

    public static void sendFeedback(Context context) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:" + m.emailsForFeedback[0]));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else if (context instanceof androidx.appcompat.app.o) {
            v.newInstance(0, null, context.getString(R.string.navig_no_email_app, m.emailsForFeedback[0])).show(((androidx.appcompat.app.o) context).getSupportFragmentManager(), "MON_NO_EMAIL_APP");
        }
    }

    public static float spToPx(Context context, int i) {
        return TypedValue.applyDimension(2, i, context.getResources().getDisplayMetrics());
    }

    public static File switchLogcatToFile(Context context) {
        File file = new File(context.getCacheDir().getPath(), "logcat_" + getUniqueTimeStamp() + ".txt");
        g.a.b.a("Switching logcat to file %s", file);
        try {
            Runtime.getRuntime().exec("logcat -b all -c");
            Runtime.getRuntime().exec("logcat -f " + file);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return file;
    }

    public static void switchLogcatToNormal() {
        g.a.b.a("Switching logcat to normal", new Object[0]);
        try {
            Runtime.getRuntime().exec("logcat -b all -c");
            Runtime.getRuntime().exec("logcat");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static Date timeStampToDate(String str) throws ParseException {
        if (str.length() == 8) {
            return new SimpleDateFormat("yyyyMMdd", Locale.US).parse(str);
        }
        if (str.length() == 15) {
            return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).parse(str);
        }
        throw new ParseException("Wrong length of timeStamp " + str.length(), 0);
    }

    public static String formatTimeShortHm(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1974, 4, 14, i, i2, 0);
        return ((SimpleDateFormat) DateFormat.getTimeInstance(3)).format(calendar.getTime());
    }

    public static long getNextSchedulerChangeTimeMs(int[][] iArr, int i, int i2) {
        int i3;
        if (iArr == null) {
            return -1L;
        }
        Calendar calendar = Calendar.getInstance();
        if (i != Integer.MAX_VALUE) {
            calendar.add(14, i - getUtcOffset());
        }
        int i4 = 7;
        int i5 = calendar.get(7);
        int i6 = (calendar.get(11) * 100) + calendar.get(12);
        int i7 = 0;
        boolean z = false;
        while (i7 < 8) {
            int i8 = i5 + i7;
            int i9 = i8 - 1;
            int[] iArr2 = iArr[calendarDayNoToArrayNo((i9 % 7) + 1)];
            int[] iArr3 = iArr[calendarDayNoToArrayNo(((i9 - 1) % i4) + 1)];
            int[] iArr4 = iArr[calendarDayNoToArrayNo((((i8 + 1) - 1) % i4) + 1)];
            for (int i10 = 0; i10 < iArr2.length; i10++) {
                if ((i7 != 0 || iArr2[i10] > i6) && ((i2 >= 0 || i10 % 2 != 0) && ((i2 <= 0 || i10 % 2 != 1) && !((iArr2[i10] == 2400 && iArr4.length > 0 && iArr4[0] == 0) || (iArr2[i10] == 0 && iArr3.length > 0 && iArr3[iArr3.length - 1] == 2400))))) {
                    calendar.set(11, iArr2[i10] / 100);
                    calendar.set(12, iArr2[i10] % 100);
                    calendar.set(13, 0);
                    i3 = 14;
                    calendar.set(14, 0);
                    z = true;
                    break;
                }
            }
            i3 = 14;
            if (z) {
                break;
            }
            if (i7 == 0) {
                calendar.add(5, 1);
                calendar.set(11, 0);
                calendar.set(12, 0);
                calendar.set(13, 0);
                calendar.set(i3, 0);
            } else {
                calendar.add(5, 1);
            }
            i7++;
            i4 = 7;
        }
        if (z) {
            return calendar.getTimeInMillis();
        }
        g.a.b.a("Not found any next scheduler up", new Object[0]);
        return -1L;
    }

    public static boolean isCurrentlyScheduled(int[][] iArr, int i) {
        Calendar calendar = Calendar.getInstance();
        if (i != Integer.MAX_VALUE) {
            calendar.add(14, i - getUtcOffset());
        }
        int i2 = calendar.get(7);
        int[] iArr2 = iArr[calendarDayNoToArrayNo(i2)];
        if (iArr2.length % 2 != 0) {
            g.a.b.b("Scheduler data has wrong length", new Object[0]);
            return false;
        }
        int i3 = (calendar.get(11) * 100) + calendar.get(12);
        for (int i4 = 0; i4 < iArr2.length / 2; i4++) {
            int i5 = i4 * 2;
            int i6 = iArr2[i5];
            int i7 = iArr2[i5 + 1];
            if (i3 >= i6 && i3 < i7) {
                g.a.b.a("Inside scheduler times for day %d", Integer.valueOf(i2));
                return true;
            }
            if (i3 < i7) {
                break;
            }
        }
        g.a.b.a("Outside scheduler times for day %d", Integer.valueOf(i2));
        return false;
    }

    private static String getFullPictureSize(int i, int i2) {
        return getResolutionStr(i, i2) + " (" + getAspectRatioStr(i, i2) + ")";
    }

    public static int getImageRatio(int i, int i2) {
        double d2 = i / i2;
        int i3 = 0;
        double d3 = S.IMAGE_RATIOS[0];
        Double.isNaN(d2);
        double abs = Math.abs(d3 - d2);
        int i4 = 1;
        while (true) {
            double[] dArr = S.IMAGE_RATIOS;
            if (i4 >= dArr.length) {
                return i3;
            }
            double d4 = dArr[i4];
            Double.isNaN(d2);
            if (Math.abs(d4 - d2) < abs) {
                double d5 = S.IMAGE_RATIOS[i4];
                Double.isNaN(d2);
                abs = Math.abs(d5 - d2);
                i3 = i4;
            }
            i4++;
        }
    }
}
