package com.example.cmp309cwk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private String getPointOfInterestName(Context context, String requestId) {
        int id = Integer.parseInt(requestId);
        //appendLandmarksVisited(context, id);
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

    public String appendLandmarksVisited(Context context, String landmarkCode) {

        String landmarkString = getPointOfInterestName(context, landmarkCode);

        if (context.getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE).contains("landmarksVisited")) {
            String landmarksVisited = context.getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE).getString("landmarksVisited", "");
            if (!landmarksVisited.contains(landmarkString)) {
                landmarksVisited = landmarksVisited + ", " + landmarkString;
                context.getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE).edit().putString("landmarksVisited", landmarksVisited).apply();
            }
        } else {
            String landmarksVisited = landmarkString;
            context.getSharedPreferences("landmarksVisited", Context.MODE_PRIVATE).edit().putString("landmarksVisited", landmarksVisited).apply();
        }

        return landmarkString;
    }

    private String getGeofenceTransitionDetails(Context context, GeofencingEvent geofencingEvent) {
        //LatLng latLng = new LatLng(geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude());
        return geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
    }



    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            String geofenceTransitionString = getGeofenceTransitionDetails(context, geofencingEvent);
            String pointOfInterestName = appendLandmarksVisited(context, geofenceTransitionString);
            Toast.makeText(context, "You have entered " + pointOfInterestName, Toast.LENGTH_LONG).show();
        }
    }

}