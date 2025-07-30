package wni.WeathernewsTouch.jp.Koyo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import twitter4j.GeoQuery;
import wni.WeathernewsTouch.CommonExecutor;
import wni.WeathernewsTouch.Dialogs.AuthenticationError;
import wni.WeathernewsTouch.Dialogs.CommonAlertDialog;
import wni.WeathernewsTouch.Dialogs.NetworkError;
import wni.WeathernewsTouch.GLHelpers;
import wni.WeathernewsTouch.Help.LoginPrefs;
import wni.WeathernewsTouch.MapImageFilterResourceData;
import wni.WeathernewsTouch.Report.DEG2DMS;
import wni.WeathernewsTouch.Report.RepoItem;
import wni.WeathernewsTouch.Report.SendReportSendEffect;
import wni.WeathernewsTouch.jp.All;
import wni.WeathernewsTouch.jp.R;
import wni.WeathernewsTouch.jp.Top.Report.ReportInput.ReportInputActivity;

/* loaded from: classes.dex */
public class KoyoSendReport extends Activity {
    private static final int AUTH_ERROR_ACTIVITY = 1201;
    private static final int COMMENT_ACTIVITY = 1103;
    private static final int IROZUKI_ACTIVITY = 1104;
    private static final int LOCALE_ACTIVITY = 1102;
    private static final int NETWORK_ERROR_ACTIVITY = 1202;
    private static final int PHOTO_CAMERA_ACTIVITY = 1100;
    private static final int PHOTO_PICK_ACTIVITY = 1101;
    private static final int SEND_EFFECT_ACTIVITY = 1106;
    private static final int TAKEPHOTO_ACTIVITY = 1105;
    private String detailaddr;
    public FrameLayout fl;
    KoyoSendReport ref;
    Handler uiHandler;
    private int photoViewW = 0;
    private int photoViewH = 0;
    private final String CAMERA_DIR = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/Android/data/wni.WeathernewsTouch.jp/files";
    private String[] strDate = null;
    private String[] strDateNum = null;
    KoyoReportController REPO = new KoyoReportController();
    public String authKey = null;

