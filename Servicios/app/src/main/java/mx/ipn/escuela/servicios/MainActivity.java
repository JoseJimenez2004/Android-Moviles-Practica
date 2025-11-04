package mx.ipn.escuela.servicios;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView jtv;
    private Button jbnI, jbnP, jbnC, jbnT;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        jtv = (TextView) findViewById(R.id.xtvT);
        jbnI = (Button) findViewById(R.id.xbnI);
        jbnP = (Button) findViewById(R.id.xbnP);
        jbnC = (Button) findViewById(R.id.xbnC);
        jbnT = (Button) findViewById(R.id.xbnT);

        // Listener para el botón Iniciar
        jbnI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initCrono();
            }
        });

        // Listener para el botón Pausar
        jbnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se envía una acción "PAUSE" al servicio
                Intent in = new Intent(MainActivity.this, MiCrono.class);
                in.setAction("PAUSE");
                startService(in);
            }
        });

        // Listener para el botón Continuar
        jbnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se envía una acción "CONTINUE" al servicio
                Intent in = new Intent(MainActivity.this, MiCrono.class);
                in.setAction("CONTINUE");
                startService(in);
            }
        });

        // Listener para el botón Terminar
        jbnT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopCrono();
            }
        });

        // Se establece el listener para actualizar la UI desde el servicio
        MiCrono.setUpdateListener(this);
    }

    @Override
    protected void onDestroy() {
        stopCrono();
        super.onDestroy();
    }

    private void initCrono() {
        Intent in = new Intent(this, MiCrono.class);
        in.setAction("START"); // Se especifica la acción de iniciar
        startService(in);
    }

    private void stopCrono() {
        Intent in = new Intent(this, MiCrono.class);
        stopService(in);
    }

    // Método para refrescar el TextView con el tiempo
    public void refreshCrono(double t) {
        jtv.setText(String.format("%.2f", t) + " segs");
    }
}