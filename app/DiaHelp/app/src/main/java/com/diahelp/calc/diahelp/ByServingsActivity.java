package com.diahelp.calc.diahelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ByServingsActivity extends Activity {
    private Map<String, Double> map;
    private AutoCompleteTextView actv;
    private EditText serv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title);
        setContentView(R.layout.activity_byservings);

        //build the map from strings.xml or carbs_by_servings.dat
        String fname = "carbs_by_servings.dat";
        map = new HashMap<String, Double>();

        if(getBaseContext().getFileStreamPath(fname).exists()) {
            //parse the file
            BufferedReader fin = null;
            try {
                fin = new BufferedReader(new InputStreamReader(openFileInput(fname)));
                String line = null;
                while ((line = fin.readLine()) != null) {
                    map.put(line.substring(0, line.indexOf('|')).toLowerCase(), Double.parseDouble(line.substring(line.indexOf('|')+1)));
                }
                fin.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }


        else {
            FileOutputStream fout = null;
            try {
                fout = openFileOutput(fname, Context.MODE_PRIVATE);
                String[] arr = getResources().getStringArray(R.array.food_by_servings);
                for(String s : arr) {
                    map.put(s.substring(0, s.indexOf('|')).toLowerCase(), Double.parseDouble(s.substring(s.indexOf('|')+1)));
                    fout.write((s + "\n").getBytes());
                }
                fout.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        map = Collections.unmodifiableMap(map);

        actv = (AutoCompleteTextView) findViewById(R.id.servings_auto_complete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, map.keySet().toArray(new String[map.keySet().size()]));
        actv.setAdapter(adapter);

        serv = (EditText) findViewById(R.id.servings_amount_tb);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.back_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = actv.getText().toString().trim().toLowerCase();
                if(!map.containsKey(food)) {
                    Intent i = new Intent(ByServingsActivity.this, ByCustomActivity.class);
                    i.putExtra("food", food);
                    i.putExtra("activity", "servings");
                    startActivityForResult(i, 0);
                    return;
                }
                double carbs = 0.0;
                try {
                    carbs = Double.parseDouble(serv.getText().toString().trim());
                }
                catch(Exception e) {
                    Toast t = Toast.makeText(getApplicationContext(), "Enter valid number of servings", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                //double carbs = Double.parseDouble(serv.getText().toString().trim());
                Intent i = new Intent();
                i.putExtra("food", food);
                i.putExtra("carbs", String.format("%.3f", carbs * map.get(food)));
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String food = data.getStringExtra("food");
            String carbs = data.getStringExtra("carbs");
            Intent i = new Intent();
            i.putExtra("food", food);
            i.putExtra("carbs", carbs);
            setResult(RESULT_OK, i);
            finish();
        }
        else {
            Intent i = new Intent();
            i.putExtra("food", "err");
            i.putExtra("carbs", "0.0");
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
