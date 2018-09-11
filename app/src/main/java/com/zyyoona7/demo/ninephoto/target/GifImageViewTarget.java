package com.zyyoona7.demo.ninephoto.target;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/9/4.
 */
public abstract class GifImageViewTarget<Z> extends ImageViewTarget<Z> {

    private int mIndex;

    public GifImageViewTarget(ImageView view, int index) {
        super(view);
        this.mIndex = index;
    }

    @Override
    protected void setResource(@Nullable Z resource) {
        setResource(resource, mIndex);
    }

    @Override
    public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
        if (transition == null || !transition.transition(resource, this)) {
            setResource(resource);
            setResourceReady();
        }
    }

    protected abstract void setResource(@Nullable Z resource, int index);

    protected void setResourceReady() {

    }

}
