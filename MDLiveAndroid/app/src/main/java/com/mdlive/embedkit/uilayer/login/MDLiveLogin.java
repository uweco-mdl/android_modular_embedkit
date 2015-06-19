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

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.LoginServices;

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
    private ProgressDialog pDialog;
    private LocalisationHelper localisationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*LoginActivityBean loginBean = new LoginActivityBean();
        loginBean.setUsernameEt((EditText) findViewById(R.id.UserNameEt));
        loginBean.setPasswordEt((EditText) findViewById(R.id.PasswordEt));
        loginBean.setLoginBtn((Button) findViewById(R.id.LoginBtn));
        loginBean.setHomeActivity(MDLiveDashboard.class);
        setData(loginBean);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_login);
        usernameEt = (EditText)findViewById(R.id.UserNameEt);
        passwordEt = (EditText)findViewById(R.id.PasswordEt);
        loginBtn = (Button)findViewById(R.id.LoginBtn);

        ViewGroup view = (ViewGroup)getWindow().getDecorView();
        Utils.clearSharedPrefValues(this);
        pDialog = Utils.getProgressDialog("Please wait...", this);
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
                    pDialog.show();
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
                            pDialog.dismiss();
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

                    LoginServices services = new LoginServices(MDLiveLogin.this, null);
                    services.doLoginRequest(username,password, responseListener,errorListener);
                }
            }
        };
        return loginOnClickListener;
    }

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            Log.d("Response", response.toString());
            if (response.has("msg") && response.getString("msg").equalsIgnoreCase("Success")){
                usernameEt.setText("");
                passwordEt.setText("");
//                Intent i = new Intent(getApplicationContext(), MDLiveMedicalHistory.class);
                Intent i = new Intent(getApplicationContext(), MDLiveGetStarted.class);
                i.putExtra("token",response.getString("token")); // The token received from service on successful login
                startActivity(i);
                finish();

            } else {
//                displayMessage(response.has("token")?response.getString("token"):localisationHelper.getLocalizedStringFromPrefs(this, "invalid_credentials"));
                Utils.alert(pDialog,MDLiveLogin.this,"invalid_credentials");
            }
        }catch(Exception e){
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
}
