package com.zyyoona7.demo.ninephoto.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.gif.GifDrawable;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/9/3.
 */
public class ZGifDrawable extends GifDrawable {

    private static final String TAG = "ZGifDrawable";

    public ZGifDrawable(Context context, GifDecoder gifDecoder, BitmapPool bitmapPool, Transformation<Bitmap> frameTransformation, int targetFrameWidth, int targetFrameHeight, Bitmap firstFrame) {
        super(context, gifDecoder, bitmapPool, frameTransformation, targetFrameWidth, targetFrameHeight, firstFrame);
    }

    public ZGifDrawable(Context context, GifDecoder gifDecoder, Transformation<Bitmap> frameTransformation, int targetFrameWidth, int targetFrameHeight, Bitmap firstFrame) {
        super(context, gifDecoder, frameTransformation, targetFrameWidth, targetFrameHeight, firstFrame);
    }

    @Override
    public void onFrameReady() {
        super.onFrameReady();
        Log.d(TAG, "onFrameReady: ");
    }
}
