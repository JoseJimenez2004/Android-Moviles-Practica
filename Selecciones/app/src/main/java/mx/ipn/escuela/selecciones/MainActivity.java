package mx.ipn.escuela.selecciones;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private EditText et1, et2;
    private RadioButton r1, r2, r3, r4, r5, r6, r7, r8;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        tv1 = findViewById(R.id.xtv1);

        r1 = findViewById(R.id.r1); // suma
        r2 = findViewById(R.id.r2); // resta
        r3 = findViewById(R.id.r3); // producto
        r4 = findViewById(R.id.r4); // cociente
        r5 = findViewById(R.id.r5); // potencia
        r6 = findViewById(R.id.r6); // raíz
        r7 = findViewById(R.id.r7); // seno
        r8 = findViewById(R.id.r8); // coseno y tangente
    }

    public void calcular(View v) {
        String a = et1.getText().toString();
        String b = et2.getText().toString();
        String t = "Solución: ";

        double x = a.isEmpty() ? 0 : Double.parseDouble(a);
        double y = b.isEmpty() ? 0 : Double.parseDouble(b);
        double resultado = 0;

        if (r1.isChecked()) {
            resultado = x + y;
            tv1.setText(t + x + " + " + y + " = " + resultado);
        } else if (r2.isChecked()) {
            resultado = x - y;
            tv1.setText(t + x + " - " + y + " = " + resultado);
        } else if (r3.isChecked()) {
            resultado = x * y;
            tv1.setText(t + x + " * " + y + " = " + resultado);
        } else if (r4.isChecked()) {
            if (y != 0)
                resultado = x / y;
            else
                tv1.setText("Error: división por cero");
            tv1.setText(t + x + " / " + y + " = " + resultado);
        } else if (r5.isChecked()) {
            resultado = Math.pow(x, y);
            tv1.setText(t + x + "^" + y + " = " + resultado);
        } else if (r6.isChecked()) {
            resultado = Math.sqrt(x);
            tv1.setText(t + "√" + x + " = " + resultado);
        } else if (r7.isChecked()) {
            resultado = Math.sin(Math.toRadians(x));
            tv1.setText(t + "sen(" + x + ") = " + resultado);
        } else if (r8.isChecked()) {
            double cos = Math.cos(Math.toRadians(x));
            double tan = Math.tan(Math.toRadians(x));
            tv1.setText(t + "cos(" + x + ") = " + cos + " | tan(" + x + ") = " + tan);
        }
    }
}
