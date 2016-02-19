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
 * Created by sanjibkumar_p on 7/28/2015.
 */
public class ReplaceCreditCardService extends BaseServicesPlugin {

    public ReplaceCreditCardService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void replaceCreditCardInfo(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String cardInfo)
    {
        try{
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_GET_CREDIT_CARD_INFO,
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
