package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;


public class DeleteMedicalConditionServices extends BaseServicesPlugin {
    public DeleteMedicalConditionServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void deleteMedicalConditionsRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String id) {
        try {
            jsonObjectDeleteRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST + "/"+id,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
