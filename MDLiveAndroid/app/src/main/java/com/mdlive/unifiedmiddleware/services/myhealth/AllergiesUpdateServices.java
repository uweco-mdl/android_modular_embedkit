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
public class AllergiesUpdateServices extends BaseServicesPlugin {
    public AllergiesUpdateServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }
    /**
     * @param responseListener
     * @param errorListener
     */
    public void updateAllergyRequest(String conditionId, String postBody, NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener){
        try {
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_LIST + "/" + conditionId,
                                    postBody,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
