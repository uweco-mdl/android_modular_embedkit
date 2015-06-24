package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailServices;

import org.json.JSONObject;

/**
 * This class returns the Provider profile for the corresponding providers.
 */
public class MDLiveProviderDetails extends Activity implements View.OnClickListener{
    private ProgressDialog pDialog;
    private TextView aboutme_txt,specialities_txt,license_txt,location_txt,lang_txt, doctorNameTv,specialist_txt,tapSeetheDoctorTxt;
    private CircularNetworkImageView ProfileImg;
    private NetworkImageView AffilitationProviderImg;
    public String DoctorId;
    private String SharedLocation,AppointmentDate,AppointmentType;
    private LinearLayout providerImageHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_choose_provider_details);
        pDialog = Utils.getProgressDialog("Please wait...", this);
        getPreferenceDetails();
        Initialization();
        //Service call Method
        loadProviderDetails();
    }
    /**
     * Retrieve the shared data from preferences for Provider Id and Location
     *
     */


    private void getPreferenceDetails() {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        DoctorId = settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null);
        SharedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
        AppointmentDate = settings.getString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, null);
        if(AppointmentDate!=null && AppointmentDate.length() != 0){
            AppointmentDate = Utils.getFormattedDate(AppointmentDate);

        }

    }
     /*Initialization of the views were done*/
    public void Initialization()
    {
        aboutme_txt = (TextView)findViewById(R.id.aboutMe_txt);
        specialities_txt = (TextView)findViewById(R.id.specialities_txt);
        license_txt = (TextView)findViewById(R.id.license_txt);
        location_txt = (TextView)findViewById(R.id.provider_location_txt);
        lang_txt = (TextView)findViewById(R.id.provider_lang_txt);
        tapSeetheDoctorTxt = (TextView)findViewById(R.id.tapBtn);
        doctorNameTv = (TextView)findViewById(R.id.DoctorName);
        specialist_txt = (TextView)findViewById(R.id.specalist);
        Button SearchBtn = (Button) findViewById(R.id.reqappointmentBtn);
        SearchBtn.setOnClickListener(this);
        //tapSeetheDoctorTxt.setOnClickListener(this);
        ProfileImg = (CircularNetworkImageView)findViewById(R.id.ProfileImg1);
        providerImageHolder = (LinearLayout) findViewById(R.id.providerImageHolder);

        ((Button) findViewById(R.id.tapBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
            }
        });
        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveProviderDetails.this);
                finish();
            }
        });



    }
    /**
     * Load user information Details.
     * Class : loadProviderDetails - Service class used to fetch the Provider's information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void loadProviderDetails() {
        pDialog.show();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                tapSeetheDoctorTxt.setClickable(true);
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
                        tapSeetheDoctorTxt.setClickable(false);
                        // Show timeout error message
                        Utils.connectionTimeoutError(pDialog, MDLiveProviderDetails.this);
                    }
                }
            }};
        ProviderDetailServices services = new ProviderDetailServices(MDLiveProviderDetails.this, null);
        services.getProviderDetails(SharedLocation,AppointmentDate,AppointmentType,DoctorId,successCallBackListener, errorListener);
    }

    /**
     *  Successful Response Handler for Load Provider Info
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            pDialog.dismiss();
            //Fetch Data From the Services

            Log.e("details-->",response.toString());
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            JsonObject profileobj = responObj.get("doctor_profile").getAsJsonObject();
            JsonObject providerdetObj = profileobj.get("provider_details").getAsJsonObject();
            //Doctor Name
            String str_DoctorName = providerdetObj.get("name").getAsString();
            String str_Location = providerdetObj.get("location").getAsString();
            String str_AboutMe = providerdetObj.get("about_me").getAsString();
            String str_ProfileImg = providerdetObj.get("provider_image_url").getAsString();
            ProfileImg.setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));
            ProfileImg.setDefaultImageResId(R.drawable.doctor_icon);
            ProfileImg.setErrorImageResId(R.drawable.doctor_icon);
            doctorNameTv.setText(str_DoctorName);
            tapSeetheDoctorTxt.setText("Choose "+str_DoctorName);
            aboutme_txt.setText(str_AboutMe);
            location_txt.setText(str_Location);
            String license_state = "";
            //License Array
            getLicenseArrayResponse(providerdetObj, license_state);

            //Language Array
            getLanguageArrayResponse(providerdetObj);

            //Specialities Array
            getSpecialitiesArrayResponse(providerdetObj);

            //Provider Image Array
            getProviderImageArrayResponse(providerdetObj);


        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *  Successful Response Handler for getting the Affillitations and the provider image.
     *
     */
    private void getProviderImageArrayResponse(JsonObject providerdetObj) {
        String ProviderImage = "";
        JsonArray ProviderImageArray = providerdetObj.get("provider_groups").getAsJsonArray();
        Log.e("Size", ProviderImageArray.size() + "");
        providerImageHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        for(int i=0;i<ProviderImageArray.size();i++)
        {
            NetworkImageView imageView = new NetworkImageView(getApplicationContext());
            imageView.setImageUrl(ProviderImageArray.get(i).getAsJsonObject().get("logo").getAsString(),
                    ApplicationController.getInstance().getImageLoader(getApplicationContext()));
            imageView.setLayoutParams(params);
            providerImageHolder.addView(imageView);

        }
    }
    /**
     *  Response Handler for getting the Speciality and this is completely depend upon the provider type.
     *
     */

    private void getSpecialitiesArrayResponse(JsonObject providerdetObj) {
        String specialities = "";
        JsonArray specialityArray = providerdetObj.get("speciality_qualifications").getAsJsonArray();
        for(int i=0;i<specialityArray.size();i++)
        {
            specialities+= specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1)+"\n";
            specialities_txt.setText(specialities);
            Log.e("Lang Details--->", specialityArray.get(i).toString());

        }
    }
    /**
     *  Response Handler for getting the languages , what the provider speaks.
     *
     */

    private void getLanguageArrayResponse(JsonObject providerdetObj) {
        String lang = "";
        JsonArray langArray = providerdetObj.get("Language").getAsJsonArray();
        for(int i=0;i<langArray.size();i++)
        {
            lang+=langArray.get(i).toString().substring(1,langArray.get(i).toString().length()-1)+"\n";
            lang_txt.setText(lang);
            Log.e("Lang Details--->", langArray.get(i).toString());

        }
    }
    /**
     *  Response Handler for getting the license.
     *
     */

    private void getLicenseArrayResponse(JsonObject providerdetObj, String license_state) {
        JsonArray responArray = providerdetObj.get("provider_licenses").getAsJsonArray();
        for(int i=0;i<responArray.size();i++)
        {
            JsonObject licenseObject = responArray.get(i).getAsJsonObject();
            license_state +=licenseObject.get("state").getAsString()+"\n";
            license_txt.setText(license_state);
            String license_number = licenseObject.get("license_number").getAsString();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
         /*   case R.id.tapBtn:
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                break;
        */
        }

    }
}
