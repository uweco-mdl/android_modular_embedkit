package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.CheckInServices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by srinivasan_ka on 6/18/2015.
 */
public class MDLivePendingAppCheckIn extends Activity {

    private ProgressBar pBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_pendingapp_checkin);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.VISIBLE);
    }

    public void getUserInfoDetails(){

        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pBar.setVisibility(View.INVISIBLE);
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pBar.setVisibility(View.INVISIBLE);
            }};

        CheckInServices services = new CheckInServices(MDLivePendingAppCheckIn.this, null);
        services.doUserInfoRequest(responseListener,errorListener);
    }

    public void handleSuccessResponse(JSONObject response){
        try {
            if(response !=  null){
                if(response.has("notifications")){
                    JSONObject notifications = response.getJSONObject("notifications");
                    if(notifications.has("upcoming_appointments")){
                        if(notifications.getInt("upcoming_appointments") > 0){

                        }else{

                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
