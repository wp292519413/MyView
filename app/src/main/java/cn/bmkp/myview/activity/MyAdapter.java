package cn.bmkp.myview.activity;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wangpan on 2018/3/14.
 */

public class MyAdapter extends PagerAdapter {

    private List<View> mViewList;

    public MyAdapter(List<View> viewList){
        this.mViewList = viewList;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e("tag1", "destroyItem: " + position);
        container.removeView(mViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("tag2", "instantiateItem: " + position);
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }
}
