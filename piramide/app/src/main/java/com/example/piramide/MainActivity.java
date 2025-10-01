package com.example.piramide;

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
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(3);
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
        drawPyramid(canvas);
    }

    private void drawPyramid(Canvas canvas) {
        // Dibujar los bordes de la base cuadrada
        drawLine(canvas, 0, 1);
        drawLine(canvas, 1, 2);
        drawLine(canvas, 2, 3);
        drawLine(canvas, 3, 0);

        // Dibujar las aristas que conectan la base con el vértice superior
        for (int i = 0; i < 4; i++) {
            drawLine(canvas, i, 4); // 4 es el índice del vértice superior
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
        w = new Point3D[5]; // 4 vértices de la base + 1 vértice superior
        vScr = new Point2D[5];

        // Base cuadrada (z = -1)
        w[0] = new Point3D(-1, -1, -1);
        w[1] = new Point3D(1, -1, -1);
        w[2] = new Point3D(1, 1, -1);
        w[3] = new Point3D(-1, 1, -1);

        // Vértice superior (z = 1)
        w[4] = new Point3D(0, 0, 1);

        objSize = 2; // Tamaño de la pirámide
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
        for (int i = 0; i < w.length; i++) {
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