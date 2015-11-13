package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class LoadTimeZoneByState extends BaseServicesPlugin {

    public LoadTimeZoneByState(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }
    public void getTimeZoneByState(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_TIMEZONE_BY_STATE, null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

