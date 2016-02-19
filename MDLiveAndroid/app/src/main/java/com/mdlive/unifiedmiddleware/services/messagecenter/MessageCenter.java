package com.mdlive.unifiedmiddleware.services.messagecenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageCenter extends BaseServicesPlugin {
    public MessageCenter(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
    }

    public void getReceivedMessages(NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener, int page, int numberOfItems) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_GET_RECEIVED_MESSAGES + "?page=" + page + "&per_page=" + numberOfItems,
                    null, successListener, errorListener, MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSentMessages(NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener, int page, int numberOfItems) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_GET_SENT_MESSAGES + "?page=" + page + "&per_page=" + numberOfItems,
                    null, successListener, errorListener, MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProvider(NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_PROVIDER, null, successListener, errorListener, MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postMessage(NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener, String params) {
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_COMPOSE_MESSAGE, params, successListener, errorListener, MDLiveConfig.IS_SSO);
            Log.d("Test", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMyRecords(NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MY_RECORDS, null, successListener, errorListener, MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadDocument(NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener, String params) {
        try {
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_UPLOAD_DOCUMENT,
                    params,
                    successListener,
                    errorListener,
                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessaseReceivedRead(String id, NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener, String params) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MESSAGES_READ + id + AppSpecificConfig.URL_MESSAGES_READ_TYPE + "received",
                    params,
                    successListener,
                    errorListener,
                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessaseSentRead(String id, NetworkSuccessListener<JSONObject> successListener, NetworkErrorListener errorListener, String params) {
        try {
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MESSAGES_READ + id + AppSpecificConfig.URL_MESSAGES_READ_TYPE + "sent",
                    params,
                    successListener,
                    errorListener,
                    MDLiveConfig.IS_SSO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
