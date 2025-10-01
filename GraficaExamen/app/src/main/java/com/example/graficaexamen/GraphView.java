package com.example.graficaexamen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {

    private Paint paint;
    private List<float[]> points;
    private float maxX, maxY; // Escalado máximo

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        points = new ArrayList<>();
    }

    // Método para establecer puntos y límites de la gráfica
    public void setPoints(List<float[]> newPoints, float maxX, float maxY) {
        this.points = newPoints;
        this.maxX = maxX;
        this.maxY = maxY;
        invalidate(); // Redibujar la vista
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dimensiones del lienzo
        float width = getWidth();
        float height = getHeight();

        // Margen
        float margin = 50;

        // Escalado
        float scaleX = (width - 2 * margin) / maxX;
        float scaleY = (height - 2 * margin) / maxY;

        // Dibujar cuadrícula
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(1);

        // Líneas verticales (eje X)
        for (int i = 0; i <= maxX; i += 4) { // Intervalos de 4 unidades en X
            float x = margin + i * scaleX;
            canvas.drawLine(x, margin, x, height - margin, paint);
        }

        // Líneas horizontales (eje Y)
        for (int i = 0; i <= maxY; i += 20) { // Intervalos de 20 unidades en Y
            float y = height - margin - i * scaleY;
            canvas.drawLine(margin, y, width - margin, y, paint);
        }

        // Dibujar ejes
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawLine(margin, height - margin, width - margin, height - margin, paint); // Eje X
        canvas.drawLine(margin, height - margin, margin, margin, paint); // Eje Y

        // Dibujar etiquetas de los ejes
        paint.setTextSize(30);
        paint.setColor(Color.BLACK);

        // Etiquetas del eje X
        for (int i = 0; i <= maxX; i += 4) {
            float x = margin + i * scaleX;
            canvas.drawText(String.valueOf(i), x - 10, height - margin + 30, paint);
        }

        // Etiquetas del eje Y
        for (int i = 0; i <= maxY; i += 20) {
            float y = height - margin - i * scaleY;
            canvas.drawText(String.valueOf(i), margin - 40, y + 10, paint);
        }

        // Dibujar puntos y líneas de la gráfica
        if (!points.isEmpty()) {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            for (int i = 0; i < points.size() - 1; i++) {
                // Escalar los puntos
                float x1 = margin + points.get(i)[0] * scaleX;
                float y1 = height - margin - points.get(i)[1] * scaleY;
                float x2 = margin + points.get(i + 1)[0] * scaleX;
                float y2 = height - margin - points.get(i + 1)[1] * scaleY;

                // Dibujar línea entre los puntos
                canvas.drawLine(x1, y1, x2, y2, paint);
            }
        }
    }
}
