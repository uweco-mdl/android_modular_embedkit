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
 * Created by venkataraman_r on 7/23/2015.
 */
public class LoginService extends BaseServicesPlugin {

    public LoginService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void login(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String params)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.LOGIN_SERVICES,
                                    params,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
