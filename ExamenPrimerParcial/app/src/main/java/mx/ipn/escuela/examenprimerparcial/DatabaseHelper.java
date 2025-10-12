package mx.ipn.escuela.examenprimerparcial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "usuarios.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COL_ID = "id";
    private static final String COL_CORREO = "correo";
    private static final String COL_PATRON = "patron";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CORREO + " TEXT UNIQUE, " +
                COL_PATRON + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public boolean insertarUsuario(String correo, String patron) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CORREO, correo);
        values.put(COL_PATRON, patron);
        long result = db.insert(TABLE_USUARIOS, null, values);
        return result != -1;
    }

    public String consultarPatron(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_PATRON + " FROM " + TABLE_USUARIOS + " WHERE " + COL_CORREO + "=?", new String[]{correo});
        if (cursor.moveToFirst()) {
            String patron = cursor.getString(0);
            cursor.close();
            return patron;
        } else {
            cursor.close();
            return null;
        }
    }
}
