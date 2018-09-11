package com.zyyoona7.demo.ninephoto;

import android.util.SparseArray;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/8/31.
 */
public class GifController implements AnimationListener {

    public static final int LOOP_INFINITY = -1;

    //保存GifDrawable，加载完Gif的时间不同，所以保存下标+GifDrawable
    private SparseArray<GifDrawable> mDrawableArray;
    //当前播放下标
    private int mCurrentIndex;
    //最大循环次数
    private int mMaxLoopCount = 1;
    //当前循环次数
    private int mLoopCount;
    //是否正在播放
    private boolean mIsPlaying;

    //递增的下标变量
    private int mIncrementIndex;
    //是否重置下标
    private boolean mIsNeedReset;
    //onBindViewHolder时的下标，判断是否是刷新
    private int mGridPosition;

    public GifController() {
        mDrawableArray = new SparseArray<>(1);
    }


    @Override
    public void onAnimationCompleted(int loopNumber) {
        mCurrentIndex++;
        if (mCurrentIndex >= mDrawableArray.size()) {
            mLoopCount++;
            if (canPlay()) {
                mCurrentIndex = 0;
            }
        }
        playGif(mCurrentIndex);
    }

    private boolean canPlay() {
        return mMaxLoopCount == LOOP_INFINITY || mLoopCount < mMaxLoopCount;
    }

    /**
     * 保存GifDrawable
     *
     * @param index       index使其始终保持有序
     * @param gifDrawable gifDrawable
     */
    public void addGif(int index, GifDrawable gifDrawable) {
        if (gifDrawable != null) {
            mDrawableArray.put(index, gifDrawable);
        }
        int indexOfValue = mDrawableArray.indexOfValue(gifDrawable);
        if (indexOfValue != -1) {

        }
    }

    /**
     * 从第一个开始播放
     */
    public void playGif() {
        if (mIsPlaying) {
            return;
        }
        mIsPlaying = true;
        playGif(0);
    }

    /**
     * 播放指定下标的Gif
     *
     * @param index 下标
     */
    public void playGif(int index) {
        //停止最后一次循环的最后一个
        if (index == mDrawableArray.size()) {
            stopGif(mDrawableArray.size() - 1);
            mIsPlaying = false;
        }
        if (mDrawableArray != null && mDrawableArray.size() > 0 && isIndexInRange(index)) {
            stopGif(index - 1);
            GifDrawable gifDrawable = mDrawableArray.get(index);
            if (gifDrawable == null) {
                return;
            }
            gifDrawable.setLoopCount(1);
            gifDrawable.addAnimationListener(this);
            if (gifDrawable.getCurrentFrameIndex() != 0) {
                gifDrawable.seekToFrame(0);
            }
            gifDrawable.start();
            mCurrentIndex = index;
        }
    }

    /**
     * 停止Gif播放
     */
    public void stopGif() {
        if (mDrawableArray != null && mDrawableArray.size() > 0) {
            for (int i = 0; i < mDrawableArray.size(); i++) {
                stopGif(i);
            }
        }
        mLoopCount = 0;
        mIsPlaying = false;
    }

    /**
     * 停止指定下标的Gif播放
     *
     * @param index 下标
     */
    public void stopGif(int index) {
        if (index < 0 && mLoopCount > 0 && canPlay()) {
            index = mDrawableArray.size() - 1;
        }
        if (mDrawableArray != null && mDrawableArray.size() > 0 && isIndexInRange(index)) {
            GifDrawable gifDrawable = mDrawableArray.get(index);
            if (gifDrawable == null) {
                return;
            }
            gifDrawable.seekToFrame(0);
            gifDrawable.removeAnimationListener(this);
            gifDrawable.stop();
        }
    }

    /**
     * 下标是否在范围内
     *
     * @param index 下标
     * @return 是否在范围内
     */
    private boolean isIndexInRange(int index) {
        return mDrawableArray != null && index < mDrawableArray.size() && index >= 0;
    }

    /**
     * 设置最大循环次数
     *
     * @param maxLoopCount 最大循环次数
     */
    public void setMaxLoopCount(int maxLoopCount) {
        this.mMaxLoopCount = maxLoopCount;
    }

    /**
     * 清空回收GifDrawable
     */
    public void clear() {
        if (mDrawableArray != null && mDrawableArray.size() > 0) {
            for (int i = 0; i < mDrawableArray.size(); i++) {
                GifDrawable gifDrawable = mDrawableArray.get(i);
                if (gifDrawable == null) {
                    continue;
                }
                gifDrawable.removeAnimationListener(this);
                gifDrawable.stop();
                gifDrawable.recycle();
            }
            mDrawableArray.clear();
        }
    }

    /**
     * 获取是否正在播放
     *
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * 获取递增的下标
     *
     * @return 递增下标
     */
    public int getIncrementIndex(int position) {
        updateGridPosition(position);
        return getIncrementIndex(mIsNeedReset);
    }

    /**
     * 获取递增的下标
     *
     * @param reset 是否重置
     * @return 递增下标
     */
    public int getIncrementIndex(boolean reset) {
        int index = reset ? mIncrementIndex = 0 : mIncrementIndex;
        if (reset) {
            clear();
            mIsNeedReset = false;
        }
        mIncrementIndex++;
        return index;
    }

    /**
     * 记录GridAdapter下标，判断是否重置递增下标
     *
     * @param position 下标
     */
    public void updateGridPosition(int position) {
        if (mGridPosition >= position) {
            mIsNeedReset = true;
        }
        mGridPosition = position;
    }
}
