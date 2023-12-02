package ru.malw.weatherinformer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                        ((TextView) root.findViewById(R.id.TemperatureText)).setText(String.valueOf(Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp")))));
                        String description = weather.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
                        ((TextView) root.findViewById(R.id.WeatherText)).setText(description.substring(0, 1).toUpperCase() + description.substring(1));
                        ((TextView) root.findViewById(R.id.FeelsLikeText)).setText("Ощущается как " + Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like"))));
                        String units = Data.UseFahrenheit ? "F" : "C";
                        for (int e = 1; e <= 40; e++) {
                            TextView t = root.findViewById(getResources().getIdentifier("t" + e, "id", requireActivity().getPackageName()));
                            ImageView i = root.findViewById(getResources().getIdentifier("i" + e, "id", requireActivity().getPackageName()));
                            try {
                                t.setText(Math.round(weather.getJSONArray("list").getJSONObject(e).getJSONObject("main").getDouble("temp")) + "°" + units);
                                i.setImageResource(getResources().getIdentifier("i"+weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2), "drawable", requireActivity().getPackageName()));
                                description = weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("description");
                                TooltipCompat.setTooltipText(i, description.substring(0, 1).toUpperCase() + description.substring(1));
                            } catch (JSONException ex) {
                                t.setText("???");
                                i.setImageResource(R.drawable.unknown);
                                TooltipCompat.setTooltipText(i, "Неизвестно");
                            }
                        }
                        for (int d : new int[]{ 9, 17, 25, 33}) {
                            String date = new SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("ru")).format(new Date(Long.parseLong(weather.getJSONArray("list").getJSONObject(d).getString("dt")) * 1000));
                            ((TextView) root.findViewById(getResources().getIdentifier("d" + d, "id", requireActivity().getPackageName()))).setText(date.substring(0, 1).toUpperCase() + date.substring(1));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException | JSONException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        new AlertDialog.Builder(context)
                                .setTitle("Ошибка отправки запроса!")
                                .setMessage("Попробуйте сменить IP адрес (перезагрузить роутер или использовать VPN). Показать рекомендуемый VPN-сервис?")
                                .setPositiveButton("Да", (dialog, which) -> {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://zelenka.guru/threads/4807721")));
                                    dialog.dismiss();})
                                .setNegativeButton("Нет", null)
                                .show()
                );
            }
        }, Executors.newSingleThreadExecutor()).thenRun(() -> Executors.newSingleThreadExecutor().shutdown());
    }
}