package com.mdlive.unifiedmiddleware.services.helpandsupport;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONArray;

/**
 * Created by sanjibkumar_p on 7/14/2015.
 */
public class HelpAndSupportServices extends BaseServicesPlugin {

    public HelpAndSupportServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getHelpAndSupportServices(NetworkSuccessListener<JSONArray> responseListener , NetworkErrorListener errorListener){
        try {
            Log.d("HelpAndSupportRequest", AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_HELP_AND_SUPPORT);

            jsonArrayGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_HELP_AND_SUPPORT,
                                null,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);

    //            https://dev-members.mdlive.com/services/support/faqs
    //            jsonObjectGetRequest("https://dev-members.mdlive.com/services" + "support/faqs", null, responseListener, errorListener);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
