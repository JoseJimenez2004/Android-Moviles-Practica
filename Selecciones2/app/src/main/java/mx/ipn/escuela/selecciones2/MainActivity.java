package mx.ipn.escuela.selecciones2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private CheckBox chbSoccer, chbAmericano, chbBeisbol, chbTenis, chbOtro;
    private EditText etOtro;
    private Button bn;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        chbSoccer = findViewById(R.id.xchbsoccer);
        chbAmericano = findViewById(R.id.xchbamericano);
        chbBeisbol = findViewById(R.id.xchbbeisbol);
        chbTenis = findViewById(R.id.xchbtenis);
        chbOtro = findViewById(R.id.xchotro);
        etOtro = findViewById(R.id.xetotro);
        bn = findViewById(R.id.xbn);

        chbOtro.setOnCheckedChangeListener((buttonView, isChecked) ->
                etOtro.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );
    }

    public void opcion(View v) {
        StringBuilder seleccion = new StringBuilder("Seleccionaste: ");

        if (chbSoccer.isChecked()) seleccion.append("Fútbol Soccer, ");
        if (chbAmericano.isChecked()) seleccion.append("Fútbol Americano, ");
        if (chbBeisbol.isChecked()) seleccion.append("Béisbol, ");
        if (chbTenis.isChecked()) seleccion.append("Tenis, ");
        if (chbOtro.isChecked())
            seleccion.append("Otro: ").append(etOtro.getText().toString());

        Toast.makeText(getApplicationContext(), seleccion.toString(), Toast.LENGTH_LONG).show();
    }
}
