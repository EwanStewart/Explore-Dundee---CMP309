package com.example.cmp309cwk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActivitiesListActivity extends AppCompatActivity {

    private ListView list;
    private ArrayList<String[]> activities = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStartMaps).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ActivitiesListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivitiesListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Intent intent = new Intent(ActivitiesListActivity.this, MapsActivity.class);
                startActivity(intent);
            }

        });

        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            Intent intent = new Intent(ActivitiesListActivity.this, ProfileActivitiesListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(ActivitiesListActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("activities");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot clientSnapshot: dataSnapshot.getChildren()) {

                    for (DataSnapshot childSnapshot: clientSnapshot.getChildren()) {
                        String childKey = childSnapshot.getKey();
                        String[] contact = new String[5];
                        int counter = 0;

                        for (DataSnapshot childSnapshot2: childSnapshot.getChildren()) {
                            contact[counter] = childSnapshot2.getValue().toString();
                            counter ++;
                        }
                        activities.add(contact);
                        populateList();
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("TAG", "Failed to read value.", databaseError.toException());
            }
        });



        getSupportActionBar().hide();

    }


    private void populateList(){
        activities.sort(Comparator.comparing(o -> o[0])); //sort object by date time
        Collections.reverse(activities);  //reverse the order

        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities);
        list = findViewById(R.id.list_activities);
        list.setAdapter(adapter);
    }
}