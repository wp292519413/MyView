package cn.bmkp.myview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.bmkp.myview.R;
import cn.bmkp.myview.widget.SlidingRadioButton;

public class DActivity extends AppCompatActivity {

    protected SlidingRadioButton mRadioButton;
    protected List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d);

        mData = new ArrayList<>();
        mData.add("一");
        mData.add("二二二");
        mData.add("三三三三三三");

        mRadioButton = ((SlidingRadioButton) findViewById(R.id.srb));

        mRadioButton
                .setData(mData)
                .setOnItemSelectedListener(new SlidingRadioButton.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(SlidingRadioButton slidingRadioButton, int position, String text) {
                        Log.e("tag", "当前选中的是: " + position);
                    }
                })
                .init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mRadioButton.setPosition(2, false);
    }

    public void click1(View view) {
        //int position = mRadioButton.getSelectedPosition();
        //Toast.makeText(this, mData.get(position), Toast.LENGTH_SHORT).show();
        mRadioButton.setPosition(2, false);
    }
}
