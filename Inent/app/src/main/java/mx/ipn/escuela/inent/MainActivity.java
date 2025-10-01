package mx.ipn.escuela.inent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText etNumero;
    Button btnAnalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNumero = findViewById(R.id.etNumero);
        btnAnalizar = findViewById(R.id.btnAnalizar);

        btnAnalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(etNumero.getText().toString());
                Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
                intent.putExtra("num", num);
                startActivity(intent);
            }
        });
    }
}
