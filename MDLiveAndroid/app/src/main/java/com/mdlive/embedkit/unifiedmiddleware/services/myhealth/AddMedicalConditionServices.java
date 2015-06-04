package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class AddMedicalConditionServices extends BaseServicesPlugin {
    public AddMedicalConditionServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void addMedicalConditionsRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String name) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            JSONObject wrapperObj = new JSONObject();
            wrapperObj.put("condition",obj);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST, wrapperObj.toString(), responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
