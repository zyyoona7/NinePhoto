package com.zyyoona7.demo.ninephoto.base;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.zyyoona7.ninegrid.BaseNineGridAdapter;

import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/31.
 */
public abstract class BaseWrapGridAdapter<T> extends BaseNineGridAdapter<T> {

    public BaseWrapGridAdapter(int layoutRes, List<T> dataList) {
        super(layoutRes, dataList);
    }

    /**
     * 一张图时自适应
     *
     * @param imageView imageView
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     */
    protected void wrapWhenSingle(ImageView imageView, int maxWidth, int maxHeight) {
        if (getItemCount() == 1) {
            setAdjustViewBounds(imageView, true, maxWidth, maxHeight);
        } else {
            setAdjustViewBounds(imageView, false, 0, 0);
        }
    }

    /**
     * 一张图时自适应
     *
     * @param imageView imageView
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     */
    protected void wrapWhenSingle(ImageView imageView, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        if (getItemCount() == 1) {
            setAdjustViewBounds(imageView, true, maxWidth, maxHeight, scaleType);
        } else {
            setAdjustViewBounds(imageView, false, 0, 0, scaleType);
        }
    }

    /**
     * 设置是否自适应宽高
     *
     * @param imageView        imageView
     * @param adjustViewBounds 是否自适应宽高
     * @param maxWidth         最大宽度
     * @param maxHeight        最大高度
     */
    protected void setAdjustViewBounds(ImageView imageView, boolean adjustViewBounds, int maxWidth, int maxHeight) {
        setAdjustViewBounds(imageView, adjustViewBounds, maxWidth, maxHeight, getDefaultScaleType());
    }

    /**
     * 设置是否自适应宽高
     *
     * @param imageView        imageView
     * @param adjustViewBounds 是否自适应宽高
     * @param maxWidth         最大宽度
     * @param maxHeight        最大高度
     * @param scaleType        scaleType
     */
    protected void setAdjustViewBounds(ImageView imageView, boolean adjustViewBounds, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        if (imageView.getAdjustViewBounds() == adjustViewBounds) {
            if (adjustViewBounds) {
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                if (lp.width != ViewGroup.LayoutParams.WRAP_CONTENT && lp.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                    imageView.setLayoutParams(getWrapLayoutParams());
                }
            } else {
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                if (lp.width != ViewGroup.LayoutParams.MATCH_PARENT && lp.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    imageView.setLayoutParams(getMatchLayoutParams());
                }
            }
            return;
        }
        imageView.setAdjustViewBounds(adjustViewBounds);
        if (adjustViewBounds) {
            imageView.setMaxWidth(maxWidth);
            imageView.setMaxHeight(maxHeight);
        } else {
            imageView.setMaxWidth(Integer.MAX_VALUE);
            imageView.setMaxHeight(Integer.MAX_VALUE);
            imageView.setScaleType(scaleType);
            imageView.setLayoutParams(getMatchLayoutParams());
        }
    }

    /**
     * 获取默认ScaleType
     *
     * @return ScaleType
     */
    protected ImageView.ScaleType getDefaultScaleType() {
        return ImageView.ScaleType.CENTER_CROP;
    }

    /**
     * 获取自适应时LayoutParams
     *
     * @return layoutParams
     */
    protected ViewGroup.LayoutParams getWrapLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 获取填充时layoutParams
     *
     * @return layoutParams
     */
    protected ViewGroup.LayoutParams getMatchLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
