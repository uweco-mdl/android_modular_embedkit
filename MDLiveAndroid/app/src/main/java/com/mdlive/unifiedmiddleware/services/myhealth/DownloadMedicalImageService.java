package com.mdlive.unifiedmiddleware.services.myhealth;

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
 * This is a webservice class to get suggestion results of pharmacies
 */
public class DownloadMedicalImageService extends BaseServicesPlugin {
    public DownloadMedicalImageService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }
    /**
     * @param responseListener
     * @param errorListener
     */
    public void doDownloadImagesRequest(int photoId, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            //Log.e("Url for image--->", AppSpecificConfig.BASE_URL + AppSpecificConfig.DOWNLOAD_MEDICAL_IMAGE+"/"+photoId);
            jsonObjectGetRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.DOWNLOAD_MEDICAL_IMAGE + "/" + photoId,
                                    null,
                                    responseListener,
                                    errorListener,
                                    MDLiveConfig.IS_SSO);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
