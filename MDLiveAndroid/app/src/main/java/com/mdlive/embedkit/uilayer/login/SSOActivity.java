package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SSOUser;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendigVisitService;
import com.mdlive.unifiedmiddleware.services.SSOService;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 7/2/2015.
 */
public class SSOActivity extends Activity {
    private ProgressDialog mProgressDialog;
    private String mToken;
    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_sso);
        MdliveUtils.clearSharedPrefValues(this);

        mProgressDialog = MdliveUtils.getProgressDialog("Please Wait.....", this);
        progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        SSOUser ssoUser = getUser();
        MDLiveConfig.setData(ssoUser.getCurrentEnvironment());
        makeSSOCall(ssoUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Get the User send through Intent or creates a dummy SSOUser, which is passed used for getting the uniqueid
     * @return
     */
    private SSOUser getUser() {
        SSOUser user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(SSOUser.SSO_USER) != null) {
            user = getIntent().getExtras().getParcelable(SSOUser.SSO_USER);
        }
        return user;
    }

    /**
     * makes the customer_logins/embed_kit call to get the uniqueid.
     *
     * Class : SSOService - Service class used to fetch the uniqueid
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     *
     * After getting the uniqueid save it to shared preference.
     */
    private void makeSSOCall(final SSOUser user) {
        MdliveUtils.ssoInstance = user;
        if (user == null) {
            MdliveUtils.showDialog(this, "Error", getString(R.string.user_details_missing), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            return;
        }

        setProgressBarVisibility();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    setInfoVisibilty();
                    SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(PreferenceConstants.USER_UNIQUE_ID,response.getString("uniqueid"));
                    editor.commit();
                    mToken = response.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loadUserInformationDetails();
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                MdliveUtils.handelVolleyErrorResponse(SSOActivity.this, error, mProgressDialog);
            }
        };

        final SSOService ssoService = new SSOService(this, mProgressDialog);
        ssoService.doSSORequest(user, successListener, errorListener);
    }

    /**
     * makes the customer/user_information call to get the User information.
     *
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     *
     * After getting the uniqueid save it to shared preference.
     */
    private void loadUserInformationDetails() {
        setProgressBarVisibility();

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                setInfoVisibilty();
                handleSuccessResponse(response);
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                setInfoVisibilty();
                //Utils.handelVolleyErrorResponse(SSOActivity.this, error, mProgressDialog);
               if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        mProgressDialog.dismiss();
                        MdliveUtils.connectionTimeoutError(mProgressDialog, SSOActivity.this);
                    }
                }
            }};

        final UserBasicInfoServices services = new UserBasicInfoServices(SSOActivity.this, null);
        services.getUserBasicInfoRequest("", successCallBackListener, errorListener);
    }

    /**
     * Checks the upcoming appoinment count, depending on that
     * starts new screens either Pending Visit or Get Started
     * @param response
     */
    public void handleSuccessResponse(JSONObject response){
        try {
            if(response !=  null){
                if(response.has("notifications")){
                    JSONObject notifications = response.getJSONObject("notifications");
                    if(notifications.has("upcoming_appointments")) {

                        if(notifications.getInt("upcoming_appointments") > 0){
                            getPendingAppointments();
                        }else{
                            setInfoVisibilty();


                            final Intent intent = new Intent(getBaseContext(), MDLiveGetStarted.class);
                            intent.putExtra("token", mToken);
                            startActivity(intent);
                        }



                        finish();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /***
     *This method is used to get user Pending Appointments History from server.
     * MDLivePendigVisitService-class is responsible for sending request to the server
     */

    public void getPendingAppointments(){
        setProgressBarVisibility();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                //Utils.hideProgressDialog(mProgressDialog);
                setInfoVisibilty();
                handlePendingResponse(response.toString());
                Log.e("Pending Response",response.toString());
            }
        };
        NetworkErrorListener errorListner=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Utils.hideProgressDialog(mProgressDialog);
                setInfoVisibilty();
                Log.e("Pending Error Response", error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        MdliveUtils.connectionTimeoutError(mProgressDialog, SSOActivity.this);
                    }
                }

            }
        };

        MDLivePendigVisitService getApponitmentsService=new MDLivePendigVisitService(SSOActivity.this, mProgressDialog);
        getApponitmentsService.getUserPendingHistory(successListener, errorListner);
    }

    /**
     *
     * THis function handles the pending visits if any. If there is any pending visits,
     * the user will be taken to PEndingVisits screen, else the user will ber taken to
     * getstarted screen.
     *
     * @param response
     */
    public void handlePendingResponse(String response){
        setInfoVisibilty();

        try{
            JSONObject resObj=new JSONObject(response);
            JSONArray appointArray=resObj.getJSONArray("appointments");
            JSONArray onCallAppointmentArray=resObj.getJSONArray("oncall_appointments");
            if(appointArray.length()!=0){
                String docName=appointArray.getJSONObject(0).getString("physician_name");
                String appointmnetID=appointArray.getJSONObject(0).getString("id");
                String chiefComplaint=appointArray.getJSONObject(0).getString("chief_complaint");
                Intent pendingVisitIntent = new Intent(SSOActivity.this, MDLivePendingVisits.class);
                pendingVisitIntent.putExtra("DocName",docName); // The doctor name  from service on successful response
                pendingVisitIntent.putExtra("AppointmentID",appointmnetID); // The Appointment id  from service on successful response
                pendingVisitIntent.putExtra("Reason",chiefComplaint); // The Reason for visit from service on successful response
                startActivity(pendingVisitIntent);
                finish();
            }else {
                Intent i = new Intent(getApplicationContext(), MDLiveGetStarted.class);
                i.putExtra("token", mToken); // The token received from service on successful login
                startActivity(i);
                finish();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /*
  * set visible for the progress bar
  */
    public void setProgressBarVisibility()
    {
        progressBar.setVisibility(View.VISIBLE);

    }

    /*
    * set visible for the details view layout
    */
    public void setInfoVisibilty()
    {
        progressBar.setVisibility(View.GONE);

    }
}
