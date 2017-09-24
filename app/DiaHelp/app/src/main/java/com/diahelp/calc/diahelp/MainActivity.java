package com.diahelp.calc.diahelp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, Double> meals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        meals = new HashMap<String, Double>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.enter_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this, ResultActivity.class);
                //startActivityForResult(i, 0);
                Intent i = new Intent(MainActivity.this, StartBGActivity.class);
                i.putExtra("data", (Serializable) meals);
                meals = new HashMap<String, Double>();
                clearDataTable();
                startActivity(i);
            }
        });

        Button bycals = (Button) findViewById(R.id.by_serves_input_button);
        bycals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ByServingsActivity.class);
                startActivityForResult(i, 0);
            }
        });

        Button bymass = (Button) findViewById(R.id.by_mass_input_button);
        bymass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ByMassActivity.class);
                startActivityForResult(i, 0);
//                String food = getIntent().getStringExtra("food");
//                double carbs = getIntent().getDoubleExtra("carbs", 0.0);
//                Context context = getApplicationContext();
//                CharSequence text = "XD: " + food + ": " + carbs + " g.";
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, text, duration);
//                TableLayout ll = (TableLayout) findViewById(R.id.food_input_table);
//                TableRow row = new TableRow(MainActivity.this);
//                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//                row.setLayoutParams(lp);
//                TextView food_tv = new TextView(MainActivity.this);
//                TextView carbs_tv = new TextView(MainActivity.this);
//                food_tv.setText(food);
//                carbs_tv.setText("" + carbs);
//                row.addView(food_tv);
//                row.addView(carbs_tv);
//                ll.addView(row);

            }
        });

        Button bycustom = (Button) findViewById(R.id.custom_input_button);
        bycustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ByCustomActivity.class);
                startActivityForResult(i, 0);
            }
        });

    }

    protected void clearDataTable() {
        TableLayout ll = (TableLayout) findViewById(R.id.food_display_table);
        int a = ll.getChildCount();
        for (int i = 1; i < a; i++) {
            View v = ll.getChildAt(1);
            TableRow row = (TableRow) v;
            ll.removeView(row);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String food = data.getStringExtra("food");
            double carbs = Double.parseDouble(data.getStringExtra("carbs"));
            if(food.equals("err")) {
                return;
            }
            if(meals.containsKey(food)) {
                meals.put(food, meals.get(food) + carbs);
            }
            else {
                meals.put(food, carbs);
            }
//            Context context = getApplicationContext();
//            CharSequence text = "<" + food + ", " + carbs + ">";
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
            TableLayout ll = (TableLayout) findViewById(R.id.food_display_table);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView food_tv = new TextView(this);
            TextView carbs_tv = new TextView(this);
            food_tv.setText(food);
            carbs_tv.setText("" + carbs);
            food_tv.setGravity(Gravity.CENTER);
            carbs_tv.setGravity(Gravity.CENTER);
            food_tv.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            carbs_tv.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            //food_tv.setLayoutParams(new LinearLayout.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
            //carbs_tv.setLayoutParams(new LinearLayout.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
            food_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_text_size));
            carbs_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_text_size));
            food_tv.setTextColor(ContextCompat.getColor(this, R.color.tableText));
            carbs_tv.setTextColor(ContextCompat.getColor(this, R.color.tableText));


            row.addView(food_tv);
            row.addView(carbs_tv);
            ll.addView(row);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
