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
 * This is a webservice class to update pharmacy details.
 */
public class SetPharmacyService extends BaseServicesPlugin {

    private Context context;
    private ProgressDialog pDialog;

    public SetPharmacyService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }


    /**
     * @param pharmacyId
     * @param postBody
     * @param responseListener
     * @param errorListener
     */

    public void doPharmacyResultsRequest(String postBody, String pharmacyId, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_UPDATE+pharmacyId,
                                    postBody,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }




}
