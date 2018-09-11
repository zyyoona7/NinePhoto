package com.zyyoona7.demo.ninephoto.uitls;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class ImageUtils {

    private ImageUtils() {

    }

    public static String getMimeType(String url) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
    }

    public static boolean isBigImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int h = width * 3;
//        return height > h || (width > 1500 && height > 1500);
        return height > h ;
    }

    public static boolean isBigImage(int width, int height) {
        int h = width * 3;
        return height > h;
    }

    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        if (TextUtils.isEmpty(pictureType)) {
            return false;
        }
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
        }
        return false;
    }

    public static boolean isImageGif(String url) {
        return isGif(getMimeType(url));
    }
}
