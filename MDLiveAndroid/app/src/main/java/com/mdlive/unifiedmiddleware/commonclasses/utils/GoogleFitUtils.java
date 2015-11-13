package com.mdlive.unifiedmiddleware.commonclasses.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.mdlive.embedkit.uilayer.lifestyle.MDLiveLifeStyleFragment;
import com.mdlive.embedkit.uilayer.myhealth.MedicalHistoryFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by unnikrishnan_b on 10/1/2015.
 */
public class GoogleFitUtils {

    private static final String TAG = "MDLIVE";
    public GoogleApiClient mClient = null;
    private JSONObject fitnessObject;
    public static final int REQUEST_OAUTH = 51;
    private static GoogleFitUtils sInstance;
    private Activity context;
    public boolean authInProgress = false;


    public static synchronized GoogleFitUtils getInstance() {
        if(sInstance == null){
            sInstance = new GoogleFitUtils();

        }
        return sInstance;
    }


    /**
     * Build a {@link com.google.android.gms.common.api.GoogleApiClient} that will authenticate the user and allow the application
     * to connect to Fitness APIs.
     */
    public void buildFitnessClient(final boolean isGetData, final String[] values, final Activity context) {
        // Create the Google API Client
        this.context = context;
        mClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                SharedPreferences sharedPref = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences userPrefs = context.getSharedPreferences(sharedPref.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPrefs.edit();
                                editor.putBoolean(PreferenceConstants.GOOGLE_FIT_PREFERENCES, true).commit();
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
//                                getGoogleFitData();
                                if (isGetData) {
                                    getGoogleFitData();
                                } else if (values != null) {
                                    setGoogleFitData(values, context);
                                } else if (context instanceof MedicalHistoryFragment.OnGoogleFitSyncResponse) {
                                    ((MedicalHistoryFragment.OnGoogleFitSyncResponse)context).setHealthStatus("success");
                                }

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            context, 0).show();
//                                    TODO : Show error dialog
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i("Google Fit - ", "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(context,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
        mClient.connect();

    }


    protected void getGoogleFitData() {
        // Setting a start and end date using a range of 1 year before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        final JSONObject jsonObject = new JSONObject();

        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT) // Read the weight details
                .read(DataType.TYPE_HEIGHT) // Read the height details
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(1)
                .build();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DataReadResult dataReadResult =
                            Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
                    for (DataSet dS : dataReadResult.getDataSets()) {
                        for (DataPoint dp : dS.getDataPoints()) {
                            Log.i(TAG, "Data point:");
                            Log.i(TAG, "\tType: " + dp.getDataType().getName());
                            for (Field field : dp.getDataType().getFields()) {
                                float value = 0.0f;
                                if (field.getName().equalsIgnoreCase("Weight")) {
                                    value =  Math.round(dp.getValue(field).asFloat() * 2.20462f);
                                } else {
                                    value = dp.getValue(field).asFloat();
                                }
                                jsonObject.put(field.getName(), value + "");
                                Log.i(TAG, "\tField: " + field.getName() +
                                        " Value: " + value);
                                break;
                            }
                        }
                    }
                    fitnessObject = jsonObject;
                } catch (Exception e) {
                    try {
                        fitnessObject = new JSONObject("{\"weight\" : \"0\", \"height\" : \"0\"}");
                        if (context instanceof MDLiveLifeStyleFragment.OnGoogleFitGetData) {
                            ((MDLiveLifeStyleFragment.OnGoogleFitGetData) context).getGoogleFitData(fitnessObject.toString());
                        } else if(context instanceof MedicalHistoryFragment.OnGoogleFitGetData){
                            ((MedicalHistoryFragment.OnGoogleFitGetData) context).getGoogleFitData(fitnessObject.toString());
                        }
                    }catch (JSONException ex){
                        // Never happen
                    }
                    e.printStackTrace();
                }
                if (context instanceof MDLiveLifeStyleFragment.OnGoogleFitGetData) {
                    ((MDLiveLifeStyleFragment.OnGoogleFitGetData) context).getGoogleFitData(fitnessObject.toString());
                } else if(context instanceof MedicalHistoryFragment.OnGoogleFitGetData){
                    ((MedicalHistoryFragment.OnGoogleFitGetData) context).getGoogleFitData(fitnessObject.toString());
                }

            }
        }).start();

    }

    protected void setGoogleFitData(String[] values, Context context) {
        // Setting a start and end date using a range of 1 year before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        final float weightValue = Float.parseFloat(values[1]) / 2.20462f;
        final float heightValue = Float.parseFloat(values[0]) / 39.3701f;
        final DataSet weightDataSet = createDataForRequest(
                DataType.TYPE_WEIGHT,
                DataSource.TYPE_RAW,
                weightValue,                  // weight in kgs
                startTime,              // start time
                endTime,                // end time
                TimeUnit.MILLISECONDS,
                context
        );

        final DataSet heightDataSet = createDataForRequest(
                DataType.TYPE_HEIGHT,
                DataSource.TYPE_RAW,
                heightValue,
                startTime,              // height in metres
                endTime,                // end time
                TimeUnit.MILLISECONDS,
                context
        );
        new Thread(new Runnable() {

            @Override
            public void run() {
                if(weightValue > 0) {
                    com.google.android.gms.common.api.Status weightInsertStatus =
                            Fitness.HistoryApi.insertData(mClient, weightDataSet)
                                    .await(1, TimeUnit.MINUTES);
                }
                if(heightValue > 0) {
                    com.google.android.gms.common.api.Status heightInsertStatus =
                            Fitness.HistoryApi.insertData(mClient, heightDataSet)
                                    .await(1, TimeUnit.MINUTES);
                }
            }
        }).start();

    }

    private DataSet createDataForRequest(DataType dataType
            , float dataSourceType
            , float values
            , long startTime, long endTime, TimeUnit timeUnit, Context context) {

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(dataType)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, timeUnit);
        dataPoint = dataPoint.setFloatValues(values);
        dataSet.add(dataPoint);

        return dataSet;
    }


    public static double[] convertMetersToFeet(double meters)
    {
        //function converts Feet to Meters.
        double toFeet = meters;
        toFeet = meters*3.2808f;  // official conversion rate of Meters to Feet
        double toInch = toFeet - Math.floor(toFeet);
        return new double[]{Math.floor(toFeet),Math.round(toInch * 12)};
    }
}
