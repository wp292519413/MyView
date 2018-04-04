package cn.bmkp.slidingbutton.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmkp.slidingbutton.R;

import static android.widget.RelativeLayout.TRUE;


/**
 * Created by wangpan on 2017/10/25.
 */

public class SlidingButton extends FrameLayout {

    public static final int NORMAL = 1;
    public static final int LOADING = 2;

    //滑条布局
    private RelativeLayout mBgSlider;
    //背景布局
    private RelativeLayout mBgButton;
    private ImageView mIvLoading;
    private ImageView mIvSlider;
    protected ImageView mIvTips;
    private TextView mTvCenterText;

    private int mSliderBackground;
    private int mButtonBackground;
    private CharSequence mCenterText;
    private Drawable mLoadingDrawable;
    private Drawable mSliderImageDrawable;
    private Drawable mTipsImageDrawable;

    private int mCenterTextColor;
    private float mCenterTextSize;
    private float mSliderImageSize;
    private int mBgMinWidth;            //底层最小宽度
    private float mTipsImageSize;
    private float mLoadingSize;
    //中间文本textView id
    private int mCenterTextId;

    private int leftMargin = 14;

    //当前状态
    private int mState;
    //滑块宽度
    private int mSliderWidth;
    private int mLeftBorder;
    private int mRightBorder;
    private int mLastDownX;
    protected int mRange;

    private OnStateChangedListener mOnStateChangedListener;
    protected ViewDragHelper mViewDragHelper;

    public SlidingButton(Context context) {
        this(context, null);
    }

