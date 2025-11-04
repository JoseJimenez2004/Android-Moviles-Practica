package mx.ipn.escuela.cubo3dimagenes;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Clase NUEVA que extiende GLSurfaceView para manejar el input táctil (tocar y arrastrar).
 */
    public class MiGLSurfaceView extends GLSurfaceView {

    private final Renderizador renderizador;

    // Variables para el control de arrastre
    private float previousX;
    private float previousY;

    // Variables para detectar un "toque" (tap)
    private long tapStartTime;
    private float tapStartX;
    private float tapStartY;
    private static final int MAX_TAP_DURATION = 200; // milisegundos
    private static final float MAX_TAP_MOVE_DISTANCE = 10; // pixeles

    public MiGLSurfaceView(Context context, Renderizador renderer) {
        super(context);
        this.renderizador = renderer;
        setRenderer(renderer);

        // CAMBIO IMPORTANTE:
        // Solo dibujamos un frame cuando es necesario (al arrastrar o tocar).
        // Esto detiene la animación automática y ahorra batería.
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Iniciar seguimiento de toque y arrastre
                tapStartTime = System.currentTimeMillis();
                tapStartX = x;
                tapStartY = y;
                previousX = x;
                previousY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;
                float distance = (float) Math.sqrt((x - tapStartX) * (x - tapStartX) + (y - tapStartY) * (y - tapStartY));

                // Si el movimiento es significativo, es un ARRASTRE
                if (distance > MAX_TAP_MOVE_DISTANCE) {
                    // Marcar como que ya no es un "toque" válido
                    tapStartTime = 0;

                    // Actualizar ángulos en el renderizador
                    // Invertimos 'dy' para que la rotación sea intuitiva
                    renderizador.anguloX += (dy * 0.4f); // Factor de sensibilidad
                    renderizador.anguloY += (dx * 0.4f);

                    // Pedir que se redibuje el frame
                    requestRender();
                }
                previousX = x;
                previousY = y;
                break;

            case MotionEvent.ACTION_UP:
                long duration = System.currentTimeMillis() - tapStartTime;
                // Si el tiempo es corto y no se movió mucho, es un "TOQUE"
                if (tapStartTime > 0 && duration < MAX_TAP_DURATION) {
                    handleTap(x, y);
                }
                break;
        }
        return true;
    }

    private void handleTap(float x, float y) {

        if (getContext() instanceof MainActivity) {
            // Llamamos al método en MainActivity
            ((MainActivity) getContext()).onFaceTapped(0);
        }
    }
}
