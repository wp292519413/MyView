package cn.bmkp.myview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.bmkp.myview.R;
import cn.bmkp.myview.widget.CodeEditText;

public class AActivity extends AppCompatActivity {

    protected CodeEditText mCodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        mCodeEditText = (CodeEditText) findViewById(R.id.cet);
        mCodeEditText.setOnInputCompletedListener(new CodeEditText.OnInputCompletedListener() {
            @Override
            public void onInputCompleted(CodeEditText codeEditText, String text) {
                Log.e("tag", "text: " + text);
                /*if("1111".equals(text)){
                    Toast.makeText(AActivity.this, "验证码正确", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    codeEditText.clear();
                }*/
            }
        });
        /*mCodeEditText.setText("5820");
        mCodeEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCodeEditText.setText("");
            }
        }, 2000);*/

    }
}
