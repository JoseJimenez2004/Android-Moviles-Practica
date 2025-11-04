package mx.ipn.escuela.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import java.util.Timer;
import java.util.TimerTask;

public class MiCrono extends Service {
    private Timer t = new Timer();
    private static final long INTERVALO_ACTUALIZACION = 10; // En milisegundos
    public static MainActivity UPDATE_LISTENER;
    private double n = 0;
    private Handler h;
    private boolean isPaused = false;

    public static void setUpdateListener(MainActivity sta) {
        UPDATE_LISTENER = sta;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (UPDATE_LISTENER != null) {
                    UPDATE_LISTENER.refreshCrono(n);
                }
            }
        };
        iniciarCrono();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals("PAUSE")) {
                isPaused = true;
            } else if (action.equals("CONTINUE")) {
                isPaused = false;
            } else if (action.equals("START")) {
                n = 0; // Reinicia el cronómetro al iniciar
                isPaused = false;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        pararCrono();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void iniciarCrono() {
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!isPaused) {
                    n += 0.01;
                    h.sendEmptyMessage(0);
                }
            }
        }, 0, INTERVALO_ACTUALIZACION);
    }

    private void pararCrono() {
        if (t != null) {
            t.cancel();
        }
    }
}