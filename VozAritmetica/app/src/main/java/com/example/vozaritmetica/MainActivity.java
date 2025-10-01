package com.example.vozaritmetica;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView xtvOperacion;
    private Button xbtnVoz, xbtnResultado;
    private TextToSpeech textToSpeech;
    private final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xtvOperacion = findViewById(R.id.xtvOperacion);
        xbtnVoz = findViewById(R.id.xbtnVoz);
        xbtnResultado = findViewById(R.id.xbtnResultado);

        // Inicializar TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        // Configurar el botón de voz
        xbtnVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escucharVoz();
            }
        });

        // Configurar el botón de reproducir resultado
        xbtnResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = xtvOperacion.getText().toString();
                if (!texto.isEmpty()) {
                    textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(MainActivity.this, "No hay resultado para leer", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para iniciar el reconocimiento de voz
    private void escucharVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga una operación aritmética");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            Log.e("ERROR", "No se pudo iniciar el reconocimiento de voz", e);
        }
    }

    // Recibir resultado de la entrada de voz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> resultados = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (resultados != null && !resultados.isEmpty()) {
                String operacion = resultados.get(0);
                double resultado = evaluarOperacion(operacion);
                xtvOperacion.setText(operacion + " = " + resultado);
            }
        }
    }

    // Método para evaluar una operación matemática
    private double evaluarOperacion(String operacion) {
        double resultado = 0;
        try {
            // Quitar espacios en blanco
            operacion = operacion.replaceAll(" ", "");

            if (operacion.contains("+")) {
                String[] partes = operacion.split("\\+");
                resultado = Double.parseDouble(partes[0]) + Double.parseDouble(partes[1]);
            } else if (operacion.contains("-")) {
                String[] partes = operacion.split("-");
                resultado = Double.parseDouble(partes[0]) - Double.parseDouble(partes[1]);
            } else if (operacion.contains("*")) {
                String[] partes = operacion.split("\\*");
                resultado = Double.parseDouble(partes[0]) * Double.parseDouble(partes[1]);
            } else if (operacion.contains("/")) {
                String[] partes = operacion.split("/");
                resultado = Double.parseDouble(partes[0]) / Double.parseDouble(partes[1]);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error en la operación", Toast.LENGTH_SHORT).show();
        }
        return resultado;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(new Locale("es", "ES"));
        } else {
            Toast.makeText(this, "TextToSpeech no está disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
