package de.heinekingmedia.stashcat_api.model.cloud;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;
import de.heinekingmedia.stashcat_api.customs.g;
import de.heinekingmedia.stashcat_api.model.base.ChangeableBaseModel;
import de.heinekingmedia.stashcat_api.model.enums.i;
import de.heinekingmedia.stashcat_api.model.enums.t;
import java.io.Serializable;
import java.util.Date;

@Keep
/* loaded from: classes2.dex */
public class File extends ChangeableBaseModel<File> implements Parcelable, Comparable<File>, Serializable {
    public static final Parcelable.Creator<File> CREATOR = new a();
    private Date dateDeleted;
    private String downloadURL;
    private String ext;
    private boolean isFolder;
    private Date last_download;
    private Date latestFolderContentUpdate;
    private String md5;
    private MediaDimension mediaDimensions;
    private String mime;
    private String name;
    private long ownerID;
    private long parent_id;
    private Permission permission;
    private String preview;
    private String previewURL;
    private int size;
    private String size_string;
    private i status;
    private long times_downloaded;
    private t type;
    private long type_id;
    private Date uploaded;

    /* loaded from: classes2.dex */
    static class a implements Parcelable.Creator<File> {
        a() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public File createFromParcel(Parcel parcel) {
            return new File(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public File[] newArray(int i2) {
            return new File[i2];
        }
    }

    public File() {
        this.isFolder = false;
        this.dateDeleted = null;
        this.ext = "";
        this.size_string = "";
        this.name = "";
        this.downloadURL = "";
        this.ownerID = -1L;
        this.type = t.NONE;
        this.md5 = "";
    }

    protected File(Parcel parcel) {
        this.isFolder = false;
        this.dateDeleted = null;
        this.ext = "";
        this.size_string = "";
        this.name = "";
        this.downloadURL = "";
        this.ownerID = -1L;
        this.type = t.NONE;
        this.md5 = "";
        this.id = parcel.readLong();
        this.isFolder = parcel.readByte() != 0;
        long readLong = parcel.readLong();
        this.dateDeleted = readLong != -1 ? new Date(readLong) : null;
        this.ext = parcel.readString();
        this.size_string = parcel.readString();
        this.size = parcel.readInt();
        this.times_downloaded = parcel.readLong();
        this.previewURL = parcel.readString();
        this.name = parcel.readString();
        this.ownerID = parcel.readLong();
        this.mime = parcel.readString();
        this.status = i.findByKey(parcel.readString());
        this.permission = (Permission) parcel.readParcelable(File.class.getClassLoader());
        this.type = t.findByKey(parcel.readString());
        this.parent_id = parcel.readLong();
        this.type_id = parcel.readLong();
        long readLong2 = parcel.readLong();
        this.last_download = readLong2 != -1 ? new Date(readLong2) : null;
        long readLong3 = parcel.readLong();
        this.uploaded = readLong3 != -1 ? new Date(readLong3) : null;
        this.mediaDimensions = (MediaDimension) parcel.readParcelable(MediaDimension.class.getClassLoader());
        initDownloadUrl();
    }

    @Keep
    public File(g gVar) {
        this(gVar, t.NONE, 0L);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0050, code lost:
    
        if (r4.isEmpty() != false) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x003b, code lost:
    
        if (r4.isEmpty() != false) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0053, code lost:
    
        r12 = de.heinekingmedia.stashcat_api.model.enums.t.findByKey(r4);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public File(de.heinekingmedia.stashcat_api.customs.g r11, de.heinekingmedia.stashcat_api.model.enums.t r12, long r13) {
        /*
            Method dump skipped, instructions count: 263
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: de.heinekingmedia.stashcat_api.model.cloud.File.<init>(de.heinekingmedia.stashcat_api.customs.g, de.heinekingmedia.stashcat_api.model.enums.t, long):void");
    }

    public File(File file) {
        this.isFolder = false;
        this.dateDeleted = null;
        this.ext = "";
        this.size_string = "";
        this.name = "";
        this.downloadURL = "";
        this.ownerID = -1L;
        this.type = t.NONE;
        this.md5 = "";
        this.isFolder = file.isFolder();
        this.type = file.getType();
        if (this.isFolder) {
            this.parent_id = file.getParent_id();
            this.type_id = file.getType_id();
        }
        this.dateDeleted = file.dateDeleted;
        this.ext = file.getExt();
        this.id = file.getId();
        this.last_download = file.getLast_download();
        this.mime = file.getMime();
        this.name = file.getName();
        this.ownerID = file.getOwnerID();
        this.permission = file.getPermission();
        this.previewURL = file.getPreviewURLString();
        this.size = file.getSize();
        this.size_string = file.getSize_string();
        this.status = file.getStatus();
        this.times_downloaded = file.getTimes_downloaded();
        this.uploaded = file.getUploaded();
        MediaDimension mediaDimension = file.mediaDimensions;
        this.mediaDimensions = mediaDimension != null ? mediaDimension.copy2() : null;
        initDownloadUrl();
    }

    @Override // java.lang.Comparable
    public int compareTo(File file) {
        if (this.isFolder && !file.isFolder()) {
            return -1;
        }
        if (!file.isFolder || this.isFolder) {
            return this.name.compareToIgnoreCase(file.name);
        }
        return 1;
    }

    @Override // de.heinekingmedia.stashcat_api.model.base.ChangeableBaseModel
    /* renamed from: copy, reason: avoid collision after fix types in other method */
    public File copy2() {
        return new File(this);
    }

    @Override // de.heinekingmedia.stashcat_api.model.base.ChangeableBaseModel, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == null || !File.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        File file = (File) obj;
        return this.id == file.id && this.isFolder == file.isFolder && this.type == file.type;
    }

    public Date getDateDeleted() {
        return this.dateDeleted;
    }

    public String getDownloadURL() {
        String str = this.downloadURL;
        if (str == null || str.isEmpty()) {
            initDownloadUrl();
        }
        return this.downloadURL;
    }

    public String getExt() {
        return this.ext.toLowerCase();
    }

    public Date getLast_download() {
        return this.last_download;
    }

    public Date getLatestFolderContentUpdate() {
        return this.latestFolderContentUpdate;
    }

    public String getMd5() {
        return this.md5;
    }

    public MediaDimension getMediaDimensions() {
        return this.mediaDimensions;
    }

    public String getMime() {
        return this.mime;
    }

    public String getName() {
        return this.name;
    }

    public String getNameWithoutExt() {
        if (this.isFolder) {
            return this.name;
        }
        String[] split = this.name.split("\\.");
        int i2 = 0;
        for (String str : split) {
            if (!str.isEmpty()) {
                i2++;
            }
        }
        if (i2 <= 1) {
            return this.name;
        }
        return this.name.substring(0, (this.name.length() - split[split.length - 1].length()) - 1);
    }

    public long getOwnerID() {
        return this.ownerID;
    }

    public long getParent_id() {
        return this.parent_id;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public String getPreview() {
        return this.preview;
    }

    public String getPreviewURLString() {
        return this.previewURL;
    }

    public String getPreviewUrl() {
        String str = this.previewURL;
        if (str == null || str.isEmpty()) {
            str = de.heinekingmedia.stashcat_api.a.a() + "/file/image?id=" + this.id;
        }
        return str + "&client_key=" + de.heinekingmedia.stashcat_api.a.c() + "&device_id=" + de.heinekingmedia.stashcat_api.a.d();
    }

    public int getSize() {
        return this.size;
    }

    public String getSize_string() {
        return this.size_string;
    }

    public i getStatus() {
        return this.status;
    }

    public long getTimes_downloaded() {
        return this.times_downloaded;
    }

    public t getType() {
        return this.type;
    }

    public long getType_id() {
        return this.type_id;
    }

    public Date getUploaded() {
        return this.uploaded;
    }

    public boolean hasExtension() {
        if (isFolder()) {
            return false;
        }
        int i2 = 0;
        for (String str : this.name.split("\\.")) {
            if (!str.isEmpty()) {
                i2++;
            }
        }
        return i2 > 1;
    }

    public int hashCode() {
        return ((267 + ((int) this.id)) * 89) + (this.isFolder ? 1 : 0);
    }

    public void initDownloadUrl() {
        this.downloadURL = de.heinekingmedia.stashcat_api.a.a() + "/file/download?id=" + this.id + "&client_key=" + de.heinekingmedia.stashcat_api.a.c() + "&device_id=" + de.heinekingmedia.stashcat_api.a.d();
    }

    @Override // de.heinekingmedia.stashcat_api.model.base.ChangeableBaseModel
    public boolean isChanged(File file) {
        i iVar;
        Date date;
        if (this.isFolder != file.isFolder) {
            return true;
        }
        Date date2 = this.dateDeleted;
        if (date2 == null || (date = file.dateDeleted) == null ? this.dateDeleted != file.dateDeleted : date2.compareTo(date) != 0) {
            return true;
        }
        String str = this.ext;
        if (str == null ? file.ext != null : !str.equals(file.ext)) {
            return true;
        }
        String str2 = this.size_string;
        if (str2 == null ? file.size_string != null : !str2.equals(file.size_string)) {
            return true;
        }
        if (this.size != file.size || this.times_downloaded != file.times_downloaded) {
            return true;
        }
        String str3 = this.previewURL;
        if (str3 == null ? file.previewURL != null : !str3.equals(file.previewURL)) {
            return true;
        }
        String str4 = this.preview;
        if (str4 == null ? file.preview != null : !str4.equals(file.preview)) {
            return true;
        }
        String str5 = this.name;
        if (str5 == null ? file.name != null : !str5.equals(file.name)) {
            return true;
        }
        if (this.ownerID != file.ownerID) {
            return true;
        }
        String str6 = this.mime;
        if (str6 == null ? file.mime != null : !str6.equals(file.mime)) {
            return true;
        }
        if (this.parent_id != file.parent_id || this.type_id != file.type_id) {
            return true;
        }
        Date date3 = this.last_download;
        if (date3 == null ? file.last_download != null : date3.compareTo(file.last_download) != 0) {
            return true;
        }
        Date date4 = this.uploaded;
        if (date4 == null ? file.uploaded != null : date4.compareTo(file.uploaded) != 0) {
            return true;
        }
        if ((this.status == null && file.status != null) || ((iVar = this.status) != null && file.status != null && !iVar.getText().equals(file.status.getText()))) {
            return true;
        }
        MediaDimension mediaDimension = this.mediaDimensions;
        MediaDimension mediaDimension2 = file.mediaDimensions;
        if (mediaDimension != null) {
            if (!mediaDimension.isChanged(mediaDimension2)) {
                return false;
            }
        } else if (mediaDimension2 == null) {
            return false;
        }
        return true;
    }

    public boolean isDeleted() {
        return this.dateDeleted != null;
    }

    public boolean isFolder() {
        return this.isFolder;
    }

    @Override // de.heinekingmedia.stashcat_api.model.base.ChangeableBaseModel
    public void mergeMissingFromOld(File file) {
        MediaDimension mediaDimension;
        MediaDimension mediaDimension2;
        if (file == null) {
            return;
        }
        String str = this.ext;
        if (str == null || str.isEmpty()) {
            this.ext = file.ext;
        }
        String str2 = this.size_string;
        if (str2 == null || str2.isEmpty()) {
            this.size_string = file.size_string;
        }
        if (this.size == -1) {
            this.size = file.size;
        }
        if (this.times_downloaded == -1) {
            this.times_downloaded = file.times_downloaded;
        }
        String str3 = this.previewURL;
        if (str3 == null || str3.isEmpty()) {
            this.previewURL = file.previewURL;
        }
        String str4 = this.preview;
        if (str4 == null || str4.isEmpty()) {
            this.preview = file.preview;
        }
        String str5 = this.name;
        if (str5 == null || str5.isEmpty()) {
            this.name = file.name;
        }
        String str6 = this.downloadURL;
        if (str6 == null || str6.isEmpty()) {
            this.downloadURL = file.downloadURL;
        }
        if (this.ownerID == -1) {
            this.ownerID = file.ownerID;
        }
        String str7 = this.mime;
        if (str7 == null || str7.isEmpty()) {
            this.mime = file.mime;
        }
        if (this.status == null) {
            this.status = file.status;
        }
        if (this.permission == null) {
            this.permission = file.permission;
        }
        t tVar = this.type;
        if (tVar == null || (tVar == t.NONE && file.type != null)) {
            this.type = file.type;
        }
        if (this.parent_id == -1) {
            this.parent_id = file.parent_id;
        }
        long j2 = this.type_id;
        if (j2 == -1 || j2 == 0) {
            this.type_id = file.type_id;
        }
        if (this.last_download == null) {
            this.last_download = file.last_download;
        }
        if (this.uploaded == null) {
            this.uploaded = file.uploaded;
        }
        String str8 = this.md5;
        if (str8 == null || str8.isEmpty()) {
            this.md5 = file.md5;
        }
        MediaDimension mediaDimension3 = this.mediaDimensions;
        if (mediaDimension3 == null && mediaDimension3 != (mediaDimension2 = file.mediaDimensions)) {
            this.mediaDimensions = mediaDimension2;
            return;
        }
        MediaDimension mediaDimension4 = this.mediaDimensions;
        if (mediaDimension4 == null || (mediaDimension = file.mediaDimensions) == null) {
            return;
        }
        mediaDimension4.mergeMissingFromOld(mediaDimension);
    }

    public void setDateDeleted(Date date) {
        this.dateDeleted = date;
    }

    public void setDownloadURL(String str) {
        this.downloadURL = str;
    }

    public void setExt(String str) {
        this.ext = str;
    }

    public void setFolder(boolean z) {
        this.isFolder = z;
    }

    public void setLast_download(Date date) {
        this.last_download = date;
    }

    public void setLatestFolderContentUpdate(Date date) {
        this.latestFolderContentUpdate = date;
    }

    public void setMd5(String str) {
        this.md5 = str;
    }

    public void setMediaDimensions(MediaDimension mediaDimension) {
        this.mediaDimensions = mediaDimension;
    }

    public void setMime(String str) {
        this.mime = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setOwnerID(long j2) {
        this.ownerID = j2;
    }

    public void setParent_id(long j2) {
        this.parent_id = j2;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public void setPreview(String str) {
        this.previewURL = str;
    }

    public void setPreviewBase64(String str) {
        this.preview = str;
    }

    public void setSize(int i2) {
        this.size = i2;
    }

    public void setSize_string(String str) {
        this.size_string = str;
    }

    public void setStatus(i iVar) {
        this.status = iVar;
    }

    public void setTimes_downloaded(long j2) {
        this.times_downloaded = j2;
    }

    public void setType(t tVar) {
        this.type = tVar;
    }

    public void setType_id(long j2) {
        this.type_id = j2;
    }

    public void setUploaded(Date date) {
        this.uploaded = date;
    }

    @Override // de.heinekingmedia.stashcat_api.model.base.ChangeableBaseModel, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeLong(this.id);
        parcel.writeByte(this.isFolder ? (byte) 1 : (byte) 0);
        Date date = this.dateDeleted;
        parcel.writeLong(date != null ? date.getTime() : -1L);
        parcel.writeString(this.ext);
        parcel.writeString(this.size_string);
        parcel.writeInt(this.size);
        parcel.writeLong(this.times_downloaded);
        parcel.writeString(this.previewURL);
        parcel.writeString(this.name);
        parcel.writeLong(this.ownerID);
        parcel.writeString(this.mime);
        parcel.writeString(this.status.getText());
        parcel.writeParcelable(this.permission, 0);
        parcel.writeString(this.type.getText());
        parcel.writeLong(this.parent_id);
        parcel.writeLong(this.type_id);
        Date date2 = this.last_download;
        parcel.writeLong(date2 != null ? date2.getTime() : -1L);
        Date date3 = this.uploaded;
        parcel.writeLong(date3 != null ? date3.getTime() : -1L);
        parcel.writeParcelable(this.mediaDimensions, i2);
    }
}
