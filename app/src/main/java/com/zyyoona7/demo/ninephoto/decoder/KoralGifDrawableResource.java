package com.zyyoona7.demo.ninephoto.decoder;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.Resource;

import pl.droidsonroids.gif.GifDrawable;

public class KoralGifDrawableResource implements Resource<GifDrawable>, Initializable {

    private GifDrawable gifDrawable;

    public KoralGifDrawableResource(GifDrawable gifDrawable) {
      this.gifDrawable = gifDrawable;
    }

    @NonNull
    @Override
    public Class<GifDrawable> getResourceClass() {
      return GifDrawable.class;
    }

    @NonNull
    @Override
    public GifDrawable get() {
      return gifDrawable;
    }

    @Override
    public int getSize() {
      return gifDrawable.getFrameByteCount() * gifDrawable.getNumberOfFrames();
    }

    @Override
    public void recycle() {
      gifDrawable.stop();
      gifDrawable.recycle();
    }

    @Override
    public void initialize() {
      gifDrawable.seekToFrameAndGet(0).prepareToDraw();
    }
  }
