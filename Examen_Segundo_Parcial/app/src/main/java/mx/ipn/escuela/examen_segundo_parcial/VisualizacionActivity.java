package mx.ipn.escuela.examen_segundo_parcial;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import mx.ipn.escuela.examen_segundo_parcial.entidades.Triangulo;

public class VisualizacionActivity extends AppCompatActivity implements SensorEventListener {

    private FractalView fractalView;
    private SensorManager sensorManager;
    private Sensor gravitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacion);

        fractalView = findViewById(R.id.fractalView);
        Triangulo triangulo = (Triangulo) getIntent().getSerializableExtra("triangulo_seleccionado");

        if (triangulo != null) {
            fractalView.setTriangulo(triangulo);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            // El sensor de gravedad ya proporciona una lectura limpia del vector de gravedad.
            // Usamos su componente x directamente, sin necesidad de un filtro de paso bajo.
            float rotationAngle = -event.values[0] * 5; // El multiplicador ajusta la sensibilidad
            fractalView.setRotationAngle(rotationAngle);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario para esta implementación
    }
}