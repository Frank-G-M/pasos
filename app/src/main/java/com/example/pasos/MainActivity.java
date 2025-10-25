package com.example.pasos;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepDetector.StepListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private StepDetector stepDetector;
    private FirebaseHelper firebaseHelper;

    private TextView txtSteps;
    private Button btnHistorial;

    private int currentSteps = 0;
    private boolean isSensorRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSensor();
        setupFirebase();
        loadTodaySteps();

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        txtSteps = findViewById(R.id.txtSteps);
        btnHistorial = findViewById(R.id.btnHistorial);
    }

    private void setupSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.setStepListener(this);
    }

    private void setupFirebase() {
        firebaseHelper = new FirebaseHelper();
    }

    private void loadTodaySteps() {
        firebaseHelper.getTodaySteps(new FirebaseHelper.FirestoreCallback() {
            @Override
            public void onCallback(int steps) {
                currentSteps = steps;
                runOnUiThread(() -> updateStepDisplay());
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error cargando datos", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            isSensorRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorRegistered) {
            sensorManager.unregisterListener(this);
            isSensorRegistered = false;
            saveCurrentSteps();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            stepDetector.updateAcceleration(x, y, z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    @Override
    public void onStep() {
        currentSteps++;
        runOnUiThread(this::updateStepDisplay);

        if (currentSteps % 10 == 0) {
            saveCurrentSteps();
        }
    }

    private void updateStepDisplay() {
        txtSteps.setText(String.valueOf(currentSteps));
    }

    private void saveCurrentSteps() {
        firebaseHelper.saveDailySteps(currentSteps, new FirebaseHelper.FirestoreCallback() {
            @Override
            public void onCallback(int steps) {
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error guardando datos", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCurrentSteps();
    }
}