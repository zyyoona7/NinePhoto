package com.zyyoona7.imgbrowser.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.internal.DebugOverlay;
import com.alexvasilkov.gestures.internal.GestureDebug;
import com.alexvasilkov.gestures.utils.ClipHelper;
import com.alexvasilkov.gestures.views.interfaces.ClipBounds;
import com.alexvasilkov.gestures.views.interfaces.ClipView;
import com.zyyoona7.imgbrowser.StateView;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/6.
 */
public class ZImageView extends AppCompatImageView implements ClipView, ClipBounds, StateView {

    private final ClipHelper clipViewHelper = new ClipHelper(this);
    private final ClipHelper clipBoundsHelper = new ClipHelper(this);

    private final Matrix matrix = new Matrix();
    private final Matrix matrixInverse = new Matrix();

    public ZImageView(Context context) {
        super(context);
    }

    public ZImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        clipBoundsHelper.onPreDraw(canvas);
        clipViewHelper.onPreDraw(canvas);
        super.draw(canvas);
        clipViewHelper.onPostDraw(canvas);
        clipBoundsHelper.onPostDraw(canvas);

        if (GestureDebug.isDrawDebugOverlay()) {
            DebugOverlay.drawDebug(this, canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(matrix);
        super.onDraw(canvas);
        canvas.restore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clipView(@Nullable RectF rect, float rotation) {
        clipViewHelper.clipView(rect, rotation);
    }

    @Override
    public void clipBounds(@Nullable RectF rect) {
        clipBoundsHelper.clipView(rect, 0f);
    }

    @Override
    public void applyState(State state) {
        state.get(matrix);
        matrix.invert(matrixInverse);
        invalidate();
    }
}
