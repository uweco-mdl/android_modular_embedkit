package com.mdlive.embedkit.uilayer.sav;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
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
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.util.Calendar.MONTH;

/**
 * This class returns the Provider profile for the corresponding providers.Along with that
 * the provider profile the details of the provider has also be defined.The Qualification of
 * the provider like Specalties, about the provider,languages has been defined.
 */
public class MDLiveProviderDetails extends MDLiveBaseActivity{
    private TextView aboutme_txt,education_txt,specialities_txt, hospitalAffilations_txt,location_txt,lang_txt, doctorNameTv,detailsGroupAffiliations;
    private CircularNetworkImageView ProfileImg;
    public String DoctorId;
    private TextView byvideoBtn,byphoneBtn,reqfutureapptBtn, tapSeetheDoctorTxt;
    private LinearLayout byvideoBtnLayout, byphoneBtnLayout, tapBtnLayout;
    private RelativeLayout reqfutureapptBtnLayout;
    private String SharedLocation,AppointmentDate,AppointmentType,groupAffiliations,updatedAppointmentDate;
    private LinearLayout providerImageHolder,detailsLl;
    private HorizontalScrollView horizontalscrollview;
    private int month, day, year;
    private static final int DATE_PICKER_ID = IdConstants.SEARCHPROVIDER_DATEPICKER;
    private ArrayList<HashMap<String, String>> timeSlotListMap = new ArrayList<HashMap<String, String>>();
    private ArrayList<String> timeSlotList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_choose_provider_details);

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.doctor_details));


        Initialization();
        getPreferenceDetails();
        //Service call Method
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        AppointmentDate = format.format(new Date());
        loadProviderDetails(AppointmentDate);
        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveProviderDetails.this);
        onBackPressed();
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
        tapSeetheDoctorTxt = (TextView)findViewById(R.id.tapBtn);
        tapBtnLayout = (LinearLayout) findViewById(R.id.tapBtnLayout);
        byvideoBtn = (TextView)findViewById(R.id.byvideoBtn);
        byvideoBtnLayout = (LinearLayout) findViewById(R.id.byvideoBtnLayout);
        byphoneBtn = (TextView)findViewById(R.id.byphoneBtn);
        byphoneBtnLayout = (LinearLayout) findViewById(R.id.byphoneBtnLayout);
        reqfutureapptBtn = (TextView)findViewById(R.id.reqfutureapptBtn);
        reqfutureapptBtnLayout = (RelativeLayout)findViewById(R.id.reqfutureapptBtnLayout);
        doctorNameTv = (TextView)findViewById(R.id.DoctorName);
        ProfileImg = (CircularNetworkImageView)findViewById(R.id.ProfileImg1);
        providerImageHolder = (LinearLayout) findViewById(R.id.providerImageHolder);
        detailsGroupAffiliations = (TextView) findViewById(R.id.detailsGroupAffiliations);
        detailsLl = (LinearLayout) findViewById(R.id.detailsLl);
         horizontalscrollview = (HorizontalScrollView) findViewById(R.id.horizontalscrollview);
        setProgressBar(findViewById(R.id.progressDialog));



    }

    public void onBackBtnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveProviderDetails.this);
        onBackPressed();
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
    private void loadProviderDetails(String AppointmentDate) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                tapBtnLayout.setClickable(true);
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                tapBtnLayout.setClickable(false);
                detailsLl.setVisibility(View.GONE);
                tapBtnLayout.setVisibility(View.GONE);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        tapBtnLayout.setClickable(false);
                        tapBtnLayout.setVisibility(View.GONE);
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveProviderDetails.this);
                    }
                }
            }};
        ProviderDetailServices services = new ProviderDetailServices(MDLiveProviderDetails.this, null);

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

            JsonObject appointment_slot = profileobj.get("appointment_slot").getAsJsonObject();
            JsonArray available_hour = appointment_slot.get("available_hour").getAsJsonArray();

            boolean isDoctorAvailableNow = false, isDoctorWithPatient = false;
            LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
            String str_appointmenttype="",str_timeslot="",str_phys_avail_id="",str_Availability_Type="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "availability_type")) {
                str_Availability_Type = providerdetObj.get("availability_type").getAsString();
            }
          for(int i=0;i<available_hour.size();i++)
          {
              JsonObject availabilityStatus = available_hour.get(i).getAsJsonObject();
             String str_availabilityStatus = "";

              if(MdliveUtils.checkJSONResponseHasString(availabilityStatus, "status")) {
                  str_availabilityStatus = availabilityStatus.get("status").getAsString();
                  if(str_availabilityStatus.equals("Available"))
                  {
                      JsonArray timeSlotArray = availabilityStatus.get("time_slot").getAsJsonArray();
                      for(int j=0;j<timeSlotArray.size();j++) {
                          JsonObject timeSlotObj = timeSlotArray.get(j).getAsJsonObject();


                          if(MdliveUtils.checkJSONResponseHasString(timeSlotObj, "appointment_type")&&MdliveUtils.checkJSONResponseHasString(timeSlotObj, "timeslot")) {
                              str_appointmenttype = timeSlotObj.get("appointment_type").getAsString();
                              str_timeslot = timeSlotObj.get("timeslot").getAsString();
                              if(MdliveUtils.checkJSONResponseHasString(timeSlotObj, "physician_type_id")) {
                                  str_phys_avail_id = timeSlotObj.get("physician_type_id").getAsString();
                              }
                              HashMap<String, String> map = new HashMap<String, String>();
                              map.put("timeslot",str_timeslot);
                              map.put("phys_id", str_phys_avail_id);

                              timeSlotListMap.add(map);
                                if(str_timeslot.equals("0")){
                                    isDoctorAvailableNow = true;
                                    View line = new View(MDLiveProviderDetails.this);
                                    line.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
                                    line.setBackgroundColor(0xAA345556);
                                    TextView myText = new TextView(MDLiveProviderDetails.this);
                                    myText.setTextColor(Color.BLACK);
                                    myText.setTextSize(12);
                                    myText.setPadding(10,5,10,5);
                                    myText.setBackgroundResource(R.drawable.edittext_bg);
                                    myText.setText("Now");
                                    layout.addView(myText, 0);
                                    layout.addView(line, 1);
                                }else {
                                    setHorizontalScrollviewTimeslots(layout, str_timeslot);
                                }

//                              str_phys_avail_id = timeSlotObj.get("phys_availability_id").getAsString();
                              Log.e("timeslot",str_timeslot);
                              //Setting horizontal scroll view for the timeslots
                              //
                          }
                      }
//                      horizontalscrollview.addView(myText);
                  }else if(str_availabilityStatus.equalsIgnoreCase("With patient")){
                      isDoctorWithPatient = true;
                  }
              }
          }

            Log.e("layout.getChildCount()",layout.getChildCount()+"");
            //with patient
            if(isDoctorWithPatient){
                if(layout.getChildCount() > 1)
                {
                    // Req Future Appmt

                    enableOrdisableProviderDetails(str_Availability_Type);
                }

                else if(layout.getChildCount()<1)
                {
                    //Make future appointment

                }

            }

            /*This is for Status is available and the timeslot is Zero..Remaining all
                    the status were not available.*/
            else if(isDoctorAvailableNow && layout.getChildCount() == 1){

            horizontalscrollview.setVisibility(View.GONE);
                Log.e("Am in availble now","Am in availble now");
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
                if(str_Availability_Type.equalsIgnoreCase("video"))
                {
                    Log.e("Am in availble now","Am in video");
                     tapSeetheDoctorTxt.setText("See Doctor");
                     reqfutureapptBtnLayout.setVisibility(View.GONE);
                    byvideoBtnLayout.setVisibility(View.VISIBLE);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
                     byphoneBtnLayout.setClickable(false);
                     byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_grey);

                }else  if(str_Availability_Type.equalsIgnoreCase("video or phone"))
                {
                    Log.e("Am in availble now","Am in phone");
                    tapSeetheDoctorTxt.setText("See Doctor");
                    reqfutureapptBtnLayout.setVisibility(View.GONE);
                    byvideoBtnLayout.setVisibility(View.VISIBLE);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
                    byphoneBtnLayout.setVisibility(View.VISIBLE);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
                }
                else  if(str_Availability_Type.equalsIgnoreCase("phone")){
                    Log.e("Am in availble now","Am in video or phone");

                    tapSeetheDoctorTxt.setText("Talk to Doctor");
                    reqfutureapptBtnLayout.setVisibility(View.GONE);
                    byvideoBtnLayout.setClickable(false);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_grey);
                    byphoneBtnLayout.setVisibility(View.VISIBLE);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
            }


            }
            //not available

            else if(layout.getChildCount() == 0&&str_Availability_Type.equals("not available")){
                if(str_appointmenttype.equals("1"))
                {
                    tapSeetheDoctorTxt.setText("Currently not available");
                    tapBtnLayout.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
                    ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
                    byvideoBtnLayout.setVisibility(View.GONE);
                    byphoneBtnLayout.setVisibility(View.GONE);
                     horizontalscrollview.setVisibility(View.GONE);
                }else  if(str_appointmenttype.equals("2"))
                {
                    tapSeetheDoctorTxt.setText("Currently not available");
                    tapBtnLayout.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
                    reqfutureapptBtn.setText("Make an appointment request");
                    ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
                    byvideoBtnLayout.setVisibility(View.GONE);
                    byphoneBtnLayout.setVisibility(View.GONE);
                    horizontalscrollview.setVisibility(View.GONE);
                }else{
                    tapSeetheDoctorTxt.setText("Currently not available");
                    tapBtnLayout.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
                    reqfutureapptBtn.setText("Make an appointment request");
                    ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
                    byvideoBtnLayout.setVisibility(View.GONE);
                    byphoneBtnLayout.setVisibility(View.GONE);
                    horizontalscrollview.setVisibility(View.GONE);
                }



            }

            //Available now and later
            //Part1 ----> timeslot zero followed by many timeslots
            else if(isDoctorAvailableNow && layout.getChildCount() > 1){
                enableOrdisableProviderDetails(str_Availability_Type);


            }
            //part 2 available now nd later
            //Part2 ----> timeslot not zero followed by many timeslots
            else if(!isDoctorAvailableNow && layout.getChildCount() > 1){
                enableOrdisableProviderDetails(str_Availability_Type);

            }

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

            String str_education="";
            if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "education")) {
                str_education = providerdetObj.get("education").getAsString();
            }
            if(str_Availability_Type.equals(getString(R.string.with_patient)))
            {
                tapBtnLayout.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tapBtnLayout.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey));
                } else {
                    tapBtnLayout.setBackgroundResource(R.drawable.btn_rounded_grey);
                }
