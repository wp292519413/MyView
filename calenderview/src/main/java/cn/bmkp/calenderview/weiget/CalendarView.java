package cn.bmkp.calenderview.weiget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by wangpan on 2018/3/31.
 */

public class CalendarView extends ViewPager {

    private DateBean mInitDate;
    private DateBean mStartDate;
    private DateBean mEndDate;

    private AttrsBean mAttrsBean;
    private CalendarAdapter mAdapter;
    private int mCurrentPosition;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAttrsBean = new AttrsBean();
        //设置默认起始日期
        mStartDate = new DateBean(1970, 1);
        mEndDate = new DateBean(2100, 12);
        mAttrsBean.setStartDate(mStartDate);
        mAttrsBean.setEndDate(mEndDate);
    }

    public void setInitDate(DateBean initDate){
        this.mInitDate = initDate;
    }

    public void setStartDate(DateBean startDate){
        this.mStartDate = startDate;
    }

    public void setEndDate(DateBean endDate){
        this.mEndDate = endDate;
    }

    public void init(){
        //根据设定的起始日期计算总页卡数
        int pageCount = CalendarUtils.getPageCount(mStartDate, mEndDate);
        mAdapter = new CalendarAdapter(pageCount);
        mAdapter.setAttrsBean(mAttrsBean);
        setAdapter(mAdapter);

        mCurrentPosition = CalendarUtils.getDatePosition(mInitDate, mStartDate);
        //设置当前显示的position页卡
        setCurrentItem(mCurrentPosition, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });
    }
}
