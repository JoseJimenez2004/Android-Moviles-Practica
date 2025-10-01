package mx.ipn.escuela.inent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultadoActivity extends AppCompatActivity {
    TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        tvResultado = findViewById(R.id.tvResultado);

        int n = getIntent().getIntExtra("num", 0);
        StringBuilder sb = new StringBuilder("Resultados para " + n + ":\n");

        if (esPrimo(n)) sb.append("✔ Es Primo\n");
        else sb.append("✘ No es Primo\n");

        if (esFibonacci(n)) sb.append("✔ Es Fibonacci\n");
        else sb.append("✘ No es Fibonacci\n");

        if (esKaprekar(n)) sb.append("✔ Es Kaprekar\n");
        else sb.append("✘ No es Kaprekar\n");

        if (esMaravilloso(n)) sb.append("✔ Es Maravilloso\n");
        else sb.append("✘ No es Maravilloso\n");

        tvResultado.setText(sb.toString());
    }

    boolean esPrimo(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    boolean esFibonacci(int n) {
        int a = 0, b = 1;
        while (b < n) {
            int temp = b;
            b = a + b;
            a = temp;
        }
        return b == n || n == 0;
    }

    boolean esKaprekar(int n) {
        if (n == 1) return true;
        int cuadrado = n * n;
        String s = String.valueOf(cuadrado);
        for (int i = 1; i < s.length(); i++) {
            int izquierda = Integer.parseInt(s.substring(0, i));
            int derecha = Integer.parseInt(s.substring(i));
            if (derecha > 0 && izquierda + derecha == n) return true;
        }
        return false;
    }

    boolean esMaravilloso(int n) {
        int temp = n;
        while (temp != 1 && temp != 4) {
            if (temp % 2 == 0) temp /= 2;
            else temp = temp * 3 + 1;
        }
        return temp == 1;
    }
}
