package mx.ipn.escuela.cubo3dimagenes;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import android.content.*;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
class Renderizador implements GLSurfaceView.Renderer {
    private MiCubo mc;

    // CAMBIO 1: Eliminar 'angulo' y 'velocidad'
    // Estas variables controlarán la rotación desde la clase MiGLSurfaceView
    public volatile float anguloX = 0;
    public volatile float anguloY = 0;

    public Renderizador(Context cx) {
        mc = new MiCubo(cx);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglc) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
        mc.loadTexture(gl);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if (h == 0) h = 1;
        float spct = (float) w/h;
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, spct, 0.1f, 100.f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -6.0f);

        // CAMBIO 2: Aplicar la rotación manual controlada por el input táctil
        gl.glRotatef(anguloX, 1.0f, 0.0f, 0.0f); // Rotar en eje X
        gl.glRotatef(anguloY, 0.0f, 1.0f, 0.0f); // Rotar en eje Y

        mc.draw(gl);

        // CAMBIO 3: Eliminar la rotación automática
        // angulo += velocidad;
    }
}
