package com.apple.android.music.playback.player.datasource;

import android.net.Uri;
import com.apple.android.music.playback.androidauto.AndroidAutoMediaProvider;
import com.apple.android.music.playback.model.NetworkConnectionDeniedException;
import com.apple.android.music.playback.player.MediaPlayerContext;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import f.c.b.a.a;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* compiled from: MusicApp */
/* loaded from: classes.dex */
public final class PlayerHttpDataSource implements DataSource {
    public static final int DEFAULT_CONNECT_TIMEOUT = 4000;
    public static final int DEFAULT_READ_TIMEOUT = 2000;
    public static final String TAG = "PlayerHttpDataSource";
    public final int connectTimeoutMs;
    public HttpURLConnection connection;
    public final CookieManager cookieManager;
    public DataSpec dataSpec;
    public final int dataType;
    public InputStream inputStream;
    public final UUID playbackSessionId;
    public final MediaPlayerContext playerContext;
    public final int readTimeoutMs;
    public long skipByteCount;
    public long totalByteCount;
    public long totalBytesRead;
    public long totalBytesSkipped;
    public final TransferListener transferListener;
    public final String userAgent;
    public static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
    public static final AtomicReference<byte[]> skipBufferReference = new AtomicReference<>();

    public PlayerHttpDataSource(int i, String str, MediaPlayerContext mediaPlayerContext, CookieManager cookieManager, UUID uuid, TransferListener transferListener, int i2, int i3) {
        this.dataType = i;
        this.userAgent = str;
        this.playerContext = mediaPlayerContext;
        this.cookieManager = cookieManager;
        this.playbackSessionId = uuid;
        this.transferListener = transferListener;
        this.connectTimeoutMs = i2;
        this.readTimeoutMs = i3;
    }

