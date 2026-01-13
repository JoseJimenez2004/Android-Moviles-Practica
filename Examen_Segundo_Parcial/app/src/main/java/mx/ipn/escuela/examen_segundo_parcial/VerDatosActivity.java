package mx.ipn.escuela.examen_segundo_parcial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import mx.ipn.escuela.examen_segundo_parcial.db.DbTriangulos;
import mx.ipn.escuela.examen_segundo_parcial.entidades.Triangulo;

public class VerDatosActivity extends AppCompatActivity {

    RadioGroup rgConfigs;
    Button btnVisualizar;
    ArrayList<Triangulo> triangulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos);

        rgConfigs = findViewById(R.id.rgConfigs);
        btnVisualizar = findViewById(R.id.btnVisualizar);

        DbTriangulos dbTriangulos = new DbTriangulos(this);
        triangulos = dbTriangulos.mostrarTriangulos();

        for (Triangulo triangulo : triangulos) {
            RadioButton rb = new RadioButton(this);
            String config = "Color(R:" + triangulo.getColor_rojo() + ", G:" + triangulo.getColor_verde() + ", B:" + triangulo.getColor_azul() + ") " +
                          "Puntos: " + triangulo.getIteraciones() + " " +
                          "Triángulo: " + triangulo.getTipo_triangulo();
            rb.setText(config);
            rb.setTag(triangulo);
            rgConfigs.addView(rb);
        }

        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgConfigs.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    Triangulo trianguloSeleccionado = (Triangulo) selectedRadioButton.getTag();

                    Intent intent = new Intent(VerDatosActivity.this, VisualizacionActivity.class);
                    intent.putExtra("triangulo_seleccionado", trianguloSeleccionado);
                    startActivity(intent);
                } else {
                    Toast.makeText(VerDatosActivity.this, "Seleccione una configuración para visualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}