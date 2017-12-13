package cn.bmkp.myview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.bmkp.myview.R;

/**
 * Created by wangpan on 2017/12/8.
 */

public class CustomEditTextView extends RelativeLayout implements View.OnClickListener, TextWatcher {

    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_PHONE = 1;
    public static final int INPUT_TYPE_PASSWORD = 2;
    public static final int INPUT_TYPE_MONEY = 3;

    private final String mPhoneDigits = "1234567890";
    private final String mPasswordDigits = ".1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String mMoneyDigits = ".1234567890";

    private float mMaxValue = 9999.99f;
    private float mMinValue = 0.00f;
    private int mPrecision = 2;         //默认金额保留两位小数

    private CustomEditText mEditText;
    private RelativeLayout mRlDelete;
    private RelativeLayout mRlShow;
    private ImageView mIvShow;
    private LinearLayout mLlPoints;
    private ImageView mIvUnderLine;

    private int mInputType;     // 默认PHONE类型
    private int mMaxLength;
    private float mTextSize;
    private int mTextColor;
    private int mCursorColor;
    private int mUnderLineVisible;
    private int mCursorVisible;
    private String mDigits;
    private String mEditTextHintText;
    private String mEditTextText;

    public CustomEditTextView(Context context) {
        this(context, null);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextView);
        mInputType = typedArray.getInt(R.styleable.CustomEditTextView_input_type, INPUT_TYPE_TEXT);
        mMaxLength = typedArray.getInt(R.styleable.CustomEditTextView_max_length, Integer.MAX_VALUE);    //密码长度限制
        mTextSize = typedArray.getDimension(R.styleable.CustomEditTextView_text_size, sp2px(context, 14));
        mTextColor = typedArray.getColor(R.styleable.CustomEditTextView_text_color, Color.parseColor("#3D3D3D"));
        mCursorColor = typedArray.getColor(R.styleable.CustomEditTextView_cursor_color, Color.parseColor("#999999"));
        mUnderLineVisible = typedArray.getInt(R.styleable.CustomEditTextView_underLine_visibility, VISIBLE);
        mCursorVisible = typedArray.getInt(R.styleable.CustomEditTextView_cursor_visibility, VISIBLE);
        mEditTextHintText = typedArray.getString(R.styleable.CustomEditTextView_hint);
        mEditTextText = typedArray.getString(R.styleable.CustomEditTextView_text);
        mDigits = typedArray.getString(R.styleable.CustomEditTextView_digits);
        mMaxValue = typedArray.getFloat(R.styleable.CustomEditTextView_max_value, mMaxValue);
        mMinValue = typedArray.getFloat(R.styleable.CustomEditTextView_min_value, mMinValue);
        mPrecision = typedArray.getInt(R.styleable.CustomEditTextView_precision, mPrecision);
        typedArray.recycle();

