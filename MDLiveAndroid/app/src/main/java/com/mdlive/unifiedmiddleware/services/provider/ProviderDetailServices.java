package com.mdlive.unifiedmiddleware.services.provider;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;
public class ProviderDetailServices extends BaseServicesPlugin {

    public ProviderDetailServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }
    public void getProviderDetails(String locatedIn, String appointementDate, String appointmentType,
                                   String DoctorId, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Log.v("appmtDate",appointementDate);
            Log.v("providerdetailsUrl",AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_DETAILS + DoctorId + "?appointment_date=" + appointementDate + "&appointment_type=" + appointmentType + "&located_in=" + locatedIn);
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_DETAILS + DoctorId + "?appointment_date=" + appointementDate + "&appointment_type=" + appointmentType + "&located_in=" + locatedIn, null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getProviderDetails(String DoctorId, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER_DETAILS + DoctorId, null, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}