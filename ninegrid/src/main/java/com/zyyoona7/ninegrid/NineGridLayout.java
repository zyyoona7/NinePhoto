package com.zyyoona7.ninegrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/31.
 */
public class NineGridLayout extends ViewGroup {

    private static final int DEFAULT_ITEM_SPACING = 20;
    private static final float DEFAULT_SINGLE_RATIO = 0.75f;
    private static final int DEFAULT_COLUMN_NUM = 3;

    //单张图模式
    //宽高比模式
    public static final int SINGLE_MODE_RATIO = 0;
    //宽高自适应模式
    public static final int SINGLE_MODE_WRAP = 1;

    private int mColumnNum = DEFAULT_COLUMN_NUM;
    private int mItemSpacing;
    //可以手动设置
    private int mItemWidth;
    //item width是否明确
    private boolean mIsItemWidthExactly = false;
    //单张图片时宽度
    private int mSingleItemWidth;
    //单张图片时宽高比 宽/高
    private float mSingleRatio;
    //单张图片显示模式
    private int mSingleMode;
    //四张图时是否为田字格展示
    private boolean mIsFourSpecial;

    //Adapter
    private NineGridAdapter mAdapter;
    //数据监听
    private DataSetObserver mDataSetObserver;
    //缓存View
    private SparseArray<View> mCacheViewArray;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public NineGridLayout(Context context) {
        this(context, null);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
        init();
    }

    /**
     * 初始化属性
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout);
        mItemWidth = typedArray.getDimensionPixelOffset(R.styleable.NineGridLayout_ngl_itemWidth,
                0);
        mItemSpacing = typedArray.getDimensionPixelOffset(R.styleable.NineGridLayout_ngl_itemSpacing,
                DEFAULT_ITEM_SPACING);
        mSingleItemWidth = typedArray.getDimensionPixelOffset(
                R.styleable.NineGridLayout_ngl_singleItemWidth, 0);
        mSingleRatio = typedArray.getFloat(R.styleable.NineGridLayout_ngl_singleRatio,
                DEFAULT_SINGLE_RATIO);
        mSingleMode = typedArray.getInt(R.styleable.NineGridLayout_ngl_singleMode, SINGLE_MODE_WRAP);
        mIsFourSpecial = typedArray.getBoolean(R.styleable.NineGridLayout_ngl_fourSpecial, true);
        typedArray.recycle();
    }

    /**
     * 初始化
     */
    private void init() {
        mDataSetObserver = createDataSetObserver();
        mCacheViewArray = new SparseArray<>(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getChildCount() == 1) {
            if (mSingleMode == SINGLE_MODE_RATIO && mSingleItemWidth > 0
                    && mSingleRatio > 0) {
                measureSingleRatioChild(widthMeasureSpec, heightMeasureSpec);
            } else {
                measureWrapChildren(widthMeasureSpec, heightMeasureSpec, false);
            }
        } else if (mItemWidth > 0) {
            int maxWidth = mItemWidth * mColumnNum + getHorizontalItemSpacing()
                    + getHorizontalPadding();
            measureGridChild(maxWidth, widthMeasureSpec, heightMeasureSpec, true);
        } else {
            measureWrapChildren(widthMeasureSpec, heightMeasureSpec, true);
        }
    }

