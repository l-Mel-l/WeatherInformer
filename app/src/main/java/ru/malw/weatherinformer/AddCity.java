package ru.malw.weatherinformer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddCity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_place);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.searchButton).setOnClickListener(v -> search(((TextView)findViewById(R.id.cityEditText)).getText().toString(), (ListView)findViewById(R.id.cityListView)));
    }

    private void search(String query, ListView list) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.openweathermap.org/data/2.5/find?q=" + query.trim() + "&appid=" + Data.token).openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
                if (query.equals("")) {
                    tellError("Поиск городов", "Введите нормальный запрос!");
                    return;
                }
                JSONObject response = new JSONObject(new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next());
                if (response.getInt("cod") != 200) {
                    if (response.getString("message").equals("bad query") || response.get("message").equals("Nothing to geocode")) {
                        tellError("Поиск городов", "Введите нормальный запрос!");
                    }
                }
                else if (response.getInt("count") == 0) {
                    tellError("Поиск городов", "Ничего не найдено!");
                }
                else {
                    JSONArray cities = response.getJSONArray("list");
                    runOnUiThread(() -> {
                        // Здесь я мучался три часа...
                        list.setAdapter(new CityAdapter(this, R.layout.cities_list,
                            IntStream.range(0, cities.length())
                                .mapToObj(i -> {
                                    try {
                                        return new JSONObject()
                                            .put("id", cities.getJSONObject(i).getInt("id"))
                                            .put("name", cities.getJSONObject(i).getString("name") + ", " + cities.getJSONObject(i).getJSONObject("sys").getString("country"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                            .collect(Collectors.toList()), (view) -> {
                            MainActivity.db.execAndLeave("INSERT INTO Cities VALUES (" + view.getTag() + ", \"" + ((TextView) view.findViewById(R.id.name)).getText() + "\")");
                            setResult(Activity.RESULT_OK, new Intent().putExtra("id", Integer.parseInt(view.getTag().toString())).putExtra("name", ((TextView) view.findViewById(R.id.name)).getText()));
                            finish();
                        }));
                    });

                }

            } catch (IOException | JSONException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                    new AlertDialog.Builder(this)
                        .setTitle("Ошибка отправки запроса!")
                        .setMessage("Попробуйте сменить IP адрес (перезагрузить роутер или использовать VPN). Показать рекомендуемый VPN-сервис?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://zelenka.guru/threads/4807721")));
                            dialog.dismiss();})
                        .setNegativeButton("Нет", null)
                        .show()
                );
            }
        }).join();

    }

    void tellError(String title, String text) {
        new Handler(Looper.getMainLooper()).post(() ->
            new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Закрыть", null)
                .show()
        );
    }
}
