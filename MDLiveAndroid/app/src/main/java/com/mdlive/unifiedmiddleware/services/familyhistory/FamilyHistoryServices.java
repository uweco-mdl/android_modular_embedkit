package com.mdlive.unifiedmiddleware.services.familyhistory;

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
 * Created by sanjibkumar_p on 7/14/2015.
 */
public class FamilyHistoryServices extends BaseServicesPlugin {

    public FamilyHistoryServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getFamilyHistoryServices(NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Log.d("FamilyHistoryRequest", AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_FAMILY_HISTORY);
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_FAMILY_HISTORY,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
