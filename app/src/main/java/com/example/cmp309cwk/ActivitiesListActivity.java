package com.example.cmp309cwk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ActivitiesListActivity extends AppCompatActivity {

    private ListView list;
    private ArrayList<String[]> contacts = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStartMaps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActivitiesListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivitiesListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    Intent intent = new Intent(ActivitiesListActivity.this, MapsActivity.class);
                    startActivity(intent);
                }

            }
        } );

        findViewById(R.id.btnProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitiesListActivity.this, ActivitiesListActivity.class);
                startActivity(intent);
            }
        } );


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("activities");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot clientSnapshot: dataSnapshot.getChildren()) {

                    for (DataSnapshot childSnapshot: clientSnapshot.getChildren()) {
                        String childKey = childSnapshot.getKey();
                        //create a new contact
                        String[] contact = new String[3];
                        int counter = 0;

                        for (DataSnapshot childSnapshot2: childSnapshot.getChildren()) {
                            contact[counter] = childSnapshot2.getValue().toString();
                            counter ++;
                        }
                        contacts.add(contact);
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
        ActivitiesAdapter adapter = new ActivitiesAdapter(this, contacts);
        list = findViewById(R.id.list_contacts);
        list.setAdapter(adapter);
    }
}
