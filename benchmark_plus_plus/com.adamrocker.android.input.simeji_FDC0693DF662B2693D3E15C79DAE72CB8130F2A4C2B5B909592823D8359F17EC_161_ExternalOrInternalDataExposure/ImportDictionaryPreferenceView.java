package com.adamrocker.android.input.simeji.pref;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.adamrocker.android.input.simeji.P;
import com.adamrocker.android.input.simeji.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import jp.co.omronsoft.openwnn.OpenWnnEN;
import jp.co.omronsoft.openwnn.OpenWnnEvent;
import jp.co.omronsoft.openwnn.OpenWnnJAJP;
import jp.co.omronsoft.openwnn.WnnWord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* loaded from: classes.dex */
public class ImportDictionaryPreferenceView extends PreferenceView {
    private AlertDialog mDialog;
    private CharSequence[] mEntries;
    private int mIndex;
    private volatile boolean mKill;
    private ProgressDialog mProgress;
    private CharSequence[] mValues;

    public ImportDictionaryPreferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIndex = 0;
        this.mKill = false;
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.simeji_preference_list, this);
        ImageView iv = (ImageView) getChildAt(getChildCount() - 1);
        iv.setImageResource(android.R.drawable.ic_menu_agenda);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ListPreferenceView);
        this.mEntries = ta.getTextArray(0);
        this.mValues = ta.getTextArray(1);
        ta.recycle();
    }

    @Override // com.adamrocker.android.input.simeji.pref.PreferenceView, android.view.View.OnClickListener
    public void onClick(View v) {
        super.onClick(v);
        showList(this.mContext);
        this.mTitleView.setPadding(8, 0, 0, 0);
        this.mSummaryView.setPadding(8, 0, 0, 0);
    }

    @Override // com.adamrocker.android.input.simeji.pref.PreferenceView, android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    private void showList(Context context) {
        if (this.mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.icon);
            builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
            builder.setItems(this.mEntries, new DialogInterface.OnClickListener() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    String dic = (String) ImportDictionaryPreferenceView.this.mValues[position];
                    switch (position) {
                        case 0:
                            ImportDictionaryPreferenceView.this.startImportSimejiWithProgress(dic);
                            break;
                        case 1:
                            ImportDictionaryPreferenceView.this.startImportNicoWnnWithProgress(dic);
                            break;
                        case 2:
                            ImportDictionaryPreferenceView.this.startImportFlickWnnWithProgress(dic);
                            break;
                    }
                    di.dismiss();
                }
            });
            CharSequence title = getTitle();
            builder.setTitle(title);
            this.mDialog = builder.create();
        }
        this.mDialog.show();
    }

    private void startImportWithProgress(String dicPath, String title, String message, Runnable task) {
        if (OpenWnnJAJP.getInstance() == null) {
            new OpenWnnJAJP(this.mContext);
        }
        if (OpenWnnEN.getInstance() == null) {
            new OpenWnnEN(this.mContext);
        }
        this.mKill = false;
        this.mProgress = ProgressDialog.show(this.mContext, title, message, true, true, new DialogInterface.OnCancelListener() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                ImportDictionaryPreferenceView.this.mKill = true;
            }
        });
        new Thread(task).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImportNicoWnnWithProgress(final String dicPath) {
        Resources res = getResources();
        String title = res.getString(R.string.import_nico_wnn_dictionary_title);
        String message = res.getString(R.string.import_nico_wnn_dictionary_message);
        startImportWithProgress(dicPath, title, message, new Runnable() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ImportDictionaryPreferenceView.this.startImportNicoWnn(dicPath);
                } catch (IOException e) {
                    ImportDictionaryPreferenceView.this.showErrorToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImportNicoWnn(String dicPath) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        String path = sdcard.getPath();
        LinkedList<WnnWord> list = new LinkedList<>();
        File fileJAJP = new File(String.valueOf(path) + dicPath + "JAJP.xml");
        File fileEN = new File(String.valueOf(path) + dicPath + "EN.xml");
        boolean flag = getNicoWnnList(fileJAJP, list);
        if (flag && addDictionariesJAJP(list)) {
            int total = list.size();
            list.clear();
            boolean flag2 = getNicoWnnList(fileEN, list);
            if (flag2 && addDictionariesEN(list)) {
                int total2 = total + list.size();
                list.clear();
                showFinishToast(String.valueOf(total2));
            }
        }
    }

    private boolean getNicoWnnList(File file, LinkedList<WnnWord> list) {
        try {
            FileReader fr = new FileReader(file);
            XmlPullParser xpp = null;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                xpp = factory.newPullParser();
            } catch (XmlPullParserException e) {
                showErrorToast(String.valueOf(e.getMessage()) + ":" + file.getAbsolutePath());
            }
            try {
                try {
                    xpp.setInput(fr);
                    String key = null;
                    for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
                        if (eventType != 0 && eventType != 1) {
                            if (eventType == 2) {
                                key = xpp.getAttributeValue(null, "stroke");
                            } else if (eventType != 3 && eventType == 4) {
                                String value = xpp.getText();
                                if (value != null && 1 < value.length()) {
                                    try {
                                        if (value.startsWith("\"") && value.endsWith("\"")) {
                                            String value0 = value.substring(1, value.length() - 1);
                                            if (key != null && key.length() > 0 && value0.length() > 0) {
                                                WnnWord word = new WnnWord();
                                                word.stroke = key;
                                                word.candidate = value0;
                                                list.add(word);
                                            }
                                        }
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                                key = null;
                            }
                        }
                    }
                    return true;
                } finally {
                    try {
                        fr.close();
                    } catch (Exception e3) {
                    }
                }
            } catch (IOException e4) {
                showErrorToast(String.valueOf(e4.getMessage()) + ":Couldn't read XML file.");
                try {
                    fr.close();
                } catch (Exception e5) {
                }
                return false;
            } catch (XmlPullParserException e6) {
                showErrorToast(String.valueOf(e6.getMessage()) + ":XML format error.");
                try {
                    fr.close();
                } catch (Exception e7) {
                }
                return false;
            }
        } catch (FileNotFoundException e8) {
            showErrorToast(String.valueOf(e8.getMessage()) + ":" + file.getAbsolutePath());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImportFlickWnnWithProgress(final String dicPath) {
        Resources res = getResources();
        String title = res.getString(R.string.import_flick_wnn_dictionary_title);
        String message = res.getString(R.string.import_flick_wnn_dictionary_message);
        startImportWithProgress(dicPath, title, message, new Runnable() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ImportDictionaryPreferenceView.this.startImportFlickWnn(dicPath);
                } catch (IOException e) {
                    ImportDictionaryPreferenceView.this.showErrorToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImportFlickWnn(String dicPath) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        String path = sdcard.getPath();
        File file = new File(String.valueOf(path) + dicPath);
        try {
            FileReader in = new FileReader(file);
            BufferedReader br = new BufferedReader(in);
            LinkedList<WnnWord> list = new LinkedList<>();
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                int index = line.indexOf(" ");
                try {
                    String key = line.substring(0, index);
                    String value = line.substring(index + 1);
                    if (key.length() > 0 && value.length() > 0) {
                        WnnWord word = new WnnWord();
                        word.stroke = key;
                        word.candidate = value;
                        list.add(word);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            br.close();
            in.close();
            if (!this.mKill) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    if (!this.mKill) {
                        WnnWord word2 = list.get(i);
                        OpenWnnEvent event = new OpenWnnEvent(OpenWnnEvent.ADD_WORD, 2, word2);
                        char head = word2.stroke.charAt(0);
                        if (isAlphabet(head)) {
                            if (OpenWnnEN.getInstance() == null) {
                                new OpenWnnEN(this.mContext);
                            }
                            OpenWnnEN.getInstance().onEvent(event);
                        } else {
                            if (OpenWnnJAJP.getInstance() == null) {
                                new OpenWnnJAJP(this.mContext);
                            }
                            OpenWnnJAJP.getInstance().onEvent(event);
                        }
                    } else {
                        return;
                    }
                }
                showFinishToast(String.valueOf(size));
            }
        } catch (FileNotFoundException e2) {
            showErrorToast(String.valueOf(e2.getMessage()) + ":" + dicPath);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImportSimejiWithProgress(final String dicPath) {
        Resources res = getResources();
        String title = res.getString(R.string.import_simeji_dictionary_title);
        String message = res.getString(R.string.import_simeji_dictionary_message);
        startImportWithProgress(dicPath, title, message, new Runnable() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ImportDictionaryPreferenceView.this.startImportSimeji(dicPath);
                } catch (Exception e) {
                    ImportDictionaryPreferenceView.this.showErrorToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImportSimeji(String dicPath) {
        JSONException e;
        int total = 0;
        File sdcard = Environment.getExternalStorageDirectory();
        String path = sdcard.getPath();
        File file = new File(String.valueOf(path) + dicPath);
        if (file.isFile()) {
            String str = null;
            try {
                str = getFileString(file);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                JSONObject json = new JSONObject(str);
                try {
                    JSONArray jajpKey = json.getJSONArray("JAJP_KEY");
                    JSONArray jajpValue = json.getJSONArray("JAJP_VALUE");
                    LinkedList<WnnWord> list = new LinkedList<>();
                    int len = jajpKey.length();
                    for (int i = 0; i < len; i++) {
                        try {
                            String key = jajpKey.getString(i);
                            String value = jajpValue.getString(i);
                            if (key.length() > 0 && value.length() > 0) {
                                WnnWord word = new WnnWord();
                                word.stroke = key;
                                word.candidate = value;
                                list.add(word);
                            }
                        } catch (JSONException e3) {
                        }
                    }
                    list.size();
                    if (addDictionariesJAJP(list)) {
                        int total2 = 0 + list.size();
                        list.clear();
                        try {
                            JSONArray enKey = json.getJSONArray("EN_KEY");
                            JSONArray enValue = json.getJSONArray("EN_VALUE");
                            int len2 = enKey.length();
                            for (int i2 = 0; i2 < len2; i2++) {
                                try {
                                    String key2 = enKey.getString(i2);
                                    String value2 = enValue.getString(i2);
                                    if (key2.length() > 0 && value2.length() > 0) {
                                        WnnWord word2 = new WnnWord();
                                        word2.stroke = key2;
                                        word2.candidate = value2;
                                        list.add(word2);
                                    }
                                } catch (JSONException e4) {
                                }
                            }
                            list.size();
                            if (addDictionariesEN(list)) {
                                total = total2 + list.size();
                                list.clear();
                            } else {
                                return;
                            }
                        } catch (JSONException e5) {
                            showErrorToast(String.valueOf(e5.getMessage()) + ":" + path + P.SIMEJI_DIC);
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (JSONException e6) {
                    e = e6;
                    showErrorToast(String.valueOf(e.getMessage()) + ":" + path + P.SIMEJI_DIC);
                    return;
                }
            } catch (JSONException e7) {
                e = e7;
            }
        }
        showFinishToast(String.valueOf(total));
    }

    private boolean addDictionariesJAJP(LinkedList<WnnWord> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (this.mKill) {
                return false;
            }
            WnnWord word = list.get(i);
            addDictionaryJAJP(word);
        }
        return true;
    }

    private void addDictionaryJAJP(WnnWord word) {
        OpenWnnEvent event = new OpenWnnEvent(OpenWnnEvent.ADD_WORD, 2, word);
        OpenWnnJAJP.getInstance().onEvent(event);
    }

    private boolean addDictionariesEN(LinkedList<WnnWord> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (this.mKill) {
                return false;
            }
            WnnWord word = list.get(i);
            addDictionaryEN(word);
        }
        return true;
    }

    private void addDictionaryEN(WnnWord word) {
        OpenWnnEvent event = new OpenWnnEvent(OpenWnnEvent.ADD_WORD, 2, word);
        OpenWnnEN.getInstance().onEvent(event);
    }

    private String getFileString(File file) throws IOException {
        FileReader in = new FileReader(file);
        BufferedReader br = new BufferedReader(in);
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line != null) {
                sb.append(line);
            } else {
                br.close();
                in.close();
                return sb.toString();
            }
        }
    }

    private void showFinishToast(final String msg) {
        post(new Runnable() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.6
            @Override // java.lang.Runnable
            public void run() {
                if (ImportDictionaryPreferenceView.this.mProgress != null && ImportDictionaryPreferenceView.this.mProgress.isShowing()) {
                    ImportDictionaryPreferenceView.this.mProgress.dismiss();
                }
                String text = ImportDictionaryPreferenceView.this.getResources().getString(R.string.import_dictionary_finished);
                Toast.makeText(ImportDictionaryPreferenceView.this.mContext, String.valueOf(text) + "(" + msg + ")", 0).show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorToast(final String msg) {
        post(new Runnable() { // from class: com.adamrocker.android.input.simeji.pref.ImportDictionaryPreferenceView.7
            @Override // java.lang.Runnable
            public void run() {
                if (ImportDictionaryPreferenceView.this.mProgress != null && ImportDictionaryPreferenceView.this.mProgress.isShowing()) {
                    ImportDictionaryPreferenceView.this.mProgress.dismiss();
                }
                String text = ImportDictionaryPreferenceView.this.getResources().getString(R.string.import_dictionary_error);
                Toast.makeText(ImportDictionaryPreferenceView.this.mContext, String.valueOf(text) + ":" + msg, 0).show();
            }
        });
    }

    protected static final boolean isAlphabet(int ch) {
        return isLowerCase(ch) || isUpperCase(ch);
    }

    protected static final boolean isLowerCase(int ch) {
        return 97 <= ch && ch <= 122;
    }

    protected static final boolean isUpperCase(int ch) {
        return 65 <= ch && ch <= 90;
    }

    @Override // com.adamrocker.android.input.simeji.pref.PreferenceInterface
    public void save(SharedPreferences.Editor edit) {
    }

    @Override // com.adamrocker.android.input.simeji.pref.PreferenceInterface
    public void load(SharedPreferences sp) {
    }
}
