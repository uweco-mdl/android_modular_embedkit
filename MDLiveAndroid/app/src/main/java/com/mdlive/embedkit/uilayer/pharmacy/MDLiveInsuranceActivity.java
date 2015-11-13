package com.mdlive.embedkit.uilayer.pharmacy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by srinivasan_ka on 10/21/2015.
 */
public class MDLiveInsuranceActivity  extends MDLiveBaseActivity {

    boolean redirect_mypharmacy = false;
    Intent paymentIntent;
    Intent confirmAppmtIntent;
    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility_service);
        try {
            Class paymentClass = Class.forName("com.mdlive.sav.payment.MDLivePayment");
            Class appointmentClass = Class.forName("com.mdlive.sav.payment.MDLiveConfirmappointment");
            paymentIntent = new Intent(getApplicationContext(), paymentClass);
            confirmAppmtIntent = new Intent(getApplicationContext(), appointmentClass);
            if (getIntent() != null) {
                if (getIntent().getStringExtra("final_amount").equalsIgnoreCase("0.00")) ;
                {
                    confirmAppmtIntent.putExtra("final_amount", getIntent().getStringExtra("final_amount"));
                }
                if (getIntent().hasExtra("final_amount")) {
                    paymentIntent.putExtra("final_amount", getIntent().getStringExtra("final_amount"));
                }
                if (getIntent().hasExtra("redirect_mypharmacy")) {
                    paymentIntent.putExtra("redirect_mypharmacy", getIntent().getBooleanExtra("redirect_mypharmacy", false));
                }
            }
            if (getIntent().getStringExtra("final_amount").equalsIgnoreCase("0.00")) {
                mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(confirmAppmtIntent);
                        finish();
                    }
                }, 3000);
            } else {
                mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(paymentIntent);
                        finish();
                    }
                }, 3000);
            }
        } catch (ClassNotFoundException e){
            Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
        }
       // checkInsuranceEligibility();
    }


    /**
     * This method handles checks user insurance eligibility and return the final amount for the user.
     * successListener-Listner to handle success response.
     * errorListener -Listner to handle failed response.
     * PharmacyService-Class will send the request to the server and get the responses
     * doPostCheckInsulranceEligibility-Method will carries required parameters for sending request to the server.
     */

    public void checkInsuranceEligibility() {
        NetworkSuccessListener successListener = new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                Log.e("Zero Dollar Insurance", response.toString());
                try {
                    JSONObject jobj = new JSONObject(response.toString());
                    if (jobj.has("final_amount")) {
                        if (!jobj.getString("final_amount").equals("0") && !jobj.getString("final_amount").equals("0.00")) {
                            try {
                                Class clazz = Class.forName("com.mdlive.sav.payment.MDLivePayment");
                                Intent i = new Intent(getApplicationContext(), clazz);
                                i.putExtra("final_amount", jobj.getString("final_amount"));
                                if (redirect_mypharmacy) {
                                    i.putExtra("redirect_mypharmacy", true);
                                }
                                startActivity(i);
                                MdliveUtils.startActivityAnimation(MDLiveInsuranceActivity.this);
                            }catch (ClassNotFoundException e){
                                Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            moveToNextPage();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                if (getIntent() != null && getIntent().hasExtra("redirect_mypharmacy")) {
                    Intent startMyPharmacyIntent = new Intent(getApplicationContext(), MDLivePharmacy.class);
                    startMyPharmacyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if ((getIntent().getBooleanExtra("redirect_mypharmacy", false))) {
                        startMyPharmacyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(startMyPharmacyIntent);
                }else{
                    setResult(RESULT_OK);
                    finish();
                }
            }
        };
        PharmacyService insuranceService = new PharmacyService(MDLiveInsuranceActivity.this, null);
        insuranceService.doPostCheckInsulranceEligibility(formPostInsuranceParams(), successListener, errorListener);
    }

    // This is For navigating to the next Screen
    //if the amount has been deducted then it should go to the Confirm Appointment Screen
    private void moveToNextPage() {
        CheckdoconfirmAppointment(true);
        try {
            Class clazz = Class.forName("com.mdlive.sav.payment.MDLiveConfirmappointment");
            Intent i = new Intent(MDLiveInsuranceActivity.this, clazz);
            storePayableAmount("0.00");
            startActivity(i);
            MdliveUtils.startActivityAnimation(MDLiveInsuranceActivity.this);
        }catch (ClassNotFoundException e){
            Toast.makeText(getBaseContext(), getString(R.string.mdl_mdlive_module_not_found), Toast.LENGTH_LONG).show();
        }
    }

    public void storePayableAmount(String amount) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.PAY_AMOUNT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.AMOUNT, amount);
        editor.commit();
    }
    public void CheckdoconfirmAppointment(boolean checkExixtingCard) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(PreferenceConstants.EXISTING_CARD_CHECK,checkExixtingCard);
        editor.commit();
    }

    /**
     * This function is used to get post body content for Check Insurance Eligibility
     * Values hard coded are default criteria from get response of Insurance Eligibility of all users.
     */

    public String formPostInsuranceParams() {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        HashMap<String, String> insuranceMap = new HashMap<>();
        insuranceMap.put("appointment_method", "1");
        insuranceMap.put("provider_id", settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null));
        insuranceMap.put("timeslot", "Now");
        insuranceMap.put("provider_type_id",settings.getString(PreferenceConstants.PROVIDERTYPE_ID,""));
        insuranceMap.put("state_id", settings.getString(PreferenceConstants.LOCATION, "FL"));
        return new Gson().toJson(insuranceMap);
    }


    /**
     * This method will close the activity with transition effect.
     */
    @Override
    public void onBackPressed() {

    }
}
