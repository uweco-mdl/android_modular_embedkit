package com.mdlive.embedkit.uilayer.sav;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.login.MDLiveLogin;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.userinfo.FamilyMembersList;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.util.Calendar.MONTH;

public class MDLiveGetStarted extends FragmentActivity{
    private LinearLayout DobLl,LocationLl;
    private ProgressDialog pDialog;
    private TextView locationTxt,DateTxt,genderText;
    private int month,day,year;
    private String strPatientName,SavedLocation,dependentNmaeStr;
    private DatePickerDialog datePickerDialog;
    public static boolean isFemale;
    private ArrayList<HashMap<String, String>> PatientList = new ArrayList<HashMap<String, String>>();
    private ArrayList<String> GenderList = new ArrayList<String>();
    private  ArrayList<String> dependentList = new ArrayList<String>();
    private Spinner patientSpinner;
    private int serviceCount;
    private boolean isUserInfo=false;
    private HashMap<String,Object> userInfoObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_get_started1);
        MDLiveConfig.setData();
        setRemoteUserId();
        pDialog = Utils.getProgressDialog(getResources().getString(R.string.please_wait), this);
        Utils.hideSoftKeyboard(this);
        initialiseData();
        setonClickListener();
         /*  Load Services*/
        loadUserInformationDetails();
//        setGenderSpinnerValues();
    }

    /**
     * The gender should be populated in the arraylist.Male and Female should be pushed to the
     * gender arraylist also it should be populated only when the gender list size is equal to
     * zero and the gender arraylist should be passed to the spinner values
     */

    private void setGenderSpinnerValues() {
        if(GenderList.size() == 0){
            GenderList.add("Male");
            GenderList.add("Female");
        }
       // setSpinnerValues(GenderList, (Spinner)findViewById(R.id.genderSpinner));
    }
    /**
     * The Remote userid is hardcoded here for the Vsee and for configuring the .aar file
     * here the remote user id is saved in the preference constant by using the USER_UNIQUE_ID key
     * and this remote user id will be fetched through out the EmbedKit application.
     */
    private void setRemoteUserId() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString(PreferenceConstants.USER_UNIQUE_ID,"09198eb5-e350-4f58-a0dd-6e91f3993a70");
        editor.commit();
    }

    /**
     *
     * The location text should be updated from the preference value.The location can be fetched from
     * the MDLiveLocation class.The location text can be either selecting the Current location
     * or by entering the zip code the corresponding location for the particular zip code should
     * be returned or by choosing the state name the short name of the particular state will be
     * fetched and displayed.
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        String SavedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL");
        String longNameLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, "FLORIDA");
        locationTxt.setText(longNameLocation);
    }
    /**
     * Intialization of the views should be done here and the on click listener of the particular
     * views also displayed here.
     */
    private void initialiseData() {
        locationTxt= (TextView) findViewById(R.id.locationTxt);
        DateTxt = (TextView) findViewById(R.id.dobTxt);
        DobLl = (LinearLayout) findViewById(R.id.dobLl);
        LocationLl = (LinearLayout) findViewById(R.id.locationLl);
        genderText= (TextView) findViewById(R.id.txt_gender);
        patientSpinner=(Spinner)findViewById(R.id.patientSpinner);
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(MDLiveGetStarted.this);
                finish();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });
        userInfoObject=new HashMap<>();

    }
    /**
     *
     * The Click event for the corresponding views should be done here on clicking the
     * Dateof Birth layout the native date picker will be opened and the selected date
     * from the picker will set in the dateTextView.Similarly the location will also be done.
     * We can either select the current location or by using the manual search the text
     * will be defined
     *
     */

    private void setonClickListener() {
        try{
            ((Button)findViewById(R.id.SavContinueBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDateOfBirth();
                    try{
                        if(patientSpinner!=null) {
                            if ((dependentList.size() >= IntegerConstants.ADD_CHILD_SIZE) && patientSpinner.getSelectedItem().toString().equals("Add Child")) {
                                DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "1800888444"));// TODO : Change it one number is confirmed
                                        startActivity(intent);
                                    }
                                };

                                DialogInterface.OnClickListener negativeOnClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                };

                                Utils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.app_name), "Please call \n" +
                                                "1-888-818-0978 to \nadd another child.", "Call Now", "Dismiss",
                                        positiveOnClickListener,negativeOnClickListener);
