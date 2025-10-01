package mx.ipn.escuela.dibujo2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;


public class MainActivity extends AppCompatActivity {
    private Lienzo l;

    @Override
    protected void onCreate (Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        ConstraintLayout cl = findViewById(R.id.x11);
        l = new Lienzo(this);
        // NO se necesita l.setOnTouchListener(this) porque Lienzo implementa onTouchEvent
        cl.addView(l);
    }

    // Clase Lienzo modificada para el Ejercicio
    class Lienzo extends View {
        private Path drawnPath; // Para almacenar la línea dibujada por el usuario
        private Paint pathPaint; // Para el estilo de la línea dibujada a mano
        private Paint pointPaint; // Para el estilo de los círculos fijos

        // Coordenadas fijas para los tres puntos (ajuste estos valores según sea necesario)
        private final float[] fixedPointsX = {200f, 500f, 800f};
        private final float[] fixedPointsY = {300f, 800f, 300f};
        private final float pointRadius = 20f;

        public Lienzo (Context c) {
            super(c);
            drawnPath = new Path();

            // Configuración del Paint para la línea a mano (azul, grosor visible)
            pathPaint = new Paint();
            pathPaint.setColor(Color.BLUE);
            pathPaint.setStyle(Paint.Style.STROKE);
            pathPaint.setStrokeWidth(8);
            pathPaint.setAntiAlias(true);

            // Configuración del Paint para los puntos fijos (rojo, solo borde)
            pointPaint = new Paint();
            pointPaint.setColor(Color.RED);
            pointPaint.setStyle(Paint.Style.STROKE);
            pointPaint.setStrokeWidth(4);
            pointPaint.setAntiAlias(true);
        }

        protected void onDraw(Canvas c) {
            // Fondo amarillo (como en el Ejemplo 2)
            c.drawRGB(255, 255, 0);

            // 1. Dibujar los tres puntos fijos (circunferencias rojas)
            for (int i = 0; i < fixedPointsX.length; i++) {
                c.drawCircle(fixedPointsX[i], fixedPointsY[i], pointRadius, pointPaint);
            }

            // 2. Dibujar el Path creado por el usuario
            c.drawPath(drawnPath, pathPaint);
        }

        // Manejo de eventos táctiles para dibujar
        @Override
        public boolean onTouchEvent (MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Inicia una nueva línea en el punto de contacto
                    drawnPath.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Dibuja una línea continua hasta la nueva posición
                    drawnPath.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    // Opcional: Podríamos dejar de dibujar o realizar alguna acción al soltar.
                    // En este caso, simplemente se detiene el registro de líneas.
                    break;
            }

            // Llama a onDraw para redibujar la vista con el nuevo segmento de línea
            invalidate();
            return true;
        }
    }
}