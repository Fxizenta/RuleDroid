package com.owncloud.android.data.storage;

import android.os.Environment;
import java.io.File;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LegacyStorageProvider.kt */
@Deprecated(message = "Do not use this anymore. We have moved to Scoped Storage")
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, d2 = {"Lcom/owncloud/android/data/storage/LegacyStorageProvider;", "Lcom/owncloud/android/data/storage/LocalStorageProvider;", "rootFolderName", "", "(Ljava/lang/String;)V", "getPrimaryStorageDirectory", "Ljava/io/File;", "owncloudData_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class LegacyStorageProvider extends LocalStorageProvider {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public LegacyStorageProvider(String rootFolderName) {
        super(rootFolderName, null);
        Intrinsics.checkNotNullParameter(rootFolderName, "rootFolderName");
    }

    @Override // com.owncloud.android.data.storage.LocalStorageProvider
    public File getPrimaryStorageDirectory() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        Intrinsics.checkNotNullExpressionValue(externalStorageDirectory, "getExternalStorageDirectory()");
        return externalStorageDirectory;
    }
}
