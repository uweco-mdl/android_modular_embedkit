package com.mdlive.unifiedmiddleware.services.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/19/2015.
 */
public class ChangePinService extends BaseServicesPlugin {

    public ChangePinService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void changePin(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String pin)
    {
        try{
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_CHANGE_PIN,
                    pin, responseListener, errorListener);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}