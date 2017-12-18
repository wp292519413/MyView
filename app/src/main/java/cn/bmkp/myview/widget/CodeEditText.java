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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.bmkp.myview.R;

/**
 * Created by wangpan on 2017/12/18.
 */

public class CodeEditText extends RelativeLayout implements View.OnKeyListener, SecurityEditText.OnDelKeyEventListener {

    private final String DIGITS = "0123456789";

    protected LinearLayout mContainerView;

    protected int mLength;
    protected float mTextSize;
    protected int mTextColor;
    protected float mMarginLeft;
    protected float mCodeWidth;
    protected float mCodeHeight;
    protected Drawable mNormalBg;
    protected Drawable mSelectedBg;

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
        mMarginLeft = typedArray.getDimension(R.styleable.CodeEditText_margin_left, dp2px(context, 12));
        mCodeWidth = typedArray.getDimension(R.styleable.CodeEditText_code_width, dp2px(context, 36));
        mCodeHeight = typedArray.getDimension(R.styleable.CodeEditText_code_height, dp2px(context, 36));
        mNormalBg = typedArray.getDrawable(R.styleable.CodeEditText_background_normal);
        mSelectedBg = typedArray.getDrawable(R.styleable.CodeEditText_background_selected);
        typedArray.recycle();

        initView();
    }

    private void initView() {
        mContainerView = new LinearLayout(getContext());
        mContainerView.setGravity(Gravity.CENTER);
        mContainerView.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContainerView.setLayoutParams(params);
        addView(mContainerView);

        for (int i = 0; i < mLength; i++) {
            SecurityEditText et = new SecurityEditText(getContext());
            et.setOnDelKeyEventListener(this);
            //et.setOnKeyListener(this);
            //et.setCursorVisible(false);
            //禁止粘贴复制
            et.setLongClickable(false);
            et.setGravity(Gravity.CENTER);
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            et.setTextColor(mTextColor);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            KeyListener keyListener = new NumberKeyListener() {
                @Override
                protected char[] getAcceptedChars() {
                    return DIGITS.toCharArray();
                }

                @Override
                public int getInputType() {
                    return InputType.TYPE_CLASS_NUMBER;
                }
            };
            et.setKeyListener(keyListener);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) mCodeWidth, (int) mCodeHeight);
            layoutParams.leftMargin = (int) mMarginLeft;
            et.setLayoutParams(layoutParams);

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
                        if (index == mLength - 1) {
                            if (mOnInputCompletedListener != null) {
                                mOnInputCompletedListener.onInputCompleted(CodeEditText.this, getText());
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //禁止点击切换选中的EditText 直接把触摸事件消费掉
            et.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            /*final int index = i;
            et.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        setSelection(index);
                    }
                }
            });*/

            //et.setOnKeyListener(this);

            mContainerView.addView(et);
        }

        setSelection(0);
    }

    public String getText() {
        String result = "";
        for (int i = 0; i < mLength; i++) {
            EditText child = (EditText) mContainerView.getChildAt(i);
            result += child.getText().toString();
        }
        return result;
    }

    public void clear(){
        for (int i = 0; i < mLength; i++) {
            EditText child = (EditText) mContainerView.getChildAt(i);
            child.setText("");
            setSelection(0);
        }
    }

    private void setSelection(int index) {
        if (index < 0 || index >= mLength) {
            return;
        }
        this.mSelection = index;
        for (int i = 0; i < mLength; i++) {
            EditText child = (EditText) mContainerView.getChildAt(i);
            if (i == index) {
                child.setBackgroundDrawable(mSelectedBg);
                child.setFocusable(true);
                child.setFocusableInTouchMode(true);
                child.requestFocus();
                showKeyboard(child);
            } else {
                child.setBackgroundDrawable(mNormalBg);
            }
        }
    }

    private void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                EditText childEt = (EditText) v;
                if(keyCode == KeyEvent.KEYCODE_DEL && TextUtils.isEmpty(childEt.getText())){
                    setSelection(--mSelection);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onDeleteClick(SecurityEditText editText) {
        if(TextUtils.isEmpty(editText.getText())){
            setSelection(--mSelection);
        }
    }

    public void setOnInputCompletedListener(OnInputCompletedListener listener) {
        this.mOnInputCompletedListener = listener;
    }

    public interface OnInputCompletedListener {

        /**
         * 输入完成
         *
         * @param codeEditTextView
         * @param text
         */
        void onInputCompleted(CodeEditText codeEditTextView, String text);
    }

    private float dp2px(Context context, float val) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                val, context.getResources().getDisplayMetrics());
    }
}
