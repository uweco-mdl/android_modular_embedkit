package com.mdlive.embedkit.uilayer.appointment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

/**
 * Created by sudha_s on 8/23/2015.
 */
public class MDLiveAppointmentThankYou extends MDLiveBaseActivity {
    private String providerName,consultationDate,Time;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);
        clearMinimizedTime();

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                showHamburgerBell();
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handleIntent();
        getPreferenceValue();
    }

    public void handleIntent(){
        findViewById(R.id.toolbar_tick).setVisibility(View.VISIBLE);
        Intent receivingIntent = getIntent();
        if(receivingIntent != null){
             if(receivingIntent.hasExtra("activitycaller")
                     && receivingIntent.getStringExtra("activitycaller").equals(getString(R.string.mdl_makeAppmtRequest))){
                     findViewById(R.id.appoint_details_view).setVisibility(View.GONE);
                     findViewById(R.id.cencel_info).setVisibility(View.GONE);
                     findViewById(R.id.tick_circle_img).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.infoText)).setText(getString(R.string.mdl_thankkyou_appoint_txt));
            }
        }
    }


    public void showHamburgerBell() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_thankyou_string).toUpperCase());
        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.toolbar_hamburger).setVisibility(View.GONE);
        findViewById(R.id.toolbar_bell).setVisibility(View.GONE);
        findViewById(R.id.toolbar_tick).setVisibility(View.GONE);
    }

    @Override
    public void onRightDrawerClicked(View view) {
        onHomeClicked();
    }

    public void onTickClicked(View view){
        onHomeClicked();
    }

    public void getPreferenceValue()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        providerName = sharedpreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, "");
        ((TextView)findViewById(R.id.provider)).setText(providerName);
        String activityCaller = null;
        if(getIntent().hasExtra("activitycaller")){
            activityCaller  = getIntent().getStringExtra("activitycaller");
        }

        if(activityCaller != null && activityCaller.equalsIgnoreCase(getString(R.string.mdl_makeAppmtRequest))){
            consultationDate = sharedpreferences.getString(PreferenceConstants.IDEAL_DATE, "");
            ((TextView)findViewById(R.id.date)).setText(consultationDate);
            Time = sharedpreferences.getString(PreferenceConstants.NEXT_AVAIL_DATE, "");
            ((TextView)findViewById(R.id.time)).setText(Time);

        }else
        {

            consultationDate = sharedpreferences.getString(PreferenceConstants.SELECTED_DATE, "");
            ((TextView)findViewById(R.id.date)).setText(consultationDate);
            Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
            ((TextView)findViewById(R.id.time)).setText(Time);
        }

    }
}