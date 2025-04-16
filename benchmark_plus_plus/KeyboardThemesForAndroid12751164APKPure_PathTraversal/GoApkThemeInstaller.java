package com.timmystudios.redrawkeyboard.themes.go;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.JobIntentService;
import com.android.inputmethod.latin.common.FileUtils;
import com.timmystudios.redrawkeyboard.RedrawConstants;
import com.timmystudios.redrawkeyboard.app.main.store.main.StoreItemInfo;
import com.timmystudios.redrawkeyboard.app.main.store.personalize.PersonalizeDownloader;
import com.timmystudios.redrawkeyboard.themes.InstalledThemeDescription;
import com.timmystudios.redrawkeyboard.themes.ThemeManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes3.dex */
public class GoApkThemeInstaller extends JobIntentService {
    public static final String EXTRA_GO_THEME_ID = "go_theme_id";
    public static final String EXTRA_PACKAGE_NAME = "package-name";
    public static final String EXTRA_RES_ZIP_PATH = "go_res_zip_path";
    public static final String EXTRA_SELECTED_FONT = "selected-font";
    public static final String EXTRA_SELECTED_SOUND = "selected-sound";
    protected static final transient int JOB_ID = 1986;
    private static final String TAG = "ApkInstaller";

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GoApkThemeInstaller.class, JOB_ID, intent);
    }

    @Override // androidx.core.app.JobIntentService
    protected void onHandleWork(Intent intent) {
        String stringExtra = intent.getStringExtra("package-name");
        StoreItemInfo storeItemInfo = (StoreItemInfo) intent.getParcelableExtra("selected-font");
        StoreItemInfo storeItemInfo2 = (StoreItemInfo) intent.getParcelableExtra("selected-sound");
        String stringExtra2 = intent.getStringExtra(EXTRA_RES_ZIP_PATH);
        int intExtra = intent.getIntExtra(EXTRA_GO_THEME_ID, -1);
        if (intExtra == -1) {
            return;
        }
        try {
            File file = new File(stringExtra2);
            unzip(file.getAbsolutePath(), getFilesDir() + "/" + RedrawConstants.DIR_THEMES_GO + "/" + intExtra + "/");
            FileUtils.deleteRecursively(file);
            resetThemesAndApply(intExtra);
            if (storeItemInfo != null) {
                PersonalizeDownloader.downloadItem(this, storeItemInfo, false);
            }
            if (storeItemInfo2 != null) {
                PersonalizeDownloader.downloadItem(this, storeItemInfo2, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not install theme " + stringExtra, e);
        }
    }

    public void unzip(String zipFile, String location) throws IOException {
        ZipInputStream zipInputStream;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        dirChecker(location, "");
        Closeable closeable = null;
        try {
            fileInputStream = new FileInputStream(zipFile);
            try {
                zipInputStream = new ZipInputStream(fileInputStream);
                try {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);
                    while (true) {
                        try {
                            ZipEntry nextEntry = zipInputStream.getNextEntry();
                            if (nextEntry != null) {
                                Log.v(TAG, "Unzipping " + nextEntry.getName());
                                if (nextEntry.isDirectory()) {
                                    dirChecker(location, nextEntry.getName());
                                } else {
                                    try {
                                        fileOutputStream = new FileOutputStream(location + nextEntry.getName());
                                        try {
                                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                                            try {
                                                byte[] bArr = new byte[1024];
                                                while (true) {
                                                    int read = bufferedInputStream.read(bArr);
                                                    if (read < 0) {
                                                        break;
                                                    } else {
                                                        bufferedOutputStream.write(bArr, 0, read);
                                                    }
                                                }
                                                closeStreamSafely(bufferedOutputStream);
                                                closeStreamSafely(fileOutputStream);
                                                try {
                                                    zipInputStream.closeEntry();
                                                } catch (IOException unused) {
                                                }
                                            } catch (Throwable th) {
                                                th = th;
                                                closeable = bufferedOutputStream;
                                                closeStreamSafely(closeable);
                                                closeStreamSafely(fileOutputStream);
                                                throw th;
                                            }
                                        } catch (Throwable th2) {
                                            th = th2;
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        fileOutputStream = null;
                                    }
                                }
                            } else {
                                closeStreamSafely(bufferedInputStream);
                                closeStreamSafely(zipInputStream);
                                closeStreamSafely(fileInputStream);
                                return;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            closeable = bufferedInputStream;
                            closeStreamSafely(closeable);
                            closeStreamSafely(zipInputStream);
                            closeStreamSafely(fileInputStream);
                            throw th;
                        }
                    }
                } catch (Throwable th5) {
                    th = th5;
                }
            } catch (Throwable th6) {
                th = th6;
                zipInputStream = null;
            }
        } catch (Throwable th7) {
            th = th7;
            zipInputStream = null;
            fileInputStream = null;
        }
    }

    private void dirChecker(String parent, String dir) {
        File file = new File(parent + dir);
        if (file.isDirectory()) {
            return;
        }
        file.mkdirs();
    }

    private void resetThemesAndApply(final int id) {
        List<InstalledThemeDescription> installedThemes = ThemeManager.getInstance().getInstalledThemes();
        synchronized (installedThemes) {
            Log.d(TAG, "will reset from " + Thread.currentThread());
            ThemeManager.getInstance().resetThemes();
            while (ThemeManager.getInstance().isInstalledThemesAsyncRunning()) {
                try {
                    Log.d(TAG, "waiting from " + Thread.currentThread());
                    installedThemes.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.timmystudios.redrawkeyboard.themes.go.GoApkThemeInstaller.1
            @Override // java.lang.Runnable
            public void run() {
                ThemeManager.getInstance().selectThemeById(id);
            }
        });
    }

    private boolean closeStreamSafely(Closeable closeable) {
        if (closeable == null) {
            return true;
        }
        try {
            closeable.close();
            return true;
        } catch (IOException unused) {
            return false;
        }
    }
}
