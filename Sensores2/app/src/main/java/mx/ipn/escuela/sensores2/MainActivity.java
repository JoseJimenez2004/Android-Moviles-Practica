package mx.ipn.escuela.sensores2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    SensorManager sm;
    Sensor s;
    int n;
    double x, y, z, a, m, g;
    TextView jtvX, jtvY, jtvZ, jtvA, jtvM, jtvG;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);

        x = y = z = a = m = 0;
        n = 0;
        g = SensorManager.STANDARD_GRAVITY;

        jtvX = findViewById(R.id.xtvX);
        jtvY = findViewById(R.id.xtvY);
        jtvZ = findViewById(R.id.xtvZ);
        jtvA = findViewById(R.id.xtvA);
        jtvM = findViewById(R.id.xtvM);
        jtvG = findViewById(R.id.xtvG);

        new MiAsincronia().execute();
    }

    public void onSensorChanged(SensorEvent se) {
        x = se.values[0];
        y = se.values[1];
        z = se.values[2];
        a = Math.sqrt(x * x + y * y + z * z);
        if (a > m) m = a;
    }

    public void onAccuracyChanged(Sensor s, int i) {}

    class MiAsincronia extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... x) {
            while (true) {
                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                n++;
                publishProgress();
            }
        }

        protected void onProgressUpdate(Void... progress) {
            jtvX.setText("X: " + x);
            jtvY.setText("Y: " + y);
            jtvZ.setText("Z: " + z);
            jtvA.setText("A: " + a);
            jtvM.setText("Max: " + m);
            jtvG.setText("Gravedad: " + g + "\t\tActualización: " + n);
        }
    }
}
