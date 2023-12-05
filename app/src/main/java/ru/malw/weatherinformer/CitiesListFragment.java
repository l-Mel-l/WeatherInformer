package ru.malw.weatherinformer;

import static ru.malw.weatherinformer.Data.description;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
public class CitiesListFragment extends Fragment {
    private ActivityResultLauncher<Intent> activityLauncher;
    CityAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CityAdapter(getContext(), R.layout.cities_list, MainActivity.db.retrieveCities(), (v) -> {
            Data.CityID = Integer.parseInt(v.getTag().toString());
            Data.change(getContext(), "CityID", Integer.parseInt(v.getTag().toString()));
            ((MainActivity) getActivity()).toMainTab();
        });
        activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    try {
                        adapter.add(new JSONObject()
                            .put("id", data.getIntExtra("id", 0))
                            .put("name", data.getStringExtra("name")));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);
        ((ListView)view.findViewById(R.id.cities)).setAdapter(adapter);
        view.findViewById(R.id.locationEditText).setOnClickListener(v -> {
            activityLauncher.launch(new Intent(getActivity(), AddCity.class));
        });
        String currentDescription = description;
        if (currentDescription.contains("Пасмурно") || currentDescription.contains("Облачно с прояснениями")) {

            view.setBackgroundResource(R.drawable.cloud_back);
        } else if (currentDescription.contains("Небольшой снег")) {
            view.setBackgroundResource(R.drawable.snow_back);
        } else if (currentDescription.contains("Небольшой дождь")) {
            view.setBackgroundResource(R.drawable.rain_back);
        } else {
            view.setBackgroundResource(R.drawable.sun_back);
        }

        return view;
    }

}