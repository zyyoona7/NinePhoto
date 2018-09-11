package com.zyyoona7.demo.ninephoto;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.zyyoona7.demo.ninephoto.decoder.KoralStreamGifDecoder;

import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/9/3.
 */
@com.bumptech.glide.annotation.GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
//        super.registerComponents(context, glide, registry);
//        final KoralFileGifDecoder koralFileGifDecoder = new KoralFileGifDecoder(
//                registry.getImageHeaderParsers(), glide.getArrayPool());
//        registry.prepend(/*Data:*/File.class, /*Resource:*/GifDrawable.class, koralFileGifDecoder);
        final KoralStreamGifDecoder koralStreamGifDecoder = new KoralStreamGifDecoder(
                registry.getImageHeaderParsers(), glide.getArrayPool());
        registry.prepend(/*Data:*/InputStream.class, /*Resource:*/GifDrawable.class, koralStreamGifDecoder);
    }

}
