package ru.malw.weatherinformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class CitiesListFragment extends Fragment {
    private ActivityResultLauncher<Intent> activityLauncher;
    CityAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CityAdapter(getContext(), R.layout.cities_list, MainActivity.db.retrieveCities(), (v) -> {
            Data.CityID = Integer.parseInt(v.getTag().toString());
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

        return view;
    }

}