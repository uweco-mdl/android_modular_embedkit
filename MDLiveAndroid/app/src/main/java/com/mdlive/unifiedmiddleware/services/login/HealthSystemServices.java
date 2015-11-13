package com.mdlive.unifiedmiddleware.services.login;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by unnikrishnan_b on 9/24/2015.
 */
public class HealthSystemServices extends BaseServicesPlugin {

    public HealthSystemServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void getHealthSystemsData(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String ipAddress) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.GEO_TARGET_DETAILS + ipAddress,
                    null, responseListener, errorListener, MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}