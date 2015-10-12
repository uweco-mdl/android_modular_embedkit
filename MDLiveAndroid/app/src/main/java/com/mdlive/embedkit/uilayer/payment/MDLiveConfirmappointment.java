package com.mdlive.embedkit.uilayer.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.sav.CircularNetworkImageView;
import com.mdlive.embedkit.uilayer.sav.MDLiveAppointmentThankYou;
import com.mdlive.embedkit.uilayer.sav.MDLiveChooseProvider;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by sudha_s on 8/22/2015.
 */
public class MDLiveConfirmappointment extends MDLiveBaseActivity {
    private String providerName, providerType, consultationType, consultationDate, Time, TimeStamp, phone, doctorEVisit;
    private String promoCode = null;
    private String appointmentMethodType;
    private String timeStamp, phys_ID;
    private boolean CheckdoconfirmAppmt = false;
    private static String errorPhoneNumber=null;
    private boolean CheckTermsConsent = false;
    private boolean CheckPrivacyPolicy = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);
        clearMinimizedTime();
        getPreferenceValue();

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_confirm_appointment_txt).toUpperCase());


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


        TextView mdlTermsConsent = (TextView) findViewById(R.id.mdl_terms_consent);
        String str_termsConsent = "I certify that I have read and accept the terms of <a href='https://www.mdlive.com/consumer/informed_consent_medicalgroup.html' style=\"text-decoration:none; color:#257cfc;\">MDLIVE Medical Group\'s Informed Consent</a>. ";

        Spannable underLinedString = (Spannable) Html.fromHtml(str_termsConsent);
        for (URLSpan u: underLinedString.getSpans(0, underLinedString.length(), URLSpan.class)) {
            underLinedString.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, underLinedString.getSpanStart(u), underLinedString.getSpanEnd(u), 0);
        }
        mdlTermsConsent.setText(underLinedString);
        mdlTermsConsent.setMovementMethod(LinkMovementMethod.getInstance());


        final CheckBox ConsentCheckbox = (CheckBox) findViewById(R.id.mdl_terms_consent_checkbox);
        final CheckBox PrivacyPolicy = (CheckBox) findViewById(R.id.mdl_privacy_policy_checkbox);

        // Set Checkbox values & check changed listener
        ConsentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckTermsConsent = (isChecked) ? true : false;
                enableConfirmAppt();
            }
        });
        PrivacyPolicy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheckbox) {
                CheckPrivacyPolicy = (isCheckbox) ? true : false;
                enableConfirmAppt();
            }
        });

        TextView mdlPrivacyPolicy = (TextView) findViewById(R.id.mdl_privacy_policy);

        String str_privacy_policy = "I have read MDLIVE Medical Group's <a href='https://www.mdlive.com/consumer/privacy_medicalgroup.html' style=\"text-decoration:none; color:#257cfc;\">Privacy Policy </a> and I acknowledge that I have the ability to print a hard copy of the Privacy Policy for my records.";

        Spannable underLinedStringPolicy = (Spannable) Html.fromHtml(str_privacy_policy);
        for (URLSpan u: underLinedStringPolicy.getSpans(0, underLinedStringPolicy.length(), URLSpan.class)) {
        underLinedStringPolicy.setSpan(new UnderlineSpan() {
            public void updateDrawState(TextPaint tP) {
                tP.setUnderlineText(false);
            }
        }, underLinedStringPolicy.getSpanStart(u), underLinedStringPolicy.getSpanEnd(u), 0);
        }
        mdlPrivacyPolicy.setText(underLinedStringPolicy);
        mdlPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void enableConfirmAppt(){
        if(CheckTermsConsent && CheckPrivacyPolicy){
            ((Button) findViewById(R.id.start_visit)).setVisibility(View.VISIBLE);
        }else{
            ((Button) findViewById(R.id.start_visit)).setVisibility(View.GONE);
        }
    }

    //do confirm appointment service
    private void doConfirmAppointment() {
        showProgress();
        try {
            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("confirm appmt res---->", response.toString());
                    hideProgress();
                    try {
                        String apptId = response.getString("appointment_id");
                        if (apptId != null) {
                            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(PreferenceConstants.APPT_ID, apptId);
                            editor.commit();
                            if (consultationType.equalsIgnoreCase("phone")) {
                                movetothankyou();
                            } else if (!Time.equalsIgnoreCase("Now")) {

                                movetothankyou();
                            } else {
                                movetostartVisit();
                            }

                        } else {
                            final String resumeScreen = response.getString("resume_screen");

                            MdliveUtils.showDialog(MDLiveConfirmappointment.this, response.getString("message"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (resumeScreen.equalsIgnoreCase("Get Started")) {
                                        Intent getStartedIntent = new Intent(MDLiveConfirmappointment.this, MDLiveGetStarted.class);
                                        startActivity(getStartedIntent);
                                        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
                                        finish();
                                    }
                                }
                            });

                        }

                    } catch (Exception e) {
                        hideProgress();
                        e.printStackTrace();
                    }
                }
            };

            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Log.e("Error Response", error.toString());
                        dismissDialog();
                        NetworkResponse errorResponse = error.networkResponse;
                        if (error.getClass().equals(TimeoutError.class)) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            };
                            // Show timeout error message
                            MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveConfirmappointment.this);
                        }else if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("responseBody",responseBody);
                            JSONObject errorObj = new JSONObject(responseBody);
                            if (errorObj.has("message")) {
                                if(errorObj.has("phone")){
                                    errorPhoneNumber=errorObj.getString("phone");
                                }else{
                                    errorPhoneNumber=null;
                                }
                                showAlertPopup(errorObj.getString("message"));
                            } else if (errorObj.has("error")) {
                                if(errorObj.has("phone")){
                                    errorPhoneNumber=errorObj.getString("phone");
                                }else{
                                    errorPhoneNumber=null;
                                }
                                showAlertPopup(errorObj.getString("error"));
                            }

                        }else{
                            MdliveUtils.handelVolleyErrorResponse(MDLiveConfirmappointment.this, error, getProgressDialog());
                        }

                    } catch (Exception e) {
                        dismissDialog();
                        e.printStackTrace();
                    }
                }
            };

            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
            HashMap<String, Object> params = new HashMap<String, Object>();
            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
            params.put("appointment_method", appointmentMethodType);
            params.put("alternate_visit_option", "No Answer");

            if(phys_ID==null)
            {
                params.put("phys_availability_id", "");
            }else
            {
                params.put("phys_availability_id", phys_ID);
            }
            String TimeStamp = settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "");
            if(TimeStamp == null ||
                    TimeStamp.trim().length() == 0 ||
                    settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "").equalsIgnoreCase("Now"))
            {
                params.put("timeslot","Now");
            }else if(settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "").equalsIgnoreCase("0"))
            {
                params.put("timeslot","Now");
            }else {
                params.put("timeslot", Long.parseLong(settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "")));
            }
            params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
            params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON, "Not Sure"));
            params.put("customer_call_in_number", MdliveUtils.getSpecialCaseRemovedNumber(settings.getString(PreferenceConstants.PHONE_NUMBER, "")));

            params.put("do_you_have_primary_care_physician", settings.getString(PreferenceConstants.PRIMARY_PHYSICIAN_STATUS, "No"));
            params.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
            SharedPreferences promocodePreferences = this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
            params.put("promocode", promocodePreferences.getString(PreferenceConstants.OFFER_CODE, ""));
            Gson gson = new GsonBuilder().serializeNulls().create();
            ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLiveConfirmappointment.this, null);
            services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    public void leftBtnOnClick(View v) {
        MdliveUtils.hideSoftKeyboard(MDLiveConfirmappointment.this);
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveConfirmappointment.this);
    }

    public void rightBtnOnClick(View v) {
        if (CheckdoconfirmAppmt) {
            if(MDLiveChooseProvider.isDoctorOnCall){
                doOnCallConsultaion();
            }else if(MDLiveChooseProvider.isDoctorOnVideo){
                doOnVideoConsultaion();
            }else{
                doConfirmAppointment();
            }

        }

    }

    private void movetostartVisit() {
        Intent intent = new Intent(MDLiveConfirmappointment.this, MDLiveStartVisit.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
    }

    private void movetothankyou() {
        Intent intent = new Intent(MDLiveConfirmappointment.this, MDLiveAppointmentThankYou.class);
        intent.putExtra("activitycaller", getString(R.string.mdl_appointment_details));
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
    }


    public void getPreferenceValue() {
        SharedPreferences sharedpreferences = null;
        try {
            sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            CheckdoconfirmAppmt = sharedpreferences.getBoolean(PreferenceConstants.EXISTING_CARD_CHECK, true);
            providerName = sharedpreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, "");
            ((TextView) findViewById(R.id.txtProfileName)).setText(providerName);
            providerType = sharedpreferences.getString(PreferenceConstants.PROVIDER_MODE, "");
            ((TextView) findViewById(R.id.txtproviderType)).setText(providerType);
            consultationType = sharedpreferences.getString(PreferenceConstants.CONSULTATION_TYPE, "");
            String consultationTypeStr = getString(R.string.mdl_title_video_home);
            if (consultationType.equalsIgnoreCase("phone")){
                consultationTypeStr = getString(R.string.mdl_phone_consultation);
                findViewById(R.id.PhoneToCallNumberLayout).setVisibility(View.VISIBLE);
            }
            ((TextView) findViewById(R.id.txtConsultationtype)).setText(consultationTypeStr);
            consultationDate = sharedpreferences.getString(PreferenceConstants.SELECTED_DATE, "");
            Time = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
            TimeStamp = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESTAMP, "");
            phys_ID = sharedpreferences.getString(PreferenceConstants.SELECTED_PHYSID, "");
            if (consultationType.equalsIgnoreCase("Video")) {
                appointmentMethodType = "1";
            } else if (consultationType.equalsIgnoreCase("Phone")) {
                appointmentMethodType = "2";
                Log.e("Phone", "Am in Phone");
            } else {
                appointmentMethodType = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("Step 2", "11111");

        //If Provider name is equal to Doctor on call Time field visibility should be gone.
        try {
            if(providerName.equalsIgnoreCase("Doctor On Call")){
                ((TextView) findViewById(R.id.txtTime)).setVisibility(View.INVISIBLE);
                Calendar calendar = TimeZoneUtils.getCalendarWithOffset(this);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d");
                sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
                String format = sdf.format(calendar.getTime());
                ((TextView) findViewById(R.id.txtDate)).setText(format);
                Log.e("Step 2 A", "11111");
            }else if (!consultationDate.isEmpty() && !Time.isEmpty()) {
                ((TextView) findViewById(R.id.txtDate)).setText(consultationDate);
                ((TextView) findViewById(R.id.txtTime)).setText(Time);
                Log.e("Step 2 B", "11111");
            } else {
                Calendar calendar = TimeZoneUtils.getCalendarWithOffset(this);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d");
                sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
                String format = sdf.format(calendar.getTime());
                ((TextView) findViewById(R.id.txtDate)).setText(format);
                SimpleDateFormat df = new SimpleDateFormat("HH:mm  a");
                String currentTime = df.format(calendar.getTime());
                ((TextView) findViewById(R.id.txtTime)).setText(currentTime);
                Log.e("Step 2 C", "11111");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //This is for Now Scenario
        phone = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        formatDualString(phone);
        doctorEVisit = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        SharedPreferences amountPreferences = this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.amountInDollar)).setText(getString(R.string.mdl_dollar) + amountPreferences.getString(PreferenceConstants.AMOUNT, "0.00"));
        String str_ProfileImg = sharedpreferences.getString(PreferenceConstants.PROVIDER_PROFILE, "");
        ((CircularNetworkImageView) findViewById(R.id.imgProfilePic)).setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));
    }

    public void formatDualString(String formatText) {
        boolean hasParenthesis = false;
        if (formatText.indexOf(")") > 0) {
            hasParenthesis = true;
        }
        formatText = formatText.replace("(", "");
        formatText = formatText.replace(")", "");
        formatText = formatText.replace(" ", "");
        if (formatText.length() > 10) {
            formatText = formatText.substring(0, formatText.length());
        }
        if (formatText.length() >= 7) {
            formatText = "(" + formatText.substring(0, 3) + ") " + formatText.substring(3, 6) + formatText.substring(6, formatText.length());
        } else if (formatText.length() >= 4) {
            formatText = "(" + formatText.substring(0, 3) + ") " + formatText.substring(3, formatText.length());
        } else if (formatText.length() == 3 && hasParenthesis) {
            formatText = "(" + formatText.substring(0, formatText.length()) + ")";
        }
        ((TextView) findViewById(R.id.phoneNumber)).setText(MdliveUtils.formatDualString(formatText));

    }



    /***
     * This method will be called when doctor on call by Video is available
     * On successful response it will return an appointment ID which will saved in shared Preference for future use.
     * On Error respose the corresponding message will be notified to the user.
     */


    private void doOnVideoConsultaion() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissDialog();
                try {
                    Log.e("Response",response.toString());
                    if(response.has("id")){
                        String callConsultationId=response.getString("id");
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.APPT_ID, callConsultationId);
                        editor.commit();
                        movetostartVisit();
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("Error Response", error.toString());
                    dismissDialog();
                    NetworkResponse errorResponse = error.networkResponse;
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveConfirmappointment.this);
                    }else if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.e("responseBody",responseBody);
                        JSONObject errorObj = new JSONObject(responseBody);
                        if (errorObj.has("message")) {
                            if(errorObj.has("phone")){
                                errorPhoneNumber=errorObj.getString("phone");
                            }else{
                                errorPhoneNumber=null;
                            }
                            showAlertPopup(errorObj.getString("message"));
                        } else if (errorObj.has("error")) {
                            if(errorObj.has("phone")){
                                errorPhoneNumber=errorObj.getString("phone");
                            }else{
                                errorPhoneNumber=null;
                            }
                            showAlertPopup(errorObj.getString("error"));
                        }

                    }else{
                        MdliveUtils.handelVolleyErrorResponse(MDLiveConfirmappointment.this, error, null);
                    }

                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };

        SharedPreferences settings =   getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
        HashMap<String, HashMap<String, Object>> onCallParams=new HashMap<>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("consultation_method", "Video");
        params.put("physician_type", settings.getString(PreferenceConstants.PROVIDERTYPE_ID,"3"));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON, "Not Sure"));
        params.put("call_in_number", MdliveUtils.getSpecialCaseRemovedNumber(settings.getString(PreferenceConstants.PHONE_NUMBER, "")));
        params.put("do_you_have_primary_care_physician", "No");
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));

        onCallParams.put("user", params);

        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLiveConfirmappointment.this, null);
        services.doOnCallAppointment(gson.toJson(onCallParams), responseListener, errorListener);
    }



    /***
     * This method will be called when doctor on call by Phone is available
     * On Success response it will be taken to the thank you screen.
     */


    private void doOnCallConsultaion() {
        showProgressDialog();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissDialog();
                try {
                    Log.e("Response",response.toString());
                    if(response.has("message")){
                        Intent thankYouIntent=new Intent(MDLiveConfirmappointment.this, MDLiveAppointmentThankYou.class);
                        thankYouIntent.putExtra("activitycaller","OnCall");
                        startActivity(thankYouIntent);
                        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
                    }

                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("Error Response", error.toString());
                    NetworkResponse errorResponse = error.networkResponse;
                    dismissDialog();
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveConfirmappointment.this);
                    }else if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.e("responseBody",responseBody);
                        JSONObject errorObj = new JSONObject(responseBody);
                        if (errorObj.has("message")) {
                            if(errorObj.has("phone")){
                                errorPhoneNumber=errorObj.getString("phone");
                            }else{
                                errorPhoneNumber=null;
                            }
                            showAlertPopup(errorObj.getString("message"));
                        } else if (errorObj.has("error")) {
                            if(errorObj.has("phone")){
                                errorPhoneNumber=errorObj.getString("phone");
                            }else{
                                errorPhoneNumber=null;
                            }
                            showAlertPopup(errorObj.getString("error"));
                        }

                    }else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveConfirmappointment.this, error, null);
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            }
        };


        SharedPreferences settings =   getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
        HashMap<String, HashMap<String, Object>> onCallParams=new HashMap<>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("consultation_method", "Phone");
        params.put("physician_type", settings.getString(PreferenceConstants.PROVIDERTYPE_ID,"3"));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON, "Not Sure"));
        params.put("call_in_number", MdliveUtils.getSpecialCaseRemovedNumber(settings.getString(PreferenceConstants.PHONE_NUMBER, "")));
        params.put("do_you_have_primary_care_physician", "No");
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));

        onCallParams.put("user",params);

        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLiveConfirmappointment.this, null);
        services.doOnCallAppointment(gson.toJson(onCallParams), responseListener, errorListener);
    }




    private void dismissDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    hideProgress();
                } catch (final Exception ex) {
                }
            }
        });
    }

    private void showProgressDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    showProgress();
                } catch (final Exception ex) {

                }
            }
        });
    }

    public void showAlertPopup(String errorMessage){
        try {
            Log.e("Alert","Cominr Alert");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MDLiveConfirmappointment.this);
            if(errorPhoneNumber==null){
                alertDialogBuilder
                        .setTitle("")
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.mdl_Ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }else{
                alertDialogBuilder
                        .setTitle("")
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setPositiveButton(StringConstants.ALERT_CALLNOW, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("Phone Inside",errorPhoneNumber);
                                if (errorPhoneNumber != null) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + errorPhoneNumber.replaceAll("-", "")));
                                    startActivity(intent);
                                    MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
                                }else{
                                    dialog.dismiss();
                                }


                            }
                        }).setNegativeButton(StringConstants.ALERT_DISMISS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MDLiveChooseProvider.isDoctorOnCall=false;
                        MDLiveChooseProvider.isDoctorOnVideo=false;
                        dialog.dismiss();
                    }
                });
            }



            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                    if(errorPhoneNumber!=null){
                        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.mdlivePrimaryBlueColor));
                    }
                }
            });

            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
