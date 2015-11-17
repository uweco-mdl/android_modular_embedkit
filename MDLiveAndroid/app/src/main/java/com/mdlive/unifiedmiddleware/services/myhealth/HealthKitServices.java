package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.provider.Settings;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by unnikrishnan_b on 10/8/2015.
 */
public class HealthKitServices extends BaseServicesPlugin {
    Context context;
    public HealthKitServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);
        this.context = context;

    }

    public void registerHealthKitSync(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            String deviceToken = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_HEALTH_KIT_SYNC + deviceToken,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addHealthKitSync(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            String deviceToken = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String body = "{\"id\" : \"" + deviceToken + "\" }";
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_HEALTH_KIT_SYNC,
                    body, responseListener, errorListener, MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deleteHealthKitSync(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            String deviceToken = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            jsonObjectDeleteRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_HEALTH_KIT_SYNC + deviceToken,
                                        null,
                                        responseListener,
                                        errorListener,
                                        MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
