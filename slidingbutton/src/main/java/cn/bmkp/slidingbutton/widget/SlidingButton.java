package cn.bmkp.slidingbutton.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmkp.slidingbutton.R;


/**
 * Created by wangpan on 2017/10/25.
 */

public class SlidingButton extends RelativeLayout {

    public static final int NORMAL = 1;
    public static final int LOADING = 2;

    //滑条布局
    private RelativeLayout mBgSlider;
    //背景布局
    private RelativeLayout mBgButton;
    private ImageView mIvLoading;
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

    private OnStateChangedListener mOnStateChangedListener;
    protected VelocityTracker mVelocityTracker;

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

        //滑块 第一层背景
        mBgSlider = new RelativeLayout(context);
        mBgSlider.setId(R.id.sliding_button_slider);
        mBgSlider.setBackgroundResource(mSliderBackground);
        //滑块左边图标
        if (mSliderImageDrawable != null) {
            ImageView ivSlider = new ImageView(context);
            ivSlider.setImageDrawable(mSliderImageDrawable);
            LayoutParams params = new LayoutParams((int) mSliderImageSize, (int) mSliderImageSize);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = (int) dp2px(context, leftMargin);
            ivSlider.setLayoutParams(params);
            mBgSlider.addView(ivSlider);
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
            LayoutParams params = new LayoutParams(w, h);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
            mTvCenterText.setLayoutParams(params);
            mBgSlider.addView(mTvCenterText);
        }
        //加载动画
        if (mLoadingDrawable != null) {
            mIvLoading = new ImageView(context);
            mIvLoading.setImageDrawable(mLoadingDrawable);
            LayoutParams params = new LayoutParams((int) mLoadingSize, (int) mLoadingSize);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = (int) dp2px(context, 8);
            params.addRule(RelativeLayout.RIGHT_OF, mCenterTextId);
            mIvLoading.setLayoutParams(params);
            mBgSlider.addView(mIvLoading);
        }

        //第二层背景
        mBgButton = new RelativeLayout(context);
        mBgButton.setBackgroundResource(mButtonBackground);
        if (mTipsImageDrawable != null) {
            ImageView ivTips = new ImageView(context);
            ivTips.setImageDrawable(mTipsImageDrawable);
            LayoutParams params = new LayoutParams((int) mTipsImageSize, (int) mTipsImageSize);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = (int) dp2px(context, leftMargin);
            ivTips.setLayoutParams(params);
            mBgButton.addView(ivTips);
        }

        mBgSlider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mBgButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mBgButton);
        addView(mBgSlider);

        setState(NORMAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //滑块宽度
        mSliderWidth = mBgSlider.getWidth();
        //滑动的左边界
        mLeftBorder = - mBgSlider.getLeft();
        //滑动的右边界
        mRightBorder = mBgSlider.getRight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }else{
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                mLastDownX = (int) event.getRawX();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mVelocityTracker.addMovement(event);

                int moveX = (int) event.getRawX();
                int dX = moveX - mLastDownX;

                moveSlider(dX);

                mLastDownX = moveX;
                break;
            }
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.addMovement(event);
                //计算手指离开时的瞬时速度
                mVelocityTracker.computeCurrentVelocity(1000);

                resetSlider(mVelocityTracker.getXVelocity());
                break;
            }
        }
        return true;
    }

    //移动滑块
    private void moveSlider(int dX) {
        if(isEnabled() && mState == NORMAL){
            int l, r = 0;
            if(mBgSlider.getLeft() + dX <= mLeftBorder){
                l = mLeftBorder;
                r = mSliderWidth;
            }else if(mBgSlider.getLeft() + dX >= mRightBorder){
                l = mRightBorder;
                r = mSliderWidth + mRightBorder;
            }else{
                l = mBgSlider.getLeft() + dX;
                r = mBgSlider.getRight() + dX;
            }
            int t = mBgSlider.getTop();
            int b = mBgSlider.getBottom();

            mBgSlider.layout(l, t, r, b);
        }
    }

    //重置滑块
    private void resetSlider(float xVelocity) {
        int range = getRange(xVelocity);
        ValueAnimator animator = null;
        if(mBgSlider.getLeft() >= range){
            //开
            animator = ValueAnimator.ofInt(mBgSlider.getLeft(), mRightBorder);
            animator.addListener(new MyAnimatorListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(LOADING);
                }
            });
        }else{
            //关
            animator = ValueAnimator.ofInt(mBgSlider.getLeft(), mLeftBorder);
            animator.addListener(new MyAnimatorListener(){
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

    //根据速度计算滑动范围值
    private int getRange(float xVelocity) {
        //最大速度
        float maxV = 15500.0f;
        float minV = 500.0f;
        int maxRange = (int) (mSliderWidth * 0.7);
        int minRange = (int) (mSliderWidth * 0.15);
        float k = maxV * minRange;
        if(xVelocity < minV){
            xVelocity = minV;
        }else if(xVelocity > maxV){
            xVelocity = maxV;
        }
        int range = (int) (k / xVelocity);
        Log.e("tag", "xVelocity: " + xVelocity + ", range: " + range);
        if(range > maxRange){
            range = maxRange;
        }else if(range < minRange){
            range = minRange;
        }
        return range;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.mOnStateChangedListener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        mBgSlider.setEnabled(enabled);
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
            ((AnimationDrawable) mIvLoading.getDrawable()).stop();
        } else if (mState == LOADING) {
            mBgSlider.setEnabled(false);
            mIvLoading.setVisibility(VISIBLE);
            ((AnimationDrawable) mIvLoading.getDrawable()).start();
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
