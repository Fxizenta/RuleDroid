package d.h.b;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import d.f.k.v;
import java.util.Arrays;

/* loaded from: classes.dex */
public class a {
    private static final Interpolator w = new InterpolatorC0073a();
    private int a;
    private int b;

    /* renamed from: d, reason: collision with root package name */
    private float[] f1553d;

    /* renamed from: e, reason: collision with root package name */
    private float[] f1554e;

    /* renamed from: f, reason: collision with root package name */
    private float[] f1555f;

    /* renamed from: g, reason: collision with root package name */
    private float[] f1556g;

    /* renamed from: h, reason: collision with root package name */
    private int[] f1557h;

    /* renamed from: i, reason: collision with root package name */
    private int[] f1558i;

    /* renamed from: j, reason: collision with root package name */
    private int[] f1559j;

    /* renamed from: k, reason: collision with root package name */
    private int f1560k;

    /* renamed from: l, reason: collision with root package name */
    private VelocityTracker f1561l;
    private float m;
    private float n;
    private int o;
    private int p;
    private OverScroller q;
    private final c r;
    private View s;
    private boolean t;
    private final ViewGroup u;

    /* renamed from: c, reason: collision with root package name */
    private int f1552c = -1;
    private final Runnable v = new b();

    /* renamed from: d.h.b.a$a, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    static class InterpolatorC0073a implements Interpolator {
        InterpolatorC0073a() {
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f2) {
            float f3 = f2 - 1.0f;
            return (f3 * f3 * f3 * f3 * f3) + 1.0f;
        }
    }

    /* loaded from: classes.dex */
    class b implements Runnable {
        b() {
        }

