package com.brakefield.infinitestudio.account;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import com.brakefield.infinitestudio.utils.HttpUtil;
import java.util.ArrayList;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class UserFunctions {
    private static String edit_tag = "edit";
    private static String loginURL = "http://www.seanbrakefield.com/users/index.php";
    private static String login_tag = "login";
    private static String registerURL = "http://www.seanbrakefield.com/users/index.php";
    private static String register_tag = "register";
    private static String resetURL = "http://www.seanbrakefield.com/users/reset.php";
    private JSONParser jsonParser = new JSONParser();

    public JSONObject loginUser(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new String[]{"tag", login_tag});
        arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, str});
        arrayList.add(new String[]{"password", str2});
        return this.jsonParser.getJSONFromUrl(loginURL, arrayList);
    }

    public JSONObject registerUser(String str, String str2, String str3) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new String[]{"tag", register_tag});
        arrayList.add(new String[]{"name", str});
        arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, str2});
        arrayList.add(new String[]{"password", str3});
        return this.jsonParser.getJSONFromUrl(registerURL, arrayList);
    }

    public JSONObject resetPassword(String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, str});
        return this.jsonParser.getJSONFromUrl(resetURL, arrayList);
    }

    public JSONObject updateUserInfo(String str, String str2, String... strArr) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new String[]{"tag", edit_tag});
        arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, str});
        arrayList.add(new String[]{"password", str2});
        for (int i = 0; i < strArr.length; i += 2) {
            int i2 = i + 1;
            if (i2 < strArr.length) {
                arrayList.add(new String[]{strArr[i], strArr[i2]});
            }
        }
        return this.jsonParser.getJSONFromUrl(loginURL, arrayList);
    }

    public boolean isUserLoggedIn(Context context) {
        return new AccountInfo(context).isUserLoggedIn();
    }

    public boolean logoutUser(Context context) {
        new AccountInfo(context).logout();
        return true;
    }

    /* loaded from: classes.dex */
    public static class ReportImageTask extends AsyncTask<String, Void, Void> {
        AccountInfo info;

        public ReportImageTask(AccountInfo accountInfo) {
            this.info = accountInfo;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            ArrayList arrayList = new ArrayList(3);
            arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, this.info.getEmail()});
            arrayList.add(new String[]{"password", this.info.getPassword()});
            arrayList.add(new String[]{"image", strArr[0]});
            HttpUtil.sendPostsToHTTP("http://www.seanbrakefield.com/users/report.php", arrayList);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((ReportImageTask) r1);
        }
    }

    /* loaded from: classes.dex */
    public static class UnreportImageTask extends AsyncTask<String, Void, Void> {
        AccountInfo info;

        public UnreportImageTask(AccountInfo accountInfo) {
            this.info = accountInfo;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            ArrayList arrayList = new ArrayList(3);
            arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, this.info.getEmail()});
            arrayList.add(new String[]{"password", this.info.getPassword()});
            arrayList.add(new String[]{"image", strArr[0]});
            HttpUtil.sendPostsToHTTP("http://www.seanbrakefield.com/users/unreport.php", arrayList);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((UnreportImageTask) r1);
        }
    }

    /* loaded from: classes.dex */
    public static class AdminDeleteResourceTask extends AsyncTask<String, Void, Void> {
        AccountInfo info;
        String type;

        public AdminDeleteResourceTask(AccountInfo accountInfo, String str) {
            this.info = accountInfo;
            this.type = str;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            ArrayList arrayList = new ArrayList(4);
            arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, this.info.getEmail()});
            arrayList.add(new String[]{"password", this.info.getPassword()});
            arrayList.add(new String[]{"image", strArr[0]});
            arrayList.add(new String[]{"type", this.type});
            HttpUtil.sendPostsToHTTP("http://www.seanbrakefield.com/users/delete_admin.php", arrayList);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((AdminDeleteResourceTask) r1);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteResourceTask extends AsyncTask<String, Void, Void> {
        AccountInfo info;
        String type;

        public DeleteResourceTask(AccountInfo accountInfo, String str) {
            this.info = accountInfo;
            this.type = str;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            ArrayList arrayList = new ArrayList(4);
            arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, this.info.getEmail()});
            arrayList.add(new String[]{"password", this.info.getPassword()});
            arrayList.add(new String[]{"image", strArr[0]});
            arrayList.add(new String[]{"type", this.type});
            HttpUtil.sendPostsToHTTP("http://www.seanbrakefield.com/users/delete.php", arrayList);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((DeleteResourceTask) r1);
        }
    }

    /* loaded from: classes.dex */
    public static class LoveImageTask extends AsyncTask<String, Void, Void> {
        AccountInfo info;

        public LoveImageTask(AccountInfo accountInfo) {
            this.info = accountInfo;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            ArrayList arrayList = new ArrayList(3);
            arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, this.info.getEmail()});
            arrayList.add(new String[]{"password", this.info.getPassword()});
            arrayList.add(new String[]{"image", strArr[0]});
            HttpUtil.sendPostsToHTTP("http://www.seanbrakefield.com/users/love.php", arrayList);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((LoveImageTask) r1);
        }
    }

    /* loaded from: classes.dex */
    public static class PromoteImageTask extends AsyncTask<String, Void, Void> {
        AccountInfo info;

        public PromoteImageTask(AccountInfo accountInfo) {
            this.info = accountInfo;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(String... strArr) {
            ArrayList arrayList = new ArrayList(3);
            arrayList.add(new String[]{NotificationCompat.CATEGORY_EMAIL, this.info.getEmail()});
            arrayList.add(new String[]{"password", this.info.getPassword()});
            arrayList.add(new String[]{"image", strArr[0]});
            HttpUtil.sendPostsToHTTP("http://www.seanbrakefield.com/users/promote.php", arrayList);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((PromoteImageTask) r1);
        }
    }
}
