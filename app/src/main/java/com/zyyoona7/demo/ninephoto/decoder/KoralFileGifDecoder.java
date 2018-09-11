package com.zyyoona7.demo.ninephoto.decoder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

public class KoralFileGifDecoder implements ResourceDecoder<File, GifDrawable> {

    private final List<ImageHeaderParser> imageHeaderParsers;
    private final ArrayPool arrayPool;

    public KoralFileGifDecoder(List<ImageHeaderParser> imageHeaderParsers,
        ArrayPool arrayPool) {
      this.imageHeaderParsers = imageHeaderParsers;
      this.arrayPool = arrayPool;
    }

    @Override
    public boolean handles(@NonNull File source, @NonNull Options options) throws IOException {
      // TODO: Maybe it is too expensive to open stream each time?
      InputStream fileInputStream = null;
      try {
        fileInputStream = new FileInputStream(source);
        return ImageHeaderParserUtils.getType(imageHeaderParsers, fileInputStream, arrayPool)
            == ImageHeaderParser.ImageType.GIF;
      } finally {
        if (fileInputStream != null) {
          try {
            fileInputStream.close();
          } catch (IOException ignore) {
          }
        }
      }
    }

    @Nullable
    @Override
    public Resource<GifDrawable> decode(@NonNull File source, int width, int height,
                                        @NonNull Options options) throws IOException {
      return new KoralGifDrawableResource(new GifDrawable(source));
    }
  }
