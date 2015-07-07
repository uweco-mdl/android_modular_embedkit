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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.LoginServices;
import com.mdlive.unifiedmiddleware.services.MDLivePendigVisitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * The wrapper class for Login Activity. The Login layout is set here. The necessary logic inputs
 * are passed to UMWLogin using setData() method.
 *
 */
public class MDLiveLogin extends Activity {

    private Button loginBtn;
    private EditText usernameEt,passwordEt;
    private ProgressDialog pDialog = null;
    private LocalisationHelper localisationHelper;
    private String token=null;
    private RelativeLayout progressBar;
    private LinearLayout loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_login);
        MDLiveConfig.setData();
        usernameEt = (EditText)findViewById(R.id.UserNameEt);
        passwordEt = (EditText)findViewById(R.id.PasswordEt);
        loginBtn = (Button)findViewById(R.id.LoginBtn);
        progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        loginInfo = (LinearLayout)findViewById(R.id.infoView);

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        Utils.clearSharedPrefValues(this);
//        pDialog = Utils.getProgressDialog("Please wait...", this);
        loginBtn.setOnClickListener(getLoginOnClickListener());

    }

    private View.OnClickListener getLoginOnClickListener(){
        View.OnClickListener loginOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveLogin.this);
                String username = usernameEt.getText().toString();
                String password = passwordEt.getText().toString();
                if(validateData(username,password)){
                    // Call service to check login
                    /**/
//                    pDialog.show();
                    setProgressBarVisibility();
                    NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(PreferenceConstants.USER_UNIQUE_ID,response.getString("uniqueid"));
                                editor.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            handleSuccessResponse(response);
                        }
                    };

                    NetworkErrorListener errorListener = new NetworkErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            pDialog.dismiss();
                            setInfoVisibilty();
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class)) {
                                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            passwordEt.setText("");
                                            dialog.dismiss();
                                        }
                                    };
                                    // Show timeout error message
                                    Utils.connectionTimeoutError(pDialog, MDLiveLogin.this);
                                }
                                else
                                {
                                    Utils.alert(pDialog,MDLiveLogin.this,error.toString());
                                }
                            }
                        }};

                    LoginServices services = new LoginServices(MDLiveLogin.this, pDialog);
                    services.doLoginRequest(username,password, responseListener,errorListener);
                }
            }
        };
        return loginOnClickListener;
    }

    private void handleSuccessResponse(JSONObject response) {
        try {
//            pDialog.dismiss();
            setInfoVisibilty();
            Log.d("Response", response.toString());
            if (response.has("msg") && response.getString("msg").equalsIgnoreCase("Success")){
                usernameEt.setText("");
                passwordEt.setText("");
                token=response.getString("token");
                getPendingAppointments();
            } else {
//                displayMessage(response.has("token")?response.getString("token"):localisationHelper.getLocalizedStringFromPrefs(this, "invalid_credentials"));
                Utils.alert(pDialog,MDLiveLogin.this,"invalid_credentials");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     *This method is used to get user Pending Appointments History from server.
     * MDLivePendigVisitService-class is responsible for sending request to the server
     */

    public void getPendingAppointments(){
//        Utils.showProgressDialog(pDialog);
        setProgressBarVisibility();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
//                Utils.hideProgressDialog(pDialog);
//                setInfoVisibilty();
                handlePendingResponse(response.toString());
                Log.e("Pending Response",response.toString());
            }
        };
        NetworkErrorListener errorListner=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Utils.hideProgressDialog(pDialog);
                setInfoVisibilty();
                Log.e("Pending Error Response",error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        Utils.connectionTimeoutError(pDialog, MDLiveLogin.this);
                    }
                }

            }
        };

        MDLivePendigVisitService getApponitmentsService=new MDLivePendigVisitService(MDLiveLogin.this,pDialog);
        getApponitmentsService.getUserPendingHistory(successListener,errorListner);
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
        try{
            JSONObject resObj=new JSONObject(response);
            JSONArray appointArray=resObj.getJSONArray("appointments");
            JSONArray onCallAppointmentArray=resObj.getJSONArray("oncall_appointments");
            if(appointArray.length()!=0){
                String docName=appointArray.getJSONObject(0).getString("physician_name");
                String appointmnetID=appointArray.getJSONObject(0).getString("id");
                String chiefComplaint=appointArray.getJSONObject(0).getString("chief_complaint");
                Intent pendingVisitIntent = new Intent(getApplicationContext(), MDLivePendingVisits.class);
                pendingVisitIntent.putExtra("DocName",docName); // The doctor name  from service on successful response
                pendingVisitIntent.putExtra("AppointmentID", appointmnetID); // The Appointment id  from service on successful response
                pendingVisitIntent.putExtra("Reason", chiefComplaint); // The Reason for visit from service on successful response
                startActivity(pendingVisitIntent);
                finish();
            }else {
                Intent i = new Intent(getApplicationContext(), MDLiveGetStarted.class);
                i.putExtra("token",token); // The token received from service on successful login
                startActivity(i);
                finish();
            }

        }catch (Exception e){
                e.printStackTrace();
        }

    }

    /**
     *
     * This method performs filed validation.
     *
     * NOTE : This method may be overridden by the child activity to have extra validations if necessary.
     *
     * @param username
     * @param password
     * @return - boolean value
     */
    protected boolean validateData(String username, String password){
        if(username!=null && username.trim().length()>0 && password!=null && password.trim().length()>0){
            if(Utils.isValidUserName(username) && Utils.isValidPassword(password)){
                return true;
            }
            else{
//                displayMessage(localisationHelper.getLocalizedStringFromPrefs(this, "invalid_credentials"));
                Utils.alert(pDialog,MDLiveLogin.this,"invalid_credentials");
            }
        } else{
//            displayMessage(localisationHelper.getLocalizedStringFromPrefs(this, "empty_credentials"));
            Utils.alert(pDialog,MDLiveLogin.this,"empty_credentials");
        }
        return false;
    }

    private void displayMessage(String message){
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                passwordEt.setText("");
                dialog.dismiss();
            }
        };
        // Show timeout error message
        Utils.showDialog(MDLiveLogin.this, MDLiveLogin.this.getApplicationInfo().loadLabel(MDLiveLogin.this.getPackageManager()).toString(),message, localisationHelper.getLocalizedStringFromPrefs(this, "OK"),null, onClickListener,null);
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
