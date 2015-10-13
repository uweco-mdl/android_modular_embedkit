package com.mdlive.unifiedmiddleware.services.myhealth;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class UploadImageService  extends BaseServicesPlugin {

    public UploadImageService(Context context, ProgressDialog pDialog){
        super(context,pDialog);
    }

    /**
     * @param responseListener
     * @param errorListener
     */
    public void doUploadDocumentService(File file, NetworkSuccessListener<JSONObject> responseListener , NetworkErrorListener errorListener){
        try {
            HashMap<String, Object> customer_document = new HashMap<String, Object>();
            HashMap<String, Object> contents = new HashMap<String, Object>();
            String extension = MdliveUtils.getFileExtention(file);
            contents.put("file_type", extension);
            contents.put("file_name", file.getName());
            contents.put("document_type_id", 2);
            contents.put("document", "data:image/" + extension + ";base64," + MdliveUtils.encodeFileToBase64Binary(file, extension));
            customer_document.put("customer_document", contents);
            jsonObjectPostRequest(AppSpecificConfig.BASE_URL + AppSpecificConfig.UPLOAD_MEDICALREPORT,
                    new Gson().toJson(customer_document), responseListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
