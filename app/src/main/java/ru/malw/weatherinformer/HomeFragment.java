package ru.malw.weatherinformer;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class HomeFragment extends Fragment {
    private View root;

    public interface WeatherDataCallback {
        void onWeatherDataReceived(JSONObject weatherData);

        void onError();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        refresh(getContext());
        SwipeRefreshLayout srl = root.findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(() -> {
            refresh(getContext());
            srl.setRefreshing(false);
        });
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            refresh(getContext());
            new Handler(Looper.getMainLooper()).postDelayed(() -> refresh(getContext()), 600000);
        }, 600000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) requestNotificationsPermission((MainActivity) getContext());
        return root;
    }

    private void refresh(Context context) {
        getWeatherData(new WeatherDataCallback() {
            @Override
            public void onWeatherDataReceived(JSONObject weather) {
                getActivity().runOnUiThread(() -> {
                    try {
                        String units = Data.UseFahrenheit ? "F" : "C";
                        ((TextView) root.findViewById(R.id.temperature)).setText(Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp"))) + "°" + units);
                        String description = weather.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
                        ((TextView) root.findViewById(R.id.WeatherText)).setText(description.substring(0, 1).toUpperCase() + description.substring(1) + ", ощущается как " + Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like"))) + "°" + units);
                        int icon = getResources().getIdentifier("big" + weather.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2), "drawable", requireActivity().getPackageName());
                        if (!Data.CityFriendlyName.equals(weather.getJSONObject("city").getString("name"))) {
                            Data.CityFriendlyName = weather.getJSONObject("city").getString("name");
                            MainActivity.db.execAndLeave("UPDATE cities SET FriendlyName = \"" + Data.CityFriendlyName + "\" WHERE id = " + Data.CityID);
                        }
                        ((TextView) root.findViewById(R.id.city)).setText(Data.CityFriendlyName);
                        for (int e = 1; e < 40; e++) {
                            TextView t = root.findViewById(getResources().getIdentifier("t" + e, "id", requireActivity().getPackageName()));
                            ImageView i = root.findViewById(getResources().getIdentifier("i" + e, "id", requireActivity().getPackageName()));
                            try {
                                int temperature = (int) Math.round(weather.getJSONArray("list").getJSONObject(e).getJSONObject("main").getDouble("temp"));
                                t.setText(String.valueOf(temperature));
                                i.setImageResource(getResources().getIdentifier("i" + weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2), "drawable", requireActivity().getPackageName()));
                                description = weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("description");
                                TooltipCompat.setTooltipText(i, description.substring(0, 1).toUpperCase() + description.substring(1));
                                i.setContentDescription(description);
                                String currentDescription = ((TextView) root.findViewById(R.id.WeatherText)).getText().toString();
                                System.out.println(currentDescription);
                                Data.description = currentDescription;
                                if (currentDescription.contains("Пасмурно") || description.contains("Облачно с прояснениями")) {

                                    root.setBackgroundResource(R.drawable.cloud_back);
                                } else if (currentDescription.contains("Небольшой снег")) {
                                    root.setBackgroundResource(R.drawable.snow_back);
                                } else if (currentDescription.contains("Небольшой дождь")) {
                                    root.setBackgroundResource(R.drawable.rain_back);
                                } else {
                                    root.setBackgroundResource(R.drawable.sun_back);
                                }
                            } catch (JSONException ex) {
                                t.setText("???");
                                i.setImageResource(R.drawable.unknown);
                                TooltipCompat.setTooltipText(i, "Неизвестно");
                                i.setContentDescription("Неизвестно");
                            }
                        }
                        for (int i = 0; i < 4; i++) {
                            int index = new int[]{8, 16, 24, 32}[i];
                            String date = new SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("ru"))
                                    .format(new Date(Long.parseLong(weather.getJSONArray("list").getJSONObject(index).getString("dt")) * 1000));
                            ((TextView) root.findViewById(getResources().getIdentifier("d" + index, "id", requireActivity().getPackageName()))).setText(date.substring(0, 1).toUpperCase() + date.substring(1) + " (" + Integer.parseInt(((TextView) root.findViewById(getResources().getIdentifier("t" + index, "id", requireActivity().getPackageName()))).getText().toString().replace("°", "")) + "°" + units + ")");
                        }
                        if (Data.tray) {
                            prepareNotification(context, "В городе " + Data.CityFriendlyName + " " + ((TextView) root.findViewById(R.id.temperature)).getText(), ((TextView) root.findViewById(R.id.WeatherText)).getText().toString(), icon);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    catch (IllegalStateException e) {
                        Log.e("IllegalStateException", e.getMessage());
                    }
                });
            }

            @Override
            public void onError() {
                new Handler(Looper.getMainLooper()).post(() ->
                        new AlertDialog.Builder(context)
                                .setTitle("Ошибка отправки запроса!")
                                .setMessage("Попробуйте сменить IP адрес (перезагрузить роутер или использовать VPN). Показать рекомендуемый VPN-сервис?")
                                .setPositiveButton("Да", (dialog, which) -> {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://zelenka.guru/threads/4807721")));
                                    dialog.dismiss();
                                })
                                .setNegativeButton("Нет", null)
                                .show()
                );
            }
        });
    }

    private void getWeatherData(WeatherDataCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.openweathermap.org/data/2.5/forecast?id=" + Data.CityID + "&appid=" + Data.token + "&lang=" + Data.language + "&units=" + (Data.UseFahrenheit ? "imperial" : "metric")).openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
                JSONObject weather = new JSONObject(new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next());
                callback.onWeatherDataReceived(weather);
            } catch (IOException | JSONException e) {
                callback.onError();

            }
        });
    }

    private void prepareNotification(Context context, String title, String message, int icon) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(1, builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void requestNotificationsPermission(MainActivity context) {
        ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 123);
    }
}

