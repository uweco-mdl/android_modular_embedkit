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
import com.mdlive.unifiedmiddleware.services.myhealth.AddMedicationService;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteMedicationService;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicationService;
import com.mdlive.unifiedmiddleware.services.myhealth.SuggestMedicationService;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateMedicalService;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * This class is used to manipulate CRUD (Create, Read, Update, Delete) function for Medications.
 *
 * This class extends with MDLiveCommonConditionsMedicationsActivity
 *  which has all functions that is helped to achieve CRUD functions.
 *
 */
public class MDLiveAddMedications extends MDLiveCommonConditionsMedicationsActivity {

    protected boolean isPerformingAutoSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Medications
        type = TYPE_CONSTANT.MEDICATION;
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.CommonConditionsAllergiesHeaderTv)).setText(getResources().getString(R.string.add_medication));
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt
        )).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to save new medication details entered by user.
     *
     */
    @Override
    protected void saveNewConditionsOrAllergies() {
        pDialog.show();
        if (newConditions.size() == 0) {
            updateConditionsOrAllergies();
        } else {
            for (int i = 0; i < newConditions.size(); i++) {
                NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        updateConditionsOrAllergies();
                        /*if (newConditions.size() == ++addConditionsCount) {
                        }*/
                    }
                };
                NetworkErrorListener errorListener = new NetworkErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        medicalCommonErrorResponseHandler(error);
                    }
                };
                HashMap<String, String> medication = new HashMap<String, String>();
                medication.put("name", newConditions.get(i));
                HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
                postBody.put("medication", medication);
                AddMedicationService services = new AddMedicationService(MDLiveAddMedications.this, null);
                services.doLoginRequest(new Gson().toJson(postBody), successCallBackListener, errorListener);
            }
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
            conditionsText = "No medications reported";
        else
            conditionsText += "...";
        resultData.putExtra("medicationData", conditionsText);
        setResult(RESULT_OK, resultData);
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to update medication details modified by user.
     *
     */
    @Override
    protected void updateConditionsOrAllergies() {
        try {
            pDialog.show();
            if (existingConditions.size() == 0) {
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
                    HashMap<String, String> medication = existingConditions.get(i);
                    HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
                    postBody.put("medication", medication);
                    UpdateMedicalService services = new UpdateMedicalService(MDLiveAddMedications.this, null);
                    services.doLoginRequest(medication.get("id"), new Gson().toJson(postBody), successCallBackListener, errorListener);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This function will retrieve the known allergies from the server.
     * MedicalConditionListServices - This service class will make the service calls to get the
     * conditions list.
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
            }
        };
        MedicationService services = new MedicationService(MDLiveAddMedications.this, null);
        services.doLoginRequest(successCallBackListener, errorListener);
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to delete medication details
     * @param deleteView        :: The image has clicklistner to delete
     * @param addConditionsLl :: Holder of all medication list datas.
     *
     */
    @Override
    protected void deleteMedicalConditionsOrAllergyAction(ImageView deleteView, final LinearLayout addConditionsLl) {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                while (addConditionsLl.getChildCount() < 3) {
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

        HashMap<String, String> postBody = new HashMap<String, String>();
        postBody.put("id", (String) ((ViewGroup) (deleteView.getParent())).getTag());
        DeleteMedicationService services = new DeleteMedicationService(MDLiveAddMedications.this, null);
        services.doLoginRequest(new Gson().toJson(postBody), (String) ((ViewGroup) (deleteView.getParent())).getTag(),
                successCallBackListener, errorListener);
    }


    /**
     * This function will n=make the service call to get the auto-completion allergies list based up
     * on the data entered in the edit text.
     * MedicalConditionAutoSuggestionServices - The service class for getting the Auto suggestion list.
     *
     * @param atv        :: The auto completion text view
     * @param constraint :: The text entered by the user.
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
            }
        };
        if (!isPerformingAutoSuggestion && !previousSearch.equalsIgnoreCase(constraint)) {
            SuggestMedicationService services = new SuggestMedicationService(MDLiveAddMedications.this, null);
            services.doLoginRequest(constraint, successCallBackListener, errorListener);
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
     * FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_NEW_TASK will flags to start a MedicalHistory Page with clear
     * all activities on stack
     */

    public void callMedicalHistoryIntent() {
        finish();
        /*Intent medicalHistory = new Intent(getApplicationContext(), MDLiveMedicalHistory.class);
        medicalHistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        medicalHistory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(medicalHistory);*/
    }


}
