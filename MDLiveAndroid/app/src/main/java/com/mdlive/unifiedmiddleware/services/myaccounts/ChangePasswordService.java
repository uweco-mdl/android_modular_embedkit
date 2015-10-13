package com.mdlive.unifiedmiddleware.services.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/17/2015.
 */
public class ChangePasswordService extends BaseServicesPlugin {

    public ChangePasswordService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void changePassword(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String password)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_CHANGE_PASSWORD,
                    password, responseListener, errorListener);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
