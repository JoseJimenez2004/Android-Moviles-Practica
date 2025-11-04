package mx.ipn.escuela.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void iniciarServicio(View v){
        Intent i= new Intent(this, MiServicio.class);
        startService(i);
    }

    public void detenerServicio(View v){
        Intent i= new Intent(this, MiServicio.class);
        stopService(i);
    }
}