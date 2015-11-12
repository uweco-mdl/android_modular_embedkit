package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * This is a webservice class to add medication results of users
 */
public class AddMedicationService extends BaseServicesPlugin {
    public AddMedicationService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }
    /**
     * @param responseListener
     * @param errorListener
     */
    public void doLoginRequest(String postBody, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_MEDICATION_CREATE,
                                    postBody,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void addMedicationRequest(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener
                                        , String conditionName){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_MEDICATION_CREATE,
                                    conditionName,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
