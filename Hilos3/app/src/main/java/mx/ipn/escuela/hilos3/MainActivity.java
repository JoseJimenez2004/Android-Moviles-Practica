package mx.ipn.escuela.hilos3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MainActivity extends Activity {

    Handler h = new Control();
    TextView jtv1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        jtv1 = (TextView) findViewById(R.id.xtv1);

        // e. Cambiar la temporización
        Hilo h1 = new Hilo(10, 800);  // 10 veces, cada 800ms
        Hilo h2 = new Hilo(15, 500); // 15 veces, cada 500ms

        // a. Asignar nombre
        h1.setName("HILO 1");
        h2.setName("HILO 2");

        h2.setPriority(Thread.MAX_PRIORITY); // Damos prioridad máxima al Hilo 2

        h1.start();
        h2.start();
    }

    class Hilo extends Thread {
        int n, t;
        Message m;
        Bundle b;

        Hilo(int n, int t) {
            this.n = n;
            this.t = t;
        }

        public void run() {
            for (int i = 0; i < n; i++) {
                try {
                    Thread.sleep(t);
                } catch (InterruptedException ie) {
                }

                m = new Message();
                b = new Bundle();
                b.putInt("cuenta", i);

                // b. Mostrar el nombre del hilo
                b.putString("hilo", currentThread().getName().toString());

                // d. Obtener y mostrar el ID
                b.putLong("id", currentThread().getId());

                m.setData(b);
                h.sendMessage(m);
            }
        }
    }

    class Control extends Handler {
        public void handleMessage(Message m) {
            // Extraer todos los datos del Bundle
            int c = m.getData().getInt("cuenta");
            String hilo = m.getData().getString("hilo");
            long id = m.getData().getLong("id");

            // Construir el mensaje para la UI
            String mensaje = "\nID: " + id + ", " + hilo + ", Contador: " + c;

            jtv1.append(mensaje);
        }
    }
}