package mx.ipn.escuela.examen3erparcial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MapActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private String name;
    private double latitude, longitude;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        TextView locationTextView = findViewById(R.id.location_text_view);
        message = "Hello " + name + " this is your latitude: " + latitude + ", and longitude: " + longitude;
        locationTextView.setText(message);

        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "English is not supported", Toast.LENGTH_SHORT).show();
                // Si el TTS no funciona, abre el mapa de todos modos.
                openMap();
            } else {
                // Habla y, cuando termine, abre el mapa.
                speakAndOpenMap();
            }
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
            // Si el TTS falla, abre el mapa de todos modos.
            openMap();
        }
    }

    private void speakAndOpenMap() {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                openMap();
            }

            @Override
            public void onError(String utteranceId) {
                openMap();
            }
        });

        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "MAP_MESSAGE");
    }

    private void openMap() {
        // Crea una URI de geolocalización. El formato es "geo:lat,lon?q=lat,lon(Etiqueta)"
        String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Tu ubicación)";
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        // Asegúrate de que haya una aplicación que pueda manejar este Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
        // Opcional: cierra esta actividad después de lanzar el mapa
        // finish();
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
