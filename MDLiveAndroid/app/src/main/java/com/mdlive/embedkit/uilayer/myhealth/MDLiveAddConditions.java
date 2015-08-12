package com.mdlive.embedkit.uilayer.myhealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.AddMedicalConditionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteMedicalConditionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionAutoSuggestionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionListServices;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * This class is used to manipulate CRUD (Create, Read, Update, Delete) function for Conditions.
 *
 * This class extends with MDLiveCommonConditionsMedicationsActivity
 *  which has all functions that is helped to achieve CRUD functions.
 *
 */

public class MDLiveAddConditions extends MDLiveCommonConditionsMedicationsActivity {

    protected boolean isPerformingAutoSuggestion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Conditions
        type = TYPE_CONSTANT.CONDITION;
        super.onCreate(savedInstanceState);
        IsThisPageEdited = false;
        ((TextView) findViewById(R.id.CommonConditionsAllergiesHeaderTv)).setText(getResources().getString(R.string.add_medical_condition));
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to save new condition details entered by user.
     *
     */
    @Override
    protected void saveNewConditionsOrAllergies() {
        showProgress();
        setResult(RESULT_OK);
        IsThisPageEdited = true;
        if (newConditions.size() == 0) {
            hideProgress();
            updateConditionsOrAllergies();
        } else {
            NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    hideProgress();
                    updateConditionsOrAllergies();
                }
            };
            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    medicalCommonErrorResponseHandler(error);
                }
            };

            AddMedicalConditionServices services = new AddMedicalConditionServices(MDLiveAddConditions.this, null);
            services.addMedicalConditionsRequest(successCallBackListener, errorListener, newConditions);
        }
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to update conditions details modified by user.
     *
     */

    @Override
    protected void updateConditionsOrAllergies() {
        try {
            IsThisPageEdited = true;
            new UpdateExistingConditionsService().execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This asyntask class is used to update preexisting condition data sequentially
     * This function will be called in MDLiveCommonConditionsMedicationsActivity
     * which has already extends with MDLiveAddConditions
     */
    public class UpdateExistingConditionsService extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            showProgress();
            super.onPreExecute();

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();
            checkMedicalAggregation();
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (existingConditions.size() == 0) {
            } else {
                for (int i = 0; i < existingConditions.size(); i++) {
                    HashMap<String, String> condition = existingConditions.get(i);
                    HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
                    condition.put("condition", condition.get("name"));
                    condition.remove("name");
                    postBody.put("medical_condition", condition);
                    try{
                        updateConditionDetails(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST + "/" +condition.get("id"),
                                new Gson().toJson(postBody));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }


    /**
     * This function will retrieve the known allergies from the server.
     * MedicalConditionListServices - This service class will make the service calls to get the
     * conditions list.
     */
    @Override
    protected void getConditionsOrAllergiesData() {
        showProgress();
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
        MedicalConditionListServices services = new MedicalConditionListServices(MDLiveAddConditions.this, null);
        services.getMedicalConditionsListRequest(successCallBackListener, errorListener);
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to delete allergy details
     * @param deleteView        :: The image has clicklistner to delete
     * @param addConditionsLl :: Holder of all allergy list datas.
     *
     */
    @Override
    protected void deleteMedicalConditionsOrAllergyAction(ImageView deleteView, final LinearLayout addConditionsLl) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                IsThisPageEdited = true;
                if (addConditionsLl.getChildCount() == 0) {
                    addBlankConditionOrAllergy();
                }
                hideProgress();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        DeleteMedicalConditionServices services = new DeleteMedicalConditionServices(MDLiveAddConditions.this, null);
        services.deleteMedicalConditionsRequest(successCallBackListener, errorListener, (String) ((ViewGroup) (deleteView.getParent())).getTag());
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
        if (!isPerformingAutoSuggestion /*&& !previousSearch.equalsIgnoreCase(constraint)*/) {
            MedicalConditionAutoSuggestionServices services = new MedicalConditionAutoSuggestionServices(MDLiveAddConditions.this, null);
            services.getMedicalConditionsAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
            previousSearch = constraint;
            isPerformingAutoSuggestion = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(IsThisPageEdited){
            checkMedicalAggregation();
        }
        else{
            super.onBackPressed();
            MdliveUtils.closingActivityAnimation(this);
        }
    }

    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
    }
}
