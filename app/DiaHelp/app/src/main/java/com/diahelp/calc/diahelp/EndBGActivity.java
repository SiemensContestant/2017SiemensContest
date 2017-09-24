package com.diahelp.calc.diahelp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class EndBGActivity extends Activity {
    private EditText bg;
    private FloatingActionButton fab;
    private FileOutputStream fout = null;
    Map<String, Double> data, features;
    DataPoint[] history;
    private final static double REGULARIZATION_COEFFICIENT = 1.0,
            LEARNING_RATE = 0.002;
    private final static int ITERATIONS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title);
        setContentView(R.layout.activity_startbg);
        final int mealNum = getIntent().getIntExtra("meal num", 1);
        try {
            fout = openFileOutput("meal" + mealNum + ".dat", Context.MODE_APPEND);
        }
        catch (Exception e) {
            Log.i("XD", "IS FAIL");
            finish();
        }

        data = (Map<String, Double>) getIntent().getSerializableExtra("data");
        features = (Map<String, Double>) getIntent().getSerializableExtra("features");
        bg = (EditText) findViewById(R.id.start_bg_tb);
        fab = (FloatingActionButton) findViewById(R.id.enter_fab_2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double endbg = 0.0;
                try {
                    endbg = Double.parseDouble(bg.getText().toString().trim());
                }
                catch (Exception e) {
                    Toast t = Toast.makeText(EndBGActivity.this, "Enter valid blood glucose amount", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
//                Toast t = Toast.makeText(EndBGActivity.this, endbg+"", Toast.LENGTH_SHORT);
//                t.show();
                FileOutputStream feature_out = null;
                try {
                    fout.write(("End BG|" + endbg + "\n").getBytes());
                    fout.close();
                    history = new DataPoint[mealNum+1];
                    for(int i = 0; i <= mealNum; i++) {
                        history[i] = new DataPoint("meal" + i + ".dat", EndBGActivity.this);
                    }
                    Map<String, Double> new_features = learnFeatures(history, data, features, ITERATIONS, LEARNING_RATE);
                    feature_out = openFileOutput("features.dat", Context.MODE_PRIVATE);
                    for (String key : new_features.keySet()) {
                        feature_out.write((key + "|" + new_features.get(key) + "\n").getBytes());
                    }
                    finish();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private double predict(Map<String, Double> data, Map<String, Double> features) {
        double ret = 0;
        for(String key : data.keySet()) {
            if(data.containsKey(key) && features.containsKey(key))
                ret += data.get(key) * features.get(key);
        }
        String retform = String.format("%.2f", ret);
        return Double.parseDouble(retform);
    }

    private double getErrorNoReg(DataPoint[] history, Map<String, Double> features) {
        double err = 0;
        for(int i = 0; i < history.length; i++) {
            double expected_dose = predict(features, history[i].food)/7;
            err += Math.pow(expected_dose - history[i].ActualDose, 2);
        }
        return (0.5 / history.length) * err;
    }

    private double getError(DataPoint[] history, Map<String, Double> features) {
        double err = 0;
        for(int i = 0; i < history.length; i++){
            double expected_dose = predict(features, history[i].food)/7;
            err += Math.pow(expected_dose - history[i].ActualDose, 2);
        }
        return (0.5 / history.length) * (err + getRegularizationError(features, REGULARIZATION_COEFFICIENT));
    }

    private double getRegularizationError(Map<String, Double> features, double reg_coeff) {
        double reg_err = 0;
        for(String key : features.keySet()) {
            reg_err += Math.pow(features.get(key) - 1, 2);
        }
        return reg_err * reg_coeff;
    }

    private Map<String, Double> getRegularizationDelta(Map<String, Double> data, Map<String, Double> features, double reg_coeff) {
        Map<String, Double> reg_delta = new HashMap<>();
        for (String key : data.keySet()) {
            reg_delta.put(key, 2 * reg_coeff * (features.get(key)-1));
        }
        return reg_delta;
    }

    private Map<String, Double> getParamDelta(DataPoint[] history, Map<String, Double> features, Map<String, Double> data) {
        Map<String, Double> param_delta = new HashMap<>();
        for(String key : data.keySet()) {
            for(int i = 0; i < history.length; i++) {
                double expected_dose = predict(features, history[i].food)/7;
                if(param_delta.containsKey(key)) {
                    param_delta.put(key, param_delta.get(key) + history[i].food.get(key) * (expected_dose - history[i].ActualDose));
                }
                else if(history[i].food.containsKey(key)){
                    param_delta.put(key, history[i].food.get(key) * (expected_dose - history[i].ActualDose));
                }
            }
        }
        Map<String, Double> reg_delta = getRegularizationDelta(data, features, REGULARIZATION_COEFFICIENT);
        Map<String, Double> total_delta = new HashMap<String, Double>();
        for(String key : param_delta.keySet()) {
            total_delta.put(key, param_delta.get(key) + reg_delta.get(key));
            total_delta.put(key, total_delta.get(key) / history.length);
        }
        return total_delta;
    }

    private Map<String, Double> learnFeatures(DataPoint[] history, Map<String, Double> data, Map<String, Double> features, int iterations, double learning_rate) {
        Map<String, Double> new_features = new HashMap<>(features), old_features = new HashMap<>(features);
        double last_err = getError(history, new_features);
        for(int count = 0; count < iterations; count++) {
            double err = getError(history, new_features);
            if(err > last_err) {
                return old_features;
            }
            last_err = err;
            old_features = new HashMap<>(new_features);
            Map<String, Double> feature_delta = getParamDelta(history, features, data);
            for (String key : feature_delta.keySet()) {
                new_features.put(key, new_features.get(key) - learning_rate * feature_delta.get(key));
            }
        }
        return new_features;
    }

}
class DataPoint {
    Map<String, Double> food;
    double StartBG, EndBG, ActualDose;
    public DataPoint(String fname, Context context) {
        BufferedReader fin = null;
        food = new HashMap<String, Double>();
        try {
            fin = new BufferedReader(new InputStreamReader(context.openFileInput(fname)));
            String line = null;
            while ((line=fin.readLine()) != null) {
                String key = line.substring(0, line.indexOf('|'));
                Double val = Double.parseDouble(line.substring(line.indexOf('|')+1));
                Log.i(key, String.valueOf(val));
                if(key.equals("Start BG")) StartBG = val;
                else if(key.equals("End BG")) EndBG = val;
                else if(key.equals("Actual Dose")) ActualDose = val;
                else food.put(key, val);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return food.toString() + "|" + StartBG + " > " + EndBG + "|" + ActualDose;
    }
}




