package ru.malw.weatherinformer;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private static final int REQUEST_CODE = 1;
    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        requestLocationPermission();
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
                        ((TextView) root.findViewById(R.id.TemperatureText)).setText(String.valueOf(Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp")))));
                        String description = weather.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
                        ((TextView) root.findViewById(R.id.WeatherText)).setText(description.substring(0, 1).toUpperCase() + description.substring(1));
                        ((TextView) root.findViewById(R.id.FeelsLikeText)).setText("Ощущается как " + Math.round(Double.parseDouble(weather.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like"))));
                        String units = Data.UseFahrenheit ? "F" : "C";
                        for (int e = 1; e <= 40; e++) {
                            TextView t = root.findViewById(getResources().getIdentifier("t" + e, "id", requireActivity().getPackageName()));
                            ImageView i = root.findViewById(getResources().getIdentifier("i" + e, "id", requireActivity().getPackageName()));
                            try {
                                int temperature = (int) Math.round(weather.getJSONArray("list").getJSONObject(e).getJSONObject("main").getDouble("temp"));
                                t.setText(String.valueOf(temperature));
                                i.setImageResource(getResources().getIdentifier("i"+weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2), "drawable", requireActivity().getPackageName()));
                                description = weather.getJSONArray("list").getJSONObject(e).getJSONArray("weather").getJSONObject(0).getString("description");
                                TooltipCompat.setTooltipText(i, description.substring(0, 1).toUpperCase() + description.substring(1));
                                i.setContentDescription(description);
                                String currentDescription = ((TextView) root.findViewById(R.id.WeatherText)).getText().toString();
                                System.out.println(currentDescription);
                                if (currentDescription.contains("Пасмурно") || description.contains("Облачно с прояснениями")) {

                                    root.setBackgroundResource(R.drawable.cloud_back);
                                } else if (currentDescription.contains("Небольшой снег")) {
                                    root.setBackgroundResource(R.drawable.snow_back);
                                } else if (currentDescription.contains("Небольшой дождь")) {
                                    root.setBackgroundResource(R.drawable.rain_back);
                                } else {
                                    root.setBackgroundResource(R.drawable.sun_back);
                                }
                                TextView textView1 = (TextView) root.findViewById(R.id.d1);
                                TextView textView2 = (TextView) root.findViewById(R.id.t1);
                                textView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (textView2.getVisibility() == View.INVISIBLE) {
                                            for (int x = 1; x <= 8; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        } else {
                                            for (int x = 1; x <= 8; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        }
                                    }
                                });
                                TextView textView3 = (TextView) root.findViewById(R.id.d9);
                                TextView textView4 = (TextView) root.findViewById(R.id.t9);
                                textView3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (textView4.getVisibility() == View.INVISIBLE) {
                                            for (int i = 9; i <= 16; i++) {
                                                int resId = getResources().getIdentifier("t" + i, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + i, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + i, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        } else {
                                            for (int i = 9; i <= 16; i++) {
                                                int resId = getResources().getIdentifier("t" + i, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + i, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + i, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        }
                                    }
                                });
                                TextView textView5 = (TextView) root.findViewById(R.id.d17);
                                TextView textView6 = (TextView) root.findViewById(R.id.t18);
                                textView5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (textView6.getVisibility() == View.INVISIBLE) {
                                            for (int x = 17; x <= 24; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        } else {
                                            for (int x = 17; x <= 24; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        }
                                    }
                                });
                                TextView textView7 = (TextView) root.findViewById(R.id.d25);
                                TextView textView8 = (TextView) root.findViewById(R.id.t26);
                                textView7.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (textView8.getVisibility() == View.INVISIBLE) {
                                            for (int x = 25; x <= 32; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        } else {
                                            for (int x = 25; x <= 32; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        }
                                    }
                                });
                                TextView textView9 = (TextView) root.findViewById(R.id.d33);
                                TextView textView10 = (TextView) root.findViewById(R.id.t34);
                                textView9.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (textView10.getVisibility() == View.INVISIBLE) {
                                            for (int x = 33; x <= 40; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.VISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        } else {
                                            for (int x = 33; x <= 40; x++) {
                                                int resId = getResources().getIdentifier("t" + x, "id", requireActivity().getPackageName());
                                                TextView textView = (TextView) root.findViewById(resId);
                                                textView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId2 = getResources().getIdentifier("i" + x, "id", requireActivity().getPackageName());
                                                ImageView imageView = (ImageView) root.findViewById(resId2);
                                                imageView.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая

                                                int resId3 = getResources().getIdentifier("textView" + x, "id", requireActivity().getPackageName());
                                                TextView Date = (TextView) root.findViewById(resId3);
                                                Date.setVisibility(View.INVISIBLE); // или View.GONE, в зависимости от вашего случая
                                            }
                                        }
                                    }
                                });


                            } catch (JSONException ex) {
                                t.setText("???");
                                i.setImageResource(R.drawable.unknown);
                                TooltipCompat.setTooltipText(i, "Неизвестно");
                                i.setContentDescription("Неизвестно");
                            }
                        }
                        int[] temperatures = new int[]{Integer.parseInt(((TextView) root.findViewById(R.id.t4)).getText().toString()), Integer.parseInt(((TextView) root.findViewById(R.id.t12)).getText().toString()), Integer.parseInt(((TextView) root.findViewById(R.id.t20)).getText().toString()), Integer.parseInt(((TextView) root.findViewById(R.id.t28)).getText().toString()), Integer.parseInt(((TextView) root.findViewById(R.id.t36)).getText().toString())};
                        for (int i = 0; i < 5; i++) {
                            int d = new int[]{1, 9, 17, 25, 33}[i];
                            String date = new SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("ru")).format(new Date(Long.parseLong(weather.getJSONArray("list").getJSONObject(d).getString("dt")) * 1000));
                            String temperature = String.valueOf(temperatures[i]);
                            ((TextView) root.findViewById(getResources().getIdentifier("d" + d, "id", requireActivity().getPackageName()))).setText(date.substring(0, 1).toUpperCase() + date.substring(1) + "                                                             " + temperature + "°" + units);
                        }
                        scheduleWeatherNotification();
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
    private void getLocationUpdates() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Получение новой доступной локации пользователя
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    // Обновление погодных данных
                    refresh(getContext());

                    // Остановка обновления локации после получения первой позиции
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            // Проверка разрешения на использование геолокации
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Запуск обновления локации
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    latitude = lastKnownLocation.getLatitude();
                    longitude = lastKnownLocation.getLongitude();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                // Разрешение не предоставлено, обработайте эту ситуацию соответствующим образом
            }
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрос разрешения у пользователя
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            // Разрешение уже предоставлено
            // Получение локации пользователя
            getLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, получение локации пользователя
                getLocationUpdates();
            } else {
                // Разрешение не получено, обработайте эту ситуацию соответствующим образом
            }
        }
    }
    private void scheduleWeatherNotification() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), WeatherNotificationReceiver.class);

        // Добавь следующие строки для передачи данных о погоде в Intent
        int currentTemperature = Integer.parseInt(((TextView) root.findViewById(R.id.TemperatureText)).getText().toString());
        String currentDescription = ((TextView) root.findViewById(R.id.WeatherText)).getText().toString();
        String currentCity = ((TextView) root.findViewById(R.id.CityText)).getText().toString();
        intent.putExtra("currentTemperature", currentTemperature);
        intent.putExtra("currentDescription", currentDescription);
        intent.putExtra("currentCity", currentCity);

        // Также добавь следующие строки для получения данных о погоде через 3 часа
        int futureTemperature = Integer.parseInt    (((TextView) root.findViewById(R.id.t1)).getText().toString().replace("°C", "").replace("°F", ""));
        String futureDescription = root.findViewById(R.id.i1).getContentDescription().toString();
        intent.putExtra("futureTemperature", futureTemperature);
        intent.putExtra("futureDescription", futureDescription);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Устанавливаем повторяющееся событие каждые три часа
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3 * 60 * 60 * 1000, pendingIntent);
    }
}
