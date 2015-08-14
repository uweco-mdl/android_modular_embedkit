package com.mdlive.embedkit.uilayer.sav;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailServices;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class returns the Provider profile for the corresponding providers.Along with that
 * the provider profile the details of the provider has also be defined.The Qualification of
 * the provider like Specalties, about the provider,languages has been defined.
 */
public class MDLiveProviderDetails extends MDLiveBaseActivity{
    private TextView aboutme_txt,education_txt,specialities_txt, hospitalAffilations_txt,location_txt,lang_txt, doctorNameTv,detailsGroupAffiliations;
    private CircularNetworkImageView ProfileImg;
    public String DoctorId;
    private Button tapSeetheDoctorTxt;
    private String SharedLocation,AppointmentDate,AppointmentType,groupAffiliations;
    private LinearLayout providerImageHolder,detailsLl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_choose_provider_details);
        Initialization();
        getPreferenceDetails();
        //Service call Method
        loadProviderDetails();
    }
    /**
     * Retrieve the shared data from preferences for Provider Id and Location.The Provider id and
     * the Location can be fetched from the GetStarted screen and the provider's Id and the
     * Provider's Appointment date will be fetched from the Choose Provider Screen.Here the Appointment
     * type will be 3 because we will be using only the video type so the appointment type will
     * be video by default.
     *
     */

    private void getPreferenceDetails() {
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        DoctorId = settings.getString(PreferenceConstants.PROVIDER_DOCTORID_PREFERENCES, null);
        SharedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
        AppointmentDate = settings.getString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, null);
        groupAffiliations = settings.getString(PreferenceConstants.PROVIDER_GROUP_AFFILIATIONS_PREFERENCES, null);
        detailsGroupAffiliations.setText(groupAffiliations);
        AppointmentType = StringConstants.APPOINTMENT_TYPE;
        if(AppointmentDate!=null && AppointmentDate.length() != 0){
            AppointmentDate = MdliveUtils.getFormattedDate(AppointmentDate);
        }
    }

    /**
     *
     * The initialization of the views was done here.All the labels was defined here and
     * the click event for the back button and the home button was done here.
     * On clicking the back button image will be finishing the current Activity
     * and on clicking the Home button you will be navigated to the SSo Screen with
     * an alert.
     *
     * **/

    public void Initialization()
    {
        aboutme_txt = (TextView)findViewById(R.id.aboutMe_txt);
        education_txt = (TextView)findViewById(R.id.education_txt);
        specialities_txt = (TextView)findViewById(R.id.specialities_txt);
        hospitalAffilations_txt = (TextView)findViewById(R.id.license_txt);
        location_txt = (TextView)findViewById(R.id.provider_location_txt);
        lang_txt = (TextView)findViewById(R.id.provider_lang_txt);
        tapSeetheDoctorTxt = (Button)findViewById(R.id.tapBtn);
        doctorNameTv = (TextView)findViewById(R.id.DoctorName);
        ProfileImg = (CircularNetworkImageView)findViewById(R.id.ProfileImg1);
        providerImageHolder = (LinearLayout) findViewById(R.id.providerImageHolder);
        detailsGroupAffiliations = (TextView) findViewById(R.id.detailsGroupAffiliations);
        detailsLl = (LinearLayout) findViewById(R.id.detailsLl);
        setProgressBar(findViewById(R.id.progressDialog));

    /**
     * The back image will pull you back to the Previous activity
     * The tap button will pull you  to the Reason for visit Screen.
     */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveProviderDetails.this);
                onBackPressed();
            }
        });
    }
    /**
     * LProviderDetailServices
      to user or Get started screen will shown to user).
     */
    public void detailsTapBtnAction(View view)
    {
        Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
        startActivity(Reasonintent);
        MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
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
        showProgress();
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
                hideProgress();
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
                        MdliveUtils.connectionTimeoutError(null, MDLiveProviderDetails.this);
                    }
                }
            }};
        ProviderDetailServices services = new ProviderDetailServices(MDLiveProviderDetails.this, null);
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        AppointmentDate = format.format(new Date());
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
            hideProgress();
            //Fetch Data From the Services
            Log.e("Response pdetails",response.toString());
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
            if(str_Availability_Type.equals(getString(R.string.with_patient)))
            {
                tapSeetheDoctorTxt.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tapSeetheDoctorTxt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
                } else {
                    tapSeetheDoctorTxt.setBackgroundResource(R.drawable.btn_rounded_grey);
                }
                tapSeetheDoctorTxt.setText(getString(R.string.currently_with_patient));
            }

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
                if(!str_Availability_Type.equals(getString(R.string.with_patient)))
                   tapSeetheDoctorTxt.setText(getString(R.string.providerDeatils_choose_doctor,str_DoctorName));
            }
           if(str_AboutMe.length()!= IntegerConstants.NUMBER_ZERO)
           {
               aboutme_txt.setText(str_AboutMe);
           }else
           {
               ((LinearLayout)findViewById(R.id.aboutmeLl)).setVisibility(View.GONE);
           }
            if(!str_education.equals("") && !str_education.isEmpty()||str_education.length()!=0)
            {
                education_txt.setText(str_education);
            }else
            {
                education_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.educationLl)).setVisibility(View.GONE);
            }

            if(!str_BoardCertifications.equals("")||str_BoardCertifications == null && !str_BoardCertifications.isEmpty()||str_BoardCertifications.length()!=0)
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
     *  This response is for the Affilations Purpose.The image can be placed one below the other
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
            specialities+= "\u2022"+" "+specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1)+"\n";
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
            lang+="\u2022"+" "+langArray.get(i).toString().substring(1,langArray.get(i).toString().length()-1)+"\n";
            if(!lang.equals("")&& !lang.isEmpty()||lang.length()!=IntegerConstants.NUMBER_ZERO)
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
        String hospitalAffilations = "";
        for(int i=0;i<responArray.size();i++)
        {
//            JsonObject licenseObject = responArray.get(i).getAsJsonObject();
//            license_state +=licenseObject.get("state").getAsString()+"\n";

            hospitalAffilations+= responArray.get(i).toString().substring(1,responArray.get(i).toString().length()-1)+"\n";
            if(!hospitalAffilations.equals("")|| !hospitalAffilations.isEmpty()||hospitalAffilations.length()!=0)
            {
                hospitalAffilations_txt.setText(hospitalAffilations);
            }else
            {
                hospitalAffilations_txt.setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.hosaffiliationsLl)).setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveProviderDetails.this);
    }


}
