package com.mdlive.sav.WaitingRoom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;

import org.json.JSONObject;

/**
 * Created by prabhu_a on 6/1/2015.
 */
public class WaitingRoomService extends BaseServicesPlugin {
    Context context;
    public WaitingRoomService(Context context, ProgressDialog pDialog) {
        super(context, pDialog);
        this.context = context;
    }
    public void doGetProviderStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        //https://stage-rtl.mdlive.com/services/consultations/provider_status/40071
        //jsonObjectGetRequest("https://stage-rtl.mdlive.com/services/consultations/provider_status/40071",null,responseListener,errorListener);
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String apptId = sharedpreferences.getString(PreferenceConstants.APPT_ID, "");
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_WAITING_ROOM + apptId,
                            null,
                            responseListener,
                            errorListener,
                            MDLiveConfig.IS_SSO);
    }

    public void doPostVseeCredentials(String postBody,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String apptId = sharedpreferences.getString(PreferenceConstants.APPT_ID, "");
        jsonObjectPostRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_WAITING_ROOM_VSEE+apptId,
                                postBody,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);

    }

    public void doPutCallBackRequest(String url,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        jsonObjectPutRequest(url, null, responseListener, errorListener, MDLiveConfig.IS_SSO);

    }
    public void doGetOnCallProviderStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        String apptId = sharedpreferences.getString(PreferenceConstants.APPT_ID, "");
        String jointUrl=AppSpecificConfig.URL_WAITING_ONCALL_STATUS.replace(":id",apptId);
        jsonObjectGetRequest(AppSpecificConfig.BASE_URL + jointUrl,
                                null,
                                responseListener,
                                errorListener,
                                MDLiveConfig.IS_SSO);
    }
}
