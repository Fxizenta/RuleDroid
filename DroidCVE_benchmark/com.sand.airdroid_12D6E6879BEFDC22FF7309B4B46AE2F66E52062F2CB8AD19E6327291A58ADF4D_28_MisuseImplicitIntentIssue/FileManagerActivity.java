package com.sand.airdroid;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/* loaded from: classes.dex */
public class FileManagerActivity extends ListActivity implements View.OnClickListener {
    private LinearLayout c;
    private LinearLayout d;
    private TextView e;
    private TextView f;
    private ListView g;
    private ai u;

    /* renamed from: a, reason: collision with root package name */
    private Handler f127a = new p(this);
    private ImageView b = null;
    private af h = null;
    private ArrayList i = null;
    private File j = null;
    private String k = null;
    private String l = null;
    private String m = null;
    private ab n = null;
    private String o = null;
    private HashSet p = null;
    private com.ad.wd.b.f q = null;
    private Stack r = new Stack();
    private int s = 1;
    private File t = null;
    private ArrayList v = new ArrayList();
    private String w = null;
    private boolean x = false;
    private HashSet y = new HashSet();
    private boolean z = false;
    private int A = 1;
    private String[] B = null;
    private HashMap C = new HashMap();
    private int D = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int a(FileManagerActivity fileManagerActivity, File file) {
        int i = 0;
        if (file == null || !file.isDirectory()) {
            return 0;
        }
        String[] list = file.list();
        if (fileManagerActivity.z) {
            if (list != null) {
                return list.length;
            }
            return 0;
        }
        if (list == null) {
            return 0;
        }
        for (String str : list) {
            if (str != null && !str.startsWith(".")) {
                i++;
            }
        }
        return i;
    }

