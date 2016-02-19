package com.mdlive.unifiedmiddleware.services.provider;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by raja_rath on 10/1/2015.
 */
public class ProviderDetailsAffiliationServices extends BaseServicesPlugin {



    public ProviderDetailsAffiliationServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }

    public void getProviderDetailsAffiliation(String GroupID, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            //jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_GROUP_AFFILIATION + GroupID, null, responseListener, errorListener);
            jsonObjectGetRequest(GroupID, null, responseListener, errorListener, MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
