package ru.malw.weatherinformer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db.db";
    private static final int DB_VERSION = 3;


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Cities (id INTEGER UNIQUE PRIMARY KEY, FriendlyName VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Settings (UseFahrenheit BOOLEAN, Language VARCHAR(2), CurrentCity INTEGER, NotificationsEnabled BOOLEAN, FOREIGN KEY (CurrentCity) REFERENCES Cities(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void addCity(String friendlyName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FriendlyName", friendlyName);
        db.insert("Cities", null, values);
    }
}
