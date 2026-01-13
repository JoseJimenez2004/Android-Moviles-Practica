package mx.ipn.escuela.hilos2;

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
        Hilo h1 = new Hilo(10, 1000);
        Hilo h2 = new Hilo(5, 500);
        h2.setPriority(7);
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
                b.putString("hilo", currentThread().toString());
                m.setData(b);
                h.sendMessage(m);
            }
        }
    }

    class Control extends Handler {
        public void handleMessage(Message m) {
            int c = m.getData().getInt("cuenta");
            String h = m.getData().getString("hilo");
            jtv1.append("\n" + h + ", " + "Contador: " + c);
        }
    }
}