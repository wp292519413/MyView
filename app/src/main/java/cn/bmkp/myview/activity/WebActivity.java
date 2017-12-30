package cn.bmkp.myview.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.ChromeClientCallbackManager;

import cn.bmkp.myview.R;

public class WebActivity extends AppCompatActivity implements ChromeClientCallbackManager.ReceivedTitleCallback, View.OnClickListener {

    protected RelativeLayout mRlContent;
    protected AgentWeb mAgentWeb;
    protected ImageView mIvBack;
    protected TextView mTvTitle;
    protected ImageView mIvClose;
    private boolean mLoadCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        findView();

        initWebView();
    }

    private void findView() {
        mRlContent = (RelativeLayout) findViewById(R.id.rl_content);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(this);
    }

    private void initWebView() {
        mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(mRlContent, new RelativeLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                .setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if(newProgress == 100){
                            mIvClose.setImageResource(R.drawable.ic_refresh);
                        }else{
                            mIvClose.setImageResource(R.drawable.ic_close);
                        }
                    }
                })
                .setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        mLoadCompleted = false;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        mLoadCompleted = true;
                    }
                })
                .createAgentWeb()//
                .ready()
                .go("https://www.baidu.com");

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_close:
                if(mLoadCompleted){
                    finish();
                }else{

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(!mAgentWeb.back()){
            super.onBackPressed();
        }
    }
}
