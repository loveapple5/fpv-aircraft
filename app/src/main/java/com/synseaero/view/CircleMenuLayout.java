package com.synseaero.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.synseaero.fpv.R;
import com.synseaero.util.DensityUtil;

import java.util.HashMap;


public class CircleMenuLayout extends ViewGroup {

    private int mRadius;

    public static final float DEFAULT_BANNER_WIDTH = 750.0f;        //中间显示的图标大小
    public static final float DEFAULT_BANNER_HEIGHT = 420.0f;       //其余图标大小

    /**
     * 该容器内child item的默认尺寸
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 25.0f;

    private static final float RADIO_TOP_CHILD_DIMENSION = 25.0f;

    /**
     * 菜单的中心child的默认尺寸
     */
    private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private static final float RADIO_PADDING_LAYOUT = 20;


    private static final int RADIO_MARGIN_LAYOUT = 10;

    /**
     * menu和边框的距离，要小于RADIO_MARGIN_LAYOUT
     */
    private static final int RADIO_INTERNAL_MARGIN = 5;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;


    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;

    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 90;
    /**
     * 菜单项的图标
     */
    private int[] mItemImgs;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;

    /**
     * 菜单的个数
     */
    private int mMenuItemCount;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    //private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isTouchUp = true;

    /**
     * 当前中间顶部的那个从起始角度计算的位置
     */
    private int mCurrentPosition = 0;

    private int mCurrentIndex = 0;

    private Paint mLinePaint = new Paint();
    private Paint mTextPaint = new Paint();

    public static final float DEFAULT_TEXT_SIZE = 12;

    private HashMap<Integer, Integer> itemImgMap = new HashMap();

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        int color = 0;

        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.CircleMenuLayout);
        if (ta != null) {
            color = ta.getColor(R.styleable.CircleMenuLayout_menuColor, color);
        }

        // 无视padding
        setPadding(0, 0, 0, 0);
        mLinePaint.setStrokeWidth(2); // 设置圆环的宽度，这个应该是内边距
        mLinePaint.setAntiAlias(true); // 消除锯齿
        mLinePaint.setStyle(Paint.Style.STROKE); // 设置空心
