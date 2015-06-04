package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.embedkit.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class AddAllergyServices extends BaseServicesPlugin {
    public AddAllergyServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void addAllergyRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String name) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            JSONObject wrapperObj = new JSONObject();
            wrapperObj.put("allergy",obj);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_LIST, wrapperObj.toString(), responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
