package com.mdlive.unifiedmiddleware.services.userinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;

import org.json.JSONObject;

import java.util.HashMap;

public class SummaryService extends BaseServicesPlugin {
        Context context;
        public SummaryService(Context context, ProgressDialog pDialog) {
            super(context, pDialog);
            this.context = context;
        }
        public void sendRating(String rating,String comment, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
            SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            String apptId = sharedpreferences.getString(PreferenceConstants.APPT_ID, "");
            HashMap<String,String> postParams = new HashMap<String,String>();
            postParams.put("cust_appointment_id",apptId);
            postParams.put("rating",rating);
            postParams.put("comment",comment);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_RATINGS,
                                    new Gson().toJson(postParams),
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);
        }

    }
