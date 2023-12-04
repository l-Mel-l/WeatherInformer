package ru.malw.weatherinformer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Data {
    public static String token = "2a605935cb485e63d8657f2f7c2774e9";
    public static boolean UseFahrenheit;
    public static int CityID;
    public static String CityFriendlyName;
    public static String language;

    static void updateSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        UseFahrenheit = preferences.getBoolean("UseFahrenheit", false);
        CityID = preferences.getInt("CityID", 0);
        CityFriendlyName = preferences.getString("CityFriendlyName", "Обновление информации...");
        language = preferences.getString("language", "ru");
    }
    static void change(Activity activity, String name, int value) {
        SharedPreferences.Editor preferences = activity.getPreferences(Context.MODE_PRIVATE).edit();
        preferences.putInt(name, value);
        preferences.apply();
    }

    static void change(Activity activity, String name, String value) {
        SharedPreferences.Editor preferences = activity.getPreferences(Context.MODE_PRIVATE).edit();
        preferences.putString(name, value);
        preferences.apply();
    }
}

