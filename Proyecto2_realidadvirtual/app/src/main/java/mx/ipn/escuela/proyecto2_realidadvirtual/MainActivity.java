package mx.ipn.escuela.proyecto2_realidadvirtual;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoIzquierdo = findViewById(R.id.videoIzquierdo);
        VideoView videoDerecho = findViewById(R.id.videoDerecho);

        // Ruta del video en la carpeta res/raw.
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        Uri uri = Uri.parse(videoPath);

        // Configurar y reproducir el video para la vista izquierda
        videoIzquierdo.setVideoURI(uri);
        videoIzquierdo.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoIzquierdo.start();
        });

        // Configurar y reproducir el video para la vista derecha
        videoDerecho.setVideoURI(uri);
        videoDerecho.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoDerecho.start();
        });
    }
}
