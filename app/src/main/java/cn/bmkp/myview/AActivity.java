package cn.bmkp.myview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.bmkp.myview.widget.CodeEditText;

public class AActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        ((CodeEditText) findViewById(R.id.cet)).setOnInputCompletedListener(new CodeEditText.OnInputCompletedListener() {
            @Override
            public void onInputCompleted(CodeEditText codeEditTextView, String text) {
                Log.e("tag", "text: " + text);
            }
        });
    }
}
