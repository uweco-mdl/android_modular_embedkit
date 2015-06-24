package com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.R;
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

        Log.e("Received Id", getIntent().getIntExtra("id", 0)+"");

            if(Utils.mphotoList.get(getIntent().getIntExtra("id", 0)) != null){
                byte[] decodedString = Base64.decode((String) Utils.mphotoList.get(getIntent().getIntExtra("id", 0)), Base64.DEFAULT);
                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                //        options.inSampleSize = 8;
                options.inSampleSize = 2;

                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

                if(decodedByte != null)
                    //imageView.setImageBitmap(decodedByte);
                    ((ImageView) findViewById(R.id.galleryImageView)).setImageBitmap(decodedByte);
            }

//        decodedByte.recycle();
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
                pDialog.dismiss();
                try {
                    if(response != null){
                        Log.e("Response", response.toString());
                        if(response.has("message")){
                            if(response.getString("message").equals("Customer document deleted successfully")){
                                setResult(RESULT_OK);
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
        services.deleteAllergyRequest(successCallBackListener, errorListener, getIntent().getIntExtra("id", 0));
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
