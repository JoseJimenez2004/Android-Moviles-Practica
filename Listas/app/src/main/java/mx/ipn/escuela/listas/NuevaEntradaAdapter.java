package mx.ipn.escuela.listas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

public final class NuevaEntradaAdapter extends ArrayAdapter<NuevaEntrada> {

    private final int entradaLayoutRecurso;

    public NuevaEntradaAdapter(Context c, int layoutRes) {
        super(c, 0);
        this.entradaLayoutRecurso = layoutRes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View workingView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(entradaLayoutRecurso, parent, false)
                : convertView;

        ViewHolder vh;
        Object tag = workingView.getTag();
        if (tag == null || !(tag instanceof ViewHolder)) {
            vh = new ViewHolder();
            vh.tituloView = workingView.findViewById(R.id.xtvtitulo);
            vh.subTituloView = workingView.findViewById(R.id.xtvsubtitulo);
            vh.imagenView = workingView.findViewById(R.id.xivicono);
            workingView.setTag(vh);
        } else {
            vh = (ViewHolder) tag;
        }

        NuevaEntrada ne = getItem(position);
        if (ne != null) {
            vh.tituloView.setText(ne.getTitulo());
            String s = String.format(
                    "Por %s • %s",
                    ne.getAutor(),
                    DateFormat.getDateInstance(DateFormat.SHORT).format(ne.getFecha())
            );
            vh.subTituloView.setText(s);
            vh.imagenView.setImageResource(ne.getIcono());
        }

        return workingView;
    }

    private static class ViewHolder {
        TextView tituloView;
        TextView subTituloView;
        ImageView imagenView;
    }
}