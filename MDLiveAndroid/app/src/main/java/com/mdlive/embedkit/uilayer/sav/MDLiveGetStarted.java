package com.mdlive.embedkit.uilayer.sav;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.embedkit.uilayer.helpandsupport.MDLiveHelpAndSupportActivity;
import com.mdlive.embedkit.uilayer.login.MDLiveDashBoardFragment;
import com.mdlive.embedkit.uilayer.login.MDliveDashboardActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.myaccounts.MyAccountActivity;
import com.mdlive.embedkit.uilayer.myhealth.activity.MDLiveMyHealthActivity;
import com.mdlive.embedkit.uilayer.symptomchecker.MDLiveSymptomCheckerActivity;
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
import java.util.HashMap;
import java.util.List;
/**
 * The GetStarted class has the dependents name,Date of Birth ,gender and the Phone number
 * fields. Along with that it also contains the disclaimer text for the Telephone number.
 * The phone number field alone can be editable.
 */

public class  MDLiveGetStarted extends MDLiveBaseActivity  implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private ProgressDialog pDialog = null;
    private TextView locationTxt,DateTxt,genderText;
    private String strPatientName,SavedLocation;
    public static boolean isFemale;
    private ArrayList<HashMap<String, String>> PatientList = new ArrayList<HashMap<String, String>>();
    private  ArrayList<String> dependentList = new ArrayList<String>();
    private Spinner patientSpinner;
    private EditText  phonrNmberEditTxt;
    private String dependentName=null;
    private String userInfoJSONString;
    ArrayAdapter<String> dataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_get_started);

        //getSupportActionBar().hide();
        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, NavigationDrawerFragment.newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, MDLiveDashBoardFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

        MdliveUtils.hideSoftKeyboard(this);
        initialiseData();
         /*  Load Services*/
        loadUserInformationDetails();
    }
    /**
     *
     * This button action is for navigating to the Provider screen.Here we will be checking
     * the dependent list size. It should be compared with the Remaining Count which comes
     * from the user info services.If the dependent list size is greater than the remaining
     * user count then the Alert will be shown.Also when the spinner Action item has the Add Child
     * String in that case it will be navigated to the MDLiveFamilyMember class.else it will
     * be navigated to the MDLiveChooseProvider Screen.
     *
     */

    public void nextBtnAction(View v){
        saveDateOfBirth();
        try{
            if(patientSpinner!=null) {
                if ((dependentList.size() >= IntegerConstants.ADD_CHILD_SIZE) && patientSpinner.getSelectedItem().toString().equals(StringConstants.ADD_CHILD)) {
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

                    MdliveUtils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.app_name),getResources().getString(R.string.call_to_add_another_child),  StringConstants.ALERT_DISMISS,StringConstants.ALERT_CALLNOW,
                            negativeOnClickListener,  positiveOnClickListener);


                } else {
                    if (patientSpinner.getSelectedItem().toString().equals(StringConstants.ADD_CHILD)) {
                        Intent intent = new Intent(MDLiveGetStarted.this, MDLiveFamilymember.class);
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
                        if(phonrNmberEditTxt.getText().toString().length()<IntegerConstants.PHONENUMBER_LENGTH)
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
            }
        }catch (Exception e){
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


    private void initialiseData() {
        locationTxt= (TextView) findViewById(R.id.locationTxt);
        DateTxt = (TextView) findViewById(R.id.dobTxt);
        genderText= (TextView) findViewById(R.id.txt_gender);
        patientSpinner=(Spinner)findViewById(R.id.patientSpinner);
        setProgressBar(findViewById(R.id.progressDialog));

        ((ImageView)findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveGetStarted.this);
                onBackPressed();
            }
        });

    }
    /**
     *
     * The Click event for the Current location was done here and on clicking the location
     * will be navigated to the MDLiveLocation class. The location can be fetched either by
     * selecting the Zipcode or by using the current location or by selecting the state.
     * We will be having the stateid and the state name for each location.The stateId
     * will be passed to the further classes through preferences and the state name
     * will be displayed in the Patient Information screen.
     *
     */


    public void goToLocation(View v){
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

    /**
     * @param requestCode : The request code Will be passed from the current Activity
     *                        to the location actiivty.
     * @param  resultCode : The result code will produce the result from the other
     *                   Activity to the Current Activity.
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IdConstants.REQUEST_ADD_CHILD && resultCode == Activity.RESULT_OK){
            loadUserInformationDetails();
        }else if(requestCode == IdConstants.REQUEST_LOCATION_CHANGE && resultCode == Activity.RESULT_OK){
            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
            String longNameLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, "Florida");
            locationTxt.setText(longNameLocation);
        }
    }

    /**
     * @param list,spinner
     * The native Spinner will be shown on selecting the dependent name
     * The setSpinnerValues method will be called in the user basic info
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
                if (list != null) {
                    if (list.size() >= IntegerConstants.ADD_CHILD_SIZE) {
                        if (StringConstants.ADD_CHILD.equalsIgnoreCase(dependentName)) {
                            patientSpinner.setSelection(IntegerConstants.NUMBER_ZERO);
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
                                    patientSpinner.setSelection(IntegerConstants.NUMBER_ZERO);

                                }
                            };
                            MdliveUtils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.app_name), "Please call 1-888-818-0978 to \nadd another child.", StringConstants.ALERT_DISMISS, StringConstants.ALERT_CALLNOW,
                                    negativeOnClickListener, positiveOnClickListener);
                        } else {
                            loadDependentInformationDetails(dependentName, position);

                        }
                    } else {
                        if (StringConstants.ADD_CHILD.equalsIgnoreCase(dependentName)) {
                            Intent intent = new Intent(MDLiveGetStarted.this, MDLiveFamilymember.class);
                            intent.putExtra("user_info", userInfoJSONString);
                            startActivityForResult(intent, IdConstants.REQUEST_ADD_CHILD);
                            MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                            patientSpinner.setSelection(IntegerConstants.NUMBER_ZERO);
                        } else {
                            loadDependentInformationDetails(dependentName, position);

                        }
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
    /**
     * This method is for loading the dependent information details.The dependent's
     * date of birth and the dependents gender will be varied for each dependent.
     * on selecting the dependent the corresponding response that is date
     * of birth and the gender will be populated to the respective fields.
     * @param dependentName-User selected dependent name from spinner
     * @param position-User selected dependent item position,used to map user dependent id
     */

    private void loadDependentInformationDetails(String dependentName,int position) {
        try{
            if(position!=IntegerConstants.NUMBER_ZERO){
                HashMap<String,String> tmpMap=PatientList.get(position-1);
                if(!tmpMap.containsKey("authorized")){
                    loadUserInformationDetails();
                }else{
                    if(tmpMap.get("name").equalsIgnoreCase(dependentName)&&tmpMap.get("authorized").equalsIgnoreCase("true")){//Condition to check whether the user is below 18 years old
                        if(!dependentList.get(0).equals(tmpMap.get("name"))){//Condition to avoid calling dependent service if already data is available for dependents

                            loadDependentUserInformationDetails(tmpMap.get("id"));//Method call to load the selected dependent details.

                        }
                    }else if(tmpMap.get("name").equalsIgnoreCase(dependentName)&&tmpMap.get("authorized").equalsIgnoreCase("false")){
                        DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setSpinnerValues(dependentList,patientSpinner);
                            }
                        };

                        MdliveUtils.showDialog(MDLiveGetStarted.this, "",getString(R.string.adult_share_account), getString(R.string.Ok), "", positiveOnClickListener, null);
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /***
     *This method is used to get user Pending Appointments History from server.
     * MDLivePendigVisitService-class is responsible for sending request to the server.
     * if the response is null it will be navigated to the corresponding screen that
     * is MDLiveChooseProvider. Incase if the response has some datas it will be
     * navigated to the PendingVisit Screen.
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
                try {
                    MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this, error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, MDLiveGetStarted.this);
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
     * @param response
     */
    public void handlePendingResponse(String response){
        try{
            JSONObject resObj=new JSONObject(response);
            JSONArray appointArray=resObj.getJSONArray("appointments");
            JSONArray onCallAppointmentArray=resObj.getJSONArray("oncall_appointments");
            if(appointArray.length()!=IntegerConstants.NUMBER_ZERO){
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
     *  here small validation has been done here.The Phone number format has been
     *  done here.the number which comes from the service is also formatted and the
     *  number which we are editing is also formatted.The Phone number should not
     *  exceed more than 10 digits.
     *
     */

    private void handleSuccessResponse(JSONObject response) {
        try {
            JSONObject personalInfo = response.getJSONObject("personal_info");
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
                    }
                }
                genderText.setText(personalInfo.getString("gender"));
                String numStr = personalInfo.getString("phone");
                try {
                    String formattedString= MdliveUtils.phoneNumberFormat(Long.parseLong(numStr));
                    phonrNmberEditTxt = (EditText) findViewById(R.id.telephoneTxt);

                    phonrNmberEditTxt.setText(formattedString);
                    phonrNmberEditTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String changeEditText = phonrNmberEditTxt.getText().toString();
                            if(changeEditText.length()>=IntegerConstants.PHONENUMBER_LENGTH){
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
            }

            if(dependentList.size()>IntegerConstants.NUMBER_ZERO){
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
     * The Family memeber has response like the patient name and the id for the
     * corresponding users.
     *
     */
    private void handleSuccessResponseFamilyMember(JSONObject response) {
        try {
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
                test.put("authorized",conditionsSearch.get(i).getAsJsonObject().get("primary_authorized").getAsString());
                Log.e("dependent list", strPatientName);
                dependentList.add(strPatientName);
                PatientList.add(test);
            }
//
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
     *  The state name can be fetched and the state name is compared to the state id
     *  The state id is needed because it is passes to the other activity.
     *
     */

    private void handleDependentSuccessResponse(JSONObject response) {
        try {
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
            if(notiObj.getInt("upcoming_appointments")>=1){
                getPendingAppointments();
            }
            JsonArray conditionsSearch = responObj.get("dependant_users").getAsJsonArray();
            for(int i=0;i<conditionsSearch.size();i++) {
                strPatientName = conditionsSearch.get(i).getAsJsonObject().get("name").getAsString();
                HashMap<String, String> parentNameMap = new HashMap<String, String>();
                parentNameMap.put("name",strPatientName);
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
     * The location will also be saved to the preferences.
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
     * This function is for calling the Closing Activity Animation
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveGetStarted.this);
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            // Home
            case 0:
                startActivityWithClassName(MDliveDashboardActivity.class);
                break;

            // Talk to a Doctor
            case 1:

                break;

            // Schedule a Visit
            case 2:

                break;

            // My Health
            case 3:
                startActivityWithClassName(MDLiveMyHealthActivity.class);
                break;

            // Message Center
            case 4:
                startActivityWithClassName(MessageCenterActivity.class);
                break;

            // MDLIVE Assist
            case 5:
                showMDLiveAssistDialog();
                break;

            // Symptom Checker
            case 6:
                startActivityWithClassName(MDLiveSymptomCheckerActivity.class);
                break;

            // My Accounts
            case 7:
                startActivityWithClassName(MyAccountActivity.class);
                break;

            // Support
            case 8:
                startActivityWithClassName(MDLiveHelpAndSupportActivity.class);
                break;

            // Share this App
            case 9:

                break;

            // Sign Out
            case 10:

                break;
        }
    }
}


