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
 * Created by venkataraman_r on 7/27/2015.
 */
public class AddFamilyMemberInfoService extends BaseServicesPlugin {

    public AddFamilyMemberInfoService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void addFamilyMemberInfo(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String cardInfo)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_ADD_FAMILY_INFO,
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
