package mx.ipn.escuela.examen_segundo_parcial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

import mx.ipn.escuela.examen_segundo_parcial.entidades.Triangulo;

public class FractalView extends View {

    private Paint paint;
    private Paint linePaint; // Paint para la línea divisoria
    private Triangulo triangulo;
    private Random random = new Random();
    private float rotationAngle = 0f;

    public FractalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(2f);
    }

    public void setTriangulo(Triangulo triangulo) {
        this.triangulo = triangulo;
        invalidate();
    }

    public void setRotationAngle(float angle) {
        this.rotationAngle = angle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (triangulo == null) {
            return;
        }

        int totalWidth = getWidth();
        int totalHeight = getHeight();
        int viewportWidth = totalWidth / 2;

        // --- Dibuja el recuadro izquierdo ---
        canvas.save();
        canvas.clipRect(0, 0, viewportWidth, totalHeight);
        drawFractalInViewport(canvas, viewportWidth, totalHeight);
        canvas.restore();

        // --- Dibuja el recuadro derecho ---
        canvas.save();
        canvas.translate(viewportWidth, 0);
        canvas.clipRect(0, 0, viewportWidth, totalHeight);
        drawFractalInViewport(canvas, viewportWidth, totalHeight);
        canvas.restore();
        
        // --- Dibuja la línea divisoria ---
        canvas.drawLine(viewportWidth, 0, viewportWidth, totalHeight, linePaint);
    }

    private void drawFractalInViewport(Canvas canvas, int width, int height) {
        canvas.drawColor(Color.BLACK);

        canvas.save();
        canvas.rotate(rotationAngle, width / 2f, height / 2f);

        paint.setColor(Color.rgb(triangulo.getColor_rojo(), triangulo.getColor_verde(), triangulo.getColor_azul()));

        PointF v1 = new PointF();
        PointF v2 = new PointF();
        PointF v3 = new PointF();
        
        // Margen aumentado al 35% para hacer la figura aún más pequeña
        float marginH = width * 0.35f;
        float marginV = height * 0.35f;

        switch (triangulo.getTipo_triangulo()) {
            case "Equilátero":
            case "Isósceles":
                v1.set(width / 2f, marginV);
                v2.set(marginH, height - marginV);
                v3.set(width - marginH, height - marginV);
                break;
            case "Rectángulo":
                v1.set(marginH, marginV);
                v2.set(marginH, height - marginV);
                v3.set(width - marginH, height - marginV);
                break;
        }

        canvas.drawCircle(v1.x, v1.y, 5f, paint);
        canvas.drawCircle(v2.x, v2.y, 5f, paint);
        canvas.drawCircle(v3.x, v3.y, 5f, paint);

        PointF p = new PointF(random.nextInt(width), random.nextInt(height));
        PointF[] vertices = {v1, v2, v3};

        for (int i = 0; i < triangulo.getIteraciones(); i++) {
            PointF vn = vertices[random.nextInt(3)];
            PointF pm = new PointF((p.x + vn.x) / 2, (p.y + vn.y) / 2);
            canvas.drawCircle(pm.x, pm.y, 2f, paint);
            p = pm;
        }

        canvas.restore();
    }
}