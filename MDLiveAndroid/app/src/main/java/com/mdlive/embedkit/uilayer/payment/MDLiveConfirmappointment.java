package com.mdlive.embedkit.uilayer.payment;

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
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.sav.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sudha_s on 8/22/2015.
 */
public class MDLiveConfirmappointment extends MDLiveBaseActivity {
    private String providerName,providerType,consultationType,consultationDate,Time,phone,doctorEVisit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);
        getPreferenceValue();

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.confirm_appointment_txt).toUpperCase());



        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveConfirmappointment.this);
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveConfirmappointment.this);
    }

    public void rightBtnOnClick(View v) {
        Intent intent = new Intent(MDLiveConfirmappointment.this, MDLiveStartVisit.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);

    }


    public void getPreferenceValue()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        providerName = sharedpreferences.getString(PreferenceConstants.PATIENT_NAME, "");
        ((TextView)findViewById(R.id.txtProfileName)).setText("Dr. "+providerName);
        providerType = sharedpreferences.getString(PreferenceConstants.PROVIDER_TYPE, "");
        ((TextView)findViewById(R.id.txtproviderType)).setText(providerType);
        consultationType = sharedpreferences.getString(PreferenceConstants.ACCESS_MODE, "");
        ((TextView)findViewById(R.id.txtConsultationtype)).setText(consultationType+" Consultation");
        consultationDate = sharedpreferences.getString(PreferenceConstants.SELECTED_DATE, "");
        Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");


        if(!consultationDate.isEmpty()&&!Time.isEmpty())
        {
            ((TextView)findViewById(R.id.txtDate)).setText(consultationDate);
            ((TextView)findViewById(R.id.txtTime)).setText(Time);
        }else
        {
            Calendar calendar = Calendar.getInstance();
            String format = new SimpleDateFormat("E, MMM d, yyyy").format(calendar.getTime());
            ((TextView)findViewById(R.id.txtDate)).setText(format);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm a");
            String  currentTime = df.format(calendar.getTime());
            ((TextView)findViewById(R.id.txtTime)).setText(currentTime);
        }

        phone = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        formatDualString(phone);
        doctorEVisit = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        SharedPreferences amountPreferences =this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.amountInDollar)).setText(getString(R.string.dollar)+amountPreferences.getString(PreferenceConstants.AMOUNT,"0.00"));
        String str_ProfileImg= sharedpreferences.getString(PreferenceConstants.PROVIDER_PROFILE, "");
        ((CircularNetworkImageView)findViewById(R.id.imgProfilePic)).setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));
    }
    public void formatDualString(String formatText){
        boolean hasParenthesis = false;
        if(formatText.indexOf(")") > 0){
            hasParenthesis = true;
        }
        formatText= formatText.replace("(", "");
        formatText= formatText.replace(")", "");
        formatText= formatText.replace(" ", "");
        if(formatText.length() > 10){
            formatText = formatText.substring(0, formatText.length()-1);
        }
        if(formatText.length() >= 7){
            formatText = "("+formatText.substring(0, 3)+") "+formatText.substring(3, 6)+formatText.substring(6, formatText.length());
        }else if(formatText.length() >= 4){
            formatText = "("+formatText.substring(0, 3)+") "+formatText.substring(3, formatText.length());
        }else if(formatText.length() == 3 && hasParenthesis){
            formatText = "("+formatText.substring(0, formatText.length())+")";
        }
        ((TextView)findViewById(R.id.phoneNumber)).setText(formatText);

    }
}
