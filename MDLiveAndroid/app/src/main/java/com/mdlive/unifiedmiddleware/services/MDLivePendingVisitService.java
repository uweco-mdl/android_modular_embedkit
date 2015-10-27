package com.mdlive.unifiedmiddleware.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

public class MDLivePendingVisitService extends BaseServicesPlugin {
    public MDLivePendingVisitService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }

    public void getUserPendingHistory(NetworkSuccessListener successListener,
                                      NetworkErrorListener errorListener){
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PENDING_APPOINTMENT, null, successListener, errorListener);

    }

    public void getUserAggregateInformation(NetworkSuccessListener successListener,
                                            NetworkErrorListener errorListener){
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_USER_INFO,null,successListener,errorListener);

    }

    public void deleteAppointment(final String id,
                                  NetworkSuccessListener successListener,
                                  NetworkErrorListener errorListener){
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL+AppSpecificConfig.URL_USER_INFO + "/" + id,null,successListener,errorListener);

    }
}
