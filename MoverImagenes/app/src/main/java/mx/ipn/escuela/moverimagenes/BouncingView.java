package mx.ipn.escuela.moverimagenes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BouncingView extends View {

    private final Handler handler = new Handler();
    private static final int FRAME_RATE = 30;

    private Bitmap ball;
    private float imageX = 0;
    private float imageY = 0;
    private float velocityX = 8; // más rápido para que rebote visible
    private float velocityY = 10;

    private int screenWidth;
    private int screenHeight;
    private int imageWidth;
    private int imageHeight;

    private final Paint pathPaint;
    private final List<float[]> trailPoints = new ArrayList<>();

    public BouncingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Cargar la imagen original
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.ipn);

        // Escalarla (ejemplo: 100x100 px)
        int newSize = 100;
        ball = Bitmap.createScaledBitmap(original, newSize, newSize, true);

        imageWidth = ball.getWidth();
        imageHeight = ball.getHeight();

        // Pintura para la trayectoria
        pathPaint = new Paint();
        pathPaint.setColor(0x55FF0000); // Rojo semitransparente
        pathPaint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        // Posición inicial en el centro
        imageX = (screenWidth - imageWidth) / 2f;
        imageY = (screenHeight - imageHeight) / 2f;

        // Arrancar animación
        handler.postDelayed(movementRunnable, FRAME_RATE);
    }

    private final Runnable movementRunnable = new Runnable() {
        @Override
        public void run() {
            // Mover
            imageX += velocityX;
            imageY += velocityY;

            // Rebote X
            if (imageX + imageWidth > screenWidth) {
                imageX = screenWidth - imageWidth;
                velocityX *= -1;
            } else if (imageX < 0) {
                imageX = 0;
                velocityX *= -1;
            }

            // Rebote Y
            if (imageY + imageHeight > screenHeight) {
                imageY = screenHeight - imageHeight;
                velocityY *= -1;
            } else if (imageY < 0) {
                imageY = 0;
                velocityY *= -1;
            }

            // Guardar punto de trayectoria (centro de la imagen)
            trailPoints.add(new float[]{imageX + imageWidth / 2f, imageY + imageHeight / 2f});

            // Redibujar
            invalidate();

            // Próxima actualización
            handler.postDelayed(this, FRAME_RATE);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar trayectoria
        for (int i = 1; i < trailPoints.size(); i++) {
            float[] p1 = trailPoints.get(i - 1);
            float[] p2 = trailPoints.get(i);
            canvas.drawLine(p1[0], p1[1], p2[0], p2[1], pathPaint);
        }

        // Dibujar la imagen
        canvas.drawBitmap(ball, imageX, imageY, null);
    }
}
