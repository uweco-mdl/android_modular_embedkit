package com.mdlive.unifiedmiddleware.services.login;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.BuildConfig;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/23/2015.
 */
public class UpgradeAlert extends BaseServicesPlugin {
    public static final String APP_MARKET_URI = "market://details?id=com.mdlive.mobile";
    public static final String APP_PLAY_STORE_URI = "https://play.google.com/store/apps/details?id=com.mdlive.mobile";

    public UpgradeAlert(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void upgradeAlertService(NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener, String params) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.UPGRADE_SERVICES + "os=Android&version=" + BuildConfig.VERSION_NAME,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