    /**
     * 测量单张图时比例模式
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    private void measureSingleRatioChild(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = mSingleItemWidth;
        int maxHeight = (int) (mSingleItemWidth / mSingleRatio);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        maxWidth += getHorizontalPadding();
        maxHeight += getVerticalPadding();
        //根据测量模式获取实际宽度
        maxWidth = Math.min(maxWidth, width);
        maxHeight = Math.min(maxHeight, height);
        int layoutWidth = maxWidth - getHorizontalPadding();
        int layoutHeight = maxHeight - getVerticalPadding();
        float ratio = layoutWidth * 1.0f / layoutHeight;
        if (ratio > mSingleRatio) {
            //高度比期望的短
            maxWidth = (int) (layoutHeight * mSingleRatio) + getHorizontalPadding();
        } else if (ratio < mSingleRatio) {
            //宽度比期望的短
            maxHeight = (int) (layoutWidth / mSingleRatio) + getVerticalPadding();
        }

        //精确测量
        measureChildrenExactly(maxWidth - getHorizontalPadding(),
                maxHeight - getVerticalPadding());
        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
    }

    /**
     * 测量网格时子View
     *
     * @param maxWidth          最大宽度
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     * @param isItemExactly     itemWidth是否确定
     */
    private void measureGridChild(int maxWidth, int widthMeasureSpec, int heightMeasureSpec,
                                  boolean isItemExactly) {
        int width = resolveSize(maxWidth, widthMeasureSpec);

        //确定宽度后，精确测量子View
        if (isItemExactly) {
            mItemWidth = getItemWidth(Math.min(width, maxWidth));
            mIsItemWidthExactly = true;
        } else {
            mItemWidth = getItemWidth(width);
        }
        if (mItemWidth > 0) {
            measureChildrenExactly(mItemWidth, mItemWidth);
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int rowNum = getRowNum();
        int resultHeight = mItemWidth * rowNum + mItemSpacing * (rowNum - 1) + getVerticalPadding();
        setMeasuredDimension(width,
                heightMode == MeasureSpec.EXACTLY ? height : resultHeight);
    }

    /**
     * 指定宽高，精确测量子View
     *
     * @param childWidth  子View宽度
     * @param childHeight 子View高度
     */
    private void measureChildrenExactly(int childWidth, int childHeight) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    /**
     * 测量子View 计算最大宽高
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec widthMeasureSpec
     * @param isGridChildren    是否是网格模式测量
     */
    private void measureWrapChildren(int widthMeasureSpec, int heightMeasureSpec, boolean isGridChildren) {
        int maxWidth = 0;
        int maxHeight = 0;
        int measuredRow = 0;
        int maxLineWidth = 0;
        int maxLineHeight = 0;
        int columnCount = getColumnCount();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) {
                continue;
            }
            //常规测量子View
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            //计算最大宽高
            int currentRow = i / columnCount;
            if (currentRow == measuredRow) {
                maxLineWidth += childView.getMeasuredWidth() + layoutParams.leftMargin +
                        layoutParams.rightMargin;
                maxLineHeight = Math.max(maxLineHeight, childView.getMeasuredHeight() +
                        layoutParams.topMargin + layoutParams.bottomMargin);
            } else {
                maxWidth = Math.max(maxLineWidth, maxWidth);
                measuredRow++;
                maxLineWidth = 0;
                maxLineWidth += childView.getMeasuredWidth() + layoutParams.leftMargin +
                        layoutParams.rightMargin;
                maxHeight += maxLineHeight;
                maxLineHeight = 0;
                maxLineHeight = Math.max(maxLineHeight, childView.getMeasuredHeight() +
                        layoutParams.topMargin + layoutParams.bottomMargin);
            }
        }

        maxWidth = Math.max(maxLineWidth, maxWidth);
        maxHeight += maxLineHeight;
        //最大宽高=测量的宽高+间距+padding
        int rowNum = getRowNum();
        maxWidth += getHorizontalItemSpacing() + getHorizontalPadding();
        maxHeight += mItemSpacing * (rowNum - 1) + getVerticalPadding();

