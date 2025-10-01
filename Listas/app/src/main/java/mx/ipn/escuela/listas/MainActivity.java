package mx.ipn.escuela.listas;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        ListView lv = findViewById(R.id.xlv1);
        NuevaEntradaAdapter adapter = new NuevaEntradaAdapter(this, R.layout.nueva_entrada_lista);
        lv.setAdapter(adapter);

        for (NuevaEntrada ne : getEntradas()) {
            adapter.add(ne);
        }
    }

    private List<NuevaEntrada> getEntradas() {
        List<NuevaEntrada> datos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            datos.add(new NuevaEntrada(
                    "Entrada " + i,
                    "Autor " + i,
                    new GregorianCalendar(2025, 8, Math.min(i, 28)).getTime(), // mes 8 = septiembre (0-based)
                    (i % 2 == 0) ? R.drawable.icon_1 : R.drawable.icon_2
            ));
        }
        return datos;
    }
}