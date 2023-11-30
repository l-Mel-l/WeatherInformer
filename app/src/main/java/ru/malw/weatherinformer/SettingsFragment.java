package ru.malw.weatherinformer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;


import java.util.Locale;

public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // ... инициализация элементов интерфейса ...

        RadioGroup languageRadioGroup = view.findViewById(R.id.languageRadioGroup);
        RadioButton englishRadioButton = view.findViewById(R.id.englishRadioButton);
        RadioButton russianRadioButton = view.findViewById(R.id.russianRadioButton);
        RadioGroup themeRadioGroup = view.findViewById(R.id.ThemeRadioGroup);
        RadioButton lightThemeRadioButton = view.findViewById(R.id.LightThemeRadioBtn);
        RadioButton darkThemeRadioButton = view.findViewById(R.id.DarkthemeRadioBtn);
        RadioButton newYearThemeRadioButton = view.findViewById(R.id.NewYearthemeRadioBtn);
        //Темы
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.LightThemeRadioBtn) {
                setTheme(AppCompatDelegate.MODE_NIGHT_NO); // Светлая тема
            } else if (checkedId == R.id.DarkthemeRadioBtn) {
                setTheme(AppCompatDelegate.MODE_NIGHT_YES); // Тёмная тема
            } else if (checkedId == R.id.NewYearthemeRadioBtn) {
                // Здесь вы можете установить свою новогоднюю тему
                // Например, через стили или другие настройки
            }
        });

        // Обработка события выбора языка
        languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.englishRadioButton) {
                setLocale("en"); // Установите код языка для английского
            } else if (checkedId == R.id.russianRadioButton) {
                setLocale("ru"); // Установите код языка для русского
            }
        });

        return view;
    }
    //Метод для изменения языка
    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Перезапустите активити для применения изменений языка
        getActivity().recreate();
    }
    //Метод для изменения темы
    private void setTheme(int mode) {
        // Установите режим темы для приложения
        AppCompatDelegate.setDefaultNightMode(mode);

        // Перезапустите активити для применения изменений темы
        getActivity().recreate();
    }
}