package com.mdlive.unifiedmiddleware.services.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 9/7/2015.
 */
public class SSOBaylorService extends BaseServicesPlugin {
    public SSOBaylorService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void BaylorSSO(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String params) {
        try {
            Log.e("baylor param", params+"");
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_AUTHENTICATE_LOGIN_BAYLOR,
                    params, responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
