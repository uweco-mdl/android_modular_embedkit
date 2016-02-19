package com.mdlive.unifiedmiddleware.services.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 8/25/2015.
 */
public class AddPCP extends BaseServicesPlugin {

    public AddPCP(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void addPCPInfo(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String cardInfo)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_ADD_PCP,
                                    cardInfo,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
