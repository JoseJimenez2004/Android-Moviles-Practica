package mx.ipn.escuela.mapas1;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        // Obtiene el fragmento del mapa y notifica cuando esté listo
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agregar un marcador en ESCOM - IPN
        LatLng escom = new LatLng(19.504011, -99.151653);
        mMap.addMarker(new MarkerOptions().position(escom).title("Marcador en ESCOM - IPN"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(escom, 17));
    }
}