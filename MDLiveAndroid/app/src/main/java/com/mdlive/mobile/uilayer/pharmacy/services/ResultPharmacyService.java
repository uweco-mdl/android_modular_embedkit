package com.mdlive.mobile.uilayer.pharmacy.services;

import android.app.ProgressDialog;
import android.content.Context;

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

    /**
     * @param postBody
     * @param responseListener
     * @param errorListener
     */

    public void doLoginRequest(String postBody, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_SEARCH_LOCATION,
                    postBody, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
