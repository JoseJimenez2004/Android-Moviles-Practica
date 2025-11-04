package mx.ipn.escuela.cubo3dimagenes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
// Ya no se usa android.opengl.GLSurfaceView directamente

public class MainActivity extends Activity {

    // CAMBIO 1: Usar nuestra clase personalizada MiGLSurfaceView
    private MiGLSurfaceView glsv;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        // CAMBIO 2: Inicializar el Renderizador y pasarlo a MiGLSurfaceView
        Renderizador render = new Renderizador(this);
        glsv = new MiGLSurfaceView(this, render); // 'this' es el Context

        // Establecer nuestra vista personalizada como el content
        this.setContentView(glsv);
    }

    // CAMBIO 3: Este método es llamado por MiGLSurfaceView cuando detecta un "toque"
    public void onFaceTapped(final int faceIndex) {
        // Las operaciones de UI (como mostrar un Diálogo) DEBEN
        // ejecutarse en el Hilo Principal (UI Thread)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSpinnerDialog(faceIndex);
            }
        });
    }

    // CAMBIO 4: Lógica para crear y mostrar el Spinner en un Diálogo
    private void showSpinnerDialog(int faceIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Usamos faceIndex para saber qué cara fue (aunque ahora siempre es 0 como demo)
        builder.setTitle("Cara " + (faceIndex + 1) + " Seleccionada");

        // Crear un Spinner programáticamente
        final Spinner spinner = new Spinner(this);

        // Estos datos vendrían de tu base de datos (como en DAMN Spinner2)
        String[] items = new String[]{"Dato DB 1", "Dato DB 2", "Dato DB 3", "Item de ESCOM", "Item de IPN"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        // Añadir el spinner al diálogo
        builder.setView(spinner);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = spinner.getSelectedItem().toString();
                Toast.makeText(MainActivity.this, "Accediendo a: " + selected, Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glsv.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        glsv.onResume();
    }
}
