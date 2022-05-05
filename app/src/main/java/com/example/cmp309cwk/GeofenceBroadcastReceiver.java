package com.example.cmp309cwk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private String getGeofenceTransitionDetails(Context context, GeofencingEvent geofencingEvent) {
        //LatLng latLng = new LatLng(geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude());
        return geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
    }

    private String getPointOfInterestName(String requestId) {
        int id = Integer.parseInt(requestId);

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
            String pointOfInterestName = getPointOfInterestName(geofenceTransitionString);
            Toast.makeText(context, "You have entered " + pointOfInterestName, Toast.LENGTH_LONG).show();
            Log.i("TAG", geofenceTransitionString);
        } else {
            Log.i("TAG", "b");
        }
    }




}