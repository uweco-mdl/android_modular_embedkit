package com.mdlive.unifiedmiddleware.services.helpandsupport;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sanjibkumar_p on 7/23/2015.
 */
public class HelpandSupportAskQuestionPostService extends BaseServicesPlugin {

    public HelpandSupportAskQuestionPostService(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }


    public void postHelpandSupportAskQuestionPostService(JSONObject jsonObject, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){

        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ASK_A_QUESTION,
                                    jsonObject.toString(),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
