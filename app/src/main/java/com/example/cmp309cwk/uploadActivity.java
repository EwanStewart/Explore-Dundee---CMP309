package com.example.cmp309cwk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class uploadActivity extends AppCompatActivity {
    private GoogleMap mMap;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String landmarksVisitedString;
    Bundle bundle;

    public static class User {

        public String totalDistance;
        public String totalTime;
        public String landmarksVisitedString;
        public String status;

        public User(String status, String totalDistance, String totalTime, String landmarksVisitedString) {
            this.status = status;
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
            this.landmarksVisitedString = landmarksVisitedString;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();
        TextView landmarksVisitedTextView = findViewById(R.id.txtViewLandmarksVisited);



        bundle = getIntent().getExtras();

        if(bundle.getString("landmarksVisited") != null){
            String landmarksVisitedString = bundle.getString("landmarksVisited");
            landmarksVisitedString = landmarksVisitedString.substring(1);
            landmarksVisitedTextView.setText("Landmarks Visited: " + landmarksVisitedString);
        } else {
            landmarksVisitedTextView.setText("Landmarks Visited: You didn't visit any landmarks");
        }


        TextView distanceText = findViewById(R.id.txtViewDistance);
        distanceText.setText("Distance Travelled: " + String.format("%.2f",  bundle.getDouble("distance")) + " KM");

        TextView timeText = findViewById(R.id.txtTotalTime);
        timeText.setText("Total time: " +  bundle.getString("time"));

        //set the onclick listener to onClick
        Button button = findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "No username");
                bundle.putString("username", username);

                firebaseDatabase = FirebaseDatabase.getInstance();

                databaseReference = firebaseDatabase.getReference("activities");
                DatabaseReference userRef = databaseReference.child(username);


                HashMap<String, User> user = new HashMap<>();

                landmarksVisitedString = bundle.getString("landmarksVisited");
                landmarksVisitedString = landmarksVisitedString.substring(1);

                RadioButton pub = findViewById(R.id.rbPublic);
                boolean status = pub.isChecked();

                user.put("activity_data", new User(String.valueOf(status), String.valueOf(bundle.getDouble("distance")), bundle.getString("time"), landmarksVisitedString));

                userRef.setValue(user);
            }
        }
        );
    }




}