        if (isGridChildren) {
            measureGridChild(maxWidth, widthMeasureSpec, heightMeasureSpec, false);
        } else {
            setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                    resolveSize(maxHeight, heightMeasureSpec));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() > 1 && (!mIsItemWidthExactly || mItemWidth <= 0)) {
            mItemWidth = getItemWidth(getWidth());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int columnCount = getColumnCount();
        int childWidth = mItemWidth;
        int childHeight = mItemWidth;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childCount == 1) {
                childWidth = childView.getMeasuredWidth();
                childHeight = childView.getMeasuredHeight();
            }
            int rowNum = i / columnCount;
            int columnNum = i % columnCount;
            int left = (childWidth + mItemSpacing) * columnNum + getPaddingLeft();
            int top = (childHeight + mItemSpacing) * rowNum + getPaddingTop();
            int right = left + childWidth;
            int bottom = top + childHeight;
            childView.layout(left, top, right, bottom);
        }
    }

    /**
     * 获取每个条目宽度
     *
     * @param width 总宽度
     * @return 每个条目宽度
     */
    private int getItemWidth(int width) {
        return (width - getHorizontalItemSpacing() -
                getPaddingLeft() - getPaddingRight()) / mColumnNum;
    }

    /**
     * 获取最大行数
     *
     * @return 最大行数
     */
    private int getRowNum() {
        int childCount = getChildCount();
        return childCount / mColumnNum + (childCount % mColumnNum == 0 ? 0 : 1);
    }

    /**
     * 获取横向padding
     *
     * @return 横向padding
     */
    private int getHorizontalPadding() {
        return getPaddingLeft() + getPaddingRight();
    }

    /**
     * 获取纵向padding
     *
     * @return 纵向padding
     */
    private int getVerticalPadding() {
        return getPaddingTop() + getPaddingBottom();
    }

    /**
     * 获取横向总间距
     *
     * @return 横向总间距
     */
    private int getHorizontalItemSpacing() {
        return mItemSpacing * (mColumnNum - 1);
    }

    /**
     * 获取布局和测量时列数，主要处理4个view的时候为2*2
     * <p>
     * 普通展现形式
     * | x | x | x |
     * | x |   |   |
     * <p>
     * 4个子View展示形式
     * | x | x |
     * | x | x |
     *
     * @return 列数
     */
    protected int getColumnCount() {
        int childCount = getChildCount();
        return mIsFourSpecial && childCount == 4 ? 2 : mColumnNum;
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException when called.
     *
     * @param child Ignored.
     * @throws UnsupportedOperationException Every time this method is invoked.
     */
    @Override
    public void addView(View child) {
        throw new UnsupportedOperationException("addView(View) is not supported in NineGridLayout");
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException when called.
     *
     * @param child Ignored.
     * @param index Ignored.
     * @throws UnsupportedOperationException Every time this method is invoked.
     */
    @Override
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("addView(View, int) is not supported in NineGridLayout");
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException when called.
     *
     * @param child  Ignored.
     * @param params Ignored.
     * @throws UnsupportedOperationException Every time this method is invoked.
     */
    @Override
    public void addView(View child, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, LayoutParams) "
                + "is not supported in NineGridLayout");
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException when called.
     *
     * @param child  Ignored.
     * @param index  Ignored.
     * @param params Ignored.
     * @throws UnsupportedOperationException Every time this method is invoked.
     */
    @Override
    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, int, LayoutParams) "
                + "is not supported in NineGridLayout");
    }


    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    public void setAdapter(NineGridAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mCacheViewArray.clear();
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
            fillViews();
        }
    }

    /**
     * 填充View
     */
    private void fillViews() {
        if (mAdapter == null) {
            return;
        }
        removeAllViewsInLayout();
        int itemCount = mAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View convertView = mCacheViewArray.get(i);
            View childView = mAdapter.getView(this, convertView, i);
            childView.setId(i);
            addViewInLayout(childView, -1, generateDefaultLayoutParams());
            addListener(childView);
            if (convertView == null) {
                mCacheViewArray.put(i, childView);
            }
        }
        requestLayout();
    }

    /**
     * 创建数据监听
     *
     * @return 数据监听器
     */
    private DataSetObserver createDataSetObserver() {
        return new DataSetObserver() {
            @Override
            public void onChanged() {
                fillViews();
            }
        };
    }

    /**
     * 为View添加监听器
     *
     * @param view view
     */
    private void addListener(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(NineGridLayout.this, v, v.getId());
                }
            }
        });

        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(NineGridLayout.this, v, v.getId());
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 设置child点击监听器
     *
     * @param listener 点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 设置child长按事件监听器
     *
     * @param listener 长按事件监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    /**
     * Adapter
     */
    interface NineGridAdapter {

        /**
         * 获取item总数
         *
         * @return item总数
         */
        int getItemCount();

        /**
         * 获取View
         *
         * @param viewGroup   viewGroup
         * @param convertView 可复用的View
         * @param position    下标
         * @return View
         */
        @NonNull
        View getView(ViewGroup viewGroup, View convertView, int position);

        /**
         * 刷新数据
         */
        void notifyDataSetChanged();

        /**
         * 注册数据监听
         *
         * @param dataSetObserver 数据监听
         */
        void registerDataSetObserver(DataSetObserver dataSetObserver);

        /**
         * 取消注册数据监听
         *
         * @param dataSetObserver 数据监听
         */
        void unregisterDataSetObserver(DataSetObserver dataSetObserver);
    }

    public interface OnItemClickListener {

        void onItemClick(NineGridLayout nineGridLayout, View childView, int position);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(NineGridLayout nineGridLayout, View childView, int position);
    }
}