    public SlidingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingButton);
        //滑块背景(第一层)
        mSliderBackground = typedArray.getResourceId(R.styleable.SlidingButton_slider_background, 0);
        //按钮背景(第二层)
        mButtonBackground = typedArray.getResourceId(R.styleable.SlidingButton_button_background, 0);
        //中间文本
        mCenterText = typedArray.getText(R.styleable.SlidingButton_center_text);
        mCenterTextColor = typedArray.getColor(R.styleable.SlidingButton_center_text_color, context.getResources().getColor(R.color.white));
        mCenterTextSize = typedArray.getDimension(R.styleable.SlidingButton_center_text_size, 14);
        //滑块图片
        mSliderImageDrawable = typedArray.getDrawable(R.styleable.SlidingButton_slider_image_src);
        mSliderImageSize = typedArray.getDimension(R.styleable.SlidingButton_slider_image_size, 20);
        //背景提示图片
        mTipsImageDrawable = typedArray.getDrawable(R.styleable.SlidingButton_tips_image_src);
        mTipsImageSize = typedArray.getDimension(R.styleable.SlidingButton_tips_image_size, 20);
        //加载背景
        mLoadingDrawable = typedArray.getDrawable(R.styleable.SlidingButton_loading_drawable);
        mLoadingSize = typedArray.getDimension(R.styleable.SlidingButton_loading_size, 18);
        typedArray.recycle();

        //setBackgroundColor(Color.BLACK);

        //滑块层
        mBgSlider = new RelativeLayout(context);
        if (mSliderBackground != 0) {
            mBgSlider.setBackgroundResource(mSliderBackground);
        }
        //滑块左边图标
        if (mSliderImageDrawable != null) {
            mIvSlider = new ImageView(context);
            mIvSlider.setImageDrawable(mSliderImageDrawable);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) mSliderImageSize, (int) mSliderImageSize);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = (int) dp2px(context, leftMargin);
            mIvSlider.setLayoutParams(params);
            mBgSlider.addView(mIvSlider);
        }
        //滑块中间文字
        if (!TextUtils.isEmpty(mCenterText)) {
            mTvCenterText = new TextView(context);
            mCenterTextId = R.id.sliding_button_center_text;
            mTvCenterText.setId(mCenterTextId);
            mTvCenterText.setGravity(Gravity.CENTER);
            mTvCenterText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCenterTextSize);
            mTvCenterText.setTextColor(mCenterTextColor);
            mTvCenterText.setText(mCenterText);
            int w = LayoutParams.WRAP_CONTENT;
            int h = LayoutParams.WRAP_CONTENT;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
            mTvCenterText.setLayoutParams(params);
            mBgSlider.addView(mTvCenterText);
        }
        //加载图标
        if (mLoadingDrawable != null) {
            mIvLoading = new ImageView(context);
            mIvLoading.setImageDrawable(mLoadingDrawable);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) mLoadingSize, (int) mLoadingSize);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = (int) dp2px(context, 8);
            params.addRule(RelativeLayout.RIGHT_OF, mCenterTextId);
            mIvLoading.setLayoutParams(params);
            mBgSlider.addView(mIvLoading);
        }

        mBgMinWidth = (int) mTipsImageSize;

        //底层背景
        mBgButton = new RelativeLayout(context);
        if (mButtonBackground != 0) {
            mBgButton.setBackgroundResource(mButtonBackground);
        }
        if (mTipsImageDrawable != null) {
            mIvTips = new ImageView(context);
            mIvTips.setImageDrawable(mTipsImageDrawable);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) mTipsImageSize, (int) mTipsImageSize);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
            mIvTips.setLayoutParams(params);
            mBgButton.addView(mIvTips);
        }

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mBgSlider.setLayoutParams(params1);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(mBgMinWidth, FrameLayout.LayoutParams.MATCH_PARENT);
        params2.leftMargin = -mBgMinWidth;
        mBgButton.setLayoutParams(params2);
        addView(mBgButton);
        addView(mBgSlider);

        setState(NORMAL);

        mViewDragHelper = ViewDragHelper.create(this, mCallback);
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mBgSlider;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < mLeftBorder) {
                left = mLeftBorder;
            } else if (left > mRightBorder) {
                left = mRightBorder;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //移动滑块
            FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mBgSlider.getLayoutParams();
            params1.leftMargin += dx;
            params1.rightMargin -= dx;
            mBgSlider.setLayoutParams(params1);

            Log.e("tag", "left: " + left + ", dx: " + dx);

            //移动背景
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mBgButton.getLayoutParams();

            if((params2.leftMargin < 0 && left < mBgMinWidth) || (params2.leftMargin == 0 && left == mBgMinWidth)){
                params2.leftMargin += dx;
                params2.width = mBgMinWidth;
            }

            /*if ((params2.leftMargin < 0 && params2.leftMargin + dx >= 0)
                    || (params2.width > mBgMinWidth && params2.width + dx >= mBgMinWidth)) {
                params2.leftMargin = 0;
                params2.width = left;
            } else {
                params2.leftMargin += dx;
                params2.width = mBgMinWidth;
            }*/
            mBgButton.setLayoutParams(params2);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

        }

    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //滑块宽度
        mSliderWidth = mBgSlider.getWidth();
        //滑动的左边界
        mLeftBorder = -mBgSlider.getLeft();
        //滑动的右边界
        mRightBorder = mBgSlider.getRight();
        //区分开或者关的范围
        mRange = (int) (mSliderWidth * 0.6);
        Log.d("tag", "mSliderWidth: " + mSliderWidth + ", mLeftBorder: " + mLeftBorder + ", mRightBorder: " + mRightBorder + ", mBgMinWidth: " + mBgMinWidth);
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastDownX = (int) event.getRawX();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int moveX = (int) event.getRawX();
                int dX = moveX - mLastDownX;
                Log.e("tag", "==================> dX: " + dX);

                moveSlider(dX);

                mLastDownX = moveX;
                break;
            }
            case MotionEvent.ACTION_UP: {
                //resetSlider();
                break;
            }
        }
        return true;
    }*/

    /**
     * ============= ViewDragHelper =============
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * ============= ViewDragHelper =============
     */

    //移动滑块
    private void moveSlider(int dX) {
        if (isEnabled() && mState == NORMAL) {
            int l, r = 0;
            if (mBgSlider.getLeft() + dX <= mLeftBorder) {
                l = mLeftBorder;
                r = mSliderWidth;
            } else if (mBgSlider.getLeft() + dX >= mRightBorder) {
                l = mRightBorder;
                r = mSliderWidth + mRightBorder;
            } else {
                l = mBgSlider.getLeft() + dX;
                r = mBgSlider.getRight() + dX;
            }
            int t = mBgSlider.getTop();
            int b = mBgSlider.getBottom();

            mBgSlider.layout(l, t, r, b);
        }
    }

    //重置滑块
    private void resetSlider() {
        ValueAnimator animator = null;
        if (mBgSlider.getLeft() >= mRange) {
            //开
            animator = ValueAnimator.ofInt(mBgSlider.getLeft(), mRightBorder);
            animator.addListener(new MyAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(LOADING);
                }
            });
        } else {
            //关
            animator = ValueAnimator.ofInt(mBgSlider.getLeft(), mLeftBorder);
            animator.addListener(new MyAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(NORMAL);
                }
            });
        }
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int l = value;
                int t = mBgSlider.getTop();
                int r = mSliderWidth + value;
                int b = mBgSlider.getBottom();
                mBgSlider.layout(l, t, r, b);
            }
        });
        animator.start();
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.mOnStateChangedListener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        mBgSlider.setEnabled(enabled);
        if (enabled) {
            if (mSliderImageDrawable != null) {
                ((AnimationDrawable) mIvSlider.getDrawable()).start();
            }
        } else {
            if (mSliderImageDrawable != null) {
                ((AnimationDrawable) mIvSlider.getDrawable()).stop();
                mIvSlider.setImageDrawable(null);
                mIvSlider.setImageDrawable(mSliderImageDrawable);
            }
        }
    }

    /**
     * 设置当前状态
     *
     * @param state
     */
    public void setState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        mBgSlider.clearAnimation();
        mBgSlider.setAlpha(1.0f);
        mBgSlider.layout(mLeftBorder, mBgSlider.getTop(), mRightBorder, mBgSlider.getBottom());
        if (state == NORMAL) {
            mBgSlider.setEnabled(true);
            mIvLoading.setVisibility(GONE);
            if (mSliderImageDrawable != null) {
                ((AnimationDrawable) mIvSlider.getDrawable()).start();
            }
            if (mLoadingDrawable != null) {
                ((AnimationDrawable) mIvLoading.getDrawable()).stop();
                mIvLoading.setImageDrawable(null);
                mIvLoading.setImageDrawable(mLoadingDrawable);
            }
        } else if (mState == LOADING) {
            mBgSlider.setEnabled(false);
            mIvLoading.setVisibility(VISIBLE);
            if (mSliderImageDrawable != null) {
                ((AnimationDrawable) mIvSlider.getDrawable()).stop();
                mIvSlider.setImageDrawable(null);
                mIvSlider.setImageDrawable(mSliderImageDrawable);
            }
            if (mLoadingDrawable != null) {
                ((AnimationDrawable) mIvLoading.getDrawable()).start();
            }
        }
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(mState);
        }
    }

    public int getState() {
        return this.mState;
    }

    public void setCenterText(String text) {
        mTvCenterText.setText(text);
    }

    public CharSequence getCenterText() {
        return mTvCenterText.getText();
    }

    private float dp2px(Context context, float val) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, metrics);
    }

    public interface OnStateChangedListener {
        void onStateChanged(int state);
    }

    /**
     * 为了偷懒写的空实现AnimatorListener的实现类
     */
    private class MyAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
