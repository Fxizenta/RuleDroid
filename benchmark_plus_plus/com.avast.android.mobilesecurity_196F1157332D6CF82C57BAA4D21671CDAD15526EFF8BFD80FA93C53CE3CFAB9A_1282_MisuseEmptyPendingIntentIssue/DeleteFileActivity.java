package com.avast.android.mobilesecurity.app.scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.avast.android.mobilesecurity.C0000R;
import java.io.File;

/* loaded from: classes.dex */
public class DeleteFileActivity extends FragmentActivity {

    /* renamed from: a, reason: collision with root package name */
    Uri f344a;

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f344a = getIntent().getData();
        a();
    }

    private void a() {
        new DialogFragment().a(f(), "dialog");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        String path = this.f344a.getPath();
        try {
            File file = new File(path);
            new SecurityManager().checkDelete(path);
            if (file.delete()) {
                getContentResolver().delete(com.avast.android.mobilesecurity.q.a(), "name = ?", new String[]{path});
            }
        } catch (SecurityException e) {
            com.avast.android.generic.util.u.b("Cannot delete file [" + path + "] because of " + e.getMessage(), e);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        com.avast.android.generic.util.u.b("DeleteFileActivity onStop()");
        super.onStop();
    }

    /* loaded from: classes.dex */
    public class DialogFragment extends android.support.v4.app.DialogFragment {
        public DialogFragment() {
        }

        @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            com.avast.android.generic.util.u.b("DeleteFileActivity$DialogFragment onCancel()");
            ((DeleteFileActivity) l()).finish();
            super.onCancel(dialogInterface);
        }

        @Override // android.support.v4.app.DialogFragment
        public Dialog c(Bundle bundle) {
            return new AlertDialog.Builder(l()).setTitle(C0000R.string.menu_delete).setMessage(a(C0000R.string.msg_delete_confirm, ((DeleteFileActivity) l()).f344a.toString().replace("file://", ""))).setPositiveButton(C0000R.string.l_yes, new r(this)).setNegativeButton(C0000R.string.l_no, new p(this)).setNeutralButton(C0000R.string.l_cancel, new v(this)).setCancelable(true).create();
        }
    }
}
