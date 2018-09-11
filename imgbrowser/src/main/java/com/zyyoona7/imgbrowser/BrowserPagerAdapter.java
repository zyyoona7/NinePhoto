package com.zyyoona7.imgbrowser;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.view.ImageSaveCallback;
import com.zyyoona7.imgbrowser.widgets.ZBigImageView;
import com.zyyoona7.imgbrowser.widgets.ZGestureLayout;
import com.zyyoona7.imgbrowser.widgets.ZGifImageView;
import com.zyyoona7.imgbrowser.widgets.ZImageFactory;
import com.zyyoona7.imgbrowser.widgets.ZSSImageView;

import java.io.File;
import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/5.
 */
public class BrowserPagerAdapter extends RecyclePagerAdapter<BrowserPagerAdapter.PreviewHolder> {

    private List<String> mPhotoList;
    private static final String TAG = "BrowserPagerAdapter";

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private int mCurrentPosition = 0;
    private ProgressIndicatorFactory mIndicatorFactory;

    public BrowserPagerAdapter() {

    }

    /**
     * 获取图片List
     *
     * @return 图片List
     */
    public List<String> getPhotoList() {
        return mPhotoList;
    }

    /**
     * 设置图片List
     *
     * @param photoList 图片List
     */
    public void setPhotoList(List<String> photoList) {
        mPhotoList = photoList;
        notifyDataSetChanged();
    }

    /**
     * 移除指定Item
     *
     * @param position 下标
     */
    public void removeItem(int position) {
        if (mPhotoList != null && position >= 0 && position < mPhotoList.size()) {
            mPhotoList.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置Item点击监听器
     *
     * @param onItemClickListener 点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 设置Item长按监听器
     *
     * @param onItemLongClickListener 长按监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 设置图片加载指示器
     *
     * @param indicatorFactory 加载指示器工厂对象
     */
    public void setProgressIndicator(ProgressIndicatorFactory indicatorFactory) {
        mIndicatorFactory = indicatorFactory;
    }

    /**
     * 设置当前ViewPager显示的下标
     *
     * @param currentPosition 当前显示的下标
     */
    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }

    /**
     * 保存图片到相册
     *
     * @param position 下标
     * @param callback 监听回调
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void saveImageIntoGallery(int position, ImageSaveCallback callback) {
        PreviewHolder holder = getViewHolder(position);
        if (holder != null) {
            holder.saveImageIntoGallery(callback);
        }
    }

    @Override
    public PreviewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        PreviewHolder holder = new PreviewHolder(container);
        holder.onItemClickListener = mOnItemClickListener;
        holder.onItemLongClickListener = mOnItemLongClickListener;
        if (mIndicatorFactory != null) {
            holder.setProgressIndicator(mIndicatorFactory.getProgressIndicator());
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewHolder holder, int position) {
        String photoUrl = mPhotoList.get(position);
        View childView = holder.findImageView();
        if (childView != null) {
            childView.setVisibility(View.GONE);
        }
        holder.bigImageView.showImage(Uri.parse(photoUrl));
        holder.position = position;
        holder.currentPosition = mCurrentPosition;
    }

    @Override
    public void onRecycleViewHolder(@NonNull PreviewHolder holder) {
        super.onRecycleViewHolder(holder);
        //回收
        holder.cancel();
    }

    @Override
    public int getCount() {
        return mPhotoList == null ? 0 : mPhotoList.size();
    }

    static ZGestureLayout getGestureLayout(PreviewHolder holder) {
        return holder.gestureLayout;
    }

    static class PreviewHolder extends RecyclePagerAdapter.ViewHolder {

        ZGestureLayout gestureLayout;
        ZBigImageView bigImageView;
        int position;
        int currentPosition;
        OnItemClickListener onItemClickListener;
        OnItemLongClickListener onItemLongClickListener;

        public PreviewHolder(@NonNull View view) {
            super(new ZGestureLayout(view.getContext()));
            gestureLayout = (ZGestureLayout) itemView;
            ZBigImageView biv = new ZBigImageView(view.getContext());
            gestureLayout.addView(biv, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            bigImageView = biv;
            gestureLayout.getController().getSettings()
                    .setZoomEnabled(false)
                    .setDoubleTapEnabled(false);
            bigImageView.setOptimizeDisplay(true);
            bigImageView.setImageViewFactory(new ZImageFactory());

            bigImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            });

            bigImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        return onItemLongClickListener.onItemLongClick(v, position);
                    } else {
                        return false;
                    }
                }
            });

            bigImageView.setImageLoaderCallback(new ImageLoader.Callback() {
                @Override
                public void onCacheHit(int imageType, File image) {

                }

                @Override
                public void onCacheMiss(int imageType, File image) {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onSuccess(File image) {
                    ZGifImageView gifImageView = findGifImageView();
                    if (gifImageView != null && position != currentPosition) {
                        gifImageView.stopGif();
                    }
                }

                @Override
                public void onFail(Exception error) {

                }
            });
        }

        void setProgressIndicator(ProgressIndicator progressIndicator) {
            bigImageView.setProgressIndicator(progressIndicator);
        }

        void clear() {
            bigImageView.cancel();
            bigImageView.getSSIV().recycle();
        }

        void cancel() {
            bigImageView.cancel();
        }

        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        void saveImageIntoGallery(ImageSaveCallback callback) {
            bigImageView.setImageSaveCallback(callback);
            bigImageView.saveImageIntoGallery();
        }

        ZGifImageView findGifImageView() {
            int childCount = bigImageView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = bigImageView.getChildAt(i);
                if (childView.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (childView instanceof ZGifImageView) {
                    return (ZGifImageView) childView;
                }
            }
            return null;
        }

        View findImageView() {
            int childCount = bigImageView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = bigImageView.getChildAt(i);
                if (childView.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (childView instanceof ZGifImageView ||
                        childView instanceof ZSSImageView) {
                    return childView;
                }
            }
            return null;
        }

    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(View view, int position);
    }

    public interface ProgressIndicatorFactory {

        /**
         * must new instance
         *
         * @return new instance ProgressIndicator.
         */
        ProgressIndicator getProgressIndicator();
    }
}
