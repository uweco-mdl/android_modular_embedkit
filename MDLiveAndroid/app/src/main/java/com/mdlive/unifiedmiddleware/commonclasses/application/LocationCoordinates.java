package com.mdlive.unifiedmiddleware.commonclasses.application;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;

import java.util.Timer;

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
        getLocation();
        return true;
    }


    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location == null) return;

            trackingTimer.cancel();
            if(isTrackingLocation){
                //locationResult.gotLocation(location);
                sendLocationInfo(location);
                isTrackingLocation = false;
                stopListeners();
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void stopListeners() {
        if (lm != null) {
            if(locationListenerGps != null){
                if(lm != null){
                    if ( Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return  ;
                    }

                    lm.removeUpdates(locationListenerGps);
                    lm = null;
                }
            }
        }
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
     * Method to get the location from Google Location API
     * */
    private void getLocation() {
        try {

            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return  ;
            }

            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(MDLiveBaseAppcompatActivity.mGoogleApiClient);
            sendLocationInfo(mLastLocation);

            if (mLastLocation != null) {
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
        MDLiveBaseAppcompatActivity.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle arg0) {
                        // Once connected with google api, get the location
                        getLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int arg0) {
                        MDLiveBaseAppcompatActivity.mGoogleApiClient.connect();
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