        View view = View.inflate(context, R.layout.view_customer_edit_text, null);
        findView(view);
        initView();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        addView(view);
    }

    private void findView(View view) {
        mIvUnderLine = (ImageView) view.findViewById(R.id.iv_underLine);
        mEditText = (CustomEditText) view.findViewById(R.id.edit_text);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mEditText.addTextChangedListener(this);
        mRlDelete = (RelativeLayout) view.findViewById(R.id.rl_delete);
        mRlDelete.setOnClickListener(this);
        mRlShow = (RelativeLayout) view.findViewById(R.id.rl_show);
        mRlShow.setOnClickListener(this);
        mIvShow = (ImageView) view.findViewById(R.id.iv_show);
        mLlPoints = (LinearLayout) view.findViewById(R.id.ll_points);
    }

    private void initView() {
        setEditTextFocus();
        mIvUnderLine.setVisibility(mUnderLineVisible);
        mEditText.setTextColor(mTextColor);
        mEditText.setCursorColor(mCursorColor);
        mEditText.setCursorVisibility(mCursorVisible);
        if (!TextUtils.isEmpty(mEditTextHintText)) {
            mEditText.setHint(mEditTextHintText);
        }
        if (!TextUtils.isEmpty(mEditTextText)) {
            mEditText.setText(mEditTextText);
        }

        //设置长度限制
        InputFilter[] filters = {new InputFilter.LengthFilter(mMaxLength)};
        mEditText.setFilters(filters);

        switch (mInputType) {
            case INPUT_TYPE_PHONE:
                if(TextUtils.isEmpty(mDigits)){
                    mDigits = mPhoneDigits;
                }
                break;
            case INPUT_TYPE_PASSWORD:
                if(TextUtils.isEmpty(mDigits)){
                    mDigits = mPasswordDigits;
                }
                break;
            case INPUT_TYPE_MONEY:
                if(TextUtils.isEmpty(mDigits)){
                    mDigits = mMoneyDigits;
                }
                break;
        }

        if(mInputType != INPUT_TYPE_TEXT || !TextUtils.isEmpty(mDigits)){
            //设置输入限制
            NumberKeyListener keyListener = new NumberKeyListener() {
                @Override
                protected char[] getAcceptedChars() {
                    if (!TextUtils.isEmpty(mDigits)) {
                        return mDigits.toCharArray();
                    } else {
                        return new char[0];
                    }
                }

                @Override
                public int getInputType() {
                    if(mInputType == INPUT_TYPE_PHONE){
                        return EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_CLASS_PHONE;
                    }else if(mInputType == INPUT_TYPE_PASSWORD){
                        return EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                    }else if(mInputType == INPUT_TYPE_MONEY){
                        return EditorInfo.TYPE_CLASS_NUMBER;
                    }else{
                        return EditorInfo.TYPE_CLASS_TEXT;
                    }
                }
            };
            mEditText.setKeyListener(keyListener);
        }

        if (mInputType == INPUT_TYPE_PASSWORD) {
            mRlShow.setVisibility(VISIBLE);
            mIvShow.setImageResource(R.drawable.ic_show);
            mEditText.setState(CustomEditText.CIPHER);      //默认密文
            mIvShow.setTag(true);
        }
    }

    public void setCursorVisible(int visible) {
        mEditText.setCursorVisibility(visible);
    }

    public void setHint(String text) {
        mEditText.setHint(text);
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_delete) {
            mEditText.setText("");
            mEditText.setSelection(0);
            setEditTextFocus();
        } else if (v.getId() == R.id.rl_show) {
            boolean show = (boolean) mIvShow.getTag();
            if (show) {
                mIvShow.setImageResource(R.drawable.ic_hide);
                mEditText.setState(CustomEditText.PLAIN);
            } else {
                mIvShow.setImageResource(R.drawable.ic_show);
                mEditText.setState(CustomEditText.CIPHER);
            }
            mIvShow.setTag(!show);
            mEditText.invalidate();
            setEditTextFocus();
        }
    }

    //让mEditText获取焦点
    public void setEditTextFocus() {
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.requestFocusFromTouch();
    }

    //===============TextWatcher start==============
    private String mBeforeText = "";

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(mInputType == INPUT_TYPE_MONEY){
            if(!TextUtils.isEmpty(s)){
                mBeforeText = s.toString();
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            mRlDelete.setVisibility(GONE);
            mEditText.invalidate();
            return;
        } else {
            mRlDelete.setVisibility(VISIBLE);
        }
        String str = s.toString();
        if (mInputType == INPUT_TYPE_PHONE) {
            if (count == 1) {
                //count == 1 一个一个在输入
                if (str.length() == 3 || str.length() == 8) {
                    str += " ";
                } else if (str.length() == 4) {
                    str = str.substring(0, 3) + " " + str.substring(3, str.length());
                } else if (str.length() == 9) {
                    str = str.substring(0, 3) + " " + str.substring(3, 8) + " " + str.substring(8, str.length());
                }

            } else if (count == 0 && (str.length() == 4 || str.length() == 9)) {
                //count == 0 一个一个在删除
                str = str.substring(0, str.length() - 1);
            } else if (count > 1) {
                //复制粘贴
                str = str.replaceAll(" ", "");
                if (str.length() == 3) {
                    str += " ";
                } else if (str.length() > 3 && str.length() < 7) {
                    str = str.substring(0, 3) + " " + str.substring(3, str.length());
                } else if (str.length() == 7) {
                    str = str.substring(0, 3) + " " + str.substring(3, str.length()) + " ";
                } else if (str.length() > 7) {
                    str = str.substring(0, 3) + " " + str.substring(3, 7) + " " + str.substring(7, str.length());
                }
            }

            mEditText.removeTextChangedListener(this);
            mEditText.setText(str);
            mEditText.setSelection(mEditText.getText().length());
            mEditText.addTextChangedListener(this);
        } else if (mInputType == INPUT_TYPE_PASSWORD) {
            mEditText.invalidate();
        } else if(mInputType == INPUT_TYPE_MONEY){
            //如果第一个输入的是.  就在第一位补齐0
            if(str.startsWith(".")){
                mEditText.removeTextChangedListener(this);
                mEditText.setText("0" + str);
                mEditText.setSelection(mEditText.getText().length());
                mEditText.addTextChangedListener(this);
                str = mEditText.getText().toString();
            }
            //限制小数点后面只能输入2位 mPrecision
            if(str.contains(".")){
                String[] arr = str.split("\\.");
                if(arr.length > 1){
                    String decimals = "";
                    if(arr[1].length() > mPrecision){
                        decimals = arr[1].substring(0, mPrecision);
                    }else{
                        decimals = arr[1];
                    }
                    mEditText.removeTextChangedListener(this);
                    mEditText.setText(arr[0] + "." + decimals);
                    mEditText.setSelection(mEditText.getText().length());
                    mEditText.addTextChangedListener(this);
                    str = mEditText.getText().toString();
                }
            }

            //限制最大输入值
            double feeValue = 0;
            try {
                feeValue = Double.valueOf(str);
            } catch (Exception e) {
                mEditText.removeTextChangedListener(this);
                mEditText.setText(mBeforeText);
                mEditText.setSelection(mEditText.getText().length());
                mEditText.addTextChangedListener(this);
                return;
            }

            if(feeValue < mMinValue || feeValue > mMaxValue){
                mEditText.removeTextChangedListener(this);
                mEditText.setText(mBeforeText);
                mEditText.setSelection(mEditText.getText().length());
                mEditText.addTextChangedListener(this);
                return;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    //===============TextWatcher end==============

    public static float sp2px(Context context, float val) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                val, context.getResources().getDisplayMetrics());
    }
}
