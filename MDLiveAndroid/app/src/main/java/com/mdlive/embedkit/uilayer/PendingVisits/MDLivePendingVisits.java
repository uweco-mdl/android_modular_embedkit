package com.mdlive.embedkit.uilayer.PendingVisits;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendigVisitService;

import org.json.JSONException;
import org.json.JSONObject;

public class MDLivePendingVisits extends Activity {
    private ProgressDialog pDialog;
    private Button resumeBtn;
    private TextView txtPatientName,txtReason,txtDoctorName,txtAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pending_visits);
        pDialog= Utils.getProgressDialog("Please wait...", MDLivePendingVisits.this);
        getUserInformation();

    }


    /***
     * This function will deals with all necessary UI Initialization.
     */
    public void initializeUI(){
        resumeBtn= (Button) findViewById(R.id.resumeConsultationBtn);
        txtPatientName = (TextView) findViewById(R.id.txtPendingPatientName);
        txtDoctorName= (TextView) findViewById(R.id.txtPendingDoctorName);
        txtReason= (TextView) findViewById(R.id.txtPendingReason);
        txtAddress = (TextView) findViewById(R.id.txtPharmacyAddress);
        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.APPT_ID, getIntent().getExtras().getString("AppointmentID"));//Saving appointment id in Preference which is received from service
                editor.commit();
                Intent waitingRoomIntent=new Intent(MDLivePendingVisits.this, MDLiveWaitingRoom.class);
                startActivity(waitingRoomIntent);
            }
        });

        Bundle extras=getIntent().getExtras();
        txtDoctorName.setText(extras.getString("DocName",""));
        txtReason.setText(extras.getString("Reason",""));
        // Provider name that needs to be shon at thank you screen.
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, extras.getString("DocName",""));
        editor.commit();
    }


    /**
     * This method is responsible for getting user information  details from server
     * MDLivePendigVisitService class will send the request to the server and recieves the response
     */

    public void getUserInformation(){
        Utils.showProgressDialog(pDialog);
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                Utils.hideProgressDialog(pDialog);

                Log.e("UserInfo Response", response.toString());
                handleUserInfoResponse(response.toString());
            }
        };
        NetworkErrorListener errorListner=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hideProgressDialog(pDialog);
                Log.e("UserInfo Error Response",error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        Utils.connectionTimeoutError(pDialog, MDLivePendingVisits.this);
                    }
                }

            }
        };

        MDLivePendigVisitService getUserInfoService=new MDLivePendigVisitService(MDLivePendingVisits.this,pDialog);
        getUserInfoService.getUserAggregateInformation(successListener, errorListner);
    }

    /**
     * This method is used to parse json from and append the value in the UI
     * @param response--Success resoponse from Server
     */

    public void handleUserInfoResponse(String response){
        try{
            initializeUI();
            JSONObject resObject=new JSONObject(response);
            JSONObject personalObj=resObject.getJSONObject("personal_info");
            JSONObject notificationsObj=resObject.getJSONObject("notifications");
            JSONObject pharmacyDetails=notificationsObj.getJSONObject("pharmacy_details");
            txtPatientName.setText(personalObj.getString("first_name")+" "+personalObj.getString("last_name"));
            txtAddress.setText(pharmacyDetails.getString("store_name")+"\n"+pharmacyDetails.getString("address1")+","+pharmacyDetails.getString("address2")+"\n"+
                    pharmacyDetails.getString("city")+","+pharmacyDetails.getString("state")+" "+pharmacyDetails.getString("zipcode"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * This methos will be called when user clicks on the cancel text in the UI
     * @param v-User clicked view from the screen
     */
    public void cancelVisit(View v){
        Utils.movetohome(MDLivePendingVisits.this, MDLiveLogin.class);
    }


}
