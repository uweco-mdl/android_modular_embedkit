package com.mdlive.unifiedmiddleware.services.myaccounts;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by venkataraman_r on 7/27/2015.
 */
public class GetFamilyMemberInfoService extends BaseServicesPlugin {

    public GetFamilyMemberInfoService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }

    public void getFamilyMemberInfo(NetworkSuccessListener<JSONObject> responseListener,NetworkErrorListener errorListener, String params)
    {
        try{
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_GET_FAMILY_MEMBER_INFO,
                    params, responseListener, errorListener);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}