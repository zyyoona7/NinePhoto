package com.zyyoona7.imgbrowser;

import android.Manifest;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.animation.ViewPosition;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker;
import com.alexvasilkov.gestures.utils.GravityUtils;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.ImageSaveCallback;
import com.zyyoona7.imgbrowser.widgets.ViewPagerFixed;
import com.zyyoona7.imgbrowser.widgets.ZGestureLayout;
import com.zyyoona7.imgbrowser.widgets.ZGifImageView;

import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/9/5.
 */
public class ImageBrowserLayout extends FrameLayout {

    private static final String TAG = "ImageBrowserLayout";

    // <0.7f向上滑动到极限会闪闪闪
    private static final float MIN_ZOOM_FACTOR = 0.7f;

    private final Point tmpPivot = new Point();

    @ColorInt
    private int mBgColor;
    private View mBgView;
    private ViewPager mViewPager;
    private BrowserPagerAdapter mPagerAdapter;
    private List<String> mPhotoList;
    private List<String> mPhotoPosList;
    private ViewsTransitionAnimator<Integer> mAnimator;
    private int mCurrentPosition = 0;

    private OnPositionUpdateListener mListener;

    public ImageBrowserLayout(@NonNull Context context) {
        this(context, null);
    }

    public ImageBrowserLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageBrowserLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageBrowserLayout);
        mBgColor = typedArray.getColor(R.styleable.ImageBrowserLayout_ibl_backgroundClr, Color.BLACK);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        mBgView = new View(context);
        mBgView.setBackgroundColor(mBgColor);
        mBgView.setAlpha(0f);
        mViewPager = new ViewPagerFixed(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mBgView, lp);
        addView(mViewPager, lp);
        mPagerAdapter = new BrowserPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mPagerAdapter.setCurrentPosition(mCurrentPosition);
                //优化Gif播放
                optimizedGifPlay(position);
            }
        });
    }

    /**
     * 优化Gif播放
     *
     * @param position 当前选中的下标
     */
    private void optimizedGifPlay(int position) {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            BrowserPagerAdapter.PreviewHolder holder = mPagerAdapter.getViewHolder(i);
            if (holder == null) {
                continue;
            }
            ZGifImageView gifImageView = holder.findGifImageView();
            if (gifImageView != null) {
                if (i == position) {
                    gifImageView.startGif();
                } else {
                    gifImageView.stopGif();
                }
            }
        }
    }

    /**
     * 设置数据
     *
     * @param photoList    图片地址列表
     * @param photoPosList 图片位置信息列表
     */
    public void setData(List<String> photoList, List<String> photoPosList) {
        setData(photoList, photoPosList, 0);
    }

    /**
     * 设置数据
     *
     * @param photoList       图片地址列表
     * @param photoPosList    图片位置信息列表
     * @param currentPosition 当前下标
     */
    public void setData(List<String> photoList, List<String> photoPosList, int currentPosition) {
        if (photoList == null || photoPosList == null || photoList.size() != photoPosList.size()) {
            return;
        }

        mPhotoList = photoList;
        mPagerAdapter.setPhotoList(mPhotoList);
        mPhotoPosList = photoPosList;
        if (currentPosition < 0) {
            currentPosition = 0;
        }
        if (currentPosition > mPagerAdapter.getCount()) {
            currentPosition = mPagerAdapter.getCount() - 1;
        }
        mCurrentPosition = currentPosition;
        initAnimator();
        if (mCurrentPosition > 0) {
            setCurrentItem(mCurrentPosition);
        }
    }

    /**
     * 初始化下拉手势动画
     */
    private void initAnimator() {
        final SimpleTracker pagerTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int position) {
                BrowserPagerAdapter.PreviewHolder holder = mPagerAdapter.getViewHolder(position);
                return holder == null ? null : BrowserPagerAdapter.getGestureLayout(holder);
            }
        };
        mAnimator = GestureTransitions.from(new ViewsTransitionAnimator.RequestListener<Integer>() {
            @Override
            public void onRequestView(@NonNull Integer integer) {
                getAnimator().setFromPos(integer, ViewPosition.unpack(mPhotoPosList.get(integer)));
            }
        }).into(mViewPager, pagerTracker);

        mAnimator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                applyImageAnimationState(position, isLeaving);
            }
        });
    }

    /**
     * 更新动画状态
     *
     * @param position  偏移
     * @param isLeaving 是否正在离开
     */
    private void applyImageAnimationState(float position, boolean isLeaving) {
        boolean isFinished = position == 0f && isLeaving; // Exit animation is finished

        mBgView.setAlpha(position);
        int visibility = isFinished ? View.INVISIBLE : View.VISIBLE;
        mBgView.setVisibility(visibility);
        setVisibility(visibility);

        zoomOnExit();

        if (mListener != null) {
            mListener.onPositionUpdate(position, isLeaving);
        }

        if (isFinished && mListener != null) {
            mListener.onFinished();
        }
    }

    /**
     * 滑动缩放
     */
    private void zoomOnExit() {
        ZGestureLayout view = (ZGestureLayout) mAnimator.getToView();

        if (view != null) {
            // Indirectly checking if we are in exit gesture
            float pos = view.getPositionAnimator().getPosition();
            float posTo = view.getPositionAnimator().getToPosition();
            boolean isExitGesture = pos < 1f && State.equals(pos, posTo);

            if (isExitGesture) {
                // Calculating new zoom level
                State state = view.getController().getState();
                float minZoom = view.getController().getStateController().getMinZoom(state);
                float zoom = minZoom * (MIN_ZOOM_FACTOR + pos * (1f - MIN_ZOOM_FACTOR));

                // Calculating pivot point
                GravityUtils.getDefaultPivot(view.getController().getSettings(), tmpPivot);

                // Applying new zoom level
                state.zoomTo(zoom, tmpPivot.x, tmpPivot.y);
                view.getController().updateState();
            }
        }
    }

    /**
     * 设置加载图片进度条指示器
     *
     * @param indicatorFactory 进度条指示器工厂对象
     */
    public void setProgressIndicator(
            BrowserPagerAdapter.ProgressIndicatorFactory indicatorFactory) {
        if (mPagerAdapter != null) {
            mPagerAdapter.setProgressIndicator(indicatorFactory);
        }
    }

    /**
     * 设置当前选中下标
     *
     * @param currentItem 当前下标
     */
    public void setCurrentItem(int currentItem) {
        if (isItemInRange(currentItem)) {
            mViewPager.setCurrentItem(currentItem);
        }
    }

    /**
     * 是否在范围区间内
     *
     * @param currentItem 当前下标
     * @return 是否在范围区间内
     */
    private boolean isItemInRange(int currentItem) {
        return currentItem >= 0 && currentItem < mPagerAdapter.getCount();
    }

    /**
     * 进入大图模式
     *
     * @param animate 是否带有动画
     */
    public void enterFullImage(final boolean animate) {
        runAfterImageDraw(new Runnable() {
            @Override
            public void run() {
                if (mAnimator == null) {
                    return;
                }
                mAnimator.enter(mCurrentPosition, animate);
            }
        });
    }

    /**
     * 退出大图模式
     *
     * @param animate 是否带动画
     * @return 是否执行退出
     */
    public boolean exitFullImage(boolean animate) {
        if (mAnimator != null && !mAnimator.isLeaving()) {
            mAnimator.exit(animate);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 动画是否正在推出
     *
     * @return 是否正在推出
     */
    public boolean isLeaving() {
        return mAnimator != null && mAnimator.isLeaving();
    }

    /**
     * Runs provided action after image is drawn for the first time.
     *
     * @param action action
     */
    public void runAfterImageDraw(final Runnable action) {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                runOnNextFrame(action);
                return true;
            }
        });
    }

    /**
     * Runs provided action after image is drawn for the first time.
     *
     * @param action action
     */
    private void runOnNextFrame(Runnable action) {
        final long frameLength = 17L; // 1 frame at 60 fps
        postDelayed(action, frameLength);
    }

    /**
     * 设置底层背景颜色
     *
     * @param bgColor 背景颜色
     */
    public void setBackgroundClr(@ColorInt int bgColor) {
        mBgColor = bgColor;
        mBgView.setBackgroundColor(bgColor);
    }

    /**
     * 设置底层背景颜色
     *
     * @param bgColorRes 背景颜色
     */
    public void setBackgroundClrRes(@ColorRes int bgColorRes) {
        setBackgroundClr(ContextCompat.getColor(getContext(), bgColorRes));
    }

    /**
     * 移除指定Item
     *
     * @param position 下标
     */
    public void removeItem(int position) {
        if (mPagerAdapter != null) {
            mPagerAdapter.removeItem(position);
        }
        mCurrentPosition = mViewPager.getCurrentItem();
        mPagerAdapter.setCurrentPosition(mCurrentPosition);
    }

    /**
     * 手势更新回调
     *
     * @param listener 回调
     */
    public void setOnGesturePositionUpdateListener(OnPositionUpdateListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置item点击监听
     *
     * @param onItemClickListener 点击监听器
     */
    public void setOnItemClickListener(BrowserPagerAdapter.OnItemClickListener onItemClickListener) {
        if (mPagerAdapter != null) {
            mPagerAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

    /**
     * 设置item长按监听事件
     *
     * @param onItemLongClickListener 长按监听器
     */
    public void setOnItemLongClickListener(BrowserPagerAdapter.OnItemLongClickListener onItemLongClickListener) {
        if (mPagerAdapter != null) {
            mPagerAdapter.setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    /**
     * 保存图片到相册
     *
     * @param position 下标
     * @param callback 保存图片回调
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void saveImageIntoGallery(int position, ImageSaveCallback callback) {
        if (mPagerAdapter != null) {
            mPagerAdapter.saveImageIntoGallery(position, callback);
        }
    }

    /**
     * 获取ViewPager对象
     *
     * @return viewPager
     */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * 获取当前下标
     *
     * @return 当前下标
     */
    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * 获取Item总数
     *
     * @return item总数
     */
    public int getItemCount() {
        return mPagerAdapter == null ? 0 : mPagerAdapter.getCount();
    }

    /**
     * 初始化BigImageViewer
     *
     * @param context 上下文
     */
    public static void initBigImageViewer(Context context) {
        BigImageViewer.initialize(GlideImageLoader.with(context.getApplicationContext()));
    }

    /**
     * 确定View的信息
     *
     * @param view view
     * @return pack过的View信息
     */
    public static String positionView(View view) {
        return ViewPosition.from(view).pack();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator = null;
        mPagerAdapter = null;
        mListener = null;
    }

    /**
     * 下拉手势更新回调
     */
    public interface OnPositionUpdateListener {

        /**
         * 位置变化
         *
         * @param position  偏移
         * @param isLeaving 是否正在离开
         */
        void onPositionUpdate(float position, boolean isLeaving);

        /**
         * 动画整体结束
         */
        void onFinished();
    }

    public static class SimplePositionUpdateListener implements OnPositionUpdateListener {

        @Override
        public void onPositionUpdate(float position, boolean isLeaving) {

        }

        @Override
        public void onFinished() {

        }
    }
}
