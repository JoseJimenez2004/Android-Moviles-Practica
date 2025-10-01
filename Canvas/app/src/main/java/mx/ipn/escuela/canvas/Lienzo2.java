package mx.ipn.escuela.canvas;

import android.content.*;
import android.graphics.*;
import android.view.View;

public class Lienzo2 extends View {
    Paint p;
    Path r;
    int x, y, x0, y0;
    float A_seno, T_seno, A_cos, T_cos;

    public Lienzo2(Context c, float A_seno, float T_seno, float A_cos, float T_cos) {
        super(c);
        this.A_seno = A_seno;
        this.T_seno = T_seno;
        this.A_cos = A_cos;
        this.T_cos = T_cos;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        p = new Paint();
        r = new Path();

        x = c.getWidth();
        y = c.getHeight();
        x0 = x / 2;
        y0 = y / 2;

        // Fondo blanco
        p.setColor(Color.WHITE);
        c.drawPaint(p);

        // Ejes
        p.setColor(Color.rgb(0, 0, 255));
        c.drawLine(x0, 0, x0, y, p);
        c.drawLine(0, y0, x, y0, p);

        // Texto del origen
        p.setColor(Color.BLACK);
        p.setTextSize(25);
        c.drawText("0,0", x0 + 5, y0 - 10, p);

        // Configuración general
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);
        p.setAntiAlias(true);

        // Función seno
        p.setColor(Color.BLUE);
        c.drawText("sen(x)", 20, 40, p);
        r = new Path();
        r.moveTo(0, 0);
        for (int i = 1; i < x; i++) {
            float yVal = (float) (A_seno * Math.sin((2 * Math.PI / T_seno) * i));
            r.lineTo(i, -yVal);
        }
        r.offset(0, y0);
        c.drawPath(r, p);

        // Función coseno
        p.setColor(Color.RED);
        c.drawText("cos(x)", 20, 70, p);
        r = new Path();
        r.moveTo(0, 0);
        for (int i = 1; i < x; i++) {
            float yVal = (float) (A_cos * Math.cos((2 * Math.PI / T_cos) * i));
            r.lineTo(i, -yVal);
        }
        r.offset(0, y0);
        c.drawPath(r, p);
    }
}
