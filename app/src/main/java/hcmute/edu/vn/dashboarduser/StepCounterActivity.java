package hcmute.edu.vn.dashboarduser;

import android.Manifest;
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

public class StepCounterActivity extends AppCompatActivity implements StepReceiver.StepUpdateListener {
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 100;
    private TextView stepCountText, statusText;
    private Button startButton, stopButton;

    private StepReceiver stepReceiver;
    private boolean isReceiverRegistered = false;

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

        // Đăng ký StepReceiver với interface StepUpdateListener
        stepReceiver = new StepReceiver(this);
        registerReceiver(stepReceiver, new IntentFilter("STEP_UPDATE"), Context.RECEIVER_NOT_EXPORTED);
        isReceiverRegistered = true;
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

        if (isReceiverRegistered) {
            unregisterReceiver(stepReceiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(stepReceiver);
            isReceiverRegistered = false;
        }
    }

    // Cập nhật UI khi nhận số bước mới từ StepReceiver
    @Override
    public void onStepUpdate(int steps) {
        runOnUiThread(() -> stepCountText.setText("Steps: " + steps));
    }
}
