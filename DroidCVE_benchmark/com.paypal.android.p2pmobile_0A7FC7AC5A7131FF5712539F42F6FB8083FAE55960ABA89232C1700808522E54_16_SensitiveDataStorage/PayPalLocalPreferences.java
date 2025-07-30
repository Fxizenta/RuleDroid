package com.paypal.android.p2pmobile.paypallocal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PayPalLocalPreferences {
    public static final String LOG_TAG = "PayPalLocalPreferences";
    public static final String PAYPAL_LOCAL_PREFS = "PAYPAL_LOCAL";
    public static final String PREF_PAYPAL_LOCAL_DISTANCE_OPTION = "com.paypal.DISTANCE_OPTION";
    public static final String PREF_PAYPAL_LOCAL_FAVORITES = "com.paypal.FAVORITES";
    public static final String PREF_PAYPAL_LOCAL_PAYMENT_OPTION = "com.paypal.PAYMENT_OPTION";

    public static boolean isFavorite(Context context, String merchantId) {
        boolean found = false;
        try {
            SharedPreferences prefs = context.getSharedPreferences(PAYPAL_LOCAL_PREFS, 0);
            String data = prefs.getString(PREF_PAYPAL_LOCAL_FAVORITES, "");
            BufferedReader reader = new BufferedReader(new StringReader(data));
            while (true) {
                String id = reader.readLine();
                if (id == null) {
                    break;
                }
                if (id.compareTo(merchantId) == 0) {
                    found = true;
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
        }
        return found;
    }

    public static void addFavorite(Context context, String merchantId) {
        if (isFavorite(context, merchantId)) {
            Log.d(LOG_TAG, "merchant " + merchantId + " is already a favorite");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PAYPAL_LOCAL_PREFS, 0);
        String data = prefs.getString(PREF_PAYPAL_LOCAL_FAVORITES, "");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_PAYPAL_LOCAL_FAVORITES, String.valueOf(data) + merchantId + "\n");
        editor.commit();
        Log.d(LOG_TAG, "added favorite merchant " + merchantId);
    }

    public static void removeFavorite(Context context, String merchantId) {
        SharedPreferences prefs = context.getSharedPreferences(PAYPAL_LOCAL_PREFS, 0);
        String data = prefs.getString(PREF_PAYPAL_LOCAL_FAVORITES, "");
        boolean found = false;
        String newData = "";
        try {
            BufferedReader reader = new BufferedReader(new StringReader(data));
            while (true) {
                String id = reader.readLine();
                if (id == null) {
                    break;
                } else if (id.compareTo(merchantId) == 0) {
                    found = true;
                } else {
                    newData = String.valueOf(newData) + id + "\n";
                }
            }
            reader.close();
        } catch (Exception e) {
        }
        if (found) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_PAYPAL_LOCAL_FAVORITES, newData);
            editor.commit();
            Log.d(LOG_TAG, "removed favorite merchant " + merchantId);
            return;
        }
        Log.d(LOG_TAG, "attempt to remove non-existent merchant " + merchantId);
    }

    public static ArrayList<String> getFavorites(Context context) {
        ArrayList<String> retval = new ArrayList<>();
        try {
            SharedPreferences prefs = context.getSharedPreferences(PAYPAL_LOCAL_PREFS, 0);
            String data = prefs.getString(PREF_PAYPAL_LOCAL_FAVORITES, "");
            BufferedReader reader = new BufferedReader(new StringReader(data));
            while (true) {
                String id = reader.readLine();
                if (id == null) {
                    break;
                }
                retval.add(id);
            }
            reader.close();
        } catch (Exception e) {
        }
        return retval;
    }
}
