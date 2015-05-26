package com.mdlive.mobile.uilayer.sav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.mobile.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalisationHelper;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ProviderDetailServices;

import org.json.JSONObject;

/**
 * Created by sudha_s on 5/21/2015.
 */
public class MDLiveProviderDetails extends Activity implements View.OnClickListener{
    private ProgressDialog pDialog;
    private TextView aboutme_txt,specialities_txt,license_txt,location_txt,lang_txt,DoctorName_txt,specialist_txt;
    private NetworkImageView ProfileImg;
    private String DoctorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_provider_details);
        pDialog = Utils.getProgressDialog(LocalisationHelper.getLocalizedStringFromPrefs(this, getResources().getString(R.string.please_wait)), this);
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
         DoctorId = settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null);
        Initialization();
        //Service call Method
        loadProviderDetails();


    }

    public void Initialization()
    {
        aboutme_txt = (TextView)findViewById(R.id.aboutMe_txt);
        specialities_txt = (TextView)findViewById(R.id.specialities_txt);
        license_txt = (TextView)findViewById(R.id.license_txt);
        location_txt = (TextView)findViewById(R.id.provider_location_txt);
        lang_txt = (TextView)findViewById(R.id.provider_lang_txt);
        DoctorName_txt = (TextView)findViewById(R.id.DoctorName);
        specialist_txt = (TextView)findViewById(R.id.specalist);
        Button SearchBtn = (Button) findViewById(R.id.reqappointmentBtn);
        SearchBtn.setOnClickListener(this);
        ProfileImg = (NetworkImageView)findViewById(R.id.ProfileImg1);
        ProfileImg.setImageUrl("https://rtl.mdlive.com/user/photo/67/original_twilson_150x150.jpg", ApplicationController.getInstance().getImageLoader());



    }
    /**
     *
     * Load user information Details.
     * Class : loadProviderDetails - Service class used to fetch the Provider's information
     *
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     *
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadProviderDetails() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                pDialog.dismiss();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveProviderDetails.this);
                    }
                }
            }};
        ProviderDetailServices services = new ProviderDetailServices(MDLiveProviderDetails.this, null);
        services.getProviderDetails(DoctorId,successCallBackListener, errorListener);
    }

    /**
     *
     *  Successful Response Handler for Load Provider Info
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services

            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            JsonObject profileobj = responObj.get("doctor_profile").getAsJsonObject();
            JsonObject providerdetObj = profileobj.get("provider_details").getAsJsonObject();
            //Doctor Name
            String str_DoctorName = providerdetObj.get("name").getAsString();
            String str_Location = providerdetObj.get("location").getAsString();
            String str_AboutMe = providerdetObj.get("about_me").getAsString();
            String str_ProfileImg = providerdetObj.get("provider_image_url").getAsString();

            DoctorName_txt.setText(str_DoctorName);
            aboutme_txt.setText(str_AboutMe);
            location_txt.setText(str_Location);
            String license_state = "";
            //License Array
            JsonArray responArray = providerdetObj.get("provider_licenses").getAsJsonArray();
            for(int i=0;i<responArray.size();i++)
            {
                JsonObject licenseObject = responArray.get(i).getAsJsonObject();
                license_state += "\u2022"+" "+licenseObject.get("state").getAsString()+"\n";
                license_txt.setText(license_state);
                String license_number = licenseObject.get("license_number").getAsString();
            }

            //Language Array
            String lang = "";
            JsonArray langArray = providerdetObj.get("Language").getAsJsonArray();
            for(int i=0;i<langArray.size();i++)
            {
                lang+="\u2022"+" "+langArray.get(i).toString().substring(1,langArray.get(i).toString().length()-1)+"\n";
                lang_txt.setText(lang);
                Log.e("Lang Details--->", langArray.get(i).toString());

            }

            //Specialities Array
            String specialities = "";
            JsonArray specialityArray = providerdetObj.get("speciality_qualifications").getAsJsonArray();
            for(int i=0;i<specialityArray.size();i++)
            {
                specialities+= "\u2022"+" "+specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1)+"\n";
                specialities_txt.setText(specialities);
                Log.e("Lang Details--->", specialityArray.get(i).toString());

            }





        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.reqappointmentBtn:
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                finish();
                break;
        }

    }
}
