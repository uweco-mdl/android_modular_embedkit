package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * This is a webservice class to add medication results of users
 */
public class MedicalConditionUpdateServices extends BaseServicesPlugin {


    public MedicalConditionUpdateServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }

    /**
     * @param responseListener
     * @param errorListener
     */

    public void updateConditionRequest(String conditionId, String postBody, NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener){
        try {
            Log.e("Post Body", postBody);
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_MEDICAL_CONDITIONS_LIST + "/" + conditionId,
                    postBody, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
