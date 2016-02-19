package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class CurrentLocationServices extends BaseServicesPlugin

    {

        public CurrentLocationServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getCurrentLocation(String Latitude,String Longitude ,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {

            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+"/geolocations/find_location_by_coordinates?lat="+Latitude+"&long="+Longitude,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

