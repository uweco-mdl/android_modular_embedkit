package com.mdlive.unifiedmiddleware.services.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class ZipCodeServices extends BaseServicesPlugin {

    public ZipCodeServices(Context context, ProgressDialog pDialog) {
        super(context, pDialog);

    }


    public void getZipCodeServices(String getzipvalue, NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener) {
        try {
            Log.e("print Zip code-->", getzipvalue);
            jsonObjectPostRequest(AppSpecificConfig.GEOCODE_API_ENDPOINT + "json?address=" + getzipvalue + "&sensor=false", null, responseListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
