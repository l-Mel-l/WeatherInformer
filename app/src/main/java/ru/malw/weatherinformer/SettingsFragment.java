package ru.malw.weatherinformer;

import static ru.malw.weatherinformer.Data.description;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SwitchMaterial switchNotifications = view.findViewById(R.id.switchNotifications);
        ((RadioButton) view.findViewById(Data.UseFahrenheit ? R.id.radioButtonFahrenheit : R.id.radioButtonCelsius)).setChecked(true);
        switchNotifications.setChecked(Data.tray);
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

        view.findViewById(R.id.save).setOnClickListener(v -> {
                Data.UseFahrenheit = ((RadioButton)view.findViewById(R.id.radioButtonFahrenheit)).isChecked();
                Data.tray = switchNotifications.isChecked();
                Data.change(requireContext(), "UseFahrenheit", Data.UseFahrenheit);
                Data.change(requireContext(), "tray", Data.tray);
        });
        return view;
    }
}