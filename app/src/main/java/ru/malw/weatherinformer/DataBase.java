package ru.malw.weatherinformer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    final SQLiteDatabase db;
    public DataBase(Context context) {
        db = context.openOrCreateDatabase("Malw_weather_informer.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Cities (id INTEGER UNIQUE PRIMARY KEY, FriendlyName VARCHAR)");
    }

    public void execAndLeave(String sql) {
        db.execSQL(sql);
    }

    public List<JSONObject> retrieveCities() {
        List<JSONObject> citiesList = new ArrayList<>();

        try (Cursor cursor = db.rawQuery("SELECT id, FriendlyName FROM Cities", null)) {
            while (cursor.moveToNext()) {
                JSONObject cityObject = new JSONObject()
                        .put("id", cursor.getInt(cursor.getColumnIndex("id")))
                        .put("name", cursor.getString(cursor.getColumnIndex("FriendlyName")));
                citiesList.add(cityObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return citiesList;
    }

}