    private void closeConnection() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            } catch (Exception unused) {
            }
            this.connection = null;
        }
    }

    private void convertCookiesToRFC2109(DataSpec dataSpec) {
        CookieStore cookieStore = this.cookieManager.getCookieStore();
        URI create = URI.create(dataSpec.uri.toString());
        for (HttpCookie httpCookie : cookieStore.get(create)) {
            if (httpCookie.getVersion() > 0) {
                httpCookie.setVersion(0);
                cookieStore.add(create, httpCookie);
            }
        }
    }

    public static String getAcceptLanguage() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        return country.isEmpty() ? language : a.a(language, AndroidAutoMediaProvider.ITEM_ID_DELIMITER, country);
    }

    public static long getContentLength(HttpURLConnection httpURLConnection) {
        long j;
        String headerField;
        String headerField2 = httpURLConnection.getHeaderField("Content-Length");
        if (headerField2 != null && !headerField2.isEmpty()) {
            try {
                j = Long.parseLong(headerField2);
            } catch (NumberFormatException unused) {
            }
            headerField = httpURLConnection.getHeaderField("Content-Range");
            if (headerField == null && !headerField.isEmpty()) {
                Matcher matcher = CONTENT_RANGE_PATTERN.matcher(headerField);
                if (!matcher.find()) {
                    return j;
                }
                try {
                    long parseLong = (Long.parseLong(matcher.group(2)) - Long.parseLong(matcher.group(1))) + 1;
                    return j < 0 ? parseLong : Math.max(j, parseLong);
                } catch (NumberFormatException unused2) {
                    return j;
                }
            }
        }
        j = -1;
        headerField = httpURLConnection.getHeaderField("Content-Range");
        return headerField == null ? j : j;
    }

    private HttpURLConnection openConnection(DataSpec dataSpec) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(dataSpec.uri.toString()).openConnection();
        httpURLConnection.setConnectTimeout(this.connectTimeoutMs);
        httpURLConnection.setReadTimeout(this.readTimeoutMs);
        httpURLConnection.setUseCaches(shouldUseHttpCache(this.dataType));
        httpURLConnection.setRequestProperty("Accept", "*/*");
        httpURLConnection.setRequestProperty("Accept-Language", getAcceptLanguage());
        httpURLConnection.setRequestProperty("User-Agent", this.userAgent);
        httpURLConnection.setRequestProperty("X-Playback-Session-Id", this.playbackSessionId.toString());
        convertCookiesToRFC2109(dataSpec);
        httpURLConnection.setInstanceFollowRedirects(true);
        if (!dataSpec.isFlagSet(1)) {
            httpURLConnection.setRequestProperty("Accept-Encoding", HlsPlaylistParser.KEYFORMAT_IDENTITY);
        }
        long j = dataSpec.position;
        long j2 = dataSpec.length;
        if (j != 0 || j2 != -1) {
            StringBuilder sb = new StringBuilder("bytes=");
            sb.append(j);
            sb.append('-');
            if (j2 != -1) {
                sb.append(j2 + j);
            }
            httpURLConnection.setRequestProperty("Range", sb.toString());
        }
        byte[] bArr = dataSpec.httpBody;
        if (bArr != null) {
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            if (bArr.length == 0) {
                httpURLConnection.connect();
            } else {
                httpURLConnection.setFixedLengthStreamingMode(bArr.length);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(bArr);
                outputStream.close();
            }
        } else {
            httpURLConnection.connect();
        }
        return httpURLConnection;
    }

    public static boolean shouldUseHttpCache(int i) {
        return i == 4;
    }

    private void skip() {
        if (this.totalBytesSkipped == this.skipByteCount) {
            return;
        }
        byte[] andSet = skipBufferReference.getAndSet(null);
        if (andSet == null) {
            andSet = new byte[4096];
        }
        while (true) {
            long j = this.totalBytesSkipped;
            long j2 = this.skipByteCount;
            if (j != j2) {
                int read = this.inputStream.read(andSet, 0, (int) Math.min(j2 - j, andSet.length));
                if (Thread.interrupted()) {
                    throw new InterruptedIOException();
                }
                if (read != -1) {
                    this.totalBytesSkipped += read;
                    TransferListener transferListener = this.transferListener;
                    if (transferListener != null) {
                        transferListener.onBytesTransferred(this, this.dataSpec, true, read);
                    }
                } else {
                    throw new EOFException();
                }
            } else {
                skipBufferReference.set(andSet);
                return;
            }
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void addTransferListener(TransferListener transferListener) {
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() {
        TransferListener transferListener;
        boolean z = this.inputStream != null;
        try {
            if (this.inputStream != null) {
                try {
                    this.inputStream.close();
                } catch (IOException e) {
                    String str = "close() IOException msg: " + e.getMessage();
                    throw new HttpDataSource.HttpDataSourceException(e, this.dataSpec, 3);
                } catch (IllegalStateException e2) {
                    String str2 = "close() IllegalStateException msg: " + e2.getMessage();
                    throw new HttpDataSource.HttpDataSourceException(new IOException(e2.getMessage()), this.dataSpec, 3);
                }
            }
        } finally {
            this.inputStream = null;
            closeConnection();
            if (z && (transferListener = this.transferListener) != null) {
                transferListener.onTransferEnd(this, this.dataSpec, true);
            }
        }
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Map<String, List<String>> getResponseHeaders() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null) {
            return Collections.emptyMap();
        }
        return httpURLConnection.getHeaderFields();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            return Uri.parse(httpURLConnection.getURL().toString());
        }
        return Uri.EMPTY;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public boolean isReadingFromNetwork() {
        return this.inputStream != null;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) {
        if (this.playerContext.canMakeNetworkRequestWithCurrentConditions()) {
            Uri uri = dataSpec.uri;
            if (uri != null && !uri.toString().isEmpty()) {
                CookieManager.setDefault(this.cookieManager);
                this.dataSpec = dataSpec;
                this.totalByteCount = 0L;
                this.skipByteCount = 0L;
                this.totalBytesRead = 0L;
                this.totalBytesSkipped = 0L;
                try {
                    this.connection = openConnection(dataSpec);
                    int responseCode = this.connection.getResponseCode();
                    if (responseCode >= 200 && responseCode <= 299) {
                        if (responseCode == 200) {
                            long j = dataSpec.position;
                            if (j != 0) {
                                this.skipByteCount = j;
                            }
                        }
                        try {
                            this.inputStream = this.connection.getInputStream();
                            if (dataSpec.isFlagSet(1)) {
                                this.totalByteCount = dataSpec.length;
                            } else {
                                long j2 = dataSpec.length;
                                if (j2 != -1) {
                                    this.totalByteCount = j2;
                                } else {
                                    this.totalByteCount = getContentLength(this.connection);
                                    long j3 = this.totalByteCount;
                                    if (j3 != -1) {
                                        this.totalByteCount = j3 - this.skipByteCount;
                                    }
                                }
                            }
                            TransferListener transferListener = this.transferListener;
                            if (transferListener != null) {
                                transferListener.onTransferStart(this, dataSpec, true);
                            }
                            return this.totalByteCount;
                        } catch (IOException e) {
                            closeConnection();
                            throw new HttpDataSource.HttpDataSourceException(e, dataSpec, 1);
                        }
                    }
                    Map<String, List<String>> headerFields = this.connection.getHeaderFields();
                    closeConnection();
                    throw new HttpDataSource.InvalidResponseCodeException(responseCode, headerFields, dataSpec);
                } catch (IOException e2) {
                    closeConnection();
                    StringBuilder b = a.b("Error connecting to ");
                    b.append(dataSpec.uri.toString());
                    throw new HttpDataSource.HttpDataSourceException(b.toString(), e2, dataSpec, 1);
                }
            }
            StringBuilder b2 = a.b("open() ERROR invalid dataSpec dataType: ");
            b2.append(this.dataType);
            b2.toString();
            throw new IOException("ERROR invalid dataSpec Uri");
        }
        throw new NetworkConnectionDeniedException("Network unreachable with current settings");
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] bArr, int i, int i2) {
        if (i2 == 0) {
            return 0;
        }
        skip();
        long j = this.totalByteCount;
        if (j != -1) {
            long j2 = j - this.totalBytesRead;
            if (j2 == 0) {
                return -1;
            }
            i2 = (int) Math.min(i2, j2);
        }
        try {
            int read = this.inputStream.read(bArr, i, i2);
            if (read == -1) {
                return -1;
            }
            this.totalBytesRead += read;
            if (this.transferListener != null) {
                this.transferListener.onBytesTransferred(this, this.dataSpec, true, read);
            }
            return read;
        } catch (IOException e) {
            StringBuilder b = a.b("read() key: ");
            b.append(this.dataSpec.key);
            b.append(" error: ");
            b.append(e.toString());
            b.toString();
            throw new HttpDataSource.HttpDataSourceException(e, this.dataSpec, 2);
        }
    }

    public PlayerHttpDataSource(int i, String str, MediaPlayerContext mediaPlayerContext, CookieManager cookieManager, UUID uuid, TransferListener transferListener) {
        this(i, str, mediaPlayerContext, cookieManager, uuid, transferListener, 4000, 2000);
    }
}
