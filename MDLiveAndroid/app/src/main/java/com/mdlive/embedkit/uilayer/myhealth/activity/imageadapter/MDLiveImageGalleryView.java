package com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteMedicalServices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to manipulate CRUD (Create, Read, Update, Delete) function for Allergies.
 *
 * This class extends with MDLiveCommonConditionsMedicationsActivity
 *  which has all functions that is helped to achieve CRUD functions.
 *
 */

public class MDLiveImageGalleryView extends Activity {

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Allergy
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_image_galleryview);
        pDialog = Utils.getProgressDialog("Please wait...", this);

        ((TextView) findViewById(R.id.doneText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.deleteImageText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMedicalRecordService();
            }
        });
        ((TextView) findViewById(R.id.uploadText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ((TextView) findViewById(R.id.imageNameText)).setText(getIntent().getStringExtra("doc_name"));

        ((NetworkImageView) findViewById(R.id.galleryImageView)).setImageUrl(getIntent().getStringExtra("download_link"),
                ApplicationController.getInstance().getImageLoader(getApplicationContext()));
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void deleteMedicalRecordService() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response != null){
                        if(response.has("message")){
                            if(response.getString("message").equals("Customer document deleted successfully")){
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        DeleteMedicalServices services = new DeleteMedicalServices(MDLiveImageGalleryView.this, null);
        services.deleteAllergyRequest(successCallBackListener, errorListener, getIntent().getStringExtra("id"));
    }



    /**
     * Error Response Handler for Medical History Completion.
     */
    private void medicalCommonErrorResponseHandler(VolleyError error) {
        pDialog.dismiss();
        if (error.networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                // Show timeout error message
                Utils.connectionTimeoutError(pDialog, MDLiveImageGalleryView.this);
            }
        }
    }

}