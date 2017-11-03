package cn.bmkp.gestureunlock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.bmkp.gestureunlock.R;

/**
 * Created by wangpan on 2017/10/18.
 */
public class GestureUnlockView extends View {

    public static final int MODE_SETTING = 1;         //设置密码模式
    public static final int MODE_UNLOCK = 2;          //解锁模式

    private int mCurrentMode = 0;

    private List<GUVPoint> mPoints = new ArrayList<>();
    private int mPointCount = 9;
    private List<GUVPoint> mSelectedPoints = new ArrayList<>();

    private Paint mPaint;

    private float mPointDiameter = 0;                   //大圆直径

    private float mTouchX;                              //触摸位置的Y

    private float mTouchY;                              //触摸位置的Y

    public GestureUnlockView(Context context) {
        this(context, null);
    }

    public GestureUnlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureUnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setMode(int mode) {
        this.mCurrentMode = mode;
    }

    public int getMode() {
        return this.mCurrentMode;
    }

    private OnSlidingCompletedListener mOnSlidingCompletedListener;

    public void setOnSlidingCompletedListener(OnSlidingCompletedListener listener) {
        this.mOnSlidingCompletedListener = listener;
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //初始化点
        initPoints();
    }

    //初始化点
    private void initPoints() {
        // 当前视图的大小
        float width = getWidth();
        float height = getHeight();

        //每个圆的直径
        mPointDiameter = 0;
        if (width > height) {
            mPointDiameter = height / 5;
        } else {
            mPointDiameter = width / 5;
        }

        float offsetX = (width - mPointDiameter * 3) / 2;       //每个点的X偏移量
        float offsetY = (height - mPointDiameter * 3) / 2;      //每个点的Y偏移量

        mPoints.clear();
        for (int i = 0; i < mPointCount; i++) {
            if (i <= 2) {
                //第一排3个点
                GUVPoint point = new GUVPoint(
                        (mPointDiameter * (i + 1) + offsetX * i - mPointDiameter / 2),
                        (mPointDiameter * 1 + offsetY * 0 - mPointDiameter / 2),
                        i);
                mPoints.add(point);
            } else if (i > 2 && i <= 5) {
                //第二排3个点
                GUVPoint point = new GUVPoint(
                        (mPointDiameter * (i - 3 + 1) + offsetX * (i - 3) - mPointDiameter / 2),
                        (mPointDiameter * 2 + offsetY * 1 - mPointDiameter / 2),
                        i);
                mPoints.add(point);
            } else {
                //第三排3个点
                GUVPoint point = new GUVPoint(
                        (mPointDiameter * (i - 6 + 1) + offsetX * (i - 6) - mPointDiameter / 2),
                        (mPointDiameter * 3 + offsetY * 2 - mPointDiameter / 2),
                        i);
                mPoints.add(point);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画已经选好的点的连线
        drawSelectedPointLine(canvas);
        //画未选好的点的连线
        drawUnSelectedPointLine(canvas);
        //画点
        drawPoints(canvas);
        //画箭头
        drawArrows(canvas);
    }

    //画箭头
    private void drawArrows(Canvas canvas) {
        if (mSelectedPoints != null && mSelectedPoints.size() > 1) {
            float angle = 80;
            int k = 30;
            float d = mPointDiameter / 4;
            for (int i = 0; i < mSelectedPoints.size() - 1; i++) {
                GUVPoint point1 = mSelectedPoints.get(i);
                GUVPoint point2 = mSelectedPoints.get(i + 1);

                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(getResources().getColor(R.color.color_2));

                PointF[] points = generateSanJiaoPoints(point1, point2, angle, k, d);
                Path path = new Path();
                path.moveTo(points[0].x, points[0].y);
                path.lineTo(points[1].x, points[1].y);
                path.lineTo(points[2].x, points[2].y);
                path.close();
                canvas.drawPath(path, mPaint);
            }
        }

    }

    //画未选好的点的连线
    private void drawUnSelectedPointLine(Canvas canvas) {
        if (mSelectedPoints != null && mSelectedPoints.size() > 0 && mTouchX > 0 && mTouchY > 0) {
            GUVPoint lastPoint = mSelectedPoints.get(mSelectedPoints.size() - 1);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(5);
            mPaint.setColor(getResources().getColor(R.color.color_2));
            canvas.drawLine(lastPoint.x, lastPoint.y, mTouchX, mTouchY, mPaint);
        }
    }

    //画已经选好的点的连线
    private void drawSelectedPointLine(Canvas canvas) {
        if (mSelectedPoints != null && mSelectedPoints.size() > 1) {
            for (int i = 0; i < mSelectedPoints.size() - 1; i++) {
                GUVPoint point1 = mSelectedPoints.get(i);
                GUVPoint point2 = mSelectedPoints.get(i + 1);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(5);
                mPaint.setColor(getResources().getColor(R.color.color_2));
                canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
            }
        }
    }

    //画点
    private void drawPoints(Canvas canvas) {
        for (int i = 0; i < mPoints.size(); i++) {
            GUVPoint point = mPoints.get(i);
            switch (point.status) {
                case GUVPoint.STATUS_NORMAL: {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(getResources().getColor(R.color.color_1));
                    canvas.drawCircle(point.x, point.y, mPointDiameter / 6, mPaint);        //画中间小圆
                    break;
                }
                case GUVPoint.STATUS_PRESS: {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(getResources().getColor(R.color.white));
                    canvas.drawCircle(point.x, point.y, mPointDiameter / 2 - 5, mPaint);        //白背景
                    mPaint.setColor(getResources().getColor(R.color.color_2));
                    canvas.drawCircle(point.x, point.y, mPointDiameter / 6, mPaint);        //中间小圆
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setStrokeWidth(5);
                    canvas.drawCircle(point.x, point.y, mPointDiameter / 2 - 5, mPaint);    //外围圆环
                    break;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取触摸的位置
        mTouchX = event.getX();
        mTouchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //重置所有的点
                resetPoints();
                //获取被触摸的点
                GUVPoint point = getSelectedPoint(mTouchX, mTouchY);
                if (point != null) {
                    point.status = GUVPoint.STATUS_PRESS;
                    //被选中的点存入一个集合
                    mSelectedPoints.add(point);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //获取被触摸的点
                GUVPoint point = getSelectedPoint(mTouchX, mTouchY);
                if (point != null && !mSelectedPoints.contains(point)) {
                    point.status = GUVPoint.STATUS_PRESS;
                    //被选中的点存入一个集合
                    mSelectedPoints.add(point);

                    //获取经过的点
                    GUVPoint passPoint = getPassPoint(mTouchX, mTouchY);
                    if (passPoint != null && !mSelectedPoints.contains(passPoint)) {
                        passPoint.status = GUVPoint.STATUS_PRESS;
                        //经过的点放入倒数第二个
                        mSelectedPoints.add(mSelectedPoints.size() - 1, passPoint);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mTouchX = -1;
                mTouchY = -1;

                if (mOnSlidingCompletedListener != null) {
                    String pwd = getResultPassword();
                    int length = getResultPasswordLength();
                    mOnSlidingCompletedListener.onSlidingCompleted(pwd, length);
                }
                break;
            }
        }
        //重绘
        invalidate();
        return true;
    }

    /**
     * 返回密码的长度
     *
     * @return
     */
    private int getResultPasswordLength() {
        int length = 0;
        if (mSelectedPoints != null && mSelectedPoints.size() > 0) {
            length = mSelectedPoints.size();
        }
        return length;
    }

    /**
     * 获取结果密码
     *
     * @return
     */
    private String getResultPassword() {
        String pwd = null;
        if (mSelectedPoints != null && mSelectedPoints.size() > 0) {
            for (GUVPoint point : mSelectedPoints) {
                pwd += point.position + ",";
            }
            if (!TextUtils.isEmpty(pwd)) {
                pwd = pwd.substring(0, pwd.length() - 1);
            }
        }
        return pwd;
    }

    /**
     * 获取经过的点
     *
     * @param touchX
     * @param touchY
     * @return
     */
    private GUVPoint getPassPoint(float touchX, float touchY) {
        GUVPoint touchPoint = new GUVPoint(touchX, touchY);
        if (mSelectedPoints != null && mSelectedPoints.size() > 1) {
            GUVPoint lastPoint = mSelectedPoints.get(mSelectedPoints.size() - 2);
            GUVPoint midPoint = getMidPoint(lastPoint, touchPoint);
            for (int i = 0; i < mPoints.size(); i++) {
                GUVPoint point = mPoints.get(i);
                if (getDistance(point, midPoint) <= mPointDiameter / 2) {
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * 获取两个点的中间点
     *
     * @param point1
     * @param point2
     * @return
     */
    private GUVPoint getMidPoint(GUVPoint point1, GUVPoint point2) {
        float x = point1.x + (point2.x - point1.x) / 2;
        float y = point1.y + (point2.y - point1.y) / 2;
        return new GUVPoint(x, y);
    }

    /**
     * 根据触摸的位置 返回触摸到的点
     *
     * @param touchX
     * @param touchY
     * @return
     */
    private GUVPoint getSelectedPoint(float touchX, float touchY) {
        GUVPoint touchPoint = new GUVPoint(touchX, touchY);
        for (int i = 0; i < mPoints.size(); i++) {
            GUVPoint point = mPoints.get(i);
            if (getDistance(point, touchPoint) <= mPointDiameter / 2) {
                return point;
            }
        }
        return null;
    }

    /**
     * 获取两个点的直线距离
     *
     * @param point1
     * @param point2
     * @return
     */
    private float getDistance(GUVPoint point1, GUVPoint point2) {
        return (float) Math.sqrt(Math.pow((point2.x - point1.x), 2)
                + Math.pow((point2.y - point1.y), 2));
    }

    /**
     * 根据已知两点和固定边长k求一个等边三角形的三个点坐标
     *
     * @param point1 起点
     * @param point2
     * @param angle  顶端角度
     * @param k      斜边长
     * @param d      底边距起点距离
     * @return
     */
    private PointF[] generateSanJiaoPoints(GUVPoint point1, GUVPoint point2, float angle, int k, float d) {
        PointF[] points = new PointF[3];

        PointF pointF1, pointF2, pointF3 = null;
        float dx = point2.x - point1.x;
        float dy = point2.y - point1.y;
        if ((dx == 0 && dy != 0) || (dx != 0 && dy == 0)) {
            if (dx == 0 && dy != 0) {
                float l0 = 0;
                if (dy < 0) {
                    dy = -d;
                    l0 = -(float) (Math.cos(Math.PI * (angle / 2) / 180) * k);
                } else {
                    dy = d;
                    l0 = (float) (Math.cos(Math.PI * (angle / 2) / 180) * k);
                }
                //顶点
                float x1 = point1.x;
                float y1 = point1.y + dy + l0;
                pointF1 = new PointF(x1, y1);
                //侧边点1
                float x2 = (float) (point1.x - Math.sin(Math.PI * (angle / 2) / 180) * k);
                float y2 = point1.y + dy;
                pointF2 = new PointF(x2, y2);
                //侧边点2
                float x3 = (float) (point1.x + Math.sin(Math.PI * (angle / 2) / 180) * k);
                float y3 = point1.y + dy;
                pointF3 = new PointF(x3, y3);
            } else {
                float l0 = 0;
                if (dx < 0) {
                    l0 = -(float) (Math.cos(Math.PI * (angle / 2) / 180) * k);
                    dx = -d;
                } else {
                    dx = d;
                    l0 = (float) (Math.cos(Math.PI * (angle / 2) / 180) * k);
                }
                //顶点
                float x1 = point1.x + dx + l0;
                float y1 = point1.y;
                pointF1 = new PointF(x1, y1);
                //侧边点1
                float x2 = point1.x + dx;
                float y2 = (float) (point1.y - Math.sin(Math.PI * (angle / 2) / 180) * k);
                pointF2 = new PointF(x2, y2);
                //侧边点2
                float x3 = point1.x + dx;
                float y3 = (float) (point1.y + Math.sin(Math.PI * (angle / 2) / 180) * k);
                pointF3 = new PointF(x3, y3);
            }
        } else if (dx != 0 && dy != 0) {
            float l0 = (float) Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
            float l1 = (float) (d + k * Math.cos(Math.PI * (angle / 2) / 180));

            dx = point2.x - point1.x;
            dy = point2.y - point1.y;
            //顶点
            float x1 = point1.x + (l1 * dx / l0);
            float y1 = point1.y + (l1 * dy / l0);
            pointF1 = new PointF(x1, y1);
            //直线与三角形底边的交点
            float x0 = point1.x + (d * dx / l0);
            float y0 = point1.y + (d * dy / l0);
            PointF point0 = new PointF(x0, y0);
            dx = point0.x - point1.x;
            dy = point0.y - point1.y;
            //侧边点1
            float x2 = (float) (point0.x + ((k * Math.sin(Math.PI * (angle / 2) / 180) * dy) / d));
            float y2 = (float) (point0.y - ((k * Math.sin(Math.PI * (angle / 2) / 180) * dx) / d));
            pointF2 = new PointF(x2, y2);
            //侧边点2
            float x3 = (float) (point0.x - ((k * Math.sin(Math.PI * (angle / 2) / 180) * dy) / d));
            float y3 = (float) (point0.y + ((k * Math.sin(Math.PI * (angle / 2) / 180) * dx) / d));
            pointF3 = new PointF(x3, y3);
        } else {
            throw new IllegalArgumentException("两个点不能为同一点");
        }
        points[0] = pointF1;
        points[1] = pointF2;
        points[2] = pointF3;
        return points;
    }

    /**
     * 重置所有的点
     */
    public void resetPoints() {
        mSelectedPoints.clear();
        for (GUVPoint point : mPoints) {
            point.status = GUVPoint.STATUS_NORMAL;
        }
        invalidate();
    }

    /**
     * 绘制完成回调
     */
    public interface OnSlidingCompletedListener {
        void onSlidingCompleted(String pwd, int length);
    }
}
