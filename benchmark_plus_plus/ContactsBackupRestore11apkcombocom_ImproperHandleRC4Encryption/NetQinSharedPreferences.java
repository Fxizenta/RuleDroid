package com.netqin.antivirus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Calendar;
import android.view.WindowManager;
import com.netqin.contact.ContactCommon;

/* loaded from: classes.dex */
public class NetQinSharedPreferences<T> {
    private SharedPreferences mSharedPreference;

    public NetQinSharedPreferences(Context context, String sharedPreferenceName) {
        this.mSharedPreference = context.getSharedPreferences(sharedPreferenceName, 0);
    }

    public void Clear() {
        this.mSharedPreference.edit().clear().commit();
    }

    public Boolean getBoolean(T name, Boolean defaultValue) {
        return Boolean.valueOf(this.mSharedPreference.getBoolean(name.toString(), defaultValue.booleanValue()));
    }

    public Boolean getBoolean(T name) {
        return getBoolean(name, true);
    }

    public float getFloat(T name, float defaultValue) {
        return this.mSharedPreference.getFloat(name.toString(), defaultValue);
    }

    public float getFloat(T name) {
        return getFloat(name, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF);
    }

    public int getInt(T name, int defaultValue) {
        return this.mSharedPreference.getInt(name.toString(), defaultValue);
    }

    public int getInt(T name) {
        return getInt(name, 0);
    }

    public boolean getLoginState() {
        return this.mSharedPreference.getBoolean(ContactCommon.LoginState, false);
    }

    public void setLoginState(boolean state) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putBoolean(ContactCommon.LoginState, state);
        editor.commit();
    }

    public long getLong(T name, long defaultValue) {
        return this.mSharedPreference.getLong(name.toString(), defaultValue);
    }

    public long getLong(T name) {
        return getLong(name, 0L);
    }

    public String getString(T name, String defaultValue) {
        return this.mSharedPreference.getString(name.toString(), defaultValue);
    }

    public String getString(T name) {
        return getString(name, Calendar.Events.DEFAULT_SORT_ORDER);
    }

    public String getEncryptString(T name, String defaultValue) {
        String tempString = getString(name, DataUtils.encryptForXml(DataUtils.ENCRYPT_KEY, defaultValue));
        return DataUtils.decryptForXml(DataUtils.ENCRYPT_KEY, tempString);
    }

    public String getEncryptString(T name) {
        return getEncryptString(name, Calendar.Events.DEFAULT_SORT_ORDER);
    }

    public void putBoolean(T name, Boolean value) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putBoolean(name.toString(), value.booleanValue());
        editor.commit();
    }

    public void putFloat(T name, Float value) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putFloat(name.toString(), value.floatValue());
        editor.commit();
    }

    public void putInt(T name, int value) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putInt(name.toString(), value);
        editor.commit();
    }

    public void putLong(T name, long value) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putLong(name.toString(), value);
        editor.commit();
    }

    public void putString(T name, String value) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putString(name.toString(), value);
        editor.commit();
    }

    public void putStringWithEncrypt(T name, String value) {
        SharedPreferences.Editor editor = this.mSharedPreference.edit();
        editor.putString(name.toString(), DataUtils.encryptForXml(DataUtils.ENCRYPT_KEY, value));
        editor.commit();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.mSharedPreference.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.mSharedPreference.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean contains(T name) {
        return this.mSharedPreference.contains(name.toString());
    }
}
