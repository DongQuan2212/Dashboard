package hcmute.edu.vn.dashboarduser;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StepCounterActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 100;
    private TextView stepCountText, statusText;
    private Button startButton, stopButton;

    private int lastStepCount = 0;
    private BroadcastReceiver stepReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountText = findViewById(R.id.stepCountText);
        statusText = findViewById(R.id.statusText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        ImageButton backButton = findViewById(R.id.backButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
            }
        }

        backButton.setOnClickListener(v -> finish());

        startButton.setOnClickListener(v -> startTracking());
        stopButton.setOnClickListener(v -> stopTracking());

        // Nhận dữ liệu từ StepService
        stepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("STEP_UPDATE")) {
                    lastStepCount = intent.getIntExtra("steps", 0);
                    stepCountText.setText("Steps: " + lastStepCount);
                }
            }
        };
    }

    private void startTracking() {
        statusText.setText("Tracking started...");
        Intent serviceIntent = new Intent(this, StepService.class);
        startService(serviceIntent);
    }

    private void stopTracking() {
        statusText.setText("Tracking stopped.");
        Intent serviceIntent = new Intent(this, StepService.class);
        stopService(serviceIntent);
        unregisterReceiver(stepReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(stepReceiver);
        } catch (IllegalArgumentException e) {
            // Ignore nếu đã unregister
        }
    }
}
