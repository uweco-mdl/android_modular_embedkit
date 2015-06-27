package com.mdlive.embedkit.global;

/**
 * Created by sampath_k on 27/06/15.
 * Intention : To have this session management logic as global for all the activities
 * Core Logic OverView : Will store the application pause time when it goes to background and validates
 *      the session when user comes bak into application.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Date;

public class SessionTimeOutManagement {
    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MDLiveEmbedKit";
    // All Shared Preferences Keys
    public static final String PAUSE_TIME = "app_pause_time";
    // Default session timeout value
    public static long TimeOutMilliSec = 1800000;
    public static Handler SessionHandler = null;

    /**
     * @param context : Application context or activity context
     *                With the help of application context this will declare the preference object that referred for editing purposes later.
     */
    public SessionTimeOutManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * When the screen goes background this function will have the current date and time stored in shared preference.
     * Needs to be called in Activities onPause()
     */
    public void setPosueTime(){
        editor.putLong(PAUSE_TIME, new Date().getTime());
        editor.commit();
    }

    /**
     * Assuming that WAG will provide handler to MDLive to pass the session expiry back to WAG
     *
     * Get the pause time from preference and check against current time if difference is more than expected session time
     *      then handle the session exipry back to caller app(WAG)
     * Needs to be called in Activities onResume()
     */
    public void validateSession(){
        if(SessionHandler != null && pref.getLong(PAUSE_TIME, 0) != 0){
            long pauseTime = pref.getLong(PAUSE_TIME, new Date().getTime());
            long resumeTime = new Date().getTime();
            if (resumeTime - pauseTime >= SessionTimeOutManagement.TimeOutMilliSec){
                // Send the error or session expiry info back the the caller application (WAG)
                Message message = new Message();
                Bundle timeout = new Bundle();
                timeout.putString("timeOut", "Session expiryed");
                message.setData(timeout);
                SessionHandler.sendMessage(message);
            }
        }
    }

    /**
     * Clear the preference values so that we will not have duplicate/issues in the preference storage.
     * Needs to be called in Activities onResume() after doing validateSession()
     */
    public void clearSession(){
        editor.clear();
        editor.commit();
    }
}
