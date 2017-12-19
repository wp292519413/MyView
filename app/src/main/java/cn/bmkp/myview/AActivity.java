package cn.bmkp.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.bmkp.myview.widget.CodeEditText;

public class AActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        final CodeEditText codeEditText = (CodeEditText) findViewById(R.id.cet);
        codeEditText.setOnInputCompletedListener(new CodeEditText.OnInputCompletedListener() {
            @Override
            public void onInputCompleted(CodeEditText codeEditText, String text) {
                /*Log.e("tag", "text: " + text);
                if("1111".equals(text)){
                    Toast.makeText(AActivity.this, "验证码正确", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                    codeEditText.clear();
                }*/
            }
        });
        codeEditText.setText("58205288");
        codeEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                codeEditText.setText("");
            }
        }, 2000);
    }

}
