package com.zyyoona7.demo.ninephoto;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zyyoona7.ninegrid.NineGridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/9/7.
 */
public class Main2Adapter extends BaseQuickAdapter<ContentEntity, BaseViewHolder> {

    private OnPhotoClickListener mOnPhotoClickListener;

    public Main2Adapter() {
        super(R.layout.item_main2, null);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ContentEntity item) {
        NineGridLayout nineGridLayout = helper.getView(R.id.ngl_photo);
        GridAdapter adapter = new GridAdapter();
        nineGridLayout.setAdapter(adapter);
        final List<String> photoList = item.getPhotoList();
        adapter.setDataList(photoList);
        if (photoList == null || photoList.size() <= 0) {
            nineGridLayout.setVisibility(View.GONE);
        } else {
            nineGridLayout.setVisibility(View.VISIBLE);

            nineGridLayout.setOnItemClickListener(new NineGridLayout.OnItemClickListener() {
                @Override
                public void onItemClick(NineGridLayout nineGridLayout, View childView, int position) {
                    if (mOnPhotoClickListener != null) {
                        ArrayList<String> photoArrayList = new ArrayList<>(1);
                        photoArrayList.addAll(photoList);
                        mOnPhotoClickListener.onPhotoClick(nineGridLayout, position, helper.getAdapterPosition(), photoArrayList);
                    }
                }
            });
        }

    }

    public void setPhotoItemClickListener(OnPhotoClickListener listener) {
        mOnPhotoClickListener = listener;
    }

    public interface OnPhotoClickListener {

        void onPhotoClick(NineGridLayout gridLayout, int nglPos,
                          int rvPos, ArrayList<String> photoList);
    }
}
