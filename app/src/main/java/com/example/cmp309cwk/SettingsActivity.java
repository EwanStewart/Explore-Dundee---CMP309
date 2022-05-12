package com.example.cmp309cwk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnTouchListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
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

        findViewById(R.id.btnHome).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Intent intent;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            findViewById(v.getId()).setBackgroundColor(Color.parseColor("#F0F0F0"));
            if (v.getId() == R.id.btnHome) {
                intent = new Intent(v.getContext(), MainActivity.class);
            } else {
                throw new IllegalStateException("Unexpected value: " + v.getId());
            }
            startActivity(intent);
            finish();

            return true;

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            findViewById(v.getId()).setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

        return false;
    }
}