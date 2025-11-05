package mx.ipn.escuela.sensores1;

import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView jtv;
    Sensor s;
    SensorManager sm;
    List<Sensor> l;
    String c, v;
    int n, t;
    float p, r, d;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        jtv = (TextView) findViewById(R.id.xtv);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        l = sm.getSensorList(Sensor.TYPE_ALL);
        n = l.size();

        jtv.append("Sensores detectados: " + n + "\n\n");

        for (int i = 0; i < n; i++) {
            s = l.get(i);
            v = s.getName();
            c = s.getVendor();
            t = s.getType();
            p = s.getPower(); // Consumo en mA
            r = s.getResolution(); // Resolución
            d = s.getMinDelay(); // Retardo mínimo en microsegundos

            jtv.append((i + 1) + ") Nombre: " + v + "\n" +
                    "\tFabricante: " + c + "\n" +
                    "\tTipo: " + t + "\n" +
                    "\tPotencia: " + p + " mA\n" +
                    "\tResolución: " + r + "\n" +
                    "\tRetardo mínimo: " + d + " µs\n\n");
        }
    }
}