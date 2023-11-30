package ru.malw.weatherinformer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;


import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        refresh(getContext());
        return root;
    }

    private void refresh(Context context) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.openweathermap.org/data/2.5/forecast?id=" + Data.CityID + "&appid=" + Data.token + "&lang=" + Data.language + "&units=" + (Data.UseFahrenheit ? "imperial" : "metric")).openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
                JSONObject weather = new JSONObject(new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next());

                getActivity().runOnUiThread(() -> {
                    try {
                        ((TextView) root.findViewById(R.id.TemperatureText)).setText(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp"));
                        ((TextView) root.findViewById(R.id.WeatherText)).setText(weather.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));
                        ((TextView) root.findViewById(R.id.FeelsLikeText)).setText("Ощущается как " + weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException | JSONException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        // TODO: надо будет потом сделать диалоговое окно об ошибке таким же, как в десктопе. Но щас лень.
                        new AlertDialog.Builder(context)
                            .setTitle("Не удалось получить данные с сервера!")
                            .setMessage("Контакты не были синхронизированы с сервером!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show()
                );
            }
        }, Executors.newSingleThreadExecutor()).thenRun(() -> Executors.newSingleThreadExecutor().shutdown());
    }
}