package cn.bmkp.myview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.bmkp.myview.R;

/**
 * Created by wangpan on 2017/12/18.
 */

public class CodeEditText extends LinearLayout implements View.OnKeyListener {

    //每个EditText内容长度
    protected int mLength;
    //EditText的字体大小
    protected float mTextSize;
    //EditText的字体颜色
    protected int mTextColor;
    //EditText相隔距离
    protected float mMarginLeft;
    //单个EditText宽度
    protected float mCodeWidth;
    //单个EditText高度
    protected float mCodeHeight;
    //EditText正常时背景
    protected Drawable mNormalBg;
    //EditText选中时背景
    protected Drawable mSelectedBg;
    //EditText输入类型
    protected int mInputType;
    //EditText输入内容限制
    protected String mDigits;

    private int mDefaultPadding;

    private int mSelection;

    private OnInputCompletedListener mOnInputCompletedListener;

    public CodeEditText(Context context) {
        this(context, null);
    }

    public CodeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText);
        mLength = typedArray.getInt(R.styleable.CodeEditText_length, 4);
        mTextSize = typedArray.getDimension(R.styleable.CodeEditText_text_size, dp2px(context, 14));
        mTextColor = typedArray.getColor(R.styleable.CodeEditText_text_color, getResources().getColor(R.color.text_color1));
        mMarginLeft = typedArray.getDimension(R.styleable.CodeEditText_margin_left, 0);
        mCodeWidth = typedArray.getDimension(R.styleable.CodeEditText_code_width, -1);
        mCodeHeight = typedArray.getDimension(R.styleable.CodeEditText_code_height, -1);
        mNormalBg = typedArray.getDrawable(R.styleable.CodeEditText_background_normal);
        mSelectedBg = typedArray.getDrawable(R.styleable.CodeEditText_background_selected);
        mInputType = typedArray.getInt(R.styleable.CodeEditText_input_type1, InputType.TYPE_CLASS_NUMBER);
        mDigits = typedArray.getString(R.styleable.CodeEditText_digits);
        typedArray.recycle();

        mDefaultPadding = (int) dp2px(context, 4);

        initView();
    }

    private void initView() {
        //设置父布局属性
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.HORIZONTAL);

        //增加EditText
        for (int i = 0; i < mLength; i++) {
            //EditText et = new EditText(getContext());
            EditText et = (EditText) View.inflate(getContext(), R.layout.view_edit_text, null);
            et.setGravity(Gravity.CENTER);
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            et.setTextColor(mTextColor);
            //设置最大长度
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            //设置输入类型
            if (!TextUtils.isEmpty(mDigits)) {
                KeyListener keyListener = new NumberKeyListener() {
                    @Override
                    protected char[] getAcceptedChars() {
                        return mDigits.toCharArray();
                    }

                    @Override
                    public int getInputType() {
                        return mInputType;
                    }
                };
                et.setKeyListener(keyListener);
            } else {
                et.setInputType(mInputType);
            }

            final int index = i;
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        setSelection(index - 1);
                    } else {
                        setSelection(index + 1);
                        if (mOnInputCompletedListener != null && !TextUtils.isEmpty(getText())) {
                            mOnInputCompletedListener.onInputCompleted(CodeEditText.this, getText());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            et.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        setSelection(index);
                    }
                }
            });

            et.setOnKeyListener(this);
            //默认会有padding 不知道为什么
            et.setPadding(mDefaultPadding, mDefaultPadding, mDefaultPadding, mDefaultPadding);
            LinearLayout.LayoutParams layoutParams = null;
            if(mCodeWidth > 0 && mCodeHeight > 0){
                layoutParams = new LinearLayout.LayoutParams((int) mCodeWidth, (int) mCodeHeight);
            }else{
                layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            if(i != 0){
                layoutParams.leftMargin = (int) mMarginLeft;
            }
            et.setLayoutParams(layoutParams);
            addView(et);
        }

        //默认选中第一个EditText
        setSelection(0);
    }

    /**
     * 获取验证码结果<br/>
     * 如果任何某一个EditText没有值就返回null
     *
     * @return
     */
    public String getText() {
        String result = "";
        for (int i = 0; i < mLength; i++) {
            EditText child = (EditText) getChildAt(i);
            //如果任何某一个EditText没有值就返回空
            if (TextUtils.isEmpty(child.getText())) {
                return null;
            } else {
                result += child.getText().toString();
            }
        }
        return result;
    }

    /**
     * 设置验证码结果
     *
     * @param text
     */
    public void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            for (int i = 0; i < mLength; i++) {
                EditText child = (EditText) getChildAt(i);
                if (i < text.length()) {
                    child.setText(String.valueOf(text.charAt(i)));
                }
            }
            setSelection(mLength);
        } else {
            clear();
        }
    }

    /**
     * 清空内容并选中第一个EditText
     */
    public void clear() {
        for (int i = 0; i < mLength; i++) {
            EditText child = (EditText) getChildAt(i);
            child.setText("");
        }
        setSelection(0);
    }

    /**
     * 请求获取焦点
     */
    public void setRequestFocus(){
        setSelection(mSelection);
    }

    /**
     * 获取当前选择的EditText
     * @return
     */
    public EditText getSelectionEditText(){
        return (EditText) getChildAt(mSelection);
    }

    /**
     * 设置当前选中的EditText
     *
     * @param index
     */
    private void setSelection(int index) {
        if (index < 0) {
            index = 0;
        } else if (index > mLength - 1) {
            index = mLength - 1;
        }

        this.mSelection = index;
        for (int i = 0; i < mLength; i++) {
            EditText child = (EditText) getChildAt(i);
            if (i == index) {
                child.setBackgroundDrawable(mSelectedBg);
                child.setFocusable(true);
                child.setFocusableInTouchMode(true);
                child.requestFocus();
                child.setSelection(child.getText().length());
            } else {
                child.setBackgroundDrawable(mNormalBg);
            }
            if (!TextUtils.isEmpty(child.getText())) {
                child.setBackgroundDrawable(mSelectedBg);
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                EditText childEt = (EditText) v;
                //如果当前EditText没有输入值并且按了DEL删除按键
                if (keyCode == KeyEvent.KEYCODE_DEL && TextUtils.isEmpty(childEt.getText())) {
                    setSelection(--mSelection);
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * 设置输入完成监听
     *
     * @param listener
     */
    public void setOnInputCompletedListener(OnInputCompletedListener listener) {
        this.mOnInputCompletedListener = listener;
    }

    public interface OnInputCompletedListener {

        /**
         * 输入完成时会回调
         *
         * @param codeEditText
         * @param text
         */
        void onInputCompleted(CodeEditText codeEditText, String text);
    }

    private float dp2px(Context context, float val) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                val, context.getResources().getDisplayMetrics());
    }
}
