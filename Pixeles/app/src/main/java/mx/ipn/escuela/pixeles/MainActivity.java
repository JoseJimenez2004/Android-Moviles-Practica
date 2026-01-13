package mx.ipn.escuela.pixeles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import mx.ipn.escuela.pixeles.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Bitmap mutableBitmap;
    private int currentX;
    private int currentY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeBitmap();
        setupTouchListener();
        setupButtonClickListener();
    }

    private void initializeBitmap() {
        // TODO: Reemplaza "@android:drawable/sym_def_app_icon" con tu propia imagen.
        // Añade tu imagen a la carpeta res/drawable para poder usarla.
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.sym_def_app_icon);
        mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        binding.imageView.setImageBitmap(mutableBitmap);
    }

    private void setupTouchListener() {
        binding.imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleImageTouch(event);
                return true;
            }
            return false;
        });
    }

    private void handleImageTouch(MotionEvent event) {
        int imageX = (int) (event.getX() * (float) mutableBitmap.getWidth() / binding.imageView.getWidth());
        int imageY = (int) (event.getY() * (float) mutableBitmap.getHeight() / binding.imageView.getHeight());

        if (isCoordinatesInsideBitmap(imageX, imageY)) {
            currentX = imageX;
            currentY = imageY;
            int pixelColor = mutableBitmap.getPixel(imageX, imageY);
            binding.colorView.setBackgroundColor(pixelColor);

            drawRedDot(imageX, imageY);

            binding.x1.setText(String.valueOf(imageX));
            binding.y1.setText(String.valueOf(imageY));

            binding.xtv.setText("Punto 1 en (" + imageX + ", " + imageY + "). Toca 'Guardar Punto 2' para guardar como punto 2.");
        } else {
            binding.xtv.setText("Has tocado fuera de la imagen.");
        }
    }

    private void drawRedDot(int x, int y) {
        if (isCoordinatesInsideBitmap(x, y)) {
            mutableBitmap.setPixel(x, y, Color.RED);
            binding.imageView.setImageBitmap(mutableBitmap);
        }
    }

    private void setupButtonClickListener() {
        binding.btnopcion.setOnClickListener(v -> swapPixels());

        binding.btnSaveP2.setOnClickListener(v -> {
            binding.x2.setText(String.valueOf(currentX));
            binding.y2.setText(String.valueOf(currentY));
            binding.xtv.setText("Punto 2 guardado en (" + currentX + ", " + currentY + ")");
        });
    }

    private void swapPixels() {
        try {
            int x1 = Integer.parseInt(binding.x1.getText().toString());
            int y1 = Integer.parseInt(binding.y1.getText().toString());
            int x2 = Integer.parseInt(binding.x2.getText().toString());
            int y2 = Integer.parseInt(binding.y2.getText().toString());

            if (isCoordinatesInsideBitmap(x1, y1) && isCoordinatesInsideBitmap(x2, y2)) {
                int color1 = mutableBitmap.getPixel(x1, y1);
                int color2 = mutableBitmap.getPixel(x2, y2);

                mutableBitmap.setPixel(x1, y1, color2);
                mutableBitmap.setPixel(x2, y2, color1);

                binding.imageView.invalidate(); // Más eficiente que setImageBitmap
                Toast.makeText(this, "Píxeles intercambiados", Toast.LENGTH_SHORT).show();
                binding.xtv.setText("Píxeles intercambiados en (" + x1 + "," + y1 + ") y (" + x2 + "," + y2 + ")");
            } else {
                Toast.makeText(this, "Error: Las coordenadas están fuera de la imagen.", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: Ingresa coordenadas válidas.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCoordinatesInsideBitmap(int x, int y) {
        return x >= 0 && x < mutableBitmap.getWidth() && y >= 0 && y < mutableBitmap.getHeight();
    }
}
