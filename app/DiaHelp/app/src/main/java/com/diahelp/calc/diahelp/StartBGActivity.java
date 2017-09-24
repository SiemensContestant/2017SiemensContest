package com.diahelp.calc.diahelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;
import java.util.Map;

public class StartBGActivity extends Activity {
    private EditText bg;
    private FloatingActionButton fab;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title);
        setContentView(R.layout.activity_startbg);

        final Map<String, Double> foodInfo = (Map<String, Double>) getIntent().getSerializableExtra("data");

        bg = (EditText) findViewById(R.id.start_bg_tb);

        fab = (FloatingActionButton) findViewById(R.id.enter_fab_2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartBGActivity.this, GetResultActivity.class);
                i.putExtra("data", (Serializable) foodInfo);
                double startbg = 0.0;
                try {
                    startbg = Double.parseDouble(bg.getText().toString().trim());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                i.putExtra("start bg", startbg);
                startActivity(i);
                finish();
            }
        });



    }
}
