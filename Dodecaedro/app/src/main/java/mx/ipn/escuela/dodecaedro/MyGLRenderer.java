package mx.ipn.escuela.dodecaedro;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangle;

    // mMVPMatrix es la abreviatura de "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Establece el color de fondo (negro en este caso)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // Inicializa la figura que vas a dibujar
        mTriangle = new Triangle();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Limpia la pantalla y el buffer de profundidad
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Configura la posición de la cámara (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calcula la proyección y la vista (combina las matrices)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Crea una rotación para el triángulo (opcional)
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Dibuja la figura
        mTriangle.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Ajusta el viewport según los cambios de tamaño de la pantalla
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // Aplica la proyección de perspectiva a la matriz
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    // Método de utilidad para compilar los shaders de OpenGL
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}