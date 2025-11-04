package mx.ipn.escuela.fragmentos2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MiFragmento extends Fragment {
    public final static int OK = 0;
    public final static int CANCEL = 1;
    private FragmentoListener fl;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        if (a instanceof FragmentoListener) {
            fl = (FragmentoListener) a;
        }
    }

    public interface FragmentoListener {
        public void digitado(int resultado, String texto);
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.activity_mifragmento, vg, false);

        ((Button) v.findViewById(R.id.xbnA)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonDigitado(v);
                Toast.makeText(getActivity(), "Desde MiFragmento", Toast.LENGTH_LONG).show();
            }
        });

        ((Button) v.findViewById(R.id.xbnC)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonDigitado(v);
            }
        });
        return v;
    }

    public void botonDigitado(View v) {
        if (null == fl)
            return;
        if (((Button) v).getText().equals("Aceptar"))
            fl.digitado(OK, ((EditText) getActivity().findViewById(R.id.xet)).getText().toString());
        else
            fl.digitado(CANCEL, "");
    }
}