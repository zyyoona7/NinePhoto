package com.zyyoona7.demo.ninephoto;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zyyoona7.demo.ninephoto.base.BaseWrapGridAdapter;
import com.zyyoona7.demo.ninephoto.target.GifImageViewTarget;
import com.zyyoona7.demo.ninephoto.uitls.ImageUtils;
import com.zyyoona7.demo.ninephoto.uitls.Utils;
import com.zyyoona7.ninegrid.NineGridViewHolder;

import java.lang.reflect.Field;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/8/31.
 */
public class GridAdapter extends BaseWrapGridAdapter<String> {

    private static final String TAG = "GridAdapter";
    private GifController mGifController;

    public GridAdapter() {
        super(R.layout.item_nine, null);
        mGifController = new GifController();
    }

    @Override
    protected ViewGroup.LayoutParams getWrapLayoutParams() {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams getMatchLayoutParams() {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onBindViewHolder(ViewGroup viewGroup, NineGridViewHolder viewHolder, String item, int position) {
        final AppCompatImageView ivNine = viewHolder.getView(R.id.iv_nine);
//        final GifImageView ivGif = viewHolder.getView(R.id.iv_gif);
        int max = (int) Utils.dp2px(200f);
        wrapWhenSingle(ivNine, max, max);
//        wrapWhenSingle(ivGif,max,max, ImageView.ScaleType.FIT_XY);
//        if (ImageUtils.isImageGif(item)) {
//            ivGif.setVisibility(View.VISIBLE);
//            ivNine.setVisibility(View.GONE);
//            RequestOptions requestOptions = new RequestOptions()
//                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//            GlideApp.with(ivNine)
//                    .asGIF()
//                    .load(item)
//                    .disallowHardwareConfig()
//                    .apply(requestOptions)
//                    .into(new GifImageViewTarget<pl.droidsonroids.gif.GifDrawable>(ivNine,
//                            mGifController.getIncrementIndex(position)) {
//                        @Override
//                        protected void setResource(@Nullable pl.droidsonroids.gif.GifDrawable resource, int index) {
//                            if (resource == null) {
//                                return;
//                            }
//                            resource.setLoopCount(1);
//                            resource.seekToFrame(0);
//                            resource.stop();
//                            Log.d(TAG, "setResource: gifIndex=" + index);
//                            mGifController.addGif(index, resource);
//                            ivNine.setImageDrawable(resource);
//                        }
//
//                        @Override
//                        protected void setResourceReady() {
//                            playGif();
//                        }
//                    });
//        } else {
//            ivGif.setVisibility(View.GONE);
//            ivNine.setVisibility(View.VISIBLE);
            GlideApp.with(ivNine)
                    .setDefaultRequestOptions(new RequestOptions()
                    .dontAnimate())
                    .load(item)
                    .into(ivNine);
//        }
    }

    /**
     * 获取Gif持续时长
     *
     * @param gifDrawable GifDrawable
     * @return gif duration
     */
    protected int getGifDuration(GifDrawable gifDrawable) {
        if (gifDrawable == null) {
            return 0;
        }
        int frameCount = gifDrawable.getFrameCount();
        GifDecoder gifDecoder = getGifDecoder(gifDrawable);
        int duration = 0;
        if (gifDecoder != null) {
            for (int i = 0; i < frameCount; i++) {
                duration += gifDecoder.getDelay(i);
            }
        }
        return duration;
    }

    /**
     * 反射获取GifDecoder
     *
     * @param gifDrawable gifDrawable
     * @return GifDecoder
     */
    protected GifDecoder getGifDecoder(GifDrawable gifDrawable) {
        try {
            Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
            Field frameLoaderField = gifStateClass.getDeclaredField("frameLoader");
            frameLoaderField.setAccessible(true);
            Object frameLoader = frameLoaderField.get(gifDrawable.getConstantState());

            Class frameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
            Field gifDecoderField = frameLoaderClass.getDeclaredField("gifDecoder");
            gifDecoderField.setAccessible(true);
            return (GifDecoder) gifDecoderField.get(frameLoader);
        } catch (Exception e) {
            return null;
        }

    }

    public void playGif() {
        mGifController.playGif();
    }

    public void stopGif() {
        mGifController.stopGif();
    }

    public void clearGif() {
        mGifController.clear();
    }
}
