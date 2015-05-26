package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sudha_s on 5/18/2015.
 */
public class ReasonForVisitServices extends BaseServicesPlugin {

    public ReasonForVisitServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }
    public void getReasonList(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_REASON_FOR_VISIT, null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