//        mPaint.setColor(Color.MAGENTA); // 设置圆环的颜色
        mLinePaint.setColor(color);

        mTextPaint.setStrokeWidth(1); // 设置圆环的宽度，这个应该是内边距
        mTextPaint.setAntiAlias(true); // 消除锯齿
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(color);
        mTextPaint.setTextSize(DensityUtil.dip2px(context, DEFAULT_TEXT_SIZE));
    }

    public void setMenuColor(int color) {
        mLinePaint.setColor(color);
        mLinePaint.setColor(color);mTextPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置布局的宽高，并策略menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        double startAngle = mStartAngle;

        double angle = 360 / mMenuItemCount;
        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * 如果宽或者高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY
                || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度

            resWidth = getDefaultWidth();

            resHeight = (int) (resWidth * DEFAULT_BANNER_HEIGHT /
                    DEFAULT_BANNER_WIDTH);

        } else {
            // 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }

        setMeasuredDimension(resWidth, resHeight);

        // 获得直径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        // menu item数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize;

        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;

        boolean positionChanged = false;

        // 迭代测量：根据孩子的数量进行遍历，为每一个孩子测量大小，设置监听回调。
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            startAngle = startAngle % 360;
            if (startAngle > 269 && startAngle < 271 && isTouchUp) {
                if (mCurrentPosition != i) {
                    positionChanged = true;
                }
                mCurrentPosition = i;                       //本次使用mCurrentPosition，只是把他作为一个temp变量。可以有更多的使用，比如动态设置每个孩子相隔的角度
                childSize = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);            //设置大小
            } else {
                childSize = DensityUtil.dip2px(getContext(), RADIO_DEFAULT_CHILD_DIMENSION);
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
                    childMode);
            child.measure(makeMeasureSpec, makeMeasureSpec);
            startAngle += angle;
        }
        //item容器内边距
        mPadding = DensityUtil.dip2px(getContext(), RADIO_MARGIN_LAYOUT);

        if (positionChanged) {
            for (int i = 1; i < mMenuItemCount / 2; i++) {
                int imgPosition = itemImgMap.get(mCurrentPosition);
                int nextPosition = (mCurrentPosition + i) % mMenuItemCount;
                int nextImgPosition = (imgPosition + i) % mItemImgs.length;
                //Log.d("test", nextPosition + ":" + nextImgPosition);
                itemImgMap.put(nextPosition, nextImgPosition);
                int lastPosition = (mCurrentPosition - i + mMenuItemCount) % mMenuItemCount;
                int lastImgPosition = (imgPosition - i + mItemImgs.length ) % mItemImgs.length;
                itemImgMap.put(lastPosition, lastImgPosition);
                //Log.d("test", lastPosition + ":" + lastImgPosition);
                updateMenuItems();
            }


        }
    }

    /**
     * MenuItem的点击事件接口
     *
     * @author zhy
     */
    public interface OnMenuItemClickListener {
        void itemClick(int pos);

        void itemCenterClick(View view);
    }

    /**
     * MenuItem的点击事件接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    /**
     * 设置MenuItem的点击事件接口
     *
     * @param mOnMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }


    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布置圆的半径
        int layoutRadius = mRadius;
        // Laying out the child views
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth;

        // 根据menu item的个数，计算角度
        float angleDelay = 360 / mMenuItemCount;
        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            //根据孩子遍历，设置中间顶部那个的大小以及其他图片大小。
            if (mStartAngle > 269 && mStartAngle < 271 && isTouchUp) {
                if (child.getTag() != null) {
                    int index = (int) child.getTag();
                    mOnMenuItemClickListener.itemClick(index);              //设置监听回调。
                    mCurrentIndex = index;
                }
                cWidth = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);
                child.setSelected(true);
            } else {
                child.setTag(null);
                cWidth = DensityUtil.dip2px(getContext(), RADIO_DEFAULT_CHILD_DIMENSION);
                child.setSelected(false);

            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            //大于360就取余归于小于360度
            mStartAngle = mStartAngle % 360;

            //计算图片布置的中心点的圆半径。就是tmp
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
            // tmp cosa 即menu item中心点的横坐标。计算的是item的位置，是计算位置！！！
            left = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth)
                    /*+ DensityUtil.dip2px(getContext(), 1)*/;
            // tmp sina 即menu item的纵坐标
            top = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth) /*+ DensityUtil.dip2px(getContext(), 8)*/;
            //接着当然是布置孩子的位置啦，就是根据小圆的来布置的
            child.layout(left, top, left + cWidth, top + cWidth);

            // 叠加尺寸
            mStartAngle += angleDelay;
        }
    }

    //缓冲的角度。即我们将要固定几个位置，而不是任意位置。我们要设计一个可能的角度去自动帮他选择。
    private void backOrPre() {
        isTouchUp = true;
        float angleDelay = 360 / mMenuItemCount;              //这个是每个图形相隔的角度
        if ((mStartAngle - angleDelay) % angleDelay == 0) {
            return;
        }
        float angle = (float) ((mStartAngle - angleDelay) % angleDelay);                 //angle就是那个不是13度开始布局，然后是36度的整数的多出来的部分角度
        if (angleDelay > angle) {
            mStartAngle -= angle;
        } else if (angleDelay < angle) {
            mStartAngle = mStartAngle - angle + angleDelay;         //mStartAngle就是当前角度啦，取余36度就是多出来的角度，拿这个多出来的角度去数据处理。
        }
        requestLayout();
    }

    //item为奇数是用这个方法
