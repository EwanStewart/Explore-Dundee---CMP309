package com.example.cmp309cwk;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class uploadActivity extends AppCompatActivity {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();
        TextView landmarksVisitedTextView = findViewById(R.id.txtViewLandmarksVisited);



        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("landmarksVisited") != null){
            String landmarksVisitedString = bundle.getString("landmarksVisited");
            landmarksVisitedString = landmarksVisitedString.substring(1);
            landmarksVisitedTextView.setText("Landmarks Visited: " + landmarksVisitedString);
        } else {
            landmarksVisitedTextView.setText("Landmarks Visited: You didn't visit any landmarks");
        }



        double totalDistance = bundle.getDouble("distance");
        TextView distanceText = findViewById(R.id.txtViewDistance);
        distanceText.setText("Distance Travelled: " + String.format("%.2f", totalDistance) + " KM");

        String totalTime = bundle.getString("time");
        TextView timeText = findViewById(R.id.txtTotalTime);
        timeText.setText("Total time: " +  totalTime);

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.46341960060735, -2.973959916879307), 15));


    }
}