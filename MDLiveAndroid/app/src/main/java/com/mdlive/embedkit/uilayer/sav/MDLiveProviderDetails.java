package com.mdlive.embedkit.uilayer.sav;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailServices;

import org.json.JSONObject;

/**
 * This class returns the Provider profile for the corresponding providers.
 */
public class MDLiveProviderDetails extends MDLiveBaseActivity{
    private ProgressDialog pDialog;
    private TextView aboutme_txt,education_txt,specialities_txt, hospitalAffilations_txt,location_txt,lang_txt, doctorNameTv,specialist_txt,withpatientTxt;
    private CircularNetworkImageView ProfileImg;
    private NetworkImageView AffilitationProviderImg;
    public String DoctorId;
    private Button tapSeetheDoctorTxt;
    private RelativeLayout progressBar;
    private String SharedLocation,AppointmentDate,AppointmentType;
    private LinearLayout providerImageHolder,detailsLl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_choose_provider_details);
//        pDialog = Utils.getProgressDialog("Please wait...", this);
        getPreferenceDetails();
        Initialization();
        //Service call Method
        loadProviderDetails();
    }
    /**
     * Retrieve the shared data from preferences for Provider Id and Location.The Provider id and
     * the Location can be fetched from the GetStarted screen and the provider's Id and the
     * Provider's Appointment date will be fetched from the Choose Provider Screen.
     *
     */

    private void getPreferenceDetails() {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        DoctorId = settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null);
        SharedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
        AppointmentDate = settings.getString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, null);
        if(AppointmentDate!=null && AppointmentDate.length() != 0){
            AppointmentDate = MdliveUtils.getFormattedDate(AppointmentDate);

        }

    }
     /**
      * Initialization of the views are done here.
      * The click event of the particular view also are done here.
      **/

    public void Initialization()
    {
        aboutme_txt = (TextView)findViewById(R.id.aboutMe_txt);
        education_txt = (TextView)findViewById(R.id.education_txt);
        specialities_txt = (TextView)findViewById(R.id.specialities_txt);
        hospitalAffilations_txt = (TextView)findViewById(R.id.license_txt);
        location_txt = (TextView)findViewById(R.id.provider_location_txt);
        lang_txt = (TextView)findViewById(R.id.provider_lang_txt);
        tapSeetheDoctorTxt = (Button)findViewById(R.id.tapBtn);
        progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        doctorNameTv = (TextView)findViewById(R.id.DoctorName);
        specialist_txt = (TextView)findViewById(R.id.specalist);
        withpatientTxt = (TextView)findViewById(R.id.withpatientTxt);
        Button SearchBtn = (Button) findViewById(R.id.reqappointmentBtn);
        ProfileImg = (CircularNetworkImageView)findViewById(R.id.ProfileImg1);
        providerImageHolder = (LinearLayout) findViewById(R.id.providerImageHolder);
        detailsLl = (LinearLayout) findViewById(R.id.detailsLl);
    /**
     * The back image will pull you back to the Previous activity
     * The tap button will pull you  to the Reason for visit Screen.
     */
        ((Button) findViewById(R.id.tapBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
            }
        });
        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveProviderDetails.this);
                finish();
            }
        });
    }
    /**
     * LProviderDetailServices
     * Class : ProviderDetailServices - Service class used to fetch the Provider's information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to
     * the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error
     * message to user or Get started screen will shown to user).
     */
    private void loadProviderDetails() {
//        pDialog.show();
        progressBar.setVisibility(View.VISIBLE);
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
//                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                tapSeetheDoctorTxt.setClickable(false);
                detailsLl.setVisibility(View.GONE);
                tapSeetheDoctorTxt.setVisibility(View.GONE);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        tapSeetheDoctorTxt.setClickable(false);
                        tapSeetheDoctorTxt.setVisibility(View.GONE);
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, MDLiveProviderDetails.this);
                    }
                }
            }};
        ProviderDetailServices services = new ProviderDetailServices(MDLiveProviderDetails.this, pDialog);
        services.getProviderDetails(SharedLocation,AppointmentDate,AppointmentType,DoctorId,successCallBackListener, errorListener);
    }

    /**
     *  Successful Response Handler for Load Provider Info.Here the Profile image of
     *  the Particular provider will be displayed.Along with that the Provider's
     *  speciality,about the Provider , license and the languages will also be
     *  displayed.Along with this Provider's affilitations will also be displayed
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
//            pDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            //Fetch Data From the Services
            Log.e("details-->",response.toString());
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            JsonObject profileobj = responObj.get("doctor_profile").getAsJsonObject();
            JsonObject providerdetObj = profileobj.get("provider_details").getAsJsonObject();
            //Doctor Name
            String str_DoctorName ="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "name")) {
                 str_DoctorName = providerdetObj.get("name").getAsString();
            }
            String str_BoardCertifications="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "board_certifications")) {
                 str_BoardCertifications = providerdetObj.get("board_certifications").getAsString();
            }
            String str_AboutMe="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "about_me"))
            {
                 str_AboutMe = providerdetObj.get("about_me").getAsString();
            }
            String str_ProfileImg="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "provider_image_url")) {
                 str_ProfileImg = providerdetObj.get("provider_image_url").getAsString();
            }
            String str_Availability_Type="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "availability_type")) {
                 str_Availability_Type = providerdetObj.get("availability_type").getAsString();
            }
            String str_education="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "education")) {
                str_education = providerdetObj.get("education").getAsString();
            }
            if(str_Availability_Type.equals("Available now"))
            {
//                withpatientTxt.setText(str_Availability_Type);
//                withpatientTxt.setTextColor(Color.parseColor("#31B404"));
                withpatientTxt.setVisibility(View.GONE);

            }
            else if(str_Availability_Type.equals("With Patient"))
            {
                withpatientTxt.setText(str_Availability_Type);
                withpatientTxt.setTextColor(Color.parseColor("#F18032"));
                tapSeetheDoctorTxt.setClickable(false);
                tapSeetheDoctorTxt.setBackgroundColor(getResources().getColor(R.color.search_bgd));
                tapSeetheDoctorTxt.setText("Currently with patient");
            }
            else if(str_Availability_Type.equals("not available"))
            {
                if(!str_Availability_Type.equals("With Patient"))
                    withpatientTxt.setVisibility(View.GONE);
            }

            Log.e("str_Availability_type",str_Availability_Type);

            ProfileImg.setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));
            ProfileImg.setDefaultImageResId(R.drawable.doctor_icon);
            ProfileImg.setErrorImageResId(R.drawable.doctor_icon);
            doctorNameTv.setText(str_DoctorName);
            if(str_DoctorName.equals(""))
            {
                tapSeetheDoctorTxt.setClickable(false);
                tapSeetheDoctorTxt.setVisibility(View.GONE);
                detailsLl.setVisibility(View.GONE);

            }else
            {
                if(!str_Availability_Type.equals("With Patient"))
                   tapSeetheDoctorTxt.setText("Choose "+str_DoctorName);
            }
            Log.e("About me",str_AboutMe.length()+"");
           if(str_AboutMe.length()!=0)
           {
               aboutme_txt.setText(str_AboutMe);
           }else
           {
              Log.e("str_AboutMe-->",str_AboutMe);

               ((LinearLayout)findViewById(R.id.aboutmeLl)).setVisibility(View.GONE);
           }
            if(!str_education.equals("")||str_education != null && !str_education.isEmpty()||str_education.length()!=0)
            {
                education_txt.setText(str_education);
            }else
            {
                education_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.educationLl)).setVisibility(View.GONE);
            }

            if(!str_BoardCertifications.equals("")||str_BoardCertifications != null && !str_BoardCertifications.isEmpty()||str_BoardCertifications.length()!=0)
            {
                location_txt.setText(str_BoardCertifications);
            }else
            {
                location_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.boardCertificationsLl)).setVisibility(View.GONE);
            }

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
     *  This method will give the successful response of the Provider's affilitations.
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
     *  Here the Provider type can either be family physician or Pediatrician.so based on this type
     *  the Speciality data will be populated .
     *
     */

    private void getSpecialitiesArrayResponse(JsonObject providerdetObj) {
        String specialities = "";
        JsonArray specialityArray = providerdetObj.get("speciality_qualifications").getAsJsonArray();
        for(int i=0;i<specialityArray.size();i++)
        {
            specialities+= specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1)+"\n";
            if(!specialities.equals("")||specialities.length()!=0)
            {
                specialities_txt.setText(specialities);
            }else
            {
                specialities_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.specialitiesLl)).setVisibility(View.GONE);
            }

        }
    }
    /**
     *  Response Handler for getting the languages , what the provider speaks.
     *  This method returns what the Provider speaks .The speaks will be populated in the arraylist
     *  and it will be loaded in the TextView.
     *
     */

    private void getLanguageArrayResponse(JsonObject providerdetObj) {
        String lang = "";
        JsonArray langArray = providerdetObj.get("Language").getAsJsonArray();
        for(int i=0;i<langArray.size();i++)
        {
            lang+=langArray.get(i).toString().substring(1,langArray.get(i).toString().length()-1)+"\n";
            if(!lang.equals("")||lang != null && !lang.isEmpty()||lang.length()!=0)
            {
                lang_txt.setText(lang);
            }else
            {
                lang_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.languagesLl)).setVisibility(View.GONE);
            }

        }
    }
    /**
     *  Response Handler for getting the license.
     *  This method returns the successful response of the Provider for the corresponding
     *  Providers.
     *
     */

    private void getLicenseArrayResponse(JsonObject providerdetObj, String license_state) {
        JsonArray responArray = providerdetObj.get("provider_affiliations").getAsJsonArray();
        for(int i=0;i<responArray.size();i++)
        {
//            JsonObject licenseObject = responArray.get(i).getAsJsonObject();
//            license_state +=licenseObject.get("state").getAsString()+"\n";
            String hospitalAffilations = "";
            hospitalAffilations+= responArray.get(i).toString().substring(1,responArray.get(i).toString().length()-1)+"\n";
            if(!hospitalAffilations.equals("")||hospitalAffilations != null || !hospitalAffilations.isEmpty()||hospitalAffilations.length()!=0)
            {
                hospitalAffilations_txt.setText(hospitalAffilations);
            }else
            {
                hospitalAffilations_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.hosaffiliationsLl)).setVisibility(View.GONE);
            }

//            String license_number = licenseObject.get("license_number").getAsString();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveProviderDetails.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //ApplicationController.getInstance().cancelPendingRequests(ApplicationController.TAG);
    }
}
