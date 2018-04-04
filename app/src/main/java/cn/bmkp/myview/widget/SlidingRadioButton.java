package cn.bmkp.myview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.bmkp.myview.R;

/**
 * Created by wangpan on 2018/4/2.
 * 滑动的单选控件
 */

public class SlidingRadioButton extends FrameLayout {

    private Drawable mBgSlideRadioButton;
    private Drawable mBgSlide;
    private float mTextSize;
    private int mTextColor;

    private Context mContext;
    private List<String> mData;
    private OnItemSelectedListener mListener;
    private LinearLayout mItemContainer;
    private View mSlideView;
    private int mCurrentPosition = 0;

    public SlidingRadioButton(Context context) {
        this(context, null);
    }

    public SlidingRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingRadioButton);
        mBgSlideRadioButton = typedArray.getDrawable(R.styleable.SlidingRadioButton_background_slideRadioButton);
        mBgSlide = typedArray.getDrawable(R.styleable.SlidingRadioButton_background_slide);
        mTextSize = typedArray.getDimension(R.styleable.SlidingRadioButton_text_size, 14.0f);
        mTextColor = typedArray.getColor(R.styleable.SlidingRadioButton_text_color, Color.GRAY);
        typedArray.recycle();

        if (mBgSlideRadioButton != null) {
            setBackgroundDrawable(mBgSlideRadioButton);
        }

        mSlideView = new View(mContext);
        FrameLayout.LayoutParams params1 = new LayoutParams(0, 0);
        mSlideView.setLayoutParams(params1);
        if (mBgSlide != null) {
            mSlideView.setBackgroundDrawable(mBgSlide);
        }
        addView(mSlideView);

        mItemContainer = new LinearLayout(mContext);
        mItemContainer.setOrientation(LinearLayout.HORIZONTAL);
        mItemContainer.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mItemContainer.setLayoutParams(params2);
        addView(mItemContainer);
    }

    public SlidingRadioButton setData(List<String> data) {
        this.mData = data;
        return this;
    }

    public SlidingRadioButton setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mListener = listener;
        return this;
    }

    public void init() {
        if (mData == null || !(mData.size() > 0)) {
            throw new IllegalStateException("SlideRadioButton data is null");
        }

        for (int i = 0; i < mData.size(); i++) {
            final String s = mData.get(i);
            TextView item = new TextView(mContext);
            item.setText(s);
            item.setGravity(Gravity.CENTER);
            item.setSingleLine(true);
            item.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            item.setTextColor(mTextColor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            item.setLayoutParams(params);
            mItemContainer.addView(item);
            final int position = i;
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPosition(position, true);
                    if (mListener != null) {
                        mListener.onItemSelected(SlidingRadioButton.this, position, s);
                    }
                }
            });
        }

        post(new Runnable() {
            @Override
            public void run() {
                //重新计算滑块的宽度和高度
                int slideW = getMeasuredWidth() / mData.size();
                int slideH = getMeasuredHeight();
                FrameLayout.LayoutParams params = (LayoutParams) mSlideView.getLayoutParams();
                params.width = slideW;
                params.height = slideH;
                mSlideView.setLayoutParams(params);
                mSlideView.measure(0, 0);
            }
        });
    }

    /**
     * 设置当前选中位置
     *
     * @param position
     * @param isAnimation
     */
    public void setPosition(final int position, final boolean isAnimation) {
        post(new Runnable() {
            @Override
            public void run() {
                if (position == mCurrentPosition) {
                    return;
                }
                mCurrentPosition = position;
                if (isAnimation) {
                    final int left = mSlideView.getLeft();
                    int newLeft = mSlideView.getMeasuredWidth() * position;

                    ValueAnimator animator = ValueAnimator.ofInt(left, newLeft);
                    animator.setDuration(200);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int l = (int) animation.getAnimatedValue();
                            mSlideView.layout(l, mSlideView.getTop(), mSlideView.getMeasuredWidth() + l, mSlideView.getBottom());
                        }
                    });
                    animator.start();
                } else {
                    mSlideView.layout(
                            mSlideView.getMeasuredWidth() * position,
                            0,
                            mSlideView.getMeasuredWidth() * (position + 1),
                            mSlideView.getMeasuredHeight()
                    );
                }
            }
        });
    }

    /**
     * 获取当前选中的位置
     *
     * @return
     */
    public int getSelectedPosition() {
        return this.mCurrentPosition;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

    }

    public interface OnItemSelectedListener {

        /**
         * 每个item被选中的回调
         *
         * @param slidingRadioButton
         * @param position
         * @param text
         */
        void onItemSelected(SlidingRadioButton slidingRadioButton, int position, String text);
    }
}
