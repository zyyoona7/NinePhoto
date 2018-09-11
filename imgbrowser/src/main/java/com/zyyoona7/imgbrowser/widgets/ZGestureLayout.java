package com.zyyoona7.imgbrowser.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.GestureControllerForPager;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.internal.DebugOverlay;
import com.alexvasilkov.gestures.internal.GestureDebug;
import com.alexvasilkov.gestures.views.interfaces.AnimatorView;
import com.alexvasilkov.gestures.views.interfaces.ClipBounds;
import com.alexvasilkov.gestures.views.interfaces.ClipView;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.zyyoona7.imgbrowser.StateView;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/7.
 */
public class ZGestureLayout extends FrameLayout implements GestureView, AnimatorView,
        ClipView, ClipBounds {

    private final GestureControllerForPager controller;

    private ViewPositionAnimator positionAnimator;

    private final Matrix matrix = new Matrix();
    private final Matrix matrixInverse = new Matrix();

    private final RectF tmpFloatRect = new RectF();
    private final float[] tmpPointArray = new float[2];

    private MotionEvent currentMotionEvent;

    public ZGestureLayout(@NonNull Context context) {
        this(context, null);
    }

    public ZGestureLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZGestureLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        controller = new GestureControllerForPager(this);
        controller.getSettings().initFromAttributes(context, attrs);
        controller.addOnStateChangeListener(new GestureController.OnStateChangeListener() {
            @Override
            public void onStateChanged(State state) {
                applyState(state);
            }

            @Override
            public void onStateReset(State oldState, State newState) {
                applyState(newState);
            }
        });

        controller.setOnStateSourceChangeListener(new GestureController.OnStateSourceChangeListener() {
            @Override
            public void onStateSourceChanged(GestureController.StateSource source) {
                applyChildStateSourceChange(source);
            }
        });

        controller.setOnGesturesListener(new GestureController.SimpleOnGestureListener() {
            @Override
            public void onDown(@NonNull MotionEvent event) {
                super.onDown(event);
                applyChildOnDown(event);
            }

            @Override
            public void onUpOrCancel(@NonNull MotionEvent event) {
                super.onUpOrCancel(event);
                applyChildOnUpOrCancel(event);
            }

        });
    }

    private void applyChildStateSourceChange(GestureController.StateSource source) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof GestureController.OnStateSourceChangeListener) {
                ((GestureController.OnStateSourceChangeListener) childView).onStateSourceChanged(source);
            }
        }
    }

    private void applyChildOnDown(MotionEvent event) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof GestureController.OnGestureListener) {
                ((GestureController.OnGestureListener) childView).onDown(event);
            }
        }
    }

    private void applyChildOnUpOrCancel(MotionEvent event) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof GestureController.OnGestureListener) {
                ((GestureController.OnGestureListener) childView).onUpOrCancel(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GestureControllerForPager getController() {
        return controller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewPositionAnimator getPositionAnimator() {
        if (positionAnimator == null) {
            positionAnimator = new ViewPositionAnimator(this);
        }
        return positionAnimator;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        currentMotionEvent = event;
        // We should remap given event back to original coordinates
        // so that children can correctly respond to it
        MotionEvent invertedEvent = applyMatrix(event, matrixInverse);
        try {
            return super.dispatchTouchEvent(invertedEvent);
        } finally {
            invertedEvent.recycle();
        }
    }

    // It seems to be fine to use this method instead of suggested onDescendantInvalidated(...)
    @SuppressWarnings("deprecation")
    @Override
    public ViewParent invalidateChildInParent(int[] location, @NonNull Rect dirty) {
        // Invalidating correct rectangle
        applyMatrix(dirty, matrix);
        return super.invalidateChildInParent(location, dirty);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Passing original event to controller
        return controller.onInterceptTouch(this, currentMotionEvent);
    }

    @SuppressLint("ClickableViewAccessibility") // performClick() will be called by controller
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // Passing original event to controller
        return controller.onTouch(this, currentMotionEvent);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        controller.getSettings().setViewport(width - getPaddingLeft() - getPaddingRight(),
                height - getPaddingTop() - getPaddingBottom());
        controller.updateState();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        View child = getChildCount() == 0 ? null : getChildAt(0);
        if (child != null) {
            controller.getSettings().setImage(child.getMeasuredWidth(), child.getMeasuredHeight());
            controller.updateState();
        }
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int extraW = getPaddingLeft() + getPaddingRight()
                + lp.leftMargin + lp.rightMargin + widthUsed;
        final int extraH = getPaddingTop() + getPaddingBottom()
                + lp.topMargin + lp.bottomMargin + heightUsed;

        child.measure(getChildMeasureSpecFixed(parentWidthMeasureSpec, extraW, lp.width),
                getChildMeasureSpecFixed(parentHeightMeasureSpec, extraH, lp.height));
    }

    protected void applyState(State state) {
        applyChildState(state);
    }

    private void applyChildState(State state) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof StateView) {
                ((StateView) childView).applyState(state);
            }
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        if (GestureDebug.isDrawDebugOverlay()) {
            DebugOverlay.drawDebug(this, canvas);
        }
    }

    @Override
    public void addView(@NonNull View child, int index, @NonNull ViewGroup.LayoutParams params) {
        if (getChildCount() != 0) {
            throw new IllegalArgumentException("GestureFrameLayout can contain only one child");
        }
        super.addView(child, index, params);
    }


    private MotionEvent applyMatrix(MotionEvent event, Matrix matrix) {
        tmpPointArray[0] = event.getX();
        tmpPointArray[1] = event.getY();
        matrix.mapPoints(tmpPointArray);

        MotionEvent copy = MotionEvent.obtain(event);
        copy.setLocation(tmpPointArray[0], tmpPointArray[1]);
        return copy;
    }

    private void applyMatrix(Rect rect, Matrix matrix) {
        tmpFloatRect.set(rect.left, rect.top, rect.right, rect.bottom);
        matrix.mapRect(tmpFloatRect);
        rect.set(Math.round(tmpFloatRect.left), Math.round(tmpFloatRect.top),
                Math.round(tmpFloatRect.right), Math.round(tmpFloatRect.bottom));
    }


    protected static int getChildMeasureSpecFixed(int spec, int extra, int childDimension) {
        if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(spec), MeasureSpec.UNSPECIFIED);
        } else {
            return getChildMeasureSpec(spec, extra, childDimension);
        }
    }

    @Override
    public void clipBounds(@Nullable RectF rect) {
        applyChildClipBounds(rect);
    }

    private void applyChildClipBounds(RectF rect) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ClipBounds) {
                ((ClipBounds) childView).clipBounds(rect);
            }
        }
    }

    @Override
    public void clipView(@Nullable RectF rect, float rotation) {
        applyChildClipView(rect, rotation);
    }

    private void applyChildClipView(RectF rect, float rotation) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ClipView) {
                ((ClipView) childView).clipView(rect, rotation);
            }
        }
    }
}
