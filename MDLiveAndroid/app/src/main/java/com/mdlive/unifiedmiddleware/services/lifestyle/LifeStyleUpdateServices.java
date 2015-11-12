package com.mdlive.unifiedmiddleware.services.lifestyle;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sanjibkumar_p on 7/10/2015.
 */
public class LifeStyleUpdateServices extends BaseServicesPlugin {

    public LifeStyleUpdateServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }


    public void postLifeStyleServices(JSONObject jsonObject, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){

        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_UPDATE_LIFE_STYLE,
                                    jsonObject.toString(),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
