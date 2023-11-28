package com.example.weatherinformer;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {
    private static final String API_KEY = "2a605935cb485e63d8657f2f7c2774e9";
    private static final String CITY_NAME = "CITY_NAME";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/forecast?id=" + 511565 + "&appid=" + Weather.Data.token + "&lang=" + "ru" + "&units=" + (Weather.Data.UseFahrenheit ? "imperial" : "metric");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        new GetWeather().execute();
        return view;
    }

    private class GetWeather extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();
            String jsonStr = httpHandler.makeServiceCall(API_URL);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Получение объекта для информации о погоде


                    // Здесь можно использовать данные о погоде по своему усмотрению
                    TextView temperatureTextView = getActivity().findViewById(R.id.TemperatureText);
                    TextView descriptionTextView = getActivity().findViewById(R.id.WeatherText);
                    TextView feelsLikeTextView = getActivity().findViewById(R.id.FeelsLikeText);
                    getActivity().runOnUiThread(() -> {
                        try {
                            temperatureTextView.setText(jsonObj.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp"));
                            descriptionTextView.setText(jsonObj.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));
                            feelsLikeTextView.setText("Ощущается как " +jsonObj.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}