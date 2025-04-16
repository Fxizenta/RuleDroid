package com.google.samples.apps.iosched.sync;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.samples.apps.iosched.io.model.DataManifest;
import com.google.samples.apps.iosched.util.HashUtils;
import com.google.samples.apps.iosched.util.IOUtils;
import com.google.samples.apps.iosched.util.LogUtils;
import com.google.samples.apps.iosched.util.TimeUtils;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.ConsoleRequestLogger;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.RequestLogger;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.List;

/* loaded from: classes.dex */
public class RemoteConferenceDataFetcher {
    private Context mContext;
    private String mManifestUrl;
    private static final String TAG = LogUtils.makeLogTag(SyncHelper.class);
    private static String CACHE_DIR = "data_cache";
    private String mServerTimestamp = null;
    private HashSet<String> mCacheFilesToKeep = new HashSet<>();
    private long mBytesDownloaded = 0;
    private long mBytesReadFromCache = 0;
    private RequestLogger mQuietLogger = new ConsoleRequestLogger() { // from class: com.google.samples.apps.iosched.sync.RemoteConferenceDataFetcher.1
        @Override // com.turbomanage.httpclient.ConsoleRequestLogger, com.turbomanage.httpclient.RequestLogger
        public void logRequest(HttpURLConnection uc, Object content) throws IOException {
        }

        @Override // com.turbomanage.httpclient.ConsoleRequestLogger, com.turbomanage.httpclient.RequestLogger
        public void logResponse(HttpResponse res) {
        }
    };

    public RemoteConferenceDataFetcher(Context context) {
        this.mContext = null;
        this.mManifestUrl = null;
        this.mContext = context;
        this.mManifestUrl = getManifestUrl();
    }

    public String[] fetchConferenceDataIfNewer(String refTimestamp) throws IOException {
        if (TextUtils.isEmpty(this.mManifestUrl)) {
            LogUtils.LOGW(TAG, "Manifest URL is empty (remote sync disabled!).");
            return null;
        }
        BasicHttpClient httpClient = new BasicHttpClient();
        httpClient.setRequestLogger(this.mQuietLogger);
        IOUtils.authorizeHttpClient(this.mContext, httpClient);
        if (!TextUtils.isEmpty(refTimestamp)) {
            if (TimeUtils.isValidFormatForIfModifiedSinceHeader(refTimestamp)) {
                httpClient.addHeader("If-Modified-Since", refTimestamp);
            } else {
                LogUtils.LOGW(TAG, "Could not set If-Modified-Since HTTP header. Potentially downloading unnecessary data. Invalid format of refTimestamp argument: " + refTimestamp);
            }
        }
        HttpResponse response = httpClient.get(this.mManifestUrl, null);
        if (response == null) {
            LogUtils.LOGE(TAG, "Request for manifest returned null response.");
            throw new IOException("Request for data manifest returned null response.");
        }
        int status = response.getStatus();
        if (status == 200) {
            LogUtils.LOGD(TAG, "Server returned HTTP_OK, so new data is available.");
            this.mServerTimestamp = getLastModified(response);
            LogUtils.LOGD(TAG, "Server timestamp for new data is: " + this.mServerTimestamp);
            String body = response.getBodyAsString();
            if (TextUtils.isEmpty(body)) {
                LogUtils.LOGE(TAG, "Request for manifest returned empty data.");
                throw new IOException("Error fetching conference data manifest: no data.");
            }
            LogUtils.LOGD(TAG, "Manifest " + this.mManifestUrl + " read, contents: " + body);
            this.mBytesDownloaded += body.getBytes().length;
            return processManifest(body);
        }
        if (status == 304) {
            LogUtils.LOGD(TAG, "HTTP_NOT_MODIFIED: data has not changed since " + refTimestamp);
            return null;
        }
        LogUtils.LOGE(TAG, "Error fetching conference data: HTTP status " + status + " and manifest " + this.mManifestUrl);
        throw new IOException("Error fetching conference data: HTTP status " + status);
    }

    public String getServerDataTimestamp() {
        return this.mServerTimestamp;
    }

    private String getManifestUrl() {
        File urlOverrideFile = new File(this.mContext.getFilesDir(), "iosched_manifest_url_override.txt");
        if (!urlOverrideFile.exists()) {
            return "http://storage.googleapis.com/io2017-festivus/manifest_v1.json";
        }
        try {
            String overrideUrl = IOUtils.readFileAsString(urlOverrideFile).trim();
            LogUtils.LOGW(TAG, "Debug URL override active: " + overrideUrl);
            return overrideUrl;
        } catch (IOException e) {
            return "http://storage.googleapis.com/io2017-festivus/manifest_v1.json";
        }
    }

