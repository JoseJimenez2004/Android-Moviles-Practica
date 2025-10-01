package mx.ipn.escuela.listasejercicio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Persona> personas = getPersonas();
        PersonaAdapter adapter = new PersonaAdapter(this, R.layout.persona_list_item, personas);

        ListView listView = findViewById(R.id.persona_list_view);
        listView.setAdapter(adapter);
    }

    private List<Persona> getPersonas() {
        List<Persona> personas = new ArrayList<>();
        personas.add(new Persona(1, "Ana", "Gomez", "55-1234-5678", "ana.gomez@email.com"));
        personas.add(new Persona(2, "Luis", "Ramirez", "55-9876-5432", "luis.ramirez@email.com"));
        personas.add(new Persona(3, "Carlos", "Perez", "55-1111-2222", "carlos.perez@email.com"));
        personas.add(new Persona(4, "Maria", "Lopez", "55-3333-4444", "maria.lopez@email.com"));
        personas.add(new Persona(5, "Sofia", "Hernandez", "55-5555-6666", "sofia.hernandez@email.com"));
        return personas;
    }
}
