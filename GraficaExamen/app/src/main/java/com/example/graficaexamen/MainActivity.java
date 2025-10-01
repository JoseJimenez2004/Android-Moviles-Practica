package com.example.graficaexamen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputAmplitude, inputTime;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincular elementos del layout
        inputAmplitude = findViewById(R.id.inputAmplitude);
        inputTime = findViewById(R.id.inputTime);
        btnNext = findViewById(R.id.btnGraph);

        // Botón para ir a la siguiente actividad
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amplitudeStr = inputAmplitude.getText().toString();
                String timeStr = inputTime.getText().toString();

                if (!amplitudeStr.isEmpty() && !timeStr.isEmpty()) {
                    // Convertir valores
                    float amplitude = Float.parseFloat(amplitudeStr);
                    int time = Integer.parseInt(timeStr);

                    // Crear Intent para enviar datos
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    intent.putExtra("AMPLITUDE", amplitude);
                    intent.putExtra("TIME", time);

                    // Iniciar la segunda actividad
                    startActivity(intent);
                }
            }
        });
    }
}
