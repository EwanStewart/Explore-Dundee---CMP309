package com.example.cmp309cwk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class uploadActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String landmarksVisitedString;
    Bundle bundle;
    public static class User { //Class to store user data

        public String totalDistance;
        public String totalTime;
        public String landmarksVisitedString;
        public String username;
        public String dateTime;

        public User(String dateTime, String username, String totalDistance, String totalTime, String landmarksVisitedString) {
            this.username = username;
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
            this.landmarksVisitedString = landmarksVisitedString;
            this.dateTime = dateTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();
        TextView landmarksVisitedTextView = findViewById(R.id.txtViewLandmarksVisited);

        bundle = getIntent().getExtras();

        if(bundle.containsKey("landmarksVisited")){ //If landmarksVisited is not null
            String landmarksVisitedString = bundle.getString("landmarksVisited");
            if(landmarksVisitedString.charAt(0) == ','){
                landmarksVisitedString = landmarksVisitedString.substring(1);
            }
            landmarksVisitedTextView.setText("Landmarks Visited: " + landmarksVisitedString); //Set text to landmarksVisited

        } else { //If landmarksVisited is null
            landmarksVisitedString = "No landmarks visited";
            landmarksVisitedTextView.setText("Landmarks Visited: No landmarks visited"); //Set text to landmarksVisited
        }

        this.getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE).edit().putString("landmarksVisited", "").apply(); //Clear landmarksVisited

        String distanceMetric = getApplication().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).getString("distanceMetric", "0");

        TextView distanceText = findViewById(R.id.txtViewDistance);

        if (distanceMetric.equals("0")) {
            double km = bundle.getDouble("distance");
            distanceText.setText("Total Distanced Travelled: "  + String.format("%.2f", km) + " km");
        } else {
            double miles = bundle.getDouble("distance")  * 0.621371;
            miles = Math.round(miles * 100.0) / 100.0;

            distanceText.setText("Total Distanced Travelled: " + String.valueOf(miles) + " miles");
        }


        TextView timeText = findViewById(R.id.txtTotalTime);
        timeText.setText("Total time: " +  bundle.getString("time"));

        Button button = findViewById(R.id.btnSave);
        button.setOnClickListener(v -> { //When upload is clicked

            SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("userID", UUID.randomUUID().toString());

            firebaseDatabase = FirebaseDatabase.getInstance();

            databaseReference = firebaseDatabase.getReference("activities");
            DatabaseReference ref = databaseReference.child(UUID.randomUUID().toString());  //Create a new child in the database

            HashMap<String, User> user = new HashMap<>();
            String dateTime = LocalDateTime.now().toString();

            TextView landmarksVisitedTextView1 = findViewById(R.id.txtViewLandmarksVisited);
            user.put("activity_data", new User(dateTime, username, String.valueOf(bundle.getDouble("distance")), bundle.getString("time"), landmarksVisitedTextView1.getText().toString()));

            ref.setValue(user);
            Intent intent = new Intent(uploadActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        Button discardButton = findViewById(R.id.btnDiscard);
        discardButton.setOnClickListener(v -> {
            Intent intent = new Intent(uploadActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}