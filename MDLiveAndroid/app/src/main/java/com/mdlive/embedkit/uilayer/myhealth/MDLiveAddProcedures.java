package com.mdlive.embedkit.uilayer.myhealth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteProcedureServices;
import com.mdlive.unifiedmiddleware.services.myhealth.ProcedureListServices;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by srinivasan_ka on 8/26/2015.
 */
public class MDLiveAddProcedures extends MDLiveCommonConditionsMedicationsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Conditions
        type = TYPE_CONSTANT.PROCEDURE;
        super.onCreate(savedInstanceState);
        IsThisPageEdited = false;
        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.options_icon);
        ((TextView) findViewById(R.id.headerTxt)).setText(getResources().getString(R.string.add_procedure));
    }


    public void addConditionsClick(View view){
        Intent i = new Intent(getApplicationContext(), MDLiveHealthModule.class);
        i.putExtra("type", "procedure");
        startActivityForResult(i, INSERT_CODE);
        MdliveUtils.startActivityAnimation(MDLiveAddProcedures.this);
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
        ProcedureListServices services = new ProcedureListServices(MDLiveAddProcedures.this, null);
        services.getAllergyListRequest(successCallBackListener, errorListener);
    }

    /**
     *
     *  Successful Response Handler for Medical History Completion. Once the data is successfully received,
     *  the conditions are fetched from the JSONObject response and this data is pre-rendered in the
     *  layout by calling the preRenderKnownConditionData() function.
     *
     */

    protected void medicalConditionOrAllergyListHandleSuccessResponse(JSONObject response) {
        try {
            Log.e("Conditions response", response.toString());
            conditionsListJSONArray = response.getJSONArray("surgeries");
            preRenderKnownConditionData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void deleteConditions(){
        showProgress();
        ArrayList<String> deleteIdItems = adapter.getRemovedItemsIds();
        for(String id : deleteIdItems){
            deleteMedicalConditionsOrAllergyAction(id);
        }
    }

    /**
     * This is a override function which was declared in MDLiveCommonConditionsMedicationsActivity
     *
     * This function is used to delete allergy details
     *
     */
    public void deleteMedicalConditionsOrAllergyAction(final String conditionId) {

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(adapter.getRemovedItemsIds().get(adapter.getRemovedItemsIds().size()-1).equals(conditionId)){
                    hideProgress();
                    isEditCalled = false;
                    adapter.getRemovedItemsIds().clear();
                    adapter.notifyDataSetChanged();
                    ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.options_icon);
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        DeleteProcedureServices services = new DeleteProcedureServices(MDLiveAddProcedures.this, null);
        services.deleteAllergyRequest(successCallBackListener, errorListener, conditionId);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INSERT_CODE && resultCode == RESULT_OK){
            getConditionsOrAllergiesData();
        }else if(requestCode == UPDATE_CODE && resultCode == RESULT_OK){
            getConditionsOrAllergiesData();
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