package cn.bmkp.calenderview.weiget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangpan on 2018/3/31.
 */

public class CalendarAdapter extends PagerAdapter {

    private int mPageCount;
    private AttrsBean mAttrsBean;

    public CalendarAdapter(int pageCount){
        this.mPageCount = pageCount;
    }

    @Override
    public int getCount() {
        return mPageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }

    public void setAttrsBean(AttrsBean attrsBean) {
        this.mAttrsBean = attrsBean;
    }
}
