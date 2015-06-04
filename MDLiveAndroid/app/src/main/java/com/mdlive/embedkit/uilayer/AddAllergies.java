package com.mdlive.embedkit.uilayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.myhealth.activity.CommonConditionsMedicationsActivity;
import com.mdlive.embedkit.uilayer.myhealth.activity.MedicalHistory;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.embedkit.unifiedmiddleware.services.myhealth.AddAllergyServices;
import com.mdlive.embedkit.unifiedmiddleware.services.myhealth.AllergiesUpdateServices;
import com.mdlive.embedkit.unifiedmiddleware.services.myhealth.AllergyAutoSuggestionServices;
import com.mdlive.embedkit.unifiedmiddleware.services.myhealth.AllergyListServices;
import com.mdlive.embedkit.unifiedmiddleware.services.myhealth.DeleteAllergyServices;

import org.json.JSONObject;

import java.util.HashMap;

public class AddAllergies extends CommonConditionsMedicationsActivity {

    protected boolean isPerformingAutoSuggestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        type = TYPE_CONSTANT.ALLERGY ;
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.CommonConditionsAllergiesHeaderTv)).setText(getResources().getString(R.string.add_allergy));
    }

    @Override
    protected void saveNewConditionsOrAllergies() {

        pDialog.show();
        if(newConditions.size() == 0){
            pDialog.dismiss();
            updateConditionsOrAllergies();
        } else {
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

                AddAllergyServices services = new AddAllergyServices(AddAllergies.this, null);
                services.addAllergyRequest(successCallBackListener, errorListener, newConditions.get(i));

            }
        }
    }

    @Override
    protected void updateConditionsOrAllergies() {

        pDialog.show();
        if(existingConditions.size() == 0){
            pDialog.dismiss();
            callMedicalHistoryIntent();
            finish();
        } else {
            for (int i = 0; i < existingConditions.size(); i++) {
                NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (existingConditions.size() == ++existingConditionsCount) {
                            pDialog.dismiss();
                            callMedicalHistoryIntent();
                            finish();
                        }
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

                AllergiesUpdateServices services = new AllergiesUpdateServices(AddAllergies.this, null);
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

        AllergyListServices services = new AllergyListServices(AddAllergies.this, null);
        services.getAllergyListRequest(successCallBackListener, errorListener);
    }


    @Override
    protected void deleteMedicalConditionsOrAllergyAction(ImageView deleteView, final LinearLayout addConditionsLl) {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                while(addConditionsLl.getChildCount()<5){
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

        DeleteAllergyServices services = new DeleteAllergyServices(AddAllergies.this, null);
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
            AllergyAutoSuggestionServices services = new AllergyAutoSuggestionServices(AddAllergies.this, null);
            services.getAllergyAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
            previousSearch = constraint;
            isPerformingAutoSuggestion = true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callMedicalHistoryIntent();
    }

    /**
     * This function is for calling Medical History Page after done update on Add Medications Page.
     *
     * FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_NEW_TASK will flags to start a MedicalHistory Page with clear
     *  all activities on stack
     */

    public void callMedicalHistoryIntent(){
        Intent medicalHistory = new Intent(getApplicationContext(), MedicalHistory.class);
        medicalHistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        medicalHistory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(medicalHistory);
    }

}
