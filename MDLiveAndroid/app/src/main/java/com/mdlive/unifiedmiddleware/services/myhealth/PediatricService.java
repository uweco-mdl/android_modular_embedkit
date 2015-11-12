package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class PediatricService extends BaseServicesPlugin {
    public PediatricService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }

    public void doPostPediatricBelowTwo(String params,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        jsonObjectPostRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_UPDATE_PEDIATRIC,params,responseListener,errorListener);

    }
    public void doGetPediatricBelowTwo(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_GET_PEDIATRIC,
                                null,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);

    }
    public void doPostPediatricAboveTwo(){

    }
    public void doGetPediatricAboveTwo(){

    }
}
