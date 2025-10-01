package mx.ipn.escuela.layoutsandcomponents;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText xetD;
    private Button xbn;
    private TextView tvResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xetD = findViewById(R.id.xetD);
        xbn = findViewById(R.id.xbn);
        tvResultados = findViewById(R.id.tvResultados);
        xbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analizarNumero();
            }
        });
    }
    private void analizarNumero() {
        int numero = 1234;
        StringBuilder resultado = new StringBuilder();
        resultado.append("Análisis del número: ").append(numero).append("\n\n");
        if (esNumeroMaravilloso(numero)) {
            resultado.append("Es un número maravilloso\n");
        } else {
            resultado.append("No es un número maravilloso\n");
        }
        if (esFibonacci(numero)) {
            resultado.append("Es un número de Fibonacci\n");
        } else {
            resultado.append("No es un número de Fibonacci\n");
        }
        if (esMersenne(numero)) {
            resultado.append("Es un número de Mersenne\n");
        } else {
            resultado.append("No es un número de Mersenne\n");
        }
        resultado.append("\nProceso de Kaprekar:\n");
        resultado.append(procesoKaprekar(numero));
        tvResultados.setText(resultado.toString());
    }
    private boolean esNumeroMaravilloso(int n) {
        int suma = 0;
        for (int i = 1; i < n; i++) {
            if (n % i == 0) {
                suma += i;
            }
        }
        return suma < n;
    }
    private boolean esFibonacci(int n) {
        if (n < 0) return false;
        if (n == 0 || n == 1) return true;
        int a = 0, b = 1;
        while (b < n) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b == n;
    }
    private boolean esMersenne(int n) {
        if (n <= 1) return false;
        int temp = n + 1;
        if ((temp & (temp - 1)) != 0) {
            return false;
        }
        int p = 0;
        int power = temp;
        while (power > 1) {
            power /= 2;
            p++;
        }
        return esPrimo(p);
    }
    private boolean esPrimo(int n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
    private String procesoKaprekar(int numero) {
        StringBuilder proceso = new StringBuilder();
        String numeroStr = String.format("%04d", numero);
        proceso.append("Número inicial: ").append(numeroStr).append("\n");
        String actual = numeroStr;
        int paso = 1;
        while (!actual.equals("6174") && paso <= 7) {
            char[] digitos = actual.toCharArray();
            Arrays.sort(digitos);
            String menor = new String(digitos);
            StringBuilder sb = new StringBuilder(menor);
            String mayor = sb.reverse().toString();
            int numeroMayor = Integer.parseInt(mayor);
            int numeroMenor = Integer.parseInt(menor);
            int diferencia = numeroMayor - numeroMenor;
            proceso.append("Paso ").append(paso).append(": ");
            proceso.append(mayor).append(" - ").append(menor)
                    .append(" = ").append(String.format("%04d", diferencia)).append("\n");
            actual = String.format("%04d", diferencia);
            paso++;
            if (actual.equals("6174")) {
                proceso.append("Llegó a la constante de Kaprekar: 6174\n");
                break;
            }
        }
        return proceso.toString();
    }
}