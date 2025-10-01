package mx.ipn.escuela.listasejercicio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PersonaAdapter extends ArrayAdapter<Persona> {

    private final int resourceLayout;

    public PersonaAdapter(Context context, int resource, List<Persona> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resourceLayout, parent, false);
        }

        Persona persona = getItem(position);

        if (persona != null) {
            TextView nombre = view.findViewById(R.id.nombre);
            TextView telefono = view.findViewById(R.id.telefono);

            nombre.setText(persona.getNombre() + " " + persona.getApellido());
            telefono.setText(persona.getTelefono());
        }

        return view;
    }
}
