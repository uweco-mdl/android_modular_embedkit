package com.mdlive.embedkit.uilayer.myhealth.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.AddAllergyServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AllergiesUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AllergyAutoSuggestionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.AllergyListServices;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteAllergyServices;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * This class is used to manipulate CRUD (Create, Read, Update, Delete) function for Allergies.
 *
 * This class extends with MDLiveCommonConditionsMedicationsActivity
 *  which has all functions that is helped to achieve CRUD functions.
 *
 */

public class MDLiveAddAllergies extends MDLiveCommonConditionsMedicationsActivity {

    protected boolean isPerformingAutoSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Allergy
        type = TYPE_CONSTANT.ALLERGY;
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.CommonConditionsAllergiesHeaderTv)).setText(getResources().getString(R.string.add_allergy));
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
    }


    @Override
    protected void saveNewConditionsOrAllergies() {
        pDialog.show();
        setResult(RESULT_OK);
        if(newConditions.size() == 0){
            pDialog.dismiss();
            updateConditionsOrAllergies();
        } else {
            NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    updateConditionsOrAllergies();
                  /*  if (newConditions.size() == ++addConditionsCount) {
                    }*/
                }
            };

            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    medicalCommonErrorResponseHandler(error);
                }
            };

            AddAllergyServices services = new AddAllergyServices(MDLiveAddAllergies.this, null);
            services.addAllergyRequest(successCallBackListener, errorListener, newConditions);

            /*
            for (int i = 0; i < newConditions.size(); i++) {
                NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (newConditions.size() == ++addConditionsCount) {
                            pDialog.dismiss();
                            updateConditionsOrAllergies();
                        }
                    }
                };

                NetworkErrorListener errorListener = new NetworkErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        medicalCommonErrorResponseHandler(error);
                    }
                };

                AddAllergyServices services = new AddAllergyServices(MDLiveAddAllergies.this, null);
                services.addAllergyRequest(successCallBackListener, errorListener, newConditions.get(i));

            }*/
        }
    }

    public void setDatas(){
        for(int i = 0; i < newConditions.size(); i++){
            conditionsText += newConditions.get(i)+", ";
        }
        for(int i = 0; i < existingConditions.size(); i++){
            HashMap<String, String> data = existingConditions.get(i);
            conditionsText += data.get("name")+", ";
        }
        Intent resultData = new Intent();
        if(conditionsText == null || conditionsText.trim().length() == 0)
            conditionsText = "No allergies reported";
        resultData.putExtra("allegiesData", conditionsText);
        setResult(RESULT_OK, resultData);
    }


    @Override
    protected void updateConditionsOrAllergies() {

        pDialog.show();
        if(existingConditions.size() == 0){
            pDialog.dismiss();
            setDatas();
            finish();
        } else {
            for (int i = 0; i < existingConditions.size(); i++) {
                NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        setDatas();
                        finish();
                    }
                };

                NetworkErrorListener errorListener = new NetworkErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        medicalCommonErrorResponseHandler(error);
                    }
                };

                HashMap<String, String> allergy = existingConditions.get(i);
                HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
                postBody.put("allergy", allergy);

                AllergiesUpdateServices services = new AllergiesUpdateServices(MDLiveAddAllergies.this, null);
                services.updateAllergyRequest(allergy.get("id"), new Gson().toJson(postBody), successCallBackListener, errorListener);

            }
        }
    }

    /**
     *
     * This function will retrieve the known allergies from the server.
     *
     * MedicalConditionListServices - This service class will make the service calls to get the
     * conditions list.
     *
     */

    @Override
    protected void getConditionsOrAllergiesData() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                medicalConditionOrAllergyListHandleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }};

        AllergyListServices services = new AllergyListServices(MDLiveAddAllergies.this, null);
        services.getAllergyListRequest(successCallBackListener, errorListener);
    }

    @Override
    protected void deleteMedicalConditionsOrAllergyAction(ImageView deleteView, final LinearLayout addConditionsLl) {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(addConditionsLl.getChildCount() == 0){
                    addBlankConditionOrAllergy();
                }
                pDialog.dismiss();
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };

        DeleteAllergyServices services = new DeleteAllergyServices(MDLiveAddAllergies.this, null);
        services.deleteAllergyRequest(successCallBackListener, errorListener, (String)((ViewGroup)(deleteView.getParent())).getTag());
    }



    /**
     *
     * This function will n=make the service call to get the auto-completion allergies list based up
     * on the data entered in the edit text.
     *
     * MedicalConditionAutoSuggestionServices - The service class for getting the Auto suggestion list.
     *
     * @param atv :: The auto completion text view
     * @param constraint :: The text entered by the user.
     *
     */
    protected void getAutoCompleteData(final AutoCompleteTextView atv, String constraint) {
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                isPerformingAutoSuggestion = false;
                autoCompletionHandleSuccessResponse(atv, response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isPerformingAutoSuggestion = false;
                medicalCommonErrorResponseHandler(error);
            }};
        if( !isPerformingAutoSuggestion && !previousSearch.equalsIgnoreCase(constraint) ) {
            AllergyAutoSuggestionServices services = new AllergyAutoSuggestionServices(MDLiveAddAllergies.this, null);
            services.getAllergyAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
            previousSearch = constraint;
            isPerformingAutoSuggestion = true;
        }

    }

    @Override
    public void onBackPressed() {
        setDatas();
        super.onBackPressed();
    }

    /**
     * This function is for calling Medical History Page after done update on Add Medications Page.
     *
     * FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_NEW_TASK will flags to start a MedicalHistory Page with clear
     *  all activities on stack
     */

    public void callMedicalHistoryIntent(){
        finish();
    }

}
