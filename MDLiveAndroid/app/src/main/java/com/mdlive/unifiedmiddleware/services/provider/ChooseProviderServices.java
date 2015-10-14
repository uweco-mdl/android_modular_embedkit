package com.mdlive.unifiedmiddleware.services.provider;

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
public class ChooseProviderServices extends BaseServicesPlugin {

    public ChooseProviderServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }
    public void doChooseProviderRequest(String locationType,String providerType, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("located_in",locationType);
            params.put("provider_type",providerType);
            Log.e("Located in-->",locationType+"  Provider Type--->"+ providerType);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_CHOOSE_PROVIDER, new Gson().toJson(params), responseListener, errorListener);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}