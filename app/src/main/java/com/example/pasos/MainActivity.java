package com.example.pasos;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView txtSteps;
    private boolean running = false;
    private int stepCount = 0;
    private double previousMagnitude = 0;
    private double threshold = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSteps = findViewById(R.id.txtSteps);

        SharedPreferences prefs = getSharedPreferences("Pasos", MODE_PRIVATE);
        String savedDate = prefs.getString("Step_date","");
        int savedSteps = prefs.getInt("steps_today",0);
        if (savedDate.equals(getTodayDate())){
            stepCount=savedSteps;
        }else{
            stepCount=0;
        }
        txtSteps.setText("Pasos: " + stepCount);

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }
    @Override
    protected void onResume(){
        super.onResume();
        running=true;
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("Pasos",MODE_PRIVATE);
        prefs.edit()
                .putInt("steps_today",stepCount)
                .putString("Step_date",getTodayDate())
                .apply();
    }
    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("Pasos",MODE_PRIVATE);
        prefs.edit().putInt("Step_today",stepCount).apply();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

    double magnitude = Math.sqrt(x*x+y*y+z*z);
    double delta = magnitude - previousMagnitude;
    previousMagnitude = magnitude;

    if (delta>threshold){
        stepCount++;
        txtSteps.setText("Pasos: "+stepCount);
    }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

}