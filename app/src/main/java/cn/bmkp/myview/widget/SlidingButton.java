package cn.bmkp.myview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wangpan on 2018/4/3.
 */

public class SlidingButton extends RelativeLayout {

    public SlidingButton(Context context) {
        this(context, null);
    }

    public SlidingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
}
