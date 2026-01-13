package mx.ipn.escuela.proyecto2_joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class JoystickView extends View {

    private Paint circlePaint;
    private float circleX, circleY;
    private float circleRadius = 50f;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#FF4081")); // Un color más vibrante
        circlePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Centrar el círculo cuando el tamaño de la vista esté disponible
        centerCircle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(circleX, circleY, circleRadius, circlePaint);
    }

    /**
     * Mueve el círculo a la posición central de la pantalla.
     */
    public void centerCircle() {
        circleX = getWidth() / 2f;
        circleY = getHeight() / 2f;
        // Llama a invalidate para forzar el redibujado
        invalidate();
    }

    /**
     * Actualiza la posición del círculo basándose en el movimiento del joystick.
     * @param dx El cambio en el eje X (-1.0 a 1.0).
     * @param dy El cambio en el eje Y (-1.0 a 1.0).
     */
    public void updatePosition(float dx, float dy) {
        float newX = circleX + dx;
        float newY = circleY + dy;

        // Asegurar que el círculo no se salga de los límites de la pantalla
        // Límite izquierdo
        if (newX - circleRadius < 0) {
            newX = circleRadius;
        }
        // Límite derecho
        if (newX + circleRadius > getWidth()) {
            newX = getWidth() - circleRadius;
        }
        // Límite superior
        if (newY - circleRadius < 0) {
            newY = circleRadius;
        }
        // Límite inferior
        if (newY + circleRadius > getHeight()) {
            newY = getHeight() - circleRadius;
        }

        circleX = newX;
        circleY = newY;

        // Solicita un redibujado. Esto es más eficiente que invalidate() para animaciones.
        postInvalidateOnAnimation();
    }
}
