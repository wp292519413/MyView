package cn.bmkp.myview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    protected WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaveView = (WaveView) findViewById(R.id.wave_view1);
        mWaveView.setInitAlpha(0.5f);
        mWaveView.setStrokeColor(Color.parseColor("#7D7D7D"));
        mWaveView.setFillColor(Color.parseColor("#BABABA"));
        mWaveView.setCircleCycleTime(6000);
        mWaveView.setMaxCircleCount(4);
        mWaveView.start();
    }
}
