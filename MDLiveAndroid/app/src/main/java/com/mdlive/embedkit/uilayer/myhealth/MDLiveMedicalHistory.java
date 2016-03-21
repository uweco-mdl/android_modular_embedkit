package com.mdlive.embedkit.uilayer.myhealth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MDLiveBehaviouralHealthActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.pediatric.MDLivePediatric;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacy;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyChange;
import com.mdlive.embedkit.uilayer.pharmacy.MDLivePharmacyResult;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocationCoordinates;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.StringConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryAggregationServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryLastUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UpdateFemaleAttributeServices;
import com.mdlive.unifiedmiddleware.services.pharmacy.PharmacyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * This class is for MDLiveMedicalHistory page.
 * User have to fill up yes/no questions for
 * Allergies
 * Medications
 * Conditions
 * Pediatric Profile
 * For female Pediatric users has two more questions to fill up.
 */

public class MDLiveMedicalHistory extends MDLiveBaseActivity {

    public static ProgressDialog pDialog;
    private JSONObject medicalAggregationJsonObject;
    private boolean isPregnant, isBreastfeeding, hasFemaleAttribute = false, isTherapiestUser = false;
    private boolean  isNewUser = false;
    private RadioGroup PediatricAgeCheckGroup_1, PediatricAgeCheckGroup_2, PreExisitingGroup,
            MedicationsGroup, AllergiesGroup, ProceduresGroup, primaryCareGroup;
    private LocationCoordinates locationService;
    private LatLng currentLocation;
    private IntentFilter intentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_medical_history);
        clearMinimizedTime();
        this.setTitle(getString(R.string.mdl_medical_history));

        try {
            setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
            final Toolbar toolbar = (Toolbar) findViewById(R.id.header);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                elevateToolbar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ImageView) findViewById(R.id.backImg)).setImageResource(R.drawable.back_arrow_hdpi);
        findViewById(R.id.backImg).setContentDescription(getString(R.string.mdl_ada_back_button));
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.reverse_arrow);
        findViewById(R.id.txtApply).setContentDescription(getString(R.string.mdl_ada_right_arrow_button));
        findViewById(R.id.txtApply).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_medical_history).toUpperCase());

        findViewById(R.id.ContainerScrollView).setVisibility(View.GONE);
        //((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME,""));
        PediatricAgeCheckGroup_1 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup1));
        PediatricAgeCheckGroup_2 = ((RadioGroup) findViewById(R.id.pediatricAgeGroup2));
        PreExisitingGroup = ((RadioGroup) findViewById(R.id.conditionsGroup));
        ProceduresGroup = ((RadioGroup) findViewById(R.id.proceduresGroup));
        primaryCareGroup = ((RadioGroup) findViewById(R.id.primaryCareGroup));
        setProgressBar(findViewById(R.id.progressDialog));
        MedicationsGroup = ((RadioGroup) findViewById(R.id.medicationsGroup));
        AllergiesGroup = ((RadioGroup) findViewById(R.id.allergiesGroup));
        //error
        ProceduresGroup = ((RadioGroup) findViewById(R.id.proceduresGroup));
        locationService = new LocationCoordinates(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(getClass().getSimpleName());

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
        // First we need to check availability of play services
        if (MdliveUtils.checkPlayServices(this)) {
            // Building the GoogleApi client
            locationService.buildGoogleApiClient();
        }

        initializeYesNoButtonActions();
        checkMedicalDateHistory();
    }

    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveMedicalHistory.this);
        onBackPressed();
    }

    public void pediatricOnClick(View v){
        Intent Reasonintent = new Intent(MDLiveMedicalHistory.this, MDLivePediatric.class);
        startActivityForResult(Reasonintent, IntegerConstants.PEDIATRIC_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
    }

    /**
     * This function is used to update Medical history data in service
     * MedicalHistoryUpdateServices :: This class is used to update medical history. This class holds data ot update service
     *
     */
    private void updateMedicalHistory(){
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                if (hasFemaleAttribute) {
                    updateFemaleAttributes();
                } else {
                    getUserPharmacyDetails();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        try {
            boolean hasAllergies = false, hasConditions = false, hasMedications = false, hasProcedures = false;
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            hasAllergies = !(healthHistory.getJSONArray("allergies").length() == 0);
            hasConditions = !(healthHistory.getJSONArray("conditions").length() == 0);
            hasMedications = !(healthHistory.getJSONArray("medications").length() == 0);
            HashMap<String,String> updateMap = new HashMap<String,String>();
            updateMap.put("Do you have any health conditions?", hasConditions?"Yes":"No");
            updateMap.put("Are you currently taking any medication?", hasMedications?"Yes":"No");
            updateMap.put("Do you have any Allergies or Drug Sensitivities?", hasAllergies?"Yes":"No");
            updateMap.put("Have you ever had any surgeries or medical procedures?", hasProcedures?"Yes":"No");
            HashMap<String, HashMap<String,String>> medhistoryMap = new HashMap<String, HashMap<String,String>>();
            medhistoryMap.put("medical_history",updateMap);
            MedicalHistoryUpdateServices services = new MedicalHistoryUpdateServices(MDLiveMedicalHistory.this, null);
            services.updateMedicalHistoryRequest(medhistoryMap, successCallBackListener, errorListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This function is used to initialize clickListners of Buttons used in MedicalHistory page
     */

    public void MyHealthConditionsLlOnClick(View view){
        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
    }

    public void BehaviourLlOnClick(View view){
        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveBehaviouralHealthActivity.class);
        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
    }


    public void MedicationsLlOnClick(View view){
        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
    }

    public void AllergiesLlOnClick(View view){
        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
    }

    public void ProcedureL1OnClick(View view){
        Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddProcedures.class);
        startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
    }

    public void backImgOnClick(View view){
        onBackPressed();
    }

    public void rightBtnOnClick(View view){
        updateMedicalHistory();
    }

    public void SavContinueBtnOnClick(View view){
        updateMedicalHistory();
    }



    /**
     * This function is used to initialize Yes/No Button actions used in layout.
     */
    private void initializeYesNoButtonActions() {
        PediatricAgeCheckGroup_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isPregnant = checkedId == R.id.perdiatric1YesButton;
                ValidateModuleFields();
            }
        });
        PediatricAgeCheckGroup_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isBreastfeeding = checkedId == R.id.perdiatric2YesButton;
                ValidateModuleFields();
            }
        });
        PreExisitingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.conditionYesButton) {
                    PreExisitingGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddConditions.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
                }else{
                    ValidateModuleFields();
                }
            }
        });
        MedicationsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.medicationsYesButton) {
                    MedicationsGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddMedications.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
                }else{
                    ValidateModuleFields();
                }
            }
        });
        AllergiesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.allergiesYesButton) {
                    AllergiesGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddAllergies.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
                }else{
                    ValidateModuleFields();
                }
            }
        });

        /*if (TimeZoneUtils.calculteAgeFromPrefs(MDLiveMedicalHistory.this) <= IntegerConstants.PEDIATRIC_AGE_ABOVETWO) {
            ((RelativeLayout)findViewById(R.id.PediatricLayoutLl)).setVisibility(View.VISIBLE);
        }else{
            ((RelativeLayout)findViewById(R.id.PediatricLayoutLl)).setVisibility(View.GONE);
        }

        if (TimeZoneUtils.calculteAgeFromPrefs(MDLiveMedicalHistory.this) <= IntegerConstants.PEDIATRIC_AGE_ABOVETWO) {
            ((RelativeLayout)findViewById(R.id.PediatricLayoutLl)).setVisibility(View.VISIBLE);
        }else{
            ((RelativeLayout)findViewById(R.id.PediatricLayoutLl)).setVisibility(View.GONE);
        }*/

        // Provider mode
        final SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        String providerMode = sharedpreferences.getString(PreferenceConstants.PROVIDER_MODE, "");

        if(providerMode != null && providerMode.length() > 0 && providerMode.equalsIgnoreCase(MDLiveConfig.PROVIDERTYPE_THERAPIST)){
            isTherapiestUser = true;
            findViewById(R.id.BehaviouralHealthLl).setVisibility(View.VISIBLE);
        }else{
            isTherapiestUser = false;
            findViewById(R.id.BehaviouralHealthLl).setVisibility(View.GONE);
        }

        ProceduresGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //error
                if (checkedId == R.id.procedureYesButton) {
                    ProceduresGroup.clearCheck();
                    Intent i = new Intent(MDLiveMedicalHistory.this, MDLiveAddProcedures.class);
                    startActivityForResult(i, IntegerConstants.RELOAD_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
                }else{
                    ValidateModuleFields();
                }
            }
        });

        final SharedPreferences.Editor editor = sharedpreferences.edit();
        primaryCareGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.primaryCareYesButton) {
                    editor.putString(PreferenceConstants.PRIMARY_PHYSICIAN_STATUS, "Yes");
                }else{
                    editor.putString(PreferenceConstants.PRIMARY_PHYSICIAN_STATUS, "No");
                }
                editor.commit();
            }
        });

    }




    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == IntegerConstants.RELOAD_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                if(data.hasExtra("medicationData")){
                    findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                    findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.MedicationsNameTv)).setText(data.getStringExtra("medicationData"));
                }else if(data.hasExtra("conditionsData")){
                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.MyHealthConditionsNameTv)).setText(data.getStringExtra("conditionsData"));
                }else if(data.hasExtra("allegiesData")){
                    findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                    findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.AlergiesNameTv)).setText(data.getStringExtra("allegiesData"));
                }
                else if(data.hasExtra("proceduresData")){
                    findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
                    findViewById(R.id.ProcedureLl).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.ProcedureNameTv)).setText(data.getStringExtra("proceduresData"));
                }
            }
        }else if(requestCode == IntegerConstants.PEDIATRIC_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                ((TextView) findViewById(R.id.PediatricNameTv)).setText(getString(R.string.mdl_pediatric_completed_txt));
            }
        }
    }

    /**
     * This override function will be called on every time with this page loading.
     * <p/>
     * if any progressBar loading on screen anonymously on this will stop it.
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            MdliveUtils.checkPlayServices(this);
            registerReceiver(locationReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(locationReceiver);
            //locationService.setBroadCastData(StringConstants.DEFAULT);
            if(locationService != null && locationService.isTrackingLocation()){
                locationService.stopListners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkMedicalDateHistory() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                try {
                    if(response.get("health_last_update") instanceof Number){
                        long num=response.getLong("health_last_update");
                        int length = (int) Math.log10(num) + 1;
                        System.out.println(length);
                    }else if(response.get("health_last_update") instanceof CharSequence){
                        if(response.getString("health_last_update").equals("")){
                        }
                    }
                    if(response.getString("health_last_update").length() == 0){
                        isNewUser = true;
                    }else{
                        if(response.has("health_last_update")){
                            long time = response.getLong("health_last_update");
                            if(time != 0){
                                Calendar calendar = TimeZoneUtils.getCalendarWithOffset(MDLiveMedicalHistory.this);
                                calendar.setTimeInMillis(time * 1000);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                                dateFormat.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLiveMedicalHistory.this));
                                findViewById(R.id.UpdateInfoWindow).setVisibility(View.VISIBLE);
                                ((TextView)findViewById(R.id.updateInfoText)).setText(
                                        getResources().getString(R.string.mdl_last_update_txt)+" "+
                                                dateFormat.format(calendar.getTime())
                                );
                                isNewUser = false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isNewUser = true;
                }
                checkMedicalAggregation();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryLastUpdateServices services = new MedicalHistoryLastUpdateServices(MDLiveMedicalHistory.this, null);
        services.getMedicalHistoryLastUpdateRequest(successCallBackListener, errorListener);
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkMedicalAggregation() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                medicalAggregationHandleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error", error.getMessage());
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryAggregationServices services = new MedicalHistoryAggregationServices(MDLiveMedicalHistory.this, null);
        services.getMedicalHistoryAggregationRequest(successCallBackListener, errorListener);
    }

    /**
     * Handling the response of medical Aggregation webservice response.
     */

    private void medicalAggregationHandleSuccessResponse(JSONObject response) {
        try {
            medicalAggregationJsonObject = response;
            checkMedicalCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applying validation on form and enable/disable continue button for further steps over.
     */
    public void ValidateModuleFields() {
        boolean isAllFieldsfilled = true;
        if (hasFemaleAttribute) {
            if (PediatricAgeCheckGroup_1.getCheckedRadioButtonId() < 0
                    || PediatricAgeCheckGroup_2.getCheckedRadioButtonId() < 0) {
                isAllFieldsfilled = false;
            }
        }
        if (findViewById(R.id.MyHealthConditionChoiceLl).getVisibility() == View.VISIBLE &&
                PreExisitingGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (findViewById(R.id.MyHealthMedicationsLl).getVisibility() == View.VISIBLE &&
                MedicationsGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }
        if (findViewById(R.id.MyHealthAllergiesLl).getVisibility() == View.VISIBLE &&
                AllergiesGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }

        if (findViewById(R.id.MyHealthProceduresLl).getVisibility() == View.VISIBLE &&
                ProceduresGroup.getCheckedRadioButtonId() < 0) {
            isAllFieldsfilled = false;
        }

        if (isAllFieldsfilled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                findViewById(R.id.txtApply).setVisibility(View.GONE);
            } else {
                findViewById(R.id.txtApply).setVisibility(View.GONE);
            }
        }
    }

    /**
     * Checks user medical history completion details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkMedicalCompletion() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                medicalCompletionHandleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryCompletionServices services = new MedicalHistoryCompletionServices(MDLiveMedicalHistory.this, null);
        services.getMedicalHistoryCompletionRequest(successCallBackListener, errorListener);
    }

    /**
     * Error Response Handler for Medical History Completion.
     */
    private void medicalCommonErrorResponseHandler(VolleyError error) {
        hideProgress();
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null) {
            String message = "No Internet Connection";
            if (networkResponse.statusCode == MDLiveConfig.HTTP_INTERNAL_SERVER_ERROR) {
                message = "Internal Server Error";
            } else if (networkResponse.statusCode == MDLiveConfig.HTTP_UNPROCESSABLE_ENTITY) {
                message = "Unprocessable Entity Error";
            } else if (networkResponse.statusCode == MDLiveConfig.HTTP_NOT_FOUND) {
                message = "Page Not Found";
            }
            MdliveUtils.showDialog(MDLiveMedicalHistory.this, "Error",
                    "Server Response : " + message);
        }
    }
    /**
     * Successful Response Handler for Medical History Completion.
     */
    private void medicalCompletionHandleSuccessResponse(JSONObject response) {
        try {
            JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
            //checkIsFirstTimeUser(historyPercentageArray);
            checkMyHealthHistory(historyPercentageArray);
            checkProcedure(historyPercentageArray);
            checkMyMedications(historyPercentageArray);
            checkAllergies(historyPercentageArray);
            //checkPediatricCompletion(historyPercentageArray);
            checkPrimaryCarePhysicianHistory();
            checkMyHealthBehaviouralHistory(historyPercentageArray);
            //checkMyHealthLifestyleAndFamilyHistory(historyPercentageArray);
            ValidateModuleFields();
            checkAgeAndFemale();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPrimaryCarePhysicianHistory(){
        final SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getBaseContext());
        if(userBasicInfo != null){
            String primaryPhyTxt = userBasicInfo.getPersonalInfo().getDoYouHavePrimaryCarePhysician();
            if(primaryPhyTxt != null && primaryPhyTxt.equalsIgnoreCase("yes")){
                ((RadioButton)findViewById(R.id.primaryCareYesButton)).setChecked(true);
                editor.putString(PreferenceConstants.PRIMARY_PHYSICIAN_STATUS, "Yes");
            }else if(primaryPhyTxt != null && primaryPhyTxt.equalsIgnoreCase("no")){
                ((RadioButton)findViewById(R.id.primaryCareNoButton)).setChecked(true);
                editor.putString(PreferenceConstants.PRIMARY_PHYSICIAN_STATUS, "No");
            }else{
                editor.putString(PreferenceConstants.PRIMARY_PHYSICIAN_STATUS, "");
            }
        }
        editor.commit();
    }

    /**
     * This will check weather the user has completed the allergy section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkPediatricCompletion(JSONArray historyPercentageArray) {
        try {
            int pediatricPercentage = 0;
            for(int i = 0; i < historyPercentageArray.length(); i++){
                if(historyPercentageArray.getJSONObject(i).has("pediatric")){
                    pediatricPercentage = historyPercentageArray.getJSONObject(i).getInt("pediatric");
                }
            }
            Log.v("pediatricPercentage", pediatricPercentage+"");
            if(pediatricPercentage != 0){
                ((TextView) findViewById(R.id.PediatricNameTv)).setText(getString(R.string.mdl_pediatric_completed_txt));
            }else{
                ((TextView) findViewById(R.id.PediatricNameTv)).setText(getString(R.string.mdl_pediatric_notcompleted_txt));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isNewUser){
            findViewById(R.id.PediatricLayoutLl).setVisibility(View.GONE);
        }
    }

    /**
     * Check the age of user and sex whether male or female to enable
     * Pediatric questions.
     */
    public void checkAgeAndFemale() {
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
            String gender = sharedpreferences.getString(PreferenceConstants.GENDER, "");
            if(gender.equalsIgnoreCase("Female")){
                JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
                if(healthHistory.has("female_questions") && !isTherapiestUser){
                    findViewById(R.id.PediatricAgeCheck1).setVisibility(View.VISIBLE);
                    findViewById(R.id.PediatricAgeCheck2).setVisibility(View.VISIBLE);
                    hasFemaleAttribute = true;
                }
            }else{
                hasFemaleAttribute = false;
            }
            ValidateModuleFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will check weather the user has completed the allergy section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkAllergies(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("allergies").length() == 0)) {
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("allergies");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    if (conditonsArray.getJSONObject(i).getString("name").trim() != null &&
                            !conditonsArray.getJSONObject(i).getString("name").trim().equals("")) {
                        conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                        if (i != conditonsArray.length() - 1) {
                            conditonsNames += ", ";
                        }
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.mdl_no_allergies_reported));
                else
                    ((TextView) findViewById(R.id.AlergiesNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.AlergiesNameTv)).setText(getString(R.string.mdl_no_allergies_reported));
            }
            findViewById(R.id.ContainerScrollView).setVisibility(View.VISIBLE);
            hideProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isTherapiestUser) {
            findViewById(R.id.AllergiesLl).setVisibility(View.GONE);
            findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
        }else{
            if (isNewUser) {
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.VISIBLE);
                findViewById(R.id.AllergiesLl).setVisibility(View.GONE);
            } else {
                findViewById(R.id.MyHealthAllergiesLl).setVisibility(View.GONE);
                findViewById(R.id.AllergiesLl).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * This will check weather the user has completed the medications section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyMedications(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("medications").length() == 0)) {
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("medications");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    if (conditonsArray.getJSONObject(i).getString("name").trim() != null &&
                            !conditonsArray.getJSONObject(i).getString("name").trim().equals("")) {
                        conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                        if (i != conditonsArray.length() - 1) {
                            conditonsNames += ", ";
                        }
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.mdl_no_medications_reported));
                else
                    ((TextView) findViewById(R.id.MedicationsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.MedicationsNameTv)).setText(getString(R.string.mdl_no_medications_reported));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isTherapiestUser) {
            findViewById(R.id.MedicationsLl).setVisibility(View.GONE);
            findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
        }else {
            if (isNewUser) {
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.VISIBLE);
                findViewById(R.id.MedicationsLl).setVisibility(View.GONE);
            } else {
                findViewById(R.id.MyHealthMedicationsLl).setVisibility(View.GONE);
                findViewById(R.id.MedicationsLl).setVisibility(View.VISIBLE);
            }
        }

        if(MedicationsGroup.getCheckedRadioButtonId() > 0 &&
                MedicationsGroup.getCheckedRadioButtonId() == R.id.medicationsYesButton){
            ((RadioButton) findViewById(R.id.medicationsYesButton)).setChecked(false);
        }

    }

    /**
     * This will check weather the user has completed the my health condition section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthHistory(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("conditions").length() == 0)) {
                findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("conditions");
                for (int i = 0; i < conditonsArray.length(); i++) {
                    if (conditonsArray.getJSONObject(i).getString("condition").trim() != null &&
                            !conditonsArray.getJSONObject(i).getString("condition").trim().equals("")) {
                        conditonsNames += conditonsArray.getJSONObject(i).getString("condition");
                        if (i != conditonsArray.length() - 1) {
                            conditonsNames += ", ";
                        }
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.mdl_no_condition_reported));
                else
                    ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.MyHealthConditionsNameTv)).setText(getString(R.string.mdl_no_condition_reported));
            }

            if(isTherapiestUser) {
                findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                findViewById(R.id.MyHealthConditionsLl).setVisibility(View.GONE);
            }else{
                if(isNewUser){
                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.VISIBLE);
                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.MyHealthConditionChoiceLl).setVisibility(View.GONE);
                    findViewById(R.id.MyHealthConditionsLl).setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton){
            ((RadioButton) findViewById(R.id.conditionYesButton)).setChecked(false);
        }
    }

    /**
     * This will check weather the user has completed the behavioural heaqalth history section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthBehaviouralHistory(JSONArray historyPercentageArray) {
        Log.d("BEHAVIOURAL --->", historyPercentageArray.toString());
        try {
            for(int i =0; i<historyPercentageArray.length();i++){
                if(historyPercentageArray.getJSONObject(i).has("behavioral")){
                    findViewById(R.id.BehaviouralHealthLl).setVisibility(View.VISIBLE);
                    if(historyPercentageArray.getJSONObject(i).getInt("behavioral")!=0){
                        ((TextView)findViewById(R.id.BehavourNameTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isTherapiestUser){
            findViewById(R.id.BehaviouralHealthLl).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.BehaviouralHealthLl).setVisibility(View.GONE);
        }

     /*   if(PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton){
            ((RadioButton) findViewById(R.id.conditionYesButton)).setChecked(false);
        }
    */
        if(isNewUser){
            findViewById(R.id.BehaviouralHealthLl).setVisibility(View.GONE);
        }
    }

    /**
     * This will check weather the user has completed the behavioural health history section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthLifestyleAndFamilyHistory(JSONArray historyPercentageArray) {
        try {
            for(int i =0; i<historyPercentageArray.length();i++){
                if(historyPercentageArray.getJSONObject(i).has("life_style")){
                    if(historyPercentageArray.getJSONObject(i).getInt("life_style")!=0){
                        ((TextView)findViewById(R.id.LifestyleTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }
                } else if(historyPercentageArray.getJSONObject(i).has("family_history")){
                    findViewById(R.id.BehaviouralHealthCardView).setVisibility(View.VISIBLE);
                    if(historyPercentageArray.getJSONObject(i).getInt("family_history")!=0){
                        ((TextView)findViewById(R.id.BehaviouralHealthTv)).setText(getResources().getString(R.string.mdl_pediatric_completed_txt));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(PreExisitingGroup.getCheckedRadioButtonId() > 0 &&
                PreExisitingGroup.getCheckedRadioButtonId() == R.id.conditionYesButton){
            ((RadioButton) findViewById(R.id.conditionYesButton)).setChecked(false);
        }
    }

    /**
     * This will check weather the user has completed the Pediatric Profile section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkProcedure(JSONArray historyPercentageArray) {
        try {
            JSONObject healthHistory = medicalAggregationJsonObject.getJSONObject("health_history");
            int myHealthPercentage = historyPercentageArray.getJSONObject(0).getInt("health");
            if (myHealthPercentage != 0 && !(healthHistory.getJSONArray("surgeries").length() == 0)) {
                findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
                findViewById(R.id.ProcedureLl).setVisibility(View.VISIBLE);
                String conditonsNames = "";
                JSONArray conditonsArray = healthHistory.getJSONArray("surgeries");
                for(int i = 0;i<conditonsArray.length();i++){
                    conditonsNames += conditonsArray.getJSONObject(i).getString("name");
                    if(i!=conditonsArray.length() - 1){
                        conditonsNames += ", ";
                    }
                }
                if (conditonsNames.trim().length() == 0)
                    ((TextView) findViewById(R.id.ProcedureNameTv)).setText(getString(R.string.mdl_no_procedures_reported));
                else
                    ((TextView) findViewById(R.id.ProcedureNameTv)).setText(conditonsNames);
            } else {
                ((TextView) findViewById(R.id.ProcedureNameTv)).setText(getString(R.string.mdl_no_procedures_reported));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isTherapiestUser) {
            findViewById(R.id.ProcedureLl).setVisibility(View.GONE);
            findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
        }else{
            if(isNewUser){
                findViewById(R.id.MyHealthProceduresLl).setVisibility(View.VISIBLE);
                findViewById(R.id.ProcedureLl).setVisibility(View.GONE);
            }else{
                findViewById(R.id.MyHealthProceduresLl).setVisibility(View.GONE);
                findViewById(R.id.ProcedureLl).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Sending the answer details of Female pediatric users
     */
    private void updateFemaleAttributes() {
        HashMap<String, String> femaleAttributes = new HashMap<String, String>();
        femaleAttributes.put("is_pregnant", isPregnant + "");
        femaleAttributes.put("is_breast_feeding", isBreastfeeding + "");
        HashMap<String, HashMap<String, String>> postBody = new HashMap<String, HashMap<String, String>>();
        postBody.put("female_questions", femaleAttributes);
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                getUserPharmacyDetails();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                medicalCommonErrorResponseHandler(error);
            }
        };
        UpdateFemaleAttributeServices services = new UpdateFemaleAttributeServices(MDLiveMedicalHistory.this, null);
        services.updateFemaleAttributeRequest(new Gson().toJson(postBody), successCallBackListener, errorListener);
    }

    /*
   * This function will get latest default pharmacy details of users from webservice.
   * PharmacyService class handles webservice integration.
   * @responseListener - Receives webservice informatoin
   * @errorListener - Received error information (if any problem in webservice)
   * once message received by  @responseListener then it will redirect to handleSuccessResponse function
   * to parse message content.
   */
    public void getUserPharmacyDetails() {
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleSuccessResponse(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
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
                        // Show timeout error message
                        MdliveUtils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
                    }
                }
            }
        };
        callPharmacyService(responseListener, errorListener);
    }



    /**
     *  This method is used to call pharmacy service
     *  In pharmacy service, it requires GPS location details to get distance details.
     *
     *  @param errorListener - Pharmacy error response listener
     *  @param responseListener - Pharmacy detail Success response listener
     */
    public void callPharmacyService(final NetworkSuccessListener<JSONObject> responseListener,
                                    final NetworkErrorListener errorListener){
        if(locationService.checkLocationServiceSettingsEnabled(this)){
            showProgress();
            registerReceiver(locationReceiver, intentFilter);
            locationService.setBroadCastData(getClass().getSimpleName());
            locationService.startTrackingLocation(this);
        }else{
            PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
            services.doMyPharmacyRequest("","",responseListener, errorListener);
        }
    }

    public BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(locationReceiver);
            NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handleSuccessResponse(response);
                }
            };
            NetworkErrorListener errorListener = new NetworkErrorListener() {
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
                            // Show timeout error message
                            MdliveUtils.connectionTimeoutError(pDialog, MDLiveMedicalHistory.this);
                        }
                    }
                }
            };
            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
                double lat=intent.getDoubleExtra("Latitude",0d);
                double lon=intent.getDoubleExtra("Longitude",0d);
                currentLocation = new LatLng(lat, lon);
                if(lat!=0 && lon!=0){
                    PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
                    services.doMyPharmacyRequest(lat+"", +lon+"",
                            responseListener, errorListener);
                }else{
                    PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
                    services.doMyPharmacyRequest("","",responseListener, errorListener);
                }
            }else{
                PharmacyService services = new PharmacyService(MDLiveMedicalHistory.this, null);
                services.doMyPharmacyRequest("","",responseListener, errorListener);
            }
        }
    };



    /**
     *  This function handles webservice response and parsing the contents.
     *  Once parsing operation done, then it will update UI
     *  bundletoSend is stand for to send bundle of datas received from webservice to next page.
     */
    Bundle bundletoSend = new Bundle();
    String jsonResponse;

    private void handleSuccessResponse(JSONObject response) {
        try {
            hideProgress();
            jsonResponse = response.toString();
            if(response.has("message")){
                if(response.getString("message").equals("No pharmacy selected")){
                    getLocationBtnOnClickAction();
                }
            }else{
                JSONObject pharmacyDatas = response.getJSONObject("pharmacy");
                bundletoSend.putInt("pharmacy_id", pharmacyDatas.getInt("pharmacy_id"));
                JSONObject coordinates = pharmacyDatas.getJSONObject("coordinates");
                bundletoSend.putDouble("longitude", coordinates.getDouble("longitude"));
                bundletoSend.putDouble("latitude", coordinates.getDouble("latitude"));
                bundletoSend.putBoolean("twenty_four_hours", pharmacyDatas.getBoolean("twenty_four_hours"));
                bundletoSend.putBoolean("active", pharmacyDatas.getBoolean("active"));
                bundletoSend.putString("store_name", pharmacyDatas.getString("store_name"));
                bundletoSend.putString("phone", pharmacyDatas.getString("phone"));
                bundletoSend.putString("address1", pharmacyDatas.getString("address1"));
                bundletoSend.putString("address2", pharmacyDatas.getString("address2"));
                bundletoSend.putString("zipcode", pharmacyDatas.getString("zipcode"));
                bundletoSend.putString("fax", pharmacyDatas.getString("fax"));
                bundletoSend.putString("city", pharmacyDatas.getString("city"));
                bundletoSend.putString("distance", pharmacyDatas.getString("distance"));
                bundletoSend.putString("state", pharmacyDatas.getString("state"));
                String res = response.toString();
                Intent i = new Intent(getApplicationContext(), MDLivePharmacy.class);
                i.putExtra("Response",res);
                startActivity(i);
                MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OnClickAction for Location Button Action. This will get the current location. If the current
     * location is received, starts teh MDLivePharmacyResult activity.
     *
     */
    private void getLocationBtnOnClickAction() {
        if(currentLocation != null && currentLocation.latitude != 0 && currentLocation.longitude != 0){
            Intent i = new Intent(getApplicationContext(), MDLivePharmacyResult.class);
            i.putExtra("longitude", currentLocation.latitude+"");
            i.putExtra("latitude", currentLocation.longitude+"");
            i.putExtra("errorMesssage", "No Pharmacies listed in your location");
            startActivity(i);
            MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
        }else{
            Intent i = new Intent(getApplicationContext(), MDLivePharmacyChange.class);
            i.putExtra("Response",jsonResponse);
            startActivity(i);
            MdliveUtils.startActivityAnimation(MDLiveMedicalHistory.this);
        }
    }


    /**
     * This method will close the activity with transition effect.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveMedicalHistory.this);
    }

}
