package mx.ipn.escuela.archivosejemplo2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {

    private static final String FILENAME = "misdatos.txt";
    private EditText editTextData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se enlaza el EditText del layout con la variable
        editTextData = findViewById(R.id.editText_data);
    }

    /**
     * Guarda el texto del EditText en un archivo en el almacenamiento interno privado de la app.
     * Este método se llama cuando el usuario toca el botón "Guardar".
     * @param v La vista que fue clickeada (el botón).
     */
    public void guardar(View v) {
        String textToSave = editTextData.getText().toString();

        // Se utiliza un bloque try-with-resources para asegurar que el FileOutputStream y
        // OutputStreamWriter se cierren automáticamente al finalizar, incluso si ocurren errores.
        try (FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {

            // Escribe el contenido del string en el archivo.
            osw.write(textToSave);

            Toast.makeText(this, "Archivo guardado en " + getFilesDir() + "/" + FILENAME, Toast.LENGTH_LONG).show();
            // Limpia el EditText después de guardar.
            editTextData.setText("");

        } catch (IOException e) {
            e.printStackTrace(); // Imprime el error en Logcat para depuración.
            Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lee el contenido del archivo desde el almacenamiento interno y lo muestra en el EditText.
     * Este método se llama cuando el usuario toca el botón "Abrir".
     * @param v La vista que fue clickeada (el botón).
     */
    public void abrir(View v) {
        // Se utiliza un bloque try-with-resources para que los streams se cierren automáticamente.
        try (FileInputStream fis = openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            // Lee el archivo línea por línea hasta el final.
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // Muestra el contenido leído en el EditText.
            editTextData.setText(stringBuilder.toString());
            Toast.makeText(this, "Archivo abierto.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace(); // Imprime el error en Logcat para depuración.
            Toast.makeText(this, "Error al abrir el archivo. ¿Quizás no ha sido guardado aún?", Toast.LENGTH_LONG).show();
        }
    }
}
