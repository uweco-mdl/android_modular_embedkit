package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;

/**
 * Created by venkataraman_r on 7/7/2015.
 */

public class SplashScreenActivity extends Activity
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
        setContentView(R.layout.mdlive_splashscreen);

        // Pass 1 for Dev env
        // Pass 2 for Dev env
        MDLiveConfig.setData(3);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
    }
}


