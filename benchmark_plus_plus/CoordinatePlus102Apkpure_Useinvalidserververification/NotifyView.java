package jp.co.toshiba.vft.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.co.toshiba.vft.R;
import jp.co.toshiba.vft.data.NotifyData;
import jp.co.toshiba.vft.data.NotifyDataType;

/* loaded from: classes.dex */
public class NotifyView extends LinearLayout {
    private static final String TAG = "NotifyView";
    private Button mOK;
    private TextView mTitle;
    private WebView mWeb;
    private InnerWebClientView mWebViewClient;

    public NotifyView(Context context) {
        super(context);
    }

    public NotifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitle = (TextView) findViewById(R.id.notifyTitle);
        this.mOK = (Button) findViewById(R.id.notifyOK);
        this.mWeb = (WebView) findViewById(R.id.notifyContent);
        this.mWebViewClient = new InnerWebClientView();
        this.mWeb.setWebViewClient(this.mWebViewClient);
    }

    public void setTitle(String title) {
        this.mTitle.setText(title);
    }

    public void setContent(NotifyData notifyData) {
        if (notifyData == null) {
            this.mWeb.loadDataWithBaseURL(null, null, "text/html", "utf-8", null);
            return;
        }
        this.mWebViewClient.baseUrl = notifyData.getBaseUrl();
        if (notifyData.isLocalData()) {
            this.mWeb.loadUrl(notifyData.getUrl());
        } else if (notifyData.getType() == NotifyDataType.Consent) {
            this.mWeb.loadDataWithBaseURL(notifyData.getBaseUrl(), notifyData.getData(), "text/html", "utf-8", null);
        } else if (notifyData.getType() == NotifyDataType.Notify) {
            this.mWeb.loadUrl(notifyData.getUrl());
        }
    }

    public void setOKClickListener(View.OnClickListener listener) {
        this.mOK.setOnClickListener(listener);
    }

    /* loaded from: classes.dex */
    class InnerWebClientView extends WebViewClient {
        private static final String TAG = "InnerWebClientView";
        public String baseUrl;

        public InnerWebClientView() {
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url == null || this.baseUrl == null || url.startsWith(this.baseUrl)) {
                return false;
            }
            view.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            return true;
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "[onReceivedError] errorCode=" + errorCode + " description=" + description + " url=" + failingUrl);
        }
    }
}
