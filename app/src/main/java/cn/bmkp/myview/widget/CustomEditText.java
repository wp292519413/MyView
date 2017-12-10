package cn.bmkp.myview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * Created by wangpan on 2017/12/9.
 */

public class CustomEditText extends EditText {

    public static final int PLAIN = 0;     //明文
    public static final int CIPHER = 1;    //密文

    private int mState;
    private Paint mPaint;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    //光标默认X偏移量
    private final float mCursorDX = dp2px(getContext(), 1);
    //光标上一次X位置
    private float mCursorLastX;

    //光标当前显示状态
    private boolean mCursorShowState = true;
    //光标能否显示
    private int mCursorVisible = VISIBLE;
    //光标闪烁间隔时间
    private long mCursorFlickerInterval = 400;
    private int mCursorColor;

    private Runnable drawCursorRunnable = new Runnable() {
        @Override
        public void run() {
            mCursorShowState = !mCursorShowState;
            invalidate();
            mHandler.postDelayed(drawCursorRunnable, mCursorFlickerInterval);
        }
    };

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        //不显示EditText默认光标
        setCursorVisible(false);
    }

    public void setCursorColor(int cursorColor) {
        mCursorColor = cursorColor;
    }

    //设置光标能否显示
    public void setCursorVisibility(int visible) {
        mCursorVisible = visible;
    }

    //设置显示状态  明文  密文
    public void setState(int state) {
        this.mState = state;
        //密文状态下禁止长按
        //TODO 主要为了解决密文长按全选的bug
        setLongClickable(state == PLAIN);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        resetCursorPosition();
        if (mState == CIPHER) {
            if(TextUtils.isEmpty(getText())){
                super.onDraw(canvas);
            }else{
                drawPoints(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
        if (isFocused() && mCursorShowState && mCursorVisible == VISIBLE) {
            drawCursor(canvas);
        }
    }

    //重置光标位置
    private void resetCursorPosition() {
        mCursorLastX = mCursorDX;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (mCursorVisible == VISIBLE) {
            if (focused) {
                mCursorShowState = true;
                invalidate();
                mHandler.postDelayed(drawCursorRunnable, mCursorFlickerInterval);
            } else {
                mCursorShowState = false;
                invalidate();
                mHandler.removeCallbacks(drawCursorRunnable);
            }
        }
    }

    //画密文圆点
    private void drawPoints(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        float diameter = getTextSize() / 2;
        mPaint.setTextSize(diameter);

        for (int i = 0; i < getText().length(); i++) {
            float cx = diameter * i + diameter / 2 + i * dp2px(getContext(), 2);
            mCursorLastX = cx + diameter / 2;
            float cy = getHeight() / 2.0f;
            canvas.drawCircle(cx, cy, diameter / 2, mPaint);
        }
        mCursorLastX += mCursorDX;
    }

    private void drawCursor(Canvas canvas) {
        if (mState == PLAIN && !TextUtils.isEmpty(getText())) {
            TextPaint paint = getPaint();
            mCursorLastX = paint.measureText(getText().toString()) + mCursorDX;
        }
        mPaint.setColor(mCursorColor);
        mPaint.setStrokeWidth(dp2px(getContext(), 1f));
        float textHeight = getTextSize() + dp2px(getContext(), 6);
        float dy = (getHeight() - textHeight) / 2;
        canvas.drawLine(mCursorLastX, dy, mCursorLastX, getHeight() - dy, mPaint);
    }

    public float dp2px(Context context, float val) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                val, context.getResources().getDisplayMetrics());
    }

}
