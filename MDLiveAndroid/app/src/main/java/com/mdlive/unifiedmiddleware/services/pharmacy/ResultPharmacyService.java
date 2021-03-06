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
 * This is a webservice class to get list of results depend upon search criteria
 */
public class ResultPharmacyService extends BaseServicesPlugin {

    public ResultPharmacyService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }

    public void doPharmacyLocationRequest(String postBody, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_SEARCH_LOCATION,
                                    postBody,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
