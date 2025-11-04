package mx.ipn.escuela.fragmentos1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Cargar el primer fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Agregar el fragmento de mensaje
        MessageFragment messageFragment = new MessageFragment();
        fragmentTransaction.add(R.id.fragment_container, messageFragment);

        // Agregar el fragmento de botón
        ButtonFragment buttonFragment = new ButtonFragment();
        fragmentTransaction.add(R.id.fragment_container, buttonFragment);

        // Confirmar la transacción
        fragmentTransaction.commit();
    }
}