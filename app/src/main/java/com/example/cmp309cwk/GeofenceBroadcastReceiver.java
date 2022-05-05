package com.example.cmp309cwk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private ArrayList<String> landmarksVisited = new ArrayList<>();

    public boolean appendLandmarksVisited(Context context, int landmarkCode) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences != null) {
            sharedPreferences = context.getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE);
            int size = sharedPreferences.getInt("size", 0);
            for(int i=0;i<size;i++)
            {
                landmarksVisited.add(sharedPreferences.getString("landmark" + i, null));
            }
            landmarksVisited.add(Integer.toString(landmarkCode));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("size", landmarksVisited.size());
            for(int i=0;i<landmarksVisited.size();i++) {
                editor.putString("landmark" + i, landmarksVisited.get(i));
            }
            editor.apply();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("size", 1);
            editor.putString("landmark0", Integer.toString(landmarkCode));
            editor.apply();
        }

        SharedPreferences.Editor mEdit1 = sharedPreferences.edit();
        mEdit1.putInt("Status_size", landmarksVisited.size());

        //output the list of landmarks visited
        for(int i=0;i<landmarksVisited.size();i++)
        {
            Log.i("TAG", landmarksVisited.get(i).toString());
        }

        return mEdit1.commit();
    }

    private String getGeofenceTransitionDetails(Context context, GeofencingEvent geofencingEvent) {
        //LatLng latLng = new LatLng(geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude());
        return geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
    }

    private String getPointOfInterestName(Context context, String requestId) {
        int id = Integer.parseInt(requestId);
        appendLandmarksVisited(context, id);
        switch (id) {
            case 0:
                return "Tannadice Park";
            case 1:
                return "Dens Park";
            case 2:
                return "Abertay University";
            case 3:
                return "Dundee University";
            case 4:
                return "V&A Dundee";
            case 5:
                return "Dundee Airport";
            case 6:
                return "Overgate";
            case 7:
                return "Wellgate";
            default:
                return "";
        }
    }

    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            String geofenceTransitionString = getGeofenceTransitionDetails(context, geofencingEvent);
            String pointOfInterestName = getPointOfInterestName(context, geofenceTransitionString);
            Toast.makeText(context, "You have entered " + pointOfInterestName, Toast.LENGTH_LONG).show();

        } else {
            Log.i("TAG", "b");
        }
    }

}