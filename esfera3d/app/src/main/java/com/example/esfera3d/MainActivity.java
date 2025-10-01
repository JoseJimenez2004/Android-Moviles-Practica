package com.example.esfera3d;

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
        setContentView(new Perspectiva(this, 20, 20)); // Esfera con 10 divisiones en theta y phi
    }
}

class Perspectiva extends View {
    int centerX, centerY, maxX, maxY, minMaxXY;
    Obj obj;
    Paint paint = new Paint();

    public Perspectiva(Context context, int numDivTheta, int numDivPhi) {
        super(context);
        obj = new Obj(numDivTheta, numDivPhi);
        paint.setColor(Color.MAGENTA);
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
        drawSphere(canvas);
    }

    private void drawSphere(Canvas canvas) {
        for (int i = 0; i < obj.numDivTheta - 1; i++) {
            for (int j = 0; j < obj.numDivPhi - 1; j++) {
                drawLine(canvas, i, j, i + 1, j); // Líneas entre divisiones de theta
                drawLine(canvas, i, j, i, j + 1); // Líneas entre divisiones de phi
            }
        }
    }

    private void drawLine(Canvas canvas, int i1, int j1, int i2, int j2) {
        Point2D p = obj.vScr[i1][j1];
        Point2D q = obj.vScr[i2][j2];
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
            obj.theta = getWidth() / x;
            obj.phi = getHeight() / y;
            obj.rho = (obj.phi / obj.theta) * getHeight();
            centerX = (int) x;
            centerY = (int) y;
            invalidate(); // Redibuja la vista
        }
        return true;
    }
}

class Obj {
    float rho, theta = 0.3F, phi = 1.3F, d, objSize, v11, v12, v13, v21, v22, v23, v32, v33, v43;
    Point3D[][] w;
    Point2D[][] vScr;
    int numDivTheta, numDivPhi;

    Obj(int numDivTheta, int numDivPhi) {
        this.numDivTheta = numDivTheta;
        this.numDivPhi = numDivPhi;

        // Esfera: matriz de vértices (numDivTheta x numDivPhi)
        w = new Point3D[numDivTheta][numDivPhi];
        vScr = new Point2D[numDivTheta][numDivPhi];

        // Calcular vértices usando coordenadas esféricas
        float r = 1; // Radio de la esfera
        for (int i = 0; i < numDivTheta; i++) {
            float theta = (float) (Math.PI * i / (numDivTheta - 1));  // Ángulo polar (0 a PI)
            for (int j = 0; j < numDivPhi; j++) {
                float phi = (float) (2 * Math.PI * j / (numDivPhi - 1));  // Ángulo azimutal (0 a 2PI)
                float x = r * (float) (Math.sin(theta) * Math.cos(phi));
                float y = r * (float) (Math.sin(theta) * Math.sin(phi));
                float z = r * (float) Math.cos(theta);
                w[i][j] = new Point3D(x, y, z);
            }
        }

        objSize = 2.0f;  // Ajusta el tamaño de la esfera
        rho = 5 * objSize;
    }

    void initPersp() {
        float costh = (float) Math.cos(theta), sinth = (float) Math.sin(theta),
                cosph = (float) Math.cos(phi), sinph = (float) Math.sin(phi);
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
        for (int i = 0; i < numDivTheta; i++) {
            for (int j = 0; j < numDivPhi; j++) {
                Point3D p = w[i][j];
                float x = v11 * p.x + v21 * p.y;
                float y = v12 * p.x + v22 * p.y + v32 * p.z;
                float z = v13 * p.x + v23 * p.y + v33 * p.z + v43;
                vScr[i][j] = new Point2D(-d * x / z, -d * y / z);
            }
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

    Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}