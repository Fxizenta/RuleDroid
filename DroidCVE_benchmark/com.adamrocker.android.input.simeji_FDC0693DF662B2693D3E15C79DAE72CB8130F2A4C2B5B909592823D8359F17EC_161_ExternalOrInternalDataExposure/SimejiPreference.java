package com.adamrocker.android.input.simeji.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SimejiPreference {
    private static final String NONE = "NONE";
    private static final String SEPARATOR = " ";

    public static void saveAllPreferences(Context context, String saveFile) throws IOException {
        PrintWriter pw = ExternalStrageUtil.getWriterInSimeji(saveFile);
        print(context, pw);
        pw.close();
    }

    public static final HashMap<String, String> getAllPreferences(Context context) {
        String value;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> map = pref.getAll();
        HashMap<String, String> res = new HashMap<>();
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            if (key != null && obj != null) {
                if (obj instanceof String) {
                    value = (String) obj;
                } else {
                    value = String.valueOf(obj);
                }
                if (key.length() > 0 && value.length() > 0) {
                    res.put(key, value);
                }
            }
        }
        return res;
    }

    private static void print(Context context, PrintWriter pw) {
        String value;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> map = pref.getAll();
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            if (key != null && obj != null) {
                if (obj instanceof String) {
                    value = (String) obj;
                } else {
                    value = String.valueOf(obj);
                }
                if (key.length() > 0 && value.length() > 0) {
                    pw.println(String.valueOf(key) + SEPARATOR + value);
                }
            }
        }
    }

    public static String getSettings(Context context) {
        String value;
        StringBuilder sb = new StringBuilder();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> map = pref.getAll();
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            if (key != null && obj != null) {
                if (obj instanceof String) {
                    value = (String) obj;
                } else {
                    value = String.valueOf(obj);
                }
                if (key.length() > 0 && value.length() > 0) {
                    sb.append(key).append("=").append(value).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public static void loadAllPreferences(Context context, String loadFile) throws IOException {
        File file = Environment.getExternalStorageDirectory();
        String sdcard = file.getPath();
        File file2 = new File(String.valueOf(sdcard) + loadFile);
        FileReader in = new FileReader(file2);
        BufferedReader br = new BufferedReader(in);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        while (true) {
            String line = br.readLine();
            if (line != null) {
                String[] values = line.split(SEPARATOR);
                if (values.length > 0) {
                    String key = values[0];
                    String value = values[1];
                    if (key.length() > 0 && value.length() > 0) {
                        if (value.equals("true")) {
                            edit.putBoolean(key, true);
                        } else if (value.equals("false")) {
                            edit.putBoolean(key, false);
                        } else {
                            try {
                                int val = Integer.parseInt(value);
                                edit.putInt(key, val);
                            } catch (NumberFormatException e) {
                                edit.putString(key, value);
                            }
                        }
                    }
                }
            } else {
                br.close();
                in.close();
                edit.commit();
                return;
            }
        }
    }

    public static int size(Context context) {
        Map<String, ?> map = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        return map.size();
    }
}
