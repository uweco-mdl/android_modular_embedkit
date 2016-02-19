package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;


public class DeleteProcedureServices extends BaseServicesPlugin {
    public DeleteProcedureServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void deleteAllergyRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String id) {
        try {
            jsonObjectDeleteRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROCEDURE_LIST + "/"+id,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
