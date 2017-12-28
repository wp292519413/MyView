package cn.bmkp.myview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Map;

import cn.bmkp.myview.R;

/**
 * Created by wangpan on 2017/12/28.
 */
public class CustomWebView extends RelativeLayout {

    private RelativeLayout mRlContent;
    private WebView mWebView;
    private RelativeLayout mRlError;
    private ProgressBar mProgressBar;
    private View mErrorView;

    //是否显示进度条
    private boolean mShowProgress;
    //自定义进度条样式
    private Drawable mProgressDrawable;
    //是否网页加载错误
    private boolean mLoadError;
    //网页标题
    private String mWebTitle;

    private WebViewClient mWebViewClient;

    private WebChromeClient mWebChromeClient;

    public CustomWebView(Context context) {
        this(context, null);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomWebView);
        mShowProgress = typedArray.getBoolean(R.styleable.CustomWebView_show_progress, true);
        mProgressDrawable = typedArray.getDrawable(R.styleable.CustomWebView_progress_drawable);
        typedArray.recycle();

        View view = View.inflate(context, R.layout.view_progress_web_view, null);
        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        addView(view);
        findView(view);
        initView();
    }

    private void findView(View view) {
        mRlContent = (RelativeLayout) view.findViewById(R.id.rl_content);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mRlError = (RelativeLayout) view.findViewById(R.id.rl_error);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb);
    }

    private void initView() {
        mRlContent.setVisibility(VISIBLE);
        mRlError.setVisibility(GONE);
        mProgressBar.setVisibility(GONE);
        if (mProgressDrawable != null) {
            mProgressBar.setProgressDrawable(mProgressDrawable);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mLoadError = true;

                if (mWebViewClient != null) {
                    mWebViewClient.onReceivedError(view, request, error);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mLoadError = true;

                if (mWebViewClient != null) {
                    mWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mLoadError = false;

                if (mWebViewClient != null) {
                    mWebViewClient.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebTitle = view.getTitle();
                if (mLoadError) {
                    mRlContent.setVisibility(GONE);
                    mRlError.setVisibility(VISIBLE);
                } else {
                    mRlContent.setVisibility(VISIBLE);
                    mRlError.setVisibility(GONE);
                }

                if (mWebViewClient != null) {
                    mWebViewClient.onPageFinished(view, url);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //设置进度条显示
                if (mShowProgress) {
                    mProgressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        mProgressBar.setVisibility(GONE);
                    } else {
                        mProgressBar.setVisibility(VISIBLE);
                    }
                }

                if (mWebChromeClient != null) {
                    mWebChromeClient.onProgressChanged(view, newProgress);
                }
            }
        });
    }

    /**
     * 获取标题
     *
     * @return
     */
    public String getTitle() {
        return this.mWebTitle;
    }

    /**
     * 是否加载失败
     *
     * @return
     */
    public boolean isLoadError() {
        return this.mLoadError;
    }

    /**
     * 该方法并不能回调WebViewClient的所有方法,详情请仔细看当前代码,如有需求,请自行添加实现
     *
     * @param webViewClient
     */
    public void setWebViewClient(WebViewClient webViewClient) {
        this.mWebViewClient = webViewClient;
    }

    /**
     * 该方法并不能回调WebChromeClient的所有方法,详情请仔细看代码,如有需求,请自行添加实现
     *
     * @param webChromeClient
     */
    public void setWebChromeClient(WebChromeClient webChromeClient) {
        this.mWebChromeClient = webChromeClient;
    }

    /**
     * 获取WebView
     *
     * @return
     */
    public WebView getWebView() {
        return this.mWebView;
    }

    /**
     * 设置页面加载错误时显示的视图
     *
     * @param layoutResID
     */
    public void setErrorLayout(int layoutResID) {
        this.mErrorView = View.inflate(getContext(), layoutResID, null);
        addErrorView();
    }

    /**
     * 设置页面加载错误时显示的视图
     *
     * @param errorView
     */
    public void setErrorLayout(View errorView) {
        this.mErrorView = errorView;
        addErrorView();
    }

    //添加错误视图
    private void addErrorView() {
        if (mErrorView != null) {
            mRlError.removeAllViews();
            RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mErrorView.setLayoutParams(params);
            mErrorView.setLayoutParams(params);
            mRlError.addView(mErrorView);
        }
    }

    /**
     * 加载地址
     *
     * @param url
     */
    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    /**
     * 加载地址
     *
     * @param url
     * @param additionalHttpHeaders
     */
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mWebView.loadUrl(url, additionalHttpHeaders);
    }

    /**
     * 加载数据
     *
     * @param data
     * @param mimeType
     * @param encoding
     */
    public void loadData(String data, String mimeType, String encoding) {
        mWebView.loadData(data, mimeType, encoding);
    }

    /**
     * 停止加载
     */
    public void stopLoading() {
        mWebView.stopLoading();
    }

    /**
     * 是否能回退
     *
     * @return
     */
    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    /**
     * 回退
     */
    public void goBack() {
        mWebView.goBack();
    }

    /**
     * 往前
     */
    public void goForward() {
        mWebView.goForward();
    }

    /**
     * 重新加载
     */
    public void reload() {
        mWebView.reload();
    }

}
