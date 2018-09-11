package com.zyyoona7.demo.ninephoto.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.zyyoona7.demo.ninephoto.R;

/**
 * ImageView that scales like centerCrop, but instead of showing the centre of the image, it shows the top or bottom.
 */
public class BTCropImageView extends AppCompatImageView {

    public static final int CROP_TYPE_TOP = 0;
    public static final int CROP_TYPE_BOTTOM = 1;

    private int mCropType;

    public BTCropImageView(Context context) {
        this(context, null);
    }

    public BTCropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BTCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BTCropImageView);
        mCropType = typedArray.getInt(R.styleable.BTCropImageView_cropType, CROP_TYPE_TOP);
        typedArray.recycle();
        setScaleType(ScaleType.MATRIX);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mCropType == CROP_TYPE_TOP) {
            topCrop();
        } else {
            bottomCrop();
        }
    }

    @Override
    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom) {

        if (mCropType == CROP_TYPE_TOP) {
            topCrop();
        } else {
            bottomCrop();
        }
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);

    }

    private void topCrop() {
        if (getDrawable() == null) {
            return;
        }
        Matrix matrix = getImageMatrix();
        float scaleWidth = getWidth() / (float) getDrawable().getIntrinsicWidth();
        float scaleHeight = getHeight() / (float) getDrawable().getIntrinsicHeight();
        float scaleFactor = (scaleWidth > scaleHeight) ? scaleWidth : scaleHeight;
        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
        if (scaleFactor == scaleHeight) {
            float tanslateX = ((getDrawable().getIntrinsicWidth() * scaleFactor) - getWidth()) / 2;
            matrix.postTranslate(-tanslateX, 0);
        }
        setImageMatrix(matrix);
    }

    private void bottomCrop() {
        if (getDrawable() == null)
            return;

        Matrix matrix = getImageMatrix();

        float scale;
        int viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();
        //Get the scale
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        //Define the rect to take image portion from
        RectF drawableRect = new RectF(0, drawableHeight - (viewHeight / scale), drawableWidth, drawableHeight);
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);

        setImageMatrix(matrix);
    }

    public void setCropType(int cropType) {
        mCropType = cropType;
        requestLayout();
        invalidate();
    }
}