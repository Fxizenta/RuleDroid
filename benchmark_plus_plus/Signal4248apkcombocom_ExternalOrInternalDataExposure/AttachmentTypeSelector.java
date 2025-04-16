package org.thoughtcrime.securesms.components;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.components.RecentPhotoViewRail;
import org.thoughtcrime.securesms.permissions.Permissions;
import org.thoughtcrime.securesms.util.ViewUtil;

/* loaded from: classes.dex */
public class AttachmentTypeSelector extends PopupWindow {
    public static final int ADD_CONTACT_INFO = 4;
    public static final int ADD_DOCUMENT = 2;
    public static final int ADD_GALLERY = 1;
    public static final int ADD_GIF = 7;
    public static final int ADD_LOCATION = 6;
    public static final int ADD_SOUND = 3;
    private static final int ANIMATION_DURATION = 300;
    private static final String TAG = AttachmentTypeSelector.class.getSimpleName();
    public static final int TAKE_PHOTO = 5;
    private final ImageView audioButton;
    private final ImageView cameraButton;
    private final ImageView closeButton;
    private final ImageView contactButton;
    private View currentAnchor;
    private final ImageView documentButton;
    private final ImageView gifButton;
    private final ImageView imageButton;
    private AttachmentClickedListener listener;
    private final LoaderManager loaderManager;
    private final ImageView locationButton;
    private final RecentPhotoViewRail recentRail;

    /* loaded from: classes.dex */
    public interface AttachmentClickedListener {
        void onClick(int i);

        void onQuickAttachment(Uri uri);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AttachmentTypeSelector(Context context, LoaderManager loaderManager, AttachmentClickedListener attachmentClickedListener) {
        super(context);
        LinearLayout linearLayout = (LinearLayout) ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.attachment_type_selector, (ViewGroup) null, true);
        this.listener = attachmentClickedListener;
        this.loaderManager = loaderManager;
        this.recentRail = (RecentPhotoViewRail) ViewUtil.findById(linearLayout, R.id.recent_photos);
        this.imageButton = (ImageView) ViewUtil.findById(linearLayout, R.id.gallery_button);
        this.audioButton = (ImageView) ViewUtil.findById(linearLayout, R.id.audio_button);
        this.documentButton = (ImageView) ViewUtil.findById(linearLayout, R.id.document_button);
        this.contactButton = (ImageView) ViewUtil.findById(linearLayout, R.id.contact_button);
        this.cameraButton = (ImageView) ViewUtil.findById(linearLayout, R.id.camera_button);
        this.locationButton = (ImageView) ViewUtil.findById(linearLayout, R.id.location_button);
        this.gifButton = (ImageView) ViewUtil.findById(linearLayout, R.id.giphy_button);
        this.closeButton = (ImageView) ViewUtil.findById(linearLayout, R.id.close_button);
        this.imageButton.setOnClickListener(new PropagatingClickListener(1));
        this.audioButton.setOnClickListener(new PropagatingClickListener(3));
        this.documentButton.setOnClickListener(new PropagatingClickListener(2));
        this.contactButton.setOnClickListener(new PropagatingClickListener(4));
        this.cameraButton.setOnClickListener(new PropagatingClickListener(5));
        this.locationButton.setOnClickListener(new PropagatingClickListener(6));
        this.gifButton.setOnClickListener(new PropagatingClickListener(7));
        this.closeButton.setOnClickListener(new CloseClickListener());
        this.recentRail.setListener(new RecentPhotoSelectedListener());
        if (Build.VERSION.SDK_INT < 16) {
            ViewUtil.findById(linearLayout, R.id.location_linear_layout).setVisibility(4);
        }
        setContentView(linearLayout);
        setWidth(-1);
        setHeight(-2);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(0);
        setInputMethodMode(2);
        setFocusable(true);
        setTouchable(true);
        loaderManager.initLoader(1, null, this.recentRail);
    }

