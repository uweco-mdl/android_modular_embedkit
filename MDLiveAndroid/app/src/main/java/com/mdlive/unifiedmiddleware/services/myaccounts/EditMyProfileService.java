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
public class EditMyProfileService extends BaseServicesPlugin {

    public EditMyProfileService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void editMyProfile(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String cardInfo)
    {
        try{
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_EDIT_PROFILE_INFO,
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
