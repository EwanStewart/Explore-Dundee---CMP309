package com.example.cmp309cwk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        //on radio button click
        android.widget.RadioGroup radioGroup = findViewById(R.id.distanceRadioGroup);

        String distanceMetric = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).getString("distanceMetric", "0");
        if (distanceMetric.equals("0")) {
            radioGroup.check(R.id.radioButtonKm);
        } else {
            radioGroup.check(R.id.radioButtonMiles);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonKm:
                    getSharedPreferences("sharedPrefs", MODE_PRIVATE).edit().putString("distanceMetric", "0").apply();
                    break;
                case R.id.radioButtonMiles:
                    getSharedPreferences("sharedPrefs", MODE_PRIVATE).edit().putString("distanceMetric", "1").apply();
                    break;
            }
        });
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}