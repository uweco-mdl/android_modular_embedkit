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
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.AddMedicalConditionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteMedicalConditionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionAutoSuggestionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionListServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalConditionUpdateServices;

import org.json.JSONObject;

import java.util.HashMap;

public class AddConditions extends CommonConditionsMedicationsActivity {

    protected boolean isPerformingAutoSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        type = TYPE_CONSTANT.CONDITION ;
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.CommonConditionsAllergiesHeaderTv)).setText(getResources().getString(R.string.add_medical_condition));
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

                AddMedicalConditionServices services = new AddMedicalConditionServices(AddConditions.this, null);
                services.addMedicalConditionsRequest(successCallBackListener, errorListener, newConditions.get(i));

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

                HashMap<String, String> condition = existingConditions.get(i);
                HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
                condition.put("condition",condition.get("name"));
                condition.remove("name");
                postBody.put("medical_condition", condition);

                MedicalConditionUpdateServices services = new MedicalConditionUpdateServices(AddConditions.this, null);
                services.updateConditionRequest(condition.get("id"), new Gson().toJson(postBody), successCallBackListener, errorListener);

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

        MedicalConditionListServices services = new MedicalConditionListServices(AddConditions.this, null);
        services.getMedicalConditionsListRequest(successCallBackListener, errorListener);
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

        DeleteMedicalConditionServices services = new DeleteMedicalConditionServices(AddConditions.this, null);
        services.deleteMedicalConditionsRequest(successCallBackListener, errorListener, (String) ((ViewGroup) (deleteView.getParent())).getTag());

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
        if(!isPerformingAutoSuggestion && !previousSearch.equalsIgnoreCase(constraint)) {
            MedicalConditionAutoSuggestionServices services = new MedicalConditionAutoSuggestionServices(AddConditions.this, null);
            services.getMedicalConditionsAutoSuggestionRequest(successCallBackListener, errorListener, constraint);
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
