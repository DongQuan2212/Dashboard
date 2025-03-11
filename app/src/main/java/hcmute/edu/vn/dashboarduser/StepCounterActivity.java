package hcmute.edu.vn.dashboarduser;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isTracking = false;
    private int stepCount = 0;
    private TextView stepCountText, statusText;
    private Button startButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);
        stepCountText = findViewById(R.id.stepCountText);
        statusText = findViewById(R.id.statusText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        ImageButton backButton = findViewById(R.id.backButton);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay về màn hình trước đó
            }
        });
        if (stepSensor == null) {
            statusText.setText("Thiết bị không có cảm biến!");
            startButton.setEnabled(false);
            stopButton.setEnabled(false);
        } else {
            statusText.setText("Ready to track steps!");
            startButton.setOnClickListener(v -> startTracking());
            stopButton.setOnClickListener(v -> stopTracking());
        }
    }

    private void startTracking() {
        if (stepSensor != null && !isTracking) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            isTracking = true;
            stepCount = 0;
            stepCountText.setText("Steps: 0");
            statusText.setText("Tracking started...");
        }
    }

    private void stopTracking() {
        if (isTracking) {
            sensorManager.unregisterListener(this);
            isTracking = false;
            statusText.setText("Tracking stopped.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isTracking && event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
            stepCountText.setText("Steps: " + stepCount);
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for step detector
    }
}