//                tapSeetheDoctorTxt.setText(getString(R.string.currently_with_patient));
            }

            ProfileImg.setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));
            ProfileImg.setDefaultImageResId(R.drawable.doctor_icon);
            ProfileImg.setErrorImageResId(R.drawable.doctor_icon);
            doctorNameTv.setText(str_DoctorName);
            if(str_DoctorName.equals(""))
            {
                tapBtnLayout.setClickable(false);
                tapBtnLayout.setVisibility(View.GONE);
                detailsLl.setVisibility(View.GONE);

            }else
            {
                if(!str_Availability_Type.equals(getString(R.string.with_patient))) {
//                   tapSeetheDoctorTxt.setText(getString(R.string.providerDeatils_choose_doctor,str_DoctorName));
                }
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

    private void enableOrdisableProviderDetails(String str_Availability_Type) {
        if(str_Availability_Type.equalsIgnoreCase("video"))
        {
            horizontalscrollview.setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            tapSeetheDoctorTxt.setText("See Doctor");
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
            byphoneBtnLayout.setVisibility(View.INVISIBLE);
            tapReqFutureBtnAction();

        }else  if(str_Availability_Type.equalsIgnoreCase("video or phone"))
        {
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            horizontalscrollview.setVisibility(View.GONE);
            tapSeetheDoctorTxt.setText("See Doctor");
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
            byphoneBtnLayout.setVisibility(View.VISIBLE);
            byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
            tapReqFutureBtnAction();
        }
        else  if(str_Availability_Type.equalsIgnoreCase("phone")){
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            horizontalscrollview.setVisibility(View.GONE);
            tapSeetheDoctorTxt.setText("Talk to Doctor");
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setVisibility(View.INVISIBLE);
            byphoneBtnLayout.setVisibility(View.VISIBLE);
            byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
            tapReqFutureBtnAction();
        }
        else if(str_Availability_Type.equalsIgnoreCase("With Patient")){
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            horizontalscrollview.setVisibility(View.GONE);
            tapSeetheDoctorTxt.setText("Currently with patient");
            tapBtnLayout.setBackgroundColor(Color.parseColor("#FF6600"));
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setVisibility(View.GONE);
            byphoneBtnLayout.setVisibility(View.GONE);
            byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
            tapReqFutureBtnAction();
        }
    }

    private void tapReqFutureBtnAction() {
        reqfutureapptBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reqfutureapptBtn.getText().toString().equals("Make an appointment request")) {

                } else {
                    ((TextView) findViewById(R.id.dateTxt)).setVisibility(View.VISIBLE);
                    tapBtnLayout.setVisibility(View.GONE);
                    reqfutureapptBtnLayout.setVisibility(View.GONE);
                    horizontalscrollview.setVisibility(View.VISIBLE);
                    //calling service for each date selection on clicking request appmt btn
                    ((TextView) findViewById(R.id.dateTxt)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadProviderDetails(updatedAppointmentDate);
                        }
                    });
                }
            }
        });
    }

    private void setHorizontalScrollviewTimeslots(LinearLayout layout, String str_timeslot) {
        TextView myText;View line = new View(this);

        line.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        line.setBackgroundColor(0xAA345556);
        myText = new TextView(this);
        myText.setTextColor(Color.BLACK);
        myText.setTextSize(12);
        myText.setPadding(10,5,10,5);
        myText.setBackgroundResource(R.drawable.edittext_bg);
        myText.setText(MdliveUtils.getTimeFromTimestamp(str_timeslot));
        layout.addView(myText, 0);
        layout.addView(line, 1);
        horizontalscrollview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveIdForConfirmAppmt();
                for(int i=0;i<timeSlotListMap.size();i++)
                {
                    SharedPreferences settings = MDLiveProviderDetails.this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PreferenceConstants.SAVE_TIMESLOT_PREFERENCES, timeSlotListMap.get(i).get("timeslot"));
                    editor.putString(PreferenceConstants.SAVE_PHYS_ID_PREFERENCES, timeSlotListMap.get(i).get("phys_id"));
                    Log.e("savedTime",timeSlotListMap.get(i).get("timeslot")+"saved physid-->"+timeSlotListMap.get(i).get("phys_id"));
                    editor.commit();
                }


            }
        });
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
//        providerImageHolder.removeAllViews();
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


    // new code MDLIVE Embed Lit Implementation for Date Picker

    /**
     * This method is to fetch the apoointment date and the native date picker is called for selecting
     * the required date.
     */
    public void appointmentAction(View v) {
        GetCurrentDate((TextView) findViewById(R.id.dateTxt));
        // On button click show datepicker dialog
        showDialog(DATE_PICKER_ID);

    }
    public void GetCurrentDate(TextView selectedText) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Show current date

        selectedText.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("/").append(day).append("/")
                .append(year).append(" "));
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime());
                return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Show selected date
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, selectedYear);
            cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            cal.set(Calendar.MONTH, selectedMonth);
            String format = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());
            ((TextView)findViewById(R.id.dateTxt)).setText(format);

            DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
            updatedAppointmentDate = format1.format(new Date());

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveProviderDetails.this);
    }


}
