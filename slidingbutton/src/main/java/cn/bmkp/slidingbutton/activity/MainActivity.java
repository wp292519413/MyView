package cn.bmkp.slidingbutton.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.bmkp.slidingbutton.R;
import cn.bmkp.slidingbutton.widget.SlidingButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SlidingButton.OnStateChangedListener {

    protected SlidingButton mSlidingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingButton = (SlidingButton) findViewById(R.id.sliding_button);
        mSlidingButton.setOnStateChangedListener(this);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
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
        }
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case SlidingButton.NORMAL:
                Toast.makeText(this, "NORMAL", Toast.LENGTH_SHORT).show();
                break;
            case SlidingButton.LOADING:
                Toast.makeText(this, "LOADING", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

