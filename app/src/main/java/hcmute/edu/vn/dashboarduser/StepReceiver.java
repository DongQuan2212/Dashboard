package hcmute.edu.vn.dashboarduser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StepReceiver extends BroadcastReceiver {
    private StepUpdateListener listener;

    public StepReceiver() {
        // Constructor mặc định (bắt buộc)
    }


    public interface StepUpdateListener {
        void onStepUpdate(int steps);
    }

    public StepReceiver(StepUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("STEP_UPDATE")) {
            int steps = intent.getIntExtra("steps", 0);
            if (listener != null) {
                listener.onStepUpdate(steps);
            }
        }
    }
}
