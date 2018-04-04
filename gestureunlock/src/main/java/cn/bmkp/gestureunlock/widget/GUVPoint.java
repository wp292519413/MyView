package cn.bmkp.gestureunlock.widget;

/**
 * Created by wangpan on 2017/10/19.
 */

public class GUVPoint {

    public float x;
    public float y;
    public State status = State.NORMAL;
    public int position;

    public enum State {
        NORMAL, SELECTED, ERROR;
    }

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
