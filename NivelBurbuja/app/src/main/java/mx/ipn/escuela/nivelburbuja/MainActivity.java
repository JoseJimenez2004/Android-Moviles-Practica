package mx.ipn.escuela.nivelburbuja;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float[] accelerometerReading = new float[3];

    private TextView angleTextView;
    private BubbleLevelView bubbleLevelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        angleTextView = findViewById(R.id.angleTextView);
        bubbleLevelView = findViewById(R.id.bubbleLevelView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Aplicar un filtro de paso bajo para suavizar los valores del sensor
            final float alpha = 0.8f;
            accelerometerReading[0] = alpha * accelerometerReading[0] + (1 - alpha) * event.values[0];
            accelerometerReading[1] = alpha * accelerometerReading[1] + (1 - alpha) * event.values[1];
            accelerometerReading[2] = alpha * accelerometerReading[2] + (1 - alpha) * event.values[2];

            float x = accelerometerReading[0];
            float y = accelerometerReading[1];

            // Calcular el ángulo de inclinación (roll)
            double roll = Math.toDegrees(Math.atan2(x, y));

            angleTextView.setText(String.format("%.0f°", roll));
            bubbleLevelView.setAngle((float) -roll);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario para esta aplicación
    }
}
