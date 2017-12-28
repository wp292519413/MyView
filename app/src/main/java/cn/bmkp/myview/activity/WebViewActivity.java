package cn.bmkp.myview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import cn.bmkp.myview.R;
import cn.bmkp.myview.widget.CustomWebView;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = "https://www.baidu.com";

    protected TextView mTvTitle;
    protected CustomWebView mCustomWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initView();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.rl_back).setOnClickListener(this);
        findViewById(R.id.rl_refresh).setOnClickListener(this);
        mCustomWebView = (CustomWebView) findViewById(R.id.customWebView);
        mCustomWebView.setErrorLayout(R.layout.view_error);

        initWebView();
    }

    private void setWebTitle(String title){
        mTvTitle.setText(title);
    }

    private void initWebView() {
        mCustomWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if(mCustomWebView.isLoadError()){
                    setWebTitle("出错啦!!");
                }else{
                    setWebTitle(mCustomWebView.getTitle());
                }
            }
        });
        mCustomWebView.loadUrl(URL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                goBack();
                break;
            case R.id.rl_refresh:
                refresh();
                break;
        }
    }

    private void goBack() {
        if(mCustomWebView.canGoBack()){
            mCustomWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    private void refresh() {
        mCustomWebView.reload();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
