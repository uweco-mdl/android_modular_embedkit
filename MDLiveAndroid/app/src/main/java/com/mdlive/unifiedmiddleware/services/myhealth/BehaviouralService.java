package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.Response;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;

import org.json.JSONObject;

public class BehaviouralService extends BaseServicesPlugin {
    public BehaviouralService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }
    public void doGetBehavioralHealthService(Response.Listener< JSONObject > responseListener, Response.ErrorListener errorListener){
        //https://stage-rtl.members.mdlive.com/services/behavioral_histories
        //AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_BEHAVIOURAL_HEALTH
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL+"/behavioral_histories",
                                null,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);
    }
}
