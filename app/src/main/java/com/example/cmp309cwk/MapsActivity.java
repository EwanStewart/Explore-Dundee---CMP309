package com.example.cmp309cwk;


import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker h;
    private FusedLocationProviderClient fusedLocationClient;

    private ArrayList<LatLng> latlong = new ArrayList<>();
    private ArrayList<LatLng> pointsOfInterest = new ArrayList<>();


    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
    private double totalDistance = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //credit: https://stackoverflow.com/questions/51054247/chronometer-showing-just-minutes-and-hours
        //--------------------------------------------------------------------------------------------------
        Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer);

        timeElapsed.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                cArg.setText(hh + ":" + mm + ":" + ss);
            }
        });

        timeElapsed.setBase(SystemClock.elapsedRealtime());
        timeElapsed.start();
        //---------------------------------------------------------------------------------------------------


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                        }
                    });
        }


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());

    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                h.setPosition(currentLocation);

                if (!latlong.isEmpty()) {
                    LatLng last = latlong.get(latlong.size() - 1);
                    float[] results = new float[1];
                    Location.distanceBetween(last.latitude, last.longitude, currentLocation.latitude, currentLocation.longitude, results);
                    float distance = results[0] / 1000;
                    totalDistance += distance;
                    TextView distanceText = findViewById(R.id.txtViewDistance);
                    distanceText.setText("Distance Travelled: " + String.format("%.2f", totalDistance) + " KM");

                }


                latlong.add(currentLocation);
                drawGPSLine(latlong);
            }
        }
    };


    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public void buildGeofence(int ID, LatLng latLng, float radius) {
        final boolean add = geofenceList.add(new Geofence.Builder()
                .setRequestId(Integer.toString(ID))
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
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

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng z = new LatLng(0, 0);
        h = mMap.addMarker(new MarkerOptions().position(z).title("My Location"));
        int radius = 75;
        geofencingClient = LocationServices.getGeofencingClient(this);


        pointsOfInterest = new ArrayList<>();

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
            buildGeofence(i, pointsOfInterest.get(i), radius);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("TAG", "Geofences added");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("FAILED", "Geofences added");
                    }
                });

        addCircle(pointsOfInterest);

    }


}

