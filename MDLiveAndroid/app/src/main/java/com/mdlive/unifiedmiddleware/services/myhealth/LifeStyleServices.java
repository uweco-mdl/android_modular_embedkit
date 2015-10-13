package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class LifeStyleServices extends BaseServicesPlugin {

    public LifeStyleServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getLifeStyleServices(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_LIFE_STYLE, null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

