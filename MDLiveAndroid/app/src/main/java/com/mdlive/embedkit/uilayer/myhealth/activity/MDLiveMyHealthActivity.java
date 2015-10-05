package com.mdlive.embedkit.uilayer.myhealth.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.mdliveassist.MDLiveAssistFragment;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

/**
 * Created by sanjibkumar_p on 7/28/2015.
 */
public class MDLiveMyHealthActivity extends AppCompatActivity implements FragmentTabHost.OnTabChangeListener {
    private static final String TAG = "MY_HEALTH";

    private static final String MEDICALHISTORY = "MedicalHistory";
    private static final String PHARMACY = "pharmacy";
    private static final String PROVIDERS = "Providers";
    private static final String VISITS = "Visits";

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_my_health_activity);
        clearMinimizedTime();

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().add(R.id.mdlive_my_health_activity, MDLiveMyHealthListFragment.newInstance(), TAG).commit();
//        }

        final FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        if (tabHost != null) {
            tabHost.setup(this, getSupportFragmentManager(), R.id.mdlive_my_health_activity);

            tabHost.addTab(createTabSpec(tabHost.newTabSpec(MEDICALHISTORY), MEDICALHISTORY, R.drawable.icon_i), MDLiveMyHealthListFragment.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(PHARMACY), PHARMACY, R.drawable.icon_i), MDLiveAssistFragment.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(PROVIDERS), PROVIDERS, R.drawable.icon_i), MDLiveAssistFragment.class, null);
            tabHost.addTab(createTabSpec(tabHost.newTabSpec(VISITS), VISITS, R.drawable.icon_i), MDLiveAssistFragment.class, null);

            tabHost.setOnTabChangedListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onTabChanged(String s) {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }
    private TabHost.TabSpec createTabSpec(final TabHost.TabSpec spec, final String text, @DrawableRes final int imageResourceId) {
        final View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.myaccounts_tab_indicator, null);

        final TextView tv = (TextView) v.findViewById(R.id.TabTextView);
        final ImageView img = (ImageView) v.findViewById(R.id.TabImageView);

        tv.setText(text);
        img.setBackgroundResource(imageResourceId);

        return spec.setIndicator(v);
    }

    public void clearMinimizedTime() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Log.d("Timer", "clear called");
            }
        }, 1000);
    }
}
