package com.theolivetree.ftpserverlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.theolivetree.utilities.FileUtil;
import java.io.File;

/* loaded from: classes.dex */
public class Prefs {
    public static final boolean DEFAULT_ANONYMOUS = true;
    public static final String DEFAULT_CUSTOMFOLDER = "/";
    public static final boolean DEFAULT_ENERGYSAVE = false;
    public static final boolean DEFAULT_FOREGROUND = true;
    public static final String DEFAULT_HOMEDIR = "1";
    public static final String DEFAULT_INTERFACES = "0";
    public static final String DEFAULT_PASIVE_PORT = "2300-2399";
    public static final String DEFAULT_PORT = "2221";
    public static final boolean DEFAULT_READONLY = false;
    public static final boolean DEFAULT_SHOWCREDENTIALS = true;
    public static final boolean DEFAULT_SHOWHIDDEN = false;
    public static final String DEFAULT_USERNAME = "ftp";
    public static final String DEFAULT_USERPASS = "ftp";
    private static String KEY_VOLUME = "PERSISTED_URI_VOLUME";
    public static final String PREF_ANONYMOUS = "prefAnonymous";
    public static final String PREF_CUSTOMFOLDER = "prefCustomFolder";
    public static final String PREF_ENERGYSAVE = "prefEnergySave";
    public static final String PREF_FOREGROUND = "prefForeground";
    public static final String PREF_HOMEDIR = "prefHomeDir";
    public static final String PREF_INTERFACES = "prefInterfaces";
    public static final String PREF_PASIVE_PORT = "prefPasivePort";
    public static final String PREF_PORT = "prefPort";
    public static final String PREF_READONLY = "prefReadonly";
    public static final String PREF_RESETPREFS = "prefResetPrefs";
    public static final String PREF_SHOWCREDENTIALS = "prefShowCredentials";
    public static final String PREF_SHOWHIDDEN = "prefShowHidden";
    public static final String PREF_USERNAME = "prefUsername";
    public static final String PREF_USERPASS = "prefUserpass";

    public static int getPort(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return Integer.parseInt(pref.getString(PREF_PORT, DEFAULT_PORT));
        } catch (Exception e) {
            return Integer.parseInt(DEFAULT_PORT);
        }
    }

    public static String getPasivePort(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getString(PREF_PASIVE_PORT, DEFAULT_PASIVE_PORT);
        } catch (Exception e) {
            return DEFAULT_PASIVE_PORT;
        }
    }

    public static boolean getAnonymous(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_ANONYMOUS, true);
        } catch (Exception e) {
            return true;
        }
    }

    public static String getUserName(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getString(PREF_USERNAME, "ftp");
        } catch (Exception e) {
            return "ftp";
        }
    }

    public static String getUserPass(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getString(PREF_USERPASS, "ftp");
        } catch (Exception e) {
            return "ftp";
        }
    }

    public static String getHomeDir(Context context) {
        String ext;
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefHomeDir = pref.getString(PREF_HOMEDIR, "1");
            if (prefHomeDir.equals("1")) {
                ext = new StringBuilder().append(Environment.getExternalStorageDirectory()).toString();
            } else if (prefHomeDir.equals("0")) {
                ext = "/";
            } else if (prefHomeDir.equals("2")) {
                ext = new StringBuilder().append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).toString();
            } else if (prefHomeDir.equals("3")) {
                ext = getCustomFolder(context);
            } else if (prefHomeDir.equals("4")) {
                ext = FileUtil.GetSecondaryPrivateDirectory(context);
                if (ext == null) {
                    ext = new StringBuilder().append(Environment.getExternalStorageDirectory()).toString();
                }
            } else {
                ext = "/";
            }
            return ext;
        } catch (Exception e) {
            return "/";
        }
    }

    public static boolean isHomeDirCustomDir(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefHomeDir = pref.getString(PREF_HOMEDIR, "1");
            return prefHomeDir.equals("3");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isHomeDirCustomDir(Context context, int value) {
        return 3 == value;
    }

    public static int getInterfaces(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefInterfaces = pref.getString(PREF_INTERFACES, "0");
            if (prefInterfaces.equals("0")) {
                return 32;
            }
            if (prefInterfaces.equals("1")) {
                return 2;
            }
            if (prefInterfaces.equals("2")) {
                return 4;
            }
            if (prefInterfaces.equals("3")) {
                return 8;
            }
            if (prefInterfaces.equals("4")) {
                return 255;
            }
            return prefInterfaces.equals("5") ? 16 : 255;
        } catch (Exception e) {
            return 255;
        }
    }

    public static boolean getReadonly(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_READONLY, false);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getShowHidden(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_SHOWHIDDEN, false);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getLock(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean prefEnergySave = pref.getBoolean(PREF_ENERGYSAVE, false);
            if (!prefEnergySave) {
                return 0;
            }
            if (Build.VERSION.SDK_INT >= 12) {
                return 2;
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean getForeground(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_FOREGROUND, true);
        } catch (Exception e) {
            return true;
        }
    }

    public static String getCustomFolder(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String folder = pref.getString(PREF_CUSTOMFOLDER, "/");
            File theDir = new File(folder);
            if (theDir.exists()) {
                if (theDir.isDirectory()) {
                    return folder;
                }
            }
            return "/";
        } catch (Exception e) {
            return "/";
        }
    }

    public static void setCustomFolder(Context context, String folder) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_CUSTOMFOLDER, folder);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public static boolean getShowCredentials(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_SHOWCREDENTIALS, true);
        } catch (Exception e) {
            return true;
        }
    }

    public static void setHomeDirUriVolume(Context context, String volume) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_VOLUME, volume);
        editor.commit();
    }

    public static String getHomeDirUriVolume(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String storedVolume = settings.getString(KEY_VOLUME, "");
        if (storedVolume == null) {
            return "";
        }
        return storedVolume;
    }
}
