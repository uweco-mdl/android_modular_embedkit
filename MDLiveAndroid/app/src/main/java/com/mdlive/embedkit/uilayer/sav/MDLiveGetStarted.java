package com.mdlive.embedkit.uilayer.sav;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.MDLiveBaseAppcompatActivity;
import com.mdlive.embedkit.uilayer.PendingVisits.MDLivePendingVisits;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myaccounts.AddFamilyMemberActivity;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.PharmacyDetails;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Security;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.MDLivePendigVisitService;
import com.mdlive.unifiedmiddleware.services.location.CurrentLocationServices;
import com.mdlive.unifiedmiddleware.services.provider.ChooseProviderServices;
import com.mdlive.unifiedmiddleware.services.provider.SearchProviderDetailServices;
import com.mdlive.unifiedmiddleware.services.userinfo.UserBasicInfoServices;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.OnUserChangedInGetStarted;
import static com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment.newInstance;

/**
 * The GetStarted class has the dependents name,Date of Birth ,gender and the Phone number
 * fields. Along with that it also contains the disclaimer text for the Telephone number.
 * The phone number field alone can be editable.
 */

public class  MDLiveGetStarted extends MDLiveBaseActivity implements OnUserChangedInGetStarted {
    public static Intent getGetStartedIntentWithUser(final Context context, final User user) {
        final Intent intent = new Intent(context, MDLiveGetStarted.class);
        intent.putExtra(User.USER_TAG, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private TextView locationTxt;/*,genderText*/
    private String DateTxt;
    private String strPatientName,SavedLocation,strProviderId,assistFamilyMemmber;

    private int remainingFamilyMemberCount;

    private ArrayList<HashMap<String, String>> PatientList = new ArrayList<HashMap<String, String>>();
    public static ArrayList<String> providerTypeArrayList;
    public static ArrayList<String> providerTypeIdList;
    private  ArrayList<String> dependentList = new ArrayList<String>();
    private Spinner patientSpinner;
    private EditText phonrNmberEditTxt;
    private String dependentName=null;
    private String userInfoJSONString;
    ArrayAdapter<String> dataAdapter;
    User user = null;

    //Location Services

    private LocationCooridnates locationService;
    private IntentFilter intentFilter;
    private String selectedCity, locationServiceText, shortNameText;
    private boolean isLocationFetched;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_get_started);
        clearMinimizedTime();
        clearCacheInVolley();
        this.setTitle(getString(R.string.mdl_getstarted));
        locationService = new LocationCooridnates(MDLiveGetStarted.this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                showHamburgerTick();
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MdliveUtils.hideSoftKeyboard(this);
        initialiseData();
        loadProviderType();
        clearCacheInVolley();

        try {
            if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(User.USER_TAG) != null) {
                user = getIntent().getExtras().getParcelable(User.USER_TAG);
                Log.d("Hello", "Selected User : " + user.toString());
            }
            if (user != null && user.mMode == User.MODE_DEPENDENT) {
                final SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PreferenceConstants.DEPENDENT_USER_ID, user.mId);
                editor.commit();
                loadDependentUserInformationDetails(user.mId);
                loadDependentProviderTypeDetails(user.mId);
                Log.e("Mid", user.mId);
            } else {
                loadProviderType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        loadProviderType();


        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__left_container, newInstance(), LEFT_MENU).
                    commit();

            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.dash_board__right_container, NotificationFragment.newInstance(), RIGHT_MENU).
                    commit();
        }

        findViewById(R.id.txt_alert_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsAlert();
            }
        });
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

    public void onTickClicked(View v){
        saveDateOfBirth();
        try{
            SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
            SharedPreferences.Editor searchPrefEditor = searchPref.edit();
            searchPrefEditor.putString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, locationTxt.getText().toString());
            searchPrefEditor.commit();
            Log.e("Arkansas",locationTxt.getText().toString());
            if(locationTxt.getText().toString().equals("Arkansas"))
            {
                Log.e("Arkansas",locationTxt.getText().toString());
                ChooseProviderResponseList();
            }else {

                if (patientSpinner != null) {
                    if ((remainingFamilyMemberCount < 1) && patientSpinner.getSelectedItem().toString().equals(StringConstants.ADD_FAMILY_MEMBER)) {
                        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());

                        DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + StringConstants.ALERT_PHONENUMBER.replaceAll("-", "")));
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

                        MdliveUtils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.mdl_app_name), getResources().getString(R.string.mdl_call_to_add_another_child), StringConstants.ALERT_DISMISS, StringConstants.ALERT_CALLNOW,
                                negativeOnClickListener, positiveOnClickListener);


                    } else {
                        if (patientSpinner.getSelectedItem().toString().equals(StringConstants.ADD_FAMILY_MEMBER)) {
                            Intent intent = new Intent(MDLiveGetStarted.this, AddFamilyMemberActivity.class);
                            intent.putExtra("user_info", userInfoJSONString);
                            startActivityForResult(intent, IdConstants.REQUEST_ADD_CHILD);
                            MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                        } else {
                            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(PreferenceConstants.PATIENT_NAME, patientSpinner.getSelectedItem().toString());
                            if (!dependentList.contains(patientSpinner.getSelectedItem().toString())) {
                                editor.putString(PreferenceConstants.DEPENDENT_USER_ID, null);
                            }
                            editor.commit();
                            Log.e("phne num lenght-->",phonrNmberEditTxt.getText().toString()+"Length-->"+phonrNmberEditTxt.getText().toString().length());
                            if (phonrNmberEditTxt.getText() != null && phonrNmberEditTxt.getText().toString().length() == IntegerConstants.PHONENUMBER_LENGTH) {
                                String phoneNumberText = phonrNmberEditTxt.getText().toString();
                                Log.e("phne num lenght-->",phoneNumberText+"Length-->"+phoneNumberText.length());
                                //phoneNumberText = MdliveUtils.getSpecialCaseRemovedNumber(phoneNumberText);
                                editor.putString(PreferenceConstants.PHONE_NUMBER, phoneNumberText);
                                editor.putString(PreferenceConstants.PROVIDERTYPE_ID, strProviderId);
                                if(((TextView)findViewById(R.id.providertypeTxt)).getText() != null)
                                    editor.putString(PreferenceConstants.PROVIDER_MODE, ((TextView)findViewById(R.id.providertypeTxt)).getText().toString());
                                editor.commit();
                                clearCacheInVolley();
                                Intent intent = new Intent(MDLiveGetStarted.this, MDLiveChooseProvider.class);
                                startActivity(intent);
                                MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                            } else {
                                MdliveUtils.alert(getProgressDialog(), MDLiveGetStarted.this, getString(R.string.mdl_valid_phone_number));
                            }

                        }

                    }
                }
            }
            HideKeyboard();
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

    private void HideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void initialiseData() {
        locationTxt= (TextView) findViewById(R.id.locationTxt);
        patientSpinner=(Spinner)findViewById(R.id.patientSpinner);
        providerTypeArrayList = new ArrayList<String>();
        providerTypeIdList = new ArrayList<String>();
        setProgressBar(findViewById(R.id.progressDialog));
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


    public void goToLocation(View v) {
        if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
            findViewById(R.id.txt_alert_img).setVisibility(View.GONE);
        } else {
            findViewById(R.id.txt_alert_img).setVisibility(View.VISIBLE);
        }
        Intent LocationIntent = new Intent(MDLiveGetStarted.this, MDLiveLocation.class);
        LocationIntent.putExtra("activitycaller", getString(R.string.mdl_getstarted));
        startActivityForResult(LocationIntent, IdConstants.REQUEST_LOCATION_CHANGE);
        MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
        SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        String longLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, getString(R.string.mdl_florida));
        Log.e("Long Location Nmae-->", longLocation);
        SavedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, getString(R.string.mdl_fl));

        if (longLocation != null && longLocation.length() != IntegerConstants.NUMBER_ZERO){
            locationTxt.setText(longLocation);
        }




    }

    /**
     *
     * The Click event for the Provider Type will be showing the dialog which
     * contains the provider type like Family Physician or Pediatrician or
     * Therapist.For switching over to the dependent we will be having the
     * change for provider type .This will be based on the corresponding
     * dependents.
     *
     */
    public void goToProviderType(View v) {

        showListViewDialog(providerTypeArrayList,(TextView)findViewById(R.id.providertypeTxt));

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
            String longNameLocation = settings.getString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, getString(R.string.mdl_florida));
            SavedLocation = settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, getString(R.string.mdl_fl));
            Log.e("Result Location Nmae-->",SavedLocation);
            locationTxt.setText(longNameLocation);
            SharedPreferences searchPref = this.getSharedPreferences("SearchPref", 0);
            SharedPreferences.Editor searchEditor = searchPref.edit();
            searchEditor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, SavedLocation);
            searchEditor.putString(PreferenceConstants.SEARCHFILTER_LONGNAME_LOCATION_PREFERENCES, longNameLocation);
            searchEditor.commit();
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

        SpinnerAdapter mAdapter=new SpinnerAdapter(MDLiveGetStarted.this,R.layout.mdlive_custom_spinner,list);
        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, final int position, long id) {
                dependentName = spinner.getSelectedItem().toString();
                if(list!=null){
                    if( remainingFamilyMemberCount<1)
                    {
                        if(StringConstants.ADD_FAMILY_MEMBER.equalsIgnoreCase(dependentName))
                        {
                            patientSpinner.setSelection(IntegerConstants.NUMBER_ZERO);
                            DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(StringConstants.TEL + StringConstants.ALERT_PHONENUMBER));
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
                            MdliveUtils.showDialog(MDLiveGetStarted.this, getResources().getString(R.string.mdl_app_name), getString(R.string.mdl_plscalAlert_txt, assistFamilyMemmber),  StringConstants.ALERT_DISMISS,StringConstants.ALERT_CALLNOW,
                                    negativeOnClickListener,positiveOnClickListener );
                        }else
                        {
                            loadDependentInformationDetails(dependentName,position);

                        }
                    }else {
                        if (StringConstants.ADD_FAMILY_MEMBER.equalsIgnoreCase(dependentName)) {
                            Intent intent = new Intent(MDLiveGetStarted.this, AddFamilyMemberActivity.class);
                            intent.putExtra("activitycaller", getString(R.string.mdl_getstarted));
                            intent.putExtra("user_info", userInfoJSONString);
                            startActivityForResult(intent, IdConstants.REQUEST_ADD_CHILD);
                            MdliveUtils.startActivityAnimation(MDLiveGetStarted.this);
                            patientSpinner.setSelection(IntegerConstants.NUMBER_ZERO);
                        }else {
                            loadDependentInformationDetails(dependentName, position);
                        }
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) { }
        });
    }


    /***
     * SpinnerAdapter- Class is used to inflate custom text and inflate the custom view in spinner
     */


    public class SpinnerAdapter extends ArrayAdapter<String>{
        ArrayList<String> objects;
        public SpinnerAdapter(Context ctx, int txtViewResourceId, ArrayList<String> objects){
            super(ctx, txtViewResourceId, objects);
            this.objects=objects;
        }
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt){

            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.mdlive_custom_spinner_drop_down, prnt, false);
            TextView main_text = (TextView) mySpinner.findViewById(R.id.txtPatientName);
            main_text.setText(objects.get(position));
            return mySpinner;


        }
        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt){
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.mdlive_custom_spinner_text, parent, false);
            TextView main_text = (TextView) mySpinner .findViewById(R.id.txtPatientName);
            main_text.setText(objects.get(position));

            return  mySpinner;
        }
    }






    /**
     * This function is used to clear cache from volley.This is mainly for clearing
     * the images in the medical history.It will just clear all the pictures that
     * has been loaded already in the cache.
     */
    public void clearCacheInVolley(){
        ApplicationController.getInstance().getRequestQueue(MDLiveGetStarted.this).getCache().clear();
        ApplicationController.getInstance().getBitmapLruCache().evictAll();
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
                    loadDependentProviderTypeDetails("");
                }else{
                    if(tmpMap.get("name").equalsIgnoreCase(dependentName)&&tmpMap.get("authorized").equalsIgnoreCase("true")){//Condition to check whether the user is below 18 years old
                        if(!dependentList.get(IntegerConstants.NUMBER_ZERO).equals(tmpMap.get("name"))){//Condition to avoid calling dependent service if already data is available for dependents

                            loadDependentUserInformationDetails(tmpMap.get("id"));//Method call to load the selected dependent details.
                            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                            SharedPreferences.Editor sharedEditor = sharedpreferences.edit();
                            sharedEditor.putString(PreferenceConstants.DEPENDENT_USER_ID,tmpMap.get("id"));
                            sharedEditor.commit();
                            loadDependentProviderTypeDetails(tmpMap.get("id"));
                            //Method call to load the selected dependent details.
                            SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("dependent_id",tmpMap.get("id"));
                            editor.commit();


                        }
                    }else if(tmpMap.get("name").equalsIgnoreCase(dependentName)&&tmpMap.get("authorized").equalsIgnoreCase("false")){
                        DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setSpinnerValues(dependentList,patientSpinner);
                            }
                        };

                        MdliveUtils.showDialog(MDLiveGetStarted.this, "",getString(R.string.mdl_adult_share_account), getString(R.string.mdl_Ok), "", positiveOnClickListener, null);
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
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveGetStarted.this);
                    }
                }

            }
        };

        MDLivePendigVisitService getApponitmentsService=new MDLivePendigVisitService(MDLiveGetStarted.this,getProgressDialog());
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
        MDLiveBaseAppcompatActivity.IS_DEPENDENT_SELECTED = false;
        if(NotificationFragment.getInstance() != null && NotificationFragment.getInstance().mUpcomingAppoinmantListView != null){
            NotificationFragment.getInstance().mUpcomingAppoinmantListView.setAdapter(null);
            NotificationFragment.getInstance().onCallNotificationLayout.setVisibility(View.GONE);
        }
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                handleSuccessResponse(response);
                if(NavigationDrawerFragment.getInstance() != null){
                    NavigationDrawerFragment.getInstance().handleServiceResponseForParent(response);
                }
                createAndSaveUser(response, "");
                getCurrentLocation();
               // upldateUserData(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this, error, getProgressDialog());
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest("", successCallBackListener, errorListener);
    }
    /**
     *
     * Load loadProviderType Details.
     * Class : ProviderTypeList - Service class used to fetch the Provider Detail information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     *
     */
    private void loadProviderType() {
        showProgress();
        final SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PreferenceConstants.DEPENDENT_USER_ID,null);
        editor.commit();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                hideProgress();
                    if (response.has("located_in")) {
                        editor.putString(PreferenceConstants.USER_STATE_LIST, response.getJSONArray("located_in").toString()).commit();
                    }
                    if (user != null && user.mMode == User.MODE_DEPENDENT) {
                        loadDependentUserInformationDetails(user.mId);
                    } else {
                        loadUserInformationDetails();
                    }
                    handleproviderTypeSuccessResponse(response);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this, error, getProgressDialog());
            }};
        SearchProviderDetailServices ptypeservices = new SearchProviderDetailServices(MDLiveGetStarted.this, getProgressDialog());
        ptypeservices.getSearchDetails(successCallBackListener, errorListener);
    }
    /**
     * Load Family Member Type Details.
     * Class : FamilyMembersList - Service class used to fetch the Family Member List information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */

    private void loadDependentUserInformationDetails(final String depenedentId) {
        showProgress();
        MDLiveBaseAppcompatActivity.IS_DEPENDENT_SELECTED = true;
        if(NotificationFragment.getInstance() != null && NotificationFragment.getInstance().mUpcomingAppoinmantListView != null){
            NotificationFragment.getInstance().mUpcomingAppoinmantListView.setAdapter(null);
            NotificationFragment.getInstance().onCallNotificationLayout.setVisibility(View.GONE);
        }
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                handleDependentSuccessResponse(response);
                if(NavigationDrawerFragment.getInstance() != null){
                    NavigationDrawerFragment.getInstance().handleServiceResponseForDependent(response);
                }
                createAndSaveUser(response, depenedentId);
                getCurrentLocation();
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this,error,getProgressDialog());
            }};
        UserBasicInfoServices services = new UserBasicInfoServices(MDLiveGetStarted.this, null);
        services.getUserBasicInfoRequest(depenedentId, successCallBackListener, errorListener);
    }

    private void createAndSaveUser(final JSONObject response, final String dependentId) {
        try {
            JSONObject personalInfo = response.getJSONObject("personal_info");

            final User user = new User();
            user.mName = personalInfo.getString("first_name") + " " + personalInfo.getString("last_name");
            user.mImageUrl = personalInfo.getString("image_url");

            if (dependentId != null && !TextUtils.isEmpty(dependentId)) {

                user.mId = dependentId;
                user.mMode = User.MODE_DEPENDENT;
            } else {
                user.mId = "";
                user.mMode = User.MODE_PRIMARY;
            }

            user.saveSelectedUser(getBaseContext());

        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private void loadDependentProviderTypeDetails(String depenedentId) {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.e("ptype Response", response.toString());
                    hideProgress();
                    if (response.has("located_in")) {
                        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PreferenceConstants.USER_STATE_LIST, response.getJSONArray("located_in").toString()).commit();
                        Log.d("Shared List", sharedpreferences.getString(PreferenceConstants.USER_STATE_LIST, "No List"));
                    }
                    handleproviderTypeSuccessResponse(response);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this,error,getProgressDialog());
            }};
        SearchProviderDetailServices ptypeservices = new SearchProviderDetailServices(MDLiveGetStarted.this, getProgressDialog());
        ptypeservices.getSearchDetails(successCallBackListener, errorListener);
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
            Log.e("userinfo Res-->",response.toString());
            assistFamilyMemmber = response.getString("assist_phone_number");
            remainingFamilyMemberCount = response.getInt("remaining_family_members_limit");
            JSONObject personalInfo = response.getJSONObject("personal_info");
            userInfoJSONString = personalInfo.toString();
            if (!personalInfo.toString().isEmpty()) {

                for(int i=0;i< Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).size();i++){
                    if(personalInfo.getString("state").equals(Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(i))){
                        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, personalInfo.getString("state"));
                        editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(i));
                        editor.commit();
                        locationTxt.setText(Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(i));
                    }
                }

                phonrNmberEditTxt = (EditText) findViewById(R.id.telephoneTxt);
                String numStr = personalInfo.getString("phone");

                try {
                    String formattedString = MdliveUtils.phoneNumberFormat(Long.parseLong(numStr));
                    phonrNmberEditTxt = (EditText) findViewById(R.id.telephoneTxt);

                    phonrNmberEditTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (mayIallowtoEdit) {
                                mayIallowtoEdit=false;
                                phonrNmberEditTxt.setText(MdliveUtils.formatDualString(s.toString()));
                                phonrNmberEditTxt.setSelection(phonrNmberEditTxt.getText().toString().length());
                                mayIallowtoEdit = true;
                                /*String temp = s.toString().replace("-", "");
                                if (temp.length() == 10) {
                                    String number = temp;
                                    String formattedString = MdliveUtils.phoneNumberFormat(Long.parseLong(number));
                                    mayIallowtoEdit = false;
                                    phonrNmberEditTxt.setText(formattedString);
                                    phonrNmberEditTxt.setSelection(phonrNmberEditTxt.getText().toString().length());
                                    mayIallowtoEdit = true;
                                }*/
                            }

                        }
                    });
