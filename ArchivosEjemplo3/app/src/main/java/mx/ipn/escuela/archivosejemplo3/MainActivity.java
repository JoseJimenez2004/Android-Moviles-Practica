package mx.ipn.escuela.archivosejemplo3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private Button btnCrearArchivo;
    private TextView tvContenido;
    private static final String HIMNO_IPN = "Politécnico, Politécnico, la gloria\n" +
            "de forjar en la técnica el futuro de la patria.\n" +
            "En sus aulas se enciende la llama\n" +
            "del saber, y en su yunque se templa el acero.\n" +
            "De la ciencia es crisol y es doctrina\n" +
            "de la técnica un yunque y un malacate.\n" +
            "Politécnico, yunque y crisol,\n" +
            "de la patria en la técnica forjas\n" +
            "un destino de luz y de gloria,\n" +
            "un anhelo de excelsa ascensión.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCrearArchivo = findViewById(R.id.btnCrearArchivo);
        tvContenido = findViewById(R.id.tvContenido);

        btnCrearArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearYEncriptarArchivo();
            }
        });
    }

    private void crearYEncriptarArchivo() {
        try {
            // 1. Crear y escribir el himno en el archivo
            File file = new File(getFilesDir(), "himno_ipn.txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(HIMNO_IPN.getBytes());
            fos.close();

            // 2. Leer el contenido del archivo para calcular los hashes
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            // 3. Calcular los hashes
            String sha1Hash = bytesToHex(MessageDigest.getInstance("SHA-1").digest(data));
            String md5Hash = bytesToHex(MessageDigest.getInstance("MD5").digest(data));
            String sha256Hash = bytesToHex(MessageDigest.getInstance("SHA-256").digest(data));

            // 4. Agregar los hashes al final del archivo
            fos = new FileOutputStream(file, true); // true para append
            fos.write("\n\n--- Hashes ---".getBytes());
            fos.write(("\nSHA-1: " + sha1Hash).getBytes());
            fos.write(("\nMD5: " + md5Hash).getBytes());
            fos.write(("\nSHA-256: " + sha256Hash).getBytes());
            fos.close();

            // 5. Leer y mostrar el contenido final
            fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            tvContenido.setText(sb.toString());

            Toast.makeText(this, "Archivo creado y encriptado con éxito.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}