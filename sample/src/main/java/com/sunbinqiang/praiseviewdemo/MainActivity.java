package com.sunbinqiang.praiseviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.sunbinqiang.iconcountview.IconCountView;

public class MainActivity extends AppCompatActivity {

    private IconCountView praiseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        praiseView = findViewById(R.id.praise_view1);
        praiseView.setOnStateChangedListener(new IconCountView.OnSelectedStateChangedListener() {
            @Override
            public void select(boolean isSelected) {
                Log.v("selected", String.valueOf(isSelected));
            }
        });
        //
        final EditText editText = (EditText) findViewById(R.id.edit_text);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    long count = Long.valueOf(editText.getText().toString());
                    praiseView.setCount(count);
                } catch (Exception e) {

                }
            }
        });
    }

}
