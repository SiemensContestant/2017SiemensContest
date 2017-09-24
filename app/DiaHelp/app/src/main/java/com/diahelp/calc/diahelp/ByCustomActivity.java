package com.diahelp.calc.diahelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ByCustomActivity extends Activity {
    private Map<String, Double> serveMap;
    private Map<String, Double> massMap;
    private EditText foody, cust, carby;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title);
        setContentView(R.layout.activity_bycustom);

        final String fname_mass = "carbs_by_mass.dat", fname_servings = "carbs_by_servings.dat";
        serveMap = new HashMap<String, Double>();
        massMap = new HashMap<String, Double>();
        if(getBaseContext().getFileStreamPath(fname_mass).exists()) {
            //parse the file
            BufferedReader fin = null;
            try {
                fin = new BufferedReader(new InputStreamReader(openFileInput(fname_mass)));
                String line = null;
                while ((line = fin.readLine()) != null) {
                    massMap.put(line.substring(0, line.indexOf('|')).toLowerCase(), Double.parseDouble(line.substring(line.indexOf('|') + 1)));
                }
                fin.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        else {
            //read from strings.xml
            FileOutputStream fout = null;
            try {
                fout = openFileOutput(fname_mass, Context.MODE_PRIVATE);
                String[] arr = getResources().getStringArray(R.array.food_by_mass);
                for(String s : arr) {
                    massMap.put(s.substring(0, s.indexOf('|')).toLowerCase(), Double.parseDouble(s.substring(s.indexOf('|')+1)));
                    fout.write((s + "\n").getBytes());
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        if(getBaseContext().getFileStreamPath(fname_servings).exists()) {
            //parse the file
            BufferedReader fin = null;
            try {
                fin = new BufferedReader(new InputStreamReader(openFileInput(fname_servings)));
                String line = null;
                while ((line = fin.readLine()) != null) {
                    serveMap.put(line.substring(0, line.indexOf('|')).toLowerCase(), Double.parseDouble(line.substring(line.indexOf('|')+1)));
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
                fout = openFileOutput(fname_servings, Context.MODE_PRIVATE);
                String[] arr = getResources().getStringArray(R.array.food_by_servings);
                for(String s : arr) {
                    serveMap.put(s.substring(0, s.indexOf('|')).toLowerCase(), Double.parseDouble(s.substring(s.indexOf('|')+1)));
                    fout.write((s + "\n").getBytes());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        foody = (EditText) findViewById(R.id.custom_food_tb);

        cust = (EditText) findViewById(R.id.custom_food_amt);

        spinner = (Spinner) findViewById(R.id.custom_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.custom_spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        carby = (EditText) findViewById(R.id.custom_carb_tb);

        if(getIntent().hasExtra("food")) {
            foody.setText(getIntent().getStringExtra("food"));
        }

        if(getIntent().hasExtra("activity")) {
            int pos = adapter.getPosition(getIntent().getStringExtra("activity"));
            spinner.setSelection(pos);
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.back_fab_3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = foody.getText().toString().trim().toLowerCase();
                String spinner_op = spinner.getSelectedItem().toString();
//                Toast xd = Toast.makeText(getApplicationContext(), spinner_op, Toast.LENGTH_SHORT);
//                xd.show();
                if(spinner_op == "") {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select units", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                double custom = 0.0;
                try {
                    custom = Double.parseDouble(cust.getText().toString().trim().toLowerCase());
                }
                catch (NumberFormatException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid number of " + spinner_op, Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                double carbs = 0.0;
                try {
                    carbs = Double.parseDouble(carby.getText().toString().trim().toLowerCase());
                }
                catch (NumberFormatException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid number of carbs", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(spinner_op.equals("servings")) {
                    FileOutputStream fout = null;
                    try {
                        fout = openFileOutput(fname_servings, Context.MODE_APPEND);
                        fout.write((food + "|" + String.format("%.3f", carbs / custom) + "\n").getBytes());
                        fout.close();
                        fout = openFileOutput("ratios.dat", Context.MODE_APPEND);
                        fout.write((food + "|1.0" + "\n").getBytes());
                        fout.close();
                        Intent i = new Intent();
                        i.putExtra("food", food);
                        i.putExtra("carbs", String.format("%.3f", carbs));
                        setResult(RESULT_OK, i);
                        finish();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                if(spinner_op.equals("grams")) {
                    FileOutputStream fout = null;
                    try {
                        fout = openFileOutput(fname_mass, Context.MODE_APPEND);
                        fout.write((food + "|" + String.format("%.3f", carbs / custom) + "\n").getBytes());
                        fout.close();
                        fout = openFileOutput("ratios.dat", Context.MODE_APPEND);
                        fout.write((food + "|1.0" + "\n").getBytes());
                        fout.close();
                        Intent i = new Intent();
                        i.putExtra("food", food);
                        i.putExtra("carbs", String.format("%.3f", carbs));
                        setResult(RESULT_OK, i);
                        finish();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;

            }
        });

    }
}
