package com.zyyoona7.ninegrid;

import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.View;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/31.
 */
public class NineGridViewHolder {

    private View mItemView;
    private SparseArray<View> mChildArray;

    public NineGridViewHolder(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("itemView can not be null.");
        }
        this.mItemView = itemView;
        mChildArray = new SparseArray<>(1);
    }

    /**
     * 获取View
     *
     * @param childId child id
     * @param <V>     泛型类型
     * @return View
     */
    public <V extends View> V findViewById(@IdRes int childId) {
        return getView(childId);
    }

    /**
     * 获取View
     *
     * @param childId child id
     * @param <V>     泛型类型
     * @return View
     */
    @SuppressWarnings("unchecked")
    public <V extends View> V getView(@IdRes int childId) {
        View child = mChildArray.get(childId);
        if (child == null) {
            child = mItemView.findViewById(childId);
            mChildArray.put(childId, child);
        }
        return (V) child;
    }
}
