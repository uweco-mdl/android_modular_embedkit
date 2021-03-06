package com.mdlive.sav;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.uilayer.MDLiveBaseActivity;
import com.mdlive.embedkit.uilayer.behaviouralhealth.BehavioralHistory;
import com.mdlive.embedkit.uilayer.behaviouralhealth.ConditionAndActive;
import com.mdlive.embedkit.uilayer.behaviouralhealth.MDLiveBehaviouralHealthActivity;
import com.mdlive.embedkit.uilayer.login.NavigationDrawerFragment;
import com.mdlive.embedkit.uilayer.login.NotificationFragment;
import com.mdlive.embedkit.uilayer.myhealth.MDLiveMedicalHistory;
import com.mdlive.embedkit.uilayer.pediatric.MDLivePediatric;
import com.mdlive.sav.adapters.ImageAdapter;
import com.mdlive.sav.adapters.ReasonForVisitAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.ReasonForVisitServices;
import com.mdlive.unifiedmiddleware.services.behavioural.BehaviouralService;
import com.mdlive.unifiedmiddleware.services.behavioural.BehaviouralUpdateService;
import com.mdlive.unifiedmiddleware.services.myhealth.DownloadMedicalService;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryCompletionServices;
import com.mdlive.unifiedmiddleware.services.myhealth.MedicalHistoryLastUpdateServices;
import com.mdlive.unifiedmiddleware.services.myhealth.UploadImageService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by srinivasan_ka on 8/13/2015.
 */
