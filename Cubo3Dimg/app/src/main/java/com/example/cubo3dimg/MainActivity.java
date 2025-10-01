package com.example.cubo3dimg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);

        glSurfaceView.setRenderer(new CubeRenderer(this, (face) -> {
            // Asigna un URL a cada cara
            String url = "";
            switch (face) {
                case 0: url = "https://www.google.com"; break;
                case 1: url = "https://www.facebook.com"; break;
                case 2: url = "https://www.twitter.com"; break;
                case 3: url = "https://www.instagram.com"; break;
                case 4: url = "https://www.linkedin.com"; break;
                case 5: url = "https://www.github.com"; break;
            }

            if (!url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        }));

        setContentView(glSurfaceView);
    }
}