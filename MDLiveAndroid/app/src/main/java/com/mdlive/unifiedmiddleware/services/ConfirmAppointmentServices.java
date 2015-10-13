package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

public class ConfirmAppointmentServices extends BaseServicesPlugin{
    public ConfirmAppointmentServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);

    }



    public void doConfirmAppointment(String params, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            Log.d("Postparams",params);
            Log.e("req Url",(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_CONFIRM_APPT));
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_CONFIRM_APPT, params, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void doGetPromocode(String promocode, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            //https://api.mdlive.com/services/consultation_charges/get_promocode_details?promocode=XXXXX
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_GET_PROMOCODE+promocode,null,responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void doUpdateBillingInformation(String params, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        Log.e("req Url",(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_BILLING_UPDATE));
        Log.e("post val",params);
        // https://dev-members.mdlive.com/services/billing_informations/1
//        jsonObjectPutRequest("https://dev-members.mdlive.com/services/billing_informations/1",params,responseListener,errorListener);
        jsonObjectPutRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_BILLING_UPDATE,params,responseListener,errorListener);
    }
}
