package mx.ipn.escuela.serviciocesar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText etMensajeCifrado, etClave;
    private Button btnDescifrar;
    private TextView tvResultado;
    private boolean receiverRegistrado = false;

    private final BroadcastReceiver resultadoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ServicioCesar.ACTION_DESCIFRADO_COMPLETO.equals(intent.getAction())) {
                String textoDescifrado = intent.getStringExtra(ServicioCesar.EXTRA_TEXTO_DESCIFRADO);
                tvResultado.setText(textoDescifrado);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMensajeCifrado = findViewById(R.id.etMensajeCifrado);
        etClave = findViewById(R.id.etClave);
        btnDescifrar = findViewById(R.id.btnDescifrar);
        tvResultado = findViewById(R.id.tvResultado);

        btnDescifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = etMensajeCifrado.getText().toString();
                String claveStr = etClave.getText().toString();

                if (mensaje.isEmpty() || claveStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, introduce el mensaje y la clave.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int clave = Integer.parseInt(claveStr);
                iniciarServicioDeDescifrado(mensaje, clave);
            }
        });
    }

    private void iniciarServicioDeDescifrado(String mensajeCifrado, int clave) {
        Intent intent = new Intent(this, ServicioCesar.class);
        intent.setAction(ServicioCesar.ACTION_DESCIFRAR);
        intent.putExtra(ServicioCesar.EXTRA_TEXTO_CIFRADO, mensajeCifrado);
        intent.putExtra(ServicioCesar.EXTRA_CLAVE, clave);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!receiverRegistrado) {
            IntentFilter filter = new IntentFilter(ServicioCesar.ACTION_DESCIFRADO_COMPLETO);
            registerReceiver(resultadoReceiver, filter, Context.RECEIVER_NOT_EXPORTED); // ✅ Correcto en API 36
            receiverRegistrado = true;
        }
    }

    @Override
    protected void onPause() {
        if (receiverRegistrado) {
            unregisterReceiver(resultadoReceiver);
            receiverRegistrado = false;
        }
        super.onPause();
    }
}
