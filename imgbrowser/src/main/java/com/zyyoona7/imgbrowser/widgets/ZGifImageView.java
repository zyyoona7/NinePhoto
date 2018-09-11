package com.zyyoona7.imgbrowser.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.internal.DebugOverlay;
import com.alexvasilkov.gestures.internal.GestureDebug;
import com.alexvasilkov.gestures.utils.ClipHelper;
import com.alexvasilkov.gestures.views.interfaces.ClipBounds;
import com.alexvasilkov.gestures.views.interfaces.ClipView;
import com.zyyoona7.imgbrowser.StateView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/5.
 */
public class ZGifImageView extends GifImageView implements ClipView, ClipBounds, StateView,
        GestureController.OnGestureListener, GestureController.OnStateSourceChangeListener {

    private static final String TAG = "ZGifImageView";

    private final ClipHelper mClipViewHelper = new ClipHelper(this);
    private final ClipHelper mClipBoundsHelper = new ClipHelper(this);

    private final Matrix mMatrix = new Matrix();
    private final Matrix mMatrixInverse = new Matrix();

    private boolean mIsDown = false;
    private boolean mIsStopped = false;
    private GestureController.StateSource mStateSource;

    public ZGifImageView(Context context) {
        super(context);
    }

    public ZGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        mClipBoundsHelper.onPreDraw(canvas);
        mClipViewHelper.onPreDraw(canvas);
        super.draw(canvas);
        mClipViewHelper.onPostDraw(canvas);
        mClipBoundsHelper.onPostDraw(canvas);

        if (GestureDebug.isDrawDebugOverlay()) {
            DebugOverlay.drawDebug(this, canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(mMatrix);
        super.onDraw(canvas);
        canvas.restore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clipView(@Nullable RectF rect, float rotation) {
        mClipViewHelper.clipView(rect, rotation);
    }

    @Override
    public void clipBounds(@Nullable RectF rect) {
        mClipBoundsHelper.clipView(rect, 0f);
    }

    @Override
    public void applyState(State state) {
        if (!mIsStopped && mIsDown) {
            stopGif();
            mIsStopped = true;
        }
        state.get(mMatrix);
        mMatrix.invert(mMatrixInverse);
        invalidate();
    }

    @Override
    public void onDown(@NonNull MotionEvent event) {
        mIsDown = true;
    }

    @Override
    public void onUpOrCancel(@NonNull MotionEvent event) {
        mIsDown = false;
        //动画回弹时才继续播放Gif
        if (mStateSource != null && mStateSource == GestureController.StateSource.ANIMATION) {
            startGif();
        }
        mIsStopped = false;
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

    public void stopGif() {
        if (getDrawable() instanceof GifDrawable) {
            ((GifDrawable) getDrawable()).stop();
        }
    }

    public void startGif() {
        if (getDrawable() instanceof GifDrawable) {
            ((GifDrawable) getDrawable()).start();
        }
    }

    public void toggleGif() {
        if (getDrawable() instanceof GifDrawable) {
            GifDrawable gifDrawable = (GifDrawable) getDrawable();
            if (gifDrawable.isRunning()) {
                gifDrawable.stop();
            } else {
                gifDrawable.start();
            }
        }
    }

    @Override
    public void onStateSourceChanged(GestureController.StateSource source) {
        mStateSource = source;
    }
}
