package cn.bmkp.myview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangpan on 2017/10/31.
 * 水波纹特效view
 */
public class WaveView extends View {

    private int mFillColor;
    private int mStrokeColor;
    private Paint mFillPaint;
    private Paint mStrokePaint;
    private int mInitAlpha = 200;

    private List<Circle> mCircles = new ArrayList<>();
    private List<Runnable> mCallbacks = new ArrayList<>();

    private int mX, mY = 0;
    private int mMinR, mMaxR = 0;

    private boolean isStart = false;

    private long mCycleTime = 5000;             //每个圆的默认生存周期
    private int mCircleCount = 3;               //默认一起显示多少个圆
    private long mIntervalTime;                 //没生成一个圆的间隔时间

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //内容画笔
        mFillColor = Color.parseColor("#438FDB");
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mFillColor);

        //边框画笔
        mStrokeColor = Color.parseColor("#4343DB");
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(1);
        mStrokePaint.setColor(mStrokeColor);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //以下默认值不建议修改
        mX = getWidth() / 2;
        mY = getHeight() / 2;
        mMinR = 10;
        if (getWidth() > getHeight()) {
            mMaxR = getHeight() / 2;
        } else {
            mMaxR = getWidth() / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Log.e("tag", "mCircles: " + mCircles.size() + ",   mCallbacks: " + mCallbacks.size());
        for (Circle circle : mCircles) {
            mFillPaint.setAlpha(circle.alpha);
            canvas.drawCircle(circle.x, circle.y, circle.r, mFillPaint);
            mStrokePaint.setAlpha(circle.alpha);
            canvas.drawCircle(circle.x, circle.y, circle.r, mStrokePaint);
        }
    }

    private class DrawCircleRunnable implements Runnable {

        @Override
        public void run() {
            if (isStart) {
                final Circle circle = new Circle(mX, mY, mMinR, mInitAlpha);
                mCircles.add(circle);
                ValueAnimator animator = ValueAnimator.ofInt(mMinR, mMaxR);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(mCycleTime);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        if (value == mMaxR) {
                            mCircles.remove(circle);
                            mCallbacks.remove(DrawCircleRunnable.this);
                            removeCallbacks(DrawCircleRunnable.this);
                        } else {
                            circle.r = value;
                            circle.alpha = (int) ((mMaxR - value) * 1.0f / (mMaxR - mMinR) * mInitAlpha);
                            invalidate();
                        }
                    }
                });
                animator.start();

                DrawCircleRunnable runnable = new DrawCircleRunnable();
                mCallbacks.add(runnable);
                postDelayed(runnable, mIntervalTime);
            }
        }
    }

    /**
     * 设置起始透明度
     *
     * @param alpha
     */
    public void setInitAlpha(float alpha) {
        if (alpha <= 0) {
            alpha = 0;
        } else if (alpha >= 1.0f) {
            alpha = 1.0f;
        }
        this.mInitAlpha = (int) (alpha * 255);
    }

    /**
     * 设置圆填充颜色
     *
     * @param color
     */
    public void setFillColor(int color) {
        mFillColor = color;
        mFillPaint.setColor(mFillColor);
    }

    /**
     * 设置圆边框颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        mStrokeColor = color;
        mStrokePaint.setColor(mStrokeColor);
    }

    /**
     * 设置每个圆的生命周期
     *
     * @param time
     */
    public void setCircleCycleTime(long time) {
        this.mCycleTime = time;
    }

    /**
     * 设置同屏最大圆的个数
     *
     * @param count
     */
    public void setMaxCircleCount(int count) {
        this.mCircleCount = count;
    }

    /**
     * 开始绘制
     */
    public void start() {
        isStart = true;
        mCallbacks.clear();
        mCircles.clear();

        //计算每生成一个新圆的间隔时间
        this.mIntervalTime = mCycleTime / mCircleCount;

        DrawCircleRunnable runnable = new DrawCircleRunnable();
        mCallbacks.add(runnable);
        post(runnable);
    }

    /**
     * 停止
     */
    public void stop() {
        isStart = true;
        mCircles.clear();
        if (mCallbacks != null && mCallbacks.size() > 0) {
            for (Runnable callback : mCallbacks) {
                removeCallbacks(callback);
            }
        }
        mCallbacks.clear();
    }

    private class Circle {

        public int x;
        public int y;
        public int r;
        public int alpha;     //0-255

        public Circle(int x, int y, int r, int alpha) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.alpha = alpha;
        }
    }
}
