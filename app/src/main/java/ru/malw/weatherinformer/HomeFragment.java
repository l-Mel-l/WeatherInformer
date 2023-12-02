package ru.malw.weatherinformer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherinformer.R;

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
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                        for (int e = 1; e <= 40; e++) {
                            int textViewId = getResources().getIdentifier("t" + e, "id", requireActivity().getPackageName());
                            int imageViewId = getResources().getIdentifier("i" + e, "id", requireActivity().getPackageName());

                            TextView t = root.findViewById(textViewId);
                            ImageView i = root.findViewById(imageViewId);

                            try {
                                double temperature = weather.getJSONArray("list").getJSONObject(e).getJSONObject("main").getDouble("temp");
                                int roundedTemperature = (int) Math.round(temperature);
                                t.setText(roundedTemperature + "°");
                                String icon = "i"+weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2);
                                int iconResourceId = getResources().getIdentifier(icon, "drawable", requireActivity().getPackageName());
                                i.setImageResource(iconResourceId);
                                i.setContentDescription(weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("description"));
                                TooltipCompat.setTooltipText(i, weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("description"));
                            } catch (Exception ex) {
                                t.setText("???");
                                ex.printStackTrace();
                                i.setImageResource(R.drawable.unknown);
                                //i.setContentDescription(getString(R.string.unknown));
                            }
                        }
                        ((TextView) root.findViewById(R.id.TemperatureText)).setText(String.valueOf(Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp")))));
                        ((TextView) root.findViewById(R.id.WeatherText)).setText(weather.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));
                        ((TextView) root.findViewById(R.id.FeelsLikeText)).setText("Ощущается как " + Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like"))));
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