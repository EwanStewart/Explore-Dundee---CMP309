package com.example.cmp309cwk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private LatLng getGeofenceTransitionDetails(Context context, GeofencingEvent geofencingEvent) {
        LatLng latLng = new LatLng(geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude());
        return latLng;
    }

    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            LatLng geofenceTransitionString = getGeofenceTransitionDetails(context, geofencingEvent);
            Log.i("TAG", String.valueOf(geofenceTransitionString));
        } else {
            Log.i("TAG", "b");
        }
    }




}