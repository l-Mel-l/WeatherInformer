package ru.malw.weatherinformer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlaceActivity extends AppCompatActivity {

    private ListView cityListView;
    private ArrayAdapter<String> adapter;
    private List<String> cityNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        getSupportActionBar().hide();

        ImageButton imageButton = findViewById(R.id.backButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        EditText editText = findViewById(R.id.cityEditText);


        cityListView = findViewById(R.id.cityListView);
        cityNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNames);
        cityListView.setAdapter(adapter);



       Button Button = findViewById(R.id.searchButton);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editText.getText().toString();
                new FetchCitiesTask().execute(city);
            }
        });
    }

    private class FetchCitiesTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            String city = params[0];
            JSONArray citiesArray = null;

            try {
                String url = "https://api.openweathermap.org/data/2.5/find?q=" + URLEncoder.encode(city, "UTF-8") + "&appid=" + Data.token + "&lang=ru";
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();

                JSONObject response = new JSONObject(new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next());
                citiesArray = response.getJSONArray("list");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return citiesArray;
        }

        @Override
        protected void onPostExecute(JSONArray citiesArray) {
            cityNames.clear();
            if (citiesArray != null) {
                for (int i = 0; i < citiesArray.length(); i++) {
                    try {
                        JSONObject cityObject = citiesArray.getJSONObject(i);
                        String cityName = cityObject.getString("name");
                        int cityId = cityObject.getInt("id");
                        cityNames.add(cityName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();

                cityListView.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedCity = cityNames.get(position);
                    Data.CityFriendlyName = selectedCity;

                    // сюда вставить добавление в бд

                    Intent intent = new Intent(PlaceActivity.this, MainActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}
