package mx.ipn.escuela.canvas;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        // Crear formulario con EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText ampSeno = new EditText(this);
        ampSeno.setHint("Amplitud seno");
        layout.addView(ampSeno);

        EditText perSeno = new EditText(this);
        perSeno.setHint("Periodo seno");
        layout.addView(perSeno);

        EditText ampCos = new EditText(this);
        ampCos.setHint("Amplitud coseno");
        layout.addView(ampCos);

        EditText perCos = new EditText(this);
        perCos.setHint("Periodo coseno");
        layout.addView(perCos);

        // Mostrar AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Configura las funciones")
                .setMessage("Introduce amplitud y periodo:")
                .setView(layout)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Obtener valores
                    float A_seno = Float.parseFloat(ampSeno.getText().toString());
                    float T_seno = Float.parseFloat(perSeno.getText().toString());
                    float A_cos = Float.parseFloat(ampCos.getText().toString());
                    float T_cos = Float.parseFloat(perCos.getText().toString());

                    // Mostrar lienzo con parámetros
                    Lienzo2 l = new Lienzo2(this, A_seno, T_seno, A_cos, T_cos);
                    setContentView(l);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Valores por defecto
                    Lienzo2 l = new Lienzo2(this, 80f, 40f, 100f, 40f);
                    setContentView(l);
                })
                .show();
    }
}
