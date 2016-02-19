package com.mdlive.unifiedmiddleware.services.pharmacy;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * This is a webservice class to get results of default pharmacy details
 */
public class PharmacyService extends BaseServicesPlugin {
    public PharmacyService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }
    /**
     *
     * @param responseListener
     * @param errorListener
     */
    public void doMyPharmacyRequest(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_CURRENT,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param responseListener
     * @param errorListener
     */
    public void doMyPharmacyRequest(String latitude, String longitude, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            if(latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()){
                jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_CURRENT+
                                "?latitude="+latitude+"&longitude="+longitude,
                                null,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);
            }else{
                jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_CURRENT,
                                null,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method will send the request to the server for checking user insurance eligibility
     * @param params- Contains post parameters needed for the server to check insurance eligibility
     * @param responseListener-Handles Success Response from server
     * @param errorListener-Handles error resoinse from server.
     */

    public void doPostCheckInsulranceEligibility(String params,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_ZERO_INSURANCE,
                                params,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);
    }


}
