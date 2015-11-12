package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;


public class MedicalHistoryAggregationServices extends BaseServicesPlugin {

    public MedicalHistoryAggregationServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }
    public void getMedicalHistoryAggregationRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_HISTORY_AGGREGATION,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}