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

public class SuggestMedicationService extends BaseServicesPlugin {
    public SuggestMedicationService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }
    /**
     * @param searchText
     * @param responseListener
     * @param errorListener
     */
    public void doLoginRequest(String searchText, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        HashMap<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("query", searchText);
        try {
            Gson gson = new Gson();
            String postBody = gson.toJson(queryMap);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_MEDICATION_SEARCH,
                                    postBody,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
