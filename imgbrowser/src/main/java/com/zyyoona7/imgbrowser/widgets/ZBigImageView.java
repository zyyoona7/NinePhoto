package com.zyyoona7.imgbrowser.widgets;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.views.interfaces.AnimatorView;
import com.alexvasilkov.gestures.views.interfaces.ClipBounds;
import com.alexvasilkov.gestures.views.interfaces.ClipView;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.github.piasy.biv.view.BigImageView;
import com.zyyoona7.imgbrowser.StateView;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/9/5.
 */
public class ZBigImageView extends BigImageView implements GestureView, AnimatorView,
        ClipView, ClipBounds, StateView, GestureController.OnGestureListener,
        GestureController.OnStateSourceChangeListener {

    private static final String TAG = "ZBigImageView";

    public ZBigImageView(Context context) {
        this(context, null);
    }

    public ZBigImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZBigImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @Override
    public void applyState(State state) {
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

    @Override
    public void onDown(@NonNull MotionEvent event) {
        applyChildOnDown(event);
    }

    @Override
    public void onUpOrCancel(@NonNull MotionEvent event) {
        applyChildOnUpOrCancel(event);
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent event) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent event) {

    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent event) {
        return false;
    }

    @Override
    public void onStateSourceChanged(GestureController.StateSource source) {
        applyChildStateSourceChange(source);
    }

    @Override
    public ViewPositionAnimator getPositionAnimator() {
        return null;
    }

    @Override
    public GestureController getController() {
        return null;
    }
}