public class MDLiveReasonForVisit extends MDLiveBaseActivity {
    private BehavioralHistory mBehavioralHistory;
    private loadDownloadedImages loadImageService;
    private ImageAdapter imageAdapter;
    private GridView gridview;
    private ArrayList<HashMap<String, Object>> myPhotosList;
    public JSONArray recordsArray;
    private ListView listView;
    private ArrayList<String> ReasonList;
    private ReasonForVisitAdapter baseadapter;
    public static Uri fileUri;
    public EditText behaviour_reason;
    public ImageView takePhoto, takeGallery;
    public RelativeLayout photosContainer;
    public boolean isTherapistUser = false;
    public static String photoId;
    public boolean isNewUser = false, isBehaviourHistoryCompleted = false;
    public int validateCount = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_reason);
        this.setTitle(getString(R.string.mdl_header_reason_txt));

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
        findViewById(R.id.backImg).setContentDescription(getString(R.string.mdl_ada_back_button));
        findViewById(R.id.txtApply).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.txtApply)).setImageResource(R.drawable.reverse_arrow);
        findViewById(R.id.txtApply).setContentDescription(getString(R.string.mdl_ada_right_arrow_button));
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_header_reason_txt).toUpperCase());

        setProgressBar(findViewById(R.id.progressDialog));
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, Context.MODE_PRIVATE);
        //((TextView) findViewById(R.id.reason_patientTxt)).setText(sharedpreferences.getString(PreferenceConstants.PATIENT_NAME, ""));
        ReasonList = new ArrayList<String>();
        initializeViews();

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

     /*   ((ImageView) findViewById(R.id.backImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdliveUtils.hideSoftKeyboard(MDLiveReasonForVisit.this);
                onBackPressed();
            }
        });*/

        findViewById(R.id.MyHealthAddPhotoL2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.topContentHolder).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.topContentHolder).setVisibility(View.GONE);
                    findViewById(R.id.photoLayout).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.indicatorIcon)).setImageResource(R.drawable.down_arrow_icon_white);
                    findViewById(R.id.indicatorIcon).setContentDescription(getString(R.string.mdl_ada_arrowdown_photo_button));
                } else {
                    findViewById(R.id.topContentHolder).setVisibility(View.VISIBLE);
                    findViewById(R.id.photoLayout).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.indicatorIcon)).setImageResource(R.drawable.arrow_up);
                }
            }
        });

        // Provider mode
        String providerMode = sharedpreferences.getString(PreferenceConstants.PROVIDER_MODE, "");

        Log.v("Provider Mode", providerMode);

        if (providerMode != null && providerMode.length() > 0 && providerMode.equalsIgnoreCase(MDLiveConfig.PROVIDERTYPE_THERAPIST)) {
            isTherapistUser = true;
            findViewById(R.id.behaviourView).setVisibility(View.VISIBLE);
            findViewById(R.id.childHeader).setVisibility(View.GONE);
            getBehaviouralHealthServiceData();
        } else {
            isTherapistUser = false;
            findViewById(R.id.behaviourView).setVisibility(View.GONE);
            findViewById(R.id.childHeader).setVisibility(View.VISIBLE);
            ReasonForVisit();
        }

    }



    private void getBehaviouralHealthServiceData() {
        showProgress();
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    handleSuccessResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    MdliveUtils.handleVolleyErrorResponse(MDLiveReasonForVisit.this, error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveReasonForVisit.this);
                }
            }};
        BehaviouralService services = new BehaviouralService(MDLiveReasonForVisit.this, getProgressDialog());
        services.doGetBehavioralHealthService(responseListener, errorListener);
    }

    private void enableOrDisableNextStep(){
        if(validateTherapyFields()){
            findViewById(R.id.txtApply).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.txtApply).setVisibility(View.GONE);
        }
    }

    private boolean validateTherapyFields(){
        if(validateCount == 0){
            return false;
        }else if(validateCount == 1){
            if(behaviour_reason.getVisibility()== View.VISIBLE){
                if(behaviour_reason.getText() == null || behaviour_reason.getText().toString().length() == 0){
                    return false;
                }
            }
        }
        return true;
    }

    private void handleSuccessResponse(JSONObject response) {
        //try {
        hideProgress();
        //Log.v("Provider Mode Response ", response.toString());
        final Gson gson = new Gson();
        mBehavioralHistory = gson.fromJson(response.toString(), BehavioralHistory.class);
        behaviour_reason = ((EditText) findViewById(R.id.behaviour_reason));
        //Log.v("Hello", mBehavioralHistory.toString());

        LinearLayout behaviourHolder = (LinearLayout) findViewById(R.id.behaviourHolder);
        if(behaviourHolder.getChildCount() > 0){
            behaviourHolder.removeAllViews();
        }
        if (mBehavioralHistory.behavioralHealthReasons != null && mBehavioralHistory.behavioralHealthReasons.size() > 0) {
            try {
                for (int i = 0; i < mBehavioralHistory.behavioralHealthReasons.size(); i++) {
                    final int position = i;
                    final View child = getLayoutInflater().inflate(R.layout.mdlive_behavioural_checkbox_layout, null);
                    final CheckBox checkBox = (CheckBox)child.findViewById(R.id.behavioral_history_checkBox);
                    checkBox.setText(mBehavioralHistory.behavioralHealthReasons.get(position).condition);

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(buttonView.getText() != null && buttonView.getText().toString().toLowerCase().contains("other")){
                                if(isChecked){
                                    behaviour_reason.setVisibility(View.VISIBLE);
                                }else{
                                    behaviour_reason.setVisibility(View.GONE);
                                }
                            }
                            if (isChecked) {
                                if(validateCount != 4){
                                    validateCount++;
                                }
                                enableOrDisableNextStep();
                                mBehavioralHistory.behavioralHealthReasons.get(position).active = ConditionAndActive.YES;
                            } else {
                                if(validateCount != 0){
                                    validateCount--;
                                }
                                enableOrDisableNextStep();
                                mBehavioralHistory.behavioralHealthReasons.get(position).active = ConditionAndActive.NO;
                            }

                        }
                    });
                    if ("Yes".equalsIgnoreCase(mBehavioralHistory.behavioralHealthReasons.get(position).active)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                    behaviourHolder.addView(child);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        behaviour_reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() != 0){
                    mBehavioralHistory.behavioralHealthDescription = s.toString();
                }else{
                    mBehavioralHistory.behavioralHealthDescription = "";
                }
                enableOrDisableNextStep();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(mBehavioralHistory.behavioralHealthDescription != null && mBehavioralHistory.behavioralHealthDescription.length() != 0){
            behaviour_reason.setText(mBehavioralHistory.behavioralHealthDescription);
        }


        enableOrDisableNextStep();
        //}catch(Exception e){
        //e.printStackTrace();
        //}
    }


    public void updateBehaviourHealthService() {
        showProgress();
        final Gson gson = new Gson();
        mBehavioralHistory.familyHospitalized = "Yes";
        String request = gson.toJson(mBehavioralHistory);
        try{
            String resonText = "";
            for (int position = 0; position < mBehavioralHistory.behavioralHealthReasons.size(); position++) {
                if(mBehavioralHistory.behavioralHealthReasons.get(position).active.equals(ConditionAndActive.YES)){
                    if(mBehavioralHistory.behavioralHealthReasons.get(position).condition.equalsIgnoreCase("Other")){
                        resonText += ((behaviour_reason.getText() != null) ? behaviour_reason.getText().toString() : "")+", ";
                    }else{
                        resonText += mBehavioralHistory.behavioralHealthReasons.get(position).condition +", ";
                    }
                }
            }
            resonText = resonText.trim().substring(0, resonText.length() - 2);
            SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PreferenceConstants.REASON, resonText.trim());
            editor.commit();
        }catch (Exception e){
        }
        NetworkSuccessListener<JSONObject> responseListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                checkBehaviourHistoryCompletion();
                //checkMedicalDateHistory();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                try {
                    MdliveUtils.handleVolleyErrorResponse(MDLiveReasonForVisit.this, error, getProgressDialog());
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), MDLiveReasonForVisit.this);
                }
            }};
        BehaviouralUpdateService behaviouralUpdateServices = new BehaviouralUpdateService(MDLiveReasonForVisit.this, getProgressDialog());
        behaviouralUpdateServices.postBehaviouralUpdateService(request, responseListener, errorListener);
    }


    public void leftBtnOnClick(View v){
        MdliveUtils.hideSoftKeyboard(MDLiveReasonForVisit.this);
        onBackPressed();
    }

    public void rightBtnOnClick(View v){
        try {
            if (!MdliveUtils.isNetworkAvailable(this)) {
                MdliveUtils.connectionTimeoutError(getProgressDialog(), this);
                return;
            }
            if(isTherapistUser){
                updateBehaviourHealthService();
            }else{
                if(baseadapter.getSelectedPosition() >= 0){
                    SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.REASON_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    if(baseadapter.isNotFoundinList()){
                        editor.putString(PreferenceConstants.REASON,
                                ((EditText) findViewById(R.id.search_edit)).getText().toString().trim());
                    }else{
                        editor.putString(PreferenceConstants.REASON, listView.getAdapter().getItem(baseadapter.getSelectedPosition()).toString());
                    }
                    editor.commit();
                    startNextActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNextActivity(){
        if (TimeZoneUtils.calculteAgeFromPrefs(MDLiveReasonForVisit.this) <= IntegerConstants.PEDIATRIC_AGE_ABOVETWO) {
            Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLivePediatric.class);
            medicalIntent.putExtra("firstTimeUser", "true");
            startActivity(medicalIntent);
            MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
        }else{
            Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLiveMedicalHistory.class);
            startActivity(medicalIntent);
            MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
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
                if(response != null){
                    Log.e("Server response", response.toString());
                }
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
                                Calendar calendar = TimeZoneUtils.getCalendarWithOffset(MDLiveReasonForVisit.this);
                                calendar.setTimeInMillis(time * 1000);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                                dateFormat.setTimeZone(TimeZoneUtils.getOffsetTimezone(MDLiveReasonForVisit.this));
                                isNewUser = false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isNewUser = true;
                }
                if(!isBehaviourHistoryCompleted && !isNewUser){
                    Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLiveBehaviouralHealthActivity.class);
                    medicalIntent.putExtra("from_sav", true);
                    startActivity(medicalIntent);
                    MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
                }else if(isNewUser){
                    Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLiveBehaviouralHealthActivity.class);
                    medicalIntent.putExtra("from_sav", true);
                    medicalIntent.putExtra("isNewUser", true);
                    startActivity(medicalIntent);
                    MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
                }else{
                    startNextActivity();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryLastUpdateServices services = new MedicalHistoryLastUpdateServices(MDLiveReasonForVisit.this, null);
        services.getMedicalHistoryLastUpdateRequest(successCallBackListener, errorListener);
    }

    /**
     * Checks user medical history completion details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */
    private void checkBehaviourHistoryCompletion() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                try {
                    JSONArray historyPercentageArray = response.getJSONArray("history_percentage");
                    checkMyHealthBehaviouralHistory(historyPercentageArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkMedicalDateHistory();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                medicalCommonErrorResponseHandler(error);
            }
        };
        MedicalHistoryCompletionServices services = new MedicalHistoryCompletionServices(MDLiveReasonForVisit.this, null);
        services.getMedicalHistoryCompletionRequest(successCallBackListener, errorListener);
    }

    /**
     * This will check weather the user has completed the behavioural heaqalth history section and will hide and
     * display teh layouts accordingly.
     *
     * @param historyPercentageArray - The history percentage JSONArray
     */
    private void checkMyHealthBehaviouralHistory(JSONArray historyPercentageArray) {
        try {
            for(int i =0; i<historyPercentageArray.length();i++){
                if(historyPercentageArray.getJSONObject(i).has("behavioral")){
                    isBehaviourHistoryCompleted = historyPercentageArray.getJSONObject(i).getInt("behavioral") > 10;
                    break;
                }
            }
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
    private void checkPediatricCompletion(JSONArray historyPercentageArray) {
        try {
            hideProgress();
            int pediatricPercentage = 0;
            for(int i = 0; i < historyPercentageArray.length(); i++){
                if(historyPercentageArray.getJSONObject(i).has("pediatric")){
                    pediatricPercentage = historyPercentageArray.getJSONObject(i).getInt("pediatric");
                }
            }
            Log.v("pediatricPercentage", pediatricPercentage + "");
            if(pediatricPercentage != 0){
                Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLiveMedicalHistory.class);
                startActivity(medicalIntent);
                MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
            }else{
                Intent medicalIntent = new Intent(MDLiveReasonForVisit.this, MDLivePediatric.class);
                medicalIntent.putExtra("firstTimeUser", "true");
                startActivity(medicalIntent);
                MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            if(loadImageService != null && loadImageService.getStatus().equals(AsyncTask.Status.RUNNING)){
                findViewById(R.id.thumpProgressBar).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.thumpProgressBar).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(loadImageService != null){
                loadImageService.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void initializeViews() {
        myPhotosList = new ArrayList<>();
        imageAdapter = new ImageAdapter(MDLiveReasonForVisit.this, myPhotosList);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                /*Toast.makeText(MDLiveReasonForVisit.this, "" + position, Toast.LENGTH_SHORT).show();*/
                Snackbar.make(findViewById(android.R.id.content),
                        "" + position,
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        takePhoto = (ImageView) findViewById(R.id.takePicture);
        takeGallery = (ImageView) findViewById(R.id.takeGallery);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDeviceSupportCamera()){
                    MdliveUtils.alert(null, getApplicationContext(), getString(R.string.mdl_camera_nt_supported_msg));
                }else if(myPhotosList.size() < 8){
                    captureImage();
                }
            }
        });
        takeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPhotosList.size() < 8) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, IntegerConstants.PICK_IMAGE_REQUEST_CODE);
                    MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
                }
            }
        });
        photosContainer = (RelativeLayout) findViewById(R.id.photosContainer);
    }




    /**
     * Reason for Visit List Details.
     * Class : ReasonForVisitServices - Service class used to fetch the List information
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered(Either error message to user or Get started screen will shown to user).
     */
    private void ReasonForVisit() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleSuccessListener(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                MdliveUtils.handleVolleyErrorResponse(MDLiveReasonForVisit.this, error, getProgressDialog());
            }
        };
        ReasonForVisitServices services = new ReasonForVisitServices(MDLiveReasonForVisit.this, null);
        services.getReasonList(successCallBackListener, errorListener);
    }

    /**
     * Successful Response Handler for Provider Request.
     * The response will provide the list of symptoms.If there is no symptoms the user can
     * create the new symptom and add the symptom.
     */
    private void handleSuccessListener(JSONObject response) {
        try {
            hideProgress();
            JSONArray arr = response.getJSONArray("chief_complaint");
            for (int i = 0; i < arr.length(); i++) {
                ReasonList.add(arr.getJSONObject(i).getString(arr.getJSONObject(i).keys().next()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //findViewById(R.id.footer).setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.reasonList);
        //showOrHideFooter();

        baseadapter = new ReasonForVisitAdapter(MDLiveReasonForVisit.this, ReasonList,
                ((ImageView)findViewById(R.id.txtApply)));
        listView.setAdapter(baseadapter);
        RefineSearch();
        downloadMedicalRecordService();
    }



    /**
     * Filter Search for the Listview. we can filter the list by giving the name and if the name
     * is not in the listview then it will ask for submitting the name to the service.
     */
    public void RefineSearch() {
        final EditText search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > IntegerConstants.NUMBER_ZERO && s.subSequence(0, 1).toString().equalsIgnoreCase(" ")) {
                    search_edit.setText("");
                    search_edit.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.startsWith(" ")) {
                    baseadapter.getFilter().filter(s.toString());
                }
                baseadapter.notifyDataSetChanged();
                baseadapter.getFilter().filter(s.toString());
            }
        });
    }


    /*
    * Capturing Camera Image will launch camera app request image capture
    */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        fetchLastFileInGallery();
        startActivityForResult(intent, IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /*
    * returning image / video
    */
    private static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MDLive Images";


    /**
     *  This function is used to Check size of Picked/Captured Image from device
     *  If it exceeds more than 10 mb then it will make alert to user about size exceeding
     *
     *  @param file :: Image file captured or picked by user
     */
    public String checkSizeOfImageAndType(File file){
        boolean acceptSize = true;
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        double hasexceededSize = 10.0000000 - megabytes;
        if(hasexceededSize < 0){
            acceptSize = false;
        }
        if(!acceptSize)
            return getString(R.string.mdl_photo_size_message);
        else
            return null;
    }


    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        // this device has a camera
// no camera on this device
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * Checks user medical history aggregation details.
     * Class : MedicalHistoryCompletionServices - Service class used to fetch the medical history completion deials.
     * Listeners : SuccessCallBackListener and errorListener are two listeners passed to the service class to handle the service response calls.
     * Based on the server response the corresponding action will be triggered.
     */

    private void downloadMedicalRecordService() {
        showProgress();
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleDownloadRecordService(response);
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        DownloadMedicalService services = new DownloadMedicalService(MDLiveReasonForVisit.this, null);
        services.doDownloadImagesRequest(successCallBackListener, errorListener);
    }

    /**
     * Error Response Handler for Medical History Completion.
     */
    private void medicalCommonErrorResponseHandler(VolleyError error) {
        hideProgress();
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null) {
            String message = getString(R.string.mdl_no_internet_connection);
            if (networkResponse.statusCode == MDLiveConfig.HTTP_INTERNAL_SERVER_ERROR) {
                message = getString(R.string.mdl_internal_server_error);
            } else if (networkResponse.statusCode == MDLiveConfig.HTTP_UNPROCESSABLE_ENTITY) {
                message = getString(R.string.mdl_unprocessable_entity_error);
            } else if (networkResponse.statusCode == MDLiveConfig.HTTP_NOT_FOUND) {
                message = getString(R.string.mdl_page_not_found);
            }
            MdliveUtils.showDialog(MDLiveReasonForVisit.this, getString(R.string.mdl_error),
                    getString(R.string.mdl_server_response) + message);
        }
    }

    /**
     * This function is used to handle downloaded records data
     * Once data parsed, imageAdapter will be notified to display images in UI.
     */
    public void handleDownloadRecordService(JSONObject response) {
        hideProgress();
        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<>();
        try {
            if (response != null && response.toString().contains("No Previous Documents Found")) {
                photosContainer.setVisibility(View.GONE);
            } else if (response != null && response.has("records")) {
                listDatas.clear();
                recordsArray = response.getJSONArray("records");
                for (int i = 0; i < recordsArray.length(); i++) {
                    JSONObject jsonObject = recordsArray.getJSONObject(i);
                    if (isUserAPrimaryUser(jsonObject.getString("uploaded_by"))) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("download_link", jsonObject.getString("download_link"));
                        data.put("doc_type", jsonObject.getString("doc_type"));
                        data.put("uploaded_by", jsonObject.getString("uploaded_by"));
                        data.put("doc_name", jsonObject.getString("doc_name"));
                        data.put("id", jsonObject.getInt("id"));
                        data.put("uploaded_at", jsonObject.getString("uploaded_at"));

                        listDatas.add(data);
                        if (i == 7) {
                            i = recordsArray.length();
                        }
                    }
                }

                if (recordsArray.length() >= 8) {
                    takePhoto.setClickable(false);
                    takeGallery.setClickable(false);
                    takePhoto.setImageResource(R.drawable.camera_icon_deselect);
                    takeGallery.setImageResource(R.drawable.gallery_icon_deselect);
                } else {
                    takePhoto.setClickable(true);
                    takeGallery.setClickable(true);
                    takePhoto.setImageResource(R.drawable.camera_icon2);
                    takeGallery.setImageResource(R.drawable.gallery_icon);
                }

                if (recordsArray != null) {
                    if (recordsArray.length() > 4) {
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels;
                        gridview.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                (width / 2) + 200));
                    } else if (recordsArray.length() > 0) {
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int width = dm.widthPixels;
                        gridview.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                (width / 4) + 25));
                    }
                }

                if (recordsArray != null && recordsArray.length() > 0) {
                    photosContainer.setVisibility(View.VISIBLE);
                    if(loadImageService == null){
                        loadImageService = new loadDownloadedImages();
                        loadImageService.execute();
                    }
                } else {
                    findViewById(R.id.thumpProgressBar).setVisibility(View.GONE);
                    photosContainer.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myPhotosList = listDatas;
        imageAdapter.notifyWithDataSet(listDatas);
    }

    private Boolean isUserAPrimaryUser(String pUser) {
        if (pUser == null)
            return false;

        List<User> users = UserBasicInfo.getUsersAsPrimaryUser(getApplicationContext());
        //Log.v("MDLiveReasonForVisit", "### Registered Primary Users ==> "+ users);
        for (int i=0; i<users.size(); i++) {
            if (pUser.contains(users.get(i).mName))
                return true;
        }
        return false;
    }

    /**
     * This function is used to upload new medical record to service
     * UploadImageService - This class file is used to upload record to service.
     * @param filePath :: path of image in Device.
     */
    private void uploadMedicalRecordService(final String filePath, final boolean capturedInCamera) {
        showProgress();
        try {
            File file = new File(filePath);
            int size = (int) file.length();
            byte[] bytes = new byte[(int) file.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Base64.decode(response.getString("file_stream").getBytes("UTF-8"), Base64.DEFAULT);
            feedDatasInVolleyCache("temp", bytes);
            if(loadImageService != null){
                loadImageService.cancel(true);
                loadImageService = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response",response.toString());
                if(capturedInCamera){
                    checkOutLastFileInGallery();
                    File file = new File(filePath);
                    if(file.exists()){
                        file.delete();
                    }
                }
                hideProgress();
                try {
                    if(response!= null){
                        if(response.has("message")){
                            if(response.getString("message").equals("Document uploaded successfully")){
                                if(response.has("id")){
                                    photoId = response.getString("id");
                                    replaceWithDefaultInVolleyCache(photoId);
                                }
                                downloadMedicalRecordService();
                            }
                        }else{
                            /*Toast.makeText(getApplicationContext(), "Something Went Wrong in Uploading Document!",
                                    Toast.LENGTH_SHORT).show();*/
                            Snackbar.make(findViewById(android.R.id.content),
                                    getString(R.string.mdl_problem_uploading),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                medicalCommonErrorResponseHandler(error);
            }
        };
        UploadImageService services = new UploadImageService(MDLiveReasonForVisit.this, null);
        services.doUploadDocumentService(new File(filePath), successCallBackListener, errorListener);
    }


    String lastFileNameId = "";

    public void fetchLastFileInGallery(){
        String[] projections = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE};

        final Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if(cursor != null && cursor.moveToFirst()){
            lastFileNameId = cursor.getString(0);
        }
        cursor.close();
    }

    public void checkOutLastFileInGallery(){
        String[] projections = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE};

        final Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if(cursor != null && cursor.moveToFirst()){
            if(lastFileNameId == null){
                if(cursor.getString(0) != null){
                    ContentResolver cr = getContentResolver();
                    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(0), null);
                }
            }else if(lastFileNameId != null && lastFileNameId.equalsIgnoreCase(cursor.getString(0))){
                ContentResolver cr = getContentResolver();
                cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(0), null);
            }
            lastFileNameId = "";
            cursor.close();
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        cursor.close();
        // this is our fallback here
        return uri.getPath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                if(fileUri != null){
                    File file = new File(fileUri.getPath());
                    String hasErrorText = checkSizeOfImageAndType(file);
                    if(file.exists() && hasErrorText == null){
                        uploadMedicalRecordService(fileUri.getPath(), true);
                    }else{
                        MdliveUtils.showDialog(MDLiveReasonForVisit.this,
                                hasErrorText,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        captureImage();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        );
                    }
                }
            }
        }


        if (requestCode == IntegerConstants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    fileUri = data.getData();

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(fileUri,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(picturePath);
                    String hasErrorText = checkSizeOfImageAndType(file);

                    if(fileUri != null && hasErrorText == null){
                        uploadMedicalRecordService(getPath(fileUri), false);
                    }else{
                        MdliveUtils.showDialog(MDLiveReasonForVisit.this,
                                hasErrorText,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, IntegerConstants.PICK_IMAGE_REQUEST_CODE);
                                        MdliveUtils.startActivityAnimation(MDLiveReasonForVisit.this);
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == IntegerConstants.IMAGE_PREVIEW_CODE) {
            if (resultCode == RESULT_OK) {
                downloadMedicalRecordService();
            }
        }
    }

    /**
     * This function is used to handle downloaded records data
     * Once data parsed, imageAdapter will be notified to display images in UI.
     */
    class loadDownloadedImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if(!(progressBarLayout.getVisibility() == View.VISIBLE)){
                findViewById(R.id.thumpProgressBar).setVisibility(View.VISIBLE);
            }
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(imageAdapter != null)
                imageAdapter.notifyDataSetChanged();
            boolean hasPendingDownloads = false;
            try {
                if(recordsArray != null){
                    for(int i =0; i<recordsArray.length(); i++){
                        JSONObject jsonObject = recordsArray.getJSONObject(i);
                        if(i == 7){
                            i = recordsArray.length();
                        }else if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null) {
                            hasPendingDownloads = true;
                            i = recordsArray.length();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                hasPendingDownloads = true;
            }
            if(hasPendingDownloads && loadImageService != null && !loadImageService.isCancelled()){
                findViewById(R.id.thumpProgressBar).setVisibility(View.GONE);
                loadImageService = new loadDownloadedImages();
                loadImageService.execute();
            }else{
                findViewById(R.id.thumpProgressBar).setVisibility(View.GONE);
            }
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(recordsArray != null){
                    for(int i =0; i<recordsArray.length(); i++){
                        JSONObject jsonObject = recordsArray.getJSONObject(i);
                        try {
                            if(getDatasInVolleyCache(jsonObject.getInt("id")+"") == null){
                                String response = makeImageRequestCall(AppSpecificConfig.BASE_URL + AppSpecificConfig.DOWNLOAD_MEDICAL_IMAGE + "/"+jsonObject.getInt("id"));
                                if(response != null && response.length() != 0){
                                    handleDownloadImageService(new JSONObject(response), jsonObject.getInt("id"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * This method is used to handle downloaded byte data content from webservice.
     * @param photoId :: id of image
     * @param response :: response received from service.
     */
    public void handleDownloadImageService(JSONObject response, int photoId){
        try {
            if(response != null){
                if(response.has("message") && response.getString("message").equals("Document found")){
                    byte[] bytes = Base64.decode(response.getString("file_stream").getBytes("UTF-8"), Base64.DEFAULT);
                    if(bytes == null){
                    }else{
                        feedDatasInVolleyCache(photoId+"", bytes);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* For Testing Purpose Get Method Call*/
    public String makeImageRequestCall(String urlString) throws Exception {
        //Url link for choose provider details
        URL url = new URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(true);
        urlConnection.setConnectTimeout(30000);
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
//            urlConnection.setChunkedStreamingMode(0);

        String creds = String.format("%s:%s", AppSpecificConfig.API_KEY,AppSpecificConfig.SECRET_KEY);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        SharedPreferences sharedpreferences = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);

        urlConnection.setRequestProperty("Authorization", auth);
        if(MDLiveConfig.IS_SSO){
            urlConnection.setRequestProperty("RemoteUserId", MDLiveConfig.USR_UNIQ_ID!=null ? MDLiveConfig.USR_UNIQ_ID : AppSpecificConfig.DEFAULT_USER_ID);
        }
        else {
            urlConnection.setRequestProperty("RemoteUserId", sharedpreferences.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID));
//        urlConnection.setRequestProperty("RemoteUserId", MDLiveConfig.USR_UNIQ_ID); // for SSO2 we no longer persist sensitive data in sharedprefs
        }

        String dependentId = sharedpreferences.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
        if(dependentId != null) {
            urlConnection.setRequestProperty("DependantId", dependentId);
        }
        if (urlConnection.getResponseCode() == 200) {
            InputStream in = urlConnection.getInputStream();
            return convertInputStreamToString(in);
        } else {
            return null;
        }
    }

    /** This function is used to convert Inputstream Datas to String type*/
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

    /**
     * This function is used to feed data to volley cache
     * @param bytes :: byte data to be inserted.
     * @param photoId :: id of photo to be display.
     */
    public void feedDatasInVolleyCache(String photoId, byte[] bytes){
        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveReasonForVisit.this).getCache();
        Cache.Entry entry = new Cache.Entry();
        entry.etag = photoId+"";
        entry.data = bytes;
        cache.put(photoId + "", entry);
    }

    /**
     * This function is used to get Cache Entry of photo
     * @param photoId :: id of photo to be display.
     */
    public Cache.Entry getDatasInVolleyCache(String photoId){
        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveReasonForVisit.this).getCache();
        Cache.Entry entry = cache.get(photoId+"");
        return entry;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MdliveUtils.closingActivityAnimation(MDLiveReasonForVisit.this);
    }
    public void replaceWithDefaultInVolleyCache(String photoId) {
        Cache cache = ApplicationController.getInstance().getRequestQueue(MDLiveReasonForVisit.this).getCache();
        Cache.Entry entry = new Cache.Entry();
        entry.etag = photoId + "";
        entry.data =  cache.get("temp").data;
        cache.put(photoId + "", entry);
        cache.remove("temp");
    }
}