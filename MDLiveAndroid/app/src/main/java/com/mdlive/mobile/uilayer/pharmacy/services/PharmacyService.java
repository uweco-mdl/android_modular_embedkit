package com.mdlive.mobile.uilayer.pharmacy.services;

import android.app.ProgressDialog;
import android.content.Context;

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

    public void doLoginRequest(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_PHARMACIES_CURRENT,
                    null, responseListener, errorListener);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
