package mx.ipn.escuela.archivosejemplo1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity { 
    TextView tv;
    TextView txtDatos;
    String s;
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.xtv);
        txtDatos = (TextView) findViewById(R.id.txt_datos); 
        tv.append("\nAbriendo: res/raw/misdatos.txt");

        is = getResources().openRawResource(R.raw.misdatos);
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr, 8192);
        StringBuilder textoLeido = new StringBuilder();

        try {
            while (null != (s = br.readLine())) {
                textoLeido.append(s).append("\n");
            }
            is.close();
            isr.close();
            br.close();
            txtDatos.setText(textoLeido.toString());
        } catch (Exception e) {
            tv.append("\n" + e);
        }
        tv.append("\nEnd of file.");
    }
}