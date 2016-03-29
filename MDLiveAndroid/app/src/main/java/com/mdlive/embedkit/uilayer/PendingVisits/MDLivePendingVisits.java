package com.mdlive.embedkit.uilayer.PendingVisits;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCoordinates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendingVisitService;

import org.json.JSONObject;

public class MDLivePendingVisits extends MDLiveBaseActivity {
    private TextView txtPatientName, txtAddress;
    private LocationCoordinates locationService;
    private IntentFilter intentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pending_visits);
        clearMinimizedTime();
        initializeUI();
        getUserInformation();

    }


    /**
     * This function performs all necessary UI Initializations.
     */
    public void initializeUI() {
        Button resumeBtn = (Button) findViewById(R.id.resumeConsultationBtn);
        txtPatientName = (TextView) findViewById(R.id.txtPendingPatientName);
        TextView txtDoctorName = (TextView) findViewById(R.id.txtPendingDoctorName);
        TextView txtReason = (TextView) findViewById(R.id.txtPendingReason);
        txtAddress = (TextView) findViewById(R.id.txtPharmacyAddress);
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());
        setProgressBar(findViewById(R.id.progressDialog));
        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.APPT_ID, getIntent().getExtras().getString("AppointmentID"));//Saving appointment id in Preference which is received from service
                editor.commit();
                try {
                    Class clazz = Class.forName("com.mdlive.sav.WaitingRoom.MDLiveWaitingRoom");
                    Intent waitingRoomIntent = new Intent(MDLivePendingVisits.this, clazz);
                    startActivity(waitingRoomIntent);
                    MdliveUtils.startActivityAnimation(MDLivePendingVisits.this);
                } catch (ClassNotFoundException e){
                    /*Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();*/
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.mdl_mdlive_module_not_found),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        locationService = new LocationCoordinates(this);
        // First we need to check availability of play services
        if (MdliveUtils.checkPlayServices(this)) {
            // Building the GoogleApi client
            locationService.buildGoogleApiClient();
         }

        Bundle extras = getIntent().getExtras();
        txtDoctorName.setText(extras.getString("DocName", ""));
        txtReason.setText(extras.getString("Reason", ""));
        // Provider name that needs to be shown at thank you screen.
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, extras.getString("DocName", ""));
        editor.commit();
    }


    /**
     * This method is responsible for getting user information  details from server
     * MDLivePendigVisitService class will send the request to the server and recieves the response
     */

    public void getUserInformation() {
        showProgress();
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                handleUserInfoResponse(response.toString());
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handleVolleyErrorResponse(MDLivePendingVisits.this, error, getProgressDialog());

            }
        };

        MDLivePendingVisitService getUserInfoService = new MDLivePendingVisitService(MDLivePendingVisits.this, null);
        getUserInfoService.getUserAggregateInformation(successListener, errorListner);
    }

    /**
     * This method is used to parse json from and append the value in the UI
     *
     * @param response--Success resoponse from Server
     */

    public void handleUserInfoResponse(String response) {
        try {
            JSONObject resObject = new JSONObject(response);
            JSONObject personalObj = resObject.getJSONObject("personal_info");
            JSONObject notificationsObj = resObject.getJSONObject("notifications");
            JSONObject pharmacyDetails = notificationsObj.getJSONObject("pharmacy_details");
            txtPatientName.setText(personalObj.getString("first_name") + " " + personalObj.getString("last_name"));
            txtAddress.setText(pharmacyDetails.getString("store_name") + "\n" + pharmacyDetails.getString("address1") + "," + pharmacyDetails.getString("address2") + "\n" +
                    pharmacyDetails.getString("city") + "," + pharmacyDetails.getString("state") + " " + pharmacyDetails.getString("zipcode"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * This method will be called when user clicks on the Cancel text in the UI
     *
     * @param v-User clicked view from the screen
     */
    public void cancelVisit(View v) {
        super.movetohome(v);
    }


    /**
     * This method will close the activity with a transition effect.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * This override function will be called on every time with this page loading.
     * <p/>
     * if any progressBar loading on screen anonymously on this callPharmacyServicewill stop it.
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            MdliveUtils.checkPlayServices(this);
            //registerReceiver(locationReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //unregisterReceiver(locationReceiver);
            //locationService.setBroadCastData(StringConstants.DEFAULT);
            if(locationService != null && locationService.isTrackingLocation()){
                locationService.stopListeners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgress();
            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handleMilesResponse(response);
                }
            };
            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgress();
                    MdliveUtils.handelVolleyErrorResponse(MDLivePendingVisits.this, error, null);
                }
            };
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                double lat = intent.getDoubleExtra("Latitude", 0d);
                double lon = intent.getDoubleExtra("Longitude", 0d);
                showProgress();
                PharmacyService services = new PharmacyService(MDLivePendingVisits.this, null);
                services.doMyPharmacyRequest(lat+"", lon+"",responseListener, errorListener);
            }
        }
    };
    public void handleMilesResponse(JSONObject response){
        hideProgress();
        try{
            JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
            ((TextView) findViewById(R.id.text_view_a)).setText(pharmacyDatas.getString("store_name"));
            ((TextView) findViewById(R.id.text_view_b)).setText(((pharmacyDatas.getString("distance") != null && !pharmacyDatas.getString("distance").isEmpty()) ?
                    pharmacyDatas.getString("distance").replace(" miles", "mi") : ""));
            ((TextView) findViewById(R.id.txtAddressLine2)).setText(pharmacyDatas.getString("address1"));
            ((TextView) findViewById(R.id.txtAddressLine3)).setText(pharmacyDatas.getString("city") + "," + pharmacyDatas.getString("state") + " " +
                    MdliveUtils.zipCodeFormat(pharmacyDatas.getString("zipcode")) + " ");
            setMaxWidthForLeftText(findViewById(R.id.relative_layout),
                    (TextView) findViewById(R.id.text_view_a),
                    (TextView) findViewById(R.id.text_view_b)
            );
        }catch (Exception e){
        }
    }
    */

    /**
     *  This method is used to call pharmacy service
     *  In pharmacy service, it requires GPS location details to get distance details.
     *
     *  @param errorListener - Pharmacy error response listener
     *  @param responseListener - Pharmacy detail Success response listener
     */
    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
                                    final NetworkErrorListener errorListener){
        if(locationService.checkLocationServiceSettingsEnabled(this)){
            showProgress();
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(getApplicationContext());
        }else{
            hideProgress();
        }
    }

}
