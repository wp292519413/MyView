package cn.bmkp.myview.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmkp.myview.R;

public class CActivity extends AppCompatActivity {

    protected ViewPager mViewPager;
    protected LinearLayout mLlPoints;
    protected List<ImageView> mDotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLlPoints = (LinearLayout) findViewById(R.id.ll_points);

        List<View> viewList = new ArrayList<>();
        mDotList = new ArrayList<>();
        for (int i = 0; i < 0; i++) {
            View view = View.inflate(this, R.layout.view_activity_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            /*if (i % 2 == 0) {
                imageView.setBackgroundDrawable(new ColorDrawable(Color.RED));
            } else {
                imageView.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
            }*/
            if(i == 0){
                imageView.setImageResource(R.drawable.a1);
            }else if(i == 1){
                imageView.setImageResource(R.drawable.a2);
            }else if(i == 2){
                imageView.setImageResource(R.drawable.a3);
            }
            final int position = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CActivity.this, "这是第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
            viewList.add(view);

            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(0, 0, 15, 0);
            iv.setLayoutParams(params);
            iv.setImageResource(R.drawable.dot_deactive);
            mDotList.add(iv);
            mLlPoints.addView(iv);
        }
        mDotList.get(0).setImageResource(R.drawable.dot_active);
        MyAdapter adapter = new MyAdapter(viewList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (ImageView iv : mDotList) {
                    iv.setImageResource(R.drawable.dot_deactive);
                }
                mDotList.get(position).setImageResource(R.drawable.dot_active);
            }
        });
    }
}
