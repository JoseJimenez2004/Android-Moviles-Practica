package mx.ipn.escuela.proyecto2_joystick;

import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private JoystickView joystickView;

    // --- Constantes para un control más preciso ---
    // La "zona muerta" ignora movimientos leves del joystick que no son intencionados
    private static final float JOYSTICK_DEAD_ZONE = 0.15f;
    // Velocidad de movimiento constante
    private static final float MOVEMENT_SPEED = 15.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joystickView = findViewById(R.id.joystickView);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        // Asegurarse de que el evento proviene de un joystick y es un movimiento
        if ((ev.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
                ev.getAction() == MotionEvent.ACTION_MOVE) {

            // Obtener los valores de los ejes del joystick
            float axisX = ev.getAxisValue(MotionEvent.AXIS_X);
            float axisY = ev.getAxisValue(MotionEvent.AXIS_Y);

            // Aplicar la "zona muerta" para evitar movimientos accidentales
            float finalDx = 0;
            if (Math.abs(axisX) > JOYSTICK_DEAD_ZONE) {
                finalDx = axisX * MOVEMENT_SPEED;
            }

            float finalDy = 0;
            if (Math.abs(axisY) > JOYSTICK_DEAD_ZONE) {
                finalDy = axisY * MOVEMENT_SPEED;
            }

            // Solo actualizar si hay un movimiento real
            if (finalDx != 0 || finalDy != 0) {
                joystickView.updatePosition(finalDx, finalDy);
            }

            return true; // Indicamos que hemos manejado el evento
        }
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Asegurarse de que el evento proviene de un gamepad y es una pulsación (no un levantamiento)
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD &&
                event.getAction() == KeyEvent.ACTION_DOWN) {
            
            // Comprobar si se ha presionado el botón del stick (L3 o R3)
            if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_THUMBL || event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_THUMBR) {
                // Llamar a nuestro nuevo método para centrar el círculo
                joystickView.centerCircle();
                return true; // Evento manejado
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
