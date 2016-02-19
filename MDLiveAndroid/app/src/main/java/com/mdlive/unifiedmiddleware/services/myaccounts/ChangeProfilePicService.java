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
 * Created by venkataraman_r on 8/11/2015.
 */
public class ChangeProfilePicService extends BaseServicesPlugin {

    public ChangeProfilePicService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void changeProfilePic(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String picture)
    {
        try{
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_CHANGE_PROFILE_PIC,
                                    picture,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}