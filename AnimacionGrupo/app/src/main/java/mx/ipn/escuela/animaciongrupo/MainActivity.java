package mx.ipn.escuela.animaciongrupo;


import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button oscila, gira, esfuma, mueve, contrae, aumenta, detener;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageview);
        oscila = findViewById(R.id.oscila);
        gira = findViewById(R.id.gira);
        esfuma = findViewById(R.id.esfuma);
        mueve = findViewById(R.id.mueve);
        contrae = findViewById(R.id.contrae);
        aumenta = findViewById(R.id.aumenta);
        detener = findViewById(R.id.detener);
        createAnimation(oscila, R.anim.oscila);
        createAnimation(gira, R.anim.gira);
        createAnimation(esfuma, R.anim.esfuma);
        createAnimation(mueve, R.anim.mueve);
        createAnimation(contrae, R.anim.contrae);
        createAnimation(aumenta, R.anim.aumenta);
        detener.setOnClickListener(v -> imageView.clearAnimation());
    }
    private void createAnimation(View view, int animResId) {
        view.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this,
                    animResId);
            imageView.startAnimation(animation); // Inicia la animación del ImageView.
        });
    }
}