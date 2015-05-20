package com.mdlive.mobile.uilayer.sav;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.VideoConsultServices;
import com.vsee.kit.VSeeKit;
import com.vsee.kit.VSeeServerConnection;
import com.vsee.kit.VSeeVideoManager;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class VseeMainActivity extends Activity
{
    private boolean stopUpdatingStatus = false;
    private static boolean CONSULTED = false,
            FINISH = false;
    private ProgressBar spinner;
    private static final int BLINKING_PERIOD = 1300; // milliseconds

    private static VSeeServerConnection.SimpleVSeeServerConnectionReceiver simpleServerConnectionReceiver = null;
    private static VSeeVideoManager.SimpleVSeeVideoManagerReceiver simpleVidManagerReceiver = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vsee);

        CONSULTED = false;

        // Must always come first when using VSeeKit.  Doesn't consume many resources so it can always be done at app startup.
        //
        VSeeKit.setContext(getApplicationContext());

        if(simpleServerConnectionReceiver==null)
            simpleServerConnectionReceiver = new VSeeServerConnection.SimpleVSeeServerConnectionReceiver() {
                @Override
                public void onInitializationComplete() {
                    VSeeVideoManager.instance().setWaitingForCallView(getWaitingMessageView());
                    VSeeServerConnection.instance().setAutoAccept(true);
                    VSeeVideoManager.instance().setConfirmEndCall(true);
                }

                @Override
                public void onNewStatusMessage(String message) {
                    if (message.toLowerCase().contains("password")) {
                        Log.e("VseeMainActivity", "onNewStatusMessage() called. Invalid/Bad password received !! This should never happen !!");
                        stopUpdatingStatus = true;
                    }
                }

                @Override
                public void onLoginStateChange(VSeeServerConnection.LoginState newState) {
                    updateLoginState(newState);
                }
            };

        // For this example, just accept all calls.  VSeeVideoManager calls can only be made after initialization is complete.
        // Also, set Call Join Waiting text message;
        //
        if(simpleVidManagerReceiver == null)
            simpleVidManagerReceiver = new VSeeVideoManager.SimpleVSeeVideoManagerReceiver() {
                @Override
                public void onRemoveRemoteVideoView(String username, boolean isLast) {

                    Log.e("VSeeMainActivity", "onRemoveRemoteVideoView() got called.");

                    if (isLast) {
                        Log.e("VSeeMainActivity", "onRemoveRemoteVideoView() ----- LAST Logout.");
                        // video call is over... end the call activity if it is open
                        VSeeServerConnection.instance().logout();
                        VSeeVideoManager.instance().finishVideoActivity();
                    }
                }

            };

        spinner = (ProgressBar)findViewById(R.id.waitSpinner);

        TextView mesg0 = (TextView) findViewById(R.id.waitTitle);
        mesg0.setText(LocalizationSingleton.getLocalizedString(R.string.temporary_placeholder, "nat_wsc_header_title", this));
        Typeface tf= Typeface.createFromAsset(getAssets(), "HelveticaNeue_extended.ttf");
        mesg0.setTypeface(tf);

        TextView mesg1 = (TextView) findViewById(R.id.waitmesg1);
        mesg1.setText(LocalizationSingleton.getLocalizedString(R.string.temporary_placeholder, "nat_wsc_sit_relax", this));
        TextView mesg2 = (TextView) findViewById(R.id.waitmesg2);
        mesg2.setText(LocalizationSingleton.getLocalizedString(R.string.temporary_placeholder, "nat_wsc_starts_shortly", this));

        TextView blinkingText1 = (TextView)findViewById(R.id.waitingforprovider1);
        blinkingText1.setText(LocalizationSingleton.getLocalizedString(R.string.temporary_placeholder, "nat_wsc_footer", this));
        Animation anim1 = new AlphaAnimation(1.0f, 0.0f);
        anim1.setDuration(BLINKING_PERIOD);      // You can manage the blinking time with this parameter
        anim1.setStartOffset(0);
        anim1.setRepeatMode(Animation.REVERSE);
        anim1.setRepeatCount(Animation.INFINITE);
        blinkingText1.startAnimation(anim1);

        TextView blinkingText2 = (TextView)findViewById(R.id.waitingforprovider2);
        blinkingText2.setText(LocalizationSingleton.getLocalizedString(R.string.temporary_placeholder, "nat_wsc_please_dont_leave", this));
        Animation anim2 = new AlphaAnimation(0.0f, 1.0f);
        anim2.setDuration(BLINKING_PERIOD);      // You can manage the blinking time with this parameter
        anim2.setStartOffset(0);
        anim2.setRepeatMode(Animation.REVERSE);
        anim2.setRepeatCount(Animation.INFINITE);
        blinkingText2.startAnimation(anim2);

        // perform combination of Waiting Room 1 and Waiting Room 2 functionality.
        initializeVideoConsult();

        if(simpleServerConnectionReceiver!=null)
            VSeeServerConnection.instance().addReceiver(simpleServerConnectionReceiver);
        if(simpleVidManagerReceiver!=null)
            VSeeVideoManager.instance().addReceiver(simpleVidManagerReceiver);
    }

    @SuppressLint("InflateParams")
    private View getWaitingMessageView() {

        FrameLayout frame = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.vseewaitingmessage, null);
        TextView waitConnectMesg = (TextView)frame.findViewById(R.id.txtWaitCallConnect);
        waitConnectMesg.setText(LocalizationSingleton.getLocalizedString(R.string.temporary_placeholder, "nat_wait_for_call_connect", this));
        return(frame);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(CONSULTED)   // if user returned to this page after a consultation
        {
            FINISH = true;
			VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
			VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
			simpleServerConnectionReceiver = null;
			simpleVidManagerReceiver = null;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        /*
        if(isServiceRunning(VSeeKitBackgroundService.class))
            stopService(new Intent(this, VSeeKitBackgroundService.class));
        */

        VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
        VSeeVideoManager.instance().removeReceiver(simpleVidManagerReceiver);
		simpleServerConnectionReceiver = null;
		simpleVidManagerReceiver = null;
        super.onDestroy();
    }

    /**
     * Checks to see whether the named service is already running or not
     *
     * @param serviceClass
     * @return TRUE if service is already running, FALSE otherwise
     */
    private boolean isServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the consultation status.
     * Reset the consultation flag if consult was finished/closed
     *
     * @return
     */
    public static boolean getResetConsultStatus()
    {
        if(FINISH)
        {
            FINISH = false;
            CONSULTED = false;
            return (true);
        }

        return(false);
    }

    private void updateLoginState(VSeeServerConnection.LoginState loginState)
    {
        if (stopUpdatingStatus) {
            return;
        }

        // On any change to login state, update the loginState text.
        //
        Log.d("VSeeExample", String.format("VSEE -- Got state change: %s", loginState.toString()));

        switch (loginState) {
            case TRY_LOGIN:

                break;

            case LOGGED_IN:
                Log.d("VSeeExample", "######### VSEE -- Got logged in!!! #########");

                // make sure auto-accept is enabled even for cases of reconnection
                VSeeServerConnection.instance().setAutoAccept(true);
/*
                CONSULTED = true;
                startActivity(VSeeVideoManager.instance().getVideoLaunchIntent());
*/
                // start video session after brief delay (2.3s)
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CONSULTED = true;
                                startActivity(VSeeVideoManager.instance().getVideoLaunchIntent());
                            }
                        });
                    }
                }, 2300);
                break;
        }
    }

    /**
     * Initializes the video consultation.
     * Login process occurs in THREE steps:
     *
     * 1) first obtain the provider's Joining status,
     * 2) then request VSee login credentials
     * 3) then log in to VSee via ephemeral userID/passwd strings
     *
     * This method handles the first, then invokes a method to execute the second,
     * which in turn, invokes another method to effect the third
     *
     */
    private void initializeVideoConsult()
    {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponseProviderStatus(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());

                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(null, VseeMainActivity.this);
                    }
                }
            }};

        VideoConsultServices vidConsultServices = new VideoConsultServices(VseeMainActivity.this, null);
        vidConsultServices.performProviderStatusRequest(responseListener, errorListener);
    }

    /**
     * Fetch VSee login credentials
     */
    private void fetchVideoConsultCredentials()
    {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponseVseeLoginCredentials(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(null, VseeMainActivity.this);
                    }
                }
            }};

        VideoConsultServices vidConsultServices = new VideoConsultServices(VseeMainActivity.this, null);
        vidConsultServices.performVseeCredentialsRequest(responseListener, errorListener);
    }

    /**
     * Perform VSee login using ephemeral credentials
     *
     * @param uid       user name for VSee video session
     * @param pass      password for VSee video session
     */
    private boolean doVideoConsultLogin(String uid, String pass)
    {
        VseeApplication.setCredentials(uid, pass);

        VSeeServerConnection.instance().loginUser(uid, pass);

        if (!VseeApplication.loginCredentialsValid())
        {
            //Toast.makeText(this,"ERROR!\n\n INVALID VSEE CREDENTIALS", Toast.LENGTH_LONG).show();
            return(false);
        }

        // Get feedback about our login state.  Set the initial state and then get updates from
        // VSeeServerConnection.
        //
        updateLoginState(VSeeServerConnection.instance().getLoginState());

        return(true);
    }

    /**
     * 'Successful Response Handler' for video consult Provider Status check.
     * It will trigger a login request into the video consult session
     *
     */
    private void handleSuccessResponseProviderStatus(JSONObject response)
    {
        try {
            Log.d("Response", response.toString());

            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.SAVED_MASTER_USER,response.toString());
            editor.commit();

            // Extract the data from the call
            JsonParser parser = new JsonParser();
            JsonObject responseObj = (JsonObject)parser.parse(response.toString());
            Log.d("provider status ----->", responseObj.toString());

            String providerStatus = responseObj.get("provider_status").getAsString();

            if(providerStatus!=null && "true".equals(providerStatus.toLowerCase()))
            {
                fetchVideoConsultCredentials();
            }
            else
            if(providerStatus!=null && "false".equals(providerStatus.toLowerCase()))    // repeat the web service call
            {
                // wait 30 seconds and repeat the call
                try {
                    Thread.sleep(1500);
                }catch(InterruptedException iex){
                    // ...
                }
                initializeVideoConsult();

            }
            else    // response = "Appointment has expired"
            {

            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 'Successful Response Handler' for VSee credential retrieval
     *
     * @param response
     */
    private void handleSuccessResponseVseeLoginCredentials(JSONObject response)
    {
        try {
            Log.d("Response", response.toString());

            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.SAVED_MASTER_USER,response.toString());
            editor.commit();

            // Extract the data from the call
            JsonParser parser = new JsonParser();
            JsonObject responseObj = (JsonObject)parser.parse(response.toString());
            Log.d("provider status ----->", responseObj.toString());

            String usrname = responseObj.get("username").getAsString();
            String passwd = responseObj.get("password").getAsString();

            if(Utils.isNotEmpty(usrname) && Utils.isNotEmpty(passwd))
            {
                boolean success = doVideoConsultLogin(usrname, passwd);
                if(!success)
                    finish();   // exit this activity if fail
            }
            else{
                // failed to obtain the proper credentials
                finish();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
