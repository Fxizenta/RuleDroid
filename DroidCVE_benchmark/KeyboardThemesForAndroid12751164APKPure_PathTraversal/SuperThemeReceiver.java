package com.timmystudios.redrawkeyboard.themes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import com.facebook.appevents.internal.ViewHierarchyConstants;
import com.timmystudios.redrawkeyboard.app.main.store.main.StoreItemInfo;
import com.timmystudios.redrawkeyboard.app.main.store.main.StoreType;
import com.timmystudios.redrawkeyboard.fonts.FontManager;
import com.timmystudios.redrawkeyboard.sounds.SoundManager;
import com.timmystudios.redrawkeyboard.themes.go.GoApkThemeInstaller;

/* loaded from: classes3.dex */
public class SuperThemeReceiver extends BroadcastReceiver {
    public static final String APPLY_PREF = "com.redrawkeyboard.applytheme";
    public static final String EXTRA_PACKAGE_NAME = "package-name";
    public static final String THEME_APPLIED = "com.redrawkeyboard.THEME_APPLIED";
    public static final String THEME_TO_LOAD = "com.redrawkeyboard.THEME_TO_LOAD";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("package-name");
        StoreItemInfo checkFontExtra = checkFontExtra(intent);
        StoreItemInfo checkSoundExtra = checkSoundExtra(intent);
        int intExtra = intent.getIntExtra(GoApkThemeInstaller.EXTRA_GO_THEME_ID, -1);
        String stringExtra2 = intent.getStringExtra(GoApkThemeInstaller.EXTRA_RES_ZIP_PATH);
        if (stringExtra2 != null) {
            Intent intent2 = new Intent(context, (Class<?>) GoApkThemeInstaller.class);
            intent2.putExtra("package-name", stringExtra);
            intent2.putExtra(GoApkThemeInstaller.EXTRA_RES_ZIP_PATH, stringExtra2);
            intent2.putExtra(GoApkThemeInstaller.EXTRA_GO_THEME_ID, intExtra);
            if (checkFontExtra != null) {
                intent2.putExtra("selected-font", checkFontExtra);
            }
            if (checkSoundExtra != null) {
                intent2.putExtra("selected-sound", checkSoundExtra);
            }
            GoApkThemeInstaller.enqueueWork(context, intent2);
        } else {
            ThemeManager.getInstance().selectGoTheme(stringExtra, checkFontExtra, checkSoundExtra);
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            defaultSharedPreferences.edit().putBoolean(APPLY_PREF, true).commit();
            defaultSharedPreferences.edit().putString("package-name", stringExtra).commit();
            defaultSharedPreferences.edit().putString(GoApkThemeInstaller.EXTRA_RES_ZIP_PATH, stringExtra2).commit();
            defaultSharedPreferences.edit().putInt(GoApkThemeInstaller.EXTRA_GO_THEME_ID, intExtra).commit();
        }
        defaultSharedPreferences.edit().putBoolean(THEME_APPLIED, true).commit();
    }

    public StoreItemInfo checkFontExtra(Intent intent) {
        if (intent == null || !intent.getBooleanExtra("selected-font", false)) {
            return null;
        }
        String stringExtra = intent.getStringExtra("font_name");
        return new StoreItemInfo(intent.getIntExtra("font_id", -1), stringExtra, FontManager.FONTS_DATA_DIR, false, false, true, 0, 0, intent.getStringExtra("font_resource"), null, null, null, null, null, null, intent.getLongExtra(ViewHierarchyConstants.TEXT_SIZE, -1L), 0, 0, StoreType.PERSONALIZE, null, null, intent.getLongExtra(ViewHierarchyConstants.TEXT_SIZE, -1L), null);
    }

    public StoreItemInfo checkSoundExtra(Intent intent) {
        if (intent == null || !intent.getBooleanExtra("selected-sound", false)) {
            return null;
        }
        String stringExtra = intent.getStringExtra("sound_name");
        return new StoreItemInfo(intent.getIntExtra("sound_id", -1), stringExtra, SoundManager.SOUNDS_DATA_DIR, false, false, true, 0, 0, intent.getStringExtra("sound_resource"), null, null, null, null, null, null, intent.getLongExtra("sound_size", -1L), 0, 0, StoreType.PERSONALIZE, null, null, intent.getLongExtra("sound_size", -1L), null);
    }
}
