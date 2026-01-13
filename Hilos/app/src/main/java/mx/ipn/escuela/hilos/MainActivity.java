package mx.ipn.escuela.hilos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    private EditText jet1;
    private Button jbn1;
    private TextView jtv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jet1 = (EditText) findViewById(R.id.xet1);
        jbn1 = (Button) findViewById(R.id.xbn1);
        jbn1.setOnClickListener(this);
        jtv2 = (TextView) findViewById(R.id.xtv2);
    }

    @Override
    public void onClick(View v) {
        try {
            final int n = Integer.parseInt(jet1.getText().toString());

            new Thread(new Runnable() {
                public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                jtv2.setText("El hilo se bloquea durante " + n + " segundos");
                            }
                        });

                        Thread.sleep(n * 1000);

                        // Opcional: Limpiar el mensaje después de que termine
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                jtv2.setText(""); // Limpia el TextView
                            }
                        });

                    } catch (InterruptedException ie) {
                        // Manejar la excepción si es necesario
                    }
                }
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresar segundos...", Toast.LENGTH_SHORT).show();
        }
    }
}