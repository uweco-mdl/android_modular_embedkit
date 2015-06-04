package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by Unnikrishnan_b on 23/05/15.
 */
public class MedicalConditionListServices extends BaseServicesPlugin {
    public MedicalConditionListServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void getMedicalConditionsListRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener) {
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST, null, responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
