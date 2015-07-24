package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendigVisitService;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.util.Calendar.MONTH;

public class   MDLiveGetStarted extends MDLiveBaseActivity {
    private LinearLayout DobLl,LocationLl;
    private ProgressDialog pDialog = null;
    private TextView locationTxt,DateTxt,genderText;
    private int month,day,year;
    private String strPatientName,SavedLocation;
    private DatePickerDialog datePickerDialog;
    // private RelativeLayout progressBar;
    public static boolean isFemale;
    private ArrayList<HashMap<String, String>> PatientList = new ArrayList<HashMap<String, String>>();
    private  ArrayList<String> dependentList = new ArrayList<String>();
    private Spinner patientSpinner;

    private EditText  phonrNmberEditTxt;
    private String parentName,dependentName=null;//Variable to save the parent name.
    private String userInfoJSONString;
    ArrayAdapter<String> dataAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_get_started);
        MdliveUtils.hideSoftKeyboard(this);
        initialiseData();
        setonClickListener();
         /*  Load Services*/
        loadUserInformationDetails();
//
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
    public void onResume() {
        super.onResume();
    }
    private void initialiseData() {
        locationTxt= (TextView) findViewById(R.id.locationTxt);
        DateTxt = (TextView) findViewById(R.id.dobTxt);
        DobLl = (LinearLayout) findViewById(R.id.dobLl);
        LocationLl = (LinearLayout) findViewById(R.id.locationLl);
        genderText= (TextView) findViewById(R.id.txt_gender);
        patientSpinner=(Spinner)findViewById(R.id.patientSpinner);
        //progressBar = (RelativeLayout)findViewById(R.id.progressDialog);
        setProgressBar(findViewById(R.id.progressDialog));
//        patientInfo= (LinearLayout) findViewById(R.id.patientInfo);
        /**
         * The back image will pull you back to the Previous activity
         * The home button will pull you back to the Dashboard activity
         */

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveGetStarted.this);
                onBackPressed();
            }
        });
        ((ImageView)findViewById(R.id.homeImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetohome();
            }
        });


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
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + StringConstants.ALERT_PHONENUMBER.replaceAll("-","")));
                                        startActivity(intent);
                                        MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);

                                    }
                                };

                                DialogInterface.OnClickListener negativeOnClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        dialogInterface.dismiss();
                                    }
                                };

                                MdliveUtils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.app_name), "Please call " +
                                                "1-888-818-0978 to \nadd another child.",  StringConstants.ALERT_DISMISS,StringConstants.ALERT_CALLNOW,
                                        negativeOnClickListener,  positiveOnClickListener);

                                                                /*new AlertDialog.Builder(
                                        new ContextThemeWrapper(MDLiveGetStarted.this,R.style.AppCompatAlertDialogStyle));*/
//                                Utils.alert(pDialog, MDLiveGetStarted.this, "Please call 1-800-XXX-XXXX to \nadd another child.");
                            } else {
                                if (patientSpinner.getSelectedItem().toString().equals(StringConstants.ADD_CHILD)) {
                                    Intent intent = new Intent(MDLiveGetStarted.this, MDLiveFamilymember.class);
                                    Log.e("TEST", "JSON :" + userInfoJSONString);
                                    intent.putExtra("user_info", userInfoJSONString);
                                    startActivityForResult(intent, IdConstants.REQUEST_ADD_CHILD);
                                    MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                                } else {
                                    SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(PreferenceConstants.PATIENT_NAME, patientSpinner.getSelectedItem().toString());
                                    if(!dependentList.contains(patientSpinner.getSelectedItem().toString())){
                                        editor.putString(PreferenceConstants.DEPENDENT_USER_ID,null);
                                        Log.d("Dep","null");
                                    }
                                    editor.commit();
                                    if(phonrNmberEditTxt.getText().toString().length()<12)
                                    {
                                        MdliveUtils.alert(pDialog, MDLiveGetStarted.this, "Please enter a valid Phone number");

                                    }else
                                    {
                                        editor.putString(PreferenceConstants.PHONE_NUMBER, phonrNmberEditTxt.getText().toString()
                                                .replace("-", ""));
                                        Intent intent = new Intent(MDLiveGetStarted.this, MDLiveChooseProvider.class);
                                        startActivity(intent);
                                        MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                                    }

                                }

                            }
                        }else {
                            ((Button)findViewById(R.id.SavContinueBtn)).setBackgroundColor(getResources().getColor(R.color.grey_txt));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
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
                    LocationIntent.putExtra("activitycaller", "getstarted");
                    startActivityForResult(LocationIntent, IdConstants.REQUEST_LOCATION_CHANGE);
                    MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                    SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                    String  longLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, "Florida");
                    SavedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, "FL");


                    if(longLocation != null && longLocation.length() != 0)
                        locationTxt.setText(longLocation);
                }
            });
            getDateOfBirth();
            GetCurrentDate((TextView) findViewById(R.id.dobTxt));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onactivity Result", "Yes called " + resultCode);
        if(requestCode == IdConstants.REQUEST_ADD_CHILD && resultCode == Activity.RESULT_OK){
            loadUserInformationDetails();
        }else if(requestCode == IdConstants.REQUEST_LOCATION_CHANGE && resultCode == Activity.RESULT_OK){
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            String longNameLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, "Florida");
            Log.e("Getstarted page",longNameLocation);
            locationTxt.setText(longNameLocation);
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
        Log.e("List of Spinner value ", list.toString());
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, final int position, long id) {
                dependentName = spinner.getSelectedItem().toString();
                if(list!=null){
                    if(list.size()>= IntegerConstants.ADD_CHILD_SIZE)
                    {
                        if(StringConstants.ADD_CHILD.equalsIgnoreCase(dependentName))
                        {
                            patientSpinner.setSelection(0);
                            DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + StringConstants.ALERT_PHONENUMBER));// TODO : Change it one number is confirmed
                                    startActivity(intent);
                                    MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);

                                }
                            };

                            DialogInterface.OnClickListener negativeOnClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    dialogInterface.dismiss();
                                    patientSpinner.setSelection(0);
                                    //This method is called bcs primary name shld come first after tapping the Add child btn
                                    //loadUserInformationDetails();
