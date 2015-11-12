package com.mdlive.unifiedmiddleware.services.provider;

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
public class FilterSearchServices extends BaseServicesPlugin {

    public FilterSearchServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getFilterSearch(HashMap<String,String> postParams,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Gson gson=  new Gson();
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_FILTER_SEARCH,
                                    gson.toJson(postParams),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
