package mx.ipn.escuela.calculadoralayouts;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {
    private TextView pantalla;
    private StringBuilder operacion = new StringBuilder();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_absolute);

        pantalla = findViewById(R.id.pantalla);


        int[] idsBotones = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnMas, R.id.btnIgual
        };

        for (int id : idsBotones) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    String texto = btn.getText().toString();
                    if (texto.equals("=")) {
                        try {
                            String[] partes = operacion.toString().split("\\+");
                            int suma = 0;
                            for (String p : partes) {
                                suma += Integer.parseInt(p);
                            }
                            pantalla.setText(String.valueOf(suma));
                            operacion.setLength(0);
                            operacion.append(suma);
                        } catch (Exception e) {
                            pantalla.setText("Error");
                            operacion.setLength(0);
                        }
                    } else {
                        operacion.append(texto);
                        pantalla.setText(operacion.toString());
                    }
                });
            }
        }
    }
}
