package com.example.operacionvoz;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView tvOperation;
    private TextView tvResult;
    private TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOperation = findViewById(R.id.tv_operation);
        tvResult = findViewById(R.id.tv_result);
        Button btnListen = findViewById(R.id.btn_listen);

        textToSpeech = new TextToSpeech(this, this);

        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition();
            }
        });
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga una operación aritmética");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String operation = result.get(0);
                tvOperation.setText(operation);
                calculateAndSpeakResult(operation);
            }
        }
    }

    private void calculateAndSpeakResult(String operation) {
        try {
            double result = eval(operation);
            tvResult.setText(operation + " = " + result);
            String speechText = operation + " es igual a " + result;
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
        } catch (Exception e) {
            tvResult.setText("Error en la operación");
            textToSpeech.speak("No entendí la operación", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private double eval(String expression) throws Exception {
        // Usar exp4j para evaluar la expresión
        Expression exp = new ExpressionBuilder(expression).build();
        return exp.evaluate();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.getDefault());
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
