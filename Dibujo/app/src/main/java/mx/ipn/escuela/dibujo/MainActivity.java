package mx.ipn.escuela.dibujo;

import android.content.Context;
import android.os.*;
import android.app.Activity; // Se ajusta la importación de Activity
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity{ // Extiende de Activity
    Lienzo l;

    public void onCreate(Bundle b) {
        super.onCreate(b); // La llamada a super.onCreate(b) debe ir primero
        l = new Lienzo(this); // Instanciación de Lienzo
        setContentView(l);
    }

    class Lienzo extends View{
        Path pt;
        Paint pn;
        String s;
        float x, y;

        public Lienzo (Context c) {
            super(c);
            pt = new Path();
        }

        public void onDraw (Canvas c) {
            pn = new Paint();
            pn.setStyle(Paint.Style.STROKE);
            pn.setStrokeWidth(3);
            pn.setColor(Color.BLACK);
            // Color de fondo amarillo claro (250, 250, 100)
            c.drawColor(Color.rgb(250, 250, 100));

            // Si s es "00" (ACTION_DOWN), mueve el punto de inicio del Path
            if (s != null && s.equals("00")) {
                pt.moveTo(x, y);
            }
            // Si s es "xy" (ACTION_MOVE), dibuja una línea hasta el nuevo punto
            if (s != null && s.equals("xy")) {
                pt.lineTo(x, y);
            }

            c.drawPath(pt, pn);
        }

        public boolean onTouchEvent (MotionEvent e) {
            x = e.getX(); // Obtiene la coordenada X del evento
            y = e.getY(); // Obtiene la coordenada Y del evento

            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                s = "00";
            }
            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                s = "xy";
            }
            // Llamar a invalidate() para forzar a la vista a redibujarse (llama a onDraw)
            invalidate();

            return true;
        }
    }
}