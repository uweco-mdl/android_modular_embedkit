package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class MedicalConditionAutoSuggestionServices extends BaseServicesPlugin{
    public MedicalConditionAutoSuggestionServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void getMedicalConditionsAutoSuggestionRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String query) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("query", query);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_AUTOSUGGESTION,
                                    obj.toString(),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
