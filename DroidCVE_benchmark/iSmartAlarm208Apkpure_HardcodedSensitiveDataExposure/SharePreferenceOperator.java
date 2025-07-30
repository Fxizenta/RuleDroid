package andon.isa.database;

import andon.common.C;
import andon.common.Log;
import android.content.Context;
import android.content.SharedPreferences;
import iSA.common.svCode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public class SharePreferenceOperator {
    private static final String TAG = "SharePreferenceOperator";
    private static String sharedPrefrenceFileName = "iSmartAlermData";
    private static String showdemo = "showdemo";

    private static String addShowDemo(String key) {
        if (C.isIsshowdemo()) {
            return String.valueOf(showdemo) + key;
        }
        return key;
    }

    public static String getStringValue(Context context, String key) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return null;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            String valueStr = share.getString(key2, svCode.asyncSetHome);
            return valueStr;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error:" + e.getMessage());
            return svCode.asyncSetHome;
        }
    }

    public static String getStringValue(Context context, String key, String defaultValue) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return null;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            String valueStr = share.getString(key2, defaultValue);
            return valueStr;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return svCode.asyncSetHome;
        }
    }

    public static boolean setStringValue(Context context, String key, String value) {
        if (key == null || key.equals(svCode.asyncSetHome) || value == null) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            Log.i(TAG, "setStringValue key=" + key2 + "\n value=" + value + "\n context=" + context.toString());
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            SharedPreferences.Editor editor = share.edit();
            editor.putString(key2, value.trim());
            editor.commit();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setStringValue error: " + e.getMessage());
            return false;
        }
    }

    public static boolean getBooleanValue(Context context, String key) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            boolean value = share.getBoolean(key2, false);
            return value;
        } catch (Exception e) {
            Log.e(TAG, "getBooleanValue error: " + e.getMessage());
            return false;
        }
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            boolean value = share.getBoolean(key2, defaultValue);
            return value;
        } catch (Exception e) {
            Log.e(TAG, "getBooleanValue error :" + e.getMessage());
            return false;
        }
    }

    public static int getIntValue(Context context, String key) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return -1;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            int value = share.getInt(key2, -1);
            return value;
        } catch (Exception e) {
            Log.e(TAG, "getIntValue error:" + e.getMessage());
            return -1;
        }
    }

    public static int getIntValue(Context context, String key, int defaultValue) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return -1;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            int value = share.getInt(key2, defaultValue);
            return value;
        } catch (Exception e) {
            Log.e(TAG, "getIntValue error:" + e.getMessage());
            return -1;
        }
    }

    public static boolean setBooleanValue(Context context, String key, boolean value) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            SharedPreferences.Editor editor = share.edit();
            editor.putBoolean(key2, value);
            editor.commit();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return false;
        }
    }

    public static boolean setIntValue(Context context, String key, int value) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            SharedPreferences.Editor editor = share.edit();
            editor.putInt(key2, value);
            editor.commit();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return false;
        }
    }

    public static boolean setLongValue(Context context, String key, long value) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            SharedPreferences.Editor editor = share.edit();
            editor.putLong(key2, value);
            editor.commit();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return false;
        }
    }

    public static Long getLongValue(Context context, String key) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return 0L;
        }
        String key2 = addShowDemo(key);
        long j = 0L;
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            Long result = Long.valueOf(share.getLong(key2, 0L));
            return result;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return j;
        }
    }

    public static boolean setFloatValue(Context context, String key, float value) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return false;
        }
        String key2 = addShowDemo(key);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            SharedPreferences.Editor editor = share.edit();
            editor.putFloat(key2, value);
            editor.commit();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return false;
        }
    }

    public static Float getFloatValue(Context context, String key) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return Float.valueOf(0.0f);
        }
        String key2 = addShowDemo(key);
        Float valueOf = Float.valueOf(0.0f);
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            Float result = Float.valueOf(share.getFloat(key2, 0.0f));
            return result;
        } catch (Exception e) {
            Log.e(TAG, "getStringValue error: " + e.getMessage());
            return valueOf;
        }
    }

    public static boolean setIsc3Uid(Context context, String isc3Id, String uid) {
        if (isc3Id == null || isc3Id.equals(svCode.asyncSetHome) || uid == null || uid.equals(svCode.asyncSetHome)) {
            return false;
        }
        boolean result = setStringValue(context, addShowDemo(isc3Id), uid);
        return result;
    }

    public static String getIsc3Uid(Context context, String isc3Id) {
        if (isc3Id == null || isc3Id.equals(svCode.asyncSetHome)) {
            return null;
        }
        String uid = getStringValue(context, addShowDemo(isc3Id));
        return uid;
    }

    public static boolean setStringArray(Context context, String key, String[] stringArray) {
        if (key == null || key.equals(svCode.asyncSetHome) || stringArray == null || stringArray.length == 0) {
            return false;
        }
        String key2 = addShowDemo(key);
        boolean result = false;
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            Set<String> stringSet = new HashSet<>();
            for (String str : stringArray) {
                stringSet.add(str);
            }
            SharedPreferences.Editor editor = share.edit();
            editor.putStringSet(key2, stringSet);
            editor.commit();
            result = true;
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setStringArray error: " + e.getMessage());
            return result;
        }
    }

    public static String[] getStringArray(Context context, String key, String[] defaultValue) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return null;
        }
        String key2 = addShowDemo(key);
        Set<String> stringSetdefault = new HashSet<>();
        new HashSet();
        for (String str : defaultValue) {
            stringSetdefault.add(str);
        }
        String[] valueStrArray = null;
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            Set<String> stringSet = share.getStringSet(key2, stringSetdefault);
            int i = 0;
            valueStrArray = new String[stringSet.size()];
            Iterator<String> it = stringSet.iterator();
            while (true) {
                int i2 = i;
                if (it.hasNext()) {
                    String str2 = it.next();
                    i = i2 + 1;
                    valueStrArray[i2] = str2;
                } else {
                    return valueStrArray;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getStringArray error: " + e.getMessage());
            return valueStrArray;
        }
    }

    public static boolean setIntArray(Context context, String key, int[] intArray) {
        if (key == null || key.equals(svCode.asyncSetHome) || intArray == null || intArray.length == 0) {
            return false;
        }
        String key2 = addShowDemo(key);
        boolean result = false;
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            Set<String> stringSet = new HashSet<>();
            for (int a : intArray) {
                stringSet.add(String.valueOf(a));
            }
            SharedPreferences.Editor editor = share.edit();
            editor.putStringSet(key2, stringSet);
            editor.commit();
            result = true;
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setIntArray error: " + e.getMessage());
            return result;
        }
    }

    public static int[] getIntArray(Context context, String key, int[] defaultValue) {
        if (key == null || key.equals(svCode.asyncSetHome)) {
            return null;
        }
        String key2 = addShowDemo(key);
        Set<String> stringSetdefault = new HashSet<>();
        new HashSet();
        for (int a : defaultValue) {
            stringSetdefault.add(String.valueOf(a));
        }
        int[] array = null;
        int i = 0;
        try {
            SharedPreferences share = context.getSharedPreferences(sharedPrefrenceFileName, 32768);
            Set<String> stringSet = share.getStringSet(key2, stringSetdefault);
            array = new int[stringSet.size()];
            Log.e(TAG, "getIntArray stringSet.size(): " + stringSet.size());
            for (String str : stringSet) {
                array[i] = Integer.parseInt(str);
                i++;
            }
            return array;
        } catch (Exception e) {
            Log.e(TAG, "getIntArray error: " + e.getMessage());
            return array;
        }
    }
}
