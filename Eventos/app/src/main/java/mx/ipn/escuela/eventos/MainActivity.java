package mx.ipn.escuela.eventos;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements
        OnClickListener {

    EditText jet;
    Button jbn;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        jet = (EditText) findViewById(R.id.xet);
        jbn = (Button) findViewById(R.id.xbn);
        jbn.setOnClickListener(this);
    }

    public void onClick(View v){
        jet.setText("Hola, botón digitalizado!");
    }
}