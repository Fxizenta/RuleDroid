package com.cisco.webex.meetings.ui.premeeting.recording;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cisco.webex.meetings.R;
import defpackage.ehm;
import defpackage.eqa;
import defpackage.eqh;
import defpackage.mj;
import defpackage.na;
import org.greenrobot.eventbus.ThreadMode;

/* loaded from: classes.dex */
public class DialogFragmentPlayRecording extends DialogFragment {
    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.dialog_fragment_play_recording, viewGroup);
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        setCancelable(false);
        eqa.a().a(this);
        na.p().b(getArguments().getLong("recordingId"));
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        eqa.a().c(this);
    }

    @eqh(a = ThreadMode.MAIN)
    public void a(na.c cVar) {
        if (cVar.a() != 0) {
            mj.a(getContext(), new Intent(), cVar.a(), new Object[0]);
        } else {
            Intent intent = new Intent("android.intent.action.VIEW");
            if (!ehm.B(cVar.a.getPlayUrl())) {
                intent.setData(Uri.parse(cVar.a.getPlayUrl()));
            } else if (!ehm.B(cVar.a.getPlaybackPortal())) {
                intent.setData(Uri.parse(cVar.a.getPlaybackPortal()));
            } else {
                intent.setData(Uri.parse(cVar.a.getStreamUrl()));
            }
            startActivity(intent);
        }
        dismissAllowingStateLoss();
    }

    public static DialogFragmentPlayRecording a(long j) {
        DialogFragmentPlayRecording dialogFragmentPlayRecording = new DialogFragmentPlayRecording();
        Bundle bundle = new Bundle();
        bundle.putLong("recordingId", j);
        dialogFragmentPlayRecording.setArguments(bundle);
        return dialogFragmentPlayRecording;
    }
}