    public static Intent a(Context context) {
        Intent intent = new Intent(context, (Class<?>) FileManagerActivity.class);
        intent.putExtra("has_btn_back", true);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void a() {
    }

    private void a(Bundle bundle) {
        if (bundle == null) {
            this.k = Environment.getExternalStorageDirectory().getAbsolutePath();
            return;
        }
        this.k = bundle.getString("cur_dir_path");
        if (TextUtils.isEmpty(this.k) || !new File(this.k).exists()) {
            this.k = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str) {
        Toast.makeText(this, str, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(boolean z) {
        if (z) {
            this.x = false;
            this.f.setText(C0000R.string.fm_all);
            this.f.setCompoundDrawablesWithIntrinsicBounds(0, C0000R.drawable.fm_ob_ic_all, 0, 0);
        }
        this.c.setVisibility(z ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        this.s = 2;
        this.d.setVisibility(0);
        this.c.setVisibility(8);
        this.p = new HashSet();
        this.p.addAll(this.y);
        this.o = this.k;
        h();
        this.h.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void b(FileManagerActivity fileManagerActivity) {
        int firstVisiblePosition = fileManagerActivity.g.getFirstVisiblePosition();
        int lastVisiblePosition = fileManagerActivity.g.getLastVisiblePosition();
        PackageManager packageManager = fileManagerActivity.getPackageManager();
        for (int i = firstVisiblePosition; i < lastVisiblePosition + 1; i++) {
            View childAt = fileManagerActivity.g.getChildAt(i - firstVisiblePosition);
            if (childAt == null) {
                String str = "child_view == null: " + i;
            } else {
                ah ahVar = (ah) childAt.getTag();
                if (!ahVar.j) {
                    String absolutePath = ahVar.f145a.getAbsolutePath();
                    if (fileManagerActivity.C.containsKey(absolutePath)) {
                        ahVar.b.setImageDrawable((Drawable) fileManagerActivity.C.get(absolutePath));
                    } else {
                        String k = com.ad.wd.a.p.k(ahVar.f145a.getName());
                        if (!TextUtils.isEmpty(k)) {
                            String lowerCase = k.toLowerCase();
                            if (lowerCase.equals("apk")) {
                                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(absolutePath, 0);
                                if (packageArchiveInfo != null) {
                                    if (Build.VERSION.SDK_INT >= 8) {
                                        packageArchiveInfo.applicationInfo.sourceDir = absolutePath;
                                        packageArchiveInfo.applicationInfo.publicSourceDir = absolutePath;
                                    }
                                    Drawable applicationIcon = packageManager.getApplicationIcon(packageArchiveInfo.applicationInfo);
                                    if (applicationIcon != null) {
                                        String str2 = "缓存图标apk：" + ahVar.f145a.getName();
                                        fileManagerActivity.C.put(absolutePath, applicationIcon);
                                        ahVar.b.setImageDrawable(applicationIcon);
                                    }
                                }
                            } else if (lowerCase.equals("png") || lowerCase.equals("gif") || lowerCase.equals("jpg") || lowerCase.equals("jpeg")) {
                                Bitmap b = com.ad.wd.a.bi.b(absolutePath, fileManagerActivity.D);
                                if (b == null) {
                                    String str3 = "读取图片到bitmap中失败" + ahVar.f145a.getName();
                                } else {
                                    BitmapDrawable bitmapDrawable = new BitmapDrawable(b);
                                    String str4 = "缓存图片png：" + ahVar.f145a.getName();
                                    fileManagerActivity.C.put(absolutePath, bitmapDrawable);
                                    ahVar.b.setImageDrawable(bitmapDrawable);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        this.s = 3;
        this.d.setVisibility(0);
        this.c.setVisibility(8);
        this.p = new HashSet();
        this.p.addAll(this.y);
        this.o = this.k;
        h();
        this.h.notifyDataSetChanged();
    }

    private boolean d() {
        return this.j == null || Environment.getExternalStorageDirectory().compareTo(this.j) == 0;
    }

    private static boolean e() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    private boolean f() {
        Integer num;
        if (d()) {
            return false;
        }
        this.k = this.j.getParent();
        j();
        this.g.setSelection((this.r.isEmpty() || (num = (Integer) this.r.pop()) == null) ? -1 : num.intValue());
        g();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g() {
        this.y.clear();
        a(false);
    }

    private void h() {
        if (this.o == null || this.i == null || this.k == null || !this.o.equals(this.k)) {
            return;
        }
        Iterator it = this.i.iterator();
        while (it.hasNext()) {
            if (this.p.contains(((File) it.next()).getAbsolutePath())) {
                it.remove();
            }
        }
    }

    private void i() {
        com.ad.wd.a.ao.a(this.i, this.A);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j() {
        if (!e()) {
            findViewById(C0000R.id.llSDNotAvailable).setVisibility(0);
            findViewById(C0000R.id.llFileList).setVisibility(8);
            return;
        }
        findViewById(C0000R.id.llSDNotAvailable).setVisibility(8);
        findViewById(C0000R.id.llFileList).setVisibility(0);
        this.j = new File(this.k);
        String str = this.k;
        if (str.startsWith("/mnt")) {
            str = str.substring(4);
        }
        this.e.setText(str);
        File[] listFiles = this.j.listFiles(new s(this));
        if (listFiles != null) {
            this.i = new ArrayList();
            this.i.addAll(Arrays.asList(listFiles));
        } else {
            this.i = null;
        }
        if (this.s == 2 || this.s == 3) {
            h();
        }
        i();
        if (this.h == null) {
            this.h = new af(this);
            this.g.setAdapter((ListAdapter) this.h);
        }
        this.h.notifyDataSetChanged();
        this.b.setVisibility(d() ? 8 : 0);
        a(false);
        this.f127a.sendEmptyMessageDelayed(2, 200L);
        if (this.C.size() > 100) {
            this.C.clear();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case C0000R.id.tvCurPath /* 2131558417 */:
                showDialog(6);
                return;
            case C0000R.id.ivFileUpLevel /* 2131558418 */:
                f();
                return;
            case C0000R.id.llFileOperationBar /* 2131558419 */:
            case C0000R.id.llFileCopyOper /* 2131558425 */:
            default:
                return;
            case C0000R.id.tvFileOperDel /* 2131558420 */:
                showDialog(1);
                return;
            case C0000R.id.tvFileOperCopy /* 2131558421 */:
                b();
                return;
            case C0000R.id.tvFileOperMove /* 2131558422 */:
                c();
                return;
            case C0000R.id.tvFileOperSend /* 2131558423 */:
                Object[] array = this.y.toArray();
                for (Object obj : array) {
                    if (new File((String) obj).isDirectory()) {
                        this.l = getString(C0000R.string.fm_folder_cant_send);
                        this.m = getString(C0000R.string.fm_send);
                        showDialog(3);
                        this.y.clear();
                        this.c.setVisibility(8);
                        j();
                        return;
                    }
                }
                if (array.length == 1) {
                    String str = (String) array[0];
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("*/*");
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(str)));
                    startActivity(intent);
                } else {
                    ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
                    for (Object obj2 : array) {
                        arrayList.add(Uri.fromFile(new File((String) obj2)));
                    }
                    Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent2.setType("*/*");
                    intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
                    startActivity(intent2);
                }
                this.y.clear();
                this.c.setVisibility(8);
                j();
                return;
            case C0000R.id.tvFileOperSelectAll /* 2131558424 */:
                TextView textView = (TextView) view;
                if (this.x) {
                    this.x = false;
                    this.y.clear();
                    textView.setText(C0000R.string.fm_all);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, C0000R.drawable.fm_ob_ic_all, 0, 0);
                    a(false);
                } else {
                    this.x = true;
                    Iterator it = this.i.iterator();
                    while (it.hasNext()) {
                        this.y.add(((File) it.next()).getAbsolutePath());
                    }
                    textView.setText(C0000R.string.fm_cancel);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, C0000R.drawable.fm_ob_ic_cancel, 0, 0);
                }
                this.h.notifyDataSetChanged();
                return;
            case C0000R.id.btnPaste /* 2131558426 */:
                ab abVar = new ab(this);
                this.n = abVar;
                if (this.s == 3) {
                    abVar.f140a = 2;
                }
                abVar.execute(null);
                return;
            case C0000R.id.btnCancelPaste /* 2131558427 */:
                this.s = 1;
                this.y.clear();
                this.o = null;
                this.d.setVisibility(8);
                this.p.clear();
                this.p = null;
                j();
                return;
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0000R.layout.ad_file_manager);
        if (com.ad.wd.a.br.a(C0000R.string.pref_always_screen, (Context) this, false)) {
            getWindow().addFlags(128);
        }
        a(bundle);
        this.e = (TextView) findViewById(C0000R.id.tvCurPath);
        this.b = (ImageView) findViewById(C0000R.id.ivFileUpLevel);
        this.c = (LinearLayout) findViewById(C0000R.id.llFileOperationBar);
        this.f = (TextView) findViewById(C0000R.id.tvFileOperSelectAll);
        this.d = (LinearLayout) findViewById(C0000R.id.llFileCopyOper);
        this.g = getListView();
        this.g.setOnItemClickListener(new z(this));
        this.g.setOnItemLongClickListener(new aa(this));
        this.g.setFastScrollEnabled(true);
        this.g.setOnScrollListener(new q(this));
        findViewById(C0000R.id.ivFileUpLevel).setOnClickListener(this);
        this.f.setOnClickListener(this);
        findViewById(C0000R.id.tvFileOperCopy).setOnClickListener(this);
        findViewById(C0000R.id.tvFileOperDel).setOnClickListener(this);
        findViewById(C0000R.id.tvFileOperMove).setOnClickListener(this);
        findViewById(C0000R.id.tvFileOperSend).setOnClickListener(this);
        findViewById(C0000R.id.btnPaste).setOnClickListener(this);
        findViewById(C0000R.id.btnCancelPaste).setOnClickListener(this);
        this.e.setOnClickListener(this);
        this.u = new ai(this);
        this.B = getResources().getStringArray(C0000R.array.fm_list_item_menu_array);
        this.D = (int) (getResources().getDimension(C0000R.dimen.fm_file_icon_size) + 0.5d);
        View findViewById = findViewById(C0000R.id.btnBack);
        findViewById.setOnClickListener(new r(this));
        findViewById.setVisibility(getIntent().getBooleanExtra("has_btn_back", false) ? 0 : 4);
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int i) {
        switch (i) {
            case 1:
                com.ad.wd.b.a aVar = new com.ad.wd.b.a(this);
                aVar.d(getString(C0000R.string.fm_del));
                aVar.b(getString(C0000R.string.fm_del_tip));
                aVar.a(getString(C0000R.string.ad_yes), new t(this));
                aVar.a(getString(C0000R.string.ad_no));
                return aVar;
            case 2:
                com.ad.wd.b.f fVar = new com.ad.wd.b.f(this);
                this.q = fVar;
                fVar.a(getString(C0000R.string.fm_copy));
                fVar.a(getString(C0000R.string.fm_cancel), new x(this));
                return fVar;
            case 3:
                com.ad.wd.b.a aVar2 = new com.ad.wd.b.a(this);
                aVar2.d("");
                aVar2.a();
                aVar2.b(this.l == null ? "" : this.l);
                aVar2.a(getString(C0000R.string.ad_ok));
                return aVar2;
            case 4:
                com.ad.wd.b.c cVar = new com.ad.wd.b.c(this);
                cVar.a(getString(C0000R.string.fm_new_folder));
                cVar.a(getString(C0000R.string.ad_ok), new v(this));
                cVar.b(getString(C0000R.string.ad_cancel));
                return cVar;
            case 5:
                com.ad.wd.b.c cVar2 = new com.ad.wd.b.c(this);
                cVar2.a(getString(C0000R.string.fm_rename));
                cVar2.a(getString(C0000R.string.ad_ok), new y(this));
                cVar2.b(getString(C0000R.string.ad_cancel));
                return cVar2;
            case 6:
                com.ad.wd.b.d dVar = new com.ad.wd.b.d(this);
                dVar.a(getString(C0000R.string.fm_jumpto));
                dVar.a(this.u, new w(this));
                dVar.b(getString(C0000R.string.fm_cancel), null);
                return dVar;
            case 7:
                com.ad.wd.b.d dVar2 = new com.ad.wd.b.d(this);
                dVar2.a(" ");
                dVar2.a(this.B, new u(this));
                dVar2.b(getResources().getString(C0000R.string.fm_cancel), null);
                return dVar2;
            default:
                return super.onCreateDialog(i);
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0000R.menu.fm_option_menu, menu);
        return true;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && e()) {
            if (!this.y.isEmpty() && this.s == 1) {
                this.y.clear();
                this.h.notifyDataSetChanged();
                a(false);
                return false;
            }
            if (f()) {
                return true;
            }
            if (this.s != 2 && this.s != 3) {
                return super.onKeyDown(i, keyEvent);
            }
            this.o = null;
            this.p.clear();
            this.p = null;
            this.s = 1;
            this.d.setVisibility(8);
            this.y.clear();
            j();
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        CharSequence title = menuItem.getTitle();
        if (title != null) {
            title.toString();
        }
        switch (menuItem.getItemId()) {
            case C0000R.id.menu_sort_by_name /* 2131558569 */:
                this.A = 1;
                i();
                this.h.notifyDataSetChanged();
                setSelection(0);
                break;
            case C0000R.id.menu_sort_by_size /* 2131558570 */:
                this.A = 2;
                i();
                this.h.notifyDataSetChanged();
                setSelection(0);
                break;
            case C0000R.id.menu_sort_by_time /* 2131558571 */:
                this.A = 3;
                i();
                this.h.notifyDataSetChanged();
                setSelection(0);
                break;
            case C0000R.id.menu_sort_by_type /* 2131558572 */:
                this.A = 4;
                i();
                this.h.notifyDataSetChanged();
                setSelection(0);
                break;
            case C0000R.id.menu_new_folder /* 2131558573 */:
                showDialog(4);
                break;
            case C0000R.id.menu_show_dot /* 2131558575 */:
                this.z = this.z ? false : true;
                j();
                break;
            case C0000R.id.menu_refresh /* 2131558576 */:
                this.y.clear();
                a(false);
                j();
                a(getString(C0000R.string.fm_refresh_finish));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int i, Dialog dialog) {
        switch (i) {
            case 2:
                if (this.s != 2) {
                    if (this.s == 3) {
                        this.q.a(getString(C0000R.string.fm_cut));
                        break;
                    }
                } else {
                    this.q.a(getString(C0000R.string.fm_copy));
                    break;
                }
                break;
            case 3:
                com.ad.wd.b.a aVar = (com.ad.wd.b.a) dialog;
                aVar.b(this.l == null ? "" : this.l);
                aVar.d(this.m == null ? getString(C0000R.string.fm_notice) : this.m);
                break;
            case 4:
                ((com.ad.wd.b.c) dialog).b().setText("");
                break;
            case 5:
                if (this.w == null) {
                    return;
                }
                EditText b = ((com.ad.wd.b.c) dialog).b();
                File file = new File(this.w);
                b.setText(file.getName());
                if (!file.isDirectory()) {
                    String j = com.ad.wd.a.p.j(file.getName());
                    if (j != null) {
                        Selection.setSelection(b.getEditableText(), 0, j.length());
                        break;
                    }
                } else {
                    b.selectAll();
                    break;
                }
                break;
            case 6:
                File file2 = new File(this.k);
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                this.v.clear();
                while (file2.compareTo(externalStorageDirectory) != 0) {
                    file2 = file2.getParentFile();
                    this.v.add(file2);
                }
                if (this.v.isEmpty()) {
                    this.v.add(externalStorageDirectory);
                }
                ((ai) ((com.ad.wd.b.d) dialog).b()).notifyDataSetChanged();
                break;
            case 7:
                ((com.ad.wd.b.d) dialog).a(this.t.getName());
                break;
        }
        super.onPrepareDialog(i, dialog);
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        int i = C0000R.id.menu_sort_by_name;
        switch (this.A) {
            case 2:
                i = C0000R.id.menu_sort_by_size;
                break;
            case 3:
                i = C0000R.id.menu_sort_by_time;
                break;
            case 4:
                i = C0000R.id.menu_sort_by_type;
                break;
        }
        menu.findItem(i).setChecked(true);
        menu.findItem(C0000R.id.menu_show_dot).setTitle(!this.z ? C0000R.string.fm_show_dot : C0000R.string.fm_hide_dot);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // android.app.ListActivity, android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        a(bundle);
        super.onRestoreInstanceState(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        j();
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        if (!TextUtils.isEmpty(this.k) && bundle != null) {
            bundle.putString("cur_dir_path", this.k);
        }
        super.onSaveInstanceState(bundle);
    }
}
