package com.diahelp.calc.diahelp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GetResultActivity extends Activity {
    private TextView title_a, title_b;
    private EditText needle;
    private Map<String, Double> features, data;
    private double startbg;
    private int mealNum;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title);
        setContentView(R.layout.activity_result);

        title_a = (TextView) findViewById(R.id.result_title);
        title_b = (TextView) findViewById(R.id.result_title_supp);
        needle = (EditText) findViewById(R.id.shoot_up_tb);
        startbg = getIntent().getDoubleExtra("start bg", 0.0);

        data = (Map<String, Double>) getIntent().getSerializableExtra("data");

        String fname = "features.dat";
        features = new HashMap<>();
        if(getBaseContext().getFileStreamPath(fname).exists()) {
            BufferedReader fin = null;
            try {
                fin = new BufferedReader(new InputStreamReader(openFileInput(fname)));
                String line = null;
                while ((line = fin.readLine()) != null) {
                    features.put(line.substring(0, line.indexOf('|')).toLowerCase(), Double.parseDouble(line.substring(line.indexOf('|')+1)));
                }
                fin.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            //FileOutputStream fout = null;
            BufferedReader finM, finS;
            try {
                //fout = openFileOutput(fname, Context.MODE_PRIVATE);
                Map<String, Double> temp = new HashMap<>();

                if(getBaseContext().getFileStreamPath("carbs_by_mass.dat").exists()) {
                    finM = new BufferedReader(new InputStreamReader(openFileInput("carbs_by_mass.dat")));
                    String line = null;
                    while ((line = finM.readLine()) != null) {
                        temp.put(line.substring(0, line.indexOf('|')).toLowerCase(), 1.0);
                    }
                    finM.close();
                }
                else {
                    FileOutputStream fout = openFileOutput("carbs_by_mass.dat", Context.MODE_PRIVATE);
                    String[] arr = getResources().getStringArray(R.array.food_by_mass);
                    for (String s : arr) {
                        temp.put(s.substring(0, s.indexOf('|')).toLowerCase(), Double.parseDouble(s.substring(s.indexOf('|')+1)));
                        fout.write((s + "\n").getBytes());
                    }
                    fout.close();
                }

                if(getBaseContext().getFileStreamPath("carbs_by_servings.dat").exists()) {
                    finS = new BufferedReader(new InputStreamReader(openFileInput("carbs_by_servings.dat")));
                    String line = null;
                    while ((line = finS.readLine()) != null) {
                        temp.put(line.substring(0, line.indexOf('|')).toLowerCase(), 1.0);
                    }
                    finS.close();
                }
                else {
                    FileOutputStream fout = openFileOutput("carbs_by_servings.dat", Context.MODE_PRIVATE);
                    String[] arr = getResources().getStringArray(R.array.food_by_servings);
                    for (String s : arr) {
                        temp.put(s.substring(0, s.indexOf('|')).toLowerCase(), Double.parseDouble(s.substring(s.indexOf('|')+1)));
                        fout.write((s + "\n").getBytes());
                    }
                    fout.close();
                }

                for(String key : temp.keySet()) {
                    features.put(key, 1.0);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String fname2 = "current_meal_num.dat";
        if(getBaseContext().getFileStreamPath(fname2).exists()) {
            BufferedReader fin = null;
            try {
                fin = new BufferedReader(new InputStreamReader(openFileInput(fname2)));
                String line = fin.readLine();
                mealNum = Integer.parseInt(line.trim());
                fin.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            mealNum = 0;
        }
        if(mealNum < 50) {
            title_a.setText("You'll need to log " + (50 - mealNum) + " more meals before I can recommend a dosage");
            title_a.setTextColor(ContextCompat.getColor(this, R.color.needInfo));
        }
        else if(mealNum < 100) {
            title_a.setText("I would recommend an approximate dosage of " + getInsulinDosage() + " units\n\nTake this value with a grain of salt");
            title_a.setTextColor(ContextCompat.getColor(this, R.color.tenativeDose));
        }
        else {
            title_a.setText("I recommend a dosage of " + getInsulinDosage() + " units");
            title_a.setTextColor(ContextCompat.getColor(this, R.color.weGucci));
        }


        fab = (FloatingActionButton) findViewById(R.id.im_done_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double dose;
                try {
                    dose = Double.parseDouble(needle.getText().toString().trim());
                }
                catch(Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter valid dose", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                String mealName = "meal" + mealNum + ".dat";
                FileOutputStream fout = null;
                try {
                    fout = openFileOutput(mealName, Context.MODE_PRIVATE);
                    for (String key : data.keySet()) {
                        fout.write((key + "|" + data.get(key) + "\n").getBytes());
                    }
                    fout.write(("Start BG|" + startbg + "\n").getBytes());
                    fout.write(("Actual Dose|" + dose + "\n").getBytes());
                    fout.close();
                    fout = openFileOutput(fname2, Context.MODE_PRIVATE);
                    fout.write(((mealNum+1)+"\n").getBytes());
                    fout.close();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Intent notif_intent = new Intent(GetResultActivity.this, ShowNotification.class);
                notif_intent.putExtra("meal num", mealNum);
                notif_intent.putExtra("data", (Serializable) data);
                notif_intent.putExtra("features", (Serializable) features);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent contentIntent = PendingIntent.getService(GetResultActivity.this, (int) (Math.random() * 1000), notif_intent, PendingIntent.FLAG_CANCEL_CURRENT);
                am.cancel(contentIntent);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000/* 7200000 /* 2 hours */, contentIntent);


//                PendingIntent resultPendingIntent =
//                        stackBuilder.getPendingIntent(
//                                0,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        );
//                mBuilder.setContentIntent(resultPendingIntent);
//                NotificationManager mNotificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.notify((int) (Math.random() * 10000), mBuilder.build());


                finish();


            }
        });



    }



    public double getInsulinDosage() {
        double ret = 0;
        for(String key : data.keySet()) {
            ret += data.get(key) * features.get(key);
        }
        ret /= 7;
        String retform = String.format("%.2f", ret);
        return Double.parseDouble(retform);
    }


}
