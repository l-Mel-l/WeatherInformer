package ru.malw.weatherinformer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ru.malw.weatherinformer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    static DataBase db;
    static ActivityMainBinding binding;

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
                    Data.CityFriendlyName = db.getCityName(Data.CityID);
                    init();
                }
                else finish();
            }).launch(new Intent(this, AddCity.class));
        }
        else init();
    }
    @SuppressLint("NonConstantResourceId")
    private void init() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        toMainTab();
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

    @SuppressLint("NonConstantResourceId")
    public void expand(View view) {
        int gridId = 0;
        switch (view.getId()) {
            case R.id.d0:
                gridId = R.id.grid0;
                break;
            case R.id.d8:
                gridId = R.id.grid8;
                break;
            case R.id.d16:
                gridId = R.id.grid16;
                break;
            case R.id.d24:
                gridId = R.id.grid24;
                break;
            case R.id.d32:
                gridId = R.id.grid32;
                break;
        }
        findViewById(gridId).setVisibility(findViewById(gridId).getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void toMainTab() {
        binding.bottomNavigationView.getMenu().findItem(R.id.main).setChecked(true);
        replaceFragment(new HomeFragment());
    }
}