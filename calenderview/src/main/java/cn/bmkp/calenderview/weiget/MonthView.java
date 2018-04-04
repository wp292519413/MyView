package cn.bmkp.calenderview.weiget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by wangpan on 2018/3/31.
 */

public class MonthView extends ViewGroup {

    private final int ROW = 5;
    private final int COLUMN = 7;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
