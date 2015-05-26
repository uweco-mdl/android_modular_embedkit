package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sudha_s on 5/13/2015.
 */
public class ChooseProviderServices extends BaseServicesPlugin {

    public ChooseProviderServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }


    /**
     *
     * @param locationType
     * @param providerType
     */
    public void doChooseProviderRequest(String locationType,String providerType, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("located_in",locationType);
            params.put("provider_type",providerType);
            Log.d("new Gson().toJson(params)", new Gson().toJson(params));
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_CHOOSE_PROVIDER, new Gson().toJson(params), responseListener, errorListener);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}