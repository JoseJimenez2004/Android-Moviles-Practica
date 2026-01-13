package mx.ipn.escuela.proyecto2_realidadvirtual;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

public class VistaCubo extends View {

    private Paint paint;
    private float angulo = 0;

    // Coordenadas de los 8 vértices de un cubo (X, Y, Z)
    private float[][] vertices = {
            {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
            {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1}
    };

    // Conexiones entre vértices (índices del array 'vertices') para formar las aristas
    private int[][] aristas = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Cara trasera
            {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Cara delantera
            {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Uniones entre caras
    };

    public VistaCubo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VistaCubo(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.CYAN); // Color de las líneas
        paint.setStrokeWidth(8);    // Grosor de las líneas
        paint.setAntiAlias(true);   // Suavizar bordes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fondo transparente para que el video se vea detrás
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Centro de la pantalla (o de la mitad de pantalla en VR)
        int ancho = getWidth();
        int alto = getHeight();
        int centerX = ancho / 2;
        int centerY = alto / 2;

        // Factor de escala (Zoom) para que el cubo se vea grande
        float escala = Math.min(ancho, alto) / 4.0f;

        paint.setColor(Color.MAGENTA); // Color del cubo

        // Matriz de rotación simple
        angulo += 0.02; // Velocidad de giro

        // Dibujar cada arista
        for (int[] arista : aristas) {
            float[] p1 = vertices[arista[0]];
            float[] p2 = vertices[arista[1]];

            // Proyectar y rotar punto 1
            float[] r1 = rotarYproyectar(p1[0], p1[1], p1[2], escala);
            // Proyectar y rotar punto 2
            float[] r2 = rotarYproyectar(p2[0], p2[1], p2[2], escala);

            // Dibujar línea ajustada al centro de la vista
            canvas.drawLine(centerX + r1[0], centerY - r1[1], 
                            centerX + r2[0], centerY - r2[1], paint);
        }

        // Forzar repintado para animar (bucle infinito)
        invalidate();
    }

    // Función matemática para rotar en 3D y convertir a 2D
    private float[] rotarYproyectar(float x, float y, float z, float escala) {
        // Rotación en eje Y
        float xRot = (float) (x * Math.cos(angulo) - z * Math.sin(angulo));
        float zRot = (float) (x * Math.sin(angulo) + z * Math.cos(angulo));
        
        // Rotación en eje X (opcional, para que gire en diagonal)
        float yRot = (float) (y * Math.cos(angulo) - zRot * Math.sin(angulo));
        
        // Retornamos X e Y multiplicados por la escala para la pantalla
        // (Proyección ortográfica simple)
        return new float[]{xRot * escala, yRot * escala};
    }
}
