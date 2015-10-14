package com.mdlive.embedkit.uilayer.sav;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.provider.ProviderDetailServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This class returns the Provider profile for the corresponding providers.Along with that
 * the provider profile the details of the provider has also be defined.The Qualification of
 * the provider like Specalties, about the provider,languages has been defined.
 */
public class MDLiveProviderDetails extends MDLiveBaseActivity{
    private TextView aboutme_txt,education_txt,specialities_txt, hospitalAffilations_txt,location_txt,lang_txt, doctorNameTv,detailsGroupAffiliations;
    private CircularNetworkImageView ProfileImg;
    public String DoctorId,str_ProfileImg="",str_Availability_Type = "",selectedTimestamp,str_phys_avail_id,str_appointmenttype = "";
    private TextView tapSeetheDoctorTxt, byvideoBtn,byphoneBtn,reqfutureapptBtn;
    private LinearLayout tapSeetheDoctorTxtLayout, byvideoBtnLayout, byphoneBtnLayout,videophoneparentLl;
    private RelativeLayout reqfutureapptBtnLayout;
    private boolean selectedTimeslot=false;
    private String SharedLocation,AppointmentDate,AppointmentType,groupAffiliations,updatedAppointmentDate;
    private String Shared_AppointmentDate,longLocation;
    private LinearLayout detailsLl, providerImageCollectionHolder;
    private HorizontalScrollView horizontalscrollview;
    private  LinearLayout layout;
    private Button reqApmtBtm;
    private static final int DATE_PICKER_ID = IdConstants.SEARCHPROVIDER_DATEPICKER;
    private ArrayList<HashMap<String, String>> timeSlotListMap = new ArrayList<HashMap<String, String>>();
    boolean isDoctorAvailableNow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_choose_provider_details);
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_doctor_details));

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        ((ImageView) findViewById(R.id.backImg)).setContentDescription(getString(R.string.mdl_ada_back_button));
        ((ImageView) findViewById(R.id.txtApply)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_doctor_details).toUpperCase());
        ((TextView) findViewById(R.id.headerTxt)).setTextColor(Color.WHITE);


        Initialization();
        getPreferenceDetails();
        //Service call Method
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
        //Enable or disable Request Appointment Button
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
        Shared_AppointmentDate = settings.getString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, null);
        groupAffiliations = settings.getString(PreferenceConstants.PROVIDER_GROUP_AFFILIATIONS_PREFERENCES, null);
        detailsGroupAffiliations.setText(groupAffiliations);
        AppointmentType = StringConstants.APPOINTMENT_TYPE;
        longLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, getString(R.string.mdl_florida));

        try {
            if(Shared_AppointmentDate!=null && Shared_AppointmentDate.length() != 0){
                final Calendar cal = TimeZoneUtils.getCalendarWithOffset(this);
                cal.setTimeInMillis(Long.parseLong(Shared_AppointmentDate) * 1000);
                final Date date = cal.getTime();
                final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                format.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
                String convertedTime =  format.format(date);
                AppointmentDate = convertedTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        reqApmtBtm = (Button)findViewById(R.id.reqApmtBtm);
        aboutme_txt = (TextView)findViewById(R.id.aboutMe_txt);
        education_txt = (TextView)findViewById(R.id.education_txt);
        specialities_txt = (TextView)findViewById(R.id.specialities_txt);
        hospitalAffilations_txt = (TextView)findViewById(R.id.license_txt);
        location_txt = (TextView)findViewById(R.id.provider_location_txt);
        lang_txt = (TextView)findViewById(R.id.provider_lang_txt);
        tapSeetheDoctorTxt = (TextView)findViewById(R.id.tapSeetheDoctorTxt);
        tapSeetheDoctorTxtLayout = (LinearLayout)findViewById(R.id.tapSeetheDoctorTxtLayout);
        videophoneparentLl  = (LinearLayout)findViewById(R.id.videophoneparentLl);
        byvideoBtn = (TextView)findViewById(R.id.byvideoBtn);
        byvideoBtnLayout  = (LinearLayout)findViewById(R.id.byvideoBtnLayout);
        byphoneBtn = (TextView)findViewById(R.id.byphoneBtn);
        byphoneBtnLayout = (LinearLayout)findViewById(R.id.byphoneBtnLayout);
        reqfutureapptBtn = (TextView)findViewById(R.id.reqfutureapptBtn);
        layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
        reqfutureapptBtnLayout= (RelativeLayout)findViewById(R.id.reqfutureapptBtnLayout);

        doctorNameTv = (TextView)findViewById(R.id.DoctorName);
        ProfileImg = (CircularNetworkImageView)findViewById(R.id.ProfileImg1);
        providerImageCollectionHolder = (LinearLayout) findViewById(R.id.providerImageCollectionHolder);
        detailsGroupAffiliations = (TextView) findViewById(R.id.detailsGroupAffiliations);
        detailsLl = (LinearLayout) findViewById(R.id.detailsLl);

        horizontalscrollview  = (HorizontalScrollView) findViewById(R.id.horizontalscrollview);
        setProgressBar(findViewById(R.id.progressDialog));

        GetCurrentDate((TextView)findViewById(R.id.dateTxt));

    }




    public void rightBtnOnClick(View view){
        reqApmtBtm.setVisibility(View.VISIBLE);
        Intent Reasonintent = new Intent(MDLiveProviderDetails.this, MDLiveReasonForVisit.class);
        startActivity(Reasonintent);
        MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
    }

    public void leftBtnOnClick(View view){
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
    private void displayProviderDetailonUI(JSONObject response){
        try{
            tapSeetheDoctorTxtLayout.setClickable(true);
            layout.removeAllViews();
            videoList.clear();
            phoneList.clear();

            if( ((RelativeLayout) findViewById(R.id.dateTxtLayout)).getVisibility() == View.VISIBLE){
                selectedTimeslot=true;
                handleDateResponse(response);
            }else{
                handleSuccessResponse(response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
                new LoadProviderDetais(response).execute(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                tapSeetheDoctorTxtLayout.setClickable(false);
                detailsLl.setVisibility(View.GONE);
                tapSeetheDoctorTxtLayout.setVisibility(View.GONE);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        tapSeetheDoctorTxtLayout.setClickable(false);
                        tapSeetheDoctorTxtLayout.setVisibility(View.GONE);
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(null, MDLiveProviderDetails.this);
                    }
                }
            }};
        ProviderDetailServices services = new ProviderDetailServices(MDLiveProviderDetails.this, null);

        services.getProviderDetails(SharedLocation,AppointmentDate,AppointmentType,DoctorId,successCallBackListener, errorListener);
    }
    //This respose is for while updating the daye response

    public void handleDateResponse(JSONObject response){
        horizontalscrollview.setVisibility(View.GONE);
        //Fetch Data From the Services
        JsonParser parser = new JsonParser();
        JsonObject responObj = (JsonObject) parser.parse(response.toString());
        JsonObject profileobj = responObj.get("doctor_profile").getAsJsonObject();

        JsonObject appointment_slot = profileobj.get("appointment_slot").getAsJsonObject();
        JsonArray available_hour = appointment_slot.get("available_hour").getAsJsonArray();

        LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
        String  str_timeslot = "", str_phys_avail_id = "";
        if (layout.getChildCount() > 0) {
            layout.removeAllViews();
        }
        timeSlotListMap.clear();

        for (int i = 0; i < available_hour.size(); i++) {
            JsonObject availabilityStatus = available_hour.get(i).getAsJsonObject();
            String str_availabilityStatus = "";

            if (MdliveUtils.checkJSONResponseHasString(availabilityStatus, "status")) {
                str_availabilityStatus = availabilityStatus.get("status").getAsString();
                if (str_availabilityStatus.equals("Available")) {
                    //This visibility is for future timeslots response for the corresponding date selection.
                    //if  the future date has timeslots then make an appointment req layout nd textview visibility will be gone

                    ((RelativeLayout)findViewById(R.id.noappmtsTxtLayout)).setVisibility(View.GONE);
                    reqfutureapptBtnLayout.setVisibility(View.GONE);
                    JsonArray timeSlotArray = availabilityStatus.get("time_slot").getAsJsonArray();


                    for (int j = 0; j < timeSlotArray.size(); j++) {
                        JsonObject timeSlotObj = timeSlotArray.get(j).getAsJsonObject();


                        if (MdliveUtils.checkJSONResponseHasString(timeSlotObj, "appointment_type") && MdliveUtils.checkJSONResponseHasString(timeSlotObj, "timeslot")) {
                            str_appointmenttype = timeSlotObj.get("appointment_type").getAsString();
                            str_timeslot = timeSlotObj.get("timeslot").getAsString();
                            selectedTimestamp = timeSlotObj.get("timeslot").getAsString();


                            if(MdliveUtils.checkJSONResponseHasString(timeSlotObj, "phys_availability_id")){
                                str_phys_avail_id = timeSlotObj.get("phys_availability_id").getAsString();
                            }else{
                                str_phys_avail_id = null;
                            }

                            HashMap<String, String> datemap = new HashMap<String, String>();
                            datemap.put("timeslot", str_timeslot);
                            datemap.put("phys_id", str_phys_avail_id);
                            datemap.put("appointment_type", str_appointmenttype);
                            videophoneparentLl.setVisibility(View.VISIBLE);
                            timeSlotListMap.add(datemap);

                            final Button myText = new Button(MDLiveProviderDetails.this);

                            if (str_timeslot.equals("0")) {
                                final int density = (int) getBaseContext().getResources().getDisplayMetrics().density;


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    myText.setElevation(0f);
                                }
                                LinearLayout.LayoutParams params = new
                                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(4 * density ,4 * density, 4 * density, 4 * density);
                                myText.setLayoutParams(params);
                                myText.setGravity(Gravity.CENTER);
                                myText.setTextColor(Color.WHITE);
                                myText.setTextSize(16);
                                myText.setPadding(8 * density ,4 * density, 8 * density, 4 * density);
                                myText.setBackgroundResource(R.drawable.edittext_bg);
                                myText.setText("Now");
                                myText.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                                myText.setClickable(true);
                                previousSelectedTv = myText;
                                if(str_appointmenttype.toLowerCase().contains("video")||str_appointmenttype.toLowerCase().contains("video or phone")){
                                    videoList.add(myText);
                                }
                                if(str_appointmenttype.toLowerCase().contains("phone")){
                                    phoneList.add(myText);
                                }

                                LinearLayout.LayoutParams lp = new
                                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(4 * density ,4 * density, 4 * density, 4 * density);
                                myText.setLayoutParams(lp);
                                myText.setTag("Now");
                                defaultNowTextPreferences(myText, str_appointmenttype);
                                selectedTimeslot=true;
                                clickEventForHorizontalText(myText,str_timeslot,str_phys_avail_id);
                                layout.addView(myText);
                            } else {
                                setHorizontalScrollviewTimeslots(layout, str_timeslot, j,str_phys_avail_id);

                            }

                        }
                    }
                }else
                {
                    if(layout.getChildCount() == 0){
                        selectedTimeslot=false;
                        ((RelativeLayout)findViewById(R.id.noappmtsTxtLayout)).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.noAppmtsTxt)).setText(getString(R.string.mdl_notimeslots_txt));
                        reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.reqfutureapptBtn)).setText("Make an appointment request");
                        ((TextView)findViewById(R.id.reqfutureapptBtn)).setTextColor(Color.parseColor("#0079FD"));
                        videophoneparentLl.setVisibility(View.GONE);
                        tapReqFutureBtnAction();
                        ((Button)findViewById(R.id.reqApmtBtm)).setVisibility(View.GONE);

                    }
                }
            }
        }

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
            //Fetch Data From the Services
            Log.i("Response details", response.toString());
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject) parser.parse(response.toString());
            JsonObject profileobj = responObj.get("doctor_profile").getAsJsonObject();
            JsonObject providerdetObj = profileobj.get("provider_details").getAsJsonObject();

            JsonObject appointment_slot = profileobj.get("appointment_slot").getAsJsonObject();
            JsonArray available_hour = appointment_slot.get("available_hour").getAsJsonArray();

            boolean isDoctorWithPatient = false;
            LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
            String str_timeslot = ""; /*str_phys_avail_id = "",*/
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            str_Availability_Type = sharedpreferences.getString(PreferenceConstants.PROVIDER_AVAILABILITY_TYPE_PREFERENCES,"");
            String  str_avail_status = sharedpreferences.getString(PreferenceConstants.PROVIDER_AVAILABILITY_STATUS_PREFERENCES,"");
            if(str_avail_status.equalsIgnoreCase("true"))
            {
                if(str_Availability_Type.equalsIgnoreCase("video or phone"))
                {   isDoctorAvailableNow=true;
                }else if (str_Availability_Type.equalsIgnoreCase("phone")) {
                    isDoctorAvailableNow=true;
                }else if (str_Availability_Type.equalsIgnoreCase("video")) {
                    isDoctorAvailableNow=true;
                }

                else if (str_Availability_Type.equalsIgnoreCase("With Patient")) {
                    isDoctorAvailableNow=false;
                }else
                {
                    isDoctorAvailableNow=false;
                }
            } else {
                isDoctorAvailableNow=false;
            }

            if (layout.getChildCount() > 0) {
                layout.removeAllViews();
            }
            videoList.clear();
            phoneList.clear();

            for (int i = 0; i < available_hour.size(); i++) {
                JsonObject availabilityStatus = available_hour.get(i).getAsJsonObject();
                String str_availabilityStatus = "";

                if (MdliveUtils.checkJSONResponseHasString(availabilityStatus, "status")) {
                    str_availabilityStatus = availabilityStatus.get("status").getAsString();
                    if (str_availabilityStatus.equals("Available")) {
                        JsonArray timeSlotArray = availabilityStatus.get("time_slot").getAsJsonArray();
                        for (int j = 0; j < timeSlotArray.size(); j++) {
                            JsonObject timeSlotObj = timeSlotArray.get(j).getAsJsonObject();

                            if (MdliveUtils.checkJSONResponseHasString(timeSlotObj, "appointment_type") && MdliveUtils.checkJSONResponseHasString(timeSlotObj, "timeslot")) {
                                str_appointmenttype = timeSlotObj.get("appointment_type").getAsString();
                                str_timeslot = timeSlotObj.get("timeslot").getAsString();
                                selectedTimestamp = timeSlotObj.get("timeslot").getAsString();


                                if(MdliveUtils.checkJSONResponseHasString(timeSlotObj, "phys_availability_id")){
                                    str_phys_avail_id = timeSlotObj.get("phys_availability_id").getAsString();
                                }else{
                                    str_phys_avail_id = null;
                                }

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("timeslot", str_timeslot);
                                map.put("phys_id", (str_phys_avail_id == null)?"":str_phys_avail_id+"");
                                map.put("appointment_type", str_appointmenttype);

                                timeSlotListMap.add(map);
                                if (str_timeslot.equals("0")) {
                                    if(!str_Availability_Type.equalsIgnoreCase("With Patient")) {
                                        final int density = (int) getBaseContext().getResources().getDisplayMetrics().density;

                                        final Button myText = new Button(MDLiveProviderDetails.this);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            myText.setElevation(0f);
                                        }
                                        isDoctorAvailableNow = true;
                                        LinearLayout.LayoutParams params = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(4 * density, 4 * density, 4 * density, 4 * density);
                                        myText.setLayoutParams(params);
                                        myText.setGravity(Gravity.CENTER);
                                        myText.setTextColor(Color.WHITE);
                                        myText.setTextSize(16);
                                        myText.setPadding(8 * density, 4 * density, 8 * density, 4 * density);
                                        myText.setBackgroundResource(R.drawable.edittext_bg);
                                        myText.setText("Now");
                                        myText.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                                        myText.setClickable(true);
                                        previousSelectedTv = myText;
                                        if (str_appointmenttype.toLowerCase().contains("video")) {
                                            videoList.add(myText);
                                        }
                                        if (str_appointmenttype.toLowerCase().contains("phone")) {
                                            phoneList.add(myText);
                                        }
                                        LinearLayout.LayoutParams lp = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lp.setMargins(4 * density, 4 * density, 4 * density, 4 * density);
                                        myText.setLayoutParams(lp);
                                        myText.setTag("Now");
                                        defaultNowTextPreferences(myText, str_appointmenttype);
                                        selectedTimeslot = true;
                                        clickEventForHorizontalText(myText,str_timeslot,str_phys_avail_id);
                                        layout.addView(myText);
                                        //layout.addView(line, 1);
                                    }
                                } else {
                                    setHorizontalScrollviewTimeslots(layout, str_timeslot, j,str_phys_avail_id);
                                }

                            }
                        }
                    } else if (str_availabilityStatus.equalsIgnoreCase("With patient")) {
                        isDoctorWithPatient = true;
                    }
                }
            }

            //with patient
            if (isDoctorWithPatient) {
                if (layout.getChildCount() >= 1) {
                    // Req Future Appmt

                    enableOrdisableProviderDetails(str_Availability_Type);
                } else if (layout.getChildCount() <1) {
                    //Make future appointment
                    onlyWithPatient();

                }

            }

            /*This is for Status is available and the timeslot is Zero..Remaining all
                    the status were not available.*/
            //Available now only
            else if (isDoctorAvailableNow && layout.getChildCount() < 1) {

                horizontalscrollview.setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
                if (str_Availability_Type.equalsIgnoreCase("video")) {
                    onlyVideo();

                } else if (str_Availability_Type.equalsIgnoreCase("video or phone")) {
                    VideoOrPhoneNotAvailable();
                } else if (str_Availability_Type.equalsIgnoreCase("phone")) {
                    onlyPhone();
                }else if (str_Availability_Type.equalsIgnoreCase("With Patient")) {
                    onlyWithPatient();
                }


            }

            //Available now and later
            //Part1 ----> timeslot zero followed by many timeslots
            else if (isDoctorAvailableNow && layout.getChildCount() >= 1) {
                enableOrdisableProviderDetails(str_Availability_Type);



            }
            //part 2 available ly later
            //Part2 ----> timeslot not zero followed by many timeslots
            else if (!isDoctorAvailableNow && layout.getChildCount() >=1) {
                availableOnlyLater(str_Availability_Type);

            }

            //not available

            else if (layout.getChildCount() == 0 && str_Availability_Type.equals("not available")) {
                if (str_appointmenttype.equals("1")) {
                    notAvailable();
                } else if (str_appointmenttype.equals("2")) {
                    notAvailable();
                } else {
                    notAvailable();
                }

            }


            //not available
            else if (layout.getChildCount() == 0) {
                horizontalscrollview.setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
                if (str_Availability_Type.equalsIgnoreCase("video")) {
                    onlyVideo();

                } else if (str_Availability_Type.equalsIgnoreCase("video or phone")) {
                    VideoOrPhoneNotAvailable();
                } else if (str_Availability_Type.equalsIgnoreCase("phone")) {
                    onlyPhone();
                }
                else if (str_Availability_Type.equalsIgnoreCase("With patient")) {
                    onlyWithPatient();
                }
                else if (str_Availability_Type.equalsIgnoreCase("not available")){
                    //Make future appointment only
                    notAvailable();
                }

            }

            setResponseQualificationDetails(providerdetObj, str_Availability_Type);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void enableReqAppmtBtn() {
        if(!selectedTimeslot)
        {
            reqApmtBtm.setVisibility(View.GONE);

        }else
        {
            reqApmtBtm.setVisibility(View.VISIBLE);

        }
    }

    private void notAvailable() {
        tapSeetheDoctorTxtLayout.setVisibility(View.VISIBLE);
        tapSeetheDoctorTxt.setText(getString(R.string.mdl_currently_unavail));
        tapSeetheDoctorTxtLayout.setClickable(false);
        tapSeetheDoctorTxtLayout.setBackgroundResource((R.color.darkgrey_background));
        ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
        videophoneparentLl.setVisibility(View.GONE);
        byvideoBtnLayout.setVisibility(View.GONE);
        byphoneBtnLayout.setVisibility(View.GONE);
        reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
        reqfutureapptBtnLayout.setClickable(true);
        reqfutureapptBtn.setText(getString(R.string.mdl_make_appt));
        tapReqFutureBtnAction();
    }

    private void onlyWithPatient() {
        isDoctorAvailableNow=false;
        tapSeetheDoctorTxt.setText(getString(R.string.mdl_currently_with_patient));
        tapSeetheDoctorTxt.setClickable(false);
        tapSeetheDoctorTxtLayout.setBackgroundResource(R.drawable.searchpvr_orange_rounded_corner);
        ((ImageView)findViewById(R.id.see_icon)).setImageResource(R.drawable.clock_icon_white);
        reqfutureapptBtn.setText(getString(R.string.mdl_make_appt));
        tapReqFutureBtnAction();
        videophoneparentLl.setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
        byvideoBtnLayout.setVisibility(View.GONE);
        byphoneBtnLayout.setVisibility(View.GONE);
        horizontalscrollview.setVisibility(View.GONE);
    }

    private void onlyPhone() {
        tapSeetheDoctorTxt.setText(getString(R.string.mdl_talk_to_doctor));
        saveConsultationType("Phone");
        ((ImageView)findViewById(R.id.see_icon)).setBackgroundResource(R.drawable.phone_icon_white);
        reqfutureapptBtnLayout.setVisibility(View.GONE);
        videophoneparentLl.setVisibility(View.GONE);
        byvideoBtnLayout.setVisibility(View.GONE);
        byvideoBtnLayout.setClickable(false);
        byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_grey);
        byphoneBtnLayout.setVisibility(View.GONE);
        byphoneBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
        saveTimeSlotToNowMode();
        accessModeCall("phone");
    }

    private void onlyVideo() {
        tapSeetheDoctorTxt.setText(getString(R.string.mdl_see_doc_now));
        saveConsultationType("Video");
        ((ImageView)findViewById(R.id.see_icon)).setBackgroundResource(R.drawable.video_icon_white);
        reqfutureapptBtnLayout.setVisibility(View.GONE);
        videophoneparentLl.setVisibility(View.GONE);
        byvideoBtnLayout.setVisibility(View.GONE);
        byvideoBtnLayout.setBackgroundResource(R.drawable.btn_rounded_bg);
        byphoneBtnLayout.setVisibility(View.GONE);
        accessModeCall("video");
    }

    private void VideoOrPhoneNotAvailable() {
        tapSeetheDoctorTxtLayout.setVisibility(View.GONE);
        tapSeetheDoctorTxt.setVisibility(View.GONE);
        reqfutureapptBtnLayout.setVisibility(View.GONE);
        byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
        ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
        byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
        ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
        byvideoBtn.setTextColor(Color.WHITE);
        byphoneBtn.setTextColor(Color.WHITE);
        byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
                ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                byvideoBtn.setTextColor(Color.WHITE);
                byphoneBtn.setTextColor(Color.GRAY);
                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
                accessModeCall("video");
                saveConsultationType("Video");
                saveTimeSlotToNowMode();
            }


        });
        byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
                ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
                byvideoBtn.setTextColor(Color.GRAY);
                byphoneBtn.setTextColor(Color.WHITE);
                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
                accessModeCall("phone");
                saveConsultationType("Phone");
            }
        });
    }


    private void saveTimeSlotToNowMode() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.SELECTED_TIMESLOT, "Now");
        editor.commit();
    }

    private void accessModeCall(String accessType) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.ACCESS_MODE, accessType);
        editor.putString(PreferenceConstants.CONSULTATION_TYPE, accessType);
        editor.commit();
    }
    //This is to show the by video and by Phone icon for both the available now (video or phone)
    //and available now and later(video/phone).

    private void ByPhoneOrByVideoForNowAndLater() {
        tapSeetheDoctorTxtLayout.setVisibility(View.GONE);
        byvideoBtnLayout.setVisibility(View.VISIBLE);
        byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
        byvideoBtn.setTextColor(Color.WHITE);
        ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
        byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
        byphoneBtn.setTextColor(Color.WHITE);
        ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
        byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
                ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                byvideoBtn.setTextColor(Color.WHITE);
                ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);
                byphoneBtn.setTextColor(Color.GRAY);
                saveConsultationType("Video");
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
                saveTimeSlotToNowMode();
            }
        });

        byphoneBtnLayout.setVisibility(View.VISIBLE);
        byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_green_rounded_corner);
                ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
                ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);
                saveConsultationType("Phone");
                byphoneBtn.setTextColor(Color.WHITE);
                byvideoBtn.setTextColor(Color.GRAY);
                Intent Reasonintent = new Intent(MDLiveProviderDetails.this,MDLiveReasonForVisit.class);
                startActivity(Reasonintent);
                MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
            }
        });
    }

    private void setResponseQualificationDetails(JsonObject providerdetObj, String str_Availability_Type) {
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
        if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "provider_image_url")) {
            str_ProfileImg = providerdetObj.get("provider_image_url").getAsString();
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PROVIDER_PROFILE, str_ProfileImg);
            editor.commit();
        }

        String str_education="";
        if(MdliveUtils.checkJSONResponseHasString(providerdetObj, "education")) {
            str_education = providerdetObj.get("education").getAsString();
        }
        if(str_Availability_Type.equals(getString(R.string.mdl_with_patient)))
        {
            tapSeetheDoctorTxtLayout.setClickable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                tapSeetheDoctorTxtLayout.setBackground(getResources().getDrawable(R.drawable.searchpvr_orange_rounded_corner));
                ((ImageView)findViewById(R.id.see_icon)).setImageResource(R.drawable.clock_icon_white);
            }
        }

        ProfileImg.setImageUrl(str_ProfileImg, ApplicationController.getInstance().getImageLoader(this));
        ProfileImg.setDefaultImageResId(R.drawable.doctor_icon);
        ProfileImg.setErrorImageResId(R.drawable.doctor_icon);
        doctorNameTv.setText(str_DoctorName);
        if(str_DoctorName.equals(""))
        {
            tapSeetheDoctorTxtLayout.setClickable(false);
            tapSeetheDoctorTxtLayout.setVisibility(View.GONE);
            detailsLl.setVisibility(View.GONE);

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

        //Provider affiliation Logo

        getProviderImageArrayResponse(providerdetObj);
    }

    private void enableOrdisableProviderDetails(String str_Availability_Type) {
        if(str_Availability_Type.equalsIgnoreCase("video"))
        {
            horizontalscrollview.setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            tapSeetheDoctorTxtLayout.setVisibility(View.VISIBLE);
            tapSeetheDoctorTxt.setText("See this doctor now");
            tapSeetheDoctorTxt.setContentDescription(getString(R.string.mdl_ada_seethisdoctor_button));
            saveConsultationType("Video");
            ((ImageView)findViewById(R.id.see_icon)).setBackgroundResource(R.drawable.video_icon_white);
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            videophoneparentLl.setVisibility(View.GONE);
            byvideoBtnLayout.setVisibility(View.GONE);
            byphoneBtnLayout.setVisibility(View.GONE);
            byphoneBtnLayout.setClickable(false);
            accessModeCall("video");
            saveTimeSlotToNowMode();
            tapReqFutureBtnAction();
        }else  if(str_Availability_Type.equalsIgnoreCase("video or phone"))
        {
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            horizontalscrollview.setVisibility(View.GONE);
            tapSeetheDoctorTxt.setVisibility(View.GONE);
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            ByPhoneOrByVideoForNowAndLater();
            tapReqFutureBtnAction();
        }
        else  if(str_Availability_Type.equalsIgnoreCase("phone")){
            ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.GONE);
            horizontalscrollview.setVisibility(View.GONE);
            tapSeetheDoctorTxtLayout.setVisibility(View.VISIBLE);
            tapSeetheDoctorTxt.setText("Talk to this doctor now");
            tapSeetheDoctorTxt.setContentDescription(getString(R.string.mdl_ada_talktodoctor_button));
            saveConsultationType("Phone");
            ((ImageView)findViewById(R.id.see_icon)).setBackgroundResource(R.drawable.phone_icon_white);
            reqfutureapptBtnLayout.setVisibility(View.VISIBLE);
            videophoneparentLl.setVisibility(View.GONE);
            byvideoBtnLayout.setVisibility(View.GONE);
            byphoneBtnLayout.setVisibility(View.GONE);
            accessModeCall("phone");
            saveTimeSlotToNowMode();
            tapReqFutureBtnAction();
        } else if(str_Availability_Type.equalsIgnoreCase("With Patient")){
            isDoctorAvailableNow=false;
            ((LinearLayout)findViewById(R.id.withpatineLayout)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.withpatientTxt)).setText("Currently with patient");
            clickForVideoOrPhoneTapReqFutureAction();
            ((LinearLayout)findViewById(R.id.withpatineLayout)).setClickable(false);
            ((LinearLayout)findViewById(R.id.withpatineLayout)).setBackgroundResource(R.color.choose_pro_orange_color);
            ((ImageView)findViewById(R.id.withpatient_icon)).setImageResource(R.drawable.clock_icon_white);
            ((LinearLayout)findViewById(R.id.withpatineLayout)).setVisibility(View.VISIBLE);


        } else {
            notAvailable();
        }
    }

    //This is For only Future Appointments..That is available only later

    private void availableOnlyLater(String str_Availability_Type) {
        if(str_Availability_Type.equalsIgnoreCase("video")) {
            clickForVideoOrPhoneTapReqFutureAction();

        } else  if(str_Availability_Type.equalsIgnoreCase("video or phone")) {
            clickForVideoOrPhoneTapReqFutureAction();
            horizontalscrollview.setVisibility(View.GONE);
        } else  if(str_Availability_Type.equalsIgnoreCase("phone")){
            clickForVideoOrPhoneTapReqFutureAction();
        } else if(str_Availability_Type.equalsIgnoreCase("With Patient")){
            (findViewById(R.id.withpatineLayout)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.withpatientTxt)).setText("Currently with patient");
            clickForVideoOrPhoneTapReqFutureAction();
            (findViewById(R.id.withpatineLayout)).setClickable(false);
            (findViewById(R.id.withpatineLayout)).setBackgroundResource(R.color.choose_pro_orange_color);
            ((ImageView)findViewById(R.id.withpatient_icon)).setImageResource(R.drawable.clock_icon_white);
            (findViewById(R.id.withpatineLayout)).setVisibility(View.VISIBLE);


        }else if(str_Availability_Type.equalsIgnoreCase("not available")&&layout.getChildCount()>=1) {
            clickForVideoOrPhoneTapReqFutureAction();
        } else {
            notAvailable();
        }
    }

    private void tapReqFutureBtnAction() {
        reqfutureapptBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reqfutureapptBtn.getText().toString().equalsIgnoreCase("Make an appointment request")) {
                    reqfutureapptBtnLayout.setClickable(true);
                    Intent intent = new Intent(MDLiveProviderDetails.this,MDLiveMakeAppmtrequest.class);
                    startActivity(intent);
                    MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
                    finish();

                } else {
                    clickForVideoOrPhoneTapReqFutureAction();

                }
            }


        });

    }

    private void clickForVideoOrPhoneTapReqFutureAction() {
        ((RelativeLayout) findViewById(R.id.dateTxtLayout)).setVisibility(View.VISIBLE);
        tapSeetheDoctorTxtLayout.setVisibility(View.GONE);
        reqfutureapptBtnLayout.setVisibility(View.GONE);
        videophoneparentLl.setVisibility(View.VISIBLE);
        horizontalscrollview.setVisibility(View.GONE);
        //This condition is only for PHS Users

        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        //PHS user
        if(userBasicInfo.getPersonalInfo().getConsultMethod().equalsIgnoreCase("video"))
        {
                byphoneBtnLayout.setVisibility(View.INVISIBLE);
                byphoneBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
                byphoneBtn.setTextColor(getResources().getColor(R.color.disableBtn));
                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_gray);
                byphoneBtnLayout.setClickable(false);



            saveAppmtType("video");
            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            byvideoBtn.setTextColor(Color.GRAY);
            byvideoBtn.setTextColor(Color.GRAY);
            byvideoBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                        byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                        byvideoBtn.setTextColor(Color.WHITE);
                        ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                        ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_gray);
                        byphoneBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
                        byphoneBtn.setTextColor(getResources().getColor(R.color.disableBtn));
                        byphoneBtnLayout.setVisibility(View.INVISIBLE);
                        byphoneBtnLayout.setClickable(false);

                        horizontalscrollview.setVisibility(View.VISIBLE);
                        final LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
                        if (layout.getChildCount() > 0) {
                            layout.removeAllViews();
                        }

                        for (TextView tv : videoList) {
                            layout.addView(tv);
                        }
                        saveConsultationType("Video");
                        //Enable Request Appointment Button
                        enableReqAppmtBtn();
                        ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                        //horizontalscrollview.smoothScrollTo(0,0);

                        horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                        horizontalscrollview.postDelayed(new Runnable() {
                            public void run() {
                                horizontalscrollview.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                            }
                        }, 100L);
                        //horizontalscrollview.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                        horizontalscrollview.startAnimation(AnimationUtils.loadAnimation(MDLiveProviderDetails.this, R.anim.mdlive_trans_left_in));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }else if(userBasicInfo.getPersonalInfo().getConsultMethod().equalsIgnoreCase("phone"))
        {
            phsOnlyForPhone();
        }
