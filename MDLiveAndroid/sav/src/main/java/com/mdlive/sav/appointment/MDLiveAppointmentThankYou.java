package com.mdlive.sav.appointment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.sav.MDLiveChooseProvider;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sudha_s on 8/23/2015.
 */
public class MDLiveAppointmentThankYou extends MDLiveBaseActivity {
    private String providerName, consultationDate, Time;
    //LinearLayout thankYouLayout, onCallThankYouLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_thankyou_string));

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

        User user = null;
        if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
            user = getIntent().getExtras().getParcelable(User.USER_TAG);
            }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(user), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                commit();
        }

        TextView mdlCancelInstruction = (TextView) findViewById(R.id.mdl_cancellation_instructions);
        Spannable word = new SpannableString(getString(R.string.mdl_cancellation_instructions));
        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.darkblack)), 39, 55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mdlCancelInstruction.setText(word);
        getPreferenceValue();
        handleIntent();
    }
    public void handleIntent() {
        findViewById(R.id.toolbar_tick).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_tick).setContentDescription(getString(R.string.mdl_ada_tick_button));
        Intent receivingIntent = getIntent();
        if (receivingIntent != null) {
            SharedPreferences phoneNumberPref = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            Log.v("Print Doc On Call",receivingIntent.getStringExtra("activitycaller"));


            if(MDLiveChooseProvider.isDoctorOnCall|| MDLiveChooseProvider.isDoctorOnVideo){
                if (receivingIntent.hasExtra("activitycaller") && receivingIntent.getStringExtra("activitycaller").equals("OnCall")) {
                    final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this);
                    findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                    findViewById(R.id.cencel_info).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_on_call_header));
                    ((TextView) findViewById(R.id.txt_summary)).setText(getString(R.string.mdl_Oncall_Summary, MdliveUtils.formatDualString(userBasicInfo.getPersonalInfo().getPhone())));
                } else if (receivingIntent.hasExtra("activitycaller") && receivingIntent.getStringExtra("activitycaller").equals("escalated")) {
                    final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this);
                    findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                    findViewById(R.id.cencel_info).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_escalate_header));
                    ((TextView) findViewById(R.id.txt_summary)).setText(getString(R.string.mdl_escalate_summary));
                    ((TextView) findViewById(R.id.txt_escalate_phone)).setText(getString(R.string.mdl_escalate_phone_text, MdliveUtils.formatDualString(userBasicInfo.getPersonalInfo().getPhone())));
                }
            }else{
                if (getTimeSlotToNowMode() != null && getTimeSlotToNowMode().length() != 0 && getTimeSlotToNowMode().equalsIgnoreCase("Now")
                        && getConsultationType() != null && getConsultationType().equalsIgnoreCase("phone")) {
                    findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                    findViewById(R.id.cencel_info).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_thankkyou_phone_appoint_txt, getProviderDoctorName()));
                } else if (receivingIntent.hasExtra("activitycaller") && receivingIntent.getStringExtra("activitycaller").equals(getString(R.string.mdl_makeAppmtRequest))) {
                    findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                    findViewById(R.id.cencel_info).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_thankkyou_appoint_txt));
                }
            }


           /* if (getTimeSlotToNowMode() != null && getTimeSlotToNowMode().length() != 0 && getTimeSlotToNowMode().equalsIgnoreCase("Now")
                    && getConsultationType() != null && getConsultationType().equalsIgnoreCase("phone")) {
                findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                findViewById(R.id.cencel_info).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_thankkyou_phone_appoint_txt, getProviderDoctorName()));
            } else if (receivingIntent.hasExtra("activitycaller") && receivingIntent.getStringExtra("activitycaller").equals(getString(R.string.mdl_makeAppmtRequest))) {
                findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                findViewById(R.id.cencel_info).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_thankkyou_appoint_txt));
            } else if (receivingIntent.hasExtra("activitycaller") && receivingIntent.getStringExtra("activitycaller").equals("OnCall")) {
                final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this);
                findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                findViewById(R.id.cencel_info).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_on_call_header));
                ((TextView) findViewById(R.id.txt_summary)).setText(getString(R.string.mdl_Oncall_Summary, MdliveUtils.formatDualString(userBasicInfo.getPersonalInfo().getPhone())));
            } else if (receivingIntent.hasExtra("activitycaller") && receivingIntent.getStringExtra("activitycaller").equals("escalated")) {
                final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this);
                findViewById(R.id.onCallThankyouLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.thankyouLayout).setVisibility(View.GONE);
                findViewById(R.id.cencel_info).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txtThanksMsg)).setText(getString(R.string.mdl_escalate_header));
                ((TextView) findViewById(R.id.txt_summary)).setText(getString(R.string.mdl_escalate_summary));
                ((TextView) findViewById(R.id.txt_escalate_phone)).setText(getString(R.string.mdl_escalate_phone_text, MdliveUtils.formatDualString(userBasicInfo.getPersonalInfo().getPhone())));
            }*/
            //This is only for Phone
