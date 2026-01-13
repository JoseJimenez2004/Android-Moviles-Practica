package mx.ipn.escuela.laberinto;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LaberintoView laberintoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout container = findViewById(R.id.container);
        laberintoView = new LaberintoView(this);
        container.addView(laberintoView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (laberintoView != null) {
            float x = -event.values[0];
            float y = event.values[1];
            laberintoView.moverBola(x, y);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    class LaberintoView extends View {
        private Paint paintBola, paintPared, paintMeta, paintVacio;
        private float posX, posY;
        private float radio;
        private float celdaAncho, celdaAlto;

        private boolean juegoTerminado = false;

        // 0 = vacío, 1 = pared, 2 = inicio, 3 = meta
        private int[][] laberinto = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
                {1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 3, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        private int filas = laberinto.length;
        private int columnas = laberinto[0].length;

        public LaberintoView(Context context) {
            super(context);
            paintBola = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintBola.setColor(Color.rgb(229, 57, 53)); // Red

            paintPared = new Paint();
            paintPared.setColor(Color.rgb(26, 35, 126)); // Dark Blue

            paintVacio = new Paint();
            paintVacio.setColor(Color.rgb(236, 239, 241)); // Light Gray

            paintMeta = new Paint();
            paintMeta.setColor(Color.rgb(255, 215, 0)); // Gold
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            celdaAncho = (float) w / columnas;
            celdaAlto = (float) h / filas;
            radio = Math.min(celdaAncho, celdaAlto) / 2.8f;

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    if (laberinto[i][j] == 2) {
                        posX = j * celdaAncho + celdaAncho / 2;
                        posY = i * celdaAlto + celdaAlto / 2;
                        break;
                    }
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(paintVacio.getColor());

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    float x = j * celdaAncho;
                    float y = i * celdaAlto;
                    if (laberinto[i][j] == 1) {
                        canvas.drawRect(x, y, x + celdaAncho, y + celdaAlto, paintPared);
                    } else if (laberinto[i][j] == 3) {
                        canvas.drawRect(x, y, x + celdaAncho, y + celdaAlto, paintMeta);
                    }
                }
            }

            canvas.drawCircle(posX, posY, radio, paintBola);

            if (!juegoTerminado) {
                invalidate();
            }
        }

        public void moverBola(float dx, float dy) {
            if (juegoTerminado) return;

            float futuraPosX = posX + dx * 2.0f;
            float futuraPosY = posY + dy * 2.0f;

            // Check for wall collisions
            int futuraCol = (int) (futuraPosX / celdaAncho);
            int futuraFila = (int) (futuraPosY / celdaAlto);

            if (futuraCol >= 0 && futuraCol < columnas && futuraFila >= 0 && futuraFila < filas) {
                int checkFila = (int) ((futuraPosY + (dy > 0 ? radio : -radio)) / celdaAlto);
                if (checkFila >= 0 && checkFila < filas && (laberinto[checkFila][(int)(posX/celdaAncho)] == 0 || laberinto[checkFila][(int)(posX/celdaAncho)] == 2 || laberinto[checkFila][(int)(posX/celdaAncho)] == 3)) {
                    posY = futuraPosY;
                }

                int checkCol = (int) ((futuraPosX + (dx > 0 ? radio : -radio)) / celdaAncho);
                if (checkCol >= 0 && checkCol < columnas && (laberinto[(int)(posY/celdaAlto)][checkCol] == 0 || laberinto[(int)(posY/celdaAlto)][checkCol] == 2 || laberinto[(int)(posY/celdaAlto)][checkCol] == 3)) {
                    posX = futuraPosX;
                }
            }

            // Screen edge collision
            if (posX - radio < 0) posX = radio;
            if (posX + radio > getWidth()) posX = getWidth() - radio;
            if (posY - radio < 0) posY = radio;
            if (posY + radio > getHeight()) posY = getHeight() - radio;

            // Win condition
            int finalCol = (int) (posX / celdaAncho);
            int finalFila = (int) (posY / celdaAlto);

            if (finalCol >= 0 && finalCol < columnas && finalFila >= 0 && finalFila < filas) {
                if (laberinto[finalFila][finalCol] == 3) {
                    juegoTerminado = true;
                    ((Activity)getContext()).runOnUiThread(() -> Toast.makeText(getContext(), "¡Felicidades, ganaste!", Toast.LENGTH_LONG).show());
                }
            }
        }
    }
}
