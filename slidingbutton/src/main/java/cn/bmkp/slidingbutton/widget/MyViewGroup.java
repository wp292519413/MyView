package cn.bmkp.slidingbutton.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wangpan on 2018/4/3.
 */

public class MyViewGroup extends FrameLayout {

    protected View mView1;
    protected View mView2;
    protected ViewDragHelper mViewDragHelper;
    private  int mLeft;

    public MyViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public MyViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mView2;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //mView1.layout(mView1.getLeft() + dx / 2, mView1.getTop() + dy / 2, +mView1.getRight() + dx / 2, mView1.getBottom() + dy / 2);
            FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) mView1.getLayoutParams());
            params.leftMargin += (dx/2);
            mView1.setLayoutParams(params);
            FrameLayout.LayoutParams params1 = ((FrameLayout.LayoutParams) mView2.getLayoutParams());
            params1.leftMargin += dx;
            mView2.setLayoutParams(params1);


            /*LayoutParams params2 = (LayoutParams) mView2.getLayoutParams();
            params2.leftMargin += dx;
            mView2.setLayoutParams(params2);*/
            /*Log.e("tag", "getLeft: " + mView2.getLeft() + ", left: " + left + ", dx: " + dx);
            mLeft+=dx;
//            mView2.layout(mLeft, mView2.getTop(), mView2.getRight() + dx, mView2.getBottom());
            mView2.setX(mLeft);
            mView2.setTranslationX(mLeft);*/

        }
    };

    private void init() {

        mView1 = new View(getContext());
        mView1.setBackgroundColor(Color.BLUE);
        LayoutParams params1 = new LayoutParams(200, 200);
        mView1.setLayoutParams(params1);
        addView(mView1);

        mView2 = new View(getContext());
        mView2.setBackgroundColor(Color.RED);
        LayoutParams params2 = new LayoutParams(200, 200);
        mView2.setLayoutParams(params2);
        addView(mView2);

        mViewDragHelper = ViewDragHelper.create(this, mCallback);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