//            else
//            {
//                findViewById(R.id.onCallThankyouLayout).setVisibility(View.GONE);
//                findViewById(R.id.appoint_details_view).setVisibility(View.GONE);
//                findViewById(R.id.cencel_info).setVisibility(View.GONE);
//                findViewById(R.id.tick_circle_img).setVisibility(View.GONE);
//                ((TextView)findViewById(R.id.infoText)).setText(getString(R.string.mdl_thankkyou_appoint_txt));
//            }
        }
    }


    private String getProviderDoctorName() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, null);
    }


    private String getTimeSlotToNowMode() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, null);
    }

    private String getConsultationType() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(PreferenceConstants.CONSULTATION_TYPE, null);
    }

    public void showHamburgerBell() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_thankyou_string).toUpperCase());
        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.toolbar_hamburger).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_bell).setVisibility(View.GONE);
        findViewById(R.id.toolbar_tick).setVisibility(View.GONE);
    }

    @Override
    public void onRightDrawerClicked(View view) {
        onHomeClicked();
    }

    public void onTickClicked(View view) {
        onHomeClicked();
    }

    public void getPreferenceValue() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        providerName = sharedpreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, "");
        ((TextView) findViewById(R.id.provider)).setText("Provider: " + providerName);
        String activityCaller = null;
        if (getIntent().hasExtra("activitycaller")) {
            activityCaller = getIntent().getStringExtra("activitycaller");
        }

        if (activityCaller != null && activityCaller.equalsIgnoreCase(getString(R.string.mdl_makeAppmtRequest))) {
            consultationDate = sharedpreferences.getString(PreferenceConstants.IDEAL_DATE, "");
            ((TextView) findViewById(R.id.date)).setText("Date: " + consultationDate);
            Time = sharedpreferences.getString(PreferenceConstants.NEXT_AVAIL_DATE, "");
            String timeZoneValue = "";
            if (UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this).getPersonalInfo() != null) {
                timeZoneValue = UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this).getPersonalInfo().getTimezone();
            }
            ((TextView) findViewById(R.id.time)).setText("Time: " + Time);

        } else {

            try {
                consultationDate = sharedpreferences.getString(PreferenceConstants.SELECTED_DATE, "");

                Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
                String timeZoneValue = "";
                String Timestamp = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESTAMP, "");
                if (UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this).getPersonalInfo() != null) {
                    timeZoneValue = UserBasicInfo.readFromSharedPreference(MDLiveAppointmentThankYou.this).getPersonalInfo().getTimezone();
                }
                Calendar calendar;
                calendar = TimeZoneUtils.getCalendarWithOffset(this);
                SimpleDateFormat sdfNow = new SimpleDateFormat("MMM dd, yyyy");
                sdfNow.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
                Date dateNow = calendar.getTime();
//                ((TextView) findViewById(R.id.txtDate)).setText(format);
                if(!Timestamp.equals("0")) {
                    calendar.setTimeInMillis(Long.parseLong(Timestamp) * 1000);
                    final Date dateTime = calendar.getTime();
    //                ((TextView) findViewById(R.id.txtDate)).setText(format);
    //                ((TextView) findViewById(R.id.time)).setText("Time: " +sdfNow.format(dateTime)+ " " + timeZoneValue);
                    ((TextView) findViewById(R.id.date)).setText("Date: " + sdfNow.format(dateTime));
                    ((TextView) findViewById(R.id.time)).setText("Time: " + Time + " " + timeZoneValue);
                }else {
                    ((TextView) findViewById(R.id.time)).setText("Time: " + Time + " " + timeZoneValue);
                    ((TextView) findViewById(R.id.date)).setText("Date: " + consultationDate);
                }

                if(MDLiveChooseProvider.isDoctorOnCall) {
                    SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a z");
                    ((TextView) findViewById(R.id.time)).setText("Time: " + sdfTime.format(dateNow));
                    ((TextView) findViewById(R.id.date)).setText("Date: " + sdfNow.format(dateNow));
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }
}