//                                Utils.alert(pDialog, MDLiveGetStarted.this, "Please call 1-800-XXX-XXXX to \nadd another child.");
                            } else {
                                if (patientSpinner.getSelectedItem().toString().equals("Add Child")) {
                                    Intent intent = new Intent(MDLiveGetStarted.this, MDLiveFamilymember.class);
                                    startActivity(intent);
                                } else {

                                    SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(PreferenceConstants.PATIENT_NAME, patientSpinner.getSelectedItem().toString());
                                     if(!dependentList.contains(patientSpinner.getSelectedItem().toString())){
                                         editor.putString(PreferenceConstants.DEPENDENT_USER_ID,null);
                                     }
                                    editor.commit();
                                    Intent intent = new Intent(MDLiveGetStarted.this, MDLiveChooseProvider.class);
                                    startActivity(intent);
                                }

                            }
                        }else {
                            ((Button)findViewById(R.id.SavContinueBtn)).setBackgroundColor(getResources().getColor(R.color.grey_txt));
                        }
                    }catch (Exception e){

                    }


                }
            });
            DobLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // datePickerDialog.show();
                }
            });
            LocationLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent LocationIntent  = new Intent(MDLiveGetStarted.this,MDLiveLocation.class);
                    startActivityForResult(LocationIntent, 2222);
                    SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                    SavedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, null);
                    if(SavedLocation != null && SavedLocation.length() != 0)
                        locationTxt.setText(SavedLocation);




                }
            });
            getDateOfBirth();
            GetCurrentDate((TextView) findViewById(R.id.dobTxt));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * @param list,spinner
     * The native Spinner will be shown on selecting the dependent name and bt selecting
     * the gender.The detSpinnerValues methos will be called in the user basic info
     * services.In that services the dependent name will be fetched and it should be populated in the
     * arraylist. The spinner view and the arraylist will be passed as the parameter for the
     * setSpinnerValues method.Similarly the gender arraylist will also be defined.
     *
     */

    private void setSpinnerValues(final ArrayList<String> list, final Spinner spinner) {


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                int item = spinner.getSelectedItemPosition();
                String dependentName = spinner.getSelectedItem().toString();
                if(dependentName.equals(dependentList.get(0))){
                    userInfoChange((JSONObject)userInfoObject.get(dependentName));
                    Log.e("Inside Condition","Coming");;
                       /* Log.e("Inside Condition","Coming");
                        PatientList.clear();
                        dependentList.clear();
                        loadUserInformationDetails();*/;

                }
                if(list!=null){
                    if(list.size()>= IntegerConstants.ADD_CHILD_SIZE)
                    {
                        if(StringConstants.ADD_CHILD.equalsIgnoreCase(dependentName))
                        {
                            DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "1-888-818-0978"));// TODO : Change it one number is confirmed
                                    startActivity(intent);
                                }
                            };

                            DialogInterface.OnClickListener negativeOnClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            };
