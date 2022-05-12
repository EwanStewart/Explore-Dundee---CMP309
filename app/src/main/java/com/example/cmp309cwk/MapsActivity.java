package com.example.cmp309cwk;


import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.cmp309cwk.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentPositionMarker;

    private final ArrayList<LatLng> LatLongPoints = new ArrayList<>();


    private PendingIntent geofencePendingIntent;
    private final ArrayList<Geofence> geofenceList = new ArrayList<>();


    public double totalDistance = 0;
    public Chronometer timeElapsed;

    GeofencingClient geofencingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.cmp309cwk.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timeElapsed = findViewById(R.id.chronometer);

        //credit: https://stackoverflow.com/questions/51054247/chronometer-showing-just-minutes-and-hours
        //--------------------------------------------------------------------------------------------------

        timeElapsed.setOnChronometerTickListener(cArg -> {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            int h = (int) (time / 3600000);
            int m = (int) (time - h * 3600000) / 60000;
            int s = (int) (time - h * 3600000 - m * 60000) / 1000;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            cArg.setText(hh + ":" + mm + ":" + ss);
        });

        timeElapsed.setBase(SystemClock.elapsedRealtime());
        timeElapsed.start();
        //---------------------------------------------------------------------------------------------------


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //check for fine location permission


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            Log.i("location", location.toString());
                        }
                    });
        }


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        binding.btnEnd.setOnClickListener(v -> {
                    Intent intent = new Intent(this, uploadActivity.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE);
                    if (!sharedPreferences.getString("landmarksVisited", "").isEmpty()) {
                        intent.putExtra("landmarksVisited", sharedPreferences.getString("landmarksVisited", ""));
                    }

                    intent.putExtra("time", timeElapsed.getText());
                    intent.putExtra("distance", totalDistance);

                    startActivity(intent);
                    finish();
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeElapsed.stop();
        geofencingClient.removeGeofences(geofencePendingIntent);
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                currentPositionMarker.setPosition(currentLocation);

                if (!LatLongPoints.isEmpty()) {
                    LatLng last = LatLongPoints.get(LatLongPoints.size() - 1);
                    float[] results = new float[1];
                    Location.distanceBetween(last.latitude, last.longitude, currentLocation.latitude, currentLocation.longitude, results);
                    float distance = results[0] / 1000;
                    totalDistance += distance;
                    TextView distanceText = findViewById(R.id.txtViewDistance);

                    String distanceMetric = getApplicationContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).getString("distanceMetric", "0");


                    if (distanceMetric.equals("0")) {
                        double km = totalDistance;
                        distanceText.setText("Total Distanced Travelled: "  + String.format("%.2f", km) + " km");
                    } else {
                        double miles = totalDistance * 0.621371;
                        miles = Math.round(miles * 100.0) / 100.0;

                        distanceText.setText("Total Distanced Travelled: " + String.valueOf(miles) + " miles");
                    }
                }

                LatLongPoints.add(currentLocation);
                drawGPSLine(LatLongPoints);
            }
        }
    };

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        return geofencePendingIntent;
    }

    public void buildGeofence(int ID, LatLng latLng, float radius) {
        geofenceList.add(new Geofence.Builder()
                .setRequestId(Integer.toString(ID))
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    public void drawGPSLine(ArrayList<LatLng> gpsPoints) {
        PolylineOptions line = new PolylineOptions().width(5).color(Color.BLUE);

        for (int i = 0; i < gpsPoints.size(); i++) {
            line.add(gpsPoints.get(i));
        }
        mMap.addPolyline(line);
    }


    public void addCircle(ArrayList<LatLng> geoFenceCoordinates) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.radius(75);
        circleOptions.strokeColor(Color.BLACK);
        circleOptions.fillColor(Color.LTGRAY);
        circleOptions.strokeWidth(5);

        for (int i = 0; i < geoFenceCoordinates.size(); i++) {
            circleOptions.center(geoFenceCoordinates.get(i));
            mMap.addCircle(circleOptions);
        }
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        currentPositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(56.462002, -2.970700)).title("My Location"));
        geofencingClient = LocationServices.getGeofencingClient(this);

        ArrayList<LatLng> pointsOfInterest = new ArrayList<>();

        LatLng tannadice_park = new LatLng(56.47479113892371, -2.968978643868099);
        LatLng dens_park = new LatLng(56.47512756806344, -2.971774961627042);
        LatLng abertay_university = new LatLng(56.46341960060735, -2.973959916879307);
        LatLng dundee_university = new LatLng(56.4583691719005, -2.982174988044316);
        LatLng va_dundee = new LatLng(56.45759278960625, -2.966939170857703);
        LatLng dundee_airport = new LatLng(56.454275573348546, -3.01583558804444);
        LatLng overgate = new LatLng(56.460182912789605, -2.972678302024439);
        LatLng wellgate = new LatLng(56.46433610732626, -2.9693356285278263);

        pointsOfInterest.add(tannadice_park);
        pointsOfInterest.add(dens_park);
        pointsOfInterest.add(abertay_university);
        pointsOfInterest.add(dundee_university);
        pointsOfInterest.add(va_dundee);
        pointsOfInterest.add(dundee_airport);
        pointsOfInterest.add(overgate);
        pointsOfInterest.add(wellgate);

        for (int i = 0; i < pointsOfInterest.size(); i++) {
            buildGeofence(i, pointsOfInterest.get(i), 75);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, aVoid -> Log.i("TAG", "Geofence success"))
                .addOnFailureListener(this, e -> Log.i("TAG", "Geofence failed"));

        addCircle(pointsOfInterest);

    }


}

