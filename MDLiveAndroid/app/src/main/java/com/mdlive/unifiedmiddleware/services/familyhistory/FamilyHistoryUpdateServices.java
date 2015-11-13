package com.mdlive.unifiedmiddleware.services.familyhistory;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sanjibkumar_p on 7/14/2015.
 */
public class FamilyHistoryUpdateServices extends BaseServicesPlugin {

    public FamilyHistoryUpdateServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }

    public void postFamilyHistoryUpdateServices(JSONObject jsonObject, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){

        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_UPDATE_FAMILY_HISTORY,
                                    jsonObject.toString(),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
