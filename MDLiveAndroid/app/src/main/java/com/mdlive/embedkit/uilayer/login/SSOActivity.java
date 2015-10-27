package com.mdlive.embedkit.uilayer.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.global.MDLiveConfig.ENVIRON;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendingVisitService;
import com.mdlive.unifiedmiddleware.services.SSO2Service;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 7/2/2015.
 */
public class SSOActivity extends MDLiveBaseActivity {
    private ProgressDialog mProgressDialog;
    private ProgressDialog pDialog = null;
    private String mToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_sso);
        MdliveUtils.clearSharedPrefValues(this);

        mProgressDialog = MdliveUtils.getProgressDialog(getString(R.string.mdl_please_wait), this);
        setProgressBar(findViewById(R.id.progressDialog));

        Bundle extras = getIntent().getExtras();

        String affiliateEncyData = getData(extras, "affiliate_sso_login");
        ENVIRON env=null;
        try{
            env = ENVIRON.valueOf(getData(extras, "env"));
        }catch(IllegalArgumentException e){
            env = ENVIRON.UNDEFINED;
        }
        finally{
            MDLiveConfig.CURRENT_ENVIRONMENT = env;
        }

        MDLiveConfig.setData(env);

        makeSSO2call(affiliateEncyData);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Extract affiliate data
     * @return
     */
    private String getData(Bundle b, String key)
    {
        String s=null;

        if(b != null) {
            s = b.getString(key);
        }

        return(s);
    }


    /**
     * @param errorCode : Error code on the user information processing
     * @param errorMessage : Message to be displayed to user on the screen
     *                     After passing the error details to affiliate close the embedkit activity from  visibility
     */
    public void notifyErrorAffiliate(String errorCode, String errorMessage){
        Intent errorIntent = new Intent();
        errorIntent.putExtra("error_code",errorCode);
        errorIntent.putExtra("error_message",errorMessage);
        setResult(RESULT_CANCELED, errorIntent);
        finish();
        MdliveUtils.closingActivityAnimation(SSOActivity.this);
    }

    /**
     * SSO Phase II :
     * makes the customer_logins/embed_kit call to get the UniqueId.
     *
     * Class : SSOService - Service class used to fetch the UniqueId
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     *
     * After getting the UniqueId and AuthKey save them to non-persistent storage, i.e. volatile RAM.  They **MUST NOT** be stored in SharedPrefs.
     *
     * @param affiliateEncyData  client secret used to generate the user credentials(UniqueID, IDPID, RemoteID, MDL_Web_APIKey)
     *
     *        This parameter has the three values namely client_secret, Degital_signature and Encrypted message for Phase 2
     *                           and User information JSON for Phase 1
     */
    private void makeSSO2call( String affiliateEncyData)
    {
        if (affiliateEncyData == null || affiliateEncyData.isEmpty()) {
            // With reference to Confluence error code "EKSSO03" been passed when the request misses the required data
            notifyErrorAffiliate("EKSSO03", "Required params missing");
        }else {

            showProgress();

            NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.e("Response", response.toString());
                        hideProgress();
                        MDLiveConfig.USR_UNIQ_ID = response.getString(MDLiveConfig.UNIQUE_ID_STRINGNAME);
                        MDLiveConfig.AUTH_KEY = response.getString(MDLiveConfig.AUTHORIZATION_KEY);
                        mToken = response.getString("token");

                    //    Log.w("EMBEDKIT SSO2","********\n********\n******* RemoteUsrId = "+ MDLiveConfig.USR_UNIQ_ID);
                    //    Log.w("EMBEDKIT SSO2","********\n********\n******* Auth Key = "+ MDLiveConfig.AUTH_KEY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    loadUserInformationDetails();
                }
            };

            NetworkErrorListener errorListener = new NetworkErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgress();
                    try{
                        if (error.networkResponse != null) {
                            NetworkResponse errorResponse = error.networkResponse;
                            Log.e("Status Code", "" + error.networkResponse.statusCode);
                            if (error.getClass().equals(TimeoutError.class)) {
                                // send timeout error message back to affiliate
                                notifyErrorAffiliate("408", "Request connection time out");

                            } else if (errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY || errorResponse.statusCode == HttpStatus.SC_NOT_FOUND || errorResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e("responseBody", responseBody);
                                JSONObject errorObj = new JSONObject(responseBody);
                                String errorCode = errorObj.has("error_code") ? errorObj.getString("error_code"): "";
                                String message = "";
                                if (errorObj.has("message")) {
                                    message = errorObj.getString("message");
                                } else if (errorObj.has("error")) {
                                    message = errorObj.getString("error");
                                } else { // Worst case can show time oout exception
                                    notifyErrorAffiliate("408", "Request connection time out");
                                }
                                notifyErrorAffiliate(errorCode, message);
                            } else if (errorResponse.statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                                notifyErrorAffiliate(errorResponse.statusCode + "", "Request connection time out");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        notifyErrorAffiliate("500", "500 Internal server error");
                    }
                }
            };

            SSO2Service ssoService = new SSO2Service(this, mProgressDialog);
            ssoService.doSSORequest(affiliateEncyData, successListener, errorListener);
        }
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
        showProgress();

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                handleSuccessResponse(response);
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    MdliveUtils.handelVolleyErrorResponse(SSOActivity.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, SSOActivity.this);
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

        switch(MDLiveConfig.SELECTED_COMPONENT){
            case SYMPTOM_CHECKER:

                break;

            case MY_MESSAGES:

            case MY_HEALTH:

            case MY_ACCOUNT:

            case DOCTOR_CONSULT:

                try {
                    Class clazz_mdlGetStarted = Class.forName(getString(R.string.mdl_mdlive_sav_module));

                    if(response !=  null){
                        if(response.has("notifications")){
                            JSONObject notifications = response.getJSONObject("notifications");
                            if(notifications.has("upcoming_appointments")) {

                                if(notifications.getInt("upcoming_appointments") > 0){
                                    getPendingAppointments();
                                }else{
                                    hideProgress();


                                    final Intent intent = new Intent(getBaseContext(), clazz_mdlGetStarted);
                                    intent.putExtra("token", mToken);
                                    startActivity(intent);

                                    finish();
                                }
                            }
                        }
                    }

                } catch (ClassNotFoundException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case CALL_ASSIST:

                break;

            default:        //

                break;
        }

    }

    /***
     *This method is used to get user Pending Appointments History from server.
     * MDLivePendingVisitService-class is responsible for sending request to the server
     */

    public void getPendingAppointments(){
        showProgress();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                //Utils.hideProgressDialog(mProgressDialog);
                hideProgress();
                handlePendingResponse(response.toString());
            }
        };
        NetworkErrorListener errorListener=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    MdliveUtils.handelVolleyErrorResponse(SSOActivity.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, SSOActivity.this);
                }

            }
        };

        MDLivePendingVisitService getApponitmentsService=new MDLivePendingVisitService(SSOActivity.this, mProgressDialog);
        getApponitmentsService.getUserPendingHistory(successListener, errorListener);
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
        hideProgress();

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

                Class clazz_mdlGetStarted = Class.forName(getString(R.string.mdl_mdlive_sav_module));

                Intent i = new Intent(getApplicationContext(), clazz_mdlGetStarted);
                i.putExtra("token", mToken); // The token received from service on successful login
                startActivity(i);
                finish();
            }

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
