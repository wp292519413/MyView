package cn.bmkp.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.bmkp.myview.widget.CustomEditTextView;

public class MainActivity extends AppCompatActivity {

    protected WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*mWaveView = (WaveView) findViewById(R.id.wave_view1);
        mWaveView.setInitAlpha(0.5f);
        mWaveView.setStrokeColor(Color.parseColor("#7D7D7D"));
        mWaveView.setFillColor(Color.parseColor("#BABABA"));
        mWaveView.setCircleCycleTime(6000);
        mWaveView.setMaxCircleCount(4);
        mWaveView.start();*/

        CustomEditTextView etPhone = (CustomEditTextView) findViewById(R.id.cet_phone);
        etPhone.setEditTextFocus();
    }
}
