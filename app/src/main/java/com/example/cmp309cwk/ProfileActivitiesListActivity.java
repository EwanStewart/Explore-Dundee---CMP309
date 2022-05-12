package com.example.cmp309cwk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public class ProfileActivitiesListActivity extends AppCompatActivity implements View.OnTouchListener{

    private final ArrayList<String[]> activities = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.btnHome).setOnTouchListener(this);
        findViewById(R.id.btnSettings).setOnTouchListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("activities");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot clientSnapshot: Objects.requireNonNull(dataSnapshot).getChildren()) {

                    for (DataSnapshot childSnapshot: clientSnapshot.getChildren()) {
                        String[] contact = new String[5];
                        int counter = 0;

                        for (DataSnapshot childSnapshot2: childSnapshot.getChildren()) {
                            contact[counter] = Objects.requireNonNull(childSnapshot2.getValue()).toString();
                            counter ++;
                        }
                        activities.add(contact);
                        populateList();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("TAG", "Failed to read value.", databaseError.toException());
            }
        });



        getSupportActionBar().hide();

    }

    public boolean onTouch(View v, MotionEvent event) {
        Intent intent;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            findViewById(v.getId()).setBackgroundColor(Color.parseColor("#F0F0F0"));


            switch (v.getId()) {
                case R.id.btnHome:
                    intent = new Intent(v.getContext(), MainActivity.class);
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

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", UUID.randomUUID().toString());


        for (int i = 0; i < activities.size(); i++) {
            if (!activities.get(i)[4].equals(userID)) {
                activities.remove(i);
            }
        }
        Collections.reverse(activities);  //reverse the order

        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities);
        ListView list = findViewById(R.id.list_activities);
        list.setAdapter(adapter);
    }
}
