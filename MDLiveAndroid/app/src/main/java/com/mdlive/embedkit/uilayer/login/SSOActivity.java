package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.SSOUser;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.SSOService;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 7/2/2015.
 */
public class SSOActivity extends Activity {
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_sso);

        MDLiveConfig.setData();

        mProgressDialog = Utils.getProgressDialog("Please Wait.....", this);

        makeSSOCall();
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
    private void makeSSOCall() {
        final SSOUser user = getUser();
        Utils.ssoInstance = getUser();
        if (user == null) {
            Utils.showDialog(this, "Error", getString(R.string.user_details_missing), new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            return;
        }

        mProgressDialog.show();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
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

                loadUserInformationDetails();
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Utils.handelVolleyErrorResponse(SSOActivity.this, error,mProgressDialog);
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
        if(!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        final NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog(mProgressDialog);
                handleSuccessResponse(response);
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                mProgressDialog.dismiss();
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
                        Utils.connectionTimeoutError(mProgressDialog, SSOActivity.this);
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
                    if(notifications.has("upcoming_appointments")){
                        Intent intent = null;

                        if(notifications.getInt("upcoming_appointments") > 0){
                            intent = new Intent(getBaseContext(), MDLivePendingVisits.class);
                        }else{
                            intent = new Intent(getBaseContext(), MDLiveGetStarted.class);
                        }

                        startActivity(intent);

                        finish();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
