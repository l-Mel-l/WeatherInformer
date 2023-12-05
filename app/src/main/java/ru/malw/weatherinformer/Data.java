package ru.malw.weatherinformer;

import android.content.Context;
import android.content.SharedPreferences;
public class Data {
    public static String token = "2a605935cb485e63d8657f2f7c2774e9";
    public static boolean UseFahrenheit;
    public static int CityID;
    public static String CityFriendlyName;
    public static String language;
    public static boolean tray;
    public static String description;

    static void updateSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        UseFahrenheit = preferences.getBoolean("UseFahrenheit", false);
        CityID = preferences.getInt("CityID", 0);
        CityFriendlyName = preferences.getString("CityFriendlyName", "Обновление информации...");
        language = context.getResources().getConfiguration().locale.getLanguage();
        tray = preferences.getBoolean("tray", false);
    }

    static void change(Context context, String name, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.apply();
    }

    static void change(Context context, String name, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString(name, value);
        editor.apply();
    }

    static void change(Context context, String name, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

}

