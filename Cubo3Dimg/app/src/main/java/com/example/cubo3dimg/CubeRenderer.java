package com.example.cubo3dimg;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class CubeRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final Cube cube;
    private final Cube.CubeTouchListener touchListener;

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private float angleX = 0f;
    private float angleY = 0f;

    public CubeRenderer(Context context, Cube.CubeTouchListener touchListener) {
        this.context = context;
        this.touchListener = touchListener;
        this.cube = new Cube(context, touchListener);
    }

    @Override
    public void onSurfaceCreated(javax.microedition.khronos.egl.EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        cube.init();
    }

    @Override
    public void onSurfaceChanged(javax.microedition.khronos.egl.EGLConfig config, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleX, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, angleY, 0, 1, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);

        cube.draw(mvpMatrix);
    }

    public void rotate(float dx, float dy) {
        angleX += dy;
        angleY += dx;
    }
}