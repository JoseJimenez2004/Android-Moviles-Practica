package mx.ipn.escuela.calculadoraeventos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    EditText jet;
    String s = "";

    Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bFacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EditText
        jet = findViewById(R.id.res);

        // Botones
        b0 = findViewById(R.id.button0);
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.button4);
        b5 = findViewById(R.id.button5);
        b6 = findViewById(R.id.button6);
        b7 = findViewById(R.id.button7);
        b8 = findViewById(R.id.button8);
        b9 = findViewById(R.id.button9);
        bFacto = findViewById(R.id.buttonfacto);

        // Asignamos el listener a todos los botones
        b0.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        bFacto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Usamos if-else en lugar de switch
        if(v.getId() == R.id.button0){ s += "0"; }
        else if(v.getId() == R.id.button1){ s += "1"; }
        else if(v.getId() == R.id.button2){ s += "2"; }
        else if(v.getId() == R.id.button3){ s += "3"; }
        else if(v.getId() == R.id.button4){ s += "4"; }
        else if(v.getId() == R.id.button5){ s += "5"; }
        else if(v.getId() == R.id.button6){ s += "6"; }
        else if(v.getId() == R.id.button7){ s += "7"; }
        else if(v.getId() == R.id.button8){ s += "8"; }
        else if(v.getId() == R.id.button9){ s += "9"; }
        else if(v.getId() == R.id.buttonfacto){
            if(!s.isEmpty()){
                int n = Integer.parseInt(s);
                long fact = factorial(n);
                jet.setText(n + "! = " + fact);
                s = ""; // Reinicia para el próximo número
            }
            return; // Salimos para no mostrar número incompleto
        }

        // Actualiza el EditText con el número actual
        jet.setText(s);
    }

    // Función factorial iterativa
    private long factorial(int n){
        long result = 1;
        for(int i=1; i<=n; i++){
            result *= i;
        }
        return result;
    }
}