//phonrNmberEditTxt.addTextChangedListener(watcher);
                    phonrNmberEditTxt.setText(formattedString);
                } catch (Exception e) {
                }

               /* phonrNmberEditTxt.addTextChangedListener(watcher);

                try {
                    String numStr = personalInfo.getString("phone");
                    if(numStr != null){
                        numStr.replace("-", "");
                        numStr.replace("(", "");
                        numStr.replace(")", "");
                        numStr.replace(" ", "");
                    }
                    phonrNmberEditTxt.setText(numStr);
                } catch (Exception e) {
                }*/
            }

            if(dependentList.size()>IntegerConstants.NUMBER_ZERO){
                dependentList.clear();
                PatientList.clear();
            }
            DateTxt = personalInfo.getString("birthdate");
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

    int lastIndex = 12;
    boolean mayIallowtoParse = true;
    boolean mayIallowtoEdit = true;

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mayIallowtoParse){
                //formatDualString(s.toString());
            }


            //



            //


        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };



    public void formatDualString(String formatText){
//        boolean hasParenthesis = false;
//        Log.e("Print format txt",formatText);
//        if(formatText.indexOf(")") > 0){
//            hasParenthesis = true;
//        }
//        formatText= formatText.replace("(", "");
//        formatText= formatText.replace(")", "");
//        formatText= formatText.replace(" ", "");
//        if(formatText.length() > 10){
//            formatText = formatText.substring(0, formatText.length()-1);
//            Log.e("Print format txt",">10");
//        }
//        if(formatText.length() >= 7){
//            formatText = "("+formatText.substring(0, 3)+") "+formatText.substring(3, 6)+" "+formatText.substring(6, formatText.length());
//            Log.e("Print format txt",formatText);
//        }else if(formatText.length() >= 4){
//            formatText = "("+formatText.substring(0, 3)+") "+formatText.substring(3, formatText.length());
//            Log.e("Print format txt",">4");
//        }else if(formatText.length() == 3 && hasParenthesis){
//            Log.e("Print format txt",">3");
//            formatText = "("+formatText.substring(0, formatText.length())+")";
//        }
//        mayIallowtoParse = false;
//        phonrNmberEditTxt.setText(formatText);
//        phonrNmberEditTxt.setSelection(phonrNmberEditTxt.getText().length());
//        mayIallowtoParse = true;




        try {
            String formattedString = MdliveUtils.phoneNumberFormat(Long.parseLong(formatText));
            phonrNmberEditTxt = (EditText) findViewById(R.id.telephoneTxt);
            phonrNmberEditTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mayIallowtoEdit) {
                        mayIallowtoEdit=false;
                        phonrNmberEditTxt.setText(MdliveUtils.formatDualString(s.toString()));
                        phonrNmberEditTxt.setSelection(phonrNmberEditTxt.getText().toString().length());
                        mayIallowtoEdit = true;
                                /*String temp = s.toString().replace("-", "");
                                if (temp.length() == 10) {
                                    String number = temp;
                                    String formattedString = MdliveUtils.phoneNumberFormat(Long.parseLong(number));
                                    mayIallowtoEdit = false;
                                    phonrNmberEditTxt.setText(formattedString);
                                    phonrNmberEditTxt.setSelection(phonrNmberEditTxt.getText().toString().length());
                                    mayIallowtoEdit = true;
                                }*/
                    }

                }
            });
