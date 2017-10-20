package cn.bmkp.gestureunlock.widget;

/**
 * Created by wangpan on 2017/10/19.
 */

public class GUVPoint {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_PRESS = 2;
    public static final int STATUS_ERROR = 3;

    public float x;
    public float y;
    public int status = STATUS_NORMAL;
    public int position;

    public GUVPoint(float x, float y, int position) {
        this.x = x;
        this.y = y;
        this.position = position;
    }

    public GUVPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GUVPoint) {
            GUVPoint point = (GUVPoint) obj;
            if (this.position == point.position) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "GUVPoint{" +
                "x=" + x +
                ", y=" + y +
                ", position=" + position +
                '}';
    }
}
