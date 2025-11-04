package mx.ipn.escuela.fragmentos1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class MessageFragment extends Fragment {
    private TextView textView;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        textView = view.findViewById(R.id.textView);
        return view;
    }
    public void updateMessage (String message) {
        textView.setText (message);
    }
}