//    private void backOrPre() {
//        isTouchUp = true;
//        float angleDelay = 360 / mMenuItemCount;              //这个是每个图形相隔的角度
//        if ((mStartAngle - angleDelay / 2) % angleDelay == 0) {
//            return;
//        }
//        float angle = (float) ((mStartAngle - angleDelay / 2) % angleDelay);                 //angle就是那个不是13度开始布局，然后是36度的整数的多出来的部分角度
//        if (angleDelay / 2 > angle) {
//            mStartAngle -= angle;
//        } else if (angleDelay / 2 < angle) {
//            mStartAngle = mStartAngle - angle + angleDelay;         //mStartAngle就是当前角度啦，取余36度就是多出来的角度，拿这个多出来的角度去数据处理。
//        }
//        requestLayout();
//    }


    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;


    //dispatchTouchEvent是处理触摸事件分发,事件(多数情况)是从Activity的dispatchTouchEvent开始的。执行super.dispatchTouchEvent(ev)，事件向下分发。
    //onTouchEvent是View中提供的方法，ViewGroup也有这个方法，view中不提供onInterceptTouchEvent。view中默认返回true，表示消费了这个事件。
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                //mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                isTouchUp = false;          //注意isTouchUp 这个标记量！！！
                /**
                 * 获得开始的角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度
                 */
                float end = getAngle(x, y);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else
                // 二、三象限，色角度值是付值
                {
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }
                // 重新布局
                if (mTmpAngle != 0) {
                    requestLayout();
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                backOrPre();
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }

    }


//    public void setAngle(int position) {
//        float angleDelay = 360 / mMenuItemCount;
//        if (position > mCurrentPosition) {
//            mStartAngle += (mCurrentPosition - position) * angleDelay;
//        } else {
//            mStartAngle -= (position - mCurrentPosition) * angleDelay;
//        }
//        requestLayout();
//    }

    public void setAngle(int positionDif) {
        float angleDelay = 360 / mMenuItemCount;
        mStartAngle += positionDif * angleDelay;
        requestLayout();
    }

    public void setMenuItemCount(int count) {
        if (360 % count != 0) {
            throw new IllegalArgumentException("item个数有误");
        }
        this.mMenuItemCount = count;
    }


    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIcons(int[] resIds) {
        mItemImgs = resIds;

        // 参数检查
        if (resIds == null) {
            throw new IllegalArgumentException("图片资源为空");
        }

        for (int i = 0; i < mMenuItemCount; i++) {
            itemImgMap.put(i, i % mItemImgs.length);
        }

        addMenuItems();
    }

    public void setMenuItemTexts(String[] resIds) {
        this.mItemTexts = resIds;
    }

    private void updateMenuItems() {
        for (int i = 0; i < mMenuItemCount; i++) {
            ImageView iv = (ImageView) getChildAt(i);
            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(mItemImgs[itemImgMap.get(i)]);
                iv.setTag(itemImgMap.get(i));
            }
        }
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {

        /**
         * 根据用户设置的参数，初始化view
         */
        for (int i = 0; i < mMenuItemCount; i++) {
            ImageView iv = new ImageView(getContext());
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(mItemImgs[itemImgMap.get(i)]);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                        isTouchUp = true;
                    Object tag = v.getTag();
                    if (tag != null) {
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });


            // 添加view到容器中
            addView(iv);
        }
    }

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param mFlingableValue
     */
    public void setFlingableValue(int mFlingableValue) {
        this.mFlingableValue = mFlingableValue;
    }

    /**
     * 设置内边距的比例
     *
     * @param mPadding
     */
    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2; // 获取大半径

        Log.d("onDraw", "width/2:" + center);
        int innerMargin = DensityUtil.dip2px(getContext(), RADIO_MARGIN_LAYOUT);
        Log.d("onDraw", "innerMargin:" + innerMargin);

        int childSize = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);
        Log.d("onDraw", "childSize:" + childSize);

        int internalSize = DensityUtil.dip2px(getContext(), RADIO_INTERNAL_MARGIN);
        Log.d("onDraw", "internalSize:" + internalSize);

        int radius = center - innerMargin * 2 + childSize / 2 + internalSize;// 大半径减去小半径的一半才是半径。
        int radius2 = center - innerMargin * 2 - childSize / 2 - internalSize;

        canvas.drawCircle(center, center, radius, mLinePaint); // 画出圆环
        canvas.drawCircle(center, center, radius2, mLinePaint); // 画出圆环

        if(mItemTexts != null && mItemTexts.length > mCurrentIndex) {
            int strLen = mItemTexts[mCurrentIndex].length();
            canvas.drawText(mItemTexts[mCurrentIndex], center - DensityUtil.dip2px(getContext(), DEFAULT_TEXT_SIZE) * strLen / 2,
                    center - DensityUtil.dip2px(getContext(), 20) - DensityUtil.dip2px(getContext(), DEFAULT_TEXT_SIZE), mTextPaint);
        }

    }


}