    public void show(Activity activity, final View view) {
        if (Permissions.hasAll(activity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            this.recentRail.setVisibility(0);
            this.loaderManager.restartLoader(1, null, this.recentRail);
        } else {
            this.recentRail.setVisibility(8);
        }
        this.currentAnchor = view;
        showAtLocation(view, 80, 0, 0);
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: org.thoughtcrime.securesms.components.AttachmentTypeSelector.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                AttachmentTypeSelector.this.getContentView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (Build.VERSION.SDK_INT >= 21) {
                    AttachmentTypeSelector.this.animateWindowInCircular(view, AttachmentTypeSelector.this.getContentView());
                } else {
                    AttachmentTypeSelector.this.animateWindowInTranslate(AttachmentTypeSelector.this.getContentView());
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            animateButtonIn(this.imageButton, 150);
            animateButtonIn(this.cameraButton, 150);
            animateButtonIn(this.audioButton, 100);
            animateButtonIn(this.locationButton, 100);
            animateButtonIn(this.documentButton, 75);
            animateButtonIn(this.gifButton, 75);
            animateButtonIn(this.contactButton, 0);
            animateButtonIn(this.closeButton, 0);
        }
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        if (Build.VERSION.SDK_INT >= 21) {
            animateWindowOutCircular(this.currentAnchor, getContentView());
        } else {
            animateWindowOutTranslate(getContentView());
        }
    }

    public void setListener(AttachmentClickedListener attachmentClickedListener) {
        this.listener = attachmentClickedListener;
    }

    private void animateButtonIn(View view, int i) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 1, 0.5f, 1, 0.0f));
        animationSet.setInterpolator(new OvershootInterpolator(1.0f));
        animationSet.setDuration(300L);
        animationSet.setStartOffset(i);
        view.startAnimation(animationSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(21)
    public void animateWindowInCircular(View view, View view2) {
        Pair<Integer, Integer> clickOrigin = getClickOrigin(view, view2);
        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(view2, ((Integer) clickOrigin.first).intValue(), ((Integer) clickOrigin.second).intValue(), 0.0f, Math.max(view2.getWidth(), view2.getHeight()));
        createCircularReveal.setDuration(300L);
        createCircularReveal.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateWindowInTranslate(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, view.getHeight(), 0.0f);
        translateAnimation.setDuration(300L);
        getContentView().startAnimation(translateAnimation);
    }

    @TargetApi(21)
    private void animateWindowOutCircular(View view, View view2) {
        Pair<Integer, Integer> clickOrigin = getClickOrigin(view, view2);
        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(getContentView(), ((Integer) clickOrigin.first).intValue(), ((Integer) clickOrigin.second).intValue(), Math.max(getContentView().getWidth(), getContentView().getHeight()), 0.0f);
        createCircularReveal.setDuration(300L);
        createCircularReveal.addListener(new Animator.AnimatorListener() { // from class: org.thoughtcrime.securesms.components.AttachmentTypeSelector.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                AttachmentTypeSelector.super.dismiss();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        createCircularReveal.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateWindowOutTranslate(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, view.getTop() + view.getHeight());
        translateAnimation.setDuration(300L);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: org.thoughtcrime.securesms.components.AttachmentTypeSelector.3
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                AttachmentTypeSelector.super.dismiss();
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }
        });
        getContentView().startAnimation(translateAnimation);
    }

    private Pair<Integer, Integer> getClickOrigin(View view, View view2) {
        if (view == null) {
            return new Pair<>(0, 0);
        }
        view.getLocationOnScreen(r0);
        int[] iArr = {iArr[0] + (view.getWidth() / 2), iArr[1] + (view.getHeight() / 2)};
        int[] iArr2 = new int[2];
        view2.getLocationOnScreen(iArr2);
        return new Pair<>(Integer.valueOf(iArr[0] - iArr2[0]), Integer.valueOf(iArr[1] - iArr2[1]));
    }

    /* loaded from: classes.dex */
    private class RecentPhotoSelectedListener implements RecentPhotoViewRail.OnItemClickedListener {
        private RecentPhotoSelectedListener() {
        }

        @Override // org.thoughtcrime.securesms.components.RecentPhotoViewRail.OnItemClickedListener
        public void onItemClicked(Uri uri) {
            AttachmentTypeSelector.this.animateWindowOutTranslate(AttachmentTypeSelector.this.getContentView());
            if (AttachmentTypeSelector.this.listener != null) {
                AttachmentTypeSelector.this.listener.onQuickAttachment(uri);
            }
        }
    }

    /* loaded from: classes.dex */
    private class PropagatingClickListener implements View.OnClickListener {
        private final int type;

        private PropagatingClickListener(int i) {
            this.type = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AttachmentTypeSelector.this.animateWindowOutTranslate(AttachmentTypeSelector.this.getContentView());
            if (AttachmentTypeSelector.this.listener != null) {
                AttachmentTypeSelector.this.listener.onClick(this.type);
            }
        }
    }

    /* loaded from: classes.dex */
    private class CloseClickListener implements View.OnClickListener {
        private CloseClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AttachmentTypeSelector.this.dismiss();
        }
    }
}
