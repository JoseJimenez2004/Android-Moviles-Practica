package mx.ipn.escuela.cubo3danimacion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CubeView cubeView = new CubeView(this);
        setContentView(cubeView);
    }

    public class CubeView extends View {
        private Obj obj = new Obj();
        private Paint paint = new Paint();
        private float centerX, centerY;

        public CubeView(Context context) {
            super(context);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5);
            paint.setAntiAlias(true);
            setBackgroundColor(Color.YELLOW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            centerX = width / 2f;
            centerY = height / 2f;
            int minMaxXY = Math.min(width, height);

            obj.d = obj.rho * minMaxXY / obj.objSize;
            obj.eyeAndScreen();

            // Dibujar líneas del cubo
            line(canvas, 0, 1);
            line(canvas, 1, 2);
            line(canvas, 2, 3);
            line(canvas, 3, 0);
            line(canvas, 4, 5);
            line(canvas, 5, 6);
            line(canvas, 6, 7);
            line(canvas, 7, 4);
            line(canvas, 0, 4);
            line(canvas, 1, 5);
            line(canvas, 2, 6);
            line(canvas, 3, 7);
        }

        private void line(Canvas canvas, int i, int j) {
            Point2D p = obj.vScr[i];
            Point2D q = obj.vScr[j];
            float x1 = centerX + p.x;
            float y1 = centerY - p.y;
            float x2 = centerX + q.x;
            float y2 = centerY - q.y;
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float x = event.getX();
                float y = event.getY();
                obj.theta = getWidth() / x;
                obj.phi = getHeight() / y;
                obj.rho = (obj.phi / obj.theta) * getHeight();
                invalidate();
            }
            return true;
        }
    }

    // --- Clases auxiliares (igual que tu código original adaptado) ---
    class Obj {
        float rho, theta = 0.3F, phi = 1.3F, d, objSize;
        float v11, v12, v13, v21, v22, v23, v32, v33, v43;
        Point3D[] w;
        Point2D[] vScr;

        Obj() {
            w = new Point3D[8];
            vScr = new Point2D[8];

            // Coordenadas del cubo
            w[0] = new Point3D(1, -1, -1);
            w[1] = new Point3D(1, 1, -1);
            w[2] = new Point3D(-1, 1, -1);
            w[3] = new Point3D(-1, -1, -1);
            w[4] = new Point3D(1, -1, 1);
            w[5] = new Point3D(1, 1, 1);
            w[6] = new Point3D(-1, 1, 1);
            w[7] = new Point3D(-1, -1, 1);

            objSize = (float) Math.sqrt(12F);
            rho = 5 * objSize;
        }

        void initPersp() {
            float costh = (float) Math.cos(theta),
                    sinth = (float) Math.sin(theta),
                    cosph = (float) Math.cos(phi),
                    sinph = (float) Math.sin(phi);
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
            for (int i = 0; i < 8; i++) {
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
        Point2D(float x, float y) { this.x = x; this.y = y; }
    }

    class Point3D {
        float x, y, z;
        Point3D(double x, double y, double z) {
            this.x = (float) x;
            this.y = (float) y;
            this.z = (float) z;
        }
    }
}
