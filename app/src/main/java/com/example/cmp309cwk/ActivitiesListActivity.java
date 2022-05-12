package com.example.cmp309cwk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class ActivitiesListActivity extends AppCompatActivity implements View.OnTouchListener {

    private final ArrayList<String[]> activities = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        findViewById(R.id.btnStartMaps).setOnTouchListener(this);
        findViewById(R.id.btnProfile).setOnTouchListener(this);
        findViewById(R.id.btnSettings).setOnTouchListener(this);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("activities");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot clientSnapshot: dataSnapshot.getChildren()) { //iterate through all the stored activities

                    for (DataSnapshot childSnapshot: clientSnapshot.getChildren()) {
                        String[] activity = new String[5];
                        int counter = 0;

                        for (DataSnapshot childSnapshot2: childSnapshot.getChildren()) {
                            activity[counter] = childSnapshot2.getValue().toString();
                            counter ++;
                        }
                        activities.add(activity);
                        populateList();
                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("TAG", "Failed to read value.", databaseError.toException());
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Intent intent;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            findViewById(v.getId()).setBackgroundColor(Color.parseColor("#F0F0F0"));

            switch (v.getId()) {
                case R.id.btnStartMaps:
                    if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        intent = new Intent(v.getContext(), MapsActivity.class);
                        break;
                    }
                    return true;
                case R.id.btnProfile:
                     intent = new Intent(v.getContext(), ProfileActivitiesListActivity.class);
                     break;
                case R.id.btnSettings:
                     intent = new Intent(v.getContext(), SettingsActivity.class);
                     break;
                default:
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


    private void populateList(){
        activities.sort(Comparator.comparing(o -> o[0])); //sort object by date time
        Collections.reverse(activities);  //reverse the order

        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities);
        ListView list = findViewById(R.id.list_activities);
        list.setAdapter(adapter);

    }

    public void popupMessage(){ //show dialog explaining why location permission is needed
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("To access the location functionality, please allow location access.");
        alertDialogBuilder.setTitle("Location Access");
        alertDialogBuilder.setNegativeButton("Proceed", (dialogInterface, i) -> {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { //upon user granting/denying permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //if permission granted
                    Intent intent = new Intent(this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        popupMessage(); //if permission denied, show popup message
                    } else {
                        Intent intent = new Intent(this, MapsActivity.class);
                        startActivity(intent);
                    }
                    return;
                }
            } break;
            case 2: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //if permission granted
                    Intent intent = new Intent(this, MapsActivity.class);
                    startActivity(intent);
                }
            } break;
        }
    }
}
