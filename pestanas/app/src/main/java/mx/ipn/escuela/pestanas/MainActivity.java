package mx.ipn.escuela.pestanas;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    Resources r;
    TabHost th;
    TabSpec ts;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        r = getResources();
        th = findViewById(android.R.id.tabhost);
        th.setup();

        // Se corrigen las llamadas a getDrawable
        ts = th.newTabSpec("mitab1");
        ts.setContent(R.id.xtab1);
        ts.setIndicator("TAB1", ContextCompat.getDrawable(this, android.R.drawable.ic_btn_speak_now));
        th.addTab(ts);

        ts = th.newTabSpec("mitab2");
        ts.setContent(R.id.xtab2);
        ts.setIndicator("TAB2", ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        th.addTab(ts);

        th.setCurrentTab(0);
        th.setOnTabChangedListener(tabId ->
                Toast.makeText(getApplicationContext(), "Pestaña: " + tabId, Toast.LENGTH_SHORT).show());

        Button xbtn = findViewById(R.id.xbtn);
        EditText xnum = findViewById(R.id.xnum);
        TextView xout = findViewById(R.id.xout);

        xbtn.setOnClickListener(v -> {
            String s = xnum.getText().toString().trim();
            if (TextUtils.isEmpty(s)) { xout.setText("Ingresa un número."); return; }
            if (!s.matches("\\d+")) { xout.setText("Solo números naturales."); return; }

            // Verificación del número de dígitos para la constante de Kaprekar
            if(s.length() != 3 && s.length() != 4){
                xout.setText("Solo se aceptan números de 3 o 4 dígitos para el cálculo de Kaprekar.");
                return;
            }

            long n = Long.parseLong(s);
            boolean esKaprekar = isKaprekar(n);

            StringBuilder sb = new StringBuilder();
            sb.append("Número: ").append(n).append("\n");

            int d = s.length();
            sb.append("Iteraciones (d=").append(d).append("):\n");
            sb.append(kaprekarIteraciones(s, d));

            xout.setText(sb.toString());
        });
    }

    private boolean isKaprekar(long n) {
        if (n < 1) return false;
        long sq = n * n;
        String s = String.valueOf(sq);
        for (int i = 1; i <= s.length(); i++) {
            long left = (i == s.length()) ? 0 : Long.parseLong(s.substring(0, s.length() - i));
            long right = Long.parseLong(s.substring(s.length() - i));
            if (right > 0 && left + right == n) return true;
        }
        return n == 1;
    }

    private String kaprekarIteraciones(String s, int d) {
        String cur = pad(s, d);
        if (!variosDigitos(cur)) return cur + " → 0 (dígitos iguales)\nFin.\n";
        Set<String> vistos = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (int step = 0; step < 30; step++) {
            String asc = sortAsc(cur);
            String desc = sortDesc(cur);
            long va = Long.parseLong(asc);
            long vd = Long.parseLong(desc);
            long r = vd - va;
            String rs = pad(String.valueOf(r), d);
            sb.append(cur).append(": ").append(desc).append(" - ").append(asc).append(" = ").append(rs).append("\n");

            if (esConstanteConocida(rs, d)) {
                sb.append("Constante de Kaprekar alcanzada: ").append(rs).append("\nFin.\n");
                break;
            }
            if (vistos.contains(rs) || rs.equals(cur)) {
                sb.append("Ciclo detectado.\nFin.\n");
                break;
            }

            vistos.add(cur);
            cur = rs;
        }
        return sb.toString();
    }

    private boolean esConstanteConocida(String s, int d) {
        if (d == 4) return s.equals("6174");
        if (d == 3) return s.equals("495");
        return false;
    }

    private boolean variosDigitos(String s) {
        return s.chars().distinct().count() > 1;
    }

    private String sortAsc(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    private String sortDesc(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            char t = a[i]; a[i] = a[j]; a[j] = t;
        }
        return new String(a);
    }

    private String pad(String s, int d) {
        while (s.length() < d) s = "0" + s;
        return s;
    }
}