package com.zyyoona7.demo.ninephoto;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import pl.droidsonroids.gif.GifDrawable;

import static com.bumptech.glide.request.RequestOptions.decodeTypeOf;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/9/4.
 */
@GlideExtension
public class MyGlideExtensions {

    private MyGlideExtensions(){

    }

    private static final RequestOptions DECODE_TYPE_GIF = decodeTypeOf(GifDrawable.class).lock();

    @GlideType(GifDrawable.class)
    public static void asGIF(RequestBuilder<GifDrawable> requestBuilder) {
        requestBuilder
                .transition(new DrawableTransitionOptions())
                .apply(DECODE_TYPE_GIF);
    }

}
