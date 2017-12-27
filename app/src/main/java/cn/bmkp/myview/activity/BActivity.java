package cn.bmkp.myview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmkp.myview.R;
import cn.bmkp.myview.adapter.MyRecyclerViewAdapter;

public class BActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);

        mRecyclerView = (RecyclerView) findViewById(R.id.rcv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> list = getData();
        mAdapter = new MyRecyclerViewAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<String> getData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("第" + i + "条数据");
        }
        return list;
    }
}
