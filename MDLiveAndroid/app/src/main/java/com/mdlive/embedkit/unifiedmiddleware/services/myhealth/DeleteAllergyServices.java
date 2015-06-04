package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.embedkit.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;


public class DeleteAllergyServices extends BaseServicesPlugin {
    public DeleteAllergyServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void deleteAllergyRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String id) {
        try {
            jsonObjectDeleteRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_LIST + "/"+id, null, responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