//phonrNmberEditTxt.addTextChangedListener(watcher);
            phonrNmberEditTxt.setText(formattedString);
        } catch (Exception e) {
        }
    }

    /**
     *
     *  Successful Response Handler for Provider Type Info.The Provider type info will provider the gender
     *  of the user and the date of birth of the corresponding user.The dependent id will be
     *  passed for the the each provider while switching over the dependent so that the
     *  corresponding provider type will be changed to the selected dependents.
     *
     */

    private void handleproviderTypeSuccessResponse(JSONObject response) {
        try {
            providerTypeArrayList.clear();
            providerTypeIdList.clear();
            JSONArray provider_array = response.getJSONArray("provider_type");
            Log.e("providerTypeArrayList", provider_array.toString());
            for (int i = 0; i < provider_array.length(); i++)
            {
                providerTypeIdList.add(provider_array.getJSONObject(i).getInt("id") + "");
                providerTypeArrayList.add(provider_array.getJSONObject(i).getString("provider_type"));
            }
            Log.e("providerTypeArrayList", providerTypeArrayList.toString());

            Collections.reverse(providerTypeArrayList);

            Collections.reverse(providerTypeIdList);
            Log.e("providerTypeIDList", providerTypeIdList.toString());
            ((TextView) findViewById(R.id.providertypeTxt)).setText(providerTypeArrayList.get(0));
            findViewById(R.id.providertypeTxt).setContentDescription(getString(R.string.mdl_ada_dropdown) + providerTypeArrayList.get(0));
            strProviderId = providerTypeIdList.get(0);



        } catch (Exception e) {

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
                dependentList.add(strPatientName);
                PatientList.add(test);
            }
//
        }catch(Exception e){
            e.printStackTrace();
        }
        dependentList.add(StringConstants.ADD_FAMILY_MEMBER);
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
            remainingFamilyMemberCount = 0;
            Log.e("dependent user info",response.toString());
            dependentList.clear();
            PatientList.clear();
            JSONObject personalInfo = response.getJSONObject("personal_info");
            dependentList.add(personalInfo.getString("first_name") + " " + personalInfo.getString("last_name")) ;
            JSONObject notiObj=response.getJSONObject("notifications");
            DateTxt = personalInfo.getString("birthdate");
            locationTxt.setText(personalInfo.getString("city")+" ,"+personalInfo.getString("state"));
            String state = personalInfo.getString("state");
            String dep_phone = personalInfo.getString("phone");
            formatDualString(dep_phone);


            if(state.length()<3) {
                List<String> stateArr = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName));
                List<String> stateIdArr = Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode));
                for (int l = 0; l < stateIdArr.size(); l++) {
                    if (state.equals(stateIdArr.get(l))) {
                        state = stateArr.get(l);
                        break;
                    }
                }
            }

            for(int i=0;i< Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).size();i++){
                if(personalInfo.getString("state").equals(Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(i))){
                    SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, personalInfo.getString("state"));
                    editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(i));
                    editor.commit();
                    locationTxt.setText(Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(i));
                }
            }

            locationTxt.setText(state);
            JsonParser parser = new JsonParser();
            JsonObject responObj = (JsonObject)parser.parse(response.toString());
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.PATIENT_NAME, personalInfo.getString("first_name") + " " +personalInfo.getString("last_name"));
            editor.putString(PreferenceConstants.GENDER, personalInfo.getString("gender"));
            editor.commit();
            /*if(notiObj.getInt("upcoming_appointments")>=1){
                getPendingAppointments();
            }*/
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
     *
     * Choose Provider List Details.
     * Class : ChooseProviderServices - Service class used to fetch the provider list information.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     *
     */
    private void ChooseProviderResponseList() {
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                providerSuccessResponse(response.toString());
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d("Error Response", error.toString());
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    NetworkResponse errorResponse = error.networkResponse;
                    if(errorResponse.statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        if (errorObj.has("message") || errorObj.has("error")) {
                            final String errorMsg = errorObj.has("message")?errorObj.getString("message") : errorObj.getString("error");
                            (MDLiveGetStarted.this).runOnUiThread(new Runnable() {
                                public void run() {
                                    MdliveUtils.showDialog(MDLiveGetStarted.this, getApplicationInfo().loadLabel(getPackageManager()).toString(), errorMsg, getString(R.string.mdl_ok_upper), null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }, null);
                                }
                            });
                        }
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this, error, getProgressDialog());
                    }
                }catch(Exception e){
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveGetStarted.this);
                    e.printStackTrace();
                }
            }};
        ChooseProviderServices services = new ChooseProviderServices(MDLiveGetStarted.this, getProgressDialog());
        SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        showProgress();
        services.doChooseProviderRequest(settings.getString(PreferenceConstants.ZIPCODE_PREFERENCES, getString(R.string.mdl_fl)), strProviderId, successCallBackListener, errorListener);
    }

    /**
     * Instantiating array adapter to populate the listView
     * The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
     * The dialog will be showing the provider type in the inflated listview and the
     * user can select either one among the list so it can be set to the Provider Type Text.
     * @param list : Dependent users array list
     */
    private void showListViewDialog(final ArrayList<String> list, final TextView selectedText) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveGetStarted.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.mdlive_screen_popup, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.popupListview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        dialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String SelectedText = list.get(position);
                selectedText.setText(SelectedText);
                strProviderId = providerTypeIdList.get(position);
                Log.e("selected pos pID", strProviderId);
                dialog.dismiss();
            }
        });
    }


    private void upldateUserData(final JSONObject response) {
    /* Security JSON we need to read again, because of the web service issue..
            * We are excluding the security tag to be parsed by GSON,
            * then we are manually adding the Security JSON again
            * */

        Log.e("upldateUserData", response.toString());
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final UserBasicInfo userBasicInfo = gson.fromJson(response.toString().trim(), UserBasicInfo.class);
        userBasicInfo.getPersonalInfo().setSecurity(Security.fromJSON(response.toString().trim()));
        userBasicInfo.getNotifications().setPharmacyDetails(PharmacyDetails.fromJSON(response.toString().trim()));
        try {
            userBasicInfo.setHealthLastUpdate(response.getLong("health_last_update"));
        } catch (JSONException e) {
            e.printStackTrace();
            userBasicInfo.setHealthLastUpdate(-1l);
        }

        userBasicInfo.saveToSharedPreference(getBaseContext(), response.toString().trim());

        List<User> users = null;

        if (userBasicInfo.getPrimaryUser()) {
            users = UserBasicInfo.getUsersAsPrimaryUser(getBaseContext());
        } else {
            users = UserBasicInfo.getUsersAsDependentUser(getBaseContext());
        }

        if (users != null && users.size() > 0) {
            final User user = users.get(0);
            if (user != null) {
                user.saveSelectedUser(getBaseContext());
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
                if (fragment != null && fragment instanceof  NavigationDrawerFragment) {
                    ((NavigationDrawerFragment) fragment).reload();
                }
            }
        }
    }

    /**
     *
     *  Successful Response Handler for Load Basic Info.
     *   Here if the doctor on call String returns true then the Doctor On Call should
     *   be available else the doctor on call should be hidden.
     *
     */
    private void providerSuccessResponse(String response) {
        try {
            Log.e("REsponse--->", response.toString());
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
        editor.putString(PreferenceConstants.DATE_OF_BIRTH, DateTxt);
        editor.putString(PreferenceConstants.LOCATION, SavedLocation);
        editor.commit();
    }

    /**
     * This function is for calling the Closing Activity Animation
     */
    @Override
    public void onBackPressed() {
        onHomeClicked();
        HideKeyboard();
    }

    public void showHamburgerTick() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_get_started_txt));

        findViewById(R.id.toolbar_cross).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_tick).setVisibility(View.VISIBLE);

        findViewById(R.id.toolbar_hamburger).setVisibility(View.GONE);
        findViewById(R.id.toolbar_bell).setVisibility(View.GONE);
    }

    public void onCrossClicked(View v) {
        onHomeClicked();
        HideKeyboard();
    }


    @Override
    public void onUserChangedInGetStarted() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LEFT_MENU);
        if (fragment != null && fragment instanceof NavigationDrawerFragment) {
            ((NavigationDrawerFragment) fragment).onUserChangedInGetStarted();
        }
    }

    public void saveUserDataAndReloadDrawer(final JSONObject response) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final UserBasicInfo userBasicInfo = gson.fromJson(response.toString().trim(), UserBasicInfo.class);
        userBasicInfo.getPersonalInfo().setSecurity(Security.fromJSON(response.toString().trim()));
        userBasicInfo.getNotifications().setPharmacyDetails(PharmacyDetails.fromJSON(response.toString().trim()));
        try {
            userBasicInfo.setHealthLastUpdate(response.getLong("health_last_update"));
        } catch (JSONException e) {
            userBasicInfo.setHealthLastUpdate(-1l);
        }

        userBasicInfo.saveToSharedPreference(getBaseContext(), response.toString().trim());

        onUserChangedInGetStarted();

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(RIGHT_MENU);
        if (fragment != null && fragment instanceof NotificationFragment) {
            ((NotificationFragment) fragment).setNotification(userBasicInfo);
        }
    }


    //Location Fetched class


    @Override
    public void onPause() {
        super.onPause();
        try {
            hideProgress();
            unregisterReceiver(locationReceiver);
            locationService.setBroadCastData(StringConstants.DEFAULT);
            if (locationService != null && locationService.isTrackingLocation()) {
                locationService.stopListners();
            }
            HideKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCurrentLocation() {
        showProgress();
        Log.e("Location ", "Sta");
        try {
            registerReceiver(locationReceiver, intentFilter);
            isLocationFetched = true;
            if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
                locationService.setBroadCastData(getClass().getSimpleName());
                locationService.startTrackingLocation(MDLiveGetStarted.this);
                findViewById(R.id.txt_alert_img).setVisibility(View.GONE);
            } else {
                showSettingsAlert();
                findViewById(R.id.txt_alert_img).setVisibility(View.VISIBLE);
                hideProgress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                hideProgress();
                Log.e("Location ", "ended");
                if (intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                    double lat = intent.getDoubleExtra("Latitude", 0d);
                    double lon = intent.getDoubleExtra("Longitude", 0d);
                    loadCurrentLocation(lat + "", lon + "");
                }else{
                    MdliveUtils.showGPSFailureDialog(MDLiveGetStarted.this,null);
                }
            }
             catch (Exception e) {
                e.printStackTrace();
            }
            locationService.setBroadCastData(StringConstants.DEFAULT);

        }
    };

    /**
     * Load Current location.
     * Class : CurrentLocationServices - Service class used to fetch the current latitude and longitude
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void loadCurrentLocation(String latitude, String longitude) {
        showProgress();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Location Response", response.toString());
                hideProgress();
                CurrentLocationResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", error.toString());
                hideProgress();
                MdliveUtils.handelVolleyErrorResponse(MDLiveGetStarted.this, error, getProgressDialog());

            }
        };

        CurrentLocationServices currentlocationservices = new CurrentLocationServices(MDLiveGetStarted.this, getProgressDialog());
        currentlocationservices.getCurrentLocation(latitude, longitude, responseListener, errorListener);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
                findViewById(R.id.txt_alert_img).setVisibility(View.GONE);
            } else {
                findViewById(R.id.txt_alert_img).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Successful Response Handler for getting Current Location
     */


    private void CurrentLocationResponse(JSONObject response) {
        try {
            hideProgress();
            //Fetch Data From the Services
            if (response.has("state")) {
                selectedCity = response.getString("state");
                for (int l = 0; l < Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).size(); l++) {
                    if (selectedCity.equals(Arrays.asList(getResources().getStringArray(R.array.mdl_stateCode)).get(l))) {
                        locationServiceText = Arrays.asList(getResources().getStringArray(R.array.mdl_stateName)).get(l);
                        shortNameText = selectedCity;
                        break;
                    }
                }
                SharedPreferences settings = this.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PreferenceConstants.ZIPCODE_PREFERENCES, shortNameText);
                editor.putString(PreferenceConstants.LONGNAME_LOCATION_PREFERENCES, locationServiceText);
                editor.commit();
                locationTxt.setText(locationServiceText);
            }/*else{
                locationTxt.setText(serverUserLocation);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MDLiveGetStarted.this);
        // Setting Dialog Title
        alertDialog.setTitle("");
        // Setting Dialog Message
        alertDialog.setMessage("We need access to your GPS to determine your location.");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}


