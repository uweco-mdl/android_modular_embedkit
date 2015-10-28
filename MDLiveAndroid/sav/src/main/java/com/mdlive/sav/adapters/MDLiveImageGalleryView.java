package com.mdlive.sav.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.DeleteMedicalServices;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to display Medical history image in large view
 * Zoom In/Out features added to Medical history image
 * Delete photo feature is added with this class.
 */

public class MDLiveImageGalleryView extends MDLiveBaseActivity {

    private RelativeLayout progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Allergy
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_image_galleryview);
        progressBar = (RelativeLayout) findViewById(R.id.progressDialog);
        ((TextView) findViewById(R.id.imageNameText)).setText(StringConstants.EMPTY_STRING);
        if (getDatasInVolleyCache(getIntent().getIntExtra("id", 0) + "") != null) {
            byte[] decodedString = getDatasInVolleyCache(getIntent().getIntExtra("id", 0) + "").data;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
            if (decodedByte != null)
                ((ImageView) findViewById(R.id.galleryImageView)).setImageBitmap(decodedByte);
        }
    }

    /**
     * Handling doneButton onclick event.
     */
    public void doneTxtClick(View view){
        onBackPressed();
    }
    /**
     * This method is belongs to deleteImage button Onclick event
     * while user clicks on deleteImage it will call to deleteMedicalRecordService.
     */
    public void deleteImageTextClick(View view){
        deleteMedicalRecordService();
    }

    /**
     * This function is used to get Cache Entry data from Volley Cache.
     * Each entry has byte data of image to be displayed in Gallery View.
     */
    public Cache.Entry getDatasInVolleyCache(String photoId) {
        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveImageGalleryView.this).getCache();
        Cache.Entry entry = new Cache.Entry();
        entry = cache.get(photoId + "");
        return entry;
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void deleteMedicalRecordService() {
        progressBar.setVisibility(View.VISIBLE);
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    if (response != null) {
                        if (response.has("message")) {
                            if (response.getString("message").equals("Customer document deleted successfully")) {
                                ApplicationController.getInstance().getBitmapLruCache().remove(getIntent().getIntExtra("id", 0) + "");
                                ApplicationController.getInstance().getRequestQueue(MDLiveImageGalleryView.this).getCache().remove(getIntent().getIntExtra("id", 0) + "");
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
        progressBar.setVisibility(View.GONE);
        MdliveUtils.handelVolleyErrorResponse(MDLiveImageGalleryView.this, error, getProgressDialog());
    }


    /**
     * This method will close the activity with transition effect.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(this);
    }

    /**
     * This method will stop the service call if activity is closed during service call.
     */
    @Override
    public void onStop() {
        super.onStop();
    }
}
