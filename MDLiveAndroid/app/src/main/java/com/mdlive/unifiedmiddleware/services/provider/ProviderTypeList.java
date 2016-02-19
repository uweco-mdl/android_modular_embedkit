package com.mdlive.unifiedmiddleware.services.provider;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;
public class ProviderTypeList extends BaseServicesPlugin {

    public ProviderTypeList(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getProviderType(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_TYPE,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}