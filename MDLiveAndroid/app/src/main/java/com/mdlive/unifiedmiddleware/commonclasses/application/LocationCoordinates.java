package com.mdlive.unifiedmiddleware.commonclasses.application;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * This class is used to get current location of user.
 * This class uses GPS Provider to get current location.
 * Location Listener is used to get current location depends upon availability of Types
 * Timer tracker is used to track user location for particular time.
 * If time get elapsed, then it will determine automatically stops location listener.
 * Location results will be sent through LocationResult class.
 */

public class LocationCoordinates {

    private Context context;
    private Timer trackingTimer;
    private LocationManager lm;
    private boolean gps_enabled = false;
    private boolean isTrackingLocation = false;
    public static String broadCastData = StringConstants.DEFAULT;

    public LocationCoordinates(Context context){
        this.context = context;
        broadCastData = StringConstants.DEFAULT;
    }

    public void setBroadCastData(String broadCastData){
        LocationCoordinates.broadCastData = broadCastData;
    }

    //Check whether GPS or PROVIDER enabled or not.
    public boolean checkLocationServiceSettingsEnabled(Context context) {
        this.context = context;

        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            if(lm != null)
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }


        //don't start listeners if no provider is enabled
        return gps_enabled;
    }

    /**
     * This function is used to get status of this class tracking location or not.
     */
    public boolean isTrackingLocation(){
        return  isTrackingLocation;
    }

    //Starting location listener and initializing result receiver.
    public boolean startTrackingLocation(Context context) {
        isTrackingLocation = true;
        if (gps_enabled && lm != null)
            // @ToDo : use a generic permissions check handling method
            //if(permissionsEnabled(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);

        trackingTimer = new Timer();
        // timer will run until 20000 milli second for location updates.
        trackingTimer.schedule(new GetLastLocation(), 20000);
        return true;
    }


    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            trackingTimer.cancel();
            if(isTrackingLocation){
                //locationResult.gotLocation(location);
                sendLocationInfo(location);
                isTrackingLocation = false;
                stopListners();
            }
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
            if(locationListenerGps != null){
                if(lm != null){
                    lm.removeUpdates(locationListenerGps);
                    lm = null;
                }
            }
        }
    }

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            if(lm != null) {
                lm.removeUpdates(locationListenerGps);
            }
            Location gps_loc = null;
            if (gps_enabled) {
                gps_loc = getLastKnownLocation();
            }

            if (gps_loc != null) {
                //locationResult.gotLocation(gps_loc);
                sendLocationInfo(gps_loc);
                return;
            }
            sendLocationInfo(null);
            //locationResult.gotLocation(null);
        }
    }

    private Location getLastKnownLocation() {
        if(lm != null){
            List<String> providers = lm.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            return bestLocation;
        }
        return null;
    }

    public void sendLocationInfo(Location location){
        isTrackingLocation = false;
        Intent locationIntent = new Intent();
        if(location != null){
            locationIntent.putExtra("Latitude", location.getLatitude());
            locationIntent.putExtra("Longitude", location.getLongitude());
        }
        locationIntent.setAction(broadCastData);
        context.sendBroadcast(locationIntent);
    }

    /**
     * Result receiver class which will have body of gotLocation function at the place
     * of implementation of Activity.
     */
    /*public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }*/

    /**
     * Method to get the location from Google Location API
     * */
    public void getLocation() {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(((MDLiveBaseActivity)context).mGoogleApiClient);
            sendLocationInfo(mLastLocation);

            if (mLastLocation == null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
            } else {
                Log.d("Location error", "Cannot get location");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Creating google api client object
     * */
    public synchronized void buildGoogleApiClient() {
        ((MDLiveBaseActivity)context).mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle arg0) {
                        // Once connected with google api, get the location
                        getLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int arg0) {
                        ((MDLiveBaseActivity)context).mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i("Location Error", "Connection failed: ConnectionResult.getErrorCode() = "
                                + result.getErrorCode());
                    }
                })
                .addApi(LocationServices.API).build();
    }

}