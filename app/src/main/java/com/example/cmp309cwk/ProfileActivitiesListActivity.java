package com.example.cmp309cwk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class ProfileActivitiesListActivity extends AppCompatActivity {

    private ListView list;
    private ArrayList<String[]> activities = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //onclick of home button go to main activity
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivitiesListActivity.this, SettingsActivity.class);
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

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", UUID.randomUUID().toString());


        for (int i = 0; i < activities.size(); i++) {
            if (!activities.get(i)[4].equals(userID)) {
                activities.remove(i);
            }
        }
        Collections.reverse(activities);  //reverse the order

        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities);
        list = findViewById(R.id.list_activities);
        list.setAdapter(adapter);
    }
}
