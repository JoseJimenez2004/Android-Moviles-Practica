package mx.ipn.escuela.moverimagenes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryView extends View {

    private Bitmap ball;
    private final Paint linePaint;
    private final Handler handler = new Handler();

    private final List<float[]> pathPoints = new ArrayList<>();
    private int currentIndex = 0;

    private static final int FRAME_RATE = 30;

    public TrajectoryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Imagen escalada (más pequeña)
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.ipn);
        ball = Bitmap.createScaledBitmap(original, 100, 100, true);

        // Pintura para la trayectoria
        linePaint = new Paint();
        linePaint.setColor(0xFF0000FF); // azul
        linePaint.setStrokeWidth(8);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Reiniciar trayectoria
                pathPoints.clear();
                currentIndex = 0;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                // Guardar puntos mientras el usuario dibuja
                pathPoints.add(new float[]{x, y});
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // Cuando termina de dibujar → iniciar animación
                if (!pathPoints.isEmpty()) {
                    currentIndex = 0;
                    handler.post(animationRunnable);
                }
                break;
        }
        return true;
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentIndex < pathPoints.size() - 1) {
                currentIndex++;
                invalidate();
                handler.postDelayed(this, FRAME_RATE);
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar la trayectoria
        for (int i = 1; i < pathPoints.size(); i++) {
            float[] p1 = pathPoints.get(i - 1);
            float[] p2 = pathPoints.get(i);
            canvas.drawLine(p1[0], p1[1], p2[0], p2[1], linePaint);
        }

        // Dibujar la imagen en el punto actual de la trayectoria
        if (!pathPoints.isEmpty() && currentIndex < pathPoints.size()) {
            float[] pos = pathPoints.get(currentIndex);
            float drawX = pos[0] - ball.getWidth() / 2f;
            float drawY = pos[1] - ball.getHeight() / 2f;
            canvas.drawBitmap(ball, drawX, drawY, null);
        }
    }
}
