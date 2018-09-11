package com.zyyoona7.imgbrowser.widgets;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.metadata.ImageInfoExtractor;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;

import java.io.File;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/5.
 */
public class ZImageFactory extends GlideImageViewFactory {

    @Override
    protected View createAnimatedImageView(final Context context, final int imageType,
                                           final File imageFile, int initScaleType) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF:
                ZGifImageView view = new ZGifImageView(context);
                view.setImageURI(Uri.parse("file://" + imageFile.getAbsolutePath()));
                view.setScaleType(BigImageView.scaleType(initScaleType));
                return view;
            default:
                return super.createAnimatedImageView(context, imageType, imageFile, initScaleType);
        }
    }

    @Override
    protected SubsamplingScaleImageView createStillImageView(Context context) {
        return new ZSSImageView(context);
    }

    @Override
    public View createThumbnailView(Context context, Uri thumbnail, int scaleType) {
        ZImageView thumbnailView = new ZImageView(context);
        thumbnailView.setScaleType(BigImageView.scaleType(scaleType));
        Glide.with(context)
                .load(thumbnail)
                .into(thumbnailView);
        return thumbnailView;
    }
}
