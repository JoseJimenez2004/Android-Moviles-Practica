package mx.ipn.escuela.dodecaedro;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView gLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gLView = findViewById(R.id.glSurfaceView);

        // Configura la versión de OpenGL ES. 2.0 es compatible con la mayoría de dispositivos.
        gLView.setEGLContextClientVersion(2);

        // Asigna tu Renderer personalizado para dibujar en el GLSurfaceView.
        gLView.setRenderer(new MyGLRenderer());

        // Opcional: Renderiza solo cuando los datos cambian.
        // gLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gLView.onResume();
    }
}