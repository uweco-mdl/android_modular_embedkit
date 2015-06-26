package com.mdlive.embedkit.uilayer.sav;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used to get current location of user.
 * This class uses GPS Provider to get current location.
 * Location Listener is used to get current location depends upon availability of Types
 * Timer tracker is used to track user location for particular time.
 * If time get elapsed, then it will determine automatically stops location listener.
 * Location results will be sent through LocationResult class.
 */

public class LocationCooridnates {

    private Context context;
    private Timer trackingTimer;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled = false;


    //Check whether GPS or PROVIDER enabled or not.
    public boolean checkLocationServiceSettingsEnabled(Context context) {
        this.context = context;

        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }


        //don't start listeners if no provider is enabled
        if (!gps_enabled)
            return false;
        else
            return true;
    }

    //Starting location listener and initializing result receiver.
    public boolean getLocation(Context context, LocationResult result) {

        //LocationResult callback class to pass location value from location service to user code.
        locationResult = result;

        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);

        trackingTimer = new Timer();
        // timer will run until 20000 milli second for location updates.
        trackingTimer.schedule(new GetLastLocation(), 20000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            trackingTimer.cancel();
            locationResult.gotLocation(location);
            stopListners();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void stopListners() {
        if (lm != null) {
            lm.removeUpdates(locationListenerGps);
            lm = null;
        }
    }

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);

            Location gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            }

            locationResult.gotLocation(null);
        }
    }

    /**
     * Result receiver class which will have body of gotLocation function at the place
     * of implementation of Activity.
     */

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}