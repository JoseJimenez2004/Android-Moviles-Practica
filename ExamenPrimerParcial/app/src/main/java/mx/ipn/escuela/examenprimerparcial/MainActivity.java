package mx.ipn.escuela.examenprimerparcial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> secuencia = new ArrayList<>();
    private Button[] botones;
    private LineaView lineaView;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a vistas
        lineaView = findViewById(R.id.linea_view);
        btnGuardar = findViewById(R.id.btn_guardar);
        GridLayout grid = findViewById(R.id.grid_patron);

        // Inicializar botones del patrón
        botones = new Button[]{
                findViewById(R.id.b1), findViewById(R.id.b2), findViewById(R.id.b3),
                findViewById(R.id.b4), findViewById(R.id.b5), findViewById(R.id.b6),
                findViewById(R.id.b7), findViewById(R.id.b8), findViewById(R.id.b9)
        };

        for (int i = 0; i < botones.length; i++) {
            int numero = i + 1;
            Button boton = botones[i];

            boton.setOnClickListener(v -> {
                if (!secuencia.contains(numero)) {
                    secuencia.add(numero);
                    boton.setAlpha(0.6f); // marca visualmente que fue seleccionado

                    // obtener coordenadas absolutas del centro del botón
                    int[] loc = new int[2];
                    boton.getLocationOnScreen(loc);
                    float cx = loc[0] + boton.getWidth() / 2f;
                    float cy = loc[1] + boton.getHeight() / 2f;
                    lineaView.agregarPunto(new PointF(cx, cy));
                } else {
                    Toast.makeText(this, "Ya seleccionado", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> {
            if (secuencia.size() >= 3) {
                StringBuilder patron = new StringBuilder();
                for (int n : secuencia) patron.append(n);

                Toast.makeText(this, "Patrón guardado: " + patron, Toast.LENGTH_LONG).show();

                // Aquí puedes enviar el patrón a tu clase de base de datos:
                // DatabaseHelper db = new DatabaseHelper(this);
                // db.insertarPatron(correo, patron.toString());

                finish(); // opcional: cerrar actividad
            } else {
                Toast.makeText(this, "Seleccione al menos 3 puntos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ======================================================
    // Clase interna para dibujar las líneas del patrón
    // ======================================================
    public static class LineaView extends View {

        private final Paint paint;
        private final ArrayList<PointF> puntos = new ArrayList<>();

        public LineaView(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint = new Paint();
            paint.setColor(Color.parseColor("#7E57C2")); // Morado
            paint.setStrokeWidth(8f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
        }

        public void agregarPunto(PointF p) {
            puntos.add(p);
            invalidate(); // redibuja
        }

        public void reiniciar() {
            puntos.clear();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 1; i < puntos.size(); i++) {
                PointF p1 = puntos.get(i - 1);
                PointF p2 = puntos.get(i);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
            }
        }
    }
}
