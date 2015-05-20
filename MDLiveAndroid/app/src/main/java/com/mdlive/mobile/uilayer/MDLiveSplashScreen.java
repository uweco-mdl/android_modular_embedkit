package com.mdlive.mobile.uilayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.mdlive.mobile.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;


/**
 * Created by unnikrishnan_b on 4/7/2015.
 */

public class MDLiveSplashScreen extends Activity
{
    private Handler mHandler;
    private Runnable mRunnable;
    private static final int SPLASH_TIME_OUT = 5000;

    /**
     *
     * The config data is shared to common module by calling MDLiveConfig.setData() method.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MDLiveConfig.setData();
        setLocalisationData();
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MDLiveSplashScreen.this, MDLiveLogin.class);
                startActivity(intent);
                finish();

            }
        };

        // set default lang
        LocalizationSingleton.setLanguage(this, "en");

        // if this is an *update* install, then clear the language shared prefs to prevent
        // old language shared prefs(from previous installation) from being applied
        LocalizationSingleton.purgeLangPrefsIfInstallUpdated(this);

    }

    /**
     *
     * The language preferences is setup here.
     *
     * NOTE : Only a template, need to have the implementation.
     *
     */
    private void setLocalisationData(){
        // TODO : Implementation yet to be done.
    }

    @Override
    public void onBackPressed() {
// Remove callback on back press
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
// Remove callback on pause
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
// Attach and start callback with delay on resume
        if (mHandler != null && mRunnable != null) {
            mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
        }
        super.onResume();
    }

}


