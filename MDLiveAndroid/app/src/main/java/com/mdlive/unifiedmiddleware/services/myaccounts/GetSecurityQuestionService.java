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
 * Created by venkataraman_r on 6/17/2015.
 */
public class GetSecurityQuestionService extends BaseServicesPlugin {

    public GetSecurityQuestionService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void getSecurityQuestions(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String password)
    {
        try{
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_SECURITY_QUESTION,
                                    password,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}