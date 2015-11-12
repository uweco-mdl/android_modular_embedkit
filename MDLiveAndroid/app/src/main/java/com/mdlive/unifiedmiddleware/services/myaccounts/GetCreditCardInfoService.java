package com.mdlive.unifiedmiddleware.services.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/26/2015.
 */
public class GetCreditCardInfoService extends BaseServicesPlugin {

    public GetCreditCardInfoService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void getCreditCardInfo(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String params)
    {
        try{
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_GET_CREDIT_CARD_INFO,
                                    params,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
            //Log.e("req Url",(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_GET_CREDIT_CARD_INFO));

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}