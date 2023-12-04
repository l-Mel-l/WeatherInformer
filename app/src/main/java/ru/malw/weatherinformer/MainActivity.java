package ru.malw.weatherinformer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ru.malw.weatherinformer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    static DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DataBase(this);
        Data.updateSettings(this);
        if (Data.CityID == 0) {
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Data.change(this, "CityID", data.getIntExtra("id", 0));
                    Data.CityID = data.getIntExtra("id", 0);
                    init();
                }
                else finish();
            }).launch(new Intent(this, AddCity.class));
        }
        else init();
    }
    @SuppressLint("NonConstantResourceId")
    private void init() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        getSupportActionBar().hide();
        binding.bottomNavigationView.getMenu().findItem(R.id.main).setChecked(true);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.main:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.cities:
                    replaceFragment(new CitiesListFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlace, fragment).commit();
    }
}