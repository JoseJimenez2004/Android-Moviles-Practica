package mx.ipn.escuela.examen_segundo_parcial.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "triangulos.db";
    public static final String TABLE_TRIANGULOS = "t_triangulos";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TRIANGULOS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "color_rojo INTEGER," +
                "color_verde INTEGER," +
                "color_azul INTEGER," +
                "iteraciones INTEGER," +
                "tipo_triangulo TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_TRIANGULOS);
        onCreate(db);
    }
}