        @Override // java.lang.Runnable
        public void run() {
            a.this.c(0);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class c {
        public int a(int i2) {
            return i2;
        }

        public abstract int a(View view);

        public abstract int a(View view, int i2, int i3);

        public abstract void a(int i2, int i3);

        public abstract void a(View view, float f2, float f3);

        public abstract void a(View view, int i2);

        public abstract void a(View view, int i2, int i3, int i4, int i5);

        public int b(View view) {
            return 0;
        }

        public abstract int b(View view, int i2, int i3);

        public void b(int i2, int i3) {
        }

        public boolean b(int i2) {
            return false;
        }

        public abstract boolean b(View view, int i2);

        public abstract void c(int i2);
    }

    private a(Context context, ViewGroup viewGroup, c cVar) {
        if (viewGroup == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        }
        if (cVar == null) {
            throw new IllegalArgumentException("Callback may not be null");
        }
        this.u = viewGroup;
        this.r = cVar;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.o = (int) ((context.getResources().getDisplayMetrics().density * 20.0f) + 0.5f);
        this.b = viewConfiguration.getScaledTouchSlop();
        this.m = viewConfiguration.getScaledMaximumFlingVelocity();
        this.n = viewConfiguration.getScaledMinimumFlingVelocity();
        this.q = new OverScroller(context, w);
    }

    private float a(float f2, float f3, float f4) {
        float abs = Math.abs(f2);
        return abs < f3 ? BitmapDescriptorFactory.HUE_RED : abs > f4 ? f2 > BitmapDescriptorFactory.HUE_RED ? f4 : -f4 : f2;
    }

    private int a(int i2, int i3, int i4) {
        int abs = Math.abs(i2);
        if (abs < i3) {
            return 0;
        }
        return abs > i4 ? i2 > 0 ? i4 : -i4 : i2;
    }

    private int a(View view, int i2, int i3, int i4, int i5) {
        float f2;
        float f3;
        float f4;
        float f5;
        int a = a(i4, (int) this.n, (int) this.m);
        int a2 = a(i5, (int) this.n, (int) this.m);
        int abs = Math.abs(i2);
        int abs2 = Math.abs(i3);
        int abs3 = Math.abs(a);
        int abs4 = Math.abs(a2);
        int i6 = abs3 + abs4;
        int i7 = abs + abs2;
        if (a != 0) {
            f2 = abs3;
            f3 = i6;
        } else {
            f2 = abs;
            f3 = i7;
        }
        float f6 = f2 / f3;
        if (a2 != 0) {
            f4 = abs4;
            f5 = i6;
        } else {
            f4 = abs2;
            f5 = i7;
        }
        return (int) ((b(i2, a, this.r.a(view)) * f6) + (b(i3, a2, this.r.b(view)) * (f4 / f5)));
    }

    public static a a(ViewGroup viewGroup, float f2, c cVar) {
        a a = a(viewGroup, cVar);
        a.b = (int) (a.b * (1.0f / f2));
        return a;
    }

    public static a a(ViewGroup viewGroup, c cVar) {
        return new a(viewGroup.getContext(), viewGroup, cVar);
    }

    private void a(float f2, float f3) {
        this.t = true;
        this.r.a(this.s, f2, f3);
        this.t = false;
        if (this.a == 1) {
            c(0);
        }
    }

    private void a(float f2, float f3, int i2) {
        int i3 = a(f2, f3, i2, 1) ? 1 : 0;
        if (a(f3, f2, i2, 4)) {
            i3 |= 4;
        }
        if (a(f2, f3, i2, 2)) {
            i3 |= 2;
        }
        if (a(f3, f2, i2, 8)) {
            i3 |= 8;
        }
        if (i3 != 0) {
            int[] iArr = this.f1558i;
            iArr[i2] = iArr[i2] | i3;
            this.r.a(i3, i2);
        }
    }

    private void a(int i2, int i3, int i4, int i5) {
        int left = this.s.getLeft();
        int top = this.s.getTop();
        if (i4 != 0) {
            i2 = this.r.a(this.s, i2, i4);
            v.d(this.s, i2 - left);
        }
        int i6 = i2;
        if (i5 != 0) {
            i3 = this.r.b(this.s, i3, i5);
            v.e(this.s, i3 - top);
        }
        int i7 = i3;
        if (i4 == 0 && i5 == 0) {
            return;
        }
        this.r.a(this.s, i6, i7, i6 - left, i7 - top);
    }

    private boolean a(float f2, float f3, int i2, int i3) {
        float abs = Math.abs(f2);
        float abs2 = Math.abs(f3);
        if ((this.f1557h[i2] & i3) != i3 || (this.p & i3) == 0 || (this.f1559j[i2] & i3) == i3 || (this.f1558i[i2] & i3) == i3) {
            return false;
        }
        int i4 = this.b;
        if (abs <= i4 && abs2 <= i4) {
            return false;
        }
        if (abs >= abs2 * 0.5f || !this.r.b(i3)) {
            return (this.f1558i[i2] & i3) == 0 && abs > ((float) this.b);
        }
        int[] iArr = this.f1559j;
        iArr[i2] = iArr[i2] | i3;
        return false;
    }

    private boolean a(View view, float f2, float f3) {
        if (view == null) {
            return false;
        }
        boolean z = this.r.a(view) > 0;
        boolean z2 = this.r.b(view) > 0;
        if (!z || !z2) {
            return z ? Math.abs(f2) > ((float) this.b) : z2 && Math.abs(f3) > ((float) this.b);
        }
        float f4 = (f2 * f2) + (f3 * f3);
        int i2 = this.b;
        return f4 > ((float) (i2 * i2));
    }

    private float b(float f2) {
        return (float) Math.sin((f2 - 0.5f) * 0.47123894f);
    }

    private int b(int i2, int i3, int i4) {
        if (i2 == 0) {
            return 0;
        }
        int width = this.u.getWidth();
        float f2 = width / 2;
        float b2 = f2 + (b(Math.min(1.0f, Math.abs(i2) / width)) * f2);
        int abs = Math.abs(i3);
        return Math.min(abs > 0 ? Math.round(Math.abs(b2 / abs) * 1000.0f) * 4 : (int) (((Math.abs(i2) / i4) + 1.0f) * 256.0f), 600);
    }

    private void b(float f2, float f3, int i2) {
        f(i2);
        float[] fArr = this.f1553d;
        this.f1555f[i2] = f2;
        fArr[i2] = f2;
        float[] fArr2 = this.f1554e;
        this.f1556g[i2] = f3;
        fArr2[i2] = f3;
        this.f1557h[i2] = e((int) f2, (int) f3);
        this.f1560k |= 1 << i2;
    }

    private boolean b(int i2, int i3, int i4, int i5) {
        int left = this.s.getLeft();
        int top = this.s.getTop();
        int i6 = i2 - left;
        int i7 = i3 - top;
        if (i6 == 0 && i7 == 0) {
            this.q.abortAnimation();
            c(0);
            return false;
        }
        this.q.startScroll(left, top, i6, i7, a(this.s, i6, i7, i4, i5));
        c(2);
        return true;
    }

    private void c(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        for (int i2 = 0; i2 < pointerCount; i2++) {
            int pointerId = motionEvent.getPointerId(i2);
            if (g(pointerId)) {
                float x = motionEvent.getX(i2);
                float y = motionEvent.getY(i2);
                this.f1555f[pointerId] = x;
                this.f1556g[pointerId] = y;
            }
        }
    }

    private int e(int i2, int i3) {
        int i4 = i2 < this.u.getLeft() + this.o ? 1 : 0;
        if (i3 < this.u.getTop() + this.o) {
            i4 |= 4;
        }
        if (i2 > this.u.getRight() - this.o) {
            i4 |= 2;
        }
        return i3 > this.u.getBottom() - this.o ? i4 | 8 : i4;
    }

    private void e(int i2) {
        if (this.f1553d == null || !b(i2)) {
            return;
        }
        this.f1553d[i2] = 0.0f;
        this.f1554e[i2] = 0.0f;
        this.f1555f[i2] = 0.0f;
        this.f1556g[i2] = 0.0f;
        this.f1557h[i2] = 0;
        this.f1558i[i2] = 0;
        this.f1559j[i2] = 0;
        this.f1560k = (~(1 << i2)) & this.f1560k;
    }

    private void f(int i2) {
        float[] fArr = this.f1553d;
        if (fArr == null || fArr.length <= i2) {
            int i3 = i2 + 1;
            float[] fArr2 = new float[i3];
            float[] fArr3 = new float[i3];
            float[] fArr4 = new float[i3];
            float[] fArr5 = new float[i3];
            int[] iArr = new int[i3];
            int[] iArr2 = new int[i3];
            int[] iArr3 = new int[i3];
            float[] fArr6 = this.f1553d;
            if (fArr6 != null) {
                System.arraycopy(fArr6, 0, fArr2, 0, fArr6.length);
                float[] fArr7 = this.f1554e;
                System.arraycopy(fArr7, 0, fArr3, 0, fArr7.length);
                float[] fArr8 = this.f1555f;
                System.arraycopy(fArr8, 0, fArr4, 0, fArr8.length);
                float[] fArr9 = this.f1556g;
                System.arraycopy(fArr9, 0, fArr5, 0, fArr9.length);
                int[] iArr4 = this.f1557h;
                System.arraycopy(iArr4, 0, iArr, 0, iArr4.length);
                int[] iArr5 = this.f1558i;
                System.arraycopy(iArr5, 0, iArr2, 0, iArr5.length);
                int[] iArr6 = this.f1559j;
                System.arraycopy(iArr6, 0, iArr3, 0, iArr6.length);
            }
            this.f1553d = fArr2;
            this.f1554e = fArr3;
            this.f1555f = fArr4;
            this.f1556g = fArr5;
            this.f1557h = iArr;
            this.f1558i = iArr2;
            this.f1559j = iArr3;
        }
    }

    private void g() {
        float[] fArr = this.f1553d;
        if (fArr == null) {
            return;
        }
        Arrays.fill(fArr, BitmapDescriptorFactory.HUE_RED);
        Arrays.fill(this.f1554e, BitmapDescriptorFactory.HUE_RED);
        Arrays.fill(this.f1555f, BitmapDescriptorFactory.HUE_RED);
        Arrays.fill(this.f1556g, BitmapDescriptorFactory.HUE_RED);
        Arrays.fill(this.f1557h, 0);
        Arrays.fill(this.f1558i, 0);
        Arrays.fill(this.f1559j, 0);
        this.f1560k = 0;
    }

    private boolean g(int i2) {
        if (b(i2)) {
            return true;
        }
        Log.e("ViewDragHelper", "Ignoring pointerId=" + i2 + " because ACTION_DOWN was not received for this pointer before ACTION_MOVE. It likely happened because  ViewDragHelper did not receive all the events in the event stream.");
        return false;
    }

    private void h() {
        this.f1561l.computeCurrentVelocity(1000, this.m);
        a(a(this.f1561l.getXVelocity(this.f1552c), this.n, this.m), a(this.f1561l.getYVelocity(this.f1552c), this.n, this.m));
    }

    public void a() {
        b();
        if (this.a == 2) {
            int currX = this.q.getCurrX();
            int currY = this.q.getCurrY();
            this.q.abortAnimation();
            int currX2 = this.q.getCurrX();
            int currY2 = this.q.getCurrY();
            this.r.a(this.s, currX2, currY2, currX2 - currX, currY2 - currY);
        }
        c(0);
    }

    public void a(float f2) {
        this.n = f2;
    }

    public void a(MotionEvent motionEvent) {
        int i2;
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            b();
        }
        if (this.f1561l == null) {
            this.f1561l = VelocityTracker.obtain();
        }
        this.f1561l.addMovement(motionEvent);
        int i3 = 0;
        if (actionMasked == 0) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int pointerId = motionEvent.getPointerId(0);
            View b2 = b((int) x, (int) y);
            b(x, y, pointerId);
            b(b2, pointerId);
            int i4 = this.f1557h[pointerId];
            int i5 = this.p;
            if ((i4 & i5) != 0) {
                this.r.b(i4 & i5, pointerId);
                return;
            }
            return;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                if (this.a != 1) {
                    int pointerCount = motionEvent.getPointerCount();
                    while (i3 < pointerCount) {
                        int pointerId2 = motionEvent.getPointerId(i3);
                        if (g(pointerId2)) {
                            float x2 = motionEvent.getX(i3);
                            float y2 = motionEvent.getY(i3);
                            float f2 = x2 - this.f1553d[pointerId2];
                            float f3 = y2 - this.f1554e[pointerId2];
                            a(f2, f3, pointerId2);
                            if (this.a != 1) {
                                View b3 = b((int) x2, (int) y2);
                                if (a(b3, f2, f3) && b(b3, pointerId2)) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        i3++;
                    }
                } else {
                    if (!g(this.f1552c)) {
                        return;
                    }
                    int findPointerIndex = motionEvent.findPointerIndex(this.f1552c);
                    float x3 = motionEvent.getX(findPointerIndex);
                    float y3 = motionEvent.getY(findPointerIndex);
                    float[] fArr = this.f1555f;
                    int i6 = this.f1552c;
                    int i7 = (int) (x3 - fArr[i6]);
                    int i8 = (int) (y3 - this.f1556g[i6]);
                    a(this.s.getLeft() + i7, this.s.getTop() + i8, i7, i8);
                }
                c(motionEvent);
                return;
            }
            if (actionMasked != 3) {
                if (actionMasked == 5) {
                    int pointerId3 = motionEvent.getPointerId(actionIndex);
                    float x4 = motionEvent.getX(actionIndex);
                    float y4 = motionEvent.getY(actionIndex);
                    b(x4, y4, pointerId3);
                    if (this.a != 0) {
                        if (c((int) x4, (int) y4)) {
                            b(this.s, pointerId3);
                            return;
                        }
                        return;
                    } else {
                        b(b((int) x4, (int) y4), pointerId3);
                        int i9 = this.f1557h[pointerId3];
                        int i10 = this.p;
                        if ((i9 & i10) != 0) {
                            this.r.b(i9 & i10, pointerId3);
                            return;
                        }
                        return;
                    }
                }
                if (actionMasked != 6) {
                    return;
                }
                int pointerId4 = motionEvent.getPointerId(actionIndex);
                if (this.a == 1 && pointerId4 == this.f1552c) {
                    int pointerCount2 = motionEvent.getPointerCount();
                    while (true) {
                        if (i3 >= pointerCount2) {
                            i2 = -1;
                            break;
                        }
                        int pointerId5 = motionEvent.getPointerId(i3);
                        if (pointerId5 != this.f1552c) {
                            View b4 = b((int) motionEvent.getX(i3), (int) motionEvent.getY(i3));
                            View view = this.s;
                            if (b4 == view && b(view, pointerId5)) {
                                i2 = this.f1552c;
                                break;
                            }
                        }
                        i3++;
                    }
                    if (i2 == -1) {
                        h();
                    }
                }
                e(pointerId4);
                return;
            }
            if (this.a == 1) {
                a(BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_RED);
            }
        } else if (this.a == 1) {
            h();
        }
        b();
    }