//Only For idaho
        else if(longLocation.equalsIgnoreCase("idaho"))
        {
            onlyForIdaho();
        }
//Only For Texas
        else if(longLocation.equalsIgnoreCase("texas"))
        {
            phsOnlyForPhone();
        }
        //Vido or Phone Button on Click listener for Blue color
        else
        {
                //Disabling the Video or Phone Based on the Phone list and video list
                //if the phone list is empty we should not shown the Phone Layout
                //if the video list is empty then we should not show the Video Layout.
                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);
                ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);
                byvideoBtn.setTextColor(Color.GRAY);
                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                byphoneBtn.setTextColor(Color.GRAY);

                if(videoList.size()==0)
                {
                    saveAppmtType("phone");
                    byvideoBtnLayout.setVisibility(View.VISIBLE);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
                    byvideoBtn.setTextColor(getResources().getColor(R.color.disableBtn));
                    ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_gray);
                    byvideoBtnLayout.setClickable(false);
                }else {
                    byvideoBtnLayout.setVisibility(View.VISIBLE);
                    byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                                byvideoBtn.setTextColor(Color.WHITE);
                                ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                                byphoneBtnLayout.setVisibility(View.VISIBLE);
                                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                                byphoneBtn.setTextColor(Color.GRAY);
                                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);

                                horizontalscrollview.setVisibility(View.VISIBLE);
                                LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
                                if (layout.getChildCount() > 0) {
                                    layout.removeAllViews();
                                }

                                for (TextView tv : videoList) {
                                    layout.addView(tv);
                                }
                                saveConsultationType("Video");
                                //Enable Request Appointment Button
                                enableReqAppmtBtn();
                                horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                                ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon);
                                horizontalscrollview.startAnimation(AnimationUtils.loadAnimation(MDLiveProviderDetails.this, R.anim.mdlive_trans_left_in));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                if(phoneList.size()==0)
                {
                    saveAppmtType("video");
                    byphoneBtnLayout.setVisibility(View.VISIBLE);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
                    byphoneBtn.setTextColor(getResources().getColor(R.color.disableBtn));
                    ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_gray);
                    byphoneBtnLayout.setClickable(false);
                }else {
                    byphoneBtnLayout.setVisibility(View.VISIBLE);
                    byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                                byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                                byphoneBtn.setTextColor(Color.WHITE);
                                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);

                                byvideoBtnLayout.setVisibility(View.VISIBLE);
                                byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                                byvideoBtn.setTextColor(Color.GRAY);
                                ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_gray);
                                horizontalscrollview.setVisibility(View.VISIBLE);
                                LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
                                if (layout.getChildCount() > 0) {
                                    layout.removeAllViews();
                                }
                                for (TextView tv : phoneList) {
                                    layout.addView(tv);
                                }
                                saveConsultationType("Phone");
                                //Enable Request Appointment Button
                                enableReqAppmtBtn();
                                horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                                ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
                                ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon);
                                horizontalscrollview.startAnimation(AnimationUtils.loadAnimation(MDLiveProviderDetails.this, R.anim.mdlive_trans_left_in));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

        }

    }

    private void onlyForIdaho() {
        byphoneBtnLayout.setVisibility(View.VISIBLE);
        byphoneBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
        byphoneBtn.setTextColor(getResources().getColor(R.color.disableBtn));
        ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_gray);
        byphoneBtnLayout.setClickable(false);
        saveAppmtType("video");
        byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
        byvideoBtn.setTextColor(Color.GRAY);
        byvideoBtn.setTextColor(Color.GRAY);
        byvideoBtnLayout.setVisibility(View.VISIBLE);
        byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    byvideoBtn.setTextColor(Color.WHITE);
                    ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                    ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_gray);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
                    byphoneBtn.setTextColor(getResources().getColor(R.color.disableBtn));
                    byphoneBtnLayout.setVisibility(View.VISIBLE);
                    byphoneBtnLayout.setClickable(false);

                    horizontalscrollview.setVisibility(View.VISIBLE);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
                    if (layout.getChildCount() > 0) {
                        layout.removeAllViews();
                    }

                    for (TextView tv : videoList) {
                        layout.addView(tv);
                    }
                    saveConsultationType("Video");
                    //Enable Request Appointment Button
                    enableReqAppmtBtn();
                    horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                    ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_white);
                    horizontalscrollview.startAnimation(AnimationUtils.loadAnimation(MDLiveProviderDetails.this, R.anim.mdlive_trans_left_in));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //This method will show only the Phone icon and it will disable the Video Icon
    //This is applicable for the PHS Users that is the consult method is phone and
    //also for the texas.

    private void phsOnlyForPhone() {
        saveAppmtType("phone");
        byvideoBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
        byvideoBtn.setTextColor(getResources().getColor(R.color.disableBtn));
        ((ImageView) findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_gray);
        byvideoBtnLayout.setVisibility(View.VISIBLE);
        byvideoBtnLayout.setClickable(false);
        byphoneBtnLayout.setVisibility(View.VISIBLE);
        byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    byphoneBtn.setTextColor(Color.WHITE);
                    ((ImageView)findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
                    ((ImageView)findViewById(R.id.videoicon)).setImageResource(R.drawable.video_icon_gray);
                    horizontalscrollview.setVisibility(View.VISIBLE);
                    byvideoBtnLayout.setVisibility(View.VISIBLE);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.disable_round_rect_grey_border);
                    byvideoBtn.setTextColor(getResources().getColor(R.color.disableBtn));
                    byvideoBtnLayout.setClickable(false);

                    LinearLayout layout = (LinearLayout) findViewById(R.id.panelMessageFiles);
                    if (layout.getChildCount() > 0) {
                        layout.removeAllViews();
                    }
                    for (TextView tv : phoneList) {
                        layout.addView(tv);
                    }
                    saveConsultationType("Phone");
                    //Enable Request Appointment Button
                    enableReqAppmtBtn();
                    horizontalscrollview.smoothScrollTo(layout.getChildAt(0).getLeft(),0);
                    ((ImageView) findViewById(R.id.phoneicon)).setImageResource(R.drawable.phone_icon_white);
                    horizontalscrollview.startAnimation(AnimationUtils.loadAnimation(MDLiveProviderDetails.this, R.anim.mdlive_trans_left_in));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveAppmtType(String appmtType) {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.APPOINTMENT_TYPE, appmtType);
        editor.commit();
    }

    TextView previousSelectedTv;
    ArrayList<TextView> videoList = new ArrayList<>();
    ArrayList<TextView> phoneList = new ArrayList<>();

    private void setHorizontalScrollviewTimeslots(LinearLayout layout, String str_timeslot,int position,String physId) {

        final int density = (int) getBaseContext().getResources().getDisplayMetrics().density;

        final Button myText = new Button(MDLiveProviderDetails.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myText.setElevation(0f);
        }
        horizontalscrollview.setContentDescription("Horizontal ScrollView");
        myText.setTextColor(Color.GRAY);
        myText.setTextSize(16);
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4 * density ,4 * density, 4 * density, 4 * density);
        myText.setLayoutParams(params);
        myText.setGravity(Gravity.CENTER);
        myText.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
        myText.setClickable(true);
        myText.setTag(timeSlotListMap.get(position).get("appointment_type"));
        myText.setPadding(8 * density ,4 * density, 8 * density, 4 * density);
        myText.setText(TimeZoneUtils.getTimeFromTimestamp(str_timeslot, this));
        layout.addView(myText);
        if(str_appointmenttype.equalsIgnoreCase("video")||str_appointmenttype.equalsIgnoreCase("video or phone") && str_timeslot != null && !str_timeslot.equals("0")){

            videoList.add(myText);
        }

        if(str_appointmenttype.equalsIgnoreCase("phone")||str_appointmenttype.equalsIgnoreCase("video or phone") && str_timeslot != null && !str_timeslot.equals("0")){
            phoneList.add(myText);
        }

        clickForVideoOrPhoneTapReqFutureAction();
        clickEventForHorizontalText(myText,str_timeslot,physId);

    }

    private void defaultNowTextPreferences(final TextView timeslotTxt, final String appointmentType) {

        selectedTimeslot=true;
        saveProviderDetailsForConFirmAppmt(timeslotTxt.getText().toString(), ((TextView) findViewById(R.id.dateTxt)).getText().toString(), str_ProfileImg,selectedTimestamp,str_phys_avail_id);
        //This is to select and Unselect the Timeslot
        if(previousSelectedTv == null){
            previousSelectedTv = timeslotTxt;
            timeslotTxt.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
            timeslotTxt.setTextColor(Color.WHITE);
        }else{
            previousSelectedTv.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            previousSelectedTv.setTextColor(Color.GRAY);
            previousSelectedTv = timeslotTxt;
            timeslotTxt.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
            timeslotTxt.setTextColor(Color.WHITE);
        }

    }

    private void clickEventForHorizontalText(final Button timeslotTxt,final String Timestamp,final String phys_avail_id) {
        timeslotTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTimeslot=true;
                saveProviderDetailsForConFirmAppmt(timeslotTxt.getText().toString(), ((TextView) findViewById(R.id.dateTxt)).getText().toString(), str_ProfileImg,Timestamp,phys_avail_id);
                //This is to select and Unselect the Timeslot
                if(previousSelectedTv == null){
                    previousSelectedTv = timeslotTxt;
                    timeslotTxt.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    timeslotTxt.setTextColor(Color.WHITE);
                }else{
                    previousSelectedTv.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                    previousSelectedTv.setTextColor(Color.GRAY);
                    previousSelectedTv = timeslotTxt;
                    timeslotTxt.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    timeslotTxt.setTextColor(Color.WHITE);
                }
                //Enabling or Disabling the Request Appointment Button.
                enableReqAppmtBtn();
            }

        });
    }

         /* The visibility is based on Clicking the Horizontal Textview  and on clicking the
            the Slot we can get the video or phone of the timeslot . So that the visibility
             is based on the particular Method type.*/


    private void visibilityBasedOnHorizontalTextView(String position) {
        if(position.equalsIgnoreCase("video"))
        {
            saveConsultationType("Video");
            reqfutureapptBtnLayout.setVisibility(View.GONE);
            byvideoBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
            byvideoBtn.setTextColor(Color.WHITE);
            byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            byphoneBtn.setTextColor(Color.GRAY);
            byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    byvideoBtn.setTextColor(Color.WHITE);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                    byphoneBtn.setTextColor(Color.GRAY);
                    if(layout.getChildCount() > 0)
                        layout.removeAllViews();

                }
            });
            byphoneBtnLayout.setVisibility(View.VISIBLE);
            byphoneBtnLayout.setClickable(false);

        }else  if(position.equalsIgnoreCase("video or phone"))
        {
            byvideoBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    byvideoBtn.setTextColor(Color.WHITE);
                    byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                    byphoneBtn.setTextColor(Color.GRAY);
                    if(layout.getChildCount() > 0)
                        layout.removeAllViews();
                }
            });
            byphoneBtnLayout.setVisibility(View.VISIBLE);
            byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    byphoneBtn.setTextColor(Color.WHITE);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                    byvideoBtn.setTextColor(Color.GRAY);
                }
            });
        }
        else  if(position.equalsIgnoreCase("phone")){
            saveConsultationType("Phone");
            byvideoBtnLayout.setVisibility(View.VISIBLE);
            byvideoBtnLayout.setClickable(false);
            byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
            byvideoBtn.setTextColor(Color.GRAY);
            byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
            byphoneBtn.setTextColor(Color.WHITE);
            byphoneBtnLayout.setVisibility(View.VISIBLE);
            byphoneBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byphoneBtnLayout.setBackgroundResource(R.drawable.searchpvr_blue_rounded_corner);
                    byphoneBtn.setTextColor(Color.WHITE);
                    byvideoBtnLayout.setBackgroundResource(R.drawable.searchpvr_white_rounded_corner);
                    byvideoBtn.setTextColor(Color.GRAY);
                }
            });
        }
    }
    //Save to preferences for the Confirm appointment screen
    public void saveProviderDetailsForConFirmAppmt(String selectedTime,String datteText,String providerProfile,String selectedTimestamp,String phys_Id)
    {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(PreferenceConstants.SELECTED_TIMESLOT, selectedTime);
        editor.putString(PreferenceConstants.SELECTED_TIMESTAMP, selectedTimestamp);
        editor.putString(PreferenceConstants.SELECTED_DATE, datteText);
        editor.putString(PreferenceConstants.SELECTED_PHYSID, phys_Id);
        editor.putString(PreferenceConstants.PROVIDER_PROFILE, providerProfile);
        editor.commit();

    }

    public void providerGroupDetailsAffiliation(View view){
        Intent intent = new Intent(MDLiveProviderDetails.this,MDLiveProviderGroupDetailsAffiliations.class);
        startActivity(intent);
        MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
    }

    public void saveConsultationType(String consultationType){
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(consultationType != null){
            editor.putString(PreferenceConstants.CONSULTATION_TYPE,consultationType);
        }
        editor.commit();
    }

    /**
     *  Successful Response Handler for getting the Affillitations and the provider image.
     *  This method will give the successful response of the Provider's affilitations.
     *  This response is for the Affilations Purpose.The image can be placed one below the other
     *
     */
    private void getProviderImageArrayResponse(JsonObject providerdetObj) {
        JsonArray ProviderImageArray = providerdetObj.get("provider_groups").getAsJsonArray();
        if(ProviderImageArray.size() != 0) {
            providerImageCollectionHolder.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 10, 5, 10);
            for (int i = 0; i < ProviderImageArray.size(); i++) {
                try {
                    if (!ProviderImageArray.get(i).getAsJsonObject().get("logo").isJsonNull()) {
                        final ImageView imageView = new ImageView(MDLiveProviderDetails.this);
                        imageView.setLayoutParams(params);
                        ImageRequest request = new ImageRequest(ProviderImageArray.get(i).getAsJsonObject().get("logo").getAsString(),
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                }, 0, 0, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                        ApplicationController.getInstance().getRequestQueue(this).add(request);
                        imageView.setTag(ProviderImageArray.get(i).getAsJsonObject().get("url").getAsString());
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MDLiveProviderDetails.this, MDLiveProviderGroupDetailsAffiliations.class);
                                intent.putExtra("affurl", (imageView.getTag() != null) ? imageView.getTag().toString() : "");
                                startActivity(intent);
                                MdliveUtils.startActivityAnimation(MDLiveProviderDetails.this);
                            }
                        });
                        providerImageCollectionHolder.addView(imageView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            ((LinearLayout)findViewById(R.id.providerGroupAffiliation)).setVisibility(View.GONE);
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
            if(i == specialityArray.size()-1){
                specialities+= "\u2022"+" "+specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1);
            } else {
                specialities+= "\u2022"+" "+specialityArray.get(i).toString().substring(1,specialityArray.get(i).toString().length()-1)+"\n";
            }

            if(!specialities.equals("")||specialities.length()!=0)
            {
                specialities_txt.setText(specialities);
                ((TextView)findViewById(R.id.specalist)).setText(specialityArray.get(0).toString().replace("\"", ""));
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
            if(i == langArray.size()-1) {
                lang += "\u2022" + " " + langArray.get(i).toString().substring(1, langArray.get(i).toString().length() - 1);
            } else {
                lang += "\u2022" + " " + langArray.get(i).toString().substring(1, langArray.get(i).toString().length() - 1) + "\n";
            }
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
            if(i == responArray.size()-1){
                hospitalAffilations+= responArray.get(i).toString().substring(1,responArray.get(i).toString().length()-1);
            } else {
                hospitalAffilations+= responArray.get(i).toString().substring(1,responArray.get(i).toString().length()-1)+"\n";
            }

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
     * the required
     * .
     */
    public void appointmentAction(View v)
    {
//        GetCurrentDate((TextView) findViewById(R.id.dateTxt));
        // On button click show datepicker dialog
        showDialog(DATE_PICKER_ID);
    }

    public void GetCurrentDate(TextView selectedText) {
        final Calendar c = TimeZoneUtils.getCalendarWithOffset(this);
        SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
         Shared_AppointmentDate = settings.getString(PreferenceConstants.PROVIDER_APPOINTMENT_DATE_PREFERENCES, null);
        try {
            if(Shared_AppointmentDate!=null && Shared_AppointmentDate.length() != 0){
                c.setTimeInMillis(Long.parseLong(Shared_AppointmentDate) * 1000);
                final Date date = c.getTime();
                final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                format.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));

                final SimpleDateFormat format1 = new SimpleDateFormat("EEEE,  MMM dd  yyyy");
                format1.setTimeZone(TimeZoneUtils.getOffsetTimezone(this));
                selectedText.setText(format1.format(date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                Calendar calendar = TimeZoneUtils.getCalendarWithOffset(this);
                DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis() + TimeZoneUtils.getOffsetTimezone(this).getRawOffset() + TimeZoneUtils.getOffsetTimezone(this).getDSTSavings());
                return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            // Show selected date
            Calendar cal = TimeZoneUtils.getCalendarWithOffset(MDLiveProviderDetails.this);
            cal.set(Calendar.YEAR, selectedYear);
            cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            cal.set(Calendar.MONTH, selectedMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE,  MMM dd  yyyy");
            sdf.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLiveProviderDetails.this));
            String format = sdf.format(cal.getTime());
            ((TextView)findViewById(R.id.dateTxt)).setText(format);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
            format1.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLiveProviderDetails.this));
            updatedAppointmentDate = format1.format(cal.getTime());
            loadProviderDetails(updatedAppointmentDate);


        }
    };

    /**
     * Refer PMA-2128 for more details about the issue and the fix.
     *
     * Since there are few providers who has more details and time slots
     *
     * So having loop for long process the OS throws an alert as App not responding
     * to avoid this the long UI process made as Asyntask.
     *
     * The reason not to have the process on background method is the UI cannot be accessed in this case.
     */

    private class LoadProviderDetais extends AsyncTask<JSONObject, Void, Void>{
        private JSONObject response;
        public LoadProviderDetais(JSONObject details){
            response = details;
        }
        @Override
        protected Void doInBackground(JSONObject... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProviderDetailonUI(response);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveProviderDetails.this);
    }


}