package com.mdlive.embedkit.uilayer.WaitingRoom;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.MDLiveVsee;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.embedkit.uilayer.login.MDLiveSummary;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * THis is the waiting Room screen. The provider status is checked here. If the Provider status is
 * true, then the VSeeActivity is called. In the returning user case, where the user is taken back
 * to this swcreen by Vsee, if the provider status is false, MDLiveSummary activity  is called.
 *
 */
public class MDLiveWaitingRoom extends Activity{

    private WaitingRoomService waitingService;
    public static String OPEN_URI = "mdlive://mdlivemobile/vsee?result=thankyou";
    private String userName=null,password=null;
    private boolean isReturning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_waiting_room);
        isReturning = getIntent().getBooleanExtra("isReturning",false);
        getProviderStatus();
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });
    }


    /***
     *This function will retrieve user Provider status from the server.
     *@Listner-successListner will  handles the success response from server.
     *@Listner-errorListener will handles error response from server.
     *WaitingRoomService class will send the get request to the server and receives the corresponding response
     */

    public void getProviderStatus() {
        NetworkSuccessListener successListener = new NetworkSuccessListener() {

            @Override
            public void onResponse(Object response) {
                Log.e("Response Provider", response.toString());
                handleSuccessResponse(response.toString());//Method to handle the response from Server
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Provider Error", error.toString());
                Utils.handelVolleyErrorResponse(MDLiveWaitingRoom.this, error, null);

                if(isReturning){
                    Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    Utils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                    finish();
                }
            }
        };

        waitingService = new WaitingRoomService(MDLiveWaitingRoom.this, null);
        waitingService.doGetProviderStatus(successListener, errorListner);
    }


    /***
     *This function will retrieve username and password from the server.
     *@Listner-successListner will  handles the success response from server.
     *@Listner-errorListener will handles error response from server.
     *WaitingRoomService class will send the post request to the server and receives the corresponding response
     */

    public void getVSEECredentials() {
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
               handleVSeeResponse(response.toString());
                Log.e("Response Vsee", response.toString());
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error Vsee", error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(null, MDLiveWaitingRoom.this);
                    }
                }
                if(isReturning){
                    Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    Utils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                    finish();
                }
            }
        };
        waitingService = new WaitingRoomService(MDLiveWaitingRoom.this, null);
        waitingService.doPostVseeCredentials(getPostBody(), successListener, errorListner);
    }


    /***
     * This method will invoked on successful completion of getProvider method.
     * @param response-Object contains the response in Json Format
     *  if Provider status is true getVSEECredentials credential method will be invoked else
     *   again getProviderStatus method will be called.
     */
    public void handleSuccessResponse(String response) {
        try{
            JSONObject resObj=new JSONObject(response);
            Log.d("isReturning",isReturning + "" + " - Provider status" + resObj.getString("provider_status"));
            if(resObj.getString("provider_status").equals("true")){
                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Doctor has arrived...");
                ((TextView)findViewById(R.id.numberOne)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberTwo)).setTextColor(getResources().getColor(R.color.green));
                ((TextView)findViewById(R.id.numberThree)).setTextColor(getResources().getColor(R.color.grey_txt));
                getVSEECredentials();
            }else if(isReturning && !resObj.getString("provider_status").equals("true")) {
                Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                Utils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                finish();
            }else {
                getProviderStatus();
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /***
     * This method will be responsible for parsing the response from VseeCredential,
     * @param response-Object contains the response in Json Format
     *
     */

    public void handleVSeeResponse(String response){
        try{
            if(response == null){
                getVSEECredentials();
            }else{
                JSONObject resObj=new JSONObject(response);
                userName=resObj.getString("username");
                password=resObj.getString("password");
                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Starting consultation...");
                ((TextView)findViewById(R.id.numberOne)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberTwo)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberThree)).setTextColor(getResources().getColor(R.color.green));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveVsee.class);
                        i.putExtra("username",userName);
                        i.putExtra("password",password);
                        startActivity(i);
                        finish();
                    }
                }, 5000);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    /***
     * This method will be responsible for creating post params in Json Format
     * @return -postparams as json format.
     */
    public String getPostBody() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String apptId = sharedpreferences.getString(PreferenceConstants.APPT_ID, "");

        HashMap<String, String> postBody = new HashMap<String, String>();
        postBody.put("id", apptId);
        postBody.put("open_uri", OPEN_URI);
        return new Gson().toJson(postBody);
    }


    public void updateUiForProviderStatus(int stageId){
        switch (stageId){
            case 1:
                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Waiting for Doctor...");
                ((TextView)findViewById(R.id.numberOne)).setTextColor(getResources().getColor(R.color.green));
                ((TextView)findViewById(R.id.numberTwo)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberThree)).setTextColor(getResources().getColor(R.color.grey_txt));
            case 2:
                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Doctor has arrived...");
                ((TextView)findViewById(R.id.numberOne)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberTwo)).setTextColor(getResources().getColor(R.color.green));
                ((TextView)findViewById(R.id.numberThree)).setTextColor(getResources().getColor(R.color.grey_txt));
            case 3:
                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Starting consultation...");
                ((TextView)findViewById(R.id.numberOne)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberTwo)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberThree)).setTextColor(getResources().getColor(R.color.green));
        }

    }
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLiveWaitingRoom.this, MDLiveLogin.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isReturning = getIntent().getBooleanExtra("isReturning",false);
        if(isReturning){
            getProviderStatus();
        }
    }


    @Override
    public void onBackPressed() {

    }
    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    protected void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
}
