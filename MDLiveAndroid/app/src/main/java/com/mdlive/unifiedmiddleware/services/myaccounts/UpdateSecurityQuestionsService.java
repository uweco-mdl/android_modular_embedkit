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
 * Created by venkataraman_r on 6/18/2015.
 */
public class UpdateSecurityQuestionsService extends BaseServicesPlugin {

    public UpdateSecurityQuestionsService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void updateSecurityQuestions(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String params)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_UPDATE_SECURITY_QUESTIONS,
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
