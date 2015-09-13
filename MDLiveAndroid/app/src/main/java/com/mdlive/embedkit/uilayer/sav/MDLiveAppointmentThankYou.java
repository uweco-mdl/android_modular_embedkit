package com.mdlive.embedkit.uilayer.sav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by sudha_s on 8/23/2015.
 */
public class MDLiveAppointmentThankYou extends MDLiveBaseActivity {
    private String providerName,providerType,consultationType,consultationDate,Time,phone,doctorEVisit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.top_tick_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_thankyou_string).toUpperCase());


        getPreferenceValue();
    }


    @Override
    public void onBackPressed() {

    }

    public void rightBtnOnClick(View view){
        Intent Reasonintent = new Intent(MDLiveAppointmentThankYou.this,MDLiveDashboardActivity.class);
        Reasonintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Reasonintent);
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveAppointmentThankYou.this);
        onBackPressed();
    }


    public void getPreferenceValue()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        providerName = sharedpreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, "");
        ((TextView)findViewById(R.id.provider)).setText(providerName);
        providerType = sharedpreferences.getString(PreferenceConstants.PROVIDER_TYPE, "");
        consultationType = sharedpreferences.getString(PreferenceConstants.CONSULTATION_TYPE, "");

        phone = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        doctorEVisit = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        String activityCaller = null;
        if(getIntent().hasExtra("activitycaller")){
            activityCaller  = getIntent().getStringExtra("activitycaller");
        }

        if(activityCaller != null && activityCaller.equals(getString(R.string.mdl_makeAppmtRequest))){
            consultationDate = sharedpreferences.getString(PreferenceConstants.SELECTED_DATE, "");
            ((TextView)findViewById(R.id.date)).setText(consultationDate);
            Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
            ((TextView)findViewById(R.id.time)).setText(Time);
        }else
        {
            consultationDate = sharedpreferences.getString(PreferenceConstants.IDEAL_DATE, "");
            ((TextView)findViewById(R.id.date)).setText(consultationDate);
            Time = sharedpreferences.getString(PreferenceConstants.NEXT_AVAIL_DATE, "");
            ((TextView)findViewById(R.id.time)).setText(Time);
        }

    }
}