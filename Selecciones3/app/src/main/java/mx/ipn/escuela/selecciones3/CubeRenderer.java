package mx.ipn.escuela.selecciones3;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class CubeRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];

    private int mProgram;

    private float angleX = 30f;
    private float angleY = 45f;

    static final int COORDS_PER_VERTEX = 3;
    static float cubeCoords[] = {
            // Frente
            -1, 1, 1,   -1, -1, 1,   1, -1, 1,   1, 1, 1,
            // Atrás
            -1, 1, -1,  -1, -1, -1,  1, -1, -1,  1, 1, -1,
    };

    float colors[] = {
            // Frente (azul)
            0.1f, 0.5f, 1f, 1f,
            0.1f, 0.5f, 1f, 1f,
            0.1f, 0.5f, 1f, 1f,
            0.1f, 0.5f, 1f, 1f,

            // Atrás (rojo)
            1f, 0.3f, 0.3f, 1f,
            1f, 0.3f, 0.3f, 1f,
            1f, 0.3f, 0.3f, 1f,
            1f, 0.3f, 0.3f, 1f,
    };

    public CubeRenderer(Context context) {
        this.context = context;

        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
        GLES20.glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
        mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUseProgram(mProgram);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.setRotateM(modelMatrix, 0, angleX, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, angleY, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        colorBuffer.position(0);
        GLES20.glVertexAttribPointer(colorHandle, 4,
                GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 4, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
    }

    public float getAngleX() { return angleX; }
    public float getAngleY() { return angleY; }
    public void setAngleX(float value) { angleX = value; }
    public void setAngleY(float value) { angleY = value; }

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 fragColor;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  fragColor = vColor;" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec4 fragColor;" +
                    "void main() {" +
                    "  gl_FragColor = fragColor;" +
                    "}";

    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private static int createProgram(String vertex, String fragment) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertex);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }
}
