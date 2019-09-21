package com.example.pratyush.geofencing;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeoFenceService extends IntentService {
    public static final String TAG = "GeoFenceService";

    public GeoFenceService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()){

        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER){
                Intent notification = new Intent(this, MyDialogPositive.class);
                startActivity(notification);
                Log.d("GeoL","Entering Geofence");
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                Intent notification = new Intent(this, MyDialogNegative.class);
                startActivity(notification);
                Log.d("GeoL","Exiting Geofence");
            }
        }
    }
}