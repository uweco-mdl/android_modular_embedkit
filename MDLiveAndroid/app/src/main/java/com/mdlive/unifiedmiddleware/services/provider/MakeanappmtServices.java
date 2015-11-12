package com.mdlive.unifiedmiddleware.services.provider;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.util.HashMap;

public class MakeanappmtServices extends BaseServicesPlugin {

    public MakeanappmtServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void makeappmt(HashMap<String,Object> postParams,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Gson gson=  new Gson();
            Log.v("PostParams",gson.toJson(postParams).toString());
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MAKE_APPOINTMENT,
                                    gson.toJson(postParams),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
