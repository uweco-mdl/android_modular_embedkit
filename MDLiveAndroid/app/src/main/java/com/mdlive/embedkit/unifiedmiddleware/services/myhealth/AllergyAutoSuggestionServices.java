package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.embedkit.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by Unnikrishnan_b on 23/05/15.
 */
public class AllergyAutoSuggestionServices extends BaseServicesPlugin {
    public AllergyAutoSuggestionServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void getAllergyAutoSuggestionRequest(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String query) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("query", query);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_AUTOSUGGESTION, obj.toString(), responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
