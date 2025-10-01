package mx.ipn.escuela.ejercicio1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText numberInput;
    Button checkButton;
    TextView resultText;
    TabHost th;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TabHost
        th = findViewById(android.R.id.tabhost);
        th.setup();

        // TAB1
        TabHost.TabSpec ts = th.newTabSpec("tab1");
        ts.setContent(R.id.xtab1);
        ts.setIndicator("Instrucciones");
        th.addTab(ts);

        // TAB2
        ts = th.newTabSpec("tab2");
        ts.setContent(R.id.xtab2);
        ts.setIndicator("Kaprekar");
        th.addTab(ts);

        th.setCurrentTab(0);

        // Referencias UI de TAB2
        numberInput = findViewById(R.id.numberInput);
        checkButton = findViewById(R.id.checkButton);
        resultText = findViewById(R.id.resultText);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = numberInput.getText().toString();
                if (input.isEmpty()) {
                    resultText.setText("Por favor ingresa un número.");
                    return;
                }
                try {
                    int n = Integer.parseInt(input);
                    if (n <= 0) {
                        resultText.setText("Ingresa un número natural mayor que 0.");
                        return;
                    }
                    checkKaprekar(n);
                } catch (NumberFormatException e) {
                    resultText.setText("Número inválido.");
                }
            }
        });
    }

    private void checkKaprekar(int n) {
        ArrayList<String> iterations = new ArrayList<>();
        long square = (long) n * n;
        String squareStr = String.valueOf(square);
        boolean isKaprekar = false;

        for (int i = 1; i < squareStr.length(); i++) {
            String left = squareStr.substring(0, i);
            String right = squareStr.substring(i);

            int leftNum = Integer.parseInt(left);
            int rightNum = Integer.parseInt(right);

            if (rightNum != 0 && leftNum + rightNum == n) {
                isKaprekar = true;
                iterations.add(left + " + " + right + " = " + n);
            }
        }

        StringBuilder result = new StringBuilder();
        if (isKaprekar) {
            result.append(n).append(" es un número Kaprekar!\nIteraciones:\n");
            for (String iter : iterations) {
                result.append(iter).append("\n");
            }
        } else {
            result.append(n).append(" NO es un número Kaprekar.");
        }
        resultText.setText(result.toString());
    }
}