    public void a(View view, int i2) {
        if (view.getParent() == this.u) {
            this.s = view;
            this.f1552c = i2;
            this.r.a(view, i2);
            c(1);
            return;
        }
        throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.u + ")");
    }

    public boolean a(int i2) {
        int length = this.f1553d.length;
        for (int i3 = 0; i3 < length; i3++) {
            if (a(i2, i3)) {
                return true;
            }
        }
        return false;
    }

    public boolean a(int i2, int i3) {
        if (!b(i3)) {
            return false;
        }
        boolean z = (i2 & 1) == 1;
        boolean z2 = (i2 & 2) == 2;
        float f2 = this.f1555f[i3] - this.f1553d[i3];
        float f3 = this.f1556g[i3] - this.f1554e[i3];
        if (!z || !z2) {
            return z ? Math.abs(f2) > ((float) this.b) : z2 && Math.abs(f3) > ((float) this.b);
        }
        float f4 = (f2 * f2) + (f3 * f3);
        int i4 = this.b;
        return f4 > ((float) (i4 * i4));
    }

    public boolean a(View view, int i2, int i3) {
        return view != null && i2 >= view.getLeft() && i2 < view.getRight() && i3 >= view.getTop() && i3 < view.getBottom();
    }

    public boolean a(boolean z) {
        if (this.a == 2) {
            boolean computeScrollOffset = this.q.computeScrollOffset();
            int currX = this.q.getCurrX();
            int currY = this.q.getCurrY();
            int left = currX - this.s.getLeft();
            int top = currY - this.s.getTop();
            if (left != 0) {
                v.d(this.s, left);
            }
            if (top != 0) {
                v.e(this.s, top);
            }
            if (left != 0 || top != 0) {
                this.r.a(this.s, currX, currY, left, top);
            }
            if (computeScrollOffset && currX == this.q.getFinalX() && currY == this.q.getFinalY()) {
                this.q.abortAnimation();
                computeScrollOffset = false;
            }
            if (!computeScrollOffset) {
                if (z) {
                    this.u.post(this.v);
                } else {
                    c(0);
                }
            }
        }
        return this.a == 2;
    }

