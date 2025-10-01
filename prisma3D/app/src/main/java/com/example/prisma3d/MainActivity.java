package com.example.prisma3d;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Perspectiva(this));
    }
}

class Perspectiva extends View {
    int centerX, centerY, maxX, maxY, minMaxXY;
    Obj obj = new Obj();
    Paint paint = new Paint();

    public Perspectiva(Context context) {
        super(context);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        maxX = getWidth() - 1;
        maxY = getHeight() - 1;
        minMaxXY = Math.min(maxX, maxY);
        centerX = maxX / 2;
        centerY = maxY / 2;
        obj.d = obj.rho * minMaxXY / obj.objSize;
        obj.eyeAndScreen();
        drawPrism(canvas);
    }

    private void drawPrism(Canvas canvas) {
        // Dibujar base inferior
        for (int i = 0; i < 5; i++) {
            drawLine(canvas, i, (i + 1) % 5);
        }
        // Dibujar base superior
        for (int i = 5; i < 10; i++) {
            drawLine(canvas, i, 5 + (i + 1) % 5);
        }
        // Dibujar aristas verticales
        for (int i = 0; i < 5; i++) {
            drawLine(canvas, i, i + 5);
        }
    }

    private void drawLine(Canvas canvas, int i, int j) {
        Point2D p = obj.vScr[i];
        Point2D q = obj.vScr[j];
        canvas.drawLine(iX(p.x), iY(p.y), iX(q.x), iY(q.y), paint);
    }

    private int iX(float x) {
        return Math.round(centerX + x);
    }

    private int iY(float y) {
        return Math.round(centerY - y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            obj.theta += (x - centerX) * 0.0001f;
            obj.phi += (y - centerY) * 0.0001f;
            centerX = (int) x;
            centerY = (int) y;
            invalidate(); // Redibuja la vista
        }
        return true;
    }
}

class Obj {
    float rho, theta = 0.3F, phi = 1.3F, d, objSize, v11, v12, v13, v21, v22, v23, v32, v33, v43;
    Point3D[] w;
    Point2D[] vScr;

    Obj() {
        w = new Point3D[10];
        vScr = new Point2D[10];

        // Base inferior (z = -1)
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI / 5 * i;
            w[i] = new Point3D(Math.cos(angle), Math.sin(angle), -1);
        }

        // Base superior (z = 1)
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI / 5 * i;
            w[i + 5] = new Point3D(Math.cos(angle), Math.sin(angle), 1);
        }

        objSize = 2; // Tamaño del prisma
        rho = 5 * objSize;
    }

    void initPersp() {
        float costh = (float) Math.cos(theta), sinth = (float) Math.sin(theta), cosph = (float) Math.cos(phi), sinph = (float) Math.sin(phi);
        v11 = -sinth;
        v12 = -cosph * costh;
        v13 = sinph * costh;
        v21 = costh;
        v22 = -cosph * sinth;
        v23 = sinph * sinth;
        v32 = sinph;
        v33 = cosph;
        v43 = -rho;
    }

    void eyeAndScreen() {
        initPersp();
        for (int i = 0; i < 10; i++) {
            Point3D p = w[i];
            float x = v11 * p.x + v21 * p.y;
            float y = v12 * p.x + v22 * p.y + v32 * p.z;
            float z = v13 * p.x + v23 * p.y + v33 * p.z + v43;
            vScr[i] = new Point2D(-d * x / z, -d * y / z);
        }
    }
}

class Point2D {
    float x, y;

    Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

class Point3D {
    float x, y, z;

    Point3D(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }
}