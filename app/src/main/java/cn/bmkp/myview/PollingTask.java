package cn.bmkp.myview;

import android.util.Log;

import cn.bmkp.myview.utils.RepeatTask;

/**
 * Created by wangpan on 2017/12/19.
 */

public class PollingTask extends RepeatTask {

    public PollingTask(long delay, long interval, int repeatCount){
        super(delay, interval, repeatCount);
    }

    @Override
    public void onRun() {
        Log.e("tag", "onRun.." + System.currentTimeMillis() + " ThreadName: " + Thread.currentThread().getName());
    }
}
