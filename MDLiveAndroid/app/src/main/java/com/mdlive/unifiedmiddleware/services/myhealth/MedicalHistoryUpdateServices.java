package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.util.HashMap;

public class MedicalHistoryUpdateServices extends BaseServicesPlugin {
    public MedicalHistoryUpdateServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void updateMedicalHistoryRequest(HashMap<String,HashMap<String,String>> params, NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener) {
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_UPDATE_MEDICAL_HISTORY,
                                    new Gson().toJson(params),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