    public KoyoSendReport() {
        this.ref = null;
        this.ref = this;
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.uiHandler = new Handler();
        setContentView(R.layout.koyo_send_report);
        this.fl = (FrameLayout) findViewById(R.id.loading);
        this.REPO.lang = getResources().getString(R.string.lang);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        this.authKey = b.getString("auth");
        this.REPO.category = "502";
        if (this.authKey == null || this.authKey.length() == 0) {
            Log.e("WeathernewsTouch", "authID is not registered or something");
            authError("8");
        } else {
            this.REPO.id = b.getString("auth");
            this.uiHandler.post(new Runnable() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.1
                @Override // java.lang.Runnable
                public void run() {
                    KoyoSendReport.this.ref.makePhotoDate();
                    KoyoSendReport.this.ref.loadData();
                }
            });
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    protected void display() {
        ImageView photoView = (ImageView) findViewById(R.id.krepo_photo);
        this.photoViewW = photoView.getWidth();
        this.photoViewH = photoView.getHeight();
        Button photo = (Button) findViewById(R.id.repo_button_add_photo);
        photo.setOnClickListener(new AnonymousClass2());
        LinearLayout locale = (LinearLayout) findViewById(R.id.krepo_button_location);
        locale.setOnClickListener(new View.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent i = new Intent(KoyoSendReport.this, (Class<?>) KoyoInputLocale.class);
                if (KoyoSendReport.this.REPO.prefname != null) {
                    i.putExtra("prefname", KoyoSendReport.this.REPO.prefname);
                }
                if (KoyoSendReport.this.REPO.area != null) {
                    i.putExtra("area", KoyoSendReport.this.REPO.area);
                }
                if (KoyoSendReport.this.REPO.city != null) {
                    i.putExtra(GeoQuery.CITY, KoyoSendReport.this.REPO.city);
                }
                if (KoyoSendReport.this.REPO.city_str != null) {
                    i.putExtra("cityname1", KoyoSendReport.this.REPO.city_str);
                }
                if (KoyoSendReport.this.REPO.city != null && KoyoSendReport.this.REPO.city.equals("00000")) {
                    if (KoyoSendReport.this.REPO.cityname != null) {
                        i.putExtra("cityname2", KoyoSendReport.this.detailaddr);
                    }
                } else if (KoyoSendReport.this.REPO.cityname != null) {
                    i.putExtra("cityname2", KoyoSendReport.this.REPO.cityname);
                }
                if (KoyoSendReport.this.REPO.meisho != null) {
                    i.putExtra("meisho", KoyoSendReport.this.REPO.meisho);
                }
                if (KoyoSendReport.this.REPO.lat != null) {
                    i.putExtra("lat", KoyoSendReport.this.REPO.lat);
                }
                if (KoyoSendReport.this.REPO.lon != null) {
                    i.putExtra("lon", KoyoSendReport.this.REPO.lon);
                }
                KoyoSendReport.this.startActivityForResult(i, KoyoSendReport.LOCALE_ACTIVITY);
            }
        });
        locale.setOnTouchListener(new View.OnTouchListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                TextView city1 = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_location_cityname1);
                TextView city2 = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_location_cityname2);
                TextView notes = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_location_notes);
                TextView lat = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_location_lat);
                TextView lon = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_location_lon);
                ImageView arrow = (ImageView) KoyoSendReport.this.findViewById(R.id.krepo_arrow_location);
                Resources res = KoyoSendReport.this.getResources();
                switch (event.getAction()) {
                    case 0:
                        city1.setTextColor(-1);
                        city2.setTextColor(-1);
                        notes.setTextColor(-1);
                        lat.setTextColor(-1);
                        lon.setTextColor(-1);
                        arrow.setImageResource(R.drawable.angleright_white);
                        return false;
                    default:
                        String city = KoyoSendReport.this.REPO.city;
                        if (city == null) {
                            city = "";
                        }
                        if (city.length() == 0) {
                            city1.setTextColor(res.getColor(R.color.srepo_default));
                            city2.setTextColor(res.getColor(R.color.srepo_default));
                            notes.setTextColor(res.getColor(R.color.srepo_default));
                            lat.setTextColor(res.getColor(R.color.srepo_default));
                            lon.setTextColor(res.getColor(R.color.srepo_default));
                        } else {
                            city1.setTextColor(-16777216);
                            city2.setTextColor(-16777216);
                            notes.setTextColor(-16777216);
                            lat.setTextColor(-16777216);
                            lon.setTextColor(-16777216);
                        }
                        arrow.setImageResource(R.drawable.angleright_gray);
                        return false;
                }
            }
        });
        LinearLayout comment = (LinearLayout) findViewById(R.id.krepo_button_comment);
        comment.setOnClickListener(new View.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent i = new Intent(KoyoSendReport.this, (Class<?>) KoyoInputComment.class);
                if (KoyoSendReport.this.REPO.title != null) {
                    i.putExtra("title", KoyoSendReport.this.REPO.title);
                }
                if (KoyoSendReport.this.REPO.comment != null) {
                    i.putExtra("comment", KoyoSendReport.this.REPO.comment);
                }
                i.putExtra(ReportInputActivity.EXTRATAG_STRING_CATEGORY, KoyoSendReport.this.REPO.category);
                Log.e("gotocommetn", "REPO.category:" + KoyoSendReport.this.REPO.category);
                KoyoSendReport.this.startActivityForResult(i, KoyoSendReport.COMMENT_ACTIVITY);
            }
        });
        comment.setOnTouchListener(new View.OnTouchListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                TextView title = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_val_title);
                TextView comment2 = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_val_comment);
                ImageView arrow = (ImageView) KoyoSendReport.this.findViewById(R.id.krepo_arrow_comment);
                Resources res = KoyoSendReport.this.getResources();
                switch (event.getAction()) {
                    case 0:
                        title.setTextColor(-1);
                        comment2.setTextColor(-1);
                        arrow.setImageResource(R.drawable.angleright_white);
                        return false;
                    default:
                        String cmt = KoyoSendReport.this.REPO.comment;
                        if (cmt == null) {
                            cmt = "";
                        }
                        if (cmt.length() == 0) {
                            title.setTextColor(res.getColor(R.color.srepo_default));
                            comment2.setTextColor(res.getColor(R.color.srepo_default));
                        } else {
                            title.setTextColor(res.getColor(R.color.comment_title_color));
                            comment2.setTextColor(-16777216);
                        }
                        arrow.setImageResource(R.drawable.angleright_gray);
                        return false;
                }
            }
        });
        LinearLayout coloring = (LinearLayout) findViewById(R.id.krepo_btn_coloring);
        coloring.setOnClickListener(new View.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                String slctCode = KoyoSendReport.this.REPO.coloring;
                Intent i = new Intent(KoyoSendReport.this, (Class<?>) KoyoInputColoring.class);
                if (slctCode != null) {
                    i.putExtra("code", slctCode);
                }
                i.putExtra("itemType", "coloring");
                i.putExtra("item", KoyoSendReport.this.REPO.getLoadedData("coloring_list"));
                KoyoSendReport.this.startActivityForResult(i, KoyoSendReport.IROZUKI_ACTIVITY);
            }
        });
        coloring.setOnTouchListener(new View.OnTouchListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.8
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                TextView item = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_item_coloring);
                TextView val = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_val_coloring);
                ImageView arrow = (ImageView) KoyoSendReport.this.findViewById(R.id.krepo_arrow_coloring);
                Resources res = KoyoSendReport.this.getResources();
                switch (event.getAction()) {
                    case 0:
                        item.setTextColor(-1);
                        val.setTextColor(-1);
                        arrow.setImageResource(R.drawable.angleright_white);
                        return false;
                    default:
                        int Col = res.getColor(R.color.srepo_default);
                        if (KoyoSendReport.this.REPO.isValid(KoyoSendReport.this.REPO.coloring)) {
                            Col = -16777216;
                        }
                        item.setTextColor(res.getColor(R.color.srepo_item));
                        val.setTextColor(Col);
                        arrow.setImageResource(R.drawable.angleright_gray);
                        return false;
                }
            }
        });
        LinearLayout repodate = (LinearLayout) findViewById(R.id.krepo_btn_date);
        repodate.setOnClickListener(new View.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                KoyoSendReport.this.showSelectSoratomoDialog();
            }
        });
        repodate.setOnTouchListener(new View.OnTouchListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.10
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                TextView item = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_item_date);
                TextView val = (TextView) KoyoSendReport.this.findViewById(R.id.krepo_val_date);
                ImageView arrow = (ImageView) KoyoSendReport.this.findViewById(R.id.krepo_arrow_date);
                Resources res = KoyoSendReport.this.getResources();
                switch (event.getAction()) {
                    case 0:
                        item.setTextColor(-1);
                        val.setTextColor(-1);
                        arrow.setImageResource(R.drawable.angleright_white);
                        return false;
                    default:
                        int Col = res.getColor(R.color.srepo_default);
                        if (KoyoSendReport.this.REPO.isValid(KoyoSendReport.this.REPO.date_takephoto)) {
                            Col = -16777216;
                        }
                        item.setTextColor(res.getColor(R.color.srepo_item));
                        val.setTextColor(Col);
                        arrow.setImageResource(R.drawable.angleright_gray);
                        return false;
                }
            }
        });
        FrameLayout send = (FrameLayout) findViewById(R.id.krepo_send);
        send.setOnClickListener(new View.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent i = new Intent(KoyoSendReport.this, (Class<?>) SendReportSendEffect.class);
                i.putExtra("repo_k", KoyoSendReport.this.REPO);
                KoyoSendReport.this.startActivityForResult(i, KoyoSendReport.SEND_EFFECT_ACTIVITY);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport$2, reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Resources res = KoyoSendReport.this.getResources();
            String dialog1 = res.getString(R.string.srepo_select_photo_dialog_1);
            String dialog2 = res.getString(R.string.srepo_select_photo_dialog_2);
            String dialog3 = res.getString(R.string.srepo_select_photo_dialog_3);
            int size = KoyoSendReport.this.REPO.isValid(KoyoSendReport.this.REPO.imagefilename) ? 3 : 2;
            CharSequence[] dialogItems = new CharSequence[size];
            dialogItems[0] = dialog1;
            dialogItems[1] = dialog2;
            if (size == 3) {
                dialogItems[2] = dialog3;
            }
            final String cancel = KoyoSendReport.this.getResources().getString(R.string.srepo_cancel);
            new AlertDialog.Builder(KoyoSendReport.this).setItems(dialogItems, new DialogInterface.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.2.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        File chdir = new File(KoyoSendReport.this.CAMERA_DIR);
                        if (!chdir.isDirectory()) {
                            try {
                                chdir.mkdirs();
                            } catch (SecurityException e) {
                            }
                        }
                        String filename = KoyoSendReport.this.getResources().getString(R.string.krepo_photo_filename);
                        String camFile = String.valueOf(KoyoSendReport.this.CAMERA_DIR) + "/" + filename;
                        Intent cam = new Intent("android.media.action.IMAGE_CAPTURE");
                        cam.putExtra("output", Uri.fromFile(new File(camFile)));
                        KoyoSendReport.this.startActivityForResult(cam, KoyoSendReport.PHOTO_CAMERA_ACTIVITY);
                        return;
                    }
                    if (item == 1) {
                        Intent pick = new Intent("android.intent.action.PICK");
                        pick.setType("image/*");
                        KoyoSendReport.this.startActivityForResult(pick, KoyoSendReport.PHOTO_PICK_ACTIVITY);
                    } else if (item == 2) {
                        CharSequence[] delItem = KoyoSendReport.this.getResources().getStringArray(R.array.repo_del_photo_dialog);
                        new AlertDialog.Builder(KoyoSendReport.this).setItems(delItem, new DialogInterface.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.2.1.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface deldialog, int item2) {
                                if (item2 == 0) {
                                    KoyoSendReport.this.REPO.imagefilename = "";
                                    FrameLayout slctPhotoTxt = (FrameLayout) KoyoSendReport.this.findViewById(R.id.krepo_select_photo);
                                    slctPhotoTxt.setVisibility(4);
                                    ImageView photo = (ImageView) KoyoSendReport.this.findViewById(R.id.krepo_photo);
                                    photo.setVisibility(4);
                                }
                            }
                        }).setNegativeButton(cancel, new DialogInterface.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.2.1.2
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface deldialog, int id) {
                                deldialog.cancel();
                            }
                        }).show();
                    }
                }
            }).setNegativeButton(cancel, new DialogInterface.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.2.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).show();
        }
    }

    private void setRepoPhoto(String filename) {
        Log.e("filename", filename);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        ImageView photoView = (ImageView) findViewById(R.id.krepo_photo);
        int scaleW = options.outWidth / this.photoViewW;
        int scaleH = options.outHeight / this.photoViewH;
        int scale = Math.max(scaleW, scaleH);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        Bitmap image = BitmapFactory.decodeFile(filename, options);
        photoView.setImageBitmap(image);
        photoView.setVisibility(0);
        FrameLayout slctPhoto = (FrameLayout) findViewById(R.id.krepo_select_photo);
        slctPhoto.setVisibility(0);
        if (this.REPO.checkInputItem()) {
            FrameLayout send = (FrameLayout) findViewById(R.id.krepo_send);
            send.setVisibility(0);
        }
    }

    public String rotateImage(String filename) {
        boolean savedSmallSize = false;
        String fname = filename.toLowerCase();
        if (fname.indexOf(".jpg") < 0 && fname.indexOf(".jpeg") < 0) {
            return filename;
        }
        try {
            Class<?> trans = Class.forName("android.media.ExifInterface");
            Constructor<?> constructor = trans.getConstructor(String.class);
            Object obj = constructor.newInstance(filename);
            Field f = trans.getField("TAG_ORIENTATION");
            Method m = trans.getMethod("getAttribute", String.class);
            Object oriField = f.get(null);
            String orientation = (String) m.invoke(obj, oriField);
            Field tateF = trans.getField("ORIENTATION_ROTATE_90");
            Object tate = tateF.get(null);
            Field yokoF = trans.getField("ORIENTATION_ROTATE_180");
            Object yoko = yokoF.get(null);
            Log.e("orientation", "orientation:" + orientation);
            savedSmallSize = true;
            if (orientation.equals(yoko.toString())) {
                filename = rotate(filename, 180);
            } else if (orientation.equals(tate.toString())) {
                filename = rotate(filename, 90);
            } else {
                filename = rotate(filename, 0);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (InstantiationException e4) {
            e4.printStackTrace();
        } catch (NoSuchFieldException e5) {
            e5.printStackTrace();
        } catch (NoSuchMethodException e6) {
            e6.printStackTrace();
        } catch (SecurityException e7) {
            e7.printStackTrace();
        } catch (InvocationTargetException e8) {
            e8.printStackTrace();
        }
        if (!savedSmallSize) {
            filename = rotate(filename, 0);
        }
        return filename;
    }

    public String rotate(String filename, int angle) {
        try {
            String sdFilename = getResources().getString(R.string.krepo_photo_filename);
            String camFile = String.valueOf(this.CAMERA_DIR) + "/" + sdFilename;
            File chdir = new File(this.CAMERA_DIR);
            if (!chdir.isDirectory()) {
                chdir.mkdirs();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);
            int shortSide = options.outHeight > options.outWidth ? options.outWidth : options.outHeight;
            int scale = shortSide / 960;
            if (shortSide % 960 != 0) {
                scale++;
            }
            int nextScale = GLHelpers.nextPow(scale);
            Log.e("rotate", "nextScale:" + nextScale + "/scale:" + scale + "/outHeight:" + options.outHeight + "/outWidth:" + options.outWidth);
            options.inJustDecodeBounds = false;
            options.inSampleSize = nextScale;
            Bitmap image = BitmapFactory.decodeFile(filename, options);
            int imageShortSide = image.getHeight() > image.getWidth() ? image.getWidth() : image.getHeight();
            int imageLongSide = image.getHeight() > image.getWidth() ? image.getHeight() : image.getWidth();
            float dispScale = Math.min(960.0f / imageShortSide, 1280.0f / imageLongSide);
            int mImageHeight = dispScale < 1.0f ? (int) (image.getHeight() * dispScale) : image.getHeight();
            int mImageWidth = dispScale < 1.0f ? (int) (image.getWidth() * dispScale) : image.getWidth();
            Log.e("test", String.valueOf(dispScale) + ":" + mImageHeight + "/" + mImageWidth + "  " + image.getHeight() + "/" + image.getWidth());
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, mImageWidth, mImageHeight, true);
            image.recycle();
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            Bitmap newBm = Bitmap.createBitmap(scaledBitmap, 0, 0, mImageWidth, mImageHeight, matrix, true);
            scaledBitmap.recycle();
            FileOutputStream fos = new FileOutputStream(new File(camFile));
            newBm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            newBm.recycle();
            return camFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return filename;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            return filename;
        }
    }

    public void networkError() {
        Intent i = new Intent(this, (Class<?>) NetworkError.class);
        startActivityForResult(i, NETWORK_ERROR_ACTIVITY);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_CAMERA_ACTIVITY) {
            if (resultCode == -1) {
                String filename = getResources().getString(R.string.krepo_photo_filename);
                String camFile = String.valueOf(this.CAMERA_DIR) + "/" + filename;
                this.REPO.imagefilename = rotateImage(camFile);
                setRepoPhoto(this.REPO.imagefilename);
                return;
            }
            return;
        }
        if (requestCode == PHOTO_PICK_ACTIVITY) {
            if (resultCode == -1) {
                ContentResolver cr = getContentResolver();
                String[] columns = {"_data"};
                Cursor c = cr.query(data.getData(), columns, null, null, null);
                c.moveToFirst();
                String filename2 = c.getString(0);
                this.REPO.imagefilename = rotateImage(filename2);
                setRepoPhoto(this.REPO.imagefilename);
                return;
            }
            return;
        }
        if (requestCode == LOCALE_ACTIVITY) {
            if (resultCode == -1) {
                CharSequence prefname = data.getCharSequenceExtra("prefname");
                CharSequence area = data.getCharSequenceExtra("area");
                CharSequence city = data.getCharSequenceExtra(GeoQuery.CITY);
                CharSequence cityname1 = data.getCharSequenceExtra("cityname1");
                CharSequence cityname2 = data.getCharSequenceExtra("cityname2");
                CharSequence meisho = data.getCharSequenceExtra("meisho");
                CharSequence lat = data.getCharSequenceExtra("lat");
                CharSequence lon = data.getCharSequenceExtra("lon");
                if (prefname == null) {
                    prefname = "";
                }
                this.REPO.prefname = prefname.toString();
                this.REPO.area = area.toString();
                this.REPO.city = city.toString();
                this.REPO.city_str = cityname1.toString();
                this.detailaddr = cityname2.toString();
                if (city.toString().equals("00000")) {
                    this.REPO.cityname = String.valueOf(cityname1.toString()) + cityname2.toString();
                } else {
                    this.REPO.cityname = cityname2.toString();
                }
                if (meisho == null) {
                    this.REPO.meisho = "";
                } else {
                    this.REPO.meisho = meisho.toString();
                }
                this.REPO.lat = lat.toString();
                this.REPO.lon = lon.toString();
                setLocaleItem();
                return;
            }
            return;
        }
        if (requestCode == COMMENT_ACTIVITY) {
            if (resultCode == -1) {
                CharSequence title = data.getCharSequenceExtra("title");
                CharSequence comment = data.getCharSequenceExtra("comment");
                this.REPO.title = title.toString();
                this.REPO.comment = comment.toString();
                setCommentItem();
                return;
            }
            return;
        }
        if (requestCode == IROZUKI_ACTIVITY) {
            if (resultCode == -1) {
                KoyoRepoItems coloring = (KoyoRepoItems) data.getSerializableExtra("coloring");
                this.REPO.coloring = coloring.code;
                setColoringItem(coloring.name);
                return;
            }
            return;
        }
        if (requestCode == TAKEPHOTO_ACTIVITY) {
            if (resultCode == -1) {
                RepoItem item = (RepoItem) data.getSerializableExtra("weather");
                this.REPO.date_takephoto = item.getCode();
                setTakePhotoItem(item.getName());
                return;
            }
            return;
        }
        if (requestCode == AUTH_ERROR_ACTIVITY) {
            if (resultCode == -1) {
                this.authKey = LoginPrefs.getAuthkey(this.ref);
                return;
            } else {
                if (resultCode == 0) {
                    finish();
                    return;
                }
                return;
            }
        }
        if (requestCode == NETWORK_ERROR_ACTIVITY) {
            if (resultCode == -1) {
                loadData();
                return;
            } else {
                if (resultCode == 0) {
                    finish();
                    return;
                }
                return;
            }
        }
        int i = this.REPO.sendResult;
        this.REPO.getClass();
        if (i == 3) {
            int i2 = this.REPO.sendResult;
            this.REPO.getClass();
            if (i2 == 6) {
                return;
            }
        }
        finish();
    }

    private void setLocaleItem() {
        CharSequence prefname = this.REPO.prefname;
        String str = this.REPO.area;
        CharSequence city = this.REPO.city;
        CharSequence cityname1 = this.REPO.city_str;
        CharSequence cityname2 = this.REPO.cityname;
        String str2 = this.REPO.meisho;
        CharSequence lat = this.REPO.lat;
        CharSequence lon = this.REPO.lon;
        TextView address = (TextView) findViewById(R.id.krepo_location_cityname1);
        TextView address_detail = (TextView) findViewById(R.id.krepo_location_cityname2);
        TextView notes = (TextView) findViewById(R.id.krepo_location_notes);
        TextView poslat = (TextView) findViewById(R.id.krepo_location_lat);
        TextView poslon = (TextView) findViewById(R.id.krepo_location_lon);
        ImageView arrow = (ImageView) findViewById(R.id.krepo_arrow_location);
        ImageView icon = (ImageView) findViewById(R.id.krepo_icon_location);
        Resources res = getResources();
        String latstr = "";
        String lonstr = "";
        if (lat != null && lat.length() != 0) {
            DEG2DMS latobj = new DEG2DMS(Double.valueOf(lat.toString()).doubleValue());
            latstr = Double.valueOf(lat.toString()).doubleValue() >= 0.0d ? ((Object) res.getText(R.string.srepo_locale_nlat)) + String.valueOf(latobj.getDeg()) + ((Object) res.getText(R.string.srepo_locale_deg)) + String.valueOf(latobj.getMin()) + ((Object) res.getText(R.string.srepo_locale_min)) + String.valueOf(latobj.getSec()) + ((Object) res.getText(R.string.srepo_locale_sec)) : ((Object) res.getText(R.string.srepo_locale_slat)) + String.valueOf(latobj.getDeg()) + ((Object) res.getText(R.string.srepo_locale_deg)) + String.valueOf(latobj.getMin()) + ((Object) res.getText(R.string.srepo_locale_min)) + String.valueOf(latobj.getSec()) + ((Object) res.getText(R.string.srepo_locale_sec));
        }
        if (lon != null && lon.length() != 0) {
            DEG2DMS lonobj = new DEG2DMS(Double.valueOf(lon.toString()).doubleValue());
            lonstr = Double.valueOf(lon.toString()).doubleValue() >= 0.0d ? ((Object) res.getText(R.string.srepo_locale_elon)) + String.valueOf(lonobj.getDeg()) + ((Object) res.getText(R.string.srepo_locale_deg)) + String.valueOf(lonobj.getMin()) + ((Object) res.getText(R.string.srepo_locale_min)) + String.valueOf(lonobj.getSec()) + ((Object) res.getText(R.string.srepo_locale_sec)) : ((Object) res.getText(R.string.srepo_locale_wlon)) + String.valueOf(lonobj.getDeg()) + ((Object) res.getText(R.string.srepo_locale_deg)) + String.valueOf(lonobj.getMin()) + ((Object) res.getText(R.string.srepo_locale_min)) + String.valueOf(lonobj.getSec()) + ((Object) res.getText(R.string.srepo_locale_sec));
        }
        if (cityname1 != null && cityname1.length() != 0) {
            address.setTextColor(-16777216);
            arrow.setImageResource(R.drawable.angleright_gray);
            icon.setImageResource(R.drawable.report_icon_location_enabled);
            address.setText(String.valueOf(prefname.toString()) + cityname1.toString());
            notes.setVisibility(8);
        } else {
            address.setTextColor(res.getColor(R.color.srepo_default));
            icon.setImageResource(R.drawable.report_icon_location_disabled);
            arrow.setImageResource(R.drawable.angleright_gray);
            notes.setVisibility(0);
        }
        if (cityname2 != null && cityname2.length() != 0) {
            address_detail.setTextColor(-16777216);
            arrow.setImageResource(R.drawable.angleright_gray);
            if (city.equals("00000")) {
                address_detail.setText(this.detailaddr);
            } else {
                address_detail.setText(cityname2.toString());
            }
            address_detail.setVisibility(0);
        } else {
            address_detail.setTextColor(res.getColor(R.color.srepo_default));
            arrow.setImageResource(R.drawable.angleright_gray);
            address_detail.setText("");
            address_detail.setVisibility(8);
        }
        if (lat != null && lat.length() != 0) {
            poslat.setTextColor(-16777216);
            arrow.setImageResource(R.drawable.angleright_gray);
            poslat.setText(latstr);
            poslat.setVisibility(0);
        } else {
            poslat.setTextColor(res.getColor(R.color.srepo_default));
            arrow.setImageResource(R.drawable.angleright_gray);
            poslat.setText("");
            poslat.setVisibility(8);
        }
        if (lon != null && lon.length() != 0) {
            poslon.setTextColor(-16777216);
            arrow.setImageResource(R.drawable.angleright_gray);
            poslon.setText(lonstr);
            poslon.setVisibility(0);
        } else {
            poslon.setTextColor(res.getColor(R.color.srepo_default));
            arrow.setImageResource(R.drawable.angleright_gray);
            poslon.setText("");
            poslon.setVisibility(8);
        }
        if (this.REPO.checkInputItem()) {
            FrameLayout send = (FrameLayout) findViewById(R.id.krepo_send);
            send.setVisibility(0);
        }
    }

    private void setCommentItem() {
        String comment = this.REPO.comment;
        String title = this.REPO.title;
        Resources res = getResources();
        TextView val_title = (TextView) findViewById(R.id.krepo_val_title);
        TextView val_comment = (TextView) findViewById(R.id.krepo_val_comment);
        ImageView icon = (ImageView) findViewById(R.id.krepo_icon_comment);
        if (comment != null && comment.length() != 0) {
            val_title.setTextColor(res.getColor(R.color.comment_title_color));
            val_comment.setTextColor(-16777216);
            icon.setImageResource(R.drawable.report_icon_title_and_body_enabled);
            if (title != null) {
                val_title.setText(title);
            }
            val_comment.setText(comment);
            val_comment.setVisibility(0);
        } else {
            val_title.setTextColor(res.getColor(R.color.srepo_default));
            val_comment.setTextColor(res.getColor(R.color.srepo_default));
            icon.setImageResource(R.drawable.report_icon_title_and_body_disabled);
            val_title.setText(getText(R.string.srepo_title_disable));
            val_comment.setVisibility(8);
        }
        if (this.REPO.checkInputItem()) {
            FrameLayout send = (FrameLayout) findViewById(R.id.krepo_send);
            send.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTakePhotoItem() {
        setTakePhotoItem(this.REPO.date_takephoto);
    }

    private void setTakePhotoItem(String date) {
        Resources res = getResources();
        TextView weatherVal = (TextView) findViewById(R.id.krepo_val_date);
        ImageView icon = (ImageView) findViewById(R.id.krepo_icon_selectday);
        weatherVal.setText(date);
        weatherVal.setTextColor(-16777216);
        icon.setImageResource(R.drawable.koyo_icon_search_day);
        TextView weatherI = (TextView) findViewById(R.id.krepo_item_date);
        weatherI.setTextColor(res.getColor(R.color.srepo_item));
        if (this.REPO.checkInputItem()) {
            FrameLayout send = (FrameLayout) findViewById(R.id.krepo_send);
            send.setVisibility(0);
        }
    }

    private void setColoringItem(String Coloring) {
        Resources res = getResources();
        TextView weatherVal = (TextView) findViewById(R.id.krepo_val_coloring);
        weatherVal.setText(Coloring);
        weatherVal.setTextColor(-16777216);
        ImageView icon = (ImageView) findViewById(R.id.krepo_icon_coloring);
        if (this.REPO.coloring.equals("1")) {
            icon.setImageResource(R.drawable.koyo_rank04);
        } else if (this.REPO.coloring.equals("2")) {
            icon.setImageResource(R.drawable.koyo_rank03);
        } else if (this.REPO.coloring.equals("3")) {
            icon.setImageResource(R.drawable.koyo_rank02);
        } else if (this.REPO.coloring.equals("4")) {
            icon.setImageResource(R.drawable.koyo_rank01);
        }
        TextView weatherI = (TextView) findViewById(R.id.krepo_item_coloring);
        weatherI.setTextColor(res.getColor(R.color.srepo_item));
        if (this.REPO.checkInputItem()) {
            FrameLayout send = (FrameLayout) findViewById(R.id.krepo_send);
            send.setVisibility(0);
        }
    }

    public void loadData() {
        CommonExecutor.instance.execute(new Runnable() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.12
            @Override // java.lang.Runnable
            public void run() {
                KoyoSendReport.this.REPO.checkIdValidity();
                String[] codes = KoyoSendReport.this.getResources().getStringArray(R.array.koyo_codes);
                String[] texts = KoyoSendReport.this.getResources().getStringArray(R.array.koyo_texts);
                KoyoSendReport.this.REPO.setItemList(codes, texts);
                Log.e("koyosendreport", "done!");
                KoyoSendReport.this.uiHandler.post(new Runnable() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.e("KoyoRepo", " checkIdResult = " + KoyoSendReport.this.REPO.checkIdResult);
                        int i = KoyoSendReport.this.REPO.checkIdResult;
                        KoyoSendReport.this.REPO.getClass();
                        if (i == 0) {
                            ViewGroup loading = (ViewGroup) KoyoSendReport.this.findViewById(R.id.loading);
                            loading.setVisibility(4);
                            KoyoSendReport.this.display();
                        } else {
                            int i2 = KoyoSendReport.this.REPO.checkIdResult;
                            KoyoSendReport.this.REPO.getClass();
                            if (i2 != 0) {
                                KoyoSendReport.this.commonDialog(KoyoSendReport.this.REPO.checkIdResult);
                            }
                        }
                        KoyoSendReport.this.fl.setVisibility(8);
                    }
                });
            }
        });
    }

    public void makePhotoDate() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.set(MapImageFilterResourceData.SAKURA_MIYAGI, 8, 1, 0, 0, 0);
        long l_cal1 = cal1.getTime().getTime();
        long l_cal2 = cal2.getTime().getTime();
        int dnum = (int) (((l_cal2 - l_cal1) / 86400000) + 1);
        this.strDate = new String[dnum];
        this.strDateNum = new String[dnum];
        for (int dd = dnum - 1; dd >= 0; dd--) {
            int dif = (dnum - 1) - dd;
            long m = 86400 * dif;
            long ms = l_cal2 - (1000 * m);
            Date newdate = new Date(ms);
            Calendar newcal = Calendar.getInstance();
            newcal.setTime(newdate);
            int year = newcal.get(1) + 1900;
            int month = newcal.get(2) + 1;
            int day = newcal.get(5);
            String str_date = String.format("%d月%d日", Integer.valueOf(month), Integer.valueOf(day));
            this.strDate[(dnum - dd) - 1] = str_date;
            String str_date2 = String.format("%d%02d%02d", Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            this.strDateNum[(dnum - dd) - 1] = str_date2;
        }
    }

    public void showSelectSoratomoDialog() {
        this.REPO.date_takephoto = this.strDate[0];
        this.REPO.num_takephoto = this.strDateNum[0];
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String dialogTitle = getString(R.string.question_select_soratomo);
        alert.setTitle(dialogTitle);
        alert.setSingleChoiceItems(this.strDate, 0, new DialogInterface.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                KoyoSendReport.this.REPO.date_takephoto = KoyoSendReport.this.strDate[which];
                KoyoSendReport.this.REPO.num_takephoto = KoyoSendReport.this.strDateNum[which];
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: wni.WeathernewsTouch.jp.Koyo.KoyoSendReport.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int idx) {
                KoyoSendReport.this.setTakePhotoItem();
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void onClickClose(View v) {
        finish();
    }

    public void authError(String reason) {
        Log.i("koyo", "authError = " + reason);
        if (reason.equals("7")) {
            Intent i = new Intent(this, (Class<?>) AuthenticationError.class);
            startActivityForResult(i, AUTH_ERROR_ACTIVITY);
        } else if (reason.equals("4")) {
            commonDialog(9);
        } else if (reason.equals("8")) {
            commonDialog(8);
        } else {
            commonDialog(7);
        }
    }

    public void commonDialog(int msgId) {
        String msg = "";
        Resources res = getResources();
        if (msgId == 0) {
            msg = res.getString(R.string.common_alert_message_0);
        } else if (msgId == 2) {
            msg = res.getString(R.string.common_alert_message_2);
        } else if (msgId == 3) {
            msg = res.getString(R.string.common_alert_message_3);
        } else if (msgId == 4) {
            msg = res.getString(R.string.common_alert_message_4);
        } else if (msgId == 5) {
            msg = res.getString(R.string.common_alert_message_5);
        } else if (msgId == 6) {
            msg = res.getString(R.string.common_alert_message_6);
        } else if (msgId == 7) {
            msg = res.getString(R.string.common_alert_message_7);
        } else if (msgId == 8) {
            msg = res.getString(R.string.common_alert_message_8);
        } else if (msgId == 9) {
            msg = res.getString(R.string.common_alert_message_9);
        } else if (msgId == 11) {
            msg = res.getString(R.string.common_alert_message_11);
        }
        Intent i = new Intent(this, (Class<?>) CommonAlertDialog.class);
        i.putExtra(NetworkError.NAME_MESSAG, msg);
        startActivityForResult(i, 0);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (4 == keyCode) {
            return false;
        }
        return super.onKeyDown(keyCode, e);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent e) {
        if (82 == keyCode) {
            startActivity(new Intent(this, (Class<?>) All.class));
            return true;
        }
        if (4 == keyCode) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, e);
    }
}
