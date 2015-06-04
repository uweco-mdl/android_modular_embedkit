package com.mdlive.embedkit.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

/**
 * This is a webservice class to delete medication details from list
 */
public class DeleteMedicationService extends BaseServicesPlugin {

    public DeleteMedicationService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }

    /**
     * @param responseListener
     * @param errorListener
     */

    public void doLoginRequest(String postBody, String id, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            jsonObjectDeleteRequest(AppSpecificConfig.BASE_URL+ AppSpecificConfig.URL_MEDICATION_DELETE+"/"+id,
                    postBody, responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
