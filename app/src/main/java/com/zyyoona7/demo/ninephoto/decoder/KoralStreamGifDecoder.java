package com.zyyoona7.demo.ninephoto.decoder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.gif.GifOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @author Administrator
 * @date 2018/1/12 14:39
 */
public class KoralStreamGifDecoder implements ResourceDecoder<InputStream, GifDrawable> {

    private static final String TAG = "KoralStreamGifDecoder";

    private final List<ImageHeaderParser> parsers;
    private final ArrayPool byteArrayPool;

    public KoralStreamGifDecoder(List<ImageHeaderParser> parsers, ArrayPool byteArrayPool) {
        this.parsers = parsers;
        this.byteArrayPool = byteArrayPool;
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
        Boolean bool = options.get(GifOptions.DISABLE_ANIMATION);
        return bool != null && !bool
                && ImageHeaderParserUtils.getType(parsers, source, byteArrayPool) == ImageHeaderParser.ImageType.GIF;
    }

    @Override
    public Resource<GifDrawable> decode(@NonNull InputStream source, int width, int height,
                                        @NonNull Options options) throws IOException {
        byte[] data = inputStreamToBytes(source);
        if (data == null) {
            return null;
        }

        GifDrawable gifDrawable = new GifDrawable(data);
        return new KoralGifDrawableResource(gifDrawable);
    }

    private static byte[] inputStreamToBytes(InputStream is) {
        final int bufferSize = 16384;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bufferSize);
        try {
            int nRead;
            byte[] data = new byte[bufferSize];
            while ((nRead = is.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Error reading data from stream", e);
            }
            return null;
        }
        return buffer.toByteArray();
    }
}