package com.mdlive.unifiedmiddleware.services.behavioural;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sanjibkumar_p on 7/14/2015.
 */
public class BehaviouralUpdateService extends BaseServicesPlugin {

    public BehaviouralUpdateService(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }


    public void postBehaviouralUpdateService(String jsonString, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){

        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_UPDATE_BEHAVIOURAL_HISTORY,
                                    jsonString,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
