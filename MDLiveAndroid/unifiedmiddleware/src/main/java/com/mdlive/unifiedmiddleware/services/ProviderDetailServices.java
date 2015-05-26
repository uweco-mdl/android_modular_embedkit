package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sudha_s on 5/21/2015.
 */
public class ProviderDetailServices extends BaseServicesPlugin {

    public ProviderDetailServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getProviderDetails(String DoctorId,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
//            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + "/providers/"+DoctorId+"?appointment_date=2015/1/20&appointment_type=3&located_in=FL", null, responseListener, errorListener);
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_DETAILS, null, responseListener, errorListener);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}