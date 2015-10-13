package com.mdlive.unifiedmiddleware.services.login;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 9/7/2015.
 */
public class DeeplinkService extends BaseServicesPlugin {
    public DeeplinkService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void deeplinkService(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String params, final String deviceId) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.DEEPLINK_SERVICE + deviceId,
                    null, responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
