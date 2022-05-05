package com.example.cmp309cwk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class uploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();



        Bundle bundle = getIntent().getExtras();



        double totalDistance = bundle.getDouble("distance");
        TextView distanceText = findViewById(R.id.txtViewDistance);
        distanceText.setText("Distance Travelled: " + String.format("%.2f", totalDistance) + " KM");

        String totalTime = bundle.getString("time");
        TextView timeText = findViewById(R.id.txtTotalTime);
        timeText.setText("Total time: " +  totalTime);

    }
}