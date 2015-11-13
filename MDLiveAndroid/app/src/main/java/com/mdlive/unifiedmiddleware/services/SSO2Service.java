package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class SSO2Service extends BaseServicesPlugin {
    public SSO2Service(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }

    public void doSSORequest(final String requestData,
                             NetworkSuccessListener<JSONObject> successListener,
                             NetworkErrorListener errorListener){

        jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.SSO_SERVICE,
                                requestData,
                                successListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);
    }
}