//                                        onResume();
                                    //loadDependentInformationDetails(dependentName,position);

                                }
                            };
                            MdliveUtils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.app_name), "Please call 1-888-818-0978 to \nadd another child.",  StringConstants.ALERT_DISMISS,StringConstants.ALERT_CALLNOW,
                                    negativeOnClickListener,positiveOnClickListener );
                        }else
                        {
                            loadDependentInformationDetails(dependentName,position);

                        }
                    }else {
                        if (StringConstants.ADD_CHILD.equalsIgnoreCase(dependentName)) {
                            Intent intent = new Intent(MDLiveGetStarted.this, MDLiveFamilymember.class);
                            intent.putExtra("user_info", userInfoJSONString);
                            startActivityForResult(intent, IdConstants.REQUEST_ADD_CHILD);
                            MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                            patientSpinner.setSelection(0);
                        }else {
                            loadDependentInformationDetails(dependentName, position);

                        }
                    }
                }
            }

            //}
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
    }
    /**
     *
     * @param dependentName-User selected dependent name from spinner
     * @param position-User selected dependent item position,used to map user dependent id
     */

    private void loadDependentInformationDetails(String dependentName,int position) {
        try{
            if(position!=0){
                HashMap<String,String> tmpMap=PatientList.get(position-1);
                if(!tmpMap.containsKey("authorized")){
                    loadUserInformationDetails();
                }else{
                    if(tmpMap.get("name").equalsIgnoreCase(dependentName)&&tmpMap.get("authorized").equalsIgnoreCase("true")){//Condition to check whether the user is below 18 years old
                            loadDependentUserInformationDetails(tmpMap.get("id"));//Method call to load the selected dependent details.


                    }else if(tmpMap.get("name").equalsIgnoreCase(dependentName)&&tmpMap.get("authorized").equalsIgnoreCase("false")){
                        DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setSpinnerValues(dependentList,patientSpinner);
                            }
                        };
//
                        MdliveUtils.showDialog(MDLiveGetStarted.this, "", "The adult dependent has opted not to share his account", "Ok", "", positiveOnClickListener, null);
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }




        //   }


    }




    /***
     *This method is used to get user Pending Appointments History from server.
     * MDLivePendigVisitService-class is responsible for sending request to the server
     */

    public void getPendingAppointments(){
        showProgress();
        NetworkSuccessListener successListener=new NetworkSuccessListener() {
            @Override
            public void onResponse(Object response) {
                hideProgress();
                handlePendingResponse(response.toString());
            }
        };
        NetworkErrorListener errorListner=new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        MdliveUtils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
                    }
                }

            }
        };

        MDLivePendigVisitService getApponitmentsService=new MDLivePendigVisitService(MDLiveGetStarted.this,pDialog);
        getApponitmentsService.getUserPendingHistory(successListener,errorListner);
    }



    /**
     *
     * THis function handles the pending visits if any. If there is any pending visits,
     * the user will be taken to PEndingVisits screen, else the user will ber taken to
     * getstarted screen.
     *
     * @param response
     */
    public void handlePendingResponse(String response){
        try{
            JSONObject resObj=new JSONObject(response);
            JSONArray appointArray=resObj.getJSONArray("appointments");
            JSONArray onCallAppointmentArray=resObj.getJSONArray("oncall_appointments");
            if(appointArray.length()!=0){
                String docName=appointArray.getJSONObject(0).getString("physician_name");
                String appointmnetID=appointArray.getJSONObject(0).getString("id");
                String chiefComplaint=appointArray.getJSONObject(0).getString("chief_complaint");
                Intent pendingVisitIntent = new Intent(getApplicationContext(), MDLivePendingVisits.class);
                pendingVisitIntent.putExtra("DocName",docName); // The doctor name  from service on successful response
                pendingVisitIntent.putExtra("AppointmentID",appointmnetID); // The Appointment id  from service on successful response
                pendingVisitIntent.putExtra("Reason",chiefComplaint); // The Reason for visit from service on successful response
                startActivity(pendingVisitIntent);
                MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
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
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                handleSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this, error, pDialog);
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest("", successCallBackListener, errorListener);
    }
    /**
     * Load Family Member Type Details.
     * Class : FamilyMembersList - Service class used to fetch the Family Member List information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */

    private void loadDependentUserInformationDetails(String depenedentId) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Dep Response", response.toString());
                hideProgress();
                handleDependentSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this,error,pDialog);
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest(depenedentId,successCallBackListener, errorListener);
    }


    /**
     *
     *  Successful Response Handler for Load Basic Info.The user basic info will provider the gender
     *  of the user and the date of birth of the corresponding user.
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            JSONObject personalInfo = response.getJSONObject("personal_info");
            Log.d("Personal Info----->",personalInfo.toString());
            userInfoJSONString = personalInfo.toString();
            if (!personalInfo.toString().isEmpty()) {

                isFemale = personalInfo.getString("gender").equalsIgnoreCase("female");
                DateTxt.setText(personalInfo.getString("birthdate"));
                for(int i=0;i< Arrays.asList(getResources().getStringArray(R.array.stateName)).size();i++){
                    if(personalInfo.getString("state").equals(Arrays.asList(getResources().getStringArray(R.array.stateCode)).get(i))){
                        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, personalInfo.getString("state"));
                        editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, Arrays.asList(getResources().getStringArray(R.array.stateName)).get(i));
                        editor.commit();
                        locationTxt.setText(Arrays.asList(getResources().getStringArray(R.array.stateName)).get(i));
                        Log.e("Location Service -->",Arrays.asList(getResources().getStringArray(R.array.stateName)).get(i));
                    }
                }
                genderText.setText(personalInfo.getString("gender"));
                String numStr = personalInfo.getString("phone");
                try {
                    String formattedString= MdliveUtils.phoneNumberFormat(Long.parseLong(numStr));
                    Log.e("formattedNumber---->",formattedString);
                    phonrNmberEditTxt = (EditText) findViewById(R.id.telephoneTxt);

                    phonrNmberEditTxt.setText(formattedString);
                    phonrNmberEditTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String changeEditText = phonrNmberEditTxt.getText().toString();
                            if(changeEditText.length()>=10){
                                if(!changeEditText.contains("-")){
                                    try {
                                        String formattedString = MdliveUtils.phoneNumberFormat(Long.parseLong(changeEditText));
                                        phonrNmberEditTxt.setText(formattedString);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }

                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } catch (Exception e) {
                }
            } else {
//                Utils.alert(pDialog,MDLiveGetStarted.this,"There is an issue loading your information. Please try again in a moment. If the problem persists please call the MDLIVE Helpdesk at 1-888-995-2183");
            }

            if(dependentList.size()>0){
                dependentList.clear();
                PatientList.clear();
            }

            dependentList.add(personalInfo.getString("first_name") + " " + personalInfo.getString("last_name")) ;
            JsonParser parser = new JsonParser();
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PATIENT_NAME, personalInfo.getString("first_name") + " " +personalInfo.getString("last_name"));
            editor.putString(PreferenceConstants.GENDER, personalInfo.getString("gender"));
            editor.commit();
            hideProgress();
            handleSuccessResponseFamilyMember(response);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *
     * Successful Response Handler for Family Member.This response will return the patient name
     * and the corresponding id's of the user.These two were populated to the Arraylist .
     *
     */
    private void handleSuccessResponseFamilyMember(JSONObject response) {
        try {
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PreferenceConstants.USER_PREFERENCES, response.toString());
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            Log.d("FmyMember response--->",responObj.toString());
//            if (!responObj.isJsonNull()) {
            JsonArray conditionsSearch = responObj.get("dependant_users").getAsJsonArray();
            for(int i=0;i<conditionsSearch.size();i++) {
                strPatientName = conditionsSearch.get(i).getAsJsonObject().get("name").getAsString();
                HashMap<String, String> test = new HashMap<String, String>();
                test.put("name",strPatientName);
                test.put("id",conditionsSearch.get(i).getAsJsonObject().get("id").getAsString());
                test.put("authorized",conditionsSearch.get(i).getAsJsonObject().get("primary_authorized").getAsString());
                Log.e("dependent list", strPatientName);
                dependentList.add(strPatientName);
                PatientList.add(test);
            }
//            }
//            else {
//                Utils.alert(pDialog,MDLiveGetStarted.this,"There is an issue loading your information. Please try again in a moment. If the problem persists please call the MDLIVE Helpdesk at 1-888-995-2183");
//            }

        }catch(Exception e){
            e.printStackTrace();
        }
        dependentList.add(StringConstants.ADD_CHILD);
        setSpinnerValues(dependentList, patientSpinner);

    }

    /**
     *
     *  Successful Response Handler for Load Basic Info.The user basic info will provider the gender
     *  of the user and the date of birth of the corresponding user.
     *
     */

    private void handleDependentSuccessResponse(JSONObject response) {
        try {
            Log.d("Dependent----->", "Called Dependent");
            dependentList.clear();
            PatientList.clear();
            JSONObject personalInfo = response.getJSONObject("personal_info");
            dependentList.add(personalInfo.getString("first_name") + " " + personalInfo.getString("last_name")) ;
            JSONObject notiObj=response.getJSONObject("notifications");
            isFemale = personalInfo.getString("gender").equalsIgnoreCase("female");
            DateTxt.setText(personalInfo.getString("birthdate"));
            String state = personalInfo.getString("state");
            if(state.length()<3) {
                List<String> stateArr = Arrays.asList(getResources().getStringArray(R.array.stateName));
                List<String> stateIdArr = Arrays.asList(getResources().getStringArray(R.array.stateCode));
                for (int l = 0; l < stateIdArr.size(); l++) {
                    if (state.equals(stateIdArr.get(l))) {
                        state = stateArr.get(l);
                        break;
                    }
                }
            }

            for(int i=0;i< Arrays.asList(getResources().getStringArray(R.array.stateName)).size();i++){
                if(personalInfo.getString("state").equals(Arrays.asList(getResources().getStringArray(R.array.stateCode)).get(i))){
                    SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, personalInfo.getString("state"));
                    editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, Arrays.asList(getResources().getStringArray(R.array.stateName)).get(i));
                    editor.commit();
                    locationTxt.setText(Arrays.asList(getResources().getStringArray(R.array.stateName)).get(i));
                    Log.e("Location Service -->",Arrays.asList(getResources().getStringArray(R.array.stateName)).get(i));
                }
            }

            locationTxt.setText(state);
            genderText.setText(personalInfo.getString("gender"));
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PATIENT_NAME, personalInfo.getString("first_name") + " " +personalInfo.getString("last_name"));
            editor.putString(PreferenceConstants.GENDER, personalInfo.getString("gender"));
            editor.commit();
            Log.e("Pending Appt", notiObj.getString("upcoming_appointments"));
            if(notiObj.getInt("upcoming_appointments")>=1){
                getPendingAppointments();
            }
            JsonArray conditionsSearch = responObj.get("dependant_users").getAsJsonArray();
            for(int i=0;i<conditionsSearch.size();i++) {
                strPatientName = conditionsSearch.get(i).getAsJsonObject().get("name").getAsString();
                HashMap<String, String> parentNameMap = new HashMap<String, String>();
                parentNameMap.put("name",strPatientName);
                /*test.put("id","test");
                test.put("authorized","PARENT");
                test.put("parent","parent");
                Log.e("dependent list", strPatientName);
              */
                dependentList.add(strPatientName);
                PatientList.add(parentNameMap);
            }
            setSpinnerValues(dependentList, patientSpinner);
            hideProgress();
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


        }
    };



    /**
     * The back image will pull you back to the Previous activity
     * The home button will pull you back to the Dashboard activity
     */
    public void movetohome()
    {
        MdliveUtils.movetohome(MDLiveGetStarted.this, getString(R.string.home_dialog_title), getString(R.string.home_dialog_text));
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveGetStarted.this);
    }





}


