package mx.ipn.escuela.selecciones3;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final CubeRenderer renderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Usar OpenGL ES 2.0
        setEGLContextClientVersion(2);

        renderer = new CubeRenderer(context);
        setRenderer(renderer);

        // Renderiza sólo cuando hay cambio
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;

                renderer.setAngleX(
                        renderer.getAngleX() + dy * TOUCH_SCALE_FACTOR);
                renderer.setAngleY(
                        renderer.getAngleY() + dx * TOUCH_SCALE_FACTOR);
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