    public View b(int i2, int i3) {
        for (int childCount = this.u.getChildCount() - 1; childCount >= 0; childCount--) {
            ViewGroup viewGroup = this.u;
            this.r.a(childCount);
            View childAt = viewGroup.getChildAt(childCount);
            if (i2 >= childAt.getLeft() && i2 < childAt.getRight() && i3 >= childAt.getTop() && i3 < childAt.getBottom()) {
                return childAt;
            }
        }
        return null;
    }

    public void b() {
        this.f1552c = -1;
        g();
        VelocityTracker velocityTracker = this.f1561l;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.f1561l = null;
        }
    }

    public boolean b(int i2) {
        return ((1 << i2) & this.f1560k) != 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x00dd, code lost:
    
        if (r12 != r11) goto L54;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean b(android.view.MotionEvent r17) {
        /*
            Method dump skipped, instructions count: 315
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: d.h.b.a.b(android.view.MotionEvent):boolean");
    }

    boolean b(View view, int i2) {
        if (view == this.s && this.f1552c == i2) {
            return true;
        }
        if (view == null || !this.r.b(view, i2)) {
            return false;
        }
        this.f1552c = i2;
        a(view, i2);
        return true;
    }

    public boolean b(View view, int i2, int i3) {
        this.s = view;
        this.f1552c = -1;
        boolean b2 = b(i2, i3, 0, 0);
        if (!b2 && this.a == 0 && this.s != null) {
            this.s = null;
        }
        return b2;
    }

    public View c() {
        return this.s;
    }

    void c(int i2) {
        this.u.removeCallbacks(this.v);
        if (this.a != i2) {
            this.a = i2;
            this.r.c(i2);
            if (this.a == 0) {
                this.s = null;
            }
        }
    }

    public boolean c(int i2, int i3) {
        return a(this.s, i2, i3);
    }

    public int d() {
        return this.o;
    }

    public void d(int i2) {
        this.p = i2;
    }

    public boolean d(int i2, int i3) {
        if (this.t) {
            return b(i2, i3, (int) this.f1561l.getXVelocity(this.f1552c), (int) this.f1561l.getYVelocity(this.f1552c));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }

    public int e() {
        return this.b;
    }

    public int f() {
        return this.a;
    }
}
