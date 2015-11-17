package com.mdlive.embedkit.uilayer.PendingVisits;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendingVisitService;

import org.json.JSONObject;

public class MDLivePendingVisits extends MDLiveBaseActivity {
    private TextView txtPatientName, txtAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pending_visits);
        clearMinimizedTime();
        initializeUI();
        getUserInformation();

    }


    /**
     * This function will deals with all necessary UI Initialization.
     */
    public void initializeUI() {
        Button resumeBtn = (Button) findViewById(R.id.resumeConsultationBtn);
        txtPatientName = (TextView) findViewById(R.id.txtPendingPatientName);
        TextView txtDoctorName = (TextView) findViewById(R.id.txtPendingDoctorName);
        TextView txtReason = (TextView) findViewById(R.id.txtPendingReason);
        txtAddress = (TextView) findViewById(R.id.txtPharmacyAddress);
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
                    Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
                }
            }
        });

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
                MdliveUtils.handelVolleyErrorResponse(MDLivePendingVisits.this, error, getProgressDialog());

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
     * This methos will be called when user clicks on the cancel text in the UI
     *
     * @param v-User clicked view from the screen
     */
    public void cancelVisit(View v) {
        super.movetohome(v);
    }


    /**
     * This method will close the activity with transition effect.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }


}
