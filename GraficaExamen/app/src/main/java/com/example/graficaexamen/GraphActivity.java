package com.example.graficaexamen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Vincular la vista personalizada
        graphView = findViewById(R.id.graphView);

        // Obtener datos del Intent
        float amplitude = getIntent().getFloatExtra("AMPLITUDE", 0); // Amplitud = 100
        int time = getIntent().getIntExtra("TIME", 0); // Tiempo = 16

        // Generar puntos
        List<float[]> points = new ArrayList<>();

        for (int t = 0; t <= time; t++) {
            float x = t; // Tiempo
            float y;

            // Dividir el tiempo en tramos
            if (t <= 2) {
                y = 50 + (50 / 2f) * t; // De 50 a 100 (Tramo 1)
            } else if (t <= 4) {
                y = 100 - (20 / 2f) * (t - 2); // De 100 a 80 (Tramo 2)
            } else if (t <= 6) {
                y = 80 - (30 / 2f) * (t - 4); // De 80 a 50 (Tramo 3)
            } else if (t <= 8) {
                y = 50 + (50 / 2f) * (t - 6); // De 50 a 100 (Tramo 4)
            } else if (t <= 10) {
                y = 100; // Mantenerse en 100 (Tramo 5)
            } else {
                y = 100 - (100 / 6f) * (t - 10); // De 100 a 0 (Tramo 6)
            }

            points.add(new float[]{x, y});
        }

        // Pasar los puntos a la vista personalizada
        graphView.setPoints(points, time, amplitude);
    }
}
