package cn.bmkp.myview.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.bmkp.myview.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = "https://www.baidu.com";

    protected TextView mTvTitle;
    protected WebView mWebView;
    protected ProgressBar mProgressBar;
    protected LinearLayout mLlError;

    private boolean mLoadError;

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
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        mWebView = (WebView) findViewById(R.id.webView);
        mLlError = (LinearLayout) findViewById(R.id.ll_error);
        findViewById(R.id.btn_reload).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);

        initWebView();
    }

    private void setWebTitle(String title){
        mTvTitle.setText(title);
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mLoadError = true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mLoadError = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(mLoadError){
                    setWebTitle("出错啦!!!");
                    mLlError.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                }else{
                    setWebTitle(view.getTitle());
                    mLlError.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if(newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);

                }
            }
        });

        mWebView.loadUrl(URL);
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
            case R.id.btn_reload:
                refresh();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }

    private void goBack() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    private void refresh() {
        mWebView.reload();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
