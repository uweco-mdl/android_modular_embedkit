package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.embedkit.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.embedkit.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * This is a webservice class to add medication results of users
 */
public class AllergiesUpdateServices extends BaseServicesPlugin {


    public AllergiesUpdateServices(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }

    /**
     * @param responseListener
     * @param errorListener
     */

    public void updateAllergyRequest(String conditionId, String postBody, NetworkSuccessListener<JSONObject> responseListener, NetworkErrorListener errorListener){
        try {
            Log.e("Post Body", postBody);
            jsonObjectPutRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.URL_ALLERGY_LIST + "/" + conditionId,
                    postBody, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
