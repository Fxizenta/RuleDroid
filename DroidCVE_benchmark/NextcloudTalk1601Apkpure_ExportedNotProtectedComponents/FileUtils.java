package com.nextcloud.talk.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.fasterxml.jackson.core.JsonPointer;
import com.google.android.exoplayer2.source.rtsp.SessionDescription;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import kotlin.Metadata;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* compiled from: FileUtils.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0007J\u0018\u0010\u0010\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0018\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u000e2\b\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u0018\u0010\u0013\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0007H\u0007J\u0010\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\nH\u0007J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/nextcloud/talk/utils/FileUtils;", "", "()V", "MD5_LENGTH", "", "RADIX", "TAG", "", "kotlin.jvm.PlatformType", "copyFileToCache", "Ljava/io/File;", "context", "Landroid/content/Context;", "sourceFileUri", "Landroid/net/Uri;", "filename", "getFileFromUri", "getFileName", "uri", "getTempCacheFile", "fileName", "md5Sum", "file", "removeTempCacheFile", "", "app_gplayRelease"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class FileUtils {
    private static final int MD5_LENGTH = 32;
    private static final int RADIX = 16;
    public static final FileUtils INSTANCE = new FileUtils();
    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    @JvmStatic
    public static final File getTempCacheFile(Context context, String fileName) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        File file = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + JsonPointer.SEPARATOR + fileName);
        String str = TAG;
        StringBuilder sb = new StringBuilder("Full path for new cache file:");
        sb.append(file.getAbsolutePath());
        Log.v(str, sb.toString());
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            throw new FileNotFoundException("could not cacheFile.getParentFile()");
        }
        if (!parentFile.exists()) {
            Log.v(str, "The folder in which the new file should be created does not exist yet. Trying to create it…");
            if (parentFile.mkdirs()) {
                Log.v(str, "Creation successful");
            } else {
                throw new IOException("Directory for temporary file does not exist and could not be created.");
            }
        }
        Log.v(str, "- Try to create actual cache file");
        if (file.createNewFile()) {
            Log.v(str, "Successfully created cache file");
            return file;
        }
        throw new IOException("Failed to create cacheFile");
    }

    public final void removeTempCacheFile(Context context, String fileName) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        File file = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + JsonPointer.SEPARATOR + fileName);
        String str = TAG;
        StringBuilder sb = new StringBuilder("Full path for new cache file:");
        sb.append(file.getAbsolutePath());
        Log.v(str, sb.toString());
        if (file.exists()) {
            if (file.delete()) {
                Log.v(str, "Deletion successful");
                return;
            }
            throw new IOException("Directory for temporary file does not exist and could not be created.");
        }
    }

    public final File getFileFromUri(Context context, Uri sourceFileUri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(sourceFileUri, "sourceFileUri");
        String fileName = getFileName(sourceFileUri, context);
        String scheme2 = sourceFileUri.getScheme();
        if (scheme2 == null) {
            Log.d(TAG, "relative uri: " + sourceFileUri.getPath());
            throw new IllegalArgumentException("relative paths are not supported");
        }
        if (Intrinsics.areEqual("content", scheme2)) {
            return copyFileToCache(context, sourceFileUri, fileName);
        }
        if (Intrinsics.areEqual("file", scheme2)) {
            if (sourceFileUri.getPath() != null) {
                String path = sourceFileUri.getPath();
                if (path != null) {
                    return new File(path);
                }
                return null;
            }
            throw new IllegalArgumentException("uri does not contain path");
        }
        throw new IllegalArgumentException("unsupported scheme: " + sourceFileUri.getPath());
    }

    public final File copyFileToCache(Context context, Uri sourceFileUri, String filename) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(sourceFileUri, "sourceFileUri");
        Intrinsics.checkNotNullParameter(filename, "filename");
        File file = new File(context.getCacheDir(), filename);
        if (file.exists()) {
            Log.d(TAG, "file is already in cache");
        } else {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                InputStream openInputStream = context.getContentResolver().openInputStream(sourceFileUri);
                if (openInputStream != null) {
                    FileOutputStream fileOutputStream2 = openInputStream;
                    try {
                        fileOutputStream2 = fileOutputStream;
                        try {
                            long copyTo$default = ByteStreamsKt.copyTo$default(fileOutputStream2, fileOutputStream2, 0, 2, null);
                            CloseableKt.closeFinally(fileOutputStream2, null);
                            Long.valueOf(copyTo$default);
                            CloseableKt.closeFinally(fileOutputStream2, null);
                        } finally {
                        }
                    } finally {
                    }
                }
                fileOutputStream.flush();
            } catch (FileNotFoundException e) {
                Log.w(TAG, "failed to copy file to cache", e);
            }
        }
        return file;
    }

    public final String getFileName(Uri uri, Context context) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        String str = null;
        if (Intrinsics.areEqual(uri.getScheme(), "content") && context != null) {
            Cursor query = context.getContentResolver().query(uri, null, null, null, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        str = query.getString(query.getColumnIndexOrThrow("_display_name"));
                    }
                } finally {
                    if (query != null) {
                        query.close();
                    }
                }
            }
        }
        if (str != null) {
            return str;
        }
        String path = uri.getPath();
        Intrinsics.checkNotNull(path);
        int lastIndexOf$default = StringsKt.lastIndexOf$default((CharSequence) path, JsonPointer.SEPARATOR, 0, false, 6, (Object) null);
        if (lastIndexOf$default == -1) {
            return path;
        }
        String substring = path.substring(lastIndexOf$default + 1);
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String).substring(startIndex)");
        return substring;
    }

    @JvmStatic
    public static final String md5Sum(File file) {
        Intrinsics.checkNotNullParameter(file, "file");
        String str = file.getName() + file.lastModified() + file.length();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] bytes = str.getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes, "this as java.lang.String).getBytes(charset)");
        messageDigest.update(bytes);
        StringBuilder sb = new StringBuilder(new BigInteger(1, messageDigest.digest()).toString(16));
        while (sb.length() < 32) {
            sb.insert(0, SessionDescription.SUPPORTED_SDP_VERSION);
        }
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "md5String.toString()");
        return sb2;
    }
}
