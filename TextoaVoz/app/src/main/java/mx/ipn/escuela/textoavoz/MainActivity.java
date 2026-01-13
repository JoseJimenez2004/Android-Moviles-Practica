package mx.ipn.escuela.textoavoz;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private EditText etTexto;
    private Button btnIngles, btnEspanol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTexto = findViewById(R.id.etTexto);
        btnIngles = findViewById(R.id.btnIngles);
        btnEspanol = findViewById(R.id.btnEspanol);

        tts = new TextToSpeech(this, this);

        btnIngles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hablar(Locale.US);
            }
        });

        btnEspanol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hablar(new Locale("es", "ES"));
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS está listo
        } else {
            Toast.makeText(this, "Error al inicializar TTS", Toast.LENGTH_SHORT).show();
        }
    }

    private void hablar(Locale locale) {
        String texto = etTexto.getText().toString();
        if (texto.isEmpty()) {
            Toast.makeText(this, "Escribe algo para hablar", Toast.LENGTH_SHORT).show();
            return;
        }
        tts.setLanguage(locale);
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
