package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sudha_s on 5/15/2015.
 */
public class CurrentLocationServices extends BaseServicesPlugin

    {

        public CurrentLocationServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getCurrentLocation(String Latitude,String Longitude ,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {

            jsonObjectPostRequest("https://stage-rtl.mdlive.com/services/geolocations/find_location_by_coordinates?lat="+Latitude+"&long="+Longitude, null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

