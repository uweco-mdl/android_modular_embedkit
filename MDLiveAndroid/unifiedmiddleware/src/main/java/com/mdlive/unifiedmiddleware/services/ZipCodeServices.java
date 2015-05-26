package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by sudha_s on 5/14/2015.
 */
public class ZipCodeServices extends BaseServicesPlugin

    {

        public ZipCodeServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void getZipCodeServices(String getzipvalue ,NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Log.e("print Zip code-->",getzipvalue);
            jsonObjectPostRequest("http://maps.googleapis.com/maps/api/geocode/json?address="+getzipvalue+"&sensor=false", null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
