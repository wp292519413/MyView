package cn.bmkp.slidingbutton.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmkp.slidingbutton.R;


/**
 * Created by wangpan on 2017/10/25.
 */

public class SlidingButton2 extends RelativeLayout implements View.OnTouchListener {

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
    //控件宽度
    private int mWidth;

    private int mTouchX;
    private int mX;

    private OnStateChangedListener mOnStateChangedListener;

    public SlidingButton2(Context context) {
        this(context, null);
    }

    public SlidingButton2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingButton2(Context context, AttributeSet attrs, int defStyleAttr) {
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

        mBgSlider.setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = mBgSlider.getWidth();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.sliding_button_slider) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mTouchX = (int) event.getRawX();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int x = (int) event.getRawX();
                    int dx = x - mTouchX;
                    int toX = (int) (mBgSlider.getX() + dx);
                    moveSlider(toX);
                    mTouchX = x;
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    updateState();
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.mOnStateChangedListener = listener;
    }

    //更新状态
    private void updateState() {
        ValueAnimator animator = null;
        if (mX >= mWidth / 3) {
            animator = ValueAnimator.ofInt(mX, mWidth);
            animator.addListener(new MyAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(LOADING);
                }
            });
        } else {
            animator = ValueAnimator.ofInt(mX, 0);
            animator.addListener(new MyAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(NORMAL);
                }
            });
        }
        animator.setDuration(300);
        animator.setTarget(mBgSlider);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                moveSlider(x);
            }
        });
        animator.start();
    }

    //移动slider
    private void moveSlider(int toX) {
        if (toX <= 0) {
            toX = 0;
        }
        if (toX >= mWidth) {
            toX = mWidth;
        }

        //增加透明效果 1.0 -> 0.5
        float alpha = 1.0f - toX * 0.5f / mWidth;
        mBgSlider.setAlpha(alpha);

        mX = toX;
        mBgSlider.setX(mX);
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
        mBgSlider.setX(0);
        mBgSlider.setAlpha(1.0f);
        if (state == NORMAL) {
            mBgSlider.setEnabled(true);
            mIvLoading.setVisibility(GONE);
        } else if (mState == LOADING) {
            mBgSlider.setEnabled(false);
            mIvLoading.setVisibility(VISIBLE);
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
