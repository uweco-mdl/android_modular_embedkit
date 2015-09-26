package com.mdlive.embedkit.uilayer.payment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.sav.CircularNetworkImageView;
import com.mdlive.embedkit.uilayer.sav.MDLiveAppointmentThankYou;
import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ConfirmAppointmentServices;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);
        getPreferenceValue();
        clearMinimizedTime();

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

    }

    //do confirm appointment service
    private void doConfirmAppointment() {
        showProgress();
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
//                        Intent i = new Intent(MDLiveConfirmappointment.this, MDLiveConfirmappointment.class);
//                        startActivity(i);
//                        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
                        if (consultationType.equalsIgnoreCase("phone")) {
                            movetothankyou();
                        }
//                        else if(consultationType.equalsIgnoreCase("video")&&Time.isEmpty())
//                        {
//                            movetostartVisit();
//                        }
                        else if (!Time.equalsIgnoreCase("Now")) {

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

//                        Toast.makeText(MDLivePayment.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    hideProgress();
                    MdliveUtils.handelVolleyErrorResponse(MDLiveConfirmappointment.this, error, getProgressDialog());
                } catch (Exception e) {
                    hideProgress();
                    e.printStackTrace();
                }
            }
        };

        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences reasonPref = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
        HashMap<String, Object> params = new HashMap<String, Object>();
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        Log.e("PostValue confirmTimeStamp", TimeStamp);
        Log.e("PostValue phys", phys_ID);
        params.put("appointment_method", appointmentMethodType);
        params.put("alternate_visit_option", "No Answer");
        if(phys_ID==null)
        {
            params.put("phys_availability_id", "");
        }else
        {
            params.put("phys_availability_id", phys_ID);
        }

        if(settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "").equalsIgnoreCase("Now"))
        {
            params.put("timeslot","Now");
        }else if(settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "").equalsIgnoreCase("0"))
        {
            params.put("timeslot","Now");
        }

        else {
            params.put("timeslot", Long.parseLong(settings.getString(PreferenceConstants.SELECTED_TIMESTAMP, "")));
        }

        params.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        params.put("chief_complaint", reasonPref.getString(PreferenceConstants.REASON, "Not Sure"));
        params.put("customer_call_in_number", settings.getString(PreferenceConstants.PHONE_NUMBER, ""));

        params.put("do_you_have_primary_care_physician", settings.getString(PreferenceConstants.PHONE_NUMBER, "No"));
        params.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
        SharedPreferences promocodePreferences = this.getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
//        if (promoCode != null && !promoCode.isEmpty()) {
            params.put("promocode", promocodePreferences.getString(PreferenceConstants.OFFER_CODE, ""));

//        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        ConfirmAppointmentServices services = new ConfirmAppointmentServices(MDLiveConfirmappointment.this, null);
        services.doConfirmAppointment(gson.toJson(params), responseListener, errorListener);
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
            Log.e("Am", "Am in rightbtnClick");
            doConfirmAppointment();
        }


    }

    private void movetostartVisit() {
        Intent intent = new Intent(MDLiveConfirmappointment.this, MDLiveStartVisit.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
    }

    private void movetothankyou() {
        Intent intent = new Intent(MDLiveConfirmappointment.this, MDLiveAppointmentThankYou.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveConfirmappointment.this);
    }


    public void getPreferenceValue() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        CheckdoconfirmAppmt = sharedpreferences.getBoolean(PreferenceConstants.EXISTING_CARD_CHECK, true);
        Log.e("CheckdoconfirmAppmt", CheckdoconfirmAppmt + "");
        providerName = sharedpreferences.getString(PreferenceConstants.PROVIDER_DOCTORNANME_PREFERENCES, "");
        ((TextView) findViewById(R.id.txtProfileName)).setText(providerName);
        providerType = sharedpreferences.getString(PreferenceConstants.PROVIDER_TYPE, "");
        ((TextView) findViewById(R.id.txtproviderType)).setText(providerType);
        consultationType = sharedpreferences.getString(PreferenceConstants.CONSULTATION_TYPE, "");
        ((TextView) findViewById(R.id.txtConsultationtype)).setText(consultationType + " Consultation");
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

        if (!consultationDate.isEmpty() && !Time.isEmpty()) {
            ((TextView) findViewById(R.id.txtDate)).setText(consultationDate);
            ((TextView) findViewById(R.id.txtTime)).setText(Time);
        } else {
            Calendar calendar = Calendar.getInstance();
            String format = new SimpleDateFormat("E, MMM d, yyyy").format(calendar.getTime());
            ((TextView) findViewById(R.id.txtDate)).setText(format);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm a");
            String currentTime = df.format(calendar.getTime());
            ((TextView) findViewById(R.id.txtTime)).setText(currentTime);
        }

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
}
