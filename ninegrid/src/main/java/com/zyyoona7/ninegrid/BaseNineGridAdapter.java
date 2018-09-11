package com.zyyoona7.ninegrid;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/31.
 */
public abstract class BaseNineGridAdapter<T> implements NineGridLayout.NineGridAdapter {

    private static final String TAG = "BaseNineGridAdapter";

    private List<T> mDataList;
    private DataSetObservable mDataSetObservable;
    private int mLayoutRes;

    public BaseNineGridAdapter(@LayoutRes int layoutRes, List<T> dataList) {
        if (layoutRes != 0) {
            this.mLayoutRes = layoutRes;
        }
        mDataList = dataList;
        mDataSetObservable = new DataSetObservable();
    }

    /**
     * 根据position获取data
     *
     * @param position 下标
     * @return position data
     */
    public T getItemData(int position) {
        if (mDataList == null) {
            return null;
        }
        if (position >= 0 && position < mDataList.size()) {
            return mDataList.get(position);
        } else {
            return null;
        }
    }

    /**
     * 获取数据列表
     *
     * @return 数据列表
     */
    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 更新数据列表
     *
     * @param dataList 数据列表
     */
    public void setDataList(List<T> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        if (dataSetObserver == null) {
            return;
        }
        mDataSetObservable.registerObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        if (dataSetObserver == null) {
            return;
        }
        mDataSetObservable.unregisterObserver(dataSetObserver);
    }

    @NonNull
    @Override
    public View getView(ViewGroup viewGroup, View convertView, int position) {
        View contentView;
        NineGridViewHolder viewHolder;
        if (convertView == null) {
            contentView = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutRes, viewGroup, false);
            viewHolder = new NineGridViewHolder(contentView);
            contentView.setTag(R.id.tag_nine_grid_view_holder, viewHolder);
        } else {
            contentView = convertView;
            viewHolder = (NineGridViewHolder) contentView.getTag(R.id.tag_nine_grid_view_holder);
        }

        onBindViewHolder(viewGroup, viewHolder, mDataList.get(position), position);
        return contentView;
    }

    public abstract void onBindViewHolder(ViewGroup viewGroup, NineGridViewHolder viewHolder,
                                          T item, int position);
}
