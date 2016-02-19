package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class ProviderTypeList extends BaseServicesPlugin {
    private Context context;
    public ProviderTypeList(Context context, ProgressDialog pDialog){
        super(context,pDialog);
        this.context = context;
    }

    public void getProviderType(String memberId,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            if(!memberId.isEmpty()){
                //url.replace(":id","get_image_url");
                SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.DEPENDENT_USER_ID,memberId);
                editor.commit();
            }else{
                SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.DEPENDENT_USER_ID,null);
                editor.commit();
            }
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_TYPE,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}