//                            Utils.alert(pDialog,MDLiveGetStarted.this,"Please call 1-800-XXX-XXXX to \nadd another child.");
                            Utils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.app_name), "Please call 1-888-818-0978 to \nadd another child.", "Call Now", "Dismiss",
                                    positiveOnClickListener,negativeOnClickListener);
                        }else
                        {
                            Log.d("Dep Name", dependentName);
                            loadDependentInformationDetails(dependentName);
                        }
                    }else {
                        if (StringConstants.ADD_CHILD.equalsIgnoreCase(dependentName)) {
                            Intent intent = new Intent(MDLiveGetStarted.this, MDLiveFamilymember.class);
                            startActivity(intent);
                        }else {
                            Log.d("Dep Name", dependentName);
                            loadDependentInformationDetails(dependentName);
                        }
                    }
                }


            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
    }

    private void loadDependentInformationDetails(String dependentName) {
        for(HashMap<String,String> tmpMap : PatientList){
            Log.d("Dep Name  - TmpMap", tmpMap.get("name"));
            if(tmpMap.get("name").equalsIgnoreCase(dependentName)){
                loadDependentUserInformationDetails(tmpMap.get("id"));
                break;
            }
        }
    }


    public void userInfoChange(JSONObject resObj){
        try{
            JSONObject personalInfo = resObj.getJSONObject("personal_info");
            isFemale = personalInfo.getString("gender").equalsIgnoreCase("female");
            DateTxt.setText(personalInfo.getString("birthdate"));
            locationTxt.setText(personalInfo.getString("state"));
            genderText.setText(personalInfo.getString("gender"));
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(resObj.toString());
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PATIENT_NAME, personalInfo.getString("first_name") + " " +personalInfo.getString("last_name"));
            editor.putString(PreferenceConstants.GENDER, personalInfo.getString("gender"));
            editor.commit();
        }catch (Exception e){

        }
    }

    /**
     * Fetching the values from the native date picker and the picker listener was implemented
     * for the particular native date picker.
     */
    private void getDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, pickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }

    /**
     *
     * Load user information Details.
     * Class : UserBasicInfoServices - Service class used to fetch the user basic information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadUserInformationDetails() {
        if(!pDialog.isShowing()) {
            pDialog.show();
        }
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog(pDialog);
                handleSuccessResponse(response);
                loadFamilyMember();
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        // Show timeout error message
                        pDialog.dismiss();
                        Utils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest("",successCallBackListener, errorListener);
    }
    /**
     * Load Family Member Type Details.
     * Class : FamilyMembersList - Service class used to fetch the Family Member List information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void loadFamilyMember() {
        if(!pDialog.isShowing()) {
            pDialog.show();
        }
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utils.hideProgressDialog(pDialog);
                handleSuccessResponseFamilyMember(response);
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
                        Utils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }
            }};

        FamilyMembersList family_services = new FamilyMembersList(MDLiveGetStarted.this, null);
        family_services.getFamilyMember(responseListener, errorListener);
    }


    private void loadDependentUserInformationDetails(String depenedentId) {
        if(!pDialog.isShowing()) {
            pDialog.show();
        }
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Dep Response", response.toString());
                Utils.hideProgressDialog(pDialog);
                handleDependentSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                Utils.hideProgressDialog(pDialog);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Utils.hideProgressDialog(pDialog);
                            }
                        };
                        // Show timeout error message
                        pDialog.dismiss();
                        Utils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest(depenedentId,successCallBackListener, errorListener);
    }

    /**
     *
     * Successful Response Handler for Family Member.This response will return the patient name
     * and the corresponding id's of the user.These two were populated to the Arraylist .
     *
     */
    private void handleSuccessResponseFamilyMember(JSONObject response) {
        try {
            isUserInfo=false;
            if(serviceCount == 2){
                pDialog.dismiss();
                serviceCount = 0;
            }
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.USER_PREFERENCES, response.toString());
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            JsonArray conditionsSearch = responObj.get("dependant_users").getAsJsonArray();

            for(int i=0;i<conditionsSearch.size();i++) {
                 strPatientName = conditionsSearch.get(i).getAsJsonObject().get("name").getAsString();
                HashMap<String, String> test = new HashMap<String, String>();
                test.put("name",strPatientName);

                test.put("id",conditionsSearch.get(i).getAsJsonObject().get("id").getAsString());
                Log.e("dependent list",strPatientName);
                dependentList.add(strPatientName);
                PatientList.add(test);


//                patientName.setText(conditionsSearch.get(0).getAsJsonObject().get("name").getAsString());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        HashMap<String, String> test = new HashMap<String, String>();

        dependentList.add(StringConstants.ADD_CHILD);

        setSpinnerValues(dependentList, patientSpinner);
    }
       /**
     *
     *  Successful Response Handler for Load Basic Info.The user basic info will provider the gender
     *  of the user and the date of birth of the corresponding user.
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            isUserInfo=true;
            JSONObject personalInfo = response.getJSONObject("personal_info");
            userInfoObject.put(personalInfo.getString("first_name") + " " + personalInfo.getString("last_name"),response);
            isFemale = personalInfo.getString("gender").equalsIgnoreCase("female");
            DateTxt.setText(personalInfo.getString("birthdate"));
            locationTxt.setText(personalInfo.getString("state"));
            genderText.setText(personalInfo.getString("gender"));
            String formattedNumber = PhoneNumberUtils.formatNumber (personalInfo.getString("phone"));
            Log.e("formattedNumber---->",formattedNumber);
            ((TextView)findViewById(R.id.telephoneTxt)).setText(formattedNumber);


            dependentList.add(0,personalInfo.getString("first_name") + " " + personalInfo.getString("last_name")) ;
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PATIENT_NAME, personalInfo.getString("first_name") + " " +personalInfo.getString("last_name"));
            editor.putString(PreferenceConstants.GENDER, personalInfo.getString("gender"));
            editor.commit();
            pDialog.dismiss();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     *  Successful Response Handler for Load Basic Info.The user basic info will provider the gender
     *  of the user and the date of birth of the corresponding user.
     *
     */

    private void handleDependentSuccessResponse(JSONObject response) {
        try {
            isUserInfo=true;
            JSONObject personalInfo = response.getJSONObject("personal_info");
            isFemale = personalInfo.getString("gender").equalsIgnoreCase("female");
            DateTxt.setText(personalInfo.getString("birthdate"));
            locationTxt.setText(personalInfo.getString("state"));
            genderText.setText(personalInfo.getString("gender"));
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
           /* SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PATIENT_NAME, personalInfo.getString("first_name") + " " +personalInfo.getString("last_name"));
            editor.putString(PreferenceConstants.GENDER, personalInfo.getString("gender"));
            editor.commit();*/
            pDialog.dismiss();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * The date Of Birth text will be saved in the preferences
     *
     */

    private void saveDateOfBirth() {
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.DATE_OF_BIRTH, DateTxt.getText().toString());
        editor.putString(PreferenceConstants.LOCATION, SavedLocation);

        editor.commit();
    }

    /**
     * The Current date and time will be retrieved by using this method.
     * @param selectedText - the corresponding Textview will be passed as an parameter so the
     * date will be set in the corresponding view.
     *
     */
    public void GetCurrentDate(TextView selectedText)
    {
        // Get current date by calender
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date
        selectedText.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("/").append(day).append("/")
                .append(year).append(" "));
    }

    /*Native date picker listener for the date picker on clicking this picker the current
    date , the current month and the current year can be fetched .The selected date or the
    current date will set in the date TextView.
    */
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, selectedMonth, selectedDay);
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            Calendar currendDate = Calendar.getInstance();
            currendDate.setTime(new Date());

            DateTxt.setText(new StringBuilder().append(month + 1)
                    .append("/").append(day).append("/").append(year)
                    .append(" "));

        }
    };
    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        Utils.movetohome(MDLiveGetStarted.this, MDLiveLogin.class);
    }

}

