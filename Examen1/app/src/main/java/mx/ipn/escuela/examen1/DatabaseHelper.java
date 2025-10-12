package mx.ipn.escuela.examen1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "users.db";
    public static final int DB_VER = 1;

    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "_id";
    public static final String COL_EMAIL = "email";
    public static final String COL_PATTERN = "pattern";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PATTERN + " TEXT NOT NULL" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // migraciones si son necesarias
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long insertUser(String email, String pattern) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EMAIL, email);
        cv.put(COL_PATTERN, pattern);
        try {
            return db.insertOrThrow(TABLE_USERS, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Cursor getAllUsers(){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_USERS, null, null, null, null, null, null);
    }

    public String getPatternForEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{COL_PATTERN}, COL_EMAIL + "=?", new String[]{email}, null, null, null);
        if (c != null && c.moveToFirst()) {
            String p = c.getString(c.getColumnIndexOrThrow(COL_PATTERN));
            c.close();
            return p;
        }
        if (c != null) c.close();
        return null;
    }
}
