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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by unnikrishnan_b on 8/22/2015.
 */
public class ConsultationHistoryServices extends BaseServicesPlugin {

    public ConsultationHistoryServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void getConsultationHistory(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener) {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_CONSULTATION_HISTORY,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);

        }
}
