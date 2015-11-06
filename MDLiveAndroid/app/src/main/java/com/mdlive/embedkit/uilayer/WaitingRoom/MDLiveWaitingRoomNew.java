package com.mdlive.embedkit.uilayer.WaitingRoom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.MDLiveVsee;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by srinivasan_ka on 6/13/2015.
 */
public class MDLiveWaitingRoomNew extends Activity{

    private WaitingRoomService waitingService;
    public static String OPEN_URI = "mdlive://mdlivemobile/mdlive_vsee?result=thankyou";
    private String userName=null,password=null;
    private ProgressDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_waiting_new_room);
        pDialog = MdliveUtils.getFullScreenProgressDialog(this);
        updateUiForProviderStatus(1);
        getProviderStatus();
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
                handleSuccessResponse(response.toString());//Method to handle the response from Server
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoomNew.this, error, pDialog);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveWaitingRoomNew.this);
                }
            }
        };

        waitingService = new WaitingRoomService(MDLiveWaitingRoomNew.this, null);
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
                //Log.v("Response Vsee", response.toString());
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoomNew.this, error, pDialog);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveWaitingRoomNew.this);
                }
            }
        };
        waitingService = new WaitingRoomService(MDLiveWaitingRoomNew.this, null);
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
            if(resObj.getString("provider_status").equals("true")){
                updateUiForProviderStatus(2);
                getVSEECredentials();
            }else{
                getProviderStatus();
                // Toast.makeText(MDLiveWaitingRoomNew.this,resObj.getString("message"),Toast.LENGTH_SHORT).show();
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
                updateUiForProviderStatus(3);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "cc", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MDLiveWaitingRoomNew.this, MDLiveVsee.class);
                        i.putExtra("username",userName);
                        i.putExtra("password",password);
                        startActivity(i);
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


}
