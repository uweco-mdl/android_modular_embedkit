package com.mdlive.embedkit.uilayer.WaitingRoom;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.MDLiveVsee;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveSummary;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * THis is the waiting Room screen. The provider status is checked here. If the Provider status is
 * true, then the VSeeActivity is called. In the returning user case, where the user is taken back
 * to this swcreen by Vsee, if the provider status is false, MDLiveSummary activity  is called.
 *
 */
public class MDLiveWaitingRoom extends MDLiveBaseActivity{

    private WaitingRoomService waitingService;
    public static String OPEN_URI = "mdlive://mdlivemobile/vsee?result=thankyou";
    private String userName=null,password=null;
    private boolean isReturning,isStartedSummary;
    private Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        handler  = new Handler();
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
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    NetworkResponse errorResponse = error.networkResponse;
                    Log.e("WaitingRoom 1 REsponse", errorObj.toString());
                    if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("message") || errorObj.has("error")) {
                            final String errorMsg = errorObj.has("message")?errorObj.getString("message") : errorObj.getString("error");
                            (MDLiveWaitingRoom.this).runOnUiThread(new Runnable() {
                                public void run() {
                                    MdliveUtils.showDialog(MDLiveWaitingRoom.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, "OK", null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveGetStarted.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            dialog.dismiss();
                                            MDLiveWaitingRoom.this.finish();
                                        }
                                    }, null);
                                }
                            });
                        }
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveWaitingRoom.this, error, null);
                    }

                    if (isReturning && !isStartedSummary) {
                        Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                        startActivity(i);
                        MdliveUtils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                        isStartedSummary = true;
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
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
            }
        };
        NetworkErrorListener errorListner = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveWaitingRoom.this);
                    }
                }
                if(isReturning && !isStartedSummary){
                    Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                    startActivity(i);
                    MdliveUtils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                    isStartedSummary = true;
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
            if(resObj.getString("provider_status").equals("true")){
                ((TextView)findViewById(R.id.txt_waitingtext)).setText("Doctor has arrived...");
                ((TextView)findViewById(R.id.numberOne)).setTextColor(getResources().getColor(R.color.grey_txt));
                ((TextView)findViewById(R.id.numberTwo)).setTextColor(getResources().getColor(R.color.green));
                ((TextView)findViewById(R.id.numberThree)).setTextColor(getResources().getColor(R.color.grey_txt));
                getVSEECredentials();
            }else if(isReturning && !resObj.getString("provider_status").equals("true") && !isStartedSummary) {
                Log.d("Waiting Room -->","handleSuccessResponse ==");
                Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveSummary.class);
                startActivity(i);
                MdliveUtils.hideSoftKeyboard(MDLiveWaitingRoom.this);
                isStartedSummary = true;
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
                Intent i = new Intent(MDLiveWaitingRoom.this, MDLiveVsee.class);
                Log.e("VeeSEE -->", "Final reached....");
                i.putExtra("username",userName);
                i.putExtra("password",password);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
//                overridePendingTransition(0, 0);

//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 5000);
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

    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.home_dialog_title));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.home_dialog_text));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(handler!=null){
                        handler.removeCallbacksAndMessages(null);
                    }
                    ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
                    Intent intent = new Intent();
                    ComponentName cn = new ComponentName(MdliveUtils.ssoInstance.getparentPackagename(),
                            MdliveUtils.ssoInstance.getparentClassname());
                    intent.setComponent(cn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = alertDialog.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
            }
        });
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        isReturning = getIntent().getBooleanExtra("isReturning",false);
        isStartedSummary = false;
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
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
}
