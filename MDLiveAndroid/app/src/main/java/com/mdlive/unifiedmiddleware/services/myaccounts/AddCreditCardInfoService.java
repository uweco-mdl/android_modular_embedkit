package com.mdlive.unifiedmiddleware.services.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 6/26/2015.
 */
public class AddCreditCardInfoService extends BaseServicesPlugin {

    public AddCreditCardInfoService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void addCreditCardInfo(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String cardInfo)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_ADD_CREDIT_CARD_INFO,
                    cardInfo, responseListener, errorListener);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
