package cn.bmkp.myview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * Created by wangpan on 2017/12/18.
 */

public class SecurityEditText extends EditText {

    private OnDelKeyEventListener mOnDelKeyEventListener;

    public SecurityEditText(Context context) {
        super(context);
    }

    public SecurityEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecurityEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
    }

    private class ZanyInputConnection extends InputConnectionWrapper {

        public ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (mOnDelKeyEventListener != null) {
                    mOnDelKeyEventListener.onDeleteClick(SecurityEditText.this);
                    return true;
                }
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public void setOnDelKeyEventListener(OnDelKeyEventListener listener) {
        this.mOnDelKeyEventListener = listener;
    }

    public interface OnDelKeyEventListener {

        void onDeleteClick(SecurityEditText editText);
    }
}
