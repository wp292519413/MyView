package cn.bmkp.myview.ywt;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.bmkp.myview.R;

public class YWTActivity extends AppCompatActivity implements View.OnClickListener {

    protected TextView mTvTitle;
    protected WebView mWebView;

    private String mPayUrl = "http://121.15.180.66:801/NetPayment/BaseHttp.dll?MB_EUserPay";
    private String mReturnUrl = "http://pay_success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ywt);

        initView();

        mTvTitle.setText("一网通支付");

        reqPay();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mWebView = (WebView) findViewById(R.id.webView);
        findViewById(R.id.iv_back).setOnClickListener(this);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setSupportZoom(false);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(mReturnUrl.equals(url)){
                    finish();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    private void reqPay() {
        //创建请求参数基类对象
        YWTBaseJsonRequestData<YWTPayReqData> requestData = new YWTBaseJsonRequestData<>();
        //生成请求参数
        YWTPayReqData payReqData = generateYWTPayReqData(3257148562l, 0.01, "https://www.baidu.com", "https://www.baidu.com", mReturnUrl, "20180104001", "20180104002");
        requestData.setSign(signPayReqData(payReqData));
        requestData.setReqData(payReqData);
        //将参数转换为JSON格式
        String jsonRequestData = new Gson().toJson(requestData);
        jsonRequestData = "jsonRequestData=" + jsonRequestData;
        Log.e("tag", "jsonRequestData: " + jsonRequestData);
        //请求支付
        mWebView.postUrl(mPayUrl, jsonRequestData.getBytes());
    }

    /**
     * 对请求数据进行签名
     * @param payReqData
     * @return
     */
    private String signPayReqData(YWTPayReqData payReqData) {
        if(payReqData == null){
            return "";
        }
        Field[] fields = payReqData.getClass().getDeclaredFields();
        List<String> fieldNameList = new ArrayList<>();
        Map<String, String> fieldMap = new HashMap<>();
        if (fields != null && fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                fieldNameList.add(field.getName());
                Object valObj = null;
                try {
                    valObj = field.get(payReqData);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fieldMap.put(field.getName(), valObj == null ? "" : String.valueOf(valObj));
            }
        }

        //按照文字母排序参数名
        Collections.sort(fieldNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        //拼接字符串
        StringBuilder sb = new StringBuilder();
        for (String name : fieldNameList) {
            String value = fieldMap.get(name);
            sb.append(name + "=" + value + "&");
        }
        sb.append(YWTConfig.PRIVATE_KEY);
        Log.e("tag", "sb: " + sb);
        byte[] bytes = null;
        try {
            // 创建加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 传入要加密的字符串,按指定的字符集将字符串转换为字节流
            messageDigest.update(sb.toString().getBytes("UTF-8"));
            bytes = messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 將 byte数组转换为16进制string
        String sign = bytesToHexString(bytes);
        return sign;
    }

    /**
     * 生成一网通支付参数
     *
     * @param orderId          订单号
     * @param amount           金额
     * @param signNoticeUrl    签约成功通知地址
     * @param payNoticeUrl     支付成功通知地址
     * @param returnUrl        成功页返回商户地址,支付成功页面上“返回商户”按钮跳转地址
     * @param agrNo            客户协议号
     * @param merchantSerialNo 协议开通请求流水号
     * @return
     */
    private YWTPayReqData generateYWTPayReqData(long orderId, double amount, String signNoticeUrl, String payNoticeUrl, String returnUrl, String agrNo, String merchantSerialNo) {
        YWTPayReqData payReqData = new YWTPayReqData();
        payReqData.dateTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        payReqData.branchNo = YWTConfig.BRANCH_NO;
        payReqData.merchantNo = YWTConfig.MERCHANT_NO;
        payReqData.date = new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date());
        payReqData.orderNo = String.valueOf(orderId);
        payReqData.amount = formatDecimal(amount, "0.00");

        if (!TextUtils.isEmpty(signNoticeUrl)) {
            payReqData.signNoticeUrl = signNoticeUrl;
        }
        if (!TextUtils.isEmpty(payNoticeUrl)) {
            payReqData.payNoticeUrl = payNoticeUrl;
        }
        if (!TextUtils.isEmpty(returnUrl)) {
            payReqData.returnUrl = returnUrl;
        }
        payReqData.agrNo = agrNo;
        if (!TextUtils.isEmpty(merchantSerialNo)) {
            payReqData.merchantSerialNo = merchantSerialNo;
        }
        return payReqData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 格式化小数 返回保留自定义位数的数字字符串
     */
    public static String formatDecimal(double decimal, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(decimal);
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        if (src == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
