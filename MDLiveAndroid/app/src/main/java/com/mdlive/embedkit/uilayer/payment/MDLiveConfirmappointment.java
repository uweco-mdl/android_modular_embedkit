package com.mdlive.embedkit.uilayer.payment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

/**
 * Created by sudha_s on 8/22/2015.
 */
public class MDLiveConfirmappointment extends MDLiveBaseActivity {
    private String providerName,providerType,consultationType,consultationDate,Time,phone,doctorEVisit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_appointment_request_form);

    }
    public void getPreferenceValue()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        providerName = sharedpreferences.getString(PreferenceConstants.PATIENT_NAME, "");
        providerType = sharedpreferences.getString(PreferenceConstants.PROVIDER_TYPE, "");
        consultationType = sharedpreferences.getString(PreferenceConstants.CONSULTATION_TYPE, "");
        consultationDate = sharedpreferences.getString(PreferenceConstants.SELECTED_TIMESLOT, "");
        Time = sharedpreferences.getString(PreferenceConstants.SELECTED_DATE, "");
        phone = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
        doctorEVisit = sharedpreferences.getString(PreferenceConstants.PHONE_NUMBER, "");
    }
}
