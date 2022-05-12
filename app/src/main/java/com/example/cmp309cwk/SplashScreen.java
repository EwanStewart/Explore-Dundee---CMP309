package com.example.cmp309cwk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class SplashScreen extends AppCompatActivity {
    int counter = 0;

    public void openMainActivity() {    //open main activity after 2 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }

    public void popupMessage(){ //show dialog explaining why location permission is needed
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("In order to use the full functionality of the app, please allow location access.");
        alertDialogBuilder.setTitle("Location Access");
        alertDialogBuilder.setNegativeButton("Proceed", (dialogInterface, i) -> {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        if (getSharedPreferences("sharedPrefs", MODE_PRIVATE).getString("userID", null) == null) {  //check if userID has been set, if not generate a unique ID.
            getSharedPreferences("sharedPrefs", MODE_PRIVATE).edit().putString("userID", UUID.randomUUID().toString()).apply();
            getSharedPreferences("sharedPrefs", MODE_PRIVATE).edit().putString("distanceMetric", "0").apply();

        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {   //check for location permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);  //ask for permission if not granted
        } else {
            openMainActivity(); //open main activity if permission granted
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { //upon user granting/denying permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //if permission granted
                    openMainActivity(); //open main activity
                } else {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        popupMessage(); //if permission denied, show popup message
                    } else {
                        openMainActivity();
                    }
                    return;
                }
            } break;
            case 2: {
                openMainActivity();
            } break;
        }
    }


}