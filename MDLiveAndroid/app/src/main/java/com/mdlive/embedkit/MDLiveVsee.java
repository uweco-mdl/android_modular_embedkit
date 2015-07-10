package com.mdlive.embedkit;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.WaitingRoom.MDLiveWaitingRoom;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.vsee.kit.VSeeKit;
import com.vsee.kit.VSeeServerConnection;
import com.vsee.kit.VSeeVideoManager;

import java.util.Timer;
import java.util.TimerTask;

public class MDLiveVsee extends MDLiveBaseActivity
{
    private boolean stopUpdatingStatus = false;
    private static boolean CONSULTED = false,
            FINISH = false;

    private static VSeeServerConnection.SimpleVSeeServerConnectionReceiver simpleServerConnectionReceiver = null;
    private static VSeeVideoManager.SimpleVSeeVideoManagerReceiver simpleVidManagerReceiver = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_vsee);

        /**
         * This function will handles home button click listener
         */
        ImageView homeImage= (ImageView) findViewById(R.id.homeImg);
        homeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.movetohome(MDLiveVsee.this, getString(R.string.home_dialog_title), getString(R.string.home_dialog_text));
            }
        });

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

        /** For this example, just accept all calls.  VSeeVideoManager calls can only be made
        *   after initialization is complete.
        *
        * Also, set Call Join Waiting text message;
        */
        if(simpleVidManagerReceiver == null)
            simpleVidManagerReceiver = new VSeeVideoManager.SimpleVSeeVideoManagerReceiver() {
                @Override
                public void onRemoveRemoteVideoView(String username, boolean isLast) {

                    Log.e("VSeeMainActivity", "onRemoveRemoteVideoView() got called.");

                    if (isLast) {
                        // video call is over... end the call activity if it is open
                        VSeeServerConnection.instance().logout();
                        VSeeVideoManager.instance().finishVideoActivity();
                        Intent i = new Intent(MDLiveVsee.this, MDLiveWaitingRoom.class);
                        i.putExtra("isReturning", true);
                        startActivity(i);
                        finish();
                    }
                }

            };

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uid = extras.getString("username");
            String pass = extras.getString("password");
            MDLiveVseeApplication.setCredentials(uid, pass);
            VSeeServerConnection.instance().loginUser(uid, pass);
        }

        if (!MDLiveVseeApplication.loginCredentialsValid())
        {
            Toast.makeText(this, "ERROR!\n\n INVALID VSEE CREDENTIALS", Toast.LENGTH_LONG).show();
            return;
        }
        updateLoginState(VSeeServerConnection.instance().getLoginState());


        if(simpleServerConnectionReceiver!=null)
            VSeeServerConnection.instance().addReceiver(simpleServerConnectionReceiver);
        if(simpleVidManagerReceiver!=null)
            VSeeVideoManager.instance().addReceiver(simpleVidManagerReceiver);
    }

    @SuppressLint("InflateParams")
    private View getWaitingMessageView() {

        FrameLayout frame = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.mdlive_vseewaitingmessage, null);
        TextView waitConnectMesg = (TextView)frame.findViewById(R.id.txtWaitCallConnect);
        waitConnectMesg.setText("Waiting for call to connect");
        return(frame);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(CONSULTED && !isFinishing())   // if user returned to this page after a consultation
        {
            FINISH = true;
            VSeeVideoManager.instance().finishVideoActivity();
			VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
            VSeeVideoManager.instance().removeReceiver(simpleVidManagerReceiver);
			simpleServerConnectionReceiver = null;
			simpleVidManagerReceiver = null;
            Intent i = new Intent(MDLiveVsee.this, MDLiveWaitingRoom.class);
            i.putExtra("isReturning", true);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onDestroy() {

        try{
            VSeeVideoManager.instance().endVideoCalls();
            VSeeVideoManager.instance().finishVideoActivity();
        } catch(Exception e){
            e.printStackTrace();
        }
        VSeeServerConnection.instance().removeReceiver(simpleServerConnectionReceiver);
        VSeeVideoManager.instance().removeReceiver(simpleVidManagerReceiver);
        simpleServerConnectionReceiver = null;
        simpleVidManagerReceiver = null;
        super.onDestroy();
    }

    /**
     * checks to see whether the named service is already running or not
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

        switch (loginState) {
            case TRY_LOGIN:

                break;

            case LOGGED_IN:

                // make sure auto-accept is enabled even for cases of reconnection
                VSeeServerConnection.instance().setAutoAccept(true);

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


    @Override
    public void onBackPressed() {
    }
}
