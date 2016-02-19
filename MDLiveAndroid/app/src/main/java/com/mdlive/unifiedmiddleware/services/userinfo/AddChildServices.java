package com.mdlive.unifiedmiddleware.services.userinfo;

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

public class AddChildServices extends BaseServicesPlugin {

    public AddChildServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getChildDependent(HashMap<String,HashMap<String,String>> Result,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Gson gson = new Gson();

            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.ADD_CHILD,
                                    gson.toJson(Result),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
