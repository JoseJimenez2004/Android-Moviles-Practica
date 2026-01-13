package mx.ipn.escuela.examen_segundo_parcial.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import mx.ipn.escuela.examen_segundo_parcial.entidades.Triangulo;

public class DbTriangulos extends DbHelper {

    Context context;

    public DbTriangulos(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarTriangulo(int colorRojo, int colorVerde, int colorAzul, int iteraciones, String tipoTriangulo) {
        long id = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("color_rojo", colorRojo);
            values.put("color_verde", colorVerde);
            values.put("color_azul", colorAzul);
            values.put("iteraciones", iteraciones);
            values.put("tipo_triangulo", tipoTriangulo);

            id = db.insert(TABLE_TRIANGULOS, null, values);
        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    public ArrayList<Triangulo> mostrarTriangulos() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Triangulo> listaTriangulos = new ArrayList<>();
        Triangulo triangulo;
        Cursor cursorTriangulos;

        cursorTriangulos = db.rawQuery("SELECT * FROM " + TABLE_TRIANGULOS, null);

        if (cursorTriangulos.moveToFirst()) {
            do {
                triangulo = new Triangulo();
                triangulo.setId(cursorTriangulos.getInt(0));
                triangulo.setColor_rojo(cursorTriangulos.getInt(1));
                triangulo.setColor_verde(cursorTriangulos.getInt(2));
                triangulo.setColor_azul(cursorTriangulos.getInt(3));
                triangulo.setIteraciones(cursorTriangulos.getInt(4));
                triangulo.setTipo_triangulo(cursorTriangulos.getString(5));
                listaTriangulos.add(triangulo);
            } while (cursorTriangulos.moveToNext());
        }

        cursorTriangulos.close();

        return listaTriangulos;
    }
}