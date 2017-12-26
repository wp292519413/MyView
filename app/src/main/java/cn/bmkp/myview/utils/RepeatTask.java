package cn.bmkp.myview.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by wangpan on 2017/12/19.
 */

public abstract class RepeatTask implements Runnable {

    private static final String TAG = RepeatTask.class.getSimpleName();

    //无限循环
    public static final int INFINITE = -1;

    public static final int START_TASK = 1;
    public static final int PAUSE_TASK = 2;

    private HandlerThread mHandlerThread;
    private final Handler mHandler;

    //第一次延时多久执行
    private long mDelay;
    //每次间隔多久执行一次
    private long mInterval;
    //总共重复次数
    private int mRepeatCount;
    //当前已重复执行次数
    private int mCount;
    //是否正在执行标志
    private boolean isStart;

    public RepeatTask(long delay, long interval, int repeatCount) {
        this.mDelay = delay;
        this.mInterval = interval;
        this.mRepeatCount = repeatCount;

        mHandlerThread = new HandlerThread("RepeatTask");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_TASK:
                        mHandler.postDelayed(RepeatTask.this, mDelay);
                        break;
                    case PAUSE_TASK:
                        mHandler.removeCallbacks(RepeatTask.this);
                        break;
                }
            }
        };
    }

    @Override
    public void run() {
        if (isStart) {
            if (mRepeatCount < 0 || mCount < mRepeatCount) {
                onRun();
                mCount++;
                mHandler.postDelayed(RepeatTask.this, mInterval);
            } else {
                mHandler.removeCallbacks(RepeatTask.this);
            }
        }
    }

    /**
     * 要执行的代码
     */
    protected abstract void onRun();

    /**
     * 开始执行
     */
    public void start() {
        if (!isStart) {
            isStart = true;
            if (mHandlerThread != null && mHandlerThread.isAlive()) {
                mHandler.obtainMessage(START_TASK).sendToTarget();
            } else {
                Log.e(TAG, "mHandlerThread status is error!!");
            }
        }
    }

    /**
     * 是否正在执行
     *
     * @return
     */
    public boolean isStart() {
        return isStart;
    }

    /**
     * 暂停 可再次执行<br/>
     * 如果只是暂时停止,后期会继续恢复执行建议调用该方法
     */
    public void pause() {
        if (isStart) {
            isStart = false;
            mHandler.obtainMessage(PAUSE_TASK).sendToTarget();
        }
    }

    /**
     * 终止 不可再次执行<br/>
     * 如果停止后不恢复执行建议调用该方法,该方法会停止当前线程中的Looper并且终止当前线程
     */
    public void stop() {
        if (isStart) {
            isStart = false;
            mHandler.obtainMessage(PAUSE_TASK).sendToTarget();
        }

        if (mHandlerThread != null && mHandlerThread.isAlive()) {
            mHandlerThread.quit();
            mHandlerThread.interrupt();
            mHandlerThread = null;
        }
    }

    /**
     * 设置间隔周期时间
     * @param interval
     */
    public void setInterval(long interval){
        this.mInterval = interval;
    }

    /**
     * 设置重复次数
     * @param repeatCount
     */
    public void setRepeatCount(int repeatCount){
        this.mRepeatCount = repeatCount;
    }
}