    private String fetchFile(String url) throws IOException {
        if (!url.contains("://")) {
            if (TextUtils.isEmpty(this.mManifestUrl) || !this.mManifestUrl.contains("/")) {
                LogUtils.LOGE(TAG, "Could not build relative URL based on manifest URL.");
                return null;
            }
            int i = this.mManifestUrl.lastIndexOf(47);
            url = this.mManifestUrl.substring(0, i) + "/" + url;
        }
        LogUtils.LOGD(TAG, "Attempting to fetch: " + sanitizeUrl(url));
        try {
            String body = loadFromCache(url);
            if (!TextUtils.isEmpty(body)) {
                this.mBytesReadFromCache += body.getBytes().length;
                this.mCacheFilesToKeep.add(getCacheKey(url));
                return body;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            LogUtils.LOGE(TAG, "IOException getting file from cache.");
        }
        BasicHttpClient client = new BasicHttpClient();
        IOUtils.authorizeHttpClient(this.mContext, client);
        client.setRequestLogger(this.mQuietLogger);
        LogUtils.LOGD(TAG, "Cache miss. Downloading from network: " + sanitizeUrl(url));
        HttpResponse response = client.get(url, null);
        if (response == null) {
            throw new IOException("Request for URL " + sanitizeUrl(url) + " returned null response.");
        }
        LogUtils.LOGD(TAG, "HTTP response " + response.getStatus());
        if (response.getStatus() == 200) {
            String body2 = response.getBodyAsString();
            if (TextUtils.isEmpty(body2)) {
                throw new IOException("Got empty response when attempting to fetch " + sanitizeUrl(url) + url);
            }
            LogUtils.LOGD(TAG, "Successfully downloaded from network: " + sanitizeUrl(url));
            this.mBytesDownloaded += body2.getBytes().length;
            writeToCache(url, body2);
            this.mCacheFilesToKeep.add(getCacheKey(url));
            return body2;
        }
        LogUtils.LOGE(TAG, "Failed to fetch from network: " + sanitizeUrl(url));
        throw new IOException("Request for URL " + sanitizeUrl(url) + " failed with HTTP error " + response.getStatus());
    }

    private File getCacheFile(String url) {
        String cacheKey = getCacheKey(url);
        return new File(this.mContext.getCacheDir() + File.separator + CACHE_DIR + File.separator + cacheKey);
    }

    private void createCacheDir() throws IOException {
        File dir = new File(this.mContext.getCacheDir() + File.separator + CACHE_DIR);
        if (!dir.exists() && !dir.mkdir()) {
            throw new IOException("Failed to mkdir: " + dir);
        }
    }

    private String loadFromCache(String url) throws IOException {
        String cacheKey = getCacheKey(url);
        File cacheFile = getCacheFile(url);
        if (cacheFile.exists()) {
            LogUtils.LOGD(TAG, "Cache hit " + cacheKey + " for " + sanitizeUrl(url));
            return IOUtils.readFileAsString(cacheFile);
        }
        LogUtils.LOGD(TAG, "Cache miss " + cacheKey + " for " + sanitizeUrl(url));
        return null;
    }

    private void writeToCache(String url, String body) throws IOException {
        String cacheKey = getCacheKey(url);
        File cacheFile = getCacheFile(url);
        createCacheDir();
        IOUtils.writeToFile(body, cacheFile);
        LogUtils.LOGD(TAG, "Wrote to cache " + cacheKey + " --> " + sanitizeUrl(url));
    }

    private String getCacheKey(String url) {
        return HashUtils.computeWeakHash(url.trim()) + String.format("%04x", Integer.valueOf(url.length()));
    }

    private String sanitizeUrl(String url) {
        int i = url.lastIndexOf(47);
        return (i < 0 || i >= url.length()) ? url.replaceAll("[A-za-z]", "*") : url.substring(0, i).replaceAll("[A-za-z]", "*") + url.substring(i);
    }

    private String[] processManifest(String manifestJson) throws IOException {
        LogUtils.LOGD(TAG, "Processing data manifest, length " + manifestJson.length());
        DataManifest manifest = (DataManifest) new Gson().fromJson(manifestJson, DataManifest.class);
        if (manifest.format == null || !manifest.format.equals("iosched-json-v1")) {
            LogUtils.LOGE(TAG, "Manifest has invalid format spec: " + manifest.format);
            throw new IOException("Invalid format spec on manifest:" + manifest.format);
        }
        if (manifest.data_files == null || manifest.data_files.length == 0) {
            LogUtils.LOGW(TAG, "Manifest does not list any files. Nothing done.");
            return null;
        }
        LogUtils.LOGD(TAG, "Manifest lists " + manifest.data_files.length + " data files.");
        String[] jsons = new String[manifest.data_files.length];
        for (int i = 0; i < manifest.data_files.length; i++) {
            String url = manifest.data_files[i];
            LogUtils.LOGD(TAG, "Processing data file: " + sanitizeUrl(url));
            jsons[i] = fetchFile(url);
            if (TextUtils.isEmpty(jsons[i])) {
                LogUtils.LOGE(TAG, "Failed to fetch data file: " + sanitizeUrl(url));
                throw new IOException("Failed to fetch data file " + sanitizeUrl(url));
            }
        }
        LogUtils.LOGD(TAG, "Got " + jsons.length + " data files.");
        cleanUpCache();
        return jsons;
    }

    private void cleanUpCache() {
        LogUtils.LOGD(TAG, "Starting cache cleanup, " + this.mCacheFilesToKeep.size() + " URLs to keep.");
        File dir = new File(this.mContext.getCacheDir() + File.separator + CACHE_DIR);
        if (!dir.exists()) {
            LogUtils.LOGD(TAG, "Cleanup complete (there is no cache).");
            return;
        }
        int deleted = 0;
        int kept = 0;
        for (File file : dir.listFiles()) {
            if (this.mCacheFilesToKeep.contains(file.getName())) {
                LogUtils.LOGD(TAG, "Cache cleanup: KEEEPING " + file.getName());
                kept++;
            } else {
                LogUtils.LOGD(TAG, "Cache cleanup: DELETING " + file.getName());
                file.delete();
                deleted++;
            }
        }
        LogUtils.LOGD(TAG, "End of cache cleanup. " + kept + " files kept, " + deleted + " deleted.");
    }

    public long getTotalBytesDownloaded() {
        return this.mBytesDownloaded;
    }

    public long getTotalBytesReadFromCache() {
        return this.mBytesReadFromCache;
    }

    private String getLastModified(HttpResponse resp) {
        if (!resp.getHeaders().containsKey("Last-Modified")) {
            return "";
        }
        List<String> s = resp.getHeaders().get("Last-Modified");
        return s.isEmpty() ? "" : s.get(0);
    }
}
