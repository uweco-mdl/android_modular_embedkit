package com.mdlive.mobile.uilayer;

import android.os.Bundle;

import com.mdlive.mobile.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.parentclasses.activity.UMWSplashSceen;
import com.mdlive.mobile.R;

/**
 * Created by unnikrishnan_b on 4/7/2015.
 */
public class MDLiveSplashScreen extends UMWSplashSceen {

    /**
     *
     * Called when the activity is created.
     *
     * The config data is shared to common module by calling MDLiveConfig.setData() method.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.splashscreen);
        MDLiveConfig.setData();
        setData(MDLiveLogin.class);
        super.onCreate(savedInstanceState);
    }
}
