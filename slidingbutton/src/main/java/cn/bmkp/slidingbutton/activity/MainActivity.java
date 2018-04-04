package cn.bmkp.slidingbutton.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.bmkp.slidingbutton.R;
import cn.bmkp.slidingbutton.widget.SlidingButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SlidingButton.OnStateChangedListener {

    protected SlidingButton mSlidingButton;
    protected boolean isSimulate;           //是否开启模拟

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingButton = (SlidingButton) findViewById(R.id.sliding_button);
        mSlidingButton.setOnStateChangedListener(this);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);

        mSlidingButton.setState(SlidingButton.NORMAL);
        mSlidingButton.setCenterText("前往乘客起点");

        //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                mSlidingButton.setState(SlidingButton.NORMAL);
                break;
            case R.id.btn2:
                mSlidingButton.setState(SlidingButton.LOADING);
                break;
            case R.id.btn3:
                mSlidingButton.setEnabled(false);
                break;
            case R.id.btn4:
                mSlidingButton.setEnabled(true);
                break;
        }
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case SlidingButton.NORMAL:
                //Toast.makeText(this, "NORMAL", Toast.LENGTH_SHORT).show();
                break;
            case SlidingButton.LOADING:
                mSlidingButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSlidingButton.setState(SlidingButton.NORMAL);
                        if(mSlidingButton.getCenterText().equals("前往乘客起点")){
                            mSlidingButton.setCenterText("等待乘客上车");
                            //mSlidingButton.setEnabled(false);

                            /*mSlidingButton.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mSlidingButton.setEnabled(true);
                                    mSlidingButton.setCenterText("出发前往目的地");
                                }
                            }, 500);*/
                        }else if(mSlidingButton.getCenterText().equals("出发前往目的地")){
                            mSlidingButton.setCenterText("到达目的地");
                        }else if(mSlidingButton.getCenterText().equals("到达目的地")){
                            mSlidingButton.setCenterText("前往乘客起点");
                        }
                    }
                }, 3000);
                break;
        }
    }
}

