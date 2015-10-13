package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;


public class DeleteMedicalServices extends BaseServicesPlugin {
    public DeleteMedicalServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void deleteAllergyRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, int id) {
        try {
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.DELETE_MEDICALREPORT + "/"+id, null, responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
