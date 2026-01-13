package mx.ipn.escuela.examen_segundo_parcial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mx.ipn.escuela.examen_segundo_parcial.db.DbTriangulos;
import mx.ipn.escuela.examen_segundo_parcial.entidades.Triangulo;

public class MainActivity extends AppCompatActivity {

    RadioGroup rgColor, rgIteraciones, rgTipoTriangulo;
    Button btnGuardar, btnDibujarFigura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgColor = findViewById(R.id.rgColor);
        rgIteraciones = findViewById(R.id.rgIteraciones);
        rgTipoTriangulo = findViewById(R.id.rgTipoTriangulo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnDibujarFigura = findViewById(R.id.btnDibujarFigura);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedColorId = rgColor.getCheckedRadioButtonId();
                int selectedIteracionesId = rgIteraciones.getCheckedRadioButtonId();
                int selectedTipoId = rgTipoTriangulo.getCheckedRadioButtonId();

                if (selectedColorId == -1 || selectedIteracionesId == -1 || selectedTipoId == -1) {
                    Toast.makeText(MainActivity.this, "POR FAVOR, SELECCIONE UNA OPCIÓN EN CADA GRUPO", Toast.LENGTH_LONG).show();
                    return;
                }

                // Determinar el color
                int colorRojo = 0, colorVerde = 0, colorAzul = 0;
                if (selectedColorId == R.id.rbRojo) {
                    colorRojo = 255;
                } else if (selectedColorId == R.id.rbVerde) {
                    colorVerde = 255;
                } else if (selectedColorId == R.id.rbAzul) {
                    colorAzul = 255;
                }

                // Obtener iteraciones y tipo de triángulo
                RadioButton rbIteraciones = findViewById(selectedIteracionesId);
                RadioButton rbTipo = findViewById(selectedTipoId);

                int iteraciones = Integer.parseInt(rbIteraciones.getText().toString());
                String tipoTriangulo = rbTipo.getText().toString();

                DbTriangulos dbTriangulos = new DbTriangulos(MainActivity.this);
                long id = dbTriangulos.insertarTriangulo(colorRojo, colorVerde, colorAzul, iteraciones, tipoTriangulo);

                if (id > 0) {
                    Toast.makeText(MainActivity.this, "DATOS GUARDADOS", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "ERROR AL GUARDAR DATOS", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDibujarFigura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedColorId = rgColor.getCheckedRadioButtonId();
                int selectedIteracionesId = rgIteraciones.getCheckedRadioButtonId();
                int selectedTipoId = rgTipoTriangulo.getCheckedRadioButtonId();

                if (selectedColorId == -1 || selectedIteracionesId == -1 || selectedTipoId == -1) {
                    Toast.makeText(MainActivity.this, "POR FAVOR, SELECCIONE UNA OPCIÓN PARA DIBUJAR", Toast.LENGTH_LONG).show();
                    return;
                }

                int colorRojo = 0, colorVerde = 0, colorAzul = 0;
                if (selectedColorId == R.id.rbRojo) {
                    colorRojo = 255;
                } else if (selectedColorId == R.id.rbVerde) {
                    colorVerde = 255;
                } else if (selectedColorId == R.id.rbAzul) {
                    colorAzul = 255;
                }

                RadioButton rbIteraciones = findViewById(selectedIteracionesId);
                RadioButton rbTipo = findViewById(selectedTipoId);

                int iteraciones = Integer.parseInt(rbIteraciones.getText().toString());
                String tipoTriangulo = rbTipo.getText().toString();

                Triangulo triangulo = new Triangulo();
                triangulo.setColor_rojo(colorRojo);
                triangulo.setColor_verde(colorVerde);
                triangulo.setColor_azul(colorAzul);
                triangulo.setIteraciones(iteraciones);
                triangulo.setTipo_triangulo(tipoTriangulo);

                Intent intent = new Intent(MainActivity.this, VisualizacionActivity.class);
                intent.putExtra("triangulo_seleccionado", triangulo);
                startActivity(intent);
            }
        });
    }

    private void limpiar() {
        rgColor.clearCheck();
        rgIteraciones.clearCheck();
        rgTipoTriangulo.clearCheck();
    }
}