package com.zyyoona7.demo.ninephoto.uitls;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/31.
 */
public class Utils {

    public static float dp2px(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, Resources.getSystem().getDisplayMetrics());
    }

    public static float sp2px(float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,Resources.getSystem().getDisplayMetrics());
    }
}
