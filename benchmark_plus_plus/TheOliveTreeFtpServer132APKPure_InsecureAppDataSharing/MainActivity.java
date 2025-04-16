package com.theolivetree.ftpserverlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.theolivetree.ftpserverlib.FTPService;
import com.theolivetree.ftpserverlib.Helper;
import com.theolivetree.utilities.CustomResultReceiver;
import com.theolivetree.utilities.Net;
import com.theolivetree.utilities.ServiceServer;
import org.taptwo.android.widget.ViewFlow;

/* loaded from: classes.dex */
public abstract class MainActivity extends AboutActivity implements CustomResultReceiver.Receiver {
    public static final int AUTHORIZE_ACTIVITY = 1;
    public static final int PREFERENCE_ACTIVITY = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private CustomResultReceiver mReceiver;
    protected ViewFlow viewFlow = null;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.theolivetree.ftpserverlib.MainActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            FTPService.FtpBinder serviceBinder = (FTPService.FtpBinder) service;
            MainActivity.this.setViewsStarted(serviceBinder.configurationString);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            MainActivity.this.setViewsStopped();
        }
    };

    protected abstract String getUpdateWidgetAction();

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultValues(false);
        Intent startIntent = getIntent();
        boolean startFromWidgetError = startIntent.getBooleanExtra("startFromWidgetError", false);
        if (startFromWidgetError) {
            Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.notConnect, null, null);
        }
    }

    @Override // com.theolivetree.ftpserverlib.AboutActivity
    public void postOnCreate(Bundle savedInstanceState) {
        super.postOnCreate(savedInstanceState);
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        this.mReceiver = new CustomResultReceiver(new Handler());
        this.mReceiver.setReceiver(this);
        if (!bindService(new Intent(FtpserverApp.getAppContext(), (Class<?>) FTPService.class), this.mConnection, 0) || FTPService.getServer() == null) {
            setViewsStopped();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
            }
        }
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        unbindService(this.mConnection);
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mReceiver.setReceiver(this);
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.mReceiver.setReceiver(null);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(1);
    }

    public void startStopClickHandler(View view) {
        String updateWidgetAction = getUpdateWidgetAction();
        if (FTPService.getServer() == null) {
            startClickHandler(updateWidgetAction);
        } else {
            stopClickHandler(updateWidgetAction);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.viewFlow.onConfigurationChanged(newConfig);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_preference) {
            Intent intent = new Intent(this, (Class<?>) PrefsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 82) {
            return super.onKeyDown(keyCode, event);
        }
        startActivityForResult(new Intent(this, (Class<?>) PrefsActivity.class), 0);
        return true;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                switch (resultCode) {
                    case 2:
                        setDefaultValues(true);
                        break;
                }
            case 1:
                if (PrefsActivity.AuthorizeActivityResult(this, resultCode, data)) {
                    String updateWidgetAction = getUpdateWidgetAction();
                    startClickHandler(updateWidgetAction);
                    break;
                }
                break;
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0) {
                    int i = grantResults[0];
                    break;
                }
                break;
        }
    }

    @Override // com.theolivetree.utilities.CustomResultReceiver.Receiver
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0) {
            Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.errorRunServer, null, null);
        }
    }

    protected void setDefaultValues(boolean readAgain) {
        if (readAgain) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().clear().commit();
        }
        PreferenceManager.setDefaultValues(this, R.xml.preference, readAgain);
    }

    public void startClickHandler(String updateWidgetAction) {
        try {
            Context context = FtpserverApp.getAppContext();
            Helper.TreeUriNeeded treeUri = Helper.IsUriNeeded(context);
            if (treeUri.uriNeeded && treeUri.uri == null) {
                Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                startActivityForResult(intent, 1);
            } else if (Helper.StartService(context, getClass(), this.mReceiver, updateWidgetAction, false)) {
                bindService(new Intent(context, (Class<?>) FTPService.class), this.mConnection, 0);
            } else {
                Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.notConnect, null, null);
            }
        } catch (Exception e) {
            Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.errorRunServer, null, null);
        }
    }

    public void stopClickHandler(String updateWidgetAction) {
        setViewsStopped();
        Intent intent = new Intent(this, (Class<?>) FTPService.class);
        stopService(intent);
        ServiceServer.updateWidgets(getApplicationContext(), updateWidgetAction, false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setViewsStarted(String connectString) {
        ImageView button = (ImageView) findViewById(R.id.imageView1);
        TextView textStartStop = (TextView) findViewById(R.id.textView3);
        TextView text2 = (TextView) findViewById(R.id.textView2);
        text2.setText(connectString);
        button.setImageResource(R.drawable.on);
        button.setContentDescription(getText(R.string.str_stop));
        textStartStop.setText(R.string.str_stop);
        text2.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setViewsStopped() {
        ImageView button = (ImageView) findViewById(R.id.imageView1);
        TextView textStartStop = (TextView) findViewById(R.id.textView3);
        TextView text2 = (TextView) findViewById(R.id.textView2);
        button.setImageResource(R.drawable.off);
        button.setContentDescription(getText(R.string.str_start));
        textStartStop.setText(R.string.str_start);
        text2.setVisibility(4);
    }
}
